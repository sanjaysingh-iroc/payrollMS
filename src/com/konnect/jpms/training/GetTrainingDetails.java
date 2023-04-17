package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetTrainingDetails extends ActionSupport implements IStatements,
		ServletRequestAware {

	
	HttpSession session;
	private String plan_id;

	CommonFunctions CF = null; 
	
	private static final long serialVersionUID = 1L;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}

		getTrainingDetails();

		return SUCCESS;
	}

	private void getTrainingDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			if (uF.parseToInt(getPlan_id()) > 0) {
				
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				Map<String, String> hmSkillName = CF.getSkillNameMap(con);
				if(hmSkillName == null) hmSkillName = new HashMap<String, String>();
				Map<String, String> hmAttribute = new HashMap<String, String>();
				pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(getPlan_id()));
				rs = pst.executeQuery();
				String minFromDate = null, maxToDate = null; 
				while (rs.next()) {
					minFromDate = rs.getString("minDate");
					maxToDate = rs.getString("maxDate");
				}
				rs.close();
				pst.close();
				
				List<String> learningPlanList = new ArrayList<String>();
				pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(getPlan_id()));
				rs = pst.executeQuery();
				while (rs.next()) {
					String alignedWith = "";
					if(rs.getString("group_or_condition") != null && rs.getString("group_or_condition").equals("3")) {
						alignedWith = "General";
					} else if(rs.getString("group_or_condition") != null && rs.getString("group_or_condition").equals("2")) {
						alignedWith = "Gap";
					} else if(rs.getString("group_or_condition") != null && rs.getString("group_or_condition").equals("1")) {
						alignedWith = "Induction";
					} 
					learningPlanList.add(rs.getString("learning_plan_id"));
					learningPlanList.add(rs.getString("learning_plan_name"));
					learningPlanList.add(rs.getString("learning_plan_objective"));
					learningPlanList.add(alignedWith);
					learningPlanList.add(uF.showData(getAppendData(rs.getString("learner_ids"), hmEmpName),"")); //asignee
					learningPlanList.add(uF.showData(getAppendData(rs.getString("attribute_id"), hmAttribute),"")); //Associated With Attribute
					learningPlanList.add(uF.showData(CF.getCertificateName(con, rs.getString("certificate_id")), ""));
					
					learningPlanList.add(uF.showData(getAppendData(rs.getString("skills"), hmSkillName), ""));
					learningPlanList.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat()));
					learningPlanList.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat()));
					learningPlanList.add(getLearningStageType(con, uF, uF.parseToInt(getPlan_id())));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("learningPlanList", learningPlanList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
private String getLearningStageType(Connection con, UtilityFunctions uF, int learningPlanId) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		StringBuilder learningType = new StringBuilder();
		try {
			Map<String,String> hmwlocation = CF.getWLocationMap(con, null, null);
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ?");
			pst.setInt(1, learningPlanId);
			rst = pst.executeQuery();
			 List<String> learningTypeList = new ArrayList<String>();
			while(rst.next()) {
				learningTypeList.add(rst.getString("learning_type"));
			}
			rst.close();
			pst.close();
			
			int a=0,b=0,c=0;
			for (int i = 0; learningTypeList != null && !learningTypeList.isEmpty() && i < learningTypeList.size(); i++) {
				if(learningTypeList.get(i).equals("Training") && a == 0){
					a++;
				} else if(learningTypeList.get(i).equals("Course") && b == 0){
					b++;
				}  else if(learningTypeList.get(i).equals("Assessment") && c == 0){
					c++;
				} 
			}
				if(a == 1 && b == 0 && c == 0) {
					learningType.append("Training");
				} else if(a == 0 && b == 1 && c == 0) {
					learningType.append("Course");
				} else if(a == 0 && b == 0 && c == 1) {
					learningType.append("Assessment");
				} else if((a == 1 && b == 1 && c == 1) || (a == 1 && b == 1 && c == 0) || (a == 1 && b == 0 && c == 1) || (a == 0 && b == 1 && c == 1)) {
					learningType.append("Hybrid");
				}
				
		}catch(Exception e){
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
		return learningType.toString();
	}
	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("") && !strID.isEmpty() && strID.length()>1) {
			strID = strID.substring(1, strID.length()-1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}

		} else {
			return null;
		}

		return sb.toString();
	}
	
	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
