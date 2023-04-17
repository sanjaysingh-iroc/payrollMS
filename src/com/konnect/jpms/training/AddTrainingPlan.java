

package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.performance.FillFrequency;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTrainingPlan extends ActionSupport implements ServletRequestAware, IStatements,Runnable {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	
	private String operation;
//	String trainingType;
	private String ID;
	private String attriID;
	private String lPlanId;
	private String lPlanStep;
	private String frmpage;
	
	CommonFunctions CF = null;

	
	private String alignedwith;
	private String certificateId;
	private String plan_organization;
	private String plan_idwlocation;
    
	private String del;
	private String quest_id;

    
	private String annualDay;
	private String annualMonth;
	private String day;
	private String monthlyDay;
	private String month;
	private String weekday;
	
	private String weekdayValue;
	private String dayValue;
	private String scheduleTypeValue;
	private List<String> weekdaysValue = new ArrayList<String>();
	
	private static Logger log = Logger.getLogger(AddTrainingPlan.class);
	
	private List<String> attributeID=new ArrayList<String>();
	private List<String> trainingLocationValue=new ArrayList<String>();
	private List<List<String>> alSessionData=new ArrayList<List<String>>();
	
	private List<FillWLocation> workList;
	private List<FillOrganisation> organisationList;
	private List<FillFrequency> frequencyList;
	private List<String> plan_idwlocationvalue=new ArrayList<String>();

	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		UtilityFunctions uF = new UtilityFunctions();
		String strOperation = request.getParameter("operation");
		String strID = request.getParameter("ID");
		setlPlanStep("1");
		request.setAttribute(PAGE, "/jsp/training/AddTrainingPlan.jsp");
		if (strOperation != null && strOperation.equalsIgnoreCase("E")){
			request.setAttribute(TITLE, "Edit Training Plan");
		}else{
			request.setAttribute(TITLE, "Add Training Plan");
		}
 				
		if((strOperation != null && !strOperation.equalsIgnoreCase("E")) && (getWeekdaysValue() == null || 
				getWeekdaysValue().equals("") || getWeekdaysValue().size() == 0)) {
			weekdaysValue.add("Mon");
			weekdaysValue.add("Tue");
			weekdaysValue.add("Wed");
			weekdaysValue.add("Thu");
			weekdaysValue.add("Fri");
			weekdaysValue.add("Sat");
			weekdaysValue.add("Sun");
		}
		
		levelList = new FillLevel(request).fillLevel();
		gradeList = new FillGrade(request).fillGrade();
		attributeList = new FillAttribute(request).fillAttribute();
		locationList = new FillWLocation(request).fillWLocation();

		desigList = new FillDesig(request).fillDesig();
		if(getScheduleTypeValue() == null || getScheduleTypeValue().equals("")) {
			setScheduleTypeValue("1");
		}
		if(getPlan_idwlocation()==null){
			setPlan_idwlocation((String)session.getAttribute(WLOCATIONID));
		}
		if(getPlan_organization()==null){
			setPlan_organization((String)session.getAttribute(ORGID));
		}
		organisationList = new FillOrganisation(request).fillOrganisation();
		workList = new FillWLocation(request).fillWLocation(getPlan_organization());

		certificateList = new FillCertificate(request).fillCertificateList();
		if (strOperation != null && strOperation.equalsIgnoreCase("A")) {
			certificateList.add(new FillCertificate("0","Create New Certificate"));
		}
		
		frequencyList=new FillFrequency(request).fillFrequency();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		if(getPlanId() == null){
			setPlanId(strID);
		}
		
		getselectedTrainersList();
		
		if (strOperation != null && strOperation.equalsIgnoreCase("A")) {
			insertData();
		} else if (strOperation != null && strOperation.equalsIgnoreCase("E")) {				
			updateData(strID);
			loadFilledData(strID);
		} else if (strOperation != null && strOperation.equalsIgnoreCase("D")) {
			return deletePlan(strID);
		}

		if (step == null || uF.parseToInt(getStep()) == 0) {
			setStep("1");
		} else if (uF.parseToInt(getStep()) == 1) {
			setStep("2");
			trainerList = new FillTrainer(request).fillTrainer();
			getTrainersList();
		} else if (uF.parseToInt(getStep()) == 2) {
			setStep("3");
		} else if (uF.parseToInt(getStep()) == 3) {
			getPlanDetails();
			setStep("4");
		}else if (uF.parseToInt(getStep()) == 4) {
			setStep("5");			
		}else if (uF.parseToInt(getStep()) == 5) {		
//			System.out.println("step==>"+getStep()+"getStepSubmit()==>"+getStepSubmit()+"==>getStepSave==>"+getStepSave());
			return SUCCESS; 
			/*if ((getStepSubmit() != null && getStepSubmit().equals("Submit & Proceed")) || (getStepSave() != null && getStepSave().equals("Save & Exit"))){
				System.out.println("inside if");
				return SUCCESS; 
			}*/
		}
		
		if (getStepSave() != null && getStepSave().equals("Save & Exit")) {
			return SUCCESS;
		}
		
		getSelectEmployeeList();
		getAnsType();
		getQuestionList();
		getplan_locationList();
		if(getStepSaveGoBack() != null ) {
			return "successgoback";
		}
		
		return LOAD;

	}
	
private void getPlanDetails() {

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rst = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();
	try {

		con = db.makeConnection(con);
		
		pst = con.prepareStatement("select schedule_id,start_date,end_date,day_schedule_type,training_frequency,training_day,training_weekday from training_schedule where plan_id=?");
		pst.setInt(1, uF.parseToInt(getPlanId()));
		rst=pst.executeQuery();
		String scheduleId = null;
		String startDate = null;
		String endDate = null;
		String scheduleType = null;
		String trainingFrequency = null;
		String trainingDay = null;
		String trainingWeekDay = null;
		while(rst.next()){	
			scheduleId = rst.getString("schedule_id");
			startDate = rst.getString("start_date");
			endDate = rst.getString("end_date");
			scheduleType = rst.getString("day_schedule_type");
			trainingFrequency = rst.getString("training_frequency");
			trainingDay = rst.getString("training_day");
			trainingWeekDay = rst.getString("training_weekday");
		}
		rst.close();
		pst.close();
		
		Map<String, String> hmDayDescription = new HashMap<String, String>();
		pst = con.prepareStatement("select training_schedule_id,day_date,day_description,long_description from training_schedule_details where training_id=? and training_schedule_id = ?");
		pst.setInt(1, uF.parseToInt(getPlanId()));
		pst.setInt(2, uF.parseToInt(scheduleId));
		rst=pst.executeQuery();
		while(rst.next()){	
			String dayDate = uF.getDateFormat(rst.getString("day_date"), DBDATE, DATE_FORMAT);
			hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate+"_S", rst.getString("day_description"));
			hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate+"_L", rst.getString("long_description"));
		}
		rst.close();
		pst.close();
		
		//System.out.println("scheduleType ===> " + scheduleType);
		String wkDays = null;
		List<String> frequencyDateList = new ArrayList<String>();
		if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
			pst = con.prepareStatement("select week_days from training_session where schedule_id = ? limit 1");
			pst.setInt(1, uF.parseToInt(scheduleId));
			rst=pst.executeQuery();
			while(rst.next()){	
				wkDays = rst.getString("week_days");
			}
			rst.close();
			pst.close();
		} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {
			pst = con.prepareStatement("select frequency_date from training_session where schedule_id = ?");
			pst.setInt(1, uF.parseToInt(scheduleId));
			rst=pst.executeQuery();
			while(rst.next()){	
				frequencyDateList.add(rst.getString("frequency_date"));
			}
			rst.close();
			pst.close();
		}
		
		
		String dayCount = uF.dateDifference(startDate, DBDATE, endDate, DBDATE);
		
		//System.out.println("dayCount ===> " + dayCount);
		
		List<String> daysList = new ArrayList<String>();
		Map<String, String> hmdaysDate = new HashMap<String, String>();
		
		if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
			if(trainingFrequency!=null && trainingFrequency.equals("1")){
				List<String> wkDayList = new ArrayList<String>();
				if(wkDays != null && !wkDays.equals("")) {
					wkDayList = Arrays.asList(getAppendData(wkDays).split(","));
				}
				
				String strNewDate = null;
				
				java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
				String strDay = new SimpleDateFormat("E").format(dtDate);
		        if(wkDayList != null && wkDayList.contains(strDay)){
		        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
					daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
		        } else if(wkDayList == null) {
		        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
					daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
		        }
				for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
					if(strNewDate == null)
					strNewDate = startDate;
					Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
					java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
					String strDay1 = new SimpleDateFormat("E").format(dtDate1);
					 if(wkDayList != null && wkDayList.contains(strDay1)){
						 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
							daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
				        }else if(wkDayList == null) {
				        	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
							daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));
				        }
					strNewDate = newDate+"";
				}
			} else if(trainingFrequency!=null && trainingFrequency.equals("2")){
				if(trainingWeekDay!=null){
					List<String> wkDayList = new ArrayList<String>();
					wkDayList.add(trainingWeekDay);
					String strNewDate = null;
					
					java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
					String strDay = new SimpleDateFormat("EEEE").format(dtDate);
			        if(wkDayList != null && wkDayList.contains(strDay)){
			        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
						daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
			        }
					for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
						if(strNewDate == null)
						strNewDate = startDate;
						Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
						java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
						String strDay1 = new SimpleDateFormat("EEEE").format(dtDate1);
						 if(wkDayList != null && wkDayList.contains(strDay1)){
							 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
								daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
					     }
						strNewDate = newDate+"";
					}
				}
			} else if(trainingFrequency!=null && trainingFrequency.equals("3")){
				if(trainingDay!=null){
					List<String> wkDayList = new ArrayList<String>();
					String strTDay = uF.parseToInt(trainingDay.trim()) < 10 ? "0"+trainingDay.trim() : trainingDay.trim();
					wkDayList.add(strTDay);
					String strNewDate = null;
					
					java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
					String strDay = new SimpleDateFormat("dd").format(dtDate);
			        if(wkDayList != null && wkDayList.contains(strDay)){
			        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
						daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
			        }
					for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
						if(strNewDate == null)
						strNewDate = startDate;
						Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
						java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
						String strDay1 = new SimpleDateFormat("dd").format(dtDate1);
						 if(wkDayList != null && wkDayList.contains(strDay1)){
							 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
								daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
					     }
						strNewDate = newDate+"";
					}
				}
			}
		} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {

			for(int i=0; frequencyDateList != null && !frequencyDateList.isEmpty() && i< frequencyDateList.size(); i++) {
				hmdaysDate.put(uF.getDateFormat(frequencyDateList.get(i), DBDATE, DATE_FORMAT), uF.getDateFormat(frequencyDateList.get(i), DBDATE, CF.getStrReportDateFormat()));
				daysList.add(uF.getDateFormat(frequencyDateList.get(i), DBDATE, DATE_FORMAT));
			}
		}
		
