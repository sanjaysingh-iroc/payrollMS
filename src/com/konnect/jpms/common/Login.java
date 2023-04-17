package com.konnect.jpms.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.CronData;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.SearchData;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>
 * Validate a user login.
 * </p>
 */
public class Login extends ActionSupport implements IStatements, ServletRequestAware, ServletResponseAware {
  
	private static final long serialVersionUID = 1L;
 
	HttpSession session;
	String strTimeZone = null;
	private String empId;
	private String userscreen;
	private String product;
	private String loginType;
	private String paycycle;
	private String profileEmpId;
	
	private static Logger log = Logger.getLogger(Login.class);
	 
	 
	public String execute() throws Exception {
		
//		log.debug("Inside execute of Login..");
		UtilityFunctions uF = new UtilityFunctions(); 
		
		session = request.getSession(true);
//		session.setMaxInactiveInterval(30*60);
		String strUserTypeId = (String)session.getAttribute(USERTYPEID);
		String strBaseUserTypeId = (String)session.getAttribute(BASEUSERTYPEID);
		String strNewUserTypeId = (String)request.getParameter("role");
		
		if(session.getAttribute(LOGIN_TYPE) == null) {
			session.setAttribute(LOGIN_TYPE, getLoginType());
		}
		
		if(session.getAttribute(LOGIN_TYPE) != null && getLoginType()==null) {
			setLoginType((String)session.getAttribute(LOGIN_TYPE));
		}
//		System.out.println("PRODUCT_TYPE ===>> " + session.getAttribute(PRODUCT_TYPE));
//		System.out.println("getLoginType ===>> " + getLoginType());
//		String productType = (String)session.getAttribute(PRODUCT_TYPE);
		
		if(getProduct() != null) {
			session.setAttribute(PRODUCT_TYPE, getProduct());
		} else if(session.getAttribute(PRODUCT_TYPE) == null && uF.parseToInt(getLoginType()) == 1) {
			session.setAttribute(PRODUCT_TYPE, "2");
		} else if(session.getAttribute(PRODUCT_TYPE) == null && uF.parseToInt(getLoginType()) == 2) {
			session.setAttribute(PRODUCT_TYPE, "3");
		}
		
		
//		System.out.println("PRODUCT_TYPE after ===>> " + session.getAttribute(PRODUCT_TYPE)); 
//		if(getEmpId()!=null && getEmpId().length()>0) {
		if(uF.parseToInt(getEmpId())>0) {
			if(uF.parseToInt(getLoginType()) == 1) {
				if(selectUserNamePassword()) {
					if(isEmpFilledStatus()) {
						return loadLogin();
					}
				} else {
					return loadLogin();
				}
			} else {
				return loadLogin();
			}
		}
		
//		System.out.println("===>> 1 ");
		
//		if(strUserTypeId!=null){
		if(uF.parseToInt(strUserTypeId) > 0) {
			Connection con=null;
			Database db=new Database();
			db.setRequest(request);
			try{
				con = db.makeConnection(con);
				Map hmUserTypeMap = new CommonFunctions(request).getUserTypeMap(con);
				Map<String, String> hmUserTypeAccess = new CommonFunctions(request).getUserTypeAccessMap(con);
				
				String accessUsertypeIds = hmUserTypeAccess.get(strBaseUserTypeId);
				List<String> alAccessUSertypeIds = new ArrayList<String>();
				if(accessUsertypeIds!=null) {
					alAccessUSertypeIds = Arrays.asList(accessUsertypeIds.split(","));
				}
				if(strNewUserTypeId!=null && alAccessUSertypeIds.contains(strNewUserTypeId)) {
					session.setAttribute(USERTYPE, (String)hmUserTypeMap.get(strNewUserTypeId));
					session.setAttribute(USERTYPEID, strNewUserTypeId);
				}else{
					session.setAttribute(USERTYPE, (String)hmUserTypeMap.get(strUserTypeId));
					session.setAttribute(USERTYPEID, strUserTypeId);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeConnection(con);
			}
			
			if(getUserscreen() != null && getUserscreen().equals("myhome")) {
				session.setAttribute("IS_HOME", "YES");
				return MYHOME;
			} else {
				session.setAttribute("IS_HOME", "NO");
//				System.out.println("getProduct() ===>> " + getProduct());
//				if(getProduct() != null) {
//				System.out.println("===>> 2 ");
				CommonFunctions CF = new CommonFunctions();
				SearchData search = new SearchData();
				search.request = this.request;
				search.session = this.session;
				search.CF = CF;
				search.getSearchingData();
//				System.out.println("===>> 3 ");
//				}
				return DASHBOARD;
			}
//			if((strNewUserTypeId != null && !strNewUserTypeId.equals("3")) || (session.getAttribute(PRODUCT_TYPE) != null && session.getAttribute(PRODUCT_TYPE).equals("3"))) {
//				return DASHBOARD;
//			} else if(getUserscreen() != null && getUserscreen().equals("interviews")) {
//				return DASHBOARD;
//			}
			
		} else if (getUsername() != null) {
			if(uF.parseToInt(getLoginType()) == 1) {
				return validateUser(session);
			} else if(uF.parseToInt(getLoginType()) == 2) {
				return validateCustomerUser(session);
			}
		}
		
		return loadLogin();
  
	}
	
	private boolean isEmpFilledStatus() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rst = null;
		CallableStatement cst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		PreparedStatement pst = null;
		try {
			
			con = db.makeConnection(con);

			/*pst = con.prepareStatement("SELECT * FROM employee_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			*/
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_employee_personal_details(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(getEmpId()));
//			cst.execute();
//			rst = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM employee_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));	
			rst = pst.executeQuery();
			while(rst.next()) {
				
				flag = rst.getBoolean("emp_filled_flag");
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeStatements(cst);
			db.closeConnection(con);
		}
		
		return flag;
		
		
	}

	private boolean selectUserNamePassword() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		CallableStatement cst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		try {
			
			con = db.makeConnection(con);

//			pst = con.prepareStatement(selectUserV1);
//			pst.setInt(1, uF.parseToInt(getEmpId()));
//			rst = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_user_details(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(getEmpId()));
//			cst.execute();
//			rst = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectUserV1);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rst = pst.executeQuery();
			while(rst.next()) {
				flag = true;
				setUsername(rst.getString("username"));
				setPassword(rst.getString("password"));
			}
			rst.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeStatements(cst);
			db.closeConnection(con);
		}
		return flag;
		
		
	}

