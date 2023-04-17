package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateInterviewSchedule extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger
			.getLogger(CandidateInterviewSchedule.class);

	String strUserType;
	String strSessionEmpId;
	HttpSession session;

	String candidateID;
	String panelEmpID;
	String recruitID;
	String roundID;

	CommonFunctions CF;

	Map<String, String> hmTimeMap = new HashMap<String, String>();

	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		Map<String, String> hmDateMap = new HashMap<String, String>();
		/*
		 * boolean isView = CF.getAccess(session, request, uF); if(!isView){
		 * request.setAttribute(PAGE, PAccessDenied);
		 * request.setAttribute(TITLE, TAccessDenied); return ACCESS_DENIED; }
		 */
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE,
				"/jsp/recruitment/CandidateInterviewSchedule.jsp");
		request.setAttribute(TITLE, "Interview Schedule");

		addInterviewDates(uF, hmDateMap);

		if (getPagefrom() != null
				&& getPagefrom().equalsIgnoreCase("MyProfile")) {

			if (getAcceptDate() != null) {
				hrApproveDenyDates(uF, hmDateMap);
				return "candprofile";
			}
			return "loadpopup";

		}

		if (getAcceptDate() != null)
			return approveDenyDates(uF, hmDateMap);

		return LOAD;

	}

	private void hrApproveDenyDates(UtilityFunctions uF, Map<String, String> hmDateMap) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;

		try {
			con = db.makeConnection(con);

			String selectionDate = request.getParameter("selectionDate");
			String selectOtherDate = request.getParameter("interviewdate");
			String selectOtherTime = request.getParameter("interviewTime");
			if (selectionDate != null && selectionDate.equals("0")) {
				
				Iterator<String> it = hmDateMap.keySet().iterator();

				while (it.hasNext()) {
					String id = it.next();

					pst = con.prepareStatement("insert into candidate_interview_panel_availability"
									+ " (int_avail_id,is_live,panel_emp_id,recruitment_id,candidate_id)values(?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(id));
					pst.setInt(2, -1);
					pst.setInt(3, uF.parseToInt(getPanelEmpID()));
					pst.setInt(4, uF.parseToInt(getRecruitID()));
					pst.setInt(5, uF.parseToInt(getCandidateID()));
					pst.execute();
					pst.close();
				}
				pst = con.prepareStatement("insert into candidate_interview_panel (recruitment_id,candidate_id,panel_emp_id ,interview_date,interview_time) values(?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitID()));
				pst.setInt(2, uF.parseToInt(getCandidateID()));
				pst.setInt(3, uF.parseToInt(getPanelEmpID()));
				pst.setDate(4, uF.getDateFormat(selectOtherDate, DATE_FORMAT));
				pst.setTime(5, uF.getTimeFormat(selectOtherTime, TIME_FORMAT));
//				System.out.println("pst===" + pst);
				pst.execute();
				pst.close();
			} else{
				pst = con.prepareStatement("insert into candidate_interview_panel (recruitment_id,candidate_id,panel_emp_id ,interview_date,interview_time) values(?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitID()));
				pst.setInt(2, uF.parseToInt(getCandidateID()));
				pst.setInt(3, uF.parseToInt(getPanelEmpID()));
				pst.setDate(4, uF.getDateFormat(hmDateMap.get(selectionDate), DATE_FORMAT));
				pst.setTime(5, uF.getTimeFormat(hmTimeMap.get(selectionDate), TIME_FORMAT));
		//		System.out.println("pst===" + pst);
				pst.execute();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private String approveDenyDates(UtilityFunctions uF,
			Map<String, String> hmDateMap) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;

		try {
			con = db.makeConnection(con);

			String selectionDate = request.getParameter("selectionDate");
			String selectOtherDate = request.getParameter("interviewdate");
			String selectOtherTime = request.getParameter("interviewTime");
//			System.out.println("printing selection date ==" + selectionDate);
			if (selectionDate != null && selectionDate.equals("0")) {

				Iterator<String> it = hmDateMap.keySet().iterator();
				while (it.hasNext()) {
					String id = it.next();
					pst = con.prepareStatement("insert into candidate_interview_panel_availability"
							+ " (int_avail_id,is_live,panel_emp_id,recruitment_id,candidate_id)values(?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(id));
					pst.setInt(2, -1);
					pst.setInt(3, uF.parseToInt(getPanelEmpID()));
					pst.setInt(4, uF.parseToInt(getRecruitID()));
					pst.setInt(5, uF.parseToInt(getCandidateID()));
					pst.execute();
					pst.close();
				}
				pst = con.prepareStatement("insert into candidate_interview_panel (recruitment_id,candidate_id,panel_emp_id ,interview_date,interview_time) values(?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitID()));
				pst.setInt(2, uF.parseToInt(getCandidateID()));
				pst.setInt(3, uF.parseToInt(getPanelEmpID()));
				pst.setDate(4, uF.getDateFormat(selectOtherDate, DATE_FORMAT));
				pst.setTime(5, uF.getTimeFormat(selectOtherTime, TIME_FORMAT));
//				System.out.println("pst===" + pst);
				pst.execute();
				pst.close();
			} else {
				pst = con.prepareStatement("insert into candidate_interview_panel (recruitment_id,candidate_id,panel_emp_id ,interview_date,interview_time) values(?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitID()));
				pst.setInt(2, uF.parseToInt(getCandidateID()));
				pst.setInt(3, uF.parseToInt(getPanelEmpID()));
				pst.setDate(4, uF.getDateFormat(hmDateMap.get(selectionDate), DATE_FORMAT));
				pst.setTime(5, uF.getTimeFormat(hmTimeMap.get(selectionDate), TIME_FORMAT));
//				System.out.println("pst===" + pst);
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	private void addInterviewDates(UtilityFunctions uF,
			Map<String, String> hmDateMap) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;

		// List<String> alRejectedDateList=new ArrayList<String>();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from candidate_interview_availability where recruitment_id=? and int_avail_id not in(select int_avail_id from candidate_interview_panel_availability where panel_emp_id=?) order by int_avail_id limit 3 ");
			pst.setInt(1, uF.parseToInt(getRecruitID()));
			pst.setInt(2, uF.parseToInt(getPanelEmpID()));

			// String SelectedDate=null;
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {

				hmDateMap.put(rst.getString("int_avail_id"), uF.getDateFormat(
						rst.getString("_date"), DBDATE, DATE_FORMAT));

				hmTimeMap.put(rst.getString("int_avail_id"),
						getTimeFormat(rst.getString("_time")));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmSelectedDate = new HashMap<String, String>();
			pst = con.prepareStatement("select * from candidate_interview_panel where recruitment_id=? and  panel_round_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitID()));
			pst.setInt(2, uF.parseToInt(getCandidateID()));
			rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmSelectedDate.put(rst.getString("int_avail_id"), uF.getDateFormat(rst.getString("_date"), DBDATE,
					DATE_FORMAT));
				}
			rst.close();
			pst.close();
			
			Map<String, String> hmRejectedDate = new HashMap<String, String>();

			pst = con.prepareStatement("select * from candidate_interview_availability where int_avail_id in(select int_avail_id from candidate_interview_panel_availability where recruitment_id=? and candidate_id=? and panel_emp_id=?  ) ");
			pst.setInt(1, uF.parseToInt(getRecruitID()));
			pst.setInt(2, uF.parseToInt(getCandidateID()));
			pst.setInt(3, uF.parseToInt(getPanelEmpID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
//				if (rst.getInt("is_live") == 1)
//					hmSelectedDate.put(rst.getString("int_avail_id"), uF
//							.getDateFormat(rst.getString("_date"), DBDATE,
//									DATE_FORMAT));
//				else if (rst.getInt("is_live") == -1)
					hmRejectedDate.put(rst.getString("int_avail_id"), uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT));
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmSelectedDate", hmSelectedDate);
			request.setAttribute("hmRejectedDate", hmRejectedDate);
//			System.out.println("ppppp" + hmSelectedDate);
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

		if (time != null && !time.equals(""))
			return time.substring(0, 5);
		else
			return "";
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getCandidateID() {
		return candidateID;
	}

	public void setCandidateID(String candidateID) {
		this.candidateID = candidateID;
	}

	public String getPanelEmpID() {
		return panelEmpID;
	}

	public void setPanelEmpID(String panelEmpID) {
		this.panelEmpID = panelEmpID;
	}

	String acceptDate;

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getRecruitID() {
		return recruitID;
	}

	public void setRecruitID(String recruitID) {
		this.recruitID = recruitID;
	}

	String pagefrom;

	public String getPagefrom() {
		return pagefrom;
	}

	public void setPagefrom(String pagefrom) {
		this.pagefrom = pagefrom;
	}

	public String getRoundID() {
		return roundID;
	}

	public void setRoundID(String roundID) {
		this.roundID = roundID;
	}

}