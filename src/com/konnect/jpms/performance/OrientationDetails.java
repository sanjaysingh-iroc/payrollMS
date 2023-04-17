package com.konnect.jpms.performance;

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

public class OrientationDetails extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String operation;
	
	private String strOrg;
	private String userscreen;
	private String navigationId;
	private String toPage;
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/OrientationDetails.jsp");
		request.setAttribute(TITLE, "Orientation Details");
		getOrientationList();
		getOrientationMemberList();
		if(getOperation() != null) {
			insertData();
		}
		getData();
		
		
		return SUCCESS;

	}
	
	public void insertData(){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from orientation_details");
			pst.execute();
			pst.close();
			
			List<List<String>> outerList=(List<List<String>>)request.getAttribute("outerList");
			List<List<String>>  memberList=(List<List<String>>  )request.getAttribute("memberList");
			for(int i=0;i<outerList.size();i++) {
				List<String> innerList=outerList.get(i);
				for(int j=0;j<memberList.size();j++) {
					List<String> memberInner=memberList.get(j);
					String val=request.getParameter(memberInner.get(2)+"orientation"+innerList.get(0));
					
					if(val!=null) {
						pst = con.prepareStatement("insert into orientation_details(orientation_id,member_id)values(?,?)");
						pst.setInt(1,uF.parseToInt(innerList.get(0)));
						pst.setInt(2,uF.parseToInt(memberInner.get(2)));
						pst.execute();
						pst.close();
					}
//					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getData(){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String,String> mp=new HashMap<String,String>();
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_details");
			rs=pst.executeQuery();
			while(rs.next()){
				mp.put(rs.getString("member_id")+"orientation"+rs.getString("orientation_id"), "");
			}
			rs.close();
			pst.close();
			System.out.println("mp : "+mp);
			request.setAttribute("mp", mp);//Created by Dattatray Date:09-09-21 Note Mistakaly delete line
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getOrientationList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from apparisal_orientation order by apparisal_orientation_id");//Created by Dattatray Date:08-09-21 Note order By set
			rs = pst.executeQuery();
			List<List<String>> outerList=new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("apparisal_orientation_id"));
				innerList.add(rs.getString("orientation_name"));
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("outerList", outerList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getOrientationMemberList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			List<List<String>> outerList=new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("orientation_member_id"));
				innerList.add(rs.getString("member_name"));
				innerList.add(rs.getString("member_id"));
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("memberList",outerList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	
}