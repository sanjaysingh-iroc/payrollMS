package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class MyReviewScore implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	
	private String id;
	private String empid;
	private String levelid;
	private String scoreid;
	private String type;
	private String memberId;
	private String appFreqId;
	
	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLevelid() {
		return levelid;
	}

	public void setLevelid(String levelid) {
		this.levelid = levelid;
	}

	public String getScoreid() {
		return scoreid;
	}

	public void setScoreid(String scoreid) {
		this.scoreid = scoreid;
	}

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
//		System.out.println("memberId===="+memberId);
		getOrientationMember();
		request.setAttribute(PAGE, "/jsp/performance/MyReviewScore.jsp");
		request.setAttribute(TITLE, "My Review Score Status");
		getAppraisalFinalStatus();
		request.setAttribute("empid", getEmpid());
		if(type!=null && type.equals("popup"))
			return "popup";
		return "success";
	}

	private void getAppraisalFinalStatus() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db=new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		con = db.makeConnection(con);

		try {
		
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(memberId==null){
					List<String> memberList=Arrays.asList(rs.getString("usertype_member").split(","));
					request.setAttribute("memberList", memberList);
				}else{
					List<String> memberList=new ArrayList<String>();
					memberList.add(memberId);
					request.setAttribute("memberList", memberList);
				}				
			}
			rs.close();
			pst.close();

			boolean flag=false;
			pst=con.prepareStatement("select * from appraisal_level_details where appraisal_level_id=?");
			pst.setInt(1,uF.parseToInt(levelid));
			rs=pst.executeQuery();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("appraisal_system"))==1){
					flag=true;
				}
			}
			rs.close();
			pst.close();


			if(flag){
			pst = con.prepareStatement("select * from appraisal_question_answer aqd,question_bank qb where aqd.question_id=qb.question_bank_id and  appraisal_id=? and scorecard_id=? "
							+ "and emp_id=? and appraisal_level_id=? and appraisal_freq_id=?");
			}else{
				pst = con.prepareStatement("select * from appraisal_question_answer aqd,question_bank qb where aqd.question_id=qb.question_bank_id and appraisal_id=? and other_id=? "
						+ "and emp_id=? and appraisal_level_id=? and appraisal_freq_id=?");
			}
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(scoreid));
			pst.setInt(3, uF.parseToInt(empid));
			pst.setInt(4, uF.parseToInt(levelid));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			
			Map<String,Map<String,Map<String,String>>> questionMp=new HashMap<String,Map<String,Map<String,String>>>();
			Map<String, String> questionDetailsMp = new HashMap<String, String>();
			while (rs.next()) {
				Map<String,Map<String,String>> userType=questionMp.get(rs.getString("question_id"));
				if(userType==null)userType=new HashMap<String,Map<String,String>>();
				Map<String,String> innerMap=userType.get(rs.getString("user_type_id"));
				if(innerMap ==null)innerMap= new HashMap<String,String>();
				
				innerMap.put("MARKS",rs.getString("marks"));
				innerMap.put("WEIGHTAGE",rs.getString("weightage"));
				questionDetailsMp.put(rs.getString("question_id"),rs.getString("question_text"));
				userType.put(rs.getString("user_type_id"), innerMap);
				questionMp.put(rs.getString("question_id"), userType);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("questionDetailsMp", questionDetailsMp);
			request.setAttribute("questionMp", questionMp);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String,String>  getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db=new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,String> orientationMemberMp=new HashMap<String,String>();
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
//			pst = con.prepareStatement(selectOrientationMember);
			rs=pst.executeQuery();
			while(rs.next()){
				//orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return orientationMemberMp;
		}

	public Map<String, String> getLevelMap() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		Database db=new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
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
		return hmLevelMap;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
	
}
