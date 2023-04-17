package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillPerformanceElements;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddAppraisalLevelForAttribute extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 6066370977286203810L;
	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	private CommonFunctions CF;
	   
	private String[] strLevel;
	private String attributeName;
	private String ID; 
	private String operation;
	private List<FillPerformanceElements> elementList; 
	private String performanceElement;
	private String attributeThreshhold;
	private String attributeid;
	
	private String attributeDesc;
	private String systemInfo;
	
	private String submit;
	private String elementid;
	private String orgId;
	
	private List<FillLevel> levelList;
	
	private String type;
	private String arribute_level_id;
	
	private String strOrg;
	private String userscreen;
	private String navigationId;
	private String toPage;
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/AddAppraisalLevelForAttribute.jsp");
		request.setAttribute(TITLE, "Attribute");
		UtilityFunctions uF = new UtilityFunctions();
		
		elementList = new FillPerformanceElements(request).fillPerformanceElements();
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(orgId));
		
//		System.out.println("getOperation() ===>"+getOperation() );
//		System.out.println("getSubmit() ===>"+getSubmit() );
		
		if (getOperation() != null && getOperation().equals("A")) {
			
			if(getSubmit()!=null && getSubmit().equals("Save")){ 
				insertLevelforAtributeData();
				return "update";
			}
			getData();
			return SUCCESS;
		} else if (getOperation() != null && getOperation().equals("E")) {

			if(getSubmit()!=null && getSubmit().equals("Save")){ 
				updateLevelforAtributeData();
				return "update";
			}
			
			getAttributeLevelData();
			getData();
			
			return SUCCESS;
		}if (getOperation() != null && getOperation().equals("D")) {
			
			deleteData();
			return "update";
		}

		return SUCCESS;

	}
	


	private void getAttributeLevelData() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM level_details order by level_id");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmLevel = new HashMap<String, String>();
			
			while(rs.next()){
				hmLevel.put(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name"));
			}
			rs.close();
			pst.close();
							
			pst = con.prepareStatement("select * from appraisal_attribute_level where arribute_level_id=?");
			pst.setInt(1, uF.parseToInt(getArribute_level_id()));
//			System.out.println("pst====>"+pst);
			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String,String> attributeDetails=new HashMap<String,String>();
			while(rs.next()){
				attributeDetails.put("ID",rs.getString("arribute_level_id"));
				attributeDetails.put("LEVEL_ID",rs.getString("level_id"));
				attributeDetails.put("Level_NAME",uF.showData(hmLevel.get(rs.getString("level_id")), ""));
				attributeDetails.put("THRESHOLD",rs.getString("threshhold"));
				attributeDetails.put("ARRTIBUTE_ID",rs.getString("attribute_id"));	
				setAttributeid(rs.getString("attribute_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("attributeDetails level for aatribute====> "+attributeDetails);
			request.setAttribute("attributeDetails",attributeDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}



	private void updateLevelforAtributeData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
				
			pst = con.prepareStatement("update appraisal_attribute_level set threshhold=? where arribute_level_id=?");
			pst.setDouble(1, uF.parseToDouble(getAttributeThreshhold()));
			pst.setInt(2, uF.parseToInt(getArribute_level_id()));
//			System.out.println("pst=====>"+pst);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}



	private void getData() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from appraisal_element");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmPerformanceElement = new HashMap<String, String>();
			while(rs.next()){
				hmPerformanceElement.put(rs.getString("appraisal_element_id"), rs.getString("appraisal_element_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_attribute aa left join appraisal_element_attribute aea " +
					" on aa.arribute_id = aea.appraisal_attribute where aa.arribute_id=?");
			pst.setInt(1, uF.parseToInt(getAttributeid()));
			
			Map<String,String> attributeList=new HashMap<String, String>();
			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				attributeList.put("ATTRIBUTE_ID",rs.getString("arribute_id"));
				attributeList.put("ATTRIBUTE_NAME",rs.getString("attribute_name"));
				attributeList.put("ELEMENT_NAME",uF.showData(hmPerformanceElement.get(rs.getString("appraisal_element")), ""));
				attributeList.put("ATTRIBUTE_DESC",rs.getString("attribute_desc"));
				attributeList.put("ATTRIBUTE_INFO",rs.getString("attribute_info"));
			}
			rs.close();
			pst.close();
			request.setAttribute("attributeList",attributeList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void insertLevelforAtributeData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
//			boolean flag = false;
//			pst = con.prepareStatement("select arribute_level_id from appraisal_attribute_level where level_id = ? and attribute_id = ? and element_id = ? ");
//			pst.setInt(1, uF.parseToInt(getStrLevel()));
//			pst.setInt(2, uF.parseToInt(getAttributeid()));
//			pst.setInt(3, uF.parseToInt(getElementid()));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				flag = true;
//			}
			for(int i=0; getStrLevel() != null && i<getStrLevel().length; i++) {
				pst = con.prepareStatement("update appraisal_attribute_level set threshhold =? where level_id =? and attribute_id = ? and element_id = ?");
				pst.setDouble(1, uF.parseToDouble(getAttributeThreshhold()));
				pst.setInt(2, uF.parseToInt(getStrLevel()[i]));
				pst.setInt(3, uF.parseToInt(getAttributeid()));
				pst.setInt(4, uF.parseToInt(getElementid()));
				int cnt = pst.executeUpdate();
				pst.close();
				if(cnt == 0){
					pst = con.prepareStatement("insert into appraisal_attribute_level(level_id,threshhold,attribute_id,element_id) values (?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrLevel()[i]));
					pst.setDouble(2, uF.parseToDouble(getAttributeThreshhold()));
					pst.setInt(3, uF.parseToInt(getAttributeid()));
					pst.setInt(4, uF.parseToInt(getElementid()));
					pst.execute();	
					pst.close();
				}
			}
//			System.out.println("pst ===> "+pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void deleteData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from appraisal_attribute_level where arribute_level_id=?");
			pst.setInt(1, uF.parseToInt(getArribute_level_id()));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String[] getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String[] strLevel) {
		this.strLevel = strLevel;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<FillPerformanceElements> getElementList() {
		return elementList;
	}

	public void setElementList(List<FillPerformanceElements> elementList) {
		this.elementList = elementList;
	}

	public String getPerformanceElement() {
		return performanceElement;
	}

	public void setPerformanceElement(String performanceElement) {
		this.performanceElement = performanceElement;
	}

	public String getAttributeThreshhold() {
		return attributeThreshhold;
	}

	public void setAttributeThreshhold(String attributeThreshhold) {
		this.attributeThreshhold = attributeThreshhold;
	}

	public String getAttributeid() {
		return attributeid;
	}

	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
	}

	public String getAttributeDesc() {
		return attributeDesc;
	}

	public void setAttributeDesc(String attributeDesc) {
		this.attributeDesc = attributeDesc;
	}

	public String getSystemInfo() {
		return systemInfo;
	}

	public void setSystemInfo(String systemInfo) {
		this.systemInfo = systemInfo;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getElementid() {
		return elementid;
	}

	public void setElementid(String elementid) {
		this.elementid = elementid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getArribute_level_id() {
		return arribute_level_id;
	}

	public void setArribute_level_id(String arribute_level_id) {
		this.arribute_level_id = arribute_level_id;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
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
	
	

}
