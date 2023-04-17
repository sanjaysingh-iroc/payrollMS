package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPerspective;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditMyPersonalGoalPopUp_1 extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;

	private String goalid;
	private String goaltype;

	private String goalattributevalue;
	private String measurewithvalue;
	private String measureKravalue;
	private String addMKravalue;
	private String mkwithvalue;
	private String goalFeedbackvalue;
	
	private String[] gradevalue;
	private String orientation_id;
	private String goalalignStatus;
	private String goal_parent_id;
	private String goalElement;
	
	private List<FillOrientation> orientationList;
	private List<FillLevel> levelList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList;
	private List<FillAttribute> attributeList;
	private List<FillPerspective> perspectiveList;
	private List<FillDesig> desigList;
	
	
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillOrganisation> organisationList;
	

	private List<String> empvalue = new ArrayList<String>();
	private List<String> desigvalue = new ArrayList<String>();
	private List<String> levelvalue = new ArrayList<String>();

	private List<FillFrequency> frequencyList;

	private String fequencyValue;
	private String weekdayValue;
	private String annualDayValue;
	private String annualMonthValue;
	private String dayValue;
	private String monthdayValue;;
	List<String> monthValue = new ArrayList<String>();
	
	
	private String userlocation;
	
	private String priority;
	private String operation;
	private String type;
	private String typeas;
	private String isMeasureKra;
	private String isMeasureKraVal;
	
	private String superId;
	
	private String proPage;
	private String minLimit;
	private  String empId;
	private String goalTitle;
	private String strID;
	
	private String fromPage;
	private String currUserType;
	private String from;
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionOrgId = (String) session.getAttribute(ORGID);

		UtilityFunctions uF = new UtilityFunctions();
		
		if(getStrID() == null || getStrID().equals("") || getStrID().equalsIgnoreCase("null")) {
			setStrID(strSessionOrgId);
		}
		orientationList = new FillOrientation(request).fillOrientation();
		getOrientationDetailsList();
//		System.out.println("typeas in java ===> "+typeas);
//		System.out.println("in EditMyPersonalGoalPopUp==>proPage==>"+proPage+"\tminLimit==>"+minLimit);
		levelList = new FillLevel(request).fillLevel();
		getLevelDetailsList();

		gradeList = new FillGrade(request).fillGrade();
		getgradeDetailsList();
		
		desigList = new FillDesig(request).fillDesig();
		
		frequencyList=new FillFrequency(request).fillFrequency();
		
		getFrequenyDetailsList();
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrID()));
		workList = new FillWLocation(request).fillWLocation(getStrID());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrID()));
		organisationList = new FillOrganisation(request).fillOrganisation();
		
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getStrID()));
		gradeList = new FillGrade(request).fillGradeByOrg(uF.parseToInt(getStrID()));
		userlocation = getLocation();
		
		if(userlocation == " " || userlocation.length() <= 0)
			userlocation = "0";
		
		if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
