package com.konnect.jpms.employee;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.LogDetails;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddEditStudentAndTeacher extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strWLocationAccess =  null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;
	
	private String userDateOfBirth;
	private String userId;
	private String operation;
	private String pageType;
	Boolean autoGenerate = false; 
	public String dobYear="1977";
	
	CommonFunctions CF = null;

	public static String MOTHER = "MOTHER";
	public static String FATHER = "FATHER";
	
	private String userCode;
	private String userFname;
	private String userMname;
	private String userLname;
	private String userCoverImageFileName;
	private File userCoverImage;
	
	private String btnSubmit;
	private String userFatherName;
	private String userMotherName;
	private String userName;
	private String userPassword;
	private String userGender;
	private String userEmail;
	private String userEmailSec;
	private String userMobileNo;
	private String skypeId;
	private String userImageFileName;
	private File userImage;
	private String userStartDate;
	private String orgId;
	private String redirectUrl;
	
	private List<FillEmployee> supervisorList;
	private List<FillEmployee> HRList;
	private List<FillEmployee> HodList;
	
	private List<FillUserType> userTypeList;
	private List<FillGender> empGenderList;
//	private List<FillSalutation> salutationList;
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	
//	private String salutation;
	
	private String wLocation;
	private String userType;
	private String supervisor;
	private String hod;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			CF = new CommonFunctions();
			CF.setRequest(request);
		}
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		request.setAttribute(PAGE, "/jsp/employee/AddEditStudentAndTeacher.jsp");
		request.setAttribute(TITLE, "Add User");
		request.setAttribute("dobYear",dobYear);
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>"
				+"<li>Add User</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		UtilityFunctions uF = new UtilityFunctions();
		
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || (!accessEmpList.contains(getUserId()) && uF.parseToInt(getUserId()) > 0)) {
			setUserId(strSessionEmpId);
		}
		
		CF.getFormValidationFields(request, ADD_UPDATE_EMPLOYEE);
		
		getFormValidationFieldWise(uF);
		
		if (getOperation()!=null && getOperation().equals("U") && uF.parseToInt(getUserId()) > 0) {
			request.setAttribute(TITLE, TEditEmployee);
			if(getBtnSubmit() == null) {
				viewUser();
				loadValidateUser();
				return SUCCESS;
			} else if(getBtnSubmit() != null) {
				updateUser();
				return VIEW;
			}
			
		} else if (getOperation()!=null && operation.equals("D")) {
			deleteUser(uF);
			return VIEW;
		} 
		
		if(getBtnSubmit() != null && getBtnSubmit().equals("Submit")) {
			insertUser();
			return VIEW;
		}
		loadValidateUser();
		
		return SUCCESS;
		
	 }
	

	private void deletePendingEmployee(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		
		PreparedStatement pst = null;
		try {
			
			pst = con.prepareStatement(deleteEmployee_P);
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteEmployee_O);
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM employee_activity_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteUserEmp);
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteSkills);
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteHobbies);
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM languages_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM education_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteDocuments);
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_family_members WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_prev_employment WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_references WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private void getFormValidationFieldWise(UtilityFunctions uF) {
		Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
//		String validReqOpt = "";
//		String validAsterix = "";
		
		
//		************************** Step 1 ***********************************
		List<String> salutationValidList = hmValidationFields.get("EMP_SALUTATION"); 
		String salutationValidReqOpt = "";
		String salutationValidAsterix = "";
		if(uF.parseToBoolean(salutationValidList.get(0))) {
			salutationValidReqOpt = "validateRequired";
			salutationValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("salutationValidReqOpt", salutationValidReqOpt);
		request.setAttribute("salutationValidAsterix", salutationValidAsterix);
		
		List<String> empFNameValidList = hmValidationFields.get("EMP_FIRST_NAME"); 
		String empFNameValidReqOpt = "";
		String empFNameValidAsterix = "";
		if(uF.parseToBoolean(empFNameValidList.get(0))) {
			empFNameValidReqOpt = "validateRequired";
			empFNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empFNameValidReqOpt", empFNameValidReqOpt);
		request.setAttribute("empFNameValidAsterix", empFNameValidAsterix);
		
		List<String> empMNameValidList = hmValidationFields.get("EMP_MIDDLE_NAME"); 
		String empMNameValidReqOpt = "";
		String empMNameValidAsterix = "";
		if(uF.parseToBoolean(empMNameValidList.get(0))) {
			empMNameValidReqOpt = "validateRequired";
			empMNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empMNameValidReqOpt", empMNameValidReqOpt);
		request.setAttribute("empMNameValidAsterix", empMNameValidAsterix);
		
		List<String> empLNameValidList = hmValidationFields.get("EMP_LAST_NAME"); 
		String empLNameValidReqOpt = "";
		String empLNameValidAsterix = "";
		if(uF.parseToBoolean(empLNameValidList.get(0))) {
			empLNameValidReqOpt = "validateRequired";
			empLNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empLNameValidReqOpt", empLNameValidReqOpt);
		request.setAttribute("empLNameValidAsterix", empLNameValidAsterix);
		
		List<String> empPersonalEmailIdValidList = hmValidationFields.get("EMP_PERSONAL_EMAIL_ID"); 
		String empPersonalEmailIdValidReqOpt = "validateEmail";
		String empPersonalEmailIdValidAsterix = "";
		if(uF.parseToBoolean(empPersonalEmailIdValidList.get(0))) {
			empPersonalEmailIdValidReqOpt = "validateEmailRequired";
			empPersonalEmailIdValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPersonalEmailIdValidReqOpt", empPersonalEmailIdValidReqOpt);
		request.setAttribute("empPersonalEmailIdValidAsterix", empPersonalEmailIdValidAsterix);
		
		
		List<String> empMobileNoValidList = hmValidationFields.get("EMP_MOBILE_NO"); 
		String empMobileNoValidReqOpt = "";
		String empMobileNoValidAsterix = "";
		if(uF.parseToBoolean(empMobileNoValidList.get(0))) {
			empMobileNoValidReqOpt = "validateRequired";
			empMobileNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empMobileNoValidReqOpt", empMobileNoValidReqOpt);
		request.setAttribute("empMobileNoValidAsterix", empMobileNoValidAsterix);
		
		
		List<String> empDateOfBirthValidList = hmValidationFields.get("EMP_DATE_OF_BIRTH"); 
		String empDateOfBirthValidReqOpt = "";
		String empDateOfBirthValidAsterix = "";
		if(uF.parseToBoolean(empDateOfBirthValidList.get(0))) {
			empDateOfBirthValidReqOpt = "validateRequired";
			empDateOfBirthValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDateOfBirthValidReqOpt", empDateOfBirthValidReqOpt);
		request.setAttribute("empDateOfBirthValidAsterix", empDateOfBirthValidAsterix);
		
		List<String> empGenderValidList = hmValidationFields.get("EMP_GENDER"); 
		String empGenderValidReqOpt = "";
		String empGenderValidAsterix = "";
		if(uF.parseToBoolean(empGenderValidList.get(0))) {
			empGenderValidReqOpt = "validateRequired";
			empGenderValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empGenderValidReqOpt", empGenderValidReqOpt);
		request.setAttribute("empGenderValidAsterix", empGenderValidAsterix);
		
		
		List<String> empPanNoValidList = hmValidationFields.get("EMP_PAN_NO"); 
		String empPanNoValidReqOpt = "";
		String empPanNoValidAsterix = "";
		if(uF.parseToBoolean(empPanNoValidList.get(0))) {
			empPanNoValidReqOpt = "validateRequired";
			empPanNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPanNoValidReqOpt", empPanNoValidReqOpt);
		request.setAttribute("empPanNoValidAsterix", empPanNoValidAsterix);
		
		
		List<String> empProfilePhotoValidList = hmValidationFields.get("EMP_PROFILE_PHOTO"); 
		String empProfilePhotoValidReqOpt = "";
		String empProfilePhotoValidAsterix = "";
		if(uF.parseToBoolean(empProfilePhotoValidList.get(0))) {
			empProfilePhotoValidReqOpt = "validateRequired";
			empProfilePhotoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empProfilePhotoValidReqOpt", empProfilePhotoValidReqOpt);
		request.setAttribute("empProfilePhotoValidAsterix", empProfilePhotoValidAsterix);
		
	}

	public String loadValidateUser() {
		try {
			FillEmployee fillEmployee = new FillEmployee(request);
			UtilityFunctions uF = new UtilityFunctions();

			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getOrgId());
			userTypeList = new FillUserType(request).fillUserType();
			
			supervisorList = fillEmployee.fillSupervisorNameCode(uF.parseToInt(getUserId()), getOrgId(), null);
			HodList = fillEmployee.fillHODNameCode(""+getUserId(), getOrgId(), uF.parseToInt(getwLocation()), CF);
			
			empGenderList = new FillGender().fillGender(); 
			request.setAttribute("empGenderList", empGenderList);
			request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			
		}
		return LOAD;
	}

	
	public String viewUser() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			setUserPersonalDetails(con, uF);
			setUserOfficialDetails(con, uF);
			setUser(con, uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	public void setUserOfficialDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			pst = con.prepareStatement("SELECT * FROM employee_official_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			rs = pst.executeQuery();
			if (rs.next()) {
				setOrgId(rs.getString("org_id"));
				setwLocation(rs.getString("wlocation_id"));
				setSupervisor(""+rs.getInt("supervisor_emp_id"));
				setHod(rs.getString("hod_emp_id"));
//				setHR(rs.getString("emp_hr"));
				
			}
			rs.close();
			pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private int setUserPersonalDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		int nEmpOffId = 0;
		try {

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, uF.parseToInt(getUserId()));
			rs = pst.executeQuery();
			if (rs.next()) {
				setUserId(rs.getString("emp_per_id"));
				
				setUserCode(rs.getString("empcode"));
//				setSalutation(rs.getString("salutation"));
				setUserFname(rs.getString("emp_fname"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = rs.getString("emp_mname");
					}
				}
				
				setUserMname(strEmpMName);
				
				setUserLname(rs.getString("emp_lname"));
				setUserEmail(rs.getString("emp_email"));
				setUserGender(rs.getString("emp_gender"));
				setUserDateOfBirth(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
//				System.out.println("empGender ===>> " + getEmpGender());
				
				setUserStartDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				request.setAttribute("strImage", rs.getString("emp_image"));
				request.setAttribute("EMPLOYEE_EMAIL", rs.getString("emp_email"));
				request.setAttribute("EMPLOYEE_EMAIL2", rs.getString("emp_email_sec"));
				
				if(rs.getString("emp_date_of_birth") != null && !rs.getString("emp_date_of_birth").equals("")) {
					dobYear = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy");
					request.setAttribute("dobYear", dobYear);
				}
//				request.setAttribute("strEmpName", uF.showData(rs.getString("emp_fname"),"") +" "+uF.showData(rs.getString("emp_mname"),"") +" "+uF.showData(rs.getString("emp_lname"),"") );
				
				setUserEmailSec(rs.getString("emp_email_sec"));
				setSkypeId(rs.getString("skype_id"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from emp_family_members where emp_id=?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("member_type") !=null && rs.getString("member_type").equals("MOTHER")) {
					setUserMotherName(rs.getString("member_name"));
				} else if(rs.getString("member_type") !=null && rs.getString("member_type").equals("FATHER")) {
					setUserFatherName(rs.getString("member_name"));
				}
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nEmpOffId;
	}

	
	public void setUser(Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			pst = con.prepareStatement(selectUserV1);
			pst.setInt(1, uF.parseToInt(getUserId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setUserName(rs.getString("username"));
				setUserPassword(rs.getString("password"));
				setUserType(rs.getString("usertype_id"));
			}
			rs.close();
			pst.close();
					
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void insertUser() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			if(uF.parseToInt(getUserId())==0) {
				setUserId(insertUserPersonalDetails(con, uF, CF));
				insertUser(con, uF, uF.parseToInt(getUserId()));
				if(getUserImage() != null) {
					uploadImage(uF.parseToInt(getUserId()),1);
				}
				
				if(getUserCoverImage() != null) {
					uploadImage(uF.parseToInt(getUserId()),2);
				}
				
				updateUserOfficialDetailsAdmin(con, uF);
				
				if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_NEW_EMPLOYEE, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrEmpId(getUserId()+"");
//					nF.setStrHostAddress(request.getRemoteHost());
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setEmailTemplate(true);
					nF.sendNotifications();
				}
				
			} else {
				updateUserPersonalDetails(con, uF);
				if(getUserImage() != null) {
					uploadImage(uF.parseToInt(getUserId()),1);
				}
				
				if(getUserCoverImage() != null) {
					uploadImage(uF.parseToInt(getUserId()),2);
				}
				updateUserOfficialDetailsAdmin(con, uF);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	public String updateUser() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			if(getUserImage()!=null) {
				uploadImage(uF.parseToInt(getUserId()),1);
			}
			if(getUserImage()!=null) {
				uploadImage(uF.parseToInt(getUserId()),2);
			}
			if(getUserFname()!=null) {
				updateUserPersonalDetails(con, uF);
				updateUserOfficialDetailsAdmin(con, uF);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		} finally {
			db.closeConnection(con);
		}
		return VIEW;
	}
	

	public void updateUserOfficialDetailsAdmin(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			int updateCnt=0;
			if (getUserMotherName() != null && getUserMotherName().length() > 0) {
				pst = con.prepareStatement("UPDATE emp_family_members SET member_name=?, member_gender=?,member_marital=? WHERE emp_id=? and member_type=?");
				pst.setString(1, getUserMotherName());
				pst.setString(2, "F");
				pst.setString(3, "M");
				pst.setInt(4, uF.parseToInt(getUserId()));
				pst.setString(5, MOTHER);
				// log.debug("pst=>"+pst);
				updateCnt = pst.executeUpdate();
				pst.close();
				
				if (updateCnt == 0) {
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_gender,member_marital, emp_id)"
						+ " VALUES (?,?,?,?, ?)");
					pst.setString(1, MOTHER);
					pst.setString(2, getUserMotherName());
					pst.setString(3, "F");
					pst.setString(4, "M");
					pst.setInt(5, uF.parseToInt(getUserId()));
					int x = pst.executeUpdate();
					pst.close();
				}
			}
			
			updateCnt=0;
			if (getUserFatherName() != null && getUserFatherName().length() > 0) {
				pst = con.prepareStatement("UPDATE emp_family_members SET member_name=?, member_gender=?,member_marital=? WHERE emp_id=? and member_type=?");
				pst.setString(1, getUserFatherName());
				pst.setString(2, "M");
				pst.setString(3, "M");
				pst.setInt(4, uF.parseToInt(getUserId()));
				pst.setString(5, FATHER);
				// log.debug("pst=>"+pst);
				updateCnt = pst.executeUpdate();
				pst.close();
				
				if (updateCnt == 0) {
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_gender,member_marital, emp_id)"
						+ "VALUES (?,?,?,?, ?)");
					pst.setString(1, FATHER);
					pst.setString(2, getUserFatherName());
					pst.setString(3, "M");
					pst.setString(4, "M");
					pst.setInt(5, uF.parseToInt(getUserId()));
					int x = pst.executeUpdate();
					pst.close();
				}
			}
			
			int nSuperWiserId = 0;
			pst = con.prepareStatement("select supervisor_emp_id from employee_official_details WHERE emp_id=?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			rs = pst.executeQuery();
			while(rs.next()){
				nSuperWiserId = rs.getInt("supervisor_emp_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("UPDATE employee_official_details SET depart_id=?,supervisor_emp_id=?,hod_emp_id=?,service_id=?," +
				"wlocation_id=?,emptype=?,grade_id=?,org_id=?,emp_hr=?,emp_contractor=?,corporate_mobile_no=?," +
				"corporate_desk=?,biometrix_id=?,is_roster=?,paycycle_duration=? WHERE emp_id=?");
			pst.setInt(1, 0);//uF.parseToInt(getDepartment())
			pst.setInt(2, uF.parseToInt(getSupervisor()));
			pst.setInt(3, uF.parseToInt(getHod()));
//			setHod(rs.getString("supervisor_emp_id"));
			pst.setString(4, ""); //sbServices.toString()
			pst.setInt(5, uF.parseToInt(getwLocation()));
			pst.setString(6, ""); //getEmpType()
			pst.setInt(7, 0); //uF.parseToInt(getEmpGrade())
			pst.setInt(8, uF.parseToInt(getOrgId()));
			pst.setInt(9, 0); //uF.parseToInt(getHR())
			pst.setInt(10, 0); //uF.parseToInt(getEmpContractor())
			pst.setString(11, ""); //getEmpCorporateMobileNo()
			pst.setString(12, ""); //getEmpCorporateDesk()
			pst.setInt(13, 0); //uF.parseToInt(getBioId())
			pst.setBoolean(14, false);
			pst.setString(15, ""); //getStrPaycycleDuration()
			pst.setInt(16, uF.parseToInt(getUserId()));
//			System.out.println("Update Pst =====> " + pst);
			int cnt = pst.executeUpdate();
			pst.close();
//			System.out.println("cnt =====> " + cnt);
			
// -------------------------------- Clock On/Off Access Control Start ----------------------------------------			
			/*pst = con.prepareStatement("select * from emp_clock_on_off_access where emp_id=?");
			pst.setInt(1, uF.parseToInt(getUserId()));
			rs = pst.executeQuery();
			boolean accessFlag = false;
			while (rs.next()) {
				accessFlag = true;
			}
			rs.close();
			pst.close();
			
			if(!accessFlag) {
				pst = con.prepareStatement("insert into emp_clock_on_off_access (emp_id,is_web_access,is_mobile_access,is_biomatric_access) values(?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getUserId()));
				pst.setBoolean(2, true);
				pst.setBoolean(3, true);
				pst.setBoolean(4, true);
				pst.executeUpdate();
				pst.close();
			}*/
// -------------------------------- Clock On/Off Access Control End ----------------------------------------
			
			setUserName(getUserEmail());
			pst = con.prepareStatement("update user_details set username=?, usertype_id=? where emp_id=?");
			pst.setString(1, getUserName());
			pst.setInt(2, uF.parseToInt(getUserType()));
			pst.setInt(3, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void updateUserPersonalDetails(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		try {
			String strEmpStatus = PERMANENT;
			pst = con.prepareStatement("UPDATE employee_personal_details SET empcode=?, emp_fname=?, emp_mname=?, emp_lname=?, emp_email=?," +
				" salutation=?, emp_gender=?,emp_date_of_birth=?, joining_date=?,skype_id=?, emp_email_sec=?, emp_status=? WHERE emp_per_id=?");
			pst.setString(1, getUserCode()); //uF.showData(getEmpCodeAlphabet(), "")+uF.showData(getEmpCodeNumber(),"")
			pst.setString(2, getUserFname());
			pst.setString(3, getUserMname());
			pst.setString(4, getUserLname());
			pst.setString(5, getUserEmail());
			pst.setString(6, ""); //getSalutation()
			pst.setString(7, getUserGender());
			pst.setDate(8, uF.getDateFormat(getUserDateOfBirth(), DATE_FORMAT));
			pst.setDate(9, uF.getDateFormat(getUserStartDate(), DATE_FORMAT));
			pst.setString(10, getSkypeId());
			pst.setString(11, getUserEmailSec());
			pst.setString(12, strEmpStatus);
			pst.setInt(13, uF.parseToInt(getUserId()));
			pst.executeUpdate();
			pst.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void uploadImage(int empId2,int type) {
		try {
			if(type == 1) {
				UploadImage uI = new UploadImage();
				uI.setServletRequest(request);
				uI.setImageType("EMPLOYEE_IMAGE");
				uI.setEmpImage(getUserImage());
				uI.setEmpImageFileName(getUserImageFileName());
				uI.setEmpId(empId2+"");
				uI.setCF(CF);
				uI.upoadImage();
			}else if(type == 2) {
				UploadImage uI = new UploadImage();
				uI.setServletRequest(request);
				uI.setImageType("EMPLOYEE_COVER_IMAGE");
				uI.setEmpImage(getUserCoverImage());
				uI.setEmpImageFileName(getUserCoverImageFileName());
				uI.setEmpId(empId2+"");
				uI.setCF(CF);
				uI.upoadImage();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	public void insertUser(Connection con, UtilityFunctions uF, int userId) {
		PreparedStatement pst = null;
		try {
//			Map<String,String> userPresent= CF.getUsersMap(con);
//			AddEmployeeMode aE = new AddEmployeeMode();
//			aE.setServletRequest(request);
//			aE.CF = CF;
//			aE.session = session;
//			aE.setFname(getUserFname());
//			aE.setMname(getUserMname());
//			aE.setLname(getUserLname());
//			String username = aE.getUserName(userPresent);
			setUserName(getUserEmail());
			SecureRandom random = new SecureRandom();
			String password = new BigInteger(130, random).toString(32).substring(5, 13);
			pst = con.prepareStatement(insertUser);
//			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			pst.setString(1, getUserName());
			pst.setString(2, password);
//			pst.setInt(3, uF.parseToInt(hmUserTypeId.get(EMPLOYEE)));
			pst.setInt(3, uF.parseToInt(getUserType()));
			pst.setInt(4, userId);
			pst.setString(5, "ACTIVE");
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public String insertUserPersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {
		PreparedStatement pst = null;
		ResultSet rs =null;
		String empPerId = "";
		try {
			
			String strEmpStatus = PERMANENT;
			
			pst = con.prepareStatement("INSERT INTO employee_personal_details (empcode, emp_fname,emp_mname, emp_lname, emp_email, emp_status, " +
				" salutation, emp_gender,emp_date_of_birth , joining_date, is_alive,emp_filled_flag,emp_filled_flag_date, emp_entry_date," +
				"emp_address1,is_one_step,skype_id, emp_email_sec) " +
				"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?) ");
			pst.setString(1, getUserCode()); //uF.showData(getEmpCodeAlphabet(), "")+uF.showData(getEmpCodeNumber(),"")
			pst.setString(2, getUserFname());
			pst.setString(3, getUserMname());
			pst.setString(4, getUserLname());
			pst.setString(5, getUserEmail());
			pst.setString(6, strEmpStatus);
			pst.setString(7, ""); //getSalutation()
			pst.setString(8, getUserGender());
			pst.setDate(9, uF.getDateFormat(getUserDateOfBirth(), DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(getUserStartDate(), DATE_FORMAT));
			pst.setBoolean(11, true);
			pst.setBoolean(12, true);
			pst.setTimestamp(13, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setTimestamp(14, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setString(15, "");
			pst.setBoolean(16, true);
			pst.setString(17, getSkypeId());
			pst.setString(18, getUserEmailSec());
			pst.execute();
			System.out.println("pst ===>> " + pst);
			pst.close();
			
			
			pst = con.prepareStatement(selectMaxEmpId);
			rs = pst.executeQuery();
			while(rs.next()) {
				empPerId = rs.getString(1);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO employee_official_details(emp_id) VALUES (?)");
			pst.setInt(1, uF.parseToInt(empPerId));
			pst.execute();
			pst.close();
			
			session.setAttribute("EMPNAME_P", getUserFname()+" "+ getUserMname()+" "+getUserLname());
			session.setAttribute("EMPID_P", getUserId()+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return empPerId;
	}

	public String deleteUser(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("update employee_personal_details set approved_flag=?, is_alive=?,emp_filled_flag=?,is_delete=? WHERE emp_per_id=? ");
			pst.setBoolean(1, false);
			pst.setBoolean(2, false);
			pst.setBoolean(3, false);
			pst.setBoolean(4, true);
			pst.setInt(5, uF.parseToInt(getUserId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return VIEW;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<FillGender> getEmpGenderList() {
		return empGenderList;
	}

	public void setEmpGenderList(List<FillGender> empGenderList) {
		this.empGenderList = empGenderList;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getSkypeId() {
		return skypeId;
	}

	public void setSkypeId(String skypeId) {
		this.skypeId = skypeId;
	}

	public String getUserDateOfBirth() {
		return userDateOfBirth;
	}

	public void setUserDateOfBirth(String userDateOfBirth) {
		this.userDateOfBirth = userDateOfBirth;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserFname() {
		return userFname;
	}

	public void setUserFname(String userFname) {
		this.userFname = userFname;
	}

	public String getUserMname() {
		return userMname;
	}

	public void setUserMname(String userMname) {
		this.userMname = userMname;
	}

	public String getUserLname() {
		return userLname;
	}

	public void setUserLname(String userLname) {
		this.userLname = userLname;
	}

	public String getUserCoverImageFileName() {
		return userCoverImageFileName;
	}

	public void setUserCoverImageFileName(String userCoverImageFileName) {
		this.userCoverImageFileName = userCoverImageFileName;
	}

	public File getUserCoverImage() {
		return userCoverImage;
	}

	public void setUserCoverImage(File userCoverImage) {
		this.userCoverImage = userCoverImage;
	}

	public String getUserFatherName() {
		return userFatherName;
	}

	public void setUserFatherName(String userFatherName) {
		this.userFatherName = userFatherName;
	}

	public String getUserMotherName() {
		return userMotherName;
	}

	public void setUserMotherName(String userMotherName) {
		this.userMotherName = userMotherName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserEmailSec() {
		return userEmailSec;
	}

	public void setUserEmailSec(String userEmailSec) {
		this.userEmailSec = userEmailSec;
	}

	public String getUserMobileNo() {
		return userMobileNo;
	}

	public void setUserMobileNo(String userMobileNo) {
		this.userMobileNo = userMobileNo;
	}

	public String getUserImageFileName() {
		return userImageFileName;
	}

	public void setUserImageFileName(String userImageFileName) {
		this.userImageFileName = userImageFileName;
	}

	public File getUserImage() {
		return userImage;
	}

	public void setUserImage(File userImage) {
		this.userImage = userImage;
	}

	public String getUserStartDate() {
		return userStartDate;
	}

	public void setUserStartDate(String userStartDate) {
		this.userStartDate = userStartDate;
	}

	public List<FillUserType> getUserTypeList() {
		return userTypeList;
	}

	public void setUserTypeList(List<FillUserType> userTypeList) {
		this.userTypeList = userTypeList;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public List<FillEmployee> getSupervisorList() {
		return supervisorList;
	}

	public void setSupervisorList(List<FillEmployee> supervisorList) {
		this.supervisorList = supervisorList;
	}

	public List<FillEmployee> getHRList() {
		return HRList;
	}

	public void setHRList(List<FillEmployee> hRList) {
		HRList = hRList;
	}

	public List<FillEmployee> getHodList() {
		return HodList;
	}

	public void setHodList(List<FillEmployee> hodList) {
		HodList = hodList;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getHod() {
		return hod;
	}

	public void setHod(String hod) {
		this.hod = hod;
	}

}