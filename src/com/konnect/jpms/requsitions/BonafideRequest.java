package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BonafideRequest extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(BonafideRequest.class);
	
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(USERTYPE);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		

		request.setAttribute(PAGE, PReqBonafide);
		request.setAttribute(TITLE, "");
		
		
		
		
		if(getStrPurpose()!=null && getStrPurpose().length()>0){
		
			int nRequisitionId = saveRequest();
			addBonafideRequest(nRequisitionId);
			loadRequisitions();
			return SUCCESS;
		}
		
		loadRequisitions();
		return LOAD;
		
	}
	
	public void loadRequisitions(){
		setHmModeRequest(fillModeRequests());
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
			pst.setString(2, "BF");
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

	
	public String addBonafideRequest(int nRequisitionId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into requisition_bonafide (requisition_id, purpose, _mode) values (?,?,?)");
			pst.setInt(1, nRequisitionId);
			pst.setString(2, strPurpose);
			pst.setString(3, strRequestMode);
			
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
			nF.setStrEmpReqType("bonafide certificate"); 
			nF.setStrEmpReqMode((strRequestMode!=null && strRequestMode.equalsIgnoreCase("SC")?"soft copy":"hard copy"));
			nF.setStrEmpReqPurpose(strPurpose);
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
	
	String strRT;
	String strPurpose;
	String strRequestMode;
	
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

	public String getStrRT() {
		return strRT;
	}

	public void setStrRT(String strRT) {
		this.strRT = strRT;
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
	
}
