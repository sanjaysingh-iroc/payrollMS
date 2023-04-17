package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;

public class FillEducation {
	String eduId;
	String eduName;
	public FillEducation(String eduId, String eduName) {
		super();
		this.eduId = eduId;
		this.eduName = eduName;
	}
	HttpServletRequest request;
	public FillEducation(HttpServletRequest request) {
		this.request=request;
	}
	
	
	public List<FillEducation> fillEducationWithId() {

		List<FillEducation> al = new ArrayList<FillEducation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select edu_id,education_name from educational_details order by education_name");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("education_name")!=null && !rs.getString("education_name").trim().equals("")){
					al.add(new FillEducation(rs.getString("edu_id"), rs.getString("education_name")));
				}
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
	
	
	public List<FillEducation> fillEducation() {

		List<FillEducation> al = new ArrayList<FillEducation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select degree_name from education_details group by degree_name order by degree_name");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("degree_name")!=null && !rs.getString("degree_name").trim().equals("")){
					al.add(new FillEducation(rs.getString("degree_name"), rs.getString("degree_name")));
				}
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
	
	
	
	public String getEduId() {
		return eduId;
	}
	public void setEduId(String eduId) {
		this.eduId = eduId;
	}
	public String getEduName() {
		return eduName;
	}
	public void setEduName(String eduName) {
		this.eduName = eduName;
	}
	
}
