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

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillLodgingType;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillRimbursementType;
import com.konnect.jpms.select.FillTravel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.task.FillProjectList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BulkReimbursements extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null; 
	String strSessionEmpId = null;  
 
	private CommonFunctions CF;
	 
	
	List<FillLevel> levelList;  
//	List<FillEmployee> empNamesList;
//	List<FillEmployee> empNamesList1;
	List<FillWLocation> wLocationList;
	List<FillOrganisation> organisationList;

	String policy_id;
	
	String paycycle;
	List<FillPayCycles> paycycleList;
	
	List<FillRimbursementType> reimbursementTypeList;
	List<FillRimbursementType> modeoftravelList;
	List<FillRimbursementType> typeList;

	List<FillTravel> travelPlanList;
	List<FillProjectList> projectList;
	List<FillClients> clientList;
	String f_org;
	
	String reimbursementType;
	String reimType;
	
	String count;
	
	File[] strDocument;
	String[] strDocumentFileName;
	
	String submit;
	
	List<FillLodgingType> lodgingTypeList;	
	List<FillRimbursementType> localConveyanceTranTypeList;	

	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/requisitions/BulkReimbursements.jsp");
		request.setAttribute(TITLE, "Bulk Claims & Reimbursements");
		
		
		boolean isView = CF.getAccess(session, request, uF);
		if (!isView) {
			request.setAttribute(PAGE, PAccessDenied); 
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}

		if (getSubmit()!=null) {
			addReimbursement();
			return SUCCESS;
		} 
		getEmpProjectDetails(uF);
		getReimbursementsPolicyMember(uF);
		
//		System.out.println("paycycle====>"+getPaycycle());

		return loadReimbursements(uF);
	}
	
	
	
	private void getEmpProjectDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			int nEmpId=0;
//			if (strUserType != null && strUserType.equals(HRMANAGER)) {
//				nEmpId=uF.parseToInt(getStrSelectedEmpId1());
//			} else {
				nEmpId=uF.parseToInt(strSessionEmpId);
//			}
			con = db.makeConnection(con);
			
			String strEmpOrgId = CF.getEmpOrgId(con, uF, ""+nEmpId);
			String strEmpLevelId = CF.getEmpLevelId(con, ""+nEmpId);
			
//			String[] arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);

			String []arrDates = null;
			if (getPaycycle() != null) {
				arrDates = getPaycycle().split("-");
			} else {
				arrDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
			}
			
			
			pst = con.prepareStatement("select * from activity_info where resource_ids like '%,"+nEmpId+",%' and pro_id in (select pro_id from projectmntnc where " +
				"(start_date, deadline) overlaps (to_date(?::text, 'YYYY-MM-DD') ,to_date(?::text, 'YYYY-MM-DD')))");
			pst.setDate(1, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(arrDates[1], DATE_FORMAT));
