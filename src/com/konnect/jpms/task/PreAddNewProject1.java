package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.GetJobCodeDetails;
import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillClientPoc;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillProjectDomain;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadProjectDocuments;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PreAddNewProject1 extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	
	List<String> alInner = new ArrayList<String>(); 
	List<Integer> emp_id = new ArrayList<Integer>();
	String msg;
	CommonFunctions CF;
	HttpSession session;
	String strSessionUserId;
	String strUserType;
	String strSessionEmpId;
	String strOrgId;
	
	List<FillServices> serviceList; 
	List<FillServices> sbuList;
	List<FillLevel> levelList;
	List<FillWLocation> workLocationList; 
	List<FillOrganisation> organisationList;
	
	String pro_id;
	List<FillEmployee> projectOwnerList;
	List<FillEmployee> projectReferanceList;
	List<FillEmployee> projectRelationShipList;
	List<FillEmployee> portfolioManagerList;
	List<FillEmployee> accountManagerList;
	List<FillEmployee> deliveryManagerList;
	List<FillProjectDomain> projectDomainList;		//add by parvez date: 21-11-2022
	
//	String strProjectOwner;
	List<String> strProjectOwner;
	String strPortfolioManager;
	String strAccountManager;
	String strDeliveryManager;
	String strReferanceBy;
	String strRelationShipBy;
	String clientID;
	String strOrg;
	
	String strActualBilling;
	String strActualBillingName;

	List<GetPriorityList> priorityList;
	List<FillEmployee> empNamesList;
	List<FillEmployee> teamleadNamesList;
	List<FillSkills> skillList;
	List<FillClients> clientList;
	List<FillClientBrand> clientBrandList;
	List<FillClientPoc> clientPocList;
	List<FillDaysList> daysList;
	List<FillBillingType> billingList;
	List<FillDependentTaskList> dependencyList;
	List<GetDependancyTypeList> dependancyTypeList;
	List<FillTaskEmpList> TaskEmpNamesList;
	List<FillSkills> empSkillList;
	List<FillBillingHeads> billingHeadDataTypeList;
	List<FillBillingHeads> billingHeadOtherVariableList;
	
	List<FillBillingType> billingKindList;
	String strBillingKind;
	String strBillingKindName;
	
	String step;
	String submit;
	String stepSave;
	String ids;
	String operation;
	String[] skill;
	
	List<FillCurrency> currencyList;
	String strCurrency;
	String strBillingCurrency;

	
	String[] milestoneId;
	String[] milestoneName;
	String[] milestoneDescription;
	String[] projectTask;
	String[] milestonePercent;
	String[] milestoneAmount;
	
	String[] billingHeadId;
	String[] mBillingHeadId;
	String[] billingHeadTRId;
	String[] billingHeadLabel;
	String[] billingHeadDataType;
	
	String[] taxHeadId;
	String[] mTaxHeadId;
	String[] taxHeadLabel;
	String[] taxNameLabel;
	String[] taxHeadPercent;
	String[] taxHeadDeductionType;
	String[] taxHeadStatus;
	
	String invoiceAdditionalInfo;
	
	String strInvoiceTemplate;
	List<FillInvoiceFormat> invoiceTemplateList;
	
	List<FillBank> bankList;
	
	String chkBank;
	String chkPaypal;
	
	String bankName;
	String strPaypal;
	
	String strAccountRef;
	String strPONo;
	String strTerms;
	String strDueDate;
	
	String fromPage;
	String strTaskId;

	String pageType;
	String proType;

	String strProOwnerOrTL;
	
	String[] skillTRId;
	String[] proResourceReqId;
	String[] strWLocation;
	String[] strWLocationFilter;
	String[] requiredSkill;
	String[] reqMinExp;
	String[] reqMaxExp;
	String[] reqMinExpFilter;
	String[] reqMaxExpFilter;
	String[] reqResource;
	String[] reqResourceGap;
	
	String proDomain;
	String ustPrjectId;
	String projectAccountId;
	String projectSegment;
	String revenueTarget;
	
	public String execute() throws Exception {
		session = request.getSession();
		strSessionUserId = (String)session.getAttribute(USERID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strOrgId = (String)session.getAttribute(ORGID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		request.setAttribute(PAGE, "/jsp/task/AddNewProject1.jsp");
		request.setAttribute(TITLE, "Add New Project");
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());

		operation = request.getParameter("operation");
		UtilityFunctions uF = new UtilityFunctions();
		clientPocList = new ArrayList<FillClientPoc>();
		clientBrandList = new ArrayList<FillClientBrand>();
		
		//====start parvez on 12-07-2021=====
		getProInfoDisplay();
		//====end parez on 12-07-2021=====
		
		Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(request);
		request.setAttribute("hmFeatureStatus", hmFeatureStatus);
		
//		System.out.println("pageType ===>> " + getPageType() + " --- proType ===>> " + getProType() + " --- fromPage ===>> " + getFromPage());
		
		if(uF.parseToInt(getPro_id()) > 0 && getPageType() != null && getPageType().equals("MP")) {
			checkSessionEmpIsProjectOwnerOrTL(uF);
		}
		
		if(getPageType() == null || getPageType().equals("") || getPageType().equals("null")) {
			setPageType(null);
		}
		
		if(uF.parseToInt(getPro_id()) == 0 && getStrClient() == null) {
			setStrClient(getClientID());
		}
//		System.out.println("Refernce by--"+strReferanceBy);
//		System.out.println("Rel--"+strRelationShipBy);
		
//		System.out.println("opration==============" + operation);
//		System.out.println("step  ==== " + getStep());
//		System.out.println("getPro_id()  ==== " + getPro_id());
//		System.out.println("getSubmit  ==== " + getSubmit());
//		System.out.println("getStepSave  ==== " + getStepSave());
		
		if(getStrActualBilling() == null) {
			setStrActualBilling("D");
		}
		
		if (uF.parseToInt(getStep()) == 0) {
			 setStep("1");
			if (operation != null) {
				getProjectInfoStep1(uF);
			}   

		} else if (uF.parseToInt(getStep()) == 1) {
			setStep("2");
			if(getSubmit() != null || getStepSave() != null) {
				if (operation != null && operation.length()>0 && getPrjectname()!=null && getPrjectname().length()>0) {
					updateProjectInfoStep1(uF);
				} else if(getPrjectname()!=null && getPrjectname().length()>0) {
					saveProjectInfoStep1(uF);
				}
			}
			getSkills(uF);

		} else if (uF.parseToInt(getStep()) == 2) {
			setStep("3");
//			 This step is handled by ajax 
			  
			if(getSubmit()!=null && getSubmit().equals("CreateResReq")) {
				setStep("2");
				createResourceRequirement(uF);
				getSkills(uF);
			} else {
			 if (operation != null && operation.length()>0) {
				if(strUserType!=null && strUserType.equals(RECRUITER)) {
					saveProjectInfoStep2(uF);
				}
			} else {
				if(strUserType!=null && strUserType.equals(RECRUITER)) {
					saveProjectInfoStep2(uF);
				}
			}
			 	dependencyList = new FillDependentTaskList(request).fillDependentTaskList(uF.parseToInt(getPro_id()));
				dependancyTypeList = new GetDependancyTypeList().fillDependancyTypeList();
				TaskEmpNamesList = new FillTaskEmpList(request).fillEmployeeName(uF.parseToInt(getPro_id()));
				String skillIds = getProjectEmpSkillIds(uF, uF.parseToInt(getPro_id())); 
				empSkillList = new FillSkills(request).fillSkillNameByIds(skillIds);
				getActivity(uF);
				
				get(uF);
			}
		} else if (uF.parseToInt(getStep()) == 3) {
			setStep("4");
//			System.out.println("getStep() ===>> " + getStep());
			if(getSubmit() != null || getStepSave() != null) {
//				System.out.println("operation ===>> " + operation);
				if (operation != null && operation.length()>0) {
					String[] taskname = request.getParameterValues("taskname");
//					System.out.println("taskname ===>> " + taskname.length);
					if(taskname!=null && taskname.length>0) {
						insertActivityDetails(uF);
					}
				} else {
					insertActivityDetails(uF);
				}
			}
			getProjectAndTaskDocuments(uF);
			
		} else if (uF.parseToInt(getStep()) == 4) {
			setStep("6");
			getEmpTaskList(uF, 0);
			getProjectBillingData(uF);
			getProjectMilestones(uF);
			invoiceTemplateList = new FillInvoiceFormat(request).fillFillInvoiceFormat();
			bankList = new FillBank(request).fillBankAccNo();
//			System.out.println("getStep() 4 ==== " + getStep());
//			System.out.println("getStep() getSubmit() 4 ==== " + getSubmit());
			if (operation != null && operation.length()>0 && (getSubmit() != null || getStepSave() != null)) {
//				updateProjectBillingInfoStep4(uF);
				createFolderForDocs(uF);
				if(getStepSave() != null) {
					if(getPageType() != null && getPageType().equals("MP")) {
						return "mydocexit";
					} else {
						return "docexit";
					}
				} else {
					if(getPageType() != null && getPageType().equals("MP")) {
						return "mydocsuccess";
					} else {
						return "docsuccess";
					}
				}
			} else if((getSubmit() != null || getStepSave() != null)) {
				createFolderForDocs(uF);
				if(getPageType() != null && getPageType().equals("MP")) {
					return "mydocsuccess";
				} else {
					return "docsuccess";
				}
//				insertProjectBillingInfoStep4(uF);
			}
		} else if (uF.parseToInt(getStep()) == 5) {
//			setStep("6");
//			getEmpTaskList(uF, 0);
//			getProjectBillingData(uF);
//			getProjectMilestones(uF);
//			invoiceTemplateList = new FillInvoiceFormat(request).fillFillInvoiceFormat();
//			bankList = new FillBank(request).fillBankAccNo();
		} else if (uF.parseToInt(getStep()) == 6) {
			setStep("7");
			if (operation != null && operation.length()>0 && (getSubmit() != null || getStepSave() != null)) {
				updateProjectBillingInfoStep5(uF);
			} else if((getSubmit() != null || getStepSave() != null)) {
				insertProjectBillingInfoStep5(uF);
			}
			getDetails(uF);
		} else if (uF.parseToInt(getStep()) == 7) {
			setStep("8");
			setDetails(uF);
			getFullDetails(uF);
			// return "report";
		} else if(uF.parseToInt(getStep()) == 8) {
			sendCreateProjectNotification(uF);
			if(getPageType() != null && getPageType().equals("MP")) {
				return MYSUCCESS;
			} else {
				return "report";
			}
		}
		
		if(uF.parseToInt(getService())<=0 && serviceList!=null && serviceList.size()>0) {
			String servId = new FillServices(request).getSBUIdOnProjectServiceId(uF, serviceList.get(0).getServiceId());
			setService(servId);
		}
		
		priorityList = new GetPriorityList().fillPriorityList();
		serviceList = new FillServices(request).fillProjectServices();
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			workLocationList = new FillWLocation(request).fillWLocation(getOrganisation(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			workLocationList = new FillWLocation(request).fillWLocation(getOrganisation());
		}
//		workLocationList = new FillWLocation(request).fillWLocation(getOrganisation());
//		organisationList = new FillOrganisation(request).fillOrganisation();
		projectOwnerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		projectReferanceList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		projectRelationShipList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		
//===start parvez date: 11-10-2022===		
		portfolioManagerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		accountManagerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		deliveryManagerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
//===end parvez date: 11-10-2022===		
		
		/*if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			workLocationList = new FillWLocation(request).fillWLocation(getOrganisation(), (String)session.getAttribute(WLOCATION_ACCESS));
			if((String)session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				projectOwnerList = new FillEmployee(request).fillEmployeeNameByLocation((String)session.getAttribute(WLOCATION_ACCESS));
				projectReferanceList = new FillEmployee(request).fillEmployeeNameByLocation((String)session.getAttribute(WLOCATION_ACCESS));
				projectRelationShipList = new FillEmployee(request).fillEmployeeNameByLocation((String)session.getAttribute(WLOCATION_ACCESS));
			} 
		} */
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrganisation()));
		sbuList = new FillServices(request).fillServices(getOrganisation(), uF);
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrganisation()));
//		System.out.println("getService() ===>> " + getService());
		empNamesList = new FillEmployee(request).fillEmployeeNameByServiceID(getService());
		teamleadNamesList = new FillEmployee(request).fillEmployeeNameByServiceID(getService());
		//skillList = new FillSkills().fillProjectSkills(uF.parseToInt(getPro_id()));
		skillList = new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(getOrganisation()));
		clientList = new FillClients(request).fillClients(false);
		
		// clientPocList = new FillClientPoc().fillClientPoc();
		
		
		daysList = new FillDaysList().fillDayList();
		billingList = new FillBillingType().fillBillingTypeList();
		billingKindList = new FillBillingType().fillBillingKindList();//new ArrayList<FillBillingType>();
		
		currencyList= new FillCurrency(request).fillCurrency();
		
		if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION))){
			projectDomainList = new FillProjectDomain(request).fillProjectDomain();		//added by parvez date: 12-11-2022
		}
		
		if(getOperation() != null && getOperation().length() > 0) {
			setOperation(getOperation());
		}
		
		if(uF.parseToInt(getPro_id()) > 0) {
			getProjectDetails(uF);
			request.setAttribute(TITLE, "Edit Project");
		}
		
//		System.out.println("getPageType() ===>> " + getPageType() + " -- getStepSave() ===================>> " + getStepSave());
		if (getStepSave() != null && getStepSave().equals("SaveAndExit")) {
			if(getPageType() != null && getPageType().equals("MP")) {
				return MYSUCCESS;
			} else {
				return "report";
			}
		}
//		System.out.println("SUCCESS ===>> " + SUCCESS);
		return SUCCESS;
		
	}

	

	private void createResourceRequirement(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			boolean resReqFlag = false;
			for(int i=0; getSkillTRId()!=null && i<getSkillTRId().length; i++) {
//				System.out.println("getSkillTRId()[i] ===>> " + getSkillTRId()[i]);
				setStrWLocation(request.getParameterValues("strWLocation"+getSkillTRId()[i]));
				StringBuilder sbWLoc = null;
//				System.out.println("getStrWLocation().length ===>> " + getStrWLocation().length);
				for(int a=0; getStrWLocation()!=null && a<getStrWLocation().length; a++) {
					if(sbWLoc==null) {
						sbWLoc = new StringBuilder();
						sbWLoc.append(getStrWLocation()[a]);
					} else {
						sbWLoc.append(","+getStrWLocation()[a]);
					}
				}
				if(sbWLoc==null) {
					sbWLoc = new StringBuilder();
				}
				
				if(strUserType !=null && strUserType.equals(RECRUITER)) {
					setStrWLocationFilter(request.getParameterValues("strWLocationFilter"+getSkillTRId()[i]));
					StringBuilder sbWLocFilter = null;
					for(int a=0; getStrWLocationFilter()!=null && a<getStrWLocationFilter().length; a++) {
						if(sbWLocFilter==null) {
							sbWLocFilter = new StringBuilder();
							sbWLocFilter.append(getStrWLocationFilter()[a]);
						} else {
							sbWLocFilter.append(","+getStrWLocationFilter()[a]);
						}
					}
					if(sbWLocFilter==null) {
						sbWLocFilter = new StringBuilder();
					}
					pst = con.prepareStatement("update project_resource_req_details set wloc_ids_filter=?,min_exp_filter=?,max_exp_filter=? where project_resource_req_id=?");
					pst.setString(1, sbWLocFilter.toString());
					pst.setDouble(2, uF.parseToDouble(getReqMinExpFilter()[i]));
					pst.setDouble(3, uF.parseToDouble(getReqMaxExpFilter()[i]));
					pst.setInt(4, uF.parseToInt(getProResourceReqId()[i]));
					pst.executeUpdate();
//					System.out.println("pst rec ===>> " + pst);
				} else {
					if(uF.parseToInt(getProResourceReqId()[i])>0) {
						pst = con.prepareStatement("update project_resource_req_details set skill_id=?,wloc_ids=?,min_exp=?,max_exp=?,req_resource=?," +
							"resource_gap=?,updated_by=?,update_date=? where project_resource_req_id=?");
						pst.setInt(1, uF.parseToInt(getRequiredSkill()[i]));
						pst.setString(2, sbWLoc.toString());
						pst.setDouble(3, uF.parseToDouble(getReqMinExp()[i]));
						pst.setDouble(4, uF.parseToDouble(getReqMaxExp()[i]));
						pst.setDouble(5, uF.parseToDouble(getReqResource()[i]));
						pst.setDouble(6, uF.parseToDouble(getReqResourceGap()[i]));
						pst.setInt(7, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
						pst.setInt(9, uF.parseToInt(getProResourceReqId()[i]));
						pst.executeUpdate();
//						System.out.println("pst ===>> " + pst);
					} else {
						pst = con.prepareStatement("insert into project_resource_req_details(pro_id,skill_id,wloc_ids,min_exp,max_exp,req_resource,resource_gap," +
							"added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?)");
						pst.setInt(1, uF.parseToInt(getPro_id()));
						pst.setInt(2, uF.parseToInt(getRequiredSkill()[i]));
						pst.setString(3, sbWLoc.toString());
						pst.setDouble(4, uF.parseToDouble(getReqMinExp()[i]));
						pst.setDouble(5, uF.parseToDouble(getReqMaxExp()[i]));
						pst.setDouble(6, uF.parseToDouble(getReqResource()[i]));
						pst.setDouble(7, uF.parseToDouble(getReqResourceGap()[i]));
						pst.setInt(8, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(9, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
						pst.executeUpdate();
//						System.out.println("pst ===>> " + pst);
					}
					
					resReqFlag = true;
				}
			}
			
			if(resReqFlag) {
				request.setAttribute(MESSAGE, SUCCESSM+"Your resource request has been created successfully!"+END);
			}
//			System.out.println("getStrProOwnerOrTL() ===>> " + getStrProOwnerOrTL());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private void checkSessionEmpIsProjectOwnerOrTL(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			boolean flag = CF.getFeatureManagementStatus(request, uF, F_SHOW_ALL_PRO_DATA_TO_TL);
			pst = con.prepareStatement("select * from project_emp_details where _isteamlead=true and pro_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!flag) {
					setStrProOwnerOrTL("2");
				}
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and project_owner=? ");
			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and project_owners like '%,"+strSessionEmpId+",%' ");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			pst.setInt(2, uF.parseToInt(strSessionEmpId)); 
			rs = pst.executeQuery();
			while (rs.next()) {
				setStrProOwnerOrTL("1");
			}
			rs.close();
			pst.close();
//			System.out.println("getStrProOwnerOrTL() ===>> " + getStrProOwnerOrTL());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void sendCreateProjectNotification(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			//Map<String, String> hmEmpLevelMap = cF.getEmpLevelMap();
			con = db.makeConnection(con);

			if(getOperation() == null || !getOperation().equals("E")) {
				List<String> empList = new ArrayList<String>();
				pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				String proName = null;
				String proOwner = null;
				String proDescription = null;
				String clientId = null;
				String orgName = null;
				while(rs.next()) {
					/*if(uF.parseToInt(rs.getString("project_owner"))> 0) {
						empList.add(rs.getString("project_owner"));
					}*/
					proName = rs.getString("pro_name");
//					proOwner = CF.getEmpNameMapByEmpId(con, rs.getString("project_owner"));
					if(rs.getString("project_owners") !=null && !rs.getString("project_owners").equals("")){
						List<String> proOwnerList = Arrays.asList(rs.getString("project_owners").split(","));
						StringBuilder sbProOwners = null;
						for(int k=1;k<proOwnerList.size();k++){
							
							if(uF.parseToInt(proOwnerList.get(k)) > 0){
								empList.add(proOwnerList.get(k));
							}
							
							if(sbProOwners==null){
								sbProOwners = new StringBuilder();
								sbProOwners.append(CF.getEmpNameMapByEmpId(con, proOwnerList.get(k)));
							} else{
								sbProOwners.append(", "+CF.getEmpNameMapByEmpId(con, proOwnerList.get(k)));
							}
						}
						
						proOwner = sbProOwners.toString();
					}
					proDescription = rs.getString("short_description");
					clientId = rs.getString("poc");
					orgName = CF.getOrgNameById(con, rs.getString("org_id"));
					
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select ped.emp_id, epd.emp_fname, epd.emp_lname from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and is_alive = true " +
						"and ped.pro_id=? and ped._isteamlead = true");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				while(rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id"))> 0 && !empList.contains(rs.getString("emp_id"))) {
						empList.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_CREATE_NEW_PROJECT, CF); 
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrOrgId(strOrgId);
				nF.setEmailTemplate(true);
				nF.setStrEmpId(strSessionEmpId);
				
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(clientId));
				rs = pst.executeQuery();
				boolean flg=false;
				while(rs.next()) {
					nF.setStrCustFName(rs.getString("contact_fname"));
					nF.setStrCustLName(rs.getString("contact_lname"));
					nF.setStrEmpMobileNo(rs.getString("contact_number"));
					if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
						nF.setStrEmpEmail(rs.getString("contact_email"));
						nF.setStrEmailTo(rs.getString("contact_email"));
					}
					flg = true;
				}
				rs.close();
				pst.close();
				
				if(flg) {
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrProjectName(proName);
					nF.setStrProjectOwnerName(proOwner);
					nF.setStrProjectDescription(proDescription);
					nF.setStrOrgName(orgName);
					nF.sendNotifications(); 
				}
				
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
					Map<String, String> hmEmpName = new HashMap<String, String>();
					pst = con.prepareStatement("select * from employee_personal_details epd where epd.emp_per_id=? ");
					pst.setInt(1, uF.parseToInt(empList.get(i)));
					rs = pst.executeQuery();
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("emp_per_id"))> 0) {
							hmEmpName.put(rs.getString("emp_per_id")+"_FNAME", rs.getString("emp_fname"));
							hmEmpName.put(rs.getString("emp_per_id")+"_LNAME", rs.getString("emp_lname"));
							if(rs.getString("emp_email_sec") !=null && rs.getString("emp_email_sec").indexOf("@")>0) {
								hmEmpName.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email_sec"));
							} else if(rs.getString("emp_email") !=null && rs.getString("emp_email").indexOf("@")>0) {
								hmEmpName.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email"));
							}
							hmEmpName.put(rs.getString("emp_per_id")+"_CONTACT_NO", rs.getString("emp_contactno_mob"));
						}
					}
					rs.close();
					pst.close();
					
					nF.setStrCustFName(hmEmpName.get(empList.get(i)+"_FNAME"));
					nF.setStrCustLName(hmEmpName.get(empList.get(i)+"_LNAME"));
					nF.setStrEmpMobileNo(hmEmpName.get(empList.get(i)+"_CONTACT_NO"));
					nF.setStrEmpEmail(hmEmpName.get(empList.get(i)+"_EMAIL"));
					nF.setStrEmailTo(hmEmpName.get(empList.get(i)+"_EMAIL"));
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrProjectName(proName);
					nF.setStrProjectOwnerName(proOwner);
					nF.setStrProjectDescription(proDescription);
					nF.setStrOrgName(orgName);
					nF.sendNotifications();
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



	private void getProInfoDisplay() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmProInfoDisplay = CF.getProjectInformationDisplay(con);
			request.setAttribute("hmProInfoDisplay", hmProInfoDisplay);
//			setStrCurrency(CF.getOrgCurrencyIdByOrg(con, strOrgId));
//			setStrBillingCurrency(CF.getOrgCurrencyIdByOrg(con, strOrgId));
			
//			System.out.println("sbSkillIds ===>>> " + sbSkillIds);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}


	private String getProjectEmpSkillIds(UtilityFunctions uF, int proId) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbSkillIds = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbEmpIds = null;
			List<String> alEmpID = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=?");
			pst.setInt(1, proId);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(!alEmpID.contains(rs.getString("emp_id"))) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(rs.getString("emp_id"));
					} else {
						sbEmpIds.append(","+rs.getString("emp_id"));
					}
					alEmpID.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			
//			System.out.println("sbEmpIds ===>>> " + sbEmpIds);
			if(sbEmpIds.length()>0) {
				List<String> alSkillIds = new ArrayList<String>();
				pst = con.prepareStatement("select skill_id from skills_description where emp_id in ("+sbEmpIds.toString()+")");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(!alSkillIds.contains(rs.getString("skill_id"))) {
						if(sbSkillIds == null) {
							sbSkillIds = new StringBuilder();
							sbSkillIds.append(rs.getString("skill_id"));
						} else {
							sbSkillIds.append(","+rs.getString("skill_id"));
						}
						alSkillIds.add(rs.getString("skill_id"));
					}
				}
				rs.close();
				pst.close();
			}
			if(sbSkillIds == null) {
				sbSkillIds = new StringBuilder();
			}
//			System.out.println("sbSkillIds ===>>> " + sbSkillIds);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbSkillIds.toString();
	}



	private void getProjectBillingData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCurr = CF.getCurrencyDetails(con);
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select * from projectmntnc where pro_id=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				while(rs.next()) {
					Map<String, String> hmInnerCurr = hmCurr.get(rs.getString("curr_id"));
					Map<String, String> hmInnerCurr1 = hmCurr.get(rs.getString("billing_curr_id"));
					if(hmInnerCurr == null) hmInnerCurr = new HashMap<String, String>();
					if(hmInnerCurr1 == null) hmInnerCurr1 = new HashMap<String, String>();
					
					setOrganisation(rs.getString("org_id"));
					setStrCurrency(hmInnerCurr.get("LONG_CURR"));
					setStrBillingCurrency(hmInnerCurr1.get("LONG_CURR"));
					setStrBillingKindName(CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
					setBillingTypeName(CF.getBillinType(rs.getString("billing_type")));
	//				setStrCurrency(rs.getString("curr_id"));
	//				setStrBillingCurrency(rs.getString("billing_curr_id"));
					setStrBillingKind(rs.getString("billing_kind"));
					setBillingType(rs.getString("billing_type"));
					setWeekdayCycle(rs.getString("billing_cycle_weekday"));
					setDayCycle(rs.getString("billing_cycle_day"));
					if(rs.getString("bank_id") != null) {
						setChkBank("checked");
					} 
					if(rs.getString("paypal_mail_id") != null) {
						setChkPaypal("checked");
					} 
					setBankName(uF.showData(rs.getString("bank_id"), ""));
					setStrPaypal(uF.showData(rs.getString("paypal_mail_id"), ""));
					
					setStrAccountRef(uF.showData(rs.getString("acc_ref"), ""));
					setStrPONo(uF.showData(rs.getString("po_no"), ""));
					setStrTerms(uF.showData(rs.getString("terms"), ""));
					setStrDueDate(uF.showData(uF.getDateFormat(rs.getString("bill_due_date"), DBDATE, DATE_FORMAT), ""));
					
					if (billingType != null && (billingType.equals("H") || billingType.equals("D") || billingType.equals("M"))) 
						setBillingAmountH(rs.getString("billing_amount"));
					else
						setBillingAmountF(rs.getString("billing_amount"));
					setEstimatedHours(rs.getString("idealtime"));
					setStrActualBillingName(CF.getBillinType(rs.getString("actual_calculation_type")));
					setStrActualBilling(rs.getString("actual_calculation_type"));
					setMilestoneDependentOn(rs.getString("milestone_dependent_on"));
					setStrInvoiceTemplate(rs.getString("invoice_template_type"));
					
					setHoursToDay(rs.getString("bill_days_type"));
					setHoursForDay(uF.parseToDouble(rs.getString("hours_for_bill_day")) > 0 ? rs.getString("hours_for_bill_day") : "");
				}
				rs.close();
				pst.close();
			}
			
