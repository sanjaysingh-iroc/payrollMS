package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillSubject implements IStatements {

	String subjectId;
	String subjectName;

	public FillSubject(String subjectId, String subjectName) {
		this.subjectId = subjectId;
		this.subjectName = subjectName;
	}

	
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}


	HttpServletRequest request;
	public FillSubject(HttpServletRequest request) {
		this.request = request;
	}
	public FillSubject() {
	}

	public List<FillSubject> fillSubjectName() {

		List<FillSubject> al = new ArrayList<FillSubject>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from course_subject_details");
			rst= pst.executeQuery();
			while (rst.next()) {
				al.add(new FillSubject(rst.getString("course_subject_id"), rst.getString("course_subject_name")));
			}	
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;

	}

//	public List<FillEmployee> fillEmployeeName(int gradeId, int location) {
//
//		List<FillEmployee> al = new ArrayList<FillEmployee>();
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rsEmpCode = null;
//		Database db = new Database();
//		db.setRequest(request);
//		try {
//
//			con = db.makeConnection(con);
//
//			pst = con
//					.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and grade_id=? and wlocation_id=? order by epd.emp_fname");
//			pst.setInt(1, gradeId);
//			pst.setInt(2, location);
//			rsEmpCode = pst.executeQuery();
//			while (rsEmpCode.next()) {
//
//				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") + " " + rsEmpCode.getString("emp_lname") + " ["
//						+ rsEmpCode.getString("empcode") + "]"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rsEmpCode);
//		}
//		return al;
//
//	}


}
