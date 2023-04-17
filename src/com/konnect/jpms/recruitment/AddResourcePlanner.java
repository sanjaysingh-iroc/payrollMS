package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddResourcePlanner extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strOrgID = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	 
	private static Logger log = Logger.getLogger(ResourcePlanner.class);

	String strSessionEmpId = null;

	private List<FillFinancialYears> financialYearList;
	private List<FillWLocation> wLocationList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillDepartment> departmentList;
	private List<FillDesig> desigList;
	private List<FillOrganisation> orgList;
	
	private String f_level;
//	String f_strFinancialYear;
	private String f_department;
//	String f_service;
	private String f_strWLocation;
	private String f_desig;
	private String f_org;
	private String f_strFinancialYear;
	private String userLocation;
	private String f_financialYearStart;
	private String f_financialYearEnd;
	private String finansyr;
	private String orgid;
	private String wlocid;
	private String lvlid;
	private String deptid;

	private String currentYear;
	private String currentMonth;
	
	private String currUserType;
	private String fromPage;
	
// update data *********************
	
	private String editResource;
	private String editResourceDesig;
	private String editResourceYearStart;
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strOrgID = (String) session.getAttribute(ORGID);

//		request.setAttribute(TITLE, "Edit Workforce Plan");
//		request.setAttribute(PAGE, "/jsp/recruitment/AddResourcePlanner.jsp");
		loadData();
		getResourcePlannerDesigData();
		System.out.println("AddResourcePlanner getFromPage() ===>> " + getFromPage());
		
		String submit = request.getParameter("submit");
		if(submit!=null && submit.equals("Save")) {
			insertResourcePlanner();
			return SUCCESS;
		}
		
        if(getEditResource()!=null ){
        	updateResourcePlanner(getEditResourceDesig(),getEditResourceYearStart(),getEditResourceYearEnd());
			return SUCCESS;
        }
        
		getResourcePlannerData();
        return LOAD;

	}

	private void updateResourcePlanner(String desigid, String yearStart, String yearEnd) {
	
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
           
			String[] resourceVal=getEditResource().split(",");
//			System.out.println("getEditResource() ===> "+getEditResource());
			String query="update resource_planner_details set resource_requirement=?,added_by=? where designation_id=? and rmonth=? and ryear=?"; // and wlocation_id=? 
			for(int j=1; j<=12; j++) {
				  pst=con.prepareStatement(query);
				  pst.setInt(1,uF.parseToInt(resourceVal[j]));
				  pst.setInt(2,uF.parseToInt(strSessionEmpId));
				  pst.setInt(3,uF.parseToInt(desigid));
				  pst.setInt(4,j);
				  if(j<4) {
					  pst.setInt(5,uF.parseToInt(yearEnd)); 
				  } else {
					  pst.setInt(5,uF.parseToInt(yearStart)); 
				  }
//				  pst.setInt(6,uF.parseToInt(userLocation));
				 int cnt = pst.executeUpdate();
				 pst.close();
				 if(cnt == 0) {
					 String query1 = "insert into resource_planner_details (designation_id,financial_year_from,financial_year_to," +
					 		"rmonth,ryear,added_by,added_date,resource_requirement) values (?,?,?,?, ?,?,?,?)"; //,wlocation_id ,?
					  pst=con.prepareStatement(query1);
					  pst.setInt(1, uF.parseToInt(desigid));
					  pst.setDate(2, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT) );
					  pst.setDate(3, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT) ); 
					  pst.setInt(4, j);
					  if(j<4) {
						  pst.setInt(5, uF.parseToInt(yearEnd)); 
					  } else {
						  pst.setInt(5, uF.parseToInt(yearStart)); 
					  }
					  pst.setInt(6, uF.parseToInt(strSessionEmpId));
					  pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//					  pst.setInt(8,uF.parseToInt(userLocation));
					  pst.setInt(8, uF.parseToInt(resourceVal[j]));
					  pst.executeUpdate();
					  pst.close();
				}
//				  System.out.println("pst ===> "+pst);
			}
	
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}

