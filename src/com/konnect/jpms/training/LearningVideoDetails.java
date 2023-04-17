package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class LearningVideoDetails extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private int empId; 
	
	CommonFunctions CF=null;
	
	private String fromPage; 
	private String learningVideoId;
	private String lPlanId;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
//		System.out.println("LVD/42--lplanId="+lPlanId);
		getLearningVideoDetails();
		
		return SUCCESS;
	}
	
	
	private void getLearningVideoDetails() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF=new UtilityFunctions();
		ResultSet rst=null;
		
		try{
			con=db.makeConnection(con);
			
			pst=con.prepareStatement("select learning_plan_stage_name_id from learning_plan_stage_details where learning_plan_stage_name_id > 0 and learning_type='Video'");
			rst=pst.executeQuery();
			List<String> alAssignVideo = new ArrayList<String>();
			
			while(rst.next()){
				if(!alAssignVideo.contains(rst.getString("learning_plan_stage_name_id"))){
					alAssignVideo.add(rst.getString("learning_plan_stage_name_id"));
				}
			}
			request.setAttribute("alAssignVideo", alAssignVideo);
			rst.close();
			pst.close();
			
			List<List<String>> alVideoDetails=new ArrayList<List<String>>();
//			StringBuilder sbVideoData = new StringBuilder();

			pst=con.prepareStatement("select * from learning_video_details left join video_link_details using(video_link_id) where learning_video_id=?");
			pst.setInt(1, uF.parseToInt(getLearningVideoId()));
			rst=pst.executeQuery();
//			System.out.println("LVD/64--pst="+pst);
			while(rst.next()){
				
				List<String> alinner=new ArrayList<String>();
//				sbVideoData.replace(0, sbVideoData.length(), "");
				
				alinner.add(rst.getString("learning_video_id"));//0
				alinner.add(rst.getString("learning_video_title"));		//1	
				alinner.add(rst.getString("video_link_file"));   //2
				alinner.add(rst.getString("learning_video_description"));	//3
				alinner.add(rst.getString("video_name"));	//4
//				boolean statusFlag = checkVideoStatus(con, rst.getString("learning_video_id"));
				/*if(statusFlag == false) {
					sbVideoData.append("<a onclick=\"editVideo('"+rst.getString("learning_video_id")+"','LD')\" href=\"javascript:void(0)\" style=\"padding:0\"><i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i></a> ");
				}*/
				/*sbVideoData.append("<a onclick=\"editVideo('"+rst.getString("learning_video_id")+"','LD')\" href=\"javascript:void(0)\" style=\"padding:0\"><i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i></a> ");
				if(!alAssignVideo.contains(rst.getString("learning_video_id"))){
					sbVideoData.append("<a onclick=\"deleteVideo('<%=alInner.get(0) %>')\" href=\"javascript:void(0)\" class=\"del\" style=\"color:rgb(221, 0, 0);\"><i class=\"fa fa-trash\" aria-hidden=\"true\"></i></a>");
				}*/
				
				alVideoDetails.add(alinner);
			}
			rst.close();
			pst.close();
			
			List<List<String>> alSubVideoDetails=new ArrayList<List<String>>();
			pst=con.prepareStatement("select * from learning_subvideo_details left join video_link_details using(video_link_id) where learning_video_id=?");
			pst.setInt(1, uF.parseToInt(getLearningVideoId()));
			rst=pst.executeQuery();
//			System.out.println("LVD/64--pst="+pst);
			while(rst.next()){
				
				List<String> alinner=new ArrayList<String>();
//				sbVideoData.replace(0, sbVideoData.length(), "");
				
				alinner.add(rst.getString("learning_subvideo_id"));//0
				alinner.add(rst.getString("learning_subvideo_title"));		//1	
				alinner.add(rst.getString("video_link_file"));   //2
				alinner.add(rst.getString("learning_subvideo_description"));	//3
				alinner.add(rst.getString("video_name"));	//4
//				boolean statusFlag = checkVideoStatus(con, rst.getString("learning_video_id"));
				/*if(statusFlag == false) {
					sbVideoData.append("<a onclick=\"editVideo('"+rst.getString("learning_video_id")+"','LD')\" href=\"javascript:void(0)\" style=\"padding:0\"><i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i></a> ");
				}*/
				/*sbVideoData.append("<a onclick=\"editVideo('"+rst.getString("learning_video_id")+"','LD')\" href=\"javascript:void(0)\" style=\"padding:0\"><i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i></a> ");
				if(!alAssignVideo.contains(rst.getString("learning_video_id"))){
					sbVideoData.append("<a onclick=\"deleteVideo('<%=alInner.get(0) %>')\" href=\"javascript:void(0)\" class=\"del\" style=\"color:rgb(221, 0, 0);\"><i class=\"fa fa-trash\" aria-hidden=\"true\"></i></a>");
				}*/
				
				alSubVideoDetails.add(alinner);
			}
			rst.close();
			pst.close();
			//System.out.println("alVideoDetails =====> " + alVideoDetails);
			request.setAttribute("alVideoDetails", alVideoDetails);
			request.setAttribute("alSubVideoDetails", alSubVideoDetails);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
		
	}
	
	
	private boolean checkVideoStatus(Connection con, String videoId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean videoStatusFlag = false;
		try {
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Video' and learning_plan_stage_name_id = ?");
			pst.setInt(1, uF.parseToInt(videoId));
			rst = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while (rst.next()) {
				videoStatusFlag = true;
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
		
		return videoStatusFlag;
	}
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	
	public String getFromPage() {
		return fromPage;
	}
	
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
	public String getLearningVideoId() {
		return learningVideoId;
	}
	
	public void setLearningVideoId(String learningVideoId) {
		this.learningVideoId = learningVideoId;
	}


	public String getlPlanId() {
		return lPlanId;
	}


	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}
	

}
