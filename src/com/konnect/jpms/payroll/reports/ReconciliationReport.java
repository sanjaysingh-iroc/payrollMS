package com.konnect.jpms.payroll.reports;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReconciliationReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ReconciliationReport.class);
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strSalaryhead;
	
	String[] f_salaryhead;
	String financialYear;
	String strMonth;
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillSalaryHeads> salaryHeadList;
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	
	
	
	String exportType; 
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, "Reconciliation Report");
		request.setAttribute(PAGE, "/jsp/payroll/reports/ReconciliationReport.jsp");
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrSalaryhead() != null && !getStrSalaryhead().equals("")) {
			setF_salaryhead(getStrSalaryhead().split(","));
		} else {
			setF_salaryhead(null);
		}
		
		if(getStrMonth()==null){
			setStrMonth("1");
		}
		
		viewReconciliationReport(uF);
		if(getExportType()!= null && getExportType().equals("pdf")){
			getReconciliationPdfReport(uF);
		}

		return loadReconciliationReport(uF);

	}
   
	
	public String loadReconciliationReport(UtilityFunctions uF) {
		
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsByOrgWithoutCTC("E", getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
				
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("SALARYHEADS");
		if(getF_salaryhead()!=null)  {
			String strSalaryHeads="";
			int k=0;
			for(int i=0;salaryHeadList!=null && i<salaryHeadList.size();i++){
				if(getF_salaryhead().equals(salaryHeadList.get(i).getSalaryHeadId())) {
					if(k==0) {
						strSalaryHeads=salaryHeadList.get(i).getSalaryHeadName();
					} else {
						strSalaryHeads+=", "+salaryHeadList.get(i).getSalaryHeadName();
					}
					k++;
				}
			}
			if(strSalaryHeads!=null && !strSalaryHeads.equals("")) {
				hmFilter.put("SALARYHEADS", strSalaryHeads);
			} else {
				hmFilter.put("SALARYHEADS", "");
			}
		} else {
			hmFilter.put("SALARYHEADS", "");
		}
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private void getReconciliationPdfReport(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEmpPTax = (Map)request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}


			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con);
			Map<String, String> hmLevelMap =(Map<String, String>)request.getAttribute("hmLevelMap");
	
			String  strPrevMonth = (String)request.getAttribute("strPrevMonth");
			
	
			Map<String, String> hmCurrMonthAmt =(Map<String, String>)request.getAttribute("hmCurrMonthAmt");
			Map<String, String> hmPrevMonthAmt =(Map<String, String>)request.getAttribute("hmPrevMonthAmt");

			Map<String, String> hmOrgCode=(Map<String, String>) request.getAttribute("hmOrgCode");;
			if(hmOrgCode == null) hmOrgCode=new HashMap<String, String>();
	
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();
					
			/*String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Reconciliation for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM")+" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+"</b></font></td></tr></table>";*/
			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Reconciliation for the month of "+uF.getMonth(uF.parseToInt(strMonth))+" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+"</b></font></td></tr></table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			document.add(new Paragraph(" "));
			
			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td align=\"center\"><font size=\"1\"><b>____________________________________________________________________________________________________________________________________________________________________________</b></font></td>" +
			"</tr></table>";                       
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase4.add(supList4.get(0));
			document.add(phrase4);
			
			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
					"<td><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>" +
					"<td><font size=\"1\"><b>&nbsp;Organization&nbsp;&nbsp;</b></font></td>" +
					"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.getMonth(uF.parseToInt(strPrevMonth))+"&nbsp;&nbsp;</b></font></td>" +
					"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.getMonth(uF.parseToInt(strMonth))+"&nbsp;&nbsp;</b></font></td>"+
					"<td align=\"right\"><font size=\"1\"><b>&nbsp;Diffrence&nbsp;&nbsp;</b></font></td>" +
			"</tr></table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			
	
			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase5.add(supList4.get(0));
			document.add(phrase5);
			
			Set set = hmCurrMonthAmt.keySet();
			Iterator it = set.iterator();
			int count=0;
			double dblPrevAmountTotal = 0;
			double dblCurrAmountTotal = 0;
			double dblDifferenceAmountTotal = 0;
			while(it.hasNext()){
				String orgid = (String)it.next();
			
				count++;
				dblPrevAmountTotal+=uF.parseToDouble((String)hmPrevMonthAmt.get(orgid));
				dblCurrAmountTotal+=uF.parseToDouble((String)hmCurrMonthAmt.get(orgid));
				double difference=uF.parseToDouble((String)hmPrevMonthAmt.get(orgid))-uF.parseToDouble((String)hmCurrMonthAmt.get(orgid));
				dblDifferenceAmountTotal+=difference;
				
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td><font size=\"1\"><b>&nbsp;"+(count)+"&nbsp;&nbsp;</b></font></td>" +
				"<td><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmOrg.get(orgid), "")+"&nbsp;&nbsp;</b></font></td>" +
				"<td><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmOrgCode.get(orgid), "")+"&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmPrevMonthAmt.get(orgid), "0")+"&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmCurrMonthAmt.get(orgid), "0")+"&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),difference), "0")+"&nbsp;&nbsp;</b></font></td>" +
				"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
		
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			if(count==0){
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\"><b>&nbsp;No Data found</b></font></td>" +
				"</tr></table>";
				
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}else{
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td>&nbsp;</td>" +
				"<td>&nbsp;</td>" +
				"<td style=\"text-align:right;\"><font size=\"2\"><strong>Total</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPrevAmountTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCurrAmountTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblDifferenceAmountTotal)+"</strong></font></td></tr>" +
				"</table>";
				
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			
			
			document.close();
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=ReconciliationReport.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeConnection(con);
	}
	
}
	
	public void viewReconciliationReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

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
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpPanNo =CF.getEmpPANNoMap(con);
			
			String strMonth=null;
			String strYear=null;			
			Map<String, String> hmPrevMonthAmt = new HashMap<String, String>();			
			Map<String, String> hmCurrMonthAmt = new HashMap<String, String>();
						
			String prevMonth=uF.parseToInt(getStrMonth())>1 ? ""+(uF.parseToInt(getStrMonth())-1):"12";
			String strPrevMonthEmpIds = getEmpPayrollHistory(con,uF,prevMonth,strFinancialYearStart,strFinancialYearEnd);
			
			if(strPrevMonthEmpIds !=null && !strPrevMonthEmpIds.equals("") && strPrevMonthEmpIds.length() > 0){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(amount)as amount,month,year,eod.org_id,eod.emp_id from employee_personal_details epd, employee_official_details eod, " +
						" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
						" and month=? and financial_year_from_date=? and financial_year_to_date=? " +
						" and salary_head_id  in (0");
				for(int i=0;getF_salaryhead()!=null && i<getF_salaryhead().length;i++){
					sbQuery.append(","+getF_salaryhead()[i]); 
				}
				sbQuery.append(")");
				sbQuery.append(" and pg.emp_id in ("+strPrevMonthEmpIds+") ");
				sbQuery.append(" group by month,year,eod.org_id,eod.emp_id order by eod.org_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(prevMonth));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst strPrevMonthEmpIds =======>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){				
					strMonth = rs.getString("month");
					strYear = rs.getString("year");
	
					double dblAmount = rs.getDouble("amount");
	
					hmPrevMonthAmt.put(rs.getString("org_id")+"_"+rs.getString("emp_id"), uF.formatIntoTwoDecimal(dblAmount));
				}
				rs.close();
				pst.close();
			}
			
			String strCurrMonthEmpIds = getEmpPayrollHistory(con,uF,getStrMonth(),strFinancialYearStart,strFinancialYearEnd);
			
			if(strCurrMonthEmpIds !=null && !strCurrMonthEmpIds.equals("") && strCurrMonthEmpIds.length() > 0){
				StringBuilder sbQuery = new StringBuilder();			
				sbQuery.append("select sum(amount)as amount,month,year,eod.org_id,eod.emp_id from employee_personal_details epd, employee_official_details eod, " +
						" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
						" and month=? and financial_year_from_date=? and financial_year_to_date=? " +
						" and salary_head_id  in (0");
				
				for(int i=0;getF_salaryhead()!=null && i<getF_salaryhead().length;i++){
					sbQuery.append(","+getF_salaryhead()[i]);
				}
				sbQuery.append(")");
				sbQuery.append(" and pg.emp_id in ("+strCurrMonthEmpIds+") ");
				sbQuery.append(" group by month,year,eod.org_id,eod.emp_id order by eod.org_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrMonth()));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst=======>"+pst);
				rs = pst.executeQuery();
				strMonth=null;
				strYear=null;
				while(rs.next()){				
					strMonth = rs.getString("month");
					strYear = rs.getString("year");
					hmCurrMonthAmt.put(rs.getString("org_id")+"_"+rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),rs.getDouble("amount")));
				}
				rs.close();
				pst.close();
			}
			
			pst=con.prepareStatement("select org_id,org_name,org_code from org_details");
			rs=pst.executeQuery();
			Map<String, String> hmOrg=new HashMap<String, String>();
			Map<String, String> hmOrgCode=new HashMap<String, String>();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
				hmOrgCode.put(rs.getString("org_id"), rs.getString("org_code"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			Iterator<String> it = hmCurrMonthAmt.keySet().iterator();
			int count=0;
			double dblPrevAmountTotal = 0.0d;
			double dblCurrAmountTotal = 0.0d;
			double dblDifferenceAmountTotal = 0.0d;
			List<List<String>> reportList = new ArrayList<List<String>>();
			while(it.hasNext()){
				String strKey = (String)it.next();
				String[] temp = strKey.split("_");
				String orgId = temp[0];
				String empId = temp[1];
				count++;
				dblPrevAmountTotal += uF.parseToDouble((String)hmPrevMonthAmt.get(strKey));
				dblCurrAmountTotal += uF.parseToDouble((String)hmCurrMonthAmt.get(strKey));
				double difference = uF.parseToDouble((String)hmPrevMonthAmt.get(strKey))-uF.parseToDouble((String)hmCurrMonthAmt.get(strKey));
				dblDifferenceAmountTotal += difference;
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+count);
				alInner.add(uF.showData(""+hmOrgCode.get(orgId),""));
				alInner.add(uF.showData(""+hmOrg.get(orgId),""));
				alInner.add(uF.showData(""+hmEmpCode.get(empId),""));
				alInner.add(uF.showData(""+hmEmpName.get(empId),""));
				alInner.add(uF.showData(""+hmEmpPanNo.get(empId),""));
				alInner.add(uF.showData((String)hmPrevMonthAmt.get(strKey), "0"));
				alInner.add(uF.showData((String)hmCurrMonthAmt.get(strKey), "0"));
				alInner.add(uF.showData(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),difference), "0"));
				
				reportList.add(alInner);
			}
			
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("Total");
				alInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPrevAmountTotal));
				alInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCurrAmountTotal));
				alInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblDifferenceAmountTotal));
				
				reportList.add(alInner);
			}
