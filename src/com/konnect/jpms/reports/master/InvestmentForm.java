package com.konnect.jpms.reports.master;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.TDSProjection;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillSection;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class InvestmentForm extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<FillSection> sectionList;
	CommonFunctions CF=null;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null; 

	private List <FillFinancialYears> financialYearList;
	private List <FillLevel> levelList;
	private List <FillEmployee> employeeList;
	
	private String strEmployeeId;
	private String level;
	private String f_strFinancialYear;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PInvestment);
		request.setAttribute(TITLE, "Compliances");
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		String operation = (String) request.getParameter("operation");
		String investmentDocId = (String) request.getParameter("investmentDocId");
		if(operation != null && operation.trim().equals("deleteDoc") && uF.parseToInt(investmentDocId) > 0){
			removeDocument(uF, investmentDocId);
			return "delete";
		}
		
	//===start parvez date: 30-07-2022===	
		String dataType = (String) request.getParameter("dataType");
//		System.out.println("IF/85--dataType="+dataType+"--operation="+operation);
		if(operation != null && operation.trim().equals("A") && dataType!=null && dataType.trim().equals("ITS")){
			updateIncomeTaxSlab(uF);
			return "delete";
		}
	//===end parvez date: 30-07-2022===	
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			setStrEmployeeId(strSessionEmpId);
		}

		if (getF_strFinancialYear() == null || getF_strFinancialYear().trim().equals("") || getF_strFinancialYear().trim().equalsIgnoreCase("NULL")) {
			String[] strFinancialYearDates = null;
			strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setF_strFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
		} else {
			String str = URLDecoder.decode(getF_strFinancialYear());
			setF_strFinancialYear(str);
		}
		loadInvestment(uF);
		getSelectedFilter(uF);
	//===start parvez date: 30-07-2022===	
		getIncomeTaxSlab();
	//===end parvez date: 30-07-2022===	
		viewInvestment(uF);		
		viewAgreedInvestments(uF);
		getTDSProjection(uF);
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		if (getF_strFinancialYear() != null) {	
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYear = getF_strFinancialYear().split("-");
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYear[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYear[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		if (strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
			alFilter.add("EMP");
			if(getStrEmployeeId()!=null) {
				String strEmployee="";
				
				for(int i=0; employeeList!=null && i<employeeList.size();i++) {
					if(getStrEmployeeId().equals(employeeList.get(i).getEmployeeId())) {
						strEmployee = employeeList.get(i).getEmployeeCode();
					}
				}
				if(strEmployee!=null && !strEmployee.equals("")) {
					hmFilter.put("EMP", strEmployee);
				} else {
					hmFilter.put("EMP", "NA");
				}
			} else {
				hmFilter.put("EMP", "NA");
			}
		}
		
		String selectedFilter= CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private void removeDocument(UtilityFunctions uF, String investmentDocId) {
		Connection con = null;
		PreparedStatement pst=null;
		Database db = new Database();
		db.setRequest(request);
		String docFile = (String) request.getParameter("docFile");
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			pst = con.prepareStatement("delete from investment_documents where investment_doc_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(investmentDocId));
			pst.setInt(2, uF.parseToInt(getStrEmployeeId()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				String docFilePath = (String) request.getParameter("docFilePath");
				
				File file = new File(docFilePath);
				if (file.exists()) {
					boolean flag = file.delete();
					if(flag){
						con.commit();
						session.setAttribute(MESSAGE, SUCCESSM+"You have successfully deleted documents ("+docFile+")."+END);
					} else {
						con.rollback();
						session.setAttribute(MESSAGE, ERRORM+"Colud not delete documents ("+docFile+"). Please,try again."+END);
					}					
				} else {
					con.rollback();
					session.setAttribute(MESSAGE, ERRORM+"Colud not delete documents ("+docFile+"). Please,try again."+END);
				}
			} else {
				con.rollback();
				session.setAttribute(MESSAGE, ERRORM+"Colud not delete documents ("+docFile+"). Please,try again."+END);
			}
			
		} catch (Exception e) {
			try {
				con.rollback();
				session.setAttribute(MESSAGE, ERRORM+"Colud not delete documents ("+docFile+"). Please,try again."+END);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getTDSProjection(UtilityFunctions uF){
		
		TDSProjection tDSProjection=new TDSProjection();
		tDSProjection.setServletRequest(request);
		tDSProjection.CF=CF;
		tDSProjection.setFinancialYear(getF_strFinancialYear());
		if(getStrEmployeeId()!=null)
			tDSProjection.viewTDSNew(uF, getStrEmployeeId());
		
	}

	private void viewAgreedInvestments(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null,pst1=null;
		ResultSet rs= null,rs1= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getF_strFinancialYear() != null) {
				strFinancialYearDates = getF_strFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			List<List<String>> alA = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			con = db.makeConnection(con);
						
			Date sDate = uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,strUserType, strSessionEmpId);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(getStrEmployeeId())) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(getStrEmployeeId()));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(ACCOUNTANT) )) {
				//pst = con.prepareStatement(selectInvestment1);
				pst = con.prepareStatement("SELECT * FROM (SELECT * FROM investment_details WHERE status=? and parent_section=0) asd LEFT JOIN section_details sd ON asd.section_id = sd.section_id order by  asd.fy_from desc, emp_id");
				pst.setBoolean(1, true);
			} else {
//				pst = con.prepareStatement(selectInvestment);
				pst = con.prepareStatement("SELECT *, asd.entry_date as entrydate FROM (SELECT * FROM investment_details WHERE emp_id=? AND status=? and parent_section=0) asd LEFT JOIN section_details sd ON asd.section_id = sd.section_id order by  asd.fy_from desc, emp_id");
				pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
				pst.setBoolean(2, true);
			}
			rs = pst.executeQuery();
//			System.out.println("pst===>"+pst);
			boolean isCurrentDeclaration = false;
			while(rs.next()){
					alInner = new ArrayList<String>();
					alInner.add(rs.getInt("investment_id")+"");
					alInner.add(hmEmpCode.get(rs.getString("emp_id")));
					alInner.add(hmEmpName.get(rs.getString("emp_id")));
					alInner.add(uF.getDateFormat(rs.getString("fy_from"), DBDATE, "yy")+"-"+uF.getDateFormat(rs.getString("fy_to"), DBDATE, "yy"));
					alInner.add(rs.getString("section_code"));
					alInner.add(rs.getString("amount_paid"));
					alInner.add(uF.getDateFormat(rs.getString("agreed_date"), DBDATE, CF.getStrReportDateFormat()));
					alA.add(alInner);
					
					if(strUserType.equalsIgnoreCase(EMPLOYEE) && rs.getString("emp_id").equalsIgnoreCase(strSessionEmpId) && uF.isDateBetween(sDate, eDate, uF.getDateFormatUtil(rs.getString("agreed_date"), DBDATE))){
						isCurrentDeclaration = true;
					}						
			}
			rs.close();
			pst.close();
			request.setAttribute("reportListA", alA);
			request.setAttribute("isCurrentDeclaration", isCurrentDeclaration+"");
			
			pst = con.prepareStatement("select * from investment_documents where emp_id=? order by section_id, fy_from");
			pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
			rs = pst.executeQuery();  
			Map<String, List<String>> hmSectionDetails = new HashMap<String, List<String>>();
			List<String> alSection = new ArrayList<String>();
			String strSectionNew = null;
			String strSectionOld = null;
			String strFYStartNew = null;
			String strFYStartOld = null;
			while(rs.next()){
				strSectionNew = rs.getString("section_id");
				strFYStartNew = rs.getString("fy_from");
				if((strSectionNew!=null && !strSectionNew.equalsIgnoreCase(strSectionOld)) || (strFYStartNew!=null && !strFYStartNew.equalsIgnoreCase(strFYStartOld))){
					alSection = new ArrayList<String>();
				}
				alSection.add(rs.getString("document_name"));
				hmSectionDetails.put(strSectionNew+"_"+rs.getString("fy_from")+"_"+rs.getString("fy_to"), alSection);
				strSectionOld = strSectionNew;
				strFYStartOld = strFYStartNew;
			}
			rs.close();
			pst.close();
			
//			System.out.println("agree hmSectionDetails===>"+hmSectionDetails);
			
			//pst = con.prepareStatement(selectInvestmentEmp);
			pst=con.prepareStatement("SELECT sum(amount_paid) as amount_paid, fy_from, fy_to, agreed_date FROM investment_details WHERE emp_id =? " +
					"AND status = ? and parent_section=0 group by fy_from, fy_to, agreed_date");
			pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
			pst.setBoolean(2, true);
			rs = pst.executeQuery();
			List<String> alPastInvestments = new ArrayList<String>();
			while(rs.next()){
				
//			pst1 = con.prepareStatement(selectInvestmentEmp1);
				pst1=con.prepareStatement("SELECT * FROM (SELECT * FROM investment_details WHERE emp_id =? AND status = ? and fy_from = ? and fy_to=? " +
						"and trail_status=1 and parent_section=0) asd right JOIN section_details sd ON asd.section_id = sd.section_id " +
						"where sd.financial_year_start=? and sd.financial_year_end =?");
				pst1.setInt(1, uF.parseToInt(getStrEmployeeId()));
				pst1.setBoolean(2, true);
				pst1.setDate(3, rs.getDate("fy_from"));
				pst1.setDate(4, rs.getDate("fy_to"));
				pst1.setDate(5, rs.getDate("fy_from"));
				pst1.setDate(6, rs.getDate("fy_to"));
				rs1 = pst1.executeQuery();
				
				StringBuilder sbSectionDocs = new StringBuilder();
				StringBuilder sb = new StringBuilder("<table width=\"90%\">");
				int ii=0;
				while(rs1.next()){
					List alSectionId = (List)hmSectionDetails.get(rs1.getString("section_id")+"_"+rs.getDate("fy_from")+"_"+rs.getDate("fy_to"));
//					System.out.println("alSectionId===>"+alSectionId);
					sbSectionDocs.replace(0, sbSectionDocs.length(), "");
					for(int i=0; alSectionId!=null && i<alSectionId.size(); i++){
						if(CF.getStrDocRetriveLocation()==null){
							sbSectionDocs.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + alSectionId.get(i) + "\" title=\""+alSectionId.get(i)+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
						}else{
							sbSectionDocs.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getStrEmployeeId() +"/"+ alSectionId.get(i) + "\" title=\""+alSectionId.get(i)+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
						}
					}
					
					sb.append(
						((ii==0)?
								"<tr>" +
								"<td class=\"reportHeading\">Section Code</td>" +
								"<td class=\"reportHeading\">Amount</td>" +
								"<td class=\"reportHeading\">Documents</td>" +
							"</tr>"
							:"")+
							"<tr>" +
								"<td class=\"reportLabel\">"+rs1.getString("section_code")+"</td>" +
								"<td class=\"reportLabel\" style=\"text-align:right;padding-right:5px;\">"+strCurrency+uF.showData(rs1.getString("amount_paid"), "0")+"</td>" +
								"<td class=\"reportLabel\">"+sbSectionDocs.toString()+"</td>" +
							"</tr>");
					ii++;
				}
				
				sb.append("</table>");				
				pst1.close();
				rs1.close();
				
				alPastInvestments.add("<a href=\"javascript:void(0);\" onclick=\"return hs.htmlExpand(this);\">" +
						"FYI- "+uF.getDateFormat(rs.getString("fy_from"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("fy_to"), DBDATE, "yy")+", updated on "+uF.getDateFormat(rs.getString("agreed_date"), DBDATE, CF.getStrReportDateFormat())+"<br/>["+strCurrency+" "+uF.formatIntoOneDecimal(rs.getDouble("amount_paid"))+"]" +
								"</a>" +
								"" +
								"<div class=\"highslide-maincontent\">"+
								"<h3>Investments declared on "+uF.getDateFormat(rs.getString("agreed_date"), DBDATE, CF.getStrReportDateFormat())+" for financial year "+uF.getDateFormat(rs.getString("fy_from"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("fy_to"), DBDATE, "yy")+"</h3>"+
								sb.toString()+
								"</div>" +
								"");
			}
			rs.close();
			pst.close();
			request.setAttribute("alPastInvestments", alPastInvestments);
			
//			System.out.println("alPastInvestments==>"+alPastInvestments);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}


	Map hmSectionMap = new HashMap();
	public void loadInvestment(UtilityFunctions uF){	
		
		String[] strFinancialYearDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		if (getF_strFinancialYear() != null) {
			strFinancialYearDates = getF_strFinancialYear().split("-");
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];
		}
		
		sectionList = new FillSection(request).fillSectionFinancialYear(strFinancialYearStart,strFinancialYearEnd);
		request.setAttribute("sectionList", sectionList);
		if(sectionList!=null && sectionList.size() > 0){
			String sectionId;
			String sectionName;
			int i=0;
			StringBuilder sbSectionList = new StringBuilder();
			sbSectionList.append("{");
			for(i=0; sectionList!=null && i<sectionList.size()-1;i++ ) {
	    		sectionId = (sectionList.get(i)).getSectionId();
	    		sectionName = sectionList.get(i).getSectionCode();
	    		sbSectionList.append("\""+ sectionId+"\":\""+sectionName+"\",");
	    		
	    		hmSectionMap.put(sectionId, sectionName);
			}
			
			if(sectionList!=null){
				sectionId = (sectionList.get(i)).getSectionId();
				sectionName = sectionList.get(i).getSectionCode();
				hmSectionMap.put(sectionId, sectionName);
				sbSectionList.append("\""+ sectionId+"\":\""+sectionName+"\"");
				sbSectionList.append("}");
			}
			
			request.setAttribute("sbSectionList", sbSectionList.toString());
			request.setAttribute("hmSectionMap", hmSectionMap);
		}
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		levelList = new FillLevel(request).fillLevel();
//		employeeList = new FillEmployee(request).fillEmployeeName(null, null, session);
//		if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)){
//			employeeList = new FillEmployee(request).fillEmployeeName(strUserType, null, session);
			employeeList = getEmployeeList(uF,strFinancialYearStart,strFinancialYearEnd, (String)session.getAttribute(ORG_ACCESS), (String)session.getAttribute(WLOCATION_ACCESS));
//		} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(CEO))){
//			employeeList = new FillEmployee(request).fillEmployeeNameByAccess(null, (String)session.getAttribute(ORG_ACCESS),null, (String)session.getAttribute(WLOCATION_ACCESS),strUserType,false);
//		} else {
//			employeeList = new ArrayList<FillEmployee>();
//		}
		
		
		
	}
	
	private List<FillEmployee> getEmployeeList(UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, String strOrgAccess, String strWLocationAccess) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con,request);//Created By Dattatray Date:21-10-21
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)){
				pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod " +
						"WHERE epd.emp_per_id = eod.emp_id and (employment_end_date is null OR employment_end_date >= ?) and joining_date<= ? order by emp_fname");
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getInt("emp_per_id") < 0) {
						continue;
					}
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					al.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname")+" [" + rs.getString("empcode") + "]"));
				}
				rs.close();
				pst.close();
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strWLocationAccess!=null && !strWLocationAccess.equals("null") && !strWLocationAccess.equals("")) {
				pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					"and  org_id in ("+strOrgAccess+") and wlocation_id in ("+ strWLocationAccess + ")order by emp_fname, emp_lname");
				rs = pst.executeQuery();
				while (rs.next()) {
					
					String strEmpMName = "";
					
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					al.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname")+" [" + rs.getString("empcode") + "]"));
				}
				rs.close();
				pst.close();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public String viewInvestment(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getF_strFinancialYear() != null) {
				strFinancialYearDates = getF_strFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con); 
			
			String slabType = CF.getEmpIncomeTaxSlabType(con, CF, getStrEmployeeId(), strFinancialYearStart, strFinancialYearEnd);
			request.setAttribute("slabType", slabType);
