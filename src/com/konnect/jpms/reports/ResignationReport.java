package com.konnect.jpms.reports;

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
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class ResignationReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 *  
	 */ 
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ResignationReport.class);
	 
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	CommonFunctions CF;
	
	String strLocation;
	String strDepartment;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillOrganisation> organisationList;
	
	String currUserType;
	
	String alertID;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID); 
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
				
		request.setAttribute(PAGE, PReportResignation);
		request.setAttribute(TITLE, TResignationReport);
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(getF_org()==null) {
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
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		getOffboardStatus(uF);
		
		if(request.getParameter("operation")!=null && request.getParameter("operation").equalsIgnoreCase("U")) {
			return updateNoticePeriod(request.getParameter("value"), request.getParameter("id"), uF);
		} else {
			viewResignationReport(uF);
			return loadResignationReport(uF);
		}
	}
	
	public String loadResignationReport(UtilityFunctions uF) {
		getSelectedFilter(uF);
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				int k=0;
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
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
			
		}

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public void getOffboardStatus(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org())); 
		
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select emp_id from emp_offboard_status where status=false group by emp_id");
			rs=pst.executeQuery();
			Map<String,String> statuaMp=new HashMap<String,String>();
			while(rs.next()) {
				statuaMp.put(rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("statuaMp", statuaMp);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public String updateNoticePeriod(String strNoticeDays,String strId, UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from emp_off_board where off_board_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			int nEmpId = 0;
			java.util.Date utDate = null;
			while(rs.next()) {
				nEmpId = rs.getInt("emp_id");
				utDate = uF.getDateFormatUtil(rs.getString("entry_date"), DBDATE);
			}
			rs.close();
			pst.close();
			
			int nWLocation = 0;
			int nDepartment = 0;
			int nLevel = 0;
			int nDesignation = 0;
			int nGrade = 0;
			int nActivity = 0;
			int nNoticePeriod = 0;
			int nProbationPeriod = 0;
			String strStatusCode = null;
			
			pst = con.prepareStatement(selectEmpActivityDetails1);
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				nWLocation = rs.getInt("wlocation_id");
				nDepartment = rs.getInt("department_id");
				nLevel = rs.getInt("level_id");
				nDesignation = rs.getInt("desig_id");
				nGrade = rs.getInt("grade_id");
				nActivity = rs.getInt("activity_id");
				nNoticePeriod = rs.getInt("notice_period");
				nProbationPeriod = rs.getInt("probation_period");
				strStatusCode = rs.getString("emp_status_code"); 
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strEmpName = null;
			if(rs.next()) {
				strEmpName  = rs.getString("emp_fname")+" "+rs.getString("emp_lname");
			}
			rs.close();
			pst.close();
			 
			String strReason = "updated by "+strEmpName;
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertEmpActivity);
			pst.setInt(1, nWLocation);
			pst.setInt(2, nDepartment);
			pst.setInt(3, nLevel);
			pst.setInt(4, nDesignation);
			pst.setInt(5, nGrade);
			pst.setString(6, strStatusCode);
			pst.setInt(7, 8); // 8 for notice
			pst.setString(8, strReason);
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt((String)session.getAttribute(USERID)));
			pst.setInt(12, nEmpId);
			pst.setInt(13, uF.parseToInt(strNoticeDays));
			pst.setInt(14, nProbationPeriod);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("update emp_off_board set notice_days=?, last_day_date=? where off_board_id=?");
			pst.setInt(1, uF.parseToInt(strNoticeDays));
			pst.setDate(2, uF.getFutureDate(utDate, uF.parseToInt(strNoticeDays)));
			pst.setInt(3, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
		
	}
	
	public List<List<String>> viewResignationReport(UtilityFunctions uF) {
		List<List<String>> al = new ArrayList<List<String>>();	
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			String locationID = hmEmpWlocationMap.get(strSessionEmpId);
			
			//1		
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			
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
					" and is_approved=0 and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append("))");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(2, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
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
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(") and approved_1=-1 and approved_2=-1 ");
			pst = con.prepareStatement(sbQuery.toString());
			
			rs = pst.executeQuery();	
			List<String> deniedList=new ArrayList<String>();
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("off_board_id"))) {
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
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			
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
					" and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append("))  group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
			
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
					" and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
		
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
				" and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			
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
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_RESIGN+"' " +
					"and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(1, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
				if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
				checkEmpUserTypeList.add(rs.getString("user_type_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select ud.emp_id from user_details ud,employee_official_details eod,employee_personal_details epd where " +
//					" ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'");
//			pst.setInt(1, uF.parseToInt(locationID));
//			rs = pst.executeQuery();			
//			Map<String, String> hmEmpByLocation = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
//			}
//			rs.close();
//			pst.close();
			
			//9
			sbQuery=new StringBuilder();
			sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id " +
					" and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id ");
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
	        sbQuery.append(") e, work_flow_details wfd where e.off_board_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_RESIGN+"' ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by e.entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			
			rs = pst.executeQuery();
			int nCount =0;
			Map<String,String> statuaMp = (Map<String,String>)request.getAttribute("statuaMp");
			/*Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String, String> hmEmpDepartment = CF.getF_departmentMap(con,null,null); 
			Map<String, String> hmEmpDesignation = CF.getEmpDesigMap(con); */
			List<String> alList = new ArrayList<String>();	
			while(rs.next()) {
				
//				if(hmEmpNames!=null && !hmEmpNames.containsKey(rs.getString("emp_id"))) {
//					continue;
//				}
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("off_board_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("off_board_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
//				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)) {
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				
//				if(strUserType.equalsIgnoreCase(HRMANAGER) && hmEmpByLocation.get(rs.getString("emp_id"))==null) {
//					continue;
//				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) && alList.contains(rs.getString("off_board_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) && !alList.contains(rs.getString("off_board_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("off_board_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;	
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("off_board_id"));//0
				alInner.add(hmEmpNames.get(rs.getString("emp_id")));//1
				alInner.add(uF.showData(rs.getString("off_board_type"),""));//2
				alInner.add(uF.getDateFormat(rs.getString("entry_date"),DBTIMESTAMP, CF.getStrReportDateFormat()) +" at "+uF.getDateFormat(rs.getString("entry_date"),DBTIMESTAMP, CF.getStrReportTimeFormat()));//3
				alInner.add(uF.limitContent(uF.showData(rs.getString("emp_reason"),""), 50));//4
				
				alInner.add(uF.parseToInt(rs.getString("notice_days"))+"");//5
				alInner.add(uF.getDateFormat(rs.getString("last_day_date"), DBDATE, CF.getStrReportDateFormat()));//6
				
				if(deniedList.contains(rs.getString("off_board_id"))) {
					 /*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*///7
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					 
				} else if(rs.getInt("approved_1")==1) {							
					/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*///7
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("off_board_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("off_board_id")))==rs.getInt("approved_1")) {
					/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*///7
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("off_board_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))>0) {
					alInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved \"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");//7
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("off_board_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("off_board_id")+"_"+userType)))) {
					if(rs.getInt("approved_1")==0) {
						if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
							alInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved \"></i></a> " +
									" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Denied\"></i></a> ");//7
						} else {
							StringBuilder sbdata = new StringBuilder();
							/*sbdata.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
							sbdata.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"title=\"Waiting for workflow\" ></i>");
							
							if(!checkGHRInWorkflow) {
								sbdata.append("&nbsp;|&nbsp;&nbsp;<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"','');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved ("+ADMIN+") \"></i></a> " +
									" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"','');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied ("+ADMIN+")\"></i></a>");	
							}
							alInner.add(sbdata.toString());//7
							
						}
					} else if(rs.getInt("approved_1")==1) {							
						/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*///7
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						
					} else {
						/*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*///7
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					}
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
								" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");//7
					} else {
						/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*///7
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
						
						
					}
				}
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("off_board_id"))!=null) {

					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("off_board_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");//8
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("off_board_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("off_board_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");//8
				} else {
					alInner.add("");//8
				}
								
				if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1) {
					if(statuaMp!=null && statuaMp.get(rs.getString("emp_id"))!=null ) {
						alInner.add("<a href=\"ExitForm.action?id="+rs.getString("emp_id")+"&resignId="+rs.getString("off_board_id")+"\">Final</a>");//9
					} else {
						if(!uF.parseToBoolean(rs.getString("is_alive"))) {
							alInner.add("<a href=\"ExitForm.action?id="+rs.getString("emp_id")+"&resignId="+rs.getString("off_board_id")+"\">Account closed</a>");//9
						} else {
							alInner.add("<a href=\"ExitForm.action?id="+rs.getString("emp_id")+"&resignId="+rs.getString("off_board_id")+"\">Full & Final</a>");//9
						}
					}
				} else {
					alInner.add("-");//9
				}
				
				alInner.add(uF.showData(hmUserTypeMap.get(userType), "")); //10
				alInner.add(uF.showData(rs.getString("emp_image"), "")); //11
				
				al.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
