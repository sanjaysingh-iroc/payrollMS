package com.konnect.jpms.training;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.EmpDashboard;
import com.konnect.jpms.tms.GetExceptionReason;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainerMyProfile extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strUserType = null;

	String strSessionEmpId = null;

	CommonFunctions CF;
	private String empId;
	private String proPopup;
	

	private String recruitId; 
	private String trainerId;
	private String fromPage;
	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		viewProfile(getEmpId(),uF);
 		addCalenderEvents(uF);

		if(proPopup != null){
			return "proPopup";
		}

		return SUCCESS;
	}



	private void addCalenderEvents(UtilityFunctions uF) {
	
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		
		List<String> alTrainerCalender = new ArrayList<String>();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select plan_id from training_schedule join training_session using (schedule_id) " +
					"join training_plan using(plan_id) where trainer_ids like '%,"+getTrainerId()+",%' and plan_id > 0");
			rs = pst.executeQuery();
			Set<String> setPlanId = new HashSet<String>();
			while(rs.next()){
				setPlanId.add(rs.getString("plan_id"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = setPlanId.iterator();
			while(it.hasNext()){
				String strTrainPlanId = it.next();
				
				pst = con.prepareStatement("select training_title from training_plan where plan_id=?");
				pst.setInt(1, uF.parseToInt(strTrainPlanId));
				rs=pst.executeQuery();
				String strTrainingPlanName = null;
				while(rs.next()){	
					strTrainingPlanName = rs.getString("training_title");
				}
				rs.close();
				pst.close();
				
				
				pst = con.prepareStatement("select schedule_id,start_date,end_date,day_schedule_type,training_frequency,training_day,training_weekday from training_schedule where plan_id=?");
				pst.setInt(1, uF.parseToInt(strTrainPlanId));
				rs=pst.executeQuery();
				String scheduleId = null;
				String startDate = null;
				String endDate = null;
				String scheduleType = null;
				String trainingFrequency = null;
				String trainingDay = null;
				String trainingWeekDay = null;
				while(rs.next()){	
					scheduleId = rs.getString("schedule_id");
					startDate = rs.getString("start_date");
					endDate = rs.getString("end_date");
					scheduleType = rs.getString("day_schedule_type");
					trainingFrequency = rs.getString("training_frequency");
					trainingDay = rs.getString("training_day");
					trainingWeekDay = rs.getString("training_weekday");
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmDayDescription = new HashMap<String, String>();
				pst = con.prepareStatement("select training_schedule_id,day_date,day_description,long_description from training_schedule_details where training_id=? and training_schedule_id = ?");
				pst.setInt(1, uF.parseToInt(strTrainPlanId));
				pst.setInt(2, uF.parseToInt(scheduleId));
				rs=pst.executeQuery();
				while(rs.next()){	
					String dayDate = uF.getDateFormat(rs.getString("day_date"), DBDATE, DATE_FORMAT);
					hmDayDescription.put(rs.getString("training_schedule_id")+"_"+dayDate+"_S", rs.getString("day_description"));
					hmDayDescription.put(rs.getString("training_schedule_id")+"_"+dayDate+"_L", rs.getString("long_description"));
				}
				rs.close();
				pst.close();
				
				//System.out.println("scheduleType ===> " + scheduleType);
				String wkDays = null;
				List<String> frequencyDateList = new ArrayList<String>();
				if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
					pst = con.prepareStatement("select week_days from training_session where schedule_id = ? limit 1");
					pst.setInt(1, uF.parseToInt(scheduleId));
					rs=pst.executeQuery();
					while(rs.next()){	
						wkDays = rs.getString("week_days");
					}
					rs.close();
					pst.close();
				} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {
					pst = con.prepareStatement("select frequency_date from training_session where schedule_id = ?");
					pst.setInt(1, uF.parseToInt(scheduleId));
					rs=pst.executeQuery();
					while(rs.next()){	
						frequencyDateList.add(rs.getString("frequency_date"));
					}
					rs.close();
					pst.close();
				}
				
				
				String dayCount = uF.dateDifference(startDate, DBDATE, endDate, DBDATE);
				
				//System.out.println("dayCount ===> " + dayCount);
				
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
				        	alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(""+startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+startDate, DBDATE, "dd")+")}");
				        } else if(wkDayList == null) {
				        	alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(""+startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+startDate, DBDATE, "dd")+")}");
				        }
						for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
							if(strNewDate == null)
							strNewDate = startDate;
							Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
							java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
							String strDay1 = new SimpleDateFormat("E").format(dtDate1);
							 if(wkDayList != null && wkDayList.contains(strDay1)){
								 	alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
						        }else if(wkDayList == null) {
						        	alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
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
					        	alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(""+startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+startDate, DBDATE, "dd")+")}");
					        }
							for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
								if(strNewDate == null)
								strNewDate = startDate;
								Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
								java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
								String strDay1 = new SimpleDateFormat("EEEE").format(dtDate1);
								 if(wkDayList != null && wkDayList.contains(strDay1)){
									 alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
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
					        	alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(""+startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+startDate, DBDATE, "dd")+")}");
					        }
							for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
								if(strNewDate == null)
								strNewDate = startDate;
								Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
								java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
								String strDay1 = new SimpleDateFormat("dd").format(dtDate1);
								 if(wkDayList != null && wkDayList.contains(strDay1)){
									 alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
							     }
								strNewDate = newDate+"";
							}
						}
					}
				} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {

					for(int i=0; frequencyDateList != null && !frequencyDateList.isEmpty() && i< frequencyDateList.size(); i++) {
						alTrainerCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+strTrainingPlanName+"("+trainingFrequency+")',start: new Date("+uF.getDateFormat(frequencyDateList.get(i), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(frequencyDateList.get(i), DBDATE, "M"))-1)+", "+uF.getDateFormat(frequencyDateList.get(i), DBDATE, "dd")+")}");
					}
				}
			}
			
			
			
			   
	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		request.setAttribute("alTrainerCalender", alTrainerCalender);
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
	
	public String viewProfile(String strEmpIdReq,UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		List<List<String>> alSkills = new ArrayList<List<String>>();
		List<List<String>> alHobbies;
		List<List<String>> alLanguages;
		List<List<String>> alEducation;
		List<List<Object>> alDocuments;
		List<List<String>> alPrevEmployment;

		try {

			Map<String, String> hm = new HashMap<String, String>();

			con = db.makeConnection(con);
			pst = con.prepareStatement("Select * from trainer_personal_details where trainer_id=?");
			if (strEmpIdReq != null) {
				pst.setInt(1, uF.parseToInt(strEmpIdReq));
			} else {
				pst.setInt(1,uF.parseToInt(strSessionEmpId));
			}
			rs = pst.executeQuery();

			while (rs.next()) {
//				System.out.println("strUserType===>" + strUserType);
				if (strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
//					System.out.println("strUserType===>" + strUserType);
					request.setAttribute(TITLE, rs.getString("trainer_fname") + " " + rs.getString("trainer_lname") + "'s Profile");
				}

			//	hm.put("job_code", rs.getString("job_code"));
				hm.put("NAME",rs.getString("trainer_fname") + " " + rs.getString("trainer_lname"));
				hm.put("ADDRESS",rs.getString("trainer_address1") + " " + rs.getString("trainer_address2"));
				hm.put("CITY", rs.getString("trainer_state_id"));
				hm.put("PINCODE", rs.getString("trainer_pincode"));
				hm.put("CONTACT", rs.getString("trainer_contactno"));
				hm.put("CONTACT_MOB", rs.getString("trainer_contactno_mob"));
				hm.put("IMAGE", rs.getString("trainer_image"));
				hm.put("EMAIL", rs.getString("trainer_email"));

				hm.put("GENDER", rs.getString("trainer_gender"));

				hm.put("DOB", uF.getDateFormat( rs.getString("trainer_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
				hm.put("MARITAL_STATUS", rs.getString("marital_status"));
			//	hm.put("isaccepted", rs.getString("application_status"));
				hm.put("EMPCODE", rs.getString("trainer_code"));

			}
			rs.close();
			pst.close();
			
			request.setAttribute("myProfile", hm);

			int intEmpIdReq = uF.parseToInt(strEmpIdReq);

	//		request.setAttribute("myProfile", hm);

			alSkills = selectSkills(con, intEmpIdReq);
			alHobbies = selectHobbies(con, intEmpIdReq);
			alLanguages = selectLanguages(con, intEmpIdReq);
			alEducation = selectEducation(con, intEmpIdReq);

			String filePath = request.getRealPath("/userDocuments/");
			alDocuments = selectDocuments(con, intEmpIdReq, filePath);

		//	alFamilyMembers = selectFamilyMembers(intEmpIdReq);
			alPrevEmployment = selectPrevEmploment(con, intEmpIdReq);

			/* alActivityDetails = CF.selectEmpActivityDetails(intEmpIdReq, CF); */

			request.setAttribute("alSkills", alSkills);
			request.setAttribute("alHobbies", alHobbies);
			request.setAttribute("alLanguages", alLanguages);
			request.setAttribute("alEducation", alEducation);
			request.setAttribute("alDocuments", alDocuments);
		//	request.setAttribute("alFamilyMembers", alFamilyMembers);

			request.setAttribute("alPrevEmployment", alPrevEmployment);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}



	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	String approvedeny;

	public String getApprovedeny() {
		return approvedeny;
	}

	public void setApprovedeny(String approvedeny) {
		this.approvedeny = approvedeny;
	}
	
	public String getProPopup() {
		return proPopup;
	}

	public void setProPopup(String proPopup) {
		this.proPopup = proPopup;
	}

	public List<List<String>> selectPrevEmploment(Connection con, int empId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alPrevTraining = new ArrayList<List<String>>();

		try {
			Map<String,String> hmwlocation=CF.getWLocationMap(con, null, null);
			pst=con.prepareStatement("select start_date,end_date,wlocation_id,training_title,training_objective from training_schedule " +
					"join training_plan using (plan_id) where trainer_ids LIKE '%,"+getTrainerId()+",%'");
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("start_date"));
				alInner.add(rs.getString("end_date"));
				alInner.add(hmwlocation.get(rs.getString("wlocation_id")));
				alInner.add(rs.getString("training_title"));
				alInner.add(rs.getString("training_objective"));
	
				alPrevTraining.add(alInner);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();

		}
		return alPrevTraining;
	}

	public List<List<Object>> selectDocuments(Connection con, int empId, String filePath) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<Object>> alDocuments = new ArrayList<List<Object>>();

		try {
			pst = con.prepareStatement("SELECT * FROM trainer_documents_details where trainer_id = ?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			while (rs.next()) {
				ArrayList<Object> alInner1 = new ArrayList<Object>();
				alInner1.add(rs.getInt("documents_id") + "");
				alInner1.add(rs.getString("documents_name"));
				alInner1.add(rs.getString("documents_type"));
				alInner1.add(rs.getInt("trainer_id") + "");
				File fileName = new File(rs.getString("documents_file_name"));
				alInner1.add(fileName);
				alDocuments.add(alInner1);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

		return alDocuments;

	}

	public List<List<String>> selectEducation(Connection con, int empId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alEducation = new ArrayList<List<String>>();

		try {
			pst = con.prepareStatement("SELECT * FROM trainer_education_details WHERE trainer_id = ?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("degree_id"));
				alInner.add(CF.getDegreeNameByDegreeId(con, rs.getString("education_id")));
				alInner.add(rs.getString("degree_duration"));
				alInner.add(rs.getString("completion_year"));
				alInner.add(rs.getString("grade"));
				alEducation.add(alInner);
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alEducation;
	}

	
	
	public List<List<String>> selectLanguages(Connection con, int EmpId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alLanguages = new ArrayList<List<String>>();

		try {
			pst = con.prepareStatement("SELECT * FROM trainer_languages_details WHERE trainer_id = ?");
			pst.setInt(1, EmpId);

			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("language_id"));
				alInner.add(rs.getString("language_name"));
				alInner.add(rs.getString("language_read"));
				alInner.add(rs.getString("language_write"));
				alInner.add(rs.getString("language_speak"));
				alLanguages.add(alInner);
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alLanguages;
	}
	
	

	public List<List<String>> selectHobbies(Connection con, int empId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alHobbies = new ArrayList<List<String>>();

		try {
			pst = con.prepareStatement("SELECT * FROM trainer_hobbies_details WHERE trainer_id=? ORDER BY hobbies_name");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> alInner1 = new ArrayList<String>();
				alInner1.add(rs.getInt("hobbies_id") + "");
				alInner1.add(rs.getString("hobbies_name"));
				alInner1.add(rs.getInt("trainer_id") + "");
				alHobbies.add(alInner1);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return alHobbies;
	}
	
	

	public List<List<String>> selectSkills(Connection con, int EmpId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alSkills = new ArrayList<List<String>>();
//		StringBuilder sb = new StringBuilder();
//		String str = "";
		try {
			pst = con.prepareStatement("SELECT * FROM trainer_skills_description WHERE trainer_id=? ORDER BY skills_id");
			pst.setInt(1, EmpId);
			rs = pst.executeQuery();
//			int count = 0;
			while (rs.next()) {
				List<String> alInner1 = new ArrayList<String>();
				alInner1.add(rs.getInt("skills_id") + "");
				alInner1.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
				alInner1.add(rs.getString("skills_value"));
				alInner1.add(rs.getInt("trainer_id") + "");

					alSkills.add(alInner1);

//				sb.append(rs.getString("skills_name")
//						+ ((count == 0) ? " [Pri]" : "") + ", ");
//				count++;
			}
			rs.close();
			pst.close();

//			int index = sb.lastIndexOf(",");
//			if (index > 0) {
//				str = sb.substring(0, index);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return alSkills;
	}

	public String getTrainerId() {
		return trainerId;
	}

	public void setTrainerId(String trainerId) {
		this.trainerId = trainerId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
