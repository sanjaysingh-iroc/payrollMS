package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateAllowance extends ActionSupport implements IStatements, ServletRequestAware{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	String strEmpId; 
	String salaryHeadId; 
	String paycycle; 
	String amount;
	String conditionId;
	String conditionAmt;
	String logicId; 
	String logicAmt;
	
	String type;
	String requestid;
	String payStatus;
	String approval;
	String productionLineId;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		if(getType()!=null && getType().equals("revoke")){
			revokeAllowance();
		} else {
			if(getRequestid()!=null && getRequestid().length()>0){
				updateAllowanceApproval();	
			}else{
				updateAllowance();
			}
		}
		return SUCCESS;	
	}
	
	private void revokeAllowance() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from allowance_individual_details where allowance_id =?");
			pst.setInt(1, uF.parseToInt(getRequestid()));
			pst.execute();
            pst.close();
            
            pst = con.prepareStatement("delete from allowance_pay_details where allowance_id =?");
			pst.setInt(1, uF.parseToInt(getRequestid()));
			pst.execute();
            pst.close();
            
			request.setAttribute("STATUS_MSG", "<img  style=\"margin-left: 4px;\" width=\"20px\" src=\"images1/icons/icons/undo_icon.png\" title=\"Revoke\"/>");

			
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void updateAllowanceApproval() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update allowance_individual_details set is_approved =?, approved_by=?, approved_date=? where allowance_id =?");
			
			if(uF.parseToInt(getApproval())==1){
				pst.setInt(1, 1);
			}else{
				pst.setInt(1, -1);
			}  
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getRequestid()));
			pst.execute();
			
			
			String strEmpName = CF.getEmpNameMapByEmpId(con, getStrEmpId());
			String msg = "getContent('myDiv_"+getStrEmpId()+"', 'UpdateAllowance.action?requestid="+getRequestid()+"&type=revoke')";
			if(uF.parseToBoolean(getPayStatus())){
				msg = "alert('"+strEmpName+"'s payroll has been processed for this paycycle.');";
			}
			if(uF.parseToInt(getApproval())==1){
				/*String status = "<img width=\"17px\" src=\"images1/icons/approved.png\"/>" + */
				String status = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>" +
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
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void updateAllowance() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			String []arrPaycycle=null;
			int nPaycycle = 0;
			if(getPaycycle()!=null){
				arrPaycycle = getPaycycle().split("-");
				nPaycycle = uF.parseToInt(arrPaycycle[2]);
			}
			
			pst = con.prepareStatement("insert into allowance_individual_details (emp_id, pay_paycycle, salary_head_id, amount, pay_amount, added_by," +
					"entry_date, paid_from, paid_to, is_approved,production_line_id) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, nPaycycle);
			pst.setInt(3, uF.parseToInt(getSalaryHeadId()));
			pst.setDouble(4, uF.parseToDouble(getAmount()));
			pst.setDouble(5, uF.parseToDouble(getAmount()));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(8, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
			pst.setDate(9, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
			pst.setInt(10, 2);
			pst.setInt(11, uF.parseToInt(getProductionLineId()));
			int x = pst.executeUpdate();
            pst.close();
            
            if(x > 0){
            	pst = con.prepareStatement("select max(allowance_id) as allowance_id from allowance_individual_details where emp_id=? " +
						"and entry_date=? and paid_from = ? and paid_to=? and pay_paycycle=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(3, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
				pst.setInt(5, nPaycycle);
				rs = pst.executeQuery();
				int nAllowanceId = 0;
				while(rs.next()){
					nAllowanceId = rs.getInt("allowance_id");
				}
		        rs.close();
		        pst.close();
		        
		        if(getConditionId()!=null && !getConditionId().equals("") && !getConditionId().equalsIgnoreCase("NULL") && getConditionAmt()!=null && !getConditionAmt().equals("") && !getConditionAmt().equalsIgnoreCase("NULL")){
		        	if(getLogicId()!=null && !getLogicId().equals("") && !getLogicId().equalsIgnoreCase("NULL") && getLogicAmt()!=null && !getLogicAmt().equals("") && !getLogicAmt().equalsIgnoreCase("NULL")){
		        		List<String> alConditionId = Arrays.asList(getConditionId().split(","));
		        		if(alConditionId == null) alConditionId = new ArrayList<String>();
		        		List<String> alConditionAmt = Arrays.asList(getConditionAmt().split(","));
		        		if(alConditionAmt == null) alConditionAmt = new ArrayList<String>();
		        		List<String> alLogicId = Arrays.asList(getLogicId().split(","));
		        		if(alLogicId == null) alLogicId = new ArrayList<String>();
		        		List<String> alLogicAmt = Arrays.asList(getLogicAmt().split(","));
		        		if(alLogicAmt == null) alLogicAmt = new ArrayList<String>();
		        		
		        		for(int i = 0; i < alConditionId.size(); i++){
		        			int nConditionId = uF.parseToInt(alConditionId.get(i));
		        			double dblConditionAmt = uF.parseToDouble(alConditionAmt.get(i));
		        			
		        			pst = con.prepareStatement("insert into allowance_pay_details (allowance_id, condition_id, amount)" +
		        					"values(?,?,?)");
							pst.setInt(1, nAllowanceId);
							pst.setInt(2, nConditionId);
							pst.setDouble(3, dblConditionAmt);
							pst.execute();
				            pst.close();
		        		}
		        		
		        		for(int i = 0; i < alLogicId.size(); i++){
		        			int nLogicId = uF.parseToInt(alLogicId.get(i));
		        			double dblLogicAmt = uF.parseToDouble(alLogicAmt.get(i));
		        			
		        			pst = con.prepareStatement("insert into allowance_pay_details (allowance_id, payment_logic_id, amount)" +
		        					"values(?,?,?)");
							pst.setInt(1, nAllowanceId);
							pst.setInt(2, nLogicId);
							pst.setDouble(3, dblLogicAmt);
							pst.execute();
				            pst.close();
		        		}
		        	}
		        }
		        
		        String strEmpName = CF.getEmpNameMapByEmpId(con, getStrEmpId());
				String approveMsg = "getContent('myDiv_"+getStrEmpId()+"', 'UpdateAllowance.action?requestid="+nAllowanceId+"&approval=1&payStatus="+getPayStatus()+"&strEmpId="+getStrEmpId()+"')";
				String denyMsg = "getContent('myDiv_"+getStrEmpId()+"', 'UpdateAllowance.action?requestid="+nAllowanceId+"&approval=-1&payStatus="+getPayStatus()+"&strEmpId="+getStrEmpId()+"')";
				if(uF.parseToBoolean(getPayStatus())){
					approveMsg = "alert('"+strEmpName+"'s payroll has been processed for this paycycle.');";
					denyMsg = "alert('"+strEmpName+"'s payroll has been processed for this paycycle.');";
				}
				
				/*String status = "<img onclick=\""+approveMsg+"\" width=\"17px\" src=\"images1/icons/icons/approve_icon.png\"/> " +
						"<img onclick=\""+denyMsg+"\" width=\"17px\" src=\"images1/icons/icons/close_button_icon.png\"/>";
				
			*/
				
				String status = "<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\""+approveMsg+"\"></i>" +
				"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\""+denyMsg+"\"></i>";



				
				request.setAttribute("STATUS_MSG", status);
            }
			
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getConditionId() {
		return conditionId;
	}

	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}

	public String getConditionAmt() {
		return conditionAmt;
	}

	public void setConditionAmt(String conditionAmt) {
		this.conditionAmt = conditionAmt;
	}

	public String getLogicId() {
		return logicId;
	}

	public void setLogicId(String logicId) {
		this.logicId = logicId;
	}

	public String getLogicAmt() {
		return logicAmt;
	}

	public void setLogicAmt(String logicAmt) {
		this.logicAmt = logicAmt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}


	public String getPayStatus() {
		return payStatus;
	}


	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}


	public String getApproval() {
		return approval;
	}


	public void setApproval(String approval) {
		this.approval = approval;
	}


	public String getProductionLineId() {
		return productionLineId;
	}


	public void setProductionLineId(String productionLineId) {
		this.productionLineId = productionLineId;
	}

}