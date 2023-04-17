package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class RemoveEmpFromGoal implements ServletRequestAware, SessionAware, IStatements {
	Map session;
	CommonFunctions CF;
	HttpServletRequest request;
	HttpSession sess;
	String strUserType = null;
	String strSessionEmpId = null;

	private String goalId;
	private String empId;
	private String from;
	private String typeas;
	private String proPage;
	private String minLimit;
	
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);
		
		sess = request.getSession(true);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
		
		removeEmpFromGoal();
        
		if(getFrom() != null && getFrom().equals("KRA")) {
			return "GKSUCCESS";
		} else if(getFrom() != null && getFrom().equals("Target")) {
			return "GTSUCCESS";
		}
		return "success";
	}
	
	

	private void removeEmpFromGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String,String> hmEmpMap = CF.getEmpNameMap(con, null, null);
			pst = con.prepareStatement("select * from goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getGoalId()));
			rst = pst.executeQuery();
			String goalEmpIds = null;
			String goalTitle = null;
			String measure_kra = "Goal";
			while (rst.next()) {
				goalEmpIds = rst.getString("emp_ids");
				goalTitle = rst.getString("goal_title");
				if(rst.getString("measure_kra")!=null && rst.getString("measure_kra").equals("KRA")){
					measure_kra = "KRA";
				}
				if(rst.getString("measure_kra")!=null && rst.getString("measure_kra").equals("Measure")){
					measure_kra = "Target";
				}
				
			}
			rst.close();
			pst.close();
			
			
//			System.out.println("goalEmpIds =====>> " + goalEmpIds);
			
			List<String> alEmpIds = Arrays.asList(goalEmpIds.split(","));
			StringBuilder sbEmpIds = null;
			for(int i=0; alEmpIds != null && !alEmpIds.isEmpty() && i<alEmpIds.size(); i++) {
				if(!alEmpIds.get(i).equals("") && !alEmpIds.get(i).equals(getEmpId())) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append("," + alEmpIds.get(i) + ",");
					} else {
						sbEmpIds.append(alEmpIds.get(i) + ",");
					}
				}
			}
			
			if(sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			
//			String query="";
//			query ="update goal_details set emp_ids = ? where goal_id = ?";
			
			pst = con.prepareStatement("update goal_details set emp_ids=? where goal_id=?");
			pst.setString(1, sbEmpIds.toString());
			pst.setInt(2, uF.parseToInt(getGoalId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("update goal_kras set emp_ids=? where goal_id=?");
			pst.setString(1, sbEmpIds.toString());
			pst.setInt(2, uF.parseToInt(getGoalId()));
			pst.execute();
			pst.close();
			sess.setAttribute(MESSAGE,SUCCESSM+""+hmEmpMap.get(empId)+"'s <b>"+goalTitle+"</b> "+ measure_kra +" has been deleted successfully."+END );
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getGoalId() {
		return goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	

	

	public String getProPage() {
		return proPage;
	}



	public void setProPage(String proPage) {
		this.proPage = proPage;
	}



	public String getMinLimit() {
		return minLimit;
	}



	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}
	
	


	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getTypeas() {
		return typeas;
	}



	public void setTypeas(String typeas) {
		this.typeas = typeas;
	}



	@Override
	public void setSession(Map session) {
		this.session = session;
	}

}
