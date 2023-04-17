package com.konnect.jpms.leave;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TravelBookingAttachment extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public CommonFunctions CF = null;
	
	String strUserType = null;  
	String strSesionEmpId = null;
	
	String travelId;
	String strEmpId;
	String submit;
	File[] strBooking;
	String[] strBookingFileName;
	
	public String execute() throws Exception { 
		session=request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSesionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getSubmit()!=null){
			uploadAttachment(uF);
			return SUCCESS;
		}
		
		return loadTravelBooking(uF);

	}
	
	private void uploadAttachment(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con=db.makeConnection(con);
			
			for (int i = 0; getStrBooking() != null && i < getStrBooking().length; i++) {
				String strFileName = null;
				if(CF.getStrDocSaveLocation()==null){
					strFileName= uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrBooking()[i], getStrBookingFileName()[i], getStrBookingFileName()[i], CF);
				}else{
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_TRAVELS+"/"+I_DOCUMENT+"/"+getStrEmpId()+"/"+getTravelId(), getStrBooking()[i], getStrBookingFileName()[i], getStrBookingFileName()[i], CF);
				} 
				
				if(strFileName!=null && !strFileName.trim().equals("") && !strFileName.trim().equalsIgnoreCase("NULL")){
					pst = con.prepareStatement("insert into travel_booking_documents(travel_id,emp_id,document_name,added_by,added_date)" +
							" values(?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(getTravelId()));
					pst.setInt(2, uF.parseToInt(getStrEmpId()));
					pst.setString(3, strFileName);
					pst.setInt(4, uF.parseToInt(strSesionEmpId));
					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					int x = pst.executeUpdate();
					if(x > 0){
						String strDomain = request.getServerName().split("\\.")[0];
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(getStrEmpId());
						userAlerts.set_type(TRAVEL_BOOKING_ALERT);
						userAlerts.setStatus(INSERT_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
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

	private String loadTravelBooking(UtilityFunctions uF) {
		return LOAD;
	}

	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getTravelId() {
		return travelId;
	}

	public void setTravelId(String travelId) {
		this.travelId = travelId;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public File[] getStrBooking() {
		return strBooking;
	}

	public void setStrBooking(File[] strBooking) {
		this.strBooking = strBooking;
	}

	public String[] getStrBookingFileName() {
		return strBookingFileName;
	}

	public void setStrBookingFileName(String[] strBookingFileName) {
		this.strBookingFileName = strBookingFileName;
	}
	
}
