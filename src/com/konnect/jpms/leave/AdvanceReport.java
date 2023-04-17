package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AdvanceReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType;
	String strSessionEmpId;
	String destinations;
	
	public String execute() throws Exception {
		request.setAttribute(PAGE, PTravelAdvanceReport);
		request.setAttribute(TITLE, "Advance Report");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}	
		viewAdvances(uF);
		
		return loadAdvanceEntry(uF);
	}
	public String loadAdvanceEntry(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		empNamesList = new FillEmployee(request).fillEmployeeName(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), uF.parseToInt(getF_level()), uF.parseToInt(getF_org()), uF.parseToInt(getF_strWLocation()));
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	String strStartDate;
	String strEndDate;
	String f_level;
	String strSelectedEmpId;
	
	List<FillLevel> levelList;
	List<FillEmployee> empNamesList; 
	List<FillWLocation> wLocationList;
	List<FillOrganisation> organisationList;

	String f_org;
	String f_strWLocation;
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					strOrg=organisationList.get(i).getOrgName();
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
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				if(getF_strWLocation().equals(wLocationList.get(i).getwLocationId())) {
					strLocation=wLocationList.get(i).getwLocationName();
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		alFilter.add("EMP");
		if(getF_level()!=null) {
			String strEmpName="";
			for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
				if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
					strEmpName=empNamesList.get(i).getEmployeeName();
				}
			}
			if(strEmpName!=null && !strEmpName.equals("")) {
				hmFilter.put("EMP", strEmpName);
			} else {
				hmFilter.put("EMP", "All Employee");
			}
		} else {
			hmFilter.put("EMP", "All Employee");
		}
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewAdvances(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		List<List<String>> reportList = new ArrayList<List<String>>();
		List<String> alInner = new ArrayList<String>();
		
		try {
			
			if(getF_strWLocation()==null){
				setF_strWLocation((String)session.getAttribute(WLOCATIONID));
			}
			if(getF_org()==null){
				setF_org((String)session.getAttribute(ORGID));
			}
			
			if(getStrStartDate()==null && getStrEndDate()==null){

				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				
				setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			}

			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con); 
			Map<String, String> hmEmpTravelEligibility = CF.getEmpTravelEligibility(con, uF);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			/*pst = con.prepareStatement("select sum(reimbursement_amount) as reimbursement_amount, reimbursement_type::integer, emp_id from emp_reimbursement where reimbursement_type1 = 'T' group by reimbursement_type::integer, emp_id");
			rs = pst.executeQuery();
			Map<String, String> hmClaimReimbursement = new HashMap<String, String>();
			while(rs.next()){
				hmClaimReimbursement.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_type"), rs.getString("reimbursement_amount"));
			}*/
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select reimbursement_amount, reimbursement_type, eod.emp_id from emp_reimbursement er  inner join employee_official_details  eod on eod.emp_id = er.emp_id  where reimbursement_type1 = 'T' ");
			
			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			} 
			sbQuery.append(" order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			//System.out.println("pst=====>"+pst);
			Map<String, String> hmClaimReimbursement = new HashMap<String, String>();
			while(rs.next()){
				if(uF.isInteger(rs.getString("reimbursement_type"))){
					double reimAmt=uF.parseToDouble(hmClaimReimbursement.get(rs.getString("emp_id")+"_"+rs.getString("reimbursement_type")));
					reimAmt+=uF.parseToDouble(rs.getString("reimbursement_amount"));
					hmClaimReimbursement.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_type"), uF.formatIntoTwoDecimalWithOutComma(reimAmt));
				}
			}
			rs.close();
			pst.close();
			
			StringBuilder sbStatus = new StringBuilder();
			sbQuery = new StringBuilder();
			sbQuery.append("select ta.advance_id,ta.advance_status, ta.emp_id, ta.entry_date,ta.travel_id,ta.advance_amount, ta.settlement_amount, ta.settlement_status from emp_leave_entry elt, travel_advance ta   inner join employee_official_details  eod on eod.emp_id = ta.emp_id where ta.travel_id = elt.leave_id ");
			
			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			} 
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && getStrStartDate().length()>0 && getStrEndDate().length()>0){
				sbQuery.append(" and ta.entry_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'");
			}
			
			if (getF_strWLocation() != null && uF.parseToInt(getF_strWLocation())>0) {
				sbQuery.append(" and eod.wlocation_id in (" + getF_strWLocation()+")");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
			}
			
			 if(getF_level()!=null && uF.parseToInt(getF_level())>0){
                 sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+getF_level()+") ) ");
             }
			if(uF.parseToInt(getStrSelectedEmpId())>0){
				sbQuery.append(" and ta.emp_id = "+uF.parseToInt(getStrSelectedEmpId()));
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				sbQuery.append(" and ( ta.emp_id in (select emp_id from project_emp_details where _isteamlead = false  and pro_id in (select pro_id from projectmntnc where (start_date, deadline) overlaps (to_date('"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"'::text, 'YYYY-MM-DD') ,to_date('"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'::text, 'YYYY-MM-DD') +1) and pro_id in (select pro_id from project_emp_details where emp_id = "+uF.parseToInt(strSessionEmpId)+") )) ");
				sbQuery.append(" OR ta.emp_id in (select emp_id from employee_official_details where supervisor_emp_id  = "+uF.parseToInt(strSessionEmpId)+") )");
						
			}
			sbQuery.append(" order by ta.entry_date");
			pst = con.prepareStatement(sbQuery.toString());
			
			//System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				alInner = new ArrayList<String>();
				
				double dblEligibilityAmount = uF.parseToDouble(hmEmpTravelEligibility.get(rs.getString("emp_id")));
				double dblAdvAmount = uF.parseToDouble(rs.getString("advance_amount"));
				double dblClaimAmount = uF.parseToDouble(hmClaimReimbursement.get(rs.getString("emp_id")+"_"+rs.getString("travel_id")));
				double dblBalanceAmount = 0;
				
				
				dblBalanceAmount = dblAdvAmount - dblClaimAmount;
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				sbStatus.replace(0, sbStatus.length(), "");
				if(uF.parseToInt(rs.getString("advance_status"))==1){
					/*sbStatus.append(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblAdvAmount)+" <img src=\"images1/icons/approved.png\" title=\"Approved\">");*/
					sbStatus.append(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblAdvAmount)+" <i class=\"fa fa-circle\" aria-hidden=\"true\"  title=\"Approved\" style=\"color:#54aa0d\"></i>");
					
					
				}else if(uF.parseToInt(rs.getString("advance_status"))==-1){
					/*sbStatus.append(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblAdvAmount)+" <img src=\"images1/icons/denied.png\" title=\"Denied\">");*/
					sbStatus.append(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblAdvAmount)+" <i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Denied\" style=\"color:#e22d25\"></i>");
				}else if(uF.parseToInt(rs.getString("advance_status"))==0){
					/*sbStatus.append(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblAdvAmount)+" <a href=\"javascript:void(0)\" onclick=\"approveAdvance("+rs.getString("advance_id")+")\" ><img src=\"images1/icons/pending.png\" title=\"Waiting for approval\"></a>");*/
					sbStatus.append(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblAdvAmount)+" <a href=\"javascript:void(0)\" onclick=\"approveAdvance("+rs.getString("advance_id")+")\" ><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i></a>");
				}
				
				
				alInner.add(hmEmpCode.get(rs.getString("emp_id")));
				alInner.add(hmEmpName.get(rs.getString("emp_id")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(sbStatus.toString());
				alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblClaimAmount));
				alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblEligibilityAmount));
				alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblBalanceAmount));
				
				
				
				if(uF.parseToBoolean(rs.getString("settlement_status"))){
					/*alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("settlement_amount"))) +((uF.parseToInt(rs.getString("advance_status"))==1)? " <a href=\"javascript:void(0);\" onclick=\"settleAdvance("+rs.getString("advance_id")+",1)\"><img src=\"images1/icons/approved.png\" title=\"Amount Settled\"></a>":""));*/
					alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("settlement_amount"))) +((uF.parseToInt(rs.getString("advance_status"))==1)? " <a href=\"javascript:void(0);\" onclick=\"settleAdvance("+rs.getString("advance_id")+",1)\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Amount Settled\"></i></a>":""));
				}else{
					/*alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("settlement_amount"))) +((uF.parseToInt(rs.getString("advance_status"))==1)?" <a href=\"javascript:void(0);\" onclick=\"settleAdvance("+rs.getString("advance_id")+",0)\"><img src=\"images1/icons/pending.png\" title=\"Yet to be settled\"></a>":""));*/
					alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("settlement_amount"))) +((uF.parseToInt(rs.getString("advance_status"))==1)?" <a href=\"javascript:void(0);\" onclick=\"settleAdvance("+rs.getString("advance_id")+",0)\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Yet to be settled\"></i></a>":""));
				}
				
				if(uF.parseToDouble(rs.getString("settlement_amount")) > 0){
					double dblBalanceSettle = dblBalanceAmount - uF.parseToDouble(rs.getString("settlement_amount"));
					alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(dblBalanceSettle));
				} else {
					alInner.add(strCurrency+uF.formatIntoTwoDecimalWithOutComma(0.0d));
				}
				
				
				reportList.add(alInner);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("reportList", reportList);
		
		
		return SUCCESS;

	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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
	public String getF_level() {
		return f_level;
	}
	public void setF_level(String f_level) {
		this.f_level = f_level;
	}
	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}
	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}
	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}
	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	public String getF_org() {
		return f_org;
	}
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	public String getF_strWLocation() {
		return f_strWLocation;
	}
	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
	
}