//			String slabType = hmEmpSlabMap.get(getStrEmployeeId());
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);//Created By Dattatray Date:21-10-21
			request.setAttribute("hmFeatureStatus",hmFeatureStatus);//Created By Dattatray Date:21-10-21
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			request.setAttribute("strFinancialYearStart",strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd",strFinancialYearEnd);
			
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd " +
					"WHERE eod.emp_id > 0 and epd.emp_per_id=eod.emp_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
			rs = pst.executeQuery();
			while(rs.next()){
				//String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+ " " : "";
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpName = rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname");
				request.setAttribute("strEmpName",strEmpName);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from form16_documents where financial_year_start=? and financial_year_end=? and emp_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrEmployeeId()));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			boolean flag = false;
			while(rs.next()){
				flag = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("isApproveRelease", ""+flag);
			
			pst = con.prepareStatement("select * from investment_documents where fy_from =? and fy_to =? and emp_id=? and section_id >0 order by section_id");
			/*pst.setDate(1, uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(CF.getStrFinancialYearTo(), DATE_FORMAT));*/
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrEmployeeId()));
//			System.out.println("fsdfg pst=======>"+pst);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmSectionDetails = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alSectionOuter = new ArrayList<List<String>>();
			String strSectionNew = null;
			String strSectionOld = null;
			while(rs.next()){
				strSectionNew = rs.getString("section_id");
				if(strSectionNew!=null && !strSectionNew.equalsIgnoreCase(strSectionOld)){
					alSectionOuter = new ArrayList<List<String>>();
				}
				List<String> alSection = new ArrayList<String>();
				alSection.add(rs.getString("investment_doc_id"));
				alSection.add(rs.getString("document_name")); 
				
				alSectionOuter.add(alSection);
				hmSectionDetails.put(strSectionNew, alSectionOuter);
				strSectionOld = strSectionNew;
			}
			rs.close();
			pst.close();
