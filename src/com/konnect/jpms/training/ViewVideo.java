package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewVideo extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(CourseRead.class);
	
	private String videoId;
	private String lPlanId;
	private String videoName;
	private String dataType;
	private String subVideoId;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
		return LOGIN;
		}
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/training/ViewVideo.jsp");
		request.setAttribute(TITLE, "View Video");
		
		if(dataType.equalsIgnoreCase("I")){
			viewVideo();
		}
		
		
		return SUCCESS;

	}
	
	private void getVideoNameByID(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		try {
			pst = con.prepareStatement("select learning_video_id,learning_video_title from learning_video_details where learning_video_id = ?");
			pst.setInt(1, uF.parseToInt(videoId));
			rst = pst.executeQuery();
			while(rst.next()) {
				setVideoName(rst.getString("learning_video_title"));
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void viewVideo() {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst=null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con=db.makeConnection(con);
			getVideoNameByID(con, uF);
			boolean flag = false;
			String viewedVideoCount = null;
			pst = con.prepareStatement("select * from learning_video_seen_details where emp_id=? and learning_plan_id=? and learning_video_id=? and learning_video_seen_status=1");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(lPlanId));
			pst.setInt(3, uF.parseToInt(videoId));
			rst = pst.executeQuery();
			while(rst.next()) {
				flag = true;
				viewedVideoCount = rst.getString("learning_video_seen_count");
			}
			rst.close();
			pst.close();
			if(viewedVideoCount == null){
				viewedVideoCount = getVideoId()+",";
			}
//			System.out.println("VV/102--videoId="+getVideoId());
			
			if(!flag){
				pst = con.prepareStatement("insert into learning_video_seen_details(emp_id,learning_plan_id,learning_video_id,learning_video_seen_status,added_by,entry_date,learning_video_seen_count) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(lPlanId));
				pst.setInt(3, uF.parseToInt(videoId));
				pst.setInt(4, uF.parseToInt("1"));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setString(7, viewedVideoCount);
				pst.executeUpdate();
				pst.close();
			} else {
				StringBuilder newViewedVideoCount = new StringBuilder();
				if(!viewedVideoCount.contains(subVideoId)){
					
					newViewedVideoCount.append(viewedVideoCount+subVideoId+",");
					
					pst = con.prepareStatement("update learning_video_seen_details set learning_video_seen_count=? where emp_id=? and learning_plan_id=? and learning_video_id=?");
					pst.setString(1, newViewedVideoCount.toString());
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setInt(3, uF.parseToInt(lPlanId));
					pst.setInt(4, uF.parseToInt(videoId));
					pst.execute();
					pst.close();
				}
			}
			
			
//			request.setAttribute("alLiveLearnings", alLiveLearnings);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	
	public String getVideoId() {
		return videoId;
	}
	
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	
	public String getlPlanId() {
		return lPlanId;
	}
	
	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}
	
	public String getVideoName() {
		return videoName;
	}
	
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSubVideoId() {
		return subVideoId;
	}

	public void setSubVideoId(String subVideoId) {
		this.subVideoId = subVideoId;
	}
	
	
}
