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
 
public class IncentiveForm extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null; 
	String profileEmpId;
	String strBaseUserType = null;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	private String[] f_strWLocation; 
	private String[] f_level;
	private String[] f_department;
	private String[] f_service;
	private String paycycle;
	
	private List<FillPayCycles> paycycleList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillWLocation> wLocationList;

	private List<FillOrganisation> organisationList;
	private String f_org;
	private String strAction = null;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strEmpId = (String)session.getAttribute(EMPID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserType = (String)session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, "/jsp/payroll/IncentiveForm.jsp");
		request.setAttribute(TITLE, "Incentives");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		//Created By Dattatray 13-06-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		System.out.println("IncentiveForm");
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
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
		
		viewIncentives(uF);
		loadPageVisitAuditTrail(CF, uF);//Created By Dattatray 13-06-2022
		return loadIncentives(uF);
	}
	
	//Created By Dattatray 13-06-2022
		private void loadPageVisitAuditTrail(CommonFunctions CF,UtilityFunctions uF) {
			Connection con=null;
			Database db = new Database();
			db.setRequest(request);
			try {
				con = db.makeConnection(con);
				StringBuilder builder = new StringBuilder();
				builder.append("Filter:");
				builder.append("\nOrganization:"+getF_org());
				builder.append("\nLocation:"+getStrLocation());	
				builder.append("\nDepartment:"+getStrDepartment());	
				builder.append("\nService:"+getStrSbu());	
				builder.append("\nLevel:"+StringUtils.join(f_level,","));
				builder.append("\nPaycycle:"+getPaycycle());	
				
				
				CF.pageVisitAuditTrail(con, CF,uF, strEmpId, strAction, strBaseUserType, builder.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				db.closeConnection(con);
			}
		}
	public String loadIncentives(UtilityFunctions uF){
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
public String viewIncentives(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			String []strPayCycleDates;
			
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
//				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF,getF_org(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			
			con = db.makeConnection(con);
			
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
						
			pst = con.prepareStatement("select salary_head_id, amount, esd.emp_id from emp_salary_details esd, (select max(entry_date) as max_date, emp_id from emp_salary_details group by emp_id ) as b where esd.entry_date = b.max_date and b.emp_id = esd.emp_id and isdisplay = true and salary_head_id not in ("+GROSS+","+CTC+") order by esd.emp_id, salary_head_id ");
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
            sbQuery.append(" and eod.emp_id in ( select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
            		" from emp_salary_details where isdisplay = true and is_approved=true and effective_date <=? group by emp_id ) as b where esd.effective_date = b.max_date " +
            		"and b.emp_id = esd.emp_id and isdisplay = true and is_approved=true and esd.salary_head_id=? and esd.effective_date <=?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, INCENTIVES);
			pst.setDate(4,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			while(rs.next()){
				List<String> alEmpReportInner = new ArrayList<String>();
				alEmpReportInner.add(rs.getString("emp_per_id"));
				alEmpReportInner.add(hmEmpMap.get(rs.getString("emp_per_id")));			
				
				alEmpReport.add(alEmpReportInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alEmpReport", alEmpReport);
			request.setAttribute("hmSalaryList", hmSalaryList);
			request.setAttribute("hmSalaryHeadsMap", hmSalaryHeadsMap);
			
			pst = con.prepareStatement("select * from incentive_details where paid_from = ? and paid_to=? and pay_paycycle = ?");  
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmIncentives = new HashMap<String, String>();
			Map<String,String> hmIncentivesId = new HashMap<String, String>();
			Map<String,String> hmIncentivesValue = new HashMap<String, String>();
			Map<String,String> hmIncentivePercent = new HashMap<String, String>();
			Map<String,String> hmIncentiveSalId= new HashMap<String, String>();
			while(rs.next()){
				hmIncentives.put(rs.getString("emp_id"), rs.getString("is_approved"));
				hmIncentivesId.put(rs.getString("emp_id"), rs.getString("incentive_id"));
				hmIncentivesValue.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
				hmIncentivePercent.put(rs.getString("emp_id"), rs.getDouble("percent")>0.0d ? rs.getString("percent") : "");
				hmIncentiveSalId.put(rs.getString("emp_id"), rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmIncentives", hmIncentives);
			request.setAttribute("hmIncentivesId", hmIncentivesId);
			request.setAttribute("hmIncentivesValue", hmIncentivesValue);
			request.setAttribute("hmIncentiveSalId", hmIncentiveSalId);
			request.setAttribute("hmIncentivePercent", hmIncentivePercent);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
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
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
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
		if(getF_level()!=null){
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++){
				for(int j=0;j<getF_level().length;j++){
					if(getF_level()[j].equals(levelList.get(i).getLevelId())){
						if(k==0){
							strLevel=levelList.get(i).getLevelCodeName();
						}else{
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")){
				hmFilter.put("LEVEL", strLevel);
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
		}else{
			hmFilter.put("LEVEL", "All Levels");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
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