package com.konnect.jpms.task;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddPeopleMode extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AddPeopleMode.class);
	
	String strUserType;
	String strSessionEmpId;
	HttpSession session;
	private String fname;
	private String mname;
	private String lname;
	private String email;
	private String strEmpORContractor;
	private String message;
	private String notification;
	private int empId;
	
	public CommonFunctions CF;
	
	public String execute() throws Exception {
		
		session = request.getSession();		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
//		request.setAttribute(PAGE, PAddEmployeeMode);
//		request.setAttribute(TITLE, "Add Employee Mode");
		
		if(getNotification()!=null && getNotification().equals("reminder")){
			sendReminderNotif();
			return "preport";
		}
		
		if(getNotification()!=null && getNotification().equals("signup")) {
			sendSingupNotif();
			return SUCCESS;
		}
		
		if(getEmpId()!=0) {
			approveEmployee();
			return REPORT;
		}
		checkNotificationStatus();
		
		return VIEW;
		
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	
	private void checkNotificationStatus() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("select * from notifications where notification_code = ? and isemail = true");
			pst.setInt(1, N_NEW_EMPLOYEE_JOINING);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("email_notification") != null && !rs.getString("email_notification").equals("") && rs.getString("email_subject") != null && !rs.getString("email_subject").equals("")) {
					flag = true;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("statusFlag", flag);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	private void sendReminderNotif() {
		
		Connection con = null;
		PreparedStatement pst =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String username = "";
		String password = "";
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUsers = CF.getEmpIdUserNameMap(con);
			if(hmUsers.get(getEmpId()+"")!=null) {
				
				username = ((String)hmUsers.get(getEmpId()+"")).split(" ")[0];
				password = ((String)hmUsers.get(getEmpId()+"")).split(" ")[1];
				
			}
//			String strDomain = request.getServerName().split("\\.")[0];
//			Notifications nF = new Notifications(0, CF);
//			nF.setDomain(strDomain);
//			nF.setStrEmailTo(getEmail());
//			nF.setStrEmpFname(getFname());
//			nF.setStrEmpLname(getLname());
//			
//			nF.setStrEmailSubject("Reminder to fill up the Employee induction Form.");
//			nF.setStrEmailBody("You have not filled up the employee induction Form completely. Login Crenditials are <br> " +
//					"username:"+ username +" & password: "+password );
//			nF.sendNotifications();

			
			pst = con.prepareStatement("INSERT INTO reminder_details(_date, emp_id, reminder_type) values(?,?,?)");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, getEmpId());
			pst.setString(3, "T");
			log.debug("pst===>"+pst);
			pst.execute();
			pst.close();
			
			setMessage("Reminder Sent To the Employee "+getFname()+ " "+getLname());
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void approveEmployee() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE employee_personal_details SET approved_flag = ? and is_alive = ? WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, getEmpId());
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
		
	}

	private void sendSingupNotif() {
		
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		int empId = 0;
		String username = "";
		String password = "";
		
		try {
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from employee_personal_details where emp_email = ?");
			pst.setString(1, getEmail());
			rs = pst.executeQuery();
			boolean isEmailExist = false;
			while(rs.next()){
				isEmailExist = true;
			}
			rs.close();
			pst.close();
			
			if(isEmailExist){
				setMessage("Entered email-id already exists in the system, please use different email.");
				return;
			}
			
			
			AddPeople aP = new AddPeople();
			aP.setEmpFname(getFname());
			aP.setEmpLname(getLname());
			aP.setEmpEmail(getEmail());
			aP.setStrEmpORContractor(getStrEmpORContractor());
			aP.setStreetAddress("");
			aP.setApprovedFlag(false);
			empId = aP.insertEmpPersonalDetails(uF, CF);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
			
		try {
			con = db.makeConnection(con);
//			username = getFname()+getLname();
			Map<String,String> userPresent= CF.getUsersMap(con);
			username = this.getUserName(userPresent);
			
			SecureRandom random = new SecureRandom();
			password = new BigInteger(130, random).toString(32).substring(5, 13);
			
			pst = con.prepareStatement(insertUser);
			pst.setString(1, username);
			pst.setString(2, password);
			pst.setInt(3, 3);
			pst.setInt(4, empId);
			pst.setString(5, "ACTIVE");
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.execute();
			pst.close();
			
		}catch (Exception e) {
			e.printStackTrace();
			setMessage("User already exist! Please use a different first or last name.");
			return;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		try {
			
			
			con = db.makeConnection(con);
			
			String strSessionId = session.getId();
			strSessionId = URLEncoder.encode(strSessionId);
			pst = con.prepareStatement("update employee_personal_details set session_id =?, _timestamp=?, added_by=? where emp_per_id=?");
			pst.setString(1, strSessionId);
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(4, empId);
			pst.execute();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_NEW_EMPLOYEE_JOINING, CF);
			nF.setDomain(strDomain);
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			
			nF.request = request;
			nF.setStrOrgId((String)session.getAttribute(ORGID));
			nF.setEmailTemplate(true);
			
			nF.setStrEmpId(empId+"");
			nF.setStrAddPeopleLink("?empId="+empId+"&sessionId="+strSessionId);
			/*nF.setStrHostAddress(request.getRemoteHost());*/
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.sendNotifications();
			
			
			setMessage("Email has been sent to the Employee with induction form link.");
			setFname("");
			setLname("");
			setEmail("");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}

	
	public String getUserName(Map<String,String> userPresent) {
		
		int size=getFname().length();
//		Map<String,String> userPresent= CF.getUsersMap();
		
		String createUserName = getFname().charAt(0) + getLname();
		
				
		if(CF.getStrUserNameFormat()!=null && CF.getStrUserNameFormat().equalsIgnoreCase(FIRSTNAME_DOT_LASTNAME)){
			createUserName = ((getFname()!=null)?getFname().toLowerCase():"") +"."+((getLname()!=null)?getLname().toLowerCase():"");
			createUserName = createUserName.replace(" ", "");
			
			
			for(int i=0;i<=userPresent.size();i++){
				if((userPresent.values()).contains(createUserName)){
					createUserName = getFname().toLowerCase() +"."+ getLname().toLowerCase()+ (i + 1);
				}
			}	
		}else{
			for(int i=1;i<=size;i++){
				if((userPresent.values()).contains(createUserName)){
					createUserName = getFname().substring(0, i) + getLname();
				}
			}
			
			for(int i=0;i<=userPresent.size();i++){
				
				if((userPresent.values()).contains(createUserName)){
					if(size<=i) {
						createUserName = getFname() + getLname() + (i -size + 1);
					}
				}
			}

		}
		
		log.debug("createUserName===>"+createUserName);
		
		return createUserName;
		
	}
	
	private HttpServletRequest request;
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStrEmpORContractor() {
		return strEmpORContractor;
	}

	public void setStrEmpORContractor(String strEmpORContractor) {
		this.strEmpORContractor = strEmpORContractor;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}
	
	public void setCF(CommonFunctions CF) {
		this.CF = CF;
	}
}
