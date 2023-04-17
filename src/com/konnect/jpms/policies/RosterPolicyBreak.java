package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillBreakType;
import com.konnect.jpms.select.FillDesignation;
import com.konnect.jpms.select.FillInOut;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillTimeType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RosterPolicyBreak extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionOrgId = null;
	
	String rosterpolicyHDId;
	String timeValue;
	String dayValue;
	String monthValue;
	String []in_out;
	String orgId;
	 
	String strEffectiveDate; 
	
	List<FillInOut> in_out_List;
	List <FillBreakType> breakList;
	String strWLocation;
	String breakType;
	
	List<FillLeaveType> leaveTypeList;
	String strLeaveType;
	String isAlignLeave;
	
	CommonFunctions CF = null; 
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception { 
		
		request.setAttribute(PAGE, PPolicyRosterBreak);
		session = request.getSession();

		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getOrgId()==null){
			setOrgId((String)session.getAttribute(ORGID));
		}
		
		if (operation!=null && operation.equals("D")) {
			return deletePolicyRoster(strId);
		}
		if (operation!=null && operation.equals("E")) {
			loadValidatePolicyRoster(uF);
			return viewPolicyRoster(strId);
		}
		
		if(uF.parseToInt(getIsAlignLeave())==0){
			setIsAlignLeave("1");
		}
		
		if (getRosterpolicyHDId()!=null && getRosterpolicyHDId().length()>0) {
				return updatePolicyRoster();
		}
		if (getTimeValue()!=null && getTimeValue().length()>0) {
				return insertPolicyRoster();
		}
		loadValidatePolicyRoster(uF);
		
		return LOAD;
	}
	
	
	public String loadValidatePolicyRoster(UtilityFunctions uF) {
		request.setAttribute(PAGE, PPolicyRoster);
		request.setAttribute(TITLE, TAddRosterPolicy);
		
		in_out_List = new FillInOut().fillInOut();
		breakList = new FillBreakType(request).fillBreaks(uF.parseToInt(getOrgId()));
		setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		
		leaveTypeList = new FillLeaveType(request).fillLeaveWithoutCompensetary(uF.parseToInt(getOrgId()),false,uF.getCurrentDate(CF.getStrTimeZone()));
		
		return LOAD;
	}
	
	
	public String updatePolicyRoster() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement(updateBreakPolicy);
			pst = con.prepareStatement("UPDATE break_policy SET time_value=?,_mode=?,days=?, months=?,effective_date=?, entry_date=?, user_id=?, " +
					"break_type_id=?,is_align_leave=?,leave_type_id=? WHERE break_policy_id=?");
			pst.setDouble(1, uF.parseToDouble(getTimeValue()));			
			
			StringBuilder sb = new StringBuilder();
			for(int i=0; getIn_out()!=null && i<getIn_out().length; i++){
				sb.append(""+getIn_out()[i]+",");
			}
