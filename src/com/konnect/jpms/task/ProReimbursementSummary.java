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

public class ProReimbursementSummary extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6916474692176705308L;
	String pro_id;
	String emp_id;
	private CommonFunctions CF;
	HttpSession session;

	String strUserType;
	
	public String execute(){ 
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN; 
		
		
		String strD1 = request.getParameter("strD1");
		String strD2 = request.getParameter("strD2");
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		getReimbursement(strD1, strD2);
		
		return SUCCESS;
	}

	
	
	public void getReimbursement(String strD1, String strD2){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmProjectMap = CF.getProjectNameMap(con);
			Map<String, String> hmTravelPlanMap = CF.getTravelPlanMap(con);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			List<List<String>> alReport = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			if(uF.parseToInt(getEmp_id())>0){
//				pst = con.prepareStatement("select * from emp_reimbursement where emp_id =? and reimbursement_type=? and reimbursement_type1 = 'P'  and (from_date, to_date) overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD') +1)");
				pst = con.prepareStatement("select * from emp_reimbursement where emp_id =? and reimbursement_type=? and reimbursement_type1 = 'P' " +
				"and from_date >=? and from_date <=? and approval_1 =1 and approval_2=1 ");
				pst.setInt(1, uF.parseToInt(getEmp_id()));
				pst.setString(2, getPro_id());
				pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			}else{
//				pst = con.prepareStatement("select * from emp_reimbursement where reimbursement_type=? and reimbursement_type1 = 'P'  and (from_date, to_date) overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD') +1)");
				pst = con.prepareStatement("select * from emp_reimbursement where reimbursement_type=? and reimbursement_type1 = 'P' " +
				"and from_date >=? and from_date <=? and approval_1 =1 and approval_2=1 ");
				pst.setString(1, getPro_id());
				pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			}
			rs = pst.executeQuery();
			int nCount = 0;
			
			while(rs.next()) {
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				String strReimbursementType = null;
				if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("P")) {
					strReimbursementType = "project " + uF.showData(hmProjectMap.get(rs.getString("reimbursement_type")), "");
				} else if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("T")) {
					strReimbursementType = "travel plan " + uF.showData(hmTravelPlanMap.get(rs.getString("reimbursement_type")), "");
				} else {
					strReimbursementType = uF.showData(rs.getString("reimbursement_type"), "");
				}

				StringBuilder sb = new StringBuilder();
				alInner = new ArrayList<String>();
				
				
				sb.append("<div style=\"float:left;width:130px;margin-top:1px;padding-right:8px\" id=\"myDiv" + nCount + "\" >");

				if (rs.getInt("approval_1") == 0) {
					 /*sb.append("<img src=\"" + request.getContextPath() + "/images1/icons/pending.png\" title=\"Waiting for approval\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i>");
					
				} else if (rs.getInt("approval_1") == 1) {
					 /*sb.append("<img src=\"" + request.getContextPath() + "/images1/icons/approved.png\" title=\"Approved\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if (rs.getInt("approval_1") == -1) {
					/*sb.append("<img src=\"" + request.getContextPath() + "/images1/icons/denied.png\" title=\"Denied\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
				}
				
				if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE) && !strUserType.equalsIgnoreCase(ARTICLE) && !strUserType.equalsIgnoreCase(CONSULTANT)){
					if (rs.getInt("approval_1") == 0) {
						sb.append(" <a href=\"javascript:void(0);\" onclick=\"getContent('myDiv" + nCount + "', 'UpdateRequest.action?S=1&RID=" + rs.getString("reimbursement_id")
								+ "&T=RIM&M=MA')\">Approve</a> |");
						sb.append(" <a href=\"javascript:void(0);\" onclick=\"getContent('myDiv" + nCount + "', 'UpdateRequest.action?S=-1&RID=" + rs.getString("reimbursement_id")
								+ "&T=RIM&M=MA')\">Deny</a> ");

					}
				}
				
				sb.append("</div>");

				sb.append("<div style=\"float:left;width:70%;\">");
				sb.append(hmEmpNames.get(rs.getString("emp_id")));
				sb.append(" has submitted a request for reimbursement for " + strReimbursementType + " on "
						+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()) + " for <strong>" + strCurrency
						+ uF.formatIntoComma(uF.parseToDouble(rs.getString("reimbursement_amount"))) + "</strong>" + " specifying " + "\""
						+ uF.showData(rs.getString("reimbursement_purpose"), "-") + "\"");

				boolean isApproval1 = false;
				if (rs.getInt("approval_1") == -1) {
					sb.append(" has been denied by " + hmEmpNames.get(rs.getString("approval_1_emp_id")));
					isApproval1 = true;
				} else if (rs.getInt("approval_1") == 0) {
					sb.append(" is waiting for approval");
					isApproval1 = true;
				} else if (rs.getInt("approval_1") == 1) {
					sb.append(" is approved by " + hmEmpNames.get(rs.getString("approval_1_emp_id")) + " on "
							+ uF.getDateFormat(rs.getString("approval_1_date"), DBDATE, CF.getStrReportDateFormat()));
					isApproval1 = true;
				}

				if (rs.getInt("approval_2") == -1) {
					if (isApproval1) {
						sb.append(" and ");
					}
					sb.append(" has been denied by " + hmEmpNames.get(rs.getString("approval_2_emp_id")));
					isApproval1 = true;
				} else if (rs.getInt("approval_2") == 0) {
					if (isApproval1) {
						sb.append(" and ");
					}
					sb.append(" is waiting for HR approval");
					isApproval1 = true;
				} else if (rs.getInt("approval_2") == 1) {
					if (isApproval1) {
						sb.append(" and ");
					}
					sb.append(" is approved by " + hmEmpNames.get(rs.getString("approval_2_emp_id")) + " on "
							+ uF.getDateFormat(rs.getString("approval_2_date"), DBDATE, CF.getStrReportDateFormat()));
					isApproval1 = true;
				}

				// sb.append(" <a href=\"Reimbursements.action?E="+rs.getString("reimbursement_id")+"\">Edit</a> ");

				
				
				if(rs.getString("reimbursement_info")!=null && rs.getString("reimbursement_info").equalsIgnoreCase("Travel")){
					sb.append(" <strong>Travel Details:</strong>");
					sb.append(" From "+rs.getString("travel_from")+" to "+rs.getString("travel_to")+", ");
					sb.append(" Travel Mode - "+rs.getString("travel_mode")+", ");
					if(rs.getString("travel_distance")!=null){
						sb.append(" Travel Distance - "+rs.getString("travel_distance")+"km"+", ");
					}
					sb.append(" Travel Rate - "+strCurrency+rs.getString("travel_rate"));
				}
				
				
				sb.append("</div>");

				String[] strDocs = null;
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					strDocs = rs.getString("ref_document").split(":_:");
				}
				StringBuilder sbDoc = new StringBuilder();
				for (int k = 0; strDocs != null && k < strDocs.length; k++) {
					// sbDoc.append("<a href=\""+request.getContextPath()+DOCUMENT_LOCATION+strDocs[k]+"\">Document "+(k+1)+"</a><br/>");
//					sbDoc.append("<a target=\"blank\" href=\"userDocuments/" + strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					if(CF.getStrDocRetriveLocation()==null){
//						sbDoc.append("<a target=\"blank\" href=\"userDocuments/" + strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
						sbDoc.append("<a target=\"blank\" href=\"" + request.getContextPath()+DOCUMENT_LOCATION + strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}else{
//						sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
						sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}

				}
				sb.append("<div style=\"float:left;width:60px;margin: 1px 5px 0 5px;cursor:pointer;padding-left:2px;\">" + sbDoc.toString() + "</div>");

				alInner.add(sb.toString());

				alReport.add(alInner);
				nCount++;
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("alReport=="+alReport);
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getPro_id() {
		return pro_id;
	}
	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}



	public String getEmp_id() {
		return emp_id;
	}



	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

}
