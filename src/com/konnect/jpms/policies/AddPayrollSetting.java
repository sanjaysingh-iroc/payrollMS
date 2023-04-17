package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillSalaryCalculationTypes;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddPayrollSetting extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF;
	HttpSession session;
	
	String orgId;
	String startPaycycle;
	String displayPaycycle;
	String strPaycycleDuration;
	String strSalaryCalculation;
	String strFixDays;
	
	List<FillPayCycleDuration> paycycleDurationList;
	List<FillSalaryCalculationTypes> salaryCalculationList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");

		loadPayrollSetting();
		
		if (operation!=null && operation.equals("E")) {
			return viewPayrollSetting(strId);
		}
		
		if (getOrgId()!=null && getOrgId().length()>0) {
				return updatePayrollSetting();
		}
		
		return LOAD;
		
	}
	public String updatePayrollSetting() {

		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update org_details set start_paycycle=?,display_paycycle=?,duration_paycycle=?,salary_cal_basis=?,salary_fix_days=? where org_id=?");
			pst.setDate(1, uF.getDateFormat(getStartPaycycle(), DATE_FORMAT));
			pst.setString(2, getDisplayPaycycle());
			pst.setString(3, getStrPaycycleDuration());
			pst.setString(4, getStrSalaryCalculation());
			pst.setInt(5, (getStrSalaryCalculation()!=null && getStrSalaryCalculation().equals("AFD")) ? uF.parseToInt(getStrFixDays()) : 0);
			pst.setInt(6, uF.parseToInt(getOrgId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	public String viewPayrollSetting(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setOrgId(rs.getString("org_id"));
				String strDate = uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT).equals("-") ? uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT): uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT);
				setStartPaycycle(strDate);
				setDisplayPaycycle(uF.showData(rs.getString("display_paycycle"), "1-100"));
				setStrPaycycleDuration(uF.showData(rs.getString("duration_paycycle"), "M"));
				setStrSalaryCalculation(rs.getString("salary_cal_basis"));
				setStrFixDays(rs.getString("salary_fix_days"));
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
	
	public String loadPayrollSetting() {
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		salaryCalculationList = new FillSalaryCalculationTypes().fillSalaryCalculationTypes();
		
		return LOAD;
	}
	

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getOrgId() {
		return orgId;
	}
	
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public String getStartPaycycle() {
		return startPaycycle;
	}
	
	public void setStartPaycycle(String startPaycycle) {
		this.startPaycycle = startPaycycle;
	}
	
	public String getDisplayPaycycle() {
		return displayPaycycle;
	}
	
	public void setDisplayPaycycle(String displayPaycycle) {
		this.displayPaycycle = displayPaycycle;
	}
	
	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}
	
	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}
	
	public String getStrSalaryCalculation() {
		return strSalaryCalculation;
	}
	
	public void setStrSalaryCalculation(String strSalaryCalculation) {
		this.strSalaryCalculation = strSalaryCalculation;
	}
	
	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}
	
	public void setPaycycleDurationList(List<FillPayCycleDuration> paycycleDurationList) {
		this.paycycleDurationList = paycycleDurationList;
	}
	
	public List<FillSalaryCalculationTypes> getSalaryCalculationList() {
		return salaryCalculationList;
	}
	
	public void setSalaryCalculationList(List<FillSalaryCalculationTypes> salaryCalculationList) {
		this.salaryCalculationList = salaryCalculationList;
	}
	
	public String getStrFixDays() {
		return strFixDays;
	}
	
	public void setStrFixDays(String strFixDays) {
		this.strFixDays = strFixDays;
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
