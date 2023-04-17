package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeActivityList extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
//	String strSessionUserType;
	String strUserTypeId;

	String strBaseUserType ;
	String strBaseUserTypeId ;
	CommonFunctions CF;
	
	String strUserType;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
//		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("inside employeeactivityList");
		getNewJoineeEmp(uF);
		getConfirmationEmp(uF);
		getResignAndFinalDayEmp(uF);
		getRetirementEmps(uF);
		
		return LOAD;
	}

	
	private void getRetirementEmps(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst=null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
					
			List<String> retirementEmpList = new ArrayList<String>();
			Map<String, String> hmwLocation = CF.getWLocationMap(con, null, null);
			
			Map<String, String> hmOrgRetirementAge = new HashMap<String, String>();
			pst = con.prepareStatement("select * from org_details");
			rst = pst.executeQuery();
			while(rst.next()) {
				hmOrgRetirementAge.put(rst.getString("org_id"), rst.getString("retirement_age"));
			}
			rst.close();
			pst.close();
			System.out.println("hmOrgRetirementAge ===>> " + hmOrgRetirementAge);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and approved_flag=true and is_alive=true and emp_filled_flag=true and joining_date is not null ");
			
			 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	} else {
	        		sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	}
			}
	            
	        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	} else {
	        		sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	}
			}
	        sbQuery.append(" order by epd.emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				if(rst.getString("emp_date_of_birth")==null) {
					continue;
				}
				String strEmpAge = uF.getTimeDurationBetweenDatesWithYearMonthDays(rst.getString("emp_date_of_birth"), DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF, uF, request);
//				System.out.println(rst.getString("emp_per_id") +" --- " + rst.getString("emp_fname") + " " + rst.getString("emp_lname") + " -- strEmpAge ===>> " + strEmpAge);
				if(strEmpAge != null) {
					String[] strTmpEmpAge = strEmpAge.split("::::");
					String strRetireAge = hmOrgRetirementAge.get(rst.getString("org_id"));
					if(uF.parseToInt(strTmpEmpAge[0]) >= uF.parseToInt(strRetireAge) || (uF.parseToInt(strTmpEmpAge[0]) == (uF.parseToInt(strRetireAge)-1) && uF.parseToInt(strTmpEmpAge[1]) == 11)) {
						StringBuilder sbRetirementList = new StringBuilder();
						String empimg = uF.showData(rst.getString("emp_image"), "avatar_photo.png");
						String supervisorName = CF.getEmpNameMapByEmpId(con, rst.getString("supervisor_emp_id"));
						String empDesignation = CF.getEmpDesigMapByEmpId(con, rst.getString("emp_per_id"));
						if(rst.getString("joining_date")!=null && !rst.getString("joining_date").equals("")) {
							String empDOBDate = uF.getDateFormat(rst.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT);
							String strDay = uF.getDateFormat(empDOBDate, DATE_FORMAT, "dd");
							String strMonth = uF.getDateFormat(empDOBDate, DATE_FORMAT, "MM");
							String strYear = uF.getDateFormat(empDOBDate, DATE_FORMAT, "yyyy");
							int intRetirementYr = uF.parseToInt(strYear)+uF.parseToInt(strRetireAge);
							String retirementDate = strDay+"/"+strMonth+"/"+intRetirementYr;		
							
							if(CF.getStrDocRetriveLocation()==null) { 
								sbRetirementList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
							} else { 
								sbRetirementList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rst.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
							}  
							sbRetirementList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
							
							String strEmpMName = "";
							if(flagMiddleName) {
								if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
									strEmpMName = " "+rst.getString("emp_mname");
								}
							}
						
							
							sbRetirementList.append(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
							sbRetirementList.append(" designation is "+uF.showData(empDesignation, "-"));
							sbRetirementList.append(", manager is "+uF.showData(supervisorName, "-"));
							sbRetirementList.append(", work location is "+uF.showData(hmwLocation.get(rst.getString("wlocation_id")), "-"));
							sbRetirementList.append(" and retirement date is "+retirementDate+".");
							sbRetirementList.append("</span>");
							
							retirementEmpList.add(sbRetirementList.toString());
						}
					}
				}
			}
			rst.close();
			pst.close();
			