	public void validate() {
        if (getUsername()!=null && getUsername().length() == 0) {
            addFieldError("username", "Please enter user name");
        } 
        if (getPassword()!=null && getPassword().length() == 0) {
            addFieldError("password", "Please enter password");
        }
    }
	
	public String loadLogin() {
		setUsername("");
		setPassword("");

		return LOGIN;
	}

	
	public String validateCustomerUser(HttpSession session) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rst = null;
		CallableStatement cst = null;
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = new CommonFunctions();
		CF.request = request;
		String strEmpType = null;
		String strEmpId = null;
		boolean isUser = false;
		
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		
		try {
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);

			Date dtCurrentDt = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
			Date dtStartDt = null;
			Date dtEndDt = null;
			boolean isTermsCondition = false;
			boolean isForcePassword = false;
			
			pst = con.prepareStatement("SELECT * FROM user_details_customer udc, user_type ut, client_poc cp WHERE ut.user_type_id = udc.usertype_id and udc.emp_id = cp.poc_id and upper(username)=? and password=? and udc.status = 'ACTIVE'");
			pst.setString(1, getUsername().toUpperCase());
			pst.setString(2, getPassword());
//			System.out.println("pst == >> " + pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				strEmpType = rst.getString("user_type");
				strEmpId = rst.getString("emp_id");
				
				CF.setTermsCondition(rst.getBoolean("is_termscondition"));
				
				dtStartDt = uF.getDateFormat(rst.getString("start_date"), DBDATE);
				dtEndDt = uF.getDateFormat(rst.getString("end_date"), DBDATE);
				isTermsCondition = rst.getBoolean("is_termscondition");
				isForcePassword = rst.getBoolean("is_forcepassword");
				
				session.setAttribute(MENU, PMenu);
				session.setAttribute(SUBMENU, PSubMenuAdmin);
				
				session.setAttribute(BASEUSERTYPEID, rst.getString("user_type_id"));
				session.setAttribute(USERTYPEID, rst.getString("user_type_id"));
				session.setAttribute(BASEUSERTYPE, rst.getString("user_type"));
				session.setAttribute(USERTYPE, rst.getString("user_type"));				
				session.setAttribute(USERNAME, getUsername());
				session.setAttribute(USERID, rst.getString("user_id"));
				session.setAttribute(PROFILE_IMG, rst.getString("contact_photo"));
				session.setAttribute(STATUS, rst.getString("status"));
				session.setAttribute(EMPID, rst.getString("emp_id"));
				session.setAttribute(EMPNAME, rst.getString("contact_fname")+" "+rst.getString("contact_lname"));
				
				session.setAttribute(USERTYPE, CUSTOMER);
				session.setAttribute(USERTYPEID, rst.getString("user_type_id"));
				session.setAttribute("MAILID", rst.getString("contact_fname")+" "+rst.getString("contact_lname"));
				
				isUser = true;
				
				
				pst1 = con.prepareStatement("select cd.org_id from client_poc cp, client_details cd where cp.client_id = cd.client_id and cp.poc_id = ?");
				pst1.setInt(1, rst.getInt("emp_id"));
				rs = pst1.executeQuery();
				int nOrgId = 0;
				while(rs.next()) {
					session.setAttribute(ORGID, rs.getString("org_id"));
					nOrgId = uF.parseToInt(rs.getString("org_id"));
				}
				rs.close();
				pst1.close();
				
				pst1 = con.prepareStatement("select * from org_details where org_id =?");
				pst1.setInt(1, nOrgId);
				rs = pst1.executeQuery();
				while(rs.next()) {
					CF.setStrEmpOrgLogo(CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+rs.getString("org_logo"));
					session.setAttribute("ORG_LOGO", rs.getString("org_logo"));
					
					CF.setStrEmpOrgLogoSmall(CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE_SMALL+"/"+rs.getString("org_logo_small"));
					session.setAttribute("ORG_LOGO_SMALL", rs.getString("org_logo_small"));
				}
				rs.close();
				pst1.close();
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT timezone_region, timezone_country1, timezone_country2 FROM client_poc cp, work_location_info wl, timezones tz where tz.timezone_id = wl.timezone_id and wl.wlocation_id = cp.contact_location_id and poc_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rst = pst.executeQuery();
			
			String strTimezone = null;
			boolean isTimeZone = false;
			while (rst.next()) {
				isTimeZone = true;
				strTimezone = rst.getString("timezone_region")+"/"+rst.getString("timezone_country1")+((rst.getString("timezone_country2")!=null && rst.getString("timezone_country2").length()>1)?"/"+rst.getString("timezone_country2"):"");
				session.setAttribute(O_TIME_ZONE, strTimezone);
				CF.setStrTimeZone(strTimezone);
			}
			rst.close();
			pst.close();
			
