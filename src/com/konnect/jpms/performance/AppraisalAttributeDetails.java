package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AppraisalAttributeDetails extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	String strEmpOrgId = null;
	
	private CommonFunctions CF; 
	
	private String type;  
	private List<FillOrganisation> orgList;
	private String strOrg;

	private String userscreen;
	private String navigationId;
	private String toPage;
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		request.setAttribute(TITLE, "Appraisal Attribute Details");
//		System.out.println("type======>"+getType());
		UtilityFunctions uF = new UtilityFunctions();
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
//			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
//				setStrOrg(orgList.get(0).getOrgId());
//			}
		}else{
//			if(uF.parseToInt(getStrOrg()) == 0){
//				setStrOrg((String) session.getAttribute(ORGID));
//			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
			
//		System.out.println("getType() ===>> " + getType());
		
		if(getType()==null || getType().equals("")){
			request.setAttribute(PAGE, "/jsp/performance/AppraisalAttributeDetails.jsp");
			getElementDetails();
			getLevelDetails();
			getData();
		}else{
			request.setAttribute(PAGE, "/jsp/performance/AppraisalAttributeLevelWise.jsp");
			getElementDetails();
//			getLevelDetails();
			getLevelWiseData();
			getData();
		
		}
		
		return SUCCESS;
	}
	
	
	private void getLevelWiseData() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		List<List<String>> outerList = null;
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM appraisal_attribute order by arribute_id");
			rs = pst.executeQuery();
			Map<String, String> hmAttribute = new HashMap<String, String>();
			Map<String, List<String>> hmAttributeDetails = new HashMap<String, List<String>>();
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_id"));
				innerList.add(rs.getString("attribute_name"));
				innerList.add(rs.getString("status"));
				innerList.add(rs.getString("attribute_desc"));
				innerList.add(rs.getString("attribute_info"));
				
				hmAttributeDetails.put(rs.getString("arribute_id"), innerList);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAttributeDetails",hmAttributeDetails);
			
			pst = con.prepareStatement("select * from appraisal_attribute_level");
			Map<String,List<List<String>>> hmAttributeLevel=new HashMap<String,List<List<String>>>();
			rs=pst.executeQuery();
			while(rs.next()){
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_level_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("attribute_id")), ""));
				innerList.add(rs.getString("threshhold"));
				innerList.add(rs.getString("attribute_id"));
				
				outerList=hmAttributeLevel.get(rs.getString("level_id")+rs.getString("element_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
			
				outerList.add(innerList);
				hmAttributeLevel.put(rs.getString("level_id")+rs.getString("element_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAttributeLevel",hmAttributeLevel);
//			System.out.println("hmAttributeLevel=====>"+hmAttributeLevel);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getLevelDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		List<List<String>> outerList = null;
		
		try {
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("SELECT * FROM level_details order by level_id");
			rs = pst.executeQuery();
			Map<String, String> hmlevel = new HashMap<String, String>();
			while(rs.next()){
				hmlevel.put(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select * from appraisal_attribute_level where level_id in " +
//					"(select level_id from level_details where org_id=?)");
//			pst.setInt(1, uF.parseToInt(orgid));
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_attribute_level ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" where level_id in (select level_id from level_details where org_id="+uF.parseToInt(getStrOrg())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" where level_id in (select level_id from level_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ==>> " + pst);
			Map<String,List<List<String>>> hmAttributeLevel=new HashMap<String,List<List<String>>>();
			rs=pst.executeQuery();
			while(rs.next()){
				 
				outerList=hmAttributeLevel.get(rs.getString("attribute_id")+rs.getString("element_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_level_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(uF.showData(hmlevel.get(rs.getString("level_id")), ""));
				innerList.add(rs.getString("threshhold"));
				innerList.add(rs.getString("attribute_id"));
				
				 
				outerList.add(innerList);
				hmAttributeLevel.put(rs.getString("attribute_id")+rs.getString("element_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAttributeLevel",hmAttributeLevel);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getElementDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_name");
			rs = pst.executeQuery();
			List<List<String>> elementOuterList = new ArrayList<List<String>>();
			while(rs.next()){
				List<String> elementList = new ArrayList<String>();
				elementList.add(rs.getString("appraisal_element_id"));
				elementList.add(rs.getString("appraisal_element_name"));
				
				elementOuterList.add(elementList);
			}
			rs.close();
			pst.close();
			request.setAttribute("elementOuterList",elementOuterList);
//			System.out.println("elementOuterList======>"+elementOuterList);
			getSelectedFilter(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
			alFilter.add("ORGANISATION");
			if(getStrOrg()!=null) {
				String strOrg="";
				for(int i=0;orgList!=null && i<orgList.size();i++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
				}
				if(strOrg!=null && !strOrg.equals("")) {
					hmFilter.put("ORGANISATION", strOrg);
				} else {
					hmFilter.put("ORGANISATION", "All Organisation");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public void getData(){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		List<List<String>> outerList = null;
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from appraisal_element");
			rs = pst.executeQuery();
			Map<String, String> hmPerformanceElement = new HashMap<String, String>();
			while(rs.next()){
				hmPerformanceElement.put(rs.getString("appraisal_element_id"), rs.getString("appraisal_element_name"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
//			pst = con.prepareStatement("select * from appraisal_attribute aa left join appraisal_element_attribute aea on aa.arribute_id = aea.appraisal_attribute where aa.status=true");
			pst = con.prepareStatement("select * from (select aa.arribute_id,aea.appraisal_element from appraisal_attribute aa " +
					" left join appraisal_element_attribute aea on aa.arribute_id = aea.appraisal_attribute where aa.status=true" +
					" group by aa.arribute_id,aea.appraisal_element) as a,appraisal_attribute aa where aa.arribute_id= a.arribute_id " +
					" order by aa.attribute_name");
			
			Map<String,List<List<String>>> mp=new LinkedHashMap<String,List<List<String>>>();
			rs=pst.executeQuery();
			while(rs.next()){
				 
				outerList=mp.get(rs.getString("appraisal_element"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_id"));
				innerList.add(rs.getString("attribute_name"));
				innerList.add(uF.showData(hmPerformanceElement.get(rs.getString("appraisal_element")), ""));
				innerList.add(rs.getString("threshhold"));
				innerList.add(rs.getString("attribute_info"));
				innerList.add(rs.getString("attribute_desc"));
				
				 
				outerList.add(innerList);
				mp.put(rs.getString("appraisal_element"), outerList);
			}
			rs.close();
			pst.close();
			
			String orgid="";
			if((getStrOrg()==null || getStrOrg().equals("")) && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
				orgid=orgList.get(0).getOrgId();
			}else{
				orgid=getStrOrg();
			}
			
			pst = con.prepareStatement("select * from level_details where org_id=? order by level_code");
			pst.setInt(1, uF.parseToInt(orgid));
			rs=pst.executeQuery();
			List<List<String>> levelList=new ArrayList<List<String>>();
			while(rs.next()){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("level_name")+" ["+rs.getString("level_code")+"]");
				levelList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("levelList",levelList);

			request.setAttribute("mp",mp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public String getStrOrg() {
		return strOrg;
	}


	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	

	public String getUserscreen() {
		return userscreen;
	}


	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}


	public String getNavigationId() {
		return navigationId;
	}


	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}


	public String getToPage() {
		return toPage;
	}


	public void setToPage(String toPage) {
		this.toPage = toPage;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
