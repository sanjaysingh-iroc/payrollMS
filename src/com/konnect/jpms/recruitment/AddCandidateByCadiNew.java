package com.konnect.jpms.recruitment;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

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
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillProbationDuration;
import com.konnect.jpms.select.FillSalutation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillSources;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCandidateByCadiNew extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	// HttpSession session;
	// String strUserType = null;
	// String strSessionEmpId = null;

	private String mode;
	// private int empId;
	String operation;

	Boolean autoGenerate = false;
	private String step;
	private String serviceId;
	private String ServiceName;
	String org_id;
	String CandidateId;
	String show;
	String candibymail;
	String otherDocumentCnt;
	String candiCurrCTC;
	String candiExpectedCTC;
	String candiNoticePeriod;
	String availability;
	String strAvailability;
	private String sessionId;

	String refEmpCode;
	String otherRefSrc;
	String candiSource;
	String isEmpCode;
	String refEmpId;
	String frombgverify;
	File idOtherDoc[];

	private List<FillOrganisation> orgList;
	List<FillSources> sourceList;
	StringBuilder sbServicesLink = new StringBuilder();
	CommonFunctions CF = null;

	public static String MOTHER = "MOTHER";
	public static String FATHER = "FATHER";
	public static String SPOUSE = "SPOUSE";
	public static String SIBLING = "SIBLING";

	private String[] skillName;
	private String[] skillValue;
	private String[] skillNameSec;
	private String[] skillValueSec;
	private String[] candiTitle;
	private String[] candiTitleValue;
	private String[] strWLocation;
	private String[] certificationTitle;
	private String[] certificationCompletionYear;
	private String[] location;
	
	private String prefLocation;
	private String currentLocation;

	private String strHighestDegree;
	private String strHighestDegreeSubject;
	private String strHighestDegreeStartDate;
	private String strHighestDegreeCompletionDate;
	private String strHighestDegreeGrade;
	private String strHighestDegreeMarksCGPA;

	private String strGraduateDegree;
	private String strGraduateDegreeSubject;
	private String strGraduateDegreeStartDate;
	private String strGraduateDegreeCompletionDate;
	private String strGraduateDegreeGrade;
	private String strGraduateDegreeMarksCGPA;

	private String strHSC;
	private String strHSCBoard;
	private String strHSCInstitute;
	private String strHSCCity;
	private String strHSCStartDate;
	private String strHSCCompletionDate;
	private String strHSCGrade;
	private String strHSCMarksCGPA;

	private String strSSC;
	private String strSSCBoard;
	private String strSSCInstitute;
	private String strSSCCity;
	private String strSSCStartDate;
	private String strSSCCompletionDate;
	private String strSSCGrade;
	private String strSSCMarksCGPA;

	private static Logger log = Logger.getLogger(AddCandidate.class);

	@Override
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		// session = request.getSession();
		CF = new CommonFunctions();
		CF.setRequest(request);

		orgList = new FillOrganisation(request).fillOrganisation();
		if (uF.parseToInt(getOrg_id()) == 0) {
			setOrg_id(orgList.get(0).getOrgId());
		}
//		System.out.println("ACBC/114--orgList=" + getOrg_id());

		request.setAttribute(PAGE, "/jsp/recruitment/AddCandidateByCadiNew.jsp");
		request.setAttribute(TITLE, "Add Candidate Details");
		request.setAttribute(MENU, null);


		CF.getFormValidationFields(request, ADD_UPDATE_CANDIDATE);
		boolean candiFlag = getCandidateName();
		if (!candiFlag) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}

