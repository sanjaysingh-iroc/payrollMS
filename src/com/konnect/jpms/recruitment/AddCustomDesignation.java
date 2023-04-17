package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCustomDesignation extends ActionSupport implements ServletRequestAware, IStatements, Runnable {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;

	private String currUserType;
	
	String recruitmentID;
	String nCount;
	String operation;
	
	String userType;
	
	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		if(getApproveSubmit()==null)
		getFilledDesignation();

		desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
		gradeList = new FillGrade(request).fillGrade();
		if (ApproveSubmit != null) {
//			System.out.println("getnCount() ===>> " + getnCount());
			if (getStrDesignationUpdate() != null && !getStrDesignationUpdate().equals("")) {
				updateRecruitmentRequest();
			} else {
				setDesignationDetails();
			}
//			request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\"" + request.getContextPath() + "/images1/icons/approved.png\" border=\"0\">");
			
			return SUCCESS;
		}
		return LOAD;

	}

	
	private void setDesignationDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
//		System.out.println("getCustomDesignation() ===> " + getCustomDesignation());
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("insert into designation_details (designation_code,designation_name,level_id) values(?,?,?) ");
			pst.setString(1,""+getCustomDesignation().substring(0, getCustomDesignation().length() > 3 ? 3 : getCustomDesignation().length()));
			pst.setString(2, getCustomDesignation());
			pst.setInt(3,uF.parseToInt(getStrLevel()));
			pst.execute();
//			System.out.println("pst ===>> " + pst);
    		pst.close();
    		
			int desigIDNew=0;
			pst=con.prepareStatement("select max(designation_id) as desigid from designation_details");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while(rst.next()){
				desigIDNew=rst.getInt("desigid");
			}
			rst.close();
    		pst.close();
			
			pst=con.prepareStatement("insert into grades_details (grade_code,grade_name,designation_id) values(?,?,?) ");
			pst.setString(1,""+getCustomGrade().substring(0, getCustomGrade().length() > 3 ? 3 : getCustomGrade().length()));
			pst.setString(2, getCustomGrade());
			pst.setInt(3, desigIDNew);
			pst.execute();
//			System.out.println("pst ===>> " + pst);
    		pst.close();
			
			int gradeIDNew=0;
			pst=con.prepareStatement("select max(grade_id) as gradeid from grades_details");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while(rst.next()){
				gradeIDNew=rst.getInt("gradeid");
			}
			rst.close();
    		pst.close();
			
			pst=con.prepareStatement("update recruitment_details set designation_id=?,grade_id=?,custum_designation=?," +
					"custum_grade=? where recruitment_id=? ");
			pst.setInt(1, desigIDNew);
			pst.setInt(2, gradeIDNew);
			pst.setString(3, getCustomDesignation());
			pst.setString(4, getCustomGrade());
			pst.setInt(5, uF.parseToInt(getRecruitmentID()));		
			pst.execute();
//			System.out.println("pst ===>> " + pst);
    		pst.close();
    		
//    		updateRequest("1", getRecruitmentID(), con);
    		
    		viewRequestDetails(con, uF);
    		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);

		}
		
	}

	private void updateRecruitmentRequest() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("update recruitment_details set designation_id=?,grade_id=? where recruitment_id=? ");
			pst.setInt(1, uF.parseToInt(getStrDesignationUpdate()));
			pst.setInt(2, uF.parseToInt(getStrGradeUpdate()));
			pst.setInt(3, uF.parseToInt(getRecruitmentID()));		
			pst.execute();
    		pst.close();
    		