			if(!isTimeZone){
				strTimezone = "Asia/Calcutta";
				session.setAttribute(O_TIME_ZONE, strTimezone);
				CF.setStrTimeZone(strTimezone);
			}
			
			if(isUser) {
				pst = con.prepareStatement(selectSettings);
				rst = pst.executeQuery();
				
				while(rst.next()){
					if(rst.getString("options").equalsIgnoreCase(O_TIME_ZONE)){
					}else if(rst.getString("options").equalsIgnoreCase(O_SHORT_CURR)){
						CF.setStrCURRENCY_SHORT(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_LONG_CURR)){
						CF.setStrCURRENCY_FULL(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DATE_FORMAT)){
						CF.setStrReportDateFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_TIME_FORMAT)){
						CF.setStrReportTimeFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DAY_FORMAT)){
						CF.setStrReportDayFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_PAYCYCLE_DURATION)){
						CF.setStrPaycycleDuration(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DISPLAY_PAYCYCLE)){
						CF.setStrDisplayPayCycle(rst.getString("value"));
					}
//					else if(rst.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_START)){
//						CF.setStrFinancialYearFrom(rst.getString("value"));
//					}else if(rst.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_END)){
//						CF.setStrFinancialYearTo(rst.getString("value"));
//					}
					else if(rst.getString("options").equalsIgnoreCase(O_ORG_LOGO)){
						CF.setStrOrgLogo(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ORG_LOGO_SMALL)){
						CF.setStrOrgLogoSmall(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_NAME)){
						CF.setStrOrgName(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ORG_SUB_TITLE)){
						CF.setStrOrgSubTitle(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_EMP_CODE_ALPHA)){
						CF.setStrOEmpCodeAlpha(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_ADDRESS)){
						CF.setStrOrgAddress(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_SALARY_CALCULATION)){
						CF.setStrOSalaryCalculationType(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_LOCAL_HOST)){
						CF.setStrEmailLocalHost(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_STANDARD_FULL_TIME_HOURS)){
						CF.setStrStandardHrs(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ATTENDANCE_INTEGRATED_WITH_ACTIVITY)){
						CF.setStrAttendanceIntegratedWithActivity(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_USERNAME_FORMAT)){
						CF.setStrUserNameFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_COMMON_ATTEN_FORMAT)){
						CF.setStrCommonAttendanceFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_WORKFLOW)){
						CF.setIsWorkFlow(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_BONUS_PAYROLL)){
						CF.setIsBonusPaidWithPayroll(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_SPECIFIC_EMP)){
						CF.setIsSpecificEmp(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_BACKUP_LOCATION)){
						CF.setBackUpLocation(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_PGDUMP_LOCATION)){
						CF.setDumpLocation(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_PAYCYCLE_MONTH_ADJUSTMENT)){
						CF.setIsPaycycleAdjustment(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DOC_RETRIVE_LOCATION)){
						CF.setStrDocRetriveLocation(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DOC_SAVE_LOCATION)){
						CF.setStrDocSaveLocation(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_REMOTE_LOCATION)){
						CF.setIsRemoteLocation(uF.parseToBoolean(rst.getString("value")));
					}else if(rst.getString("options").equalsIgnoreCase(O_EPF_CONDITION_1)){
						CF.setEPF_Condition1(uF.parseToBoolean(rst.getString("value")));
					}else if(rst.getString("options").equalsIgnoreCase(FIXED_MONTH_DAYS)){
						CF.setStrOSalaryCalculationDays(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_EXCEPTION_IS_AUTO_APPROVE)){
						CF.setIsExceptionAutoApprove(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_ARREAR)){
						CF.setIsArrear(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_BREAK_POLICY)){
						CF.setIsBreakPolicy(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(MAX_TIME_LIMIT_OUT)){
						CF.setMaxTimeLimitOUT(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_PROJECT_DOCUMENT_FOLDER)){
						CF.setProjectDocumentFolder(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_RETRIVE_PROJECT_DOCUMENT_FOLDER)){
						CF.setRetriveProjectDocumentFolder(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_BACKUP_RETRIVE_LOCATION)){
						CF.setBackUpRetriveLocation(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_HOST_PORT)){
						CF.setStrHostPort(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_WORKRIG)){
						CF.setWorkRig(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_TASKRIG)){
						CF.setTaskRig(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_CLOUD)){
						CF.setCloud(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_OFFICE_365_SMTP)){
						CF.setOffice365Smtp(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_CLOCK_ON_OFF)){
						CF.setClockOnOff(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_SALARY_STRUCTURE)){
						CF.setStrSalaryStructure(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_RECEIPT)) {
						CF.setIsReceipt(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_HALF_DAY_LEAVE)) {
						CF.setIsHalfDayLeave(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_ROUND_OFF_CONDITION)) {
						CF.setRoundOffCondtion(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_PRODUCTION_LINE)) {
						CF.setIsProductionLine(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_SANDWICH_ABSENT)){
						CF.setIsSandwichAbsent(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_TERMINATE_WITHOUT_FULLANDFINAL)){
						CF.setIsTerminateWithoutFullAndFinal(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_TDS_AUTO_APPROVE)){
						CF.setIsTDSAutoApprove(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_SHOW_TIME_VARIANCE)){
						CF.setIsShowTimeVariance(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_UI_THEME)){
						CF.setStrUI_Theme(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_CAL_LEAVE_IN_ATTENDANCE_DEPENDANT_NO)){
						CF.setIsCalLeaveInAttendanceDependantNo(uF.parseToBoolean(rst.getString("value")));
					}
				}
				rst.close();
				pst.close();
				session.setAttribute(PRODUCT_TYPE, "3");
				
				CF.setStrReportTimeAM_PMFormat("hh:mma");
