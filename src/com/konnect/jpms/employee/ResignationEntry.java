package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ResignationEntry extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strBaseUserType;
	String strWLocationAccess;
	CommonFunctions CF;
	
	private String empResignationReason;
	private String strResignationId;
	private String emp_id;
	
	private String policy_id;
	private String empResignationDate;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType= (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PEmployeeResignation);
		request.setAttribute(TITLE, TResignationEntry);
		/*EncryptionUtility eU = new EncryptionUtility();
		if(uF.parseToInt(getEmp_id()) > 0) {
			String encodeEmpId = eU.encode(getEmp_id());
			setEmp_id(encodeEmpId);
		}
		
		if(getEmp_id() != null && uF.parseToInt(getEmp_id()) == 0) {
			String decodeEmpId = eU.decode(getEmp_id());
			setEmp_id(decodeEmpId);
		}*/
		
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || !accessEmpList.contains(getEmp_id())) {
			setEmp_id(strSessionEmpId);
		}
		
		String strEdit = (String)request.getParameter("E");
		String strDelete = (String)request.getParameter("D");
		/*if(strEdit != null && uF.parseToInt(strEdit) == 0) {
			String decodeStrEdit = eU.decode(strEdit);
			strEdit = decodeStrEdit;
		}
		if(strDelete != null && uF.parseToInt(strDelete) == 0) {
			String decodeStrDelete = eU.decode(strDelete);
			strDelete = decodeStrDelete;
		}*/
		
		if(strDelete!=null) {
			deleteResignationEntry(uF, strDelete);
			/*if(uF.parseToInt(getEmp_id()) > 0) {
				String encodeEmpId = eU.encode(getEmp_id());
				setEmp_id(encodeEmpId);
			}*/
			return "profile";
		} else if(getStrResignationId()!=null && getStrResignationId().length()>0) {
			updateResignationEntry(uF);
		} else if(getEmpResignationReason()!=null) {
			insertResignationEntry(uF);
		}
		
		getResignPolicyMember(uF,strEdit);
		
//		if(getEmp_id()!=null){
			viewResignationEntry(strEdit, uF);
//		}
		
