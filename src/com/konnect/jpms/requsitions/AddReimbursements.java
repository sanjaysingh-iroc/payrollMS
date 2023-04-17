package com.konnect.jpms.requsitions;

import java.io.File;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillLodgingType;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillRimbursementType;
import com.konnect.jpms.select.FillTravel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.task.FillProjectList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.LogDetails;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddReimbursements extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null; 
	String strUserTypeId = null;
	String strBaseUserType = null; 
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;  

	private CommonFunctions CF;
	
	private List<FillLevel> levelList;  
	private List<FillEmployee> empNamesList;
	private List<FillEmployee> empNamesList1;
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	private String f_org;
	private String strf_WLocation;
	private String strWLocation;
	private String strStartDate;
	private String strEndDate;
	private String level;
	private String strSelectedEmpId;
	private String strSelectedEmpId1;
	
	private String strId;
	private String strPurpose;
	private String strAmount;
	private String strFrom;
	private String strTo;
	private String strType;
	private String strTravelPlan;
	private String strProject;

	private File[] strDocument;
	private String[] strDocumentContentType;
	private String[] strDocumentFileName;

	private String reimbursementType;
	private List<FillRimbursementType> reimbursementTypeList;

	private List<FillRimbursementType> modeoftravelList;
	private String modeoftravel;
	private String noofperson;
	private String placefrom;
	private String placeto;
	private String noofdays;
	private String kmpd;
	private String ratepkm;

	private String policy_id;
	
	private String paycycle;
	private List<FillPayCycles> paycycleList;
	private List<FillPayCycles> paycycleListFull;
	
	private List<FillCurrency> currencyList;
	private List<FillPayMode> paymentModeList;
	
	private String type;
	private String RID;
	
	private String paycycleDate;
	
	private String alertStatus;
	private String alert_type;
	private String approveStatus;
	
	private String strClient;
	private String strViewDocument;
	private List<FillRimbursementType> typeList;

	private List<FillTravel> travelPlanList;
	private List<FillProjectList> projectList;
	private List<FillClients> clientList;
	
	private boolean isbillable;
	
	private String strVendor;
	private String[] strReceiptNo;

//	private String strPaycycle;
	private String pageType;
	
	private List<FillLodgingType> lodgingTypeList;	
	private List<FillRimbursementType> localConveyanceTranTypeList;	
	private String strTravelPlanDays;
	private String transportType;
	private String trainType;
	private String busType;
	private String flightType;
	private String carType;
	private String strTransAmount;
	private String lodgingType;
	private String strLodgingAmount;
	private String localConveyanceTranType;
	private String localConveyanceKM;
	private String localConveyanceRate;
	private String strLocalConveyanceAmount;
	private String strFoodBeverageAmount;
	private String strLaundryAmount;
	private String strSundryAmount;	
	
	private String currUserType;
	private String alertID;
	
	private String strFromDate;
//	private String strToDate;
	
	private String reimbCurrency;
	private String reimbPaymentMode;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
			  
//		isbillable=true;

		//System.out.println("Pacy Add Reimbursement===>"+getPaycycle());
		
		String strRemoveDoc = (String) request.getParameter("removeDoc");
		String rid = (String) request.getParameter("rid");
		if(uF.parseToInt(rid) > 0 && strRemoveDoc != null && strRemoveDoc.equalsIgnoreCase("removeDoc")){
			removeDocument(uF,rid);
			return "removedocument";
		}
		
		String strE = (String) request.getParameter("E");

		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		if(getApproveStatus()==null || getApproveStatus().trim().equals("")) {
			setApproveStatus("2");
		}
		
		String strEmpID11=null;
		if (strUserType != null && strUserType.equals(HRMANAGER)) {
			strEmpID11=getStrSelectedEmpId1();
		} else {
			strEmpID11=strSessionEmpId;
		}
		
		request.setAttribute("strEmpID11", strEmpID11);
		
		String[] arrDates = null;
		if (getPaycycle() != null) {
			arrDates = getPaycycle().split("-");
			setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
		} else {
			arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
		}
		
		if(getStrStartDate()==null) {
			setStrStartDate(arrDates[0]);
		}
		if(getStrEndDate()==null) {
			setStrEndDate(arrDates[1]);
		}

		String[] arr = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
		paycycleList = new FillPayCycles(request).fillNoofPayCycles(CF, getF_org(),(uF.parseToInt(arr[2])-4));
		paycycleListFull = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		typeList = new FillRimbursementType().fillRimbursementType(); 
		travelPlanList = new FillTravel(request).fillTravelPlan(uF.parseToInt(strEmpID11));
		
		if(strUserType != null && strUserType.equals(EMPLOYEE)) {
			projectList = new FillProjectList(request).fillProjectDetailsByEmp(uF.parseToInt(strEmpID11), false, 0);
		} else {
			projectList = new FillProjectList(request).fillProjectDetailsByEmp(0, false, 0);
		}
		
		clientList = new FillClients(request).fillLiveProjectClients(uF.parseToInt(strEmpID11)); 
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		empNamesList = new FillEmployee(request).fillEmployeeNameByAccess(getF_org(), (String)session.getAttribute(ORG_ACCESS),getStrf_WLocation(), (String)session.getAttribute(WLOCATION_ACCESS),strUserType,false);
		empNamesList1 = new FillEmployee(request).fillEmployeeNameByAccess(getF_org(), (String)session.getAttribute(ORG_ACCESS),getStrWLocation(), (String)session.getAttribute(WLOCATION_ACCESS),strUserType,false);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		modeoftravelList = new FillRimbursementType().fillmodeoftravel();
		lodgingTypeList = new FillLodgingType().fillLodgingType();
		localConveyanceTranTypeList = new FillRimbursementType().fillmodeoftravel();
		
		currencyList= new FillCurrency(request).fillCurrency();
		paymentModeList = new FillPayMode().fillPaymentModeExpenses();
		
		getEmpProjectDetails(uF);

//		System.out.println("getStrId() ===>> " + getStrId());
//		System.out.println("getStrAmount() ===>> " + getStrAmount());
		if (strE != null) {
			viewReimbursement(strE);
		} else if (getStrId() != null && getStrId().length() > 0) {
			updateReimbursement();
			return SUCCESS;
		} else if (getStrAmount() != null) {
			addReimbursement();
			if(getPageType()!=null && getPageType().trim().equals("TS")) {    
				return VIEW;
			} else {
//				System.out.println("in success");
				return SUCCESS;
			}
		}
		getReimbursementsPolicyMember();
		
		return loadReimbursements(uF);
	}
	
	private void removeDocument(UtilityFunctions uF, String rid) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strDoc = (String) request.getParameter("strDoc");
			String docFilePath = (String) request.getParameter("docFilePath");
			
			strDoc = URLDecoder.decode(strDoc);
			docFilePath = URLDecoder.decode(docFilePath);
			
//			System.out.println("rid==>"+rid);
//			System.out.println("strDoc==>"+strDoc);
//			System.out.println("docFilePath==>"+docFilePath);
			
			pst = con.prepareStatement("select * from emp_reimbursement where reimbursement_id=?");
			pst.setInt(1, uF.parseToInt(rid));
			rs = pst.executeQuery();
			boolean flag = false;
			StringBuilder sbDoc = new StringBuilder();
			if (rs.next()) {
				String[] strDocs1 = null;
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>0) {
					strDocs1 = rs.getString("ref_document").split(":_:");
				}
				for (int k = 0; strDocs1 != null && k < strDocs1.length; k++) {
					if(strDocs1[k] != null && !strDocs1[k].trim().equalsIgnoreCase(strDoc.trim())) {
						sbDoc.append(strDocs1[k].trim()+ ":_:");
					}
				}
				flag = true;
			}
			rs.close();
			pst.close();
			
