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

public class FillClientPoc implements IStatements{

	String clientPocId;
	String clientPocName;
	private FillClientPoc(String clientPocId, String clientPocName) {
		this.clientPocId = clientPocId;
		this.clientPocName = clientPocName;
	}
	
	HttpServletRequest request;
	public FillClientPoc(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillClientPoc() {
	}
	 
	
	public List<FillClientPoc> fillClientPoc() {
		List<FillClientPoc> al = new ArrayList<FillClientPoc>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectClientPOC);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClientPoc(rs1.getString("client_id"), uF.showData(rs1.getString("contact_fname"), "N/A")+" "+uF.showData(rs1.getString("contact_lname"), "N/A")));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillClientPoc> fillClientPoc(String strClientId) {
		List<FillClientPoc> al = new ArrayList<FillClientPoc>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_poc where client_id=? and (client_brand_id=0 or client_brand_id is null) order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strClientId));
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClientPoc(rs1.getString("poc_id"), uF.showData(rs1.getString("contact_fname"), "N/A")+" "+uF.showData(rs1.getString("contact_lname"), "N/A")));
			}
			rs1.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillClientPoc> fillClientBrandPoc(String strClientBrandId) {
		List<FillClientPoc> al = new ArrayList<FillClientPoc>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_poc where client_brand_id=? order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strClientBrandId));
			rs1 = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			while (rs1.next()) {
				al.add(new FillClientPoc(rs1.getString("poc_id"), uF.showData(rs1.getString("contact_fname"), "N/A")+" "+uF.showData(rs1.getString("contact_lname"), "N/A")));
			}
			rs1.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String getClientPocId() {
		return clientPocId;
	}

	public void setClientPocId(String clientPocId) {
		this.clientPocId = clientPocId;
	}

	public String getClientPocName() {
		return clientPocName;
	}

	public void setClientPocName(String clientPocName) {
		this.clientPocName = clientPocName;
	}
	
}  
