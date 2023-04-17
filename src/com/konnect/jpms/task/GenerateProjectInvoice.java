package com.konnect.jpms.task;

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

public class GenerateProjectInvoice extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	
	HttpSession session;
	HttpServletRequest request;
	CommonFunctions CF;
	 
	String strProjectId; 
	
	String proType;
	
	public String execute() {
		session = request.getSession();
	
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select * from activity_info ai,project_emp_details ped where ai.emp_id = ped.emp_id and ai.pro_id = ped.pro_id and ai.pro_id = ?");
			pst.setInt(1, uF.parseToInt(getStrProjectId()));
			rs = pst.executeQuery();
			
			List<List<String>> alTaskDetails = new ArrayList<List<String>>();
			
			double dblTotalBillableAmount = 0;
			double dblTotalBillableTime = 0;
			while(rs.next()){
				
				double dblRatePerHour = uF.parseToDouble(rs.getString("billable_rate"));
				double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
				double dblRatePerHour_IdealTime = dblRatePerHour * dblIdealTime;
				
				dblTotalBillableAmount +=dblRatePerHour_IdealTime;
				dblTotalBillableTime +=dblIdealTime;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(" select * from projectmntnc pmc, client_poc cpoc, client_details cd where cd.client_id = pmc.client_id and cpoc.client_id = cd.client_id and cpoc.poc_id = pmc.poc and pro_id = ?");
			pst.setInt(1, uF.parseToInt(getStrProjectId()));
			rs = pst.executeQuery();
			
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			
			while(rs.next()){
				hmProjectDetails.put("PROJECT_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PROJECT_CODE", rs.getString("project_code"));
				hmProjectDetails.put("PROJECT_DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("PROJECT_DEADLINE", uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				hmProjectDetails.put("PROJECT_IDEALTIME", rs.getString("idealtime"));
				hmProjectDetails.put("CLIENT_NAME", rs.getString("client_name"));
				hmProjectDetails.put("CLIENT_ADDRESS", rs.getString("client_address"));
				hmProjectDetails.put("SPOC_NAME", rs.getString("contact_name"));
			}
			rs.close();
			pst.close();
			
			System.out.println("pst===>"+pst);
			
			String strInvoiceNumber = uF.showData((String)hmProjectDetails.get("PROJECT_NAME"), "")+"-"+uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-yy");
			
			pst = con.prepareStatement("insert into projectmntnc_billing (invoice_amount, pro_id, invoice_date, invoice_number) values (?,?,?,?)");
			pst.setDouble(1, dblTotalBillableAmount);
			pst.setInt(2, uF.parseToInt(getStrProjectId()));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(4, strInvoiceNumber);
			pst.execute();
			pst.close();
			
			
			
			pst = con.prepareStatement("select max(pro_billing_id) as pro_billing_id from projectmntnc_billing");
			rs = pst.executeQuery();
			String strInvoiceId=null;
			while(rs.next()){
				strInvoiceId = rs.getString("pro_billing_id");
			}
			rs.close();
			pst.close();
			
			System.out.println("pst===>"+pst);
			
			request.setAttribute("PIG", "<a href=\"GenerateProjectInvoice.action?operation=pdfDwld&pro_id="+getStrProjectId()+"&invoice_id="+strInvoiceId+"\">Invoice Generated</a><br><b>Inv. No:</b> "+strInvoiceNumber);
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
  

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}


	public String getStrProjectId() {
		return strProjectId;
	}


	public void setStrProjectId(String strProjectId) {
		this.strProjectId = strProjectId;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}
	
}
