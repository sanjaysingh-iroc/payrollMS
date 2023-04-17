package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

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

	public List<FillEmployee> fillEmployeeName(int gradeId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive = true and grade_id=? order by epd.emp_fname");
			pst.setInt(1, gradeId);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName + " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;

	}

	public List<FillEmployee> fillEmployeeName(int gradeId, int location) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and grade_id=? and wlocation_id=? order by epd.emp_fname");
			pst.setInt(1, gradeId);
			pst.setInt(2, location);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			
		}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;

	}

	public List<FillEmployee> fillEmployeeName(int gradeId, int location, int depart) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id");
			if(gradeId > 0) {
				sbQuery.append(" and grade_id= "+gradeId+"");
			}
			if(location > 0) {
				sbQuery.append(" and wlocation_id="+location+"");
			}
			if(depart > 0) {
				sbQuery.append(" and depart_id="+depart+"");
			}
			sbQuery.append("  order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;

	}

	public List<FillEmployee> fillEmployeeName(int gradeId, String depart) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and grade_id=? and depart_id=? order by epd.emp_fname");
			pst.setInt(1, gradeId);
			pst.setInt(2, uF.parseToInt(depart));
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;

	}

	public List<FillEmployee> fillEmployeeName() {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive=true order by epd.emp_fname");
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillEmployeeNameWithJoiningDate(String strStartDate, String startMinDays, String strEndDate, String endMinDays) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Date strNewStartDate = uF.getFutureDate(uF.getDateFormatUtil(strStartDate, DBDATE), -uF.parseToInt(startMinDays));
			Date strNewEndDate = uF.getFutureDate(uF.getDateFormatUtil(strEndDate, DBDATE), -uF.parseToInt(endMinDays));
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive=true ");
			if(uF.parseToInt(startMinDays)>0 && uF.parseToInt(endMinDays)>0) {
				sbQuery.append(" and (joining_date<='" + strNewStartDate +"' or joining_date<='" + strNewEndDate +"')");
			} else if(uF.parseToInt(startMinDays)>0) {
				sbQuery.append(" and joining_date<='" + strNewStartDate +"' ");
			} else if(uF.parseToInt(endMinDays)>0) {
				sbQuery.append(" and joining_date<='" + strNewEndDate +"' ");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive=true order by epd.emp_fname");
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	

	public List<FillEmployee> fillEmployeeNameCode(String strUserType, String strEmpId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
				pst = con.prepareStatement(selectEmployee_OCode);
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
				pst = con.prepareStatement(selectEmployee_OCode_HRManager);
				pst.setInt(1, uF.parseToInt(strEmpId));
			} else if (strUserType != null && strUserType.equalsIgnoreCase(MANAGER)) {
				pst = con.prepareStatement(selectEmployee_OName_Manager);
				pst.setInt(1, uF.parseToInt(strEmpId));
			} else {
				pst = con.prepareStatement(selectEmployee_OCode);
			}

			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				if (rsEmpCode.getInt("emp_per_id") < 0) {
					continue;
				}
				if (rsEmpCode.getString("empcode") != null && rsEmpCode.getString("empcode").length() > 0) {
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpCode.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
							+ rsEmpCode.getString("empcode") + "]"));
				}
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	

	public List<FillEmployee> fillSupervisorNameCode(String strUserType, String strEmpId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
				pst = con.prepareStatement(selectSupervisor_OName);
				pst.setString(1, EMPLOYEE);
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
				pst = con.prepareStatement(selectSupervisor_OName_HRManager);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setString(2, EMPLOYEE);
			} else {
				pst = con.prepareStatement(selectSupervisor_OName);
				pst.setString(1, EMPLOYEE);
			}

			rsEmpCode = pst.executeQuery();

		//===start parvez date: 01-08-2022===	
//			al.add(new FillEmployee("0", "No Supervisor"));
			al.add(new FillEmployee("0", "No Manager"));
		//===end parvez date: 01-08-2022	

			while (rsEmpCode.next()) {
				if (rsEmpCode.getInt("emp_per_id") < 0) {
					continue;
				}
				if (rsEmpCode.getString("empcode") != null && rsEmpCode.getString("empcode").length() > 0) {
					
					String strEmpMName = "";
					
					if(flagMiddleName) {
						if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpCode.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
							+ rsEmpCode.getString("empcode") + "]"));
				}
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillEmployee> fillEmployeeCode(String strUserType, String strEmpId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
		//	boolean flagMiddleName=getFeatureStatusForEmpMiddleName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
				pst = con.prepareStatement(selectEmployee_OCode);
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
				pst = con.prepareStatement(selectEmployee_OCode_HRManager);
				pst.setInt(1, uF.parseToInt(strEmpId));
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER))) {
				pst = con.prepareStatement(selectEmployee_OCode_Manager);
				pst.setInt(1, uF.parseToInt(strEmpId));
			} else {
				pst = con.prepareStatement(selectEmployee_OCode);
			}

			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				if (rsEmpCode.getInt("emp_per_id") < 0) {
					continue;
				}
				if (rsEmpCode.getString("empcode") != null && rsEmpCode.getString("empcode").length() > 0) {
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("empcode")));
				}
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	

	public List<FillEmployee> fillEmployeeName(String strUserType, String strEmpId, HttpSession session) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpName = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			StringBuilder sbQuery = new StringBuilder();
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
				pst = con.prepareStatement(selectEmployee_OName);
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
				sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod where " +
					" epd.is_alive = true and epd.emp_per_id = eod.emp_id ");
				if(session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS) +") ");
				}
				if(session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS) +") ");
				}
				sbQuery.append(" order by emp_fname,emp_mname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
//				pst = con.prepareStatement(selectEmployee_OName_HRManager);
//				pst.setInt(1, uF.parseToInt(strEmpId));
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER))) {
				pst = con.prepareStatement(selectEmployee_OName_Manager);
				pst.setInt(1, uF.parseToInt(strEmpId));
			} else {
				pst = con.prepareStatement(selectEmployee_OName);
			}
//			System.out.println("pst ===>>>> " + pst);
			rsEmpName = pst.executeQuery();
			while (rsEmpName.next()) {
				if (rsEmpName.getInt("emp_per_id") < 0) {
					continue;
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpName.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") + strEmpMName+ " " + rsEmpName.getString("emp_lname"), ""));
			}
			rsEmpName.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpName);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillEmployeeName(String strUserType, String strEmpId, int nOrgId, int wLocId, HttpSession session) {

//		System.out.println("strUserType in fillEployee==>"+strUserType);
//		System.out.println("strEmpId in fillEployee==>"+strEmpId);
//		System.out.println("nOrgId in fillEployee==>"+nOrgId);
//		System.out.println("wLocId in fillEployee==>"+nOrgId);
//		System.out.println("session in fillEployee==>"+session);
		
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpName = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery = new StringBuilder();
			if(nOrgId>0) {
				if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN))) {
					pst = con.prepareStatement(selectEmployee_OName1);
					sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id " +
							"and is_alive = true ");
					if(wLocId>0) {
						sbQuery.append(" and eod.wlocation_id in ("+wLocId+") ");
					}
					sbQuery.append(" and org_id =? order by emp_fname,emp_mname, emp_lname");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, nOrgId);
				} else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
//					pst = con.prepareStatement(selectEmployee_OName_HRManager1);
					sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod where epd.is_alive = true " +
							"and epd.emp_per_id = eod.emp_id ");
					if(wLocId>0) {
						sbQuery.append(" and eod.wlocation_id in ("+wLocId+") ");
					} else if(session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
						sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS) +") ");
					}
					sbQuery.append(" and org_id =? order by emp_fname,emp_mname, emp_lname");
					pst = con.prepareStatement(sbQuery.toString());
