package com.konnect.jpms.training;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.AddCandidate;
import com.konnect.jpms.recruitment.FillEducational;
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
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMaritalStatus;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTrainerNew extends ActionSupport implements ServletRequestAware, IStatements {

			private static final long serialVersionUID = 1L;
			HttpSession session;
			String strUserType = null;
			String strSessionEmpId = null;

			private String mode;
			
			private String operation;
			private Boolean autoGenerate = false;
			private String step;
			private String serviceId;
			private String ServiceName;
			private String org_id;
			private String empId;
			private String trainerType;
			
			private StringBuilder sbServicesLink = new StringBuilder();
			CommonFunctions CF = null;

			public static String MOTHER = "MOTHER";
			public static String FATHER = "FATHER";
			public static String SPOUSE = "SPOUSE";
			public static String SIBLING = "SIBLING";
			public String dobYear="1977";
			
			private String empUserTypeId;
			// private String empPerId;
			// private String empId;
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
			private String strwLocation;
			
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

			private String[] empKra;
			private String[] empKraId;

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

			private List<File> idDoc;
			private List<String> idDocFileName;
			private List<String> idDocStatus;
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
			private List<File> que1DescFile;
			private List<String> que1IdFileStatus;
			private List<String> que1DescFileFileName;

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
			private String redirectUrl;

			private List<FillEmploymentType> empTypeList;
			private List<FillGrade> gradeList;
			private List<FillDesig> desigList;
			private List<FillLevel> levelList;
			private List<FillDepartment> deptList;
			private List<FillEmployee> supervisorList;
			private List<FillServices> serviceList;
			private List<FillCountry> countryList;
			private List<FillState> stateList;
			private List<FillCity> cityList;
			private List<FillWLocation> wLocationList;
			private List<FillGender> empGenderList;
			private List<FillMaritalStatus> maritalStatusList;
			private List<FillBloodGroup> bloodGroupList;
			private List<FillDegreeDuration> degreeDurationList;
			private List<FillYears> yearsList;
			private List<FillSkills> skillsList;
			private List<FillEducational> educationalList;

			private static Logger log = Logger.getLogger(AddCandidate.class);

			public String execute() {

				UtilityFunctions uF = new UtilityFunctions();
				session = request.getSession();
				CF = (CommonFunctions) session.getAttribute(CommonFunctions);
				if(CF==null) return LOGIN;

				if (getOrg_id() == null) {
					setOrg_id((String) session.getAttribute(ORGID));
				}
				
				strUserType = (String) session.getAttribute(USERTYPE);
				strSessionEmpId = (String) session.getAttribute(EMPID);
				request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
				request.setAttribute("dobYear",dobYear);

				String title = TAddTrainerDetail;
				if (getOperation()!=null && getOperation().equals("U") && uF.parseToInt(getEmpId())!=0 ) {
					title = TEditTrainerDetail;
				}
				/*request.setAttribute(PAGE, "/jsp/training/AddTrainer.jsp");
				request.setAttribute(TITLE, title);*/
				
//				System.out.println("getOperation=="+getOperation()+"---getMode=="+getMode()+"---getStep=="+getStep());
				getTrainerName();

				if (getOperation() != null && getOperation().equals("U") && uF.parseToInt(getEmpId()) != 0 && getMode() != null) { 

					request.setAttribute(TITLE, TEditTrainerDetail);
					if(uF.parseToInt(getStep()) == 7){
						if(getType() == null || !getType().equals("view"))
						updateEmployee();
						return "finish";
					}else {
						
						if(getType() == null || !getType().equals("view"))
							
							updateEmployee();
							viewEmployee(uF);
							loadValidateEmployee();
							return LOAD;
						
					}

				} else if (getOperation() != null && operation.equals("D")) {
					deleteEmployee(uF.parseToInt(getEmpId()));
					return "deleted";

				} else if(getOperation() != null && operation.equals("E")) {
					
					viewEmployee(uF);
					loadValidateEmployee();
					return LOAD;
				}

				if (uF.parseToInt(getStep()) != 0) {
					if (getOperation() == null || !getOperation().equals("U")) {
						insertTrainer();
					}
					loadValidateEmployee();
					if (uF.parseToInt(getStep()) == 1) {
						setStep("2");
					} else if (uF.parseToInt(getStep()) == 2) {
						setStep("3");
					} else if (uF.parseToInt(getStep()) == 3) {
						setStep("4");
					} else if (uF.parseToInt(getStep()) == 4) {
						setStep("5");
					} else if (uF.parseToInt(getStep()) == 5) {
						setStep("6");
					} else if (uF.parseToInt(getStep()) == 6) {
						setStep("7");
					} else if (uF.parseToInt(getStep()) == 7) {
						return "finish";
					} 
//					
				} else if (uF.parseToInt(getStep()) == 0) {
					log.debug("Default Step..");
					setStep("1");
					loadValidateEmployee();
				}

				return LOAD;

			}

			public void updateEmpFilledStatus(int nEmpId) {

				Connection con = null;
				PreparedStatement pst = null;
				Database db = new Database();
				db.setRequest(request);

				try {

					con = db.makeConnection(con);
					pst = con.prepareStatement("UPDATE candidate_personal_details SET emp_filled_flag = ?, emp_filled_flag_date = ? " + "WHERE emp_per_id = ?");
					pst.setBoolean(1, true);

					java.util.Date date = new java.util.Date();
					log.debug(new Timestamp(date.getTime()));
					pst.setTimestamp(2, new Timestamp(date.getTime()));
					pst.setInt(3, nEmpId);
//					log.debug("pst===>" + pst);
					pst.execute();
					pst.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			}


			public void updateEmpLiveStatus(int nEmpId) {

				Connection con = null;
				PreparedStatement pst = null;
				Database db = new Database();
				db.setRequest(request);

				try {
					con = db.makeConnection(con);
					pst = con.prepareStatement("UPDATE candidate_personal_details SET is_alive = ?, approved_flag = ? " + "WHERE emp_per_id = ?");
					pst.setBoolean(1, true);
					pst.setBoolean(2, true);
					pst.setInt(3, nEmpId);
//					log.debug("pst===>" + pst);
					pst.execute();
					pst.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			}

			
			public String loadValidateEmployee() {

				try {

					UtilityFunctions uF = new UtilityFunctions();

					wLocationList = new FillWLocation(request).fillWLocation();
					gradeList = new FillGrade(request).fillGrade();
					desigList = new FillDesig(request).fillDesig();
					levelList = new FillLevel(request).fillLevel();
					deptList = new FillDepartment(request).fillDepartment();
					// supervisorList = new FillEmployee().fillEmployeeCode(strUserType,
					// strSessionEmpId);
					supervisorList = new FillEmployee(request).fillSupervisorNameCode(strUserType, strSessionEmpId);
					serviceList = new FillServices(request).fillServices();
					countryList = new FillCountry(request).fillCountry();
					stateList = new FillState(request).fillState();

					empTypeList = new FillEmploymentType().fillEmploymentType(request);
					empGenderList = new FillGender().fillGender();
					maritalStatusList = new FillMaritalStatus().fillMaritalStatus();
					bloodGroupList = new FillBloodGroup().fillBloodGroup();
					degreeDurationList = new FillDegreeDuration().fillDegreeDuration();
					yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
					skillsList = new FillSkills(request).fillSkillsWithId();
					educationalList = new FillEducational(request).fillEducationalQual();

					request.setAttribute("educationalList", educationalList);
					request.setAttribute("yearsList", yearsList);
					request.setAttribute("degreeDurationList", degreeDurationList);
					request.setAttribute("empGenderList", empGenderList);
					request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
					StringBuilder sbdegreeDuration = new StringBuilder();
					StringBuilder sbPrevEmployment = new StringBuilder();
					StringBuilder sbSibling = new StringBuilder();
					StringBuilder sbSkills = new StringBuilder();

					sbdegreeDuration.append("<option value=''> Degree</option>");

					for (int i = 0; i < educationalList.size(); i++) {

						sbdegreeDuration.append("<option value=" + ((FillEducational) educationalList.get(i)).getEduId()+ "> " + ((FillEducational) educationalList.get(i)).getEduName()
								+ "</option>");

					}
					sbdegreeDuration.append("<option value=other>Other</option>");
					sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name= degreeDuration style=width:90px; class=form-control >" + "<option value=''>Duration</option>");

					for (int i = 0; i < degreeDurationList.size(); i++) {

						sbdegreeDuration.append("<option value=" + ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationID() + "> "
								+ ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationName() + "</option>");

					}

					sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name=completionYear style=width:110px; class=form-control  >" + "<option value=''>Completion Year</option>");

					for (int i = 0; i < yearsList.size(); i++) {

						sbdegreeDuration.append("<option value=" + ((FillYears) yearsList.get(i)).getYearsID() + "> " + ((FillYears) yearsList.get(i)).getYearsName() + "</option>");

					}

					sbdegreeDuration.append("</select>" + "</td>" + "<td><input type= text  style=width:88px; name=grade  class=form-control ></input></td>"
							+ "<td><a href=javascript:void(0) onclick=addEducation() class=add-font ></a>");

					request.setAttribute("sbdegreeDuration", sbdegreeDuration.toString());

					sbPrevEmployment.append("<table class=table style=width:auto;>" + "<tr><td class=txtlabel style=text-align:right> Company Name:</td>"
							+ "<td><input type=text name=prevCompanyName style=width:180px; name=prevCompanyLocation class=form-control ></input></td>" + "</tr>"
							+ "<tr><td class=txtlabel style=text-align:right> Location:</td>" + "<td> <input type=text class=form-control  style=width:180px; name=prevCompanyLocation ></input></td>"
							+ "</tr>" + "<tr><td class=txtlabel style=text-align:right> City: </td>" + "<td><input type=text class=form-control  style=width:180px; name=prevCompanyCity ></input></td>"
							+ "</tr>" + "<tr><td class=txtlabel style=text-align:right> State:</td><td><input type=text class=form-control  style=width:180px; name=prevCompanyState ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right> Country:</td><td><input type=text class=form-control  style=width:180px; name=prevCompanyCountry ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right> Contact Number:</td><td><input class= form-control type=number style=width:180px; name=prevCompanyContactNo ></input>"
							+ "</td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right> Reporting To:</td><td> <input type=text class=form-control  style=width:180px; name=prevCompanyReportingTo ></input>"
							+ "</td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right> From:</td><td> <input type=text style=width:180px; class=form-control  name=prevCompanyFromDate ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right> To:</td><td> <input type=text style=width:180px; class=form-control  name=prevCompanyToDate ></input></td></tr> "
							+ "<tr><td class=txtlabel style=text-align:right> Designation:</td><td> <input type=text style=width:180px; class=form-control  name=prevCompanyDesination ></input>"
							+ "</td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right> Responsibility:</td><td> <input type=text style=width:180px; class=form-control  name=prevCompanyResponsibilities >"
							+ "</input>" + "</td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right> Skills: </td><td> <input type=text style=width:180px; class=form-control  name=prevCompanySkills ></input></td></tr>"
							//+ "<tr><td class=txtlabel style=text-align:right> <a href=javascript:void(0) onclick=addPrevEmployment() class=add>Add more information..</a></td>"
					);

					request.setAttribute("sbPrevEmployment", sbPrevEmployment.toString());

					sbSibling.append("<table class=table style=width: 30%;>" + "<tr><td style=text-align:center class=tdLabelheadingBg style=text-align:right colspan=2>Sibling's Information </td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right>Name:</td><td><input type=text style=width:180px; name=memberName class=form-control ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right>Date of birth:</td><td> <input type=text style=width:180px; name=memberDob  class=form-control ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right>Education:</td><td> <input type=text style=width:180px; name=memberEducation  class=form-control ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right>Occupation:</td><td> <input type=text style=width:180px; name=memberOccupation  class=form-control ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right>Contact Number:</td><td><input type=number style=width:180px;  name=memberContactNumber  class=form-control ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right>Email Id:</td><td><input type=email style=width:180px; name=memberEmailId  class=form-control ></input></td></tr>"
							+ "<tr><td class=txtlabel style=text-align:right>Gender:</td><td>" + "<select name= memberGender style=width:180px; class=form-control >"
							+"<option>Select Gender</option>");

					for (int i = 0; i < empGenderList.size(); i++) {

						sbSibling.append("<option value=" + ((FillGender) empGenderList.get(i)).getGenderId() + "> " + ((FillGender) empGenderList.get(i)).getGenderName() + "</option>");

					}

					sbSibling.append("</select>" + "</td></tr>"+ "");

					request.setAttribute("sbSibling", sbSibling.toString());

					sbSkills.append(" <table class=table><tr><td>" + "<select name=skillName class=form-control >" + "<option value=>Select Skill Name</option>");

					for (int k = 0; k < skillsList.size(); k++) {

						sbSkills.append("<option value=" + ((FillSkills) skillsList.get(k)).getSkillsId() + "> " + ((FillSkills) skillsList.get(k)).getSkillsName() + "</option>");
					}

					sbSkills.append("</select></td><td>" + "<select name=skillValue style=width:105px; class=form-control >" + "<option value=>Skill Rating</option>");

					for (int k = 1; k < 11; k++) {
						sbSkills.append("<option value=" + k + ">" + k + "</option>");
					}

					sbSkills.append("</select></td>");
//					log.debug("sbSkills==>" + sbSkills);
					request.setAttribute("sbSkills", sbSkills);

				} catch (Exception ex) {
					ex.printStackTrace();
				}

				return LOAD;

			}


			public String viewEmployee(UtilityFunctions uF) {
				
				
					if (uF.parseToInt(getStep()) == 0) {
						setStep("1");
					} else if (uF.parseToInt(getStep()) == 1) {
						setStep("2");
					} else if (uF.parseToInt(getStep()) == 2) {
						setStep("3");
					} else if (uF.parseToInt(getStep()) == 3) {
						setStep("4");
					} else if (uF.parseToInt(getStep()) == 4) {
						setStep("5");
					} else if (uF.parseToInt(getStep()) == 5) {
						setStep("6");
					} else if (uF.parseToInt(getStep()) == 6) {
						setStep("7");
					} 					

				Connection con = null;
				Database db = new Database();
				db.setRequest(request);

				List<List<String>> alSkills = new ArrayList<List<String>>();
				List<List<String>> alHobbies;
				List<List<String>> alLanguages;
				List<List<String>> alEducation;
				List<List<Object>> alDocuments;
				List<List<String>> alPrevEmployment;

				try {

					con = db.makeConnection(con);
					setTrainerPersonalDetails(con, uF);
					setTrainerReferences(con, uF);
					
					alSkills = selectSkills(con, uF.parseToInt(getEmpId()));
					alHobbies = selectHobbies(con, uF.parseToInt(getEmpId()));
					alLanguages = selectLanguages(con,uF.parseToInt(getEmpId()));
					alEducation = selectEducation(con, uF.parseToInt(getEmpId()));

					String filePath = request.getRealPath("/userDocuments/");

					alDocuments = selectTrainerDocuments(con, uF.parseToInt(getEmpId()), filePath);
					setTrainerFamilyMembers(con, uF);
					alPrevEmployment = selectPrevEmploment(con, uF.parseToInt(getEmpId()));
					setTrainerMedicalInfo(con, uF);

					// INTERVIEW SCHEDULE

					wLocationList = new FillWLocation(request).fillWLocation();
					gradeList = new FillGrade(request).fillGrade();
					deptList = new FillDepartment(request).fillDepartment();
					supervisorList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
					serviceList = new FillServices(request).fillServices();
					countryList = new FillCountry(request).fillCountry();
					stateList = new FillState(request).fillState();

					empTypeList = new FillEmploymentType().fillEmploymentType(request);
					request.setAttribute("alSkills", alSkills);
					
					request.setAttribute("alHobbies", alHobbies);
					request.setAttribute("alLanguages", alLanguages);
					request.setAttribute("alEducation", alEducation);
					request.setAttribute("alDocuments", alDocuments);
					request.setAttribute("alPrevEmployment", alPrevEmployment);

					

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.closeConnection(con);
				}

				return SUCCESS;

			}
			
			private void getTrainerName(){
				Connection con = null;
				PreparedStatement pst = null;
				ResultSet rs = null;
				Database db = new Database();
				db.setRequest(request);
				UtilityFunctions uF = new UtilityFunctions();
				try {
					con = db.makeConnection(con);
					String trainerName = "";
					pst = con.prepareStatement("select trainer_fname,trainer_lname from trainer_personal_details where trainer_id=?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					rs = pst.executeQuery();
//					System.out.println("pst Candi name = "+pst);
					while(rs.next()){
						trainerName = rs.getString("trainer_fname") + " " + rs.getString("trainer_lname");
					}
					rs.close();
					pst.close();
					
					request.setAttribute("strEmpName", trainerName);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			}

			// Interview Schedule


			private void setTrainerReferences(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;
				ResultSet rs = null;

				try {

					pst = con.prepareStatement("SELECT * FROM trainer_references WHERE trainer_id = ? order by ref_name");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					rs = pst.executeQuery();

					if (rs.next()) {
						setRef1Name(rs.getString("ref_name"));
						setRef1Company(rs.getString("ref_company"));
						setRef1Designation(rs.getString("ref_designation"));
						setRef1ContactNo(rs.getString("ref_contact_no"));
						setRef1Email(rs.getString("ref_email_id"));
					}

					if (rs.next()) {
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
				}
			}

			private void setTrainerMedicalInfo(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;
				ResultSet rs = null;

				try {

					pst = con.prepareStatement("SELECT * FROM trainer_medical_details WHERE trainer_id = ? order by question_id");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					rs = pst.executeQuery();

					while (rs.next()) {

						if (rs.getInt("question_id") == 1) {
							setEmpMedicalId1(rs.getString("medical_id"));
							setCheckQue1(uF.parseToBoolean(rs.getString("yes_no")));
							setQue1Desc(uF.showData(rs.getString("description"), ""));

						} else if (rs.getInt("question_id") == 2) {
							setEmpMedicalId2(rs.getString("medical_id"));
							setCheckQue2(uF.parseToBoolean(rs.getString("yes_no")));
							setQue2Desc(uF.showData(rs.getString("description"), ""));

						} else if (rs.getInt("question_id") == 3) {
							setEmpMedicalId3(rs.getString("medical_id"));
							setCheckQue3(uF.parseToBoolean(rs.getString("yes_no")));
							setQue3Desc(uF.showData(rs.getString("description"), ""));

						} else if (rs.getInt("question_id") == 4) {
							setEmpMedicalId4(rs.getString("medical_id"));
							setCheckQue4(uF.parseToBoolean(rs.getString("yes_no")));
							setQue4Desc(uF.showData(rs.getString("description"), ""));

						} else if (rs.getInt("question_id") == 5) {
							setEmpMedicalId5(rs.getString("medical_id"));
							setCheckQue5(uF.parseToBoolean(rs.getString("yes_no")));
							setQue5Desc(uF.showData(rs.getString("description"), ""));
						}

					}
					rs.close();
					pst.close();

				} catch (Exception e) {

					e.printStackTrace();
				}
			}

			

			private int setTrainerPersonalDetails(Connection con, UtilityFunctions uF) {
			//	System.out.println("inside setTrainerPersonalDetails ");
				PreparedStatement pst = null;
				ResultSet rs = null;
				int nEmpOffId = 0;

				try {

					pst = con.prepareStatement("SELECT * FROM trainer_personal_details WHERE trainer_id = ?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
//					log.debug("pst selectEmployeeR1V==>" + pst);
					rs = pst.executeQuery();
					if (rs.next()) {
						
//						nEmpOffId = rs.getInt("emp_off_id");
						setEmpId(rs.getString("trainer_id"));
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
						
						dobYear = uF.getDateFormat(rs.getString("trainer_date_of_birth"), DBDATE, "yyyy");
						//System.out.println("java dobYear==>"+dobYear);
						request.setAttribute("dobYear",dobYear);
						
						log.debug("rs.getString(trainer_image)=="+rs.getString("trainer_image"));
						request.setAttribute("strImage", rs.getString("trainer_image"));
						
//						request.setAttribute("strEmpName", uF.showData(rs.getString("trainer_fname"),"") +" "+uF.showData(rs.getString("trainer_lname"),"") );
						
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("SELECT * FROM training_trainer WHERE trainer_emp_id = ?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
				//	log.debug("pst selectEmployeeR1V==>"+pst);
//					System.out.println("pst selectEmployeeR1V==>"+pst);
					rs = pst.executeQuery();
					if (rs.next()) {				
						setStrwLocation(rs.getString("trainer_work_location"));
					}
					rs.close();
					pst.close();

				} catch (Exception e) {

					e.printStackTrace();
				}

				return nEmpOffId;

			}

			public void setTrainerFamilyMembers(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;
				ResultSet rs = null;
				ArrayList<ArrayList<String>> alSiblings = new ArrayList<ArrayList<String>>();

				try {

					pst = con.prepareStatement("SELECT * FROM trainer_family_members WHERE emp_id = ?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
//					log.debug("pst==>" + pst);
					rs = pst.executeQuery();

					while (rs.next()) {

//						log.debug("rs.getString(member_name)==>" + rs.getString("member_name"));

						if (rs.getString("member_type").equals(MOTHER)) {
														
							setMotherName(rs.getString("member_name"));
							setMotherDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
							setMotherEducation(rs.getString("member_education"));
							setMotherOccupation(rs.getString("member_occupation"));
							setMotherContactNumber(rs.getString("member_contact_no"));
							setMotherEmailId(rs.getString("member_email_id"));
						}

						if (rs.getString("member_type").equals(FATHER)) {

							setFatherName(rs.getString("member_name"));
							setFatherDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
							setFatherEducation(rs.getString("member_education"));
							setFatherOccupation(rs.getString("member_occupation"));
							setFatherContactNumber(rs.getString("member_contact_no"));
							setFatherEmailId(rs.getString("member_email_id"));

						}

						if (rs.getString("member_type").equals(SPOUSE)) {

							setSpouseName(rs.getString("member_name"));
							setSpouseDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
							setSpouseEducation(rs.getString("member_education"));
							setSpouseOccupation(rs.getString("member_occupation"));
							setSpouseContactNumber(rs.getString("member_contact_no"));
							setSpouseEmailId(rs.getString("member_email_id"));
							setSpouseGender(rs.getString("member_gender"));

						}

						if (rs.getString("member_type").equals(SIBLING)) {

							ArrayList<String> alInner = new ArrayList<String>();
							alInner.add(rs.getString("member_id"));
							alInner.add(rs.getString("member_name"));
							alInner.add(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
							alInner.add(rs.getString("member_education"));
							alInner.add(rs.getString("member_occupation"));
							alInner.add(rs.getString("member_contact_no"));
							alInner.add(rs.getString("member_email_id"));
							alInner.add(rs.getString("member_gender"));
							alSiblings.add(alInner);
						}

					}
					rs.close();
					pst.close();
					
					request.setAttribute("alSiblings", alSiblings);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			public String updateEmployee() {

				Connection con = null;
				Database db = new Database();
				db.setRequest(request);
				UtilityFunctions uF = new UtilityFunctions();

				try {

					con = db.makeConnection(con);

					if (uF.parseToInt(getStep()) == 1) {
						if (getEmpImage() != null)
							uploadImage(uF.parseToInt(getEmpId()));

						if (getEmpFname() != null) {
							updateTrainerPersonalDetails(con, uF);
						}
					} else if (uF.parseToInt(getStep()) == 5) {

						updateTrainerReferences(con, uF);

					}

					if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(MANAGER))) {
						if (uF.parseToInt(getStep()) == 2) {
							updateSkills(con, uF);
							updateHobbies(con, uF);
							updateEmpEducation(con, uF);
							updateEmpLangues(con, uF);

						} else if (uF.parseToInt(getStep()) == 3) {
							updateTrainerFamilyMembers(con, uF);

						} else if (uF.parseToInt(getStep()) == 4) {
							updateTrainerPrevEmploment(con, uF);

						} else if (uF.parseToInt(getStep()) == 6) {
							updateTrainerMedicalInfo(con, uF);

						} else if (uF.parseToInt(getStep()) == 7) {
							updateDocuments(con, uF);
						}

					}
					
					Map<String, String> hmTrainerName = CF.getTrainerNameMap(con);
					session.setAttribute(MESSAGE, SUCCESSM+"You have updated trainer "+hmTrainerName.get(getEmpId())+"'s details successfully."+END);

				} catch (Exception e) {
					e.printStackTrace();
					request.setAttribute(MESSAGE, "Error in updation");

				} finally {
					
					db.closeConnection(con);
				}

				return SUCCESS;

			}

			
			private void updateTrainerReferences(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;

				try {
					
					pst = con.prepareStatement("DELETE from trainer_references where trainer_id =?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
//					log.debug("pst deleteSkills=>" + pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("INSERT INTO trainer_references (ref_name, ref_company, ref_designation, ref_contact_no, " +
							"ref_email_id, trainer_id) values(?,?,?,?,?,?)");
					pst.setString(1, getRef1Name());
					pst.setString(2, getRef1Company());
					pst.setString(3, getRef1Designation());
					pst.setString(4, getRef1ContactNo());
					pst.setString(5, getRef1Email());
					pst.setInt(6, uF.parseToInt(getEmpId()));
//					log.debug("pst==>" + pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("INSERT INTO trainer_references (ref_name, ref_company, ref_designation, ref_contact_no, " +
							"ref_email_id, trainer_id) values(?,?,?,?,?,?)");
					pst.setString(1, getRef2Name());
					pst.setString(2, getRef2Company());
					pst.setString(3, getRef2Designation());
					pst.setString(4, getRef2ContactNo());
					pst.setString(5, getRef2Email());
					pst.setInt(6, uF.parseToInt(getEmpId()));
//					log.debug("pst==>" + pst);
					pst.execute();
					pst.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			private void updateTrainerMedicalInfo(Connection con, UtilityFunctions uF) {
				
				
				try {
					File file1=null;
					String filename1=null;
					int fileSize=1;
					if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(0).equals("1")){
						 file1=getQue1DescFile().get(fileSize-1);
						 filename1=getQue1DescFileFileName().get(fileSize-1);
						 fileSize++;
					}
					
					if(uF.parseToInt(getEmpMedicalId1())!=0)
					updateEmpMedicalInfo1(con,uF,getEmpMedicalId1(),isCheckQue1(),getQue1Desc(),file1,filename1);
					else
						insertEmpMedicalInfo1(con,uF,getQue1Id(),isCheckQue1(),getQue1Desc(),file1,filename1);
					
					
					File file2=null;
					String filename2=null;
					if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(1).equals("1")){
						 file2=getQue1DescFile().get(fileSize-1);
						 filename2=getQue1DescFileFileName().get(fileSize-1);
						 fileSize++;
					}
					
					if(uF.parseToInt(getEmpMedicalId2())!=0)
						updateEmpMedicalInfo1(con,uF,getEmpMedicalId2(),isCheckQue2(),getQue2Desc(),file2,filename2);
						else
							insertEmpMedicalInfo1(con,uF,getQue2Id(),isCheckQue2(),getQue2Desc(),file2,filename2);
					
					File file3=null;
					String filename3=null;
					if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(2).equals("1")){
						 file3=getQue1DescFile().get(fileSize-1);
						 filename3=getQue1DescFileFileName().get(fileSize-1);
						 fileSize++;
					}
					
					if(uF.parseToInt(getEmpMedicalId3())!=0)
						updateEmpMedicalInfo1(con,uF,getEmpMedicalId3(),isCheckQue3(),getQue3Desc(),file3,filename3);
						else
							insertEmpMedicalInfo1(con,uF,getQue3Id(),isCheckQue3(),getQue3Desc(),file3,filename3);
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}

			
			public void updateTrainerPersonalDetails(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;

				try {
					
					pst=con.prepareStatement("update training_trainer set trainer_name=?,trainer_mobile=?,trainer_email=?, " +
							" trainer_address=?,trainer_city=?,trainer_state=?,trainer_country=? where trainer_emp_id=? "); //,trainer_work_location=?
					
					pst.setString(1, getEmpFname()+" "+getEmpLname());
					pst.setString(2, getEmpMobileNo());
					pst.setString(3, getEmpEmail());
					pst.setString(4, getEmpAddress1());
					pst.setString(5, getCity());
					pst.setInt(6, uF.parseToInt(getState()));
					pst.setInt(7, uF.parseToInt(getCountry()));
					/*pst.setInt(8, uF.parseToInt(getStrwLocation()));*/
					pst.setInt(8, uF.parseToInt(getEmpId()));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("update trainer_personal_details set trainer_fname=?, trainer_lname=?, trainer_email=?, " +
							"trainer_address1=?, trainer_address2=?, trainer_city_id=?, trainer_state_id=?, trainer_country_id=?, trainer_pincode=?, " +
							" trainer_address1_tmp=?, trainer_address2_tmp=?, trainer_city_id_tmp=?, trainer_state_id_tmp=?, trainer_country_id_tmp=?," +
							" trainer_pincode_tmp=?, trainer_contactno=?, joining_date=?, trainer_pan_no=?,trainer_pf_no=?,trainer_gpf_no=?, trainer_gender=?," +
							" trainer_date_of_birth=?, trainer_bank_name=?, trainer_bank_acct_nbr=?, trainer_email_sec=?, skype_id=?, trainer_contactno_mob=?, " +
							" emergency_contact_name=?, emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?," +
							" trainer_date_of_marriage=?, approved_flag=?, trainer_entry_date=? where trainer_id=? ");
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
					pst.setInt(37, uF.parseToInt(getEmpId()));
//					System.out.println("pst====>"+pst);
					pst.execute();
					pst.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			public void updateTrainerPrevEmploment(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;

				try {

					pst = con.prepareStatement("DELETE FROM trainer_prev_employment WHERE trainer_id = ?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
//					System.out.println("delete step 4 pst====>"+pst);
//					log.debug("pst=>" + pst);
					pst.execute();
					pst.close();
					
					if (getPrevCompanyName() != null && getPrevCompanyName().length > 0) {
						for (int i = 0; i < getPrevCompanyName().length; i++) {

							if (getPrevCompanyName()[i].length() != 0) {

								pst = con.prepareStatement("INSERT INTO trainer_prev_employment(company_name, company_location, company_city, company_state, "
										+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, trainer_id)"
										+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
								pst.setInt(13, uF.parseToInt(getEmpId()));
//								log.debug("pst=>" + pst);
//								System.out.println("pst====>"+pst);
								pst.execute();
								pst.close();
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			public void updateTrainerFamilyMembers(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;
				int updateCnt = 0;
				try {

					pst = con.prepareStatement("UPDATE trainer_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
							+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");

					pst.setString(1, getMotherName());
					pst.setDate(2, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
					pst.setString(3, getMotherEducation());
					pst.setString(4, getMotherOccupation());
					pst.setString(5, getMotherContactNumber());
					pst.setString(6, getMotherEmailId());
					pst.setString(7, "F");
					pst.setInt(8, uF.parseToInt(getEmpId()));
					pst.setString(9, MOTHER);
//					log.debug("pst=>" + pst);
					updateCnt = pst.executeUpdate();
					pst.close();

					if (updateCnt == 0) {
						pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, "
								+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

						pst.setString(1, MOTHER);
						pst.setString(2, getMotherName());
//						log.debug(getMotherDob() + "getMotherDob()");
						pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
						pst.setString(4, getMotherEducation());
						pst.setString(5, getMotherOccupation());
						pst.setString(6, getMotherContactNumber());
						pst.setString(7, getMotherEmailId());
						pst.setString(8, "F");
						pst.setInt(9, uF.parseToInt(getEmpId()));
//						log.debug("pst=>" + pst);
						pst.execute();
						pst.close();
						
					}

					pst = con.prepareStatement("UPDATE trainer_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
							+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");

					pst.setString(1, getFatherName());
					pst.setDate(2, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
					pst.setString(3, getFatherEducation());
					pst.setString(4, getFatherOccupation());
					pst.setString(5, getFatherContactNumber());
					pst.setString(6, getFatherEmailId());
					pst.setString(7, "F");
					pst.setInt(8, uF.parseToInt(getEmpId()));
					pst.setString(9, FATHER);
//					log.debug("pst=>" + pst);
					updateCnt = pst.executeUpdate();
					pst.close();
					
					if (updateCnt == 0) {

						pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, "
								+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

						pst.setString(1, FATHER);
						pst.setString(2, getFatherName());
						pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
						pst.setString(4, getFatherEducation());
						pst.setString(5, getFatherOccupation());
						pst.setString(6, getFatherContactNumber());
						pst.setString(7, getFatherEmailId());
						pst.setString(8, "M");
						pst.setInt(9, uF.parseToInt(getEmpId()));
//						log.debug("pst=>" + pst);
						pst.execute();
						pst.close();

					}

					pst = con.prepareStatement("UPDATE trainer_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
							+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");

					pst.setString(1, getSpouseName());
					pst.setDate(2, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
					pst.setString(3, getSpouseEducation());
					pst.setString(4, getSpouseOccupation());
					pst.setString(5, getSpouseContactNumber());
					pst.setString(6, getSpouseEmailId());
					pst.setString(7, getSpouseGender());
					pst.setInt(8, uF.parseToInt(getEmpId()));
					pst.setString(9, SPOUSE);
//					log.debug("pst=>" + pst);
					updateCnt = pst.executeUpdate();
					pst.close();
					
					if (updateCnt == 0) {

						pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, "
								+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

						pst.setString(1, SPOUSE);
						pst.setString(2, getSpouseName());
						pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
						pst.setString(4, getSpouseEducation());
						pst.setString(5, getSpouseOccupation());
						pst.setString(6, getSpouseContactNumber());
						pst.setString(7, getSpouseEmailId());
						pst.setString(8, getSpouseGender());
						pst.setInt(9, uF.parseToInt(getEmpId()));
//						log.debug("pst=>" + pst);
						pst.execute();
					}

					if (getMemberName() != null && getMemberName().length != 0) {

						pst = con.prepareStatement("DELETE FROM trainer_family_members WHERE emp_id = ? and member_type = ?");
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.setString(2, SIBLING);
//						log.debug("pst=>" + pst);
						pst.execute();

						for (int i = 0; i < getMemberName().length; i++) {

							pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, "
									+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
							pst.setString(1, SIBLING);
							pst.setString(2, getMemberName()[i]);
							pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
							pst.setString(4, getMemberEducation()[i]);
							pst.setString(5, getMemberOccupation()[i]);
							pst.setString(6, getMemberContactNumber()[i]);
							pst.setString(7, getMemberEmailId()[i]);
							pst.setString(8, getMemberGender()[i]);
							pst.setInt(9, uF.parseToInt(getEmpId()));
//							log.debug("pst=>" + pst);
							pst.execute();
							pst.close();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} 

			public void updateEmpEducation(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;
				ResultSet rs = null;
				try {

					if (getDegreeName() != null && getDegreeName().length != 0) {

						pst = con.prepareStatement("DELETE FROM trainer_education_details WHERE trainer_id = ?");
						pst.setInt(1, uF.parseToInt(getEmpId()));
//						log.debug("pst=>" + pst);
						pst.execute();
						pst.close();
						
						String[] degreeNameOther = request.getParameterValues("degreeNameOther");
						for (int i = 0; i < getDegreeName().length; i++) {
//							System.out.println("getDegreeName()[i]");

							if (getDegreeName()[i].equalsIgnoreCase("other")) {

								pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) " + "VALUES (?,?,?)");
								pst.setString(1, degreeNameOther[i]);
								pst.setString(2, "");
								pst.setInt(3, uF.parseToInt(getOrg_id()));
								pst.execute();
								pst.close();

								int newEduid = 0;
								pst = con.prepareStatement("select max(edu_id) as edu_id educational_details");
								rs = pst.executeQuery();
								while (rs.next()) {
									newEduid = rs.getInt("edu_id");
								}
								rs.close();
								pst.close();
								
								pst = con.prepareStatement("INSERT INTO trainer_education_details(education_id, degree_duration, completion_year, grade, trainer_id)" + "VALUES (?,?,?,?,?)");
								pst.setInt(1, newEduid);
								pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
								pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
								pst.setString(4, getGrade()[i]);
								pst.setInt(5, uF.parseToInt(getEmpId()));
//								log.debug("pst=>" + pst);
								pst.execute();
								pst.close();
								
							} else {
								pst = con.prepareStatement("INSERT INTO trainer_education_details(education_id, degree_duration, completion_year, grade, trainer_id)" + "VALUES (?,?,?,?,?)");
								pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
								pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
								pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
								pst.setString(4, getGrade()[i]);
								pst.setInt(5, uF.parseToInt(getEmpId()));
//								log.debug("pst=>" + pst);
								pst.execute();
								pst.close();
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void updateEmpLangues(Connection con, UtilityFunctions uF) {
				
				PreparedStatement pst = null;
				
				try {
				
					if(getLanguageName()!=null && getLanguageName().length!=0) {
						pst = con.prepareStatement("DELETE FROM trainer_languages_details WHERE trainer_id = ?");
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.execute();
						pst.close();
						
						if(getIsRead()!=null)
						if(getIsWrite()!=null)
						if(getIsSpeak()!=null)
						
						for(int i=0; getLanguageName()!=null && i<getLanguageName().length; i++) {
							if(getLanguageName()[i].length()!=0) {
								pst = con.prepareStatement("INSERT INTO trainer_languages_details(language_name, language_read, language_write, language_speak, trainer_id)" +
								"VALUES (?,?,?,?,?)");
								pst.setString(1, getLanguageName()[i]);
								pst.setInt(2, uF.parseToInt(getIsRead()[i]));
								pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
								pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
								pst.setInt(5, uF.parseToInt(getEmpId()));
//								log.debug("pst=>"+pst);
								pst.execute();
								pst.close();
							}
						}
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}

			
			public void updateDocuments(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;
				
				try {
					
					int i=0;
					if(idDoc!=null) {
						
						int fileSize=1;
						
						for (i=0; getIdDocName()!=null && i<getIdDocName().length; i++) {
							
							File file1=null;
							String filename=null;
							
							if(getIdDoc()!=null && getIdDoc().size()>=fileSize && getIdDocStatus().get(i).equals("1")){
								 file1=getIdDoc().get(fileSize-1);
								 filename=getIdDocFileName().get(fileSize-1);
								  filename = uF.uploadFile(request, DOCUMENT_LOCATION, file1, filename);
								 fileSize++;
							}
							
								if(file1!=null){
					            pst = con.prepareStatement("INSERT INTO trainer_documents_details (documents_name, documents_type, trainer_id, " +
				            		"documents_file_name,added_by,entry_date) values (?,?,?,?,?,?)");
					            pst.setString(1, getIdDocName()[i]);
					            pst.setString(2, getIdDocType()[i]);
					            pst.setInt(3, uF.parseToInt(getEmpId()));
					            pst.setString(4, filename);
					            pst.setInt(5, uF.parseToInt(strSessionEmpId));
					            pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					            pst.execute();
								pst.close();
								}
						}
					}
					
					
				
//					int i=0;
//					pst = con.prepareStatement("DELETE FROM candidate_documents_details WHERE emp_id = ?");
//					pst.setInt(1, uF.parseToInt(getEmpId()));
//					pst.execute();
//					
//					if(idDoc!=null) {
//						int fileSize=1;
//						for (i=0; getIdDocName()!=null && i<getIdDocName().length; i++) {
//							File file1=null;
//							String filename=null;
//							
//							if(getIdDoc()!=null && getIdDoc().size()>=fileSize && getIdDocStatus().get(i).equals("1")){
//								 file1=getIdDoc().get(fileSize-1);
//								 filename=getIdDocFileName().get(fileSize-1);
//								  filename = uF.uploadFile(request, DOCUMENT_LOCATION, file1, filename);
//								 fileSize++;
//							}
//								
//								if(file1!=null){
//					            pst = con.prepareStatement("INSERT INTO trainer_documents_details (documents_name, documents_type, trainer_id, " +
//					            		"documents_file_name,added_by,entry_date) values (?,?,?,?,?,?)");
//					            pst.setString(1, getIdDocName()[i]);
//					            pst.setString(2, getIdDocType()[i]);
//					            pst.setInt(3, uF.parseToInt(getEmpId()));
//					            pst.setString(4, filename);
//					            pst.setInt(5, uF.parseToInt(strSessionEmpId));
//					            pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//					            pst.execute();
//								}
//						}
//					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			

			public void updateHobbies(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;

				try {

					pst = con.prepareStatement("DELETE from trainer_hobbies_details where trainer_id =?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.execute();
					pst.close();

					if(getHobbyName() != null && getHobbyName().length>0) {
						for (String h : getHobbyName()) {
							pst = con.prepareStatement("INSERT INTO trainer_hobbies_details (hobbies_name, trainer_id) VALUES (?,?)");
							pst.setString(1, h);
							pst.setInt(2, uF.parseToInt(getEmpId()));
							pst.execute();
							pst.close();
						}
					}
					

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
					if(pst !=null) {
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
					pst.setInt(1, uF.parseToInt(getEmpId()));
//					log.debug("pst deleteSkills=>" + pst);
					pst.execute();
					pst.close();

					for (int i = 0; getSkillName() != null && i < getSkillName().length; i++) {

						if (getSkillName()[i].length() != 0 || getSkillValue()[i].length() != 0) {

							pst = con.prepareStatement("INSERT INTO trainer_skills_description (skill_id, skills_value, trainer_id) VALUES (?,?,?)");
							pst.setInt(1, uF.parseToInt(getSkillName()[i]));
							pst.setString(2, getSkillValue()[i]);
							pst.setInt(3, uF.parseToInt(getEmpId()));
//							if (isDebug)
//								log.debug("pst==>>" + pst);
							pst.execute();
							pst.close();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				
					if(pst !=null){
						try {
							pst.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}

			public void insertTrainer() {

//				log.debug("inside insert");
				Connection con = null;
				Database db = new Database();
				db.setRequest(request);
				
				UtilityFunctions uF = new UtilityFunctions();

				try {

					con = db.makeConnection(con);

					if (uF.parseToInt(getStep()) == 1) {

						if (uF.parseToInt(getEmpId()) == 0) {
							insertTrainerPersonalDetails(con, uF, CF);
							// insertUser(con, uF,uF.parseToInt(getCandidateId()));
							uploadImage(uF.parseToInt(getEmpId()));
						}
						else {
							updateTrainerPersonalDetails(con, uF);
							uploadImage(uF.parseToInt(getEmpId()));
						}

						session.setAttribute("EMPNAME_P", getEmpFname() + " " + getEmpLname());
						session.setAttribute("EMPID_P", uF.parseToInt(getEmpId()) + "");

					} else if (uF.parseToInt(getStep()) == 2) {
						insertSkills(con, uF);
						insertHobbies(con, uF);
						insertEmpLangues(con, uF);
						insertEmpEducation(con, uF);

					} else if (uF.parseToInt(getStep()) == 3) {

						insertTrainerFamilyMembers(con, uF);

					} else if (uF.parseToInt(getStep()) == 4) {

						insertTrainerPrevEmploment(con, uF);

					} else if (uF.parseToInt(getStep()) == 5) {

						insertTrainerReferences(con, uF);

					} else if (uF.parseToInt(getStep()) == 6) {

						insertTrainerMedicalInfo(con, uF);

					} else if (uF.parseToInt(getStep()) == 7) {

						insertTrainerDocuments(con, uF);

					}

					Map<String, String> hmTrainerName = CF.getTrainerNameMap(con);
					session.setAttribute(MESSAGE, SUCCESSM+"You have added trainer "+hmTrainerName.get(getEmpId())+" successfully."+END);
					
				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					
					db.closeConnection(con);
				}
			}

		


			private void insertTrainerReferences(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;

				try {

					pst = con.prepareStatement("INSERT INTO trainer_references (ref_name, ref_company, ref_designation, ref_contact_no, " +
							"ref_email_id, trainer_id) values(?,?,?,?,?,?)");
					pst.setString(1, getRef1Name());
					pst.setString(2, getRef1Company());
					pst.setString(3, getRef1Designation());
					pst.setString(4, getRef1ContactNo());
					pst.setString(5, getRef1Email());
					pst.setInt(6, uF.parseToInt(getEmpId()));
//					log.debug("pst==>" + pst);
					pst.execute();
					pst.close();

					pst = con.prepareStatement("INSERT INTO trainer_references (ref_name, ref_company, ref_designation, ref_contact_no, " +
							"ref_email_id, trainer_id) values(?,?,?,?,?,?)");
					pst.setString(1, getRef2Name());
					pst.setString(2, getRef2Company());
					pst.setString(3, getRef2Designation());
					pst.setString(4, getRef2ContactNo());
					pst.setString(5, getRef2Email());
					pst.setInt(6, uF.parseToInt(getEmpId()));
//					log.debug("pst==>" + pst);
					pst.execute();
					pst.close();

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				
					if(pst !=null){
						try {
							pst.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}


			private void updateEmpMedicalInfo1(Connection con, UtilityFunctions uF,String medicalid, boolean checkQue, String queDesc, File file,String fileName1) {
				PreparedStatement pst = null;

				try {
					String fileName = null;
					if (file != null) {
						fileName = uF.uploadFile(request, DOCUMENT_LOCATION, file,fileName1);
						pst = con.prepareStatement("UPDATE trainer_medical_details SET yes_no=?, description=?,filepath=? WHERE medical_id = ?");
						pst.setBoolean(1, checkQue);
						pst.setString(2, queDesc);
						pst.setString(3, fileName);
						pst.setInt(4, uF.parseToInt(medicalid));
					} else {
						pst = con.prepareStatement("UPDATE trainer_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
						pst.setBoolean(1, checkQue);
						pst.setString(2, queDesc);
						pst.setInt(3, uF.parseToInt(medicalid));
					}
//					log.debug("pst ==>" + pst);
					pst.execute();
					pst.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
					if(pst !=null){
						try {
							pst.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					}
			}
			
			
			private void insertEmpMedicalInfo1(Connection con, UtilityFunctions uF,String queId,boolean checkQue,String queDesc,File file,String fileName1) {
				PreparedStatement pst = null;
				
				try {
					
					String fileName =null;
					if(file!=null){
					 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, file, fileName1);
					 }
//					System.out.println();
					pst = con.prepareStatement("INSERT INTO trainer_medical_details (question_id, trainer_id, yes_no, description,filepath)" +
							" values(?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(queId));
					pst.setInt(2, uF.parseToInt(getEmpId()));
					pst.setBoolean(3, checkQue);
					pst.setString(4, queDesc);
					pst.setString(5, fileName);
//							log.debug("pst ==>"+pst);
					pst.execute();
					pst.close();
					
				}catch(Exception e){
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
			
		private void insertTrainerMedicalInfo(Connection con, UtilityFunctions uF) {
				
//				PreparedStatement pst = null;
				File file1=null;
				String filename=null;
				int fileSize=1;
				
//				System.out.println("getQue1DescFile() ====> "+getQue1DescFile());
//				System.out.println("getQue1IdFileStatus().get(0) ====> "+getQue1IdFileStatus().get(0));
				if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(0).equals("1")){
					 file1=getQue1DescFile().get(fileSize-1);
					 filename=getQue1DescFileFileName().get(fileSize-1);
//					 System.out.println("file1 ====> "+file1);
//					 System.out.println("filename ====> "+filename);
					 fileSize++;
				insertEmpMedicalInfo1(con,uF,getQue1Id(),isCheckQue1(),getQue1Desc(),file1,filename);
				}
				
				file1=null;
				 filename=null;
				if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(1).equals("1")){
					 file1=getQue1DescFile().get(fileSize-1);
					 filename=getQue1DescFileFileName().get(fileSize-1);
					 fileSize++;
				insertEmpMedicalInfo1(con,uF,getQue2Id(),isCheckQue2(),getQue2Desc(),file1,filename);
				}
				
				file1=null;
				 filename=null;
				if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(2).equals("1")){
					 file1=getQue1DescFile().get(fileSize-1);
					 filename=getQue1DescFileFileName().get(fileSize-1);
					 fileSize++;
				insertEmpMedicalInfo1(con,uF,getQue3Id(),isCheckQue3(),getQue3Desc(),file1,filename);
				}
			}
			
			
			
			private void sendNotifications() {

				// Notifications nF = new Notifications(N_NEW_EMPLOYEE);
				// nF.setStrEmailTo(getEmpEmail());
				// nF.setStrEmpMobileNo(getEmpContactno());
				// nF.setStrEmpCode(getEmpCode());
				// nF.setStrEmpFname(getEmpFname());
				// nF.setStrEmpLname(getEmpLname());
				// nF.setStrUserName(getUserName());
				// nF.setStrPassword(getEmpPassword());
				// nF.sendNotifications();

			}

			private void uploadImage(int empId2) {

				try {

					UploadImage uI = new UploadImage();
					uI.setServletRequest(request);
					uI.setImageType("TRAINER_IMAGE");
					uI.setEmpImage(getEmpImage());
					uI.setEmpImageFileName(getEmpImageFileName());
					uI.setEmpId(empId2 + "");
					uI.setCF(CF);
//					System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
					uI.upoadImage();

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			public void insertTrainerPrevEmploment(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;

				try {

					if (getPrevCompanyName() != null && getPrevCompanyName().length != 0) {

						for (int i = 0; i < getPrevCompanyName().length; i++) {

							if (getPrevCompanyName()[i].length() != 0) {

								pst = con.prepareStatement("INSERT INTO trainer_prev_employment(company_name, company_location, company_city, company_state, "
										+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, trainer_id)"
										+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
								pst.setInt(13, uF.parseToInt(getEmpId()));
//								log.debug("pst=>" + pst);
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

			public void insertTrainerDocuments(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;
				try {
					String filePath1 = request.getRealPath("/userDocuments/");
					String fileName1 = "";
					if(getIdDoc()!=null && getIdDoc().size()!= 0 ) {
						
						for (int i=0; i<getIdDoc().size(); i++) {
							
							if(getIdDoc().get(i)!=null & getIdDoc().get(i).length()!= 0) {
								
								String fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getIdDoc().get(i), getIdDocFileName().get(i));
								
					            pst = con.prepareStatement("INSERT INTO trainer_documents_details (documents_name, documents_type, " +
					            		"trainer_id, documents_file_name,added_by,entry_date) values (?,?,?,?,?,?)");
								pst.setString(1, getIdDocName()[i]);
					            pst.setString(2, getIdDocType()[i]);
					            pst.setInt(3, uF.parseToInt(getEmpId()));
					            pst.setString(4, fileName);
					            pst.setInt(5, uF.parseToInt(strSessionEmpId));
					            pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//					            log.debug("pst insertDocuments==>"+pst);
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

					if (getHobbyName() != null && getHobbyName().length != 0) {

						for (String h : hobbyName) {

							if (h != null && h.length() != 0 && !h.equals("")) {

								pst = con.prepareStatement("INSERT INTO trainer_hobbies_details (hobbies_name, trainer_id) VALUES (?,?)");
								pst.setString(1, h);
								pst.setInt(2, uF.parseToInt(getEmpId()));
//								log.debug("pst=C=>>" + pst);
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
					for (int i = 0; i < getSkillName().length; i++) {
						if (getSkillName()[i] != null && getSkillName()[i].length() != 0 && !getSkillName()[i].equals("")) {
							pst = con.prepareStatement("INSERT INTO trainer_skills_description (skill_id, skills_value, trainer_id) VALUES (?,?,?)");
							pst.setInt(1, uF.parseToInt(getSkillName()[i]));
							pst.setString(2, getSkillValue()[i]);
							pst.setInt(3, uF.parseToInt(getEmpId()));
//							log.debug("pst==>>" + pst);
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


			public int insertTrainerPersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {

				PreparedStatement pst = null;
				ResultSet rs = null;
				int trainerPerId = 0;
				try {

					pst = con.prepareStatement("INSERT INTO trainer_personal_details (trainer_fname, trainer_lname, trainer_email, " +
							"trainer_address1, trainer_address2, trainer_city_id, trainer_state_id, trainer_country_id, trainer_pincode, trainer_address1_tmp, trainer_address2_tmp, trainer_city_id_tmp, trainer_state_id_tmp, trainer_country_id_tmp, trainer_pincode_tmp, trainer_contactno, joining_date, " +
							"trainer_pan_no,trainer_pf_no,trainer_gpf_no, trainer_gender, trainer_date_of_birth, trainer_bank_name, trainer_bank_acct_nbr, trainer_email_sec, skype_id, trainer_contactno_mob, " +
							"emergency_contact_name, emergency_contact_no, passport_no, passport_expiry_date, blood_group, marital_status,trainer_date_of_marriage, " +
							"approved_flag, trainer_entry_date) " +
							"VALUES (?,  ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?) ");
					
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
//					pst.setInt(37, empTrainerId);
					pst.execute();
					pst.close();

					pst = con.prepareStatement("SELECT max(trainer_id) as trainer_id from trainer_personal_details");
					rs = pst.executeQuery();
					while(rs.next()) {
						setEmpId(rs.getString("trainer_id"));
						trainerPerId = rs.getInt("trainer_id");
					}
					rs.close();
					pst.close();
					
					getTrainerName();
					
					pst=con.prepareStatement("insert into training_trainer (trainer_name,trainer_mobile,trainer_email, " +
							"trainer_address,trainer_city,trainer_state,trainer_country,trainer_emp_id)" +
							" values(?,?,?,?, ?,?,?,?)"); //,trainer_work_location ,?
					pst.setString(1, getEmpFname()+" "+getEmpLname());
					pst.setString(2, getEmpMobileNo());
					pst.setString(3, getEmpEmail());
					pst.setString(4, getEmpAddress1());
					pst.setString(5, getCity());
					pst.setInt(6, uF.parseToInt(getState()));
					pst.setInt(7, uF.parseToInt(getCountry()));
//					pst.setInt(8, uF.parseToInt(getStrwLocation()));
					pst.setInt(8, uF.parseToInt(getEmpId()));
				//	pst.setTimestamp(9, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//					System.out.println("pst==>"+pst);
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
				return trainerPerId;
			}

			
			public void insertTrainerFamilyMembers(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;

				try {

					if (getMotherName() != null && getMotherName().length() > 0) {

						pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, "
								+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
						pst.setString(1, MOTHER);
						pst.setString(2, getMotherName());
//						log.debug(getMotherDob() + "getMotherDob()");
						pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
						pst.setString(4, getMotherEducation());
						pst.setString(5, getMotherOccupation());
						pst.setString(6, getMotherContactNumber());
						pst.setString(7, getMotherEmailId());
						pst.setString(8, "F");
						pst.setInt(9, uF.parseToInt(getEmpId()));
//						log.debug("pst=>" + pst);
//						System.out.println("pst==>"+pst);
						pst.execute();
						pst.close();
					}

					if (getFatherName() != null && getFatherName().length() > 0) {

						pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, "
								+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
						pst.setString(1, FATHER);
						pst.setString(2, getFatherName());
						pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
						pst.setString(4, getFatherEducation());
						pst.setString(5, getFatherOccupation());
						pst.setString(6, getFatherContactNumber());
						pst.setString(7, getFatherEmailId());
						pst.setString(8, "M");
						pst.setInt(9, uF.parseToInt(getEmpId()));
//						log.debug("pst=>" + pst);
						pst.execute();
						pst.close();
					}

					if (getSpouseName() != null && getSpouseName().length() > 0) {
						pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, "
								+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

						pst.setString(1, SPOUSE);
						pst.setString(2, getSpouseName());
						pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
						pst.setString(4, getSpouseEducation());
						pst.setString(5, getSpouseOccupation());
						pst.setString(6, getSpouseContactNumber());
						pst.setString(7, getSpouseEmailId());
						pst.setString(8, "F");
						pst.setInt(9, uF.parseToInt(getEmpId()));
//						log.debug("pst=>" + pst);
						pst.execute();
						pst.close();
					}

					for (int i = 0; i < getMemberName().length; i++) {
						if (getMemberName()[i] != null && getMemberName()[i].length() > 0) {
							pst = con.prepareStatement("INSERT INTO trainer_family_members(member_type, member_name, member_dob, member_education, "
									+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

							pst.setString(1, SIBLING);
							pst.setString(2, getMemberName()[i]);
							pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
							pst.setString(4, getMemberEducation()[i]);
							pst.setString(5, getMemberOccupation()[i]);
							pst.setString(6, getMemberContactNumber()[i]);
							pst.setString(7, getMemberEmailId()[i]);
							pst.setString(8, getMemberGender()[i]);
							pst.setInt(9, uF.parseToInt(getEmpId()));
//							log.debug("pst=>" + pst);
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

			public void insertEmpEducation(Connection con, UtilityFunctions uF) {

				PreparedStatement pst = null;
				ResultSet rs = null;
				try {
					if (getDegreeName() != null && getDegreeName().length != 0) {
						String[] degreeNameOther = request.getParameterValues("degreeNameOther");
						for (int i = 0; i < getDegreeName().length; i++) {

							if (getDegreeName()[i].equalsIgnoreCase("other")) {
								if(degreeNameOther[i] != null && !degreeNameOther[i].equals("")){
									pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) VALUES (?,?,?)");
									pst.setString(1, degreeNameOther[i]);
									pst.setString(2, "");
									pst.setInt(3, uF.parseToInt(getOrg_id()));
									
									pst.execute();
									pst.close();
									
									int newEduid = 0;
									pst = con.prepareStatement("select max(edu_id) as edu_id educational_details");
									rs = pst.executeQuery();
									while (rs.next()) {
										newEduid = rs.getInt("edu_id");
									}
									rs.close();
									pst.close();
									
									
									pst = con.prepareStatement("INSERT INTO trainer_education_details(education_id, degree_duration, completion_year," +
											" grade, trainer_id)VALUES (?,?,?,?,?)");
									pst.setInt(1, newEduid);
									pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
									pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
									pst.setString(4, getGrade()[i]);
									pst.setInt(5, uF.parseToInt(getEmpId()));
//									log.debug("pst=>" + pst);
									pst.execute();
									pst.close();
								}
							} else {
								if(getDegreeName()[i] != null && !getDegreeName()[i].equals("")){
									pst = con.prepareStatement("INSERT INTO trainer_education_details(education_id, degree_duration, completion_year," +
											" grade, trainer_id)VALUES (?,?,?,?,?)");
									
									pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
									pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
									pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
									pst.setString(4, getGrade()[i]);
									pst.setInt(5, uF.parseToInt(getEmpId()));
									
									pst.execute();
									pst.close();
								}
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
							
							if(getLanguageName()[i] != null && getLanguageName()[i].length()!=0 && !getLanguageName()[i].equals("")) {
							
								pst = con.prepareStatement("INSERT INTO trainer_languages_details(language_name, language_read, language_write, language_speak, trainer_id)" +
								"VALUES (?,?,?,?,?)");
								pst.setString(1, getLanguageName()[i]);
								pst.setInt(2, uF.parseToInt(getIsRead()[i]));
								pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
								pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
								pst.setInt(5, uF.parseToInt(getEmpId()));
//								log.debug("pst=>"+pst);
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

			public String deleteEmployee(int EmpId) {

				Connection con = null;
				PreparedStatement pst = null;
				Database db = new Database();
				db.setRequest(request);
				try {

					con = db.makeConnection(con);
					
					Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
					Map<String, String> hmTrainerName = CF.getTrainerNameMap(con);
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("DELETE FROM training_trainer WHERE ");
					if(getTrainerType() != null && getTrainerType().equals("External")){
						sbQuery.append("trainer_emp_id = ?");
					}else{
						sbQuery.append("emp_id = ?");
					}
//					pst = con.prepareStatement("DELETE FROM training_trainer WHERE trainer_emp_id = ?");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, EmpId);
					pst.execute();
					pst.close();
					
					if(getTrainerType() != null && getTrainerType().equals("External")){
					
						pst = con.prepareStatement("DELETE FROM trainer_personal_details WHERE trainer_id=?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE from trainer_skills_description where trainer_id =?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE from trainer_hobbies_details where trainer_id =?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE FROM trainer_languages_details WHERE trainer_id = ?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE FROM trainer_education_details WHERE trainer_id = ?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE FROM trainer_documents_details WHERE trainer_id = ?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE FROM trainer_family_members WHERE emp_id = ?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE FROM trainer_prev_employment WHERE trainer_id = ?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE FROM trainer_references WHERE trainer_id = ?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("DELETE FROM trainer_medical_details WHERE trainer_id = ?");
						pst.setInt(1, EmpId);
						pst.execute();
						pst.close();
					}
					
					String empName1 = null;
					if(getTrainerType() != null && !getTrainerType().equals("External")){
						empName1 = hmEmpName.get(""+EmpId);
					} else {
//						System.out.println("hmTrainerName.get(EmpId) else ===> " + hmTrainerName.get(""+EmpId));
						empName1 = hmTrainerName.get(""+EmpId);
					}
					session.setAttribute(MESSAGE, SUCCESSM+"You have deleted trainer "+empName1+" successfully."+END);

				} catch (Exception e) {
					e.printStackTrace();
					return ERROR;
				} finally {
					
					db.closeStatements(pst);
					db.closeConnection(con);
				}
				return SUCCESS;
			}
			
			
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

			public String getStrwLocation() {
				return strwLocation;
			}

			public void setStrwLocation(String strwLocation) {
				this.strwLocation = strwLocation;
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
				this.empDateOfBirth = empDateOfBirth;
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

			/*
			 * public String getStrEdit() { return strEdit; }
			 * 
			 * public void setStrEdit(String strEdit) { this.strEdit = strEdit; }
			 */

			public List<File> getQue1DescFile() {
				return que1DescFile;
			}

			public void setQue1DescFile(List<File> que1DescFile) {
				this.que1DescFile = que1DescFile;
			}

			public List<String> getQue1IdFileStatus() {
				return que1IdFileStatus;
			}

			public void setQue1IdFileStatus(List<String> que1IdFileStatus) {
				this.que1IdFileStatus = que1IdFileStatus;
			}

			public List<String> getQue1DescFileFileName() {
				return que1DescFileFileName;
			}

			public void setQue1DescFileFileName(List<String> que1DescFileFileName) {
				this.que1DescFileFileName = que1DescFileFileName;
			}

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

			public List<File> getIdDoc() {
				return idDoc;
			}

			public void setIdDoc(List<File> idDoc) {
				this.idDoc = idDoc;
			}

			public List<String> getIdDocFileName() {
				return idDocFileName;
			}

			public void setIdDocFileName(List<String> idDocFileName) {
				this.idDocFileName = idDocFileName;
			}

			public List<String> getIdDocStatus() {
				return idDocStatus;
			}

			public void setIdDocStatus(List<String> idDocStatus) {
				this.idDocStatus = idDocStatus;
			}

			public String getEmpGPFNo() {
				return empGPFNo;
			}

			public void setEmpGPFNo(String empGPFNo) {
				this.empGPFNo = empGPFNo;
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

			// for record particular job id
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

			public List<List<String>> selectEducation(Connection con, int empId) {

				PreparedStatement pst = null;
				ResultSet rs = null;
				List<List<String>> alEducation = new ArrayList<List<String>>();

				try {

					pst = con.prepareStatement("SELECT * FROM trainer_education_details WHERE trainer_id = ?");
					pst.setInt(1, empId);
//					log.debug("pst=>" + pst);
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();

					while (rs.next()) {

						List<String> alInner = new ArrayList<String>();
						alInner.add(rs.getString("degree_id"));//0
						alInner.add(CF.getDegreeNameByDegreeId(con, rs.getString("education_id")));//1
						alInner.add(rs.getString("degree_duration"));//2
						alInner.add(rs.getString("completion_year"));//3
						alInner.add(rs.getString("grade"));//4
						alInner.add(rs.getString("education_id"));//5
						alEducation.add(alInner);
					}
					rs.close();
					pst.close();

				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getClass() + ": " + e.getMessage(), e);
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
//					log.debug("pst=>" + pst);
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
					log.error(e.getClass() + ": " + e.getMessage(), e);
				}
//				log.debug("selectLanguages: alLanguages==>" + alLanguages);
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
//					log.error(e.getClass() + ": " + e.getMessage(), e);
				}
				return alHobbies;

			}

			public List<List<String>> selectSkills(Connection con, int EmpId) {

				List<List<String>> alSkills = new ArrayList<List<String>>();
				PreparedStatement pst = null;
				ResultSet rs = null;
//				StringBuilder sb = new StringBuilder();
//				String str = "";
				try {
					pst = con.prepareStatement("SELECT * FROM trainer_skills_description WHERE trainer_id=? ORDER BY skills_id");
					pst.setInt(1, EmpId);
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();
					
//					int count = 0;
					while (rs.next()) {
						List<String> alInner1 = new ArrayList<String>();
						alInner1.add(rs.getInt("skills_id") + "");//0
						alInner1.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));//1
						alInner1.add(rs.getString("skills_value"));//2
						alInner1.add(rs.getInt("trainer_id") + "");//3
						alInner1.add(rs.getInt("skill_id") + "");//4

						alSkills.add(alInner1);

//						sb.append(rs.getString("skills_name") + ((count == 0) ? " [Pri]" : "") + ", ");
//						count++;
					}
					rs.close();
					pst.close();
					
//					int index = sb.lastIndexOf(",");
//					if (index > 0) {
//						str = sb.substring(0, index);
//					}

				} catch (Exception e) {
					e.printStackTrace();
//					log.error(e.getClass() + ": " + e.getMessage(), e);
				}
				return alSkills;

			}

			public List<List<String>> selectPrevEmploment(Connection con, int empId) {

				PreparedStatement pst = null;
				ResultSet rs = null;
				List<List<String>> alPrevEmployment = new ArrayList<List<String>>();
				UtilityFunctions uF = new UtilityFunctions();

				try {
					pst = con.prepareStatement("SELECT * FROM trainer_prev_employment WHERE trainer_id = ? order by from_date");
					pst.setInt(1, empId);
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
//					log.error(e.getClass() + ": " + e.getMessage(), e);
				}

				return alPrevEmployment;
			}

			public List<List<Object>> selectTrainerDocuments(Connection con, int empId, String filePath) {

				PreparedStatement pst = null;
				ResultSet rs = null;
				List<List<Object>> alDocuments = new ArrayList<List<Object>>();
				UtilityFunctions uF = new UtilityFunctions();
				try {

					Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
					
					pst = con.prepareStatement("SELECT * FROM trainer_documents_details where trainer_id = ?");
					pst.setInt(1, empId);
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
						alInner1.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
						alInner1.add(uF.showData(hmEmpCodeName.get(rs.getString("added_by")), "-"));
						alDocuments.add(alInner1);
					}
					rs.close();
					pst.close();

				} catch (Exception e) {
					e.printStackTrace();
//					log.error(e.getClass() + ": " + e.getMessage(), e);
				}
				return alDocuments;

			}

			public List<FillEducational> getEducationalList() {
				return educationalList;
			}

			public void setEducationalList(List<FillEducational> educationalList) {
				this.educationalList = educationalList;
			}

			String degreeNameOther;

			public String getDegreeNameOther() {
				return degreeNameOther;
			}

			public void setDegreeNameOther(String degreeNameOther) {
				this.degreeNameOther = degreeNameOther;
			}

			public String getOrg_id() {
				return org_id;
			}

			public void setOrg_id(String org_id) {
				this.org_id = org_id;
			}

			public String getEmpId() {
				return empId;
			}

			public void setEmpId(String empId) {
				this.empId = empId;
			}

			public String getTrainerType() {
				return trainerType;
			}

			public void setTrainerType(String trainerType) {
				this.trainerType = trainerType;
			}

			private String type;

			// String stepSubmit;

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}



}