//			Map<String, String> hmBillingHeadDataType = uF.getBillingHeadDataType();
			billingHeadDataTypeList = new FillBillingHeads().fillBillingHeadDataTypeListBillingTypewise(getBillingType());
			billingHeadOtherVariableList = new FillBillingHeads().fillBillingHeadOtherVariableListBillingTypewise(getBillingType());
			
			StringBuilder sbBHDatatype = new StringBuilder("<select name=\"billingHeadDataType\" id=\"billingHeadDataType\" class=\"validateRequired\" style=\"width:150px !important;\" ><option value=\"\">Select Data Type</option>");
			
			for(FillBillingHeads fillBillingHeadDataTypeList: billingHeadDataTypeList) {
				sbBHDatatype.append("<option value=\""+fillBillingHeadDataTypeList.getHeadId()+"\">"+fillBillingHeadDataTypeList.getHeadName()+"</option>");
			}
			sbBHDatatype.append("</select>");
			
			StringBuilder sbBHOtherVariable = new StringBuilder("<select name=\"billingHeadOtherVariable\" id=\"billingHeadOtherVariable\" class=\"validateRequired\" style=\"width:160px !important;\" ><option value=\"\">Select Other Variable</option>");
			
			for(FillBillingHeads fillBillingHeadOtherVariableList: billingHeadOtherVariableList) {
				sbBHOtherVariable.append("<option value=\""+fillBillingHeadOtherVariableList.getHeadId()+"\">"+fillBillingHeadOtherVariableList.getHeadName()+"</option>");
			}
			sbBHOtherVariable.append("</select>");
			
			request.setAttribute("sbBHDatatype", sbBHDatatype);
			request.setAttribute("sbBHOtherVariable", sbBHOtherVariable);
			
			Map<String, List<String>> hmProBillingHeadData = new LinkedHashMap<String, List<String>>();
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select * from porject_billing_heads_details where pro_id=? order by pro_billing_head_id");
				pst.setInt(1, uF.parseToInt(getPro_id()));
	//			System.out.println("pst======main==="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					List<FillBillingHeads> billingHeadOtherVariableList1 = new FillBillingHeads().fillBillingHeadOtherVariableListBillingTypewise(rs.getString("head_data_type"));
					String headDataType = getBillingHeadDataTypeOptions(rs.getString("head_data_type"), billingHeadDataTypeList);
					String headOtherVariable = getBillingHeadDataTypeOptions(rs.getString("head_other_variable"), billingHeadOtherVariableList1);
					alInner.add(rs.getString("pro_billing_head_id"));
					alInner.add(rs.getString("head_label"));
					alInner.add(headDataType);
					alInner.add(headOtherVariable);
					alInner.add(rs.getString("head_data_type"));
					alInner.add(rs.getString("billing_head_id"));
					
					hmProBillingHeadData.put(rs.getString("pro_billing_head_id"), alInner);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmProBillingHeadData", hmProBillingHeadData);
			
			Map<String, List<String>> hmBillingHeadData = new LinkedHashMap<String, List<String>>();
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select * from billing_head_setting where org_id=? order by head_label");
				pst.setInt(1, uF.parseToInt(getOrganisation()));
	//			System.out.println("pst======main==="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					List<FillBillingHeads> billingHeadOtherVariableList1 = new FillBillingHeads().fillBillingHeadOtherVariableListBillingTypewise(rs.getString("head_data_type"));
					String headDataType = getBillingHeadDataTypeOptions(rs.getString("head_data_type"), billingHeadDataTypeList);
					String headOtherVariable = getBillingHeadDataTypeOptions(rs.getString("head_other_variable"), billingHeadOtherVariableList1);
					alInner.add(rs.getString("billing_head_id"));
					alInner.add(rs.getString("head_label"));
					alInner.add(headDataType);
					alInner.add(headOtherVariable);
					alInner.add(rs.getString("head_data_type"));
	
					hmBillingHeadData.put(rs.getString("billing_head_id"), alInner);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmBillingHeadData", hmBillingHeadData);
			
			Map<String, List<String>> hmProTaxHeadData = new LinkedHashMap<String, List<String>>();
			StringBuilder taxIds = null;
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select * from project_tax_setting where pro_id=? order by pro_tax_setting_id");
				pst.setInt(1, uF.parseToInt(getPro_id()));
	//			System.out.println("pst======main==="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					List<FillBillingHeads> taxDeductionTypeList = new FillBillingHeads().fillTAxDeductionTypeList();
					String headDeductionType = getTaxHeadDeductionTypeOptions(rs.getString("invoice_or_customer"), taxDeductionTypeList);
					String headStatus = getTaxHeadStatusOptions(uF, rs.getString("status"));
					alInner.add(rs.getString("pro_tax_setting_id"));
					alInner.add(rs.getString("tax_name"));
					alInner.add(rs.getString("tax_percent"));
					alInner.add(headDeductionType);
					alInner.add(headStatus);
					alInner.add(rs.getString("tax_setting_id"));
					String taxNameLbl = getTaxNameLbl(con, uF, rs.getString("tax_setting_id"));
					alInner.add(uF.showData(taxNameLbl, ""));
					hmProTaxHeadData.put(rs.getString("tax_setting_id"), alInner);
					if(taxIds == null) {
						taxIds = new StringBuilder();
						taxIds.append(rs.getString("tax_setting_id"));
					} else {
						taxIds.append(","+rs.getString("tax_setting_id"));
					}
				}
				if(taxIds == null) {
					taxIds = new StringBuilder();
				}
				rs.close();
				pst.close();
			}
			
			
			Map<String, List<String>> hmTaxHeadData = new LinkedHashMap<String, List<String>>();
			if(uF.parseToInt(getPro_id()) > 0) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from tax_setting where org_id=? ");
				if(taxIds != null && !taxIds.toString().equals("")) {
					sbQuery.append(" and tax_setting_id not in ("+taxIds.toString()+") ");
				}
				sbQuery.append(" order by tax_setting_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getOrganisation()));
	//			System.out.println("pst======main==="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					List<FillBillingHeads> taxDeductionTypeList = new FillBillingHeads().fillTAxDeductionTypeList();
					String headDeductionType = getTaxHeadDeductionTypeOptions(rs.getString("invoice_or_customer"), taxDeductionTypeList);
					String headStatus = getTaxHeadStatusOptions(uF, "1");
					alInner.add(rs.getString("tax_setting_id"));
					alInner.add(rs.getString("tax_name"));
					alInner.add(rs.getString("tax_percent"));
					alInner.add(headDeductionType);
					alInner.add(headStatus);
					alInner.add(rs.getString("tax_name_label"));
					hmTaxHeadData.put(rs.getString("tax_setting_id"), alInner);
					
					List<String> alProInner = new ArrayList<String>();
					alProInner.add("");
					alProInner.add(rs.getString("tax_name"));
					alProInner.add(rs.getString("tax_percent"));
					alProInner.add(headDeductionType);
					alProInner.add(headStatus);
					alProInner.add(rs.getString("tax_setting_id"));
					alProInner.add(rs.getString("tax_name_label"));
					hmProTaxHeadData.put(rs.getString("tax_setting_id"), alProInner);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmTaxHeadData", hmTaxHeadData);
			request.setAttribute("hmProTaxHeadData", hmProTaxHeadData);
			
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select additional_info_text from additional_info_of_pro_invoice where pro_id=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				while (rs.next()) {
					setInvoiceAdditionalInfo(rs.getString("additional_info_text"));
				}
				rs.close();
				pst.close();
			}
			
			if(getInvoiceAdditionalInfo() == null || getInvoiceAdditionalInfo().equals("")) {
				pst = con.prepareStatement("select additional_info_text from additional_info_of_invoice where org_id=?");
				pst.setInt(1, uF.parseToInt(getOrganisation()));
				rs = pst.executeQuery();
				while (rs.next()) {
					setInvoiceAdditionalInfo(rs.getString("additional_info_text"));
				}
				rs.close();
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


	private String getTaxNameLbl(Connection con, UtilityFunctions uF, String taxId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String taxNameLbl = null;
		try {
			pst = con.prepareStatement("select tax_name_label from tax_setting where tax_setting_id=?");
			pst.setInt(1, uF.parseToInt(taxId));
			rs = pst.executeQuery();
			while(rs.next()) {
				taxNameLbl = rs.getString("tax_name_label");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taxNameLbl;
	}



	private String getTaxHeadStatusOptions(UtilityFunctions uF, String status) {
		StringBuilder sbHeadOptions = new StringBuilder();
			sbHeadOptions.append("<option value='1' ");
			if(uF.parseToBoolean(status)) {
				sbHeadOptions.append("selected");
			}
			sbHeadOptions.append(">Enable</option>");
			sbHeadOptions.append("<option value='0' ");
			if(!uF.parseToBoolean(status)) {
				sbHeadOptions.append("selected");
			}
			sbHeadOptions.append(">Disable</option>");
//		System.out.println("sbHeadOptions ====>>> " + sbHeadOptions.toString());
		return sbHeadOptions.toString();
	}



	private String getTaxHeadDeductionTypeOptions(String dataTypeId, List<FillBillingHeads> taxDeductionTypeList) {
		StringBuilder sbHeadOptions = new StringBuilder();
		
		for(int i=0; taxDeductionTypeList != null && i<taxDeductionTypeList.size(); i++) {
				if(taxDeductionTypeList.get(i).getHeadId().equals(dataTypeId)) {
					sbHeadOptions.append("<option value='"+taxDeductionTypeList.get(i).getHeadId()+"' selected>"+taxDeductionTypeList.get(i).getHeadName()+"</option>");
				} else {
					sbHeadOptions.append("<option value='"+taxDeductionTypeList.get(i).getHeadId()+"'>"+taxDeductionTypeList.get(i).getHeadName()+"</option>");
				}
		}
//		System.out.println("sbHeadOptions ====>>> " + sbHeadOptions.toString());
		return sbHeadOptions.toString();
	}



	private String getBillingHeadDataTypeOptions(String dataTypeId, List<FillBillingHeads> dataTypeList) {
		StringBuilder sbHeadOptions = new StringBuilder();
		
		for(int i=0; dataTypeList != null && i<dataTypeList.size(); i++) {
				if(dataTypeList.get(i).getHeadId().equals(dataTypeId)) {
					sbHeadOptions.append("<option value='"+dataTypeList.get(i).getHeadId()+"' selected>"+dataTypeList.get(i).getHeadName()+"</option>");
				} else {
					sbHeadOptions.append("<option value='"+dataTypeList.get(i).getHeadId()+"'>"+dataTypeList.get(i).getHeadName()+"</option>");
				}
		}
//		System.out.println("sbHeadOptions ====>>> " + sbHeadOptions.toString());
		return sbHeadOptions.toString();
	}
	

	private void getProjectMilestones(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			
			List<List<String>> proMilestoneList = new ArrayList<List<String>>();
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select * from project_milestone_details where pro_id=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				while(rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("project_milestone_id"));
					innerList.add(rs.getString("pro_milestone_name"));
					innerList.add(rs.getString("pro_milestone_description"));
					innerList.add(rs.getString("pro_completion_percent"));
					innerList.add(getEmpTaskList(uF, rs.getInt("pro_task_id")));
					innerList.add(rs.getString("pro_milestone_amount"));
					proMilestoneList.add(innerList);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("proMilestoneList", proMilestoneList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void updateProjectBillingInfoStep5(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update projectmntnc set milestone_dependent_on=?, invoice_template_type=?, acc_ref=?, po_no=?, terms=?, bill_due_date=?");
			if(uF.parseToBoolean(getChkBank())) {
				sbQuery.append(" ,bank_id="+uF.parseToInt(getBankName())+" ");
			} else {
				sbQuery.append(" ,bank_id = null ");
			}
			if(uF.parseToBoolean(getChkPaypal())) {
				sbQuery.append(" ,paypal_mail_id= '"+getStrPaypal()+"' ");
			} else {
				sbQuery.append(" ,paypal_mail_id = null ");
			}
			sbQuery.append(" where pro_id=?");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("update projectmntnc set milestone_dependent_on=?,invoice_template_type=? where pro_id=?");
			pst.setInt(1, uF.parseToInt(getMilestoneDependentOn()));
			pst.setInt(2, uF.parseToInt(getStrInvoiceTemplate()));
			pst.setString(3, getStrAccountRef());
			pst.setString(4, getStrPONo());
			pst.setString(5, getStrTerms());
			pst.setDate(6, uF.getDateFormat(getStrDueDate(), DATE_FORMAT));
			pst.setInt(7, uF.parseToInt(getPro_id()));
//			System.out.println("pst==>"+pst);
			pst.execute();
			pst.close();
			
			
			/*pst = con.prepareStatement("update project_bill_ref_heads set acc_ref=?, po_no=?, terms=?, due_date=?, updated_by=?, update_date=? " +
				" where pro_id=?");
			pst.setString(1, getStrAccountRef());
			pst.setString(2, getStrPONo());
			pst.setString(3, getStrTerms());
			pst.setDate(4, uF.getDateFormat(getStrDueDate(), DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt(getPro_id()));
//			System.out.println("pst==>"+pst);
			int a = pst.executeUpdate();
			pst.close();
			
			if(a == 0) {
				pst = con.prepareStatement("insert into project_bill_ref_heads (pro_id, acc_ref, po_no, terms, due_date, added_by, entry_date) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				pst.setString(2, getStrAccountRef());
				pst.setString(3, getStrPONo());
				pst.setString(4, getStrTerms());
				pst.setDate(5, uF.getDateFormat(getStrDueDate(), DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
	//			System.out.println("pst==>"+pst);
				pst.executeUpdate();
				pst.close();
				
			}*/
			
			String clientId = "";
			pst = con.prepareStatement("select client_id from projectmntnc where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while(rs.next()) {
				clientId = rs.getString("client_id");
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbMilestoneId = null;
//			System.out.println("getMilestoneId() ===>> " + (getMilestoneId() != null ? getMilestoneId().length : "0"));
			
			for (int i = 0; getMilestoneId() != null && i < getMilestoneId().length; i++) {
				pst = con.prepareStatement("update project_milestone_details set pro_milestone_name=?,pro_milestone_description=?,pro_completion_percent=?," +
						"pro_task_id=?,pro_milestone_amount=?,updated_by=?,update_date=? where project_milestone_id=?");
				pst.setString(1, getMilestoneName()[i]);
				pst.setString(2, getMilestoneDescription()[i]);
				pst.setDouble(3, uF.parseToDouble(getMilestonePercent()[i]));
				pst.setInt(4, uF.parseToInt(getProjectTask()[i]));
				pst.setDouble(5, uF.parseToDouble(getMilestoneAmount()[i]));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(8, uF.parseToInt(getMilestoneId()[i]));
//					System.out.println("pst==>"+pst);
				pst.execute();
				pst.close();
				
				Map<String, String> hmProData = CF.getProjectDetailsByProId(con, getPro_id());
				pst = con.prepareStatement("update projectmntnc_frequency set pro_freq_name=?, pro_start_date=?, pro_end_date=?, freq_start_date=?, " +
					"freq_end_date=? where pro_milestone_id=? and pro_id=?"); 
				pst.setString(1, getMilestoneName()[i]);
				pst.setDate(2, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(hmProData.get("PRO_END_DATE"), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(hmProData.get("PRO_END_DATE"), DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(getMilestoneId()[i]));
				pst.setInt(7, uF.parseToInt(getPro_id()));
//				System.out.println("pst ====> " + pst);
				int cnt = pst.executeUpdate();
				pst.close();
//				System.out.println("projectmntnc_frequency cnt ===>> " + cnt);
				
				if(cnt == 0) {
					pst = con.prepareStatement("insert into projectmntnc_frequency (pro_id, pro_start_date, pro_end_date, freq_start_date, " +
						"freq_end_date, added_by, entry_date, pro_milestone_id, pro_freq_name) values (?,?,?,?, ?,?,?,?, ?)"); 
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setDate(2, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(hmProData.get("PRO_END_DATE"), DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(hmProData.get("PRO_END_DATE"), DATE_FORMAT));
					pst.setInt(6, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(8, uF.parseToInt(getMilestoneId()[i]));
					pst.setString(9, getMilestoneName()[i]);
	//				System.out.println("pst ====> " + pst);
					pst.execute();
					pst.close();
				}
				
				if(sbMilestoneId == null) {
					sbMilestoneId = new StringBuilder();
					sbMilestoneId.append(getMilestoneId()[i]);
				} else {
					sbMilestoneId.append(","+getMilestoneId()[i]);
				}
			}
			if(sbMilestoneId == null) {
				sbMilestoneId = new StringBuilder();
			}
//			System.out.println("sbMilestoneId ===>> " + sbMilestoneId.toString());
			
			if(sbMilestoneId.length() > 0) {
				pst = con.prepareStatement("delete from projectmntnc_frequency where pro_id = ? and pro_milestone_id not in ("+sbMilestoneId.toString()+")");
				pst.setInt(1, uF.parseToInt(getPro_id()));
			//		System.out.println("pst==>"+pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from project_milestone_details where pro_id = ? and project_milestone_id not in ("+sbMilestoneId.toString()+")");
				pst.setInt(1, uF.parseToInt(getPro_id()));
			//		System.out.println("pst==>"+pst);
				pst.execute();
				pst.close();
			}
			
			int milestoneId = 0;
			if(getMilestoneId() != null) {
				milestoneId = getMilestoneId().length;
			}
//			System.out.println("milestoneId ===>> " + milestoneId);
//			System.out.println("getMilestoneName ===>> " + getMilestoneName());
			
			for (int i = milestoneId; getMilestoneName() != null && i < getMilestoneName().length; i++) {
				if(getMilestoneName()[i] != null && !getMilestoneName()[i].equals("")) {
					pst = con.prepareStatement("insert into project_milestone_details (pro_milestone_name,pro_milestone_description,pro_completion_percent," +
							"pro_task_id,pro_milestone_amount,pro_id,client_id,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, getMilestoneName()[i]);
					pst.setString(2, getMilestoneDescription()[i]);
					pst.setDouble(3, uF.parseToDouble(getMilestonePercent()[i]));
					pst.setInt(4, uF.parseToInt(getProjectTask()[i]));
					pst.setDouble(5, uF.parseToDouble(getMilestoneAmount()[i]));
					pst.setInt(6, uF.parseToInt(getPro_id()));
					pst.setInt(7, uF.parseToInt(clientId));
					pst.setInt(8, uF.parseToInt(strSessionEmpId));
					pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
//					System.out.println("pst==>"+pst);
					pst.execute();
					pst.close();
					
					String proMilestoneId = "";
					pst = con.prepareStatement("select max(project_milestone_id) as project_milestone_id from project_milestone_details");
					rs = pst.executeQuery();
					while(rs.next()) {
						proMilestoneId = rs.getString("project_milestone_id");
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmProData = CF.getProjectDetailsByProId(con, getPro_id());
					pst = con.prepareStatement("insert into projectmntnc_frequency (pro_id, pro_start_date, pro_end_date, freq_start_date, " +
						"freq_end_date, added_by, entry_date, pro_milestone_id, pro_freq_name) values (?,?,?,?, ?,?,?,?, ?)"); 
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setDate(2, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(hmProData.get("PRO_END_DATE"), DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(hmProData.get("PRO_END_DATE"), DATE_FORMAT));
					pst.setInt(6, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(8, uF.parseToInt(proMilestoneId));
					pst.setString(9, getMilestoneName()[i]);
	//				System.out.println("pst ====> " + pst);
					pst.execute();
					pst.close();
				}
			}
			
//			System.out.println("getBillingHeadId ===>> " + (getBillingHeadId() != null ? getBillingHeadId().length : "0"));
			
			for (int i = 0; getBillingHeadId() != null && i < getBillingHeadId().length; i++) {
				String billingHeadOtherVariable = request.getParameter("billingHeadOtherVariable"+getBillingHeadTRId()[i]);
				
				pst = con.prepareStatement("update porject_billing_heads_details set head_label=?,head_data_type=?,head_other_variable=?," +
						"updated_by=?,update_date=? where pro_billing_head_id=?");
				pst.setString(1, getBillingHeadLabel()[i]);
				pst.setInt(2, uF.parseToInt(getBillingHeadDataType()[i]));
				pst.setInt(3, uF.parseToInt(billingHeadOtherVariable));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(getBillingHeadId()[i]));
//					System.out.println("pst==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
			int billingHeadId = 0;
			if(getBillingHeadId() != null) {
				billingHeadId = getBillingHeadId().length;
			}
//			System.out.println("billingHeadId ===>> "+billingHeadId+"  getBillingHeadTRId().length ===>> " + (getBillingHeadTRId() != null ? getBillingHeadTRId().length : "0"));
			for (int i = billingHeadId; getBillingHeadTRId() != null && i < getBillingHeadTRId().length; i++) {
				String billingHeadOtherVariable = request.getParameter("billingHeadOtherVariable"+getBillingHeadTRId()[i]);
				//System.out.println("billingHeadOtherVariable ===>> " + billingHeadOtherVariable);
				 
				if(getBillingHeadLabel()[i] != null && !getBillingHeadLabel()[i].equals("")) {
					pst = con.prepareStatement("insert into porject_billing_heads_details (head_label,head_data_type,head_other_variable,pro_id" +
							",added_by,entry_date,billing_head_id)values(?,?,?,?, ?,?,?)");
					pst.setString(1, getBillingHeadLabel()[i]);
					pst.setInt(2, uF.parseToInt(getBillingHeadDataType()[i]));
					pst.setInt(3, uF.parseToInt(billingHeadOtherVariable));
					pst.setInt(4, uF.parseToInt(getPro_id()));
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(getmBillingHeadId()[i]));
//					System.out.println("pst==>"+pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			
//			System.out.println("getTaxHeadId ===>> " + (getTaxHeadId() != null ? getTaxHeadId().length: "0"));
			for (int i = 0; getTaxHeadId() != null && i < getTaxHeadId().length; i++) {
				
				pst = con.prepareStatement("update project_tax_setting set tax_name=?,tax_percent=?,invoice_or_customer=?,status=?," +
						"updated_by=?,update_date=?,tax_name_label=? where pro_tax_setting_id=?");
				pst.setString(1, getTaxHeadLabel()[i]);
				pst.setDouble(2, uF.parseToDouble(getTaxHeadPercent()[i]));
				pst.setInt(3, uF.parseToInt(getTaxHeadDeductionType()[i]));
				pst.setBoolean(4, uF.parseToBoolean(getTaxHeadStatus()[i]));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(7, getTaxNameLabel()[i]);
				pst.setInt(8, uF.parseToInt(getTaxHeadId()[i]));
				
//				System.out.println("pst==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
			int taxHeadId = 0;
			if(getTaxHeadId() != null) {
				taxHeadId = getTaxHeadId().length;
			}
//			System.out.println("getTaxHeadId().length ===>> " + getTaxHeadId().length);
//			System.out.println("taxHeadId ===>> " + taxHeadId +" getTaxHeadLabel() ===>> " + (getTaxHeadLabel() != null ? getTaxHeadLabel().length : "0"));
			
			for (int i = taxHeadId; getTaxHeadLabel() != null && i < getTaxHeadLabel().length; i++) {
				if(getTaxHeadLabel()[i] != null && !getTaxHeadLabel()[i].equals("")) {
					pst = con.prepareStatement("insert into project_tax_setting (tax_name,tax_percent,invoice_or_customer,status,pro_id" +
						",added_by,entry_date,tax_setting_id,tax_name_label)values(?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, getTaxHeadLabel()[i]);
					pst.setDouble(2, uF.parseToDouble(getTaxHeadPercent()[i]));
					pst.setInt(3, uF.parseToInt(getTaxHeadDeductionType()[i]));
					pst.setBoolean(4, uF.parseToBoolean(getTaxHeadStatus()[i]));
					pst.setInt(5, uF.parseToInt(getPro_id()));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(8, uF.parseToInt(getmTaxHeadId()[i]));
					pst.setString(9, getTaxNameLabel()[i]);
		//			System.out.println("pst==>"+pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			
			pst = con.prepareStatement("update additional_info_of_pro_invoice set additional_info_text=? where pro_id=?");
			pst.setString(1, getInvoiceAdditionalInfo());
			pst.setInt(2, uF.parseToInt(getPro_id()));
		//	System.out.println("pst==>"+pst);
			int cnt = pst.executeUpdate();
			pst.close();
			
			if(cnt == 0) {
				pst = con.prepareStatement("insert into additional_info_of_pro_invoice (additional_info_text,pro_id)values(?,?)");
				pst.setString(1, getInvoiceAdditionalInfo());
				pst.setInt(2, uF.parseToInt(getPro_id()));
				pst.executeUpdate();
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
	
	

	public void insertProjectBillingInfoStep5(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println(" in step 5 ---------->> ");
			con = db.makeConnection(con);
//			pst = con.prepareStatement("update projectmntnc set curr_id=?,billing_curr_id=?,billing_type=?,billing_amount=?,idealtime=?,billing_kind=?," +
//					"actual_calculation_type=?,milestone_dependent_on=? where pro_id=?");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update projectmntnc set milestone_dependent_on=?,invoice_template_type=?, acc_ref=?, po_no=?, terms=?, bill_due_date=?");
			if(uF.parseToBoolean(getChkBank())) {
				sbQuery.append(" ,bank_id="+uF.parseToInt(getBankName())+" ");
			} else {
				sbQuery.append(" ,bank_id = null ");
			}
			if(uF.parseToBoolean(getChkPaypal())) {
				sbQuery.append(" ,paypal_mail_id= '"+getStrPaypal()+"' ");
			} else {
				sbQuery.append(" ,paypal_mail_id = null ");
			}
			sbQuery.append(" where pro_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getMilestoneDependentOn()));
			pst.setInt(2, uF.parseToInt(getStrInvoiceTemplate()));
			pst.setString(3, getStrAccountRef());
			pst.setString(4, getStrPONo());
			pst.setString(5, getStrTerms());
			pst.setDate(6, uF.getDateFormat(getStrDueDate(), DATE_FORMAT));
			pst.setInt(7, uF.parseToInt(getPro_id()));
			pst.execute();
			pst.close();
			
			/*pst = con.prepareStatement("insert into project_bill_ref_heads (pro_id, acc_ref, po_no, terms, due_date, added_by, entry_date) values(?,?,?,?, ?,?,?)");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setString(2, getStrAccountRef());
			pst.setString(3, getStrPONo());
			pst.setString(4, getStrTerms());
			pst.setDate(5, uF.getDateFormat(getStrDueDate(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst==>"+pst);
			pst.executeUpdate();
			pst.close();*/
			
			
			String clientId = "";
			pst = con.prepareStatement("select client_id from projectmntnc where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while(rs.next()) {
				clientId = rs.getString("client_id");
			}
			rs.close();
			pst.close();
			
			for (int i = 0; getMilestoneName() != null && i < getMilestoneName().length; i++) {
				if(getMilestoneName()[i] != null && !getMilestoneName()[i].equals("")) {
					pst = con.prepareStatement("insert into project_milestone_details (pro_milestone_name,pro_milestone_description,pro_completion_percent," +
							"pro_task_id,pro_milestone_amount,pro_id,client_id,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, getMilestoneName()[i]);
					pst.setString(2, getMilestoneDescription()[i]);
					pst.setDouble(3, uF.parseToDouble(getMilestonePercent()[i]));
					pst.setInt(4, uF.parseToInt(getProjectTask()[i]));
					pst.setDouble(5, uF.parseToDouble(getMilestoneAmount()[i]));
					pst.setInt(6, uF.parseToInt(getPro_id()));
					pst.setInt(7, uF.parseToInt(clientId));
					pst.setInt(8, uF.parseToInt(strSessionEmpId));
					pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
//					System.out.println("pst==>"+pst);
					pst.execute();
					pst.close();
					
					String proMilestoneId = ""; 
					pst = con.prepareStatement("select max(project_milestone_id) as project_milestone_id from project_milestone_details");
					rs = pst.executeQuery();
					while(rs.next()) {
						proMilestoneId = rs.getString("project_milestone_id");
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmProData = CF.getProjectDetailsByProId(con, getPro_id());
					pst = con.prepareStatement("insert into projectmntnc_frequency (pro_id, pro_start_date, pro_end_date, freq_start_date, " +
						"freq_end_date, added_by, entry_date, pro_milestone_id, pro_freq_name) values (?,?,?,?, ?,?,?,?, ?)"); 
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setDate(2, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(hmProData.get("PRO_END_DATE"), DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(hmProData.get("PRO_END_DATE"), DATE_FORMAT));
					pst.setInt(6, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(8, uF.parseToInt(proMilestoneId));
					pst.setString(9, getMilestoneName()[i]);
	//				System.out.println("pst ====> " + pst);
					pst.execute();
					pst.close();
				}
			}
			
//			System.out.println("getBillingHeadTRId().length ===>> " + getBillingHeadTRId().length);
			for (int i = 0; getBillingHeadTRId() != null && i < getBillingHeadTRId().length; i++) {
				String billingHeadOtherVariable = request.getParameter("billingHeadOtherVariable"+getBillingHeadTRId()[i]);
//				System.out.println("billingHeadOtherVariable ===>> " + billingHeadOtherVariable);
				
				if(getBillingHeadLabel()[i] != null && !getBillingHeadLabel()[i].equals("")) {
					pst = con.prepareStatement("insert into porject_billing_heads_details (head_label,head_data_type,head_other_variable,pro_id" +
							",added_by,entry_date,billing_head_id)values(?,?,?,?, ?,?,?)");
					pst.setString(1, getBillingHeadLabel()[i]);
					pst.setInt(2, uF.parseToInt(getBillingHeadDataType()[i]));
					pst.setInt(3, uF.parseToInt(billingHeadOtherVariable));
					pst.setInt(4, uF.parseToInt(getPro_id()));
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(getmBillingHeadId()[i]));
//					System.out.println("pst==>"+pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			
			
			for (int i = 0; getTaxHeadLabel() != null && i < getTaxHeadLabel().length; i++) {
				if(getTaxHeadLabel()[i] != null && !getTaxHeadLabel()[i].equals("")) {
					pst = con.prepareStatement("insert into project_tax_setting (tax_name,tax_percent,invoice_or_customer,status,pro_id" +
							",added_by,entry_date,tax_setting_id,tax_name_label)values(?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, getTaxHeadLabel()[i]);
					pst.setDouble(2, uF.parseToDouble(getTaxHeadPercent()[i]));
					pst.setInt(3, uF.parseToInt(getTaxHeadDeductionType()[i]));
					pst.setBoolean(4, uF.parseToBoolean(getTaxHeadStatus()[i]));
					pst.setInt(5, uF.parseToInt(getPro_id()));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(8, uF.parseToInt(getmTaxHeadId()[i]));
					pst.setString(9, getTaxNameLabel()[i]);
//					System.out.println("pst==>"+pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			
			
			pst = con.prepareStatement("insert into additional_info_of_pro_invoice (additional_info_text,pro_id)values(?,?)");
			pst.setString(1, getInvoiceAdditionalInfo());
			pst.setInt(2, uF.parseToInt(getPro_id()));
//				pst.setInt(6, uF.parseToInt(strSessionEmpId));
//				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
		//	System.out.println("pst==>"+pst);
			pst.executeUpdate();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	

	private String getEmpTaskList(UtilityFunctions uF, int taskId) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbOption = new StringBuilder();
		try {
			con = db.makeConnection(con);
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select a.* from (select task_id,activity_name,parent_task_id,pro_id from activity_info where " +
					"task_id not in (select parent_task_id from activity_info where parent_task_id is not null) and task_accept_status = 1) a, " +
					"projectmntnc pmc where pmc.pro_id=a.pro_id and (parent_task_id in (select task_id from activity_info) or parent_task_id = 0) and a.pro_id = ?");
	//			pst = con.prepareStatement("select task_id,activity_name from activity_info where pro_id=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				while(rs.next()) {
					sbOption.append("<option value=");
					sbOption.append(rs.getString("task_id"));
					if(taskId == uF.parseToInt(rs.getString("task_id"))) {
						sbOption.append(" selected");
					}
					sbOption.append(">");
					sbOption.append(rs.getString("activity_name"));
					sbOption.append("</option>");
				}
				rs.close();
				pst.close();
	//			System.out.println("sbOption ===>> " + sbOption.toString());
				if(taskId==0) {
					request.setAttribute("sbOption", sbOption.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbOption.toString();
	}

	
	public void getProjectDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			
			ViewAllProjects allProjects = new ViewAllProjects();
			allProjects.session = session;
			allProjects.request = request;
			allProjects.CF = CF;
			Map<String, String> hmProFreqStartEndDate = allProjects.getProjectCurrentFreqStartEndDate(con, uF, getPro_id());
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("PANP1/1706--pst ===>> " + pst);
			rs = pst.executeQuery();
//			int projectOwnerId = 0;
			StringBuilder projectOwnerId = null;
			int nSbuId = 0;
			int nOrgId = 0;
			int nClientId = 0;
			int nPocId = 0;
			while(rs.next()) {
				Map<String, String> hmCurr = (Map)hmCurrency.get(rs.getString("curr_id"));
				Map<String, String> hmCurrBase = (Map)hmCurrency.get("3"); // 3 = INR
				
				/*double dblExRate1 = 0.0d; 
				double dblExRate2 = 0.0d; 
				
				if(hmCurrBase != null && !hmCurrBase.isEmpty()) {
					dblExRate1 = uF.parseToDouble(hmCurrBase.get("CURR_CONVERSION_USD"));
				}
				if(hmCurr != null && !hmCurr.isEmpty()) {
					dblExRate2 = uF.parseToDouble(hmCurr.get("CURR_CONVERSION_USD")); 
				}
				double currConversion = 0.0d;
				if(dblExRate2 > 0 && dblExRate1 > 0) {
					currConversion = (dblExRate2/dblExRate1);
				}*/
				
				request.setAttribute("PROJECT_NAME", rs.getString("pro_name"));
				request.setAttribute("PROJECT_CODE", rs.getString("project_code"));
				request.setAttribute("PROJECT_DESC", uF.showData(rs.getString("description"), ""));
				request.setAttribute("PROJECT_START_DATE", uF.showData(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				request.setAttribute("PROJECT_END_DATE", uF.showData(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()), ""));
				
				request.setAttribute("PROJECT_START_DATE_C", uF.showData(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT), ""));
				request.setAttribute("PROJECT_END_DATE_C", uF.showData(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT), ""));
				request.setAttribute("PROJECT_START_DATE_MM_DD", uF.showData(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_MM_DD_YYYY), ""));
				request.setAttribute("PROJECT_END_DATE_MM_DD", uF.showData(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_MM_DD_YYYY), ""));

				request.setAttribute("PROJECT_IDEALTIME", rs.getString("idealtime"));
				request.setAttribute("PROJECT_CALC_TYPE", rs.getString("actual_calculation_type"));
				request.setAttribute("SHORT_CURR", hmCurr != null ? hmCurr.get("SHORT_CURR") : "");
//				request.setAttribute("CURR_CONVERSION", currConversion+"");
				
//				projectOwnerId = rs.getInt("project_owner");
				String[] proOwnerIds = null;
				
				if(rs.getString("project_owners")!=null){
					proOwnerIds = rs.getString("project_owners").split(",");
				}
				for(int k=1; proOwnerIds!=null && k<proOwnerIds.length; k++){
					if(projectOwnerId==null){
						projectOwnerId = new StringBuilder();
						projectOwnerId.append(proOwnerIds[k]);
					}else{
						projectOwnerId.append(","+proOwnerIds[k]);
					}
				}
				
				nSbuId = rs.getInt("sbu_id");
				nOrgId = rs.getInt("org_id");
				nClientId = rs.getInt("client_id");
				nPocId = rs.getInt("poc");
				
				String strCondition = "";
				if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("n")){
					request.setAttribute("PRO_STATUS", "Working");
					if(rs.getString("deadline")!=null && rs.getDate("deadline").after(uF.getCurrentDate(CF.getStrTimeZone()))){
						strCondition = "On Target";
					} else {
						strCondition = "Overdue";
					}
				} else if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("approved")){
					request.setAttribute("PRO_STATUS", "Completed");
					if(rs.getString("deadline")!=null && rs.getDate("deadline").after(rs.getDate("approve_date"))){
						strCondition = "On Target";
					} else {
						strCondition = "Overdue";
					}
				} else if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("blocked")){
					request.setAttribute("PRO_STATUS", "Blocked");
					if(rs.getString("deadline")!=null && rs.getDate("deadline").after(rs.getDate("approve_date"))){
						strCondition = "On Target";
					} else {
						strCondition = "Overdue";
					}
				}
				request.setAttribute("strCondition", strCondition);
				
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.showData(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT), ""));
				hmProjectData.put("PRO_END_DATE", uF.showData(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT), ""));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					
				Map<String, String> hmProAWDaysAndHrs = CF.getProjectActualAndBillableEfforts(con, rs.getString("pro_id"), hmProjectData);
				double proDealineCompletePercent = 0;
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					request.setAttribute("IDEAL_TIME", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime"))));
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
					proDealineCompletePercent = uF.parseToDouble(actualTime);
					request.setAttribute("ACTUAL_TIME", actualTime);
					request.setAttribute("CAL_TYPE", "days");
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					request.setAttribute("IDEAL_TIME", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime"))));
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
					double actualMonths = uF.parseToDouble(actualTime) / 30;
					proDealineCompletePercent = actualMonths;
					request.setAttribute("ACTUAL_TIME", uF.formatIntoTwoDecimalWithOutComma(actualMonths));
					request.setAttribute("CAL_TYPE", "months");
				} else {
					request.setAttribute("IDEAL_TIME", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime"))));
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_HRS");
					proDealineCompletePercent = uF.parseToDouble(uF.getTotalTimeMinutes100To60(actualTime));
					request.setAttribute("ACTUAL_TIME", uF.getTotalTimeMinutes100To60(actualTime));
					request.setAttribute("CAL_TYPE", "hrs");
				}
				
				String strProDeadlineColor = "";
				String strProDeadlineIndicator = "";
				if(proDealineCompletePercent < uF.parseToDouble(rs.getString("idealtime"))) {
					strProDeadlineColor = "green";
					/*strProDeadlineIndicator = "<img src=\"images1/icons/approved.png\" width=\"17px\">"; */
					strProDeadlineIndicator = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>"; 
					
				} else if(proDealineCompletePercent == uF.parseToDouble(rs.getString("idealtime"))) {
					strProDeadlineColor = "yellow";
					/*strProDeadlineIndicator = "<img src=\"images1/icons/re_submit.png\" width=\"17px\">";*/
					strProDeadlineIndicator = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>";
					
				} else if(proDealineCompletePercent > uF.parseToDouble(rs.getString("idealtime"))) {
					strProDeadlineColor = "red";
					/*strProDeadlineIndicator = "<img src=\"images1/icons/denied.png\" width=\"17px\">";*/
					strProDeadlineIndicator = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>";
					
				} else {
					strProDeadlineColor = "";
					strProDeadlineIndicator = "";
				}
				request.setAttribute("strProDeadlineColor", strProDeadlineColor);
				request.setAttribute("strProDeadlineIndicator", strProDeadlineIndicator);
				
				String strBillingType = CF.getBillinType(rs.getString("billing_type"));;
				String strBillingKind = CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type"));
				request.setAttribute("strBillingType", strBillingType+" ("+strBillingKind+")");
				
				
				double deadLinePercent = 0;
				String days = null;
				String currdays = null;
				String strProFreqStartDate = null;
				String strProFreqEndDate = null;
				if(hmProFreqStartEndDate != null && !hmProFreqStartEndDate.isEmpty()) {
					days = uF.dateDifference(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, hmProFreqStartEndDate.get("FREQ_END_DATE"), DBDATE);
					if(rs.getString("approve_status") != null && !rs.getString("approve_status").equals("n")) {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("approve_date"), DBDATE);
					} else {
						currdays = uF.dateDifference(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					}
					strProFreqStartDate = uF.getDateFormat(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, CF.getStrReportDateFormat());
					strProFreqEndDate = uF.getDateFormat(hmProFreqStartEndDate.get("FREQ_END_DATE"), DBDATE, CF.getStrReportDateFormat());
				} else {
					days = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("deadline"), DBDATE);
					if(rs.getString("approve_status") != null && !rs.getString("approve_status").equals("n")) {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("approve_date"), DBDATE);
					} else {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					}
					
					strProFreqStartDate = uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat());
					strProFreqEndDate = uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat());
				}
				if(uF.parseToDouble(days) > 0) {
					deadLinePercent = (uF.parseToDouble(currdays) / uF.parseToDouble(days)) * 100;
				}
				String proDeadlinePercentColor = "";
				if(deadLinePercent <= 75) {
					/*proDeadlinePercentColor = "<img src=\"images1/icons/approved.png\" width=\"17px\">";*/
					proDeadlinePercentColor = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>";
					
				} else if(deadLinePercent > 75 && deadLinePercent < 100) {
					/*proDeadlinePercentColor = "<img src=\"images1/icons/re_submit.png\" width=\"17px\">";*/
					proDeadlinePercentColor = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>";
					
				} else if(deadLinePercent >= 100) {
					/*proDeadlinePercentColor = "<img src=\"images1/icons/denied.png\" width=\"17px\">";*/
					proDeadlinePercentColor = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>";
					
				} else {
					proDeadlinePercentColor = "";
				}
				request.setAttribute("proDeadlinePercentColor", proDeadlinePercentColor);
				
				boolean proFreqFlag = false;
				if(rs.getString("billing_type")!=null && (rs.getString("billing_type").trim().equals("H") || rs.getString("billing_type").trim().equals("D") || rs.getString("billing_type").trim().equals("M"))){
					proFreqFlag = true;
				}
				request.setAttribute("strProFreqStartDate", strProFreqStartDate);
				request.setAttribute("strProFreqEndDate", strProFreqEndDate);
				request.setAttribute("proFreqFlag", proFreqFlag);
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("PROJECT_NAME ===>> " + request.getAttribute("PROJECT_NAME"));
			
			pst = con.prepareStatement("select emp_id, _isteamlead, emp_fname,emp_mname, emp_lname from project_emp_details ped, employee_personal_details epd  where epd.emp_per_id = ped.emp_id and pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			StringBuilder sbTeamLeader = new StringBuilder();
			StringBuilder sbTeamMember = new StringBuilder();
			while(rs.next()) {
				if(uF.parseToBoolean(rs.getString("_isteamlead"))) {
				
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					sbTeamLeader.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+"[TL], ");
				} else {
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					sbTeamMember.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+", ");
				}
			}
			rs.close();
			pst.close();
			
			if(sbTeamLeader.length()>1) {
				sbTeamLeader.replace(0, sbTeamLeader.length(), sbTeamLeader.substring(0, sbTeamLeader.length()-2));
				request.setAttribute("TEAM_LEADER", sbTeamLeader.toString());
			}
			if(sbTeamMember.length()>1) {
				sbTeamMember.replace(0, sbTeamMember.length(), sbTeamMember.substring(0, sbTeamMember.length()-2));
				request.setAttribute("TEAM_MEMBER", sbTeamMember.toString());
			}
			
//			pst = con.prepareStatement("select * from employee_personal_details epd  where emp_per_id = ?");
//			pst.setInt(1, projectOwnerId);
			pst = con.prepareStatement("select * from employee_personal_details epd  where emp_per_id in ("+projectOwnerId+") ");
			rs = pst.executeQuery();
			Map<String, String> hmProOwner = new HashMap<String, String>();
			while(rs.next()) {
				hmProOwner.put("EMP_ID", rs.getString("emp_per_id"));	
//				String strMiddleName = ""; 
//				if(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("") && !rs.getString("emp_mname").trim().equalsIgnoreCase("NULL")){
//					strMiddleName = rs.getString("emp_mname")+" ";
//				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmProOwner.put("EMP_NAME", uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), "")+"[PO]");
				hmProOwner.put("EMP_IMAGE", uF.showData(rs.getString("emp_image"), ""));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProOwner", hmProOwner);
//			System.out.println("PANP1/1961---hmProOwner="+hmProOwner);
			
			request.setAttribute("proService", CF.getServiceNameById(con, ""+nSbuId));
			request.setAttribute("proOrg", CF.getOrgNameById(con, ""+nOrgId));
			
			pst = con.prepareStatement("select * from client_industry_details");
			rs = pst.executeQuery();
			Map<String, String> hmIndustry = new HashMap<String, String>();
			while(rs.next()) {
				hmIndustry.put(rs.getString("industry_id"), rs.getString("industry_name"));	
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from client_details where client_id = ?");
			pst.setInt(1, nClientId);
			rs = pst.executeQuery();
			Map<String, String> hmCustomer = new HashMap<String, String>();
			while(rs.next()) {
				hmCustomer.put("CLIENT_ID", rs.getString("client_id"));	
				hmCustomer.put("CLIENT_NAME", rs.getString("client_name"));	
				hmCustomer.put("CLIENT_LOGO", rs.getString("client_logo"));	
				
				String strIndustry = "";
				if(rs.getString("client_industry") !=null && !rs.getString("client_industry").trim().equals("")){
					if(rs.getString("client_industry").contains(",")){
						String[] strTemp =  rs.getString("client_industry").split(",");
						int x = 0;
						for(int i = 0; i < strTemp.length; i++){
							if(!strTemp[i].trim().equals("")){
								if(x == 0){
									strIndustry = uF.showData(hmIndustry.get(strTemp[i].trim()), "");
								} else {
									strIndustry += ","+ uF.showData(hmIndustry.get(strTemp[i].trim()), "");
								}
								x++;
							}
						}
						
					} else {
						strIndustry = uF.showData(hmIndustry.get(rs.getString("client_industry").trim()), "");
					}
				}
				
				hmCustomer.put("CLIENT_INDUSTRY", strIndustry);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCustomer", hmCustomer);
			
			request.setAttribute("clientPoc", CF.getClientSPOCNameById(con, ""+nPocId));
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ai.*, pmc.start_date as pmc_start_date, pmc.deadline as pmc_deadline, pmc.bill_days_type, pmc.hours_for_bill_day, " +
				"pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
				"ai.parent_task_id = 0 and task_accept_status != -1 and pmc.pro_id =? ");
			pst = con.prepareStatement(sbQuery.toString()); 
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			Map<String, String> hmProIds = new HashMap<String, String>();
			Map<String, String> hmProTaskData = new HashMap<String, String>();
			while (rs.next()) {
				double taskIdealTime = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_IDEAL_TIME"));
				taskIdealTime = taskIdealTime + uF.parseToDouble(rs.getString("idealtime"));
				double taskWoredTime = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_WORKED_TIME"));
				double actWorkTime = (uF.parseToDouble(rs.getString("idealtime")) * uF.parseToDouble(rs.getString("completed"))) / 100;
				taskWoredTime = taskWoredTime + actWorkTime;
				
				hmProTaskData.put(rs.getString("pro_id")+"_IDEAL_TIME", taskIdealTime+"");
				hmProTaskData.put(rs.getString("pro_id")+"_WORKED_TIME", taskWoredTime+"");
				
				hmProIds.put(rs.getString("pro_id"), rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmProIds.keySet().iterator();
			
			Map<String, String> hmProCompPercent = new HashMap<String, String>();
			Map<String, Map<String, String>> hmProMileAndCMileCnt = new HashMap<String, Map<String, String>>();
			while (it.hasNext()) {
				Map<String, String> hmMilestoneAndCompletedMilestone = new HashMap<String, String>();
				String strProId = it.next();
				double dblProCompletePercent = 0.0d;
				if(uF.parseToDouble(hmProTaskData.get(strProId+"_IDEAL_TIME")) > 0) {
					dblProCompletePercent = (uF.parseToDouble(hmProTaskData.get(strProId+"_WORKED_TIME")) * 100) / uF.parseToDouble(hmProTaskData.get(strProId+"_IDEAL_TIME"));
				}
				hmProCompPercent.put(strProId, dblProCompletePercent+"");
				
				int milestoneCount = 0;
				int completedMilestoneCount = 0;
				pst = con.prepareStatement("select pmd.*, p.milestone_dependent_on from project_milestone_details pmd, projectmntnc p where pmd.pro_id= p.pro_id and pmd.pro_id=?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 2) {
						int intcomplete = getProTaskCompleted(con, uF, rs.getString("pro_task_id"));
						completedMilestoneCount += intcomplete;
					} else if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 1) {
						if(rs.getDouble("pro_completion_percent") <= uF.parseToDouble(hmProCompPercent.get(strProId))) {
							completedMilestoneCount++;
						}
					}
					
					milestoneCount++;
				}
				rs.close();
				pst.close();
				
				hmMilestoneAndCompletedMilestone.put("MILESTONE_COUNT", milestoneCount+"");
				hmMilestoneAndCompletedMilestone.put("COMPLETED_MILESTONE_COUNT", completedMilestoneCount+"");
				hmProMileAndCMileCnt.put(strProId, hmMilestoneAndCompletedMilestone);
			}
			
			request.setAttribute("hmProCompPercent", hmProCompPercent);
			request.setAttribute("hmProMileAndCMileCnt", hmProMileAndCMileCnt);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private int getProTaskCompleted(Connection con, UtilityFunctions uF, String taskId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int intTaskCompleted = 0;
		try {
			
			pst = con.prepareStatement("select task_id from activity_info where task_id =? and approve_status = 'approved' and task_accept_status != -1");
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while (rs.next()) {
				intTaskCompleted = 1;
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return intTaskCompleted;
	}
	
	public void getActivity(UtilityFunctions uF) {
		

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);

			Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, getPro_id());
			
			request.setAttribute("BILL_FREQUENCY", hmProjectData.get("PRO_BILL_FREQUENCY"));
			
			Map<String, List<String>> hmProTasks = new LinkedHashMap<String, List<String>>();
			int taskAndSubtaskCount = 0;
			Map<String, List<List<String>>> hmProSubTasks = new LinkedHashMap<String, List<List<String>>>();
			
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select ai.*, pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc " +
					" where ai.pro_id=pmc.pro_id and ai.pro_id=? and ai.parent_task_id = 0 and (ai.task_accept_status >=0 or ai.task_accept_status = -2)" +
					" order by ai.start_date, ai.task_id");
				pst.setInt(1, uF.parseToInt(getPro_id()));
//				System.out.println("pst======main===" + pst);
				rs = pst.executeQuery();
	//			List<List<String>> proTaskList = new ArrayList<List<String>>();
				while (rs.next()) {
	//				proTaskList = hmProTasks.get(rs.getString("task_id"));
	//				if(proTaskList == null) proTaskList = new ArrayList<List<String>>();
					String taskActivityCnt = CF.getTaskActivityTaskCount(con, uF, rs.getString("task_id"));
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("task_id")); //0
					alInner.add(rs.getString("parent_task_id"));
					alInner.add(rs.getString("pro_id")); //2
					alInner.add(uF.showData(rs.getString("activity_name"), "")); //3
					String dependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), dependencyList);
					alInner.add(uF.showData(dependencyTask, "")); //4
					alInner.add(uF.showData(rs.getString("dependency_type"), "")); //5
					alInner.add(uF.showData(rs.getString("priority"), "")); //6
					alInner.add(uF.showData(rs.getString("task_skill_id"), "")); //7
					String taskEmps = getTaskEmployee(rs.getString("resource_ids"), TaskEmpNamesList); 
					alInner.add(uF.showData(taskEmps, "")); //8
					alInner.add(uF.showData(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT), "")); //9
					alInner.add(uF.showData(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT), "")); //10
					alInner.add(uF.showData(rs.getString("idealtime"), "")); //11
					alInner.add(uF.showData(rs.getString("color_code"), "")); //12
	//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
	////					alInner.add(uF.showData(rs.getString("already_work_days"), "")); //13
	//				} else {
	//					alInner.add(uF.showData(rs.getString("already_work"), "")); //13
	//				}
					alInner.add(uF.showData(taskActivityCnt, "")); //13
					String timeFilledEmp = getTimesheetFilledEmp(con, rs.getString("resource_ids"), rs.getString("task_id"));
//					System.out.println(rs.getString("task_id") + " -- timeFilledEmp ===>> " + timeFilledEmp);
					
					alInner.add(uF.showData(timeFilledEmp, "")); //14
					String subTaskCnt = getSubTaskCountOfTask(con, uF, rs.getString("task_id"));
					alInner.add(uF.showData(subTaskCnt, "")); //15
					alInner.add(uF.showData(rs.getString("task_description"), "")); //16
					alInner.add(uF.showData(rs.getString("recurring_task"), "")); //17
					alInner.add(uF.showData(rs.getString("is_billable_task"), "")); //18
	//				proTaskList.add(alInner);
					hmProTasks.put(rs.getString("task_id"), alInner);
					taskAndSubtaskCount++;
				}
				rs.close();
				pst.close();
			
			
				pst = con.prepareStatement("select ai.*, pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc " +
						" where ai.pro_id=pmc.pro_id and ai.pro_id=? and ai.parent_task_id != 0 and (ai.task_accept_status >=0 or ai.task_accept_status = -2)" +
						" order by ai.start_date, ai.task_id");
				pst.setInt(1, uF.parseToInt(getPro_id()));
//				System.out.println("pst======main==="+pst);
				rs = pst.executeQuery();
				List<List<String>> proSubTaskList = new ArrayList<List<String>>();
				while (rs.next()) {
					proSubTaskList = hmProSubTasks.get(rs.getString("parent_task_id"));
					if(proSubTaskList == null) proSubTaskList = new ArrayList<List<String>>();
					
					List<String> alInner = new ArrayList<String>();
					List<FillDependentTaskList> subDependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(getPro_id()), uF.parseToInt(rs.getString("parent_task_id")));
					String subDependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), subDependencyList);
					
					String taskActivityCnt = CF.getTaskActivityTaskCount(con, uF, rs.getString("task_id"));
					alInner.add(rs.getString("task_id")); //0
					alInner.add(rs.getString("parent_task_id")); //1
					alInner.add(rs.getString("pro_id")); //2
					alInner.add(uF.showData(rs.getString("activity_name"), "")); //3
					alInner.add(uF.showData(subDependencyTask, "")); //4
					alInner.add(uF.showData(rs.getString("dependency_type"), "")); //5
					alInner.add(uF.showData(rs.getString("priority"), "")); //6
					alInner.add(uF.showData(rs.getString("task_skill_id"), "")); //7
					String taskEmps = getTaskEmployee(rs.getString("resource_ids"), TaskEmpNamesList);
					alInner.add(uF.showData(taskEmps, ""));//8
	//				alInner.add(rs.getString("emp_id"));
					alInner.add(uF.showData(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT), ""));//9
					alInner.add(uF.showData(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT), "")); //10
					alInner.add(uF.showData(rs.getString("idealtime"), "")); //11
					alInner.add(uF.showData(rs.getString("color_code"), "")); //12
	//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
	//					alInner.add(uF.showData(rs.getString("already_work_days"), "")); //13
	//				} else {
	//					alInner.add(uF.showData(rs.getString("already_work"), "")); //13
	//				}
					alInner.add(uF.showData(taskActivityCnt, "")); //13
					String timeFilledEmp = getTimesheetFilledEmp(con, rs.getString("resource_ids"), rs.getString("task_id"));
					alInner.add(uF.showData(timeFilledEmp, "")); //14
					alInner.add(uF.showData(rs.getString("task_description"), "")); //15
					alInner.add(uF.showData(rs.getString("recurring_task"), "")); //16
					alInner.add(uF.showData(rs.getString("is_billable_task"), "")); //17
					
					proSubTaskList.add(alInner);
					
					hmProSubTasks.put(rs.getString("parent_task_id"), proSubTaskList);
					taskAndSubtaskCount++;
				}
				rs.close();
				pst.close();
			}	
			request.setAttribute("hmProTasks", hmProTasks);
//			System.out.println("hmProTasks ===>> " + hmProTasks);
				
			request.setAttribute("hmProSubTasks", hmProSubTasks);
			request.setAttribute("taskAndSubtaskCount", taskAndSubtaskCount);
//			System.out.println("hmProSubTasks ===>> " + hmProSubTasks);
			
			pst = con.prepareStatement("select * from project_documents_details where pro_id=? ");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			Map<String, List<String>> hmProjectDocuments = new HashMap<String, List<String>>();
			List<String> alDocuments = new ArrayList<String>();
			rs = pst.executeQuery();
			int nTaskIdOld = 0;
			int nTaskIdNew = 0;
			int nDocCount = 0;
			while(rs.next()) {
				nTaskIdNew = rs.getInt("task_id");
				if(nTaskIdNew!=0 && nTaskIdNew!=nTaskIdOld) {
					alDocuments = new ArrayList<String>();
					nDocCount = 0;
				}
				alDocuments.add("<a target=\"_blank\" title=\""+rs.getString("doc_name")+"\" href=\""+CF.getStrDocRetriveLocation()+rs.getString("doc_path")+"\">Document "+ ++nDocCount+"</a>");
				hmProjectDocuments.put(nTaskIdNew+"", alDocuments);
				nTaskIdOld = nTaskIdNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProjectDocuments", hmProjectDocuments);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
private String getTimesheetFilledEmp(Connection con, String resourceIds, String taskId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder timeFilledEmp = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			if(resourceIds != null && resourceIds.length() > 1) {
				resourceIds = resourceIds.substring(1, resourceIds.length()-1);
				if(!resourceIds.trim().equals("") && resourceIds.length() > 1) {
					pst = con.prepareStatement("select * from task_activity ta, employee_personal_details epd where ta.emp_id=epd.emp_per_id and epd.is_alive=true and emp_id in("+resourceIds+") and activity_id=?");
					pst.setInt(1, uF.parseToInt(taskId));
//					System.out.println("pst======main===" + pst);
					rs = pst.executeQuery();
					List<String> alEmpIds = new ArrayList<String>();
					while (rs.next()) {
						if(!alEmpIds.contains(rs.getString("emp_id"))) {
							if(timeFilledEmp == null) {
								timeFilledEmp = new StringBuilder();
								timeFilledEmp.append(rs.getString("emp_id"));
							} else {
								timeFilledEmp.append(","+rs.getString("emp_id"));
							}
							alEmpIds.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					if(timeFilledEmp == null) {
						timeFilledEmp = new StringBuilder();
					}
	//				System.out.println("timeFilledEmp ===>> " + timeFilledEmp.toString());
				}
			}
			if(timeFilledEmp == null) {
				timeFilledEmp = new StringBuilder();
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
		return timeFilledEmp.toString();
		
	}
	

	private String getSubTaskCountOfTask(Connection con, UtilityFunctions uF, String taskId) {
		String subTaskCnt = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select count(task_id) as stCnt from activity_info where parent_task_id = ? and task_accept_status != -1");
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while(rs.next()) {
				subTaskCnt = rs.getString("stCnt");
			}
			rs.close();
			pst.close();
//			System.out.println("PROJECT_NAME ===>> " + request.getAttribute("PROJECT_NAME"));
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
		return subTaskCnt;
	}



	private String getTaskEmployee(String resourceIds, List<FillTaskEmpList> taskEmpNamesList) {
		StringBuilder sbTaskEmps = new StringBuilder();
		
		List<String> alResources = new ArrayList<String>();
		if(resourceIds != null) {
			alResources = Arrays.asList(resourceIds.split(","));
		}
		for(int i=0; taskEmpNamesList != null && i<taskEmpNamesList.size(); i++) {
			if(alResources.contains(taskEmpNamesList.get(i).getTaskEmployeeId())) {
				sbTaskEmps.append("<option value='"+taskEmpNamesList.get(i).getTaskEmployeeId()+"' selected>"+taskEmpNamesList.get(i).getTaskEmployeeName()+"</option>");
			} else {
				sbTaskEmps.append("<option value='"+taskEmpNamesList.get(i).getTaskEmployeeId()+"'>"+taskEmpNamesList.get(i).getTaskEmployeeName()+"</option>");
			}
		}
//		System.out.println("sbTaskEmps ====>>> " + sbTaskEmps.toString());
		return sbTaskEmps.toString();
	}



	private String getDependencyTaskOptions(String taskId, String dependencyId, List<FillDependentTaskList> dependencyList) {
		StringBuilder sbTaskOptions = new StringBuilder();
		
		for(int i=0; dependencyList != null && i<dependencyList.size(); i++) {
			if(!dependencyList.get(i).getDependencyId().equals(taskId)) {
				if(dependencyList.get(i).getDependencyId().equals(dependencyId)) {
					sbTaskOptions.append("<option value='"+dependencyList.get(i).getDependencyId()+"' selected>"+dependencyList.get(i).getDependencyName()+"</option>");
				} else {
					sbTaskOptions.append("<option value='"+dependencyList.get(i).getDependencyId()+"'>"+dependencyList.get(i).getDependencyName()+"</option>");
				}
			}
		}
//		System.out.println("sbTaskOptions ====>>> " + sbTaskOptions.toString());
		return sbTaskOptions.toString();
	}



	public void getSkills(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			String proOrgId = null;
			while (rs.next()) {
				proOrgId = rs.getString("org_id");
			}
			rs.close();
			pst.close();
			
			setOrganisation(proOrgId);
			
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
				workLocationList = new FillWLocation(request).fillWLocation(getOrganisation(), (String)session.getAttribute(WLOCATION_ACCESS));
			} else {
				workLocationList = new FillWLocation(request).fillWLocation(getOrganisation());
			}
			StringBuilder sbWLocs = new StringBuilder(); //<option value=\"\">Select Employee
			for(int i=0; workLocationList!=null && i<workLocationList.size(); i++) {
				sbWLocs.append("<option value=\"" + workLocationList.get(i).getwLocationId() + "\">" + workLocationList.get(i).getwLocationName() + "</option>");
			}
			//System.out.println("sbSkills ===>> " + sbSkills.toString());
			request.setAttribute("sbWLocs", sbWLocs.toString());
			
			skillList = new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(getOrganisation()));
			StringBuilder sbSkills = new StringBuilder(); //<option value=\"\">Select Employee
			for(int i=0; skillList!=null && i<skillList.size(); i++) {
				sbSkills.append("<option value=\"" + skillList.get(i).getSkillsId() + "\">" + skillList.get(i).getSkillsName() + "</option>");
			}
			request.setAttribute("sbSkills", sbSkills.toString());
			
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			Map<String, String> hmWLocationName = CF.getWLocationMap(con, null, null);
			pst = con.prepareStatement("select * from project_resource_req_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			List<List<String>> alResReqData = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				StringBuilder sbWLoc = new StringBuilder(); //<option value=\"\">Select Employee
				List<String> alLoc = new ArrayList<String>();
				List<String> alExistLoc = new ArrayList<String>();
				if(strUserType!=null && strUserType.equals(RECRUITER)) {
					if(rs.getString("wloc_ids_filter")!=null && !rs.getString("wloc_ids_filter").equals("")) {
						alLoc = Arrays.asList(rs.getString("wloc_ids_filter").split(","));
						if(rs.getString("wloc_ids") !=null) {
							alExistLoc = Arrays.asList(rs.getString("wloc_ids").split(","));
						}
					} else {
						if(rs.getString("wloc_ids") !=null) {
							alLoc = Arrays.asList(rs.getString("wloc_ids").split(","));
							alExistLoc = Arrays.asList(rs.getString("wloc_ids").split(","));
						}
					}
				} else {
					if(rs.getString("wloc_ids") !=null) {
						alLoc = Arrays.asList(rs.getString("wloc_ids").split(","));
					}
				}
				for(int i=0; workLocationList!=null && i<workLocationList.size(); i++) {
					sbWLoc.append("<option value=\"" + workLocationList.get(i).getwLocationId() + "\" ");
					if(alLoc.contains(workLocationList.get(i).getwLocationId())) {
						sbWLoc.append(" selected ");
					}
					sbWLoc.append(">" + workLocationList.get(i).getwLocationName() + "</option>");
				}
				innerList.add(sbWLoc.toString() );
				StringBuilder sbSkill = new StringBuilder(); //<option value=\"\">Select Employee
				for(int i=0; skillList!=null && i<skillList.size(); i++) {
					sbSkill.append("<option value=\"" + skillList.get(i).getSkillsId() + "\" ");
					if(rs.getInt("skill_id") == uF.parseToInt(skillList.get(i).getSkillsId())) {
						sbSkill.append(" selected ");
					}
					sbSkill.append(">" + skillList.get(i).getSkillsName() + "</option>");
				}
				innerList.add(sbSkill.toString());
				if(strUserType!=null && strUserType.equals(RECRUITER)) {
					if(uF.parseToDouble(rs.getString("min_exp_filter"))>0 || uF.parseToDouble(rs.getString("max_exp_filter"))>0) {
						innerList.add(rs.getString("min_exp_filter"));
						innerList.add(rs.getString("max_exp_filter"));
					} else {
						innerList.add(rs.getString("min_exp"));
						innerList.add(rs.getString("max_exp"));
					}
				} else {
					innerList.add(rs.getString("min_exp"));
					innerList.add(rs.getString("max_exp"));
				}
				innerList.add(rs.getString("req_resource"));
				innerList.add(rs.getString("resource_gap"));
				innerList.add(rs.getString("skill_id")); //6
				innerList.add(rs.getString("project_resource_req_id")); //7
				innerList.add(hmSkillName.get(rs.getString("skill_id"))); //8
				StringBuilder sbWLocName = null;
				for(int a=0; alExistLoc!=null && a<alExistLoc.size(); a++) {
					if(uF.parseToInt(alExistLoc.get(a))>0) {
						if(sbWLocName ==null) {
							sbWLocName = new StringBuilder();
							sbWLocName.append(hmWLocationName.get(alExistLoc.get(a)));
						} else {
							sbWLocName.append(", "+hmWLocationName.get(alExistLoc.get(a)));
						}
					}
				}
				if(sbWLocName ==null) {
					sbWLocName = new StringBuilder();
				}
				innerList.add(sbWLocName.toString()); //9
				innerList.add(rs.getString("min_exp"));//10
				innerList.add(rs.getString("max_exp"));//11
				alResReqData.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("alResReqData", alResReqData);
			
//			skillList = new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(proOrgId));
//			
//			workLocationList = new FillWLocation(request).fillWLocation(proOrgId);
//			levelList = new FillLevel(request).fillLevel(uF.parseToInt(proOrgId));
//			sbuList = new FillServices(request).fillServices(proOrgId, uF);
//			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(proOrgId));
			
			pst = con.prepareStatement("select * from project_emp_details where pro_id=? and _isteamlead='true'");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			StringBuilder sb1 = new StringBuilder();
			int i = 0;
			while (rs.next()) {
				if (i == 0)
					sb1.append(rs.getString("emp_id"));
				else
					sb1.append("," + rs.getString("emp_id"));

				i++;
			}
			rs.close();
			pst.close();

			request.setAttribute("TL", sb1.toString());

			pst = con.prepareStatement("select * from project_emp_details where pro_id=? and _isteamlead='false'");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			StringBuilder sb2 = new StringBuilder();
			i = 0;
			while (rs.next()) {
				if (i == 0)
					sb2.append(rs.getString("emp_id"));
				else
					sb2.append("," + rs.getString("emp_id"));

				i++;

			}
			rs.close();
			pst.close();
			request.setAttribute("TM", sb2.toString());

			pst = con.prepareStatement("select * from project_skill_details where pro_id=?");

			pst.setInt(1, uF.parseToInt(getPro_id()));

			rs = pst.executeQuery();
			StringBuilder sb = new StringBuilder();
			i = 0;
			while (rs.next()) {
				if (i == 0)
					sb.append(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
				else
					sb.append("," + CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
				i++;
			}
			rs.close();
			pst.close();

			setSkill(sb.toString().split(","));
			setF_wLocation("".split(","));
			setF_service("".split(","));
			setF_level("".split(","));
			setF_department("".split(","));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void getFullDetails(UtilityFunctions uF) {


		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			//Map<String, String> hmEmpLevelMap = cF.getEmpLevelMap();
			con = db.makeConnection(con);
			
			List<List<String>> variableCostList = new ArrayList<List<String>>();
			List<List<String>> alReport = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmProjectSummarySubTaskReport = new LinkedHashMap<String, List<List<String>>>();
			String actualBillingType = "";
			
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select * from variable_cost where pro_id=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				double dblVariableCost = 0.0d;
				
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("variable_name"));
					alInner.add(rs.getString("variable_cost"));
	
					variableCostList.add(alInner);
					
					dblVariableCost += uF.parseToDouble(rs.getString("variable_cost"));
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("SELECT * FROM projectmntnc where pro_id = ?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				String strBillingType = null;
				String strActualBilling = null;
				while(rs.next()) {
					strBillingType = rs.getString("billing_type");
					strActualBilling = rs.getString("actual_calculation_type");
					request.setAttribute("strActualBilling", strActualBilling);
				}
				rs.close();
				pst.close();
				
				
	//			Map<String, String> hmEmpGrossAmount = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", strActualBilling);
				List<String> empList = new ArrayList<String>();
	
				Map<String, String> empcostMp = CF.getProjectEmpActualRates(con, uF, getPro_id(), strActualBilling);
				Map<String, String> hmEmpBillRate = CF.getProjectEmpBillRates(con, uF, getPro_id(), strActualBilling);
				
				pst = con.prepareStatement("select * from services_project");
				rs = pst.executeQuery();
				Map<String, String> hmServiceProject = new HashMap<String, String>();			
				while (rs.next()) {
					hmServiceProject.put(rs.getString("service_project_id"), rs.getString("service_name"));
				}
				rs.close();
				pst.close();
				
				
				Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getPro_id());
				
			// pst = con
			// .prepareStatement("select a.activity_name,a.task_id,pm.pro_name,epd.emp_fname,a.emp_id,pm.service,a.idealtime,a.billable_rate,a.billable_amount from(select * from activity_info where pro_id=? order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id=pm.pro_id) LEFT JOIN employee_personal_details epd ON(a.emp_id=epd.emp_per_id) ");
//			pst = con.prepareStatement("select a.activity_name,a.task_id,pm.pro_name,epd.emp_fname,epd.emp_lname,a.emp_id,pm.service,a.idealtime," +
//				"a.billable_rate,a.billable_amount,pm.billing_type,pm.billing_amount,eod.service_id,eod.wlocation_id,eod.depart_id," +
//				"pm.actual_calculation_type from(select * from activity_info where pro_id=? order by task_id) as a LEFT JOIN projectmntnc pm " +
//				"ON(a.pro_id=pm.pro_id) LEFT JOIN employee_personal_details epd ON(a.emp_id=epd.emp_per_id) LEFT JOIN employee_official_details eod " +
//				"ON(epd.emp_per_id=eod.emp_id)");
				pst = con.prepareStatement("select a.activity_name,a.task_id,a.resource_ids,pm.service,a.idealtime,a.billable_rate,a.billable_amount," +
					"pm.billing_type,pm.billing_amount,pm.actual_calculation_type from (select * from activity_info where pro_id=? and " +
					"parent_task_id = 0 and task_accept_status != -1 order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id = pm.pro_id) order by a.start_date, a.task_id");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				String service = null;
				ids = "";
				
				while (rs.next()) {
					actualBillingType = rs.getString("actual_calculation_type");
					double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, rs.getString("resource_ids")));
					double dblEmpBillRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(hmEmpBillRate, rs.getString("resource_ids")));
					double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					
	//				double dblGrossRate = uF.parseToDouble(hmEmpGrossAmount.get(rs.getString("emp_id")));
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("task_id")); //0
					alInner.add(rs.getString("activity_name")); //1
					alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //2
					alInner.add(uF.showData(CF.getResourcesSkills(con, rs.getString("resource_ids")), "-")); //3
					
					service = rs.getString("service");
					ids += rs.getString("task_id") + ",";
					
					alInner.add(uF.showData((String)hmServiceProject.get(service), "")); //4
					
					alInner.add(rs.getString("idealtime")); //5
	
					  
					/**
					 * Based on the instruction given By Suhrud/Anuja 19/04/2013
					 */
					
	//				alInner.add(uF.formatIntoOneDecimal(dblGrossRate));
	//				alInner.add(uF.formatIntoOneDecimal(rs.getInt("idealtime") * dblGrossRate));
					double budgetedAmt = 0;
					if(dblIdealTime > 0) {
						budgetedAmt = dblEmpRate * dblIdealTime;
					}
					
					double expectedBillAmt = 0;
					if(dblIdealTime > 0) {
						expectedBillAmt = dblEmpBillRate * dblIdealTime;
					}
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpRate)); //6
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(budgetedAmt)); //7
					
					
					if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
						alInner.add("Fixed"); //8
						alInner.add("Fixed"); //9
						
						request.setAttribute("FIXED", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("billing_amount"))));
					} else {
						/*if(rs.getString("billable_rate")!=null) {
							alInner.add(rs.getString("billable_rate"));
						} else {
							alInner.add(uF.formatIntoOneDecimalWithOutComma(dblEmpRate));
						}
						
						if(rs.getString("billable_amount")!=null) {
							alInner.add(rs.getString("billable_amount"));
						} else {
							alInner.add(uF.formatIntoOneDecimalWithOutComma(dblIdealTime * dblEmpRate));
						}*/
						alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpBillRate)); //8
						alInner.add(uF.formatIntoTwoDecimalWithOutComma(expectedBillAmt)); //9
					}
					
					alInner.add(rs.getString("resource_ids")); //10
					
					alReport.add(alInner);
				}
				rs.close();
				pst.close();
				
				
				if(dblVariableCost > 0) {
					List<String> alInner = new ArrayList<String>();
					alInner.add("");
					alInner.add("Other project specific expenses");
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add(uF.formatIntoOneDecimal(dblVariableCost));
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alReport.add(alInner);
				}
			
			
				pst = con.prepareStatement("select a.parent_task_id,a.activity_name,a.task_id,a.resource_ids,pm.service,a.idealtime,a.billable_rate," +
					"a.billable_amount,pm.billing_type,pm.billing_amount,pm.actual_calculation_type from (select * from activity_info where pro_id=? and " +
					"parent_task_id != 0 and task_accept_status != -1 order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id = pm.pro_id) order by a.start_date, a.task_id");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				List<List<String>> alProjectSummarySubTaskReport = new ArrayList<List<String>>();
				ids = "";
				while (rs.next()) {
					
					alProjectSummarySubTaskReport = hmProjectSummarySubTaskReport.get(rs.getString("parent_task_id"));
					if(alProjectSummarySubTaskReport == null) alProjectSummarySubTaskReport = new ArrayList<List<String>>();
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("task_id")); //0
					alInner.add(rs.getString("activity_name")); //1
					alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //2
					alInner.add(uF.showData(CF.getResourcesSkills(con, rs.getString("resource_ids")), "-")); //3
					alInner.add(uF.showData(CF.getProjectServiceNameById(con, rs.getString("service")), "")); //4
		
					alProjectSummarySubTaskReport.add(alInner);
					hmProjectSummarySubTaskReport.put(rs.getString("parent_task_id"), alProjectSummarySubTaskReport);
				}
				rs.close();
				pst.close();
			}
//			System.out.println("hmProjectSummarySubTaskReport ====>> " + hmProjectSummarySubTaskReport);
			request.setAttribute("hmProjectSummarySubTaskReport", hmProjectSummarySubTaskReport);
			request.setAttribute("actualBillingType", actualBillingType);
			request.setAttribute("variableCostList", variableCostList);

			
//			pst = con.prepareStatement("select a.activity_name,a.task_id,pm.pro_name,epd.emp_fname,epd.emp_lname,a.emp_id,pm.service,a.idealtime,eod.service_id,eod.wlocation_id,eod.depart_id,a.billable_rate,a.billable_amount, pm.billing_type, pm.billing_amount from(select * from activity_info where pro_id=? order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id=pm.pro_id) LEFT JOIN employee_personal_details epd ON(a.emp_id=epd.emp_per_id) LEFT JOIN employee_official_details eod ON(epd.emp_per_id=eod.emp_id)  order by emp_id");
//			pst.setInt(1, uF.parseToInt(getPro_id()));
//			rs = pst.executeQuery();
//			List<List<String>> alReportE = new ArrayList<List<String>>();
//			
//			Map<String, Map<String, String>> hmEmpTasks = new HashMap<String, Map<String, String>>();
//			Map<String, String> hmEmpTasksInner = new HashMap<String, String>();
//			ids = "";
//			
//			String strEmpIdNew = null;
//			while (rs.next()) {
//				strEmpIdNew = rs.getString("emp_id");
//				hmEmpTasksInner = (Map)hmEmpTasks.get(strEmpIdNew);
//				if(hmEmpTasksInner==null)hmEmpTasksInner=new HashMap();
//				
//				
//				double dblEmpRate = uF.parseToDouble(empcostMp.get(rs.getString("emp_id")));
////				double dblGrossRate = uF.parseToDouble(hmEmpGrossAmount.get(rs.getString("emp_id")));
//				double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
//				
//				hmEmpTasksInner.put("EMP_NAME", rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
//				String strTask = (String)hmEmpTasksInner.get("TASK_NAME");
//				if(strTask!=null) {
//					strTask = strTask + ", "+rs.getString("activity_name");
//				} else {
//					strTask = rs.getString("activity_name");
//				}
//				hmEmpTasksInner.put("TASK_NAME", strTask );
//				
//				double dblTime = uF.parseToDouble((String)hmEmpTasksInner.get("IDEAL_TIME"));  
//				dblTime += uF.parseToDouble(rs.getString("idealtime"));
//				hmEmpTasksInner.put("IDEAL_TIME", dblTime+"");
//				hmEmpTasksInner.put("RATE", uF.formatIntoOneDecimal(0)); //dblGrossRate
//				
//				
//				hmEmpTasksInner.put("BUDGET_AMOUNT",uF.formatIntoOneDecimal(rs.getDouble("idealtime") * 1)); //dblGrossRate
//				
//				service = rs.getString("service");
//				hmEmpTasksInner.put("SERVICE_NAME",service);
//				
//				ids += rs.getString("task_id") + ",";
//				
//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
//				
//					hmEmpTasksInner.put("B_RATE","Fixed");
//					hmEmpTasksInner.put("B_AMOUNT","Fixed");
//							
//					request.setAttribute("FIXED", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("billing_amount"))));
//				} else {
//					if(rs.getString("billable_rate")!=null) {
//						hmEmpTasksInner.put("B_RATE", rs.getString("billable_rate"));
//					} else {
//						hmEmpTasksInner.put("B_RATE", uF.formatIntoOneDecimalWithOutComma(dblEmpRate));
//					}
//					
//					if(rs.getString("billable_amount")!=null) {
//						hmEmpTasksInner.put("B_AMOUNT",rs.getString("billable_amount"));
//						hmEmpTasksInner.put("B_AMOUNT",uF.formatIntoOneDecimalWithOutComma(dblTime * uF.parseToDouble(hmEmpTasksInner.get("B_RATE"))));
//					} else {
//						hmEmpTasksInner.put("B_AMOUNT",uF.formatIntoOneDecimalWithOutComma(dblTime * dblEmpRate));
//					}
//					
//				}
//
//				alReportE.add(alInner);
//				
//				hmEmpTasks.put(strEmpIdNew, hmEmpTasksInner);
//			}
			
			
			
//			pst = con.prepareStatement("select * from services_project where service_project_id in(" + service + ")");
//			rs = pst.executeQuery();
//			int i = 0;
//			StringBuilder sb1 = new StringBuilder();
//
//			while (rs.next()) {
//				if (i == 0)
//					sb1.append(rs.getString("service_name"));
//				else
//					sb1.append("," + rs.getString("service_name"));
//
//			}
//			request.setAttribute("service", sb1.toString());

			request.setAttribute("alReport", alReport);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void setDetails(UtilityFunctions uF) {

		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {

//			String[] billablerate = request.getParameterValues("billablerate");
//			String[] billableamount = request.getParameterValues("billableamount");
			String[] variableName = request.getParameterValues("variableName");
			String[] variableAmount = request.getParameterValues("variableAmount");
//			String[] empIDs = request.getParameterValues("empID");
//			String actualBillingType = request.getParameter("actualBillingType");
			
			con = db.makeConnection(con);
//			String[] arrIds = null;
					
//			if(ids!=null) {
//				arrIds = ids.split(",");
//			}

			/*for (int i = 0; arrIds!=null && i < arrIds.length; i++) {
				pst = con.prepareStatement("update activity_info set billable_rate=? ,billable_amount=? where task_id=?");
				pst.setDouble(1, uF.parseToDouble(billablerate[i]));
				pst.setDouble(2, uF.parseToDouble(billableamount[i]));
				pst.setInt(3, uF.parseToInt(arrIds[i]));
				pst.executeUpdate();
				pst.close();

			}*/
			
//			for (int i = 0; empIDs!=null && i < empIDs.length; i++) {
//				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("update project_emp_details set emp_id=? ");
//				if(actualBillingType != null && actualBillingType.equals("H")) {
//					sbQuery.append(", emp_rate_per_hour=? ");
//				} else if(actualBillingType != null && actualBillingType.equals("D")) {
//					sbQuery.append(", emp_rate_per_day=?");
//				}
//				sbQuery.append(" where emp_id=? and pro_id=?");
//				pst = con.prepareStatement(sbQuery.toString());
//				pst.setInt(1, uF.parseToInt(empIDs[i]));
//				pst.setDouble(2, uF.parseToDouble(billablerate[i]));
//				pst.setInt(3, uF.parseToInt(empIDs[i]));
//				pst.setInt(4, uF.parseToInt(pro_id));
//				pst.executeUpdate();
//
//			}
			

			if(variableName!=null && variableName.length>0) {
				pst = con.prepareStatement("delete from variable_cost where pro_id=?");
				pst.setInt(1, uF.parseToInt(pro_id));
				pst.executeUpdate();
				pst.close();
			}
			

			if (variableName != null) {
				for (int i = 0; i < variableName.length; i++) {
					if(variableAmount[i]!=null && variableAmount[i].length()>0 && uF.parseToDouble(variableAmount[i])>0 ) {
						pst = con.prepareStatement("insert into variable_cost(variable_name,variable_cost,pro_id)values(?,?,?)");
	
						pst.setString(1, variableName[i]);
						pst.setDouble(2, uF.parseToDouble(variableAmount[i]));
						pst.setInt(3, uF.parseToInt(pro_id));
						pst.executeUpdate();
						pst.close();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void getDetails(UtilityFunctions uF) {
//		System.out.println("getDetails ==== main ==== >> ");

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);

//			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("SELECT * FROM projectmntnc where pro_id = ?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				
				String strBillingType = null;
				String strActualBilling = null;
				while(rs.next()) {
					strBillingType = rs.getString("billing_type");
					strActualBilling = rs.getString("actual_calculation_type");
					request.setAttribute("strActualBilling", strActualBilling);
				}
				rs.close();
				pst.close();
//			}

			Map<String, String> empcostMp = CF.getProjectEmpActualRates(con, uF, getPro_id(), strActualBilling);
			Map<String, String> hmEmpBillRate = CF.getProjectEmpBillRates(con, uF, getPro_id(), strActualBilling);
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getPro_id());
			
			String actualBillingType = "";
//			Map<String, String> hmEmpGrossSalaryMap = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", strActualBilling);
			
//				double dblGrossRate = uF.parseToDouble(hmEmpGrossAmount.get(rs.getString("emp_id")));
				
//			pst = con.prepareStatement("select a.activity_name,a.task_id,pm.pro_name,epd.emp_fname,epd.emp_lname,a.emp_id,pm.service,a.idealtime," +
//				"eod.service_id,eod.wlocation_id,eod.depart_id,a.billable_rate,a.billable_amount, pm.billing_type, pm.billing_amount, pm.actual_calculation_type " +
//				"from(select * from activity_info where pro_id=? order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id=pm.pro_id) LEFT JOIN " +
//				"employee_personal_details epd ON(a.emp_id=epd.emp_per_id) LEFT JOIN employee_official_details eod ON(epd.emp_per_id=eod.emp_id)");
			
			Map<String, List<List<String>>> hmProjectCostSubTaskReport = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alProjectCostReport = new ArrayList<List<String>>();
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select a.activity_name,a.task_id,a.resource_ids,pm.service,a.idealtime,a.billable_rate,a.billable_amount," +
					"pm.billing_type,pm.billing_amount,pm.actual_calculation_type from (select * from activity_info where pro_id=? and " +
					"parent_task_id = 0 and task_accept_status  != -1 order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id = pm.pro_id) order by a.start_date, a.task_id");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				String service = null;
				ids = "";
				while (rs.next()) {
					actualBillingType = rs.getString("actual_calculation_type");
					double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, rs.getString("resource_ids")));
					double dblEmpBillRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(hmEmpBillRate, rs.getString("resource_ids")));
					double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("task_id"));
					alInner.add(rs.getString("activity_name")); //1
					alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //2
					alInner.add(uF.showData(CF.getResourcesSkills(con, rs.getString("resource_ids")), "-")); //3
					alInner.add(uF.showData(CF.getProjectServiceNameById(con, rs.getString("service")), "")); //4
					alInner.add(rs.getString("idealtime")); //5
	//				alInner.add(uF.showData(empcostMp.get(rs.getString("emp_id")), "0"));
					
					/**
					 * Based on the instruction given By Suhrud/Anuja 19/04/2013
					 */
					
	//				alInner.add(uF.formatIntoOneDecimal(dblGrossRate));
	//				alInner.add(uF.formatIntoOneDecimal(rs.getDouble("idealtime") * dblGrossRate));
					double budgetedAmt = 0;
					if(dblIdealTime > 0) {
						budgetedAmt = dblEmpRate * dblIdealTime;
					}
					
					double expectedBillAmt = 0;
					if(dblIdealTime > 0) {
						expectedBillAmt = dblEmpBillRate * dblIdealTime;
					}
					
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpRate)); //6
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(budgetedAmt)); //7
					
					service = rs.getString("service");
					ids += rs.getString("task_id") + ",";
					
					
					if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
						alInner.add("Fixed"); //8
						alInner.add("Fixed"); //9
						
						request.setAttribute("FIXED", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("billing_amount"))));
					} else {
						/*if(rs.getString("billable_rate")!=null) {
							alInner.add(rs.getString("billable_rate"));
						} else {
							alInner.add(uF.formatIntoOneDecimalWithOutComma(dblEmpRate));
						} 
						
						if(rs.getString("billable_amount")!=null) {
							alInner.add(rs.getString("billable_amount"));
						} else {
							alInner.add(uF.formatIntoOneDecimalWithOutComma(dblIdealTime * dblEmpRate));
						}*/
						alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpBillRate)); //8
						alInner.add(uF.formatIntoTwoDecimalWithOutComma(expectedBillAmt)); //9
					}
	
					alInner.add(rs.getString("resource_ids")); //10
					
					alProjectCostReport.add(alInner);
				}
				rs.close();
				pst.close();
				
				
				
				pst = con.prepareStatement("select a.parent_task_id,a.activity_name,a.task_id,a.resource_ids,pm.service,a.idealtime,a.billable_rate," +
					"a.billable_amount,pm.billing_type,pm.billing_amount,pm.actual_calculation_type from (select * from activity_info where pro_id=? " +
					"and parent_task_id != 0 and task_accept_status != -1 order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id = pm.pro_id)  order by a.start_date, a.task_id");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				List<List<String>> alProjectCostSubTaskReport = new ArrayList<List<String>>();
				ids = "";
				while (rs.next()) {
					
					alProjectCostSubTaskReport = hmProjectCostSubTaskReport.get(rs.getString("parent_task_id"));
					if(alProjectCostSubTaskReport == null) alProjectCostSubTaskReport = new ArrayList<List<String>>();
					
	//				actualBillingType = rs.getString("actual_calculation_type");
	//				double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, rs.getString("resource_ids")));
	//				double dblEmpBillRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(hmEmpBillRate, rs.getString("resource_ids")));
	//				double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("task_id")); //0
					alInner.add(rs.getString("activity_name")); //1
					alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //2
					alInner.add(uF.showData(CF.getResourcesSkills(con, rs.getString("resource_ids")), "-")); //3
					alInner.add(uF.showData(CF.getProjectServiceNameById(con, rs.getString("service")), "")); //4
	//				alInner.add(rs.getString("idealtime"));
					
	//				double budgetedAmt = 0;
	//				if(dblIdealTime > 0) {
	//					budgetedAmt = dblEmpRate * dblIdealTime;
	//				}
	//				
	//				double expectedBillAmt = 0;
	//				if(dblIdealTime > 0) {
	//					expectedBillAmt = dblEmpBillRate * dblIdealTime;
	//				}
					
	//				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpRate));
	//				alInner.add(uF.formatIntoTwoDecimalWithOutComma(budgetedAmt));
					
					
					
	//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
	//					alInner.add("Fixed");
	//					alInner.add("Fixed");
	//					
	//					request.setAttribute("FIXED", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("billing_amount"))));
	//				} else {
	//					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpBillRate));
	//					alInner.add(uF.formatIntoTwoDecimalWithOutComma(expectedBillAmt));
	//				}
	
	//				alInner.add(rs.getString("resource_ids"));
					
					alProjectCostSubTaskReport.add(alInner);
					hmProjectCostSubTaskReport.put(rs.getString("parent_task_id"), alProjectCostSubTaskReport);
				}
				rs.close();
				pst.close();
			}
