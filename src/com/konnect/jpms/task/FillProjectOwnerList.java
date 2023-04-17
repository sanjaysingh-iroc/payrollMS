package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillProjectOwnerList implements IStatements{

	String proOwnerId;
	String proOwnerName;

	HttpServletRequest request;
	public FillProjectOwnerList(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillProjectOwnerList() {
		
	}
	
	public FillProjectOwnerList(String proOwnerId, String proOwnerName) {
		this.proOwnerId = proOwnerId;
		this.proOwnerName = proOwnerName;
	}
	
	
//===start parvez date: 17-10-2022===	
	/*public List<FillProjectOwnerList> fillProjectOwner() {
		
		List<FillProjectOwnerList> al = new ArrayList<FillProjectOwnerList>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			
			boolean flagMiddleName = getFeatureStatusForEmpMiddleName();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(p.project_owner) as project_owner, epd.emp_fname,epd.emp_mname, epd.emp_lname from projectmntnc p, employee_personal_details epd " +
					" where p.project_owner = epd.emp_per_id ");
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select distinct(p.project_owner) as project_owner, epd.emp_fname,epd.emp_lname from projectmntnc p, employee_personal_details epd where p.project_owner = epd.emp_per_id order by epd.emp_fname");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				if(rs.getInt("project_owner") < 0) {
					continue;
				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				al.add(new FillProjectOwnerList(rs.getString("project_owner"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")));				
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
	}*/
	
	public List<FillProjectOwnerList> fillProjectOwner() {
		
		List<FillProjectOwnerList> al = new ArrayList<FillProjectOwnerList>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			
			boolean flagMiddleName = getFeatureStatusForEmpMiddleName();
			

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select project_owners from projectmntnc ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			List<String> ownersList1 = new ArrayList<String>();
			StringBuilder strProOwnerIds = null;
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				
				if(rs.getString("project_owners") != null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					for(int i=1; i<tempList.size();i++){
						if(uF.parseToInt(tempList.get(i)) < 0){
							continue;
						}
						
						if(!ownersList1.contains(tempList.get(i)) && uF.parseToInt(tempList.get(i))>0){
							ownersList1.add(tempList.get(i));
							if(strProOwnerIds == null){
		    	        		strProOwnerIds = new StringBuilder();
		    	        		strProOwnerIds.append(tempList.get(i));
		    	        	} else {
		    	        		strProOwnerIds.append(","+tempList.get(i));
		    	        	}
						}
					}
				}
								
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT emp_id, emp_fname,emp_mname, emp_lname FROM employee_official_details eod, employee_personal_details epd " +
					" WHERE epd.emp_per_id=eod.emp_id and eod.emp_id in ("+strProOwnerIds+") order by epd.emp_fname");
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if (rs.getInt("emp_id") < 0) {
					continue;
				}
				
				String strEmpMName = "";

				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}

//				hmEmpCodeName.put(rs.getString("emp_id"), rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname"));
				al.add(new FillProjectOwnerList(rs.getString("emp_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")));
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
	
//===end parvez date: 17-10-2022===

	public List<FillProjectOwnerList> fillProjectOwner(String userType, String empId) {
		
		List<FillProjectOwnerList> al = new ArrayList<FillProjectOwnerList>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			
			boolean flagMiddleName = getFeatureStatusForEmpMiddleName();

			
			/*StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(p.project_owner) as project_owner, epd.emp_fname,epd.emp_mname,epd.emp_lname from projectmntnc p, employee_personal_details epd " +
					" where p.project_owner = epd.emp_per_id ");
			if(userType != null && userType.equals(IConstants.CUSTOMER)) {
				sbQuery.append(" and p.poc = "+uF.parseToInt(empId)+" ");
			}
			if(userType != null && userType.equals(IConstants.MANAGER)) {
				sbQuery.append(" and ( p.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+uF.parseToInt(empId)+" ) or p.added_by = "+uF.parseToInt(empId)+" ) ");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select distinct(p.project_owner) as project_owner, epd.emp_fname,epd.emp_lname from projectmntnc p, employee_personal_details epd where p.project_owner = epd.emp_per_id order by epd.emp_fname");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				if(rs.getInt("project_owner") < 0) {
					continue;
				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				al.add(new FillProjectOwnerList(rs.getString("project_owner"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")));				
			}
			rs.close();
			pst.close();*/
			
		//===start parvez date: 17-10-2022===
			List<String> ownersList1 = new ArrayList<String>();
			StringBuilder strProOwnerIds = null;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select project_owners from projectmntnc p where p.pro_id>0 ");
			if(userType != null && userType.equals(IConstants.CUSTOMER)) {
				sbQuery.append(" and p.poc = "+uF.parseToInt(empId)+" ");
			}
			if(userType != null && userType.equals(IConstants.MANAGER)) {
				sbQuery.append(" and ( p.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+uF.parseToInt(empId)+" ) or p.added_by = "+uF.parseToInt(empId)+" ) ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				
				if(rs.getString("project_owners") != null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					for(int i=1; i<tempList.size();i++){
						if(uF.parseToInt(tempList.get(i)) < 0){
							continue;
						}
						
						if(!ownersList1.contains(tempList.get(i)) && uF.parseToInt(tempList.get(i))>0){
							ownersList1.add(tempList.get(i));
							if(strProOwnerIds == null){
		    	        		strProOwnerIds = new StringBuilder();
		    	        		strProOwnerIds.append(tempList.get(i));
		    	        	} else {
		    	        		strProOwnerIds.append(","+tempList.get(i));
		    	        	}
						}
					}
				}
							
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT emp_id, emp_fname,emp_mname, emp_lname FROM employee_official_details eod, employee_personal_details epd " +
					" WHERE epd.emp_per_id=eod.emp_id and eod.emp_id in ("+strProOwnerIds+") order by epd.emp_fname");
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if (rs.getInt("emp_id") < 0) {
					continue;
				}
				
				String strEmpMName = "";

				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				al.add(new FillProjectOwnerList(rs.getString("emp_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")));
			}
			rs.close();
			pst.close();
		//===end parvez date: 17-10-2022===	
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
	
	
	public String getProOwnerId() {
		return proOwnerId;
	}

	public void setProOwnerId(String proOwnerId) {
		this.proOwnerId = proOwnerId;
	}

	public String getProOwnerName() {
		return proOwnerName;
	}

	public void setProOwnerName(String proOwnerName) {
		this.proOwnerName = proOwnerName;
	}

	
}