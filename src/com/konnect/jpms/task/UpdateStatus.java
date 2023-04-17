package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateStatus extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strSessionEmpId;
	String strSessionOrgId;
	HttpSession session;
	CommonFunctions CF; 
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		String strProjectId = (String)request.getParameter("pro_id");
		String strStatus = (String)request.getParameter("status");
		
		updateProjectStatus(strProjectId, strStatus);
		
		return SUCCESS;
	
	}
	
	
	public void updateProjectStatus(String strProjectId, String strStatus) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			if(strStatus!=null && strStatus.equals("n")) {
				pst = con.prepareStatement("update projectmntnc set approve_status = 'n' where pro_id=?");
				pst.setInt(1, uF.parseToInt(strProjectId));
//				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("UPDATE activity_info SET approve_status='n' WHERE pro_id=?");
				pst.setInt(1, uF.parseToInt(strProjectId));
				pst.executeUpdate();
//				System.out.println("pst ===>> " + pst);
				pst.close();
				
				List<String> tlEmpList = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
				pst.setInt(1, uF.parseToInt(strProjectId));
				rs = pst.executeQuery();
				while(rs.next()){
					if(uF.parseToInt(rs.getString("emp_id")) > 0){
						tlEmpList.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmTaskProData = CF.getTaskProInfo(con, null, strProjectId);
				
				if(!tlEmpList.contains(hmTaskProData.get("PROJECT_OWNER_ID"))) {
					tlEmpList.add(hmTaskProData.get("PROJECT_OWNER_ID"));
				}
				
//				System.out.println("tlEmpList ===>> " + tlEmpList);
				for(int i=0; tlEmpList!=null && !tlEmpList.isEmpty() && i<tlEmpList.size(); i++) {
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_PROJECT_RE_OPENED, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId(strSessionOrgId);
					nF.setEmailTemplate(true);
//					System.out.println("tlEmpList.get(i) ===>> " + tlEmpList.get(i));
					
					nF.setStrEmpId(tlEmpList.get(i));
					nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.sendNotifications();
				}
				
				if(uF.parseToInt(hmTaskProData.get("PROJECT_SPOC_ID")) > 0) {
					
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_PROJECT_RE_OPENED, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId(strSessionOrgId);
					nF.setEmailTemplate(true);
					
					pst = con.prepareStatement("select * from client_poc where poc_id = ?");
					pst.setInt(1, uF.parseToInt(hmTaskProData.get("PROJECT_SPOC_ID")));
					rs = pst.executeQuery();
					boolean flg=false;
					while(rs.next()) {
						nF.setStrEmpFname(rs.getString("contact_fname"));
						nF.setStrEmpLname(rs.getString("contact_lname"));
						nF.setStrEmpMobileNo(rs.getString("contact_number"));
						if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("contact_email"));
							nF.setStrEmailTo(rs.getString("contact_email"));
						}
						flg = true;
					}
					rs.close();
					pst.close();
					
					if(flg) {
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						nF.sendNotifications(); 
					}
				}
				
				request.setAttribute("STATUS_MSG", "Status Updated.");
//				System.out.println("pst==>"+pst);
			}
			
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}