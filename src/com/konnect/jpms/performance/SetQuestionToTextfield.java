package com.konnect.jpms.performance;

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

public class SetQuestionToTextfield extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	private String queid ;
	private String count;
	
	private List<FillOrientation> orientationList;
		
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		//System.out.println("count "+ count);
		//System.out.println("count "+ getCount());
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		//orientationList = new FillOrientation().fillOrientation();
		if(getQueid() != null && uF.parseToInt(getQueid()) > 0) {
			getAppraisalSelectedQuestion();
			setAllDetailsOfSelectedQue();
		}
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
			
			pst = con.prepareStatement("select * from question_bank where question_bank_id =?");
			pst.setInt(1, uF.parseToInt(getQueid()));
			rs = pst.executeQuery();
			while (rs.next()) {
				queList.add(rs.getString("question_bank_id"));//0
				queList.add(rs.getString("question_text"));//1
				queList.add(rs.getString("option_a"));//2
				queList.add(rs.getString("option_b"));//3
				queList.add(rs.getString("option_c"));//4
				queList.add(rs.getString("option_d"));//5
				queList.add(rs.getString("correct_ans"));//6
				queList.add(rs.getString("question_type"));//7
				queList.add(rs.getString("is_add"));//8
				queList.add(rs.getString("option_e"));//9
				queList.add(rs.getString("rate_option_a"));//10
				queList.add(rs.getString("rate_option_b"));//11
				queList.add(rs.getString("rate_option_c"));//12
				queList.add(rs.getString("rate_option_d"));//13
				queList.add(rs.getString("rate_option_e"));//14
				quekey = rs.getString("question_bank_id");
				quetitle = rs.getString("question_text");
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
		if(uF.parseToInt(queList.get(7)) == 1 || uF.parseToInt(queList.get(7)) == 2 || uF.parseToInt(queList.get(7)) == 8) {
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
		} else if(uF.parseToInt(queList.get(7)) == 9 ) {
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
		} else if(uF.parseToInt(queList.get(7)) == 6 ) {
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
		} else if(uF.parseToInt(queList.get(7)) == 5 ) {
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
		} else if(uF.parseToInt(queList.get(7)) == 13 ) {
			alldata.append("<th></th><th></th>" +
				"<td>a)&nbsp;<input type=\"text\" name=\"optiona\" id=\"optiona"+cnt+"\" value=\""+uF.showData(queList.get(2),"")+"\"/>" +
				"<input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" ");
				if(queList.get(6).contains("a")){
					alldata.append("checked=\"checked\" ");
				}
				alldata.append("/>&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" value=\""+uF.showData(queList.get(10),"")+"\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>" +
				"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" id=\"optionb"+cnt+"\" value=\""+uF.showData(queList.get(3),"")+"\"/>" +
				"<input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" ");
				if(queList.get(6).contains("b")){
					alldata.append("checked=\"checked\" ");
				}
				alldata.append("/>&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" value=\""+uF.showData(queList.get(11),"")+"\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>");
			alldata.append("::::");
			alldata.append("<th></th><th></th>" +
				"<td>c)&nbsp;<input type=\"text\" name=\"optionc\" id=\"optionc"+cnt+"\" value=\""+uF.showData(queList.get(4),"")+"\"/>" +
				"<input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" ");
				if(queList.get(6).contains("c")){
					alldata.append("checked=\"checked\" ");
				}
				alldata.append("/>&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" value=\""+uF.showData(queList.get(12),"")+"\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>" +
				"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" id=\"optiond"+cnt+"\" value=\""+uF.showData(queList.get(5),"")+"\"/>" +
				" <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" ");
				if(queList.get(6).contains("d")){
					alldata.append("checked=\"checked\" ");
				}
				alldata.append("/>&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" value=\""+uF.showData(queList.get(13),"")+"\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>");
			alldata.append("::::");
			alldata.append("<th></th><th></th>" +
				"<td>c)&nbsp;<input type=\"text\" name=\"optione\" id=\"optione"+cnt+"\" value=\""+uF.showData(queList.get(9),"")+"\"/>" +
				"<input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" ");
				if(queList.get(6).contains("e")){
					alldata.append("checked=\"checked\" ");
				}
				alldata.append("/>&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" value=\""+uF.showData(queList.get(14),"")+"\" onkeypress=\"return isOnlyNumberKey(event)\"/></td><td colspan=\"2\">&nbsp;</td>");
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
