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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AllClientDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(ClientReport.class);
	
	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception { 
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		 
		request.setAttribute(PAGE, "/jsp/task/AllClientDetails.jsp");
		request.setAttribute(TITLE, "All Client Details");
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		viewClient(uF);
		return LOAD;

	}
	
	
	public String viewClient(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);

		try {

			List<String> alInner = new ArrayList<String>();
			Map<String, String> hmClientIndustries = new HashMap<String, String>();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_industry_details");
			rs = pst.executeQuery();
			while(rs.next()){
				hmClientIndustries.put(rs.getString("industry_id"), rs.getString("industry_name"));
			}
			rs.close();
			pst.close();
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from client_details cd, client_poc cp where cd.client_id = cp.client_id order by client_name");
			rs = pst.executeQuery();
			while(rs.next()) {
				alInner = new ArrayList<String>();
//				alInner.add(uF.showData(rs.getString("client_id"),""));
				alInner.add(uF.showData(rs.getString("client_name"),""));
				alInner.add(uF.showData(rs.getString("client_address"),"")); //1

				String strIndustry = rs.getString("client_industry");
				String []arr=null;
				if(strIndustry!=null) {
					arr = strIndustry.split(",");
				}
				StringBuilder sb = null;;
				for(int i=0; arr!=null && i<arr.length; i++) {
					if(sb == null) {
						sb = new StringBuilder();
						sb.append(uF.showData(hmClientIndustries.get(arr[i]), ""));
					} else {
						sb.append(", " + uF.showData(hmClientIndustries.get(arr[i]), ""));
					}
				}
				
				if(sb == null) {
					sb = new StringBuilder();
				}
				
				alInner.add(sb.toString()); // industry  2
				
				alInner.add(uF.showData(rs.getString("contact_fname"),"")+" "+uF.showData(rs.getString("contact_lname"),"")); //3
				alInner.add(uF.showData(rs.getString("contact_number"),"")); //4
				alInner.add(uF.showData(rs.getString("contact_email"),"")); //5
				alInner.add(uF.showData(rs.getString("contact_desig"),"")); //6
				alInner.add(uF.showData(rs.getString("contact_department"),"")); //7
				alInner.add(uF.showData(rs.getString("contact_location"),"")); //8
				
				reportList.add(alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", reportList);
			
			
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

}
