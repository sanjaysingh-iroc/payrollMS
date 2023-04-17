package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
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

public class AddWorkFlowMember extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;

	String submit;
	String operation;
	String groupName;
	String group_id;
	
	String organization;
	String location;
	String type;
	String groupType;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;


		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
	
		getMemberList();
		
		if (getOperation() != null && getOperation().equals("E")) {			
			System.out.println("====>groupName"+getGroupName());
			if (getSubmit() != null) {
				System.out.println("====>groupName"+getGroupName());
				updateGroupDetails();
				return SUCCESS; 
			}
			getGroupDetails();
		}else if (getOperation() != null && getOperation().equals("D")) {
			deleteWorkFlowGroup();		
			return SUCCESS;
		}else if (getOperation() != null && getOperation().equals("A")) {
			if (getSubmit() != null) {
				insertWorkFlowMemberGroup();
				return SUCCESS;
			}
		}
		
		if(getGroupType() == null){
			setGroupType("1");
		}
		

		return LOAD;
	}

	private void getMemberList() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from user_type where visibility_id>0 and user_type in ('"+ADMIN+"','"+MANAGER+"','"+ACCOUNTANT+"','"+CEO+"','"+HRMANAGER+"','"+HOD+"','"+RECRUITER+"')");
//			System.out.println("pst====>" + pst);
			rs = pst.executeQuery();
			List<List<String>> userTypeList = new ArrayList<List<String>>();			
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("user_type_id"));
				innerList.add(rs.getString("user_type"));
				innerList.add(rs.getString("visibility_id"));
				userTypeList.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("userTypeList", userTypeList);
			
