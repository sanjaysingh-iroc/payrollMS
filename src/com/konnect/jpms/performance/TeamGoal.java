package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class TeamGoal implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF; 

	private String dataType;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/TeamGoal.jsp");
		request.setAttribute(TITLE, "Team Goal");
		
		getTeamDetails();  
		
		return "success";

	}

	void getTeamDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		Map<String,List<List<String>>> hmTeam=new HashMap<String, List<List<String>>>();
		Map<String, Map<String, String>> hmIndGoalCalDetailsTeam = new HashMap<String, Map<String, String>>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,String> hmAttribute=getAttributeMap(con);
			StringBuilder sbQuery = new StringBuilder();	
			if(strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)){
				sbQuery.append("select * from goal_details where goal_type = "+TEAM_GOAL+" and goal_id in(select goal_parent_id from goal_details " +
						" where (goal_type = "+INDIVIDUAL_GOAL+" or (goal_type = "+PERSONAL_GOAL+" and goalalign_with_teamgoal = true)) and " +
						"org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%') ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and is_close = true ");
				}
				sbQuery.append(" order by goal_id desc");
				pst=con.prepareStatement(sbQuery.toString());
			}else{
				sbQuery.append("select * from goal_details where goal_type = "+TEAM_GOAL+" ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and is_close = true ");
				}
				sbQuery.append(" order by goal_id desc");
				pst = con.prepareStatement(sbQuery.toString());
			}
//			System.out.println("pst ======> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList=hmTeam.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName, uF), ""));
				innerList.add(rs.getString("entry_date"));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				String priority="";
				String pClass="";
				if(rs.getString("priority")!=null && !rs.getString("priority").equals("")){
					if(rs.getString("priority").equals("1")){
						pClass="high";
						priority="High";
					}else if(rs.getString("priority").equals("2")){
						pClass="medium";
						priority="Medium";
					}else if(rs.getString("priority").equals("3")){
						pClass="low";
						priority="Low";
					}
				}
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(priority);
				innerList.add(pClass);
				
				outerList.add(innerList);
				hmTeam.put(rs.getString("goal_parent_id"), outerList);
				
				Map<String, String> hmIndGoalCalDetailsParent = new HashMap<String, String>();
				getIndGoalData(con, uF, rs.getString("goal_id"), hmIndGoalCalDetailsParent);
				hmIndGoalCalDetailsTeam.put(rs.getString("goal_id"), hmIndGoalCalDetailsParent);
				
			}
			rs.close();
			pst.close();
			
			
			
			StringBuilder sbQue = new StringBuilder();
			sbQue.append("select * from goal_details where ((goal_type=? and goalalign_with_teamgoal=true) or goal_type=?) and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%' ");
			if(getDataType() != null && getDataType().equals("L")) {
				sbQue.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQue.append(" and is_close = true ");
			}
			sbQue.append(" order by goal_id desc");
