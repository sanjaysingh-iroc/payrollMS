package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillSourceTypeAndName implements IConstants {


	String sourceTypeId;
	String sourceTypeName;
	
	
	private FillSourceTypeAndName(String sourceTypeId, String sourceTypeName) {
		this.sourceTypeId = sourceTypeId;
		this.sourceTypeName = sourceTypeName;
	}
	
	public FillSourceTypeAndName() {
	}
	
	HttpServletRequest request;
	public FillSourceTypeAndName(HttpServletRequest request) {
		this.request = request;
	}
	
	public List<FillSourceTypeAndName> fillSourceType() {
		List<FillSourceTypeAndName> al = new ArrayList<FillSourceTypeAndName>();
		try {
//			al.add(new FillSourceTypeAndName(""+SOURCE_HR, SOURCE_HR_LBL)); //Human Resource
//			al.add(new FillSourceTypeAndName(""+SOURCE_RECRUITER, SOURCE_RECRUITER_LBL));
			
			al.add(new FillSourceTypeAndName(""+SOURCE_CONSULTANT, SOURCE_CONSULTANT_LBL));
			al.add(new FillSourceTypeAndName(""+SOURCE_REFERENCE, SOURCE_REFERENCE_LBL));
			al.add(new FillSourceTypeAndName(""+SOURCE_WEBSITE, SOURCE_WEBSITE_LBL));
			al.add(new FillSourceTypeAndName(""+SOURCE_WALK_IN, SOURCE_WALK_IN_LBL));
			al.add(new FillSourceTypeAndName(""+SOURCE_OTHER, SOURCE_OTHER_LBL));
			
//			al.add(new FillSourceTypeAndName(""+SOURCE_JOB_PORTAL, "Job Portal"));
//			al.add(new FillSourceTypeAndName(""+SOURCE_SOCIAL_SITES, "Social Sites"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	public List<FillSourceTypeAndName> fillSourceNameOnType(String sourceType) {
		
		List<FillSourceTypeAndName> al = new ArrayList<FillSourceTypeAndName>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
	
		try {
			con = db.makeConnection(con);
			
			boolean flagMiddleName = getFeatureStatusForEmpMiddleName();

			
			StringBuilder sbQuery = new StringBuilder();
			if(uF.parseToInt(sourceType) == SOURCE_HR || uF.parseToInt(sourceType) == SOURCE_RECRUITER) {
				sbQuery.append("select emp_per_id,emp_fname, emp_mname,emp_lname from employee_personal_details where emp_per_id in (select distinct(added_by) " +
					" from candidate_application_details where source_type = "+uF.parseToInt(sourceType)+")");
				pst = con.prepareStatement(sbQuery.toString());
			} else if(uF.parseToInt(sourceType) == SOURCE_REFERENCE) {
				pst = con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname from employee_personal_details where emp_per_id in (select distinct(source_or_ref_code) " +
					" from candidate_application_details where source_type = "+uF.parseToInt(sourceType)+")");
			} else {
				pst = con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname from employee_personal_details where emp_per_id in (select distinct(source_or_ref_code) " +
					" from candidate_application_details where source_type != "+SOURCE_HR+" and source_type != "+SOURCE_RECRUITER+" and source_type != "+SOURCE_REFERENCE+") ");
			}
			rs = pst.executeQuery();
			while(rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				al.add(new FillSourceTypeAndName(rs.getString("emp_per_id"), rs.getString("emp_fname") +strEmpMName+" "+ rs.getString("emp_lname")));				
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
	
	
	
public List<FillSourceTypeAndName> fillAllSourceName() {
		
		List<FillSourceTypeAndName> al = new ArrayList<FillSourceTypeAndName>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			boolean flagMiddleName = getFeatureStatusForEmpMiddleName();

			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,emp_fname,emp_mname,emp_lname from employee_personal_details where emp_per_id in (select distinct(added_by) " +
				" from candidate_application_details where source_type in ("+SOURCE_HR+","+SOURCE_RECRUITER+") )");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				al.add(new FillSourceTypeAndName(rs.getString("emp_per_id"), rs.getString("emp_fname") +strEmpMName+" "+ rs.getString("emp_lname")));				
			}	
			rs.close();
			pst.close();
				
			pst = con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname from employee_personal_details where emp_per_id in (select distinct(source_or_ref_code) " +
				" from candidate_application_details where source_type = "+SOURCE_REFERENCE+")");
			rs = pst.executeQuery();
			while(rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				al.add(new FillSourceTypeAndName(rs.getString("emp_per_id"), rs.getString("emp_fname") +strEmpMName+" "+ rs.getString("emp_lname")));				
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
	
public Boolean getFeatureStatusForEmpMiddleName() {
	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
	PreparedStatement pst = null;
	ResultSet rst = null;
	boolean flag = false;
	try {
		con = db.makeConnection(con);
		pst = con.prepareStatement("select feature_name,feature_status,user_type_id,emp_ids from feature_management where feature_name=?");
		pst.setString(1, F_SHOW_EMPLOYEE_MIDDLE_NAME);
		rst = pst.executeQuery();
		while (rst.next()) {
			if(rst.getBoolean("feature_status")) {
				flag = true;
			}
		}
		// System.out.println("scree-"+ScreenShotName);
		rst.close();
		pst.close();

	} catch (Exception e) {
		e.printStackTrace();
	}  finally {
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return flag;
}


	public String getSourceTypeId() {
		return sourceTypeId;
	}

	public void setSourceTypeId(String sourceTypeId) {
		this.sourceTypeId = sourceTypeId;
	}

	public String getSourceTypeName() {
		return sourceTypeName;
	}

	public void setSourceTypeName(String sourceTypeName) {
		this.sourceTypeName = sourceTypeName;
	}

	public static Map<String, String> getResourceData() {
		Map<String, String> hmEmpResource = new HashMap<String, String>();
		hmEmpResource.put("1", "Resource");
		hmEmpResource.put("2", "Project Owner");
		hmEmpResource.put("3", "CEO");
		hmEmpResource.put("4", "Human Resource");
		return hmEmpResource;
	}
	
	
}