//		System.out.println("daysList ====> " + daysList);
		Collections.sort(daysList);
//		System.out.println("daysList after ====> " + daysList);
		request.setAttribute("daysList", daysList);
		request.setAttribute("hmdaysDate", hmdaysDate);
		request.setAttribute("scheduleId", scheduleId);
		request.setAttribute("hmDayDescription", hmDayDescription);
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

//	private void getMarksGradeType(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from training_mark_grade_type");
//			rs = pst.executeQuery();
//			Map<String, List<List<String>>> hmGradeStandardwiseValue = new HashMap<String, List<List<String>>>();
//			List<List<String>> gradeStandardwiseList = new ArrayList<List<String>>();
//			while(rs.next()){
//				
//				gradeStandardwiseList = hmGradeStandardwiseValue.get(rs.getString("grade_standard"));
//				if(gradeStandardwiseList == null) gradeStandardwiseList = new ArrayList<List<String>>();
//				
//				List<String> innerList = new ArrayList<String>();
//				innerList.add(rs.getString("numeric_grade_type"));
//				innerList.add(rs.getString("alphabet_grade_type"));
//				gradeStandardwiseList.add(innerList);
//				hmGradeStandardwiseValue.put(rs.getString("grade_standard"), gradeStandardwiseList);
//				
//			}
//			System.out.println("hmGradeStandardwiseValue ===> " + hmGradeStandardwiseValue);
//			request.setAttribute("hmGradeStandardwiseValue", hmGradeStandardwiseValue);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//	}

	/*private void getCertificatePrintMode() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_certificate");
			rs = pst.executeQuery();
			Map<String,String> hmCertificatePrintMode=new HashMap<String, String>();
			while(rs.next()){
				hmCertificatePrintMode.put(rs.getString("certificate_id"), rs.getString("print_mode"));
			}
			request.setAttribute("hmCertificatePrintMode", hmCertificatePrintMode);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}*/

