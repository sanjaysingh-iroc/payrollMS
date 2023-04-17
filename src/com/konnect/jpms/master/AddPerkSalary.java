package com.konnect.jpms.master;

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

public class AddPerkSalary extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	
	private String orgId;
	private String levelId;
	private String salaryHeadId;
	private String financialYear;
	
	private String perkSalaryId;
	private String perkCode;
	private String perkName;
	private String perkDesc;
	private String perkAmount;
	private boolean attachment;
	private boolean strIsOptimal; 
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		if (operation!=null && operation.equals("D")) {
			return deletePerkSalary(uF,strId);
		}
		
		if (operation!=null && operation.equals("E")) { 
			return viewPerkSalary(uF,strId);
		}
		
		if (getPerkSalaryId()!=null && getPerkSalaryId().length()>0) { 
			return updatePerkSalary(uF);
		}
		
		if(getPerkCode()!=null && getPerkCode().length()>0){
			return insertPerkSalary(uF);
		}
		return LOAD;
	}

	public String insertPerkSalary(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if (getFinancialYear() != null) {
				String[] strFinancialYear = getFinancialYear().split("-");
				String strFinancialYearStart = strFinancialYear[0];
				String strFinancialYearEnd = strFinancialYear[1];
			
				pst = con.prepareStatement("INSERT INTO perk_salary_details (perk_code,perk_name,perk_description,amount,entry_date,user_id," +
						"salary_head_id,level_id,org_id,financial_year_start,financial_year_end,is_attachment,is_optimal) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setString(1, getPerkCode());
				pst.setString(2, getPerkName());
				pst.setString(3, uF.showData(getPerkDesc(),""));
				pst.setInt(4, uF.parseToInt(getPerkAmount()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setInt(7, uF.parseToInt(getSalaryHeadId()));
				pst.setInt(8, uF.parseToInt(getLevelId()));				
				pst.setInt(9, uF.parseToInt(getOrgId()));
				pst.setDate(10, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(11, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setBoolean(12, getAttachment());
				pst.setBoolean(13, getStrIsOptimal());
				int x = pst.executeUpdate();
				pst.close();
				if(x > 0){
					session.setAttribute(MESSAGE, SUCCESSM+"Perk saved successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Perk not saved. Please try again!"+END);
				}
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Perk not saved. Please try again!"+END);
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String viewPerkSalary(UtilityFunctions uF, String strId) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from perk_salary_details where perk_salary_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setPerkSalaryId(rs.getString("perk_salary_id"));
				setPerkCode(rs.getString("perk_code"));
				setPerkDesc(rs.getString("perk_description"));
				setPerkName(rs.getString("perk_name"));
				setPerkAmount(rs.getString("amount"));
				setAttachment(uF.parseToBoolean(rs.getString("is_attachment")));
				setStrIsOptimal(uF.parseToBoolean(rs.getString("is_optimal")));
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
	
	public String updatePerkSalary(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			if (getFinancialYear() != null) {
				String[] strFinancialYear = getFinancialYear().split("-");
				String strFinancialYearStart = strFinancialYear[0];
				String strFinancialYearEnd = strFinancialYear[1];
			
				pst = con.prepareStatement("update perk_salary_details set perk_code=?,perk_name=?,perk_description=?,amount=?,entry_date=?," +
						"user_id=?,is_attachment=?,is_optimal=? where salary_head_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? and perk_salary_id=?");
				pst.setString(1, getPerkCode());
				pst.setString(2, getPerkName());
				pst.setString(3, uF.showData(getPerkDesc(),""));
				pst.setInt(4, uF.parseToInt(getPerkAmount()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setBoolean(7, getAttachment());
				pst.setBoolean(8, getStrIsOptimal());
				pst.setInt(9, uF.parseToInt(getSalaryHeadId()));
				pst.setInt(10, uF.parseToInt(getLevelId()));				
				pst.setInt(11, uF.parseToInt(getOrgId()));
				pst.setDate(12, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(13, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(14, uF.parseToInt(getPerkSalaryId()));
				int x = pst.executeUpdate();
				pst.close();
				if(x > 0){
					session.setAttribute(MESSAGE, SUCCESSM+"Perk updated successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Perk not updated. Please try again!"+END);
				}
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Perk not updated. Please try again!"+END);
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deletePerkSalary(UtilityFunctions uF, String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from perk_salary_details where perk_salary_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Perk deleted successfully."+END);
			
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

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getPerkSalaryId() {
		return perkSalaryId;
	}

	public void setPerkSalaryId(String perkSalaryId) {
		this.perkSalaryId = perkSalaryId;
	}

	public String getPerkCode() {
		return perkCode;
	}

	public void setPerkCode(String perkCode) {
		this.perkCode = perkCode;
	}

	public String getPerkName() {
		return perkName;
	}

	public void setPerkName(String perkName) {
		this.perkName = perkName;
	}

	public String getPerkDesc() {
		return perkDesc;
	}

	public void setPerkDesc(String perkDesc) {
		this.perkDesc = perkDesc;
	}

	public String getPerkAmount() {
		return perkAmount;
	}

	public void setPerkAmount(String perkAmount) {
		this.perkAmount = perkAmount;
	}

	public boolean getAttachment() {
		return attachment;
	}

	public void setAttachment(boolean attachment) {
		this.attachment = attachment;
	}

	public boolean getStrIsOptimal() {
		return strIsOptimal;
	}

	public void setStrIsOptimal(boolean strIsOptimal) {
		this.strIsOptimal = strIsOptimal;
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