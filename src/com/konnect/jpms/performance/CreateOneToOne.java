package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class CreateOneToOne implements ServletRequestAware, IStatements, Runnable {
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;

	private List<FillEmployee> reviewerList;
	private List<FillGoals> goalList;
	private String oneToOneId;
	private String OneToOneName;
	private String oneToOne_description;
	private String reviewerId;
	private String goal;
	private String opeartion;
	private String from;
	private String to;
	private String submit;
	public String execute() throws Exception {
		System.out.println("In createoneToOne");
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
	
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		UtilityFunctions uF = new UtilityFunctions();
		String submit = request.getParameter("submit");
		request.setAttribute(PAGE, "/jsp/performance/CreateOneToOne.jsp");
		request.setAttribute(TITLE, "Create One To One");
		initialize(uF);
		if(getSubmit()!=null && getSubmit().equalsIgnoreCase("save")){
			insertOneToOneDetails(uF);
			return "success";
			//request.setAttribute(PAGE, "/jsp/performance/CreateOneToOne.jsp");
			//request.setAttribute(TITLE, "Created One To One");
		}
		System.out.println("getSubmit:::"+getSubmit());
		if(getSubmit()!=null && getSubmit().equalsIgnoreCase("update")){
			System.out.println("In update:");
			updateOneToOneDetails(uF);
			return "success";
			//request.setAttribute(PAGE, "/jsp/performance/CreateOneToOne.jsp");
			//request.setAttribute(TITLE, "Created One To One");
		}
		if(getOpeartion() !=null && getOpeartion().equalsIgnoreCase("D") && getOneToOneId() != null ){
			deleteData(uF);
			return "success";
		}
		if(getOpeartion() !=null && getOpeartion().equalsIgnoreCase("E") && getOneToOneId() != null ){
			getData(uF);
			return LOAD;
			
		}
		System.out.println("In success");
		return LOAD;
	}
	
	public void deleteData(UtilityFunctions uF)
	{
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		con = db.makeConnection(con);
		try {
			
			pst = con.prepareStatement("delete from OneToOne_details where id = ?");
			pst.setInt(1, uF.parseToInt(getOneToOneId()))	;
			System.out.println("pst ==========>" +pst);
			pst.execute();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
		
	
	public void updateOneToOneDetails(UtilityFunctions uF)
	{
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		con = db.makeConnection(con);

		try {
				StringBuilder sbGoals = null;
				if (getGoal() != null && getGoal().length() > 0) {
					List<String> alGoals= Arrays.asList(getGoal().split(","));
					for(int i=0; alGoals!=null && !alGoals.isEmpty() && i<alGoals.size();i++) {
						if(sbGoals == null) {
							sbGoals = new StringBuilder();
							sbGoals.append("," + alGoals.get(i).trim()+",");
						} else {
							sbGoals.append(alGoals.get(i).trim()+",");
						}
					}
	//				
				}
				if(sbGoals == null) {
					sbGoals = new StringBuilder();
				}
			System.out.println("sbGoals ==========>" + sbGoals);
			
			pst = con.prepareStatement("update  OneToOne_details set name =?,description = ? ,reviewer_id = ?,from_date = ?,to_date =? ,goals = ? where id = ?");
			pst.setString(1, getOneToOneName());
			pst.setString(2, getOneToOne_description());
			pst.setString(3, getReviewerId());
			pst.setDate(4, uF.getDateFormat(getFrom(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getTo(), DATE_FORMAT));
			if(sbGoals.length() <=0){
				pst.setString(6, " ");
			}else{
				pst.setString(6,sbGoals.toString());
			}
			pst.setInt(7, uF.parseToInt(getOneToOneId()));
			System.out.println("pst ==========>" +pst);
			pst.executeUpdate();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	public void getData(UtilityFunctions uF)
	{
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		con = db.makeConnection(con);
		Map<String,String> hmEmpList = new HashMap<String,String>();
		Map<String, String> hmGoalsCodeMap = new HashMap<String,String>();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from goal_details where is_close = false");
			rs=pst.executeQuery();
			while(rs.next()){
				hmGoalsCodeMap.put(rs.getString("goal_id"),rs.getString("goal_title"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.is_alive = true");
			rs=pst.executeQuery();
			while(rs.next()){
				hmEmpList.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+" "+ rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			
			List<String> goalIdList = new ArrayList<String>();
			Set goalIdSet = hmGoalsCodeMap.keySet();
 			Iterator itgoal = goalIdSet.iterator();
 			while(itgoal.hasNext()){
 				goalIdList.add((String)itgoal.next());
 			}
			
 			System.out.print("goalIdList====>"+goalIdList);
 			rs.close();
 			pst.close();
 			
			pst = con.prepareStatement("select * from OneToOne_details where id = ?");
			pst.setInt(1, uF.parseToInt(getOneToOneId()));
			System.out.print("pst====>"+pst);
			rs = pst.executeQuery();
			
			List<String> alOneToOne = new ArrayList<String>();
			while(rs.next()){
				
				alOneToOne.add(rs.getString("id"));
				alOneToOne.add(rs.getString("name"));
				alOneToOne.add(rs.getString("description"));
				alOneToOne.add(rs.getString("reviewer_id"));
				//alOneToOne.add(rs.getString("from_date"));
				//alOneToOne.add(rs.getString("from_date"));
				
				alOneToOne.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), "")); //17
				setFrom(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));
				alOneToOne.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), "")); //17
				setTo(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));
				
				
				
				String goals = rs.getString("goals");
				System.out.println("goals::"+goals);
				List<String> allGoals = new ArrayList<String>();
				if(goals != null && !goals.equals("")) {
					allGoals = Arrays.asList(goals.split(","));
				}
				System.out.println("allGoals::"+allGoals);
				System.out.println("goalIdList::"+goalIdList);
			
				StringBuilder sbGoals = new StringBuilder();
				if(allGoals.size() > 0){
					for(int i=0; goalIdList!=null && i<goalIdList.size(); i++) {
						if(allGoals.contains(goalIdList.get(i))) {
							sbGoals.append("<option value='"+goalIdList.get(i)+"' selected>"+hmGoalsCodeMap.get(goalIdList.get(i))+"</option>");
						} else {
							sbGoals.append("<option value='"+goalIdList.get(i)+"'>"+hmGoalsCodeMap.get(goalIdList.get(i))+"</option>");
						}
					}
				
				}else if(allGoals.size() == 0){
					sbGoals.append("<option value='"+""+"' selected>"+"All Gola's"+"</option>");
					for(int i=0; goalIdList!=null && i<goalIdList.size(); i++) {
						sbGoals.append("<option value='"+goalIdList.get(i)+"'>"+hmGoalsCodeMap.get(goalIdList.get(i))+"</option>");
					}
				}
				System.out.println("sbGolas::"+sbGoals.toString());
				request.setAttribute("sbGoals", sbGoals.toString());
			
			
			System.out.println("OneToOneList::"+alOneToOne);
			request.setAttribute("OneToOneList", alOneToOne);
			
		}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	
	public void insertOneToOneDetails(UtilityFunctions uF)
	{
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		con = db.makeConnection(con);
//		Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		try {
			/*StringBuilder sbReviewers = null;
			if (getReviewerId() != null && getReviewerId().length() > 0) {
				List<String> alReviewers = Arrays.asList(getReviewerId().split(","));
				for(int i=0; alReviewers!=null && !alReviewers.isEmpty() && i<alReviewers.size();i++) {
					if(sbReviewers == null) {
						sbReviewers = new StringBuilder();
						sbReviewers.append("," + alReviewers.get(i).trim()+",");
					} else {
						sbReviewers.append(alReviewers.get(i).trim()+",");
					}
				}
//				
			}
			if(sbReviewers == null) {
				sbReviewers = new StringBuilder();
			}*/
			
			StringBuilder sbGoals = null;
			if (getGoal() != null && getGoal().length() > 0) {
				List<String> alGoals= Arrays.asList(getGoal().split(","));
				for(int i=0; alGoals!=null && !alGoals.isEmpty() && i<alGoals.size();i++) {
					if(sbGoals == null) {
						sbGoals = new StringBuilder();
						sbGoals.append("," + alGoals.get(i).trim()+",");
					} else {
						sbGoals.append(alGoals.get(i).trim()+",");
					}
				}
//				
			}
			if(sbGoals == null) {
				sbGoals = new StringBuilder();
			}
			System.out.println("sbGoals ==========>" + sbGoals);
			
			pst = con.prepareStatement("insert into OneToOne_details(name,description,reviewer_id,from_date,to_date,goals)"
					+"values(?,?,?,?,?,?)");
			
			pst.setString(1, getOneToOneName());
			pst.setString(2, getOneToOne_description());
			pst.setString(3, getReviewerId());
			pst.setDate(4, uF.getDateFormat(getFrom(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getTo(), DATE_FORMAT));
			pst.setString(6, sbGoals.toString());
			
			System.out.print("pst====>"+pst);
			pst.executeUpdate();
			pst.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	public void initialize(UtilityFunctions uF ) {
		reviewerList  = new FillEmployee(request).fillEmployeeName();
		goalList = new FillGoals(request).fillGoals();
		System.out.println("goalList::"+goalList);
	}
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
	@Override
	public void run() {
		
	}
	public List<FillEmployee> getReviewerList() {
		return reviewerList;
	}

	public List<FillGoals> getGoalList() {
		return goalList;
	}

	public void setGoalList(List<FillGoals> goalList) {
		this.goalList = goalList;
	}

	public void setReviewerList(List<FillEmployee> reviewerList) {
		this.reviewerList = reviewerList;
	}
	public String getOneToOneName() {
		return OneToOneName;
	}
	public void setOneToOneName(String oneToOneName) {
		OneToOneName = oneToOneName;
	}
	public String getOneToOne_description() {
		return oneToOne_description;
	}
	public void setOneToOne_description(String oneToOne_description) {
		this.oneToOne_description = oneToOne_description;
	}
	public String getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSubmit() {
		return submit;
	}
	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getOpeartion() {
		return opeartion;
	}

	public void setOpeartion(String opeartion) {
		this.opeartion = opeartion;
	}

	public String getOneToOneId() {
		return oneToOneId;
	}

	public void setOneToOneId(String oneToOneId) {
		this.oneToOneId = oneToOneId;
	}

}
