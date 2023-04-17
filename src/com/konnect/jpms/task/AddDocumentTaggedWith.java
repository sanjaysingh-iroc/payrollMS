package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddDocumentTaggedWith extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	List<FillEmployee> resourceList;
	List<String> taggedRes = new ArrayList<String>();
	
	String strTaggedWith;
	String strFeedId;
	
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
		
//		request.setAttribute(PAGE, "/jsp/task/Feeds.jsp");
//		request.setAttribute(TITLE, "Feeds");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		resourceList = new FillEmployee(request).fillEmployeeName(null, null, 0, 0, session);
		
		addPostTaggedWith(uF);
		
		getPostData(uF);
		
		return LOAD;
	}
	
	
	private void getPostData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from communication_1 where communication_id = ?");
			pst.setInt(1, uF.parseToInt(getStrFeedId()));
			rs = pst.executeQuery();
			String taggedResource = null;
			while(rs.next()) {
				taggedResource = getResourceName(hmResourceName, rs.getString("tagged_with"));
				
				List<String> empvalue1 = new ArrayList<String>();
				if (rs.getString("tagged_with") == null) {
					empvalue1.add("0");
				} else {
				empvalue1 = Arrays.asList(rs.getString("tagged_with").split(","));
				}				
				if (empvalue1 != null) {
					for (int i = 1; i < empvalue1.size(); i++) {
						taggedRes.add(empvalue1.get(i).trim());
					}
				} else {
					taggedRes.add("0");
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("taggedRes ===>> " + taggedRes);
			request.setAttribute("taggedResource", taggedResource);
			
//			System.out.println("hmFeeds ===>> " + hmFeeds);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getResourceName(Map<String, String> hmResourceName, String resourceIds) {
		StringBuilder resouceName = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alResIds = new ArrayList<String>();
		
		if(resourceIds != null && resourceIds.length()>0) {
			int cnt = 0;
			alResIds = Arrays.asList(resourceIds.split(","));
			for(int i=0; alResIds != null && !alResIds.isEmpty() && i<alResIds.size(); i++) {
				if(uF.parseToInt(alResIds.get(i)) > 0) {
					if(resouceName == null) {
						resouceName = new StringBuilder();
						resouceName.append(hmResourceName.get(alResIds.get(i)));
					} else {
						resouceName.append(","+hmResourceName.get(alResIds.get(i)));
						cnt++;
					}
				}
			}
//			if(cnt>0) {
//				resouceName.append(" <span style=\"color: gray;\">and</span> "+cnt+" others");
//			}
		}
		if(resouceName == null) {
			resouceName = new StringBuilder();
		}
		return resouceName.toString();
	}


	private void addPostTaggedWith(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update communication_1 set tagged_with=? where communication_id=?");
			pst.setString(1, getStrTaggedWith());
			pst.setInt(2, uF.parseToInt(getStrFeedId()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
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

	public List<FillEmployee> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<FillEmployee> resourceList) {
		this.resourceList = resourceList;
	}

	public String getStrTaggedWith() {
		return strTaggedWith;
	}

	public void setStrTaggedWith(String strTaggedWith) {
		this.strTaggedWith = strTaggedWith;
	}

	public String getStrFeedId() {
		return strFeedId;
	}

	public void setStrFeedId(String strFeedId) {
		this.strFeedId = strFeedId;
	}

	public List<String> getTaggedRes() {
		return taggedRes;
	}

	public void setTaggedRes(List<String> taggedRes) {
		this.taggedRes = taggedRes;
	}

}