package com.konnect.jpms.recruitment;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCandidatePopup extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	String fromPage;
	
	CommonFunctions CF = null;
	String recruitId;
	
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			CF = new CommonFunctions();
			CF.setRequest(request);
		}

		/*
		 * boolean isView = CF.getAccess(session, request, uF); if(!isView){
		 * request.setAttribute(PAGE, PAccessDenied);
		 * request.setAttribute(TITLE, TAccessDenied); return ACCESS_DENIED; }
		 */

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, "/jsp/recruitment/AddCandidate.jsp");
		request.setAttribute(TITLE, "Add Candidate");
		
//		System.out.println("uF.parseToInt(getCandidateId())=====>"+uF.parseToInt(getCandidateId()));
//		System.out.println("getMode()======>"+getMode());

		return LOAD;
	}


//	public boolean insertAvailability(UtilityFunctions uF, String strEmpId) {
//
//		
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		boolean isValidSesseion = false;
//		try {
//
//			con = db.makeConnection(con);
//			PreparedStatement pst = null;
//
//			for (int i = 0; getStrDate() != null && i < getStrDate().length; i++) {
//
//				if (getStrDate()[i] != null && getStrDate()[i].length() > 0) {
//					pst = con.prepareStatement("insert into candidate_interview_availability (emp_id, ip_address, _timestamp, _date, _time,recruitment_id) values (?,?,?,?,?,?)");
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setString(2, "");
//					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
//					pst.setDate(4, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
//					pst.setTime(5, uF.getTimeFormat(getStrTime()[i], TIME_FORMAT));
//					pst.setInt(6, uF.parseToInt(getRecruitId()));
//
//					pst.execute();
//				}
//
//			}
//
//			pst = con.prepareStatement("update candidate_personal_details set session_id =? where emp_per_id=?");
//			pst.setString(1, "");
//			pst.setInt(2, uF.parseToInt(getCandidateId()));
//			pst.execute();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//		}
//
//		return isValidSesseion;
//	}


//	public String loadValidateEmployee() {
//
//		try {
//
//			UtilityFunctions uF = new UtilityFunctions();
//
//			wLocationList = new FillWLocation(request).fillWLocation();
//			bankList = new FillBank(request).fillBankDetails();
//			gradeList = new FillGrade(request).fillGrade();
//			desigList = new FillDesig(request).fillDesig();
//			levelList = new FillLevel(request).fillLevel();
//			deptList = new FillDepartment(request).fillDepartment();
//			// supervisorList = new FillEmployee().fillEmployeeCode(strUserType,
//			// strSessionEmpId);
//			supervisorList = new FillEmployee(request).fillSupervisorNameCode(strUserType, strSessionEmpId);
//			serviceList = new FillServices(request).fillServices();
//			countryList = new FillCountry(request).fillCountry();
//			stateList = new FillState(request).fillState();
//
//			rosterDependencyList = new FillApproval().fillYesNo();
//			empTypeList = new FillEmploymentType().fillEmploymentType();
//			paymentModeList = new FillPayMode().fillPaymentMode();
//			empGenderList = new FillGender().fillGender();
//			probationDurationList = new FillProbationDuration().fillProbationDuration();
//			noticeDurationList = new FillNoticeDuration().fillNoticeDuration();
//			leaveTypeList = new FillLeaveType(request).fillLeave();
//			maritalStatusList = new FillMaritalStatus().fillMaritalStatus();
//			bloodGroupList = new FillBloodGroup().fillBloodGroup();
//			degreeDurationList = new FillDegreeDuration().fillDegreeDuration();
//			yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
//			skillsList = new FillSkills(request).fillSkillsWithId();
//			educationalList = new FillEducational(request).fillEducationalQual();
//
//			paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
//
//			request.setAttribute("educationalList", educationalList);
//			request.setAttribute("yearsList", yearsList);
//			request.setAttribute("degreeDurationList", degreeDurationList);
//			request.setAttribute("empGenderList", empGenderList);
//			request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
//			StringBuilder sbdegreeDuration = new StringBuilder();
//			StringBuilder sbPrevEmployment = new StringBuilder();
//			StringBuilder sbSibling = new StringBuilder();
//			StringBuilder sbSkills = new StringBuilder();
//
//			sbdegreeDuration.append("<option value=''> Degree</option>");
//
//			for (int i = 0; i < educationalList.size(); i++) {
//
//				sbdegreeDuration.append("<option value=" + ((FillEducational) educationalList.get(i)).getEduName()+ "> " + ((FillEducational) educationalList.get(i)).getEduName()
//						+ "</option>");
//
//			}
//			sbdegreeDuration.append("<option value=other>Other</option>");
//			sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name= degreeDuration style=width:110px; >" + "<option value=''>Duration</option>");
//
//			for (int i = 0; i < degreeDurationList.size(); i++) {
//
//				sbdegreeDuration.append("<option value=" + ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationID() + "> "
//						+ ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationName() + "</option>");
//
//			}
//
//			sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name=completionYear style=width:110px; >" + "<option value=''>Completion Year</option>");
//
//			for (int i = 0; i < yearsList.size(); i++) {
//
//				sbdegreeDuration.append("<option value=" + ((FillYears) yearsList.get(i)).getYearsID() + "> " + ((FillYears) yearsList.get(i)).getYearsName() + "</option>");
//
//			}
//
//			sbdegreeDuration.append("</select>" + "</td>" + "<td><input type= text  style=width:110px; name=grade ></input></td>"
//					+ "<td><a href=javascript:void(0) onclick=addEducation() class=add >Add</a></td>");
//
//			request.setAttribute("sbdegreeDuration", sbdegreeDuration.toString());
//
//			sbPrevEmployment.append("<table>" + "<tr><td class=txtlabel style=text-align:right> Company Name:</td>"
//					+ "<td><input type=text name=prevCompanyName style=width: 220px; name=prevCompanyLocation ></input></td>" + "</tr>"
//					+ "<tr><td class=txtlabel style=text-align:right> Location:</td>" + "<td> <input type=text style=width: 180px; name=prevCompanyLocation ></input></td>"
//					+ "</tr>" + "<tr><td class=txtlabel style=text-align:right> City: </td>" + "<td><input type=text style=width: 220px; name=prevCompanyCity ></input></td>"
//					+ "</tr>" + "<tr><td class=txtlabel style=text-align:right> State:</td><td><input type=text style=width: 220px; name=prevCompanyState ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right> Country:</td><td><input type=text style=width: 180px; name=prevCompanyCountry ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right> Contact Number:</td><td><input type=text style=width: 180px; name=prevCompanyContactNo ></input>"
//					+ "</td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right> Reporting To:</td><td> <input type=text style=width: 180px; name=prevCompanyReportingTo ></input>"
//					+ "</td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right> From:</td><td> <input type=text style=width: 180px; name=prevCompanyFromDate ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right> To:</td><td> <input type=text style=width: 180px; name=prevCompanyToDate ></input></td></tr> "
//					+ "<tr><td class=txtlabel style=text-align:right> Designation:</td><td> <input type=text style=width: 180px; name=prevCompanyDesination ></input>"
//					+ "</td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right> Responsibility:</td><td> <input type=text style=width: 180px; name=prevCompanyResponsibilities >"
//					+ "</input>" + "</td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right> Skills: </td><td> <input type=text style=width: 180px; name=prevCompanySkills ></input></td></tr>"
//					//+ "<tr><td class=txtlabel style=text-align:right> <a href=javascript:void(0) onclick=addPrevEmployment() class=add>Add more information..</a></td>"
//			);
//
//			request.setAttribute("sbPrevEmployment", sbPrevEmployment.toString());
//
//			sbSibling.append("<table>" + "<tr><td style=text-align:center class=tdLabelheadingBg style=text-align:right colspan=2>Sibling's Information </td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right>Name:</td><td><input type=text style=width: 180px; name=memberName ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right>Date of birth:</td><td> <input type=text style=width: 180px; name=memberDob ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right>Education:</td><td> <input type=text style=width: 180px; name=memberEducation ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right>Occupation:</td><td> <input type=text style=width: 180px; name=memberOccupation ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right>Contact Number:</td><td><input type=text style=width: 180px; name=memberContactNumber ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right>Email Id:</td><td><input type=text style=width: 180px; name=memberEmailId ></input></td></tr>"
//					+ "<tr><td class=txtlabel style=text-align:right>Gender:</td><td>" + "<select name= memberGender>");
//
//			for (int i = 0; i < empGenderList.size(); i++) {
//
//				sbSibling.append("<option value=" + ((FillGender) empGenderList.get(i)).getGenderId() + "> " + ((FillGender) empGenderList.get(i)).getGenderName() + "</option>");
//
//			}
//
//			sbSibling.append("</select>" + "</td></tr>"
//					+ "");
//
//			request.setAttribute("sbSibling", sbSibling.toString());
//
//			sbSkills.append(" <table><tr><td>" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select name=skillName>" + "<option value=>Select Skill Name</option>");
//
//			for (int k = 0; k < skillsList.size(); k++) {
//
//				sbSkills.append("<option value=" + ((FillSkills) skillsList.get(k)).getSkillsId() + "> " + ((FillSkills) skillsList.get(k)).getSkillsName() + "</option>");
//			}
//
//			sbSkills.append("</select></td><td>" + "<select name=skillValue>" + "<option value=>Select Skill Value</option>");
//
//			for (int k = 1; k < 11; k++) {
//				sbSkills.append("<option value=" + k + ">" + k + "</option>");
//			}
//
//			sbSkills.append("</select></td>");
//			log.debug("sbSkills==>" + sbSkills);
//			request.setAttribute("sbSkills", sbSkills);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		return LOAD;
//
//	}

