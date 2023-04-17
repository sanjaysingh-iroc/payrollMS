package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import java.util.*;


public class BscPerspectiveDetails extends ActionSupport implements ServletRequestAware, IStatements {
	public HttpSession session;
	public CommonFunctions CF;
	
	private String strSessionEmpId;
	private String strEmpOrgId;
	private String strUserType;
	private String strBaseUserType;
	private String strBscId;
	private String currUserType;
	private String dataType;
	
	Map<String,List<String>> hmperspectiveDetails = new HashMap<String,List<String>>();
	
	public String execute() {
	//	System.out.println("In BscPerspectiveDetails");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);

		UtilityFunctions uF = new UtilityFunctions();
//		getPerspectiveDetails1(uF);
		getPerspectiveDetails(uF);
		getEmpImage(uF);
		getKRARatingAndCompletionStatus(uF);
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		System.out.println("returning load");
		return LOAD;
	}
	
	
	private void getPerspectiveDetails1(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String strPerspectives = null;
		List<String> alBscList = new ArrayList<String>();
		List<List<String>> alOuter= new ArrayList <List<String>>();
		Map<String, List<List<String>>> hmGoalKra = new LinkedHashMap<String, List<List<String>>>();
		Map<String, Map<String, List<List<String>>>> hmGoalKraSuperIdwise = new HashMap<String, Map<String, List<List<String>>>>();
		Map<String, Map<String, Map<String, List<List<String>>>>> hmGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
		Map<String, List<String>> hmGoalDetails = new HashMap<String, List<String>>();
		Map<String, List<String>> hmGoalKraPerspective = new HashMap<String, List<String>>();
		Map<String,List<Map<String, List<String>>>> hmgoalunderPerspective = new HashMap<String,List<Map<String, List<String>>> >();
		
		Map<String, String> hmEmpwiseKRACnt = new HashMap<String, String>();
		
		Map<String,String> hmGoal = new HashMap<String,String>();
		Map<String,List<String>> hmGoal1 = new HashMap<String,List<String>>();
		Map<String,String> hmGoaldetailsData = new HashMap<String,String>();
		Map<String,String> hmEmpName= new HashMap<String,String>();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmGoalName = CF.getGoalNameMap(con, uF);
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpUserId = CF.getEmpUserIdMap(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			
			Map<String, List<List<String>>> hmKRATasks = new LinkedHashMap<String, List<List<String>>>();
			pst=con.prepareStatement("select * from bsc_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				alBscList.add(rs.getString("bsc_perspective_ids"));
			}
			rs.close();
			pst.close();
			
			if(getStrBscId() == null || getStrBscId().equals(" ") || getStrBscId() == " "){
				setStrBscId(alBscList.get(1));
			}
			
			String perspectiveIds1 = null;
			pst = con.prepareStatement("select * from bsc_details where bsc_id = ? ");
			
			pst.setInt(1,uF.parseToInt(getStrBscId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				perspectiveIds1 = rs.getString("bsc_perspective_ids");
			}
		
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.is_alive = true");
			rs=pst.executeQuery();
			while(rs.next()){
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+" "+ rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			
			
			String[] perspecives = perspectiveIds1.split(",");
			for(int i = 0 ;i <perspecives.length;i++){
					
				if(perspecives[i] != null && perspecives[i].length() > 0 ){
					pst = con.prepareStatement("select * from goal_details where perspective_id = ? ");
					pst.setInt(1,uF.parseToInt(perspecives[i]));
					System.out.println("pst goald===>"+pst);
					rs = pst.executeQuery();
					List<String> goalList = new ArrayList<String>();
					while (rs.next()) {
						hmGoal.put(rs.getString("goal_id"), rs.getString("perspective_id") );
						goalList.add(rs.getString("goal_id"));
						}
						hmGoal1.put(perspecives[i], goalList);
						}
			}
			rs.close();
			pst.close();
		
			request.setAttribute("hmGoaldetailsData", hmGoaldetailsData);
			request.setAttribute("hmGoal", hmGoal);
			request.setAttribute("goalList", hmGoal1);
			pst=con.prepareStatement("select * from bsc_details where bsc_id = ?");
			pst.setInt(1, uF.parseToInt(getStrBscId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				strPerspectives = rs.getString("bsc_perspective_ids");
			}
			rs.close();
			pst.close();
			
			
			if(strPerspectives != null && strPerspectives.length() > 0){
				String[] bscperspectiveIds = strPerspectives.split(",");
				for(int i = 0; i<bscperspectiveIds.length;i++){
					if(bscperspectiveIds[i].length() > 0 && bscperspectiveIds[i] != null){
					
					pst=con.prepareStatement("select * from bsc_perspective_details where bsc_perspective_id = ?");
					pst.setInt(1, uF.parseToInt(bscperspectiveIds[i]));
					System.out.println("pst 1==>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						List<String> alInnerList = new ArrayList<String>();
						alInnerList.add(rs.getString("bsc_perspective_name"));
						alInnerList.add(rs.getString("perspective_color"));
						
						hmperspectiveDetails.put(bscperspectiveIds[i],alInnerList);
					}
					rs.close();
					pst.close();
					
					List<Map<String, List<String>>> alhmgoallist = new ArrayList<Map<String, List<String>>>();
					pst=con.prepareStatement("select freq_end_date,freq_start_date,gd.*, gk.kra_description,gk.goal_kra_id,gk.is_assign,gk.kra_weightage,gdf.goal_freq_id ,gdf.goal_freq_name from goal_details gd, goal_kras gk,goal_details_frequency gdf where gd.goal_id=gk.goal_id and gd.goal_id=gdf.goal_id  and gd.perspective_Id = ? and gd.is_close = false  order by freq_start_date");
					pst.setInt(1, uF.parseToInt(bscperspectiveIds[i]));
					rs = pst.executeQuery();
					while(rs.next()){
					if(rs.getInt("goal_type") == EMPLOYEE_KRA && rs.getString("is_assign") != null && rs.getString("is_assign").equals("f")) {
							continue;
						}
						int kraCnt = uF.parseToInt(hmEmpwiseKRACnt.get(bscperspectiveIds[i]));
						kraCnt++;
						String strCnt = kraCnt+"";
						hmEmpwiseKRACnt.put(bscperspectiveIds[i], strCnt);
						hmGoalKraSuperIdwise = hmGoalKraEmpwise.get(bscperspectiveIds[i]);
						if(hmGoalKraSuperIdwise == null) hmGoalKraSuperIdwise = new LinkedHashMap<String, Map<String, List<List<String>>>>();
						
						String superId = rs.getString("super_id");
						if(uF.parseToInt(superId) == 0) {
							//superId = rs.getString("goal_freq_id");
						}
						hmGoalKra = hmGoalKraSuperIdwise.get(superId);
						if(hmGoalKra == null) hmGoalKra = new LinkedHashMap<String, List<List<String>>>();
						
						List<List<String>> outerList = hmGoalKra.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"));
						if(outerList==null) outerList = new ArrayList<List<String>>();
						
						List<String> innerList=new ArrayList<String>();
						innerList.add(bscperspectiveIds[i]);
						innerList.add(rs.getString("goal_id"));
						innerList.add(rs.getString("kra_description"));
						innerList.add(rs.getString("goal_title"));
						if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))) {
							innerList.add("Self");
						} else {
							innerList.add(uF.showData(hmEmpCodeName.get(rs.getString("user_id")), "-"));
						}
						
						innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()),"-")); //5
						
						String tGoalId = getPerentGoalId(con, uF, rs.getString("goal_id"));
						innerList.add(tGoalId); // team goal id 6
						String mGoalId = getPerentGoalId(con, uF, tGoalId);
						innerList.add(mGoalId); // manager goal id 7
						String cGoalId = getPerentGoalId(con, uF, mGoalId);
						innerList.add(cGoalId); // Corporate goal id 8
						String priority="";
						String pClass="";
						if(rs.getString("priority")!=null && !rs.getString("priority").equals("")) {
							if(rs.getString("priority").equals("1")) {
								pClass="high";
								priority="High";
							} else if(rs.getString("priority").equals("2")) {
								pClass="medium";
								priority="Medium";
							} else if(rs.getString("priority").equals("3")) {
								pClass="low";
								priority="Low";
							}
						}
						innerList.add(priority); // Priority 9
						innerList.add(pClass); // Priority class 10
						innerList.add(rs.getString("goal_kra_id")); //11
						innerList.add(rs.getString("is_close")); //12
						innerList.add(rs.getString("goal_objective")); //13
						innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),"")); //14
						innerList.add(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()) ); //15
						
						innerList.add(rs.getString("weightage")); //16

						innerList.add(rs.getString("goal_parent_id")); //17
						innerList.add(rs.getString("goal_type")); //18
						innerList.add(rs.getString("measure_type")); //19
						innerList.add(rs.getString("measure_kra")); //20
						String val="",daysHRVal="";
						if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")) {
							val=" "+rs.getString("measure_effort_days")+" Days and "+rs.getString("measure_effort_hrs")+" Hrs.";
							daysHRVal = (rs.getString("measure_effort_days") != null && !rs.getString("measure_effort_days").equals("") ? rs.getString("measure_effort_days") : "0")+"."
							+(rs.getString("measure_effort_hrs") != null && !rs.getString("measure_effort_hrs").equals("") ? rs.getString("measure_effort_hrs") : "0");
						} else {
							val= uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("measure_currency_value")));
						}
						innerList.add(val); //21
						innerList.add(daysHRVal); //22

						if(rs.getString("freq_end_date") != null) {
							Date dtDeadLine = uF.getDateFormat(rs.getString("freq_end_date"), DBDATE);
							Date dtCurrDate = uF.getCurrentDate(CF.getStrTimeZone());
							if(dtCurrDate.after(dtDeadLine)) {
								innerList.add("1"); //23
							} else {
								innerList.add("0"); //23
							}
						} else {
							innerList.add("0"); //23
						}
						innerList.add(rs.getString("goalalign_with_teamgoal")); //24
						innerList.add(uF.showData(hmGoalName.get(rs.getString("goal_parent_id")), "")); //25
						innerList.add(rs.getString("close_reason")); //26
						innerList.add(rs.getString("kra_weightage")); //27
						innerList.add(rs.getString("peer_ids")); //28
						innerList.add(rs.getString("manager_ids")); //29
						innerList.add(rs.getString("hr_ids")); //30
						innerList.add(rs.getString("anyone_ids")); //31
						innerList.add(rs.getString("goal_freq_id")); //32
						innerList.add(rs.getString("goal_freq_name")); //33
						innerList.add(rs.getString("orientation_id")); //34
						innerList.add(rs.getString("emp_ids")); //35
						innerList.add(rs.getString("user_id"));//36
						//String strStatusAndCount = getAllEmpFinalisationStatus(con, uF, rs.getString("goal_id"), rs.getString("goal_freq_id"), rs.getString("emp_ids"), empList.get(i));
						//String tempStr[] = strStatusAndCount.split("::::");
						innerList.add(" ");//37
						innerList.add(" ");//38
						innerList.add(rs.getString("perspective_id"));//39
						innerList.add(rs.getString("align_with_perspective"));//40
						
						
						
						outerList.add(innerList);
						alOuter.add(innerList);
						hmGoalDetails.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), innerList);
						alhmgoallist.add(hmGoalDetails);
						hmGoalKraPerspective.put(rs.getString("goal_id"), innerList);
						hmGoalKraEmpwise.put(bscperspectiveIds[i], hmGoalKraSuperIdwise);
						hmGoalKra.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), outerList);
						hmGoalKraSuperIdwise.put(bscperspectiveIds[i], hmGoalKra);
					}
					rs.close();
					pst.close();
					
					hmgoalunderPerspective.put(bscperspectiveIds[i],alhmgoallist );
					pst = con.prepareStatement("select gkt.goal_kra_task_id, gkt.task_name,gkt.added_by,gkt.kra_id,gdf.goal_id,gdf.goal_freq_id,gdf.freq_end_date" +
								" from goal_kra_tasks gkt, goal_details_frequency gdf where gkt.goal_id = gdf.goal_id and (gdf.is_delete is null or gdf.is_delete = false)  order by goal_kra_task_id");
						System.out.println("hmkratask pst==>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							//List<List<String>> outerList = hmKRATasks.get(empList.get(i)+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id"));
							List<List<String>> outerList = hmKRATasks.get(bscperspectiveIds[i]+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id"));
							if (outerList == null) outerList = new ArrayList<List<String>>();
							
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("goal_kra_task_id"));//0
							innerList.add(rs.getString("task_name"));//1
