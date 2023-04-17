package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPerformanceElements;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author user
 *
 */
public class AddAppraisalAttributeForLevel extends ActionSupport implements
		ServletRequestAware, IStatements {

	
	private static final long serialVersionUID = 6066370977286203810L;
	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;
	private CommonFunctions CF;
	
	private String strLevel;
	private String attributeid;
	private String ID;
	private String operation;
	private List<FillPerformanceElements> elementList; 
	private String performanceElement;
	private String attributeThreshhold;
	
	private String attributeDesc;
	private String systemInfo;
	private String elementid;
	
	private String submit;
	private List<FillAttribute> attributeList;
	
	private String arribute_level_id;
	private String type;

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
		attributeList = new FillAttribute(request).fillElementAttribute(null);
		
		if (getOperation() != null && getOperation().equals("A")) {
			if(getSubmit() != null && getSubmit().equals("Save")) {
				insertAtributeforLevelData();
				return "update";
			}
			return SUCCESS;
		} else if (getOperation() != null && getOperation().equals("E")) {
			
			getAttributeLevelData();
			
			if(getSubmit()!=null && getSubmit().equals("Save")) {
				updateAtributeforLevelData();
				return "update";
			}
			
			return SUCCESS;
		}if (getOperation() != null && getOperation().equals("D")) {
			
			deleteData();
			return "update";
		}

		return SUCCESS;

	}
	
	private void updateAtributeforLevelData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
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

	private void getAttributeLevelData() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM appraisal_attribute order by arribute_id");
			rs = pst.executeQuery();
			Map<String, String> hmAttribute = new HashMap<String, String>();
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
							
			pst = con.prepareStatement("select * from appraisal_attribute_level where arribute_level_id=?");
			pst.setInt(1, uF.parseToInt(getArribute_level_id()));
//			System.out.println("pst====>"+pst);
			rs=pst.executeQuery();
			System.out.println("new Date ===> " + new Date());
			Map<String,String> attributeDetails=new HashMap<String,String>();
			while(rs.next()){
				attributeDetails.put("ID",rs.getString("arribute_level_id"));
				attributeDetails.put("LEVEL_ID",rs.getString("level_id"));
				attributeDetails.put("ATTRIBUTE_NAME",uF.showData(hmAttribute.get(rs.getString("attribute_id")), ""));
				attributeDetails.put("THRESHOLD",rs.getString("threshhold"));
				attributeDetails.put("ARRTIBUTE_ID",rs.getString("attribute_id"));				
			}
			rs.close();
			pst.close();
		
			request.setAttribute("attributeDetails",attributeDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}

	private void insertAtributeforLevelData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
				
			pst = con.prepareStatement("insert into appraisal_element_attribute (appraisal_attribute,appraisal_element,status) values (?,?,?)");
			pst.setInt(1, uF.parseToInt(getAttributeid()));
			pst.setInt(2, uF.parseToInt(getElementid()));
			pst.setBoolean(3, true);
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
				
			pst = con.prepareStatement("insert into appraisal_attribute_level(level_id,threshhold,attribute_id,element_id) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
			pst.setDouble(2, uF.parseToDouble(getAttributeThreshhold()));
			pst.setInt(3, uF.parseToInt(getAttributeid()));
			pst.setInt(4, uF.parseToInt(getElementid()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}

//	private void getData() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		ResultSet rs = null;
//		UtilityFunctions uF=new UtilityFunctions();
//		List<List<String>> outerList = null;
//		db.setRequest(request);
//		try {
//			con = db.makeConnection(con);
//			
//			pst = con.prepareStatement("select * from appraisal_element");
//			rs = pst.executeQuery();
//			Map<String, String> hmPerformanceElement = new HashMap<String, String>();
//			while(rs.next()){
//				hmPerformanceElement.put(rs.getString("appraisal_element_id"), rs.getString("appraisal_element_name"));
//			}
//			
//			pst = con.prepareStatement("select * from appraisal_attribute aa left join appraisal_element_attribute aea " +
//					" on aa.arribute_id = aea.appraisal_attribute where aa.arribute_id=?");
//			
//			List<String> attributeList=new ArrayList<String>();
//			rs=pst.executeQuery();
//			while(rs.next()){
//				attributeList.add(rs.getString("arribute_id"));
//				attributeList.add(rs.getString("attribute_name"));
//				attributeList.add(uF.showData(hmPerformanceElement.get(rs.getString("appraisal_element")), ""));
//				attributeList.add(rs.getString("attribute_desc"));
//				attributeList.add(rs.getString("attribute_info"));
//				 
//			}
//			request.setAttribute("attributeList",attributeList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//		}
//	}

	public void deleteData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getAttributeid() {
		
		return attributeid;
	}

	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
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

	public String getElementid() {
		return elementid;
	}

	public void setElementid(String elementid) {
		this.elementid = elementid;
	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public String getArribute_level_id() {
		return arribute_level_id;
	} 

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setArribute_level_id(String arribute_level_id) {
		this.arribute_level_id = arribute_level_id;
	}

}