//	public String viewEmployee() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		// int nEmpOffId = 0;
//
//		List<List<String>> alSkills = new ArrayList<List<String>>();
//		List<List<String>> alHobbies;
//		List<List<String>> alLanguages;
//		List<List<String>> alEducation;
//		List<List<Object>> alDocuments;
//		List<List<String>> alPrevEmployment;
//		List<String> alSelectedDateList;
//
//		try {
//
//			wLocationList = new FillWLocation(request).fillWLocation();
//			bankList = new FillBank(request).fillBankDetails();
//			// desigList = new FillDesig().fillDesig();
//			gradeList = new FillGrade(request).fillGrade();
//			deptList = new FillDepartment(request).fillDepartment();
//			supervisorList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
//			serviceList = new FillServices(request).fillServices();
//			countryList = new FillCountry(request).fillCountry();
//			stateList = new FillState(request).fillState();
//
//			rosterDependencyList = new FillApproval().fillYesNo();
//			empTypeList = new FillEmploymentType().fillEmploymentType();
//			paymentModeList = new FillPayMode().fillPaymentMode();
//			
//			con = db.makeConnection(con);
//			setEmpPersonalDetails(con, uF);
//			setEmpReferences(con, uF);
//
//			alSkills = CF.selectCandidateSkills(con, uF.parseToInt(getCandidateId()));
//			alHobbies = CF.selectCandidateHobbies(con, uF.parseToInt(getCandidateId()));
//			alLanguages = CF.selectCandidateLanguages(con, uF.parseToInt(getCandidateId()));
//			alEducation = CF.selectCandidateEducation(con, uF.parseToInt(getCandidateId()));
//
//			String filePath = request.getRealPath("/userDocuments/");
//
//			alDocuments = selectDocuments(con, uF.parseToInt(getCandidateId()), filePath);
//			setEmpFamilyMembers(con, uF);
//			alPrevEmployment = selectPrevEmploment(con, uF.parseToInt(getCandidateId()));
//			setEmpMedicalInfo(con, uF);
//
//			request.setAttribute("alSkills", alSkills);
//
//			request.setAttribute("alHobbies", alHobbies);
//			request.setAttribute("alLanguages", alLanguages);
//			request.setAttribute("alEducation", alEducation);
//			request.setAttribute("alDocuments", alDocuments);
//			request.setAttribute("alPrevEmployment", alPrevEmployment);
//
//			String candName = "";
//			pst = con.prepareStatement("select emp_fname,emp_lname from candidate_personal_details where emp_per_id=?");
//			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			rs = pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			if (rs.next())
//				candName = rs.getString("emp_fname") + " " + rs.getString("emp_lname");
//
//			request.setAttribute("CandidateName", candName);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//
//		return SUCCESS;
//
//	}

	// Interview Schedule


//	public String updateEmployee() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//
//			con = db.makeConnection(con);
//
//			if (uF.parseToInt(getStep()) == 1) {
//				if (getEmpImage() != null)
//					uploadImage(uF.parseToInt(getCandidateId()));
//
//				if (getEmpFname() != null) {
//					updateEmpPersonalDetails(con, uF);
//				}
//			} else if (uF.parseToInt(getStep()) == 5) {
//
//				updateEmpReferences(con, uF);
//
//			}
//
//			StringBuilder sbServices = new StringBuilder();
//
//			for (int i = 0; getService() != null && i < getService().length; i++) {
//
//				if (uF.parseToInt(getService()[i]) > 0) {
//
//					sbServices.append(getService()[i] + ",");
//				}
//
//			}
//
//			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(MANAGER))) {
//
//				
//				if (uF.parseToInt(getStep()) == 2) {
//					updateSkills(con, uF);
//					updateHobbies(con, uF);
//					updateEmpEducation(con, uF);
//					updateEmpLangues(con, uF);
//
//				} else if (uF.parseToInt(getStep()) == 3) {
//					updateEmpFamilyMembers(con, uF);
//
//				} else if (uF.parseToInt(getStep()) == 4) {
//					updateEmpPrevEmploment(con, uF);
//
//				} else if (uF.parseToInt(getStep()) == 6) {
//					updateEmpMedicalInfo(con, uF);
//
//				} else if (uF.parseToInt(getStep()) == 7) {
//					updateDocuments(con, uF);
//
//				} else if (uF.parseToInt(getStep()) == 8) {
////					System.out.println("in update profile");
//					updateInterveiwDates(con, uF);
//				}
//
//			}
//
//			// request.setAttribute(MESSAGE, getEmpCode() +
//			// " updated successfully!");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			request.setAttribute(MESSAGE, "Error in updation");
//
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//
//		return SUCCESS;
//
//	}