//			System.out.println("hmProjectCostSubTaskReport ====>> " + hmProjectCostSubTaskReport);
			request.setAttribute("hmProjectCostSubTaskReport", hmProjectCostSubTaskReport);
			
			request.setAttribute("actualBillingType", actualBillingType);
//			pst = con.prepareStatement("select a.activity_name,a.task_id,pm.pro_name,epd.emp_fname,epd.emp_lname,a.emp_id,pm.service,a.idealtime,eod.service_id,eod.wlocation_id,eod.depart_id,a.billable_rate,a.billable_amount, pm.billing_type, pm.billing_amount from(select * from activity_info where pro_id=? order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id=pm.pro_id) LEFT JOIN employee_personal_details epd ON(a.emp_id=epd.emp_per_id) LEFT JOIN employee_official_details eod ON(epd.emp_per_id=eod.emp_id)  order by emp_id");
//			pst.setInt(1, uF.parseToInt(getPro_id()));
//			rs = pst.executeQuery();
//			List<List<String>> alReportE = new ArrayList<List<String>>();
//			
//			Map<String, Map<String, String>> hmEmpTasks = new HashMap<String, Map<String, String>>();
//			Map<String, String> hmEmpTasksInner = new HashMap<String, String>();
//			ids = "";
//			
//			String strEmpIdNew = null;
//			while (rs.next()) {
//				strEmpIdNew = rs.getString("emp_id");
//				hmEmpTasksInner = (Map)hmEmpTasks.get(strEmpIdNew);
//				if(hmEmpTasksInner==null)hmEmpTasksInner=new HashMap();
//				
//				double dblEmpRate = uF.parseToDouble(empcostMp.get(rs.getString("emp_id")));
//				double dblGrossRate = uF.parseToDouble(hmEmpGrossSalaryMap.get(rs.getString("emp_id")));
//				double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
//				
//				hmEmpTasksInner.put("EMP_NAME", rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
//				String strTask = (String)hmEmpTasksInner.get("TASK_NAME");
//				if(strTask!=null) {
//					strTask = strTask + ", "+rs.getString("activity_name");
//				} else {
//					strTask = rs.getString("activity_name");
//				}
//				hmEmpTasksInner.put("TASK_NAME", strTask );
//				
//				double dblTime = uF.parseToDouble((String)hmEmpTasksInner.get("IDEAL_TIME"));  
//				dblTime += uF.parseToDouble(rs.getString("idealtime"));
//				hmEmpTasksInner.put("IDEAL_TIME", dblTime+"");
//				hmEmpTasksInner.put("RATE", uF.formatIntoOneDecimal(dblGrossRate));
//				
//				
//				hmEmpTasksInner.put("BUDGET_AMOUNT",uF.formatIntoOneDecimal(rs.getDouble("idealtime") * dblGrossRate));
//				
//				service = rs.getString("service");
//				hmEmpTasksInner.put("SERVICE_NAME",service);
//				
//				ids += rs.getString("task_id") + ",";
//				
//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
//				
//					hmEmpTasksInner.put("B_RATE","Fixed");
//					hmEmpTasksInner.put("B_AMOUNT","Fixed");
//							
//					request.setAttribute("FIXED", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("billing_amount"))));
//				} else {
//					if(rs.getString("billable_rate")!=null) {
//						hmEmpTasksInner.put("B_RATE", rs.getString("billable_rate"));
//					} else {
//						hmEmpTasksInner.put("B_RATE", uF.formatIntoOneDecimalWithOutComma(dblEmpRate));
//					}
//					if(rs.getString("billable_amount")!=null) {
//						hmEmpTasksInner.put("B_AMOUNT",rs.getString("billable_amount"));
//						hmEmpTasksInner.put("B_AMOUNT",uF.formatIntoOneDecimalWithOutComma(dblTime * uF.parseToDouble(hmEmpTasksInner.get("B_RATE"))));
//					} else {
//						hmEmpTasksInner.put("B_AMOUNT",uF.formatIntoOneDecimalWithOutComma(dblTime * dblEmpRate));
//					}
//				}
//				alReportE.add(alInner);
//				hmEmpTasks.put(strEmpIdNew, hmEmpTasksInner);
//			}
			

			/*pst = con.prepareStatement("select * from services_project where service_project_id in(" + service + ")");
			rs = pst.executeQuery();

			int i = 0;
			StringBuilder sb1 = new StringBuilder();

			while (rs.next()) {
				if (i == 0)
					sb1.append(rs.getString("service_name"));
				else
					sb1.append("," + rs.getString("service_name"));

			}*/
			
