package com.konnect.jpms.reports.master;

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
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillUserStatus;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UserReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	String strAlphaVlaue = null;
	HttpSession session;
	CommonFunctions CF;
	
	private List<FillUserType> userTypeList;
	private List<FillEmployee> empCodeList;
	private List<FillUserStatus> userStatusList;
	
	private List<FillDepartment> departmentList;   
	private List<FillLevel> levelList;
	private List<FillWLocation> wLocationList;
	private List<FillServices> serviceList;
	private List<FillOrganisation> organisationList; 
	private List<FillEmploymentType> employementTypeList;
	private List<FillGrade> gradeList;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	private String f_org;
	private String[] f_strWLocation; 
	private String[] f_department; 
	private String[] f_level; 
	private String[] f_service;
	private String[] f_employeType;
	private String[] f_grade;
	
	private String strUserType;
	private String strBaseUserType;
	private String strSessionEmpId;
	private String strEmployeType;
	private String strGrade;
	private static Logger log = Logger.getLogger(UserReport.class);
	
	private String loginSubmit;
	private String assignMbAccess;
	
	private String mbAccessDisableIds;
	String strAction = null;
	public String execute() throws Exception {

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PReportUser);
		request.setAttribute(TITLE, TViewUser);
		
		session = request.getSession();
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)
			return LOGIN;
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		/*if(getF_strWLocation()==null) {
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}*/
		
		//Created By Dattatray 10-06-2022
				strAction = request.getServletPath();
				if(strAction!=null) {
					strAction = strAction.replace("/","");
				}
				
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if (uF.parseToInt(getF_org()) <= 0) { 
				for(int i=0; organisationList != null && i<organisationList.size(); i++) {
					if(uF.parseToInt(organisationList.get(i).getOrgId()) == uF.parseToInt((String) session.getAttribute(ORGID))) {
						setF_org((String) session.getAttribute(ORGID));
					} else {
						if(i==0) {
							setF_org(organisationList.get(0).getOrgId());
						}
					}
				} 
			}
		} else {
			if (uF.parseToInt(getF_org()) <= 0) {
				setF_org((String) session.getAttribute(ORGID));
			}
			organisationList = new FillOrganisation(request).fillOrganisation();
		}
		
		/*if (uF.parseToInt(getF_org()) <= 0) {
			setF_org((String) session.getAttribute(ORGID));
		}*/
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
		
		if(getStrEmployeType() !=null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		
		if(getStrGrade() !=null && !getStrGrade().equals("")) {
			setF_grade(getStrGrade().split(","));
		} else {
			setF_grade(null);
		}
		
		if(f_level!=null) {
			String level_id ="";
			for (int i = 0; i < f_level.length; i++) {
				if(i==0) {
					level_id = f_level[i];
					level_id.concat(f_level[i]);
				} else {
					level_id =level_id+","+f_level[i];
				}
			}
			gradeList = new FillGrade(request).fillGrade(level_id,getF_org());
		} else {
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}
		
		
		strAlphaVlaue = (String)request.getParameter("alphaValue");  
		
		String strUserIdDelete = (String)request.getParameter("D");
		
		if(strUserIdDelete!=null && strUserIdDelete.length()>0) {
			deleteUser(strUserIdDelete);
		}
		if(getLoginSubmit() !=null && getLoginSubmit().trim().equalsIgnoreCase("sendLogin")) {
			sendLoginDetails(uF);
		}
		
		if(getAssignMbAccess() != null) {
			assignMobileAccess(uF);
		}
		loadPageVisitAuditTrail(CF, uF,"");//Created By Dattatray 10-6-2022
		viewUser(uF);			
		return loadUser(uF);
	}
	
	//Created By Dattatray 10-6-2022
	private void loadPageVisitAuditTrail(CommonFunctions CF,UtilityFunctions uF,String empId) {
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
			builder.append("\nLevel:"+StringUtils.join(f_level));	
			builder.append("\nGrade:"+getStrGrade());	
			builder.append("\nEmployee Type:"+getStrEmployeType());	
			if(getLoginSubmit() !=null && getLoginSubmit().trim().equalsIgnoreCase("sendLogin")) {
				builder.append("\nLogin Details sent");
				builder.append("\nEmp Id : "+empId);
			}
			if(getAssignMbAccess() != null) {
				 if(getMbAccessDisableIds() != null && !getMbAccessDisableIds().equals("")) {
					 builder.append("\nAssign mobile Access disabled");
					 builder.append("\nEmp Id : "+empId);
				 }else {
					 builder.append("\nAssign mobile Access enabled");
					 builder.append("\nEmp Id : "+empId);
				 }
			}
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			db.closeConnection(con);
		}
	}
	private void assignMobileAccess(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try{

			con = db.makeConnection(con);
			
			String[] strSendMbAccess = request.getParameterValues("strSendMbAccess");
			
			if(strSendMbAccess !=null && strSendMbAccess.length>0) {
				String strEmpIds = StringUtils.join(strSendMbAccess,",");
				
//				System.out.println("strEmpIds---->"+strEmpIds);
				pst = con.prepareStatement("update user_details set is_mobile_authorized= ? where emp_id in("+strEmpIds+")");
				pst.setBoolean(1, true);
//				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();				
	            pst.close();
	            loadPageVisitAuditTrail(CF, uF, strEmpIds);//Created By dattatray 10-06-2022
	            if(getMbAccessDisableIds() != null && !getMbAccessDisableIds().equals("")) {
		            pst = con.prepareStatement("update user_details set is_mobile_authorized= ? where emp_id in("+getMbAccessDisableIds()+")");
					pst.setBoolean(1, false);
	//				System.out.println("pst ===>> " + pst);
					pst.executeUpdate();				
		            pst.close();
		            loadPageVisitAuditTrail(CF, uF, getMbAccessDisableIds());//Created By dattatray 10-06-2022
	            }
			}
			request.setAttribute(MESSAGE, SUCCESSM+"Assign Mobile Access of Selected Employee successfully."+END);
		
			
		}catch (Exception e) {
			request.setAttribute(MESSAGE, SUCCESSM+"Assign Mobile Access of Selected Employee failed."+END);
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void sendLoginDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alEmp = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			
			String[] strSendLogin = request.getParameterValues("strSendLogin");
			
			if(strSendLogin !=null) {
				String strEmpIds = StringUtils.join(strSendLogin,",");
			
				pst = con.prepareStatement("select * from user_details where emp_id in("+strEmpIds+") and emp_id > 0");
				rs = pst.executeQuery();
				while(rs.next()) {
					alEmp.add(rs.getString("emp_id"));
				}
	            rs.close();
	            pst.close();
			}
			request.setAttribute(MESSAGE, SUCCESSM+"Login details sent Successfully."+END);
		} catch (Exception e) {
			request.setAttribute(MESSAGE, SUCCESSM+"Login details sent failed."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		if(alEmp != null && alEmp.size()>0) {
            String strDomain = request.getServerName().split("\\.")[0];
			for(int i = 0; i < alEmp.size(); i++) {
				Notifications nF = new Notifications(N_NEW_EMPLOYEE, CF);
				nF.setDomain(strDomain);
				nF.setServletRequest(request);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setEmailTemplate(true);
				nF.setStrEmpId(alEmp.get(i)+"");
				loadPageVisitAuditTrail(CF, uF,alEmp.get(i)+"");//Created By Dattatray 10-6-2022
				nF.sendNotifications();
				
			}
			
        }

	}

	public String loadUser(UtilityFunctions uF) {
		
		empCodeList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
		userStatusList = new FillUserStatus().fillUserStatus();
		userTypeList = new FillUserType(request).fillUserType(strUserType);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org())); 
		
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		request.setAttribute("empCodeList", empCodeList);
		request.setAttribute("userStatusList", userStatusList);
		request.setAttribute("userTypeList", userTypeList);
		
		int empCodeId, i;
		String employeeCode;
		StringBuilder sbEmpCodeList = new StringBuilder();
		sbEmpCodeList.append("{");
	    for(i=0; i<empCodeList.size()-1;i++ ) {
	    		empCodeId = Integer.parseInt((empCodeList.get(i)).getEmployeeId());
	    		employeeCode = empCodeList.get(i).getEmployeeCode();
	    		sbEmpCodeList.append("\""+ empCodeId+"\":\""+employeeCode+"\",");
	    }
	    if(i>0) {
		    empCodeId = Integer.parseInt((empCodeList.get(i)).getEmployeeId());
		    employeeCode = empCodeList.get(i).getEmployeeCode();
			sbEmpCodeList.append("\""+ empCodeId+"\":\""+employeeCode+"\"");
	    }
	    sbEmpCodeList.append("}");
	    request.setAttribute("sbEmpCodeList", sbEmpCodeList.toString());
	    
		int i1;
		String userStatusName, userStatusId;
		StringBuilder sbUserStatusList = new StringBuilder();
		sbUserStatusList.append("{");
	    for(i1=0; i1<userStatusList.size()-1;i1++ ) {
	    		userStatusId = (userStatusList.get(i1)).getStatusId();
	    		userStatusName = userStatusList.get(i1).getStatusName();
	    		sbUserStatusList.append("\""+ userStatusId+"\":\""+userStatusName+"\",");
	    }
	    
	    if(i1>0) {
	    	userStatusId = (userStatusList.get(i1)).getStatusId();
		    userStatusName = userStatusList.get(i1).getStatusName();
			sbUserStatusList.append("\""+ userStatusId+"\":\""+userStatusName+"\"");
	    }
	    	
	    sbUserStatusList.append("}");
	    request.setAttribute("sbUserStatusList", sbUserStatusList.toString());
	    
		int userTypeId, i11;
		String userTypeName;
		StringBuilder sbUserTypeList = new StringBuilder();
		sbUserTypeList.append("{");
	    for(i11=0; i11<userTypeList.size()-1;i11++ ) {
	    		userTypeId = Integer.parseInt((userTypeList.get(i11)).getUserTypeId());
	    		userTypeName = userTypeList.get(i11).getUserTypeName();
	    		sbUserTypeList.append("\""+ userTypeId+"\":\""+userTypeName+"\",");
	    }
	    if(i11>0) {
	    	userTypeId = Integer.parseInt((userTypeList.get(i11)).getUserTypeId());
		    userTypeName = userTypeList.get(i11).getUserTypeName();
			sbUserTypeList.append("\""+ userTypeId+"\":\""+userTypeName+"\"");
	    }
	    	
	    sbUserTypeList.append("}");
	    request.setAttribute("sbUserTypeList", sbUserTypeList.toString());
	    
	    getSelectedFilter(uF);
	    
		return LOAD;
	}
	
	public String viewUser(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus1 = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus1.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
//			Map<String, String> hmEmpType = CF.getEmpTypeMap(con); 
			pst = con.prepareStatement("select * from settings where options=?");
			pst.setString(1, O_SHOW_PASSWORD);
			rs = pst.executeQuery();
			boolean isShowPassword = false;
			while(rs.next()) {
				isShowPassword = uF.parseToBoolean(rs.getString("value"));
			}
			rs.close();
			pst.close();
			
			if(strAlphaVlaue!=null && strAlphaVlaue.length()>0) {
				pst = con.prepareStatement(selectUserRAlpha);
				pst.setString(1, strAlphaVlaue+"%");
			} else {
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from (SELECT * FROM (SELECT * FROM employee_personal_details epd, employee_official_details eod " +
						"WHERE epd.emp_per_id = eod.emp_id and is_delete=false ");
				/*if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }*/
				if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
		            sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		            //sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
	            } else {
	            	 if(getF_level()!=null && getF_level().length>0) {
	                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	                 }
	            	 if(getF_grade()!=null && getF_grade().length>0) {
	                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	                 }
	            }
				if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_service()!=null && getF_service().length>0) {
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++) {
	                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                    if(i<getF_service().length-1) {
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            }
	            
	            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	            } else if((String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if((String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				
				if(strBaseUserType != null && strBaseUserType.equals(HOD) && strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or hod_emp_id = "+uF.parseToInt(strSessionEmpId)+")");
				} else if(strBaseUserType != null && strBaseUserType.equals(MANAGER) && strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and supervisor_emp_id = "+uF.parseToInt(strSessionEmpId));
				}
				/*sbQuery.append(" and epd.emp_per_id > 0 and epd.emp_per_id in (SELECT emp_id FROM user_details where emp_id>0)) e, " +
					"user_details ud where e.emp_per_id = ud.emp_id) ud, user_type ut where ut.user_type_id=ud.usertype_id " +
					"order by empcode, status, emp_fname, emp_lname");*/
				sbQuery.append(" and epd.emp_per_id > 0 and epd.emp_per_id in (SELECT emp_id FROM user_details where emp_id>0)) e, " +
						"user_details ud where e.emp_per_id = ud.emp_id) ud, user_type ut where ut.user_type_id=ud.usertype_id " +
						"order by emp_fname, emp_lname, empcode, status");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst-->"+pst);
			}
//			System.out.println("pst-->"+pst);
			rs = pst.executeQuery();
			int nCount=0;
			while(rs.next()) {
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				List<String> alInner = new ArrayList<String>();
				alInner.add(Integer.toString(rs.getInt("user_id"))); //0
				alInner.add(rs.getString("empcode")); //1
				StringBuilder sbEmpName = new StringBuilder();
				
				sbEmpName.append(uF.showData(rs.getString("emp_fname"), ""));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				sbEmpName.append(strEmpMName);

				sbEmpName.append(" " + uF.showData(rs.getString("emp_lname"), ""));
				
				alInner.add(sbEmpName.toString()); //2
//				alInner.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				String username=uF.showData(rs.getString("username"), "")+"<a href=\"javascript:void(0)\"" +
				"onclick=\"changeUserName('"+rs.getString("emp_id")+"','"+rs.getString("user_id")+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"')\" title=\"Edit Exist\"><i class=\"fa fa-pencil-square-o\"></i></a>";
				alInner.add(username); //3
				if(isShowPassword) {
					alInner.add(rs.getString("password"));  //4
				} else {
					alInner.add(uF.getPasswordString(rs.getString("password"))); //4
				}
				
				if(rs.getString("added_timestamp")!=null) {
					alInner.add(uF.getDateFormat(rs.getString("added_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())); //5
				} else {
					alInner.add("-"); //5
				}
				alInner.add(rs.getString("user_type")); //6
//				alInner.add(uF.stringMapping(hmEmpType.get(rs.getString("emp_id"))));
				alInner.add(rs.getString("emp_status"));  //7
				alInner.add("<div id=\"myDiv_"+nCount+"\"><a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure, you want to reset the password?')?getContent('myDiv_"+nCount+"','ResetPassword.action?user_id="+rs.getString("user_id")+"'):'')\" >Reset Password</a></div>"); //8
				if(rs.getString("reset_timestamp")!=null) {
					alInner.add(uF.getDateFormat(rs.getString("reset_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) +" at " +uF.getDateFormat(rs.getString("reset_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat())); //9
				} else {
					alInner.add("-"); //9
				}
				String changeUsertype= "<a href=\"javascript:void(0)\" " +
					"onclick=\"changeUserType('"+rs.getString("emp_id")+"','"+rs.getString("user_id")+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"')\" title=\"Change User Type\"> Change User Type</a>";
				alInner.add(changeUsertype); //10
				alInner.add(rs.getString("emp_id")); //11
				if(rs.getString("status")!= null && rs.getString("status").trim().equalsIgnoreCase("ACTIVE")) {
					alInner.add("Active"); //12
				} else {
					alInner.add("Inactive"); //12
				}
				alInner.add(uF.showData(rs.getString("emp_image"), "")); //13
				alInner.add(""+uF.parseToBoolean(rs.getString("is_mobile_authorized")));
				
				al.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			          
			request.setAttribute("reportList", al);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	 
	
	
	public String deleteUser(String strUserId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteUser);
			pst.setInt(1, uF.parseToInt(strUserId));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

//		System.out.println("getF_service()----"+getF_service());
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
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
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		alFilter.add("GRADE");
		if (getF_grade() != null) {
			String strgrade = "";
			int k = 0;
			for (int i = 0; gradeList != null && i < gradeList.size(); i++) {
				for (int j = 0; j < getF_grade().length; j++) {
					if (getF_grade()[j].equals(gradeList.get(i).getGradeId())) {
						if (k == 0) {
							strgrade = gradeList.get(i).getGradeCode();
						} else {
							strgrade += ", " + gradeList.get(i).getGradeCode();
						}
						k++;
					}
				}
			}
			if (strgrade != null && !strgrade.equals("")) {
				hmFilter.put("GRADE", strgrade);
			} else {
				hmFilter.put("GRADE", "All Grade's");
			}
		} else {
			hmFilter.put("GRADE", "All Grade's");
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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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

	public String getLoginSubmit() {
		return loginSubmit;
	}

	public void setLoginSubmit(String loginSubmit) {
		this.loginSubmit = loginSubmit;
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

	public String getAssignMbAccess() {
		return assignMbAccess;
	}

	public void setAssignMbAccess(String assignMbAccess) {
		this.assignMbAccess = assignMbAccess;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getMbAccessDisableIds() {
		return mbAccessDisableIds;
	}

	public void setMbAccessDisableIds(String mbAccessDisableIds) {
		this.mbAccessDisableIds = mbAccessDisableIds;
	}
	
}