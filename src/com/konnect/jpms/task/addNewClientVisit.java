package com.konnect.jpms.task;

import java.sql.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import java.util.GregorianCalendar;


public class addNewClientVisit extends ActionSupport implements ServletRequestAware, IStatements {
	
	public HttpSession session;
	public CommonFunctions CF = null;
	private String[] strHrName;
	private String[] strClientName;
	public String strVisitDate;
	public String startTime;
	public String strVisitdesc;
	public String btnSubmit;
	private String visitId;
	private String operation;
	int visitId1;
	int emp_id;
	List<FillClients> clientlist;
	List<FillEmployee> HRList;
	String clientId;
	String clientName;
	String employeeId;
	String employeeName;
	
	UtilityFunctions uF = new UtilityFunctions();
	public String execute() throws Exception { 
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		int orgId = uF.parseToInt((String) session.getAttribute(ORGID));
		if(orgId == 0)
			orgId = 1;
		emp_id = uF.parseToInt((String) session.getAttribute(EMPID));
		clientlist = new FillClients(request).fillClients(false);
		HRList = new FillEmployee(request).fillEmployeeNameHR(""+emp_id, orgId, 0, CF, uF); 
		if(getbtnSubmit() != null && getbtnSubmit().equalsIgnoreCase("Submit")){
			addClientVisitData(uF);
			return LOAD;
		}
		if(getbtnSubmit()!=null && getbtnSubmit().equalsIgnoreCase("Update"))
		{
			updateClientVisit(uF);
			return LOAD;
		}
		if(getOperation() != null && getOperation().equals("E") ||  getOperation() == "E"){
			getClientVisitData(uF);
		}
		if(getOperation() != null && getOperation().equals("D") ||  getOperation() == "D")	{
			DeleteClientVisitData(uF);
		}
		
			return SUCCESS;
	}
	