//		return SUCCESS;
	
	}

	private void getResourcePlannerData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);

			Map<String ,String> hmResourceReq=new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select rpd.designation_id,rmonth,resource_requirement from (select designation_id from designation_details where " +
					"level_id in (select level_id from level_details where level_id>0 ");
			
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and level_id="+uF.parseToInt(getF_level()));	
			}
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id="+uF.parseToInt(getF_org()));
			}
			sbQuery.append(" )) as a,resource_planner_details rpd " +
					"where a.designation_id = rpd.designation_id ");
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and rpd.wlocation_id="+uF.parseToInt(getF_strWLocation()));
			}
			
			sbQuery.append("and rpd.financial_year_from=? and rpd.financial_year_to=? order by a.designation_id");
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and eod.depart_id="+uF.parseToInt(getF_department()));
//			}
//			sbQuery.append("select * from resource_planner_details where wlocation_id=? " +
//					"and financial_year_from=? and financial_year_to=?");

            pst=con.prepareStatement(sbQuery.toString());
//            pst.setInt(1,uF.parseToInt(userLocation));
            pst.setDate(1, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
            pst.setDate(2, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));          
            rst=pst.executeQuery();
//            System.out.println("new Date ===> "+ new Date());
//            System.out.println("pst ===> "+pst);
            while(rst.next()){
            	hmResourceReq.put(rst.getString("designation_id")+rst.getString("rmonth"), rst.getString("resource_requirement"));
            }
            rst.close();
    		pst.close();
    		
			StringBuilder sbQuery1 = new StringBuilder();
			
			sbQuery1.append("select sum(resource_requirement) as count,designation_id from resource_planner_details " +
					"where financial_year_from=? and financial_year_to=? group by designation_id"); //wlocation_id=? and 
           
			Map<String ,String> hmResourceTotal=new HashMap<String, String>();
            
			pst=con.prepareStatement(sbQuery1.toString());
//            pst.setInt(1,uF.parseToInt(userLocation));
            pst.setDate(1, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
            pst.setDate(2, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));   
            rst=pst.executeQuery();
//            System.out.println("new Date ===> "+ new Date());
            while(rst.next()){
            	hmResourceTotal.put(rst.getString("designation_id"), rst.getString("count"));
            }
            rst.close();
    		pst.close();
            
            
			Map<String ,String> hmCheckDesigData = new HashMap<String, String>();
			pst=con.prepareStatement("select designation_id from resource_planner_details where financial_year_from=? " +
					"and financial_year_to=? group by designation_id"); //wlocation_id=? and 
//            pst.setInt(1,uF.parseToInt(userLocation));
            pst.setDate(1, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
            pst.setDate(2, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));   
            rst=pst.executeQuery();