//			System.out.println("reportList=======>"+reportList.toString());
			request.setAttribute("reportList", reportList);
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("hmOrgCode", hmOrgCode);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmCurrMonthAmt", hmCurrMonthAmt);
			request.setAttribute("hmPrevMonthAmt", hmPrevMonthAmt); 
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPanNo", hmEmpPanNo);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			request.setAttribute("strPrevMonth", prevMonth);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private String getEmpPayrollHistory(Connection con, UtilityFunctions uF, String strMonth, String strFinancialYearStart, String strFinancialYearEnd) {
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbEmp = null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_history where financial_year_start =? and financial_year_end=? and paid_month= ? ");
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	            
	        } 
	        if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        
	        if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strMonth));
//			System.out.println("pst====>"+pst);     
			rs = pst.executeQuery();
			Set<String> empSetlist = new HashSet<String>();
			Map<String, Map<String, String>> hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
			}
//			System.out.println("1 empSetlist====>"+empSetlist.toString());
			
			sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pg.emp_id) as emp_id from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true " +
					"and pg.financial_year_from_date=? and pg.financial_year_to_date=? and pg.month=?");
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	            
	        } 
	        
	        if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        
	        if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
	        sbQuery.append(" and pg.emp_id not in (select emp_id from payroll_history where financial_year_start =? and financial_year_end=? and paid_month= ?) ");
			sbQuery.append(" order by pg.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strMonth));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strMonth));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
			}
//			System.out.println("2 empSetlist====>"+empSetlist.toString());
			
			Iterator<String> it = empSetlist.iterator();
			while(it.hasNext()){
				String strEmp = it.next();
				if(sbEmp == null){
					sbEmp = new StringBuilder();
					sbEmp.append(strEmp);
				} else {
					sbEmp.append(","+strEmp);
				}
			}
//			System.out.println(" sbEmp ====>"+sbEmp.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		return sbEmp!=null ? sbEmp.toString() : null;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;


	}
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}


	public String[] getF_salaryhead() {
		return f_salaryhead;
	}


	public void setF_salaryhead(String[] f_salaryhead) {
		this.f_salaryhead = f_salaryhead;
	}


	public String getFinancialYear() {
		return financialYear;
	}


	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String[] getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}


	public String[] getF_service() {
		return f_service;
	}


	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}


	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}


	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}


	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}


	public List<FillMonth> getMonthList() {
		return monthList;
	}


	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}


	public String getExportType() {
		return exportType;
	}


	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrSalaryhead() {
		return strSalaryhead;
	}

	public void setStrSalaryhead(String strSalaryhead) {
		this.strSalaryhead = strSalaryhead;
	}
	
}