//				cst.close();
				
				CF.setStrFinancialYearFrom(strFinancialYearStart);
				CF.setStrFinancialYearTo(strFinancialYearEnd);
				
				strTimeZone = (String) session.getAttribute(O_TIME_ZONE);
				session.setAttribute("TODAY", uF.getDateFormat(uF.getCurrentDate(strTimeZone)+"", DBDATE, "EEEE MMM, dd"));
				session.setAttribute(CommonFunctions, CF);
					
				getModulesDetails(con, CF);
				
				String clientIp = request.getRemoteAddr();
				
				if(isRemember) {
						//Code for Remember Me 
						
						Cookie cookie_username = new Cookie ("PAYROLL_USERNAME", username);
						cookie_username.setMaxAge(365 * 24 * 60 * 60);
						cookie_username.setPath(request.getContextPath());
						response.addCookie(cookie_username);
						
						Cookie cookie_password = new Cookie ("PAYROLL_PASSWORD", password);
						cookie_password.setMaxAge(365 * 24 * 60 * 60);
						cookie_password.setPath(request.getContextPath());
						response.addCookie(cookie_password); 
						
						Cookie cookie_login_type = new Cookie ("PAYROLL_LOGIN_TYPE", loginType);
						cookie_login_type.setMaxAge(365 * 24 * 60 * 60);
						cookie_login_type.setPath(request.getContextPath());
						response.addCookie(cookie_login_type); 
//						System.out.println("Setting cookies........");
					}
					
//				con.setAutoCommit(true);
				pst = con.prepareStatement("Insert into login_timestamp (login_timestamp, emp_id, client_ip,session_id) values (?, ?, ?,?)");
				pst.setTimestamp(1, uF.getTimeStamp(""+uF.getCurrentDate(strTimeZone)+uF.getCurrentTime(strTimeZone), DBDATE+DBTIME));
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setString(3, clientIp);
				pst.setString(4, session.getId());
				pst.execute();
				pst.close();
				
//				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
//				boolean flagAssignShiftOnBasisOfRules = uF.parseToBoolean(hmFeatureStatus.get(F_ASSIGN_SHIFT_ON_BASIS_OF_RULES));
				
				SearchData search = new SearchData();
				search.request = this.request;
				search.session = this.session;
				search.CF = CF;
				search.getSearchingData();
				
				String strDomain = request.getServerName().split("\\.")[0];
				CronData cron = new CronData();
				cron.request = this.request;
				cron.session = this.session;
				cron.CF = CF;
				cron.strEmpId = strEmpId;
				cron.strDomain = strDomain;
				cron.setCronData();
				
				cron = new CronData();
				cron.request = this.request;
				cron.session = this.session;
				cron.CF = CF;
				cron.strEmpId = strEmpId;
				cron.strDomain = strDomain;
				cron.setOvertimeMinuteAlertNotification(con,uF);
//				cron.checkTodaysBirthdaysMarriageAndWorkAnniversary(con,uF);
				/*if(flagAssignShiftOnBasisOfRules) {
					cron.assignShiftsOnBasisOfRules(con,uF);
				}*/
				
			}
			
		if(isUser) {
			
			/*if(strEmpType!=null && strEmpType.equals(CUSTOMER)) {
				if(!checkEmployeeApprovedFlag(con, uF, CF, strEmpId)) {
					setEmpId(strEmpId);
					log.debug("Returning notApproved");
					session.setAttribute("isApproved", false);
					return "notapproved";
				}
			}*/
		} else {
			request.setAttribute(MESSAGE, ERRORM+"We could not find a user by this name!"+END);
			return LOGIN;
		}
		
		if(dtStartDt!=null && dtEndDt!=null) {
			CF.setTrial(true);
			
			if(dtCurrentDt.after(dtEndDt) || dtCurrentDt.before(dtStartDt)) {
				request.setAttribute(MESSAGE, E_AccountSuspended);
				return LOGIN;
			}
			if(!isTermsCondition) {
				CF.setTermsCondition(false);
				return "termsconditionscheck";
			} 
		}
		
		if(isForcePassword) {
			CF.setForcePassword(true);
			return "forcechangepassword";
		} 
		
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(cst);
			db.closeConnection(con);
		}
		log.debug("Returning DASHBOARD");
		
		return DASHBOARD;
//		return MYHOME;
	}
	
	
	
	public String validateUser(HttpSession session) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rst = null;
		CallableStatement cst = null;
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = new CommonFunctions();
		CF.request = request;
		String strEmpType = null;
		String strEmpId = null;
		String strWLocationAccess = null;
		String strOrgAccess = null;
		boolean isUser = false;
		
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		
		try {
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);

