package com.konnect.jpms.loan;

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

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillLoanCode;
import com.konnect.jpms.select.FillNumbers;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LoanApplicationForm extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	
	HttpSession session;
	String strUserType;
	String strBaseUserType;
	String strSessionEmpId; 
	private static Logger log = Logger.getLogger(LoanApplicationForm.class);
	
	private String strLoanCode;
	private String loanDesc;
	private String loanDuration;
	private String loanAmount;
	
	private String f_strWLocation;
	private String f_department;
	private String f_level;
	private String strEmpId;
	
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillEmployee> empList;
	private List<FillLoanCode> loanList;
	private List<FillNumbers> durationList;
	
	private List<FillOrganisation> orgList;
	private String f_org;
	
	private String policy_id;
	
	private String currUserType;
	
	private String effectiveDate;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PLoanApplicationForm); 
		request.setAttribute(TITLE, "Loan Application Form");
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));  
		}
		
//		if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))){
//			setStrEmpId((String)session.getAttribute(EMPID));
//		}
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			setStrEmpId(strSessionEmpId);
		}
		
//		loanList = new FillLoanCode(request).fillLoanCode();
		
		boolean isEmpLoanAutoApprove = CF.getFeatureManagementStatus(request, uF, F_EMP_LOAN_AUTO_APPROVE);
		request.setAttribute("isEmpLoanAutoApprove", ""+isEmpLoanAutoApprove);
		
