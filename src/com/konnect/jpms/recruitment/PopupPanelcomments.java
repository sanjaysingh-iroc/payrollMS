package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.views.xslt.ArrayAdapter;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PopupPanelcomments extends ActionSupport implements ServletRequestAware,IConstants{

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	public String execute(){
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/recruitment/PopupPanelcomments.jsp");
	
		preparecomments();
		return "popup";
		
	}

	private void preparecomments() {
	
	Database db=new Database();
	db.setRequest(request);
	Connection con=null;
	ResultSet rst=null;
	PreparedStatement pst=null;
	UtilityFunctions uF = new UtilityFunctions();
	
	try{
	
		con=db.makeConnection(con);
	
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		/*pst=con.prepareStatement("select panel_emp_id from panel_interview_details  where recruitment_id=? and round_id=? ");
		pst.setInt(1, uF.parseToInt(getRecruitid()));
		pst.setInt(2, uF.parseToInt(getPanelid()));	
		rst=pst.executeQuery();
		StringBuilder sbPanelEmpName = new StringBuilder();
		while(rst.next()){
			if(rst.isLast() && rst.getString("panel_emp_id") != null && !rst.getString("panel_emp_id").equals("")){
				sbPanelEmpName.append(hmEmpName.get(rst.getString("panel_emp_id")));
			} else if(rst.getString("panel_emp_id") != null && !rst.getString("panel_emp_id").equals("")){
				sbPanelEmpName.append(hmEmpName.get(rst.getString("panel_emp_id")));
				sbPanelEmpName.append(",");
			}
		}*/
//		System.out.println("sbPanelEmpName ===> "+sbPanelEmpName);
		
		pst=con.prepareStatement("select comments, panel_rating, panel_user_id from candidate_interview_panel " +
				"where candidate_id=? and panel_round_id=? and recruitment_id=? ");
		pst.setInt(1, uF.parseToInt(getCandidateid()));
		pst.setInt(2, uF.parseToInt(getPanelid()));
		pst.setInt(3, uF.parseToInt(getRecruitid()));
		rst=pst.executeQuery();
//		System.out.println("pst ===> " + pst);
		List<List<String>> empCommentList = new ArrayList<List<String>>(); 
		while(rst.next()){
			List<String> innerList = new ArrayList<String>();
			innerList.add(rst.getString("panel_user_id"));
			innerList.add(hmEmpName.get(rst.getString("panel_user_id")));
			innerList.add(uF.showData(rst.getString("comments"),"-"));
			innerList.add(rst.getString("panel_rating"));
			empCommentList.add(innerList);
		}
		rst.close();
		pst.close();
		
//		System.out.println("empCommentList ===> " + empCommentList);
		request.setAttribute("empCommentList", empCommentList);
//		System.out.println("printing candidateid"+getCandidateid()+"panelid===="+getPanelid()+"panel commenttss=-==="+getComments());
	
	} catch(Exception e) {
	
		e.printStackTrace();	
	} finally {
		if(rst != null) {
			try {
				rst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	} 
	
	}

	private String candidateid;
	private String panelid;
	private String recruitid;
	
	public String getRecruitid() {
		return recruitid;
	}

	public void setRecruitid(String recruitid) {
		this.recruitid = recruitid;
	}

	public String getCandidateid() {
		return candidateid;
	}

	public void setCandidateid(String candidateid) {
		this.candidateid = candidateid;
	}

	public String getPanelid() {
		return panelid;
	}
	public void setPanelid(String panelid) {
		this.panelid = panelid;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}
	
}
