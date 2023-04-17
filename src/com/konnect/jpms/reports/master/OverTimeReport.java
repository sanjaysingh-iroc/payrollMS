package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OverTimeReport extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	List<FillLevel> levelList;
	List<FillSalaryHeads> salaryHeadList;
	private static Logger log = Logger.getLogger(OverTimeReport.class);
	
	String f_org;
	List<FillOrganisation> orgList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, POverTime);
		request.setAttribute(TITLE, "Overtime Policy");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getF_org()) == 0  && orgList!=null && orgList.size()>0){
				setF_org(orgList.get(0).getOrgId());
			}
		}else{
			if(uF.parseToInt(getF_org()) == 0){
				setF_org((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		levelList=new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		getOverTimeDetails(uF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
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
	

	
	private void getOverTimeDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			Map<String, List<Map<String, String>>> hmEmpOverTimeLevelPolicy = new HashMap<String, List<Map<String,String>>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from overtime_details where overtime_id>0");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by level_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			StringBuffer sbOtId = null; 
			while(rs.next()){
				List<Map<String, String>> alInner = (List<Map<String,String>>) hmEmpOverTimeLevelPolicy.get(rs.getString("level_id")+"_"+rs.getString("overtime_type"));
				if(alInner == null) alInner = new ArrayList<Map<String,String>>();
				
				Map<String,String> hmEmpOverTimePolicy=new HashMap<String,String>();
				
				hmEmpOverTimePolicy.put("OVERTIME_ID",rs.getString("overtime_id"));
				hmEmpOverTimePolicy.put("OVERTIME_CODE",rs.getString("overtime_code"));
				hmEmpOverTimePolicy.put("OVERTIME_DESCRIPTION",rs.getString("overtime_description"));
				hmEmpOverTimePolicy.put("LEVEL_ID",rs.getString("level_id"));
				String overtimeType="";
				if(rs.getString("overtime_type")!=null && rs.getString("overtime_type").equals("PH")){
					overtimeType="Public Holiday";
				}else if(rs.getString("overtime_type")!=null && rs.getString("overtime_type").equals("BH")){
					overtimeType="Weekend";
				}else if(rs.getString("overtime_type")!=null && rs.getString("overtime_type").equals("EH")){
					overtimeType="Extra Hour worked";
				}
				hmEmpOverTimePolicy.put("OVERTIME_TYPE",overtimeType);
				hmEmpOverTimePolicy.put("OVERTIME_PAYMENT_TYPE",rs.getString("overtime_payment_type")!=null ? rs.getString("overtime_payment_type").equals("A")? "Amount":"Percent" : "");
				hmEmpOverTimePolicy.put("DATE_FROM",uF.getDateFormat(rs.getString("date_from"), DBDATE, DATE_FORMAT));
				hmEmpOverTimePolicy.put("DATE_TO",uF.getDateFormat(rs.getString("date_to"), DBDATE, DATE_FORMAT));
				hmEmpOverTimePolicy.put("OVERTIME_PAYMENT_AMOUNT",rs.getString("overtime_payment_amount"));
				String dayCal="";
				// Changed by M@yuri 17-Oct-2016 (Changed MD to AMD)
				if(rs.getString("day_calculation")!=null && rs.getString("day_calculation").equals("AMD")){
					dayCal="Actual Month Days";
				}else if(rs.getString("day_calculation")!=null && rs.getString("day_calculation").equals("AWD")){
					dayCal="Actual Working Days";
				}else if(rs.getString("day_calculation")!=null && rs.getString("day_calculation").equals("F")){
					dayCal="Fixed Days";
				}
				hmEmpOverTimePolicy.put("DAY_CALCULATION",dayCal);
				hmEmpOverTimePolicy.put("FIXED_DAY_CALCULATION",rs.getString("fixed_day_calculation"));
				
				String swh="";
				if(rs.getString("standard_wkg_hours")!=null && rs.getString("standard_wkg_hours").equals("RH")){
					swh="Roster Hours";
				}else if(rs.getString("standard_wkg_hours")!=null && rs.getString("standard_wkg_hours").equals("SWH")){
					swh="Standard Working Hours";
				}else if(rs.getString("standard_wkg_hours")!=null && rs.getString("standard_wkg_hours").equals("F")){
					swh="Fixed Hours";
				}
				hmEmpOverTimePolicy.put("STANDARD_WKG_HOURS",swh);
				hmEmpOverTimePolicy.put("FIXED_STWKG_HOURS",rs.getString("fixed_stwkg_hrs"));
				hmEmpOverTimePolicy.put("STANDARD_TIME",rs.getString("standard_time"));
				hmEmpOverTimePolicy.put("BUFFER_STANDARD_TIME",rs.getString("buffer_standard_time"));
				hmEmpOverTimePolicy.put("OVERTIME_HOURS",rs.getString("over_time_hrs"));
				hmEmpOverTimePolicy.put("FIXED_OVERTIME_HOURS",rs.getString("fixed_overtime_hrs"));
				hmEmpOverTimePolicy.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
				hmEmpOverTimePolicy.put("ORG_ID", rs.getString("org_id"));
				
				Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(rs.getString("level_id")));
				StringBuilder sbsalaryhead=new StringBuilder("");
				if(rs.getString("salaryhead_id")!=null && !rs.getString("salaryhead_id").equals("")){
					List<String> sHeadList=Arrays.asList(rs.getString("salaryhead_id").split(","));
					int j=0;
					for(int i=0; sHeadList!=null && !sHeadList.isEmpty() && i<sHeadList.size();i++){
						if(sHeadList.get(i)!=null && !sHeadList.get(i).equals("")){
							if(j==0){
								sbsalaryhead.append(hmSalaryHeadsMap.get(sHeadList.get(i).trim()));
							}else{
								sbsalaryhead.append(","+hmSalaryHeadsMap.get(sHeadList.get(i).trim()));
							}
							j++;
						}
					} 					
				}
				
				hmEmpOverTimePolicy.put("SALARY_HEAD_ID", sbsalaryhead.toString());
				
				String strCalBasis = "";
				if(rs.getString("calculation_basis")!=null && rs.getString("calculation_basis").equalsIgnoreCase("FD")){
					strCalBasis = "Daily";
				} else if(rs.getString("calculation_basis")!=null && rs.getString("calculation_basis").equalsIgnoreCase("H")){
					strCalBasis = "Hourly";
				} else if(rs.getString("calculation_basis")!=null && rs.getString("calculation_basis").equalsIgnoreCase("M")){
					strCalBasis = "Minute";
				}				
				hmEmpOverTimePolicy.put("CAL_BASIS", strCalBasis);

				String strRoundOffTime = "";
				if(uF.parseToInt(rs.getString("round_off_time")) == 15){
					strRoundOffTime = "15 Minute";
				} else if(uF.parseToInt(rs.getString("round_off_time")) == 30){
					strRoundOffTime = "30 Minute";
				} else if(uF.parseToInt(rs.getString("round_off_time")) == 45){
					strRoundOffTime = "45 Minute";
				} else if(uF.parseToInt(rs.getString("round_off_time")) == 60){
					strRoundOffTime = "1 Hour";
				}				
				hmEmpOverTimePolicy.put("ROUND_OFF_OVERTIME",strRoundOffTime);
				
				alInner.add(hmEmpOverTimePolicy);
				
				hmEmpOverTimeLevelPolicy.put(rs.getString("level_id")+"_"+rs.getString("overtime_type"), alInner);		
				
				if(rs.getString("calculation_basis") != null && rs.getString("calculation_basis").trim().equalsIgnoreCase("M")){
					if(sbOtId == null){
						 sbOtId = new StringBuffer();
						 sbOtId.append(rs.getString("overtime_id"));
					} else {
						sbOtId.append(","+rs.getString("overtime_id"));
					}
				}
				
			}	
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpOverTimeLevelPolicy", hmEmpOverTimeLevelPolicy);
			
			if(sbOtId !=null && sbOtId.length() > 0){
				pst = con.prepareStatement("select * from overtime_minute_slab where overtime_id in ("+sbOtId.toString()+")");
				rs=pst.executeQuery();
				Map<String, List<Map<String,String>>> hmOvertimeMinuteSlab = new HashMap<String, List<Map<String,String>>>();
				while(rs.next()){
					List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(rs.getString("overtime_id"));
					if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
					
					Map<String,String> hmOvertimeMinute =new HashMap<String, String>();
					hmOvertimeMinute.put("OVERTIME_MINUTE_ID", rs.getString("overtime_minute_id"));
					hmOvertimeMinute.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertimeMinute.put("OVERTIME_MIN_MINUTE", rs.getString("min_minute"));
					hmOvertimeMinute.put("OVERTIME_MAX_MINUTE", rs.getString("max_minute"));
					hmOvertimeMinute.put("ROUNDOFF_MINUTE", rs.getString("roundoff_minute"));	
					
					alOtMinute.add(hmOvertimeMinute);
					
					hmOvertimeMinuteSlab.put(rs.getString("overtime_id"), alOtMinute);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmOvertimeMinuteSlab", hmOvertimeMinuteSlab);
			}
			
		} catch (Exception e) {
			e.printStackTrace(); 
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
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

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
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
