package com.konnect.jpms.cafeteria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.library.BookReport;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PlaceOrderForDish extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String dishId;
	private String strQuantity;
	private String dishName;
	private String strSubmit;
	
	private static Logger log = Logger.getLogger(PlaceOrderForDish.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, POrderDish);
		request.setAttribute(TITLE, TPlaceOrder);
		
		getDishDetails(uF);
		
		if(uF.parseToInt(getStrQuantity())>0 && getStrSubmit() != null) {
			insertOrderDish(uF);
			return LOAD;
		}
		
		return SUCCESS;
	}
	
	
	private void getDishDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from dish_details where dish_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getDishId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setDishName(rs.getString("dish_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void insertOrderDish(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("insert into dish_order_details (dish_id, emp_id, dish_quantity, order_status, order_date) values (?,?,?,?, ?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getDishId()));
			pst.setInt(2,uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(getStrQuantity()));
			pst.setInt(4, 0);
			pst.setDate(5,uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();
			
			pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
			rs=pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			while(rs.next()) {
				if(!empList.contains(rs.getString("emp_per_id").trim())) {
					empList.add(rs.getString("emp_per_id").trim());	
				}
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
				if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
					
					String alertData = "<div style=\"float: left;\"> A new Cafeteria Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "Cafeteria.action?pType=WR";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empList.get(i));
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(empList.get(i));
//					userAlerts.set_type(FOOD_REQUEST_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	public String getStrQuantity() {
		return strQuantity;
	}

	public void setStrQuantity(String strQuantity) {
		this.strQuantity = strQuantity;
	}

	public String getDishName() {
		return dishName;
	}

	public void setDishName(String dishName) {
		this.dishName = dishName;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}
	
}
