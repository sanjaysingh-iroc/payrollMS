package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayLTA extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 334833469273525309L;
	
	CommonFunctions CF = null;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null; 
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	String[] ltaId;
	
	List<FillOrganisation> orgList; 
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillSalaryHeads> salaryHeadList;
	List<FillBank> bankList;
	
	String salaryHead;
	String f_org;
	String exportType;
	String strStartDate;
	String strEndDate;
	String bankAccount;
	String bankAccountType;
	String strApprove;
	String alertID;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/requisitions/PayLTA.jsp");
		request.setAttribute(TITLE, "Pay CTC Variable");
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		if(getStrApprove()!=null && getStrApprove().equalsIgnoreCase("PAY")){
			payLTA(uF);
		} 
		
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
		
		if(getStrStartDate()!=null && (getStrStartDate().trim().equals("") || getStrStartDate().equalsIgnoreCase("NULL"))){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(getStrEndDate()!=null && (getStrEndDate().trim().equals("") || getStrEndDate().equalsIgnoreCase("NULL"))){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(getStrStartDate()==null && getStrEndDate()==null){
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			
			setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
		}
		
		if(uF.parseToInt(getSalaryHead()) > 0){
			getLTAReport(uF);
		}
		return loadData(uF);
	}
	
	private void payLTA(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbApprovedLtaId = null;
			boolean flag = false;
			List<String> alEmpId = new ArrayList<String>();
			for(int i = 0; getLtaId()!=null && i < getLtaId().length; i++){
				String strLtaId = getLtaId()[i];
				if(sbApprovedLtaId == null){
					sbApprovedLtaId = new StringBuilder();
					sbApprovedLtaId.append(strLtaId);
				} else {
					sbApprovedLtaId.append(","+strLtaId);
				}
				pst = con.prepareStatement("update emp_lta_details set is_paid=?,paid_by=?,paid_date=? where emp_lta_id=?");
				pst.setBoolean(1, true);
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(strLtaId));
				int x = pst.executeUpdate();
				pst.close();
				if(x > 0){
					flag = true;
					
					pst = con.prepareStatement("select * from emp_lta_details where emp_lta_id = ?");
					pst.setInt(1, uF.parseToInt(strLtaId));
					rs = pst.executeQuery();
					while(rs.next()){
						if(!alEmpId.contains(rs.getString("emp_id"))){
							alEmpId.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
				}
			}
			
			if(flag && sbApprovedLtaId!=null){
				Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
				Map<String, String> hmStates = CF.getStateMap(con);
				Map<String, String> hmCountry = CF.getCountryMap(con);
				String strBankCode = null;
				String strBankName = null;
				String strBankAddress = null;
				Map<String, String> hmBankBranch = new HashMap<String, String>();
				
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
				
				pst = con.prepareStatement("select eld.emp_id,emp_fname, emp_mname,emp_lname, emp_bank_name, emp_bank_acct_nbr,applied_amount, " +
						"paid_date,emp_bank_name2,emp_bank_acct_nbr_2,emp_lta_id from employee_personal_details epd, emp_lta_details eld " +
						"where epd.emp_per_id = eld.emp_id and emp_lta_id in ("+sbApprovedLtaId.toString()+")");
				rs = pst.executeQuery();
				double dblAmount = 0;
				double dblTotalAmount = 0;
				int nMonth = 0;
				int nYear = 0;
				int nCount = 0;
				StringBuilder sbEmpAmountBankDetails = new StringBuilder();
				List<String> alLtaId = new ArrayList<String>();
				while(rs.next()){
					dblAmount = uF.parseToDouble(rs.getString("applied_amount"));
					nMonth = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"));
					nYear = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "yyyy"));
					
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
					sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount)+"</font></td>");
					sbEmpAmountBankDetails.append("</tr>");
					
					if(!alLtaId.contains(rs.getString("emp_lta_id"))){
						alLtaId.add(rs.getString("emp_lta_id"));
					}					
				}
				rs.close();
				pst.close();

				String strContent = null;
				String strName = null;
				
				Map<String, String> hmActivityNode = CF.getActivityNode(con);
				if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
				
				int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
				
				if(nMonth>0 && alLtaId.size() > 0){
					String strLtaIds = StringUtils.join(alLtaId.toArray(),",");
					
//					System.out.println("strBankCode==>"+strBankCode);
					
					pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' " +
							"and trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
					pst.setInt(1, uF.parseToInt(getF_org()));
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						strContent = rs.getString("document_text");
					} 
					rs.close();
					pst.close();
					
