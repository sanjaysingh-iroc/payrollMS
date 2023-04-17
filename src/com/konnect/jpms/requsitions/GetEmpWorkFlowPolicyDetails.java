package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmpWorkFlowPolicyDetails  extends ActionSupport implements IStatements, ServletRequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null; 
 
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(GetEmpWorkFlowPolicyDetails.class);
	
	
	
	String empId;
	
	public String execute(){ 
		
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
	
		System.out.println("in client "+empId);
		
		if(empId!=null){
			getLeavePolicyMember();
		}
		
			
		
		return SUCCESS;
		
	}



	private void getLeavePolicyMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		
		
		String policy_id=null;
		try {
			
			int strEmpID=uF.parseToInt(getEmpId());
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId=hmEmpLevelMap.get(""+strEmpID);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(""+strEmpID);
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_REIMBURSEMENTS+"' and level_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			rs = pst.executeQuery();
			while(rs.next()){
				policy_id=rs.getString("policy_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id)>0){
				
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
						" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
				rs=pst.executeQuery();
				
				Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
				while(rs.next()){
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
				while(it.hasNext()){
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1){
						int memid=uF.parseToInt(innerList.get(1));
						
						switch(memid){
						
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname, epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" +
														" and ud.emp_id not in(?) order by epd.emp_fname");
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
								
								if(outerList!=null && !outerList.isEmpty()){
									StringBuilder sbComboBox=new StringBuilder();
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++){
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
										"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'  order by epd.emp_fname");
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
								
								if(outerList11!=null && !outerList11.isEmpty()){
									StringBuilder sbComboBox11=new StringBuilder();
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++){
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
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" +
														" and ud.emp_id not in(?) order by epd.emp_fname");
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
								
								if(outerList1!=null && !outerList1.isEmpty()){
									StringBuilder sbComboBox1=new StringBuilder();
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++){
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
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" +
												" and ud.emp_id not in(?) order by epd.emp_fname");
							
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
								
								if(outerList2!=null && !outerList2.isEmpty()){
									StringBuilder sbComboBox2=new StringBuilder();
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList2.size();i++){
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
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" +
												" and ud.emp_id not in(?) order by epd.emp_fname");
								
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
								
								if(outerList3!=null && !outerList3.isEmpty()){
									StringBuilder sbComboBox3=new StringBuilder();
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++){
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
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" +
												" and ud.emp_id not in(?) order by epd.emp_fname");
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
								
								if(outerList4!=null && !outerList4.isEmpty()){
									StringBuilder sbComboBox4=new StringBuilder();
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++){
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
								
								if(outerList5!=null && !outerList5.isEmpty()){
									StringBuilder sbComboBox5=new StringBuilder();
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++){
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
							
							if(outerHODList!=null && !outerHODList.isEmpty()){
								StringBuilder sbComboBox11=new StringBuilder();
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++){
									List<String> alList=outerHODList.get(i);
									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
								
								hmMemberOption.put(innerList.get(4), optionTr11);
							}
						
							break;
						
						}						
						
					}else if(uF.parseToInt(innerList.get(0))==2){
						int memid=uF.parseToInt(innerList.get(1));						
						
						
						/*pst = con.prepareStatement("select * from grades_details gd, designation_details dd, level_details ld, employee_official_details eod " +
								" where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id and gd.grade_id = eod.grade_id " +
								" and dd.designation_id =? and eod.wlocation_id=? and eod.emp_id not in(?)");*/
						pst = con.prepareStatement("select emp_id from (select * from grades_details gd, designation_details dd, level_details ld, " +
								" employee_official_details eod where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id " +
								" and gd.grade_id = eod.grade_id and dd.designation_id =? and eod.wlocation_id=? and eod.emp_id not in(?))as a," +
								" employee_personal_details epd where a.emp_id=epd.emp_per_id  order by epd.emp_fname ");
						pst.setInt(1, memid);
						pst.setInt(2, uF.parseToInt(locationID));
						pst.setInt(3, strEmpID);
						rs = pst.executeQuery();
						List<List<String>> outerList=new ArrayList<List<String>>();
						while (rs.next()) {
							List<String> alList=new ArrayList<String>();
							alList.add(rs.getString("emp_id"));							
							outerList.add(alList);									
						}
						rs.close();
						pst.close();
						
						if(outerList!=null && !outerList.isEmpty()){
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++){
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
						
					}else if(uF.parseToInt(innerList.get(0))==3){
						int memid=uF.parseToInt(innerList.get(1));
											
						List<List<String>> outerList=new ArrayList<List<String>>();
						if (uF.parseToBoolean(CF.getIsSpecificEmp())) {
							pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id  order by epd.emp_fname");
							rs = pst.executeQuery();

							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								outerList.add(alList);
							}
							rs.close();
							pst.close();
						} else {

							pst = con
									.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
											+ " employee_official_details eod,employee_personal_details epd where "
											+ " ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'"
											+ " and ud.emp_id not in(?) order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(2, strEmpID);
							rs = pst.executeQuery();
							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								outerList.add(alList);
							}
							rs.close();
							pst.close();
						}
						
						if(outerList!=null && !outerList.isEmpty()){
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++){
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				StringBuilder sb = new StringBuilder();				
				 sb.append("<div id=\"popup_name" + strEmpID + "\" class=\"popup_block\">" + 
						   "<h2 class=\"textcolorWhite\">Reimbursements of "+hmEmpCodeName.get(""+strEmpID)+"</h2>" + 
						   "<table>");
				 if(hmMemberOption!=null && !hmMemberOption.isEmpty() ){
					 Iterator<String> it1=hmMemberOption.keySet().iterator();
					while(it1.hasNext()){
						String memPosition=it1.next();
						String optiontr=hmMemberOption.get(memPosition);					
						sb.append(optiontr); 
					}
					sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"input_button\"/></td>" +
							"</tr>");
				 }else{
					 sb.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
				 }
				 sb.append("</table></div>");
				
				String divpopup="<input type=\"button\" name=\"submit1\" value=\"Submit\" class=\"input_button\"/>";
				
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("divpopup",divpopup);
				request.setAttribute("reimbursementsD", sb.toString());
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



	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}



	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

}