//					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(1, nOrgId);
				} else if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER))) {
					pst = con.prepareStatement(selectEmployee_OName_Manager1);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, nOrgId);
				} else {
					pst = con.prepareStatement(selectEmployee_OName1);
					pst.setInt(1, nOrgId);
				}
			} else {
				if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
//					pst = con.prepareStatement(selectEmployee_OName_HRManager1);
					sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod where " +
						" epd.is_alive = true and epd.emp_per_id = eod.emp_id ");
					if(session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
						sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS) +") ");
					}
					if(session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
						sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS) +") ");
					}
					sbQuery.append(" order by emp_fname, emp_mname,emp_lname");
					pst = con.prepareStatement(sbQuery.toString());
				} else if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER))) {
					pst = con.prepareStatement(selectEmployee_OName_Manager);
					pst.setInt(1, uF.parseToInt(strEmpId));
				} else {
					pst = con.prepareStatement(selectEmployee_OName);
				}
			}
			
//			System.out.println("pst fillEmployee ===>> " + pst);
			rsEmpName = pst.executeQuery();
			while (rsEmpName.next()) {
				if (rsEmpName.getInt("emp_per_id") < 0) {
					continue;
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpName.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") + strEmpMName+ " " + rsEmpName.getString("emp_lname"), ""));
			}
			rsEmpName.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpName);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillEmployee> fillEmployeeNameByShift(String serviceId) {
		String id = serviceId + ",";
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		// String selectEmployeeByShift =
		// "SELECT * FROM (SELECT * FROM employee_official_details where service_id ='"+id+"' ) aepd  JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id WHERE epd.is_alive= true order by emp_fname";
		String selectEmployeeByShift = "SELECT * FROM (SELECT * FROM employee_official_details where service_id like ? ) aepd  JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id WHERE epd.is_alive= true order by emp_fname";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpName = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement(selectEmployeeByShift);
			pst.setString(1, "%," + serviceId + ",%");
			rsEmpName = pst.executeQuery();

			while (rsEmpName.next()) {
				if (rsEmpName.getInt("emp_per_id") < 0) {
					continue;
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpName.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") + strEmpMName+ " " + rsEmpName.getString("emp_lname"), rsEmpName.getString("is_roster")));
			}
			rsEmpName.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpName);
			db.closeConnection(con);
		}

		return al;
	}
	
	
	public List<FillEmployee> fillEmployeeNameByShift(String serviceId, String strWLocationId, UtilityFunctions uF) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		// String selectEmployeeByShift =
		// "SELECT * FROM (SELECT * FROM employee_official_details where service_id ='"+id+"' ) aepd  JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id WHERE epd.is_alive= true order by emp_fname";
		String selectEmployeeByShift = "SELECT * FROM (SELECT * FROM employee_official_details where service_id like ?  and wlocation_id=? ) aepd  JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id WHERE epd.is_alive= true order by emp_fname";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpName = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement(selectEmployeeByShift);
			pst.setString(1, "%," + serviceId + ",%");
			pst.setInt(2, uF.parseToInt(strWLocationId));
			rsEmpName = pst.executeQuery();
