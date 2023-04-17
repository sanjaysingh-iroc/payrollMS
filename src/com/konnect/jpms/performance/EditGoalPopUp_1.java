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
import com.konnect.jpms.select.FillManager;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditGoalPopUp_1 extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	String strEmpOrgId = null;

	private String goalid;
	private String goaltype;
	private String score;
	private String goalParentID;
	private String fromPage;
	
	private String goalattributevalue;
	private String goalelement;
	private String measurewithvalue;
	private String measureKravalue;
	private String addMKravalue;
	private String mkwithvalue;
	private String goalFeedbackvalue;
	private String[] gradevalue;
	private String orientation_id;
	private String isMeasureKra;
	private String isMeasureKraVal;
	private String strOrgVal;
	private String cgoalEffectDate;
	private String cgoalDueDate;
	
	private List<FillOrientation> orientationList;
	private List<FillLevel> levelList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList;
	private List<FillAttribute> attributeList;
	private List<FillDesig> desigList;
	private List<FillManager> managerList;
	
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
	private String monthdayValue;
	private String goalCreaterId;
	private List<String> monthValue = new ArrayList<String>();
		
	private String userlocation;
	private String priority;
	private	String strOrg;
	private String goalTitle;
	private String goalCType;
	private String dataType;
	private String currUserType;
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getStrOrg() == null){
			setStrOrg(strEmpOrgId);
		}
		
		frequencyList=new FillFrequency(request).fillFrequency();
		
		managerList  = new FillManager(request).fillManager();
		userlocation = getLocation();
		
		orientationList = new FillOrientation(request).fillOrientation();
		getOrientationDetailsList();
		workList = new FillWLocation(request).fillWLocation(getStrOrg());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		getLevelDetailsList();
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getStrOrg()));
		gradeList = new FillGrade(request).fillGradeByOrg(uF.parseToInt(getStrOrg()));
		getgradeDetailsList();
		
		
		empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);

		if (goaltype != null && (goaltype.equals(TEAM_GOAL+"") || goaltype.equals(INDIVIDUAL_GOAL+""))) {
			// || goaltype.equals("4")
			String supervisorId = "";
			if (goaltype.equals(TEAM_GOAL+"")) {
				supervisorId = getSupervisorID();
			} else if (goaltype.equals(INDIVIDUAL_GOAL+"")) {
				supervisorId = getSupervisorIDIndividual();
				getEmployeeForIndividualGoal();
			}
			if(supervisorId != null && !supervisorId.equals("")){
				supervisorId = supervisorId.substring(1, supervisorId.length()-1);
			}
			empList = new FillEmployee(request).fillEmployeeName();
//			empList = new FillEmployee().fillEmployeeNameBySupervisor(supervisorId);
			getEmpDetailsList();
			request.setAttribute("supervisorId", supervisorId);
		} else {
			empList = new FillEmployee(request).fillEmployeeNameByOrg(uF.parseToInt(getStrOrg()));
			getEmpDetailsList();
		}

