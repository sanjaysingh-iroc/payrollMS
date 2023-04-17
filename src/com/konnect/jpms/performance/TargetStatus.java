package com.konnect.jpms.performance;

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

public class TargetStatus implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	private String goalid;
	private String empid;
	private String type;
	private String  assignedTarget;
	private String measureType;
	
	private String goalFreqId;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";	
		UtilityFunctions uF=new UtilityFunctions();
		getTargetStatus(uF);
		
		return "popup";
	}
	
	private void getTargetStatus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Database db = new Database(); 
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			pst = con.prepareStatement("select td.added_by,gd.measure_type,td.amt_percentage,td.entry_date,td.target_remark,td.target_id,gd.org_id" +
				" from target_details td,goal_details gd, goal_details_frequency gdf where td.goal_id=gd.goal_id and td.goal_id=gdf.goal_id " +
				" and (gdf.is_delete is null or gdf.is_delete = false) and td.goal_id=? and td.emp_id=? and gdf.goal_freq_id=? order by td.entry_date desc,td.entry_time desc");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			pst.setInt(2, uF.parseToInt(getEmpid()));
			pst.setInt(3, uF.parseToInt(getGoalFreqId()));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<List<String>> targetStatusList=new ArrayList<List<String>>();
			String measure_type="";
			String strOrgCurrId = null;
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(hmEmpCodeName.get(rs.getString("added_by")));//0
				String val="";
				if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")){
					measure_type="Days and Hours";
					String daysHr=uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amt_percentage")));
					String days="0";
					String hrs="0";
					if(daysHr.contains(".")) {
						daysHr=daysHr.replace(".", "_");
						String[] temp=daysHr.split("_"); 
						days=temp[0];
						hrs=temp[1];
						val=days+" Days and "+hrs+" Hrs."; 
					} else {
						days=daysHr;
						val=days+" Days.";
					}
				} else if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Amount")){
					measure_type="Amount";
					val= uF.getAmountInCrAndLksFormat(uF.parseToDouble(rs.getString("amt_percentage")));
				} else if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Percentage")){
					measure_type="Percentage";
					val= ""+uF.parseToInt(rs.getString("amt_percentage"));
				}
				/* updated by kalpana on 17/10/2016 
				 * added else if condition for percentage 
				 *  */
				
				innerList.add(val);//1
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//2
				innerList.add(uF.showData(rs.getString("target_remark"),""));//3
				innerList.add(rs.getString("target_id"));//4
				
				targetStatusList.add(innerList);
				
				strOrgCurrId = rs.getString("org_id");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("measure_type",measure_type);
			request.setAttribute("targetStatusList",targetStatusList);
			
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			Map<String,String> hmOrgDetails = CF.getOrgDetails(uF, strOrgCurrId, request);
			String currencyId = hmOrgDetails.get("ORG_CURRENCY");
//			System.out.println("strOrgCurrId==>"+strOrgCurrId);
			if(uF.parseToInt(strOrgCurrId) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(currencyId);
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
//			System.out.println("strCurrency java==>"+strCurrency);
			request.setAttribute("strCurrency",strCurrency);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	public String getGoalid() {
		return goalid;
	}

	public void setGoalid(String goalid) {
		this.goalid = goalid;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAssignedTarget() {
		return assignedTarget;
	}

	public void setAssignedTarget(String assignedTarget) {
		this.assignedTarget = assignedTarget;
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public String getGoalFreqId() {
		return goalFreqId;
	}

	public void setGoalFreqId(String goalFreqId) {
		this.goalFreqId = goalFreqId;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
}
