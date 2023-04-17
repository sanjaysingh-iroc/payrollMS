package com.konnect.jpms.policies;

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

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AllowancePolicy extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	
	HttpSession session;
	CommonFunctions CF; 
	String strUserType;

	String strOrg;
	String strLevel;
	String strSalaryHeadId;
	
	List<FillOrganisation> orgList;
	List<FillLevel> levelList;
	List<FillSalaryHeads> salaryAllowanceHeadList;
	
	String condiOrLogicId;
	String type;
	String operation;
	String isPublish;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute()	{
		
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/policies/AllowancePolicy.jsp");
		request.setAttribute(TITLE, "Allowance Policy");
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		if(getOperation() != null && getOperation().equals("PUP")) {
			checkAndSetPublishUnpublish(uF);
			return "ajax";
		}
		
		if(uF.parseToInt(getStrSalaryHeadId()) > 0) {
			getAllowancePolicyDetails(uF);
		}
		
		return loadAllowancePolicy(uF);
		
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++) {
				if(getStrOrg().equals(orgList.get(i).getOrgId())) {
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
		
		alFilter.add("LEVEL");
		if(getStrLevel()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getStrLevel().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "-");
			}
		} else {
			hmFilter.put("LEVEL", "-");
		}
		
		alFilter.add("SAL_HEAD");
		if(getStrSalaryHeadId()!=null) {
			String strSalaryHead="";
			for(int i=0;salaryAllowanceHeadList!=null && i<salaryAllowanceHeadList.size();i++) {
				if(getStrSalaryHeadId().equals(salaryAllowanceHeadList.get(i).getSalaryHeadId())) {
					strSalaryHead=salaryAllowanceHeadList.get(i).getSalaryHeadName();
				}
			}
			if(strSalaryHead!=null && !strSalaryHead.equals("")) {
				hmFilter.put("SAL_HEAD", strSalaryHead);
			} else {
				hmFilter.put("SAL_HEAD", "-");
			}
		} else {
			hmFilter.put("SAL_HEAD", "-");
		}
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	
	private void checkAndSetPublishUnpublish(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
//			if(getType() != null && getType().equals("C")) {
//				pst = con.prepareStatement("update allowance_condition_details set is_publish=? where allowance_condition_id=?");
//				pst.setBoolean(1, uF.parseToBoolean(getIsPublish()));
//				pst.setInt(2, uF.parseToInt(getCondiOrLogicId()));
//				pst.executeUpdate();
//				pst.close();
//				
//				StringBuilder sbStatus = new StringBuilder();
//				if(uF.parseToBoolean(getIsPublish())) {
//					sbStatus.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to unpublish this condition?'))getPublishConditionORLogic('"+getCondiOrLogicId()+"','C', 'F');\">" +
//						"<img title=\"Published\" src=\"images1/icons/icons/publish_icon_b.png\"></a>");
//				} else {
//					sbStatus.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to publish this condition?'))getPublishConditionORLogic('"+getCondiOrLogicId()+"','C', 'T');\">" +
//						"<img title=\"Published\" src=\"images1/icons/icons/unpublish_icon_b.png\"></a>");
//				}
//				request.setAttribute("STATUS_MSG", sbStatus.toString());
//			}
			/*
			 * Payment Logic
			 * */
			if(getType() != null && getType().equals("PL")) {
				pst = con.prepareStatement("update allowance_payment_logic set is_publish=? where payment_logic_id=? ");
				pst.setBoolean(1, uF.parseToBoolean(getIsPublish()));
				pst.setInt(2, uF.parseToInt(getCondiOrLogicId()));
				pst.executeUpdate();
				pst.close();
				
				StringBuilder sbStatus = new StringBuilder();
				if(uF.parseToBoolean(getIsPublish())) {
					sbStatus.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to unpublish this payment logic?'))getPublishConditionORLogic('"+getCondiOrLogicId()+"','PL', 'F');\">" +
						"<img title=\"Published\" src=\"images1/icons/icons/publish_icon_b.png\"></a>");
				} else {
					sbStatus.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to publish this payment logic?'))getPublishConditionORLogic('"+getCondiOrLogicId()+"','PL', 'T');\">" +
						"<img title=\"Published\" src=\"images1/icons/icons/unpublish_icon_b.png\"></a>");
				}
				request.setAttribute("STATUS_MSG", sbStatus.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getAllowancePolicyDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmAllowanceCondition = CF.getAllowanceCondition();
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getStrLevel()));
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmOrg = CF.getOrgName(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmAllowancePaymentLogic = CF.getAllowancePaymentLogic();
			
			pst = con.prepareStatement("select * from allowance_condition_details where salary_head_id=? and level_id=? and org_id=? order by allowance_condition_id desc");
			pst.setInt(1, uF.parseToInt(getStrSalaryHeadId()));
			pst.setInt(2, uF.parseToInt(getStrLevel()));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			List<Map<String, String>> alCondition = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String,String> hmCondition = new HashMap<String, String>();
				hmCondition.put("ALLOWANCE_CONDITION_ID", rs.getString("allowance_condition_id"));
				hmCondition.put("ALLOWANCE_CONDITION_SLAB", uF.showData(rs.getString("condition_slab"),""));
				hmCondition.put("ALLOWANCE_CONDITION_TYPE", rs.getString("allowance_condition"));
				hmCondition.put("ALLOWANCE_CONDITION", uF.showData(hmAllowanceCondition.get(rs.getString("allowance_condition")),""));
				hmCondition.put("MIN_CONDITION", uF.showData(rs.getString("min_condition"),"0"));
				hmCondition.put("MAX_CONDITION", uF.showData(rs.getString("max_condition"),"0"));
				hmCondition.put("SALARY_HEAD_ID", uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")),""));
				hmCondition.put("LEVEL_ID", uF.showData(hmLevelMap.get(rs.getString("level_id")),""));
				hmCondition.put("ORG_ID", uF.showData(hmOrg.get(rs.getString("org_id")),""));
				hmCondition.put("ADDED_BY", uF.showData(hmEmpName.get(rs.getString("updated_by") != null ? rs.getString("updated_by") : rs.getString("added_by")),""));
				hmCondition.put("ENTRY_DATE", uF.getDateFormat(rs.getString("update_date") != null ? rs.getString("update_date") : rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmCondition.put("CUSTOM_FACTOR_TYPE", uF.showData(rs.getString("custom_type"),"A"));
				hmCondition.put("CUSTOM_AMT_PERCENTAGE", uF.showData(rs.getString("custom_amt_percentage"),"0"));
				
				hmCondition.put("IS_PUBLISH", rs.getString("is_publish"));
				hmCondition.put("UPDATED_BY", uF.showData(hmEmpName.get(rs.getString("updated_by")),""));
				hmCondition.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));
				hmCondition.put("CALCULATE_FROM", uF.showData(rs.getString("calculation_from"),"0"));
				
				hmCondition.put("IS_ADD_ATTENDANCE", uF.parseToBoolean(rs.getString("is_add_days_attendance")) ? "Yes" : "No");
				
				alCondition.add(hmCondition);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alCondition", alCondition);
			
			
			/*
			 * Payment Logic
			 * */
			
			pst = con.prepareStatement("select * from allowance_condition_details");
			rs = pst.executeQuery();
			Map<String, String> hmAllowanceConditionSlab = new HashMap<String, String>();
			while(rs.next()){
				hmAllowanceConditionSlab.put(rs.getString("allowance_condition_id"), uF.showData(rs.getString("condition_slab"),""));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from allowance_payment_logic where salary_head_id=? and level_id=? and org_id=?");
			pst.setInt(1, uF.parseToInt(getStrSalaryHeadId()));
			pst.setInt(2, uF.parseToInt(getStrLevel()));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			List<Map<String, String>> alLogic = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String,String> hmLogic = new HashMap<String, String>();
				hmLogic.put("PAYMENT_LOGIC_ID", rs.getString("payment_logic_id"));
				hmLogic.put("PAYMENT_LOGIC_SLAB", uF.showData(rs.getString("payment_logic_slab"),""));
				hmLogic.put("ALLOWANCE_CONDITION_ID", rs.getString("allowance_condition_id"));
				hmLogic.put("ALLOWANCE_CONDITION", uF.showData(hmAllowanceConditionSlab.get(rs.getString("allowance_condition_id")),""));
				
				hmLogic.put("ALLOWANCE_PAYMENT_LOGIC_ID", rs.getString("payment_logic"));
				hmLogic.put("ALLOWANCE_PAYMENT_LOGIC", uF.showData(hmAllowancePaymentLogic.get(rs.getString("payment_logic")),""));
				
				hmLogic.put("ADDED_BY", uF.showData(hmEmpName.get(rs.getString("updated_by") != null ? rs.getString("updated_by") : rs.getString("added_by")),""));
				hmLogic.put("ENTRY_DATE", uF.getDateFormat(rs.getString("update_date") != null ? rs.getString("update_date") : rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmLogic.put("SALARY_HEAD_ID", uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")),""));
				hmLogic.put("LEVEL_ID", uF.showData(hmLevelMap.get(rs.getString("level_id")),""));
				hmLogic.put("ORG_ID", uF.showData(hmOrg.get(rs.getString("org_id")),""));
				
				hmLogic.put("FIXED_AMOUNT", uF.showData(rs.getString("fixed_amount"),"0"));
				hmLogic.put("CAL_SALARY_HEAD_ID", uF.showData(rs.getString("cal_salary_head_id"),""));
				hmLogic.put("CAL_SALARY_HEAD_NAME", uF.showData(hmSalaryHeadsMap.get(rs.getString("cal_salary_head_id")),""));
				
				hmLogic.put("IS_PUBLISH", rs.getString("is_publish"));
				hmLogic.put("UPDATED_BY", uF.showData(hmEmpName.get(rs.getString("updated_by")),""));
				hmLogic.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));
				
				hmLogic.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				hmLogic.put("PER_HOUR_DAY_AMOUNT", uF.showData(rs.getString("per_hour_day"),"0"));
				hmLogic.put("IS_DEDUCT_FULL_AMOUNT", uF.parseToBoolean(rs.getString("is_deduct_full_amount")) ? "Yes" : "No");
				
				alLogic.add(hmLogic);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLogic", alLogic);
			
			request.setAttribute("SALARY_HEAD_NAME", uF.showData(hmSalaryHeadsMap.get(getStrSalaryHeadId()),""));
			request.setAttribute("LEVEL_NAME", uF.showData(hmLevelMap.get(getStrLevel()),""));
			request.setAttribute("ORG_NAME", uF.showData(hmOrg.get(getStrOrg()),""));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private String loadAllowancePolicy(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
			salaryAllowanceHeadList = new FillSalaryHeads(request).fillAllowanceSalaryHeads(getStrLevel());
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			getSelectedFilter(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
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

	public List<FillSalaryHeads> getSalaryAllowanceHeadList() {
		return salaryAllowanceHeadList;
	}

	public void setSalaryAllowanceHeadList(List<FillSalaryHeads> salaryAllowanceHeadList) {
		this.salaryAllowanceHeadList = salaryAllowanceHeadList;
	}

	public String getCondiOrLogicId() {
		return condiOrLogicId;
	}

	public void setCondiOrLogicId(String condiOrLogicId) {
		this.condiOrLogicId = condiOrLogicId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(String isPublish) {
		this.isPublish = isPublish;
	}

}
