package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmpListForTrainer extends ActionSupport implements ServletRequestAware,IStatements {

	private static final long serialVersionUID = 1L;
	
	private String location;
	private String grade;
	private String level;
	private String desig;
	private String org_id;
	private String desigId;

	private String f_organization;
	private String[] empLocation;
	private String[] strlearnerLevel;
	private String[] strstrlearnerDesignation;
	private String[] strlearnerGrade;
	
	private List<String> strOrgList = new ArrayList<String>();;
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> locationList;
	private List<FillLevel> levelList;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList;
	
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF = null;
	private String fromPage;

	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getF_organization()==null){
			setF_organization((String)session.getAttribute(ORGID));
		}
		
		// Started Dattatray Date : 17-July-2021
		if(getLocation() != null && !getLocation().equals("")) {
			setEmpLocation(getLocation().split(","));
		} else {
			setEmpLocation(null);
		}
		
		if(getLevel() != null && !getLevel().equals("")) {
			setStrlearnerLevel(getLevel().split(","));
		} else {
			setStrlearnerLevel(null);
		}
		
		if(getDesig() != null && !getDesig().equals("")) {
			setStrstrlearnerDesignation(getDesig().split(","));
		} else {
			setStrstrlearnerDesignation(null);
		}
		// End Dattatray Date : 17-July-2021
		
		loadTrainerList(uF);
//		System.out.println("GetEmpListForTrainer fromPage==>"+getFromPage());
		getInfo();
		getSelectEmployeeList();
		return SUCCESS;
	}
	
	public void loadTrainerList(UtilityFunctions uF) {
			
		organisationList = new FillOrganisation(request).fillOrganisation();
		locationList = new FillWLocation(request).fillWLocation(getF_organization());	
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_organization()));
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_organization()));
		gradeList = new FillGrade(request).fillGradeByOrg(uF.parseToInt(getF_organization()));		
		
		empList = getEmployeeList(uF);
	}

private List<FillEmployee> getEmployeeList(UtilityFunctions uF) {
		
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive = true");
			
			if(uF.parseToInt(getF_organization())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_organization()));
			}
			if(getEmpLocation()!=null && getEmpLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+removeExtraCommas(StringUtils.join(getEmpLocation(), ","))+") ");//Created Dattatray Date : 17-July-2021 Note : removeExtraCommas method use
	        }
            if(getStrlearnerLevel()!=null && getStrlearnerLevel().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+removeExtraCommas(StringUtils.join(getStrlearnerLevel(), ","))+") ) ");//Created Dattatray Date : 17-July-2021 Note : removeExtraCommas method use
            }
            if(getStrstrlearnerDesignation()!=null && getStrstrlearnerDesignation().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and dd.designation_id in ( "+removeExtraCommas(StringUtils.join(getStrstrlearnerDesignation(), ","))+") ) ");//Created Dattatray Date : 17-July-2021 Note : removeExtraCommas method use
            }
            if (getStrlearnerGrade() != null && !getStrlearnerGrade().equals("")) {
				sbQuery.append(" and grade_id in ("+StringUtils.join(getStrlearnerGrade(), ",")+") ");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("getEmployeeList : "+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
			
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	//Created Dattatray Date : 17-July-2021 
	public static String removeExtraCommas(String entry) {
	    if(entry==null)
	        return null;
	
	    String ret="";
	    entry=entry.replaceAll("\\s","");
	    String arr[]=entry.split(",");
	    boolean start=true;
	    for(String str:arr) {
	        if(!"".equalsIgnoreCase(str)) {
	            if(start) {
	                ret=str;
	                start=false;
	            }
	            else {
	            ret=ret+","+str;
	            }
	        }
	    }               
	    return ret;
	}
	
	private void getInfo() {
		Connection con = null;
		Database db =new Database();
		db.setRequest(request);
		try{
			con =db.makeConnection(con);
			Map<String,String> hmEmpLocation=CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null,null); 
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
	}


private void getSelectEmployeeList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			
			con=db.makeConnection(con);
			List<String> existTrainerList = new ArrayList<String>();
			Map<String, String> hmExistTrainer = new HashMap<String, String>();
			pst = con.prepareStatement("select * from training_trainer");
			rst=pst.executeQuery();
			while(rst.next()){
				if(rst.getString("emp_id") != null && !rst.getString("emp_id").equals("")) {
					hmExistTrainer.put(rst.getString("emp_id"), rst.getString("emp_id"));
					existTrainerList.add(rst.getString("emp_id"));
				}
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmExistTrainer ===> " + hmExistTrainer);
			
			request.setAttribute("existTrainerList", existTrainerList);
			request.setAttribute("hmExistTrainer", hmExistTrainer);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDesig() {
		return desig;
	}

	public void setDesig(String desig) {
		this.desig = desig;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getF_organization() {
		return f_organization;
	}

	public void setF_organization(String f_organization) {
		this.f_organization = f_organization;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<FillWLocation> locationList) {
		this.locationList = locationList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<String> getStrOrgList() {
		return strOrgList;
	}

	public void setStrOrgList(List<String> strOrgList) {
		this.strOrgList = strOrgList;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String[] getEmpLocation() {
		return empLocation;
	}

	public void setEmpLocation(String[] empLocation) {
		this.empLocation = empLocation;
	}

	public String[] getStrlearnerLevel() {
		return strlearnerLevel;
	}

	public void setStrlearnerLevel(String[] strlearnerLevel) {
		this.strlearnerLevel = strlearnerLevel;
	}

	public String[] getStrstrlearnerDesignation() {
		return strstrlearnerDesignation;
	}

	public void setStrstrlearnerDesignation(String[] strstrlearnerDesignation) {
		this.strstrlearnerDesignation = strstrlearnerDesignation;
	}

	public String[] getStrlearnerGrade() {
		return strlearnerGrade;
	}

	public void setStrlearnerGrade(String[] strlearnerGrade) {
		this.strlearnerGrade = strlearnerGrade;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
}