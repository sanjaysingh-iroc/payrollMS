package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.requsitions.UpdateRequest;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateLeaveEncashment extends ActionSupport  implements ServletRequestAware, IStatements {
	
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(UpdateRequest.class);
	
	private String approveStatus;
	private String leaveEncashId;
	private String paycycle;
	private String empId;
	private List<FillPayCycles> paycycleList;

	private String mReason;
	private String type;
	private String userType;
	private String currUserType;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		System.out.println("userType::"+userType);

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getApproveStatus()!= null && getApproveStatus().equalsIgnoreCase("2")) {
			deleteRequest();
			if(getType()!=null && getType().equals("type")){
				return DASHBOARD;
			}
			return SUCCESS;
		} else if(getApproveStatus()!= null && getApproveStatus().equalsIgnoreCase("-1")) {			
			updateRequest();
			if(getType()!=null && getType().equals("type")){
				return DASHBOARD;
			}
			return SUCCESS;
		} else if(getApproveStatus()!= null && getPaycycle()!=null){			
			updateRequest();
			if(getType()!=null && getType().equals("type")){
				return DASHBOARD;
			}
			return SUCCESS;
		}
		
//		System.out.println("userType==>"+getUserType()+"==>getCurrentUserType==>"+getCurrUserType());
		return loadData();
		
	}
	
	private String loadData() {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			Map<String,String> hmUserTypes = CF.getUserTypeMap(con);
			/*String userTypeName = "";
			if(uF.parseToInt(getUserType()) > 0) {
				userTypeName = hmUserTypes.get(getUserType());
			}
			
			request.setAttribute("userTypeName", userTypeName);*/
			String strOrg = CF.getEmpOrgId(con, uF, getEmpId());
			paycycleList = new FillPayCycles(request).fillCurrentNextPayCycleByOrg(CF.getStrTimeZone(), CF,strOrg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
		return LOAD;
	}

	public void updateRequest(){
		
		System.out.println("In update Request");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			String strPaycyle=null;
			String strStartDate=null;
			String strEndDate=null;
			if(getApproveStatus()!= null && getApproveStatus().equalsIgnoreCase("1")) {
				String[] arrDates = getPaycycle().split("-");
				strStartDate = arrDates[0];
				strEndDate = arrDates[1];
				strPaycyle = arrDates[2];
			}
//			is_approved,approved_by,approved_date,paid_from,paid_to,paycycle
			
			PreparedStatement pst1 = con.prepareStatement("select * from emp_leave_encashment where leave_encash_id = ?");
			pst1.setInt(1, uF.parseToInt(getLeaveEncashId()));
			ResultSet rs1 = pst1.executeQuery();
			String strEmpId=null;
			String leaveTypeId=null;
			String noDays=null;
			if(rs1.next()){
				strEmpId=rs1.getString("emp_id");
				leaveTypeId=rs1.getString("leave_type_id");
				noDays=rs1.getString("no_days");
			}
            rs1.close();
            pst1.close();
            
            if(uF.parseToInt(getEmpId()) == 0) {
            	setEmpId(strEmpId);
            }
			if(uF.parseToBoolean(CF.getIsWorkFlow())) {
				
//				pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE_ENCASH+"'" +
//					" and effective_id=? order by effective_id,member_position");
//				pst.setInt(1, uF.parseToInt(getLeaveEncashId()));
//				rs = pst.executeQuery();			
//				List<String> checkEmpList=new ArrayList<String>();
//				while(rs.next()){
//					checkEmpList.add(rs.getString("emp_id"));					
//				}
//	            rs.close();
//	            pst.close();
				System.out.println("strUserType::"+strUserType);
				
				if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
					System.out.println("strUserType::=ADMIN");
					pst = con.prepareStatement("update emp_leave_encashment set is_approved = ?,approved_by = ?,approved_date = ?,paid_from = ?,paid_to = ?,paycycle = ?,approve_reason=?  where leave_encash_id = ?");
					pst.setInt(1, uF.parseToInt(getApproveStatus()));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setInt(6, uF.parseToInt(strPaycyle));
					pst.setString(7, getmReason());
					pst.setInt(8, uF.parseToInt(getLeaveEncashId()));
					pst.executeUpdate();
					System.out.println("pst====>"+pst);
		            pst.close();
					
				} else {	
					System.out.println("strUserType::other thanADMIN");
					pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? " +
							"and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
					pst.setInt(1, uF.parseToInt(getLeaveEncashId()));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setInt(3, uF.parseToInt(getUserType()));
                    System.out.println("WORK_FLOW_LEAVE_ENCASH pst====>"+pst);
					rs = pst.executeQuery();
					int work_id=0;
					while(rs.next()) {
						work_id=rs.getInt("work_flow_id");
						break;
					}
		            rs.close();
		            pst.close();
					
					pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id=?");
					pst.setInt(1, uF.parseToInt(getApproveStatus()));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(3, getmReason());
					pst.setInt(4, work_id);
					pst.executeUpdate();	
					 System.out.println("update work_flow_details pst====>"+pst);
					pst.close();
					
					boolean flag=true;
					pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' " +
							" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
							" and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' and member_position not in " +
							" (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' )) " +
							" order by work_flow_id");
					pst.setInt(1, uF.parseToInt(getLeaveEncashId()));
					pst.setInt(2, uF.parseToInt(getLeaveEncashId()));
					pst.setInt(3, uF.parseToInt(getLeaveEncashId()));
					System.out.println("flag work_flow_details pst====>"+pst);

					rs = pst.executeQuery();
					while(rs.next()) {
						flag=false;
					}
		            rs.close();
		            pst.close();
		        	System.out.println("flag ====>"+flag);
					if(flag) {				
						pst = con.prepareStatement("update emp_leave_encashment set is_approved = ?,approved_by = ?,approved_date = ?,paid_from = ?,paid_to = ?,paycycle = ?,approve_reason=?  where leave_encash_id = ?");
						pst.setInt(1, uF.parseToInt(getApproveStatus()));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
						pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
						pst.setInt(6, uF.parseToInt(strPaycyle));
						pst.setString(7, getmReason());
						pst.setInt(8, uF.parseToInt(getLeaveEncashId()));
						pst.executeUpdate();
			            pst.close();
					} else if(uF.parseToInt(getApproveStatus()) == -1) {				
						pst = con.prepareStatement("update emp_leave_encashment set is_approved = ?,approved_by = ?,approved_date = ?,paid_from = ?,paid_to = ?,paycycle = ?,approve_reason=?  where leave_encash_id = ?");
						pst.setInt(1, uF.parseToInt(getApproveStatus()));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
						pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
						pst.setInt(6, uF.parseToInt(strPaycyle));
						pst.setString(7, getmReason());
						pst.setInt(8, uF.parseToInt(getLeaveEncashId()));
						pst.executeUpdate();
			            pst.close();
					}
				}
			} else {
				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
					pst = con.prepareStatement("update emp_leave_encashment set is_approved = ?, approved_by = ?, approved_date = ?, " +
							"paid_from = ?, paid_to = ?, paycycle = ?, approve_reason=? where leave_encash_id = ?");
					pst.setInt(1, uF.parseToInt(getApproveStatus()));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setInt(6, uF.parseToInt(strPaycyle));
					pst.setString(7, getmReason());
					pst.setInt(8, uF.parseToInt(getLeaveEncashId()));
					pst.executeUpdate();
					System.out.println("emp_leave_encashment pst ====>"+pst);
		            pst.close();
				}
			}
			
			String strApproveDeny = "approved";
			if(getApproveStatus()!=null && getApproveStatus().equals("-1")) {
				strApproveDeny = "denied";
			}
			
			updateLeaveBalance(con,CF,uF,strEmpId,leaveTypeId,noDays);
			String strDomain = request.getServerName().split("\\.")[0];
			/*UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(strEmpId);
			userAlerts.set_type(LEAVE_ENCASH_APPROVAL_ALERT);
			userAlerts.setStatus(INSERT_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();*/
			String alertData = "<div style=\"float: left;\"> Your Leave Encashment for "+noDays+" has been "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
			String alertAction = "MyPay.action?pType=WR&callFrom=MyLeaveEncashment";
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(getEmpId());
			userAlerts.setStrData(alertData);
			userAlerts.setStrAction(alertAction);
			userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
			userAlerts.setStatus(INSERT_WR_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
			getStatusMessage(uF.parseToInt(getApproveStatus()));
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void updateLeaveBalance(Connection con, CommonFunctions CF, UtilityFunctions uF, String strEmpId, String leaveTypeId,String noDays) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			if(uF.parseToInt(leaveTypeId)>0){
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				
				pst = con.prepareStatement("select elt.min_leave_encashment,elt.encashment_applicable,elt.encashment_times,elt.max_leave_encash,is_carryforward,effective_date_type from leave_type lt, emp_leave_type elt" +
					" where lt.leave_type_id = elt.leave_type_id and lt.leave_type_id = ? and elt.level_id=? order by entrydate desc limit 1");
				pst.setInt(1, uF.parseToInt(leaveTypeId));
				pst.setInt(2, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
				rs = pst.executeQuery();
//				System.out.println("pst=="+pst);
				double dblMinLeavesForEncashment = 0;
				int encashApplicable=0;
				int encashNoOfTimes=0;
				boolean isCarryforward = false;
				String effective_date_type="";
				double dblMaxLeavesForEncashment = 0;
				while(rs.next()){
					dblMinLeavesForEncashment = rs.getDouble("min_leave_encashment");
					encashApplicable = rs.getInt("encashment_applicable");
					encashNoOfTimes = rs.getInt("encashment_times");
					isCarryforward = uF.parseToBoolean(rs.getString("is_carryforward"));
					effective_date_type = rs.getString("effective_date_type");
					dblMaxLeavesForEncashment = rs.getDouble("max_leave_encash");
				}
	            rs.close();
	            pst.close();
				
				if(isCarryforward || encashApplicable == 2) {	
					String effectiveDate="";
					
					if(effective_date_type.equals("CY")){
						int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
						effectiveDate = "01/01/"+nCurrentYear;
					} else {
						String strDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
						String[] arrDate = CF.getFinancialYear(con, strDate, CF, uF);
						int nCurrentYear = uF.parseToInt(uF.getDateFormat(arrDate[0], DATE_FORMAT, "yyyy"));
						effectiveDate = "01/04/"+nCurrentYear;
					}
					
					pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
		            		"and register_id >=(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
		            		"and leave_type_id=?) and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
		            		"and leave_type_id=? and  _type='C') and  _type='C' ");
		            pst.setInt(1, uF.parseToInt(strEmpId));
		            pst.setInt(2, uF.parseToInt(leaveTypeId));
		            pst.setInt(3, uF.parseToInt(strEmpId));
		            pst.setInt(4, uF.parseToInt(leaveTypeId));
		            pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
		            pst.setInt(6, uF.parseToInt(strEmpId));
		            pst.setInt(7, uF.parseToInt(leaveTypeId));
		            rs = pst.executeQuery();
		            double dblBalance = 0;
		            String balanceDate=null;
		            while (rs.next()) {
		                dblBalance = uF.parseToDouble(rs.getString("balance"));
		                balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
		            }
		            rs.close();
		            pst.close();
		            
		            pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
		            		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
		            		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
		            		"and leave_type_id=? and  _type='C') ");
		            pst.setInt(1, uF.parseToInt(strEmpId));
		            pst.setInt(2, uF.parseToInt(leaveTypeId));
		            pst.setInt(3, uF.parseToInt(strEmpId));
		            pst.setInt(4, uF.parseToInt(leaveTypeId));
		            pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
		            pst.setInt(6, uF.parseToInt(strEmpId));
		            pst.setInt(7, uF.parseToInt(leaveTypeId));
		            rs = pst.executeQuery();
		            while (rs.next()) {
		                dblBalance += uF.parseToDouble(rs.getString("accrued"));
		            }
		            rs.close();
		            pst.close();
		            
		            pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false) and emp_id=? and leave_type_id=? and _date>=?");
		            pst.setInt(1, uF.parseToInt(strEmpId));
		            pst.setInt(2, uF.parseToInt(leaveTypeId));
		            pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
		            rs = pst.executeQuery();
		            double dblPaidBalance = 0;
		            while (rs.next()) {
		            	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
		            }
		            rs.close();
		            pst.close();
		            
		            if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		            	dblBalance = dblBalance - dblPaidBalance;
		            	dblBalance = dblBalance - uF.parseToDouble(noDays);
		            }
					
		            if(dblBalance>0.0d) {
						pst=con.prepareStatement("insert into leave_register1(emp_id,_date,balance,leave_type_id,_type)values(?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDouble(3, dblBalance);
						pst.setInt(4, uF.parseToInt(leaveTypeId));
						pst.setString(5, "C");
//						System.out.println("pst=====>"+pst);
						pst.executeUpdate();
			            pst.close();
		            }
				
				} else {
					String effectiveDate="";
					String effectiveEndDate="";
					
					if(effective_date_type.equals("CY")){
						int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
						effectiveDate = "01/01/"+(nCurrentYear-1);
						effectiveEndDate = "31/12/"+nCurrentYear;
					} else {
						String strDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
						String[] arrDate = CF.getFinancialYear(con, strDate, CF, uF);
						int nCurrentYear = uF.parseToInt(uF.getDateFormat(arrDate[0], DATE_FORMAT, "yyyy"));
						effectiveDate = "01/04/"+(nCurrentYear-1);
						effectiveEndDate = "31/03/"+nCurrentYear;
					}
					
					pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
		            		"and register_id >=(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
		            		"and leave_type_id=?) and _date = (select max(_date) as _date from leave_register1 where _date between ? and ? and emp_id = ? " +
		            		"and leave_type_id=? and  _type='C') and  _type='C' ");
		            pst.setInt(1, uF.parseToInt(strEmpId));
		            pst.setInt(2, uF.parseToInt(leaveTypeId));
		            pst.setInt(3, uF.parseToInt(strEmpId));
		            pst.setInt(4, uF.parseToInt(leaveTypeId));
		            pst.setDate(5, uF.getDateFormat(effectiveDate, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(effectiveEndDate, DATE_FORMAT));
		            pst.setInt(7, uF.parseToInt(strEmpId));
		            pst.setInt(8, uF.parseToInt(leaveTypeId));
		            rs = pst.executeQuery();
		            double dblBalance = 0;
		            String balanceDate=null;
		            while (rs.next()) {
		                dblBalance = uF.parseToDouble(rs.getString("balance"));
		                balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
		            }
		            rs.close();
		            pst.close();
		            
		            pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
		            		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
		            		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date between ? and ? and emp_id = ? " +
		            		"and leave_type_id=? and  _type='C') ");
		            pst.setInt(1, uF.parseToInt(strEmpId));
		            pst.setInt(2, uF.parseToInt(leaveTypeId));
		            pst.setInt(3, uF.parseToInt(strEmpId));
		            pst.setInt(4, uF.parseToInt(leaveTypeId));
		            pst.setDate(5, uF.getDateFormat(effectiveDate, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(effectiveEndDate, DATE_FORMAT));
		            pst.setInt(7, uF.parseToInt(strEmpId));
		            pst.setInt(8, uF.parseToInt(leaveTypeId));
		            rs = pst.executeQuery();
		            while (rs.next()) {
		                dblBalance += uF.parseToDouble(rs.getString("accrued"));
		            }
		            rs.close();
		            pst.close();
		            
		            pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and " +
		            		"(is_modify is null or is_modify=false) and emp_id=? and leave_type_id=? and _date  between ? and ?");
		            pst.setInt(1, uF.parseToInt(strEmpId));
		            pst.setInt(2, uF.parseToInt(leaveTypeId));
		            pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
		            pst.setDate(4, uF.getDateFormat(effectiveEndDate, DATE_FORMAT));
		            rs = pst.executeQuery();
		            double dblPaidBalance = 0;
		            while (rs.next()) {
		            	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
		            }
		            rs.close();
		            pst.close();
		            
		            if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		            	dblBalance = dblBalance - dblPaidBalance;
		            	dblBalance = dblBalance - uF.parseToDouble(noDays);
		            }
					
		            if(dblBalance>0.0d){
						pst=con.prepareStatement("insert into leave_register1(emp_id,_date,balance,leave_type_id,_type)values(?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDouble(3, dblBalance);
						pst.setInt(4, uF.parseToInt(leaveTypeId));
						pst.setString(5, "C");
//						System.out.println("pst=====>"+pst);
						pst.executeUpdate();
			            pst.close();
		            }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deleteRequest(){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' and effective_id in (select leave_encash_id from emp_leave_encashment where leave_encash_id = ?)");
			pst.setInt(1, uF.parseToInt(getLeaveEncashId()));
			pst.execute();
            pst.close();
			
			pst = con.prepareStatement("delete from emp_leave_encashment where leave_encash_id = ?");
			pst.setInt(1, uF.parseToInt(getLeaveEncashId()));
			pst.execute();
            pst.close();
			
			getStatusMessage(uF.parseToInt(getApproveStatus()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
//		return nRequisitionId;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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
			request.setAttribute("STATUS_MSG","<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>" /*"<img title=\"Approved\" src=\""+request.getContextPath()+"/images1/icons/approved.png\" border=\"0\">"*/);
			break;
			
		case 2:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pulled\" src=\""+request.getContextPath()+"/images1/icons/pullout.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled\"></i>");
			
			break;
			
		case 3:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Submited\" src=\""+request.getContextPath()+"/images1/icons/re_submit.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Submited\"></i>");
			
			break;
		}
	}
	
	
	public void getNotificationStatusMessage(int nStatus, String str){
		
		switch(nStatus){
		
		case 0:
			request.setAttribute("STATUS_MSG", "<img width=\"20px\" title=\"Pending\" src=\""+request.getContextPath()+"/images1/"+(("E".equalsIgnoreCase(str))?"mail_disbl.png":"mob_disbl.png")+"\" border=\"0\">&nbsp;");
			break;
			
		case 1:
			request.setAttribute("STATUS_MSG", "<img width=\"20px\" title=\"Approved\" src=\""+request.getContextPath()+"/images1/"+(("E".equalsIgnoreCase(str))?"mail_enbl.png":"mob_enbl.png")+"\" border=\"0\">&nbsp;");
			break;
		}
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getLeaveEncashId() {
		return leaveEncashId;
	}

	public void setLeaveEncashId(String leaveEncashId) {
		this.leaveEncashId = leaveEncashId;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
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
	
}
