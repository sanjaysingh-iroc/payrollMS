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
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPerspective;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditMyPersonalGoalPopUp extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private String goalid;
	private String goaltype;
	private String goalTypePG;

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
	private String createReviewYesno;
	
	private List<FillOrientation> orientationList;
	private List<FillLevel> levelList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList;
	private List<FillAttribute> attributeList;
	private List<FillPerspective> perspectiveList;
	private List<FillDesig> desigList;
	
	private List<FillYears> yearsList;
	private List<FillYears> yearTypeList;
	private List<FillMonth> monthsList;
	private List<FillMonth> quartersList;
	private List<FillMonth> halfYearsList;
	
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
		
		yearsList = new FillYears(request).fillYearsFromCurrentToNext10Year();
		yearTypeList = new FillYears().fillYearType();
		monthsList = new FillMonth().fillMonth();
		quartersList = new FillMonth().fillQuarters();
		halfYearsList = new FillMonth().fillHalfYears();
		
		getFrequenyDetailsList();
		
		levelList = new FillLevel(request).fillLevel();
		workList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		organisationList = new FillOrganisation(request).fillOrganisation();
		
		
		desigList = new FillDesig(request).fillDesig();

		gradeList = new FillGrade(request).fillGrade();

		userlocation = getLocation();
		
		if(strUserType != null && strUserType.equals(ADMIN)) {
			empList = new FillEmployee(request).fillEmployeeNameByLocation(null);
			
		} else if(strUserType != null && strUserType.equals(HRMANAGER)) {
			empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
		
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
		
		//===start parvez date: 04-09-2021===
		UtilityFunctions uF = new UtilityFunctions();
		getPolicyDetails(uF);
		//===end parvez date: 04-09-2021===
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
		
		//Started By Dattatray Date:08-09-21
		String strAttributeName = "";
		String strAttributeId = "";
		String strAttributeElementName = "";
		String strAttributeElementId = "";
		String strFeqId = "";
		String strFeqName = "";
		String strOrientationId = "";
		String strOrientationName = "";
		//End By Dattatray Date:08-09-21
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
//			System.out.println("cinnerList pst==>"+pst);
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
				//Started By Dattatray Date:08-09-21
				strAttributeName = getElementName(goalElement, uF);
				strAttributeId = goalElement;
				//End By Dattatray Date:08-09-21
				
				System.out.println("goalElement : "+ goalElement);
				System.out.println("getElementName : "+getElementName(goalElement, uF));
				createReviewYesno = rs.getString("review_this_goal");
				String elementList = getElementList(goalElement);
				System.out.println("elementList : "+ elementList);
				cinnerList.add(elementList); //31
				attributeList = getAttributeListElementwise(goalElement);
				StringBuilder sbAttribList = new StringBuilder();
				for(int i=0; attributeList != null && !attributeList.isEmpty() && i<attributeList.size(); i++) {
					if(rs.getString("goal_attribute") != null && attributeList.get(i).getId().equals(rs.getString("goal_attribute"))) {
						sbAttribList.append("<option value=\"" + attributeList.get(i).getId() + "\" selected=\"selected\">"+ attributeList.get(i).getName() + "</option>");
					//Started By Dattatray Date:08-09-21
					strAttributeElementId = attributeList.get(i).getId();
					strAttributeElementName = attributeList.get(i).getName();
					//Ended By Dattatray Date:08-09-21
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
						//Started By Dattatray Date:08-09-21
						strOrientationId =orientationList.get(i).getId();
					strOrientationName = orientationList.get(i).getName();
					
					//End By Dattatray Date:08-09-21
					} else {
						sbOrientList.append("<option value=\"" + orientationList.get(i).getId() + "\">"+ orientationList.get(i).getName() + "</option>");
					}
				}
				cinnerList.add(sbOrientList.toString()); //36
				
				StringBuilder sbFrequencyList = new StringBuilder();
				for(int i=0; frequencyList != null && !frequencyList.isEmpty() && i<frequencyList.size(); i++) {
					if(rs.getString("frequency") != null && frequencyList.get(i).getId().equals(rs.getString("frequency"))) {
						sbFrequencyList.append("<option value=\"" + frequencyList.get(i).getId() + "\" selected=\"selected\">"+ frequencyList.get(i).getName() + "</option>");
						//Started By Dattatray Date:08-09-21
						strFeqId = frequencyList.get(i).getId();
						strFeqName = frequencyList.get(i).getName();
						//Ended By Dattatray Date:08-09-21
					} else {
						sbFrequencyList.append("<option value=\"" + frequencyList.get(i).getId() + "\">"+ frequencyList.get(i).getName() + "</option>");
					}
				}
				cinnerList.add(sbFrequencyList.toString()); //37
				
				fequencyValue = rs.getString("frequency");
				
				if(fequencyValue == null || fequencyValue.equals("")) {
					fequencyValue  = "1";
				}
				
				List<String> alMonths = new ArrayList<String>();
				List<String> alQuaters = new ArrayList<String>();
				List<String> alHalfYears = new ArrayList<String>();
				List<String> alRecurrYears = new ArrayList<String>();
				
				if (fequencyValue != null && fequencyValue.equals("3")) {
					alMonths = Arrays.asList(rs.getString("frequency_month").split(","));

				} else if (fequencyValue != null && fequencyValue.equals("4")) {
					alQuaters = Arrays.asList(rs.getString("frequency_month").split(","));

				} else if (fequencyValue != null && fequencyValue.equals("5")) {
					alHalfYears = Arrays.asList(rs.getString("frequency_month").split(","));

				}
				if(rs.getString("recurring_years")!=null) {
					alRecurrYears = Arrays.asList(rs.getString("recurring_years").split(","));
				}
				
				
				StringBuilder sbmonthsList = new StringBuilder();
				for(int i=0; monthsList != null && !monthsList.isEmpty() && i<monthsList.size(); i++) {
					if(alMonths != null && alMonths.contains(monthsList.get(i).getMonthId())) {
						sbmonthsList.append("<option value=\"" + monthsList.get(i).getMonthId() + "\" selected=\"selected\">"+ monthsList.get(i).getMonthName() + "</option>");
					} else {
						sbmonthsList.append("<option value=\"" + monthsList.get(i).getMonthId() + "\">"+ monthsList.get(i).getMonthName() + "</option>");
					}
				}
				cinnerList.add(sbmonthsList.toString()); //38
				
				StringBuilder sbquartersList = new StringBuilder();
				for(int i=0; quartersList != null && !quartersList.isEmpty() && i<quartersList.size(); i++) {
					if(alQuaters != null && alQuaters.contains(quartersList.get(i).getMonthId())) {
						sbquartersList.append("<option value=\"" + quartersList.get(i).getMonthId() + "\" selected=\"selected\">"+ quartersList.get(i).getMonthName() + "</option>");
					} else {
						sbquartersList.append("<option value=\"" + quartersList.get(i).getMonthId() + "\">"+ quartersList.get(i).getMonthName() + "</option>");
					}
				}
				cinnerList.add(sbquartersList.toString()); //39
				
				StringBuilder sbhalfYearsList = new StringBuilder();
				for(int i=0; halfYearsList != null && !halfYearsList.isEmpty() && i<halfYearsList.size(); i++) {
					if(alHalfYears != null && alHalfYears.contains(halfYearsList.get(i).getMonthId())) {
						sbhalfYearsList.append("<option value=\"" + halfYearsList.get(i).getMonthId() + "\" selected=\"selected\">"+ halfYearsList.get(i).getMonthName() + "</option>");
					} else {
						sbhalfYearsList.append("<option value=\"" + halfYearsList.get(i).getMonthId() + "\">"+ quartersList.get(i).getMonthName() + "</option>");
					}
				}
				cinnerList.add(sbhalfYearsList.toString()); //40
				
				StringBuilder sbFreqYearTypeList = new StringBuilder();
				for(int i=0; yearTypeList != null && !yearTypeList.isEmpty() && i<yearTypeList.size(); i++) {
					if(rs.getString("orientation_id") != null && yearTypeList.get(i).getYearsID() == rs.getInt("freq_year_type")) {
						sbFreqYearTypeList.append("<option value=\"" + yearTypeList.get(i).getYearsID() + "\" selected=\"selected\">"+ yearTypeList.get(i).getYearsName() + "</option>");
					} else {
						sbFreqYearTypeList.append("<option value=\"" + yearTypeList.get(i).getYearsID() + "\">"+ yearTypeList.get(i).getYearsName() + "</option>");
					}
				}
				cinnerList.add(sbFreqYearTypeList.toString()); //41
				
				StringBuilder sbyearsList = new StringBuilder();
				for(int i=0; yearsList != null && !yearsList.isEmpty() && i<yearsList.size(); i++) {
					if(alRecurrYears != null && alRecurrYears.contains(yearsList.get(i).getYearsID()+"")) {
						sbyearsList.append("<option value=\"" + yearsList.get(i).getYearsID() + "\" selected=\"selected\">"+ yearsList.get(i).getYearsName() + "</option>");
					} else {
						sbyearsList.append("<option value=\"" + yearsList.get(i).getYearsID() + "\">"+ yearsList.get(i).getYearsName() + "</option>");
					}
				}
				cinnerList.add(sbyearsList.toString()); //42
				
				
				/*String[] tempWeekdays = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
				List<String> alWeekdays = Arrays.asList(tempWeekdays);
				StringBuilder sbWeekdayList = new StringBuilder();
				for(int i=0; alWeekdays != null && !alWeekdays.isEmpty() && i<alWeekdays.size(); i++) {
					if(rs.getString("weekday") != null && alWeekdays.get(i).equals(rs.getString("weekday").trim())) {
						sbWeekdayList.append("<option value=\"" + alWeekdays.get(i)+ "\" selected=\"selected\">"+ alWeekdays.get(i) + "</option>");
					} else {
						sbWeekdayList.append("<option value=\"" + alWeekdays.get(i) + "\">"+ alWeekdays.get(i) + "</option>");
					}
				}*/
//				cinnerList.add(sbWeekdayList.toString()); //38
				
				/*StringBuilder sbDatesList = new StringBuilder();
				for(int i=1; i<=31; i++) {
					if(rs.getString("frequency_day") != null && (i+"").equals(rs.getString("frequency_day").trim())) {
						sbDatesList.append("<option value=\"" + i + "\" selected=\"selected\">"+ i + "</option>");
					} else {
						sbDatesList.append("<option value=\"" + i + "\">"+ i + "</option>");
					}
				}*/
//				cinnerList.add(sbDatesList.toString()); //39
				
//				cinnerList.add(rs.getString("frequency")); //40
//				cinnerList.add(rs.getString("weekday")); //41
//				cinnerList.add(rs.getString("frequency_day")); //42
				
				
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
					sbIsAlignPerspective.append("<option value=\"" +"Yes"+ "\" selected=\"selected\">" +"Yes"+"</option>");

					sbIsAlignPerspective.append("<option value=\"" + "No" + "\">" +"No" + "</option>");

				} else {
					sbIsAlignPerspective.append("<option value=\"" +"No"+ "\" selected=\"selected\">" +"No"+"</option>");

					sbIsAlignPerspective.append("<option value=\"" + "Yes" + "\">" +"Yes" + "</option>");
					
				}
