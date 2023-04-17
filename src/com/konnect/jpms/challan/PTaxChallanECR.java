package com.konnect.jpms.challan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class PTaxChallanECR implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String financialYear;
	List<FillMonth> monthList;
	String strFinancialYearStart;
	String strFinancialYearEnd;
	double paidamount;
	double totalamount;
	String sessionEmpId;

	public void execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return;
		sessionEmpId = (String) session.getAttribute(EMPID);
		monthList = new FillMonth().fillMonth();

		viewForm5PTChallanData();
		generatePTaxChallanECR();
		
		return; 
	}

	String challanDate;
	String operation;
	String challanNum;

	public void viewForm5PTChallanData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		int year = 0;
		String payMonts = "";
		String emp = "";
		int totalamount = 0;
		Map<Integer, Map<String, String>> hmMap = new HashMap<Integer, Map<String, String>>();
		int payYear = 0;
		String periodFrom = "";
		String periodTo = "";
		try {
			if (getFinancialYear() != null) {

				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			} else {

				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);

				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			}

			con = db.makeConnection(con);

			pst = con.prepareStatement("select income_from,income_to,deduction_paycycle from "
					+ "deduction_details_india where financial_year_from=? and financial_year_to=? order by deduction_paycycle");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst= 1 =>"+pst);
			rs = pst.executeQuery();
			int i = 0;
			List<Integer> alList = new ArrayList<Integer>();
			while (rs.next()) {
				alList.add(i);
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("AMOUNT_RANGE", uF.formatIntoComma(rs.getDouble("income_from")) + " to " + uF.formatIntoComma(rs.getDouble("income_to")));
				hmInner.put("TAXDEDUCTION", rs.getString("deduction_paycycle"));
				hmMap.put(i, hmInner);
				i++;
			}
	        rs.close();
	        pst.close();
	        
			if (getOperation() != null && getOperation().equalsIgnoreCase("pdf") && getChallanNum() != null) {
				pst = con.prepareStatement("select count(emp_id) as emp_id,amount from challan_details where challan_no=? group by amount");
				pst.setString(1, getChallanNum());
			} else if (getOperation() != null && getOperation().equalsIgnoreCase("pdf")) {

				pst = con.prepareStatement("select * from challan_details where entry_date = ? and challan_type=? and is_paid=?");
				pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setInt(2, PROFESSIONAL_TAX);
				pst.setBoolean(3, false);
//				System.out.println("pst= 2 =>"+pst);
				rs = pst.executeQuery();
				StringBuilder sbMonths = new StringBuilder();
				while (rs.next()) {

					String[] arr = rs.getString("month").split(",");

					for (i = 0; arr != null && i < arr.length; i++) {
						if (arr[i] != null && arr[i].length() > 0) {
							sbMonths.append(arr[i] + ",");
						}

					}
				}
		        rs.close();
		        pst.close();

				sbMonths.replace(0, sbMonths.length(), sbMonths.substring(0, sbMonths.length() - 1));

				pst = con
						.prepareStatement("select count(emp_id),amount from payroll_generation where"
								+ " month in ("
								+ sbMonths.toString()
								+ ") and financial_year_from_date=? and"
								+ " financial_year_to_date=? and salary_head_id=? and emp_id in (select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=? ) group by amount order by amount");

				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, PROFESSIONAL_TAX);
				pst.setDate(4, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setInt(5, PROFESSIONAL_TAX);
				pst.setBoolean(6, false);

			}

			/*
			 * else { for(int j=0;j<getEmpIds().length;j++){
			 * emp+=getEmpIds()[j]+","; } if(emp.contains(",")){
			 * emp=emp.substring(0, emp.length()-1); }
			 * 
			 * pst = con.prepareStatement(
			 * "select count(emp_id),amount from payroll_generation where" +
			 * " month in ("+totalMonths+") and financial_year_from_date=? and"
			 * +
			 * " financial_year_to_date=? and salary_head_id=? and emp_id in ("
			 * +emp+") group by amount order by amount");
			 * 
			 * pst.setDate(1, uF.getDateFormat(strFinancialYearStart,
			 * DATE_FORMAT)); pst.setDate(2,
			 * uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			 * pst.setInt(3,PROFESSIONAL_TAX); }
			 */

//			System.out.println("pst= 3 =>"+pst);

			rs = pst.executeQuery();
			Map<String, String> hmempcnt = new HashMap<String, String>();
			while (rs.next()) {
				int empcnt = rs.getInt(1);
				int amnt = rs.getInt(2);
				int total = empcnt * amnt;
				hmempcnt.put(amnt + "TOTAL", total + "");
				hmempcnt.put(amnt + "", empcnt + "");

			}
	        rs.close();
	        pst.close();

			if (getChallanNum() != null) {
				pst = con.prepareStatement("select entry_date from challan_details where challan_no=?");
				pst.setString(1, getChallanNum());
				rs = pst.executeQuery();
				while (rs.next()) {
					setChallanDate(rs.getString("entry_date"));
				}
		        rs.close();
		        pst.close();
			}

			
			
//			System.out.println("hmempcnt=="+hmempcnt);
//			System.out.println("alList=="+alList);
//			System.out.println("hmMap=="+hmMap);
			
			
			request.setAttribute("hmempcnt", hmempcnt);
			request.setAttribute("alList", alList);
			request.setAttribute("hmMap", hmMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void generatePTaxChallanECR() {

	}

	public void publishReport(String strData, String strFileName, UtilityFunctions uF) {
		try {

			ServletOutputStream op = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setContentLength((int) strData.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + strFileName + "\"");
			op.write(strData.getBytes());
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	private HttpServletResponse response;
	private HttpServletRequest request;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(String challanDate) {
		this.challanDate = challanDate;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getChallanNum() {
		return challanNum;
	}

	public void setChallanNum(String challanNum) {
		this.challanNum = challanNum;
	}

}
