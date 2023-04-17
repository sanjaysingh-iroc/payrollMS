package com.konnect.jpms.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReceivedEMailNotification extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId = null;
	
	String nEmailId;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/mail/ReceivedEMailNotification.jsp");
		request.setAttribute(TITLE, "Emails");
		
		viewInboxEmail(uF);
		
		return LOAD;
	}
	
	
	public void viewInboxEmail(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from email_details where email_details_id=?");
			pst.setInt(1, uF.parseToInt(getnEmailId()));
			rs = pst.executeQuery();
			List<String> alInboxData = new ArrayList<String>();
			while (rs.next()) {
				alInboxData.add(rs.getString("email_details_id"));			//0
				alInboxData.add(rs.getString("email_no"));					//1
				alInboxData.add(uF.getDateFormat(rs.getString("email_received_date"), DBTIMESTAMP, DBTIMESTAMP_STR));		//2
				alInboxData.add(rs.getString("email_from"));				//3
				alInboxData.add(rs.getString("email_cc"));					//4
				alInboxData.add(rs.getString("email_subject"));				//5
				alInboxData.add(rs.getString("email_body"));				//6
//				alInboxData.add(rs.getString("email_attachment"));			//7
				if(rs.getString("email_attachment") != null){
					String[] arr = rs.getString("email_attachment").split(",");
					StringBuilder sb = new StringBuilder();
					sb.append("<div style=\"float:left; width:50%\">");
					for(int k=0; k<arr.length; k++){
						if(CF.getStrDocRetriveLocation()==null) {
							sb.append("<a href=\""+request.getContextPath()+DOCUMENT_LOCATION +arr[k]+"\" target=\"_blank\" class=\"viewattach\" title=\""+arr[k]+"\"></a>&nbsp;&nbsp;");
						}else{
							sb.append("<a href=\""+CF.getStrDocRetriveLocation()+ I_INBOX_ATTACHMENT + "/" + I_DOCUMENT + "/"+arr[k]+"\" target=\"_blank\" class=\"viewattach\" title=\""+arr[k]+"\"></a>&nbsp;&nbsp;");
						}
					}
					sb.append("</div>");
					alInboxData.add(sb.toString());			//7
				} else{
					alInboxData.add("");			//7
				}
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alInboxData", alInboxData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getnEmailId() {
		return nEmailId;
	}

	public void setnEmailId(String nEmailId) {
		this.nEmailId = nEmailId;
	}

}
