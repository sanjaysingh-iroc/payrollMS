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
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class WeeklyOffPolicy extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(WeeklyOffPolicy.class);
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String strOrg;
	String strLocation;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> workList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, "/jsp/reports/master/WeeklyOffPolicy.jsp");
		request.setAttribute(TITLE, "Weekly Off Policy");
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
			workList = new FillWLocation(request).fillWLocation(getStrOrg(), (String)session.getAttribute(WLOCATION_ACCESS));
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
			workList = new FillWLocation(request).fillWLocation(getStrOrg());
		}
		
		if(uF.parseToInt(getStrLocation()) == 0 && workList!=null && workList.size()>0){
			setStrLocation(workList.get(0).getwLocationId());
		}
		
		viewLevel(uF);
		getSelectedFilter(uF);
		return LOAD;
 
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
		
		alFilter.add("LOCATION");
		if(getStrLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;workList!=null && i<workList.size();i++) {
				if(getStrLocation().equals(workList.get(i).getwLocationId())) {
					if(k==0) {
						strLocation=workList.get(i).getwLocationName();
					} else {
						strLocation+=", "+workList.get(i).getwLocationName();
					}
					k++;
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "-");
			}
		} else {
			hmFilter.put("LOCATION", "-");
		}
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewLevel(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
//			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con, uF.parseToInt(getStrOrg()));
			
			Map<String, Map<String, String>> hmWorkLocation = new HashMap<String, Map<String, String>>();
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from work_location_info where org_id=? ");
//			if(uF.parseToInt(getStrLocation()) > 0){
//				sbQuery.append("and wlocation_id="+uF.parseToInt(getStrLocation()));
//			}
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from work_location_info where wlocation_id>0 ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getStrLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getStrLocation()));
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("WL_ID", rs.getString("wlocation_id"));
				hm.put("WL_NAME", rs.getString("wlocation_name"));
				hm.put("WL_CODE", rs.getString("wloacation_code"));
				hm.put("WL_PINCODE", rs.getString("wlocation_pincode"));
				hm.put("WL_ADDRESS", rs.getString("wlocation_address"));
				hm.put("WL_CITY", rs.getString("wlocation_city"));
				hm.put("WL_CONTACT_NO", rs.getString("wlocation_contactno"));
				hm.put("WL_FAX_NO", rs.getString("wlocation_faxno"));

				hm.put("WL_PAN_NO", rs.getString("wlocation_pan_no"));
				hm.put("WL_TAN_NO", rs.getString("wlocation_tan_no"));
				hm.put("WL_REG_NO", rs.getString("wlocation_reg_no"));
				hm.put("WL_ECC1_NO", rs.getString("wlocation_ecc_code_1"));
				hm.put("WL_ECC2_NO", rs.getString("wlocation_ecc_code_2"));

				hm.put("WL_START_TIME", rs.getString("wlocation_start_time"));
				hm.put("WL_END_TIME", rs.getString("wlocation_end_time"));
				
				hm.put("WL_WEEKLY_OFF_1", rs.getString("wlocation_weeklyoff1"));
				String wlocation_weeklyofftype1 = rs.getString("wlocation_weeklyofftype1")!=null && !rs.getString("wlocation_weeklyofftype1").equals("") ? rs.getString("wlocation_weeklyofftype1").equals("HD") ? "Half Day" : "Full Day" : "";
				hm.put("WL_WEEKLY_OFF_TYPE_1", wlocation_weeklyofftype1);
				String wlocation_weeknos1 = "";
				if(rs.getString("wlocation_weeknos1")!=null && !rs.getString("wlocation_weeknos1").equals("")){
					String[] arr = rs.getString("wlocation_weeknos1").split(",");
					for(int i = 0; i < arr.length; i++){
						if(i==0){
							wlocation_weeknos1 = uF.getDigitPosition(uF.parseToInt(arr[i]));
						} else {
							wlocation_weeknos1 += ","+uF.getDigitPosition(uF.parseToInt(arr[i]));
						}
					}
				}
				hm.put("WL_WEEK_No_1", wlocation_weeknos1);
				
				hm.put("WL_WEEKLY_OFF_2", rs.getString("wlocation_weeklyoff2"));
				String wlocation_weeklyofftype2 = rs.getString("wlocation_weeklyofftype2")!=null && !rs.getString("wlocation_weeklyofftype2").equals("") ? rs.getString("wlocation_weeklyofftype2").equals("HD") ? "Half Day" : "Full Day" : "";
				hm.put("WL_WEEKLY_OFF_TYPE_2", wlocation_weeklyofftype2);
				String wlocation_weeknos2 = "";
				if(rs.getString("wlocation_weeknos2")!=null && !rs.getString("wlocation_weeknos2").equals("")){
					String[] arr = rs.getString("wlocation_weeknos2").split(",");
					for(int i = 0; i < arr.length; i++){
						if(i==0){
							wlocation_weeknos2 = uF.getDigitPosition(uF.parseToInt(arr[i]));
						} else {
							wlocation_weeknos2 += ","+uF.getDigitPosition(uF.parseToInt(arr[i]));
						}
					}
				}
				hm.put("WL_WEEK_No_2", wlocation_weeknos2);
				
				hm.put("WL_WEEKLY_OFF_3", rs.getString("wlocation_weeklyoff3"));
				String wlocation_weeklyofftype3 = rs.getString("wlocation_weeklyofftype3")!=null && !rs.getString("wlocation_weeklyofftype3").equals("") ? rs.getString("wlocation_weeklyofftype3").equals("HD") ? "Half Day" : "Full Day" : "";
				hm.put("WL_WEEKLY_OFF_TYPE_3", wlocation_weeklyofftype3);
				String wlocation_weeknos3 = "";
				if(rs.getString("wlocation_weeknos3")!=null && !rs.getString("wlocation_weeknos3").equals("")){
					String[] arr = rs.getString("wlocation_weeknos3").split(",");
					for(int i = 0; i < arr.length; i++){
						if(i==0){
							wlocation_weeknos3 = uF.getDigitPosition(uF.parseToInt(arr[i]));
						} else {
							wlocation_weeknos3 += ","+uF.getDigitPosition(uF.parseToInt(arr[i]));
						}
					}
				}
				hm.put("WL_WEEK_No_3", wlocation_weeknos3);
				
				hm.put("WL_BOIMETRIC_INFO", rs.getString("biometric_info"));   
				hm.put("WL_PT_REG_NO", rs.getString("wlocation_ptreg_no"));
				
				hmWorkLocation.put(rs.getString("wlocation_id"), hm);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmWorkLocation", hmWorkLocation);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
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
