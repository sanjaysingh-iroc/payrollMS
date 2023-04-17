package com.konnect.jpms.cafeteria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddEmpFoodRequests extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;

	public CommonFunctions CF;
	
	private String dishId;
	private String[] strEmpIds;
	 
	private List<FillEmployee> empList;
	private String strSubmit;
	
	private static Logger log = Logger.getLogger(AddEmpFoodRequests.class);
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PAddEmpOrders);
		request.setAttribute(TITLE, "Add Orders");
		if(strUserType != null && strUserType.equals(HRMANAGER)) {
			if((String)session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				empList = new FillEmployee(request).fillCafeEmployees(strUserType,(String)session.getAttribute(WLOCATION_ACCESS));
			} else {
				empList = new FillEmployee(request).fillCafeEmployees(strUserType,(String)session.getAttribute(WLOCATIONID));
			}
		}else if(strUserType != null && strUserType.equals(ADMIN)) {
			empList = new FillEmployee(request).fillCafeEmployees(strUserType,"");
		}else {
			empList = new FillEmployee(request).fillCafeEmployees(strUserType,(String)session.getAttribute(WLOCATIONID));
		}
		
		if(getStrSubmit() != null) {
			if(getStrEmpIds() != null && getStrEmpIds().length>0) {
//				System.out.println("getStrEmps==>"+getStrEmpIds());
				addDishOrder(uF);
				return LOAD;
			}
		}
		
		return SUCCESS;
	}
	
	private void addDishOrder(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			for(String empId : getStrEmpIds()) {
			StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("insert into dish_order_details (dish_id, emp_id, order_status, dish_quantity, order_date, confirmed_by,confirmed_date)"
						+" values(?,?,?,?,?,?,?)");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getDishId()));
				pst.setInt(2, uF.parseToInt(empId));
				pst.setInt(3,1);
				pst.setInt(4,1);
				pst.setDate(5,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6,uF.parseToInt(strSessionEmpId));
				pst.setDate(7,uF.getCurrentDate(CF.getStrTimeZone()));
				
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getDishId() {
		return dishId;
	}

	public void setDishId(String dishId) {
		this.dishId = dishId;
	}

	
	public String[] getStrEmpIds() {
		return strEmpIds;
	}

	public void setStrEmpIds(String[] strEmpIds) {
		this.strEmpIds = strEmpIds;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}
	
}
