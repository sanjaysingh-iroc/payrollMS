package com.konnect.jpms.training;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillBloodGroup;
import com.konnect.jpms.select.FillCity;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillDegreeDuration;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMaritalStatus;
import com.konnect.jpms.select.FillNoticeDuration;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillProbationDuration;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTrainerNewTemp extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	private String mode;
	private int empId;
	private String operation;
	Boolean autoGenerate = false;
	private String step; 
	private String serviceId;  
	private String ServiceName;

	private String stepSubmit;
	
	private String type;
	StringBuilder sbServicesLink = new StringBuilder();
	CommonFunctions CF = null;
	
	
	private static Logger log = Logger.getLogger(AddTrainerNew.class);
	
	public String execute() {

//		log.debug("AddTrainerNew: execute() step=>"+getStep());
//		log.debug("EmpId===>"+getEmpId());
//		log.debug("getMode===>"+getMode());
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF==null){
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/ 
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		String title="Add Trainer";
		if (getOperation()!=null && getOperation().equals("U") && getEmpId()!=0 ) {
			title="Edit Trainer";
		}
		
		request.setAttribute(PAGE, "/jsp/training/AddTrainer.jsp");		
		request.setAttribute(TITLE, title);
		
		UtilityFunctions uF = new UtilityFunctions();
				
		
		if (getOperation()!=null && getOperation().equals("U") && getEmpId()!=0 && getMode()!=null && getType()!=null) {
			
			request.setAttribute(TITLE, title);
			
			viewEmployee();
			loadValidateEmployee();	
			
			setType(null);
			return LOAD;
			
		}else if (getEmpId()!=0 && getMode()!=null && (getMode().equals("profile"))) {
			//|| getMode().equals("report")
			
			
			
			if (getStepSubmit() != null){
				updateEmployee();
			}
			
			/*
			Notifications nF = new Notifications(N_UPD_EMPLOYEE_PROFILE, CF);
			nF.setStrEmpId(getEmpId()+"");
//				nF.setStrHostAddress(request.getRemoteHost());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrContextPath(request.getContextPath());
			nF.sendNotifications();*/
		
			
			return PROFILE;
			
		
		}else if (getOperation()!=null && getOperation().equals("U") && getEmpId()!=0 ) {
			
			if (getStepSubmit() != null){
				updateEmployee();
			}
			viewEmployee();
			loadValidateEmployee();		 	
				
		}else if (getOperation()!=null && operation.equals("D"))	{
			deleteEmployee();
			return "deleted";
		
		} 
		
		
		if (uF.parseToInt(getStep())!= 0) {
			if(getOperation()==null || !getOperation().equals("U"))
			{
				insertEmployee();
			}
			loadValidateEmployee();
			if(uF.parseToInt( getStep())==1) {
				setStep("2");
			}else if(uF.parseToInt( getStep())==2) {
				setStep("3");
			}else if(uF.parseToInt( getStep())==3) {
				setStep("4");
			}else if(uF.parseToInt( getStep())==4) {
				setStep("5");
			}else if(uF.parseToInt( getStep())==5) {
				setStep("6");
			}else if(uF.parseToInt( getStep())==6) {
				setStep("7");
				return "finish";
			}
			
		}else if(uF.parseToInt( getStep())==0) {
			
			log.debug("Default Step..");
			setStep("1");
			
			loadValidateEmployee();
			if(strUserType==null){
				session.setAttribute(MENU, "/jsp/common/PreMenu.jsp");
			}
	
		}
		
		return LOAD;
			
	}

	
	String []strTime;
	String []strDate;


	public void updateEmpLiveStatus(int nEmpId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE trainer_personal_details SET is_alive = ?, approved_flag = ? " +
					"WHERE trainer_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, nEmpId);
			
//			log.debug("pst===>"+pst);
			pst.execute();

			
			
		} catch (Exception e) {
				e.printStackTrace();
		}finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	


//	private void setEmpFnameLnameEmail() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs =	null;
//		Database db = new Database();
//		db.setRequest(request);
//		try {
//			
//			con = db.makeConnection(con);
//			pst = con.prepareStatement( "SELECT * FROM trainer_personal_details WHERE trainer_id = ?");
//			pst.setInt(1, getEmpId());
//			
//			log.debug("pst===>"+pst);
//			rs = pst.executeQuery();
//			
//			while(rs.next()) {
//				setEmpFname(rs.getString("emp_fname"));
//				setEmpLname(rs.getString("emp_lname"));
//				setEmpEmail(rs.getString("emp_email"));
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}


	public String loadValidateEmployee() {
		
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			
			wLocationList = new FillWLocation(request).fillWLocation();
			bankList = new FillBank(request).fillBankDetails();
			gradeList = new FillGrade(request).fillGrade();
			desigList = new FillDesig(request).fillDesig();
			levelList = new FillLevel(request).fillLevel();
			deptList = new FillDepartment(request).fillDepartment();
//			supervisorList = new FillEmployee().fillEmployeeCode(strUserType, strSessionEmpId);
			supervisorList = new FillEmployee(request).fillSupervisorNameCode(strUserType, strSessionEmpId); 
			serviceList = new FillServices(request).fillServices();
			countryList = new FillCountry(request).fillCountry();
			stateList = new FillState(request).fillState();
			
			rosterDependencyList = new FillApproval().fillYesNo();
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			paymentModeList = new FillPayMode().fillPaymentMode();
			empGenderList = new FillGender().fillGender();
			probationDurationList = new FillProbationDuration().fillProbationDuration();
			noticeDurationList = new FillNoticeDuration().fillNoticeDuration();
			leaveTypeList = new FillLeaveType(request).fillLeave();
			maritalStatusList = new FillMaritalStatus().fillMaritalStatus();
			bloodGroupList = new FillBloodGroup().fillBloodGroup();
			degreeDurationList = new FillDegreeDuration().fillDegreeDuration();
			yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
			skillsList = new FillSkills(request).fillSkills();
			
			paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
			
			request.setAttribute("yearsList", yearsList);
			request.setAttribute("degreeDurationList", degreeDurationList);
			request.setAttribute("empGenderList", empGenderList);
			request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
			StringBuilder sbdegreeDuration = new StringBuilder();
			StringBuilder sbPrevEmployment = new StringBuilder();
			StringBuilder sbSibling = new StringBuilder();
			StringBuilder sbSkills = new StringBuilder();
			
			sbdegreeDuration.append("<table>" +
					"<tr><td><input type=text style=width:110px; name=degreeName></input></td>" +
					"<td>" +
					"<select name= degreeDuration style=width:110px; >" +
					"<option value=-1>Duration</option>"		
					);
				
			for (int i=0; i<degreeDurationList.size(); i++) {
				
				sbdegreeDuration.append("<option value="+((FillDegreeDuration)degreeDurationList.get(i)).getDegreeDurationID()+"> "+((FillDegreeDuration)degreeDurationList.get(i)).getDegreeDurationName()+"</option>"); 
						
			}
					
			sbdegreeDuration.append(
					"</select>" +
					"</td>" +
					"<td>" +
					"<select name=completionYear style=width:110px; >"+
					"<option value=-1>Completion Year</option>"		
				);
			
			for (int i=0; i<yearsList.size(); i++) {
				
				sbdegreeDuration.append("<option value="+((FillYears)yearsList.get(i)).getYearsID()+"> "+((FillYears)yearsList.get(i)).getYearsName()+"</option>"); 
						
			}
			
			sbdegreeDuration.append("</select>" +
					"</td>" +
					"<td><input type= text  style=width:110px; name=grade ></input></td>" +
					"<td><a href=javascript:void(0) onclick=addEducation() class=add >Add</a></td>"
					);
			
			
			request.setAttribute("sbdegreeDuration", sbdegreeDuration.toString());
			
			sbPrevEmployment.append(
			"<table>"+
		 	"<tr><td class=txtlabel style=text-align:right> Company Name:</td>" +
			"<td><input type=text name=prevCompanyName style=width: 220px; name=prevCompanyLocation ></input></td>" +
			"</tr>" +
			"<tr><td class=txtlabel style=text-align:right> Location:</td>" +
			"<td> <input type=text style=width: 180px; name=prevCompanyLocation ></input></td>" +
			"</tr>" +
			"<tr><td class=txtlabel style=text-align:right> City: </td>" +
			"<td><input type=text style=width: 220px; name=prevCompanyCity ></input></td>" +
			"</tr>" +
			"<tr><td class=txtlabel style=text-align:right> State:</td><td><input type=text style=width: 220px; name=prevCompanyState ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Country:</td><td><input type=text style=width: 180px; name=prevCompanyCountry ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Contact Number:</td><td><input type=text style=width: 180px; name=prevCompanyContactNo ></input>" +                "</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Reporting To:</td><td> <input type=text style=width: 180px; name=prevCompanyReportingTo ></input>" +                "</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> From:</td><td> <input type=text style=width: 180px; name=prevCompanyFromDate ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> To:</td><td> <input type=text style=width: 180px; name=prevCompanyToDate ></input></td></tr> " +
			"<tr><td class=txtlabel style=text-align:right> Designation:</td><td> <input type=text style=width: 180px; name=prevCompanyDesination ></input>" +                "</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Responsibility:</td><td> <input type=text style=width: 180px; name=prevCompanyResponsibilities >" +                "</input>" +  
			"</td></tr>" + 
			"<tr><td class=txtlabel style=text-align:right> Skills: </td><td> <input type=text style=width: 180px; name=prevCompanySkills ></input></td></tr>" + 
			"<tr><td class=txtlabel style=text-align:right> <a href=javascript:void(0) onclick=addPrevEmployment() class=add>Add more information..</a></td>"  
	
			);
			
			request.setAttribute("sbPrevEmployment", sbPrevEmployment.toString());
			
			sbSibling.append(
				"<table>" +
		        "<tr><td style=text-align:center class=tdLabelheadingBg style=text-align:right colspan=2>Sibling's Information </td></tr>" +    
		        "<tr><td class=txtlabel style=text-align:right>Name:</td><td><input type=text style=width: 180px; name=memberName ></input></td></tr>" + 
				"<tr><td class=txtlabel style=text-align:right>Date of birth:</td><td> <input type=text style=width: 180px; name=memberDob ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Education:</td><td> <input type=text style=width: 180px; name=memberEducation ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Occupation:</td><td> <input type=text style=width: 180px; name=memberOccupation ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Contact Number:</td><td><input type=text style=width: 180px; name=memberContactNumber ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Email Id:</td><td><input type=text style=width: 180px; name=memberEmailId ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Gender:</td><td>"+
				"<select name= memberGender>");
			
			for (int i=0; i<empGenderList.size(); i++) {
				
				sbSibling.append("<option value="+((FillGender)empGenderList.get(i)).getGenderId()+"> "+((FillGender)empGenderList.get(i)).getGenderName()+"</option>"); 
						
			}
				
			sbSibling.append("</select>" +
					"</td></tr>" +
					"<tr><td class=txtlabel style=text-align:right><a href=javascript:void(0) onclick=addSibling() class=add>Add another..</a></td>" );
			
			request.setAttribute("sbSibling", sbSibling.toString());
		
			
			sbSkills.append(" <table><tr><td>"+
	                        	"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select name=skillName>" +
	                        	"<option value=>Select Skill Name</option>"
							);
			
			for(int k=0; k< skillsList.size(); k++) { 
				                		
				sbSkills.append("<option value="+((FillSkills)skillsList.get(k)).getSkillsId()+"> "+((FillSkills)skillsList.get(k)).getSkillsName()+"</option>");
			}
			
			sbSkills.append("</select></td><td>" +
	                        "<select name=skillValue>" +
					"<option value=>Select Skill Value</option>"
					);
			
			for(int k=1; k< 11; k++) {
				sbSkills.append("<option value="+k+">"+k+"</option>");
			}
			
			sbSkills.append("</select></td>");
			log.debug("sbSkills==>"+sbSkills);
			request.setAttribute("sbSkills", sbSkills);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return LOAD;
		
	}