//            System.out.println("new Date ===> "+ new Date());
            while(rst.next()){
            	String str = "yes";
            	hmCheckDesigData.put(rst.getString("designation_id"), str);
            }
            rst.close();
    		pst.close();
           
            request.setAttribute("hmResourceTotal", hmResourceTotal);
			request.setAttribute("hmResourceReq", hmResourceReq);
			request.setAttribute("hmCheckDesigData", hmCheckDesigData);
			

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void insertResourcePlanner() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);

			String[] desigid=request.getParameterValues("desigid");
			String yearStart=request.getParameter("yearStart");
			String yearEnd=request.getParameter("yearEnd");
			
			for(int i=0;desigid != null && i<desigid.length;i++) {
				if(checkrecord(desigid[i], yearStart, yearEnd)) {
					updateResourcePlanner(desigid[i], yearStart, yearEnd);
				} else {
					String query="insert into resource_planner_details (designation_id,financial_year_from,financial_year_to,rmonth,ryear," +
						"added_by,added_date,wlocation_id,resource_requirement) values (?,?,?,?, ?,?,?,?, ?)";
					for(int j=1;j<=12;j++) {
						String resourseRequire;
					  if(j<4) {
						  resourseRequire=request.getParameter("require"+desigid[i]+j+yearEnd);
					  }else{
						resourseRequire=request.getParameter("require"+desigid[i]+j+yearStart);
					  }
					  pst=con.prepareStatement(query);
					  pst.setInt(1,uF.parseToInt(desigid[i]));
					  pst.setDate(2,uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT) );
					  pst.setDate(3,uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT) ); 
					  pst.setInt(4,j);
					  if(j<4) {
						  pst.setInt(5,uF.parseToInt(yearEnd)); 
					  } else {
						  pst.setInt(5,uF.parseToInt(yearStart)); 
					  }
					  pst.setInt(6,uF.parseToInt(strSessionEmpId));
					  pst.setDate(7,uF.getCurrentDate(CF.getStrTimeZone()));
					  pst.setInt(8,uF.parseToInt(userLocation));
					  pst.setInt(9,uF.parseToInt(resourseRequire));
					  pst.executeUpdate();
		    		  pst.close();
					}
				}
			}
			
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}

	}

	

	private boolean checkrecord(String desigid, String yearStart,String yearEnd) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String flag = null;
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from resource_planner_details where designation_id=?" +
					" and financial_year_from=? and financial_year_to=?");
			pst.setInt(1,uF.parseToInt(desigid));
			pst.setDate(2,uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
			pst.setDate(3,uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
             flag="true";
			}
			rst.close();
    		pst.close();
	
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return uF.parseToBoolean(flag);

	}

	
	private String getManagerDesigIds() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String desigIds = null;

		try {
			con = db.makeConnection(con);
			
			if(strUserType.equals(MANAGER)) {
				List<String> designList = new ArrayList<String>();
				pst=con.prepareStatement("select supervisor_emp_id,emp_id,dd.designation_id from employee_personal_details epd join " +
						"employee_official_details eod on(epd.emp_per_id = eod.emp_id) join grades_details gd on eod.grade_id=gd.grade_id " +
						"join designation_details dd on gd.designation_id=dd.designation_id where  is_alive= true and emp_per_id >0 and " +
						"(supervisor_emp_id = ? or emp_id = ?)"); //appraisal_attribute in ("+getCheckParam()+") and 
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				rst=pst.executeQuery();
				while(rst.next()){
					if(!designList.contains(rst.getString("designation_id"))) {
						designList.add(rst.getString("designation_id"));
					}
				}
				rst.close();
	    		pst.close();
				desigIds = getAppendData(designList);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return desigIds;
		
	}

	public String getAppendData(List<String> strID) {
		StringBuilder sb = new StringBuilder();
		if (strID != null) {
			for (int i = 0; i < strID.size(); i++) {
				if (i == 0) {
					sb.append(strID.get(i));
				} else {
					sb.append("," + strID.get(i));
				}
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	
	private void getResourcePlannerDesigData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			userLocation = hmEmpLocation.get(strSessionEmpId);

			/*StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select designation_id  from designation_details where " +
					"level_id in (select level_id from level_details where level_id>0 ");
			
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and level_id="+uF.parseToInt(getF_level()));	
			}
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id="+uF.parseToInt(getF_org()));
			}
			sbQuery.append(" ) as a where a.designation_id = rpd.designation_id ");
			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and rpd.wlocation_id="+uF.parseToInt(getF_strWLocation()));
			}
			
			sbQuery.append("and rpd.financial_year_from=? and rpd.financial_year_to=? order by a.designation_id");
			*/
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM designation_details ald JOIN level_details ld ON ald.level_id = ld.level_id where ld.level_id>0 ");
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and ld.level_id="+uF.parseToInt(getF_level()));	
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and ld.org_id="+uF.parseToInt(getF_org()));
			}
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				String desigIds = getManagerDesigIds();
				sbQuery.append(" and designation_id in ("+desigIds+")");
			}
			sbQuery.append(" order by designation_name");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("pst ===> "+ pst);
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rst.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("designation_id"));
				innerList.add(rst.getString("designation_code"));
				innerList.add(rst.getString("designation_name"));
				innerList.add(rst.getString("designation_description"));
				innerList.add(rst.getString("level_id"));
				innerList.add(rst.getString("attribute_ids"));

				outerList.add(innerList);
			}
			rst.close();
    		pst.close();
			request.setAttribute("designationList", outerList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void loadData() {

		UtilityFunctions uF = new UtilityFunctions();

		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
//		levelList = new FillLevel(request).fillLevel();
		
		if(getF_org() == null) {
			if(getOrgid() != null && !getOrgid().equals("")) {
				setF_org(getOrgid());
			}else {
				setF_org(strOrgID);
				setOrgid(strOrgID); 
			}
		} else {
			setOrgid(getF_org());
		}
		
		if(strUserType.equals(MANAGER)) {
			orgList = new FillOrganisation(request).fillOrganisation(getF_org());
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices();
		departmentList = new FillDepartment(request).fillDepartment();
		desigList = new FillDesig(request).fillDesig();
		

		int intCYear = Calendar.getInstance().get(Calendar.YEAR);
		int intCMonth = Calendar.getInstance().get(Calendar.MONTH);
		
		String[] strFinancialYearDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;

		if (getF_strFinancialYear() != null) {
			strFinancialYearDates = getF_strFinancialYear().split("-");
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];
		} else {
			strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setF_strFinancialYear(strFinancialYearDates[0] + "-"+ strFinancialYearDates[1]);
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1]; 
		}
		String monthStart = uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "MM");
		String yearStart = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy");

		String monthEnd = uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"MM");
		String yearEnd = uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy");
		setF_financialYearStart(strFinancialYearStart);
		setF_financialYearEnd(strFinancialYearEnd);
