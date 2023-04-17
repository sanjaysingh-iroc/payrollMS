package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

public class LeaveCard1  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String paycycle;
	String exportType;
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
	
	String profileEmpId;
	
	String strSessionEmpId;
	
	public String execute() throws Exception {
  
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		  
		request.setAttribute(TITLE, TLeaveCard);
		request.setAttribute(PAGE, "/jsp/leave/LeaveCard1.jsp");
		UtilityFunctions uF = new UtilityFunctions();
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
		viewLeaveCard1(uF);
		
		return loadLeaveCard(uF);

	}
	
	private void viewLeaveCard1(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			
			Map<String, String> hmLeaveType = CF.getLeaveTypeMap(con);
			Map<String, String> hmLeavesColour = new HashMap<String, String>(); 
			CF.getLeavesColour(con, hmLeavesColour);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWLocationMap = CF.getEmpWlocationMap(con);
			Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(con);  		//added by parvez date: 01-11-2022
			
			String []strDate = null;
			if(getPaycycle()!=null){
				strDate = getPaycycle().split("-");
			}else{
				strDate = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF,getF_org());
				setPaycycle(strDate[0] + "-" + strDate[1] + "-" + strDate[2]);
			}
			
			
			String []strPrevDate =CF.getPrevPayCycleByOrg(con, strDate[0], CF.getStrTimeZone(), CF,getF_org());
			String []strFutureDate =CF.getNextPayCycleByOrg(con, strDate[0], CF.getStrTimeZone(), CF,getF_org());

			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id > 0 "
					+"and  epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and is_delete = false");
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				sbQuery.append(" and eod.supervisor_emp_id="+uF.parseToInt(strSessionEmpId));
			}
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strDate[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strDate[1], DATE_FORMAT));
			//System.out.println("pst======>"+pst); 
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()){
				if(!alEmp.contains(rs.getString("emp_id")) && uF.parseToInt(rs.getString("emp_id")) > 0){
					alEmp.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			int nEmp = alEmp.size();
			if(nEmp > 0){
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				
				pst = con.prepareStatement("SELECT * FROM probation_policy WHERE emp_id in("+strEmpIds+")");
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				Map<String, List<String>> hmEmpLeaves = new HashMap<String, List<String>>();
				List<String> alLeaveTypeIds = new ArrayList<String>();
				while(rs.next()) {
					String strAllowedLeaves = rs.getString("leaves_types_allowed");
					if(strAllowedLeaves!=null && strAllowedLeaves.length()>0){
						List<String> al = Arrays.asList(strAllowedLeaves.split(","));
						for(String leaveTypeId : al){
	 						if(uF.parseToInt(leaveTypeId) > 0) {
								List<String> alEmpLeave = hmEmpLeaves.get(rs.getString("emp_id"));
								if(alEmpLeave == null) alEmpLeave = new ArrayList<String>();
								alEmpLeave.add(leaveTypeId);
								
								hmEmpLeaves.put(rs.getString("emp_id"), alEmpLeave);
								
								if(!alLeaveTypeIds.contains(leaveTypeId)) {
									alLeaveTypeIds.add(leaveTypeId);
								}
							}
						}
					}
				}
				rs.close();
				pst.close();
				
				
				Map<String, String> hmTakenPaid = new HashMap<String, String>();
				pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
						"where is_paid=true and (is_modify is null or is_modify=false) and _date between ? and ? and emp_id in("+strEmpIds+") group by leave_type_id, emp_id, is_paid");
				pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					hmTakenPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmTakenHalfDayPaid = new HashMap<String, String>();
				pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
						"where is_paid=true and (is_modify is null or is_modify=false) and leave_no=0.5 and _date between ? and ? and emp_id in("+strEmpIds+") group by leave_type_id, emp_id, is_paid");
				pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					hmTakenHalfDayPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmTakenUnPaid = new HashMap<String, String>();
				pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
						"where is_paid=false and (is_modify is null or is_modify=false) and _date between ? and ? and emp_id in("+strEmpIds+") group by leave_type_id, emp_id, is_paid");
				pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					double dblUnpaid = uF.parseToDouble(hmTakenHalfDayPaid.get(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"))) + uF.parseToDouble(rs.getString("leave_no"));
					hmTakenUnPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), ""+dblUnpaid);
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmPreviousPaid = new HashMap<String, String>();
				pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
						"where leave_id in (select leave_id from emp_leave_entry where approval_from<? and entrydate between ? and ?  and emp_id in("+strEmpIds+")) " +
						" and _date<? and (is_modify is null or is_modify=false) and emp_id in("+strEmpIds+") group by leave_type_id, emp_id, is_paid");
				pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDate[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strDate[1], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strDate[0], DATE_FORMAT));
