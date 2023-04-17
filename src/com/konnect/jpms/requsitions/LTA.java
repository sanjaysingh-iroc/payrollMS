package com.konnect.jpms.requsitions;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LTA extends ActionSupport  implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	
	
	private CommonFunctions CF; 
	 
	private String strId;
	private String strPurpose;
	private String strAmount;
	private File strDocument;
	private String strDocumentContentType;
	private String strDocumentFileName;
	private String strViewDocument;
	private String policy_id;
	private String f_org;
	private String f_strWLocation;
	private String strSelectedEmpId;
	private String strStartDate;
	private String strEndDate;
	private String strActualAmount;
	private String salaryHead;
	private String alertStatus;
	private String alert_type;
	private String currUserType;
	private String alertID;
	
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	private List<FillEmployee> empNamesList;
	private List<FillSalaryHeads> salaryHeadList;
	
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
		request.setAttribute(PAGE, "/jsp/requisitions/LTA.jsp");
		request.setAttribute(TITLE, "CTC Variable");
		
		String strE = (String)request.getParameter("E");

//		System.out.println("AlertId===>"+getAlertID());
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
				
		if(uF.parseToInt(getF_org()) == 0) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getCurrUserType() == null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		String[] arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
		if(getStrStartDate() == null || getStrStartDate().trim().equals("") || getStrStartDate().trim().equalsIgnoreCase("NULL")) {
			setStrStartDate(arrDates[0]);
		}
		if(getStrEndDate() == null || getStrEndDate().trim().equals("") || getStrEndDate().trim().equalsIgnoreCase("NULL")) {
			setStrEndDate(arrDates[1]);
		}
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		empNamesList = getEmpList();
		
		if(strE!=null) {
			viewLTA(strE,uF);
		} else if(getStrId()!=null && getStrId().length()>0) {
			return updateLTA(uF);
		} else if(getStrAmount()!=null) {
			return addLTA(uF); 
		}
		
