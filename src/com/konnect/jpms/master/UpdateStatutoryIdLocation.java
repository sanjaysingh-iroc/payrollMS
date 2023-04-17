package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateStatutoryIdLocation  extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	CommonFunctions CF;
	
	String strWLocationId;
	String strWLocationName;
	String citAddress;
	String ptRCEC;
	List<FillFinancialYears> financialYearList;
	List<FillEmployee> empList;
	String strSessionOrgId;
	String strEmpId;
	String f_strFinancialYear;
	
	String userscreen;
	String navigationId;
	String toPage;
	String fromPage;
	
	

	public String execute() throws Exception {

		session = request.getSession();
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		UtilityFunctions uF = new UtilityFunctions();
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		empList = new FillEmployee(request).fillEmployeeName(null, null, uF.parseToInt(strSessionOrgId),0,session);
	
		if (CF == null)
			return "login";
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
    	
		if (operation!=null && operation.equals("E")) {
			return viewOrganisation(strId);
		}
		System.out.println("getStrWLocationId==>"+getStrWLocationId());
		if (getStrWLocationId()!=null && getStrWLocationId().length()>0) { 
			
			return updateOrganisation();
		}
		
		return LOAD;
	}
	
	public String updateOrganisation() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE work_location_info SET wlocation_cit_address = ?,wlocation_pt_rcec=? where wlocation_id = ?");
			pst.setString(1, getCitAddress());
			pst.setString(2, getPtRCEC());
			pst.setInt(3, uF.parseToInt(getStrWLocationId()));			
			int x = pst.executeUpdate();
			pst.close();
			
			String[] parts = getF_strFinancialYear().split("-");
			pst = con.prepareStatement("update authorised_details set emp_id=? where wlocation_id=? and financial_year_start=? and financial_year_end=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrWLocationId()));
			pst.setDate(3, uF.getDateFormat(parts[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(parts[1], DATE_FORMAT));
			int y = pst.executeUpdate();
			pst.close();
			
			if(y == 0){
				pst = con.prepareStatement("insert into authorised_details(wlocation_id,financial_year_start,financial_year_end,emp_id)values(?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getStrWLocationId()));
				pst.setDate(2, uF.getDateFormat(parts[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(parts[1], DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(getStrEmpId()));
				pst.execute();
				pst.close();
			}
			
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+getStrWLocationName()+"'s Statutory Id's & Registration Information updated successfully."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+getStrWLocationName()+"'s Statutory Id's & Registration Information updated failed."+END);
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+getStrWLocationName()+"'s Statutory Id's & Registration Information updated failed."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewOrganisation(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from work_location_info where wlocation_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrWLocationId(uF.showData(rs.getString("wlocation_id"), ""));
				setStrWLocationName(uF.showData(rs.getString("wlocation_name"), ""));
				setCitAddress(uF.showData(rs.getString("wlocation_cit_address"), ""));
				setPtRCEC(uF.showData(rs.getString("wlocation_pt_rcec"), ""));
			}
			rs.close();
			pst.close();
			
			List<Map<String, String>> hmAuthorisedDetails = new ArrayList<Map<String,String>>();
			pst = con.prepareStatement("select * from authorised_details where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()) {
				String strFinancialYearFrom = uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT);
				String strFinancialYearTo = uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT);
				setStrEmpId(rs.getString("emp_id"));
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("FINANCIAL_YEAR", strFinancialYearFrom + "-" + strFinancialYearTo);
				hmInner.put("EMP_NAME", ""+CF.getEmpNameMapByEmpId(con, rs.getString("emp_id")));
			
				hmAuthorisedDetails.add(hmInner);
			}
			
			rs.close();
			pst.close();
			request.setAttribute("hmAuthorisedDetails", hmAuthorisedDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrWLocationId() {
		return strWLocationId;
	}

	public void setStrWLocationId(String strWLocationId) {
		this.strWLocationId = strWLocationId;
	}

	public String getStrWLocationName() {
		return strWLocationName;
	}

	public void setStrWLocationName(String strWLocationName) {
		this.strWLocationName = strWLocationName;
	}

	public String getCitAddress() {
		return citAddress;
	}

	public void setCitAddress(String citAddress) {
		this.citAddress = citAddress;
	}

	public String getPtRCEC() {
		return ptRCEC;
	}

	public void setPtRCEC(String ptRCEC) {
		this.ptRCEC = ptRCEC;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getF_strFinancialYear() {
		return f_strFinancialYear;
	}

	public void setF_strFinancialYear(String f_strFinancialYear) {
		this.f_strFinancialYear = f_strFinancialYear;
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
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
}
