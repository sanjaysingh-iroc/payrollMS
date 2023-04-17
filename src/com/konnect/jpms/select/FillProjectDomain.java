package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillProjectDomain implements IStatements {
	
	String domainId;
	String domainName;
	
	public FillProjectDomain(String domainId, String domainName) {
		this.domainId = domainId;
		this.domainName = domainName;
	}
	
	public FillProjectDomain() {
		
	}
	
	HttpServletRequest request;
	public FillProjectDomain(HttpServletRequest request) {
		this.request = request;
	}
	
	public List<FillProjectDomain> fillProjectDomain(){
		List<FillProjectDomain> al = new ArrayList<FillProjectDomain>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from domain_details order by domain_name");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
//				System.out.println("domain_id="+rs1.getString("domain_id"));
				al.add(new FillProjectDomain(rs1.getString("domain_id"), rs1.getString("domain_name")));
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

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
}