//			System.out.println("fsdfg hmSectionDetails=======>"+hmSectionDetails);
			
			Map<String, String> hmInvestment = new LinkedHashMap<String, String>();
			Map<String, String> hmInvestmentId = new LinkedHashMap<String, String>();
			Map<String, String> hmInvestmentStatus = new LinkedHashMap<String, String>();
			Map<String, String> hmInvestmentDocuments = new LinkedHashMap<String, String>();
			Map<String, String> hmUnderSection = new LinkedHashMap<String, String>();
			
			StringBuilder sbSectionDocs = new StringBuilder();
			pst = con.prepareStatement("select * from (SELECT *, asd.entry_date as entrydate, sd.section_id as sectionid  FROM " +
				"(SELECT * FROM investment_details WHERE emp_id =? and fy_from =? and fy_to =? and trail_status=1 and parent_section=0) asd " +
				"RIGHT JOIN section_details sd ON asd.section_id = sd.section_id where sd.financial_year_start=? and sd.financial_year_end=? " +
				" and (sd.slab_type=? or sd.slab_type=2) order by asd.fy_from desc, emp_id,sd.under_section) as a order by under_section,sectionid, status");
			pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(slabType));
//			System.out.println("fsdfg pst=======>"+pst);
			rs = pst.executeQuery();
			
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("investment_id"));
				alInner.add(rs.getString("section_code"));
				alInner.add(rs.getString("amount_paid"));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				List<List<String>> alSectionOuterDoc = (List<List<String>>) hmSectionDetails.get(rs.getString("section_id"));
				sbSectionDocs.replace(0, sbSectionDocs.length(), "");
				for(int i=0; alSectionOuterDoc!=null && i<alSectionOuterDoc.size(); i++){
					List<String> alSectionId = alSectionOuterDoc.get(i);
					if(CF.getStrDocRetriveLocation()==null){
						sbSectionDocs.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + alSectionId.get(1) + "\" title=\""+alSectionId.get(1)+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
					}else{
						sbSectionDocs.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getStrEmployeeId() +"/"+ alSectionId.get(1) + "\" title=\""+alSectionId.get(1)+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
						if(!uF.parseToBoolean(rs.getString("status"))){
							String docFilePath = CF.getStrDocSaveLocation() + I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getStrEmployeeId() +"/"+ alSectionId.get(1);
							sbSectionDocs.append("&nbsp;<a href=\"javascript:void(0);\" title=\"Remove "+alSectionId.get(1)+"\" onclick=\"removeDocument('"+getStrEmployeeId()+"','"+alSectionId.get(0)+"','"+docFilePath+"','"+alSectionId.get(1)+"');\"><img src=\"images1/icons/hd_cross_16x16.png\" style=\"vertical-align: top; width:10px;\"/></a>");
						}
					}
				}
				alInner.add(sbSectionDocs.toString());
				
				if(uF.parseToBoolean(rs.getString("status"))){
					al.add(alInner);
				}
				 
				hmInvestment.put(rs.getString("sectionid"), uF.showData(rs.getString("amount_paid"), "0"));
