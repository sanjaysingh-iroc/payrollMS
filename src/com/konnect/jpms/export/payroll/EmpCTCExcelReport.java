package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class EmpCTCExcelReport implements ServletRequestAware,ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String financialYear;
	String strEmpId;
	UtilityFunctions uF = new UtilityFunctions();
	public void execute() 
	{
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		viewSalaryYearlyReport();
		generateCTCExcelReport();
		return;
	}
	public void generateCTCExcelReport(){
		
		try {



			String strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String strEmpId = (String)request.getAttribute("strEmpId");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			List alServiceId = (List)request.getAttribute("alServiceId");
			Map hmServiceSalaryHeadMap = (Map)request.getAttribute("hmServiceSalaryHeadMap");
			String org_name=(String)request.getAttribute("org_name");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
		
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			
			header.add(new DataStyle(uF.showData(org_name, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("CTC report for the period of "+strFinancialYearStart+" to "+strFinancialYearEnd+" ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			header.add(new DataStyle("Date- "+curr_date,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		
			
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Component",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
			int count=0;
			double dblAmount = 0;
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			for(int i=0; i<alServiceId.size(); i++){
				String strServiceId = (String)alServiceId.get(i);
				List alSalaryList = (List)hmServiceSalaryHeadMap.get(strServiceId);
				if(alSalaryList==null)alSalaryList=new ArrayList();
				
				
				for (; count<alSalaryList.size(); count++){
					String strSalaryHeadId = (String)alSalaryList.get(count);
					dblAmount += uF.parseToDouble((String)hmEarningSalaryMap.get(strSalaryHeadId+"_"+strServiceId));
				
					List<DataStyle> innerList=new ArrayList<DataStyle>();
					String empCode=(count==0)?uF.showData((String)hmEmpCode.get(strEmpId),""):"";
					String empName=(count==0)?uF.showData((String)hmEmpName.get(strEmpId),""):"";
					String Component=uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),"");
					String Amount=uF.showData((String)hmEarningSalaryMap.get(strSalaryHeadId+"_"+strServiceId),"");
						
					innerList.add(new DataStyle(empCode,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					innerList.add(new DataStyle(empName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					innerList.add(new DataStyle(Component,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					innerList.add(new DataStyle(Amount,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					
					reportData.add(innerList);
		
				}
			}
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.generateExcelSheet(workbook,sheet,header,reportData);
		
		
		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=EmpCTCExcelReport.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		
	}catch (Exception e) {
		e.printStackTrace();
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
			
			con = db.makeConnection(con);
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			Map hmEarningSalaryMap = new LinkedHashMap();
			Map hmEarningSalaryTotalMap = new HashMap();

			List<String> alServiceId = new ArrayList<String>();
			List<String> alSalaryHeadId = new ArrayList<String>();
			Map hmServiceSalaryHeadMap = new HashMap();
			
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			if(nSalaryStrucuterType == S_GRADE_WISE){
				String strEmpGradeId = CF.getEmpGradeId(con, getStrEmpId());
				pst = con.prepareStatement("select * from emp_salary_details esd, salary_details sd " +
						"where sd.salary_head_id = esd.salary_head_id and emp_id = ? " +
						"and entry_date = (select max(entry_date) from emp_salary_details " +
						"where emp_id = ? and is_approved = true and grade_id=?) and esd.earning_deduction = 'E' " +
						"and esd.salary_head_id not in ("+GROSS+") and sd.grade_id=esd.grade_id " +
						"and sd.grade_id=? and esd.grade_id=? order by weight");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(getStrEmpId()));
				pst.setInt(3, uF.parseToInt(strEmpGradeId));
				pst.setInt(4, uF.parseToInt(strEmpGradeId));
				pst.setInt(5, uF.parseToInt(strEmpGradeId));
			} else {	
				String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());
				pst = con.prepareStatement("select * from emp_salary_details esd, salary_details sd " +
						"where sd.salary_head_id = esd.salary_head_id and emp_id = ? " +
						"and entry_date = (select max(entry_date) from emp_salary_details " +
						"where emp_id = ? and is_approved = true and level_id=?) and esd.earning_deduction = 'E' " +
						"and esd.salary_head_id not in ("+GROSS+") and sd.level_id=esd.level_id " +
						"and sd.level_id=? and esd.level_id=? order by weight");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(getStrEmpId()));
				pst.setInt(3, uF.parseToInt(strEmpLevelId));
				pst.setInt(4, uF.parseToInt(strEmpLevelId));
				pst.setInt(5, uF.parseToInt(strEmpLevelId));
			}
			rs = pst.executeQuery();
			String strSalaryHeadId = null;
			String strServiceId = null;			
			while(rs.next()){
				
				strSalaryHeadId = rs.getString("salary_head_id");
				strServiceId = rs.getString("service_id");
			
				double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get(strSalaryHeadId+strServiceId));
				dblAmount += rs.getDouble("amount");
				hmEarningSalaryMap.put(strSalaryHeadId+"_"+strServiceId, dblAmount+"");
				
				if(!alServiceId.contains(strServiceId)){
					alServiceId.add(strServiceId);
				}
				if(!alSalaryHeadId.contains(strSalaryHeadId)){
					alSalaryHeadId.add(strSalaryHeadId);
				}
				hmServiceSalaryHeadMap.put(strServiceId, alSalaryHeadId);
				
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select org_name from employee_official_details eod,org_details od where eod.org_id=od.org_id and emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs=pst.executeQuery();
			String org_name=null;
			while(rs.next()){
				org_name=rs.getString("org_name");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("org_name", org_name);			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			request.setAttribute("alServiceId", alServiceId);
			request.setAttribute("hmServiceSalaryHeadMap", hmServiceSalaryHeadMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strEmpId", getStrEmpId());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		

	}

	HttpServletResponse response;
	HttpServletRequest request;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
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
	}}