//			System.out.println("sbDoc.toString()==>"+sbDoc.toString());
			
			if(flag){
				File file = new File(docFilePath);		         
		        if(file.delete()){
//		            System.out.println("File deleted successfully");
		            pst = con.prepareStatement("update emp_reimbursement set ref_document=? where reimbursement_id=?");
					pst.setString(1, sbDoc.toString());
					pst.setInt(2, uF.parseToInt(rid));
					pst.execute();
					pst.close();
					
					request.setAttribute("STATUS_MSG", "Deleted");
		        }else{
//		            System.out.println("Failed to delete the file");
		            request.setAttribute("STATUS_MSG", "failed");
		        }
			} else {
				 request.setAttribute("STATUS_MSG", "failed");
			}
			
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getEmpProjectDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			int nEmpId=0;
			if (strUserType != null && strUserType.equals(HRMANAGER)) {
				nEmpId=uF.parseToInt(getStrSelectedEmpId1());
			} else {
				nEmpId=uF.parseToInt(strSessionEmpId);
			}
			con = db.makeConnection(con);
			String strEmpOrgId = CF.getEmpOrgId(con, uF, ""+nEmpId);
			String strEmpLevelId = CF.getEmpLevelId(con, ""+nEmpId);
			
			setReimbCurrency(CF.getOrgCurrencyIdByOrg(con, getF_org()));
//			String[] arrDates = getPaycycle().split("-");
			String []arrDates = null;
			if (getPaycycle() != null) {
				arrDates = getPaycycle().split("-");
			} else {
				arrDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
			}
