package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditAndDeleteQuotes extends ActionSupport implements ServletRequestAware, IStatements{
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	private String operation;
	private String thoughtId;
	
	private String strQuotedesc;
	private String strQuoteBy;
	
	private String strQuotedesc1;
	private String strQuoteBy1;
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF == null) {
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
//		System.out.println(""+getOperation());
		UtilityFunctions uF = new UtilityFunctions();
		getLoggedEmpImage(uF);
		setThoughtId(request.getParameter("thoughtId"));
		if(getOperation() != null && getOperation().equals("Q_E")) {
			getQuoteData();
		}else if (operation != null && (operation.equals("U") || operation.equals("C"))) {
			updateQuote();
		} else if (operation != null && operation.equals("Q_D")) {
			deleteQuote();
		}
		return LOAD;
	}

	public void getLoggedEmpImage(UtilityFunctions uF){
		Connection con = null;
		
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			Map<String, List<List<String>>> hmComments = new HashMap<String, List<List<String>>>();
			Map<String, String> hmLastCommentId = new HashMap<String, String>();
			
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String createdByImage = hmCustImage.get(strSessionEmpId);
				String strClientId = CF.getClientIdBySPOCId(con, uF, strSessionEmpId);
				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
	//			System.out.println("MyLargeImage==>"+ MYLargeImage);
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			} else {
				String createdByImage = hmResourceImage.get(strSessionEmpId);
				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	
	}
public void getQuoteData(){
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();
	try {
		con = db.makeConnection(con);
		
//		System.out.println("inside getQuotedata==>"+getThoughtId());
		pst = con.prepareStatement("select * from daythoughts where thought_id = ?");
		pst.setInt(1, uF.parseToInt(getThoughtId()));
		
		rs = pst.executeQuery();
		while(rs.next()) {

			setStrQuoteBy(rs.getString("thought_by"));
			setStrQuotedesc(rs.getString("thought_text"));
		   
			
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

public void deleteQuote(){
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();

	try {
		con = db.makeConnection(con);
		pst = con.prepareStatement("delete from daythoughts where thought_id = ?");
		pst.setInt(1, uF.parseToInt(request.getParameter("thoughtId")));
		pst.execute();
		pst.close();
		request.setAttribute(MESSAGE, "Quote deleted successfully!");
	} catch (Exception e) {
		e.printStackTrace();
		request.setAttribute(MESSAGE, "Error in deletion");
	}finally{
		
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

public void updateQuote(){
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();
//	System.out.println("inside updating quote");
	
	try {
		String strDomain = request.getServerName().split("\\.")[0];
		con = db.makeConnection(con);
		Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmCustName = CF.getCustomerNameMap(con);
		Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
		Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
		
		List<String> alInner = new ArrayList<String>();
		if(getOperation() != null && getOperation().equals("U")) {
				
				pst = con.prepareStatement("update daythoughts set thought_by=?, thought_text=?, entry_date=?,updated_by=?,last_updated=? where thought_id=?");
				if(getStrQuoteBy1() != null && !getStrQuoteBy1().equals("")) {
					pst.setString(1,getStrQuoteBy1().replace("::", "&"));
				}else {
					pst.setString(1,getStrQuoteBy1());
				}
				if(getStrQuoteBy1() != null && !getStrQuoteBy1().equals("")) {
					pst.setString(2,getStrQuotedesc1().replace("::", "&"));
				}else {
					pst.setString(2,getStrQuotedesc1());
				}
				
				pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(6,uF.parseToInt(getThoughtId()));
				pst.executeUpdate();
				pst.close();
				
				if(hmResourceName != null){
					Set empIdSet  = hmResourceName.keySet();
					Iterator<String> it = empIdSet.iterator();
					while(it.hasNext()){
						String empId = it.next();
						String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> updated quote by <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrQuoteBy1()+"</b></div></div>";
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.set_type(NEWS_AND_ALERTS);
						userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=Q");
					}
				}
			}
		
		pst = con.prepareStatement("select * from daythoughts where thought_id = ?");
		pst.setInt(1, uF.parseToInt(getThoughtId()));
		rs = pst.executeQuery();
		
		while(rs.next()){
			
			alInner.add(Integer.toString(rs.getInt("thought_id")));//0
			alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//1
			
			alInner.add(rs.getString("thought_by"));//2
			alInner.add(rs.getString("thought_text"));//3
			String addedBy = rs.getString("added_by");
			alInner.add(uF.showData(hmResourceName.get(rs.getString("added_by")),"Someone"));//4
			
			alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//5
			alInner.add(rs.getString("added_by"));//6
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String createdByImage1 = hmCustImage.get(rs.getString("added_by"));
				String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("added_by"));
				String MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
			  	} else if(createdByImage1 != null && !createdByImage1.equals("")) {
			  		MYImage1= "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage1+"\" />";
	            }
				alInner.add(MYImage1);//7
			} else {
				String createdByImage1 = hmResourceImage.get(rs.getString("added_by"));
				String MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
				} else if(createdByImage1 != null && !createdByImage1.equals("")) {
			  		MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("added_by")+"/"+I_22x22+"/"+createdByImage1+"\" />";
				}
				
				alInner.add(MYImage1);//7
			}
			
		}
		request.setAttribute("alInner", alInner);
//		System.out.println("alInner==>"+alInner);
		
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


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public String getThoughtId() {
		return thoughtId;
	}


	public void setThoughtId(String thoughtId) {
		this.thoughtId = thoughtId;
	}


	public String getStrQuotedesc() {
		return strQuotedesc;
	}


	public void setStrQuotedesc(String strQuotedesc) {
		this.strQuotedesc = strQuotedesc;
	}


	public String getStrQuoteBy() {
		return strQuoteBy;
	}


	public void setStrQuoteBy(String strQuoteBy) {
		this.strQuoteBy = strQuoteBy;
	}


	public String getStrQuotedesc1() {
		return strQuotedesc1;
	}


	public void setStrQuotedesc1(String strQuotedesc1) {
		this.strQuotedesc1 = strQuotedesc1;
	}


	public String getStrQuoteBy1() {
		return strQuoteBy1;
	}


	public void setStrQuoteBy1(String strQuoteBy1) {
		this.strQuoteBy1 = strQuoteBy1;
	}
	
}