//			System.out.println("pst=====>" + pst);
			while (rsEmpName.next()) {
				if (rsEmpName.getInt("emp_per_id") < 0) {
					continue;
				}
               String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpName.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") + strEmpMName+ " " + rsEmpName.getString("emp_lname"), rsEmpName.getString("is_roster")));
			}
			rsEmpName.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpName);
			db.closeConnection(con);
		}

		return al;
	}

	
	public List<FillEmployee> fillEmployeeName(String serviceId, String strUserType, String strEmpId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpName = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
				pst = con.prepareStatement(selectEmployeeByServiceId);
				pst.setString(1, serviceId + ",%");
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
				pst = con.prepareStatement(selectEmployeeByServiceIdHRManager);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setString(2, serviceId + ",%");
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER))) {
				pst = con.prepareStatement(selectEmployeeByServiceIdManager);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setString(2, serviceId + ",%");
			}

			rsEmpName = pst.executeQuery();
			while (rsEmpName.next()) {
				if (rsEmpName.getInt("emp_per_id") < 0) {
					continue;
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpName.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") + strEmpMName+ " " + rsEmpName.getString("emp_lname"), ""));
			}
			rsEmpName.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpName);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillEmployee> fillEmployeeName(String strD2, int level_id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();

		try {

			con = db.makeConnection(con);
		    boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if (level_id == 0) {
				pst = con.prepareStatement(selectEmpPerPC);
				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
			} else {
				pst = con.prepareStatement(selectEmpPerLevelPerPC);
				pst.setInt(1, level_id);
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			}

			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("emp_id") < 0) {
					continue;
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname"), ""));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}

	
	public List<FillEmployee> fillEmployeeName(String strD1, String strD2, String strSessionEmpId, String strUserType) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.emp_id ,epd.emp_fname, epd.emp_lname, epd.emp_mname from attendance_details ad inner join employee_official_details eod on eod.emp_id =ad.emp_id inner join employee_personal_details epd on epd.emp_per_id= eod.emp_id where user_location!='null' and in_out_timestamp between ? and ? ");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and eod.superviser_id = "+uF.parseToInt(strSessionEmpId)+" ");
			}
			sbQuery.append(" group by eod.emp_id ,epd.emp_fname,epd.emp_mname, epd.emp_lname ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
		
		//	System.out.println("===>pstemp"+pst);
			rs = pst.executeQuery();			
		
			while (rs.next()) {
				if (rs.getInt("emp_id") < 0) {
					continue;
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname"), ""));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}
	
	
	public List<FillEmployee> fillEmployeeNameByParentLevel(int nParentLevel) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("SELECT emp_id, emp_fname,emp_mname, emp_lname FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id = eod.emp_id and epd.is_alive=true and grade_id in (select grade_id from level_details ld, designation_details dd, grades_details gd where dd.level_id = ld.level_id and gd.designation_id = dd.designation_id and (ld.level_parent = ? or ld.level_parent is null)) order by emp_fname,emp_mname, emp_lname"); 
			pst.setInt(1, nParentLevel);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("emp_id") < 0) {
					continue;
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname"), ""));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return empNamesList;
	}
	public List<FillEmployee> fillEmployeeNameBySkills(String[] skills, com.konnect.jpms.util.CommonFunctions CF ) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			for (int i = 0; i < skills.length; i++) {
				pst = con.prepareStatement("SELECT epd.emp_per_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,sd.skill_id FROM employee_personal_details epd,skills_description sd where epd.emp_per_id=sd.emp_id and is_alive=true and sd.skill_id=?  order by epd.emp_fname");
				pst.setString(1, skills[i]);
				rs = pst.executeQuery();
				while (rs.next()) {
					String skillName = CF.getSkillNameBySkillId(con, rs.getString("skill_id"));
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + "(" + skillName + ")", ""));
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;

	}

	public List<FillEmployee> fillEmployeeNameByServiceID(String serviceId) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
//			System.out.println("serviceId ===>> "  +  serviceId);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if(serviceId != null && !serviceId.equals("")) {
				pst = con.prepareStatement("SELECT epd.emp_per_id,epd.emp_fname,epd.emp_mname,epd.emp_lname FROM employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and is_alive = true and eod.service_id like '%," + serviceId + ",%' order by epd.emp_fname");
//				System.out.println("pst =====>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname"), ""));
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;

	}

	public List<FillEmployee> fillManagerList() {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("SELECT emp_per_id,emp_fname,emp_mname,emp_lname FROM employee_personal_details where emp_per_id in (select distinct(supervisor_emp_id) from employee_official_details where supervisor_emp_id>0)  order by emp_fname");
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname"), ""));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;

	}

	public List<FillEmployee> fillEmployeeNameBySkill(String skill) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd,skills_description eod where epd.emp_per_id=eod.emp_id and eod.skills_name=? order by epd.emp_fname");
			pst.setString(1, skill);

			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname"), ""));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;

	}

	public List<FillEmployee> fillEmployeeNameByLocation(String userlocation) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive = true ");
			if(userlocation != null && userlocation.length()>0) {
				sbQuery.append("and wlocation_id in ("+ userlocation + ")");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}

	
	public List<FillEmployee> fillEmployeeNameByLocationWithJoiningDate(String userlocation, String strStartDate, String startMinDays, String strEndDate, String endMinDays) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Date strNewStartDate = uF.getFutureDate(uF.getDateFormatUtil(strStartDate, DBDATE), -uF.parseToInt(startMinDays));
			Date strNewEndDate = uF.getFutureDate(uF.getDateFormatUtil(strEndDate, DBDATE), -uF.parseToInt(endMinDays));
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod, employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id in ("
					+ userlocation + ") and is_alive = true ");
			if(uF.parseToInt(startMinDays)>0 && uF.parseToInt(endMinDays)>0) {
				sbQuery.append(" and (joining_date<='" + strNewStartDate +"' or joining_date<='" + strNewEndDate +"')");
			} else if(uF.parseToInt(startMinDays)>0) {
				sbQuery.append(" and joining_date<='" + strNewStartDate +"' ");
			} else if(uF.parseToInt(endMinDays)>0) {
				sbQuery.append(" and joining_date<='" + strNewEndDate +"' ");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}
	
	public List<FillEmployee> fillFinalizationNameByLocation(String userlocation) {
		ArrayList<FillEmployee> finalizeNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where" +
				" epd.emp_per_id=eod.emp_id and epd.emp_per_id = ud.emp_id and ud.usertype_id in(1,7) ");
			if(userlocation != null && userlocation.length()>0) {
				sbQuery.append(" and wlocation_id in (" + userlocation + ") ");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				finalizeNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]"));

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return finalizeNamesList;
	}
	
	public List<FillEmployee> fillEmployeeNameByLocation(String userlocation, boolean isWithCode) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id in ("
					+ userlocation + ")  order by emp_fname, emp_lname");
			// pst.setInt(1, uF.parseToInt(userlocation));
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + ((isWithCode)?""+" [" + rs.getString("empcode") + "]":""),""));

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}
	
	public List<FillEmployee> fillEmployeeNameByDepart(String location, String depart) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id in (" + location
					+ ") and depart_id in (" + depart + ")  order by epd.emp_fname");
			/*
			 * pst.setInt(1, uF.parseToInt(location)); pst.setInt(2,
			 * uF.parseToInt(depart));
			 */
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]"));

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}

	public List<FillEmployee> fillEmployeeNameByLevel(String level, String location, String depart) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id in (" + location
					+ ")  and depart_id in (" + depart
					+ ") and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id in (" + level
					+ ")) )  order by epd.emp_fname");
			/*
			 * pst.setInt(1, uf.parseToInt(location)); pst.setInt(2,
			 * uf.parseToInt(depart)); pst.setInt(3, uf.parseToInt(level));
			 */
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;

	}

	public List<FillEmployee> fillEmployeeNameByDesignation(String level, String location, String depart, String design) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id in (" + location
					+ ")  and depart_id in (" + depart
					+ ") and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id in (" + level
					+ ") and designation_id in (" + design + ")))  order by epd.emp_fname");
			/*
			 * pst.setInt(1, uf.parseToInt(location)); pst.setInt(2,
			 * uf.parseToInt(depart)); pst.setInt(3, uf.parseToInt(level));
			 * pst.setInt(4, uf.parseToInt(design));
			 */
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillEmployeeNameByGrade(String level, String location, String depart, String design, String grade) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id in (" + location
					+ ")  and depart_id in (" + depart + ") and grade_id in(SELECT grade_id FROM grades_details where grade_id in (" + grade
					+ ") and designation_id in(SELECT designation_id FROM designation_details  WHERE level_id in (" + level + ") and designation_id in (" + design
					+ "))) order by epd.emp_fname");
			/*
			 * pst.setInt(1, uf.parseToInt(location)); pst.setInt(2,
			 * uf.parseToInt(depart)); pst.setInt(3, uf.parseToInt(grade));
			 * pst.setInt(4, uf.parseToInt(level)); pst.setInt(5,
			 * uf.parseToInt(design));
			 */
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillCandidateRecruitmentEmployee(String org, String location, String level, String desig, String grade) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {     
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuffer query = new StringBuffer();
			query.append(" select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive=true ");
			if(org !=null && !org.equals("")){
				query.append(" and eod.org_id in ("+ org +") ");
			}
			
			if (location != null && !location.equals("")) {
				query.append(" and eod.wlocation_id in (" + location + ")");
			}
			
			if (level != null && !level.equals("")) {
				query.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id in ("
						+ level + "))) ");

			}
			if (desig != null && !desig.equals("")) {
				query.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  where designation_id in (" + desig + ") ))");

			}
			if (grade != null && !grade.equals("")) {
				query.append(" and grade_id in (" + grade + ")");
			}
			query.append(" order by epd.emp_fname");
			pst = con.prepareStatement(query.toString());
	
			rst = pst.executeQuery();
			while (rst.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rst.getString("emp_per_id"), rst.getString("emp_fname")+ strEmpMName+ " " + rst.getString("emp_lname") + " [" + rst.getString("empcode") + "]"));
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}
		return al;
	}
	

	public List<FillEmployee> fillEmployeeNameBySupervisor(String supervisorId) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.supervisor_emp_id in ("
					+ supervisorId + ") and epd.is_alive=true order by epd.emp_fname");
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillEmployee> fillTrainingEmployee(String location, String alignedwith, String level, String desig, String grade, String org_id) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuffer sbQuery = new StringBuffer();
			if(uF.parseToInt(alignedwith)==1){
				
			}else if(uF.parseToInt(alignedwith)==2){
				sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive = true and emp_per_id in (select emp_id from training_gap_details) ");
//				sbQuery.append("select empcode,emp_fname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd  join training_gap_details on emp_per_id=emp_id where epd.emp_per_id=eod.emp_id ");
			}else{
				sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive = true ");
			}
			if (location != null && !location.equals("")) {
				sbQuery.append(" and eod.wlocation_id in (" + location + ")");
			}
			if (level != null && !level.equals("")) {
				sbQuery.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id in ("
						+ level + "))) ");
			}
			if (desig != null && !desig.equals("")) {
				sbQuery.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  where designation_id in (" + desig + ") ))");
			}
			if (grade != null && !grade.equals("")) {
				sbQuery.append(" and grade_id in (" + grade + ")");
			}
			if (org_id != null && !org_id.equals("")) {
				sbQuery.append(" and org_id in(" + org_id + ")");
			}
			sbQuery.append("  order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	

	public List<FillEmployee> fillEmployeeName(String strD2, int level_id,int org_id) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					" and epd.is_alive= true and epd.joining_date <= ? ");
			if(org_id>0){
				sbQuery.append(" and eod.org_id="+org_id);
			}
			if(level_id>0){
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
						" (SELECT designation_id FROM designation_details  WHERE level_id =" + level_id+ ")) ");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname"), ""));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillEmployeeNameOrgLocationDepartSBUDesigGrade(com.konnect.jpms.util.CommonFunctions CF, String strOrg, String strLocation, String strDepart, String strSBU, String strLevel, String strDesig, String strGrade, String strEmployeementType, boolean exEmpFlag) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			String[] f_employeType = null;
			if(strEmployeementType != null && !strEmployeementType.equals("")) {
				f_employeType = strEmployeementType.split(",");
			}
			String[] f_service = null;
			if(strSBU != null && !strSBU.equals("")) {
				f_service = strSBU.split(",");
			}
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id "); // and epd.joining_date<=? 
			if(!exEmpFlag) {
				sbQuery.append(" and epd.is_alive=true");
			}
			if(strOrg != null && !strOrg.trim().equals("null")) {
				sbQuery.append(" and eod.org_id in ("+strOrg+") ");
			}
			if(strLocation != null && !strLocation.trim().equals("null") && !strLocation.trim().equals("")) {
				sbQuery.append(" and eod.wlocation_id in ("+strLocation+") ");
			}
			if(strDepart != null && !strDepart.trim().equals("null") && !strDepart.trim().equals("") && uF.parseToInt(strDepart)>0) {
				sbQuery.append(" and eod.depart_id in ("+strDepart+") ");
			}
			 if(f_service!=null && f_service.length>0){
                sbQuery.append(" and (");
                for(int i=0; i<f_service.length; i++){
                    sbQuery.append(" eod.service_id like '%,"+f_service[i]+",%'");
                    
                    if(i<f_service.length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			if(strLevel != null && !strLevel.trim().equals("null") && !strLevel.trim().equals("")) {
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
					" (SELECT designation_id FROM designation_details  WHERE level_id in (" + strLevel+ ") )) ");
			}
			if(strDesig != null && !strDesig.trim().equals("null") && !strDesig.trim().equals("")) {
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
					" (" + strDesig+ ") ) ");
			}
			if(strEmployeementType != null && !strEmployeementType.trim().equals("'null'") && !strEmployeementType.trim().equals("")) {
				sbQuery.append("  and emptype in ( '" + StringUtils.join(f_employeType, "','") + "') ");
			}
			if(strGrade != null && !strGrade.trim().equals("null") && !strGrade.trim().equals("")) {
				sbQuery.append(" and eod.grade_id in (" + strGrade+ ") ");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===>> " + pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname")+ " [" + rsEmpCode.getString("empcode") + "]", ""));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillEmployeeName(String strD2, int level_id,int org_id,int location_id) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					" and epd.is_alive= true and epd.joining_date <= ? ");
			if(org_id>0){
				sbQuery.append(" and eod.org_id="+org_id);
			}
			if (location_id>0) {
				sbQuery.append(" and eod.wlocation_id=" + location_id);
			}
			if(level_id>0){
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
						" (SELECT designation_id FROM designation_details  WHERE level_id =" + level_id+ ")) ");
			}
			sbQuery.append(" order by epd.emp_fname");
			
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pstfill Emp===>"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname"), ""));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	public Map<String, String> getFeatureStatusMap(Connection con, HttpServletRequest request) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmFeatureStatus = new HashMap<String, String>();
		Map<String, List<String>> hmFeatureUserTypeId = new HashMap<String, List<String>>();
		try {
			pst = con.prepareStatement("select feature_name,feature_status,user_type_id from feature_management");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmFeatureStatus.put(rst.getString("feature_name"), rst.getString("feature_status"));
				List<String> innerList = new ArrayList<String>();
				if (rst.getString("user_type_id") != null) {
					innerList = Arrays.asList(rst.getString("user_type_id").split(","));
				}
				hmFeatureUserTypeId.put(rst.getString("feature_name"), innerList);
			}
			// System.out.println("scree-"+ScreenShotName);
			rst.close();
			pst.close();

			request.setAttribute("hmFeatureUserTypeId", hmFeatureUserTypeId);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmFeatureStatus;
	}
	
	public List<FillEmployee> fillSupervisorNameCode(int strEmpId, String orgId, String depart) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName = getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			boolean flagGlobalMgrAccess = getFeatureStatusName(F_ENABLE_GLOBAL_MANAGER_ACCESS);
			StringBuffer sbQuery = new StringBuffer();
			Map<String, String> hmFeatureStatus = getFeatureStatusMap(con, request);
			// this if condition is for HBFuller customizations for Only
			// Supervisor/Manager List
			if (hmFeatureStatus != null && hmFeatureStatus.get(F_USERTYPE_ONLY_MANAGER) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_ONLY_MANAGER))) {
				sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod, user_details ud WHERE "
					+ " is_alive = true and epd.emp_per_id = eod.emp_id and epd.emp_per_id = ud.emp_id and epd.emp_per_id != ? ");
				if (strEmpId > 0) {
					sbQuery.append(" and epd.emp_per_id not in (select emp_id from employee_official_details where supervisor_emp_id = "+strEmpId+") ");
				}
				if (uF.parseToInt(orgId) > 0 && !flagGlobalMgrAccess) {
					sbQuery.append(" and org_id=" + uF.parseToInt(orgId) + " ");
				}
				sbQuery.append(" and usertype_id = (select user_type_id from user_type where user_type = '" + MANAGER + "') order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, strEmpId);
				// System.out.println("pst==>"+pst);
				rsEmpCode = pst.executeQuery();
				/*
				 * if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER))) { 
				 * pst = con.prepareStatement(selectSupervisor_OName);
				 * pst.setString(1, EMPLOYEE); // } else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) { 
				 * } else if (strUserType != null && strUserType.equalsIgnoreCase(ACCOUNTANT)) { 
				 * pst = con.prepareStatement(selectSupervisor_OName_HRManager);
				 * pst.setInt(1, uF.parseToInt(strEmpId)); pst.setString(2, EMPLOYEE); } else { 
				 * pst =con.prepareStatement(selectSupervisor_OName);
				 * pst.setString(1, EMPLOYEE); } rsEmpCode = pst.executeQuery();
				 */
				
			//===start parvez date: 01-08-2022===	
//				al.add(new FillEmployee("0", "No Supervisor"));
				al.add(new FillEmployee("0", "No Manager"));
			//===end parvez date: 01-08-2022===	
				while (rsEmpCode.next()) {
					if (rsEmpCode.getInt("emp_per_id") <= 0) {
						continue;
					}
					if (rsEmpCode.getString("empcode") != null && rsEmpCode.getString("empcode").length() > 0) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rsEmpCode.getString("emp_mname");
							}
						}
						al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname")
							+ " [" + rsEmpCode.getString("empcode") + "]"));
					}
				}
				rsEmpCode.close();
				pst.close();

			} else {
				sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod, user_details ud WHERE "
						+ " is_alive = true and epd.emp_per_id = eod.emp_id and epd.emp_per_id = ud.emp_id and epd.emp_per_id != ? ");
				if (strEmpId > 0) {
					sbQuery.append(" and epd.emp_per_id not in (select emp_id from employee_official_details where supervisor_emp_id = "+strEmpId+") ");
				}
				if (uF.parseToInt(orgId) > 0 && !flagGlobalMgrAccess) {
					sbQuery.append(" and org_id=" + uF.parseToInt(orgId) + " ");
				}
				sbQuery.append(" and usertype_id != (select user_type_id from user_type where user_type =?) order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, strEmpId);
				pst.setString(2, EMPLOYEE);
//				System.out.println("superwiser pst1 ===>> " + pst);
				rsEmpCode = pst.executeQuery();

				/*
				 * if (strUserType != null &&
				 * (strUserType.equalsIgnoreCase(ADMIN) ||
				 * strUserType.equalsIgnoreCase(CEO) ||
				 * strUserType.equalsIgnoreCase(CFO) ||
				 * strUserType.equalsIgnoreCase(HRMANAGER))) { pst =
				 * con.prepareStatement(selectSupervisor_OName);
				 * pst.setString(1, EMPLOYEE); // } else if (strUserType != null
				 * && (strUserType.equalsIgnoreCase(HRMANAGER) ||
				 * strUserType.equalsIgnoreCase(ACCOUNTANT))) { } else if
				 * (strUserType != null &&
				 * strUserType.equalsIgnoreCase(ACCOUNTANT)) { pst =
				 * con.prepareStatement(selectSupervisor_OName_HRManager);
				 * pst.setInt(1, uF.parseToInt(strEmpId)); pst.setString(2,
				 * EMPLOYEE); } else { pst =
				 * con.prepareStatement(selectSupervisor_OName);
				 * pst.setString(1, EMPLOYEE); } rsEmpCode = pst.executeQuery();
				 */
				
			//===start parvez date: 01-08-2022===	
//				al.add(new FillEmployee("0", "No Supervisor"));
				al.add(new FillEmployee("0", "No Manager"));
			//===end parvez date: 01-08-2022===	
				List<String> alEmp = new ArrayList<String>();
				while (rsEmpCode.next()) {
					if (rsEmpCode.getInt("emp_per_id") <= 0) {
						continue;
					}
					if (rsEmpCode.getString("empcode") != null && rsEmpCode.getString("empcode").length() > 0) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rsEmpCode.getString("emp_mname");
							}
						}
						al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " [" + rsEmpCode.getString("empcode") + "]"));

						if (!alEmp.contains(rsEmpCode.getString("emp_per_id"))) {
							alEmp.add(rsEmpCode.getString("emp_per_id"));
						}
					}
				}
				rsEmpCode.close();
				pst.close();

				if (uF.parseToInt(orgId) > 0 && !flagGlobalMgrAccess) {
					sbQuery = new StringBuffer();
					// sbQuery.append("select empcode,emp_fname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
					sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod, employee_personal_details epd, user_details ud "
							+ "where is_alive = true and epd.emp_per_id = ud.emp_id and epd.emp_per_id=eod.emp_id and epd.emp_per_id != ? ");
					if (strEmpId > 0) {
						sbQuery.append(" and epd.emp_per_id not in (select emp_id from employee_official_details where supervisor_emp_id = "+strEmpId+") ");
					}
					sbQuery.append(" and usertype_id = (select user_type_id from user_type where user_type = '" + EMPLOYEE + "') ");
					if (depart != null && !depart.equals("")) {
						sbQuery.append(" and eod.depart_id in (" + depart + ") ");
					}
					sbQuery.append(" order by epd.emp_fname");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, strEmpId);
					rsEmpCode = pst.executeQuery();
	//				System.out.println("SupervisorEmployee pst ===> " + pst);
					while (rsEmpCode.next()) {
						if (alEmp.contains(rsEmpCode.getString("emp_per_id")) && rsEmpCode.getInt("emp_per_id") <= 0) {
							continue;
						}
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rsEmpCode.getString("emp_mname");
							}
						}
						al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
								+ rsEmpCode.getString("empcode") + "]"));
					}
					rsEmpCode.close();
					pst.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillHODNameCode(String strEmpId, String orgId, int wlocation, com.konnect.jpms.util.CommonFunctions CF) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			boolean flagGlobalHODAccess = getFeatureStatusName(F_ENABLE_GLOBAL_HOD_ACCESS);	//added by parvez date: 13-02-2023
			StringBuilder sbQuery = new StringBuilder();
			Map<String, String> hmFeatureStatus = getFeatureStatusMap(con, request);

			// this if condition is for HBFuller customizations for Only HOD ------- Kalpana
			// Now Both COnditions are Same ------ Rahul Patil
			
			if (hmFeatureStatus != null && hmFeatureStatus.get(F_USERTYPE_ONLY_HOD) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_ONLY_HOD))) {
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id "
						+ "and epd.is_alive= true and epd.joining_date <= ? and ud.emp_id=epd.emp_per_id and epd.emp_per_id != ? and usertype_id = "
						+ "(select user_type_id from user_type where user_type = '" + HOD + "')");
			//===start parvez date: 13-02-2023===	
				if (uF.parseToInt(orgId) > 0 && !flagGlobalHODAccess) {
			//===end parvez date: 13-02-2023===		
					sbQuery.append(" and org_id in ('" + orgId + "')");
				}
			} else {

				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id "
						+ "and epd.is_alive= true  and epd.joining_date <= ? and ud.emp_id=epd.emp_per_id and epd.emp_per_id != ? and usertype_id = "
						+ "(select user_type_id from user_type where user_type = '" + HOD + "')");
			//===start parvez date: 13-02-2023===	
				if (uF.parseToInt(orgId) > 0 && !flagGlobalHODAccess) {
			//===end parvez date: 13-02-2023===		
//					sbQuery.append(" and org_id_access like '%," + orgId + ",%'"); // -------- this is previous one
					sbQuery.append(" and org_id in ('" + orgId + "')");
				}
				/*
				 * if(wlocation>0) {
				 * sbQuery.append(" and wlocation_id_access like '%,"
				 * +wlocation+",%'"); }
				 */
			}
			sbQuery.append(" order by epd.emp_fname");

			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(strEmpId));