//			pst = con.prepareStatement("select * from activity_info where resource_ids like '%,"+nEmpId+",%' and pro_id in (select pro_id from projectmntnc where " +
//			"start_date >=? and start_date <=?)");
			pst = con.prepareStatement("select * from activity_info where resource_ids like '%,"+nEmpId+",%' and pro_id in (select pro_id from projectmntnc where " +
			"(start_date >= ? and deadline <= ?) or (start_date <= ? and deadline >= ?) or (start_date >= ? and start_date <= ?))");
			pst.setDate(1, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(arrDates[1], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(arrDates[1], DATE_FORMAT));
			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			boolean isProject=false;
			while(rs.next()) {
				isProject=true;
			}
			rs.close();
			pst.close();
			
			/**
			 * Local Policy 
			 * */
			Map<String, String> hmLocalType = CF.getLocalType(uF);
			Map<String, String> hmLimitType = CF.getLimitType(uF);
			pst = con.prepareStatement("select * from reimbursement_policy where local_limit_type=2 and reimbursement_policy_type=? and level_id=? and org_id=?");
			pst.setInt(1, REIMBURSEMENTS_LOCAL);
			pst.setInt(2, uF.parseToInt(strEmpLevelId));
			pst.setInt(3, uF.parseToInt(strEmpOrgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alLocal = new ArrayList<Map<String,String>>(); 
			while(rs.next()) {
				Map<String, String> hmLocal = new HashMap<String, String>();
				hmLocal.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmLocal.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmLocal.put("REIMBURSEMENT_IS_LOCAL_POLICY", rs.getString("is_default_policy"));
				hmLocal.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmLocal.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				
				hmLocal.put("REIMBURSEMENT_LOCAL_TYPE_ID", rs.getString("local_type"));
				hmLocal.put("REIMBURSEMENT_LOCAL_TYPE", uF.showData(hmLocalType.get(rs.getString("local_type")),""));
				hmLocal.put("REIMBURSEMENT_TRANSPORT_TYPE", rs.getString("transport_type"));
				hmLocal.put("REIMBURSEMENT_LOCAL_LIMIT_TYPE_ID", rs.getString("local_limit_type"));
				hmLocal.put("REIMBURSEMENT_LOCAL_LIMIT_TYPE", uF.showData(hmLimitType.get(rs.getString("local_limit_type")),""));
				hmLocal.put("REIMBURSEMENT_LOCAL_LIMIT", ""+uF.parseToDouble(rs.getString("local_limit")));
				hmLocal.put("REIMBURSEMENT_IS_REQUIRE_POLICY", rs.getString("is_require_approval"));
				hmLocal.put("REIMBURSEMENT_MIN_AMOUNT", ""+uF.parseToDouble(rs.getString("min_amount")));
				hmLocal.put("REIMBURSEMENT_MAX_AMOUNT", ""+uF.parseToDouble(rs.getString("max_amount")));
				
				alLocal.add(hmLocal);
			}
			rs.close();
			pst.close();
			request.setAttribute("alLocal", alLocal);
			
			/**
			 * Mobile Bill Policy
			 * */
			pst = con.prepareStatement("select * from reimbursement_policy where mobile_limit_type=2 and reimbursement_policy_type=? and level_id=? and org_id=?");
			pst.setInt(1, REIMBURSEMENTS_MOBILE_BILL);
			pst.setInt(2, uF.parseToInt(strEmpLevelId));
			pst.setInt(3, uF.parseToInt(strEmpOrgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alMobileBill = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmMobileInner = new HashMap<String, String>();
				hmMobileInner.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmMobileInner.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmMobileInner.put("REIMBURSEMENT_IS_MOBILE_POLICY", rs.getString("is_default_policy"));
				hmMobileInner.put("REIMBURSEMENT_MOBILE_LIMIT_TYPE", rs.getString("mobile_limit_type"));
				hmMobileInner.put("REIMBURSEMENT_MOBILE_LIMIT", rs.getString("mobile_limit"));
				hmMobileInner.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmMobileInner.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				
				alMobileBill.add(hmMobileInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("alMobileBill", alMobileBill);
//			System.out.println("alMobileBill====>"+alMobileBill);
			
			/**
			 * Claim Policy 
			 * */
			pst = con.prepareStatement("select * from reimbursement_policy where org_id=? and reimbursement_policy_type=? and level_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, REIMBURSEMENTS_CLAIM);
			pst.setInt(3, uF.parseToInt(strEmpLevelId));
			rs = pst.executeQuery();
			List<Map<String, String>> alClaim = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				
				Map<String, String> hmClaimInner = new HashMap<String, String>();
				hmClaimInner.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmClaimInner.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmClaimInner.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmClaimInner.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				hmClaimInner.put("REIMBURSEMENT_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_TYPE_ID", rs.getString("travel_transport_type"));
				
				hmClaimInner.put("REIMBURSEMENT_TRAIN_TYPE_ID", rs.getString("train_type"));
				
				hmClaimInner.put("REIMBURSEMENT_BUS_TYPE_ID", rs.getString("bus_type"));
				
				hmClaimInner.put("REIMBURSEMENT_FLIGHT_TYPE_ID", rs.getString("flight_type"));
				
				hmClaimInner.put("REIMBURSEMENT_CAR_TYPE_ID", rs.getString("car_type"));
				
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_LIMIT_TYPE_ID", rs.getString("travel_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_LIMIT", ""+uF.parseToDouble(rs.getString("travel_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LODGING_TYPE_ID", rs.getString("lodging_type"));
				hmClaimInner.put("REIMBURSEMENT_LODGING_LIMIT_TYPE_ID", rs.getString("lodging_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_LODGING_LIMIT", ""+uF.parseToDouble(rs.getString("lodging_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LOCAL_CONVEYANCE_TRAN_ID", rs.getString("local_conveyance_tran_type"));
				hmClaimInner.put("REIMBURSEMENT_LOCAL_CONVEYANCE_LIMIT", ""+uF.parseToDouble(rs.getString("local_conveyance_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_FOOD_LIMIT_TYPE_ID", rs.getString("food_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_FOOD_LIMIT", ""+uF.parseToDouble(rs.getString("food_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LAUNDRY_LIMIT_TYPE_ID", rs.getString("laundry_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_LAUNDRY_LIMIT", ""+uF.parseToDouble(rs.getString("laundry_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_SUNDRY_LIMIT_TYPE_ID", rs.getString("sundry_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_SUNDRY_LIMIT", ""+uF.parseToDouble(rs.getString("sundry_limit")));
				
				alClaim.add(hmClaimInner);				
			}
			rs.close();
			pst.close();
			request.setAttribute("alClaim", alClaim);
//			System.out.println("alClaim=====>"+alClaim);
			
			reimbursementTypeList = new FillRimbursementType().fillRimbursementType1();
			if(getReimbursementType() == null || getReimbursementType().trim().equals("") || getReimbursementType().trim().equalsIgnoreCase("NULL")) {
				if(isProject){
					setReimbursementType("P");		
					 typeList.add(new FillRimbursementType("Mobile Bill", "Mobile Bill"));
				} else {
					setReimbursementType("L");
				}
			}
			
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}

	private void getReimbursementsPolicyMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		
		String policy_id=null;
		try {
			int strEmpID=0;
			if (strUserType != null && strUserType.equals(HRMANAGER)) {
				strEmpID = uF.parseToInt(getStrSelectedEmpId1());
			} else {
				strEmpID = uF.parseToInt(strSessionEmpId);
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
//			System.out.println("strEmpID=====> "+strEmpID);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId=hmEmpLevelMap.get(""+strEmpID);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(""+strEmpID);			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
			
//			System.out.println("empLevelId=====> "+empLevelId);
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_REIMBURSEMENTS+"' and level_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(locationID));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				policy_id=rs.getString("policy_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id) == 0) {
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(locationID));
				rs = pst.executeQuery();
				while(rs.next()) {
					policy_id=rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}
			
			if(uF.parseToInt(policy_id)>0) {
//				System.out.println("policy_id=====> "+policy_id);
				
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
						" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
				rs=pst.executeQuery();
				
				Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
				while(rs.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("member_type"));
					innerList.add(rs.getString("member_id"));
					innerList.add(rs.getString("member_position"));
					innerList.add(rs.getString("work_flow_mem"));
					innerList.add(rs.getString("work_flow_member_id"));
					
					hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();	
				
				
				
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1) {
						int memid=uF.parseToInt(innerList.get(1));
						
						switch(memid) {
						
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
								
									alList.add(strEmpMName);
									alList.add(rs.getString("emp_lname"));
									
									outerList.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList!=null && !outerList.isEmpty()) {
									StringBuilder sbComboBox=new StringBuilder();
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++) {
										List<String> alList=outerList.get(i);
										sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox.append("</select>");								
									
									String optionTr="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id="+strEmpID+" and supervisor_emp_id!=0) as a," 
										+ "employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
										+ " and epd.is_alive=true  order by epd.emp_fname");
								rs = pst.executeQuery();
								List<List<String>> outerList11=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);

									
									alList.add(rs.getString("emp_lname"));
									
									outerList11.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList11!=null && !outerList11.isEmpty()) {
									StringBuilder sbComboBox11=new StringBuilder();
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++) {
										List<String> alList=outerList11.get(i);
										sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox11.append("</select>");								
									
									String optionTr11="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList1=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);

									
									alList.add(rs.getString("emp_lname"));
									
									outerList1.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList1!=null && !outerList1.isEmpty()) {
									StringBuilder sbComboBox1=new StringBuilder();
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++) {
										List<String> alList=outerList1.get(i);
										sbComboBox1.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox1.append("</select>");								
									
									String optionTr1="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList2=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);

									
									alList.add(rs.getString("emp_lname"));
									
									outerList2.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList2!=null && !outerList2.isEmpty()) {
									StringBuilder sbComboBox2=new StringBuilder();
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList2.size();i++) {
										List<String> alList=outerList2.get(i);
										sbComboBox2.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox2.append("</select>");								
									
									String optionTr2="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
							
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList3=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);

									
									alList.add(rs.getString("emp_lname"));
									
									outerList3.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList3!=null && !outerList3.isEmpty()) {
									StringBuilder sbComboBox3=new StringBuilder();
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++) {
										List<String> alList=outerList3.get(i);
										sbComboBox3.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox3.append("</select>");								
									
									String optionTr3="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList4=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);

									
									alList.add(rs.getString("emp_lname"));
									
									outerList4.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList4!=null && !outerList4.isEmpty()) {
									StringBuilder sbComboBox4=new StringBuilder();
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++) {
										List<String> alList=outerList4.get(i);
										sbComboBox4.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox4.append("</select>");								
									
									String optionTr4="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr4);
								}
								break;
							
						case 7:
							 pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
										"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
										"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod," +
										"employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.emp_id=?)" +
										" union " +
										"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
										"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' " +
										"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true " +
										" union " +
										"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
										"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
										"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true");
								pst.setInt(1, strEmpID);
								pst.setInt(2, strEmpID);
								pst.setInt(3, strEmpID);
								pst.setInt(4, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList5=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
//									alList.add(rs.getString("usertype_id"));
									alList.add(hmUserTypeIdMap.get(HRMANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);

									
									alList.add(rs.getString("emp_lname"));
									
									outerList5.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList5!=null && !outerList5.isEmpty()) {
									StringBuilder sbComboBox5=new StringBuilder();
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++) {
										List<String> alList=outerList5.get(i);
										sbComboBox5.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox5.append("</select>");								
									
									String optionTr5="<tr><td class=\"txtlabel alignRight textcolorWhite\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;							
						
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
									"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
									"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1,strEmpID);
							rs = pst.executeQuery();
							List<List<String>> outerHODList=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);

								
								alList.add(rs.getString("emp_lname"));
								
								outerHODList.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerHODList!=null && !outerHODList.isEmpty()) {
								StringBuilder sbComboBox11=new StringBuilder();
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++) {
									List<String> alList=outerHODList.get(i);
									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
								
								hmMemberOption.put(innerList.get(4), optionTr11);
							}
						
							break;
						}						
						
					} else if(uF.parseToInt(innerList.get(0))==3) {
						int memid=uF.parseToInt(innerList.get(1));
						
						List<List<String>> outerList=new ArrayList<List<String>>();
						pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id " +
								"and se.policy_id = ? and se.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname"); 
						pst.setInt(1,uF.parseToInt(policy_id));
						pst.setInt(2, strEmpID);
						rs = pst.executeQuery();
						while (rs.next()) {
							List<String> alList = new ArrayList<String>();
							alList.add(rs.getString("emp_id"));
							outerList.add(alList);
						}
						rs.close();
						pst.close();
						
						if(outerList!=null && !outerList.isEmpty()) {
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++) {
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight textcolorWhite\">Your workflow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				/*String divpopup="";
				StringBuilder sb = new StringBuilder();
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {		
					 sb.append("<div id=\"popup_name" + strEmpID + "\" class=\"popup_block\">" + 
							   "<h2 class=\"textcolorWhite\">Reimbursements of "+hmEmpCodeName.get(""+strEmpID)+"</h2>" + 
							   "<table>"); 
										
					 if(hmMemberOption!=null && !hmMemberOption.isEmpty() ) {
						 Iterator<String> it1=hmMemberOption.keySet().iterator();
						while(it1.hasNext()) {
							String memPosition=it1.next();
							String optiontr=hmMemberOption.get(memPosition);					
							sb.append(optiontr); 
						}
						sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"input_button\"/></td>" +
								"</tr>");
					 } else {
						 sb.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
					 }
					 sb.append("</table></div>");
					
					divpopup="<input type=\"button\" name=\"submit1\" value=\"Submit\" class=\"input_button\"/>";
				} else {
					sb.append("");
					divpopup="<input type=\"submit\" name=\"submit1\" value=\"Submit\" class=\"input_button\"/>";
				}
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("divpopup",divpopup);
				request.setAttribute("reimbursementsD", sb.toString());
				request.setAttribute("strEmpID", strEmpID);*/
				
				
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("strEmpID", strEmpID);
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}

	

	public String loadReimbursements(UtilityFunctions uF) {
		String strE = (String) request.getParameter("E");
		if (strE == null) {
			setStrFrom(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT));
			setStrTo(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT));
			setStrPurpose(null);
			setStrAmount(null);
			
			setPlacefrom(null);
			setPlaceto(null);
			setStrType(null);
			setModeoftravel(null);
			setNoofperson(null);
			setNoofdays(null);
			setKmpd(null);
			setRatepkm(null);
			
			setStrVendor(null);
			setStrReceiptNo(null);
			
			setTransportType(null);
			setTrainType(null);
			setBusType(null);
			setFlightType(null);
			setCarType(null);
			setStrTransAmount(null);
			setLodgingType(null);
			setStrLodgingAmount(null);
			setLocalConveyanceTranType(null);
			setLocalConveyanceKM(null);
			setLocalConveyanceRate(null);
			setStrLocalConveyanceAmount(null);
			setStrFoodBeverageAmount(null);
			setStrLaundryAmount(null);
			setStrSundryAmount(null);
			setStrTravelPlanDays(null);
		}
		
		
		return LOAD;
	}

	public void addReimbursement() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			StringBuilder sbFileName = new StringBuilder(); 
			for (int i = 0; getStrDocument() != null && i < getStrDocument().length; i++) {
				String strEmp = "";
				if (strUserType != null && strUserType.equals(HRMANAGER)) {
					strEmp = getStrSelectedEmpId1();
				} else {
					strEmp = strSessionEmpId;
				}
				if(CF.getStrDocSaveLocation()==null) {
					sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument()[i], getStrDocumentFileName()[i], getStrDocumentFileName()[i], CF) + ":_:");
				} else {
					sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strEmp, getStrDocument()[i], getStrDocumentFileName()[i], getStrDocumentFileName()[i], CF) + ":_:");
				} 
			}  
//			System.out.println("file name=======>"+sbFileName.toString());
			
			StringBuilder sbReceiptNo = new StringBuilder();
			for (int i = 0; getStrReceiptNo() != null && i < getStrReceiptNo().length; i++) {
				if(getStrReceiptNo()[i]!=null && !getStrReceiptNo()[i].trim().equals("") && !getStrReceiptNo()[i].trim().equalsIgnoreCase("NULL")){
					sbReceiptNo.append(getStrReceiptNo()[i].trim()+ ":_:");
				}
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			int nTransportType = uF.parseToInt(getTransportType());
			int nTransportMode = 0;
			double dblTransportAmount = 0.0d;
			int nLodgingType = uF.parseToInt(getLodgingType());
			double dblLodgingAmount = 0.0d;
			String strLocalConveyanceType = null;
			double dblLocalConveyanceKm = 0.0d;
			double dblLocalConveyanceRate = 0.0d;
			double dblLocalConveyanceAmount = 0.0d;
			double dblFoodBeverageAmount = 0.0d;
			double dblLaundryAmount = 0.0d;
			double dblSundryAmount = 0.0d;
			if(getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("T")) {
				if(nTransportType == 1) {
					nTransportMode = uF.parseToInt(getTrainType());
					dblTransportAmount = uF.parseToDouble(getStrTransAmount());
				} else if(nTransportType == 2) {
					nTransportMode = uF.parseToInt(getBusType());
					dblTransportAmount = uF.parseToDouble(getStrTransAmount());
				} else if(nTransportType == 3) {
					nTransportMode = uF.parseToInt(getFlightType());
					dblTransportAmount = uF.parseToDouble(getStrTransAmount());
				} else if(nTransportType == 4) {
					nTransportMode = uF.parseToInt(getCarType());
					dblTransportAmount = uF.parseToDouble(getStrTransAmount());
				}
				
				dblLodgingAmount = uF.parseToDouble(getStrLodgingAmount());
				
				strLocalConveyanceType = getLocalConveyanceTranType();
				if(strLocalConveyanceType!=null && !strLocalConveyanceType.trim().equals("")) {
					dblLocalConveyanceKm = uF.parseToDouble(getLocalConveyanceKM());
					dblLocalConveyanceRate = uF.parseToDouble(getLocalConveyanceRate());
					dblLocalConveyanceAmount = uF.parseToDouble(getStrLocalConveyanceAmount());
				}
				
				dblFoodBeverageAmount = uF.parseToDouble(getStrFoodBeverageAmount());
				dblLaundryAmount = uF.parseToDouble(getStrLaundryAmount());
				dblSundryAmount = uF.parseToDouble(getStrSundryAmount());
			}
			
			pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
				"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
				"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
				"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
				"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,reimb_from_date," +
				"reimb_currency,reimb_payment_mode) " +
				"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?);");
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("P")) {
				pst.setString(3, getStrProject());
			} else if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("T")) {
				pst.setString(3, getStrTravelPlan());
			} else if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("M")) {
				pst.setString(3, "Mobile Bill");
			} else {
				pst.setString(3, getStrType()); 
			}
			pst.setString(4, getStrPurpose());
			pst.setDouble(5, uF.parseToDouble(getStrAmount()));
			if (strUserType != null && strUserType.equals(HRMANAGER)) {
				pst.setInt(6, uF.parseToInt(getStrSelectedEmpId1()));
			} else {
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
			}
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(8, sbFileName.toString());
			pst.setString(9, getReimbursementType());
			
			pst.setString(10, getModeoftravel());
			pst.setInt(11, uF.parseToInt(getNoofperson()));
			pst.setString(12, getPlacefrom());
			pst.setString(13, getPlaceto());
			pst.setInt(14, uF.parseToInt(getNoofdays()));
			pst.setDouble(15, uF.parseToDouble(getKmpd()));
			pst.setDouble(16, uF.parseToDouble(getRatepkm()));
			//pst.setString(17, getStrType());
			if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("M")) {
				pst.setString(17, "Mobile Bill");
			} else {
				pst.setString(17, getStrType());
			}
			pst.setBoolean(18, getIsbillable());
			pst.setInt(19, uF.parseToInt(getStrClient()));
			if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("P")) {
				pst.setInt(20, uF.parseToInt(getStrProject()));
			} else {
				pst.setInt(20, 0);
			}
			pst.setString(21, getStrVendor());
			pst.setString(22, sbReceiptNo.toString());
			
			pst.setInt(23, nTransportType);
			pst.setInt(24, nTransportMode);
			pst.setDouble(25, dblTransportAmount);
			
			pst.setInt(26, nLodgingType);
			pst.setDouble(27, dblLodgingAmount);
			
			pst.setString(28, strLocalConveyanceType);
			pst.setDouble(29, dblLocalConveyanceKm);
			pst.setDouble(30, dblLocalConveyanceRate);
			pst.setDouble(31, dblLocalConveyanceAmount);
			
			pst.setDouble(32, dblFoodBeverageAmount);
			pst.setDouble(33, dblLaundryAmount);
			pst.setDouble(34, dblSundryAmount);	
			pst.setDate(35, uF.getDateFormat(getStrFromDate(), DATE_FORMAT));
			pst.setInt(36, uF.parseToInt(getReimbCurrency()));
			pst.setInt(37, uF.parseToInt(getReimbPaymentMode()));
