package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillOfficeType implements IStatements {

	String officeTypeId;
	String officeTypeName;

	public FillOfficeType(String officeTypeId, String officeTypeName) {
		this.officeTypeId = officeTypeId;
		this.officeTypeName = officeTypeName;
	}
	HttpServletRequest request;
	public FillOfficeType(HttpServletRequest request) {
		this.request = request;
	}
	public FillOfficeType() {
	}

	public List<FillOfficeType> fillOfficeType() {

		List<FillOfficeType> al = new ArrayList<FillOfficeType>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from office_type order by location_office_type_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillOfficeType(rs.getString("location_office_type_id"), rs.getString("office_type")));
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
