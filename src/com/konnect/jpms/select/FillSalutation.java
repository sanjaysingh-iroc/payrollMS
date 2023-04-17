package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillSalutation implements IStatements{

	String salutationId;
	String salutationName;
	
	
	
	public FillSalutation(String salutationId, String salutationName) {
		this.salutationId = salutationId;
		this.salutationName = salutationName;
	}
	HttpServletRequest request;
	public FillSalutation(HttpServletRequest request) {
		this.request = request;
	}
	public FillSalutation() {
	}
	
	public List<FillSalutation> fillSalutation(){
		List<FillSalutation> al = new ArrayList<FillSalutation>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from salutation order by salutation_code");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillSalutation(rs1.getString("salutation_code"), rs1.getString("salutation_code")));
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	} 
	
	public String getSalutationId() {
		return salutationId;
	}

	public void setSalutationId(String salutationId) {
		this.salutationId = salutationId;
	}

	public String getSalutationName() {
		return salutationName;
	}

	public void setSalutationName(String salutationName) {
		this.salutationName = salutationName;
	}
}