//			System.out.println("retirementEmpList==>"+retirementEmpList.size());
			request.setAttribute("retirementEmpList", retirementEmpList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	/*public String getTimeDurationBetweenDatesWithYearMonthDays(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
		
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			sbTimeDuration.append(period.getYears()+"::::"+period.getMonths()+"::::"+period.getDays());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sbTimeDuration.toString();
	}*/


	private void getConfirmationEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst=null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
					
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpProbation = new HashMap<String, String>();
			
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date tommorowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2),DBDATE, DATE_FORMAT),DATE_FORMAT );
			
			StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select * from probation_policy order by emp_id desc");
			pst = con.prepareStatement(sbQuery1.toString());
			rst = pst.executeQuery();
			while(rst.next()) {
				int probation = uF.parseToInt((String)rst.getString("probation_duration")) + uF.parseToInt((String)rst.getString("extend_probation_duration"));
				hmEmpProbation.put((String)rst.getString("emp_id"),String.valueOf(probation) );
			}
			rst.close();
			pst.close();
			
			List<String> confirmationEmpList = new ArrayList<String>();
			Map<String, String> hmwLocation = CF.getWLocationMap(con, null, null);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and epd.emp_status = 'PROBATION' and approved_flag=true and is_alive=true and emp_filled_flag=true and joining_date is not null ");
			
			 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
	        
	        sbQuery.append(" order by epd.emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
			while (rst.next()) {
				StringBuilder sbConfirmationList = new StringBuilder();
				String empimg = uF.showData(rst.getString("emp_image"), "avatar_photo.png");
				String supervisorName = CF.getEmpNameMapByEmpId(con, rst.getString("supervisor_emp_id"));
				String empDesignation = CF.getEmpDesigMapByEmpId(con, rst.getString("emp_per_id"));
				if(rst.getString("joining_date")!=null && !rst.getString("joining_date").equals("")) {
					String joiningDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE,DATE_FORMAT);
					java.util.Date startDate = uF.getDateFormatUtil(joiningDate,DATE_FORMAT );
					
					int probation = uF.parseToInt(hmEmpProbation.get((String)rst.getString("emp_per_id")));
					String confirmationDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE,CF.getStrReportDateFormat());		
					
					String futureDate = uF.getDateFormat(""+uF.getFutureDate(startDate, probation),DBDATE,DATE_FORMAT);
					java.util.Date confDate = null;
					
					if(probation>0) {
						confDate = uF.getDateFormatUtil(futureDate,DATE_FORMAT );
						confirmationDate =  uF.getDateFormat(""+uF.getFutureDate(startDate, probation),DBDATE, CF.getStrReportDateFormat());
					} else {
						confDate = uF.getDateFormatUtil(joiningDate,DATE_FORMAT );
					}
					
					if(CF.getStrDocRetriveLocation()==null) { 
						sbConfirmationList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
					} else { 
						sbConfirmationList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rst.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
					}  
					sbConfirmationList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}
				
					
					sbConfirmationList.append(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
					sbConfirmationList.append(" designation is "+uF.showData(empDesignation, "-"));
					sbConfirmationList.append(", manager is "+uF.showData(supervisorName, "-"));
					sbConfirmationList.append(", work location is "+uF.showData(hmwLocation.get(rst.getString("wlocation_id")), "-"));
					sbConfirmationList.append(" and confirmation date is "+confirmationDate+".");
					sbConfirmationList.append("</span>");
					
					if(confDate.equals(currDate) || confDate.equals(tommorowDate) || confDate.equals(dayAfterTomorrowDate) || confDate.before(currDate)) {
						confirmationEmpList.add(sbConfirmationList.toString());
					} 
				}
			}
			rst.close();
			pst.close();
			
