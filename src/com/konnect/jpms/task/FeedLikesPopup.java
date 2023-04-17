package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class FeedLikesPopup extends ActionSupport implements ServletRequestAware, IStatements{
	HttpSession session;
	private HttpServletRequest request;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	String clientLikeIds;
	String likeIds;
	
	public String execute() throws Exception{
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/FeedLikesPopup.jsp");
		//System.out.println("java clientLikeIds==>"+clientLikeIds);
		getFeedLikesNames(CF);
		return SUCCESS;
	}
	
	public void getFeedLikesNames(CommonFunctions CF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmCustName = CF.getCustNameMap(con);
			List<String> alInner = new ArrayList<String>();
			
			String[] likeIdsList = getLikeIds().split(",");
			int size = likeIdsList.length;
			for(int i=0;i<size;i++){
				
				if(hmResourceName.get(likeIdsList[i])!=null){
					if(likeIdsList[i].equals(strSessionEmpId)){
						alInner.add("You");
					}else {
						alInner.add(hmResourceName.get(likeIdsList[i]) );
					}
					
				}
			}
			
			String[] clientlikeIdsList = getClientLikeIds().split(",");
			int size1 = clientlikeIdsList.length;
			for(int i=0;i<size1;i++){
				
				if(hmCustName.get(clientlikeIdsList[i])!=null){
					if(clientlikeIdsList[i].equals(strSessionEmpId)){
						alInner.add("You");
					}else {
						alInner.add(hmCustName.get(clientlikeIdsList[i]));
					}
					
				}
			}
			
			
			request.setAttribute("likesList",alInner);
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
	}
	


	public String getClientLikeIds() {
		return clientLikeIds;
	}

	public void setClientLikeIds(String clientLikeIds) {
		this.clientLikeIds = clientLikeIds;
	}

	public String getLikeIds() {
		return likeIds;
	}


	public void setLikeIds(String likeIds) {
		this.likeIds = likeIds;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
