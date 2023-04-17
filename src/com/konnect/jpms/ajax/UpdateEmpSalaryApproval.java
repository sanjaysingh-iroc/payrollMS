package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.salary.EmpSalaryApproval;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateEmpSalaryApproval extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3066136285346836838L;
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	String strEmpId;
	String strEffectiveDate;
	String strEntryDate;
	String status;
	String strAction = null;
	String strBaseUserType = null;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);//Created By Dattatray 10-06-2022
		
		//Created By Dattatray 10-06-2022
		UtilityFunctions uF =new UtilityFunctions();
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		
		updateEmpSalaryApproval();
		
		loadPageVisitAuditTrail(CF, uF);//Created By Dattatray 10-06-2022
		return SUCCESS;
	}
	
	//Created By Dattatray 10-06-2022
	private void loadPageVisitAuditTrail(CommonFunctions CF,UtilityFunctions uF) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder builder = new StringBuilder();
			builder.append("Salary approved empID is :"+strEmpId);
			
			CF.pageVisitAuditTrail(con, CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
		
	public void updateEmpSalaryApproval(){

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update emp_salary_details set is_approved = ?, approved_by =?, approved_date=? where emp_id =? and entry_date=? and effective_date=?");
			if(uF.parseToInt(getStatus())==1){
				pst.setBoolean(1, true);
			}else{
				pst.setBoolean(1, false);
			}
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			pst.setDate(5, uF.getDateFormat(getStrEntryDate(), DBDATE));
			pst.setDate(6, uF.getDateFormat(getStrEffectiveDate(), DBDATE));
			int x=pst.executeUpdate(); 
            pst.close();
			
			if(uF.parseToBoolean(CF.getIsArrear()) && uF.parseToInt(getStatus())==1 && x>0){
				CF.insertNewActivity(con, CF, uF, 16, strEmpId, strSessionEmpId, ""); //16 is for New Salary
				insertNewArrear(con, uF);
			}
			
			if(x > 0 && uF.parseToInt(getStatus()) == 1){
				/**
				 * Calaculate CTC
				 * */
				Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, getStrEmpId());
				
				MyProfile myProfile = new MyProfile();
				myProfile.session = session;
				myProfile.request = request;
				myProfile.CF = CF;
				int intEmpIdReq = uF.parseToInt(getStrEmpId());
				myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
				
				double grossAmount = 0.0d;
				double grossYearAmount = 0.0d;
				double deductAmount = 0.0d;
				double deductYearAmount = 0.0d;
				double netAmount = 0.0d;
				double netYearAmount = 0.0d;
				
				List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
				for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if(innerList.get(1).equals("E")) {
						grossAmount +=uF.parseToDouble(innerList.get(2));
						grossYearAmount +=uF.parseToDouble(innerList.get(3));
					} else if(innerList.get(1).equals("D")) {
						double dblDeductMonth = 0.0d;
						double dblDeductAnnual = 0.0d;
						if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else {
							dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
						}
						deductAmount += dblDeductMonth;
						deductYearAmount += dblDeductAnnual;
					}
				}
				
				Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
				if(hmContribution == null) hmContribution = new HashMap<String, String>();
				double dblMonthContri = 0.0d;
				double dblAnnualContri = 0.0d;
				boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
				boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
				boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
				if(isEPF || isESIC || isLWF){
					if(isEPF){
						double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
						double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
						dblMonthContri += dblEPFMonth;
						dblAnnualContri += dblEPFAnnual;
					}
					if(isESIC){
						double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
						double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
						dblMonthContri += dblESIMonth;
						dblAnnualContri += dblESIAnnual;
					}
					if(isLWF){
						double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
						double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
						dblMonthContri += dblLWFMonth;
						dblAnnualContri += dblLWFAnnual;
					}
				}
				
				double dblCTCMonthly = grossAmount + dblMonthContri;
				double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
				
				List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
				if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
				int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
				if(nAnnualVariSize > 0){
					double grossAnnualAmount = 0.0d;
					double grossAnnualYearAmount = 0.0d;
					for(int i = 0; i < nAnnualVariSize; i++){
						List<String> innerList = salaryAnnualVariableDetailsList.get(i);
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAnnualAmount += dblEarnMonth;
						grossAnnualYearAmount += dblEarnAnnual;
					}
					dblCTCMonthly += grossAnnualAmount;
					dblCTCAnnualy += grossAnnualYearAmount;
				}
				
				netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
				netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
	            
				EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
				salaryApproval.request = request;
				salaryApproval.session = session;
				salaryApproval.CF = CF;
				Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetails(con, uF, getStrEmpId());
				
				if(hmPrevCTC == null) hmPrevCTC = new HashMap<String, String>();
				double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
				double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
	            
				pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?," +
						"prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
				pst.setDouble(1, netAmount);
				pst.setDouble(2, netYearAmount);
				pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
				pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
				pst.setDouble(5, dblIncrementMonthAmt);
				pst.setDouble(6, dblIncrementAnnualAmt);
				pst.setInt(7, uF.parseToInt(getStrEmpId()));
				pst.execute();
				pst.close();
			}
			
			request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_tick.png\" width=\"20px\" />");
    

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}

	}
	
	private void insertNewArrear(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs=null;
		try {
			String orgId=CF.getEmpOrgId(con,uF,getStrEmpId());
			
			String[] arr = null;
			arr = CF.getPayCycleFromDate(con,uF.getDateFormat(getStrEffectiveDate(), DBDATE,DATE_FORMAT), CF.getStrTimeZone(), CF,orgId);			
			
			String[] currArr=null;
			currArr=CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(), CF,orgId);
			
			String strStartDate=arr[0];
			String strEndDate=arr[1];
			String strPaycycle=arr[2];
			int month_count=0;
			boolean flag=false;
			for(int i=uF.parseToInt(arr[2]);i<=uF.parseToInt(currArr[2]);i++){
				pst=con.prepareStatement("select paid_to,paycycle from payroll_generation where paycycle=? and emp_id=? limit 1");
				pst.setInt(1, i);
				pst.setInt(2, uF.parseToInt(getStrEmpId()));
				rs=pst.executeQuery();
				while(rs.next()){
					strEndDate=uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT);
					strPaycycle=rs.getString("paycycle");
					flag=true;
					month_count++;
				}
	            rs.close();
	            pst.close();				
			}
			
			if(flag){
				pst=con.prepareStatement("insert into emp_arrear_details(emp_id,is_arrear_paid,start_date,end_date,effective_date,month_count)" +
						"values(?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setBoolean(2, true);
				pst.setDate(3, uF.getDateFormat(strStartDate, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strEndDate, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(getStrEffectiveDate(), DBDATE));
				pst.setInt(6, month_count);
				pst.execute();
	            pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}

	public String getStrEntryDate() {
		return strEntryDate;
	}

	public void setStrEntryDate(String strEntryDate) {
		this.strEntryDate = strEntryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}