//			System.out.println("============>"+sb1.toString());
			
			List<List<String>> variableList = new ArrayList<List<String>>();
			if(uF.parseToInt(getPro_id()) > 0) {
				pst = con.prepareStatement("select * from variable_cost where pro_id=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("variable_name"));
					alInner.add(rs.getString("variable_cost"));
					variableList.add(alInner);
				}
				rs.close();
				pst.close();
			}
//			request.setAttribute("service", sb1.toString());
			request.setAttribute("variableList", variableList);

			request.setAttribute("alProjectCostReport", alProjectCostReport);
//			request.setAttribute("hmEmpTasks", hmEmpTasks);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void insertActivityDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
//			System.out.println("insertActivityDetails ...");
			String[] taskname = request.getParameterValues("taskname");
			String[] taskDescription = request.getParameterValues("taskDescription");
			String[] isRecurringTask = request.getParameterValues("isRecurringTask");
			String[] isBillableTask = request.getParameterValues("isBillableTask");
			String[] taskID = request.getParameterValues("taskID");
			String[] dependency = request.getParameterValues("dependency");
			String[] dependencyType = request.getParameterValues("dependencyType");
			
//			System.out.println("isBillableTask ===>> " + isBillableTask.length);
			
//			String task_dependency = request.getParameter("task_dependency");
//			String dependency_type = request.getParameter("dependency_type");

			String[] priority = request.getParameterValues("priority");
			String[] empSkills = request.getParameterValues("empSkills");
			
			String[] startDate = request.getParameterValues("startDate");
			String[] deadline1 = request.getParameterValues("deadline1");
			String[] idealTime = request.getParameterValues("idealTime");
			String[] colourCode = request.getParameterValues("colourCode");

			con = db.makeConnection(con);

			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			String forcedTask = null;
			Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, getPro_id());
			if(hmProjectData != null) {
				forcedTask = CF.getProjectForcedTask(con, hmProjectData.get("PRO_ORG_ID"));
			}
			
			String freqTaskName = null;
			pst = con.prepareStatement("select freq_start_date,freq_end_date from projectmntnc_frequency where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				freqTaskName = uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT_STR)+" to "+uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT_STR);
			}
			rs.close();
			pst.close();
			
//			System.out.println("taskID ===>>> " + taskID.length);
			for (int i = 0; taskID!=null && i<taskID.length; i++) {
//				System.out.println("taskID ===>>> "+ i +" --- "+ taskID[i]);
				if(startDate[i] != null && !startDate[i].equals("") && !startDate[i].equals("-") && deadline1[i] !=null && !deadline1[i].equals("") && !deadline1[i].equals("-")) {
					freqTaskName = uF.getDateFormat(startDate[i], DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(deadline1[i], DATE_FORMAT, DATE_FORMAT_STR);
				}
				String[] emp_id = request.getParameterValues("emp_id"+getTaskTRId()[i]);
//				System.out.println("emp_id ===>> " + emp_id.length);
//				System.out.println("getTaskTRId().length ====>> " + getTaskTRId().length);
//				System.out.println("getTaskTRId()[i] ====>> " + getTaskTRId()[i]);
				
				String[] subtaskname = request.getParameterValues("subtaskname"+getTaskTRId()[i]); 
				String[] subtaskDescription = request.getParameterValues("subTaskDescription"+getTaskTRId()[i]);
				String[] isRecurringSubTask = request.getParameterValues("isRecurringSubTask"+getTaskTRId()[i]);
				String[] isBillableSubTask = request.getParameterValues("isBillableSubTask"+getTaskTRId()[i]);
				
				String[] subTaskID = request.getParameterValues("subTaskID"+getTaskTRId()[i]);
				String[] subTaskTRId = request.getParameterValues("subTaskTRId"+getTaskTRId()[i]);
				
//				System.out.println("subTaskTRId.length ====>> " + subTaskTRId.length);
				
				String[] subDependency = request.getParameterValues("subDependency"+getTaskTRId()[i]);
				String[] subDependencyType = request.getParameterValues("subDependencyType"+getTaskTRId()[i]);
				 
				String[] subpriority = request.getParameterValues("subpriority"+getTaskTRId()[i]);
//				String[] empSubSkills = request.getParameterValues("empSubSkills"+getTaskTRId()[i]);
				
				
				String[] substartDate = request.getParameterValues("substartDate"+getTaskTRId()[i]);
				String[] subdeadline1 = request.getParameterValues("subdeadline1"+getTaskTRId()[i]);
				String[] subidealTime = request.getParameterValues("subidealTime"+getTaskTRId()[i]);
				String[] subcolourCode = request.getParameterValues("subcolourCode"+getTaskTRId()[i]);
				
				StringBuilder sbEmpIds = null;
//				System.out.println("emp_id ===>> " + emp_id);
				if (emp_id != null && emp_id.length > 0) {
					List<String> empIdList = Arrays.asList(emp_id);
//					System.out.println("empIdList ===>> " + empIdList);
					for (int a = 0; empIdList != null && a < empIdList.size(); a++) {
						if (sbEmpIds == null) {
							sbEmpIds = new StringBuilder();
							sbEmpIds.append("," + empIdList.get(a).trim()+",");
						} else {
							sbEmpIds.append(empIdList.get(a).trim()+",");
						}
					}
				}
				if (sbEmpIds == null) {
					sbEmpIds = new StringBuilder();
				}
//				System.out.println("sbEmpIds ===>> " +sbEmpIds.toString());
				
				List<String> alEmp = new ArrayList<String>();
				int x = 0;
				if(uF.parseToInt(taskID[i]) > 0) {
//					System.out.println("isBillableTask[i] ===>> " + isBillableTask[i]);
					pst = con.prepareStatement("select pro_id,resource_ids from activity_info where task_id=?");
					pst.setInt(1, uF.parseToInt(taskID[i]));
					rs = pst.executeQuery();
					String strResourceIds = null;
					while(rs.next()){
						strResourceIds = rs.getString("resource_ids");
					}
					rs.close();
					pst.close();
					
					if(strResourceIds !=null && !strResourceIds.trim().equals("") && strResourceIds.contains(",")) {
						strResourceIds = strResourceIds.substring(1, strResourceIds.length()-1);
						List<String> alTemp = new ArrayList<String>();
						if (emp_id != null && emp_id.length > 0) {
							alTemp = Arrays.asList(emp_id);
						}
						List<String> alResource = Arrays.asList(strResourceIds.split(","));
						/*for (int a = 0; alResource != null && a < alResource.size(); a++) {
							if (!alTemp.contains(alResource.get(a))) {
								if (sbEmpIds == null) {
									sbEmpIds = new StringBuilder();
									sbEmpIds.append("," + alResource.get(a).trim()+",");
								} else {
									sbEmpIds.append(alResource.get(a).trim()+",");
								}
							}
						}*/
						for(String strEmp : alTemp) {
							if(!alResource.contains(strEmp.trim())) {
								alEmp.add(strEmp.trim());
							}
						}
					} else {
						if (emp_id != null && emp_id.length > 0) {
							alEmp = Arrays.asList(emp_id);
						}
					}
					
					pst = con.prepareStatement("update activity_info set activity_name=?, priority=?, resource_ids=?, deadline=?, idealtime=?, start_date=?," +
						" dependency_task=?, dependency_type=?, color_code=?, taskstatus=?, task_skill_id=?, task_description=?," +
						"task_freq_name=?,recurring_task=?,task_accept_status=?,is_billable_task=? where task_id =? ");
					pst.setString(1, taskname[i]);
					pst.setString(2, priority[i]);
//					pst.setInt(3, uF.parseToInt(emp_id[i]));
					pst.setString(3, sbEmpIds.toString());
					pst.setDate(4, uF.getDateFormat(deadline1[i], DATE_FORMAT));
					pst.setDouble(5, uF.parseToDouble(idealTime[i]));
					pst.setDate(6, uF.getDateFormat(startDate[i], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(dependency[i]));
					pst.setString(8, dependencyType[i]);
					pst.setString(9, colourCode[i]);
					pst.setString(10, "New Task");
//					pst.setInt(11, uF.parseToInt(empSkills[i]));
					pst.setInt(11, 0);
					pst.setString(12, taskDescription[i]);
//					pst.setInt(13, uF.parseToInt(strSessionEmpId));
					pst.setString(13, freqTaskName);
					pst.setInt(14, uF.parseToInt(isRecurringTask[i]));
					if(uF.parseToBoolean(forcedTask)) {
						pst.setInt(15, 1);
					} else {
						pst.setInt(15, 0);
					}
					pst.setBoolean(16, uF.parseToBoolean(isBillableTask[i]));
					pst.setInt(17, uF.parseToInt(taskID[i]));
					x = pst.executeUpdate();
					pst.close();
//					System.out.println("pst ===>> " + pst);
				} else {
					
					if (emp_id != null && emp_id.length > 0) {
						alEmp = Arrays.asList(emp_id);
					}
					pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
						"dependency_task,dependency_type,color_code,taskstatus,pro_id,task_skill_id,task_description,added_by,task_freq_name," +
						"recurring_task,task_accept_status,is_billable_task)" +
						" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?) ");
					pst.setString(1, taskname[i]);
					pst.setString(2, priority[i]);
//					pst.setInt(3, uF.parseToInt(emp_id[i]));
					pst.setString(3, sbEmpIds.toString());
					pst.setDate(4, uF.getDateFormat(deadline1[i], DATE_FORMAT));
					pst.setDouble(5, uF.parseToDouble(idealTime[i]));
					pst.setDate(6, uF.getDateFormat(startDate[i], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(dependency[i]));
					pst.setString(8, dependencyType[i]);
					pst.setString(9, colourCode[i]); 
					pst.setString(10, "New Task");
					pst.setInt(11, uF.parseToInt(getPro_id()));
//					pst.setInt(12, uF.parseToInt(empSkills[i]));
					pst.setInt(12, 0);
					pst.setString(13, taskDescription[i]);
					pst.setInt(14, uF.parseToInt(strSessionEmpId));
					pst.setString(15, freqTaskName);
					pst.setInt(16, uF.parseToInt(isRecurringTask[i]));
					if(uF.parseToBoolean(forcedTask)) {
						pst.setInt(17, 1);
					} else {
						pst.setInt(17, 0);
					}
					pst.setBoolean(18, uF.parseToBoolean(isBillableTask[i]));
					x = pst.executeUpdate();
					pst.close();
//					System.out.println("pst ===>> " + pst);
				}
				
				String strTaskId = taskID[i];
				if(uF.parseToInt(strTaskId) == 0) {
					pst = con.prepareStatement("select max(task_id) as task_id from activity_info");
					rs = pst.executeQuery();
					while (rs.next()) {
						strTaskId =	rs.getString("task_id");
					}
					rs.close();
					pst.close();
				}
				
				if(x > 0) {
					
					Map<String, String> hmTaskProData = CF.getTaskProInfo(con, strTaskId, null);
					
					for(int a=0; alEmp!=null && !alEmp.isEmpty() && a<alEmp.size(); a++) {
						Map<String, String> hmEmpData = hmEmpInfo.get(alEmp.get(a));
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_NEW_TASK_ASSIGN, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrOrgId(strOrgId);
						nF.setEmailTemplate(true);
						nF.setStrEmpId(alEmp.get(a));
						nF.setStrResourceFName(hmEmpData.get("FNAME"));
						nF.setStrResourceLName(hmEmpData.get("LNAME"));
						nF.setStrTaskName(taskname[i]);
						nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
						nF.setStrProjectOwnerName(hmEmpName.get(hmTaskProData.get("PROJECT_OWNER_ID")));
						nF.setStrTeamLeader(hmTaskProData.get("TEAM_LEADER"));
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.sendNotifications();
					}
					
					String strDomain = request.getServerName().split("\\.")[0];
					
					String alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+taskname[i]+"</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "MyWork.action";
					StringBuilder taggedWith = null;
					for(String strEmp : alEmp) {
						if(taggedWith == null) {
							taggedWith = new StringBuilder();
							taggedWith.append(","+strEmp.trim()+",");
						} else {
							taggedWith.append(strEmp.trim()+",");
						}
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(TASK_ALLOCATE_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
					}
					
					String activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+taskname[i]+"</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					UserActivities userAct = new UserActivities(con, uF, CF, request);
					userAct.setStrDomain(strDomain);
					userAct.setStrAlignWith(TASK+"");
					userAct.setStrAlignWithId(strTaskId);
					userAct.setStrTaggedWith(taggedWith.toString());
					userAct.setStrVisibilityWith(taggedWith.toString());
					userAct.setStrVisibility("2");
					userAct.setStrData(activityData);
					userAct.setStrSessionEmpId(strSessionEmpId);
					userAct.setStatus(INSERT_TR_ACTIVITY);
					Thread tt = new Thread(userAct);
					tt.run();
					
					alEmp = new ArrayList<String>();
					
					pst = con.prepareStatement("select emp_id from project_emp_details where pro_id = (select pro_id from activity_info where task_id=?) and _isteamlead = true");
					pst.setInt(1, uF.parseToInt(strTaskId));
					rs = pst.executeQuery();
					while(rs.next()){
						if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))) {
							alEmp.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					
					
					alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+taskname[i]+"</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to your team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					alertAction = "ViewMyProjects.action";
					taggedWith = null;
					for(String strEmp : alEmp) {
						if(taggedWith == null) {
							taggedWith = new StringBuilder();
							taggedWith.append(","+strEmp.trim()+",");
						} else {
							taggedWith.append(strEmp.trim()+",");
						}
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(TASK_ALLOCATE_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
					}
					
					activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+taskname[i]+"</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to your team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					userAct = new UserActivities(con, uF, CF, request);
					userAct.setStrDomain(strDomain);
					userAct.setStrAlignWith(TASK+"");
					userAct.setStrAlignWithId(strTaskId);
					userAct.setStrTaggedWith(taggedWith.toString());
					userAct.setStrVisibilityWith(taggedWith.toString());
					userAct.setStrVisibility("2");
					userAct.setStrData(activityData);
					userAct.setStrSessionEmpId(emp_id+"");
					userAct.setStatus(INSERT_TR_ACTIVITY);
					Thread tt1 = new Thread(userAct);
					tt1.run();
				}
				
				
				for (int k = 0; subTaskID != null && k < subTaskID.length; k++) {
					
					if(substartDate[k] != null && !substartDate[k].equals("") && !substartDate[k].equals("-") && subdeadline1[k] !=null && !subdeadline1[k].equals("") && !subdeadline1[k].equals("-")) {
						freqTaskName = uF.getDateFormat(substartDate[k], DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(subdeadline1[k], DATE_FORMAT, DATE_FORMAT_STR);
					}
					
					StringBuilder taskResourceIds = new StringBuilder();
					pst = con.prepareStatement("select resource_ids from activity_info where parent_task_id=? and task_id != ?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					pst.setInt(2, uF.parseToInt(subTaskID[k]));
//					System.out.println("pst ===>> " + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						taskResourceIds.append(rs.getString("resource_ids"));
					}
					rs.close();
					pst.close();
					
					List<String> alTaskResources = Arrays.asList(taskResourceIds.toString().split(","));
					List<String> taskResources = new ArrayList<String>();
					for (int aa = 0; alTaskResources != null && aa < alTaskResources.size(); aa++) {
						if(!taskResources.contains(alTaskResources.get(aa)) && !alTaskResources.get(aa).equals("") && !alTaskResources.get(aa).equals("null")) {
							taskResources.add(alTaskResources.get(aa));
						}
					}
//					System.out.println("alTaskResources ===>> " + alTaskResources);
					
//					System.out.println("subTaskTRId[k] ===>> " + getTaskTRId()[i]+"_"+subTaskTRId[k]);
					String[] sub_emp_id = request.getParameterValues("sub_emp_id"+getTaskTRId()[i]+"_"+subTaskTRId[k]);
					
					StringBuilder sbSTEmpIds = null;
//					System.out.println("sub_emp_id ===>> " + sub_emp_id);
					if (sub_emp_id != null && sub_emp_id.length > 0) {
						List<String> empIdSTList = Arrays.asList(sub_emp_id);
						for (int a = 0; empIdSTList != null && a < empIdSTList.size(); a++) {
							if(!taskResources.contains(empIdSTList.get(a).trim())) {
								if(!empIdSTList.get(a).trim().equals("") && !empIdSTList.get(a).trim().equals("null")) {
									taskResources.add(empIdSTList.get(a).trim());
								}
							}
							
							if (sbSTEmpIds == null) {
								sbSTEmpIds = new StringBuilder();
								sbSTEmpIds.append("," + empIdSTList.get(a).trim()+",");
							} else {
								sbSTEmpIds.append(empIdSTList.get(a).trim()+",");
							}
						}
					}
					if (sbSTEmpIds == null) {
						sbSTEmpIds = new StringBuilder();
					}
					
					
					x = 0;
					alEmp = new ArrayList<String>();
					if(uF.parseToInt(subTaskID[k])>0) {
						int nProId = 0;
						pst = con.prepareStatement("select pro_id,resource_ids from activity_info where task_id=?");
						pst.setInt(1, uF.parseToInt(subTaskID[k]));
						rs = pst.executeQuery();
						String strResourceIds = null;
						while(rs.next()){
							nProId = rs.getInt("pro_id");
							strResourceIds = rs.getString("resource_ids");
						}
						rs.close();
						pst.close();
						
						
						if(strResourceIds !=null && !strResourceIds.trim().equals("") && strResourceIds.contains(",")) {
							strResourceIds = strResourceIds.substring(1, strResourceIds.length()-1);
							List<String> alTemp = new ArrayList<String>();
							if (sub_emp_id != null && sub_emp_id.length > 0) {
								alTemp = Arrays.asList(sub_emp_id);
							}
							List<String> alResource = Arrays.asList(strResourceIds.split(","));
//							for (int a = 0; alResource != null && a < alResource.size(); a++) {
//								if (!alTemp.contains(alResource.get(a))) {
//									if (sbEmpIds == null) {
//										sbEmpIds = new StringBuilder();
//										sbEmpIds.append("," + alResource.get(a).trim()+",");
//									} else {
//										sbEmpIds.append(alResource.get(a).trim()+",");
//									}
//								}
//							}
							for(String strEmp : alTemp) {
								if(!alResource.contains(strEmp.trim())) {
									alEmp.add(strEmp.trim());
								}
							}
						} else {
							if (emp_id != null && emp_id.length > 0) {
								alEmp = Arrays.asList(sub_emp_id);
							}
						}
						
						pst = con.prepareStatement("update activity_info set activity_name=?, priority=?, resource_ids=?, deadline=?, idealtime=?, start_date=?," +
							"dependency_task=?,dependency_type=?,color_code=?,taskstatus=?,task_skill_id=?,task_description=?," +
							"task_freq_name=?,recurring_task=?,task_accept_status=?,is_billable_task=? where task_id =? ");
						pst.setString(1, subtaskname[k]);
						pst.setString(2, subpriority[k]);
//						pst.setInt(3, uF.parseToInt(sub_emp_id[i]));
						pst.setString(3, sbSTEmpIds.toString());
						pst.setDate(4, uF.getDateFormat(subdeadline1[k], DATE_FORMAT));
						pst.setDouble(5, uF.parseToDouble(subidealTime[k]));
						pst.setDate(6, uF.getDateFormat(substartDate[k], DATE_FORMAT));
						pst.setInt(7, uF.parseToInt(subDependency[k]));
						pst.setString(8, subDependencyType[k]);
						pst.setString(9, subcolourCode[k]);
						pst.setString(10, "New Sub Task");
//						pst.setInt(11, uF.parseToInt(empSubSkills[k]));
						pst.setInt(11, 0);
						pst.setString(12, subtaskDescription[k]);
//						pst.setInt(13, uF.parseToInt(strSessionEmpId));
						pst.setString(13, freqTaskName);
						pst.setInt(14, uF.parseToInt(isRecurringSubTask[k]));
						if(uF.parseToBoolean(forcedTask)) {
							pst.setInt(15, 1);
						} else {
							pst.setInt(15, 0);
						}
						pst.setBoolean(16, uF.parseToBoolean(isBillableSubTask[k]));
						pst.setInt(17, uF.parseToInt(subTaskID[k]));
//						System.out.println("pst ===>> " + pst);
						x = pst.executeUpdate();
						pst.close();
						
					} else {
						pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
							"dependency_task,dependency_type,color_code,taskstatus,pro_id,parent_task_id,task_skill_id,task_description,added_by," +
							"task_freq_name,recurring_task,task_accept_status,is_billable_task) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?) ");
						pst.setString(1, subtaskname[k]);
						pst.setString(2, subpriority[k]);
//						pst.setInt(3, uF.parseToInt(sub_emp_id[i]));
						pst.setString(3, sbSTEmpIds.toString());						
						pst.setDate(4, uF.getDateFormat(subdeadline1[k], DATE_FORMAT));
						pst.setDouble(5, uF.parseToDouble(subidealTime[k]));
						pst.setDate(6, uF.getDateFormat(substartDate[k], DATE_FORMAT));
						pst.setInt(7, uF.parseToInt(subDependency[k]));
						pst.setString(8, subDependencyType[k]);
						pst.setString(9, subcolourCode[k]);
						pst.setString(10, "New Sub Task");
						pst.setInt(11, uF.parseToInt(getPro_id()));
						pst.setInt(12, uF.parseToInt(strTaskId));
//						pst.setInt(13, uF.parseToInt(empSubSkills[k]));
						pst.setInt(13, 0);
						pst.setString(14, subtaskDescription[k]);
						pst.setInt(15, uF.parseToInt(strSessionEmpId));
						pst.setString(16, freqTaskName);
						pst.setInt(17, uF.parseToInt(isRecurringSubTask[k]));
						if(uF.parseToBoolean(forcedTask)) {
							pst.setInt(18, 1);
						} else {
							pst.setInt(18, 0);
						}
						pst.setBoolean(19, uF.parseToBoolean(isBillableSubTask[k]));
//						System.out.println("pst ===>> " + pst);
						x = pst.executeUpdate();
						pst.close();
					}
//					
					
					String strSubTaskId = subTaskID[k];
					if(uF.parseToInt(strSubTaskId) == 0) {
						pst = con.prepareStatement("select max(task_id) as task_id from activity_info");
						rs = pst.executeQuery();
						while (rs.next()) {
							strTaskId =	rs.getString("task_id");
						}
						rs.close();
						pst.close();
					}
					
					if(x > 0) {
						
						Map<String, String> hmTaskProData = CF.getTaskProInfo(con, strSubTaskId, null);
						
						for(int a=0; alEmp!=null && !alEmp.isEmpty() && a<alEmp.size(); a++) {
							Map<String, String> hmEmpData = hmEmpInfo.get(alEmp.get(a));
							String strDomain = request.getServerName().split("\\.")[0];
							Notifications nF = new Notifications(N_NEW_TASK_ASSIGN, CF); 
							nF.setDomain(strDomain);
							nF.request = request;
							nF.setStrOrgId(strOrgId);
							nF.setEmailTemplate(true);
							nF.setStrEmpId(alEmp.get(a));
							nF.setStrResourceFName(hmEmpData.get("FNAME"));
							nF.setStrResourceLName(hmEmpData.get("LNAME"));
							nF.setStrTaskName(subtaskname[k]+" [ST]");
							nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
							nF.setStrProjectOwnerName(hmEmpName.get(hmTaskProData.get("PROJECT_OWNER_ID")));
							nF.setStrTeamLeader(hmTaskProData.get("TEAM_LEADER"));
							nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
							
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							nF.sendNotifications();
						}
						
						String strDomain = request.getServerName().split("\\.")[0];
						String alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+subtaskname[k]+" [ST]</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction = "MyWork.action";
						StringBuilder taggedWith = null;
						for(String strEmp : alEmp) {
							if(taggedWith == null) {
								taggedWith = new StringBuilder();
								taggedWith.append(","+strEmp.trim()+",");
							} else {
								taggedWith.append(strEmp.trim()+",");
							}
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(TASK_ALLOCATE_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
						
						if(taggedWith == null) {
							taggedWith = new StringBuilder();
						}
						
						String activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+subtaskname[k]+" [ST]</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						UserActivities userAct = new UserActivities(con, uF, CF, request);
						userAct.setStrDomain(strDomain);
						userAct.setStrAlignWith(TASK+"");
						userAct.setStrAlignWithId(strSubTaskId);
						userAct.setStrTaggedWith(taggedWith.toString());
						userAct.setStrVisibilityWith(taggedWith.toString());
						userAct.setStrVisibility("2");
						userAct.setStrData(activityData);
						userAct.setStrSessionEmpId(strSessionEmpId);
						userAct.setStatus(INSERT_TR_ACTIVITY);
						Thread tt = new Thread(userAct);
						tt.run();
						
						alEmp = new ArrayList<String>();
						pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=(select pro_id from activity_info where task_id=?) and _isteamlead = true");
						pst.setInt(1, uF.parseToInt(strSubTaskId));
						rs = pst.executeQuery();
						while(rs.next()) {
							if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))) {
								alEmp.add(rs.getString("emp_id"));
							}
						}
						rs.close();
						pst.close();
						
						alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+subtaskname[k]+" [ST]</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to your team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						alertAction = "ViewMyProjects.action";
						taggedWith = null;
						for(String strEmp : alEmp) {
							if(taggedWith == null) {
								taggedWith = new StringBuilder();
								taggedWith.append(","+strEmp.trim()+",");
							} else {
								taggedWith.append(strEmp.trim()+",");
							}
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(TASK_ALLOCATE_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
						
						if(taggedWith == null) {
							taggedWith = new StringBuilder();
						}
						
						activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+subtaskname[k]+" [ST]</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to your team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						userAct = new UserActivities(con, uF, CF, request);
						userAct.setStrDomain(strDomain);
						userAct.setStrAlignWith(TASK+"");
						userAct.setStrAlignWithId(strSubTaskId);
						userAct.setStrTaggedWith(taggedWith.toString());
						userAct.setStrVisibilityWith(taggedWith.toString());
						userAct.setStrVisibility("2");
						userAct.setStrData(activityData);
						userAct.setStrSessionEmpId(strSessionEmpId);
						userAct.setStatus(INSERT_TR_ACTIVITY);
						Thread tt1 = new Thread(userAct);
						tt1.run();
					}
					
					
					StringBuilder sbTEmpIds = null;
//					System.out.println("sub_emp_id ===>> " + sub_emp_id);
					for (int a = 0; taskResources != null && a < taskResources.size(); a++) {
						if(!taskResources.get(a).equals("")) {							
							if (sbTEmpIds == null) {
								sbTEmpIds = new StringBuilder();
								sbTEmpIds.append("," + taskResources.get(a)+",");
							} else {
								sbTEmpIds.append(taskResources.get(a)+",");
							}
						}
					}
					if (sbTEmpIds == null) {
						sbTEmpIds = new StringBuilder();
					}
//					System.out.println("sbTEmpIds ===>> " + sbTEmpIds.toString());
					
					pst = con.prepareStatement("update activity_info set resource_ids=? where task_id=?");
					pst.setString(1, sbTEmpIds.toString());
					pst.setInt(2, uF.parseToInt(strTaskId));
					pst.executeUpdate();
					
//					System.out.println("ASDFG ====>> "+ i + " " + taskID[i] + " " + taskname[i] + " " + subTaskID[k] + " " + subtaskname[k] + " " + subDependency[k]);
//					System.out.println( i + " " + taskID[i] + " " + taskname[i] + " " + subTaskID[k]+ " " + subtaskname[k]+ " " + subDependency[k] + " " + subDependencyType[k] + " " + subpriority[k] 
//					     + " " + sub_emp_id[k] + " " + substartDate[k] + " " + subdeadline1[k] + " " + subidealTime[k] + " " + subcolourCode[k]);
				}
				
				double dblAllCompleted = 0.0d;
				int subTaskCnt = 0;
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ? and task_accept_status = 1");
				pst.setInt(1, uF.parseToInt(strTaskId));
//				pst.setInt(2, uF.parseToInt(taskId));
				rs = pst.executeQuery();
				while(rs.next()) {
					dblAllCompleted = rs.getDouble("completed");
					subTaskCnt = rs.getInt("count");
				}
				rs.close();
				pst.close();
				
				double avgComplted = 0.0d;
				if(dblAllCompleted > 0 && subTaskCnt > 0) {
					avgComplted = dblAllCompleted / subTaskCnt;
				}
				
				if(avgComplted > 0) {
					pst = con.prepareStatement("update activity_info set completed = ? where task_id = ?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(avgComplted)));
					pst.setInt(2, uF.parseToInt(strTaskId));
					pst.execute();
					pst.close();
				}
			}
			
			pst = con.prepareStatement("select sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0 and task_accept_status = 1");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			String projectCompletePercent = null;
			while (rs.next()) {
				projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("avrg")));
			}
			rs.close();
			pst.close();
	
			pst = con.prepareStatement("update projectmntnc set completed=? where pro_id=? ");
			pst.setDouble(1, uF.parseToDouble(projectCompletePercent));
			pst.setInt(2, uF.parseToInt(getPro_id()));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		// setOperation(null);
		// selectTotalTime();
		// insertDocuments();
	}

	
	
	public void get(UtilityFunctions uF) {
//		System.out.println("get====main====");

//		String selectEmployeeByShift = "select task_id,activity_name from activity_info where pro_id=?";
		Connection con = null;
		PreparedStatement pst = null; 
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		StringBuilder sb = new StringBuilder("<td><select name=\"task_dependency\" style=\"width:140px\"><option>Select Dependancy</option>");
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder("<select name=\"emp_id\" id=\"emp_id\" style=\"width:0px !important; \" class=\"validateRequired\" multiple></option>"); //<option value=\"\">Select Employee
		StringBuilder sb3 = new StringBuilder();
		StringBuilder sbSkills = new StringBuilder();
		StringBuilder subsb2 = new StringBuilder("<select name=\"sub_emp_id\" id=\"sub_emp_id\" style=\"width:0px !important;\" class=\"validateRequired\" multiple></option>"); //<option value=\"\">Select Employee
		StringBuilder subsb3 = new StringBuilder();
		StringBuilder subsbSkills = new StringBuilder();

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			pst = con.prepareStatement(selectEmployeeByShift);
//			pst.setInt(1, uF.parseToInt(getPro_id()));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				sb.append("<option value=\"" + rs.getString("task_id") + "\">" + rs.getString("activity_name") + "</option>");
//			}
//			sb.append("</select></td>");

			sb1.append("<select name=\"dependency_type\" style=\"width:140px !important;\"><option>Select Dependency</option><option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>");
			sb3.append("<select name=\"priority\" id=\"priority\" style=\"width:80px !important;\"><option value=\"0\">Low</option><option value=\"1\">Medium</option><option value=\"2\">High</option></select>"); //<option >Select Priority</option>
			subsb3.append("<select name=\"subpriority\" id=\"subpriority\" style=\"width:80px !important;\"><option value=\"0\">Low</option><option value=\"1\">Medium</option><option value=\"2\">High</option></select>"); //<option >Select Priority</option>
			
			pst = con.prepareStatement("select * from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and is_alive = true and pro_id=? and _isteamlead = true");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				sb2.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname") + " [TL]" + "</option>");
				subsb2.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") + " " + rs.getString("emp_lname") + " [TL]" + "</option>");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and is_alive = true and pro_id=? and _isteamlead = false");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				sb2.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</option>");
				subsb2.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") + " " + rs.getString("emp_lname") + "</option>");
			}
			rs.close();
			pst.close();
			sb2.append("</select>");
			subsb2.append("</select>");

			
			sbSkills.append("<select name=\"empSkills\" id=\"empSkills\" style=\"width:100px !important;\" class=\"validateRequired\"><option>Select Skill</option>");
			subsbSkills.append("<select name=\"empSubSkills\" id=\"empSubSkills\" style=\"width:100px !important;\" class=\"validateRequired\"><option>Select Skill</option>");
			for(FillSkills fillEmpSkillList: empSkillList) {
				sbSkills.append("<option value=\""+fillEmpSkillList.getSkillsId()+"\">"+fillEmpSkillList.getSkillsName()+"</option>");
				subsbSkills.append("<option value=\""+fillEmpSkillList.getSkillsId()+"\">"+fillEmpSkillList.getSkillsName()+"</option>");
			}
			sbSkills.append("</select>");
			subsbSkills.append("</select>");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

//		request.setAttribute("sb", sb);
		request.setAttribute("sb1", sb1);
		request.setAttribute("sb2", sb2);
		request.setAttribute("sb3", sb3);
		request.setAttribute("sbSkills", sbSkills);
		request.setAttribute("subsb2", subsb2);
		request.setAttribute("subsb3", subsb3);
		request.setAttribute("subsbSkills", subsbSkills);

	}

	// public void initialize() {
	//
	// serviceList = new FillServices().fillServices();
	// priorityList = new GetPriorityList().fillPriorityList();
	// levelList = new FillLevel().fillLevel();
	// wLocationList = new FillWLocation().fillWLocation();
	// empNamesList = new FillEmployee().fillEmployeeNameByServiceID(service);
	// teamleadNamesList =new
	// FillEmployee().fillEmployeeNameByServiceID(service);
	// skillList = new FillSkills().fillSkills("14");
	// clientList = new FillClients().fillClients();
	// // clientPocList = new FillClientPoc().fillClientPoc();
	// clientPocList = new ArrayList<FillClientPoc>();
	// daysList=new FillDaysList().fillDayList();
	// billingList = new FillBillingType().fillBillingTypeList();
	//
	//
	// }

	String prjectname;
	String prjectCode;
	String strClient;
	String strClientBrand;
	String clientPoc;
	String shortDescription;
	String description;
	String priority;
	
	String []folderTRId;
	String []strFolderName;
	String []docCountId;
	File[] strFolderDoc;
	String[] strFolderDocFileName;
	
	String[] strDocsCount;
	File[] strDoc;
	String[] strDocFileName;
	
	String startDate;
	String deadline;
	String service;
	String []level;
	String location;
	String strSBU;
	String organisation;
	
	String billingType;
	String billingTypeName;
	String billingAmountF;
	String billingAmountH;
	String estimatedHours;
	String milestoneDependentOn;
	String strDepartment;
	String hoursToDay;
	String hoursForDay;
	
	String weekdayCycle;
	String dayCycle;
	
	List<FillDepartment> departmentList;
	
	
	String []f_service;
	String []f_level;
	String []f_wLocation;
	String []f_department;
	
	
	String[] taskId;
	String[] taskTRId;
	
	File[] taskDoc;
	String[] taskDocFileName;
	

	public String getEstimatedHours() {
		return estimatedHours;
	}

	public void setEstimatedHours(String estimatedHours) {
		this.estimatedHours = estimatedHours;
	}

	public void getProjectAndTaskDocuments(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getPro_id());
			
			Map<String, String> hmFileIcon = CF.getFileIcon();
			request.setAttribute("hmFileIcon",hmFileIcon);

