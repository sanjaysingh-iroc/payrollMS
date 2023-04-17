package com.konnect.jpms.employee;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.EncryptionUtility;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddEmployeeMode extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AddEmployeeMode.class);
	
	public HttpSession session;
	String strUserType;
	String strSessionEmpId;
	public CommonFunctions CF;
	
	private String fname;
	private String mname;
	private String lname;
	private String email;
	private String message;
	private String notification;
	private String empId;
	private String mode;
	private String fromPage;
	
	private String userStatus;
	
	private String[] f_strWLocation; 
	private String f_org;
	
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	
	public String execute() throws Exception {
		
//		System.out.println("hii im in addemployeemode class");
		session = request.getSession();		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, PAddEmployeeMode);
		request.setAttribute(TITLE, "Employee Bulk Import");
		
		UtilityFunctions uF = new UtilityFunctions();
      
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		if(getNotification()!=null && getNotification().equals("reminder")){
			sendReminderNotif();
			return "preport";
		}
		
		
		if(getNotification()!=null && getNotification().equals("signup")) {
			sendSingupNotif();
			if(fromPage !=null && fromPage.equals("P")) {
				return REPORT;
			}
		}
		
		if(uF.parseToInt(getEmpId())!=0) {
			approveEmployee();
			return REPORT;
		}
		
		//System.out.println("f_org="+f_org);
		//System.out.println("f_strWLocation="+f_strWLocation);
		
		if(getMode() != null && getMode().equals("3")) {
			//System.out.println("getMode"+getMode());
			LoadEmployeeReport(uF);
			return LOAD;
		} else if(getMode() != null && getMode().equals("4")) {
//			System.out.println("strUserType----"+strUserType);
			//System.out.println("getMode="+getMode());
			return LoadEmployeeReport(uF);
		}
 		return SUCCESS;
		
	}

	
	public String LoadEmployeeReport(UtilityFunctions uF){

		organisationList = new FillOrganisation(request).fillOrganisation();
		if(getF_org()==null && organisationList!=null && organisationList.size()>0){
			setF_org(organisationList.get(0).getOrgId());
		}
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		//wLocationList = new FillWLocation(request).fillWLocation(null);
		getSelectedFilter(uF);
		return SUCCESS;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
	Map<String,String> hmFilter=new HashMap<String, String>();
	List<String> alFilter = new ArrayList<String>();

	alFilter.add("ORGANISATION");
	if(getF_org()!=null)  {
		String strOrg="";
		int k=0;
		for(int i=0;organisationList!=null && i<organisationList.size();i++){
			if(getF_org().equals(organisationList.get(i).getOrgId())) {
				if(k==0) {
					strOrg=organisationList.get(i).getOrgName();
				} else {
					strOrg+=", "+organisationList.get(i).getOrgName();
				}
				k++;
			}
		}
		if(strOrg!=null && !strOrg.equals("")) {
			hmFilter.put("ORGANISATION", strOrg);
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
	} else {
		hmFilter.put("ORGANISATION", "All Organisation");
	}
	
	alFilter.add("LOCATION");
	if(getF_strWLocation()!=null) {
		String strLocation="";
		int k=0;
		for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
			for(int j=0;j<getF_strWLocation().length;j++) {
				if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
					if(k==0) {
						strLocation=wLocationList.get(i).getwLocationName();
					} else {
						strLocation+=", "+wLocationList.get(i).getwLocationName();
					}
					k++;
				}
			}
		}
		if(strLocation!=null && !strLocation.equals("")) {
			hmFilter.put("LOCATION", strLocation);
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
	} else {
		hmFilter.put("LOCATION", "All Locations");
	}
	
	String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
	//request.setAttribute("selectedFilter", selectedFilter);
	request.setAttribute("selectedFilter", selectedFilter);
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
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setString(3, "T");
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
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE employee_personal_details SET approved_flag = ? and is_alive = ? WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, uF.parseToInt(getEmpId()));
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
		String empId = "";
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
			
			System.out.println("isEmailExist ===>> " + isEmailExist);
			
			if(isEmailExist){
				request.setAttribute(MESSAGE, ERRORM+"Entered email-id already exists in the system, please use different email."+END);
				setMessage("Entered email-id already exists in the system, please use different email.");
				return;
			}
			
			
			AddEmployee aE = new AddEmployee();
			aE.request = request;
			aE.session = session;
			aE.CF = CF;
			aE.setEmpFname(getFname());
			aE.setEmpLname(getLname());
			aE.setEmpEmail(getEmail());
			aE.setEmpAddress1("");
			aE.setApprovedFlag(false);
			empId = aE.insertEmpPersonalDetails(con, uF, CF);
			
			 
			
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
			pst.setInt(4, uF.parseToInt(empId));
			pst.setString(5, "ACTIVE");
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//			log.debug("pst insertUser==>"+pst);
			int x = pst.executeUpdate();
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
			
//			String strSessionId = session.getId();
//			strSessionId = URLEncoder.encode(strSessionId);
			int random = new Random().nextInt();
			String strSessionId = ""+random;
			pst = con.prepareStatement("update employee_personal_details set session_id =?, _timestamp=?, added_by=? where emp_per_id=?");
			pst.setString(1, strSessionId);
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(4, uF.parseToInt(empId));
			pst.execute();
			pst.close();
			
			log.debug("empId===>"+empId);
			System.out.println("empId ===>> " + empId);
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_NEW_EMPLOYEE_JOINING, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(empId+"");
			nF.setStrAddEmpLink("?empId="+empId+"&sessionId="+strSessionId);
			/*nF.setStrHostAddress(request.getRemoteHost());*/
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setEmailTemplate(true);
			nF.sendNotifications();
			
//			Notifications nF = new Notifications("");
//			nF.setStrEmailTo(getEmail());
//			nF.setStrEmpFname(getFname());
//			nF.setStrEmpLname(getLname());
//			nF.setStrUserName(username);
//			nF.setStrPassword(password);
//			nF.setStrEmailSubject("Please fill up the Employee Form.");
			
			/*
			 * 
			 * Dear Candidate,

			Welcome to ______________________________ (candidates name),

			Please fill up the form, from the link below to be inducted with us.

			______________ (link)

			In case you have any queries drop a mail to ____________ (hr@_____________.com)

			Sincerely,

			HR Division
			 * 
			 * 
			 * */
			
			
//			nF.setStrEmailBody("Dear "+getFname()+" "+getLname()+"," + "<br>" + 
//					
//					"Welcome to our organisation," + "<br>" +
//					
//					"Please fill up the form, from the link below to be inducted with us.<br>" +
//					
//					hmSettings.get("EMAIL_LOCAL_HOST") +request.getContextPath() +"/AddEmployee.action?empId="+empId+ "<br>" +
//
//					"In case you have any queries drop a mail to (hr@konnecttechnologies.com)" + "<br>" +
//
//					"Sincerely," + "<br>" +
//
//					"HR Division" + "<br>" );
					
//			nF.sendNotifications();
			session.setAttribute(MESSAGE, SUCCESSM+"Email has been sent to the Employee with induction form link."+END);
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
	
	public HttpServletRequest request;   
	

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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
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

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

}