//				System.out.println("1 pst======>"+pst); 
				rs = pst.executeQuery();		
				while(rs.next()) {
					if(uF.parseToBoolean(rs.getString("is_paid"))) {
						hmPreviousPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmFuturePaid = new HashMap<String, String>();
				pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
						"where leave_id in (select leave_id from emp_leave_entry where approval_to_date>? and entrydate between ? and ?  and emp_id in("+strEmpIds+")) " +
						"and _date>?  and (is_modify is null or is_modify=false) and emp_id in("+strEmpIds+") group by leave_type_id, emp_id, is_paid");
				pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDate[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strDate[1], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strDate[1], DATE_FORMAT));
//				System.out.println("2 pst======>"+pst);
				rs = pst.executeQuery();		
				while(rs.next()){
					if(uF.parseToBoolean(rs.getString("is_paid"))){
						hmFuturePaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
					}
				}
				rs.close();
				pst.close();
				
				sbQuery=new StringBuilder();
	//			sbQuery.append("select emp_id,leave_type_id,balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
	//					"where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
	//					"from emp_leave_type where is_constant_balance=false) and is_compensatory=false) and _date<= ? group by emp_id,leave_type_id)");
				sbQuery.append("select emp_id,leave_type_id,balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
						"where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
						"from emp_leave_type where is_constant_balance=false) and is_compensatory=false and is_work_from_home=false) and _date<= ? and emp_id in("+strEmpIds+") group by emp_id,leave_type_id)");
				sbQuery.append("and emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+strEmpIds+")) ");
				sbQuery.append(" order by emp_id,leave_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				 pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
//				System.out.println("3 pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmMainBalance=new HashMap<String, String>();
			    Map<String, List<List<String>>> hmEmpLeaveMap=new HashMap<String, List<List<String>>>();
			    while (rs.next()) {
			        hmMainBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("balance"));
			        
			        List<List<String>> outerList = hmEmpLeaveMap.get(rs.getString("emp_id"));
			        if(outerList==null) outerList = new ArrayList<List<String>>();
			        
			        List<String> innerList = new ArrayList<String>();
			        innerList.add(rs.getString("leave_type_id"));
			        
			        outerList.add(innerList);
			        
			        hmEmpLeaveMap.put(rs.getString("emp_id"), outerList);
			    }
				rs.close(); 
				pst.close();
	//		    System.out.println("hmMainBalance======>"+hmMainBalance);
			    
			    sbQuery=new StringBuilder();
			    sbQuery.append("select sum(accrued) as accrued,a.leave_type_id,a.emp_id from (select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
			    	"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id from emp_leave_type where is_constant_balance=false)) " +
			    	"and emp_id in("+strEmpIds+") and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr where  _type!='C' and a.leave_type_id=lr.leave_type_id and " +
			    	"a.daa<=lr._date and a.emp_id=lr.emp_id and a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+strEmpIds+")) and lr._date <? " +
	    			" group by a.emp_id,a.leave_type_id order by a.emp_id,leave_type_id");
			    pst = con.prepareStatement(sbQuery.toString());
			    pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
			    pst.setDate(2, uF.getDateFormat(strDate[0], DATE_FORMAT));
//			    System.out.println("4 pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmAccruedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmAccruedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued"));                
			    } 
				rs.close();
				pst.close();
	//		    System.out.println("hmAccruedBalance======>"+hmAccruedBalance);
				
				sbQuery=new StringBuilder();
			    sbQuery.append("select sum(accrued) as accrued,a.leave_type_id,a.emp_id from (select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
			   		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id from emp_leave_type where is_constant_balance=false)) " +
			   		"and emp_id in("+strEmpIds+") and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr where _type!='C' and a.leave_type_id=lr.leave_type_id and " +
	   				"a.daa<=lr._date and a.emp_id=lr.emp_id and a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+strEmpIds+")) and " +
					"lr._date between ? and ? and lr.compensate_id = 0 group by a.emp_id,a.leave_type_id order by a.emp_id,leave_type_id");
			    pst = con.prepareStatement(sbQuery.toString());
			    pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
			    pst.setDate(2, uF.getDateFormat(strDate[0], DATE_FORMAT));
			    pst.setDate(3, uF.getDateFormat(strDate[1], DATE_FORMAT));
