package com.konnect.jpms.task;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.AddEmployeeMode;
import com.konnect.jpms.recruitment.FillEducational;
import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillDegreeDuration;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillResourceType;
import com.konnect.jpms.select.FillSalutation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author konnect
 *
 */
public class AddPeople extends ActionSupport implements ServletRequestAware, IStatements {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5411324943271973736L;
	
	HttpSession session;
	String strUserType = null;
	String strProductType =  null;
	String strSessionEmpId = null; 
	CommonFunctions CF = null;
	
	private String mode;
	private int empId;
	String org_id;
	String operation;
	Boolean autoGenerate = false; 
	private int step;
	public String dobYear="1977";
	
	List<FillSalutation> salutationList;
	List<FillGender> empGenderList;
	
	List<FillCountry> countryList;
	List<FillState> stateList;
	
	List<FillSkills> skillsList;
	List<FillEducational> educationalList;
	List<FillDegreeDuration> degreeDurationList;
	List<FillYears> yearsList;

	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillServices> serviceList;
	List<FillDepartment> deptList;
	List<FillLevel> levelList;
	List<FillDesig> desigList;
	List<FillEmployee> HRList;
	List<FillEmployee> HodList;
	List<FillEmployee> supervisorList;
	
	String salutation;
	String empFname;
	String empMname;
	String empLname;
	String empDateOfBirth;
	String empGender;
	String corporateMobile;
	String corporateDesk;
	String corporateEmail;
	String corporateSkype;
	String personalMobile;
	String residenceNo;
	String empEmail;
	String strEmpORContractor;
	String houseNo;
	String streetAddress;
	String suburb;
	String city;
	String country;
	String state;
	String pincode;
	String relevantExperience;
	String totalExperience;
	
	String[] skillName;
	String[] skillValue;
	
	String[] degreeName;
	String[] degreeDuration;
	String[] completionYear;
	String[] grade;
	
	String empCodeAlphabet;
	String empCodeNumber;

	private String empImageFileName;
	private File empImage;
	
	List<FillEmploymentType> empTypeList;
	
	String empContractor;
	String empStartDate;
	String empType;
	String orgId;
	String wLocation;
	String[] service;
	String department;
	String strLevel;
	String strDesignation;
	String supervisor;
	String empProfile;
	String empGrade;
	
	List<FillResourceType> resourceList;
	
	private boolean approvedFlag;
	
	String userType;
	String userid;
	
	String[] orgId1;
	String[] wLocation1;
	
	List<FillUserType> userTypeList;
	List<FillOrganisation> orgList1;
	List<FillWLocation> wLocationList1;
	
	String hod;
	String HR;
//	String isCXO;
//	String isHOD; 
	private String strCXOHOD;
	String defaultCXO;
	String defaultHOD;
	
	String[] locationCXO;
	List<String> cxoLocationAccess = new ArrayList<String>();
	
