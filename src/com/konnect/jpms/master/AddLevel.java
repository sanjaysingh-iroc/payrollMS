package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.select.FillWeightage;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddLevel extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AddLevel.class);
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	
	String strOrg;
	
//	List<FillWeekDays> weeklyOffTypeList;
//	List<FillWeekDays> weeklyOffList;
//	List<FillWeekDays> weeklyOffList1;
	
	List<FillOrganisation> orgList;
	
//	String weeklyOff1; 
//	String weeklyOff2;
//	String weeklyOff3;
//	String weeklyOffType1; 
//	String weeklyOffType2;
//	String weeklyOffType3;
//	String []weekno3;
	
	String strWeightage;
	List<FillWeightage> weightageList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {

		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		loadValidateLevel();
		
//		orgList = new FillOrganisation(request).fillOrganisation();
		
//		levelList = new FillLevel(request).fillLevelBYORG(0);
//		weeklyOffList= new FillWeekDays().fillWeekDays();
//		weeklyOffList1= new FillWeekDays().fillWeekNos();
//		weeklyOffTypeList= new FillWeekDays().fillWeeklyOffType();
		weightageList = new FillWeightage().fillWeightage();
		
		if (operation!=null && operation.equals("D")) {
			return deleteLevel(strId, uF); 
		} 
		if (operation!=null && operation.equals("E")) { 
			return viewLevel(strId, uF);
		}
		if (getLevelId()!=null && getLevelId().length()>0) { 
			return updateLevel(uF);
		}
		
		
		if(getLevelCode()!=null && getLevelCode().length()>0){
			return insertLevel(uF);
		}
		return LOAD;
	}

	
	public String loadValidateLevel() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
			String empOrgId = null;
			if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ADD_IN_LOGIN_USER_ORG)) && hmFeatureUserTypeId.get(IConstants.F_ADD_IN_LOGIN_USER_ORG).contains(strUsertypeId)) {
				empOrgId = (String)session.getAttribute(ORGID);
			}
			orgList = new FillOrganisation(request).fillOrganisation(empOrgId);
			if(getStrOrg()==null && orgList!=null && orgList.size()>0){
				setStrOrg((String)session.getAttribute(ORGID));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	
	public String insertLevel(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("INSERT INTO level_details (level_code, level_name, level_description,standard_working_hours,standard_overtime_hours, flat_deduction, org_id, level_parent" +
//					",wlocation_weeklyoff1,wlocation_weeklyofftype1,wlocation_weeklyoff2,wlocation_weeklyofftype2,wlocation_weeklyoff3,wlocation_weeklyofftype3," +
//					"wlocation_weeknos3,weightage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst = con.prepareStatement("INSERT INTO level_details (level_code, level_name, level_description, flat_deduction, org_id" +
					",weightage) VALUES (?,?,?,?, ?,?)");
			pst.setString(1, getLevelCode());
			pst.setString(2, getLevelName());
			pst.setString(3, uF.showData(getLevelDesc(),""));
//			pst.setDouble(4, uF.parseToDouble(getStrStdWorkingHrs()));
//			pst.setDouble(5, uF.parseToDouble(getStrStdOvertimeHrs()));
			pst.setBoolean(4, getIsFlatTDSDeduction());
			pst.setInt(5, uF.parseToInt(getStrOrg()));
//			pst.setInt(8, uF.parseToInt(getStrParentId()));
			
//			pst.setString(9, getWeeklyOff1());
//			pst.setString(10, getWeeklyOffType1());
//			pst.setString(11, getWeeklyOff2());
//			pst.setString(12, getWeeklyOffType2());
//			pst.setString(13, getWeeklyOff3());
//			pst.setString(14, getWeeklyOffType3());
			
//			StringBuilder sb = new StringBuilder();
//			for(int i=0; getWeekno3()!=null && i<getWeekno3().length; i++){
//				sb.append(getWeekno3()[i]+",");
//			}
//			pst.setString(15, sb.toString());
			pst.setInt(6, uF.parseToInt(getStrWeightage()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getLevelCode()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	public String viewLevel(String strId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from level_details where level_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setLevelCode(rs.getString("level_code"));
				setLevelName(rs.getString("level_name"));
				setLevelDesc(rs.getString("level_description"));
				setLevelId(rs.getString("level_id"));
//				setStrStdWorkingHrs(rs.getString("standard_working_hours"));
//				setStrStdOvertimeHrs(rs.getString("standard_overtime_hours"));
				setIsFlatTDSDeduction(uF.parseToBoolean(rs.getString("flat_deduction")));
				setStrOrg(rs.getString("org_id"));
				setStrParentId(rs.getString("level_parent"));
				
//				setWeeklyOff1(rs.getString("wlocation_weeklyoff1"));
//				setWeeklyOffType1(rs.getString("wlocation_weeklyofftype1"));
//				setWeeklyOff2(rs.getString("wlocation_weeklyoff2"));
//				setWeeklyOffType2(rs.getString("wlocation_weeklyofftype2"));
//				setWeeklyOff3(rs.getString("wlocation_weeklyoff3"));
//				setWeeklyOffType3(rs.getString("wlocation_weeklyofftype3"));
//				String []arr = null;
//				if(rs.getString("wlocation_weeknos3")!=null){
//					arr = rs.getString("wlocation_weeknos3").split(",");
//					setWeekno3(arr);
//				}
				
				setStrWeightage(rs.getString("weightage"));
			}
			rs.close();
			pst.close();
//			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}
	
	

	public String updateLevel(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
//		String updateLevel = "UPDATE level_details SET level_code=?, level_name=?, level_description=?, standard_working_hours=?, standard_overtime_hours=?, " +
//				"flat_deduction=?,level_parent=?,wlocation_weeklyoff1=?,wlocation_weeklyofftype1=?,wlocation_weeklyoff2=?,wlocation_weeklyofftype2=?," +
//				"wlocation_weeklyoff3=?,wlocation_weeklyofftype3=?,wlocation_weeknos3=?, org_id=?,weightage=? WHERE level_id=?";
		
		try {
			con = db.makeConnection(con);
			String updateLevel = "UPDATE level_details SET level_code=?, level_name=?, level_description=?, " +
			"flat_deduction=?, org_id=?, weightage=? WHERE level_id=?";
			pst = con.prepareStatement(updateLevel);
			pst.setString(1, getLevelCode());
			pst.setString(2, getLevelName());
			pst.setString(3, getLevelDesc());
//			pst.setDouble(4, uF.parseToDouble(getStrStdWorkingHrs()));
//			pst.setDouble(5, uF.parseToDouble(getStrStdOvertimeHrs()));
			pst.setBoolean(4, getIsFlatTDSDeduction());
//			pst.setInt(7, uF.parseToInt(getStrParentId()));
			
			
//			pst.setString(8, getWeeklyOff1());
//			pst.setString(9, getWeeklyOffType1());
//			pst.setString(10, getWeeklyOff2());
//			pst.setString(11, getWeeklyOffType2());
//			pst.setString(12, getWeeklyOff3());
//			pst.setString(13, getWeeklyOffType3());
//			
//			StringBuilder sb = new StringBuilder();
//			for(int i=0; getWeekno3()!=null && i<getWeekno3().length; i++){
//				sb.append(getWeekno3()[i]+",");
//			}
//			pst.setString(14, sb.toString());
			pst.setInt(5, uF.parseToInt(getStrOrg()));
			pst.setInt(6, uF.parseToInt(getStrWeightage()));
			pst.setInt(7, uF.parseToInt(getLevelId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+getLevelCode()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		if("LeaveTypeReport.action".equalsIgnoreCase(request.getParameter("URI"))){
			return "success_redirect";
		}
		return SUCCESS;

	}
	
	public String deleteLevel(String strId,UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteLevel);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select * FROM designation_details where level_id = ?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			String strDesigId = null;
			while(rs.next()){
				strDesigId = rs.getString("designation_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(deleteDesig1);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteGrade1);
			pst.setInt(1, uF.parseToInt(strDesigId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
			
			//Delete Salary Heads related to the level.
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		if("LeaveTypeReport.action".equalsIgnoreCase(request.getParameter("URI"))){
			return "success_redirect";
		}
		return SUCCESS;

	}

	String levelId;
	String levelCode;
	String levelName;
	String levelDesc;
//	String strStdWorkingHrs;
//	String strStdOvertimeHrs;
	boolean isFlatTDSDeduction;
	String strParentId;
//	List<FillLevel> levelList;


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getLevelDesc() {
		return levelDesc;
	}

	public void setLevelDesc(String levelDesc) {
		this.levelDesc = levelDesc;
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public boolean getIsFlatTDSDeduction() {
		return isFlatTDSDeduction;
	}

	public void setIsFlatTDSDeduction(boolean isFlatTDSDeduction) {
		this.isFlatTDSDeduction = isFlatTDSDeduction;
	}

	public String getStrParentId() {
		return strParentId;
	}

	public void setStrParentId(String strParentId) {
		this.strParentId = strParentId;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrWeightage() {
		return strWeightage;
	}

	public void setStrWeightage(String strWeightage) {
		this.strWeightage = strWeightage;
	}

	public List<FillWeightage> getWeightageList() {
		return weightageList;
	}

	public void setWeightageList(List<FillWeightage> weightageList) {
		this.weightageList = weightageList;
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