package com.konnect.jpms.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillInOut;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillLoanCode;
import com.konnect.jpms.select.FillTimeType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveLoan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strUserType; 
    String strSessionEmpId;
    
    String mReason;
	String type;
	String userType;
	String currUserType;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
	    strSessionEmpId = (String) session.getAttribute(EMPID);
		
		String strLoadApplicationId = (String)request.getParameter("loanAppId");
		String strapprovalStatus = (String)request.getParameter("approvalStatus");
		
		
		approveEmployeeLoan(strLoadApplicationId, strapprovalStatus);
		if(getType()!=null && getType().equals("type")){
			return DASHBOARD;
		}
					
		return SUCCESS;
	}
	
public String approveEmployeeLoan(String strLoadApplicationId, String strapprovalStatus){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
            if (uF.parseToBoolean(CF.getIsWorkFlow())) {
//                pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LOAN+"'" +
//                        " and effective_id=? order by effective_id,member_position");
//                pst.setInt(1, uF.parseToInt(strLoadApplicationId));
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
                	pst = con.prepareStatement(updateLoanDetails2);
        			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
        			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
        			pst.setInt(3, uF.parseToInt(strapprovalStatus));
                    pst.setString(4, getmReason());
        			pst.setInt(5, uF.parseToInt(strLoadApplicationId));
        			pst.execute();
        			pst.close();
        			
        			if(uF.parseToInt(strapprovalStatus)==1){
        				pst = con.prepareStatement(selectLoanApplied);
        				pst.setInt(1, uF.parseToInt(strLoadApplicationId));
        				rs = pst.executeQuery();
        				double dblPrincipalAmt = 0;
        				double dblROI = 0;
        				double dblDuration = 0;
        				while(rs.next()){
        					dblPrincipalAmt = rs.getDouble("amount_paid");
        					dblROI = rs.getDouble("loan_interest");
        					dblDuration = rs.getDouble("duration_months");
        				}
        				rs.close();
        				pst.close();
        				
        				pst = con.prepareStatement(updateLoanApplied1);
//	        				double dblEMI = (uF.getEMI(dblPrincipalAmt, dblROI, dblDuration)/dblDuration);
        				double dblEMI = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
        				pst.setDouble(1, uF.parseToDouble(uF.formatIntoOneDecimal(uF.getEMI(dblPrincipalAmt, dblROI, dblDuration))));
        				pst.setDouble(2, uF.parseToDouble(uF.formatIntoOneDecimal(dblEMI)));
        				pst.setInt(3, uF.parseToInt(strLoadApplicationId));
        				pst.execute();
        				pst.close();
        			}
                } else {
                    pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? " +
                    		"and effective_type='"+WORK_FLOW_LOAN+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(strLoadApplicationId));
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
                    pst.setInt(1, uF.parseToInt(strapprovalStatus));
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
                    pst.setString(4, getmReason());
                    pst.setInt(5, work_id);
                    pst.execute();
        			pst.close();
                    
                    boolean flag = true;
                    pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LOAN+"' " +
                            " and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
                            " and is_approved=0 and effective_type='"+WORK_FLOW_LOAN+"' and member_position not in " +
                            " (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_LOAN+"' )) " +
                            " order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(strLoadApplicationId));
                    pst.setInt(2, uF.parseToInt(strLoadApplicationId));
                    pst.setInt(3, uF.parseToInt(strLoadApplicationId));
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        flag = false;
                    }
                    rs.close();
        			pst.close();
        			
                    if (flag) {
                    	pst = con.prepareStatement(updateLoanDetails2);
            			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
            			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
            			pst.setInt(3, uF.parseToInt(strapprovalStatus));
                        pst.setString(4, getmReason());
            			pst.setInt(5, uF.parseToInt(strLoadApplicationId));
            			pst.execute();
            			pst.close();
            			
            			if(uF.parseToInt(strapprovalStatus)==1){
            				
            				pst = con.prepareStatement(selectLoanApplied);
            				pst.setInt(1, uF.parseToInt(strLoadApplicationId));
            				rs = pst.executeQuery();
            				double dblPrincipalAmt = 0;
            				double dblROI = 0;
            				double dblDuration = 0;
            				while(rs.next()){
            					dblPrincipalAmt = rs.getDouble("amount_paid");
            					dblROI = rs.getDouble("loan_interest");
            					dblDuration = rs.getDouble("duration_months");
            				}
            				rs.close();
            				pst.close();
            				
            				
            				pst = con.prepareStatement(updateLoanApplied1);
//	            				double dblEMI = (uF.getEMI(dblPrincipalAmt, dblROI, dblDuration)/dblDuration);
            				double dblEMI = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
            				pst.setDouble(1, uF.parseToDouble(uF.formatIntoOneDecimal(uF.getEMI(dblPrincipalAmt, dblROI, dblDuration))));
            				pst.setDouble(2, uF.parseToDouble(uF.formatIntoOneDecimal(dblEMI)));
            				pst.setInt(3, uF.parseToInt(strLoadApplicationId));
            				pst.execute();
            				pst.close();
            			}
                    }/*else if (strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
                    	pst = con.prepareStatement(updateLoanDetails2);
            			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
            			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
            			pst.setInt(3, uF.parseToInt(strapprovalStatus));
                        pst.setString(4, getmReason());
            			pst.setInt(5, uF.parseToInt(strLoadApplicationId));
            			pst.execute();
            			pst.close();
            			
            			if(uF.parseToInt(strapprovalStatus)==1){
            				
            				pst = con.prepareStatement(selectLoanApplied);
            				pst.setInt(1, uF.parseToInt(strLoadApplicationId));
            				rs = pst.executeQuery();
            				double dblPrincipalAmt = 0;
            				double dblROI = 0;
            				double dblDuration = 0;
            				while(rs.next()){
            					dblPrincipalAmt = rs.getDouble("amount_paid");
            					dblROI = rs.getDouble("loan_interest");
            					dblDuration = rs.getDouble("duration_months");
            				}
            				rs.close();
            				pst.close();
            				
            				
            				pst = con.prepareStatement(updateLoanApplied1);
            				double dblEMI = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
            				pst.setDouble(1, uF.parseToDouble(uF.formatIntoOneDecimal(uF.getEMI(dblPrincipalAmt, dblROI, dblDuration))));
            				pst.setDouble(2, uF.parseToDouble(uF.formatIntoOneDecimal(dblEMI)));
            				pst.setInt(3, uF.parseToInt(strLoadApplicationId));
            				pst.execute();
            				pst.close();
            			}
                    }*/else if (uF.parseToInt(strapprovalStatus)==-1) {
                    	pst = con.prepareStatement(updateLoanDetails2);
            			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
            			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
            			pst.setInt(3, uF.parseToInt(strapprovalStatus));
                        pst.setString(4, getmReason());
            			pst.setInt(5, uF.parseToInt(strLoadApplicationId));
            			pst.execute();
            			pst.close();
                    }
                } 
            } else {
                if (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
                	pst = con.prepareStatement(updateLoanDetails2);
        			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
        			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
        			pst.setInt(3, uF.parseToInt(strapprovalStatus));
                    pst.setString(4, getmReason());
        			pst.setInt(5, uF.parseToInt(strLoadApplicationId));
        			pst.execute();
        			pst.close();
        			
        			if(uF.parseToInt(strapprovalStatus)==1){
        				
        				pst = con.prepareStatement(selectLoanApplied);
        				pst.setInt(1, uF.parseToInt(strLoadApplicationId));
        				rs = pst.executeQuery();
        				double dblPrincipalAmt = 0;
        				double dblROI = 0;
        				double dblDuration = 0;
        				while(rs.next()){
        					dblPrincipalAmt = rs.getDouble("amount_paid");
        					dblROI = rs.getDouble("loan_interest");
        					dblDuration = rs.getDouble("duration_months");
        				}
        				rs.close();
        				pst.close();
        				
        				pst = con.prepareStatement(updateLoanApplied1);
//	        			double dblEMI = (uF.getEMI(dblPrincipalAmt, dblROI, dblDuration)/dblDuration);
        				double dblEMI = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
        				pst.setDouble(1, uF.parseToDouble(uF.formatIntoOneDecimal(uF.getEMI(dblPrincipalAmt, dblROI, dblDuration))));
        				pst.setDouble(2, uF.parseToDouble(uF.formatIntoOneDecimal(dblEMI)));
        				pst.setInt(3, uF.parseToInt(strLoadApplicationId));
        				pst.execute();
        				pst.close();
        			}
                }
            }
           
            Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
           
            if (uF.parseToInt(strapprovalStatus) == 1){
            	int nEmpId = 0;
            	pst = con.prepareStatement("select emp_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and loan_applied_id=?");
				pst.setInt(1, uF.parseToInt(strLoadApplicationId));
				rs = pst.executeQuery();
				while(rs.next()){
					nEmpId = uF.parseToInt(rs.getString("emp_id"));
				}
            	
				if(nEmpId > 0){
					String strDomain = request.getServerName().split("\\.")[0];
					/* UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(""+nEmpId);
					userAlerts.set_type(LOAN_APPROVAL_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();*/
					
					
					String alertData = "<div style=\"float: left;\">  Your Loan request has been Approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "MyPay.action?pType=WR&callFrom=LoanApplicationReport";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(""+nEmpId);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
				}
            }else if(uF.parseToInt(strapprovalStatus) == -1){

            	int nEmpId = 0;
            	pst = con.prepareStatement("select emp_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and loan_applied_id=?");
				pst.setInt(1, uF.parseToInt(strLoadApplicationId));
				rs = pst.executeQuery();
				while(rs.next()){
					nEmpId = uF.parseToInt(rs.getString("emp_id"));
				}
            	
				if(nEmpId > 0){
					String strDomain = request.getServerName().split("\\.")[0];
					/* UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(""+nEmpId);
					userAlerts.set_type(LOAN_APPROVAL_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();*/
					
					
					String alertData = "<div style=\"float: left;\"> Your Loan request has been Denied by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "MyPay.action?pType=WR&callFrom=LoanApplicationReport";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(""+nEmpId);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
				}
            
            }
			 
			
			
			request.setAttribute("MSG", "");
			
			if(uF.parseToInt(strapprovalStatus)==1){
				request.setAttribute("MSG", "Approved");
			}
			if(uF.parseToInt(strapprovalStatus)==-1){
				request.setAttribute("MSG", "Denied");
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
	
//	public String approveEmployeeLoan(String strLoadApplicationId, String strapprovalStatus){
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			
//			con = db.makeConnection(con);
//			
//			pst = con.prepareStatement(updateLoanDetails2);
//			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(3, uF.parseToInt(strapprovalStatus));
//			pst.setInt(4, uF.parseToInt(strLoadApplicationId));
//			pst.execute();
//			pst.close();
//			
//			if(uF.parseToInt(strapprovalStatus)==1){
//				
//				pst = con.prepareStatement(selectLoanApplied);
//				pst.setInt(1, uF.parseToInt(strLoadApplicationId));
//				rs = pst.executeQuery();
//				double dblPrincipalAmt = 0;
//				double dblROI = 0;
//				double dblDuration = 0;
//				while(rs.next()){
//					dblPrincipalAmt = rs.getDouble("amount_paid");
//					dblROI = rs.getDouble("loan_interest");
//					dblDuration = rs.getDouble("duration_months");
//				}
//				rs.close();
//				pst.close();
//				
//				
//				pst = con.prepareStatement(updateLoanApplied1);
////				double dblEMI = (uF.getEMI(dblPrincipalAmt, dblROI, dblDuration)/dblDuration);
//				double dblEMI = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
//				pst.setDouble(1, uF.parseToDouble(uF.formatIntoOneDecimal(uF.getEMI(dblPrincipalAmt, dblROI, dblDuration))));
//				pst.setDouble(2, uF.parseToDouble(uF.formatIntoOneDecimal(dblEMI)));
//				pst.setInt(3, uF.parseToInt(strLoadApplicationId));
//				pst.execute();
//				pst.close();
//			}
//			 
//			
//			
//			request.setAttribute("MSG", "");
//			
//			if(uF.parseToInt(strapprovalStatus)==1){
//				request.setAttribute("MSG", "Approved");
//			}
//			if(uF.parseToInt(strapprovalStatus)==-1){
//				request.setAttribute("MSG", "Denied");
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace(); 
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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
