package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReportTrackerFilter extends ActionSupport implements  IStatements,ServletRequestAware{
	
	HttpServletRequest request;
	private HttpSession session;
	CommonFunctions CF;
	String filterType;
	UtilityFunctions uF=new UtilityFunctions();
	public String execute(){
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		  
		if(filterType.equalsIgnoreCase("salary_details"))
		  getSalaryContent();
		else  if(filterType.equalsIgnoreCase("employee_personal_details"))
		  getEmployeeContent();
		  
		  
		return SUCCESS;
		
		
	}
	
	public void getSalaryContent(){
		PreparedStatement pst=null;
		ResultSet rs=null;
		Database db=new Database();
		db.setRequest(request);
		Connection con=null;
		con=db.makeConnection(con);
		try{
			pst=con.prepareStatement("select * from "+filterType);
			rs=pst.executeQuery();
			Map<String,String> mp=new HashMap<String,String>();
			while(rs.next()){
				mp.put(rs.getString("salary_head_id"),rs.getString("salary_head_name"));
			}
            rs.close();
            pst.close();
			request.setAttribute("mp",mp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getEmployeeContent(){
		PreparedStatement pst=null;
		ResultSet rs=null;
		Database db=new Database();
		db.setRequest(request);
		Connection con=null;
		con=db.makeConnection(con);
		try{
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst=con.prepareStatement("select * from "+filterType);
			rs=pst.executeQuery();
			Map<String,String> mp=new HashMap<String,String>();
			while(rs.next()){
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				mp.put(rs.getString("emp_per_id"),rs.getString("emp_fname")+strEmpMName+" " +rs.getString("emp_lname"));
			}
            rs.close();
            pst.close();
			request.setAttribute("mp",mp);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getFilterType() {
		return filterType;
	}
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
	  
}
