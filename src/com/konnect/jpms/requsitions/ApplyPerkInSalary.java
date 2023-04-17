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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPerkSalary;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApplyPerkInSalary extends ActionSupport  implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	
	private CommonFunctions CF;
	
	private String financialYear;
	private String[] paycycle;
	private List<FillPayCycles> paycycleList;
	
	private List<FillFinancialYears> financialYearList; 
	
	private String perkSalary;
	private List<FillPerkSalary> perkSalaryList;
	
	private String strDescription;
	private File strDocument;
	private String strDocumentFileName;
	private String strAmount;
	private String limitAmount;
	
	public String execute() {	    
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String type = (String) request.getParameter("type");
		if(type != null && type.trim().equalsIgnoreCase("PERKSALARY")){
			return approveDeny(uF);
		}
		
		if (getFinancialYear() == null || getFinancialYear().trim().equals("") || getFinancialYear().trim().equalsIgnoreCase("NULL") || getFinancialYear().trim().length() == 0) {
			String[] strFinancialDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setFinancialYear(strFinancialDates[0] + "-" + strFinancialDates[1]);
		}
		
		if(strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			String submit1 = (String) request.getParameter("submit1");
			if(submit1!=null){
				return addPerkInSalary(uF);
			}
		}
		
		return loadPerkInSalary(uF);
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
			String PID = (String) request.getParameter("PID");
			String mReason = (String) request.getParameter("mReason");
			String userType = (String) request.getParameter("userType");
			String nonTaxable = (String) request.getParameter("nonTaxable");
			
			pst = con.prepareStatement("update perk_salary_applied_details set is_approved=?,approved_by=?,approved_date=?," +
					"is_nontaxable=?,approve_reason=?,approver_user_type_id=? where perk_salary_applied_id=?");
			pst.setInt(1, uF.parseToInt(apStatus));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setBoolean(4, uF.parseToBoolean(nonTaxable));
			pst.setString(5, mReason);
			pst.setInt(6, uF.parseToInt(userType));
			pst.setInt(7, uF.parseToInt(PID));
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

	private String addPerkInSalary(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getFinancialYear() != null) {				
				String[] strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			String fileName =null;
			if(getStrDocument()!=null) {
				if(CF.getStrDocSaveLocation()==null) {
					fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
				} else {
					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PERKS+"/"+I_PERKS_SALARY+"/"+I_DOCUMENT+"/"+strSessionEmpId, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
				} 
			}
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into perk_salary_applied_details (emp_id,is_approved,entry_date,perk_salary_id,description," +
					"ref_document,financial_year_start,financial_year_end,is_nontaxable,applied_amount)" +
					" values(?,?,?,?, ?,?,?,?, ?,?)");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, 0);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getPerkSalary()));
			pst.setString(5, getStrDescription());
			pst.setString(6, fileName);
			pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setBoolean(9, false);
			pst.setDouble(10, uF.parseToDouble(getStrAmount()));
//			System.out.println("insert pst==>"+pst);
			int x = pst.executeUpdate();
			if(x > 0){
				pst = con.prepareStatement("select max(perk_salary_applied_id) as perk_salary_applied_id from perk_salary_applied_details");
				rs = pst.executeQuery();
				int nPerkSalAppliedId = 0;
				while(rs.next()){
					nPerkSalAppliedId = uF.parseToInt(rs.getString("perk_salary_applied_id"));
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
						
						pst = con.prepareStatement("insert into perk_salary_applied_paycycle (perk_salary_applied_id,emp_id,paycycle_from," +
								"paycycle_to,paycycle,financial_year_start,financial_year_end)" +
								" values(?,?,?,?, ?,?,?)");
						pst.setInt(1, nPerkSalAppliedId);
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
						pst.setInt(5, uF.parseToInt(strPC));
						pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//						System.out.println("insert pst2==>"+pst);
						pst.execute();	
					}
				}				
				
				session.setAttribute(MESSAGE, SUCCESSM+"Perks in salary applied Successfully."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Perks in salary applied Failed."+END);
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Perks in salary applied Failed."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	public String loadPerkInSalary(UtilityFunctions uF) {
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
			
            perkSalaryList = new FillPerkSalary(request).fillPerkSalary(strSessionEmpId, strFinancialYearStart, strFinancialYearEnd);
            
            if(perkSalaryList !=null && perkSalaryList.size() > 0 && (getPerkSalary() == null || getPerkSalary().trim().equals("") || getPerkSalary().trim().equalsIgnoreCase("NULL"))){
            	setPerkSalary(perkSalaryList.get(0).getPerkSalaryId());
            }
          
            if(uF.parseToInt(getPerkSalary()) > 0){
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
						
						pst = con.prepareStatement("select * from perk_salary_details psd,perk_assign_salary_details psad " +
								"where psd.perk_salary_id = psad.perk_salary_id and psad.status=true and psad.trail_status=true " +
								"and psd.is_attachment=true and psad.emp_id=? and psd.level_id=? and psd.org_id=? " +
								"and psad.paycycle_from=? and psad.paycycle_to=? and paycycle=? and psad.perk_salary_id=?");
						pst.setInt(1, uF.parseToInt(strSessionEmpId));
						pst.setInt(2, uF.parseToInt(levelId));
						pst.setInt(3, uF.parseToInt(orgId));
						pst.setDate(4, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
						pst.setDate(5, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
						pst.setInt(6, uF.parseToInt(strPayCycleDates[2]));
						pst.setInt(7, uF.parseToInt(getPerkSalary()));
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
				
				pst = con.prepareStatement("select * from perk_salary_details where level_id=? and org_id=? and financial_year_start=? " +
						"and financial_year_end=? and perk_salary_id=?");
				pst.setInt(1, uF.parseToInt(levelId));
				pst.setInt(2, uF.parseToInt(orgId));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(getPerkSalary()));
				rs = pst.executeQuery();
				double perkAmount = 0.0d;
				while(rs.next()){
					perkAmount = uF.parseToDouble(rs.getString("amount"));
				} 
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from perk_salary_applied_details where emp_id=? and is_approved in (0,1) " +
						"and perk_salary_id=? and financial_year_start=? and financial_year_end=?");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(getPerkSalary()));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				double perkAppliedAmount = 0.0d;
				while(rs.next()){
					perkAppliedAmount = uF.parseToDouble(rs.getString("applied_amount"));
				} 
				rs.close();
				pst.close();
				
				double dblAmount = perkAmount - perkAppliedAmount;
				
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

	public String getPerkSalary() {
		return perkSalary;
	}

	public void setPerkSalary(String perkSalary) {
		this.perkSalary = perkSalary;
	}

	public List<FillPerkSalary> getPerkSalaryList() {
		return perkSalaryList;
	}

	public void setPerkSalaryList(List<FillPerkSalary> perkSalaryList) {
		this.perkSalaryList = perkSalaryList;
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

}
