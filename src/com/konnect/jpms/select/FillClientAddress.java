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

public class FillClientAddress implements IStatements{

	String clientAddressId;
	String clientAddress;
	
	private FillClientAddress(String clientAddressId, String clientAddress) {
		this.clientAddressId = clientAddressId;
		this.clientAddress = clientAddress;
	}
	
	HttpServletRequest request;
	public FillClientAddress(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillClientAddress() {
	}
	 
	public List<FillClientAddress> fillClientAddress(String clientId,UtilityFunctions uF){
		List<FillClientAddress> al = new ArrayList<FillClientAddress>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_address where client_id=?");
			pst.setInt(1, uF.parseToInt(clientId));
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClientAddress(rs1.getString("client_address_id"), rs1.getString("client_address")));
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

	public String getClientAddressId() {
		return clientAddressId;
	}

	public void setClientAddressId(String clientAddressId) {
		this.clientAddressId = clientAddressId;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
}
