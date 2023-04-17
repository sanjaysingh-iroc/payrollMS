package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

public class Navigation extends ActionSupport implements ServletRequestAware, IStatements {

 	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/common/Navigation.jsp");
		request.setAttribute(TITLE, "Navigation");

		   
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(getSave()!=null){
			saveNavigation(uF);
		}
		
		viewNotificationSettings(uF);
		return loadNotificationSettings();
		
	}
	
	String navigation;
	String save;
	  
	public String loadValidateNotificationSettings() {
		request.setAttribute(PAGE, "/jsp/common/Navigation.jsp");
		request.setAttribute(TITLE, "Navigation");
		
		return LOAD;
	}
	

	public String loadNotificationSettings() {
		request.setAttribute(PAGE, "/jsp/common/Navigation.jsp");
		request.setAttribute(TITLE, "Navigation");
		
		
		return LOAD;
	}

	public String saveNavigation(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			
			String str0 = getNavigation();
			
			int indexId = -1;
			
			for(int i=0; i<100; i++){
				
//				System.out.println("str0=="+str0);
				indexId = str0.indexOf("\"id\":"); 
				
				if(indexId<0){
					break;
				}
				
				str0 = str0.substring(str0.indexOf("\"id\":"), str0.length());
				
				
				int index = str0.indexOf("}");
				String strId = null;
				if(indexId>0){
					strId = str0.substring(5, index);
				}else{
					strId = str0.substring(0, index);
				}
				
				System.out.println("strId==>"+strId);
				//func(strId, str0, 0, indexId);
				if(strId.indexOf("children")>=0){
					
					index = strId.indexOf(",");
					
					
					strId = strId.substring(0, index);
					
					
					int indexx = str0.indexOf("\"children\":");
					if(indexx>0){
						indexx += 11;
					}
					
					indexId = str0.indexOf("\"id\":"); 
					str0 = str0.substring(indexx);
					str0 = str0.substring(indexId, str0.length());
					index = str0.indexOf("}");
					
					String strChild = str0.substring(indexId+7, index);
					
					
					
					index = str0.indexOf(",");
					
//					String strChild = str0.substring(index);
					if(index>0){
						str0 = str0.substring(index);
					}
//					System.out.println("===strChild===>"+strChild);
					
					if(strChild.indexOf("children")>=0){
						updateNavigation(con, uF.parseToInt(strId), 1, 1, uF.parseToInt(strChild));
						
						str0 = func(con, uF, strChild, str0, indexx, indexId, strId).toString();
					}else{
						updateNavigation(con, uF.parseToInt(strId), 1, 1, uF.parseToInt(strChild));
						//updateNavigation(con, 0, 1, 1, uF.parseToInt(strId));
					}
//					str0 = strChild;
					
					
				}else{
					System.out.println("===strId 00000 ===>"+strId+" ");
					str0 = str0.substring(index, str0.lastIndexOf("}]")+2);
					
					updateNavigation(con, 0, 0, 1, uF.parseToInt(strId));
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	String func(Connection con, UtilityFunctions uF, String strId, String str0, int index, int indexId, String strPId){
		
		
		if(strId.indexOf("children")>=0){
			
			index = strId.indexOf(",");
			
			
			strId = strId.substring(0, index);
			
			
			int indexx = str0.indexOf("\"children\":")+11;
			str0 = str0.substring(indexx);
			str0 = str0.substring(indexId, str0.length());
			index = str0.indexOf("}");
			
			String strChild = str0.substring(indexId+3, index);
			
			
			
			index = str0.indexOf(",");
			
//			String strChild = str0.substring(index);
			str0 = str0.substring(index);
			System.out.println("===strId= 3333 ==>"+strId);
			System.out.println("===strChild== 3333 =>"+strChild);
			
			updateNavigation(con, uF.parseToInt(strId), 0, 1, uF.parseToInt(strChild));
			
		}else{
			System.out.println("===strId= 444444 ==>"+strId);
			str0 = str0.substring(index, str0.lastIndexOf("}]")+2);
		}
		return str0;
		
	}
	
	
	
	public void updateNavigation(Connection con, int nParent, int nChild, int nWeight, int nNavigationId){
		PreparedStatement pst = null;
		try {
			
			
			pst = con.prepareStatement("update navigation_1 set parent=?, child=?, weight=? where navigation_id = ?");
			pst.setInt(1, nParent);
			pst.setInt(2, nChild);
			pst.setInt(3, nWeight);
			pst.setInt(4, nNavigationId);
			pst.execute();
			pst.close();
			
			System.out.println("pst===>"+pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public String viewNotificationSettings(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			Map hmHireracyLevels = new LinkedHashMap();
			Map<String, String> hmEmpSuperMap = new LinkedHashMap<String, String>();
			List<String> alHireracyLevels = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			
			
			pst = con.prepareStatement("select * from navigation_1 order by parent, weight"); 
			rs = pst.executeQuery();
			String strSupervisorOld = null;
			String strSupervisorNew = null;
			
			while(rs.next()){
				strSupervisorNew = rs.getString("parent");
				if(strSupervisorNew!=null && !strSupervisorNew.equalsIgnoreCase(strSupervisorOld)){
					alInner = new ArrayList<String>();
				}
				
				alHireracyLevels.add(rs.getString("navigation_id"));
				alInner.add(rs.getString("navigation_id"));
				hmHireracyLevels.put(strSupervisorNew, alInner);
				hmEmpSuperMap.put(rs.getString("navigation_id"), strSupervisorNew);
				strSupervisorOld = strSupervisorNew;
			}
			rs.close();
			pst.close();
			  
			request.setAttribute("hmHireracyLevels", hmHireracyLevels);
			request.setAttribute("alHireracyLevels", alHireracyLevels);
			
			System.out.println("hmHireracyLevels==>"+hmHireracyLevels);
			System.out.println("alHireracyLevels==>"+alHireracyLevels);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	

	public void validate() {
        loadValidateNotificationSettings();
    }

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getNavigation() {
		return navigation;
	}


	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}


	public String getSave() {
		return save;
	}


	public void setSave(String save) {
		this.save = save;
	}
}