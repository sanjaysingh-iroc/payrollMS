package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PreAddNewActivity extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<String> alInner = new ArrayList<String>();
	List<Integer> empidlist = new ArrayList<Integer>();

	List<FillServices> serviceList;
	
	HttpSession session1;
	CommonFunctions CF;
	private HttpServletRequest request;
	List<FillEmployee> empNamesList;
	public String execute() {
		session1 = request.getSession();
		CF = (CommonFunctions) session1.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/AddNewActivity.jsp");
		request.setAttribute(TITLE, "Add New Activity");
		serviceList = new FillServices(request).fillServices();
		getEmpList();
//		empNamesList =new FillEmployee().fillEmployeeNameByServiceID(service);
		return SUCCESS;
	}

	public void getEmpList() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from user_details where usertype_id=?");
			pst.setInt(1, 3);
			rs = pst.executeQuery();
			while (rs.next()) {
				alInner.add(rs.getString("username"));
				empidlist.add(rs.getInt("emp_id"));
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

	public List<String> getAlInner() {
		return alInner;
	}

	public void setAlInner(List<String> alInner) {
		this.alInner = alInner;
	}

	public List<Integer> getEmpidlist() {
		return empidlist;
	}

	public void setEmpidlist(List<Integer> empidlist) {
		this.empidlist = empidlist;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
