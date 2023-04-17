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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryCalculationTypes;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GratuityReport extends ActionSupport implements ServletRequestAware, IStatements {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	CommonFunctions CF = null; 
	
	String gratuityUpdate;
	String strOrg;		
	String[] strSalaryHeadId;
	String strServiceFrom;
	String strServiceTo;
	String strGratuityDays;
	String strMaxGratuityAmount;
	String strCalBasis;
	String strFixedDays;
	String strCalPercent;
	String effectiveDate;
	
	List<FillOrganisation> orgList;
	List<FillSalaryHeads> salaryHeadList;
	List<FillSalaryCalculationTypes> calBasisList;
	 
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PReportGratuity);
		request.setAttribute(TITLE, TViewGratuity);
		
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
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsByOrgWithoutCTC(null, getStrOrg());
		calBasisList = new FillSalaryCalculationTypes().fillSalaryCalculationTypes();
		
		if(getGratuityUpdate()!=null){
			updateGratuitySetting(uF);
		}
		viewGratuitySetting(uF);		
		
		return LOAD;
	}
	
	
	public void updateGratuitySetting(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			StringBuilder sb = null;
			for(int i=0; getStrSalaryHeadId()!=null && i<getStrSalaryHeadId().length; i++){
				if(uF.parseToInt(getStrSalaryHeadId()[i]) > 0){
					if(sb == null){
						sb = new StringBuilder();
						sb.append(","+getStrSalaryHeadId()[i]+",");
					} else {
						sb.append(getStrSalaryHeadId()[i]+",");
					}
				}
			}
			if(sb == null){
				sb = new StringBuilder();
			}
			
			/*pst = con.prepareStatement("update gratuity_details set service_from=?, service_to=?, gratuity_days=?, max_amount=?,entry_date=?," +
					" user_id=?,salary_cal_basis=?,salary_head_id=?,fixed_days=? where org_id=?");
			pst.setDouble(1, uF.parseToDouble(getStrServiceFrom()));
			pst.setDouble(2, uF.parseToDouble(getStrServiceTo()));
			pst.setInt(3, uF.parseToInt(getStrGratuityDays()));
			pst.setDouble(4, uF.parseToDouble(getStrMaxGratuityAmount()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setString(7, getStrCalBasis());
			pst.setString(8, sb.toString());
			pst.setDouble(9, getStrCalBasis() !=null && getStrCalBasis().equals("AFD") ? uF.parseToDouble(getStrFixedDays()) : 0.0d);
			pst.setInt(10, uF.parseToInt(getStrOrg()));
			int x = pst.executeUpdate();
			pst.close();*/
			
			/*if(x == 0){
				pst = con.prepareStatement("INSERT INTO gratuity_details (service_from, service_to, gratuity_days, max_amount,entry_date, user_id," +
						"salary_cal_basis,salary_head_id,org_id,fixed_days) VALUES (?,?,?,?, ?,?,?,?, ?,?)");
				pst.setDouble(1, uF.parseToDouble(getStrServiceFrom()));
				pst.setDouble(2, uF.parseToDouble(getStrServiceTo()));
				pst.setInt(3, uF.parseToInt(getStrGratuityDays()));
				pst.setDouble(4, uF.parseToDouble(getStrMaxGratuityAmount()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setString(7, getStrCalBasis());
				pst.setString(8, sb.toString());
				pst.setInt(9, uF.parseToInt(getStrOrg()));
				pst.setDouble(10, getStrCalBasis() !=null && getStrCalBasis().equals("AFD") ? uF.parseToDouble(getStrFixedDays()) : 0.0d);
				pst.execute();
				pst.close();
			}*/
			
			pst = con.prepareStatement("update gratuity_details set salary_head_id=?,calculate_percent=?,entry_date=?,user_id=? where org_id=? and effective_date=?");
			pst.setString(1, sb.toString());
			pst.setDouble(2, uF.parseToDouble(getStrCalPercent()));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(5, uF.parseToInt(getStrOrg()));
			pst.setDate(6, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x == 0) {
				pst = con.prepareStatement("INSERT INTO gratuity_details (salary_head_id,calculate_percent,org_id,entry_date,user_id,effective_date) " +
					" VALUES (?,?,?,?, ?,?)");
				pst.setString(1, sb.toString());
				pst.setDouble(2, uF.parseToDouble(getStrCalPercent()));
				pst.setInt(3, uF.parseToInt(getStrOrg()));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setDate(6, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.executeUpdate();
				pst.close();
			}
			
			request.setAttribute(MESSAGE, SUCCESSM+"Gratuity policy saved successfully."+END);
		
		}catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, ERRORM+"Gratuity policy not saved. Please try again!"+END);
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void viewGratuitySetting(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getStrOrg());
			
			pst = con.prepareStatement(selectGratuity);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			List<Map<String, String>> alGratuity = new ArrayList<Map<String,String>>();
			while(rs.next()){
				setStrOrg(rs.getString("org_id"));
				setStrSalaryHeadId(rs.getString("salary_head_id")!=null ? rs.getString("salary_head_id").split(",") : null);
				setStrCalPercent(rs.getString("calculate_percent"));
				setEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				
				Map<String, String> hmGratuity = new HashMap<String, String>();
				hmGratuity.put("ORG_NAME", uF.showData(hmOrg.get("ORG_NAME"), ""));
				StringBuilder sb = new StringBuilder();
				if(rs.getString("salary_head_id")!=null){
					String []arr = rs.getString("salary_head_id").split(",");
					for(int i=0; i<arr.length; i++){
						if(arr[i] != null && uF.parseToInt(arr[i].trim()) > 0){
							sb.append((String)hmSalaryMap.get(arr[i].trim()));
							if(i<(arr.length-1)) {
								sb.append(", ");
							}
						}
					}
				}
				hmGratuity.put("SALARY_HEAD", sb.toString());
				hmGratuity.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				hmGratuity.put("CALCULATE_PERCENT", rs.getString("calculate_percent"));
				alGratuity.add(hmGratuity);
				
				request.setAttribute("UPDATED_NAME", uF.showData(hmEmpName.get(rs.getString("user_id")), ""));
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			/*pst = con.prepareStatement(selectGratuity);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			List<Map<String, String>> alGratuity = new ArrayList<Map<String,String>>();
			while(rs.next()){
				setStrOrg(rs.getString("org_id"));
				setStrSalaryHeadId(rs.getString("salary_head_id")!=null ? rs.getString("salary_head_id").split(",") : null);
				setStrCalBasis(rs.getString("salary_cal_basis"));
				setStrFixedDays(uF.showData(rs.getString("fixed_days"), "0"));
				setStrServiceFrom(uF.showData(rs.getString("service_from"), "0"));
				setStrServiceTo(uF.showData(rs.getString("service_to"), "0"));
				setStrGratuityDays(uF.showData(rs.getString("gratuity_days"), "0"));
				setStrMaxGratuityAmount(uF.showData(rs.getString("max_amount"), "0"));
				
				Map<String, String> hmGratuity = new HashMap<String, String>();
				hmGratuity.put("ORG_NAME", uF.showData(hmOrg.get("ORG_NAME"), ""));
				StringBuilder sb = new StringBuilder();
				if(rs.getString("salary_head_id")!=null){
					String []arr = rs.getString("salary_head_id").split(",");
					for(int i=0; i<arr.length; i++){
						if(arr[i] != null && uF.parseToInt(arr[i].trim()) > 0){
							sb.append((String)hmSalaryMap.get(arr[i].trim())+", ");
						}
					}
				}
				hmGratuity.put("SALARY_HEAD", sb.toString());
				
				hmGratuity.put("SALARY_CAL_BASIS_ID", uF.showData(rs.getString("salary_cal_basis"), ""));
				String salary_cal_basis = "Fixed Days";
				if(rs.getString("salary_cal_basis")!=null && rs.getString("salary_cal_basis").equals("AMD")){
					salary_cal_basis = "Actual Month Days";
				} else if(rs.getString("salary_cal_basis")!=null && rs.getString("salary_cal_basis").equals("AWD")){
					salary_cal_basis = "Actual Working Days";
				}
				hmGratuity.put("SALARY_CAL_BASIS", salary_cal_basis);
				hmGratuity.put("FIXED_DAYS", uF.showData(rs.getString("fixed_days"), "0"));
				hmGratuity.put("SERVICE_FROM", uF.showData(rs.getString("service_from"), "0"));
				hmGratuity.put("SERVICE_TO", uF.showData(rs.getString("service_to"), "0"));
				hmGratuity.put("GRATUITY_DAYS", uF.showData(rs.getString("gratuity_days"), "0"));
				hmGratuity.put("MAX_AMOUNT", uF.showData(rs.getString("max_amount"), "0"));
				
				alGratuity.add(hmGratuity);
				
				request.setAttribute("UPDATED_NAME", uF.showData(hmEmpName.get(rs.getString("user_id")), ""));
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();*/
			
			request.setAttribute("alGratuity", alGratuity);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getGratuityUpdate() {
		return gratuityUpdate;
	}

	public void setGratuityUpdate(String gratuityUpdate) {
		this.gratuityUpdate = gratuityUpdate;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String[] getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String[] strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public String getStrServiceFrom() {
		return strServiceFrom;
	}

	public void setStrServiceFrom(String strServiceFrom) {
		this.strServiceFrom = strServiceFrom;
	}

	public String getStrServiceTo() {
		return strServiceTo;
	}

	public void setStrServiceTo(String strServiceTo) {
		this.strServiceTo = strServiceTo;
	}

	public String getStrGratuityDays() {
		return strGratuityDays;
	}

	public void setStrGratuityDays(String strGratuityDays) {
		this.strGratuityDays = strGratuityDays;
	}

	public String getStrMaxGratuityAmount() {
		return strMaxGratuityAmount;
	}

	public void setStrMaxGratuityAmount(String strMaxGratuityAmount) {
		this.strMaxGratuityAmount = strMaxGratuityAmount;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String getStrCalBasis() {
		return strCalBasis;
	}

	public void setStrCalBasis(String strCalBasis) {
		this.strCalBasis = strCalBasis;
	}

	public List<FillSalaryCalculationTypes> getCalBasisList() {
		return calBasisList;
	}

	public void setCalBasisList(List<FillSalaryCalculationTypes> calBasisList) {
		this.calBasisList = calBasisList;
	}

	public String getStrFixedDays() {
		return strFixedDays;
	}

	public void setStrFixedDays(String strFixedDays) {
		this.strFixedDays = strFixedDays;
	}

	public String getStrCalPercent() {
		return strCalPercent;
	}

	public void setStrCalPercent(String strCalPercent) {
		this.strCalPercent = strCalPercent;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

}
