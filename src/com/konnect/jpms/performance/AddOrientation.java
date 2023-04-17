package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
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

public class AddOrientation extends ActionSupport implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6334552912842090115L;
		
	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	private CommonFunctions CF;
	
	private String ID;
	private String operation;
	private String orientName;
	private String btnSubmit;

	private String strOrg;
	private String userscreen;
	private String navigationId;
	private String toPage;
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		UtilityFunctions uF=new UtilityFunctions();
		
		if(getOperation()!=null && getOperation().equals("A")) {
//			getData(uF.parseToInt(getID()));
			getOrientationMemberList();
			if(getBtnSubmit() != null) {
				inertOrientation(uF);
				return "update";
			}
		} else if(getOperation()!=null && getOperation().equals("E")) {
			getData(uF.parseToInt(getID()));
			getOrientationMemberList();
			if(getBtnSubmit() != null) {
				updateOrientation(uF.parseToInt(getID()));
				return "update";
			}
		} else if(getOperation()!=null && getOperation().equals("D")) {
				deleteOrientation(uF.parseToInt(getID()));
				return "update";
		}
		
		
		return SUCCESS;

	}
	
	
	private void deleteOrientation(int orientId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from apparisal_orientation where apparisal_orientation_id=?");
			pst.setInt(1, orientId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from orientation_details where orientation_id=?");
			pst.setInt(1, orientId);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void inertOrientation(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into apparisal_orientation(orientation_name) values(?)");
			pst.setString(1, getOrientName());
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(apparisal_orientation_id) as apparisal_orientation_id from apparisal_orientation");
			rs = pst.executeQuery();
			int orientId = 0;
			while (rs.next()) {
				orientId = rs.getInt("apparisal_orientation_id");
			}
			rs.close();
			pst.close();
			
			List<List<String>>  memberList = (List<List<String>>)request.getAttribute("memberList");
			for(int j=0; j<memberList.size(); j++) {
				List<String> memberInner = memberList.get(j);
				String val = request.getParameter(memberInner.get(2));
				String valView = request.getParameter(memberInner.get(2)+"_view");
				String valEdit = request.getParameter(memberInner.get(2)+"_edit");
				if(val != null) {
					pst = con.prepareStatement("insert into orientation_details(orientation_id, member_id,view_access,edit_access) values(?,?,?,?)");
//						pst=con.prepareStatement(insertOrientation);
					pst.setInt(1, orientId);
					pst.setInt(2, uF.parseToInt(memberInner.get(2)));
					if(valView != null) {
						pst.setBoolean(3, true);
					} else {
						pst.setBoolean(3, false);
					}
					if(valEdit != null) {
						pst.setBoolean(4, true);
					} else {
						pst.setBoolean(4, false);
					}
					pst.execute();
					pst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void updateOrientation(int orientId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update apparisal_orientation set orientation_name=? where apparisal_orientation_id=?");
			pst.setString(1, getOrientName());
			pst.setInt(2, orientId);
			pst.execute();
//			System.out.println("pst ===>> " + pst);
			pst.close();
			
			pst = con.prepareStatement("delete from orientation_details where orientation_id=?");
			pst.setInt(1, orientId);
			pst.execute();
			pst.close();
			
			List<List<String>>  memberList = (List<List<String>>)request.getAttribute("memberList");
			for(int j=0; j<memberList.size(); j++) {
				List<String> memberInner = memberList.get(j);
				String val = request.getParameter(memberInner.get(2));
				String valView = request.getParameter(memberInner.get(2)+"_view");
				String valEdit = request.getParameter(memberInner.get(2)+"_edit");
				if(val!=null) {
					pst = con.prepareStatement("insert into orientation_details(orientation_id,member_id,view_access,edit_access) values(?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getID()));
					pst.setInt(2, uF.parseToInt(memberInner.get(2)));
					if(valView != null) {
						pst.setBoolean(3, true);
					} else {
						pst.setBoolean(3, false);
					}
					if(valEdit != null) {
						pst.setBoolean(4, true);
					} else {
						pst.setBoolean(4, false);
					}
					pst.execute();
					pst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getData(int id) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			Map<String, List<String>> mp = new HashMap<String, List<String>>();
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from apparisal_orientation ao, orientation_details od where od.orientation_id = ao.apparisal_orientation_id and od.orientation_id=? ");
//			pst = con.prepareStatement(selectOrientation);
			pst.setInt(1, id);
			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("orientation_details_id"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("view_access"));
				innerList.add(rs.getString("edit_access"));
				mp.put(rs.getString("member_id"), innerList);
				if(getOrientName() == null || getOrientName().equals("")) {
					setOrientName(rs.getString("orientation_name"));
				}
			}
			rs.close();
			pst.close();	
			request.setAttribute("mp", mp);
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
			pst = con.prepareStatement("select * from orientation_member where status = true order by weightage");
	//		pst = con.prepareStatement(selectOrientationMember);
			rs = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
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
			request.setAttribute("memberList", outerList);
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getBtnSubmit() {
		return btnSubmit;
	}
	
	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public String getOrientName() {
		return orientName;
	}

	public void setOrientName(String orientName) {
		this.orientName = orientName;
	}

	
	public String getStrOrg() {
		return strOrg;
	}


	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
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


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;		
	}

}