//				System.out.println("sbIsAlignPerspective::"+sbIsAlignPerspective.toString());
				cinnerList.add(sbIsAlignPerspective.toString()); //43
				cinnerList.add(sbAlignPerspectiveList.toString()); //44
				
				
				setPriority(rs.getString("priority"));
				setSuperId(rs.getString("super_id"));
				
				if(rs.getString("goalalign_with_teamgoal") != null && rs.getString("goalalign_with_teamgoal").equals("t")) {
					goalalignStatus = "Yes";
				} else {
					goalalignStatus = "No";
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
//			System.out.println("hmFirstGoal : "+hmFirstGoal);
			
			//Started By Dattatray Date:08-09-21
			request.setAttribute("strAttributeName", strAttributeName);
			request.setAttribute("strAttributeId", strAttributeId);
			request.setAttribute("strAttributeElementName", strAttributeElementName);
			request.setAttribute("strAttributeElementId", strAttributeElementId);
			request.setAttribute("strFeqId", strFeqId);
			request.setAttribute("strFeqName", strFeqName);
			request.setAttribute("strOrientationId", strOrientationId);
			request.setAttribute("strOrientationName", strOrientationName);
			//End By Dattatray Date:08-09-21
			
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
	
	
	public String getElementName(String elementID,UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		String strName="";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element where appraisal_element_id = ?");
			pst.setInt(1, uF.parseToInt(elementID));
			rs = pst.executeQuery();
			while (rs.next()) {
				strName = rs.getString("appraisal_element_name");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return strName;
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
	
	//===start parvez date: 04-09-2021===
	private void getPolicyDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String policy_id = null;
		try {
			
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String empLevelId = CF.getEmpLevelId(con, strSessionEmpId);
//			System.out.println("MGPU/509---empLevelId="+empLevelId);
//			System.out.println("MGPU/510---strSessionEmpId="+strSessionEmpId);
//			System.out.println("MGPU/511--levelList="+getLevelList());
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_PERSONAL_GOAL+"' and level_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(getLocation()));
//			System.out.println("MGPU/518--pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				policy_id=rs.getString("policy_id");
			}
			rs.close();
			pst.close();
//			System.out.println("policy_id ===>> " + policy_id);
			
			if(uF.parseToInt(policy_id) == 0){
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(getLocation()));
//				System.out.println("MGPU/531--pst="+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					policy_id=rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}
			
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
			
			Map<String, String> hmWorkflowData = new HashMap<String, String>();
			String workflowEmpId = null;
			pst = con.prepareStatement("select user_type_id, emp_id from work_flow_details where effective_id =? and effective_type='"+WORK_FLOW_PERSONAL_GOAL+"' order by member_position");
			pst.setInt(1, uF.parseToInt(goalid));
//			System.out.println("MGPU/547--pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmWorkflowData.put(rs.getString("user_type_id"), rs.getString("emp_id"));
				workflowEmpId = rs.getString("emp_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id)>0) {
				
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
						" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
				rs=pst.executeQuery();
				Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
				while(rs.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("member_type"));
					innerList.add(rs.getString("member_id"));
					innerList.add(rs.getString("member_position"));
					innerList.add(rs.getString("work_flow_mem"));
					innerList.add(rs.getString("work_flow_member_id"));
					
					hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
				}
				rs.close();
				pst.close();
				
				
				Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
				
//				System.out.println("hmMemberMap ===>> " + hmMemberMap);
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1) {
						int memid=uF.parseToInt(innerList.get(1));
						
						switch(memid) {
						
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname, epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
										+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and epd.is_alive=true order by epd.emp_fname ");
								rs = pst.executeQuery();
								List<List<String>> outerList=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList!=null && !outerList.isEmpty()) {
									StringBuilder sbComboBox=new StringBuilder();
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++) {
										List<String> alList=outerList.get(i);
										sbComboBox.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("1")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("1")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox.append("</select>");								
									
									String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								StringBuilder sbQuery = new StringBuilder();
								sbQuery.append("select * from (select distinct(supervisor_emp_id) as supervisor_emp_id from employee_official_details where emp_id > 0 and wlocation_id=? ");
								sbQuery.append(" and grade_id in (select grade_id from level_details l, designation_details di, grades_details gd where l.level_id = di.level_id and di.designation_id = gd.designation_id and l.level_id = ?) and supervisor_emp_id!=0 ");
								if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
									sbQuery.append(" and supervisor_emp_id = (select supervisor_emp_id from employee_official_details where emp_id ="+strSessionEmpId+")");
								}
								sbQuery.append(" ) as a, employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
								pst = con.prepareStatement(sbQuery.toString());
								pst.setInt(1, uF.parseToInt(getLocation()));
								pst.setInt(2, uF.parseToInt(empLevelId));
								rs = pst.executeQuery(); 
//								System.out.println("MGPU/643--pst ===>> " + pst);
								List<List<String>> outerList11=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(MANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList11.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList11!=null && !outerList11.isEmpty()) {
									StringBuilder sbComboBox11=new StringBuilder();
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++) {
										List<String> alList=outerList11.get(i);
										sbComboBox11.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("2")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("2")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox11.append("</select>");								
									
									String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and grade_id in (select grade_id from level_details l, designation_details di, grades_details gd where l.level_id = di.level_id and di.designation_id = gd.designation_id and l.level_id = ?)"
												+ " and epd.is_alive=true order by epd.emp_fname ");
								pst.setInt(1, uF.parseToInt(getLocation()));
								pst.setInt(2, uF.parseToInt(empLevelId));
								rs = pst.executeQuery();
//								System.out.println("MGPU/692--pst="+pst);
								List<List<String>> outerList1=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList1.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList1!=null && !outerList1.isEmpty()) {
									StringBuilder sbComboBox1=new StringBuilder();
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++) {
										List<String> alList=outerList1.get(i);
										sbComboBox1.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("3")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("3")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox1.append("</select>");								
									
									String optionTr1="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getLocation()+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and epd.is_alive=true order by epd.emp_fname");
//								pst.setInt(1, uF.parseToInt(getStrWlocation()));
								rs = pst.executeQuery();
//								System.out.println("MGPU/739--pst="+pst);
								List<List<String>> outerList2=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname"));
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList2.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList2!=null && !outerList2.isEmpty()) {
									StringBuilder sbComboBox2=new StringBuilder();
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList2.size();i++) {
										List<String> alList=outerList2.get(i);
										sbComboBox2.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("4")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("4")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox2.append("</select>");								
									
									String optionTr2="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=5 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getLocation()+",%' " +
									"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true " +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
									"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true");
//								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
//										+ " employee_official_details eod,employee_personal_details epd where (ud.usertype_id=5 or ud.usertype_id=1)"
//										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getStrWlocation()+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
//										+ " and epd.is_alive=true order by epd.emp_fname");
//								System.out.println("MGPU/790--pst="+pst);
								rs = pst.executeQuery();
								List<List<String>> outerList3=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList3.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList3!=null && !outerList3.isEmpty()) {
									StringBuilder sbComboBox3=new StringBuilder();
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++) {
										List<String> alList=outerList3.get(i);
										sbComboBox3.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("5")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("5")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox3.append("</select>");								
									
									String optionTr3="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
								"employee_personal_details epd where ud.usertype_id=6 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getLocation()+",%' " +
								"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true " +
								" union " +
								"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
								"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
								"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true");
//							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
//										+ " employee_official_details eod,employee_personal_details epd where (ud.usertype_id=6 or ud.usertype_id=1) "
//										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
//										+ " and epd.is_alive=true order by epd.emp_fname");
//								pst.setInt(1, uF.parseToInt(getStrWlocation()));
								rs = pst.executeQuery();
//								System.out.println("MGPU/845--pst="+pst);
								List<List<String>> outerList4=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList4.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList4!=null && !outerList4.isEmpty()) {
									StringBuilder sbComboBox4=new StringBuilder();
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++) {
										List<String> alList=outerList4.get(i);
										sbComboBox4.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("6")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("6")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox4.append("</select>");								
									
									String optionTr4="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr4);
								}
								break;
							
						case 7:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getLocation()+",%' " +
									"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true " +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
									"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true");
								
								rs = pst.executeQuery();
