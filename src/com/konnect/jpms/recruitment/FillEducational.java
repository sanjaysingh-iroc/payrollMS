package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.util.Database;

public class FillEducational {

	private FillEducational(String eduId, String eduName) {
		this.eduId = eduId;
		this.eduName = eduName;
	}

	public FillEducational() {
	}
	HttpServletRequest request;
	public FillEducational(HttpServletRequest request) {
		this.request=request;
	}

	String eduName;

	String eduId;   

	public List<FillEducational> fillEducationalQual() {

		List<FillEducational> al = new ArrayList<FillEducational>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from educational_details where education_name != '' order by education_name");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				al.add(new FillEducational(rs.getString("edu_id"), rs.getString("education_name")));
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

	public String getEduName() {
		return eduName;
	}

	public void setEduName(String eduName) {
		this.eduName = eduName;
	}

	public String getEduId() {
		return eduId;
	}

	public void setEduId(String eduId) {
		this.eduId = eduId;
	}

}
