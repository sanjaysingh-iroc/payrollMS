package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Addinterviewpaneldate extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 459321950432564621L;
	HttpSession session;
	CommonFunctions CF = null;

	String candidateId;
	String interviewTime;
	String dateinterview;
    String panelId;
    String recruitId;
    String iCount;
    String type;
    String preferedDate;
    String preferedTime;
	String notiStatus;
	String assessmentId;
	
	String strSessionEmpId = null;
	String strEmpOrgId = null;
	
	String job_title;
	String recruiter_name;
	
    public String execute(){
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF=new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();

		/*if(getCandidateId() != null && uF.parseToInt(getCandidateId()) == 0) {
			String decodeCandidateId = eU.decode(getCandidateId());
			setCandidateId(decodeCandidateId);
		}
		if(getPanelId() != null && uF.parseToInt(getPanelId()) == 0) {
			String decodePanelId = eU.decode(getPanelId());
			setPanelId(decodePanelId);
		}
		if(getRecruitId() != null && uF.parseToInt(getRecruitId()) == 0) {
			String decodeRecruitId = eU.decode(getRecruitId());
			setRecruitId(decodeRecruitId);
		}*/
		
		String strType=(String)request.getParameter("type");
		if(strType.equalsIgnoreCase("insert") && ((!getDateinterview().equals("") && !getInterviewTime().equals("")) || (!getPreferedDate().equals("") && !getPreferedTime().equals("")))){
			insertdate(uF);
		}else if(strType.equalsIgnoreCase("remove")){
			removedate(uF);
		}
		getInterviewDates(uF);
		getInterviewDateData(uF);
		return LOAD;
	}
	
	
	
	private void removedate(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {	
			con = db.makeConnection(con);
			pst=con.prepareStatement("delete from candidate_interview_panel where recruitment_id=? and panel_round_id=? and candidate_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getPanelId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.execute();
    		pst.close();
			
			List<String> roundEmpList = new ArrayList<String>();
			pst = con.prepareStatement("select panel_emp_id from panel_interview_details where recruitment_id = ? and round_id = ? and panel_emp_id is not null");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getPanelId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				roundEmpList.add(rst.getString("panel_emp_id"));
			}
			rst.close();
    		pst.close();
    		
//			System.out.println("roundEmpList ===> " + roundEmpList);
			for (int i = 0; roundEmpList != null && !roundEmpList.isEmpty() && i < roundEmpList.size(); i++) {
				String strDomain = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(roundEmpList.get(i));
				userAlerts.set_type(REMOVE_MY_INTERVIEWS_SCHEDULED_ALERT);
				userAlerts.setStatus(INSERT_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
			
			pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and round_id=? and activity_id=? and candi_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getPanelId()));
			pst.setInt(3, CANDI_ACTIVITY_INTERVIEW_SCHEDULE_ID);
			pst.setInt(4, uF.parseToInt(getCandidateId()));
			pst.execute();
    		pst.close();
    		
//			System.out.println("removedate pst ===> "+pst);
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "<td>failed</td>");
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}

	
	private void insertdate(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {	
			con = db.makeConnection(con);
			List<String> roundEmpList = new ArrayList<String>();
			pst = con.prepareStatement("select panel_emp_id from panel_interview_details where recruitment_id = ? and round_id = ? and panel_emp_id is not null");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getPanelId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				roundEmpList.add(rst.getString("panel_emp_id"));
			}
			rst.close();
    		pst.close();
    		
//			System.out.println("roundEmpList ===> " + roundEmpList);
			
    		if(uF.parseToInt(getAssessmentId()) > 0) {
				pst=con.prepareStatement("insert into candidate_interview_panel(recruitment_id,candidate_id,panel_round_id,interview_date," +
						"interview_time,assessment_id) values(?,?,?,?, ?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, uF.parseToInt(getCandidateId()));
				pst.setInt(3, uF.parseToInt(getPanelId()));
				if(getPreferedDate() !=null && !getPreferedDate().equals("")) {
					pst.setDate(4,uF.getDateFormat(getPreferedDate(), DATE_FORMAT));
				} else {
					pst.setDate(4,uF.getDateFormat(dateinterview, DATE_FORMAT));
				}
				if(getPreferedTime() !=null && !getPreferedTime().equals("")) {
					pst.setTime(5,uF.getTimeFormat(getPreferedTime(), TIME_FORMAT));
				} else {
					pst.setTime(5,uF.getTimeFormat(getInterviewTime(), TIME_FORMAT));
				}
				pst.setInt(6, uF.parseToInt(getAssessmentId()));
				System.out.println("candidate_interview_panel : "+pst.toString());
				pst.executeUpdate();
	    		pst.close();
    		}
    		
    		int insertCnt = 0;
			for (int i = 0; roundEmpList != null && !roundEmpList.isEmpty() && i < roundEmpList.size(); i++) {
				boolean flag = false;
				pst = con.prepareStatement("select panel_user_id from candidate_interview_panel where recruitment_id = ? and panel_round_id = ? and candidate_id =? and panel_user_id = ?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, uF.parseToInt(getPanelId()));
				pst.setInt(3, uF.parseToInt(getCandidateId()));
				pst.setInt(4, uF.parseToInt(roundEmpList.get(i)));
				rst = pst.executeQuery();
				while (rst.next()) {
					flag = true;
				}
				rst.close();
	    		pst.close();
	    		
	    		if(!flag) {
					pst=con.prepareStatement("insert into candidate_interview_panel(recruitment_id,candidate_id,panel_round_id,interview_date," +
							"interview_time,panel_user_id) values(?,?,?,?, ?,?)");
					pst.setInt(1, uF.parseToInt(getRecruitId()));
					pst.setInt(2, uF.parseToInt(getCandidateId()));
					pst.setInt(3, uF.parseToInt(getPanelId()));
					if(getPreferedDate() !=null && !getPreferedDate().equals("")) {
						pst.setDate(4,uF.getDateFormat(getPreferedDate(), DATE_FORMAT));
					} else {
						pst.setDate(4,uF.getDateFormat(dateinterview, DATE_FORMAT));
					}
					if(getPreferedTime() !=null && !getPreferedTime().equals("")) {
						pst.setTime(5,uF.getTimeFormat(getPreferedTime(), TIME_FORMAT));
					} else {
						pst.setTime(5,uF.getTimeFormat(getInterviewTime(), TIME_FORMAT));
					}
					pst.setInt(6, uF.parseToInt(roundEmpList.get(i)));
					int x = pst.executeUpdate();
		    		pst.close();
		    		if(x>0) {
		    			insertCnt++;
		    		}
	    		}
			}
			
			if(insertCnt > 0) {
				for (int i = 0; roundEmpList != null && !roundEmpList.isEmpty() && i < roundEmpList.size(); i++) {
					String strDomain = request.getServerName().split("\\.")[0];
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(roundEmpList.get(i));
					userAlerts.set_type(ADD_MY_INTERVIEWS_SCHEDULED_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,round_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, uF.parseToInt(getCandidateId()));
				pst.setInt(3, uF.parseToInt(getPanelId()));
				pst.setString(4, "Interview Scheduled");
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setDate	(6, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(7, CANDI_ACTIVITY_INTERVIEW_SCHEDULE_ID);
				pst.execute();
	    		pst.close();
			}
//			System.out.println("insertdate pst ===> "+pst);
			request.setAttribute("dateinterview", dateinterview);
			request.setAttribute("interviewtime", getInterviewTime());
			sendMail();
		} catch(Exception e) {
			request.setAttribute("STATUS_MSG", "<td>failed</td>");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);		
		}
	}

	
	
	public void sendMail() {

		Connection con = null;
		ResultSet rst1 = null;
		PreparedStatement pst1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			String panel_employee_id = "";
			pst1 = con.prepareStatement("select panel_emp_id from panel_interview_details where recruitment_id = ? and round_id = ? and panel_emp_id is not null");
			pst1.setInt(1, uF.parseToInt(getRecruitId()));
			pst1.setInt(2, uF.parseToInt(getPanelId()));
			rst1 = pst1.executeQuery();
			while (rst1.next()) {
				panel_employee_id += ","+rst1.getString("panel_emp_id")+",";
			}
			rst1.close();
    		pst1.close();
    		StringBuilder strQuery = new StringBuilder();
    		// Start Dattatray Date : 10-08-21
    		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			strQuery.append("select d.designation_code,d.designation_name,g.grade_code,g.grade_name,w.wlocation_name,r.no_position,"
					+ "r.job_code,r.added_by,l.level_code,l.level_name,r.skills,r.min_exp,r.max_exp,r.min_education,r.job_title,r.hiring_manager"
					+ " from recruitment_details r,grades_details g,work_location_info w,designation_details d,employee_personal_details e," +
					"level_details l where r.grade_id=g.grade_id and r.wlocation=w.wlocation_id and r.designation_id=d.designation_id and r.added_by=e.emp_per_id "
					+ "and r.level_id=l.level_id and r.recruitment_id=?");

			pst1 = con.prepareStatement(strQuery.toString());
			pst1.setInt(1, uF.parseToInt(getRecruitId()));
			System.out.println("pst1111 : "+pst1.toString());
			rst1 = pst1.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst1.next()) {
				// Start Dattatray Date : 09-08-21
				job_title = rst1.getString("job_title");
				recruiter_name = uF.showData(getAppendData(rst1.getString("hiring_manager"), hmEmpName), "");
				// ENd Dattatray 
			}
			rst1.close();
			pst1.close();
			
			pst1 = con.prepareStatement("select * from document_signature where org_id =?");
			pst1.setInt(1, uF.parseToInt(strEmpOrgId));
			rst1 = pst1.executeQuery();
			String strAuthSign = null;
			String strHrSign = null;
			String strRecruiterSign = null;
			while (rst1.next()) {
				if(rst1.getInt("signature_type") == 1) {
					strAuthSign = rst1.getString("signature_image");
				}
				if(rst1.getInt("signature_type") == 2) {
					strHrSign = rst1.getString("signature_image");
				}
				if(rst1.getInt("signature_type") == 3) {
					if(rst1.getInt("user_id") == uF.parseToInt(strEmpOrgId)) {
						strRecruiterSign = rst1.getString("signature_image");
					}
				}
			}
			rst1.close();
			pst1.close();
			// End Dattatray Date : 10-08-21
			
			System.out.println("job_title : "+job_title);
			System.out.println("recruiter_name : "+recruiter_name);
			System.out.println("panel_employee_id ===> "+panel_employee_id);
			if(panel_employee_id != null && !panel_employee_id.equals("")){
				String tmpsltempids = panel_employee_id.substring(1, panel_employee_id.length()-1);
				List<String> selectedEmpIdsLst=Arrays.asList(tmpsltempids.split(","));
				for(int i=0; selectedEmpIdsLst!= null && i<selectedEmpIdsLst.size(); i++){
					
					Map<String, String> hmEmpInner = hmEmpInfo.get(selectedEmpIdsLst.get(i));
					if(selectedEmpIdsLst.get(i) != null && !selectedEmpIdsLst.get(i).equals("")){
						String strDomain = request.getServerName().split("\\.")[0];	
						Notifications nF = new Notifications(N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP, CF);
			//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
						 nF.setDomain(strDomain);
						 nF.request = request;
						 nF.setStrEmpId(selectedEmpIdsLst.get(i));
						 nF.setStrHostAddress(CF.getStrEmailLocalHost());
						 nF.setStrHostPort(CF.getStrHostPort());
						 nF.setStrContextPath(request.getContextPath());
						 StringBuilder sb = new StringBuilder();
						 sb.append("Date: ");
			//			 System.out.println("getPreferedDate() ===> "+getPreferedDate()+" dateinterview ===> "+dateinterview);
						 if(getPreferedDate() !=null && !getPreferedDate().equals("")){
							 sb.append(uF.getDateFormat(getPreferedDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
							}else{
								sb.append(uF.getDateFormat(dateinterview, DATE_FORMAT, CF.getStrReportDateFormat()));
							}
						 sb.append(" Time: ");
							if(getPreferedTime() !=null && !getPreferedTime().equals("")){
								sb.append(getPreferedTime());
							}else{
								sb.append(getInterviewTime());
							}
						 nF.setStrCandiInterviewDateTime(uF.showData(sb.toString(), ""));
						 nF.setStrEmpFname(hmEmpInner.get("FNAME"));
						 nF.setStrEmpLname(hmEmpInner.get("LNAME"));
						 nF.setEmailTemplate(true);
						 nF.sendNotifications();
					}
				}
			}
			
			
			
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);

			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateId());
			
//			Map<String, String> hmCandiDesig = new HashMap<String, String>();
//		
//			pst = con.prepareStatement("select e.emp_per_id,d.designation_code,d.designation_name from recruitment_details r," +
//					"designation_details d,candidate_personal_details e where r.recruitment_id = e.recruitment_id and " +
//					"r.designation_id=d.designation_id and emp_per_id = ?");
//			pst.setInt(1, uF.parseToInt(getEmp_id()));
//			rst = pst.executeQuery();
//			while(rst.next()){
//				
//				hmCandiDesig.put(rst.getString("emp_per_id"), rst.getString("designation_name"));
//			}
		String strDomain = request.getServerName().split("\\.")[0];
		CandidateNotifications nF = new CandidateNotifications(N_INTERVIEW_DATE_MAIL_FOR_CANDI, CF);
		nF.setDomain(strDomain);
		nF.request = request;
		System.out.println("applicationIdsLst.get(i) is ========= ");
		nF.setStrEmpId(getCandidateId());
		nF.setStrRecruitmentId(getRecruitId());
		System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
		System.out.println("request.getContextPath() is ========= "+request.getContextPath());
		 nF.setStrHostAddress(CF.getStrEmailLocalHost());
		 nF.setStrHostPort(CF.getStrHostPort());
		 nF.setStrContextPath(request.getContextPath());
		
		 StringBuilder sb = new StringBuilder();
		 sb.append("Date: ");
		 System.out.println("getPreferedDate() 1 ===> "+getPreferedDate()+" dateinterview 1 ===> "+dateinterview);
		 if(getPreferedDate() !=null && !getPreferedDate().equals("")){
			 sb.append(uF.getDateFormat(getPreferedDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			}else{
				sb.append(uF.getDateFormat(dateinterview, DATE_FORMAT, CF.getStrReportDateFormat()));
			}
		 sb.append(" Time: ");
			if(getPreferedTime() !=null && !getPreferedTime().equals("")){
				sb.append(getPreferedTime());
			}else{
				sb.append(getInterviewTime());
			}
		 nF.setStrCandiInterviewDateTime(uF.showData(sb.toString(), ""));
		 nF.setStrCandiFname(hmCandiInner.get("FNAME"));
		 nF.setStrCandiLname(hmCandiInner.get("LNAME"));
		 // Start Dattatray Date:10-08-21
		 nF.setStrJobPosition(job_title);
		 nF.setStrRecruiterName(recruiter_name);
		 System.out.println("getPanelId : "+getPanelId());
		 nF.setStrRoundNo("Round: "+getPanelId());
		 String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+strEmpOrgId+"/"+I_DOC_SIGN+"/"+strSessionEmpId+"/"+strRecruiterSign;
		 String strSignature ="<img src=\""+imageUrl+"\">";
		 nF.setStrRecruiterSignature(strSignature);
		// End Dattatray Date:10-08-21
		 
//		 nF.setStrRecruitmentDesignation(hmCandiDesig.get(getEmp_id()));
//		 nF.setStrAddCandidateStep8("?CandidateId="+getCandidateId()+"&step=8&operation=U&mode=profile&type=type&candibymail=yes");
		 
		 nF.sendNotifications();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst1);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
	}
	
	private String getAppendData( String strID,  Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.length()>0 && strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")){
			strID = strID.substring(1, strID.length()-1);
			}
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}

		} else {
			return null;
		}

		return sb.toString();
	}
	