//			pst = con.prepareStatement(selectUser);
//			pst.setString(1, getUsername().toUpperCase());
//			pst.setString(2, getPassword());
//			rst = pst.executeQuery();
			
			
			Date dtCurrentDt = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
			Date dtStartDt = null;
			Date dtEndDt = null;
			boolean isTermsCondition = false;
			boolean isForcePassword = false;
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_user(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setString(2, getUsername().toUpperCase());
//			cst.setString(3, getPassword());
//			cst.execute();
//			rst = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectUser);
			pst.setString(1, getUsername().toUpperCase());
			pst.setString(2, getPassword());
			rst = pst.executeQuery();
			while (rst.next()) {
				strEmpType = rst.getString("user_type");
				strEmpId = rst.getString("emp_id");
				
				strWLocationAccess = rst.getString("wlocation_id_access");	
				if(strWLocationAccess!=null && strWLocationAccess.lastIndexOf(",")>=0){
//					strWLocationAccess = strWLocationAccess.substring(0, strWLocationAccess.lastIndexOf(","));
					strWLocationAccess = strWLocationAccess.substring(1, strWLocationAccess.length()-1);
				}
				
				strOrgAccess = rst.getString("org_id_access");	
				if(strOrgAccess!=null && strOrgAccess.lastIndexOf(",")>=0){
//					strOrgAccess = strOrgAccess.substring(0, strOrgAccess.lastIndexOf(","));
					strOrgAccess = strOrgAccess.substring(1, strOrgAccess.length()-1);
				}
				
				String strEmpStatus = rst.getString("emp_status");
				String strUserStatus = rst.getString("status");
				
				boolean isApproved = rst.getBoolean("approved_flag");
				boolean isAlive = rst.getBoolean("is_alive");
				boolean isFilled = rst.getBoolean("emp_filled_flag");
				
				CF.setTermsCondition(rst.getBoolean("is_termscondition"));
				
//				PreparedStatement pst = con.prepareStatement("select * from emp_off_board where emp_id =? and approved_2=1 and last_day_date < ?");
//				pst = con.prepareStatement("select * from emp_off_board where emp_id =? and approved_2=1 and last_day_date < ?");
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getCurrentDate("Asia/Calcutta"));
//				ResultSet rs = pst.executeQuery();
//				boolean isInvalid = false;
//				while(rs.next()){
//					isInvalid = true;
//				}
				
				/*if(isInvalid){
					request.setAttribute(MESSAGE, E_ACCESSDENIED);
					return LOGIN;
				}else*/ if(!isAlive){
					request.setAttribute(MESSAGE, E_EmployeeTerminated);
					return LOGIN;
				}else if(!isApproved && !isAlive){
					request.setAttribute(MESSAGE, E_ACCESSDENIED);
					return LOGIN;
				}else if(isApproved && isAlive){
					if(strUserStatus!=null && strUserStatus.equalsIgnoreCase("SUSPENDED")){
						request.setAttribute(MESSAGE, E_AccountSuspended);
						return LOGIN;
					}
				}
				
				dtStartDt = uF.getDateFormat(rst.getString("start_date"), DBDATE);
				dtEndDt = uF.getDateFormat(rst.getString("end_date"), DBDATE);
				isTermsCondition = rst.getBoolean("is_termscondition");
				isForcePassword = rst.getBoolean("is_forcepassword");
				
				pst1 = con.prepareStatement("select * from employee_official_details where emp_id = ?");
				pst1.setInt(1, rst.getInt("emp_id"));
				rs = pst1.executeQuery();
				int nOrgId = 0;
				while(rs.next()) {
					session.setAttribute(WLOCATIONID, rs.getString("wlocation_id"));
					session.setAttribute(WLOCATION_NAME, CF.getWorkLocationNameById(con, rs.getString("wlocation_id")));
					session.setAttribute(DESIGNATION, CF.getEmpDesigMapByEmpId(con, rst.getString("emp_id")));
					session.setAttribute(DEPARTMENT, CF.getDepartMentNameById(con, rs.getString("depart_id")));
					session.setAttribute(DEPARTMENTID, rs.getString("depart_id"));		//added by parvez date: 08-02-2023
					session.setAttribute(ORG_NAME, CF.getOrgNameById(con, rs.getString("org_id")));
					session.setAttribute(ORGID, rs.getString("org_id"));
					nOrgId = uF.parseToInt(rs.getString("org_id"));
					session.setAttribute(RESOURCE_OR_CONTRACTOR, rs.getString("emp_contractor"));
				}
				rs.close();
				pst1.close();
				
				
				pst1 = con.prepareStatement("select * from employee_official_details where supervisor_emp_id = ?");
				pst1.setInt(1, rst.getInt("emp_id"));
				rs = pst1.executeQuery();
				while(rs.next()) {
					session.setAttribute(IS_SUPERVISOR, ""+true);
				}
				rs.close();
				pst1.close();
				
				
				session.setAttribute(MENU, PMenu);
				session.setAttribute(SUBMENU, PSubMenuAdmin);
				
				session.setAttribute(BASEUSERTYPEID, rst.getString("user_type_id"));
				session.setAttribute(USERTYPEID, rst.getString("user_type_id"));
				session.setAttribute(BASEUSERTYPE, rst.getString("user_type"));
				session.setAttribute(USERTYPE, rst.getString("user_type"));				
				session.setAttribute(USERNAME, getUsername());
				session.setAttribute(USERID, rst.getString("user_id"));
				session.setAttribute(PROFILE_IMG, rst.getString("emp_image"));
				session.setAttribute(STATUS, rst.getString("status"));
				session.setAttribute(EMPID, rst.getString("emp_id"));
				session.setAttribute(EMPNAME, rst.getString("emp_fname")+" "+rst.getString("emp_lname"));
				session.setAttribute(WLOCATION_ACCESS, strWLocationAccess);
				session.setAttribute(ORG_ACCESS, strOrgAccess);
//				if(rst.getString("user_type") != null && !rst.getString("user_type").equals(RECRUITER)) {
				session.setAttribute(USERTYPE, EMPLOYEE);
				session.setAttribute(USERTYPEID, "3");
//				}
				session.setAttribute("MAILID", rst.getString("emp_fname")+" "+rst.getString("emp_lname")+"("+rst.getString("empcode")+")");
				
				isUser = true;
				
				pst1 = con.prepareStatement("select * from org_details where org_id =?");
				pst1.setInt(1, nOrgId);
				rs = pst1.executeQuery();
				while(rs.next()) {
//					CF.setStrEmpOrgLogo(request.getContextPath()+"userImages/"+rs.getString("org_logo"));
					CF.setStrEmpOrgLogo(CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+rs.getString("org_logo"));
					session.setAttribute("ORG_LOGO",rs.getString("org_logo"));
					
					CF.setStrEmpOrgLogoSmall(CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE_SMALL+"/"+rs.getString("org_logo_small"));
					session.setAttribute("ORG_LOGO_SMALL", rs.getString("org_logo_small"));
				}
				rs.close();
				pst1.close();
				
			}
			rst.close();
			pst.close();
			
