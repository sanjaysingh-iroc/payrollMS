package com.konnect.jpms.reports;

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

public class ReportTracker extends ActionSupport implements  IStatements,ServletRequestAware{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5846636523966720273L;
	HttpServletRequest request;
	private HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;
	List<String> checkMe;
	String yourAnswer;
	public List<String> getCheckMe() {
		return checkMe;
	}


	public void setCheckMe(List<String> checkMe) {
		this.checkMe = checkMe;
	}


	public String execute(){
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		  
		request.setAttribute(TITLE, TLeaveCard);
		request.setAttribute(PAGE, "/jsp/reports/ReportTracker.jsp");
		UtilityFunctions uF=new UtilityFunctions();
		if(yourAnswer!=null && uF.parseToInt(yourAnswer)==1)
		emailReport(uF);
		return SUCCESS;
		
		
	}
	

	public void emailReport(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		con=db.makeConnection(con);
		try {
			System.out.println("==="+checkMe);

			System.out.println("==="+checkMe.size());
			
			StringBuilder sb=new StringBuilder(" emp_id,empcode,emp_fname,emp_mname,emp_lname ");
			for(String s:checkMe){
//				String s=checkMe.get(i);
//				if(sb==null)sb=new StringBuilder(s);
//				else
				sb.append(","+s);
			}
				
			StringBuilder sbQuery = new StringBuilder("select "+sb.toString()+" from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id"); 

			
			
			pst=con.prepareStatement(sbQuery.toString());
			List<List<String>> outerList =new ArrayList<List<String>>();
			rs=pst.executeQuery();
			while(rs.next()){
				List<String> innerlist=new ArrayList<String>();
				innerlist.add(rs.getString("emp_id"));
				innerlist.add(rs.getString("empcode"));
				innerlist.add(rs.getString("emp_fname")+" "+rs.getString("emp_mname")+" "+rs.getString("emp_lname"));
				for(String s:checkMe){
					innerlist.add(rs.getString(s));
				}
				
//				innerlist.add(rs.getString("empcode"));
//				innerlist.add(rs.getString("emp_fname")+" "+rs.getString("emp_mname")+" "+rs.getString("emp_lname"));
//				innerlist.add(rs.getString("emp_address1"));
//				innerlist.add(rs.getString("emp_address2"));

				outerList.add(innerlist);
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", outerList);
			request.setAttribute("checkMe", checkMe);
			request.setAttribute("yourAnswer", yourAnswer);


			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	
	public String getYourAnswer() {
		return yourAnswer;
	}


	public void setYourAnswer(String yourAnswer) {
		this.yourAnswer = yourAnswer;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		// TODO Auto-generated method stub
		
	}
	
	  
}