//			System.out.println("confirmationEmpList==>"+confirmationEmpList.size());
			request.setAttribute("confirmationEmpList", confirmationEmpList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private void getResignAndFinalDayEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
		
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpProbation = new HashMap<String, String>();
			
			List<String> resignationEmpList = new ArrayList<String>();
			List<String> finalDayEmpList = new ArrayList<String>();
			Map<String, String> hmwLocation = CF.getWLocationMap(con, null, null);
			
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2),DBDATE, DATE_FORMAT),DATE_FORMAT );
			//1		
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where "
					+" ((is_approved=0 and effective_type='"+WORK_FLOW_RESIGN+"') or (is_approved=1 and effective_type='"+WORK_FLOW_TERMINATION+"'))" 
					+ " and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from "
					+" employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	       if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			//2
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and ((is_approved=0 and effective_type='"+WORK_FLOW_RESIGN+"') or (is_approved=1 and effective_type='"+WORK_FLOW_TERMINATION+"')) " 
					+" and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from employee_personal_details epd, "
					+ " employee_official_details eod where epd.emp_per_id = eod.emp_id ");
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	         if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append("))");
			if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strUserTypeId));	
			if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			}
			
//			System.out.println("pst2==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			//3
			sbQuery=new StringBuilder();
			sbQuery.append("select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	         if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append(") and approved_1=-1 and approved_2=-1 ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst3==>"+pst);
			rs = pst.executeQuery();	
			List<String> deniedList=new ArrayList<String>();
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("off_board_id")) ){
					deniedList.add(rs.getString("off_board_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			//4
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_RESIGN+"' " +
					"and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	       if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst4==>"+pst);
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();
			
			//5
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"') and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
		
			if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	         if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append("))  group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst5==>"+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			//6
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"') and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			
			 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	         if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst6==>"+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			//7
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"') and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			
			 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst7==>"+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			//8
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"')" +
					" and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " 
					+" where epd.emp_per_id = eod.emp_id ");
			
			 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			
			sbQuery.append(")) ");
			if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strUserTypeId));
			if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			}
