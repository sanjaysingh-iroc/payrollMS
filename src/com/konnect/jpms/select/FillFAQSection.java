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
public class FillFAQSection implements IStatements{

	String sectionId;
	String sectionName;
	UtilityFunctions uF = new UtilityFunctions();
	
	private FillFAQSection(String sectionId, String sectionName) {
		this.sectionId = sectionId;
		this.sectionName = sectionName;
	}
	HttpServletRequest request;
	public FillFAQSection(HttpServletRequest request) {
		this.request = request;
	}
	public FillFAQSection() {
	}
	
	public List<FillFAQSection> fillSection(){
	
		System.out.println("fillSection");
		List<FillFAQSection> al = new ArrayList<FillFAQSection>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct section_id,section_name from faq_details");
			rs = pst.executeQuery();
//			System.out.print("FAQSection pst====>"+pst);
			while(rs.next()) {
				if(uF.parseToInt(rs.getString("section_id"))>0 && (rs.getString("section_name"))!=null ) {
					al.add(new FillFAQSection(rs.getString("section_id"), rs.getString("section_name")));
				}	
			}	
			rs.close();
			pst.close();
			al.add(new FillFAQSection("0", "Other"));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	

	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;	
	}

	

	

	
}  