//    		updateRequest("1", getRecruitmentID(), con);
    		
    		viewRequestDetails(con, uF);
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void viewRequestDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id = "+uF.parseToInt(getRecruitmentID())+" ");
			sbQuery.append(") group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmNextApproval ===>> " + hmNextApproval);
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id = "+uF.parseToInt(getRecruitmentID())+" ");
			sbQuery.append(") and user_type_id=? ");
			sbQuery.append(" group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getUserType()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmMemNextApproval ===>> " + hmMemNextApproval);
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id = "+uF.parseToInt(getRecruitmentID())+" ");
			sbQuery.append(") group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select recruitment_id from recruitment_details where recruitment_id = "+uF.parseToInt(getRecruitmentID())+" and status=-1 ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("recruitment_id"))) {
					deniedList.add(rs.getString("recruitment_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id = "+uF.parseToInt(getRecruitmentID())+" ");
			sbQuery.append(") group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
					
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id = "+uF.parseToInt(getRecruitmentID())+" ");
			sbQuery.append(") and user_type_id=? ");
			sbQuery.append(" order by effective_id,member_position");
//			sbQuery.append(") and user_type_id=? order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getUserType()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList == null)checkEmpList = new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
				if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
				checkEmpUserTypeList.add(rs.getString("user_type_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();

			// querying for planned records in company
			Map<String, String> hmPlannedCount = new HashMap<String, String>();
			pst = con.prepareStatement("select recruitment_id,resource_requirement from recruitment_details rd, resource_planner_details rpd " +
					" where rpd.designation_id=rd.designation_id and recruitment_id = "+uF.parseToInt(getRecruitmentID())+" and date_part('year', effective_date)=ryear and " +
					"date_part('month', effective_date)=rmonth and requirement_status = 'generate'");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hmPlannedCount.put(rs.getString("recruitment_id"), rs.getString("resource_requirement"));
			}
			rs.close();
			pst.close();

			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("(select r1.*, wfd.user_type_id as user_type from (select d.designation_id, r.priority_job_int,r.status,r.recruitment_id,r.custum_designation,e.emp_fname,e.emp_lname,"
					+ "w.wlocation_id,w.wlocation_name,r.entry_date,r.no_position,r.target_deadline,r.comments,existing_emp_count,"
					+ "d.designation_name,r.dept_id,r.added_by,r.req_form_type,r.hiring_manager from recruitment_details r join work_location_info w on(r.wlocation=w.wlocation_id) "
					+ "join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d using(designation_id) "
					+ "where recruitment_id = "+uF.parseToInt(getRecruitmentID())+" and requirement_status = 'generate' ");

			if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
				strQuery.append(" and r.recruitment_id in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_RECRUITMENT+"'");
				strQuery.append(" and emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				sbQuery.append(" and user_type_id = "+uF.parseToInt(getUserType())+" ");
				strQuery.append(") ");
			}
			
			strQuery.append(" order by r.status desc,r.recruitment_id desc) r1 , work_flow_details wfd " +
				"where r1.recruitment_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_RECRUITMENT+"') ");
			
			pst = con.prepareStatement(strQuery.toString());
//			System.out.println("pst ====> " + pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ====> " + pst);
			StringBuilder sbApproveDeny = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();
			StringBuilder sbDesig = new StringBuilder();
			List<String> alList = new ArrayList<String>();
			Map<String, String> hmUserTypeName = CF.getUserTypeMap(con);
			String usetTypeName = "";
			while (rs.next()) {
				
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("recruitment_id"));
				if(checkEmpList==null) checkEmpList = new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("recruitment_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
//				System.out.println("checkEmpUserTypeList ===>> " + checkEmpUserTypeList);
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
				List<String> checkHiringManagerList = new ArrayList<String>();
				
				if(rs.getString("hiring_manager") != null) {
					checkHiringManagerList = Arrays.asList(rs.getString("hiring_manager").split(","));
				}
//				System.out.println(rs.getString("recruitment_id")+" -- checkEmpUserTypeList ===>> " + checkEmpUserTypeList);
//				System.out.println("RID ===>> "+rs.getString("recruitment_id")+"USRTYPE ===>> "+rs.getString("user_type"));
//				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)) {
				if(!checkEmpList.contains(strSessionEmpId) && !checkHiringManagerList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strSessionEmpId) != uF.parseToInt(rs.getString("added_by"))) {
					continue;
				}
//				System.out.println("1 RID ===>> "+rs.getString("recruitment_id")+"USRTYPE ===>> "+rs.getString("user_type"));
//				if(strUserType.equalsIgnoreCase(HRMANAGER) && (rs.getString("wlocation_id")!=null && !rs.getString("wlocation_id").trim().equals(locationID))) {
//					continue;
//				}
				
				String userType = rs.getString("user_type");				
				if((!checkEmpList.contains(strSessionEmpId) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) || (strUserType!=null && strUserType.equalsIgnoreCase(ADMIN))) && alList.contains(rs.getString("recruitment_id"))) {
//					System.out.println("2 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType !=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("recruitment_id"))) {
//				} else if((( (!checkEmpList.contains(strSessionEmpId) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) || (checkEmpUserTypeList.contains(userType) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) ) || 
//					System.out.println("3 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					userType = strUserTypeId;
					alList.add(rs.getString("recruitment_id"));
				} else if(strUserType !=null && !strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("recruitment_id")) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by")) ) ) {
//				} else if((( (!checkEmpList.contains(strSessionEmpId) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) || (checkEmpUserTypeList.contains(userType) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) ) ||
					if( (!checkEmpList.contains(strSessionEmpId) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) 
						|| (checkEmpUserTypeList.contains(userType) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) )) {
//					System.out.println("3---1 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
						userType = strBaseUserTypeId;	
					} else {
						userType = strUserTypeId;
					}
					alList.add(rs.getString("recruitment_id"));
					} else {
//						System.out.println("3---1 else RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
						continue;
					}
				} else if(!checkEmpUserTypeList.contains(userType)) {
//					System.out.println("4 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					continue;	
				} 
				
//				System.out.println("strSessionEmpId ===>> " + strSessionEmpId + " -- checkEmpList ===>> " + checkEmpList);
				if(checkEmpList.contains(strSessionEmpId) && uF.parseToInt(rs.getString("req_form_type"))==0) {
					usetTypeName = "["+hmUserTypeName.get(rs.getString("user_type"))+"]";
//					System.out.println("usetTypeName  ===>> " + usetTypeName);
				}else {
					usetTypeName = "";
				}
				
				sbApproveDeny.replace(0, sbApproveDeny.length(), "");
				sbStauts.replace(0, sbStauts.length(), "");
				sbDesig.replace(0, sbDesig.length(), "");
				
				String strnCount = nCount;
				
				sbDesig.append("<a href=\"javascript:void(0);\" onclick=\"getDesignationDetails('"+rs.getString("designation_id")+"','"+rs.getString("designation_name")+"')\">"+rs.getString("designation_name")+"</a>");
				
//				System.out.println("userType ===>> " + userType);
//				System.out.println(rs.getString("recruitment_id") + " -- hmNextApproval ===>> " + uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id"))));
//				System.out.println(rs.getString("recruitment_id") + " -- hmMemNextApproval ===>> " + uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id")+"_"+userType)));
				if(deniedList.contains(rs.getString("recruitment_id"))) {
//					System.out.println("===>> 1");
					/*sbStauts.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("recruitment_id")+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(rs.getInt("status")==1) {
//					System.out.println("===>> 2");
					 /*sbStauts.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("recruitment_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("recruitment_id")))==rs.getInt("status")) {
//					System.out.println("===>> 3");
					 /*sbStauts.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if((strUserType != null && strUserType.equals(ADMIN) && (usetTypeName == null || usetTypeName.equals(""))) || (uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))>0)) {
//					System.out.println("===>> 4 -- usetTypeName ===>> " + usetTypeName);
					/*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i>");
					
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveRequest('"+ nCount +"','"+ rs.getString("recruitment_id")+"','"+userType+"','"+getCurrUserType()+"');\" >" +
						"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Create Job Profile\"></i></a> ");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))denyRequest('" + strnCount
						+ "','" + rs.getString("recruitment_id") + "','"+userType+"','"+getCurrUserType()+"');\">" + "<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Job Requirement\"></i></a>  ");
					if(uF.parseToInt(rs.getString("req_form_type")) > 0) {
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequestWOWorkflow('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
							+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
					} else {
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
							+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
					}
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id")+"_"+userType)))) {
//					System.out.println("===>> 5");
//					System.out.println("status ===>> " + rs.getInt("status") +" strUserType ===>> " + strUserType);
						if(rs.getInt("status")==0) {
//							System.out.println("===>> in status 0");
							if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) { //!checkEmpList.contains(strSessionEmpId) && 
//								System.out.println("===>> in ADMIN");
								 /*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" />");*/
								sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i>");
								
								sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveRequest('"+ nCount +"','"+ rs.getString("recruitment_id")+"','"+userType+"','"+getCurrUserType()+"');\" >" +
										"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Create Job Profile\"></i></a> ");
								sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))denyRequest('" + strnCount
										+ "','" + rs.getString("recruitment_id") + "','"+userType+"','"+getCurrUserType()+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Job Requirement\"></i></a>  ");
								if(uF.parseToInt(rs.getString("req_form_type")) > 0) {
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequestWOWorkflow('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
											+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
								} else {
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
										+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
								}
							} else {
								
								/*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for workflow\" />");*/
								sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\" style=\"color:#ea9900\"></i>");
								
								
