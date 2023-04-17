package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillManager implements IStatements{

	String managerId;
	String managerName;
	
	public FillManager(String managerId, String managerName) {
		this.managerId = managerId;
		this.managerName = managerName;
	}
	HttpServletRequest request;
	public FillManager(HttpServletRequest request) {
		this.request = request;
	}
	public FillManager() {
	}
	
	public List<FillManager> fillManager(){
		
		List<FillManager> al = new ArrayList<FillManager>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.emp_per_id from employee_personal_details epd, user_details ud " +
					"where ud.emp_id = epd.emp_per_id and ud.usertype_id = 2");
			rs = pst.executeQuery();
			while(rs.next()){
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				al.add(new FillManager(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")));				
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
	
	public Boolean getFeatureStatusForEmpMiddleName() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select feature_name,feature_status,user_type_id,emp_ids from feature_management where feature_name=?");
			pst.setString(1, F_SHOW_EMPLOYEE_MIDDLE_NAME);
			rst = pst.executeQuery();
			while (rst.next()) {
				if(rst.getBoolean("feature_status")) {
					flag = true;
				}
			}
			// System.out.println("scree-"+ScreenShotName);
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	
	
	
//	public List<FillManager> fillManager(int nOrgId){
//		
//		List<FillManager> al = new ArrayList<FillManager>();
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//
//		try {
//
//			con = db.makeConnection(con);
//			if(nOrgId>0){
//				pst = con.prepareStatement(selectLevel1);
//				pst.setInt(1, nOrgId);
//			}else{
//				pst = con.prepareStatement(selectLevel);
//			}
//			
//			rs = pst.executeQuery();
//			while(rs.next()){
//				al.add(new FillManager(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
//			}	
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//		return al;
//	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	


}
