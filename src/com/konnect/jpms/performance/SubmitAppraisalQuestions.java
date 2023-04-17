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

public class SubmitAppraisalQuestions implements ServletRequestAware,
		IStatements {

	HttpSession session;
	CommonFunctions CF;
	private String id;
	private String empId;
	private String profile;
	private String appFreqId;
	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String execute() {
		 session = request.getSession();
		// strSessionEmpId = (String) session.getAttribute(EMPID);
		// strSessionUserType = (String) session.getAttribute(USERTYPE);
		 CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		 if (CF == null)
		 return "login";
		//
		// request.setAttribute(PAGE, "/jsp/performance/Appraisal.jsp");
		// request.setAttribute(TITLE, "Appraisal");
		// getOffboardEmployeeList();
		submitAnswer();
		
		if(profile!=null){
			return "profile";
		}
		return "success";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void submitAnswer() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {

			StringBuilder sb=new StringBuilder("select * from appraisal_question_details aqd,question_bank qb where qb.question_bank_id=aqd.question_id  and appraisal_id =? ");
			/*if(uF.parseToInt((String)session.getAttribute(USERTYPEID))==2 || uF.parseToInt((String)session.getAttribute(USERTYPEID))==1){
				sb.append("and manager !=2");
				
			}else if(uF.parseToInt((String) session.getAttribute(USERTYPEID)) == 7){
				sb.append("and hr !=2");
				
			}else if(uF.parseToInt((String) session.getAttribute(USERTYPEID)) == 3){
				
				
				if(uF.parseToInt(empId)==uF.parseToInt((String) session.getAttribute(EMPID))){
					sb.append("and self !=2");
				}else {
					
					sb.append("and peer !=2");
				}
			}*/
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			List<List<Integer>> appraiselAnswerList=new ArrayList<List<Integer>>();

			while (rs.next()) {
				List<Integer> innerList = new ArrayList<Integer>();
				innerList.add(rs.getInt("question_bank_id"));
				innerList.add(rs.getInt("question_type"));
				appraiselAnswerList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con
			.prepareStatement("delete from appraisal_question_answer where emp_id=? and appraisal_id=? and user_id=? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(id));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setInt(4, uF.parseToInt(getAppFreqId()));
			pst.execute();
			pst.close();
			
			for(int i=0;i<appraiselAnswerList.size();i++){
				List<Integer> innerList =appraiselAnswerList.get(i);
			pst = con.prepareStatement("insert into appraisal_question_answer(emp_id,answer,appraisal_id,question_id,user_id,user_type_id,attempted_on,appraisal_freq_id) values(?,?,?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			if(innerList.get(1)==1){
				String answers=request.getParameter("answer"+i);
				pst.setString(2,answers);
			}else if(innerList.get(1)==2){
				String[] answers=request.getParameterValues("answer"+i);
				StringBuilder sb1=new StringBuilder();
				for(int j=0;answers!=null && j<answers.length;j++){
					sb1.append(answers[j]+",");
				}
				pst.setString(2,sb1.toString());
			}else if(innerList.get(1)==3){
				String answers=request.getParameter("answer"+i);
				pst.setString(2,answers);
			}
			pst.setInt(3, uF.parseToInt(id));
			pst.setInt(4,innerList.get(0));
			pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setInt(6, uF.parseToInt((String) session.getAttribute(USERTYPEID)));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_approval(appraisal_id,emp_id,user_id,user_type_id,status,appraisal_freq_id) values(?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3,uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(4,uF.parseToInt((String)session.getAttribute(USERTYPEID)));
			pst.setBoolean(5,true);
			pst.setInt(6, uF.parseToInt(getAppFreqId()));
			pst.execute();
			pst.close();
			
			}
		} catch (SQLException e) {
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

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	
}