//					System.out.println("strContent==>"+strContent);
					
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
				        String strTotalAmt=""+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount);
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
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
						pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
//						System.out.println("pst==>"+pst);
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

						pst = con.prepareStatement("update emp_lta_details set statement_id=? where emp_lta_id in ("+strLtaIds+")");
						pst.setInt(1, nMaxStatementId);
//						System.out.println("pst==>"+pst);
						pst.executeUpdate();
						pst.close();				
					}
				}
			}
			
			/**
			 * User Alerts
			 * */
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			for(int j = 0; j < alEmpId.size(); j++){
				String strEmpId = alEmpId.get(j);
				String strDomain = request.getServerName().split("\\.")[0];
				
				String alertData = "<div style=\"float: left;\"> Payment, CTC Variable has been released by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
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

	private String loadData(UtilityFunctions uF) {   
		
		salaryHeadList = new FillSalaryHeads(request).fillLTASalaryHeads(getF_org(), getF_level());
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
		
		alFilter.add("SAL_HEAD");
		if(getSalaryHead()!=null)  {
			String strSalaryHead="";
			int k=0;
			for(int i=0;salaryHeadList!=null && i<salaryHeadList.size();i++){
				if(getSalaryHead().equals(salaryHeadList.get(i).getSalaryHeadId())) {
					if(k==0) {
						strSalaryHead=salaryHeadList.get(i).getSalaryHeadName();
					} else {
						strSalaryHead+=", "+salaryHeadList.get(i).getSalaryHeadName();
					}
					k++;
				}
			}
			if(strSalaryHead!=null && !strSalaryHead.equals("")) {
				hmFilter.put("SAL_HEAD", strSalaryHead);
			} else {
				hmFilter.put("SAL_HEAD", "Select Salary Head");
			}
			
		} else {
			hmFilter.put("SAL_HEAD", "Select Salary Head");
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
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	public void getLTAReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? limit 1");
			pst.setInt(1, uF.parseToInt(getSalaryHead()));
			rs = pst.executeQuery();
			String strSalaryHeadName = null;
			while(rs.next()){
				strSalaryHeadName = rs.getString("salary_head_name");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			if(hmEmpNames == null) hmEmpNames = new HashMap<String, String>();
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_lta_details where emp_id>0 and is_approved=1 and salary_head_id=? and entry_date between ? and ? ");

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
            sbQuery.append(" order by entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getSalaryHead()));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			while(rs.next()){
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("emp_lta_id"));
				alInner.add(rs.getString("emp_id"));
				alInner.add(hmEmpCode.get(rs.getString("emp_id")));
				alInner.add(hmEmpNames.get(rs.getString("emp_id")));
//				alInner.add(uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")), ""));
				alInner.add(uF.showData(strSalaryHeadName, ""));
				alInner.add(""+uF.parseToBoolean(rs.getString("is_paid")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(strCurrSymbol+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("applied_amount"))));
				alInner.add(rs.getString("lta_purpose"));
								
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					StringBuilder sbDoc = new StringBuilder();
					if(CF.getStrDocRetriveLocation()==null){
						sbDoc.append("<a target=\"blank\" href=\"" + request.getContextPath()+DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}else{
						sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
					alInner.add(sbDoc.toString());
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
			}
			rs.close();
			pst.close();

			request.setAttribute("alReport", alReport);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getSalaryHead() {
		return salaryHead;
	}

	public void setSalaryHead(String salaryHead) {
		this.salaryHead = salaryHead;
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

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
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

	public String[] getLtaId() {
		return ltaId;
	}

	public void setLtaId(String[] ltaId) {
		this.ltaId = ltaId;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
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
