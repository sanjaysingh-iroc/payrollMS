package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;

public class FillEmploymentType implements IConstants {

	String empTypeId;
	String empTypeName;
    
	public String getEmpTypeId() {
		return empTypeId;
	} 

	public void setEmpTypeId(String empTypeId) {
		this.empTypeId = empTypeId;
	}

	public String getEmpTypeName() {
		return empTypeName;
	}

	public void setEmpTypeName(String empTypeName) {
		this.empTypeName = empTypeName;
	}

	public FillEmploymentType(String empTypeId, String empTypeName) {
		this.empTypeId = empTypeId;
		this.empTypeName = empTypeName;
	}

	HttpServletRequest request;
	public FillEmploymentType(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillEmploymentType() {
	}

	
	public List<FillEmploymentType> fillEmploymentTypeWithoutCurrentEmployeeType(HttpServletRequest request, String strEmpId) {
		List<FillEmploymentType> al = new ArrayList<FillEmploymentType>();
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		CommonFunctions CF = new CommonFunctions();
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			String empType = null;
			pst = con.prepareStatement("SELECT emptype FROM employee_official_details eod where eod.emp_id = ? ");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				empType = rs.getString("emptype");
			}
			rs.close();
			pst.close();
			
			if(hmFeatureStatus != null ) {
				if(hmFeatureStatus.get(F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_GENERAL))) {
					if(empType == null || !empType.equals("FT")) {
						al.add(new FillEmploymentType("FT", "Full Time"));
					}
					if(empType == null || !empType.equals("PT")) {
						al.add(new FillEmploymentType("PT", "Part Time"));
					}
					if(empType == null || !empType.equals("CON")) {
						al.add(new FillEmploymentType("CON", "Contractual"));
					}
					if(empType == null || !empType.equals("CO")) {
						al.add(new FillEmploymentType("CO", "Consultant"));
					}
					if(empType == null || !empType.equals("I")) {
						al.add(new FillEmploymentType("I", "Intern"));
					}
				}
				
				if(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE))) {
					if(empType == null || !empType.equals("R")) {
						al.add(new FillEmploymentType("R", "Regular"));
					}
					if(empType == null || !empType.equals("CT")) {
						al.add(new FillEmploymentType("CT", "Contract"));
					}
					if(empType == null || !empType.equals("PF")) {
						al.add(new FillEmploymentType("PF", "Professional"));
					}
					if(empType == null || !empType.equals("ST")) {
						al.add(new FillEmploymentType("ST", "Stipend"));
					}
					if(empType == null || !empType.equals("SCH")) {
						al.add(new FillEmploymentType("SCH", "Scholarship"));
					}
				}
				
				if(hmFeatureStatus.get(F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_FINANCE))) {
					if(empType == null || !empType.equals("ORAT")) {
						al.add(new FillEmploymentType("ORAT", "Outside Registered Article"));
					}
					if(empType == null || !empType.equals("IHAT")) {
						al.add(new FillEmploymentType("IHAT", "Inhouse Article"));
					}
					if(empType == null || !empType.equals("AT")) {
						al.add(new FillEmploymentType("AT", "Article"));
					}
					if(empType == null || !empType.equals("CO")) {
						al.add(new FillEmploymentType("CO", "Consultant"));
					}
					if(empType == null || !empType.equals("FT")) {
						al.add(new FillEmploymentType("FT", "Permanent(Full Time)"));
					}
					if(empType == null || !empType.equals("PT")) {
						al.add(new FillEmploymentType("PT", "Part Time"));
					}
					if(empType == null || !empType.equals("P")) {
						al.add(new FillEmploymentType("P", "Partner"));
					}
					if(empType == null || !empType.equals("C")) {
						al.add(new FillEmploymentType("C", "Temporary")); // Contract
					}
				}
				
				
			} else {
				if(empType == null || !empType.equals("FT")) {
					al.add(new FillEmploymentType("FT", "Full Time"));
				}
				if(empType == null || !empType.equals("PT")) {
					al.add(new FillEmploymentType("PT", "Part Time"));
				}
				if(empType == null || !empType.equals("CON")) {
					al.add(new FillEmploymentType("CON", "Contractual"));
				}
				if(empType == null || !empType.equals("CO")) {
					al.add(new FillEmploymentType("CO", "Consultant"));
				}
				if(empType == null || !empType.equals("I")) {
					al.add(new FillEmploymentType("I", "Intern"));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	public List<FillEmploymentType> fillEmploymentType(HttpServletRequest request) {
		List<FillEmploymentType> al = new ArrayList<FillEmploymentType>();
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		CommonFunctions CF = new CommonFunctions();
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			if(hmFeatureStatus != null ) {
				if(hmFeatureStatus.get(F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_GENERAL))) {
					al.add(new FillEmploymentType("FT", "Full Time"));
					al.add(new FillEmploymentType("PT", "Part Time"));
					al.add(new FillEmploymentType("CON", "Contractual"));
					al.add(new FillEmploymentType("CO", "Consultant"));
					al.add(new FillEmploymentType("I", "Intern"));  
				}
				
				if(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE))) {
					al.add(new FillEmploymentType("R", "Regular"));
					al.add(new FillEmploymentType("CT", "Contract"));
					al.add(new FillEmploymentType("PF", "Professional"));
					al.add(new FillEmploymentType("ST", "Stipend"));
					al.add(new FillEmploymentType("SCH", "Scholarship"));  
				}
				
				if(hmFeatureStatus.get(F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_FINANCE))) {
					al.add(new FillEmploymentType("AT", "Article"));
					al.add(new FillEmploymentType("ORAT", "Outside Registered Article"));
					al.add(new FillEmploymentType("IHAT", "Inhouse Article"));
					al.add(new FillEmploymentType("CO", "Consultant"));
					al.add(new FillEmploymentType("FT", "Permanent(Full Time)"));
					al.add(new FillEmploymentType("PT", "Part Time"));
					al.add(new FillEmploymentType("P", "Partner"));  
					al.add(new FillEmploymentType("C", "Temporary")); // Contract
				}
				
				
			} else {
				al.add(new FillEmploymentType("FT", "Full Time"));
				al.add(new FillEmploymentType("PT", "Part Time"));
				al.add(new FillEmploymentType("CON", "Contractual"));
				al.add(new FillEmploymentType("CO", "Consultant"));
				al.add(new FillEmploymentType("I", "Intern"));  
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
}
