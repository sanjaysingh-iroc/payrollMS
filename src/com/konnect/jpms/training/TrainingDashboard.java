package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainingDashboard extends ActionSupport implements
		ServletRequestAware, IConstants {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private int empId;

	CommonFunctions CF = null;

	
	private static Logger log = Logger.getLogger(TrainingDashboard.class);

	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}

		/*
		 * boolean isView = CF.getAccess(session, request, uF); if(!isView){
		 * request.setAttribute(PAGE, PAccessDenied);
		 * request.setAttribute(TITLE, TAccessDenied); return ACCESS_DENIED; }
		 */

		request.setAttribute(PAGE, "/jsp/training/TrainingDashboard.jsp");
		request.setAttribute(TITLE, "Training Dashboard");

		prepareInformation(uF);

		return SUCCESS;

	}

	private void prepareInformation(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);

//			Map hmwlocation = CF.getWLocationMap(con, null, null);

			// Certificate given count***************
			Map<String, String> hmIsCertificateGiven = new HashMap<String, String>();

			pst = con.prepareStatement("select plan_id,count(*) as count  from training_learnings "
							+ "where is_certificate_given=1 group by plan_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmIsCertificateGiven.put(rst.getString("plan_id"),
						rst.getString("count"));
			}
			rst.close();
			pst.close();

			// Trainer name hashmap **********
			Map<String, String> hmTrainerName = new HashMap<String, String>();
			pst = con.prepareStatement("select * from training_trainer");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmTrainerName.put(rst.getString("trainer_id"),
						rst.getString("trainer_name"));
			}
			rst.close();
			pst.close();

			StringBuilder strquery = new StringBuilder();
			strquery.append("select * from training_plan join training_schedule using (plan_id) ");

			// Trainer filter...

			List<List<String>> alFinishedTraining = new ArrayList<List<String>>();
			List<List<String>> alOngoingTraining = new ArrayList<List<String>>();
			List<List<String>> alScheduledTraining = new ArrayList<List<String>>();

			pst = con.prepareStatement(strquery.toString());

			rst = pst.executeQuery();

			while (rst.next()) {

				List<String> alInner = new ArrayList<String>();
				alInner.add(rst.getString("plan_id"));
				alInner.add(rst.getString("training_title"));
				if (rst.getString("training_type") != null && rst.getString("training_type").equalsIgnoreCase("1"))
					alInner.add("Trainer Driven");
				else
					alInner.add("Self Driven");

				if (rst.getString("emp_ids") == null)
					alInner.add("0");
				else {
					int count = countTrainees(rst.getString("emp_ids"), uF);
					alInner.add(count + "");
				}
				alInner.add(uF.showData(hmIsCertificateGiven.get(rst.getString("plan_id")), "0"));

				alInner.add(uF.getDateFormat(rst.getString("start_date"),DBDATE, DATE_FORMAT));
				alInner.add(uF.getDateFormat(rst.getString("end_date"), DBDATE,DATE_FORMAT));

				if (dateDifferenceCurrent(uF, rst.getDate("start_date")) > 0) {
					alScheduledTraining.add(alInner);
				} else if (dateDifferenceCurrent(uF, rst.getDate("end_date")) < 0) {
					alFinishedTraining.add(alInner);
				} else {
					alOngoingTraining.add(alInner);
				}
			}
			rst.close();
			pst.close();
			
			request.setAttribute("alOngoingTraining", alOngoingTraining);
			request.setAttribute("alFinishedTraining", alFinishedTraining);
			request.setAttribute("alScheduledTraining", alScheduledTraining);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private int dateDifferenceCurrent(UtilityFunctions uF, Date comparisonDate) {

		SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
		Date today = Calendar.getInstance().getTime();

		String currentDate = dateformatyyyyMMdd.format(today);
		String compareDate = dateformatyyyyMMdd.format(comparisonDate);

		int datediff = uF.parseToInt(uF.dateDifference(currentDate, DBDATE,compareDate, DBDATE));

		return datediff - 1;
	}

	private int countTrainees(String empIds, UtilityFunctions uF) {

		String[] empID = empIds.split(",");

		return empID.length - 1;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

	String filterTraining;

	public String getFilterTraining() {
		return filterTraining;
	}

	public void setFilterTraining(String filterTraining) {
		this.filterTraining = filterTraining;
	}

}