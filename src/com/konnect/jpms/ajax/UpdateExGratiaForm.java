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

public class UpdateExGratiaForm  extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8362039365216485983L;
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;  
	
	String approval;
	String requestid;
	String emp_id;
	String amt;
	String paycycle;
	String count;
	
	String type;
	String payStatus;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		

		if(getType()!=null && getType().equals("revoke")){
			revokeExGratiaForm();
		} else {
			if(getRequestid()!=null && getRequestid().length()>0){
				updateExGratiaApproval();	
			}else{
				updateExGratia();
			}
		}
		return SUCCESS;
	
	}
	
	private void revokeExGratiaForm() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from emp_exgratia_details where emp_exgratia_id =?");
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

	public void updateExGratia(){

		Connection con = null;
		PreparedStatement pst = null;
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
			
			pst = con.prepareStatement("insert into emp_exgratia_details (emp_id, pay_paycycle, amount, pay_amount, added_by,  entry_date, paid_from, paid_to, is_approved) values (?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setInt(2, nPaycycle);
			pst.setDouble(3, uF.parseToDouble(getAmt()));
			pst.setDouble(4, uF.parseToDouble(getAmt()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(7, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
			pst.setInt(9, 2);
			pst.execute();
            pst.close();
			
			
			pst = con.prepareStatement("select max(emp_exgratia_id) as emp_exgratia_id from emp_exgratia_details where emp_id=? and entry_date=? and paid_from = ? and paid_to=? and pay_paycycle=?");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(3, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
			pst.setInt(5, nPaycycle);
			rs = pst.executeQuery();
			int nEmpExgratiaId = 0;
			while(rs.next()){
				nEmpExgratiaId = rs.getInt("emp_exgratia_id");
			}
            rs.close();
            pst.close();
			
//			request.setAttribute("STATUS_MSG", "<img onclick=\"getContent('myDiv_"+getCount()+"', 'UpdateExGratiaForm.action?requestid="+nEmpExgratiaId+"&approval=1')\" width=\"17px\" src=\"images1/icons/icons/approve_icon.png\"> " +
//					"<img onclick=\"getContent('myDiv_"+getCount()+"', 'UpdateExGratiaForm.action?requestid="+nEmpExgratiaId+"&approval=-1')\" width=\"17px\" src=\"images1/icons/icons/close_button_icon.png\">");
			String strEmpName = CF.getEmpNameMapByEmpId(con, getEmp_id());
			String approveMsg = "getContent('myDiv_"+getCount()+"', 'UpdateExGratiaForm.action?requestid="+nEmpExgratiaId+"&approval=1&payStatus="+getPayStatus()+"&emp_id="+getEmp_id()+"&count="+getCount()+"')";
			String denyMsg = "getContent('myDiv_"+getCount()+"', 'UpdateExGratiaForm.action?requestid="+nEmpExgratiaId+"&approval=-1&payStatus="+getPayStatus()+"&emp_id="+getEmp_id()+"&count="+getCount()+"')";
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
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	public void updateExGratiaApproval(){

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update emp_exgratia_details set is_approved =?, approved_by=?, approved_date=? where emp_exgratia_id =?");
			
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
			String msg = "getContent('myDiv_"+getCount()+"', 'UpdateExGratiaForm.action?requestid="+getRequestid()+"&type=revoke')";
			if(uF.parseToBoolean(getPayStatus())){
				msg = "alert('"+strEmpName+"'s payroll has been processed for this paycycle.');";
			}
			if(uF.parseToInt(getApproval())==1){
				String status = /*"<img width=\"17px\" src=\"images1/icons/approved.png\"/>"*/"<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>"  +
						"<img style=\"margin-left: 4px;\" onclick=\""+msg+"\" width=\"20px\" src=\"images1/icons/icons/undo_icon.png\" title=\"Revoke\"/>";
				request.setAttribute("STATUS_MSG", status);
			}else{
				String status = /*"<img width=\"17px\" src=\"images1/icons/denied.png\"/>"*/ "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>" +
						"<img style=\"margin-left: 4px;\" onclick=\""+msg+"\" width=\"20px\" src=\"images1/icons/icons/undo_icon.png\" title=\"Revoke\"/>";
				request.setAttribute("STATUS_MSG", status);
			}
			

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
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

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
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