//			pst.setDate(36, uF.getDateFormat(getStrToDate(), DATE_FORMAT));
//			System.out.println("===>pst"+pst);
			int x = pst.executeUpdate();
					
			if(x > 0) {
				String reimbursement_id=null;
				pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
				rs=pst.executeQuery();
				while(rs.next()) {
					reimbursement_id=rs.getString("reimbursement_id");
				}
				rs.close();
				pst.close();
				
	//			System.out.println("reimbursement_id====>"+reimbursement_id);
				
				List<String> alManagers = null;
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
					alManagers = insertLeaveApprovalMember(con,pst,rs,reimbursement_id,uF, getStrAmount());
				}
				
				String strDomain = request.getServerName().split("\\.")[0];
				/*String filePath = request.getRealPath("/userDocuments/") + File.separator;
				Notifications nF = new Notifications(N_EMPLOYEE_REIMBURSEMENT_REQUEST, CF); 
				nF.setDomain(strDomain);
				nF.request = request;
				if (strUserType != null && strUserType.equals(HRMANAGER)) {
					nF.setStrEmpId(getStrSelectedEmpId1());
				} else {
					nF.setStrEmpId(strSessionEmpId);
				}
				
	//			nF.setStrHostAddress(request.getRemoteHost());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpReimbursementFrom(uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpReimbursementTo(uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpReimbursementPurpose(getStrPurpose());
				nF.setStrEmpReimbursementType(getStrType());
				nF.setStrEmpReimbursementAmount(getStrAmount());
				// nF.setStrAttachmentFileSource(filePath+fileName);
				// nF.setStrAttachmentFileName(fileName);
				nF.setEmailTemplate(true);
				nF.sendNotifications(); */
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
				Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetailsForPDF(con);
				if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
				
				String strCurrId = (strUserType != null && strUserType.equals(HRMANAGER)) ? hmEmpCurrency.get(getStrSelectedEmpId1()) : hmEmpCurrency.get(strSessionEmpId);
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if (hmCurrencyInner == null)hmCurrencyInner = new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				String strReimbType=null;
				if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("P")) {
					strReimbType = getStrProject();
				} else if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("T")) {
					strReimbType= getStrTravelPlan();
				} else if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("M")) {
					strReimbType = "Mobile Bill";
				} else {
					strReimbType = getStrType(); 
				}
				
				for(int i=0; alManagers!=null && i<alManagers.size();i++) {
					Notifications nF = new Notifications(N_MANAGER_REIMBURSEMENT_REQUEST, CF); 
					nF.setDomain(strDomain);
					nF.request = request;
					nF.session = session;
					if (strUserType != null && strUserType.equals(HRMANAGER)) {
						nF.setStrEmpId(getStrSelectedEmpId1());
					} else {
						nF.setStrEmpId(strSessionEmpId);
					}
					nF.setSupervisor(false);
					nF.setEmailTemplate(true);
					
					pst = con.prepareStatement(selectEmpDetails1);
					pst.setInt(1, uF.parseToInt((String)alManagers.get(i)));
					rs = pst.executeQuery();
					boolean flg=false;
					while(rs.next()) {
						nF.setStrSupervisorEmail(rs.getString("emp_email"));
						
						String strEmpMName = "";
						
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
						if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("emp_email_sec"));
							nF.setStrEmailTo(rs.getString("emp_email_sec"));
						} else {
							nF.setStrEmpEmail(rs.getString("emp_email"));
							nF.setStrEmailTo(rs.getString("emp_email"));
						}
						flg=true;
					}
					if(flg) {
	//					nF.setStrMgrId((String)alManagers.get(i));
	//					nF.setStrHostAddress(request.getRemoteHost());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrEmpReimbursementFrom(uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementTo(uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementPurpose(getStrPurpose());
						nF.setStrEmpReimbursementType(strReimbType);
						nF.setStrEmpReimbursementAmount(getStrAmount());
						// nF.setStrAttachmentFileSource(filePath+fileName);
						// nF.setStrAttachmentFileName(fileName);
						nF.setStrEmpReimbursementDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementCurrency(strCurrSymbol);
						nF.sendNotifications(); 
					}
				}
				session.setAttribute(MESSAGE, SUCCESSM+"Reimbursement saved successfully."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Reimbursement not saved."+END);
			}
			
			/**
			 * Log Details
			 * */
			String strProcessByName = CF.getEmpNameMapByEmpId(con, getStrSelectedEmpId1());
			String strEmpName = CF.getEmpNameMapByEmpId(con, ""+uF.parseToInt(getStrSelectedEmpId1()));
			String strProcessMsg = uF.showData(strProcessByName, "")+" has added reimbursements date of "+uF.showData(strEmpName, "") +" on " +
				""+uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat())+" " +
				""+uF.getTimeFormatStr(""+uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
			LogDetails logDetails = new LogDetails();
			logDetails.session = session;
			logDetails.CF = CF;
			logDetails.request = request;
			logDetails.setProcessId(uF.parseToInt(getStrSelectedEmpId1()));
			logDetails.setProcessType(L_ADD_REIMBURSEMENTS);
			logDetails.setProcessActivity(L_ADD);
			logDetails.setProcessMsg(strProcessMsg);
			logDetails.setProcessStep(0);
			logDetails.setProcessBy(uF.parseToInt(getStrSelectedEmpId1()));
			logDetails.insertLog(con, uF);
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Reimbursement not saved."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private List<String> insertLeaveApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, String reimbursement_id, UtilityFunctions uF, String strAmount) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1,uF.parseToInt(getPolicy_id()));
			rs=pst.executeQuery();
			Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));
				
				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it=hmMemberMap.keySet().iterator();
			while(it.hasNext()) {
				String work_flow_member_id=it.next();
				List<String> innerList=hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
//				System.out.println("innerList.get(3)+memid====>"+innerList.get(3)+memid+"=====>"+request.getParameter(innerList.get(3)+memid));
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")) {
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
//					System.out.println("approval empid====>"+empid);
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,uF.parseToInt(reimbursement_id));
					pst.setString(3,WORK_FLOW_REIMBURSEMENTS);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();
					
					String alertData = "<div style=\"float: left;\"> Received a new Claim Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> amount "+uF.formatIntoTwoDecimal(uF.parseToDouble(strAmount))+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyReimbursement"+strSubAction;
					} else {
						alertAction = "PayApprovals.action?pType=WR&callFrom=NotiApplyReimbursement"+strSubAction;
					}
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId+"");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(empid);
//					userAlerts.set_type(REIM_REQUEST_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
					
					if(!alManagers.contains(empid)) {
						alManagers.add(empid);
					}
				}
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alManagers;
	}

	public void updateReimbursement() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			StringBuilder sbFileName = new StringBuilder();
			for (int i = 0; getStrDocument() != null && i < getStrDocument().length; i++) {
//				if(CF.getStrDocSaveLocation()==null) {
//					sbFileName.append(uF.uploadFile(request, DOCUMENT_LOCATION, getStrDocument()[i], getStrDocumentFileName()[i], CF.getIsRemoteLocation(), CF) + ":_:");
//				} else {
//					sbFileName.append(uF.uploadFile(request, CF.getStrDocSaveLocation(), getStrDocument()[i], getStrDocumentFileName()[i], CF.getIsRemoteLocation(), CF) + ":_:");
//				}
				String strEmp = "";
				if (strUserType != null && strUserType.equals(HRMANAGER)) {
					strEmp = getStrSelectedEmpId1();
				} else {
					strEmp = strSessionEmpId;
				}
				if(CF.getStrDocSaveLocation()==null) {
					sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument()[i], getStrDocumentFileName()[i], getStrDocumentFileName()[i], CF) + ":_:");
				} else {
					sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strEmp, getStrDocument()[i], getStrDocumentFileName()[i], getStrDocumentFileName()[i], CF) + ":_:");
				} 
			}
			
			StringBuilder sbReceiptNo = new StringBuilder();
			for (int i = 0; getStrReceiptNo() != null && i < getStrReceiptNo().length; i++) {
				if(getStrReceiptNo()[i]!=null && !getStrReceiptNo()[i].trim().equals("") && !getStrReceiptNo()[i].trim().equalsIgnoreCase("NULL")){
					sbReceiptNo.append(getStrReceiptNo()[i].trim()+ ":_:");
				}
			}
  
			con = db.makeConnection(con);
			
			int nTransportType = uF.parseToInt(getTransportType());
			int nTransportMode = 0;
			double dblTransportAmount = 0.0d;
			int nLodgingType = uF.parseToInt(getLodgingType());
			double dblLodgingAmount = 0.0d;
			String strLocalConveyanceType = null;
			double dblLocalConveyanceKm = 0.0d;
			double dblLocalConveyanceRate = 0.0d;
			double dblLocalConveyanceAmount = 0.0d;
			double dblFoodBeverageAmount = 0.0d;
			double dblLaundryAmount = 0.0d;
			double dblSundryAmount = 0.0d;
			if(getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("T")) {
				if(nTransportType == 1) {
					nTransportMode = uF.parseToInt(getTrainType());
					dblTransportAmount = uF.parseToDouble(getStrTransAmount());
				} else if(nTransportType == 2) {
					nTransportMode = uF.parseToInt(getBusType());
					dblTransportAmount = uF.parseToDouble(getStrTransAmount());
				} else if(nTransportType == 3) {
					nTransportMode = uF.parseToInt(getFlightType());
					dblTransportAmount = uF.parseToDouble(getStrTransAmount());
				} else if(nTransportType == 4) {
					nTransportMode = uF.parseToInt(getCarType());
					dblTransportAmount = uF.parseToDouble(getStrTransAmount());
				}
				
				dblLodgingAmount = uF.parseToDouble(getStrLodgingAmount());
				
				strLocalConveyanceType = getLocalConveyanceTranType();
				if(strLocalConveyanceType!=null && !strLocalConveyanceType.trim().equals("")) {
					dblLocalConveyanceKm = uF.parseToDouble(getLocalConveyanceKM());
					dblLocalConveyanceRate = uF.parseToDouble(getLocalConveyanceRate());
					dblLocalConveyanceAmount = uF.parseToDouble(getStrLocalConveyanceAmount());
				}
				
				dblFoodBeverageAmount = uF.parseToDouble(getStrFoodBeverageAmount());
				dblLaundryAmount = uF.parseToDouble(getStrLaundryAmount());
				dblSundryAmount = uF.parseToDouble(getStrSundryAmount());
			}
			
			pst = con.prepareStatement("update emp_reimbursement set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
				"reimbursement_amount=?, reimbursement_type1=?,travel_mode=?,no_person=?,travel_from=?,travel_to=?,no_days=?,travel_distance=?," +
				"travel_rate=?,reimbursement_info=?,is_billable=?, client_id=?,pro_id=?,vendor=?,receipt_no=?,transport_type=?,transport_mode=?," +
				"transport_amount=?,lodging_type=?,lodging_amount=?,local_conveyance_type=?,local_conveyance_km=?,local_conveyance_rate=?," +
				"local_conveyance_amount=?,food_beverage_amount=?,laundry_amount=?,sundry_amount=?,reimb_from_date=?,reimb_currency=?," +
				"reimb_payment_mode=? where reimbursement_id=?");
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));

			if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("P")) {
				pst.setString(3, getStrProject());
			} else if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("T")) {
				pst.setString(3, getStrTravelPlan());
			} else if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("M")) {
				pst.setString(3, "Mobile Bill");
			} else {
				pst.setString(3, getStrType());
			}

			pst.setString(4, getStrPurpose());
			pst.setDouble(5, uF.parseToDouble(getStrAmount()));
			pst.setString(6, getReimbursementType());			
			pst.setString(7, getModeoftravel());
			pst.setInt(8, uF.parseToInt(getNoofperson()));
			pst.setString(9, getPlacefrom());
			pst.setString(10, getPlaceto());
			pst.setInt(11, uF.parseToInt(getNoofdays()));
			pst.setDouble(12, uF.parseToDouble(getKmpd()));
			pst.setDouble(13, uF.parseToDouble(getRatepkm()));