//							System.out.println("task_id==>"+rs.getString("goal_kra_task_id")+"==>task_name==>"+rs.getString("task_name"));
							if(rs.getString("freq_end_date") != null) {
								Date dtDeadLine = uF.getDateFormat(rs.getString("freq_end_date"), DBDATE);
								Date dtCurrDate = uF.getCurrentDate(CF.getStrTimeZone());
								if(dtCurrDate.after(dtDeadLine)) {
									innerList.add("1");//2
								} else {
									innerList.add("0");//2
								}
							}
							innerList.add("0");//3
							innerList.add(rs.getString("added_by"));//4
							outerList.add(innerList);
							hmKRATasks.put(bscperspectiveIds[i]+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id"), outerList);
						}
						rs.close();
						pst.close();
						request.setAttribute("hmKRATasks", hmKRATasks);
				}
			  }
			}
			
			request.setAttribute("bscperspectiveDetails", hmperspectiveDetails);
			request.setAttribute("hmGoalKraSuperIdwise", hmGoalKraSuperIdwise);
			request.setAttribute("hmGoalDetails", hmGoalDetails);
			request.setAttribute("hmEmpName", hmEmpName);
		}catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);			
			}
	}
	
	
private void getPerspectiveDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmGoalName = CF.getGoalNameMap(con, uF);
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			Map<String, String> hmEmpName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpUserId = CF.getEmpUserIdMap(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			
			String perspectiveIds = null;
			pst = con.prepareStatement("select * from bsc_details where bsc_id = ? ");
			pst.setInt(1,uF.parseToInt(getStrBscId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				perspectiveIds = rs.getString("bsc_perspective_ids");
			}
		
			rs.close();
			pst.close();
			
			Map<String, List<String>> hmProspectiveData = new LinkedHashMap<String, List<String>>();
			String[] perspecives = perspectiveIds.split(",");
			for(int i = 0 ;i <perspecives.length;i++) {
				if(perspecives[i] != null && perspecives[i].length() > 0 ) {
					pst = con.prepareStatement("select bpd.*,gd.goal_id from goal_details gd, bsc_perspective_details bpd where gd.perspective_id = bpd.bsc_perspective_id and gd.perspective_id = ? ");
					pst.setInt(1,uF.parseToInt(perspecives[i]));
					System.out.println("pst ===> " + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
//						List<List<String>> alProspectiveData = hmProspectiveData.get(rs.getString("bsc_perspective_id"));
//						if(alProspectiveData==null) alProspectiveData = new ArrayList<List<String>>();
						List<String> innerList = new ArrayList<String>();
//						innerList.add(rs.getString("goal_id"));
						innerList.add(rs.getString("bsc_perspective_name"));
						innerList.add(rs.getString("perspective_color"));
						
//						alProspectiveData.add(innerList);
						hmProspectiveData.put(rs.getString("bsc_perspective_id"), innerList);
					}
					rs.close();
					pst.close();
				}
			}
		
			request.setAttribute("hmProspectiveData", hmProspectiveData);

			
			Map<String, Map<String, Map<String, List<String>>>> hmPerspectivewiseGoalDetails = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
//			Map<String, Map<String, List<String>>> hmSuperIdwiseGoalDetails = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<List<String>>> hmGoalKRADetails = new LinkedHashMap<String, List<List<String>>>();
			StringBuilder sbKraIds = null;
			for(int i = 0; i<perspecives.length;i++) {
				if(perspecives[i].length() > 0 && perspecives[i] != null) {
					pst = con.prepareStatement("select freq_end_date,freq_start_date,gd.*, gk.kra_description,gk.goal_kra_id,gk.is_assign,gk.kra_weightage,gdf.goal_freq_id ,gdf.goal_freq_name " +
						" from goal_details gd, goal_kras gk,goal_details_frequency gdf where gd.goal_id=gk.goal_id and gd.goal_id=gdf.goal_id and gd.perspective_Id = ? and gd.is_close = false order by freq_start_date");
					pst.setInt(1, uF.parseToInt(perspecives[i]));
					rs = pst.executeQuery();
					while(rs.next()) {
						if(rs.getInt("goal_type") == EMPLOYEE_KRA && rs.getString("is_assign") != null && rs.getString("is_assign").equals("f")) {
							continue;
						}
						
						if(sbKraIds==null) {
							sbKraIds = new StringBuilder();
							sbKraIds.append(rs.getString("goal_kra_id"));
						} else {
							sbKraIds.append(","+rs.getString("goal_kra_id"));
						}
						Map<String, Map<String, List<String>>> hmSuperIdwiseGoalDetails = hmPerspectivewiseGoalDetails.get(perspecives[i]);
						if(hmSuperIdwiseGoalDetails == null) hmSuperIdwiseGoalDetails = new LinkedHashMap<String, Map<String, List<String>>>();
						
						String superId = rs.getString("super_id");
						if(uF.parseToInt(superId) == 0) {
							//superId = rs.getString("goal_freq_id");
						}
						Map<String, List<String>> hmGoalDetails = hmSuperIdwiseGoalDetails.get(superId);
						if(hmGoalDetails == null) hmGoalDetails = new LinkedHashMap<String, List<String>>();
						
						List<List<String>> outerList = hmGoalKRADetails.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"));
						if(outerList==null) outerList = new ArrayList<List<String>>();
						
						List<String> innerList = new ArrayList<String>();
						innerList.add(perspecives[i]);
						innerList.add(rs.getString("goal_id"));
						innerList.add(rs.getString("kra_description"));
						innerList.add(rs.getString("goal_title"));
						if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))) {
							innerList.add("Self");
						} else {
							innerList.add(uF.showData(hmEmpName.get(rs.getString("user_id")), "-"));
						}
						
						innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()),"-")); //5
						
						String tGoalId = getPerentGoalId(con, uF, rs.getString("goal_id"));
						innerList.add(tGoalId); // team goal id 6
						String mGoalId = getPerentGoalId(con, uF, tGoalId);
						innerList.add(mGoalId); // manager goal id 7
						String cGoalId = getPerentGoalId(con, uF, mGoalId);
						innerList.add(cGoalId); // Corporate goal id 8
						String priority="";
						String pClass="";
						if(rs.getString("priority")!=null && !rs.getString("priority").equals("")) {
							if(rs.getString("priority").equals("1")) {
								pClass="high";
								priority="High";
							} else if(rs.getString("priority").equals("2")) {
								pClass="medium";
								priority="Medium";
							} else if(rs.getString("priority").equals("3")) {
								pClass="low";
								priority="Low";
							}
						}
						innerList.add(priority); // Priority 9
						innerList.add(pClass); // Priority class 10
						innerList.add(rs.getString("goal_kra_id")); //11
						innerList.add(rs.getString("is_close")); //12
						innerList.add(rs.getString("goal_objective")); //13
						innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),"")); //14
						innerList.add(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()) ); //15
						
						innerList.add(rs.getString("weightage")); //16

						innerList.add(rs.getString("goal_parent_id")); //17
						innerList.add(rs.getString("goal_type")); //18
						innerList.add(rs.getString("measure_type")); //19
						innerList.add(rs.getString("measure_kra")); //20
						String val="",daysHRVal="";
						if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")) {
							val=" "+rs.getString("measure_effort_days")+" Days and "+rs.getString("measure_effort_hrs")+" Hrs.";
							daysHRVal = (rs.getString("measure_effort_days") != null && !rs.getString("measure_effort_days").equals("") ? rs.getString("measure_effort_days") : "0")+"."
							+(rs.getString("measure_effort_hrs") != null && !rs.getString("measure_effort_hrs").equals("") ? rs.getString("measure_effort_hrs") : "0");
						} else {
							val= uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("measure_currency_value")));
						}
						innerList.add(val); //21
						innerList.add(daysHRVal); //22

						if(rs.getString("freq_end_date") != null) {
							Date dtDeadLine = uF.getDateFormat(rs.getString("freq_end_date"), DBDATE);
							Date dtCurrDate = uF.getCurrentDate(CF.getStrTimeZone());
							if(dtCurrDate.after(dtDeadLine)) {
								innerList.add("1"); //23
							} else {
								innerList.add("0"); //23
							}
						} else {
							innerList.add("0"); //23
						}
						innerList.add(rs.getString("goalalign_with_teamgoal")); //24
						innerList.add(uF.showData(hmGoalName.get(rs.getString("goal_parent_id")), "")); //25
						innerList.add(rs.getString("close_reason")); //26
						innerList.add(rs.getString("kra_weightage")); //27
						innerList.add(rs.getString("peer_ids")); //28
						innerList.add(rs.getString("manager_ids")); //29
						innerList.add(rs.getString("hr_ids")); //30
						innerList.add(rs.getString("anyone_ids")); //31
						innerList.add(rs.getString("goal_freq_id")); //32
						innerList.add(rs.getString("goal_freq_name")); //33
						innerList.add(rs.getString("orientation_id")); //34
						innerList.add(rs.getString("emp_ids")); //35
						innerList.add(rs.getString("user_id"));//36
						innerList.add(" ");//37
						innerList.add(" ");//38
						innerList.add(rs.getString("perspective_id"));//39
						innerList.add(rs.getString("align_with_perspective"));//40
						innerList.add(rs.getString("org_id"));//41
						
						outerList.add(innerList);
						hmGoalKRADetails.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), outerList);
						
						hmGoalDetails.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), innerList);
						hmSuperIdwiseGoalDetails.put(superId, hmGoalDetails);
						hmPerspectivewiseGoalDetails.put(perspecives[i], hmSuperIdwiseGoalDetails);
					}
					rs.close();
					pst.close();
				}
			}
			request.setAttribute("hmGoalKRADetails", hmGoalKRADetails);
			request.setAttribute("hmPerspectivewiseGoalDetails", hmPerspectivewiseGoalDetails);
					
			Map<String, List<List<String>>> hmKRATasks = new LinkedHashMap<String, List<List<String>>>();
			if(sbKraIds != null) {
				pst = con.prepareStatement("select gkt.goal_kra_task_id, gkt.task_name,gkt.added_by,gkt.kra_id,gdf.goal_id,gdf.goal_freq_id,gdf.freq_end_date" +
					" from goal_kra_tasks gkt, goal_details_frequency gdf where gkt.goal_id=gdf.goal_id and (gdf.is_delete is null or gdf.is_delete = false) " +
					" and gkt.kra_id in ("+sbKraIds.toString()+") order by goal_kra_task_id");
				System.out.println("hmkratask pst==> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					//List<List<String>> outerList = hmKRATasks.get(empList.get(i)+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id"));
					List<List<String>> outerList = hmKRATasks.get(rs.getString("kra_id"));
					if (outerList == null) outerList = new ArrayList<List<String>>();
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("goal_kra_task_id"));//0
					innerList.add(rs.getString("task_name"));//1
	//							System.out.println("task_id==>"+rs.getString("goal_kra_task_id")+"==>task_name==>"+rs.getString("task_name"));
					if(rs.getString("freq_end_date") != null) {
						Date dtDeadLine = uF.getDateFormat(rs.getString("freq_end_date"), DBDATE);
						Date dtCurrDate = uF.getCurrentDate(CF.getStrTimeZone());
						if(dtCurrDate.after(dtDeadLine)) {
							innerList.add("1");//2
						} else {
							innerList.add("0");//2
						}
					}
					innerList.add("0");//3
					innerList.add(rs.getString("added_by"));//4
					outerList.add(innerList);
					hmKRATasks.put(rs.getString("kra_id"), outerList);
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("hmKRATasks", hmKRATasks);
			request.setAttribute("hmEmpName", hmEmpName);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}


	private String getPerentGoalId(Connection con, UtilityFunctions uF, String goalID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String goalId = null;
		try {
				String query1 = "select goal_parent_id from goal_details where goal_id = ?";
				pst = con.prepareStatement(query1);
				pst.setInt(1, uF.parseToInt(goalID));
				rs = pst.executeQuery();
				while (rs.next()) {
					goalId = rs.getString("goal_parent_id");
				}
				rs.close();
				pst.close();
			} catch (Exception e) {
			e.printStackTrace();
			}
		return goalId;
	}
	
	public void getKRARatingAndCompletionStatus(UtilityFunctions uF) {
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		Map<String, String> hmKraAverage = new HashMap<String, String>();
		Map<String, String> hmTargetRatingAndComment = new HashMap<String, String>();
		Map<String, String> hmEmpwiseGoalAndTargetRating = new HashMap<String, String>();
		pst=con.prepareStatement("select gksrd.*, gd.goal_id from goal_kra_status_rating_details gksrd, goal_details gd where gksrd.goal_id = gd.goal_id");
		rs=pst.executeQuery();
			while (rs.next()) {
				if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
					double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if(rs.getString("manager_rating") == null) {
						strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if(rs.getString("hr_rating") == null) {
						strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					hmEmpwiseGoalAndTargetRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING", strCurrGoalORTargetRating+"");
				}
				
				hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_STATUS", rs.getString("complete_percent"));
				hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_MGR_RATING", rs.getString("manager_rating"));
				hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_MGR_COMMENT", rs.getString("manager_comment"));
				hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_HR_RATING", rs.getString("hr_rating"));
				hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_HR_COMMENT", rs.getString("hr_comment"));
			}
		rs.close();
		pst.close();
		request.setAttribute("hmTargetRatingAndComment", hmTargetRatingAndComment);
		request.setAttribute("hmEmpwiseGoalAndTargetRating", hmEmpwiseGoalAndTargetRating);
		
		
		Map<String, String> hmEmpwiseGoalAndTargetEmpRating = new HashMap<String, String>();
		
			pst=con.prepareStatement("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.goal_id, gksrd.emp_id," +
				" gksrd.goal_freq_id from goal_kra_emp_status_rating_details gksrd, goal_details gd where gksrd.goal_id = gd.goal_id and user_type != '-' " +
				"group by gksrd.goal_freq_id,gksrd.goal_id,gksrd.emp_id");
			rs=pst.executeQuery();
			while (rs.next()) {
				if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
					double strCurrGoalORTargetRating = rs.getDouble("user_rating");
					hmEmpwiseGoalAndTargetEmpRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING", strCurrGoalORTargetRating+"");
					hmEmpwiseGoalAndTargetEmpRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT", rs.getInt("cnt")+"");
				}
			}
		rs.close();
		pst.close();
		request.setAttribute("hmEmpwiseGoalAndTargetEmpRating", hmEmpwiseGoalAndTargetEmpRating);
			
		Map<String,String> hmKraCompletedPercentage = new HashMap<String,String>();
		Map<String, String> hmKRATaskStatusAndRating = new HashMap<String, String>();
		Map<String, String> hmEmpwiseKRARating = new HashMap<String, String>();
		Map<String, String> hmEmpwiseGoalRating = new HashMap<String, String>();
		
		pst=con.prepareStatement("select gksrd.*, gk.goal_id from goal_kra_status_rating_details gksrd, goal_kras gk where gksrd.kra_id = gk.goal_kra_id");
		rs=pst.executeQuery();
//		System.out.println("490 pstt=>"+pst);
		while (rs.next()) {
				
				if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
					double strTaskRating = uF.parseToDouble(hmEmpwiseKRARating.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_RATING"));
					int strTaskCount = uF.parseToInt(hmEmpwiseKRARating.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_COUNT"));
					strTaskCount++;
					double strCurrTaskRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if(rs.getString("manager_rating") == null) {
						strCurrTaskRating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if(rs.getString("hr_rating") == null) {
						strCurrTaskRating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					strTaskRating += strCurrTaskRating;
					hmEmpwiseKRARating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_RATING", strTaskRating+"");
					hmEmpwiseKRARating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_COUNT", strTaskCount+"");
					
					double strGoalwiseTaskRating = uF.parseToDouble(hmEmpwiseGoalRating.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING"));
					int strGoalwiseTaskCount = uF.parseToInt(hmEmpwiseGoalRating.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT"));
					strGoalwiseTaskCount++;
					double strGoalwiseCurrTaskRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if(rs.getString("manager_rating") == null) {
						strGoalwiseCurrTaskRating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if(rs.getString("hr_rating") == null) {
						strGoalwiseCurrTaskRating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					strGoalwiseTaskRating += strGoalwiseCurrTaskRating;
					hmEmpwiseGoalRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING", strGoalwiseTaskRating+"");
					hmEmpwiseGoalRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT", strGoalwiseTaskCount+"");
				}
				
				int totalTaskCount = uF.parseToInt(hmKraCompletedPercentage.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_TASKCOUNT"));
				totalTaskCount++;
				
				double kraPercentage = uF.parseToDouble(hmKraCompletedPercentage.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_PERCENTAGE"));
				kraPercentage = kraPercentage + uF.parseToDouble(rs.getString("complete_percent"));
				
				hmKraCompletedPercentage.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_TASKCOUNT",""+totalTaskCount);
				hmKraCompletedPercentage.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_PERCENTAGE",""+kraPercentage );
				
				hmKRATaskStatusAndRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_STATUS", rs.getString("complete_percent"));
				hmKRATaskStatusAndRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_MGR_RATING", rs.getString("manager_rating"));
				hmKRATaskStatusAndRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_MGR_COMMENT", rs.getString("manager_comment"));
				hmKRATaskStatusAndRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_HR_RATING", rs.getString("hr_rating"));
				hmKRATaskStatusAndRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_HR_COMMENT", rs.getString("hr_comment"));
			}
		rs.close();
		pst.close();
		

		Map<String, String> hmKRATaskEmpRating = new HashMap<String, String>();
		Map<String, String> hmEmpwiseKRAEmpRating = new HashMap<String, String>();
		Map<String, String> hmEmpwiseGoalEmpRating = new HashMap<String, String>();
		pst = con.prepareStatement("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gk.goal_id, gksrd.emp_id, " +
				" gksrd.kra_id, gksrd.kra_task_id,gksrd.goal_freq_id from goal_kra_emp_status_rating_details gksrd, goal_kras gk where " +
				" gksrd.kra_id = gk.goal_kra_id and user_type != '-' " +
				" group by gksrd.goal_freq_id,gk.goal_id,gksrd.emp_id,gksrd.kra_id,gksrd.kra_task_id");
		rs = pst.executeQuery();
//		System.out.println("549 pst==>"+pst);
		while (rs.next()) {
				
				if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
					double strTaskRating = uF.parseToDouble(hmEmpwiseKRAEmpRating.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_RATING"));
					int strTaskCount = uF.parseToInt(hmEmpwiseKRAEmpRating.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_COUNT"));
					strTaskCount++;
					double strCurrTaskRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
					
					hmKRATaskEmpRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_RATING", strCurrTaskRating+"");
					hmKRATaskEmpRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_COUNT", rs.getInt("cnt")+"");
					
					strTaskRating += strCurrTaskRating;
					hmEmpwiseKRAEmpRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_RATING", strTaskRating+"");
					hmEmpwiseKRAEmpRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_COUNT", strTaskCount+"");
					
					
					double strGoalwiseTaskRating = uF.parseToDouble(hmEmpwiseGoalEmpRating.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING"));
					int strGoalwiseTaskCount = uF.parseToInt(hmEmpwiseGoalEmpRating.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT"));
					strGoalwiseTaskCount++;
					double strGoalwiseCurrTaskRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
					strGoalwiseTaskRating += strGoalwiseCurrTaskRating;
					hmEmpwiseGoalEmpRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING", strGoalwiseTaskRating+"");
					hmEmpwiseGoalEmpRating.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT", strGoalwiseTaskCount+"");
				}
				
			}
		rs.close();
		pst.close();
		request.setAttribute("hmKraCompletedPercentage", hmKraCompletedPercentage);
		
		request.setAttribute("hmEmpwiseGoalRating", hmEmpwiseGoalRating);
		request.setAttribute("hmEmpwiseKRARating", hmEmpwiseKRARating);
		request.setAttribute("hmEmpwiseGoalEmpRating", hmEmpwiseGoalEmpRating);
		request.setAttribute("hmEmpwiseKRAEmpRating", hmEmpwiseKRAEmpRating);
		request.setAttribute("hmKRATaskStatusAndRating", hmKRATaskStatusAndRating);
		request.setAttribute("hmKRATaskEmpRating", hmKRATaskEmpRating);
		request.setAttribute("hmKraAverage", hmKraAverage);
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
	private void getEmpImage(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()) {
				empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			request.setAttribute("empImageMap", empImageMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getStrBscId() {
		return strBscId;
	}
	public void setStrBscId(String strBscId) {
		this.strBscId = strBscId;
	}
	public String getCurrUserType() {
		return currUserType;
	}
	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}
	

}
