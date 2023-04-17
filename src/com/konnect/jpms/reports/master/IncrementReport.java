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
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class IncrementReport extends ActionSupport implements ServletRequestAware, IStatements {
  
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(IncrementReport.class);
	
	List<FillOrganisation> orgList;
	String strOrg;
	CommonFunctions CF;
	HttpSession session;
	String strUserType;
	
	public String execute() throws Exception {		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, PIncrement);
		request.setAttribute(TITLE, TIncrement);	
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
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
		
		viewIncrement(uF);			
		return loadIncrement(); 
	}
	 
	public String loadIncrement(){
		
		return LOAD;
	}
	
	public String viewIncrement(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null,null);
			
			Map hmIncrementReport = new HashMap();
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			
			pst = con.prepareStatement(selectIncrement);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getInt("increment_id")+"");
				alInner.add(uF.showData(rs.getString("increment_from"),""));
				alInner.add(uF.showData(rs.getString("increment_to"),""));
				alInner.add(uF.showData(rs.getString("increment_amount"),""));
				alInner.add(uF.getMonth(rs.getInt("due_month")));
				alInner.add(hmEmpName.get(rs.getString("user_id")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				al.add(alInner);
				
				
				hmIncrementReport.put(rs.getString("increment_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			request.setAttribute("hmIncrementReport", hmIncrementReport);
			
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

}