//	private void updateInterveiwDates(Connection con, UtilityFunctions uF) {
//
//		try {
//
//			PreparedStatement pst = null;
//
//			if(getStrDate() != null){
//				pst = con.prepareStatement("DELETE FROM candidate_interview_availability WHERE emp_id = ? and recruitment_id=?");
//				pst.setInt(1, uF.parseToInt(getCandidateId()));
//				pst.setInt(2, uF.parseToInt(getRecruitId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//
//				for (int i = 0; getStrDate() != null && i < getStrDate().length; i++) {
//	
//					if (getStrDate()[i] != null && getStrDate()[i].length() > 0) {
//						pst = con.prepareStatement("insert into candidate_interview_availability (emp_id, ip_address, _timestamp, _date, _time,recruitment_id) values (?,?,?,?,?,?)");
//						pst.setInt(1, uF.parseToInt(getCandidateId()));
//						pst.setString(2, "");
//						pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
//						pst.setDate(4, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
//						pst.setTime(5, uF.getTimeFormat(getStrTime()[i], TIME_FORMAT));
//						pst.setInt(6, uF.parseToInt(getRecruitId()));
//						pst.execute();
//					}
//	
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//
//		}
//
//	}

	
	/*
	 * private boolean checkAllSalaryInfoFilled(Connection con, UtilityFunctions
	 * uF) {
	 * 
	 * PreparedStatement pst = null; ResultSet rs = null;
	 * 
	 * try {
	 * 
	 * setEmpOfficialDetails(con, uF);
	 * 
	 * if(getService()!=null && getService().length > 0) {
	 * 
	 * int cnt = 0; int i = 0; for(; i < getService().length ; i++) {
	 * 
	 * pst = con.prepareStatement(
	 * "SELECT * from emp_salary_details WHERE emp_id = ? and service_id = ? LIMIT 1"
	 * ); pst.setInt(1,uF.parseToInt(getCandidateId())); pst.setInt(2,
	 * uF.parseToInt(getService()[i])); log.debug("pst====>"+pst); rs =
	 * pst.executeQuery();
	 * 
	 * while(rs.next()) { cnt++; }
	 * 
	 * }
	 * 
	 * if(getService().length==cnt) { log.debug("all Info available!!"); return
	 * true; }
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * 
	 * }
	 * 
	 * return false; }
	 */

	/*
	 * private void updateActivity(Connection con, UtilityFunctions uF) {
	 * 
	 * PreparedStatement pst = null;
	 * 
	 * try { pst = con.prepareStatement(updateGradeDesigLevel); pst.setInt(1,
	 * uF.parseToInt(getEmpGrade())); pst.setInt(2,
	 * uF.parseToInt(getwLocation())); pst.setInt(3,
	 * uF.parseToInt(getDepartment())); pst.setInt(4,
	 * uF.parseToInt(getEmpGrade())); pst.setInt(5,
	 * uF.parseToInt(getEmpGrade()));
	 * pst.setInt(6,uF.parseToInt(getCandidateId()));
	 * pst.setInt(7,uF.parseToInt(getCandidateId())); pst.execute();
	 * 
	 * 
	 * pst = con.prepareStatement(
	 * "UPDATE employee_activity_details SET emp_status_code=?, notice_period=?, probation_period=? where emp_id=?  and "
	 * +
	 * "entry_date = (select max(entry_date) from employee_activity_details WHERE emp_id = ?)"
	 * ); pst.setString(1, getEmpType()); pst.setInt(2, getNoticeDuration());
	 * pst.setInt(3, getProbationDuration());
	 * pst.setInt(4,uF.parseToInt(getCandidateId()));
	 * pst.setInt(5,uF.parseToInt(getCandidateId())); pst.execute();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	/*
	 * private void updateEmpJoiningDate(Connection con, UtilityFunctions uF) {
	 * 
	 * PreparedStatement pst = null;
	 * 
	 * try { pst = con.prepareStatement(
	 * "UPDATE employee_personal_details SET joining_date = ?, skype_id=?, emp_email_sec=? where emp_per_id = ?"
	 * ); pst.setDate(1, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
	 * pst.setString(2, getSkypeId()); pst.setString(3, getEmpEmailSec());
	 * pst.setInt(4,uF.parseToInt(getCandidateId()));
	 * 
	 * // log.debug("pst==>"+pst);
	 * 
	 * pst.execute();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */
	
	
//	private void updateEmpReferences(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			pst = con.prepareStatement("UPDATE candidate_references SET ref_name = ?, ref_company = ? , ref_designation =? , " + "ref_contact_no = ?, ref_email_id = ? ");
//			pst.setString(1, getRef1Name());
//			pst.setString(2, getRef1Company());
//			pst.setString(3, getRef1Designation());
//			pst.setString(4, getRef1ContactNo());
//			pst.setString(5, getRef1Email());
//
//			log.debug("pst==>" + pst);
//			pst.execute();
//
//			pst = con.prepareStatement("UPDATE candidate_references SET ref_name = ?, ref_company = ? , ref_designation =? , " + "ref_contact_no = ?, ref_email_id = ? ");
//			pst.setString(1, getRef2Name());
//			pst.setString(2, getRef2Company());
//			pst.setString(3, getRef2Designation());
//			pst.setString(4, getRef2ContactNo());
//			pst.setString(5, getRef2Email());
//
//			log.debug("pst==>" + pst);
//			pst.execute();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	
//	private void updateEmpMedicalInfo(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//		try {
//
//			pst = con.prepareStatement("UPDATE candidate_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue1());
//			pst.setString(2, getQue1Desc());
//			pst.setInt(3, uF.parseToInt(getEmpMedicalId1()));
//			log.debug("pst ==>" + pst);
//			pst.execute();
//
//			pst = con.prepareStatement("UPDATE candidate_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue2());
//			pst.setString(2, getQue2Desc());
//			pst.setInt(3, uF.parseToInt(getEmpMedicalId2()));
//			log.debug("pst ==>" + pst);
//			pst.execute();
//
//			pst = con.prepareStatement("UPDATE candidate_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue3());
//			pst.setString(2, getQue3Desc());
//			pst.setInt(3, uF.parseToInt(getEmpMedicalId3()));
//			log.debug("pst ==>" + pst);
//			pst.execute();
//
//			pst = con.prepareStatement("UPDATE candidate_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue4());
//			pst.setString(2, getQue4Desc());
//			pst.setInt(3, uF.parseToInt(getEmpMedicalId4()));
//			log.debug("pst ==>" + pst);
//			pst.execute();
//
//			pst = con.prepareStatement("UPDATE candidate_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue5());
//			pst.setString(2, getQue5Desc());
//			pst.setInt(3, uF.parseToInt(getEmpMedicalId5()));
//			log.debug("pst ==>" + pst);
//			pst.execute();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	
	/*
	 * public void updateProbationPeriod(Connection con, UtilityFunctions uF) {
	 * 
	 * PreparedStatement pst = null;
	 * 
	 * try {
	 * 
	 * StringBuilder sbProbationLeaves = new StringBuilder(); int i = 1; for (;
	 * getProbationLeaves() != null && i < getProbationLeaves().length - 1; i++)
	 * {
	 * 
	 * if (uF.parseToInt(getProbationLeaves()[i]) > 0) {
	 * sbProbationLeaves.append(getProbationLeaves()[i] + ","); } }
	 * 
	 * if(getProbationLeaves()!=null) {
	 * 
	 * if (uF.parseToInt(getProbationLeaves()[i]) > 0) {
	 * sbProbationLeaves.append(getProbationLeaves()[i]); } }
	 * 
	 * for (int i = 0; getProbationLeaves() != null && i <
	 * getProbationLeaves().length; i++) { if
	 * (uF.parseToInt(getProbationLeaves()[i]) > 0 ) {
	 * sbProbationLeaves.append(getProbationLeaves()[i]); if(i <
	 * getProbationLeaves().length -1) { sbProbationLeaves.append(","); } } }
	 * 
	 * pst = con.prepareStatement(
	 * "UPDATE probation_policy SET leaves_types_allowed=?, probation_duration=?, notice_duration = ? "
	 * + "WHERE emp_id = ?"); pst.setString(1, sbProbationLeaves.toString() );
	 * pst.setInt(2, getProbationDuration()); pst.setInt(3,
	 * getNoticeDuration()); pst.setInt(4,uF.parseToInt(getCandidateId()));
	 * log.debug("pst updateProbationPolicy=" + pst); pst.execute();
	 * 
	 * }catch(Exception e) { e.printStackTrace(); } }
	 */

	/*
	 * public void updateUser(Connection con, UtilityFunctions uF, String
	 * strEmpType) {
	 * 
	 * PreparedStatement pst = null;
	 * 
	 * try {
	 * 
	 * if (strEmpType != null && strEmpType.equalsIgnoreCase(ADMIN)) {
	 * 
	 * pst = con.prepareStatement(updateUser1); pst.setString(1, getUserName());
	 * pst.setString(2, getEmpPassword());
	 * pst.setInt(3,uF.parseToInt(getCandidateId())); pst.setInt(4,
	 * uF.parseToInt(getEmpUserTypeId()));
	 * 
	 * pst.execute(); pst.close();
	 * 
	 * }else {
	 * 
	 * pst = con.prepareStatement(updateUser1E); pst.setString(1,
	 * getEmpPassword()); pst.setInt(2,uF.parseToInt(getCandidateId()));
	 * pst.setString(3, getUserName());
	 * 
	 * pst.execute(); pst.close();
	 * 
	 * }
	 * 
	 * }catch(Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */
	/*
	 * public void updateEmpOfficialDetailsAdmin(Connection con,
	 * UtilityFunctions uF, StringBuilder sbServices) {
	 * 
	 * PreparedStatement pst = null;
	 * 
	 * try {
	 * 
	 * pst = con.prepareStatement(
	 * "UPDATE employee_official_details SET depart_id=?, supervisor_emp_id=?, service_id=?, "
	 * +
	 * "available_days=?, wlocation_id=?, is_roster=?, is_attendance=?, emptype=?, first_aid_allowance=?, grade_id = ?,paycycle_duration=?, payment_mode=? WHERE emp_id=?"
	 * ); pst.setInt(1, uF.parseToInt(getDepartment())); pst.setInt(2,
	 * uF.parseToInt(getSupervisor())); pst.setString(3, sbServices.toString());
	 * // pst.setTime(5, uF.getTimeFormat((getAvailFrom() != null) ?
	 * getAvailFrom().replace("T", "") : "", CF.getStrReportTimeFormat())); //
	 * pst.setTime(6, uF.getTimeFormat((getAvailTo() != null) ?
	 * getAvailTo().replace("T", "") : "", CF.getStrReportTimeFormat()));
	 * pst.setString(4, ""); pst.setInt(5, uF.parseToInt(getwLocation()));
	 * pst.setBoolean(6, uF.parseToBoolean(getRosterDependency()));
	 * pst.setBoolean(7, uF.parseToBoolean(getAttendanceDependency()));
	 * pst.setString(8, getEmpType()); pst.setBoolean(9,
	 * getIsFirstAidAllowance()); pst.setInt(10, uF.parseToInt(getEmpGrade()));
	 * pst.setString(11, getStrPaycycleDuration()); pst.setInt(12,
	 * uF.parseToInt(getEmpPaymentMode()));
	 * pst.setInt(13,uF.parseToInt(getCandidateId()));
	 * 
	 * 
	 * 
	 * 
	 * log.debug("pst officialDetails=>"+pst); int cnt = pst.executeUpdate();
	 * log.debug("cnt==>"+cnt); if(cnt==0) { insertEmpOfficialDetails(con, uF,
	 * sbServices); }
	 * 
	 * }catch(Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */
