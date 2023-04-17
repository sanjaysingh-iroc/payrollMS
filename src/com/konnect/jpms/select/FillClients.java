package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillClients implements IStatements{

	String clientId;
	String clientName;
	
	public FillClients(String clientId, String clientName) {
		this.clientId = clientId;
		this.clientName = clientName;
	}
	HttpServletRequest request;
	public FillClients(HttpServletRequest request) {
		this.request = request;
	}
	public FillClients() {
	}
	
	public List<FillClients> fillClients(boolean isOther){
		List<FillClients> al = new ArrayList<FillClients>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectClients);
			rs1 = pst.executeQuery();
			if(isOther) {
				al.add(new FillClients("0", "Other"));
			}
			while (rs1.next()) {
				al.add(new FillClients(rs1.getString("client_id"), rs1.getString("client_name")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs1);
			db.closeConnection(con);
		}
		return al;
	} 
	
	
	public List<FillClients> fillClients(int nEmpId){
		List<FillClients> al = new ArrayList<FillClients>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectClients1);
//			pst.setInt(1, nEmpId);
			pst = con.prepareStatement("select * from client_details cd where client_id in (select distinct client_id from projectmntnc where pro_id in (" +
					"select distinct pro_id from activity_info where resource_ids like '%,"+nEmpId+",%'))");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClients(rs1.getString("client_id"), rs1.getString("client_name")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs1);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillClients> fillLiveProjectClients(int nEmpId){
		List<FillClients> al = new ArrayList<FillClients>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectClients1);
//			pst.setInt(1, nEmpId);
			pst = con.prepareStatement("select * from client_details cd where client_id in (select distinct client_id from projectmntnc where pro_id in (" +
					"select distinct pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' and approve_status='n'))");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClients(rs1.getString("client_id"), rs1.getString("client_name")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs1);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillClients> fillClientsWithOther(int nEmpId){
		List<FillClients> al = new ArrayList<FillClients>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectClients1);
			pst = con.prepareStatement("select * from client_details cd where client_id in (select distinct client_id from projectmntnc where" +
				" pro_id in (select distinct pro_id from activity_info where resource_ids like '%,"+ nEmpId +",%') and approve_status = 'n')");
//			pst.setInt(1, nEmpId); 
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClients(rs1.getString("client_id"), rs1.getString("client_name")));
			}
			al.add(new FillClients("-1", "Other"));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs1);
			db.closeConnection(con);
		}
		return al;
	}

	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public List<FillClients> fillAllClients(boolean isOther) {
		List<FillClients> al = new ArrayList<FillClients>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectAllClients);
			rs1 = pst.executeQuery();
			if(isOther) {
				al.add(new FillClients("0", "Other"));
			}
			while (rs1.next()) {
				al.add(new FillClients(rs1.getString("client_id"), rs1.getString("client_name")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs1);
			db.closeConnection(con);
		}
		return al;
	}
}