//			Map<String, List<Map<String,String>>> hmFolder = new HashMap<String, List<Map<String,String>>>();
//			Map<String, List<Map<String,String>>> hmDoc = new HashMap<String, List<Map<String,String>>>();
			List<Map<String, String>> alProFolder = new ArrayList<Map<String,String>>();
//			if(uF.parseToInt(getPro_id()) > 0) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
					"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
				if(uF.parseToInt(getStrTaskId()) > 0) {
					sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
				}
				sbQuery.append(") and pro_folder_id = 0 and (file_size is null or file_size ='') ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)){
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
				} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 14-10-2022===		
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 14-10-2022===		
				} else if(strUserType != null && strUserType.equals(CUSTOMER)){
					sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
				} 
				sbQuery.append(" order by pro_document_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getPro_id()));
				pst.setInt(2, uF.parseToInt(getPro_id()));
//				System.out.println("pst folder ====>>>>> " + pst);
				
				rs = pst.executeQuery();
				while (rs.next()) {
	//				List<Map<String, String>> alFolder = (List<Map<String, String>>) hmFolder.get(rs.getString("pro_document_id")); 
	//				if(alFolder == null) alFolder = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInner.put("PRO_ID", rs.getString("pro_id"));
					hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInner.put("CLIENT_ID", rs.getString("client_id"));
					hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					
					hmInner.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
					hmInner.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
					
					if(uF.parseToInt(rs.getString("project_category")) == 1){
						hmInner.put("CATEGORY", "Project");
						String strAlign = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
						} else {
							strAlign = "Full Project";
						}
						hmInner.put("ALIGN", uF.showData(strAlign, "-"));
					} else if(uF.parseToInt(rs.getString("project_category")) == 2){
						hmInner.put("CATEGORY", "Category");
						String strOther = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"))+", Category";
						}
						hmInner.put("ALIGN", uF.showData(strOther, "-"));
					} else {
						hmInner.put("CATEGORY", "-");
						hmInner.put("ALIGN", "-");
					}
					hmInner.put("DOC_VERSION", rs.getString("doc_version"));
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					alProFolder.add(hmInner);
	//				hmFolder.put(rs.getString("pro_document_id"), alProFolder);
				}
				rs.close();
				pst.close(); 
//			}
//			System.out.println("hmFolder ====>>>>> " + hmFolder);
			request.setAttribute("alProFolder", alProFolder); 
			
			List<Map<String, String>> alMainDoc = new ArrayList<Map<String,String>>();
//			if(uF.parseToInt(getPro_id()) > 0) {
				sbQuery = new StringBuilder();
				if(getFromPage() != null && getFromPage().equals("MyProject")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details where " +
						" ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='')) " +
						" and (sharing_type =0 or sharing_type = 1 or sharing_resources like '%,"+strSessionEmpId+",%') and doc_parent_id = 0 ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
								"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
				//===start parvez date: 14-10-2022===		
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
								"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
								"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//								"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
								"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
				//===end parvez date: 14-10-2022===		
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(getPro_id()));
						pst.setInt(2, uF.parseToInt(getPro_id()));
						pst.setInt(3, uF.parseToInt(getPro_id()));
						pst.setInt(4, uF.parseToInt(getPro_id()));
				} else {
					sbQuery = new StringBuilder();
					sbQuery.append("select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details where " +
						" ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='')) and doc_parent_id = 0 ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
								"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
					//===start parvez date: 14-10-2022===	
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
								"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
								"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//								"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
								"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
					//===end parvez date: 14-10-2022===	
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setInt(3, uF.parseToInt(getPro_id()));
					pst.setInt(4, uF.parseToInt(getPro_id()));
				}
//				System.out.println("pst files ====>>>>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
	//				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmDoc.get(rs.getString("pro_document_id")); 
	//				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInnerDoc = new HashMap<String, String>();
					hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
					hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
					hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					
					hmInnerDoc.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
					hmInnerDoc.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
					
					if(uF.parseToInt(rs.getString("project_category")) == 1){
						hmInnerDoc.put("CATEGORY", "Project");
						String strAlign = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
						} else {
							strAlign = "Full Project";
						}
						hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-"));
					} else if(uF.parseToInt(rs.getString("project_category")) == 2){
						hmInnerDoc.put("CATEGORY", "Category");
						String strOther = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"))+", Category";
						}
						hmInnerDoc.put("ALIGN", uF.showData(strOther, "-"));
					} else {
						hmInnerDoc.put("CATEGORY", "-");
						hmInnerDoc.put("ALIGN", "-");
					}
					hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					
					String extenstion = null;
					if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
					}
					hmInnerDoc.put("FILE_EXTENSION", extenstion);
					
					alMainDoc.add(hmInnerDoc);
	//				hmDoc.put(rs.getString("pro_document_id"), alDoc);
				}
				rs.close();
				pst.close(); 
	//			System.out.println("1 hmDoc ====>>>>> " + hmDoc);
				
				if(getFromPage() != null && getFromPage().equals("MyProject")) {
					sbQuery = new StringBuilder();
//					((align_with=0 and project_category=1 and pro_id>0 and pro_id=?) " +
//					"or (align_with=0 and project_category=2 and pro_id>0 and pro_id=?) ");
//				if(uF.parseToInt(getStrTaskId()) > 0) {
//					sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
//				}
//				sbQuery.append(") and pro_folder_id = 0 and (file_size is null or file_size ='') ");
				
					sbQuery.append("select * from (select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details " +
						" where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='')) and (sharing_type =0 or sharing_type = 1 " +
						" or sharing_resources like '%,"+strSessionEmpId+",%') and doc_parent_id > 0) a, (select max(pro_document_id) as pro_document_id from project_document_details where " +
						" ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='') and (sharing_type =0 or sharing_type = 1 or " +
						" sharing_resources like '%,"+strSessionEmpId+",%') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((a.sharing_type = 2 and a.sharing_resources like '%,"+strSessionEmpId+",%') or a.sharing_type = 0 " +
								"or (a.sharing_type = 1 and a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
					//===start parvez date: 14-10-2022===	
						sbQuery.append(" and ((a.sharing_type = 2 and a.sharing_resources like '%,"+strSessionEmpId+",%') or a.sharing_type = 0 " +
								"or (a.sharing_type = 1 and (a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
								"or a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
								"or a.pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
					//===end parvez date: 14-10-2022===	
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and a.sharing_poc like '%,"+strSessionEmpId+",%'");
					}				
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setInt(3, uF.parseToInt(getPro_id()));
					pst.setInt(4, uF.parseToInt(getPro_id()));
					pst.setInt(5, uF.parseToInt(getPro_id()));
					pst.setInt(6, uF.parseToInt(getPro_id()));
				} else {
					sbQuery = new StringBuilder();
					sbQuery.append("select * from (select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details " +
						"where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='')) and doc_parent_id > 0) a," +
						" (select max(pro_document_id) as pro_document_id from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id = 0 and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((a.sharing_type = 2 and a.sharing_resources like '%,"+strSessionEmpId+",%') or a.sharing_type = 0 " +
								"or (a.sharing_type = 1 and a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
					//===start parvez date: 14-10-2022===	
						sbQuery.append(" and ((a.sharing_type = 2 and a.sharing_resources like '%,"+strSessionEmpId+",%') or a.sharing_type = 0 " +
								"or (a.sharing_type = 1 and (a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
								"or a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//								"or a.pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
								"or a.pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
					//===end parvez date: 14-10-2022===	
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and a.sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setInt(3, uF.parseToInt(getPro_id()));
					pst.setInt(4, uF.parseToInt(getPro_id()));
					pst.setInt(5, uF.parseToInt(getPro_id()));
					pst.setInt(6, uF.parseToInt(getPro_id()));
				}
	//			System.out.println("pst ====>>>>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
	//				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmDoc.get(rs.getString("pro_document_id")); 
	//				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInnerDoc = new HashMap<String, String>();
					hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
					hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
					hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					
					hmInnerDoc.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
					hmInnerDoc.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
					
					if(uF.parseToInt(rs.getString("project_category")) == 1){
						hmInnerDoc.put("CATEGORY", "Project");
						String strAlign = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
						} else {
							strAlign = "Full Project";
						}
						hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-"));
					} else if(uF.parseToInt(rs.getString("project_category")) == 2){
						hmInnerDoc.put("CATEGORY", "Category");
						String strOther = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"))+", Category";
						}
						hmInnerDoc.put("ALIGN", uF.showData(strOther, "-"));
					} else {
						hmInnerDoc.put("CATEGORY", "-");
						hmInnerDoc.put("ALIGN", "-");
					}
					hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					
					String extenstion = null;
					if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
					}
					hmInnerDoc.put("FILE_EXTENSION", extenstion);
					
					alMainDoc.add(hmInnerDoc);
	//				hmDoc.put(rs.getString("pro_document_id"), alDoc);
				}
				rs.close();
				pst.close();
//			}
//			System.out.println("2 hmDoc ====>>>>> " + hmDoc);
			request.setAttribute("alMainDoc", alMainDoc);
//			request.setAttribute("hmDoc", hmDoc);
			
			
			Map<String, List<Map<String,String>>> hmSubFolder = new HashMap<String, List<Map<String,String>>>();
			Map<String, List<Map<String,String>>> hmSubDoc = new HashMap<String, List<Map<String,String>>>();
//			if(uF.parseToInt(getPro_id()) > 0) {
				sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
					"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
				if(uF.parseToInt(getStrTaskId()) > 0) {
					sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
				}
				sbQuery.append(") and pro_folder_id > 0 and (file_size is null or file_size ='') ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)) {
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
				} else if(strUserType != null && strUserType.equals(MANAGER)) {
				//===start parvez date: 14-10-2022===	
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
				//===end parvez date: 14-10-2022===	
				} else if(strUserType != null && strUserType.equals(CUSTOMER)) {
					sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
				} 
				sbQuery.append(" order by pro_document_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getPro_id()));
				pst.setInt(2, uF.parseToInt(getPro_id()));
	//			System.out.println("pst ====>>>>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<Map<String, String>> alFolder = (List<Map<String, String>>) hmSubFolder.get(rs.getString("pro_folder_id")); 
					if(alFolder == null) alFolder = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInner.put("PRO_ID", rs.getString("pro_id"));
					hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInner.put("CLIENT_ID", rs.getString("client_id"));
					hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					
					hmInner.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
					hmInner.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
					
					if(uF.parseToInt(rs.getString("project_category")) == 1){
						hmInner.put("CATEGORY", "Project");
						String strAlign = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
						} else {
							strAlign = "Full Project";
						}
						hmInner.put("ALIGN", uF.showData(strAlign, "-"));
					} else if(uF.parseToInt(rs.getString("project_category")) == 2){
						hmInner.put("CATEGORY", "Category");
						String strOther = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"))+", Category";
						}
						hmInner.put("ALIGN", uF.showData(strOther, "-"));
					} else {
						hmInner.put("CATEGORY", "-");
						hmInner.put("ALIGN", "-");
					}
					hmInner.put("DOC_VERSION", rs.getString("doc_version"));
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					alFolder.add(hmInner);
					hmSubFolder.put(rs.getString("pro_folder_id"), alFolder);
				}
				rs.close();
				pst.close();
//			} 
//			System.out.println("hmSubFolder ====>>>>> " + hmSubFolder);
			request.setAttribute("hmSubFolder", hmSubFolder);
			
			
//			if(uF.parseToInt(getPro_id()) > 0) {
				sbQuery = new StringBuilder();
				if(getFromPage() != null && getFromPage().equals("MyProject")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details where " +
						" ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='')) and (sharing_type =0 or sharing_type = 1 or " +
						"sharing_resources like '%,"+strSessionEmpId+",%') and doc_parent_id = 0 ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
					//===start parvez date: 14-10-2022===	
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
					//===end parvez date: 14-10-2022===	
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					sbQuery.append(" order by pro_document_id");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setInt(3, uF.parseToInt(getPro_id()));
					pst.setInt(4, uF.parseToInt(getPro_id()));
				} else {
					sbQuery = new StringBuilder();
					sbQuery.append("select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details where " +
						"((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='')) and doc_parent_id = 0 ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
					//===start parvez date: 14-10-2022===	
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
					//===end parvez date: 14-10-2022===	
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					sbQuery.append(" order by pro_document_id");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setInt(3, uF.parseToInt(getPro_id()));
					pst.setInt(4, uF.parseToInt(getPro_id()));
				}
	//			System.out.println("pst ====>>>>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
					if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInnerDoc = new HashMap<String, String>();
					hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
					hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
					hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					
					hmInnerDoc.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
					hmInnerDoc.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
					
					if(uF.parseToInt(rs.getString("project_category")) == 1){
						hmInnerDoc.put("CATEGORY", "Project");
						String strAlign = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
						} else {
							strAlign = "Full Project";
						}
						hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-"));
					} else if(uF.parseToInt(rs.getString("project_category")) == 2){
						hmInnerDoc.put("CATEGORY", "Category");
						String strOther = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"))+", Category";
						}
						hmInnerDoc.put("ALIGN", uF.showData(strOther, "-"));
					} else {
						hmInnerDoc.put("CATEGORY", "-");
						hmInnerDoc.put("ALIGN", "-");
					}
					hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					
					String extenstion = null;
					if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
					}
					hmInnerDoc.put("FILE_EXTENSION", extenstion);
					
					alDoc.add(hmInnerDoc);
					hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
				}
				rs.close();
				pst.close();
//			System.out.println("1 hmSubDoc ====>>>>> " + hmSubDoc);
			
				if(getFromPage() != null && getFromPage().equals("MyProject")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select * from (select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details " +
						" where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='')) and (sharing_type =0 or " +
						" sharing_type = 1 or sharing_resources like '%,"+strSessionEmpId+",%') and doc_parent_id > 0 ) a, (select max(pro_document_id) as pro_document_id from " +
						" project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='') and (sharing_type =0 or sharing_type = 1 or " +
						" sharing_resources like '%,"+strSessionEmpId+",%') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((a.sharing_type = 2 and a.sharing_resources like '%,"+strSessionEmpId+",%') or a.sharing_type = 0 " +
								"or (a.sharing_type = 1 and a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
					//===start parvez date: 14-10-2022===	
						sbQuery.append(" and ((a.sharing_type = 2 and a.sharing_resources like '%,"+strSessionEmpId+",%') or a.sharing_type = 0 " +
								"or (a.sharing_type = 1 and (a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
								"or a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//								"or a.pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
								"or a.pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
					//===end parvez date: 14-10-2022===	
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and a.sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setInt(3, uF.parseToInt(getPro_id()));
					pst.setInt(4, uF.parseToInt(getPro_id()));
					pst.setInt(5, uF.parseToInt(getPro_id()));
					pst.setInt(6, uF.parseToInt(getPro_id()));
				} else {
					sbQuery = new StringBuilder();
					sbQuery.append("select * from (select * from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details " +
						"where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='')) and doc_parent_id > 0 ) a," +
						" (select max(pro_document_id) as pro_document_id from project_document_details where ((align_with>=0 and project_category=1 and pro_id>0 and pro_id=?) " +
						"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) ");
					if(uF.parseToInt(getStrTaskId()) > 0) {
						sbQuery.append(" or (align_with = "+uF.parseToInt(getStrTaskId())+" and project_category=1 and pro_id="+uF.parseToInt(getPro_id())+")");
					}
					sbQuery.append(") and pro_folder_id > 0 and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((a.sharing_type = 2 and a.sharing_resources like '%,"+strSessionEmpId+",%') or a.sharing_type = 0 " +
								"or (a.sharing_type = 1 and a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
				//===start parvez date: 14-10-2022===		
						sbQuery.append(" and ((a.sharing_type = 2 and a.sharing_resources like '%,"+strSessionEmpId+",%') or a.sharing_type = 0 " +
								"or (a.sharing_type = 1 and (a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
								"or a.pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//								"or a.pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
								"or a.pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
				//===end parvez date: 14-10-2022===		
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and a.sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setInt(3, uF.parseToInt(getPro_id()));
					pst.setInt(4, uF.parseToInt(getPro_id()));
					pst.setInt(5, uF.parseToInt(getPro_id()));
					pst.setInt(6, uF.parseToInt(getPro_id()));
				}
	//			System.out.println("pst ====>>>>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
					if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInnerDoc = new HashMap<String, String>();
					hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
					hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
					hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					
					hmInnerDoc.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
					hmInnerDoc.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
					
					if(uF.parseToInt(rs.getString("project_category")) == 1){
						hmInnerDoc.put("CATEGORY", "Project");
						String strAlign = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
						} else {
							strAlign = "Full Project";
						}
						hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-"));
					} else if(uF.parseToInt(rs.getString("project_category")) == 2){
						hmInnerDoc.put("CATEGORY", "Category");
						String strOther = null;
						if(uF.parseToInt(rs.getString("align_with")) > 0) {
							strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"))+", Category";
						}
						hmInnerDoc.put("ALIGN", uF.showData(strOther, "-"));
					} else {
						hmInnerDoc.put("CATEGORY", "-");
						hmInnerDoc.put("ALIGN", "-");
					}
					hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					
					String extenstion = null;
					if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
					}
					hmInnerDoc.put("FILE_EXTENSION", extenstion);				
					
					alDoc.add(hmInnerDoc);
					hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
				}
				rs.close();
				pst.close();
//			}
//			System.out.println("2 hmSubDoc ====>>>>> " + hmSubDoc);
			request.setAttribute("hmSubDoc", hmSubDoc);
			
			
//			StringBuilder sbProTasks = new StringBuilder("<select name=\"proTasks\" id=\"proTasks\" style=\"width:100px\"><option value=\"0\">Full Project</option>");
			StringBuilder sbProTasks = new StringBuilder("<option value=\"0\">Full Project</option>");
			pst = con.prepareStatement("select task_id,activity_name,parent_task_id from activity_info where pro_id=? and task_id not in " +
			"(select parent_task_id from activity_info where pro_id=? and parent_task_id is not null) and task_accept_status != -1");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setInt(2, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(uF.parseToInt(rs.getString("parent_task_id")) > 0) {
					if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
						String activityName = rs.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						sbProTasks.append("<option value=\"" + rs.getString("task_id") + "\">" + sbTaskName.toString().trim() + " [ST]"+"</option>");
					}
				} else {
					if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
						String activityName = rs.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						sbProTasks.append("<option value=\"" + rs.getString("task_id") + "\">" + sbTaskName.toString().trim() +"</option>");
					}
				}
			}
			rs.close();
			pst.close();
//			sbProTasks.append("</select>");
			request.setAttribute("sbProTasks", sbProTasks.toString());
			
			
//			StringBuilder sbProEmp = new StringBuilder("<select name=\"proEmployee\" id=\"proEmployee\" style=\"width:100px\" multiple size=\"3\"><option >Select Resource</option>");
			StringBuilder sbProEmp = new StringBuilder();
			pst = con.prepareStatement("select * from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and is_alive = true and pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				sbProEmp.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname") + "</option>");
			}
			rs.close();
			pst.close();
//			sbProEmp.append("</select>");
			request.setAttribute("sbProEmp", sbProEmp.toString());
			
//			StringBuilder sbProCategory = new StringBuilder("<select name=\"proTasks\" id=\"proTasks\" style=\"width:100px\"><option >Select Category</option>");
			StringBuilder sbProCategory = new StringBuilder();
			pst = con.prepareStatement("select * from project_category_details where org_id=? and project_category_id>1 order by project_category");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbProCategory.append("<option value=\"" + rs.getString("project_category_id") + "\">" + rs.getString("project_category").trim()+ "</option>");
			}
			rs.close();
			pst.close();
//			sbProEmp.append("</select>");
			request.setAttribute("sbProCategory", sbProCategory.toString());
//			System.out.println("sbProCategory======>"+sbProCategory.toString());
			
			StringBuilder sbProSPOC = new StringBuilder();
			pst = con.prepareStatement("select poc_id, contact_fname, contact_lname from client_poc where poc_id in (select poc from projectmntnc " +
					"where org_id=? and client_id in (select client_id from projectmntnc where pro_id=?)) order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbProSPOC.append("<option value=\"" + rs.getString("poc_id") + "\">" + uF.showData(rs.getString("contact_fname"), "").trim()+" "+uF.showData(rs.getString("contact_lname"), "").trim()+ "</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbProSPOC", sbProSPOC.toString());
//			System.out.println("sbProSPOC======>"+sbProSPOC.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void getProjectInfoStep1(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, Map<String, String>> hmCurrData = CF.getCurrencyDetailsForPDF(con);
//			System.out.println("hmCurrData ===>> " + hmCurrData);
			
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst============"+pst);
			rs = pst.executeQuery();
			String currSign = "";
			while (rs.next()) {
				setPro_id(rs.getString("pro_id"));
				
				setPrjectname(rs.getString("pro_name"));
				setPriority(rs.getString("priority"));
				setDescription(rs.getString("description"));
				setShortDescription(rs.getString("short_description"));
				setService(rs.getString("service"));
				setDeadline(rs.getString("deadline") != null ? uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT) : "");
				setStrClient(rs.getString("client_id"));
				if(uF.parseToInt(getClientID()) > 0) {
					setStrClient(getClientID());
				}
				setPrjectCode(rs.getString("project_code"));
				setClientPoc(rs.getString("poc"));
				setStrClientBrand(rs.getString("client_brand_id"));
//				System.out.println("getStrClientBrand ===>> "+ getStrClientBrand());
				setStartDate(rs.getString("start_date") != null ? uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT) : "");
				setStrCurrency(rs.getString("curr_id"));
				Map<String, String> hmCurr = hmCurrData.get(rs.getString("curr_id"));
				if(hmCurr == null) hmCurr = new HashMap<String, String>();
				currSign = hmCurr.get("SHORT_CURR");
				
				setStrBillingCurrency(rs.getString("billing_curr_id"));
				setWeekdayCycle(rs.getString("billing_cycle_weekday"));
				setDayCycle(rs.getString("billing_cycle_day"));
				
				StringBuilder sbLevel = new StringBuilder();
				String []arrLevel = null;
				if(rs.getString("level_id")!=null) {
					arrLevel = rs.getString("level_id").split(",");
				} 
				setStrBillingKindName(CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
				setStrDepartment(rs.getString("department_id"));
				setStrBillingKind(rs.getString("billing_kind"));
				
				setLevel(arrLevel);
				setLocation(rs.getString("wlocation_id"));
				setStrSBU(rs.getString("sbu_id"));
				setOrganisation(rs.getString("org_id"));
				/*setF_service(null);
				setF_wLocation(null);
				setF_level(null);*/
				
				setBillingType(rs.getString("billing_type"));
				if (billingType != null && (billingType.equals("H") || billingType.equals("D") || billingType.equals("M"))) 
					setBillingAmountH(rs.getString("billing_amount"));
				else
					setBillingAmountF(rs.getString("billing_amount"));
				setEstimatedHours(rs.getString("idealtime"));
				setStrActualBilling(rs.getString("actual_calculation_type"));
			
			//===start parvez date: 11-10-2022===	
//				setStrProjectOwner(rs.getString("project_owner"));
				if(rs.getString("project_owners")!=null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					List<String> ownersList1 = new ArrayList<String>();
					for(int j=0; j<tempList.size();j++){
						if(j>0){
							ownersList1.add(tempList.get(j));
						}
					}
//					System.out.println("PANP1/5127---project_owners=="+ownersList1);
					setStrProjectOwner(ownersList1);
				}
			//===end parvez date: 11-10-2022===
				
//				System.out.println("project owner"+getStrProjectOwner());
				setHoursToDay(rs.getString("bill_days_type"));
				setHoursForDay(uF.parseToDouble(rs.getString("hours_for_bill_day")) > 0 ? rs.getString("hours_for_bill_day") : "");
				setStrReferanceBy(rs.getString("reference_by_id"));
				
				setStrPortfolioManager(rs.getString("portfolio_manager"));
				setStrAccountManager(rs.getString("account_manager"));
				setStrDeliveryManager(rs.getString("delivery_manager"));
				//setStrRelationShipBy(rs.getString("releation_by_id"));
				
				
			//===start parvez date: 22-11-2022===
				if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION))){
					setUstPrjectId(rs.getString("ust_project_code"));
					setProjectAccountId(rs.getString("project_acc_code"));
					setProjectSegment(rs.getString("segment"));
					setProDomain(rs.getString("pro_domain_id"));
				}
				if(getProType()!=null && getProType().equals("P")){
					setRevenueTarget(rs.getString("revenue_target"));
				}
				
			//===end parvez date: 22-11-2022===	
				
			}
			rs.close();
			pst.close();
			request.setAttribute("currSign", currSign);
			
//			clientPocList = new FillClientPoc(request).fillClientPoc(getStrClient());
			if(uF.parseToInt(getStrClientBrand())>0) {
				clientPocList = new FillClientPoc(request).fillClientBrandPoc(getStrClientBrand());
			} else {
				clientPocList = new FillClientPoc(request).fillClientPoc(getStrClient());
			}
			clientBrandList = new FillClientBrand(request).fillClientBrands(uF.parseToInt(getStrClient()));

			Map<String, List<List<String>>> hmFolderDocs = new HashMap<String, List<List<String>>>();
			List<List<String>> listDocs = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from project_document_details where pro_id=? and pro_folder_id > 0 order by pro_document_id");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				listDocs = hmFolderDocs.get(rs.getString("pro_folder_id"));
				if(listDocs == null) listDocs = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_document_id"));
				innerList.add(rs.getString("document_name"));
				innerList.add(CF.getProjectNameById(con, rs.getString("pro_id")));
				innerList.add(rs.getString("pro_id"));
				innerList.add(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("folder_file_type"));
				innerList.add(rs.getString("client_id"));
				innerList.add(uF.showData(rs.getString("file_size"), "-"));
				innerList.add(uF.showData(rs.getString("file_type"), "-"));
				listDocs.add(innerList);
				hmFolderDocs.put(rs.getString("pro_folder_id"), listDocs);
			}
			rs.close();
			pst.close();
//			System.out.println("hmFolderDocs ====>>>>> " + hmFolderDocs);
			request.setAttribute("hmFolderDocs", hmFolderDocs);
			
			
			Map<String, List<String>> hmFolderDocsData = new HashMap<String, List<String>>();
//			List<List<String>> listFoldersDocs = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from project_document_details where pro_id=? and pro_folder_id = 0 order by pro_document_id desc");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
//				listFoldersDocs = hmFolderDocsData.get(rs.getString("folder_name"));
//				List<List<String>> listFoldersDocs = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_document_id"));
				innerList.add(rs.getString("folder_name"));
				innerList.add(rs.getString("document_name"));
				innerList.add(rs.getString("pro_id"));
				innerList.add(CF.getProjectNameById(con, rs.getString("pro_id")));
				innerList.add(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("folder_file_type"));
				innerList.add(rs.getString("client_id"));
				innerList.add(uF.showData(rs.getString("file_size"), "-"));
				innerList.add(uF.showData(rs.getString("file_type"), "-"));
//				listFoldersDocs.add(innerList);
				hmFolderDocsData.put(rs.getString("pro_document_id"), innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmFolderDocsData ===>>> " + hmFolderDocsData);
			request.setAttribute("hmFolderDocsData", hmFolderDocsData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void updateProjectInfoStep1(UtilityFunctions uF) {


		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null,pst1=null;
		ResultSet rs = null,rs1=null;
//		System.out.println("operation ==>>>> " + getOperation());
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			List<String> innerList = new ArrayList<String>();
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from projectmntnc where pro_id=?");
			pst1 = con.prepareStatement(strQuery.toString());
			pst1.setInt(1, uF.parseToInt(getPro_id()));
//		/	System.out.println("\npst1 in new project====>"+pst1);
			rs1 = pst1.executeQuery();
			while(rs1.next()){
				innerList.add(rs1.getString("start_date") != null ? uF.getDateFormat(rs1.getString("start_date"), DBDATE, DATE_FORMAT) : "");
				innerList.add(rs1.getString("deadline") != null ? uF.getDateFormat(rs1.getString("deadline"), DBDATE, DATE_FORMAT) : "");
				innerList.add(rs1.getString("billing_kind"));
				innerList.add(rs1.getString("added_by"));
				innerList.add(rs1.getString("billing_cycle_day")); 
				innerList.add(rs1.getString("billing_cycle_weekday"));
			}
			
			rs1.close();
			pst1.close();
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 11-10-2022===	
			/*sbQuery.append("update projectmntnc set pro_name=?,priority=?,description=?,activity=?,service=?,taskstatus=?,deadline=?," +
				"timestatus=?,client_id=?,start_date=?,added_by=?,level_id=?,wlocation_id=?,department_id=?,project_owner=?,org_id=?,"
				+ "short_description=?,sbu_id=?");*/
			sbQuery.append("update projectmntnc set pro_name=?,priority=?,description=?,activity=?,service=?,taskstatus=?,deadline=?," +
					"timestatus=?,client_id=?,start_date=?,added_by=?,level_id=?,wlocation_id=?,department_id=?,project_owners=?,org_id=?,"
					+ "short_description=?,sbu_id=?");
		//===end parvez date: 11-10-2022===	
			if(uF.parseToInt(getClientPoc())>0) {
				sbQuery.append(",poc="+uF.parseToInt(getClientPoc()));
			}
			if(uF.parseToInt(getStrReferanceBy())>0) {
				sbQuery.append(",reference_by_id="+uF.parseToInt(getStrReferanceBy()));
			}
			if(uF.parseToInt(getStrClientBrand())>0) {
				sbQuery.append(",client_brand_id="+uF.parseToInt(getStrClientBrand()));
			}
			
	//===start parvez date: 22-11-2022===
			if(uF.parseToInt(getStrPortfolioManager())>0) {
				sbQuery.append(",portfolio_manager="+uF.parseToInt(getStrPortfolioManager()));
			}
			
			if(uF.parseToInt(getStrAccountManager())>0) {
				sbQuery.append(",account_manager="+uF.parseToInt(getStrAccountManager()));
			}
			
			if(uF.parseToInt(getStrDeliveryManager())>0) {
				sbQuery.append(",delivery_manager="+uF.parseToInt(getStrDeliveryManager()));
			}
			
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION))){
				sbQuery.append(", ust_project_code='"+getUstPrjectId()+"'");
				sbQuery.append(", project_acc_code='"+getProjectAccountId()+"'");
				sbQuery.append(", segment='"+getProjectSegment()+"'");
				sbQuery.append(", pro_domain_id="+uF.parseToInt(getProDomain()));
			}
	//===end parvez date: 22-11-2022===		
			
			if(uF.parseToInt(strProOwnerOrTL) != 2 && uF.parseToInt(getPro_id()) > 0) {
				sbQuery.append(",idealtime=?,billing_type=?,billing_amount=?,actual_calculation_type=?,curr_id=?,billing_curr_id=?,billing_kind=?, " +
					"bill_days_type=?,hours_for_bill_day=?,billing_cycle_weekday=?,billing_cycle_day=? ");
				if(getProType()!=null && getProType().equals("P")){
					sbQuery.append(",revenue_target="+uF.parseToDouble(getRevenueTarget())+" ");
				}
			}
			sbQuery.append(" where pro_id=?");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getPrjectname());
			pst.setInt(2, uF.parseToInt(getPriority()));
			pst.setString(3, getDescription());
			pst.setString(4, getPrjectname());
			pst.setInt(5, uF.parseToInt(getService()));
			pst.setString(6, "New Task");
			pst.setDate(7, uF.getDateFormat(getDeadline(), DATE_FORMAT));
			pst.setString(8, "n");
			pst.setInt(9, uF.parseToInt(getStrClient()));
//			pst.setString(11, getPrjectCode());
//			pst.setInt(10, uF.parseToInt(getClientPoc()));
			pst.setDate(10, uF.getDateFormat(getStartDate(), DATE_FORMAT));
			pst.setInt(11, uF.parseToInt((String) session.getAttribute(EMPID)));

			StringBuilder sbLevel = new StringBuilder();
			for(int i=0; level!=null && i<level.length; i++) {
				sbLevel.append(level[i]+",");
			}
			pst.setString(12, sbLevel.toString());
			pst.setInt(13, uF.parseToInt(location));
			pst.setInt(14, uF.parseToInt(getStrDepartment()));
	//===start parvez date: 11-10-2022===		