//			System.out.println("pst8==>"+pst);
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			

			//9
			sbQuery=new StringBuilder();
			sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id " +
					" and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id and epd.emp_per_id in (select emp_id from user_details where status != 'INACTIVE') ");
			
			 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
	        
			sbQuery.append(") e, work_flow_details wfd where e.off_board_id = wfd.effective_id and (wfd.effective_type = '"+WORK_FLOW_RESIGN+"' or wfd.effective_type = '"+WORK_FLOW_TERMINATION+"') ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(CEO) || strBaseUserType.equalsIgnoreCase(HOD))) {
					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by e.entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("empActivity resig pst==>"+pst);
			rs = pst.executeQuery();
			List<String> alList = new ArrayList<String>();
			while(rs.next()) {
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("off_board_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				

				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					
					continue;
					
				}
				
				String userType = rs.getString("user_type");				
				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) && alList.contains(rs.getString("off_board_id"))){
				
					continue;
				} else if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) && !alList.contains(rs.getString("off_board_id"))){
					userType = strUserTypeId;
				
					alList.add(rs.getString("off_board_id"));
				}
				
				StringBuilder sbResignationList = new StringBuilder();
				StringBuilder sbFinalDayList = new StringBuilder();
				String empimg = uF.showData(rs.getString("emp_image"), "avatar_photo.png");
				String supervisorName = CF.getEmpNameMapByEmpId(con, rs.getString("supervisor_emp_id"));
				String empDesignation = CF.getEmpDesigMapByEmpId(con, rs.getString("emp_per_id"));
				if(rs.getString("emp_status")!=null && rs.getString("emp_status").equalsIgnoreCase(RESIGNED)) {	
					if(rs.getString("entry_date")!=null && !rs.getString("entry_date").equals("")) {
						String lastDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT);
						java.util.Date regDate = uF.getDateFormatUtil(lastDate,DATE_FORMAT );
						
						String resignationDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat());
						if(CF.getStrDocRetriveLocation()==null) { 
							sbResignationList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
						} else { 
							sbResignationList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
						}  
						sbResignationList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
					
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
					
						
						sbResignationList.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						sbResignationList.append(" designation is "+uF.showData(empDesignation, "-"));
						sbResignationList.append(", manager is "+uF.showData(supervisorName, "-"));
						sbResignationList.append(", work location is "+uF.showData(hmwLocation.get(rs.getString("wlocation_id")), "-"));
						sbResignationList.append(" and resignation date is "+resignationDate+".");
						sbResignationList.append("</span>");
						
						if(regDate.equals(currDate) ) {
							resignationEmpList.add(sbResignationList.toString());
						} else {
							if(rs.getBoolean("is_alive")) {
	//							if(rs.getString("emp_status")!=null && !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")) {	
									resignationEmpList.add(sbResignationList.toString());
	//							}
							}
						}
							
					}
				}
				
				if(rs.getString("emp_status")!=null && (rs.getString("emp_status").equalsIgnoreCase(RESIGNED) || rs.getString("emp_status").equalsIgnoreCase(TERMINATED))) {	
				    if(rs.getString("last_day_date")!=null && !rs.getString("last_day_date").equals("")) {
						String resignDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, DATE_FORMAT);
						java.util.Date resignationDate = uF.getDateFormatUtil(resignDate,DATE_FORMAT );
					
						String finalDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, CF.getStrReportDateFormat());
	
						if(CF.getStrDocRetriveLocation()==null) { 
							sbFinalDayList.append("<span style=\"float: left; width:20px; height:20px;  \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
						} else { 
							sbFinalDayList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
						}  
						sbFinalDayList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
					
						
						sbFinalDayList.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						sbFinalDayList.append(" designation is "+uF.showData(empDesignation, "-"));
						sbFinalDayList.append(", manager is "+uF.showData(supervisorName, "-"));
						sbFinalDayList.append(", work location is "+uF.showData(hmwLocation.get(rs.getString("wlocation_id")), "-"));
						sbFinalDayList.append(" and final day is "+finalDate+".");
						sbFinalDayList.append("</span>");
						
						if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1) {
							if(resignationDate.equals(currDate) ){
								finalDayEmpList.add(sbFinalDayList.toString());
							}else if(resignationDate.equals(tomorrowDate)){
								finalDayEmpList.add(sbFinalDayList.toString());
							}else if(resignationDate.equals(dayAfterTomorrowDate)){
								finalDayEmpList.add(sbFinalDayList.toString());
							}else if(resignationDate.before(currDate)){
								if(rs.getBoolean("is_alive")) {
									finalDayEmpList.add(sbFinalDayList.toString());
								}
							}
							
						}
				    }
				}
			}
			rs.close();
			pst.close();
				
			/*System.out.println("resignationEmpList==>"+resignationEmpList.size());
			System.out.println("finalDayEmpList==>"+finalDayEmpList.size());*/
			
			request.setAttribute("resignationEmpList", resignationEmpList);
			request.setAttribute("finalDayEmpList", finalDayEmpList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getNewJoineeEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			List<String> newJoineeEmpList = new ArrayList<String>();
			Map<String, String> hmwLocation = CF.getWLocationMap(con, null, null);
			

			java.sql.Date dayAfterTomorrowDate1 =  uF.getFutureDate(CF.getStrTimeZone(),2);
			java.sql.Date tomorrowDate1 =  uF.getFutureDate(CF.getStrTimeZone(),1);
			
			java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+tomorrowDate1,DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+dayAfterTomorrowDate1,DBDATE, DATE_FORMAT),DATE_FORMAT );
			
			/*StringBuilder strQuery = new StringBuilder();
			strQuery.append("select emp_per_id,emp_fname,emp_lname,emp_image,supervisor_emp_id,wlocation_id,cad.candidate_joining_date from " +
					"employee_personal_details epd, candidate_application_details cad, employee_official_details eod where " +
					"epd.emp_per_id = eod.emp_id and epd.emp_per_id = cad.candididate_emp_id and emp_per_id in(select candididate_emp_id from " +
					"candidate_application_details where candidate_joining_date >= ? and candidate_joining_date <= ? and " +
					"candididate_emp_id is not null) order by candididate_emp_id desc");
			pst = con.prepareStatement(strQuery.toString());
			
			pst.setDate(1, uF.getPrevDate(uF.getCurrentDate(CF.getStrTimeZone())+"", 7));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			System.out.println("pst ===>>>> " + pst);
*/	
			//get today,tomorrow,day after tomorrow induction
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id "
					        +"and (joining_date =? or joining_date =? or joining_date =?) and joining_date is not null ");
			
			 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
	        
	        sbQuery.append(" order by emp_per_id desc");
	        pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, tomorrowDate1);
			pst.setDate(3, dayAfterTomorrowDate1);
