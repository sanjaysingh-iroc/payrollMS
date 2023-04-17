package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SetLearningPlanQuestionToTextfield extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	private String queid ="";
	private String count="";
	
//	List<FillOrientation> orientationList;
		
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
//		System.out.println("count "+ count);
//		System.out.println("count "+ getCount());
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		//orientationList = new FillOrientation().fillOrientation();
		getAppraisalSelectedQuestion();
		setAllDetailsOfSelectedQue();
		return SUCCESS;

	}
	
	public void getAppraisalSelectedQuestion() {
		String quetitle="",quekey="";
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			List<String> queList = new ArrayList<String>();
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from learning_plan_question_bank where learning_plan_question_bank_id =?");
			pst.setInt(1, uF.parseToInt(getQueid()));
//			System.out.println("pst "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				queList.add(rs.getString("learning_plan_question_bank_id"));
				queList.add(rs.getString("learning_plan_question_text"));
				queList.add(rs.getString("option_a"));
				queList.add(rs.getString("option_b"));
				queList.add(rs.getString("option_c"));
				queList.add(rs.getString("option_d"));
				queList.add(rs.getString("correct_ans"));
				queList.add(rs.getString("answer_type"));
				quekey = rs.getString("learning_plan_question_bank_id");
				quetitle = rs.getString("learning_plan_question_text");
			}
			rs.close();
			pst.close();
			
//			System.out.println("option===="+sb.toString());
			request.setAttribute("queList", queList);
			request.setAttribute("quekey", quekey);
			request.setAttribute("quetitle", quetitle);
			request.setAttribute("count", getCount());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void setAllDetailsOfSelectedQue(){
		UtilityFunctions uF = new UtilityFunctions();
		List<String> queList = (List<String>)request.getAttribute("queList");
		StringBuilder alldata = new StringBuilder();
		if(queList != null && !queList.isEmpty()){
			int cnt = uF.parseToInt(getCount());
			alldata.append("<input type=\"hidden\" name=\"hidequeid\" id=\"hidequeid"+cnt+"\" value=\""+queList.get(0)+"\"/>");
	//		alldata.append("<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\" value=\""+queList.get(1)+"\"/>");
			alldata.append("<textarea rows=\"2\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\">"+queList.get(1)+"</textarea>");
			alldata.append("::::");
			if(uF.parseToInt(queList.get(7)) == 1 || uF.parseToInt(queList.get(7)) == 2 || uF.parseToInt(queList.get(7)) == 8){
				alldata.append("<th></th><th></th>" +
						"<td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" value=\""+uF.showData(queList.get(2),"")+"\"/>" +
						"<input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" ");
						if(queList.get(6).contains("a")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append("/> </td>" +
						"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" value=\""+uF.showData(queList.get(3),"")+"\"/>" +
						"<input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" ");
						if(queList.get(6).contains("b")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append("/></td>");
				alldata.append("::::");
				alldata.append("<th></th><th></th>" +
						"<td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" value=\""+uF.showData(queList.get(4),"")+"\"/>" +
						"<input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" ");
						if(queList.get(6).contains("c")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append("/></td>" +
						"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" value=\""+uF.showData(queList.get(5),"")+"\"/>" +
						" <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" ");
						if(queList.get(6).contains("d")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append("/></td>");
			}else if(uF.parseToInt(queList.get(7)) == 9 ){
				alldata.append("<th></th><th></th>" +
						"<td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" value=\""+uF.showData(queList.get(2),"")+"\"/>" +
						"<input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" ");
						if(queList.get(6).contains("a")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append("/> </td>" +
						"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" value=\""+uF.showData(queList.get(3),"")+"\"/>" +
						"<input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" ");
						if(queList.get(6).contains("b")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append("/></td>");
				alldata.append("::::");
				alldata.append("<th></th><th></th>" +
						"<td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" value=\""+uF.showData(queList.get(4),"")+"\"/>" +
						"<input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  ");
						if(queList.get(6).contains("c")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append("/></td>" +
						"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" value=\""+uF.showData(queList.get(5),"")+"\"/>" +
						"<input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" ");
						if(queList.get(6).contains("d")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append("/></td>");
			}else if(uF.parseToInt(queList.get(7)) == 6 ){
				alldata.append("<th></th><th></th>" +
						"<td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\" value=\""+uF.showData(queList.get(2),"")+"\"/>" +
						"<input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\" value=\""+uF.showData(queList.get(3),"")+"\"/>" +
						"<input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\" value=\""+uF.showData(queList.get(4),"")+"\"/>" +
						"<input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\" value=\""+uF.showData(queList.get(5),"")+"\"/>" +
						"<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"1\" ");
						if(queList.get(6).contains("1")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append(">True&nbsp;"
						+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\" ");
						if(queList.get(6).contains("0")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append(">False</td>");
			}else if(uF.parseToInt(queList.get(7)) == 5 ){
				alldata.append("<th></th><th></th>" +
						"<td><input type=\"hidden\" name=\"optiona\" id=\"optiona"+cnt+"\" value=\""+uF.showData(queList.get(2),"")+"\"/>" +
						"<input type=\"hidden\" name=\"optionb\" id=\"optionb"+cnt+"\" value=\""+uF.showData(queList.get(3),"")+"\"/>" +
						"<input type=\"hidden\" name=\"optionc\" id=\"optionc"+cnt+"\" value=\""+uF.showData(queList.get(4),"")+"\"/>" +
						"<input type=\"hidden\" name=\"optiond\" id=\"optiond"+cnt+"\" value=\""+uF.showData(queList.get(5),"")+"\"/>" +
						"<input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\" ");
						if(queList.get(6).contains("1")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append(">Yes&nbsp;"
						+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\" ");
						if(queList.get(6).contains("0")){
							alldata.append("checked=\"checked\" ");
						}
						alldata.append(">No</td>");
			}
	//		alldata.append("");
		}
//		System.out.println("alldata :: "+alldata);
		request.setAttribute("alldata", alldata);
	}
	
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getQueid() {
		return queid;
	}

	public void setQueid(String queid) {
		this.queid = queid;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
