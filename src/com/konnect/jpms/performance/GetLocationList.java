package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLocationList extends ActionSupport implements ServletRequestAware{

	private String strOrg;
	private List<FillWLocation> locList;
	private static final long serialVersionUID = 1L;

	public String execute() {
		
		locList=getLocationList();
		return SUCCESS;

	}
	
	private List<FillWLocation> getLocationList() {
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT * FROM work_location_info where wlocation_id is not null ");
			if(getStrOrg()!=null && !getStrOrg().equals("")){
				sbQuery.append(" and org_id in ("+getStrOrg()+")");
			}
			sbQuery.append(" order by wlocation_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillWLocation(rs.getString("wlocation_id"), "["+rs.getString("wloacation_code")+"] "+rs.getString("wlocation_name")+","+rs.getString("wlocation_city")));				
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}

	

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillWLocation> getLocList() {
		return locList;
	}

	public void setLocList(List<FillWLocation> locList) {
		this.locList = locList;
	}



	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;

	}

}