//			empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
			empList = new FillEmployee(request).fillEmployeeNameByOrg(uF.parseToInt(getStrID()));
		} else {
			empList = new FillEmployee(request).fillEmployeeNameBySupervisor(strSessionEmpId);
		}
		
		getGoalTypeDetails();
		getGoalData();
		if(getIsMeasureKra() != null && getIsMeasureKra().equals("t")){
			setIsMeasureKraVal("Yes");
		} else {
			setIsMeasureKraVal("No");
		}
		getSelectEmployeeList();
		getTeamGoals();
		
		return LOAD;
	}
	
	
	private void getTeamGoals() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sb = new StringBuilder("");
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from goal_details where emp_ids like '%," + strSessionEmpId + ",%' and goal_type = 3 order by goal_id ");
			pst = con.prepareStatement("select * from goal_details where goal_type=3 and goal_id in(select goal_parent_id from goal_details " +
					" where goal_type=4 and emp_ids like '%,"+strSessionEmpId+",%') and (is_close is null or is_close = false) order by goal_id desc");
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			while (rs.next()) {
				if(getGoal_parent_id().equals(rs.getString("goal_id"))){
					sb.append("<option value='" + rs.getString("goal_id") + "' selected=\"selected\">" + rs.getString("goal_title") + "</option>");
				} else {
					sb.append("<option value='" + rs.getString("goal_id") + "'>" + rs.getString("goal_title") + "</option>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("optionTeamGoals", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			pst = con
			.prepareStatement("select * from goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			while(rst.next()){				
				selectEmpIDs=rst.getString("emp_ids");
			}
			rst.close();
			pst.close();
			
			List<List<String>> selectEmpList = new ArrayList<List<String>>(); 
			Map<String,String> hmCheckEmpList=new HashMap<String, String>();
			String empids="";
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
				List<String> tmpselectEmpList=Arrays.asList(selectEmpIDs.split(","));
				Set<String> trainerSet = new HashSet<String>(tmpselectEmpList);
				Iterator<String> itr = trainerSet.iterator();
				int i=0;
				while (itr.hasNext()) {
					String strEmpId = (String) itr.next();
					if(strEmpId!=null && !strEmpId.equals("")){
//						selectEmpList.add(hmEmpName.get(strEmpId.trim()));
						List<String> innerList = new ArrayList<String>();
						innerList.add(strEmpId.trim());
						innerList.add(hmEmpName.get(strEmpId.trim()));
						selectEmpList.add(innerList);
						
						hmCheckEmpList.put(strEmpId.trim(), strEmpId.trim());
						if(i==0){
							empids=strEmpId.trim();
							i++;
						} else {
							empids+=","+strEmpId.trim();
							i++;
						}
					}
				}
			} else {
				selectEmpList=null;
				
			}
			request.setAttribute("selectEmpList", selectEmpList);
			request.setAttribute("hmCheckEmpList", hmCheckEmpList);
			request.setAttribute("empids", empids);
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private String getLocation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";

		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while (rst.next()) {
				location = rst.getString(1);
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return location;
	}

	
	
	private void getGoalTypeDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmGoalType = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
		    request.setAttribute("hmFeatureStatus", hmFeatureStatus);
		    
			pst = con.prepareStatement("select * from goal_type_details order by goal_type_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmGoalType.put(rs.getString("goal_type_id"), rs.getString("goal_type_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmGoalType", hmGoalType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	private String getSupervisorIDIndividual() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uf = new UtilityFunctions();
//		String supervisor_id = "";
//		try {
//
//			con = db.makeConnection(con);
//
//			String individual_parentId = "";
//			pst = con
//					.prepareStatement("select goal_parent_id from goal_details where goal_id=?");
//			pst.setInt(1, uf.parseToInt(goalid));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				individual_parentId = rs.getString("goal_parent_id");
//			}
//
//			String team_parentId = "";
//			pst = con
//					.prepareStatement("select goal_parent_id from goal_details where goal_id=?");
//			pst.setInt(1, uf.parseToInt(individual_parentId));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				team_parentId = rs.getString("goal_parent_id");
//			}
//
//			pst = con
//					.prepareStatement("select emp_ids from goal_details where goal_id=?");
//			pst.setInt(1, uf.parseToInt(team_parentId));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				supervisor_id = rs.getString("emp_ids");
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//		return supervisor_id;
//	}

//	private String getSupervisorID() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//
//		db.setRequest(request);
//		UtilityFunctions uf = new UtilityFunctions();
//		String supervisor_id = "";
//		try {
//
//			con = db.makeConnection(con);
//
//			String goal_parent_id = "";
//			pst = con
//					.prepareStatement("select goal_parent_id from goal_details where goal_id=?");
//			pst.setInt(1, uf.parseToInt(goalid));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				goal_parent_id = rs.getString("goal_parent_id");
//			}
//
//			pst = con
//					.prepareStatement("select emp_ids from goal_details where goal_id=?");
//			pst.setInt(1, uf.parseToInt(goal_parent_id));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				supervisor_id = rs.getString("emp_ids");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//		return supervisor_id;
//	}

	private void getGoalData() {
		getCorporateData();
		getKraDetails();
	
	}

	private void getKraDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();
		try {
			con = db.makeConnection(con);

			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
			pst = con.prepareStatement("select * from goal_kras where goal_id in (select goal_id from goal_details where (super_id=(select super_id from goal_details where goal_id=?) and super_id>0) or goal_id=? order by goal_id) order by goal_kra_id");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			pst.setInt(2, uF.parseToInt(getGoalid()));
			rs = pst.executeQuery();
			StringBuilder sbKraIds = null;
			while (rs.next()) {
				List<List<String>> outerList = hmKRA.get(rs.getString("goal_id"));
				if (outerList == null) outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				// goal_kra_id,goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type,
				innerList.add(rs.getString("goal_kra_id")); //0
				innerList.add(rs.getString("goal_id")); //1
				innerList.add(uF.getDateFormat(rs.getString("entry_date"),DBDATE, DATE_FORMAT)); //2
				innerList.add(uF.getDateFormat(rs.getString("effective_date"),DBDATE, DATE_FORMAT)); //3
				innerList.add(rs.getString("is_approved")); //4
				innerList.add(rs.getString("approved_by")); //5
				innerList.add(rs.getString("kra_order")); //6
				innerList.add(rs.getString("kra_description")); //7
				innerList.add(rs.getString("goal_type")); //8
				innerList.add(uF.showData(rs.getString("kra_weightage"), "0")); //9

				if(sbKraIds == null) {
					sbKraIds = new StringBuilder();
					sbKraIds.append(rs.getString("goal_kra_id"));
				} else {
					sbKraIds.append(","+rs.getString("goal_kra_id"));
				}
				outerList.add(innerList);
				hmKRA.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			
			Map<String, List<List<String>>> hmKRATasks = new LinkedHashMap<String, List<List<String>>>();
//			Map<String, List<List<String>>> hmKRAEmpTaskIds = new LinkedHashMap<String, List<List<String>>>();
			if(sbKraIds != null) {
				pst = con.prepareStatement("select * from activity_info where kra_id in ("+sbKraIds.toString()+") order by task_id");
				rs = pst.executeQuery();
				List<String> taskNameList = new ArrayList<String>();
	//			StringBuilder sbKRATasids = new StringBuilder();
				while (rs.next()) {
	//				String kraTaskId = 
					
					if(!taskNameList.contains(rs.getString("activity_name"))) {
						taskNameList.add(rs.getString("activity_name"));
						
						List<List<String>> outerList = hmKRATasks.get(rs.getString("kra_id"));
						if (outerList == null) outerList = new ArrayList<List<String>>();
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("task_id"));//0
						innerList.add(rs.getString("activity_name"));//1
						innerList.add(rs.getString("resource_ids"));//2
						innerList.add(rs.getString("goal_kra_task_id"));//3
						outerList.add(innerList);
						hmKRATasks.put(rs.getString("kra_id"), outerList);
					}
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("hmKRATasks", hmKRATasks);
			
			request.setAttribute("hmKRA", hmKRA);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private void getCorporateData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, List<String>> hmOtherGoals = new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> hmFirstGoal = new HashMap<String, List<String>>();
			Map<String, String> hmAttribute = getAttributeMap(con);
			
			int glCnt=0;
			pst = con.prepareStatement("select * from goal_details where ((super_id=(select super_id from goal_details where goal_id=?) and super_id>0) or goal_id=?) and is_close= ? order by goal_id");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			pst.setInt(2, uF.parseToInt(getGoalid()));
			pst.setBoolean(3, false);
			System.out.println("cinnerList pst==>"+pst);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				List<String> cinnerList = new ArrayList<String>();
				
				cinnerList.add(rs.getString("goal_id"));  //0
				cinnerList.add(rs.getString("goal_type"));
				goaltype = rs.getString("goal_type");
				cinnerList.add(rs.getString("goal_parent_id")); //2
				goal_parent_id = rs.getString("goal_parent_id");
				cinnerList.add(rs.getString("goal_title"));
				cinnerList.add(rs.getString("goal_objective")); //4
				cinnerList.add(rs.getString("goal_description"));
				goalattributevalue = rs.getString("goal_attribute");
				cinnerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")), "")); //6
				measurewithvalue = rs.getString("measure_type");
				
				cinnerList.add(rs.getString("measure_type"));
				cinnerList.add(rs.getString("measure_currency_value")); //8
				cinnerList.add(rs.getString("measure_currency_id"));
				cinnerList.add(rs.getString("measure_effort_days")); //10
				cinnerList.add(rs.getString("measure_effort_hrs"));
				cinnerList.add(rs.getString("measure_type1")); //12
				cinnerList.add(rs.getString("measure_kra"));
				cinnerList.add(rs.getString("measure_currency_value1")); //14
				cinnerList.add(rs.getString("measure_currency1_id"));
				cinnerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, DATE_FORMAT)); //16
				cinnerList.add(rs.getString("is_feedback")); //17
				cinnerList.add(rs.getString("orientation_id")); //18
				cinnerList.add(rs.getString("weightage")); //19
				cinnerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), "")); //20
				cinnerList.add(rs.getString("entry_date")); //21
				cinnerList.add(rs.getString("user_id")); //22
				isMeasureKra = rs.getString("is_measure_kra"); 
				cinnerList.add(rs.getString("is_measure_kra")); //23
				cinnerList.add(rs.getString("measure_kra_days")); //24
				cinnerList.add(rs.getString("measure_kra_hrs")); //25
				cinnerList.add(rs.getString("grade_id")); //26
				cinnerList.add(rs.getString("level_id")); //27
				cinnerList.add(rs.getString("frequency")); //28
				cinnerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT)); //29
				cinnerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT)); //30 priority
				goalElement = rs.getString("goal_element");
				String elementList = getElementList(goalElement);
				cinnerList.add(elementList); //31
				attributeList = getAttributeListElementwise(goalElement);
				StringBuilder sbAttribList = new StringBuilder();
				for(int i=0; attributeList != null && !attributeList.isEmpty() && i<attributeList.size(); i++) {
					if(rs.getString("goal_attribute") != null && attributeList.get(i).getId().equals(rs.getString("goal_attribute"))) {
						sbAttribList.append("<option value=\"" + attributeList.get(i).getId() + "\" selected=\"selected\">"+ attributeList.get(i).getName() + "</option>");
					} else {
						sbAttribList.append("<option value=\"" + attributeList.get(i).getId() + "\">"+ attributeList.get(i).getName() + "</option>");
					}
				}
				cinnerList.add(sbAttribList.toString()); //32
				StringBuilder sbPriority = new StringBuilder();
				sbPriority.append("<option value=\"1\"");
				if(rs.getInt("priority") == 1) {
					sbPriority.append("selected=\"selected\"");
				}
				sbPriority.append(">High</option>");
				sbPriority.append("<option value=\"2\"");
				if(rs.getInt("priority") == 2) {
					sbPriority.append("selected=\"selected\"");
				}
				sbPriority.append(">Medium</option>");
				sbPriority.append("<option value=\"3\"");
				if(rs.getInt("priority") == 3) {
					sbPriority.append("selected=\"selected\"");
				}
				sbPriority.append(">Low</option>");
				cinnerList.add(sbPriority.toString()); //33
				
				cinnerList.add(rs.getString("org_id")); //34
				cinnerList.add(rs.getString("is_close")); //35

				StringBuilder sbOrientList = new StringBuilder();
				for(int i=0; orientationList != null && !orientationList.isEmpty() && i<orientationList.size(); i++) {
					if(rs.getString("orientation_id") != null && orientationList.get(i).getId().equals(rs.getString("orientation_id"))) {
						sbOrientList.append("<option value=\"" + orientationList.get(i).getId() + "\" selected=\"selected\">"+ orientationList.get(i).getName() + "</option>");
					} else {
						sbOrientList.append("<option value=\"" + orientationList.get(i).getId() + "\">"+ orientationList.get(i).getName() + "</option>");
					}
				}
				cinnerList.add(sbOrientList.toString()); //36
				
				StringBuilder sbFrequencyList = new StringBuilder();
				for(int i=0; frequencyList != null && !frequencyList.isEmpty() && i<frequencyList.size(); i++) {
					if(rs.getString("frequency") != null && frequencyList.get(i).getId().equals(rs.getString("frequency"))) {
						sbFrequencyList.append("<option value=\"" + frequencyList.get(i).getId() + "\" selected=\"selected\">"+ frequencyList.get(i).getName() + "</option>");
					} else {
						sbFrequencyList.append("<option value=\"" + frequencyList.get(i).getId() + "\">"+ frequencyList.get(i).getName() + "</option>");
					}
				}
				cinnerList.add(sbFrequencyList.toString()); //37
				
				String[] tempWeekdays = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
				List<String> alWeekdays = Arrays.asList(tempWeekdays);
				StringBuilder sbWeekdayList = new StringBuilder();
				for(int i=0; alWeekdays != null && !alWeekdays.isEmpty() && i<alWeekdays.size(); i++) {
					if(rs.getString("weekday") != null && alWeekdays.get(i).equals(rs.getString("weekday").trim())) {
						sbWeekdayList.append("<option value=\"" + alWeekdays.get(i)+ "\" selected=\"selected\">"+ alWeekdays.get(i) + "</option>");
					} else {
						sbWeekdayList.append("<option value=\"" + alWeekdays.get(i) + "\">"+ alWeekdays.get(i) + "</option>");
					}
				}
				cinnerList.add(sbWeekdayList.toString()); //38
				
				StringBuilder sbDatesList = new StringBuilder();
				for(int i=1; i<=31; i++) {
					if(rs.getString("frequency_day") != null && (i+"").equals(rs.getString("frequency_day").trim())) {
						sbDatesList.append("<option value=\"" + i + "\" selected=\"selected\">"+ i + "</option>");
					} else {
						sbDatesList.append("<option value=\"" + i + "\">"+ i + "</option>");
					}
				}
				cinnerList.add(sbDatesList.toString()); //39
				
				cinnerList.add(rs.getString("frequency")); //40
				cinnerList.add(rs.getString("weekday")); //41
				cinnerList.add(rs.getString("frequency_day")); //42
				
				
				StringBuilder sbAlignPerspectiveList = new StringBuilder();
				perspectiveList = fillPerspective();
				for(int i=0; perspectiveList != null && !perspectiveList.isEmpty() && i<perspectiveList.size(); i++) {
					if(rs.getString("perspective_id") != null && perspectiveList.get(i).getPerspectiveId().equals(rs.getString("perspective_id"))) {
						sbAlignPerspectiveList.append("<option value=\"" + perspectiveList.get(i).getPerspectiveId() + "\" selected=\"selected\">"+ perspectiveList.get(i).getPerspectiveName() + "</option>");
					} else {
						sbAlignPerspectiveList.append("<option value=\"" + perspectiveList.get(i).getPerspectiveId() + "\">"+ perspectiveList.get(i).getPerspectiveName() + "</option>");
					}
				}
				StringBuilder sbIsAlignPerspective = new StringBuilder();
				if(rs.getString("align_with_perspective") != null && rs.getString("align_with_perspective").equals("t")) {
					sbIsAlignPerspective.append("<option value=\"" +"YES"+ "\" selected=\"selected\">" +"YES"+"</option>");

					sbIsAlignPerspective.append("<option value=\"" + "NO" + "\">" +"NO" + "</option>");

				} else {
					sbIsAlignPerspective.append("<option value=\"" +"NO"+ "\" selected=\"selected\">" +"NO"+"</option>");

					sbIsAlignPerspective.append("<option value=\"" + "YES" + "\">" +"YES" + "</option>");
					
				}
				System.out.println("sbIsAlignPerspective::"+sbIsAlignPerspective.toString());
				cinnerList.add(" "); //43
				cinnerList.add(sbAlignPerspectiveList.toString()); //44
				cinnerList.add(sbIsAlignPerspective.toString()); //45
				
				
				setPriority(rs.getString("priority"));
				setSuperId(rs.getString("super_id"));
				
				if(rs.getString("goalalign_with_teamgoal") != null && rs.getString("goalalign_with_teamgoal").equals("t")) {
					goalalignStatus = "Yes";
				} else {
					goalalignStatus = "No";
				}
				
				fequencyValue = rs.getString("frequency");
				if(fequencyValue == null || fequencyValue.equals("")) {
					fequencyValue  = "1";
				}

				if (fequencyValue != null && fequencyValue.equals("2")) {
					weekdayValue = rs.getString("weekday");
					annualDayValue = "";
					annualMonthValue = "";
					dayValue = "";
					monthdayValue = "";
					monthValue.add("");
				} else if (fequencyValue != null && fequencyValue.equals("3")) {
					weekdayValue = "";
					annualDayValue = "";
					annualMonthValue = "";
					dayValue = rs.getString("frequency_day");
					monthdayValue = "";
					monthValue.add("");
				} else if (fequencyValue != null && fequencyValue.equals("4")) {
					weekdayValue = "";
					annualDayValue = "";
					annualMonthValue = "";
					dayValue = "";
					monthdayValue = rs.getString("frequency_day");
					if (rs.getString("frequency_month") == null) {
						monthValue.add("");
					} else {
						
						
						if(rs.getString("frequency_month")==null ||rs.getString("frequency_month").equals("")){
							monthValue.add("");
						} else {
							List<String> monthValue1 = new ArrayList<String>();
							monthValue1 = Arrays.asList(rs.getString("frequency_month").split(","));
							for (int i = 0; i < monthValue1.size(); i++) {
								monthValue.add(monthValue1.get(i).trim());
							}
						}
					}
				} else if (fequencyValue != null && fequencyValue.equals("5")) {
					weekdayValue = "";
					annualDayValue = "";
					annualMonthValue = "";
					dayValue = "";
					monthdayValue = rs.getString("frequency_day");
					if (rs.getString("frequency_month") == null) {
						monthValue.add("");
					} else {
						List<String> monthValue1 = new ArrayList<String>();
						monthValue1 = Arrays.asList(rs.getString("frequency_month").split(","));
						
						if(monthValue1==null){
							monthValue.add("");
						} else {
							for (int i = 0; i < monthValue1.size(); i++) {
								monthValue.add(monthValue1.get(i).trim());
							}
						}
					}

				} else if (fequencyValue != null && fequencyValue.equals("6")) {
					weekdayValue = "";
					annualDayValue = rs.getString("frequency_day");
					annualMonthValue = rs.getString("frequency_month");
					dayValue = "";
					monthdayValue = "";
					monthValue.add("");
				}
				

