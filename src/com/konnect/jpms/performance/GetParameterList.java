package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.opensymphony.xwork2.ActionSupport;

public class GetParameterList  extends ActionSupport implements ServletRequestAware{


	private String strOrg;
	private String strWork;
	private String strDepart;
	private String strLevel;
	private String type;
	
	private List<FillAttribute> paramList;

	private static final long serialVersionUID = 1L;

	public String execute() {
		getElementList();
		
		if(getType() !=null && getType().equals("org") && getStrOrg()!=null && !getStrOrg().equals("")){
			getParameterList1();
		}else if(getType() !=null && getType().equals("wlocation") && getStrWork()!=null && !getStrWork().equals("")){
			getParameterListByWLocation();
		}else if(getType() !=null && getType().equals("department") && getStrDepart()!=null && !getStrDepart().equals("")){
			getParameterListByDepartment();
		}else if(getType() !=null && getType().equals("level") && getStrLevel()!=null && !getStrLevel().equals("")){
			getParameterListByLevel();
		}
		 
		
		return SUCCESS;

	}

	public void getElementList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
			rs = pst.executeQuery();
			List<List<String>> elementouterList=new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("appraisal_element_id"));
				innerList.add(rs.getString("appraisal_element_name"));
				elementouterList.add(innerList);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("elementouterList",elementouterList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
private void getParameterListByLevel() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select distinct(arribute_id),attribute_name,aal.element_id from appraisal_attribute aa, " +
					" appraisal_attribute_level aal where aa.arribute_id=aal.attribute_id and aa.arribute_id is not null");
			//if(){
				sbQuery.append(" and aal.level_id in ("+getStrLevel()+")");
			//}
			sbQuery.append(" order by element_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("getParameterListByLevel pst ===> " + pst);
			rs = pst.executeQuery();			
		
			Map<String,List<List<String>>> hmElementAttribute=new HashMap<String, List<List<String>>>();
			while(rs.next()){
//					al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name")));
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_id"));
				innerList.add(rs.getString("attribute_name"));
				
				List<List<String>> attributeouterList=hmElementAttribute.get(rs.getString("element_id"));
				if(attributeouterList==null) attributeouterList=new ArrayList<List<String>>();
				
				attributeouterList.add(innerList);
				hmElementAttribute.put(rs.getString("element_id"), attributeouterList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmElementAttribute",hmElementAttribute);				
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
private void getParameterListByDepartment() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select distinct(arribute_id),attribute_name,aal.element_id from appraisal_attribute aa, " +
					" appraisal_attribute_level aal where aa.arribute_id=aal.attribute_id and aa.arribute_id is not null ");
			//if(getStrDepart()!=null && !getStrDepart().equals("")){
				sbQuery.append(" and aal.level_id in (select level_id from level_details where org_id in (select org_id from department_info where dept_id in ("+getStrDepart()+")))");
			//}
			sbQuery.append(" order by element_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
//			System.out.println("getParameterListByDepartment pst =====> "+pst);
			Map<String,List<List<String>>> hmElementAttribute=new HashMap<String, List<List<String>>>();
			while(rs.next()){
//				al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name")));
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_id"));
				innerList.add(rs.getString("attribute_name"));
				
				List<List<String>> attributeouterList=hmElementAttribute.get(rs.getString("element_id"));
				if(attributeouterList==null) attributeouterList=new ArrayList<List<String>>();
				
				attributeouterList.add(innerList);
				hmElementAttribute.put(rs.getString("element_id"), attributeouterList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmElementAttribute",hmElementAttribute);
//			System.out.println("hmElementAttribute =====> "+hmElementAttribute);
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	
private void getParameterListByWLocation() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select distinct(arribute_id),attribute_name,aal.element_id from appraisal_attribute aa, " +
					" appraisal_attribute_level aal where aa.arribute_id=aal.attribute_id and aa.arribute_id is not null ");
//			if(getStrWork()!=null && !getStrWork().equals("")){
				sbQuery.append(" and aal.level_id in (select level_id from level_details where org_id in (select org_id from work_location_info where wlocation_id in ("+getStrWork()+")))");
//			}
			sbQuery.append(" order by element_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("getParameterListByWLocation pst =====> "+pst);
			Map<String,List<List<String>>> hmElementAttribute=new HashMap<String, List<List<String>>>();
			while(rs.next()){
//				al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name")));
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_id"));
				innerList.add(rs.getString("attribute_name"));
				
				List<List<String>> attributeouterList=hmElementAttribute.get(rs.getString("element_id"));
				if(attributeouterList==null) attributeouterList=new ArrayList<List<String>>();
				
				attributeouterList.add(innerList);
				hmElementAttribute.put(rs.getString("element_id"), attributeouterList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmElementAttribute",hmElementAttribute);
//			System.out.println("hmElementAttribute =====> "+hmElementAttribute);
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	private void getParameterList1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			StringBuilder sbQuery=new StringBuilder();
			/*sbQuery.append("select * from(select arribute_id,attribute_name,aa.level_id from appraisal_attribute aa,appraisal_attribute_level aal where aa.arribute_id=aal.attribute_id ) as a WHERE a.arribute_id is not null ");
			if(getStrOrg()!=null && !getStrOrg().equals("")){
				sbQuery.append(" and a.level_id in (select level_id from level_details where org_id in ("+getStrOrg()+"))");
			}
			sbQuery.append(" order by a.attribute_name");*/
			
			sbQuery.append("select distinct(arribute_id),attribute_name,aal.element_id from appraisal_attribute aa, " +
					" appraisal_attribute_level aal where aa.arribute_id=aal.attribute_id and aa.arribute_id is not null ");
//			if(getStrOrg()!=null && !getStrOrg().equals("")){
				sbQuery.append(" and aal.level_id in (select level_id from level_details where org_id in ("+getStrOrg()+"))");
//			}
			sbQuery.append(" order by element_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();	
//			System.out.println("getParameterList1 pst =====> "+pst);
			Map<String,List<List<String>>> hmElementAttribute=new HashMap<String, List<List<String>>>();
			
			while(rs.next()){
				//al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name")));				
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_id"));
				innerList.add(rs.getString("attribute_name"));
				
				List<List<String>> attributeouterList=hmElementAttribute.get(rs.getString("element_id"));
				if(attributeouterList==null) attributeouterList=new ArrayList<List<String>>();
				
				attributeouterList.add(innerList);
				hmElementAttribute.put(rs.getString("element_id"), attributeouterList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmElementAttribute",hmElementAttribute);
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrWork() {
		return strWork;
	}

	public void setStrWork(String strWork) {
		this.strWork = strWork;
	}

	public String getStrDepart() {
		return strDepart;
	}

	public void setStrDepart(String strDepart) {
		this.strDepart = strDepart;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FillAttribute> getParamList() {
		return paramList;
	}

	public void setParamList(List<FillAttribute> paramList) {
		this.paramList = paramList;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;

	}

}
