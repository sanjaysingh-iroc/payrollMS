package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class EmployeeLeaveBreakdown extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	String strSessionEmpId = null; 
	
	String paycycle;
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillPayCycles> paycycleList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String exportType;
	String dataType;

	private static Logger log = Logger.getLogger(EmployeeLeaveBreakdown.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions(); 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
				
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PEmployeeLeaveBreakdown);
		request.setAttribute(TITLE, TViewEmployeeLeaveBreakdown);
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getDataType() == null || getDataType().trim().equals("")){
			setDataType("L");
		}
		
		if(getDataType().trim().equals("L")){
			viewEmployeeLeaveBreakdown(uF);
		} else if(getDataType().trim().equals("E")){
			viewEmployeeExtraLeaveBreakdown(uF);
		} 
		
		
		return loadManagerLeaveApproval(uF);

	}
	
	
	private void viewEmployeeExtraLeaveBreakdown(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			String []strDate = null;
			String strD1 = null;
			String strD2 = null;
			if(getPaycycle()!=null){
				strDate = getPaycycle().split("-");
				strD1 = strDate[0];
				strD2 = strDate[1];
			}else{
				strDate = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF,getF_org());
				setPaycycle(strDate[0] + "-" + strDate[1] + "-" + strDate[2]);
				strD1 = strDate[0];
				strD2 = strDate[1];
			}
			
			
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
			Map<String, String> hmLeaveMap = CF.getLeaveTypeMap(con);
			Map<String, String> hmLeavesColour = new HashMap<String, String>(); 
			CF.getLeavesColour(con, hmLeavesColour);

			List<String> alLeaveCompType = new ArrayList<String>();
			pst = con.prepareStatement("select * from leave_type where org_id=? and leave_type_id not in(-1) and leave_type_id not in " +
				"(select leave_type_id from emp_leave_type where org_id=? and is_constant_balance=true) and is_compensatory=true");	
			pst.setInt(1,uF.parseToInt(getF_org()));
			pst.setInt(2,uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			String leaveType=null;
			while(rs.next()){
				alLeaveCompType.add(rs.getString("leave_type_id"));
				if(leaveType==null){
					leaveType=rs.getString("leave_type_id");
				}else{
					leaveType+=","+rs.getString("leave_type_id");
				}
			}
			rs.close();
			pst.close();
			
			Set<String> alEmpId = new HashSet<String>();
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where emp_id=emp_per_id");
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
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			StringBuilder sbEmp = null;
			while(rs.next()){   
				alEmpId.add(rs.getString("emp_id"));
				
				if(sbEmp == null){
					sbEmp = new StringBuilder();
					sbEmp.append(rs.getString("emp_id"));
				} else {
					sbEmp.append(","+rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbEmp !=null){
				sbQuery=new StringBuilder();
				sbQuery.append("select sum(leave_no) as count,leave_type_id,emp_id from leave_application_register where _date between ? and ? and emp_id in("+sbEmp.toString()+") " +
						"and leave_id in (select leave_id from emp_leave_entry where approval_from>=? and approval_from<=? " +
						"and emp_id in("+sbEmp.toString()+") and is_compensate=true) and (is_modify is null or is_modify=false) " +
						"and leave_type_id in (select leave_type_id from leave_type where leave_type_id not in(-1) " +
						"and leave_type_id not in (select leave_type_id from emp_leave_type where is_constant_balance=true) and is_compensatory=true)" +
						"group by leave_type_id,emp_id order by emp_id,leave_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3,uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(4,uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmApprovedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmApprovedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("count"));             
			    }
				rs.close();
				pst.close();
								
				pst = con.prepareStatement("select sum(emp_no_of_leave) as cnt,leave_type_id,is_approved,emp_id from emp_leave_entry where is_approved !=1 " +
						" and leave_from >=? and leave_from <= ? group by leave_type_id,is_approved,emp_id");
				pst.setDate(1,uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strD2, DATE_FORMAT));
		//		System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				Map<String,String> hmLeaveStatus=new HashMap<String, String>();
				while(rs.next()){
					hmLeaveStatus.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id")+"_"+rs.getString("is_approved"), ""+rs.getDouble("cnt"));				
				}
				rs.close();
				pst.close();
				
				
				List<List<String>> reportList = new ArrayList<List<String>>();
				Iterator<String> it = alEmpId.iterator();
			    while(it.hasNext()){ 
			    	String strEmpId = it.next();
			    	
			    	for(int i=0; i<alLeaveCompType.size(); i++){
		    			String strLeaveTypeId = alLeaveCompType.get(i);
		    			
						double dblPending = uF.parseToDouble(hmLeaveStatus.get(strEmpId+"_"+strLeaveTypeId+"_0"));
						double dblDenied = uF.parseToDouble(hmLeaveStatus.get(strEmpId+"_"+strLeaveTypeId+"_-1"));
						double dblApprovedBalance = uF.parseToDouble(hmApprovedBalance.get(strEmpId+"_"+strLeaveTypeId));
						
						List<String> alInner = new ArrayList<String>();
				    	alInner.add(uF.showData(hmEmpCode.get(strEmpId), ""));
				    	alInner.add(uF.showData(hmEmployeeNameMap.get(strEmpId), ""));
				    	alInner.add(hmLeavesColour.get(strLeaveTypeId));
		    			alInner.add(uF.showData(hmLeaveMap.get(strLeaveTypeId), ""));
		    			alInner.add(uF.showData(""+dblApprovedBalance, "0"));
		    			alInner.add(uF.showData(""+dblPending, "0"));
		    			alInner.add(uF.showData(""+dblDenied, "0"));
		    			
		    			reportList.add(alInner);
			    	}
			    	
			    	
			    	
			    }
			    
			    request.setAttribute("reportList", reportList);
//				request.setAttribute("alLeaveType", alLeaveType);
				request.setAttribute("hmLeaveMap", hmLeaveMap);
				request.setAttribute("alEmpId", alEmpId);
				
				request.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
				request.setAttribute("hmEmpCode", hmEmpCode);
				request.setAttribute("alLeaveCompType", alLeaveCompType);
				
//				request.setAttribute("hmMainBalance", hmMainBalance);
//				request.setAttribute("hmAccruedBalance", hmAccruedBalance);
//				request.setAttribute("hmPaidBalance", hmPaidBalance);
				request.setAttribute("hmLeaveStatus", hmLeaveStatus);
//				request.setAttribute("hmComOffBalance", hmComOffBalance);
				request.setAttribute("hmApprovedBalance", hmApprovedBalance);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String loadManagerLeaveApproval(UtilityFunctions uF){	
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
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
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
public String viewEmployeeLeaveBreakdown(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			String []strDate = null;
			String strD1 = null;
			String strD2 = null;
			if(getPaycycle()!=null){
				strDate = getPaycycle().split("-");
				strD1 = strDate[0];
				strD2 = strDate[1];
			}else{
				strDate = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF,getF_org());
				setPaycycle(strDate[0] + "-" + strDate[1] + "-" + strDate[2]);
				strD1 = strDate[0];
				strD2 = strDate[1];
			}
			
			
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
			Map<String, String> hmLeaveMap = CF.getLeaveTypeMap(con);
			Map<String, String> hmLeavesColour = new HashMap<String, String>(); 
			CF.getLeavesColour(con, hmLeavesColour);
			
			List<String> alLeaveType = new ArrayList<String>();
			pst = con.prepareStatement("select * from leave_type where org_id=? and leave_type_id not in(-1) and leave_type_id not in " +
					"(select leave_type_id from emp_leave_type where org_id=? and is_constant_balance=true) and is_compensatory=false");
			pst.setInt(1,uF.parseToInt(getF_org()));
			pst.setInt(2,uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			String leaveType=null;
			while(rs.next()){
				alLeaveType.add(rs.getString("leave_type_id"));
				if(leaveType==null){
					leaveType=rs.getString("leave_type_id");
				}else{
					leaveType+=","+rs.getString("leave_type_id");
				}
			}
			rs.close();
			pst.close();
			
			List<String> alLeaveCompType = new ArrayList<String>();
			pst = con.prepareStatement("select * from leave_type where org_id=? and leave_type_id not in(-1) and leave_type_id not in " +
				"(select leave_type_id from emp_leave_type where org_id=? and is_constant_balance=true) and is_compensatory=true");	
			pst.setInt(1,uF.parseToInt(getF_org()));
			pst.setInt(2,uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			while(rs.next()){
				alLeaveCompType.add(rs.getString("leave_type_id"));
				if(leaveType==null){
					leaveType=rs.getString("leave_type_id");
				}else{
					leaveType+=","+rs.getString("leave_type_id");
				}
			}
			rs.close();
			pst.close();
			
			Set<String> alEmpId = new HashSet<String>();
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where emp_id=emp_per_id");
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
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			StringBuilder sbEmp = null;
			while(rs.next()){   
				alEmpId.add(rs.getString("emp_id"));
				
				if(sbEmp == null){
					sbEmp = new StringBuilder();
					sbEmp.append(rs.getString("emp_id"));
				} else {
					sbEmp.append(","+rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbEmp !=null){
				sbQuery=new StringBuilder();
				sbQuery.append("select emp_id,leave_type_id,balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
						"where _type='C' and leave_type_id in (select leave_type_id from leave_type) and _date<=? and emp_id in("+sbEmp.toString()+") " +
								"group by emp_id,leave_type_id) order by emp_id,leave_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strD2, DATE_FORMAT));
				rs = pst.executeQuery();
				Map<String, String> hmMainBalance = new HashMap<String, String>();
				while(rs.next()){
					hmMainBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("balance"));
				}
				rs.close();
				pst.close();
				
				sbQuery=new StringBuilder();
				sbQuery.append("select sum(accrued) as accrued,a.leave_type_id,a.emp_id from (select max(_date) as daa,leave_type_id,emp_id from " +
						"leave_register1 where _type='C' and leave_type_id in (select leave_type_id from leave_type) and _date<=? and emp_id in("+sbEmp.toString()+") " +
						"group by emp_id,leave_type_id)as a,leave_register1 lr where  _type!='C' and a.leave_type_id=lr.leave_type_id and a.daa<=lr._date " +
						"and a.emp_id=lr.emp_id and _date<=? and a.emp_id in("+sbEmp.toString()+") group by a.emp_id,a.leave_type_id order by a.emp_id,leave_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strD2, DATE_FORMAT));
			    rs = pst.executeQuery();
//			    System.out.println("pst======>"+pst);
			    Map<String, String> hmAccruedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmAccruedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued"));                
			    }
				rs.close();
				pst.close();
				
				sbQuery=new StringBuilder();
				sbQuery.append("select sum(leave_no) as count,leave_type_id,emp_id from (select a.daa,lar.* from (select max(_date) as daa,leave_type_id," +
						"emp_id from leave_register1 where _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id," +
						"leave_type_id) as a,leave_application_register lar where a.emp_id=lar.emp_id and is_paid=true and (is_modify is null or " +
						"is_modify=false) and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and _date<=? and a.emp_id in("+sbEmp.toString()+")) " +
						"as a group by leave_type_id,emp_id order by emp_id,leave_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strD2, DATE_FORMAT));
			    rs = pst.executeQuery();
//			    System.out.println("pst======>"+pst);
			    Map<String, String> hmPaidBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmPaidBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("count"));             
			    }
				rs.close();
				pst.close();
				
				sbQuery=new StringBuilder();
				sbQuery.append("select sum(leave_no) as count,leave_type_id,emp_id from (select a.daa,lar.* from (select max(_date) as daa,leave_type_id," +
						"emp_id from leave_register1 where _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id," +
						"leave_type_id) as a,leave_application_register lar where a.emp_id=lar.emp_id and is_paid=true and (is_modify is null or " +
						"is_modify=false) and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and _date between ? and ? and a.emp_id in("+sbEmp.toString()+")) " +
						"as a group by leave_type_id,emp_id order by emp_id,leave_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strD2, DATE_FORMAT));
			    rs = pst.executeQuery();
//			    System.out.println("pst======>"+pst);
			    Map<String, String> hmApprovedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmApprovedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("count"));             
			    }
				rs.close();
				pst.close();
				
				sbQuery=new StringBuilder();
				sbQuery.append("select sum(leave_no) as count,leave_type_id,emp_id from (select a.daa,lar.* from (select max(_date) as daa,leave_type_id," +
						"emp_id from leave_register1 where _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id," +
						"leave_type_id) as a,leave_application_register lar where a.emp_id=lar.emp_id and is_paid=false and (is_modify is null or " +
						"is_modify=false) and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and _date<=? and a.emp_id in("+sbEmp.toString()+")) " +
						"as a group by leave_type_id,emp_id order by emp_id,leave_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strD2, DATE_FORMAT));
			    rs = pst.executeQuery();
//			    System.out.println("pst======>"+pst);
			    Map<String, String> hmComOffBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmComOffBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("count"));             
			    }
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select sum(emp_no_of_leave) as cnt,leave_type_id,is_approved,emp_id from emp_leave_entry where is_approved !=1 " +
						" and leave_from >=? and leave_from <= ? group by leave_type_id,is_approved,emp_id");
				pst.setDate(1,uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strD2, DATE_FORMAT));
		//		System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				Map<String,String> hmLeaveStatus=new HashMap<String, String>();
				while(rs.next()){
					hmLeaveStatus.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id")+"_"+rs.getString("is_approved"), ""+rs.getDouble("cnt"));				
				}
				rs.close();
				pst.close();
				
				
				List<List<String>> reportList = new ArrayList<List<String>>();
				Iterator<String> it = alEmpId.iterator();
			    while(it.hasNext()){ 
			    	String strEmpId = it.next();
			    	
			    	for(int i=0; i<alLeaveType.size(); i++){
		    			String strLeaveTypeId = alLeaveType.get(i);
		    			double dblBalance = uF.parseToDouble(hmMainBalance.get(strEmpId+"_"+strLeaveTypeId));
						dblBalance += uF.parseToDouble(hmAccruedBalance.get(strEmpId+"_"+strLeaveTypeId));
						
						double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strEmpId+"_"+strLeaveTypeId));
						
						if(dblBalance > 0 && dblBalance >= dblPaidBalance){
				            dblBalance = dblBalance - dblPaidBalance; 
				        }
		    			
						double dblPending = uF.parseToDouble(hmLeaveStatus.get(strEmpId+"_"+strLeaveTypeId+"_0"));
						double dblDenied = uF.parseToDouble(hmLeaveStatus.get(strEmpId+"_"+strLeaveTypeId+"_-1"));
						
						double dblApprovedBalance = uF.parseToDouble(hmApprovedBalance.get(strEmpId+"_"+strLeaveTypeId));
						
						List<String> alInner = new ArrayList<String>();
				    	alInner.add(uF.showData(hmEmpCode.get(strEmpId), ""));
				    	alInner.add(uF.showData(hmEmployeeNameMap.get(strEmpId), ""));
				    	alInner.add(hmLeavesColour.get(strLeaveTypeId));
		    			alInner.add(uF.showData(hmLeaveMap.get(strLeaveTypeId), ""));
		    			alInner.add(uF.showData(""+dblBalance, "0"));
		    			alInner.add(uF.showData(""+dblApprovedBalance, "0"));
		    			alInner.add(uF.showData(""+dblPending, "0"));
		    			alInner.add(uF.showData(""+dblDenied, "0"));
		    			
		    			reportList.add(alInner);
			    	}
			    	
			    	
			    	
			    }
			    
			    request.setAttribute("reportList", reportList);
				request.setAttribute("alLeaveType", alLeaveType);
				request.setAttribute("hmLeaveMap", hmLeaveMap);
				request.setAttribute("alEmpId", alEmpId);
				
				request.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
				request.setAttribute("hmEmpCode", hmEmpCode);
				request.setAttribute("alLeaveCompType", alLeaveCompType);
				
				request.setAttribute("hmMainBalance", hmMainBalance);
				request.setAttribute("hmAccruedBalance", hmAccruedBalance);
				request.setAttribute("hmPaidBalance", hmPaidBalance);
				request.setAttribute("hmLeaveStatus", hmLeaveStatus);
				request.setAttribute("hmComOffBalance", hmComOffBalance);
				request.setAttribute("hmApprovedBalance", hmApprovedBalance);
			}

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


	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}


	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
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


	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}



}
