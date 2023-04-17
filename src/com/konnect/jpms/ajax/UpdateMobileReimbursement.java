package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateMobileReimbursement extends ActionSupport implements IStatements, ServletRequestAware{

	String strSessionEmpId;
	HttpSession session; 
	CommonFunctions CF;
	
	String type;
	String payStatus;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		
		if(getType()!=null && getType().equals("revoke")){
			revokeMobileReimbursement();
		} else {
			if(getRequestid()!=null && getRequestid().length()>0){
				updateMobileReimbursementApproval();	
			}else{
				updateMobileReimbursement();
			}
		}
		return SUCCESS;
	}

	private void revokeMobileReimbursement() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from mobile_reimbursement_individual_details where mobile_reimbursement_id =?");
			pst.setInt(1, uF.parseToInt(getRequestid()));
			pst.execute();
            pst.close();
			request.setAttribute("STATUS_MSG", "<img  style=\"margin-left: 4px;\" width=\"20px\" src=\"images1/icons/icons/undo_icon.png\" title=\"Revoke\"/>");

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be revoked, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	String approval;
	String requestid;
	String emp_id;
	String salary_id;
	String amt;
	String percent;
	String paycycle;
	String count;
	
	public void updateMobileReimbursement(){

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			
			
			String []arrPaycycle=null;
			int nPaycycle = 0;
			if(getPaycycle()!=null){
				arrPaycycle = getPaycycle().split("-");
				nPaycycle = uF.parseToInt(arrPaycycle[2]);
			}

			
			
			con = db.makeConnection(con);
			
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			String strAmount = null;
			if(nSalaryStrucuterType == S_GRADE_WISE){
				String strEmpGradeId = CF.getEmpGradeId(con, getEmp_id());
				pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? " +
						"and salary_head_id = ? and effective_date = (select max(effective_date) " +
						"from emp_salary_details where emp_id = ? and effective_date <= ? " +
						"and is_approved=true and grade_id=?) and grade_id=?");
				pst.setInt(1, uF.parseToInt(getEmp_id()));
				pst.setInt(2, uF.parseToInt(getSalary_id()));
				pst.setInt(3, uF.parseToInt(getEmp_id()));
				pst.setDate(4, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strEmpGradeId));
				pst.setInt(6, uF.parseToInt(strEmpGradeId));
				rs = pst.executeQuery();
				while(rs.next()){
					strAmount = rs.getString("amount");
				}
	            rs.close();
	            pst.close();
			} else {	
				String strEmpLevelId = CF.getEmpLevelId(con, getEmp_id());
				pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? " +
						"and salary_head_id = ? and effective_date = (select max(effective_date) " +
						"from emp_salary_details where emp_id = ? and effective_date <= ? " +
						"and is_approved=true and level_id=?) and level_id=?");
				pst.setInt(1, uF.parseToInt(getEmp_id()));
				pst.setInt(2, uF.parseToInt(getSalary_id()));
				pst.setInt(3, uF.parseToInt(getEmp_id()));
				pst.setDate(4, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strEmpLevelId));
				pst.setInt(6, uF.parseToInt(strEmpLevelId));
				rs = pst.executeQuery();
				while(rs.next()){
					strAmount = rs.getString("amount");
				}
	            rs.close();
	            pst.close();
			}
			
			double dblAmount = 0;
			if(uF.parseToDouble(getPercent())>0){
				dblAmount = uF.parseToDouble(getPercent()) * uF.parseToDouble(strAmount)  / 100;
			}else{
				dblAmount = uF.parseToDouble(getAmt());
			}
			
			pst = con.prepareStatement("insert into mobile_reimbursement_individual_details (emp_id, pay_paycycle, percent, salary_head_id, amount, pay_amount, added_by,  entry_date, paid_from, paid_to, is_approved) values (?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setInt(2, nPaycycle);
			pst.setDouble(3, uF.parseToDouble(getPercent()));
			pst.setInt(4, uF.parseToInt(getSalary_id()));
			pst.setDouble(5, uF.parseToDouble(getAmt()));
			pst.setDouble(6, dblAmount);
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(9, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
			pst.setInt(11, 2);
			pst.execute();
            pst.close();
			
			pst = con.prepareStatement("select max(mobile_reimbursement_id) as mobile_reimbursement_id from mobile_reimbursement_individual_details " +
					"where emp_id=? and entry_date=? and paid_from = ? and paid_to=?  and pay_paycycle=?");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(3, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
			pst.setInt(5, nPaycycle);
			rs = pst.executeQuery();
			int nMobReimId = 0;
			while(rs.next()){
				nMobReimId = rs.getInt("mobile_reimbursement_id");
			}
            rs.close();
            pst.close();
			
//			request.setAttribute("STATUS_MSG", "<img onclick=\"getContent('myDiv_"+getCount()+"', 'UpdateMobileReimbursement.action?requestid="+nMobReimId+"&approval=1')\" width=\"17px\" src=\"images1/icons/icons/approve_icon.png\"> <img onclick=\"getContent('myDiv_"+getCount()+"', 'UpdateMobileReimbursement.action?requestid="+nIncentiveId+"&approval=-1')\" width=\"17px\" src=\"images1/icons/icons/close_button_icon.png\">");
			String strEmpName = CF.getEmpNameMapByEmpId(con, getEmp_id());
			String approveMsg = "getContent('myDiv_"+getCount()+"', 'UpdateMobileReimbursement.action?requestid="+nMobReimId+"&approval=1&payStatus="+getPayStatus()+"&emp_id="+getEmp_id()+"&count="+getCount()+"')";
			String denyMsg = "getContent('myDiv_"+getCount()+"', 'UpdateMobileReimbursement.action?requestid="+nMobReimId+"&approval=-1&payStatus="+getPayStatus()+"&emp_id="+getEmp_id()+"&count="+getCount()+"')";
			if(uF.parseToBoolean(getPayStatus())){
				approveMsg = "alert('"+strEmpName+"'s payroll has been processed for this paycycle.');";
				denyMsg = "alert('"+strEmpName+"'s payroll has been processed for this paycycle.');";
			}
			

			String status = "<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\""+approveMsg+"\"></i>" +
					"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\""+denyMsg+"\"></i>";

			
			request.setAttribute("STATUS_MSG", status);

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}

	}
	

	public void updateMobileReimbursementApproval(){

		Connection con = null;
		PreparedStatement pst = null, pst1 = null;
		
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update mobile_reimbursement_individual_details set is_approved =?, approved_by=?, approved_date=? where mobile_reimbursement_id =?");
			
			if(uF.parseToInt(getApproval())==1){
				pst.setInt(1, 1);
			}else{
				pst.setInt(1, -1);
			}
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getRequestid()));
			pst.execute();
            pst.close();
						
			String strEmpName = CF.getEmpNameMapByEmpId(con, getEmp_id());
			String msg = "getContent('myDiv_"+getCount()+"', 'UpdateMobileReimbursement.action?requestid="+getRequestid()+"&type=revoke')";
			if(uF.parseToBoolean(getPayStatus())){
				msg = "alert('"+strEmpName+"'s payroll has been processed for this paycycle.');";
			}
			
			if(uF.parseToInt(getApproval())==1){
				String status = /*"<img width=\"17px\" src=\"images1/icons/approved.png\"/>"*/ "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>"  +
						"<img style=\"margin-left: 4px;\" onclick=\""+msg+"\" width=\"20px\" src=\"images1/icons/icons/undo_icon.png\" title=\"Revoke\"/>";
				request.setAttribute("STATUS_MSG", status);
			}else{
				 /*String status = "<img width=\"17px\" src=\"images1/icons/denied.png\"/>" +*/
				String status = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>" +
						"<img style=\"margin-left: 4px;\" onclick=\""+msg+"\" width=\"20px\" src=\"images1/icons/icons/undo_icon.png\" title=\"Revoke\"/>";
				request.setAttribute("STATUS_MSG", status);
			}
			

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}

	}
	
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getSalary_id() {
		return salary_id;
	}

	public void setSalary_id(String salary_id) {
		this.salary_id = salary_id;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getApproval() {
		return approval;
	}


	public void setApproval(String approval) {
		this.approval = approval;
	}


	public String getRequestid() {
		return requestid;
	}


	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}


	public String getCount() {
		return count;
	}


	public void setCount(String count) {
		this.count = count;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getPayStatus() {
		return payStatus;
	}


	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

}