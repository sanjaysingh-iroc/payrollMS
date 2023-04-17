package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class FillSkills {

	String skillsId;
	String skillsName;
	UtilityFunctions uF=new UtilityFunctions();
	public FillSkills(String skillsId, String skillsName) {
		this.skillsId = skillsId;
		this.skillsName = skillsName;
	}
	HttpServletRequest request;
	public FillSkills(HttpServletRequest request) {
		this.request = request;
	}
	public FillSkills() {
	}
	 
	public List<FillSkills> fillSkills(){
		List<FillSkills> al = new ArrayList<FillSkills>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from skills_details order by skill_name");
			pst = con.prepareStatement("select distinct(skill_name) as skill_name from skills_details where skill_name is not null and skill_name != '' order by skill_name");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillSkills(rs1.getString("skill_name"), rs1.getString("skill_name").trim()));
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
	
	
	public List<FillSkills> fillSkillsWithId(){
		List<FillSkills> al = new ArrayList<FillSkills>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from skills_details order by skill_name");
			pst = con.prepareStatement("select skill_id, skill_name from skills_details where skill_name is not null and skill_name != '' order by skill_name");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillSkills(rs1.getString("skill_id"), rs1.getString("skill_name").trim()));
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
	
	public List<FillSkills> fillSkillsWithIdOnOrg(int orgId){
		List<FillSkills> al = new ArrayList<FillSkills>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder sb = new StringBuilder();
			sb.append("select skill_id, skill_name from skills_details where skill_name is not null and skill_name != '' ");
			if(orgId > 0){
				sb.append(" and org_id ="+ orgId +" ");
			}
			sb.append("order by skill_name");
			pst = con.prepareStatement(sb.toString());
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillSkills(rs1.getString("skill_id"), rs1.getString("skill_name").trim()));
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
	
	
	public List<FillSkills> fillSkillsOrg(int orgId){
		List<FillSkills> al = new ArrayList<FillSkills>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			StringBuilder sb = new StringBuilder();
			con = db.makeConnection(con);
			sb.append("select * from skills_details ");
			if(orgId > 0){
				sb.append("where org_id ="+ orgId +" ");
			}
			sb.append("order by skill_name");
			pst = con.prepareStatement(sb.toString());
//			pst = con.prepareStatement("select * from skills_details where skill_id = ? order by skill_name");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillSkills(rs1.getString("skill_name"), rs1.getString("skill_name").trim()));
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
	
	
	public List<FillSkills> fillSkills(String strServiceId){
		List<FillSkills> al = new ArrayList<FillSkills>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select distinct(skills_name) as skills_name from skills_description where emp_id in (select emp_id from employee_official_details where service_id like ?)");
			pst.setString(1, "%,"+strServiceId+",%");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(!al.contains(rs1.getString("skills_name")))
				al.add(new FillSkills(rs1.getString("skills_name"), rs1.getString("skills_name").trim()));
			}	
			rs1.close();
			pst.close();
			
//			System.out.println("List===>"+al);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	
	public List<FillSkills> fillProjectSkills(int nProId){
		List<FillSkills> al = new ArrayList<FillSkills>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(skill_name) as skills_name from skills_details where service_project_id::text in (select service from projectmntnc where pro_id=?) ");
			pst.setInt(1, nProId);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(!al.contains(rs1.getString("skills_name")))
					al.add(new FillSkills(rs1.getString("skills_name"), rs1.getString("skills_name").trim()));
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
	
	
	public List<FillSkills> fillSkillNameByIds(String skillIds) {
		List<FillSkills> al = new ArrayList<FillSkills>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(skillIds != null && skillIds.length() > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("select skill_id, skill_name from skills_details where skill_name is not null and skill_name != '' and skill_id in ("+skillIds+") ");
				sb.append("order by skill_name");
				pst = con.prepareStatement(sb.toString());
//				System.out.println("pst  ==========>> " + pst );
				rs1 = pst.executeQuery();
				while (rs1.next()) {
					al.add(new FillSkills(rs1.getString("skill_id"), rs1.getString("skill_name").trim()));
				}	
				rs1.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public String getSkillsId() {
		return skillsId;
	}

	public void setSkillsId(String skillsId) {
		this.skillsId = skillsId;
	}

	public String getSkillsName() {
		return skillsName;
	}

	public void setSkillsName(String skillsName) {
		this.skillsName = skillsName;
	}
	
}  