//			cst.close();
			
			
//			pst = con.prepareStatement(selectTimezoneEmp);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rst = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_timezone(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.execute();
//			rst = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectTimezoneEmp);
			pst.setInt(1, uF.parseToInt(strEmpId));
			rst = pst.executeQuery();
			
			String strTimezone = null;
			boolean isTimeZone = false;
			while (rst.next()) {
				isTimeZone = true;
				strTimezone = rst.getString("timezone_region")+"/"+rst.getString("timezone_country1")+((rst.getString("timezone_country2")!=null && rst.getString("timezone_country2").length()>1)?"/"+rst.getString("timezone_country2"):"");
				session.setAttribute(O_TIME_ZONE, strTimezone);
				CF.setStrTimeZone(strTimezone);
				//System.out.println("getStrTimeZone() ===>> " + CF.getStrTimeZone());
			}
			rst.close();
			pst.close();
			
			if(!isTimeZone){
				strTimezone = "Asia/Calcutta";
				session.setAttribute(O_TIME_ZONE, strTimezone);
				CF.setStrTimeZone(strTimezone);
			//	System.out.println("!isTimeZone getStrTimeZone() ===>> " + CF.getStrTimeZone());
			}
			
			if(isUser){
//				pst = con.prepareStatement(selectSettings);
//				rst = pst.executeQuery();
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_settings()}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.execute();
//				rst = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectSettings);
				rst = pst.executeQuery();
				
				while(rst.next()) {
					if(rst.getString("options").equalsIgnoreCase(O_TIME_ZONE)){
//						session.setAttribute(O_TIME_ZONE, rst.getString("value"));
//						System.out.println(O_TIME_ZONE+"=====>"+rst.getString("value"));
//						CF.setStrTimeZone(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_SHORT_CURR)){
						CF.setStrCURRENCY_SHORT(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_LONG_CURR)){
						CF.setStrCURRENCY_FULL(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DATE_FORMAT)){
						CF.setStrReportDateFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_TIME_FORMAT)){
						CF.setStrReportTimeFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DAY_FORMAT)){
						CF.setStrReportDayFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_PAYCYCLE_DURATION)){
						CF.setStrPaycycleDuration(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DISPLAY_PAYCYCLE)){
						CF.setStrDisplayPayCycle(rst.getString("value"));
					}
//					else if(rst.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_START)){
//						CF.setStrFinancialYearFrom(rst.getString("value"));
//					}else if(rst.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_END)){
//						CF.setStrFinancialYearTo(rst.getString("value"));
//					}
					else if(rst.getString("options").equalsIgnoreCase(O_ORG_LOGO)){
						CF.setStrOrgLogo(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ORG_LOGO_SMALL)){
						CF.setStrOrgLogoSmall(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_NAME)){
						CF.setStrOrgName(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ORG_SUB_TITLE)){
						CF.setStrOrgSubTitle(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_EMP_CODE_ALPHA)){
						CF.setStrOEmpCodeAlpha(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_CONTRACTOR_CODE_ALPHA)){
						CF.setStrOContractorCodeAlpha(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_ADDRESS)){
						CF.setStrOrgAddress(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_SALARY_CALCULATION)){
						CF.setStrOSalaryCalculationType(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_LOCAL_HOST)){
						CF.setStrEmailLocalHost(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_STANDARD_FULL_TIME_HOURS)){
						CF.setStrStandardHrs(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_ATTENDANCE_INTEGRATED_WITH_ACTIVITY)){
						CF.setStrAttendanceIntegratedWithActivity(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_USERNAME_FORMAT)){
						CF.setStrUserNameFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_COMMON_ATTEN_FORMAT)){
						CF.setStrCommonAttendanceFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_WORKFLOW)){
						CF.setIsWorkFlow(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_BONUS_PAYROLL)){
						CF.setIsBonusPaidWithPayroll(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_SPECIFIC_EMP)){
						CF.setIsSpecificEmp(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_BACKUP_LOCATION)){
						CF.setBackUpLocation(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_PGDUMP_LOCATION)){
						CF.setDumpLocation(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_PAYCYCLE_MONTH_ADJUSTMENT)){
						CF.setIsPaycycleAdjustment(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DOC_RETRIVE_LOCATION)){
						CF.setStrDocRetriveLocation(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DOC_SAVE_LOCATION)){
						CF.setStrDocSaveLocation(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_REMOTE_LOCATION)){
						CF.setIsRemoteLocation(uF.parseToBoolean(rst.getString("value")));
					}else if(rst.getString("options").equalsIgnoreCase(O_EPF_CONDITION_1)){
						CF.setEPF_Condition1(uF.parseToBoolean(rst.getString("value")));
					}else if(rst.getString("options").equalsIgnoreCase(FIXED_MONTH_DAYS)){
						CF.setStrOSalaryCalculationDays(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_EXCEPTION_IS_AUTO_APPROVE)){
						CF.setIsExceptionAutoApprove(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_IS_ARREAR)){
						CF.setIsArrear(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_BREAK_POLICY)){
						CF.setIsBreakPolicy(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(MAX_TIME_LIMIT_OUT)){
						CF.setMaxTimeLimitOUT(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_PROJECT_DOCUMENT_FOLDER)){
						CF.setProjectDocumentFolder(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_RETRIVE_PROJECT_DOCUMENT_FOLDER)){
						CF.setRetriveProjectDocumentFolder(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_BACKUP_RETRIVE_LOCATION)){
						CF.setBackUpRetriveLocation(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_HOST_PORT)){
						CF.setStrHostPort(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_WORKRIG)){
						CF.setWorkRig(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_TASKRIG)){
						CF.setTaskRig(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_CLOUD)){
						CF.setCloud(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_OFFICE_365_SMTP)){
						CF.setOffice365Smtp(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_CLOCK_ON_OFF)){
						CF.setClockOnOff(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_TERMS_CONDITIONS_TYPE)) {
						CF.setStrTermsConditionsType(rst.getString("value"));
					}  else if(rst.getString("options").equalsIgnoreCase(O_SALARY_STRUCTURE)){
						CF.setStrSalaryStructure(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_RECEIPT)) {
						CF.setIsReceipt(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_HALF_DAY_LEAVE)) {
						CF.setIsHalfDayLeave(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_ROUND_OFF_CONDITION)) {
						CF.setRoundOffCondtion(rst.getString("value")); 
					} else if(rst.getString("options").equalsIgnoreCase(O_PRODUCTION_LINE)) {
						CF.setIsProductionLine(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_SANDWICH_ABSENT)){
						CF.setIsSandwichAbsent(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_TERMINATE_WITHOUT_FULLANDFINAL)){
						CF.setIsTerminateWithoutFullAndFinal(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_TDS_AUTO_APPROVE)){
						CF.setIsTDSAutoApprove(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_SHOW_TIME_VARIANCE)){
						CF.setIsShowTimeVariance(uF.parseToBoolean(rst.getString("value")));
					} else if(rst.getString("options").equalsIgnoreCase(O_UI_THEME)){
						CF.setStrUI_Theme(rst.getString("value"));
					} else if(rst.getString("options").equalsIgnoreCase(O_IS_CAL_LEAVE_IN_ATTENDANCE_DEPENDANT_NO)){
						CF.setIsCalLeaveInAttendanceDependantNo(uF.parseToBoolean(rst.getString("value")));
					}
				}
				rst.close();
				pst.close();
				if(!CF.isWorkRig()) { //uF.parseToInt((String) session.getAttribute(RESOURCE_OR_CONTRACTOR)) == 2 ||
					session.setAttribute(PRODUCT_TYPE, "3");
				} else {
					session.setAttribute(PRODUCT_TYPE, "2");
				}
				CF.setStrReportTimeAM_PMFormat("hh:mma");
