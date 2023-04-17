package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpGratuityReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public CommonFunctions CF;
	private static Logger log = Logger.getLogger(EmpGratuityReport.class);
	
	String strUserType;  
	String strSessionEmpId;
	private String paycycle;
	private String f_org;
	private String exportType;
	private String bankAccount;
	private String bankAccountType;
	private String strApprove;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	private String[] f_service;
	private String[] empId;
	
	private List<FillOrganisation> orgList; 
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillBank> bankList;
	
	public String execute() throws Exception {		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PReportEmpGratuity);
		request.setAttribute(TITLE, "Pay Gratuity");
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId =  (String)session.getAttribute(EMPID);
		
		if(getF_org()==null){
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
		
		String[] strPayCycleDates = null;
		if (getPaycycle() == null) {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		if(getStrApprove()!=null && getStrApprove().equalsIgnoreCase("PAY")){
			payGratuity(uF);
		}
		
		viewEmpGratuity(uF);			
		return loadEmpGratuity(uF);
	} 

	private void payGratuity(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String[] strPayCycleDates = getPaycycle().split("-");
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			
			StringBuilder sbApprovedEmpId = null;
			boolean flag = false;
			List<String> alEmpIds = new ArrayList<String>();
			List<String> alEmpGratuityId = new ArrayList<String>();
			for(int i = 0; getEmpId()!=null && i < getEmpId().length; i++){
				String strEmpId = getEmpId()[i];
				if(sbApprovedEmpId == null){
					sbApprovedEmpId = new StringBuilder();
					sbApprovedEmpId.append(strEmpId);
				} else {
					sbApprovedEmpId.append(","+strEmpId);
				}
				
				flag = true;
				if(!alEmpIds.contains(strEmpId)){
					alEmpIds.add(strEmpId);
				}
				
				String strGratuityAmount = (String) request.getParameter("strGratuity_"+strEmpId);
				
				pst = con.prepareStatement("insert into emp_gratuity_details(emp_id,gratuity_amount,paid_from,paid_to,paycycle,added_by,entry_date," +
						"currency_id,is_fullandfinal) values(?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDouble(2, uF.parseToDouble(strGratuityAmount));
				pst.setDate(3,uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(4,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(8, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
				pst.setBoolean(9, false);
		//		System.out.println("pst======>"+pst);
				int x = pst.executeUpdate();
		        pst.close();
		        
		        if(x > 0){
		        	pst = con.prepareStatement("select max(emp_gratuity_id) as emp_gratuity_id from emp_gratuity_details where emp_id = ?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
					while(rs.next()){
						if(!alEmpGratuityId.contains(rs.getString("emp_gratuity_id"))){
							alEmpGratuityId.add(rs.getString("emp_gratuity_id"));
						}
					}
					rs.close();
					pst.close();
		        }
				
			}
			
			if(flag && sbApprovedEmpId!=null && alEmpGratuityId.size() > 0){
				Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
				Map<String, String> hmStates = CF.getStateMap(con);
				Map<String, String> hmCountry = CF.getCountryMap(con);
				String strBankCode = null;
				String strBankName = null;
				String strBankAddress = null;
				Map<String, String> hmBankBranch = new HashMap<String, String>();
				String strEmpGratuityIds = StringUtils.join(alEmpGratuityId.toArray(),",");
				
				pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch, bd.branch_code, bd.bank_address, bd.bank_city, bd.bank_pincode, bd.bank_state_id, bd.bank_country_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id");
				rs = pst.executeQuery();
				while(rs.next()){
					if(rs.getInt("branch_id")==uF.parseToInt(getBankAccount())){
						strBankCode = rs.getString("branch_code");
						strBankName = rs.getString("bank_name");
						strBankAddress = rs.getString("bank_address")+"<br/>"+rs.getString("bank_city")+" - "+rs.getString("bank_pincode")+"<br/>"+uF.showData(hmStates.get(rs.getString("bank_state_id")), "")+", "+uF.showData(hmCountry.get(rs.getString("bank_country_id")), "");
					}
					hmBankBranch.put(rs.getString("branch_id"), rs.getString("bank_branch")+"["+rs.getString("branch_code")+"]");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select egd.emp_id,emp_fname,emp_mname, emp_lname, emp_bank_name, emp_bank_acct_nbr,egd.gratuity_amount, " +
						"egd.entry_date,emp_bank_name2,emp_bank_acct_nbr_2,egd.emp_gratuity_id from employee_personal_details epd, emp_gratuity_details egd " +
						"where epd.emp_per_id = egd.emp_id and egd.emp_gratuity_id in ("+strEmpGratuityIds.toString()+")");
				rs = pst.executeQuery();
				double dblAmount = 0;
				double dblTotalAmount = 0;
				int nMonth = 0;
				int nYear = 0;
				int nCount = 0;
				StringBuilder sbEmpAmountBankDetails = new StringBuilder();
				List<String> alEmpGratId = new ArrayList<String>();
				while(rs.next()){
					dblAmount = uF.parseToDouble(rs.getString("gratuity_amount"));
					nMonth = uF.parseToInt(uF.getDateFormat(rs.getString("entry_date"), DBDATE, "MM"));
					nYear = uF.parseToInt(uF.getDateFormat(rs.getString("entry_date"), DBDATE, "yyyy"));
					
					dblTotalAmount+=dblAmount;
					
					String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"),"");
					String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")),"");
					if(uF.parseToInt(getBankAccountType()) == 2){
						strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr_2"),"");
						strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name2")),"");
					}
					
					sbEmpAmountBankDetails.append("<tr>");
					sbEmpAmountBankDetails.append("<td><font size=\"1\">"+ ++nCount+".</font></td>");
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),"")+"</font></td>");
					sbEmpAmountBankDetails.append("<td><font size=\"1\">"+strBankAccNo+"</font></td>");
					sbEmpAmountBankDetails.append("<td><font size=\"1\">"+strBankBranch+"</font></td>");
					sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">"+uF.formatIntoTwoDecimal(dblAmount)+"</font></td>");
					sbEmpAmountBankDetails.append("</tr>");
					
					if(!alEmpGratId.contains(rs.getString("emp_gratuity_id"))){
						alEmpGratId.add(rs.getString("emp_gratuity_id"));
					}					
				}
				rs.close();
				pst.close();

				String strContent = null;
				String strName = null;
				
				Map<String, String> hmActivityNode = CF.getActivityNode(con);
				if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
				
				int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
				
				if(nMonth>0 && alEmpGratId.size() > 0){
					String strEmpGratIds = StringUtils.join(alEmpGratId.toArray(),",");
					
					pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' " +
							"and trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
					pst.setInt(1, uF.parseToInt(getF_org()));
					rs = pst.executeQuery();
					while(rs.next()){
						strContent = rs.getString("document_text");
					} 
					rs.close();
					pst.close();
					
					if(strContent!=null && strContent.indexOf("["+strBankCode+"]")>=0){
						strContent = strContent.replace("["+strBankCode+"]", strBankName +"<br/>"+strBankAddress);
					}
					
					if(strContent!=null && strContent.indexOf(DATE)>=0){
						strContent = strContent.replace(DATE, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
					}
					
					if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT)>=0){
						strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
					}
					
					if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT_WORDS)>=0){
						String digitTotal="";
				        String strTotalAmt=""+dblTotalAmount;
				        if(strTotalAmt.contains(".")){
				        	strTotalAmt=strTotalAmt.replace(".", ",");
				        	String[] temp=strTotalAmt.split(",");
				        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
				        	if(uF.parseToInt(temp[1])>0){
				        		int pamt=0;
				        		if(temp[1].length()==1){
				        			pamt=uF.parseToInt(temp[1]+"0");
				        		}else{
				        			pamt=uF.parseToInt(temp[1]);
				        		}
				        		digitTotal+=" and "+uF.digitsToWords(pamt)+" paise";
				        	}
				        }else{
				        	int totalAmt1=(int)dblTotalAmount;
				        	digitTotal=uF.digitsToWords(totalAmt1);
				        }
				        strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
					}
					
					if(strContent!=null && strContent.indexOf(PAY_MONTH)>=0){
						strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
					}
					
					if(strContent!=null && strContent.indexOf(PAY_YEAR)>=0){
						strContent = strContent.replace(PAY_YEAR, ""+nYear);
					}
					
					if(strContent!=null && strContent.indexOf(LEGAL_ENTITY_NAME)>=0){
						strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
					}
					
					if(strContent!=null && nMonth>0){
						StringBuilder sbEmpBankDetails = new StringBuilder();
						
						sbEmpBankDetails.append("<table width=\"100%\">");
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
						sbEmpBankDetails.append("<td><b>Name</b></td>");
						sbEmpBankDetails.append("<td><b>Account No</b></td>");
						sbEmpBankDetails.append("<td><b>Branch</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
						sbEmpBankDetails.append("</tr>");
						
						sbEmpBankDetails.append(sbEmpAmountBankDetails);
						
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>"+uF.formatIntoTwoDecimal(dblTotalAmount)+"</b></td>");
						sbEmpBankDetails.append("</tr>");
						
						sbEmpBankDetails.append("</table>");
						
						strName = "BankStatement_"+nMonth+"_"+nYear;
						
						pst = con.prepareStatement("insert into payroll_bank_statement (statement_name, statement_body, generated_date, generated_by, payroll_amount) values (?,?,?,?,?)");
						pst.setString(1, strName);
						pst.setString(2, strContent+""+sbEmpBankDetails.toString());
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
						pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
						pst.execute();
						pst.close();
						
						pst  = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
						rs = pst.executeQuery();
						int nMaxStatementId = 0;
						while(rs.next()){
							nMaxStatementId = rs.getInt("statement_id");
						}
						rs.close();
						pst.close();

						pst = con.prepareStatement("update emp_gratuity_details set statement_id=? where emp_gratuity_id in ("+strEmpGratIds+")");
						pst.setInt(1, nMaxStatementId);
						pst.executeUpdate();
						pst.close();				
					}
				}
			}
			
			/**
			 * User Alerts
			 * */
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			for(int j = 0; j < alEmpIds.size(); j++){
				String strEmpId = alEmpIds.get(j);
				String strDomain = request.getServerName().split("\\.")[0];
				
				String alertData = "<div style=\"float: left;\"> Payment, Gratuity has been released by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "MyPay.action?pType=WR";
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmpId);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadEmpGratuity(UtilityFunctions uF){
		bankList = new FillBank(request).fillBankAccNoForDocuments(CF,uF,getF_org());
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
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public String viewEmpGratuity(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String[] strPayCycleDates = getPaycycle().split("-");
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con,strPayCycleDates[0],strPayCycleDates[1], CF, uF,hmWeekEndHalfDates,null);
			
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,strPayCycleDates[0],strPayCycleDates[1],alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency==null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetails(con);
			if(hmCurrencyDetailsMap==null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(gratuity_amount) as amount,emp_id from emp_gratuity_details where paid_from=? and paid_to=? and paycycle=? ");
			sbQuery.append(" and emp_id in (SELECT emp_id FROM employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and joining_date is not null and approved_flag = true");
			
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
			sbQuery.append(") group by emp_id order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpPaidGratuity = new HashMap<String, String>();
			while(rs.next()){
				hmEmpPaidGratuity.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(gratuity_amount) as amount,emp_id from emp_gratuity_details where emp_id>0 ");
			sbQuery.append(" and emp_id in (SELECT emp_id FROM employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and joining_date is not null and approved_flag = true");
			
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
			sbQuery.append(") group by emp_id order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpTotalPaidGratuity = new HashMap<String, String>();
			while(rs.next()){
				hmEmpTotalPaidGratuity.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and joining_date is not null and approved_flag = true");
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
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>();
			while(rs.next()){
				
				String strLevel = hmEmpLevelMap.get(rs.getString("emp_per_id"));			
				String strLocation = hmEmpWlocation.get(rs.getString("emp_per_id"));  
				
				String strGratuityPaid = hmEmpPaidGratuity.get(rs.getString("emp_per_id"));
			
				String strNoOdDays = uF.dateDifference(rs.getString("joining_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE,CF.getStrTimeZone());
				int nTotalNoOfDays = uF.parseToInt(strNoOdDays);
				int nNoOfYears = nTotalNoOfDays / 365;
				int nNoOfMonths = (nTotalNoOfDays % 365) /  30;
				int nNoOfDays = (nTotalNoOfDays % 365) %  30;
				
				if(nNoOfYears<5){
					continue;
				}
				
				String strOrgId = rs.getString("org_id");
				
				double dblAmount = CF.getCalculatedEmpGratuityAmount(con,request, CF, uF, rs.getString("emp_per_id"),strOrgId,strLevel, strLocation, nNoOfYears, nNoOfMonths, getPaycycle(), hmWeekEndHalfDates, hmWeekEnds, alEmpCheckRosterWeektype, hmRosterWeekEndDates);
				if(dblAmount<=0.0d){
					continue;
				}
				
				dblAmount = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblAmount)) - uF.parseToDouble(hmEmpTotalPaidGratuity.get(rs.getString("emp_per_id")));
				
				String currency_id = uF.showData(hmEmpCurrency.get(rs.getString("emp_per_id")), "0");
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(currency_id);
				if(hmCurrency==null) hmCurrency = new HashMap<String, String>();

				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("emp_per_id"));
				alInner.add(rs.getString("empcode"));
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alInner.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add((rs.getString("emp_status")!=null)?rs.getString("emp_status"):"-");
				alInner.add(nNoOfYears+"");
				alInner.add(nNoOfMonths+"");
				alInner.add(nNoOfDays+"");
				alInner.add(uF.showData(hmCurrency.get("SHORT_CURR"), "")+" "+uF.formatIntoComma(dblAmount));
				
				if(strGratuityPaid!=null && uF.parseToDouble(strGratuityPaid)>0){
					alInner.add(uF.showData(hmCurrency.get("SHORT_CURR"), "")+" "+uF.formatIntoComma(uF.parseToDouble(strGratuityPaid)));
					alInner.add("false");
				}else{
					alInner.add("<input type=\"text\" name=\"strGratuity_"+rs.getString("emp_per_id")+"\" id=\"strGratuity_"+rs.getString("emp_per_id")+"\" style=\"height:20px; width:100px !important; text-align:right;\" value=\""+dblAmount+"\"  onkeypress=\"return isNumberKey(event)\"/>");
					alInner.add("true");
				}
				alReport.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public double getGratuityAmount(Connection con, UtilityFunctions uF, int nEmpId) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		double dblGratuity = 0.0d;
		
		try {
			String[] strPayCycleDates = getPaycycle().split("-");
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			String strLevel = hmEmpLevelMap.get(""+nEmpId);			
			String strLocation = hmEmpWlocation.get(""+nEmpId);  
			
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con,strPayCycleDates[0],strPayCycleDates[1], CF, uF,hmWeekEndHalfDates,null);
			
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,strPayCycleDates[0],strPayCycleDates[1],alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency==null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetails(con);
			if(hmCurrencyDetailsMap==null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(gratuity_amount) as amount,emp_id from emp_gratuity_details where paid_from=? and paid_to=? and paycycle=? ");
			sbQuery.append(" and emp_id =? group by emp_id order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			pst.setInt(4, nEmpId);
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpPaidGratuity = new HashMap<String, String>();
			while(rs.next()){
				hmEmpPaidGratuity.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(gratuity_amount) as amount,emp_id from emp_gratuity_details where emp_id>0 ");
			sbQuery.append(" and emp_id =? group by emp_id order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, nEmpId);
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpTotalPaidGratuity = new HashMap<String, String>();
			while(rs.next()){
				hmEmpTotalPaidGratuity.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and joining_date is not null and approved_flag = true and emp_id =? order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, nEmpId);
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				String strGratuityPaid = hmEmpPaidGratuity.get(rs.getString("emp_per_id"));
			
				String strNoOdDays = uF.dateDifference(rs.getString("joining_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE,CF.getStrTimeZone());
				int nTotalNoOfDays = uF.parseToInt(strNoOdDays);
				int nNoOfYears = nTotalNoOfDays / 365;
				int nNoOfMonths = (nTotalNoOfDays % 365) /  30;
				int nNoOfDays = (nTotalNoOfDays % 365) %  30;
				
				if(nNoOfYears<5){
					continue;
				}

				String strOrgId = rs.getString("org_id");				
				double dblAmount = CF.getCalculatedEmpGratuityAmount(con,request, CF, uF, rs.getString("emp_per_id"),strOrgId,strLevel, strLocation, nNoOfYears, nNoOfMonths, getPaycycle(), hmWeekEndHalfDates, hmWeekEnds, alEmpCheckRosterWeektype, hmRosterWeekEndDates);
				if(dblAmount<=0.0d){
					continue;
				}
				
				dblAmount = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblAmount)) - uF.parseToDouble(hmEmpTotalPaidGratuity.get(rs.getString("emp_per_id")));
				dblGratuity = dblAmount;
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblGratuity;
	}
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankAccountType() {
		return bankAccountType;
	}

	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getStrApprove() {
		return strApprove;
	}

	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}

	public String[] getEmpId() {
		return empId;
	}

	public void setEmpId(String[] empId) {
		this.empId = empId;
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
	
}

