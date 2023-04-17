package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Navigation;
import com.konnect.jpms.util.UtilityFunctions;

public class FillNavigation implements IStatements {
	
	
	private static Logger log = Logger.getLogger(FillNavigation.class);
	
	HttpServletRequest request;
	public FillNavigation(HttpServletRequest request) {
		this.request = request;
	}
	
public void fillNavigation(List<Navigation> alParentNavL, Map<String, List<Navigation>> hmChildNavL, List<Navigation> alParentNavR, Map<String, List<Navigation>> hmChildNavR, HttpSession session, CommonFunctions CF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null; 
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		Map<String, String> hmNavigationAction = new HashMap<String, String>();
		Map<String, String> hmNavigation = new HashMap<String, String>();
		Map<String, String> hmNavigationParent = new HashMap<String, String>();
		
		try { 
			
			con = db.makeConnection(con);
			String productType = (String)session.getAttribute(PRODUCT_TYPE);
			String userId = (String)session.getAttribute(USERID);
			
//			System.out.println("userId ===>> " + userId);
//			System.out.println("productType ===>> " + productType);
			
			/*
			pst = con.prepareStatement(selectNavigationACL);
//			pst.setInt(1, uF.parseToInt((String)session.getAttribute(USERID)));
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(USERTYPEID)));
			
			rs = pst.executeQuery();
			String strACl = null;
			String []arrACl = null;
			while (rs.next()) {
				strACl = rs.getString("navigation");
			}
			if(strACl!=null){
				arrACl = strACl.split(",");
			}
			
			pst = con.prepareStatement(selectNavigation);
			pst.setString(1, "L");
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(USERTYPEID)));
			rs = pst.executeQuery();
			
			List<Navigation> alL = new ArrayList<Navigation>();
			
			while (rs.next()) {
				if(rs.getInt("parent")==0 && rs.getInt("_exist")==1 ){
					if(ArrayUtils.contains(arrACl, rs.getString("navigation_id"))>=0){
						alParentNavL.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc")));
					}
				}else if(rs.getInt("_exist")==1){
					alL = (List<Navigation>)hmChildNavL.get(rs.getString("parent"));
					if(alL==null){
						alL = new ArrayList<Navigation>();
					}
					
					if(ArrayUtils.contains(arrACl, rs.getString("navigation_id"))>=0){
						alL.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc")));
						hmChildNavL.put(rs.getString("parent"), alL);
					}
				}
				
				hmNavigationParent.put(rs.getString("_action"), rs.getString("parent")); 
				hmNavigation.put(rs.getString("_action"), rs.getString("navigation_id"));
			}
			
			pst = con.prepareStatement(selectNavigation);
			pst.setString(1, "R");
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(USERTYPEID)));
			rs = pst.executeQuery();
			List<Navigation> alR = new ArrayList<Navigation>();
			while (rs.next()) {
				if(rs.getInt("parent")==0 && rs.getInt("_exist")==1){
					if(ArrayUtils.contains(arrACl, rs.getString("navigation_id"))>=0){
						alParentNavR.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc")));
					}
				}else if(rs.getInt("_exist")==1){
					alR = (List<Navigation>)hmChildNavR.get(rs.getString("parent"));
					if(alR==null){
						alR = new ArrayList<Navigation>();
					}
					
					if(ArrayUtils.contains(arrACl, rs.getString("navigation_id"))>=0){
						alR.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc")));
						hmChildNavR.put(rs.getString("parent"), alR);
					}
				}
				
				hmNavigationParent.put(rs.getString("_action"), rs.getString("parent")); 
				hmNavigation.put(rs.getString("_action"), rs.getString("navigation_id"));
			}*/
			
			boolean tlOrPoFlag = false;
			
			if(productType != null && productType.equals("3")) {
				pst = con.prepareStatement("select emp_id from project_emp_details where emp_id = ? and _isteamlead = true");
				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//				System.out.println("pst =====> "+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					tlOrPoFlag = true;
				}
				rs.close();
				pst.close();
				
//				System.out.println("tlOrPoFlag =====> "+ tlOrPoFlag);
				
		//===start parvez date: 12-10-2022===		
//				pst = con.prepareStatement("select project_owner from projectmntnc where project_owner = ?");
//				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
				
				pst = con.prepareStatement("select project_owners from projectmntnc where project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ");
		//===end parvez date: 12-10-2022===		
//				System.out.println("pst =====> "+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					tlOrPoFlag = true;
				}
				rs.close();
				pst.close();
//				System.out.println("tlOrPoFlag =====> "+ tlOrPoFlag);
			}
			
//			System.out.println("BASEUSERTYPEID =====> "+ (String)session.getAttribute(BASEUSERTYPEID));
			