//			pst.setInt(15, uF.parseToInt(getStrProjectOwner()));
			pst.setString(15, getData(getStrProjectOwner()));
	//===end parvez date: 11-10-2022===		
			pst.setInt(16, uF.parseToInt(getOrganisation()));
			pst.setString(17, getShortDescription());
			pst.setInt(18, uF.parseToInt(getStrSBU()));
//			pst.setInt(20, uF.parseToInt(getStrReferanceBy()));
//			pst.setInt(21, uF.parseToInt(getStrClientBrand()));
			if(uF.parseToInt(strProOwnerOrTL) != 2 && uF.parseToInt(getPro_id()) > 0) {
				pst.setInt(19, uF.parseToInt(estimatedHours));
				pst.setString(20, billingType); 
				if (billingType != null && (billingType.equals("H") || billingType.equals("D") || billingType.equals("M"))) {
					pst.setDouble(21, uF.parseToDouble(billingAmountH));
				} else {
					pst.setDouble(21, uF.parseToDouble(billingAmountF));
				}
				if(billingType!=null && billingType.equalsIgnoreCase("F")) {
					pst.setString(22, getStrActualBilling());
				} else {
					pst.setString(22, billingType);
				}
				pst.setInt(23, uF.parseToInt(getStrCurrency()));
				pst.setInt(24, uF.parseToInt(getStrBillingCurrency()));
				pst.setString(25, getStrBillingKind());
				pst.setInt(26, uF.parseToInt(getHoursToDay())>0 ? uF.parseToInt(getHoursToDay()) :1);
				pst.setDouble(27, uF.parseToDouble(getHoursForDay()));
				pst.setString(28, getWeekdayCycle());
				pst.setInt(29, uF.parseToInt(getDayCycle()));
				pst.setInt(30, uF.parseToInt(getPro_id()));
			} else {
				pst.setInt(19, uF.parseToInt(getPro_id()));
			}
//			System.out.println("pst==>"+pst);
			pst.executeUpdate();
			pst.close();
			
		if(billingType != null) {
			boolean frqFlag = false;
			String freqEndDate = getDeadline();
		
			
			if(uF.parseToInt(getDayCycle()) > 0) {
				freqEndDate = getDayCycle() + "/" + uF.getDateFormat(getStartDate(), DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(getStartDate(), DATE_FORMAT, "yyyy");
				
				Date stDate = uF.getDateFormatUtil(getStartDate(), DATE_FORMAT);
				Date endDate = uF.getDateFormatUtil(getDeadline(), DATE_FORMAT);
				Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
				
				if(freqDate.after(stDate)) {
					frqFlag = true;
				}
				if(frqFlag) {
					freqEndDate = freqEndDate;
				} else if(getStrBillingKind() != null && getStrBillingKind().equals("M")) {
					freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
				} else if(getStrBillingKind() != null && getStrBillingKind().equals("B")) {
					freqEndDate = uF.getDateFormat(uF.getFutureDate(getStartDate(), 15)+"", DBDATE, DATE_FORMAT);
				}
//				System.out.println("freqEndDate ====> " + freqEndDate);
				Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
				
				if(newFreqDate.after(endDate)) {
					freqEndDate = getDeadline();
				}
			}
			if(getWeekdayCycle() != null && !getWeekdayCycle().equals("") && getStrBillingKind() != null && getStrBillingKind().equals("W")) {
				freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(getStartDate(), getWeekdayCycle())+"", DBDATE, DATE_FORMAT);
				
				Date endDate = uF.getDateFormatUtil(getDeadline(), DATE_FORMAT);
				Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
				
				if(newFreqDate.after(endDate)) {
					freqEndDate = getDeadline();
				}
			}
			
			String proFreqName = "";
			if(getStrBillingKind() != null && getStrBillingKind().equals("M")) {
				String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
				String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
				String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
				proFreqName = freqYear +" "+strMonth;
			} else if(getStrBillingKind() != null && getStrBillingKind().equals("B")) {
				String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
				String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
				String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
				String freqDate = uF.getDateFormat(freqEndDate, DATE_FORMAT, "dd");
				String strHalf = "- First";
				if(uF.parseToInt(freqDate) > 15) {
					strHalf = "- Second";
				}
				proFreqName = freqYear +" "+strMonth+" " + strHalf;
			} else if(getStrBillingKind() != null && getStrBillingKind().equals("W")) {
				String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
				String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
				String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
				String strWeekName = uF.getWeekOfMonthOnPassedDate(freqEndDate);
				proFreqName = freqYear +" "+strMonth+" Week-" +strWeekName ;
			}
		
				int proCnt = 0;
				pst = con.prepareStatement("select count(pro_id) as count from projectmntnc_frequency where pro_id=? ");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				while (rs.next()) {
					proCnt = rs.getInt("count");
				}
				rs.close();
				pst.close();
				
				ProjectScheduler scheduler = new ProjectScheduler(request, session, CF, uF, strSessionEmpId);
				if(proCnt == 0) {
					pst = con.prepareStatement("insert into projectmntnc_frequency (pro_id, pro_start_date, pro_end_date, freq_start_date, " +
					"freq_end_date, added_by, entry_date, pro_freq_name) values (?,?,?,?, ?,?,?,?)"); 
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setDate(2, uF.getDateFormat(getStartDate(), DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(getDeadline(), DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(getStartDate(), DATE_FORMAT));
					if(frqFlag || (getStrBillingKind() != null && getStrBillingKind().equals("O"))) {
						pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
					} else {
						pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
					}
					pst.setInt(6, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setString(8, proFreqName);
	//				System.out.println("pst ====> " + pst);
					pst.execute();
					pst.close();
				
				} else if(proCnt > 0)  {
					
					//System.out.println("StrBillingKind()==>"+getStrBillingKind());
					if(!innerList.get(2).equals(getStrBillingKind())){
						scheduler.updateProjectDetails(getPro_id());
					}
					if(innerList.get(2).equals("W") && getStrBillingKind().equals("W")){
						if(innerList.get(5).equals(getWeekdayCycle())==false){
							scheduler.updateProjectDetails(getPro_id());
						}
					}
					if(innerList.get(2).equals("B") && getStrBillingKind().equals("B")){
						if(innerList.get(4).equals(getDayCycle())==false){
							scheduler.updateProjectDetails(getPro_id());
						}
					} 
						
					if(innerList.get(2).equals("M") && getStrBillingKind().equals("M")){
						if(innerList.get(4).equals(getDayCycle())==false){
							scheduler.updateProjectDetails(getPro_id());
						}
					}
					if(!innerList.get(0).equals(getStartDate())){ 
						scheduler.updateProjectDetails(getPro_id());
					}
					if(!innerList.get(1).equals(getDeadline())){
						scheduler.updateProjectDetails(getPro_id());
					}
				}
			}
//			createFolderForDocs(uF);
			if (billingType != null && (billingType.equals("H") || billingType.equals("D") || billingType.equals("M"))) {
				deleteMilestones(con, uF, getPro_id());
			}
			
			boolean updateFlag = false;
	//===start parvez date: 11-10-2022===		
			/*pst = con.prepareStatement("select pro_name,priority,description,activity,service,taskstatus,deadline,idealtime,timestatus,client_id,poc,start_date,added_by," +
				"level_id,wlocation_id,billing_type,billing_amount,actual_calculation_type,project_owner,curr_id,sbu_id,org_id,short_description,billing_curr_id,billing_kind, " +
				"bill_days_type,hours_for_bill_day,department_id,billing_cycle_weekday,billing_cycle_day,client_brand_id from projectmntnc where pro_id=?");*/
			pst = con.prepareStatement("select pro_name,priority,description,activity,service,taskstatus,deadline,idealtime,timestatus,client_id,poc,start_date,added_by," +
					"level_id,wlocation_id,billing_type,billing_amount,actual_calculation_type,project_owner,curr_id,sbu_id,org_id,short_description,billing_curr_id,billing_kind, " +
					"bill_days_type,hours_for_bill_day,department_id,billing_cycle_weekday,billing_cycle_day,client_brand_id,project_owners from projectmntnc where pro_id=?");
	//===end parvez date: 11-10-2022===		
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while(rs.next()){
				if(getPrjectname()!=null && rs.getString("pro_name")!=null && !getPrjectname().trim().equalsIgnoreCase(rs.getString("pro_name").trim())){
					updateFlag = true;
				} else if((getPrjectname()==null || getPrjectname().trim().equals("")) && (rs.getString("pro_name")!=null && !rs.getString("pro_name").trim().equals(""))){
					updateFlag = true;
				} else if((getPrjectname()!=null && !getPrjectname().trim().equals("")) && (rs.getString("pro_name")==null || rs.getString("pro_name").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getPriority()!=null && rs.getString("priority")!=null && !getPriority().trim().equalsIgnoreCase(rs.getString("priority").trim())){
					updateFlag = true;
				} else if((getPriority()==null || getPriority().trim().equals("")) && (rs.getString("priority")!=null && !rs.getString("priority").trim().equals(""))){
					updateFlag = true;
				} else if((getPriority()!=null && !getPriority().trim().equals("")) && (rs.getString("priority")==null || rs.getString("priority").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getDescription()!=null && rs.getString("description")!=null && !getDescription().trim().equalsIgnoreCase(rs.getString("description").trim())){
					updateFlag = true;
				} else if((getDescription()==null || getDescription().trim().equals("")) && (rs.getString("description")!=null && !rs.getString("description").trim().equals(""))){
					updateFlag = true;
				} else if((getDescription()!=null && !getDescription().trim().equals("")) && (rs.getString("description")==null || rs.getString("description").trim().equals(""))){
					updateFlag = true;
				}

				if(getService()!=null && rs.getString("service")!=null && !getService().trim().equalsIgnoreCase(rs.getString("service").trim())){
					updateFlag = true;
				} else if((getService()==null || getService().trim().equals("")) && (rs.getString("service")!=null && !rs.getString("service").trim().equals(""))){
					updateFlag = true;
				} else if((getService()!=null && !getService().trim().equals("")) && (rs.getString("service")==null || rs.getString("service").trim().equals(""))){
					updateFlag = true;
				}
				
				if(estimatedHours!=null && rs.getString("timestatus")!=null && !estimatedHours.trim().equalsIgnoreCase(rs.getString("timestatus").trim())){
					updateFlag = true;
				} else if((estimatedHours==null || estimatedHours.trim().equals("")) && (rs.getString("timestatus")!=null && !rs.getString("timestatus").trim().equals(""))){
					updateFlag = true;
				} else if((estimatedHours!=null && !estimatedHours.trim().equals("")) && (rs.getString("timestatus")==null || rs.getString("timestatus").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getStrClient()!=null && rs.getString("client_id")!=null && !getStrClient().trim().equalsIgnoreCase(rs.getString("client_id").trim())){
					updateFlag = true;
				} else if((getStrClient()==null || getStrClient().trim().equals("")) && (rs.getString("client_id")!=null && !rs.getString("client_id").trim().equals(""))){
					updateFlag = true;
				} else if((getStrClient()!=null && !getStrClient().trim().equals("")) && (rs.getString("client_id")==null || rs.getString("client_id").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getStrClientBrand()!=null && rs.getString("client_brand_id")!=null && !getStrClientBrand().trim().equalsIgnoreCase(rs.getString("client_brand_id").trim())){
					updateFlag = true;
				} else if((getStrClientBrand()==null || getStrClientBrand().trim().equals("")) && (rs.getString("client_brand_id")!=null && !rs.getString("client_brand_id").trim().equals(""))){
					updateFlag = true;
				} else if((getStrClientBrand()!=null && !getStrClientBrand().trim().equals("")) && (rs.getString("client_brand_id")==null || rs.getString("client_brand_id").trim().equals(""))){
					updateFlag = true;
				}
				
				/*if(getStrReferanceBy()!=null && rs.getString("referance_by_id")!=null && !getStrReferanceBy().trim().equalsIgnoreCase(rs.getString("referance_by_id").trim())){
					updateFlag = true;
				} else if((getStrReferanceBy()==null || getStrReferanceBy().trim().equals("")) && (rs.getString("referance_by_id")!=null && !rs.getString("referance_by_id").trim().equals(""))){
					updateFlag = true;
				} else if((getStrReferanceBy()!=null && !getStrReferanceBy().trim().equals("")) && (rs.getString("referance_by_id")==null || rs.getString("referance_by_id").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getStrRelationShipBy()!=null && rs.getString("releation_by_id")!=null && !getStrRelationShipBy().trim().equalsIgnoreCase(rs.getString("releation_by_id").trim())){
					updateFlag = true;
				} else if((getStrRelationShipBy()==null || getStrRelationShipBy().trim().equals("")) && (rs.getString("releation_by_id")!=null && !rs.getString("releation_by_id").trim().equals(""))){
					updateFlag = true;
				} else if((getStrRelationShipBy()!=null && !getStrRelationShipBy().trim().equals("")) && (rs.getString("releation_by_id")==null || rs.getString("releation_by_id").trim().equals(""))){
					updateFlag = true;
				}
				*/
				
				if(getClientPoc()!=null && rs.getString("poc")!=null && !getClientPoc().trim().equalsIgnoreCase(rs.getString("poc").trim())){
					updateFlag = true;
				} else if((getClientPoc()==null || getClientPoc().trim().equals("")) && (rs.getString("poc")!=null && !rs.getString("poc").trim().equals(""))){
					updateFlag = true;
				} else if((getClientPoc()!=null && !getClientPoc().trim().equals("")) && (rs.getString("poc")==null || rs.getString("poc").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getStartDate()!=null && rs.getString("start_date")!=null && !uF.getDateFormat(getStartDate(), DATE_FORMAT).equals(uF.getDateFormat(rs.getString("start_date"), DBDATE))){
					updateFlag = true;
				} else if((getStartDate()==null || getStartDate().trim().equals("")) && (rs.getString("start_date")!=null && !rs.getString("start_date").trim().equals(""))){
					updateFlag = true;
				} else if((getStartDate()!=null && !getStartDate().trim().equals("")) && (rs.getString("start_date")==null || rs.getString("start_date").trim().equals(""))){
					updateFlag = true;
				}
				
//				StringBuilder sbLevel = new StringBuilder();
//				for(int i=0; level!=null && i<level.length; i++) {
//					sbLevel.append(level[i]+",");
//				}
//				pst.setString(14, sbLevel.toString());
				
				if(location!=null && rs.getString("wlocation_id")!=null && !location.trim().equalsIgnoreCase(rs.getString("wlocation_id").trim())){
					updateFlag = true;
				} else if((location==null || location.trim().equals("")) && (rs.getString("wlocation_id")!=null && !rs.getString("wlocation_id").trim().equals(""))){
					updateFlag = true;
				} else if((location!=null && !location.trim().equals("")) && (rs.getString("wlocation_id")==null || rs.getString("wlocation_id").trim().equals(""))){
					updateFlag = true;
				}				
				
				if(billingType!=null && rs.getString("billing_type")!=null && !billingType.trim().equalsIgnoreCase(rs.getString("billing_type").trim())){
					updateFlag = true;
				} else if((billingType==null || billingType.trim().equals("")) && (rs.getString("billing_type")!=null && !rs.getString("billing_type").trim().equals(""))){
					updateFlag = true;
				} else if((billingType!=null && !billingType.trim().equals("")) && (rs.getString("billing_type")==null || rs.getString("billing_type").trim().equals(""))){
					updateFlag = true;
				}
				
		//===start parvez date: 11-10-2022===		
				/*if(getStrProjectOwner()!=null && rs.getString("project_owner")!=null && !getStrProjectOwner().trim().equalsIgnoreCase(rs.getString("project_owner").trim())){
					updateFlag = true;
				} else if((getStrProjectOwner()==null || getStrProjectOwner().trim().equals("")) && (rs.getString("project_owner")!=null && !rs.getString("project_owner").trim().equals(""))){
					updateFlag = true;
				} else if((getStrProjectOwner()!=null && !getStrProjectOwner().trim().equals("")) && (rs.getString("project_owner")==null || rs.getString("project_owner").trim().equals(""))){
					updateFlag = true;
				}*/
				
				if(getStrProjectOwner()!=null && rs.getString("project_owners")!=null && !getData(getStrProjectOwner()).equalsIgnoreCase(rs.getString("project_owners").trim())){
					updateFlag = true;
				} else if((getStrProjectOwner()==null || getStrProjectOwner().equals("") || getStrProjectOwner().isEmpty()) && (rs.getString("project_owners")!=null && !rs.getString("project_owners").trim().equals(""))){
					updateFlag = true;
				} else if((getStrProjectOwner()!=null && !getStrProjectOwner().equals("") && !getStrProjectOwner().isEmpty()) && (rs.getString("project_owners")==null || rs.getString("project_owners").trim().equals(""))){
					updateFlag = true;
				}
		//===end parvez date: 11-10-2022===		
				
				if(getStrCurrency()!=null && rs.getString("curr_id")!=null && !getStrCurrency().trim().equalsIgnoreCase(rs.getString("curr_id").trim())){
					updateFlag = true;
				} else if((getStrCurrency()==null || getStrCurrency().trim().equals("")) && (rs.getString("curr_id")!=null && !rs.getString("curr_id").trim().equals(""))){
					updateFlag = true;
				} else if((getStrCurrency()!=null && !getStrCurrency().trim().equals("")) && (rs.getString("curr_id")==null || rs.getString("curr_id").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getStrSBU()!=null && rs.getString("sbu_id")!=null && !getStrSBU().trim().equalsIgnoreCase(rs.getString("sbu_id").trim())){
					updateFlag = true;
				} else if((getStrSBU()==null || getStrSBU().trim().equals("")) && (rs.getString("sbu_id")!=null && !rs.getString("sbu_id").trim().equals(""))){
					updateFlag = true;
				} else if((getStrSBU()!=null && !getStrSBU().trim().equals("")) && (rs.getString("sbu_id")==null || rs.getString("sbu_id").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getOrganisation()!=null && rs.getString("org_id")!=null && !getOrganisation().trim().equalsIgnoreCase(rs.getString("org_id").trim())){
					updateFlag = true;
				} else if((getOrganisation()==null || getOrganisation().trim().equals("")) && (rs.getString("org_id")!=null && !rs.getString("org_id").trim().equals(""))){
					updateFlag = true;
				} else if((getOrganisation()!=null && !getOrganisation().trim().equals("")) && (rs.getString("org_id")==null || rs.getString("org_id").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getShortDescription()!=null && rs.getString("short_description")!=null && !getShortDescription().trim().equalsIgnoreCase(rs.getString("short_description").trim())){
					updateFlag = true;
				} else if((getShortDescription()==null || getShortDescription().trim().equals("")) && (rs.getString("short_description")!=null && !rs.getString("short_description").trim().equals(""))){
					updateFlag = true;
				} else if((getShortDescription()!=null && !getShortDescription().trim().equals("")) && (rs.getString("short_description")==null || rs.getString("short_description").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getStrBillingCurrency()!=null && rs.getString("billing_curr_id")!=null && !getStrBillingCurrency().trim().equalsIgnoreCase(rs.getString("billing_curr_id").trim())){
					updateFlag = true;
				} else if((getStrBillingCurrency()==null || getStrBillingCurrency().trim().equals("")) && (rs.getString("billing_curr_id")!=null && !rs.getString("billing_curr_id").trim().equals(""))){
					updateFlag = true;
				} else if((getStrBillingCurrency()!=null && !getStrBillingCurrency().trim().equals("")) && (rs.getString("billing_curr_id")==null || rs.getString("billing_curr_id").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getStrBillingKind()!=null && rs.getString("billing_kind")!=null && !getStrBillingKind().trim().equalsIgnoreCase(rs.getString("billing_kind").trim())){
					updateFlag = true;
				} else if((getStrBillingKind()==null || getStrBillingKind().trim().equals("")) && (rs.getString("billing_kind")!=null && !rs.getString("billing_kind").trim().equals(""))){
					updateFlag = true;
				} else if((getStrBillingKind()!=null && !getStrBillingKind().trim().equals("")) && (rs.getString("billing_kind")==null || rs.getString("billing_kind").trim().equals(""))){
					updateFlag = true;
				}
				
				String strHoursDay = uF.parseToInt(getHoursToDay())>0 ? getHoursToDay() : "1"; 
				if(strHoursDay!=null && rs.getString("bill_days_type")!=null && !strHoursDay.trim().equalsIgnoreCase(rs.getString("bill_days_type").trim())){
					updateFlag = true;
				} else if((strHoursDay==null || strHoursDay.trim().equals("")) && (rs.getString("bill_days_type")!=null && !rs.getString("bill_days_type").trim().equals(""))){
					updateFlag = true;
				} else if((strHoursDay!=null && !strHoursDay.trim().equals("")) && (rs.getString("bill_days_type")==null || rs.getString("bill_days_type").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getHoursForDay()!=null && rs.getString("hours_for_bill_day")!=null && !getHoursForDay().trim().equalsIgnoreCase(rs.getString("hours_for_bill_day").trim())){
					updateFlag = true;
				} else if((getHoursForDay()==null || getHoursForDay().trim().equals("")) && (rs.getString("hours_for_bill_day")!=null && !rs.getString("hours_for_bill_day").trim().equals(""))){
					updateFlag = true;
				} else if((getHoursForDay()!=null && !getHoursForDay().trim().equals("")) && (rs.getString("hours_for_bill_day")==null || rs.getString("hours_for_bill_day").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getStrDepartment()!=null && rs.getString("department_id")!=null && !getStrDepartment().trim().equalsIgnoreCase(rs.getString("department_id").trim())){
					updateFlag = true;
				} else if((getStrDepartment()==null || getStrDepartment().trim().equals("")) && (rs.getString("department_id")!=null && !rs.getString("department_id").trim().equals(""))){
					updateFlag = true;
				} else if((getStrDepartment()!=null && !getStrDepartment().trim().equals("")) && (rs.getString("department_id")==null || rs.getString("department_id").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getWeekdayCycle()!=null && rs.getString("billing_cycle_weekday")!=null && !getWeekdayCycle().trim().equalsIgnoreCase(rs.getString("billing_cycle_weekday").trim())){
					updateFlag = true;
				} else if((getWeekdayCycle()==null || getWeekdayCycle().trim().equals("")) && (rs.getString("billing_cycle_weekday")!=null && !rs.getString("billing_cycle_weekday").trim().equals(""))){
					updateFlag = true;
				} else if((getWeekdayCycle()!=null && !getWeekdayCycle().trim().equals("")) && (rs.getString("billing_cycle_weekday")==null || rs.getString("billing_cycle_weekday").trim().equals(""))){
					updateFlag = true;
				}
				
				if(getDayCycle()!=null && rs.getString("billing_cycle_day")!=null && !getDayCycle().trim().equalsIgnoreCase(rs.getString("billing_cycle_day").trim())){
					updateFlag = true;
				} else if((getDayCycle()==null || getDayCycle().trim().equals("")) && (rs.getString("billing_cycle_day")!=null && !rs.getString("billing_cycle_day").trim().equals(""))){
					updateFlag = true;
				} else if((getDayCycle()!=null && !getDayCycle().trim().equals("")) && (rs.getString("billing_cycle_day")==null || rs.getString("billing_cycle_day").trim().equals(""))){
					updateFlag = true;
				}
				
			}
			rs.close();
			pst.close();
			
			if(updateFlag){
				List<String> empList = new ArrayList<String>();
				pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				String proName = null;
				String proOwner = null;
				String proDescription = null;
				String clientId = null;
				String orgName = null;
				while(rs.next()) {
					/*if(uF.parseToInt(rs.getString("project_owner"))> 0) {
						empList.add(rs.getString("project_owner"));
					}*/
					proName = rs.getString("pro_name");
					
				//===start parvez date: 11-10-2022===
//					proOwner = CF.getEmpNameMapByEmpId(con, rs.getString("project_owner"));
					if(rs.getString("project_owners") !=null && !rs.getString("project_owners").equals("")){
						List<String> proOwnerList = Arrays.asList(rs.getString("project_owners").split(","));
						StringBuilder sbProOwners = null;
						for(int k=0;k<proOwnerList.size();k++){
							
							empList.add(proOwnerList.get(k));
							
							if(sbProOwners==null){
								sbProOwners = new StringBuilder();
								sbProOwners.append(CF.getEmpNameMapByEmpId(con, proOwnerList.get(k)));
							} else{
								sbProOwners.append(", "+CF.getEmpNameMapByEmpId(con, proOwnerList.get(k)));
							}
						}
						
						proOwner = sbProOwners.toString();
					}
				//===end parvez date: 11-10-2022===	
					proDescription = rs.getString("short_description");
					clientId = rs.getString("poc");
					orgName = CF.getOrgNameById(con, rs.getString("org_id"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpName = new HashMap<String, String>();
				pst = con.prepareStatement("select ped.emp_id, epd.emp_fname, epd.emp_lname,epd.emp_email_sec,epd.emp_email,epd.emp_contactno_mob " +
						"from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and is_alive = true " +
						"and ped.pro_id=? and ped._isteamlead = true");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				while(rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id"))> 0 && !empList.contains(rs.getString("emp_id"))) {
						empList.add(rs.getString("emp_id"));
						hmEmpName.put(rs.getString("emp_id")+"_FNAME", rs.getString("emp_fname"));
						hmEmpName.put(rs.getString("emp_id")+"_LNAME", rs.getString("emp_lname"));
						if(rs.getString("emp_email_sec") !=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							hmEmpName.put(rs.getString("emp_id")+"_EMAIL", rs.getString("emp_email_sec"));
						} else if(rs.getString("emp_email") !=null && rs.getString("emp_email").indexOf("@")>0) {
							hmEmpName.put(rs.getString("emp_id")+"_EMAIL", rs.getString("emp_email"));
						}
						hmEmpName.put(rs.getString("emp_id")+"_CONTACT_NO", rs.getString("emp_contactno_mob"));
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_UPDATE_PROJECT, CF); 
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrOrgId(strOrgId);
				nF.setEmailTemplate(true);
				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(clientId));
				rs = pst.executeQuery();
				boolean flg=false;
				while(rs.next()) {
					nF.setStrCustFName(rs.getString("contact_fname"));
					nF.setStrCustLName(rs.getString("contact_lname"));
					nF.setStrEmpMobileNo(rs.getString("contact_number"));
					if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
						nF.setStrEmpEmail(rs.getString("contact_email"));
						nF.setStrEmailTo(rs.getString("contact_email"));
					}
					flg = true;
				}
				rs.close();
				pst.close();
				
				if(flg) {
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrProjectName(proName);
					nF.setStrProjectOwnerName(proOwner);
					nF.setStrProjectDescription(proDescription);
					nF.setStrOrgName(orgName);
					nF.sendNotifications(); 
				}
				
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
					
					nF.setStrCustFName(hmEmpName.get(empList.get(i)+"_FNAME"));
					nF.setStrCustLName(hmEmpName.get(empList.get(i)+"_LNAME"));
					nF.setStrEmpMobileNo(hmEmpName.get(empList.get(i)+"_CONTACT_NO"));
					nF.setStrEmpEmail(hmEmpName.get(empList.get(i)+"_EMAIL"));
					nF.setStrEmailTo(hmEmpName.get(empList.get(i)+"_EMAIL"));
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrProjectName(proName);
					nF.setStrProjectOwnerName(proOwner);
					nF.setStrProjectDescription(proDescription);
					nF.setStrOrgName(orgName);
					nF.sendNotifications();
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

	
	private void deleteMilestones(Connection con, UtilityFunctions uF, String pro_id) {

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("delete from project_milestone_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(pro_id));
			pst.executeUpdate();
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



	public void saveProjectInfoStep1(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);
			
			String [] fYarr = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			if(hmFeatureStatus==null) hmFeatureStatus = new HashMap<String, String>();
//			System.out.println("getStrActualBilling=====>"+getStrActualBilling());
			if(getPro_id()!=null && getPrjectname()!=null && getPrjectname().length()>0) {
				updateProjectInfoStep1(uF);
			} else {
				
				String tempfStartDate[] = fYarr[0].split("/");
				String tempfEndDate[] = fYarr[1].split("/");
//				System.out.println("tempfStartDate ===> "+ tempfStartDate[2] +" tempfEndDate ===> "+ tempfEndDate[2]);
				String startFYear = tempfStartDate[2].substring(2, 4);
				String endFYear = tempfEndDate[2].substring(2, 4);
//				System.out.println("startFYear ===> "+ startFYear +" endFYear ===> "+ endFYear);
				
				pst = con.prepareStatement("select dept_code from department_info where dept_id=?");
				pst.setInt(1, uF.parseToInt(getStrDepartment()));
				rs = pst.executeQuery();
				String strDeptCode = null;
				while(rs.next()) {
					strDeptCode = rs.getString("dept_code");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select wloacation_code from work_location_info where wlocation_id=?");
				pst.setInt(1, uF.parseToInt(getLocation()));
				rs = pst.executeQuery();
				String strWLocationCode = null;
				while(rs.next()) {
					strWLocationCode = rs.getString("wloacation_code");
				}
				rs.close();
				pst.close();
				
				String fYear = startFYear+"-"+endFYear;
				pst = con.prepareStatement("select max(pro_count) as pro_count from projectmntnc where pro_fyear=? and department_id=? and wlocation_id=?");
				pst.setString(1, fYear);
				pst.setInt(2, uF.parseToInt(getStrDepartment()));
				pst.setInt(3, uF.parseToInt(getLocation()));
				rs = pst.executeQuery();
				int nProCount = 0;
				while(rs.next()) {
					nProCount = uF.parseToInt(rs.getString("pro_count"));
				}
				rs.close();
				pst.close();
				nProCount++;
				int nLength = nProCount+"".length();
				if(nLength==0) {
					nLength = 1;
				}
				
				StringBuilder sbProjectCount = new StringBuilder();
				for(int i=0; i<(4-nLength); i++) {
					sbProjectCount.append("0");
				}
				sbProjectCount.append(nProCount);
				
			//===start parvez date: 22-11-2022===	
//				setPrjectCode(fYear+"/"+strDeptCode+"/"+strWLocationCode+"-"+sbProjectCount.toString());
				if(hmFeatureStatus!=null && !uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION))){
					setPrjectCode(fYear+"/"+strDeptCode+"/"+strWLocationCode+"-"+sbProjectCount.toString());
				}
			//===end parvez date: 22-11-2022===	
				
//				System.out.println("getStrClient ===>>>> " + getStrClient());
				StringBuilder sbQuery = new StringBuilder();
				
			//===start parvez date: 11-10-2022===	
				/*sbQuery.append("insert into projectmntnc (pro_name, priority, description, activity, service, taskstatus, deadline,idealtime, "
						+ "timestatus, client_id, project_code, start_date,added_by,level_id,wlocation_id,billing_type,billing_amount,entry_date,"
						+ "actual_calculation_type, department_id, billing_kind, pro_count,pro_fyear,project_owner,curr_id,sbu_id,org_id,"
						+ "short_description,billing_curr_id,bill_days_type,hours_for_bill_day,billing_cycle_weekday,billing_cycle_day,approve_status");*/
				sbQuery.append("insert into projectmntnc (pro_name, priority, description, activity, service, taskstatus, deadline,idealtime, "
						+ "timestatus, client_id, project_code, start_date,added_by,level_id,wlocation_id,billing_type,billing_amount,entry_date,"
						+ "actual_calculation_type, department_id, billing_kind, pro_count,pro_fyear,project_owners,curr_id,sbu_id,org_id,"
						+ "short_description,billing_curr_id,bill_days_type,hours_for_bill_day,billing_cycle_weekday,billing_cycle_day,approve_status,revenue_target");
		//===end parvez date: 11-10-2022===		
				if(uF.parseToInt(getClientPoc())>0) {
					sbQuery.append(", poc");
				}
				if(uF.parseToInt(getStrReferanceBy())>0) {
					sbQuery.append(", reference_by_id");
				}
				if(uF.parseToInt(getStrClientBrand())>0) {
					sbQuery.append(", client_brand_id");
				}
				
				if(uF.parseToInt(getStrPortfolioManager())>0) {
					sbQuery.append(", portfolio_manager");
				}
				
				if(uF.parseToInt(getStrAccountManager())>0) {
					sbQuery.append(", account_manager");
				}
				
				if(uF.parseToInt(getStrDeliveryManager())>0) {
					sbQuery.append(", delivery_manager");
				}
				
		//===start parvez date: 22-11-2022===
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION))){
					sbQuery.append(", ust_project_code, project_acc_code, segment, pro_domain_id");
				}
		//===end parvez date: 22-11-2022===		
				
				sbQuery.append(") values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?");
				if(uF.parseToInt(getClientPoc())>0) {
					sbQuery.append(","+uF.parseToInt(getClientPoc()));
				}
				if(uF.parseToInt(getStrReferanceBy())>0) {
					sbQuery.append(","+uF.parseToInt(getStrReferanceBy()));
				}
				if(uF.parseToInt(getStrClientBrand())>0) {
					sbQuery.append(","+uF.parseToInt(getStrClientBrand()));
				}
				
				if(uF.parseToInt(getStrPortfolioManager())>0) {
					sbQuery.append(","+uF.parseToInt(getStrPortfolioManager()));
				}
				
				if(uF.parseToInt(getStrAccountManager())>0) {
					sbQuery.append(","+uF.parseToInt(getStrAccountManager()));
				}
				
				if(uF.parseToInt(getStrDeliveryManager())>0) {
					sbQuery.append(","+uF.parseToInt(getStrDeliveryManager()));
				}
				
		//===start parvez date: 22-11-2022===		
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION))){
					sbQuery.append(", '"+getUstPrjectId()+"'");
					sbQuery.append(", '"+getProjectAccountId()+"'");
					sbQuery.append(", '"+getProjectSegment()+"'");
					sbQuery.append(","+uF.parseToInt(getProDomain()));
				}
		//===end parvez date: 22-11-2022===		
				
				sbQuery.append(")");
				
//				pst = con.prepareStatement("insert into projectmntnc (pro_name, priority, description, activity, service, taskstatus, deadline, " +
//					"idealtime, timestatus, client_id, project_code, poc, start_date, added_by,level_id,wlocation_id,billing_type,billing_amount, " +
//					"entry_date, actual_calculation_type, department_id, billing_kind, pro_count,pro_fyear,project_owner,curr_id,sbu_id,org_id," +
//					"short_description,billing_curr_id,bill_days_type,hours_for_bill_day,billing_cycle_weekday,billing_cycle_day,reference_by_id," +
//					"client_brand_id,approve_status) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setString(1, getPrjectname());
				pst.setInt(2, uF.parseToInt(getPriority()));
				pst.setString(3, getDescription());
				pst.setString(4, getPrjectname());
				pst.setInt(5, uF.parseToInt(getService()));
				pst.setString(6, "New Task");
				pst.setDate(7, uF.getDateFormat(getDeadline(), DATE_FORMAT));
				pst.setInt(8, uF.parseToInt(estimatedHours));
				pst.setString(9, "n");
				pst.setInt(10, uF.parseToInt(getStrClient()));
				pst.setString(11, getPrjectCode());
//				pst.setInt(12, uF.parseToInt(getClientPoc()));
				pst.setDate(12, uF.getDateFormat(getStartDate(), DATE_FORMAT));
				pst.setInt(13, uF.parseToInt((String) session.getAttribute(EMPID)));
				
				StringBuilder sbLevel = new StringBuilder();
				for(int i=0; level!=null && i<level.length; i++) {
					sbLevel.append(level[i]+",");
				}
				pst.setString(14, sbLevel.toString());
				pst.setInt(15, uF.parseToInt(location));
				pst.setString(16, billingType);
				if (billingType != null && (billingType.equals("H") || billingType.equals("D") || billingType.equals("M"))) {
					pst.setDouble(17, uF.parseToDouble(billingAmountH));
				} else {
					pst.setDouble(17, uF.parseToDouble(billingAmountF));
				}
				pst.setDate(18, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				if(billingType != null && billingType.equalsIgnoreCase("F")) {
					pst.setString(19, getStrActualBilling());
				} else {
					pst.setString(19, billingType);
				}
				pst.setInt(20, uF.parseToInt(getStrDepartment()));
				pst.setString(21, getStrBillingKind());
				pst.setInt(22, nProCount);
				pst.setString(23, fYear);
		//===start parvez date: 11-10-2022===		
//				pst.setInt(24, uF.parseToInt(getStrProjectOwner()));
				pst.setString(24, getData(getStrProjectOwner()));
		//===end parvez date: 11-10-2022===		
				pst.setInt(25, uF.parseToInt(getStrCurrency()));
				pst.setInt(26, uF.parseToInt(getStrSBU()));
				pst.setInt(27, uF.parseToInt(getOrganisation()));
				pst.setString(28, getShortDescription());
				pst.setInt(29, uF.parseToInt(getStrBillingCurrency()));
				pst.setInt(30, uF.parseToInt(getHoursToDay())>0 ? uF.parseToInt(getHoursToDay()) :1);
				pst.setDouble(31, uF.parseToDouble(getHoursForDay()));
				pst.setString(32, getWeekdayCycle());
				pst.setInt(33, uF.parseToInt(getDayCycle()));
//				pst.setInt(35, uF.parseToInt(getStrReferanceBy()));
//				pst.setInt(36, uF.parseToInt(getStrClientBrand()));
				if(getProType()!=null && getProType().equals("P")) {
					pst.setString(34, "pipelined");
				} else {
					pst.setString(34, "n");
				}
				pst.setDouble(35, uF.parseToDouble(getRevenueTarget()));
//				System.out.println("pst====>"+pst);
				int x = pst.executeUpdate(); 
				pst.close();
				
//				System.out.println("getStrProjectOwner() ===>>> " + getStrProjectOwner());
				pst = con.prepareStatement("select max(pro_id)as id from projectmntnc");
				rs = pst.executeQuery();
				while (rs.next()) {
					setPro_id(rs.getString("id"));
				}
				rs.close();
				pst.close();

				String forcedTask = null;
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, getPro_id());
				if(hmProjectData != null) {
					forcedTask = CF.getProjectForcedTask(con, hmProjectData.get("PRO_ORG_ID"));
				}
				
				pst = con.prepareStatement("select * from service_tasks_details where service_id=?");
				pst.setInt(1, uF.parseToInt(getService()));
				rs = pst.executeQuery();
				List<List<String>> alServiceTaskList = new ArrayList<List<String>>();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("task_name"));
					innerList.add(rs.getString("task_description"));
					alServiceTaskList.add(innerList);
				}
				rs.close();
				pst.close();
				
				
				for(int i=0; alServiceTaskList!=null && i<alServiceTaskList.size(); i++) {
					List<String> innerList = alServiceTaskList.get(i);
					pst = con.prepareStatement("insert into activity_info (activity_name,priority,taskstatus,pro_id,task_skill_id,task_description," +
						"task_accept_status,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?) ");
					pst.setString(1, innerList.get(0));
					pst.setString(2, "0");
					pst.setString(3, "New Task");
					pst.setInt(4, uF.parseToInt(getPro_id()));
					pst.setInt(5, 0);
					pst.setString(6, uF.showData(innerList.get(1), ""));
					if(uF.parseToBoolean(forcedTask)) {
						pst.setInt(7, 1);
					} else {
						pst.setInt(7, 0);
					}
					pst.setInt(8, uF.parseToInt(strSessionEmpId));
					pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.executeUpdate();
					pst.close();
				}
				
		//===start parvez date: 11-10-2022===		
				/*
//				String proAlertData = "<a href=\"ViewAllProjects.action\" style=\"color: black; font-weight: normal; width: 100%;\"><div style=\"float: left;\"> <b>"+getPrjectname()+"</b> has been created by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div></a>";
				String alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+getPrjectname()+"</b> project has been created by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				String alertAction = "ViewAllProjects.action";
//				System.out.println("proAlertData ===>> " + proAlertData);
				String strDomain1 = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts1=new UserAlerts(con, uF, CF, request);
				userAlerts1.setStrDomain(strDomain1);
				userAlerts1.setStrEmpId(getStrProjectOwner());
				userAlerts1.setStrData(alertData);
				userAlerts1.setStrAction(alertAction);
//				userAlerts1.set_type(PRO_CREATED_ALERT);
				userAlerts1.setStatus(INSERT_TR_ALERT);
				Thread t1 = new Thread(userAlerts1);
				t1.run();
				
				String activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> P </span> <b>"+getPrjectname()+"</b> project has been created by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrAlignWith(PROJECT+"");
				userAct.setStrAlignWithId(getPro_id());
				userAct.setStrTaggedWith(","+getStrProjectOwner()+",");
				userAct.setStrVisibilityWith(","+getStrProjectOwner()+",");
				userAct.setStrVisibility("2");
				userAct.setStrData(activityData);
				userAct.setStrSessionEmpId(strSessionEmpId);
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();*/
				
				String alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+getPrjectname()+"</b> project has been created by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				String alertAction = "ViewAllProjects.action";
				
				for(int i=0; i<getStrProjectOwner().size();i++){
					
//					System.out.println("proAlertData ===>> " + proAlertData);
					String strDomain1 = request.getServerName().split("\\.")[0];
					UserAlerts userAlerts1=new UserAlerts(con, uF, CF, request);
					userAlerts1.setStrDomain(strDomain1);
					userAlerts1.setStrEmpId(getStrProjectOwner().get(i));
					userAlerts1.setStrData(alertData);
					userAlerts1.setStrAction(alertAction);
//					userAlerts1.set_type(PRO_CREATED_ALERT);
					userAlerts1.setStatus(INSERT_TR_ALERT);
					Thread t1 = new Thread(userAlerts1);
					t1.run();
					
					String activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> P </span> <b>"+getPrjectname()+"</b> project has been created by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					UserActivities userAct = new UserActivities(con, uF, CF, request);
					userAct.setStrDomain(strDomain);
					userAct.setStrAlignWith(PROJECT+"");
					userAct.setStrAlignWithId(getPro_id());
					userAct.setStrTaggedWith(","+getStrProjectOwner().get(i)+",");
					userAct.setStrVisibilityWith(","+getStrProjectOwner().get(i)+",");
					userAct.setStrVisibility("2");
					userAct.setStrData(activityData);
					userAct.setStrSessionEmpId(strSessionEmpId);
					userAct.setStatus(INSERT_TR_ACTIVITY);
					Thread tt = new Thread(userAct);
					tt.run();
				}
		//===end parvez date: 11-10-2022===	
				
				if(uF.parseToInt(getClientPoc()) > 0) {
					UserAlerts userAlerts2=new UserAlerts(con, uF, CF, request);
					userAlerts2.setStrDomain(strDomain);
					userAlerts2.setStrEmpId(""+getClientPoc());
					userAlerts2.setStrOther("other");
					userAlerts2.setStrData(alertData);
					userAlerts2.setStrAction(alertAction);
//					userAlerts2.set_type(PRO_CREATED_ALERT);
					userAlerts2.setStatus(INSERT_TR_ALERT);
					Thread t2 = new Thread(userAlerts2);
					t2.run();
					
					String activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> P </span> <b>"+getPrjectname()+"</b> project has been created by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					UserActivities userAct = new UserActivities(con, uF, CF, request);
					userAct.setStrDomain(strDomain);
					userAct.setStrAlignWith(PROJECT+"");
					userAct.setStrAlignWithId(getPro_id());
					userAct.setStrTaggedWith(","+getClientPoc()+",");
					userAct.setStrVisibilityWith(","+getClientPoc()+",");
					userAct.setStrVisibility("2");
					userAct.setStrData(activityData);
					userAct.setStrSessionEmpId(strSessionEmpId);
					userAct.setStatus(INSERT_TR_ACTIVITY);
					userAct.setStrOther("other");
					if(strUserType.equals(CUSTOMER)) {
						userAct.setStrUserType("C");
					}
					Thread tt1 = new Thread(userAct);
					tt1.run();
				}
				
				ProjectScheduler scheduler = new ProjectScheduler(request, session, CF, uF, strSessionEmpId);
				scheduler.updateProjectDetails(getPro_id());
//				System.out.println("inserted ");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void createFolderForDocs(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);
			
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
			String mainPathWithOrg = CF.getProjectDocumentFolder()+strOrgId;
			File fileOrg = new File(mainPathWithOrg);
			if (!fileOrg.exists()) {
				if (fileOrg.mkdir()) {
//					System.out.println("Org Directory is created!");
				}
			}
			
			String mainPath = mainPathWithOrg+"/Projects";
			File file = new File(mainPath);
			if (!file.exists()) {
				if (file.mkdir()) {
//					System.out.println("Projects Directory is created!");
				}
			}
			
			String proNameFolder = mainPath +"/"+getPro_id();
			File file1 = new File(proNameFolder);
			if (!file1.exists()) {
				if (file1.mkdir()) {
//					System.out.println("pro_id Directory is created!");
				}
			} 
			
			
			String[] docsTRId = request.getParameterValues("docsTRId");
//			String[] proDocTasks = request.getParameterValues("proDocTasks");
			String[] docSharingType = request.getParameterValues("docSharingType");
			String[] proCategoryTypeDoc = request.getParameterValues("proCategoryTypeDoc");
			String[] strDocDescription = request.getParameterValues("strDocDescription");
			String[] strScopeDoc = request.getParameterValues("strScopeDoc");
			
			String[] isDocEdit = request.getParameterValues("isDocEdit");
			String[] isDocDelete = request.getParameterValues("isDocDelete");
			
			for(int j=0; docsTRId != null && j<docsTRId.length; j++) {
				String proDocTasks = request.getParameter("proDocTasks"+docsTRId[j]);
				String proDocCategory = request.getParameter("proDocCategory"+docsTRId[j]);
				double lengthBytes =  getStrDoc()[j].length();
				
				String extenstion=FilenameUtils.getExtension(getStrDocFileName()[j]);	
				String strFileName = FilenameUtils.getBaseName(getStrDocFileName()[j]);
				strFileName = strFileName+"v1."+extenstion;
				
				boolean isFileExist = false;
				File f = new File(proNameFolder+"/"+strFileName);
				if(f.isFile()) {
//				    System.out.println("isFile");
				    if(f.exists()){
						isFileExist = true;
//					    System.out.println("exists");
					}else{
//					    System.out.println("exists fail");
					}   
				}else{
//				    System.out.println("isFile fail");
				}
				
				if(lengthBytes > 0 && !isFileExist) {
					String[] proDocEmployee = request.getParameterValues("proDocEmployee"+docsTRId[j]);
					List<String> alEmployee = null;
					if(proDocEmployee != null) {
						alEmployee = Arrays.asList(proDocEmployee);
					}
					StringBuilder sbEmps = null;
					
					for(int a=0; alEmployee != null && a<alEmployee.size(); a++) {
						if(alEmployee.get(a) != null && !alEmployee.get(a).trim().equals("")) {
							if(sbEmps == null) {
								sbEmps = new StringBuilder();
								sbEmps.append(","+ alEmployee.get(a).trim() +",");
							} else {
								sbEmps.append(alEmployee.get(a).trim() +",");
							}
						}
					}
					if(sbEmps == null) {
						sbEmps = new StringBuilder();
					}
					
					String[] proDocPoc = request.getParameterValues("proDocPoc"+docsTRId[j]);
					List<String> alPoc = null;
					if(proDocPoc != null) {
						alPoc = Arrays.asList(proDocPoc);
					}
					StringBuilder sbPoc = null;
					
					for(int a=0; alPoc != null && a<alPoc.size(); a++) {
						if(alPoc.get(a) != null && !alPoc.get(a).trim().equals("")) {
							if(sbPoc == null) {
								sbPoc = new StringBuilder();
								sbPoc.append(","+ alPoc.get(a).trim() +",");
							} else {
								sbPoc.append(alPoc.get(a).trim() +",");
							}
						}
					}
					if(sbPoc == null) {
						sbPoc = new StringBuilder();
					}
					
					
					pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
					"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setString(1, strDocDescription[j]);
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(2, 1);
					} else {
						pst.setInt(2, 0);
					}
//					pst.setInt(2, uF.parseToInt(getStrAlignWith()));
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(3, uF.parseToInt(getPro_id()));
					} else {
						pst.setInt(3, uF.parseToInt(proDocCategory));
					}
//					pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
					pst.setString(4, sbEmps.toString());
					pst.setString(5, "");
					pst.setString(6, "");
					pst.setInt(7, uF.parseToInt(docSharingType[j]));
					pst.setString(8, sbEmps.toString());
					pst.setInt(9, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(11, strFileName);
					pst.executeUpdate();
					pst.close();
					
					String feedId = null;
					pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
					rs = pst.executeQuery();
					while(rs.next()) {
						feedId = rs.getString("communication_id");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,added_by, entry_date, folder_file_type, " +
						"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document, description,doc_parent_id, " +
						"doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrClient()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
//					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(5, "file");
					pst.setInt(6, 0);
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(7, uF.parseToInt(proDocTasks));
					} else {
						pst.setInt(7, uF.parseToInt(proDocCategory));
					}
					pst.setInt(8, uF.parseToInt(docSharingType[j]));
					pst.setString(9, sbEmps.toString());
					pst.setInt(10, uF.parseToInt(proCategoryTypeDoc[j]));
					pst.setString(11, strScopeDoc[j]);
					pst.setString(12, strDocDescription[j]);
					pst.setInt(13, 0);
					pst.setInt(14, 1);
					pst.setBoolean(15, uF.parseToBoolean(isDocEdit[j]));
					pst.setBoolean(16, uF.parseToBoolean(isDocDelete[j]));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(17, true);
					} else {
						pst.setBoolean(17, false);
					}
					pst.setString(18, sbPoc.toString());
					pst.setInt(19, uF.parseToInt(feedId));
	//				System.out.println("pst====>"+pst);
					pst.execute();
					pst.close();
					
					String proDocumentId = "";
					pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
					rs = pst.executeQuery();
					while (rs.next()) {
						proDocumentId = rs.getString("pro_document_id");
					}
					rs.close();
					pst.close();
					
					uploadProjectDocuments(con, getPro_id(), getStrDoc()[j], strFileName, proNameFolder, proDocumentId, feedId);
					
					
					/**
					 * Alerts
					 * */
					
					List<String> alEmp= null;
					if(proDocEmployee != null) {
						alEmp = Arrays.asList(proDocEmployee);
					}
					if(alEmp == null){
						alEmp = new ArrayList<String>();
					}
					
					List<String> alSharePoc = null;
					if(proDocPoc != null) {
						alSharePoc = Arrays.asList(proDocPoc);
					}
					if(alSharePoc == null){
						alSharePoc = new ArrayList<String>();
					}
					
					String strDocumentName = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(proDocumentId));
					rs = pst.executeQuery();
					while (rs.next()) {
						strDocumentName = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					String proName = null;
					String strCategory = null;
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proDocTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(proDocCategory));
						rs = pst.executeQuery();
						while(rs.next()) {
							strCategory = rs.getString("project_category");
						}
						rs.close();
						pst.close();
					}
					Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						nF.setStrProjectName(proName);
					} else {
						nF.setStrCategoryName(strCategory);
					}
					nF.setStrDocumentName(strDocumentName);
					
					String alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "DocumentListView.action";
					for(String strEmp : alEmp) {
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						nF.setStrEmpId(strEmp.trim());
						nF.sendNotifications();
					}
					
					for(String strEmp : alSharePoc) {
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrOther("other");
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						pst = con.prepareStatement("select * from client_poc where poc_id = ?");
						pst.setInt(1, uF.parseToInt(strEmp.trim()));
						rs = pst.executeQuery();
						boolean flg=false;
						while(rs.next()) {
							nF.setStrCustFName(rs.getString("contact_fname"));
							nF.setStrCustLName(rs.getString("contact_lname"));
							nF.setStrEmpMobileNo(rs.getString("contact_number"));
							if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
								nF.setStrEmpEmail(rs.getString("contact_email"));
								nF.setStrEmailTo(rs.getString("contact_email"));
							}
							flg = true;
						}
						rs.close();
						pst.close();
						
						if(flg) {
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
					}
					/**
					 * Alerts End
					 * */
				}
			}
			
			
			
//			String[] proFolderTasks = request.getParameterValues("proFolderTasks");
			String[] folderSharingType = request.getParameterValues("folderSharingType");
			String[] proCategoryTypeFolder = request.getParameterValues("proCategoryTypeFolder");
			String[] strFolderDescription = request.getParameterValues("strFolderDescription");
			
			String[] isFolderEdit = request.getParameterValues("isFolderEdit");
			String[] isFolderDelete = request.getParameterValues("isFolderDelete");
//			System.out.println("getStrClient 111 ===>>>> " + getStrClient());
			int docCnt = 0;
			for (int i=0; getFolderTRId() != null && i<getFolderTRId().length; i++) {
				
//				System.out.println("isFolderEdit ===>>" + isFolderEdit[i]);
//				System.out.println("isFolderDelete ===>>" + isFolderDelete[i]);
				
				String proFolderTasks = request.getParameter("proFolderTasks"+getFolderTRId()[i]);
				String proFolderCategory = request.getParameter("proFolderCategory"+getFolderTRId()[i]);

				String[] proFolderEmployee = request.getParameterValues("proFolderEmployee"+getFolderTRId()[i]);
				List<String> alEmployee = null;
				if(proFolderEmployee != null) {
					alEmployee = Arrays.asList(proFolderEmployee);
				}
				StringBuilder sbEmps = null;
				
				for(int a=0; alEmployee != null && a<alEmployee.size(); a++) {
					if(alEmployee.get(a) != null && !alEmployee.get(a).trim().equals("")) {
						if(sbEmps == null) {
							sbEmps = new StringBuilder();
							sbEmps.append(","+ alEmployee.get(a).trim() +",");
						} else {
							sbEmps.append(alEmployee.get(a).trim() +",");
						}
					}
				}
				if(sbEmps == null) {
					sbEmps = new StringBuilder();
				}
				
				String[] proFolderPoc = request.getParameterValues("proFolderPoc"+getFolderTRId()[i]);
				
				List<String> alPoc = null;
				if(proFolderPoc != null) {
					alPoc = Arrays.asList(proFolderPoc);
				}
				StringBuilder sbPoc = null;
				
				for(int a=0; alPoc != null && a<alPoc.size(); a++) {
					if(alPoc.get(a) != null && !alPoc.get(a).trim().equals("")) {
						if(sbPoc == null) {
							sbPoc = new StringBuilder();
							sbPoc.append(","+ alPoc.get(a).trim() +",");
						} else {
							sbPoc.append(alPoc.get(a).trim() +",");
						}
					}
				}
				if(sbPoc == null) {
					sbPoc = new StringBuilder();
				}
				
				
				String proFolderNameFolder = proNameFolder +"/"+ getStrFolderName()[i];
				
				File file2 = new File(proFolderNameFolder);
				if (!file2.exists()) {
					if (file2.mkdir()) {
						System.out.println("Directory is created!");
					}
				}
				
				pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, folder_file_type," +
					"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id,doc_version, " +
					"is_edit,is_delete,is_cust_add,sharing_poc) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(getStrClient()));
				pst.setInt(2, uF.parseToInt(getPro_id()));
				pst.setString(3, getStrFolderName()[i]);
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
//				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setString(6, "folder");
				pst.setInt(7, 0);
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					pst.setInt(8, uF.parseToInt(proFolderTasks));
				} else {
					pst.setInt(8, uF.parseToInt(proFolderCategory));
				}
				pst.setInt(9, uF.parseToInt(folderSharingType[i]));
				pst.setString(10, sbEmps.toString());
				pst.setInt(11, uF.parseToInt(proCategoryTypeFolder[i]));
				pst.setString(12, null);
				pst.setString(13, strFolderDescription[i]);
				pst.setInt(14, 0);
				pst.setInt(15, 0);
				pst.setBoolean(16, uF.parseToBoolean(isFolderEdit[i]));
				pst.setBoolean(17, uF.parseToBoolean(isFolderDelete[i]));
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					pst.setBoolean(18, true);
				} else {
					pst.setBoolean(18, false);
				}
				pst.setString(19, sbPoc.toString());
		//		System.out.println("pst====>"+pst);
				pst.execute();
				pst.close();
		
				String proFolderId = "";
				pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					proFolderId = rs.getString("pro_document_id");
				}
				rs.close();
				pst.close();
				
//				/**
//				 * Alerts
//				 * */
//				List<String> alEmp= null;
//				if(proFolderEmployee != null) {
//					alEmp = Arrays.asList(proFolderEmployee);
//				}
//				if(alEmp == null){
//					alEmp = new ArrayList<String>();
//				}
//				
//				for(String strEmp : alEmp){
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(strEmp.trim());
//					userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
//				}
//				List<String> alSharePoc = null;
//				if(proFolderPoc != null) {
//					alSharePoc = Arrays.asList(proFolderPoc);
//				}
//				if(alSharePoc == null){
//					alSharePoc = new ArrayList<String>();
//				}
//				for(String strEmp : alSharePoc){
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(strEmp.trim());
//					userAlerts.setStrOther("other");
//					userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
//				}
				
				
				/**
				 * Alerts
				 * */
				
				List<String> alEmp= null;
				if(proFolderEmployee != null) {
					alEmp = Arrays.asList(proFolderEmployee);
				}
				if(alEmp == null){
					alEmp = new ArrayList<String>();
				}
				
				List<String> alSharePoc = null;
				if(proFolderPoc != null) {
					alSharePoc = Arrays.asList(proFolderPoc);
				}
				if(alSharePoc == null){
					alSharePoc = new ArrayList<String>();
				}
				
				String strDocumentName = "";
				pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
				pst.setInt(1, uF.parseToInt(proFolderId));
				rs = pst.executeQuery();
				while (rs.next()) {
					strDocumentName = rs.getString("folder_name");
				}
				rs.close();
				pst.close();
				
				String proName = null;
				String strCategory = null;
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
					pst.setInt(1, uF.parseToInt(proFolderTasks));
					rs = pst.executeQuery();
					
					while(rs.next()) {
						proName = rs.getString("pro_name");
					}
					rs.close();
					pst.close();
				} else {
					pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
					pst.setInt(1, uF.parseToInt(proFolderCategory));
					rs = pst.executeQuery();
					while(rs.next()) {
						strCategory = rs.getString("project_category");
					}
					rs.close();
					pst.close();
				}
				Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);
				
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					nF.setStrProjectName(proName);
				} else {
					nF.setStrCategoryName(strCategory);
				}
				nF.setStrDocumentName(strDocumentName);
				
				String alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				String alertAction = "DocumentListView.action";
				for(String strEmp : alEmp){
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					//Mail
					nF.setStrEmpId(strEmp.trim());
					nF.sendNotifications();
				}
				
				for(String strEmp : alSharePoc) {
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrOther("other");
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					//Mail
					pst = con.prepareStatement("select * from client_poc where poc_id = ?");
					pst.setInt(1, uF.parseToInt(strEmp.trim()));
					rs = pst.executeQuery();
					boolean flg=false;
					while(rs.next()) {
						nF.setStrCustFName(rs.getString("contact_fname"));
						nF.setStrCustLName(rs.getString("contact_lname"));
						nF.setStrEmpMobileNo(rs.getString("contact_number"));
						if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("contact_email"));
							nF.setStrEmailTo(rs.getString("contact_email"));
						}
						flg = true;
					}
					rs.close();
					pst.close();
					
					if(flg) {
						nF.setStrEmpId(strEmp.trim());
						nF.sendNotifications();
					}
				}
				/**
				 * Alerts End
				 * */
				
				
				
				String[] folderDocsTRId = request.getParameterValues("folderDocsTRId" + getFolderTRId()[i]);
