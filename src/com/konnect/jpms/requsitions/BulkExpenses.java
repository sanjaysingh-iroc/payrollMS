package com.konnect.jpms.requsitions;

import java.io.File;
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

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillLodgingType;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillRimbursementType;
import com.konnect.jpms.select.FillTravel;
import com.konnect.jpms.task.FillProjectList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BulkExpenses extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strOrgCurrId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String strSessionEmpId = null;  
	
	private String btnSave;
	private String btnSubmit;
	private String btnRemove;
	
	List<FillPayCycles> paycycleList;
	private List<FillCurrency> currencyList;
	private List<FillPayMode> paymentModeList;
	
	List<FillRimbursementType> typeList;
	List<FillRimbursementType> modeoftravelList;
	
	List<FillLodgingType> lodgingTypeList;	
	List<FillRimbursementType> localConveyanceTranTypeList;
	
	List<FillTravel> travelPlanList;
	List<FillProjectList> projectList;
	List<FillClients> clientList;
	
	private String projectCount;
	private String travelCount;
	private String localCount;
	private String mobileCount;
	
	private String f_org;
	
	private String expenseId;
	private String policy_id;
	
	public String execute() throws Exception {
      
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, "Bulk Expenses");
		request.setAttribute(PAGE, "/jsp/requisitions/BulkExpenses.jsp");
		UtilityFunctions uF = new UtilityFunctions();
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"MyPay.action\" style=\"color: #3c8dbc;\"> My Pay</a></li>" +
			"<li class=\"active\">Bulk Expenses</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
//		System.out.println("getBtnSave() ===>> " + getBtnSave());
//		System.out.println("getBtnSubmit() ===>> " + getBtnSubmit());
		
		if(getBtnSave() != null) {
			saveBulkExpense();
		}
		
		if(getBtnSubmit() != null) {
			submitBulkExpense();
		}
		
		if(getBtnRemove() != null) {
			removeExpense();
		}
		
		getReimbursementsPolicyMember(uF);
		loadData(uF);
		getBulkExpense();
		return LOAD;
	}
	
	
	
	private void removeExpense() {
 		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from emp_reimbursement_draft where reimbursement_id = ?");
			pst.setInt(1, uF.parseToInt(getExpenseId()));
			pst.executeUpdate();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private void getBulkExpense() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, Map<String, List<List<String>>>> hmParentIdBulkExpenseData = new LinkedHashMap<String, Map<String, List<List<String>>>>();
			Map<String, List<List<String>>> hmBulkExpenseData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alBulkExpenseData = new ArrayList<List<String>>();
			Map<String, String> hmDraftSavedOn = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from emp_reimbursement_draft where emp_id = ? and submit_status = 0");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs=pst.executeQuery();
//			String parentId = "0";
			while(rs.next()) {
//				if(uF.parseToInt(parentId) == 0) {
//					parentId = rs.getString("parent_id");
//				}
				hmBulkExpenseData = hmParentIdBulkExpenseData.get(rs.getString("parent_id"));
				if(hmBulkExpenseData == null) hmBulkExpenseData = new HashMap<String, List<List<String>>>();
				
				alBulkExpenseData = hmBulkExpenseData.get(rs.getString("reimbursement_type1"));
				if(alBulkExpenseData == null) alBulkExpenseData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(rs.getString("from_date"), "")); // 0
				innerList.add(uF.showData(rs.getString("to_date"), ""));  // 1
				
				StringBuilder sbTypeList = new StringBuilder();
				sbTypeList.append("<option value=\"\">Select Type</option>");
				for(int i = 0; typeList!=null && i < typeList.size(); i++) {
					FillRimbursementType fillRimbursementType = typeList.get(i);
					if(rs.getString("reimbursement_type") != null && rs.getString("reimbursement_type").equals(fillRimbursementType.getTypeId())) {
						sbTypeList.append("<option value=\""+fillRimbursementType.getTypeId()+"\" selected>"+fillRimbursementType.getTypeName()+"</option>");
					} else {
						sbTypeList.append("<option value=\""+fillRimbursementType.getTypeId()+"\">"+fillRimbursementType.getTypeName()+"</option>");
					}
				}
				
				StringBuilder sbTravelPlanList = new StringBuilder();
				sbTravelPlanList.append("<option value=\"\">Select Travel Plan</option>");
				for(int i = 0; travelPlanList!=null && i < travelPlanList.size(); i++) {
					FillTravel fillTravel = travelPlanList.get(i);
					sbTravelPlanList.append("<option value=\""+fillTravel.getLeaveId()+"\">"+fillTravel.getPlanName()+"</option>");
				}
				if(rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equals("L")) {
					innerList.add(sbTypeList.toString()); //2
				} else if(rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equals("T")) {
					innerList.add(sbTravelPlanList.toString()); //2
				} else {
					innerList.add(""); //2
				}
				innerList.add(uF.showData(rs.getString("reimbursement_purpose"), "")); //3
				innerList.add(uF.showData(rs.getString("reimbursement_amount"), "")); //4
				innerList.add(uF.showData(rs.getString("entry_date"), "")); //5
				innerList.add(uF.showData(rs.getString("ref_document"), "")); //6
				innerList.add(uF.showData(rs.getString("reimbursement_type1"), "")); //7
				
				StringBuilder sbModeoftravelList = new StringBuilder();
				sbModeoftravelList.append("<option value=\"\">Select Mode</option>");
				for(int i = 0; modeoftravelList!=null && i < modeoftravelList.size(); i++) {
					FillRimbursementType fillModeOfTravel = modeoftravelList.get(i);
					if(rs.getString("travel_mode") != null && rs.getString("travel_mode").equals(fillModeOfTravel.getTypeId())) {
						sbModeoftravelList.append("<option value=\""+fillModeOfTravel.getTypeId()+"\" selected>"+fillModeOfTravel.getTypeName()+"</option>");
					} else {
						sbModeoftravelList.append("<option value=\""+fillModeOfTravel.getTypeId()+"\">"+fillModeOfTravel.getTypeName()+"</option>");
					}
				}
				innerList.add(sbModeoftravelList.toString()); //8
				innerList.add(uF.showData(rs.getString("no_person"), "")); //9
				innerList.add(uF.showData(rs.getString("travel_from"), "")); //10
				
				innerList.add(uF.showData(rs.getString("travel_to"), "")); //11
				innerList.add(uF.showData(rs.getString("no_days"), "")); //12
				innerList.add(uF.showData(rs.getString("travel_distance"), "")); //13
				innerList.add(uF.showData(rs.getString("travel_rate"), "")); //14
				innerList.add(uF.showData(rs.getString("reimbursement_info"), "")); //15
				innerList.add(uF.showData(rs.getString("is_billable"), "")); //16
				StringBuilder sbClientList = new StringBuilder();
				
				sbClientList.append("<option value=\"\">Select Client</option>");
				for(int i = 0; clientList!=null && i < clientList.size(); i++) {
					FillClients fillClients = clientList.get(i);
					if(rs.getString("client_id") != null && rs.getString("client_id").equals(fillClients.getClientId())) {
						sbClientList.append("<option value=\""+fillClients.getClientId()+"\" selected>"+fillClients.getClientName()+"</option>");
					} else {
						sbClientList.append("<option value=\""+fillClients.getClientId()+"\">"+fillClients.getClientName()+"</option>");
					}
				}
				innerList.add(sbClientList.toString()); //17
				
				projectList = new FillProjectList(request).fillProjectDetailsByEmp(uF.parseToInt((String) session.getAttribute(EMPID)), false, rs.getInt("client_id"));
				StringBuilder sbProjectList = new StringBuilder();
				sbProjectList.append("<option value=\"\">Select Project</option>");
				for(int i = 0; projectList!=null && i < projectList.size(); i++){
					FillProjectList fillProjectList = projectList.get(i);
					if(rs.getString("pro_id") != null && rs.getString("pro_id").equals(fillProjectList.getProjectID())) {
						sbProjectList.append("<option value=\""+fillProjectList.getProjectID()+"\" selected>"+fillProjectList.getProjectName()+"</option>");
					} else {
						sbProjectList.append("<option value=\""+fillProjectList.getProjectID()+"\">"+fillProjectList.getProjectName()+"</option>");
					}
				}
				innerList.add(sbProjectList.toString()); //18
				innerList.add(uF.showData(rs.getString("vendor"), "")); //19
				innerList.add(uF.showData(rs.getString("receipt_no"), "")); //20
				innerList.add(uF.showData(rs.getString("transport_type"), "")); //21
				innerList.add(uF.showData(rs.getString("transport_mode"), "")); //22
				innerList.add(uF.showData(rs.getString("transport_amount"), "")); //23
				
				StringBuilder sbLodgingTypeList = new StringBuilder();
				sbLodgingTypeList.append("<option value=\"\">Select Lodging Type</option>");
				for(int i = 0; lodgingTypeList!=null && i < lodgingTypeList.size(); i++) {
					FillLodgingType fillLodgingType = lodgingTypeList.get(i);
					if(rs.getString("lodging_type") != null && rs.getString("lodging_type").equals(fillLodgingType.getLodgingTypeId())) {
						sbLodgingTypeList.append("<option value=\""+fillLodgingType.getLodgingTypeId()+"\" selected>"+fillLodgingType.getLodgingTypeName()+"</option>");
					} else {
						sbLodgingTypeList.append("<option value=\""+fillLodgingType.getLodgingTypeId()+"\">"+fillLodgingType.getLodgingTypeName()+"</option>");
					}
				}
				innerList.add(sbLodgingTypeList.toString()); //24
				innerList.add(uF.showData(rs.getString("lodging_amount"), "")); //25
				
				StringBuilder sbLocalConveyanceTranTypeList = new StringBuilder();
				sbLocalConveyanceTranTypeList.append("<option value=\"\">Select Mode</option>");
				for(int i = 0; localConveyanceTranTypeList!=null && i < localConveyanceTranTypeList.size(); i++) {
					FillRimbursementType fillLocalConveyanceTransType = localConveyanceTranTypeList.get(i);
					if(rs.getString("local_conveyance_type") != null && rs.getString("local_conveyance_type").equals(fillLocalConveyanceTransType.getTypeId())) {
						sbLocalConveyanceTranTypeList.append("<option value=\""+fillLocalConveyanceTransType.getTypeId()+"\" selected>"+fillLocalConveyanceTransType.getTypeName()+"</option>");
					} else {
						sbLocalConveyanceTranTypeList.append("<option value=\""+fillLocalConveyanceTransType.getTypeId()+"\">"+fillLocalConveyanceTransType.getTypeName()+"</option>");
					}
				}
				innerList.add(sbLocalConveyanceTranTypeList.toString()); //26
				innerList.add(uF.showData(rs.getString("local_conveyance_km"), "")); //27
				innerList.add(uF.showData(rs.getString("local_conveyance_rate"), "")); //28
				innerList.add(uF.showData(rs.getString("local_conveyance_amount"), "")); //29
				innerList.add(uF.showData(rs.getString("food_beverage_amount"), "")); //30
				
				innerList.add(uF.showData(rs.getString("laundry_amount"), "")); //31
				innerList.add(uF.showData(rs.getString("sundry_amount"), "")); //32
				innerList.add(uF.showData(rs.getString("parent_id"), "")); //33
				innerList.add(uF.showData(rs.getString("emp_id"), "")); //34
				innerList.add(uF.showData(rs.getString("reimbursement_type"), "")); //35
				innerList.add(uF.showData(rs.getString("reimbursement_id"), "")); //36
				innerList.add(uF.showData((rs.getString("reimb_from_date") != null && !rs.getString("reimb_from_date").trim().equals("")) ? uF.getDateFormat(rs.getString("reimb_from_date"), DBDATE, DATE_FORMAT) : "", "")); //37
				innerList.add(uF.showData((rs.getString("reimb_to_date") != null && !rs.getString("reimb_to_date").trim().equals("")) ? uF.getDateFormat(rs.getString("reimb_to_date"), DBDATE, DATE_FORMAT) : "", "")); //38
				StringBuilder sbReimbCurrencyList = new StringBuilder();
//				sbReimbCurrencyList.append("<option value=\"\">Select Currency</option>");
				for(int i = 0; currencyList!=null && i < currencyList.size(); i++) {
					FillCurrency fillCurrency = currencyList.get(i);
					if(rs.getString("reimb_currency") != null && rs.getString("reimb_currency").equals(fillCurrency.getCurrencyId())) {
						sbReimbCurrencyList.append("<option value=\""+fillCurrency.getCurrencyId()+"\" selected>"+fillCurrency.getCurrencyName()+"</option>");
					} else {
						sbReimbCurrencyList.append("<option value=\""+fillCurrency.getCurrencyId()+"\">"+fillCurrency.getCurrencyName()+"</option>");
					}
				}
				innerList.add(sbReimbCurrencyList.toString()); //39
				StringBuilder sbPaymentModeList = new StringBuilder();
//				sbPaymentModeList.append("<option value=\"\">Select Payment Mode</option>");
				for(int i = 0; paymentModeList!=null && i < paymentModeList.size(); i++) {
					FillPayMode fillPaymentMode = paymentModeList.get(i);
					if(rs.getString("reimb_payment_mode") != null && rs.getString("reimb_payment_mode").equals(fillPaymentMode.getPayModeId())) {
						sbPaymentModeList.append("<option value=\""+fillPaymentMode.getPayModeId()+"\" selected>"+fillPaymentMode.getPayModeName()+"</option>");
					} else {
						sbPaymentModeList.append("<option value=\""+fillPaymentMode.getPayModeId()+"\">"+fillPaymentMode.getPayModeName()+"</option>");
					}
				}
				innerList.add(sbPaymentModeList.toString()); //40
				innerList.add(uF.showData(rs.getString("ref_document"), "")); //41
				
				alBulkExpenseData.add(innerList);
				
				hmDraftSavedOn.put(rs.getString("parent_id"), uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT_STR));
				hmBulkExpenseData.put(rs.getString("reimbursement_type1"), alBulkExpenseData);
				
				hmParentIdBulkExpenseData.put(rs.getString("parent_id"), hmBulkExpenseData);
			}
			rs.close();
			pst.close();

