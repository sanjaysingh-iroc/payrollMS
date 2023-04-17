package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveRegularise extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
//	String paycycle;
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
//	List<FillPayCycles> paycycleList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	String exportType;
	String loginSubmit;
	String strEffectiveDate;
	String exceldownload;
	
	public String execute() throws Exception {
  
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		  
		request.setAttribute(TITLE, "Leave Regularise");
		request.setAttribute(PAGE, "/jsp/leave/LeaveRegularise.jsp");
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}

//		System.out.println("====>ExcelDownload==>"+getExceldownload());
//		System.out.println("getStrLocation() ===>> " + getStrLocation());
		
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
		
//		System.out.println("getLoginSubmit() ===>> " + getLoginSubmit());
		
		if(getLoginSubmit() !=null){
			addRegulariseBalance(uF);
		}
		if(getStrEffectiveDate() == null || getStrEffectiveDate().trim().equals("") || getStrEffectiveDate().trim().equalsIgnoreCase("NULL")){
			setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		}
		viewLeaveRegularise(uF);
		
		return loadLeaveRegularise(uF);

	}
	

	public void addRegulariseBalance(UtilityFunctions uF){

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date effectiveDate = uF.getDateFormatUtil(getStrEffectiveDate(), DATE_FORMAT,DATE_FORMAT);
//			System.out.println("currDate==>"+currDate+"--effectiveDate==>"+effectiveDate);
			
			String[] employee = request.getParameterValues("employee");
			
			for(int i = 0; employee !=null && i < employee.length; i++){
				String[] strTemp = employee[i].split("_");
				String strEmpId = strTemp[0];
				String strLeaveTypeId = strTemp[1];
				
				if(uF.parseToInt(strEmpId) > 0 && uF.parseToInt(strLeaveTypeId) > 0){
					String strLeaveBalance = (String) request.getParameter("strBalance_"+strEmpId+"_"+strLeaveTypeId);
					
			//===start parvez date: 19-11-2021===		
//					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date=? and leave_type_id=?");
					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date>=? and leave_type_id=?");
			//===end parvez date: 19-11-2021===
					pst.setInt(1,  uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strLeaveTypeId));
					System.out.println("LR/150--pst="+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id," +
							" _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
					pst.setDouble(1, 0);
					pst.setDouble(2, 0);
					pst.setDouble(3, 0);
					pst.setInt(4, uF.parseToInt(strEmpId));
					pst.setInt(5, uF.parseToInt(strLeaveTypeId));
					pst.setDate(6, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
					pst.setInt(7, 1);
					pst.setString(8, "C");
					pst.setDouble(9, uF.parseToDouble(strLeaveBalance));
					pst.setInt(10, 0);
					System.out.println("LR/165--pst="+pst);
					int x = pst.executeUpdate();
					pst.close();
					
					if(x > 0){
						
						String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId);
						String strEmpWLocationId = CF.getEmpWlocationId(con, uF, strEmpId);
						String strEmpLevelId = CF.getEmpLevelId(con, strEmpId);
						
						pst = con.prepareStatement("select * from emp_leave_type where leave_type_id in (select leave_type_id from leave_type " +
								"where is_compensatory = false and is_work_from_home = false and leave_type_id=? and org_id=?) and is_constant_balance=false and leave_type_id=? " +
								"and org_id=? and is_leave_accrual=true and accrual_type=2 and is_accrued_cal_days=true and level_id=? " +
								"and wlocation_id=?");
						pst.setInt(1, uF.parseToInt(strLeaveTypeId));
						pst.setInt(2, uF.parseToInt(strEmpOrgId));
						pst.setInt(3, uF.parseToInt(strLeaveTypeId));
						pst.setInt(4, uF.parseToInt(strEmpOrgId));
						pst.setInt(5, uF.parseToInt(strEmpLevelId));
						pst.setInt(6, uF.parseToInt(strEmpWLocationId));
						rs = pst.executeQuery();
						boolean isAccrueCalDays = false;
						double dblAccrueBalance = 0.0d;
						while(rs.next()){
							isAccrueCalDays = true;
							dblAccrueBalance = uF.parseToDouble(rs.getString("no_of_leave_monthly"));
						}
						rs.close();
						pst.close();
						
						if(isAccrueCalDays){
							pst = con.prepareStatement("delete from leave_register1 where emp_id=? and _date > ? and leave_type_id=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
							pst.setInt(3, uF.parseToInt(strLeaveTypeId));
							pst.execute();
							pst.close();
							
							if(currDate.after(effectiveDate)){
								java.sql.Date tomorrowDate =  uF.getFutureDate(effectiveDate, 1);
								
								String strDateDiff = uF.dateDifference(uF.getDateFormat(""+tomorrowDate, DBDATE, DATE_FORMAT), DATE_FORMAT, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT,CF.getStrTimeZone());
			                 	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
			                    int dayCnt = uF.parseToInt(strDateDiff);
			                    
//			                    System.out.println("tomorrowDate==>"+tomorrowDate+"--dayCnt==>"+dayCnt);
			                    
			                    Calendar cal = GregorianCalendar.getInstance();
			                    cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(""+tomorrowDate, DBDATE, "dd")));
			                    cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+tomorrowDate, DBDATE, "MM")) - 1);
			                    cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+tomorrowDate, DBDATE, "yyyy")));
			                    
			                    for (int j = 0; j < dayCnt; j++) {
			                    	Date dtCurrent = uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT);
									cal.add(Calendar.DATE, 1);
									
									pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id," +
											" _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
									pst.setDouble(1, 0);
									pst.setDouble(2, 0);
									pst.setDouble(3, dblAccrueBalance);
									pst.setInt(4, uF.parseToInt(strEmpId));
									pst.setInt(5, uF.parseToInt(strLeaveTypeId));
									pst.setDate(6, dtCurrent);
									pst.setInt(7, 1);
									pst.setString(8, "A");
									pst.setDouble(9, 0.0d);
									pst.setInt(10, 0);
									pst.execute();
									pst.close();
			                    }
			                    
							}
						}
					}					
				}
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+"Leave updated Successfully."+END);
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Leave updated failed."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void viewLeaveRegularise(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		List<List<String>> reportList = new ArrayList<List<String>>();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			
			Map<String, String> hmLeaveType = CF.getLeaveTypeMap(con);
			if(hmLeaveType == null) hmLeaveType = new HashMap<String, String>();
			Map<String, String> hmOrg = CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_type elt, leave_type lt where elt.leave_type_id=lt.leave_type_id and " +
					"elt.leave_type_id in (select leave_type_id from leave_type where is_compensatory = false and is_work_from_home = false ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(") and elt.is_constant_balance=false ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and elt.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and elt.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}	
			sbQuery.append(" and (lt.is_leave_opt_holiday is null or lt.is_leave_opt_holiday=false) order by elt.level_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			List<String> alLeaveType = new ArrayList<String>();
			Map<String, List<String>> hmLeavesType = new HashMap<String, List<String>>();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("leave_type_id"))>0){
					if(!alLeaveType.contains(rs.getString("leave_type_id"))){
						alLeaveType.add(rs.getString("leave_type_id"));
					}

					List<String> alLeave = hmLeavesType.get(rs.getString("level_id")+"_"+rs.getString("wlocation_id"));
					if(alLeave == null) alLeave = new ArrayList<String>();
					alLeave.add(rs.getString("leave_type_id"));
					
					hmLeavesType.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id"), alLeave);
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod, probation_policy pp " +
						"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id and is_alive = true " +
						"and emp_per_id>0 and (employment_end_date is null OR employment_end_date <= ?)");
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(" and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') " +
					"and epd.joining_date is not null order by eod.emp_id");
            /*sbQuery.append(" and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') " +
						"and epd.joining_date is not null order by emp_fname, emp_lname,eod.emp_id");*/
		    pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//		    System.out.println("pst====>"+pst); 
		    rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			Map<String, List<String>> hmEmpLeaves = new HashMap<String, List<String>>();
			while(rs.next()){
				int nLevelId = uF.parseToInt(hmEmpLevelMap.get(rs.getString("emp_id")));
				
				List<String> alLeave = hmLeavesType.get(nLevelId+"_"+rs.getString("wlocation_id"));
				if(alLeave == null) alLeave = new ArrayList<String>();
				
				String strAllowedLeaves = rs.getString("leaves_types_allowed");
				if(strAllowedLeaves!=null && strAllowedLeaves.length()>0){
					List<String> al = Arrays.asList(strAllowedLeaves.split(","));
					for(String leaveTypeId : al){
 						if(uF.parseToInt(leaveTypeId) > 0 && alLeaveType.contains(leaveTypeId) && alLeave.contains(leaveTypeId)){
							if(!alEmp.contains(rs.getString("emp_id"))){
								alEmp.add(rs.getString("emp_id"));
							}
							
							List<String> alEmpLeave = hmEmpLeaves.get(rs.getString("emp_id"));
							if(alEmpLeave == null) alEmpLeave = new ArrayList<String>();
							alEmpLeave.add(leaveTypeId);
							
							hmEmpLeaves.put(rs.getString("emp_id"), alEmpLeave);
						}
					}
				}				
			}
			rs.close();
			pst.close();
			
			int nAlEmp = alEmp.size();
			for(int i = 0; i < nAlEmp; i++){
				int nEmpId = uF.parseToInt(alEmp.get(i));
				
				List<String> alEmpLeave = hmEmpLeaves.get(alEmp.get(i));
				if(alEmpLeave == null) alEmpLeave = new ArrayList<String>(); 
				
				for(int j = 0; j < alLeaveType.size(); j++){
					int nLeaveTypeId = uF.parseToInt(alLeaveType.get(j));
					if(!alEmpLeave.contains(""+nLeaveTypeId)){
						continue;
					}
					
					pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
							"where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?)" +
							" group by emp_id,leave_type_id) and leave_type_id=?");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nLeaveTypeId);
		            pst.setInt(3, nLeaveTypeId);
		            rs = pst.executeQuery();
		            Map<String, String> hmMainBalance=new HashMap<String, String>();
		            while (rs.next()) {
		                hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
		            }
		            rs.close();
		            pst.close();
		            
		            pst = con.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id " +
		            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) " +
		            		"group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id " +
		            		"and a.daa<=lr._date and lr.leave_type_id=? group by a.leave_type_id");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nLeaveTypeId);
		            pst.setInt(3, nEmpId);
		            pst.setInt(4, nLeaveTypeId);
		            rs = pst.executeQuery();
		            Map<String, String> hmAccruedBalance=new HashMap<String, String>();
		            while (rs.next()) {
		            	hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));                
		            }
					rs.close();
					pst.close();
					
		            pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
		            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) " +
		            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) " +
		            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=?) as a group by leave_type_id");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nLeaveTypeId);
		            pst.setInt(3, nEmpId);
		            pst.setInt(4, nLeaveTypeId);
		            rs = pst.executeQuery();
		            Map<String, String> hmPaidBalance=new HashMap<String, String>();
		            while (rs.next()) {
		            	hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
		            }
					rs.close();
					pst.close();
					
					double dblBalance = uF.parseToDouble(hmMainBalance.get(""+nLeaveTypeId));
					dblBalance += uF.parseToDouble(hmAccruedBalance.get(""+nLeaveTypeId));
					
					double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(""+nLeaveTypeId));
					
					if(dblBalance > 0 && dblBalance >= dblPaidBalance){
			            dblBalance = dblBalance - dblPaidBalance; 
			        }
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(""+nEmpId);//0
					alInner.add(hmEmpCode.get(""+nEmpId));//1
					alInner.add(hmEmpName.get(""+nEmpId));//2
					alInner.add(uF.showData(hmLeaveType.get(""+nLeaveTypeId), ""));//3
					alInner.add(""+nLeaveTypeId);//4
					alInner.add(""+dblBalance);//5
					
					reportList.add(alInner);
				}
			}
			request.setAttribute("reportList", reportList);
			
			try {
				if(!getExceldownload().equalsIgnoreCase("null") && getExceldownload()!=null)
				{
					generateLeaveRegulariseExcel(uF,"LeaveRegularise",reportList);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	
	
private void generateLeaveRegulariseExcel(UtilityFunctions uF ,String filename , List<List<String>> reportList ) {
		
		try {	
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Leave Regularise");
			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Leave Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Leave Balance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Effective From ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			
			for (int i = 0; i < reportList.size(); i++) {
				List<String> cinnerlist = (List<String>) reportList.get(i);
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(cinnerlist.get(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(cinnerlist.get(1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(cinnerlist.get(2),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(cinnerlist.get(3),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(cinnerlist.get(5),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(strEffectiveDate,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				reportData.add(alInnerExport);
			}
	
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
//			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);
			sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=LeaveRegularise.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	public String loadLeaveRegularise(UtilityFunctions uF) {
		
//		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
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
		
//		alFilter.add("PAYCYCLE");
//		String strPaycycle = "";
//		String[] strPayCycleDates = null;
//		if (getPaycycle() != null) {
//			strPayCycleDates = getPaycycle().split("-");
//			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//			
//			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
//		}
//		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

//	public String getPaycycle() {
//		return paycycle;
//	}
//
//	public void setPaycycle(String paycycle) {
//		this.paycycle = paycycle;
//	}

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

//	public List<FillPayCycles> getPaycycleList() {
//		return paycycleList;
//	}
//
//	public void setPaycycleList(List<FillPayCycles> paycycleList) {
//		this.paycycleList = paycycleList;
//	}

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


	public String getLoginSubmit() {
		return loginSubmit;
	}


	public void setLoginSubmit(String loginSubmit) {
		this.loginSubmit = loginSubmit;
	}


	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}


	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
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


	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}
	
}
