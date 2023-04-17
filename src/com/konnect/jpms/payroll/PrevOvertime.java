package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class PrevOvertime extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null; 
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		viewOvertime(uF);
		
		return loadOvertime();
	}
	
	
	public String loadOvertime(){
		
		return LOAD;
	}
	
	public String viewOvertime(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from overtime_individual_details where emp_id = ? and is_approved=? order by pay_paycycle desc");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, 1);
			rs = pst.executeQuery();

			Map<String, String> hmOvertime = new HashMap<String, String>();
			Map<String, String> hmPaycycle = new HashMap<String, String>();
			List<String> alOvertime = new ArrayList<String>();
			while(rs.next()){
				hmOvertime.put(rs.getString("pay_paycycle"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
				hmPaycycle.put(rs.getString("pay_paycycle"), uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat())+" to  "+uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
				alOvertime.add(rs.getString("pay_paycycle"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOvertime", hmOvertime);
			request.setAttribute("hmPaycycle", hmPaycycle);
			request.setAttribute("alOvertime", alOvertime);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and is_paid = ? and salary_head_id =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setBoolean(2, true);
			pst.setInt(3, OVER_TIME);
			rs = pst.executeQuery();

			Map<String, String> hmPaidOvertime = new HashMap<String, String>();
			while(rs.next()){
				hmPaidOvertime.put(rs.getString("paycycle"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPaidOvertime", hmPaidOvertime);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
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

}
