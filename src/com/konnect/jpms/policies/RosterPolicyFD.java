package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillInOut;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RosterPolicyFD extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	String rosterpolicyFDId;
	String timeValue;
	String dayValue;
	String monthValue;
	String in_out;
	String orgId;
	
	String strEffectiveDate; 
	
	List<FillInOut> in_out_List;  
	
	CommonFunctions CF = null;   
	
	String strWlocation;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception { 
		
		request.setAttribute(PAGE, "/jsp/policies/RosterPolicyFD.jsp");
		session = request.getSession();

		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		
		
		
		if (operation!=null && operation.equals("D")) {
			return deletePolicyRoster(strId);
		}
		if (operation!=null && operation.equals("E")) {
			loadValidatePolicyRoster();
			return viewPolicyRoster(strId);
		}
		
		if (getRosterpolicyFDId()!=null && getRosterpolicyFDId().length()>0) {
				return updatePolicyRoster();
		}
		if (getTimeValue()!=null && getTimeValue().length()>0) {
				return insertPolicyRoster();
		}
		loadValidatePolicyRoster();
		
		return LOAD;
	}
	
	
	public String loadValidatePolicyRoster() {
		request.setAttribute(PAGE, "/jsp/policies/RosterPolicyFD.jsp");
		request.setAttribute(TITLE, "Add Roster Full Day Policy");
		UtilityFunctions uF = new UtilityFunctions();
		
		in_out_List = new FillInOut().fillInOut();
		
		if(getStrEffectiveDate() == null) {
			setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		}
		return LOAD;
	}
	
	
	public String updatePolicyRoster() {
		Connection con = null;
		PreparedStatement pst = null; 
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String updateRosterPolicy = "UPDATE roster_fullday_policy SET time_value=?,_mode=?,days=?, months=?,effective_date=?, entry_date=?, user_id=? WHERE roster_full_policy_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateRosterPolicy);
//			pst.setDouble(1, uF.parseToDouble(getTimeValue()));
			pst.setDouble(1, ((getIn_out()!=null && getIn_out().equalsIgnoreCase("IN"))?uF.parseToDouble(getTimeValue()):-uF.parseToDouble(getTimeValue())));
			pst.setString(2, getIn_out());
			pst.setInt(3, uF.parseToInt(getDayValue()));
			pst.setInt(4, uF.parseToInt(getMonthValue()));
			pst.setDate(5, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(8, uF.parseToInt(getRosterpolicyFDId()));
			pst.execute();
			pst.close();
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
			pst = con.prepareStatement("select * from roster_fullday_policy where roster_full_policy_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setRosterpolicyFDId(rs.getString("roster_full_policy_id"));
				setTimeValue(rs.getString("time_value"));
				setDayValue(rs.getString("days"));
				setMonthValue(rs.getString("months"));
				setStrEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				setOrgId(rs.getString("org_id"));
				setIn_out(rs.getString("_mode"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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
			pst = con.prepareStatement("insert into roster_fullday_policy (time_value,_mode,days, months,effective_date, user_id, entry_date, org_id,wlocation_id) " +
					"values (?,?,?,?,?,?,?,?,?)");
			pst.setDouble(1, ((getIn_out()!=null && getIn_out().equalsIgnoreCase("IN"))?uF.parseToDouble(getTimeValue()):-uF.parseToDouble(getTimeValue())));
			pst.setString(2, getIn_out());
			pst.setInt(3, uF.parseToInt(getDayValue()));
			pst.setInt(4, uF.parseToInt(getMonthValue()));
			pst.setDate(5, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(8, uF.parseToInt(getOrgId()));
			pst.setInt(9, uF.parseToInt(getStrWlocation()));
//			System.out.println("pst======>"+pst);
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM+"Policy saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
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
			pst = con.prepareStatement("delete from roster_fullday_policy where roster_full_policy_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM+"Policy deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
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

	public String getRosterpolicyFDId() {
		return rosterpolicyFDId;
	}

	public void setRosterpolicyFDId(String rosterpolicyFDId) {
		this.rosterpolicyFDId = rosterpolicyFDId;
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

	public String getIn_out() {
		return in_out;
	}

	public void setIn_out(String in_out) {
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

	public String getStrWlocation() {
		return strWlocation;
	}

	public void setStrWlocation(String strWlocation) {
		this.strWlocation = strWlocation;
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