//				hmInvestmentId.put(rs.getString("investment_id"), rs.getString("section_id"));
				hmInvestmentId.put(rs.getString("sectionid"), rs.getString("investment_id"));
				
				hmInvestmentStatus.put(rs.getString("investment_id"), rs.getString("status"));
				hmInvestmentDocuments.put(rs.getString("sectionid"), sbSectionDocs.toString());
				hmUnderSection.put(rs.getString("sectionid"), CF.getUnderSectionName(uF.parseToInt(rs.getString("under_section"))));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmInvestment======>"+hmInvestment);
//			System.out.println("hmInvestmentId======>"+hmInvestmentId);
//			System.out.println("hmUnderSection======>"+hmUnderSection);
//			System.out.println("al======>"+al.toString());
			
			request.setAttribute("reportList", al);
			request.setAttribute("hmInvestment", hmInvestment);
			request.setAttribute("hmInvestmentId", hmInvestmentId);
			request.setAttribute("hmInvestmentStatus", hmInvestmentStatus);
			request.setAttribute("hmInvestmentDocuments", hmInvestmentDocuments);
			request.setAttribute("hmUnderSection", hmUnderSection);
			
			
			Map<String, List<Map<String, String>>> hmSubInvestment = new HashMap<String, List<Map<String, String>>>();			
			pst = con.prepareStatement("select * from (SELECT *, asd.entry_date as entrydate, sd.section_id as sectionid FROM " +
				"(SELECT * FROM investment_details WHERE emp_id=? and fy_from=? and fy_to=? and trail_status=1 and parent_section>0) asd " +
				"JOIN section_details sd ON asd.section_id=sd.section_id where sd.financial_year_start=? and sd.financial_year_end=? " +
				" and (sd.slab_type=? or sd.slab_type=2) order by asd.fy_from desc, emp_id,sd.under_section) as a order by under_section,sectionid,sub_section_no, status");
			pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(slabType));
