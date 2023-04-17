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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OneOneReviewDiscussionDetails extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -4581306410273940249L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strUserType;
	String strUserTypeId;
	String strBaseUserTypeId;
	String strBaseUserType;
	
	CommonFunctions CF;
	
	private String dataType;
	private String proPage;
	private String minLimit;
	private String fromPage;
	private String currUserType;
	private String strEmpId;
	private String appId;
	private String appFreqId;
	private String strComment;
	private String userRating;
	private String operation;
	private String discussionId;
	private String strStartTime;
	private String strEndTime;
	private String strStartDate;
	private String strTotalTimeSpent;
	private String apStatus;
	
	public String execute() {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
//		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		
		request.setAttribute(PAGE, "/jsp/performance/OneOneReviewDiscussionDetails.jsp");
		request.setAttribute(TITLE, "One On One Discussion"); //TKRAs
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		UtilityFunctions uF = new UtilityFunctions();
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request); 
		request.setAttribute("hmFeatureStatus",hmFeatureStatus);
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		if(getOperation() != null && getOperation().equals("insert")){
			insertUserRatingComment(uF);
		} 
		
		if(getOperation() != null && getOperation().equals("update")){
			updateUserRatingComment(uF);
		}
		
		if(getOperation() != null && getOperation().equals("SO")){
			updateUsersignOffDiscussion(uF);
		}
		
		if(getStrEmpId() != null && uF.parseToInt(getStrEmpId()) > 0) {
			getEmpAppraisalDetails(uF);
			getOneOneDiscussionDetails(uF);
			getAppraisalFinalScore(uF);
		}
		
		return SUCCESS;
	}
	
	private void getEmpAppraisalDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, List<String>> hmAppraisalDetails = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from appraisal_details ad,appraisal_details_frequency adf where ad.appraisal_details_id=adf.appraisal_id " +
					" and adf.is_appraisal_publish=true and self_ids like '%,"+getStrEmpId().trim()+",%'" );
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> appraisalList = new ArrayList<String>();
				
				String memberName = "";
				if(rs.getString("usertype_member") != null) {
					List<String> memberList = Arrays.asList(rs.getString("usertype_member").split(","));
					for (int i = 0; i < memberList.size(); i++) {
						if (i == 0) {
							memberName += orientationMemberMp.get(memberList.get(i));
						} else {
							memberName += ", " + orientationMemberMp.get(memberList.get(i));
						}
					}
				}
				if(memberName == null || memberName.equals("null")) {
					memberName = "Anyone"; 
				}
				
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));//0
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));//1
				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )", ""));//2
				appraisalList.add(uF.showData(hmFrequency.get(rs.getString("frequency").trim()), ""));//3
				appraisalList.add(uF.showData(rs.getString("appraisal_type"), ""));//4
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));//5
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));//6
				appraisalList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//7
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//8
				appraisalList.add(""+uF.parseToBoolean(rs.getString("is_appraisal_close")));// 9
				appraisalList.add(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));// 10
				appraisalList.add(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));// 11
				appraisalList.add(uF.showData(rs.getString("appraisal_freq_id"), ""));//12
				hmAppraisalDetails.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"), appraisalList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAppraisalDetails", hmAppraisalDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getAppraisalFinalScore(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmAppraisalFinalScore = new HashMap<String, String>();
			pst = con.prepareStatement("select * from reviewer_feedback_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppraisalFinalScore.put(rs.getString("appraisal_id"), rs.getString("reviewer_marks"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAppraisalFinalScore", hmAppraisalFinalScore);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getOneOneDiscussionDetails(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String,String> hmAppWiseFreqId = new HashMap<String, String>();
			Map<String,List<String>> hmDiscussionDetails = new HashMap<String, List<String>>();
			
			/*pst = con.prepareStatement("SELECT  edd.* FROM emp_one_one_discussion_details edd INNER JOIN (SELECT  appraisal_id, MAX(user_entry_date) AS user_entry_date,MAX(emp_entry_date) AS emp_entry_date " +
					" FROM emp_one_one_discussion_details GROUP BY appraisal_id,emp_id ) maxedd ON edd.appraisal_id = maxedd.appraisal_id AND (edd.user_entry_date = maxedd.user_entry_date OR edd.emp_entry_date = maxedd.emp_entry_date) " +
					"and edd.emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			System.out.println("pst=="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_one_one_discussion_details_id"));		//0
//				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("user_rating"));								//1
				innerList.add(rs.getString("user_remark"));								//2
				innerList.add(uF.showData(rs.getString("emp_remark"), ""));				//3
				innerList.add(rs.getString("emp_sign_off"));				//4
				hmDiscussionDetails.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), innerList);
			}
			rs.close();
			pst.close();*/
			
			/*pst = con.prepareStatement("select appraisal_details_id,appraisal_freq_id from appraisal_details ad,appraisal_details_frequency adf where ad.appraisal_details_id=adf.appraisal_id " +
					" and adf.is_appraisal_publish=true and self_ids like '%,"+getStrEmpId().trim()+",%'" );
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppWiseFreqId.put(rs.getString("appraisal_details_id"), rs.getString("appraisal_freq_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,List<String>> hmDiscussionDetails = new HashMap<String, List<String>>();
			Iterator<String> itr = hmAppWiseFreqId.keySet().iterator();
			while(itr.hasNext()){
				String strId = itr.next();
				String StrFreqId = hmAppWiseFreqId.get(strId);
				pst = con.prepareStatement("select * from emp_one_one_discussion_details where emp_id=? and user_id=? and user_type_id=? and appraisal_id=? and appraisal_freq_id=? order by emp_one_one_discussion_details_id desc limit 1");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(strUserTypeId));
				pst.setInt(4, uF.parseToInt(strId));
				pst.setInt(5, uF.parseToInt(StrFreqId));
//				System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("emp_one_one_discussion_details_id"));		//0
//					innerList.add(rs.getString("emp_id"));
					innerList.add(rs.getString("user_rating"));								//1
					innerList.add(rs.getString("user_remark"));								//2
					innerList.add(hmEmpName.get(rs.getString("user_id")));								//2
					hmDiscussionDetails.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), innerList);
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from emp_one_one_discussion_details where emp_id=? and user_id=? and user_id not in("+strSessionEmpId+") " +
						"and appraisal_id=? and appraisal_freq_id=? order by emp_one_one_discussion_details_id desc limit 1");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(getStrEmpId()));
				pst.setInt(3, uF.parseToInt(strId));
				pst.setInt(4, uF.parseToInt(StrFreqId));
//				System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					System.out.println(rs.getString("emp_entry_date"));
					innerList.add(rs.getString("emp_one_one_discussion_details_id"));
					innerList.add(rs.getString("emp_remark"));								//2
					innerList.add(hmEmpName.get(rs.getString("user_id")));								//2
					hmDiscussionDetails.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_id"), innerList);
				}
				rs.close();
				pst.close();
			}*/
			
			pst = con.prepareStatement("select appraisal_details_id,appraisal_freq_id from appraisal_details ad,appraisal_details_frequency adf where ad.appraisal_details_id=adf.appraisal_id " +
					" and adf.is_appraisal_publish=true and self_ids like '%,"+getStrEmpId().trim()+",%'" );
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppWiseFreqId.put(rs.getString("appraisal_details_id"), rs.getString("appraisal_freq_id"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> itr = hmAppWiseFreqId.keySet().iterator();
			while(itr.hasNext()){
				String strId = itr.next();
				String StrFreqId = hmAppWiseFreqId.get(strId);
				pst = con.prepareStatement("select * from emp_one_one_discussion_details where emp_id=? and appraisal_id=? and appraisal_freq_id=? order by emp_one_one_discussion_details_id desc limit 1");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(strId));
				pst.setInt(3, uF.parseToInt(StrFreqId));
				System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("emp_one_one_discussion_details_id"));		//0
//					innerList.add(rs.getString("emp_id"));
					innerList.add(rs.getString("user_rating"));								//1
					innerList.add(rs.getString("user_remark"));								//2
					innerList.add(uF.showData(rs.getString("emp_remark"), ""));				//3
					innerList.add(rs.getString("emp_sign_off"));				//4
					innerList.add(rs.getString("user_approval"));				//5
					innerList.add(rs.getString("start_time"));				//6
					innerList.add(rs.getString("end_time"));				//7
					innerList.add(uF.getDateFormat(rs.getString("discussion_date"), DBDATE, DATE_FORMAT));				//8
					innerList.add(rs.getString("total_time_spent"));				//9
					hmDiscussionDetails.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), innerList);
				}
				rs.close();
				pst.close();
			}
			
			
			request.setAttribute("hmDiscussionDetails", hmDiscussionDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void insertUserRatingComment(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			double marks = uF.parseToDouble(getUserRating()) * 100 / 5;
			
			if(getStrComment() !=null && !getStrComment().equals("")){
				pst = con.prepareStatement("insert into emp_one_one_discussion_details(emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id," +
						"user_rating,user_remark,user_entry_date,start_time,end_time,discussion_date,total_time_spent) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?)" );
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(strUserTypeId));
				pst.setInt(4, uF.parseToInt(getAppId()));
				pst.setInt(5, uF.parseToInt(getAppFreqId()));
				pst.setDouble(6, marks);
				pst.setString(7, getStrComment());
				pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setTime(9, uF.getTimeFormat(getStrStartTime(),TIME_FORMAT));
				pst.setTime(10, uF.getTimeFormat(getStrEndTime(),TIME_FORMAT));
				pst.setDate(11, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDouble(12, uF.parseToDouble(getStrTotalTimeSpent()));
				pst.executeUpdate();
				pst.close();
			}
			session.setAttribute(MESSAGE, SUCCESSM+"Request submitted successfully!"+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void updateUserRatingComment(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			double marks = uF.parseToDouble(getUserRating()) * 100 / 5;
			
			/*if(getStrComment() !=null && !getStrComment().equals("")){
				pst = con.prepareStatement("update emp_one_one_discussion_details set user_rating=?,user_remark=? where " +
						"emp_one_one_discussion_details_id=? and appraisal_id=? and appraisal_freq_id=? and emp_id=?" );
				pst.setDouble(1, marks);
				pst.setString(2, getStrComment());
				pst.setInt(3, uF.parseToInt(getDiscussionId()));
				pst.setInt(4, uF.parseToInt(getAppId()));
				pst.setInt(5, uF.parseToInt(getAppFreqId()));
				pst.setInt(6, uF.parseToInt(getStrEmpId()));
				pst.executeUpdate();
				pst.close();
			}
			*/
			
			if(getStrComment() !=null && !getStrComment().equals("")){
				pst = con.prepareStatement("update emp_one_one_discussion_details set user_rating=?,user_remark=?, start_time=?, end_time=?, discussion_date=? ,total_time_spent=? where " +
						"emp_one_one_discussion_details_id=? and appraisal_id=? and appraisal_freq_id=? and emp_id=?" );
				pst.setDouble(1, marks);
				pst.setString(2, getStrComment());
				pst.setTime(3, uF.getTimeFormat(getStrStartTime(),TIME_FORMAT));
				pst.setTime(4, uF.getTimeFormat(getStrEndTime(),TIME_FORMAT));
				pst.setDate(5, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDouble(6, uF.parseToDouble(getStrTotalTimeSpent()));
				pst.setInt(7, uF.parseToInt(getDiscussionId()));
				pst.setInt(8, uF.parseToInt(getAppId()));
				pst.setInt(9, uF.parseToInt(getAppFreqId()));
				pst.setInt(10, uF.parseToInt(getStrEmpId()));
				pst.executeUpdate();
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
	
	private void updateUsersignOffDiscussion(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbquery = new StringBuilder();
//			pst = con.prepareStatement("update emp_one_one_discussion_details set user_approval=true where emp_id=? and appraisal_id=? and appraisal_freq_id=? and emp_one_one_discussion_details_id=? " );
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			pst.setInt(2, uF.parseToInt(getAppId()));
//			pst.setInt(3, uF.parseToInt(getAppFreqId()));
//			pst.setInt(4, uF.parseToInt(getDiscussionId()));
//			pst = con.prepareStatement("update emp_one_one_discussion_details set user_approval=?, user_remark=? where appraisal_id=? and appraisal_freq_id=? and emp_one_one_discussion_details_id=? " );
			sbquery.append("update emp_one_one_discussion_details set user_approval=?, user_remark=? ");
			if(getStrStartTime() !=null && !getStrStartTime().equals("")){
				sbquery.append(",start_time='"+uF.getTimeFormat(getStrStartTime(),TIME_FORMAT)+"'");
			}
			if(getStrEndTime() !=null && !getStrEndTime().equals("")){
				sbquery.append(",end_time='"+uF.getTimeFormat(getStrEndTime(),TIME_FORMAT)+"'");
			}
			if(getStrStartDate() !=null && !getStrStartDate().equals("")){
				sbquery.append(",discussion_date='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"'");
			}
			if(getStrTotalTimeSpent() !=null && !getStrTotalTimeSpent().equals("") && uF.parseToDouble(getStrTotalTimeSpent())>0){
				sbquery.append(",total_time_spent="+uF.parseToDouble(getStrTotalTimeSpent()));
			}
			sbquery.append("where appraisal_id=? and appraisal_freq_id=? and emp_one_one_discussion_details_id=?");
			pst = con.prepareStatement(sbquery.toString());
			pst.setBoolean(1, uF.parseToBoolean(getApStatus()));
			pst.setString(2, getStrComment());
			pst.setInt(3, uF.parseToInt(getAppId()));
			pst.setInt(4, uF.parseToInt(getAppFreqId()));
			pst.setInt(5, uF.parseToInt(getDiscussionId()));
			System.out.println("pst="+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x==0){
				pst = con.prepareStatement("insert into emp_one_one_discussion_details(emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id," +
						"user_remark,user_entry_date,start_time,end_time,discussion_date,total_time_spent,user_approval) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?)" );
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(strUserTypeId));
				pst.setInt(4, uF.parseToInt(getAppId()));
				pst.setInt(5, uF.parseToInt(getAppFreqId()));
				pst.setString(6, getStrComment());
				pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setTime(8, uF.getTimeFormat(getStrStartTime(),TIME_FORMAT));
				pst.setTime(9, uF.getTimeFormat(getStrEndTime(),TIME_FORMAT));
				pst.setDate(10, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDouble(11, uF.parseToDouble(getStrTotalTimeSpent()));
				pst.setBoolean(12, uF.parseToBoolean(getApStatus()));
				pst.executeUpdate();
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
	
	private Map<String, String> getOrientationValue( Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		Map<String, String> hmorientationMembers = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientationMp", orientationMp);
			
			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				//hmorientationMembers.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				hmorientationMembers.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmorientationMembers", hmorientationMembers);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
	}
	
	private Map<String, String> getOrientationMember( Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
	
	HttpServletRequest request;

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

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getStrComment() {
		return strComment;
	}

	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public String getUserRating() {
		return userRating;
	}

	public void setUserRating(String userRating) {
		this.userRating = userRating;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(String discussionId) {
		this.discussionId = discussionId;
	}

	public String getStrStartTime() {
		return strStartTime;
	}

	public void setStrStartTime(String strStartTime) {
		this.strStartTime = strStartTime;
	}

	public String getStrEndTime() {
		return strEndTime;
	}

	public void setStrEndTime(String strEndTime) {
		this.strEndTime = strEndTime;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrTotalTimeSpent() {
		return strTotalTimeSpent;
	}

	public void setStrTotalTimeSpent(String strTotalTimeSpent) {
		this.strTotalTimeSpent = strTotalTimeSpent;
	}

	public String getApStatus() {
		return apStatus;
	}

	public void setApStatus(String apStatus) {
		this.apStatus = apStatus;
	}
	
}
