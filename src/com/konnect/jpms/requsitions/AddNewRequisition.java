package com.konnect.jpms.requsitions;

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

import com.konnect.jpms.select.FillActivity;
import com.konnect.jpms.select.FillInfrastructureType;
import com.konnect.jpms.select.FillRequisitionType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddNewRequisition extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;
	String strSWlocationId = null;
	
	private List<FillRequisitionType> requisitionTypeList;
	private List<FillActivity> docList;
	private List<FillInfrastructureType> infraTypeList;
	
	private String strRequisitionType; 
	private String strDoc;
	private String strFromDate;
	private String strToDate;
	private String strInfraType;
	private String strPurpose;

	private String policy_id;
	
	private String alertStatus;
	private String alert_type;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		
		 if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(REQUISITION_APPROVAL_ALERT)){
			String strDomain = request.getServerName().split("\\.")[0];
			CF.updateUserAlerts(CF,request,strSessionEmpId,strDomain,REQUISITION_APPROVAL_ALERT,UPDATE_ALERT);
		}
		
		String operation = (String) request.getParameter("operation");
		String strRequiId = (String) request.getParameter("strRequiId");
		
		if(operation != null && operation.equalsIgnoreCase("P") && uF.parseToInt(strRequiId) > 0){
			pullOutRequisition(uF,strRequiId);
			return UPDATE;
		}
		if(operation != null && operation.equalsIgnoreCase("R") && uF.parseToInt(strRequiId) > 0){
			receiveRequisition(uF, strRequiId);
			return UPDATE;
		} 
		if (getStrRequisitionType() != null) {			
			insertRequisition(uF);
			return SUCCESS;
		}
		
		getPolicyMember(uF);
			
		return loadLeaveEntry(uF);
	}	

	
	private void receiveRequisition(UtilityFunctions uF, String strRequiId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("update requisition_details set is_received=? where requisition_id=?");
			pst.setBoolean(1, true);
			pst.setInt(2, uF.parseToInt(strRequiId));
			int x = pst.executeUpdate();
			pst.close();
			if(x > 0){
				request.setAttribute("STATUS_MSG", "<img title=\"Received\" src=\""+request.getContextPath()+"/images1/icons/act_now.png\" border=\"0\">");
			} else {
				request.setAttribute("STATUS_MSG", "Update Failed");
			}
			  
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}


	private void pullOutRequisition(UtilityFunctions uF, String strRequiId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("update requisition_details set is_approved=? where requisition_id=?");
			pst.setInt(1, -2);
			pst.setInt(2, uF.parseToInt(strRequiId));
			int x = pst.executeUpdate();
			pst.close();
			if(x > 0){
				getStatusMessage(-2);
			} else {
				request.setAttribute("STATUS_MSG", "Pull Out Failed");
			}
			  
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
public void getStatusMessage(int nStatus){
		
		switch(nStatus){
		
		case -1:
			 /*request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\""+request.getContextPath()+"/images1/icons/denied.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
			
			break;
			
		case 0:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pending\" src=\""+request.getContextPath()+"/images1/icons/pending.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\"></i>");
			
			break;
			
		case 1:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\""+request.getContextPath()+"/images1/icons/approved.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
			break;
			
		case -2:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pulled\" src=\""+request.getContextPath()+"/images1/icons/pullout.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled\"></i>");
			break;
			
		case 3:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Submited\" src=\""+request.getContextPath()+"/images1/icons/re_submit.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Submited\"></i>");
			break;
		}
}

	private void insertRequisition(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into requisition_details (requisition_date,emp_id,requi_type,document_id,requi_from,requi_to,infra_type," +
					"purpose,is_approved,is_received)values (?,?,?,?,?,?,?,?,?,?)");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(getStrRequisitionType()));
			pst.setInt(4, uF.parseToInt(getStrDoc()));
			pst.setDate(5, uF.getDateFormat(getStrFromDate(), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(getStrToDate(), DATE_FORMAT));
			pst.setInt(7, uF.parseToInt(getStrInfraType()));
			pst.setString(8, getStrPurpose());
			pst.setInt(9, 0);
			pst.setBoolean(10, false);
			int x = pst.executeUpdate();
			pst.close();
			if(x > 0){
				int nRequisitionId = 0;
				pst = con.prepareStatement("select max(requisition_id)as requisition_id from requisition_details");
				rs=pst.executeQuery();
				if(rs.next()){
					nRequisitionId = rs.getInt("requisition_id");
				}
				rs.close();
				pst.close();
				
				if(nRequisitionId > 0){
					session.setAttribute(MESSAGE, SUCCESSM+"Requisition added successfully!"+END);
					List<String> alManagers = null;
					if(uF.parseToBoolean(CF.getIsWorkFlow())){
						alManagers = insertApprovalMember(con,pst,rs,nRequisitionId,uF);
					}
				}
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Requisition failed!"+END);
			}
				
			
			  
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Requisition failed!"+END);
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	private List<String> insertApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, int nRequisitionId, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			
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
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")){
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3){
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,nRequisitionId);
					pst.setString(3,WORK_FLOW_REQUISITION);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();
										
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.set_type(REQUISITION_REQUEST_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
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

	private void getPolicyMember(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		String policy_id=null;
		try {
			
			int strEmpID=0;
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))){
				strEmpID=uF.parseToInt(strSessionEmpId);
			}
			
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
			
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_REQUISITION+"' and level_id=? and wlocation_id=?");
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
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname, epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+" and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
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
									
									String optionTr="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id=? and supervisor_emp_id!=0) as a," +
										"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE' and epd.is_alive=true" +
										"  order by epd.emp_fname");
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
									
									String optionTr11="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
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
								
								if(outerList1!=null && !outerList1.isEmpty()){
									StringBuilder sbComboBox1=new StringBuilder();
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++){
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
								
								if(outerList2!=null && !outerList2.isEmpty()){
									StringBuilder sbComboBox2=new StringBuilder();
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList2.size();i++){
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
								
								if(outerList3!=null && !outerList3.isEmpty()){
									StringBuilder sbComboBox3=new StringBuilder();
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++){
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
								
								if(outerList4!=null && !outerList4.isEmpty()){
									StringBuilder sbComboBox4=new StringBuilder();
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++){
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
									
									String optionTr5="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
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
						
					}else if(uF.parseToInt(innerList.get(0))==3){
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
						
						
						if(outerList!=null && !outerList.isEmpty()){
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++){
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight textcolorWhite\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				StringBuilder sb = new StringBuilder();
//				System.out.println("uF.parseToBoolean(CF.getIsWorkFlow())====>"+uF.parseToBoolean(CF.getIsWorkFlow()));
				if(uF.parseToBoolean(CF.getIsWorkFlow())){		
//					 sb.append("<div id=\"popup_name" + strEmpID + "\" class=\"popup_block\">" + 
//							   "<h2 class=\"textcolorWhite\">Reimbursements of "+hmEmpCodeName.get(""+strEmpID)+"</h2>" + 
//							   "<table>");
										
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
//					 sb.append("</table></div>");
					
				}else{
					sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"input_button\"/></td></tr>");
				}
//				System.out.println("sb.toString()===="+sb.toString());
				request.setAttribute("hmMemberOption",hmMemberOption);
//				request.setAttribute("policy_id",policy_id);
				setPolicy_id(policy_id);
				request.setAttribute("requisitionD", sb.toString());
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

	public String loadLeaveEntry(UtilityFunctions uF) {
		requisitionTypeList = new FillRequisitionType(request).fillRequisitionType();
		docList = new FillActivity(request).fillActivityByNode(true, true);
		infraTypeList = new FillInfrastructureType(request).fillInfrastructureType();
		
		return LOAD;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillInfrastructureType> getInfraTypeList() {
		return infraTypeList;
	}


	public void setInfraTypeList(List<FillInfrastructureType> infraTypeList) {
		this.infraTypeList = infraTypeList;
	}


	public List<FillRequisitionType> getRequisitionTypeList() {
		return requisitionTypeList;
	}


	public void setRequisitionTypeList(List<FillRequisitionType> requisitionTypeList) {
		this.requisitionTypeList = requisitionTypeList;
	}


	public List<FillActivity> getDocList() {
		return docList;
	}


	public void setDocList(List<FillActivity> docList) {
		this.docList = docList;
	}


	public String getStrRequisitionType() {
		return strRequisitionType;
	}


	public void setStrRequisitionType(String strRequisitionType) {
		this.strRequisitionType = strRequisitionType;
	}


	public String getStrDoc() {
		return strDoc;
	}


	public void setStrDoc(String strDoc) {
		this.strDoc = strDoc;
	}


	public String getStrFromDate() {
		return strFromDate;
	}


	public void setStrFromDate(String strFromDate) {
		this.strFromDate = strFromDate;
	}


	public String getStrToDate() {
		return strToDate;
	}


	public void setStrToDate(String strToDate) {
		this.strToDate = strToDate;
	}


	public String getStrInfraType() {
		return strInfraType;
	}


	public void setStrInfraType(String strInfraType) {
		this.strInfraType = strInfraType;
	}


	public String getStrPurpose() {
		return strPurpose;
	}


	public void setStrPurpose(String strPurpose) {
		this.strPurpose = strPurpose;
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

}
