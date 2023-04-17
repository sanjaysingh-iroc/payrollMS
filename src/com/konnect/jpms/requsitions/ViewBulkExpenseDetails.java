package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewBulkExpenseDetails extends ActionSupport implements IStatements, ServletRequestAware {


	/**
	 *  
	 */
	private static final long serialVersionUID = 1267880549832602033L;
	HttpSession session;
	private CommonFunctions CF;
	
	String strEmpId;
	String parentId;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		viewBulkExpenseDetails(uF);
		
		return LOAD;		
	}

	private void viewBulkExpenseDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String orgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(hmEmpName == null) hmEmpName = new HashMap<String, String>();
			
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
			
			Map<String, String> hmTravelTransportType = CF.getTravelTransportType(uF);
			Map<String, String> hmTrainType = CF.getTrainType(uF);
			Map<String, String> hmBusType = CF.getBusType(uF);
			Map<String, String> hmFlightType = CF.getFlightType(uF);
			Map<String, String> hmCarType = CF.getCarType(uF);
			Map<String, String> hmLimitType = CF.getLimitType(uF);
			Map<String, String> hmLodgingType = CF.getLodgingType(uF);
			
			Map<String, String> hmProjectMap = CF.getProjectNameMap(con);
			if(hmProjectMap == null) hmProjectMap = new HashMap<String, String>();
			Map<String, String> hmProjectClientMap = CF.getProjectClientMap(con, uF);
			if(hmProjectClientMap == null) hmProjectClientMap = new HashMap<String, String>();
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			
			Map<String, List<Map<String, String>>> hmBulkExpenseData = new HashMap<String, List<Map<String, String>>>();
			List<Map<String, String>> alBulkExpenseData = new ArrayList<Map<String, String>>();
			pst = con.prepareStatement("select * from emp_reimbursement where emp_id = ? and parent_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getParentId()));
//			System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			while(rs.next()) {
//				nEmpId = rs.getInt("emp_id");
				alBulkExpenseData = hmBulkExpenseData.get(rs.getString("reimbursement_type1"));
				if(alBulkExpenseData == null) alBulkExpenseData = new ArrayList<Map<String, String>>();
			
				Map<String, String> hmReimbursement = new HashMap<String, String>();
				hmReimbursement.put("EMP_NAME", uF.showData(hmEmpName.get(rs.getString("emp_id")), "") +" ["+uF.showData(hmEmpCode.get(rs.getString("emp_id")), "")+"]");
				hmReimbursement.put("FROM_DATE", uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT));
				hmReimbursement.put("TO_DATE", uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT));
				hmReimbursement.put("REIMB_FROM_DATE",uF.getDateFormat(rs.getString("reimb_from_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmReimbursement.put("REIMB_TO_DATE", uF.getDateFormat(rs.getString("reimb_to_date"), DBDATE, CF.getStrReportDateFormat()));
				String []arr = CF.getPayCycleFromDate(con, uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), CF.getStrTimeZone(), CF, orgId);
				hmReimbursement.put("PAYCYCLE", "Paycycle "+arr[2]+", "+uF.getDateFormat(arr[0], DATE_FORMAT, CF.getStrReportDateFormat())+" - "+uF.getDateFormat(arr[1], DATE_FORMAT, CF.getStrReportDateFormat())); 
				
				hmReimbursement.put("APPLIED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				hmReimbursement.put("REIMBURSEMENT_APPLY_TYPE", rs.getString("reimbursement_type1"));
				if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("P")) {
					hmReimbursement.put("PROJECT", uF.showData(hmProjectMap.get(rs.getString("pro_id")), ""));
					hmReimbursement.put("CLIENT", uF.showData(hmProjectClientMap.get(rs.getString("client_id")), ""));
					hmReimbursement.put("REIMBURSEMENT_TYPE", "Project");
					hmReimbursement.put("REIMBURSEMENT_INFO", rs.getString("reimbursement_info"));
					hmReimbursement.put("IS_BILLABLE", uF.showYesNo(rs.getString("is_billable")));
				} else if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("T")) {
					hmReimbursement.put("REIMBURSEMENT_TYPE", "Travel Plan");
					hmReimbursement.put("REIMBURSEMENT_INFO", rs.getString("reimbursement_info"));
				}else if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("L")) {
					hmReimbursement.put("REIMBURSEMENT_TYPE", "Local");
					hmReimbursement.put("REIMBURSEMENT_INFO", rs.getString("reimbursement_info"));
				}else if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("M")) {
					hmReimbursement.put("REIMBURSEMENT_TYPE", "Mobile Bill");
					hmReimbursement.put("REIMBURSEMENT_INFO", rs.getString("reimbursement_info"));
				}

				hmReimbursement.put("REIMBURSEMENT_PURPOSE", rs.getString("reimbursement_purpose"));
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				if(rs.getInt("reimb_currency") > 0) {
					strCurrId = rs.getString("reimb_currency"); 
				}
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if (hmCurrencyInner == null) hmCurrencyInner = new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				hmReimbursement.put("REIMBURSEMENT_CURRENCY", strCurrSymbol);
				hmReimbursement.put("REIMBURSEMENT_AMOUNT", ""+uF.parseToDouble(rs.getString("reimbursement_amount")));
				hmReimbursement.put("REIMBURSEMENT_PAYMENT_MODE", uF.getPaymentModeExpenses(rs.getInt("reimb_payment_mode")));
				
				hmReimbursement.put("TRAVEL_FROM", rs.getString("travel_from"));
				hmReimbursement.put("TRAVEL_TO", rs.getString("travel_to"));
				hmReimbursement.put("TRAVEL_MODE", rs.getString("travel_mode"));
				hmReimbursement.put("NO_OF_PERSON", rs.getString("no_person"));
				hmReimbursement.put("NO_OF_DAYS", rs.getString("no_days"));
				hmReimbursement.put("TRAVEL_DISTANCE", rs.getString("travel_distance"));
				hmReimbursement.put("TRAVEL_RATE", rs.getString("travel_rate"));
				
				String[] strDocs = null;
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>0) {
					strDocs = rs.getString("ref_document").split(":_:");
				}
				StringBuilder sbDoc = new StringBuilder();
				for (int k = 0; strDocs != null && k < strDocs.length; k++) {
					
					if(CF.getStrDocRetriveLocation()==null){
						sbDoc.append("<a target=\"blank\" href=\"" + request.getContextPath()+DOCUMENT_LOCATION + strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}else{
						sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				hmReimbursement.put("ATTACH_DOCUMENT", sbDoc.toString());

				hmReimbursement.put("VENDOR", rs.getString("vendor"));
				
				StringBuilder sbReceiptNo = null; 
				if(rs.getString("receipt_no")!=null && !rs.getString("receipt_no").trim().equals("") && !rs.getString("receipt_no").trim().equalsIgnoreCase("NULL")){
					String[] strReceipt = rs.getString("receipt_no").split(":_:");
					for(int i = 0; strReceipt != null && i < strReceipt.length; i++){
						if(sbReceiptNo == null){
							sbReceiptNo = new StringBuilder();
							sbReceiptNo.append(strReceipt[i]);
						} else {
							sbReceiptNo.append(", "+strReceipt[i]);
						}
					}
				}
				if(sbReceiptNo == null){
					sbReceiptNo = new StringBuilder();
				}
				hmReimbursement.put("RECEIPT_NO", sbReceiptNo.toString());
				
				hmReimbursement.put("TRANSPORT_TYPE", uF.showData(hmTravelTransportType.get(rs.getString("transport_type")), ""));
				if(uF.parseToInt(rs.getString("transport_type")) == 1){
					hmReimbursement.put("TRANSPORT_MODE", uF.showData(hmTrainType.get(rs.getString("transport_mode")), ""));
				} else if(uF.parseToInt(rs.getString("transport_type")) == 2){
					hmReimbursement.put("TRANSPORT_MODE", uF.showData(hmBusType.get(rs.getString("transport_mode")), ""));
				} else if(uF.parseToInt(rs.getString("transport_type")) == 3){
					hmReimbursement.put("TRANSPORT_MODE", uF.showData(hmFlightType.get(rs.getString("transport_mode")), ""));
				} else if(uF.parseToInt(rs.getString("transport_type")) == 4){
					hmReimbursement.put("TRANSPORT_MODE", uF.showData(hmCarType.get(rs.getString("transport_mode")), ""));
				}
				
				hmReimbursement.put("TRANSPORT_AMOUNT", ""+uF.parseToDouble(rs.getString("transport_amount")));
				hmReimbursement.put("LODGING_TYPE", uF.showData(hmLodgingType.get(rs.getString("lodging_type")),""));
				hmReimbursement.put("LODGING_AMOUNT", ""+uF.parseToDouble(rs.getString("lodging_amount")));
				hmReimbursement.put("LOCAL_CONVEYANCE_TYPE", uF.showData(rs.getString("local_conveyance_type"), ""));
				hmReimbursement.put("LOCAL_CONVEYANCE_KM", ""+uF.parseToDouble(rs.getString("local_conveyance_km")));
				hmReimbursement.put("LOCAL_CONVEYANCE_RATE", ""+uF.parseToDouble(rs.getString("local_conveyance_rate")));
				hmReimbursement.put("LOCAL_CONVEYANCE_AMOUNT", ""+uF.parseToDouble(rs.getString("local_conveyance_amount")));
				hmReimbursement.put("FOOD_BEVERAGE_AMOUNT", ""+uF.parseToDouble(rs.getString("food_beverage_amount")));
				hmReimbursement.put("LAUNDRY_AMOUNT", ""+uF.parseToDouble(rs.getString("laundry_amount")));
				hmReimbursement.put("SUNDRY_AMOUNT", ""+uF.parseToDouble(rs.getString("sundry_amount")));
				
				alBulkExpenseData.add(hmReimbursement);
				
				hmBulkExpenseData.put(rs.getString("reimbursement_type1"), alBulkExpenseData);
				
			}
			rs.close();
			pst.close();

			request.setAttribute("parentId", getParentId());
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
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	
}