//			System.out.println("aa pst====>"+pst);
			rs = pst.executeQuery();
			boolean isProject=false;
			while(rs.next()) {
				isProject=true;
			}
			
			/**
			 * Local Policy 
			 * */
			Map<String, String> hmLocalType = CF.getLocalType(uF);
			Map<String, String> hmLimitType = CF.getLimitType(uF);
			pst = con.prepareStatement("select * from reimbursement_policy where local_limit_type=2 and reimbursement_policy_type=? and level_id=? and org_id=?");
			pst.setInt(1, REIMBURSEMENTS_LOCAL);
			pst.setInt(2, uF.parseToInt(strEmpLevelId));
			pst.setInt(3, uF.parseToInt(strEmpOrgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alLocal = new ArrayList<Map<String,String>>(); 
			while(rs.next()) {
				Map<String, String> hmLocal = new HashMap<String, String>();
				hmLocal.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmLocal.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmLocal.put("REIMBURSEMENT_IS_LOCAL_POLICY", rs.getString("is_default_policy"));
				hmLocal.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmLocal.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				
				hmLocal.put("REIMBURSEMENT_LOCAL_TYPE_ID", rs.getString("local_type"));
				hmLocal.put("REIMBURSEMENT_LOCAL_TYPE", uF.showData(hmLocalType.get(rs.getString("local_type")),""));
				hmLocal.put("REIMBURSEMENT_TRANSPORT_TYPE", rs.getString("transport_type"));
				hmLocal.put("REIMBURSEMENT_LOCAL_LIMIT_TYPE_ID", rs.getString("local_limit_type"));
				hmLocal.put("REIMBURSEMENT_LOCAL_LIMIT_TYPE", uF.showData(hmLimitType.get(rs.getString("local_limit_type")),""));
				hmLocal.put("REIMBURSEMENT_LOCAL_LIMIT", ""+uF.parseToDouble(rs.getString("local_limit")));
				hmLocal.put("REIMBURSEMENT_IS_REQUIRE_POLICY", rs.getString("is_require_approval"));
				hmLocal.put("REIMBURSEMENT_MIN_AMOUNT", ""+uF.parseToDouble(rs.getString("min_amount")));
				hmLocal.put("REIMBURSEMENT_MAX_AMOUNT", ""+uF.parseToDouble(rs.getString("max_amount")));
				
				alLocal.add(hmLocal);
			}
			rs.close();
			pst.close();
			request.setAttribute("alLocal", alLocal);
			
			/**
			 * Mobile Bill Policy
			 * */
			pst = con.prepareStatement("select * from reimbursement_policy where mobile_limit_type=2 and reimbursement_policy_type=? and level_id=? and org_id=?");
			pst.setInt(1, REIMBURSEMENTS_MOBILE_BILL);
			pst.setInt(2, uF.parseToInt(strEmpLevelId));
			pst.setInt(3, uF.parseToInt(strEmpOrgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alMobileBill = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmMobileInner = new HashMap<String, String>();
				hmMobileInner.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmMobileInner.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmMobileInner.put("REIMBURSEMENT_IS_MOBILE_POLICY", rs.getString("is_default_policy"));
				hmMobileInner.put("REIMBURSEMENT_MOBILE_LIMIT_TYPE", rs.getString("mobile_limit_type"));
				hmMobileInner.put("REIMBURSEMENT_MOBILE_LIMIT", rs.getString("mobile_limit"));
				hmMobileInner.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmMobileInner.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				
				alMobileBill.add(hmMobileInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("alMobileBill", alMobileBill);
//			System.out.println("alMobileBill====>"+alMobileBill);
			
			/**
			 * Claim Policy 
			 * */
			pst = con.prepareStatement("select * from reimbursement_policy where org_id=? and reimbursement_policy_type=? and level_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, REIMBURSEMENTS_CLAIM);
			pst.setInt(3, uF.parseToInt(strEmpLevelId));
			rs = pst.executeQuery();
			List<Map<String, String>> alClaim = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				
				Map<String, String> hmClaimInner = new HashMap<String, String>();
				hmClaimInner.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmClaimInner.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmClaimInner.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmClaimInner.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				hmClaimInner.put("REIMBURSEMENT_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_TYPE_ID", rs.getString("travel_transport_type"));
				
				hmClaimInner.put("REIMBURSEMENT_TRAIN_TYPE_ID", rs.getString("train_type"));
				
				hmClaimInner.put("REIMBURSEMENT_BUS_TYPE_ID", rs.getString("bus_type"));
				
				hmClaimInner.put("REIMBURSEMENT_FLIGHT_TYPE_ID", rs.getString("flight_type"));
				
				hmClaimInner.put("REIMBURSEMENT_CAR_TYPE_ID", rs.getString("car_type"));
				
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_LIMIT_TYPE_ID", rs.getString("travel_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_LIMIT", ""+uF.parseToDouble(rs.getString("travel_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LODGING_TYPE_ID", rs.getString("lodging_type"));
				hmClaimInner.put("REIMBURSEMENT_LODGING_LIMIT_TYPE_ID", rs.getString("lodging_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_LODGING_LIMIT", ""+uF.parseToDouble(rs.getString("lodging_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LOCAL_CONVEYANCE_TRAN_ID", rs.getString("local_conveyance_tran_type"));
				hmClaimInner.put("REIMBURSEMENT_LOCAL_CONVEYANCE_LIMIT", ""+uF.parseToDouble(rs.getString("local_conveyance_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_FOOD_LIMIT_TYPE_ID", rs.getString("food_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_FOOD_LIMIT", ""+uF.parseToDouble(rs.getString("food_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LAUNDRY_LIMIT_TYPE_ID", rs.getString("laundry_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_LAUNDRY_LIMIT", ""+uF.parseToDouble(rs.getString("laundry_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_SUNDRY_LIMIT_TYPE_ID", rs.getString("sundry_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_SUNDRY_LIMIT", ""+uF.parseToDouble(rs.getString("sundry_limit")));
				
				alClaim.add(hmClaimInner);				
			}
			rs.close();
			pst.close();
			request.setAttribute("alClaim", alClaim);
//			System.out.println("alClaim=====>"+alClaim);
			
			reimbursementTypeList = new FillRimbursementType().fillRimbursementType1();
			if(getReimType()==null || getReimType().trim().equals("") || getReimType().trim().equalsIgnoreCase("")) { 
				if(isProject){
					setReimType("P");
				} else {
					setReimType("L");
				}
			}
			
			request.setAttribute("isProject", isProject);
			
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}
	private void getReimbursementsPolicyMember(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		String policy_id=null;
		try {
			
			int strEmpID=0;
//			if (strUserType != null && strUserType.equals(HRMANAGER)) {
//				strEmpID=uF.parseToInt(getStrSelectedEmpId1());
//			} else {
				strEmpID=uF.parseToInt(strSessionEmpId);
//			}
			
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
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_REIMBURSEMENTS+"' and level_id=? and wlocation_id=?");
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
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname, epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
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
									
									String optionTr="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id="+strEmpID+" and supervisor_emp_id!=0) as a," 
										+ "employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
										+ " and epd.is_alive=true  order by epd.emp_fname");
								rs = pst.executeQuery();
								List<List<String>> outerList11=new ArrayList<List<String>>();
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
									
									String optionTr11="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
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
									
									String optionTr1="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
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
									
									String optionTr2="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
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
									
									String optionTr3="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
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
									
									String optionTr4="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
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
//									alList.add(rs.getString("usertype_id"));
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
									
									String optionTr5="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
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
								sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight textcolorWhite\">Your workflow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				/*String divpopup="";
				StringBuilder sb = new StringBuilder();
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {		
					 sb.append("<div id=\"popup_name" + strEmpID + "\" class=\"popup_block\">" + 
							   "<h2 class=\"textcolorWhite\">Reimbursements of "+hmEmpCodeName.get(""+strEmpID)+"</h2>" + 
							   "<table>"); 
										
					 if(hmMemberOption!=null && !hmMemberOption.isEmpty() ) {
						 Iterator<String> it1=hmMemberOption.keySet().iterator();
						while(it1.hasNext()) {
							String memPosition=it1.next();
							String optiontr=hmMemberOption.get(memPosition);					
							sb.append(optiontr); 
						}
						sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"input_button\"/></td>" +
								"</tr>");
					 } else {
						 sb.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
					 }
					 sb.append("</table></div>");
					
					divpopup="<input type=\"button\" name=\"submit1\" value=\"Submit\" class=\"input_button\"/>";
				} else {
					sb.append("");
					divpopup="<input type=\"submit\" name=\"submit1\" value=\"Submit\" class=\"input_button\"/>";
				}
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("divpopup",divpopup);
				request.setAttribute("reimbursementsD", sb.toString());
				request.setAttribute("strEmpID", strEmpID);*/
				
				
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("strEmpID", strEmpID);
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}

	public String loadReimbursements(UtilityFunctions uF) {

		String[] arr = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
		paycycleList = new FillPayCycles(request).fillNoofPayCycles(CF,getF_org(),(uF.parseToInt(arr[2])-1));
		
		typeList = new FillRimbursementType().fillRimbursementType();
		

		travelPlanList = new FillTravel(request).fillTravelPlan(uF.parseToInt((String) session.getAttribute(EMPID)));
		
		if(strUserType != null && strUserType.equals(EMPLOYEE)) {
			projectList = new FillProjectList(request).fillProjectDetailsByEmp(uF.parseToInt((String) session.getAttribute(EMPID)), false, 0);
		} else {
			projectList = new FillProjectList(request).fillProjectDetailsByEmp(0, false, 0);
		}
		
		clientList = new FillClients(request).fillClients(uF.parseToInt((String) session.getAttribute(EMPID))); 
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		modeoftravelList = new FillRimbursementType().fillmodeoftravel();
		lodgingTypeList = new FillLodgingType().fillLodgingType();
		localConveyanceTranTypeList = new FillRimbursementType().fillmodeoftravel();
		
		StringBuilder sbPaycycleList = new StringBuilder();
		for(int i = 0; paycycleList!=null && i < paycycleList.size(); i++){
			FillPayCycles payCycles = paycycleList.get(i);
			sbPaycycleList.append("<option value=\""+payCycles.getPaycycleId()+"\">"+payCycles.getPaycycleName()+"</option>");
		}
		request.setAttribute("sbPaycycleList",sbPaycycleList.toString());
		
		StringBuilder sbProjectList = new StringBuilder();
		for(int i = 0; projectList!=null && i < projectList.size(); i++){
			FillProjectList fillProjectList = projectList.get(i);
			sbProjectList.append("<option value=\""+fillProjectList.getProjectID()+"\">"+fillProjectList.getProjectName()+"</option>");
		}
		request.setAttribute("sbProjectList",sbProjectList.toString());
		
		StringBuilder sbClientList = new StringBuilder();
		for(int i = 0; clientList!=null && i < clientList.size(); i++){
			FillClients fillClients = clientList.get(i);
			sbClientList.append("<option value=\""+fillClients.getClientId()+"\">"+fillClients.getClientName()+"</option>");
		}
		request.setAttribute("sbClientList",sbClientList.toString());
		
		StringBuilder sbTravelPlanList = new StringBuilder();
		for(int i = 0; travelPlanList!=null && i < travelPlanList.size(); i++){
			FillTravel fillTravel = travelPlanList.get(i);
			sbTravelPlanList.append("<option value=\""+fillTravel.getLeaveId()+"\">"+fillTravel.getPlanName()+"</option>");
		}
		request.setAttribute("sbTravelPlanList",sbTravelPlanList.toString());
		
		StringBuilder sbTypeList = new StringBuilder();
		for(int i = 0; typeList!=null && i < typeList.size(); i++){
			FillRimbursementType fillRimbursementType = typeList.get(i);
			sbTypeList.append("<option value=\""+fillRimbursementType.getTypeId()+"\">"+fillRimbursementType.getTypeName()+"</option>");
		}
		request.setAttribute("sbTypeList",sbTypeList.toString());
		
		StringBuilder sbModeoftravelList = new StringBuilder();
		for(int i = 0; modeoftravelList!=null && i < modeoftravelList.size(); i++){
			FillRimbursementType fillRimbursementType = modeoftravelList.get(i);
			sbModeoftravelList.append("<option value=\""+fillRimbursementType.getTypeId()+"\">"+fillRimbursementType.getTypeName()+"</option>");
		}
		request.setAttribute("sbModeoftravelList",sbModeoftravelList.toString());
		
		StringBuilder sbLodgingTypeList = new StringBuilder();
		for(int i = 0; lodgingTypeList!=null && i < lodgingTypeList.size(); i++){
			FillLodgingType fillLodgingType = lodgingTypeList.get(i);
			sbLodgingTypeList.append("<option value=\""+fillLodgingType.getLodgingTypeId()+"\">"+fillLodgingType.getLodgingTypeName()+"</option>");
		}
		request.setAttribute("sbLodgingTypeList",sbLodgingTypeList.toString());
		
		StringBuilder sbLocalConveyanceTranTypeList = new StringBuilder();
		for(int i = 0; localConveyanceTranTypeList!=null && i < localConveyanceTranTypeList.size(); i++){
			FillRimbursementType fillRimbursementType = localConveyanceTranTypeList.get(i);
			sbLocalConveyanceTranTypeList.append("<option value=\""+fillRimbursementType.getTypeId()+"\">"+fillRimbursementType.getTypeName()+"</option>");
		}
		request.setAttribute("sbLocalConveyanceTranTypeList",sbLocalConveyanceTranTypeList.toString());

		return LOAD;
	}
	

	public String addReimbursement() {
	
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
				
				String[] strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				
				for(int i = 1 ; i <= uF.parseToInt(getCount()); i++){
					String strAmount = (String) request.getParameter("strAmount"+i);
					if(strAmount == null){
						continue;
					}
					
					MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;   
					  
//					System.out.println("request==>"+request.getClass());
//					System.out.println("mpRequest==>"+mpRequest.getClass());
					File[] files = mpRequest.getFiles("strDocument"+i);    //  
					String[] fileNames = mpRequest.getFileNames("strDocument"+i); 
					
					StringBuilder sbFileName = new StringBuilder(); 
					for (int ii = 0; files != null && ii < files.length; ii++) {
						if(CF.getStrDocSaveLocation()==null){
							sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
						}else{
							sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
						} 
					}  
					String reimbursementType = (String) request.getParameter("reimbursementType");
					String startDate = null;
					String endDate = null;
					if(reimbursementType!=null && reimbursementType.equals("P")){
						String paycycle = (String) request.getParameter("paycycle");
						String[] arr = paycycle.split("-");
						startDate = arr[0];
						endDate = arr[1];
					} else {
						startDate = strPayCycleDates[0];
						endDate = strPayCycleDates[1];
					}
					
					String strClient = (String) request.getParameter("strClient"+i);
					String strProject = (String) request.getParameter("strProject"+i);
					String strTravelPlan = (String) request.getParameter("strTravelPlan"+i);
					String strType = (String) request.getParameter("strType"+i);
					String modeoftravel = (String) request.getParameter("modeoftravel"+i);
					String noofperson = (String) request.getParameter("noofperson"+i);
					String placefrom = (String) request.getParameter("placefrom"+i);
					String placeto = (String) request.getParameter("placeto"+i);
					String noofdays = (String) request.getParameter("noofdays"+i);
					String kmpd = (String) request.getParameter("kmpd"+i);
					String ratepkm = (String) request.getParameter("ratepkm"+i);
					String strPurpose = (String) request.getParameter("strPurpose"+i);
					String isbillable = (String) request.getParameter("isbillable"+i);
					String strVendor = (String) request.getParameter("strVendor"+i);
					String strFromDate = (String) request.getParameter("strFromDate"+i);
					
					String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo"+i);
					StringBuilder sbReceiptNo = new StringBuilder();
					for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
						if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")){
							sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
						}
					}
					
					int nTransportType = uF.parseToInt((String)request.getParameter("transportType"+i));
					int nTransportMode = 0;
					double dblTransportAmount = 0.0d;
					int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType"+i));
					double dblLodgingAmount = 0.0d;
					String strLocalConveyanceType = null;
					double dblLocalConveyanceKm = 0.0d;
					double dblLocalConveyanceRate = 0.0d;
					double dblLocalConveyanceAmount = 0.0d;
					double dblFoodBeverageAmount = 0.0d;
					double dblLaundryAmount = 0.0d;
					double dblSundryAmount = 0.0d;
					if(reimbursementType != null && reimbursementType.equalsIgnoreCase("T")) {
						if(nTransportType == 1) {
							nTransportMode = uF.parseToInt((String)request.getParameter("trainType"+i));
							dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount"+i));
						} else if(nTransportType == 2) {
							nTransportMode = uF.parseToInt((String)request.getParameter("busType"+i));
							dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount"+i));
						} else if(nTransportType == 3) {
							nTransportMode = uF.parseToInt((String)request.getParameter("flightType"+i));
							dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount"+i));
						} else if(nTransportType == 4) {
							nTransportMode = uF.parseToInt((String)request.getParameter("carType"+i));
							dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount"+i));
						}
						
						dblLodgingAmount = uF.parseToDouble((String)request.getParameter("strLodgingAmount"+i));
						
						strLocalConveyanceType = (String)request.getParameter("localConveyanceTranType"+i);
						if(strLocalConveyanceType!=null && !strLocalConveyanceType.trim().equals("")) {
							dblLocalConveyanceKm = uF.parseToDouble((String)request.getParameter("localConveyanceKM"+i));
							dblLocalConveyanceRate = uF.parseToDouble((String)request.getParameter("localConveyanceRate"+i));
							dblLocalConveyanceAmount = uF.parseToDouble((String)request.getParameter("strLocalConveyanceAmount"+i));
						}
						
						dblFoodBeverageAmount = uF.parseToDouble((String)request.getParameter("strFoodBeverageAmount"+i));
						dblLaundryAmount = uF.parseToDouble((String)request.getParameter("strLaundryAmount"+i));
						dblSundryAmount = uF.parseToDouble((String)request.getParameter("strSundryAmount"+i));
					}
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
							"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
							"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
							"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
							"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,reimb_from_date) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					
					if (reimbursementType != null && reimbursementType.equalsIgnoreCase("P")) {
						pst.setString(3, strProject);
					} else if (reimbursementType != null && reimbursementType.equalsIgnoreCase("T")) {
						pst.setString(3, strTravelPlan);
					} else if (reimbursementType != null && reimbursementType.equalsIgnoreCase("M")) {
						pst.setString(3, "Mobile Bill");
					} else {
						pst.setString(3, strType);
					}
					
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
		
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, reimbursementType);
					
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					if (reimbursementType != null && reimbursementType.equalsIgnoreCase("M")) {
						pst.setString(17, "Mobile Bill");
					} else {
						pst.setString(17, strType);
					}
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					if (reimbursementType != null && reimbursementType.equalsIgnoreCase("P")) {
						pst.setInt(20, uF.parseToInt(strProject));
					} else {
						pst.setInt(20, 0);
					}
					pst.setString(21, strVendor);
					pst.setString(22, sbReceiptNo.toString());
					
					pst.setInt(23, nTransportType);
					pst.setInt(24, nTransportMode);
					pst.setDouble(25, dblTransportAmount);
					
					pst.setInt(26, nLodgingType);
					pst.setDouble(27, dblLodgingAmount);
					
					pst.setString(28, strLocalConveyanceType);
					pst.setDouble(29, dblLocalConveyanceKm);
					pst.setDouble(30, dblLocalConveyanceRate);
					pst.setDouble(31, dblLocalConveyanceAmount);
					
					pst.setDouble(32, dblFoodBeverageAmount);
					pst.setDouble(33, dblLaundryAmount);
					pst.setDouble(34, dblSundryAmount);		
					pst.setDate(35, uF.getDateFormat(strFromDate, DATE_FORMAT));
					int x = pst.executeUpdate();
		
					if(x > 0) {
						String reimbursement_id=null;
						pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
						rs=pst.executeQuery();
						while(rs.next()){
							reimbursement_id=rs.getString("reimbursement_id");
						}
						rs.close();
						pst.close();
		//				System.out.println("reimbursement_id====>"+reimbursement_id);
						
						List<String> alManagers = null;
						if(uF.parseToBoolean(CF.getIsWorkFlow())){
							alManagers = insertLeaveApprovalMember(con,pst,rs,reimbursement_id,uF,strAmount);
						}
						
						
						String strDomain = request.getServerName().split("\\.")[0];
						/*String filePath = request.getRealPath("/userDocuments/") + File.separator;
						Notifications nF = new Notifications(N_EMPLOYEE_REIMBURSEMENT_REQUEST, CF); 
						nF.setDomain(strDomain);
						nF.request = request;
	//					if (strUserType != null && strUserType.equals(HRMANAGER)) {
	//						nF.setStrEmpId(getStrSelectedEmpId1());
	//					} else {
							nF.setStrEmpId(strSessionEmpId);
	//					}
						
		//				nF.setStrHostAddress(request.getRemoteHost());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrEmpReimbursementFrom(uF.getDateFormat(startDate, DATE_FORMAT, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementTo(uF.getDateFormat(endDate, DATE_FORMAT, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementPurpose(strPurpose);
						nF.setStrEmpReimbursementType(strType);
						nF.setStrEmpReimbursementAmount(strAmount);
						// nF.setStrAttachmentFileSource(filePath+fileName);
						// nF.setStrAttachmentFileName(fileName);
						nF.setEmailTemplate(true);
						nF.sendNotifications(); */
						
						Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
						if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
//						Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
						Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetailsForPDF(con);
						if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
						
						String strCurrId = hmEmpCurrency.get(strSessionEmpId);
						Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
						if (hmCurrencyInner == null)hmCurrencyInner = new HashMap<String, String>();
						String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
						
						Notifications nF = new Notifications(N_MANAGER_REIMBURSEMENT_REQUEST, CF); 
						nF.setDomain(strDomain);
						nF.request = request;
	//					if (strUserType != null && strUserType.equals(HRMANAGER)) {
	//						nF.setStrEmpId(getStrSelectedEmpId1());
	//					} else {
							nF.setStrEmpId(strSessionEmpId);
	//					}
						nF.setSupervisor(false);
						nF.setEmailTemplate(true);
						for(int ii=0; alManagers!=null && ii<alManagers.size();ii++){
							pst = con.prepareStatement(selectEmpDetails1);
							pst.setInt(1, uF.parseToInt((String)alManagers.get(ii)));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()){
								nF.setStrSupervisorEmail(rs.getString("emp_email"));	
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								
								nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
								nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
								if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0){
									nF.setStrEmpEmail(rs.getString("emp_email_sec"));
									nF.setStrEmailTo(rs.getString("emp_email_sec"));
								}else{
									nF.setStrEmpEmail(rs.getString("emp_email"));
									nF.setStrEmailTo(rs.getString("emp_email"));
								}
								flg=true;
							}
							rs.close();
							pst.close();
							if(flg){
		//						nF.setStrMgrId((String)alManagers.get(i));
		//						nF.setStrHostAddress(request.getRemoteHost());
								nF.setStrHostAddress(CF.getStrEmailLocalHost());
								nF.setStrHostPort(CF.getStrHostPort());
								nF.setStrContextPath(request.getContextPath());
								nF.setStrEmpReimbursementFrom(uF.getDateFormat(startDate, DATE_FORMAT, CF.getStrReportDateFormat()));
								nF.setStrEmpReimbursementTo(uF.getDateFormat(endDate, DATE_FORMAT, CF.getStrReportDateFormat()));
								nF.setStrEmpReimbursementPurpose(strPurpose);
								nF.setStrEmpReimbursementType(strType);
								nF.setStrEmpReimbursementAmount(strAmount);
								// nF.setStrAttachmentFileSource(filePath+fileName);
								// nF.setStrAttachmentFileName(fileName);
								nF.setStrEmpReimbursementDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()));
								nF.setStrEmpReimbursementCurrency(strCurrSymbol);
								
								nF.sendNotifications();
							}
						}
					}
				}
				session.setAttribute(MESSAGE, SUCCESSM+"Reimbursement saved successfully."+END);
				
			} catch (Exception e) {
				e.printStackTrace();
				session.setAttribute(MESSAGE, ERRORM+"Reimbursement not saved."+END);
			} finally {
				
				db.closeStatements(pst);
				db.closeConnection(con);
			}
	
			return UPDATE;
	
		}
	
	private List<String> insertLeaveApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, String reimbursement_id, UtilityFunctions uF, String strAmount) {
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
					pst.setInt(2,uF.parseToInt(reimbursement_id));
					pst.setString(3,"Reimbursements");
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					
					
					String alertData = "<div style=\"float: left;\"> Received a new Reimbursement Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> amount "+uF.formatIntoTwoDecimal(uF.parseToDouble(strAmount))+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyReimbursement"+strSubAction;
					} else {
						alertAction = "PayApprovals.action?pType=WR&callFrom=NotiApplyReimbursement"+strSubAction;
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
		}
		return alManagers;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getReimbursementType() {
		return reimbursementType;
	}

	public void setReimbursementType(String reimbursementType) {
		this.reimbursementType = reimbursementType;
	}

	public List<FillRimbursementType> getReimbursementTypeList() {
		return reimbursementTypeList;
	}

	public void setReimbursementTypeList(List<FillRimbursementType> reimbursementTypeList) {
		this.reimbursementTypeList = reimbursementTypeList;
	}

	public List<FillRimbursementType> getModeoftravelList() {
		return modeoftravelList;
	}

	public void setModeoftravelList(List<FillRimbursementType> modeoftravelList) {
		this.modeoftravelList = modeoftravelList;
	}

	public List<FillRimbursementType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<FillRimbursementType> typeList) {
		this.typeList = typeList;
	}

	public List<FillTravel> getTravelPlanList() {
		return travelPlanList;
	}

	public void setTravelPlanList(List<FillTravel> travelPlanList) {
		this.travelPlanList = travelPlanList;
	}

	public List<FillProjectList> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<FillProjectList> projectList) {
		this.projectList = projectList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public File[] getStrDocument() {
		return strDocument;
	}

	public void setStrDocument(File[] strDocument) {
		this.strDocument = strDocument;
	}

	public String[] getStrDocumentFileName() {
		return strDocumentFileName;
	}
	public void setStrDocumentFileName(String[] strDocumentFileName) {
		this.strDocumentFileName = strDocumentFileName;
	}
	public String getSubmit() {
		return submit;
	}
	public void setSubmit(String submit) {
		this.submit = submit;
	}
	public String getReimType() {
		return reimType;
	}
	public void setReimType(String reimType) {
		this.reimType = reimType;
	}
	public List<FillLodgingType> getLodgingTypeList() {
		return lodgingTypeList;
	}

	public void setLodgingTypeList(List<FillLodgingType> lodgingTypeList) {
		this.lodgingTypeList = lodgingTypeList;
	}

	public List<FillRimbursementType> getLocalConveyanceTranTypeList() {
		return localConveyanceTranTypeList;
	}

	public void setLocalConveyanceTranTypeList(List<FillRimbursementType> localConveyanceTranTypeList) {
		this.localConveyanceTranTypeList = localConveyanceTranTypeList;
	}
}