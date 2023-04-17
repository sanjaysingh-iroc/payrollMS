package com.konnect.jpms.training;

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
import com.opensymphony.xwork2.ActionSupport;

public class LearningPlanSummary extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String strSessionEmpId = null;
	
	private String learningPlanId;
	private String type;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		getLearningReport();

		return LOAD;

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
	    	List<String> learningInfo =new ArrayList<String>(); 
			pst=con.prepareStatement("select * from learning_plan_details where learning_plan_id =?");
			pst.setInt(1, uF.parseToInt(getLearningPlanId()));
//			System.out.println("pst--------------"+pst);
			rst=pst.executeQuery();
			int count=0;
			StringBuilder sbIcons = new StringBuilder();
			while(rst.next()) {
				
				count++;
				sbIcons.replace(0, sbIcons.length(), "");
				List<String> learnersList = getAppendData(con, rst.getString("learner_ids"));
			
				learningInfo.add(rst.getString("learning_plan_id"));//0
				learningInfo.add(uF.showData(rst.getString("learning_plan_name"), "Not Available."));//1
				learningInfo.add(uF.showData(rst.getString("learning_plan_objective"), ""));//2
				
				if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("3")) {
					learningInfo.add("General");//3
				} else if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("2")) {
					learningInfo.add("Gap");//3
				} else if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("1")) {
					learningInfo.add("Induction");//3
				} else {
					learningInfo.add("");//3
				}
				
				learningInfo.add(getAttributeData(con, rst.getString("attribute_id")));//4
								
				learningInfo.add(uF.showData(learnersList != null && !learnersList.isEmpty() && learnersList.size() > 0 ? learnersList.get(0).toString() : "", ""));//5
				learningInfo.add(learnersList != null && !learnersList.isEmpty() && learnersList.size() > 1 ? learnersList.get(1) : "0");//6
				int ongoingEmpCount = getLearningPlanEmpCount(con, rst.getString("learning_plan_id"));
				int finalCount = getLearningPlanFinalEmpCount(con, rst.getString("learning_plan_id"));

				int pendingCount = uF.parseToInt(learnersList != null && !learnersList.isEmpty() && learnersList.size() > 1 ? learnersList.get(1).toString() : "0") - ongoingEmpCount;
				int underOngoingCount = ongoingEmpCount - finalCount;

				String learningPlanType = getLearningStageType(con, uF, rst.getInt("learning_plan_id"));
				
				/*if(getType()!=null && getType().equalsIgnoreCase("choose")) {
					sbIcons.append("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, You want to create learning from this template?')) window.location='CreateLearningFromTemplate.action?existID="+rst.getString("learning_plan_id")+"';\">Choose</a>");
				} else {
					if(learningPlanType != null && !learningPlanType.equals("Course")) {
						sbIcons.append("<a href=\"javascript:void(0);\" onclick=\"getLearningPlanStatus("+rst.getString("learning_plan_id")+")"+"\"><img src=\"images1/icons/icons/status_icon.png\" title=\"Status\" /></a>");
					}
				}*/
				if(uF.parseToBoolean(rst.getString("is_publish"))){
					sbIcons.append("<a href=\"javascript:void(0)\" onclick=\"getPublishLearningPlan('"+rst.getString("learning_plan_id")+"','1')\">"+
						"<img src=\"images1/icons/icons/publish_icon_b.png\" title=\"Published\" style=\"float:left;\"/></a>");	
				} else {
					sbIcons.append("<a href=\"javascript:void(0)\"onclick=\"getPublishLearningPlan('"+rst.getString("learning_plan_id")+"','1')\" >"+
						"<img src=\"images1/icons/icons/unpublish_icon_b.png\" title=\"Waiting to be publish\" /></a>");
				}
				
				if(!uF.parseToBoolean(rst.getString("is_close")) && !uF.parseToBoolean(rst.getString("is_publish"))) {
					sbIcons.append("&nbsp;&nbsp;<a href=\"javascript:void(0);\" onclick=\"editLearningPlan('"+rst.getString("learning_plan_id")+"')\"><i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i></a>"); //target=\"_new\"
				}
				
				if(!uF.parseToBoolean(rst.getString("is_publish"))){
					sbIcons.append("&nbsp;<a class=\"del\" href=\"javascript:void(0);\" onclick=\"deleteLearningPlan('"+rst.getString("learning_plan_id")+"')\" title=\"Delete Learning Plan\"><i class=\"fa fa-trash\" aria-hidden=\"true\"></i></a>");
				}
				if(!uF.parseToBoolean(rst.getString("is_close"))) {
					sbIcons.append("&nbsp;<a href=\"javascript:void(0);\" onclick=\"closeLPlan('"+rst.getString("learning_plan_id")+"','close')\" title=\"Close Learning Plan\"><i class=\"fa fa-times-circle-o\" aria-hidden=\"true\"></i></a>");
				} else {
					sbIcons.append("&nbsp;<a href=\"javascript:void(0);\" onclick=\"closeLPlan('"+rst.getString("learning_plan_id")+"','view')\" title=\"Close Learning Plan Reason\"><i class=\"fa fa-comment-o\" aria-hidden=\"true\"></a>");
				}
