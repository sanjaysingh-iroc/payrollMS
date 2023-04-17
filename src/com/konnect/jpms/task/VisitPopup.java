package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class VisitPopup extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = -4179145895739713025L;
	String strUserType=null;
	HttpSession session; 
	CommonFunctions CF;
	
	String strSessionEmpId;
	String strSessionOrgId;
	private String visitId;
	
	public String execute() throws Exception {
	session = request.getSession();
	CF = (CommonFunctions)session.getAttribute(CommonFunctions);
	if(CF==null)return LOGIN;
	
	request.setAttribute(PAGE, "/jsp/task/VisitPopup.jsp");
	strUserType = (String)session.getAttribute(USERTYPE);
	
	UtilityFunctions uF = new UtilityFunctions();
	getVisitById(uF);
	return SUCCESS;
	}
	
	
	private void getVisitById(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			List<String> alInner = new ArrayList<String>();
			
			Map<String,String> hmclientlist =CF.getProjectClientMap(con, uF);
			Map<String,String> hmHrList = getHrDetails(con);
			pst = con.prepareStatement("select * from hr_client_visit_details where visit_id = ?");
			pst.setInt(1, uF.parseToInt(getVisitId()));
		
			rs = pst.executeQuery();
			while(rs.next())
			{
				alInner.add(Integer.toString(rs.getInt("visit_id")));//0
				alInner.add(rs.getString("description"));//1
				alInner.add(uF.getDateFormat(rs.getString("date"), DBDATE, DATE_FORMAT));//2
				alInner.add((uF.getTimeFormat(rs.getString("time"),TIME_FORMAT)).toString());//3
				String clientName = rs.getString("client_name");
				
				String[] clients = null;
				if(clientName!=null && !clientName.equals("")){
					clients = rs.getString("client_name").split(",");
					String clientNames = null;
					for(String client:clients){
						if(client != null || !(client.equals("")) ){
							if(clientNames!=null){
							clientNames = clientNames+","+ hmclientlist.get(client);
							}else{
								clientNames =  hmclientlist.get(client);
							}
						}
					}alInner.add(clientNames);//4
				}else{					
					alInner.add("All clients");//5
				}
				
			String HRName = rs.getString("hr_name");
		
			String[] hrs =  null;
			if(HRName!=null && !(HRName.equals("")) ){
				
				hrs = rs.getString("hr_name").split(",");
				String hrNames = null;
				for(String Hr:hrs){
					if(Hr !=null || !(Hr.equals("")) ){
						if(hrNames != null){
						hrNames = hrNames +","+hmHrList.get(Hr);
						}else{
							hrNames = hmHrList.get(Hr);
						}
							
					}
				}alInner.add(hrNames);//5
			}else{					
				alInner.add("All HR's");//5
			}
			
			}
			rs.close();
			pst.close();
			request.setAttribute("alInner", alInner);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
		
	public Map<String,String> getHrDetails(Connection con)
	{
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		Map<String,String> hmHRDetails = new HashMap<String,String>();
		try{
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id "
					+ "and epd.is_alive= true and ud.emp_id=epd.emp_per_id and (usertype_id = (select user_type_id "
					+ "from user_type where user_type = '" + ADMIN + "') or (usertype_id = (select user_type_id from user_type where user_type = '" + HRMANAGER
					+ "') ");
				sbQuery.append(")) order by epd.emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
		//		System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmHRDetails.put(rs.getString("emp_per_id"),rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				}
				rs.close();
				pst.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return hmHRDetails;
	}
	
	private HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	

}
