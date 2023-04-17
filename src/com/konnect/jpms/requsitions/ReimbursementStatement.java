package com.konnect.jpms.requsitions;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

public class ReimbursementStatement extends ActionSupport  implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ReimbursementStatement.class);
	
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
	String[] f_service;
	String[] f_employeType;
	
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	
	List<FillFinancialYears> financialYearList; 
	
	String reimbursementType;
	String exportType;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, TReimbursementStatement);
		request.setAttribute(PAGE, PReimbursementStatement);
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
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
		
//		System.out.println("getReimbursementType() ===>> " + getReimbursementType());
		viewReimbursementStatement(uF);
		if(getExportType()!= null && getExportType().equals("pdf")){
			generateReimbursementReportPdf(uF);
		}

		return loadReimbursementStatement(uF);

	}
	
	public String loadReimbursementStatement(UtilityFunctions uF) {
		
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
		
		
		alFilter.add("REIMBURSEMENT_TYPE");
		if(getReimbursementType()!=null)  {
			String strReimbursementType = "";
			if(getReimbursementType().equals("P")) {
				strReimbursementType = "Project";
			} else if(getReimbursementType().equals("O")) {
				strReimbursementType = "Other";
			}
			if(strReimbursementType!=null && !strReimbursementType.equals("")) {
				hmFilter.put("REIMBURSEMENT_TYPE", strReimbursementType);
			} else {
				hmFilter.put("REIMBURSEMENT_TYPE", "All");
			}
		} else {
			hmFilter.put("REIMBURSEMENT_TYPE", "All");
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
	
	private void generateReimbursementReportPdf(UtilityFunctions uF) {
		
		try {

			
			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");

			Map hmReimbursementMap = (Map)request.getAttribute("hmReimbursementMap");
			Map hmReimbursementType = (Map)request.getAttribute("hmReimbursementType");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpPanNo = (Map)request.getAttribute("hmEmpPanNo");

			List alMonth = (List)request.getAttribute("alMonth");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Reimbursement statement for F.Y. "+strFinancialYearStart +" to "+strFinancialYearEnd+"</b></font></td></tr>" +
				"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);
		
		
		document.add(new Paragraph(" "));
	  	StringBuilder sb=new StringBuilder();
	  	sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
				"<td width=\"5%\"><font size=\"1\">&nbsp;Emp Code&nbsp;&nbsp;</font></td>" +
				"<td><font size=\"1\">&nbsp;Emp Name&nbsp;&nbsp;</font></td>" + "<td><font size=\"1\">&nbsp;Pan No&nbsp;&nbsp;</font></td>"
				+ "<td><font size=\"1\">&nbsp;Reimbursement Type&nbsp;&nbsp;</font></td>");
	  	
	  	
	  	for(int m=0; m<alMonth.size(); m++){
	  	sb.append("<td><font size=\"1\">&nbsp;"+uF.getDateFormat((String)alMonth.get(m), "MM", "MMM")+"&nbsp;&nbsp;</font></td>");
	  	}
		sb.append("</tr></table>");
		
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		
		Set set = hmReimbursementMap.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String strEmpId = (String)it.next();
			Map hmInner = (Map)hmReimbursementMap.get(strEmpId);
			if(hmInner==null)hmInner = new HashMap();
		StringBuilder sb1=new StringBuilder();
	  	sb1.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
				"<td width=\"5%\"><font size=\"1\">&nbsp;"+(String)hmEmpCode.get(strEmpId)+"&nbsp;&nbsp;</font></td>" +
				"<td><font size=\"1\">&nbsp;"+(String)hmEmpName.get(strEmpId)+"&nbsp;&nbsp;</font></td>"
				+"<td><font size=\"1\">&nbsp;"+(String)hmEmpPanNo.get(strEmpId)+"&nbsp;&nbsp;</font></td>");
	  	for(int m=0; m<alMonth.size(); m++){
	  	sb1.append("<td><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>");
	  	}
	  	sb1.append("</tr>");
	  	
	  	
	  	List alReimbursementType = (List)hmReimbursementType.get(strEmpId);
		if(alReimbursementType==null)alReimbursementType = new ArrayList();
		
		for(int i=0; i<alReimbursementType.size(); i++){
			
			
			sb1.append("<tr><td width=\"5%\"><font size=\"1\">&nbsp;"+(String)alReimbursementType.get(i)+"&nbsp;&nbsp;</font></td><td></td>");
			for(int m=0; m<alMonth.size(); m++){
				String strAmount = (String)hmInner.get((String)alMonth.get(m)+"_"+(String)alReimbursementType.get(i));
				sb1.append("<td width=\"5%\" align=\"right\"><font size=\"1\">&nbsp;"+uF.showData(strAmount, "0")+"&nbsp;&nbsp;</font></td>");
			
			}
			sb1.append("</tr>");
		}
	  	
	  	
		sb1.append("</table>");
		
		List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb1.toString()), null);
		Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase2.add(supList2.get(0));
		document.add(phrase2);
			
		}
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=ReimbursementReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush(); 
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	
	public String viewReimbursementStatement(UtilityFunctions uF) {

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
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpPanNo =CF.getEmpPANNoMap(con);
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			
			for(int i=0; i<12; i++){
				alMonth.add(uF.getDateFormat((cal.get(Calendar.MONTH)+1)+"", "MM", "MM"));
				
				cal.add(Calendar.MONTH, 1);
			}
			
			
			Map<String, Map<String, String>> hmReimbursementMap = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpInner = new HashMap<String, String>();
			List<String> alReimbursementType = new ArrayList<String>();
			Map<String, List<String>> hmReimbursementType = new HashMap<String, List<String>>();
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from emp_reimbursement where paid_date between  ? and ? ");			
//			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//				sbQuery.append(" and emp_id in (select emp_id from emp_history where emp_id > 0 ");
//			}
//			
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
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
//            if(getF_service()!=null && getF_service().length>0){
//                sbQuery.append(" and (");
//                for(int i=0; i<getF_service().length; i++){
//                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
//                    
//                    if(i<getF_service().length-1){
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" ) ");
//                
//            }
//            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//				sbQuery.append(") ");
//			}
//			sbQuery.append(" order by emp_id");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("1 pst==>"+pst);
//			rs = pst.executeQuery();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
//			List<String> empList = new ArrayList<String>();
//			while(rs.next()){
//				
//				strEmpIdNew = rs.getString("emp_id");
//				
//				
//				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
//					hmEmpInner = new HashMap<String, String>();
//					alReimbursementType = new ArrayList<String>();
//				}
//				
//				if(!empList.contains(rs.getString("emp_id"))){
//					empList.add(rs.getString("emp_id"));
//				}
//				
//				hmEmpInner = (Map<String, String>)hmReimbursementMap.get(strEmpIdNew);
//				if(hmEmpInner==null)hmEmpInner = new HashMap<String, String>();
//				
//				double dblAmount = uF.parseToDouble((String)hmEmpInner.get(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"))+"_"+rs.getString("reimbursement_info"));
//				dblAmount += rs.getDouble("reimbursement_amount");
//				hmEmpInner.put(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM")+"_"+rs.getString("reimbursement_info"), uF.formatIntoTwoDecimal(dblAmount));
//				
//				hmReimbursementMap.put(strEmpIdNew, hmEmpInner);
//				
//				if(!alReimbursementType.contains(rs.getString("reimbursement_info"))){
//					alReimbursementType.add(rs.getString("reimbursement_info"));
//				}
//				
//				hmReimbursementType.put(strEmpIdNew, alReimbursementType);
//				strEmpIdOld  = strEmpIdNew ;
//			}
			
			StringBuilder sbEmp= null;