//			System.out.println("pst2 ===>> " + pst);
			rsEmpCode = pst.executeQuery();
			al.add(new FillEmployee("0", "No HOD"));
			while (rsEmpCode.next()) {
				if (rsEmpCode.getInt("emp_per_id") < 0) {
					continue;
				}
				if (rsEmpCode.getString("empcode") != null && rsEmpCode.getString("empcode").length() > 0) {
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpCode.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName + rsEmpCode.getString("emp_lname") + " ["
							+ rsEmpCode.getString("empcode") + "]"));
				}
			}
			rsEmpCode.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillEmployeeNameHR(String strEmpId,int org_id, int wlocation, com.konnect.jpms.util.CommonFunctions cF, UtilityFunctions uF) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		// UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery = new StringBuilder();
			// sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id and ud.emp_id=epd.emp_per_id "
			// +
			// "and usertype_id=7 and epd.is_alive= true  and epd.joining_date <= ?");
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id "
					+ "and epd.is_alive= true and epd.joining_date <= ? and emp_per_id != ? and ud.emp_id=epd.emp_per_id and (usertype_id = (select user_type_id "
					+ "from user_type where user_type = '" + ADMIN + "') or (usertype_id = (select user_type_id from user_type where user_type = '" + HRMANAGER
					+ "') ");
			if (org_id > 0) {
				sbQuery.append(" and org_id_access like '%," + org_id + ",%'");
			}
			if (wlocation > 0) {
				sbQuery.append(" and wlocation_id_access like '%," + wlocation + ",%'");
			}
			sbQuery.append(")) order by epd.emp_fname");

			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(cF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(strEmpId));
			// System.out.println("pst Of HR ===>> " + pst);
			rsEmpCode = pst.executeQuery();
			al.add(new FillEmployee("0", "NO HR"));
			while (rsEmpCode.next()) {
				if (rsEmpCode.getInt("emp_per_id") < 0) {
					continue;
				}
				if (rsEmpCode.getString("empcode") != null && rsEmpCode.getString("empcode").length() > 0) {
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpCode.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
				}
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillTrainingEmployee(String f_org, String[] f_strWLocation, String[] f_department, String[] f_service, String[] f_level,
		String[] f_desig, String[] f_grade) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuffer sbQuery = new StringBuffer();
				sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive = true ");
				if(f_level!=null && f_level.length>0 && !f_level.equals("null")){
	                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(f_level, ",")+") ) ");
	            }
	            if(f_department!=null && f_department.length>0 && !f_department.equals("null")) {
	                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(f_department, ",")+") ");
	            }
	            
	            if(f_service!=null && f_service.length>0 && !f_service.equals("null")){
	                sbQuery.append(" and (");
	                for(int i=0; i<f_service.length; i++){
	                    sbQuery.append(" eod.service_id like '%,"+f_service[i]+",%'");
	                    if(i<f_service.length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            }
	            
	            if (f_desig!=null && f_desig.length>0 && !f_desig.equals("null")) {
					sbQuery.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  where designation_id in (" + StringUtils.join(f_desig, ",") + ") ))");
				}
				if (f_grade != null && f_grade.length>0 && !f_grade.equals("null")) {
					sbQuery.append(" and grade_id in (" + StringUtils.join(f_grade, ",") + ")");
				}
	            
	            if(f_strWLocation!=null && f_strWLocation.length>0 && !f_strWLocation.equals("null")){
	                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(f_strWLocation, ",")+") ");
	            }
	            
	            if(uF.parseToInt(f_org)>0){
					sbQuery.append(" and eod.org_id = "+uF.parseToInt(f_org));
				}
				sbQuery.append("  order by epd.emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===> "+pst);
				rsEmpCode = pst.executeQuery();
				while (rsEmpCode.next()) {
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpCode.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["+ rsEmpCode.getString("empcode") + "]"));
				}
				rsEmpCode.close();
				pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillTrainingEmployee(String skillsIds, String attribIds, String alignWith, String f_org, String[] f_strWLocation, String[] f_department, String[] f_service, String[] f_level,
			String[] f_desig, String[] f_grade) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive = true and emp_per_id in (select emp_id from training_gap_details where emp_id > 0 ");
			if(attribIds != null && attribIds.length()>0) {
				sbQuery.append(" and (attribute_id in ("+attribIds+")");
				List<String> attribList = Arrays.asList(attribIds.split(","));
	                sbQuery.append(" or ");
	                for(int i=0; i<attribList.size(); i++) {
	                    sbQuery.append(" learning_attribute_ids like '%,"+attribList.get(i) +",%'");
	                    if(i<attribList.size()-1) {
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(")");
			}
			
//			if(skillsIds != null && skillsIds.length()>0) {
//				List<String> skillList = Arrays.asList(skillsIds.split(","));
//	                sbQuery.append(" and (");
//	                for(int i=0; i<skillList.size(); i++) {
//	                    sbQuery.append(" learning_skill_ids like '%,"+skillList.get(i) +",%'");
//	                    if(i<skillList.size()-1) {
//	                        sbQuery.append(" OR "); 
//	                    }
//	                }
//	                sbQuery.append(")");
//			}
			sbQuery.append(")");
			
			if(skillsIds != null && skillsIds.length()>0) {
	            sbQuery.append(" and emp_id in (select emp_id from skills_description where skill_id > 0 and skill_id in ("+skillsIds+"))");
			}
			
			if(f_level!=null && f_level.length>0) {
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(f_level, ",")+") ) ");
            }
            if(f_department!=null && f_department.length>0) {
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(f_department, ",")+") ");
            }
            
            if(f_service!=null && f_service.length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<f_service.length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+f_service[i]+",%'");
                    if(i<f_service.length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
            
            if (f_desig!=null && f_desig.length>0) {
				sbQuery.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  where designation_id in (" + StringUtils.join(f_desig, ",") + ") ))");
			}
			if (f_grade != null && f_grade.length>0) {
				sbQuery.append(" and grade_id in (" + StringUtils.join(f_grade, ",") + ")");
			}
            
            if(f_strWLocation!=null && f_strWLocation.length>0) {
                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(f_strWLocation, ",")+") ");
            }
            
            if(uF.parseToInt(f_org)>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(f_org));
			}
			sbQuery.append("  order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> "+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillSupervisorEmployee(String depart, String level, String strUserType, String strEmpId) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
				pst = con.prepareStatement(selectSupervisor_OName);
				pst.setString(1, EMPLOYEE);
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
				pst = con.prepareStatement(selectSupervisor_OName_HRManager);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setString(2, EMPLOYEE);
			} else {
				pst = con.prepareStatement(selectSupervisor_OName);
				pst.setString(1, EMPLOYEE);
			}
			rsEmpCode = pst.executeQuery();
	//===start parvez date: 01-08-2022===		
			al.add(new FillEmployee("0", "No Supervisor"));
			al.add(new FillEmployee("0", "No Manager"));
	//===end parvez date: 01-08-2022===		
			while (rsEmpCode.next()) {
				if (rsEmpCode.getInt("emp_per_id") < 0) {
					continue;
				}
				if (rsEmpCode.getString("empcode") != null && rsEmpCode.getString("empcode").length() > 0) {
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpCode.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
							+ rsEmpCode.getString("empcode") + "]"));
				}
			}
			rsEmpCode.close();
			pst.close();
			
			
			StringBuffer sbQuery = new StringBuffer();
