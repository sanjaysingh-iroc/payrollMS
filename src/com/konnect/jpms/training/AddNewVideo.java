package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillVideoLink;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddNewVideo extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	String videoTitle;
	String videoDescription;
	String videoLinkName;
	String videoId;
	
	String videoName;
	String videoLink;
	

	private List<FillVideoLink> videoLinkList;
	private String operation;
	private String fromPage;

	CommonFunctions CF = null;
	
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		String strOperation = request.getParameter("operation");
//		System.out.println("ANV/56--getOperation="+getOperation());
//		System.out.println("ANV/57--strOperation="+strOperation);
//		System.out.println("ANV/59--videoLinkName"+getVideoLinkName());
		
		request.setAttribute(PAGE, "/jsp/training/AddNewVideo.jsp");
		
		if (strOperation != null && strOperation.equalsIgnoreCase("E")){
			request.setAttribute(TITLE, "Edit Learning Video");
		}else {
			request.setAttribute(TITLE, "Add Learning Video");
		}
		
		String strID = request.getParameter("ID");
		
//		videoLinkList = new FillVideoLink(request).fillVideoLink();
//		videoLinkList.add(new FillVideoLink("0","Other"));
		
		loadVideoData();
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		/*System.out.println("ANV/77--videoId="+getVideoId());
		System.out.println("ANV/78--subVideoTitle="+getSubVideoTitle());*/
		
		if (strOperation != null && strOperation.equalsIgnoreCase("A")) {
			insertData(uF);
			return SUCCESS;
		}else if (strOperation != null && strOperation.equalsIgnoreCase("E")) {
			//updateData(strID,uF);
			loadFilledData(strID);
		}else if (strOperation != null && strOperation.equalsIgnoreCase("D")) {
			return deleteVideo(strID);
		}else if(getOperation() != null && getOperation().equalsIgnoreCase("U")){
			updateData(strID,uF);
			return SUCCESS;
		}
		
		if(strOperation == null){
			setOperation("A");
		}
		
		/*if(getFromPage() == null){
			setFromPage("LD");
		}*/
		
		return LOAD;
	}
	
	private void insertData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			
			if(getVideoLink() != null && !getVideoLink().trim().equals("")){
				pst = con.prepareStatement("insert into video_link_details(video_name,video_link_file)values(?,?)");
				pst.setString(1, getVideoName());
				pst.setString(2, getVideoLink());
				pst.executeUpdate();
				pst.close();
				
				int strVideoId = 0;
				pst = con.prepareStatement("select max(video_link_id) as video_link_id from video_link_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					strVideoId = rst.getInt("video_link_id");
				}
				rst.close();
				pst.close();
				
				//System.out.println("ANV/127--strVideoId="+strVideoId);
				
				pst = con.prepareStatement("insert into learning_video_details(learning_video_title,learning_video_description,video_link_id" +
											",added_by,entry_date)values(?,?,?, ?,?)");
				pst.setString(1, getVideoTitle());
				pst.setString(2, getVideoDescription());
				pst.setInt(3, strVideoId);
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//				System.out.println("ANV/101--pst==>"+pst);
				pst.executeUpdate();
				pst.close();

			}else if(getVideoLinkName() != null && !getVideoLinkName().trim().equals("")){
				pst = con.prepareStatement("insert into learning_video_details(learning_video_title,learning_video_description,video_link_id" +
											",added_by,entry_date)values(?,?,?, ?,?)");
				pst.setString(1, getVideoTitle());
				pst.setString(2, getVideoDescription());
				pst.setInt(3, uF.parseToInt(getVideoLinkName()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//				System.out.println("ANV/151--pst==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
			int newlearningVideoId = 0;
			pst = con.prepareStatement("select max(learning_video_id) as learning_video_id from learning_video_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				newlearningVideoId = rst.getInt("learning_video_id");
			}
			rst.close();
			pst.close();
			
//			System.out.println("ANV/166--newlearningVideoId="+newlearningVideoId);
//			System.out.println("ANV/167--getSubVideoTitle="+getSubVideoTitle().toString());
			if(getSubVideoTitle() != null && getSubVideoTitle().length != 0){
				for(int i=0; i<getSubVideoTitle().length; i++){
//					System.out.println("ANV/170--getSubVideoTitle="+getSubVideoTitle()[i]);
					if(getSubVideoTitle()[i] != null && !getSubVideoTitle()[i].trim().equals("") && !getSubVideoTitle()[i].equalsIgnoreCase("null")) {
						if(getSubVideoLink()[i] != null && !getSubVideoLink()[i].trim().equals("")){
							pst = con.prepareStatement("insert into video_link_details(video_name,video_link_file)values(?,?)");
							pst.setString(1, getSubVideoName()[i]);
							pst.setString(2, getSubVideoLink()[i]);
							pst.executeUpdate();
							pst.close();
							
							int newStrVideoId = 0;
							pst = con.prepareStatement("select max(video_link_id) as video_link_id from video_link_details");
							rst = pst.executeQuery();
							while (rst.next()) {
								newStrVideoId = rst.getInt("video_link_id");
							}
							rst.close();
							pst.close();
							
							pst = con.prepareStatement("insert into learning_subvideo_details(learning_subvideo_title,learning_subvideo_description,video_link_id" +
														",learning_video_id,added_by,entry_date)values(?,?,?, ?,?,?)");
							pst.setString(1, getSubVideoTitle()[i]);
							pst.setString(2, getSubVideoDescription()[i]);
							pst.setInt(3, newStrVideoId);
							pst.setInt(4, newlearningVideoId);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
							//System.out.println("ANV/101--pst==>"+pst);
							pst.executeUpdate();
							pst.close();
						}else if(getSubVideoLinkName()[i] != null && !getSubVideoLinkName()[i].trim().equals("")){
//							System.out.println("ANV/200--getSubVideoLinkName()[i]="+getSubVideoLinkName()[i]);
							pst = con.prepareStatement("insert into learning_subvideo_details(learning_subvideo_title,learning_subvideo_description,video_link_id" +
														",learning_video_id,added_by,entry_date)values(?,?,?, ?,?,?)");
							pst.setString(1, getSubVideoTitle()[i]);
							pst.setString(2, getSubVideoDescription()[i]);
							pst.setInt(3, uF.parseToInt(getSubVideoLinkName()[i]));
							pst.setInt(4, newlearningVideoId);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//							System.out.println("ANV/208--pst==>"+pst);
							pst.executeUpdate();
							pst.close();
						}
					}
				}
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}

	}
	
	private void updateData(String strID,UtilityFunctions uF) {
//		System.out.println("ANV/123--inside edit");
		Connection con = null;
		Database db = new Database();
		PreparedStatement pst = null;
		ResultSet rst = null;
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			System.out.println("ANV/131--getVideoLinkName="+getVideoLinkName());
			
			if(getVideoLink() != null && !getVideoLink().trim().equals("")){
				pst = con.prepareStatement("insert into video_link_details(video_name,video_link_file)values(?,?)");
				pst.setString(1, getVideoName());
				pst.setString(2, getVideoLink());
				pst.executeUpdate();
				pst.close();
				
				int strVideoId = 0;
				pst = con.prepareStatement("select max(video_link_id) as video_link_id from video_link_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					strVideoId = rst.getInt("video_link_id");
				}
				rst.close();
				pst.close();
				
				pst = con.prepareStatement("update learning_video_details set learning_video_title=?,learning_video_description=?,video_link_id=?" +
											",updated_by=?,update_date=? where learning_video_id=?");
				pst.setString(1, getVideoTitle());
				pst.setString(2, getVideoDescription());
				pst.setInt(3, strVideoId);
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(6, uF.parseToInt(getVideoId()));
	//			System.out.println("ANV/141--videoId="+getVideoId());
	//			System.out.println("ANV/142--pst=="+pst);
				pst.execute();
				pst.close();
			}else{
				pst = con.prepareStatement("update learning_video_details set learning_video_title=?,learning_video_description=?,video_link_id=?" +
											",updated_by=?,update_date=? where learning_video_id=?");
				pst.setString(1, getVideoTitle());
				pst.setString(2, getVideoDescription());
				pst.setInt(3, uF.parseToInt(getVideoLinkName()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(6, uF.parseToInt(getVideoId()));
//				System.out.println("ANV/210--videoId="+getVideoId());
//				System.out.println("ANV/211--strID="+strID);
//				System.out.println("ANV/142--pst=="+pst);
				pst.execute();
				pst.close();
			}
			
			String[] subVideoId = request.getParameterValues("subVideoId");
			
			/*if(getSubVideoTitle() != null && getSubVideoTitle().length != 0){
				for(int i=0; i<getSubVideoTitle().length; i++){*/
			/*if(subVideoId != null && subVideoId.length != 0){
				for(int i=0; i<subVideoId.length; i++){
					if(subVideoId[i] != null){
						System.out.println("subVideoId="+subVideoId[i]);
						if(getSubVideoLink() != null && getSubVideoLink()[i] != null && !getSubVideoLink()[i].trim().equals("")){
							pst = con.prepareStatement("insert into video_link_details(video_name,video_link_file)values(?,?)");
							pst.setString(1, getSubVideoName()[i]);
							pst.setString(2, getSubVideoLink()[i]);
							pst.executeUpdate();
							pst.close();
							
							int strSubVideoId = 0;
							pst = con.prepareStatement("select max(video_link_id) as video_link_id from video_link_details");
							rst = pst.executeQuery();
							while (rst.next()) {
								strSubVideoId = rst.getInt("video_link_id");
							}
							rst.close();
							pst.close();
							
							pst = con.prepareStatement("update learning_subvideo_details set learning_subvideo_title=?,learning_subvideo_description=?,video_link_id=?" +
														",updated_by=?,update_date=? where learning_subvideo_id=? and learning_video_id=?");
							pst.setString(1, getSubVideoTitle()[i]);
							pst.setString(2, getSubVideoDescription()[i]);
							pst.setInt(3, strSubVideoId);
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(6, uF.parseToInt(subVideoId[i]));
							pst.setInt(7, uF.parseToInt(getVideoId()));
//							System.out.println("ANV/141--videoId="+getVideoId());
//							System.out.println("ANV/142--pst=="+pst);
							pst.execute();
							pst.close();
						} else {
							pst = con.prepareStatement("update learning_subvideo_details set learning_subvideo_title=?,learning_subvideo_description=?,video_link_id=?" +
															",updated_by=?,update_date=? where learning_video_id=? and learning_subvideo_id=?");
							pst.setString(1, getSubVideoTitle()[i]);
							pst.setString(2, getSubVideoDescription()[i]);
							pst.setInt(3, uF.parseToInt(getSubVideoLinkName()[i]));
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(6, uF.parseToInt(getVideoId()));
							pst.setInt(7, uF.parseToInt(subVideoId[i]));
//							System.out.println("ANV/210--videoId="+getVideoId());
//							System.out.println("ANV/211--strID="+strID);
//							System.out.println("ANV/142--pst=="+pst);
							pst.execute();
							pst.close();
							
						}
					}
				}
			}*/
			
			pst = con.prepareStatement("delete from learning_subvideo_details where learning_video_id=?");
			pst.setInt(1, uF.parseToInt(getVideoId()));
			pst.execute();
			pst.close();
			
			if(getSubVideoTitle() != null && getSubVideoTitle().length != 0){
				
				for(int i=0; i<getSubVideoTitle().length; i++){
//					System.out.println("ANV/170--getSubVideoTitle="+getSubVideoTitle()[i]);
					if(getSubVideoTitle()[i] != null && !getSubVideoTitle()[i].trim().equals("") && !getSubVideoTitle()[i].equalsIgnoreCase("null")) {
						if(getSubVideoLink()[i] != null && !getSubVideoLink()[i].trim().equals("")){
							pst = con.prepareStatement("insert into video_link_details(video_name,video_link_file)values(?,?)");
							pst.setString(1, getSubVideoName()[i]);
							pst.setString(2, getSubVideoLink()[i]);
							pst.executeUpdate();
							pst.close();
							
							int newStrVideoId = 0;
							pst = con.prepareStatement("select max(video_link_id) as video_link_id from video_link_details");
							rst = pst.executeQuery();
							while (rst.next()) {
								newStrVideoId = rst.getInt("video_link_id");
							}
							rst.close();
							pst.close();
							
							pst = con.prepareStatement("insert into learning_subvideo_details(learning_subvideo_title,learning_subvideo_description,video_link_id" +
														",learning_video_id,added_by,entry_date)values(?,?,?, ?,?,?)");
							pst.setString(1, getSubVideoTitle()[i]);
							pst.setString(2, getSubVideoDescription()[i]);
							pst.setInt(3, newStrVideoId);
							pst.setInt(4, uF.parseToInt(getVideoId()));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
							//System.out.println("ANV/101--pst==>"+pst);
							pst.executeUpdate();
							pst.close();
						}else if(getSubVideoLinkName()[i] != null && !getSubVideoLinkName()[i].trim().equals("")){
//							System.out.println("ANV/200--getSubVideoLinkName()[i]="+getSubVideoLinkName()[i]);
							pst = con.prepareStatement("insert into learning_subvideo_details(learning_subvideo_title,learning_subvideo_description,video_link_id" +
														",learning_video_id,added_by,entry_date)values(?,?,?, ?,?,?)");
							pst.setString(1, getSubVideoTitle()[i]);
							pst.setString(2, getSubVideoDescription()[i]);
							pst.setInt(3, uF.parseToInt(getSubVideoLinkName()[i]));
							pst.setInt(4, uF.parseToInt(getVideoId()));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//							System.out.println("ANV/208--pst==>"+pst);
							pst.executeUpdate();
							pst.close();
						}
					}
				}
			}
			//setVideoId(strID);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
		
	}
	
	
	private void loadFilledData(String strID) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> alVideoDetails = new ArrayList<List<String>>();
		List<List<String>> alSubVideoDetails = new ArrayList<List<String>>();
		
		try {
			con = db.makeConnection(con);
				pst = con.prepareStatement("select * from learning_video_details where learning_video_id=?");
				pst.setInt(1, uF.parseToInt(strID));

				rst = pst.executeQuery();
				while (rst.next()) {
					List<String> alInner = new ArrayList<String>();
					setVideoTitle(rst.getString("learning_video_title"));
					setVideoDescription(rst.getString("learning_video_description"));
					//setVideoLinkList(getVideoNameById(con, uF, rst.getString("video_link_id")));
//					setVideoLinkName(getVideoNameById(con, uF, rst.getString("video_link_id")));
					setVideoLinkName(rst.getString("video_link_id"));
//					System.out.println("VideoLinkName="+getVideoLinkName());
					setVideoId(strID);
					setOperation("U");
					request.setAttribute("strOperation1", getOperation());
					
					alInner.add(rst.getString("learning_video_id"));		//0
					alInner.add(rst.getString("learning_video_title"));		//1
					alInner.add(rst.getString("learning_video_description"));		//2
					alInner.add(rst.getString("video_link_id"));		//3
					
					alVideoDetails.add(alInner);
				}
				rst.close();
				pst.close();
				request.setAttribute("alVideoDetails", alVideoDetails);
				
				pst = con.prepareStatement("select * from learning_subvideo_details where learning_video_id=?");
				pst.setInt(1, uF.parseToInt(strID));
				rst = pst.executeQuery();
				while (rst.next()) {
					List<String> alInner = new ArrayList<String>();
					
					alInner.add(rst.getString("learning_subvideo_id"));		//0
					alInner.add(rst.getString("learning_subvideo_title"));		//1
					alInner.add(rst.getString("learning_subvideo_description"));		//2
					alInner.add(rst.getString("video_link_id"));		//3
					
					alSubVideoDetails.add(alInner);
					
				}
				rst.close();
				pst.close();
				request.setAttribute("alSubVideoDetails", alSubVideoDetails);
				
			}  catch (Exception e) {

			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String deleteVideo(String strID) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("delete from learning_subvideo_details where learning_video_id=?");
			pst.setInt(1, uF.parseToInt(strID));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from learning_video_details where learning_video_id=?");
			pst.setInt(1, uF.parseToInt(strID));
			pst.execute();
			pst.close();
			
			//session.setAttribute(MESSAGE, SUCCESSM+""+trainingName+" training plan has been deleted successfully."+END);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private void loadVideoData() {
		UtilityFunctions uF = new UtilityFunctions();
		videoLinkList = new FillVideoLink(request).fillVideoLink();
		//videoLinkList.add(new FillVideoLink("0","Other"));
		request.setAttribute("videoLinkList", videoLinkList);
		StringBuilder sbVideoList = new StringBuilder();
		
		/*sbVideoList.append("<table border='0' class='table table_no_border' style='width: 85%;'> <tr>"
				+"<td class='txtlabel' style='vertical-align: top; text-align: right' >Sub Title:<sup>*</sup></td>"
				+"<td> <input type=text name=subVideoTitle class='validateRequired form-control' style=width: 450px; required=true /></td></tr>"
				+"<tr><td class='txtlabel' style='vertical-align: top; text-align: right'>Video Description:<sup>*</sup></td>"
				+"<td><input type=text name=subVideoDescription class='validateRequired form-control' style='width: 450px;' required='true' /></td></tr>"
				+"<tr id=videoListTr><td class='txtlabel' style='vertical-align: top; text-align: right'>Video List:<sup>*</sup></td>"
				+"<td><span><select name=videoLinkName id=videoLinkName class='validateRequired form-control' onchange=addNewVideoLink(this.value,'add')>");*/
		
		sbVideoList.append("<option value=''> Select Video</option>");
		
		for(int i = 0; i < videoLinkList.size(); i++){
			sbVideoList.append("<option value=" + ((FillVideoLink) videoLinkList.get(i)).getVideoId()+ "> " + ((FillVideoLink) videoLinkList.get(i)).getVideoName()
					+ "</option>");
		}
		
		sbVideoList.append("<option value=0>Other</option>");
		sbVideoList.append("</select>"+"</span></td>");
		
		request.setAttribute("sbVideoList", sbVideoList.toString());
		
	}
	
	/*private List<FillVideoLink> getVideoNameById(Connection con, UtilityFunctions uF, String learningvideoId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		//String lVideoName = null;
		List<FillVideoLink> lVideoName = new ArrayList<FillVideoLink>();
		try {
				pst = con.prepareStatement("select video_name from video_link_details where video_link_id = ?");
				pst.setInt(1, uF.parseToInt(learningvideoId));
				rst = pst.executeQuery();
				while (rst.next()) {
					//lVideoName = rst.getString("video_name");
					lVideoName.add(new FillVideoLink(learningvideoId, rst.getString("video_name")));
				}
				rst.close();
				pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return lVideoName;
	}*/
	
	private String getVideoNameById(Connection con, UtilityFunctions uF, String learningvideoId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		String lVideoName = null;
//		List<FillVideoLink> lVideoName = new ArrayList<FillVideoLink>();
		try {
				pst = con.prepareStatement("select video_name from video_link_details where video_link_id = ?");
				pst.setInt(1, uF.parseToInt(learningvideoId));
				rst = pst.executeQuery();
				while (rst.next()) {
					lVideoName = rst.getString("video_name");
//					lVideoName.add(new FillVideoLink(learningvideoId, rst.getString("video_name")));
				}
				rst.close();
				pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return lVideoName;
	}
	
	String[] subVideoTitle;
	String[] subVideoDescription;
	String[] subVideoLinkName;
	String[] subVideoName;
	String[] subVideoLink;
//	String[] subVideoId;
	       
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public String getVideoDescription() {
		return videoDescription;
	}

	public void setVideoDescription(String videoDescription) {
		this.videoDescription = videoDescription;
	}

	public String getVideoLinkName() {
		return videoLinkName;
	}

	public void setVideoLinkName(String videoLinkName) {
		this.videoLinkName = videoLinkName;
	}

	public List<FillVideoLink> getVideoLinkList() {
		return videoLinkList;
	}

	public void setVideoLinkList(List<FillVideoLink> videoLinkList) {
		this.videoLinkList = videoLinkList;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

	public String[] getSubVideoTitle() {
		return subVideoTitle;
	}

	public void setSubVideoTitle(String[] subVideoTitle) {
		this.subVideoTitle = subVideoTitle;
	}

	public String[] getSubVideoDescription() {
		return subVideoDescription;
	}

	public void setSubVideoDescription(String[] subVideoDescription) {
		this.subVideoDescription = subVideoDescription;
	}

	public String[] getSubVideoLinkName() {
		return subVideoLinkName;
	}

	public void setSubVideoLinkName(String[] subVideoLinkName) {
		this.subVideoLinkName = subVideoLinkName;
	}

	public String[] getSubVideoName() {
		return subVideoName;
	}

	public void setSubVideoName(String[] subVideoName) {
		this.subVideoName = subVideoName;
	}

	public String[] getSubVideoLink() {
		return subVideoLink;
	}

	public void setSubVideoLink(String[] subVideoLink) {
		this.subVideoLink = subVideoLink;
	}

	/*public String[] getSubVideoId() {
		return subVideoId;
	}

	public void setSubVideoId(String[] subVideoId) {
		this.subVideoId = subVideoId;
	}*/
	

}