//		System.out.println("getFinansyr ====== "+getFinansyr());
		if(getFinansyr() != null) {
			setF_strFinancialYear(getFinansyr());
		}
		
		if(getWlocid() != null) {
			setF_strWLocation(getWlocid());
		}
		if(getDeptid() != null) {
			setF_department(getDeptid());
		}
		if(getLvlid() != null) {
			setF_level(getLvlid());
		}

		setCurrentMonth(""+(intCMonth+1));
		setCurrentYear(""+intCYear);
//		System.out.println("getCurrentMonth() ===> "+getCurrentMonth());
//		System.out.println("getCurrentYear() ===> "+getCurrentYear());
		
		request.setAttribute("monthStart", "" + uF.parseToInt(monthStart));
		request.setAttribute("yearStart", "" + uF.parseToInt(yearStart));
		request.setAttribute("monthEnd", "" + uF.parseToInt(monthEnd));
		request.setAttribute("yearEnd", "" + uF.parseToInt(yearEnd));

	}

	public String getFinansyr() {
		return finansyr;
	}

	public void setFinansyr(String finansyr) {
		this.finansyr = finansyr;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public String getWlocid() {
		return wlocid;
	}

	public void setWlocid(String wlocid) {
		this.wlocid = wlocid;
	}

	public String getLvlid() {
		return lvlid;
	}

	public void setLvlid(String lvlid) {
		this.lvlid = lvlid;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_desig() {
		return f_desig;
	}

	public void setF_desig(String f_desig) {
		this.f_desig = f_desig;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getF_strFinancialYear() {
		return f_strFinancialYear;
	}

	public void setF_strFinancialYear(String f_strFinancialYear) {
		this.f_strFinancialYear = f_strFinancialYear;
	}

	public String getUserLocation() {
		return userLocation;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public String getF_financialYearStart() {
		return f_financialYearStart;
	}

	public void setF_financialYearStart(String f_financialYearStart) {
		this.f_financialYearStart = f_financialYearStart;
	}

	public String getF_financialYearEnd() {
		return f_financialYearEnd;
	}

	public void setF_financialYearEnd(String f_financialYearEnd) {
		this.f_financialYearEnd = f_financialYearEnd;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	private HttpServletRequest request;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	
	public String getEditResourceYearStart() {
		return editResourceYearStart;
	}

	public void setEditResourceYearStart(String editResourceYearStart) {
		this.editResourceYearStart = editResourceYearStart;
	}

	String editResourceYearEnd;

	public String getEditResourceYearEnd() {
		return editResourceYearEnd;
	}

	public void setEditResourceYearEnd(String editResourceYearEnd) {
		this.editResourceYearEnd = editResourceYearEnd;
	}

	public String getEditResourceDesig() {
		return editResourceDesig;
	}

	public void setEditResourceDesig(String editResourceDesig) {
		this.editResourceDesig = editResourceDesig;
	}

	public String getEditResource() {
		return editResource;
	}

	public void setEditResource(String editResource) {
		this.editResource = editResource;
	}

	public String getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}

	public String getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(String currentMonth) {
		this.currentMonth = currentMonth;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
