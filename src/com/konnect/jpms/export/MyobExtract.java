package com.konnect.jpms.export;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyobExtract extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	HttpServletRequest request;
	HttpServletResponse response;
	CommonFunctions CF = null;
	public String execute() throws Exception {
		session = request.getSession();
		request.setAttribute(PAGE, PGeneratePaySlip);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		String strGendate = (String)request.getParameter("GEN_DATE");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//			StringBuilder sb = new StringBuilder();
			StringBuffer sb = new StringBuffer();
			pst = con.prepareStatement(selectMyobData);
			pst.setDate(1, uF.getDateFormat(strGendate, DBDATE));
			rs = pst.executeQuery();
			while(rs.next()){
				
				sb.append(rs.getString("emp_lname")+DELIMITTER);
				sb.append(rs.getString("emp_fname")+DELIMITTER);
				if(rs.getString("pay_mode").equalsIgnoreCase("H")){
					String strDay = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat());
					if(strDay!=null && (strDay.equalsIgnoreCase(SUNDAY) || strDay.equalsIgnoreCase(SATURDAY))){
						sb.append("Overtime("+uF.formatIntoTwoDecimal(rs.getDouble("rate"))+")"+DELIMITTER);
					}else{
						sb.append("Base Hourly"+DELIMITTER);
					}
				}else{
					sb.append("Fixed Amount"+DELIMITTER);
				}
				
				sb.append(rs.getString("service_name")+DELIMITTER);
				sb.append(""+DELIMITTER);
				sb.append(""+DELIMITTER);
				sb.append("Hello Notes"+DELIMITTER);
				sb.append(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+DELIMITTER);
				sb.append(rs.getString("total_time")+DELIMITTER);
				sb.append(rs.getString("empcode")+DELIMITTER);
				if(rs.getString("pay_mode").equalsIgnoreCase("H")){
					sb.append(uF.formatIntoTwoDecimal(rs.getDouble("income_amount"))+DELIMITTER);
				}else{
					double fxdAmt = rs.getDouble("income_amount"); 
					sb.append( uF.formatIntoTwoDecimal(fxdAmt/14) +DELIMITTER);
				}
				
				sb.append(""+DELIMITTER);
				sb.append(""+DELIMITTER);
				sb.append("0"+DELIMITTER);
				sb.append(System.getProperty("line.separator"));
				
				
			}
			rs.close();
			pst.close();
			
			
			publishReport(sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
		return SUCCESS;
	}

	public void publishReport(String strData) {
		try {

			ServletOutputStream op = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setContentLength((int) strData.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + "myob.txt" + "\"");
			op.write(strData.getBytes());
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		request = arg0;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		response = arg0;
	}

}
