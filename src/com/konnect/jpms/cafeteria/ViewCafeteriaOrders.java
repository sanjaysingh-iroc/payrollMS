package com.konnect.jpms.cafeteria;

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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewCafeteriaOrders  extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	private String dishId;
	private String strConfirmOrders;
	private static Logger log = Logger.getLogger(ViewCafeteriaOrders.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PViewOrders);
		request.setAttribute(TITLE, TViewOrders);
		
		getCafeteriaOrders(uF);
		
		if(getStrConfirmOrders() !=null) {
			confirmDishOrder(uF);
			return LOAD;
		}
		
		return SUCCESS;
	}
	
	private void confirmDishOrder(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			String[] strConfirm = request.getParameterValues("strConfirm");
			System.out.println();
			if(strConfirm !=null && strConfirm.length >0) {
				String strOrderIds = StringUtils.join(strConfirm,",");
						
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("update dish_order_details set order_status = ?,confirmed_date = ?, confirmed_by = ? where order_id in ("+strOrderIds+")");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, 1);
				pst.setDate(2,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3,uF.parseToInt(strSessionEmpId));
//			    System.out.println("pst-==>"+pst);	
				pst.executeUpdate();
				pst.close();
				
		   }	
//			request.setAttribute("STATUS_MSG", "Confirmed");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getCafeteriaOrders(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, List<String>> hmDishOrders = new HashMap<String, List<String>>();
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id, emp_fname,emp_mname, emp_lname, emp_image,order_id, dish_id, emp_id,"
					+" order_status, dish_quantity, order_date from employee_personal_details epd, dish_order_details d"
					+" where d.emp_id = epd.emp_per_id and order_status = 0 and dish_id = ? order by order_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getDishId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("order_id"));//0
				alInner.add(rs.getString("dish_id"));//1
				alInner.add(uF.showData(rs.getString("emp_id"),"-"));//2
				alInner.add(uF.showData(rs.getString("dish_quantity"),"-"));//3
				alInner.add(uF.getDateFormat(rs.getString("order_date"), DBDATE, CF.getStrReportDateFormat()));//4
				alInner.add(hmEmpNames.get(rs.getString("emp_id")));//5
				
				String strEmpImage = rs.getString("emp_image");
				String empImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					empImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + strEmpImage+"\" />";
				} else if(strEmpImage != null && !strEmpImage.equals("")) {
					empImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_id")+"/"+I_22x22+"/"+strEmpImage+"\" />";
				}
				alInner.add(empImage);//6
				hmDishOrders.put(rs.getString("order_id"),alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmDishOrders",hmDishOrders);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
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

	public String getStrConfirmOrders() {
		return strConfirmOrders;
	}

	public void setStrConfirmOrders(String strConfirmOrders) {
		this.strConfirmOrders = strConfirmOrders;
	}
	
}