//			    System.out.println("5 pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmCurrAccruedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmCurrAccruedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued"));                
			    } 
				rs.close();
				pst.close();
	//		    System.out.println("hmCurrAccruedBalance======>"+hmCurrAccruedBalance);
				
				sbQuery=new StringBuilder();
			    sbQuery.append("select sum(accrued) as accrued,a.leave_type_id,a.emp_id from (select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
			    	"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id from emp_leave_type where is_constant_balance=false)) " +
			    	"and emp_id in("+strEmpIds+") and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr where _type!='C' and a.leave_type_id=lr.leave_type_id and " +
	    			"a.daa<=lr._date and a.emp_id=lr.emp_id and a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+strEmpIds+")) and " +
					"lr._date between ? and ? and lr.compensate_id > 0 group by a.emp_id,a.leave_type_id order by a.emp_id,leave_type_id");
			    pst = con.prepareStatement(sbQuery.toString());
			    pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
			    pst.setDate(2, uF.getDateFormat(strDate[0], DATE_FORMAT));
			    pst.setDate(3, uF.getDateFormat(strDate[1], DATE_FORMAT));
//			    System.out.println("6 pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmAddedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmAddedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued"));                
			    } 
				rs.close();
				pst.close();
	//		    System.out.println("hmAddedBalance======>"+hmAddedBalance);
			    
				Map<String, String> hmPaidBalance=new HashMap<String, String>();
				
				for(int i=0; i<alEmp.size(); i++) {
					for(int j=0; j<alLeaveTypeIds.size(); j++) {
					    sbQuery=new StringBuilder();
					    sbQuery.append("select sum(leave_no) as count,leave_type_id,emp_id from (select a.daa,lar.* from (select max(_date) as daa,leave_type_id," +
				    		"emp_id from leave_register1 where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select " +
				    		"leave_type_id from emp_leave_type where is_constant_balance=false)) and register_id in (select max(register_id) as register_id " +
						    "from leave_register1 where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
						    "from emp_leave_type where is_constant_balance=false)) and emp_id in("+alEmp.get(i)+") and _date<=? and leave_type_id=? " +
				    		"group by emp_id,leave_type_id) and emp_id in("+alEmp.get(i)+") and _date<=? group by emp_id," +
		    				"leave_type_id) as a,leave_application_register lar where a.emp_id=lar.emp_id and (lar.is_modify is null or lar.is_modify=false) and " +
		    				"lar.leave_id in (select leave_id from emp_leave_entry where approval_to_date<?) and is_paid=true and (is_modify is null or is_modify=false) " +
		    				"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a where emp_id>0 and emp_id in (select emp_id from employee_official_details " +
		    				"where emp_id > 0 and emp_id in("+alEmp.get(i)+")) and a._date<? group by leave_type_id,emp_id order by emp_id,leave_type_id");
					    pst = con.prepareStatement(sbQuery.toString());
					    pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
					    pst.setInt(2, uF.parseToInt(alLeaveTypeIds.get(j)));
					    pst.setDate(3, uF.getDateFormat(strDate[1], DATE_FORMAT));
					    pst.setDate(4, uF.getDateFormat(strDate[0], DATE_FORMAT));
					    pst.setDate(5, uF.getDateFormat(strDate[0], DATE_FORMAT));
		//			    System.out.println("7 pst======>"+pst);    
					    rs = pst.executeQuery();
					    while (rs.next()) {
					    	hmPaidBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("count"));
					    }
						rs.close();
						pst.close();		
					}
				}
	//		    System.out.println("hmPaidBalance======>"+hmPaidBalance);
	//		    System.out.println("hmEmpLeaveMap======>"+hmEmpLeaveMap);
				
				
			    Iterator<String> it = hmEmpLeaveMap.keySet().iterator();
			    List<List<String>> reportListPrint = new ArrayList<List<String>>();
	//		    Map<String, List<String>> hm = new HashMap<String, List<String>>();
			    while(it.hasNext()){
			    	String strEmpId = it.next();
			    	
			    	
			    	List<String> alEmpLeave = hmEmpLeaves.get(strEmpId);
					if(alEmpLeave == null) alEmpLeave = new ArrayList<String>(); 
			    	
			    	 List<List<String>> outerList = hmEmpLeaveMap.get(strEmpId);
			    	 if(outerList == null) outerList = new ArrayList<List<String>>();
			    	 int nOuter = outerList.size();
			    	 for(int i=0;i<nOuter;i++){
			    		 List<String> innerList = outerList.get(i);
			    		 String leaveTypeId = innerList.get(0);
			    		 
			    		 if(!alEmpLeave.contains(leaveTypeId)){
							continue;
						}
			    		 
						double dblOpeningBalance = uF.parseToDouble(hmMainBalance.get(strEmpId+"_"+leaveTypeId));
						dblOpeningBalance += uF.parseToDouble(hmAccruedBalance.get(strEmpId+"_"+leaveTypeId));
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("strEmpId======>"+strEmpId);
	//						System.out.println("leaveTypeId======>"+leaveTypeId);
	//						System.out.println("dblOpeningBalance======>"+dblOpeningBalance);
	//						System.out.println("hmAccruedBalance======>"+hmAccruedBalance.get(strEmpId+"_"+leaveTypeId));
	//					}
						double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strEmpId+"_"+leaveTypeId));
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblPaidBalance======>"+dblPaidBalance);
	//					}
						if(dblOpeningBalance > 0 && dblOpeningBalance >= dblPaidBalance){
							dblOpeningBalance = dblOpeningBalance - dblPaidBalance; 
				        }
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblOpeningBalance======>"+dblOpeningBalance);
	//					}
						
						double dblCurrAccruedBalance = uF.parseToDouble(hmCurrAccruedBalance.get(strEmpId+"_"+leaveTypeId));
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblCurrAccruedBalance======>"+dblCurrAccruedBalance);
	//					}
						double dblhmAddedBalance = uF.parseToDouble(hmAddedBalance.get(strEmpId+"_"+leaveTypeId));
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblhmAddedBalance======>"+dblhmAddedBalance);
	//					}
						double dblTakenPaid = uF.parseToDouble(hmTakenPaid.get(strEmpId+"_"+leaveTypeId));
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblOpeningBalance======>"+dblOpeningBalance);
	//					}
						double dblTakenUnPaid = uF.parseToDouble(hmTakenUnPaid.get(strEmpId+"_"+leaveTypeId));
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblTakenUnPaid======>"+dblTakenUnPaid);
	//					}
						double dblPrevPaid = uF.parseToDouble(hmPreviousPaid.get(strEmpId+"_"+leaveTypeId));
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblPrevPaid======>"+dblPrevPaid);
	//					}
						double dblFuturePaid = uF.parseToDouble(hmFuturePaid.get(strEmpId+"_"+leaveTypeId));
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblFuturePaid======>"+dblFuturePaid);
	//					}
						
						double dblClosingBalance = (dblOpeningBalance + dblCurrAccruedBalance + dblhmAddedBalance) - dblTakenPaid;
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblOpeningBalance======>"+dblOpeningBalance);
	//					}
					//===start parvez date: 01-11-2022===	