//	private void generateEmpCode(Connection con) {
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		String empCodeAlpha = "" , empCodeNum = ""; 
//		Map<String, String> hmSettings;
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			
//			hmSettings = CF.getSettingsMap(con);
//			empCodeAlpha = hmSettings.get(O_EMP_CODE_ALPHA);
//			empCodeNum = hmSettings.get(O_EMP_CODE_NUM);
////			setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
//			
////			if(getAutoGenerate()) {
//				pst = con.prepareStatement("SELECT trainer_code FROM trainer_personal_details where trainer_code like ? order by trainer_id desc LIMIT 1");
//				pst.setString(1, "%"+empCodeAlpha+"%");
//				rs = pst.executeQuery();
//				
//				while(rs.next()) {
//					String strEmpCode = rs.getString("trainer_code");
//					String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
//					log.debug("code Number===>"+strEmpCodeNum);
//					empCodeNum = uF.parseToInt(strEmpCodeNum) + 1 + "";
//				}
//				
//				log.debug("empCodeAlpha===>"+empCodeAlpha);
//				log.debug("empCodeNum===>"+empCodeNum);
//				
//				setEmpCodeAlphabet(empCodeAlpha);
//				setEmpCodeNumber(empCodeNum);
//				
//			setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
//			
//			/***
//			 * This position of code changed on 26-04-2012 for always displaying the auto generated code
//			 */
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}

	public String viewEmployee() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpOffId = 0;
		
		List<List<String>> alSkills = new ArrayList<List<String>>();
		List<List<String>> alHobbies;
		List<List<String>> alLanguages;
		List<List<String>> alEducation;
		List<List<Object>> alDocuments;
		List<List<String>> alPrevEmployment;
		
		try {

			
			
//			request.setAttribute("strEdit", strEdit);
//			setEmpPerId(strEdit);
			
			wLocationList = new FillWLocation(request).fillWLocation();
			bankList = new FillBank(request).fillBankDetails();
//			desigList = new FillDesig().fillDesig();
			gradeList = new FillGrade(request).fillGrade();
			deptList = new FillDepartment(request).fillDepartment();
			supervisorList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
			serviceList = new FillServices(request).fillServices();
			countryList = new FillCountry(request).fillCountry();
			stateList = new FillState(request).fillState();
			
			rosterDependencyList = new FillApproval().fillYesNo();
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			paymentModeList = new FillPayMode().fillPaymentMode();
			con = db.makeConnection(con);
			setEmpPersonalDetails(con, uF);
			setEmpReferences(con, uF);
		
			getSkills(getEmpId(), alSkills,con,uF);
			alHobbies = getHobbies(getEmpId(),con,uF);
			alLanguages = getLanguages(getEmpId(),con,uF);
			alEducation = getEducation(getEmpId(),con,uF);
			
			String filePath = request.getRealPath("/userDocuments/");
			
			alDocuments = getDocuments(getEmpId(),filePath,con,uF);
			alPrevEmployment = getPrevEmploment(getEmpId(),con,uF);
			setEmpMedicalInfo(con,uF);
			
			request.setAttribute("alSkills", alSkills);
			request.setAttribute("alHobbies", alHobbies);
			request.setAttribute("alLanguages", alLanguages);
			request.setAttribute("alEducation", alEducation);
			request.setAttribute("alDocuments", alDocuments);
			request.setAttribute("alPrevEmployment", alPrevEmployment);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}

		return SUCCESS;

	}
	
	private String getSkills(int empId2, List<List<String>> alSkills,
			Connection con, UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		
		StringBuilder sb = new StringBuilder();
		String str = "";
		
		try {
			
			pst = con.prepareStatement("SELECT * FROM trainer_skills_description WHERE trainer_id=? ORDER BY skills_name");
			pst.setInt(1, empId2);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			int count = 0;

			while (rs.next()) {

				List<String> alInner1 = new ArrayList<String>();
				alInner1.add(rs.getInt("skills_id") + "");
				if (count == 0) {
					alInner1.add(rs.getString("skills_name") + " [Pri]");
				} else {
					alInner1.add(rs.getString("skills_name"));
				}
				alInner1.add(rs.getString("skills_value"));
				alInner1.add(rs.getInt("trainer_id") + "");

				if (alSkills != null) {
					alSkills.add(alInner1);
				}

				sb.append(rs.getString("skills_name") + ((count == 0) ? " [Pri]" : "") + ", ");

				count++;
			}
			rs.close();
			pst.close();

			int index = sb.lastIndexOf(",");
			if (index > 0) {
				str = sb.substring(0, index);
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
		
		return str;
		
	}




	private List<List<String>> getPrevEmploment(int empId, Connection con,
			UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		List<List<String>> alPrevEmployment = new ArrayList<List<String>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM trainer_prev_employment WHERE trainer_id = ? order by from_date");
			pst.setInt(1, empId);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();

			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("company_id"));
				alInner.add(rs.getString("company_name"));
				alInner.add(rs.getString("company_location"));
				alInner.add(rs.getString("company_city"));
				alInner.add(rs.getString("company_state"));
				alInner.add(rs.getString("company_country"));
				alInner.add(rs.getString("company_contact_no"));
				alInner.add(rs.getString("reporting_to"));
				alInner.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT));
				alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT));
				alInner.add(rs.getString("designation"));
				alInner.add(rs.getString("responsibilities"));
				alInner.add(rs.getString("skills"));
				alPrevEmployment.add(alInner);
			}
			rs.close();
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
		
		return alPrevEmployment;
		
	}




	private List<List<Object>> getDocuments(int empId, String filePath,
			Connection con, UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		List<List<Object>> alDocuments = new ArrayList<List<Object>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM trainer_documents_details where trainer_id = ?");
			pst.setInt(1, empId);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();

			while (rs.next()) {

				ArrayList<Object> alInner1 = new ArrayList<Object>();
				alInner1.add(rs.getInt("documents_id") + "");
				alInner1.add(rs.getString("documents_name"));
				alInner1.add(rs.getString("documents_type"));
				alInner1.add(rs.getInt("trainer_id") + "");

				// File fileName = new
				// File(filePath+rs.getString("documents_file_name"));

				File fileName = new File(rs.getString("documents_file_name"));

				alInner1.add(fileName);
				alDocuments.add(alInner1);
			}
			rs.close();
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
		
		return alDocuments;
		
	}




	private List<List<String>> getEducation(int empId, Connection con,
			UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		List<List<String>> alEducation = new ArrayList<List<String>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM trainer_education_details WHERE trainer_id = ?");
			pst.setInt(1, empId);
			log.debug("pst=>" + pst);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();

			while (rs.next()) {

				List<String> alInner = new ArrayList<String>();

				alInner.add(rs.getString("degree_id"));
				alInner.add(rs.getString("degree_name"));
				alInner.add(rs.getString("degree_duration"));
				alInner.add(rs.getString("completion_year"));
				alInner.add(rs.getString("grade"));
				alEducation.add(alInner);
			}
			rs.close();
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
		
		return alEducation;
		
	}




	private List<List<String>> getLanguages(int empId, Connection con,
			UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		List<List<String>> alLanguages = new ArrayList<List<String>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM trainer_languages_details WHERE trainer_id = ?");
			pst.setInt(1, empId);
			log.debug("pst=>" + pst);
//			System.out.println("pst====>"+pst);
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
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return alLanguages;
		
	}




	private List<List<String>> getHobbies(int empId, Connection con,
			UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		List<List<String>> alHobbies = new ArrayList<List<String>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM trainer_hobbies_details WHERE trainer_id=? ORDER BY hobbies_name");
			pst.setInt(1, empId);
//			System.out.println("pst====>"+pst);
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
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return alHobbies;
		
	}




	private void setEmpReferences(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("SELECT * FROM trainer_references WHERE trainer_id = ? order by ref_name");
			pst.setInt(1, getEmpId());
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			
			if(rs.next()) {
			
				setRef1Name(rs.getString("ref_name"));
				setRef1Company(rs.getString("ref_company"));
				setRef1Designation(rs.getString("ref_designation"));
				setRef1ContactNo(rs.getString("ref_contact_no"));
				setRef1Email(rs.getString("ref_email_id"));
			}

			if(rs.next()) {
			
				setRef2Name(rs.getString("ref_name"));
				setRef2Company(rs.getString("ref_company"));
				setRef2Designation(rs.getString("ref_designation"));
				setRef2ContactNo(rs.getString("ref_contact_no"));
				setRef2Email(rs.getString("ref_email_id"));
			}
			rs.close();
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


	private void setEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("SELECT * FROM trainer_medical_details WHERE trainer_id = ? order by question_id");
			pst.setInt(1, getEmpId());
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				if(rs.getInt("question_id")==1) {
					setEmpMedicalId1(rs.getString("medical_id"));
					setCheckQue1(uF.parseToBoolean(rs.getString("yes_no")));
					setQue1Desc(uF.showData(rs.getString("description"), ""));
					
				}else if(rs.getInt("question_id")==2) {
					setEmpMedicalId2(rs.getString("medical_id"));
					setCheckQue2(uF.parseToBoolean(rs.getString("yes_no")));
					setQue2Desc(uF.showData(rs.getString("description"), ""));
					
				}else if(rs.getInt("question_id")==3) {
					setEmpMedicalId3(rs.getString("medical_id"));
					setCheckQue3(uF.parseToBoolean(rs.getString("yes_no")));
					setQue3Desc(uF.showData(rs.getString("description"), ""));
					
				}else if(rs.getInt("question_id")==4) {
					setEmpMedicalId4(rs.getString("medical_id"));
					setCheckQue4(uF.parseToBoolean(rs.getString("yes_no")));
					setQue4Desc(uF.showData(rs.getString("description"), ""));
					
				}else if(rs.getInt("question_id")==5) {
					setEmpMedicalId5(rs.getString("medical_id"));
					setCheckQue5(uF.parseToBoolean(rs.getString("yes_no")));
					setQue5Desc(uF.showData(rs.getString("description"), ""));
				}
				
			}
			rs.close();
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



	private int setEmpPersonalDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		int nEmpOffId = 0;
		
		try {
			
//			pst = con.prepareStatement(selectEmployeeR1V);
			
			pst = con.prepareStatement("SELECT * FROM trainer_personal_details WHERE training_id = ?");
			pst.setInt(1, getEmpId());
			log.debug("pst selectEmployeeR1V==>"+pst);
			
//			System.out.println("pst selectEmployeeR1V==>"+pst);
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				
//				nEmpOffId = rs.getInt("emp_off_id");
				setEmpId(rs.getInt("training_id"));
//				setEmpCode(rs.getString("empcode")); 
				
				//setEmpCode(rs.getString("trainer_code"), con, uF);
				
				setEmpFname(rs.getString("trainer_fname"));
				setEmpLname(rs.getString("trainer_lname"));
				setEmpEmail(rs.getString("trainer_email"));
				
				setEmpAddress1(rs.getString("trainer_address1"));
				setEmpAddress2(rs.getString("trainer_address2"));
				setCity(rs.getString("trainer_city_id"));
				setState(rs.getString("trainer_state_id"));
				setCountry(rs.getString("trainer_country_id"));
				setEmpPincode(rs.getString("trainer_pincode"));
				
				setEmpAddress1Tmp(rs.getString("trainer_address1_tmp"));
				setEmpAddress2Tmp(rs.getString("trainer_address2_tmp"));
				setCityTmp(rs.getString("trainer_city_id_tmp"));
				setStateTmp(rs.getString("trainer_state_id_tmp"));
				setCountryTmp(rs.getString("trainer_country_id_tmp"));
				setEmpPincodeTmp(rs.getString("trainer_pincode_tmp"));
				
				
				setEmpContactno(rs.getString("trainer_contactno"));				
				setEmpPanNo(rs.getString("trainer_pan_no"));
				setEmpPFNo(rs.getString("trainer_pf_no"));
				setEmpGPFNo(rs.getString("trainer_gpf_no"));
				setEmpGender(rs.getString("trainer_gender"));
				setEmpDateOfBirth(uF.getDateFormat(rs.getString("trainer_date_of_birth"), DBDATE, DATE_FORMAT));
				setEmpDateOfMarriage(uF.getDateFormat(rs.getString("trainer_date_of_marriage"), DBDATE, DATE_FORMAT));
				setEmpBankName(rs.getString("trainer_bank_name"));
				setEmpBankAcctNbr(rs.getString("trainer_bank_acct_nbr"));
				setEmpEmailSec(rs.getString("trainer_email_sec"));
				setSkypeId(rs.getString("skype_id"));
				setEmpMobileNo(rs.getString("trainer_contactno_mob"));
				
				setEmpEmergencyContactName(rs.getString("emergency_contact_name"));
				setEmpEmergencyContactNo(rs.getString("emergency_contact_no"));
				setEmpPassportNo(rs.getString("passport_no"));
				setEmpPassportExpiryDate(uF.getDateFormat(rs.getString("passport_expiry_date"), DBDATE, DATE_FORMAT));
				setEmpBloodGroup(rs.getString("blood_group"));
				setEmpMaritalStatus(rs.getString("marital_status"));
				
				setEmpStartDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				log.debug("rs.getString(trainer_image)=="+rs.getString("trainer_image"));
				request.setAttribute("strImage", rs.getString("trainer_image"));
				
				
				request.setAttribute("strEmpName", uF.showData(rs.getString("trainer_fname"),"") +" "+uF.showData(rs.getString("trainer_lname"),"") );
				
				/*if(rs.getString("service_id")!=null){
					setService(rs.getString("service_id").split(","));
				}*/
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM training_trainer WHERE trainer_id = ?");
			pst.setInt(1, getEmpId());
			log.debug("pst selectEmployeeR1V==>"+pst);
			
//			System.out.println("pst selectEmployeeR1V==>"+pst);
			
			rs = pst.executeQuery();
			
			if (rs.next()) {				
				setStrwLocation(rs.getString("trainer_work_location"));
			}
			rs.close();
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
		
		return nEmpOffId;
		
	}

