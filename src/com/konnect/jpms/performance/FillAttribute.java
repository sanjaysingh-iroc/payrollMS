package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillAttribute implements IStatements{

	String id;
	String name;
	
	FillAttribute(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	HttpServletRequest request;
	public FillAttribute(HttpServletRequest request) {
		this.request = request; 
	}
	
	public FillAttribute() {
	}
	
	
	
//public List<FillAttribute> fillPotentialAttribute(){
//		
//		List<FillAttribute> al = new ArrayList<FillAttribute>();
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select distinct(aa.arribute_id),aa.attribute_name from appraisal_element_attribute aea, appraisal_attribute aa " +
//				"where aea.appraisal_attribute = aa.arribute_id and aea.appraisal_element = 1 order by aa.arribute_id");
//			rs = pst.executeQuery();
//			while(rs.next()){
//				al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name")));				
//			}	
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//		return al;
//	}
	


//public List<FillAttribute> fillPerformanceAttribute(){
//	
//	List<FillAttribute> al = new ArrayList<FillAttribute>();
//	Connection con = null;
//	PreparedStatement pst = null;
//	ResultSet rs = null;
//	Database db = new Database();
//	db.setRequest(request);
//
//	try {
//		con = db.makeConnection(con);
//		pst = con.prepareStatement("select distinct(aa.arribute_id),aa.attribute_name from appraisal_element_attribute aea, appraisal_attribute aa " +
//				"where aea.appraisal_attribute = aa.arribute_id and aea.appraisal_element = 2 order by aa.arribute_id ");
//		rs = pst.executeQuery();
//		while(rs.next()){
//			al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name")));				
//		}	
//		
//	} catch (Exception e) {
//		e.printStackTrace();
//	}finally{
//		
//		db.closeStatements(pst);
//		db.closeResultSet(rs);
//		db.closeConnection(con);
//	}
//	return al;
//}


	
	
	public List<FillAttribute> fillAttribute() {
		
		List<FillAttribute> al = new ArrayList<FillAttribute>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute where status=true order by attribute_name");
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name")));				
			}
			rs.close();
			pst.close();
//			System.out.println("All Attributes Are ========== > "+al.get(0).getId() + " & " + al.get(1).getName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
public List<FillAttribute> fillElementAttribute(String level){
		
		List<FillAttribute> al = new ArrayList<FillAttribute>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(arribute_id) as arribute_id, attribute_name,appraisal_element_name from (select * from " +
					"appraisal_attribute_level aal,appraisal_attribute aa where aal.attribute_id=aa.arribute_id and aa.status = true ");
			if(level != null && !level.equals("")) {
				sbQuery.append(" and aal.level_id in ("+level+") ");
			}
			sbQuery.append(") a, appraisal_element ae where ae.appraisal_element_id=a.element_id");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("attribute pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				
				al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name") + " (" + rs.getString("appraisal_element_name") + ")"));				
			}
			rs.close();
			pst.close();
//			System.out.println("All Attributes Are ========== > "+al.get(0).getId() + " & " + al.get(1).getName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}


public List<FillAttribute> fillElementAttributeElementwise(String level, String elementId) {
	
	List<FillAttribute> al = new ArrayList<FillAttribute>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();
	try {

		con = db.makeConnection(con);
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select distinct(arribute_id) as arribute_id, attribute_name,appraisal_element_name from (select * from " +
				"appraisal_attribute_level aal,appraisal_attribute aa where aal.attribute_id=aa.arribute_id and aa.status = true ");
		if(level != null && !level.equals("")) {
			sbQuery.append(" and aal.level_id in ("+level+") ");
		}
		sbQuery.append(") a, appraisal_element ae where ae.appraisal_element_id = a.element_id and ae.appraisal_element_id=?");
		
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(elementId));
		rs = pst.executeQuery();
		while(rs.next()) {
			
			al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name"))); //+ " (" + rs.getString("appraisal_element_name") + ")"				
		}	
		rs.close();
		pst.close();
//		System.out.println("All Attributes Are ========== > "+al.get(0).getId() + " & " + al.get(1).getName());
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return al;
}

	
public List<FillAttribute> fillAttribute(String level){
		
		List<FillAttribute> al = new ArrayList<FillAttribute>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(aal.attribute_id) as arribute_id,aa.attribute_name from appraisal_attribute aa,appraisal_attribute_level aal where aa.arribute_id=aal.attribute_id " +
					" and aa.status=true and aal.level_id in("+level+")");
			rs = pst.executeQuery();
//			System.out.println("pst fillAttribute =====> "+pst);
			while(rs.next()){  
				al.add(new FillAttribute(rs.getString("arribute_id"), rs.getString("attribute_name")));				
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