	List<String> userTypeOrgValue = new ArrayList<String>();
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute("dobYear",dobYear);
		request.setAttribute(PAGE, "/jsp/task/AddPeople.jsp");
		request.setAttribute(TITLE, "Add New Resource");
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		if (getOperation()!=null && getOperation().equals("U") && getEmpId() != 0 && getMode() != null) {
			request.setAttribute(TITLE, "Edit Resource");
			viewPeople(uF);
			loadValidatePeople(uF);
			loadUserType(uF);
			return SUCCESS;
			
		} else if (getEmpId()!=0 && getMode()!=null && (getMode().equals("profile") || getMode().equals("onboard") || getMode().equals("report"))) {
			if(getOperation()==null || !getOperation().equals("EO")) {
				updatePeople(uF);
			}
			
			if (getMode().equals("report")) {
				return REPORT;
			} else if (getMode().equals("profile")) {
				return PROFILE;
			} else {
				viewPeople(uF);
				loadValidatePeople(uF);
				loadUserType(uF);
				
				if(getStep() == 1) {
					setStep(2);
				} else if(getStep() == 2) {
					setStep(3);
				}
				
				if(getStep() == 3) { 
					return VIEW;
				}else {
					loadValidatePeople(uF);
					loadUserType(uF);
					return SUCCESS;
				}
				
			} 
			
		} else if (getStep()!= 0) {
			insertPeople(uF);
			loadValidatePeople(uF);
			loadUserType(uF);
			if(getStep() == 1) {
				setStep(2);
			} else if(getStep() == 2) {
				setStep(3);
			}
			
			if(getStep() == 3) { 
				
				if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)){
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_NEW_EMPLOYEE, CF);
					nF.setDomain(strDomain); 
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					nF.setStrEmpId(getEmpId()+"");
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.sendNotifications();
					
				}
				
				return VIEW;
			} else {
				loadValidatePeople(uF);
				loadUserType(uF);
				return SUCCESS;
			}
			
		} else if(getStep() == 0) {
			setStep(1);
			request.setAttribute(PAGE, "/jsp/task/AddPeople.jsp");
			request.setAttribute(TITLE, "Add New Resource");
			
			loadValidatePeople(uF);
			loadUserType(uF);
		}
		
		return LOAD;
	}
	
	private void loadUserType(UtilityFunctions uF) {
		
		
		userTypeList = new FillUserType(request).fillUserType();
		wLocationList1 = new FillWLocation(request).fillWLocation();
		orgList1 = new FillOrganisation(request).fillOrganisation();
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from user_details where emp_id=? ");
			pst.setInt(1, getEmpId());
			rs = pst.executeQuery();
			while(rs.next()){
				setUserType(uF.showData(rs.getString("usertype_id"), ""));
				setUserid(rs.getString("user_id"));
				List<String> orgValue1 = new ArrayList<String>();
				if (rs.getString("org_id_access") == null) {
					orgValue1.add("0");
				} else {
					orgValue1 = Arrays.asList(rs.getString("org_id_access").split(","));
				}
				if (orgValue1 != null) {
					for (int i = 0; i < orgValue1.size(); i++) {
						if(uF.parseToInt(orgValue1.get(i)) > 0) {
							userTypeOrgValue.add(orgValue1.get(i).trim());
						}
					}
				} else {
					userTypeOrgValue.add("0");
				}
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(getDefaultCXO())==1 || uF.parseToInt(getDefaultHOD())==1) {
				setUserType("");
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void updatePeople(UtilityFunctions uF) {
		try {
			if(getStep() == 1) {
				if(getEmpId() > 0) {
					if(getEmpImage()!=null){
						uploadImage(uF,getEmpId());
					}
					if(getEmpFname()!=null){
						updateEmpPersonalDetails(uF);
						
					}
					updateSkills(uF);
					updateEmpEducation(uF);
				}
			}else if(getStep() == 2) {
				updateEmpInfo(uF);
				updateEmpOfficialDetailsAdmin(uF);
				updateUsertype(uF);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void updateEmpEducation(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			if(getDegreeName()!=null && getDegreeName().length!= 0) {
				
				pst = con.prepareStatement("DELETE FROM education_details WHERE emp_id = ?");
				pst.setInt(1, getEmpId());
				pst.execute();
				pst.close();
				
				String[] degreeNameOther = request.getParameterValues("degreeNameOther");
				for(int i=0; i<getDegreeName().length; i++) {
					
					if(getDegreeName()[i].length()!=0) {
						
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
							
							pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id)" +
							"VALUES (?,?,?,?,?)");
							pst.setInt(1, newEduid);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, getEmpId());
							pst.execute();
							pst.close();
						} else {
							pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id)" +
							"VALUES (?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, getEmpId());
							pst.execute();
							pst.close();
						}
						
						
					}
					
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void updateSkills(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteSkills);
			pst.setInt(1, getEmpId());
			pst.execute();
			pst.close();
			
			for(int i=0; getSkillName()!= null && i<getSkillName().length; i++) {
				if(getSkillName()[i].length()!=0 && getSkillValue()[i].length()!=0) {
					pst = con.prepareStatement(insertSkill);
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3,getEmpId());
					if(isDebug){
						pst.execute();
						pst.close();
					}
					
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void updateEmpPersonalDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
			
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update employee_personal_details set emp_fname=?,emp_mname=?, emp_lname=?, emp_email=?,emp_address1=?,emp_city_id=?," +
					"emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, " +
					"emp_pincode_tmp=?,emp_date_of_birth=?,salutation=?,approved_flag=?, emp_entry_date=?, emp_status=?,emp_contactno_mob=?,emp_contactno=?," +
					"skype_id=?,emp_email_sec=?,corporate_mobile=?,corporate_desk=?,house_no=?,suburb=?,relevant_experience=?,total_experience=?,emp_gender=? " +
					"where emp_per_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getEmpFname());
			pst.setString(2, getEmpMname());
			pst.setString(3, getEmpLname());
			pst.setString(4, getEmpEmail());
			
			pst.setString(5, getStreetAddress());
			pst.setString(6, getCity());
			pst.setInt(7, uF.parseToInt(getState()));
			pst.setInt(8, uF.parseToInt(getCountry()));
			pst.setString(9, getPincode());
			
			pst.setString(10, getStreetAddress());
			pst.setString(11, getCity());
			pst.setInt(12, uF.parseToInt(getState()));
			pst.setInt(13, uF.parseToInt(getCountry()));
			pst.setString(14, getPincode());
			
			pst.setDate(15,  uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setString(16, getSalutation());
			pst.setBoolean(17, true);
			pst.setTimestamp(18, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setString(19, PROBATION);
			
			pst.setString(20, getPersonalMobile());
			pst.setString(21, getResidenceNo());
			pst.setString(22, getCorporateSkype());
			pst.setString(23, getCorporateEmail());
			pst.setString(24, getCorporateMobile());
			pst.setString(25, getCorporateDesk());
			pst.setString(26, getHouseNo());
			pst.setString(27, getSuburb());
			pst.setDouble(28, uF.parseToDouble(getRelevantExperience()));
			pst.setDouble(29, uF.parseToDouble(getTotalExperience()));
			pst.setString(30, getEmpGender());
			pst.setInt(31, getEmpId());
//			System.out.println("pst=====>"+pst);
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void viewPeople(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		
		List<List<String>> alSkills = new ArrayList<List<String>>();
		List<List<String>> alEducation;
		
		try {
			con = db.makeConnection(con);
			
			setEmpPersonalDetails(con, uF);
			setEmpOfficialDetails(con, uF);
			
			alSkills = CF.selectSkills(con,getEmpId()); 
			alEducation = CF.selectEducation(con,getEmpId());
			
			/**
			 * Cost To Company start
			 * */
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, ""+getEmpId());
			MyProfile profile = new MyProfile();
			profile.CF = CF;
			profile.request = request;
			profile.setEmpId(""+getEmpId()); 
			if(((String)hmEmpProfile.get("JOINING_DATE") !=null) && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equals("")) {
				profile.getSalaryHeadsforEmployee(con, uF, getEmpId(), hmEmpProfile);
			
				List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
				double grossAmount = 0.0d;
				double grossYearAmount = 0.0d;
				double netAmount = 0.0d;
				double netYearAmount = 0.0d;
				for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if(innerList.get(1).equals("E")) {
						grossAmount +=uF.parseToDouble(innerList.get(2));
						grossYearAmount +=uF.parseToDouble(innerList.get(3));
					}
				}
				Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
				if(hmContribution == null) hmContribution = new HashMap<String, String>();
				double dblMonthContri = 0.0d;
				double dblAnnualContri = 0.0d;
				boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
				boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
				boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
				if(isEPF || isESIC || isLWF){
					if(isEPF){
						double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
						double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
						dblMonthContri += dblEPFMonth;
						dblAnnualContri += dblEPFAnnual;
					}
					if(isESIC){
						double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
						double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
						dblMonthContri += dblESIMonth;
						dblAnnualContri += dblESIAnnual;
					}
					if(isLWF){
						double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
						double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
						dblMonthContri += dblLWFMonth;
						dblAnnualContri += dblLWFAnnual;
					}
				}
				
				double dblCTCMonthly = grossAmount + dblMonthContri;
				double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
				
				List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
				if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
				int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
				if(nAnnualVariSize > 0){
					double grossAnnualAmount = 0.0d;
					double grossAnnualYearAmount = 0.0d;
					for(int i = 0; i < nAnnualVariSize; i++){
						List<String> innerList = salaryAnnualVariableDetailsList.get(i);
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAnnualAmount += dblEarnMonth;
						grossAnnualYearAmount += dblEarnAnnual;
					}
					dblCTCMonthly += grossAnnualAmount;
					dblCTCAnnualy += grossAnnualYearAmount;
				}
				
				netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
				netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
				request.setAttribute("MONTHLY_AMT", uF.formatIntoTwoDecimal(netAmount));
				request.setAttribute("ANNUALY_AMT", uF.formatIntoTwoDecimal(netYearAmount));
			}
			/**
			 * Cost to company end
			 * */
			
			request.setAttribute("alSkills", alSkills);
			request.setAttribute("alEducation", alEducation);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}


	}

	private void setEmpOfficialDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id where emp_id = ?");
			pst.setInt(1, getEmpId());
			rs = pst.executeQuery();
			if (rs.next()) {
//				setEmpContractor(rs.getString("emp_contractor"));
				setEmpType(rs.getString("emptype"));
				
				setOrgId(rs.getString("org_id"));
				setwLocation(rs.getString("wlocation_id"));
				if(rs.getString("service_id")!=null){
					setService(rs.getString("service_id").split(","));	
				}
				setDepartment(rs.getString("depart_id"));
				setStrLevel(hmEmpLevelMap.get(rs.getString("emp_id")));
				setStrDesignation(rs.getString("designation_id"));
				setEmpGrade(rs.getString("grade_id"));
				
				setSupervisor(""+rs.getInt("supervisor_emp_id"));
				setHR(""+rs.getInt("emp_hr"));
				setHod(""+rs.getInt("hod_emp_id"));
				setEmpContractor(uF.showData(rs.getString("emp_contractor"), "1"));
				request.setAttribute("EMP_OR_CONTRACTOR", uF.showData(rs.getString("emp_contractor"), "1"));
//				setEmpProfile(rs.getString("emprofile"));
				
				setDefaultCXO(rs.getBoolean("is_cxo") ? "1" : "0");
				setDefaultHOD(rs.getBoolean("is_hod") ? "1" : "0");
				String departName = CF.getDepartMentNameById(con, rs.getString("depart_id"));
				request.setAttribute("HOD_DEPART_NAME", "Department: "+uF.showData(departName, ""));
				
				if(rs.getBoolean("is_hod")) {
					setStrCXOHOD("2");
				}
				if(rs.getBoolean("is_cxo")) {
					setStrCXOHOD("1");
					List<String> accessLocIds = CF.getCXOLocationAccessIds(con, uF, getEmpId());
					if (accessLocIds != null && !accessLocIds.isEmpty()) {
						for (int i = 0; i<accessLocIds.size(); i++) {
							cxoLocationAccess.add(accessLocIds.get(i).trim());
						}
					} else {
						cxoLocationAccess.add("0");
					}
				}
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private void setEmpPersonalDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select emp_contractor,org_id from employee_official_details where emp_id = ?");
			pst.setInt(1, getEmpId());
			rs = pst.executeQuery();
			if (rs.next()) {
				setEmpContractor(uF.showData(rs.getString("emp_contractor"), "1"));
				setOrgId(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, getEmpId());
			rs = pst.executeQuery();
			if (rs.next()) {
				setEmpId(rs.getInt("emp_per_id"));
				
				setEmpCode(rs.getString("empcode"), con, uF);
				
				setSalutation(rs.getString("salutation"));
				setEmpFname(rs.getString("emp_fname"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = rs.getString("emp_mname");
					}
				}
				
				setEmpMname(strEmpMName);
				setEmpLname(rs.getString("emp_lname"));
				setEmpDateOfBirth(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				setEmpGender(rs.getString("emp_gender"));
				
				setCorporateMobile(rs.getString("corporate_mobile"));
				setCorporateDesk(rs.getString("corporate_desk"));
				setCorporateEmail(rs.getString("emp_email_sec"));
				setCorporateSkype(rs.getString("skype_id"));
				
				setPersonalMobile(rs.getString("emp_contactno_mob"));
				setResidenceNo(rs.getString("emp_contactno"));
				setEmpEmail(rs.getString("emp_email"));
				
				setHouseNo(rs.getString("house_no"));
				setStreetAddress(rs.getString("emp_address1"));
				setSuburb(rs.getString("suburb"));
				setCity(rs.getString("emp_city_id"));
				setCountry(rs.getString("emp_country_id"));
				setState(rs.getString("emp_state_id"));
				setPincode(rs.getString("emp_pincode"));
				setRelevantExperience(rs.getString("relevant_experience"));
				setTotalExperience(rs.getString("total_experience"));
				
				setEmpStartDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				
				request.setAttribute("strImage", rs.getString("emp_image"));
				request.setAttribute("EMPLOYEE_EMAIL", rs.getString("emp_email"));
				request.setAttribute("EMPLOYEE_EMAIL2", rs.getString("emp_email_sec"));
				request.setAttribute("EMP_CODE", rs.getString("empcode"));

				dobYear = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy");
				request.setAttribute("dobYear",dobYear);
				request.setAttribute("strEmpName", uF.showData(rs.getString("emp_fname"),"") +" "+strEmpMName+" "+uF.showData(rs.getString("emp_mname"),"") +" "+uF.showData(rs.getString("emp_lname"),"") );
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setEmpCode(String empCode, Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			String empCodeAlpha = null;
			pst = con.prepareStatement("SELECT emp_code_auto_generate,emp_code_alpha,contractor_code_alpha,emp_code_numeric FROM org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				setAutoGenerate(uF.parseToBoolean(rs.getString("emp_code_auto_generate")));
				empCodeAlpha = rs.getString("emp_code_alpha");
				if(uF.parseToInt(getEmpContractor()) == 2) {
					empCodeAlpha = rs.getString("contractor_code_alpha");
				}
			}
			rs.close();
			pst.close();
			
			if(empCodeAlpha == null) {
				empCodeAlpha = "";
			}
			//System.out.println("empCode ===>> " + empCode);
			
			if(empCode!=null && empCode.contains(empCodeAlpha)) {
				
				setEmpCodeAlphabet(empCodeAlpha);
				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
				//System.out.println("if setEmpCode -- getEmpCodeAlphabet() ===>> " + getEmpCodeAlphabet() + "  getEmpCodeNumber() ===>> " + getEmpCodeNumber());
				
			} else if(empCode!=null && empCode.length()==0 && getAutoGenerate()) {
				generateEmpCode(con);
				
			} else if(empCode!=null && empCodeAlpha!=null && empCode.length()>empCodeAlpha.length()) {
				setEmpCodeAlphabet(empCode.substring(0, empCodeAlpha.length()));
				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
				//System.out.println("else if setEmpCode -- getEmpCodeAlphabet() ===>> " + getEmpCodeAlphabet() + "  getEmpCodeNumber() ===>> " + getEmpCodeNumber());
				
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	private void generateEmpCode(Connection con) {
		
//		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Database db = new Database();
		String empCodeAlpha = "" , empCodeNum = ""; 
//		Map<String, String> hmSettings;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
//			hmSettings = CF.getSettingsMap(con);
//			empCodeAlpha = hmSettings.get(O_EMP_CODE_ALPHA);
//			if(uF.parseToInt(getEmpContractor()) == 2) {
//				empCodeAlpha = hmSettings.get(O_CONTRACTOR_CODE_ALPHA);
//			}
//			empCodeNum = hmSettings.get(O_EMP_CODE_NUM);
			
			pst = con.prepareStatement("SELECT emp_code_auto_generate,emp_code_alpha,contractor_code_alpha,emp_code_numeric FROM org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				setAutoGenerate(uF.parseToBoolean(rs.getString("emp_code_auto_generate")));
				empCodeAlpha = rs.getString("emp_code_alpha");
				if(uF.parseToInt(getEmpContractor()) == 2) {
					empCodeAlpha = rs.getString("contractor_code_alpha");
				}
				empCodeNum = rs.getString("emp_code_numeric");
			}
			rs.close();
			pst.close();
			
//			if(getAutoGenerate()) {
				pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode like ? order by emp_per_id desc limit 1");
				pst.setString(1, empCodeAlpha+"%");
				rs = pst.executeQuery();
				boolean empcodeFlag = false;
				while(rs.next()) {
					empcodeFlag = true;
					String strEmpCode = rs.getString("empcode");
					String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
//					log.debug("code Number===>"+strEmpCodeNum);
					empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";
					//System.out.println("empCodeNum ===>> " + empCodeNum);
					getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
				}
				rs.close();
				pst.close();
				
				if(!empcodeFlag) {
					setEmpCodeAlphabet(empCodeAlpha);
					setEmpCodeNumber(empCodeNum);
					request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
					request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
				}
//				System.out.println("generateEmpCode final ----- getEmpCodeAlphabet() ===>> " + getEmpCodeAlphabet() + "  getEmpCodeNumber() ===>> " + getEmpCodeNumber());
//
//			request.setAttribute("EMPLOYEE_CODE", empCodeAlpha+empCodeNum);
//			setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
			/***
			 * This position of code changed on 26-04-2012 for always displaying the auto generated code
			 */
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private void getLatestEmpCode(Connection con, UtilityFunctions uF, String empCodeAlpha, String empCodeNum) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			boolean flag = false;
				pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode = ? ");
				pst.setString(1, empCodeAlpha+empCodeNum);
				rs = pst.executeQuery();
				
				while(rs.next()) {
					flag = true;
					String strEmpCode = rs.getString("empcode");
					String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
					
					empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";
//					System.out.println("generateEmpCode empCodeNum ===>> " + empCodeNum);
					getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
				}
				rs.close();
				pst.close();
				
				if(!flag) {
//					System.out.println("empCodeNum 111 ===>> " + empCodeNum);
					setEmpCodeAlphabet(empCodeAlpha);
					setEmpCodeNumber(empCodeNum);
					request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
					request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
				}
			
			/***
			 * This position of code changed on 26-04-2012 for always displaying the auto generated code
			 */
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private void insertPeople(UtilityFunctions uF) {
		try {
			if(getStep() == 1) {
				if(getEmpId()==0) {
					setEmpId(insertEmpPersonalDetails(uF, CF));
					if(getEmpId() > 0) {
						insertUser(uF, getEmpId());
						uploadImage(uF,getEmpId());
						insertSkills(uF);
						insertEmpEducation(uF);
					}
				}
				
				session.setAttribute("EMPNAME_P", getEmpFname()+" "+ getEmpMname()+" "+getEmpLname());
				session.setAttribute("EMPID_P", getEmpId()+"");
				
			} else if(getStep() == 2) {
				updateEmpInfo(uF);  
				updateUsertype(uF);
				updateEmpOfficialDetailsAdmin(uF); 
			}
			
		} catch (Exception e) { 
			e.printStackTrace();
		} 
	}

	private void updateUsertype(UtilityFunctions uF) {

		Connection con = null;		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			if(hmUserType != null && (hmUserType.get(getUserType()).equals(HRMANAGER) || hmUserType.get(getUserType()).equals(ACCOUNTANT) || hmUserType.get(getUserType()).equals(CEO))){
				StringBuilder sbOrgId = null;
				for(int i=0; getOrgId1() != null && i<getOrgId1().length; i++) {
					if(uF.parseToInt(getOrgId1()[i]) > 0) {
						if(sbOrgId == null) {
							sbOrgId = new StringBuilder();
							sbOrgId.append(","+getOrgId1()[i]+",");
						} else {
							sbOrgId.append(getOrgId1()[i]+",");
						}
					}
				}
				
				StringBuilder sbwLocId = null;
				for(int i=0; wLocation1 != null && i<wLocation1.length; i++) {
					if(uF.parseToInt(wLocation1[i]) > 0) {
						if(sbwLocId == null) {
							sbwLocId = new StringBuilder();
							sbwLocId.append("," + wLocation1[i]+",");
						} else {
							sbwLocId.append(wLocation1[i]+",");
						}
					}
				}
				if(sbOrgId == null) sbOrgId = new StringBuilder();
				
				if(getOrgId1() != null && getOrgId1().length > 0 && (wLocation1 == null || wLocation1.length==0)) {
					
					String strOrgId = sbOrgId.length() > 0 ? sbOrgId.substring(1, sbOrgId.length()-1).toString() : "";
					if(strOrgId.length() > 0){
						pst = con.prepareStatement("select wlocation_id from work_location_info where org_id in("+ strOrgId +")");
						rs = pst.executeQuery();
						while (rs.next()) {
							if(rs.getInt("wlocation_id") > 0) {
								if(sbwLocId == null) {
									sbwLocId = new StringBuilder();
									sbwLocId.append("," + rs.getInt("wlocation_id") +",");
								} else {
									sbwLocId.append(rs.getInt("wlocation_id") +",");
								}
							}
						}
						rs.close();
						pst.close();
					}
				}
				
				if(sbwLocId == null) sbwLocId = new StringBuilder();
				
				pst = con.prepareStatement("update user_details set usertype_id=?, org_id_access=?, wlocation_id_access=? where user_id=? and emp_id=? ");
				pst.setInt(1, uF.parseToInt(getUserType()));
				pst.setString(2, sbOrgId.toString());
				pst.setString(3, sbwLocId.toString());
				pst.setInt(4, uF.parseToInt(getUserid()));
				pst.setInt(5, getEmpId());
				pst.execute();
				pst.close();
			} else {		
				pst = con.prepareStatement("update user_details set usertype_id=?, org_id_access=?, wlocation_id_access=? where user_id=? and emp_id=? ");
				pst.setInt(1, uF.parseToInt(getUserType()));
				pst.setString(2, null);
				pst.setString(3, null);
				pst.setInt(4, uF.parseToInt(getUserid()));
				pst.setInt(5, getEmpId());
				pst.execute();
				pst.close();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private void updateEmpOfficialDetailsAdmin(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbServices = null;
			for (int i = 0; getService() != null && i < getService().length; i++) {
				if (uF.parseToInt(getService()[i]) > 0) {
					if(sbServices == null) {
						sbServices = new StringBuilder();
						sbServices.append("," + getService()[i] + ",");
					} else {
						sbServices.append(getService()[i] + ",");
					}
				}
			}
			if(sbServices == null) {
				sbServices = new StringBuilder();
			}
			
			pst = con.prepareStatement("select * from grades_details where designation_id=? order by grade_id limit 1");
			pst.setInt(1, uF.parseToInt(getStrDesignation()));
			rs = pst.executeQuery();
			while(rs.next()){
				setEmpGrade(rs.getString("grade_id"));
			}
			rs.close();
			pst.close();
			
			int nSuperWiserId = 0;
			pst = con.prepareStatement("select supervisor_emp_id from employee_official_details WHERE emp_id=?");
			pst.setInt(1, getEmpId());
			rs = pst.executeQuery();
			while(rs.next()){
				nSuperWiserId = rs.getInt("supervisor_emp_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("UPDATE employee_official_details SET emptype=?,org_id=?,wlocation_id=?,service_id=?,depart_id=?,grade_id=?," +
					"supervisor_emp_id=?,emp_contractor=?,emprofile=?,is_roster=?,is_attendance=?,paycycle_duration=?,payment_mode=?,hod_emp_id=?," +
					"emp_hr=?,is_hod=?,is_cxo=? where emp_id = ?");
			pst.setString(1, getEmpType());
			pst.setInt(2, uF.parseToInt(getOrgId()));
			pst.setInt(3, uF.parseToInt(getwLocation()));
			pst.setString(4, sbServices.toString());
			pst.setInt(5, uF.parseToInt(getDepartment()));
			pst.setInt(6, uF.parseToInt(getEmpGrade()));
			pst.setInt(7, uF.parseToInt(getSupervisor()));
			pst.setInt(8, uF.parseToInt(getEmpContractor()));
			pst.setInt(9, uF.parseToInt(getEmpProfile()));
			pst.setBoolean(10, true);
			pst.setBoolean(11, true);
			pst.setString(12, "M");
			pst.setInt(13, 1);
			pst.setInt(14, uF.parseToInt(getHod()));
			pst.setInt(15, uF.parseToInt(getHR()));
			pst.setBoolean(16, (uF.parseToInt(getStrCXOHOD()) == 2) ? true : false);
			pst.setBoolean(17, (uF.parseToInt(getStrCXOHOD()) == 1) ? true : false);
			pst.setInt(18, getEmpId());
			int x = pst.executeUpdate();
			pst.close();
			
			
			if(uF.parseToInt(getStrCXOHOD()) == 2) {
				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("UPDATE user_details SET usertype_id=? WHERE emp_id=?");
				pst.setInt(1, uF.parseToInt(hmUserTypeID.get(HOD)));
				pst.setInt(2, getEmpId());
				pst.executeUpdate();
				pst.close();
			}
			
			if(uF.parseToInt(getStrCXOHOD()) == 1) {
				StringBuilder sbwLocId = null;
				for(int i=0; locationCXO != null && i<locationCXO.length; i++) {
					if(uF.parseToInt(locationCXO[i]) > 0) {
						if(sbwLocId == null) {
							sbwLocId = new StringBuilder();
							sbwLocId.append("," + locationCXO[i]+",");
						} else {
							sbwLocId.append(locationCXO[i]+",");
						}
					}
				}
				if(uF.parseToInt(getOrgId())>0 && (locationCXO == null || locationCXO.length==0)) {
					pst = con.prepareStatement("select wlocation_id from work_location_info where org_id in("+ getOrgId() +")");
					rs = pst.executeQuery();
					while (rs.next()) {
						if(rs.getInt("wlocation_id") > 0) {
							if(sbwLocId == null) {
								sbwLocId = new StringBuilder();
								sbwLocId.append("," + rs.getInt("wlocation_id") +",");
							} else {
								sbwLocId.append(rs.getInt("wlocation_id") +",");
							}
						}
					}
					rs.close();
					pst.close();
				}
				if(sbwLocId == null) sbwLocId = new StringBuilder();
				
				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("UPDATE user_details SET org_id_access=?,wlocation_id_access=?,usertype_id=? WHERE emp_id=?");
				pst.setString(1, ","+getOrgId()+",");
				pst.setString(2, sbwLocId.toString());
				pst.setInt(3, uF.parseToInt(hmUserTypeID.get(CEO)));
				pst.setInt(4, getEmpId());
				pst.executeUpdate();
				pst.close();
			}
			
			
			String resourceName = CF.getEmpNameMapByEmpId(con, getEmpId()+"");
			String resourceImg = CF.getEmpImageByEmpId(con, uF, getEmpId()+"");
			String alertData = "<div style=\"float: left;\"> <b>"+resourceName+"</b> has been added in your team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
			String alertAction = "Resource.action";
			if(nSuperWiserId == 0 || nSuperWiserId != uF.parseToInt(getSupervisor())) {
				String strDomain = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(getSupervisor());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
//				userAlerts.set_type(ADD_MYTEAM_MEMBER_ALERT);
				userAlerts.setStatus(INSERT_TR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();

				String activityData = "<div style=\"float: left;\"><img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+getEmpId()+"/"+I_22x22+"/"+resourceImg+"\" height=\"20\" width=\"20\"> <b>"+resourceName+"</b> has been added in <b>"+CF.getEmpNameMapByEmpId(con, getSupervisor())+"</b> team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrTaggedWith(","+getSupervisor()+",");
				userAct.setStrVisibilityWith(","+getSupervisor()+",");
				userAct.setStrVisibility("2");
				userAct.setStrData(activityData);
				userAct.setStrSessionEmpId(strSessionEmpId);
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}
			
			if( x > 0) {
				boolean flag = checkEmpStatus(con,getEmpId());
				if(flag){
					updateEmpFilledStatus(con,getEmpId());
					updateEmpLiveStatus(con,getEmpId());
					
					if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)){
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_NEW_EMPLOYEE, CF);
						nF.setDomain(strDomain);
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						nF.setStrEmpId(getEmpId()+"");
	//					nF.setStrHostAddress(request.getRemoteHost());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.sendNotifications();
					}
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public boolean checkEmpStatus(Connection con, int nEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=? and employment_end_date is null and approved_flag=false and is_alive=false");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			if (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	public void updateEmpFilledStatus(Connection con, int nEmpId) {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("UPDATE employee_personal_details SET emp_filled_flag = ?, emp_filled_flag_date = ? " + "WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			java.util.Date date = new java.util.Date();
			pst.setTimestamp(2, new Timestamp(date.getTime()));
			pst.setInt(3, nEmpId);
			pst.execute();
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

	public void updateEmpLiveStatus(Connection con, int nEmpId) {

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("UPDATE employee_personal_details SET is_alive = ?, approved_flag = ? " + "WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, nEmpId);
			pst.execute();
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

	private void updateEmpInfo(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {  
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE employee_personal_details SET empcode = ?,joining_date = ?,approved_flag = ?,is_alive = ?,emp_filled_flag = ?" +
					" where emp_per_id = ?");
			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "")+getEmpCodeNumber());
			pst.setDate(2, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
			pst.setBoolean(3, true);
			pst.setBoolean(4, true);
			pst.setBoolean(5, true);
			pst.setInt(6, getEmpId());
			pst.executeUpdate();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertEmpEducation(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			if(getDegreeName()!=null && getDegreeName().length!= 0) {
				
				String[] degreeNameOther = request.getParameterValues("degreeNameOther");
				for(int i=0; i<getDegreeName().length; i++) {
					
					if(getDegreeName()[i].length()!=0) {
//						pst = con.prepareStatement("INSERT INTO education_details(degree_name, degree_duration, completion_year, grade, emp_id)" +
//						"VALUES (?,?,?,?,?)");
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
							
							pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id)" +
							"VALUES (?,?,?,?,?)");
							pst.setInt(1, newEduid);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, getEmpId());
	//						log.debug("pst=>"+pst);
							pst.execute();
							pst.close();
						} else {
							
							pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id)" +
							"VALUES (?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, getEmpId());
//							log.debug("pst=>"+pst);
							pst.execute();
							pst.close();
						}
						
					}
					
				}
				
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void insertSkills(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			for(int i=0; i <getSkillName().length; i++) {
				
				if(getSkillName()[i].length()!=0) {
				
					pst = con.prepareStatement(insertSkill);
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3, getEmpId());
					pst.execute();
					pst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void uploadImage(UtilityFunctions uF, int empId2) {
		
		try {
			
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("EMPLOYEE_IMAGE");
			uI.setEmpImage(getEmpImage());
			uI.setEmpImageFileName(getEmpImageFileName());
			uI.setEmpId(empId2+"");
			uI.setCF(CF);
			uI.upoadImage();
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	private void insertUser(UtilityFunctions uF, int empId) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = db.makeConnection(con);
			
			Map<String,String> userPresent= CF.getUsersMap(con);
			AddEmployeeMode aE = new AddEmployeeMode();
			aE.request = request;
			aE.session = session;
			aE.CF = CF;
			aE.setFname(getEmpFname());
			aE.setMname(getEmpMname());
			aE.setLname(getEmpLname());
			String username = aE.getUserName(userPresent);
			
			SecureRandom random = new SecureRandom();
			String password = new BigInteger(130, random).toString(32).substring(5, 13);
			
			pst = con.prepareStatement(insertUser);
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			pst.setString(1, username);
			pst.setString(2, password);
			pst.setInt(3, uF.parseToInt(hmUserTypeId.get(EMPLOYEE)));
			pst.setInt(4, empId);
			pst.setString(5, "ACTIVE");
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	int insertEmpPersonalDetails(UtilityFunctions uF, CommonFunctions CF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		int empPerId = 0;
		
		try {
			
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("INSERT INTO employee_personal_details (empcode, emp_fname,emp_mname, emp_lname, emp_email,emp_address1," +
					"emp_city_id, emp_state_id, emp_country_id, emp_pincode, emp_address1_tmp, emp_city_id_tmp, emp_state_id_tmp, emp_country_id_tmp, " +
					"emp_pincode_tmp,emp_date_of_birth,salutation,approved_flag, emp_entry_date, emp_status,emp_contactno_mob,emp_contactno," +
					"skype_id,emp_email_sec,corporate_mobile,corporate_desk,house_no,suburb,relevant_experience,total_experience,emp_gender,emp_filled_flag)");
			sbQuery.append("values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "")+uF.showData(getEmpCodeNumber(),""));
			pst.setString(2, getEmpFname());
			pst.setString(3, getEmpMname());
			pst.setString(4, getEmpLname());
			pst.setString(5, getEmpEmail());
			
			pst.setString(6, getStreetAddress());
			pst.setString(7, getCity());
			pst.setInt(8, uF.parseToInt(getState()));
			pst.setInt(9, uF.parseToInt(getCountry()));
			pst.setString(10, getPincode());
			
			pst.setString(11, getStreetAddress());
			pst.setString(12, getCity());
			pst.setInt(13, uF.parseToInt(getState()));
			pst.setInt(14, uF.parseToInt(getCountry()));
			pst.setString(15, getPincode());
			
			pst.setDate(16,  uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setString(17, getSalutation());
			pst.setBoolean(18, false);
			pst.setTimestamp(19, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setString(20, PROBATION);
			
			pst.setString(21, getPersonalMobile());
			pst.setString(22, getResidenceNo());
			pst.setString(23, getCorporateSkype());
			pst.setString(24, getCorporateEmail());
			pst.setString(25, getCorporateMobile());
			pst.setString(26, getCorporateDesk());
			pst.setString(27, getHouseNo());
			pst.setString(28, getSuburb());
			pst.setDouble(29, uF.parseToDouble(getRelevantExperience()));
			pst.setDouble(30, uF.parseToDouble(getTotalExperience()));
			pst.setString(31, getEmpGender());
			pst.setBoolean(32, true);
//			System.out.println("pst=====>"+pst);
			pst.execute();
			pst.close();
			
			
			pst = con.prepareStatement(selectMaxEmpId);
			rs = pst.executeQuery();
			while(rs.next()) {
				empPerId = rs.getInt(1);
			}
			rs.close();
			pst.close();
			
			if(empPerId > 0) {
				pst = con.prepareStatement("INSERT INTO employee_official_details(emp_id,emp_contractor) VALUES (?,?)");
				pst.setInt(1, empPerId);
				pst.setInt(2, uF.parseToInt(getStrEmpORContractor())>0 ? uF.parseToInt(getStrEmpORContractor()) : 1);
				pst.execute();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return empPerId;
		
	}

	private void loadValidatePeople(UtilityFunctions uF) {
		salutationList = new FillSalutation(request).fillSalutation();
		empGenderList = new FillGender().fillGender();
		
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();
		
		skillsList = new FillSkills(request).fillSkillsWithId();
		educationalList = new FillEducational(request).fillEducationalQual();
		degreeDurationList = new FillDegreeDuration().fillDegreeDuration();
		yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
		
		orgList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getOrgId());
		serviceList = new FillServices(request).fillServices(getOrgId(), uF);
		deptList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrgId()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrgId()));
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getOrgId()));
//		supervisorList = new FillEmployee(request).fillSupervisorNameCode(getEmpId(), getOrgId()); 
//		HodList = new FillEmployee(request).fillHODNameCode(""+getEmpId(), getOrgId());
		supervisorList = new FillEmployee(request).fillSupervisorNameCode(getEmpId(), getOrgId(), getDepartment()); 
		HodList = new FillEmployee(request).fillHODNameCode(""+getEmpId(), getOrgId(), uF.parseToInt(getwLocation()), CF);
		
		if(getwLocation()!= null && !getwLocation().equals("0")) {
			HRList = new FillEmployee(request).fillEmployeeNameHR(""+getEmpId(), 0, uF.parseToInt(getwLocation()), CF, uF); 
		} else if (getOrgId() != null && !getOrgId().equals("0")) {
			HRList = new FillEmployee(request).fillEmployeeNameHR(""+getEmpId(), uF.parseToInt(getOrgId()), 0, CF, uF); 
		} else {
			HRList = new FillEmployee(request).fillEmployeeNameHR(""+getEmpId(), 0, 0, CF, uF); 
		}
		empTypeList = new FillEmploymentType().fillEmploymentType(request); 
		resourceList = new FillResourceType().fillResourceType();
		
		request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
		
		StringBuilder sbSkills = new StringBuilder();
		sbSkills.append(" <table class=table form-table><tr><td>"+
            	"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select name=skillName>" +
            	"<option value=>Select Skill</option>"
			);

		for(int k=0; k< skillsList.size(); k++) { 
			sbSkills.append("<option value="+((FillSkills)skillsList.get(k)).getSkillsId()+"> "+((FillSkills)skillsList.get(k)).getSkillsName()+"</option>");
		}
		
		sbSkills.append("</select></td><td>" +
		            "<select name=skillValue>" +
		            "<option value=>Select Skill Rating</option>"
			);
		
		for(int k=1; k< 11; k++) {
			sbSkills.append("<option value="+k+">"+k+"</option>");
		}
		sbSkills.append("</select></td>");
		request.setAttribute("sbSkills", sbSkills);
		
		StringBuilder sbdegreeDuration = new StringBuilder();
		sbdegreeDuration.append("<option value=''> Degree</option>");
		for (int i = 0; i < educationalList.size(); i++) {
			sbdegreeDuration.append("<option value=" + ((FillEducational) educationalList.get(i)).getEduId()+ "> " + ((FillEducational) educationalList.get(i)).getEduName()
					+ "</option>");

		}
		sbdegreeDuration.append("<option value=other>Other</option>");
		sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name= degreeDuration style=width:110px !important; >" + "<option value=''>Duration</option>");
		for (int i = 0; i < degreeDurationList.size(); i++) {
			sbdegreeDuration.append("<option value=" + ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationID() + "> "
					+ ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationName() + "</option>");
		}
		sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name=completionYear style=width:110px !important; >" + "<option value=''>Completion Year</option>");
		for (int i = 0; i < yearsList.size(); i++) {
			sbdegreeDuration.append("<option value=" + ((FillYears) yearsList.get(i)).getYearsID() + "> " + ((FillYears) yearsList.get(i)).getYearsName() + "</option>");
		}
		sbdegreeDuration.append("</select>" + "</td>" + "<td><input type= text style=width:110px !important; name=grade ></input></td>"
				+ "<td><a href=javascript:void(0) onclick=addEducation() class=fa fa-fw fa-plus >&nbsp;</a></td>");
		request.setAttribute("sbdegreeDuration", sbdegreeDuration.toString());
		
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public int getEmpId() {
		return empId;
	}
	public void setEmpId(int empId) {
		this.empId = empId;
	}
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<FillSalutation> getSalutationList() {
		return salutationList;
	}

	public void setSalutationList(List<FillSalutation> salutationList) {
		this.salutationList = salutationList;
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

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}

	public List<FillEducational> getEducationalList() {
		return educationalList;
	}

	public void setEducationalList(List<FillEducational> educationalList) {
		this.educationalList = educationalList;
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillDepartment> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<FillDepartment> deptList) {
		this.deptList = deptList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillEmployee> getSupervisorList() {
		return supervisorList;
	}

	public void setSupervisorList(List<FillEmployee> supervisorList) {
		this.supervisorList = supervisorList;
	}

	public List<FillGender> getEmpGenderList() {
		return empGenderList;
	}

	public void setEmpGenderList(List<FillGender> empGenderList) {
		this.empGenderList = empGenderList;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getEmpFname() {
		return empFname;
	}

	public void setEmpFname(String empFname) {
		this.empFname = empFname;
	}

	public String getEmpMname() {
		return empMname;
	}

	public void setEmpMname(String empMname) {
		this.empMname = empMname;
	}

	public String getEmpLname() {
		return empLname;
	}

	public void setEmpLname(String empLname) {
		this.empLname = empLname;
	}

	public String getEmpDateOfBirth() {
		return empDateOfBirth;
	}

	public void setEmpDateOfBirth(String empDateOfBirth) {
		this.empDateOfBirth = empDateOfBirth;
	}

	

	public String getEmpGender() {
		return empGender;
	}

	public void setEmpGender(String empGender) {
		this.empGender = empGender;
	}

	public String getCorporateDesk() {
		return corporateDesk;
	}

	public void setCorporateDesk(String corporateDesk) {
		this.corporateDesk = corporateDesk;
	}

	public String getCorporateEmail() {
		return corporateEmail;
	}

	public void setCorporateEmail(String corporateEmail) {
		this.corporateEmail = corporateEmail;
	}

	public String getCorporateSkype() {
		return corporateSkype;
	}

	public void setCorporateSkype(String corporateSkype) {
		this.corporateSkype = corporateSkype;
	}

	public String getPersonalMobile() {
		return personalMobile;
	}

	public void setPersonalMobile(String personalMobile) {
		this.personalMobile = personalMobile;
	}

	public String getResidenceNo() {
		return residenceNo;
	}

	public void setResidenceNo(String residenceNo) {
		this.residenceNo = residenceNo;
	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getRelevantExperience() {
		return relevantExperience;
	}

	public void setRelevantExperience(String relevantExperience) {
		this.relevantExperience = relevantExperience;
	}

	public String getTotalExperience() {
		return totalExperience;
	}

	public void setTotalExperience(String totalExperience) {
		this.totalExperience = totalExperience;
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

	public String getEmpImageFileName() {
		return empImageFileName;
	}

	public void setEmpImageFileName(String empImageFileName) {
		this.empImageFileName = empImageFileName;
	}

	public File getEmpImage() {
		return empImage;
	}

	public void setEmpImage(File empImage) {
		this.empImage = empImage;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}

	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public String getEmpContractor() {
		return empContractor;
	}

	public void setEmpContractor(String empContractor) {
		this.empContractor = empContractor;
	}

	public String getEmpStartDate() {
		return empStartDate;
	}

	public void setEmpStartDate(String empStartDate) {
		this.empStartDate = empStartDate;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String[] getService() {
		return service;
	}

	public void setService(String[] service) {
		this.service = service;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getEmpProfile() {
		return empProfile;
	}

	public void setEmpProfile(String empProfile) {
		this.empProfile = empProfile;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getCorporateMobile() {
		return corporateMobile;
	}

	public void setCorporateMobile(String corporateMobile) {
		this.corporateMobile = corporateMobile;
	}

	public Boolean getAutoGenerate() {
		return autoGenerate;
	}

	public void setAutoGenerate(Boolean autoGenerate) {
		this.autoGenerate = autoGenerate;
	}

	public List<FillResourceType> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<FillResourceType> resourceList) {
		this.resourceList = resourceList;
	}

	public boolean isApprovedFlag() {
		return approvedFlag;
	}

	public void setApprovedFlag(boolean approvedFlag) {
		this.approvedFlag = approvedFlag;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public List<FillUserType> getUserTypeList() {
		return userTypeList;
	}

	public void setUserTypeList(List<FillUserType> userTypeList) {
		this.userTypeList = userTypeList;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String[] getOrgId1() {
		return orgId1;
	}

	public void setOrgId1(String[] orgId1) {
		this.orgId1 = orgId1;
	}

	public String[] getwLocation1() {
		return wLocation1;
	}

	public void setwLocation1(String[] wLocation1) {
		this.wLocation1 = wLocation1;
	}

	public List<FillOrganisation> getOrgList1() {
		return orgList1;
	}

	public void setOrgList1(List<FillOrganisation> orgList1) {
		this.orgList1 = orgList1;
	}

	public List<FillWLocation> getwLocationList1() {
		return wLocationList1;
	}

	public void setwLocationList1(List<FillWLocation> wLocationList1) {
		this.wLocationList1 = wLocationList1;
	}

	public String getStrEmpORContractor() {
		return strEmpORContractor;
	}

	public void setStrEmpORContractor(String strEmpORContractor) {
		this.strEmpORContractor = strEmpORContractor;
	}

	public String getDobYear() {
		return dobYear;
	}

	public void setDobYear(String dobYear) {
		this.dobYear = dobYear;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillEmployee> getHRList() {
		return HRList;
	}

	public void setHRList(List<FillEmployee> hRList) {
		HRList = hRList;
	}

	public List<FillEmployee> getHodList() {
		return HodList;
	}

	public void setHodList(List<FillEmployee> hodList) {
		HodList = hodList;
	}

	public String getHod() {
		return hod;
	}

	public void setHod(String hod) {
		this.hod = hod;
	}

	public String getHR() {
		return HR;
	}

	public void setHR(String hR) {
		HR = hR;
	}

	public String getStrCXOHOD() {
		return strCXOHOD;
	}

	public void setStrCXOHOD(String strCXOHOD) {
		this.strCXOHOD = strCXOHOD;
	}

	public String getDefaultCXO() {
		return defaultCXO;
	}

	public void setDefaultCXO(String defaultCXO) {
		this.defaultCXO = defaultCXO;
	}

	public String getDefaultHOD() {
		return defaultHOD;
	}

	public void setDefaultHOD(String defaultHOD) {
		this.defaultHOD = defaultHOD;
	}

	public String[] getLocationCXO() {
		return locationCXO;
	}

	public void setLocationCXO(String[] locationCXO) {
		this.locationCXO = locationCXO;
	}

	public List<String> getCxoLocationAccess() {
		return cxoLocationAccess;
	}

	public void setCxoLocationAccess(List<String> cxoLocationAccess) {
		this.cxoLocationAccess = cxoLocationAccess;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public List<String> getUserTypeOrgValue() {
		return userTypeOrgValue;
	}

	public void setUserTypeOrgValue(List<String> userTypeOrgValue) {
		this.userTypeOrgValue = userTypeOrgValue;
	}
	
}