//			System.out.println("hmSubInvestment pst =======>"+pst); 
			rs = pst.executeQuery();
			Map<String, List<String>> hmAddedSubSections = new LinkedHashMap<String, List<String>>();
			
			while(rs.next()){
				List<Map<String, String>> alSubInvestment =hmSubInvestment.get(rs.getString("parent_section"));
				if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
				
				List<String> alSubSecId = hmAddedSubSections.get(rs.getString("parent_section"));
				if(alSubSecId == null) alSubSecId = new ArrayList<String>();
				
				Map<String, String> hm = new LinkedHashMap<String, String>();
				hm.put("SECTION_ID", rs.getString("parent_section"));
				hm.put("SECTION_NAME", rs.getString("child_section"));
				hm.put("INVESTMENT_ID", rs.getString("investment_id"));
				hm.put("PAID_AMOUNT", uF.showData(rs.getString("amount_paid"), "0"));
				hm.put("STATUS", rs.getString("status"));
				hm.put("SUB_SECTION_ID", rs.getString("sub_section_no"));
				hm.put("SUB_SECTION_AMOUNT", uF.showData(rs.getString("sub_section_amt"), "0"));
				hm.put("SUB_SECTION_LIMIT_TYPE", uF.showData(rs.getString("sub_section_limit_type"), ""));
				
				alSubInvestment.add(hm);
				hmSubInvestment.put(rs.getString("parent_section"), alSubInvestment);
				if(rs.getInt("sub_section_no")>0) {
					alSubSecId.add(rs.getString("sub_section_no"));
				}
				hmAddedSubSections.put(rs.getString("parent_section"), alSubSecId);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSubInvestment", hmSubInvestment);
			request.setAttribute("hmAddedSubSections", hmAddedSubSections);
			
			
			Map<String, List<Map<String, String>>> hmSubSectionData = new LinkedHashMap<String, List<Map<String, String>>>();
			Map<String, Map<String, Map<String, String>>> hmSubSectionAmount = new LinkedHashMap<String, Map<String, Map<String, String>>>();
			pst = con.prepareStatement("select * from section_details sd where sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(slabType));
//			System.out.println("hmSubInvestment pst =======>"+pst); 
			rs = pst.executeQuery();
			while(rs.next()){
				List<Map<String, String>> alSubSection = new ArrayList<Map<String, String>>();
				Map<String, Map<String, String>> hmSubSecDetails = new LinkedHashMap<String, Map<String, String>>();
				
				Map<String, String> hmSubSecPair = new HashMap<String, String>();
				if(rs.getString("combine_sub_section") != null && rs.getString("combine_sub_section").length()>2) {
					List<String> alCss = Arrays.asList(rs.getString("combine_sub_section").substring(1, rs.getString("combine_sub_section").length()-1).split(","));
					for(int i=0; alCss!=null && i<alCss.size(); i++) {
						String[] str1 = alCss.get(i).split("::::");
						hmSubSecPair.put(str1[0], str1[1]);
					}
				}
				
				if(rs.getString("sub_section_1") != null && !rs.getString("sub_section_1").trim().equals("")) {
					Map<String, String> hm = new HashMap<String, String>();
					hm.put("SUB_SECTION_ID", "1");
					hm.put("SUB_SECTION_NAME", rs.getString("sub_section_1"));
					hm.put("SUB_SECTION_DESC", rs.getString("sub_section_1_description"));
					hm.put("SUB_SECTION_AMOUNT", uF.showData(rs.getString("sub_section_1_amt"), "0"));
					hm.put("SUB_SECTION_LIMIT_TYPE", uF.showData(rs.getString("sub_section_1_limit_type"), ""));
					hm.put("COMBINE_SUB_SECTION", uF.showData(hmSubSecPair.get("1"), ""));
					alSubSection.add(hm);
					hmSubSecDetails.put("1", hm);
				}
				if(rs.getString("sub_section_2") != null && !rs.getString("sub_section_2").trim().equals("")) {
					Map<String, String> hm = new HashMap<String, String>();
					hm.put("SUB_SECTION_ID", "2");
					hm.put("SUB_SECTION_NAME", rs.getString("sub_section_2"));
					hm.put("SUB_SECTION_DESC", rs.getString("sub_section_2_description"));
					hm.put("SUB_SECTION_AMOUNT", uF.showData(rs.getString("sub_section_2_amt"), "0"));
					hm.put("SUB_SECTION_LIMIT_TYPE", uF.showData(rs.getString("sub_section_2_limit_type"), ""));
					hm.put("COMBINE_SUB_SECTION", uF.showData(hmSubSecPair.get("2"), ""));
					alSubSection.add(hm);
					hmSubSecDetails.put("2", hm);
				}
				if(rs.getString("sub_section_3") != null && !rs.getString("sub_section_3").trim().equals("")) {
					Map<String, String> hm = new HashMap<String, String>();
					hm.put("SUB_SECTION_ID", "3");
					hm.put("SUB_SECTION_NAME", rs.getString("sub_section_3"));
					hm.put("SUB_SECTION_DESC", rs.getString("sub_section_3_description"));
					hm.put("SUB_SECTION_AMOUNT", uF.showData(rs.getString("sub_section_3_amt"), "0"));
					hm.put("SUB_SECTION_LIMIT_TYPE", uF.showData(rs.getString("sub_section_3_limit_type"), ""));
					hm.put("COMBINE_SUB_SECTION", uF.showData(hmSubSecPair.get("1"), ""));
					alSubSection.add(hm);
					hmSubSecDetails.put("3", hm);
				}
				if(rs.getString("sub_section_4") != null && !rs.getString("sub_section_4").trim().equals("")) {
					Map<String, String> hm = new HashMap<String, String>();
					hm.put("SUB_SECTION_ID", "4");
					hm.put("SUB_SECTION_NAME", rs.getString("sub_section_4"));
					hm.put("SUB_SECTION_DESC", rs.getString("sub_section_4_description"));
					hm.put("SUB_SECTION_AMOUNT", uF.showData(rs.getString("sub_section_4_amt"), "0"));
					hm.put("SUB_SECTION_LIMIT_TYPE", uF.showData(rs.getString("sub_section_4_limit_type"), ""));
					hm.put("COMBINE_SUB_SECTION", uF.showData(hmSubSecPair.get("4"), ""));
					alSubSection.add(hm);
					hmSubSecDetails.put("4", hm);
				}
				if(rs.getString("sub_section_5") != null && !rs.getString("sub_section_5").trim().equals("")) {
					Map<String, String> hm = new HashMap<String, String>();
					hm.put("SUB_SECTION_ID", "5");
					hm.put("SUB_SECTION_NAME", rs.getString("sub_section_5"));
					hm.put("SUB_SECTION_DESC", rs.getString("sub_section_5_description"));
					hm.put("SUB_SECTION_AMOUNT", uF.showData(rs.getString("sub_section_5_amt"), "0"));
					hm.put("SUB_SECTION_LIMIT_TYPE", uF.showData(rs.getString("sub_section_5_limit_type"), ""));
					hm.put("COMBINE_SUB_SECTION", uF.showData(hmSubSecPair.get("5"), ""));
					alSubSection.add(hm);
					hmSubSecDetails.put("5", hm);
				}
				
				hmSubSectionData.put(rs.getString("section_id"), alSubSection);
				hmSubSectionAmount.put(rs.getString("section_id"), hmSubSecDetails);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSubSectionData", hmSubSectionData);
			request.setAttribute("hmSubSectionAmount", hmSubSectionAmount);
			
//			System.out.println("hmSubInvestment=====>"+hmSubInvestment);
//			System.out.println("hmSubInvestment.size=====>"+hmSubInvestment.size());
			
			
			/**
			 * Other Investment
			 * */
			pst = con.prepareStatement("select * from investment_documents where fy_from =? and fy_to =? and emp_id=? and salary_head_id >0 order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrEmployeeId()));
//			System.out.println("other pst=======>"+pst);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmOtherSectionDoc = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alSectionDocOuter = new ArrayList<List<String>>(); 
			String strSectionDocNew = null;
			String strSectionDocOld = null;
			while(rs.next()){
				strSectionDocNew = rs.getString("salary_head_id");
				if(strSectionDocNew!=null && !strSectionDocNew.equalsIgnoreCase(strSectionDocOld)){
					alSectionDocOuter = new ArrayList<List<String>>();
				}
				List<String> alSectionDoc = new ArrayList<String>();
				alSectionDoc.add(rs.getString("investment_doc_id"));
				alSectionDoc.add(rs.getString("document_name")); 
				
				alSectionDocOuter.add(alSectionDoc);
				hmOtherSectionDoc.put(strSectionDocNew, alSectionDocOuter);
				strSectionDocOld = strSectionDocNew;
			}
			rs.close();
			pst.close();
//			System.out.println("fsdfg hmSectionDetails=======>"+hmSectionDetails);
			
			Map<String, String> hmOtherInvestment = new LinkedHashMap<String, String>();
			Map<String, String> hmOtherInvestmentId = new LinkedHashMap<String, String>();
			Map<String, String> hmOtherInvestmentStatus = new LinkedHashMap<String, String>();
			Map<String, String> hmOtherInvestmentDocuments = new LinkedHashMap<String, String>();
			Map<String, String> hmOtherUnderSection = new LinkedHashMap<String, String>();
			
			StringBuilder sbOtherSectionDocs = new StringBuilder();
			pst = con.prepareStatement("select * from (SELECT *, asd.entry_date as entrydate, ed.salary_head_id as salary_head_id," +
				"ed.salary_head_id as salaryheadid FROM (SELECT * FROM investment_details WHERE emp_id =? and fy_from =? and fy_to =? " +
				"and trail_status=1 and parent_section=0) asd RIGHT JOIN exemption_details ed ON asd.salary_head_id = ed.salary_head_id " +
				"where ed.exemption_from=? and ed.exemption_to=? and (ed.slab_type=? or ed.slab_type=2) and ed.investment_form=true order by asd.fy_from desc, " +
				"emp_id, ed.under_section) as a order by under_section,salaryheadid,status");
			pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(slabType));
//			System.out.println("other invest exemption_details pst=======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alOther = new ArrayList<List<String>>();
			while(rs.next()){
				List<String> alOtherInner = new ArrayList<String>();
				alOtherInner.add(rs.getString("investment_id"));
				alOtherInner.add(rs.getString("exemption_code"));
				alOtherInner.add(rs.getString("amount_paid"));
				alOtherInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				List<List<String>> alSectionOuterDoc = (List<List<String>>)hmOtherSectionDoc.get(rs.getString("salaryheadid"));;
				sbOtherSectionDocs.replace(0, sbOtherSectionDocs.length(), "");
				for(int i=0; alSectionOuterDoc!=null && i<alSectionOuterDoc.size(); i++){
					List<String> alOtherSectionId = alSectionOuterDoc.get(i);
					if(CF.getStrDocRetriveLocation()==null){
						sbOtherSectionDocs.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + alOtherSectionId.get(1) + "\" title=\""+alOtherSectionId.get(1)+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
					}else{
						sbOtherSectionDocs.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getStrEmployeeId() +"/"+ alOtherSectionId.get(1) + "\" title=\""+alOtherSectionId.get(1)+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
						if(!uF.parseToBoolean(rs.getString("status"))){
							String docFilePath = CF.getStrDocSaveLocation() + I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getStrEmployeeId() +"/"+ alOtherSectionId.get(1);
							sbOtherSectionDocs.append("&nbsp;<a href=\"javascript:void(0);\" title=\"Remove "+alOtherSectionId.get(1)+"\" onclick=\"removeDocument('"+getStrEmployeeId()+"','"+alOtherSectionId.get(0)+"','"+docFilePath+"','"+alOtherSectionId.get(1)+"');\"><img src=\"images1/icons/hd_cross_16x16.png\" style=\"vertical-align: top; width:10px;\"/>");
						}
					} 
				}
				alOtherInner.add(sbOtherSectionDocs.toString());
				
				if(uF.parseToBoolean(rs.getString("status"))){
					alOther.add(alOtherInner);
				}
				 
				hmOtherInvestment.put(rs.getString("salaryheadid"), uF.showData(rs.getString("amount_paid"), "0"));
				hmOtherInvestmentId.put(rs.getString("salaryheadid"), rs.getString("investment_id"));
				
				hmOtherInvestmentStatus.put(rs.getString("investment_id"), rs.getString("status"));
				hmOtherInvestmentDocuments.put(rs.getString("salaryheadid"), sbOtherSectionDocs.toString());
				hmOtherUnderSection.put(rs.getString("salaryheadid"), CF.getUnderSectionName(uF.parseToInt(rs.getString("under_section"))));
			}
			rs.close();
			pst.close();
//			System.out.println("hmInvestment ======> "+hmInvestment);
//			System.out.println("hmOtherUnderSection ======> "+hmOtherUnderSection);
//			System.out.println("hmOtherInvestmentId ======> "+hmOtherInvestmentId);
			
//			System.out.println("al======>"+al.toString());	
			request.setAttribute("reportOtherList", alOther);
			request.setAttribute("hmOtherInvestment", hmOtherInvestment);
			request.setAttribute("hmOtherInvestmentId", hmOtherInvestmentId);
			request.setAttribute("hmOtherInvestmentStatus", hmOtherInvestmentStatus);
			request.setAttribute("hmOtherInvestmentDocuments", hmOtherInvestmentDocuments);
			request.setAttribute("hmOtherUnderSection", hmOtherUnderSection);
			
			
			Map<String, List<Map<String, String>>> hmOtherSubInvestment = new LinkedHashMap<String, List<Map<String, String>>>();			
			pst = con.prepareStatement("select * from (SELECT *, asd.entry_date as entrydate, ed.salary_head_id as salary_head_id," +
				"ed.salary_head_id as salaryheadid FROM (SELECT * FROM investment_details WHERE emp_id =? and fy_from =? and fy_to =? " +
				"and trail_status=1 and parent_section>0) asd JOIN exemption_details ed ON asd.salary_head_id = ed.salary_head_id " +
				"where ed.exemption_from=? and ed.exemption_to=? and (ed.slab_type=? or ed.slab_type=2) and ed.investment_form=true " +
				" order by  asd.fy_from desc, emp_id,ed.under_section) as a order by under_section,salaryheadid, status");
			pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(slabType));
//			System.out.println("other sub pst=======>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				List<Map<String, String>> alOtherSubInvestment =hmOtherSubInvestment.get(rs.getString("parent_section"));
				if(alOtherSubInvestment ==null)alOtherSubInvestment = new ArrayList<Map<String, String>>();
				
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("SECTION_ID", rs.getString("parent_section"));
				hm.put("SECTION_NAME", rs.getString("child_section"));
				hm.put("INVESTMENT_ID", rs.getString("investment_id"));
				hm.put("PAID_AMOUNT", uF.showData(rs.getString("amount_paid"), "0"));
				hm.put("STATUS", rs.getString("status"));
				alOtherSubInvestment.add(hm);
				
				hmOtherSubInvestment.put(rs.getString("parent_section"), alOtherSubInvestment);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOtherSubInvestment", hmOtherSubInvestment);
//			System.out.println("hmOtherSubInvestment =====> "+hmOtherSubInvestment);
//			System.out.println("hmOtherSubInvestment.size=====>"+hmOtherSubInvestment.size());
			
			Map<String, String> hmOtherSectionMap = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=? and (slab_type=? or slab_type=2) and investment_form=true");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(slabType));
			rs=pst.executeQuery();
			while(rs.next()){
				hmOtherSectionMap.put(rs.getString("salary_head_id"), rs.getString("exemption_code"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOtherSectionMap", hmOtherSectionMap);
//			System.out.println("hmOtherSectionMap=====>"+hmOtherSectionMap);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
//===created by parvez date: 30-07-2022===
	//===start===
	private void updateIncomeTaxSlab(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			if (getF_strFinancialYear() != null) {
				strFinancialYearDates = getF_strFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setF_strFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			pst = con.prepareStatement("update emp_it_slab_access_details set slab_type=?,updated_by=?, update_date=? where emp_id=? and fyear_start=? and fyear_end=?");
			pst.setInt(1, uF.parseToInt((String)request.getParameter("chboxStandardNew")));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(3, uF.getTimeStamp(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(4, uF.parseToInt(getStrEmployeeId()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("IF/1010---pst="+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x==0) {
//				System.out.println("chboxWeb_ ===>> " + (String)request.getParameter("chboxWeb_"+hideEmpIds[i]));
				pst = con.prepareStatement("insert into emp_it_slab_access_details (emp_id,slab_type,fyear_start,fyear_end,added_by,entry_time) values(?,?,?,?, ?,?)");
				pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
				pst.setInt(2, uF.parseToInt((String)request.getParameter("chboxStandardNew")));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(6, uF.getTimeStamp(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//				System.out.println("IF/1023---pst ===>> " + pst);
				x = pst.executeUpdate();
				pst.close();
			}
			
			if(x>0){
				session.setAttribute(MESSAGE, SUCCESSM+"Income Tax Salb Updated Successfully."+END);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getIncomeTaxSlab(){
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			if (getF_strFinancialYear() != null) {
				strFinancialYearDates = getF_strFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setF_strFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_it_slab_access_details where fyear_start=? and fyear_end=? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			Map<String, String> hmEmpAccessData = new HashMap<String, String>();
			rs = pst.executeQuery();
			while(rs.next()) {
				hmEmpAccessData.put(rs.getString("emp_id"), rs.getString("slab_type"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpAccessData", hmEmpAccessData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
//===end===	

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getF_strFinancialYear() {
		return f_strFinancialYear;
	}

	public void setF_strFinancialYear(String f_strFinancialYear) {
		this.f_strFinancialYear = f_strFinancialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}
	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public List<FillEmployee> getEmployeeList() {
		return employeeList;
	}


	public void setEmployeeList(List<FillEmployee> employeeList) {
		this.employeeList = employeeList;
	}


	public String getStrEmployeeId() {
		return strEmployeeId;
	}


	public void setStrEmployeeId(String strEmployeeId) {
		this.strEmployeeId = strEmployeeId;
	}
}