//						dblClosingBalance = dblClosingBalance - dblTakenUnPaid;
						if(dblTakenUnPaid<0){
							dblClosingBalance = dblTakenUnPaid - dblClosingBalance;
						} else{
							dblClosingBalance = dblClosingBalance - dblTakenUnPaid;
						}
					//===end parvez date: 01-11-2022===	
						
						
	//					if(uF.parseToInt(strEmpId) == 431){
	//						System.out.println("dblClosingBalance======>"+dblClosingBalance);
	//					}
	//					dblClosingBalance = dblClosingBalance - dblPrevPaid;
	//					dblClosingBalance = dblClosingBalance - dblFuturePaid;
						
					//===start parvez date: 01-11-2022===	
						/*if(dblClosingBalance < 0.0d){ 
							dblClosingBalance = 0.0d;
						}*/ 
						
						if(hmFeatureStatus!=null && !uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
							if(dblClosingBalance < 0.0d){ 
								dblClosingBalance = 0.0d;
							}
						}
					//===end parvez date: 01-11-2022===	
						
						List<String> alInner = new ArrayList<String>();
						alInner.add(hmEmpCode.get(strEmpId));
						alInner.add(hmEmpName.get(strEmpId));
						alInner.add(hmLeavesColour.get(leaveTypeId));
						alInner.add(uF.showData(hmLeaveType.get(leaveTypeId), ""));
						
						alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblOpeningBalance));
						alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblCurrAccruedBalance));
						alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblhmAddedBalance));
						alInner.add(""+uF.formatIntoTwoDecimalWithOutComma(dblClosingBalance));
						alInner.add(""+uF.formatIntoTwoDecimalWithOutComma(dblTakenPaid));
						alInner.add(""+uF.formatIntoTwoDecimalWithOutComma(dblTakenUnPaid));
						alInner.add(""+uF.formatIntoTwoDecimalWithOutComma(dblPrevPaid));
						alInner.add(""+uF.formatIntoTwoDecimalWithOutComma(dblFuturePaid));
						
						reportListPrint.add(alInner);   
			    	 }
			    }
				request.setAttribute("reportListPrint", reportListPrint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public String loadLeaveCard(UtilityFunctions uF) {
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

	public String getProfileEmpId() {
		return profileEmpId;
	}

	public void setProfileEmpId(String profileEmpId) {
		this.profileEmpId = profileEmpId;
	}
}