//				System.out.println("is_publish2==>"+rst.getString("is_publish"));
				
				learningInfo.add(sbIcons.toString());//7
				
				learningInfo.add(learningPlanType); // 8
				learningInfo.add(uF.showData(CF.getCertificateName(con, rst.getString("certificate_id")),"No Certificate")); // Certificate 9
				String startEndDate = getStartAndEndDate(con, uF, rst.getInt("learning_plan_id"));
				learningInfo.add(uF.showData(startEndDate, "-")); // Start &  End Date 10
				learningInfo.add(hmEmpName.get(rst.getString("added_by"))); //Created By 11
				learningInfo.add(""+pendingCount); // 12
				learningInfo.add(""+underOngoingCount); // 13
				learningInfo.add(rst.getString("certificate_id")); // 14
				learningInfo.add(rst.getString("is_publish")); // 15
				learningInfo.add(""+finalCount); // 16
			
			}
			rst.close();
			pst.close();
			request.setAttribute("learningInfo", learningInfo);
			
		} catch (Exception e){
				e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		
	}

	
private String getStartAndEndDate(Connection con, UtilityFunctions uF, int learningPlanId) {
	
	PreparedStatement pst = null;
	ResultSet rst=null;
	StringBuilder startEndDate = new StringBuilder();
	try {
		pst = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
		pst.setInt(1, Integer.parseInt(getLearningPlanId()));
		rst = pst.executeQuery();
	//	System.out.println("pst1 =====> " + pst1);
		String minFromDate = null, maxToDate = null; 
		while (rst.next()) {
			minFromDate = rst.getString("minDate");
			maxToDate = rst.getString("maxDate");
		}
		rst.close();
		pst.close();
		String strtDate = uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat());
		String endDate = uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat());
		startEndDate.append((strtDate.equals("-") ? "" : strtDate) +" - "+ (endDate.equals("-") ? "" : endDate));
		
	}catch(Exception e){
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
	return startEndDate.toString();
}



private String getLearningStageType(Connection con, UtilityFunctions uF, int learningPlanId) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		StringBuilder learningType = new StringBuilder();
		try {
			Map<String,String> hmwlocation = CF.getWLocationMap(con, null, null);
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ?");
			pst.setInt(1, Integer.parseInt(getLearningPlanId()));
			rst = pst.executeQuery();
			 List<String> learningTypeList = new ArrayList<String>();
			while(rst.next()) {
				learningTypeList.add(rst.getString("learning_type"));
			}
			rst.close();
			pst.close();
			
			int a=0,b=0,c=0;
			for (int i = 0; learningTypeList != null && !learningTypeList.isEmpty() && i < learningTypeList.size(); i++) {
				if(learningTypeList.get(i).equals("Training") && a == 0){
					a++;
				} else if(learningTypeList.get(i).equals("Course") && b == 0){
					b++;
				}  else if(learningTypeList.get(i).equals("Assessment") && c == 0){
					c++;
				} 
			}
				if(a == 1 && b == 0 && c == 0) {
					learningType.append("Training");
				} else if(a == 0 && b == 1 && c == 0) {
					learningType.append("Course");
				} else if(a == 0 && b == 0 && c == 1) {
					learningType.append("Assessment");
				} else if((a == 1 && b == 1 && c == 1) || (a == 1 && b == 1 && c == 0) || (a == 1 && b == 0 && c == 1) || (a == 0 && b == 1 && c == 1)) {
					learningType.append("Hybrid");
				}
				
				if(learningType.toString().equals("")){
					learningType.append("NA");
				}
				
		}catch(Exception e){
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
		return learningType.toString();
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

	private String getAttributeData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
//		List<String> empList = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			Map<String, String> hmAttributeName = new HashMap<String, String>(); 
			pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmAttributeName.put(rst.getString("arribute_id"), rst.getString("attribute_name"));
			}
			rst.close();
			pst.close();
			
			if (strID != null && !strID.equals("")) {
				int flag = 0;
				String[] temp = strID.split(",");
				for (int i = 0; i < temp.length; i++) {
					if (temp[i] != null && !temp[i].equals("")) {
						if (flag == 0) {
							sb.append(hmAttributeName.get(temp[i].trim()));
						} else {
							sb.append(", " + hmAttributeName.get(temp[i].trim()));
						}
						flag = 1;
					}
				}
			}
			
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
		
		return sb.toString();
	}
	
	private List<String> getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
		List<String> empList = new ArrayList<String>();
		if (strID != null && !strID.equals("")) {
			int flag = 0, empcnt = 0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			String[] temp = strID.split(",");
			//empcnt = temp.length - 1;
			for (int i = 0; i < temp.length; i++) {

				if (temp[i] != null && !temp[i].equals("")) {
					if (flag == 0) {
						sb.append("<a href=\"javascript:void(0)\" onclick=\"openEmpProfilePopup('"+temp[i]+ "')\">" + hmEmpName.get(temp[i].trim()) + "</a>");
					} else {
						sb.append(", " + "<a href=\"javascript:void(0)\" onclick=\"openEmpProfilePopup('"+temp[i]+ "')\">" + hmEmpName.get(temp[i].trim()) + "</a>");
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
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getLearningPlanId() {
		return learningPlanId;
	}

	public void setLearningPlanId(String learningPlanId) {
		this.learningPlanId = learningPlanId;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}

