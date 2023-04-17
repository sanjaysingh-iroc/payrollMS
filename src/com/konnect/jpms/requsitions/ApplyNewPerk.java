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

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillPerkType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApplyNewPerk extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null; 
	
	private CommonFunctions CF; 
	
	private String strId;
	private String strPurpose;
	private String strAmount;
	private String strType;
	private File strDocument;
	private String strDocumentContentType;
	private String strDocumentFileName;
	
	private String strViewDocument;
	private List<FillPerkType> typeList;
	
	private String policy_id;
	
	private String strSelectedEmpId;
	private String financialYear;
	private List<FillFinancialYears> financialYearList;
	
	private String strMonth;
	private List<FillMonth> monthList;
	
	private String operation;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		
		if(uF.parseToInt(getStrMonth()) == 0){
			setStrMonth(""+uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM")));
		}
		
//		System.out.println("getStrId() ===>> " + getStrId());
		
		if(getStrId()!=null && getStrId().length()>0) {
			if(getOperation() != null && getOperation().equals("E")) {
				viewPerk(uF);
			} else if(getOperation() != null && getOperation().equals("U")) {
				return updatePerk(uF);
			}
			
		} else if(getStrAmount()!=null) {
			return addPerk(uF); 
		}
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE) ) {
			getPerkPolicyMember(uF);
		}