//		System.out.println("==USerTYpe==>"+strUserType);
		
		if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
			LTAReportAdmin(uF);
		} else if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))) {
			LTAReportManager(uF);
		} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
			LTAReportHRManager(uF);
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE) ) {
			getLTAPolicyMember(uF);
			LTAReportEmp(uF);
		}
		return loadLTAs(uF);
	}

	private List<FillEmployee> getEmpList() {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
				sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			} else {
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(uF.parseToInt(getF_strWLocation())>0) {
		            sbQuery.append(" and wlocation_id in ("+getF_strWLocation()+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
			}
            sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("2 pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				al.add(new FillEmployee(rs.getString("emp_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname")+ " ["+ rs.getString("empcode") + "]"));
			}
			rs.close();
			pst.close();
		
			
			/*con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and  eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString()); 
		//	System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + " " + rs.getString("emp_lname")
						+ " ["+ rs.getString("empcode") + "]"));
			}
			rs.close();
			pst.close();*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
		return al;
	}

	public String loadLTAs(UtilityFunctions uF) {
		
		String strE = (String)request.getParameter("E");
	
		if(strE==null) {
			setStrPurpose(null);
			setStrAmount(null);
		}
		
		salaryHeadList = new FillSalaryHeads(request).fillLTASalaryHeads(uF.parseToInt(strSessionEmpId));
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER) && !strUserType.equals(EMPLOYEE)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
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
			
			alFilter.add("EMP");
			if(getStrSelectedEmpId()!=null) {
				String strEmpName="";
				int k=0;
				for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
					if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
						strEmpName=empNamesList.get(i).getEmployeeCode();
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
		}

		alFilter.add("FROMTO");
		if(getStrStartDate() != null && getStrEndDate() != null) {
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private void getLTAPolicyMember(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		String policy_id=null;
		try {
			
		
			int strEmpID = uF.parseToInt(strSessionEmpId);
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			System.out.println("strEmpID=====> "+strEmpID);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId=hmEmpLevelMap.get(""+strEmpID);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(""+strEmpID);
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
//			System.out.println("empLevelId=====> "+empLevelId);
			
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_LTA+"' and level_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(locationID));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				policy_id=rs.getString("policy_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id) == 0) {
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(locationID));
				rs = pst.executeQuery();
				while(rs.next()) {
					policy_id=rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}
			
			if(uF.parseToInt(policy_id)>0) {
//				System.out.println("policy_id=====> "+policy_id);
				
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
						" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
				rs=pst.executeQuery();
				
				Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
				while(rs.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("member_type"));
					innerList.add(rs.getString("member_id"));
					innerList.add(rs.getString("member_position"));
					innerList.add(rs.getString("work_flow_mem"));
					innerList.add(rs.getString("work_flow_member_id"));
					
					hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
				
				
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1) {
						int memid=uF.parseToInt(innerList.get(1));
						
						switch(memid) {
						
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
								
									alList.add(strEmpMName);
									alList.add(rs.getString("emp_lname"));
									
									outerList.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList!=null && !outerList.isEmpty()) {
									StringBuilder sbComboBox=new StringBuilder();
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++) {
										List<String> alList=outerList.get(i);
										sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox.append("</select>");								
									
									String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id=? and supervisor_emp_id!=0) as a," +
										"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
										" and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList11=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(MANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
								
									
									alList.add(rs.getString("emp_lname"));
									
									outerList11.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList11!=null && !outerList11.isEmpty()) {
									StringBuilder sbComboBox11=new StringBuilder();
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++) {
										List<String> alList=outerList11.get(i);
										sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox11.append("</select>");								
									
									String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+" and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList1=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
								
									
									alList.add(rs.getString("emp_lname"));
									
									outerList1.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList1!=null && !outerList1.isEmpty()) {
									StringBuilder sbComboBox1=new StringBuilder();
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++) {
										List<String> alList=outerList1.get(i);
										sbComboBox1.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox1.append("</select>");								
									
									String optionTr1="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList2=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
								
									
									alList.add(rs.getString("emp_lname"));
									
									outerList2.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList2!=null && !outerList2.isEmpty()) {
									StringBuilder sbComboBox2=new StringBuilder();
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList2.size();i++) {
										List<String> alList=outerList2.get(i);
										sbComboBox2.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox2.append("</select>");								
									
									String optionTr2="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList3=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
								
									
									alList.add(rs.getString("emp_lname"));
									
									outerList3.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList3!=null && !outerList3.isEmpty()) {
									StringBuilder sbComboBox3=new StringBuilder();
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++) {
										List<String> alList=outerList3.get(i);
										sbComboBox3.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox3.append("</select>");								
									
									String optionTr3="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList4=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
								
									
									alList.add(rs.getString("emp_lname"));
									
									outerList4.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList4!=null && !outerList4.isEmpty()) {
									StringBuilder sbComboBox4=new StringBuilder();
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++) {
										List<String> alList=outerList4.get(i);
										sbComboBox4.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox4.append("</select>");								
									
									String optionTr4="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr4);
								}
								break;
							
						case 7:
							 pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
										"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
										"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod," +
										"employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.emp_id=?)" +
										" union " +
										"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
										"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' " +
										"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true " +
										" union " +
										"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
										"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
										"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true");
								pst.setInt(1, strEmpID);
								pst.setInt(2, strEmpID);
								pst.setInt(3, strEmpID);
								pst.setInt(4, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList5=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(HRMANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
								
									
									alList.add(rs.getString("emp_lname"));
									
									outerList5.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList5!=null && !outerList5.isEmpty()) {
									StringBuilder sbComboBox5=new StringBuilder();
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++) {
										List<String> alList=outerList5.get(i);
										sbComboBox5.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox5.append("</select>");								
									
									String optionTr5="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;	
								
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
									"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
									"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1,strEmpID);
							rs = pst.executeQuery();
							List<List<String>> outerHODList=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
							
								
								alList.add(rs.getString("emp_lname"));
								
								outerHODList.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerHODList!=null && !outerHODList.isEmpty()) {
								StringBuilder sbComboBox11=new StringBuilder();
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++) {
									List<String> alList=outerHODList.get(i);
									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
								
								hmMemberOption.put(innerList.get(4), optionTr11);
							}
						
							break;
						
						
						}						
						
					} else if(uF.parseToInt(innerList.get(0))==3) {
						int memid=uF.parseToInt(innerList.get(1));
						
						List<List<String>> outerList=new ArrayList<List<String>>();
						pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id " +
								"and se.policy_id = ? and se.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname"); 
						pst.setInt(1,uF.parseToInt(policy_id));
						pst.setInt(2, strEmpID);
						rs = pst.executeQuery();
						while (rs.next()) {
							List<String> alList = new ArrayList<String>();
							alList.add(rs.getString("emp_id"));
							outerList.add(alList);
						}
						rs.close();
						pst.close();
						
						if(outerList!=null && !outerList.isEmpty()) {
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++) {
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("strEmpID", ""+strEmpID);
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String LTAReportEmp(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			int nEmpLevelId = CF.getEmpLevelId(strSessionEmpId, request);
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LTA+"' and effective_id in(select emp_lta_id from emp_lta_details where emp_id=?");
			sbQuery.append(" and to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LTA+"' and effective_id in(select emp_lta_id from emp_lta_details where emp_id=?");
			sbQuery.append(" and to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_lta_details where emp_id=?");
			sbQuery.append(" and to_date(entry_date::text,'yyyy-MM-dd') between ? and ? ");
			sbQuery.append(" order by entry_date desc,emp_lta_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left;width:20px;margin-top:1px\" id=\"myDiv"+nCount+"\">");
							
				
				if(rs.getInt("is_approved")==0) {
					 /*sb.append("<img src=\""+request.getContextPath()+"/images1/icons/pending.png\" title=\"Waiting for approval, click to pullout\" border=\"0\" onclick=\"((confirm('Are you sure you want to pullout this request?')) ? getContent('myDiv"+nCount+"', 'UpdateLTA.action?approveStatus=2&empLtaId="+rs.getString("emp_lta_id")+"'):'')\" />");*/
					sb.append("<a href=\"javascript:void(0);\" onclick=\"((confirm('Are you sure you want to pullout this request?')) ? getContent('myDiv"+nCount+"', 'UpdateLTA.action?approveStatus=2&empLtaId="+rs.getString("emp_lta_id")+"'):'')\"> <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval, click to pullout\"></i> </a>");
				} else if(rs.getInt("is_approved")==1) {
					/*sb.append("<img src=\""+request.getContextPath()+"/images1/icons/approved.png\" title=\"Approved\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\" border=\"0\"></i>");
					
				} else if(rs.getInt("is_approved")==-1) {
					/*sb.append("<img src=\""+request.getContextPath()+"/images1/icons/denied.png\" title=\"Denied\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
				}
				sb.append("</div>");
				
				
				sb.append("<div style=\"float:left;width:80%;\">");
				
				sb.append("Your request for "+uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")), "")+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" for "+ strCurrSymbol+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("applied_amount")))
						+" specifying "
						+"\""+uF.showData(rs.getString("lta_purpose"), "")+"\"" 
						);
				
				
				if(rs.getInt("is_approved")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approved_by")));
				} else if(rs.getInt("is_approved")== 0) {
					sb.append(" is waiting for your approval");
				} else if(rs.getInt("is_approved")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approved_by")) +" on "+ uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				} 
				
				if(rs.getInt("is_approved")==0) {
					sb.append(" <a href=\"javascript:void(0)\" onclick=\"editLTA('"+rs.getString("emp_lta_id")+"')\"><i class=\"fa fa-pencil-square-o\"></i></a> ");
				}
				
				
				sb.append("</div>");
				
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
//					if(CF.getIsRemoteLocation()) {
//						sb.append("<a target=\"_blank\" title=\"View Attachment\" href=\""+CF.getStrDocRetriveLocation() +rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					} else {
//						sb.append("<a target=\"_blank\" title=\"View Attachment\" href=\""+request.getContextPath()+DOCUMENT_LOCATION+rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					}
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("emp_lta_id"))!=null) {
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("emp_lta_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("emp_lta_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("emp_lta_id"))), "")+")" : "";
//					alInner.add(hmEmpNames.get(approvedby)+strUserTypeName);
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("emp_lta_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("emp_lta_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("emp_lta_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
				
			
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;
		
	}
	
	public String LTAReportManager(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			StringBuilder sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0  and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(4, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(4, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(5, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}	
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_lta_id from emp_lta_details where is_approved=-1 and ");
			sbQuery.append("to_date(entry_date::text,'yyyy-MM-dd') between ? and ? ");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("emp_lta_id"))) {
					deniedList.add(rs.getString("emp_lta_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and emp_id=? and status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id,is_approved");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LTA+"' and emp_id=? and effective_id in(select emp_lta_id from emp_lta_details where ");
			sbQuery.append(" to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LTA+"' and emp_id=? and effective_id in(select emp_lta_id from emp_lta_details where ");
			sbQuery.append(" to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(3, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(4, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
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
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select status,effective_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" order by effective_id,status");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmCheckStatus = new HashMap<String,String>();	
			while(rs.next()) {
				String status=rs.getString("status");
				hmCheckStatus.put(rs.getString("effective_id"), status);
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select elt.*,wfd.user_type_id as user_type from emp_lta_details elt, work_flow_details wfd" +
					" where elt.emp_lta_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LTA+"' and elt.emp_id >0");
			sbQuery.append(" and to_date(entry_date::text,'yyyy-MM-dd') between ? and ? ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(CEO) || strBaseUserType.equalsIgnoreCase(HOD))) {
					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			if(uF.parseToInt(getStrSelectedEmpId())>0){
				sbQuery.append(" and elt.emp_id = "+uF.parseToInt(getStrSelectedEmpId())+" ");
			} else {			
				sbQuery.append(" and elt.emp_id in (select emp_id from employee_personal_details epd, " +
						"employee_official_details eod where epd.emp_per_id = eod.emp_id ");
				if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
					sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
				} else {
					if(uF.parseToInt(getF_org())>0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
							sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					if(uF.parseToInt(getF_strWLocation())>0) {
			            sbQuery.append(" and wlocation_id in ("+getF_strWLocation()+") ");
			        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
						sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					}
				}			
				sbQuery.append(") ");
			}
			sbQuery.append(" order by elt.entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 	
			List<String> alList = new ArrayList<String>();	
			int nCount=0;
			while(rs.next()) {
				
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("emp_lta_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("emp_lta_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
//				if(checkEmpList!=null && !checkEmpList.contains(strSessionEmpId)) {
//					continue;
//				}
//				int status=uF.parseToInt(hmCheckStatus.get(rs.getString("emp_lta_id"))); 
//				if(hmCheckStatus!=null && status==1) {
//					continue;
//				}
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("emp_lta_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("emp_lta_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("emp_lta_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;	
				}
				
				List<String> alInner = new ArrayList<String>();
				
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left;width:130px;margin-top:1px;padding-right:8px\" id=\"myDiv" + nCount + "\" >");
				if(deniedList.contains(rs.getString("emp_lta_id"))) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
				} else if(1==rs.getInt("is_approved")) {
										/* sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					 sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					 
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("emp_lta_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("emp_lta_id")))==rs.getInt("is_approved")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))>0) {
					sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
					sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)))) {
					if(rs.getInt("is_approved")==0) {		
						if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
							sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
							sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
						} else {
							/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
						}
					} else if(rs.getInt("is_approved")==1) {							
						/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\" ></i>");
					} else {
						/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					}
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
						sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
					} else {
						/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
						
					}
				}
				
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:85%\">");
				
				sb.append(hmEmpNames.get(rs.getString("emp_id")));
				
				int nEmpLevelId = CF.getEmpLevelId(rs.getString("emp_id"), request);
				Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
				sb.append(" has submitted a request for "+uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")), "")+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" for "+ strCurrSymbol+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("applied_amount")))
						+" specifying "
						+"\""+rs.getString("lta_purpose")+"\""
						);
				
				
				if(rs.getInt("is_approved")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approved_by")));
				} else if(rs.getInt("is_approved")== 0) {
					sb.append(" is waiting for approval");
				} else if(rs.getInt("is_approved")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approved_by")) +" on "+ uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				}
				
				sb.append("  ["+uF.showData(hmUserTypeMap.get(userType), "")+"]");
				
				sb.append("</div>");
					
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
//					if(CF.getIsRemoteLocation()) {
//						sb.append("<a target=\"_blank\" title=\"View Attachment\" href=\""+CF.getStrDocRetriveLocation() +rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					} else {
//						sb.append("<a target=\"_blank\" title=\"View Attachment\" href=\""+request.getContextPath()+DOCUMENT_LOCATION+rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					}
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				 
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("emp_lta_id"))!=null) {
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("emp_lta_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("emp_lta_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("emp_lta_id"))), "")+")" : "";
//					alInner.add(hmEmpNames.get(approvedby)+strUserTypeName);
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("emp_lta_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("emp_lta_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("emp_lta_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
				
			
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;
		
	}
	
	public String LTAReportAdmin(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			StringBuilder sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0  and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(4, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(4, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(5, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LTA+"' and status=0 group by effective_id");
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}	
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_lta_id from emp_lta_details where is_approved=-1 and ");
			sbQuery.append("to_date(entry_date::text,'yyyy-MM-dd') between ? and ? ");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("emp_lta_id"))) {
					deniedList.add(rs.getString("emp_lta_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and emp_id=? and status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id,is_approved");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LTA+"' and effective_id in(select emp_lta_id from emp_lta_details where ");
			sbQuery.append(" to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LTA+"' and effective_id in(select emp_lta_id from emp_lta_details where ");
			sbQuery.append(" to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(3, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(4, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
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
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select status,effective_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" order by effective_id,status");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmCheckStatus = new HashMap<String,String>();	
			while(rs.next()) {
				String status=rs.getString("status");
				hmCheckStatus.put(rs.getString("effective_id"), status);
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select elt.*,wfd.user_type_id as user_type from emp_lta_details elt, work_flow_details wfd" +
					" where elt.emp_lta_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LTA+"' and elt.emp_id >0");
			sbQuery.append(" and to_date(entry_date::text,'yyyy-MM-dd') between ? and ? ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			
			if(uF.parseToInt(getStrSelectedEmpId())>0){
				sbQuery.append(" and elt.emp_id = "+uF.parseToInt(getStrSelectedEmpId())+" ");
			} else {			
				sbQuery.append(" and elt.emp_id in (select emp_id from employee_personal_details epd, " +
						"employee_official_details eod where epd.emp_per_id = eod.emp_id ");
				if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
					sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
				} else {
					if(uF.parseToInt(getF_org())>0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
							sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					if(uF.parseToInt(getF_strWLocation())>0) {
			            sbQuery.append(" and wlocation_id in ("+getF_strWLocation()+") ");
			        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
						sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					}
				}			
				sbQuery.append(") ");
			}
			sbQuery.append(" order by elt.entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());			
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();  
//			System.out.println("====>"+pst);
			List<List<String>> alReport = new ArrayList<List<String>>(); 	
			List<String> alList = new ArrayList<String>();	
			int nCount=0;
			while(rs.next()) {
				
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("emp_lta_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("emp_lta_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
//				if(checkEmpList!=null && !checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
//					continue;
//				}
//				int status=uF.parseToInt(hmCheckStatus.get(rs.getString("emp_lta_id"))); 
//				if(hmCheckStatus!=null && status==1) {
//					continue;
//				}
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("emp_lta_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("emp_lta_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("emp_lta_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;	
				}
				
				List<String> alInner = new ArrayList<String>();
				
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left;width:130px;margin-top:1px;padding-right:8px\" id=\"myDiv" + nCount + "\" >");
				if(deniedList.contains(rs.getString("emp_lta_id"))) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
				} else if(1==rs.getInt("is_approved")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("emp_lta_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("emp_lta_id")))==rs.getInt("is_approved")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))>0) {
					sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
					sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)))) {
					
//					if(!checkEmpList.contains(strSessionEmpId) && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) { 
						if(rs.getInt("is_approved")==0) {
							if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
								sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
								sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
							} else {
								/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\"  style=\"color:#f7ee1d\"></i>");
								
								if(!checkGHRInWorkflow) {
									sb.append("&nbsp;|&nbsp;&nbsp;<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','');\"></i>&nbsp;");
									sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','');\" width=\"16px;\" title=\"Denied ("+ADMIN+")\"></i>");
								}
							}
						} else if(rs.getInt("is_approved")==1) {							
							/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						} else {
							/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
						}
						
					/*} else {
					
						sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
					}*/
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
						sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
					} else {
						/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\" ></i>");
					}
				}
				
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:85%\">");
				
				sb.append(hmEmpNames.get(rs.getString("emp_id")));
				
				int nEmpLevelId = CF.getEmpLevelId(rs.getString("emp_id"), request);
				Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, nEmpLevelId);				
				sb.append(" has submitted a request for "+uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")), "")+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" for "+ strCurrSymbol+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("applied_amount")))
						+" specifying "
						+"\""+rs.getString("lta_purpose")+"\""
						);
				
				if(rs.getInt("is_approved")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approved_by")));
				} else if(rs.getInt("is_approved")== 0) {
					sb.append(" is waiting for approval");
				} else if(rs.getInt("is_approved")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approved_by")) +" on "+ uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				} 
				
				sb.append("  ["+uF.showData(hmUserTypeMap.get(userType), "")+"]");
				
				sb.append("</div>");
					
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
//					if(CF.getIsRemoteLocation()) {
//						sb.append("<a target=\"_blank\" title=\"View Attachment\" href=\""+CF.getStrDocRetriveLocation() +rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					} else {
//						sb.append("<a target=\"_blank\" title=\"View Attachment\" href=\""+request.getContextPath()+DOCUMENT_LOCATION+rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					}
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				 
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("emp_lta_id"))!=null) {
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("emp_lta_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("emp_lta_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("emp_lta_id"))), "")+")" : "";
//					alInner.add(hmEmpNames.get(approvedby)+strUserTypeName);
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("emp_lta_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("emp_lta_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("emp_lta_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
				
			
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;
		
	}
	
	public String LTAReportHRManager(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			StringBuilder sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0  and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(4, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(4, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(5, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}	
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_lta_id from emp_lta_details where is_approved=-1 and ");
			sbQuery.append("to_date(entry_date::text,'yyyy-MM-dd') between ? and ? ");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("emp_lta_id"))) {
					deniedList.add(rs.getString("emp_lta_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and emp_id=? and status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" group by effective_id,is_approved");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LTA+"' and effective_id in(select emp_lta_id from emp_lta_details where ");
			sbQuery.append(" to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LTA+"' and effective_id in(select emp_lta_id from emp_lta_details where ");
			sbQuery.append(" to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(3, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(4, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
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
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select status,effective_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_LTA+"' ");
			sbQuery.append("and effective_id in (select emp_lta_id from emp_lta_details where to_date(entry_date::text,'yyyy-MM-dd') between ? and ?) ");
			sbQuery.append(" order by effective_id,status");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmCheckStatus = new HashMap<String,String>();	
			while(rs.next()) {
				String status=rs.getString("status");
				hmCheckStatus.put(rs.getString("effective_id"), status);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmCheckStatus====>"+hmCheckStatus);
			sbQuery = new StringBuilder();
			sbQuery.append("select elt.*,wfd.user_type_id as user_type from emp_lta_details elt, work_flow_details wfd" +
					" where elt.emp_lta_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LTA+"' and elt.emp_id >0");
			sbQuery.append(" and to_date(entry_date::text,'yyyy-MM-dd') between ? and ? ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(CEO) || strBaseUserType.equalsIgnoreCase(HOD))) {
					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			if(uF.parseToInt(getStrSelectedEmpId())>0){
				sbQuery.append(" and elt.emp_id = "+uF.parseToInt(getStrSelectedEmpId())+" ");
			} else {			
				sbQuery.append(" and elt.emp_id in (select emp_id from employee_personal_details epd, " +
						"employee_official_details eod where epd.emp_per_id = eod.emp_id ");
				if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
					sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
				} else {
					if(uF.parseToInt(getF_org())>0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
							sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					if(uF.parseToInt(getF_strWLocation())>0) {
			            sbQuery.append(" and wlocation_id in ("+getF_strWLocation()+") ");
			        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
						sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					}
				}			
				sbQuery.append(") ");
			}
			sbQuery.append(" order by elt.entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());			
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 	
			List<String> alList = new ArrayList<String>();	
			int nCount=0;
			while(rs.next()) {
				
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("emp_lta_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("emp_lta_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
//				if(checkEmpList!=null && !checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(HRMANAGER)) {
//					continue;
//				}
//				int status=uF.parseToInt(hmCheckStatus.get(rs.getString("emp_lta_id"))); 
//				if(hmCheckStatus!=null && status==1) {
//					continue;
//				}
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("emp_lta_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("emp_lta_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("emp_lta_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;	
				}
				
				List<String> alInner = new ArrayList<String>();
				
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left;width:130px;margin-top:1px;padding-right:8px\" id=\"myDiv" + nCount + "\" >");
				if(deniedList.contains(rs.getString("emp_lta_id"))) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
				} else if(1==rs.getInt("is_approved")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("emp_lta_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("emp_lta_id")))==rs.getInt("is_approved")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))>0) {
					sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
					sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id")+"_"+userType)))) {
//					if(!checkEmpList.contains(strSessionEmpId) && strUserType.equalsIgnoreCase(HRMANAGER)) { 
						if(rs.getInt("is_approved")==0) {
							if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {		
								sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
								sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
							} else {
								/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\" ></i>");
								
							}
						} else if(rs.getInt("is_approved")==1) {							
							/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
							
						} else {
							/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
						}
					/*} else {
						sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
					}*/
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {		
						sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>&nbsp;");
						sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("emp_lta_id")+"','"+userType+"');\"></i>");
					} else {
						/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\" ></i>");
						
					}
				}
				
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:85%\">");
				
				sb.append(hmEmpNames.get(rs.getString("emp_id")));
				
				int nEmpLevelId = CF.getEmpLevelId(rs.getString("emp_id"), request);
				Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
				sb.append(" has submitted a request for "+uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")), "")+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" for "+ strCurrSymbol+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("applied_amount")))
						+" specifying "
						+"\""+rs.getString("lta_purpose")+"\""
						);
				
				if(rs.getInt("is_approved")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approved_by")) +" on "+ uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				} else if(rs.getInt("is_approved")== 0) {
					sb.append(" is waiting for approval");
				} else if(rs.getInt("is_approved")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approved_by")) +" on "+ uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				} 
				
				sb.append("  ["+uF.showData(hmUserTypeMap.get(userType), "")+"]");
				
				sb.append("</div>");
					
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
//					if(CF.getIsRemoteLocation()) {
//						sb.append("<a target=\"_blank\" title=\"View Attachment\" href=\""+CF.getStrDocRetriveLocation() +rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					} else {
//						sb.append("<a target=\"_blank\" title=\"View Attachment\" href=\""+request.getContextPath()+DOCUMENT_LOCATION+rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					}
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				 
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("emp_lta_id"))!=null) {
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("emp_lta_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("emp_lta_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("emp_lta_id"))), "")+")" : "";
//					alInner.add(hmEmpNames.get(approvedby)+strUserTypeName);
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("emp_lta_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("emp_lta_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("emp_lta_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
				
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String addLTA(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			con = db.makeConnection(con);
			
			
			String fileName =null;
			if(getStrDocument()!=null) {
			
//				if(CF.getStrDocSaveLocation()==null) {
//					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), CF.getIsRemoteLocation(), CF);
//				} else {
//					fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getStrDocument(), getStrDocumentFileName(), CF.getIsRemoteLocation(), CF);
//				}
				if(CF.getStrDocSaveLocation()==null) {
					fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
				} else {
					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+strSessionEmpId, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
				}
			}
			
			pst = con.prepareStatement("insert into emp_lta_details (emp_id,actual_amount,applied_amount,is_approved,entry_date,ref_document,lta_purpose,salary_head_id,is_paid) values (?,?,?,?,?,?,?,?,?);");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDouble(2, uF.parseToDouble(getStrActualAmount()));
			pst.setDouble(3, uF.parseToDouble(getStrAmount()));
			pst.setInt(4, 0);
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(6, fileName);
			pst.setString(7, getStrPurpose());
			pst.setInt(8, uF.parseToInt(getSalaryHead()));
			pst.setBoolean(9, false);
			int x = pst.executeUpdate();
			pst.close();
			if(x>0) {
				String emp_lta_id=null;
				pst = con.prepareStatement("select max(emp_lta_id) as emp_lta_id from emp_lta_details");
				rs=pst.executeQuery();
				while(rs.next()) {
					emp_lta_id=rs.getString("emp_lta_id");
				}
				rs.close();
				pst.close();
				List<String> alManagers = null;
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
					alManagers = insertLeaveApprovalMember(con,pst,rs,emp_lta_id,uF);
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;
		
	}
	
	private List<String> insertLeaveApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, String emp_lta_id, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1,uF.parseToInt(getPolicy_id()));
			rs=pst.executeQuery();
			
			Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));
				
				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it=hmMemberMap.keySet().iterator();
			while(it.hasNext()) {
				String work_flow_member_id=it.next();
				List<String> innerList=hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
//				System.out.println("innerList.get(3)+memid====>"+innerList.get(3)+memid+"=====>"+request.getParameter(innerList.get(3)+memid));
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")) {
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
//					System.out.println("approval empid====>"+empid);
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,uF.parseToInt(emp_lta_id));
					pst.setString(3,WORK_FLOW_LTA);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();
					

					String alertData = "<div style=\"float: left;\"> Received a new CTC Variable Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> amount "+uF.formatIntoTwoDecimal(uF.parseToDouble(getStrAmount()))+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
						strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
					}
					String alertAction = "CTCVariable.action?pType=WR"+strSubAction;
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId+"");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(empid);
//					userAlerts.set_type(LTA_REQUEST_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
					
					if(!alManagers.contains(empid)) {
						alManagers.add(empid);
					}
				}
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alManagers;
	}

	public String updateLTA(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs=null;
		
		try{
			String fileName =null;
			if(getStrDocument()!=null) {
			
//				if(CF.getStrDocSaveLocation()==null) {
//					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), CF.getIsRemoteLocation(), CF);
//				} else {
//					fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getStrDocument(), getStrDocumentFileName(), CF.getIsRemoteLocation(), CF);
//				}
				if(CF.getStrDocSaveLocation()==null) {
					fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
				} else {
					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+strSessionEmpId, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
				}
			}
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_LTA+"' and effective_id in (select emp_lta_id from emp_lta_details where emp_lta_id = ?)");
			pst.setInt(1, uF.parseToInt(getStrId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("update emp_lta_details set actual_amount=?,applied_amount=?,ref_document=?,lta_purpose=?,salary_head_id=? where emp_lta_id=?");
			pst.setDouble(1, uF.parseToDouble(getStrActualAmount()));
			pst.setDouble(2, uF.parseToDouble(getStrAmount()));
			pst.setString(3, fileName);
			pst.setString(4, getStrPurpose());
			pst.setInt(5, uF.parseToInt(getSalaryHead()));
			pst.setInt(6, uF.parseToInt(getStrId()));
			pst.execute();
			pst.close();
			
			List<String> alManagers = null;
			if(uF.parseToBoolean(CF.getIsWorkFlow())) {
				alManagers = insertLeaveApprovalMember(con,pst,rs,getStrId(),uF);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;
		
	}
	
	public String viewLTA(String strE,UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from emp_lta_details where emp_lta_id=?");
			pst.setInt(1, uF.parseToInt(strE));
			rs = pst.executeQuery();
			if(rs.next()) {
				setStrPurpose(rs.getString("lta_purpose"));
				setStrActualAmount(rs.getString("actual_amount"));
				setStrAmount(rs.getString("applied_amount"));
				setStrViewDocument(rs.getString("ref_document"));
				setStrId(strE);
				
				StringBuilder sbDoc = new StringBuilder();
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>0) {
//					if(CF.getIsRemoteLocation()) {
//						sbDoc.append("<a target=\"_blank\" title=\"Reference Document\" href=\""+CF.getStrDocRetriveLocation() +rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//					} else {
//						sbDoc.append("<a target=\"_blank\" title=\"Reference Document\" href=\""+request.getContextPath()+DOCUMENT_LOCATION+rs.getString("ref_document")+"\"><img src=\"images1/payslip.png\"></a> ");
//						
//					}
					if(CF.getStrDocRetriveLocation()==null) {
						sbDoc.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				setStrViewDocument(sbDoc.toString());
				setSalaryHead(rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrPurpose() {
		return strPurpose;
	}

	public void setStrPurpose(String strPurpose) {
		this.strPurpose = strPurpose;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public File getStrDocument() {
		return strDocument;
	}

	public void setStrDocument(File strDocument) {
		this.strDocument = strDocument;
	}

	public String getStrViewDocument() {
		return strViewDocument;
	}

	public void setStrViewDocument(String strViewDocument) {
		this.strViewDocument = strViewDocument;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrDocumentFileName() {
		return strDocumentFileName;
	}

	public void setStrDocumentFileName(String strDocumentFileName) {
		this.strDocumentFileName = strDocumentFileName;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
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

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
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

	public String getStrActualAmount() {
		return strActualAmount;
	}

	public void setStrActualAmount(String strActualAmount) {
		this.strActualAmount = strActualAmount;
	}

	public String getSalaryHead() {
		return salaryHead;
	}

	public void setSalaryHead(String salaryHead) {
		this.salaryHead = salaryHead;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
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

}
