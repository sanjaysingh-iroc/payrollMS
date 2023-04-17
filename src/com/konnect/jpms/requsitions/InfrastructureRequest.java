package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillInfrastructureType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class InfrastructureRequest extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(InfrastructureRequest.class);
	
	
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(USERTYPE);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		

		request.setAttribute(PAGE, PInfrastructureRequest);
		request.setAttribute(TITLE, "");
		
		
		
		
		if(getStrPurpose()!=null && getStrPurpose().length()>0){
			int nRequisitionId = saveRequest();
			addInfrastructureRequest(nRequisitionId);
			loadInfrastructureRequest();
			return SUCCESS;
		}

		loadInfrastructureRequest();
		return LOAD;
	}
	
	public void loadInfrastructureRequest(){
		setHmModeRequest(fillModeRequests());
		wLocationList = new FillWLocation(request).fillWLocation();
		strInfraTypeList = new FillInfrastructureType(request).fillInfrastructureType();
		
		setStrPurpose(null);
		setStrRequestMode("SC");
		
	}
	
	private Map<String, String> fillModeRequests(){
		
		Map<String, String> hmRequests = new HashMap<String, String>(); 
		hmRequests.put("HC", "Hard Copy");
		hmRequests.put("SC", "Soft Copy");
		
		setStrRequestMode("SC");
		
		return hmRequests;
	}

	public int saveRequest(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try{
			
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into requisition_details (requisition_date, requisition_type, emp_id) values (?,?,?)");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(2, "IR");
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
			
			pst.execute();
			pst.close();
			

			
			pst = con.prepareStatement("select max(requisition_id) as requisition_id from requisition_details where emp_id=?");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			while(rs.next()){
				nRequisitionId = rs.getInt("requisition_id");
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return nRequisitionId;
	}

	
	public String addInfrastructureRequest(int nRequisitionId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into requisition_infrastructure (requisition_id, from_date, to_date, from_time, to_time, infrastructure_name, infrastructure_type, purpose, wlocation_id, _mode) values (?,?,?,?,?,?,?,?,?,?)");
			
			pst.setInt(1, nRequisitionId);
			pst.setDate(2, uF.getDateFormat(getStrFromDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrToDate(), DATE_FORMAT));
			pst.setTime(4, uF.getTimeFormat(getStrFromTime(), TIME_FORMAT));
			pst.setTime(5, uF.getTimeFormat(getStrToTime(), TIME_FORMAT));
			pst.setString(6, getStrInfraName());
			pst.setString(7, getStrInfraType());
			pst.setString(8, getStrPurpose());
			pst.setInt(9, uF.parseToInt(getwLocation()));
			pst.setString(10, getStrRequestMode());
			
			pst.execute();
			pst.close();
			
				
			
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_EMPLOYEE_REQUISITION_REQUEST, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId((String)session.getAttribute(EMPID));
//			nF.setStrHostAddress(request.getRemoteHost());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpReqType(getStrInfraType());
			nF.setStrEmpReqMode((strRequestMode!=null && strRequestMode.equalsIgnoreCase("SC")?"soft copy":"hard copy"));
			nF.setStrEmpReqPurpose(strPurpose);
			nF.setStrEmpReqFrom(uF.getDateFormat(getStrFromDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" "+uF.getDateFormat(getStrFromTime(), TIME_FORMAT, CF.getStrReportTimeAM_PMFormat()) );
			nF.setStrEmpReqTo(uF.getDateFormat(getStrToDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" "+uF.getDateFormat(getStrToTime(), TIME_FORMAT, CF.getStrReportTimeAM_PMFormat()) );
			nF.setEmailTemplate(true);
			nF.sendNotifications();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return UPDATE;
		
	}
	
	Map<String, String> hmModeRequest;
	
	String strFromDate;
	String strToDate;
	String strFromTime;
	String strToTime;
	String strInfraName;
	String strInfraType;
	String wLocation;
	String strPurpose;
	String strRequestMode;
	
	List<FillWLocation> wLocationList;
	List<FillInfrastructureType> strInfraTypeList;
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public Map<String, String> getHmModeRequest() {
		return hmModeRequest;
	}

	public void setHmModeRequest(Map<String, String> hmModeRequest) {
		this.hmModeRequest = hmModeRequest;
	}

	public String getStrFromDate() {
		return strFromDate;
	}

	public void setStrFromDate(String strFromDate) {
		this.strFromDate = strFromDate;
	}

	public String getStrToDate() {
		return strToDate;
	}

	public void setStrToDate(String strToDate) {
		this.strToDate = strToDate;
	}

	public String getStrFromTime() {
		return strFromTime;
	}

	public void setStrFromTime(String strFromTime) {
		this.strFromTime = strFromTime;
	}

	public String getStrToTime() {
		return strToTime;
	}

	public void setStrToTime(String strToTime) {
		this.strToTime = strToTime;
	}

	public String getStrInfraName() {
		return strInfraName;
	}

	public void setStrInfraName(String strInfraName) {
		this.strInfraName = strInfraName;
	}

	public String getStrInfraType() {
		return strInfraType;
	}

	public void setStrInfraType(String strInfraType) {
		this.strInfraType = strInfraType;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String getStrPurpose() {
		return strPurpose;
	}

	public void setStrPurpose(String strPurpose) {
		this.strPurpose = strPurpose;
	}

	public String getStrRequestMode() {
		return strRequestMode;
	}

	public void setStrRequestMode(String strRequestMode) {
		this.strRequestMode = strRequestMode;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillInfrastructureType> getStrInfraTypeList() {
		return strInfraTypeList;
	}

	public void setStrInfraTypeList(List<FillInfrastructureType> strInfraTypeList) {
		this.strInfraTypeList = strInfraTypeList;
	}

	
	
}