//		System.out.println("getStrLoanCode() ===>> " + getStrLoanCode());
		if(getStrLoanCode()!=null) {
//			System.out.println("if getStrLoanCode() ===>> " + getStrLoanCode());
			return addEmployeeLoan(uF,isEmpLoanAutoApprove);
		}
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE) && !isEmpLoanAutoApprove){
			getLoanPolicyMember(uF);
		}
		
		return loadLoanPolicy(uF);
	}
	
	private void getLoanPolicyMember(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		String policy_id=null;
		try {
			
			int nEmpID = uF.parseToInt(getStrEmpId());
			
			con = db.makeConnection(con);
			
//			System.out.println("nEmpID=====> "+nEmpID);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId=hmEmpLevelMap.get(""+nEmpID);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(""+nEmpID);
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
//			System.out.println("empLevelId=====> "+empLevelId);
			
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_LOAN+"' and level_id=? and wlocation_id=?");
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
				System.out.println("==pst"+pst);
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
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
				boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
				
				
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()){
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1){
						int memid=uF.parseToInt(innerList.get(1));
						
						switch(memid) {
						
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE' and epd.emp_per_id=eod.emp_id " 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, nEmpID);
								System.out.println("==>"+pst);
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
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
									sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++){
										List<String> alList=outerList.get(i);
										sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox.append("</select>");								
									
									String optionTr="<tr><td valign=\"top\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
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
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
									sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++){
										List<String> alList=outerList11.get(i);
										sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox11.append("</select>");								
									
									String optionTr11="<tr><td valign=\"top\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and and epd.emp_per_id=eod.emp_id "
												+ " ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
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
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++){
										List<String> alList=outerList1.get(i);
										sbComboBox1.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox1.append("</select>");								
									
									String optionTr1="<tr><td valign=\"top\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " 
										+ " and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
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
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"4</option>");
									for(int i=0;i<outerList2.size();i++){
										List<String> alList=outerList2.get(i);
										sbComboBox2.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox2.append("</select>");								
									
									String optionTr2="<tr><td valign=\"top\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
										+ " and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
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
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++){
										List<String> alList=outerList3.get(i);
										sbComboBox3.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox3.append("</select>");								
									
									String optionTr3="<tr><td valign=\"top\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id "
										+ " and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
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
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++){
										List<String> alList=outerList4.get(i);
										sbComboBox4.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox4.append("</select>");								
									
									String optionTr4="<tr><td valign=\"top\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
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
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++){
										List<String> alList=outerList5.get(i);
										sbComboBox5.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox5.append("</select>");								
									
									String optionTr5="<tr><td valign=\"top\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;							
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
									"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
									"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, nEmpID);
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
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++){
									List<String> alList=outerHODList.get(i);
									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><td valign=\"top\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
								
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
						
						
						if(outerList!=null && !outerList.isEmpty()) {
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired form-control\">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++) {
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td valign=\"top\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				String divpopup="";
				StringBuilder sb = new StringBuilder();
//				System.out.println("uF.parseToBoolean(CF.getIsWorkFlow())====>"+uF.parseToBoolean(CF.getIsWorkFlow()));
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {		
					 sb.append("<div id=\"popup_name" + nEmpID + "\" class=\"popup_block\">" + 
							   "<h2 class=\"textcolorWhite\">Perk of "+hmEmpCodeName.get(""+nEmpID)+"</h2>" + 
							   "<table>");
										
					 if(hmMemberOption!=null && !hmMemberOption.isEmpty() ) {
						 Iterator<String> it1=hmMemberOption.keySet().iterator();
						while(it1.hasNext()) {
							String memPosition=it1.next();
							String optiontr=hmMemberOption.get(memPosition);					
							sb.append(optiontr); 
						}
						sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Apply For Loan\" class=\"input_button\"/></td>" +
								"</tr>");
					 } else {
						 sb.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
					 }
					 sb.append("</table></div>");
					
					divpopup="<input type=\"button\" name=\"submit1\" value=\"Apply For Loan\" class=\"input_button\"/>";
				} else {
					sb.append("");
					divpopup="<input type=\"submit\" name=\"submit1\" value=\"Apply For Loan\" class=\"input_button\"/>";
				}
				request.setAttribute("hmMemberOption", hmMemberOption);
				request.setAttribute("policy_id", policy_id);
				System.out.println("policy_id ===>> " + policy_id);
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

	public String loadLoanPolicy(UtilityFunctions uF){
		
		durationList = new FillNumbers().fillNumbers(1, 60);
//		loanList = new FillLoanCode(request).fillLoanCode();
		loanList = new FillLoanCode(request).fillLoanCode(uF.parseToInt(getStrEmpId()),CF);
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		empList=getEmployeeList(uF);
		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
		try {
			
		
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
					
//					con = db.makeConnection(con);
//					
//					pst = con.prepareStatement(selectLoanApplied2);
//					pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//					ResultSet rs = pst.executeQuery();
					
					boolean isCurrentLoan = false;
//					while(rs.next()){
//						isCurrentLoan = true;
//					}
					
					request.setAttribute("isCurrentLoan", isCurrentLoan+"");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
//			db.closeConnection(con);
		}
		
		return LOAD;
	}
	
	
	
	private List<FillEmployee> getEmployeeList(UtilityFunctions uF) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive=true ");
//			System.out.println("strUserType ===>>>> " + strUserType);
//			System.out.println("currUserType ===>>>> " + currUserType);
			if(strUserType != null && strUserType.equals(MANAGER) && (currUserType == null || !currUserType.equals(strBaseUserType))) {
				sbQuery.append(" and eod.supervisor_emp_id="+uF.parseToInt(strSessionEmpId));
			} else {
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
				}
				if(uF.parseToInt(getF_strWLocation())>0) {
					sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
				}
				if(uF.parseToInt(getF_department())>0) {
					sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
				}
				if(uF.parseToInt(getF_level())>0) {
					sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
				}
			}
			sbQuery.append(" order by epd.emp_fname");
			
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("==>emppst"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String addEmployeeLoan(UtilityFunctions uF, boolean isEmpLoanAutoApprove){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			if(getStrLoanCode()!=null) {
				con = db.makeConnection(con);
				
//				pst = con.prepareStatement(selectEmpSalaryDetails1);
//				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				pst.setInt(2, uF.parseToInt(getStrEmpId()));
//				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
//				double dblTotalEarning = 0;
//				while(rs.next()) {
//					dblTotalEarning += rs.getDouble("amount");
//				}
//				rs.close();
//				pst.close();
				
//				pst = con.prepareStatement("select * from loan_details where loan_id =?");
//				pst = con.prepareStatement(selectLoanDetails1);
//				pst.setInt(1, uF.parseToInt(getStrLoanCode()));
//				rs = pst.executeQuery();
//				double dblTimes = 0;
//				
//				while(rs.next()) {
//					dblTimes = rs.getDouble("times_salary");
//				}
//				rs.close();
//				pst.close();

				if(isEmpLoanAutoApprove){
					double dblPrincipalAmt = uF.parseToDouble(getLoanAmount());
    				double dblROI = 0;
    				double dblDuration = uF.parseToDouble(getLoanDuration());
					pst = con.prepareStatement("select * from loan_details where loan_id=?");
    				pst.setInt(1, uF.parseToInt(getStrLoanCode()));
    				rs = pst.executeQuery();    				
    				while(rs.next()){
    					dblROI = rs.getDouble("loan_interest");
    				}
    				rs.close();
    				pst.close();
					
    				double dblEMI = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
    				
					pst = con.prepareStatement("insert into loan_applied_details (loan_id,emp_id,applied_by," +
							"applied_date,loan_desc,duration_months,amount_paid,approved_by,approved_date," +
							"is_approved,balance_amount,loan_emi,effective_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(getStrLoanCode()));
					pst.setInt(2, uF.parseToInt(getStrEmpId()));
					pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(5, getLoanDesc());
					pst.setInt(6, uF.parseToInt(getLoanDuration()));
					pst.setDouble(7, uF.parseToDouble(getLoanAmount()));
					pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(10, 1);
					pst.setDouble(11, dblEMI);
					pst.setDouble(12, dblEMI);
					pst.setDate(13, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
//					System.out.println("1 pst ===>> " + pst);
					pst.execute();
					pst.close();
				} else {
					pst = con.prepareStatement(insertLoanAppliedDetails);
					pst.setInt(1, uF.parseToInt(getStrLoanCode()));
					pst.setInt(2, uF.parseToInt(getStrEmpId()));
					pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(5, getLoanDesc());
					pst.setInt(6, uF.parseToInt(getLoanDuration()));
	//				pst.setDouble(7, (dblTotalEarning * dblTimes));
					pst.setDouble(7, uF.parseToDouble(getLoanAmount()));
					pst.setDate(8, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
//					System.out.println("2 pst ===>> " + pst);
					int x = pst.executeUpdate();
					pst.close();
					
					if(x > 0) {
						pst = con.prepareStatement("select max(loan_applied_id) as loan_applied_id from loan_applied_details");
						rs = pst.executeQuery();
						int nLoanId = 0;
						while(rs.next()){
							nLoanId = uF.parseToInt(rs.getString("loan_applied_id"));
						}
						
						List<String> alManagers = null;
						if(uF.parseToBoolean(CF.getIsWorkFlow())){
							alManagers = insertApprovalMember(con,pst,rs,nLoanId,uF);
						}
					}
				}
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
	
	
	
	private List<String> insertApprovalMember(Connection con, PreparedStatement pst, ResultSet rs, int nLoanId, UtilityFunctions uF) {
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
				
				if(empid!=null && !empid.equals("")){
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3){
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
//					System.out.println("approval empid====>"+empid);
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,nLoanId);
					pst.setString(3,WORK_FLOW_LOAN);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();
					
					String alertData = "<div style=\"float: left;\"> Received a new Loan Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> amount "+uF.formatIntoTwoDecimal(uF.parseToDouble(getLoanAmount()))+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyLoan"+strSubAction;
					} else {
						alertAction = "PayApprovals.action?pType=WR&callFrom=NotiApplyLoan"+strSubAction;
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
	
	

	public void validate(){
		
		
		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		request.setAttribute(PAGE, PLoanApplicationForm);
		request.setAttribute(TITLE, "Loan Application Form");
		strUserType = (String)session.getAttribute(USERTYPE);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			setStrEmpId((String)session.getAttribute(EMPID));
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();    
		db.setRequest(request);
		double dblTotalLoan = 0;
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
		
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement(selectEmpSalaryDetails1);
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			double dblTotalEarning = 0;
			
			while(rs.next()){
				dblTotalEarning += rs.getDouble("amount");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(selectLoanDetails1);
			pst.setInt(1, uF.parseToInt(getStrLoanCode()));
			rs = pst.executeQuery();
			while(rs.next()){
				dblTotalLoan = dblTotalEarning * rs.getDouble("times_salary");
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
		
		
		if(uF.parseToDouble(getLoanAmount())>dblTotalLoan){
			addFieldError("loanAmount", " You can not apply for more than specified amount ("+dblTotalLoan+").");
		}
		
		loadLoanPolicy(uF);
		
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
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

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<FillLoanCode> getLoanList() {
		return loanList;
	}

	public void setLoanList(List<FillLoanCode> loanList) {
		this.loanList = loanList;
	}

	public String getStrLoanCode() {
		return strLoanCode;
	}

	public void setStrLoanCode(String strLoanCode) {
		this.strLoanCode = strLoanCode;
	}

	public String getLoanDesc() {
		return loanDesc;
	}

	public void setLoanDesc(String loanDesc) {
		this.loanDesc = loanDesc;
	}

	public String getLoanDuration() {
		return loanDuration;
	}

	public void setLoanDuration(String loanDuration) {
		this.loanDuration = loanDuration;
	}

	public String getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	public List<FillNumbers> getDurationList() {
		return durationList;
	}

	public void setDurationList(List<FillNumbers> durationList) {
		this.durationList = durationList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

}