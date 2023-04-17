package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.common.ViewCompanyManual;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewEventFilePopup extends ActionSupport implements IStatements,ServletRequestAware, ServletResponseAware 
{
  
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strOrgId;
	
	private static Logger log = Logger.getLogger(ViewCompanyManual.class);
	
	private String strEventId;
	private String pageFrom;
		
	public ViewEventFilePopup(HttpServletRequest request,CommonFunctions CF, String strOrgId) {
		super();
		this.request = request;
		this.CF = CF;
		this.strOrgId = strOrgId;
		
	}
	
	public ViewEventFilePopup() {
		
	}
	public String execute() throws Exception {
		session = request.getSession(true);
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
	
		strOrgId = (String)session.getAttribute(ORGID);
				
		request.setAttribute(PAGE, "/jsp/reports/ViewEventFilePopup.jsp");
		request.setAttribute(TITLE, "View Event File");
			
		
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("eventId==>"+getStrEventId());
		viewEventFile(uF);
		if(getPageFrom() != null && getPageFrom().equalsIgnoreCase("EP")) {
			return VIEW;
		}
		return LOAD;
	}
	
	
	private void viewEventFile(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			
			con = db.makeConnection(con);
			if(uF.parseToInt(getStrEventId()) > 0) {
				pst = con.prepareStatement("select * from events where event_id = ?");
				pst.setInt(1, uF.parseToInt(getStrEventId()));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);				
				while(rs.next()) {
					String extenstion = null;
					if(rs.getString("event_image") !=null && !rs.getString("event_image").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("event_image").trim());
					}
										
					String eventImgPath = "";
					if(rs.getString("event_image")!=null && !rs.getString("event_image").equals("")){
						if(CF.getStrDocSaveLocation()==null){
							eventImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("event_image") ;
						} else {
							eventImgPath = CF.getStrDocRetriveLocation()+I_EVENTS+"/"+rs.getString("added_by")+"/"+rs.getString("event_image");
	
						}
					}
					List<String> availableExt = CF.getAvailableExtention();
					request.setAttribute("availableExt", availableExt);
					request.setAttribute("eventFile",uF.showData(rs.getString("event_image"),"Not Available"));
					request.setAttribute("extention",extenstion);
					request.setAttribute("eventImgPath",eventImgPath);
				}
				rs.close();
				pst.close();
			} 
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
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

	public String getStrEventId() {
		return strEventId;
	}

	public void setStrEventId(String strEventId) {
		this.strEventId = strEventId;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}
}