package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class TrainingFeedBack  implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	
	private String empID;
	private String plan_id;
	private String submit;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/training/TrainingFeedBack.jsp");
		request.setAttribute(TITLE, "Training Feedback");
		UtilityFunctions uF = new UtilityFunctions();
		
		getQuestionSubType(uF);		
		getQuestionMap(uF);
		getQuestionList(uF);
		getFeedBackAnswer(uF);
		getanswerTypeMap(uF);
		
		getTrainingDetails(uF);
		
		if(getSubmit()!=null){
			insertMarks(uF);
			return "success";
		}
		
		return LOAD;

	}
	
	private void getTrainingDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);
			Map<String,String> hmOrg=getOrganization(con);
			Map<String,String> hmLocationName=getLocationName(con);
			
			Map<String,String> hmAttribute=new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
			rs = pst.executeQuery();
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from training_plan where plan_id=?");
			pst.setInt(1, uF.parseToInt(getPlan_id()));

			rs = pst.executeQuery();
			StringBuilder sbAttributeID=new StringBuilder();
			int i=0;
			List<String> trainingDetails=new ArrayList<String>();
			while (rs.next()) {
				trainingDetails.add(rs.getString("training_title"));
				trainingDetails.add(rs.getString("training_objective"));
				trainingDetails.add(rs.getString("training_summary"));
				trainingDetails.add(rs.getString("training_type"));
				
				String certificate="No";
				if(uF.parseToBoolean(rs.getString("is_certificate"))){
					if(rs.getString("certificate_id")!=null && !rs.getString("certificate_id").equals("")){
						certificate=CF.getCertificateName(con, rs.getString("certificate_id").trim());
					}else{
						certificate="";
					}
				}
				trainingDetails.add(certificate);
				
				if(rs.getString("attribute_id")!=null && !rs.getString("attribute_id").equals("")){
					List<String> attributeList=Arrays.asList(rs.getString("attribute_id").split(","));
					Set<String> attributeSet = new HashSet<String>(attributeList);
					Iterator<String> itr = attributeSet.iterator();
					
					while (itr.hasNext()) {
						String attributeId = (String) itr.next();
						if(i==0){
							sbAttributeID.append(hmAttribute.get(attributeId.trim()));
						}else{
							sbAttributeID.append(","+hmAttribute.get(attributeId.trim()));
						}
						i++;
						trainingDetails.add(sbAttributeID.toString());
					}
				}else{
					trainingDetails.add("");
				}
				trainingDetails.add(rs.getString("alignedwith"));
				trainingDetails.add(rs.getString("org_id")==null || rs.getString("org_id").equals("") ? "" : hmOrg.get(rs.getString("org_id").trim()));
				
				String location="";
				i=0;
				if(rs.getString("location_id")!=null && !rs.getString("location_id").equals("")){
					List<String> locationList=Arrays.asList(rs.getString("location_id").split(","));
					Set<String> locationSet = new HashSet<String>(locationList);
					Iterator<String> itr = locationSet.iterator();
					
					while (itr.hasNext()) {
						String locationId = (String) itr.next();
						if(i==0){
							location=hmLocationName.get(locationId.trim());
						}else{
							location+=","+hmLocationName.get(locationId.trim());
						}
						i++;
					}
				}
				trainingDetails.add(location);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("trainingDetails", trainingDetails);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private Map<String, String> getLocationName(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmLocationName=new HashMap<String, String>();
		
		try {
			pst = con.prepareStatement("select * from work_location_info");
			rs=pst.executeQuery();
			
			while(rs.next()){
				hmLocationName.put(rs.getString("wlocation_id"),rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLocationName;
	}

	
	
	private Map<String, String> getOrganization(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmOrg=new HashMap<String, String>();
		
		try {
			pst = con.prepareStatement("select org_id,org_name from org_details");
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"),rs.getString("org_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return hmOrg;
	}
	
	
	
	private void insertMarks(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		List<List<String>> outerList = (List<List<String>>) request.getAttribute("questionList");
		Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");

		try {

			pst = con.prepareStatement("delete from training_question_answer where emp_id=? and plan_id=?");
			pst.setInt(1, uF.parseToInt(empID));
			pst.setInt(2, uF.parseToInt(getPlan_id()));
			pst.execute();
			pst.close();
			
			for (int i = 0; outerList != null && i < outerList.size(); i++) {
				List<String> innerlist = (List<String>) outerList.get(i);
//				System.out.println("1 innerlist.get(0)=====>"+innerlist.get(0));
				List<String> questioninnerList = hmQuestion.get(innerlist.get(1));

				String weightage = innerlist.get(4);
				String givenAnswer = null;
				double marks = 0;
				String remark = null;

				if (uF.parseToInt(questioninnerList.get(8)) == 1) {
					String[] correct = request.getParameterValues("correct" + innerlist.get(1));
					remark = request.getParameter("" + innerlist.get(1));
					String correctanswer = questioninnerList.get(6);
					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k];
						} else {
							givenAnswer += "," + correct[k];
						}
					}
					if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
					String[] correct = request.getParameterValues("correct" + innerlist.get(1));
					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k];
						} else {
							givenAnswer += "," + correct[k];
						}
					}
					String correctanswer = questioninnerList.get(6);

					if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					} else {
						marks = 0;
					} 

				} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {

					marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)));
				} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {

					givenAnswer = request.getParameter("" + innerlist.get(1));
					marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(weightage)) / 100;

				} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
					givenAnswer = request.getParameter("" + innerlist.get(1));
					String answer = questioninnerList.get(6);
					if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
						marks = uF.parseToDouble(weightage);
					}
				} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
					givenAnswer = request.getParameter("" + innerlist.get(1));
					String answer = questioninnerList.get(6);
					if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
						marks = uF.parseToDouble(weightage);
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {

					givenAnswer = request.getParameter("" + innerlist.get(1));
					marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)));
					weightage = request.getParameter("outofmarks" + innerlist.get(1));

				} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {

					givenAnswer = request.getParameter("correct" + innerlist.get(1));
					String correctanswer = questioninnerList.get(6);
					if (givenAnswer != null && correctanswer != null && correctanswer.contains(givenAnswer)) {
						marks = uF.parseToDouble(weightage);
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {

					String[] correct = request.getParameterValues("correct" + innerlist.get(1));

					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k];
						} else {
							givenAnswer += "," + correct[k];
						}
					}

					String correctanswer = questioninnerList.get(6);

					if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {

					marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)));
					String a = request.getParameter("a" + innerlist.get(1));
					String b = request.getParameter("b" + innerlist.get(1));
					String c = request.getParameter("c" + innerlist.get(1));
					String d = request.getParameter("d" + innerlist.get(1));

					givenAnswer = uF.showData(a, "") + " :_:" + uF.showData(b, "") + " :_:" + uF.showData(c, "") + " :_: " + uF.showData(d, "");

				} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
					String rating = request.getParameter("gradewithrating" + innerlist.get(1));

					marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 5;

				}  else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
					givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+questioninnerList.get(9));
				}
				
				  //training_question_answer_id serial NOT NULL,training_question_id
				  //emp_id,answer,plan_id,question_id,user_id,user_type_id,attempted_on,weightage,marks,remark,training_question_id
				
				pst = con.prepareStatement("insert into training_question_answer(emp_id,answer,plan_id,question_id,user_id,user_type_id," +
								"attempted_on,weightage,marks,remark,training_question_id)values(?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getEmpID()));
				pst.setString(2, givenAnswer);
				pst.setInt(3, uF.parseToInt(getPlan_id()));
				pst.setInt(4, uF.parseToInt(innerlist.get(1)));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setInt(6, uF.parseToInt(""));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDouble(8, uF.parseToDouble(weightage));
				pst.setDouble(9, marks);
				pst.setString(10, remark);
				pst.setInt(11, uF.parseToInt(innerlist.get(0)));
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void getanswerTypeMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<List<String>>> hmQuestionanswerType = new HashMap<String, List<List<String>>>();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_answer_type_sub ");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList = hmQuestionanswerType.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score_label"));
				innerList.add(rs.getString("score"));
				outerList.add(innerList);
				hmQuestionanswerType.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuestionanswerType", hmQuestionanswerType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void getFeedBackAnswer(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from training_question_answer  where plan_id=?  and emp_id=?");
			pst.setInt(1, uF.parseToInt(getPlan_id()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionanswerMp = new HashMap<String, Map<String, String>>();

			while (rs.next()) {
				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				
				questionanswerMp.put(rs.getString("plan_id") + "question" + rs.getString("question_id"), innerMp);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("questionanswerMp", questionanswerMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	

	public boolean getQuestionList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);

			StringBuilder sb = new StringBuilder("select * from training_question_details where plan_id=? ");
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getPlan_id()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("training_question_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add(rs.getString("question_for"));
				innerList.add(rs.getString("weightage"));
				outerList.add(innerList);
			}
			rs.close();
			pst.close();

			request.setAttribute("questionList", outerList);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	public Map<String, String> getQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_question_bank qb, training_question_details aqd " +
					" where qb.training_question_bank_id=aqd.question_id and plan_id=?");
			pst.setInt(1, uF.parseToInt(getPlan_id()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("training_question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("is_add"));
				innerList.add(rs.getString("question_type"));

				outerList.add(innerList);
				hmQuestion.put(rs.getString("training_question_bank_id"), innerList);
				AppraisalQuestion.put(rs.getString("training_question_bank_id"), rs.getString("question_text"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuestion", hmQuestion);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return AppraisalQuestion;
	}

	
	
	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		con = db.makeConnection(con);

		try {
			pst = con.prepareStatement("select * from training_answer_type_sub order by training_answer_type_sub_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList = answertypeSub.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score"));
				innerList.add(rs.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();

			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getEmpID() {
		return empID;
	}


	public void setEmpID(String empID) {
		this.empID = empID;
	}


	public String getPlan_id() {
		return plan_id;
	}


	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	
}
