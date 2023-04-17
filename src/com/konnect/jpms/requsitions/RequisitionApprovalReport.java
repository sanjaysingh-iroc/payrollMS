package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RequisitionApprovalReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	String strStartDate;
	String strEndDate;
	String requiStatus;
	
	String alertStatus;
	String alert_type;
	
	String mReason; 
	String type;
	String userType;
	
	String currUserType;
	String alertID;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
			  
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions(); 
		request.setAttribute(PAGE, "/jsp/requisitions/RequisitionApprovalReport.jsp");
		request.setAttribute(TITLE, "Requisition Approval");
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		String operation = (String) request.getParameter("operation");
		String strRequiId = (String) request.getParameter("strRequiId");
		String approveStatus = (String) request.getParameter("approveStatus"); 
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		if(operation != null && operation.equalsIgnoreCase("E") && uF.parseToInt(strRequiId) > 0){
			updateRequisitionRequest(uF,strRequiId,approveStatus);
			if(getType()!=null && getType().equals("type")){
				return DASHBOARD;
			}
		}
		
		if(getRequiStatus()==null){
			setRequiStatus("2");
		}
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(REQUISITION_REQUEST_ALERT)){
//			String strDomain = request.getServerName().split("\\.")[0];
//			CF.updateUserAlerts(CF,request,strSessionEmpId,strDomain,REQUISITION_REQUEST_ALERT,UPDATE_ALERT);
//		} 

		viewRequisitionAppraoval1(uF);
		
		return loadRequisitionLeaveApproval(uF);

	}
	
	 
	private void updateRequisitionRequest(UtilityFunctions uF, String strRequiId, String approveStatus) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			
			boolean flag = true;
			boolean flagAdmin = false;
			if (uF.parseToBoolean(CF.getIsWorkFlow())) {
//                pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_REQUISITION+"'" +
//                        " and effective_id=? order by effective_id,member_position");
//                pst.setInt(1, uF.parseToInt(strRequiId));
//                rs = pst.executeQuery();
//                Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
//                while (rs.next()) {
//                    List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
//                    if (checkEmpList == null) checkEmpList = new ArrayList<String>();
//                    checkEmpList.add(rs.getString("emp_id"));
//                    hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
//                }
//    			rs.close();
//    			pst.close();
    			
                if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
                    pst = con.prepareStatement("update requisition_details set is_approved=?, approved_by=?, approved_date=?,approve_reason=? where requisition_id=?");
                    pst.setInt(1, uF.parseToInt(approveStatus));
        			pst.setInt(2, uF.parseToInt(strSessionEmpId));
        			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
        			pst.setString(4, getmReason());
        			pst.setInt(5, uF.parseToInt(strRequiId));
                    pst.execute();
        			pst.close();
        			flagAdmin = true;
                } else {
                    pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? " +
                    		"and effective_type='"+WORK_FLOW_REQUISITION+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(strRequiId));
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setInt(3, uF.parseToInt(getUserType()));
                    rs = pst.executeQuery();
                    int work_id = 0;
                    while (rs.next()) {
                        work_id = rs.getInt("work_flow_id");
                        break;
                    }
        			rs.close();
        			pst.close();
                    
                    pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=?,reason=? WHERE work_flow_id=?");
                    pst.setInt(1, uF.parseToInt(approveStatus));
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
        			pst.setString(4, getmReason());
                    pst.setInt(5, work_id);
                    pst.execute();
        			pst.close();
                    
                    
                    pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_REQUISITION+"' " +
                            " and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
                            " and is_approved=0 and effective_type='"+WORK_FLOW_REQUISITION+"' and member_position not in " +
                            " (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_REQUISITION+"' )) " +
                            " order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(strRequiId));
                    pst.setInt(2, uF.parseToInt(strRequiId));
                    pst.setInt(3, uF.parseToInt(strRequiId));
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        flag = false;
                    }
        			rs.close();
        			pst.close();
        			
                    if (flag) {
                        pst = con.prepareStatement("update requisition_details set is_approved=?, approved_by=?, approved_date=?,approve_reason=? where requisition_id=?");
                        pst.setInt(1, uF.parseToInt(approveStatus));
            			pst.setInt(2, uF.parseToInt(strSessionEmpId));
            			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
            			pst.setString(4, getmReason());
            			pst.setInt(5, uF.parseToInt(strRequiId));
                        pst.execute();
            			pst.close();
                    } else if (uF.parseToInt(approveStatus) == -1) {
                        pst = con.prepareStatement("update requisition_details set is_approved=?, approved_by=?, approved_date=?,approve_reason=? where requisition_id=?");
                        pst.setInt(1, uF.parseToInt(approveStatus));
            			pst.setInt(2, uF.parseToInt(strSessionEmpId));
            			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
            			pst.setString(4, getmReason());
            			pst.setInt(5, uF.parseToInt(strRequiId));
                        pst.execute();
            			pst.close();
                    }
                } 
            } else {
                if (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
                	pst = con.prepareStatement("update requisition_details set is_approved=?, approved_by=?, approved_date=?,approve_reason=? where requisition_id=?");
                    pst.setInt(1, uF.parseToInt(approveStatus));
         			pst.setInt(2, uF.parseToInt(strSessionEmpId));
         			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
         			pst.setString(4, getmReason());
         			pst.setInt(5, uF.parseToInt(strRequiId));
                    pst.execute();
         			pst.close();
         			flagAdmin = true;
                }
            }
			
            if (flagAdmin || flag || uF.parseToInt(approveStatus)== -1) {
            	pst = con.prepareStatement("select * from requisition_details where requisition_id=?");
    			pst.setInt(1, uF.parseToInt(strRequiId));
    			rs = pst.executeQuery();
    			int nEmpId = 0;
    			while (rs.next()){
    				nEmpId = rs.getInt("emp_id");
    			}
    			rs.close();
    			pst.close();
    			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
    			
    			String strDomain = request.getServerName().split("\\.")[0];
				String strApproveDeny = "approved";
				if(uF.parseToInt(approveStatus)== -1) {
					strApproveDeny = "denied";
				}
				String alertData = "<div style=\"float: left;\"> Your Requisition has been "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "MyPay.action?pType=WR";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(""+nEmpId);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
//	            UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(""+nEmpId);
//				userAlerts.set_type(REQUISITION_APPROVAL_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
            }
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Requisition approve failed!"+END);
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}


	private void viewRequisitionAppraoval1(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {
			
			if(getStrStartDate()==null && getStrEndDate()==null){

				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				
				setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				
			}
			
			con=db.makeConnection(con);
			
//			String []arrEnabledModules = CF.getArrEnabledModules();
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
//			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
//			String locationID=hmEmpWlocationMap.get(strSessionEmpId);
			
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null); 
//			Map<String, String> hmLevelMap = CF.getEmpLevelMap(con);
			
			
			pst = con.prepareStatement("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_REQUISITION+"' group by effective_id");
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
//			" and is_approved=0 and effective_type='"+WORK_FLOW_REQUISITION+"' and user_type_id=? group by effective_id ");
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_REQUISITION+"'");
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
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_REQUISITION+"' group by effective_id");
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}	
			rs.close();
			pst.close();		
			
			pst = con.prepareStatement("select requisition_id from requisition_details where is_approved=-1");
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("requisition_id"))){
					deniedList.add(rs.getString("requisition_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
			" and effective_type='"+WORK_FLOW_REQUISITION+"' group by effective_id,is_approved");
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()){
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_REQUISITION+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();		
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();		
			while(rs.next()){
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_REQUISITION+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();			
			while(rs.next()){
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			StringBuilder squery = new StringBuilder();
			squery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_REQUISITION+"'");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				squery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				squery.append(" and user_type_id=? ");
			}
			squery.append(" order by effective_id,member_position "); 
			
//			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_REQUISITION+"' and user_type_id=? order by effective_id,member_position");
			pst = con.prepareStatement(squery.toString());
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(1, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()){
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
//			while(rs.next()){
//				hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
//			}
//			rs.close();
//			pst.close();
			
			pst = con.prepareStatement("select activity_id,activity_name from activity_details ad, nodes n where ad.activity_id=n.mapped_activity_id and ad.isactivity=? order by activity_name");
			pst.setBoolean(1, true);
			rs = pst.executeQuery();
			Map<String, String> hmDocTypeMap = new HashMap<String, String>();
			while(rs.next()){
				hmDocTypeMap.put(rs.getString("activity_id"), rs.getString("activity_name"));				
			}	
			rs.close();
			pst.close(); 
			
			pst = con.prepareStatement("select * from infrastructure_type order by infra_type_id");
			rs = pst.executeQuery();
			Map<String, String> hmInfraTypeMap = new HashMap<String, String>();
			while (rs.next()) {
				hmInfraTypeMap.put(rs.getString("infra_type_id"), rs.getString("infra_type"));
			}
			rs.close();
			pst.close();
			
			List<List<String>> reportList = new ArrayList<List<String>>();
//			List<String> alEmployeeList = new ArrayList<String>();			
			List<String> alList = new ArrayList<String>();	
			sbQuery=new StringBuilder();
			sbQuery.append("select rd.*,wfd.user_type_id as user_type from requisition_details rd, work_flow_details wfd where requisition_id > 0 and to_date(requisition_date::text,'yyyy-MM-dd') between ? and ?" +
					" and rd.requisition_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_REQUISITION+"' and rd.is_approved > -2 ");
			if(uF.parseToInt(getRequiStatus())==1){ 
				sbQuery.append(" and rd.is_approved=1");
			}else if(uF.parseToInt(getRequiStatus())==2){
				sbQuery.append(" and rd.is_approved=0");
			}else if(uF.parseToInt(getRequiStatus())==3){
				sbQuery.append(" and rd.is_approved=-1");
			}		
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by requisition_date desc");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();	 
			while(rs.next()){
				
//				if(!alEmployeeList.contains(rs.getString("emp_id")) ){
//					alEmployeeList.add(rs.getString("emp_id"));
//				}
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("requisition_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("requisition_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
//				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)){ 
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
//				if(strUserType.equalsIgnoreCase(HRMANAGER) && hmEmpByLocation.get(rs.getString("emp_id"))==null){
//					continue;
//				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("requisition_id"))){
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("requisition_id"))){
					userType = strUserTypeId;
					alList.add(rs.getString("requisition_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("requisition_id"));
				alInner.add(rs.getString("emp_id"));
				alInner.add(hmEmployeeNameMap.get(rs.getString("emp_id")));
				
				String strRequitype = "";
				String strDoctype = "";
				String strInfraType = "";
				if(rs.getInt("requi_type") == R_N_REQUI_DOCUMENT){
					strRequitype = R_S_REQUI_DOCUMENT;
					strDoctype = uF.showData(hmDocTypeMap.get(rs.getString("document_id")), "");
				} else if(rs.getInt("requi_type") == R_N_REQUI_INFRASTRUCTURE){
					strRequitype = R_S_REQUI_INFRASTRUCTURE;					
					strInfraType = uF.showData(hmInfraTypeMap.get(rs.getString("infra_type")), "");
				} else if(rs.getInt("requi_type") == R_N_REQUI_OTHER){
					strRequitype = R_S_REQUI_OTHER;
				}
				alInner.add(strRequitype);
				alInner.add(strDoctype);
				alInner.add(strInfraType);
				alInner.add(rs.getString("purpose"));
				alInner.add(uF.getDateFormat(rs.getString("requisition_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("requi_from"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("requi_to"), DBDATE, CF.getStrReportDateFormat()));
				
				 
				if(deniedList.contains(rs.getString("requisition_id"))){
					/*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				}else if(rs.getInt("is_approved")==1 && uF.parseToBoolean(rs.getString("is_received"))){							
					alInner.add("<img title=\"Received\" src=\"images1/icons/act_now.png\" border=\"0\">");
				}else if(rs.getInt("is_approved")==1){							
					 /*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				}else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("requisition_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("requisition_id")))==rs.getInt("is_approved")){
					/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />"); */
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>"); 
					
				}else if(uF.parseToInt(hmNextApproval.get(rs.getString("requisition_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("requisition_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("requisition_id")))>0){
					alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+rs.getString("requisition_id")+"','"+getRequiStatus()+"','"+getStrStartDate()+"','"+getStrEndDate()+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("requisition_id")+"','"+getRequiStatus()+"','"+getStrStartDate()+"','"+getStrEndDate()+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Denied\"></i></a> ");
				}else if(uF.parseToInt(hmNextApproval.get(rs.getString("requisition_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("requisition_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("requisition_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("requisition_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("requisition_id")+"_"+userType)))){
					if(rs.getInt("is_approved")==0){
						if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)){
							alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+rs.getString("requisition_id")+"','"+getRequiStatus()+"','"+getStrStartDate()+"','"+getStrEndDate()+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Approved\"  border=\"0\" ></i></a> " +
									" <a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("requisition_id")+"','"+getRequiStatus()+"','"+getStrStartDate()+"','"+getStrEndDate()+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Denied\"></i></a> ");
						}else{
							/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
							
						}
					}else if(rs.getInt("is_approved")==1){							
						/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						
					}else{
						/*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					}
				}else{
					if(strUserType.equalsIgnoreCase(ADMIN)){
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+rs.getString("requisition_id")+"','"+getRequiStatus()+"','"+getStrStartDate()+"','"+getStrEndDate()+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Approved\"></i></a> " +
								" <a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("requisition_id")+"','"+getRequiStatus()+"','"+getStrStartDate()+"','"+getStrEndDate()+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Denied\"></i></a> ");
					}else{
						/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
						
					}
				}
				
				StringBuilder sbCheckApproveby=new StringBuilder();
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("requisition_id"))!=null){
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("requisition_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("requisition_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("requisition_id"))), "")+")" : "";
//					sbCheckApproveby.append(hmEmployeeNameMap.get(approvedby.trim())+strUserTypeName);
					sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("requisition_id")+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				}else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("requisition_id"))!=null){
					sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("requisition_id")+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");	
				}else{
					sbCheckApproveby.append("");
				}
				
				alInner.add(sbCheckApproveby.toString());
				alInner.add(uF.showData(hmUserTypeMap.get(userType), ""));
				
				reportList.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", reportList);
					
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadRequisitionLeaveApproval(UtilityFunctions uF){
		getSelectedFilter(uF);
		return "load";
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				alFilter.add("FROMTO");
				hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			
			alFilter.add("STATUS");
			if(uF.parseToInt(getRequiStatus())==1) { 
				hmFilter.put("STATUS", "Approved");
			} else if(uF.parseToInt(getRequiStatus())==2) {
				hmFilter.put("STATUS", "Pending");
			} else if(uF.parseToInt(getRequiStatus())==3) {
				hmFilter.put("STATUS", "Denied");
			} else {
				hmFilter.put("STATUS", "All");
			}
			
		String selectedFilter = CF.getSelectedFilter2(CF, uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getRequiStatus() {
		return requiStatus;
	}

	public void setRequiStatus(String requiStatus) {
		this.requiStatus = requiStatus;
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

	public String getmReason() {
		return mReason;
	}

	public void setmReason(String mReason) {
		this.mReason = mReason;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
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
