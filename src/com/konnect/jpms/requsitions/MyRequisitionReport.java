package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyRequisitionReport extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(MyRequisitionReport.class);
	
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		

		request.setAttribute(PAGE, PRequisitionsReport);
		request.setAttribute(TITLE, "");
		
		viewReport();
		return SUCCESS;
	}
	
	public void viewReport(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		List<List<String>> reportList = new ArrayList<List<String>>();
		try{
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con, null, null);
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))){
				
				pst = con.prepareStatement("select * from requisition_details where emp_id = ? order by requisition_date desc");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				
				pst = con.prepareStatement("select * from requisition_details where emp_id = ? order by requisition_date desc");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				
			}else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))){
				pst = con.prepareStatement("select * from requisition_details rd, employee_official_details eod where rd.emp_id = eod.emp_id and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) order by requisition_date desc, eod.emp_id");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			
			}else if(strUserType!=null && (strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(ADMIN))){
				pst = con.prepareStatement("select * from requisition_details order by requisition_date desc, emp_id");
			}
			rs = pst.executeQuery();
			
			List<String> alInner = new ArrayList<String>();
			int nCount = 0;
			while(rs.next()){
				
				alInner = new ArrayList<String>();
				
				alInner.add(hmEmpMap.get(rs.getString("emp_id")));
				alInner.add(getRequisitionType(rs.getString("requisition_type")));
				alInner.add(uF.getDateFormat(rs.getString("requisition_date"), DBDATE, CF.getStrReportDateFormat()));
			
				alInner.add("<a href=\"ViewRequest.action?RT="+rs.getString("requisition_type")+"&RID="+rs.getString("requisition_id")+"\" onclick=\"return hs.htmlExpand(this, {objectType: 'ajax'} )\" >View Request</a>");
				
				alInner.add(getStatus(rs.getInt("status"), nCount, rs.getString("requisition_id")));
				
				reportList.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", reportList);
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public String getStatus(int nStatus, int nCount, String strReqId){
		String strStatus = null;
		switch(nStatus){
		case -1:
			 /*strStatus = "<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=-1&RID="+strReqId+"')\">"+"<img src=\""+request.getContextPath()+"/images1/icons/denied.png\" border=\"0\">" +"</a>";*/
			strStatus = "<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=-1&RID="+strReqId+"')\">"+"<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>" +"</a>";
			break;
		case 0:
			/*strStatus = "<a href=\"javascript:void(0);\" >"+"<img src=\""+request.getContextPath()+"/images1/icons/pending.png\" border=\"0\">" +"</a>"*/
			strStatus = "<a href=\"javascript:void(0);\" >"+"<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>" +"</a>"
			+
						"<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=1&RID="+strReqId+"')\">"+"<img src=\""+request.getContextPath()+"/images1/icons/act_now.png\" border=\"0\">" +"</a>"+
						/*"<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=2&RID="+strReqId+"')\">"+"<img src=\""+request.getContextPath()+"/images1/icons/pullout.png\" border=\"0\">" +"</a>"+*/
						"<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=2&RID="+strReqId+"')\">"+"<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i>" +"</a>"+
						
						/*"<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=3&RID="+strReqId+"')\">"+"<img src=\""+request.getContextPath()+"/images1/icons/re_submit.png\" border=\"0\">"+"</a>";*/
						"<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=3&RID="+strReqId+"')\">"+"<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>"+"</a>";
			break;
		case 1:
			 /*strStatus = "<img src=\""+request.getContextPath()+"/images1/icons/approved.png\" border=\"0\">";*/
			strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>";
			break;
		case 2:
			/*strStatus = "<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=2&RID="+strReqId+"')\">"+"<img src=\""+request.getContextPath()+"/images1/icons/pullout.png\" border=\"0\">" +"</a>";*/
			strStatus = "<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=2&RID="+strReqId+"')\">"+"<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i>" +"</a>";
			break;
		case 3:
			/*strStatus = "<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=3&RID="+strReqId+"')\">"+"<img src=\""+request.getContextPath()+"/images1/icons/re_submit.png\" border=\"0\">"+"</a>";*/
			strStatus = "<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateRequest.action?S=3&RID="+strReqId+"')\">"+"<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>"+"</a>";
			
			break;
		}
		return strStatus;
	}
	
	public String getRequisitionType(String strShortType){
		String strFullType = null;
		
		if(strShortType!=null && strShortType.equalsIgnoreCase("BF")){
			strFullType = "Bonafide";
		}else if(strShortType!=null && strShortType.equalsIgnoreCase("IR")){
			strFullType = "Infrastructure";
		}else if(strShortType!=null && strShortType.equalsIgnoreCase("OR")){
			strFullType = "Other";
		}
		
		return strFullType;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
}