//				String[] proFolderDocTasks = request.getParameterValues("proFolderDocTasks" + getFolderTRId()[i]);
				String[] folderDocDharingType = request.getParameterValues("folderDocDharingType" + getFolderTRId()[i]);
				String[] proCategoryTypeFolderDoc = request.getParameterValues("proCategoryTypeFolderDoc" + getFolderTRId()[i]);
				String[] strFolderDocDescription = request.getParameterValues("strFolderDocDescription" + getFolderTRId()[i]);
				String[] strFolderScopeDoc = request.getParameterValues("strFolderScopeDoc" + getFolderTRId()[i]);
				
				String[] isFolderDocEdit = request.getParameterValues("isFolderDocEdit" + getFolderTRId()[i]);
				String[] isFolderDocDelete = request.getParameterValues("isFolderDocDelete" + getFolderTRId()[i]);
				for(int j=0; folderDocsTRId != null && j<folderDocsTRId.length; j++) {
				
					String proFolderDocTasks = request.getParameter("proFolderDocTasks" + getFolderTRId()[i]+"_"+folderDocsTRId[j]);
					String proFolderDocCategory = request.getParameter("proFolderDocCategory" + getFolderTRId()[i]+"_"+folderDocsTRId[j]);
//					System.out.println("folderDocsTRId[j] ===>> " + folderDocsTRId[j]);
//					System.out.println("getFolderTRId()[i] ===>> " + getFolderTRId()[i]);
					double lengthBytes =  getStrFolderDoc()[docCnt].length();
					boolean isFileExist = false;
					
					String extenstion=FilenameUtils.getExtension(getStrFolderDocFileName()[docCnt]);	
					String strFileName = FilenameUtils.getBaseName(getStrFolderDocFileName()[docCnt]);
					strFileName = strFileName+"v1."+extenstion;
					
					File f = new File(proFolderNameFolder+"/"+strFileName);
					if(f.isFile()) {
					    System.out.println("isFile");
					    if(f.exists()) {
							isFileExist = true;
//						    System.out.println("exists");
						} else {
//						    System.out.println("exists fail");
						}   
					} else {
//					    System.out.println("isFile fail");
					}
//					if(f.isFile() && f.exists()){
//						isFileExist = true;
//					    System.out.println("success");
//					}
//					else{
//					    System.out.println("fail");
//					}
					if(lengthBytes > 0 && !isFileExist) {
						String[] proFolderDocEmployee = request.getParameterValues("proFolderDocEmployee" + getFolderTRId()[i]+"_"+folderDocsTRId[j]);
						List<String> alFDEmployee = null;
						if(proFolderDocEmployee != null) {
							alFDEmployee = Arrays.asList(proFolderDocEmployee);
						}
						StringBuilder sbFDEmps = null;
						
						for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
							if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
								if(sbFDEmps == null) {
									sbFDEmps = new StringBuilder();
									sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
								} else {
									sbFDEmps.append(alFDEmployee.get(a).trim() +",");
								}
							}
						}
						if(sbFDEmps == null) {
							sbFDEmps = new StringBuilder();
						}
						
						String[] proFolderDocPoc = request.getParameterValues("proFolderDocPoc" + getFolderTRId()[i]+"_"+folderDocsTRId[j]);
						List<String> alFDPoc = null;
						if(proFolderDocPoc != null) {
							alFDPoc = Arrays.asList(proFolderDocPoc);
						}
						StringBuilder sbFDPoc = null;
						
						for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
							if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
								if(sbFDPoc == null) {
									sbFDPoc = new StringBuilder();
									sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
								} else {
									sbFDPoc.append(alFDPoc.get(a).trim() +",");
								}
							}
						}
						if(sbFDPoc == null) {
							sbFDPoc = new StringBuilder();
						}
						
						
						pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
						"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, strFolderDocDescription[j]);
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(3, uF.parseToInt(getPro_id()));
						} else {
							pst.setInt(3, uF.parseToInt(proFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(folderDocDharingType[j]));
						pst.setString(8, sbFDEmps.toString());
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(11, strFileName);
						pst.executeUpdate();
						pst.close();
						
						String feedId = null;
						pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
						rs = pst.executeQuery();
						while(rs.next()) {
							feedId = rs.getString("communication_id");
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date," +
							"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description," +
							"doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getStrClient()));
						pst.setInt(2, uF.parseToInt(getPro_id()));
						pst.setString(3, getStrFolderName()[i]);
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(proFolderId));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(8, uF.parseToInt(proFolderDocTasks));
						} else {
							pst.setInt(8, uF.parseToInt(proFolderDocCategory));
						}
						pst.setInt(9, uF.parseToInt(folderDocDharingType[j]));
						pst.setString(10, sbFDEmps.toString());
						pst.setInt(11, uF.parseToInt(proCategoryTypeFolderDoc[j]));
						pst.setString(12, strFolderScopeDoc[j]);
						pst.setString(13, strFolderDocDescription[j]);
						pst.setInt(14, 0);
						pst.setInt(15, 1);
						pst.setBoolean(16, uF.parseToBoolean(isFolderDocEdit[j]));
						pst.setBoolean(17, uF.parseToBoolean(isFolderDocDelete[j]));
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							pst.setBoolean(18, true);
						} else {
							pst.setBoolean(18, false);
						}
						pst.setString(19, sbFDPoc.toString());
						pst.setInt(20, uF.parseToInt(feedId));
	//					System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
						
						
						String proDocumentId = "";
						pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
						rs = pst.executeQuery();
						while (rs.next()) {
							proDocumentId = rs.getString("pro_document_id");
						}
						rs.close();
						pst.close();
						
//						/**
//						 * Alerts
//						 * */
//						List<String> alEmp1= null;
//						if(proFolderDocEmployee != null) {
//							alEmp1 = Arrays.asList(proFolderDocEmployee);
//						}
//						if(alEmp1 == null){
//							alEmp1 = new ArrayList<String>();
//						}
//						
//						for(String strEmp : alEmp1){
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.setStrEmpId(strEmp.trim());
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
//							userAlerts.setStatus(INSERT_ALERT);
//							Thread t = new Thread(userAlerts);
//							t.run();
//						}
//						List<String> alSharePoc1 = null;
//						if(proFolderDocPoc != null) {
//							alSharePoc1 = Arrays.asList(proFolderDocPoc);
//						}
//						if(alSharePoc1 == null){
//							alSharePoc1 = new ArrayList<String>();
//						}
//						for(String strEmp : alSharePoc1){
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.setStrEmpId(strEmp.trim());
//							userAlerts.setStrOther("other");
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
//							userAlerts.setStatus(INSERT_ALERT);
//							Thread t = new Thread(userAlerts);
//							t.run();
//						}
						
						
						uploadProjectFolderDocuments(con, getPro_id(), getStrFolderDoc()[docCnt], strFileName, proFolderNameFolder, proDocumentId, feedId);
						
						/**
						 * Alerts
						 * */
						
						List<String> alEmp1= null;
						if(proFolderDocEmployee != null) {
							alEmp1 = Arrays.asList(proFolderDocEmployee);
						}
						if(alEmp1 == null){
							alEmp1 = new ArrayList<String>();
						}
						
						List<String> alSharePoc1 = null;
						if(proFolderDocPoc != null) {
							alSharePoc1 = Arrays.asList(proFolderDocPoc);
						}
						if(alSharePoc1 == null){
							alSharePoc1 = new ArrayList<String>();
						}
						
						strDocumentName = "";
						pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, uF.parseToInt(proDocumentId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						proName = null;
						strCategory = null;
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName = rs.getString("pro_name");
							}
							rs.close();
							pst.close();
						} else {
							pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocCategory));
							rs = pst.executeQuery();
							while(rs.next()) {
								strCategory = rs.getString("project_category");
							}
							rs.close();
							pst.close();
						}
						nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							nF.setStrProjectName(proName);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						alertAction = "DocumentListView.action";
						for(String strEmp : alEmp1){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
						
						for(String strEmp : alSharePoc1) {
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrOther("other");
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							pst = con.prepareStatement("select * from client_poc where poc_id = ?");
							pst.setInt(1, uF.parseToInt(strEmp.trim()));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()) {
								nF.setStrCustFName(rs.getString("contact_fname"));
								nF.setStrCustLName(rs.getString("contact_lname"));
								nF.setStrEmpMobileNo(rs.getString("contact_number"));
								if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
									nF.setStrEmpEmail(rs.getString("contact_email"));
									nF.setStrEmailTo(rs.getString("contact_email"));
								}
								flg = true;
							}
							rs.close();
							pst.close();
							
							if(flg) {
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
						}
						/**
						 * Alerts End
						 * */
						
						docCnt++;
					}
				}
				
				String[] SubFolderTR = request.getParameterValues("SubFolderTR" + getFolderTRId()[i]);
				String[] strSubFolderName = request.getParameterValues("strSubFolderName" + getFolderTRId()[i]);
				String[] proCategoryTypeSubFolder = request.getParameterValues("proCategoryTypeSubFolder" + getFolderTRId()[i]);
//				String[] proSubFolderTasks = request.getParameterValues("proSubFolderTasks" + getFolderTRId()[i]);
				String[] SubfolderSharingType = request.getParameterValues("SubfolderSharingType" + getFolderTRId()[i]);
				String[] strSubFolderDescription = request.getParameterValues("strSubFolderDescription" + getFolderTRId()[i]);
				
				String[] isSubFolderEdit = request.getParameterValues("isSubFolderEdit" + getFolderTRId()[i]);
				String[] isSubFolderDelete = request.getParameterValues("isSubFolderDelete" + getFolderTRId()[i]);
				for(int j = 0; SubFolderTR != null && j < SubFolderTR.length; j++) {
					
					String proSubFolderTasks = request.getParameter("proSubFolderTasks" + getFolderTRId()[i]+"_"+SubFolderTR[j]);
					String proSubFolderCategory = request.getParameter("proSubFolderCategory" + getFolderTRId()[i]+"_"+SubFolderTR[j]);
					
					String[] proSubFolderEmployee = request.getParameterValues("proSubFolderEmployee" + getFolderTRId()[i]+"_"+SubFolderTR[j]);
					List<String> alSubEmployee = null;
					if(proSubFolderEmployee != null) {
						alSubEmployee = Arrays.asList(proSubFolderEmployee);
					}
					StringBuilder sbSubEmps = null;
					
					for(int a=0; alSubEmployee != null && a<alSubEmployee.size(); a++) {
						if(alSubEmployee.get(a) != null && !alSubEmployee.get(a).trim().equals("")) {
							if(sbSubEmps == null) {
								sbSubEmps = new StringBuilder();
								sbSubEmps.append(","+ alSubEmployee.get(a).trim() +",");
							} else {
								sbSubEmps.append(alSubEmployee.get(a).trim() +",");
							}
						}
					}
					if(sbSubEmps == null) {
						sbSubEmps = new StringBuilder();
					}
					
					String[] proSubFolderPoc = request.getParameterValues("proSubFolderPoc" + getFolderTRId()[i]+"_"+SubFolderTR[j]);
					List<String> alSubPoc = null;
					if(proSubFolderPoc != null) {
						alSubPoc = Arrays.asList(proSubFolderPoc);
					}
					StringBuilder sbSubPoc = null;
					
					for(int a=0; alSubPoc != null && a<alSubPoc.size(); a++) {
						if(alSubPoc.get(a) != null && !alSubPoc.get(a).trim().equals("")) {
							if(sbSubPoc == null) {
								sbSubPoc = new StringBuilder();
								sbSubPoc.append(","+ alSubPoc.get(a).trim() +",");
							} else {
								sbSubPoc.append(alSubPoc.get(a).trim() +",");
							}
						}
					}
					if(sbSubPoc == null) {
						sbSubPoc = new StringBuilder();
					}
					
					String proSubFolderNameFolder = proFolderNameFolder +"/"+ strSubFolderName[j];
					
					File file3 = new File(proSubFolderNameFolder);
					if (!file3.exists()) {
						if (file3.mkdir()) {
//							System.out.println("Directory is created!");
						}
					}
					
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date," +
						"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description," +
						"doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc)" +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrClient()));
					pst.setInt(2, uF.parseToInt(getPro_id()));
					pst.setString(3, strSubFolderName[j]);
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(proFolderId));
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst.setInt(8, uF.parseToInt(proSubFolderTasks));
					} else {
						pst.setInt(8, uF.parseToInt(proSubFolderCategory));
					}
					pst.setInt(9, uF.parseToInt(SubfolderSharingType[j]));
					pst.setString(10, sbSubEmps.toString());
					pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolder[j]));
					pst.setString(12, null);
					pst.setString(13, strSubFolderDescription[j]);
					pst.setInt(14, 0);
					pst.setInt(15, 0);
					pst.setBoolean(16, uF.parseToBoolean(isSubFolderEdit[j]));
					pst.setBoolean(17, uF.parseToBoolean(isSubFolderDelete[j]));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(18, true);
					} else {
						pst.setBoolean(18, false);
					}
					pst.setString(19, sbSubPoc.toString());
			//		System.out.println("pst====>"+pst);
					pst.execute();
					pst.close();
			
					String proSubFolderId = "";
					pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
					rs = pst.executeQuery();
					while (rs.next()) {
						proSubFolderId = rs.getString("pro_document_id");
					}
					rs.close();
					pst.close();
					
//					/**
//					 * Alerts
//					 * */
//					List<String> alEmp1= null;
//					if(proSubFolderEmployee != null) {
//						alEmp1 = Arrays.asList(proSubFolderEmployee);
//					}
//					if(alEmp1 == null){
//						alEmp1 = new ArrayList<String>();
//					}
//					
//					for(String strEmp : alEmp1){
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(strEmp.trim());
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
//					}
//					List<String> alSharePoc1 = null;
//					if(proSubFolderPoc != null) {
//						alSharePoc1 = Arrays.asList(proSubFolderPoc);
//					}
//					if(alSharePoc1 == null){
//						alSharePoc1 = new ArrayList<String>();
//					}
//					for(String strEmp : alSharePoc1){
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(strEmp.trim());
//						userAlerts.setStrOther("other");
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
//					}
					
					
					/**
					 * Alerts
					 * */
					
					List<String> alEmp1= null;
					if(proSubFolderEmployee != null) {
						alEmp1 = Arrays.asList(proSubFolderEmployee);
					}
					if(alEmp1 == null){
						alEmp1 = new ArrayList<String>();
					}
					
					List<String> alSharePoc1 = null;
					if(proSubFolderPoc != null) {
						alSharePoc1 = Arrays.asList(proSubFolderPoc);
					}
					if(alSharePoc1 == null){
						alSharePoc1 = new ArrayList<String>();
					}
					
					strDocumentName = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(proSubFolderId));
					rs = pst.executeQuery();
					while (rs.next()) {
						strDocumentName = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					proName = null;
					strCategory = null;
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderCategory));
						rs = pst.executeQuery();
						while(rs.next()) {
							strCategory = rs.getString("project_category");
						}
						rs.close();
						pst.close();
					}
					nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						nF.setStrProjectName(proName);
					} else {
						nF.setStrCategoryName(strCategory);
					}
					nF.setStrDocumentName(strDocumentName);
					
					alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					alertAction = "DocumentListView.action";
					for(String strEmp : alEmp1){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						nF.setStrEmpId(strEmp.trim());
						nF.sendNotifications();
					}
					for(String strEmp : alSharePoc1){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrOther("other");
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						pst = con.prepareStatement("select * from client_poc where poc_id = ?");
						pst.setInt(1, uF.parseToInt(strEmp.trim()));
						rs = pst.executeQuery();
						boolean flg=false;
						while(rs.next()) {
							nF.setStrCustFName(rs.getString("contact_fname"));
							nF.setStrCustLName(rs.getString("contact_lname"));
							nF.setStrEmpMobileNo(rs.getString("contact_number"));
							if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
								nF.setStrEmpEmail(rs.getString("contact_email"));
								nF.setStrEmailTo(rs.getString("contact_email"));
							}
							flg = true;
						}
						rs.close();
						pst.close();
						
						if(flg) {
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
					}
					/**
					 * Alerts End
					 * */
					