//		System.out.println("loadPerks ===>> ");
		
		return loadPerks(uF);
	}
	
	public String loadPerks(UtilityFunctions uF) {
		Connection con= null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			} else {
				strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			con = db.makeConnection(con);
			String empLevel = CF.getEmpLevelId(con, (String)session.getAttribute(EMPID));
			
			
			typeList = new FillPerkType(request).fillPerkType(uF.parseToInt(empLevel), strFinancialYearStart, strFinancialYearEnd);
			
			if(getStrId()==null) {
				setStrPurpose(null);
				setStrAmount(null);
				setStrType(null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	private void getPerkPolicyMember(UtilityFunctions uF) {
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
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_PERK+"' and level_id=? and wlocation_id=?");
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
									
									String optionTr="<tr><td >"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
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
									
									String optionTr11="<tr><td >"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
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
									
									String optionTr1="<tr><td >"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
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
									
									String optionTr2="<tr><td >"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
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
									
									String optionTr3="<tr><td >"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
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
									
									String optionTr4="<tr><td >"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
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
									
									String optionTr5="<tr><td >"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
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
							
							String optionTr="<tr><td >Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				String divpopup="";
				StringBuilder sb = new StringBuilder();
//				System.out.println("uF.parseToBoolean(CF.getIsWorkFlow())====>"+uF.parseToBoolean(CF.getIsWorkFlow()));
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {		
					 sb.append("<div id=\"popup_name" + strEmpID + "\" class=\"popup_block\">" + 
							   "<h2 class=\"textcolorWhite\">Perk of "+hmEmpCodeName.get(""+strEmpID)+"</h2>" + 
							   "<table>");
										
					 if(hmMemberOption!=null && !hmMemberOption.isEmpty() ) {
						 Iterator<String> it1=hmMemberOption.keySet().iterator();
						while(it1.hasNext()) {
							String memPosition=it1.next();
							String optiontr=hmMemberOption.get(memPosition);					
							sb.append(optiontr); 
						}
						sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"input_button\" onclick=\"return checkPerkLimit();\"/></td>" +
								"</tr>");
					 } else {
						 sb.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
					 }
					 sb.append("</table></div>");
					
					divpopup="<input type=\"button\" name=\"submit1\" value=\"Submit\" class=\"input_button\" onclick=\"return checkPerkLimit();\"/>";
				} else {
					sb.append("");
					divpopup="<input type=\"submit\" name=\"submit1\" value=\"Submit\" class=\"input_button\" onclick=\"return checkPerkLimit();\"/>";
				}
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("divpopup",divpopup);
				request.setAttribute("perksD", sb.toString());
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
	
	
	public String viewPerk(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from emp_perks where perks_id=?");
			pst.setInt(1, uF.parseToInt(getStrId()));
			rs = pst.executeQuery();
			if(rs.next()) {
				setFinancialYear(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT) + "-" + uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT));
				setStrType(rs.getString("perk_type_id"));
				setStrPurpose(rs.getString("perk_purpose"));
				setStrAmount(rs.getString("perk_amount"));
				setStrDocument(strDocument);
				
				File strdoc = new File(request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document"));
				
//				System.out.println("Path===>"+strdoc.getAbsolutePath());
				setStrDocument(strdoc);
//				System.out.println("===>"+getStrDocument());
				StringBuilder sbDoc = new StringBuilder();
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>0) {
					if(CF.getStrDocRetriveLocation()==null) {
						sbDoc.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_PERKS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				setStrViewDocument(sbDoc.toString());
				
				setStrMonth(rs.getString("perk_month"));
				setOperation("U");
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
	
	public String addPerk(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
				String fileName =null;
				if(getStrDocument()!=null) {
//					if(CF.getStrDocSaveLocation()==null) {
//						fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), CF.getIsRemoteLocation(), CF);
//					} else {
//						fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getStrDocument(), getStrDocumentFileName(), CF.getIsRemoteLocation(), CF);
//					}
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
					} else {
						fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PERKS+"/"+I_DOCUMENT+"/"+strSessionEmpId, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
					} 
				}
//				System.out.println("fileName===>"+fileName);
				con = db.makeConnection(con);
				pst = con.prepareStatement("insert into emp_perks (financial_year_start, financial_year_end, perk_type_id, perk_purpose, perk_amount, emp_id, entry_date, ref_document,perk_month) values (?,?,?,?, ?,?,?,?, ?);");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrType()));
				pst.setString(4, getStrPurpose());
				pst.setDouble(5, uF.parseToDouble(getStrAmount()));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(8, fileName);
				pst.setInt(9, uF.parseToInt(getStrMonth()));
//				System.out.println("Pst===>"+pst);
				int x = pst.executeUpdate();
				pst.close();
				
				if(x>0) {
					String perks_id=null;
					pst = con.prepareStatement("select max(perks_id)as perks_id from emp_perks");
					rs=pst.executeQuery();
					while(rs.next()) {
						perks_id=rs.getString("perks_id");
					}
					rs.close();
					pst.close();
					List<String> alManagers = null;
					if(uF.parseToBoolean(CF.getIsWorkFlow())) {
						alManagers = insertLeaveApprovalMember(con,pst,rs,perks_id,uF);
					}
					session.setAttribute(MESSAGE, SUCCESSM+"Perks Applied Successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Perks Applied Failed."+END);
				}
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Perks applied failed."+END);
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;
		
	}
	
	private List<String> insertLeaveApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, String perks_id, UtilityFunctions uF) {
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
					pst.setInt(2,uF.parseToInt(perks_id));
					pst.setString(3,WORK_FLOW_PERK);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();
					
					String alertData = "<div style=\"float: left;\"> Received a new Perk Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> amount "+uF.formatIntoTwoDecimal(uF.parseToDouble(getStrAmount()))+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyPerk"+strSubAction;
					} else {
						alertAction = "PayApprovals.action?pType=WR&callFrom=NotiApplyPerk"+strSubAction;
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
//					userAlerts.set_type(PERK_REQUEST_ALERT);
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


	public String updatePerk(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
				String fileName =null;
				if(getStrDocument()!=null) {
				
//					if(CF.getStrDocSaveLocation()==null) {
//						fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), CF.getIsRemoteLocation(), CF);
//					} else {
//						fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getStrDocument(), getStrDocumentFileName(), CF.getIsRemoteLocation(), CF);
//					}
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
					} else {
						fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PERKS+"/"+I_DOCUMENT+"/"+strSessionEmpId, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
					} 
				}
				con = db.makeConnection(con);
				pst = con.prepareStatement("update emp_perks set financial_year_start=?, financial_year_end=?, perk_type_id=?, perk_purpose=?, perk_amount=?,perk_month=? where perks_id=?");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrType()));
				pst.setString(4, getStrPurpose());
				pst.setDouble(5, uF.parseToDouble(getStrAmount()));
				pst.setInt(6, uF.parseToInt(getStrMonth()));
				pst.setInt(7, uF.parseToInt(getStrId()));
				pst.execute();
				pst.close();
				
				if(getStrDocument()!=null) {
					pst = con.prepareStatement("update emp_perks set ref_document=? where perks_id=?");
					pst.setString(1, fileName);
					pst.setInt(2, uF.parseToInt(getStrId()));
					pst.execute();
					pst.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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

	public List<FillPerkType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<FillPerkType> typeList) {
		this.typeList = typeList;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
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

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