			List<String> tlPONaviList = new ArrayList<String>();
			tlPONaviList.add("My Projects");
//			tlPONaviList.add("My Team");
			
			List<String> onlyTLPONaviList = new ArrayList<String>();
			onlyTLPONaviList.add("My Team");
			
			String []arrEnabledModules = CF.getArrEnabledModules();
			
			StringBuilder sbQueryL = new StringBuilder();
//			sbQueryL.append("select * from navigation_1 where _position = ? and _type='N' and (product_type = '1'");
//			if(productType != null && !productType.equals("")) {
//				sbQueryL.append(" or product_type = '" + productType + "'");
//			}
//			sbQueryL.append(") and (user_type_id like ? ");
//			if(BASEUSERTYPE != null && !BASEUSERTYPE.equals(ADMIN)) {
//				sbQueryL.append(" or (user_type_id like '%1,%' and _position = 'C')");
//			}
			sbQueryL.append("select * from navigation_1 where ((_position = ? and user_type_id like ?)");
			if(BASEUSERTYPE != null && !BASEUSERTYPE.equals(ADMIN)) {
				sbQueryL.append(" or (user_type_id like '%,1,%' and _position = 'C')");
			}
			sbQueryL.append(") and _type='N' and (product_type = '1'");
			if(productType != null && !productType.equals("")) {
				sbQueryL.append(" or product_type = '" + productType + "'");
			}
			sbQueryL.append(") order by weight");
			pst = con.prepareStatement(sbQueryL.toString());
			pst.setString(1, "L");
			if(productType != null && productType.equals("3")) {
				pst.setString(2, "%,"+(String)session.getAttribute(BASEUSERTYPEID)+",%");
			} else {
				pst.setString(2, "%,"+(String)session.getAttribute(USERTYPEID)+",%");
			}
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			
			List<Navigation> alL = new ArrayList<Navigation>();
			 
