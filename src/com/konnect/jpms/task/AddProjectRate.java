package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddProjectRate extends ActionSupport implements ServletRequestAware, IStatements {
	HttpServletRequest request;
	
	CommonFunctions CF; 
	HttpSession session; 
	String strSessionEmpId;
	String strStdHrs;
	/*String skill;*/
	
	String service_porject_id;
	List<FillLevel> levelList;
	List<FillWLocation> wLocationList;
	String lavel;
	String wLocation;
	String rate;
	String operation;
	String ID;
	String rate1;
	String monthRate;
	String skillId;
	

	String userscreen;
	String navigationId;
	String toPage;
	
	public String getMonthRate() {
		return monthRate;
	}

	public void setMonthRate(String monthRate) {
		this.monthRate = monthRate;
	}

	public String getRate1() {
		return rate1;
	}

	public void setRate1(String rate1) {
		this.rate1 = rate1;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getLavel() {
		return lavel;
	}

	public void setLavel(String lavel) {
		this.lavel = lavel;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String execute() throws Exception {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/AddTaskService.jsp");
		request.setAttribute(TITLE, "Add New Project");
		levelList = new FillLevel(request).fillLevel();
		wLocationList = new FillWLocation(request).fillWLocation();
		
		strStdHrs = CF.getStrStandardHrs();
		request.setAttribute("strStdHrs", strStdHrs);
		
		if(operation!=null){
			if(operation.equals("E")){
				getSkillRate();
			}else if(operation.equals("D")){
				deleteRate();
				return "update";
			}else if(operation.equals("A")){
				updateSkillRate();
				return "update";
			}
		}else if(lavel != null) {
			addSkillRate();
			return "update";
		}
		
		return SUCCESS;
	}
	
	public void getSkillRate() {


		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from level_skill_rates where emp_skill_rate_id=? ");
			pst.setInt(1, uF.parseToInt(ID));
			
			rs=pst.executeQuery();
			while(rs.next()){
				lavel = rs.getString("level_id");
				wLocation = rs.getString("wlocation_id");
				rate = rs.getString("rate_per_day");
				rate1 = rs.getString("rate_per_hour");
				monthRate = rs.getString("rate_per_month");
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
	}
	
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public void updateSkillRate() {
//		System.out.println("updateSkills==============");

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;

		try {

			con = db.makeConnection(con);
			String currId = CF.getOrgCurrencyId(con, wLocation);
			pst = con.prepareStatement("update level_skill_rates set wlocation_id=?, level_id=?, rate_per_day=?, rate_per_hour=?, rate_per_month=?, " +
				"curr_id=? where emp_skill_rate_id=?");
			pst.setInt(1, uF.parseToInt(wLocation));
			pst.setInt(2, uF.parseToInt(lavel));
			pst.setDouble(3, uF.parseToDouble(rate));
			pst.setDouble(4, uF.parseToDouble(rate1));
			pst.setDouble(5, uF.parseToDouble(monthRate));
			pst.setInt(6, uF.parseToInt(currId));
			pst.setInt(7, uF.parseToInt(ID));
			pst.executeUpdate();
			pst.close();
			
//			System.out.println("pst========"+pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	
	public void deleteRate() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from level_skill_rates where emp_skill_rate_id=? ");
			pst.setInt(1, uF.parseToInt(ID));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void addSkillRate() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			String currId = CF.getOrgCurrencyId(con, wLocation);
			boolean flag =false;
			pst = con.prepareStatement("select * from level_skill_rates where service_project_id=? and wlocation_id=? and level_id=? and skill_id=? ");//skill_name,
			pst.setInt(1, uF.parseToInt(service_porject_id));
			pst.setInt(2, uF.parseToInt(wLocation));
			pst.setInt(3, uF.parseToInt(lavel));
			pst.setInt(4, uF.parseToInt(getSkillId()));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(!flag) {
				pst = con.prepareStatement("insert into level_skill_rates(service_project_id,wlocation_id,level_id,rate_per_day,rate_per_hour," +
					"skill_id,curr_id,rate_per_month) values(?,?,?,?, ?,?,?,?) ");//skill_name,
	//			pst.setString(1, skill);
				pst.setInt(1, uF.parseToInt(service_porject_id));
				pst.setInt(2, uF.parseToInt(wLocation));
				pst.setInt(3, uF.parseToInt(lavel));
				pst.setDouble(4, uF.parseToDouble(rate));
				pst.setDouble(5, uF.parseToDouble(rate1));
				pst.setInt(6, uF.parseToInt(getSkillId()));
				pst.setInt(7, uF.parseToInt(currId));
				pst.setDouble(8, uF.parseToDouble(monthRate));
				pst.execute();
				pst.close();
			} else {
				
			}
//			System.out.println("pst==>"+pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getService_porject_id() {
		return service_porject_id;
	}

	public void setService_porject_id(String service_porject_id) {
		this.service_porject_id = service_porject_id;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		// TODO Auto-generated method stub
		
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getStrStdHrs() {
		return strStdHrs;
	}

	public void setStrStdHrs(String strStdHrs) {
		this.strStdHrs = strStdHrs;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}
