package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;


import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainingPlansNameList extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;
	
	private int empId;  
	private String fromPage; 
	private String trainingPlanId;
	
	private String proPage;
	private String minLimit;
	private String strSearchJob;
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
		prepareInformation();
		return SUCCESS;
	}
	
	private void prepareInformation() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rst=null;
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con=db.makeConnection(con);
			Map<String,String> hmLocation=CF.getWLocationMap(con, null,null);
	    	
			List<List<String>> alTrainingPlan=new ArrayList<List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(plan_id) as cnt from training_plan tp left join training_schedule ts using(plan_id)");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" where (upper(training_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rst=pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rst.next()) {
				proCnt = rst.getInt("cnt");
				proCount = rst.getInt("cnt")/15;
				if(rst.getInt("cnt")%15 != 0) {
					proCount++;
				}
			}
			rst.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			int i =0;
			sbQuery = new StringBuilder();
			sbQuery.append("select * from training_plan tp left join training_schedule ts using(plan_id) ");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" where (upper(training_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			sbQuery.append(" order by plan_id desc");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 15 offset "+intOffset+"");
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst2=====>"+pst);
			rst=pst.executeQuery();
			while(rst.next()){
				
				List<String> alinner=new ArrayList<String>();
				
				alinner.add(rst.getString("plan_id"));//0
				alinner.add(rst.getString("training_title"));	//1		
				alinner.add(uF.showData(hmLocation.get(rst.getString("location_id").trim().toString()), "-") );//2	
				StringBuilder sbStauts = new StringBuilder();
				boolean statusFlag = checkTrainingPlanStatus(con, rst.getString("plan_id"));
				if(statusFlag == false) {
					sbStauts.append("<div style=\"float:left;border-left:4px solid #ff9a02;padding:10px;\" class=\"custom-legend pullout\"><div class=\"legend-info\"></div></div>");//3
				} else {
					sbStauts.append("<div style=\"float:left;border-left:4px solid #15AA08;padding:10px;\" class=\"custom-legend approved\"><div class=\"legend-info\"></div></div>");//3
				}
				if(i == 0) {
					setTrainingPlanId(rst.getString("plan_id"));
				}
				i++;
				alinner.add(sbStauts.toString());	//3		
				alTrainingPlan.add(alinner);
			}
			rst.close();
			pst.close();
			request.setAttribute("alTrainingPlan", alTrainingPlan);
					
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
		
	}

	private boolean checkTrainingPlanStatus(Connection con, String planId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean assessmentStatusFlag = false;
		try {
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Training' and learning_plan_stage_name_id = ? and ? between from_date and to_date");
			pst.setInt(1, uF.parseToInt(planId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rst = pst.executeQuery();
			while (rst.next()) {
				assessmentStatusFlag = true;
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return assessmentStatusFlag;
	}
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getTrainingPlanId() {
		return trainingPlanId;
	}

	public void setTrainingPlanId(String trainingPlanId) {
		this.trainingPlanId = trainingPlanId;
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

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}
}
