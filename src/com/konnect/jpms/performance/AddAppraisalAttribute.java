package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPerformanceElements;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddAppraisalAttribute extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = -6219343578524564527L;
	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	private CommonFunctions CF;
	
	private String level;
	private String attributeName; 
	private String ID;
	private String operation;
	private List<FillPerformanceElements> elementList; 
	private String performanceElement;
	private String attributeThreshhold;
	private String elementid;
	
	private String attributeDesc;
	private String isSystemInfo;
	private String systemInfo;
	
	private String submit;
	
	private String attributeid;
	
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
		request.setAttribute(PAGE, "/jsp/performance/AddAppraisalAttribute.jsp");
		request.setAttribute(TITLE, "Appraisal Attribute");
		UtilityFunctions uF = new UtilityFunctions();

		
		elementList = new FillPerformanceElements(request).fillPerformanceElements();
//		System.out.println("in getOperation =====>"+getOperation());
		if (getOperation() != null && getOperation().equals("A")) {
			if(getSubmit()!=null && getSubmit().equals("Save")){
				insertAtributeData();
				return "update";
			}
			setIsSystemInfo("0");
			return SUCCESS;
		} else if (getOperation() != null && getOperation().equals("E")) {
//			System.out.println("in edit");
			if(getSubmit()!=null && getSubmit().equals("Save")){
				updateAtributeData();
				return "update";
			}
			getData();
			return SUCCESS;
		}if (getOperation() != null && getOperation().equals("D")) {
			
			deleteData();
			return "update";
		}

		return SUCCESS;

	}
	


	private void updateAtributeData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("update appraisal_attribute set attribute_name=?,attribute_desc=?,attribute_info=? where arribute_id=?");
			pst.setString(1, getAttributeName());
			pst.setString(2, getAttributeDesc());
			pst.setInt(3, uF.parseToInt(getSystemInfo()));
			pst.setInt(4, uF.parseToInt(getAttributeid()));
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
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from appraisal_attribute where arribute_id=?");
			pst.setInt(1, uF.parseToInt(getAttributeid()));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				setAttributeName(rs.getString("attribute_name"));
				setSystemInfo(rs.getString("attribute_info"));
				if(getSystemInfo() != null && !getSystemInfo().equals("") && !getSystemInfo().equals("0")) {
					setIsSystemInfo("1");
				} else {
					setIsSystemInfo("0");
				}
				setAttributeDesc(rs.getString("attribute_desc"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private void insertAtributeData() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("insert into appraisal_attribute(attribute_name,status,attribute_desc,attribute_info) values (?,?,?,?)");
			pst.setString(1, getAttributeName());
			pst.setBoolean(2, true);
			pst.setString(3, getAttributeDesc());
			pst.setInt(4, uF.parseToInt(getSystemInfo()));
//			System.out.println("pst==>"+pst);
			pst.execute();
			pst.close();


			
			pst = con.prepareStatement("select max(arribute_id) as attribute_id from appraisal_attribute");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nAttributeId = 0;
			while(rs.next()){
				nAttributeId = uF.parseToInt(rs.getString("attribute_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("nAttributeId==>"+nAttributeId);
			if(nAttributeId>0){
				pst = con.prepareStatement("insert into appraisal_element_attribute (appraisal_attribute,appraisal_element,status) values (?,?,?)");
				pst.setInt(1, nAttributeId);
				pst.setInt(2, uF.parseToInt(getElementid()));
				pst.setBoolean(3, true);
//				System.out.println("pst2==>"+pst);
				pst.execute();
				pst.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	public void insertData(int id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("insert into appraisal_attribute(level_id,attribute_name,status, threshhold) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getLevel()));
			pst.setString(2, getAttributeName());
			pst.setBoolean(3, true);
			pst.setDouble(4, uF.parseToDouble(getAttributeThreshhold()));
			pst.execute();
			pst.close();


			
			pst = con.prepareStatement("select max(arribute_id) as attribute_id from appraisal_attribute");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nAttributeId = 0;
			while(rs.next()){
				nAttributeId = uF.parseToInt(rs.getString("attribute_id"));
			}
			rs.close();
			pst.close();
			
			
			if(nAttributeId>0){
				pst = con.prepareStatement("insert into appraisal_element_attribute (appraisal_attribute,appraisal_element,status) values (?,?,?)");
				pst.setInt(1, nAttributeId);
				pst.setInt(2, uF.parseToInt(getPerformanceElement()));
				pst.setBoolean(3, true);
				pst.execute();
				pst.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void deleteData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			
			pst = con.prepareStatement("delete from appraisal_attribute where arribute_id=?");
			pst.setInt(1, uF.parseToInt(getAttributeid()));
			pst.execute();
			pst.close();
			
			
			pst = con.prepareStatement("delete from appraisal_element_attribute where appraisal_attribute=?");
			pst.setInt(1, uF.parseToInt(getAttributeid()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from appraisal_attribute_level where attribute_id=?");
			pst.setInt(1, uF.parseToInt(getAttributeid()));
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
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

	public String getIsSystemInfo() {
		return isSystemInfo;
	}

	public void setIsSystemInfo(String isSystemInfo) {
		this.isSystemInfo = isSystemInfo;
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

	public String getElementid() {
		return elementid;
	}

	public void setElementid(String elementid) {
		this.elementid = elementid;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getAttributeid() {
		return attributeid;
	}

	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
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
