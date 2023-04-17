package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;

public class FillClientBrand {

	String clientBrandId;
	String clientBrandName;
	
	HttpServletRequest request;
	public FillClientBrand(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillClientBrand(){}
	
	public FillClientBrand(String clientBrandId, String clientBrandName) {
		this.clientBrandId = clientBrandId;
		this.clientBrandName = clientBrandName;
	}
	
	
	public List<FillClientBrand> fillClientBrand(int clientId) {
		
		List<FillClientBrand> al = new ArrayList<FillClientBrand>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select client_brand_id,client_brand_name from client_brand_details where client_id=?");
			pst.setInt(1, clientId);
			rs = pst.executeQuery();
			al.add(new FillClientBrand("0", "For Client"));
			while(rs.next()){
				if(rs.getString("client_brand_name") != null && !rs.getString("client_brand_name").equals("")) {
					al.add(new FillClientBrand(rs.getString("client_brand_id"), rs.getString("client_brand_name")));
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
	
	
	public List<FillClientBrand> fillClientBrands(int clientId) {
		
		List<FillClientBrand> al = new ArrayList<FillClientBrand>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select client_brand_id,client_brand_name from client_brand_details where client_id=?");
			pst.setInt(1, clientId);
			rs = pst.executeQuery();
//			al.add(new FillClientBrand("0", "For Client"));
			while(rs.next()) {
				if(rs.getString("client_brand_name") != null && !rs.getString("client_brand_name").equals("")) {
					al.add(new FillClientBrand(rs.getString("client_brand_id"), rs.getString("client_brand_name")));
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

	public String getClientBrandId() {
		return clientBrandId;
	}

	public void setClientBrandId(String clientBrandId) {
		this.clientBrandId = clientBrandId;
	}

	public String getClientBrandName() {
		return clientBrandName;
	}

	public void setClientBrandName(String clientBrandName) {
		this.clientBrandName = clientBrandName;
	}
	
	
	
}