//			request.setAttribute("parentId", parentId);
			request.setAttribute("hmDraftSavedOn", hmDraftSavedOn);
			request.setAttribute("hmParentIdBulkExpenseData", hmParentIdBulkExpenseData);
			request.setAttribute("hmBulkExpenseData", hmBulkExpenseData);
			
//			System.out.println("hmBulkExpenseData ===>> " + hmBulkExpenseData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private void submitBulkExpense() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
//			System.out.println("getTravelCount() ===>>>> " + getTravelCount());
//			System.out.println("getLocalCount() ===>>>> " + getLocalCount());
//			System.out.println("getMobileCount() ===>>>> " + getMobileCount());
//			System.out.println("getProjectCount() ===>>>> " + getProjectCount());
			int intParentId = 0;
			pst = con.prepareStatement("select max(parent_id)as parent_id from emp_reimbursement_draft");
			rs=pst.executeQuery();
			while(rs.next()) {
				intParentId = rs.getInt("parent_id");
			}
			rs.close();
			pst.close();
			intParentId++;
			
			String parentId = request.getParameter("parentId");
			if(uF.parseToInt(parentId)>0) {
				intParentId = uF.parseToInt(parentId);
			}
			
			double strExpensesAmount = 0.0d;
			String strExpenseCurr = null;
			String expStartDate = null;
			String expEndDate = null;
			String[] strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			for(int i = 1 ; i <= uF.parseToInt(getTravelCount()); i++) {
				String strAmount = (String) request.getParameter("strAmount_travel"+i);
				String reimbCurrency = (String) request.getParameter("reimbCurrency_travel"+i);
				String reimbPaymentMode = (String) request.getParameter("reimbPaymentMode_travel"+i);
//				System.out.println("strAmount ===>> " + strAmount);
				if(uF.parseToDouble(strAmount) == 0) {
					continue;
				}
				strExpensesAmount += uF.parseToDouble(strAmount);
				if(strExpenseCurr == null) {
					strExpenseCurr = reimbCurrency;
				}
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//				System.out.println("request==>"+request.getClass());
//				System.out.println("mpRequest==>"+mpRequest.getClass());
				File[] files = mpRequest.getFiles("strDocument_travel"+i);    //  
				String[] fileNames = mpRequest.getFileNames("strDocument_travel"+i); 
				
				StringBuilder sbFileName = new StringBuilder(); 
				for (int ii = 0; files != null && ii < files.length; ii++) {
					if(CF.getStrDocSaveLocation()==null) {
						sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} else {
						sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					}
				}
//				String reimbursementType = (String) request.getParameter("reimbursementType");
				String startDate = strPayCycleDates[0];
				String endDate = strPayCycleDates[1];
				if(expStartDate == null && expEndDate == null) {
					expStartDate = strPayCycleDates[0];
					expEndDate = strPayCycleDates[1];
				}
				
				String strReimbursementId = (String) request.getParameter("travelReimbursementId"+i);
				String strClient = (String) request.getParameter("strClient_travel"+i);
				String strProject = (String) request.getParameter("strProject_travel"+i);
				String strTravelPlan = (String) request.getParameter("strTravelPlan_travel"+i);
				String strType = (String) request.getParameter("strType_travel"+i);
				String modeoftravel = (String) request.getParameter("modeoftravel_travel"+i);
				String fromDate = (String) request.getParameter("fromDate_travel"+i);
				String toDate = (String) request.getParameter("toDate_travel"+i);
				String noofperson = (String) request.getParameter("noofperson_travel"+i);
				String placefrom = (String) request.getParameter("placefrom_travel"+i);
				String placeto = (String) request.getParameter("placeto_travel"+i);
				String noofdays = (String) request.getParameter("noofdays_travel"+i);
				String kmpd = (String) request.getParameter("kmpd_travel"+i);
				String ratepkm = (String) request.getParameter("ratepkm_travel"+i);
				String strPurpose = (String) request.getParameter("strPurpose_travel"+i);
				String isbillable = (String) request.getParameter("isbillable_travel"+i);
				String strVendor = (String) request.getParameter("strVendor_travel"+i);
				
				String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo_travel"+i);
				StringBuilder sbReceiptNo = new StringBuilder();
				for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
					if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")){
						sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
					}
				}
				
				int nTransportType = uF.parseToInt((String)request.getParameter("transportType_travel"+i));
				int nTransportMode = 0;
				double dblTransportAmount = 0.0d;
				int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType_travel"+i));
				double dblLodgingAmount = 0.0d;
				String strLocalConveyanceType = null;
				double dblLocalConveyanceKm = 0.0d;
				double dblLocalConveyanceRate = 0.0d;
				double dblLocalConveyanceAmount = 0.0d;
				double dblFoodBeverageAmount = 0.0d;
				double dblLaundryAmount = 0.0d;
				double dblSundryAmount = 0.0d;

				if(nTransportType == 1) {
					nTransportMode = uF.parseToInt((String)request.getParameter("trainType_travel"+i));
					dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount_travel"+i));
				} else if(nTransportType == 2) {
					nTransportMode = uF.parseToInt((String)request.getParameter("busType_travel"+i));
					dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount_travel"+i));
				} else if(nTransportType == 3) {
					nTransportMode = uF.parseToInt((String)request.getParameter("flightType_travel"+i));
					dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount_travel"+i));
				} else if(nTransportType == 4) {
					nTransportMode = uF.parseToInt((String)request.getParameter("carType_travel"+i));
					dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount_travel"+i));
				}
				
				dblLodgingAmount = uF.parseToDouble((String)request.getParameter("strLodgingAmount_travel"+i));
				
				strLocalConveyanceType = (String)request.getParameter("localConveyanceTranType_travel"+i);
				if(strLocalConveyanceType!=null && !strLocalConveyanceType.trim().equals("")) {
					dblLocalConveyanceKm = uF.parseToDouble((String)request.getParameter("localConveyanceKM_travel"+i));
					dblLocalConveyanceRate = uF.parseToDouble((String)request.getParameter("localConveyanceRate_travel"+i));
					dblLocalConveyanceAmount = uF.parseToDouble((String)request.getParameter("strLocalConveyanceAmount_travel"+i));
				}
				
				dblFoodBeverageAmount = uF.parseToDouble((String)request.getParameter("strFoodBeverageAmount_travel"+i));
				dblLaundryAmount = uF.parseToDouble((String)request.getParameter("strLaundryAmount_travel"+i));
				dblSundryAmount = uF.parseToDouble((String)request.getParameter("strSundryAmount_travel"+i));
				
				if(uF.parseToInt(strReimbursementId) == 0) {
					pst = con.prepareStatement("insert into emp_reimbursement_draft (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,submit_status," +
						"submited_by,submit_date,reimb_from_date,reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strTravelPlan);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "T");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setInt(36, 1);
					pst.setInt(37, uF.parseToInt(strSessionEmpId));
					pst.setDate(38, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(39, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(40, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(41, uF.parseToInt(reimbCurrency));
					pst.setInt(42, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strTravelPlan);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "T");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
//					System.out.println("pst ====>>> " + pst);
					
					String reimbursementId=null;
					pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
					rs=pst.executeQuery();
					while(rs.next()){
						reimbursementId=rs.getString("reimbursement_id");
					}
					rs.close();
					pst.close();
					insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
//					insertWorkflowData(con, uF, pst, rs, reimbursementId, strAmount, startDate, endDate, strPurpose, strType);
				} else {
					pst = con.prepareStatement("update emp_reimbursement_draft set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
						"reimbursement_amount=?, emp_id=?, entry_date=?, reimbursement_type1=?, travel_mode=?, no_person=?, travel_from=?, " +
						"travel_to=?, no_days=?, travel_distance=?, travel_rate=?, reimbursement_info=?, is_billable=?, client_id=?, pro_id=?, vendor=?, " +
						"receipt_no=?, transport_type=?, transport_mode=?, transport_amount=?, lodging_type=?, lodging_amount=?, local_conveyance_type=?, " +
						"local_conveyance_km=?, local_conveyance_rate=?, local_conveyance_amount=?, food_beverage_amount=?, laundry_amount=?, " +
						"sundry_amount=?, parent_id=?,submit_status=?,submited_by=?,submit_date=?,reimb_from_date=?,reimb_to_date=?,reimb_currency=?," +
						"reimb_payment_mode=? where reimbursement_id=?"); // ref_document=?, 
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strTravelPlan);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(8, sbFileName.toString());
					pst.setString(8, "T");
					pst.setString(9, modeoftravel);
					pst.setInt(10, uF.parseToInt(noofperson));
					pst.setString(11, placefrom);
					pst.setString(12, placeto);
					pst.setInt(13, uF.parseToInt(noofdays));
					pst.setDouble(14, uF.parseToDouble(kmpd));
					pst.setDouble(15, uF.parseToDouble(ratepkm));
					pst.setString(16, strType);
					pst.setBoolean(17, uF.parseToBoolean(isbillable));
					pst.setInt(18, uF.parseToInt(strClient));
					pst.setInt(19, 0);
					pst.setString(20, strVendor);
					pst.setString(21, sbReceiptNo.toString());
					pst.setInt(22, nTransportType);
					pst.setInt(23, nTransportMode);
					pst.setDouble(24, dblTransportAmount);
					pst.setInt(25, nLodgingType);
					pst.setDouble(26, dblLodgingAmount);
					pst.setString(27, strLocalConveyanceType);
					pst.setDouble(28, dblLocalConveyanceKm);
					pst.setDouble(29, dblLocalConveyanceRate);
					pst.setDouble(30, dblLocalConveyanceAmount);
					pst.setDouble(31, dblFoodBeverageAmount);
					pst.setDouble(32, dblLaundryAmount);
					pst.setDouble(33, dblSundryAmount);
					pst.setInt(34, intParentId);
					pst.setInt(35, 1);
					pst.setInt(36, uF.parseToInt(strSessionEmpId));
					pst.setDate(37, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(38, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(39, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(40, uF.parseToInt(reimbCurrency));
					pst.setInt(41, uF.parseToInt(reimbPaymentMode));
					pst.setInt(42, uF.parseToInt(strReimbursementId));
					pst.executeUpdate();
					
					String refDocument = "";
					pst = con.prepareStatement("select ref_document from emp_reimbursement_draft where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strReimbursementId));
					rs=pst.executeQuery();
					while(rs.next()) {
						refDocument = rs.getString("ref_document");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("update emp_reimbursement_draft set ref_document=? where reimbursement_id=?"); 
					pst.setString(1, refDocument+sbFileName.toString());
					pst.setInt(2, uF.parseToInt(strReimbursementId));
					pst.executeUpdate();
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strTravelPlan);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, refDocument+sbFileName.toString());
					pst.setString(9, "T");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					String reimbursementId=null;
					pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
					rs=pst.executeQuery();
					while(rs.next()){
						reimbursementId=rs.getString("reimbursement_id");
					}
					rs.close();
					pst.close();
					insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
//					insertWorkflowData(con, uF, pst, rs, reimbursementId, strAmount, startDate, endDate, strPurpose, strType);
				}
			}
			
			
			for(int i = 1 ; i <= uF.parseToInt(getLocalCount()); i++) {
				String strAmount = (String) request.getParameter("strAmount_local"+i);
				String reimbCurrency = (String) request.getParameter("reimbCurrency_local"+i);
				String reimbPaymentMode = (String) request.getParameter("reimbPaymentMode_local"+i);
				if(uF.parseToDouble(strAmount) == 0) {
					continue;
				}
				strExpensesAmount += uF.parseToDouble(strAmount);
				if(strExpenseCurr == null) {
					strExpenseCurr = reimbCurrency;
				}
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//				System.out.println("request==>"+request.getClass());
//				System.out.println("mpRequest==>"+mpRequest.getClass());
				File[] files = mpRequest.getFiles("strDocument_local"+i);    //  
				String[] fileNames = mpRequest.getFileNames("strDocument_local"+i); 
				
				StringBuilder sbFileName = new StringBuilder(); 
				for (int ii = 0; files != null && ii < files.length; ii++) {
					if(CF.getStrDocSaveLocation()==null) {
						sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} else {
						sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} 
				}  
				String reimbursementType = (String) request.getParameter("reimbursementType");
				String startDate = null;
				String endDate = null;
				if(reimbursementType!=null && reimbursementType.equals("P")) {
					String paycycle = (String) request.getParameter("paycycle");
					String[] arr = paycycle.split("-");
					startDate = arr[0];
					endDate = arr[1];
					if(expStartDate == null && expEndDate == null) {
						expStartDate = strPayCycleDates[0];
						expEndDate = strPayCycleDates[1];
					}
				} else {
					startDate = strPayCycleDates[0];
					endDate = strPayCycleDates[1];
					if(expStartDate == null && expEndDate == null) {
						expStartDate = strPayCycleDates[0];
						expEndDate = strPayCycleDates[1];
					}
				}
				
				String strReimbursementId = (String) request.getParameter("localReimbursementId"+i);
				String strClient = (String) request.getParameter("strClient_local"+i);
				String strProject = (String) request.getParameter("strProject_local"+i);
				String strTravelPlan = (String) request.getParameter("strTravelPlan_local"+i);
				String strType = (String) request.getParameter("strType_local"+i);
				String modeoftravel = (String) request.getParameter("modeoftravel_local"+i);
				String fromDate = (String) request.getParameter("fromDate_local"+i);
				String toDate = (String) request.getParameter("toDate_local"+i);
				String noofperson = (String) request.getParameter("noofperson_local"+i);
				String placefrom = (String) request.getParameter("placefrom_local"+i);
				String placeto = (String) request.getParameter("placeto_local"+i);
				String noofdays = (String) request.getParameter("noofdays_local"+i);
				String kmpd = (String) request.getParameter("kmpd_local"+i);
				String ratepkm = (String) request.getParameter("ratepkm_local"+i);
				String strPurpose = (String) request.getParameter("strPurpose_local"+i);
				String isbillable = (String) request.getParameter("isbillable_local"+i);
				String strVendor = (String) request.getParameter("strVendor_local"+i);
				
				String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo_local"+i);
				StringBuilder sbReceiptNo = new StringBuilder();
				for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
					if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")){
						sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
					}
				}
				
				int nTransportType = uF.parseToInt((String)request.getParameter("transportType_local"+i));
				int nTransportMode = 0;
				double dblTransportAmount = 0.0d;
				int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType_local"+i));
				double dblLodgingAmount = 0.0d;
				String strLocalConveyanceType = null;
				double dblLocalConveyanceKm = 0.0d;
				double dblLocalConveyanceRate = 0.0d;
				double dblLocalConveyanceAmount = 0.0d;
				double dblFoodBeverageAmount = 0.0d;
				double dblLaundryAmount = 0.0d;
				double dblSundryAmount = 0.0d;
				
				if(uF.parseToInt(strReimbursementId) == 0) {
					pst = con.prepareStatement("insert into emp_reimbursement_draft (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,submit_status," +
						"submited_by,submit_date,reimb_from_date,reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strType);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "L");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setInt(36, 1);
					pst.setInt(37, uF.parseToInt(strSessionEmpId));
					pst.setDate(38, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(39, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(40, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(41, uF.parseToInt(reimbCurrency));
					pst.setInt(42, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strType);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "L");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					String reimbursementId=null;
					pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
					rs=pst.executeQuery();
					while(rs.next()){
						reimbursementId=rs.getString("reimbursement_id");
					}
					rs.close();
					pst.close();
					insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
//					insertWorkflowData(con, uF, pst, rs, reimbursementId, strAmount, startDate, endDate, strPurpose, strType);
				} else {
					pst = con.prepareStatement("update emp_reimbursement_draft set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
						"reimbursement_amount=?, emp_id=?, entry_date=?, reimbursement_type1=?, travel_mode=?, no_person=?, travel_from=?, " +
						"travel_to=?, no_days=?, travel_distance=?, travel_rate=?, reimbursement_info=?, is_billable=?, client_id=?, pro_id=?, vendor=?, " +
						"receipt_no=?, transport_type=?, transport_mode=?, transport_amount=?, lodging_type=?, lodging_amount=?, local_conveyance_type=?, " +
						"local_conveyance_km=?, local_conveyance_rate=?, local_conveyance_amount=?, food_beverage_amount=?, laundry_amount=?, " +
						"sundry_amount=?, parent_id=?,submit_status=?,submited_by=?,submit_date=?,reimb_from_date=?,reimb_to_date=?,reimb_currency=?," +
						"reimb_payment_mode=? where reimbursement_id=?"); //ref_document=?, 
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strType);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(8, sbFileName.toString());
					pst.setString(8, "L");
					pst.setString(9, modeoftravel);
					pst.setInt(10, uF.parseToInt(noofperson));
					pst.setString(11, placefrom);
					pst.setString(12, placeto);
					pst.setInt(13, uF.parseToInt(noofdays));
					pst.setDouble(14, uF.parseToDouble(kmpd));
					pst.setDouble(15, uF.parseToDouble(ratepkm));
					pst.setString(16, strType);
					pst.setBoolean(17, uF.parseToBoolean(isbillable));
					pst.setInt(18, uF.parseToInt(strClient));
					pst.setInt(19, 0);
					pst.setString(20, strVendor);
					pst.setString(21, sbReceiptNo.toString());
					pst.setInt(22, nTransportType);
					pst.setInt(23, nTransportMode);
					pst.setDouble(24, dblTransportAmount);
					pst.setInt(25, nLodgingType);
					pst.setDouble(26, dblLodgingAmount);
					pst.setString(27, strLocalConveyanceType);
					pst.setDouble(28, dblLocalConveyanceKm);
					pst.setDouble(29, dblLocalConveyanceRate);
					pst.setDouble(30, dblLocalConveyanceAmount);
					pst.setDouble(31, dblFoodBeverageAmount);
					pst.setDouble(32, dblLaundryAmount);
					pst.setDouble(33, dblSundryAmount);
					pst.setInt(34, intParentId);
					pst.setInt(35, 1);
					pst.setInt(36, uF.parseToInt(strSessionEmpId));
					pst.setDate(37, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(38, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(39, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(40, uF.parseToInt(reimbCurrency));
					pst.setInt(41, uF.parseToInt(reimbPaymentMode));
					pst.setInt(42, uF.parseToInt(strReimbursementId));
					pst.executeUpdate();
					
					String refDocument = "";
					pst = con.prepareStatement("select ref_document from emp_reimbursement_draft where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strReimbursementId));
					rs=pst.executeQuery();
					while(rs.next()) {
						refDocument = rs.getString("ref_document");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("update emp_reimbursement_draft set ref_document=? where reimbursement_id=?"); 
					pst.setString(1, refDocument+sbFileName.toString());
					pst.setInt(2, uF.parseToInt(strReimbursementId));
					pst.executeUpdate();
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strType);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, refDocument+sbFileName.toString());
					pst.setString(9, "L");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					String reimbursementId=null;
					pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
					rs=pst.executeQuery();
					while(rs.next()){
						reimbursementId=rs.getString("reimbursement_id");
					}
					rs.close();
					pst.close();
					insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
//					insertWorkflowData(con, uF, pst, rs, reimbursementId, strAmount, startDate, endDate, strPurpose, strType);
				}
			}
			
			
			for(int i = 1 ; i <= uF.parseToInt(getMobileCount()); i++) {
				String strAmount = (String) request.getParameter("strAmount_mobile"+i);
				String reimbCurrency = (String) request.getParameter("reimbCurrency_mobile"+i);
				String reimbPaymentMode = (String) request.getParameter("reimbPaymentMode_mobile"+i);
				if(uF.parseToDouble(strAmount) == 0) {
					continue;
				}
				strExpensesAmount += uF.parseToDouble(strAmount);
				if(strExpenseCurr == null) {
					strExpenseCurr = reimbCurrency;
				}
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//				System.out.println("request==>"+request.getClass());
//				System.out.println("mpRequest==>"+mpRequest.getClass());
				File[] files = mpRequest.getFiles("strDocument_mobile"+i);    //  
				String[] fileNames = mpRequest.getFileNames("strDocument_mobile"+i); 
				
				StringBuilder sbFileName = new StringBuilder(); 
				for (int ii = 0; files != null && ii < files.length; ii++) {
					if(CF.getStrDocSaveLocation()==null) {
						sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} else {
						sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} 
				}  
				String reimbursementType = (String) request.getParameter("reimbursementType");
				String startDate = null;
				String endDate = null;
				if(reimbursementType!=null && reimbursementType.equals("P")) {
					String paycycle = (String) request.getParameter("paycycle");
					String[] arr = paycycle.split("-");
					startDate = arr[0];
					endDate = arr[1];
					if(expStartDate == null && expEndDate == null) {
						expStartDate = strPayCycleDates[0];
						expEndDate = strPayCycleDates[1];
					}
				} else {
					startDate = strPayCycleDates[0];
					endDate = strPayCycleDates[1];
					if(expStartDate == null && expEndDate == null) {
						expStartDate = strPayCycleDates[0];
						expEndDate = strPayCycleDates[1];
					}
				}
				
				String strReimbursementId = (String) request.getParameter("mobileReimbursementId"+i);
				String strClient = (String) request.getParameter("strClient_mobile"+i);
				String strProject = (String) request.getParameter("strProject_mobile"+i);
				String strTravelPlan = (String) request.getParameter("strTravelPlan_mobile"+i);
				String strType = (String) request.getParameter("strType_mobile"+i);
				String modeoftravel = (String) request.getParameter("modeoftravel_mobile"+i);
				String fromDate = (String) request.getParameter("fromDate_mobile"+i);
				String toDate = (String) request.getParameter("toDate_mobile"+i);
				String noofperson = (String) request.getParameter("noofperson_mobile"+i);
				String placefrom = (String) request.getParameter("placefrom_mobile"+i);
				String placeto = (String) request.getParameter("placeto_mobile"+i);
				String noofdays = (String) request.getParameter("noofdays_mobile"+i);
				String kmpd = (String) request.getParameter("kmpd_mobile"+i);
				String ratepkm = (String) request.getParameter("ratepkm_mobile"+i);
				String strPurpose = (String) request.getParameter("strPurpose_mobile"+i);
				String isbillable = (String) request.getParameter("isbillable_mobile"+i);
				String strVendor = (String) request.getParameter("strVendor_mobile"+i);
				
				String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo_mobile"+i);
				StringBuilder sbReceiptNo = new StringBuilder();
				for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
					if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")){
						sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
					}
				}
				
				int nTransportType = uF.parseToInt((String)request.getParameter("transportType_mobile"+i));
				int nTransportMode = 0;
				double dblTransportAmount = 0.0d;
				int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType_mobile"+i));
				double dblLodgingAmount = 0.0d;
				String strLocalConveyanceType = null;
				double dblLocalConveyanceKm = 0.0d;
				double dblLocalConveyanceRate = 0.0d;
				double dblLocalConveyanceAmount = 0.0d;
				double dblFoodBeverageAmount = 0.0d;
				double dblLaundryAmount = 0.0d;
				double dblSundryAmount = 0.0d;
				
				if(uF.parseToInt(strReimbursementId) == 0) {
					pst = con.prepareStatement("insert into emp_reimbursement_draft (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,submit_status," +
						"submited_by,submit_date,reimb_from_date,reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, "Mobile Bill");
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "M");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, "Mobile Bill");
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setInt(36, 1);
					pst.setInt(37, uF.parseToInt(strSessionEmpId));
					pst.setDate(38, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(39, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(40, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(41, uF.parseToInt(reimbCurrency));
					pst.setInt(42, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, "Mobile Bill");
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "M");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, "Mobile Bill");
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					String reimbursementId=null;
					pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
					rs=pst.executeQuery();
					while(rs.next()){
						reimbursementId=rs.getString("reimbursement_id");
					}
					rs.close();
					pst.close();
					insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
//					insertWorkflowData(con, uF, pst, rs, reimbursementId, strAmount, startDate, endDate, strPurpose, strType);
				} else {
					pst = con.prepareStatement("update emp_reimbursement_draft set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
						"reimbursement_amount=?, emp_id=?, entry_date=?, reimbursement_type1=?, travel_mode=?, no_person=?, travel_from=?, " +
						"travel_to=?, no_days=?, travel_distance=?, travel_rate=?, reimbursement_info=?, is_billable=?, client_id=?, pro_id=?, vendor=?, " +
						"receipt_no=?, transport_type=?, transport_mode=?, transport_amount=?, lodging_type=?, lodging_amount=?, local_conveyance_type=?, " +
						"local_conveyance_km=?, local_conveyance_rate=?, local_conveyance_amount=?, food_beverage_amount=?, laundry_amount=?, " +
						"sundry_amount=?, parent_id=?,submit_status=?,submited_by=?,submit_date=?,reimb_from_date=?,reimb_to_date=?,reimb_currency=?," +
						"reimb_payment_mode=? where reimbursement_id=?"); // ref_document=?, 
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, "Mobile Bill");
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(8, sbFileName.toString());
					pst.setString(8, "M");
					pst.setString(9, modeoftravel);
					pst.setInt(10, uF.parseToInt(noofperson));
					pst.setString(11, placefrom);
					pst.setString(12, placeto);
					pst.setInt(13, uF.parseToInt(noofdays));
					pst.setDouble(14, uF.parseToDouble(kmpd));
					pst.setDouble(15, uF.parseToDouble(ratepkm));
					pst.setString(16, "Mobile Bill");
					pst.setBoolean(17, uF.parseToBoolean(isbillable));
					pst.setInt(18, uF.parseToInt(strClient));
					pst.setInt(19, 0);
					pst.setString(20, strVendor);
					pst.setString(21, sbReceiptNo.toString());
					pst.setInt(22, nTransportType);
					pst.setInt(23, nTransportMode);
					pst.setDouble(24, dblTransportAmount);
					pst.setInt(25, nLodgingType);
					pst.setDouble(26, dblLodgingAmount);
					pst.setString(27, strLocalConveyanceType);
					pst.setDouble(28, dblLocalConveyanceKm);
					pst.setDouble(29, dblLocalConveyanceRate);
					pst.setDouble(30, dblLocalConveyanceAmount);
					pst.setDouble(31, dblFoodBeverageAmount);
					pst.setDouble(32, dblLaundryAmount);
					pst.setDouble(33, dblSundryAmount);
					pst.setInt(34, intParentId);
					pst.setInt(35, 1);
					pst.setInt(36, uF.parseToInt(strSessionEmpId));
					pst.setDate(37, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(38, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(39, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(40, uF.parseToInt(reimbCurrency));
					pst.setInt(41, uF.parseToInt(reimbPaymentMode));
					pst.setInt(42, uF.parseToInt(strReimbursementId));
					pst.executeUpdate();
					
					String refDocument = "";
					pst = con.prepareStatement("select ref_document from emp_reimbursement_draft where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strReimbursementId));
					rs=pst.executeQuery();
					while(rs.next()) {
						refDocument = rs.getString("ref_document");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("update emp_reimbursement_draft set ref_document=? where reimbursement_id=?"); 
					pst.setString(1, refDocument+sbFileName.toString());
					pst.setInt(2, uF.parseToInt(strReimbursementId));
					pst.executeUpdate();
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, "Mobile Bill");
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, refDocument+sbFileName.toString());
					pst.setString(9, "M");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, "Mobile Bill");
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					String reimbursementId=null;
					pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
					rs=pst.executeQuery();
					while(rs.next()){
						reimbursementId=rs.getString("reimbursement_id");
					}
					rs.close();
					pst.close();
					insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
//					insertWorkflowData(con, uF, pst, rs, reimbursementId, strAmount, startDate, endDate, strPurpose, strType);
				}
			}
			
			
			for(int i = 1 ; i <= uF.parseToInt(getProjectCount()); i++) {
				String strAmount = (String) request.getParameter("strAmount_project"+i);
				String reimbCurrency = (String) request.getParameter("reimbCurrency_project"+i);
				String reimbPaymentMode = (String) request.getParameter("reimbPaymentMode_project"+i);
				if(uF.parseToDouble(strAmount) == 0) {
					continue;
				}
				strExpensesAmount += uF.parseToDouble(strAmount);
				if(strExpenseCurr == null) {
					strExpenseCurr = reimbCurrency;
				}
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//				System.out.println("request==>"+request.getClass());
//				System.out.println("mpRequest==>"+mpRequest.getClass());
				File[] files = mpRequest.getFiles("strDocument_project"+i);    //  
				String[] fileNames = mpRequest.getFileNames("strDocument_project"+i); 
				
				StringBuilder sbFileName = new StringBuilder(); 
				for (int ii = 0; files != null && ii < files.length; ii++) {
					if(CF.getStrDocSaveLocation()==null) {
						sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} else {
						sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} 
				}  
				String reimbursementType = (String) request.getParameter("reimbursementType");
				String startDate = null;
				String endDate = null;
				if(reimbursementType!=null && reimbursementType.equals("P")) {
					String paycycle = (String) request.getParameter("paycycle");
					String[] arr = paycycle.split("-");
					startDate = arr[0];
					endDate = arr[1];
					if(expStartDate == null && expEndDate == null) {
						expStartDate = strPayCycleDates[0];
						expEndDate = strPayCycleDates[1];
					}
				} else {
					startDate = strPayCycleDates[0];
					endDate = strPayCycleDates[1];
					if(expStartDate == null && expEndDate == null) {
						expStartDate = strPayCycleDates[0];
						expEndDate = strPayCycleDates[1];
					}
				}
				
				String strReimbursementId = (String) request.getParameter("projectReimbursementId"+i);
				String strClient = (String) request.getParameter("strClient_project"+i);
				String strProject = (String) request.getParameter("strProject_project"+i);
				String strTravelPlan = (String) request.getParameter("strTravelPlan_project"+i);
				String strType = (String) request.getParameter("strType_project"+i);
				String modeoftravel = (String) request.getParameter("modeoftravel_project"+i);
				String fromDate = (String) request.getParameter("fromDate_project"+i);
				String toDate = (String) request.getParameter("toDate_project"+i);
				String noofperson = (String) request.getParameter("noofperson_project"+i);
				String placefrom = (String) request.getParameter("placefrom_project"+i);
				String placeto = (String) request.getParameter("placeto_project"+i);
				String noofdays = (String) request.getParameter("noofdays_project"+i);
				String kmpd = (String) request.getParameter("kmpd_project"+i);
				String ratepkm = (String) request.getParameter("ratepkm_project"+i);
				String strPurpose = (String) request.getParameter("strPurpose_project"+i);
				String isbillable = (String) request.getParameter("isbillable_project"+i);
				String strVendor = (String) request.getParameter("strVendor_project"+i);
				
				String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo_project"+i);
				StringBuilder sbReceiptNo = new StringBuilder();
				for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
					if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")) {
						sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
					}
				}
				
				int nTransportType = uF.parseToInt((String)request.getParameter("transportType_project"+i));
				int nTransportMode = 0;
				double dblTransportAmount = 0.0d;
				int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType_project"+i));
				double dblLodgingAmount = 0.0d;
				String strLocalConveyanceType = null;
				double dblLocalConveyanceKm = 0.0d;
				double dblLocalConveyanceRate = 0.0d;
				double dblLocalConveyanceAmount = 0.0d;
				double dblFoodBeverageAmount = 0.0d;
				double dblLaundryAmount = 0.0d;
				double dblSundryAmount = 0.0d;
				
				if(uF.parseToInt(strReimbursementId) == 0) {
					pst = con.prepareStatement("insert into emp_reimbursement_draft (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,submit_status," +
						"submited_by,submit_date,reimb_from_date,reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strProject);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "P");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, uF.parseToInt(strProject));
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setInt(36, 1);
					pst.setInt(37, uF.parseToInt(strSessionEmpId));
					pst.setDate(38, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(39, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(40, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(41, uF.parseToInt(reimbCurrency));
					pst.setInt(42, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strProject);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "P");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, uF.parseToInt(strProject));
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					String reimbursementId=null;
					pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
					rs=pst.executeQuery();
					while(rs.next()){
						reimbursementId=rs.getString("reimbursement_id");
					}
					rs.close();
					pst.close();
					insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
//					insertWorkflowData(con, uF, pst, rs, reimbursementId, strAmount, startDate, endDate, strPurpose, strType);
					
				} else {
					pst = con.prepareStatement("update emp_reimbursement_draft set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
						"reimbursement_amount=?, emp_id=?, entry_date=?, reimbursement_type1=?, travel_mode=?, no_person=?, travel_from=?, " +
						"travel_to=?, no_days=?, travel_distance=?, travel_rate=?, reimbursement_info=?, is_billable=?, client_id=?, pro_id=?, vendor=?, " +
						"receipt_no=?, transport_type=?, transport_mode=?, transport_amount=?, lodging_type=?, lodging_amount=?, local_conveyance_type=?, " +
						"local_conveyance_km=?, local_conveyance_rate=?, local_conveyance_amount=?, food_beverage_amount=?, laundry_amount=?, " +
						"sundry_amount=?, parent_id=?,submit_status=?,submited_by=?,submit_date=?,reimb_from_date=?,reimb_to_date=?,reimb_currency=?," +
						"reimb_payment_mode=? where reimbursement_id=?"); // ref_document=?, 
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strProject);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(8, sbFileName.toString());
					pst.setString(8, "P");
					pst.setString(9, modeoftravel);
					pst.setInt(10, uF.parseToInt(noofperson));
					pst.setString(11, placefrom);
					pst.setString(12, placeto);
					pst.setInt(13, uF.parseToInt(noofdays));
					pst.setDouble(14, uF.parseToDouble(kmpd));
					pst.setDouble(15, uF.parseToDouble(ratepkm));
					pst.setString(16, strType);
					pst.setBoolean(17, uF.parseToBoolean(isbillable));
					pst.setInt(18, uF.parseToInt(strClient));
					pst.setInt(19, uF.parseToInt(strProject));
					pst.setString(20, strVendor);
					pst.setString(21, sbReceiptNo.toString());
					pst.setInt(22, nTransportType);
					pst.setInt(23, nTransportMode);
					pst.setDouble(24, dblTransportAmount);
					pst.setInt(25, nLodgingType);
					pst.setDouble(26, dblLodgingAmount);
					pst.setString(27, strLocalConveyanceType);
					pst.setDouble(28, dblLocalConveyanceKm);
					pst.setDouble(29, dblLocalConveyanceRate);
					pst.setDouble(30, dblLocalConveyanceAmount);
					pst.setDouble(31, dblFoodBeverageAmount);
					pst.setDouble(32, dblLaundryAmount);
					pst.setDouble(33, dblSundryAmount);
					pst.setInt(34, intParentId);
					pst.setInt(35, 1);
					pst.setInt(36, uF.parseToInt(strSessionEmpId));
					pst.setDate(37, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(38, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(39, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(40, uF.parseToInt(reimbCurrency));
					pst.setInt(41, uF.parseToInt(reimbPaymentMode));
					pst.setInt(42, uF.parseToInt(strReimbursementId));
					pst.executeUpdate();
					
					String refDocument = "";
					pst = con.prepareStatement("select ref_document from emp_reimbursement_draft where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strReimbursementId));
					rs=pst.executeQuery();
					while(rs.next()) {
						refDocument = rs.getString("ref_document");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("update emp_reimbursement_draft set ref_document=? where reimbursement_id=?"); 
					pst.setString(1, refDocument+sbFileName.toString());
					pst.setInt(2, uF.parseToInt(strReimbursementId));
					pst.executeUpdate();
					
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strProject);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, refDocument+sbFileName.toString());
					pst.setString(9, "P");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, uF.parseToInt(strProject));
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					pst.executeUpdate();
					
					String reimbursementId=null;
					pst = con.prepareStatement("select max(reimbursement_id)as reimbursement_id from emp_reimbursement");
					rs=pst.executeQuery();
					while(rs.next()) {
						reimbursementId=rs.getString("reimbursement_id");
					}
					rs.close();
					pst.close();
					insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
//					insertWorkflowData(con, uF, pst, rs, reimbursementId, strAmount, startDate, endDate, strPurpose, strType);
				}
			}
			
			if(expStartDate != null && expEndDate != null) {
				insertWorkflowDataNotification(con, uF, pst, rs, strExpensesAmount+"", strExpenseCurr, expStartDate, expEndDate, "", "");
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

	private void insertWorkflowDataNotification(Connection con, UtilityFunctions uF, PreparedStatement pst, ResultSet rs, 
			String strAmount, String strCurrency, String startDate, String endDate, String strPurpose, String strType) {
		
		try {
			List<String> alManagers = null;
			if(uF.parseToBoolean(CF.getIsWorkFlow())){
				alManagers = insertBulkExpenseApprovalMemberNotification(con, pst, rs, uF, strAmount);
			}
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String strDomain = request.getServerName().split("\\.")[0];
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
//			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
			
			Notifications nF = new Notifications(N_MANAGER_REIMBURSEMENT_REQUEST, CF); 
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(strSessionEmpId);
			nF.setSupervisor(false);
			nF.setEmailTemplate(true);
			for(int ii=0; alManagers!=null && ii<alManagers.size();ii++) {
				pst = con.prepareStatement(selectEmpDetails1);
				pst.setInt(1, uF.parseToInt((String)alManagers.get(ii)));
				rs = pst.executeQuery();
				boolean flg = false;
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
					flg = true;
				}
				rs.close();
				pst.close();
				if(flg) {
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpReimbursementFrom(uF.getDateFormat(startDate, DATE_FORMAT, CF.getStrReportDateFormat()));
					nF.setStrEmpReimbursementTo(uF.getDateFormat(endDate, DATE_FORMAT, CF.getStrReportDateFormat()));
					nF.setStrEmpReimbursementPurpose(strPurpose);
					nF.setStrEmpReimbursementType(strType);
					nF.setStrEmpReimbursementAmount(strAmount);
					// nF.setStrAttachmentFileSource(filePath+fileName);
					// nF.setStrAttachmentFileName(fileName);
					nF.setStrEmpReimbursementDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()));
					nF.setStrEmpReimbursementCurrency(strCurrency);
					nF.sendNotifications();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/*private void insertWorkflowData(Connection con, UtilityFunctions uF, PreparedStatement pst, ResultSet rs, String reimbursementId, 
			String strAmount, String startDate, String endDate, String strPurpose, String strType) {
		try {
			List<String> alManagers = null;
			if(uF.parseToBoolean(CF.getIsWorkFlow())){
				alManagers = insertBulkExpenseApprovalMember(con, pst, rs, reimbursementId, uF, strAmount);
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
//			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
			
			String strCurrId = hmEmpCurrency.get(strSessionEmpId);
			Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
			if (hmCurrencyInner == null)hmCurrencyInner = new HashMap<String, String>();
			String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
			
			Notifications nF = new Notifications(N_MANAGER_REIMBURSEMENT_REQUEST, CF); 
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(strSessionEmpId);
			nF.setSupervisor(false);
			nF.setEmailTemplate(true);
			for(int ii=0; alManagers!=null && ii<alManagers.size();ii++) {
				pst = con.prepareStatement(selectEmpDetails1);
				pst.setInt(1, uF.parseToInt((String)alManagers.get(ii)));
				rs = pst.executeQuery();
				boolean flg=false;
				while(rs.next()) {
					nF.setStrSupervisorEmail(rs.getString("emp_email"));
					nF.setStrSupervisorName(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
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
				rs.close();
				pst.close();
				if(flg){
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpReimbursementFrom(uF.getDateFormat(startDate, DATE_FORMAT, CF.getStrReportDateFormat()));
					nF.setStrEmpReimbursementTo(uF.getDateFormat(endDate, DATE_FORMAT, CF.getStrReportDateFormat()));
					nF.setStrEmpReimbursementPurpose(strPurpose);
					nF.setStrEmpReimbursementType(strType);
					nF.setStrEmpReimbursementAmount(strAmount);
					// nF.setStrAttachmentFileSource(filePath+fileName);
					// nF.setStrAttachmentFileName(fileName);
					nF.setStrEmpReimbursementDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()));
					nF.setStrEmpReimbursementCurrency(strCurrSymbol);
					
					nF.sendNotifications();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


	private List<String> insertBulkExpenseApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, String reimbursement_id, UtilityFunctions uF, String strAmount) {
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
			while(rs.next()){
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
			while(it.hasNext()){
				String work_flow_member_id=it.next();
				List<String> innerList=hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
//				System.out.println("innerList.get(3)+memid====>"+innerList.get(3)+memid+"=====>"+request.getParameter(innerList.get(3)+memid));
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")){
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3){
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
//					System.out.println("approval empid====>"+empid);
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,uF.parseToInt(reimbursement_id));
					pst.setString(3,"Reimbursements");
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					
					
					/*String alertData = "<div style=\"float: left;\"> Received a new Reimbursement Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> amount "+uF.formatIntoTwoDecimal(uF.parseToDouble(strAmount))+". ["+hmUserType.get(userTypeId+"")+"] </div>";
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
					
					if(!alManagers.contains(empid)){
						alManagers.add(empid);
					}*/
				}
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alManagers;
	}

	
	private List<String> insertBulkExpenseApprovalMemberNotification(Connection con,PreparedStatement pst, ResultSet rs, UtilityFunctions uF, String strAmount) {
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
			while(rs.next()){
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
			while(it.hasNext()){
				String work_flow_member_id=it.next();
				List<String> innerList = hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")){
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
					String alertData = "<div style=\"float: left;\"> Received a new Reimbursement Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> amount "+uF.formatIntoTwoDecimal(uF.parseToDouble(strAmount))+". ["+hmUserType.get(userTypeId+"")+"] </div>";
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
					
					if(!alManagers.contains(empid)){
						alManagers.add(empid);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alManagers;
	}
	
	

	private void saveBulkExpense() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
//			System.out.println("getTravelCount() ===>>>> " + getTravelCount());
//			System.out.println("getLocalCount() ===>>>> " + getLocalCount());
//			System.out.println("getMobileCount() ===>>>> " + getMobileCount());
//			System.out.println("getProjectCount() ===>>>> " + getProjectCount());
			int intParentId = 0;
			pst = con.prepareStatement("select max(parent_id)as parent_id from emp_reimbursement_draft");
			rs=pst.executeQuery();
			while(rs.next()) {
				intParentId = rs.getInt("parent_id");
			}
			rs.close();
			pst.close();
			intParentId++;
			
			String parentId = request.getParameter("parentId");
			if(uF.parseToInt(parentId)>0) {
				intParentId = uF.parseToInt(parentId);
			}
			
			String[] strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			for(int i = 1 ; i <= uF.parseToInt(getTravelCount()); i++) {
				String strAmount = (String) request.getParameter("strAmount_travel"+i);
				String reimbCurrency = (String) request.getParameter("reimbCurrency_travel"+i);
				String reimbPaymentMode = (String) request.getParameter("reimbPaymentMode_travel"+i);
//				System.out.println("strAmount ===>> " + strAmount);
				if(uF.parseToDouble(strAmount) == 0) {
					continue;
				}
				
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//				System.out.println("request==>"+request.getClass());
//				System.out.println("mpRequest==>"+mpRequest.getClass());
				File[] files = mpRequest.getFiles("strDocument_travel"+i);    //  
				String[] fileNames = mpRequest.getFileNames("strDocument_travel"+i); 
				
				StringBuilder sbFileName = new StringBuilder(); 
				for (int ii = 0; files != null && ii < files.length; ii++) {
					if(CF.getStrDocSaveLocation()==null) {
						sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} else {
						sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					}
				}
//				String reimbursementType = (String) request.getParameter("reimbursementType");
				String startDate = strPayCycleDates[0];
				String endDate = strPayCycleDates[1];
				
				String strReimbursementId = (String) request.getParameter("travelReimbursementId"+i);
				String strClient = (String) request.getParameter("strClient_travel"+i);
				String strProject = (String) request.getParameter("strProject_travel"+i);
				String strTravelPlan = (String) request.getParameter("strTravelPlan_travel"+i);
				String strType = (String) request.getParameter("strType_travel"+i);
				String modeoftravel = (String) request.getParameter("modeoftravel_travel"+i);
				String fromDate = (String) request.getParameter("fromDate_travel"+i);
				String toDate = (String) request.getParameter("toDate_travel"+i);
				String noofperson = (String) request.getParameter("noofperson_travel"+i);
				String placefrom = (String) request.getParameter("placefrom_travel"+i);
				String placeto = (String) request.getParameter("placeto_travel"+i);
				String noofdays = (String) request.getParameter("noofdays_travel"+i);
				String kmpd = (String) request.getParameter("kmpd_travel"+i);
				String ratepkm = (String) request.getParameter("ratepkm_travel"+i);
				String strPurpose = (String) request.getParameter("strPurpose_travel"+i);
				String isbillable = (String) request.getParameter("isbillable_travel"+i);
				String strVendor = (String) request.getParameter("strVendor_travel"+i);
				
				String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo_travel"+i);
				StringBuilder sbReceiptNo = new StringBuilder();
				for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
					if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")){
						sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
					}
				}
				
				int nTransportType = uF.parseToInt((String)request.getParameter("transportType_travel"+i));
				int nTransportMode = 0;
				double dblTransportAmount = 0.0d;
				int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType_travel"+i));
				double dblLodgingAmount = 0.0d;
				String strLocalConveyanceType = null;
				double dblLocalConveyanceKm = 0.0d;
				double dblLocalConveyanceRate = 0.0d;
				double dblLocalConveyanceAmount = 0.0d;
				double dblFoodBeverageAmount = 0.0d;
				double dblLaundryAmount = 0.0d;
				double dblSundryAmount = 0.0d;

				if(nTransportType == 1) {
					nTransportMode = uF.parseToInt((String)request.getParameter("trainType_travel"+i));
					dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount_travel"+i));
				} else if(nTransportType == 2) {
					nTransportMode = uF.parseToInt((String)request.getParameter("busType_travel"+i));
					dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount_travel"+i));
				} else if(nTransportType == 3) {
					nTransportMode = uF.parseToInt((String)request.getParameter("flightType_travel"+i));
					dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount_travel"+i));
				} else if(nTransportType == 4) {
					nTransportMode = uF.parseToInt((String)request.getParameter("carType_travel"+i));
					dblTransportAmount = uF.parseToDouble((String)request.getParameter("strTransAmount_travel"+i));
				}
				
				dblLodgingAmount = uF.parseToDouble((String)request.getParameter("strLodgingAmount_travel"+i));
				
				strLocalConveyanceType = (String)request.getParameter("localConveyanceTranType_travel"+i);
				if(strLocalConveyanceType!=null && !strLocalConveyanceType.trim().equals("")) {
					dblLocalConveyanceKm = uF.parseToDouble((String)request.getParameter("localConveyanceKM_travel"+i));
					dblLocalConveyanceRate = uF.parseToDouble((String)request.getParameter("localConveyanceRate_travel"+i));
					dblLocalConveyanceAmount = uF.parseToDouble((String)request.getParameter("strLocalConveyanceAmount_travel"+i));
				}
				
				dblFoodBeverageAmount = uF.parseToDouble((String)request.getParameter("strFoodBeverageAmount_travel"+i));
				dblLaundryAmount = uF.parseToDouble((String)request.getParameter("strLaundryAmount_travel"+i));
				dblSundryAmount = uF.parseToDouble((String)request.getParameter("strSundryAmount_travel"+i));
				
				if(uF.parseToInt(strReimbursementId) == 0) {
					pst = con.prepareStatement("insert into emp_reimbursement_draft (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strTravelPlan);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "T");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					int x = pst.executeUpdate();
//					System.out.println("pst ====>>> " + pst);
				} else {
					pst = con.prepareStatement("update emp_reimbursement_draft set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
						"reimbursement_amount=?, emp_id=?, entry_date=?, reimbursement_type1=?, travel_mode=?, no_person=?, travel_from=?, " +
						"travel_to=?, no_days=?, travel_distance=?, travel_rate=?, reimbursement_info=?, is_billable=?, client_id=?, pro_id=?, vendor=?, " +
						"receipt_no=?, transport_type=?, transport_mode=?, transport_amount=?, lodging_type=?, lodging_amount=?, local_conveyance_type=?, " +
						"local_conveyance_km=?, local_conveyance_rate=?, local_conveyance_amount=?, food_beverage_amount=?, laundry_amount=?, " +
						"sundry_amount=?, parent_id=?,reimb_from_date=?,reimb_to_date=?,reimb_currency=?,reimb_payment_mode=? where reimbursement_id=?"); // ref_document=?, 
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strTravelPlan);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(8, sbFileName.toString());
					pst.setString(8, "T");
					pst.setString(9, modeoftravel);
					pst.setInt(10, uF.parseToInt(noofperson));
					pst.setString(11, placefrom);
					pst.setString(12, placeto);
					pst.setInt(13, uF.parseToInt(noofdays));
					pst.setDouble(14, uF.parseToDouble(kmpd));
					pst.setDouble(15, uF.parseToDouble(ratepkm));
					pst.setString(16, strType);
					pst.setBoolean(17, uF.parseToBoolean(isbillable));
					pst.setInt(18, uF.parseToInt(strClient));
					pst.setInt(19, 0);
					pst.setString(20, strVendor);
					pst.setString(21, sbReceiptNo.toString());
					pst.setInt(22, nTransportType);
					pst.setInt(23, nTransportMode);
					pst.setDouble(24, dblTransportAmount);
					pst.setInt(25, nLodgingType);
					pst.setDouble(26, dblLodgingAmount);
					pst.setString(27, strLocalConveyanceType);
					pst.setDouble(28, dblLocalConveyanceKm);
					pst.setDouble(29, dblLocalConveyanceRate);
					pst.setDouble(30, dblLocalConveyanceAmount);
					pst.setDouble(31, dblFoodBeverageAmount);
					pst.setDouble(32, dblLaundryAmount);
					pst.setDouble(33, dblSundryAmount);
					pst.setInt(34, intParentId);
					pst.setDate(35, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(36, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(37, uF.parseToInt(reimbCurrency));
					pst.setInt(38, uF.parseToInt(reimbPaymentMode));
					pst.setInt(39, uF.parseToInt(strReimbursementId));
					int x = pst.executeUpdate();
					
					String refDocument = "";
					pst = con.prepareStatement("select ref_document from emp_reimbursement_draft where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strReimbursementId));
					rs=pst.executeQuery();
					while(rs.next()) {
						refDocument = rs.getString("ref_document");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("update emp_reimbursement_draft set ref_document=? where reimbursement_id=?"); 
					pst.setString(1, refDocument+sbFileName.toString());
					pst.setInt(2, uF.parseToInt(strReimbursementId));
					x = pst.executeUpdate();
				}
			}
			
//			System.out.println("getLocalCount ===>> " + getLocalCount());
			for(int i = 1 ; i <= uF.parseToInt(getLocalCount()); i++) {
				String strAmount = (String) request.getParameter("strAmount_local"+i);
				String reimbCurrency = (String) request.getParameter("reimbCurrency_local"+i);
				String reimbPaymentMode = (String) request.getParameter("reimbPaymentMode_local"+i);
//				System.out.println("strAmount ===>> " + strAmount);
				if(uF.parseToDouble(strAmount) == 0) {
					continue;
				}
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//				System.out.println("request==>"+request.getClass());
//				System.out.println("mpRequest==>"+mpRequest.getClass());
				File[] files = mpRequest.getFiles("strDocument_local"+i);    //  
				String[] fileNames = mpRequest.getFileNames("strDocument_local"+i); 
				
				StringBuilder sbFileName = new StringBuilder(); 
				for (int ii = 0; files != null && ii < files.length; ii++) {
					if(CF.getStrDocSaveLocation()==null) {
						sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} else {
						sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} 
				}  
				String reimbursementType = (String) request.getParameter("reimbursementType");
				String startDate = null;
				String endDate = null;
				if(reimbursementType!=null && reimbursementType.equals("P")) {
					String paycycle = (String) request.getParameter("paycycle");
					String[] arr = paycycle.split("-");
					startDate = arr[0];
					endDate = arr[1];
				} else {
					startDate = strPayCycleDates[0];
					endDate = strPayCycleDates[1];
				}
				
				String strReimbursementId = (String) request.getParameter("localReimbursementId"+i);
				String strClient = (String) request.getParameter("strClient_local"+i);
				String strProject = (String) request.getParameter("strProject_local"+i);
				String strTravelPlan = (String) request.getParameter("strTravelPlan_local"+i);
				String strType = (String) request.getParameter("strType_local"+i);
				String modeoftravel = (String) request.getParameter("modeoftravel_local"+i);
				String fromDate = (String) request.getParameter("fromDate_local"+i);
				String toDate = (String) request.getParameter("toDate_local"+i);
				String noofperson = (String) request.getParameter("noofperson_local"+i);
				String placefrom = (String) request.getParameter("placefrom_local"+i);
				String placeto = (String) request.getParameter("placeto_local"+i);
				String noofdays = (String) request.getParameter("noofdays_local"+i);
				String kmpd = (String) request.getParameter("kmpd_local"+i);
				String ratepkm = (String) request.getParameter("ratepkm_local"+i);
				String strPurpose = (String) request.getParameter("strPurpose_local"+i);
				String isbillable = (String) request.getParameter("isbillable_local"+i);
				String strVendor = (String) request.getParameter("strVendor_local"+i);
				
				String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo_local"+i);
				StringBuilder sbReceiptNo = new StringBuilder();
				for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
					if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")){
						sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
					}
				}
				
				int nTransportType = uF.parseToInt((String)request.getParameter("transportType_local"+i));
				int nTransportMode = 0;
				double dblTransportAmount = 0.0d;
				int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType_local"+i));
				double dblLodgingAmount = 0.0d;
				String strLocalConveyanceType = null;
				double dblLocalConveyanceKm = 0.0d;
				double dblLocalConveyanceRate = 0.0d;
				double dblLocalConveyanceAmount = 0.0d;
				double dblFoodBeverageAmount = 0.0d;
				double dblLaundryAmount = 0.0d;
				double dblSundryAmount = 0.0d;
				
				if(uF.parseToInt(strReimbursementId) == 0) {
					pst = con.prepareStatement("insert into emp_reimbursement_draft (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strType);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "L");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					int x = pst.executeUpdate();
				} else {
					pst = con.prepareStatement("update emp_reimbursement_draft set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
						"reimbursement_amount=?, emp_id=?, entry_date=?, reimbursement_type1=?, travel_mode=?, no_person=?, travel_from=?, " +
						"travel_to=?, no_days=?, travel_distance=?, travel_rate=?, reimbursement_info=?, is_billable=?, client_id=?, pro_id=?, vendor=?, " +
						"receipt_no=?, transport_type=?, transport_mode=?, transport_amount=?, lodging_type=?, lodging_amount=?, local_conveyance_type=?, " +
						"local_conveyance_km=?, local_conveyance_rate=?, local_conveyance_amount=?, food_beverage_amount=?, laundry_amount=?, " +
						"sundry_amount=?, parent_id=?,reimb_from_date=?,reimb_to_date=?,reimb_currency=?,reimb_payment_mode=? where reimbursement_id=?"); //ref_document=?, 
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strType);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(8, sbFileName.toString());
					pst.setString(8, "L");
					pst.setString(9, modeoftravel);
					pst.setInt(10, uF.parseToInt(noofperson));
					pst.setString(11, placefrom);
					pst.setString(12, placeto);
					pst.setInt(13, uF.parseToInt(noofdays));
					pst.setDouble(14, uF.parseToDouble(kmpd));
					pst.setDouble(15, uF.parseToDouble(ratepkm));
					pst.setString(16, strType);
					pst.setBoolean(17, uF.parseToBoolean(isbillable));
					pst.setInt(18, uF.parseToInt(strClient));
					pst.setInt(19, 0);
					pst.setString(20, strVendor);
					pst.setString(21, sbReceiptNo.toString());
					pst.setInt(22, nTransportType);
					pst.setInt(23, nTransportMode);
					pst.setDouble(24, dblTransportAmount);
					pst.setInt(25, nLodgingType);
					pst.setDouble(26, dblLodgingAmount);
					pst.setString(27, strLocalConveyanceType);
					pst.setDouble(28, dblLocalConveyanceKm);
					pst.setDouble(29, dblLocalConveyanceRate);
					pst.setDouble(30, dblLocalConveyanceAmount);
					pst.setDouble(31, dblFoodBeverageAmount);
					pst.setDouble(32, dblLaundryAmount);
					pst.setDouble(33, dblSundryAmount);
					pst.setInt(34, intParentId);
					pst.setDate(35, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(36, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(37, uF.parseToInt(reimbCurrency));
					pst.setInt(38, uF.parseToInt(reimbPaymentMode));
					pst.setInt(39, uF.parseToInt(strReimbursementId));
					int x = pst.executeUpdate();
					
					String refDocument = "";
					pst = con.prepareStatement("select ref_document from emp_reimbursement_draft where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strReimbursementId));
					rs=pst.executeQuery();
					while(rs.next()) {
						refDocument = rs.getString("ref_document");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("update emp_reimbursement_draft set ref_document=? where reimbursement_id=?"); 
					pst.setString(1, refDocument+sbFileName.toString());
					pst.setInt(2, uF.parseToInt(strReimbursementId));
					x = pst.executeUpdate();
				}
			}
			
			
//			System.out.println("getMobileCount ===>> " + getMobileCount());
			for(int i = 1 ; i <= uF.parseToInt(getMobileCount()); i++) {
				String strAmount = (String) request.getParameter("strAmount_mobile"+i);
				String reimbCurrency = (String) request.getParameter("reimbCurrency_mobile"+i);
				String reimbPaymentMode = (String) request.getParameter("reimbPaymentMode_mobile"+i);
//				System.out.println("strAmount ===>> " + strAmount);
				if(uF.parseToDouble(strAmount) == 0) {
					continue;
				}
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//				System.out.println("request==>"+request.getClass());
//				System.out.println("mpRequest==>"+mpRequest.getClass());
				File[] files = mpRequest.getFiles("strDocument_mobile"+i);    //  
				String[] fileNames = mpRequest.getFileNames("strDocument_mobile"+i); 
				
				StringBuilder sbFileName = new StringBuilder(); 
				for (int ii = 0; files != null && ii < files.length; ii++) {
					if(CF.getStrDocSaveLocation()==null) {
						sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} else {
						sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} 
				}  
				String reimbursementType = (String) request.getParameter("reimbursementType");
				String startDate = null;
				String endDate = null;
				if(reimbursementType!=null && reimbursementType.equals("P")) {
					String paycycle = (String) request.getParameter("paycycle");
					String[] arr = paycycle.split("-");
					startDate = arr[0];
					endDate = arr[1];
				} else {
					startDate = strPayCycleDates[0];
					endDate = strPayCycleDates[1];
				}
				
				String strReimbursementId = (String) request.getParameter("mobileReimbursementId"+i);
				String strClient = (String) request.getParameter("strClient_mobile"+i);
				String strProject = (String) request.getParameter("strProject_mobile"+i);
				String strTravelPlan = (String) request.getParameter("strTravelPlan_mobile"+i);
				String strType = (String) request.getParameter("strType_mobile"+i);
				String modeoftravel = (String) request.getParameter("modeoftravel_mobile"+i);
				String fromDate = (String) request.getParameter("fromDate_mobile"+i);
				String toDate = (String) request.getParameter("toDate_mobile"+i);
				String noofperson = (String) request.getParameter("noofperson_mobile"+i);
				String placefrom = (String) request.getParameter("placefrom_mobile"+i);
				String placeto = (String) request.getParameter("placeto_mobile"+i);
				String noofdays = (String) request.getParameter("noofdays_mobile"+i);
				String kmpd = (String) request.getParameter("kmpd_mobile"+i);
				String ratepkm = (String) request.getParameter("ratepkm_mobile"+i);
				String strPurpose = (String) request.getParameter("strPurpose_mobile"+i);
				String isbillable = (String) request.getParameter("isbillable_mobile"+i);
				String strVendor = (String) request.getParameter("strVendor_mobile"+i);
				
				String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo_mobile"+i);
				StringBuilder sbReceiptNo = new StringBuilder();
				for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
					if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")){
						sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
					}
				}
				
				int nTransportType = uF.parseToInt((String)request.getParameter("transportType_mobile"+i));
				int nTransportMode = 0;
				double dblTransportAmount = 0.0d;
				int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType_mobile"+i));
				double dblLodgingAmount = 0.0d;
				String strLocalConveyanceType = null;
				double dblLocalConveyanceKm = 0.0d;
				double dblLocalConveyanceRate = 0.0d;
				double dblLocalConveyanceAmount = 0.0d;
				double dblFoodBeverageAmount = 0.0d;
				double dblLaundryAmount = 0.0d;
				double dblSundryAmount = 0.0d;
				
				if(uF.parseToInt(strReimbursementId) == 0) {
					pst = con.prepareStatement("insert into emp_reimbursement_draft (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, "Mobile Bill");
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "M");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, "Mobile Bill");
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, 0);
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					int x = pst.executeUpdate();
				} else {
					pst = con.prepareStatement("update emp_reimbursement_draft set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
						"reimbursement_amount=?, emp_id=?, entry_date=?, reimbursement_type1=?, travel_mode=?, no_person=?, travel_from=?, " +
						"travel_to=?, no_days=?, travel_distance=?, travel_rate=?, reimbursement_info=?, is_billable=?, client_id=?, pro_id=?, vendor=?, " +
						"receipt_no=?, transport_type=?, transport_mode=?, transport_amount=?, lodging_type=?, lodging_amount=?, local_conveyance_type=?, " +
						"local_conveyance_km=?, local_conveyance_rate=?, local_conveyance_amount=?, food_beverage_amount=?, laundry_amount=?, " +
						"sundry_amount=?, parent_id=?,reimb_from_date=?,reimb_to_date=?,reimb_currency=?,reimb_payment_mode=? where reimbursement_id=?"); //ref_document=?, 
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, "Mobile Bill");
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(8, sbFileName.toString());
					pst.setString(8, "M");
					pst.setString(9, modeoftravel);
					pst.setInt(10, uF.parseToInt(noofperson));
					pst.setString(11, placefrom);
					pst.setString(12, placeto);
					pst.setInt(13, uF.parseToInt(noofdays));
					pst.setDouble(14, uF.parseToDouble(kmpd));
					pst.setDouble(15, uF.parseToDouble(ratepkm));
					pst.setString(16, "Mobile Bill");
					pst.setBoolean(17, uF.parseToBoolean(isbillable));
					pst.setInt(18, uF.parseToInt(strClient));
					pst.setInt(19, 0);
					pst.setString(20, strVendor);
					pst.setString(21, sbReceiptNo.toString());
					pst.setInt(22, nTransportType);
					pst.setInt(23, nTransportMode);
					pst.setDouble(24, dblTransportAmount);
					pst.setInt(25, nLodgingType);
					pst.setDouble(26, dblLodgingAmount);
					pst.setString(27, strLocalConveyanceType);
					pst.setDouble(28, dblLocalConveyanceKm);
					pst.setDouble(29, dblLocalConveyanceRate);
					pst.setDouble(30, dblLocalConveyanceAmount);
					pst.setDouble(31, dblFoodBeverageAmount);
					pst.setDouble(32, dblLaundryAmount);
					pst.setDouble(33, dblSundryAmount);
					pst.setInt(34, intParentId);
					pst.setDate(35, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(36, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(37, uF.parseToInt(reimbCurrency));
					pst.setInt(38, uF.parseToInt(reimbPaymentMode));
					pst.setInt(39, uF.parseToInt(strReimbursementId));
					int x = pst.executeUpdate();
					
					String refDocument = "";
					pst = con.prepareStatement("select ref_document from emp_reimbursement_draft where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strReimbursementId));
					rs=pst.executeQuery();
					while(rs.next()) {
						refDocument = rs.getString("ref_document");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("update emp_reimbursement_draft set ref_document=? where reimbursement_id=?"); 
					pst.setString(1, refDocument+sbFileName.toString());
					pst.setInt(2, uF.parseToInt(strReimbursementId));
					x = pst.executeUpdate();
				}
			}
			
			
//			System.out.println("getProjectCount ===>> " + getProjectCount());
			for(int i = 1 ; i <= uF.parseToInt(getProjectCount()); i++) {
				String strAmount = (String) request.getParameter("strAmount_project"+i);
				String reimbCurrency = (String) request.getParameter("reimbCurrency_project"+i);
				String reimbPaymentMode = (String) request.getParameter("reimbPaymentMode_project"+i);
				if(uF.parseToDouble(strAmount) == 0) {
					continue;
				}
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//				System.out.println("request==>"+request.getClass());
//				System.out.println("mpRequest==>"+mpRequest.getClass());
				File[] files = mpRequest.getFiles("strDocument_project"+i);    //  
				String[] fileNames = mpRequest.getFileNames("strDocument_project"+i); 
				
				StringBuilder sbFileName = new StringBuilder(); 
				for (int ii = 0; files != null && ii < files.length; ii++) {
					if(CF.getStrDocSaveLocation()==null) {
						sbFileName.append(uF.uploadImageDocuments(request, DOCUMENT_LOCATION, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} else {
						sbFileName.append(uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strSessionEmpId, files[ii], fileNames[ii], fileNames[ii], CF) + ":_:");
					} 
				}  
				String reimbursementType = (String) request.getParameter("reimbursementType");
				String startDate = null;
				String endDate = null;
				if(reimbursementType!=null && reimbursementType.equals("P")) {
					String paycycle = (String) request.getParameter("paycycle");
					String[] arr = paycycle.split("-");
					startDate = arr[0];
					endDate = arr[1];
				} else {
					startDate = strPayCycleDates[0];
					endDate = strPayCycleDates[1];
				}
				
				String strReimbursementId = (String) request.getParameter("projectReimbursementId"+i);
				String strClient = (String) request.getParameter("strClient_project"+i);
				String strProject = (String) request.getParameter("strProject_project"+i);
				String strTravelPlan = (String) request.getParameter("strTravelPlan_project"+i);
				String strType = (String) request.getParameter("strType_project"+i);
				String modeoftravel = (String) request.getParameter("modeoftravel_project"+i);
				String fromDate = (String) request.getParameter("fromDate_project"+i);
				String toDate = (String) request.getParameter("toDate_project"+i);
				String noofperson = (String) request.getParameter("noofperson_project"+i);
				String placefrom = (String) request.getParameter("placefrom_project"+i);
				String placeto = (String) request.getParameter("placeto_project"+i);
				String noofdays = (String) request.getParameter("noofdays_project"+i);
				String kmpd = (String) request.getParameter("kmpd_project"+i);
				String ratepkm = (String) request.getParameter("ratepkm_project"+i);
				String strPurpose = (String) request.getParameter("strPurpose_project"+i);
				String isbillable = (String) request.getParameter("isbillable_project"+i);
				String strVendor = (String) request.getParameter("strVendor_project"+i);
				
				String[] strReceiptNo = (String[]) request.getParameterValues("strReceiptNo_project"+i);
				StringBuilder sbReceiptNo = new StringBuilder();
				for (int x = 0; strReceiptNo != null && x < strReceiptNo.length; x++) {
					if(strReceiptNo[x]!=null && !strReceiptNo[x].trim().equals("") && !strReceiptNo[x].trim().equalsIgnoreCase("NULL")) {
						sbReceiptNo.append(strReceiptNo[x].trim()+ ":_:");
					}
				}
				
				int nTransportType = uF.parseToInt((String)request.getParameter("transportType_project"+i));
				int nTransportMode = 0;
				double dblTransportAmount = 0.0d;
				int nLodgingType = uF.parseToInt((String)request.getParameter("lodgingType_project"+i));
				double dblLodgingAmount = 0.0d;
				String strLocalConveyanceType = null;
				double dblLocalConveyanceKm = 0.0d;
				double dblLocalConveyanceRate = 0.0d;
				double dblLocalConveyanceAmount = 0.0d;
				double dblFoodBeverageAmount = 0.0d;
				double dblLaundryAmount = 0.0d;
				double dblSundryAmount = 0.0d;
				
				if(uF.parseToInt(strReimbursementId) == 0) {
					pst = con.prepareStatement("insert into emp_reimbursement_draft (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, ref_document, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no," +
						"transport_type,transport_mode,transport_amount,lodging_type,lodging_amount,local_conveyance_type,local_conveyance_km," +
						"local_conveyance_rate,local_conveyance_amount,food_beverage_amount,laundry_amount,sundry_amount,parent_id,reimb_from_date," +
						"reimb_to_date,reimb_currency,reimb_payment_mode) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strProject);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, sbFileName.toString());
					pst.setString(9, "P");
					pst.setString(10, modeoftravel);
					pst.setInt(11, uF.parseToInt(noofperson));
					pst.setString(12, placefrom);
					pst.setString(13, placeto);
					pst.setInt(14, uF.parseToInt(noofdays));
					pst.setDouble(15, uF.parseToDouble(kmpd));
					pst.setDouble(16, uF.parseToDouble(ratepkm));
					pst.setString(17, strType);
					pst.setBoolean(18, uF.parseToBoolean(isbillable));
					pst.setInt(19, uF.parseToInt(strClient));
					pst.setInt(20, uF.parseToInt(strProject));
					pst.setString(21, strVendor);
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
					pst.setInt(35, intParentId);
					pst.setDate(36, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(37, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(38, uF.parseToInt(reimbCurrency));
					pst.setInt(39, uF.parseToInt(reimbPaymentMode));
					int x = pst.executeUpdate();
				} else {
					pst = con.prepareStatement("update emp_reimbursement_draft set from_date=?, to_date=?, reimbursement_type=?, reimbursement_purpose=?, " +
						"reimbursement_amount=?, emp_id=?, entry_date=?, reimbursement_type1=?, travel_mode=?, no_person=?, travel_from=?, " +
						"travel_to=?, no_days=?, travel_distance=?, travel_rate=?, reimbursement_info=?, is_billable=?, client_id=?, pro_id=?, vendor=?, " +
						"receipt_no=?, transport_type=?, transport_mode=?, transport_amount=?, lodging_type=?, lodging_amount=?, local_conveyance_type=?, " +
						"local_conveyance_km=?, local_conveyance_rate=?, local_conveyance_amount=?, food_beverage_amount=?, laundry_amount=?, " +
						"sundry_amount=?, parent_id=?,reimb_from_date=?,reimb_to_date=?,reimb_currency=?,reimb_payment_mode=? where reimbursement_id=?"); //ref_document=?, 
					pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setString(3, strProject);
					pst.setString(4, strPurpose);
					pst.setDouble(5, uF.parseToDouble(strAmount));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(8, sbFileName.toString());
					pst.setString(8, "P");
					pst.setString(9, modeoftravel);
					pst.setInt(10, uF.parseToInt(noofperson));
					pst.setString(11, placefrom);
					pst.setString(12, placeto);
					pst.setInt(13, uF.parseToInt(noofdays));
					pst.setDouble(14, uF.parseToDouble(kmpd));
					pst.setDouble(15, uF.parseToDouble(ratepkm));
					pst.setString(16, strType);
					pst.setBoolean(17, uF.parseToBoolean(isbillable));
					pst.setInt(18, uF.parseToInt(strClient));
					pst.setInt(19, uF.parseToInt(strProject));
					pst.setString(20, strVendor);
					pst.setString(21, sbReceiptNo.toString());
					pst.setInt(22, nTransportType);
					pst.setInt(23, nTransportMode);
					pst.setDouble(24, dblTransportAmount);
					pst.setInt(25, nLodgingType);
					pst.setDouble(26, dblLodgingAmount);
					pst.setString(27, strLocalConveyanceType);
					pst.setDouble(28, dblLocalConveyanceKm);
					pst.setDouble(29, dblLocalConveyanceRate);
					pst.setDouble(30, dblLocalConveyanceAmount);
					pst.setDouble(31, dblFoodBeverageAmount);
					pst.setDouble(32, dblLaundryAmount);
					pst.setDouble(33, dblSundryAmount);
					pst.setInt(34, intParentId);
					pst.setDate(35, uF.getDateFormat(fromDate, DATE_FORMAT));
					pst.setDate(36, uF.getDateFormat(toDate, DATE_FORMAT));
					pst.setInt(37, uF.parseToInt(reimbCurrency));
					pst.setInt(38, uF.parseToInt(reimbPaymentMode));
					pst.setInt(39, uF.parseToInt(strReimbursementId));
					int x = pst.executeUpdate();
					
					String refDocument = "";
					pst = con.prepareStatement("select ref_document from emp_reimbursement_draft where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strReimbursementId));
					rs=pst.executeQuery();
					while(rs.next()) {
						refDocument = rs.getString("ref_document");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("update emp_reimbursement_draft set ref_document=? where reimbursement_id=?"); 
					pst.setString(1, refDocument+sbFileName.toString());
					pst.setInt(2, uF.parseToInt(strReimbursementId));
					x = pst.executeUpdate();
				}
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

	
	private void getReimbursementsPolicyMember(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		String policy_id=null;
		try {
			
			int strEmpID=0;
//			if (strUserType != null && strUserType.equals(HRMANAGER)) {
//				strEmpID=uF.parseToInt(getStrSelectedEmpId1());
//			} else {
				strEmpID=uF.parseToInt(strSessionEmpId);
//			}
			
			con = db.makeConnection(con);
			
			strOrgCurrId = CF.getOrgCurrencyIdByOrg(con, (String) session.getAttribute(ORGID));
			
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
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
				boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
				
				
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
	
	
	
	private void loadData(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		
		travelPlanList = new FillTravel(request).fillTravelPlan(uF.parseToInt((String) session.getAttribute(EMPID)));
		if(strUserType != null && strUserType.equals(EMPLOYEE)) {
			projectList = new FillProjectList(request).fillProjectDetailsByEmp(uF.parseToInt((String) session.getAttribute(EMPID)), false, 0);
		} else {
			projectList = new FillProjectList(request).fillProjectDetailsByEmp(0, false, 0);
		}
		
		clientList = new FillClients(request).fillClients(uF.parseToInt((String) session.getAttribute(EMPID)));
		currencyList= new FillCurrency(request).fillCurrency();
		paymentModeList = new FillPayMode().fillPaymentModeExpenses();
		
		typeList = new FillRimbursementType().fillRimbursementType();
		modeoftravelList = new FillRimbursementType().fillmodeoftravel();
		
		lodgingTypeList = new FillLodgingType().fillLodgingType();
		localConveyanceTranTypeList = new FillRimbursementType().fillmodeoftravel();
		
		StringBuilder sbPaycycleList = new StringBuilder();
		for(int i = 0; paycycleList!=null && i < paycycleList.size(); i++){
			FillPayCycles payCycles = paycycleList.get(i);
			sbPaycycleList.append("<option value=\""+payCycles.getPaycycleId()+"\">"+payCycles.getPaycycleName()+"</option>");
		}
		request.setAttribute("sbPaycycleList",sbPaycycleList.toString());
		
		StringBuilder sbProjectList = new StringBuilder();
		for(int i = 0; projectList!=null && i < projectList.size(); i++){
			FillProjectList fillProjectList = projectList.get(i);
			sbProjectList.append("<option value=\""+fillProjectList.getProjectID()+"\">"+fillProjectList.getProjectName()+"</option>");
		}
		request.setAttribute("sbProjectList",sbProjectList.toString());
		
		StringBuilder sbClientList = new StringBuilder();
		for(int i = 0; clientList!=null && i < clientList.size(); i++){
			FillClients fillClients = clientList.get(i);
			sbClientList.append("<option value=\""+fillClients.getClientId()+"\">"+fillClients.getClientName()+"</option>");
		}
		request.setAttribute("sbClientList",sbClientList.toString());
		
		StringBuilder sbTravelPlanList = new StringBuilder();
		for(int i = 0; travelPlanList!=null && i < travelPlanList.size(); i++){
			FillTravel fillTravel = travelPlanList.get(i);
			sbTravelPlanList.append("<option value=\""+fillTravel.getLeaveId()+"\">"+fillTravel.getPlanName()+"</option>");
		}
		request.setAttribute("sbTravelPlanList",sbTravelPlanList.toString());
		
		StringBuilder sbTypeList = new StringBuilder();
		for(int i = 0; typeList!=null && i < typeList.size(); i++) {
			FillRimbursementType fillRimbursementType = typeList.get(i);
			sbTypeList.append("<option value=\""+fillRimbursementType.getTypeId()+"\">"+fillRimbursementType.getTypeName()+"</option>");
		}
		request.setAttribute("sbTypeList",sbTypeList.toString());
		
		StringBuilder sbModeoftravelList = new StringBuilder();
		for(int i = 0; modeoftravelList!=null && i < modeoftravelList.size(); i++) {
			FillRimbursementType fillRimbursementType = modeoftravelList.get(i);
			sbModeoftravelList.append("<option value=\""+fillRimbursementType.getTypeId()+"\">"+fillRimbursementType.getTypeName()+"</option>");
		}
		request.setAttribute("sbModeoftravelList", sbModeoftravelList.toString());
		
		StringBuilder sbLodgingTypeList = new StringBuilder();
		for(int i = 0; lodgingTypeList!=null && i < lodgingTypeList.size(); i++){
			FillLodgingType fillLodgingType = lodgingTypeList.get(i);
			sbLodgingTypeList.append("<option value=\""+fillLodgingType.getLodgingTypeId()+"\">"+fillLodgingType.getLodgingTypeName()+"</option>");
		}
		request.setAttribute("sbLodgingTypeList",sbLodgingTypeList.toString());
		
		StringBuilder sbLocalConveyanceTranTypeList = new StringBuilder();
		for(int i = 0; localConveyanceTranTypeList!=null && i < localConveyanceTranTypeList.size(); i++){
			FillRimbursementType fillRimbursementType = localConveyanceTranTypeList.get(i);
			sbLocalConveyanceTranTypeList.append("<option value=\""+fillRimbursementType.getTypeId()+"\">"+fillRimbursementType.getTypeName()+"</option>");
		}
		request.setAttribute("sbLocalConveyanceTranTypeList",sbLocalConveyanceTranTypeList.toString());
		
		StringBuilder sbReimbCurrencyList = new StringBuilder();
		
		for(int i = 0; currencyList!=null && i < currencyList.size(); i++) {
			sbReimbCurrencyList.append("<option value=\""+currencyList.get(i).getCurrencyId()+"\">"+currencyList.get(i).getCurrencyName()+"</option>");
		}
		request.setAttribute("sbReimbCurrencyList",sbReimbCurrencyList.toString());
		
		StringBuilder sbPaymentModeList = new StringBuilder();
//		sbPaymentModeList.append("<option value=\"\">Select Payment Mode</option>");
		for(int i = 0; paymentModeList!=null && i < paymentModeList.size(); i++) {
			sbPaymentModeList.append("<option value=\""+paymentModeList.get(i).getPayModeId()+"\">"+paymentModeList.get(i).getPayModeName()+"</option>");
		}
		request.setAttribute("sbPaymentModeList",sbPaymentModeList.toString());
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getProjectCount() {
		return projectCount;
	}

	public void setProjectCount(String projectCount) {
		this.projectCount = projectCount;
	}

	public String getTravelCount() {
		return travelCount;
	}

	public void setTravelCount(String travelCount) {
		this.travelCount = travelCount;
	}

	public String getLocalCount() {
		return localCount;
	}

	public void setLocalCount(String localCount) {
		this.localCount = localCount;
	}

	public String getMobileCount() {
		return mobileCount;
	}

	public void setMobileCount(String mobileCount) {
		this.mobileCount = mobileCount;
	}

	public List<FillRimbursementType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<FillRimbursementType> typeList) {
		this.typeList = typeList;
	}

	public List<FillRimbursementType> getModeoftravelList() {
		return modeoftravelList;
	}

	public void setModeoftravelList(List<FillRimbursementType> modeoftravelList) {
		this.modeoftravelList = modeoftravelList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
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

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getBtnSave() {
		return btnSave;
	}

	public void setBtnSave(String btnSave) {
		this.btnSave = btnSave;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public String getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(String expenseId) {
		this.expenseId = expenseId;
	}

	public String getBtnRemove() {
		return btnRemove;
	}

	public void setBtnRemove(String btnRemove) {
		this.btnRemove = btnRemove;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
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

	public String getStrOrgCurrId() {
		return strOrgCurrId;
	}

	public void setStrOrgCurrId(String strOrgCurrId) {
		this.strOrgCurrId = strOrgCurrId;
	}
	
}
