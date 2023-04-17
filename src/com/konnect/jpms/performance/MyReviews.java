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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyReviews extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	UtilityFunctions uF = new UtilityFunctions();

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "My Reviews");
		request.setAttribute(PAGE, "/jsp/performance/MyReviews.jsp");

		getAppraisalReport();

		return LOAD;

	}

	public void getAppraisalReport(){
		
		List<List<String>> allAppraisalreport=new ArrayList<List<String>>();	
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
	    try {	
	    	con=db.makeConnection(con);
	    	Map<String,String> orientationMp= CF.getOrientationValue(con);
	    	Map<String, String> hmFrequency = new HashMap<String, String>();
	    	Map<String, String> hmAppraisalCount = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmFrequency.put(rst.getString("appraisal_frequency_id"), rst.getString("frequency_name"));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt, appraisal_id ,appraisal_freq_id from appraisal_final_sattlement group by appraisal_id,appraisal_freq_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmAppraisalCount.put(rst.getString("appraisal_id")+"_"+rst.getString("appraisal_freq_id"), rst.getString("cnt"));
			}
			rst.close();
			pst.close();
	    	
			pst=con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal.id "
					+" and (is_delete is null or is_delete = false ) and my_review_status = 1 and added_by = ? and self_ids like'%,"+strSessionEmpId+",%' order by is_appraisal_publish desc, freq_end_date,to_date");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst=pst.executeQuery();
			int count=0;
			String appFreqId = "";
			StringBuilder sbIcons = new StringBuilder();
			while(rst.next()){
				sbIcons.replace(0, sbIcons.length(), "");
				
				List<String> appraisal_info =new ArrayList<String>(); 
				appFreqId = rst.getString("appraisal_freq_id");
				List<String> empList =new ArrayList<String>();
				empList = getAppendData(con, rst.getString("self_ids"));
				
				appraisal_info.add(rst.getString("appraisal_details_id"));
				
				//System.out.println("empList ===> "+empList );
				if(uF.parseToBoolean(rst.getString("is_appraisal_publish"))){
					if(uF.parseToInt(hmAppraisalCount.get(rst.getString("appraisal_details_id")+"_"+appFreqId))== uF.parseToInt(empList.get(1))){
						 /*appraisal_info.add("<img src=\"images1/icons/approved.png\"/> <a target=\"_new\" href=\"MyReviewSummary.action?id="+rst.getString("appraisal_details_id")+"&type=\">"+rst.getString("appraisal_name")+"&appFreqId="+appFreqId+"</a>");*/
						appraisal_info.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i> <a target=\"_new\" href=\"MyReviewSummary.action?id="+rst.getString("appraisal_details_id")+"&type=\">"+rst.getString("appraisal_name")+"&appFreqId="+appFreqId+"</a>");
					}else{
						/*appraisal_info.add("<img src=\"images1/icons/pullout.png\"/> <a target=\"_new\" href=\"MyReviewSummary.action?id="+rst.getString("appraisal_details_id")+"&type=\">"+rst.getString("appraisal_name")+"&appFreqId="+appFreqId+"</a>");*/
						appraisal_info.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i> <a target=\"_new\" href=\"MyReviewSummary.action?id="+rst.getString("appraisal_details_id")+"&type=\">"+rst.getString("appraisal_name")+"&appFreqId="+appFreqId+"</a>");
					}
				}else{
					/*appraisal_info.add("<img src=\"images1/icons/denied.png\"/> <a target=\"_new\" href=\"MyReviewSummary.action?id="+rst.getString("appraisal_details_id")+"&appFreqId="+appFreqId+"&type=\">"+rst.getString("appraisal_name")+"</a>");*/
					appraisal_info.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i> <a target=\"_new\" href=\"MyReviewSummary.action?id="+rst.getString("appraisal_details_id")+"&appFreqId="+appFreqId+"&type=\">"+rst.getString("appraisal_name")+"</a>");
				}
				
				appraisal_info.add(rst.getString("appraisal_type"));
				appraisal_info.add(orientationMp.get(rst.getString("oriented_type"))+"&deg;");
				appraisal_info.add(uF.showData(hmFrequency.get(rst.getString("frequency")), ""));
				appraisal_info.add(uF.getDateFormat(rst.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));
				
				appraisal_info.add(uF.showData(empList != null && !empList.isEmpty() && empList.size() > 0 ? empList.get(0).toString() : "", ""));
				appraisal_info.add(empList != null && !empList.isEmpty() && empList.size() > 1 ? empList.get(1) : "");
				int queAnsEmpCount = getAppraisalEmpQueAnsCount(rst.getString("appraisal_details_id"),appFreqId);
				int finalcount = getAppraisalFinalCount(rst.getString("appraisal_details_id"),appFreqId);
				
				int pendingCount = uF.parseToInt(empList != null && !empList.isEmpty() && empList.size() > 1 ? empList.get(1).toString() : "0") - queAnsEmpCount;
				appraisal_info.add(""+pendingCount); 
				
				int underReviewCount = queAnsEmpCount - finalcount;
				appraisal_info.add(""+underReviewCount);
						
				if(empList != null && !empList.isEmpty() && empList.size() > 0){
					appraisal_info.add(""+finalcount);
				} else{
					appraisal_info.add("0");
				}
				
