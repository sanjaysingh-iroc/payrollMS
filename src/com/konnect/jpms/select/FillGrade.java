package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillGrade implements IStatements {

	String gradeId;
	String gradeCode;

	public FillGrade(String gradeId, String gradeCode) {
		this.gradeId = gradeId;
		this.gradeCode = gradeCode;
	}
	HttpServletRequest request;
	
	public FillGrade(HttpServletRequest request) {
		this.request = request;
	}
	public FillGrade() {
	}

	
	public List<FillGrade> fillGrade() {

		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectGrade1);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillGrade> fillGradeFromDesignation(String designationId) {

		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			if(designationId != null && !designationId.equals("")) {
//				pst = con.prepareStatement(selectGradeFromDesignation);
//				pst.setInt(1, uF.parseToInt(designationId));
				pst = con.prepareStatement("SELECT * FROM grades_details where designation_id in (" + designationId + ") order by grade_code");	
			} else {
				pst = con.prepareStatement("SELECT * FROM grades_details order by grade_code");
			}
			rs = pst.executeQuery();
//			System.out.println("pst selectGradeFromDesignation==>" + pst); 
			while (rs.next()) {
				al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillGrade> fillGradeFromMultipleDesignation(String designationId) {

		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			if(designationId.length()>0 && designationId.substring(0, 1).equals(",") && designationId.substring(designationId.length()-1, designationId.length()).equals(",")){
				designationId = designationId.substring(1, designationId.length()-1);
			} else if(designationId.length()>0 && designationId.substring(designationId.length()-1, designationId.length()).equals(",")){
				designationId = designationId.substring(0, designationId.length()-1);
			}else if(designationId.length()>0 && designationId.substring(0, 1).equals(",")){
				designationId = designationId.substring(1, designationId.length());
			}
			con = db.makeConnection(con);
			if(designationId != null && !designationId.equals("")) {
				pst = con.prepareStatement("SELECT * FROM grades_details where designation_id in (" + designationId + ") order by grade_code");	
			} else {
				pst = con.prepareStatement("SELECT * FROM grades_details order by grade_code");
			}
			rs = pst.executeQuery();
//			System.out.println("pst selectGradeFromDesignation==>" + pst); 
			while (rs.next()) {
				al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String getGradeId() {
		return gradeId;
	}

	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}

	public String getGradeCode() {
		return gradeCode;
	}

	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}
	
	
	public List<FillGrade> fillGradeByOrg(int orgId) {

		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(orgId > 0) {
				pst = con.prepareStatement("SELECT * FROM grades_details where designation_id in(SELECT designation_id FROM designation_details ald RIGHT JOIN level_details ld ON ald.level_id = ld.level_id where org_id = ?)");
				pst.setInt(1, orgId);
			} else {
				pst = con.prepareStatement("SELECT * FROM grades_details order by grade_code");
			}
			//System.out.println("pst =====> " + pst);
			
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	public List<FillGrade> fillGradeFromEmpDesignation(int nEmpId) {

		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			if(nEmpId > 0){
				pst = con.prepareStatement("SELECT * FROM grades_details where designation_id in (select designation_id from grades_details " +
						"where grade_id in (select grade_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
						"and epd.emp_per_id=?)) order by grade_code");
				pst.setInt(1, nEmpId);
				rs = pst.executeQuery();
				while (rs.next()) {
					al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
				}
				rs.close();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	public List<FillGrade> fillGradeFromEmpDesignationWithoutCurrentGrade(int nEmpId) {
//		System.out.println("nEmpId==>"+nEmpId);
		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			if(nEmpId > 0){
				pst = con.prepareStatement("SELECT * FROM grades_details where designation_id in (select designation_id from grades_details " +
						"where grade_id in (select grade_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
						"and epd.emp_per_id=?)) and grade_id not in (select grade_id from employee_personal_details epd, employee_official_details eod " +
						"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?) order by grade_code");
				/*pst = con.prepareStatement("select * from (SELECT * FROM grades_details where designation_id in (select designation_id from "
						+" grades_details where grade_id in (select grade_id from employee_personal_details epd, employee_official_details eod"
						+" where epd.emp_per_id=eod.emp_id and epd.emp_per_id = ?))) as a, grades_details gd where a.grade_id=gd.grade_id and "
						+" gd.grade_id not in (select grade_id from employee_personal_details epd, employee_official_details eod"
						+" where epd.emp_per_id=eod.emp_id and epd.emp_per_id = ?) order by gd.grade_code");*/
				pst.setInt(1, nEmpId);
				pst.setInt(2, nEmpId);
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
				}
				rs.close();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	public List<FillGrade> fillGradeByOrgLevel(int orgId, int levelId) {

		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM grades_details where designation_id in(SELECT designation_id FROM designation_details ald " +
					"where ald.level_id > 0 and ald.level_id in(select level_id from level_details ld where org_id=? ");
			if(levelId > 0) {
				sbQuery.append(" and ld.level_id="+levelId);
			}
			sbQuery.append("))");	
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, orgId);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
			}
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return al;
	}
	
	public List<FillGrade> fillGrade(String Levelid, String orgid) {

		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			if(Levelid != null && Levelid.length()>0) {
				pst = con.prepareStatement("select  * from grades_details gd, designation_details dd, level_details ld where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id and ld.level_id in ("+Levelid+") and ld.org_id="+orgid);
			} else {
				pst = con.prepareStatement("select  * from grades_details gd, designation_details dd, level_details ld where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id and ld.org_id="+orgid);
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	public List<FillGrade> fillGradebyorganisation(String orgid) {

		List<FillGrade> al = new ArrayList<FillGrade>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			if(uf.parseToInt(orgid)>0)
			{
				pst = con.prepareStatement("select  * from grades_details gd, designation_details dd, level_details ld where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id  and ld.org_id="+orgid);
			}else{
				pst = con.prepareStatement("SELECT * FROM grades_details order by grade_code");				
			}			
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillGrade(rs.getString("grade_id"), "["+rs.getString("grade_code")+"] "+rs.getString("grade_name")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
}