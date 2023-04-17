package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillAnswerType implements IStatements {

	private String id;
	private String name;

	public FillAnswerType(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
 
	HttpServletRequest request;
	public FillAnswerType(HttpServletRequest request) {
		this.request = request; 
	}
	public FillAnswerType() {

		// TODO Auto-generated constructor stub
	}

	public List<FillAnswerType> fillAnswerType() {

		List<FillAnswerType> al = new ArrayList<FillAnswerType>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillAnswerType(rs.getString("appraisal_answer_type_id"), rs.getString("appraisal_answer_type_name")));
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