//	public void updateEmpPersonalDetails(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
//
//				pst = con
//						.prepareStatement("UPDATE candidate_personal_details SET emp_fname=?, emp_lname=?, emp_email=?, emp_address1=?, emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_address2_tmp=?, emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, emp_pincode_tmp=?, emp_contactno=?, emp_pan_no = ?, emp_pf_no = ?, emp_gpf_no = ?, emp_gender=?, emp_date_of_birth=?, emp_bank_name=?, emp_bank_acct_nbr=?, emp_email_sec=?, skype_id=?, emp_contactno_mob=?, emergency_contact_name=?, emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?, emp_date_of_marriage=?,recruitment_id=?,job_code=?,added_by=?,_timestamp=CURRENT_TIMESTAMP WHERE emp_per_id=?");
//				// pst.setString(1, getEmpCodeAlphabet()+getEmpCodeNumber());
//				pst.setString(1, getEmpFname());
//				pst.setString(2, getEmpLname());
//				pst.setString(3, getEmpEmail());
//
//				pst.setString(4, getEmpAddress1());
//				pst.setString(5, getEmpAddress2());
//				pst.setString(6, getCity());
//				pst.setInt(7, uF.parseToInt(getState()));
//				pst.setInt(8, uF.parseToInt(getCountry()));
//				pst.setString(9, getEmpPincode());
//
//				pst.setString(10, getEmpAddress1Tmp());
//				pst.setString(11, getEmpAddress2Tmp());
//				pst.setString(12, getCityTmp());
//				pst.setInt(13, uF.parseToInt(getStateTmp()));
//				pst.setInt(14, uF.parseToInt(getCountryTmp()));
//				pst.setString(15, getEmpPincodeTmp());
//
//				pst.setString(16, getEmpContactno());
//				// pst.setDate(11, uF.getDateFormat(getEmpStartDate(),
//				// DATE_FORMAT));
//				// pst.setString(13, (fileName.length() > 0) ? fileName :
//				// "avatar_photo.png");
//				pst.setString(17, getEmpPanNo());
//				pst.setString(18, getEmpPFNo());
//				pst.setString(19, getEmpGPFNo());
//				pst.setString(20, getEmpGender());
//				pst.setDate(21, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
//				pst.setString(22, getEmpBankName());
//				pst.setString(23, getEmpBankAcctNbr());
//				pst.setString(24, getEmpEmailSec());
//				pst.setString(25, getSkypeId());
//				pst.setString(26, getEmpMobileNo());
//
//				pst.setString(27, getEmpEmergencyContactName());
//				pst.setString(28, getEmpEmergencyContactNo());
//				pst.setString(29, getEmpPassportNo());
//				pst.setDate(30, uF.getDateFormat(getEmpPassportExpiryDate(), DATE_FORMAT));
//				pst.setString(31, getEmpBloodGroup());
//				pst.setString(32, getEmpMaritalStatus());
//				pst.setDate(33, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
//				pst.setInt(34, uF.parseToInt(getRecruitId()));
//				pst.setString(35, getJobcode());
//				pst.setInt(36, uF.parseToInt(strSessionEmpId));
//				pst.setInt(37, uF.parseToInt(getCandidateId()));
//
//				log.debug("pst updateEmployeeP==>" + pst);
//				pst.execute();
//
//			} else {
//
//				pst = con
//						.prepareStatement("UPDATE candidate_personal_details SET emp_fname=?, emp_lname=?, emp_email=?, emp_address1=?, emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_address2_tmp=?, emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, emp_pincode_tmp=?, emp_contactno=?, emp_pan_no = ?, emp_pf_no = ?, emp_gpf_no = ?, emp_gender=?, emp_date_of_birth=?, emp_bank_name=?, emp_bank_acct_nbr=?, emp_email_sec=?, skype_id=?, emp_contactno_mob=?, emergency_contact_name=?, emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?, emp_date_of_marriage=?,recruitment_id=?,job_code=?,added_by=?,_timestamp=CURRENT_TIMESTAMP WHERE emp_per_id=?");
//				pst.setString(1, getEmpFname());
//				pst.setString(2, getEmpLname());
//				pst.setString(3, getEmpEmail());
//
//				pst.setString(4, getEmpAddress1());
//				pst.setString(5, getEmpAddress2());
//				pst.setString(6, getCity());
//				pst.setInt(7, uF.parseToInt(getState()));
//				pst.setInt(8, uF.parseToInt(getCountry()));
//				pst.setString(9, getEmpPincode());
//
//				pst.setString(10, getEmpAddress1Tmp());
//				pst.setString(11, getEmpAddress2Tmp());
//				pst.setString(12, getCityTmp());
//				pst.setInt(13, uF.parseToInt(getStateTmp()));
//				pst.setInt(14, uF.parseToInt(getCountryTmp()));
//				pst.setString(15, getEmpPincodeTmp());
//				pst.setString(16, getEmpContactno());
//				// pst.setString(11, (fileName.length() > 0) ? fileName :
//				// "avatar_photo.png");
//				pst.setString(17, getEmpPanNo());
//				pst.setString(18, getEmpPFNo());
//				pst.setString(19, getEmpGPFNo());
//				pst.setString(20, getEmpGender());
//				pst.setDate(21, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
//				pst.setString(22, getEmpBankName());
//				pst.setString(23, getEmpBankAcctNbr());
//				pst.setString(24, uF.showData(getEmpEmailSec(), ""));
//				pst.setString(25, uF.showData(getSkypeId(), ""));
//				pst.setString(26, uF.showData(getEmpMobileNo(), ""));
//
//				pst.setString(27, getEmpEmergencyContactName());
//				pst.setString(28, getEmpEmergencyContactNo());
//				pst.setString(29, getEmpPassportNo());
//				pst.setDate(30, uF.getDateFormat(getEmpPassportExpiryDate(), DATE_FORMAT));
//				pst.setString(31, getEmpBloodGroup());
//				pst.setString(32, getEmpMaritalStatus());
//
//				pst.setDate(33, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
//				pst.setInt(34, uF.parseToInt(getRecruitId()));
//				pst.setString(35, getJobcode());
//				pst.setInt(36, uF.parseToInt(strSessionEmpId));
//				pst.setInt(37, uF.parseToInt(getCandidateId()));
//
//				log.debug("pst updateEmployeePE" + pst);
//
//				int pstint = pst.executeUpdate();
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

