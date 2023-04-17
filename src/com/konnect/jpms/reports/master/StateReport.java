package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class StateReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	List<FillCountry> countryList;
	private static Logger log = Logger.getLogger(StateReport.class);
	
	public String execute() throws Exception {
				
		request.setAttribute(PAGE, PReportState);
		request.setAttribute(TITLE, TViewState);
		viewState();			
		return loadState(); 
	}
	
	public String loadState(){	
		countryList = new FillCountry(request).fillCountry();
		request.setAttribute("countryList", countryList);
		int countryId, i=0;
		String countryName;
		
		StringBuilder sbCountryList = new StringBuilder();
		sbCountryList.append("{");
		for(i=0; i<countryList.size()-1;i++ ) {
    		countryId = Integer.parseInt((countryList.get(i)).getCountryId());
    		countryName = countryList.get(i).getCountryName();
    		sbCountryList.append("\""+ countryId+"\":\""+countryName+"\",");
		}
		countryId = Integer.parseInt((countryList.get(i)).getCountryId());
		countryName = countryList.get(i).getCountryName();
		sbCountryList.append("\""+ countryId+"\":\""+countryName+"\"");	//no comma for last record
		sbCountryList.append("}");
		request.setAttribute("sbCountryList", sbCountryList.toString());
		
		return LOAD;
	}
	
	public String viewState(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();

		try {
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectStateR);
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(Integer.toString(rs.getInt("state_id")));
				alInner.add(uF.showData(rs.getString("state_name"),""));
				alInner.add(uF.showData(rs.getString("country_name"),""));
//				alInner.add("<a href="+request.getContextPath()+"/AddState.action?E="+rs.getString("state_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/AddState.action?D="+rs.getString("state_id")+">Delete</a>");
				al.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
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
