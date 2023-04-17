package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillUserStatus;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddUser extends ActionSupport implements ServletRequestAware, IStatements, ServletResponseAware {

	private static final long serialVersionUID = 1L;

	String strUserType;
	String strSessionEmpId; 
	HttpSession session;
	CommonFunctions CF; 
	
	public String execute() throws Exception {

		request.setAttribute(PAGE, PAddUser);
		session = request.getSession();		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		String operation = request.getParameter("operation");
		if (operation.equals("D")) {
			return deleteUser();
		}
		if (operation.equals("U")) { 
				return updateUser();
		}
		if (operation.equals("A")) {
				System.out.println("calling insert..");
				return insertUser();
			}
		
		System.out.println("40 : returning success..");
		return SUCCESS;
		
	}

	public String loadLaVidateUser() {
		request.setAttribute(PAGE, PAddUser);
		request.setAttribute(TITLE, TAddUser);
		empCodeList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
		userStatusList = new FillUserStatus().fillUserStatus();
		userTypeList = new FillUserType(request).fillUserType();
		System.out.println("returning LOAD");
		return LOAD;
	}

	public String insertUser() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int generated_user_id;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertUser);
			pst.setString(1, getUserName());
			pst.setString(2, getPassword());
			pst.setInt(3, uF.parseToInt(getUserType()));
			pst.setInt(4, uF.parseToInt(getEmpCode()));
			pst.setString(5, getUserStatus());

			ResultSet rsUserId = pst.executeQuery();
			rsUserId.next();
			generated_user_id = rsUserId.getInt("user_id");

			pst = con.prepareStatement(updateUserStatus);
			pst.setString(1, getUserStatus());
			pst.setInt(2, uF.parseToInt(getEmpCode()));
			pst.execute();
			pst.close();

			setUserId(generated_user_id+"");
			
			System.out.println("Dispatching request");
			RequestDispatcher rd = request.getRequestDispatcher("/jsp/common/AddACL.jsp");
			rd.forward(request, response);
			
			System.out.println("Request dispatched..");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		System.out.println("91: returning SUCCESS");
		return SUCCESS;

	}
	
	public String updateUser() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = uF.parseToInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
//			case 0 : columnName = "username"; break;
//			case 1 : columnName = "password"; break;
//			case 2 : columnName = "usertype_id"; break;
//			case 3 : columnName = "emp_id"; break;
//			case 4 : columnName = "status"; break;
		
		
		case 2 : columnName = "username"; break;
		case 3 : columnName = "password"; break;
		case 5 : columnName = "usertype_id"; break;
		
		case 6 : columnName = "status"; break;
		
		}
		String updateUserDetails = "UPDATE user_details SET "+columnName+"=? WHERE user_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateUserDetails);
			
			
			if(columnId==5)
				pst.setInt(1,uF.parseToInt(request.getParameter("value")));
			else
				pst.setString(1, request.getParameter("value"));
			pst.setInt(2, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			
//			if(columnId==4)
//			{	//status updation in EPD table.
//				pst = con.prepareStatement(updateUserStatus);
//				pst.setString(1, request.getParameter("value"));
//				pst.setInt(2, uF.parseToInt(getEmpCode()));
//				pst.execute();
//				pst.close();
//			}

			
			
			
			AddEmployee objAddEmp = new AddEmployee();
			objAddEmp.request = request;
			objAddEmp.session = session;
			objAddEmp.CF = CF;
			
			if(columnId==5){
				pst = con.prepareStatement("select * from user_details where user_id = ?");
				pst.setInt(1, uF.parseToInt(request.getParameter("id")));
				rs = pst.executeQuery();
				if(rs.next()){
					objAddEmp.insertEmpActivity(con,rs.getString("emp_id"), CF, strSessionEmpId, ACTIVITY_USER_TYPE_CHANGE_ID);
				}
				rs.close();
				pst.close();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;

	}
	
	public String deleteUser() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteUser);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String userId;
	String userType;
	String userName;
	String password;
	String empCode;
	String userStatus;

	List<FillUserType> userTypeList;
	List<FillEmployee> empCodeList;
	List<FillUserStatus> userStatusList;

	public void validate() {

		if (getUserName() != null && getUserName().length() == 0) {
			addFieldError("userName", "User Name is required");
		}
		if (getPassword() != null && getPassword().length() == 0) {
			addFieldError("password", "Password is required");
		}
		if (getUserType() != null && getUserType().equals("0")) {
			addFieldError("userType", "Select User Type is required");
		}
		if (getEmpCode() != null && getEmpCode().equals("0")) {
			addFieldError("empCode", "Select Employee Code is required");
		}
		if (getUserStatus() != null && getUserStatus().equals("0")) {
			addFieldError("userStatus", "User Status is required");
		}
		loadLaVidateUser();

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public List<FillUserType> getUserTypeList() {
		return userTypeList;
	}

	public List<FillEmployee> getEmpCodeList() {
		return empCodeList;
	}

	public List<FillUserStatus> getUserStatusList() {
		return userStatusList;
	}

	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}
}