//					System.out.println("SubFolderTR[j] ====>> " + SubFolderTR[j]);
					String[] SubfolderDocsTRId = request.getParameterValues("SubfolderDocsTRId"+SubFolderTR[j]);
//					System.out.println("SubfolderDocsTRId ====>> " + SubfolderDocsTRId.length);
					File[] files = mpRequest.getFiles("strSubFolderDoc"+SubFolderTR[j]);    //  
					String[] fileNames = mpRequest.getFileNames("strSubFolderDoc"+SubFolderTR[j]); 
					String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc" +SubFolderTR[j]);
//					String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks" +SubFolderTR[j]);
					String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType" + SubFolderTR[j]);
					String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription" +SubFolderTR[j]);
					String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc" + SubFolderTR[j]);
					
					String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit" + SubFolderTR[j]);
					String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete" + SubFolderTR[j]);
					
					for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
						
						String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks" +SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory" +SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						
						double lengthBytes =  files[k].length();
						boolean isFileExist = false;
//						System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
						
						String extenstion=FilenameUtils.getExtension(fileNames[k]);	
						String strFileName = FilenameUtils.getBaseName(fileNames[k]);
						strFileName = strFileName+"v1."+extenstion;
						
						File f = new File(proSubFolderNameFolder+"/"+strFileName);
						if(f.isFile()) {
//						    System.out.println("isFile");
						    if(f.exists()) {
								isFileExist = true;
//							    System.out.println("exists");
							} else {
//							    System.out.println("exists fail");
							}   
						} else {
//						    System.out.println("isFile fail");
						}
						
						if(lengthBytes > 0 && !isFileExist) {
							String[] proSubFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee" +SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDEmployee = null;
							if(proSubFolderDocEmployee != null) {
								alFDEmployee = Arrays.asList(proSubFolderDocEmployee);
							}
							StringBuilder sbFDEmps = null;
							
							for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
								if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
									if(sbFDEmps == null) {
										sbFDEmps = new StringBuilder();
										sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
									} else {
										sbFDEmps.append(alFDEmployee.get(a).trim() +",");
									}
								}
							}
							if(sbFDEmps == null) {
								sbFDEmps = new StringBuilder();
							}
							
							String[] proSubFolderDocPoc = request.getParameterValues("proSubFolderDocPoc" +SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDPoc = null;
							if(proSubFolderDocPoc != null) {
								alFDPoc = Arrays.asList(proSubFolderDocPoc);
							}
							StringBuilder sbFDPoc = null;
							
							for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
								if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
									if(sbFDPoc == null) {
										sbFDPoc = new StringBuilder();
										sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
									} else {
										sbFDPoc.append(alFDPoc.get(a).trim() +",");
									}
								}
							}
							if(sbFDPoc == null) {
								sbFDPoc = new StringBuilder();
							}
							
							
							pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
							"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setString(1, strSubFolderDocDescription[k]);
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(3, uF.parseToInt(getPro_id()));
							} else {
								pst.setInt(3, uF.parseToInt(proSubFolderDocCategory));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbFDEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(SubfolderDocDharingType[k]));
							pst.setString(8, sbFDEmps.toString());
							pst.setInt(9, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
							pst.setString(11, strFileName);
							pst.executeUpdate();
							pst.close();
							
							String feedId = null;
							pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
							rs = pst.executeQuery();
							while(rs.next()) {
								feedId = rs.getString("communication_id");
							}
							rs.close();
							pst.close();
							
							
							pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
								"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
								"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getStrClient()));
							pst.setInt(2, uF.parseToInt(getPro_id()));
							pst.setString(3, strSubFolderName[j]);
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
//							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
							pst.setString(6, "folder");
							pst.setInt(7, uF.parseToInt(proSubFolderId));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(8, uF.parseToInt(proSubFolderDocTasks));
							} else {
								pst.setInt(8, uF.parseToInt(proSubFolderDocCategory));
							}
							pst.setInt(9, uF.parseToInt(SubfolderDocDharingType[k]));
							pst.setString(10, sbFDEmps.toString());
							pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolderDoc[k]));
							pst.setString(12, strSubFolderScopeDoc[k]);
							pst.setString(13, strSubFolderDocDescription[k]);
							pst.setInt(14, 0);
							pst.setInt(15, 1);
							pst.setBoolean(16, uF.parseToBoolean(isSubFolderDocEdit[k]));
							pst.setBoolean(17, uF.parseToBoolean(isSubFolderDocDelete[k]));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(18, true);
							} else {
								pst.setBoolean(18, false);
							}
							pst.setString(19, sbFDPoc.toString());
							pst.setInt(20, uF.parseToInt(feedId));
		//					System.out.println("pst====>"+pst);
							pst.execute();
							pst.close();
							
							
							String proDocumentId = "";
							pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								proDocumentId = rs.getString("pro_document_id");
							}
							rs.close();
							pst.close();
							
//							/**
//							 * Alerts
//							 * */
//							List<String> alEmp11= null;
//							if(proSubFolderDocEmployee != null) {
//								alEmp11 = Arrays.asList(proSubFolderDocEmployee);
//							}
//							if(alEmp11 == null){
//								alEmp11 = new ArrayList<String>();
//							}
//							
//							for(String strEmp : alEmp11){
//								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//								userAlerts.setStrDomain(strDomain);
//								userAlerts.setStrEmpId(strEmp.trim());
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
//								userAlerts.setStatus(INSERT_ALERT);
//								Thread t = new Thread(userAlerts);
//								t.run();
//							}
//							List<String> alSharePoc11 = null;
//							if(proSubFolderDocPoc != null) {
//								alSharePoc11 = Arrays.asList(proSubFolderDocPoc);
//							}
//							if(alSharePoc11 == null){
//								alSharePoc11 = new ArrayList<String>();
//							}
//							for(String strEmp : alSharePoc11){
//								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//								userAlerts.setStrDomain(strDomain);
//								userAlerts.setStrEmpId(strEmp.trim());
//								userAlerts.setStrOther("other");
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
//								userAlerts.setStatus(INSERT_ALERT);
//								Thread t = new Thread(userAlerts);
//								t.run();
//							}
							
							uploadProjectFolderDocuments(con, getPro_id(), files[k], strFileName, proSubFolderNameFolder, proDocumentId, feedId);
							
							/**
							 * Alerts
							 * */
							
							List<String> alEmp11= null;
							if(proSubFolderDocEmployee != null) {
								alEmp11 = Arrays.asList(proSubFolderDocEmployee);
							}
							if(alEmp11 == null){
								alEmp11 = new ArrayList<String>();
							}
							
							List<String> alSharePoc11 = null;
							if(proSubFolderDocPoc != null) {
								alSharePoc11 = Arrays.asList(proSubFolderDocPoc);
							}
							if(alSharePoc11 == null){
								alSharePoc11 = new ArrayList<String>();
							}
							
							strDocumentName = "";
							pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
							pst.setInt(1, uF.parseToInt(proDocumentId));
							rs = pst.executeQuery();
							while (rs.next()) {
								strDocumentName = rs.getString("folder_name");
							}
							rs.close();
							pst.close();
							
							proName = null;
							strCategory = null;
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName = rs.getString("pro_name");
								}
								rs.close();
								pst.close();
							} else {
								pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocCategory));
								rs = pst.executeQuery();
								while(rs.next()) {
									strCategory = rs.getString("project_category");
								}
								rs.close();
								pst.close();
							}
							nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
							nF.setDomain(strDomain);
							
							nF.request = request;
							nF.setStrOrgId((String)session.getAttribute(ORGID));
							nF.setEmailTemplate(true);
							
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								nF.setStrProjectName(proName);
							} else {
								nF.setStrCategoryName(strCategory);
							}
							nF.setStrDocumentName(strDocumentName);
							
							alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
							alertAction = "DocumentListView.action";
							for(String strEmp : alEmp11){
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
							for(String strEmp : alSharePoc11){
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrOther("other");
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								pst = con.prepareStatement("select * from client_poc where poc_id = ?");
								pst.setInt(1, uF.parseToInt(strEmp.trim()));
								rs = pst.executeQuery();
								boolean flg=false;
								while(rs.next()) {
									nF.setStrCustFName(rs.getString("contact_fname"));
									nF.setStrCustLName(rs.getString("contact_lname"));
									nF.setStrEmpMobileNo(rs.getString("contact_number"));
									if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
										nF.setStrEmpEmail(rs.getString("contact_email"));
										nF.setStrEmailTo(rs.getString("contact_email"));
									}
									flg = true;
								}
								rs.close();
								pst.close();
								
								if(flg) {
									nF.setStrEmpId(strEmp.trim());
									nF.sendNotifications();
								}
							}
							/**
							 * Alerts End
							 * */
							
							docCnt++;
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void uploadProjectFolderDocuments(Connection con, String proId, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

		PreparedStatement pst = null;
		try {
			
			double lengthBytes =  contentFile.length();
			
			String ext = FilenameUtils.getExtension(realFolderPath+"/"+contentFileName);
			UtilityFunctions uF = new UtilityFunctions();
			
			String fileType = uF.getFileTypeOnExtension(ext);
			String fileSize = uF.getFileTypeSize(lengthBytes);
			
			pst = con.prepareStatement("update project_document_details set file_size=?, file_type=?, size_in_bytes=? where pro_document_id=?");
			pst.setString(1, fileSize);
			pst.setString(2, fileType);
			pst.setString(3, lengthBytes+"");
			pst.setInt(4, uF.parseToInt(proDocumentId));
			pst.execute();
			pst.close();
			
			UploadProjectDocuments upd = new UploadProjectDocuments();
			upd.setServletRequest(request);
			upd.setDocType("PROJECT_FOLDER_DOCUMENTS");
			upd.setDocumentFile(contentFile);
			upd.setDocumentFileFileName(contentFileName);
			upd.setRealFolderPath(realFolderPath);
			upd.setProId(proId);
			upd.setProDocumentId(proDocumentId);
			upd.setFeedId(feedId);
			upd.setCF(CF);
//			System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
			upd.uploadProjectDocuments();
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
	
	
	private void uploadProjectDocuments(Connection con, String proId, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

		PreparedStatement pst = null;
		try {
			
			double lengthBytes =  contentFile.length();
			
			String ext = FilenameUtils.getExtension(realFolderPath+"/"+contentFileName);
			UtilityFunctions uF = new UtilityFunctions();
			
			String fileType = uF.getFileTypeOnExtension(ext);
			String fileSize = uF.getFileTypeSize(lengthBytes);
			
			pst = con.prepareStatement("update project_document_details set file_size=?, file_type=?, size_in_bytes=? where pro_document_id=?");
			pst.setString(1, fileSize);
			pst.setString(2, fileType);
			pst.setString(3, lengthBytes+"");
			pst.setInt(4, uF.parseToInt(proDocumentId));
			pst.execute();
			pst.close();
			
			UploadProjectDocuments upd = new UploadProjectDocuments();
			upd.setServletRequest(request);
			upd.setDocType("PROJECT_DOCUMENTS");
			upd.setDocumentFile(contentFile);
			upd.setDocumentFileFileName(contentFileName);
			upd.setRealFolderPath(realFolderPath);
			upd.setProId(proId);
			upd.setProDocumentId(proDocumentId);
			upd.setFeedId(feedId);
			upd.setCF(CF);
//			System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
			upd.uploadProjectDocuments();
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
	
	
	String[] strAllEmpId;
	String[] strEmpId;
	String[] strTeamLeadId;
	String[] rate;
	String[] actualRate;

	public void saveProjectInfoStep2(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from  project_resource_req_details where pro_id=? and res_req_create_status=false");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			List<List<String>> proResReqData= new ArrayList<List<String>>();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_id")); 
				innerList.add(rs.getString("skill_id")); //1
				innerList.add(rs.getString("wloc_ids"));
				innerList.add(rs.getString("min_exp")); //3
				innerList.add(rs.getString("max_exp"));
				innerList.add(rs.getString("req_resource")); //5
				innerList.add(rs.getString("resource_gap"));
				innerList.add(rs.getString("added_by")); //7
				innerList.add(rs.getString("entry_date"));
				innerList.add(rs.getString("updated_by")); //9
				innerList.add(rs.getString("update_date"));
				innerList.add(rs.getString("project_resource_req_id")); //11
				proResReqData.add(innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("proResReqData ===>> " + proResReqData);
			
			Map<String, String> hmProData = CF.getProjectDetailsByProId(con, getPro_id());
		
			String [] fYarr = CF.getFinancialYear(con, hmProData.get("PRO_START_DATE"), CF, uF);
			int intMonth = uF.parseToInt(uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT, "MM"));
			int intYear = uF.parseToInt(uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT, "yyyy"));
			
			List<String> alProResReqId = new ArrayList<String>();
			StringBuilder sbProResReqIds = null;
			for(int i=0; proResReqData!=null && i<proResReqData.size(); i++) {
				List<String> innerList = proResReqData.get(i);
				for(int j=0; j<uF.parseToDouble(innerList.get(6)); j++) {
					pst = con.prepareStatement("insert into  resource_plan_request_details (pro_id,pro_start_date,skill_id,wloc_ids,min_exp,max_exp," +
						"req_res,res_gap,req_month,req_year,fy_start,fy_end,requested_by,request_date,added_by,entry_date,project_resource_req_id) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getPro_id()));
					pst.setDate(2, uF.getDateFormat(hmProData.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(innerList.get(1)));
					pst.setString(4, innerList.get(2));
					pst.setDouble(5, uF.parseToDouble(innerList.get(3)));
					pst.setDouble(6, uF.parseToDouble(innerList.get(4)));
					pst.setDouble(7, uF.parseToDouble(innerList.get(5)));
					pst.setDouble(8, 1);
					pst.setInt(9, intMonth);
					pst.setInt(10, intYear);
					pst.setDate(11, uF.getDateFormat(fYarr[0], DATE_FORMAT));
					pst.setDate(12, uF.getDateFormat(fYarr[1], DATE_FORMAT));
					pst.setInt(13, uF.parseToInt(innerList.get(9))>0 ? uF.parseToInt(innerList.get(9)) : uF.parseToInt(innerList.get(7)));
					String reqDate = innerList.get(10)!=null ? innerList.get(10) : innerList.get(8);
					pst.setTimestamp(14, uF.getTimeStamp(reqDate, DBTIMESTAMP));
					pst.setInt(15, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(16, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
					pst.setInt(17, uF.parseToInt(innerList.get(11)));
					pst.executeUpdate();
					pst.close();
//					System.out.println("PANP/8077---pst ===>> " + pst);
					
					if(!alProResReqId.contains(innerList.get(11))) {
						alProResReqId.add(innerList.get(11));
						if(sbProResReqIds==null) {
							sbProResReqIds = new StringBuilder();
							sbProResReqIds.append(innerList.get(11));
						} else {
							sbProResReqIds.append(","+innerList.get(11));
						}
					}
				}
			}
			
			if(sbProResReqIds!=null) {
				pst = con.prepareStatement("update project_resource_req_details set res_req_create_status=?,req_created_by=?,req_create_date=? where project_resource_req_id in ("+sbProResReqIds.toString()+") ");
				pst.setBoolean(1, true);
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.executeUpdate();
				pst.close();
//				System.out.println("sbProResReqIds - pst ===>> " + pst);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//===created by parvez date: 11-10-2022===
	//===start===
	public String getData(List<String> list) {
		StringBuilder a=null;
        if (list != null) {
            for (String b : list) {
                if (a == null) {
//                	a = new StringBuilder(b);
                    a = new StringBuilder(","+b+",");
                } else {
//                    a.append("," + b);
                	a.append(b + ",");
                }
            }
        }
        if (a == null)
            return null;
		return a.toString();
	}
//===end===	

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public List<GetPriorityList> getPriorityList() {
		return priorityList;
	}

	public void setPriorityList(List<GetPriorityList> priorityList) {
		this.priorityList = priorityList;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<Integer> getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(List<Integer> emp_id) {
		this.emp_id = emp_id;
	}

	public List<String> getAlInner() {
		return alInner;
	}

	public void setAlInner(List<String> alInner) {
		this.alInner = alInner;
	}

	public List<FillSkills> getSkillList() {
		return skillList;
	}
       
	public void setSkillList(List<FillSkills> skillList) {
		this.skillList = skillList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public List<FillDaysList> getDaysList() {
		return daysList;
	}

	public void setDaysList(List<FillDaysList> daysList) {
		this.daysList = daysList;
	}

	public List<FillEmployee> getTeamleadNamesList() {
		return teamleadNamesList;
	}

	public void setTeamleadNamesList(List<FillEmployee> teamleadNamesList) {
		this.teamleadNamesList = teamleadNamesList;
	}

	public String getStrClient() {
		return strClient;
	}

	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}

	public List<FillClientPoc> getClientPocList() {
		return clientPocList;
	}

	public void setClientPocList(List<FillClientPoc> clientPocList) {
		this.clientPocList = clientPocList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public String[] getLevel() {
		return level;
	}

	public void setLevel(String []level) {
		this.level = level;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<FillBillingType> getBillingList() {
		return billingList;
	}

	public void setBillingList(List<FillBillingType> billingList) {
		this.billingList = billingList;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getPrjectname() {
		return prjectname;
	}

	public void setPrjectname(String prjectname) {
		this.prjectname = prjectname;
	}

	public String getPrjectCode() {
		return prjectCode;
	}

	public void setPrjectCode(String prjectCode) {
		this.prjectCode = prjectCode;
	}

	public String getClientPoc() {
		return clientPoc;
	}

	public void setClientPoc(String clientPoc) {
		this.clientPoc = clientPoc;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getFolderTRId() {
		return folderTRId;
	}

	public void setFolderTRId(String[] folderTRId) {
		this.folderTRId = folderTRId;
	}

	public String[] getStrFolderName() {
		return strFolderName;
	}

	public void setStrFolderName(String[] strFolderName) {
		this.strFolderName = strFolderName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getBillingTypeName() {
		return billingTypeName;
	}

	public void setBillingTypeName(String billingTypeName) {
		this.billingTypeName = billingTypeName;
	}

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	public String getBillingAmountF() {
		return billingAmountF;
	}

	public void setBillingAmountF(String billingAmountF) {
		this.billingAmountF = billingAmountF;
	}

	public String getBillingAmountH() {
		return billingAmountH;
	}

	public void setBillingAmountH(String billingAmountH) {
		this.billingAmountH = billingAmountH;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getHoursToDay() {
		return hoursToDay;
	}

	public void setHoursToDay(String hoursToDay) {
		this.hoursToDay = hoursToDay;
	}

	public String getHoursForDay() {
		return hoursForDay;
	}

	public void setHoursForDay(String hoursForDay) {
		this.hoursForDay = hoursForDay;
	}

	public String[] getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String[] strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String[] getStrTeamLeadId() {
		return strTeamLeadId;
	}

	public void setStrTeamLeadId(String[] strTeamLeadId) {
		this.strTeamLeadId = strTeamLeadId;
	}

	public String[] getRate() {
		return rate;
	}

	public void setRate(String[] rate) {
		this.rate = rate;
	}

	public String[] getStrAllEmpId() {
		return strAllEmpId;
	}

	public void setStrAllEmpId(String[] strAllEmpId) {
		this.strAllEmpId = strAllEmpId;
	}

	public String[] getActualRate() {
		return actualRate;
	}

	public void setActualRate(String[] actualRate) {
		this.actualRate = actualRate;
	}

	public String getStrActualBillingName() {
		return strActualBillingName;
	}

	public void setStrActualBillingName(String strActualBillingName) {
		this.strActualBillingName = strActualBillingName;
	}

	public String getStrActualBilling() {
		return strActualBilling;
	}

	public void setStrActualBilling(String strActualBilling) {
		this.strActualBilling = strActualBilling;
	}

	public String[] getTaskTRId() {
		return taskTRId;
	}

	public void setTaskTRId(String[] taskTRId) {
		this.taskTRId = taskTRId;
	}

	public String[] getTaskId() {
		return taskId;
	}

	public void setTaskId(String[] taskId) {
		this.taskId = taskId;
	}

	public File[] getTaskDoc() {
		return taskDoc;
	}

	public void setTaskDoc(File[] taskDoc) {
		this.taskDoc = taskDoc;
	}

	public String[] getTaskDocFileName() {
		return taskDocFileName;
	}

	public void setTaskDocFileName(String[] taskDocFileName) {
		this.taskDocFileName = taskDocFileName;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String []f_service) {
		this.f_service = f_service;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String []f_level) {
		this.f_level = f_level;
	}

	public String[] getF_wLocation() {
		return f_wLocation;
	}

	public void setF_wLocation(String []f_wLocation) {
		this.f_wLocation = f_wLocation;
	}

	public String[] getSkill() {
		return skill;
	}

	public void setSkill(String[] skill) {
		this.skill = skill;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	public List<FillDependentTaskList> getDependencyList() {
		return dependencyList;
	}

	public void setDependencyList(List<FillDependentTaskList> dependencyList) {
		this.dependencyList = dependencyList;
	}

	public List<GetDependancyTypeList> getDependancyTypeList() {
		return dependancyTypeList;
	}

	public void setDependancyTypeList(List<GetDependancyTypeList> dependancyTypeList) {
		this.dependancyTypeList = dependancyTypeList;
	}

	public List<FillTaskEmpList> getTaskEmpNamesList() {
		return TaskEmpNamesList;
	}

	public void setTaskEmpNamesList(List<FillTaskEmpList> taskEmpNamesList) {
		TaskEmpNamesList = taskEmpNamesList;
	}

	public List<FillSkills> getEmpSkillList() {
		return empSkillList;
	}

	public void setEmpSkillList(List<FillSkills> empSkillList) {
		this.empSkillList = empSkillList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public List<FillBillingType> getBillingKindList() {
		return billingKindList;
	}

	public void setBillingKindList(List<FillBillingType> billingKindList) {
		this.billingKindList = billingKindList;
	}

	public List<FillBillingHeads> getBillingHeadDataTypeList() {
		return billingHeadDataTypeList;
	}

	public void setBillingHeadDataTypeList(List<FillBillingHeads> billingHeadDataTypeList) {
		this.billingHeadDataTypeList = billingHeadDataTypeList;
	}

	public List<FillBillingHeads> getBillingHeadOtherVariableList() {
		return billingHeadOtherVariableList;
	}

	public void setBillingHeadOtherVariableList(List<FillBillingHeads> billingHeadOtherVariableList) {
		this.billingHeadOtherVariableList = billingHeadOtherVariableList;
	}

	public String[] getTaxHeadId() {
		return taxHeadId;
	}

	public void setTaxHeadId(String[] taxHeadId) {
		this.taxHeadId = taxHeadId;
	}

	public String[] getmTaxHeadId() {
		return mTaxHeadId;
	}

	public void setmTaxHeadId(String[] mTaxHeadId) {
		this.mTaxHeadId = mTaxHeadId;
	}

	public String[] getTaxHeadLabel() {
		return taxHeadLabel;
	}

	public void setTaxHeadLabel(String[] taxHeadLabel) {
		this.taxHeadLabel = taxHeadLabel;
	}

	public String[] getTaxNameLabel() {
		return taxNameLabel;
	}

	public void setTaxNameLabel(String[] taxNameLabel) {
		this.taxNameLabel = taxNameLabel;
	}

	public String[] getTaxHeadPercent() {
		return taxHeadPercent;
	}

	public void setTaxHeadPercent(String[] taxHeadPercent) {
		this.taxHeadPercent = taxHeadPercent;
	}

	public String[] getmBillingHeadId() {
		return mBillingHeadId;
	}

	public void setmBillingHeadId(String[] mBillingHeadId) {
		this.mBillingHeadId = mBillingHeadId;
	}

	public String[] getTaxHeadDeductionType() {
		return taxHeadDeductionType;
	}

	public void setTaxHeadDeductionType(String[] taxHeadDeductionType) {
		this.taxHeadDeductionType = taxHeadDeductionType;
	}

	public String[] getTaxHeadStatus() {
		return taxHeadStatus;
	}

	public void setTaxHeadStatus(String[] taxHeadStatus) {
		this.taxHeadStatus = taxHeadStatus;
	}

	public String getStrInvoiceTemplate() {
		return strInvoiceTemplate;
	}

	public void setStrInvoiceTemplate(String strInvoiceTemplate) {
		this.strInvoiceTemplate = strInvoiceTemplate;
	}

	public String getStrBillingKindName() {
		return strBillingKindName;
	}

	public void setStrBillingKindName(String strBillingKindName) {
		this.strBillingKindName = strBillingKindName;
	}

	public String getStrBillingKind() {
		return strBillingKind;
	}

	public void setStrBillingKind(String strBillingKind) {
		this.strBillingKind = strBillingKind;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String []f_department) {
		this.f_department = f_department;
	}

	public List<FillEmployee> getProjectOwnerList() {
		return projectOwnerList;
	}

	public void setProjectOwnerList(List<FillEmployee> projectOwnerList) {
		this.projectOwnerList = projectOwnerList;
	}

	/*public String getStrProjectOwner() {
		return strProjectOwner;
	}

	public void setStrProjectOwner(String strProjectOwner) {
		this.strProjectOwner = strProjectOwner;
	}*/

	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
	}

	public String getStrCurrency() {
		return strCurrency;
	}

	public void setStrCurrency(String strCurrency) {
		this.strCurrency = strCurrency;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public List<FillServices> getSbuList() {
		return sbuList;
	}

	public void setSbuList(List<FillServices> sbuList) {
		this.sbuList = sbuList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getStrSBU() {
		return strSBU;
	}

	public void setStrSBU(String strSBU) {
		this.strSBU = strSBU;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getStrBillingCurrency() {
		return strBillingCurrency;
	}

	public void setStrBillingCurrency(String strBillingCurrency) {
		this.strBillingCurrency = strBillingCurrency;
	}

	public String[] getDocCountId() {
		return docCountId;
	}

	public void setDocCountId(String[] docCountId) {
		this.docCountId = docCountId;
	}

	public File[] getStrFolderDoc() {
		return strFolderDoc;
	}

	public void setStrFolderDoc(File[] strFolderDoc) {
		this.strFolderDoc = strFolderDoc;
	}

	public String[] getStrFolderDocFileName() {
		return strFolderDocFileName;
	}

	public void setStrFolderDocFileName(String[] strFolderDocFileName) {
		this.strFolderDocFileName = strFolderDocFileName;
	}

	public File[] getStrDoc() {
		return strDoc;
	}

	public void setStrDoc(File[] strDoc) {
		this.strDoc = strDoc;
	}

	public String[] getStrDocsCount() {
		return strDocsCount;
	}

	public void setStrDocsCount(String[] strDocsCount) {
		this.strDocsCount = strDocsCount;
	}

	public String[] getStrDocFileName() {
		return strDocFileName;
	}

	public void setStrDocFileName(String[] strDocFileName) {
		this.strDocFileName = strDocFileName;
	}

	public String[] getMilestoneId() {
		return milestoneId;
	}

	public void setMilestoneId(String[] milestoneId) {
		this.milestoneId = milestoneId;
	}

	public String[] getMilestoneName() {
		return milestoneName;
	}

	public void setMilestoneName(String[] milestoneName) {
		this.milestoneName = milestoneName;
	}

	public String[] getMilestoneDescription() {
		return milestoneDescription;
	}

	public void setMilestoneDescription(String[] milestoneDescription) {
		this.milestoneDescription = milestoneDescription;
	}

	public String[] getProjectTask() {
		return projectTask;
	}

	public void setProjectTask(String[] projectTask) {
		this.projectTask = projectTask;
	}

	public String[] getMilestonePercent() {
		return milestonePercent;
	}

	public void setMilestonePercent(String[] milestonePercent) {
		this.milestonePercent = milestonePercent;
	}

	public String[] getMilestoneAmount() {
		return milestoneAmount;
	}

	public void setMilestoneAmount(String[] milestoneAmount) {
		this.milestoneAmount = milestoneAmount;
	}

	public String[] getBillingHeadId() {
		return billingHeadId;
	}

	public void setBillingHeadId(String[] billingHeadId) {
		this.billingHeadId = billingHeadId;
	}

	public String[] getBillingHeadTRId() {
		return billingHeadTRId;
	}

	public void setBillingHeadTRId(String[] billingHeadTRId) {
		this.billingHeadTRId = billingHeadTRId;
	}

	public String[] getBillingHeadLabel() {
		return billingHeadLabel;
	}

	public void setBillingHeadLabel(String[] billingHeadLabel) {
		this.billingHeadLabel = billingHeadLabel;
	}

	public String[] getBillingHeadDataType() {
		return billingHeadDataType;
	}

	public void setBillingHeadDataType(String[] billingHeadDataType) {
		this.billingHeadDataType = billingHeadDataType;
	}

	public String getMilestoneDependentOn() {
		return milestoneDependentOn;
	}

	public void setMilestoneDependentOn(String milestoneDependentOn) {
		this.milestoneDependentOn = milestoneDependentOn;
	}

	public String getWeekdayCycle() {
		return weekdayCycle;
	}

	public void setWeekdayCycle(String weekdayCycle) {
		this.weekdayCycle = weekdayCycle;
	}

	public String getDayCycle() {
		return dayCycle;
	}

	public void setDayCycle(String dayCycle) {
		this.dayCycle = dayCycle;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillInvoiceFormat> getInvoiceTemplateList() {
		return invoiceTemplateList;
	}

	public void setInvoiceTemplateList(List<FillInvoiceFormat> invoiceTemplateList) {
		this.invoiceTemplateList = invoiceTemplateList;
	}

	public String getInvoiceAdditionalInfo() {
		return invoiceAdditionalInfo;
	}

	public void setInvoiceAdditionalInfo(String invoiceAdditionalInfo) {
		this.invoiceAdditionalInfo = invoiceAdditionalInfo;
	}

	public String getStepSave() {
		return stepSave;
	}

	public void setStepSave(String stepSave) {
		this.stepSave = stepSave;
	}
	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getStrTaskId() {
		return strTaskId;
	}

	public void setStrTaskId(String strTaskId) {
		this.strTaskId = strTaskId;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getChkBank() {
		return chkBank;
	}

	public void setChkBank(String chkBank) {
		this.chkBank = chkBank;
	}

	public String getChkPaypal() {
		return chkPaypal;
	}

	public void setChkPaypal(String chkPaypal) {
		this.chkPaypal = chkPaypal;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getStrPaypal() {
		return strPaypal;
	}

	public void setStrPaypal(String strPaypal) {
		this.strPaypal = strPaypal;
	}

	public String getStrAccountRef() {
		return strAccountRef;
	}

	public void setStrAccountRef(String strAccountRef) {
		this.strAccountRef = strAccountRef;
	}

	public String getStrPONo() {
		return strPONo;
	}

	public void setStrPONo(String strPONo) {
		this.strPONo = strPONo;
	}

	public String getStrTerms() {
		return strTerms;
	}

	public void setStrTerms(String strTerms) {
		this.strTerms = strTerms;
	}

	public String getStrDueDate() {
		return strDueDate;
	}

	public void setStrDueDate(String strDueDate) {
		this.strDueDate = strDueDate;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}
	
	public List<FillEmployee> getProjectReferanceList() {
		return projectReferanceList;
	}
	
	public void setProjectReferanceList(List<FillEmployee> projectReferanceList) {
		this.projectReferanceList = projectReferanceList;
	}
	
	public List<FillEmployee> getProjectRelationShipList() {
		return projectRelationShipList;
	}
	
	public void setProjectRelationShipList(List<FillEmployee> projectRelationShipList) {
		this.projectRelationShipList = projectRelationShipList;
	}
	
	public String getStrReferanceBy() {
		return strReferanceBy;
	}
	
	public void setStrReferanceBy(String strReferanceBy) {
		this.strReferanceBy = strReferanceBy;
	}
	
	public String getStrRelationShipBy() {
		return strRelationShipBy;
	}

	public void setStrRelationShipBy(String strRelationShipBy) {
		this.strRelationShipBy = strRelationShipBy;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getStrProOwnerOrTL() {
		return strProOwnerOrTL;
	}

	public void setStrProOwnerOrTL(String strProOwnerOrTL) {
		this.strProOwnerOrTL = strProOwnerOrTL;
	}

	public List<FillClientBrand> getClientBrandList() {
		return clientBrandList;
	}

	public void setClientBrandList(List<FillClientBrand> clientBrandList) {
		this.clientBrandList = clientBrandList;
	}

	public String getStrClientBrand() {
		return strClientBrand;
	}

	public void setStrClientBrand(String strClientBrand) {
		this.strClientBrand = strClientBrand;
	}

	public String[] getSkillTRId() {
		return skillTRId;
	}

	public void setSkillTRId(String[] skillTRId) {
		this.skillTRId = skillTRId;
	}

	public String[] getRequiredSkill() {
		return requiredSkill;
	}

	public void setRequiredSkill(String[] requiredSkill) {
		this.requiredSkill = requiredSkill;
	}

	public String[] getReqMinExp() {
		return reqMinExp;
	}

	public void setReqMinExp(String[] reqMinExp) {
		this.reqMinExp = reqMinExp;
	}

	public String[] getReqMaxExp() {
		return reqMaxExp;
	}

	public void setReqMaxExp(String[] reqMaxExp) {
		this.reqMaxExp = reqMaxExp;
	}

	public String[] getReqResource() {
		return reqResource;
	}

	public void setReqResource(String[] reqResource) {
		this.reqResource = reqResource;
	}

	public String[] getReqResourceGap() {
		return reqResourceGap;
	}

	public void setReqResourceGap(String[] reqResourceGap) {
		this.reqResourceGap = reqResourceGap;
	}

	public String[] getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String[] strWLocation) {
		this.strWLocation = strWLocation;
	}

	public String[] getProResourceReqId() {
		return proResourceReqId;
	}

	public void setProResourceReqId(String[] proResourceReqId) {
		this.proResourceReqId = proResourceReqId;
	}

	public String[] getStrWLocationFilter() {
		return strWLocationFilter;
	}

	public void setStrWLocationFilter(String[] strWLocationFilter) {
		this.strWLocationFilter = strWLocationFilter;
	}

	public String[] getReqMinExpFilter() {
		return reqMinExpFilter;
	}

	public void setReqMinExpFilter(String[] reqMinExpFilter) {
		this.reqMinExpFilter = reqMinExpFilter;
	}

	public String[] getReqMaxExpFilter() {
		return reqMaxExpFilter;
	}

	public void setReqMaxExpFilter(String[] reqMaxExpFilter) {
		this.reqMaxExpFilter = reqMaxExpFilter;
	}

	public List<String> getStrProjectOwner() {
		return strProjectOwner;
	}

	public void setStrProjectOwner(List<String> strProjectOwner) {
		this.strProjectOwner = strProjectOwner;
	}
	
	public List<FillEmployee> getPortfolioManagerList() {
		return portfolioManagerList;
	}

	public void setPortfolioManagerList(List<FillEmployee> portfolioManagerList) {
		this.portfolioManagerList = portfolioManagerList;
	}

	public List<FillEmployee> getAccountManagerList() {
		return accountManagerList;
	}

	public void setAccountManagerList(List<FillEmployee> accountManagerList) {
		this.accountManagerList = accountManagerList;
	}

	public List<FillEmployee> getDeliveryManagerList() {
		return deliveryManagerList;
	}

	public void setDeliveryManagerList(List<FillEmployee> deliveryManagerList) {
		this.deliveryManagerList = deliveryManagerList;
	}
	
	public String getStrPortfolioManager() {
		return strPortfolioManager;
	}

	public void setStrPortfolioManager(String strPortfolioManager) {
		this.strPortfolioManager = strPortfolioManager;
	}

	public String getStrAccountManager() {
		return strAccountManager;
	}

	public void setStrAccountManager(String strAccountManager) {
		this.strAccountManager = strAccountManager;
	}

	public String getStrDeliveryManager() {
		return strDeliveryManager;
	}

	public void setStrDeliveryManager(String strDeliveryManager) {
		this.strDeliveryManager = strDeliveryManager;
	}	
//===start parvez date:22-11-2022===
	public List<FillProjectDomain> getProjectDomainList() {
		return projectDomainList;
	}

	public void setProjectDomainList(List<FillProjectDomain> projectDomainList) {
		this.projectDomainList = projectDomainList;
	}
	
	public String getProDomain() {
		return proDomain;
	}

	public void setProDomain(String proDomain) {
		this.proDomain = proDomain;
	}

	public String getUstPrjectId() {
		return ustPrjectId;
	}

	public void setUstPrjectId(String ustPrjectId) {
		this.ustPrjectId = ustPrjectId;
	}

	public String getProjectAccountId() {
		return projectAccountId;
	}

	public void setProjectAccountId(String projectAccountId) {
		this.projectAccountId = projectAccountId;
	}

	public String getProjectSegment() {
		return projectSegment;
	}

	public void setProjectSegment(String projectSegment) {
		this.projectSegment = projectSegment;
	}

	public String getRevenueTarget() {
		return revenueTarget;
	}

	public void setRevenueTarget(String revenueTarget) {
		this.revenueTarget = revenueTarget;
	}

//===end parvez date: 22-11-2022===	
}