//			System.out.println("employee activity induction pst1==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				StringBuilder sbNewjoineeList = new StringBuilder();
				String empimg = uF.showData(rs.getString("emp_image"), "avatar_photo.png");
				String supervisorName = CF.getEmpNameMapByEmpId(con, rs.getString("supervisor_emp_id"));
				String empDesignation = CF.getEmpDesigMapByEmpId(con, rs.getString("emp_per_id"));
				
//				sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #000 \"><img src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() + empimg +"\" height=\"20\" width=\"20\"> </span>");
				if(CF.getStrDocRetriveLocation()==null) { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px;  \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
				} else { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px;  \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
				}  
				sbNewjoineeList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				sbNewjoineeList.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				sbNewjoineeList.append(" designation is "+uF.showData(empDesignation, "-"));
				sbNewjoineeList.append(", manager is "+uF.showData(supervisorName, "-"));
				sbNewjoineeList.append(", work location is "+uF.showData(hmwLocation.get(rs.getString("wlocation_id")), "-"));
				sbNewjoineeList.append(" and joining date is "+uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat())+".");
				sbNewjoineeList.append("</span>");
				
				newJoineeEmpList.add(sbNewjoineeList.toString());
			}
			rs.close();
			pst.close();
			
			//get pending induction
			StringBuilder sbQuery3 = new StringBuilder();
			sbQuery3.append("select * from employee_personal_details  epd,employee_official_details eod where epd.emp_per_id = eod.emp_id and (joining_date is null or joining_date < ?)"
					+" and ((approved_flag=false and is_alive=false and emp_filled_flag=false) or (approved_flag=false and is_alive=false and emp_filled_flag=true))");
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery3.append(" and (wlocation_id is null or wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
	        	  } else {
	        		  sbQuery3.append(" and (wlocation_id is null or wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+"))");
	        	  }
			}
	            
	        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery3.append(" and (org_id is null or org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
	        	  } else {
	        		  sbQuery3.append(" and (org_id is null or org_id in ("+(String)session.getAttribute(ORGID)+"))");
	        	  }
			}
	        
	        sbQuery3.append(" order by emp_per_id desc");
			pst = con.prepareStatement(sbQuery3.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("employee activity induction pst2==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				StringBuilder sbNewjoineeList = new StringBuilder();
				String empimg = uF.showData(rs.getString("emp_image"), "avatar_photo.png");
				String supervisorName = CF.getEmpNameMapByEmpId(con, rs.getString("supervisor_emp_id"));
				String empDesignation = CF.getEmpDesigMapByEmpId(con, rs.getString("emp_per_id"));
				
//				sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #000 \"><img src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() + empimg +"\" height=\"20\" width=\"20\"> </span>");
				if(CF.getStrDocRetriveLocation()==null) { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
				} else { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
				}  
				sbNewjoineeList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				sbNewjoineeList.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				sbNewjoineeList.append(" designation is "+uF.showData(empDesignation, "-"));
				sbNewjoineeList.append(", manager is "+uF.showData(supervisorName, "-"));
				sbNewjoineeList.append(", work location is "+uF.showData(hmwLocation.get(rs.getString("wlocation_id")), "-"));
				sbNewjoineeList.append(" and joining date is "+uF.showData(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat())+".","-"));
				sbNewjoineeList.append("</span>");
				
				newJoineeEmpList.add(sbNewjoineeList.toString());
			}
			rs.close();
			pst.close();
			
//			System.out.println("newJoineeEmpList==>"+newJoineeEmpList.size());
			request.setAttribute("newJoineeEmpList", newJoineeEmpList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

}
