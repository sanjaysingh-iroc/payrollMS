package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBreakType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ChangeBreakPolicyType extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;

	private static Logger log = Logger.getLogger(ChangeBreakPolicyType.class);
	
	List<FillBreakType> empBreakTypeList;
	String strDate;
	String empid;
	String serviceId;
	String strAS;
	String strAE;
	String divid;
	String status;
	String typeOfbreak;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		empBreakTypeList = new FillBreakType(request).fillBreakEmpPolicy("1982",CF); 
		
		if(getStatus()!=null && getStatus().equals("change")){
			changeBreakPolicy();
			return SUCCESS;
		}
		
		return LOAD;
		
	}

	private void changeBreakPolicy() {
		
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst=null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmBreakTypeCode = new HashMap<String, String>();
			pst = con.prepareStatement("select * from leave_break_type");
			rs = pst.executeQuery();
			while(rs.next()){
				hmBreakTypeCode.put(rs.getString("break_type_id"), rs.getString("break_type_code"));
			}
			rs.close();
			pst.close();
			
			if(getTypeOfbreak()!=null && !getTypeOfbreak().equals("")){
				pst=con.prepareStatement("update break_application_register set break_type_id=?,is_modify=? where emp_id=? and _date=? ");
				pst.setInt(1, uF.parseToInt(getTypeOfbreak()));
				pst.setBoolean(2, true);
				pst.setInt(3, uF.parseToInt(getEmpid()));
				pst.setDate(4, uF.getDateFormat(getStrDate(), DBDATE));
				int x=pst.executeUpdate();
				pst.close();
				if(x==0){
					pst = con.prepareStatement("insert into break_application_register (_date, emp_id, break_type_id, leave_no, is_paid, balance, _type,is_modify) values (?,?,?,?,?,?,?,?)");
					pst.setDate(1, uF.getDateFormat(getStrDate(), DBDATE));
					pst.setInt(2, uF.parseToInt(getEmpid()));
					pst.setInt(3, uF.parseToInt(getTypeOfbreak()));
					pst.setInt(4, 1);
					pst.setBoolean(5, true);
					pst.setDouble(6, 0);
					pst.setString(7, "IN");
					pst.setBoolean(8, true);
					pst.execute();
					pst.close();
				}
				
				request.setAttribute("STATUS_MSG", uF.showData(hmBreakTypeCode.get(getTypeOfbreak()), ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}

	public List<FillBreakType> getEmpBreakTypeList() {
		return empBreakTypeList;
	}
	public void setEmpBreakTypeList(List<FillBreakType> empBreakTypeList) {
		this.empBreakTypeList = empBreakTypeList;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getStrAS() {
		return strAS;
	}

	public void setStrAS(String strAS) {
		this.strAS = strAS;
	}

	public String getStrAE() {
		return strAE;
	}

	public void setStrAE(String strAE) {
		this.strAE = strAE;
	}

	public String getDivid() {
		return divid;
	}

	public void setDivid(String divid) {
		this.divid = divid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTypeOfbreak() {
		return typeOfbreak;
	}

	public void setTypeOfbreak(String typeOfbreak) {
		this.typeOfbreak = typeOfbreak;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}

}