//	public void updateEmpPrevEmploment(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			pst = con.prepareStatement("DELETE FROM candidate_prev_employment WHERE emp_id = ?");
//			pst.setInt(1, uF.parseToInt(getCandidateId()));
////			System.out.println("delete step 4 pst====>"+pst);
//			log.debug("pst=>" + pst);
//			pst.execute();
//
//			if (getPrevCompanyName() != null && getPrevCompanyName().length > 0) {
//				for (int i = 0; i < getPrevCompanyName().length; i++) {
//
//					if (getPrevCompanyName()[i].length() != 0) {
//
//						pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
//								+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id)"
//								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
//
//						pst.setString(1, getPrevCompanyName()[i]);
//						pst.setString(2, getPrevCompanyLocation()[i]);
//						pst.setString(3, getPrevCompanyCity()[i]);
//						pst.setString(4, getPrevCompanyState()[i]);
//						pst.setString(5, getPrevCompanyCountry()[i]);
//						pst.setString(6, getPrevCompanyContactNo()[i]);
//						pst.setString(7, getPrevCompanyReportingTo()[i]);
//						pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
//						pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
//						pst.setString(10, getPrevCompanyDesination()[i]);
//						pst.setString(11, getPrevCompanyResponsibilities()[i]);
//						pst.setString(12, getPrevCompanySkills()[i]);
//						pst.setInt(13, uF.parseToInt(getCandidateId()));
//
//						log.debug("pst=>" + pst);
////						System.out.println("pst====>"+pst);
//						pst.execute();
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

//	public void updateEmpFamilyMembers(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//		int updateCnt = 0;
//		try {
//
//			pst = con.prepareStatement("UPDATE candidate_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
//					+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");
//
//			pst.setString(1, getMotherName());
//			pst.setDate(2, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
//			pst.setString(3, getMotherEducation());
//			pst.setString(4, getMotherOccupation());
//			pst.setString(5, getMotherContactNumber());
//			pst.setString(6, getMotherEmailId());
//			pst.setString(7, "F");
//			pst.setInt(8, uF.parseToInt(getCandidateId()));
//			pst.setString(9, MOTHER);
//			log.debug("pst=>" + pst);
//			updateCnt = pst.executeUpdate();
//
//			if (updateCnt == 0) {
//				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
//						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
//
//				pst.setString(1, MOTHER);
//				pst.setString(2, getMotherName());
//				log.debug(getMotherDob() + "getMotherDob()");
//				pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
//				pst.setString(4, getMotherEducation());
//				pst.setString(5, getMotherOccupation());
//				pst.setString(6, getMotherContactNumber());
//				pst.setString(7, getMotherEmailId());
//				pst.setString(8, "F");
//				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//			}
//
//			pst = con.prepareStatement("UPDATE candidate_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
//					+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");
//
//			pst.setString(1, getFatherName());
//			pst.setDate(2, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
//			pst.setString(3, getFatherEducation());
//			pst.setString(4, getFatherOccupation());
//			pst.setString(5, getFatherContactNumber());
//			pst.setString(6, getFatherEmailId());
//			pst.setString(7, "F");
//			pst.setInt(8, uF.parseToInt(getCandidateId()));
//			pst.setString(9, FATHER);
//			log.debug("pst=>" + pst);
//			updateCnt = pst.executeUpdate();
//
//			if (updateCnt == 0) {
//
//				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
//						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
//
//				pst.setString(1, FATHER);
//				pst.setString(2, getFatherName());
//				pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
//				pst.setString(4, getFatherEducation());
//				pst.setString(5, getFatherOccupation());
//				pst.setString(6, getFatherContactNumber());
//				pst.setString(7, getFatherEmailId());
//				pst.setString(8, "M");
//				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//
//			}
//
//			pst = con.prepareStatement("UPDATE candidate_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
//					+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");
//
//			pst.setString(1, getSpouseName());
//			pst.setDate(2, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
//			pst.setString(3, getSpouseEducation());
//			pst.setString(4, getSpouseOccupation());
//			pst.setString(5, getSpouseContactNumber());
//			pst.setString(6, getSpouseEmailId());
//			pst.setString(7, getSpouseGender());
//			pst.setInt(8, uF.parseToInt(getCandidateId()));
//			pst.setString(9, SPOUSE);
//			log.debug("pst=>" + pst);
//			updateCnt = pst.executeUpdate();
//
//			if (updateCnt == 0) {
//
//				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
//						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
//
//				pst.setString(1, SPOUSE);
//				pst.setString(2, getSpouseName());
//				pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
//				pst.setString(4, getSpouseEducation());
//				pst.setString(5, getSpouseOccupation());
//				pst.setString(6, getSpouseContactNumber());
//				pst.setString(7, getSpouseEmailId());
//				pst.setString(8, "F");
//				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//			}
//
//			if (getMemberName() != null && getMemberName().length != 0) {
//
//				pst = con.prepareStatement("DELETE FROM candidate_family_members WHERE emp_id = ? and member_type = ?");
//				pst.setInt(1, uF.parseToInt(getCandidateId()));
//				pst.setString(2, SIBLING);
//				log.debug("pst=>" + pst);
//				pst.execute();
//
//				for (int i = 0; i < getMemberName().length; i++) {
//
//					pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
//							+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
//					pst.setString(1, SIBLING);
//					pst.setString(2, getMemberName()[i]);
//					pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
//					pst.setString(4, getMemberEducation()[i]);
//					pst.setString(5, getMemberOccupation()[i]);
//					pst.setString(6, getMemberContactNumber()[i]);
//					pst.setString(7, getMemberEmailId()[i]);
//					pst.setString(8, getMemberGender()[i]);
//					pst.setInt(9, uF.parseToInt(getCandidateId()));
//					log.debug("pst=>" + pst);
//					pst.execute();
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	} 

