package com.konnect.jpms.recruitment;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddCandidateMode extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AddCandidateMode.class);
	
	String strUserType;
	String strSessionEmpId;
	HttpSession session;
	private String fname;
	private String lname;
	private String email;
	private String message;
	private String notification;
	private int empId;
	
	CommonFunctions CF;
	
	public String execute() throws Exception {
		
		session = request.getSession();		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/recruitment/AddCandidateMode.jsp");
		request.setAttribute(TITLE, "Add Candidate Mode");
	//	request.setAttribute("jobid", getJobid());
		request.setAttribute("recruitID",getRecruitId());
	
		
		if(getNotification()!=null && getNotification().equals("reminder")){
			sendReminderNotif();
			return "preport";
		}
		
		if(getNotification()!=null && getNotification().equals("signup")) {
			sendSingupNotif();
		}
		
		if(getEmpId()!=0) {
			approveEmployee();
			return REPORT;
		}
		return LOAD;
		

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
			
//			Notifications nF = new Notifications(0, CF);
			/*nF.setStrEmailTo(getEmail());
			nF.setStrEmpFname(getFname());
			nF.setStrEmpLname(getLname());
			
			nF.setStrEmailSubject("Reminder to fill up the Employee induction Form.");
			nF.setStrEmailBody("You have not filled up the employee induction Form completely. Login Crenditials are <br> " +
					"username:"+ username +" & password: "+password );
			nF.sendNotifications();
*/
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
			pst = con.prepareStatement("UPDATE candidate_personal_details SET approved_flag = ? and is_alive = ? WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, getEmpId());
			log.debug("pst===>"+pst);
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
		
			pst = con.prepareStatement("select * from candidate_personal_details where emp_email = ?");
			pst.setString(1, getEmail());
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
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
			
			AddCandidate aE = new AddCandidate();
			aE.setServletRequest(request);
			aE.CF = CF;
			aE.setEmpFname(getFname());
			aE.setEmpLname(getLname());
			aE.setEmpEmail(getEmail());
			aE.setEmpAddress1("");
			aE.setRecruitId(getRecruitId());
			aE.setApprovedFlag(false);
			empId = aE.insertCandidatePersonalDetails(con, uF, CF);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
			
		try {
			
			String strSessionId = session.getId();
//			strSessionId = URLEncoder.encode(strSessionId);
			pst = con.prepareStatement("update candidate_personal_details set session_id=?, _timestamp=?, added_by=? where emp_per_id=?");
			pst.setString(1, strSessionId);
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(4, empId);
			pst.execute();
			pst.close();
//			System.out.println("pst ===> "+pst);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			
			log.debug("empId===>"+empId);
//			System.out.println("empId ===> "+empId);
			String strDomain = request.getServerName().split("\\.")[0];	
			CandidateNotifications nF = new CandidateNotifications(N_NEW_CADIDATE_ADD, CF); 
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(empId+"");
			nF.setStrRecruitmentId(getRecruitId());
			nF.setStrAddCandiLink("?empId="+empId+"&sessionId="+strSessionId+"&org_id="+session.getAttribute(ORGID));
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setHmFeatureStatus(hmFeatureStatus);
			nF.setHmFeatureUserTypeId(hmFeatureUserTypeId);
			
			nF.sendNotifications();
			
			nF.setStrEmailTo(getEmail());
			nF.setStrCandiFname(getFname());
			nF.setStrCandiLname(getLname());
//			nF.setStrUserName(username);
//			nF.setStrPassword(password);
//			nF.setStrEmailSubject("Please fill up the Employee Form.");
			
/*			
			  Dear Candidate,

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
//					hmSettings.get("EMAIL_LOCAL_HOST") +request.getContextPath() +"/AddCandidate.action?empId="+empId+ "<br>" +
//
//					"In case you have any queries drop a mail to (hr@konnecttechnologies.com)" + "<br>" +
//
//					"Sincerely," + "<br>" +
//
//					"HR Division" + "<br>" );
//					
//			nF.sendNotifications();
//			
			setMessage("Email has been sent to the Candidate with link.");
			setFname("");
			setLname("");
			setEmail("");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}


//public void sendMail() {
//
//	Connection con = null;
//	ResultSet rst = null;
//	PreparedStatement pst = null;
//	Database db = new Database();
//	db.setRequest(request);
//	UtilityFunctions uF = new UtilityFunctions();
//
//	try {
//		con = db.makeConnection(con);
//		Map<String, Map<String, String>> hmCandiInfo = getCandiInfoMap(con, false);
//
//			Map<String, String> hmCandiInner = hmCandiInfo.get(getEmp_id());
//		
//		String strDomain = request.getServerName().split("\\.")[0];	
//		CandidateNotifications nF = new CandidateNotifications(N_CANDI_SPECIFY_OTHER_DATE, CF);
//		nF.setDomain(strDomain);
////		System.out.println("applicationIdsLst.get(i) is ========= "+applicationIdsLst.get(i));
//		nF.setStrEmpId(getEmp_id());
////		System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
////		System.out.println("request.getContextPath() is ========= "+request.getContextPath());
//		 nF.setStrHostAddress(CF.getStrEmailLocalHost());
//		 nF.setStrContextPath(request.getContextPath());
//		
//		 nF.setStrCandiFname(hmCandiInner.get("FNAME"));
//		 nF.setStrCandiLname(hmCandiInner.get("LNAME"));
////		 nF.setStrRecruitmentDesignation(hmCandiDesig.get(getEmp_id()));
//		 nF.setStrAddCandidateStep8("?CandidateId="+getEmp_id()+"&step=8&operation=U&mode=profile&type=type&candibymail=yes");
//		 
//		 nF.sendNotifications();
////		}
//	} catch (Exception e) {
//		e.printStackTrace();
//	} finally {
//		db.closeConnection(con);
//		db.closeResultSet(rst);
//		db.closeStatements(pst);
//	}
// }

//	public String getUserName() {
//		
//		int size=getFname().length();
//		Map<String,String> userPresent= CF.getUsersMap();
//		
//		String createUserName = getFname().charAt(0) + getLname();
//		
//				
//		if(CF.getStrUserNameFormat()!=null && CF.getStrUserNameFormat().equalsIgnoreCase(FIRSTNAME_DOT_LASTNAME)){
//			createUserName = ((getFname()!=null)?getFname().toLowerCase():"") +"."+((getLname()!=null)?getLname().toLowerCase():"");
//			createUserName = createUserName.replace(" ", "");
//			
//			
//			for(int i=0;i<=userPresent.size();i++){
//				if((userPresent.values()).contains(createUserName)){
//					createUserName = getFname().toLowerCase() +"."+ getLname().toLowerCase()+ (i + 1);
//				}
//			}	
//		}else{
//			for(int i=1;i<=size;i++){
//				if((userPresent.values()).contains(createUserName)){
//					createUserName = getFname().substring(0, i) + getLname();
//				}
//			}
//			
//			for(int i=0;i<=userPresent.size();i++){
//				
//				if((userPresent.values()).contains(createUserName)){
//					if(size<=i) {
//						createUserName = getFname() + getLname() + (i -size + 1);
//					}
//				}
//			}
//		}
//		
//		log.debug("createUserName===>"+createUserName);
//		
//		return createUserName;
//		
//	}

	private HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	
	
	//  for adding candidate for a particular job
	
	String recruitId;
	


	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	/*String jobid;

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		
		this.jobid = jobid;
	
	}
*/
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
	
}