//				System.out.println("goaltype==>"+goaltype+"==>is_measure_kra==>"+ rs.getString("is_measure_kra"));
				if (goaltype != null && (goaltype.equals("4") || goaltype.equals("5")) ) {
					if (rs.getString("is_measure_kra") != null && rs.getString("is_measure_kra").equals("t")) {
						measureKravalue = "Yes";
					} else {
						measureKravalue = "No";
					}
				} else {
					measureKravalue = "No";
				}
				addMKravalue = rs.getString("measure_kra");
				mkwithvalue = rs.getString("measure_type1");
				if (rs.getString("is_feedback") != null && rs.getString("is_feedback").equals("t")) {
					goalFeedbackvalue = "Yes";
				} else {
					goalFeedbackvalue = "No";
				}

				List<String> levelvalue1 = new ArrayList<String>();
				if (rs.getString("level_id") == null) {
					levelvalue1.add("0");
				} else {
					levelvalue1 = Arrays.asList(rs.getString("level_id").split(
							","));
				}
				if (levelvalue1 != null) {
					for (int i = 0; levelvalue1 != null
							&& i < levelvalue1.size(); i++) {
						levelvalue.add(levelvalue1.get(i).trim());
					}
				} else {
					levelvalue.add("0");
				}

				List<String> desigvalue1 = new ArrayList<String>();
				if (rs.getString("grade_id") == null) {
					desigvalue1.add("0");
				} else {
					desigvalue1 = Arrays.asList(rs.getString("grade_id").split(","));
				}
				if (desigvalue1 != null) {
					for (int i = 0; desigvalue1 != null && i < desigvalue1.size(); i++) {
						desigvalue.add(desigvalue1.get(i).trim());
					}
				} else {
					desigvalue.add("0");
				}

				List<String> empvalue1 = new ArrayList<String>();
				if (rs.getString("emp_ids") == null) {
					empvalue1.add("0");
				} else {
					empvalue1 = Arrays.asList(rs.getString("emp_ids").split(","));
				}
				if (empvalue1 != null) {
					for (int i = 0; i < empvalue1.size(); i++) {
						empvalue.add(empvalue1.get(i).trim());
					}
				} else {
					empvalue.add("0");
				}
				
				orientation_id = rs.getString("orientation_id");
				
				if(glCnt == 0) {
					hmFirstGoal.put(rs.getString("goal_id"), cinnerList);
				}
				if(glCnt > 0) {
					hmOtherGoals.put(rs.getString("goal_id"), cinnerList);
				}
				checkGoalInProcessOrNot(con, uF, rs.getString("goal_id"));
				
				request.setAttribute("strID", rs.getString("org_id"));
				request.setAttribute("checkClose", uF.parseToBoolean(rs.getString("is_close")));
				glCnt++;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from target_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			rs = pst.executeQuery();
			boolean flag = false;
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("checkTarget", flag);
			
			request.setAttribute("hmOtherGoals", hmOtherGoals);
			request.setAttribute("hmFirstGoal", hmFirstGoal);