//	public String viewResignationReport(UtilityFunctions uF) {
//		
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		con = db.makeConnection(con);
//
//		try {
//
//			List<List<String>> al = new ArrayList<List<String>>();
//			List<String> alInner = new ArrayList<String>();
//			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
//		
//			
//			if (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
//				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id " +
//						" and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id ");
//				
//				if(getF_department()!=null && getF_department().length>0) {
//		               sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//		        }
//				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
//		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//		         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//		        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//				}
//		            
//		        if(uF.parseToInt(getF_org())>0) {
//					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//				}
//				sbQuery.append(" order by entry_date desc");
//				pst =con.prepareStatement(sbQuery.toString());
//			} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id " +
//						" and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id and eod.emp_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt(strSessionEmpId)+") ");
//				
//				if(getF_department()!=null && getF_department().length>0) {
//		               sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//		        }
//				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
//		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//		         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//		        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//				}
//		            
//		        if(uF.parseToInt(getF_org())>0) {
//					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//				}
//				sbQuery.append(" order by entry_date desc");
//				
//				pst =con.prepareStatement(sbQuery.toString());
//				
//			} else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
//				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id " +
//						" and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id and eod.emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt((String)session.getAttribute(WLOCATIONID))+") ");
//				
//				if(getF_department()!=null && getF_department().length>0) {
//		               sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//		        }
//				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
//		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//		         } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//		        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//				}
//		            
//		        if(uF.parseToInt(getF_org())>0) {
//					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//				}
//				sbQuery.append(" order by entry_date desc");
//				
//				pst =con.prepareStatement(sbQuery.toString());
//				
//			} 
////			System.out.println("pst===>"+pst);
//			rs = pst.executeQuery();
//			int nCount =0;
//			Map<String,String> statuaMp = (Map<String,String> )request.getAttribute("statuaMp");
//
//			while(rs.next()) {
//				
//				if(hmEmpNames!=null && !hmEmpNames.containsKey(rs.getString("emp_id"))) {
//					continue;
//				}
//				
//				StringBuilder sbManager =  new StringBuilder();
//				StringBuilder sbHRManager =  new StringBuilder();
//				alInner = new ArrayList<String>();
//				
//				alInner.add(rs.getString("off_board_id"));
//				alInner.add(hmEmpNames.get(rs.getString("emp_id")));
//				alInner.add(uF.showData(rs.getString("off_board_type"),""));
//				alInner.add(uF.getDateFormat(rs.getString("entry_date"),DBTIMESTAMP, CF.getStrReportDateFormat()) +" at "+uF.getDateFormat(rs.getString("entry_date"),DBTIMESTAMP, CF.getStrReportTimeFormat()));
//				alInner.add(uF.limitContent(uF.showData(rs.getString("emp_reason"),""), 50));
//				
//				alInner.add(uF.parseToInt(rs.getString("notice_days"))+"");
//				alInner.add(uF.getDateFormat(rs.getString("last_day_date"), DBDATE, CF.getStrReportDateFormat()));
//				
//				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
//					if(rs.getInt("approved_1")==0) {
//						
//						sbManager.append("<div id=\"myDivM"+nCount+"\" > ");
//						sbManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"');\" ><img src=\"images1/icons/icons/approve_icon.png\" title=\"Approve\" /></a> ");
//						sbManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"');\"><img src=\"images1/icons/icons/close_button_icon.png\" title=\"Deny\" style=\"width: 16px;\"/></a> ");
//						sbManager.append("</div>");
//						
//					} else if(rs.getInt("approved_1")==1 ) {
//						sbManager.append("<img src=\"images1/icons/approved.png\" title=\"Approved\" /> ");
//					} else if(rs.getInt("approved_1")==-1 ) {
//						sbManager.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" />");
//					}
//					
//					if(rs.getInt("approved_2")==0) {
//						sbHRManager.append("<div id=\"myDivH"+nCount+"\" > ");
//						sbHRManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','2','"+rs.getString("off_board_id")+"');\" ><img src=\"images1/icons/icons/approve_icon.png\" title=\"Approve\" /></a> ");
//						sbHRManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','2','"+rs.getString("off_board_id")+"');\"><img src=\"images1/icons/icons/close_button_icon.png\" title=\"Deny\" style=\"width: 16px;\"/></a> ");
//						sbHRManager.append("</div>");
//					} else if(rs.getInt("approved_2")==1 ) {
//						sbHRManager.append("<img src=\"images1/icons/approved.png\" title=\"Approved\" /> ");
//					} else if(rs.getInt("approved_2")==-1 ) {
//						sbHRManager.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" />");
//					}
//
//				} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER))) { // || strUserType.equalsIgnoreCase(ACCOUNTANT)
//					if(rs.getInt("approved_1")==0) {
//						sbManager.append("<div id=\"myDivM"+nCount+"\" > ");
//						sbManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"');\" ><img src=\"images1/icons/icons/approve_icon.png\" title=\"Approve\" /></a> ");
//						sbManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"');\"><img src=\"images1/icons/icons/close_button_icon.png\" title=\"Deny\" style=\"width: 16px;\"/></a> ");
//						sbManager.append("</div>");
//						
////						sbManager.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /> ");
//					} else if(rs.getInt("approved_1")==1) {
//						sbManager.append("<img src=\"images1/icons/approved.png\" title=\"Approved\" /> ");
//					} else if(rs.getInt("approved_1")==-1) {
//						sbManager.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" />");
//					}
//					
//					
//					if(rs.getInt("approved_2")==0) {
//						sbHRManager.append("<div id=\"myDiv"+nCount+"\" style=\"margin-right:10px;\"> ");
//						sbHRManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','2','"+rs.getString("off_board_id")+"');\" ><img src=\"images1/icons/icons/approve_icon.png\" title=\"Approve\" /></a> ");
//						sbHRManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','2','"+rs.getString("off_board_id")+"');\"><img src=\"images1/icons/icons/close_button_icon.png\" title=\"Deny\" style=\"width: 16px;\"/></a> ");
//						sbHRManager.append("</div>");
//					} else if(rs.getInt("approved_2")==1) {
//						sbHRManager.append("<img src=\"images1/icons/approved.png\" title=\"Approved\" /> ");
//					} else if(rs.getInt("approved_2")==-1) {
//						sbHRManager.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" />");
//					}
//
//					
//				} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//					if(rs.getInt("approved_1")==0) {
//						
//						sbManager.append("<div id=\"myDiv"+nCount+"\" > ");
//						sbManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"');\" ><img src=\"images1/icons/icons/approve_icon.png\" title=\"Approve\" /></a> ");
//						sbManager.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"');\"><img src=\"images1/icons/icons/close_button_icon.png\" title=\"Deny\" style=\"width: 16px;\"/></a> ");
//						sbManager.append("</div>");
//						
//					} else if(rs.getInt("approved_1")==1) {
//						sbManager.append("<img src=\"images1/icons/approved.png\" title=\"Approved\" /> ");
//					} else if(rs.getInt("approved_1")==-1) {
//						sbManager.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" />");
//					}
//					
//					if(rs.getInt("approved_2")==0) {
//						sbHRManager.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /> ");
//					} else if(rs.getInt("approved_2")==1) {
//						sbHRManager.append("<img src=\"images1/icons/approved.png\" title=\"Approved\" /> ");
//					} else if(rs.getInt("approved_2")==-1) {
//						sbHRManager.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" />");
//					}
//				}
//				
//				alInner.add(sbManager.toString());
//				alInner.add(sbHRManager.toString());
//				if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1) {
//					if(statuaMp!=null && statuaMp.get(rs.getString("emp_id"))!=null ) {
//						alInner.add("<a href=\"ExitForm.action?id="+rs.getString("emp_id")+"&resignId="+rs.getString("off_board_id")+"\">Final</a>");
//					} else {
//						if(!uF.parseToBoolean(rs.getString("is_alive"))) {
//							alInner.add("<a href=\"ExitForm.action?id="+rs.getString("emp_id")+"&resignId="+rs.getString("off_board_id")+"\">Account closed</a>");
//						} else {
//							alInner.add("<a href=\"ExitForm.action?id="+rs.getString("emp_id")+"&resignId="+rs.getString("off_board_id")+"\">Full & Final</a>");
//						}
//					}
//				} else {
//					alInner.add("-");
//				}
//				al.add(alInner);
//				nCount++;
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("reportList", al);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		return SUCCESS;
//		
//	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
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

}
