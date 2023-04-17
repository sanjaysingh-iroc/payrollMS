package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
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

public class HRClientVisit extends ActionSupport implements ServletRequestAware, IStatements {
	
	public HttpSession session;
	public CommonFunctions CF = null;
	UtilityFunctions uF = new UtilityFunctions();
	Map<String,String>hmHRMap = new HashMap<String,String>();
	Map<String,String>hmClientMap = new HashMap<String,String>();
	public String execute() throws Exception { 
		request.setAttribute(PAGE, "/jsp/task/hrClientVisit.jsp");
		request.setAttribute(TITLE, " Client Visit");
		getData();
		loadClientVisits(uF);
		return SUCCESS;
	}
public void getData()
{
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try{
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id and epd.is_alive= true and usertype_id = (select user_type_id from user_type where user_type in('Global HR','HR'))");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			
			while(rs.next())
			{
				hmHRMap.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			rs.close();
			pst.close();
			pst = con.prepareStatement("select * from client_details where isdisabled =false order by client_name");
			rs = pst.executeQuery();
			
			while(rs.next())
			{
				hmClientMap.put(rs.getString("client_id"), rs.getString("client_name"));
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			rs.close();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
			} finally {
				db.closeStatements(pst);
				db.closeConnection(con);
			}
}
	

public void loadClientVisits(UtilityFunctions uF){
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rs = null;
	try{
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
					//Map<String,List<String>> hmClientVisits = new HashMap<String,List<String>>();
			List<List<String>> alClientVisits =  new ArrayList<List<String>>();
			sbQuery.append("select * from hr_client_visit_details");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next())
			{
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("visit_id"));
				String strHrIds = rs.getString("hr_name");
				String strHrId[] = null;
				StringBuilder hr = null;
				if(strHrIds !=null){
							strHrId = strHrIds.split(",");
				}
				if(strHrId != null && strHrId.length > 0){
					for(String hrId : strHrId ){
						if(uF.parseToInt(hrId)>0) {
							if(hr == null) {
								hr = new StringBuilder();
								hr.append(hmHRMap.get(hrId));
							} else {
								hr.append(","+hmHRMap.get(hrId));
							}
						}
					}
				}
				String strClientIds = rs.getString("client_name");
				String strClientId[] = null;
				StringBuilder clients = null;
				if(strClientIds !=null)	{
					strClientId = strClientIds.split(",");
				}
				if(strClientId !=null && strClientId.length > 0){
					for(String clientId : strClientId){
						if(uF.parseToInt(clientId)>0) {
							if(clients == null){
								clients = new StringBuilder();
								clients.append(hmClientMap.get(clientId));
							}else{
								clients.append(","+hmClientMap.get(clientId));
							}
						 }
					 }
				}
				alInner.add(hr.toString());
				alInner.add(clients.toString());
				alInner.add(rs.getString("description"));
				alInner.add(rs.getString("date"));
				alInner.add(rs.getString("time"));
				alClientVisits.add(alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("alClientVisits",alClientVisits);
					
		}catch (Exception e) {
		e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