//			pst.setString(14, getStrType());
			if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("M")) {
				pst.setString(14, "Mobile Bill");
			} else {
				pst.setString(14, getStrType());
			}
			pst.setBoolean(15, getIsbillable());
			pst.setInt(16, uF.parseToInt(getStrClient()));
			if (getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("P")) {
				pst.setInt(17, uF.parseToInt(getStrProject()));
			} else {
				pst.setInt(17, 0);
			}
			pst.setString(18, getStrVendor());
			pst.setString(19, sbReceiptNo.toString());
			
			pst.setInt(20, nTransportType);
			pst.setInt(21, nTransportMode);
			pst.setDouble(22, dblTransportAmount);
			
			pst.setInt(23, nLodgingType);
			pst.setDouble(24, dblLodgingAmount);
			
			pst.setString(25, strLocalConveyanceType);
			pst.setDouble(26, dblLocalConveyanceKm);
			pst.setDouble(27, dblLocalConveyanceRate);
			pst.setDouble(28, dblLocalConveyanceAmount);
			
			pst.setDouble(29, dblFoodBeverageAmount);
			pst.setDouble(30, dblLaundryAmount);
			pst.setDouble(31, dblSundryAmount);
			pst.setDate(32, uF.getDateFormat(getStrFromDate(), DATE_FORMAT));
			pst.setInt(33, uF.parseToInt(getReimbCurrency()));
			pst.setInt(34, uF.parseToInt(getReimbPaymentMode()));
			pst.setInt(35, uF.parseToInt(getStrId()));
			pst.execute();
			pst.close();

			pst = con.prepareStatement("select * from emp_reimbursement where reimbursement_id=?");
			pst.setInt(1, uF.parseToInt(getStrId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbFileName.append(rs.getString("ref_document"));
			}
			rs.close();
			pst.close();

			if (getStrDocument() != null) {
				pst = con.prepareStatement("update emp_reimbursement set ref_document=? where reimbursement_id=?");
				pst.setString(1, sbFileName.toString());
				pst.setInt(2, uF.parseToInt(getStrId()));
				pst.execute();
				pst.close();
			}
			
			if(uF.parseToBoolean(CF.getIsWorkFlow())) {
				pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"'");
				pst.setInt(1, uF.parseToInt(getStrId()));
				pst.execute();
				pst.close();
				
				insertLeaveApprovalMember(con,pst,rs,getStrId(),uF, getStrAmount());
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+"Reimbursement saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Reimbursement not saved."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void viewReimbursement(String strE) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			List<String> alReceiptNo = new ArrayList<String>();
			pst = con.prepareStatement("select * from emp_reimbursement where reimbursement_id=?");
			pst.setInt(1, uF.parseToInt(strE));
			rs = pst.executeQuery();
			int nEmpId = 0;
			if (rs.next()) {
				nEmpId = rs.getInt("emp_id");
					
				setStrFrom(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT));
				setStrTo(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT));

				if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("P")) {
					setStrProject(rs.getString("pro_id"));
					setStrType(rs.getString("reimbursement_info"));
				} else if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("T")) {
					setStrTravelPlan(rs.getString("reimbursement_type"));
					setStrType(rs.getString("reimbursement_info"));
				} else {
					setStrType(rs.getString("reimbursement_info"));
				}

				setStrPurpose(rs.getString("reimbursement_purpose"));
				setStrAmount(rs.getString("reimbursement_amount")); 

				setPlacefrom(rs.getString("travel_from"));
				setPlaceto(rs.getString("travel_to"));
				setModeoftravel(rs.getString("travel_mode"));
				setNoofperson(rs.getString("no_person"));
				setNoofdays(rs.getString("no_days"));
				setKmpd(rs.getString("travel_distance"));
				setRatepkm(rs.getString("travel_rate"));
				setStrSelectedEmpId1(rs.getString("emp_id"));
				
				setStrFromDate(uF.getDateFormat(rs.getString("reimb_from_date"), DBDATE, DATE_FORMAT));
