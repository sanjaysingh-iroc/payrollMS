package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalDetail implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	String id;
	String empId;
	 

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/performance/Appraisal.jsp");
		request.setAttribute(TITLE, "Appraisal");
		getOffboardEmployeeList();

		return "success";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public void getOffboardEmployeeList() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			Map<String,String> appraisalMp=new HashMap<String,String>();
			int oriented = 0;
			pst=con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs=pst.executeQuery();
			while(rs.next()){
				appraisalMp.put("APPRAISAL",rs.getString("appraisal_name"));
				appraisalMp.put("ORIENTATION",rs.getString("oriented_type"));
				oriented = rs.getInt("oriented_type");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalMp",appraisalMp);
			
			List<String> memberList=CF.getOrientationMemberDetails(con,oriented);
			StringBuilder sb=new StringBuilder("select * from appraisal_question_details aqd,question_bank qb where qb.question_bank_id=aqd.question_id  and appraisal_id =? ");
			/*if(uF.parseToInt((String)session.getAttribute(USERTYPEID))==2 || uF.parseToInt((String)session.getAttribute(USERTYPEID))==1){
				sb.append("and manager !=2");
				
			}else if(uF.parseToInt((String) session.getAttribute(USERTYPEID)) == 7){
				sb.append("and hr !=2");
				
			}else {
				sb.append("and peer !=2");
				
			}*/
			if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(ADMIN)){
//				sb.append("or manager !=2");
//				sb.append("or hr !=2");
//				sb.append("or peer !=2");
				
			}else
			if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER)){
				sb.append("and manager !=2");
				
			}else if(strSessionUserType != null	&& strSessionUserType.equalsIgnoreCase(HRMANAGER)){
				sb.append("and hr !=2");
				
			}else if(strSessionUserType != null	&& strSessionUserType.equalsIgnoreCase(CEO)){
				sb.append("and ceo !=2");
				
			}else if(strSessionUserType != null	&& strSessionUserType.equalsIgnoreCase(HOD)){
				sb.append("and hod !=2");
				
			}else {
				sb.append("and hod !=2");
				
			}
			
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> appraiselQuestionList=new ArrayList<List<String>>();
			while(rs.next()){
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("question_type"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("manager"));
				innerList.add(rs.getString("hr"));
				innerList.add(rs.getString("peer"));
				innerList.add(rs.getString("self"));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("ceo"));
				innerList.add(rs.getString("hod"));
				appraiselQuestionList.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraiselQuestionList",appraiselQuestionList);
			
			pst = con
			.prepareStatement("select * from appraisal_question_answer where appraisal_id =? and emp_id=? and user_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
		
			rs = pst.executeQuery();
			List<List<String>> appraiselAnswerList=new ArrayList<List<String>>();
			Map<String,List<String>> mp=new HashMap<String,List<String>>();
			while(rs.next()){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("answer"));
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("question_id"));
				
				appraiselAnswerList.add(innerList);
				mp.put(rs.getString("question_id"), innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("mp",mp);
			request.setAttribute("appraiselAnswerList",appraiselAnswerList);
		
			Map<String,Boolean> approvalList=new HashMap<String,Boolean>();
			
			
			pst = con.prepareStatement("select * from  appraisal_approval where appraisal_id =? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
				
			rs=pst.executeQuery();
			while(rs.next()){
				approvalList.put(rs.getString("user_type_id"),rs.getBoolean("status"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("approvalList",approvalList);
			
			boolean flag=false;
			 int userTypeId=uF.parseToInt((String) session.getAttribute(USERTYPEID)) ; 
			
			 if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(ADMIN)){
				 flag=true;
			 } /*else  if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(HRMANAGER)){
					if(uF.parseToInt( appraisalMp.get("ORIENTATION"))==90)
						flag=true;
					else if(approvalList.get(strSessionUserTypeID)!=null && approvalList.get(strSessionUserTypeID)){
						flag=true;
					}
			 } else if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) && uF.parseToInt( appraisalMp.get("ORIENTATION"))!=90 ){
					flag=true;
			 } else if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(EMPLOYEE) ){
					
					if(uF.parseToInt( appraisalMp.get("ORIENTATION"))==270 || uF.parseToInt( appraisalMp.get("ORIENTATION"))==360){
						
						if(approvalList.get(strSessionUserTypeID)!=null && approvalList.get(strSessionUserTypeID)){
							flag=true;
						}
					}
			 }*/
			 
			 else  if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(HRMANAGER)){
					if(hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR")))
						flag=true;
					else if(approvalList.get(strSessionUserTypeID)!=null && approvalList.get(strSessionUserTypeID)){
						flag=true;
					}
			 } else if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) ){
				 if(hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager"))) {
					 flag=true;
				 }
					
			 } else if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(EMPLOYEE) ){
					
					if(hmOrientMemberID.get("Self") != null && memberList.contains(hmOrientMemberID.get("Self"))){
						
						if(approvalList.get(strSessionUserTypeID)!=null && approvalList.get(strSessionUserTypeID)){
							flag=true;
						}
					}
			 }  else if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(CEO) ){
					
					if(hmOrientMemberID.get("CEO") != null && memberList.contains(hmOrientMemberID.get("CEO"))){
						
						if(approvalList.get(strSessionUserTypeID)!=null && approvalList.get(strSessionUserTypeID)){
							flag=true;
						}
					}
			 } else if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(HOD) ){
					
					if(hmOrientMemberID.get("HOD") != null && memberList.contains(hmOrientMemberID.get("HOD"))){
						
						if(approvalList.get(strSessionUserTypeID)!=null && approvalList.get(strSessionUserTypeID)){
							flag=true;
						}
					}
			 }
			
		//	System.out.println("flag===="+flag);
			request.setAttribute("flag",flag);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	private List<String> getOrientationMemberDetails(int id) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		List<String> memberList=new ArrayList<String>();
//		try {
//			
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from orientation_details where orientation_id=?");
//			pst.setInt(1,id);
//			rs=pst.executeQuery();
//			while(rs.next()){
//				memberList.add(rs.getString("member_id"));
//			}
//			rs.close();
//			pst.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return memberList;
//		}
	
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

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	
}