	public Map<String,String> getHrDetails(Connection con)
	{
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		Map<String,String> hmHRDetails = new HashMap<String,String>();
		try{
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id "
					+ "and epd.is_alive= true and ud.emp_id=epd.emp_per_id and (usertype_id = (select user_type_id "
					+ "from user_type where user_type = '" + ADMIN + "') or (usertype_id = (select user_type_id from user_type where user_type = '" + HRMANAGER
					+ "') ");
				sbQuery.append(")) order by epd.emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while(rs.next()){
					hmHRDetails.put(rs.getString("emp_per_id"),rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				}
				rs.close();
				pst.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return hmHRDetails;
	}
	
	public void updateClientVisit(UtilityFunctions uF)
	{
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
	//	int rs = null;
		
		String visitId  = getVisitId();
		try{
			con = db.makeConnection(con);
			
			StringBuilder sbHrName = null;
			if(getStrHrName()!=null && getStrHrName().length>0){
				if(sbHrName == null){
					sbHrName = new StringBuilder();
					sbHrName.append(",");
				}
				for(String hrName:getStrHrName()){
					if(hrName!=null && !hrName.equals("")){
						sbHrName.append(hrName+",");
					}else{
						sbHrName = new StringBuilder();
						sbHrName.append("");
						break;
					}
				}
			}
			if(sbHrName == null) {
				sbHrName = new StringBuilder();
			}
			
			
			StringBuilder sbClientName = null;
			if(getStrClientName()!=null && getStrClientName().length>0){
				if(sbClientName == null){
					sbClientName = new StringBuilder();
					sbClientName.append(",");
				}
				for(String clientName:getStrClientName()){
					if(clientName!=null && !clientName.equals("")){
						sbClientName.append(clientName+",");
					}else{
						sbClientName = new StringBuilder();
						sbClientName.append("");
						break;
					}
				}
			}
			if(sbClientName == null) {
				sbClientName = new StringBuilder();
			}
			pst = con.prepareStatement("update hr_client_visit_details set hr_name = ?,client_name = ?,description = ?,time = ?,date = ? where visit_id = ?");
			pst.setString(1, sbHrName.toString());
			pst.setString(2, sbClientName.toString());
			
			pst.setString(3, getStrVisitdesc());
			pst.setTime(4, uF.getTimeFormat(getstartTime(), TIME_FORMAT));
			pst.setDate(5,uF.getDateFormat(getstrVisitDate(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getVisitId()));
		//	System.out.println(" update psttt============>"+pst);
			pst.executeUpdate();
			pst.close();
			
			
		}catch (Exception e) {
			e.printStackTrace();
			} finally {
				db.closeStatements(pst);
				db.closeConnection(con);
			}
	}
	
	public void DeleteClientVisitData(UtilityFunctions uF)
	{
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		String visitId  = getVisitId();
		try{
			StringBuilder sbQuery = new StringBuilder();
			con = db.makeConnection(con);
			sbQuery.append("delete from hr_client_visit_details where visit_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getVisitId()));
//			System.out.println("psttt============>"+pst);
			pst.executeUpdate();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
			} finally {
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			
	}
	
	public void getClientVisitData(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		String visitId  = getVisitId();
		try{
			StringBuilder sbQuery = new StringBuilder();
			con = db.makeConnection(con);
			Map<String, String> hmHrCodeMap = CF.getHrCodeMap(con, uF,1,0);
			Map<String, String> hmClientCodeMap = CF.getClientCodeMap(con,uF);
			List<String> hrList = CF.getHRList(con, uF);
			
			List<String> hrIdList = new ArrayList<String>();
			Set hrIdSet = hmHrCodeMap.keySet();
 			Iterator it = hrIdSet.iterator();
 			while(it.hasNext()){
 				hrIdList.add((String)it.next());
 			}
 			
 			List<String> clientList = CF.getClientList(con, uF);
			
			List<String> clientIdList = new ArrayList<String>();
			Set clientIdSet = hmClientCodeMap.keySet();
 			Iterator itclient = clientIdSet.iterator();
 			while(itclient.hasNext()){
 				clientIdList.add((String)itclient.next());
 			}
 			sbQuery.append("select * from hr_client_visit_details where visit_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getVisitId()));
//			System.out.println("psttt============>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				String strHrNames = rs.getString("hr_name");
				List<String> allHr = new ArrayList<String>();
				if(strHrNames != null && !strHrNames.equals("")) {
					allHr = Arrays.asList(strHrNames.split(","));
				}	

				StringBuilder sbHrNames = new StringBuilder();
				if(allHr.size() > 0){
					
					for(int i=0; hrIdList!=null && i<hrIdList.size(); i++) {
						
						if(allHr.contains(hrIdList.get(i))) {
							sbHrNames.append("<option value='"+hrIdList.get(i)+"' selected>"+hmHrCodeMap.get(hrIdList.get(i))+"</option>");
						} else {
							sbHrNames.append("<option value='"+hrIdList.get(i)+"'>"+hmHrCodeMap.get(hrIdList.get(i))+"</option>");
						}
						
					}
//					System.out.println("sbHrNames::"+sbHrNames);
					
				}else if(allHr.size() == 0){
					sbHrNames.append("<option value='"+""+"' selected>"+"All Hr's"+"</option>");
					for(int i=0; hrIdList!=null && i<hrIdList.size(); i++) {
						sbHrNames.append("<option value='"+hrIdList.get(i)+"'>"+hmHrCodeMap.get(hrIdList.get(i))+"</option>");
					}
				}
//				System.out.println("sbHrNames:::"+sbHrNames);
				request.setAttribute("sbHrNames", sbHrNames.toString());
				
				
				String clients =  rs.getString("client_name");
				List<String> allClient = new ArrayList<String>();
				if(clients != null && !clients.equals("")) {
					allClient = Arrays.asList(clients.split(","));
				}
				StringBuilder sbClient = new StringBuilder();
				if(allClient.size() > 0){
					for(int i=0; clientIdList!=null && i<clientIdList.size(); i++) {
						if(allClient.contains(clientIdList.get(i))) {
							sbClient.append("<option value='"+clientIdList.get(i)+"' selected>"+hmClientCodeMap.get(clientIdList.get(i))+"</option>");
						} else {
							sbClient.append("<option value='"+clientIdList.get(i)+"'>"+hmClientCodeMap.get(clientIdList.get(i))+"</option>");
							}
						}
					
					}else if(allClient.size() == 0){
						sbClient.append("<option value='"+""+"' selected>"+"All Department's"+"</option>");
						for(int i=0; clientIdList!=null && i<clientIdList.size(); i++) {
							sbClient.append("<option value='"+clientIdList.get(i)+"'>"+hmClientCodeMap.get(clientIdList.get(i))+"</option>");
						}
					}
					request.setAttribute("sbClient", sbClient.toString());
					request.setAttribute("operationName", "Update");
					request.setAttribute("visitId", visitId);
					
					setStrVisitdesc(rs.getString("description"));
					String visit_time = rs.getString("time");
					setstrVisitDate(uF.getDateFormat(rs.getString("date"),DBDATE,DATE_FORMAT));
				
			
				if(visit_time != null  && !visit_time.equals("")){
					setstartTime(visit_time.substring(0,visit_time.lastIndexOf(":")));//15
				}else{
					setstartTime("");
				}
				
				
			}
			rs.close();
			pst.close();
				
		}catch (Exception e) {
		e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	
	
	public void addClientVisitData(UtilityFunctions uF)
	{
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		db.setRequest(request);
		try{
			
			StringBuilder sbHrName = null;
			if(getStrHrName()!=null && getStrHrName().length>0){
				if(sbHrName == null){
					sbHrName = new StringBuilder();
					sbHrName.append(",");
				}
				for(String hrName:getStrHrName()){
					if(hrName!=null && !hrName.equals("")){
						sbHrName.append(hrName+",");
					}else{
						sbHrName = new StringBuilder();
						sbHrName.append("");
						break;
					}
				}
			}
			if(sbHrName == null) {
				sbHrName = new StringBuilder();
			}
			
			
			StringBuilder sbClientName = null;
			if(getStrClientName()!=null && getStrClientName().length>0){
				if(sbClientName == null){
					sbClientName = new StringBuilder();
					sbClientName.append(",");
				}
				for(String clientName:getStrClientName()){
					if(clientName!=null && !clientName.equals("")){
						sbClientName.append(clientName+",");
					}else{
						sbClientName = new StringBuilder();
						sbClientName.append("");
						break;
					}
				}
			}
			if(sbClientName == null) {
				sbClientName = new StringBuilder();
			}
			
			
			
			//Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			con = db.makeConnection(con);
			
			
			Map<String,String> clientlist =CF.getProjectClientMap(con, uF);
			Map<String,String> hrList = getHrDetails(con);
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("insert into hr_client_visit_details(hr_name,client_name,description,time,date) values(?,?,?,?,?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, sbHrName.toString());
			pst.setString(2, sbClientName.toString());
			pst.setString(3, getStrVisitdesc());
			//pst.setString(4, getstartTime());
			//pst.setString(5, getStrHrName());
			pst.setTime(4, uF.getTimeFormat(getstartTime(), TIME_FORMAT));
			pst.setDate(5,uF.getDateFormat(getstrVisitDate(), DATE_FORMAT));
			
//			System.out.println("insert pst=======>"+pst);
			pst.executeUpdate();
			pst.close();
			String hrNames = sbHrName.toString();
			String clientName = sbClientName.toString();
			
			//Sending notifications to HR
			String[] HRList = hrNames.split(",");
			String[] clientList = clientName.split(",");
			
			String strDomain = request.getServerName().split("\\.")[0];
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			for(String HR:HRList)
			{
				if(HR != null){
				
				for(int i=0;i<clientList.length;i++){
					if(clientList[i]!=null){
						String alertData = "<div>A New Visit for client"+"<div style=\"font-size:15px;\"><b>"+clientlist.get(clientList[i])+"</b>on Date<b>"+uF.getDateFormat(getstrVisitDate(), DATE_FORMAT)+" "+uF.getTimeFormat(getstartTime(), TIME_FORMAT)+"</b></div></div>";
						String alertAction = "Hub.action?pType=WR&type=E";
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(HR);
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
				}
			}
		}
		
		//Sending notifications to Clients Employee
		List<Integer>alClientIdList = new ArrayList<Integer>();
		List<Integer>alProIdList = new ArrayList<Integer>();
		List<Integer>alEmpList = new ArrayList<Integer>();
		
		for(int i=0;i<clientList.length;i++){
			if(clientList[i] != null){
				pst = con.prepareStatement("select * from client_details where client_name = ?");
				pst.setString(1,clientlist.get(clientList[i]));
//				System.out.println("client details pst===>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					alClientIdList.add(rs.getInt("client_id"));
					}
				rs.close();
				pst.close();
			}
		}
//		System.out.println("alClientIdList::"+alClientIdList);
		
		for(int i = 0;i<alClientIdList.size();i++)
		{
			if(alClientIdList.get(i) != null){
				pst = con.prepareStatement("select * from projectmntnc where client_id = ?");
				pst.setInt(1,alClientIdList.get(i));
				rs = pst.executeQuery();
				while(rs.next()){
						alProIdList.add(rs.getInt("pro_id"));
					}
				rs.close();
				pst.close();
			}
		}
//		System.out.println("alProIdList:::"+alProIdList);
		
		for(int i= 0;i<alProIdList.size();i++)
		{
			if(alProIdList.get(i) != null){
				pst = con.prepareStatement("select * from project_emp_details where pro_id = ?");
				pst.setInt(1,alProIdList.get(i));
				rs = pst.executeQuery();
				while(rs.next()){
						alEmpList.add(rs.getInt("emp_id"));
					}
				rs.close();
				pst.close();
			}
			
		}
		for(String HR:HRList)
		{	
			if(HR != null){
			for(int i =0;i<alEmpList.size();i++)
			{
				if(alEmpList != null){
				String alertData = "<div>A New Visit from HR "+"<div style=\"font-size:15px;\"><b>"+hrList.get(HR)+"</b>on Date<b>"+uF.getDateFormat(getstrVisitDate(), DATE_FORMAT)+" "+uF.getTimeFormat(getstartTime(), TIME_FORMAT)+"</b></div></div>";
				String alertAction = "Hub.action?pType=WR&type=E";
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId((alEmpList.get(i)).toString());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				}
			 }
			}
		}
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getstrVisitDate() {
		return strVisitDate;
	}
	public void setstrVisitDate(String strVisitDate) {
		this.strVisitDate = strVisitDate;
	}
	public String getstartTime() {
		return startTime;
	}
	public void setstartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getbtnSubmit() {
		return btnSubmit;
	}
	public void setbtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}
	public String getStrVisitdesc() {
		return strVisitdesc;
	}

	public void setStrVisitdesc(String strVisitdesc) {
		this.strVisitdesc = strVisitdesc;
	}
	
	public List<FillClients> getClientlist() {
		return clientlist;
	}
	public void setClientlist(List<FillClients> clientlist) {
		this.clientlist = clientlist;
	}
	public List<FillEmployee> getHRList() {
		return HRList;
	}
	public void setHRList(List<FillEmployee> hRList) {
		HRList = hRList;
	}
	public String[] getStrHrName() {
		return strHrName;
	}
	public void setStrHrName(String[] strHrName) {
		this.strHrName = strHrName;
	}
	public String[] getStrClientName() {
		return strClientName;
	}
	public void setStrClientName(String[] strClientName) {
		this.strClientName = strClientName;
	}
	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	
	
}