//			pst = con.prepareStatement("select * from designation_details");
////			System.out.println("pst====>" + pst);
//			rs = pst.executeQuery();
//			List<List<String>> desigList=new ArrayList<List<String>>();			
//			while (rs.next()) {
//				List<String> innerList=new ArrayList<String>();
//				innerList.add(rs.getString("designation_id"));
//				innerList.add(rs.getString("designation_code"));
//				innerList.add(rs.getString("designation_name"));
//				innerList.add(rs.getString("designation_description"));
//				innerList.add(rs.getString("level_id"));
//				innerList.add(rs.getString("attribute_ids"));
//				
//				desigList.add(innerList);
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("desigList", desigList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void deleteWorkFlowGroup() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from work_flow_member where group_id=?");
			pst.setInt(1,uF.parseToInt(getGroup_id()));
//			System.out.println("pst====>" + pst);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void updateGroupDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from work_flow_member where group_id=?");
			pst.setInt(1,uF.parseToInt(getGroup_id()));
			pst.execute();
			pst.close();
						
			
			pst = con.prepareStatement("select * from user_type");
			rs = pst.executeQuery();
			Map<String,String> hmUser=new HashMap<String, String>();		
			while (rs.next()) {
				hmUser.put(rs.getString("user_type_id"), rs.getString("user_type"));
			}
			rs.close();
			pst.close();
		
		
//			pst = con.prepareStatement("select * from designation_details");
//			rs = pst.executeQuery();
//			Map<String,String> hmDesig=new HashMap<String, String>();		
//			while (rs.next()) {				
//				hmDesig.put(rs.getString("designation_id"), rs.getString("designation_name"));
//			}
//			rs.close();
//			pst.close();
			
			String[] userid=request.getParameterValues("userid");

			if(userid!=null && userid.length>0){
				
				for(int i=0;i<userid.length;i++){
					pst = con.prepareStatement("insert into work_flow_member(work_flow_mem,member_type,group_name,group_id,member_id,org_id," +
						"wlocation_id,is_default)values(?,?,?,?, ?,?,?,?)");
					pst.setString(1,hmUser.get(userid[i].trim()));
					pst.setInt(2,1);
					pst.setString(3, getGroupName());
					pst.setInt(4, uF.parseToInt(getGroup_id()));
					pst.setInt(5, uF.parseToInt(userid[i]));
					pst.setInt(6, uF.parseToInt(getOrganization()));
					pst.setInt(7, uF.parseToInt(getLocation()));
					pst.setBoolean(8, false);
					System.out.println("====>Workforcepst"+pst);
					pst.execute();
					pst.close();
				}
			}
			
//			String[] desigid=request.getParameterValues("desigid");
//
//			if(desigid!=null && desigid.length>0){
//				
//				for(int i=0;i<desigid.length;i++){
//					pst = con.prepareStatement("insert into work_flow_member(work_flow_mem,member_type," +
//							"group_name,group_id,member_id)" +
//							"values(?,?,?,?,?)");
//					pst.setString(1,hmDesig.get(desigid[i].trim()));
//					pst.setInt(2,2);
//					pst.setString(3, getGroupName());
//					pst.setInt(4, uF.parseToInt(getGroup_id()));
//					pst.setInt(5, uF.parseToInt(desigid[i]));
//					pst.execute();
//					pst.close();
//				}
//			}
			
			String anyone=request.getParameter("anyone");
			if(anyone !=null){
				pst = con.prepareStatement("insert into work_flow_member(work_flow_mem,member_type,group_name,group_id,member_id,org_id," +
						"wlocation_id,is_default)values(?,?,?,?, ?,?,?,?)");
				pst.setString(1,"Any One");
				pst.setInt(2,3);
				pst.setString(3, getGroupName());
				pst.setInt(4, uF.parseToInt(getGroup_id()));
				pst.setInt(5, uF.parseToInt(anyone));
				pst.setInt(6, uF.parseToInt(getOrganization()));
				pst.setInt(7, uF.parseToInt(getLocation()));
				pst.setBoolean(8, false);
				pst.execute();
				pst.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getGroupDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select member_id,group_name,member_type from work_flow_member where group_id=?");
			pst.setInt(1,uF.parseToInt(getGroup_id()));
//			System.out.println("pst====>" + pst);
			rs = pst.executeQuery();
			Map<String, String> hmGroup=new HashMap<String, String>();
			while (rs.next()) {
				setGroupName(rs.getString("group_name"));
				setGroupType(rs.getString("member_type"));
				hmGroup.put(rs.getString("member_id"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmGroup", hmGroup);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void insertWorkFlowMemberGroup() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			int group_id1 = 0;
			pst = con.prepareStatement("select max(group_id)as group_id from work_flow_member ");
//			System.out.println("pst====>" + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				group_id1 = rs.getInt("group_id");
			}
			rs.close();
			pst.close();
			group_id1++;
			
			pst = con.prepareStatement("select * from user_type");
//			System.out.println("pst====>" + pst);
			rs = pst.executeQuery();
			Map<String,String> hmUser=new HashMap<String, String>();		
			while (rs.next()) {
				hmUser.put(rs.getString("user_type_id"), rs.getString("user_type"));
			}
			rs.close();
			pst.close();
		
			String[] userid=request.getParameterValues("userid");

			if(userid!=null && userid.length>0){				
				for(int i=0;i<userid.length;i++){
					pst = con.prepareStatement("insert into work_flow_member(work_flow_mem,member_type,group_name,group_id,member_id,org_id," +
						"wlocation_id,is_default)values(?,?,?,?, ?,?,?,?)");
					pst.setString(1,hmUser.get(userid[i].trim()));
					pst.setInt(2,1);
					pst.setString(3, getGroupName());
					pst.setInt(4, group_id1);
					pst.setInt(5, uF.parseToInt(userid[i]));
					pst.setInt(6, uF.parseToInt(getOrganization()));
					pst.setInt(7, uF.parseToInt(getLocation()));
					pst.setBoolean(8, false);
					pst.execute();
					pst.close();
				}
			}
			
			String anyone=request.getParameter("anyone");
			if(anyone !=null){
				pst = con.prepareStatement("insert into work_flow_member(work_flow_mem,member_type,group_name,group_id,member_id,org_id," +
						"wlocation_id,is_default)values(?,?,?,?, ?,?,?,?)");
				pst.setString(1,"Any One"); 
				pst.setInt(2,3);
				pst.setString(3, getGroupName());
				pst.setInt(4, group_id1);
				pst.setInt(5, uF.parseToInt(anyone));
				pst.setInt(6, uF.parseToInt(getOrganization()));
				pst.setInt(7, uF.parseToInt(getLocation()));
				pst.setBoolean(8, false);
				pst.execute();
				pst.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}
	
	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
    
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

}
