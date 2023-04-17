package com.konnect.jpms.payroll.reports;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SalaryYearlyReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(SalaryYearlyReport.class);
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strEmployeType;
	
	String financialYear;
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_employeType;
	String[] f_service;
	
	List<FillFinancialYears> financialYearList;
	List<FillOrganisation> orgList; 
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	
	String exportType;
	
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		 
		request.setAttribute(TITLE, TReportSalaryYearly);
		request.setAttribute(PAGE, PReportSalaryYearly);
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
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
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		if(getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		viewSalaryYearlyReport(uF);
		if(getExportType()!= null && getExportType().equals("pdf")){
			generateSalaryYearlyPdfReport(uF);
		}

		return loadSalaryYearlyReport(uF);

	}
	
	
	public String loadSalaryYearlyReport(UtilityFunctions uF) {
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
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
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
				
		getSelectedFilter(uF);
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
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
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public void generateSalaryYearlyPdfReport(UtilityFunctions uF){
		
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

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

					
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Yearly Salary Summary for the period of "+strFinancialYearStart+" to "+strFinancialYearEnd+"</b></font></td></tr>" +
				"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);
		
		document.add(new Paragraph(" "));
		
		
		StringBuilder sb=new StringBuilder();
		
		sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
				"<td align=\"left\" width=\"35%\"><font size=\"1\">&nbsp;Components&nbsp;&nbsp;</font></td>");
		for(int i=0; i<alMonth.size(); i++){
		sb.append("<td align=\"right\"><font size=\"1\">&nbsp;"+uF.getDateFormat((String)alMonth.get(i),"MM","MMM")+"&nbsp;&nbsp;</font></td>");
		}
		sb.append("</tr></table>");
		
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		
		String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
		"<td align=\"left\"><font size=\"1\"><b>&nbsp;Earning</b></font></td>" +
		"</tr></table>";                       
		List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
		Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase2.add(supList2.get(0));
		document.add(phrase2);
		
		StringBuilder sb1=new StringBuilder();
		
		sb1.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		
		
		Set set = hmEarningSalaryMap.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String strSalaryHeadId = (String)it.next();
			Map hmInner = (Map)hmEarningSalaryMap.get(strSalaryHeadId);

			sb1.append("<tr><td align=\"left\"><font size=\"1\">&nbsp;"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),"")+"&nbsp;&nbsp;</font></td>");
			for(int i=0; i<alMonth.size(); i++){
				String strAmount = (String)hmInner.get((String)alMonth.get(i));
				sb1.append("<td align=\"right\"><font size=\"1\">&nbsp;"+uF.showData(strAmount,"0")+"&nbsp;&nbsp;</font></td>");
			}
			sb1.append("</tr>");
		}
		sb1.append("</table>");
		
		List<Element> supList6 = HTMLWorker.parseToList(new StringReader(sb1.toString()), null);
		Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase6.add(supList6.get(0));
		document.add(phrase6);
		
		
		StringBuilder sb2=new StringBuilder();
		
		sb2.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		sb2.append("<tr><td align=\"left\"><font size=\"1\"><b>&nbsp;Total &nbsp;&nbsp;</b></font></td>");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
			
			sb2.append("<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData(strTotalAmount,"0")+"&nbsp;&nbsp;</b></font></td>");
		}
		sb2.append("</tr>");
		sb2.append("</table>");
	
		List<Element> supList7 = HTMLWorker.parseToList(new StringReader(sb2.toString()), null);
		Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase7.add(supList7.get(0));
		document.add(phrase7);
		
		
		document.add(new Paragraph(" "));
		
		
				
		String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
		"<td align=\"left\"><font size=\"1\"><b>&nbsp;Deduction</b></font></td>" +
		"</tr></table>";                       
		List<Element> supList9 = HTMLWorker.parseToList(new StringReader(tbl3), null);
		Phrase phrase9 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase9.add(supList9.get(0));
		document.add(phrase9);
		
		
		StringBuilder sb4=new StringBuilder();
		
		sb4.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		
		
		set = hmDeductionSalaryMap.keySet();
		it = set.iterator();
		while(it.hasNext()){
			String strSalaryHeadId = (String)it.next();
			Map hmInner = (Map)hmDeductionSalaryMap.get(strSalaryHeadId);

			sb4.append("<tr><td align=\"left\"><font size=\"1\">&nbsp;"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),"")+"&nbsp;&nbsp;</font></td>");
			for(int i=0; i<alMonth.size(); i++){
				String strAmount = (String)hmInner.get((String)alMonth.get(i));
				sb4.append("<td align=\"right\"><font size=\"1\">&nbsp;"+uF.showData(strAmount,"0")+"&nbsp;&nbsp;</font></td>");
			}
			sb4.append("</tr>");
		}
		sb4.append("</table>");
		
		List<Element> supList10 = HTMLWorker.parseToList(new StringReader(sb4.toString()), null);
		Phrase phrase10 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase10.add(supList10.get(0));
		document.add(phrase10);
		
		
		StringBuilder sb5=new StringBuilder();
		
		sb5.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		sb5.append("<tr><td align=\"left\"><font size=\"1\"><b>&nbsp;Total &nbsp;&nbsp;</b></font></td>");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
			
			sb5.append("<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData(strTotalAmount,"0")+"&nbsp;&nbsp;</b></font></td>");
		}
		sb5.append("</tr>");
		sb5.append("</table>");
	
		List<Element> supList11 = HTMLWorker.parseToList(new StringReader(sb5.toString()), null);
		Phrase phrase11 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase11.add(supList11.get(0));
		document.add(phrase11);
		
		
		StringBuilder sb6=new StringBuilder();
		
		sb6.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		sb6.append("<tr><td align=\"left\"><font size=\"1\"><b>&nbsp;Net Pay &nbsp;&nbsp;</b></font></td>");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalEarAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
			String strTotalDedAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
			
			String strNet = (uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(uF.parseToDouble(strTotalEarAmount) - uF.parseToDouble(strTotalDedAmount))))+"";
			
			sb6.append("<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData(strNet,"0")+"&nbsp;&nbsp;</b></font></td>");
		}
		sb6.append("</tr>");
		sb6.append("</table>");
	
		List<Element> supList12 = HTMLWorker.parseToList(new StringReader(sb6.toString()), null);
		Phrase phrase12 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase12.add(supList12.get(0));
		document.add(phrase12);
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=YearlySalaryReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	
public String viewSalaryYearlyReport(UtilityFunctions uF) {

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	

	try {
		String[] strFinancialYearDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;

		if (getFinancialYear() != null) {
			
			strFinancialYearDates = getFinancialYear().split("-");
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];
		
		} else {
			
			strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
			
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];
			 
		}
		
		con = db.makeConnection(con);
		
		Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
		
		Map hmEmpName = CF.getEmpNameMap(con,null, null);
		Map hmEmpCode = CF.getEmpCodeMap(con);
		
		
		
		
		Map hmEarningSalaryMap = new LinkedHashMap();
		Map hmEarningSalaryTotalMap = new HashMap();
		Map hmDeductionSalaryMap = new LinkedHashMap();
		Map hmDeductionSalaryTotalMap = new HashMap();
		
		
		
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
		cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
		cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
		
		List alMonth = new ArrayList();
		
		for(int i=0; i<12; i++){
			alMonth.add((cal.get(Calendar.MONTH)+1)+""); 
			
			cal.add(Calendar.MONTH, 1);
		}
		
		
		for(int i=0; i<alMonth.size(); i++){
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_history ph, employee_official_details eod where ph.emp_id=eod.emp_id and financial_year_start =? and financial_year_end=? and paid_month= ? ");
			
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and ph.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and ph.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
            
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int j=0; j<getF_service().length; j++){
                	sbQuery.append(" ph.service_id like '%,"+getF_service()[j]+",%'");
                    
                    if(j<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and ph.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and ph.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and ph.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and ph.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by ph.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt((String)alMonth.get(i)));
//			System.out.println("pst1====>"+pst);     
			rs = pst.executeQuery();
			Set<String> empSetlist = new HashSet<String>();
			Map<String, Map<String, String>> hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));							
			}
			
			sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pg.emp_id) as emp_id from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and pg.financial_year_from_date=? and pg.financial_year_to_date=? and pg.month= ?");
			
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int j=0; j<getF_service().length; j++){
                	sbQuery.append(" eod.service_id like '%,"+getF_service()[j]+",%'");
                    
                    if(j<getF_service().length-1){
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
			pst.setInt(3, uF.parseToInt((String)alMonth.get(i)));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt((String)alMonth.get(i)));
//			System.out.println("pst2====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			StringBuilder sbEmp = null;
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
			
			if(sbEmp !=null && sbEmp.length() > 0){
			
				sbQuery = new StringBuilder();
				sbQuery.append("select salary_head_id, sum(amount) as amount, month from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and month= ? and earning_deduction = ? and is_paid = true ");
				sbQuery.append(" and emp_id in ("+sbEmp.toString()+") ");
				sbQuery.append(" group by salary_head_id, month order by salary_head_id");
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt((String)alMonth.get(i)));
				pst.setString(4, "E");
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				String strMonthNew = null;
				String strMonthOld = null;
				while(rs.next()){
					
					Map hmEmpInner1 = (Map) hmEarningSalaryMap.get(rs.getString("salary_head_id"));
					if(hmEmpInner1 == null) hmEmpInner1 = new HashMap();
					
					double dblAmount1 = uF.parseToDouble((String)hmEmpInner1.get(rs.getString("month")));
					dblAmount1 += uF.parseToDouble(rs.getString("amount"));
					
					hmEmpInner1.put(rs.getString("month"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount1));
					
					hmEarningSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner1);
					
					double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get(rs.getString("month")));
					dblAmount += rs.getDouble("amount");
					hmEarningSalaryTotalMap.put(rs.getString("month"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt((String)alMonth.get(i)));
				pst.setString(4, "D");
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				strMonthNew = null;
				strMonthOld = null;
				while(rs.next()){
					Map hmEmpInner2 = (Map) hmDeductionSalaryMap.get(rs.getString("salary_head_id"));
					if(hmEmpInner2 == null) hmEmpInner2 = new HashMap();
					
					double dblAmount1 = uF.parseToDouble((String)hmEmpInner2.get(rs.getString("month")));
					dblAmount1 += uF.parseToDouble(rs.getString("amount"));
					
					hmEmpInner2.put(rs.getString("month"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount1));
					
					hmDeductionSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner2);
					
					double dblAmount = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get(rs.getString("month")));
					dblAmount += rs.getDouble("amount");
					
					hmDeductionSalaryTotalMap.put(rs.getString("month"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				}
				rs.close();
				pst.close();
			}
		}
		
		List<List<String>> reportList = new ArrayList<List<String>>();
		List<String> alInner = new ArrayList<String>();
		alInner.add("Earning");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		reportList.add(alInner);
		
		Set set = hmEarningSalaryMap.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String strSalaryHeadId = (String)it.next();
			Map hmInner = (Map)hmEarningSalaryMap.get(strSalaryHeadId);
			alInner = new ArrayList<String>();
			alInner.add(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),""));
			
			for(int i=0; i<alMonth.size(); i++){
				String strAmount = (String)hmInner.get((String)alMonth.get(i));
				
				alInner.add(uF.showData(strAmount,"0"));
			}
			reportList.add(alInner);
		}
		
		alInner = new ArrayList<String>();
		alInner.add("Total");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
			alInner.add(""+uF.showData(strTotalAmount,"0")+"");
		}
		reportList.add(alInner);
		
		alInner = new ArrayList<String>();
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		reportList.add(alInner);
		
		alInner = new ArrayList<String>();
		alInner.add("Deduction");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		alInner.add("");
		reportList.add(alInner);
		
		set = hmDeductionSalaryMap.keySet();
		it = set.iterator();
		while(it.hasNext()){
			String strSalaryHeadId = (String)it.next();
			Map hmInner = (Map)hmDeductionSalaryMap.get(strSalaryHeadId);
			
			alInner = new ArrayList<String>();
			alInner.add(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),""));
			
			for(int i=0; i<alMonth.size(); i++){
				String strAmount = (String)hmInner.get((String)alMonth.get(i));
				
				alInner.add(uF.showData(strAmount,"0"));
			}
			reportList.add(alInner);
		}
		
		alInner = new ArrayList<String>();
		alInner.add("Total");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
			alInner.add(""+uF.showData(strTotalAmount,"0")+"");
		}
		reportList.add(alInner);
		
		alInner = new ArrayList<String>();
		alInner.add("Net Pay");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalEarAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
			String strTotalDedAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
			String strNet = uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(uF.parseToDouble(strTotalEarAmount) - uF.parseToDouble(strTotalDedAmount)));
			alInner.add(""+uF.showData(strNet,"0")+"");
		}
		reportList.add(alInner);
		
		request.setAttribute("reportList", reportList);
		
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
		log.error(e.getClass() + ": " +  e.getMessage(), e);
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return SUCCESS;

}


	
//	public String viewSalaryYearlyReport(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//
//		try {
//			String[] strFinancialYearDates = null;
//			String strFinancialYearStart = null;
//			String strFinancialYearEnd = null;
//
//			if (getFinancialYear() != null) {
//				
//				strFinancialYearDates = getFinancialYear().split("-");
//				strFinancialYearStart = strFinancialYearDates[0];
//				strFinancialYearEnd = strFinancialYearDates[1];
//			
//			} else {
//				
//				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
//				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
//				
//				strFinancialYearStart = strFinancialYearDates[0];
//				strFinancialYearEnd = strFinancialYearDates[1];
//				 
//			}
//			
//			con = db.makeConnection(con);
//			
//			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
//			
//			Map hmEmpName = CF.getEmpNameMap(con,null, null);
//			Map hmEmpCode = CF.getEmpCodeMap(con);
//			
//			
//			
//			
//			Map hmEarningSalaryMap = new LinkedHashMap();
//			Map hmEarningSalaryTotalMap = new HashMap();
//			Map hmDeductionSalaryMap = new LinkedHashMap();
//			Map hmDeductionSalaryTotalMap = new HashMap();
//			Map hmEmpInner = new HashMap();
//			
//			
//			
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
//			
//			List alMonth = new ArrayList();
//			
//			for(int i=0; i<12; i++){
//				alMonth.add((cal.get(Calendar.MONTH)+1)+""); 
//				
//				cal.add(Calendar.MONTH, 1);
//			}
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select salary_head_id, sum(amount) as amount, month, entry_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and earning_deduction = ? and is_paid = true ");
//			
//			if((getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//				sbQuery.append("and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
//			}
//			
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
//	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
//				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//            if(getF_department()!=null && getF_department().length>0){
//                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//            }
//            if(getF_level()!=null && getF_level().length>0){
//                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//            }
//            if((getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//				sbQuery.append(") ");
//			}
//            if(getF_service()!=null && getF_service().length>0){
//            	sbQuery.append(" and service_id in ("+StringUtils.join(getF_service(), ",")+") ");
//                
//            } 
//			sbQuery.append(" group by salary_head_id, month, entry_date order by salary_head_id");
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setString(3, "E");
////			System.out.println("pst==>"+pst);
//			rs = pst.executeQuery();
//			String strMonthNew = null;
//			String strMonthOld = null;
//			
//			while(rs.next()){
//				
//				strMonthNew = rs.getString("salary_head_id");
//				
//				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
//					hmEmpInner = new HashMap();
//				}
//				
//				double dblAmount1 = uF.parseToDouble((String)hmEmpInner.get(rs.getString("month")));
//				dblAmount1 += uF.parseToDouble(rs.getString("amount"));
//				
////				hmEmpInner.put(rs.getString("month"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
//				hmEmpInner.put(rs.getString("month"), uF.formatIntoTwoDecimal(dblAmount1));
//				
//				hmEarningSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner);
//				
//				double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get(rs.getString("month")));
//				dblAmount += rs.getDouble("amount");
//				hmEarningSalaryTotalMap.put(rs.getString("month"), uF.formatIntoTwoDecimal(dblAmount));
//				
//				strMonthOld  = strMonthNew ;
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setString(3, "D");
////			System.out.println("pst==>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				strMonthNew = rs.getString("salary_head_id");
//				
//				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
//					hmEmpInner = new HashMap();
//				}
//				
//				double dblAmount1 = uF.parseToDouble((String)hmEmpInner.get(rs.getString("month")));
//				dblAmount1 += uF.parseToDouble(rs.getString("amount"));
//				
////				hmEmpInner.put(rs.getString("month"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
//				hmEmpInner.put(rs.getString("month"), uF.formatIntoTwoDecimal(dblAmount1));
//				
//				hmDeductionSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner);
//				
//				double dblAmount = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get(rs.getString("month")));
//				dblAmount += rs.getDouble("amount");
//				
//				hmDeductionSalaryTotalMap.put(rs.getString("month"), uF.formatIntoTwoDecimal(dblAmount));
//				
//				strMonthOld  = strMonthNew ;
//			}
//			rs.close();
//			pst.close();
//			
//			List<List<String>> reportList = new ArrayList<List<String>>();
//			List<String> alInner = new ArrayList<String>();
//			alInner.add("Earning");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			reportList.add(alInner);
//			
//			Set set = hmEarningSalaryMap.keySet();
//			Iterator it = set.iterator();
//			while(it.hasNext()){
//				String strSalaryHeadId = (String)it.next();
//				Map hmInner = (Map)hmEarningSalaryMap.get(strSalaryHeadId);
//				alInner = new ArrayList<String>();
//				alInner.add(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),""));
//				
//				for(int i=0; i<alMonth.size(); i++){
//					String strAmount = (String)hmInner.get((String)alMonth.get(i));
//					
//					alInner.add(uF.showData(strAmount,"0"));
//				}
//				reportList.add(alInner);
//			}
//			
//			alInner = new ArrayList<String>();
//			alInner.add("Total");
//			for(int i=0; i<alMonth.size(); i++){
//				String strTotalAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
//				alInner.add(""+uF.showData(strTotalAmount,"0")+"");
//			}
//			reportList.add(alInner);
//			
//			alInner = new ArrayList<String>();
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			reportList.add(alInner);
//			
//			alInner = new ArrayList<String>();
//			alInner.add("Deduction");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			alInner.add("");
//			reportList.add(alInner);
//			
//			set = hmDeductionSalaryMap.keySet();
//			it = set.iterator();
//			while(it.hasNext()){
//				String strSalaryHeadId = (String)it.next();
//				Map hmInner = (Map)hmDeductionSalaryMap.get(strSalaryHeadId);
//				
//				alInner = new ArrayList<String>();
//				alInner.add(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),""));
//				
//				for(int i=0; i<alMonth.size(); i++){
//					String strAmount = (String)hmInner.get((String)alMonth.get(i));
//					
//					alInner.add(uF.showData(strAmount,"0"));
//				}
//				reportList.add(alInner);
//			}
//			
//			alInner = new ArrayList<String>();
//			alInner.add("Total");
//			for(int i=0; i<alMonth.size(); i++){
//				String strTotalAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
//				alInner.add(""+uF.showData(strTotalAmount,"0")+"");
//			}
//			reportList.add(alInner);
//			
//			alInner = new ArrayList<String>();
//			alInner.add("Net Pay");
//			for(int i=0; i<alMonth.size(); i++){
//				String strTotalEarAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
//				String strTotalDedAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
//				String strNet = uF.formatIntoTwoDecimal((uF.parseToDouble(strTotalEarAmount) - uF.parseToDouble(strTotalDedAmount)));
//				alInner.add(""+uF.showData(strNet,"0")+"");
//			}
//			reportList.add(alInner);
//			
//			request.setAttribute("reportList", reportList);
//			
//			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
//			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
//			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
//			request.setAttribute("hmDeductionSalaryMap", hmDeductionSalaryMap);
//			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
//			request.setAttribute("hmDeductionSalaryTotalMap", hmDeductionSalaryTotalMap);
//			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
//			request.setAttribute("hmEmpName", hmEmpName);
//			request.setAttribute("hmEmpCode", hmEmpCode);
//			request.setAttribute("alMonth", alMonth);
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//
//	}

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




	public String getFinancialYear() {
		return financialYear;
	}




	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
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




	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}




	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}
}