//				cst.close();
				
				CF.setStrFinancialYearFrom(strFinancialYearStart);
				CF.setStrFinancialYearTo(strFinancialYearEnd);
				
				
				strTimeZone = (String) session.getAttribute(O_TIME_ZONE);
				session.setAttribute("TODAY", uF.getDateFormat(uF.getCurrentDate(strTimeZone)+"", DBDATE, "EEEE MMM, dd"));
				session.setAttribute(CommonFunctions, CF);
				
					
				getModulesDetails(con, CF);
				
				
				String clientIp = request.getRemoteAddr();
				
				
				if(uF.parseToInt((String)session.getAttribute(USERTYPEID)) == 3 && uF.parseToInt((String)session.getAttribute(PRODUCT_TYPE)) == 2) {
					session.setAttribute("IS_HOME", "YES");
				} else {
					session.setAttribute("IS_HOME", "NO");
				}
//				System.out.println("getIsRemember==>"+getIsRemember());
				if(isRemember) {
						//Code for Remember Me 
						
						Cookie cookie_username = new Cookie ("PAYROLL_USERNAME",username);
						cookie_username.setMaxAge(365 * 24 * 60 * 60);
						cookie_username.setPath(request.getContextPath());
						response.addCookie(cookie_username);
						
						
						Cookie cookie_password = new Cookie ("PAYROLL_PASSWORD",password);
						cookie_password.setMaxAge(365 * 24 * 60 * 60);
						cookie_password.setPath(request.getContextPath());
						response.addCookie(cookie_password);
						
						Cookie cookie_login_type = new Cookie ("PAYROLL_LOGIN_TYPE", loginType);
						cookie_login_type.setMaxAge(365 * 24 * 60 * 60);
						cookie_login_type.setPath(request.getContextPath());
						response.addCookie(cookie_login_type);
//						System.out.println("Setting cookies........"+request.getCookies());
				}
				
//				con.setAutoCommit(true);
				pst = con.prepareStatement("Insert into login_timestamp (login_timestamp, emp_id, client_ip,session_id) values (?, ?, ?,?)");
				pst.setTimestamp(1, uF.getTimeStamp(""+uF.getCurrentDate(strTimeZone)+uF.getCurrentTime(strTimeZone), DBDATE+DBTIME));
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setString(3, clientIp);
				pst.setString(4, session.getId());
				pst.execute();
				pst.close();
				
				
				/*con.setAutoCommit(true);
				cst = con.prepareCall("{call ins_login_timestamp(?,?,?)}");
				cst.setTimestamp(1, uF.getTimeStamp(""+uF.getCurrentDate(strTimeZone)+uF.getCurrentTime(strTimeZone), DBDATE+DBTIME));
				cst.setInt(2, uF.parseToInt(strEmpId));
				cst.setString(3, clientIp);
				cst.execute();
				cst.close();*/
				
