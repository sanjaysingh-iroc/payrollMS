package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReimbursementPolicy extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	String strUserType = null;
	String strSessionEmpId = null;
	
	HttpSession session;
	CommonFunctions CF; 

	String f_org;
	List<FillOrganisation> orgList;
	List<FillLevel> levelList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute()	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		request.setAttribute(PAGE, "/jsp/reports/master/ReimbursementPolicy.jsp");
		request.setAttribute(TITLE, "Travel, Claims & Reimbursement Policy");
		
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String) session.getAttribute(ORGID));
		}
		orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		
		levelList=new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		getMobileBillReimbursementPolicy(uF);
		getLocalBillReimbursementPolicy(uF);
		getTravelAdvanceReimbursementPolicy(uF);
		getClaimReimbursementPolicy(uF);
		getReimbursementPartOfCTC(uF);
		
		getSelectedFilter(uF);
		return LOAD;
	}
	
	private void getReimbursementPartOfCTC(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from reimbursement_ctc_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmReimbursementCTC = new HashMap<String, List<Map<String,String>>>(); 
			while(rs.next()){
				List<Map<String, String>> alReimbursementCTC = hmReimbursementCTC.get(rs.getString("level_id"));
				if(alReimbursementCTC == null) alReimbursementCTC = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmReimbursementCTCInner = new HashMap<String, String>();
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ID", rs.getString("reimbursement_ctc_id"));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_CODE", uF.showData(rs.getString("reimbursement_code"), ""));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_NAME", uF.showData(rs.getString("reimbursement_name"), ""));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_LEVEL_ID", rs.getString("level_id"));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ORG_ID", rs.getString("org_id"));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), ""));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ADDED_DATE", uF.getDateFormat(rs.getString("added_date"), DBDATE, CF.getStrReportDateFormat()));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_UPDATE_BY", uF.showData(hmEmpCodeName.get(rs.getString("update_by")), ""));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alReimbursementCTC.add(hmReimbursementCTCInner);
				
				hmReimbursementCTC.put(rs.getString("level_id"),alReimbursementCTC);				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmReimbursementCTC", hmReimbursementCTC);
			
			pst = con.prepareStatement("select * from reimbursement_head_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmReimbursementCTCHead = new HashMap<String, List<Map<String,String>>>(); 
			List<String> aReimHeadId = new ArrayList<String>();
			while(rs.next()){
				List<Map<String, String>> alReimbursementCTCHead = hmReimbursementCTCHead.get(rs.getString("reimbursement_ctc_id"));
				if(alReimbursementCTCHead == null) alReimbursementCTCHead = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmReimbursementHeadInner = new HashMap<String, String>();
				hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursement_head_id"));
				hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_CODE", uF.showData(rs.getString("reimbursement_head_code"), ""));
				hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_NAME", uF.showData(rs.getString("reimbursement_head_name"), ""));
				hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_DESC", uF.showData(rs.getString("reimbursement_head_description"), ""));
				hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), ""));
				hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_ADDED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_LEVEL_ID", rs.getString("level_id"));
				hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_ORG_ID", rs.getString("org_id"));
				
				alReimbursementCTCHead.add(hmReimbursementHeadInner);
				
				hmReimbursementCTCHead.put(rs.getString("reimbursement_ctc_id"),alReimbursementCTCHead);	
				
				if(!aReimHeadId.contains(rs.getString("reimbursement_head_id"))){
					aReimHeadId.add(rs.getString("reimbursement_head_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmReimbursementCTCHead", hmReimbursementCTCHead);
//			System.out.println("hmReimbursementCTCHead==>"+hmReimbursementCTCHead);
			
			if(aReimHeadId.size() > 0){
				String strReimHeadIds = StringUtils.join(aReimHeadId.toArray(),",");
				pst = con.prepareStatement("select * from reimbursement_head_amt_details where reimbursement_head_id in ("+strReimHeadIds+") " +
						"order by financial_year_start desc,reimbursement_head_id");
				rs = pst.executeQuery();
				Map<String, List<Map<String, String>>> hmReimbursementHeadAmt = new HashMap<String, List<Map<String,String>>>(); 
				while(rs.next()){
					List<Map<String, String>> alReimHeadAmt = hmReimbursementHeadAmt.get(rs.getString("reimbursement_head_id"));
					if(alReimHeadAmt == null) alReimHeadAmt = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("AMOUNT",uF.showData(rs.getString("amount"), "0"));
					hmInner.put("FINANCIAL_YEAR",uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, CF.getStrReportDateFormat()) + " to "
							+ uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, CF.getStrReportDateFormat()));
					hmInner.put("ATTACHMENT",uF.showYesNo(rs.getString("is_attachment")));
					hmInner.put("IS_OPTIMAL",uF.showYesNo(rs.getString("is_optimal")));
					
					alReimHeadAmt.add(hmInner);
					
					hmReimbursementHeadAmt.put(rs.getString("reimbursement_head_id"),alReimHeadAmt);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmReimbursementHeadAmt", hmReimbursementHeadAmt);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	private void getClaimReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			
			Map<String, String> hmCountry = CF.getCountryMap(con);
			if(hmCountry == null) hmCountry = new HashMap<String, String>();
			
			Map<String, String> hmTravelTransportType = CF.getTravelTransportType(uF);
			Map<String, String> hmTrainType = CF.getTrainType(uF);
			Map<String, String> hmBusType = CF.getBusType(uF);
			Map<String, String> hmFlightType = CF.getFlightType(uF);
			Map<String, String> hmCarType = CF.getCarType(uF);
			Map<String, String> hmLimitType = CF.getLimitType(uF);
			Map<String, String> hmLodgingType = CF.getLodgingType(uF);
			
			pst = con.prepareStatement("select * from reimbursement_policy where org_id=? and reimbursement_policy_type=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, REIMBURSEMENTS_CLAIM);
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmClaim = new HashMap<String, List<Map<String,String>>>(); 
			while(rs.next()){
				List<Map<String, String>> alClaim = hmClaim.get(rs.getString("level_id"));
				if(alClaim == null) alClaim = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmClaimInner = new HashMap<String, String>();
				hmClaimInner.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmClaimInner.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmClaimInner.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmClaimInner.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				hmClaimInner.put("REIMBURSEMENT_ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), ""));
				hmClaimInner.put("REIMBURSEMENT_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_TYPE_ID", rs.getString("travel_transport_type"));
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_TYPE", uF.showData(hmTravelTransportType.get(rs.getString("travel_transport_type")), ""));
				
				hmClaimInner.put("REIMBURSEMENT_TRAIN_TYPE_ID", rs.getString("train_type"));
				hmClaimInner.put("REIMBURSEMENT_TRAIN_TYPE", uF.showData(hmTrainType.get(rs.getString("train_type")), ""));
				
				hmClaimInner.put("REIMBURSEMENT_BUS_TYPE_ID", rs.getString("bus_type"));
				hmClaimInner.put("REIMBURSEMENT_BUS_TYPE", uF.showData(hmBusType.get(rs.getString("bus_type")), ""));
				
				hmClaimInner.put("REIMBURSEMENT_FLIGHT_TYPE_ID", rs.getString("flight_type"));
				hmClaimInner.put("REIMBURSEMENT_FLIGHT_TYPE", uF.showData(hmFlightType.get(rs.getString("flight_type")), ""));
				
				hmClaimInner.put("REIMBURSEMENT_CAR_TYPE_ID", rs.getString("car_type"));
				hmClaimInner.put("REIMBURSEMENT_CAR_TYPE", uF.showData(hmCarType.get(rs.getString("car_type")), ""));
				
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_LIMIT_TYPE_ID", rs.getString("travel_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_LIMIT_TYPE", uF.showData(hmLimitType.get(rs.getString("travel_limit_type")),""));
				hmClaimInner.put("REIMBURSEMENT_TRAVEL_LIMIT", ""+uF.parseToDouble(rs.getString("travel_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LODGING_TYPE_ID", rs.getString("lodging_type"));
				hmClaimInner.put("REIMBURSEMENT_LODGING_TYPE", uF.showData(hmLodgingType.get(rs.getString("lodging_type")),""));
				hmClaimInner.put("REIMBURSEMENT_LODGING_LIMIT_TYPE_ID", rs.getString("lodging_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_LODGING_LIMIT_TYPE", uF.showData(hmLimitType.get(rs.getString("lodging_limit_type")),""));
				hmClaimInner.put("REIMBURSEMENT_LODGING_LIMIT", ""+uF.parseToDouble(rs.getString("lodging_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LOCAL_CONVEYANCE_TRAN_ID", rs.getString("local_conveyance_tran_type"));
				hmClaimInner.put("REIMBURSEMENT_LOCAL_CONVEYANCE_TRAN_TYPE", rs.getString("local_conveyance_tran_type"));
				hmClaimInner.put("REIMBURSEMENT_LOCAL_CONVEYANCE_LIMIT", ""+uF.parseToDouble(rs.getString("local_conveyance_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_FOOD_LIMIT_TYPE_ID", rs.getString("food_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_FOOD_LIMIT_TYPE", uF.showData(hmLimitType.get(rs.getString("food_limit_type")),""));
				hmClaimInner.put("REIMBURSEMENT_FOOD_LIMIT", ""+uF.parseToDouble(rs.getString("food_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_LAUNDRY_LIMIT_TYPE_ID", rs.getString("laundry_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_LAUNDRY_LIMIT_TYPE", uF.showData(hmLimitType.get(rs.getString("laundry_limit_type")),""));
				hmClaimInner.put("REIMBURSEMENT_LAUNDRY_LIMIT", ""+uF.parseToDouble(rs.getString("laundry_limit")));
				
				hmClaimInner.put("REIMBURSEMENT_SUNDRY_LIMIT_TYPE_ID", rs.getString("sundry_limit_type"));
				hmClaimInner.put("REIMBURSEMENT_SUNDRY_LIMIT_TYPE", uF.showData(hmLimitType.get(rs.getString("sundry_limit_type")),""));
				hmClaimInner.put("REIMBURSEMENT_SUNDRY_LIMIT", ""+uF.parseToDouble(rs.getString("sundry_limit")));
				
				
				alClaim.add(hmClaimInner);
				
				hmClaim.put(rs.getString("level_id"),alClaim);				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClaim", hmClaim);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getTravelAdvanceReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			
			Map<String, String> hmCountry = CF.getCountryMap(con);
			if(hmCountry == null) hmCountry = new HashMap<String, String>();
			
			Map<String, String> hmEligibleType = CF.getEligibleType(uF);
			
			pst = con.prepareStatement("select * from reimbursement_policy where org_id=? and reimbursement_policy_type=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, REIMBURSEMENTS_TRAVEL_ADVANCE);
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmTravelAdvance = new HashMap<String, List<Map<String,String>>>(); 
			while(rs.next()){
				List<Map<String, String>> alTravelAdvance = hmTravelAdvance.get(rs.getString("level_id"));
				if(alTravelAdvance == null) alTravelAdvance = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmTravelAdvanceInner = new HashMap<String, String>();
				hmTravelAdvanceInner.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmTravelAdvanceInner.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				
				hmTravelAdvanceInner.put("REIMBURSEMENT_COUNTRY_ID", rs.getString("country_id"));
				hmTravelAdvanceInner.put("REIMBURSEMENT_COUNTRY_NAME", uF.showData(hmCountry.get(rs.getString("country_id")), ""));
				hmTravelAdvanceInner.put("REIMBURSEMENT_CITY", uF.showData(rs.getString("city"), ""));
				hmTravelAdvanceInner.put("REIMBURSEMENT_ELIGIBLE_AMOUNT", ""+uF.parseToDouble(rs.getString("eligible_amount")));
				hmTravelAdvanceInner.put("REIMBURSEMENT_ELIGIBLE_TYPE_ID", rs.getString("eligible_type"));
				hmTravelAdvanceInner.put("REIMBURSEMENT_ELIGIBLE_TYPE", uF.showData(hmEligibleType.get(rs.getString("eligible_type")),""));
				
				hmTravelAdvanceInner.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmTravelAdvanceInner.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				hmTravelAdvanceInner.put("REIMBURSEMENT_ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), ""));
				hmTravelAdvanceInner.put("REIMBURSEMENT_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alTravelAdvance.add(hmTravelAdvanceInner);
				
				hmTravelAdvance.put(rs.getString("level_id"),alTravelAdvance);				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmTravelAdvance", hmTravelAdvance);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getLocalBillReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			
			Map<String, String> hmLocalType = CF.getLocalType(uF);
			Map<String, String> hmLimitType = CF.getLimitType(uF);
			
			pst = con.prepareStatement("select * from reimbursement_policy where org_id=? and reimbursement_policy_type=? ");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, REIMBURSEMENTS_LOCAL);
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmLocalBill = new HashMap<String, List<Map<String,String>>>(); 
			while(rs.next()){
				List<Map<String, String>> alLocalBill = hmLocalBill.get(rs.getString("level_id"));
				if(alLocalBill == null) alLocalBill = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmLocalInner = new HashMap<String, String>();
				hmLocalInner.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmLocalInner.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmLocalInner.put("REIMBURSEMENT_IS_LOCAL_POLICY", rs.getString("is_default_policy"));
				hmLocalInner.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmLocalInner.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				hmLocalInner.put("REIMBURSEMENT_ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), ""));
				hmLocalInner.put("REIMBURSEMENT_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				hmLocalInner.put("REIMBURSEMENT_LOCAL_TYPE_ID", rs.getString("local_type"));
				hmLocalInner.put("REIMBURSEMENT_LOCAL_TYPE", uF.showData(hmLocalType.get(rs.getString("local_type")),""));
				hmLocalInner.put("REIMBURSEMENT_TRANSPORT_TYPE", rs.getString("transport_type"));
				hmLocalInner.put("REIMBURSEMENT_LOCAL_LIMIT_TYPE_ID", rs.getString("local_limit_type"));
				hmLocalInner.put("REIMBURSEMENT_LOCAL_LIMIT_TYPE", uF.showData(hmLimitType.get(rs.getString("local_limit_type")),""));
				hmLocalInner.put("REIMBURSEMENT_LOCAL_LIMIT", ""+uF.parseToDouble(rs.getString("local_limit")));
				hmLocalInner.put("REIMBURSEMENT_IS_REQUIRE_POLICY", rs.getString("is_require_approval"));
				hmLocalInner.put("REIMBURSEMENT_MIN_AMOUNT", ""+uF.parseToDouble(rs.getString("min_amount")));
				hmLocalInner.put("REIMBURSEMENT_MAX_AMOUNT", ""+uF.parseToDouble(rs.getString("max_amount")));
				
				alLocalBill.add(hmLocalInner);
				
				hmLocalBill.put(rs.getString("level_id"),alLocalBill);				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmLocalBill", hmLocalBill);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getMobileBillReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from reimbursement_policy where org_id=? and reimbursement_policy_type=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, REIMBURSEMENTS_MOBILE_BILL);
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmMobileBill = new HashMap<String, List<Map<String,String>>>(); 
			while(rs.next()){
				List<Map<String, String>> alMobileBill = hmMobileBill.get(rs.getString("level_id"));
				if(alMobileBill == null) alMobileBill = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmMobileInner = new HashMap<String, String>();
				hmMobileInner.put("REIMBURSEMENT_POLICY_ID", rs.getString("reimbursement_policy_id"));
				hmMobileInner.put("REIMBURSEMENT_POLICY_TYPE", rs.getString("reimbursement_policy_type"));
				hmMobileInner.put("REIMBURSEMENT_IS_MOBILE_POLICY", rs.getString("is_default_policy"));
//				hmMobileInner.put("REIMBURSEMENT_MOBILE_LIMIT_TYPE", uF.parseToInt(rs.getString("mobile_limit_type")) == 2 ? "Actual" : "No Limit");
				hmMobileInner.put("REIMBURSEMENT_MOBILE_LIMIT_TYPE", rs.getString("mobile_limit_type"));
				hmMobileInner.put("REIMBURSEMENT_MOBILE_LIMIT", rs.getString("mobile_limit"));
				hmMobileInner.put("REIMBURSEMENT_LEVEL_ID", rs.getString("level_id"));
				hmMobileInner.put("REIMBURSEMENT_ORG_ID", rs.getString("org_id"));
				hmMobileInner.put("REIMBURSEMENT_ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), ""));
				hmMobileInner.put("REIMBURSEMENT_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alMobileBill.add(hmMobileInner);
				
				hmMobileBill.put(rs.getString("level_id"),alMobileBill);				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMobileBill", hmMobileBill);
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
		this.request = request;
	}
	public String getF_org() {
		return f_org;
	}
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}
	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
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
	
}