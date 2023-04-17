package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillRound;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ChangePanelRound extends ActionSupport implements ServletRequestAware, IStatements{
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	List<FillRound> roundList;
	String strUserType = null;
	String strSessionEmpId = null;
	String recruitID;
	String roundID;
	String empID;
	String strRound;
	String updateRound;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/recruitment/ChangePanelRound.jsp");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE); 
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Requirement Request");

		UtilityFunctions uF=new UtilityFunctions();

		roundList = new FillRound().fillRound(getRecruitID());
		if(updateRound != null && updateRound.equals("Update")){
			changePanelRound(uF);
		}
		return LOAD;
	}

	private void changePanelRound(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			boolean flag = false;
			con=db.makeConnection(con);
			/*pst=con.prepareStatement("select round_id from panel_interview_details where recruitment_id=? and round_id = ? and " +
					"panel_emp_id = ? ");
			pst.setInt(1,uF.parseToInt(getRecruitID()));
			pst.setInt(2, uF.parseToInt(getRoundID()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			rst=pst.executeQuery();
			while(rst.next()){
				maxRoundId = rst.getString("maxround");
			}
	*/
			pst=con.prepareStatement("select round_id from panel_interview_details where recruitment_id=? and round_id = ? and " +
					"panel_emp_id = ? ");
			pst.setInt(1,uF.parseToInt(getRecruitID()));
			pst.setInt(2, uF.parseToInt(getStrRound()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				flag = true;
			}
			rst.close();
			pst.close();
			
			if(flag == false){
				pst = con.prepareStatement("update panel_interview_details set round_id=?,added_by=?,added_date=? where recruitment_id=? and round_id = ? and " +
					"panel_emp_id = ? ");
				pst.setInt(1, uF.parseToInt(getStrRound()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(4, uF.parseToInt(getRecruitID()));
				pst.setInt(5, uF.parseToInt(getRoundID()));
				pst.setInt(6, uF.parseToInt(getEmpID()));
				pst.executeUpdate();
				pst.close();
//				System.out.println("pst ===> "+pst);
			}
		}catch(Exception e){
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

	public String getRecruitID() {
		return recruitID;
	}

	public void setRecruitID(String recruitID) {
		this.recruitID = recruitID;
	}

	public String getRoundID() {
		return roundID;
	}

	public void setRoundID(String roundID) {
		this.roundID = roundID;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getStrRound() {
		return strRound;
	}

	public void setStrRound(String strRound) {
		this.strRound = strRound;
	}

	public List<FillRound> getRoundList() {
		return roundList;
	}

	public void setRoundList(List<FillRound> roundList) {
		this.roundList = roundList;
	}

	public String getUpdateRound() {
		return updateRound;
	}

	public void setUpdateRound(String updateRound) {
		this.updateRound = updateRound;
	}
	

}