//	public void updateEmpEducation(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			if (getDegreeName() != null && getDegreeName().length != 0) {
//
//				pst = con.prepareStatement("DELETE FROM candidate_education_details WHERE emp_id = ?");
//				pst.setInt(1, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//
//				String[] degreeNameOther = request.getParameterValues("degreeNameOther");
//				for (int i = 0; i < getDegreeName().length; i++) {
////					System.out.println("getDegreeName()[i]");
//
//					if (getDegreeName()[i].equalsIgnoreCase("other")) {
//
//						pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) " + "VALUES (?,?,?)");
//						pst.setString(1, degreeNameOther[i]);
//						pst.setString(2, "");
//						pst.setInt(3, uF.parseToInt(getOrg_id()));
//
//						pst.execute();
//
//						pst = con.prepareStatement("INSERT INTO candidate_education_details(degree_name, degree_duration, completion_year, grade, emp_id)" + "VALUES (?,?,?,?,?)");
//						pst.setString(1, degreeNameOther[i]);
//						pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
//						pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
//						pst.setString(4, getGrade()[i]);
//						pst.setInt(5, uF.parseToInt(getCandidateId()));
//						log.debug("pst=>" + pst);
//
//						pst.execute();
//					} else {
//						pst = con.prepareStatement("INSERT INTO candidate_education_details(degree_name, degree_duration, completion_year, grade, emp_id)" + "VALUES (?,?,?,?,?)");
//						pst.setString(1, getDegreeName()[i]);
//						pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
//						pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
//						pst.setString(4, getGrade()[i]);
//						pst.setInt(5, uF.parseToInt(getCandidateId()));
//						log.debug("pst=>" + pst);
//						pst.execute();
//					}
//				}
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public void updateEmpLangues(Connection con, UtilityFunctions uF) {
//		
//		PreparedStatement pst = null;
//		
//		try {
//		
//			if(getLanguageName()!=null && getLanguageName().length!=0) {
//				pst = con.prepareStatement("DELETE FROM candidate_languages_details WHERE emp_id = ?");
//				pst.setInt(1, uF.parseToInt(getCandidateId()));
//				pst.execute();
//				
//				if(getIsRead()!=null)
//				if(getIsWrite()!=null)
//				if(getIsSpeak()!=null)
//				
//				for(int i=0; getLanguageName()!=null && i<getLanguageName().length; i++) {
//					if(getLanguageName()[i].length()!=0) {
//						pst = con.prepareStatement("INSERT INTO candidate_languages_details(language_name, language_read, language_write, language_speak, emp_id)" +
//						"VALUES (?,?,?,?,?)");
//						pst.setString(1, getLanguageName()[i]);
//						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
//						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
//						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
//						pst.setInt(5, uF.parseToInt(getCandidateId()));
////						log.debug("pst=>"+pst);
//						pst.execute();
//					}
//				}
//			}
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	
///*
//		PreparedStatement pst = null;
//
//		try {
////			System.out.println("getIsRead() ===> "+getIsRead());
////			System.out.println("getIsWrite() ===> "+getIsWrite().toString());
////			System.out.println("getIsSpeak() ===> "+getIsSpeak().toString());
//
//			if (getLanguageName() != null && getLanguageName().length != 0) {
//
//				pst = con.prepareStatement("DELETE FROM candidate_languages_details WHERE emp_id = ?");
//				pst.setInt(1, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//
//				for (int i = 0; getIsRead() != null && i < getIsRead().length; i++) {
//
//					log.debug("Read" + i + "==>" + getIsRead()[i]);
//
//				}
//
//				for (int i = 0; getLanguageName() != null && i < getLanguageName().length; i++) {
//
//					if (getLanguageName()[i].length() != 0) {
//						pst = con.prepareStatement("INSERT INTO candidate_languages_details(language_name, language_read, language_write, language_speak, emp_id)"
//								+ "VALUES (?,?,?,?,?)");
//						pst.setString(1, getLanguageName()[i]);
//						pst.setInt(2, uF.parseToInt(getIsRead() != null ? getIsRead()[i] : "0"));
//						pst.setInt(3, uF.parseToInt(getIsWrite() != null ? getIsWrite()[i] : "0"));
//						pst.setInt(4, uF.parseToInt(getIsSpeak() != null ? getIsSpeak()[i] : "0"));
//						pst.setInt(5, uF.parseToInt(getCandidateId()));
//						log.debug("pst=>" + pst);
//						pst.execute();
//					}
//
//				}
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}*/
//
//	}

	
//	public void updateDocuments(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//		
//		try {
//			int i=0;
//			if(idDoc!=null) {
//
//				int fileSize=1;
//				
//				for (i=0; getIdDocName()!=null && i<getIdDocName().length; i++) {
//					File file1=null;
//					String filename=null;
//					
//					if(getIdDoc()!=null && getIdDoc().size()>=fileSize && getIdDocStatus().get(i).equals("1")){
//						 file1=getIdDoc().get(fileSize-1);
//						 filename=getIdDocFileName().get(fileSize-1);
//						  filename = uF.uploadFile(request, DOCUMENT_LOCATION, file1, filename);
//						 fileSize++;
//					}
//						
//						if(file1!=null){
//			            pst = con.prepareStatement("INSERT INTO candidate_documents_details (documents_name, documents_type, emp_id, " +
//			            		"documents_file_name,added_by,entry_date) values (?,?,?,?,?,?)");
//			            pst.setString(1, getIdDocName()[i]);
//			            pst.setString(2, getIdDocType()[i]);
//			            pst.setInt(3, uF.parseToInt(getCandidateId()));
//			            pst.setString(4, filename);
//			            pst.setInt(5, uF.parseToInt(strSessionEmpId));
//			            pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//			            pst.execute();
//			            
//						}
//				}
//			}
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		
//	}
	
	
//	public void updateHobbies(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			pst = con.prepareStatement("DELETE from candidate_hobbies_details where emp_id =?");
//			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			if (isDebug)
//				log.debug("pst deleteHobbies=>" + pst);
//			pst.execute();
//
//			for (String h : hobbyName) {
//
//				pst = con.prepareStatement("INSERT INTO candidate_hobbies_details (hobbies_name, emp_id) VALUES (?,?)");
//				pst.setString(1, h);
//				pst.setInt(2, uF.parseToInt(getCandidateId()));
//				if (isDebug)
//					log.debug("pst==>>" + pst);
//				pst.execute();
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

//	public void updateSkills(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			pst = con.prepareStatement("DELETE from candidate_skills_description where emp_id =?");
//			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			log.debug("pst deleteSkills=>" + pst);
//
//			pst.execute();
//
//			for (int i = 0; getSkillName() != null && i < getSkillName().length; i++) {
//
//				if (getSkillName()[i].length() != 0 || getSkillValue()[i].length() != 0) {
//
//					pst = con.prepareStatement("INSERT INTO candidate_skills_description (skills_name, skills_value, emp_id) VALUES (?,?,?)");
//					pst.setString(1, getSkillName()[i]);
//					pst.setString(2, getSkillValue()[i]);
//					pst.setInt(3, uF.parseToInt(getCandidateId()));
//					if (isDebug)
//						log.debug("pst==>>" + pst);
//					pst.execute();
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

//	public void insertEmployee() {
//
//		log.debug("inside insert");
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//
//			con = db.makeConnection(con);
//
//			if (uF.parseToInt(getStep()) == 1) {
//
//				if (uF.parseToInt(getCandidateId()) == 0) {
//					insertCandidatePersonalDetails(con, uF, CF);
//					// insertUser(con, uF,uF.parseToInt(getCandidateId()));
//					uploadImage(uF.parseToInt(getCandidateId()));
//				}
//
//				else {
//
//					updateEmpPersonalDetails(con, uF);
//					uploadImage(uF.parseToInt(getCandidateId()));
//				}
//
//				session.setAttribute("EMPNAME_P", getEmpFname() + " " + getEmpLname());
//				session.setAttribute("EMPID_P", uF.parseToInt(getCandidateId()) + "");
//
//			} else if (uF.parseToInt(getStep()) == 2) {
//
//				insertSkills(con, uF);
//				insertHobbies(con, uF);
//				insertEmpLangues(con, uF);
//				insertEmpEducation(con, uF);
//
//			} else if (uF.parseToInt(getStep()) == 3) {
//
//				insertEmpFamilyMembers(con, uF);
//
//			} else if (uF.parseToInt(getStep()) == 4) {
//
//				insertEmpPrevEmploment(con, uF);
//
//			} else if (uF.parseToInt(getStep()) == 5) {
//
//				insertEmpReferences(con, uF);
//
//			} else if (uF.parseToInt(getStep()) == 6) {
//
//				insertEmpMedicalInfo(con, uF);
//
//			} else if (uF.parseToInt(getStep()) == 7) {
//
//				insertDocuments(con, uF);
//
//			} else if (uF.parseToInt(getStep()) == 8) {
//
//				insertAvailability(uF, getCandidateId());
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	

