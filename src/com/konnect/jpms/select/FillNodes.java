package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillNodes implements IStatements{

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	String nodeId;
	String nodeName;
	
	private FillNodes(String nodeId, String nodeName) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
	}
	HttpServletRequest request;
	public FillNodes(HttpServletRequest request) {
		this.request = request;
	}
	public FillNodes() {
	}
	
	public List<FillNodes> fillNodes(String strType, CommonFunctions CF){
		
		List<FillNodes> al = new ArrayList<FillNodes>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			String[] arrEnabledModules = CF.getArrEnabledModules();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from nodes where node_type=? order by node_name");
			pst.setString(1, strType);
			rs = pst.executeQuery();
			while(rs.next()){
				if(ArrayUtils.contains(arrEnabledModules, rs.getString("module_id"))<0){
					continue;
				}
				al.add(new FillNodes(rs.getString("node_id"), rs.getString("node_name")));				
			}	
			rs.close();
			pst.close();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillNodes> fillFormNodesByOrg(String strType, CommonFunctions CF, int nOrgId) {
		
		List<FillNodes> al = new ArrayList<FillNodes>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			String[] arrEnabledModules = CF.getArrEnabledModules();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from nodes where node_type=? and node_id not in (select node_id from " +
					"form_management_details where org_id=?) order by node_name");
			pst.setString(1, strType);
			pst.setInt(2, nOrgId);
			rs = pst.executeQuery();
			while(rs.next()){
				if(ArrayUtils.contains(arrEnabledModules, rs.getString("module_id"))<0){
					continue;
				}
				al.add(new FillNodes(rs.getString("node_id"), rs.getString("node_name")));				
			}	
			rs.close();
			pst.close();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
public List<FillNodes> fillFormNodes(String strType, CommonFunctions CF, int nOrgId, int nFormId) {
		
		List<FillNodes> al = new ArrayList<FillNodes>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			String[] arrEnabledModules = CF.getArrEnabledModules();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from nodes where node_type=? and node_id not in (select node_id from form_management_details " +
					"where org_id=? and node_id not in(select node_id from form_management_details where form_id=?)) order by node_name");
			pst.setString(1, strType);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nFormId);
			rs = pst.executeQuery();
			while(rs.next()){
				if(ArrayUtils.contains(arrEnabledModules, rs.getString("module_id"))<0){
					continue;
				}
				al.add(new FillNodes(rs.getString("node_id"), rs.getString("node_name")));				
			}	
			rs.close();
			pst.close();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
}  
