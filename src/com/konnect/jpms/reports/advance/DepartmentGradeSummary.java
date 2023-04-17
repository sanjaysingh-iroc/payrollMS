package com.konnect.jpms.reports.advance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DepartmentGradeSummary extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4517776879787852532L;
	CommonFunctions CF = null;
	HttpSession session;
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillLevel> levelList;
	String f_strWLocation;
	String f_org;
	
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

	
	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	String level;
	private HttpServletRequest request;
	public String execute() throws Exception {

		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		UtilityFunctions uF=new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/reports/advance/DepartmentGradeSummary.jsp");
		request.setAttribute(TITLE, "Depart/Grade Summary Report");
		if(uF.parseToInt(getF_org())>0){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
			
		}else{
			wLocationList = new FillWLocation(request).fillWLocation();
			levelList = new FillLevel(request).fillLevel();
		}
		orgList = new FillOrganisation(request).fillOrganisation();
		
		
		getBirthday();
		 
		return LOAD;
	}

public void getBirthday(){
	
	Connection con = null;
	PreparedStatement pst=null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF=new UtilityFunctions();
	
	try {
		con = db.makeConnection(con);
//		String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-dd");
//		String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"", DBDATE, "MM-dd");
//		String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2)+"", DBDATE, "MM-dd");
		
		
		StringBuilder sbQuery = new StringBuilder("select a.*,gd.grade_name from(select a.*,di.dept_name from(SELECT count(*) as cnt,depart_id,grade_id FROM employee_personal_details epd , employee_official_details eod where epd.emp_per_id=eod.emp_id");
		
	
		
		if(uF.parseToInt(getF_org())>0){
			sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
		}			
		if(uF.parseToInt(getF_strWLocation())>0){
			sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
		}
		if(uF.parseToInt(getLevel())>0){
			sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getLevel())+")");
		}
		
		sbQuery.append(" group by depart_id,grade_id)a LEFT JOIN department_info di  on di.dept_id=a.depart_id )a LEFT JOIN grades_details gd  on a.grade_id=gd.grade_id");			

//			sbQuery.append(" order by DATEPART(mm,emp_date_of_birth),DATEPART(dd,emp_date_of_birth) ");			
		
		pst = con.prepareStatement(sbQuery.toString());
		rs = pst.executeQuery();
		
		
		Map<String,String> empCountMp=new HashMap<String,String>();
		Map<String,String> gradeMp=new HashMap<String,String>();

		Map<String,String> departMp=new HashMap<String,String>();
		List<String> gradeList = new ArrayList<String>();
		List<String> departList = new ArrayList<String>();
//		List<List<String>> outerList=new ArrayList<List<String>>();
		while (rs.next()) {
			if(!gradeList.contains(rs.getString("grade_id"))){
				gradeList.add(rs.getString("grade_id"));
				gradeMp.put(rs.getString("grade_id"), rs.getString("grade_name"));
			}
			if(!departList.contains(rs.getString("grade_id"))){
				departList.add(rs.getString("depart_id"));
				departMp.put(rs.getString("depart_id"), rs.getString("dept_name"));
			}
			empCountMp.put(rs.getString("depart_id")+"_"+rs.getString("grade_id"), rs.getString("cnt"));
			
		}
		rs.close();
		pst.close();
		
		request.setAttribute("empCountMp",empCountMp);
		request.setAttribute("gradeMp",gradeMp);
		request.setAttribute("departMp",departMp);
		request.setAttribute("gradeList",gradeList);
		request.setAttribute("departList",departList);

		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
@Override
public void setServletRequest(HttpServletRequest request) {
	this.request = request;
}
}