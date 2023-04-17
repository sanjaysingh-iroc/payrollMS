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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OneToOneSummary extends ActionSupport implements ServletRequestAware, IStatements {
private static final long serialVersionUID = 6485071086781961535L;
		
	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	String strId =  null;
	String fromPage = null;

	Map<String, List<Map<String, List<List<String>>>>> levelMp = new HashMap<String, List<Map<String, List<List<String>>>>>();
	Map<String,String> hmEmpList = new HashMap<String,String>();
	Map<String,String> hmGoalList = new HashMap<String,String>();

	private CommonFunctions CF;
	public  String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);

		UtilityFunctions uF = new UtilityFunctions();

		if (CF == null) {
			return LOGIN;
		}
		request.setAttribute(TITLE, " OneToOneSummary");
		request.setAttribute(PAGE, "/jsp/performance/OneToOneSummary.jsp");
		getSummaryDetails(uF);
		return LOAD;
	}
	private void getSummaryDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		String levelID=null;
		Map<String,List<String>> hmOneToOneDetails = new HashMap<String,List<String>>();
		try {
			List<String> alOneToOneDetails = new ArrayList<String>();
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.is_alive = true");
			rs=pst.executeQuery();
			while(rs.next()){
				hmEmpList.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+" "+ rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from goal_details");
			rs=pst.executeQuery();
			while(rs.next()){
				hmGoalList.put(rs.getString("goal_id"), rs.getString("goal_title"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from onetoone_details where id = ?");
			pst.setInt(1, uF.parseToInt(getStrId()));
			rs=pst.executeQuery();
			while(rs.next())
			{
				StringBuilder sbGoals = new StringBuilder();
				alOneToOneDetails.add(rs.getString("name"));//0
				alOneToOneDetails.add(rs.getString("description"));//1
				System.out.println("hmEmpList:"+hmEmpList);
				System.out.println("rs.getString:"+rs.getString("reviewer_id"));
				
				alOneToOneDetails.add(hmEmpList.get(rs.getString("reviewer_id")));//2
				alOneToOneDetails.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT));//3
				alOneToOneDetails.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT));//4
					String goals = rs.getString("goals");
				if(goals !=null){
					String[] goalList = goals.split(",");
					for(int i = 0 ;i < goalList.length;i++){
						if(goalList[i] != null && goalList[i].length() > 0){
							sbGoals.append(","+hmGoalList.get(goalList[i]));
							}
					}
				}
				alOneToOneDetails.add(sbGoals.toString());//5
				alOneToOneDetails.add(rs.getString("is_publish"));//6
			}
			hmOneToOneDetails.put(getStrId(), alOneToOneDetails);
			rs.close();
			pst.close();

		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		System.out.println("hmOneToOneDetails:::"+hmOneToOneDetails);
		request.setAttribute("hmOneToOneDetails", hmOneToOneDetails);

	}
	
	@Override
	public  void setServletRequest( HttpServletRequest request) {
		this.request = request;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
	
}