//				setStrToDate(uF.getDateFormat(rs.getString("reimb_to_date"), DBDATE, DATE_FORMAT));
				String orgId = CF.getEmpOrgId(con, uF, ""+nEmpId);
				String []arr = CF.getPayCycleFromDate(con, uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), CF.getStrTimeZone(), CF, orgId);
				
				StringBuilder sb = new StringBuilder();
				for(int i=0; arr!=null && i<arr.length; i++) {
					sb.append(arr[i]+"-");
				}
				if(sb.length()>0) {
					sb.replace(0, sb.length(), sb.substring(0, sb.length()-1));
				}
				setPaycycle(sb.toString());
				
				String[] strDocs = null;
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>0) {
					strDocs = rs.getString("ref_document").split(":_:");
				}
				StringBuilder sbDoc = new StringBuilder();
				for (int k = 0; strDocs != null && k < strDocs.length; k++) {
					if(CF.getStrDocRetriveLocation()==null) {
						String docFilePath = request.getContextPath()+DOCUMENT_LOCATION + strDocs[k].trim();
						String docRemoveFilePath = request.getContextPath()+DOCUMENT_LOCATION + strDocs[k].trim();
						sbDoc.append("<span id=\"doc_"+k+"\" style=\"padding: 5px;\"><a target=\"blank\" href=\"" + docFilePath + "\" class=\"viewattach\" title=\"View Attachment\" ></a>" +
								"nbsp;<a href=\"javascript:void(0);\" title=\"Remove "+strDocs[k]+"\" onclick=\"removeDocument('doc_"+k+"','"+rs.getString("reimbursement_id")+"','"+strDocs[k].trim()+"','"+docRemoveFilePath+"');\"><img src=\"images1/icons/hd_cross_16x16.png\" style=\"vertical-align: top; width:10px;\"/></a></span>");
					} else {
						String docFilePath = CF.getStrDocRetriveLocation() + I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ strDocs[k].trim();
						String docRemoveFilePath = CF.getStrDocSaveLocation() + I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ strDocs[k].trim();
						sbDoc.append("<span id=\"doc_"+k+"\" style=\"padding: 5px;\"><a target=\"blank\" href=\""+docFilePath+ "\" class=\"viewattach\" title=\"View Attachment\" ></a>" +
								"&nbsp;<a href=\"javascript:void(0);\" title=\"Remove "+strDocs[k]+"\" onclick=\"removeDocument('doc_"+k+"','"+rs.getString("reimbursement_id")+"','"+strDocs[k].trim()+"','"+docRemoveFilePath+"');\"><img src=\"images1/icons/hd_cross_16x16.png\" style=\"vertical-align: top; width:10px;\"/></a></span>");
					}
				} 
				setStrViewDocument(sbDoc.toString());

				if (rs.getString("reimbursement_type1") != null) {
					setReimbursementType(rs.getString("reimbursement_type1"));
				} 

				setStrId(strE);
				setIsbillable(uF.parseToBoolean(rs.getString("is_billable")));
				setStrClient(rs.getString("client_id"));
				
				setStrVendor(rs.getString("vendor"));
				
				String[] strReceiptNo = null;
				if (rs.getString("receipt_no") != null && rs.getString("receipt_no").length()>0) {
					strReceiptNo = rs.getString("receipt_no").split(":_:");
				}
				for (int k = 0; strReceiptNo != null && k < strReceiptNo.length; k++) {
					if(strReceiptNo[k]!=null && !strReceiptNo[k].trim().equals("") && !strReceiptNo[k].trim().equalsIgnoreCase("NULL")){
						alReceiptNo.add(strReceiptNo[k]);
					}
				}
				
				setTransportType(rs.getString("transport_type"));
				if(uF.parseToInt(rs.getString("transport_type")) == 1) {
					setTrainType(rs.getString("transport_mode"));
				} else if(uF.parseToInt(rs.getString("transport_type")) == 2) {
					setBusType(rs.getString("transport_mode"));
				} else if(uF.parseToInt(rs.getString("transport_type")) == 3) {
					setFlightType(rs.getString("transport_mode"));
				} else if(uF.parseToInt(rs.getString("transport_type")) == 4) {
					setCarType(rs.getString("transport_mode"));
				}
				
				setStrTransAmount(""+uF.parseToDouble(rs.getString("transport_amount")));
				setLodgingType(rs.getString("lodging_type"));
				setStrLodgingAmount(""+uF.parseToDouble(rs.getString("lodging_amount")));
				setLocalConveyanceTranType(rs.getString("local_conveyance_type"));
				setLocalConveyanceKM(""+uF.parseToDouble(rs.getString("local_conveyance_km")));
				setLocalConveyanceRate(""+uF.parseToDouble(rs.getString("local_conveyance_rate")));
				setStrLocalConveyanceAmount(""+uF.parseToDouble(rs.getString("local_conveyance_amount")));
				setStrFoodBeverageAmount(""+uF.parseToDouble(rs.getString("food_beverage_amount")));
				setStrLaundryAmount(""+uF.parseToDouble(rs.getString("laundry_amount")));
				setStrSundryAmount(""+uF.parseToDouble(rs.getString("sundry_amount")));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReceiptNo", alReceiptNo);
			
			if(getReimbursementType() != null && getReimbursementType().equalsIgnoreCase("T") && uF.parseToInt(getStrTravelPlan()) > 0) {
				double dblNoOfDays = 0.0d;
				pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and leave_id=? and istravel = true ");
				pst.setInt(1, nEmpId);
				pst.setInt(2, uF.parseToInt(getStrTravelPlan()));
				rs = pst.executeQuery();
				while(rs.next()) {
					dblNoOfDays = rs.getDouble("emp_no_of_leave");
				}
				rs.close();
				pst.close();
				
				setStrTravelPlanDays(""+dblNoOfDays);
			}
			
			pst = con.prepareStatement("select * from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId1()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setStrWLocation(rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();
			
			empNamesList1 = new FillEmployee(request).fillEmployeeNameByLocation(getStrWLocation(), false);
			clientList = new FillClients(request).fillClients(uF.parseToInt(getStrSelectedEmpId1()));
			projectList = new FillProjectList(request).fillProjectDetailsByEmp(uF.parseToInt(getStrSelectedEmpId1()), false, 0);

		} catch (Exception e) { 
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		//System.out.println("getFromDate==>"+getStrFromDate());
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrPurpose() {
		return strPurpose;
	}

	public void setStrPurpose(String strPurpose) {
		this.strPurpose = strPurpose;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getStrFrom() {
		return strFrom;
	}

	public void setStrFrom(String strFrom) {
		this.strFrom = strFrom;
	}

	public String getStrTo() {
		return strTo;
	}

	public void setStrTo(String strTo) {
		this.strTo = strTo;
	}

	public List<FillRimbursementType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<FillRimbursementType> typeList) {
		this.typeList = typeList;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getStrViewDocument() {
		return strViewDocument;
	}

	public void setStrViewDocument(String strViewDocument) {
		this.strViewDocument = strViewDocument;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getReimbursementType() {
		return reimbursementType;
	}

	public void setReimbursementType(String reimbursementType) {
		this.reimbursementType = reimbursementType;
	}

	public List<FillRimbursementType> getReimbursementTypeList() {
		return reimbursementTypeList;
	}

	public void setReimbursementTypeList(List<FillRimbursementType> reimbursementTypeList) {
		this.reimbursementTypeList = reimbursementTypeList;
	}

	public List<FillTravel> getTravelPlanList() {
		return travelPlanList;
	}

	public void setTravelPlanList(List<FillTravel> travelPlanList) {
		this.travelPlanList = travelPlanList;
	}

	public List<FillProjectList> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<FillProjectList> projectList) {
		this.projectList = projectList;
	}

	public String getStrTravelPlan() {
		return strTravelPlan;
	}

	public void setStrTravelPlan(String strTravelPlan) {
		this.strTravelPlan = strTravelPlan;
	}

	public String getStrProject() {
		return strProject;
	}

	public void setStrProject(String strProject) {
		this.strProject = strProject;
	}

	public File[] getStrDocument() {
		return strDocument;
	}

	public void setStrDocument(File[] strDocument) {
		this.strDocument = strDocument;
	}

	public String[] getStrDocumentContentType() {
		return strDocumentContentType;
	}

	public void setStrDocumentContentType(String[] strDocumentContentType) {
		this.strDocumentContentType = strDocumentContentType;
	}

	public String[] getStrDocumentFileName() {
		return strDocumentFileName;
	}

	public void setStrDocumentFileName(String[] strDocumentFileName) {
		this.strDocumentFileName = strDocumentFileName;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

	public List<FillEmployee> getEmpNamesList1() {
		return empNamesList1;
	}

	public void setEmpNamesList1(List<FillEmployee> empNamesList1) {
		this.empNamesList1 = empNamesList1;
	}

	public String getStrSelectedEmpId1() {
		return strSelectedEmpId1;
	}

	public void setStrSelectedEmpId1(String strSelectedEmpId1) {
		this.strSelectedEmpId1 = strSelectedEmpId1;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getStrf_WLocation() {
		return strf_WLocation;
	}

	public void setStrf_WLocation(String strf_WLocation) {
		this.strf_WLocation = strf_WLocation;
	}

	public String getStrClient() {
		return strClient;
	}

	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getPolicy_id() {
		return policy_id;
	}


	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}


	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}


	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getRID() {
		return RID;
	}


	public void setRID(String rID) {
		RID = rID;
	}


	public String getPaycycleDate() {
		return paycycleDate;
	}


	public void setPaycycleDate(String paycycleDate) {
		this.paycycleDate = paycycleDate;
	}


	public List<FillPayCycles> getPaycycleListFull() {
		return paycycleListFull;
	}


	public void setPaycycleListFull(List<FillPayCycles> paycycleListFull) {
		this.paycycleListFull = paycycleListFull;
	}


	public String getAlertStatus() {
		return alertStatus;
	}


	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}


	public String getAlert_type() {
		return alert_type;
	}


	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getStrVendor() {
		return strVendor;
	}

	public void setStrVendor(String strVendor) {
		this.strVendor = strVendor;
	}

	public String[] getStrReceiptNo() {
		return strReceiptNo;
	}

	public void setStrReceiptNo(String[] strReceiptNo) {
		this.strReceiptNo = strReceiptNo;
	}
	public boolean getIsbillable() {
		return isbillable;
	}

	public void setIsbillable(boolean isbillable) {
		this.isbillable = isbillable;
	}

	public String getNoofperson() {
		return noofperson;
	}

	public void setNoofperson(String noofperson) {
		this.noofperson = noofperson;
	}

	public String getPlacefrom() {
		return placefrom;
	}

	public void setPlacefrom(String placefrom) {
		this.placefrom = placefrom;
	}

	public String getPlaceto() {
		return placeto;
	}

	public void setPlaceto(String placeto) {
		this.placeto = placeto;
	}

	public String getNoofdays() {
		return noofdays;
	}

	public void setNoofdays(String noofdays) {
		this.noofdays = noofdays;
	}

	public String getKmpd() {
		return kmpd;
	}

	public void setKmpd(String kmpd) {
		this.kmpd = kmpd;
	}

	public String getRatepkm() {
		return ratepkm;
	}

	public void setRatepkm(String ratepkm) {
		this.ratepkm = ratepkm;
	}

	public String getModeoftravel() {
		return modeoftravel;
	}

	public void setModeoftravel(String modeoftravel) {
		this.modeoftravel = modeoftravel;
	}

	public List<FillRimbursementType> getModeoftravelList() {
		return modeoftravelList;
	}

	public void setModeoftravelList(List<FillRimbursementType> modeoftravelList) {
		this.modeoftravelList = modeoftravelList;
	}
	/*public String getStrPaycycle() {
		return strPaycycle;
	}

	public void setStrPaycycle(String strPaycycle) {
		this.strPaycycle = strPaycycle;
	}*/

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public List<FillLodgingType> getLodgingTypeList() {
		return lodgingTypeList;
	}

	public void setLodgingTypeList(List<FillLodgingType> lodgingTypeList) {
		this.lodgingTypeList = lodgingTypeList;
	}

	public List<FillRimbursementType> getLocalConveyanceTranTypeList() {
		return localConveyanceTranTypeList;
	}

	public void setLocalConveyanceTranTypeList(List<FillRimbursementType> localConveyanceTranTypeList) {
		this.localConveyanceTranTypeList = localConveyanceTranTypeList;
	}

	public String getStrUserTypeId() {
		return strUserTypeId;
	}

	public void setStrUserTypeId(String strUserTypeId) {
		this.strUserTypeId = strUserTypeId;
	}

	public String getStrBaseUserType() {
		return strBaseUserType;
	}

	public void setStrBaseUserType(String strBaseUserType) {
		this.strBaseUserType = strBaseUserType;
	}

	public String getStrBaseUserTypeId() {
		return strBaseUserTypeId;
	}

	public void setStrBaseUserTypeId(String strBaseUserTypeId) {
		this.strBaseUserTypeId = strBaseUserTypeId;
	}

	public String getStrTravelPlanDays() {
		return strTravelPlanDays;
	}

	public void setStrTravelPlanDays(String strTravelPlanDays) {
		this.strTravelPlanDays = strTravelPlanDays;
	}

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}

	public String getTrainType() {
		return trainType;
	}

	public void setTrainType(String trainType) {
		this.trainType = trainType;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getFlightType() {
		return flightType;
	}

	public void setFlightType(String flightType) {
		this.flightType = flightType;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getStrTransAmount() {
		return strTransAmount;
	}

	public void setStrTransAmount(String strTransAmount) {
		this.strTransAmount = strTransAmount;
	}

	public String getLodgingType() {
		return lodgingType;
	}

	public void setLodgingType(String lodgingType) {
		this.lodgingType = lodgingType;
	}

	public String getStrLodgingAmount() {
		return strLodgingAmount;
	}

	public void setStrLodgingAmount(String strLodgingAmount) {
		this.strLodgingAmount = strLodgingAmount;
	}

	public String getLocalConveyanceTranType() {
		return localConveyanceTranType;
	}

	public void setLocalConveyanceTranType(String localConveyanceTranType) {
		this.localConveyanceTranType = localConveyanceTranType;
	}

	public String getLocalConveyanceKM() {
		return localConveyanceKM;
	}

	public void setLocalConveyanceKM(String localConveyanceKM) {
		this.localConveyanceKM = localConveyanceKM;
	}
	
	public String getLocalConveyanceRate() {
		return localConveyanceRate;
	}

	public void setLocalConveyanceRate(String localConveyanceRate) {
		this.localConveyanceRate = localConveyanceRate;
	}

	public String getStrLocalConveyanceAmount() {
		return strLocalConveyanceAmount;
	}

	public void setStrLocalConveyanceAmount(String strLocalConveyanceAmount) {
		this.strLocalConveyanceAmount = strLocalConveyanceAmount;
	}

	public String getStrFoodBeverageAmount() {
		return strFoodBeverageAmount;
	}

	public void setStrFoodBeverageAmount(String strFoodBeverageAmount) {
		this.strFoodBeverageAmount = strFoodBeverageAmount;
	}

	public String getStrLaundryAmount() {
		return strLaundryAmount;
	}

	public void setStrLaundryAmount(String strLaundryAmount) {
		this.strLaundryAmount = strLaundryAmount;
	}

	public String getStrSundryAmount() {
		return strSundryAmount;
	}

	public void setStrSundryAmount(String strSundryAmount) {
		this.strSundryAmount = strSundryAmount;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getStrFromDate() {
		return strFromDate;
	}

	public void setStrFromDate(String strFromDate) {
		this.strFromDate = strFromDate;
	}

	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
	}

	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}

	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}

	public String getReimbCurrency() {
		return reimbCurrency;
	}

	public void setReimbCurrency(String reimbCurrency) {
		this.reimbCurrency = reimbCurrency;
	}

	public String getReimbPaymentMode() {
		return reimbPaymentMode;
	}

	public void setReimbPaymentMode(String reimbPaymentMode) {
		this.reimbPaymentMode = reimbPaymentMode;
	}

	
//	public String getStrToDate() {
//		return strToDate;
//	}
//
//	public void setStrToDate(String strToDate) {
//		this.strToDate = strToDate;
//	}
	
}