//	    System.out.println("strResignationId==>"+getStrResignationId()+"==>empId==>"+getEmp_id()+"==>policyId==>"+getPolicy_id());		
		return SUCCESS;
		
	}
	
	private void getResignPolicyMember(UtilityFunctions uF,String strEdit) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		String policy_id=null;
		try {
			
			int nEmpID = uF.parseToInt(strSessionEmpId);
			if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
				nEmpID = uF.parseToInt(getEmp_id());
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
//			System.out.println("nEmpID=====> "+nEmpID);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId=hmEmpLevelMap.get(""+nEmpID);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(""+nEmpID);
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
//			System.out.println("empLevelId=====> "+empLevelId);
			int memberId=0;
			if(uF.parseToInt(strEdit) > 0) {
				pst = con.prepareStatement("select * from work_flow_details where effective_type='"+WORK_FLOW_RESIGN+"' and effective_id=? ");
				pst.setInt(1, uF.parseToInt(strEdit));
			
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					memberId=rs.getInt("emp_id");
				}
				rs.close();
				pst.close();
			}
			
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_RESIGN+"' and level_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(locationID));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				policy_id=rs.getString("policy_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id) == 0){
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(locationID));
				rs = pst.executeQuery();
				while(rs.next()){
					policy_id=rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}
			
			if(uF.parseToInt(policy_id)>0){ 
//				System.out.println("policy_id=====> "+policy_id);
				
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
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
										+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, nEmpID);
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
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
									//sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++){
										List<String> alList=outerList.get(i);
										if(memberId == uF.parseToInt(alList.get(0))) {
											sbComboBox.append("<option value=\""+alList.get(0)+"\" selected>"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}else {
											sbComboBox.append("<option value=\""+alList.get(0)+"\">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}
										
									}
									sbComboBox.append("</select>");								
									
									String optionTr="<tr><td>"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id=? and supervisor_emp_id!=0) as a," +
										"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
										" and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, nEmpID);
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
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
								//	sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++){
										List<String> alList=outerList11.get(i);
										if(memberId == uF.parseToInt(alList.get(0))) {
											sbComboBox11.append("<option value=\""+alList.get(0)+"\" selected>"+alList.get(2)+" "+alList.get(3)+"</option>");
										} else {
											sbComboBox11.append("<option value=\""+alList.get(0)+"\">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}
										
									}
									sbComboBox11.append("</select>");								
									
									String optionTr11="<tr><td>"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, nEmpID);
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
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
								//	sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++){
										List<String> alList=outerList1.get(i);
										if(memberId == uF.parseToInt(alList.get(0))) {
											sbComboBox1.append("<option value=\""+alList.get(0)+"\" selected>"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										} else {
											sbComboBox1.append("<option value=\""+alList.get(0)+"\">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}
										
									}
									sbComboBox1.append("</select>");								
									
									String optionTr1="<tr><td>"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id  and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, nEmpID);
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
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
								//	sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList2.size();i++){
										List<String> alList=outerList2.get(i);
										if(memberId == uF.parseToInt(alList.get(0))) {
											sbComboBox2.append("<option value=\""+alList.get(0)+"\" selected>"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}else {
											sbComboBox2.append("<option value=\""+alList.get(0)+"\" >"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}
									}
									sbComboBox2.append("</select>");								
									
									String optionTr2="<tr><td>"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and "
										+"  epd.emp_per_id=eod.emp_id  and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, nEmpID);
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
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
								//	sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++){
										List<String> alList=outerList3.get(i);
										if(memberId == uF.parseToInt(alList.get(0))) {
											sbComboBox3.append("<option value=\""+alList.get(0)+"\" selected>"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										} else {
											sbComboBox3.append("<option value=\""+alList.get(0)+"\">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}
										
									}
									sbComboBox3.append("</select>");								
									
									String optionTr3="<tr><td>"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id  and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, nEmpID);
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
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
									//sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++){
										List<String> alList=outerList4.get(i);
										if(memberId == uF.parseToInt(alList.get(0))) {
											sbComboBox4.append("<option value=\""+alList.get(0)+"\" selected >"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										} else {
											sbComboBox4.append("<option value=\""+alList.get(0)+"\">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}
									}
									sbComboBox4.append("</select>");								
									
									String optionTr4="<tr><td>"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
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
								pst.setInt(1, nEmpID);
								pst.setInt(2, nEmpID);
								pst.setInt(3, nEmpID);
								pst.setInt(4, nEmpID);								
							
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
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
//									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++){
										List<String> alList=outerList5.get(i);
										if(memberId == uF.parseToInt(alList.get(0))) {
											sbComboBox5.append("<option value=\""+alList.get(0)+"\" selected >"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}else {
											sbComboBox5.append("<option value=\""+alList.get(0)+"\">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
										}
										
									}
									sbComboBox5.append("</select>");								
									
									String optionTr5="<tr><td>"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;			
								
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
									"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
									"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1,nEmpID);
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
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
//								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++){
									List<String> alList=outerHODList.get(i);
									if(memberId == uF.parseToInt(alList.get(0))) {
										sbComboBox11.append("<option value=\""+alList.get(0)+"\" selected >"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}else {
										sbComboBox11.append("<option value=\""+alList.get(0)+"\" >"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><td>"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
								
								hmMemberOption.put(innerList.get(4), optionTr11);
							}
						
							break;
						
						}						
						
					}else if(uF.parseToInt(innerList.get(0))==3){
						int memid=uF.parseToInt(innerList.get(1));
						
						List<List<String>> outerList=new ArrayList<List<String>>();
						pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id " +
								"and se.policy_id = ? and se.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname"); 
						pst.setInt(1,uF.parseToInt(policy_id));
						pst.setInt(2, nEmpID);
						rs = pst.executeQuery();
						while (rs.next()) {
							List<String> alList = new ArrayList<String>();
							alList.add(rs.getString("emp_id"));
							outerList.add(alList);
						}
						rs.close();
						pst.close();
												
						if(outerList!=null && !outerList.isEmpty()){
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control \">");
//							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++){
								List<String> alList=outerList.get(i);
								if(memberId == uF.parseToInt(alList.get(0))) {
									sbComboBox.append("<option value=\""+alList.get(0)+"\" selected>"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");
								}else {
									sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");
								}
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td>Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				String divpopup="";
				StringBuilder sb = new StringBuilder();
//				System.out.println("uF.parseToBoolean(CF.getIsWorkFlow())====>"+uF.parseToBoolean(CF.getIsWorkFlow()));
				if(uF.parseToBoolean(CF.getIsWorkFlow())){		
					 sb.append("<div id=\"popup_name" + nEmpID + "\" class=\"popup_block\">" + 
							   "<h2 class=\"textcolorWhite\">Perk of "+hmEmpCodeName.get(""+nEmpID)+"</h2>" + 
							   "<table>");
										
					 if(hmMemberOption!=null && !hmMemberOption.isEmpty() ){
						 Iterator<String> it1=hmMemberOption.keySet().iterator();
						while(it1.hasNext()){
							String memPosition=it1.next();
							String optiontr=hmMemberOption.get(memPosition);					
							sb.append(optiontr); 
						}
						sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Apply For Loan\" class=\"btn btn-primary\"/></td>" +
								"</tr>");
					 }else{
						 sb.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
					 }
					 sb.append("</table></div>");
					
					divpopup="<input type=\"button\" name=\"submit1\" value=\"Apply For Loan\" class=\"btn btn-primary\"/>";
				}else{
					sb.append("");
					divpopup="<input type=\"submit\" name=\"submit1\" value=\"Apply For Loan\" class=\"btn btn-primary\"/>";
				}
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				/*request.setAttribute("divpopup",divpopup);
				request.setAttribute("loanD", sb.toString());
				request.setAttribute("strEmpID", nEmpID);*/
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadResignationEntry(UtilityFunctions uF) {
		
		setStrResignationId(null);
		setEmpResignationReason(null);
		return LOAD;
	}
	
	public void validate() {
		
	}

	public String insertResignationEntry(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
//			pst = con.prepareStatement("select * from employee_activity_details where emp_id = ? and emp_activity_id =(select max(emp_activity_id) from employee_activity_details )");
			pst = con.prepareStatement("select * from employee_personal_details epd,probation_policy p where epd.emp_per_id = p.emp_id and emp_id = ?");
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			}else{
				pst.setInt(1, uF.parseToInt(getEmp_id()));
			}
			rs = pst.executeQuery();
			int nNoticeDays = 0;
			while(rs.next()){
				System.out.println("RE/744--emp_status==>"+ rs.getString("emp_status"));
				if(rs.getString("emp_status")!=null && !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")){
					nNoticeDays = rs.getInt("notice_duration");
					
				}
			}
			rs.close();
			pst.close();
			
			System.out.println("RE/753--nNoticeDays ===>> " + nNoticeDays);
			
			String empStatus = "TERMINATED";
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id = ?");
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			}else{
				pst.setInt(1, uF.parseToInt(getEmp_id()));
			}
			rs = pst.executeQuery();
			while(rs.next()){
//				System.out.println("resig entry emp_status==>"+ rs.getString("emp_status"));
				if(rs.getString("emp_status")!=null && !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")){
					empStatus = rs.getString("emp_status");
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date,previous_emp_status) values (?,?,?,?,?,?,?)");
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			}else{
				pst.setInt(1, uF.parseToInt(getEmp_id()));
			}
			pst.setString(2, RESIGNED);
			pst.setString(3, getEmpResignationReason());
			pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			
			pst.setInt(5, nNoticeDays);
			pst.setDate(6, uF.getFutureDate(uF.getDateFormat(getEmpResignationDate(), DATE_FORMAT), nNoticeDays));
//			pst.setDate(6, uF.getFutureDate(CF.getStrTimeZone(), nNoticeDays));
			pst.setString(7, empStatus);
			System.out.println("new pst==>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				String empId = strSessionEmpId;
				if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
					empId = getEmp_id();
				}
				
				AddEmployee aE = new AddEmployee();
				aE.request = request;
				aE.session = session;
				aE.CF = CF;
				aE.insertEmpActivity(con, empId, CF, strSessionEmpId, ACTIVITY_RESIGNED_ID);
				
				EmployeeActivity activity = new EmployeeActivity();
				activity.request = request;
				activity.session = session;
				activity.CF = CF;
				activity.processUserStatus(con, uF.parseToInt(ACTIVITY_RESIGNED_ID), uF.parseToInt(empId));
				
//				List<String> hrManagerAndManagerId = new ArrayList<String>();
//				pst = con.prepareStatement("select * from employee_official_details where emp_id = ?");
//				pst.setInt(1, uF.parseToInt(empId));
//				rs = pst.executeQuery();
//				while(rs.next()){
//					hrManagerAndManagerId.add(rs.getString("supervisor_emp_id"));
//					hrManagerAndManagerId.add(rs.getString("emp_hr"));
//				}
//				rs.close();
//				pst.close();
//				
//				for(int i=0; hrManagerAndManagerId!= null && !hrManagerAndManagerId.isEmpty() && i<hrManagerAndManagerId.size(); i++) {
//					if(hrManagerAndManagerId.get(i) != null && !hrManagerAndManagerId.get(i).equals("")) {
//						String strDomain = request.getServerName().split("\\.")[0];
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(hrManagerAndManagerId.get(i));
//						userAlerts.set_type(EMPLOYEE_RESIGNED_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
//					} 
//				}
				
				pst = con.prepareStatement("select max(off_board_id) as off_board_id from emp_off_board");
				rs = pst.executeQuery();
				int nOffBoardId = 0;
				while(rs.next()){
					nOffBoardId = uF.parseToInt(rs.getString("off_board_id"));
				}
				rs.close();
				pst.close();
				
				List<String> alManagers = null;
				if(uF.parseToBoolean(CF.getIsWorkFlow())){
					alManagers = insertApprovalMember(con,pst,rs,nOffBoardId,uF, empId);
				}
				
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; alManagers!=null && i<alManagers.size();i++) {
					Notifications nF = new Notifications(N_EMPLOYEE_RESIGNATION_REQUEST, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(empId);
					nF.setSupervisor(false);
					nF.setEmailTemplate(true);
					
					pst = con.prepareStatement(selectEmpDetails1);
					pst.setInt(1, uF.parseToInt((String)alManagers.get(i)));
					rs = pst.executeQuery();
					boolean flg=false;
					while(rs.next()) {
						nF.setStrSupervisorEmail(rs.getString("emp_email"));					
					
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
						if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("emp_email_sec"));
							nF.setStrEmailTo(rs.getString("emp_email_sec"));
						} else {
							nF.setStrEmpEmail(rs.getString("emp_email"));
							nF.setStrEmailTo(rs.getString("emp_email"));
						}
						flg=true;
					}
					if(flg) {
						nF.setStrResignationDate(uF.getDateFormat(getEmpResignationDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
						nF.setStrResignationReason(getEmpResignationReason());
						nF.sendNotifications();
					}
				}
				
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
	
	private List<String> insertApprovalMember(Connection con, PreparedStatement pst, ResultSet rs, int nOffBoardId, UtilityFunctions uF, String strEmpId) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1,uF.parseToInt(getPolicy_id()));
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
			
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it=hmMemberMap.keySet().iterator();
			while(it.hasNext()){
				String work_flow_member_id=it.next();
				List<String> innerList=hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
//				System.out.println("innerList.get(3)+memid====>"+innerList.get(3)+memid+"=====>"+request.getParameter(innerList.get(3)+memid));
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")) {
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3){
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
//					System.out.println("approval empid====>"+empid);
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,nOffBoardId);
					pst.setString(3,WORK_FLOW_RESIGN);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();
					
					
					String alertData = "<div style=\"float: left;\"> Received a new Exit Request from <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b>. ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String strAction = "EmployeeActivity.action";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
 						strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
					}
					if(userTypeId != uF.parseToInt(hmUserTypeId.get(ADMIN)) && userTypeId != uF.parseToInt(hmUserTypeId.get(HRMANAGER))) {
						strAction = "TeamRequests.action";
						strSubAction = strSubAction + "&callFrom=NotiResignation";
					}
					String alertAction = strAction+"?empType=R&pType=WR"+strSubAction;
					
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
//					userAlerts.set_type(EMPLOYEE_RESIGNED_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
					
					if(!alManagers.contains(empid)){
						alManagers.add(empid);
					}
				}
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alManagers;
	}
	
	public String updateResignationEntry(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String empId = strSessionEmpId;
			if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)){
				empId = getEmp_id();
			}
			
			pst = con.prepareStatement("select * from employee_personal_details epd,probation_policy p where epd.emp_per_id = p.emp_id and emp_id = ?");
			pst.setInt(1, uF.parseToInt(empId));
			rs = pst.executeQuery();
			int nNoticeDays = 0;
			while(rs.next()){
//				System.out.println("RE/1042-- emp_status==>"+ rs.getString("emp_status"));
				if(rs.getString("emp_status")!=null && !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")) {
					nNoticeDays = rs.getInt("notice_duration");
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("update emp_off_board set emp_reason=?, notice_days=?, last_day_date=? where off_board_id=?");
//			pst = con.prepareStatement("update emp_off_board set emp_reason=?, notice_days=?, last_day_date=?, entry_date=? where off_board_id=?");
			pst.setString(1, getEmpResignationReason());
//			System.out.println("RE/1052--getEmpResignationDate="+getEmpResignationDate());
			pst.setInt(2, nNoticeDays);
			pst.setDate(3, uF.getFutureDate(uF.getDateFormat(getEmpResignationDate(), DATE_FORMAT), nNoticeDays));
	//===start parvez date: 25-10-2021===		
//			pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//			pst.setInt(5, uF.parseToInt(getStrResignationId()));
			pst.setInt(4, uF.parseToInt(getStrResignationId()));
	//===end parvez date: 25-10-2021===
			int x = pst.executeUpdate();
//			System.out.println("RE/1057--pst="+pst);
//			System.out.println("RE/1057--x="+x);
			pst.close();
			
			if(x > 0) {
				
			//===start parvez date: 22-11-2022===	
				EmployeeActivity activity = new EmployeeActivity();
				activity.request = request;
				activity.session = session;
				activity.CF = CF;
				activity.processUserStatus(con, uF.parseToInt(ACTIVITY_RESIGNED_ID), uF.parseToInt(empId));
			//===end parvez date: 22-11-2022===	
				
				List<String> alManagers = null;
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
					pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_RESIGN+"'");
					pst.setInt(1, uF.parseToInt(getStrResignationId()));
					pst.execute();
					pst.close();
					alManagers = insertApprovalMember(con,pst,rs,uF.parseToInt(getStrResignationId()),uF, empId);
				}
				
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; alManagers!=null && i<alManagers.size();i++) {
					Notifications nF = new Notifications(N_EMPLOYEE_RESIGNATION_REQUEST, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(empId);
					nF.setSupervisor(false);
					nF.setEmailTemplate(true);
					
					pst = con.prepareStatement(selectEmpDetails1);
					pst.setInt(1, uF.parseToInt((String)alManagers.get(i)));
					rs = pst.executeQuery();
					boolean flg=false;
					while(rs.next()) {
						nF.setStrSupervisorEmail(rs.getString("emp_email"));					
					
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
						if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("emp_email_sec"));
							nF.setStrEmailTo(rs.getString("emp_email_sec"));
						} else {
							nF.setStrEmpEmail(rs.getString("emp_email"));
							nF.setStrEmailTo(rs.getString("emp_email"));
						}
						flg=true;
					}
					if(flg) {
						nF.setStrResignationDate(uF.getDateFormat(getEmpResignationDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
						nF.setStrResignationReason(getEmpResignationReason());
						nF.sendNotifications();
					}
				}
				
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
	
	public String viewResignationEntry(String strEdit, UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
//		EncryptionUtility eU = new EncryptionUtility();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpNamMap = CF.getEmpNameMap(con, null, null);
			
			String empId = strSessionEmpId;
			if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
				empId = getEmp_id();
			}
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id and eod.emp_id=? )) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(empId));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and eod.emp_id=?)) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(empId));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()){
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_off_board where emp_id =? order by entry_date desc");
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			} else {
				pst.setInt(1, uF.parseToInt(getEmp_id()));
			}
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			List alResignationEntry = new ArrayList();
			int count = 0;
			while(rs.next()) {
				List alInner = new ArrayList();
				alInner.add(rs.getString("off_board_id"));
				
				//===start parvez date: 26-10-2021===
				Date strResigDate = uF.getFutureDate(uF.getDateFormat(rs.getString("last_day_date"), DBDATE), -uF.parseToInt(rs.getString("notice_days")));
				//===end parvez date: 26-10-2021===
				
				StringBuilder sb = new StringBuilder();
				if(rs.getString("off_board_type")!=null && !rs.getString("off_board_type").equalsIgnoreCase("TERMINATED")){
				sb.append("Your resignation submitted on "
//				+"<strong>"+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())+"</strong>"
			
			//===start parvez date: 26-10-2021===
				+"<strong>"+uF.getDateFormat(strResigDate+"", DBDATE, CF.getStrReportDateFormat())+"</strong>"
			//===end parvez date: 26-10-2021===
				
				+" \"<i>"+uF.showData(rs.getString("emp_reason"),"Reason not specified.")+"</i>\"");
					if(rs.getInt("approved_1")==0) {
						sb.append(" is <font color=\"orange\">waiting</font> for your manager's approval ");
					} else if(rs.getInt("approved_1")==1) {
						sb.append(" has been <font color=\"green\">approved</font> by your manager ");
					} else if(rs.getInt("approved_1")==-1) {
						sb.append(" has been <font color=\"red\">denied</font> by your manager ");
					}
				if(rs.getInt("approved_1")==0 && rs.getInt("approved_2")==0) {
					sb.append(" <a href=\"ResignationEntry1.action?E="+rs.getString("off_board_id")+"&emp_id="+rs.getString("emp_id")+" \">" +
							"Edit" +
							"</a> ");
					sb.append("<a href=\"ResignationEntry1.action?D="+rs.getString("off_board_id")+"&emp_id="+rs.getString("emp_id")+" \">" +
						 /*	"<img src=\"images1/icons/pullout.png\" title=\"Pull out\" />" +*/
							"<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pull out\" /></i>" +
							"</a>");
				}else if(rs.getInt("approved_1") == 1 && rs.getInt("approved_2")==1){
					sb.append(" <br/><a href=\"ExitForm.action?id="+rs.getString("emp_id")+"&resignId="+rs.getString("off_board_id")+" \">" +
						"Please fill up the exit form to process your full and final." +
						"</a> ");
				}
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("off_board_id"))!=null){
					String approvedby=hmAnyOneApproeBy.get(rs.getString("off_board_id"));
					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("off_board_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("off_board_id"))), "")+")" : "";
					sb.append("&nbsp;&nbsp;(Work Flow-"+hmEmpNamMap.get(approvedby)+strUserTypeName+")");
				} else{
					if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("off_board_id"))!=null){
						sb.append("&nbsp;&nbsp;(<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("off_board_id")+"','"+hmEmpNamMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 1px;\">View Workflow</a>)");
					} else{
						sb.append("");
					}
				}
			} else {
				sb.append("Your service has been terminated on "
						+"<strong>"+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())+"</strong>"
						+" \"<i>"+rs.getString("emp_reason")+"</i>\"");
					
						sb.append(" <br/><a href=\"ExitForm.action?id="+rs.getString("emp_id")+"&resignId="+rs.getString("off_board_id")+" \">" +
						"Please fill up the exit form to process your full and final." +
						"</a> ");
					
			}							
				alInner.add(sb.toString());
				alResignationEntry.add(alInner);
				
				if(strEdit!=null && strEdit.equalsIgnoreCase(rs.getString("off_board_id"))){
					setEmpResignationReason(rs.getString("emp_reason"));
					String strNoticeDays = rs.getString("notice_days");
					System.out.println("RE/1260---last_day_date=="+rs.getString("last_day_date")+"--strNoticeDays="+strNoticeDays);
					Date strResignationDate = uF.getFutureDate(uF.getDateFormat(rs.getString("last_day_date"), DBDATE), -uF.parseToInt(strNoticeDays));
//					System.out.println("RE/1251--strResignationDate ===>> " +strResignationDate);
					setEmpResignationDate(uF.getDateFormat(strResignationDate+"", DBDATE, DATE_FORMAT));
					setStrResignationId(rs.getString("off_board_id"));
					request.setAttribute("DISPLAY_FORM", "");
				} else if(count++==0 && (rs.getInt("approved_1")==-1 || rs.getInt("approved_2")==-1) ) {
					request.setAttribute("DISPLAY_FORM", "");
				}
			}
			rs.close();
			pst.close();
			
			if(getEmpResignationDate() == null || getEmpResignationDate().trim().equals("") || getEmpResignationDate().trim().equalsIgnoreCase("null")) {
				setEmpResignationDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			}
			request.setAttribute("alResignationEntry", alResignationEntry);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteResignationEntry(UtilityFunctions uF, String strDelete) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from emp_off_board where off_board_id=?");
			pst.setInt(1, uF.parseToInt(strDelete));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, SUCCESSM+"You have pulled out your resignation successfully!"+END);
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
				AddEmployee aE = new AddEmployee();
				aE.request = request;
				aE.session = session;
				aE.CF = CF;
				aE.insertEmpActivity(con, strSessionEmpId, CF, strSessionEmpId, ACTIVITY_RESIGNATION_WITHDRWAL_ID);
				
				EmployeeActivity activity = new EmployeeActivity();
				activity.request = request;
				activity.session = session;
				activity.CF = CF;
				activity.processUserStatus(con, uF.parseToInt(ACTIVITY_RESIGNATION_WITHDRWAL_ID), uF.parseToInt(strSessionEmpId));
			}else{
				AddEmployee aE = new AddEmployee();
				aE.request = request;
				aE.session = session;
				aE.CF = CF;
				aE.insertEmpActivity(con, getEmp_id(), CF, strSessionEmpId, ACTIVITY_RESIGNATION_WITHDRWAL_ID);
				
				EmployeeActivity activity = new EmployeeActivity();
				activity.request = request;
				activity.session = session;
				activity.CF = CF;
				activity.processUserStatus(con, uF.parseToInt(ACTIVITY_RESIGNATION_WITHDRWAL_ID), uF.parseToInt(getEmp_id()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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

	public String getEmpResignationReason() {
		return empResignationReason;
	}

	public void setEmpResignationReason(String empResignationReason) {
		this.empResignationReason = empResignationReason;
	}

	public String getStrResignationId() {
		return strResignationId;
	}

	public void setStrResignationId(String strResignationId) {
		this.strResignationId = strResignationId;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public String getEmpResignationDate() {
		return empResignationDate;
	}

	public void setEmpResignationDate(String empResignationDate) {
		this.empResignationDate = empResignationDate;
	}
	
}