//								****************** Workflow *******************************
								sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("recruitment_id")+"');\" style=\"margin-left: 10px;\">Work flow</a>");
								if(checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) {
									if(uF.parseToInt(rs.getString("req_form_type")) > 0) {
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequestWOWorkflow('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
												+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
									} else {
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
											+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
									}
								}
								if(!checkGHRInWorkflow) {
									sbApproveDeny.append("&nbsp;|&nbsp;<a href=\"javascript:void(0)\" onclick=\"approveRequest('"+ nCount +"','"+ rs.getString("recruitment_id")+"','','"+getCurrUserType()+"');\" >" +
										"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Create Job Profile ("+ADMIN+")\"></i></a> ");
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))denyRequest('" + strnCount
										+ "','" + rs.getString("recruitment_id") + "','','"+getCurrUserType()+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Job Requirement ("+ADMIN+")\"></i></a>  ");
								}
//								****************** Workflow *******************************
							}
						} else if(rs.getInt("status")==1) {
							/*sbStauts.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						} else {
							/*sbStauts.append("<img   src=\"images1/icons/denied.png\" border=\"0\" />");*/
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
						}
						
				} else {
					/*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for workflow\" /> </a> ");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Waiting for workflow\"></i>");
					
//					****************** Workflow *******************************
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("recruitment_id")+"');\" style=\"margin-left: 10px;\">Work flow</a>");
					if(checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) {
						if(uF.parseToInt(rs.getString("req_form_type")) > 0) {
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequestWOWorkflow('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
								+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
						} else {
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
								+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
						}
					}
//					****************** Workflow *******************************
				}
			}
			rs.close();
			pst.close();
			setOperation("VIEW");
//			System.out.println("getOperation() =====>> " + getOperation());
//			System.out.println(" =====>> " + sbStauts.toString() +"::::"+ sbApproveDeny.toString());
			request.setAttribute("sbStautsApproveDeny", sbStauts.toString() +"::::"+ sbDesig.toString() +"::::"+ sbApproveDeny.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	
	public void getFilledDesignation() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmLevelName = CF.getLevelMap(con);
			pst = con.prepareStatement("Select level_id,designation_id,custum_designation,custum_grade from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitmentID()));
			rst = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while (rst.next()) {
				setStrLevelName(hmLevelName.get(rst.getString("level_id")));
				setStrLevel(rst.getString("level_id"));
				setCustomDesignation(rst.getString("custum_designation"));
				setCustomGrade(rst.getString("custum_grade"));
			}
			rst.close();
    		pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
	private void updateRequest(String strStatus, String strId, Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		UtilityFunctions uF = new UtilityFunctions();

		boolean flag = false;

		if (strId != null && strStatus != null) {
			int intStatus = uF.parseToInt(strStatus);
			switch (intStatus) {
			case 1:
				try {
					String addedBY = null;
					String jobCode="";
					pst=con.prepareStatement("select wloacation_code,designation_code,custum_designation,added_by from recruitment_details " +
							" left join designation_details using (designation_id) join work_location_info on (wlocation_id=wlocation)" +
							" where recruitment_id=? ");
					pst.setInt(1, uF.parseToInt(strId));
					rs=pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					if(rs.next()) {
						addedBY = rs.getString("added_by");
						if(rs.getString("designation_code")==null) {
							jobCode+=rs.getString("wloacation_code")+"-NEW";
						} else {
							jobCode+=rs.getString("wloacation_code")+"-"+rs.getString("designation_code");
						}
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select count(*) as count from recruitment_details where job_code like '"+jobCode+"%'");
					rs = pst.executeQuery();
					
					while (rs.next()) {
						int count=uF.parseToInt(rs.getString("count"));
						count++;
						DecimalFormat decimalFormat = new DecimalFormat();
						decimalFormat.setMinimumIntegerDigits(3);
							
						jobCode+="-"+decimalFormat.format(count);
					}
					rs.close();
					pst.close();
					
					String query = "update recruitment_details set status=?,job_code=?,approved_by=?,approved_date=? where recruitment_id=?";
					pst = con.prepareStatement(query);
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setString(2, jobCode);
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, uF.parseToInt(strId));
					pst.execute();
//					System.out.println("pst ===>> " + pst);
					pst.close();
					
					String strAddedBy = addedBY;
//					System.out.println("strAddedBy ===>> " + strAddedBy);
					if(uF.parseToInt(strAddedBy) > 0) {
						String strDomain = request.getServerName().split("\\.")[0];
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strAddedBy);
						userAlerts.set_type(REQUIREMENT_APPROVAL_ALERT);
						userAlerts.setStatus(INSERT_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					
					pst = con.prepareStatement("select emp_id from user_details where usertype_id=7 OR usertype_id=1");
					rs = pst.executeQuery();
					while (rs.next()) {
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(rs.getString(1));
						userAlerts.set_type(JOBCODE_REQUEST_ALERT);
						userAlerts.setStatus(INSERT_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					rs.close();
					pst.close();
					Map<String, String> hmDesignation = CF.getDesigMap(con);
					Map<String, String> hmRecruitmentData = CF.getRecruitmentDetails(con, uF, CF, request, strId);
					
					String designationName = hmRecruitmentData.get("DESIG_NAME");
					session.setAttribute(MESSAGE, SUCCESSM+""+designationName+" designation requirement has been approved successfully."+END);

					String strDomain = request.getServerName().split("\\.")[0];
					setDomain(strDomain);
					Thread th = new Thread(this);
					th.start();
					sendMail(strId);
					
					flag = true;
				} catch (Exception e) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	
	public void sendMail(String strId) {
		System.out.println("++++++Thread example UpdateADRRequest++++++");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);

		try{
			con = db.makeConnection(con);
			Map<String, String> hmRecruitmentData = CF.getRecruitmentDetails(con, uF, CF, request, strId);	
			String strAddedBy = hmRecruitmentData.get("ADDED_BY");
			
			if(strAddedBy != null && !strAddedBy.equals("")){
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_RECRUITMENT_APPROVAL, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(strAddedBy);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
		
				nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
				nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
				nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
				nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
				nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
				nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
				nF.setEmailTemplate(true);
				nF.setStrJobTitle(hmRecruitmentData.get("JOB_TITLE"));//Created By Dattatray Date : 05-10-21 
				nF.sendNotifications();
			}
			
		Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
		String empWlocation = hmEmpWLocation.get(strSessionEmpId);
//		String panel_employee_id = "";
		pst = con.prepareStatement("select emp_per_id from employee_personal_details epd, employee_official_details eod, user_details ud " +
				"where epd.emp_per_id = eod.emp_id and epd.emp_per_id = ud.emp_id and ud.usertype_id = 7 and eod.wlocation_id = ?");
		pst.setInt(1, uF.parseToInt(empWlocation));
		rst = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
//		System.out.println("pst ===> "+pst);
		while (rst.next()) {
			//Map<String, String> hmEmpInner = hmEmpInfo.get(rst.getString("emp_per_id"));

			if(rst.getString("emp_per_id") != null && !rst.getString("emp_per_id").equals("")) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_RECRUITMENT_APPROVAL, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(rst.getString("emp_per_id"));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
		
				nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
				nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
				nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
				nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
				nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
				nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
				nF.setEmailTemplate(true);				
				nF.setStrJobTitle(hmRecruitmentData.get("JOB_TITLE"));//Created By Dattatray Date : 05-10-21 
				nF.sendNotifications();
			} 
		}
		rst.close();
		pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getRecruitmentID() {
		return recruitmentID;
	}

	public void setRecruitmentID(String recruitmentID) {
		this.recruitmentID = recruitmentID;
	}

	String customDesignation;
	String customGrade;

	public String getCustomGrade() {
		return customGrade;
	}

	public void setCustomGrade(String customGrade) {
		this.customGrade = customGrade;
	}

	public String getCustomDesignation() {
		return customDesignation;
	}

	public void setCustomDesignation(String customDesignation) {
		this.customDesignation = customDesignation;
	}

	String strLevel;
	String strLevelName;

	public String getStrLevelName() {
		return strLevelName;
	}

	public void setStrLevelName(String strLevelName) {
		this.strLevelName = strLevelName;
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

	public String getStrGradeUpdate() {
		return strGradeUpdate;
	}

	public void setStrGradeUpdate(String strGradeUpdate) {
		this.strGradeUpdate = strGradeUpdate;
	}

	String strDesignationUpdate;
	String strGradeUpdate;
	
	String ApproveSubmit;

	public String getApproveSubmit() {
		return ApproveSubmit;
	}

	public void setApproveSubmit(String approveSubmit) {
		ApproveSubmit = approveSubmit;
	}
	
	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}
	
   public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getnCount() {
		return nCount;
	}

	public void setnCount(String nCount) {
		this.nCount = nCount;
	}
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}