//	private void deleteQuestion(String strID) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("delete from training_question_details where training_question_id=? and plan_id=?");
//			pst.setInt(1, uF.parseToInt(getQuest_id()));
//			pst.setInt(2, uF.parseToInt(strID));
//			rs = pst.executeQuery();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//	}

	public void getQuestionList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_question_bank where is_add=true");
			rs = pst.executeQuery();
			while (rs.next()) {
				sb.append("<option value=\"" + rs.getString("training_question_bank_id")
						+ "\">" + rs.getString("question_text") + "</option>");
			}
			rs.close();
			pst.close();

			sb.append("<option value=\"0\">Add new Question</option>");

			request.setAttribute("option", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getAnsType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_answer_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("answer_type")) == 9) {
					sb.append("<option value=\""
							+ rs.getString("answer_type")
							+ "\" selected>"
							+ rs.getString("answer_type_name")
							+ "</option>");
				} else {
					sb.append("<option value=\""
							+ rs.getString("answer_type") + "\">"
							+ rs.getString("answer_type_name")
							+ "</option>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("anstype", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
private void loadFilledData(String strID) {

	Database db = new Database();
	db.setRequest(request);
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rst = null;
	UtilityFunctions uF = new UtilityFunctions();
	try {
		con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_plan where plan_id=?");
			pst.setInt(1, uF.parseToInt(strID));

			rst = pst.executeQuery();
			while (rst.next()) {
				setAlignedwith(rst.getString("alignedwith"));
			}
			rst.close();
			pst.close();
			
		}  catch (Exception e) {

		e.printStackTrace();
	} finally {
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	}



	private void getSelectEmployeeList() {
			
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {
			
			con=db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			pst=con.prepareStatement("select emp_ids from training_schedule where plan_id=?");
			pst.setInt(1,uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			while(rst.next()){				
				selectEmpIDs=rst.getString("emp_ids");
			}
			rst.close();
			pst.close();
			
			List<String> selectEmpList=new ArrayList<String>();
			Map<String,String> hmCheckEmpList=new HashMap<String, String>();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
				List<String> tmpselectEmpList=Arrays.asList(selectEmpIDs.split(","));
				Set<String> trainerSet = new HashSet<String>(tmpselectEmpList);
				Iterator<String> itr = trainerSet.iterator();
				while (itr.hasNext()) {
					String trainerId = (String) itr.next();
					if(trainerId!=null && !trainerId.equals("")){
						selectEmpList.add(hmEmpName.get(trainerId.trim()));
						hmCheckEmpList.put(trainerId.trim(), trainerId.trim());
					}
				}
			} else {
				selectEmpList=null;
			}
			request.setAttribute("selectEmpList", selectEmpList);
			request.setAttribute("hmCheckEmpList", hmCheckEmpList);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void getplan_locationList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {
			
			con=db.makeConnection(con);
			
			pst = con.prepareStatement("select location_id from training_plan where plan_id=?");
			pst.setInt(1, uF.parseToInt(getPlanId()));
			rst = pst.executeQuery();
//			System.out.println("pst ====> "+pst);
			List<String> plan_locationList=new ArrayList<String>();
			while (rst.next()) {										
				if(rst.getString("location_id")==null || rst.getString("location_id").equals("")){
					plan_locationList=null;
				}else{
					List<String> locationValue1 = new ArrayList<String>();
					locationValue1 = Arrays.asList(rst.getString("location_id").split(","));
					for (int i = 0; i < locationValue1.size(); i++) {
						if(locationValue1.get(i)!=null && !locationValue1.get(i).equals("")){
							plan_locationList.add(locationValue1.get(i).trim());
						}
					}
				}
			}
			rst.close();
			pst.close();
			
//			System.out.println("plan_locationList ====> "+plan_locationList);
			request.setAttribute("plan_locationList", plan_locationList);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
private void getTrainersList() {
	
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rst = null;
	UtilityFunctions uF = new UtilityFunctions();
	try {
		
		con=db.makeConnection(con);
		
		pst=con.prepareStatement("select eod.emp_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive = false");
		rst=pst.executeQuery();
		List<String> alExEmp = new ArrayList<String>();
 		while(rst.next()){
 			alExEmp.add(rst.getString("emp_id"));
 		}
		
		pst=con.prepareStatement("select * from training_trainer");
		rst=pst.executeQuery();
		List<List<String>> trainerOuterList=new ArrayList<List<String>>();
 		while(rst.next()){
			
			if(rst.getString("trainer_emp_id") != null && !rst.getString("trainer_emp_id").equals("")){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rst.getString("trainer_id"));	
				innerList.add(rst.getString("trainer_emp_id"));
				innerList.add("EXTrainer");
				innerList.add(rst.getString("trainer_name"));
				trainerOuterList.add(innerList);
				
			} else if(rst.getString("emp_id") != null && !rst.getString("emp_id").equals("") && (uF.parseToInt(rst.getString("emp_id")) > 0 && !alExEmp.contains(rst.getString("emp_id")))){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rst.getString("trainer_id"));	
				innerList.add(rst.getString("emp_id"));
				innerList.add("INTrainer");
				innerList.add(rst.getString("trainer_name"));
				trainerOuterList.add(innerList);
			}
			
			
			
		}
 		rst.close();
		pst.close();
		
		request.setAttribute("trainerOuterList", trainerOuterList);
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	
}

private void getselectedTrainersList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		try { 
			
			con=db.makeConnection(con);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con, null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
//			System.out.println("hmWLocation ===> "+hmWLocation);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
			Map<String, String> hmtrainer=new HashMap<String, String>();
			pst=con.prepareStatement("select * from training_trainer");
			rst=pst.executeQuery();
			while(rst.next()){
				hmtrainer.put(rst.getString("trainer_id"),rst.getString("trainer_name"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmtrainer ===> " + hmtrainer);
			
			pst=con.prepareStatement("select trainer_ids from training_schedule where plan_id=?");
			pst.setInt(1,uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			String trainerIDs=null;
			while(rst.next()){
				trainerIDs=rst.getString("trainer_ids");
			}
			rst.close();
			pst.close();
			
			
			List<List<String>> selectedTrainerList = new ArrayList<List<String>>();
			Map<String, String> hmSelectedTrainer=new HashMap<String, String>();
			if(trainerIDs!=null){
				List<String> tmpTrainerList=Arrays.asList(trainerIDs.split(","));
				Set<String> trainerSet = new HashSet<String>(tmpTrainerList);
				Iterator<String> itr = trainerSet.iterator();
	
				while (itr.hasNext()) {
					List<String> innerList=new ArrayList<String>();
					String trainerId = (String) itr.next();
					if(trainerId!=null && !trainerId.equals("") && uF.parseToInt(trainerId) > 0){
						innerList.add(trainerId.trim());
						innerList.add(hmtrainer.get(trainerId.trim()));
						selectedTrainerList.add(innerList);
						hmSelectedTrainer.put(trainerId.trim(), trainerId.trim());
					}
				}
			}
//			System.out.println("hmSelectedTrainer ===> " + hmSelectedTrainer);
//			System.out.println("selectedTrainerList ===> " + selectedTrainerList);
			
			request.setAttribute("hmSelectedTrainer", hmSelectedTrainer);
			request.setAttribute("selectedTrainerList", selectedTrainerList);
			request.setAttribute("trainerIds", trainerIDs);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private String deletePlan(String strID) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);

			String trainingName = getTrainingPlanNameById(con, uF, strID);
			
			pst = con.prepareStatement("delete from training_session where schedule_id=(select schedule_id from training_schedule where plan_id=?) ");
			pst.setInt(1, uF.parseToInt(getID()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from training_schedule where plan_id=? ");
			pst.setInt(1, uF.parseToInt(getID()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete  from training_plan where plan_id=?");
			pst.setInt(1, uF.parseToInt(getID()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+""+trainingName+" training plan has been deleted successfully."+END);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	

	private void updateData(String strID) {
//		System.out.println("inside edit");
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if (getStepSubmit() != null || getStepSave() != null || getStepSaveGoBack() != null) {
				insertUpdatedStepData(con, strID);
			}
			updateStepData(con, strID);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
		
	}

	private void insertUpdatedStepData(Connection con, String strID) {
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
//		ResultSet rst = null;
		try {
			if (uF.parseToInt(step) == 1) {
				//System.out.println("getStrAttribute() ===> "+getStrAttribute());
				pst = con.prepareStatement("update training_plan set training_title=?,training_objective=?,training_summary=?,training_type=?," +
						"is_certificate=?,attribute_id=?,level_id=? ,certificate_id=?,alignedwith=?,location_id=?,org_id=?,updated_by=?," +
						"update_date=? where plan_id=? "); //,marks_grade_type=?,marks_grade_standard=?
				pst.setString(1, getTrainingTitle());
				pst.setString(2, getTrainingObjective());
				pst.setString(3, getTrainingSummary());
				pst.setString(4, "1");
				pst.setBoolean(5, getStrCertificate());
				pst.setString(6, getStrAttribute());
				pst.setInt(7, uF.parseToInt(getStrLevel()));
				pst.setInt(8, uF.parseToInt(getStrCertificateId()));
				/*if (getStrCertificate() == true){
					String[] temp=getStrCertificateId().split("::::");
					pst.setInt(8, uF.parseToInt(temp[0]));
				}else{
					pst.setInt(8, uF.parseToInt("-1"));
				}*/
				pst.setInt(9, uF.parseToInt(getAlignedwith()));
				pst.setString(10, getPlan_idwlocation());
				pst.setString(11, getPlan_organization());				
				pst.setInt(12, uF.parseToInt(strSessionEmpId));
				pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(14, uF.parseToInt(strID));
				pst.execute();
				pst.close();
				
				setPlanId(strID);

			} else if (uF.parseToInt(step) == 2) {

				insertStep2Data(con);

			} else if (uF.parseToInt(step) == 3) {
				
				 insertStep3Data(con);

			} else if (uF.parseToInt(step) == 4) {
				
				insertStep4Data(con);

			} else if (uF.parseToInt(step) == 5) {

				insertStep6Data(con);
			}/*else if (uF.parseToInt(step) ==5) {

				insertStep5Data();

			}*/

			String trainingName = getTrainingPlanNameById(con, uF, strID);
			session.setAttribute(MESSAGE, SUCCESSM+""+trainingName+" training plan has been updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private String getTrainingPlanNameById(Connection con, UtilityFunctions uF, String planId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		String lPlanName = null;
		try {
				pst = con.prepareStatement("select training_title from training_plan where plan_id = ?");
				pst.setInt(1, uF.parseToInt(planId));
				rst = pst.executeQuery();
				while (rst.next()) {
					lPlanName = rst.getString("training_title");
				}
				rst.close();
				pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return lPlanName;
	}
	
	
	
	private void updateStepData(Connection con, String strID) {

		PreparedStatement pst = null;
		ResultSet rst = null;
//		System.out.println("step ===> "+step);
		String step1 = step;
		/*if(getTrainingType() != null && getTrainingType().equals("0") && step1 != null && step1.equals("1")){
			step1 = "2";
		}*/
		UtilityFunctions uF = new UtilityFunctions();
		try {

			if (uF.parseToInt(step1) == 0) {

				pst = con.prepareStatement("select * from training_plan where plan_id=?");
				pst.setInt(1, uF.parseToInt(strID));
				rst = pst.executeQuery();
				while (rst.next()) {
					setTrainingTitle(rst.getString("training_title"));
					setTrainingObjective(rst.getString("training_objective"));
					setTrainingSummary(rst.getString("training_summary"));
//					setTrainingType(rst.getString("training_type"));
					setStrCertificate(rst.getBoolean("is_certificate"));
//					setMarksGradeType(rst.getString("marks_grade_type"));
//					setMarksGradeStandard(rst.getString("marks_grade_standard"));
//					setStrAttribute(rst.getString("attribute_id"));
					setStrLevel(rst.getString("level_id"));
					setStrCertificateId(rst.getString("certificate_id"));
					setCertificateId(rst.getString("certificate_id"));
					
					if(rst.getString("attribute_id")==null || rst.getString("attribute_id").equals("")){
					}else{
						List<String> attributeValue1 = new ArrayList<String>();
						attributeValue1 = Arrays.asList(rst.getString("attribute_id").split(","));
						for (int k = 0; k < attributeValue1.size(); k++) {
							if(attributeValue1.get(k)!=null && !attributeValue1.get(k).equals("")){
								attributeID.add(attributeValue1.get(k).trim());
							}
						}
					}
//					System.out.println("attributeID ===> " + attributeID);
					setAlignedwith(rst.getString("alignedwith"));
					
					setPlan_idwlocation(rst.getString("location_id"));
					
					if(rst.getString("location_id")==null || rst.getString("location_id").equals("")){
						locationList=null;
					}else{
						List<String> locationValue1 = new ArrayList<String>();
						locationValue1 = Arrays.asList(rst.getString("location_id").split(","));
						for (int k = 0; k < locationValue1.size(); k++) {
							if(locationValue1.get(k)!=null && !locationValue1.get(k).equals("")){
								plan_idwlocationvalue.add(locationValue1.get(k).trim());
							}
						}
					}
					setPlan_organization(rst.getString("org_id"));
				}
				rst.close();
				pst.close();
				
//				request.setAttribute("TRAININGTYPE", getTrainingType());
				// getting second screen data ******************

				request.setAttribute("strCertificateID", getStrCertificateId());

			} else if (uF.parseToInt(step1) == 1) {

				// preparing trainer map************

				Map<String, String> hmTrainerName = new HashMap<String, String>();

				pst = con.prepareStatement("select trainer_id,trainer_name from training_trainer");
				rst = pst.executeQuery();
				while (rst.next()) {
					hmTrainerName.put(rst.getString("trainer_id"), rst.getString("trainer_name"));
				}
				rst.close();
				pst.close();

				List<String> alselectedTrainerList = new ArrayList<String>();
				
				String[] exixtingTrainerIds = null;
				
				pst = con.prepareStatement("select trainer_ids  from training_schedule where plan_id=?");
				pst.setInt(1, uF.parseToInt(getID()));
				rst = pst.executeQuery();
				while (rst.next()) {
					if (rst.getString("trainer_ids") != null)
						exixtingTrainerIds = rst.getString("trainer_ids").split(",");
				}
				rst.close();
				pst.close();
				
				if (exixtingTrainerIds != null) {
					for (int i = 1; i < exixtingTrainerIds.length; i++) {
						alselectedTrainerList.add(exixtingTrainerIds[i].trim());
					}
				}
				request.setAttribute("alselectedTrainerList",alselectedTrainerList);
				request.setAttribute("trainerCount",alselectedTrainerList.size() + 1);
				request.setAttribute("hmTrainerName", hmTrainerName);
				
				pst = con.prepareStatement("select wlocation_id from training_schedule where plan_id=?");
				pst.setInt(1, uF.parseToInt(getID()));
				rst = pst.executeQuery();
				while (rst.next()) {										
					if(rst.getString("wlocation_id")==null || rst.getString("wlocation_id").equals("")){
						trainingLocationValue.add("");
					}else{
						List<String> trainingLocationValue1 = new ArrayList<String>();
						trainingLocationValue1 = Arrays.asList(rst.getString("wlocation_id").split(","));
						for (int i = 0; i < trainingLocationValue1.size(); i++) {
							trainingLocationValue.add(trainingLocationValue1.get(i).trim());
						}
					}
				}
				rst.close();
				pst.close();
				
			} else if (uF.parseToInt(step1) == 2) {

				String schedule_id = null;
				pst = con.prepareStatement("select * from training_schedule join training_session "
								+ "using (schedule_id) where plan_id=?");
				pst.setInt(1, uF.parseToInt(getID()));
//				System.out.println("pst==>"+pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					setStartdateTraining(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));
					setEnddateTraining(uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));
//					setNoofdaysTraining(rst.getString("training_duration"));
					setTrainingSchedulePeriod(rst.getString("training_frequency") != null ? rst.getString("training_frequency") : "1");
					schedule_id = rst.getString("schedule_id");
					if (getTrainingSchedulePeriod() != null && getTrainingSchedulePeriod().equals("2")) {
						weekdayValue = rst.getString("training_weekday");
						dayValue = "";
					} else if (getTrainingSchedulePeriod() != null && getTrainingSchedulePeriod().equals("3")) {
						dayValue = rst.getString("training_day");
					}
					setDurationType(rst.getString("training_duration_type"));
					
					if(rst.getString("day_schedule_type") != null && !rst.getString("day_schedule_type").equals("")) {
						setScheduleTypeValue(rst.getString("day_schedule_type"));
					} else {
						setScheduleTypeValue("1");
					}
					
//					setScheduleTypeValue(rst.getString("day_schedule_type") != null ? rst.getString("day_schedule_type") : "1");
					
					List<String> weekdaysValue1 = new ArrayList<String>();
					if (rst.getString("week_days") == null) {
						weekdaysValue1.add("");
					} else {
						String appMonths = ","+rst.getString("week_days")+",";
						weekdaysValue1 = Arrays.asList(appMonths.split(","));
					}				
					if (weekdaysValue1 != null) {
						for (int i = 1; i < weekdaysValue1.size(); i++) {
							weekdaysValue.add(weekdaysValue1.get(i).trim());
						}
					} else {
						weekdaysValue.add("");
					}
				}
				rst.close();
				pst.close();
				
//				System.out.println("weekdaysValue==>"+getWeekdaysValue());
				/*if(getScheduleTypeValue() == null && !getScheduleTypeValue().equals("")) {
					setScheduleTypeValue("1");
				}*/
//				System.out.println("weekdaysValue ===> "+weekdaysValue);
				
				request.setAttribute("trainingSchedulePeriod",getTrainingSchedulePeriod());

				pst=con.prepareStatement("select frequency,frequency_date,training_frequency,start_time,end_time,schedule_type,week_days" +
						" from training_schedule join training_session using (schedule_id) where plan_id=?");
				pst.setInt(1, uF.parseToInt(getID()));
				rst=pst.executeQuery();
				
				while(rst.next()){
					List<String> alInner= new ArrayList<String>();
					alInner.add(rst.getString("training_frequency"));
					alInner.add(uF.getDateFormat(rst.getString("frequency_date"),DBDATE,DATE_FORMAT));
					
					String startTime="";
					if(rst.getString("start_time")!=null && !rst.getString("start_time").equals("")){
						startTime=rst.getString("start_time").substring(0,5);
					}					
					alInner.add(startTime);
					
					String endTime="";
					if(rst.getString("end_time")!=null && !rst.getString("end_time").equals("")){
						endTime=rst.getString("end_time").substring(0, 5);
					}
					alInner.add(endTime); 
					alInner.add(rst.getString("schedule_type"));
					alInner.add(rst.getString("week_days"));
					
				 alSessionData.add(alInner);
				}
				rst.close();
				pst.close();
				
				request.setAttribute("alSessionData", alSessionData);
				
			} else if (uF.parseToInt(step1) == 3) {

//				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

				pst = con.prepareStatement("select schedule_id,start_date,end_date,day_schedule_type,training_frequency,training_day,training_weekday from training_schedule where plan_id=?");
				pst.setInt(1, uF.parseToInt(getPlanId()));
				rst=pst.executeQuery();
				String scheduleId = null;
				String startDate = null;
				String endDate = null;
				String scheduleType = null;
				String trainingFrequency = null;
				String trainingDay = null;
				String trainingWeekDay = null;
				while(rst.next()){	
					scheduleId = rst.getString("schedule_id");
					startDate = rst.getString("start_date");
					endDate = rst.getString("end_date");
					scheduleType = rst.getString("day_schedule_type");
					trainingFrequency = rst.getString("training_frequency");
					trainingDay = rst.getString("training_day");
					trainingWeekDay = rst.getString("training_weekday");
					 
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmDayDescription = new HashMap<String, String>();
				pst = con.prepareStatement("select training_schedule_id,day_date,day_description,long_description from training_schedule_details where training_id=? and training_schedule_id = ?");
				pst.setInt(1, uF.parseToInt(getPlanId()));
				pst.setInt(2, uF.parseToInt(scheduleId));
				rst=pst.executeQuery();
				while(rst.next()){	
					String dayDate = uF.getDateFormat(rst.getString("day_date"), DBDATE, DATE_FORMAT);
					hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate+"_S", rst.getString("day_description"));
					hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate+"_L", rst.getString("long_description"));
				}
				rst.close();
				pst.close();
				
				//System.out.println("scheduleType ===> " + scheduleType);
				String wkDays = null;
				List<String> frequencyDateList = new ArrayList<String>();
				if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
					pst = con.prepareStatement("select week_days from training_session where schedule_id = ? limit 1");
					pst.setInt(1, uF.parseToInt(scheduleId));
					rst=pst.executeQuery();
					while(rst.next()){	
						wkDays = rst.getString("week_days");
					}
					rst.close();
					pst.close();
					
				} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {
					pst = con.prepareStatement("select frequency_date from training_session where schedule_id = ?");
					pst.setInt(1, uF.parseToInt(scheduleId));
					rst=pst.executeQuery();
					while(rst.next()){	
						frequencyDateList.add(rst.getString("frequency_date"));
					}
					rst.close();
					pst.close();
				}
				
				
				String dayCount = uF.dateDifference(startDate, DBDATE, endDate, DBDATE);
				
				//System.out.println("dayCount ===> " + dayCount);
				
				List<String> daysList = new ArrayList<String>();
				Map<String, String> hmdaysDate = new HashMap<String, String>();
				
				if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
					if(trainingFrequency!=null && trainingFrequency.equals("1")){
						List<String> wkDayList = new ArrayList<String>();
						if(wkDays != null && !wkDays.equals("")) {
							wkDayList = Arrays.asList(getAppendData(wkDays).split(","));
						}
						
						String strNewDate = null;
						
						java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
						String strDay = new SimpleDateFormat("E").format(dtDate);
				        if(wkDayList != null && wkDayList.contains(strDay)){
				        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
							daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
				        } else if(wkDayList == null) {
				        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
							daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
				        }
						for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
							if(strNewDate == null)
							strNewDate = startDate;
							Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
							java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
							String strDay1 = new SimpleDateFormat("E").format(dtDate1);
							 if(wkDayList != null && wkDayList.contains(strDay1)){
								 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
									daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
						        }else if(wkDayList == null) {
						        	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
									daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));
						        }
							strNewDate = newDate+"";
						}
					} else if(trainingFrequency!=null && trainingFrequency.equals("2")){
						List<String> wkDayList = new ArrayList<String>();
						wkDayList.add(trainingWeekDay);
						String strNewDate = null;
						
						java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
						String strDay = new SimpleDateFormat("EEEE").format(dtDate);
				        if(wkDayList != null && wkDayList.contains(strDay)){
				        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
							daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
				        }
						for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
							if(strNewDate == null)
							strNewDate = startDate;
							Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
							java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
							String strDay1 = new SimpleDateFormat("EEEE").format(dtDate1);
							 if(wkDayList != null && wkDayList.contains(strDay1)){
								 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
									daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
						     }
							strNewDate = newDate+"";
						}
					} else if(trainingFrequency!=null && trainingFrequency.equals("3")){
						if(trainingDay!=null){
							List<String> wkDayList = new ArrayList<String>();
							String strTDay = uF.parseToInt(trainingDay.trim()) < 10 ? "0"+trainingDay.trim() : trainingDay.trim();
							wkDayList.add(strTDay);
							String strNewDate = null;
							
							java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
							String strDay = new SimpleDateFormat("dd").format(dtDate);
					        if(wkDayList != null && wkDayList.contains(strDay)){
					        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
								daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
					        }
							for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
								if(strNewDate == null)
								strNewDate = startDate;
								Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
								java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
								String strDay1 = new SimpleDateFormat("dd").format(dtDate1);
								 if(wkDayList != null && wkDayList.contains(strDay1)){
									 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
										daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
							     }
								strNewDate = newDate+"";
							}
						}
					}
				} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {

					for(int i=0; frequencyDateList != null && !frequencyDateList.isEmpty() && i< frequencyDateList.size(); i++) {
						hmdaysDate.put(uF.getDateFormat(frequencyDateList.get(i), DBDATE, DATE_FORMAT), uF.getDateFormat(frequencyDateList.get(i), DBDATE, CF.getStrReportDateFormat()));
						daysList.add(uF.getDateFormat(frequencyDateList.get(i), DBDATE, DATE_FORMAT));
					}
				}
				
//				System.out.println("daysList ====> " + daysList);
				Collections.sort(daysList);
//				System.out.println("daysList after ====> " + daysList);
				request.setAttribute("daysList", daysList);
				request.setAttribute("hmdaysDate", hmdaysDate);
				request.setAttribute("scheduleId", scheduleId);
				request.setAttribute("hmDayDescription", hmDayDescription);
				
			}  else if (uF.parseToInt(step1) == 4) {

				pst = con.prepareStatement("select * from training_question_details tqd,training_question_bank tq where tqd.plan_id=? " +
						" and tqd.question_id=tq.training_question_bank_id and tqd.question_for = 2");
				pst.setInt(1, uF.parseToInt(strID));
				rst = pst.executeQuery();
				List<List<String>> learnerFeedbackQueList = new ArrayList<List<String>>();
				while (rst.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rst.getString("training_question_id"));
					innerList.add(rst.getString("question_id"));
					innerList.add(rst.getString("question_text"));
					innerList.add(rst.getString("question_type"));
					innerList.add(rst.getString("plan_id"));
					innerList.add(rst.getString("question_for"));
					
					learnerFeedbackQueList.add(innerList);
				}
				rst.close();
				pst.close();
				
				request.setAttribute("learnerFeedbackQueList", learnerFeedbackQueList);
				
				
				pst = con.prepareStatement("select * from training_question_details tqd,training_question_bank tq where tqd.plan_id=? " +
						" and tqd.question_id=tq.training_question_bank_id and tqd.question_for = 1");
				pst.setInt(1, uF.parseToInt(strID));
				rst = pst.executeQuery();
				List<List<String>> trainerFeedbackQueList = new ArrayList<List<String>>();
				while (rst.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rst.getString("training_question_id"));
					innerList.add(rst.getString("question_id"));
					innerList.add(rst.getString("question_text"));
					innerList.add(rst.getString("question_type"));
					innerList.add(rst.getString("plan_id"));
					innerList.add(rst.getString("question_for"));
					
					trainerFeedbackQueList.add(innerList);
				}
				rst.close();
				pst.close();
				
				request.setAttribute("trainerFeedbackQueList", trainerFeedbackQueList);
				
//				System.out.println("learnerFeedbackQueList ===> " + learnerFeedbackQueList);
//				System.out.println("trainerFeedbackQueList ===> " + trainerFeedbackQueList);
			}
			
			if (strID != null)
				setPlanId(strID);

			request.setAttribute("plan_Id", getPlanId());

		} catch (Exception e) {

			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void insertData() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			if (uF.parseToInt(step) == 1) {
	
				setPlanId(insertStep1Data(con));
				request.setAttribute("plan_Id", getPlanId());
	//			request.setAttribute("TRAININGTYPE", getTrainingType());
	
			} else if (uF.parseToInt(step) == 2) {
				request.setAttribute("plan_Id", getPlanId());
				insertStep2Data(con);
	
			} else if (uF.parseToInt(step) == 3) {
	
				request.setAttribute("plan_Id", getPlanId());
				insertStep3Data(con);
	//			request.setAttribute("TRAININGTYPE", getTrainingType());
	
			} else if (uF.parseToInt(step) == 4) {
				request.setAttribute("plan_Id", getPlanId());
				insertStep4Data(con);
			} else if(uF.parseToInt(step) == 0){
				//String attriID=request.getParameter("attriID");
				attributeID.add(getAttriID());
				setAlignedwith("3");
			} else if(uF.parseToInt(step) == 5){
				request.setAttribute("plan_Id", getPlanId());
				insertStep6Data(con);
			}
			
			String trainingName = getTrainingPlanNameById(con, uF, getPlanId());
			session.setAttribute(MESSAGE, SUCCESSM+""+trainingName+" training plan has been created successfully."+END);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}

	}

	private void insertStep6Data(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
				//String question_for = request.getParameter("question_for");
				
				String question[] = request.getParameterValues("question");
				
				String addFlag[] = request.getParameterValues("status");
				String optiona[] = request.getParameterValues("optiona");
				String optionb[] = request.getParameterValues("optionb");
				String optionc[] = request.getParameterValues("optionc");
				String optiond[] = request.getParameterValues("optiond");
				String ansType[] = request.getParameterValues("ansType");
				String orientt[] = request.getParameterValues("orientt");
				
				for (int i = 0; question != null && i < question.length; i++) {
//					System.out.println("orientt[i] ===> " + orientt[i]);
					
					String question_for = request.getParameter("question_for"+orientt[i]);
//					System.out.println("question_for ===> " + question_for);
					int question_id = 0;
						String[] correct = request.getParameterValues("correct"+ orientt[i]);
						StringBuilder option = new StringBuilder();

						for (int ab = 0; correct != null && ab < correct.length; ab++) {
							option.append(correct[ab] + ",");
						}

						pst = con.prepareStatement("insert into training_question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)" +
								"values(?,?,?,?, ?,?,?,?)");
						pst.setString(1, question[i]);
						pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
						pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
						pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
						pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
						pst.setString(6, option.toString());
						pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
						pst.setInt(8, uF.parseToInt(ansType[i]));
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("select max(training_question_bank_id) from training_question_bank");
						rst = pst.executeQuery();
		
						while (rst.next()) {
							question_id = rst.getInt(1);
						}
						rst.close();
						pst.close();
						
						pst = con.prepareStatement("insert into training_question_details(question_id,plan_id,question_for,weightage)" +
										"values(?,?,?,?)");
						pst.setInt(1, question_id);
						pst.setInt(2, uF.parseToInt(getPlanId()));
						pst.setInt(3, uF.parseToInt(question_for));	
						pst.setDouble(4, uF.parseToDouble("100"));
						pst.execute();
						pst.close();
				}
				
			
//			String questionSelect = request.getParameter("questionSelect");
//			String weightage = request.getParameter("weightage");
//			String question = request.getParameter("question");
//			String question_for = request.getParameter("question_for");
//
//			String addFlag = request.getParameter("status");
//			String optiona = request.getParameter("optiona");
//			String optionb = request.getParameter("optionb");
//			String optionc = request.getParameter("optionc");
//			String optiond = request.getParameter("optiond");		
//			
//			
//			if (questionSelect.length() > 0) {
//				int question_id =uF.parseToInt(questionSelect);
//				if (uF.parseToInt(questionSelect) == 0) {
//
//					String[] correct = request.getParameterValues("correct0");
//					String ansType = request.getParameter("ansType0");
//					StringBuilder option = new StringBuilder();
//
//					for (int ab = 0; correct != null
//							&& ab < correct.length; ab++) {
//						option.append(correct[ab] + ",");
//					}
//
//					pst = con
//							.prepareStatement("insert into training_question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
//					pst.setString(1, question);
//					pst.setString(2, optiona);
//					pst.setString(3, optionb);
//					pst.setString(4, optionc);
//					pst.setString(5, optiond);
//					pst.setString(6, option.toString());
//					pst.setBoolean(7, uF.parseToBoolean(addFlag));
//					pst.setInt(8, uF.parseToInt(ansType));
//					pst.execute();
//
//					pst = con
//							.prepareStatement("select max(training_question_bank_id) from training_question_bank");
//					rst = pst.executeQuery();
//
//					while (rst.next()) {
//						question_id = rst.getInt(1);
//					}
//
//				}  
//
//				pst = con
//				.prepareStatement("insert into training_question_details(question_id,plan_id,question_for,weightage)" +
//								"values(?,?,?,?)");
//				pst.setInt(1, question_id);
//				pst.setInt(2, uF.parseToInt(getPlanId()));
//				pst.setInt(3, uF.parseToInt(question_for));	
//				pst.setDouble(4, uF.parseToDouble(weightage));
//				pst.execute();
//			}
//
//			

			

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}



	private void insertStep5Data(Connection con) {

		// For sending Notifications *****************
		
		/*Thread thread=new Thread(this);
		thread.start();*/
		

		

	}

	private void insertStep4Data(Connection con) {

		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
//			String learnersID = request.getParameter("empselected");
			//System.out.println("getPlanId() ===> " + getPlanId());
			
			String dayDate[] = request.getParameterValues("dayDate");
			String daydescription[] = request.getParameterValues("daydescription");
			String longdescription[] = request.getParameterValues("longdescription");
			//System.out.println("dayDate length =====> " +dayDate.length);
			String scheduleId = request.getParameter("scheduleId");
			
			pst = con.prepareStatement("delete from training_schedule_details where training_id = ?");
			pst.setInt(1, uF.parseToInt(getPlanId()));
			pst.execute();
			pst.close();
			
			for(int i=0; dayDate != null && i < dayDate.length; i++) {
				pst = con.prepareStatement("insert into training_schedule_details(day_date,day_description,training_id,added_by,entry_date," +
						"training_schedule_id,long_description)values(?,?,?,? ,?,?,?)");
				pst.setDate(1, uF.getDateFormat(dayDate[i], DATE_FORMAT));
				pst.setString(2, daydescription[i]);
				pst.setInt(3, uF.parseToInt(getPlanId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(scheduleId));
				pst.setString(7, longdescription[i]);
				pst.execute();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void insertStep3Data(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("ScheduleType ===> "+getScheduleType());
//		System.out.println("WeekDays ===> "+getWeekDays());
		try {
			
			String training_day=null;
			String weeklyDay1=null;
			if(trainingSchedulePeriod!=null && trainingSchedulePeriod.equals("2")){ 
				weeklyDay1=weekday;
				training_day=null;
			}else if(trainingSchedulePeriod!=null && trainingSchedulePeriod.equals("3")){
				weeklyDay1=null;
				training_day=day;
			}

			pst = con.prepareStatement("delete from training_session where "
							+ "schedule_id=(select schedule_id from training_schedule where plan_id=?) ");
			pst.setInt(1, uF.parseToInt(getID()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("update training_schedule set start_date=?,end_date=?,training_duration=?,"
							+ "training_frequency=?,training_day=?,training_weekday=?,training_duration_type=?," 
							+ "day_schedule_type=? where plan_id=?");
			pst.setDate(1,uF.getDateFormat(getStartdateTraining(), DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(getEnddateTraining(), DATE_FORMAT));
			pst.setDouble(3, uF.parseToDouble("0"));
			pst.setString(4, getTrainingSchedulePeriod());
			pst.setString(5, training_day);
//			pst.setString(6, appraisal_month);
			pst.setString(6, weeklyDay1);
			pst.setString(7, getDurationType());
			pst.setInt(8, uF.parseToInt(getScheduleType()) == 0 ? 1 : uF.parseToInt(getScheduleType()));
			pst.setInt(9, uF.parseToInt(getPlanId()));
			pst.execute();
			pst.close();
			
			// adding sessions ............

			String schedule_id = null;
			pst = con.prepareStatement("select schedule_id from training_schedule where plan_id=?");
			pst.setInt(1, uF.parseToInt(getPlanId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				schedule_id = rst.getString("schedule_id");
			}
			rst.close();
			pst.close();
			
			String[] oneTime = request.getParameterValues("oneTimeDate");
			String[] startTime = request.getParameterValues("startTime");
			String[] endTime = request.getParameterValues("endTime");

			for (int i = 0; i < startTime.length; i++) {
				

				pst = con.prepareStatement("insert into training_session(schedule_id,frequency_date,frequency_day,"
								+ "start_time,end_time,frequency,session_training_day,schedule_type,week_days)" 
								+ "values(?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(schedule_id));
				if (getTrainingSchedulePeriod().equalsIgnoreCase("1")){
					if (oneTime.length == startTime.length){
						pst.setDate(2, uF.getDateFormat(oneTime[i], DATE_FORMAT));
					}else{
						pst.setDate(2, uF.getDateFormat("", DATE_FORMAT));
					}
				}else if (getTrainingSchedulePeriod().equalsIgnoreCase("2")){
					pst.setDate(2, uF.getDateFormat("", DATE_FORMAT));
				}else if (getTrainingSchedulePeriod().equalsIgnoreCase("3")){
					pst.setDate(2, uF.getDateFormat("", DATE_FORMAT));
				}
				if (getTrainingSchedulePeriod().equalsIgnoreCase("1")){
					pst.setString(3, "");
				}else if (getTrainingSchedulePeriod().equalsIgnoreCase("3")){
					pst.setString(3, "");
				}else if (getTrainingSchedulePeriod().equalsIgnoreCase("2")){
					pst.setString(3, getWeekday());
				}
				pst.setTime(4, uF.getTimeFormat(startTime[i], DBTIME));
				pst.setTime(5, uF.getTimeFormat(endTime[i], DBTIME));
				pst.setString(6, getTrainingSchedulePeriod());
				pst.setString(7, training_day);
				pst.setInt(8, uF.parseToInt(getScheduleType()));
				if (getTrainingSchedulePeriod().equalsIgnoreCase("1")){
					pst.setString(9, getWeekDays());
				}else if (getTrainingSchedulePeriod().equalsIgnoreCase("3")){
					pst.setString(9, "");
				}else if (getTrainingSchedulePeriod().equalsIgnoreCase("2")){
					pst.setString(9, "");
				}
				pst.execute();
				pst.close();
				
			}
			
			pst = con.prepareStatement("delete from training_schedule_details where training_id=? and training_schedule_id=? ");
			pst.setInt(1, uF.parseToInt(getID()));
			pst.setInt(2, uF.parseToInt(schedule_id));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
//	private void insertStep3Data(Connection con) {
//
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//
//		try {
//			
//			String training_day=null;
//			String weeklyDay1=null;
//			if(trainingSchedulePeriod!=null && trainingSchedulePeriod.equals("2")){ 
//				weeklyDay1=weekday;
//				training_day=null;
//			}else if(trainingSchedulePeriod!=null && trainingSchedulePeriod.equals("3")){
//				weeklyDay1=null;
//				training_day=day;
//			}
//			
//			pst = con.prepareStatement("update training_schedule set start_date=?,end_date=?,training_duration=?,"
//							+ "training_frequency=?,training_day=?,training_weekday=? where plan_id=?");
//
//			pst.setDate(1,uF.getDateFormat(getStartdateTraining(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getEnddateTraining(), DATE_FORMAT));
//			pst.setDouble(3, uF.parseToDouble(getNoofdaysTraining()));
//			pst.setString(4, getTrainingSchedulePeriod());
//			pst.setString(5, training_day);
////			pst.setString(6, appraisal_month);
//			pst.setString(6, weeklyDay1);
//			pst.setInt(7, uF.parseToInt(getPlanId()));
//
//
//			pst.execute();
//
//			// adding sessions ............
//
//			String schedule_id = null;
//			pst = con.prepareStatement("select max(schedule_id) as schedule_id from training_schedule ");
//			rst = pst.executeQuery();
//			while (rst.next()) {
//				schedule_id = rst.getString("schedule_id");
//			}
//
//			String[] oneTime = request.getParameterValues("oneTimeDate");
//			String[] startTime = request.getParameterValues("startTime");
//			String[] endTime = request.getParameterValues("endTime");
//
//			for (int i = 0; i < startTime.length; i++) {
//				pst = con.prepareStatement("insert into training_session (schedule_id,frequency_date,frequency_day,"
//								+ "start_time,end_time,frequency,session_training_day)values(?,?,?,?,?,?,?)");
//				pst.setInt(1, uF.parseToInt(schedule_id));
//				
//				if (getTrainingSchedulePeriod().equalsIgnoreCase("1"))
//					pst.setDate(2, uF.getDateFormat(oneTime[i], DATE_FORMAT));
//				else if (getTrainingSchedulePeriod().equalsIgnoreCase("3"))
//					pst.setDate(2, uF.getDateFormat(oneTime[i], DATE_FORMAT));
//				else if (getTrainingSchedulePeriod().equalsIgnoreCase("2"))
//					pst.setDate(2, uF.getDateFormat("", DATE_FORMAT));
//
//				if (getTrainingSchedulePeriod().equalsIgnoreCase("1"))
//					pst.setString(3, "");
//				else if (getTrainingSchedulePeriod().equalsIgnoreCase("3"))
//					pst.setString(3, "");
//				else if (getTrainingSchedulePeriod().equalsIgnoreCase("2"))
//					pst.setString(3, getWeekday());
//				
////				if (getTrainingSchedulePeriod().equalsIgnoreCase("OT"))
////					pst.setDate(2, uF.getDateFormat(oneTime[i], DATE_FORMAT));
////				else
////					pst.setDate(2, uF.getDateFormat(getMonthDay(), DATE_FORMAT));
////				pst.setString(3, getWeekDay());
//				
//				pst.setTime(4, uF.getTimeFormat(startTime[i]));
//				pst.setTime(5, uF.getTimeFormat(endTime[i]));
//				pst.setString(6, getTrainingSchedulePeriod());
//				pst.setString(7, training_day);
////				pst.setString(8, appraisal_month);
//
//				pst.execute();
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	private void insertStep2Data(Connection con) {

		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			pst = con.prepareStatement("update training_schedule set trainer_ids=?, wlocation_id=? where plan_id=?");
			pst.setString(1, getTrnselected());
			pst.setInt(2, uF.parseToInt(getTrainingLocation()));
			pst.setInt(3, uF.parseToInt(getPlanId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private String insertStep1Data(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		String plan_idNew = null;
		String trainingType = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			pst = con.prepareStatement("insert into training_plan (training_title,training_objective,training_summary,training_type," +
					"is_certificate,attribute_id,level_id,certificate_id,alignedwith,location_id,org_id,added_by,entry_date" +
					")values(?,?,?,? ,?,?,?,? ,?,?,?,? ,?)"); //,marks_grade_type,marks_grade_standard
			pst.setString(1, getTrainingTitle());
			pst.setString(2, getTrainingObjective());
			pst.setString(3, getTrainingSummary());
			pst.setString(4, "1");
			pst.setBoolean(5, getStrCertificate());
			pst.setString(6, getStrAttribute());
			pst.setInt(7, uF.parseToInt(getStrLevel()));
			pst.setInt(8, uF.parseToInt(getStrCertificateId()));
			/*if (getStrCertificate() == true){
				String[] temp=getStrCertificateId().split("::::");
				pst.setInt(8, uF.parseToInt(temp[0]));
			}else{
				pst.setInt(8, uF.parseToInt("-1"));
			}*/
			pst.setInt(9, uF.parseToInt(getAlignedwith()));
			pst.setString(10, getPlan_idwlocation());
			pst.setString(11, getPlan_organization());
			pst.setInt(12, uF.parseToInt(strSessionEmpId));
			pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			pst = con.prepareStatement(" select max(plan_id) as plan_id from training_plan ");
			rst = pst.executeQuery();
			while (rst.next()) {
				plan_idNew = rst.getString("plan_id");
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into training_schedule (plan_id) values(?)");
			pst.setInt(1, uF.parseToInt(plan_idNew));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plan_idNew;
	}

	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

	/*
	 * private String mode;
	 * 
	 * public String getMode() { return mode; } public void setMode(String mode)
	 * { this.mode = mode; }
	 */

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	private String step;

	// form parameters **********

	public String getStrEmployee() {
		return strEmployee;
	}

	public void setStrEmployee(String strEmployee) {
		this.strEmployee = strEmployee;
	}

	String strEmployee;

	List<FillAttribute> attributeList;

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	List<FillDesig> desigList;

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	List<FillTrainer> trainerList;

	/*
	 * List<FillCertificate> certificatelist;
	 * 
	 * public List<FillCertificate> getCertificatelist() { return
	 * certificatelist; } public void setCertificatelist(List<FillCertificate>
	 * certificatelist) { this.certificatelist = certificatelist; }
	 */

	// first step variables **********

	public List<FillTrainer> getTrainerList() {
		return trainerList;
	}

	public void setTrainerList(List<FillTrainer> trainerList) {
		this.trainerList = trainerList;
	}

	String trainingTitle;
	String trainingObjective;
	String trainingSummary;
	boolean strCertificate;
	String strLevel;
	String empselected;
	String trnselected;
//	String marksGradeType;
//	String marksGradeStandard;
	
	public String getEmpselected() {
		return empselected;
	}

	public void setEmpselected(String empselected) {
		this.empselected = empselected;
	}

	public String getTrnselected() {
		return trnselected;
	}

	public void setTrnselected(String trnselected) {
		this.trnselected = trnselected;
	}

	public String getTrainingTitle() {
		return trainingTitle;
	}

	public void setTrainingTitle(String trainingTitle) {
		this.trainingTitle = trainingTitle;
	}

	public String getTrainingObjective() {
		return trainingObjective;
	}

	public void setTrainingObjective(String trainingObjective) {
		this.trainingObjective = trainingObjective;
	}

	public String getTrainingSummary() {
		return trainingSummary;
	}

	public void setTrainingSummary(String trainingSummary) {
		this.trainingSummary = trainingSummary;
	}

	public boolean getStrCertificate() {
		return strCertificate;
	}

	public void setStrCertificate(boolean b) {
		this.strCertificate = b;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	String strAttribute;

	public String getStrAttribute() {
		return strAttribute;
	}

	public void setStrAttribute(String strAttribute) {
		this.strAttribute = strAttribute;
	}

	String strGrade;

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	// fields for 2nd screen trainer info **************


	
	// String selectedTrainer;
//	String noofdaysTraining;
	String trainingSchedulePeriod;
	String startdateTraining;
	String durationType;
	String weekDays;
	String scheduleType;
	
	public String getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(String weekDays) {
		this.weekDays = weekDays;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getDurationType() {
		return durationType;
	}

	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}

	public String getTrainingSchedulePeriod() {
		return trainingSchedulePeriod;
	}

	public void setTrainingSchedulePeriod(String trainingSchedulePeriod) {
		this.trainingSchedulePeriod = trainingSchedulePeriod;
	}

	public String getStartdateTraining() {
		return startdateTraining;
	}

	public void setStartdateTraining(String startdateTraining) {
		this.startdateTraining = startdateTraining;
	}

	public String getEnddateTraining() {
		return enddateTraining;
	}

	public void setEnddateTraining(String enddateTraining) {
		this.enddateTraining = enddateTraining;
	}

	String enddateTraining;

	String planId;

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	// Fields for 3rd Screen ****************

	// String selectedTrainer;
	String trainingLocation;

	public String getTrainingLocation() {
		return trainingLocation;
	}

	public void setTrainingLocation(String trainingLocation) {
		this.trainingLocation = trainingLocation;
	}

	// fields for 4rth Screen *********

	List<FillLevel> levelList;

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	List<FillCertificate> certificateList;

	public List<FillCertificate> getCertificateList() {
		return certificateList;
	}

	public void setCertificateList(List<FillCertificate> certificateList) {
		this.certificateList = certificateList;
	}

	List<FillGrade> gradeList;
	List<FillEmployee> empList;

	List<FillWLocation> locationList;

	public List<FillWLocation> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<FillWLocation> locationList) {
		this.locationList = locationList;
	}

	String strlearnerLevel;
	String strlearnerGrade;
	String strlearnerEmployee;
	String strstrlearnerDesignation;
	String strCertificateId;

	public String getStrCertificateId() {
		return strCertificateId;
	}

	public void setStrCertificateId(String strCertificateId) {
		this.strCertificateId = strCertificateId;
	}

	String stepSubmit;
	String stepSave;
	String stepSaveGoBack;


	public String getStepSaveGoBack() {
		return stepSaveGoBack;
	}

	public void setStepSaveGoBack(String stepSaveGoBack) {
		this.stepSaveGoBack = stepSaveGoBack;
	}

	public String getStepSave() {
		return stepSave;
	}

	public void setStepSave(String stepSave) {
		this.stepSave = stepSave;
	}

	public String getStepSubmit() {
		return stepSubmit;
	}

	public void setStepSubmit(String stepSubmit) {
		this.stepSubmit = stepSubmit;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	@Override
	public void run() {

		// trainee notification ******************
		
/*       List<String> candidateList=getcandidateIds();

       for(int i=0;i<candidateList.size();i++){
    	   
          if(uF.parseToInt(candidateList.get(i))!=0){
		 Notifications nF = new Notifications(904, CF);
		 nF.setStrEmpId(candidateList.get(i));
		 
		 nF.setStrHostAddress(CF.getStrEmailLocalHost());
		 nF.setStrContextPath(request.getContextPath());

		 
		 nF.setStrRecruitmentDesignation(getDesignation_name());
		 nF.setStrRecruitmentGrade(getGrade_name());
		 nF.setStrRecruitmentLevel(getLevel_name());
		 nF.setStrRecruitmentPosition(getPositions());
		 nF.setStrRecruitmentWLocation(getLocation_name());
		 nF.setStrRecruitmentProfile(getServices());
		 nF.setStrRecruitmentSkill(getSkills_name());
		 
		 nF.sendNotifications();
       }
       }*/
		
		
		// Trainer Notification ***********

		UtilityFunctions uF = new UtilityFunctions();
		
		 List<String> trainerList=getTrainerIds();
		 
		    for(int i=0;i<trainerList.size();i++){
		 
		    	
		    	if(uF.parseToInt(trainerList.get(i))!=0){
		 
		    		NotificationContent nF = new NotificationContent(904, CF);
		    		nF.request = request;
//		    		getTrainerNotification(nF);
		    		getjoiningNotification(nF);
		    		getrecruitmentNotification(nF);
		    		
		    	nF.setStrHostAddress(CF.getStrEmailLocalHost()); 
		   		nF.setStrContextPath(request.getContextPath());
		     		
		    		nF.setStrEmpId(trainerList.get(i));	
	 
		    		nF.sendNotifications();
		 
		    	}
		    	}
       
	}

	private void getrecruitmentNotification(NotificationContent nF) {


		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		
		try {
			con=db.makeConnection(con);
		
			//query changes yet to be made ********** candidate id 
			
		  pst=con.prepareStatement("select interview_date,cip.job_code,job_description,rd.added_by,no_position,skills,grade_name from " +
		  		"candidate_interview_panel cip join recruitment_details rd using (recruitment_id) join candidate_personal_details cpd " +
		  		"on candidate_id=emp_per_id join grades_details using (grade_id)");
			
		  rst=pst.executeQuery();
		  while(rst.next()){
			  nF.setStrRecruitmentGrade(rst.getString("grade_name"));
			  nF.setStrRecruitmentPosition((rst.getString("no_position")));
			  nF.setJobDescription((rst.getString("job_description")));
			  nF.setInterviewSchedule("Interview schedule hardcoded===");
			  nF.setReportingToManager((rst.getString("added_by")));
			  nF.setStrRecruitmentSkill((rst.getString("skills")));
		  }
		  rst.close();
		  pst.close();
			
		}catch(Exception e){
		
			e.printStackTrace();
		}
		finally{
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getjoiningNotification(NotificationContent nF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		
		try {
			con=db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			//query changes yet to be made ********** candidate id 
			
		  pst=con.prepareStatement("select designation_id,rd.job_code,wlocation,emp_fname, emp_mname,emp_lname,emp_email,ctc_offered, candidate_joining_date," +
		  		"designation_name,wlocation_name from recruitment_details rd join candidate_personal_details using (recruitment_id) " +
		  		"join designation_details using (designation_id) join  work_location_info on (wlocation_id=wlocation) ");
			
		  rst=pst.executeQuery();
		  while(rst.next()){
			  
			  nF.setStrRecruitmentDesignation(rst.getString("designation_name"));
			  nF.setStrRecruitmentWLocation((rst.getString("wlocation_name")));
			  nF.setJobCode((rst.getString("job_code")));
			  
			  String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
			
			  
			  nF.setCandidateName((rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname")));
			  nF.setCtcOffered((rst.getString("ctc_offered")));
			  nF.setJoiningDate((rst.getString("candidate_joining_date")));
		  }
		  rst.close();
		  pst.close();
			
		}catch(Exception e){
		
			e.printStackTrace();
		} finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}

	/*private void getTrainerNotification(NotificationContent nF) {

		Database db = new Database();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		
		try {
			con=db.makeConnection(con);
			
			pst=con.prepareStatement("select plan_id,start_date,end_date,training_frequency,training_title," +
					"wlocation_name,attribute_id,wlocation_id,attribute_name,certificate_name,training_frequency" +
					" from training_schedule join training_plan using (plan_id) " +
					"join appraisal_attribute aa on (attribute_id=aa.arribute_id)  " +
					"join  work_location_info using (wlocation_id) " +
					"join training_certificate using (certificate_id)  order by plan_id desc LIMIT 1");
			rst=pst.executeQuery();
			while(rst.next()){
				
				nF.setTraining_attribute(rst.getString("attribute_name"));
				nF.setTraining_start_date(rst.getString("start_date"));
				nF.setTraining_end_date(rst.getString("end_date"));
				nF.setTraining_title(rst.getString("training_title"));
				nF.setTraining_certificate_name(rst.getString("certificate_name"));
				
				// hard coded value ---------
				nF.setTraining_trainer_name("trainer Name Hardcoded");
				
				if(rst.getString("training_frequency").equalsIgnoreCase("M"))
				nF.setTraining_schedule_period("Monthly");
				else if(rst.getString("training_frequency").equalsIgnoreCase("W"))
					nF.setTraining_schedule_period("Weekly");
				else
					nF.setTraining_schedule_period("One Time");
				
				nF.setTraining_location(rst.getString("wlocation_name"));
			}
			
	
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rst);
		}
		
	}*/

	private List<String> getTrainerIds() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		List<String> empID=new ArrayList<String>();
		try {
			con=db.makeConnection(con);
			
			pst=con.prepareStatement("select  schedule_id,trainer_ids from training_schedule" +
					" order by schedule_id desc LIMIT 1");			
			
			rst=pst.executeQuery();
		    if(rst.next()){
		    	empID=Arrays.asList(rst.getString("trainer_ids").split(","));
		    }
		    rst.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return empID;
	}
	

	private List<String> getcandidateIds() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		List<String> empID=new ArrayList<String>();
		try {
			con=db.makeConnection(con);
			
			pst=con.prepareStatement("select  schedule_id,emp_ids from training_schedule " +
					"order by schedule_id desc LIMIT 1");
			
			rst=pst.executeQuery();
		    if(rst.next()){
		    	empID=Arrays.asList(rst.getString("emp_ids").split(","));
		    }
		    rst.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return empID;
	}

	
	
	public String getAppendData(String strIds) {
		
		StringBuilder sb = new StringBuilder();
		if(strIds != null && !strIds.equals("")) {
			
			List<String> idsList = Arrays.asList(strIds.split(","));
			if (idsList != null && !idsList.isEmpty()) {
				
				for (int i = 0; i < idsList.size(); i++) {
					if (i == 0) {
						sb.append("," + idsList.get(i).trim() + ",");
					} else {
						sb.append(idsList.get(i).trim() + ",");
					}
				}
			} else {
				return null;
			}
		}
		return sb.toString();
	}


	public List<String> getAttributeID() {
		return attributeID;
	}

	public void setAttributeID(List<String> attributeID) {
		this.attributeID = attributeID;
	}

	public String getAlignedwith() {
		return alignedwith;
	}

	public void setAlignedwith(String alignedwith) {
		this.alignedwith = alignedwith;
	}

	public String getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(String certificateId) {
		this.certificateId = certificateId;
	}
	
	public List<String> getTrainingLocationValue() {
		return trainingLocationValue;
	}
	
	public void setTrainingLocationValue(List<String> trainingLocationValue) {
		this.trainingLocationValue = trainingLocationValue;
	}
	
	public List<List<String>> getAlSessionData() {
		return alSessionData;
	}
	
	public void setAlSessionData(List<List<String>> alSessionData) {
		this.alSessionData = alSessionData;
	}
	
	public String getPlan_organization() {
		return plan_organization;
	}
	
	public void setPlan_organization(String plan_organization) {
		this.plan_organization = plan_organization;
	}
	
	public String getPlan_idwlocation() {
		return plan_idwlocation;
	}
	
	public void setPlan_idwlocation(String plan_idwlocation) {
		this.plan_idwlocation = plan_idwlocation;
	}
	
	public List<FillWLocation> getWorkList() {
		return workList;
	}
	
	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}
	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}
	
	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	
	public List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public String getAttriID() {
		return attriID;
	}
	public void setAttriID(String attriID) {
		this.attriID = attriID;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public String getQuest_id() {
		return quest_id;
	}

	public void setQuest_id(String quest_id) {
		this.quest_id = quest_id;
	}

	public List<String> getPlan_idwlocationvalue() {
		return plan_idwlocationvalue;
	}

	public void setPlan_idwlocationvalue(List<String> plan_idwlocationvalue) {
		this.plan_idwlocationvalue = plan_idwlocationvalue;
	}

	public String getAnnualDay() {
		return annualDay;
	}

	public void setAnnualDay(String annualDay) {
		this.annualDay = annualDay;
	}

	public String getAnnualMonth() {
		return annualMonth;
	}

	public void setAnnualMonth(String annualMonth) {
		this.annualMonth = annualMonth;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMonthlyDay() {
		return monthlyDay;
	}

	public void setMonthlyDay(String monthlyDay) {
		this.monthlyDay = monthlyDay;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getWeekdayValue() {
		return weekdayValue;
	}

	public void setWeekdayValue(String weekdayValue) {
		this.weekdayValue = weekdayValue;
	}

	public String getDayValue() {
		return dayValue;
	}

	public void setDayValue(String dayValue) {
		this.dayValue = dayValue;
	}

	public String getScheduleTypeValue() {
		return scheduleTypeValue;
	}

	public void setScheduleTypeValue(String scheduleTypeValue) {
		this.scheduleTypeValue = scheduleTypeValue;
	}

	public List<String> getWeekdaysValue() {
		return weekdaysValue;
	}

	public void setWeekdaysValue(List<String> weekdaysValue) {
		this.weekdaysValue = weekdaysValue;
	}

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	public String getFrmpage() {
		return frmpage;
	}

	public void setFrmpage(String frmpage) {
		this.frmpage = frmpage;
	}

	public String getlPlanStep() {
		return lPlanStep;
	}

	public void setlPlanStep(String lPlanStep) {
		this.lPlanStep = lPlanStep;
	}

}