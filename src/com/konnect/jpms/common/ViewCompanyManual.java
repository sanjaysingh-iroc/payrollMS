package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

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
public class ViewCompanyManual extends ActionSupport implements IStatements,ServletRequestAware, ServletResponseAware {
  
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strOrgId;
	
	private static Logger log = Logger.getLogger(ViewCompanyManual.class);
	
	private String strTitle;
	private String strBody;
	private String strSubmit;
	private String pageFrom;
	
	public ViewCompanyManual(HttpServletRequest request,CommonFunctions CF, String strOrgId) {
		super();
		this.request = request;
		this.CF = CF;
		this.strOrgId = strOrgId;
		
	}
	
	public ViewCompanyManual() {
		
	}
	public String execute() throws Exception {
		session = request.getSession(true);
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
	
		strOrgId = (String)session.getAttribute(ORGID);
		
		String strE = request.getParameter("E");
//		System.out.println("strView==>"+ request.getParameter("strView"));
		
		request.setAttribute(PAGE, PViewCompanyManual);
		request.setAttribute(TITLE, TCompanyManual);
			
		
		viewManual(strE);
//		System.out.println("getPageFrom==>"+getPageFrom());
		if(getPageFrom()!=null && getPageFrom().equalsIgnoreCase("MyHub")) {
			return VIEW;
		}
		return LOAD;
	}
	
	
	public void viewManual(String strE) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		List<List<String>> reportList = new ArrayList<List<String>>();
		try{
			
			con = db.makeConnection(con);
//			Map<String, String> hmOrgName = CF.getOrgName(con);
			if(uF.parseToInt(strE) > 0) {
				pst = con.prepareStatement("select * from company_manual where manual_id = ?");
				pst.setInt(1, uF.parseToInt(strE));
				rs = pst.executeQuery();
//				System.out.println("VCM/99---pst ===>> " + pst);				
				while(rs.next()) {
					String extenstion = null;
					if(rs.getString("manual_doc") !=null && !rs.getString("manual_doc").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("manual_doc").trim());
					}
					
					String manualDocPath = "";
					if(rs.getString("manual_doc")!=null && !rs.getString("manual_doc").equals("")){
						if(CF.getStrDocSaveLocation()==null){
							manualDocPath =  DOCUMENT_LOCATION +"/"+rs.getString("manual_id")+"/"+rs.getString("manual_doc"); //+"/"+rs.getString("emp_id")
						} else {
							manualDocPath = CF.getStrDocRetriveLocation()+I_COMPANY_MANUAL+"/"+rs.getString("manual_id")+"/"+rs.getString("manual_doc"); //+"/"+rs.getString("emp_id")
	
						}
					}
					List<String> availableExt = CF.getAvailableExtention();
					request.setAttribute("availableExt", availableExt);
					
					request.setAttribute("extention",extenstion);
					request.setAttribute("manualDocPath",manualDocPath);
					request.setAttribute("TITLE", rs.getString("manual_title"));
					request.setAttribute("BODY", rs.getString("manual_body"));
					request.setAttribute("MANUAL_ID", rs.getString("manual_id"));
					request.setAttribute("DATE", uF.getDateFormat(rs.getString("_date"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				}
				rs.close();
				pst.close();
			} else {
				pst = con.prepareStatement("select * from company_manual where status = 1 and org_id = ? order by _date desc limit 1");
				pst.setInt(1, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
//				System.out.println("VCM/131--else pst ===>> " + pst);
				while(rs.next()) {
					String extenstion = null;
					if(rs.getString("manual_doc") !=null && !rs.getString("manual_doc").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("manual_doc").trim());
					}
					
					String manualDocPath = "";
					if(rs.getString("manual_doc")!=null && !rs.getString("manual_doc").equals("")){
						if(CF.getStrDocSaveLocation()==null){
							manualDocPath =  DOCUMENT_LOCATION +"/"+rs.getString("manual_id")+"/"+rs.getString("manual_doc"); //+"/"+rs.getString("emp_id")
						} else {
							manualDocPath = CF.getStrDocRetriveLocation()+I_COMPANY_MANUAL+"/"+rs.getString("manual_id")+"/"+rs.getString("manual_doc"); //+"/"+rs.getString("emp_id")
	
						}
					}
					List<String> availableExt = CF.getAvailableExtention();
					request.setAttribute("availableExt", availableExt);
					
					request.setAttribute("extention",extenstion);
					request.setAttribute("manualDocPath",manualDocPath);
					request.setAttribute("MANUAL_ID", rs.getString("manual_id"));
					request.setAttribute("TITLE", rs.getString("manual_title"));
					request.setAttribute("BODY", rs.getString("manual_body"));
					request.setAttribute("DATE", uF.getDateFormat(rs.getString("_date"), DBTIMESTAMP, CF.getStrReportDateFormat()));
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
	public String getStrTitle() {
		return strTitle;
	}
	public void setStrTitle(String strTitle) {
		this.strTitle = strTitle;
	}
	public String getStrBody() {
		return strBody;
	}
	public void setStrBody(String strBody) {
		this.strBody = strBody;
	}
	public String getStrSubmit() {
		return strSubmit;
	}
	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	
	
	
}