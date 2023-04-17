package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLearningEmployeeList extends ActionSupport implements
		ServletRequestAware,IStatements {

	private static final long serialVersionUID = 1L;
	
	private String location;
	private String planId;
	private String alignedwith;
	private String grade;
	private String level;
	private String desig;
	private  String org_id;
	private String boolPublished;
	
	private List<FillEmployee> empList;
	
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	

	CommonFunctions CF = null;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		empList=new FillEmployee(request).fillTrainingEmployee(location,alignedwith,level,desig,grade,org_id);
		//List<FillWLocation> wLocList = new FillWLocation(request).fillWLocation(org_id);
//		System.out.println("boolPublished =====> "+ boolPublished);
		getSelectEmployeeList();
		return SUCCESS;
	}

	private void getSelectEmployeeList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {
			
			con=db.makeConnection(con);
			Map<String,String> hmEmpLocation=CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null,null); 
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);				
			pst=con.prepareStatement("select learner_ids from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1,uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			while(rst.next()){
				selectEmpIDs=rst.getString("learner_ids");
			}
			rst.close();
			pst.close();

//			System.out.println("selectEmpIDs =====> " + selectEmpIDs);
			
			Map<String,String> hmCheckEmpList=new HashMap<String, String>();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
				List<String> tmpselectEmpList=Arrays.asList(selectEmpIDs.split(","));
				Set<String> trainerSet = new HashSet<String>(tmpselectEmpList);
				Iterator<String> itr = trainerSet.iterator();	
				while (itr.hasNext()) {
					String trainerId = (String) itr.next();
					if(trainerId!=null && !trainerId.equals("")){
						hmCheckEmpList.put(trainerId.trim(), trainerId.trim());
					}
				}
			}
//			System.out.println("hmCheckEmpList =====> " + hmCheckEmpList);
			request.setAttribute("hmCheckEmpList", hmCheckEmpList);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public String getAlignedwith() {
		return alignedwith;
	}

	public void setAlignedwith(String alignedwith) {
		this.alignedwith = alignedwith;
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

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
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

	public String getBoolPublished() {
		return boolPublished;
	}

	public void setBoolPublished(String boolPublished) {
		this.boolPublished = boolPublished;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
