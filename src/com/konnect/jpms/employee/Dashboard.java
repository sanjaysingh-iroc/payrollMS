 package com.konnect.jpms.employee;
 
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.charts.PieCharts;
import com.konnect.jpms.common.FillNavigation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.CustomEmailer;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.EncryptionUtility;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.MoveDocuments;
import com.konnect.jpms.util.Navigation;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author workrig
 *
 */
public class Dashboard extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId;
	
	CommonFunctions CF = null;
	private String userscreen;
	private String productType;
	private String toAction;
//	String resourceORContractor;
	private String strMV;
	private String ch;
	private String paycycle;
	
	private String navigationId;
	private String toPage;
	private String toTab;
	private String strOrg;
	private String strLocation;
	private String strLevel;
	private String strCFYear;
	private String strSalaryHeadId;
	private String strGrade;
	private String salaryBand;
	
	private String profileEmpId;
	
	private static Logger log = Logger.getLogger(Dashboard.class);
	public String execute() throws Exception {

		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		  
		
		if(CF.isTrial() && !CF.isTermsCondition()){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		/*if(getProfileEmpId() != null) {
			EncryptionUtility eU = new EncryptionUtility();
			String decodeEmpId = eU.decode(getProfileEmpId());
			setProfileEmpId(decodeEmpId);
		}*/
		
		productType = (String) session.getAttribute(PRODUCT_TYPE);
//		resourceORContractor = (String) session.getAttribute(RESOURCE_OR_CONTRACTOR);
		String empType = (String) session.getAttribute(USERTYPE);
		String userTypeId = (String) session.getAttribute(USERTYPEID);
		strEmpId = (String) session.getAttribute("EMPID");
		 
//		System.out.println("empType ===>> " + empType);
		
		if(CF !=null){
			CF.getAlertUpdates(CF, strEmpId, request,empType);
		}
		request.setAttribute(TITLE, TViewDashboard);
		//getThoughtByEmp(uF);
		if(((String)session.getAttribute(USERTYPE))!=null && ((String)session.getAttribute(USERTYPE)).equals(ADMIN)) {
			request.setAttribute(TITLE, "Control Panel");
		} else if(((String)session.getAttribute(USERTYPE))!=null && ((String)session.getAttribute(USERTYPE)).equals(HRMANAGER)) {
			request.setAttribute(TITLE, "HR Manager's Dashboard");
		} else if(((String)session.getAttribute(USERTYPE))!=null && ((String)session.getAttribute(USERTYPE)).equals(OTHER_HR)) {
			request.setAttribute(TITLE, "Other HR's Dashboard");
		}
		

		if(uF.parseToInt(getStrMV()) == 1){
			MoveDocuments documents = new MoveDocuments();
			documents.moveEmpImageDocument();
			documents.moveInvestmentDocument();
			documents.moveReimbursementsDocument();
			documents.movePerkDocument();
			documents.moveCTCVariablesDocument();
		}
		
		if(getCh()!=null && getCh().equals("reset")){
			resetAllPassword(uF);
		}
		
		
		
		String[] arrModules = CF.getArrEnabledModules();
		request.setAttribute("arrModules", arrModules);
		
		
		
		List<Navigation> alParentNavL = new ArrayList<Navigation>();
		Map<String, List<Navigation>> hmChildNavL = new HashMap<String, List<Navigation>>();
		List<Navigation> alParentNavR = new ArrayList<Navigation>();
		Map<String, List<Navigation>> hmChildNavR = new HashMap<String, List<Navigation>>();
		new FillNavigation(request).fillNavigation(alParentNavL, hmChildNavL, alParentNavR, hmChildNavR, session, CF);

		
		loadNotifications();
		
//		System.out.println("getUserscreen() ===>> " + getUserscreen()); 
		
		/*getTaskBarNotification(empType);*/
		if(getUserscreen()!=null && getUserscreen().equals(ADMIN)) {
			empType = ADMIN;
		}
		
//		System.out.println("BASEUSERTYPE =====> " + (String)session.getAttribute(BASEUSERTYPE) + " -- empType ===>> " + empType + " -- IS_WORKRIG ===>> " + CF.isWorkRig());
//		System.out.println("empType ===>> " + empType + "--getUserscreen=="+getUserscreen());
		
		if (getUserscreen() != null && getUserscreen().equalsIgnoreCase(ADMIN)) {
			request.setAttribute(PAGE, PAdminDashboard);
//			return loadAdminDashboard();
			request.setAttribute(TITLE, "Control Panel");
//			System.out.println("getUserscreen() *** ===>> " + getUserscreen());
//			System.out.println("getStrOrg() *** ===>> " + getStrOrg());
//			System.out.println("getStrLocation() *** ===>> " + getStrLocation());
			String baseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
			if((uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_CONTROL_PANEL_ALL_USER)) && hmFeatureUserTypeId != null && hmFeatureUserTypeId.get(F_SHOW_CONTROL_PANEL_ALL_USER) != null && hmFeatureUserTypeId.get(F_SHOW_CONTROL_PANEL_ALL_USER).contains(baseUserTypeId)) || (!uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_CONTROL_PANEL_ALL_USER)) && hmFeatureUserTypeId != null && hmFeatureUserTypeId.get(F_SHOW_CONTROL_PANEL_ALL_USER+"_USER_IDS") != null && hmFeatureUserTypeId.get(F_SHOW_CONTROL_PANEL_ALL_USER+"_USER_IDS").contains(strEmpId))) {
				return new AdminDashboard(request, session, CF, strEmpId, getNavigationId(), getToPage(), getToTab(), getStrOrg(), getStrLocation(), getStrLevel(), getSalaryBand(), getStrCFYear(), getStrSalaryHeadId(), getStrGrade(), getPaycycle()).loadDashboard();
			} else {
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}
		} else if (empType != null && empType.equalsIgnoreCase(ADMIN)) {
//			
			if(getProductType() != null && getProductType().equals("2")) {
//				request.setAttribute(PAGE, PHRManagerDashboard);
//				request.setAttribute(PAGE, "/jsp/employee/DashboardHRManagerC.jsp");
//				request.setAttribute(TITLE, "Global HR's Dashboard");
////				return loadHRManagerDashboard();
//				return new HRManagerDashboard(request, session, CF, strEmpId).loadDashboard();
				if(getUserscreen()!=null && getUserscreen().equals("THREESTEP")) {
					return "THREESTEP";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
					return "approveTravel";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkLeaveCard")) {
					return "factQuickLinkLeaveCard";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkClockEntry")) {
					return "factQuickLinkClockEntry";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkViewSalarySlip")) {
					return "factQuickLinkViewSalarySlip";
				} else {
//					request.setAttribute(PAGE, PCEODashboard);
//					request.setAttribute(TITLE, "Global HR's Dashboard");
//					return new CEODashboard(request, session, CF, strEmpId).loadDashboard();
					return "peopleDash";
				}
				
			} else {
				return "gHRTaskrigDash";
			}
			
		} else if (empType != null && empType.equalsIgnoreCase(MANAGER)) {
			if(getUserscreen()!=null && getUserscreen().equals("myteam")) {
				return "userscreen";
			} else if(getUserscreen()!=null && getUserscreen().equals("teamReviews")) {
				return "teamReviews";
			} else if(getUserscreen()!=null && getUserscreen().equals("teamGoalsKRATarget")) {
				return "teamGoalsKRATarget";
			} else if(getUserscreen()!=null && getUserscreen().equals("managerGoals")) {
				return "managerGoals";
			} else if(getUserscreen()!=null && getUserscreen().equals("nineBlockAnalysis")) {
				return "managerDash";
			} else if(getUserscreen()!=null && getUserscreen().equals("leaveRequest")) {
				return "leaveRequest";
			} else if(getUserscreen()!=null && getUserscreen().equals("reimbRequest")) {
				return "reimbRequest";
			} else if(getUserscreen()!=null && getUserscreen().equals("teamExceptions")) {
				return "teamExceptions";
			} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			}  else {
//			request.setAttribute(PAGE, PManagerDashboard);
//			return loadManagerDashboard();
//				request.setAttribute(PAGE, "/jsp/employee/DashboardManagerC.jsp");
//				return new ManagerDashboard(request, session, CF, strEmpId).loadDashboard(); 
				if(getProductType() != null && getProductType().equals("2")) {
					if(((String)session.getAttribute(BASEUSERTYPE)) != null && (((String)session.getAttribute(BASEUSERTYPE)).equals(CEO) || ((String)session.getAttribute(BASEUSERTYPE)).equals(ADMIN)) ) {
//						request.setAttribute(PAGE, PCEODashboard);
//						request.setAttribute(TITLE, "CEO's Dashboard");
//						return new CEODashboard(request, session, CF, strEmpId).loadDashboard();
						return "managerDash";
					} else {
						return "managerDash"; 
					}
				} else {
					return "managerTaskrigDash";
				}
				
			}
		} else if (empType != null && empType.equalsIgnoreCase(EMPLOYEE)) { 
//			System.out.println("getProductType() ===>> " + getProductType());
//				System.out.println("type==>"+type);
//				if(type!=null && type.equalsIgnoreCase("ML")){
//					customMailerFun();
//				}
			if(getProductType() != null && getProductType().equals("2")) {
	//			request.setAttribute(PAGE, PEmpDashboard);
//				System.out.println("getToAction() ===>> " + getToAction());
				if(getToAction() != null && getToAction().equals("MyHome")) {
//					System.out.println("getToAction() if ===>> " + getToAction());
					request.setAttribute(PAGE, "/jsp/employee/MyHome.jsp");
					return new MyHome(request, session, CF, strEmpId, empType).loadMyHome();
					
				} else {
					if(getUserscreen()!=null && getUserscreen().equals("interviews")) {
						return "interviews";
					} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
						return "approveTravel";
					} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkLeaveSummary")) {
						return "factQuickLinkLeaveSummary";
					} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkMyClockEntry")) {
						return "factQuickLinkMyClockEntry";
					} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkMySalarySlip")) {
						return "factQuickLinkMySalarySlip";
					} else {
//						request.setAttribute(PAGE, "/jsp/employee/DashboardEmpC.jsp");
//						return new EmpDashboard(request, session, CF, strEmpId).loadDashboard();
						request.setAttribute(PAGE, "/jsp/employee/MyHome.jsp");
						return new MyHome(request, session, CF, strEmpId, empType).loadMyHome();
					}
				}
			} else {
				
//				System.out.println("Dash/266--getUserscreen="+getUserscreen());
				if(getUserscreen()!=null && getUserscreen().equals("CEODashboard")) {
					return "CEODashboard";
				} else if(getUserscreen()!=null && getUserscreen().equals("myProjects")) {
					return "myProjects";
				} else if(getUserscreen()!=null && getUserscreen().equals("allProjects")) {
					return "allProjects";
				} else if(getUserscreen()!=null && getUserscreen().equals("myTimesheet")) {
					return "myTimesheet";
				} else if(getUserscreen()!=null && getUserscreen().equals("myTeamTimesheets")) {
//					System.out.println("paycycle ====>>> " + paycycle);
					return "myTeamTimesheets";
				} else if(getUserscreen()!=null && getUserscreen().equals("myWorkTasks")) {
//					System.out.println("paycycle ====>>> " + paycycle);
					return "myWorkTasks";
				} else if(getUserscreen()!=null && getUserscreen().equals("generateBills")) {
					return "generateBills";
		//===start parvez date: 31-03-2022===			
				} else if(getUserscreen()!=null && getUserscreen().equals("reportMIS")) {
					getMISReportUrl();
//					System.out.println("Dashbord/282---NavigationId="+getNavigationId());
					return "reportMIS";
				}else {
					return "myTaskrigDash";
				}
		//===end parvez date: 31-03-2022===		
			}
		} else if (empType != null && empType.equalsIgnoreCase(HRMANAGER)) {
			if(getProductType() != null && getProductType().equals("2")) {
	//			request.setAttribute(PAGE, PHRManagerDashboard);
				
				if(getUserscreen()!=null && getUserscreen().equals("approvePay")) {
					return "approvePay";
				} else if(getUserscreen()!=null && getUserscreen().equals("onboardCandidate")) {
					return "onboardCandidate";
				} else if(getUserscreen()!=null && getUserscreen().equals("finalizeReviews")) {
					return "finalizeReviews";
				} else if(getUserscreen()!=null && getUserscreen().equals("planLearningGaps")) {
					return "planLearningGaps";
				} else if(getUserscreen()!=null && getUserscreen().equals("updateStatutoryCompliance")) {
					return "updateStatutoryCompliance";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
					return "approveTravel";
				} else if(getUserscreen()!=null && getUserscreen().equals("THREESTEP")) {
					return "THREESTEP";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkLeaveCard")) {
					return "factQuickLinkLeaveCard";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkClockEntry")) {
					return "factQuickLinkClockEntry";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkViewSalarySlip")) {
					return "factQuickLinkViewSalarySlip";
				} else {
//					request.setAttribute(PAGE, "/jsp/employee/DashboardHRManagerC.jsp"); 
//		//			return loadHRManagerDashboard();
//					return new HRManagerDashboard(request, session, CF, strEmpId).loadDashboard();
//					request.setAttribute(PAGE, PCEODashboard);
//					request.setAttribute(TITLE, "HR Manager's Dashboard");
//					return new CEODashboard(request, session, CF, strEmpId).loadDashboard();
					return "peopleDash";
				}
			} else {
//				request.setAttribute(PAGE, "/jsp/employee/DashboardHRManagerC.jsp"); 
//				return new HRManagerDashboard(request, session, CF, strEmpId).loadDashboard();
				return "HRTaskrigDash";
			}
		} else if (empType != null && empType.equalsIgnoreCase(ACCOUNTANT)) {
			if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			} else {
				request.setAttribute(PAGE, PAccountantDashboard);
	//			return loadAccountantDashboard();
				return new AccountantDashboard(request, session, CF, strEmpId).loadDashboard();
			}
		} else if (empType != null && empType.equalsIgnoreCase(CEO)) {
			if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			} else {
				request.setAttribute(PAGE, PCEODashboard);
	//			return loadCEODashboard();
				return new CEODashboard(request, session, CF, strEmpId).loadDashboard();
			}
		} else if (empType != null && empType.equalsIgnoreCase(CFO)) {
			if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			} else {
				request.setAttribute(PAGE, PCFODashboard);
	//			return loadCFODashboard();
				return new CFODashboard(request, session, CF, strEmpId).loadDashboard();
			}
		} else if (empType != null && empType.equalsIgnoreCase(ARTICLE)) {
			if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			} else {
				request.setAttribute(PAGE, PArticleDashboard);
	//			return loadCFODashboard();
				return new ArticleDashboard(request, session, CF, strEmpId).loadDashboard();
			}
		} else if (empType != null && empType.equalsIgnoreCase(CONSULTANT)) {
			if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			} else {
				request.setAttribute(PAGE, "/jsp/recruitment/jobreport.jsp");
	//			return loadCFODashboard();
				return new ConsultantDashboard(request, session, CF, strEmpId).loadDashboard();
			}
		} else if (empType != null && empType.equalsIgnoreCase(TRAINER)) {
			if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			} else {
				request.setAttribute(PAGE, "/jsp/employee/DashboardTrainer.jsp");
				return new TrainerDashboard(request, session, CF, strEmpId).loadDashboard();
			}
		} else if (empType != null && empType.equalsIgnoreCase(RECRUITER)) {
//			System.out.println("RECRUITER empType ===>> " + empType);
			if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			} else {
	//			request.setAttribute(PAGE, "/jsp/employee/DashboardTrainer.jsp");
	//			return new TrainerDashboard(request, session, CF, strEmpId).loadDashboard();
//				return "RecruiterDashboard";
				request.setAttribute(PAGE, PCEODashboard);
				request.setAttribute(TITLE, "Recruiter's Dashboard");
				return new CEODashboard(request, session, CF, strEmpId).loadDashboard();
			}
		} else if (empType != null && empType.equalsIgnoreCase(CUSTOMER)) {
			if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
				return "approveTravel";
			} else {
	//			request.setAttribute(PAGE, "/jsp/employee/DashboardTrainer.jsp");
	//			return new TrainerDashboard(request, session, CF, strEmpId).loadDashboard();
				return "CustomerDashboard";
			}
		} else if (empType != null && empType.equalsIgnoreCase(OTHER_HR)) {
			if(getProductType() != null && getProductType().equals("2")) {
				if(getUserscreen()!=null && getUserscreen().equals("approvePay")) {
					return "approvePay";
				} else if(getUserscreen()!=null && getUserscreen().equals("onboardCandidate")) {
					return "onboardCandidate";
				} else if(getUserscreen()!=null && getUserscreen().equals("finalizeReviews")) {
					return "finalizeReviews";
				} else if(getUserscreen()!=null && getUserscreen().equals("planLearningGaps")) {
					return "planLearningGaps";
				} else if(getUserscreen()!=null && getUserscreen().equals("updateStatutoryCompliance")) {
					return "updateStatutoryCompliance";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("travel")) {
					return "approveTravel";
				} else if(getUserscreen()!=null && getUserscreen().equals("THREESTEP")) {
					return "THREESTEP";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkLeaveCard")) {
					return "factQuickLinkLeaveCard";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkClockEntry")) {
					return "factQuickLinkClockEntry";
				} else if(getUserscreen()!=null && getUserscreen().trim().equalsIgnoreCase("FactQuickLinkViewSalarySlip")) {
					return "factQuickLinkViewSalarySlip";
				} else {
//					request.setAttribute(PAGE, PCEODashboard);
//					request.setAttribute(TITLE, "Other HR's Dashboard");
//					return new CEODashboard(request, session, CF, strEmpId).loadDashboard();
					return "peopleDash";
				}
			} else {
				return "HRTaskrigDash";
			}
		} else {
			return LOGIN;
		}

	}
	
	/*public void getThoughtByEmp(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,List<String>> hmthoughts = new LinkedHashMap<String,List<String>>();
		try {
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * from daythoughts where day_id = ? and year = ?");
			//pst.setInt(1,uF.parseToInt(strEmpId));
			pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
			pst.setInt(2, cal.get(Calendar.YEAR));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> thoughtList = new ArrayList<String>();
				thoughtList.add(rs.getString("thought_text"));
				thoughtList.add(rs.getString("thought_by"));
				hmthoughts.put(rs.getString("thought_id"), thoughtList);
			}
			request.setAttribute("hmthoughts", hmthoughts);
//			System.out.println("hmThoughts==>"+hmthoughts.size());
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
	}
	
	/*private void getTaskBarNotification(String empType) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			 pst = con.prepareStatement("select * from user_alerts where emp_id=?");			
			 pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			 System.out.println("pst ===> " +pst);
			 rs = pst.executeQuery();
			 Map<String, String> hmTaskNotification=new HashMap<String, String>();
			 while (rs.next()) {
				 hmTaskNotification.put("MY_PAY_CNT",""+rs.getInt("mypay"));				
				 hmTaskNotification.put("LEAVE_REQUEST_CNT",""+rs.getInt("leave_request"));
				 hmTaskNotification.put("REIM_REQUEST_CNT",""+rs.getInt("reimbursement_request"));
				 hmTaskNotification.put("TRAVEL_CNT",""+rs.getInt("travel_request"));
				 hmTaskNotification.put("LEAVE_APPOVAL_CNT",""+rs.getInt("leave_approval"));
				 
				 hmTaskNotification.put("JOBCODE_REQUEST_CNT",""+rs.getInt("jobcode_request"));
				 hmTaskNotification.put("REQUI_REQUEST_CNT",""+rs.getInt("requirement_request"));
				 hmTaskNotification.put("HR_REVIEWS_CNT",""+rs.getInt("hr_reviews"));
				 hmTaskNotification.put("REVIEWS_CNT",""+rs.getInt("reviews"));
				 
				 hmTaskNotification.put("JOBCODE_APPROVE_CNT",""+rs.getInt("jobcode_approval"));
				 hmTaskNotification.put("REQUI_APPROVE_CNT",""+rs.getInt("requirement_approval"));
				 hmTaskNotification.put("MANAGER_REVIEWS_CNT",""+rs.getInt("manager_reviews"));
				 
				 hmTaskNotification.put("MY_REVIEWS_CNT",""+rs.getInt("my_reviews"));
				 hmTaskNotification.put("MY_GOALS_CNT",""+rs.getInt("my_goals"));
				 hmTaskNotification.put("MY_KRAS_CNT",""+rs.getInt("my_kras"));
				 hmTaskNotification.put("MY_TARGETS_CNT",""+rs.getInt("my_targets"));
				 hmTaskNotification.put("MY_LEARNING_PLAN_CNT",""+rs.getInt("my_learning_plans"));
				 hmTaskNotification.put("INTERVIEW_CNT",""+rs.getInt("interviews"));
				 hmTaskNotification.put("NEWJOINEE_CNT",""+rs.getInt("new_joinees"));
				 hmTaskNotification.put("MANGER_LEARNING_GAPS_CNT",""+rs.getInt("manager_learning_gaps"));
				 hmTaskNotification.put("HR_LEARNING_GAPS_CNT",""+rs.getInt("hr_learning_gaps"));
				 
			 }
//			 System.out.println("HR_REVIEWS_CNT ======> " +hmTaskNotification.get("HR_REVIEWS_CNT") );
			 
			pst = con.prepareStatement(getUnreadMailCount);			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			while (rs.next()) {
				 hmTaskNotification.put("MAIL_CNT",""+rs.getInt("count"));
			}
			
			if(empType != null && empType.equalsIgnoreCase(MANAGER)){
				String date = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat());
				pst = con.prepareStatement("SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, " +
						" employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? " +
						" ORDER BY in_out_timestamp desc) a RIGHT JOIN roster_details rd ON a.emp_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t " +
						" WHERE t._date BETWEEN ? AND ? AND empl_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) " +
						" and early_late>0 and approved=0");
				pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(3, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
				rs = pst.executeQuery();
				int nExceptionCount = 0;
				while (rs.next()) {
					nExceptionCount++;
				}
				hmTaskNotification.put("EXCEPTION_CNT",""+nExceptionCount);
			} else if(empType != null && empType.equalsIgnoreCase(HRMANAGER)){
				String date = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat());
				pst = con.prepareStatement("SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, " +
						" employee_official_details eod WHERE eod.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? " +
						" ORDER BY in_out_timestamp desc) a RIGHT JOIN roster_details rd ON a.empl_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t " +
						" WHERE t._date BETWEEN ? AND ? AND wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and early_late>0 and approved=0");
				pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(3, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
				rs = pst.executeQuery();
				int nExceptionCount = 0;
				while (rs.next()) {
					nExceptionCount++;
				}
				hmTaskNotification.put("EXCEPTION_CNT",""+nExceptionCount);
			}
			
			session.setAttribute("hmTaskNotification", hmTaskNotification);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}*/
	
