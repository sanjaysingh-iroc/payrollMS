package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EmpSalaryYearlyExcelReports implements ServletRequestAware,ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String financialYear;
	String strEmpId;
	Map<String, String> hmEmployeeDetails = new HashMap<String, String>();
	UtilityFunctions uF = new UtilityFunctions();
	public void execute()
	{
		 
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return; 
		strUserType = (String) session.getAttribute(USERTYPE);
		getEmployeeName();
		viewSalaryYearlyReport();
		generateEmpSalaryYearlyExcelReport();
		return;
		
		
	}
	
public void generateEmpSalaryYearlyExcelReport(){
		
		try {
			
			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			List alMonth = (List)request.getAttribute("alMonth");
			Map hmEmpPANNo = (Map)request.getAttribute("hmEmpPANNo");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");	
			
//			System.out.println("alMonth=======>"+alMonth.toString());
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("Yearly Salary Summary of "+uF.showData((String)hmEmployeeDetails.get("NAME"),"")+" for the period of "+strFinancialYearStart+" to "+strFinancialYearEnd+" Employee Pan No: "+uF.showData((String)hmEmployeeDetails.get("PAN_NO"),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Components",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			for(int i=0; i<alMonth.size(); i++){
				header.add(new DataStyle(uF.getDateFormat((String)alMonth.get(i),"MM","MMM"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			List<DataStyle> innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Earning",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
		
			
		
			Set set = hmEarningSalaryMap.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				String strSalaryHeadId = (String)it.next();
				Map hmInner = (Map)hmEarningSalaryMap.get(strSalaryHeadId);
				innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				for(int i=0; i<alMonth.size(); i++){
					String strAmount = (String)hmInner.get((String)alMonth.get(i));
					innerList.add(new DataStyle(uF.showData(strAmount,"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				}
				reportData.add(innerList);
			}
		
			innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			for(int i=0; i<alMonth.size(); i++){
				String strTotalAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
				innerList.add(new DataStyle(uF.showData(strTotalAmount,"0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			reportData.add(innerList);
		 
			innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Deduction",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			set = hmDeductionSalaryMap.keySet();
			it = set.iterator();
			while(it.hasNext()){
				String strSalaryHeadId = (String)it.next();
				Map hmInner = (Map)hmDeductionSalaryMap.get(strSalaryHeadId);
				innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				for(int i=0; i<alMonth.size(); i++){
					String strAmount = (String)hmInner.get((String)alMonth.get(i));
					innerList.add(new DataStyle(uF.showData(strAmount,"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				}
				reportData.add(innerList);
			}
			innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			for(int i=0; i<alMonth.size(); i++){
				String strTotalAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
				innerList.add(new DataStyle(uF.showData(strTotalAmount,"0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			reportData.add(innerList);
		
		
			innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Net Pay",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			
			for(int i=0; i<alMonth.size(); i++){
				String strTotalEarAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
				String strTotalDedAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
				
				String strNet = (uF.formatIntoTwoDecimalWithOutComma((uF.parseToDouble(strTotalEarAmount) - uF.parseToDouble(strTotalDedAmount))))+"";
				innerList.add(new DataStyle(uF.showData(strNet,"0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			reportData.add(innerList);
		
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=EmpSalaryYearlyExcelReports.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	public void getEmployeeName(){
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();
	try {
		con = db.makeConnection(con);
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		
		pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
		pst.setInt(1, uF.parseToInt(getStrEmpId()));
		rs = pst.executeQuery();
		while(rs.next()){
			
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rs.getString("emp_mname");
				}
			}
		
			
			hmEmployeeDetails.put("NAME", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			hmEmployeeDetails.put("PAN_NO", rs.getString("emp_pan_no"));
		}
		rs.close();
		pst.close();
	}catch (Exception e) {
		e.printStackTrace();
	}finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	}
	public void viewSalaryYearlyReport() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

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
			
			
			Map hmEarningSalaryMap = new LinkedHashMap();
			Map hmEarningSalaryTotalMap = new HashMap();
			Map hmDeductionSalaryMap = new LinkedHashMap();
			Map hmDeductionSalaryTotalMap = new HashMap();
			Map hmEmpInner = new HashMap();
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List alMonth = new ArrayList();
			 
			for(int i=0; i<12; i++){
				alMonth.add((cal.get(Calendar.MONTH)+1)+"");
				cal.add(Calendar.MONTH, 1);
			}
			
			
			con = db.makeConnection(con);			
			
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);			
			Map hmEmpName = CF.getEmpNameMap(con, null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, month, entry_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and earning_deduction = ? and emp_id =? and is_paid = true group by salary_head_id, month, entry_date order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(3, "E");
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			String strMonthNew = null;
			String strMonthOld = null;
			while(rs.next()){
				
				strMonthNew = rs.getString("salary_head_id");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					hmEmpInner = new HashMap();
				}
				
				
				hmEmpInner.put(rs.getString("month"), rs.getString("amount"));
				hmEarningSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner);
				
				
				double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get(rs.getString("month")));
				dblAmount += rs.getDouble("amount");
				hmEarningSalaryTotalMap.put(rs.getString("month"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				
				strMonthOld  = strMonthNew ;
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, month, entry_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and earning_deduction = ? and emp_id=? and is_paid = true group by salary_head_id, month, entry_date order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(3, "D");
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				
				strMonthNew = rs.getString("salary_head_id");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					hmEmpInner = new HashMap();
				}
				
				
				hmEmpInner.put(rs.getString("month"), rs.getString("amount"));
				hmDeductionSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner);
				
				
				double dblAmount = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get(rs.getString("month")));
				dblAmount += rs.getDouble("amount");
				hmDeductionSalaryTotalMap.put(rs.getString("month"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				
				strMonthOld  = strMonthNew ;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmDeductionSalaryMap", hmDeductionSalaryMap);
			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
			request.setAttribute("hmDeductionSalaryTotalMap", hmDeductionSalaryTotalMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("alMonth", alMonth);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public boolean isEmpUserType() {
		return isEmpUserType;
	}

	public void setEmpUserType(boolean isEmpUserType) {
		this.isEmpUserType = isEmpUserType;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
	private HttpServletResponse response;
	private HttpServletRequest request;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
		
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

}
