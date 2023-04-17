package com.konnect.jpms.common;

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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class MenuNavigationInner extends ActionSupport implements IStatements, ServletRequestAware {

	private String userTypeId;
	private String productType;;
	HttpSession session;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(MenuNavigationInner.class);
	
	private String NN;
	private String toPage;
	private String toTab;
	private String strOrg;
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	private String strCFYear;
	private String strMonth;
	private String strYear;
	private String strFromDate;
	private String strToDate;
	
	String strUserType = null;
	private String strNavigationId;
	private String callFrom;
	
	public String execute() throws Exception {
		
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		userTypeId = (String)session.getAttribute(USERTYPEID);
		productType = (String)session.getAttribute(PRODUCT_TYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(productType) == 3) {
			userTypeId = (String)session.getAttribute(BASEUSERTYPEID);
			strUserType = (String) session.getAttribute(BASEUSERTYPE);
		}
		String strParentId = getNN();
		
		request.setAttribute(PAGE, PMenuNavigationInner);
		request.setAttribute(TITLE, "Reports");
		if(uF.parseToInt(productType) == 3){
			if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(RECRUITER)
					&& !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(ACCOUNTANT) && !strUserType.equalsIgnoreCase(MANAGER)
					&& !strUserType.equalsIgnoreCase(HOD) && !strUserType.equalsIgnoreCase(CEO))) {
				 	
					request.setAttribute(PAGE, PAccessDenied);
					request.setAttribute(TITLE, TAccessDenied);
					return ACCESS_DENIED;
				}
		} else{
		//===start parvez date: 09-02-2023===	
			if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(RECRUITER)
					&& !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(ACCOUNTANT) && !strUserType.equalsIgnoreCase(OTHER_HR))) {
				 	
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}
		//===end parvez date: 09-02-2023===	
		}
		
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		List<Map<String, String>> al = new ArrayList<Map<String, String>>();
		
		try {

			con = db.makeConnection(con);
			/*pst = con.prepareStatement(selectNavigationInner); 
			pst.setInt(1, uF.parseToInt(strParentId));
			rs = pst.executeQuery();*/
			
			pst = con.prepareStatement(selectNavigationInnerTitle);
			pst.setInt(1, uF.parseToInt(strParentId));
			rs = pst.executeQuery();
			String strTitle = null;
			while(rs.next()) {
				strTitle = rs.getString("_label");
				if(strTitle != null && !strTitle.equals("")) {
					strTitle = strTitle.replace("'", "::");
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("callFrom ===>> " + callFrom);
			pst = con.prepareStatement("select * from navigation_1 where parent = ? and _exist = 1 and user_type_id like '%,"+userTypeId+",%' order by weight"); 
			pst.setInt(1, uF.parseToInt(strParentId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hm = new HashMap<String, String>();
			String firstNaviLbl = null;
			String firstNaviAction = null;
			String firstNaviParentId = null;
//		System.out.println("strNavigationId ===>> " + getStrNavigationId());
		int count = 0;//Created By Dattatray Date:28-09-21 Note : Count declared
			while(rs.next()) {
				hm = new HashMap<String, String>();
				
				hm.put("LABEL", rs.getString("_label"));
//				hm.put("ACTION", rs.getString("_action"));
				String naviLbl1 = rs.getString("_label") != null ? rs.getString("_label") : "";
				if(!naviLbl1.equals("")) {
					naviLbl1 = naviLbl1.replaceAll("'", "::");
				}
								
				hm.put("ACTION", "getPageOnAction('"+rs.getString("_action")+"','"+strParentId+"','"+strTitle+"','"+naviLbl1+"','"+count+"')");//Created By Dattatray Date:28-09-21 Note : Count used
				hm.put("DESC", rs.getString("link_desc"));
				hm.put("LABEL_CODE", rs.getString("_label_code"));
				hm.put("NAVI_ID", rs.getString("navigation_id"));
				if(callFrom != null && callFrom.equals("FactQuickLinkViewSalarySlip")) {
					if(rs.getInt("navigation_id") == 731 || rs.getInt("navigation_id") == 1131) {
						firstNaviLbl = rs.getString("_label") != null ? rs.getString("_label") : "";
						if(!firstNaviLbl.equals("")) {
							firstNaviLbl = firstNaviLbl.replaceAll("'", "::");
						}
						
						firstNaviAction = rs.getString("_action");
						firstNaviParentId = strParentId;
					}
				} else if(firstNaviLbl == null || (uF.parseToInt(getStrNavigationId())>0 && uF.parseToInt(getStrNavigationId()) == rs.getInt("navigation_id")) ) {
//					System.out.println("navigation_id -->> "+rs.getInt("navigation_id"));
					firstNaviLbl = rs.getString("_label") != null ? rs.getString("_label") : "";
					if(!firstNaviLbl.equals("")) {
						firstNaviLbl = firstNaviLbl.replaceAll("'", "::");
					}
					
					firstNaviAction = rs.getString("_action");
					firstNaviParentId = strParentId;
				}
				al.add(hm);
//				System.out.println("hm ===>> " + hm);
				count++;//Created By Dattatray Date:28-09-21 Note : Count increment
			}
			rs.close();
			pst.close();
			
//			System.out.println("firstNaviLbl =====>> " + firstNaviLbl);
//			System.out.println("firstNaviAction =====>> " + firstNaviAction);
//			System.out.println("firstNaviParentId =====>> " + firstNaviParentId);
//			System.out.println("strTitle =====>> " + strTitle);
			
			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-th-large\"></i><a href=\"MenuNavigationInner.action?NN=1109\" style=\"color: #3c8dbc;\"> Reports</a></li>" +
					"<li><a href=\"MenuNavigationInner.action?NN="+strParentId+"\" style=\"color: #3c8dbc;\">"+strTitle+"</a></li>" +
					"<li class=\"active\">"+firstNaviLbl+"</li>");
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
			
			request.setAttribute("firstNaviLbl", firstNaviLbl);
			request.setAttribute("firstNaviAction", firstNaviAction);
			request.setAttribute("firstNaviParentId", firstNaviParentId);
			request.setAttribute("strTitle", strTitle);

		} catch(Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		request.setAttribute("alLinks", al);
		
		return loadNavigationInner();
	}

	private String loadNavigationInner() {
		return LOAD;
	}
	
	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getToTab() {
		return toTab;
	}

	public void setToTab(String toTab) {
		this.toTab = toTab;
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

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrCFYear() {
		return strCFYear;
	}

	public void setStrCFYear(String strCFYear) {
		this.strCFYear = strCFYear;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public String getStrFromDate() {
		return strFromDate;
	}

	public void setStrFromDate(String strFromDate) {
		this.strFromDate = strFromDate;
	}

	public String getStrToDate() {
		return strToDate;
	}

	public void setStrToDate(String strToDate) {
		this.strToDate = strToDate;
	}

	public String getNN() {
		return NN;
	}

	public void setNN(String nN) {
		NN = nN;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getStrNavigationId() {
		return strNavigationId;
	}

	public void setStrNavigationId(String strNavigationId) {
		this.strNavigationId = strNavigationId;
	}


	private HttpServletRequest request;
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}