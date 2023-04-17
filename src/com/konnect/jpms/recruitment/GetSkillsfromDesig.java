package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSkillsfromDesig extends ActionSupport implements ServletRequestAware{

	  String strDesignation;
	  List<FillSkills> skillslist;
	 

	  private static final long serialVersionUID = 1L;

	  public String execute() {
			
		  	if(getStrDesignation()!=null && !getStrDesignation().equals("")){
		  		skillslist=getSkillsByDesig();
		  	}
			
			return SUCCESS;			
	  }


	  private List<FillSkills> getSkillsByDesig() {
			List<FillSkills> al = new ArrayList<FillSkills>();
			
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs1 = null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF=new UtilityFunctions();
			try {

				con = db.makeConnection(con);	
				pst = con.prepareStatement("select * from desig_attribute where _type=5 and desig_id=?");
				pst.setInt(1,uF.parseToInt(getStrDesignation()));
				rs1 = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				String skillname="";
				while (rs1.next()) {
					skillname=rs1.getString("desig_value");
				}
				rs1.close();
				pst.close();
				
				List<String> skills=Arrays.asList(skillname.split(","));
				for(int i=0;skills!=null && !skills.isEmpty() && i<skills.size();i++){
					String name=skills.get(i).trim();
					if(!al.contains(name))
						al.add(new FillSkills(name, name));
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


	public String getStrDesignation() {
			return strDesignation;
	  }

	  public void setStrDesignation(String strDesignation) {
			this.strDesignation = strDesignation;
	  }

	  public List<FillSkills> getSkillslist() {
			return skillslist;
	  }

	  public void setSkillslist(List<FillSkills> skillslist) {
			this.skillslist = skillslist;
	  }


	  HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

		
}
