package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetDepartmentList  extends ActionSupport implements ServletRequestAware{

	private String strOrg;
	private String type;
	
	private List<FillDepartment> departList;

	private static final long serialVersionUID = 1L;

	public String execute() {
		
		
		if(getType() !=null && getType().equals("org")){
			departList=getDepartmentList();
		}
		
		
		return SUCCESS;

	}
	
	private List<FillDepartment> getDepartmentList() {
		
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT * FROM department_info WHERE dept_id is not null ");
			if(getStrOrg()!=null && !getStrOrg().equals("")){
				sbQuery.append(" and org_id in ("+getStrOrg()+")");
			}
			sbQuery.append(" order by dept_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillDepartment(rs.getString("dept_id"), rs.getString("dept_name")));				
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FillDepartment> getDepartList() {
		return departList;
	}

	public void setDepartList(List<FillDepartment> departList) {
		this.departList = departList;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;

	}
}
