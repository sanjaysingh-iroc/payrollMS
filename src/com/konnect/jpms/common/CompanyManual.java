 package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>
 * Validate a user login.
 * </p>
 */
public class CompanyManual extends ActionSupport implements IStatements,ServletRequestAware, ServletResponseAware {
  
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;   
	
	private List<FillOrganisation> orgList;
	private String strOrg;
	private String pageFrom;
	
	private static Logger log = Logger.getLogger(CompanyManual.class);
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		strUserType = (String)session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PCompanyManual);
		request.setAttribute(TITLE, TCompanyManual);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		
		if(!isView && !getPageFrom().equalsIgnoreCase("MyHub")) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
//		System.out.println("getUserscreen CM.java ===>> " + getUserscreen());
//		System.out.println("getNavigationId CM.java ===>> " + getNavigationId());
//		System.out.println("getToPage CM.java ===>> " + getToPage());
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
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
		
		if(getPageFrom()!=null && getPageFrom().equalsIgnoreCase("MyHub")) {
			viewReportManual(uF);
			return "tab";
		}
		viewReportManual(uF);
		getSelectedFilter(uF);
		
		return LOAD;
  
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORG");
		if(getStrOrg()!=null) {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++) {
				if(getStrOrg().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORG", strOrg);
			} else {
				hmFilter.put("ORG", "All Organizations");
			}
		} else {
			hmFilter.put("ORG", "All Organizations");
		}
		
		String selectedFilter = getSelectedFilter(uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
//		StringBuilder sbFilter=new StringBuilder("<strong>Filter Summary: </strong>");
		StringBuilder sbFilter=new StringBuilder("<span style=\"float: left; margin-right: 5px;\"><i class=\"fa fa-filter\"></i></span>");
		sbFilter.append("<span style=\"float: left; width: 95%\">");
		
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
				sbFilter.append(", ");
			}
			
			if(alFilter.get(i).equals("ORG")) {
				sbFilter.append("<strong>ORG:</strong> ");
				sbFilter.append(hmFilter.get("ORG"));
//			 
			} 
		}
		sbFilter.append("</span>");
		
		return sbFilter.toString();
	}
	
	
	public void viewReportManual(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		List<List<String>> reportList = new ArrayList<List<String>>();
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmOrgName = CF.getOrgName(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from company_manual where manual_id>0");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by _date desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int nCount = 0;
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				
				
				alInner.add(uF.getDateFormat(rs.getString("_date"), DBTIMESTAMP, CF.getStrReportDateFormat())
						+" "+uF.getDateFormat(rs.getString("_date"), DBTIMESTAMP, CF.getStrReportTimeFormat()));//0
				alInner.add(rs.getString("manual_title"));//1
				
				alInner.add(getStaus(rs.getInt("status"), rs.getInt("manual_id"), nCount,rs.getString("org_id")));//2
//				alInner.add("<a href=\"AddCompanyManual.action?E="+rs.getString("manual_id")+"\">Edit</a>");
				alInner.add(rs.getString("manual_id"));//3
				
				alInner.add(hmOrgName.get(rs.getString("org_id")));//4
				 /*String strLive = "<img border=\"0\" style=\"width: 14px; height: 14px;\" src=\"images1/icons/pending.png\" title=\"Draft\">";*/
				String strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Draft\"></i>";
				
				if(rs.getInt("status")== 1){
					/*strLive = "<img border=\"0\" style=\"width: 14px; height: 14px;\" src=\"images1/icons/approved.png\" title=\"Published\">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Published\" title=\"Published\"></i>";
				}else if(rs.getInt("status")== 2){
					/*strLive = "<img border=\"0\" style=\"width: 14px; height: 14px;\" src=\"images1/icons/pullout.png\" title=\"Archived\">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Archived\"></i>";
				}
				alInner.add(strLive);//5
				reportList.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", reportList);
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	/*public String getStaus(int nStatus, int nManualId, int nCount){
		String strStatus = null;
		
		switch(nStatus){
		case -1:
			strStatus = "";
			break;
			
		case 0:
			strStatus = "Draft <a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateManualStatus.action?MID="+nManualId+"')\">Publish this version</a>";
			break;
			
		case 1:
			strStatus = "Published";
			break;
			
		case 2:
			strStatus = "Archived <a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateManualStatus.action?MID="+nManualId+"')\">Publish this version</a>";
			break;
		
		}
		
		return strStatus;
	}*/
	
	public String getStaus(int nStatus, int nManualId, int nCount,String orgId){
		String strStatus = null;
		
		switch(nStatus){
		case -1:
			strStatus = "";
			break;
			
		case 0:
			//strStatus = "Draft <a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"')\">Publish this version</a>";
			strStatus = "Draft <a href=\"UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"&userscreen="+getUserscreen()+"&navigationId="+getNavigationId()+"&toPage="+getToPage()+"\">Publish this version</a>";
			break;
			
		case 1:
			strStatus = "Published <a href=\"UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"&operation=UP&userscreen="+getUserscreen()+"&navigationId="+getNavigationId()+"&toPage="+getToPage()+"\">Unpublish this version</a> ";
			break;
			
		case 2:
			//strStatus = "Archived <a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"')\">Publish this version</a>";
			strStatus = "Archived <a href=\"UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"&userscreen="+getUserscreen()+"&navigationId="+getNavigationId()+"&toPage="+getToPage()+"\">Publish this version</a>";
			break;
		
		}
//		System.out.println("status==>"+strStatus+"==>manualId==>"+nManualId);
		return strStatus;
	}

	
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response= response;
		
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

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
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