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
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class CreateMyReview implements ServletRequestAware, IStatements {
	CommonFunctions CF;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private List<FillLevel> levelList;
	private List<FillGrade> gradeList;
	private List<FillDesig> desigList;
	private List<FillAttribute> attributeList;
	private List<FillAnswerType> ansTypeList;
	private List<FillFrequency> frequencyList;
	private String ansTypeOption; 

	private String id;
	private String step;
	private String appraiselName;
	private String appraisal_description;
	private String oreinted;
	private String employee;
	private String finalizationName;
	private String strLevel;
	private String strDesignationUpdate;
	private String empGrade;
	private String strDepart;
	private String strWlocation;
	private String emp_status;

	private String userlocation;

	private String appraisalType;
	private String appraiseeList;

	private String frequency;
	private String from;
	private String to;

	private String startFrom;
	private String endTo;
	private String weekday;

	private List<FillEmployee> empList;
	private List<FillEmployee> finalizationList;
	private List<FillEmployee> reviewerList;
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillOrientation> orientationList;
	private List<FillOrganisation> organisationList;
	
	private String annualDay;
	private String annualMonth;
	private String day;
	private String monthday;
	private String month;
	
	private String main_level_id;
	private String strOrg;
	
	private String reviewerId;
	
	private String appraisal_instruction;
	
	private String policy_id;
	
	public String execute() {
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		UtilityFunctions uF = new UtilityFunctions();
		String submit = request.getParameter("submit");
		String saveandnew = request.getParameter("saveandnew");
		String submitandpublish = request.getParameter("submitandpublish");
		String saveandnewsystem=request.getParameter("saveandnewsystem");
		String cancel = request.getParameter("cancel");
		if(id==null) {
			initialize(uF);
		}

		if(cancel!= null && cancel.equals("Cancel")) {
			return "cancel";
		}
		System.out.println("Step Cnt======> "+step);
		if (id != null && step != null && !step.equals("1")) {
			String levelID=getSelfIDs(getId());			
			if (levelID != null && levelID.length()>0) {
				System.out.println("levelID in id != null :: "+levelID);
				attributeList = new FillAttribute(request).fillElementAttribute(levelID);
			}else{				
				attributeList = new FillAttribute(request).fillElementAttribute(null);
			}
			getattribute();
			ansTypeList = new FillAnswerType(request).fillAnswerType();
			String appraisalSystem = request.getParameter("appraisalSystem");
			getOrientationValue(uF.parseToInt(getOreinted()));

			if (saveandnewsystem != null && saveandnewsystem.equals("Save And Add New Subsection")) {
				insertInMainLevelDetails();
				if (appraisalSystem.equals("1")) {
					selectFunction();
					getSelectedOrientationPosition(uF);
				} else if (appraisalSystem.equals("2")) {
					addOtherQuestions();
					getSelectedOrientationPosition(uF);
				}else if (appraisalSystem.equals("3") || appraisalSystem.equals("4") || appraisalSystem.equals("5")){
					insertGoalKRATarget();
					getSelectedOrientationPosition(uF);
				}				
				
			} else if (appraisalSystem != null) {
				System.out.println("in else ");
				insertInMainLevelDetails();

				if (appraisalSystem.equals("1")) {
					selectFunction();
					getSelectedOrientationPosition(uF);
				} else if (appraisalSystem.equals("2")) { 
					addOtherQuestions();
					getSelectedOrientationPosition(uF);
				}else if (appraisalSystem.equals("3") || appraisalSystem.equals("4") || appraisalSystem.equals("5")){
					insertGoalKRATarget();
					getSelectedOrientationPosition(uF);
				}
				request.setAttribute("mainlevelTitle", null);
				request.setAttribute("mainshortDesrciption", null);
				request.setAttribute("mainlongDesrciption", null);
				request.setAttribute("attribname", null);
				request.setAttribute("attribid", null);
				request.setAttribute("sectionWeightage",null);
				setMain_level_id(null);
			}
			getOtherAnsType();
			getAppraisalQuestionList();
			getLevelDetails();
			request.setAttribute(PAGE, "/jsp/performance/CreateMyReview.jsp");
			request.setAttribute(TITLE, "Create My Review");
			if(submitandpublish!= null && submitandpublish.equals("Save And Publish")) {
				updateStatus();
				return "update";
			} else if (saveandnew != null && saveandnew.equals("Save And Add New Section")) {				
				return "success";
			} else if (saveandnewsystem != null && saveandnewsystem.equals("Save And Add New Subsection")) {
				return "success";
			} else {
				return "update";
			}
		} else if (appraiselName != null) {
			if (submit != null && submit.equals("Save") && step != null && step.equals("1")) {
				addAppraisal(uF);
				System.out.println("submit1 : "+submit);
				System.out.println("step1 : "+step);
			}
			getLevelDetails();
			
			request.setAttribute(PAGE, "/jsp/performance/CreateMyReview.jsp");
			request.setAttribute(TITLE, "Create My Review");
		}
//		else if(submit != null && submit.equals("Save") && step != null && step.equals("2")){
//			addTeamMember(uF);
//		
//		getOtherAnsType();
//		getLevelDetails();
//		getOrientationValue(uF.parseToInt(getOreinted()));
//		String levelID=getSelfIDs(getId());
//		if (levelID != null && levelID.length()>0){
//			attributeList = new FillAttribute().fillAttribute(levelID);
//		}else{				
//			attributeList = new FillAttribute().fillAttribute();
//		}
//		getattribute();
//		request.setAttribute(PAGE, "/jsp/performance/CreateMyReview.jsp");
//		request.setAttribute(TITLE, "Create My Review");
//		}
		if(getStep() == null) {
			setStep("1");
			getReviewWorkflowMember(uF);
		} else {
			int cnt = uF.parseToInt(getStep());
			cnt++;
			setStep(""+cnt);
		}
		request.setAttribute("id", id);
		getAppraisalQuestionList();
		request.setAttribute(PAGE, "/jsp/performance/CreateMyReview.jsp");
		request.setAttribute(TITLE, "Create My Review");
		return "success";
	}
	
	
	private void getReviewWorkflowMember(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		String policy_id=null;
		try {
			
			int nEmpID = uF.parseToInt(strSessionEmpId);
			
			con = db.makeConnection(con);
			
//			System.out.println("nEmpID=====> "+nEmpID);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId=hmEmpLevelMap.get(""+nEmpID);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(""+nEmpID);
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
//			System.out.println("empLevelId=====> "+empLevelId);
			
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_SELF_REVIEW+"' and level_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				policy_id=rs.getString("policy_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id) == 0){
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(locationID));
				rs = pst.executeQuery();
				while(rs.next()){
					policy_id=rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}
			
			if(uF.parseToInt(policy_id)>0){
//				System.out.println("policy_id=====> "+policy_id);
				
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
						" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
				rs=pst.executeQuery();
				Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
				while(rs.next()){
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
				
//				System.out.println("hmMemberMap==>"+hmMemberMap);
				Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
				boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
				
				
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()){
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1) {
						int memid=uF.parseToInt(innerList.get(1));
						
						switch(memid){
						
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, nEmpID);
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
								
								if(outerList!=null && !outerList.isEmpty()){
									StringBuilder sbComboBox=new StringBuilder();
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
									sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++){
										List<String> alList=outerList.get(i);
										sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox.append("</select>");								
									
									String optionTr="<tr><th style=\"text-align: right\">"+innerList.get(3)+":<sup>*</sup></th><td colspan=\"6\">"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select distinct(supervisor_emp_id) as supervisor_emp_id from employee_official_details where emp_id=? and supervisor_emp_id!=0) as a," +
										"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
										" and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, nEmpID);
								rs = pst.executeQuery();
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
								
								if(outerList11!=null && !outerList11.isEmpty()){
									StringBuilder sbComboBox11=new StringBuilder();
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
									sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++){
										List<String> alList=outerList11.get(i);
										sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox11.append("</select>");								
									
									String optionTr11="<tr><th style=\"text-align: right\">"+innerList.get(3)+":<sup>*</sup></th><td colspan=\"6\">"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and and epd.emp_per_id=eod.emp_id "
												+ " ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, nEmpID);
								rs = pst.executeQuery();
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
								
								if(outerList1!=null && !outerList1.isEmpty()){
									StringBuilder sbComboBox1=new StringBuilder();
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++){
										List<String> alList=outerList1.get(i);
										sbComboBox1.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox1.append("</select>");								
									
									String optionTr1="<tr><th style=\"text-align: right\">"+innerList.get(3)+":<sup>*</sup></th><td colspan=\"6\">"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " 
										+ " and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
							
								pst.setInt(1, nEmpID);
								rs = pst.executeQuery();
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
								
								if(outerList2!=null && !outerList2.isEmpty()){
									StringBuilder sbComboBox2=new StringBuilder();
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"4</option>");
									for(int i=0;i<outerList2.size();i++){
										List<String> alList=outerList2.get(i);
										sbComboBox2.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox2.append("</select>");								
									
									String optionTr2="<tr><th style=\"text-align: right\">"+innerList.get(3)+":<sup>*</sup></th><td colspan=\"6\">"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " 
										+ " and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
							
								pst.setInt(1, nEmpID);
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
								
								if(outerList3!=null && !outerList3.isEmpty()){
									StringBuilder sbComboBox3=new StringBuilder();
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++){
										List<String> alList=outerList3.get(i);
										sbComboBox3.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox3.append("</select>");								
									
									String optionTr3="<tr><th style=\"text-align: right\">"+innerList.get(3)+":<sup>*</sup></th><td colspan=\"6\">"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id "
										+ " and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, nEmpID);
								rs = pst.executeQuery();
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
								
								if(outerList4!=null && !outerList4.isEmpty()){
									StringBuilder sbComboBox4=new StringBuilder();
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++){
										List<String> alList=outerList4.get(i);
										sbComboBox4.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox4.append("</select>");								
									
									String optionTr4="<tr><th style=\"text-align: right\">"+innerList.get(3)+":<sup>*</sup></th><td colspan=\"6\">"+sbComboBox4.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr4);
								}
								break;
							
						case 7:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
									"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod," +
									"employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.emp_id=?)" +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' " +
									"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true " +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
									"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true");
								
								pst.setInt(1, nEmpID);
								pst.setInt(2, nEmpID);
								pst.setInt(3, nEmpID);
								pst.setInt(4, nEmpID);						
								rs = pst.executeQuery();
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
								
								if(outerList5!=null && !outerList5.isEmpty()){
									StringBuilder sbComboBox5=new StringBuilder();
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++){
										List<String> alList=outerList5.get(i);
										sbComboBox5.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox5.append("</select>");								
									
									String optionTr5="<tr><th style=\"text-align: right\">"+innerList.get(3)+":<sup>*</sup></th><td colspan=\"6\">"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;							
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
									"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
									"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, nEmpID);
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
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++){
									List<String> alList=outerHODList.get(i);
									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><th style=\"text-align: right\">"+innerList.get(3)+":<sup>*</sup></th><td>"+sbComboBox11.toString()+"</td></tr>";
								
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
						
						if(outerList!=null && !outerList.isEmpty()){
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"requiredClass form-control \">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++){
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><th style=\"text-align: right\">Your work flow:<sup>*</sup></th><td colspan=\"6\">"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
//				System.out.println("hmMemberOption ===>> " + hmMemberOption);
				
				request.setAttribute("hmMemberOption", hmMemberOption);
				request.setAttribute("policy_id", policy_id);
				/*request.setAttribute("divpopup",divpopup);
				request.setAttribute("loanD", sb.toString());
				request.setAttribute("strEmpID", nEmpID);*/
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void updateStatus() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String appraisalName = CF.getReviewNameById(con, uF, getId());
			boolean flag = true;
			pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type=?");
			pst.setInt(1, uF.parseToInt(id));
			pst.setString(2, WORK_FLOW_SELF_REVIEW);
//			System.out.println("CR pst1==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				flag = false;
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id = ?");
			pst.setBoolean(1, flag);
			pst.setInt(2, uF.parseToInt(id));
//			System.out.println("CR pst2==>"+pst);
			pst.executeUpdate();
			pst.close();

			
			pst = con.prepareStatement("update appraisal_details set is_publish=?, publish_request_to_workflow=true where appraisal_details_id = ?");
			pst.setBoolean(1, flag);
			pst.setInt(2, uF.parseToInt(id));
			pst.executeUpdate();
			pst.close();
//			System.out.println("CR pst3==>"+pst);
			if(!flag) {
				session.setAttribute("message", SUCCESSM+"Waiting for workflow approval to publish."+END);

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void insertInMainLevelDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;		
		UtilityFunctions uF=new UtilityFunctions();
		
		String levelTitle = request.getParameter("levelTitle");
		String shortDesrciption = request.getParameter("shortDesrciption");
		String longDesrciption = request.getParameter("longDesrciption");
		String attribute = request.getParameter("attribute");
		String sectionWeightage = request.getParameter("sectionWeightage");
	
		try {
			con = db.makeConnection(con);
			Map<String,String> orientationMemberMp=getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			if(getMain_level_id()==null || getMain_level_id().equals("") || getMain_level_id().equals("null")){
				pst = con.prepareStatement("insert into appraisal_main_level_details(level_title,short_description,long_description,appraisal_id," +
					"attribute_id,section_weightage,added_by,hr,manager,peer,self,subordinate,grouphead,vendor,client,entry_date,ceo,hod,other_peer) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setString(1, levelTitle);
				pst.setString(2, shortDesrciption);
				pst.setString(3, longDesrciption);
				pst.setInt(4, uF.parseToInt(id));
				pst.setInt(5, uF.parseToInt(attribute));
				pst.setString(6, sectionWeightage);
				pst.setInt(7, uF.parseToInt(strSessionEmpId));
				if (hmOrientMemberID.get("HR") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HR"))) != null) {
					pst.setInt(8,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HR")))));
				} else {
					pst.setInt(8, 0);
				}
				if (hmOrientMemberID.get("Manager") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Manager"))) != null) {
					pst.setInt(9, uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Manager")))));
				} else {
					pst.setInt(9, 0);
				}
				if (hmOrientMemberID.get("Peer") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Peer"))) != null) {
					pst.setInt(10,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Peer")))));
				} else {
					pst.setInt(10, 0);
				}
				if (hmOrientMemberID.get("Self") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Self"))) != null) {
					pst.setInt(11,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Self")))));
				} else {
					pst.setInt(11, 0);
				}
				if (hmOrientMemberID.get("Sub-ordinate") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Sub-ordinate"))) != null) {
					pst.setInt(12,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Sub-ordinate")))));
				} else {
					pst.setInt(12, 0);
				}
				if (hmOrientMemberID.get("GroupHead") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("GroupHead"))) != null) {
					pst.setInt(13,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("GroupHead")))));
				} else {
					pst.setInt(13, 0);
				}
				if (hmOrientMemberID.get("Vendor") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Vendor"))) != null) {
					pst.setInt(14,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Vendor")))));
				} else {
					pst.setInt(14, 0);
				}
				if (hmOrientMemberID.get("Client") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Client"))) != null) {
					pst.setInt(15,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Client")))));
				} else {
					pst.setInt(15, 0);
				}
				pst.setTimestamp(16, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				
				if (hmOrientMemberID.get("CEO") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("CEO"))) != null) {
					pst.setInt(17,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("CEO")))));
				} else {
					pst.setInt(17, 0);
				}
				
				if (hmOrientMemberID.get("HOD") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HOD"))) != null) {
					pst.setInt(18,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HOD")))));
				} else {
					pst.setInt(18, 0);
				}
				
				if (hmOrientMemberID.get("Other Peer") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Other Peer"))) != null) {
					pst.setInt(19,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Other Peer")))));
				} else {
					pst.setInt(19, 0);
				}
				
				pst.execute();
				pst.close();
				
				int main_level_id = 0;
				pst = con.prepareStatement("select max(main_level_id) from appraisal_main_level_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					main_level_id = rs.getInt(1);
				}
				rs.close();
				pst.close();
				
				setMain_level_id(""+main_level_id);
				request.setAttribute("mainlevelTitle", levelTitle);
				request.setAttribute("mainshortDesrciption", shortDesrciption);
				request.setAttribute("mainlongDesrciption", longDesrciption);
				Map<String, String> attributeMp = getAttributeMap();
				request.setAttribute("attribname",uF.showData(attributeMp.get(attribute), ""));
				request.setAttribute("attribid",attribute);
				request.setAttribute("sectionWeightage",sectionWeightage);
			}else{
				request.setAttribute("mainlevelTitle", levelTitle);
				request.setAttribute("mainshortDesrciption", shortDesrciption);
				request.setAttribute("mainlongDesrciption", longDesrciption);
				Map<String, String> attributeMp = getAttributeMap();
				request.setAttribute("attribname",uF.showData(attributeMp.get(attribute), ""));
				request.setAttribute("attribid",attribute);
				request.setAttribute("sectionWeightage",sectionWeightage);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getSelectedOrientationPosition(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> orientPosition = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id = ?");
			pst.setInt(1, uF.parseToInt(getMain_level_id()));
			rs = pst.executeQuery();
//			System.out.println("pst ;;;;;;;;;;;; "+pst);
			
			while (rs.next()) {
				orientPosition.put("HR", rs.getString("hr"));
				orientPosition.put("Manager", rs.getString("manager"));
				orientPosition.put("Self", rs.getString("self"));
				orientPosition.put("Peer", rs.getString("peer"));
				orientPosition.put("Client", rs.getString("client"));
				orientPosition.put("Sub-ordinate", rs.getString("subordinate"));
				orientPosition.put("GroupHead", rs.getString("grouphead"));
				orientPosition.put("Vendor", rs.getString("vendor"));
				orientPosition.put("CEO", rs.getString("ceo"));
				orientPosition.put("HOD", rs.getString("hod"));
				orientPosition.put("Other Peer", rs.getString("other_peer"));
			}
			rs.close();
			pst.close();
//			System.out.println("orientPosition ::: "+orientPosition);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("orientPosition",orientPosition);
	}
	
	
	public Map<String, String> getAttributeMap() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute ");
			rs = pst.executeQuery();
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
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
		return AppraisalQuestion;
	}
	
	private String getSelfIDs(String id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;		
		UtilityFunctions uF=new UtilityFunctions();
		
		String levelID=null;
		try {
			con = db.makeConnection(con);
			String empID=null;
			pst = con.prepareStatement("select self_ids from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs=pst.executeQuery();
			
			while(rs.next()){
				empID=rs.getString("self_ids");
			}
			rs.close();
			pst.close();
			
			if(empID!=null && !empID.equals("")){
				
				empID=empID.substring(1,empID.length()-1);
//				System.out.println("empID=====>"+empID);
				List<String> levellistID=new ArrayList<String>();
				pst = con.prepareStatement("select ld.level_id from level_details ld right join (select * from designation_details dd " +
						"right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd " +
						"where gd.grade_id=eod.grade_id and eod.emp_id in ("+empID+")) a on a.designationid=dd.designation_id)" +
						" a on a.level_id=ld.level_id");
//				System.out.println("pst=====>"+pst);
				rs=pst.executeQuery(); 
				
				while(rs.next()){
					levellistID.add(rs.getString(1)); 
				}
				rs.close();
				pst.close();
				
				Set<String> levelIdSet = new HashSet<String>(levellistID);
				Iterator<String> itr = levelIdSet.iterator();
				int i=0;
				while (itr.hasNext()) {
					String levelid = (String) itr.next();
					if(i==0){
						levelID=levelid;
					}else{
						levelID+=","+levelid;
					}
					i++;
				}
//				System.out.println("levelID=====>"+levelID);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return levelID;
	}

	
	private void insertGoalKRATarget() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;		
		UtilityFunctions uF=new UtilityFunctions();
		
		String systemName = request.getParameter("subsectionname");
		String systemDescription = request.getParameter("subsectionDescription");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

		try {
			con = db.makeConnection(con);
			Map<String,String> orientationMemberMp=getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?)");
			pst.setString(1, systemName); 
			pst.setString(2, systemDescription);
			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(9, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void initialize(UtilityFunctions uF ) {
		
		orientationList=new FillOrientation(request).fillOrientation();
		frequencyList=new FillFrequency(request).fillFrequency();
		levelList = new FillLevel(request).fillLevel();
		workList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		organisationList = new FillOrganisation(request).fillOrganisation();
		
		if (strLevel != null){
			desigList = new FillDesig(request).fillDesigFromLevel(strLevel);
		}else{
			desigList = new FillDesig(request).fillDesig();
		}
		if (strDesignationUpdate != null){
			gradeList = new FillGrade(request).fillGradeFromDesignation(strDesignationUpdate);
		}else{
			gradeList = new FillGrade(request).fillGrade();
		}
		userlocation = getManagerLocation();
		if (empGrade != null) {
			empList = new FillEmployee(request).fillEmployeeName(uF.parseToInt(empGrade));
		} else {
			empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
		}
		finalizationList = new FillEmployee(request).fillFinalizationNameByLocation(userlocation);
		reviewerList  = new FillEmployee(request).fillReviewerNameByLocation(null);
		String levelID=getSelfIDs(getId());
//		System.out.println("levelID :: "+levelID);
		if (levelID != null && levelID.length()>0){
//			System.out.println("levelID in :: "+levelID);
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		}else{				
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}
		getattribute();
		ansTypeList = new FillAnswerType(request).fillAnswerType();
	}
	
private void getOrientationValue(int id) {
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rs = null;
	
	try {
		StringBuilder sb=new StringBuilder();
		con = db.makeConnection(con);
		
		pst = con.prepareStatement("select member_name from orientation_details od,orientation_member om  where orientation_id=? and od.member_id=orientation_member_id");
		pst.setInt(1,id);
		rs=pst.executeQuery();
		int i=0;
		while(rs.next()){
			if(i==0)
			sb.append(rs.getString("member_name"));
			else
				sb.append(","+rs.getString("member_name"));
			i++;
		}
		rs.close();
		pst.close();
		
		request.setAttribute("member", sb.toString());
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	}


	public List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public List<FillOrientation> getOrientationList() {
		return orientationList;
	}

	public void setOrientationList(List<FillOrientation> orientationList) {
		this.orientationList = orientationList;
	}

	public void getLevelDetails() {
//		System.out.println("APP ID ===="+ id);
		MyReviewSummary report = new MyReviewSummary();
		report.setServletRequest(request);
		report.setId(id);
		report.execute();
	}

	private void getOtherAnsType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("appraisal_answer_type_id")) == 9) {
					sb.append("<option value=\"" + rs.getString("appraisal_answer_type_id") + "\" selected>"
							+ rs.getString("appraisal_answer_type_name") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("appraisal_answer_type_id") + "\">"
							+ rs.getString("appraisal_answer_type_name") + "</option>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("anstype", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void addOtherQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
		
			con = db.makeConnection(con);
	
			String subsectionName = request.getParameter("subsectionname");
			String subsectionDescription = request.getParameter("subsectionDescription");
			String subsectionLongDescription = request.getParameter("subsectionLongDescription");
			String subSectionWeightage = request.getParameter("subSectionWeightage");
			
			String attribute = request.getParameter("attribute");
			String appraisalSystem = request.getParameter("appraisalSystem");
			String scoreCard = request.getParameter("scoreCard");
	
			String otherQuestionType = request.getParameter("otherQuestionType");
			String checkWeightage = request.getParameter("checkWeightage");
	
			String[] otherSDescription = request.getParameterValues("otherSDescription");
			String[] orientt = request.getParameterValues("orientt");
	
			//String[] questionSelect = request.getParameterValues("questionSelect");
			//String[] hidequeid = request.getParameterValues("hidequeid");
			String[] weightage = request.getParameterValues("weightage");
			String[] question = request.getParameterValues("question");
			String[] addFlag = request.getParameterValues("status");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			String[] optione = request.getParameterValues("optione");
			String[] rateoptiona = request.getParameterValues("rateoptiona");
			String[] rateoptionb = request.getParameterValues("rateoptionb");
			String[] rateoptionc = request.getParameterValues("rateoptionc");
			String[] rateoptiond = request.getParameterValues("rateoptiond");
			String[] rateoptione = request.getParameterValues("rateoptione");
			
			Map<String,String> orientationMemberMp=getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
						
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionName, ""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage,"100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_other_question_type_details(other_question_type,is_weightage,"
				+ "appraisal_id,level_id)values(?,?,?,?)");
			pst.setString(1, otherQuestionType);
			pst.setBoolean(2, uF.parseToBoolean(checkWeightage));
			pst.setInt(3, uF.parseToInt(id));
			pst.setInt(4, appraisal_level_id);
			pst.execute();
			pst.close();
			
			int other_question_type_id = 0;
			pst = con.prepareStatement("select max(othe_question_type_id) from appraisal_other_question_type_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				other_question_type_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			for (int i = 0; question != null && i < question.length; i++) {
				String ansType = request.getParameter("ansType");
//				int question_id = uF.parseToInt(hidequeid[i]);
				int question_id = 0;
//				if (uF.parseToInt(hidequeid[i]) == 0) {

					String[] correct = request.getParameterValues("correct" + orientt[i]);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question[i]);
					pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
					pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
					pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
					pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
					pst.setString(6, (optione != null && optione.length > i ? optione[i]: ""));
					pst.setInt(7, (rateoptiona != null && rateoptiona.length > i ? uF.parseToInt(rateoptiona[i]): 0));
					pst.setInt(8, (rateoptionb != null && rateoptionb.length > i ? uF.parseToInt(rateoptionb[i]): 0));
					pst.setInt(9, (rateoptionc != null && rateoptionc.length > i ? uF.parseToInt(rateoptionc[i]): 0));
					pst.setInt(10, (rateoptiond != null && rateoptiond.length > i ? uF.parseToInt(rateoptiond[i]): 0));
					pst.setInt(11, (rateoptione != null && rateoptione.length > i ? uF.parseToInt(rateoptione[i]): 0));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question[i]);
					pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
					pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
					pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
					pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();

					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();

//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,other_id,attribute_id,weightage,appraisal_id," +
					"other_short_description,appraisal_level_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, other_question_type_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage[i]));
				pst.setInt(5, uF.parseToInt(id));
				pst.setString(6, (otherSDescription!= null && otherSDescription.length > i) ? otherSDescription[i] : "");
				pst.setInt(7, appraisal_level_id);
				pst.setInt(8, uF.parseToInt(ansType));
				pst.execute();
				pst.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void selectFunction() {
		UtilityFunctions uF = new UtilityFunctions();

		int scoreCard = uF.parseToInt((String) request.getParameter("scoreCard"));
		if (scoreCard == 1) {
			insertDatawithGoalObjective();
		} else if (scoreCard == 2) {
			insertData();
		} else if (scoreCard == 3) {
			insertDatawithGoal();
		}

	}

	public void insertDatawithGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] scoreSectionName = request.getParameterValues("scoreSectionName");
			String[] scoreCardDescription = request.getParameterValues("scoreCardDescription");
			String[] scoreCardWeightage = request.getParameterValues("scoreCardWeightage");
	
			String[] goalSectionName = request.getParameterValues("goalSectionName");
			String[] goalDescription = request.getParameterValues("goalDescription");
			String[] goalWeightage = request.getParameterValues("goalWeightage");
	
			String[] measuresSectionName = request.getParameterValues("measuresSectionName");
			String[] measuresDescription = request.getParameterValues("measuresDescription");
			String[] measureWeightage=request.getParameterValues("measureWeightage");
	
			//String[] hidequeid = request.getParameterValues("hidequeid");
			String[] weightage = request.getParameterValues("weightage");
			String[] measurecount = request.getParameterValues("measurecount");
			String[] questioncount = request.getParameterValues("questioncount");
			String[] goalcount = request.getParameterValues("goalcount");
			String[] question = request.getParameterValues("question");
	
			String subsectionname = request.getParameter("subsectionname");
			String subsectionDescription = request.getParameter("subsectionDescription");
			String subsectionLongDescription = request.getParameter("subsectionLongDescription");
			String subSectionWeightage = request.getParameter("subSectionWeightage");
			
			//String longDesrciption = request.getParameter("longDesrciption");
			String attribute = request.getParameter("attribute");
			String appraisalSystem = request.getParameter("appraisalSystem");
			String scoreCard = request.getParameter("scoreCard");
	
			String[] addFlag = request.getParameterValues("status");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			String[] optione = request.getParameterValues("optione");
			String[] rateoptiona = request.getParameterValues("rateoptiona");
			String[] rateoptionb = request.getParameterValues("rateoptionb");
			String[] rateoptionc = request.getParameterValues("rateoptionc");
			String[] rateoptiond = request.getParameterValues("rateoptiond");
			String[] rateoptione = request.getParameterValues("rateoptione");
			String[] orientt=request.getParameterValues("orientt");
			con = db.makeConnection(con);
			Map<String,String> orientationMemberMp=getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
	
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionname,""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage,"100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int questionserial = 0;
			int measureserial = 0;
			int goalserial = 0;
			for (int i = 0; i < scoreSectionName.length; i++) {
				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
								+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute));
				pst.execute();
				pst.close();
				
				int scorecard_id = 0;
				pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					scorecard_id = rst.getInt(1);
				}
				rst.close();
				pst.close();

				int goal = uF.parseToInt(goalcount[i]);
				for (int j = 0; j < goal; j++) {

					pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
									+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
					pst.setString(1, goalSectionName[goalserial]);
					pst.setString(2, goalDescription[goalserial]);
					pst.setString(3, goalWeightage[goalserial]);
					pst.setInt(4, scorecard_id);
					pst.setInt(5, uF.parseToInt(id));
					pst.execute();
					pst.close();
					
					int goal_id = 0;
					pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
					rst = pst.executeQuery();
					while (rst.next()) {
						goal_id = rst.getInt(1);
					}
					rst.close();
					pst.close();

					int measure = uF.parseToInt(measurecount[goalserial]);
					goalserial++;
					for (int k = 0; k < measure; k++) {
						pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
										+ "values(?,?,?,?,?)");
						pst.setString(1, measuresSectionName[measureserial]);
						pst.setString(2, measuresDescription[measureserial]);
						pst.setInt(3, goal_id);
						pst.setInt(4, uF.parseToInt(id));
						pst.setString(5,measureWeightage[measureserial]);
						pst.execute();
						pst.close();
						
						int measure_id = 0;
						pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
						rst = pst.executeQuery();
						while (rst.next()) {
							measure_id = rst.getInt(1);
						}
						rst.close();
						pst.close();

						int questioncnt = uF.parseToInt(questioncount[measureserial]);
						measureserial++;
						for (int l = 0; l < questioncnt; l++) {
							String ansType = request.getParameter("ansType");
//							int question_id = uF.parseToInt(hidequeid[questionserial]);
							int question_id = 0;
							
							if (question[questionserial].length() > 0) {
//								if (uF.parseToInt(hidequeid[questionserial]) == 0) {
									String[] correct = request.getParameterValues("correct"+ orientt[questionserial]);
									StringBuilder option = new StringBuilder();

									for (int ab = 0; correct != null && ab < correct.length; ab++) {
										option.append(correct[ab] + ",");
									}

									pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
										"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
										"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
									pst.setString(1, question[questionserial]);
									pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
									pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
									pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
									pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
									pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
									pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
									pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
									pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
									pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
									pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
									pst.setString(12, option.toString());
									pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
									pst.setInt(14, uF.parseToInt(ansType));
									pst.executeUpdate();
									pst.close();
									
									/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
									pst.setString(1, question[questionserial]);
									pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
									pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
									pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
									pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
									pst.setString(6, option.toString());
									pst.setBoolean(7,uF.parseToBoolean(addFlag[i]));
									pst.setInt(8, uF.parseToInt(ansType));
									pst.execute();
									pst.close();*/
									
									pst = con.prepareStatement("select max(question_bank_id) from question_bank");
									rst = pst.executeQuery();
									while (rst.next()) {
										question_id = rst.getInt(1);
									}
									rst.close();
									pst.close();
//								}

								pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
									"appraisal_id,appraisal_level_id,scorecard_id,answer_type)values(?,?,?,?, ?,?,?,?)");
								pst.setInt(1, question_id);
								pst.setInt(2, measure_id);
								pst.setInt(3, uF.parseToInt(attribute));
								pst.setDouble(4,uF.parseToDouble(weightage[questionserial]));
								pst.setInt(5, uF.parseToInt(id));
								pst.setInt(14,appraisal_level_id);
								pst.setInt(15,scorecard_id);
								pst.setInt(16,uF.parseToInt(ansType));
								pst.execute();
								pst.close();
							}
							questionserial++;
						}
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private Map<String,String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> orientationMemberMp=new HashMap<String,String>();
	
		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs=pst.executeQuery();
			while(rs.next()){
				orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
		}

	
	public void insertDatawithGoalObjective() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] scoreSectionName = request.getParameterValues("scoreSectionName");
			String[] scoreCardDescription = request.getParameterValues("scoreCardDescription");
			String[] scoreCardWeightage = request.getParameterValues("scoreCardWeightage");
	
			String[] goalSectionName = request.getParameterValues("goalSectionName");
			String[] goalDescription = request.getParameterValues("goalDescription");
			String[] goalWeightage = request.getParameterValues("goalWeightage");
	
			String[] objectiveSectionName = request.getParameterValues("objectiveSectionName");
			String[] objectiveDescription = request.getParameterValues("objectiveDescription");
			String[] objectiveWeightage = request.getParameterValues("objectiveWeightage");
	
			String[] measuresSectionName = request.getParameterValues("measuresSectionName");
			String[] measuresDescription = request.getParameterValues("measuresDescription");
			String[] measureWeightage=request.getParameterValues("measureWeightage");
	
			//String[] hidequeid = request.getParameterValues("hidequeid");
			String[] weightage = request.getParameterValues("weightage");
			String[] measurecount = request.getParameterValues("measurecount");
			String[] questioncount = request.getParameterValues("questioncount");
			String[] goalcount = request.getParameterValues("goalcount");
			String[] objectivecount = request.getParameterValues("objectivecount");
			String[] question = request.getParameterValues("question");
	
			String subsectionname = request.getParameter("subsectionname");
			String subsectionDescription = request.getParameter("subsectionDescription");
			String subsectionLongDescription = request.getParameter("subsectionLongDescription");
			String subSectionWeightage = request.getParameter("subSectionWeightage");
			
			String attribute = request.getParameter("attribute");
			String appraisalSystem = request.getParameter("appraisalSystem");
			String scoreCard = request.getParameter("scoreCard");
	
			String[] addFlag = request.getParameterValues("status");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			String[] optione = request.getParameterValues("optione");
			String[] rateoptiona = request.getParameterValues("rateoptiona");
			String[] rateoptionb = request.getParameterValues("rateoptionb");
			String[] rateoptionc = request.getParameterValues("rateoptionc");
			String[] rateoptiond = request.getParameterValues("rateoptiond");
			String[] rateoptione = request.getParameterValues("rateoptione");
			String[] orientt = request.getParameterValues("orientt");
			
			
			con = db.makeConnection(con);
			Map<String,String> orientationMemberMp=getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionname,""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage, "100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();

			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int questionserial = 0;
			int measureserial = 0;
			int goalserial = 0;
			int objectiveserial = 0;
			for (int i = 0; i < scoreSectionName.length; i++) {

				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
					+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute));
				pst.execute();
				pst.close();
				
				int scorecard_id = 0;
				pst = con
						.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
				rst = pst.executeQuery();

				while (rst.next()) {
					scorecard_id = rst.getInt(1);
				}
				rst.close();
				pst.close();
				
				int goal = uF.parseToInt(goalcount[i]);
				for (int j = 0; j < goal; j++) {

					pst = con
							.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
									+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
					pst.setString(1, goalSectionName[goalserial]);
					pst.setString(2, goalDescription[goalserial]);
					pst.setString(3, goalWeightage[goalserial]);
					pst.setInt(4, scorecard_id);
					pst.setInt(5, uF.parseToInt(id));
					pst.execute();
					pst.close();
					
					int goal_id = 0;
					pst = con
							.prepareStatement("select max(goal_id) from appraisal_goal_details");
					rst = pst.executeQuery();

					while (rst.next()) {
						goal_id = rst.getInt(1);
					}
					rst.close();
					pst.close();

					int objective = uF.parseToInt(objectivecount[goalserial]);
					goalserial++;
					for (int m = 0; m < objective; m++) {

						pst = con
								.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
										+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
						pst.setString(1, objectiveSectionName[objectiveserial]);
						pst.setString(2, objectiveDescription[objectiveserial]);
						pst.setString(3, objectiveWeightage[objectiveserial]);
						pst.setInt(4, goal_id);
						pst.setInt(5, uF.parseToInt(id));
						pst.execute();
						pst.close();
						
						int objective_id = 0;
						pst = con
								.prepareStatement("select max(objective_id) from appraisal_objective_details");
						rst = pst.executeQuery();

						while (rst.next()) {
							objective_id = rst.getInt(1);
						}
						rst.close();
						pst.close();
						
						int measure = uF.parseToInt(measurecount[objectiveserial]);
						objectiveserial++;
						for (int k = 0; k < measure; k++) {
							pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
											+ "values(?,?,?,?,?)");
							pst.setString(1, measuresSectionName[measureserial]);
							pst.setString(2, measuresDescription[measureserial]);
							pst.setInt(3, objective_id);
							pst.setInt(4, uF.parseToInt(id));
							pst.setString(5,measureWeightage[measureserial]);
							pst.execute();
							pst.close();
							
							int measure_id = 0;
							pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
							rst = pst.executeQuery();
							while (rst.next()) {
								measure_id = rst.getInt(1);
							}
							rst.close();
							pst.close();
							
							int questioncnt = uF.parseToInt(questioncount[measureserial]);
							measureserial++;
							for (int l = 0; l < questioncnt; l++) {
								String ansType = request.getParameter("ansType");
//								int question_id = uF.parseToInt(hidequeid[questionserial]);
								int question_id = 0;
								if (question[questionserial].length() > 0) {
//									if (uF.parseToInt(hidequeid[questionserial]) == 0) {
										String[] correct = request.getParameterValues("correct" + orientt[questionserial]);
										StringBuilder option = new StringBuilder();

										for (int ab = 0; correct != null && ab < correct.length; ab++) {
											option.append(correct[ab] + ",");
										}

										pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
											"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
											"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
										pst.setString(1, question[questionserial]);
										pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
										pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
										pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
										pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
										pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
										pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
										pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
										pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
										pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
										pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
										pst.setString(12, option.toString());
										pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
										pst.setInt(14, uF.parseToInt(ansType));
										pst.executeUpdate();
										pst.close();
										
										/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
										pst.setString(1,question[questionserial]);
										pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
										pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
										pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
										pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
										pst.setString(6, option.toString());
										pst.setBoolean(7,uF.parseToBoolean(addFlag[i]));
										pst.setInt(8, uF.parseToInt(ansType));
										pst.execute();
										pst.close();*/
										
										pst = con.prepareStatement("select max(question_bank_id) from question_bank");
										rst = pst.executeQuery();
										while (rst.next()) {
											question_id = rst.getInt(1);
										}
										rst.close();
										pst.close();
//									}

									pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
										"appraisal_id,appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?,?,?,?,?)");
									pst.setInt(1, question_id);
									pst.setInt(2, measure_id);
									pst.setInt(3, uF.parseToInt(attribute));
									pst.setDouble(4,uF.parseToDouble(weightage[questionserial]));
									pst.setInt(5, uF.parseToInt(id));
									pst.setInt(6, appraisal_level_id);
									pst.setInt(7, scorecard_id);
									pst.setInt(8, uF.parseToInt(ansType));
									pst.execute();
									pst.close();
								}
								questionserial++;
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void insertData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
				
			String subsectionname = request.getParameter("subsectionname");
			String subsectionDescription = request.getParameter("subsectionDescription");
			String subsectionLongDescription = request.getParameter("subsectionLongDescription");
			String subSectionWeightage = request.getParameter("subSectionWeightage");
			
			String attribute = request.getParameter("attribute");
			String appraisalSystem = request.getParameter("appraisalSystem");
			String scoreCard = request.getParameter("scoreCard");
	
			String[] scoreSectionName = request.getParameterValues("scoreSectionName");
			String[] scoreCardDescription = request.getParameterValues("scoreCardDescription");
			String[] scoreCardWeightage = request.getParameterValues("scoreCardWeightage");
	
			String[] measuresSectionName = request.getParameterValues("measuresSectionName");
			String[] measuresDescription = request.getParameterValues("measuresDescription");
			String[] measureWeightage=request.getParameterValues("measureWeightage");
	
			//String[] hidequeid = request.getParameterValues("hidequeid");
			String[] weightage = request.getParameterValues("weightage");
			String[] measurecount = request.getParameterValues("measurecount");
			String[] questioncount = request.getParameterValues("questioncount");
			String[] question = request.getParameterValues("question");
	
			String[] addFlag = request.getParameterValues("status");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			String[] optione = request.getParameterValues("optione");
			String[] rateoptiona = request.getParameterValues("rateoptiona");
			String[] rateoptionb = request.getParameterValues("rateoptionb");
			String[] rateoptionc = request.getParameterValues("rateoptionc");
			String[] rateoptiond = request.getParameterValues("rateoptiond");
			String[] rateoptione = request.getParameterValues("rateoptione");
			String[] orientt = request.getParameterValues("orientt");
			
			con = db.makeConnection(con);
			Map<String,String> orientationMemberMp=getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionname,""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage,"100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int questionserial = 0;
			int measureserial = 0;

			for (int i = 0; i < scoreSectionName.length; i++) {

				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
								+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute));
				pst.execute();
				pst.close();
				
				int scorecard_id = 0;
				pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					scorecard_id = rst.getInt(1);
				}
				rst.close();
				pst.close();

				int measure = uF.parseToInt(measurecount[i]);
				for (int k = 0; k < measure; k++) {
					pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,scorecard_id,appraisal_id,weightage)"
									+ "values(?,?,?,?,?)");
					pst.setString(1, measuresSectionName[measureserial]);
					pst.setString(2, measuresDescription[measureserial]);
					pst.setInt(3, scorecard_id);
					pst.setInt(4, uF.parseToInt(id));
					pst.setString(5,measureWeightage[measureserial]);
					pst.execute();
					pst.close();
					
					int measure_id = 0;
					pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
					rst = pst.executeQuery();
					while (rst.next()) {
						measure_id = rst.getInt(1);
					}
					rst.close();
					pst.close();

					int questioncnt = uF.parseToInt(questioncount[measureserial]);
					measureserial++;
					for (int l = 0; l < questioncnt; l++) {
//						int question_id = uF.parseToInt(hidequeid[questionserial]);
						int question_id = 0;
						String ansType = request.getParameter("ansType");
						
						if (question[questionserial].length() > 0) {
//							if (uF.parseToInt(hidequeid[questionserial]) == 0) {
								String[] correct = request.getParameterValues("correct"+ orientt[questionserial]);
								StringBuilder option = new StringBuilder();

								for (int ab = 0; correct != null && ab < correct.length; ab++) {
									option.append(correct[ab] + ",");
								}

								pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
									"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
								pst.setString(1, question[questionserial]);
								pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
								pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
								pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
								pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
								pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
								pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
								pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
								pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
								pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
								pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
								pst.setString(12, option.toString());
								pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
								pst.setInt(14, uF.parseToInt(ansType));
								pst.executeUpdate();
								pst.close();
								
								/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
								pst.setString(1, question[questionserial]);
								pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
								pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
								pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
								pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
								pst.setString(6, option.toString());
								pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
								pst.setInt(8, uF.parseToInt(ansType));
								pst.execute();
								pst.close();*/
								
								pst = con.prepareStatement("select max(question_bank_id) from question_bank");
								rst = pst.executeQuery();
								while (rst.next()) {
									question_id = rst.getInt(1);
								}
								rst.close();
								pst.close();
//							}

							pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
								"appraisal_id,appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
							pst.setInt(1, question_id);
							pst.setInt(2, measure_id);
							pst.setInt(3, uF.parseToInt(attribute));
							pst.setDouble(4, uF.parseToDouble(weightage[questionserial]));
							pst.setInt(5, uF.parseToInt(id));
							pst.setInt(6, appraisal_level_id);
							pst.setInt(7, scorecard_id);
							pst.setInt(8, uF.parseToInt(ansType));
							pst.execute();
							pst.close();
							}
						questionserial++;
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();

//			System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
	
	public String getEmployeeList1(String existIds, String self, int type) {
		StringBuilder sb = null; 
		List<String> empList = new ArrayList<String>();
		if(existIds != null) {
			List<String> empList1 = Arrays.asList(existIds.split(","));
			for(int i=0; empList1 != null && !empList1.isEmpty() && i<empList1.size(); i++) {
				if(!empList1.get(i).trim().equals("") && !empList.contains(empList1.get(i).trim())) {
					empList.add(empList1.get(i).trim());
					if(sb == null) {
						sb = new StringBuilder();
						sb.append(","+empList1.get(i).trim()+",");
					} else {
						sb.append(empList1.get(i).trim()+",");
					}
				}
			}
		}
		if(sb == null) {
			sb = new StringBuilder();
		}
		return sb.toString();
	}

	public String getEmployeeList(String self,int type){
		StringBuilder sb=new StringBuilder();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			if(self.length()>1){
			 self=self.substring(1,self.length()-1);
			}
//			System.out.println("self=====>"+self);
//			System.out.println("type=====>"+type);
			
			if (type == 2) {

				pst = con.prepareStatement("select supervisor_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in ("+ self+ ") and supervisor_emp_id!=0");
				rs = pst.executeQuery();
				
				int cnt=0;
				while(rs.next()){
					if(rs.getString("supervisor_emp_id") != null && rs.getInt("supervisor_emp_id")>0) {
						if(cnt==0){
							sb.append(","+rs.getString("supervisor_emp_id").trim()+",");
						}else{
							sb.append(rs.getString("supervisor_emp_id").trim()+",");
						}
						cnt++;
					}
				}
				rs.close();
				pst.close();
				return sb.toString();
		
			} else if (type == 3) {

			} else if (type == 4) {
				pst=con.prepareStatement("select grade_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by grade_id");
				rs=pst.executeQuery();
				StringBuilder sb4=new StringBuilder();
				int cnt=0;
				while(rs.next()){
					if(rs.getString("grade_id") != null &&  rs.getInt("grade_id")>0) {
						if(cnt==0){
							sb4.append(rs.getString("grade_id").trim());
						}else{
							sb4.append(","+rs.getString("grade_id").trim());
						}
	//					System.out.println("sb4=====>"+sb4.toString());
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
				rs=pst.executeQuery();
				StringBuilder sb5=new StringBuilder();
				 cnt=0;
				while(rs.next()){
					if(rs.getString("wlocation_id") != null && rs.getInt("wlocation_id") > 0) {
						if(cnt==0){
							sb5.append(","+rs.getString("wlocation_id").trim()+",");
						}else{
							sb5.append(rs.getString("wlocation_id").trim()+",");
						}
	//					System.out.println("sb5=====>"+sb5.toString());
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
				//String strsb4 = (sb4 != null ? sb4.toString().substring(1, sb4.toString().length()-1) : "");
				pst=con.prepareStatement("select emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and wlocation_id in("+strsb5+") and grade_id in("+sb4.toString()+") group by emp_id");
				rs=pst.executeQuery();
				cnt=0;
				while(rs.next()){
					if(rs.getString("emp_id") != null && rs.getInt("emp_id")>0) {
						if(cnt==0){
							sb.append(","+rs.getString("emp_id").trim()+",");
						}else{
							sb.append(rs.getString("emp_id").trim()+",");
						}
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				return sb.toString();

			} else if (type == 5) {
				pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
				rs=pst.executeQuery();
				StringBuilder sb5=new StringBuilder();
				int cnt=0;
				while(rs.next()){
					if(rs.getString("wlocation_id") != null && rs.getInt("wlocation_id") > 0) {
						if(cnt==0){
							sb5.append(rs.getString("wlocation_id").trim());
						}else{
							sb5.append(","+rs.getString("wlocation_id").trim());
	
						}
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				//String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
				pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=? and wlocation_id in("+sb5.toString()+")");
				pst.setInt(1, 5);
				rs=pst.executeQuery();
				while(rs.next()) {
					if(rs.getString("emp_per_id") != null && rs.getInt("emp_per_id")>0) {
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(","+rs.getString("emp_per_id").trim()+",");
						} else {
							sb.append(rs.getString("emp_per_id").trim()+",");
						}
				//	empList.add(rs.getString("emp_per_id").trim());	
					}
				}
				rs.close();
				pst.close();
				
				if(sb == null) {
					sb = new StringBuilder();
				}
				return sb.toString();
			} else if (type == 6) {

			} else if (type == 7) {
				pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
				rs=pst.executeQuery();
				StringBuilder sb5=new StringBuilder();
				int cnt=0;
				while(rs.next()){
					if(rs.getString("wlocation_id") != null && rs.getInt("wlocation_id") > 0) {
						if(cnt==0){
							sb5.append(rs.getString("wlocation_id").trim());
						}else{
							sb5.append(","+rs.getString("wlocation_id").trim());
	
						}
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				//String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
				pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=? and wlocation_id in("+sb5.toString()+")");
				pst.setInt(1, 7);
				rs=pst.executeQuery();
				cnt=0;
				while(rs.next()){
					if(rs.getString("emp_id") != null && rs.getInt("emp_id")>0) {
						if(cnt==0){
							sb.append(","+rs.getString("emp_id").trim()+",");
						}else{
							sb.append(rs.getString("emp_id").trim()+",");
						}
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				return sb.toString();
			} else if (type == 8) {

			} else if (type == 9) {
				
			} else if (type == 13) {
				
				pst=con.prepareStatement("select hod_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id=eod.emp_id "
						+"  and is_alive = true and hod_emp_id > 0  and eod.emp_id in("+self+")");
				
				rs=pst.executeQuery();
				while(rs.next()) {
					if(rs.getString("hod_emp_id") != null && rs.getInt("hod_emp_id")>0) {
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(","+rs.getString("hod_emp_id").trim()+",");
						} else {
							sb.append(rs.getString("hod_emp_id").trim()+",");
						}
					
					}
				}
				rs.close();
				pst.close();
				
				if(sb == null) {
					sb = new StringBuilder();
				}
				return sb.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return null;
	}
	

	private String getManagerLocation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";

		con = db.makeConnection(con);
		try {
			pst = con
					.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
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

	

	public void addAppraisal(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			List<String> selfList = getSelfList();
			
			StringBuilder sb2 = new StringBuilder();
			for (int i = 0; i < selfList.size(); i++) {
				if (i == 0) {
					sb2.append("," + selfList.get(i).trim()+",");
					
				} else {
					sb2.append(selfList.get(i).trim()+",");
				}
			}
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			appraiseeList = uF.showData(CF.getAppendData(con,selfList, hmEmpName), "");
					
			//weekday annualDay annualMonth day monthday month
			String appraisal_day=null;
			String appraisal_month=null;
			String weeklyDay=null;
		/*	if(frequency!=null && frequency.equals("2")){ 
				weeklyDay=weekday;
				appraisal_day=null;
				appraisal_month=null;
			}else if(frequency!=null && frequency.equals("3")){
				weeklyDay=null;
				appraisal_day=day;
				appraisal_month=null;
			}else if(frequency!=null && frequency.equals("4")){
				weeklyDay=null;
				appraisal_day=monthday;
				appraisal_month=month;
			}else if(frequency!=null && frequency.equals("5")){
				weeklyDay=null;
				appraisal_day=monthday;
				appraisal_month=month;
			}else if(frequency!=null && frequency.equals("6")){
				weeklyDay=null;
				appraisal_day=annualDay;
				appraisal_month=annualMonth;
			}*/
			
			setFrequency("1");
//			System.out.println("create self review frequency ==>"+getFrequency());
			List<String> memberList=CF.getOrientationMemberDetails(con,uF.parseToInt(getOreinted()));
			StringBuilder members=new StringBuilder();
			for(int i=0;i<memberList.size();i++){
				if(i==0)
					members.append(memberList.get(i));
				else
					members.append(","+memberList.get(i));
			}
			
			String hrIds = request.getParameter("hidehrId");
			String managerIds = request.getParameter("hidemanagerId");
			String peerIds = request.getParameter("hidepeerId");
			String otherIds = request.getParameter("hideotherId");
			String ceoIds = request.getParameter("hideCeoId");
			String hodIds = request.getParameter("hideHodId");
			
			StringBuilder sbReviewers = null;
			if (getReviewerId() != null && getReviewerId().length() > 0) {
				List<String> alReviewers = Arrays.asList(getReviewerId().split(","));
				for(int i=0; alReviewers!=null && !alReviewers.isEmpty() && i<alReviewers.size();i++) {
					if(sbReviewers == null) {
						sbReviewers = new StringBuilder();
						sbReviewers.append("," + alReviewers.get(i).trim()+",");
					} else {
						sbReviewers.append(alReviewers.get(i).trim()+",");
					}
				}
			}
			if(sbReviewers == null) {
				sbReviewers = new StringBuilder();
			}
			
			pst = con.prepareStatement("insert into appraisal_details(appraisal_name,appraisal_type,added_by,entry_date"
							+ ",frequency,from_date,to_date,appraisal_day,appraisal_month,weekday,appraisal_description,"
							+ "is_publish,appraisal_instruction, oriented_type,employee_id,supervisor_id,peer_ids,self_ids," 
							+ "emp_status,hr_ids,usertype_member,my_review_status,other_ids,ceo_ids,hod_ids,reviewer_id)"
							+ "values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			pst.setString(1, getAppraiselName());
			pst.setString(2, getAppraisalType());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, "1");
			pst.setDate(6, uF.getDateFormat(getFrom(), DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(getTo(), DATE_FORMAT));
			pst.setString(8, appraisal_day);
			pst.setString(9, appraisal_month);
			pst.setString(10, weeklyDay);
			pst.setString(11, getAppraisal_description());
			pst.setBoolean(12,false);
			pst.setString(13, getAppraisal_instruction());

			pst.setString(14, getOreinted());
			
			if(hmOrientMemberID.get("Self") != null && memberList.contains(hmOrientMemberID.get("Self"))) {
				pst.setString(15, ","+ strSessionEmpId +",");
			} else {
				pst.setString(15, "");
			}
			
//			pst.setString(15, ","+ strSessionEmpId +",");
			if(managerIds != null && !managerIds.equals("")){
				pst.setString(16, managerIds);
			}else{
				if (hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager"))) {
					pst.setString(16, getEmployeeList(","+ strSessionEmpId +",",uF.parseToInt(hmOrientMemberID.get("Manager"))));
				} else {
					pst.setString(16, "");
				}
			}
			if(peerIds != null && !peerIds.equals("")){
				pst.setString(17, peerIds);
			}else{
				if(hmOrientMemberID.get("Peer") != null && memberList.contains(hmOrientMemberID.get("Peer"))) {
					pst.setString(17, getEmployeeList(","+ strSessionEmpId +",",uF.parseToInt(hmOrientMemberID.get("Peer"))));
				} else {
					pst.setString(17, "");
				}
			}
			pst.setString(18, ","+ strSessionEmpId +",");
			/*if(hmOrientMemberID.get("Self") != null && memberList.contains(hmOrientMemberID.get("Self"))) {
				pst.setString(18, ","+ strSessionEmpId +",");
			} else {
				pst.setString(18, "");
			}*/
			
			pst.setString(19, getEmp_status());
			if(hrIds != null && !hrIds.equals("")){
				pst.setString(20, hrIds);
			}else{
				if(hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR"))) {
					pst.setString(20, getEmployeeList(","+ strSessionEmpId +",",uF.parseToInt(hmOrientMemberID.get("HR"))));
				} else {
					pst.setString(20, "");
				}
			}
			pst.setString(21,members.toString());
			pst.setInt(22, 1);
			if(otherIds != null && !otherIds.equals("")){
				pst.setString(23, otherIds);
			}else{
				if(hmOrientMemberID.get("Anyone") != null && memberList.contains(hmOrientMemberID.get("Anyone"))) {
					pst.setString(23, getEmployeeList(","+ strSessionEmpId +",",uF.parseToInt(hmOrientMemberID.get("Anyone"))));
				} else {
					pst.setString(23, "");
				}
			}
			
			if(ceoIds != null && !ceoIds.equals("")){
				pst.setString(24, ceoIds);
			}else{
				if(hmOrientMemberID.get("CEO") != null && memberList.contains(hmOrientMemberID.get("CEO"))) {
					pst.setString(24, getEmployeeList(","+ strSessionEmpId +",",uF.parseToInt(hmOrientMemberID.get("CEO"))));
				} else {
					pst.setString(24, "");
				}
			}
			
			if(hodIds != null && !hodIds.equals("")){
				pst.setString(25, hodIds);
			}else{
				if(hmOrientMemberID.get("HOD") != null && memberList.contains(hmOrientMemberID.get("HOD"))) {
					pst.setString(25, getEmployeeList(","+ strSessionEmpId +",",uF.parseToInt(hmOrientMemberID.get("HOD"))));
				} else {
					pst.setString(25, "");
				}
			}
			pst.setString(26, sbReviewers.toString());
			pst.execute();
//			System.out.println("add pst ===>> " + pst);
			pst.close();
			
			pst = con.prepareStatement("select max(appraisal_details_id) from appraisal_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				id = rs.getString(1);
			}
			rs.close();
			pst.close();
			
			
			if(getFrom() != null && !getFrom().equals("") && getTo()!= null && !getTo().equals("")) {
//				***************************** appraisal Frequency Start ************************************
				AppraisalScheduler scheduler = new AppraisalScheduler(request, session, CF, uF, strSessionEmpId);
				scheduler.updateAppraisalDetails(id);
//				***************************** appraisal Frequency End ************************************
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+""+uF.showData(getAppraiselName(), "This")+" added successfully."+END);
			
			if(uF.parseToInt(id) > 0) {
				List<String> alManagers = null;
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
					alManagers = insertWorkrigMember(con, pst, rs, uF.parseToInt(id), uF);
				}
			}
			// Start Dattatray
			List<String> appFreqIDs = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details_frequency where appraisal_id=? and is_delete=false");
			pst.setInt(1, uF.parseToInt(id));
			rs= pst.executeQuery();
			while(rs.next()) {
				appFreqIDs.add(rs.getString("appraisal_freq_id"));
				
			}
			rs.close();
			pst.close();
			
			List<String> managerIDList = new ArrayList<String>();
			List<String> peerIDList = new ArrayList<String>();
			List<String> hrIDList = new ArrayList<String>();
			List<String> ceoIDList = new ArrayList<String>();
			List<String> hodIDList = new ArrayList<String>();
			List<String> subordinateIDList = new ArrayList<String>();
			for(int i=0; appFreqIDs!= null && !appFreqIDs.isEmpty() && i<appFreqIDs.size(); i++) {
				for(int j=0; selfList!= null && !selfList.isEmpty() && j<selfList.size(); j++) {
					
					String hrsId = uF.getAppendData(request.getParameterValues("hrsId_"+selfList.get(i)));
					String ceosId = uF.getAppendData(request.getParameterValues("ceosId_"+selfList.get(i)));
					String hodsId = uF.getAppendData(request.getParameterValues("hodsId_"+selfList.get(i)));
					String subordinatesId = uF.getAppendData(request.getParameterValues("subordinatesId_"+selfList.get(i)));
					String managersId = uF.getAppendData(request.getParameterValues("managersId_"+selfList.get(j)));
					String peersId = uF.getAppendData(request.getParameterValues("peersId_"+selfList.get(i)));
					
					List<String> managerList = Arrays.asList(uF.showData(managersId, "").split(","));
					managerIDList.addAll(managerList);
					List<String> peerList = Arrays.asList(uF.showData(peersId, "").split(","));
					peerIDList.addAll(peerList);
					List<String> hrList = Arrays.asList(uF.showData(hrsId, "").split(","));
					hrIDList.addAll(hrList);
					List<String> ceoList = Arrays.asList(uF.showData(ceosId, "").split(","));
					ceoIDList.addAll(ceoList);
					List<String> hodList = Arrays.asList(uF.showData(hodsId, "").split(","));
					hodIDList.addAll(hodList);
					List<String> subordinateList = Arrays.asList(uF.showData(subordinatesId, "").split(","));
					subordinateIDList.addAll(subordinateList);
					
					pst = con.prepareStatement("insert into appraisal_reviewee_details (appraisal_id, appraisal_freq_id,reviewee_id,subordinate_ids," +
						"supervisor_ids,peer_ids,hr_ids,hod_ids,ceo_ids) values (?,?,?,?, ?,?,?,?, ?)"); 
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(appFreqIDs.get(i)));
					pst.setInt(3, uF.parseToInt(selfList.get(j)));
					if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate")!= null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
						pst.setString(4, (subordinatesId!=null && subordinatesId.length()>0) ? ","+subordinatesId+"," : "");
					} else {
						pst.setString(4, "");
					}
					if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!= null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
						pst.setString(5, (managersId!=null && managersId.length()>0) ? ","+managersId+"," : "");
					} else {
						pst.setString(5, "");
					}
					if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
						pst.setString(6, (peersId!=null && peersId.toString().length()>0) ? ","+peersId.toString()+"," : "");
					} else {
						pst.setString(6, "");
					}
					if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!= null  && memberList.contains(hmOrientMemberID.get("HR"))) {
						pst.setString(7, (hrsId!=null && hrsId.length()>0) ? ","+hrsId+"," : "");
					} else {
						pst.setString(7, "");
					}
					if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!= null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
						pst.setString(8, (hodsId!=null && hodsId.length()>0) ? ","+hodsId+"," : "");
					} else {
						pst.setString(8, "");
					}
					if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!= null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
						pst.setString(9, (ceosId!=null && ceosId.toString().length()>0) ? ","+ceosId.toString()+"," : "");
					} else {
						pst.setString(9, "");
					}
					pst.executeUpdate();
					pst.close();
	
				}
			}
			//End dattatray
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private List<String> insertWorkrigMember(Connection con, PreparedStatement pst, ResultSet rs, int reveiwId, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
		
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1,uF.parseToInt(getPolicy_id()));
			rs=pst.executeQuery();
			
			Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
			while(rs.next()){
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
			
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it=hmMemberMap.keySet().iterator();
			while(it.hasNext()){
				String work_flow_member_id=it.next();
				List<String> innerList=hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
//				System.out.println("innerList.get(3)+memid====>"+innerList.get(3)+memid+"=====>"+request.getParameter(innerList.get(3)+memid));
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")){
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3){
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
//					System.out.println("approval empid====>"+empid);
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id) values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,reveiwId);
					pst.setString(3,WORK_FLOW_SELF_REVIEW);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplySelfReview"+strSubAction;
					} else {
						 alertAction = "Reviews.action?pType=WR&callFrom=NotiApplySelfReview"+strSubAction;
					}
					
					String alertData = "<div style=\"float: left;\"> Received a new Self Review Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> review name ("+getAppraiselName()+"). ["+hmUserType.get(userTypeId+"")+"] </div>";
					 
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId+"");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(empid);
//					userAlerts.set_type(SELF_REVIEW_REQUEST_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
					
					if(!alManagers.contains(empid)) {
						alManagers.add(empid);
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
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
		return alManagers;
	}
	
	
//	public void addTeamMember(UtilityFunctions uF) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		ResultSet rs = null;
//		con = db.makeConnection(con);
//		Map<String, String> hmOrientMemberID = getOrientMemberID();
//		try {
//			List<String> selfList = getSelfList();
//			List<String> finalizeList = getFinalizeIdsList();
//			Map<String, String> hmEmpName = CF.getEmpNameMap(null, null);
//			appraiseeList = uF.showData(getAppendData(selfList, hmEmpName), "");
//			StringBuilder sb1 = new StringBuilder();
//			for (int i = 0; i < selfList.size(); i++) {
//				if (i == 0) {
//					sb1.append("," + selfList.get(i).trim()+",");
//				} else {
//					sb1.append(selfList.get(i).trim()+",");
//				}
//			}
//			
//			StringBuilder sb2 = new StringBuilder();
//			for (int i = 0; i < finalizeList.size(); i++) {
//				if (i == 0) {
//					sb2.append("," + finalizeList.get(i).trim()+",");
//				} else {
//					sb2.append(finalizeList.get(i).trim()+",");
//				}
//			}
//					
//			List<String> memberList=getOrientationMemberDetails(uF.parseToInt(getOreinted()));
//			StringBuilder members=new StringBuilder();
//			for(int i=0;i<memberList.size();i++){
//				if(i==0)
//					members.append(memberList.get(i));
//				else
//					members.append(","+memberList.get(i));
//			}
//			
//			String hrIds = request.getParameter("hidehrId");
//			String managerIds = request.getParameter("hidemanagerId");
//			String peerIds = request.getParameter("hidepeerId");
//			
//			pst = con.prepareStatement("update appraisal_details set oriented_type=?,employee_id=?,level_id=?,desig_id=?,"
//							+ "grade_id=?,wlocation_id=?,department_id=?,supervisor_id=?,peer_ids=?,self_ids=?,emp_status=?," +
//									"added_by=?,entry_date=?,hr_ids=?,usertype_member=?,finalization_ids=? where appraisal_details_id=?");
//			pst.setString(1, getOreinted());
//			pst.setString(2, sb1.toString());
//			pst.setString(3, getStrLevel());
//			pst.setString(4, getStrDesignationUpdate());
//			pst.setString(5, getEmpGrade()); 
//			pst.setString(6, getStrWlocation());
//			pst.setString(7, getStrDepart());
//			if(managerIds != null && !managerIds.equals("")){
//				pst.setString(8, managerIds);
//			}else{
//				if (memberList.contains(hmOrientMemberID.get("Manager"))) {
//					pst.setString(8, getEmployeeList(sb1.toString(),uF.parseToInt(hmOrientMemberID.get("Manager"))));
//				} else {
//					pst.setString(8, null);
//				}
//			}
//			if(peerIds != null && !peerIds.equals("")){
//				pst.setString(9, peerIds);
//			}else{
//				if(memberList.contains(hmOrientMemberID.get("Peer"))) {
//					pst.setString(9, getEmployeeList(sb1.toString(),uF.parseToInt(hmOrientMemberID.get("Peer"))));
//				} else {
//					pst.setString(9, null);
//				}
//			}
//			pst.setString(10, sb1.toString());
//			pst.setString(11, getEmp_status());
//			pst.setInt(12, uF.parseToInt(strSessionEmpId));
//			pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
//			if(hrIds != null && !hrIds.equals("")){
//				pst.setString(14, hrIds);
//			}else{
//				if(memberList.contains(hmOrientMemberID.get("HR"))) {
//					pst.setString(14, getEmployeeList(sb2.toString(),uF.parseToInt(hmOrientMemberID.get("HR"))));
//				} else {
//					pst.setString(14, null);
//				}
//			}
//			pst.setString(15,members.toString());
//			pst.setString(16, sb2.toString());
//			pst.setInt(17, uF.parseToInt(getId()));
//			pst.execute();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//	}
	
	
	
	private List<String> getSelfList() {
		List<String> al = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			if (getEmployee() != null && getEmployee().length() > 0) {
				List<String> emp=Arrays.asList(getEmployee().split(","));
				for(int i=0;emp!=null && !emp.isEmpty() && i<emp.size();i++){
					al.add(emp.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
			} else {
			
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
				
				if(getStrOrg()!=null && !getStrOrg().equals("")){
					sbQuery.append(" and eod.org_id in ("+getStrOrg()+") ");
				}
				if(getStrWlocation()!=null && !getStrWlocation().equals("")){
					sbQuery.append(" and eod.wlocation_id in ("+getStrWlocation()+") ");
				}
				if(getStrDepart()!=null && !getStrDepart().equals("")){
					sbQuery.append(" and eod.depart_id in ("+getStrDepart()+") ");
				}
				if(getStrLevel()!=null && !getStrLevel().equals("")){
					sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
							" (SELECT designation_id FROM designation_details  WHERE level_id in (" + getStrLevel()+ "))) ");
				}
				if(getStrDesignationUpdate()!=null && !getStrDesignationUpdate().equals("")){
					sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
							" (SELECT designation_id FROM designation_details  WHERE designation_id in (" + getStrDesignationUpdate() + ")))  ");
				}
				if(getEmpGrade()!=null && !getEmpGrade().equals("")){
					sbQuery.append("  and eod.grade_id in(SELECT grade_id FROM grades_details where grade_id in (" + getEmpGrade()+ ") ) ");
				}
				
				sbQuery.append(" order by epd.emp_per_id");
				
	
				pst = con.prepareStatement(sbQuery.toString());			
				rsEmpCode = pst.executeQuery();
				while (rsEmpCode.next()) {	
					al.add(rsEmpCode.getString("emp_per_id"));
				}
				rsEmpCode.close();
				pst.close();
		
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	/*private List<String> getFinalizeIdsList() {
		List<String> al = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		UtilityFunctions uf = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			if (getFinalizationName() != null && getFinalizationName().length() > 0) {
				List<String> empFinal=Arrays.asList(getFinalizationName().split(","));
				for(int i=0;empFinal!=null && !empFinal.isEmpty() && i<empFinal.size();i++){
					al.add(empFinal.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
			} else {
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where " +
						"epd.emp_per_id=eod.emp_id and epd.emp_per_id=ud.emp_id and ud.usertype_id in(1,7) ");
				if(getStrWlocation()!=null && !getStrWlocation().equals("")){
					sbQuery.append(" and eod.wlocation_id in ("+getStrWlocation()+") ");
				}
				sbQuery.append(" order by epd.emp_per_id");
	
				pst = con.prepareStatement(sbQuery.toString());			
				rsEmpCode = pst.executeQuery();
				while (rsEmpCode.next()) {
					al.add(rsEmpCode.getString("emp_per_id"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rsEmpCode);
		}
		return al;
	}*/

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public void getattribute() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < attributeList.size(); i++) {
			FillAttribute fillAttribute = attributeList.get(i);

			sb.append("<option value=\"" + fillAttribute.getId() + "\">"
					+ fillAttribute.getName() + "</option>");

		}
		request.setAttribute("attribute", sb.toString());

	}

	public void getAppraisalQuestionList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			StringBuilder sb = new StringBuilder("");
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from question_bank where is_add=true");
			rs = pst.executeQuery();
			while (rs.next()) {
				sb.append("<option value=\"" + rs.getString("question_bank_id") + "\">" + rs.getString("question_text").replace("'", "") + "</option>");
			}
			rs.close();
			pst.close();
			sb.append("<option value=\"0\">Add new Question</option>");

//			System.out.println("option===="+sb.toString());
			request.setAttribute("option", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private List<String> getOrientationMemberDetails(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		List<String> memberList=new ArrayList<String>();
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from orientation_details where orientation_id=?");
			pst.setInt(1,id);
			rs=pst.executeQuery();
			
			while(rs.next()){
				memberList.add(rs.getString("member_id"));
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
		return memberList;
		}
	
	
	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillEmployee> getFinalizationList() {
		return finalizationList;
	}

	public void setFinalizationList(List<FillEmployee> finalizationList) {
		this.finalizationList = finalizationList;
	}

	public String getAppraiselName() {
		return appraiselName;
	}

	public void setAppraiselName(String appraiselName) {
		this.appraiselName = appraiselName;
	}

	public String getOreinted() {
		return oreinted;
	}

	public void setOreinted(String oreinted) {
		this.oreinted = oreinted;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getFinalizationName() {
		return finalizationName;
	}

	public void setFinalizationName(String finalizationName) {
		this.finalizationName = finalizationName;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}

	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getAnsTypeOption() {
		return ansTypeOption;
	}

	public void setAnsTypeOption(String ansTypeOption) {
		this.ansTypeOption = ansTypeOption;
	}

	public String getEndTo() {
		return endTo;
	}

	public void setEndTo(String endTo) {
		this.endTo = endTo;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(String startFrom) {
		this.startFrom = startFrom;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getAppraiseeList() {
		return appraiseeList;
	}

	public void setAppraiseeList(String appraiseeList) {
		this.appraiseeList = appraiseeList;
	}

	public List<FillAnswerType> getAnsTypeList() {
		return ansTypeList;
	}

	public void setAnsTypeList(List<FillAnswerType> ansTypeList) {
		this.ansTypeList = ansTypeList;
	}

	public String getAppraisalType() {
		return appraisalType;
	}

	public void setAppraisalType(String appraisalType) {
		this.appraisalType = appraisalType;
	}

	public String getEmp_status() {
		return emp_status;
	}

	public void setEmp_status(String emp_status) {
		this.emp_status = emp_status;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}

	public String getStrDepart() {
		return strDepart;
	}

	public void setStrDepart(String strDepart) {
		this.strDepart = strDepart;
	}

	public String getStrWlocation() {
		return strWlocation;
	}

	public void setStrWlocation(String strWlocation) {
		this.strWlocation = strWlocation;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getAppraisal_description() {
		return appraisal_description;
	}

	public void setAppraisal_description(String appraisal_description) {
		this.appraisal_description = appraisal_description;
	}

	public String getAnnualDay() {
		return annualDay;
	}

	public void setAnnualDay(String annualDay) {
		this.annualDay = annualDay;
	}

	public String getAnnualMonth() {
		return annualMonth;
	}

	public void setAnnualMonth(String annualMonth) {
		this.annualMonth = annualMonth;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMonthday() {
		return monthday;
	}

	public void setMonthday(String monthday) {
		this.monthday = monthday;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMain_level_id() {
		return main_level_id;
	}

	public void setMain_level_id(String main_level_id) {
		this.main_level_id = main_level_id;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getAppraisal_instruction() {
		return appraisal_instruction;
	}

	public void setAppraisal_instruction(String appraisal_instruction) {
		this.appraisal_instruction = appraisal_instruction;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public List<FillEmployee> getReviewerList() {
		return reviewerList;
	}

	public void setReviewerList(List<FillEmployee> reviewerList) {
		this.reviewerList = reviewerList;
	}

	public String getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
}