//	private void setEmpCode(String empCode, Connection con, UtilityFunctions uF) {
//		
//		try {
//			
//			Map<String, String> hmSettings = CF.getSettingsMap(con);
//			String empCodeAlpha = hmSettings.get(O_EMP_CODE_ALPHA); 
//			boolean isAutoGeneration = uF.parseToBoolean(hmSettings.get(O_EMP_CODE_AUTO_GENERATION));
//			
//			if(empCode.contains(empCodeAlpha)) {
//				
//				setEmpCodeAlphabet(empCodeAlpha);
//				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
//				setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
//				
//			}else if(empCode.length()==0) {
//				generateEmpCode(con);
//			}else{
//				setEmpCodeAlphabet(empCode.substring(0, empCodeAlpha.length()));
//				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
//				setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
//					
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}

	
	public String updateEmployee() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);

				if(uF.parseToInt( getStep())==1) {
//					if(getEmpImage()!=null)
//						uploadImage(getEmpId());					
					if(getEmpFname()!=null){
						updateEmpPersonalDetails(con, uF);
					}
				}else if(uF.parseToInt( getStep())==2) {
					updateSkills(con, uF);
					updateHobbies(con, uF);
					updateEmpEducation(con, uF);
					updateEmpLangues(con, uF);
				
				}else if(uF.parseToInt( getStep())==3) {
					updateEmpPrevEmploment(con, uF);
				}else if(uF.parseToInt( getStep())==4) {
					updateEmpReferences(con, uF);				
				}else if(uF.parseToInt( getStep())==5) {					
					updateEmpMedicalInfo(con, uF);
				}else if(uF.parseToInt( getStep())==6) { 
					//updateDocuments(con, uF);
					insertDocuments(con, uF);
				}
				
			