//	public Map<String, Map<String, String>> getCandiInfoMap(Connection con, boolean isFamilyInfo) {
//		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
//
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			Map<String, String> hmCandiInner = new HashMap<String, String>();
//			if(isFamilyInfo){
//				pst = con.prepareStatement("select * from candidate_family_members order by emp_id");
//				rs = pst.executeQuery();
////				System.out.println("new Date ===> "+ new Date());
//				while(rs.next()){
//					
//					hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
//					if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
//					
//					hmCandiInner.put(rs.getString("member_type"), rs.getString("member_name"));
//					hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
//				}
//			}
//			
//			pst = con.prepareStatement("SELECT emp_per_id, emp_fname, emp_lname, empcode, emp_image, emp_email, emp_date_of_birth, candidate_joining_date, " +
//					"emp_gender, marital_status FROM candidate_personal_details order by emp_per_id");
//			rs = pst.executeQuery();
////			System.out.println("new Date ===> "+ new Date());
//			while (rs.next()) {
//				if (rs.getInt("emp_per_id") < 0) {
//					continue;
//				}
//				hmCandiInner = hmCandiInfo.get(rs.getString("emp_per_id"));
//				if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
//
//				hmCandiInner.put("FNAME", rs.getString("emp_fname"));
//				hmCandiInner.put("LNAME", rs.getString("emp_lname"));
//				hmCandiInner.put("FULLNAME", rs.getString("emp_lname")+" "+rs.getString("emp_lname"));
//				hmCandiInner.put("EMPCODE", rs.getString("empcode"));
//				hmCandiInner.put("IMAGE", rs.getString("emp_image"));
//				hmCandiInner.put("EMAIL", rs.getString("emp_email"));
//				hmCandiInner.put("DOB", rs.getString("emp_date_of_birth"));
//				hmCandiInner.put("JOINING_DATE", rs.getString("candidate_joining_date"));
//				hmCandiInner.put("GENDER", rs.getString("emp_gender"));
//				hmCandiInner.put("MARITAL_STATUS", rs.getString("marital_status"));
//				 
//				hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			//log.error(e.getClass() + ": " + e.getMessage(), e);
//		}
//		return hmCandiInfo;
//	}
	
	private void getInterviewDates(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmDateMap =new LinkedHashMap<String, String>();
			Map<String, String> hmTimeMap =new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from candidate_interview_availability where emp_id=?"); //recruitment_id=? and 
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmDateMap.put(rst.getString("int_avail_id"), uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT));

				hmTimeMap.put(rst.getString("int_avail_id"), getTimeFormat(rst.getString("_time")));
			}	
			rst.close();
    		pst.close();
			request.setAttribute("hmDateMap", hmDateMap);
			request.setAttribute("hmTimeMap", hmTimeMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	private String getTimeFormat(String time) {
		if (time != null && !time.equals("")){
			return time.substring(0, 5);
		}else{
			return "";
		}
	}
	
	
	private void getInterviewDateData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alInner=null;
		Map<String, List<String>> hmPanelScheduleInfo = new HashMap<String, List<String>>();
		Map<String, List<String>> hmPanelInterviewTaken = new HashMap<String, List<String>>();
		
		try{
			con=db.makeConnection(con);
		
			List<String> roundIdsRecruitwiseList = new ArrayList<String>(); 
			Map<String, String> hmAssessmentName = CF.getAssessmentNameMap(con, uF);
			Map<String, String> hmRoundAssessment = new HashMap<String, String>();
			pst = con.prepareStatement("select distinct(round_id),recruitment_id,assessment_id from panel_interview_details where recruitment_id =? and panel_emp_id is null order by round_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				roundIdsRecruitwiseList.add(rst.getString("round_id"));
				if(uF.parseToInt(rst.getString("assessment_id"))>0) {
					hmRoundAssessment.put(rst.getString("round_id")+"_ID", rst.getString("assessment_id"));
					hmRoundAssessment.put(rst.getString("round_id")+"_NAME", hmAssessmentName.get(rst.getString("assessment_id")));
				}
			}
			rst.close();
    		pst.close();
			request.setAttribute("roundIdsRecruitwiseList", roundIdsRecruitwiseList);
			request.setAttribute("hmRoundAssessment", hmRoundAssessment);
			
			Map<String, List<String>> hmpanelIDSRAndRwise = new HashMap<String, List<String>>();
			List<String> panelEmpIDRAndRwiseList = new ArrayList<String>();
			pst = con.prepareStatement("select recruitment_id,round_id,panel_emp_id from panel_interview_details where recruitment_id =? and panel_emp_id is not null");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				panelEmpIDRAndRwiseList = hmpanelIDSRAndRwise.get(rst.getString("round_id"));
				if(panelEmpIDRAndRwiseList == null)panelEmpIDRAndRwiseList = new ArrayList<String>();
				panelEmpIDRAndRwiseList.add(rst.getString("panel_emp_id"));
				hmpanelIDSRAndRwise.put(rst.getString("round_id"), panelEmpIDRAndRwiseList);
			}
			rst.close();
    		pst.close();
    		
    		Map<String, String> hmpanelNameRAndRwise = new HashMap<String, String>();
			List<String> panelEmpIDList1RAndRwise = new ArrayList<String>();
			Iterator<String> it = hmpanelIDSRAndRwise.keySet().iterator();
			while (it.hasNext()) {
				String roundId = it.next();
				panelEmpIDList1RAndRwise = hmpanelIDSRAndRwise.get(roundId);
				String panelEmpNamesRAndRwise = uF.showData(getAppendDataList1(con, panelEmpIDList1RAndRwise), "");
				hmpanelNameRAndRwise.put(roundId, panelEmpNamesRAndRwise);
			}
			
			
//			Map<String, String> hmpanelNameRAndRwise = new HashMap<String, String>();
//			List<String> panelEmpIDList1RAndRwise = new ArrayList<String>(); 
//			pst = con.prepareStatement("select recruitment_id,round_id,panel_emp_id from panel_interview_details where recruitment_id =? and panel_emp_id is not null");
//			pst.setInt(1, uF.parseToInt(getRecruitId()));
//			rst = pst.executeQuery();
////			System.out.println("new Date ===> "+ new Date());
//			while (rst.next()) {
//				panelEmpIDList1RAndRwise = hmpanelIDSRAndRwise.get(rst.getString("round_id"));
//				if(panelEmpIDList1RAndRwise == null) panelEmpIDList1RAndRwise = new ArrayList<String>();
//				
//				String panelEmpNamesRAndRwise = uF.showData(getAppendDataList1(con, panelEmpIDList1RAndRwise), "");
//				hmpanelNameRAndRwise.put(rst.getString("round_id"), panelEmpNamesRAndRwise);
//			}
//			rst.close();
//    		pst.close();
			request.setAttribute("hmpanelNameRAndRwise", hmpanelNameRAndRwise);
//			request.setAttribute("hmpanelIDSRAndRwise", hmpanelIDSRAndRwise);
			
			
			pst = con.prepareStatement("select * from candidate_interview_panel where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				alInner=new ArrayList<String>();

				alInner.add(uF.getDateFormat(rst.getString("interview_date"), DBDATE, DATE_FORMAT) );
				if(rst.getString("interview_time") != null){
					alInner.add(rst.getString("interview_time").substring(0, 5));
				}else {
					alInner.add(uF.showData(rst.getString("interview_time"),"-"));
				}
				if(rst.getBoolean("is_interview_taken")){
				    hmPanelInterviewTaken.put(rst.getString("panel_round_id"), alInner);
				}else if(!rst.getBoolean("is_interview_taken")){
					hmPanelScheduleInfo.put(rst.getString("panel_round_id"), alInner);
				}
			}
			rst.close();
    		pst.close();
    		
//			System.out.println("hmPanelScheduleInfo ===> " + hmPanelScheduleInfo);
//			System.out.println("hmPanelInterviewTaken ===> " + hmPanelInterviewTaken);
			
			request.setAttribute("hmPanelScheduleInfo", hmPanelScheduleInfo);
			request.setAttribute("hmPanelInterviewTaken", hmPanelInterviewTaken);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private String getAppendDataList1(Connection con, List<String> strIDList) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

		for (int i =0; strIDList != null && i<strIDList.size(); i++) {

			if(strIDList.get(i)!=null && !strIDList.get(i).equals("") && !strIDList.get(i).equals("null")) {

				if(i==strIDList.size()-1) {
					sb.append(hmEmpName.get(strIDList.get(i).trim()));
				} else {
					sb.append(hmEmpName.get(strIDList.get(i).trim())+", ");
				}
			}
		}
		return sb.toString();
	}
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
	
		this.request=request;
	}

	 
     
     public String getPreferedDate() {
		return preferedDate;
	}

	public void setPreferedDate(String preferedDate) {
		this.preferedDate = preferedDate;
	}

	public String getPreferedTime() {
		return preferedTime;
	}

	public void setPreferedTime(String preferedTime) {
		this.preferedTime = preferedTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getiCount() {
		return iCount;
	}

	public void setiCount(String iCount) {
		this.iCount = iCount;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}


public String getPanelId() {
		return panelId;
	}

	public void setPanelId(String panelId) {
		this.panelId = panelId;
	}

	public String getDateinterview() {
		return dateinterview;
	}

	public void setDateinterview(String dateinterview) {
		this.dateinterview = dateinterview;
	}
	
	public String getInterviewTime() {
		return interviewTime;
	}

	public void setInterviewTime(String interviewTime) {
		this.interviewTime = interviewTime;
	}
	
	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}
	
	public String getNotiStatus() {
		return notiStatus;
	}

	public void setNotiStatus(String notiStatus) {
		this.notiStatus = notiStatus;
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	String pageFrom;

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}



	public String getJob_title() {
		return job_title;
	}



	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}



	public String getRecruiter_name() {
		return recruiter_name;
	}



	public void setRecruiter_name(String recruiter_name) {
		this.recruiter_name = recruiter_name;
	}
	
}