			while (rs.next()) {
				if(productType != null && productType.equals("3") && !tlOrPoFlag && (uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 3 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 4 
					|| uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 7 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 1) && tlPONaviList.contains(rs.getString("_label").trim()) ) { //|| rs.getString("_label").trim().equalsIgnoreCase("") **********  || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 2 
					continue;
				} else if(productType != null && productType.equals("3") && !tlOrPoFlag && (uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 3 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 4) && onlyTLPONaviList.contains(rs.getString("_label").trim()) ) { //|| rs.getString("_label").trim().equalsIgnoreCase("") 
					continue;
				} else if(productType != null && productType.equals("2") && (!((String)session.getAttribute(BASEUSERTYPE)).equals(CEO) && !((String)session.getAttribute(BASEUSERTYPE)).equals(HRMANAGER) && !((String)session.getAttribute(BASEUSERTYPE)).equals(ADMIN)) && rs.getInt("navigation_id") == 200) { //|| rs.getString("_label").trim().equalsIgnoreCase("") 
					continue;
				} else {
					if(rs.getInt("parent") == 0 && rs.getInt("_exist") == 1 && arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
						alParentNavL.add(new Navigation(rs.getString("navigation_id"), rs.getString("_label"), ((rs.getString("_label_selected")!=null) ? rs.getString("_label_selected") : rs.getString("_label")), ((rs.getString("_label_unselected")!=null) ? rs.getString("_label_unselected") : rs.getString("_label")), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("_position"), rs.getString("link_desc"), rs.getString("nav_visibility"), rs.getString("_label_code")));
					} else if(rs.getInt("_exist") == 1) {
						alL = (List<Navigation>)hmChildNavL.get(rs.getString("parent"));
						if(alL == null) {
							alL = new ArrayList<Navigation>();
						}
						
						if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
							alL.add(new Navigation(rs.getString("navigation_id"), rs.getString("_label"), ((rs.getString("_label_selected")!=null) ? rs.getString("_label_selected") : rs.getString("_label")), ((rs.getString("_label_unselected")!=null) ? rs.getString("_label_unselected") : rs.getString("_label")), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("_position"), rs.getString("link_desc"), rs.getString("nav_visibility"), rs.getString("_label_code")));
							hmChildNavL.put(rs.getString("parent"), alL);
						}
					}
					
					if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
						hmNavigationParent.put(rs.getString("_action"), rs.getString("parent")); 
						hmNavigation.put(rs.getString("_action"), rs.getString("navigation_id"));
						//if(hmNavigationParent.containsValue(rs.getString("navigation_id"))){
							hmNavigationAction.put(rs.getString("navigation_id"), rs.getString("_action"));
						//}
					}
				}
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQueryR = new StringBuilder();
			sbQueryR.append("select * from navigation_1 where _position = ? and user_type_id like ? and _type='N' and (product_type = '1'");
			if(productType != null && !productType.equals("")) {
				sbQueryR.append(" or product_type = '" + productType + "'");
			}
			sbQueryR.append(") order by weight");
			pst = con.prepareStatement(sbQueryR.toString());
			pst.setString(1, "R");
			if(productType != null && productType.equals("3")) {
				pst.setString(2, "%,"+(String)session.getAttribute(BASEUSERTYPEID)+",%");
			} else {
				pst.setString(2, "%,"+(String)session.getAttribute(USERTYPEID)+",%");
			}
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			
			List<Navigation> alR = new ArrayList<Navigation>();
			while (rs.next()) {
				if(productType != null && productType.equals("3") && !tlOrPoFlag && (uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 3 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 4 
					|| uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 7 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 1 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 2) && tlPONaviList.contains(rs.getString("_label").trim()) ) { //|| rs.getString("_label").trim().equalsIgnoreCase("") 
					continue;
				} else {
					if(rs.getInt("parent") == 0 && rs.getInt("_exist") == 1 && arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
						alParentNavR.add(new Navigation(rs.getString("navigation_id"), rs.getString("_label"), ((rs.getString("_label_selected")!=null) ? rs.getString("_label_selected") : rs.getString("_label")), ((rs.getString("_label_unselected")!=null) ? rs.getString("_label_unselected") : rs.getString("_label")), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("_position"), rs.getString("link_desc"), rs.getString("nav_visibility"), rs.getString("_label_code")));
					} else if(rs.getInt("_exist") == 1) {
						alR = (List<Navigation>)hmChildNavR.get(rs.getString("parent"));
						if(alR == null) {
							alR = new ArrayList<Navigation>();
						}
						
						if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id"))>=0) {
							alR.add(new Navigation(rs.getString("navigation_id"),rs.getString("_label"), ((rs.getString("_label_selected") != null) ? rs.getString("_label_selected") : rs.getString("_label")), ((rs.getString("_label_unselected")!=null) ? rs.getString("_label_unselected") : rs.getString("_label")), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("_position"), rs.getString("link_desc"), rs.getString("nav_visibility"), rs.getString("_label_code")));
							hmChildNavR.put(rs.getString("parent"), alR);
						}
					}
					
					if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
						hmNavigationParent.put(rs.getString("_action"), rs.getString("parent")); 
						hmNavigation.put(rs.getString("_action"), rs.getString("navigation_id"));
	//					if(hmNavigationParent.containsValue(rs.getString("navigation_id"))){
							hmNavigationAction.put(rs.getString("navigation_id"), rs.getString("_action"));
	//					}
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmChildNavL ===>> "+ hmChildNavL); 
			
			session.setAttribute("alParentNavL", alParentNavL);
			session.setAttribute("hmChildNavL", hmChildNavL);
			session.setAttribute("alParentNavR", alParentNavR);
			session.setAttribute("hmChildNavR", hmChildNavR);
			session.setAttribute("hmNavigation", hmNavigation);
			session.setAttribute("hmNavigationParent", hmNavigationParent);
			session.setAttribute("hmNavigationAction", hmNavigationAction);
			
			
//			System.out.println("hmNavigation ==>>> " + hmNavigation);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void fillNavigation11111(List<Navigation> alParentNavL, Map<String, List<Navigation>> hmChildNavL, List<Navigation> alParentNavR, Map<String, List<Navigation>> hmChildNavR, String strUserTypeId){
		
		
		Database db = new Database();
		db.setRequest(request);		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement(selectNavigation);
			pst.setString(1, "L");
			pst.setInt(2, uF.parseToInt(strUserTypeId));
			rs = pst.executeQuery();
			
			List<Navigation> alL = new ArrayList<Navigation>();
			
			while (rs.next()) { 
				int nShow = rs.getInt("_exist");
				if(rs.getInt("parent")==0 && nShow==1){
						alParentNavL.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"),rs.getString("_label_selected"),rs.getString("_label_unselected"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc"),rs.getString("nav_visibility"), rs.getString("_label_code")));
				}else if(nShow==1){
					alL = (List<Navigation>)hmChildNavL.get(rs.getString("parent"));
					if(alL==null){
						alL = new ArrayList<Navigation>();
					}
						alL.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"),rs.getString("_label_selected"),rs.getString("_label_unselected"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc"),rs.getString("nav_visibility"), rs.getString("_label_code")));
						hmChildNavL.put(rs.getString("parent"), alL);
					
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectNavigation);
			pst.setString(1, "R");
			pst.setInt(2, uF.parseToInt(strUserTypeId));
			rs = pst.executeQuery();
			List<Navigation> alR = new ArrayList<Navigation>();
			while (rs.next()) {
				int nShow = rs.getInt("_exist");
				if(rs.getInt("parent")==0 && nShow==1){
						alParentNavR.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"),rs.getString("_label_selected"),rs.getString("_label_unselected"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc"),rs.getString("nav_visibility"), rs.getString("_label_code")));
				}else if(nShow==1){
					alR = (List<Navigation>)hmChildNavR.get(rs.getString("parent"));
					if(alR==null){
						alR = new ArrayList<Navigation>();
					}
						alR.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"),rs.getString("_label_selected"),rs.getString("_label_unselected"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc"),rs.getString("nav_visibility"), rs.getString("_label_code")));
						hmChildNavR.put(rs.getString("parent"), alR);
				}
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
//	public void fillNavigation(List<Navigation> alParentNavL, Map<String, List<Navigation>> hmChildNavL, List<Navigation> alParentNavR, Map<String, List<Navigation>> hmChildNavR, HttpSession session, CommonFunctions CF){
//		
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null; 
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		Map<String, String> hmNavigationAction = new HashMap<String, String>();
//		Map<String, String> hmNavigation = new HashMap<String, String>();
//		Map<String, String> hmNavigationParent = new HashMap<String, String>();
//		
//		try { 
//			
//			con = db.makeConnection(con);
//			String productType = (String)session.getAttribute(PRODUCT_TYPE);
//			String userId = (String)session.getAttribute(USERID);
//			
////			System.out.println("userId ===>> " + userId);
////			System.out.println("productType ===>> " + productType);
//			
//			boolean tlOrPoFlag = false;
//			if(productType != null && productType.equals("3")) {
//				pst = con.prepareStatement("select emp_id from project_emp_details where emp_id = ? and _isteamlead = true");
//				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
////				System.out.println("pst =====> "+pst);
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					tlOrPoFlag = true;
//				}
//				rs.close();
//				pst.close();
//				
////				System.out.println("tlOrPoFlag =====> "+ tlOrPoFlag);
//				
//				pst = con.prepareStatement("select project_owner from projectmntnc where project_owner = ?");
//				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
////				System.out.println("pst =====> "+pst);
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					tlOrPoFlag = true;
//				}
//				rs.close();
//				pst.close();
////				System.out.println("tlOrPoFlag =====> "+ tlOrPoFlag);
//			}
//			
////			System.out.println("BASEUSERTYPE =====> "+ (String)session.getAttribute(BASEUSERTYPE));
//			List<String> tlPONaviList = new ArrayList<String>();
//			tlPONaviList.add("My Projects");
////			tlPONaviList.add("My Team");
//			
//			List<String> onlyTLPONaviList = new ArrayList<String>();
//			onlyTLPONaviList.add("My Team");
//			
//			String []arrEnabledModules = CF.getArrEnabledModules();
//			
//			StringBuilder sbQueryL = new StringBuilder();
//			sbQueryL.append("select * from navigation_1 where ((_position = ? and user_type_id like ?)");
//			if(((String)session.getAttribute(BASEUSERTYPE)) != null && (((String)session.getAttribute(BASEUSERTYPE)).equals(ADMIN) || ((String)session.getAttribute(BASEUSERTYPE)).equals(HRMANAGER)) ){
//				sbQueryL.append(" or (user_type_id like '%,1,%' and _position = 'C')");
//			}
//			sbQueryL.append(") and _type='N' and (product_type = '1'");
//			if(productType != null && !productType.equals("")) {
//				sbQueryL.append(" or product_type = '" + productType + "'");
//			}
//			sbQueryL.append(") ");
//			if(((String)session.getAttribute(BASEUSERTYPE)) != null && !((String)session.getAttribute(BASEUSERTYPE)).equals(CEO) && !((String)session.getAttribute(BASEUSERTYPE)).equals(ADMIN) ) {
//				sbQueryL.append(" and navigation_id != 200 ");
//			}
//			sbQueryL.append(" order by weight");
//			pst = con.prepareStatement(sbQueryL.toString());
//			pst.setString(1, "L");
//			if(productType != null && productType.equals("3")) {
//				pst.setString(2, "%,"+(String)session.getAttribute(BASEUSERTYPEID)+",%");
//			} else {
//				pst.setString(2, "%,"+(String)session.getAttribute(USERTYPEID)+",%");
//			}
////			System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			
//			List<Navigation> alL = new ArrayList<Navigation>();
//			 
//			while (rs.next()) {
//				if(productType != null && productType.equals("3") && !tlOrPoFlag && (uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 3 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 4 
//					|| uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 7 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 1) && tlPONaviList.contains(rs.getString("_label").trim()) ) { //|| rs.getString("_label").trim().equalsIgnoreCase("") || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 2 
//					continue;
//				} else if(productType != null && productType.equals("3") && !tlOrPoFlag && (uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 3) && onlyTLPONaviList.contains(rs.getString("_label").trim()) ) { //|| rs.getString("_label").trim().equalsIgnoreCase("")   || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 4 
//					continue;
//				} else {
//					if(rs.getInt("parent") == 0 && rs.getInt("_exist") == 1 && arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
//						alParentNavL.add(new Navigation(rs.getString("navigation_id"), rs.getString("_label"), ((rs.getString("_label_selected")!=null) ? rs.getString("_label_selected") : rs.getString("_label")), ((rs.getString("_label_unselected")!=null) ? rs.getString("_label_unselected") : rs.getString("_label")), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("_position"), rs.getString("link_desc"), rs.getString("nav_visibility")));
//					} else if(rs.getInt("_exist") == 1) {
//						alL = (List<Navigation>)hmChildNavL.get(rs.getString("parent"));
//						if(alL == null) {
//							alL = new ArrayList<Navigation>();
//						}
//						
//						if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
//							alL.add(new Navigation(rs.getString("navigation_id"), rs.getString("_label"), ((rs.getString("_label_selected")!=null) ? rs.getString("_label_selected") : rs.getString("_label")), ((rs.getString("_label_unselected")!=null) ? rs.getString("_label_unselected") : rs.getString("_label")), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("_position"), rs.getString("link_desc"), rs.getString("nav_visibility")));
//							hmChildNavL.put(rs.getString("parent"), alL);
//						}
//					}
//					
//					if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
//						hmNavigationParent.put(rs.getString("_action"), rs.getString("parent")); 
//						hmNavigation.put(rs.getString("_action"), rs.getString("navigation_id"));
//						//if(hmNavigationParent.containsValue(rs.getString("navigation_id"))){
//							hmNavigationAction.put(rs.getString("navigation_id"), rs.getString("_action"));
//						//}
//					}
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			StringBuilder sbQueryR = new StringBuilder();
//			sbQueryR.append("select * from navigation_1 where _position = ? and user_type_id like ? and _type='N' and (product_type = '1'");
//			if(productType != null && !productType.equals("")) {
//				sbQueryR.append(" or product_type = '" + productType + "'");
//			}
//			sbQueryR.append(") order by weight");
//			pst = con.prepareStatement(sbQueryR.toString());
//			pst.setString(1, "R");
//			if(productType != null && productType.equals("3")) {
//				pst.setString(2, "%,"+(String)session.getAttribute(BASEUSERTYPEID)+",%");
//			} else {
//				pst.setString(2, "%,"+(String)session.getAttribute(USERTYPEID)+",%");
//			}
////			System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			
//			List<Navigation> alR = new ArrayList<Navigation>();
//			
//			while (rs.next()) {
//				if(productType != null && productType.equals("3") && !tlOrPoFlag && (uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 3 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 4 
//					|| uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 7 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 1 || uF.parseToInt((String)session.getAttribute(BASEUSERTYPEID)) == 2) && tlPONaviList.contains(rs.getString("_label").trim()) ) { //|| rs.getString("_label").trim().equalsIgnoreCase("") 
//					continue;
//				} else {
//					if(rs.getInt("parent") == 0 && rs.getInt("_exist") == 1 && arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
//						alParentNavR.add(new Navigation(rs.getString("navigation_id"), rs.getString("_label"), ((rs.getString("_label_selected")!=null) ? rs.getString("_label_selected") : rs.getString("_label")), ((rs.getString("_label_unselected")!=null) ? rs.getString("_label_unselected") : rs.getString("_label")), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("_position"), rs.getString("link_desc"), rs.getString("nav_visibility")));
//					} else if(rs.getInt("_exist") == 1) {
//						alR = (List<Navigation>)hmChildNavR.get(rs.getString("parent"));
//						if(alR == null) {
//							alR = new ArrayList<Navigation>();
//						}
//						
//						if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id"))>=0) {
//							alR.add(new Navigation(rs.getString("navigation_id"),rs.getString("_label"), ((rs.getString("_label_selected") != null) ? rs.getString("_label_selected") : rs.getString("_label")), ((rs.getString("_label_unselected")!=null) ? rs.getString("_label_unselected") : rs.getString("_label")), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("_position"), rs.getString("link_desc"), rs.getString("nav_visibility")));
//							hmChildNavR.put(rs.getString("parent"), alR);
//						}
//					}
//					
//					if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, rs.getString("module_id")) >= 0) {
//						hmNavigationParent.put(rs.getString("_action"), rs.getString("parent")); 
//						hmNavigation.put(rs.getString("_action"), rs.getString("navigation_id"));
//	//					if(hmNavigationParent.containsValue(rs.getString("navigation_id"))){
//							hmNavigationAction.put(rs.getString("navigation_id"), rs.getString("_action"));
//	//					}
//					}
//				
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			session.setAttribute("alParentNavL", alParentNavL);
//			session.setAttribute("hmChildNavL", hmChildNavL);
//			session.setAttribute("alParentNavR", alParentNavR);
//			session.setAttribute("hmChildNavR", hmChildNavR);
//			session.setAttribute("hmNavigation", hmNavigation);
//			session.setAttribute("hmNavigationParent", hmNavigationParent);
//			session.setAttribute("hmNavigationAction", hmNavigationAction);
//			
//			
////			System.out.println("hmNavigation ==>>> " + hmNavigation);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
//	
//	public void fillNavigation11111(List<Navigation> alParentNavL, Map<String, List<Navigation>> hmChildNavL, List<Navigation> alParentNavR, Map<String, List<Navigation>> hmChildNavR, String strUserTypeId){
//		
//		Database db = new Database();
//		db.setRequest(request);		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectNavigation);
//			pst.setString(1, "L");
//			pst.setInt(2, uF.parseToInt(strUserTypeId));
//			rs = pst.executeQuery();
//			List<Navigation> alL = new ArrayList<Navigation>();
//			while (rs.next()) {
//				int nShow = rs.getInt("_exist");
//				if(rs.getInt("parent")==0 && nShow==1){
//						alParentNavL.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"),rs.getString("_label_selected"),rs.getString("_label_unselected"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc"),rs.getString("nav_visibility")));
//				}else if(nShow==1){
//					alL = (List<Navigation>)hmChildNavL.get(rs.getString("parent"));
//					if(alL==null){
//						alL = new ArrayList<Navigation>();
//					}
//					
//						alL.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"),rs.getString("_label_selected"),rs.getString("_label_unselected"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc"),rs.getString("nav_visibility")));
//						hmChildNavL.put(rs.getString("parent"), alL);
//					
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			
//			
//			pst = con.prepareStatement(selectNavigation);
//			pst.setString(1, "R");
//			pst.setInt(2, uF.parseToInt(strUserTypeId));
//			rs = pst.executeQuery();
//			List<Navigation> alR = new ArrayList<Navigation>();
//			while (rs.next()) {
//				int nShow = rs.getInt("_exist");
//				if(rs.getInt("parent")==0 && nShow==1){
//						alParentNavR.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"),rs.getString("_label_selected"),rs.getString("_label_unselected"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc"),rs.getString("nav_visibility")));
//				}else if(nShow==1){
//					alR = (List<Navigation>)hmChildNavR.get(rs.getString("parent"));
//					if(alR==null){
//						alR = new ArrayList<Navigation>();
//					}
//					
//						alR.add(new Navigation(rs.getString("navigation_id") ,rs.getString("_label"),rs.getString("_label_selected"),rs.getString("_label_unselected"), rs.getString("_action"), rs.getString("parent"), rs.getString("child"), rs.getString("position"), rs.getString("link_desc"),rs.getString("nav_visibility")));
//						hmChildNavR.put(rs.getString("parent"), alR);
//				}
//			}
//			rs.close();
//			pst.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	

}