//		attributeList = new FillAttribute().fillAttribute();
//		getElementList();
//		getattribute();
		
		getGoalTypeDetails();
		getGoalData();
		if(getIsMeasureKra() != null && getIsMeasureKra().equals("t")){
			setIsMeasureKraVal("Yes");
		}else{
			setIsMeasureKraVal("No");
		}
		getSelectEmployeeList();

		return LOAD;

	}
	
	
	public void getElementList(String elementID) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			
			StringBuilder sb = new StringBuilder("");

			request.setAttribute("attribute", sb.toString());
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(elementID != null && rs.getString("appraisal_element_id").equals(elementID)){
					sb.append("<option value='" + rs.getString("appraisal_element_id") + "' selected=\"selected\">"+ rs.getString("appraisal_element_name") + "</option>");
				}else{
					sb.append("<option value='" + rs.getString("appraisal_element_id") + "'>"+ rs.getString("appraisal_element_name") + "</option>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("elementOptions",sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	private String getEmployeeForIndividualGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";
		StringBuilder teamGoalEmpId =  new StringBuilder();
		String teamGoalPerentID = "";
		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select * from goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(goalParentID));
			rst = pst.executeQuery();
			while (rst.next()) {
				teamGoalEmpId.append(rst.getString("emp_ids"));
				teamGoalPerentID = rst.getString("goal_parent_id");
			}
			rst.close();
			pst.close();
			
			List<String> teamindEmplist = Arrays.asList(teamGoalEmpId.toString().split(","));
			if(!teamGoalPerentID.equals("")){
				pst = con.prepareStatement("select * from goal_details where goal_id=?");
				pst.setInt(1, uF.parseToInt(teamGoalPerentID));
				rst = pst.executeQuery();
				while (rst.next()) {
					List<String> managerindEmplist = Arrays.asList(rst.getString("emp_ids").split(","));
					for(int i=0; managerindEmplist != null && i<managerindEmplist.size();i++) {
//						System.out.println("ID Before ===> "+managerindEmplist.get(i));
						if(!teamindEmplist.contains(rst.getString("emp_ids"))) {
	//						System.out.println("ID After ===> "+managerindEmplist.get(i));
							teamGoalEmpId.append(managerindEmplist.get(i)+",");
						}
					}
					teamGoalPerentID = rst.getString("goal_parent_id");
				}
				rst.close();
				pst.close();
			}
			List<String> individualEmplist = Arrays.asList(teamGoalEmpId.toString().split(","));
			request.setAttribute("individualEmplist", individualEmplist);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return location;
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
			pst = con.prepareStatement("select * from goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			String responsibleEmpID=null;
			while(rst.next()){		
				selectEmpIDs=rst.getString("emp_ids");
				responsibleEmpID=rst.getString("responsible_emp_id");
			}
			rst.close();
			pst.close();
			
			List<List<String>> selectEmpList = new ArrayList<List<String>>();
			Map<String,String> hmCheckEmpList=new HashMap<String, String>();
//			String empids="";
			StringBuilder sb = new StringBuilder();
			StringBuilder sbOption = new StringBuilder();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
				
				List<String> tmpselectEmpList=Arrays.asList(selectEmpIDs.split(","));
				
				int i=0;
				if(tmpselectEmpList != null && !tmpselectEmpList.isEmpty()){
					for(String empId:tmpselectEmpList){
						if(empId.equals("0") || empId.equals("")){
							continue;
						}
						List<String> innerList = new ArrayList<String>();
						innerList.add(empId);
						innerList.add(hmEmpName.get(empId));
						selectEmpList.add(innerList);
						if(responsibleEmpID != null && responsibleEmpID.equals(empId)){
	//						System.out.println("in if empId == "+empId + "  responsibleEmpID == "+responsibleEmpID);
							sbOption.append("<option value=\"" + empId + "\" selected=\"selected\">" + hmEmpName.get(empId) + "</option>");
						}else{
							sbOption.append("<option value=\"" + empId + "\">" + hmEmpName.get(empId) + "</option>");
						}
						if(i==0){
							sb.append(empId);
							i++;
						}else{
							sb.append(","+empId);
						}
					hmCheckEmpList.put(empId.trim(), empId.trim());
					}
				}
				/*Set<String> trainerSet = new HashSet<String>(tmpselectEmpList);
				Iterator<String> itr = trainerSet.iterator();
				
				while (itr.hasNext()) {
					String trainerId = (String) itr.next();
					if(trainerId!=null && !trainerId.equals("")){
						selectEmpList.add(hmEmpName.get(trainerId.trim()));
						hmCheckEmpList.put(trainerId.trim(), trainerId.trim());
						if(i==0){
							empids=trainerId.trim();
							i++;
						}else{
							empids+=","+trainerId.trim();
							i++;
						}
					}
				}*/
			}else{
				selectEmpList=null;
				
			}
			request.setAttribute("selectEmpList", selectEmpList);
			request.setAttribute("hmCheckEmpList", hmCheckEmpList);
			request.setAttribute("empids", sb.toString());
			request.setAttribute("SelectEmpOption", sbOption.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
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
	

	private String getSupervisorIDIndividual() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();
		String supervisor_id = "";
		try {

			con = db.makeConnection(con);

			String individual_parentId = "";
			pst = con
					.prepareStatement("select goal_parent_id from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(goalid));
			rs = pst.executeQuery();
			while (rs.next()) {
				individual_parentId = rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();

			String team_parentId = "";
			pst = con
					.prepareStatement("select goal_parent_id from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(individual_parentId));
			rs = pst.executeQuery();
			while (rs.next()) {
				team_parentId = rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select emp_ids from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(team_parentId));
			rs = pst.executeQuery();
			while (rs.next()) {
				supervisor_id = rs.getString("emp_ids");
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
		return supervisor_id;
	}

	
	
	private String getSupervisorID() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uf = new UtilityFunctions();
		String supervisor_id = "";
		try {

			con = db.makeConnection(con);

			String goal_parent_id = "";
			pst = con
					.prepareStatement("select goal_parent_id from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(goalid));
			rs = pst.executeQuery();
			while (rs.next()) {
				goal_parent_id = rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select emp_ids from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(goal_parent_id));
			rs = pst.executeQuery();
			while (rs.next()) {
				supervisor_id = rs.getString("emp_ids");
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
		return supervisor_id;
	}

	private void getGoalData() {
		getCorporateData();
//		System.out.println("goaltype === " + goaltype);
		if (goaltype.equals(INDIVIDUAL_GOAL+"") || goaltype.equals(INDIVIDUAL_KRA+"") || goaltype.equals(INDIVIDUAL_TARGET+"")) {
			getKraDetails();
		}
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
			
			pst = con.prepareStatement("select * from goal_kras where goal_id=? order by goal_kra_id");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			rs = pst.executeQuery();
			StringBuilder sbKraIds = null;
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				List<List<String>> outerList = hmKRA.get(rs.getString("goal_id"));
				if (outerList == null) outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				// goal_kra_id,goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type,
				innerList.add(rs.getString("goal_kra_id")); //0
				innerList.add(rs.getString("kra_description")); //1
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				innerList.add(rs.getString("is_approved"));
				innerList.add(rs.getString("approved_by"));
				innerList.add(rs.getString("kra_order"));
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
			if(sbKraIds != null) {
				pst = con.prepareStatement("select * from activity_info where kra_id in ("+sbKraIds.toString()+") order by task_id");
				rs = pst.executeQuery();
				List<String> taskNameList = new ArrayList<String>();
				while (rs.next()) {
					if(!taskNameList.contains(rs.getString("activity_name"))) {
						taskNameList.add(rs.getString("activity_name"));
						
						List<List<String>> outerList = hmKRATasks.get(rs.getString("kra_id"));
						if (outerList == null) outerList = new ArrayList<List<String>>();
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("task_id"));
						innerList.add(rs.getString("activity_name"));
						innerList.add(rs.getString("resource_ids"));
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

//	private void getGoalKras() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uf = new UtilityFunctions();
//		Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();
//		try {
//			con = db.makeConnection(con);
//			
//			
//			pst = con.prepareStatement("select * from goal_kras where goal_id=? order by goal_kra_id");
//			pst.setInt(1, uf.parseToInt(getGoalid()));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				List<List<String>> outerList = hmKRA.get(rs
//						.getString("goal_id"));
//				if (outerList == null)
//					outerList = new ArrayList<List<String>>();
//
//				List<String> innerList = new ArrayList<String>();
//
//				// goal_kra_id,goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type,
//				innerList.add(rs.getString("goal_kra_id"));
//				innerList.add(rs.getString("goal_id"));
//				innerList.add(uf.getDateFormat(rs.getString("entry_date"),
//						DBDATE, DATE_FORMAT));
//				innerList.add(uf.getDateFormat(rs.getString("effective_date"),
//						DBDATE, DATE_FORMAT));
//				innerList.add(rs.getString("is_approved"));
//				innerList.add(rs.getString("approved_by"));
//				innerList.add(rs.getString("kra_order"));
//				innerList.add(rs.getString("kra_description"));
//				innerList.add(rs.getString("goal_type"));
//
//				outerList.add(innerList);
//				hmKRA.put(rs.getString("goal_id"), outerList);
//			}
//			request.setAttribute("hmKRA", hmKRA);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//	}

	private void getCorporateData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("hmEmpName", hmEmpName);
			Map<String, List<String>> hmCorporate = new HashMap<String, List<String>>();
			Map<String, String> hmAttribute = getAttributeMap(con);
			
			Map<String, String> hmOrgName = new HashMap<String, String>();
			pst = con.prepareStatement("select od.org_id,od.org_name from goal_details gd, org_details od where od.org_id=gd.org_id and gd.goal_id=?");
			pst.setInt(1, uF.parseToInt(goalid));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOrgName.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			rs = pst.executeQuery();
			List<String> cinnerList = new ArrayList<String>();
			while (rs.next()) {
				double curweightage = uF.parseToDouble(getScore())+ uF.parseToDouble(rs.getString("weightage"));
				setScore(curweightage+"");
				cinnerList.add(rs.getString("goal_id"));//0
				cinnerList.add(rs.getString("goal_type"));//1
				cinnerList.add(rs.getString("goal_parent_id"));//2
				cinnerList.add(rs.getString("goal_title"));//3
				cinnerList.add(rs.getString("goal_objective"));//4
				cinnerList.add(rs.getString("goal_description"));//5
				goalattributevalue = rs.getString("goal_attribute");
				cinnerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")), ""));//6
				measurewithvalue = rs.getString("measure_type");
				cinnerList.add(rs.getString("measure_type"));//7
				cinnerList.add(rs.getString("measure_currency_value"));//8
				cinnerList.add(rs.getString("measure_currency_id"));//9
				cinnerList.add(rs.getString("measure_effort_days"));//10
				cinnerList.add(rs.getString("measure_effort_hrs"));//11
				cinnerList.add(rs.getString("measure_type1"));//12
				cinnerList.add(rs.getString("measure_kra"));//13
				cinnerList.add(rs.getString("measure_currency_value1"));//14
				cinnerList.add(rs.getString("measure_currency1_id"));//15
				cinnerList.add(uF.getDateFormat(rs.getString("due_date"),DBDATE, DATE_FORMAT)); //16
				setCgoalDueDate(uF.getDateFormat(rs.getString("due_date"),DBDATE, DATE_FORMAT)); 
				cinnerList.add(rs.getString("is_feedback"));//17
				cinnerList.add(rs.getString("orientation_id"));//18
				cinnerList.add(rs.getString("weightage"));//10
				cinnerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));//20
				cinnerList.add(rs.getString("entry_date"));//21
				cinnerList.add(rs.getString("user_id"));//22
				isMeasureKra = rs.getString("is_measure_kra");
				cinnerList.add(rs.getString("is_measure_kra"));//23
				cinnerList.add(rs.getString("measure_kra_days"));//24
				cinnerList.add(rs.getString("measure_kra_hrs"));//25
				cinnerList.add(rs.getString("grade_id"));//26
				cinnerList.add(rs.getString("level_id"));//27
				cinnerList.add(rs.getString("frequency"));//28 
//				System.out.println("frequency==>"+rs.getString("frequency"));
				cinnerList.add(rs.getString("measure_desc"));//29
				cinnerList.add(rs.getString("measure_val"));//30
				
				setPriority(rs.getString("priority"));

				fequencyValue = rs.getString("frequency");
				//updated by kalpana on 18/10/2016
				/**
				 * start
				 */
				if(fequencyValue == null || fequencyValue.equals("")) {
					setFequencyValue("1");
				}
				
				/**
				 * end
				 */
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
						}else{
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
						}else{
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
				

				if (goaltype != null && goaltype.equals("4")) {
					if (rs.getString("is_measure_kra") != null
							&& rs.getString("is_measure_kra").equals("t")) {
						setMeasureKravalue("Yes");
					} else {
						setMeasureKravalue("No");
					}
				} else {
					setMeasureKravalue("No");
				}
				addMKravalue = rs.getString("measure_kra");
				mkwithvalue = rs.getString("measure_type1");
				if (rs.getString("is_feedback").equals("t")) {
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
					desigvalue1 = Arrays.asList(rs.getString("grade_id").split(
							","));
				}
				if (desigvalue1 != null) {
					for (int i = 0; desigvalue1 != null
							&& i < desigvalue1.size(); i++) {
						desigvalue.add(desigvalue1.get(i).trim());
					}
				} else {
					desigvalue.add("0");
				}

				List<String> empvalue1 = new ArrayList<String>();
				if (rs.getString("emp_ids") == null) {
					empvalue1.add("0");
				} else {
					empvalue1 = Arrays.asList(rs.getString("emp_ids")
							.split(","));
				}
				if (empvalue1 != null) {
					for (int i = 0; i < empvalue1.size(); i++) {
						empvalue.add(empvalue1.get(i).trim());
					}
				} else {
					empvalue.add("0");
				}
				
				orientation_id = rs.getString("orientation_id");
				cinnerList.add(rs.getString("measure_desc"));//31
				cinnerList.add(uF.getDateFormat(rs.getString("effective_date"),DBDATE, DATE_FORMAT)); //32
				setCgoalEffectDate(uF.getDateFormat(rs.getString("effective_date"),DBDATE, DATE_FORMAT));
				cinnerList.add(rs.getString("goal_creater_type"));//33
				cinnerList.add(rs.getString("goal_creater_id"));//34
				goalCreaterId = rs.getString("goal_creater_id");
				goalelement = rs.getString("goal_element");
				strOrgVal = rs.getString("org_id");
				
				//updated  by kalpana on 18/10/2016
				/**
				 * start
				 */
				StringBuilder sbFrequencyList = new StringBuilder();
				for(int i=0; frequencyList != null && !frequencyList.isEmpty() && i<frequencyList.size(); i++) {
					if(rs.getString("frequency") != null && frequencyList.get(i).getId().equals(rs.getString("frequency"))) {
						sbFrequencyList.append("<option value=\"" + frequencyList.get(i).getId() + "\" selected=\"selected\">"+ frequencyList.get(i).getName() + "</option>");
					} else {
						sbFrequencyList.append("<option value=\"" + frequencyList.get(i).getId() + "\">"+ frequencyList.get(i).getName() + "</option>");
					}
				}
				cinnerList.add(sbFrequencyList.toString()); //35
				
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
				cinnerList.add(sbWeekdayList.toString()); //36
				
				StringBuilder sbDatesList = new StringBuilder();
				for(int i=1; i<=31; i++) {
					if(rs.getString("frequency_day") != null && (i+"").equals(rs.getString("frequency_day").trim())) {
						sbDatesList.append("<option value=\"" + i + "\" selected=\"selected\">"+ i + "</option>");
					} else {
						sbDatesList.append("<option value=\"" + i + "\">"+ i + "</option>");
					}
				}
				cinnerList.add(sbDatesList.toString()); //37
				
			
				cinnerList.add(rs.getString("weekday")); //38
				cinnerList.add(rs.getString("frequency_day")); //39
				
				StringBuilder sbOrientList = new StringBuilder();
				for(int i=0; orientationList != null && !orientationList.isEmpty() && i<orientationList.size(); i++) {
					if(rs.getString("orientation_id") != null && orientationList.get(i).getId().equals(rs.getString("orientation_id"))) {
						sbOrientList.append("<option value=\"" + orientationList.get(i).getId() + "\" selected=\"selected\">"+ orientationList.get(i).getName() + "</option>");
					} else {
						sbOrientList.append("<option value=\"" + orientationList.get(i).getId() + "\">"+ orientationList.get(i).getName() + "</option>");
					}
				}
				cinnerList.add(sbOrientList.toString()); //40
//				System.out.println("strOrgVal ===> " + strOrgVal);
				
				/**
				 * end
				 */
//				System.out.println("java innerList===>"+cinnerList.size());
				hmCorporate.put(rs.getString("goal_id"), cinnerList);
				
				request.setAttribute("checkClose",uF.parseToBoolean(rs.getString("is_close")));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(strOrgVal)) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(strOrgVal));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			request.setAttribute("strCurrency",strCurrency);
			
			List<String> indiGoalEmpIds = new ArrayList<String>();
			StringBuilder sbEmpIds = null;
			if(uF.parseToInt(getGoaltype()) == MANAGER_GOAL) {
				pst = con.prepareStatement("select goal_id from goal_details where goal_parent_id = ?");
				pst.setInt(1, uF.parseToInt(goalid));
				rs = pst.executeQuery();
				StringBuilder sbTeamGoalIds = null;
				while (rs.next()) {
					if(sbTeamGoalIds == null) {
						sbTeamGoalIds = new StringBuilder();
						sbTeamGoalIds.append(rs.getString("goal_id"));
					} else {
						sbTeamGoalIds.append(","+rs.getString("goal_id"));
					}
				}
				rs.close();
				pst.close();
				
				if(sbTeamGoalIds != null) {
					pst = con.prepareStatement("select emp_ids from goal_details where goal_parent_id in ("+sbTeamGoalIds.toString()+")");
					rs = pst.executeQuery();
					while (rs.next()) {
						if(sbEmpIds == null) {
							sbEmpIds = new StringBuilder();
							sbEmpIds.append(rs.getString("emp_ids"));
						} else {
							sbEmpIds.append(rs.getString("emp_ids"));
						}
					}
					rs.close();
					pst.close();
					
					if(sbEmpIds != null) {
						indiGoalEmpIds = Arrays.asList(sbEmpIds.toString().split(","));
					}
				}
				
				
			} else if(uF.parseToInt(getGoaltype()) == TEAM_GOAL) {
				pst = con.prepareStatement("select emp_ids from goal_details where goal_parent_id = ?");
				pst.setInt(1, uF.parseToInt(goalid));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(rs.getString("emp_ids"));
					} else {
						sbEmpIds.append(rs.getString("emp_ids"));
					}
				}
				rs.close();
				pst.close();
				
				if(sbEmpIds != null) {
					indiGoalEmpIds = Arrays.asList(sbEmpIds.toString().split(","));
				}
			}
			request.setAttribute("indiGoalEmpIds", indiGoalEmpIds);
			if(sbEmpIds != null) {
				request.setAttribute("indiSbEmpIds", sbEmpIds.toString());
			}
			
			
			attributeList = getAttributeListElementwise(goalelement);
			getElementList(goalelement);
			
			checkGoalInProcessOrNot(con, uF, getGoalid());
			
			request.setAttribute("hmCorporate", hmCorporate);
			request.setAttribute("innerList", cinnerList);
			request.setAttribute("hmOrgName", hmOrgName);
//			System.out.println("hmOrgName ===> " + hmOrgName);

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
			request.setAttribute("processOrNotFlag", flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private Map<String, String> getAttributeMap(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmAttribute = new HashMap<String, String>();
		try {

			pst = con
					.prepareStatement("select * from appraisal_attribute where status=true");
			rs = pst.executeQuery();

			while (rs.next()) {
				hmAttribute.put(rs.getString("arribute_id"),
						rs.getString("attribute_name"));
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

//	public void getattribute() {
//
//		StringBuilder sb = new StringBuilder("");
//
//		for (int i = 0; attributeList != null && i < attributeList.size(); i++) {
//			FillAttribute fillAttribute = attributeList.get(i);
//
//			sb.append("<option value='" + fillAttribute.getId() + "'>"
//					+ fillAttribute.getName() + "</option>");
//
//		}
//
//		request.setAttribute("attribute", sb.toString());
//
//	}

	private void getEmpDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < empList.size(); i++) {
			FillEmployee fillEmployee = empList.get(i);

			sb.append("<option value='" + fillEmployee.getEmployeeId() + "'>"
					+ fillEmployee.getEmployeeCode() + "</option>");

		}
		request.setAttribute("empListOption", sb.toString());

	}

	private void getgradeDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < gradeList.size(); i++) {
			FillGrade fillGrade = gradeList.get(i);

			sb.append("<option value='" + fillGrade.getGradeId() + "'>"
					+ fillGrade.getGradeCode() + "</option>");

		}
		request.setAttribute("gradeListOption", sb.toString());

	}

	private void getLevelDetailsList() {
		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < levelList.size(); i++) {
			FillLevel fillLevel = levelList.get(i);

			sb.append("<option value='" + fillLevel.getLevelId() + "'>"
					+ fillLevel.getLevelCodeName() + "</option>");

		}
		request.setAttribute("levelListOption", sb.toString());
	}

	public void getOrientationDetailsList() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < orientationList.size(); i++) {
			FillOrientation fillOrientation = orientationList.get(i);

			sb.append("<option value=\"" + fillOrientation.getId() + "\">"
					+ fillOrientation.getName() + "</option>");

		}
		request.setAttribute("orientation", sb.toString());

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

	public String getIsMeasureKra() {
		return isMeasureKra;
	}

	public void setIsMeasureKra(String isMeasureKra) {
		this.isMeasureKra = isMeasureKra;
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

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
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

	public String getGoalCreaterId() {
		return goalCreaterId;
	}

	public void setGoalCreaterId(String goalCreaterId) {
		this.goalCreaterId = goalCreaterId;
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

	public List<FillManager> getManagerList() {
		return managerList;
	}

	public void setManagerList(List<FillManager> managerList) {
		this.managerList = managerList;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}

	public String getIsMeasureKraVal() {
		return isMeasureKraVal;
	}

	public void setIsMeasureKraVal(String isMeasureKraVal) {
		this.isMeasureKraVal = isMeasureKraVal;
	}

	public String getGoalelement() {
		return goalelement;
	}

	public void setGoalelement(String goalelement) {
		this.goalelement = goalelement;
	}

	public String getGoalParentID() {
		return goalParentID;
	}

	public void setGoalParentID(String goalParentID) {
		this.goalParentID = goalParentID;
	}

	public String getStrOrgVal() {
		return strOrgVal;
	}

	public void setStrOrgVal(String strOrgVal) {
		this.strOrgVal = strOrgVal;
	}

	public String getCgoalEffectDate() {
		return cgoalEffectDate;
	}

	public void setCgoalEffectDate(String cgoalEffectDate) {
		this.cgoalEffectDate = cgoalEffectDate;
	}

	public String getCgoalDueDate() {
		return cgoalDueDate;
	}

	public void setCgoalDueDate(String cgoalDueDate) {
		this.cgoalDueDate = cgoalDueDate;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
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


	public String getStrOrg() {
		return strOrg;
	}


	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}


	public String getGoalTitle() {
		return goalTitle;
	}


	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}


	public String getGoalCType() {
		return goalCType;
	}


	public void setGoalCType(String goalCType) {
		this.goalCType = goalCType;
	}


	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public String getCurrUserType() {
		return currUserType;
	}


	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	
}
