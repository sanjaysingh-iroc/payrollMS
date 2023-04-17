package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.konnect.jpms.util.Database;

public class FillProject {

	String id;
	String name;
	
	HttpServletRequest request;
	public  FillProject(){
		
	}
	public FillProject(HttpServletRequest request) {
		this.request = request;
	}
	public FillProject(String id,String name) {
		this.id=id;
		this.name=name;
	}
	
	
	public List<FillProject> fillProjects(String[] clients){
		List<FillProject> al = new ArrayList<FillProject>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from projectmntnc p where p.client_id in ("+StringUtils.join(clients, ",")+")");
//			System.out.println("pst ===>> " + pst);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillProject(rs1.getString("pro_id"), rs1.getString("pro_name")));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs1);
			db.closeConnection(con);
		}
		return al;
	} 
	public List<FillProject> fillProjects() {
		List<FillProject> al = new ArrayList<FillProject>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from projectmntnc p ");
			rs1 = pst.executeQuery();
			
			while (rs1.next()) {
				al.add(new FillProject(rs1.getString("pro_id"), rs1.getString("pro_name")));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs1);
			db.closeConnection(con);
		}
		return al;
	} 

	public List<FillProject> fillProjectList(){
		List<FillProject> al = new ArrayList<FillProject>();
		
		try {

			al.add(new FillProject("X", "Fixed"));
			al.add(new FillProject("H", "Hourly"));
				
		} catch (Exception e) {
			e.printStackTrace();
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
