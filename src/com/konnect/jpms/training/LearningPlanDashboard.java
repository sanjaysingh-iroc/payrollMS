package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LearningPlanDashboard extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	private String dataType;
	String strSessionEmpId = null;
	private String learningPlanId;
	private String strSearchJob;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, TLearnings);
		request.setAttribute(PAGE, "/jsp/training/LearningPlanDashboard.jsp");

		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
			getSearchAutoCompleteData();
		}
		getLearningReport();

		return LOAD;

	}

	private void getSearchAutoCompleteData() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			SortedSet<String> setLearningNamesList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from learning_plan_details ");
	    	if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" where is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" where is_close = true ");
			}
	    	
    	    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and (upper(learning_plan_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setLearningNamesList.add(rs.getString("learning_plan_name"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setLearningNamesList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			
			request.setAttribute("sbDataLP", sbData.toString());
//			System.out.println("sbDataLP==>"+sbData);
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	public void getLearningReport(){
		
		List<List<String>> allLearningreport = new ArrayList<List<String>>();	
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
		UtilityFunctions uF = new UtilityFunctions();
	    try {	
	    	
	    	con=db.makeConnection(con);
	    	Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
	    	
			pst=con.prepareStatement("select * from learning_plan_details order by is_close, is_publish desc, entry_date desc");
			rst=pst.executeQuery();
			int count=0;
			StringBuilder sbIcons = new StringBuilder();
			while(rst.next()) {
				
				count++;
				sbIcons.replace(0, sbIcons.length(), "");
				
				List<String> learningInfo =new ArrayList<String>(); 
				
//				List<String> learnersList =new ArrayList<String>();
				List<String> learnersList = getAppendData(con, rst.getString("learner_ids"));
				
				learningInfo.add(rst.getString("learning_plan_id"));//0
				
				learningInfo.add(uF.showData(learnersList != null && !learnersList.isEmpty() && learnersList.size() > 0 ? learnersList.get(0).toString() : "", ""));//1
				learningInfo.add(learnersList != null && !learnersList.isEmpty() && learnersList.size() > 1 ? learnersList.get(1) : "0");//2
				int ongoingEmpCount = getLearningPlanEmpCount(con, rst.getString("learning_plan_id"));
				int finalCount = getLearningPlanFinalEmpCount(con, rst.getString("learning_plan_id"));

				int pendingCount = uF.parseToInt(learnersList != null && !learnersList.isEmpty() && learnersList.size() > 1 ? learnersList.get(1).toString() : "0") - ongoingEmpCount;
				int underOngoingCount = ongoingEmpCount - finalCount;

				learningInfo.add(""+pendingCount); // 3
				learningInfo.add(""+underOngoingCount); // 4
				learningInfo.add(""+finalCount); // 5
//				System.out.println("learningInfo ===> " + learningInfo);
				allLearningreport.add(learningInfo);
			}
			rst.close();
			pst.close();
			
		} catch (Exception e){
				e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		System.out.println("allLearningreport ===> " + allLearningreport);
		request.setAttribute("allLearningreport", allLearningreport);
	}

	public int getLearningPlanFinalEmpCount(Connection con, String lPlanId) {
	
		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			pst = con.prepareStatement("select count(distinct emp_id) as count, emp_id from learning_plan_finalize_details where learning_plan_id = ? group by emp_id");
			pst.setInt(1, uF.parseToInt(lPlanId));
			rst = pst.executeQuery();
	//		System.out.println("pst course_read_details ===> " + pst);
			while (rst.next()) {
				count += rst.getInt("count");
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rst !=null){
				try {
					rst.close();
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
		return count;
	}


	
	public int getLearningPlanEmpCount(Connection con, String lPlanId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			List<String> empIdList = new ArrayList<String>();
//			StringBuilder sbEmpIds = new StringBuilder();
			pst = con.prepareStatement("select count(distinct emp_id) as count, emp_id from course_read_details where learning_plan_id = ? group by emp_id");
			pst.setInt(1, uF.parseToInt(lPlanId));
			rst = pst.executeQuery();
//			System.out.println("pst course_read_details ===> " + pst);
			while (rst.next()) {
				if(!empIdList.contains(rst.getString("emp_id"))) {
				count += rst.getInt("count");
				empIdList.add(rst.getString("emp_id"));
//				sbEmpIds.append(rst.getString("emp_id")+",");
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select count(distinct emp_id) as count, tad.emp_id from training_attend_details tad where tad.learning_plan_id = ? " +
					"and tad.emp_id not in(select crd.emp_id from course_read_details crd where crd.learning_plan_id =? group by crd.emp_id) group by tad.emp_id");
			pst.setInt(1, uF.parseToInt(lPlanId));
			pst.setInt(2, uF.parseToInt(lPlanId));
			rst = pst.executeQuery();
//			System.out.println("pst training_attend_details ===> " + pst);
			while (rst.next()) {
				if(!empIdList.contains(rst.getString("emp_id"))) {
					count += rst.getInt("count");
					empIdList.add(rst.getString("emp_id"));
//					sbEmpIds.append(rst.getString("emp_id")+",");
					}
			}
			rst.close();
			pst.close();
			
			String empIds = getAppendData(empIdList);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(distinct emp_id) as count from assessment_question_answer where learning_plan_id = ? ");
			if(!empIds.equals("")){
				sbQuery.append("and emp_id not in("+ empIds +")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(lPlanId));
//			System.out.println("pst assessment_question_answer 1 ===> " + pst);
			rst = pst.executeQuery();
//			System.out.println("pst assessment_question_answer ===> " + pst);
			while (rst.next()) {
				count += rst.getInt("count");
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rst !=null){
				try {
					rst.close();
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
		return count;
	}
	
	public String getAppendData(List<String> strID) {
		StringBuilder sb = new StringBuilder();
		if (strID != null) {
			for (int i = 0; i < strID.size(); i++) {
				if (i == 0) {
					sb.append(strID.get(i));
				} else {
					sb.append("," + strID.get(i));
				}
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	
	private List<String> getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
		List<String> empList = new ArrayList<String>();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 21-July-2021 Note : Encryption
		if (strID != null && !strID.equals("")) {
			int flag = 0, empcnt = 0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			String[] temp = strID.split(",");
			//empcnt = temp.length - 1;
			for (int i = 0; i < temp.length; i++) {

				if (temp[i] != null && !temp[i].equals("")) {
					if (flag == 0) { //encryption.encrypt(temp[i])
						sb.append("<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 21-July-2021 Note : empId Encryption
					} else {
						sb.append(", " + "<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 21-July-2021 Note : empId Encryption
					}
					flag = 1;
					empcnt++;
				}
			}
			empList.add(sb.toString());
			empList.add(empcnt + "");
			// System.out.println("empList ========== >>>> "+empList.toString());
		}
		return empList;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getLearningPlanId() {
		return learningPlanId;
	}

	public void setLearningPlanId(String learningPlanId) {
		this.learningPlanId = learningPlanId;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

}