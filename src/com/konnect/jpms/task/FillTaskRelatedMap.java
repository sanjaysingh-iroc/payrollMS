package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;

public class FillTaskRelatedMap {
	
	HttpServletRequest request;
	public FillTaskRelatedMap(HttpServletRequest request) {
		this.request = request;
	}
	
public Map<String, String> getClientNameMap() {
		
		Map<String, String> hmClientName = new HashMap<String, String>();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select client_id,client_name from client_details");
			
			rs = pst.executeQuery();
			while (rs.next()) {
				
				hmClientName.put(rs.getString("client_id"), rs.getString("client_name"));
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
		return hmClientName;
	}

}