//		System.out.println(" getStepSubmit() ===>> " + getStepSubmit());
		if (getStepSubmit() != null && getStepSubmit().equalsIgnoreCase("Submit")) {

			insertEmployee();
			request.setAttribute("STATUS_MSG", "<h4 style='text-align:center;'>Your details has been submited successfully.</h4>");
			return "finish";
		}
		
		loadValidateEmployee();
		return LOAD;
	}

	public boolean validateSession(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean isValidSesseion = false;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(request.getParameter("empId")));
			rs = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (rs.getString("session_id") != null && rs.getString("session_id").equals(request.getParameter("sessionId"))) {
					isValidSesseion = true;
				}
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return isValidSesseion;
	}

	public String loadValidateEmployee() {

		try {

			UtilityFunctions uF = new UtilityFunctions();

			wLocationList = new FillWLocation(request).fillWorkLocationName(getOrg_id());
			bankList = new FillBank(request).fillBankDetails();
			gradeList = new FillGrade(request).fillGrade();
			desigList = new FillDesig(request).fillDesig();
			levelList = new FillLevel(request).fillLevel();
			deptList = new FillDepartment(request).fillDepartment();
			serviceList = new FillServices(request).fillServices();
			countryList = new FillCountry(request).fillCountry();
			stateList = new FillState(request).fillState();

			salutationList = new FillSalutation(request).fillSalutation();

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
			skillsList = new FillSkills(request).fillSkillsWithId();
			educationalList = new FillEducational(request).fillEducationalQual();
			sourceList = new FillSources().fillSourcesDetails();
			paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();

			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return LOAD;

	}

	
	private boolean getCandidateName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmSettingOption = CF.getSettingsMap(con);
			CF.setStrDocSaveLocation(hmSettingOption.get("DOC_SAVE_LOCATION"));

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			String candName = "";
			pst = con.prepareStatement("select emp_fname,emp_mname,emp_lname,session_id from candidate_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rs = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			// System.out.println("pst Candi name = "+pst);
			String candiSessionId = null;
			while (rs.next()) {
				String strEmpMName = "";
				if (flagMiddleName) {
					if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
						strEmpMName = " " + rs.getString("emp_mname");
					}
				}
				candName = rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname");
				candiSessionId = rs.getString("session_id");
			}
			rs.close();
			pst.close();

			// System.out.println("candiSessionId ===>>" + candiSessionId+"--
			// getSessionId() ===>> " + getSessionId());
			if ((candiSessionId == null && getSessionId() == null) || candiSessionId.equals(getSessionId())) {
				flag = true;
			}
			flag = true;
			request.setAttribute("CandidateName", candName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	public void insertEmployee() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);

		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			insertCandidatePersonalDetails(con, uF, CF);
			insertSkills1(con, uF);
			insertEmpEducation(con, uF);
			insertCertificationDetails(con, uF);
			updateSupportingDocuments(con, uF);
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			db.closeConnection(con);
		}
	}
	/**
	 * Created By Dattatray
	 * 
	 * @since 12-08-21
	 * @param candidateId
	 */
	public void sendMail(String candidateId) {
		System.out.println("candidateId : " + candidateId);
		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = getCandiInfoMap(con, false);
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateId());

			String strDomain = request.getServerName().split("\\.")[0];
			CandidateNotifications nF = new CandidateNotifications(N_FRESHER_JOB_SUBMISSION, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(candidateId);
			nF.setStrRecruitmentId(getRecruitId());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());

			nF.setStrCandiFname(hmCandiInner != null ? hmCandiInner.get("FNAME") : "");
			nF.setStrCandiLname(hmCandiInner != null ? hmCandiInner.get("LNAME") : "");
			nF.sendNotifications();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	/**
	 * Created By Dattatray
	 * 
	 * @since 12-08-21
	 * @param con
	 * @param isFamilyInfo
	 * @return
	 */
	public Map<String, Map<String, String>> getCandiInfoMap(Connection con, boolean isFamilyInfo) {
		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmCandiInner = new HashMap<String, String>();
			if (isFamilyInfo) {
				pst = con.prepareStatement("select * from candidate_family_members order by emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
					if (hmCandiInner == null)
						hmCandiInner = new HashMap<String, String>();
					hmCandiInner.put(rs.getString("member_type"), rs.getString("member_name"));
					hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
				}
				rs.close();
				pst.close();
			}

			pst = con.prepareStatement(
					"SELECT cpd.emp_per_id, cpd.emp_fname, cpd.emp_lname, cpd.empcode, cpd.emp_image, cpd.emp_email,cpd.emp_date_of_birth,  cpd.emp_gender, cpd.marital_status FROM candidate_personal_details cpd order by emp_per_id");
			rs = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (rs.getInt("emp_per_id") < 0) {
					continue;
				}
				hmCandiInner = hmCandiInfo.get(rs.getString("emp_per_id"));
				if (hmCandiInner == null)
					hmCandiInner = new HashMap<String, String>();

				hmCandiInner.put("FNAME", rs.getString("emp_fname"));
				hmCandiInner.put("LNAME", rs.getString("emp_lname"));
				hmCandiInner.put("FULLNAME", rs.getString("emp_lname") + " " + rs.getString("emp_lname"));

				hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmCandiInfo;
	}
	

	


	public void updateSupportingDocuments(Connection con, UtilityFunctions uF) {

		try {
			if(getIdDoc0() !=null) {
				insertDocuments(con, uF, getIdDoc0(), getIdDocName0(), getIdDocType0(), getIdDocStatus0(), getIdDoc0FileName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateDocuments(Connection con, UtilityFunctions uF, String docId, File idDoc, String idDocName, String idDocType, String idDocStatus,
			String idDocFileName) {
		PreparedStatement pst = null;
		try {
			String strFileName = null;
			// System.out.println("In updateDocuments");
			if (idDoc != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, idDoc, idDocFileName, idDocFileName, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request,
							CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/" + getCandidateId(), idDoc, idDocFileName,
							idDocFileName, CF);
				}
			}
			pst = con.prepareStatement("UPDATE candidate_documents_details SET documents_file_name=?,added_by=?,entry_date=? where documents_id = ?");
			pst.setString(1, strFileName);
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(docId));
			// System.out.println("update pst ===>> " + pst);
			int x = pst.executeUpdate();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertDocuments(Connection con, UtilityFunctions uF, File idDoc, String idDocName, String idDocType, String idDocStatus, String idDocFileName) {
		PreparedStatement pst = null;
		try {
			String strFileName = null;
			if (idDoc != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, idDoc, idDocFileName, idDocFileName, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request,
							CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/" + getCandidateId(), idDoc, idDocFileName,
							idDocFileName, CF);
				}
			}
			// System.out.println("strFileName ===>> " + strFileName);
			pst = con.prepareStatement(
					"INSERT INTO candidate_documents_details(documents_name, documents_type, emp_id, documents_file_name, entry_date) values (?,?,?,?, ?)");
			pst.setString(1, idDocName);
			pst.setString(2, idDocType);
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setString(4, strFileName);
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			int x = pst.executeUpdate();
			// System.out.println("Insert pst======>"+pst);

			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// public void insertDocuments(Connection con, UtilityFunctions uF) {
	// PreparedStatement pst = null;
	// try {
	// if(getIdDoc()!=null && getIdDoc().size()!= 0 ) {
	// for (int i=0; i<getIdDoc().size(); i++) {
	//
	// if(getIdDoc().get(i)!=null & getIdDoc().get(i).length()!= 0) {
	//
	// String fileName = uF.uploadFile(request, DOCUMENT_LOCATION,
	// getIdDoc().get(i), getIdDocFileName().get(i));
	// pst = con.prepareStatement("INSERT INTO candidate_documents_details
	// (documents_name, documents_type, emp_id, documents_file_name," +
	// "entry_date) values (?,?,?,?,?)");
	// pst.setString(1, getIdDocName()[i]);
	// pst.setString(2, getIdDocType()[i]);
	// pst.setInt(3, uF.parseToInt(getCandidateId()));
	// pst.setString(4, fileName);
	//// pst.setInt(5, uF.parseToInt(strSessionEmpId));
	// pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
	//// log.debug("pst insertDocuments==>"+pst);
	// pst.execute();
	// pst.close();
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }finally {
	//
	// if(pst != null) {
	// try {
	// pst.close();
	// } catch (SQLException e1) {
	// e1.printStackTrace();
	// }
	// }
	// }
	// }

	// public void insertDocuments(Connection con, UtilityFunctions uF) {
	// PreparedStatement pst = null;
	//
	// try {
	//
	// String filePath1 = request.getRealPath("/userDocuments/");
	// String fileName1 = "";
	//
	// if (getIdDoc() != null && getIdDoc().length != 0) {
	//
	// log.debug("getIdDoc().length==>" + getIdDoc().length);
	// log.debug("idDoc.length==>" + idDoc.length + " " +
	// getIdDocName().length);
	//
	// log.debug(getIdDoc()[0]);
	//
	// for (int i = 0; i < getIdDoc().length; i++) {
	//
	// if (getIdDoc()[i] != null & getIdDoc()[i].length() != 0) {
	//
	// /*
	// * int random1 = new Random().nextInt(); fileName1 =
	// * random1 + getIdDoc()[i].getName(); File fileToCreate
	// * = new File(filePath1, fileName1);
	// * FileUtils.copyFile(getIdDoc()[i], fileToCreate);
	// */
	//
	// String fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(),
	// getIdDoc()[i], getIdDocFileName()[i], CF.getIsRemoteLocation(), CF);
	//
	// pst = con.prepareStatement("INSERT INTO candidate_documents_details
	// (documents_name, documents_type, emp_id, documents_file_name) values
	// (?,?,?,?)");
	// pst.setString(1, getIdDocName()[i]);
	// pst.setString(2, getIdDocType()[i]);
	// pst.setInt(3, uF.parseToInt(getCandidateId()));
	// pst.setString(4, fileName);
	// log.debug("pst insertDocuments==>" + pst);
	// pst.execute();
	//
	// }
	//
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	//
	// }
	// }

	public void insertHobbies(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			if (getHobbyName() != null && getHobbyName().length != 0) {

				for (String h : hobbyName) {
					if (h != null && h.length() != 0 && !h.trim().equals("") && !h.equalsIgnoreCase("null")) {
						pst = con.prepareStatement("INSERT INTO candidate_hobbies_details (hobbies_name, emp_id) VALUES (?,?)");
						pst.setString(1, h);
						pst.setInt(2, uF.parseToInt(getCandidateId()));
						// log.debug("pst==>>" + pst);
						pst.execute();
						pst.close();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public void insertSkills(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		// System.out.println("getSkillName:"+getSkillName());
		try {

			for (int i = 0; i < getSkillName().length; i++) {

				if (getSkillName()[i] != null && getSkillName()[i].length() != 0 && !getSkillName()[i].trim().equals("")
						&& !getSkillName()[i].equalsIgnoreCase("null")) {

					pst = con.prepareStatement("INSERT INTO candidate_skills_description (skill_id, skills_value, emp_id) VALUES (?,?,?)");
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3, uF.parseToInt(getCandidateId()));
					// log.debug("pst==>>" + pst);
					pst.execute();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	
	public int insertCandidatePersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		int empPerId = 0;
		try {
			pst = con.prepareStatement("INSERT INTO candidate_personal_details (emp_fname, emp_lname, emp_email,emp_contactno_mob,applied_location,current_location," +
				"source_or_ref_code,emp_entry_date,org_id) VALUES (?,?,?,?, ?,?,?,?, ?)");
			pst.setString(1, getEmpFname());
			pst.setString(2, getEmpLname());
			pst.setString(3, getEmpEmail());
			pst.setString(4, getEmpContactno());
			pst.setString(5, uF.getAppendData(getStrWLocation()));
			pst.setString(6, getCurrentLocation());
			pst.setInt(7, uF.parseToInt(getRefEmpId()));
			pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.setInt(9, uF.parseToInt(getOrg_id()));
			pst.executeUpdate();
			System.out.println("insert pst ===>> " + pst);
			pst.close();

			pst = con.prepareStatement("SELECT max(emp_per_id) as emp_per_id from candidate_personal_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				setCandidateId(rs.getString("emp_per_id"));
				empPerId = uF.parseToInt(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select wlocation,org_id from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			int orgId = 0;
			int wLocId = 0;
			while (rs.next()) {
				orgId = rs.getInt("org_id");
				wLocId = rs.getInt("wlocation");
			}
			rs.close();
			pst.close();

			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			List<String> empList = new ArrayList<String>();
			if (orgId > 0 && wLocId > 0) {
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where "
						+ " epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
				sbQue.append(" and (org_id = " + orgId + " or org_id_access like '%," + orgId + ",%') and (wlocation_id = " + wLocId
						+ " or wlocation_id_access like '%," + wLocId + ",%') ");
				pst = con.prepareStatement("");
				pst.setInt(1, uF.parseToInt(hmUserTypeId.get(HRMANAGER)));
				rs = pst.executeQuery();
				while (rs.next()) {
					if (!empList.contains(rs.getString("emp_per_id").trim())) {
						empList.add(rs.getString("emp_per_id").trim());
					}
				}
				rs.close();
				pst.close();
			}
			pst = con.prepareStatement(
					"select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
			rs = pst.executeQuery();
			while (rs.next()) {
				if (!empList.contains(rs.getString("emp_per_id").trim())) {
					empList.add(rs.getString("emp_per_id").trim());
				}
			}
			rs.close();
			pst.close();

			// System.out.println("empList ===>> " + empList);
			String strDomain = request.getServerName().split("\\.")[0];
			for (int i = 0; empList != null && !empList.isEmpty() && i < empList.size(); i++) {
				if (!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empList.get(i));
					userAlerts.set_type(NEW_CANDIDATE_FILL_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
			}

			// System.out.println("empPerId ===> "+empPerId);
			getCandidateName();

			if (uF.parseToInt(getRecruitId()) > 0) {
				pst = con.prepareStatement("INSERT INTO candidate_application_details (candidate_id,recruitment_id,job_code,application_date,"
						+ "entry_date) VALUES (?,?,?,?, ?)");
				pst.setInt(1, empPerId);
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				pst.setString(3, getJobcode());
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				// pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				pst.execute();
				pst.close();

				pst = con.prepareStatement(
						"delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " + "activity_id = ?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, empPerId);
				pst.setInt(3, 0);
				pst.setInt(4, CANDI_ACTIVITY_APPLY_ID);
				pst.executeUpdate();
				pst.close();

				pst = con.prepareStatement(
						"insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, empPerId);
				pst.setString(3, "Apply for Job");
				pst.setInt(4, 0);
				pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
				pst.setInt(6, CANDI_ACTIVITY_APPLY_ID);
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}

			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return empPerId;
	}

	public void insertSkills1(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		CommonFunctions CF = new CommonFunctions();
		try {
			Map<String, String> hmSkillDetails = CF.getSkillNameMap(con);
			String skill_id = "";
			for (int i = 0; i < getSkillName().length; i++) {
				if (getSkillName()[i] != null && getSkillName()[i].length() != 0 && !getSkillName()[i].trim().equals("")
						&& !getSkillName()[i].equalsIgnoreCase("null")) {
					if (hmSkillDetails != null && hmSkillDetails.containsValue(getSkillName()[i])) {
						skill_id = getSkillId(hmSkillDetails, getSkillName()[i]);
					} else if (hmSkillDetails != null && !hmSkillDetails.containsValue(getSkillName()[i])) {
						skill_id = addSkillsDetails(con, pst, rs, getSkillName()[i], uF);
					}
					if(uF.parseToInt(skill_id )>0) {
						addCandidateSkillsDesc(con, pst, skill_id, getSkillValue()[i], getCandidateId(), "1", uF);
					}
				}
			}
			String sec_skill_id = "";
			for (int i = 0; i < getSkillNameSec().length; i++) {
				if (getSkillNameSec()[i] != null && getSkillNameSec()[i].length() != 0 && !getSkillNameSec()[i].trim().equals("")
						&& !getSkillNameSec()[i].equalsIgnoreCase("null")) {
					if (hmSkillDetails != null && hmSkillDetails.containsValue(getSkillNameSec()[i])) {
						sec_skill_id = getSkillId(hmSkillDetails, getSkillNameSec()[i]);
					} else if (hmSkillDetails != null && !hmSkillDetails.containsValue(getSkillNameSec()[i])) {
						sec_skill_id = addSkillsDetails(con, pst, rs, getSkillNameSec()[i], uF);
					}
					if(uF.parseToInt(sec_skill_id)>0) {
						addCandidateSkillsDesc(con, pst, sec_skill_id, getSkillValueSec()[i], getCandidateId(), "2", uF);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	private String addSkillsDetails(Connection con, PreparedStatement pst, ResultSet rs, String skillName, UtilityFunctions uF) {
		String skillId = null;
		try {
			pst = con.prepareStatement("INSERT INTO skills_details (skill_name, org_id) VALUES (?,?)");
			pst.setString(1, skillName);
			pst.setInt(2, uF.parseToInt(org_id));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(skill_id) as skill_id from skills_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				skillId = rs.getString("skill_id");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return skillId;
	}

	private void addCandidateSkillsDesc(Connection con, PreparedStatement pst, String skillId, String value, String candiID, String type, UtilityFunctions uF) {
		try {
			pst = con.prepareStatement("INSERT INTO candidate_skills_description (skill_id, skills_value, emp_id,skill_type) VALUES (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(skillId));
			pst.setString(2, value);
			pst.setInt(3, uF.parseToInt(candiID));
			pst.setInt(4, uF.parseToInt(type));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getSkillId(Map<String, String> hmMap, String str) {
		String skill_id = "";
		for (Entry<String, String> entry : hmMap.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(str)) {
				skill_id = entry.getKey();
			}
		}
		return skill_id;
	}

	public void insertEmpEducation(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			if (getStrHighestDegree() != null && !getStrHighestDegree().trim().equals("")) {
				insertEducationDetails("1", con, pst, rs, uF, getStrHighestDegree(), getStrHighestDegreeSubject(), getStrHighestDegreeStartDate(),
						getStrHighestDegreeCompletionDate(), getStrHighestDegreeMarksCGPA(), "", "", "", getStrHighestDegreeGrade());
			}

			if (getStrGraduateDegree() != null && !getStrGraduateDegree().trim().equals("")) {
				insertEducationDetails("2", con, pst, rs, uF, getStrGraduateDegree(), getStrGraduateDegreeSubject(), getStrGraduateDegreeStartDate(),
						getStrGraduateDegreeCompletionDate(), getStrGraduateDegreeMarksCGPA(), "", "", "", getStrGraduateDegreeGrade());
			}

			if (getStrHSC() != null && !getStrHSC().trim().equals("")) {
				insertEducationDetails("3", con, pst, rs, uF, getStrHSC(), "", getStrHSCStartDate(), getStrHSCCompletionDate(), getStrHSCMarksCGPA(),
						getStrHSCCity(), getStrHSCInstitute(), getStrHSCBoard(), getStrHSCGrade());
			}

			if (getStrSSC() != null && !getStrSSC().trim().equals("")) {
				insertEducationDetails("4", con, pst, rs, uF, getStrSSC(), "", getStrSSCStartDate(), getStrSSCCompletionDate(), getStrSSCMarksCGPA(),
						getStrSSCCity(), getStrSSCInstitute(), getStrSSCBoard(), getStrSSCGrade());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	private void insertEducationDetails(String type, Connection con, PreparedStatement pst, ResultSet rs, UtilityFunctions uF, String degree, String subject,
			String startDate, String endDate, String marks, String city, String instituteName, String universityName, String grade) throws SQLException {
		pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id,education_type) VALUES (?,?,?,?)");
		pst.setString(1, degree);
		pst.setString(2, "");
		pst.setInt(3, uF.parseToInt(getOrg_id()));
		pst.setInt(4, uF.parseToInt(type));
		pst.execute();
		pst.close();

		int newEduid = 0;
		pst = con.prepareStatement("select max(edu_id) as edu_id from educational_details");
		rs = pst.executeQuery();
		while (rs.next()) {
			newEduid = rs.getInt("edu_id");
		}
		rs.close();
		pst.close();

		pst = con.prepareStatement(
				"INSERT INTO candidate_education_details(education_id,subject, emp_id,start_date,completion_date,marks,city,institute_name,university_name,"
						+ "grade) VALUES (?,?,?,?, ?,?,?,?, ?,?)");
		pst.setInt(1, newEduid);
		pst.setString(2, subject);
		pst.setInt(3, uF.parseToInt(getCandidateId()));
		pst.setDate(4, uF.getDateFormat(startDate, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(endDate, DATE_FORMAT));
		pst.setDouble(6, uF.parseToDouble(marks));
		pst.setString(7, city);
		pst.setString(8, instituteName);
		pst.setString(9, universityName);
		pst.setString(10, grade);
		// log.debug("pst=>" + pst);
		pst.execute();
		pst.close();
	}
	
	public void insertCertificationDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			if (getCertificationTitle() != null) {//Created by Dattatray Date:21-08-21 Note: removed getCertificationCompletionYear() from condition
				for (int i = 0; i < getCertificationTitle().length; i++) {
						pst = con.prepareStatement(
								"INSERT INTO candidate_certification_details(certification_title, certification_completion_year, location, candidate_id)"
										+ "VALUES (?,?,?,?)");//Created by Dattatray Date:21-08-21 Note: emp_id to candidate_id
						pst.setString(1, getCertificationTitle()[i]);
						pst.setInt(2, uF.parseToInt(getCertificationCompletionYear()[i]));
						pst.setString(3, getLocation()[i]);
						pst.setInt(4, uF.parseToInt(getCandidateId()));
						pst.execute();
						pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	

	private String empUserTypeId;
	// private String empPerId;
	// private String empId;
	private String empCodeAlphabet;
	private String empCodeNumber;
	private String salutation;;
	private String empFname;
	private String empMname;
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

	private String[] empKra;
	private String[] empKraId;

	private String[] probationLeaves;

	private String[] hobbyName;

	private String[] otherDocument[];

	private String[] languageName;
	private String[] isRead;
	private String[] isWrite;
	private String[] isSpeak;

	private String[] degreeName;
	private String[] degreeDuration;
	private String[] completionYear;
	private String[] grade;

	/*
	 * private List<File> idDoc; private List<String> idDocFileName; private
	 * List<String> idDocStatus; private int[] docId; private String[]
	 * idDocName; private String[] idDocType;
	 */

	private String docId0;
	private File idDoc0;
	private String idDoc0FileName;
	private String idDocStatus0;
	private String idDocName0;
	private String idDocType0;

	private String[] docIdother;
	private File[] idDocOther;
	private String[] idDocOtherFileName;
	private String[] idDocStatusOther;
	private String[] idDocNameOther;
	private String[] idDocTypeOther;
	private String[] otherDocName;

	private String docId1;
	private File idDoc1;
	private String idDoc1FileName;
	private String idDocStatus1;
	private String idDocName1;
	private String idDocType1;

	private String docId2;
	private File idDoc2;
	private String idDoc2FileName;
	private String idDocStatus2;
	private String idDocName2;
	private String idDocType2;

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
	private String[] prevCompanyReportManagerPhNo;
	private String[] prevCompanyHRManager;
	private String[] prevCompanyHRManagerPhNo;
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

	// private String empDesignation;

	List<FillSalutation> salutationList;
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
	List<FillEducational> educationalList;

	String empPaymentMode;
	List<FillPayMode> paymentModeList;

	List<FillPayCycleDuration> paycycleDurationList;
	String strPaycycleDuration;

	/*
	 * public void validate() {
	 * 
	 * log.debug("Inside Validate.."); UtilityFunctions uF = new
	 * UtilityFunctions(); session = request.getSession(); CF =
	 * (CommonFunctions)session.getAttribute(CommonFunctions);
	 * loadValidateEmployee(); String referer = request.getHeader("Referer");
	 * strUserType = (String)session.getAttribute(USERTYPE);
	 * request.setAttribute(PAGE, PAddEmployee);
	 * 
	 * if (getMode()!=null && getMode().length()>0 ) {
	 * request.setAttribute(TITLE, TEditEmployee); }else
	 * request.setAttribute(TITLE, TAddEmployee);
	 * 
	 * if (referer != null) { int index1 =
	 * referer.indexOf(request.getContextPath()); int index2 =
	 * request.getContextPath().length(); referer = referer.substring(index1 +
	 * index2 + 1); } setRedirectUrl(referer);
	 * 
	 * if(strUserType.equals(IConstants.ADMIN) ||
	 * strUserType.equals(IConstants.HRMANAGER)) {
	 * 
	 * if (getEmpCodeAlphabet() != null && getEmpCodeAlphabet().length() == 0) {
	 * addFieldError("empCodeAlphabet", "Employee Code Alphabet is required"); }
	 * if (getEmpCodeNumber() != null && getEmpCodeNumber().length() == 0) {
	 * addFieldError("empCodeNumber", "Employee Code Number is required"); }
	 * 
	 * if(getEmpCodeAlphabet() != null && getEmpCodeAlphabet().length() != 0 &&
	 * getEmpCodeNumber() != null && getEmpCodeNumber().length() != 0 &&
	 * !getAutoGenerate() &&uF.parseToInt(getCandidateId())== 0) {
	 * 
	 * Map<String, String> hmEmpCode = CF.getEmpCodeMap1();
	 * 
	 * log.debug("hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber())====>" +
	 * ""+hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber()));
	 * 
	 * if(hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber()) != null) {
	 * 
	 * addFieldError("empCodeAlphabet", "Employee Code already Exist"); }
	 * 
	 * } } if (getEmpFname() != null && getEmpFname().length() == 0) {
	 * addFieldError("empFname", "Employee First Name is required"); } if
	 * (getEmpLname() != null && getEmpLname().length() == 0) {
	 * addFieldError("empLname", "Employee Last Name is required"); } if
	 * (getEmpFname() != null && getEmpLname() != null &&
	 * getEmpFname().length()>0 && getEmpLname().length()>0 &&
	 * getEmpFname().equalsIgnoreCase(getEmpLname())) {
	 * addFieldError("empFLname", "First name and last name can not be same"); }
	 * if (getUserName() != null && getUserName().length() == 0) {
	 * addFieldError("userName", "Username is required"); } if (getEmpPassword()
	 * != null && getEmpPassword().length() == 0) { addFieldError("empPassword",
	 * "Password is required"); } if (getEmpEmail() != null &&
	 * getEmpEmail().length() == 0) { addFieldError("empEmail",
	 * "Employee Email is required"); }else if(getEmpEmail() != null){ Pattern p
	 * = Pattern.compile(".+@.+\\.[a-z]+"); Matcher m=p.matcher(getEmpEmail());
	 * if(!m.matches()){ log.debug("Field Error email");
	 * addFieldError("empEmail", "Please enter valid email address"); } }
	 * 
	 * if (getEmpAddress1() != null && getEmpAddress1().length() == 0) {
	 * addFieldError("empAddress1", "Employee Address1 is required"); } if
	 * (getCountry() != null && uF.parseToInt(getCountry()) == 0) {
	 * addFieldError("country", "Select Country is required"); } if (getState()
	 * != null && uF.parseToInt(getState()) == 0) { addFieldError("state",
	 * "Select State is required"); } if (getCity() != null &&
	 * getCity().length() == 0) { addFieldError("city", "Suburb is required"); }
	 * if(getEmpEmergencyContactNo()!=null &&
	 * getEmpEmergencyContactNo().length() == 0){
	 * addFieldError("empEmergencyContactNo",
	 * "Emergency Contact No is required"); } if (getEmpGender() != null &&
	 * getEmpGender().equals("0")) { addFieldError("empGender",
	 * "Gender is required"); } if (getEmpDateOfBirth() != null &&
	 * getEmpDateOfBirth().length() == 0) { addFieldError("empDateOfBirth",
	 * "Date Of Birth is required"); }
	 * 
	 * if(strUserType.equals(IConstants.ADMIN) ||
	 * strUserType.equals(IConstants.HRMANAGER)) {
	 * 
	 * if (getEmpType() != null && getEmpType().equalsIgnoreCase("0")) {
	 * addFieldError("empType", "Select Employment Type is required"); } if
	 * (getwLocation() != null && uF.parseToInt(getwLocation()) == 0) {
	 * addFieldError("wLocation", "Select Work Location is required"); } if
	 * (getDepartment() != null && uF.parseToInt(getDepartment()) == 0) {
	 * addFieldError("department", "Department is required"); } if
	 * (getEmpGrade() != null && uF.parseToInt(getEmpGrade()) == 0) {
	 * 
	 * addFieldError("empGrade", "Grade is required"); } if (getSupervisor() !=
	 * null && uF.parseToInt(getSupervisor()) == 0) {
	 * addFieldError("supervisor", "Supervisor is required"); } if (
	 * (getEmpFname()!=null && getService()==null) || (getService() != null &&
	 * getService().length==0)) { addFieldError("service",
	 * "Cost-center is required"); } if (getRosterDependency() != null &&
	 * getRosterDependency().equalsIgnoreCase("0")) {
	 * addFieldError("rosterDependency", "Roster Dependency is required"); }
	 * 
	 * if (getAvailFrom() != null && getAvailFrom().equalsIgnoreCase("0")) {
	 * addFieldError("availFrom", "Available from time is required"); }else
	 * if(getAvailFrom() != null && uF.getTimeFormat(getAvailFrom(),
	 * CF.getStrReportTimeFormat())==null){ addFieldError("availFrom",
	 * "Please enter available from time in correct format. For example 12:00PM"
	 * ); }
	 * 
	 * if (getAvailTo() != null && getAvailTo().equalsIgnoreCase("0")) {
	 * addFieldError("availTo", "Available to time is required"); }else
	 * if(getAvailTo() != null && uF.getTimeFormat(getAvailTo(),
	 * CF.getStrReportTimeFormat())==null){ addFieldError("availFrom",
	 * "Please enter available to time in correct format. For example 12:00PM");
	 * }
	 * 
	 * if(getProbationDuration() == -1 ) { addFieldError("probationDuration",
	 * "Probation Duration time is required"); }
	 * log.debug("getProbationLeaves()=-==="+getProbationLeaves());
	 * if(((getEmpFname()!=null && getProbationLeaves()==null) ||
	 * (getProbationLeaves()!=null && getProbationLeaves().length==0))){
	 * addFieldError("probationLeaves", "Probation Leaves are required"); }
	 * 
	 * log.debug("getEmpCodeAlphabet()==>"+getEmpCodeAlphabet());
	 * 
	 * } }
	 */

	private String redirectUrl;

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getEmpMname() {
		return empMname;
	}

	public void setEmpMname(String empMname) {
		this.empMname = empMname;
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

	// public String getDesignation() {
	// return designation;
	// }
	//
	// public void setDesignation(String designation) {
	// this.designation = designation;
	// }

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

	// public List<FillDesig> getDesigList() {
	// return desigList;
	// }
	//
	// public void setDesigList(List<FillDesig> desigList) {
	// this.desigList = desigList;
	// }

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

	public List<FillSalutation> getSalutationList() {
		return salutationList;
	}

	public void setSalutationList(List<FillSalutation> salutationList) {
		this.salutationList = salutationList;
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

	public List<FillProbationDuration> getProbationDurationList() {
		return probationDurationList;
	}

	public void setProbationDurationList(List<FillProbationDuration> probationDurationList) {
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

	public String[] getPrevCompanyReportManagerPhNo() {
		return prevCompanyReportManagerPhNo;
	}

	public void setPrevCompanyReportManagerPhNo(String[] prevCompanyReportManagerPhNo) {
		this.prevCompanyReportManagerPhNo = prevCompanyReportManagerPhNo;
	}

	public String[] getPrevCompanyHRManager() {
		return prevCompanyHRManager;
	}

	public void setPrevCompanyHRManager(String[] prevCompanyHRManager) {
		this.prevCompanyHRManager = prevCompanyHRManager;
	}

	public String[] getPrevCompanyHRManagerPhNo() {
		return prevCompanyHRManagerPhNo;
	}

	public void setPrevCompanyHRManagerPhNo(String[] prevCompanyHRManagerPhNo) {
		this.prevCompanyHRManagerPhNo = prevCompanyHRManagerPhNo;
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

	public String getEmpGPFNo() {
		return empGPFNo;
	}

	public void setEmpGPFNo(String empGPFNo) {
		this.empGPFNo = empGPFNo;
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

	// public List<List<String>> selectEducation(Connection con, int empId) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// List<List<String>> alEducation = new ArrayList<List<String>>();
	//
	// try {
	//
	// pst = con.prepareStatement("SELECT * FROM candidate_education_details
	// WHERE emp_id = ?");
	// pst.setInt(1, empId);
	// log.debug("pst=>" + pst);
	// rs = pst.executeQuery();
	// System.out.println("new Date ===> " + new Date());
	// while (rs.next()) {
	//
	// List<String> alInner = new ArrayList<String>();
	//
	// alInner.add(rs.getString("degree_id"));
	// alInner.add(rs.getString("degree_name"));
	// alInner.add(rs.getString("degree_duration"));
	// alInner.add(rs.getString("completion_year"));
	// alInner.add(rs.getString("grade"));
	// alEducation.add(alInner);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// }
	// return alEducation;
	//
	// }

	// public List<List<String>> selectLanguages(Connection con, int EmpId) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// List<List<String>> alLanguages = new ArrayList<List<String>>();
	//
	// try {
	// pst = con.prepareStatement("SELECT * FROM candidate_languages_details
	// WHERE emp_id = ?");
	// pst.setInt(1, EmpId);
	// log.debug("pst=>" + pst);
	// rs = pst.executeQuery();
	// System.out.println("new Date ===> " + new Date());
	// while (rs.next()) {
	// List<String> alInner = new ArrayList<String>();
	// alInner.add(rs.getString("language_id"));
	// alInner.add(rs.getString("language_name"));
	// alInner.add(rs.getString("language_read"));
	// alInner.add(rs.getString("language_write"));
	// alInner.add(rs.getString("language_speak"));
	// alLanguages.add(alInner);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// }
	// log.debug("selectLanguages: alLanguages==>" + alLanguages);
	// return alLanguages;
	//
	// }

	// public List<List<String>> selectHobbies(Connection con, int empId) {
	//
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// List<List<String>> alHobbies = new ArrayList<List<String>>();
	//
	// try {
	// pst = con.prepareStatement("SELECT * FROM candidate_hobbies_details WHERE
	// emp_id=? ORDER BY hobbies_name");
	// pst.setInt(1, empId);
	// rs = pst.executeQuery();
	// System.out.println("new Date ===> " + new Date());
	// while (rs.next()) {
	// List<String> alInner1 = new ArrayList<String>();
	// alInner1.add(rs.getInt("hobbies_id") + "");
	// alInner1.add(rs.getString("hobbies_name"));
	// alInner1.add(rs.getInt("emp_id") + "");
	// alHobbies.add(alInner1);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// }
	// return alHobbies;
	//
	// }

	// public String selectSkills(Connection con, int EmpId, List<List<String>>
	// alSkills) {
	//
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// StringBuilder sb = new StringBuilder();
	// String str = "";
	// try {
	// pst = con.prepareStatement("SELECT * FROM candidate_skills_description
	// WHERE emp_id=? ORDER BY skills_name");
	// pst.setInt(1, EmpId);
	// rs = pst.executeQuery();
	// int count = 0;
	// System.out.println("new Date ===> " + new Date());
	// while (rs.next()) {
	//
	// List<String> alInner1 = new ArrayList<String>();
	// alInner1.add(rs.getInt("skills_id") + "");
	// if (count == 0) {
	// alInner1.add(rs.getString("skills_name"));
	// } else {
	// alInner1.add(rs.getString("skills_name"));
	// }
	// alInner1.add(rs.getString("skills_value"));
	// alInner1.add(rs.getInt("emp_id") + "");
	//
	// if (alSkills != null) {
	// alSkills.add(alInner1);
	// }
	//
	// sb.append(rs.getString("skills_name") + ((count == 0) ? " [Pri]" : "") +
	// ", ");
	//
	// count++;
	// }
	//
	// int index = sb.lastIndexOf(",");
	// if (index > 0) {
	// str = sb.substring(0, index);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// }
	// return str;
	//
	// }

	public List<List<String>> selectPrevEmploment(Connection con, int empId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alPrevEmployment = new ArrayList<List<String>>();
		UtilityFunctions uF = new UtilityFunctions();

		try {
			pst = con.prepareStatement("SELECT * FROM candidate_prev_employment WHERE emp_id = ? order by from_date");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
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
				alInner.add(rs.getString("report_manager_ph_no"));
				alInner.add(rs.getString("hr_manager"));
				alInner.add(rs.getString("hr_manager_ph_no"));
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
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}

		return alPrevEmployment;
	}

	public List<List<Object>> selectOtherDocuments(Connection con, int empId, String filePath) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<Object>> alOtherDocuments = new ArrayList<List<Object>>();
		UtilityFunctions uF = new UtilityFunctions();
		try {

			Map<String, String> hmEmpCodeName = CF.getExistingEmpNameMap(con);
			pst = con.prepareStatement("select * from candidate_documents_details where emp_id = ? and documents_type = ?");
			pst.setInt(1, empId);
			pst.setString(2, "others");

			rs = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				ArrayList<Object> alInner1 = new ArrayList<Object>();
				alInner1.add(rs.getInt("documents_id") + "");
				alInner1.add(rs.getString("documents_name"));
				alInner1.add(rs.getString("documents_type"));
				alInner1.add(rs.getInt("emp_id") + "");
				alInner1.add(rs.getString("documents_file_name"));

				alInner1.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				alInner1.add(uF.showData(hmEmpCodeName.get(rs.getString("added_by")), "-"));

				alOtherDocuments.add(alInner1);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		return alOtherDocuments;
	}

	public List<List<Object>> selectDocuments(Connection con, int empId, String filePath) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<Object>> alDocuments = new ArrayList<List<Object>>();
		UtilityFunctions uF = new UtilityFunctions();
		try {

			Map<String, String> hmEmpCodeName = CF.getExistingEmpNameMap(con);
			pst = con.prepareStatement("select * from candidate_documents_details where emp_id = ?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				ArrayList<Object> alInner1 = new ArrayList<Object>();
				alInner1.add(rs.getInt("documents_id") + "");
				alInner1.add(rs.getString("documents_name"));
				alInner1.add(rs.getString("documents_type"));
				alInner1.add(rs.getInt("emp_id") + "");
				alInner1.add(rs.getString("documents_file_name"));

				alInner1.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				alInner1.add(uF.showData(hmEmpCodeName.get(rs.getString("added_by")), "-"));

				alDocuments.add(alInner1);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
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

	public String getCandidateId() {
		return CandidateId;
	}

	public void setCandidateId(String candidateId) {
		CandidateId = candidateId;
	}

	String type;

	String stepSubmit;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public String getCandibymail() {
		return candibymail;
	}

	public void setCandibymail(String candibymail) {
		this.candibymail = candibymail;
	}

	public String getRefEmpCode() {
		return refEmpCode;
	}

	public void setRefEmpCode(String refEmpCode) {
		this.refEmpCode = refEmpCode;
	}

	public String getOtherRefSrc() {
		return otherRefSrc;
	}

	public void setOtherRefSrc(String otherRefSrc) {
		this.otherRefSrc = otherRefSrc;
	}

	public String getCandiSource() {
		return candiSource;
	}

	public void setCandiSource(String candiSource) {
		this.candiSource = candiSource;
	}

	public String getIsEmpCode() {
		return isEmpCode;
	}

	public void setIsEmpCode(String isEmpCode) {
		this.isEmpCode = isEmpCode;
	}

	public String getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(String refEmpId) {
		this.refEmpId = refEmpId;
	}

	public String getCandiCurrCTC() {
		return candiCurrCTC;
	}

	public void setCandiCurrCTC(String candiCurrCTC) {
		this.candiCurrCTC = candiCurrCTC;
	}

	public String getCandiExpectedCTC() {
		return candiExpectedCTC;
	}

	public void setCandiExpectedCTC(String candiExpectedCTC) {
		this.candiExpectedCTC = candiExpectedCTC;
	}

	public String getCandiNoticePeriod() {
		return candiNoticePeriod;
	}

	public void setCandiNoticePeriod(String candiNoticePeriod) {
		this.candiNoticePeriod = candiNoticePeriod;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public String getStrAvailability() {
		return strAvailability;
	}

	public void setStrAvailability(String strAvailability) {
		this.strAvailability = strAvailability;
	}

	public List<FillSources> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<FillSources> sourceList) {
		this.sourceList = sourceList;
	}

	public String getDocId0() {
		return docId0;
	}

	public void setDocId0(String docId0) {
		this.docId0 = docId0;
	}

	public File getIdDoc0() {
		return idDoc0;
	}

	public void setIdDoc0(File idDoc0) {
		this.idDoc0 = idDoc0;
	}

	public String getIdDoc0FileName() {
		return idDoc0FileName;
	}

	public void setIdDoc0FileName(String idDoc0FileName) {
		this.idDoc0FileName = idDoc0FileName;
	}

	public String getIdDocStatus0() {
		return idDocStatus0;
	}

	public void setIdDocStatus0(String idDocStatus0) {
		this.idDocStatus0 = idDocStatus0;
	}

	public String getIdDocName0() {
		return idDocName0;
	}

	public void setIdDocName0(String idDocName0) {
		this.idDocName0 = idDocName0;
	}

	public String getIdDocType0() {
		return idDocType0;
	}

	public void setIdDocType0(String idDocType0) {
		this.idDocType0 = idDocType0;
	}

	public String getDocId1() {
		return docId1;
	}

	public void setDocId1(String docId1) {
		this.docId1 = docId1;
	}

	public File getIdDoc1() {
		return idDoc1;
	}

	public void setIdDoc1(File idDoc1) {
		this.idDoc1 = idDoc1;
	}

	public String getIdDoc1FileName() {
		return idDoc1FileName;
	}

	public void setIdDoc1FileName(String idDoc1FileName) {
		this.idDoc1FileName = idDoc1FileName;
	}

	public String getIdDocStatus1() {
		return idDocStatus1;
	}

	public void setIdDocStatus1(String idDocStatus1) {
		this.idDocStatus1 = idDocStatus1;
	}

	public String getIdDocName1() {
		return idDocName1;
	}

	public void setIdDocName1(String idDocName1) {
		this.idDocName1 = idDocName1;
	}

	public String getIdDocType1() {
		return idDocType1;
	}

	public void setIdDocType1(String idDocType1) {
		this.idDocType1 = idDocType1;
	}

	public String getDocId2() {
		return docId2;
	}

	public void setDocId2(String docId2) {
		this.docId2 = docId2;
	}

	public File getIdDoc2() {
		return idDoc2;
	}

	public void setIdDoc2(File idDoc2) {
		this.idDoc2 = idDoc2;
	}

	public String getIdDoc2FileName() {
		return idDoc2FileName;
	}

	public void setIdDoc2FileName(String idDoc2FileName) {
		this.idDoc2FileName = idDoc2FileName;
	}

	public String getIdDocStatus2() {
		return idDocStatus2;
	}

	public void setIdDocStatus2(String idDocStatus2) {
		this.idDocStatus2 = idDocStatus2;
	}

	public String getIdDocName2() {
		return idDocName2;
	}

	public void setIdDocName2(String idDocName2) {
		this.idDocName2 = idDocName2;
	}

	public String getIdDocType2() {
		return idDocType2;
	}

	public void setIdDocType2(String idDocType2) {
		this.idDocType2 = idDocType2;
	}
	public String getotherDocumentCnt() {
		return otherDocumentCnt;
	}

	public void setotherDocumentCnt(String otherDocumentCnt) {
		this.otherDocumentCnt = otherDocumentCnt;
	}
	public File[] getIdOtherDoc() {
		return idOtherDoc;
	}

	public void setIdOtherDoc(File[] idOtherDoc) {
		this.idOtherDoc = idOtherDoc;
	}

	public String[] getOtherDocName() {
		return otherDocName;
	}

	public void setOtherDocName(String[] otherDocName) {
		this.otherDocName = otherDocName;
	}

	public String[] getDocIdother() {
		return docIdother;
	}

	public void setDocIdother(String[] docIdother) {
		this.docIdother = docIdother;
	}

	public File[] getIdDocOther() {
		return idDocOther;
	}

	public void setIdDocOther(File[] idDocOther) {
		this.idDocOther = idDocOther;
	}

	public String[] getIdDocOtherFileName() {
		return idDocOtherFileName;
	}

	public void setIdDocOtherFileName(String[] idDocOtherFileName) {
		this.idDocOtherFileName = idDocOtherFileName;
	}

	public String[] getIdDocStatusOther() {
		return idDocStatusOther;
	}

	public void setIdDocStatusOther(String[] idDocStatusOther) {
		this.idDocStatusOther = idDocStatusOther;
	}

	public String[] getIdDocNameOther() {
		return idDocNameOther;
	}

	public void setIdDocNameOther(String[] idDocNameOther) {
		this.idDocNameOther = idDocNameOther;
	}

	public String[] getIdDocTypeOther() {
		return idDocTypeOther;
	}

	public void setIdDocTypeOther(String[] idDocTypeOther) {
		this.idDocTypeOther = idDocTypeOther;
	}

	public String getFrombgverify() {
		return frombgverify;
	}

	public void setFrombgverify(String frombgverify) {
		this.frombgverify = frombgverify;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String[] getSkillNameSec() {
		return skillNameSec;
	}

	public void setSkillNameSec(String[] skillNameSec) {
		this.skillNameSec = skillNameSec;
	}

	public String[] getSkillValueSec() {
		return skillValueSec;
	}

	public void setSkillValueSec(String[] skillValueSec) {
		this.skillValueSec = skillValueSec;
	}

	public String[] getCandiTitle() {
		return candiTitle;
	}

	public void setCandiTitle(String[] candiTitle) {
		this.candiTitle = candiTitle;
	}

	public String[] getCandiTitleValue() {
		return candiTitleValue;
	}

	public void setCandiTitleValue(String[] candiTitleValue) {
		this.candiTitleValue = candiTitleValue;
	}

	public String getPrefLocation() {
		return prefLocation;
	}

	public void setPrefLocation(String prefLocation) {
		this.prefLocation = prefLocation;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public String getStrHighestDegree() {
		return strHighestDegree;
	}

	public void setStrHighestDegree(String strHighestDegree) {
		this.strHighestDegree = strHighestDegree;
	}

	public String getStrHighestDegreeSubject() {
		return strHighestDegreeSubject;
	}

	public void setStrHighestDegreeSubject(String strHighestDegreeSubject) {
		this.strHighestDegreeSubject = strHighestDegreeSubject;
	}

	public String getStrHighestDegreeStartDate() {
		return strHighestDegreeStartDate;
	}

	public void setStrHighestDegreeStartDate(String strHighestDegreeStartDate) {
		this.strHighestDegreeStartDate = strHighestDegreeStartDate;
	}

	public String getStrHighestDegreeCompletionDate() {
		return strHighestDegreeCompletionDate;
	}

	public void setStrHighestDegreeCompletionDate(String strHighestDegreeCompletionDate) {
		this.strHighestDegreeCompletionDate = strHighestDegreeCompletionDate;
	}

	public String getStrHighestDegreeGrade() {
		return strHighestDegreeGrade;
	}

	public void setStrHighestDegreeGrade(String strHighestDegreeGrade) {
		this.strHighestDegreeGrade = strHighestDegreeGrade;
	}

	public String getStrHighestDegreeMarksCGPA() {
		return strHighestDegreeMarksCGPA;
	}

	public void setStrHighestDegreeMarksCGPA(String strHighestDegreeMarksCGPA) {
		this.strHighestDegreeMarksCGPA = strHighestDegreeMarksCGPA;
	}

	public String getStrGraduateDegree() {
		return strGraduateDegree;
	}

	public void setStrGraduateDegree(String strGraduateDegree) {
		this.strGraduateDegree = strGraduateDegree;
	}

	public String getStrGraduateDegreeSubject() {
		return strGraduateDegreeSubject;
	}

	public void setStrGraduateDegreeSubject(String strGraduateDegreeSubject) {
		this.strGraduateDegreeSubject = strGraduateDegreeSubject;
	}

	public String getStrGraduateDegreeStartDate() {
		return strGraduateDegreeStartDate;
	}

	public void setStrGraduateDegreeStartDate(String strGraduateDegreeStartDate) {
		this.strGraduateDegreeStartDate = strGraduateDegreeStartDate;
	}

	public String getStrGraduateDegreeCompletionDate() {
		return strGraduateDegreeCompletionDate;
	}

	public void setStrGraduateDegreeCompletionDate(String strGraduateDegreeCompletionDate) {
		this.strGraduateDegreeCompletionDate = strGraduateDegreeCompletionDate;
	}

	public String getStrGraduateDegreeGrade() {
		return strGraduateDegreeGrade;
	}

	public void setStrGraduateDegreeGrade(String strGraduateDegreeGrade) {
		this.strGraduateDegreeGrade = strGraduateDegreeGrade;
	}

	public String getStrGraduateDegreeMarksCGPA() {
		return strGraduateDegreeMarksCGPA;
	}

	public void setStrGraduateDegreeMarksCGPA(String strGraduateDegreeMarksCGPA) {
		this.strGraduateDegreeMarksCGPA = strGraduateDegreeMarksCGPA;
	}

	public String getStrHSC() {
		return strHSC;
	}

	public void setStrHSC(String strHSC) {
		this.strHSC = strHSC;
	}

	public String getStrHSCBoard() {
		return strHSCBoard;
	}

	public void setStrHSCBoard(String strHSCBoard) {
		this.strHSCBoard = strHSCBoard;
	}

	public String getStrHSCInstitute() {
		return strHSCInstitute;
	}

	public void setStrHSCInstitute(String strHSCInstitute) {
		this.strHSCInstitute = strHSCInstitute;
	}

	public String getStrHSCCity() {
		return strHSCCity;
	}

	public void setStrHSCCity(String strHSCCity) {
		this.strHSCCity = strHSCCity;
	}

	public String getStrHSCStartDate() {
		return strHSCStartDate;
	}

	public void setStrHSCStartDate(String strHSCStartDate) {
		this.strHSCStartDate = strHSCStartDate;
	}

	public String getStrHSCCompletionDate() {
		return strHSCCompletionDate;
	}

	public void setStrHSCCompletionDate(String strHSCCompletionDate) {
		this.strHSCCompletionDate = strHSCCompletionDate;
	}

	public String getStrHSCGrade() {
		return strHSCGrade;
	}

	public void setStrHSCGrade(String strHSCGrade) {
		this.strHSCGrade = strHSCGrade;
	}

	public String getStrHSCMarksCGPA() {
		return strHSCMarksCGPA;
	}

	public void setStrHSCMarksCGPA(String strHSCMarksCGPA) {
		this.strHSCMarksCGPA = strHSCMarksCGPA;
	}

	public String getStrSSC() {
		return strSSC;
	}

	public void setStrSSC(String strSSC) {
		this.strSSC = strSSC;
	}

	public String getStrSSCInstitute() {
		return strSSCInstitute;
	}

	public void setStrSSCInstitute(String strSSCInstitute) {
		this.strSSCInstitute = strSSCInstitute;
	}

	public String getStrSSCCity() {
		return strSSCCity;
	}

	public void setStrSSCCity(String strSSCCity) {
		this.strSSCCity = strSSCCity;
	}

	public String getStrSSCStartDate() {
		return strSSCStartDate;
	}

	public void setStrSSCStartDate(String strSSCStartDate) {
		this.strSSCStartDate = strSSCStartDate;
	}

	public String getStrSSCCompletionDate() {
		return strSSCCompletionDate;
	}

	public void setStrSSCCompletionDate(String strSSCCompletionDate) {
		this.strSSCCompletionDate = strSSCCompletionDate;
	}

	public String getStrSSCGrade() {
		return strSSCGrade;
	}

	public void setStrSSCGrade(String strSSCGrade) {
		this.strSSCGrade = strSSCGrade;
	}

	public String getStrSSCMarksCGPA() {
		return strSSCMarksCGPA;
	}

	public void setStrSSCMarksCGPA(String strSSCMarksCGPA) {
		this.strSSCMarksCGPA = strSSCMarksCGPA;
	}

	public String getStepSubmit() {
		return stepSubmit;
	}

	public void setStepSubmit(String stepSubmit) {
		this.stepSubmit = stepSubmit;
	}

	public String[] getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String[] strWLocation) {
		this.strWLocation = strWLocation;
	}

	public String getStrSSCBoard() {
		return strSSCBoard;
	}

	public void setStrSSCBoard(String strSSCBoard) {
		this.strSSCBoard = strSSCBoard;
	}

	public String[] getCertificationTitle() {
		return certificationTitle;
	}

	public void setCertificationTitle(String[] certificationTitle) {
		this.certificationTitle = certificationTitle;
	}

	public String[] getLocation() {
		return location;
	}

	public void setLocation(String[] location) {
		this.location = location;
	}

	public String[] getCertificationCompletionYear() {
		return certificationCompletionYear;
	}

	public void setCertificationCompletionYear(String[] certificationCompletionYear) {
		this.certificationCompletionYear = certificationCompletionYear;
	}

}