//				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
//				boolean flagAssignShiftOnBasisOfRules = uF.parseToBoolean(hmFeatureStatus.get(F_ASSIGN_SHIFT_ON_BASIS_OF_RULES));
				
				SearchData search = new SearchData();
				search.request = this.request;
				search.session = this.session;
				search.CF = CF;
				search.getSearchingData();
				
				
				String strDomain = request.getServerName().split("\\.")[0];
				CronData cron = new CronData();
				cron.request = this.request;
				cron.session = this.session;
				cron.CF = CF;
				cron.strEmpId = strEmpId;
				cron.strDomain = strDomain;
				cron.setCronData();
				
				cron = new CronData();
				cron.request = this.request;
				cron.session = this.session;
				cron.CF = CF;
				cron.strEmpId = strEmpId;
				cron.strDomain = strDomain;
				cron.setOvertimeMinuteAlertNotification(con,uF);
//				cron.checkTodaysBirthdaysMarriageAndWorkAnniversary(con,uF);
//				if(flagAssignShiftOnBasisOfRules) {
//					cron.assignShiftsOnBasisOfRules(con,uF);
//				}
				
			}
			
			if(isUser){
				if(strEmpType!=null && strEmpType.equals(EMPLOYEE)) {
					if(!checkEmployeeApprovedFlag(con, uF, CF, strEmpId)) {
						setEmpId(strEmpId);
						session.setAttribute("isApproved", false);
						return "notapproved";
					}
				}
			}else{
				request.setAttribute(MESSAGE, E_WrongUserName);
				return LOGIN;
			}
			
			if(dtStartDt!=null && dtEndDt!=null){
				CF.setTrial(true);
				
				if(dtCurrentDt.after(dtEndDt) || dtCurrentDt.before(dtStartDt)){
					request.setAttribute(MESSAGE, E_AccountSuspended);
					return LOGIN;
				}
				if(!isTermsCondition){
					CF.setTermsCondition(false);
					return "termsconditionscheck";
				} 
			}
			
			if(isForcePassword){
				CF.setForcePassword(true);
				return "forcechangepassword";
			} 
		
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(cst);
			db.closeConnection(con);
		}
		
		log.debug("Returning DASHBOARD");
		
//		String strUserType = (String)session.getAttribute(USERTYPE);
//		if(strUserType!=null && strUserType.equalsIgnoreCase(CEO)){
//			
//			ProjectPerformanceReportWP obj = new ProjectPerformanceReportWP();
//			obj.setServletRequest(request);
//			obj.execute();
//		}
//		System.out.println("login return MYHOME");
//		return "myCalendar";
		return MYHOME;
	}

	private boolean checkEmployeeApprovedFlag(Connection con, UtilityFunctions uF, CommonFunctions cF, String strEmpId) {
		
		CallableStatement cst = null;
		ResultSet rst =null;
		boolean isApproved = true;
		PreparedStatement pst = null;
		try {
			
//			pst = con.prepareStatement("SELECT approved_flag FROM employee_personal_details WHERE emp_per_id = ?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			log.debug("pst==>"+pst);
//			rs =pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_employee_personal_details(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(getEmpId()));
//			cst.execute();
//			rst = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT approved_flag FROM employee_personal_details WHERE emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rst =pst.executeQuery();
			
			while(rst.next()) {
				isApproved = rst.getBoolean("approved_flag");
			}
			rst.close();
			pst.close();
			
			log.debug(isApproved+"<=isApproved");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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
		
		return isApproved;
		
	}
	
	private boolean getModulesDetails(Connection con, CommonFunctions  CF) {
		
		CallableStatement cst = null;
		ResultSet rst =null;
		boolean isApproved = true;
		PreparedStatement pst=null;
		try {
			
//			pst = con.prepareStatement("SELECT * FROM user_modules");
//			rst =pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_user_modules()}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.execute();
//			rst = (ResultSet) cst.getObject(1); 
			pst = con.prepareStatement("SELECT * FROM user_modules");
			rst =pst.executeQuery();
			String []arrAllModules = null;   
			String []arrEnabledModules = null;
			String []arrModulesLimit = null;
			while(rst.next()){
				arrAllModules = rst.getString("modules_list").split(",");
				arrEnabledModules = rst.getString("modules_enabled").split(",");
				arrModulesLimit = rst.getString("type_limit").split(",");
			}
			rst.close();
			pst.close();
			
			for(int i=0;arrModulesLimit!=null && i<arrModulesLimit.length; i++){
				String str = arrModulesLimit[i];
				if(str!=null){
					String []arr = str.split(":");
					
					if(arr[0]!=null && arr[0].equalsIgnoreCase(ADMIN)){
						CF.setStrMaxAdmin(arr[1]);
					}
					if(arr[0]!=null && arr[0].equalsIgnoreCase(EMPLOYEE)){
						CF.setStrMaxEmployee(arr[1]);
					}
					if(arr[0]!=null && arr[0].equalsIgnoreCase(WLOCATION)){
						CF.setStrMaxLocations(arr[1]);
					}
					if(arr[0]!=null && arr[0].equalsIgnoreCase(ORGANISATION)){
						CF.setStrMaxOrganisation(arr[1]);
					}
					if(arr[0]!=null && arr[0].equalsIgnoreCase(USER_LEVEL)){
						CF.setStrMaxUserLevels(arr[1]);
					}
				}
			}
			session.setAttribute("arrEnabledModules", arrEnabledModules);
			CF.setArrEnabledModules(arrEnabledModules);
			CF.setArrAllModules(arrAllModules);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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
		
		return isApproved;
		
	}

	private String username;
	private String password;
	private boolean isRemember;
	
	public String getUsername() {		
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getIsRemember() {
		return isRemember;
	}
	
	public void setIsRemember(boolean isRemember) {
		this.isRemember = isRemember;
	}
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response= response;
		
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getProfileEmpId() {
		return profileEmpId;
	}

	public void setProfileEmpId(String profileEmpId) {
		this.profileEmpId = profileEmpId;
	}

}