//				sbIcons.append("<a href=\"AppraisalSummary.action?id="+rst.getString("appraisal_details_id")+"&type="+getType()+"\"><img src=\"images1/icons/icons/summary_icon.png\" title=\"Summary\" /></a>");
				/*sbIcons.append("<a href=\"AppraisalPreview.action?id="+rst.getString("appraisal_details_id")+"&type="+getType()+"\"><img src=\"images1/icons/icons/summary_icon.png\" title=\"Preview\" /></a>");*/
				sbIcons.append("<a href=\"javascript: void(0)\" onclick=\"openAppraisalPreview('"+rst.getString("appraisal_details_id")+"','"+appFreqId+"')\" title=\"Preview\" ><i class=\"fa fa-eye\" aria-hidden=\"true\"></i></a>");
				
				if(getType()!=null && getType().equalsIgnoreCase("choose")){
					sbIcons.append("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, You want to create Review from this template?')) window.location='CreateAppraisalFromTemplate.action?existID="+rst.getString("appraisal_details_id")+"&appFreqId="+appFreqId+"';\">Choose</a>");
				}else{
					sbIcons.append("<a href=\"MyReviewStatus.action?id="+rst.getString("appraisal_details_id")+"&appFreqId="+appFreqId+"\"><img src=\"images1/icons/icons/summary_status_icon_b.png\" title=\"Status\" /></a>");
				}
				  
				if(uF.parseToBoolean(rst.getString("is_appraisal_publish"))){
					sbIcons.append("<div id=\"myDivM"+count+"\"  style=\"float:left\">"+
							"<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to unpublish this appraisal?'))"+
						"getContent('myDivM"+count+"','PublishAppraisal.action?id="+rst.getString("appraisal_details_id")+"&dcount="+count+"&appFreqId="+appFreqId+"');\" >"+
						"<i class=\"fa fa-toggle-on\" aria-hidden=\"true\" title=\"Published\"></i></a>"+ 
						"</div>");	
				}else{
					sbIcons.append("<div id=\"myDivM"+count+"\" style=\"float:left\">"+
						"<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to publish this appraisal?'))"+
						"getContent('myDivM"+count+"','PublishAppraisal.action?id="+rst.getString("appraisal_details_id")+"&dcount="+count+"&appFreqId="+appFreqId+"');\" >"+
						"<i class=\"fa fa-toggle-on\" aria-hidden=\"true\" title=\"Waiting for Publish\"></i></a>"+
						"</div>");
				}
				appraisal_info.add(sbIcons.toString());
				
				allAppraisalreport.add(appraisal_info);
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
		request.setAttribute("allAppraisalreport",allAppraisalreport);
	}

	
	
	public int getAppraisalEmpQueAnsCount(String appraisal_id,String appFreqId) {

		Connection con = null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(distinct emp_id) as count from appraisal_question_answer where appraisal_id = ? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(appraisal_id));
			pst.setInt(2, uF.parseToInt(appFreqId));
			rst = pst.executeQuery();
			while (rst.next()) {
				count = rst.getInt("count");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return count;
	}
	
	
	public int getAppraisalFinalCount(String appraisal_id , String appFreqId) {

		Connection con = null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(*) as count from appraisal_final_sattlement where appraisal_id = ? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(appraisal_id));
			pst.setInt(2, uF.parseToInt(appFreqId));
			rst = pst.executeQuery();
			while (rst.next()) {
				count = rst.getInt("count");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return count;
	}

	
	private List<String> getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
		List<String> empList = new ArrayList<String>();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
		if (strID != null && !strID.equals("")) {
			int flag = 0, empcnt = 0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			String[] temp = strID.split(",");
			empcnt = temp.length - 1;
			for (int i = 0; i < temp.length; i++) {

				if (temp[i] != null && !temp[i].equals("")) {
					if (flag == 0) { //encryption.encrypt(temp[i])
						sb.append("<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					} else {
						sb.append(", " + "<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					}
					flag = 1;
				}
			}
			empList.add(sb.toString());
			empList.add(empcnt + "");
			// System.out.println("empList ========== >>>> "+empList.toString());
		}
		return empList;
	}
	
	
	String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
