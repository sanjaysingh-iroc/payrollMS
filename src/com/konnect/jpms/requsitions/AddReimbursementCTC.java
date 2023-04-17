package com.konnect.jpms.requsitions;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillReimbursementCTCHead;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddReimbursementCTC extends ActionSupport  implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	
	private CommonFunctions CF;
	
	
	private String financialYear;
	private String reimbursementCTCHead;	
	private String[] paycycle;
	private String strAmount;
	private String strDescription;
	private File strDocument;
	private String strDocumentFileName;
	private String limitAmount;
	
	private List<FillFinancialYears> financialYearList;
	private List<FillReimbursementCTCHead> reimbursementCTCHeadList;
	private List<FillPayCycles> paycycleList;	
	
	public String execute() {	    
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/requisitions/ReimbursementCTC.jsp");
		request.setAttribute(TITLE, "Reimbursement Part of CTC");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String type = (String) request.getParameter("type");
		if(type != null && type.trim().equalsIgnoreCase("REIMBURSEMENTCTC")){
			return approveDeny(uF);
		}
		
		if (getFinancialYear() == null || getFinancialYear().trim().equals("") || getFinancialYear().trim().equalsIgnoreCase("NULL") || getFinancialYear().trim().length() == 0) {
			String[] strFinancialDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setFinancialYear(strFinancialDates[0] + "-" + strFinancialDates[1]);
		}
		
		if(strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			String submit1 = (String) request.getParameter("submit1");
//			System.out.println("submit1==>"+submit1);
			if(submit1 != null && submit1.trim().equalsIgnoreCase("Submit")){
				return addReimbursementCTC(uF);
			}
		}
		
		loadReimbursementCTC(uF);
		
		
		return LOAD;
	}

	private String approveDeny(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			con = db.makeConnection(con);
			
			String apStatus = (String) request.getParameter("apStatus"); 
			String RCHID = (String) request.getParameter("RCHID");
			String mReason = (String) request.getParameter("mReason");
			String userType = (String) request.getParameter("userType");
			String nonTaxable = (String) request.getParameter("nonTaxable");
			
			pst = con.prepareStatement("update reimbursement_ctc_applied_details set is_approved=?,approved_by=?,approved_date=?," +
					"is_nontaxable=?,approve_reason=?,approver_user_type_id=? where reim_ctc_applied_id=?");
			pst.setInt(1, uF.parseToInt(apStatus));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setBoolean(4, uF.parseToBoolean(nonTaxable));
			pst.setString(5, mReason);
			pst.setInt(6, uF.parseToInt(userType));
			pst.setInt(7, uF.parseToInt(RCHID));
			int x = pst.executeUpdate();
			pst.close();
			
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	

	private String addReimbursementCTC(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {				
				String[] strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			String fileName =null;
			if(getStrDocument()!=null) {
				if(CF.getStrDocSaveLocation()==null) {
					fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
				} else {
					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_REIMBURSEMENTS_CTC_HEAD+"/"+I_DOCUMENT+"/"+strSessionEmpId, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
				} 
			}
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from reimbursement_head_details where reimbursement_head_id=?");
			pst.setInt(1, uF.parseToInt(getReimbursementCTCHead()));
			rs = pst.executeQuery();
			int nReimCTCId = 0;
			while(rs.next()){
				nReimCTCId = uF.parseToInt(rs.getString("reimbursement_ctc_id"));
			}
			rs.close();
			pst.close();
			
			if(nReimCTCId > 0){
				pst = con.prepareStatement("insert into reimbursement_ctc_applied_details (emp_id,is_approved,entry_date,reimbursement_head_id," +
						"description,ref_document,financial_year_start,financial_year_end,is_nontaxable,applied_amount,reimbursement_ctc_id)" +
						" values(?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, 0);
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(getReimbursementCTCHead()));
				pst.setString(5, getStrDescription());
				pst.setString(6, fileName);
				pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setBoolean(9, false);
				pst.setDouble(10, uF.parseToDouble(getStrAmount()));
				pst.setInt(11, nReimCTCId);
				int x = pst.executeUpdate();
				if(x > 0){
					pst = con.prepareStatement("select max(reim_ctc_applied_id) as reim_ctc_applied_id from reimbursement_ctc_applied_details");
					rs = pst.executeQuery();
					int nReimbursementCTCAppliedId = 0;
					while(rs.next()){
						nReimbursementCTCAppliedId = uF.parseToInt(rs.getString("reim_ctc_applied_id"));
					}
					rs.close();
					pst.close();
					
					if (getPaycycle() != null && getPaycycle().length > 0) {
						int nPaycycleSize = getPaycycle().length;
						for(int i = 0;  i < nPaycycleSize; i++){
							String[] strPayCycleDates = getPaycycle()[i].split("-");
							String strD1 = strPayCycleDates[0];
							String strD2 = strPayCycleDates[1];
							String strPC = strPayCycleDates[2];
							
							pst = con.prepareStatement("insert into reimbursement_ctc_applied_paycycle (reim_ctc_applied_id,emp_id,paycycle_from," +
									"paycycle_to,paycycle,financial_year_start,financial_year_end)" +
									" values(?,?,?,?, ?,?,?)");
							pst.setInt(1, nReimbursementCTCAppliedId);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
							pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
							pst.setInt(5, uF.parseToInt(strPC));
							pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
							pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
							pst.execute();	
						}
					}				
					
					session.setAttribute(MESSAGE, SUCCESSM+"Reimbursement CTC applied Successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Reimbursement CTC applied Failed."+END);
				}
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Reimbursement CTC applied Failed."+END);
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Reimbursement CTC applied Failed."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	public String loadReimbursementCTC(UtilityFunctions uF) {
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		getEmpPaycycle(uF);
		
		return LOAD;
	}
	
	private void getEmpPaycycle(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				String[] strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			con = db.makeConnection(con);
			
			String orgId = CF.getEmpOrgId(con, uF, strSessionEmpId);
			String levelId = CF.getEmpLevelId(con, strSessionEmpId);
			
			Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
            cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")) - 1);
            cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
            reimbursementCTCHeadList = new FillReimbursementCTCHead(request).fillReimbursementCTCHead(strSessionEmpId, strFinancialYearStart, strFinancialYearEnd);
            
            if(reimbursementCTCHeadList !=null && reimbursementCTCHeadList.size() > 0 
            		&& (getReimbursementCTCHead() == null || getReimbursementCTCHead().trim().equals("") || getReimbursementCTCHead().trim().equalsIgnoreCase("NULL"))){
            	setReimbursementCTCHead(reimbursementCTCHeadList.get(0).getReimbursementCTCHeadId());
            }
          
            if(uF.parseToInt(getReimbursementCTCHead()) > 0){
	            List<Date> alDate = new ArrayList<Date>();
				for (int i = 1; i <= 12; i++){
					int nMonthStart = cal.getActualMinimum(Calendar.DATE);
					int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
					int nMonth = (cal.get(Calendar.MONTH) + 1);
					
					String strDateStart =  (nMonthStart <10 ? "0"+nMonthStart : nMonthStart)+"/"+(nMonth <10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);
					
					alDate.add(uF.getDateFormat(strDateStart, DATE_FORMAT));
					
					cal.add(Calendar.MONTH, 1);
				}
				Collections.reverse(alDate);
				
		        Date date2 = uF.getCurrentDate(CF.getStrTimeZone());
		        paycycleList = new ArrayList<FillPayCycles>();
				for(Date ad : alDate){
					String strDateStart = uF.getDateFormat(""+ad, DBDATE, DATE_FORMAT);
					String[] strPayCycleDates = CF.getPayCycleFromDate(con, strDateStart, CF.getStrTimeZone(), CF, orgId);
					
					Date date1 = uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT);
					
					if(date1.before(date2) || date1.equals(date2)){
						
						pst = con.prepareStatement("select * from reimbursement_head_details rhd,reimbursement_assign_head_details rsad " +
								"where rhd.reimbursement_head_id = rsad.reimbursement_head_id and rsad.status=true and rsad.trail_status=true " +
								"and rhd.reimbursement_head_id in (select reimbursement_head_id from reimbursement_head_amt_details where is_attachment=true " +
								"and financial_year_start=? and financial_year_end=?) and rsad.emp_id=? and rsad.level_id=? and rsad.org_id=? " +
								"and rsad.paycycle_from=? and rsad.paycycle_to=? and rsad.paycycle=? and rhd.reimbursement_head_id=? " +
								"and rhd.reimbursement_ctc_id in (select reimbursement_ctc_id from reimbursement_ctc_details where level_id=? and org_id=?)");
						pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setInt(4, uF.parseToInt(levelId));
						pst.setInt(5, uF.parseToInt(orgId));
						pst.setDate(6, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
						pst.setDate(7, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
						pst.setInt(8, uF.parseToInt(strPayCycleDates[2]));
						pst.setInt(9, uF.parseToInt(getReimbursementCTCHead()));
						pst.setInt(10, uF.parseToInt(levelId));
						pst.setInt(11, uF.parseToInt(orgId));
						rs = pst.executeQuery();
						boolean flag = false;
						while(rs.next()){
							flag = true;
						} 
						rs.close();
						pst.close();
						
						if(flag){
							paycycleList.add(new FillPayCycles(strPayCycleDates[0]+"-"+strPayCycleDates[1]+"-"+strPayCycleDates[2], "Pay Cycle " + strPayCycleDates[2] + ", " + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat())));
						}
					} else {
						paycycleList.add(new FillPayCycles(strPayCycleDates[0]+"-"+strPayCycleDates[1]+"-"+strPayCycleDates[2], "Pay Cycle " + strPayCycleDates[2] + ", " + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat())));
					}
				}
				
				pst = con.prepareStatement("select * from reimbursement_head_amt_details rhad, reimbursement_head_details rhd " +
						"where rhad.reimbursement_head_id=rhd.reimbursement_head_id and rhd.reimbursement_head_id=? and rhad.financial_year_start=? " +
						"and rhad.financial_year_end=? and rhd.level_id=? and rhd.org_id=?");
				pst.setInt(1, uF.parseToInt(getReimbursementCTCHead()));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(levelId));
				pst.setInt(5, uF.parseToInt(orgId));
				rs = pst.executeQuery();
				double reimHeadAmount = 0.0d;
				while(rs.next()){
					reimHeadAmount = uF.parseToDouble(rs.getString("amount"));
				} 
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from reimbursement_ctc_applied_details where emp_id=? and is_approved in (0,1) " +
						"and reimbursement_head_id=? and financial_year_start=? and financial_year_end=?");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(getReimbursementCTCHead()));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				double reimHeadAppliedAmount = 0.0d;
				while(rs.next()){
					reimHeadAppliedAmount = uF.parseToDouble(rs.getString("applied_amount"));
				} 
				rs.close();
				pst.close();
				
				double dblAmount = reimHeadAmount - reimHeadAppliedAmount;
				
				setLimitAmount(""+(dblAmount > 0 ? dblAmount : 0));
				
            } else {
            	 paycycleList = new ArrayList<FillPayCycles>();
            }
			
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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String[] getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String[] paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getStrDescription() {
		return strDescription;
	}

	public void setStrDescription(String strDescription) {
		this.strDescription = strDescription;
	}

	public File getStrDocument() {
		return strDocument;
	}

	public void setStrDocument(File strDocument) {
		this.strDocument = strDocument;
	}

	public String getStrDocumentFileName() {
		return strDocumentFileName;
	}

	public void setStrDocumentFileName(String strDocumentFileName) {
		this.strDocumentFileName = strDocumentFileName;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(String limitAmount) {
		this.limitAmount = limitAmount;
	}

	public String getReimbursementCTCHead() {
		return reimbursementCTCHead;
	}

	public void setReimbursementCTCHead(String reimbursementCTCHead) {
		this.reimbursementCTCHead = reimbursementCTCHead;
	}

	public List<FillReimbursementCTCHead> getReimbursementCTCHeadList() {
		return reimbursementCTCHeadList;
	}

	public void setReimbursementCTCHeadList(List<FillReimbursementCTCHead> reimbursementCTCHeadList) {
		this.reimbursementCTCHeadList = reimbursementCTCHeadList;
	}

}
