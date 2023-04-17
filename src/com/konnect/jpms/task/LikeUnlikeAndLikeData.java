package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LikeUnlikeAndLikeData extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	String likeUnlike;
	String postId;
	String type;
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF == null) {
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/Feeds.jsp");
		request.setAttribute(TITLE, "Feeds");
		
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("likeUnlike ===>> " + likeUnlike);
//		System.out.println("postId ===>> " + postId);
//		System.out.println("type ===>> " + type);
		likeUnlikeAndLikeData(uF);
		
		return SUCCESS;
	}
	

	private void likeUnlikeAndLikeData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			int likeCnt = 0;
			int lcnt = 0;
			String likeIds = null;
			String clientLikeIds = null;
			pst = con.prepareStatement("select likes,like_ids,client_like_ids from communication_1 where communication_id=?");
			pst.setInt(1, uF.parseToInt(getPostId()));
			rs= pst.executeQuery();
			while(rs.next()) {
				likeCnt = rs.getInt("likes");
				likeIds = rs.getString("like_ids");
				clientLikeIds = rs.getString("client_like_ids");
			}
			rs.close();
			pst.close();
			
			
			
			if(getLikeUnlike() != null && getLikeUnlike().equals("L")) {
				likeCnt++;
				
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					if(clientLikeIds != null && !clientLikeIds.equals("")) {
						clientLikeIds = clientLikeIds + strSessionEmpId + ",";
					} else {
						clientLikeIds = "," + strSessionEmpId + ",";
					}
					
				} else {
					if(likeIds != null && !likeIds.equals("")) {
						likeIds = likeIds + strSessionEmpId + ",";
					} else {
						likeIds = "," + strSessionEmpId + ",";
					}
					
				}
			} else if(getLikeUnlike() != null && getLikeUnlike().equals("UL")) {
				likeCnt--;
				
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					if(clientLikeIds != null && !clientLikeIds.equals("")) {
						clientLikeIds = clientLikeIds.replace(strSessionEmpId + ",", "");
					} else {
						clientLikeIds = "";
					}
					
				} else {
					if(likeIds != null && !likeIds.equals("")) {
						likeIds = likeIds.replace(strSessionEmpId + ",", "");
					} else {
						likeIds = "";
					}
					
				}
			}
			
			
			StringBuilder sbQue = new StringBuilder();
			sbQue.append("update communication_1 set likes = ?");
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				sbQue.append(",client_like_ids=?");
			} else {
				sbQue.append(",like_ids=?");
			}
			sbQue.append(" where communication_id=?");
			pst = con.prepareStatement(sbQue.toString());
			pst.setInt(1, likeCnt);
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				pst.setString(2, clientLikeIds);
			} else {
				pst.setString(2, likeIds);
			}
			pst.setInt(3, uF.parseToInt(getPostId()));
			pst.executeUpdate();
			pst.close();
			
			/*if(getType() != null && getType().equals("C")) {
				request.setAttribute("likeCnt", likeCnt+"");
			} else {
				request.setAttribute("likeCnt", likeCnt+" People like this.");
			}
			*/
			StringBuilder st = new StringBuilder();
			pst = con.prepareStatement("select likes,like_ids,client_like_ids from communication_1 where communication_id=?");
			pst.setInt(1, uF.parseToInt(getPostId()));
			rs= pst.executeQuery();
			while(rs.next()) {
				likeCnt = rs.getInt("likes");
				likeIds = rs.getString("like_ids");
				clientLikeIds = rs.getString("client_like_ids");
			}
			rs.close();
			pst.close();
			
			int likes = uF.parseToInt(uF.showData(likeCnt+"","0"));
			
			int clikeCount = 0;
			if(clientLikeIds!=null && !clientLikeIds.equals("") && !clientLikeIds.equals(",")){
				String[] clikes = clientLikeIds.split(",");
				int length = clikes.length;
				clikeCount = length-1;
			}
			
			//System.out.println(" lcnt==>"+(likes+clikeCount));
			lcnt = likes +clikeCount;
			if(lcnt>0){
				st.append("<a href=\"#\" style=\"color:gray;\" onclick=\"openLikesPopup('"+likeIds+"','"+clientLikeIds+"')\">"+lcnt+" People like this .</a>");
				
			}else{
				st.append(lcnt+" People like this. ");
			}
			
			StringBuilder st1 = new StringBuilder();
			if(getType() != null && getType().equals("C")) {
				
				if(lcnt>0){
					st1.append("<a href=\"#\" style=\"color:gray;\" onclick=\"openLikesPopup('"+likeIds+"','"+clientLikeIds+"')\">"+lcnt+"</a>");
				}else{
					st1.append(""+lcnt);
				}
				request.setAttribute("likeCnt", st1.toString());
			} else {
				request.setAttribute("likeCnt", st.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
//			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+uF.showData(strCustName, "")+". Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getLikeUnlike() {
		return likeUnlike;
	}

	public void setLikeUnlike(String likeUnlike) {
		this.likeUnlike = likeUnlike;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}