package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

public class KRADetails extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF; 
	
	private String type;  
 
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		request.setAttribute(PAGE, "/jsp/performance/KRADetails.jsp");
		request.setAttribute(TITLE, "Key Responsibility Area");
		if(getType()==null || getType().equals("")){
			getLevelWiseKRADetails();
		}else{
			request.setAttribute(PAGE, "/jsp/performance/KRAAttributeWise.jsp");
			getAttributeWiseKRADetails();
		}
		return SUCCESS;
	}
	
	

	private void getAttributeWiseKRADetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst=null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from level_details");
			rst=pst.executeQuery();
			Map<String, String> hmLevel=new HashMap<String, String>();
			while(rst.next()){
				hmLevel.put(rst.getString("level_id"),"["+rst.getString("level_code")+"] "+rst.getString("level_name"));
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmLevel", hmLevel);
			
				
			pst = con.prepareStatement("select * from appraisal_attribute");
			rst=pst.executeQuery();
			List<List<String>> attributeOuterList=new ArrayList<List<String>>();
			while(rst.next()){
				List<String> levelList=new ArrayList<String>();
				levelList.add(rst.getString("arribute_id"));
				levelList.add(rst.getString("attribute_name"));
				
				attributeOuterList.add(levelList);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("attributeOuterList", attributeOuterList);
			
			
			pst = con.prepareStatement("select * from kra_details");
			rst=pst.executeQuery();
			Map<String, List<List<String>>> hmAttributeWiseKRA=new HashMap<String, List<List<String>>>();
			while(rst.next()){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rst.getString("kra_id"));
				innerList.add(rst.getString("kra"));
				innerList.add(rst.getString("kra_desc"));
				innerList.add(rst.getString("attribute_id"));
				innerList.add(rst.getString("level_id"));
				
				String measurable="No";
				if(rst.getString("measurable")!=null && !rst.getString("measurable").equals("")){
					if(rst.getString("measurable").equals("1")){
						measurable="Yes";
					}else {
						measurable="No";
					}
				}				
				innerList.add(measurable);				
				
				List<List<String>> outerList=hmAttributeWiseKRA.get(rst.getString("attribute_id"));
				if(outerList==null) outerList=new ArrayList<List<String>>();
				
				outerList.add(innerList);
				
				hmAttributeWiseKRA.put(rst.getString("attribute_id"), outerList);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmAttributeWiseKRA", hmAttributeWiseKRA);
//			System.out.println("hmAttributeWiseKRA======>"+hmAttributeWiseKRA);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}



	private void getLevelWiseKRADetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst=null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from appraisal_attribute");
			rst=pst.executeQuery();
			Map<String, String> hmAttribute=new HashMap<String, String>();
			while(rst.next()){
				hmAttribute.put(rst.getString("arribute_id"),rst.getString("attribute_name"));
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmAttribute", hmAttribute);
			
				
			pst = con.prepareStatement("select * from level_details");
			rst=pst.executeQuery();
			List<List<String>> levelOuterList=new ArrayList<List<String>>();
			while(rst.next()){
				List<String> levelList=new ArrayList<String>();
				levelList.add(rst.getString("level_id"));
				levelList.add(rst.getString("level_code"));
				levelList.add(rst.getString("level_name"));
				
				levelOuterList.add(levelList);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("levelOuterList", levelOuterList);
			
			pst = con.prepareStatement("select * from kra_details");
			rst=pst.executeQuery();
			Map<String, List<List<String>>> hmLevelWiseKRA=new HashMap<String, List<List<String>>>();
			while(rst.next()){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rst.getString("kra_id"));
				innerList.add(rst.getString("kra"));
				innerList.add(rst.getString("kra_desc"));
				innerList.add(rst.getString("attribute_id"));
				innerList.add(rst.getString("level_id"));
				
				String measurable="No";
				if(rst.getString("measurable")!=null && !rst.getString("measurable").equals("")){
					if(rst.getString("measurable").equals("1")){
						measurable="Yes";
					}else {
						measurable="No";
					}
				}				
				innerList.add(measurable);				
				
				List<List<String>> outerList=hmLevelWiseKRA.get(rst.getString("level_id"));
				if(outerList==null) outerList=new ArrayList<List<String>>();
				
				outerList.add(innerList);
				
				hmLevelWiseKRA.put(rst.getString("level_id"), outerList);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmLevelWiseKRA", hmLevelWiseKRA);
//			System.out.println("hmLevelWiseKRA======>"+hmLevelWiseKRA);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
