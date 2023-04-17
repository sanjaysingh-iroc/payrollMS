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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class IncrementReportDA extends ActionSupport implements ServletRequestAware, IStatements {
  
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(IncrementReportDA.class);
	
	CommonFunctions CF;
	HttpSession session;
	public String execute() throws Exception {		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, PIncrementDA);
		request.setAttribute(TITLE, TIncrement);	
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
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
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null,  null);
			
			Map hmIncrementReport = new HashMap();
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			
			pst = con.prepareStatement(selectIncrementDA);
			rs = pst.executeQuery();
			
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getInt("increment_id")+"");
				alInner.add(uF.showData(rs.getString("increment_from"),""));
				alInner.add(uF.showData(rs.getString("increment_to"),""));
				alInner.add(uF.showData(rs.getString("increment_amount"),""));
				
				if("P".equalsIgnoreCase(rs.getString("increment_amount_type"))){
					alInner.add("%");
				}else{
					alInner.add("Fixed Amount");
				}
				
				
				String []arrMonth = rs.getString("due_month").split(",");
				StringBuilder sb = new StringBuilder();
				
				for(int i=0; arrMonth!=null && i< arrMonth.length; i++){
					sb.append(uF.getMonth(uF.parseToInt(arrMonth[i]))+"");
					if(i<arrMonth.length-1){
						sb.append(", ");
					}
				}
				alInner.add(sb.toString());
				
				
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

}
