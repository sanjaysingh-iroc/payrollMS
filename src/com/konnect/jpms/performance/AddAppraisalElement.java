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

public class AddAppraisalElement extends ActionSupport implements ServletRequestAware, IStatements  {

	private static final long serialVersionUID = -6298007526866999392L;
	private String ID;
	private String operation;
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

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	private CommonFunctions CF;
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/OrientationDetails.jsp");
		request.setAttribute(TITLE, "Appraisal Summary");
		UtilityFunctions uF=new UtilityFunctions();
		
		if(getOperation()!=null && getOperation().equals("A")){
			getData(uF.parseToInt(getID()));
			getOrientationMemberList();
		}else if(getOperation()!=null && getOperation().equals("E")){
			getOrientationMemberList();
			insertData(uF.parseToInt(getID()));
			return "update";
		}
		
		return SUCCESS;

	}
public void insertData(int id){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		UtilityFunctions uF=new UtilityFunctions();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from appraisal_element_attribute where appraisal_element=?");
//			pst = con.prepareStatement(deleteElementAttribute);
			pst.setInt(1, id);
			pst.execute();
			pst.close(); 
			
			List<List<String>>  memberList=(List<List<String>>  )request.getAttribute("innerList");
				for(int j=0;j<memberList.size();j++){
					List<String> memberInner=memberList.get(j);
					String val=request.getParameter(memberInner.get(0));
					
					if(val!=null){
						pst = con.prepareStatement("insert into appraisal_element_attribute(appraisal_element,appraisal_attribute,status)values(?,?,?)");
//						pst = con.prepareStatement(insertElementAttribute);
						pst.setInt(1,uF.parseToInt(getID()));
						pst.setInt(2,uF.parseToInt(memberInner.get(0)));
						pst.setBoolean(3,true);
						pst.execute();
						pst.close();
					}
//					
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
public void getData(int id){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		db.setRequest(request);

		try {
			Map<String,String> mp=new HashMap<String,String>();
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element_attribute where appraisal_element=?");
//			pst=con.prepareStatement(selectElementAttribute);
			pst.setInt(1, id);
			rs=pst.executeQuery();
			while(rs.next()){
				mp.put(rs.getString("appraisal_attribute"), "");
				
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
public void getOrientationMemberList() {

	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	ResultSet rs = null;
	db.setRequest(request);
	try {

		con = db.makeConnection(con);
		pst = con.prepareStatement("select * from appraisal_attribute where status=true");
//		pst = con.prepareStatement(selectAttribute);
		rs = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
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
		this.request=request;
		
	}

}
