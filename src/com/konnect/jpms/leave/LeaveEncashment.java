package com.konnect.jpms.leave;

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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.ajax.GetLeaveEncashmentInfo;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveEncashment extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(LeaveEncashment.class);
	
	private String strId;
	private String strReason;
	private String strNoOfDays;
	private String typeOfLeave;
	private String strAvailableEncashment;
	private List<FillLeaveType> leaveTypeList;
	 
	private String policy_id;
	
	private String strStartDate;
	private String strEndDate;
	
	private String alertStatus;
	private String alert_type;
	
	private String strPaycycle;
	private String pageType;
	
	private String currUserType;
	private String alertID;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		System.out.println("CF ===>> " + CF);
		if (CF==null) return LOGIN;
//		System.out.println("CF 1111 ===>> " + CF);
		
//		System.out.println("pageType=>"+getPageType());
//		System.out.println("StrPaycycle=>"+getStrPaycycle());

		String strOrgId=(String)session.getAttribute(ORGID);
	
		System.out.println("strOrgId::"+strOrgId);
	
		strUserType = (String)session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PLeaveEncashment);
		request.setAttribute(TITLE, TLeaveEncashment);
		
		UtilityFunctions uF = new UtilityFunctions();
		String strE = (String)request.getParameter("E");
		
//		String strDomain = request.getServerName().split("\\.")[0];
//		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
//		

		System.out.println("getUserType===>"+getCurrUserType());
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		String[] arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, (String)session.getAttribute(ORGID), request);
		if(getStrStartDate()==null) {
			setStrStartDate(arrDates[0]);
		}
		if(getStrEndDate()==null) {
			setStrEndDate(arrDates[1]);
		}
		System.out.println("strE===>"+strE);
		System.out.println("getStrId===>"+getStrId());
		if(strE!=null) {
			viewLeaveEncashment(strE);
		} else if(getStrId()!=null && getStrId().length()>0) {
			updateLeaveEncashment();
		} else if(getStrNoOfDays()!=null) {
			addLeaveEncashment();
			if(getPageType()!=null && getPageType().trim().equals("TS")) {    
				return VIEW;
			}
		}
		
