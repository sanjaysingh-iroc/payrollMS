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

public class MyAppraisalDetails implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	private String id;
	private String empId;
	private String flag1;

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
		con = db.makeConnection(con);

		try {
//			System.out.println("getId() "+getId());
			
			Map<String,String> appraisalMp=new HashMap<String,String>();
			pst=con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs=pst.executeQuery();
			while(rs.next()){
				appraisalMp.put("APPRAISAL",rs.getString("appraisal_name"));
				appraisalMp.put("ORIENTATION",rs.getString("oriented_type"));
				appraisalMp.put("EMPLOYEE",rs.getString("self_ids"));
				appraisalMp.put("PEER",rs.getString("peer_ids"));

			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalMp",appraisalMp);
			
			pst = con.prepareStatement("select * from appraisal_question_details aqd,question_bank qb where qb.question_bank_id=aqd.question_id  and appraisal_id =? and self !=2");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> appraiselQuestionList=new ArrayList<List<String>>();
			while(rs.next()) {
				
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
				
				appraiselQuestionList.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraiselQuestionList",appraiselQuestionList);
			
			pst = con
			.prepareStatement("select * from appraisal_question_answer where appraisal_id =? and emp_id=? and user_id=?");
	pst.setInt(1, uF.parseToInt(getId()));
	pst.setInt(2, uF.parseToInt(getEmpId()));
	pst.setInt(3, uF.parseToInt((String) session.getAttribute(EMPID)));

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
	int peerCount=0;
	while(rs.next()){
		approvalList.put(rs.getString("user_type_id"),rs.getBoolean("status"));
		if(rs.getInt("user_type_id")==3)
			peerCount++;
	}
	rs.close();
	pst.close();
	
//	System.out.println("peerCount====="+peerCount);
	
	//String[] a=appraisalMp.get("PEER").split(",");
	
	request.setAttribute("approvalList",approvalList);
	
	boolean flag=false;
	
//	System.out.println("======>>>"+appraisalMp.get("EMPLOYEE").length());
		
		if(uF.parseToInt( appraisalMp.get("ORIENTATION"))==90 || uF.parseToInt( appraisalMp.get("ORIENTATION"))==180){
			if(approvalList.get("1")!=null && approvalList.get("1")){
				flag=true;
			}else if(approvalList.get("7")!=null && approvalList.get("7")){
				flag=true;
			} 
		}else if(uF.parseToInt( appraisalMp.get("ORIENTATION"))==270 || uF.parseToInt( appraisalMp.get("ORIENTATION"))==360){
			if(approvalList.get("1")!=null && approvalList.get("1")){
				flag=true;
			}else if(approvalList.get("3")!=null && approvalList.get("3")){
				String[] a=appraisalMp.get("PEER").split(",");
				
				if(appraisalMp.get("EMPLOYEE")!=null && appraisalMp.get("EMPLOYEE").length()>0){
					if(peerCount==a.length-3)
						flag=true;
				}else{
					if(peerCount==a.length-2)
						flag=true;
				}				
				
			}
		}
		
	
	
	
	
//	System.out.println("flag===="+flag);
	request.setAttribute("flag",flag);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	
}
