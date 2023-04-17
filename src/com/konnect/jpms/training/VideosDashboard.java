package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class VideosDashboard extends ActionSupport implements ServletRequestAware, IStatements{
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;

	private int empId;
	private String fromPage; 
	private String strSearchJob;
	private String strVideoId;
	
	public String execute() {
		
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		viewVideoDetails();
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
//			System.out.println("VD/49--strUserType="+strUserType);
			getSearchAutoCompleteData();
		}
		return SUCCESS;
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
			
			SortedSet<String> setVideoDataList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from learning_video_details where learning_video_id > 0 ");
		    
    	    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and (upper(learning_video_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst search===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("learning_video_title")!=null){
					setVideoDataList.add(rs.getString("learning_video_title"));
				}
				
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setVideoDataList.iterator();
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
			
			request.setAttribute("sbDataV", sbData.toString());
//			System.out.println("sbDataV==>"+sbData);	
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void viewVideoDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF =new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			List<List<String>> alVideoDetails=new ArrayList<List<String>>();
			StringBuilder strQuery = new StringBuilder();
			Map<String, String> hmVideoName = CF.getVideoNameMap(con);
			/*strQuery.append("select count(learning_video_id) as cnt from learning_video_details");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				strQuery.append(" where (upper(learning_video_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			
			pst=con.prepareStatement(strQuery.toString());
//			System.out.println("pst=====>"+pst);
			rst=pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rst.next()) {
				proCnt = rst.getInt("cnt");
				proCount = rst.getInt("cnt")/15;
				if(rst.getInt("cnt")%15 != 0) {
					proCount++;
				}
			}
			rst.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");*/
			
//			strQuery = new StringBuilder();
			
			
			int i =0;
			strQuery = new StringBuilder();
			strQuery.append("select * from learning_video_details left join video_link_details using(video_link_id)");
		    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
		    	strQuery.append("where (upper(learning_video_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			strQuery.append(" order by learning_video_id desc"); 
			pst = con.prepareStatement(strQuery.toString());
//			System.out.println("VD/157--pst="+pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				
				List<String> alinner=new ArrayList<String>();
				boolean statusFlag = checkVideoStatus(con, rst.getString("learning_video_id"));;
				
				alinner.add(rst.getString("learning_video_id"));//0
//				System.out.println("VD/162--videoId="+rst.getString("learning_video_id"));
				alinner.add(rst.getString("learning_video_title"));	//1
				alinner.add(uF.showData(hmVideoName.get(rst.getString("video_link_id").trim().toString()), "-") );//2
				
				if(statusFlag == false) {
					alinner.add("<div style=\"float:left;border-left:4px solid #ff9a02;padding:10px;\" class=\"custom-legend pullout\"><div class=\"legend-info\"></div></div>");	//3
				} else {
					alinner.add("<div style=\"float:left;border-left:4px solid #15AA08;padding:10px;\" class=\"custom-legend approved\"><div class=\"legend-info\"></div></div>");	//3
				}
				
				if(i == 0) {
					setStrVideoId(rst.getString("learning_video_id"));
				}
				i++;
				alVideoDetails.add(alinner);
				
//				System.out.println("VD/168--alVideoDetails="+alVideoDetails);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("alVideoDetails", alVideoDetails);

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private boolean checkVideoStatus(Connection con, String VideoId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean courseStatusFlag = false;
		try {
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Video' and learning_plan_stage_name_id = ? and ? between from_date and to_date");
			pst.setInt(1, uF.parseToInt(VideoId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rst = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while (rst.next()) {
				courseStatusFlag = true;
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
		
		return courseStatusFlag;
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
    
	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getStrVideoId() {
		return strVideoId;
	}

	public void setStrVideoId(String strVideoId) {
		this.strVideoId = strVideoId;
	}

}