//		System.out.println("leave encashment currUserType==>"+getCurrUserType());
		getLeaveEncashPolicyMember();
		
		if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT)) ) {
			leaveEncashmentReportEmp();
		} else {
			leaveEncashmentReportAdmin();
		}
		System.out.println("returning load");
		return loadLeaveEncashment(uF,true, uF.parseToInt(strOrgId));
	}
	
	
	public String loadLeaveEncashment(UtilityFunctions uF, boolean isNull, int orgId) {
		
		String strE = (String)request.getParameter("E");
		leaveTypeList = new FillLeaveType(request).fillLeaveForEncashment(orgId);
		
		if(strE==null && isNull) {
			setStrReason(null);
			setStrNoOfDays(null);
		}
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	 
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}

		String selectedFilter = CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public void validate() {
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PLeaveEncashment);
		request.setAttribute(TITLE, TLeaveEncashment);
		
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return;
		
		if (getStrAvailableEncashment()!=null && getStrAvailableEncashment().length()>0 && (uF.parseToDouble(getStrAvailableEncashment())<=0 || uF.parseToDouble(getStrAvailableEncashment()) < uF.parseToDouble(getStrNoOfDays()))) {
            addFieldError("strEncashment", "The requested no of days are not availble for encashment.");
            
            GetLeaveEncashmentInfo obj = new GetLeaveEncashmentInfo();
            obj.setServletRequest(request);
            obj.getLeaveEncashmentInfo(strSessionEmpId, getTypeOfLeave(), CF, uF);
        } 
		String strOrgId=(String)session.getAttribute(ORGID);
		loadLeaveEncashment(uF, false, uF.parseToInt(strOrgId));
	}
	
	
	private void getLeaveEncashPolicyMember() {
		System.out.println("In getLeaveEncashPolicyMember ");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		
		
		String policy_id=null;
		try {
			
			int strEmpID = uF.parseToInt(strSessionEmpId);
			
			con = db.makeConnection(con);
			
//			System.out.println("strEmpID=====> "+strEmpID);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId=hmEmpLevelMap.get(""+strEmpID);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(""+strEmpID);
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
//			System.out.println("empLevelId=====> "+empLevelId);
			
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_LEAVE_ENCASH+"' and level_id=? and wlocation_id=?");
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
				System.out.println("policy_id=====> "+policy_id);
				
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
						" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
				rs=pst.executeQuery();
				System.out.println("policy_id=====> "+pst);
				
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
				
				System.out.println("hmMemberMap=====> "+hmMemberMap);
				
				Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
			
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
				boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
				
				
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1) {
						int memid=uF.parseToInt(innerList.get(1));
						System.out.println("memid::"+memid);
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
									
									String optionTr="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id=? and supervisor_emp_id!=0) as a," +
										"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
										" and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1,strEmpID);
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
									
									String optionTr11="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id "
												+ " and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
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
									
									String optionTr1="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
										+ " and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
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
									
									String optionTr2="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
										+ " and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
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
									
									String optionTr3="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id "
										+ " and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
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
									
									String optionTr4="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
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
									
									String optionTr5="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;							
						
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
									"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
									"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, strEmpID);
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
							
							String optionTr="<tr><td class=\"label alignRight\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				String divpopup="";
				StringBuilder sb = new StringBuilder();
//				System.out.println("uF.parseToBoolean(CF.getIsWorkFlow())====>"+uF.parseToBoolean(CF.getIsWorkFlow()));
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {		
					 sb.append("<div id=\"popup_name" + strEmpID + "\" class=\"popup_block\">" + 
							   "<h2 class=\"textcolorWhite\">LeaveEncash of "+hmEmpCodeName.get(""+strEmpID)+"</h2>" + 
							   "<table>");
										
					 if(hmMemberOption!=null && !hmMemberOption.isEmpty() ) {
						 Iterator<String> it1=hmMemberOption.keySet().iterator();
						while(it1.hasNext()) {
							String memPosition=it1.next();
							String optiontr=hmMemberOption.get(memPosition);					
							sb.append(optiontr); 
						}
						sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"input_button\" onclick=\"return checkLeaveEncashLimit();\"/></td>" +
								"</tr>");
					 } else {
						 sb.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
					 }
					 sb.append("</table></div>");
					
					divpopup="<input type=\"button\" name=\"submit1\" value=\"Submit\" class=\"input_button\" onclick=\"return checkLeaveEncashLimit();\"/>";
				} else {
					sb.append("");
					divpopup="<input type=\"submit\" name=\"submit1\" value=\"Submit\" class=\"input_button\" onclick=\"return checkLeaveEncashLimit();\"/>";
				}
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("divpopup",divpopup);
				request.setAttribute("LeaveEncashsD", sb.toString());
				request.setAttribute("strEmpID", strEmpID);
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String leaveEncashmentReportEmp() {
		System.out.println("In leaveEncashmentReportEmp");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			con = db.makeConnection(con);

			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();		
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();		
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			System.out.println("pst1===>"+pst);
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();			
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			System.out.println("pst2===>"+pst);
//			pst = con.prepareStatement("select * from emp_leave_encashment where emp_id=? and encashment_status = true");
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			rs = pst.executeQuery();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_encashment where emp_id=?");
			sbQuery.append(" and to_date(entry_date::text,'yyyy-MM-dd') between ? and ? ");
			sbQuery.append(" order by entry_date desc ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			System.out.println("pst1===>"+pst);
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left; width:7%;\">");
							
				if(rs.getInt("is_approved")==0 ) {
					 /*sb.append("<div style=\"float:left;\"><img src=\""+request.getContextPath()+"/images1/icons/pending.png\" title=\"Waiting for approval\" border=\"0\" /></div>");*/
					sb.append("<div style=\"float:left;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5;\" title=\"Waiting for approval\"></i></div>");
				} else if(rs.getInt("is_approved")==1) {
					/*sb.append("<div style=\"float:left;\"><img src=\""+request.getContextPath()+"/images1/icons/approved.png\" title=\"approved\" border=\"0\" /></div>");*/
					sb.append("<div style=\"float:left;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d;\" title=\"approved\"></i></div>");
				} else if(rs.getInt("is_approved")==-1) {
					/*sb.append("<div style=\"float:left;\"><img src=\""+request.getContextPath()+"/images1/icons/denied.png\" title=\"deny\" border=\"0\" /></div>");*/
					sb.append("<div style=\"float:left;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"deny\" style=\"color:#e22d25;\"></i></div>");
				}
				
				
				
				if(rs.getInt("is_approved")==0) {
					sb.append("<div style=\"float:left;width:20px;margin: 0px 5px 0 10px; cursor:pointer;\">");
					/*sb.append("<img src=\""+request.getContextPath()+"/images1/icons/pullout.png\" title=\"Remove Request\" onclick=\"cancelLeaveEncashment('2', '"+rs.getString("leave_encash_id")+"');\" border=\"0\" />");*/
					sb.append("<a href=\"javascript:void(0);\" onclick=\"cancelLeaveEncashment('2', '"+rs.getString("leave_encash_id")+"');\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i></a>");
					sb.append("</div>");
					sb.append("<div style=\"float:left;width:20px;margin: 0px 5px 0 10px;\" id=\"myDiv"+nCount+"\"></div>");  
					}
				sb.append("</div>");
				    
				sb.append("<div style=\"float:left;width:80%\">");
				
				sb.append("You have submitted a request for leave encashment for "+rs.getString("no_days")+
						" days on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" specifying "
						+"\""+rs.getString("encash_reason")+"\""
						);
				
				
				boolean isApproval1 = false;
				if(rs.getInt("is_approved")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approved_by")));
					isApproval1 = true;
				} else if(rs.getInt("is_approved")== 0) {
					sb.append(" is waiting for approval");
					isApproval1 = true;
				} else if(rs.getInt("is_approved")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approved_by")) );
					isApproval1 = true;
				} 
				
//				sb.append(" <a href=\"LeaveEncashment.action?E="+rs.getString("leave_encash_id")+"\">Edit</a> ");
				 
				sb.append("</div>");
				System.out.println("sb===>"+sb);
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("leave_encash_id"))!=null) {
					String approvedby=hmAnyOneApproeBy.get(rs.getString("leave_encash_id"));
					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("leave_encash_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("leave_encash_id"))), "")+")" : "";
					alInner.add(hmEmpNames.get(approvedby)+strUserTypeName);
				} else {
					if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("leave_encash_id"))!=null) {
						alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_encash_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
					} else {
						alInner.add("");
					}
				}
				System.out.println("alInner===>"+alInner);
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
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
	
	public String leaveEncashmentReportAdmin() {
        	
		System.out.println("In leaveEncashmentReportAdmin");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			con = db.makeConnection(con);

			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
//			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
//			String locationID=hmEmpWlocationMap.get(strSessionEmpId);
			
//			Map<String, String> hmLevelMap = CF.getEmpLevelMap(con);
			
			pst = con.prepareStatement("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' group by effective_id");
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			System.out.println("In hmNextApproval::"+hmNextApproval);
			rs.close();
			pst.close();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"'");
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
			System.out.println("In pst::"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			System.out.println("In hmMemNextApproval::"+hmMemNextApproval);
			pst = con.prepareStatement("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' group by effective_id");
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select leave_encash_id from emp_leave_encashment where is_approved=-1");
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("leave_encash_id"))) {
					deniedList.add(rs.getString("leave_encash_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
			" and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' group by effective_id,is_approved");
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();		
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();		
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			System.out.println("In pst::"+pst);
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();			
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			System.out.println("In pst::"+pst);
			rs.close();
			pst.close();
			 
			
			
//			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where " +
//			"effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' and user_type_id=? order by effective_id,member_position");
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE_ENCASH+"'");
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
			System.out.println("In pst::"+pst);
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
			
			sbQuery=new StringBuilder();
			sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_leave_encashment ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
				" and  to_date(entry_date::text,'yyyy-MM-dd')>=? and to_date(entry_date::text,'yyyy-MM-dd') <=? and is_approved > -2 ");
			sbQuery.append(") e, work_flow_details wfd where e.leave_encash_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEAVE_ENCASH+"' ");
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
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			System.out.println("In pst::"+pst);
			List<List<String>> alReport = new ArrayList<List<String>>();
			List<String> alEmployeeList = new ArrayList<String>();	
			List<String> alList = new ArrayList<String>();	
			int nCount=0;
			while(rs.next()) {
				
				if(!alEmployeeList.contains(rs.getString("emp_id")) ) {
					alEmployeeList.add(rs.getString("emp_id"));
				}
			
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("leave_encash_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("leave_encash_id")+"_"+strSessionEmpId);
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
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("leave_encash_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("leave_encash_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("leave_encash_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;
				}
				
				List<String> alInner = new ArrayList<String>();
				
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				System.out.println("hmNextApproval::"+hmNextApproval);
				System.out.println("hmNextApproval::"+rs.getString("leave_encash_id"));
				System.out.println("userType::"+userType);
				
				sb.append("<div style=\"float:left; margin-top:-7px; width:9%;\">");
				
				if(deniedList.contains(rs.getString("leave_encash_id"))) {
					 /*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25; padding-top:15px\" title=\"Denied\"></i>");
				} else if(rs.getInt("is_approved")==1) {							
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i title=\"Approved\" class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d;padding-top:15px;\"></i>");
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("leave_encash_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("leave_encash_id")))==rs.getInt("is_approved")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" title=\"Approved\"  aria-hidden=\"true\" style=\"color:#54aa0d;padding-top:15px;\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("leave_encash_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_encash_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("leave_encash_id")))>0) {
					sb.append("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) getApprovalEncashment('1','"+rs.getString("leave_encash_id")+"','"+uF.showData(hmEmpNames.get(rs.getString("emp_id")), "")+"','"+rs.getString("emp_id")+"','"+userType+"','"+getCurrUserType()+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("leave_encash_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("leave_encash_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_encash_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("leave_encash_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("leave_encash_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_encash_id")+"_"+userType)))) {
					if(rs.getInt("is_approved")==0) {
						if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
							sb.append("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) getApprovalEncashment('1','"+rs.getString("leave_encash_id")+"','"+uF.showData(hmEmpNames.get(rs.getString("emp_id")), "")+"','"+rs.getString("emp_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
									" <a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("leave_encash_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
						} else {
							 /*sb.append("<img src=\"images1/icons/re_submit.png\" style=\"margin-top: -10px;\" title=\"Waiting for workflow\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d;margin-top: -10px;\" title=\"Waiting for workflow\"></i>");
							System.out.println("checkGHRInWorkflow::"+checkGHRInWorkflow);
							if(!checkGHRInWorkflow) {
								sb.append("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) getApprovalEncashment('1','"+rs.getString("leave_encash_id")+"','"+uF.showData(hmEmpNames.get(rs.getString("emp_id")), "")+"','"+rs.getString("emp_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
									" <a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("leave_encash_id")+"','');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
							}
						}
					} else if(rs.getInt("is_approved")==1) {							
						 /*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\"/>");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d;padding-top:15px;\" title=\"Approved\"></i>");
					} else {
						/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\"/>");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25;padding-top:15px;\"></i>");
					}
					
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						sb.append("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) getApprovalEncashment('1','"+rs.getString("leave_encash_id")+"','"+uF.showData(hmEmpNames.get(rs.getString("emp_id")), "")+"','"+rs.getString("emp_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
								" <a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("leave_encash_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
					} else {
						/*sb.append("<img src=\"images1/icons/re_submit.png\" style=\"margin-top: -10px;\" title=\"Waiting for workflow\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
						
					}
				}
							
//				if(rs.getInt("is_approved")==0 ) {
//					sb.append("<div id=\"myDiv"+nCount+"\">");
//					sb.append("<img src=\""+request.getContextPath()+"/images1/icons/approved.png\" title=\"Approve\" border=\"0\" onclick=\"confirm('Are you sure you want to approve this?')?getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=1&RID="+rs.getString("leave_encash_id")+"&T=LE&M=MA&EMPID="+rs.getString("emp_id")+"&LTID="+rs.getString("leave_type_id")+"&NOD="+rs.getString("no_days")+"'):''\"/>");
//					sb.append("&nbsp;<img src=\""+request.getContextPath()+"/images1/icons/denied.png\" title=\"Deny\" border=\"0\" onclick=\"confirm('Are you sure you want to deny this?')?getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=-1&RID="+rs.getString("leave_encash_id")+"&T=LE&M=MA&EMPID="+rs.getString("emp_id")+"&LTID="+rs.getString("leave_type_id")+"&NOD="+rs.getString("no_days")+"'):''\"/>");
//					sb.append("</div>");
//				} else if(rs.getInt("is_approved")==1) {
//					sb.append("<img src=\""+request.getContextPath()+"/images1/icons/approved.png\" title=\"Approved\" border=\"0\" />");
//				} else if(rs.getInt("is_approved")==-1) {
//					sb.append("<img src=\""+request.getContextPath()+"/images1/icons/denied.png\" title=\"Denied\" border=\"0\" />");
//				}
				
				sb.append("</div>");
				
//				if(rs.getInt("is_approved")==0) {
//					sb.append("<div style=\"float:left;width:20px;margin: 1px 5px 0 5px;cursor:pointer;\">");
//					sb.append("<img src=\""+request.getContextPath()+"/images1/icons/pullout.png\" title=\"Remove Request\" onclick=\"confirm('Are you sure you want to remove this?')?getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=2&RID="+rs.getString("leave_encash_id")+"&T=LER&M=D'):''\" border=\"0\" />");
//					sb.append("</div>");
//					
//					sb.append("<div style=\"float:left;width:20px;margin: 1px 5px 0 5px;\" id=\"myDiv"+nCount+"\"></div>");
//					
//				}
				
				    
				sb.append("<div style=\"float:left;width:80%\">");
				
				sb.append(hmEmpNames.get(rs.getString("emp_id"))+" has submitted a request for leave encashment for "+rs.getString("no_days")+
						" days on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" specifying "
						+"\""+rs.getString("encash_reason")+"\""
						);
				
				
				boolean isApproval1 = false;
				if(rs.getInt("is_approved")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approved_by")));
					isApproval1 = true;
				} else if(rs.getInt("is_approved")== 0) {
					sb.append(" is waiting for approval");
					isApproval1 = true;
				} else if(rs.getInt("is_approved")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approved_by")) );
					isApproval1 = true;
				}
				
				sb.append("  ["+uF.showData(hmUserTypeMap.get(userType), "")+"]");
				
				/*sb.append(" <a href=\"LeaveEncashment.action?E="+rs.getString("leave_encash_id")+"\">Edit</a> ");*/
				
				sb.append("</div>");
				System.out.println("sb====>"+sb);
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("leave_encash_id"))!=null) {
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("leave_encash_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("leave_encash_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("leave_encash_id"))), "")+")" : "";
//					alInner.add(hmEmpNames.get(approvedby)+strUserTypeName);
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_encash_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("leave_encash_id"))!=null) {
						alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_encash_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
					} else {
						alInner.add("");
					}
				}
				
				alReport.add(alInner);
				nCount++;
			}
			
			System.out.println("In alEmployeeList::"+alEmployeeList);
			rs.close();
			pst.close();
				
			request.setAttribute("alReport", alReport);
			
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
	
		
	public String addLeaveEncashment() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			int nNoOfDays = uF.parseToInt(getStrNoOfDays());
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into emp_leave_encashment(emp_id,leave_type_id,no_days,encash_reason,entry_date,is_approved,is_paid) values (?,?,?,?,?,?,?);");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			pst.setInt(3, nNoOfDays);
			pst.setString(4, getStrReason());
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, 0);
			pst.setBoolean(7, false);			
			pst.execute();
			pst.close();
			
