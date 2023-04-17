package com.konnect.jpms.task;


	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.util.ArrayList;
	import java.util.List;

import javax.servlet.http.HttpServletRequest;

	import com.konnect.jpms.util.Database;
	import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

	public class FillTaskEmpList  implements IStatements{

		String TaskEmployeeId;
		String TaskEmployeeName;
	
		HttpServletRequest request;
		public FillTaskEmpList(HttpServletRequest request) {
			this.request = request;
		}
		
		public FillTaskEmpList() {
		}
		
		public FillTaskEmpList(String TaskEmployeeId, String TaskEmployeeName) {
			this.TaskEmployeeId = TaskEmployeeId;
			this.TaskEmployeeName = TaskEmployeeName;
		}
		
		
		

		public List<FillTaskEmpList> fillTeamLeadName(String pro_id){
			
			List<FillTaskEmpList> al = new ArrayList<FillTaskEmpList>();
			String selectEmployeeByShift = "select * from project_emp_details where pro_id=? and _isteamlead = true";
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rsEmpName = null;
			Database db = new Database();
			db.setRequest(request);
			try {
				
				con = db.makeConnection(con);
				boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

				pst = con.prepareStatement(selectEmployeeByShift);
				pst.setString(1, pro_id);
				rsEmpName = pst.executeQuery();
				while(rsEmpName.next()){
					if(rsEmpName.getInt("emp_per_id")<0){
						continue;
					}
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpName.getString("emp_mname");
						}
					}
				
					al.add(new FillTaskEmpList(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname")+strEmpMName+" "+rsEmpName.getString("emp_lname")));				
				}	
				rsEmpName.close();
				pst.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rsEmpName);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			
			return al;
		}
		
public List<FillTaskEmpList> fillEmployeeName(int pro_id){
	
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rsEmpName = null;
			Database db = new Database();
			db.setRequest(request);
			
			List<FillTaskEmpList> al = new ArrayList<FillTaskEmpList>();
			
			try {
				
				con = db.makeConnection(con);
				boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

				pst = con.prepareStatement("select * from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and is_alive = true and pro_id=? and _isteamlead = true");
				pst.setInt(1, pro_id);
				rsEmpName = pst.executeQuery();
				while(rsEmpName.next()){
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpName.getString("emp_mname");
						}
					}
				
					al.add(new FillTaskEmpList(rsEmpName.getString("emp_id"), rsEmpName.getString("emp_fname")+strEmpMName+" "+rsEmpName.getString("emp_lname") +" [TL]" ));
				}
				rsEmpName.close();
				pst.close();
				
				
				pst = con.prepareStatement("select * from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and is_alive = true and pro_id=? and _isteamlead = false");
				pst.setInt(1, pro_id);
				rsEmpName = pst.executeQuery();
				while(rsEmpName.next()){
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpName.getString("emp_mname");
						}
					}
					
					al.add(new FillTaskEmpList(rsEmpName.getString("emp_id"), rsEmpName.getString("emp_fname")+strEmpMName+" "+rsEmpName.getString("emp_lname")));
				}
				rsEmpName.close();
				pst.close();
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rsEmpName);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			
			return al;
		}
public List<Integer> fillEmployeeId(int pro_id)
{
	List<Integer> empidlist = new ArrayList<Integer>();
	String selectEmployeeByShift = "select * from project_emp_details where pro_id=?";
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rsEmpId = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		
		con = db.makeConnection(con);
		pst = con.prepareStatement(selectEmployeeByShift);
		pst.setInt(1, pro_id);
		rsEmpId = pst.executeQuery();
		while(rsEmpId.next()){
			if(rsEmpId.getInt("emp_id")<0){
				continue;
			}
			empidlist.add(rsEmpId.getInt("emp_id"));				
		}	
		rsEmpId.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		db.closeResultSet(rsEmpId);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return empidlist;
	
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


public String getTaskEmployeeId() {
	return TaskEmployeeId;
}

public void setTaskEmployeeId(String taskEmployeeId) {
	TaskEmployeeId = taskEmployeeId;
}

public String getTaskEmployeeName() {
	return TaskEmployeeName;
}

public void setTaskEmployeeName(String taskEmployeeName) {
	TaskEmployeeName = taskEmployeeName;
}

			
		
	}