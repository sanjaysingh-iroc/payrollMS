package com.konnect.jpms.roster;

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

import com.konnect.jpms.reports.master.GradeReport;
import com.konnect.jpms.select.FillColour;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class ShiftRoster extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session; 
	String strUserType = null;
	
	String strOrg;
	List<FillOrganisation> orgList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession(); 
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, "/jsp/roster/ShiftReport.jsp");
		request.setAttribute(TITLE, "Create Shifts");
		UtilityFunctions uF = new UtilityFunctions();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0) {
				setStrOrg(orgList.get(0).getOrgId());
			}
		} else {
			if(uF.parseToInt(getStrOrg()) == 0) {
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		viewShiftRoster(uF);
		viewShiftRosterRules(uF); 
		viewRosterWeeklyOffDetails(uF);
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private String viewShiftRosterRules(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		try {

			List<List<String>> rosterPolicyRulesList = new ArrayList<List<String>>();
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM roster_policy_rules where org_id > 0 ");
			if(uF.parseToInt(getStrOrg())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by roster_policy_rule_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement(selectShift);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			
			Map<String, String> hmShift = CF.getShiftMap(con);
			
			while(rs.next()) {
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("roster_policy_rule_name"));
				alInner.add(rs.getString("roster_policy_rule_id"));
				alInner.add(rs.getString("org_id"));
				rosterPolicyRulesList.add(alInner);
				
				/*StringBuilder sbSentance = new StringBuilder();
				if(rs.getInt("rule_type_id") == 1) {
					String strShift = hmShift.get(rs.getString("shift_id"));
					sbSentance.append("<b>"+uF.showData(strShift, "-")+"</b> can only be allocated for "+uF.showData(rs.getString("no_of_days"), "0")+ " days continuously.");
				} else if(rs.getInt("rule_type_id") == 2) {
					sbSentance.append("An individual can work continuously on one shift for "+uF.showData(rs.getString("no_of_days"), "0")+ " days.");
				} else if(rs.getInt("rule_type_id") == 3) {
					StringBuilder sbShift = null; 
					if(rs.getString("shift_ids") != null && rs.getString("shift_ids").length()>0) {
						List<String> al1 = Arrays.asList(rs.getString("shift_ids").split(","));
						for(int i=0; al1!= null && i<al1.size(); i++) {
							if(uF.parseToInt(al1.get(i))>0) {
								if(sbShift == null) {
									sbShift = new StringBuilder();
									sbShift.append(hmShift.get(al1.get(i)));
								} else {
									sbShift.append(", "+hmShift.get(al1.get(i)));
								}
							}
						}
					}
					if(sbShift == null) {
						sbShift = new StringBuilder();
					}
					sbSentance.append("<b>"+uF.getGender(rs.getString("gender"))+ "</b> can be assigned with <b>"+ sbShift +"</b> shifts.");
				} else if(rs.getInt("rule_type_id") == 4) {
					StringBuilder sbShift = null; 
					if(rs.getString("shift_ids") != null && rs.getString("shift_ids").length()>0) {
						List<String> al1 = Arrays.asList(rs.getString("shift_ids").split(","));
						for(int i=0; al1!= null && i<al1.size(); i++) {
							if(uF.parseToInt(al1.get(i))>0) {
								if(sbShift == null) {
									sbShift = new StringBuilder();
									sbShift.append(hmShift.get(al1.get(i)));
								} else {
									sbShift.append(", "+hmShift.get(al1.get(i)));
								}
							}
						}
					}
					if(sbShift == null) {
						sbShift = new StringBuilder();
					}
					sbSentance.append("<b>"+sbShift+"</b> can be combined.");
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(sbSentance.toString());
				alInner.add(rs.getString("roster_policy_rule_id"));
				alInner.add(rs.getString("org_id"));
				
				rosterPolicyRulesList.add(alInner);
				*/
			}	
			rs.close();
			pst.close();
			
			request.setAttribute("rosterPolicyRulesList", rosterPolicyRulesList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}


	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
//				for(int j=0;j<getF_sbu().length;j++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
//				}
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
	

	
	private void viewRosterWeeklyOffDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_weeklyoff_policy where roster_weeklyoff_id > 1 ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by roster_weeklyoff_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select * from roster_weeklyoff_policy where roster_weeklyoff_id > 1 order by roster_weeklyoff_id desc");
			rs = pst.executeQuery();
			List<Map<String, String>> rosterWeeklyoffList = new ArrayList<Map<String, String>>();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("ROSTER_WEEKLYOFF_ID", rs.getString("roster_weeklyoff_id"));
				hmInner.put("WEEKLYOFF_NAME", rs.getString("weeklyoff_name"));
				
				String weeklyoff_type = rs.getString("weeklyoff_type")!=null && !rs.getString("weeklyoff_type").equals("") ? rs.getString("weeklyoff_type").equals("HD") ? "Half Day" : "Full Day" : "";
				hmInner.put("WEEKLYOFF_TYPE", weeklyoff_type);
				
				String weeklyoff_day = "";
				if(rs.getString("weeklyoff_day")!=null && !rs.getString("weeklyoff_day").equals("")){
					String[] arr = rs.getString("weeklyoff_day").split(",");
					for(int i = 0; i < arr.length; i++){
						if(i==0){
							weeklyoff_day = arr[i];
						} else {
							weeklyoff_day += ","+arr[i];
						}
					}
				}
				hmInner.put("WEEKLYOFF_DAY", weeklyoff_day);
				
				String weeklyoff_weekno = "All";
				if(rs.getString("weeklyoff_weekno")!=null && !rs.getString("weeklyoff_weekno").equals("")){
					String[] arr = rs.getString("weeklyoff_weekno").split(",");
					for(int i = 0; i < arr.length; i++){
						if(i==0){
							weeklyoff_weekno = uF.getDigitPosition(uF.parseToInt(arr[i]));
						} else {
							weeklyoff_weekno += ","+uF.getDigitPosition(uF.parseToInt(arr[i]));
						}
					}
				}
				hmInner.put("WEEKLYOFF_WEEKNO", weeklyoff_weekno);
				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), ""));
				hmInner.put("ORG_ID", rs.getString("org_id"));
				
				rosterWeeklyoffList.add(hmInner);
			}	
			rs.close();
			pst.close();
			
			request.setAttribute("rosterWeeklyoffList", rosterWeeklyoffList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String viewShiftRoster(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM shift_details where shift_id > 1 ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by shift_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement(selectShift);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			StringBuilder sbColour = new StringBuilder();
			while(rs.next()){
				
				if(!(rs.getString("shift_code").equalsIgnoreCase("ST"))){
					long fromTime=uF.getTimeFormat(rs.getString("_from"), DBTIME ).getTime();
					long toTime=uF.getTimeFormat(rs.getString("_to"), DBTIME ).getTime();
					
	
					double timeDiff=Double.parseDouble((uF.getTimeDiffInHoursMins(fromTime,toTime)));
					alInner = new ArrayList<String>();
					alInner.add(rs.getInt("shift_id")+"");
					alInner.add(rs.getString("shift_code"));
					alInner.add(rs.getString("shift_type"));
					alInner.add(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
					alInner.add(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
					alInner.add(uF.getDateFormat(rs.getString("break_start"), DBTIME, CF.getStrReportTimeFormat()));
					alInner.add(uF.getDateFormat(rs.getString("break_end"), DBTIME, CF.getStrReportTimeFormat()));
					alInner.add(timeDiff+"");
					alInner.add("<div style=\"height:10px; background-color:"+rs.getString("colour_code")+"\">");
					alInner.add(rs.getString("org_id"));
					
					al.add(alInner);
				
				}
				
				sbColour.append("'"+rs.getString("colour_code")+"',");
				
				
			}	
			rs.close();
			pst.close();
			
			
			
			
			
			
			request.setAttribute("sbColour", (sbColour !=null && !sbColour.toString().trim().equals("") ? sbColour.substring(0, sbColour.length()-1) : ""));
			
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	

	private HttpServletRequest request;

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