//			leave_encash_id,emp_id,leave_type_id,no_days,encash_reason,entry_date,is_approved,approved_by,approved_date,is_paid,paid_from,paid_to,paycycle
			String leave_encash_id=null;
			pst = con.prepareStatement("select max(leave_encash_id)as leave_encash_id from emp_leave_encashment");
			rs=pst.executeQuery();
			while(rs.next()) {
				leave_encash_id=rs.getString("leave_encash_id");
			}
			rs.close();
			pst.close();
			
//			System.out.println("reimbursement_id====>"+reimbursement_id);
			
			List<String> alManagers = null;
			if(uF.parseToBoolean(CF.getIsWorkFlow())) {
				alManagers = insertApprovalMember(con,pst,rs,leave_encash_id,uF);
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
	
	private List<String> insertApprovalMember(Connection con, PreparedStatement pst, ResultSet rs, String leave_encash_id, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1,uF.parseToInt(getPolicy_id()));
			rs = pst.executeQuery();
			
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
					pst.setInt(2,uF.parseToInt(leave_encash_id));
					pst.setString(3,WORK_FLOW_LEAVE_ENCASH);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9, userTypeId);
					pst.execute();
					pst.close();
					
					String alertData = "<div style=\"float: left;\"> Received a new Leave Encashment from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> no. of days "+uF.parseToInt(getStrNoOfDays())+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiLeaveEncashment"+strSubAction;
					} else {
						alertAction = "PayApprovals.action?pType=WR&callFrom=NotiLeaveEncashment"+strSubAction;
					}
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
//					userAlerts.set_type(LEAVE_ENCASH_REQUEST_ALERT);
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


	public String updateLeaveEncashment() {
		
		System.out.println("In updateLeaveEncashment");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		
		try{
			
			int nNoOfDays = uF.parseToInt(getStrNoOfDays());
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("update emp_leave_encashment set leave_type_id=?,no_days=?,encash_reason=? where leave_encash_id=?");
			pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
			pst.setInt(2, nNoOfDays);
			pst.setString(3, getStrReason());
			pst.setInt(4, uF.parseToInt(getStrId()));		
			pst.execute();
			pst.close();
			
			if(uF.parseToBoolean(CF.getIsWorkFlow())) {
				pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"'");
				pst.setInt(1, uF.parseToInt(getStrId()));
				pst.execute();
				pst.close();
				
				List<String> alManagers = insertApprovalMember(con,pst,rs,getStrId(),uF);
			}
			
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
	
	public String viewLeaveEncashment(String strE) {

		System.out.println("In viewLeaveEncashment");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
//			leave_encash_id,emp_id,leave_type_id,no_days,encash_reason,entry_date,is_approved,approved_by,approved_date,is_paid,paid_from,paid_to,paycycle
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from emp_leave_encashment where leave_encash_id=?");
			pst.setInt(1, uF.parseToInt(strE));
			rs = pst.executeQuery();
			System.out.println(" viewLeaveEncashment pst==>"+pst);

			if(rs.next()) {
				setTypeOfLeave(rs.getString("leave_type_id"));
				setStrReason(rs.getString("encash_reason"));
				setStrNoOfDays(rs.getString("no_days"));
				setStrId(strE);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
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

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public String getStrNoOfDays() {
		return strNoOfDays;
	}

	public void setStrNoOfDays(String strNoOfDays) {
		this.strNoOfDays = strNoOfDays;
	}

	public String getTypeOfLeave() {
		return typeOfLeave;
	}

	public void setTypeOfLeave(String typeOfLeave) {
		this.typeOfLeave = typeOfLeave;
	}

	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}

	public String getStrAvailableEncashment() {
		return strAvailableEncashment;
	}

	public void setStrAvailableEncashment(String strAvailableEncashment) {
		this.strAvailableEncashment = strAvailableEncashment;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
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
	public String getStrPaycycle() {
		return strPaycycle;
	}

	public void setStrPaycycle(String strPaycycle) {
		this.strPaycycle = strPaycycle;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
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
