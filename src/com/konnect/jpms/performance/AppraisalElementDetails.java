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

public class AppraisalElementDetails  extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String operation;
	
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
		request.setAttribute(PAGE, "/jsp/performance/AppraisalElementDetails.jsp");
		request.setAttribute(TITLE, "Appraisal Element Details");
		getElementList();
		getOrientationMemberList();
//		if(getOperation()!=null){
//			insertData();
//		}
		getData();
		return SUCCESS;

	}
	
//	public void insertData(){
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		ResultSet rs = null;
//		UtilityFunctions uF=new UtilityFunctions();
//
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("delete from orientation_details");
//			pst.execute();
//			
//			List<List<String>> outerList=(List<List<String>>)request.getAttribute("outerList");
//			List<List<String>>  memberList=(List<List<String>>  )request.getAttribute("innerList");
//			for(int i=0;i<outerList.size();i++){
//				List<String> innerList=outerList.get(i);
//				for(int j=0;j<memberList.size();j++){
//					List<String> memberInner=memberList.get(j);
//					String val=request.getParameter(memberInner.get(0)+"orientation"+innerList.get(0));
//					
//					if(val!=null){
//						pst = con.prepareStatement("insert into orientation_details(orientation_id,member_id)values(?,?)");
//						pst.setInt(1,uF.parseToInt(innerList.get(0)));
//						pst.setInt(2,uF.parseToInt(memberInner.get(0)));
//						pst.execute();
//					}
////					
//				}
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//		}
//	}
	
	public void getData(){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String,String> mp=new HashMap<String,String>();
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element_attribute");
//			pst = con.prepareStatement(selectAttribute);
			rs=pst.executeQuery();
			while(rs.next()){
				mp.put(rs.getString("appraisal_attribute")+"element"+rs.getString("appraisal_element"), "");
			}
			rs.close();
			pst.close();
					
			request.setAttribute("mp",mp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getElementList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element");
//			pst = con.prepareStatement(selectElement);
			rs = pst.executeQuery();
			List<List<String>> outerList=new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("appraisal_element_id"));
				innerList.add(rs.getString("appraisal_element_name"));
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("outerList",outerList);

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
			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
//			pst = con.prepareStatement(selectAttribute);
			rs = pst.executeQuery();
			List<List<String>> outerList=new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("arribute_id"));
				innerList.add(rs.getString("attribute_name"));
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("innerList",outerList);

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
}