//			pst.setString(2, getIn_out());
			pst.setString(2, sb.toString());
			
			pst.setInt(3, uF.parseToInt(getDayValue()));
			pst.setInt(4, uF.parseToInt(getMonthValue()));
			pst.setDate(5, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(8, uF.parseToInt(getBreakType()));
			pst.setInt(9, uF.parseToInt(getIsAlignLeave())==0 ? 1 : uF.parseToInt(getIsAlignLeave()));
			pst.setInt(10, uF.parseToInt(getIsAlignLeave())<=1 ? 0 : uF.parseToInt(getStrLeaveType()));
			pst.setInt(11, uF.parseToInt(getRosterpolicyHDId()));			
			pst.execute();
			pst.close();
//			System.out.println("pst=====>"+pst);
			
			session.setAttribute(MESSAGE, SUCCESSM+"Policy updated successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	public String viewPolicyRoster(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectBreakPolicy);
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setRosterpolicyHDId(rs.getString("break_policy_id"));
				setTimeValue(rs.getString("time_value"));
				setDayValue(rs.getString("days"));
				setMonthValue(rs.getString("months"));
				setStrEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				setOrgId(rs.getString("org_id"));
				setStrWLocation(rs.getString("wlocation_id"));
				setBreakType(rs.getString("break_type_id"));
			
				String arr[] = null;
				if(rs.getString("_mode")!=null){
					arr = rs.getString("_mode").split(",");
				}
				setIn_out(arr);
				
				setIsAlignLeave(rs.getString("is_align_leave"));
				setStrLeaveType(rs.getString("leave_type_id"));
				
			}
			rs.close();
			pst.close();
			
			breakList = new FillBreakType(request).fillBreaks(uF.parseToInt(getOrgId()));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	public String insertPolicyRoster() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement(insertBreakPolicy);
			pst = con.prepareStatement("insert into break_policy (time_value,_mode,days, months,effective_date, user_id, entry_date, " +
					"org_id, wlocation_id, break_type_id,is_align_leave,leave_type_id) values (?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setDouble(1, uF.parseToDouble(getTimeValue()));
//			pst.setString(2, getIn_out());
			StringBuilder sb = new StringBuilder();
			for(int i=0; getIn_out()!=null && i<getIn_out().length; i++){
				sb.append(""+getIn_out()[i]+",");
			}
			pst.setString(2, sb.toString());
			
			pst.setInt(3, uF.parseToInt(getDayValue()));
			pst.setInt(4, uF.parseToInt(getMonthValue()));
			pst.setDate(5, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(8, uF.parseToInt(getOrgId()));
			pst.setInt(9, uF.parseToInt(getStrWLocation()));
			pst.setInt(10, uF.parseToInt(getBreakType()));
			pst.setInt(11, uF.parseToInt(getIsAlignLeave())==0 ? 1 : uF.parseToInt(getIsAlignLeave()));
			pst.setInt(12, uF.parseToInt(getIsAlignLeave())<=1 ? 0 : uF.parseToInt(getStrLeaveType()));
			pst.execute();
			pst.close();
//			System.out.println("pst=====>"+pst);
			session.setAttribute(MESSAGE, SUCCESSM+"Policy saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	
	public String deletePolicyRoster(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteBreakPolicy);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+"Policy deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getRosterpolicyHDId() {
		return rosterpolicyHDId;
	}

	public void setRosterpolicyHDId(String rosterpolicyHDId) {
		this.rosterpolicyHDId = rosterpolicyHDId;
	}

	public String getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(String timeValue) {
		this.timeValue = timeValue;
	}

	public String getDayValue() {
		return dayValue;
	}

	public void setDayValue(String dayValue) {
		this.dayValue = dayValue;
	}

	public String getMonthValue() {
		return monthValue;
	}

	public void setMonthValue(String monthValue) {
		this.monthValue = monthValue;
	}

	public String[] getIn_out() {
		return in_out;
	}

	public void setIn_out(String []in_out) {
		this.in_out = in_out;
	}

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}

	public List<FillInOut> getIn_out_List() {
		return in_out_List;
	}

	public void setIn_out_List(List<FillInOut> in_out_List) {
		this.in_out_List = in_out_List;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<FillBreakType> getBreakList() {
		return breakList;
	}

	public void setBreakList(List<FillBreakType> breakList) {
		this.breakList = breakList;
	}

	public String getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

	public String getBreakType() {
		return breakType;
	}

	public void setBreakType(String breakType) {
		this.breakType = breakType;
	}

	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}

	public String getStrLeaveType() {
		return strLeaveType;
	}

	public void setStrLeaveType(String strLeaveType) {
		this.strLeaveType = strLeaveType;
	}

	public String getIsAlignLeave() {
		return isAlignLeave;
	}

	public void setIsAlignLeave(String isAlignLeave) {
		this.isAlignLeave = isAlignLeave;
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
