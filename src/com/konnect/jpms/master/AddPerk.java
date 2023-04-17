package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillPerkPaymentCycle;
import com.konnect.jpms.select.FillPerkType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddPerk extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	String orgId;
	String financialYear;
	
	String perkId;
	String perkCode;
	String perkName;
	String perkDesc;
	String perkType;
	String perkPaymentCycle;
	String perklevel;
	String perkMaxAmount;
	
	List<FillPerkType> perkTypeList;
	List<FillPerkPaymentCycle> perkPaymentCycleList;

	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		 
		loadValidatePerk();
		
		if (operation!=null && operation.equals("D")) {
			return deletePerk(strId);
		}
		if (operation!=null && operation.equals("E")) { 
			return viewPerk(strId);
		}
		if (getPerkId()!=null && getPerkId().length()>0) { 
			return updatePerk();
		}
		
		
		if(getPerkCode()!=null && getPerkCode().length()>0){
			return insertPerk();
		}
		return LOAD;
		
	}

	public String loadValidatePerk() {
		perkTypeList = new FillPerkType(request).fillPerkType();
		perkPaymentCycleList = new FillPerkPaymentCycle().fillPerkPaymentCycle();
		return LOAD;
	}

	public String insertPerk() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement(insertPerk);
			if (getFinancialYear() != null) {
				String[] strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
				pst = con.prepareStatement("INSERT INTO perk_details (perk_code, perk_name, perk_description, perk_type, perk_payment_cycle, level_id, max_amount, entry_date, user_id, org_id, financial_year_start, financial_year_end) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setString(1, getPerkCode());
				pst.setString(2, getPerkName());
				pst.setString(3, uF.showData(getPerkDesc(),""));
				pst.setString(4, uF.showData(getPerkType(), ""));
				pst.setString(5, uF.showData(getPerkPaymentCycle(), ""));
				pst.setInt(6, uF.parseToInt(getPerklevel()));
				pst.setDouble(7, uF.parseToDouble(getPerkMaxAmount()));
				pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(9, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setInt(10, uF.parseToInt(getOrgId()));
				pst.setDate(11, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(12, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.execute();	
				pst.close();
				
				session.setAttribute(MESSAGE, SUCCESSM+"Perk saved successfully."+END);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String viewPerk(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from perk_details where perk_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setPerkId(rs.getString("perk_id"));
				setPerkCode(rs.getString("perk_code"));
				setPerkDesc(rs.getString("perk_description"));
				setPerkName(rs.getString("perk_name"));
				setPerkType(rs.getString("perk_type"));
				setPerklevel(rs.getString("level_id"));
				setPerkPaymentCycle(rs.getString("perk_payment_cycle"));
				setPerkMaxAmount(rs.getString("max_amount"));
				setOrgId(rs.getString("org_id")); 
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
	
	public String updatePerk() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String updatePerk = "UPDATE perk_details SET perk_code=?,perk_name=?, perk_description=?, perk_type=?, perk_payment_cycle=?,level_id=?, max_amount=?, entry_date=?, user_id=?  WHERE perk_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updatePerk);

			pst.setString(1, getPerkCode());
			pst.setString(2, getPerkName());
			pst.setString(3, uF.showData(getPerkDesc(),""));
			pst.setString(4, uF.showData(getPerkType(), ""));
			pst.setString(5, uF.showData(getPerkPaymentCycle(), ""));
			pst.setInt(6, uF.parseToInt(getPerklevel()));
			pst.setDouble(7, uF.parseToDouble(getPerkMaxAmount()));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(10, uF.parseToInt(getPerkId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Perk updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deletePerk(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deletePerk);
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

	public String getPerkId() {
		return perkId;
	}

	public void setPerkId(String perkId) {
		this.perkId = perkId;
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

	public String getPerkCode() {
		return perkCode;
	}

	public void setPerkCode(String perkCode) {
		this.perkCode = perkCode;
	}

	public String getPerklevel() {
		return perklevel;
	}

	public void setPerklevel(String perklevel) {
		this.perklevel = perklevel;
	}

	public String getPerkType() {
		return perkType;
	}

	public void setPerkType(String perkType) {
		this.perkType = perkType;
	}

	public String getPerkPaymentCycle() {
		return perkPaymentCycle;
	}

	public void setPerkPaymentCycle(String perkPaymentCycle) {
		this.perkPaymentCycle = perkPaymentCycle;
	}

	public List<FillPerkType> getPerkTypeList() {
		return perkTypeList;
	}

	public void setPerkTypeList(List<FillPerkType> perkTypeList) {
		this.perkTypeList = perkTypeList;
	}

	public List<FillPerkPaymentCycle> getPerkPaymentCycleList() {
		return perkPaymentCycleList;
	}

	public void setPerkPaymentCycleList(
			List<FillPerkPaymentCycle> perkPaymentCycleList) {
		this.perkPaymentCycleList = perkPaymentCycleList;
	}

	public String getPerkMaxAmount() {
		return perkMaxAmount;
	}

	public void setPerkMaxAmount(String perkMaxAmount) {
		this.perkMaxAmount = perkMaxAmount;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
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