//			sbQuery.append("select empcode,emp_fname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			
			sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd, user_details ud " +
					"where is_alive = true and epd.emp_per_id = ud.emp_id and wlocation_id = (select wlocation_id from employee_official_details where " +
					"emp_id = " + strEmpId + ") and epd.emp_per_id=eod.emp_id and usertype_id = (select user_type_id from user_type " +
					"where user_type = '" + EMPLOYEE + "') ");
			
			if(depart!=null && !depart.equals("")) {
                sbQuery.append(" and eod.depart_id in (" + depart + ") ");
            }
			if (level != null && !level.equals("")) {
				sbQuery.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id in ("
						+ level + "))) ");
			}
			sbQuery.append("  order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
//			System.out.println("SupervisorEmployee pst ===> "+pst);
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillEmployee> fillEmployeeNameByAccess(String strOrgId, String strOrgAccess, String strWLocation, String strWLocationAccess, String strUserType, boolean isWithCode) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			
			if(uF.parseToInt(strOrgId)>0) {
				if(strWLocation!=null && !strWLocation.equals("null") && !strWLocation.equals("")) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
						"and org_id=? and wlocation_id in ("+ strWLocation + ")  order by emp_fname, emp_lname");
					pst.setInt(1, uF.parseToInt(strOrgId));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + ((isWithCode)?""+" [" + rs.getString("empcode") + "]":""),""));
					}
					rs.close();
					pst.close();
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strWLocationAccess!=null && !strWLocationAccess.equals("null") && !strWLocationAccess.equals("")) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
						"and org_id=? and wlocation_id in ("+ strWLocationAccess + ")order by emp_fname,emp_mname, emp_lname");
					pst.setInt(1, uF.parseToInt(strOrgId));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + ((isWithCode)?""+" [" + rs.getString("empcode") + "]":""),""));
					}
					rs.close();
					pst.close();
				} else {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
							"and org_id=? order by emp_fname, emp_lname");
					pst.setInt(1, uF.parseToInt(strOrgId));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + ((isWithCode)?""+" [" + rs.getString("empcode") + "]":""),""));
					}
					rs.close();
					pst.close();
				}
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strOrgAccess!=null) {
				if(strWLocation!=null && !strWLocation.equals("null") && !strWLocation.equals("")) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
						"and  org_id in ("+strOrgAccess+") and wlocation_id in ("+ strWLocation + ")  order by emp_fname,emp_mname, emp_lname");
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + ((isWithCode)?""+" [" + rs.getString("empcode") + "]":""),""));
					}
					rs.close();
					pst.close();
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strWLocationAccess!=null && !strWLocationAccess.equals("null") && !strWLocationAccess.equals("")) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
						"and  org_id in ("+strOrgAccess+") and wlocation_id in ("+ strWLocationAccess + ")order by emp_fname,emp_mname, emp_lname");
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + ((isWithCode)?""+" [" + rs.getString("empcode") + "]":""),""));
					}
					rs.close();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}

	public List<FillEmployee> fillTrainingProbationEmployee(String f_org, String[] f_strWLocation, String[] f_department, String[] f_service, String[] f_level,
			String[] f_desig, String[] f_grade) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuffer sbQuery = new StringBuffer();
			
				sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive = true and emp_status = '"+PROBATION+"'");

				if(f_level!=null && f_level.length>0){
	                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(f_level, ",")+") ) ");
	            }
	            if(f_department!=null && f_department.length>0) {
	                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(f_department, ",")+") ");
	            }
	            
	            if(f_service!=null && f_service.length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<f_service.length; i++){
	                    sbQuery.append(" eod.service_id like '%,"+f_service[i]+",%'");
	                    if(i<f_service.length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            }
	            
	            if (f_desig!=null && f_desig.length>0) {
					sbQuery.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  where designation_id in (" + StringUtils.join(f_desig, ",") + ") ))");
				}
				if (f_grade != null && f_grade.length>0) {
					sbQuery.append(" and grade_id in (" + StringUtils.join(f_grade, ",") + ")");
				}
	            if(f_strWLocation!=null && f_strWLocation.length>0){
	                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(f_strWLocation, ",")+") ");
	            }
	            if(uF.parseToInt(f_org)>0){
					sbQuery.append(" and eod.org_id = "+uF.parseToInt(f_org));
				}
				sbQuery.append("  order by epd.emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===> "+pst);
				rsEmpCode = pst.executeQuery();
				while (rsEmpCode.next()) {
					
					String strEmpMName = "";
					
					if(flagMiddleName) {
						if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpCode.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["+ rsEmpCode.getString("empcode") + "]"));
				}
				rsEmpCode.close();
				pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillTrainingInductionEmployee(String f_org, String[] f_strWLocation, String[] f_department, String[] f_service, String[] f_level,
			String[] f_desig, String[] f_grade, String strCurrDate, String strPrevDate) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuffer sbQuery = new StringBuffer();
				sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and joining_date between ? and ? and epd.is_alive = true");
				if(f_level!=null && f_level.length>0){
	                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(f_level, ",")+") ) ");
	            }
	            if(f_department!=null && f_department.length>0) {
	                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(f_department, ",")+") ");
	            }
	            if(f_service!=null && f_service.length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<f_service.length; i++){
	                    sbQuery.append(" eod.service_id like '%,"+f_service[i]+",%'");
	                    if(i<f_service.length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            }
	            
	            if (f_desig!=null && f_desig.length>0) {
					sbQuery.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  where designation_id in (" + StringUtils.join(f_desig, ",") + ") ))");
				}
				if (f_grade != null && f_grade.length>0) {
					sbQuery.append(" and grade_id in (" + StringUtils.join(f_grade, ",") + ")");
				}
	            if(f_strWLocation!=null && f_strWLocation.length>0){
	                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(f_strWLocation, ",")+") ");
	            }
	            if(uF.parseToInt(f_org)>0){
					sbQuery.append(" and eod.org_id = "+uF.parseToInt(f_org));
				}
				sbQuery.append("  order by epd.emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPrevDate, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strCurrDate, DATE_FORMAT));
//				System.out.println("pst ===> "+pst);
				rsEmpCode = pst.executeQuery();
				while (rsEmpCode.next()) {
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpCode.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["+ rsEmpCode.getString("empcode") + "]"));
				}
				rsEmpCode.close();
				pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillLiveEmployeeName() {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive=true order by epd.emp_fname");
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillEmployee> fillEmployeeNameByOrg(int nOrgId) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.org_id=? and epd.is_alive=true order by epd.emp_fname");
			pst.setInt(1, nOrgId);
//			System.out.println("FE/2738--pst="+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;

	}
	
	
	public List<FillEmployee> fillProjectEmployeeName(String strEmpId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpName = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if(strEmpId != null && !strEmpId.equals("")) {
				pst = con.prepareStatement("SELECT * FROM employee_personal_details WHERE is_alive = true and emp_per_id in ("+strEmpId+") order by emp_fname");
				rsEmpName = pst.executeQuery();
				while (rsEmpName.next()) {
					if (rsEmpName.getInt("emp_per_id") < 0) {
						continue;
					}
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rsEmpName.getString("emp_mname");
						}
					}
					al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") + strEmpMName+ " " + rsEmpName.getString("emp_lname"), ""));
				}
				rsEmpName.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rsEmpName);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillEmployee> fillGlobalHRAndHRName(String orgId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbque = new StringBuilder();
			sbque.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id " +
			" and eod.emp_id = ud.emp_id and epd.is_alive=true and (usertype_id = 7 or usertype_id = 1 or usertype_id = 2 or usertype_id = 11 or " +
			"usertype_id = 13 or usertype_id = 5) and eod.org_id = ?");//Created Dattatray Date:25-08-21 Note: usertype_id = 13 or usertype_id = 5 added
			sbque.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbque.toString());
			pst.setInt(1, uF.parseToInt(orgId));
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
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
	
	
	public List<FillEmployee> fillAllLiveEmployees(com.konnect.jpms.util.CommonFunctions CF, String strUserType, String strEmpId, int nOrgId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null, pst1 = null;
		ResultSet rsEmpName=null,rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			Map<String,String> hmUserLocationAccessMap = new HashMap<String,String>();
			Map<String,String> hmUserLocationMap = new HashMap<String,String>();
			StringBuilder sQuery = new StringBuilder();
			
			sQuery.append("select * from employee_official_details eod,user_details ud where eod.emp_id = ud.emp_id and status='ACTIVE' " +
			"and (usertype_id =7 or usertype_id =1 or usertype_id = 4) and eod.emp_id in(select emp_per_id from employee_personal_details where is_alive=true)  order by user_id desc");
			pst = con.prepareStatement(sQuery.toString());
			rst = pst.executeQuery();
			while(rst.next()){
				List<String> locationList = new ArrayList<String>();
				String accessLocations = (String)rst.getString("wlocation_id_access");
				if(accessLocations!= null && !accessLocations.equals("")){
					locationList = Arrays.asList(accessLocations.split(","));
				}else{
					locationList.add(rst.getString("wlocation_id"));
				}	
				
				StringBuilder sb = null;
				for(String loc : locationList){
					if(loc!=null && !loc.equals("")){
						if(sb == null){
							sb = new StringBuilder();
							sb.append(loc);
						}else{
							sb.append(","+loc);
						}
					}
				}
				
//				System.out.println("empId==>"+rst.getString("emp_id")+"==>locationAccess==>"+sb.toString());
				hmUserLocationAccessMap.put(rst.getString("emp_id"),sb.toString());
				hmUserLocationMap.put(rst.getString("emp_id"),rst.getString("wlocation_id"));
			}
			
			rst.close();
			pst.close();
			
			if(nOrgId>0){
//				System.out.println("strUserType==>"+strUserType+"==>strEmpId==>"+strEmpId+"==>nOrgId==>"+nOrgId);
				if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
					pst1 = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod where eod.emp_id = epd.emp_per_id and epd.emp_status!='TERMINATED' and "+
							"epd.emp_per_id not in (select emp_id from emp_off_board eob where approved_1=1 and approved_2=1) and eod.org_id= ? order by emp_fname ");
					pst1.setInt(1, nOrgId);
				} else if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
					pst1 = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod where eod.emp_id = epd.emp_per_id and epd.emp_status!='TERMINATED' and "
							+"eod.wlocation_id in ("+hmUserLocationAccessMap.get(strEmpId)+") and epd.emp_per_id not in (select emp_id from emp_off_board eob where approved_1=1 and approved_2=1)"
							+" and eod.org_id=? order by emp_fname ");
					pst1.setInt(1, nOrgId);
				} else if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER))) {
					pst1 = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod where eod.emp_id = epd.emp_per_id and epd.emp_status!='TERMINATED' and "
					+" and epd.emp_per_id not in (select emp_id from emp_off_board eob where approved_1=1 and approved_2=1) and eod.supervisor_emp_id = ? and eod.org_id=? and epd.joining_date <= ? order by emp_fname ");
					
				    pst1.setInt(1, uF.parseToInt(strEmpId));
					pst1.setInt(2, nOrgId);
					pst1.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				}else {
					pst1 = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod where eod.emp_id = epd.emp_per_id and epd.emp_status!='TERMINATED' and "+
					"epd.emp_per_id not in (select emp_id from emp_off_board eob where approved_1=1 and approved_2=1) and eod.org_id= ? and epd.joining_date <= ? order by emp_fname ");
					pst1.setInt(1, nOrgId);
					pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				} 
			}else{
				pst1 = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod where eod.emp_id = epd.emp_per_id and epd.emp_status!='TERMINATED' and "
						+" epd.emp_per_id not in (select emp_id from emp_off_board eob where approved_1=1 and approved_2=1) and epd.joining_date <= ? order by emp_fname ");
				pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			}
			System.out.println("pst1==>"+pst1);
			rsEmpName = pst1.executeQuery();
			while (rsEmpName.next()) {
				if (rsEmpName.getInt("emp_per_id") <= 0) {
					continue;
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpName.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") + strEmpMName +" "+ rsEmpName.getString("emp_lname"), ""));
			}
			rsEmpName.close();
			pst1.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpName);
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
		return al;
	}
	
	public List<FillEmployee> fillCafeEmployees(String strUserType,String wlocations) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpName=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
