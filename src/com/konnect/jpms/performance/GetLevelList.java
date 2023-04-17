package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.util.Database;
import com.opensymphony.xwork2.ActionSupport;

public class GetLevelList extends ActionSupport implements ServletRequestAware{

	private String strOrg;
	private String type;
	private List<FillLevel> levelList;

	private static final long serialVersionUID = 1L;

	public String execute() {
		
		if(getType() !=null && getType().equals("org")){
			levelList=getLevelsList();
		}
//		else if(getType() !=null && getType().equals("wlocation")){
//			levelList=getLevelsListByWLocation();
//		}
		
		
		return SUCCESS;

	}
	
//	private List<FillLevel> getLevelsListByWLocation() {
//		
//		List<FillLevel> al = new ArrayList<FillLevel>();
//		Connection con = null;
//		PreparedStatement pst = null;else if(getType() !=null && getType().equals("wlocation")){
//	levelList=getLevelsListByWLocation();
//	
//		ResultSet rs = null;
//		Database db = new Database();
//		
//		try {
////			System.out.println("getStrOrg =======>"+getStrOrg());
////			System.out.println("getStrOrg type=======>"+getType());
//			con = db.makeConnection(con);
//			StringBuilder sbQuery=new StringBuilder();
//			sbQuery.append("SELECT * FROM level_details WHERE level_id is not null ");
//			if(getStrOrg()!=null && !getStrOrg().equals("")){
//				sbQuery.append(" and org_id in ("+getStrOrg()+")");
//			}
//			sbQuery.append(" order by level_name");
//			pst = con.prepareStatement(sbQuery.toString());
//			rs = pst.executeQuery();			
//			while(rs.next()){
//				al.add(new FillLevel(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();			
//		}finally{
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//		
//		return al;
//	}

	private List<FillLevel> getLevelsList() {
		
		List<FillLevel> al = new ArrayList<FillLevel>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
//			System.out.println("getStrOrg =======>"+getStrOrg());
//			System.out.println("getStrOrg type=======>"+getType());
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT * FROM level_details WHERE level_id is not null ");
			if(getStrOrg()!=null && !getStrOrg().equals("")){
				sbQuery.append(" and org_id in ("+getStrOrg()+")");
			}
			sbQuery.append(" order by level_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillLevel(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
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

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;

	}

}