//	private void insertEmpReferences(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			pst = con.prepareStatement("INSERT INTO candidate_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " + "values(?,?,?,?,?,?)");
//			pst.setString(1, getRef1Name());
//			pst.setString(2, getRef1Company());
//			pst.setString(3, getRef1Designation());
//			pst.setString(4, getRef1ContactNo());
//			pst.setString(5, getRef1Email());
//			pst.setInt(6, uF.parseToInt(getCandidateId()));
//
//			log.debug("pst==>" + pst);
//			pst.execute();
//
//			pst = con.prepareStatement("INSERT INTO candidate_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " + "values(?,?,?,?,?,?)");
//			pst.setString(1, getRef2Name());
//			pst.setString(2, getRef2Company());
//			pst.setString(3, getRef2Designation());
//			pst.setString(4, getRef2ContactNo());
//			pst.setString(5, getRef2Email());
//			pst.setInt(6, uF.parseToInt(getCandidateId()));
//
//			log.debug("pst==>" + pst);
//			pst.execute();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}


	
//	private void insertEmpMedicalInfo1(Connection con, UtilityFunctions uF,String queId,boolean checkQue,String queDesc,File file,String fileName1) {
//		PreparedStatement pst = null;
//		
//		try {
//			
//			
//			String fileName =null;
//			if(file!=null){
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, file, fileName1);
//			 }
//			System.out.println();
//			pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description,filepath) values(?,?,?,?,?)");
//			pst.setInt(1, uF.parseToInt(queId));
//			pst.setInt(2, uF.parseToInt(getCandidateId()));
//			pst.setBoolean(3, checkQue);
//			pst.setString(4, queDesc);
//			pst.setString(5, fileName);
//			
////					log.debug("pst ==>"+pst);
//			pst.execute();
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
//private void insertEmpMedicalInfo(Connection con, UtilityFunctions uF) {
//		
////		PreparedStatement pst = null;
//		File file1=null;
//		String filename=null;
//		int fileSize=1;
//		
////		System.out.println("getQue1DescFile() ====> "+getQue1DescFile());
////		System.out.println("getQue1IdFileStatus().get(0) ====> "+getQue1IdFileStatus().get(0));
//		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(0).equals("1")){
//			 file1=getQue1DescFile().get(fileSize-1);
//			 filename=getQue1DescFileFileName().get(fileSize-1);
////			 System.out.println("file1 ====> "+file1);
////			 System.out.println("filename ====> "+filename);
//			 fileSize++;
//		insertEmpMedicalInfo1(con,uF,getQue1Id(),isCheckQue1(),getQue1Desc(),file1,filename);
//		}
//		
//		file1=null;
//		 filename=null;
//		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(1).equals("1")){
//			 file1=getQue1DescFile().get(fileSize-1);
//			 filename=getQue1DescFileFileName().get(fileSize-1);
//			 fileSize++;
//		insertEmpMedicalInfo1(con,uF,getQue2Id(),isCheckQue2(),getQue2Desc(),file1,filename);
//		}
//		
//		file1=null;
//		 filename=null;
//		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(2).equals("1")){
//			 file1=getQue1DescFile().get(fileSize-1);
//			 filename=getQue1DescFileFileName().get(fileSize-1);
//			 fileSize++;
//		insertEmpMedicalInfo1(con,uF,getQue3Id(),isCheckQue3(),getQue3Desc(),file1,filename);
//		}
//	}
	
	
	
//	private void uploadImage(int empId2) {
//
//		try {
//
//			UploadImage uI = new UploadImage();
//			uI.setServletRequest(request);
//			uI.setImageType("CANDIDATE_IMAGE");
//			uI.setEmpImage(getEmpImage());
//			uI.setEmpId(empId2 + "");
////			System.out.println("getEmpImage() ===> "+getEmpImage());
//			uI.upoadImage();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}

//	public void insertEmpPrevEmploment(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			if (getPrevCompanyName() != null && getPrevCompanyName().length != 0) {
//
//				for (int i = 0; i < getPrevCompanyName().length; i++) {
//
//					if (getPrevCompanyName()[i].length() != 0) {
//
//						pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
//								+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id)"
//								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
//
//						pst.setString(1, getPrevCompanyName()[i]);
//						pst.setString(2, getPrevCompanyLocation()[i]);
//						pst.setString(3, getPrevCompanyCity()[i]);
//						pst.setString(4, getPrevCompanyState()[i]);
//						pst.setString(5, getPrevCompanyCountry()[i]);
//						pst.setString(6, getPrevCompanyContactNo()[i]);
//						pst.setString(7, getPrevCompanyReportingTo()[i]);
//						pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
//						pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
//						pst.setString(10, getPrevCompanyDesination()[i]);
//						pst.setString(11, getPrevCompanyResponsibilities()[i]);
//						pst.setString(12, getPrevCompanySkills()[i]);
//						pst.setInt(13, uF.parseToInt(getCandidateId()));
//
//						log.debug("pst=>" + pst);
//						pst.execute();
//
//					}
//
//				}
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}

//	public void insertDocuments(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//		try {
//			String filePath1 = request.getRealPath("/userDocuments/");
//			String fileName1 = "";
//			if(getIdDoc()!=null && getIdDoc().size()!= 0 ) {
//				
//				for (int i=0; i<getIdDoc().size(); i++) {
//					
//					if(getIdDoc().get(i)!=null & getIdDoc().get(i).length()!= 0) {
//						
//						/*int random1 = new Random().nextInt();
//						fileName1 = random1 + getIdDoc()[i].getName();
//						File fileToCreate = new File(filePath1, fileName1);
//						FileUtils.copyFile(getIdDoc()[i], fileToCreate);
//						*/
//						
//						String fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getIdDoc().get(i), getIdDocFileName().get(i));
//						
//			            pst = con.prepareStatement("INSERT INTO candidate_documents_details (documents_name, documents_type, emp_id, documents_file_name," +
//			            		"added_by,entry_date) values (?,?,?,?,?,?)");
//						pst.setString(1, getIdDocName()[i]);
//			            pst.setString(2, getIdDocType()[i]);
//			            pst.setInt(3, uF.parseToInt(getCandidateId()));
//			            pst.setString(4, fileName);
//			            pst.setInt(5, uF.parseToInt(strSessionEmpId));
//			            pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
////			            log.debug("pst insertDocuments==>"+pst);
//			            pst.execute();
//					}
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}



//	public void insertHobbies(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			if (getHobbyName() != null && getHobbyName().length != 0) {
//
//				for (String h : hobbyName) {
//
//					if (h != null && h.length() != 0 && !h.equals("")) {
//
//						pst = con.prepareStatement("INSERT INTO candidate_hobbies_details (hobbies_name, emp_id) VALUES (?,?)");
//						pst.setString(1, h);
//						pst.setInt(2, uF.parseToInt(getCandidateId()));
//						log.debug("pst==>>" + pst);
//						pst.execute();
//
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}

