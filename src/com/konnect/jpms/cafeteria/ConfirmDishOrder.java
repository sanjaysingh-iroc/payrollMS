package com.konnect.jpms.cafeteria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ConfirmDishOrder extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String dishId;
	private String orderId;
	
	private static Logger log = Logger.getLogger(ConfirmDishOrder.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PConfirmOrders);
		request.setAttribute(TITLE, "Confirm Order");

		if(uF.parseToInt(getOrderId())>0) {
			confirmDishOrder(uF);
		}
		
		return SUCCESS;
	}
	
	private void confirmDishOrder(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update dish_order_details set order_status = ?,confirmed_date = ?, confirmed_by = ? where order_id =?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, 1);
			pst.setDate(2,uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3,uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getOrderId()));
//			System.out.println("pst==>"+pst);
			pst.executeUpdate();
			pst.close();
		
			request.setAttribute("STATUS_MSG", "Confirmed");
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
