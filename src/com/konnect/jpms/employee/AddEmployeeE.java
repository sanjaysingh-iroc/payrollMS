package com.konnect.jpms.employee;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddEmployeeE extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	boolean isExecute = false;
	String strEdit = null;
	String strUserType = null;
	StringBuilder sbServicesLink = new StringBuilder();
	private static Logger log = Logger.getLogger(AddEmployeeE.class);
	
	public String execute() {
		session = request.getSession();
		session.setAttribute("COST", null);
		session.setAttribute("ServicesLinkNo", null);
		session.setAttribute("ServicesLink", null);
		session.setAttribute("ALLOWANCE", null);

		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, PAddEmployeeP);
		isExecute = true;
		strUserType = (String) session.getAttribute(USERTYPE);

		String strProfile = (String) request.getParameter("P");

		strEdit = request.getParameter("E");
		if (getEmpId() != null) {
			strEdit = getEmpId();
		}
		String strDelete = request.getParameter("D");

		log.debug("strEdit=======>" + strEdit);
		log.debug("=== getEmpPerId ====>" + getEmpPerId());

		if (getEmpPerId() != null && getEmpPerId().length() > 0) {
			updateEmployee();
			request.setAttribute(PAGE, PAddEmployee);
			request.setAttribute(TITLE, TEditEmployee);
			if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
				return REPORT;
			} else {
				return PROFILE;
			}
		}

		if (strEdit != null) {
			viewEmployee(strEdit);

			log.debug("strEdit=  11  ======>" + strEdit);

			if (getEmpId() == null && strEdit != null) {
				setEmpId(strEdit);
			}

			if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(MANAGER))) {
				request.setAttribute(PAGE, PAddEmployeeP);
				request.setAttribute(TITLE, TViewEmployee);
			} else if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)){
				request.setAttribute(PAGE, PAddEmployee);
				request.setAttribute(TITLE, TViewEmployee);
			}
			return SUCCESS;
		}

		return loadEmployee();

	}

	public String loadValidateEmployee() {

		log.debug("=== 1 ====>" + getEmpId());
		log.debug("=== 2 ====>" + strEdit);

		if (getEmpId() != null) {
			request.setAttribute(PAGE, PAddEmployeeP);
		} else {
			request.setAttribute(PAGE, PAddEmployee);
		}

		log.debug("=== 2 ====> getUserName()" + getUserName());
		
		setEmpCode(getEmpCode());
		setUserName(getUserName());
		
		log.debug("=== 2 ====> getUserName()" + getUserName());
		
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();

		return LOAD;
	}

	public String loadEmployee() {
		request.setAttribute(PAGE, PAddEmployee);
		request.setAttribute(TITLE, TAddEmployee);
		UtilityFunctions uF = new UtilityFunctions();

		if (isExecute) {
			setEmpPerId(null);
			setEmpCode(null);
			setEmpFname(null);
			setEmpLname(null);
			setUserName(null);
			setEmpPassword(null);
			setEmpEmail(null);
			setEmpAddress1(null);
			setEmpAddress2(null);
			setCountry(null);
			setState(null);
			setCity(null);
			setEmpPincode(null);
			setEmpContactno(null);
			setEmpCode(null);
		}

		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();

		return LOAD;
	}

	public String updateEmployee() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			int random = new Random().nextInt();
			String filePath = request.getRealPath("/userImages/");
			String fileName = "";

			if (getEmpImage() != null) {

				fileName = random + getEmpImage().getName();
				File fileToCreate = new File(filePath, fileName);
				FileUtils.copyFile(getEmpImage(), fileToCreate);

			}

			if (isDebug)
				log.debug(getEmpImageFileName());

			con = db.makeConnection(con);

			String strEmpType = (String) session.getAttribute(USERTYPE);

			if (fileName.length() > 0) {

				pst = con.prepareStatement(updateEmployeePE);
				pst.setString(1, getEmpFname());
				pst.setString(2, getEmpLname());
				pst.setString(3, getEmpEmail());
				pst.setString(4, getEmpAddress1());
				pst.setString(5, getEmpAddress2());
				pst.setString(6, getCity());
				pst.setInt(7, uF.parseToInt(getState()));
				pst.setInt(8, uF.parseToInt(getCountry()));
				pst.setString(9, getEmpPincode());
				pst.setString(10, getEmpContactno());
				pst.setString(11, fileName);
				pst.setInt(12, uF.parseToInt(getEmpPerId()));

			} else {
				pst = con.prepareStatement(updateEmployeePE1);
				pst.setString(1, getEmpFname());
				pst.setString(2, getEmpLname());
				pst.setString(3, getEmpEmail());
				pst.setString(4, getEmpAddress1());
				pst.setString(5, getEmpAddress2());
				pst.setString(6, getCity());
				pst.setInt(7, uF.parseToInt(getState()));
				pst.setInt(8, uF.parseToInt(getCountry()));
				pst.setString(9, getEmpPincode());
				pst.setString(10, getEmpContactno());
				pst.setInt(11, uF.parseToInt(getEmpPerId()));
			}

			pst.execute();
			pst.close();

			pst = con.prepareStatement(updateUser1E);
			pst.setString(1, getEmpPassword());
			pst.setInt(2, uF.parseToInt(getEmpPerId()));
			pst.setString(3, ((strUserType!=null)?strUserType.toUpperCase():""));
			
			pst.execute();
			pst.close();

			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_UPD_EMPLOYEE_PROFILE);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(getEmpPerId());
			nF.setEmailTemplate(true);
			nF.sendNotifications(); 

			request.setAttribute(MESSAGE, getEmpCode() + " updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewEmployee(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmployeeR1V);
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			int nEmpOffId = 0;
			if (rs.next()) {
				nEmpOffId = rs.getInt("emp_off_id");

				setEmpPerId(rs.getString("emp_per_id"));
				setEmpCode(rs.getString("empcode"));
				setEmpFname(rs.getString("emp_fname"));
				setEmpLname(rs.getString("emp_lname"));
				setEmpEmail(rs.getString("emp_email"));
				setEmpAddress1(rs.getString("emp_address1"));
				setEmpAddress2(rs.getString("emp_address2"));
				setCity(rs.getString("emp_city_id"));
				setState(rs.getString("state_id"));
				setCountry(rs.getString("country_id"));
				setEmpPincode(rs.getString("emp_pincode"));
				setEmpContactno(rs.getString("emp_contactno"));

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(selectUserV3);
			pst.setInt(1, uF.parseToInt(strEdit));
			pst.setString(2, ((strUserType!=null)?strUserType.toUpperCase():""));
			rs = pst.executeQuery();
			if (rs.next()) {
				setUserName(rs.getString("username"));
				setEmpPassword(rs.getString("password"));
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

		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();

		return SUCCESS;

	}

	private String empPerId;
	private String empId;
	private String empCode;
	private String empFname;
	private String empLname;

	private String userName;
	private String empPassword;

	private String empEmail;
	private String empAddress1;
	private String empAddress2;
	private String country;
	private String state;
	private String city;
	private String empPincode;
	private String empContactno;
	private String empImageFileName;
	private File empImage;

	List<FillCountry> countryList;
	List<FillState> stateList;

	public void validate() {
		UtilityFunctions uF = new UtilityFunctions();

		if (getEmpCode() != null && getEmpCode().length() == 0) {
			addFieldError("empCode", "Employee Code is required");
		}
		if (getEmpFname() != null && getEmpFname().length() == 0) {
			addFieldError("empFname", "Employee First Name is required");
		}
		if (getEmpLname() != null && getEmpLname().length() == 0) {
			addFieldError("empLname", "Employee Last Name is required");
		}
		if (getEmpFname() != null && getEmpLname() != null && getEmpFname().length() > 0 && getEmpLname().length() > 0 && getEmpFname().equalsIgnoreCase(getEmpLname())) {
			addFieldError("empFLname", "First name and last name can not be same");
		}
		if (getUserName() != null && getUserName().length() == 0) {
			addFieldError("userName", "UserName Name is required");
		}
		if (getEmpPassword() != null && getEmpPassword().length() == 0) {
			addFieldError("empPassword", "Password is required");
		}
		if (getEmpEmail() != null && getEmpEmail().length() == 0) {
			addFieldError("empEmail", "Employee Email is required");
		} else if (getEmpEmail() != null) {
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher m = p.matcher(getEmpEmail());
			if (!m.matches()) {
				addFieldError("empEmail", "Please enter valid email address");
			}
		}

		if (getEmpAddress1() != null && getEmpAddress1().length() == 0) {
			addFieldError("empAddress1", "Employee Address1 is required");
		}
		if (getCity() != null && getCity().length() == 0) {
			addFieldError("city", "Suburb is required");
		}

		if (getState() != null && uF.parseToInt(getState()) == 0) {
			addFieldError("state", "Select State is required");
		}
		if (getCountry() != null && uF.parseToInt(getCountry()) == 0) {
			addFieldError("country", "Select Country is required");
		}

		loadValidateEmployee();
	}

	public String getEmpPerId() {
		return empPerId;
	}

	public void setEmpPerId(String empPerId) {
		this.empPerId = empPerId;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getEmpFname() {
		return empFname;
	}

	public void setEmpFname(String empFname) {
		this.empFname = empFname;
	}

	public String getEmpLname() {
		return empLname;
	}

	public void setEmpLname(String empLname) {
		this.empLname = empLname;
	}

	public String getEmpAddress1() {
		return empAddress1;
	}

	public void setEmpAddress1(String empAddress1) {
		this.empAddress1 = empAddress1;
	}

	public String getEmpAddress2() {
		return empAddress2;
	}

	public void setEmpAddress2(String empAddress2) {
		this.empAddress2 = empAddress2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmpPincode() {
		return empPincode;
	}

	public void setEmpPincode(String empPincode) {
		this.empPincode = empPincode;
	}

	public String getEmpContactno() {
		return empContactno;
	}

	public void setEmpContactno(String empContactno) {
		this.empContactno = empContactno;
	}

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	public void setEmpImage(File empImage) {
		this.empImage = empImage;
	}

	public File getEmpImage() {
		return empImage;
	}

	public String getEmpImageFileName() {
		return empImageFileName;
	}

	public void setEmpImageFileName(String empImageFileName) {
		this.empImageFileName = empImageFileName;
	}

	public String getEmpPassword() {
		return empPassword;
	}

	public void setEmpPassword(String empPassword) {
		this.empPassword = empPassword;
	}

	public String getUserName() {
		
		log.debug("Getting==>"+userName);
		
		return userName;
	}

	public void setUserName(String userName) {
		log.debug("Setting==>"+userName);
		
		this.userName = userName;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

}