//			request.setAttribute(MESSAGE, getEmpCode() + " updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
			
		} finally {
			 
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;

	}


	private void updateEmpReferences(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("DELETE from trainer_references where trainer_id =?");
			pst.setInt(1, getEmpId());
//			System.out.println("pst====>"+pst);
			log.debug("pst deleteSkills=>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO trainer_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, trainer_id) " +
			"values(?,?,?,?,?,?)");
			pst.setString(1, getRef1Name());
			pst.setString(2, getRef1Company());
			pst.setString(3, getRef1Designation());
			pst.setString(4, getRef1ContactNo());
			pst.setString(5, getRef1Email());
			pst.setInt(6, getEmpId());
			log.debug("pst==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO trainer_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, trainer_id) " +
						"values(?,?,?,?,?,?)");
			pst.setString(1, getRef2Name());
			pst.setString(2, getRef2Company());
			pst.setString(3, getRef2Designation());
			pst.setString(4, getRef2ContactNo());
			pst.setString(5, getRef2Email());
			pst.setInt(6, getEmpId());
			log.debug("pst==>"+pst);
			pst.execute();
			pst.close();
			
			/*pst = con.prepareStatement("UPDATE trainer_references SET ref_name = ?, ref_company = ? , ref_designation =? , " +
							"ref_contact_no = ?, ref_email_id = ? where trainer_id=? ");
			pst.setString(1, getRef1Name());
			pst.setString(2, getRef1Company());
			pst.setString(3, getRef1Designation());
			pst.setString(4, getRef1ContactNo());
			pst.setString(5, getRef1Email());
			pst.setInt(6, getEmpId());
			System.out.println("pst ref1=====>"+pst);
			log.debug("pst==>"+pst);
			pst.execute();
			
			pst = con.prepareStatement("UPDATE trainer_references SET ref_name = ?, ref_company = ? , ref_designation =? , " +
						"ref_contact_no = ?, ref_email_id = ? where trainer_id=?");
			pst.setString(1, getRef2Name());
			pst.setString(2, getRef2Company());
			pst.setString(3, getRef2Designation());
			pst.setString(4, getRef2ContactNo());
			pst.setString(5, getRef2Email());
			pst.setInt(6, getEmpId());
			System.out.println("pst ref2=====>"+pst);
			log.debug("pst==>"+pst);
			pst.execute();*/
			
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


	private void updateEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		try {
			
			pst = con.prepareStatement("UPDATE trainer_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
			pst.setBoolean(1, isCheckQue1());
			pst.setString(2, getQue1Desc());
			pst.setInt(3, uF.parseToInt(getEmpMedicalId1()));
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("UPDATE trainer_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
			pst.setBoolean(1, isCheckQue2());
			pst.setString(2, getQue2Desc());
			pst.setInt(3, uF.parseToInt(getEmpMedicalId2()));
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("UPDATE trainer_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
			pst.setBoolean(1, isCheckQue3());
			pst.setString(2, getQue3Desc());
			pst.setInt(3, uF.parseToInt(getEmpMedicalId3()));
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("UPDATE trainer_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
			pst.setBoolean(1, isCheckQue4());
			pst.setString(2, getQue4Desc());
			pst.setInt(3, uF.parseToInt(getEmpMedicalId4()));
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("UPDATE trainer_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
			pst.setBoolean(1, isCheckQue5());
			pst.setString(2, getQue5Desc());
			pst.setInt(3, uF.parseToInt(getEmpMedicalId5()));
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
		}catch(Exception e) {
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

	public void updateEmpPersonalDetails(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst=con.prepareStatement("update training_trainer set trainer_name=?,trainer_mobile=?,trainer_email=?, " +
					" trainer_address=?,trainer_city=?,trainer_state=?,trainer_country=?,trainer_work_location=? where trainer_id=? ");
			
			pst.setString(1, getEmpFname()+" "+getEmpLname());
			pst.setString(2, getEmpEmail());
			pst.setString(3, getEmpMobileNo());
			pst.setString(4, getEmpAddress1());
			pst.setString(5, getCity());
			pst.setInt(6, uF.parseToInt(getState()));
			pst.setInt(7, uF.parseToInt(getCountry()));
			pst.setInt(8, uF.parseToInt(getStrwLocation()));
			pst.setInt(9, getEmpId());
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("update trainer_personal_details set trainer_fname=?, trainer_lname=?, trainer_email=?, " +
					"trainer_address1=?, trainer_address2=?, trainer_city_id=?, trainer_state_id=?, trainer_country_id=?, trainer_pincode=?, " +
					" trainer_address1_tmp=?, trainer_address2_tmp=?, trainer_city_id_tmp=?, trainer_state_id_tmp=?, trainer_country_id_tmp=?," +
					" trainer_pincode_tmp=?, trainer_contactno=?, joining_date=?, trainer_pan_no=?,trainer_pf_no=?,trainer_gpf_no=?, trainer_gender=?," +
					" trainer_date_of_birth=?, trainer_bank_name=?, trainer_bank_acct_nbr=?, trainer_email_sec=?, skype_id=?, trainer_contactno_mob=?, " +
					" emergency_contact_name=?, emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?," +
					" trainer_date_of_marriage=?, approved_flag=?, trainer_entry_date=? where training_id=? ");
			pst.setString(1, getEmpFname());
			pst.setString(2, getEmpLname());
			pst.setString(3, getEmpEmail());
			pst.setString(4, getEmpAddress1());
			pst.setString(5, getEmpAddress2());
			pst.setString(6, getCity());
			pst.setInt(7, uF.parseToInt(getState()));
			pst.setInt(8, uF.parseToInt(getCountry()));
			pst.setString(9, getEmpPincode());
			
			pst.setString(10, getEmpAddress1Tmp());
			pst.setString(11, getEmpAddress2Tmp());
			pst.setString(12, getCityTmp());
			pst.setInt(13, uF.parseToInt(getStateTmp()));
			pst.setInt(14, uF.parseToInt(getCountryTmp()));
			pst.setString(15, getEmpPincodeTmp());
			
			pst.setString(16, getEmpContactno());
			pst.setDate(17, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
			pst.setString(18, getEmpPanNo());
			pst.setString(19, getEmpPFNo());
			pst.setString(20, getEmpGPFNo());
			pst.setString(21, getEmpGender());
			pst.setDate(22,  uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setString(23, getEmpBankName());
			pst.setString(24, getEmpBankAcctNbr());
			pst.setString(25, getEmpEmailSec());
			pst.setString(26, getSkypeId());
			pst.setString(27, getEmpMobileNo());
			pst.setString(28, getEmpEmergencyContactName());
			pst.setString(29, getEmpEmergencyContactNo());
			pst.setString(30, getEmpPassportNo());
			pst.setDate(31, uF.getDateFormat(getEmpPassportExpiryDate(),DATE_FORMAT));
			pst.setString(32, getEmpBloodGroup());
			pst.setString(33, getEmpMaritalStatus());
			pst.setDate(34,  uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
			pst.setBoolean(35, false);
			pst.setTimestamp(36, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(37, getEmpId());
//			System.out.println("pst====>"+pst);
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

	public void updateEmpPrevEmploment(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("DELETE FROM trainer_prev_employment WHERE trainer_id = ?");
			pst.setInt(1, getEmpId());
//			log.debug("pst=>"+pst);
			pst.execute();
			pst.close();
			
			if(getPrevCompanyName()!=null && getPrevCompanyName().length > 0) {
				for(int i=0; i<getPrevCompanyName().length; i++) {
					
					if(getPrevCompanyName()[i].length()!=0) {
						
						pst = con.prepareStatement("INSERT INTO trainer_prev_employment(company_name, company_location, company_city, company_state, " +
								"company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, trainer_id)" +
											"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						pst.setString(1, getPrevCompanyName()[i]);
						pst.setString(2, getPrevCompanyLocation()[i]);
						pst.setString(3, getPrevCompanyCity()[i]);
						pst.setString(4, getPrevCompanyState()[i]);
						pst.setString(5, getPrevCompanyCountry()[i]);
						pst.setString(6, getPrevCompanyContactNo()[i]);
						pst.setString(7, getPrevCompanyReportingTo()[i]);
						pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
						pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
						pst.setString(10, getPrevCompanyDesination()[i]);
						pst.setString(11, getPrevCompanyResponsibilities()[i]);
						pst.setString(12, getPrevCompanySkills()[i]);
						pst.setInt(13, getEmpId());
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
				}
			}
			
		}catch (Exception e) {
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

	/*public void updateEmpFamilyMembers(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		int updateCnt = 0;
		try {
			
			pst = con.prepareStatement("UPDATE trainer_family_members SET member_name = ?, member_dob = ?, member_education = ?, " +
							"member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " +
							"WHERE trainer_id = ? and member_type = ?");
			
			pst.setString(1, getMotherName());
			pst.setDate(2, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
			pst.setString(3, getMotherEducation());
			pst.setString(4, getMotherOccupation());
			pst.setString(5, getMotherContactNumber());
			pst.setString(6, getMotherEmailId());
			pst.setString(7, "F");
			pst.setInt(8, getEmpId());
			pst.setString(9, MOTHER);
			log.debug("pst=>"+pst);
			updateCnt = pst.executeUpdate();
			
			if(updateCnt==0) {
				pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, " +
						"member_occupation, member_contact_no, member_email_id, member_gender, trainer_id)" +
							"VALUES (?,?,?,?,?,?,?,?,?)");
		
				pst.setString(1, MOTHER);
				pst.setString(2, getMotherName());
				log.debug(getMotherDob()+"getMotherDob()");
				pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
				pst.setString(4, getMotherEducation());
				pst.setString(5, getMotherOccupation());
				pst.setString(6, getMotherContactNumber());
				pst.setString(7, getMotherEmailId());
				pst.setString(8, "F");
				pst.setInt(9, getEmpId());
				log.debug("pst=>"+pst);
				pst.execute();
			}
			
			pst = con.prepareStatement("UPDATE trainer_family_members SET member_name = ?, member_dob = ?, member_education = ?, " +
					"member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " +
					"WHERE trainer_id = ? and member_type = ?");
		
			pst.setString(1, getFatherName());
			pst.setDate(2, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
			pst.setString(3, getFatherEducation());
			pst.setString(4, getFatherOccupation());
			pst.setString(5, getFatherContactNumber());
			pst.setString(6, getFatherEmailId());
			pst.setString(7, "F");
			pst.setInt(8,getEmpId());
			pst.setString(9, FATHER);
			log.debug("pst=>"+pst);
			updateCnt = pst.executeUpdate();
			
			if(updateCnt==0) {
				
				pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, " +
						"member_occupation, member_contact_no, member_email_id, member_gender, trainer_id)" +
							"VALUES (?,?,?,?,?,?,?,?,?)");
			
				pst.setString(1, FATHER);
				pst.setString(2, getFatherName());
				pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
				pst.setString(4, getFatherEducation());
				pst.setString(5, getFatherOccupation());
				pst.setString(6, getFatherContactNumber());
				pst.setString(7, getFatherEmailId());
				pst.setString(8, "M");
				pst.setInt(9, getEmpId());
				log.debug("pst=>"+pst);
				pst.execute();
				
			}
			
			pst = con.prepareStatement("UPDATE trainer_family_members SET member_name = ?, member_dob = ?, member_education = ?, " +
					"member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " +
					"WHERE trainer_id = ? and member_type = ?");
			
			pst.setString(1, getSpouseName());
			pst.setDate(2, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
			pst.setString(3, getSpouseEducation());
			pst.setString(4, getSpouseOccupation());
			pst.setString(5, getSpouseContactNumber());
			pst.setString(6, getSpouseEmailId());
			pst.setString(7, getSpouseGender());
			pst.setInt(8, getEmpId());
			pst.setString(9, SPOUSE);
			log.debug("pst=>"+pst);
			updateCnt = pst.executeUpdate();
			
			if(updateCnt==0) {
				
				pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, " +
						"member_occupation, member_contact_no, member_email_id, member_gender, trainer_id)" +
							"VALUES (?,?,?,?,?,?,?,?,?)");
			
				pst.setString(1, SPOUSE);
				pst.setString(2, getSpouseName());
				pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
				pst.setString(4, getSpouseEducation());
				pst.setString(5, getSpouseOccupation());
				pst.setString(6, getSpouseContactNumber());
				pst.setString(7, getSpouseEmailId());
				pst.setString(8, "F");
				pst.setInt(9, getEmpId());
				log.debug("pst=>"+pst);
				pst.execute();
			}
			
			if(getMemberName().length != 0) {
				
				pst = con.prepareStatement("DELETE FROM trainer_family_members WHERE trainer_id = ? and member_type = ?");
				pst.setInt(1, getEmpId());
				pst.setString(2, SIBLING);	
				log.debug("pst=>"+pst);
				pst.execute();
				
				for(int i=0; i<getMemberName().length; i++) {
					
					pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, " +
							"member_occupation, member_contact_no, member_email_id, member_gender, trainer_id)" +
								"VALUES (?,?,?,?,?,?,?,?,?)");
			
					pst.setString(1, SIBLING);
					pst.setString(2, getMemberName()[i]);
					pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
					pst.setString(4, getMemberEducation()[i]);
					pst.setString(5, getMemberOccupation()[i]);
					pst.setString(6, getMemberContactNumber()[i]);
					pst.setString(7, getMemberEmailId()[i]);
					pst.setString(8, getMemberGender()[i]);
					pst.setInt(9, getEmpId());
					log.debug("pst=>"+pst);
					pst.execute();
					
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/

	public void updateEmpEducation(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			if(getDegreeName()!=null && getDegreeName().length!= 0) {
				
				pst = con.prepareStatement("DELETE FROM trainer_education_details WHERE trainer_id = ?");
				pst.setInt(1, getEmpId());
				log.debug("pst=>"+pst);
//				System.out.println("pst====>"+pst);
				pst.execute();
				pst.close();
				
				for(int i=0; i<getDegreeName().length; i++) {
					
					if(getDegreeName()[i].length()!=0) {
						
						pst = con.prepareStatement("INSERT INTO trainer_education_details(degree_name, degree_duration, completion_year, grade, trainer_id)" +
											"VALUES (?,?,?,?,?)");
						pst.setString(1, getDegreeName()[i]);
						pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
						pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
						pst.setString(4, getGrade()[i]);
						pst.setInt(5, getEmpId());
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
					
				}
				
			}
			
		}catch (Exception e) {
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

	public void updateEmpLangues(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
		
			if(getLanguageName()!=null && getLanguageName().length!=0) {
				
				pst = con.prepareStatement("DELETE FROM trainer_languages_details WHERE trainer_id = ?");
				pst.setInt(1, getEmpId());
//				log.debug("pst=>"+pst);
//				System.out.println("pst====>"+pst);
				pst.execute();
				pst.close();
				
				for(int i=0; getIsRead()!=null && i<getIsRead().length; i++) {
//					log.debug("Read"+i+"==>"+getIsRead()[i]);
					
				}
				
				for(int i=0; getLanguageName()!=null && i<getLanguageName().length; i++) {
					
					if(getLanguageName()[i].length()!=0) {
						pst = con.prepareStatement("INSERT INTO trainer_languages_details(language_name, language_read, language_write, language_speak, trainer_id)" +
												"VALUES (?,?,?,?,?)");
						pst.setString(1, getLanguageName()[i]);
						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
						pst.setInt(5, getEmpId());
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
				}
				
			}
			
		}catch (Exception e) {
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

	public void updateDocuments(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		
		try {
			
//			pst = con.prepareStatement(deleteDocuments);
//			pst.setInt(1, getEmpId());
//			log.debug("pst deleteDocuments=>"+pst);
//			pst.execute();
			
			String filePath1 = request.getRealPath("/userDocuments/");
			String fileName1 = "";

			int i=0;
			
			if(idDoc!=null) {
				
//				log.debug("idDoc.length==>"+idDoc.length+" "+idDocName.length);
				
//				for (i=0; getDocId()!=null && i<getDocId().length; i++) {
				for (i=0; getIdDocName()!=null && i<getIdDocName().length; i++) {
					
					if(getIdDoc()[i]!=null & getIdDoc()[i].length()!= 0) {
						
						/*int random1 = new Random().nextInt();
						fileName1 = random1 + getIdDoc()[i].getName();
						File fileToCreate = new File(filePath1, fileName1);
						FileUtils.copyFile(getIdDoc()[i], fileToCreate);
						*/
						
						String fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getIdDoc()[i], getIdDocFileName()[i], CF.getIsRemoteLocation(), CF);
						
						
			            pst = con.prepareStatement("INSERT INTO trainer_documents_details (documents_name, documents_type, trainer_id, documents_file_name) values (?,?,?,?)");
			            pst.setString(1, getIdDocName()[i]);
			            pst.setString(2, getIdDocType()[i]);
			            pst.setInt(3, getEmpId());
//			            pst.setString(4, fileName1);
			            pst.setString(4, fileName);
			            
//			            log.debug("pst insertDocuments==>"+pst);
			            pst.execute();
						pst.close();
					}
				}
			}
			
			
		}catch (Exception e) {
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

	public void updateHobbies(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("DELETE from trainer_hobbies_details where trainer_id =?");
			pst.setInt(1, getEmpId());
//			if(isDebug)
//				log.debug("pst deleteHobbies=>"+pst);
//			System.out.println("pst====>"+pst);
			pst.execute();
			pst.close();
			
			for(String h: hobbyName) {
				
				pst = con.prepareStatement("INSERT INTO trainer_hobbies_details (hobbies_name, trainer_id) VALUES (?,?)");
				pst.setString(1, h);
				pst.setInt(2,getEmpId());
//				if(isDebug)
//					log.debug("pst==>>"+pst);
				pst.execute();
				pst.close();
			}

		}catch (Exception e) {
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

	public void updateSkills(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("DELETE from trainer_skills_description where trainer_id =?");
			pst.setInt(1, getEmpId());
//			System.out.println("pst====>"+pst);
//			log.debug("pst deleteSkills=>"+pst);
			pst.execute();
			pst.close();
			
			for(int i=0; getSkillName()!= null && i<getSkillName().length; i++) {
				
				if(getSkillName()[i].length()!=0 || getSkillValue()[i].length()!=0) {
				
					pst = con.prepareStatement("INSERT INTO trainer_skills_description (skills_name, skills_value, trainer_id) VALUES (?,?,?)");
					pst.setString(1, getSkillName()[i]);
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3,getEmpId());
//					if(isDebug)
//						log.debug("pst==>>"+pst);
					pst.execute();
					pst.close();
				}
			}
			
		}catch (Exception e) {
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

	public void insertEmployee() {
	
//		log.debug("inside insert");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			if(uF.parseToInt( getStep())==1) {

				if(getEmpId()==0) {
					setEmpId(insertCandidatePersonalDetails(con, uF, CF));
//					uploadImage(getEmpId());
				}
				
				
				session.setAttribute("EMPNAME_P", getEmpFname()+" "+getEmpLname());
				session.setAttribute("EMPID_P", getEmpId()+"");
				
			}else if(uF.parseToInt( getStep())==2) {				
				insertSkills(con, uF);
				insertHobbies(con, uF);
				insertEmpLangues(con, uF);
				insertEmpEducation(con, uF);			
			}else if(uF.parseToInt( getStep())==3) {				
				insertEmpPrevEmploment(con, uF);			
			}else if(uF.parseToInt( getStep())==4) {
				//updateEmpReferences(con, uF);
				insertEmpReferences(con, uF);
			}else if(uF.parseToInt( getStep())==5) {				
				insertEmpMedicalInfo(con, uF);				
			}else if(uF.parseToInt( getStep())==6) {
				insertDocuments(con, uF);			
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
/*	private void approveEmployee() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE trainer_personal_details SET approved_flag = ? , is_alive = ? WHERE trainer_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, getEmpId());
			log.debug("pst===>"+pst);
			int cnt =pst.executeUpdate();
			log.debug("cnt==>"+cnt);
			
		} catch (Exception e) {
				e.printStackTrace();
		}finally {
			db.closeConnection(con);
			db.closeStatements(pst);
		}
		
	}


	private void updateEmpCodeBankInfo(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("UPDATE trainer_personal_details SET empcode = ?, emp_bank_name = ? , emp_bank_acct_nbr = ? " +
					"where trainer_id = ?");
			pst.setString(1, getEmpCodeAlphabet()+getEmpCodeNumber());
			pst.setString(2, getEmpBankName());
			pst.setString(3, getEmpBankAcctNbr());
			pst.setInt(4, getEmpId());
			
			log.debug("pst==>"+pst);
			pst.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/

	private void insertEmpReferences(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("INSERT INTO trainer_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, trainer_id) " +
						"values(?,?,?,?,?,?)");
			pst.setString(1, getRef1Name());
			pst.setString(2, getRef1Company());
			pst.setString(3, getRef1Designation());
			pst.setString(4, getRef1ContactNo());
			pst.setString(5, getRef1Email());
			pst.setInt(6, getEmpId());
//			log.debug("pst==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO trainer_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, trainer_id) " +
						"values(?,?,?,?,?,?)");
			pst.setString(1, getRef2Name());
			pst.setString(2, getRef2Company());
			pst.setString(3, getRef2Designation());
			pst.setString(4, getRef2ContactNo());
			pst.setString(5, getRef2Email());
			pst.setInt(6, getEmpId());
//			log.debug("pst==>"+pst);
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


	private void insertEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {

			pst = con.prepareStatement("INSERT INTO trainer_medical_details (question_id, trainer_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue1Id()));
			pst.setInt(2, getEmpId());
			pst.setBoolean(3, isCheckQue1());
			pst.setString(4, getQue1Desc());
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO trainer_medical_details (question_id, trainer_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue2Id()));
			pst.setInt(2, getEmpId());
			pst.setBoolean(3, isCheckQue2());
			pst.setString(4, getQue2Desc());
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO trainer_medical_details (question_id, trainer_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue3Id()));
			pst.setInt(2, getEmpId());
			pst.setBoolean(3, isCheckQue3());
			pst.setString(4, getQue3Desc());
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO trainer_medical_details (question_id, trainer_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue4Id()));
			pst.setInt(2, getEmpId());
			pst.setBoolean(3, isCheckQue4());
			pst.setString(4, getQue4Desc());
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO trainer_medical_details (question_id, trainer_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue5Id()));
			pst.setInt(2, getEmpId());
			pst.setBoolean(3, isCheckQue5());
			pst.setString(4, getQue5Desc());
//			log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
		}catch (Exception e) {
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

	private void sendNotifications() {
		
//		Notifications nF = new Notifications(N_NEW_EMPLOYEE);
//		nF.setStrEmailTo(getEmpEmail());
//		nF.setStrEmpMobileNo(getEmpContactno());
//		nF.setStrEmpCode(getEmpCode());
//		nF.setStrEmpFname(getEmpFname());
//		nF.setStrEmpLname(getEmpLname());
//		nF.setStrUserName(getUserName());
//		nF.setStrPassword(getEmpPassword());
//		nF.sendNotifications();
		
	}

//	private void uploadImage(int empId2) {		
//	 try {
//			
//			UploadImage uI = new UploadImage();
//			uI.setServletRequest(request);
//			uI.setImageType("EMPLOYEE_IMAGE");
//			uI.setEmpImage(getEmpImage());
//			uI.setEmpId(empId2+"");
//			uI.upoadImage();
//			
//		}catch (Exception e) {
//			e.printStackTrace();			
//		}		
//	}

	public void insertEmpPrevEmploment(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
	
			if(getPrevCompanyName()!=null && getPrevCompanyName().length!=0) {
				
				for(int i=0; i<getPrevCompanyName().length; i++) {

					if(getPrevCompanyName()[i].length()!=0) {
						
						pst = con.prepareStatement("INSERT INTO trainer_prev_employment(company_name, company_location, company_city, company_state, " +
								"company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, trainer_id)" +
											"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						pst.setString(1, getPrevCompanyName()[i]);
						pst.setString(2, getPrevCompanyLocation()[i]);
						pst.setString(3, getPrevCompanyCity()[i]);
						pst.setString(4, getPrevCompanyState()[i]);
						pst.setString(5, getPrevCompanyCountry()[i]);
						pst.setString(6, getPrevCompanyContactNo()[i]);
						pst.setString(7, getPrevCompanyReportingTo()[i]);
						pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
						pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
						pst.setString(10, getPrevCompanyDesination()[i]);
						pst.setString(11, getPrevCompanyResponsibilities()[i]);
						pst.setString(12, getPrevCompanySkills()[i]);
						pst.setInt(13, getEmpId());
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
					
				}
				
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

	public void insertDocuments(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			String filePath1 = request.getRealPath("/userDocuments/");
			String fileName1 = "";

			if(getIdDoc()!=null && getIdDoc().length!= 0 ) {
				
//				log.debug("getIdDoc().length==>"+getIdDoc().length);
//				log.debug("idDoc.length==>"+idDoc.length+" "+getIdDocName().length);
				
				log.debug(getIdDoc()[0]);
				
				for (int i=0; i<getIdDoc().length; i++) {
					
					if(getIdDoc()[i]!=null & getIdDoc()[i].length()!= 0) {
						
											
						String fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getIdDoc()[i], getIdDocFileName()[i], CF.getIsRemoteLocation(), CF);
						
			            pst = con.prepareStatement("INSERT INTO trainer_documents_details (documents_name, documents_type, trainer_id, documents_file_name) values (?,?,?,?)");
						pst.setString(1, getIdDocName()[i]);
			            pst.setString(2, getIdDocType()[i]);
			            pst.setInt(3, getEmpId());
			            pst.setString(4, fileName);
//			            log.debug("pst insertDocuments==>"+pst);
			            pst.execute();
						pst.close();
					}
		            
				}
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

	public void insertHobbies(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			if(getHobbyName()!=null && getHobbyName().length!=0 ) {
				
				for(String h: hobbyName) {
					
					if(h.length()!=0 && h!="") {
						
						pst = con.prepareStatement( "INSERT INTO trainer_hobbies_details (hobbies_name, trainer_id) VALUES (?,?)");
						pst.setString(1, h);
						pst.setInt(2, getEmpId());
//						log.debug("pst==>>"+pst);
						pst.execute();
						pst.close();
					}
				}
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

	public void insertSkills(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
				for(int i=0; i <getSkillName().length; i++) {
					
					if(getSkillName()[i].length()!=0) {
					
						pst = con.prepareStatement( "INSERT INTO trainer_skills_description (skills_name, skills_value, trainer_id) VALUES (?,?,?)");
						pst.setString(1, getSkillName()[i]);
						pst.setString(2, getSkillValue()[i]);
						pst.setInt(3, getEmpId());
//						log.debug("pst==>>"+pst);
						pst.execute();
						pst.close();
					}
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

	
	public int insertCandidatePersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {
		
		PreparedStatement pst = null;
		ResultSet rs =null;
		int empPerId = 0;
		
		try {
			
			pst=con.prepareStatement("insert into training_trainer (trainer_name,trainer_mobile,trainer_email, " +
					"trainer_address,trainer_city,trainer_state,trainer_country,trainer_work_location)" +
					" values(?,?,?,?,?,?,?,?)");
			
			pst.setString(1, getEmpFname()+" "+getEmpLname());
			pst.setString(2, getEmpEmail());
			pst.setString(3, getEmpMobileNo());
			pst.setString(4, getEmpAddress1());
			pst.setString(5, getCity());
			pst.setInt(6, uF.parseToInt(getState()));
			pst.setInt(7, uF.parseToInt(getCountry()));
			pst.setInt(8, uF.parseToInt(getStrwLocation()));			
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("SELECT max(trainer_id) from training_trainer");
			rs = pst.executeQuery();
			while(rs.next()) {
				empPerId = rs.getInt(1);
			}
			rs.close();
			pst.close();
			

			pst = con.prepareStatement("INSERT INTO trainer_personal_details (trainer_fname, trainer_lname, trainer_email, " +
					"trainer_address1, trainer_address2, trainer_city_id, trainer_state_id, trainer_country_id, trainer_pincode, trainer_address1_tmp, trainer_address2_tmp, trainer_city_id_tmp, trainer_state_id_tmp, trainer_country_id_tmp, trainer_pincode_tmp, trainer_contactno, joining_date, " +
					"trainer_pan_no,trainer_pf_no,trainer_gpf_no, trainer_gender, trainer_date_of_birth, trainer_bank_name, trainer_bank_acct_nbr, trainer_email_sec, skype_id, trainer_contactno_mob, " +
					"emergency_contact_name, emergency_contact_no, passport_no, passport_expiry_date, blood_group, marital_status,trainer_date_of_marriage, " +
					"approved_flag, trainer_entry_date,training_id) " +
					"VALUES (?,  ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?) ");
			pst.setString(1, getEmpFname());
			pst.setString(2, getEmpLname());
			pst.setString(3, getEmpEmail());
			
			pst.setString(4, getEmpAddress1());
			pst.setString(5, getEmpAddress2());
			pst.setString(6, getCity());
			pst.setInt(7, uF.parseToInt(getState()));
			pst.setInt(8, uF.parseToInt(getCountry()));
			pst.setString(9, getEmpPincode());
			
			pst.setString(10, getEmpAddress1Tmp());
			pst.setString(11, getEmpAddress2Tmp());
			pst.setString(12, getCityTmp());
			pst.setInt(13, uF.parseToInt(getStateTmp()));
			pst.setInt(14, uF.parseToInt(getCountryTmp()));
			pst.setString(15, getEmpPincodeTmp());
			
			pst.setString(16, getEmpContactno());
			pst.setDate(17, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
			pst.setString(18, getEmpPanNo());
			pst.setString(19, getEmpPFNo());
			pst.setString(20, getEmpGPFNo());
			pst.setString(21, getEmpGender());
			pst.setDate(22,  uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setString(23, getEmpBankName());
			pst.setString(24, getEmpBankAcctNbr());
			pst.setString(25, getEmpEmailSec());
			pst.setString(26, getSkypeId());
			pst.setString(27, getEmpMobileNo());
			pst.setString(28, getEmpEmergencyContactName());
			pst.setString(29, getEmpEmergencyContactNo());
			pst.setString(30, getEmpPassportNo());
			pst.setDate(31, uF.getDateFormat(getEmpPassportExpiryDate(),DATE_FORMAT));
			pst.setString(32, getEmpBloodGroup());
			pst.setString(33, getEmpMaritalStatus());
			pst.setDate(34,  uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
			pst.setBoolean(35, false);
			pst.setTimestamp(36, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(37, empPerId);
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
		
		return empPerId;
		
	}

	public void insertEmpEducation(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		
		try {
			
			if(getDegreeName()!=null && getDegreeName().length!= 0) {
				
				for(int i=0; i<getDegreeName().length; i++) {
					
					if(getDegreeName()[i].length()!=0) {
						pst = con.prepareStatement("INSERT INTO trainer_education_details(degree_name, degree_duration, completion_year, grade, trainer_id)" +
											"VALUES (?,?,?,?,?)");
						pst.setString(1, getDegreeName()[i]);
						pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
						pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
						pst.setString(4, getGrade()[i]);
						pst.setInt(5, getEmpId());
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
					
				}
				
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

	public void insertEmpLangues(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			if(getLanguageName()!=null && getLanguageName().length!=0) {
			
				for(int i=0; i<getLanguageName().length; i++) {
					
					if(getLanguageName()[i].length()!=0 && getLanguageName()[i]!="") {
					
						pst = con.prepareStatement("INSERT INTO trainer_languages_details(language_name, language_read, language_write, language_speak, trainer_id)" +
												"VALUES (?,?,?,?,?)");
						pst.setString(1, getLanguageName()[i]);
						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
						pst.setInt(5, getEmpId());
//						log.debug("pst=>"+pst);
//						System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
					}
				}
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

	public String deleteEmployee() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		int deleteEmpId = getEmpId();
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("DELETE FROM training_trainer WHERE trainer_id=?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM trainer_personal_details WHERE training_id=?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("DELETE from trainer_skills_description where trainer_id =?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE from trainer_hobbies_details where trainer_id =?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM trainer_languages_details WHERE trainer_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM trainer_education_details WHERE trainer_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement( "DELETE FROM trainer_documents_details WHERE trainer_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM trainer_prev_employment WHERE trainer_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM trainer_references WHERE trainer_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM trainer_medical_details WHERE trainer_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			//request.setAttribute(MESSAGE, "Deleted successfully!");
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	private String empUserTypeId;
//	private String empPerId;
//	private String empId;
	private String empCodeAlphabet;
	private String empCodeNumber;
	private String empFname;
	private String empLname;
	
	private String userName;
	private String empPassword;
	
	private String empEmail;
	
	private String empAddress1;
	private String empAddress2;
	private String country;
	private String state;
	private String city;
	private String empPincode;
	
	
	private String empAddress1Tmp;
	private String empAddress2Tmp;
	private String countryTmp;
	private String stateTmp;
	private String cityTmp;
	private String empPincodeTmp;
	
	private String empContactno;	
	private String empEmergencyContactName;
	private String empEmergencyContactNo;
	private String empPassportNo;
	private String empPassportExpiryDate;
	private String empBloodGroup;
	private String empMaritalStatus;
	private boolean approvedFlag;
	
	private Timestamp empFilledFlagDate;
	
	private String empImageFileName;
	private File empImage;
	
	private String empPanNo;
	private String empPFNo;
	private String empGPFNo;
	private String empGender;
	private String empDateOfBirth;
	private String empDateOfMarriage;
	private String empBankName;
	private String empBankAcctNbr;
	private String empEmailSec;
	private String skypeId;
	private String empMobileNo;
	
	private String ref1Name;
	private String ref1Company;
	private String ref1Designation;
	private String ref1ContactNo;
	private String ref1Email;
	
	private String ref2Name;
	private String ref2Company;
	private String ref2Designation;
	private String ref2ContactNo;
	private String ref2Email;
	
	private String empStartDate;
	private String wLocation;
	private String empGrade;
	private String strLevel;
	private String strDesignation;
	private String department;
	private String supervisor;
	private String[] service;
	private String availFrom;
	private String availTo;
	private String rosterDependency;
	private String attendanceDependency;
	private String empType;
	private boolean isFirstAidAllowance;
	private int probationDuration;
	private int noticeDuration;
	
	
	private String []empKra;
	private String []empKraId;
	
	
	private String[] probationLeaves;

	
	private String[] skillName;
	private String[] skillValue;
	
	private String[] hobbyName;
	
	private String[] languageName;
	private String[] isRead;
	private String[] isWrite;
	private String[] isSpeak;
	
	private String[] degreeName;
	private String[] degreeDuration;
	private String[] completionYear;
	private String[] grade;
	
	private File[] idDoc;
	private String[] idDocFileName;
	private int[] docId;
	private String[] idDocName;
	private String[] idDocType;
	
	private String fatherName;
	private String fatherDob;
	private String fatherEducation;
	private String fatherOccupation;
	private String fatherContactNumber;
	private String fatherEmailId;
	private String motherName;
	private String motherDob;
	private String motherEducation;
	private String motherOccupation;
	private String motherContactNumber;
	private String motherEmailId;
	
	private String spouseName;
	private String spouseDob;
	private String spouseEducation;
	private String spouseOccupation;
	private String spouseContactNumber;
	private String spouseEmailId;
	private String spouseGender;
	
	private String[] memberName;
	private String[] memberDob;
	private String[] memberEducation;
	private String[] memberOccupation;
	private String[] memberContactNumber;
	private String[] memberEmailId;
	private String[] memberGender;
	
	private String[] documentNames;
	private String[] documentValues;
	
	private String[] prevCompanyName;
	private String[] prevCompanyLocation;
	private String[] prevCompanyCity;
	private String[] prevCompanyState;
	private String[] prevCompanyCountry;
	private String[] prevCompanyContactNo;
	private String[] prevCompanyReportingTo;
	private String[] prevCompanyFromDate;
	private String[] prevCompanyToDate;
	private String[] prevCompanyDesination;
	private String[] prevCompanyResponsibilities;
	private String[] prevCompanySkills;
	
	private String que1Desc;
	private String que2Desc;
	private String que3Desc;
	private String que4Desc;
	private String que5Desc;
	private String que1Id;
	private String que2Id;
	private String que3Id;
	private String que4Id;
	private String que5Id;
	
	private boolean checkQue1;
	private boolean checkQue2;
	private boolean checkQue3;
	private boolean checkQue4;
	private boolean checkQue5;
	
	private String empMedicalId1;
	private String empMedicalId2;
	private String empMedicalId3;
	private String empMedicalId4;
	private String empMedicalId5;
	
	
//	private String empDesignation;
	
	List<FillEmploymentType> empTypeList;
	List<FillApproval> rosterDependencyList;
	List<FillGrade> gradeList;
	List<FillDesig> desigList;
	List<FillLevel> levelList;
	List<FillDepartment> deptList;
	List<FillEmployee> supervisorList;
	List<FillServices> serviceList;
	List<FillCountry> countryList;
	List<FillState> stateList;
	List<FillCity> cityList;
	List<FillWLocation> wLocationList;
	List<FillBank> bankList;
	List<FillGender> empGenderList;
	List<FillProbationDuration> probationDurationList;
	List<FillNoticeDuration> noticeDurationList;
	List<FillLeaveType> leaveTypeList;
	List<FillMaritalStatus> maritalStatusList;
	List<FillBloodGroup> bloodGroupList;
	List<FillDegreeDuration> degreeDurationList;
	List<FillYears> yearsList;
	List<FillSkills> skillsList;
	
	String empPaymentMode;
	List<FillPayMode> paymentModeList;
	
	List<FillPayCycleDuration> paycycleDurationList;
	String strPaycycleDuration;
	
	
		
	private String redirectUrl;
	
	public String getEmpFname() {
		return empFname;
	}

	public void setEmpFname(String empFname) {
		this.empFname = empFname;
	}

	public String getEmpLname() {
		return empLname;
	}

	public void setEmpLname(String empLname) {
		this.empLname = empLname;
	}

	public String getEmpAddress1() {
		return empAddress1;
	}

	public void setEmpAddress1(String empAddress1) {
		this.empAddress1 = empAddress1;
	}

	public String getEmpAddress2() {
		return empAddress2;
	}

	public void setEmpAddress2(String empAddress2) {
		this.empAddress2 = empAddress2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmpPincode() {
		return empPincode;
	}

	public void setEmpPincode(String empPincode) {
		this.empPincode = empPincode;
	}

	public String getEmpContactno() {
		return empContactno;
	}

	public void setEmpContactno(String empContactno) {
		this.empContactno = empContactno;
	}
	
	public String getEmpPanNo() {
		return empPanNo;
	}
	
	public void setEmpPanNo(String empPanNo) {
		this.empPanNo = empPanNo;
	}

	public String getEmpGender() {
		return empGender;
	}
	
	public void setEmpGender(String empGender) {
		this.empGender = empGender;
	}
	
	public void setEmpDateOfBirth(String empDateOfBirth) {
		this.empDateOfBirth=empDateOfBirth;
	}
	
	public String getEmpDateOfBirth() {
		return empDateOfBirth;
	}
	
	public void setEmpBankName(String empBankName) {
		this.empBankName = empBankName;
	}
	
	public String getEmpBankName() {
		return empBankName;
	}
	
	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String[] getService() {
		return service;
	}

	public void setService(String[] service) {
		this.service = service;
	}

	public String getAvailFrom() {
		return availFrom;
	}

	public void setAvailFrom(String availFrom) {
		this.availFrom = availFrom;
	}

	public String getAvailTo() {
		return availTo;
	}

	public void setAvailTo(String availTo) {
		this.availTo = availTo;
	}

	public List<FillWLocation> getWLocationList() {
		return wLocationList;
	}
	
	public List<FillDepartment> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<FillDepartment> deptList) {
		this.deptList = deptList;
	}

	public List<FillEmployee> getSupervisorList() {
		return supervisorList;
	}

	public void setSupervisorList(List<FillEmployee> supervisorList) {
		this.supervisorList = supervisorList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}

	public List<FillCity> getCityList() {
		return cityList;
	}

	public void setCityList(List<FillCity> cityList) {
		this.cityList = cityList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getRosterDependency() {
		return rosterDependency;
	}

	public void setRosterDependency(String rosterDependency) {
		this.rosterDependency = rosterDependency;
	}

	public List<FillApproval> getRosterDependencyList() {
		return rosterDependencyList;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	public boolean getIsFirstAidAllowance() {
		return isFirstAidAllowance;
	}

	public void setIsFirstAidAllowance(boolean isFirstAidAllowance) {
		this.isFirstAidAllowance = isFirstAidAllowance;
	}

	public void setEmpImage(File empImage) {
		this.empImage = empImage;
	}

	public File getEmpImage() {
		return empImage;
	}

	public String getEmpImageFileName() {
		return empImageFileName;
	}

	public void setEmpImageFileName(String empImageFileName) {
		this.empImageFileName = empImageFileName;
	}

	public String getEmpPassword() {
		return empPassword;
	}

	public void setEmpPassword(String empPassword) {
		this.empPassword = empPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public String getEmpStartDate() {
		return empStartDate;
	}

	public void setEmpStartDate(String empStartDate) {
		this.empStartDate = empStartDate;
	}

	public String getEmpUserTypeId() {
		return empUserTypeId;
	}

	public void setEmpUserTypeId(String empUserTypeId) {
		this.empUserTypeId = empUserTypeId;
	}

	public String getEmpBankAcctNbr() {
		return empBankAcctNbr;
	}

	public void setEmpBankAcctNbr(String empBankAcctNbr) {
		this.empBankAcctNbr = empBankAcctNbr;
	}

	public List<FillGender> getEmpGenderList() {
		return empGenderList;
	}

	public void setEmpGenderList(List<FillGender> empGenderList) {
		this.empGenderList = empGenderList;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public List<FillProbationDuration> getProbationDurationList() {
		return probationDurationList;
	}

	public void setProbationDurationList(
			List<FillProbationDuration> probationDurationList) {
		this.probationDurationList = probationDurationList;
	}

	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}

	public int getProbationDuration() {
		return probationDuration;
	}

	public void setProbationDuration(int probationDuration) {
		this.probationDuration = probationDuration;
	}

	public String getEmpEmailSec() {
		return empEmailSec;
	}

	public void setEmpEmailSec(String empEmailSec) {
		this.empEmailSec = empEmailSec;
	}

	public String getSkypeId() {
		return skypeId;
	}

	public void setSkypeId(String skypeId) {
		this.skypeId = skypeId;
	}

	public String getEmpMobileNo() {
		return empMobileNo;
	}

	public void setEmpMobileNo(String empMobileNo) {
		this.empMobileNo = empMobileNo;
	}

	public String[] getProbationLeaves() {
		return probationLeaves;
	}

	public void setProbationLeaves(String[] probationLeaves) {
		this.probationLeaves = probationLeaves;
	}

	public File[] getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(File[] idDoc) {
		this.idDoc = idDoc;
	}

	public String[] getIdDocName() {
		return idDocName;
	}

	public void setIdDocName(String[] idDocName) {
		this.idDocName = idDocName;
	}

	public String[] getHobbyName() {
		return hobbyName;
	}

	public void setHobbyName(String[] hobbyName) {
		this.hobbyName = hobbyName;
	}

	public String[] getDocumentNames() {
		return documentNames;
	}

	public void setDocumentNames(String[] documentNames) {
		this.documentNames = documentNames;
	}

	public String[] getDocumentValues() {
		return documentValues;
	}

	public void setDocumentValues(String[] documentValues) {
		this.documentValues = documentValues;
	}

	public String[] getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String[] languageName) {
		this.languageName = languageName;
	}

	public String[] getIsRead() {
		return isRead;
	}

	public void setIsRead(String[] isRead) {
		this.isRead = isRead;
	}

	public String[] getIsWrite() {
		return isWrite;
	}

	public void setIsWrite(String[] isWrite) {
		this.isWrite = isWrite;
	}

	public String[] getIsSpeak() {
		return isSpeak;
	}

	public void setIsSpeak(String[] isSpeak) {
		this.isSpeak = isSpeak;
	}

	public String[] getDegreeName() {
		return degreeName;
	}

	public void setDegreeName(String[] degreeName) {
		this.degreeName = degreeName;
	}

	public String[] getDegreeDuration() {
		return degreeDuration;
	}

	public void setDegreeDuration(String[] degreeDuration) {
		this.degreeDuration = degreeDuration;
	}

	public String[] getCompletionYear() {
		return completionYear;
	}

	public void setCompletionYear(String[] completionYear) {
		this.completionYear = completionYear;
	}

	public String[] getGrade() {
		return grade;
	}

	public void setGrade(String[] grade) {
		this.grade = grade;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getFatherDob() {
		return fatherDob;
	}

	public void setFatherDob(String fatherDob) {
		this.fatherDob = fatherDob;
	}

	public String getFatherEducation() {
		return fatherEducation;
	}

	public void setFatherEducation(String fatherEducation) {
		this.fatherEducation = fatherEducation;
	}

	public String getFatherOccupation() {
		return fatherOccupation;
	}

	public void setFatherOccupation(String fatherOccupation) {
		this.fatherOccupation = fatherOccupation;
	}

	public String getFatherContactNumber() {
		return fatherContactNumber;
	}

	public void setFatherContactNumber(String fatherContactNumber) {
		this.fatherContactNumber = fatherContactNumber;
	}

	public String getFatherEmailId() {
		return fatherEmailId;
	}

	public void setFatherEmailId(String fatherEmailId) {
		this.fatherEmailId = fatherEmailId;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getMotherDob() {
		return motherDob;
	}

	public void setMotherDob(String motherDob) {
		this.motherDob = motherDob;
	}

	public String getMotherEducation() {
		return motherEducation;
	}

	public void setMotherEducation(String motherEducation) {
		this.motherEducation = motherEducation;
	}

	public String getMotherOccupation() {
		return motherOccupation;
	}

	public void setMotherOccupation(String motherOccupation) {
		this.motherOccupation = motherOccupation;
	}

	public String getMotherContactNumber() {
		return motherContactNumber;
	}

	public void setMotherContactNumber(String motherContactNumber) {
		this.motherContactNumber = motherContactNumber;
	}

	public String getMotherEmailId() {
		return motherEmailId;
	}

	public void setMotherEmailId(String motherEmailId) {
		this.motherEmailId = motherEmailId;
	}

	public String[] getMemberName() {
		return memberName;
	}

	public void setMemberName(String[] memberName) {
		this.memberName = memberName;
	}

	public String[] getMemberDob() {
		return memberDob;
	}

	public void setMemberDob(String[] memberDob) {
		this.memberDob = memberDob;
	}

	public String[] getMemberEducation() {
		return memberEducation;
	}

	public void setMemberEducation(String[] memberEducation) {
		this.memberEducation = memberEducation;
	}

	public String[] getMemberOccupation() {
		return memberOccupation;
	}

	public void setMemberOccupation(String[] memberOccupation) {
		this.memberOccupation = memberOccupation;
	}

	public String[] getMemberContactNumber() {
		return memberContactNumber;
	}

	public void setMemberContactNumber(String[] memberContactNumber) {
		this.memberContactNumber = memberContactNumber;
	}

	public String[] getMemberEmailId() {
		return memberEmailId;
	}

	public void setMemberEmailId(String[] memberEmailId) {
		this.memberEmailId = memberEmailId;
	}

	public String[] getMemberGender() {
		return memberGender;
	}

	public void setMemberGender(String[] memberGender) {
		this.memberGender = memberGender;
	}

	public String[] getPrevCompanyName() {
		return prevCompanyName;
	}

	public void setPrevCompanyName(String[] prevCompanyName) {
		this.prevCompanyName = prevCompanyName;
	}

	public String[] getPrevCompanyLocation() {
		return prevCompanyLocation;
	}

	public void setPrevCompanyLocation(String[] prevCompanyLocation) {
		this.prevCompanyLocation = prevCompanyLocation;
	}

	public String[] getPrevCompanyCity() {
		return prevCompanyCity;
	}

	public void setPrevCompanyCity(String[] prevCompanyCity) {
		this.prevCompanyCity = prevCompanyCity;
	}

	public String[] getPrevCompanyState() {
		return prevCompanyState;
	}

	public void setPrevCompanyState(String[] prevCompanyState) {
		this.prevCompanyState = prevCompanyState;
	}

	public String[] getPrevCompanyCountry() {
		return prevCompanyCountry;
	}

	public void setPrevCompanyCountry(String[] prevCompanyCountry) {
		this.prevCompanyCountry = prevCompanyCountry;
	}

	public String[] getPrevCompanyContactNo() {
		return prevCompanyContactNo;
	}

	public void setPrevCompanyContactNo(String[] prevCompanyContactNo) {
		this.prevCompanyContactNo = prevCompanyContactNo;
	}

	public String[] getPrevCompanyReportingTo() {
		return prevCompanyReportingTo;
	}

	public void setPrevCompanyReportingTo(String[] prevCompanyReportingTo) {
		this.prevCompanyReportingTo = prevCompanyReportingTo;
	}

	public String[] getPrevCompanyFromDate() {
		return prevCompanyFromDate;
	}

	public void setPrevCompanyFromDate(String[] prevCompanyFromDate) {
		this.prevCompanyFromDate = prevCompanyFromDate;
	}

	public String[] getPrevCompanyToDate() {
		return prevCompanyToDate;
	}

	public void setPrevCompanyToDate(String[] prevCompanyToDate) {
		this.prevCompanyToDate = prevCompanyToDate;
	}

	public String[] getPrevCompanyDesination() {
		return prevCompanyDesination;
	}

	public void setPrevCompanyDesination(String[] prevCompanyDesination) {
		this.prevCompanyDesination = prevCompanyDesination;
	}

	public String[] getPrevCompanyResponsibilities() {
		return prevCompanyResponsibilities;
	}

	public void setPrevCompanyResponsibilities(String[] prevCompanyResponsibilities) {
		this.prevCompanyResponsibilities = prevCompanyResponsibilities;
	}

	public String[] getPrevCompanySkills() {
		return prevCompanySkills;
	}

	public void setPrevCompanySkills(String[] prevCompanySkills) {
		this.prevCompanySkills = prevCompanySkills;
	}

	public String[] getSkillName() {
		return skillName;
	}

	public void setSkillName(String[] skillName) {
		this.skillName = skillName;
	}

	public String[] getSkillValue() {
		return skillValue;
	}

	public void setSkillValue(String[] skillValue) {
		this.skillValue = skillValue;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getEmpEmergencyContactName() {
		return empEmergencyContactName;
	}

	public void setEmpEmergencyContactName(String empEmergencyContactName) {
		this.empEmergencyContactName = empEmergencyContactName;
	}

	public String getEmpEmergencyContactNo() {
		return empEmergencyContactNo;
	}

	public void setEmpEmergencyContactNo(String empEmergencyContactNo) {
		this.empEmergencyContactNo = empEmergencyContactNo;
	}

	public String getEmpPassportNo() {
		return empPassportNo;
	}

	public void setEmpPassportNo(String empPassportNo) {
		this.empPassportNo = empPassportNo;
	}

	public String getEmpPassportExpiryDate() {
		return empPassportExpiryDate;
	}

	public void setEmpPassportExpiryDate(String empPassportExpiryDate) {
		this.empPassportExpiryDate = empPassportExpiryDate;
	}

	public String getEmpBloodGroup() {
		return empBloodGroup;
	}

	public void setEmpBloodGroup(String empBloodGroup) {
		this.empBloodGroup = empBloodGroup;
	}

	public String getEmpMaritalStatus() {
		return empMaritalStatus;
	}

	public void setEmpMaritalStatus(String empMaritalStatus) {
		this.empMaritalStatus = empMaritalStatus;
	}

	public List<FillMaritalStatus> getMaritalStatusList() {
		return maritalStatusList;
	}

	public void setMaritalStatusList(List<FillMaritalStatus> maritalStatusList) {
		this.maritalStatusList = maritalStatusList;
	}

	public List<FillBloodGroup> getBloodGroupList() {
		return bloodGroupList;
	}

	public void setBloodGroupList(List<FillBloodGroup> bloodGroupList) {
		this.bloodGroupList = bloodGroupList;
	}

	public List<FillDegreeDuration> getDegreeDurationList() {
		return degreeDurationList;
	}

	public void setDegreeDurationList(List<FillDegreeDuration> degreeDurationList) {
		this.degreeDurationList = degreeDurationList;
	}

	public List<FillYears> getYearsList() {
		return yearsList;
	}

	public void setYearsList(List<FillYears> yearsList) {
		this.yearsList = yearsList;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	public String getSpouseDob() {
		return spouseDob;
	}

	public void setSpouseDob(String spouseDob) {
		this.spouseDob = spouseDob;
	}

	public String getSpouseEducation() {
		return spouseEducation;
	}

	public void setSpouseEducation(String spouseEducation) {
		this.spouseEducation = spouseEducation;
	}

	public String getSpouseContactNumber() {
		return spouseContactNumber;
	}

	public void setSpouseContactNumber(String spouseContactNumber) {
		this.spouseContactNumber = spouseContactNumber;
	}

	public String getSpouseEmailId() {
		return spouseEmailId;
	}

	public void setSpouseEmailId(String spouseEmailId) {
		this.spouseEmailId = spouseEmailId;
	}

	public String getSpouseOccupation() {
		return spouseOccupation;
	}

	public void setSpouseOccupation(String spouseOccupation) {
		this.spouseOccupation = spouseOccupation;
	}

	public String getSpouseGender() {
		return spouseGender;
	}

	public void setSpouseGender(String spouseGender) {
		this.spouseGender = spouseGender;
	}

	public String getQue1Desc() {
		return que1Desc;
	}

	public void setQue1Desc(String que1Desc) {
		this.que1Desc = que1Desc;
	}

	public String getQue2Desc() {
		return que2Desc;
	}

	public void setQue2Desc(String que2Desc) {
		this.que2Desc = que2Desc;
	}

	public String getQue3Desc() {
		return que3Desc;
	}

	public void setQue3Desc(String que3Desc) {
		this.que3Desc = que3Desc;
	}

	public String getQue4Desc() {
		return que4Desc;
	}

	public void setQue4Desc(String que4Desc) {
		this.que4Desc = que4Desc;
	}

	public String getQue5Desc() {
		return que5Desc;
	}

	public void setQue5Desc(String que5Desc) {
		this.que5Desc = que5Desc;
	}

	public String getQue1Id() {
		return que1Id;
	}

	public void setQue1Id(String que1Id) {
		this.que1Id = que1Id;
	}

	public String getQue2Id() {
		return que2Id;
	}

	public void setQue2Id(String que2Id) {
		this.que2Id = que2Id;
	}

	public String getQue3Id() {
		return que3Id;
	}

	public void setQue3Id(String que3Id) {
		this.que3Id = que3Id;
	}

	public String getQue4Id() {
		return que4Id;
	}

	public void setQue4Id(String que4Id) {
		this.que4Id = que4Id;
	}

	public String getQue5Id() {
		return que5Id;
	}

	public void setQue5Id(String que5Id) {
		this.que5Id = que5Id;
	}

	public boolean isCheckQue1() {
		return checkQue1;
	}

	public void setCheckQue1(boolean checkQue1) {
		this.checkQue1 = checkQue1;
	}

	public boolean isCheckQue2() {
		return checkQue2;
	}

	public void setCheckQue2(boolean checkQue2) {
		this.checkQue2 = checkQue2;
	}

	public boolean isCheckQue3() {
		return checkQue3;
	}

	public void setCheckQue3(boolean checkQue3) {
		this.checkQue3 = checkQue3;
	}

	public boolean isCheckQue4() {
		return checkQue4;
	}

	public void setCheckQue4(boolean checkQue4) {
		this.checkQue4 = checkQue4;
	}

	public boolean isCheckQue5() {
		return checkQue5;
	}

	public void setCheckQue5(boolean checkQue5) {
		this.checkQue5 = checkQue5;
	}

	public String getEmpMedicalId1() {
		return empMedicalId1;
	}

	public void setEmpMedicalId1(String empMedicalId1) {
		this.empMedicalId1 = empMedicalId1;
	}

	public String getEmpMedicalId2() {
		return empMedicalId2;
	}

	public void setEmpMedicalId2(String empMedicalId2) {
		this.empMedicalId2 = empMedicalId2;
	}

	public String getEmpMedicalId3() {
		return empMedicalId3;
	}

	public void setEmpMedicalId3(String empMedicalId3) {
		this.empMedicalId3 = empMedicalId3;
	}

	public String getEmpMedicalId4() {
		return empMedicalId4;
	}

	public void setEmpMedicalId4(String empMedicalId4) {
		this.empMedicalId4 = empMedicalId4;
	}

	public String getEmpMedicalId5() {
		return empMedicalId5;
	}

	public void setEmpMedicalId5(String empMedicalId5) {
		this.empMedicalId5 = empMedicalId5;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}

	/*public String getStrEdit() {
		return strEdit;
	}

	public void setStrEdit(String strEdit) {
		this.strEdit = strEdit;
	}*/

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getEmpCodeAlphabet() {
		return empCodeAlphabet;
	}

	public void setEmpCodeAlphabet(String empCodeAlphabet) {
		this.empCodeAlphabet = empCodeAlphabet;
	}

	public String getEmpCodeNumber() {
		return empCodeNumber;
	}

	public void setEmpCodeNumber(String empCodeNumber) {
		this.empCodeNumber = empCodeNumber;
	}

	public Boolean getAutoGenerate() {
		return autoGenerate;
	}

	public void setAutoGenerate(Boolean autoGenerate) {
		this.autoGenerate = autoGenerate;
	}
	public String getRef1Name() {
		return ref1Name;
	}
	public void setRef1Name(String ref1Name) {
		this.ref1Name = ref1Name;
	}
	public String getRef1Company() {
		return ref1Company;
	}
	public void setRef1Company(String ref1Company) {
		this.ref1Company = ref1Company;
	}
	public String getRef1Designation() {
		return ref1Designation;
	}
	public void setRef1Designation(String ref1Designation) {
		this.ref1Designation = ref1Designation;
	}
	public String getRef1ContactNo() {
		return ref1ContactNo;
	}
	public void setRef1ContactNo(String ref1ContactNo) {
		this.ref1ContactNo = ref1ContactNo;
	}
	public String getRef1Email() {
		return ref1Email;
	}
	public void setRef1Email(String ref1Email) {
		this.ref1Email = ref1Email;
	}
	public String getRef2Name() {
		return ref2Name;
	}
	public void setRef2Name(String ref2Name) {
		this.ref2Name = ref2Name;
	}
	public String getRef2Company() {
		return ref2Company;
	}
	public void setRef2Company(String ref2Company) {
		this.ref2Company = ref2Company;
	}
	public String getRef2Designation() {
		return ref2Designation;
	}
	public void setRef2Designation(String ref2Designation) {
		this.ref2Designation = ref2Designation;
	}
	public String getRef2ContactNo() {
		return ref2ContactNo;
	}
	public void setRef2ContactNo(String ref2ContactNo) {
		this.ref2ContactNo = ref2ContactNo;
	}
	public String getRef2Email() {
		return ref2Email;
	}
	public void setRef2Email(String ref2Email) {
		this.ref2Email = ref2Email;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return ServiceName;
	}
	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}
	public boolean isApprovedFlag() {
		return approvedFlag;
	}
	public void setApprovedFlag(boolean approvedFlag) {
		this.approvedFlag = approvedFlag;
	}
	public Timestamp getEmpFilledFlagDate() {
		return empFilledFlagDate;
	}
	public void setEmpFilledFlagDate(Timestamp empFilledFlagDate) {
		this.empFilledFlagDate = empFilledFlagDate;
	}
	public List<FillNoticeDuration> getNoticeDurationList() {
		return noticeDurationList;
	}
	public void setNoticeDurationList(List<FillNoticeDuration> noticeDurationList) {
		this.noticeDurationList = noticeDurationList;
	}
	public int getNoticeDuration() {
		return noticeDuration;
	}
	public void setNoticeDuration(int noticeDuration) {
		this.noticeDuration = noticeDuration;
	}
	public String[] getIdDocType() {
		return idDocType;
	}
	public void setIdDocType(String[] idDocType) {
		this.idDocType = idDocType;
	}
	public int[] getDocId() {
		return docId;
	}
	public void setDocId(int[] docId) {
		this.docId = docId;
	}
	public String getEmpPFNo() {
		return empPFNo;
	}
	public void setEmpPFNo(String empPFNo) {
		this.empPFNo = empPFNo;
	}
	public List<FillDesig> getDesigList() {
		return desigList;
	}
	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}
	public String getStrDesignation() {
		return strDesignation;
	}
	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}
	public String getAttendanceDependency() {
		return attendanceDependency;
	}
	public void setAttendanceDependency(String attendanceDependency) {
		this.attendanceDependency = attendanceDependency;
	}
	public String[] getIdDocFileName() {
		return idDocFileName;
	}
	public void setIdDocFileName(String[] idDocFileName) {
		this.idDocFileName = idDocFileName;
	}
	public String getEmpGPFNo() {
		return empGPFNo;
	}
	public void setEmpGPFNo(String empGPFNo) {
		this.empGPFNo = empGPFNo;
	}
	public String[] getStrTime() {
		return strTime;
	}
	public void setStrTime(String[] strTime) {
		this.strTime = strTime;
	}
	public String[] getStrDate() {
		return strDate;
	}
	public void setStrDate(String[] strDate) {
		this.strDate = strDate;
	}
	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}
	public void setPaycycleDurationList(List<FillPayCycleDuration> paycycleDurationList) {
		this.paycycleDurationList = paycycleDurationList;
	}
	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}
	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}
	public String getEmpDateOfMarriage() {
		return empDateOfMarriage;
	}
	public void setEmpDateOfMarriage(String empDateOfMarriage) {
		this.empDateOfMarriage = empDateOfMarriage;
	}
	public String[] getEmpKra() {
		return empKra;
	}
	public void setEmpKra(String[] empKra) {
		this.empKra = empKra;
	}
	public String[] getEmpKraId() {
		return empKraId;
	}
	public void setEmpKraId(String[] empKraId) {
		this.empKraId = empKraId;
	}
	public String getEmpAddress1Tmp() {
		return empAddress1Tmp;
	}
	public void setEmpAddress1Tmp(String empAddress1Tmp) {
		this.empAddress1Tmp = empAddress1Tmp;
	}
	public String getEmpAddress2Tmp() {
		return empAddress2Tmp;
	}
	public void setEmpAddress2Tmp(String empAddress2Tmp) {
		this.empAddress2Tmp = empAddress2Tmp;
	}
	public String getCountryTmp() {
		return countryTmp;
	}
	public void setCountryTmp(String countryTmp) {
		this.countryTmp = countryTmp;
	}
	public String getStateTmp() {
		return stateTmp;
	}
	public void setStateTmp(String stateTmp) {
		this.stateTmp = stateTmp;
	}
	public String getCityTmp() {
		return cityTmp;
	}
	public void setCityTmp(String cityTmp) {
		this.cityTmp = cityTmp;
	}
	public String getEmpPincodeTmp() {
		return empPincodeTmp;
	}
	public void setEmpPincodeTmp(String empPincodeTmp) {
		this.empPincodeTmp = empPincodeTmp;
	}
	public List<FillBank> getBankList() {
		return bankList;
	}
	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}



	public String getEmpPaymentMode() {
		return empPaymentMode;
	}



	public void setEmpPaymentMode(String empPaymentMode) {
		this.empPaymentMode = empPaymentMode;
	}



	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}



	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}



	public List<FillLevel> getLevelList() {
		return levelList;
	}



	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}



	public String getStrLevel() {
		return strLevel;
	}



	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	

	//for record particular job id
	String recruitId;
	public String getRecruitId() {
		return recruitId;
	}



	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}
	
  String jobcode;

public String getJobcode() {
	return jobcode;
}



public void setJobcode(String jobcode) {
	this.jobcode = jobcode;
}

String strwLocation;

public String getStrwLocation() {
	return strwLocation;
}


public void setStrwLocation(String strwLocation) {
	this.strwLocation = strwLocation;
}




public String getStepSubmit() {
	return stepSubmit;
}




public String getType() {
	return type;
}




public void setType(String type) {
	this.type = type;
}




public void setStepSubmit(String stepSubmit) {
	this.stepSubmit = stepSubmit;
}

}