//								System.out.println("MGPU/895--pst="+pst);
								List<List<String>> outerList5=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(HRMANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList5.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList5!=null && !outerList5.isEmpty()) {
									StringBuilder sbComboBox5=new StringBuilder();
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++) {
										List<String> alList=outerList5.get(i);
										sbComboBox5.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("7")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("7")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox5.append("</select>");								
									
									String optionTr5="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;	
								
						case 13:
							pst = con.prepareStatement("select * from  employee_personal_details epd, employee_official_details eod,user_details ud where epd.emp_per_id=eod.emp_id "
									+" and eod.emp_id = ud.emp_id and epd.emp_per_id = ud.emp_id and usertype_id = 13 and wlocation_id=? and ud.status='ACTIVE' and epd.is_alive=true "
									+" order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(getLocation()));
//							System.out.println("MGPU/939--pst="+pst);
							rs = pst.executeQuery();
							
							List<List<String>> outerHODList=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								
								alList.add(rs.getString("emp_lname"));
								
								outerHODList.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerHODList!=null && !outerHODList.isEmpty()){
								StringBuilder sbComboBox11=new StringBuilder();
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++){
									List<String> alList=outerHODList.get(i);
									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("13")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("13")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
//									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
								
								hmMemberOption.put(innerList.get(4), optionTr11);
							}
						
							break;
						
						}						
						
					} else if(uF.parseToInt(innerList.get(0))==3) {
						int memid=uF.parseToInt(innerList.get(1));
						
						
						List<List<String>> outerList=new ArrayList<List<String>>();
						pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id " +
								"and se.policy_id = ? and epd.is_alive=true order by epd.emp_fname"); 
						pst.setInt(1,uF.parseToInt(policy_id));
						rs = pst.executeQuery();
						while (rs.next()) {
							List<String> alList = new ArrayList<String>();
							alList.add(rs.getString("emp_id"));
							outerList.add(alList);
						}
						rs.close();
						pst.close();
						
						if(outerList!=null && !outerList.isEmpty()) {
							StringBuilder sbComboBox = new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							String a = "";
							sbComboBox.append("<option value=\""+a+"\">Select "+innerList.get(3)+"</option>");
//							System.out.println("hmWorkflowData ===>> " + hmWorkflowData + " -- hmWorkflowData.get(memid) ===>> " + uF.parseToInt(hmWorkflowData.get(memid)));
							
							for(int i=0;i<outerList.size();i++) {
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\" "+((uF.parseToInt(workflowEmpId) == uF.parseToInt(alList.get(0)) ) ? " selected" : "")+">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	//===end parvez date 04-09-2021
	
	
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

	public List<FillYears> getYearsList() {
		return yearsList;
	}

	public void setYearsList(List<FillYears> yearsList) {
		this.yearsList = yearsList;
	}

	public List<FillYears> getYearTypeList() {
		return yearTypeList;
	}

	public void setYearTypeList(List<FillYears> yearTypeList) {
		this.yearTypeList = yearTypeList;
	}

	public List<FillMonth> getMonthsList() {
		return monthsList;
	}

	public void setMonthsList(List<FillMonth> monthsList) {
		this.monthsList = monthsList;
	}

	public List<FillMonth> getQuartersList() {
		return quartersList;
	}

	public void setQuartersList(List<FillMonth> quartersList) {
		this.quartersList = quartersList;
	}

	public List<FillMonth> getHalfYearsList() {
		return halfYearsList;
	}

	public void setHalfYearsList(List<FillMonth> halfYearsList) {
		this.halfYearsList = halfYearsList;
	}

	public String getCreateReviewYesno() {
		return createReviewYesno;
	}

	public void setCreateReviewYesno(String createReviewYesno) {
		this.createReviewYesno = createReviewYesno;
	}

	public String getGoalTypePG() {
		return goalTypePG;
	}

	public void setGoalTypePG(String goalTypePG) {
		this.goalTypePG = goalTypePG;
	}

}