//			pst = con.prepareStatement("select * from goal_details where ((goal_type=? and goalalign_with_teamgoal=true) or goal_type=?) and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%' order by goal_id desc");//(goal_type=4 or goal_type=5)
			pst = con.prepareStatement(sbQue.toString());
			pst.setInt(1, PERSONAL_GOAL);
			pst.setInt(2, INDIVIDUAL_GOAL);
			rs = pst.executeQuery();
			Map<String,List<List<String>>> hmIndGoalIdWithType = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<List<String>> outerList = hmIndGoalIdWithType.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				if(uF.parseToBoolean(rs.getString("is_measure_kra")) == false) {
					innerList.add("Goal");
					if(rs.getInt("goal_type") == INDIVIDUAL_GOAL){
						innerList.add("Individual Goal");
					} else if(rs.getInt("goal_type") == PERSONAL_GOAL){
						innerList.add("Personal Goal");
					} 
				} else if(uF.parseToBoolean(rs.getString("is_measure_kra")) == true && !rs.getString("measure_type").equals("")) { //is_measure_kra = true and gd.measure_type !=''
					innerList.add("Target");
					if(rs.getInt("goal_type") == INDIVIDUAL_GOAL){
						innerList.add("Individual Target");
					} else if(rs.getInt("goal_type") == PERSONAL_GOAL){
						innerList.add("Personal Target");
					} 
				} else if(rs.getString("measure_type") == null || rs.getString("measure_type").equals("")) {
					innerList.add("KRA");
					if(rs.getInt("goal_type") == INDIVIDUAL_GOAL){
						innerList.add("Individual KRA");
					} else if(rs.getInt("goal_type") == PERSONAL_GOAL){
						innerList.add("Personal KRA");
					} 
				}
				
				outerList.add(innerList);
				hmIndGoalIdWithType.put(rs.getString("goal_parent_id"), outerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmIndGoalIdWithType ===> " + hmIndGoalIdWithType);
			
			request.setAttribute("hmIndGoalIdWithType", hmIndGoalIdWithType);
			request.setAttribute("hmTeam", hmTeam);
			request.setAttribute("hmIndGoalCalDetailsTeam", hmIndGoalCalDetailsTeam);
//			System.out.println("hmTeam ===> " + hmTeam);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getIndGoalData(Connection con, UtilityFunctions uF, String parentID, Map<String, String> hmIndGoalCalDetailsParent){
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,String>> hmIndGoalCalTeam= new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmIndGoalCalTeamParent= new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? and is_measure_kra = true and measure_type !='' order by goal_id ");
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
//			System.out.println("PST===> "+pst);
			while (rs.next()) {
				
				Map<String, String> hmIndGoalCalDetails = new HashMap<String, String>();
				getIndividualGoalTargetCalculation(con, rs.getString("goal_id"), rs.getString("emp_ids"), rs.getString("measure_type"), rs.getString("measure_currency_value"),
						rs.getString("measure_effort_days"), rs.getString("measure_effort_hrs"), uF, hmIndGoalCalDetails);
//				System.out.println("hmIndGoalCalDetails==="+hmIndGoalCalDetails);
				hmIndGoalCalTeam.put(rs.getString("goal_id"), hmIndGoalCalDetails);
				hmIndGoalCalTeamParent.put(rs.getString("goal_parent_id"), hmIndGoalCalTeam);
			}
			rs.close();
			pst.close();
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
			Iterator it1 = hmIndGoalCalTeamParent.keySet().iterator();
			while (it1.hasNext()) {
				String parentid =(String)it1.next();
//				System.out.println("parentid ===> "+parentid);
				Map<String,Map<String,String>> hmIndGoalCalTeam1 = hmIndGoalCalTeamParent.get(parentid);
				Iterator it2 = hmIndGoalCalTeam1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
					Map<String, String> hmIndGoalCalDetails = hmIndGoalCalTeam1.get(goalid);
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_PERCENT"));
					dblalltotal100 += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_TOTAL"));
					dblstrtwoDeciTot += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_STR_PERCENT"));
					cnt++;
				}
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
				hmIndGoalCalDetailsParent.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmIndGoalCalDetailsParent.put(parentid+"_TOTAL", alltotal100);
				hmIndGoalCalDetailsParent.put(parentid+"_STR_PERCENT", strtwoDeciTot);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	private void getIndividualGoalTargetCalculation(Connection con, String indGoalId, String empIds, String measureType, String targetAmt, String strTargetDays, String strTargetHrs, UtilityFunctions uF,Map<String, String> hmIndGoalCalDetails){
		String alltwoDeciTotProgressAvg ="0";
 		String alltotal100 ="100";
 		String strtwoDeciTot = "0";
 		String strTotTarget = "0";
 		String strTotDays = "0";
 		String strTotHrs = "0";
 		Map<String,String> hmTargetValue=getMaxAchievedTargetBYEmpAndGoalwise(con, uF);
		if(empIds !=null){
			List<String> emplistID=Arrays.asList(empIds.split(","));
			double alltotalTarget=0, allTotal=0, alltwoDeciTot=0, totTarget=0;
			int empListSize=0;
			int allTotHRS =0;
//			System.out.println(" indGoalId ===> "+indGoalId+" emplistID.size() ===> "+emplistID.size()+" emplistID ===> "+emplistID);
			for(int i=0; emplistID!=null && i<emplistID.size();i++){
				empListSize = emplistID.size()-1;		
			if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
			String target="0";
			if(hmTargetValue != null && hmTargetValue.get(emplistID.get(i)+"_"+indGoalId)!= null){
				target=hmTargetValue.get(emplistID.get(i)+"_"+indGoalId);
			}
//			String dayhrs = iinnerList.get(10)+"."+iinnerList.get(11);
			
			String twoDeciTotProgressAvg = "0";
			String twoDeciTot = "0";
			String total="100";
			double totalTarget=0;
			if(measureType!=null && !measureType.equals("Effort")){
				totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(targetAmt))*100;
				twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
			}else{
				String t=""+uF.parseToDouble(target);
				String days="0";
				String hours="0";
				if(t.contains(".")){
					t=t.replace(".","_");
					String[] temp=t.split("_");
					days=temp[0];
					hours=temp[1];
				}	
				String targetDays = strTargetDays;
				String targetHrs = strTargetHrs;
				int daysInHrs = uF.parseToInt(days) * 8;
				int inttotHrs = daysInHrs + uF.parseToInt(hours);
				allTotHRS += inttotHrs;
				
				int targetDaysInHrs = uF.parseToInt(targetDays) * 8;
				int inttotTargetHrs = targetDaysInHrs + uF.parseToInt(targetHrs);
				//System.out.println("inttotTargetHrs = "+ inttotTargetHrs + " inttotHrs = "+ inttotHrs + " allTotHRS = "+ allTotHRS);
				if(inttotTargetHrs != 0){
					totalTarget= uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
				}
				twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
			}
				if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
					double totalTarget1=(totalTarget/150)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="150";
				}else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
					double totalTarget1=(totalTarget/200)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="200";
				}else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
					double totalTarget1=(totalTarget/250)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="250";
				}else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
					double totalTarget1=(totalTarget/300)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="300";
				}else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
					double totalTarget1=(totalTarget/350)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="350";
				}else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
					double totalTarget1=(totalTarget/400)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="400";
				}else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
					double totalTarget1=(totalTarget/450)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="450";
				}else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
					double totalTarget1=(totalTarget/500)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="500";
				}else{
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
					if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
						twoDeciTotProgressAvg = "100";
						total=""+Math.round(totalTarget);
					}else{
						total="100";
					}
