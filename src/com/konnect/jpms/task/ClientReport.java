package com.konnect.jpms.task;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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

public class ClientReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(ClientReport.class);
	
	HttpSession session; 
	CommonFunctions CF;
	String strUserType;
	String strProductType =  null;
	String strSessionEmpId;
	String strOrgId;
	
	String custId;
	String type;
	
	String proPage;
	String minLimit;
	
	String strSearch;
	
	String customerId;
	String customerName;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		 
		request.setAttribute(PAGE, PReportClient);
		request.setAttribute(TITLE, "Customer");
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-address-book\"></i><a href=\"Customer.action\" style=\"color: #3c8dbc;\"> Customer</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		if(getProPage() == null || getProPage().equals("") || getProPage().equals("null")) {
			setProPage("1");
		}
		
		if(getType() != null) {
			resetPasswordAndSendMailToCustomer(uF);
		}
		viewClient(uF);
		getSearchAutoCompleteData(uF);
		clientWiseProjectSatus(uF);
		customerWiseDocumentSize(uF);
		return loadClient();

	}
	
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from client_details where client_id > 0 and IsDisabled=false");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and added_by = "+uF.parseToInt(strSessionEmpId)+"" );
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			SortedSet<String> setSearchList = new TreeSet<String>();
			while(rs.next()) {
				setSearchList.add(rs.getString("client_name"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from client_poc where contact_fname is not null and contact_fname !='' and contact_fname !='N/A' " +
					"and contact_lname is not null and contact_lname !='' and contact_lname !='N/A' " +
					"and client_id in (select client_id from client_details where client_id > 0 and IsDisabled=false)");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				setSearchList.add(rs.getString("contact_fname")+" "+rs.getString("contact_lname") );
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
//			System.out.println("");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void resetPasswordAndSendMailToCustomer(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			SecureRandom random = new SecureRandom();
			String password = new BigInteger(130, random).toString(32).substring(5, 13);
			
			con = db.makeConnection(con);
			
			if(getType().equals("RSC")) {
				pst = con.prepareStatement("update user_details_customer set password=?, reset_timestamp=? where emp_id=?");
				pst.setString(1, password);
				pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(3, uF.parseToInt(getCustId()));
				pst.execute();
	            pst.close();
			}
            String spocFName = null;
            String spocLName = null;
            String spocMailId = null;
            String clientName = null;
            String strUsername = null;
            String strPassword = null;
            pst = con.prepareStatement("select contact_fname,contact_lname,contact_email,client_name,username,password from client_poc cp, " +
        		"client_details cd, user_details_customer udc where cd.client_id = cp.client_id and udc.emp_id = cp.poc_id and cp.poc_id = ?");
			pst.setInt(1, uF.parseToInt(getCustId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				spocFName = rs.getString("contact_fname");
				spocLName = rs.getString("contact_lname");
				spocMailId = rs.getString("contact_email");
				clientName = rs.getString("client_name");
				strUsername = rs.getString("username");
				strPassword = rs.getString("password");
			}
			rs.close();
            pst.close();
            
            if(spocMailId.trim()!=null && spocMailId.trim().indexOf("@")>0) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = null;
				if(getType().equals("RSC")) {
					nF = new Notifications(N_RESET_CUSTOMER_PASSWORD, CF);
				} else {
					nF = new Notifications(N_SEND_CUSTOMER_LOGIN_DETAILS, CF);
				}
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId(strOrgId);
				nF.setEmailTemplate(true);
				
				nF.setStrCustFName(spocFName);
				nF.setStrCustLName(spocLName);
//				nF.setStrEmpMobileNo(getStrClientContactNo().trim());
				nF.setStrEmpEmail(spocMailId);
				nF.setStrEmailTo(spocMailId);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrUserName(strUsername);
				nF.setStrPassword(strPassword);
				nF.sendNotifications();
			}
//			String strDomain = request.getServerName().split("\\.")[0];
//			Notifications nF = new Notifications(N_RESET_PASSWORD, CF);				
//			nF.setDomain(strDomain);
//			nF.setStrEmpId(getCustId()+"");
//			nF.setStrContextPath(request.getContextPath());
//			nF.setStrHostAddress(CF.getStrEmailLocalHost());
//			nF.setStrHostPort(CF.getStrHostPort());
//			nF.setStrNewPassword(password);				
//			nF.sendNotifications();
            if(getType().equals("RSC")) {
            	session.setAttribute(MESSAGE, SUCCESSM+ "Client: " + clientName + " Customer: " +spocFName +" " + spocLName +"'s password reset successfully."+END);
            } else {
            	session.setAttribute(MESSAGE, SUCCESSM+ "Client: " + clientName + " Customer: " +spocFName +" " + spocLName +"'s login details sent successfully."+END);
            }
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void customerWiseDocumentSize(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmCustomerDocSize1 = new HashMap<String, String>();
			pst = con.prepareStatement("select size_in_bytes, added_by from project_document_details where is_cust_add = true");
			rs = pst.executeQuery();
			while(rs.next()) {
				double dblFileSize = uF.parseToDouble(hmCustomerDocSize1.get(rs.getString("added_by")));
				dblFileSize =+ uF.parseToDouble(rs.getString("size_in_bytes"));
				hmCustomerDocSize1.put(rs.getString("added_by"), dblFileSize+"");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmCustomerDocSize = new HashMap<String, String>();
			Iterator<String> it = hmCustomerDocSize1.keySet().iterator();
			while(it.hasNext()) {
				String addedBy = it.next();
				String fileSize = uF.getFileTypeSize(uF.parseToDouble(hmCustomerDocSize1.get(addedBy)));
				hmCustomerDocSize.put(addedBy, fileSize);
			}
			request.setAttribute("hmCustomerDocSize", hmCustomerDocSize);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void clientWiseProjectSatus(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			setCustomerName(CF.getClientNameById(con, getCustomerId()));
			
			Map<String, Map<String, String>> hmClientwiseBillAmt = new HashMap<String, Map<String, String>>();
			Map<String, String> hmClientProFreqBillAmt = new HashMap<String, String>();
			pst = con.prepareStatement("select sum(received_amount+tds_deducted+write_off_amount) as received_amount, pro_freq_id, b.pro_id , " +
				"client_id from (select a.*,pmc.client_id from (select sum(received_amount) as received_amount,sum(tds_deducted) as tds_deducted," +
				"sum(write_off_amount) as write_off_amount, pro_freq_id, pro_id from promntc_bill_amt_details where pro_freq_id in (select " +
				"pro_freq_id from projectmntnc_frequency where pro_id in(select pro_id from projectmntnc)) group by pro_freq_id,pro_id) a, " +
				"projectmntnc pmc where a.pro_id = pmc.pro_id and pmc.client_id =?) b group by b.client_id, b.pro_id, pro_freq_id");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientProFreqBillAmt = hmClientwiseBillAmt.get(rs.getString("client_id"));
				if(hmClientProFreqBillAmt == null) hmClientProFreqBillAmt = new HashMap<String, String>();
				
				hmClientProFreqBillAmt.put(rs.getString("client_id")+"_"+rs.getString("pro_id")+"_"+rs.getString("pro_freq_id"), rs.getString("received_amount"));
				hmClientwiseBillAmt.put(rs.getString("client_id"), hmClientProFreqBillAmt);
				
			}
			rs.close();
			pst.close();
//			request.setAttribute("hmClientwiseBillAmt", hmClientwiseBillAmt);
			
			
			Map<String, String> hmClientwiseInvoiceAmt = new HashMap<String, String>();
			pst = con.prepareStatement("select sum(invoice_amount) as invoice_amount, pro_freq_id, b.pro_id , client_id from (select a.*," +
				" pmc.client_id from (select sum(invoice_amount) as invoice_amount, pro_freq_id, pro_id from promntc_invoice_details where " +
				" pro_freq_id in (select pro_freq_id from projectmntnc_frequency where pro_id in( select pro_id from projectmntnc)) group by " +
				" pro_freq_id,pro_id) a, projectmntnc pmc where a.pro_id = pmc.pro_id and pmc.client_id =?) b group by b.client_id, b.pro_id, pro_freq_id");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientwiseInvoiceAmt.put(rs.getString("client_id")+"_"+rs.getString("pro_id")+"_"+rs.getString("pro_freq_id"), rs.getString("invoice_amount"));
			}
			rs.close();
			pst.close();
//			request.setAttribute("hmClientwiseInvoiceAmt", hmClientwiseInvoiceAmt);
			
			
			Map<String, String> hmClientPaidCnt = new HashMap<String, String>();
			Iterator<String> it = hmClientwiseBillAmt.keySet().iterator();
			while(it.hasNext()) {
				int cnt=0;
				String clientId = it.next();
				Map<String, String> hmClientProFreqBillAmt1 = hmClientwiseBillAmt.get(clientId);
				Iterator<String> it1 = hmClientProFreqBillAmt1.keySet().iterator();
				while(it1.hasNext()) {
					String clientProProFeqId = it1.next();
					String clientProProFeqPaidAmt = hmClientProFreqBillAmt1.get(clientProProFeqId);
					String clientProProFeqInvoiceAmt = hmClientwiseInvoiceAmt.get(clientProProFeqId);
					if(uF.parseToDouble(clientProProFeqInvoiceAmt)<= uF.parseToDouble(clientProProFeqPaidAmt)) {
						cnt++;
					}
				}
				hmClientPaidCnt.put(clientId, cnt+"");
			}
			request.setAttribute("hmClientPaidCnt", hmClientPaidCnt);
			
			
			Map<String, String> hmClientRaisedProCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select sum(cnt) as cnt, client_id from (select * from(select count(distinct(pro_freq_id)) as cnt, pro_id " +
				" from promntc_invoice_details where pro_freq_id in(select pro_freq_id from projectmntnc_frequency pf, projectmntnc p where " +
				" pf.pro_id = p.pro_id) group by pro_id) a , projectmntnc pmc where a.pro_id = pmc.pro_id and pmc.client_id = ? ) b group by b.client_id");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientRaisedProCnt.put(rs.getString("client_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientRaisedProCnt", hmClientRaisedProCnt);
			
			Map<String, String> hmClientwiseBillProCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select sum(cnt) as cnt, client_id from (select count(pro_freq_id) as cnt, p.pro_id from " +
				" projectmntnc_frequency pf, projectmntnc p where pf.pro_id = p.pro_id group by p.pro_id) a, projectmntnc pmc " +
				" where a.pro_id = pmc.pro_id and pmc.client_id = ? group by pmc.client_id ");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientwiseBillProCnt.put(rs.getString("client_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientwiseBillProCnt", hmClientwiseBillProCnt);
			
						
			Map<String, String> hmClientWoringTeamSize = new HashMap<String, String>();
			pst = con.prepareStatement("select sum(teamCnt) as teamCnt, client_id from (select * from (select count(*) as teamCnt, pro_id from " +
				" project_emp_details where pro_id in (select pro_id from projectmntnc where approve_status = 'n') group by pro_id) a, " +
				" projectmntnc pmc where a.pro_id = pmc.pro_id and pmc.client_id = ?) b group by b.client_id ");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientWoringTeamSize.put(rs.getString("client_id"), rs.getString("teamCnt"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientWoringTeamSize", hmClientWoringTeamSize);
			
			
			Map<String, String> hmClientDeliveredProCnt = new HashMap<String, String>();
			Map<String, String> hmCustDeliveredProCnt = new HashMap<String, String>();
			
			pst = con.prepareStatement("select count(*) as proCnt,client_id from projectmntnc where approve_status = 'approved' and client_id=? group by client_id ");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientDeliveredProCnt.put(rs.getString("client_id"), rs.getString("proCnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as proCnt,poc from projectmntnc where approve_status = 'approved' and client_id=? group by poc ");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmCustDeliveredProCnt.put(rs.getString("poc"), rs.getString("proCnt"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientDeliveredProCnt", hmClientDeliveredProCnt);
			request.setAttribute("hmCustDeliveredProCnt", hmCustDeliveredProCnt);
			
			
			Map<String, String> hmClientWorkingProCnt = new HashMap<String, String>();
			Map<String, String> hmCustWorkingProCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select count(*) as proCnt,client_id from projectmntnc where approve_status = 'n' and completed>0 and client_id=? group by client_id ");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientWorkingProCnt.put(rs.getString("client_id"), rs.getString("proCnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as proCnt,poc from projectmntnc where approve_status = 'n' and completed>0 and client_id=? group by poc ");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmCustWorkingProCnt.put(rs.getString("poc"), rs.getString("proCnt"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientWorkingProCnt", hmClientWorkingProCnt);
			request.setAttribute("hmCustWorkingProCnt", hmCustWorkingProCnt);
			
			
			Map<String, String> hmClientCreatedProCnt = new HashMap<String, String>();
			Map<String, String> hmCustCreatedProCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select count(*) as proCnt,client_id from projectmntnc where approve_status = 'n' and (completed=0 or completed is null) and client_id=? group by client_id ");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientCreatedProCnt.put(rs.getString("client_id"), rs.getString("proCnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as proCnt,poc from projectmntnc where approve_status = 'n' and (completed=0 or completed is null) and client_id=? group by poc ");
			pst.setInt(1, uF.parseToInt(getCustomerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmCustCreatedProCnt.put(rs.getString("poc"), rs.getString("proCnt"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientCreatedProCnt", hmClientCreatedProCnt);
			request.setAttribute("hmCustCreatedProCnt", hmCustCreatedProCnt);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadClient(){
		
		return LOAD;
	}
	
	public String viewClient(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			Map<String, List<String>> hmClientReport = new HashMap<String, List<String>>();
			Map<String, String> hmClientIndustries = new HashMap<String, String>();
			Map<String, List<List<String>>> hmClientContactDetailsReport = new HashMap<String, List<List<String>>>();
			Map<String, List<String>> hmClientAddressDetailsReport = new HashMap<String, List<String>>();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmStates = CF.getStateMap(con);
			Map<String, String> hmCountry = CF.getCountryMap(con);
			
			pst = con.prepareStatement("select * from client_industry_details");
			rs = pst.executeQuery();
			while(rs.next()){
				hmClientIndustries.put(rs.getString("industry_id"), rs.getString("industry_name"));
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmClientsProjects = new HashMap<String, String>();
			pst = con.prepareStatement("select client_id from projectmntnc group by client_id");
			rs = pst.executeQuery();
			while(rs.next()){
				hmClientsProjects.put(rs.getString("client_id"), rs.getString("client_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientsProjects", hmClientsProjects);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(client_id) as cnt from client_details where client_id > 0 and IsDisabled=false ");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and added_by = "+uF.parseToInt(strSessionEmpId)+"" );
			}
			if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and (upper(client_name) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				sbQuery.append(" or client_id in (select client_id from client_poc where upper(contact_fname)||' '||upper(contact_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'))");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rs.next()) {
				proCnt = rs.getInt("cnt");
				proCount = rs.getInt("cnt")/10;
				if(rs.getInt("cnt")%10 != 0) {
					proCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from client_details where client_id > 0 and IsDisabled=false");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and added_by = "+uF.parseToInt(strSessionEmpId)+"" );
			}
			if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and (upper(client_name) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				sbQuery.append(" or client_id in (select client_id from client_poc where upper(contact_fname)||' '||upper(contact_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'))");
			}
			sbQuery.append(" order by client_name");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst Client Report ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(getCustomerId() == null) {
					setCustomerId(rs.getString("client_id"));
				}
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(rs.getString("client_id"),""));
				alInner.add(uF.showData(rs.getString("client_name"),""));
				alInner.add(uF.showData(rs.getString("client_address"),""));

				String strIndustry = rs.getString("client_industry");
				String []arr=null;
				if(strIndustry!=null) {
					arr = strIndustry.split(",");
				}
				StringBuilder sb = null;;
				for(int i=0; arr!=null && i<arr.length; i++) {
					if(uF.parseToInt(arr[i])>0) {
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(uF.showData(hmClientIndustries.get(arr[i]), ""));
						} else {
							sb.append(", " + uF.showData(hmClientIndustries.get(arr[i]), ""));
						}
					}
				}
				if(sb == null) {
					sb = new StringBuilder();
				}
				alInner.add(sb.toString());
				alInner.add(uF.showData(rs.getString("client_logo"), ""));
				
				hmClientReport.put(rs.getString("client_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientReport", hmClientReport);
			
			
			Map<String, String> hmDesigName = CF.getClientDesigMap(con);
			Map<String, String> hmDepartName = CF.getClientDepartMap(con);
			Map<String, String> hmLocationName = CF.getClientLocationMap(con);
			Map<String, String> hmBrandName = CF.getClientBrandMap(con);
			
			pst = con.prepareStatement("select * from client_poc cp, user_details_customer udc where cp.poc_id = udc.emp_id order by client_id,client_brand_id,poc_id");
			rs = pst.executeQuery();
			List<List<String>> clientPocList = new ArrayList<List<String>>();
			while(rs.next()) {

				clientPocList = hmClientContactDetailsReport.get(rs.getString("client_id"));
				if(clientPocList == null) clientPocList = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("poc_id")); //0
				innerList.add(uF.showData(rs.getString("contact_fname"), "N/A")+" "+uF.showData(rs.getString("contact_mname"),"")+" "+uF.showData(rs.getString("contact_lname"),""));
				innerList.add(uF.showData(rs.getString("contact_email"), "N/A")); //2
				innerList.add(uF.showData(rs.getString("contact_number"), "N/A")); //3
				innerList.add(uF.showData(hmDesigName.get(rs.getString("contact_desig_id")), "N/A")); //4
				innerList.add(uF.showData(hmDepartName.get(rs.getString("contact_department_id")), "N/A")); //5
				innerList.add(uF.showData(hmLocationName.get(rs.getString("contact_location_id")), "N/A")); //6
				innerList.add(uF.showData(rs.getString("contact_photo"), "")); //7
				innerList.add(uF.showData(rs.getString("status"), "")); //8
				innerList.add(uF.showData(rs.getString("username"), "")); //9
				innerList.add(uF.showData(rs.getString("password"), "")); //10
				innerList.add(uF.showData(hmBrandName.get(rs.getString("client_brand_id")), "")); //11
				innerList.add(uF.showData(rs.getString("client_brand_id"), "0")); //12
				clientPocList.add(innerList);
				
				hmClientContactDetailsReport.put(rs.getString("client_id"), clientPocList);
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmClientContactDetailsReport ===>> " + hmClientContactDetailsReport);
			
			request.setAttribute("hmClientContactDetailsReport", hmClientContactDetailsReport);
			
			
			pst = con.prepareStatement("select * from client_address order by client_id");
			rs = pst.executeQuery();
			
			String strClientIdNew = null;
			String strClientIdOld = null;

			List<String> alInner = new ArrayList<String>();
			StringBuilder sbAddress = new StringBuilder();
			while(rs.next()){
				strClientIdNew = rs.getString("client_id");
				sbAddress.replace(0, sbAddress.length(), "");
				if(strClientIdNew!=null && !strClientIdNew.equalsIgnoreCase(strClientIdOld)){
					alInner = new ArrayList<String>();
				}
				
				if(rs.getString("client_address")!=null){
					sbAddress.append(rs.getString("client_address")+"");
				}
				if(rs.getString("client_city")!=null){
					sbAddress.append(rs.getString("client_city")+", ");
				}
				if(uF.parseToInt(rs.getString("client_state"))>0){
					sbAddress.append(hmStates.get(rs.getString("client_state"))+", ");
				}
				if(uF.parseToInt(rs.getString("client_country"))>0){
					sbAddress.append(hmCountry.get(rs.getString("client_country")));
				}
				
				alInner.add(sbAddress.toString());
				hmClientAddressDetailsReport.put(rs.getString("client_id"), alInner);
				strClientIdOld = strClientIdNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmClientAddressDetailsReport", hmClientAddressDetailsReport);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getProPage() {
		return proPage;
	}


	public void setProPage(String proPage) {
		this.proPage = proPage;
	}


	public String getMinLimit() {
		return minLimit;
	}


	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}


	public String getStrSearch() {
		return strSearch;
	}


	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}


	public String getCustomerId() {
		return customerId;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

}
