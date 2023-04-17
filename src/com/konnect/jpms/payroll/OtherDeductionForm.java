package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class OtherDeductionForm extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	  
	CommonFunctions CF = null; 
	String profileEmpId;
	 
	String[] f_wLocation; 
	String[] f_level; 
	String[] f_department;
	String[] f_service;
	String paycycle;
	
	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillOrganisation> organisationList;
	String f_org;
	
	private static Logger log = Logger.getLogger(OtherDeductionForm.class);
	
	public String execute() throws Exception {
		
		try {
			UtilityFunctions uF = new UtilityFunctions();
			session = request.getSession();
			request.setAttribute(PAGE, "/jsp/payroll/OtherDeductionForm.jsp");
			request.setAttribute(TITLE, "Other Deduction");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strEmpId = (String)session.getAttribute(EMPID);
			strUserType = (String)session.getAttribute(USERTYPE);
			
			boolean isView  = CF.getAccess(session, request, uF);
			if(!isView){
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}
			if(getF_org()==null){
				setF_org((String)session.getAttribute(ORGID));
			}
			
			viewOvertime(uF);
			  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loadOvertime();
	}
	
	
	public String loadOvertime(){
		UtilityFunctions uF=new UtilityFunctions();
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		paycycleList =new FillPayCycles(request).fillPayCycles(CF, getF_org()); 
		
		return LOAD;
	}
	
	public String viewOvertime(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			String []strPayCycleDates;
			if (getPaycycle() != null && !getPaycycle().equals("")) {   
				strPayCycleDates = getPaycycle().split("-");
			} else {
//				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);   
			}
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			pst = con.prepareStatement("select emp_id from payroll_generation where paid_from = ? and paid_to = ? and paycycle = ? group by emp_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			rs = pst.executeQuery();
			List<String> ckEmpPayList = new ArrayList<String>();
			while(rs.next()){
				ckEmpPayList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("ckEmpPayList", ckEmpPayList);
						
			pst = con.prepareStatement("select salary_head_id, amount, esd.emp_id from emp_salary_details esd, (select max(entry_date) as max_date, emp_id from emp_salary_details group by emp_id ) as b where esd.entry_date = b.max_date and b.emp_id = esd.emp_id and isdisplay = true order by esd.emp_id, salary_head_id ");
			rs = pst.executeQuery();
			Map hmSalaryList = new HashMap();
			List alSalaryList = new ArrayList();
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			while(rs.next()){
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alSalaryList = new ArrayList();
				}
				
				alSalaryList.add(rs.getString("salary_head_id"));
				hmSalaryList.put(strEmpIdNew, alSalaryList);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			

			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			
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
            
            if(getF_wLocation()!=null && getF_wLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			
			sbQuery.append(" order by emp_fname, emp_lname");

			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			
			
			
//			System.out.println("pst===>"+pst);
			
			rs = pst.executeQuery();
			
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			List<String> alEmpReportInner = new ArrayList<String>();
			
			while(rs.next()){
				
				alEmpReportInner = new ArrayList<String>();
				
				alEmpReportInner.add(rs.getString("emp_per_id"));
				alEmpReportInner.add(hmEmpMap.get(rs.getString("emp_per_id")));
				alEmpReportInner.add("");
				alEmpReportInner.add("");
				alEmpReportInner.add("");
				alEmpReportInner.add("");
				
				
				alEmpReport.add(alEmpReportInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alEmpReport", alEmpReport);
			request.setAttribute("hmSalaryList", hmSalaryList);
			request.setAttribute("hmSalaryHeadsMap", hmSalaryHeadsMap);
			
			
			
			
			
//			pst = con.prepareStatement("select * from otherdeduction_individual_details where pay_paycycle = ?");
//			pst.setInt(1, uF.parseToInt(strPayCycleDates[2]));
//			rs = pst.executeQuery();
			pst = con.prepareStatement("select * from otherdeduction_individual_details where paid_from = ? and paid_to=? and pay_paycycle = ?");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			rs = pst.executeQuery();

			Map<String,String> hmOtherDeduction = new HashMap<String, String>();
			Map<String,String> hmOtherDeductionId = new HashMap<String, String>();
			Map<String,String> hmOtherDeductionValue = new HashMap<String, String>();
			while(rs.next()){
				hmOtherDeduction.put(rs.getString("emp_id"), rs.getString("is_approved"));
				hmOtherDeductionId.put(rs.getString("emp_id"), rs.getString("otherdeduction_id"));
				hmOtherDeductionValue.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pay_amount"))));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOtherDeduction", hmOtherDeduction);
			request.setAttribute("hmOtherDeductionId", hmOtherDeductionId);
			request.setAttribute("hmOtherDeductionValue", hmOtherDeductionValue);
			
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
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}


	public String[] getF_wLocation() {
		return f_wLocation;
	}


	public void setF_wLocation(String[] f_wLocation) {
		this.f_wLocation = f_wLocation;
	}


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}


	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}


	public String[] getF_service() {
		return f_service;
	}


	public void setF_service(String[] f_service) {
		this.f_service = f_service;
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


}