//					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
//					total="100";
				}
				alltotalTarget += uF.parseToDouble(twoDeciTotProgressAvg);
				allTotal += uF.parseToDouble(total);
				alltwoDeciTot += uF.parseToDouble(twoDeciTot);
				totTarget += uF.parseToDouble(target);
			}
			}
//			System.out.println("alltotalTarget === "+alltotalTarget);
			double alltotAvg = alltotalTarget/empListSize;
			double alltot100Avg = allTotal/empListSize;
			double alltwoDeciTotAvg = alltwoDeciTot/empListSize;
			double allTotTagetAvg = totTarget/empListSize;
			int allTotHRSAvg = allTotHRS / empListSize;
			int avgDAYS = allTotHRSAvg / 8;
			int avgHRS  = allTotHRSAvg % 8;
			strTotDays = ""+avgDAYS;
			strTotHrs = ""+avgHRS;
			alltwoDeciTotProgressAvg = ""+Math.round(alltotAvg);
			alltotal100 = ""+Math.round(alltot100Avg);
			strtwoDeciTot = ""+Math.round(alltwoDeciTotAvg);
			strTotTarget = ""+Math.round(allTotTagetAvg);
		}
		hmIndGoalCalDetails.put(indGoalId+"_PERCENT", alltwoDeciTotProgressAvg);
		hmIndGoalCalDetails.put(indGoalId+"_TOTAL", alltotal100);
		hmIndGoalCalDetails.put(indGoalId+"_STR_PERCENT", strtwoDeciTot);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_TARGET", strTotTarget);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_DAYS", strTotDays);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_HRS", strTotHrs);
		
	}
	
	
	private Map<String, String> getMaxAchievedTargetBYEmpAndGoalwise(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmTargetValue = new HashMap<String,String>();
		try {
			
			Map<String, String> hmTargetID= new HashMap<String,String>();
			Map<String, String> hmTargetTmpValue= new HashMap<String,String>();
			//Map<String, String> hmUpdateBy= new HashMap<String,String>();
			pst = con.prepareStatement("select * from target_details where target_id in (select max(target_id) from target_details group by goal_id,emp_id)");
			rs= pst.executeQuery();
			while(rs.next()){
				hmTargetValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("amt_percentage"));
				hmTargetID.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("target_id"));
				hmTargetTmpValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("emp_amt_percentage"));
				//hmUpdateBy.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), hmEmpCodeName.get(rs.getString("added_by"))+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmTargetValue", hmTargetValue);
//			System.out.println("hmTargetValue ===> " + hmTargetValue);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmTargetValue;
	}
	
	
	private Map<String, String> getAttributeMap(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmAttribute=new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
			rs=pst.executeQuery();
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmAttribute;
	}

	private String getAppendData(Connection con, String strID, Map<String, String> mp, UtilityFunctions uF) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
		
		if (strID != null && !strID.equals("")) {
			strID=strID.substring(1,strID.length()-1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim())+" ("+uF.showData(hmDesignation.get(temp[i].trim()), "")+")");
					} else {
						sb.append(", " + mp.get(temp[i].trim())+" ("+uF.showData(hmDesignation.get(temp[i].trim()), "")+")");
					}
				}
			} else {
				return mp.get(strID)+" ("+hmDesignation.get(strID)+")";
			}

		} else {
			return null;
		}

		return sb.toString();
	}
	
	
	
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}




	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
	
}
