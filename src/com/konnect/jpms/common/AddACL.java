package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddACL extends ActionSupport implements ServletRequestAware, IStatements {
 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(AddACL.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(true);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		request.setAttribute(PAGE, PAddACL);
		request.setAttribute(TITLE, "Access Control Level");
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/

		String operation = request.getParameter("operation");
		String strUserTypeId = (String)request.getParameter("U");
		String strUserType = (String)session.getAttribute(USERTYPE);
		
		
		if(getStrUserTypeId()!=null){
			return addACL(uF);
		}else{
			return viewACL(strUserTypeId);
		}
		
	}
	  
	public String addACL(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			StringBuilder sb = new StringBuilder();
			for(int i=0; getStrNavigationId()!=null && i<getStrNavigationId().length; i++){
				sb.append(getStrNavigationId()[i]+",");
			}
			con = db.makeConnection(con);

			pst = con.prepareStatement(updateNavigationACL);
			pst.setString(1, sb.toString());
			pst.setInt(2, uF.parseToInt(getStrUserTypeId()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
 
	
	public String viewACL(String strUserTypeId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			List<String> alAcl = new ArrayList<String>();
			Map<String, String> hmUserTypeMap = new HashMap<String, String>();
			
			pst = con.prepareStatement(selectUserTypeR);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmUserTypeMap.put(rs.getString("user_type_id"), rs.getString("user_type"));
				if(uF.parseToInt(rs.getString("visibility_id"))>0) {
					alAcl.add(rs.getString("user_type_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			String strProductType = (String) session.getAttribute(PRODUCT_TYPE);
			
			Map<String, String> hmNaviIdLbl = new LinkedHashMap<String, String>();
			List<String> alParentId = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from navigation_1 where _exist = 1 ");
			if(strProductType != null && strProductType.trim().equals("3")){
				sbQuery.append(" and product_type in ('1','3') ");
			} else {
				sbQuery.append(" and product_type in ('1','2') ");
			}
			sbQuery.append(" order by visibility_weight, weight");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("user_type_id")!=null) {
					if(rs.getInt("parent") > 0 && !alParentId.contains(rs.getString("parent"))) {
						alParentId.add(rs.getString("parent"));
					}
					hmNaviIdLbl.put(rs.getString("navigation_id"), rs.getString("_label"));
				}
			}
			rs.close();
			pst.close();
			
			
			Map<String, List<String>> hmParentLblUserId = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmParentLblAndUserwiseNaviId = new LinkedHashMap<String, String>();
			List<String> alParentLblUserId = new ArrayList<String>();
			
			Map<String, Map<String, List<String>>> hmParentwiseLblUserId = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> hmChildLblUserId = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmChildLblAndUserwiseNaviId = new LinkedHashMap<String, String>();
			List<String> alChildLblUserId = new ArrayList<String>();
			
			Map<String, Map<String, List<String>>> hmParentwiseLblUserId1 = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> hmChildLblUserId1 = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmChildLblAndUserwiseNaviId1 = new LinkedHashMap<String, String>();
			List<String> alChildLblUserId1 = new ArrayList<String>();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from navigation_1 where _exist = 1 ");
			if(strProductType != null && strProductType.trim().equals("3")){
				sbQuery.append(" and product_type in ('1','3') ");
			} else {
				sbQuery.append(" and product_type in ('1','2') ");
			}
			sbQuery.append(" order by visibility_weight, weight");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("user_type_id")!=null) {
					
					if(rs.getInt("parent") == 0 || rs.getInt("parent") == 1 || alParentId.contains(rs.getString("navigation_id"))) {
						hmParentLblAndUserwiseNaviId.put(rs.getString("user_type_id").substring(1, (rs.getString("user_type_id").length()-1))+"_"+rs.getString("_label"), rs.getString("navigation_id"));
						
						alParentLblUserId = hmParentLblUserId.get(rs.getString("_label"));
						if(alParentLblUserId == null) alParentLblUserId = new ArrayList<String>();
						
						alParentLblUserId.add(rs.getString("user_type_id").substring(1, (rs.getString("user_type_id").length()-1)));
						hmParentLblUserId.put(rs.getString("_label"), alParentLblUserId);
					} else if(rs.getInt("parent") > 1 && alParentId.contains(rs.getString("navigation_id"))) {
						
						hmChildLblAndUserwiseNaviId.put(rs.getString("user_type_id").substring(1, (rs.getString("user_type_id").length()-1))+"_"+rs.getString("_label"), rs.getString("navigation_id"));
						
						hmChildLblUserId = hmParentwiseLblUserId.get(hmNaviIdLbl.get(rs.getString("parent")));
						if(hmChildLblUserId == null) hmChildLblUserId = new LinkedHashMap<String, List<String>>();
						
						alChildLblUserId = hmChildLblUserId.get(rs.getString("_label"));
						if(alChildLblUserId == null) alChildLblUserId = new ArrayList<String>();
						
						alChildLblUserId.add(rs.getString("user_type_id").substring(1, (rs.getString("user_type_id").length()-1)));
						hmChildLblUserId.put(rs.getString("_label"), alChildLblUserId);
						
						hmParentwiseLblUserId.put(hmNaviIdLbl.get(rs.getString("parent")), hmChildLblUserId);
					
					} else {
						
						hmChildLblAndUserwiseNaviId1.put(rs.getString("user_type_id").substring(1, (rs.getString("user_type_id").length()-1))+"_"+rs.getString("_label"), rs.getString("navigation_id"));
						
						hmChildLblUserId1 = hmParentwiseLblUserId1.get(hmNaviIdLbl.get(rs.getString("parent")));
						if(hmChildLblUserId1 == null) hmChildLblUserId1 = new LinkedHashMap<String, List<String>>();
						
						alChildLblUserId1 = hmChildLblUserId1.get(rs.getString("_label"));
						if(alChildLblUserId1 == null) alChildLblUserId1 = new ArrayList<String>();
						
						alChildLblUserId1.add(rs.getString("user_type_id").substring(1, (rs.getString("user_type_id").length()-1)));
						hmChildLblUserId1.put(rs.getString("_label"), alChildLblUserId1);
						
						hmParentwiseLblUserId1.put(hmNaviIdLbl.get(rs.getString("parent")), hmChildLblUserId1);
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmParentLblUserId ======> " + hmParentLblUserId);
			
			request.setAttribute("hmParentLblUserId", hmParentLblUserId);
			request.setAttribute("hmParentLblAndUserwiseNaviId", hmParentLblAndUserwiseNaviId);
			
			request.setAttribute("hmParentwiseLblUserId", hmParentwiseLblUserId);
			request.setAttribute("hmChildLblAndUserwiseNaviId", hmChildLblAndUserwiseNaviId);
			
			request.setAttribute("hmParentwiseLblUserId1", hmParentwiseLblUserId1);
			request.setAttribute("hmChildLblAndUserwiseNaviId1", hmChildLblAndUserwiseNaviId1);
			
			request.setAttribute("hmUserTypeMap", hmUserTypeMap);
			request.setAttribute("alAcl", alAcl);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
			return SUCCESS;

	}
	
	
	
//	public String viewACL(String strUserTypeId) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//
//			con = db.makeConnection(con);
//
//			List<String> alAcl = new ArrayList<String>();
//			Map<String, String> hmUserTypeMap = new HashMap<String, String>();
//			
//			pst = con.prepareStatement(selectUserTypeR);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmUserTypeMap.put(rs.getString("user_type_id"), rs.getString("user_type"));
//				if(uF.parseToInt(rs.getString("visibility_id"))>0){
//					alAcl.add(rs.getString("user_type_id"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			
//			Map hmAclNavigationMap  = new HashMap();
//			Map hmAclNavigationLabelMap  = new LinkedHashMap();
//			Map hmVisibilityNavigationMap  = new HashMap();
//			
//			Map hmParentMap  = new HashMap();
//			Map hmChildMap  = new HashMap();
//			
//			String strProductType = (String) session.getAttribute(PRODUCT_TYPE);
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from navigation_1 where _exist = 1 ");
//			if(strProductType != null && strProductType.trim().equals("3")){
//				sbQuery.append(" and product_type in ('1','3') ");
//			} else {
//				sbQuery.append(" and product_type in ('1','2') ");
//			}
//			sbQuery.append(" order by visibility_weight, weight");
//			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				if(rs.getString("user_type_id")!=null) {
//					hmAclNavigationMap.put(rs.getString("navigation_id"), rs.getString("user_type_id").split(","));
//					hmVisibilityNavigationMap.put(rs.getString("navigation_id"), rs.getString("visibility").split(","));
//					hmAclNavigationLabelMap.put(rs.getString("_label"), rs.getString("navigation_id"));
//					
//					hmParentMap.put(rs.getString("navigation_id"), rs.getString("parent"));
//					hmChildMap.put(rs.getString("navigation_id"), rs.getString("child"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			
//			Map<String, String> hmNavigationAccessMap  = new HashMap<String, String>(); 
//			pst = con.prepareStatement("select * from navigation_acl");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				
//				hmNavigationAccessMap.put(rs.getString("user_id")+"_"+rs.getString("navigation_id")+"_V", rs.getString("is_view"));
//				hmNavigationAccessMap.put(rs.getString("user_id")+"_"+rs.getString("navigation_id")+"_A", rs.getString("is_add"));
//				hmNavigationAccessMap.put(rs.getString("user_id")+"_"+rs.getString("navigation_id")+"_U", rs.getString("is_update"));
//				hmNavigationAccessMap.put(rs.getString("user_id")+"_"+rs.getString("navigation_id")+"_D", rs.getString("is_delete"));
//			}
//			rs.close();
//			pst.close();
//			
//			System.out.println("hmAclNavigationLabelMap ======> " + hmAclNavigationLabelMap);
//			
//			request.setAttribute("hmNavigationAccessMap", hmNavigationAccessMap);
//			request.setAttribute("hmAclNavigationMap", hmAclNavigationMap);
//			request.setAttribute("hmVisibilityNavigationMap", hmVisibilityNavigationMap);
//			request.setAttribute("hmAclNavigationLabelMap", hmAclNavigationLabelMap);
//			request.setAttribute("hmUserTypeMap", hmUserTypeMap);
//			request.setAttribute("alAcl", alAcl);
//			request.setAttribute("hmChildMap", hmChildMap);
//			request.setAttribute("hmParentMap", hmParentMap);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//			return SUCCESS;
//
//	}
	
	
	String []strNavigationId;
	String strUserTypeId;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String[] getStrNavigationId() {
		return strNavigationId;
	}

	public void setStrNavigationId(String[] strNavigationId) {
		this.strNavigationId = strNavigationId;
	}

	public String getStrUserTypeId() {
		return strUserTypeId;
	}

	public void setStrUserTypeId(String strUserTypeId) {
		this.strUserTypeId = strUserTypeId;
	}


}