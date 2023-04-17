package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillEmployee implements IStatements {

	private String employeeId;
	private String employeeName;
	private String employeeCode;
	private String roster;

	public String getRoster() {
		return roster;
	}

	public void setRoster(String roster) {
		this.roster = roster;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public FillEmployee(String employeeId, String employeeCode) {
		this.employeeId = employeeId;
		this.employeeCode = employeeCode;
	}

	public FillEmployee(String employeeId, String employeeName, String roster) {
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.roster = roster;
	}
	HttpServletRequest request;
	public FillEmployee(HttpServletRequest request) {
		this.request = request; 
	}
	public FillEmployee() {
	}
 
	public List<FillEmployee> fillEmployeeNameByLevel(String level) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
				"and  grade_id in(SELECT grade_id FROM grades_details where designation_id in (SELECT designation_id FROM designation_details WHERE " +
				"level_id in ("+level+"))) order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;

	}
	
	public List<FillEmployee> fillEmployeeNameByLevel() {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
				"and  grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details)) order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
//System.out.println("rsEmpCode.getString(emp_per_id) "+rsEmpCode.getString("emp_per_id"));
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;

	}

	public List<FillEmployee> fillEmployeeNameByDesig(String level,String desig) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					" and grade_id in (SELECT grade_id FROM grades_details where designation_id in (SELECT designation_id FROM designation_details " +
					"WHERE designation_id in ("+desig+") and level_id in ("+level+"))) order by emp_id");
			
			rsEmpCode = pst.executeQuery(); 
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillEmployeeNameByDesig() {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					"and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details) order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillEmployeeNameByDesig(String desig) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					" and grade_id in (SELECT grade_id FROM grades_details where designation_id in (SELECT designation_id FROM designation_details " +
					"WHERE designation_id in ("+desig+"))) order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillEmployeeNameByDesigSupervisor(
			String supervisor) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and " +
				"grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details)) and " +
				"eod.supervisor_emp_id in ("+supervisor+") order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillEmployee> fillEmployeeNameByLevelSuperVisor(String level,
			String supervisor) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and " +
				"grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details WHERE " +
				"level_id in ("+level+"))) and eod.supervisor_emp_id in ("+supervisor+") order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+" " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillEmployee> fillEmployeeNameByLevelSuperVisor(
			String supervisor) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and " +
				"grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details)) and " +
				"eod.supervisor_emp_id in ("+supervisor+") order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
//System.out.println("rsEmpCode.getString(emp_per_id) "+rsEmpCode.getString("emp_per_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillEmployee> fillEmployeeNameByDesigSuperVisor(String desig,
			String supervisor) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and " +
				"grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details where " +
				"designation_id in ("+desig+"))) and eod.supervisor_emp_id in ("+supervisor+") order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillEmployeeNameByDesigSupervisor(String level,
			String desig, String supervisor) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);

			boolean flagMiddleName=getFeatureStatusForEmpMiddleName();

			
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and " +
				"grade_id in (SELECT grade_id FROM grades_details where designation_id in (SELECT designation_id FROM designation_details where " +
				"designation_id in ("+desig+"))) and eod.supervisor_emp_id in ("+supervisor+")  order by emp_id");
			
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
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
	
	
}
