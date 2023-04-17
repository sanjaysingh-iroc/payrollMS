package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RosterPolicyMinHrsHD_FD extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;   
	
	String rosterpolicyHDFDId;
	String minHrs;
	String exceptionType;
	String orgId;
	String strWlocation;
	
	String strEffectiveDate; 
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception { 
		
		request.setAttribute(PAGE, "/jsp/policies/RosterPolicyMinHrsHD_FD.jsp");
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
		
		if (getRosterpolicyHDFDId()!=null && getRosterpolicyHDFDId().length()>0) {
				return updatePolicyRoster();
		}
		if (getMinHrs()!=null && getMinHrs().length()>0) {
				return insertPolicyRoster();
		}
		loadValidatePolicyRoster();
		
		return LOAD;
	}
	
	
	public String loadValidatePolicyRoster() {
		UtilityFunctions uF = new UtilityFunctions();
		
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
		
		String updateRosterPolicy = "UPDATE roster_halfday_fullday_hrs_policy SET exception_type=?,min_hrs=?,effective_date=?,update_date=?,updated_by=? WHERE roster_halfday_fullday_hrs_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateRosterPolicy);
			pst.setString(1, getExceptionType());
			pst.setDouble(2, uF.parseToDouble(getMinHrs()));
			pst.setDate(3, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
			pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(6, uF.parseToInt(getRosterpolicyHDFDId()));
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
			pst = con.prepareStatement("select * from roster_halfday_fullday_hrs_policy where roster_halfday_fullday_hrs_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setRosterpolicyHDFDId(rs.getString("roster_halfday_fullday_hrs_id"));
				setExceptionType(rs.getString("exception_type"));
				setMinHrs(rs.getString("min_hrs"));
				setStrEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
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
			pst = con.prepareStatement("insert into roster_halfday_fullday_hrs_policy (exception_type,min_hrs,effective_date,org_id,wlocation_id,added_by,entry_date) " +
					"values (?,?,?,?, ?,?,?)");
			pst.setString(1, getExceptionType());
			pst.setDouble(2, uF.parseToDouble(getMinHrs()));
			pst.setDate(3, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getOrgId()));
			pst.setInt(5, uF.parseToInt(getStrWlocation()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//			System.out.println("pst======>"+pst);
			pst.executeUpdate();
			pst.close();
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
			pst = con.prepareStatement("delete from roster_halfday_fullday_hrs_policy where roster_halfday_fullday_hrs_id=?");
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

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
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

	public String getRosterpolicyHDFDId() {
		return rosterpolicyHDFDId;
	}

	public void setRosterpolicyHDFDId(String rosterpolicyHDFDId) {
		this.rosterpolicyHDFDId = rosterpolicyHDFDId;
	}

	public String getMinHrs() {
		return minHrs;
	}

	public void setMinHrs(String minHrs) {
		this.minHrs = minHrs;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
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