//			request.setAttribute("innerList", cinnerList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void checkGoalInProcessOrNot(Connection con, UtilityFunctions uF, String goalId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			boolean flag = false;
			pst = con.prepareStatement("select appraisal_question_details_id from appraisal_question_details where goal_kra_target_id = ?");
			pst.setInt(1, uF.parseToInt(goalId));
//			System.out.println("pst ====>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select target_id from target_details where goal_id = ?");
			pst.setInt(1, uF.parseToInt(goalId));			
//			System.out.println("pst ====>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
//			System.out.println("flag ==>>> " + flag);
			request.setAttribute(goalId+"_processOrNotFlag", flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String getElementList(String elementID) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder("");

		try {
//			request.setAttribute("attribute", sb.toString());
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
			rs = pst.executeQuery();
			StringBuilder sbAjax = new StringBuilder("");
			while (rs.next()) {
				if(elementID != null && rs.getString("appraisal_element_id").equals(elementID)){
					sb.append("<option value=\"" + rs.getString("appraisal_element_id") + "\" selected=\"selected\">"+ rs.getString("appraisal_element_name") + "</option>");
					sbAjax.append("<option value=\"" + rs.getString("appraisal_element_id") + "\">"+ rs.getString("appraisal_element_name") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("appraisal_element_id") + "\">"+ rs.getString("appraisal_element_name") + "</option>");
					sbAjax.append("<option value=\"" + rs.getString("appraisal_element_id") + "\">"+ rs.getString("appraisal_element_name") + "</option>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("elementOptionsAjax", sbAjax.toString());
			request.setAttribute("elementOptions", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sb.toString();
	}	
	
	
public List<FillAttribute> getAttributeListElementwise(String elementID){
		
		List<FillAttribute> al = new ArrayList<FillAttribute>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from appraisal_attribute where status=true order by attribute_name");
			pst = con.prepareStatement("select distinct(a.appraisal_attribute),a.appraisal_element,aa.attribute_name from (select " +
					"appraisal_element,appraisal_attribute from appraisal_element_attribute where appraisal_element = ? ) as a, " +
					"appraisal_attribute aa where a.appraisal_attribute=aa.arribute_id order by appraisal_attribute");
			pst.setInt(1, uF.parseToInt(elementID));
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			while(rs.next()){
				al.add(new FillAttribute(rs.getString("appraisal_attribute"), rs.getString("attribute_name")));				
			}
			rs.close();
			pst.close();
			
//			System.out.println("ATTRIBUTES =====> "+al.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
public List<FillPerspective> fillPerspective() {
	
	List<FillPerspective> al = new ArrayList<FillPerspective>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {

		con = db.makeConnection(con);
		pst = con.prepareStatement("SELECT * FROM bsc_perspective_details order by bsc_perspective_id");
		rs = pst.executeQuery();
		while(rs.next()){
			al.add(new FillPerspective(rs.getString("bsc_perspective_id"), rs.getString("bsc_perspective_name")));				
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
	return al;
}


	
	private Map<String, String> getAttributeMap(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmAttribute = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmAttribute;
	}
	

	private String getAppendData(Connection con, String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()) + "("
								+ hmDesignation.get(temp[i].trim()) + ")");
					} else {
						sb.append("," + mp.get(temp[i].trim()) + "("
								+ hmDesignation.get(temp[i].trim()) + ")");
					}
				}
			} else {
				return mp.get(strID) + "(" + hmDesignation.get(strID) + ")";
			}

		} else {
			return null;
		}

		return sb.toString();
	}

	/*public void getattribute() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < attributeList.size(); i++) {
			FillAttribute fillAttribute = attributeList.get(i);

			sb.append("<option value='" + fillAttribute.getId() + "'>"
					+ fillAttribute.getName() + "</option>");

		}

		request.setAttribute("attribute", sb.toString());

	}*/

	private void getEmpDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < empList.size(); i++) {
			FillEmployee fillEmployee = empList.get(i);

			sb.append("<option value='" + fillEmployee.getEmployeeId() + "'>" + fillEmployee.getEmployeeCode() + "</option>");

		}
		request.setAttribute("empListOption", sb.toString());

	}

	private void getgradeDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < gradeList.size(); i++) {
			FillGrade fillGrade = gradeList.get(i);

			sb.append("<option value='" + fillGrade.getGradeId() + "'>" + fillGrade.getGradeCode() + "</option>");

		}
		request.setAttribute("gradeListOption", sb.toString());

	}

	private void getLevelDetailsList() {
		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < levelList.size(); i++) {
			FillLevel fillLevel = levelList.get(i);

			sb.append("<option value='" + fillLevel.getLevelId() + "'>" + fillLevel.getLevelCodeName() + "</option>");

		}
		request.setAttribute("levelListOption", sb.toString());
	}

	public void getOrientationDetailsList() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < orientationList.size(); i++) {
			FillOrientation fillOrientation = orientationList.get(i);

			sb.append("<option value=\"" + fillOrientation.getId() + "\">" + fillOrientation.getName() + "</option>");

		}
		request.setAttribute("orientation", sb.toString());

	}

	
	private void getFrequenyDetailsList() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < frequencyList.size(); i++) {
			FillFrequency fillFrequency = frequencyList.get(i);
			sb.append("<option value=\"" + fillFrequency.getId() + "\">" + fillFrequency.getName() + "</option>");
		}
		request.setAttribute("frequencyOption", sb.toString());
		
		StringBuilder sbdates = new StringBuilder("");
		for (int i = 1; i <=31; i++) {
			sbdates.append("<option value=\"" + i + "\">" + i + "</option>");
		}
		request.setAttribute("datesOption", sbdates.toString());
	}
	
	
	public String getGoaltype() {
		return goaltype;
	}

	public void setGoaltype(String goaltype) {
		this.goaltype = goaltype;
	}

	public String getGoalid() {
		return goalid;
	}

	public void setGoalid(String goalid) {
		this.goalid = goalid;
	}

	public List<FillOrientation> getOrientationList() {
		return orientationList;
	}

	public void setOrientationList(List<FillOrientation> orientationList) {
		this.orientationList = orientationList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public String getGoalattributevalue() {
		return goalattributevalue;
	}

	public void setGoalattributevalue(String goalattributevalue) {
		this.goalattributevalue = goalattributevalue;
	}

	public String getMeasurewithvalue() {
		return measurewithvalue;
	}

	public void setMeasurewithvalue(String measurewithvalue) {
		this.measurewithvalue = measurewithvalue;
	}

	public String getMeasureKravalue() {
		return measureKravalue;
	}

	public void setMeasureKravalue(String measureKravalue) {
		this.measureKravalue = measureKravalue;
	}

	public String getAddMKravalue() {
		return addMKravalue;
	}

	public void setAddMKravalue(String addMKravalue) {
		this.addMKravalue = addMKravalue;
	}

	public String getMkwithvalue() {
		return mkwithvalue;
	}

	public void setMkwithvalue(String mkwithvalue) {
		this.mkwithvalue = mkwithvalue;
	}

	public String getGoalFeedbackvalue() {
		return goalFeedbackvalue;
	}

	public void setGoalFeedbackvalue(String goalFeedbackvalue) {
		this.goalFeedbackvalue = goalFeedbackvalue;
	}

	public String[] getGradevalue() {
		return gradevalue;
	}

	public void setGradevalue(String[] gradevalue) {
		this.gradevalue = gradevalue;
	}

	public String getOrientation_id() {
		return orientation_id;
	}

	public void setOrientation_id(String orientation_id) {
		this.orientation_id = orientation_id;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public String getFequencyValue() {
		return fequencyValue;
	}

	public void setFequencyValue(String fequencyValue) {
		this.fequencyValue = fequencyValue;
	}

	public String getWeekdayValue() {
		return weekdayValue;
	}

	public void setWeekdayValue(String weekdayValue) {
		this.weekdayValue = weekdayValue;
	}

	public String getAnnualDayValue() {
		return annualDayValue;
	}

	public void setAnnualDayValue(String annualDayValue) {
		this.annualDayValue = annualDayValue;
	}

	public String getAnnualMonthValue() {
		return annualMonthValue;
	}

	public void setAnnualMonthValue(String annualMonthValue) {
		this.annualMonthValue = annualMonthValue;
	}

	public String getDayValue() {
		return dayValue;
	}

	public void setDayValue(String dayValue) {
		this.dayValue = dayValue;
	}

	public String getMonthdayValue() {
		return monthdayValue;
	}

	public void setMonthdayValue(String monthdayValue) {
		this.monthdayValue = monthdayValue;
	}

	public List<String> getMonthValue() {
		return monthValue;
	}

	public void setMonthValue(List<String> monthValue) {
		this.monthValue = monthValue;
	}

	public List<String> getEmpvalue() {
		return empvalue;
	}

	public void setEmpvalue(List<String> empvalue) {
		this.empvalue = empvalue;
	}

	public List<String> getDesigvalue() {
		return desigvalue;
	}

	public void setDesigvalue(List<String> desigvalue) {
		this.desigvalue = desigvalue;
	}

	public List<String> getLevelvalue() {
		return levelvalue;
	}

	public void setLevelvalue(List<String> levelvalue) {
		this.levelvalue = levelvalue;
	}
	

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeas() {
		return typeas;
	}

	public void setTypeas(String typeas) {
		this.typeas = typeas;
	}

	public String getIsMeasureKraVal() {
		return isMeasureKraVal;
	}

	public void setIsMeasureKraVal(String isMeasureKraVal) {
		this.isMeasureKraVal = isMeasureKraVal;
	}

	public String getIsMeasureKra() {
		return isMeasureKra;
	}

	public void setIsMeasureKra(String isMeasureKra) {
		this.isMeasureKra = isMeasureKra;
	}

	public String getGoalalignStatus() {
		return goalalignStatus;
	}

	public void setGoalalignStatus(String goalalignStatus) {
		this.goalalignStatus = goalalignStatus;
	}

	public String getGoal_parent_id() {
		return goal_parent_id;
	}

	public void setGoal_parent_id(String goal_parent_id) {
		this.goal_parent_id = goal_parent_id;
	}

	public String getGoalElement() {
		return goalElement;
	}

	public void setGoalElement(String goalElement) {
		this.goalElement = goalElement;
	}

	public String getSuperId() {
		return superId;
	}

	public void setSuperId(String superId) {
		this.superId = superId;
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

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getGoalTitle() {
		return goalTitle;
	}

	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getStrID() {
		return strID;
	}

	public void setStrID(String strID) {
		this.strID = strID;
	}

	
}
