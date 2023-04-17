package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewRequest extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(ViewRequest.class);
	
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(USERTYPE);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		
		String strRequestType = (String)request.getParameter("RT");
		String strStatus = (String)request.getParameter("S");
		String strRequisitionId = (String)request.getParameter("RID");

		viewRequest(strRequestType, strStatus, strRequisitionId);
		
		return SUCCESS;
		
	}
	
	
	public int viewRequest(String strRequestType, String strStatus, String strRequisitionId){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try{
			
			con = db.makeConnection(con);
			
			if(strRequestType!=null && strRequestType.equalsIgnoreCase("BF")){
				pst = con.prepareStatement("select * from requisition_bonafide where requisition_id = ?");
				pst.setInt(1, uF.parseToInt(strRequisitionId));
				rs = pst.executeQuery();
				if(rs.next()){
					request.setAttribute("PURPOSE", rs.getString("purpose"));
				}
				rs.close();
				pst.close();
				
				
				
			}else if(strRequestType!=null && strRequestType.equalsIgnoreCase("IR")){
				pst = con.prepareStatement("select * from requisition_infrastructure where requisition_id = ?");
				pst.setInt(1, uF.parseToInt(strRequisitionId));
				rs = pst.executeQuery();
				if(rs.next()){
					request.setAttribute("FROM_DATE", rs.getString("from_date"));
					request.setAttribute("TO_DATE", rs.getString("to_date"));
					request.setAttribute("FROM_TIME", rs.getString("from_time"));
					request.setAttribute("TO_TIME", rs.getString("to_time"));
					request.setAttribute("NAME", rs.getString("infrastructure_name"));
					request.setAttribute("TYPE", rs.getString("infrastructure_type"));
					request.setAttribute("PURPOSE", rs.getString("purpose"));
					request.setAttribute("MODE", rs.getString("_mode"));
				}
				rs.close();
				pst.close();
				
			}else if(strRequestType!=null && strRequestType.equalsIgnoreCase("OR")){
				pst = con.prepareStatement("select * from requisition_other where requisition_id = ?");
				pst.setInt(1, uF.parseToInt(strRequisitionId));
				rs = pst.executeQuery();
				if(rs.next()){
					request.setAttribute("FROM_DATE", rs.getString("from_date"));
					request.setAttribute("TO_DATE", rs.getString("to_date"));
					request.setAttribute("FROM_TIME", rs.getString("from_time"));
					request.setAttribute("TO_TIME", rs.getString("to_time"));
					request.setAttribute("NAME", rs.getString("other_name"));
					request.setAttribute("TYPE", rs.getString("other_type"));
					request.setAttribute("PURPOSE", rs.getString("purpose"));
					request.setAttribute("MODE", rs.getString("_mode "));
				}
				rs.close();
				pst.close();
			}
			
			
			
			request.setAttribute("RT", strRequestType);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return nRequisitionId;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public void getStatusMessage(int nStatus){
		
		switch(nStatus){
		
		case -1:
			 /*request.setAttribute("STATUS_MSG", "<img src=\""+request.getContextPath()+"/images1/icons/denied.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
			break;
			
		case 0:
			/*request.setAttribute("STATUS_MSG", "<img src=\""+request.getContextPath()+"/images1/icons/pending.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>");
			break;
			
		case 1:
			/*request.setAttribute("STATUS_MSG", "<img src=\""+request.getContextPath()+"/images1/icons/approved.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
			break;
			
		case 2:
			/*request.setAttribute("STATUS_MSG", "<img src=\""+request.getContextPath()+"/images1/icons/pullout.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i>");
			break;
			
		case 3:
			/*request.setAttribute("STATUS_MSG", "<img src=\""+request.getContextPath()+"/images1/icons/re_submit.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
			
			break;
		}
	}
	
	
}
