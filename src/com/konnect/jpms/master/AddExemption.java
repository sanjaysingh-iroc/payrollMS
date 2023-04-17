package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillUnderSection;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddExemption extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	private String strUnderSection;
	private String strIsInvestmentForm;
	
	private String operation;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	private String fromPage;
	
	private String exemptionId;
	private String exemptionCode;
	private String exemptionName;
	private String exemptionDesc;
	private String exemptionToDate;
	private String exemptionFromDate;
	private String exemptionLimit;
	private String slabType;
	
	private String strFinancialYearStart;
	private String strFinancialYearEnd;
	
	private String financialYear;
	private List<FillSalaryHeads> salaryHeadList;
	private List<FillUnderSection> underSection10and16List;
	
	public String execute() throws Exception {
		
		session = request.getSession(); 
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		if(CF==null)return LOGIN;
		
		loadValidateExemption();
		
//		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
		underSection10and16List=new FillUnderSection().fillUnderSection10and16();
//		System.out.println("underSection10and16List==>"+underSection10and16List);
		if(fromPage != null && fromPage.equals("EA" )) {
			setFromPage(getFromPage().trim());
		}
		if(operation !=null && operation.equals("D")) {
			return deleteExemption();
		} else if(operation !=null && operation.equals("E")) {
			return viewExemption();
			
		} else if(operation !=null && operation.equals("U")) {
			return updateExemption();
		} else if(operation !=null && operation.trim().equals("A")) {
			return insertExemption();
		}
		
		if(operation == null || operation.equals("")) {
			setOperation("A");
		}
		
		if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
			String[] strPayCycleDates = getFinancialYear().split("-");
			setStrFinancialYearStart(strPayCycleDates[0]);
			setStrFinancialYearEnd(strPayCycleDates[1]);
		}
		
		/*System.out.println("financialYear==>"+financialYear);
		System.out.println("StrFinancialYearStart==>"+strFinancialYearStart);
		System.out.println("strFinancialYearEnd==>"+strFinancialYearEnd);*/
		return LOAD;
	}

	
	private String viewExemption() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from exemption_details WHERE exemption_id=?");
			pst.setInt(1, uF.parseToInt(getExemptionId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setExemptionCode(rs.getString("exemption_code"));
				setExemptionName(rs.getString("exemption_name"));
				setExemptionDesc(rs.getString("exemption_description"));
				setExemptionLimit(rs.getString("exemption_limit"));
				setStrUnderSection(rs.getString("under_section"));
				setStrIsInvestmentForm(rs.getString("investment_form"));
				setSlabType(rs.getString("slab_type"));
				setOperation("U");
				
				request.setAttribute("SALARY_HEAD_ID",rs.getString("salary_head_id"));
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
		return LOAD;

	}


	public void loadValidateExemption() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		List<String> alSalaryHeads = new ArrayList();	
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(salary_head_id), salary_head_name, earning_deduction from salary_details where salary_head_id not in ("+GROSS+","+CTC+") order by earning_deduction, salary_head_name");
			rs = pst.executeQuery();
//			System.out.println("load pst==>"+pst);
			salaryHeadList = new ArrayList<FillSalaryHeads>();
			salaryHeadList.add(new FillSalaryHeads("0", "Gross"));
			while(rs.next()) {
				alSalaryHeads.add(rs.getString("salary_head_name"));
				salaryHeadList.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
			}
			rs.close();
			pst.close();
			request.setAttribute("alSalaryHeads", alSalaryHeads);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public String insertExemption() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement(insertExemption);
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
			pst.setInt(1, uF.parseToInt(getExemptionName()));
			rs = pst.executeQuery();
			String strSalaryHeadName = null;
			while(rs.next()){
				strSalaryHeadName = rs.getString("salary_head_name");
			}
			rs.close();
			pst.close();
			if(uF.parseToInt(getExemptionName()) == 0) {
				strSalaryHeadName = "Gross";
			}
			
			pst = con.prepareStatement("INSERT INTO exemption_details (exemption_code, exemption_name, exemption_description, exemption_from, exemption_to, " +
					"exemption_limit, entry_date, user_id,salary_head_id,under_section,investment_form,slab_type) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setString(1, getExemptionCode());
//			pst.setString(2, uF.showData(hmSalaryHeadsMap.get(getExemptionName()), "") );
			pst.setString(2, uF.showData(strSalaryHeadName, "") );
			pst.setString(3, getExemptionDesc());
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDouble(6, uF.parseToDouble(getExemptionLimit()));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(9, uF.parseToInt(getExemptionName()));
			pst.setInt(10, uF.parseToInt(getStrUnderSection()));
			pst.setBoolean(11, uF.parseToBoolean(getStrIsInvestmentForm()));
			pst.setInt(12, uF.parseToInt(getSlabType()));
//			System.out.println("pst==>"+pst);
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		if(getFromPage() != null && getFromPage().equals("ER")) {
			return VIEW;
		}else {
			return SUCCESS;
		}
	}
	
	public String updateExemption() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//				Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
			pst.setInt(1, uF.parseToInt(getExemptionName()));
			rs = pst.executeQuery();
			String strSalaryHeadName = null;
			while(rs.next()){
				strSalaryHeadName = rs.getString("salary_head_name");
			}
			rs.close();
			pst.close();
			if(uF.parseToInt(getExemptionName()) == 0) {
				strSalaryHeadName = "Gross";
			}
			
			pst = con.prepareStatement("UPDATE exemption_details SET exemption_code=?, exemption_name=?, exemption_description=?, exemption_limit=?, " +
				"under_section=?, investment_form=?,salary_head_id=?, entry_date=?, user_id=?, slab_type=? WHERE exemption_id=?");
			pst.setString(1, getExemptionCode());
//				pst.setString(2, uF.showData(hmSalaryHeadsMap.get(getExemptionName()), "") );
			pst.setString(2, uF.showData(strSalaryHeadName, "") );
			pst.setString(3, getExemptionDesc());
			pst.setDouble(4, uF.parseToDouble(getExemptionLimit()));
			pst.setInt(5, uF.parseToInt(getStrUnderSection()));
			pst.setBoolean(6, uF.parseToBoolean(getStrIsInvestmentForm()));
			pst.setInt(7, uF.parseToInt(getExemptionName()));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(10, uF.parseToInt(getSlabType()));
			pst.setInt(11, uF.parseToInt(getExemptionId()));
			pst.execute(); 
			pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		if(getFromPage() != null && getFromPage().equals("ER")) {
			return VIEW;
		}else {
			return SUCCESS;
		}

	}
	
	public String deleteExemption() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteExemption);
			pst.setInt(1, uF.parseToInt(getExemptionId()));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		if(getFromPage() != null && getFromPage().equals("ER")) {
			return VIEW;
		}else {
			return SUCCESS;
		}

	}

		
	/*public void validate() {

		if (getExemptionId() != null && getExemptionId().length() == 0) {
			addFieldError("exemptionId", "Exemption ID is required");
		}
		if (getExemptionName() != null && getExemptionName().length() == 0) {
			addFieldError("password", "Exemption Name is required");
		}
		if (getExemptionCode() != null && getExemptionCode().length() == 0) {
			addFieldError("exemptionCode", "Exemption Code is required");
		}
		loadValidateExemption();

	}*/

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getExemptionId() {
		return exemptionId;
	}

	public void setExemptionId(String exemptionId) {
		this.exemptionId = exemptionId;
	}

	public String getExemptionName() {
		return exemptionName;
	}

	public void setExemptionName(String exemptionName) {
		this.exemptionName = exemptionName;
	}

	public String getExemptionDesc() {
		return exemptionDesc;
	}

	public void setExemptionDesc(String exemptionDesc) {
		this.exemptionDesc = exemptionDesc;
	}

	public String getExemptionCode() {
		return exemptionCode;
	}

	public void setExemptionCode(String exemptionCode) {
		this.exemptionCode = exemptionCode;
	}

	public String getExemptionLimit() {
		return exemptionLimit;
	}

	public void setExemptionLimit(String exemptionLimit) {
		this.exemptionLimit = exemptionLimit;
	}

	public String getExemptionToDate() {
		return exemptionToDate;
	}

	public void setExemptionToDate(String exemptionToDate) {
		this.exemptionToDate = exemptionToDate;
	}

	public String getExemptionFromDate() {
		return exemptionFromDate;
	}

	public void setExemptionFromDate(String exemptionFromDate) {
		this.exemptionFromDate = exemptionFromDate;
	}

	public String getStrFinancialYearStart() {
		return strFinancialYearStart;
	}

	public void setStrFinancialYearStart(String strFinancialYearStart) {
		this.strFinancialYearStart = strFinancialYearStart;
	}

	public String getStrFinancialYearEnd() {
		return strFinancialYearEnd;
	}

	public void setStrFinancialYearEnd(String strFinancialYearEnd) {
		this.strFinancialYearEnd = strFinancialYearEnd;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public List<FillUnderSection> getUnderSection10and16List() {
		return underSection10and16List;
	}

	public void setUnderSection10and16List(List<FillUnderSection> underSection10and16List) {
		this.underSection10and16List = underSection10and16List;
	}

	public String getStrUnderSection() {
		return strUnderSection;
	}

	public void setStrUnderSection(String strUnderSection) {
		this.strUnderSection = strUnderSection;
	}

	public String getStrIsInvestmentForm() {
		return strIsInvestmentForm;
	}

	public void setStrIsInvestmentForm(String strIsInvestmentForm) {
		this.strIsInvestmentForm = strIsInvestmentForm;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	public String getFinancialYear() {
		return financialYear;
	}


	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}


	public String getSlabType() {
		return slabType;
	}


	public void setSlabType(String slabType) {
		this.slabType = slabType;
	}

}