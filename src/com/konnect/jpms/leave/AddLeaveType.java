package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillColour;
import com.konnect.jpms.select.FillLeaveCategory;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddLeaveType extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	private String orgId; 
	boolean isMeternity; 
	private String strLocation; 
	private boolean isSandwich; 
	private String strColour;
	private String leaveType;
	private String leaveCode;
	private String leaveTypeId;
	private List<FillColour> colourList;
	private boolean isDocumentRequired;
	private boolean isCompensatory;
	private boolean isLeaveEncashment;
	private String minLeavesRequiredEncashment;
	private String strEncashApplicable;
	private String noOfTimes;
	private String defaultSelectEncash;
	
	private String isLeaveOptHoliday;
	private String isWorkFromHome;
	private String isShortLeave;// Created by dattatray Date:28-july-21 Note : added is_short_leave
	
	String leaveCategory;
	String defaultLeaveCategory;
	List<FillLeaveCategory> leaveCategoryList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	private String isBereavementLeave;		//created by parvez date: 27-09-2022
	private String alignWeekendHoliday;		//created by parvez date: 30-09-2022
	private String isMandatoryDocument;		//created by parvez date: 18-11-2022
	
	public String execute() throws Exception {

		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
	
		session = request.getSession();
		
		colourList = new FillColour(request).fillColour();
		leaveCategoryList = new FillLeaveCategory().fillLeaveCategory();
		
		if (operation!=null && operation.equals("D")) {
			return deleteLeaveType(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewWlocationType(strId);
		}
		if (getLeaveTypeId()!=null && getLeaveTypeId().length()>0) { 
			return updateLeaveType();
		}
		if(getLeaveType()!=null && getLeaveType().length()>0){
			return insertLeaveType();
		}
		setDefaultSelectEncash("2");
		setDefaultLeaveCategory("0");
		 
		return LOAD;
	}

	
	
	public String loadLeaveType() {
		request.setAttribute(PAGE, PAddLeaveType);
		request.setAttribute(TITLE, TAddLeaveType);
		
		setLeaveType("");
		setLeaveTypeId("");
		setIsCompensatory(false);
		setIsDocumentRequired(false);
		setIsLeaveEncashment(false);
		setMinLeavesRequiredEncashment(null);
		
		setIsLeaveOptHoliday("false");
		
		return LOAD;
	}

	
	public String viewWlocationType(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from leave_type where leave_type_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setLeaveType(rs.getString("leave_type_name"));
				setLeaveCode(rs.getString("leave_type_code"));
				setLeaveTypeId(rs.getString("leave_type_id"));
				setStrColour(rs.getString("leave_type_colour"));
				setIsDocumentRequired(uF.parseToBoolean(rs.getString("is_document_required")));
				setIsCompensatory(uF.parseToBoolean(rs.getString("is_compensatory")));
				setIsLeaveEncashment(uF.parseToBoolean(rs.getString("is_leave_encashment")));
				setMinLeavesRequiredEncashment(uF.parseToDouble(rs.getString("min_leave_encashment"))+"");
				setIsMeternity(uF.parseToBoolean(rs.getString("is_maternity")));
				setIsSandwich(uF.parseToBoolean(rs.getString("is_sandwich")));
				setOrgId(rs.getString("org_id"));
				
//				setStrEncashApplicable(rs.getString("encashment_applicable"));
				setDefaultSelectEncash(rs.getString("encashment_applicable"));
				setNoOfTimes(rs.getString("encashment_times"));

				setIsLeaveOptHoliday(uF.parseToBoolean(rs.getString("is_leave_opt_holiday")) ? "true" : "false");
				setIsWorkFromHome(uF.parseToBoolean(rs.getString("is_work_from_home")) ? "true" : "false");
				setDefaultLeaveCategory(""+uF.parseToInt(rs.getString("leave_category")));
				setIsShortLeave(uF.parseToBoolean(rs.getString("is_short_leave")) ? "true" : "false");
				setIsBereavementLeave(uF.parseToBoolean(rs.getString("is_bereavement_leave")) ? "true" : "false");// Created by parvez date:27-09-2022
				setAlignWeekendHoliday(uF.parseToBoolean(rs.getString("is_align_weekend_holiday")) ? "true" : "false");// Created by parvez date:30-09-2022
				setIsMandatoryDocument(uF.parseToBoolean(rs.getString("is_document_mandatory")) ? "true" : "false");
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
		return UPDATE;
	}
	
	

	public String updateLeaveType() {
		Connection con = null;
		PreparedStatement pst =null;;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
	//===start parvez date: 27-09-2022===		
			pst = con.prepareStatement("UPDATE leave_type SET leave_type_name=?, leave_type_colour=?, leave_type_code=?, is_document_required=?, " +
					"is_compensatory=?, is_leave_encashment=?, min_leave_encashment=?,is_maternity=?,is_sandwich=?,encashment_applicable=?," +
					"encashment_times=?,is_leave_opt_holiday=?,leave_category=?,is_work_from_home=?,is_short_leave=?, is_bereavement_leave=?, " +
					" is_align_weekend_holiday=?,is_document_mandatory=? where leave_type_id=?");
	//===end parvez date: 27-09-2022===		
			pst.setString(1, getLeaveType());
			pst.setString(2, getStrColour());
			pst.setString(3, getLeaveCode());
			pst.setBoolean(4, getIsDocumentRequired());
			pst.setBoolean(5, getIsCompensatory());
			pst.setBoolean(6, getIsLeaveEncashment());
			pst.setDouble(7, uF.parseToDouble(getMinLeavesRequiredEncashment()));
			pst.setBoolean(8,getIsMeternity());
			pst.setBoolean(9,getIsSandwich());
			pst.setInt(10, uF.parseToInt(getStrEncashApplicable()));
			pst.setInt(11, uF.parseToInt(getNoOfTimes()));
			pst.setBoolean(12, uF.parseToBoolean(getIsLeaveOptHoliday()));
			pst.setInt(13, uF.parseToInt(getLeaveCategory()));
			pst.setBoolean(14, uF.parseToBoolean(getIsWorkFromHome()));
			pst.setBoolean(15, uF.parseToBoolean(getIsShortLeave()));// Created by dattatray Date:28-july-21
		//===start parvez date: 27-09-2022===	
//			pst.setInt(16, uF.parseToInt(getLeaveTypeId()));
			pst.setBoolean(16, uF.parseToBoolean(getIsBereavementLeave()));
			pst.setBoolean(17, uF.parseToBoolean(getAlignWeekendHoliday()));
			pst.setBoolean(18, uF.parseToBoolean(getIsMandatoryDocument()));
			pst.setInt(19, uF.parseToInt(getLeaveTypeId()));
		//===end parvez date: 27-09-2022===	
//			System.out.println("update pst==>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				pst = con.prepareStatement("UPDATE emp_leave_type SET is_compensatory=? where leave_type_id=?");
				pst.setBoolean(1, getIsCompensatory());
				pst.setInt(2, uF.parseToInt(getLeaveTypeId()));
//				System.out.println("update pst==>"+pst);
				pst.execute();
				pst.close();
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+getLeaveType()+" updated successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String insertLeaveType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement(insertLeaveType);
	//===start parvez date: 27-09-2022===
			pst = con.prepareStatement("INSERT INTO leave_type (leave_type_name, leave_type_code, leave_type_colour,is_document_required, " +
				"is_compensatory, is_leave_encashment, min_leave_encashment, org_id,is_maternity,is_sandwich,encashment_applicable,encashment_times," +
				"is_leave_opt_holiday,leave_category,is_work_from_home,is_short_leave,is_bereavement_leave,is_align_weekend_holiday,is_document_mandatory) " +
				"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
	//===end parvez date: 27-09-2022===
			pst.setString(1, getLeaveType());
			pst.setString(2, getLeaveCode());
			pst.setString(3, getStrColour());
			pst.setBoolean(4, getIsDocumentRequired());
			pst.setBoolean(5, getIsCompensatory());
			pst.setBoolean(6, getIsLeaveEncashment());
			pst.setDouble(7, uF.parseToDouble(getMinLeavesRequiredEncashment()));
			pst.setInt(8, uF.parseToInt(getOrgId()));
			pst.setBoolean(9,getIsMeternity());
			pst.setBoolean(10,getIsSandwich());
			pst.setInt(11, uF.parseToInt(getStrEncashApplicable()));
			pst.setInt(12, uF.parseToInt(getNoOfTimes()));
			pst.setBoolean(13, uF.parseToBoolean(getIsLeaveOptHoliday()));
			pst.setInt(14, uF.parseToInt(getLeaveCategory()));
			pst.setBoolean(15, uF.parseToBoolean(getIsWorkFromHome()));
			pst.setBoolean(16, uF.parseToBoolean(getIsShortLeave()));// Created by dattatray Date:28-july-21
			pst.setBoolean(17, uF.parseToBoolean(getIsBereavementLeave()));
			pst.setBoolean(18, uF.parseToBoolean(getAlignWeekendHoliday()));		//created by parvez date:30-09-2022
			pst.setBoolean(19, uF.parseToBoolean(getIsMandatoryDocument()));		//created by parvez date:18-11-2022
//			System.out.println("insert pst==>"+pst);
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+getLeaveType()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+getLeaveType()+" could not be saved.<br/>Please try again."+END);
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}


	public String deleteLeaveType(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteLeaveType);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+" could not be deleted.<br/>Please try again."+END);
			return ERROR;
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
		
	public String getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(String leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		this.request.setAttribute(PAGE, PAddLeaveType);
	}

	public String getStrColour() {
		return strColour;
	}

	public void setStrColour(String strColour) {
		this.strColour = strColour;
	}

	public String getLeaveCode() {
		return leaveCode;
	}

	public void setLeaveCode(String leaveCode) {
		this.leaveCode = leaveCode;
	}

	public List<FillColour> getColourList() {
		return colourList;
	}
	
	public void setColourList(List<FillColour> colourList) {
		this.colourList = colourList;
	}
	
	public boolean getIsDocumentRequired() {
		return isDocumentRequired;
	}
	
	public void setIsDocumentRequired(boolean isDocumentRequired) {
		this.isDocumentRequired = isDocumentRequired;
	}
	
	public boolean getIsCompensatory() {
		return isCompensatory;
	}
	
	public void setIsCompensatory(boolean isCompensatory) {
		this.isCompensatory = isCompensatory;
	}
	
	public boolean getIsLeaveEncashment() {
		return isLeaveEncashment;
	}
	
	public void setIsLeaveEncashment(boolean isLeaveEncashment) {
		this.isLeaveEncashment = isLeaveEncashment;
	}
	
	public String getMinLeavesRequiredEncashment() {
		return minLeavesRequiredEncashment;
	}
	
	public void setMinLeavesRequiredEncashment(String minLeavesRequiredEncashment) {
		this.minLeavesRequiredEncashment = minLeavesRequiredEncashment;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrEncashApplicable() {
		return strEncashApplicable;
	}

	public void setStrEncashApplicable(String strEncashApplicable) {
		this.strEncashApplicable = strEncashApplicable;
	}

	public String getNoOfTimes() {
		return noOfTimes;
	}

	public void setNoOfTimes(String noOfTimes) {
		this.noOfTimes = noOfTimes;
	}

	public String getDefaultSelectEncash() {
		return defaultSelectEncash;
	}

	public void setDefaultSelectEncash(String defaultSelectEncash) {
		this.defaultSelectEncash = defaultSelectEncash;
	}

	public String getIsLeaveOptHoliday() {
		return isLeaveOptHoliday;
	}

	public void setIsLeaveOptHoliday(String isLeaveOptHoliday) {
		this.isLeaveOptHoliday = isLeaveOptHoliday;
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
	
	public boolean getIsMeternity() {
		return isMeternity;
	}

	public void setIsMeternity(boolean isMeternity) {
		this.isMeternity = isMeternity;
	}

	public boolean getIsSandwich() {
		return isSandwich;
	}

	public void setIsSandwich(boolean isSandwich) {
		this.isSandwich = isSandwich;
	}
	
	public String getLeaveCategory() {
		return leaveCategory;
	}

	public void setLeaveCategory(String leaveCategory) {
		this.leaveCategory = leaveCategory;
	}

	public String getDefaultLeaveCategory() {
		return defaultLeaveCategory;
	}

	public void setDefaultLeaveCategory(String defaultLeaveCategory) {
		this.defaultLeaveCategory = defaultLeaveCategory;
	}

	public List<FillLeaveCategory> getLeaveCategoryList() {
		return leaveCategoryList;
	}
	
	public void setLeaveCategoryList(List<FillLeaveCategory> leaveCategoryList) {
		this.leaveCategoryList = leaveCategoryList;
	}

	public String getIsWorkFromHome() {
		return isWorkFromHome;
	}

	public void setIsWorkFromHome(String isWorkFromHome) {
		this.isWorkFromHome = isWorkFromHome;
	}

	public String getIsShortLeave() {
		return isShortLeave;
	}

	public void setIsShortLeave(String isShortLeave) {
		this.isShortLeave = isShortLeave;
	}
//===start parvez date: 30-09-2022===
	public String getIsBereavementLeave() {
		return isBereavementLeave;
	}

	public void setIsBereavementLeave(String isBereavementLeave) {
		this.isBereavementLeave = isBereavementLeave;
	}
	
	public String getAlignWeekendHoliday() {
		return alignWeekendHoliday;
	}

	public void setAlignWeekendHoliday(String alignWeekendHoliday) {
		this.alignWeekendHoliday = alignWeekendHoliday;
	}
//===end parvez date: 30-09-2022===	

	public String getIsMandatoryDocument() {
		return isMandatoryDocument;
	}

	public void setIsMandatoryDocument(String isMandatoryDocument) {
		this.isMandatoryDocument = isMandatoryDocument;
	}
}