//			for(int i = 0; empList != null && i < empList.size(); i++){
//				if(sbEmp == null){
//					sbEmp = new StringBuilder();
//					sbEmp.append(empList.get(i).trim());
//				} else {
//					sbEmp.append(","+empList.get(i).trim());
//				}
//			}
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_reimbursement where paid_date between  ? and ? ");
			if(getReimbursementType() != null && getReimbursementType().equals("P")) {	
				sbQuery.append(" and reimbursement_type1 = 'P'");
			} else if(getReimbursementType() != null && getReimbursementType().equals("O")) {	
				sbQuery.append(" and reimbursement_type1 != 'P'");
			}
			
			if(sbEmp != null) {
				sbQuery.append(" and emp_id not in ("+sbEmp.toString()+")");
			}
			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
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
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}
			sbQuery.append(" order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("2 pst==>"+pst);
			rs = pst.executeQuery();
			strEmpIdNew = null;
			strEmpIdOld = null;
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmEmpInner = new HashMap<String, String>();
					alReimbursementType = new ArrayList<String>();
				}
				
				hmEmpInner = (Map<String, String>)hmReimbursementMap.get(strEmpIdNew);
				if(hmEmpInner==null)hmEmpInner = new HashMap<String, String>();
				
				double dblAmount = uF.parseToDouble((String)hmEmpInner.get(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"))+"_"+rs.getString("reimbursement_info"));
				dblAmount += rs.getDouble("reimbursement_amount");
				hmEmpInner.put(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM")+"_"+rs.getString("reimbursement_info"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				
				hmReimbursementMap.put(strEmpIdNew, hmEmpInner);
				
				if(!alReimbursementType.contains(rs.getString("reimbursement_info"))){
					alReimbursementType.add(rs.getString("reimbursement_info"));
				}
				
				hmReimbursementType.put(strEmpIdNew, alReimbursementType);
				strEmpIdOld  = strEmpIdNew ;
			}
			rs.close();
			pst.close();
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmReimbursementMap.keySet().iterator();
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmReimbursementMap.get(strEmpId);
				if(hmInner==null)hmInner = new HashMap<String, String>();
				
				List<String> alReimType = (List<String>)hmReimbursementType.get(strEmpId);
				if(alReimType==null)alReimType = new ArrayList<String>();
				
				for(int i=0; i<alReimType.size(); i++){
					List<String> alInner = new ArrayList<String>();
					alInner.add(uF.showData(hmEmpCode.get(strEmpId), ""));
					alInner.add(uF.showData(hmEmpName.get(strEmpId), ""));
					alInner.add(uF.showData(hmEmpPanNo.get(strEmpId), ""));
					alInner.add(uF.showData(alReimType.get(i), ""));
					
					for(int m=0; m<alMonth.size(); m++){
						String strAmount = (String)hmInner.get((String)alMonth.get(m)+"_"+(String)alReimType.get(i));
						alInner.add(uF.showData(strAmount, "0"));
					}
					reportList.add(alInner);
				}
			}
//			System.out.println("reportList======>"+reportList);
			request.setAttribute("reportList", reportList);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmReimbursementMap", hmReimbursementMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPanNo", hmEmpPanNo);
			request.setAttribute("hmReimbursementType", hmReimbursementType);
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

	public String getReimbursementType() {
		return reimbursementType;
	}

	public void setReimbursementType(String reimbursementType) {
		this.reimbursementType = reimbursementType;
	}
	
}
