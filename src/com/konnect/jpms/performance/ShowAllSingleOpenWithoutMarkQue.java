package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.opensymphony.xwork2.ActionSupport;

public class ShowAllSingleOpenWithoutMarkQue extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -8588089110618591746L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF; 
	private String appid;
	private String empId;
	private String usertypeId;
	private String readstatus;
	private String appFreqId;
	private String fromPage;
	public String execute() {
		
		session = request.getSession(); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF = new UtilityFunctions();
		String submit = request.getParameter("submit");
		System.out.println("fromPage ===>> " + fromPage);
		System.out.println("submit ===>> " + submit);
		if(submit != null && submit.equals("Save")){
			updateQueStaus(uF);
			return SUCCESS;
			/*if(getFromPage() != null && !getFromPage().equals("LD")){
				return VIEW;
			}else{
				return SUCCESS;
			}*/
		}else{
		getSingleOpenWithoutMarksQueCount(uF);
		return LOAD;
		}
	}
	
	
	private void updateQueStaus(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		/*System.out.println("appid :::::: "+appid);
		System.out.println("empId :::::: "+empId);
		System.out.println("usertypeId :::::: "+usertypeId);*/
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			String queid[] = request.getParameterValues("queid");
			
			con = db.makeConnection(con);
			for(int i =0; i< queid.length; i++){
				String comment = request.getParameter("commenttext"+queid[i]);
				int status = 0;
				boolean boolstatus = uF.parseToBoolean(request.getParameter("status"+queid[i]));
				if(boolstatus == true){
					status =1;
				}
			pst = con.prepareStatement("update appraisal_question_answer set read_status = ?,read_status_comment = ? where appraisal_question_answer_id = ?");// and " +"appraisal_id = ? and emp_id = ? and user_type_id = ?
			pst.setInt(1, status);
			pst.setString(2, comment);
			pst.setInt(3, uF.parseToInt(queid[i]));
			/*pst.setInt(3, uF.parseToInt(appid));
			pst.setInt(4, uF.parseToInt(empId));
			pst.setInt(5, uF.parseToInt(usertypeId));*/
			pst.executeUpdate();
			pst.close();
			
//			System.out.println("pst ============ > "+ pst);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}


	private void getSingleOpenWithoutMarksQueCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> queNamehm = new HashMap<String, String>();
			pst = con.prepareStatement("Select * from question_bank");
			rs = pst.executeQuery();
			while (rs.next()) {
				queNamehm.put(rs.getString("question_bank_id"), rs.getString("question_text"));
			}
			rs.close();
			pst.close();
			
			List<List<String>> queDetailsList = new ArrayList<List<String>>();
			if(readstatus.equals("R")){
				pst = con.prepareStatement("Select aqa.question_id,aqa.answer,aqa.read_status,aqa.appraisal_question_answer_id,aqa.read_status_comment," +
						" aqa.answers_comment from appraisal_question_details aqd, appraisal_question_answer aqa where aqd.answer_type = 12 " +
						"and aqa.question_id = aqd.question_id and aqd.appraisal_id = aqa.appraisal_id and aqd.appraisal_id = ? " +
						"and aqa.emp_id = ? and aqa.user_type_id = ? and appraisal_freq_id = ? and aqa.read_status = 1");
			}else{
			pst = con.prepareStatement("Select aqa.question_id,aqa.answer,aqa.read_status,aqa.appraisal_question_answer_id,aqa.read_status_comment," +
					" aqa.answers_comment from appraisal_question_details aqd, appraisal_question_answer aqa where aqd.answer_type = 12 " +
					"and aqa.question_id = aqd.question_id and aqd.appraisal_id = aqa.appraisal_id and aqd.appraisal_id = ? " +
					"and aqa.emp_id = ? and aqa.user_type_id = ? and appraisal_freq_id = ? ");
			}
			/*pst = con.prepareStatement("Select * from appraisal_question_details where answer_type = 12 and appraisal_id = ? ");*/
			pst.setInt(1, uF.parseToInt(appid));
			pst.setInt(2, uF.parseToInt(empId));
			pst.setInt(3, uF.parseToInt(usertypeId));
			pst.setInt(4, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				List<String> queInnerList = new ArrayList<String>();
				queInnerList.add(queNamehm.get(rs.getString("question_id")));//0
				queInnerList.add(uF.showData(rs.getString("answer"),""));//1
				queInnerList.add(rs.getString("appraisal_question_answer_id"));//2
				queInnerList.add(rs.getString("read_status"));//3
				queInnerList.add(uF.showData(rs.getString("read_status_comment"),""));//4
				queInnerList.add(uF.showData(readstatus,""));//5
				queInnerList.add(uF.showData(rs.getString("answers_comment"),"No comments"));//6
				queDetailsList.add(queInnerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("queDetailsList", queDetailsList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}

	}
	
	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getUsertypeId() {
		return usertypeId;
	}

	public void setUsertypeId(String usertypeId) {
		this.usertypeId = usertypeId;
	}

	public String getReadstatus() {
		return readstatus;
	}

	public void setReadstatus(String readstatus) {
		this.readstatus = readstatus;
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


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
}