//****************************Start Of Mail Code****************************************//	

//	private void customMailerFun(){
//		
//		System.out.println("in customMailerFun===");
//		
//		UtilityFunctions uF=new UtilityFunctions();
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//			
//
//		try {
//			
//			 con = db.makeConnection(con);
//			 
//			    String strDomain = request.getServerName().split("\\.")[0];
//				setDomain(strDomain);
//				Thread th = new Thread();
//				th.start(); 
//				
//			 Map<String, String>hmEmpContactNo= CF.getEmpContactNoMap(con);
//				if(hmEmpContactNo == null)hmEmpContactNo = new HashMap<String,String>();
//				
//			 Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
//				if(hmEmpEmail == null) hmEmpEmail = new HashMap<String, String>();
//				
//			 String strEmp = CF.getEmpNameMapByEmpId(con, strEmpId.trim());
//			 String strEmpEmail = hmEmpEmail.get(strEmpId.trim());
//			 String strEmpDesignation=CF.getEmpDesigMapByEmpId(con, strEmpId.trim());
//			 String strEmpContactNO=hmEmpContactNo.get(strEmpId.trim());                        
//				
//			 String strSubject = "Test";
//			
//			 String strBody = "The Required Information of "+uF.showData(strEmp, "")+" is Listed Below" + "<br><br>" + 
//						"1) MailId- "+uF.showData(strEmpEmail,"")+ "<br>" +
//						"2) Designation- "+uF.showData(strEmpDesignation, "") + "<br>" +
//						"3) ContactNo-"+uF.showData(strEmpContactNO, "") + "<br>";
//			 
//			  System.out.println("strBody==>"+strBody);
//			 
//			  CustomEmailer ce = new CustomEmailer("asha.pawar@workrig.com", strSubject, strBody, strDomain);
//			  ce.sendCustomEmail();
//				
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally {
//			if(rs !=null){
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			
//			if(pst !=null){
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//	}
	
//****************************End Of Mail Code****************************************//	
	private void resetAllPassword(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select user_id from user_details where emp_id > 0");
			rs = pst.executeQuery();
			List<String> alUserList = new ArrayList<String>();
			while(rs.next()){
				alUserList.add(rs.getString("user_id"));
			}
            rs.close();
            pst.close();
			
            SecureRandom random = new SecureRandom();
			for(int i = 0; alUserList!=null && i < alUserList.size(); i++){
				String strUserId = alUserList.get(i);
				
				String password = new BigInteger(130, random).toString(32).substring(5, 13);
				pst = con.prepareStatement("update user_details set password=?, reset_timestamp=? where user_id=?");
				pst.setString(1, password);
				pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(3, uF.parseToInt(strUserId.trim()));
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


	public String loadNotifications() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(*) as cnt from emp_leave_entry where emp_id =? and is_approved=1 and approval_from > ?");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();

			while(rs.next()){
				if(rs.getInt("cnt")>0){
					session.setAttribute("LEAVE_APPROVAL_COUNT", rs.getString("cnt"));
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select count(*) as cnt from company_manual where status=1 and _date >= ?");
			pst.setTimestamp(1, uF.getTimeStamp(uF.getPrevDate(CF.getStrTimeZone(), 7)+"", DBDATE));
			rs = pst.executeQuery();

			while(rs.next()){
				if(rs.getInt("cnt")>0){
					session.setAttribute("NEW_MANUAL", "N");
				}
			}
			rs.close();
			pst.close();
			
			
			
			
			pst = con.prepareStatement("select * from user_type");
			rs = pst.executeQuery();

			Map<String, String> hmUserModules = new HashMap<String, String>();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("visibility_id"))>0){
					hmUserModules.put(rs.getString("user_type"), true+"");
				}
			}
			rs.close();
			pst.close();
			
			session.setAttribute("hmUserModules", hmUserModules);
			
			List alNotice = new ArrayList();
			CF.getNoticeList(con, uF, CF, alNotice);
			request.setAttribute("NOTICE", alNotice); 
			session.setAttribute("NOTICE", alNotice);
			
//			HolidayReport holidayReport = new HolidayReport();
//			holidayReport.setServletRequest(request);
//			holidayReport.viewHolidayReport(uF);  
			
			
//			ThreadLoop objTL = new ThreadLoop(con, uF, CF, strEmpId);
//			objTL.run();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	public String loadAdminDashboard() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement(selectEmployee1V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();


			if (rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("EMPNAME", rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname"));
				request.setAttribute("DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
				request.setAttribute("IMAGE", ((rs.getString("emp_image")!=null && rs.getString("emp_image").length()>0)?rs.getString("emp_image"):"avatar_photo.png"));
				request.setAttribute("DEPT", rs.getString("dept_name"));
				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
				request.setAttribute("EMAIL", rs.getString("emp_email"));
			}
			rs.close();
			pst.close();
			

//			pst = con.prepareStatement(selectEmployee2V);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//
//			rs = pst.executeQuery();
//
//
//			if (rs.next()) {
//				request.setAttribute("DESIG", rs.getString("desig_name"));
//				
//
//			}

			pst = con.prepareStatement(selectEmployee3V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("MANAGER", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			pst = con.prepareStatement(selectPresentDays1);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
			rs = pst.executeQuery();
			if (rs.next()) {
				request.setAttribute("COUNT", rs.getString("count"));
			}
			rs.close();
			pst.close();


			pst = con.prepareStatement(selectApprovalsCountAdmin);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			int totalApprovals = 0;
			while (rs.next()) {

				if (rs.getInt("approved") == -2) {
					request.setAttribute("PENDING", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				} else if (rs.getInt("approved") == -1) {
					request.setAttribute("DENIED", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				} else if (rs.getInt("approved") == 1) {
					request.setAttribute("APPROVED", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("TOTAL_APPROVALS", totalApprovals);

			pst = con.prepareStatement(selectWLocation);
			rs = pst.executeQuery();
			String strW1 = null;
			String strW2 = null;
			List alBusinessName = new ArrayList();
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					strW1 = rs.getString("wlocation_name");

				} else if (i == 1) {
					strW2 = rs.getString("wlocation_name");
				} else {
					break;
				}
				alBusinessName.add(rs.getString("wlocation_name"));
				i++;

			}
			rs.close();
			pst.close();
			request.setAttribute("WLOCATION", alBusinessName);

			Map hmPresentDays = new HashMap();

			pst = con.prepareStatement(selectPresentDays2);
			pst.setString(1, strW1);
			pst.setString(2, strW2);
			pst.setDate(3, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
//			pst.setDate(3, uF.getDateFormat("2011-04-02", "yyyy-MM-dd"));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmPresentDays.put(rs.getString("wlocation_name"), rs.getString("present"));
			}
			rs.close();
			pst.close();

			request.setAttribute("PRENSENCE", hmPresentDays);

			Map hmEmployeeCount = new HashMap();
			Map hmAbsenceCount = new HashMap();
			pst = con.prepareStatement(selectEmployeeCount);
			rs = pst.executeQuery();

			while (rs.next()) {

				int presentEmp = 0;

				presentEmp = uF.parseToInt((String) hmPresentDays.get(rs.getString("wlocation_name")));

				hmAbsenceCount.put(rs.getString("wlocation_name"), (rs.getInt("empcount") - presentEmp) + "");
				hmEmployeeCount.put(rs.getString("wlocation_name"), rs.getString("empcount"));

			}
			rs.close();
			pst.close();

			request.setAttribute("EMPCOUNT", hmEmployeeCount);
			request.setAttribute("ABSENCE", hmAbsenceCount);
			
			
//			verifyClockDetails();
			
			
			
//			List alNotice = new ArrayList();
//			new CommonFunctions(CF).getNoticeList(alNotice);
//			request.setAttribute("NOTICE", alNotice);
//
//			HolidayReport holidayReport = new HolidayReport();
//			holidayReport.setServletRequest(request);
//			holidayReport.viewHolidayReport();
//			
//			
//			
//			List<Navigation> alParentNavL = new ArrayList<Navigation>();
//			Map<String, List<Navigation>> hmChildNavL = new HashMap<String, List<Navigation>>();
//			List<Navigation> alParentNavR = new ArrayList<Navigation>();
//			Map<String, List<Navigation>> hmChildNavR = new HashMap<String, List<Navigation>>();
//			new FillNavigation().fillNavigation(alParentNavL, hmChildNavL, alParentNavR, hmChildNavR, session);
			
			

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}

	public String loadManagerDashboard() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement(selectEmployee1V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				request.setAttribute("DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
				request.setAttribute("IMAGE", ((rs.getString("emp_image")!=null && rs.getString("emp_image").length()>0)?rs.getString("emp_image"):"avatar_photo.png"));
				request.setAttribute("DEPT", rs.getString("dept_name"));
				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
				request.setAttribute("EMAIL", rs.getString("emp_email"));
			}
			rs.close();
			pst.close();

//			pst = con.prepareStatement(selectEmployee2V);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//
//			rs = pst.executeQuery();
//
//
//			if (rs.next()) {
//				request.setAttribute("DESIG", rs.getString("desig_name"));
//				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
//
//			}

			pst = con.prepareStatement(selectEmployee3V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("MANAGER", rs.getString("emp_fname") +strEmpMName+" " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(selectMyClockEntries);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmMyAttendence = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hm = new HashMap<String, String>();
			String strDateNew = "";
			String strDateOld = "";
			while (rs.next()) {
				
				strDateNew = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
				if(strDateNew!=null && !strDateNew.equalsIgnoreCase(strDateOld)){
					hm = new HashMap<String, String>();
				}
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					hm.put("IN", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}else{
					hm.put("OUT", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}
				
				hmMyAttendence.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()), hm);
				strDateOld = strDateNew;
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMyAttendence", hmMyAttendence);
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			pst = con.prepareStatement(selectPresentDays1);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
			rs = pst.executeQuery();
			double []PRESENT_ABSENT_DATA = new double[2];
			String []PRESENT_ABSENT_LABEL = new String[2];
			if (rs.next()) {
				request.setAttribute("PRESENT_COUNT", rs.getString("count"));
				request.setAttribute("ABSENT_COUNT", Day - uF.parseToInt(rs.getString("count")));
				
				PRESENT_ABSENT_DATA[0] = rs.getDouble("count");
				PRESENT_ABSENT_DATA[1] = Day - uF.parseToInt(rs.getString("count"));
				
				PRESENT_ABSENT_LABEL[0] = "Worked";
				PRESENT_ABSENT_LABEL[1] = "Absent";
			}
			rs.close();
			pst.close();

			request.setAttribute("CHART_WORKED_ABSENT", new PieCharts().get3DPieChart(PRESENT_ABSENT_DATA, PRESENT_ABSENT_LABEL));
			
			pst = con.prepareStatement(selectApprovalsCount);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				
				if(uF.parseToInt(rs.getString("approved")) == 1){
					request.setAttribute("APPROVED_COUNT", rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -1){
					request.setAttribute("DENIED_COUNT", rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -2){
					request.setAttribute("WAITING_COUNT", rs.getString("count"));
				}  
			}
			rs.close();
			pst.close();
			
			
			double []pending = new double[1];
			double []approved = new double[1];
			double []denied = new double[1];
			String []label = new String[]{""};
			
			pst = con.prepareStatement(selectApprovalsCountForManager);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			int total=0;
			while (rs.next()) {
				
				if(uF.parseToInt(rs.getString("approved")) == 1){
					request.setAttribute("EMP_APPROVED_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
					approved[0] = uF.parseToInt(rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -1){
					request.setAttribute("EMP_DENIED_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
					denied[0] = uF.parseToInt(rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -2){
					request.setAttribute("EMP_WAITING_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
					pending[0] = uF.parseToInt(rs.getString("count"));
				}  
			}
			rs.close();
			pst.close();
			
			request.setAttribute("CHART_APPROVALS", new BarChart().getMulitCharts(pending, approved, denied, label));
			
			request.setAttribute("TOTAL", total+"");
			
			pst = con.prepareStatement(selectApprovals);			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			List alReasons = new ArrayList();
			rs = pst.executeQuery();
			while (rs.next()) {
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					if(rs.getDouble("early_late")>0){
						alReasons.add(rs.getString("emp_fname")+", is late for office"+((rs.getString("reason")!=null)?" because "+rs.getString("reason")+".":"."));
					}else if(rs.getDouble("early_late")<0){
						alReasons.add(rs.getString("emp_fname")+", has come early"+((rs.getString("reason")!=null)?" because "+rs.getString("reason")+".":"."));
					}
					
				}else{
					if(rs.getDouble("early_late")>0){
						alReasons.add(rs.getString("emp_fname")+", has left late"+((rs.getString("reason")!=null)?" because "+rs.getString("reason")+".":"."));
					}else if(rs.getDouble("early_late")<0){
						alReasons.add(rs.getString("emp_fname")+", has left"+((rs.getString("reason")!=null)?" because "+rs.getString("reason")+".":"."));
					}
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReasons",alReasons);
			
			
			verifyClockDetails();
			
			
//			List alNotice = new ArrayList();
//			new CommonFunctions(CF).getNoticeList(alNotice);
//			request.setAttribute("NOTICE", alNotice);
//			
//			
//			HolidayReport holidayReport = new HolidayReport();
//			holidayReport.setServletRequest(request);
//			holidayReport.viewHolidayReport();
			

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return LOAD;
	}

	public String loadEmpDashboard() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map hmServices = CF.getServicesMap(con, true);
			
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement(selectEmployee1V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				request.setAttribute("DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
				request.setAttribute("IMAGE", ((rs.getString("emp_image")!=null && rs.getString("emp_image").length()>0)?rs.getString("emp_image"):"avatar_photo.png"));
				request.setAttribute("DEPT", rs.getString("dept_name"));
				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
				request.setAttribute("EMAIL", rs.getString("emp_email"));
			}
			rs.close();
			pst.close();

//			pst = con.prepareStatement(selectEmployee2V);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//
//			rs = pst.executeQuery();
//
//
//			if (rs.next()) {
//				request.setAttribute("DESIG", rs.getString("desig_name"));
//				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
//
//			}

			pst = con.prepareStatement(selectEmployee3V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("MANAGER", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(selectMyClockEntries);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmMyAttendence = new LinkedHashMap<String, Map<String, String>>();
			Map hmMyAttendence1 = new LinkedHashMap();
			Map<String, String> hm = new HashMap<String, String>();
			String strDateNew = "";
			String strDateOld = "";
			String strServiceNewId = null;
			String strServiceOldId = null;
			List alServices = new ArrayList();
			while (rs.next()) {
				
				strDateNew = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
				strServiceNewId = rs.getString("service_id");
				if(strDateNew!=null && !strDateNew.equalsIgnoreCase(strDateOld)){
					hm = new HashMap();
				}
				
				if(strServiceNewId!=null && !strServiceNewId.equalsIgnoreCase(strServiceOldId)){
					hm = new HashMap();
				}
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					hm.put("IN", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}else{
					hm.put("OUT", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}
				hm.put("SERVICE", (String)hmServices.get(strServiceNewId));
				
				hmMyAttendence.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_"+strServiceNewId, hm);
				
				
				
				alServices = (List)hmMyAttendence1.get(strDateNew);
				if(alServices==null){
					alServices = new ArrayList();
				}
				if(!alServices.contains(strServiceNewId)){
					alServices.add(strServiceNewId);
				}
				
				
				hmMyAttendence1.put(strDateNew, alServices);
				
				
				strDateOld = strDateNew;
				strServiceOldId = strServiceNewId;
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMyAttendence", hmMyAttendence);
			request.setAttribute("hmMyAttendence1", hmMyAttendence1);
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			pst = con.prepareStatement(selectPresentDays1);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
			rs = pst.executeQuery();
			double []PRESENT_ABSENT_DATA = new double[2];
			String []PRESENT_ABSENT_LABEL = new String[2];
			if (rs.next()) {
				request.setAttribute("PRESENT_COUNT", rs.getString("count"));
				request.setAttribute("ABSENT_COUNT", Day - uF.parseToInt(rs.getString("count")));
				
				PRESENT_ABSENT_DATA[0] = rs.getDouble("count");
				PRESENT_ABSENT_DATA[1] = Day - uF.parseToInt(rs.getString("count"));
				
				PRESENT_ABSENT_LABEL[0] = "Worked";
				PRESENT_ABSENT_LABEL[1] = "Absent";
			}
			rs.close();
			pst.close();

			request.setAttribute("CHART_WORKED_ABSENT", new PieCharts().get3DPieChart(PRESENT_ABSENT_DATA, PRESENT_ABSENT_LABEL));
			
			double []pending = new double[1];
			double []approved = new double[1];
			double []denied = new double[1];
			String []label = new String[]{""};
			
			pst = con.prepareStatement(selectApprovalsCount);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				if(uF.parseToInt(rs.getString("approved")) == 1){
					request.setAttribute("APPROVED_COUNT", rs.getString("count"));
					approved[0] = uF.parseToInt(rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -1){
					request.setAttribute("DENIED_COUNT", rs.getString("count"));
					denied[0] = uF.parseToInt(rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -2){
					request.setAttribute("WAITING_COUNT", rs.getString("count"));
					pending[0] = uF.parseToInt(rs.getString("count"));
				}  
			}
			rs.close();
			pst.close();
			
			request.setAttribute("CHART_APPROVALS", new BarChart().getMulitCharts(pending, approved, denied, label));
			
			
			pst = con.prepareStatement(selectApprovalsCountForManager);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));

			rs = pst.executeQuery();
			int total=0;
			while (rs.next()) {
				
				if(uF.parseToInt(rs.getString("approved")) == 1){
					request.setAttribute("EMP_APPROVED_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -1){
					request.setAttribute("EMP_DENIED_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -2){
					request.setAttribute("EMP_WAITING_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
				}  
			}
			rs.close();
			pst.close();
			request.setAttribute("TOTAL", total+"");
			
			pst = con.prepareStatement(selectApprovals);			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			
			List alReasons = new ArrayList();
			rs = pst.executeQuery();
			while (rs.next()) {
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					if(rs.getDouble("early_late")>0){
						alReasons.add(rs.getString("emp_fname")+", is late for office and the provided reason is "+rs.getString("reason")+" reason");
					}else if(rs.getDouble("early_late")<0){
						alReasons.add(rs.getString("emp_fname")+", has come early because "+rs.getString("reason"));
					}
					
				}else{
					if(rs.getDouble("early_late")>0){
						alReasons.add(rs.getString("emp_fname")+", has left late because "+rs.getString("reason"));
					}else if(rs.getDouble("early_late")<0){
						alReasons.add(rs.getString("emp_fname")+", has left early because "+rs.getString("reason"));
					}
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReasons",alReasons);
			
			pst = con.prepareStatement(selectRosterEmployeeDetails);			
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
			
			Map hmRoster = new LinkedHashMap();
			Map hmRoster1 = new LinkedHashMap();
			Map hm1 = new HashMap();
			String strOldDate = null;
			String strNewDate;
			String strServiceId=null;
			rs = pst.executeQuery();
			alServices = new ArrayList();
			
			while (rs.next()) {
				hm1 = new HashMap();
				strNewDate = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());
				strServiceId = rs.getString("service_id");
				
				if(strNewDate!=null && !strNewDate.equalsIgnoreCase(strOldDate)){
					hm1 = new HashMap();
				}
				hm1.put("FROM", uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put("TO", uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put("SERVICE", (String)hmServices.get(strServiceId));
				
				
				alServices = (List)hmRoster1.get(strNewDate);
				if(alServices==null){
					alServices = new ArrayList();
				}
				if(!alServices.contains(strServiceId)){
					alServices.add(strServiceId);
				}
				
				
				hmRoster1.put(strNewDate, alServices);
				
				hmRoster.put(strNewDate+"_"+strServiceId, hm1);
				strOldDate = strNewDate;
			}
			rs.close();
			pst.close();			
			request.setAttribute("hmRoster",hmRoster);
			request.setAttribute("hmRoster1",hmRoster1);
			
			
			verifyClockDetails();
			
			
			
//			List alNotice = new ArrayList();
//			new CommonFunctions(CF).getNoticeList(alNotice);
//			
//			request.setAttribute("NOTICE", alNotice);
			
			
			
//			HolidayReport holidayReport = new HolidayReport();
//			holidayReport.setServletRequest(request);
//			holidayReport.viewHolidayReport();
//			
//			
//			List<Navigation> alParentNavL = new ArrayList<Navigation>();
//			Map<String, List<Navigation>> hmChildNavL = new HashMap<String, List<Navigation>>();
//			List<Navigation> alParentNavR = new ArrayList<Navigation>();
//			Map<String, List<Navigation>> hmChildNavR = new HashMap<String, List<Navigation>>();
//			new FillNavigation().fillNavigation(alParentNavL, hmChildNavL, alParentNavR, hmChildNavR, session);
			

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return LOAD;
	}

	
	String strClock;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrClock() {
		return strClock;
	}

	public void setStrClock(String strClock) {
		this.strClock = strClock;
	}
	
	
	public void verifyClockDetails(){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			
			con = db.makeConnection(con);
			
			String strPrevRosterDate = null;
			Time tPrevFrom = null;
			Time tPrevTo = null;
			
			String strRosterDate = null;
			String strFrom = null;
			String strTo = null;
			
			String strRosterStartTime = null;
			String strRosterEndTime = null;
			String strPrevRosterStartTime = null;
			String strPrevRosterEndTime = null;
			
			int nPrevServiceId = 0;
			int nCurrServiceId = 0;
			int nCount = 0;
			
			pst = con.prepareStatement(selectRoster_N_COUNT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			log.debug("pst nServiceId===>"+pst);
			
			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nPrevServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCurrServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
			if(nCurrServiceId==0){
				if(nCount>1){
					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				}else{
					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				}
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
			}
			
			
			if(nCurrServiceId==0){
				nCurrServiceId = nPrevServiceId;
			}
			
			pst = con.prepareStatement(selectAttendenceClockDetails_N);
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
			pst.setString(2, "OUT");
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, nPrevServiceId);
			rs = pst.executeQuery();
			boolean isPrevOut = false;
			boolean isPrevRoster = false;
			if(rs.next()){
				isPrevOut = true;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(selectAttendenceClockDetails_N);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(2, "IN");
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, nCurrServiceId);
			rs = pst.executeQuery();

			boolean isCurrIn = false;
			if(rs.next()){
				isCurrIn = true;
			}
			rs.close();
			pst.close();
			
			if(!isCurrIn && !isPrevOut){
				
				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				if(rs.next()){
					isPrevRoster = true;
					
					tPrevFrom = rs.getTime("_from");
					tPrevTo = rs.getTime("_to");
					strPrevRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
				
			}
			
			if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime()){
				
				pst = con.prepareStatement(selectRosterClockDetails_N_IN);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				strRosterDate = null;
				strFrom = null;
				strTo = null;
				while (rs.next()) {
					strPrevRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strPrevRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
			}else if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() > tPrevTo.getTime()){
				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				strRosterDate = null;
				strFrom = null;
				strTo = null;
				while (rs.next()) {
					strPrevRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strPrevRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
			}else{
				
				pst = con.prepareStatement(selectRosterClockDetails_N1);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setInt(3, nCurrServiceId);
				rs = pst.executeQuery();
				strRosterDate = null;
				strFrom = null;
				strTo = null;
				while (rs.next()) {
					strRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();

			}
			
			pst = con.prepareStatement(selectAttendenceClockDetails1_N);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));			
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, nCurrServiceId);
			rs = pst.executeQuery();
			boolean isIn=false;
			boolean isOut=false;
			while (rs.next()) {

				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
					isIn=true;
				}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
					isOut=true;
				}
				
			}
			rs.close();
			pst.close();
			
						
			if(isIn && isOut){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered end time was :"+strRosterEndTime:""));
			}else if(!isIn && !isOut && strRosterStartTime!=null){
				request.setAttribute("ROSTER_TIME", ((strRosterStartTime!=null)?"Your rostered start time is :"+strRosterStartTime:""));
			}else if(!isIn && !isOut && strPrevRosterStartTime!=null){
				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setString(2, "IN");
				pst.setString(3, "OUT");
				pst.setInt(4, uF.parseToInt(strEmpId));
				pst.setInt(5, nPrevServiceId);
				rs = pst.executeQuery();
				
				boolean isPrevIn = false;
				isPrevOut = false;
				
				while(rs.next()){
					
					if(rs.getString("in_out").equalsIgnoreCase("IN")){
						isPrevIn = true;
					}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
						isPrevOut = true;
					} 
				}
				rs.close();
				pst.close();
				
				if(isPrevIn && isPrevOut){
					request.setAttribute("ROSTER_TIME", "Your are not rostered for today");
				}else if(isPrevIn && !isPrevOut){
					request.setAttribute("ROSTER_TIME", ((strPrevRosterEndTime!=null)?"Your rostered end time is :"+strPrevRosterEndTime:""));
				}else if(!isPrevIn && !isPrevOut && CF.isRosterDependency(con,strEmpId)){
//					request.setAttribute("ROSTER_TIME", ("Your are not rostered for today"));
					request.setAttribute("ROSTER_TIME", ((strPrevRosterStartTime!=null)?"Your rostered start time is :"+strPrevRosterStartTime:""));
				}else if(!isPrevIn && !isPrevOut && !CF.isRosterDependency(con,strEmpId)){
					request.setAttribute("ROSTER_TIME", ((strPrevRosterStartTime!=null)?"Your rostered start time is :"+strPrevRosterStartTime:""));					
				}
				
				
			}else if(isIn){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered end time is :"+strRosterEndTime:""));
			}else if(isOut){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered start time is :"+strRosterStartTime:""));
			}
			
			
			
			
			
			/**
			 *  IS Roster Dependent
			 */
			
			pst = con.prepareStatement(selectRosterDependent);
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			String strEmpType = null;
			boolean isRoster = false;
			if(rs.next()){
				strEmpType = rs.getString("emptype");
				isRoster = uF.parseToBoolean(rs.getString("is_roster"));
			}
			rs.close();
			pst.close();
			
			
			if(strEmpType!=null && !isRoster){
				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut);
//				pst.setDate(1, uF.getCurrentDate());
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				rs = pst.executeQuery();
//				
//				while(rs.next()){
//					
//					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
//						isIn = true;	
//					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
//						isOut = true;
//					}
//				}
//				
//				
//				
				request.setAttribute("ROSTER_TIME", "");
				
				
			}
			
			
			
			request.setAttribute("CURRENT_DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "EEEE "+CF.getStrReportDateFormat()));
			request.setAttribute("CURRENT_TIME", uF.getDateFormat(uF.getCurrentTime(CF.getStrTimeZone())+"", DBTIME, CF.getStrReportTimeFormat()));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
//===start parvez date: 31-03-2022===
	public void getMISReportUrl(){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from navigation_1 where _label=? and user_type_id like ?");
			pst.setString(1, "MIS Reports");
			pst.setString(2, "%,"+(String) session.getAttribute(BASEUSERTYPEID)+",%");
//			System.out.println("Dashboard/1862--pst="+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				setNavigationId(rs.getString("navigation_id"));
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
	}
//===end parvez date: 31-03-2022===	
	
	public String loadHRManagerDashboard() {

		return LOAD;
	}
	
	public String loadAccountantDashboard() {

		return LOAD;
	}
	
	public String loadCEODashboard() {

		return LOAD;
	}
	
	public String loadCFODashboard() {

		return LOAD;
	}


	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getStrMV() {
		return strMV;
	}

	public void setStrMV(String strMV) {
		this.strMV = strMV;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	public String getToAction() {
		return toAction;
	}

	public void setToAction(String toAction) {
		this.toAction = toAction;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getToTab() {
		return toTab;
	}

	public void setToTab(String toTab) {
		this.toTab = toTab;
	}

	public String getStrCFYear() {
		return strCFYear;
	}

	public void setStrCFYear(String strCFYear) {
		this.strCFYear = strCFYear;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getProfileEmpId() {
		return profileEmpId;
	}

	public void setProfileEmpId(String profileEmpId) {
		this.profileEmpId = profileEmpId;
	}

	public String getSalaryBand() {
		return salaryBand;
	}

	public void setSalaryBand(String salaryBand) {
		this.salaryBand = salaryBand;
	}

}


	class ThreadLoop implements Runnable {
	
		CommonFunctions CF;
		String strEmpId;
		Connection con;
		UtilityFunctions uF;
		
		public ThreadLoop(Connection con, UtilityFunctions uF, CommonFunctions CF, String strEmpId) {
			this.CF = CF;
			this.strEmpId = strEmpId;
			this.con = con;
			this.uF = uF;
		}
		
		@Override
		public void run() {
			try {
				CF.updateNewEmployeeLeaveRegister(con, uF, strEmpId, CF);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
