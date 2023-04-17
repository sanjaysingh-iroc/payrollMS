package com.konnect.jpms.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class InboxDashboard extends ActionSupport implements ServletRequestAware, IStatements{

private static final long serialVersionUID = 1L;
	
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;
	
	private String strSearchJob;
	private String emailId;
	
	public String execute() {
		
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
		request.setAttribute(PAGE, "/jsp/mail/InboxDashboard.jsp");
		request.setAttribute(TITLE, "Inbox Dashboard");
		
		viewEmailDetails();
		getSearchAutoCompleteData();
		
		return SUCCESS;
	}
	
	private void getSearchAutoCompleteData() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			SortedSet<String> setEmailDataList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from email_details where email_details_id > 0 ");
		    
    	    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and (email_from like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst search===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("email_from")!=null){
					setEmailDataList.add(rs.getString("email_from"));
				}
				
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setEmailDataList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			
			request.setAttribute("sbDataV", sbData.toString());
//			System.out.println("sbDataV==>"+sbData);	
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void viewEmailDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF =new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			List<List<String>> allEmails=new ArrayList<List<String>>();
			StringBuilder strQuery = new StringBuilder();
			
			int i =0;
			strQuery.append("select * from email_details ");
		    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
		    	strQuery.append("where (email_from like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			strQuery.append(" order by email_details_id desc"); 
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
			while (rst.next()) {
				List<String> alinner=new ArrayList<String>();
				alinner.add(rst.getString("email_details_id"));		//0
				alinner.add(rst.getString("email_subject"));		//1
				alinner.add(rst.getString("email_from"));			//2
				alinner.add(rst.getString("email_no"));				//3
				
				if(i == 0) {
					setEmailId(rst.getString("email_details_id"));
				}
				i++;
				allEmails.add(alinner);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("allEmails", allEmails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
}