//	public void insertSkills(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			for (int i = 0; i < getSkillName().length; i++) {
//
//				if (getSkillName()[i] != null && getSkillName()[i].length() != 0 && !getSkillName()[i].equals("")) {
//
//					pst = con.prepareStatement("INSERT INTO candidate_skills_description (skills_name, skills_value, emp_id) VALUES (?,?,?)");
//					pst.setString(1, getSkillName()[i]);
//					pst.setString(2, getSkillValue()[i]);
//					pst.setInt(3, uF.parseToInt(getCandidateId()));
//					log.debug("pst==>>" + pst);
//					pst.execute();
//
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//	}

	
//	public int insertCandidatePersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {
//
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		int empPerId = 0;
//		try {
//			pst = con.prepareStatement("INSERT INTO candidate_personal_details (empcode, emp_fname, emp_lname, emp_email, "
//							+ "emp_address1, emp_address2, emp_city_id, emp_state_id, emp_country_id, emp_pincode, emp_address1_tmp, emp_address2_tmp, emp_city_id_tmp, emp_state_id_tmp, emp_country_id_tmp, emp_pincode_tmp, emp_contactno, joining_date, "
//							+ "emp_pan_no,emp_pf_no,emp_gpf_no, emp_gender, emp_date_of_birth, emp_bank_name, emp_bank_acct_nbr, emp_email_sec, skype_id, emp_contactno_mob, "
//							+ "emergency_contact_name, emergency_contact_no, passport_no, passport_expiry_date, blood_group, marital_status,emp_date_of_marriage, "
//							+ "approved_flag, emp_entry_date, emp_status,recruitment_id,job_code) "
//							+ "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
//			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "") + uF.showData(getEmpCodeNumber(), ""));
//			pst.setString(2, getEmpFname());
//			pst.setString(3, getEmpLname());
//			pst.setString(4, getEmpEmail());
//
//			pst.setString(5, getEmpAddress1());
//			pst.setString(6, getEmpAddress2());
//			pst.setString(7, getCity());
//			pst.setInt(8, uF.parseToInt(getState()));
//			pst.setInt(9, uF.parseToInt(getCountry()));
//			pst.setString(10, getEmpPincode());
//
//			pst.setString(11, getEmpAddress1Tmp());
//			pst.setString(12, getEmpAddress2Tmp());
//			pst.setString(13, getCityTmp());
//			pst.setInt(14, uF.parseToInt(getStateTmp()));
//			pst.setInt(15, uF.parseToInt(getCountryTmp()));
//			pst.setString(16, getEmpPincodeTmp());
//
//			pst.setString(17, getEmpContactno());
//			pst.setDate(18, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
//			pst.setString(19, getEmpPanNo());
//			pst.setString(20, getEmpPFNo());
//			pst.setString(21, getEmpGPFNo());
//			pst.setString(22, getEmpGender());
//			pst.setDate(23, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
//			pst.setString(24, getEmpBankName());
//			pst.setString(25, getEmpBankAcctNbr());
//			pst.setString(26, getEmpEmailSec());
//			pst.setString(27, getSkypeId());
//			pst.setString(28, getEmpMobileNo());
//			pst.setString(29, getEmpEmergencyContactName());
//			pst.setString(30, getEmpEmergencyContactNo());
//			pst.setString(31, getEmpPassportNo());
//			pst.setDate(32, uF.getDateFormat(getEmpPassportExpiryDate(), DATE_FORMAT));
//			pst.setString(33, getEmpBloodGroup());
//			pst.setString(34, getEmpMaritalStatus());
//			pst.setDate(35, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
//			pst.setBoolean(36, false);
//			pst.setTimestamp(37, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
//			pst.setString(38, PROBATION);
//			pst.setInt(39, uF.parseToInt(getRecruitId()));
//			pst.setString(40, getJobcode());
//
//			pst.execute();
//
//			pst = con.prepareStatement("SELECT max(emp_per_id) from candidate_personal_details");
//			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			while (rs.next()) {
//				setCandidateId(rs.getString(1));
//				empPerId = uF.parseToInt(rs.getString(1));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//		return empPerId;
//	}

	
//	public void insertEmpFamilyMembers(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//
//			if (getMotherName() != null && getMotherName().length() > 0) {
//
//				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
//						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
//				pst.setString(1, MOTHER);
//				pst.setString(2, getMotherName());
//				log.debug(getMotherDob() + "getMotherDob()");
//				pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
//				pst.setString(4, getMotherEducation());
//				pst.setString(5, getMotherOccupation());
//				pst.setString(6, getMotherContactNumber());
//				pst.setString(7, getMotherEmailId());
//				pst.setString(8, "F");
//				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//
//			}
//
//			if (getFatherName() != null && getFatherName().length() > 0) {
//
//				pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
//						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
//
//				pst.setString(1, FATHER);
//				pst.setString(2, getFatherName());
//				pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
//				pst.setString(4, getFatherEducation());
//				pst.setString(5, getFatherOccupation());
//				pst.setString(6, getFatherContactNumber());
//				pst.setString(7, getFatherEmailId());
//				pst.setString(8, "M");
//				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//			}
//
//			if (getSpouseName() != null && getSpouseName().length() > 0) {
//				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
//						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
//
//				pst.setString(1, SPOUSE);
//				pst.setString(2, getSpouseName());
//				pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
//				pst.setString(4, getSpouseEducation());
//				pst.setString(5, getSpouseOccupation());
//				pst.setString(6, getSpouseContactNumber());
//				pst.setString(7, getSpouseEmailId());
//				pst.setString(8, "F");
//				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
//				pst.execute();
//			}
//
//			for (int i = 0; i < getMemberName().length; i++) {
//				if (getMemberName()[i] != null && getMemberName()[i].length() > 0) {
//					pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
//							+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
//
//					pst.setString(1, SIBLING);
//					pst.setString(2, getMemberName()[i]);
//					pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
//					pst.setString(4, getMemberEducation()[i]);
//					pst.setString(5, getMemberOccupation()[i]);
//					pst.setString(6, getMemberContactNumber()[i]);
//					pst.setString(7, getMemberEmailId()[i]);
//					pst.setString(8, getMemberGender()[i]);
//					pst.setInt(9, uF.parseToInt(getCandidateId()));
//					log.debug("pst=>" + pst);
//					pst.execute();
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}

//	public void insertEmpEducation(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//
//		try {
//			if (getDegreeName() != null && getDegreeName().length != 0) {
//				String[] degreeNameOther = request.getParameterValues("degreeNameOther");
//				for (int i = 0; i < getDegreeName().length; i++) {
//
//					if (getDegreeName()[i].equalsIgnoreCase("other")) {
//						if(degreeNameOther[i] != null && !degreeNameOther[i].equals("")){
//							pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) " + "VALUES (?,?,?)");
//							pst.setString(1, degreeNameOther[i]);
//							pst.setString(2, "");
//							pst.setInt(3, uF.parseToInt(getOrg_id()));
//							pst.execute();
//	
//							pst = con.prepareStatement("INSERT INTO candidate_education_details(degree_name, degree_duration, completion_year, grade, emp_id)" + "VALUES (?,?,?,?,?)");
//							pst.setString(1, degreeNameOther[i]);
//							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
//							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
//							pst.setString(4, getGrade()[i]);
//							pst.setInt(5, uF.parseToInt(getCandidateId()));
//							log.debug("pst=>" + pst);
//							pst.execute();
//						}
//					} else {
//						if(getDegreeName()[i] != null && !getDegreeName()[i].equals("")){
//							pst = con.prepareStatement("INSERT INTO candidate_education_details(degree_name, degree_duration, completion_year, grade, emp_id)" + "VALUES (?,?,?,?,?)");
//							pst.setString(1, getDegreeName()[i]);
//							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
//							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
//							pst.setString(4, getGrade()[i]);
//							pst.setInt(5, uF.parseToInt(getCandidateId()));
//							log.debug("pst=>" + pst);
//							pst.execute();
//						}
//					}
//
//				}
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}

//	public void insertEmpLangues(Connection con, UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//		
//		try {
//			
//			if(getLanguageName()!=null && getLanguageName().length!=0) {
//			
//				for(int i=0; i<getLanguageName().length; i++) {
//					
//					if(getLanguageName()[i] != null && getLanguageName()[i].length()!=0 && !getLanguageName()[i].equals("")) {
//					
//						pst = con.prepareStatement("INSERT INTO candidate_languages_details(language_name, language_read, language_write, language_speak, emp_id)" +
//						"VALUES (?,?,?,?,?)");
//						pst.setString(1, getLanguageName()[i]);
//						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
//						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
//						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
//						pst.setInt(5, uF.parseToInt(getCandidateId()));
////						log.debug("pst=>"+pst);
//						pst.execute();
//						
//					}
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//		}
//		
//	}

//	public String deleteEmployee(int CandidateId) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("DELETE FROM candidate_personal_details WHERE emp_per_id=?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			/*
//			 * pst = con.prepareStatement(deleteEmployee_O); pst.setInt(1,
//			 * CandidateId); pst.execute();
//			 * 
//			 * pst = con.prepareStatement(deleteAllowance); pst.setInt(1,
//			 * CandidateId); pst.execute();
//			 * 
//			 * pst = con.prepareStatement(deleteUserEmp); pst.setInt(1,
//			 * CandidateId); pst.execute();
//			 */
//			pst = con.prepareStatement("DELETE from candidate_skills_description where emp_id =?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			pst = con.prepareStatement("DELETE from candidate_hobbies_details where emp_id =?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			pst = con.prepareStatement("DELETE FROM candidate_languages_details WHERE emp_id = ?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			pst = con.prepareStatement("DELETE FROM candidate_education_details WHERE emp_id = ?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			pst = con.prepareStatement("DELETE FROM candidate_documents_details WHERE emp_id = ?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			pst = con.prepareStatement("DELETE FROM candidate_family_members WHERE emp_id = ?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			pst = con.prepareStatement("DELETE FROM candidate_prev_employment WHERE emp_id = ?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			pst = con.prepareStatement("DELETE FROM candidate_references WHERE emp_id = ?");
//			pst.setInt(1, CandidateId);
//			pst.execute();
//
//			// request.setAttribute(MESSAGE, "Deleted successfully!");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ERROR;
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	
	// for record particular job id
	

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}


	String degreeNameOther;

	public String getDegreeNameOther() {
		return degreeNameOther;
	}

	public void setDegreeNameOther(String degreeNameOther) {
		this.degreeNameOther = degreeNameOther;
	}

	String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
