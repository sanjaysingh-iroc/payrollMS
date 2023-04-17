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

public class ApplyLeaveEncashment extends ActionSupport implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(LeaveEncashment.class);
	
	String strId;
	String strReason;
	String strNoOfDays;
	String typeOfLeave;
	String strAvailableEncashment;
	List<FillLeaveType> leaveTypeList;
	
	String policy_id;
	
	String alertStatus;
	String alert_type;
	
	String strPaycycle;
	String pageType;
	
	String currUserType;
	String alertID;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
//		System.out.println("pageType=>"+getPageType());
//		System.out.println("StrPaycycle=>"+getStrPaycycle());

		String strOrgId=(String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PLeaveEncashment);
		request.setAttribute(TITLE, TLeaveEncashment);
		
		UtilityFunctions uF = new UtilityFunctions();
		String strE = (String)request.getParameter("E");
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		System.out.println("strE ===>>>> " + strE);
		System.out.println("getStrId ===>>>> " + getStrId());
		System.out.println("getStrNoOfDays ===>>>> " + getStrNoOfDays());
		if(strE!=null) {
			viewLeaveEncashment(strE);
		} else if(getStrId()!=null && getStrId().length()>0) {
			updateLeaveEncashment();
			if(getPageType()!=null && getPageType().trim().equals("TS")) {
				return VIEW;
			} else {
				return SUCCESS;
			}
		} else if(getStrNoOfDays()!=null) {
			addLeaveEncashment();
			if(getPageType()!=null && getPageType().trim().equals("TS")) {    
				return VIEW;
			} else {
				return SUCCESS;
			}
		}
		System.out.println("getLeaveEncashPolicyMember ===>>>> ");
		getLeaveEncashPolicyMember();
		System.out.println("loadLeaveEncashment ===>>>> ");
		return loadLeaveEncashment(uF,true, uF.parseToInt(strOrgId));
	}
	
	
	public String loadLeaveEncashment(UtilityFunctions uF, boolean isNull, int orgId) {
		System.out.println("loadLeaveEncashment");
		String strE = (String)request.getParameter("E");
		System.out.println("orgId"+orgId);
		leaveTypeList = new FillLeaveType(request).fillLeaveForEncashment(orgId);
		System.out.println("leaveTypeList::"+leaveTypeList);
		if(strE==null && isNull) {
			setStrReason(null);
			setStrNoOfDays(null);
		}
		
		return LOAD;
	}
	
	 
//	public void validate() {
//		UtilityFunctions uF = new UtilityFunctions();
//		request.setAttribute(PAGE, PLeaveEncashment);
//		request.setAttribute(TITLE, TLeaveEncashment);
//		
//		session = request.getSession();
//		strUserType = (String)session.getAttribute(USERTYPE);
//		strSessionEmpId = (String)session.getAttribute(EMPID);
//		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF==null) return;
//		
//		if (getStrAvailableEncashment()!=null && getStrAvailableEncashment().length()>0 && (uF.parseToDouble(getStrAvailableEncashment())<=0 || uF.parseToDouble(getStrAvailableEncashment()) < uF.parseToDouble(getStrNoOfDays()))) {
//            addFieldError("strEncashment", "The requested no of days are not availble for encashment.");
//            
//            GetLeaveEncashmentInfo obj = new GetLeaveEncashmentInfo();
//            obj.setServletRequest(request);
//            obj.getLeaveEncashmentInfo(strSessionEmpId, getTypeOfLeave(), CF, uF);
//        } 
//		String strOrgId=(String)session.getAttribute(ORGID);
//		loadLeaveEncashment(uF, false, uF.parseToInt(strOrgId));
//	}
	
	
	private void getLeaveEncashPolicyMember() {
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
			System.out.println("pst====>"+pst);
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
				System.out.println("policy_id pst=====> "+pst);
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
				System.out.println("policy_id pst=====> "+pst);
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
						
						switch(memid) {
						
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, strEmpID);
								
								System.out.println("pst case1=>"+pst);
								
								rs = pst.executeQuery();
								List<List<String>> outerList=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName="";
									
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
									
									String optionTr="<tr><th>"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id=? and supervisor_emp_id!=0) as a," +
										"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
										" and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1,strEmpID);
								
								System.out.println("pst case2=>"+pst);

								rs = pst.executeQuery();
								List<List<String>> outerList11=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(MANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName="";
									
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
									
									String optionTr11="<tr><th>"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox11.toString()+"</td></tr>";
									
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
								
							//	System.out.println("pst case3=>"+pst);

								rs = pst.executeQuery();
								
								List<List<String>> outerList1=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname"));
									
									String strEmpMName="";
									
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
									
									String optionTr1="<tr><th>"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
										+ " and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, strEmpID);
								
							//	System.out.println("pst case4=>"+pst);

								rs = pst.executeQuery();
								List<List<String>> outerList2=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName="";
									
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
									
									String optionTr2="<tr><th>"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
										+ " and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, strEmpID);
								
							//	System.out.println("pst case5=>"+pst);

								rs = pst.executeQuery();
								List<List<String>> outerList3=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName="";
									
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
									
									String optionTr3="<tr><th>"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox3.toString()+"</td></tr>";
									
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
								
							//	System.out.println("pst case6=>"+pst);

								rs = pst.executeQuery();
								List<List<String>> outerList4=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName="";
									
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
									
									String optionTr4="<tr><th>"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox4.toString()+"</td></tr>";
									
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
								
								System.out.println("pst case7=>"+pst);

								rs = pst.executeQuery();
								List<List<String>> outerList5=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(HRMANAGER));
									alList.add(rs.getString("emp_fname"));
									
									String strEmpMName="";
									
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
									
									String optionTr5="<tr><th>"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;							
						
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
									"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
									"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, strEmpID);
							
						//	System.out.println("pst case13=>"+pst);

							rs = pst.executeQuery();
							List<List<String>> outerHODList=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName="";
								
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
								
								String optionTr11="<tr><th class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox11.toString()+"</td></tr>";
								
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
							
							String optionTr="<tr><th>Your work flow:<sup>*</sup></th><td>"+sbComboBox.toString()+"</td></tr>";
							
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
				System.out.println("hmMemberOption::"+hmMemberOption);
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
	
	
	private void viewLeaveEncashment(String strE) {

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
//			System.out.println("pst ===>> " + pst);
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
		
		return SUCCESS;
		
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
		
		return SUCCESS;
		
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