//			System.out.println("strUserType==>"+strUserType+"==>wlocations==>"+wlocations);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where eod.emp_id = epd.emp_per_id and epd.emp_status!='TERMINATED' and is_alive=true ");
						
			if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
				sbQuery.append(" and wlocation_id in ("+wlocations+") ");
			} 
			sbQuery.append(" order by emp_fname ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1==>"+pst);
			rsEmpName = pst.executeQuery();
			while (rsEmpName.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpName.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") + strEmpMName+ " " + rsEmpName.getString("emp_lname") + " [" + rsEmpName.getString("empcode") + "]",""));
			}
			rsEmpName.close();
			pst.close();
			
//			System.out.println("al==>"+al);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpName);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	public List<FillEmployee> fillEmployeeNameForAttendance(String strD1, String strD2, int level_id, int org_id, int location_id) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id "
					+ " and  epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?))");

			if (org_id > 0) {
				sbQuery.append(" and eod.org_id=" + org_id);
			}
			if (location_id > 0) {
				sbQuery.append(" and eod.wlocation_id=" + location_id);
			}

			if (level_id > 0) {
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in "
						+ " (SELECT designation_id FROM designation_details  WHERE level_id =" + level_id + ")) ");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pstfill Emp===>"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname"), ""));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;
	}
	
	public List<FillEmployee> fillManagerByorg(int nOrgId) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("SELECT emp_per_id,emp_fname,emp_mname,emp_lname FROM employee_personal_details where emp_per_id "
					+ "in (select distinct(supervisor_emp_id) from employee_official_details where supervisor_emp_id>0 and org_id=?)" + " order by emp_fname");
			pst.setInt(1, nOrgId);
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;

	}
	
	public List<FillEmployee> fillEmployeeNameByAccess(String strOrgId, String strOrgAccess, String strWLocation, String strWLocationAccess,
			String strUserType, boolean isWithCode,String strStartDate, String strEndDate) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			if (uF.parseToInt(strOrgId) > 0) {
				if (strWLocation != null && !strWLocation.equals("null") && !strWLocation.equals("")) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_delete=false"
							+ " and org_id=? and wlocation_id in (" + strWLocation + ") " +
							" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) order by emp_fname, emp_lname");
					pst.setInt(1, uF.parseToInt(strOrgId));
					pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
					System.out.println("FE/3148---pst="+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname")
								+ ((isWithCode) ? "" + " [" + rs.getString("empcode") + "]" : ""), ""));
					}
					rs.close();
					pst.close();
				} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && strWLocationAccess != null && !strWLocationAccess.equals("null")
						&& !strWLocationAccess.equals("")) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_delete=false "
							+ "and org_id=? and wlocation_id in (" + strWLocationAccess + ") " +
							"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) order by emp_fname, emp_lname");
					pst.setInt(1, uF.parseToInt(strOrgId));
					pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname")
								+ ((isWithCode) ? "" + " [" + rs.getString("empcode") + "]" : ""), ""));
					}
					rs.close();
					pst.close();
				} else {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_delete=false "
							+ "and org_id=? and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) " +
							"order by emp_fname, emp_lname");
					pst.setInt(1, uF.parseToInt(strOrgId));
					pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname")
								+ ((isWithCode) ? "" + " [" + rs.getString("empcode") + "]" : ""), ""));
					}
					rs.close();
					pst.close();
				}
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && strOrgAccess != null) {
				if (strWLocation != null && !strWLocation.equals("null") && !strWLocation.equals("")) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_delete=false "
							+ "and  org_id in (" + strOrgAccess + ") and wlocation_id in (" + strWLocation + ") " +
							"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) order by emp_fname, emp_lname");
					pst.setDate(1, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strEndDate, DATE_FORMAT));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname")
								+ ((isWithCode) ? "" + " [" + rs.getString("empcode") + "]" : ""), ""));
					}
					rs.close();
					pst.close();
				} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && strWLocationAccess != null && !strWLocationAccess.equals("null")
						&& !strWLocationAccess.equals("")) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_delete=false "
							+ "and  org_id in (" + strOrgAccess + ") and wlocation_id in (" + strWLocationAccess + ") " +
							"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) order by emp_fname, emp_lname");
					pst.setDate(1, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strEndDate, DATE_FORMAT));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname")
								+ ((isWithCode) ? "" + " [" + rs.getString("empcode") + "]" : ""), ""));
					}
					rs.close();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}
	
	
	public List<FillEmployee> fillRecruiterName(String orgId) {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			
			StringBuilder sbque = new StringBuilder();
			sbque.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id " +
				" and eod.emp_id = ud.emp_id and epd.is_alive=true and usertype_id = 11 and eod.org_id = ?"); // (usertype_id = 7 or usertype_id = 1 or usertype_id = 2 or usertype_id = 11)
			sbque.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbque.toString());
			pst.setInt(1, uF.parseToInt(orgId));
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
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

	
	public List<FillEmployee> fillReviewerNameByLocation(String userlocation) {
		ArrayList<FillEmployee> reviewerNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where" +
				" epd.emp_per_id=eod.emp_id and epd.emp_per_id = ud.emp_id and ud.usertype_id in " +
				"(select user_type_id from user_type where user_type in ('"+HOD+"','"+ADMIN+"','"+HRMANAGER+"','"+MANAGER+"','"+CEO+"','"+CFO+"')) ");
			if(userlocation != null && userlocation.length()>0) {
				sbQuery.append(" and wlocation_id in (" + userlocation + ") ");
			}
			sbQuery.append("order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			// pst.setInt(1, uF.parseToInt(userlocation));
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				reviewerNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return reviewerNamesList;
	}
	
	
	public List<FillEmployee> fillGhrHrCeoAndCfo() {
		ArrayList<FillEmployee> employeeList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where" +
				" epd.emp_per_id=eod.emp_id and epd.emp_per_id = ud.emp_id and ud.usertype_id in (select user_type_id from user_type " +
				"where user_type in ('"+ADMIN+"','"+HRMANAGER+"','"+CEO+"','"+CFO+"')) order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				employeeList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return employeeList;
	}

	
	public Boolean getFeatureStatusName(String strFeatureName) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select feature_name,feature_status,user_type_id,emp_ids from feature_management where feature_name=?");
			pst.setString(1, strFeatureName);
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
	
	//===added by parvez date: 19-10-2021===
	//===start===
	public List<FillEmployee> fillEmployeeNameForRecruitment(int nOrgId) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where " +
					"epd.emp_per_id=eod.emp_id and eod.org_id=? and epd.is_alive=true and eod.emp_id not in" +
					"(select emp_id from emp_off_board where approved_1=1 and approved_2=1) order by epd.emp_fname");
			pst.setInt(1, nOrgId);
//			System.out.println("FE/3419--pst="+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
			db.closeConnection(con);
		}
		return al;

	}
	
	public List<FillEmployee> fillEmployeeNameByLocForRecruitment(String userlocation) {
		ArrayList<FillEmployee> empNamesList = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			boolean flagMiddleName=getFeatureStatusName(F_SHOW_EMPLOYEE_MIDDLE_NAME);
			
			pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id in ("
					+ userlocation + ") and is_alive = true and eod.emp_id not in (select emp_id from emp_off_board where approved_1=1 and approved_2=1) order by epd.emp_fname");
//			System.out.println("FE/3463--pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				empNamesList.add(new FillEmployee(rs.getString("emp_per_id"), rs.getString("emp_fname") + strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empNamesList;
	}
	
	//===end===
	
	
}