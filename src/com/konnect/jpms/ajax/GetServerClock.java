package com.konnect.jpms.ajax;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetServerClock extends ActionSupport implements ServletRequestAware, IConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	StringBuilder strTimeStamp = new StringBuilder();
	public String execute() throws Exception {
		
		session = request.getSession(true);
		if(session==null){
			return SUCCESS;
		}
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		if(CF==null){
			CF = new CommonFunctions();
			CF.setRequest(request);
			CF.setStrTimeZone("Asia/Calcutta");
			CF.setStrReportDateFormat(DATE_FORMAT);
			CF.setStrReportTimeAM_PMFormat(TIME_FORMAT_AM_PM);
		}
		strTimeStamp.append(getDateFormat(""+getCurrentTime(CF.getStrTimeZone()), "HH:mm:ss", "HH:mm:ss"));
		return SUCCESS;
	}
	
	public java.sql.Time getCurrentTime(String strTimeZone) {
		if(strTimeZone!=null){
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			java.util.Date dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			return new java.sql.Time((dt.getTime()));
		}else{
			return new java.sql.Time((new java.util.Date().getTime()));
		}
	}
	
	public String getDateFormat(String strDate, String inputFormat, String outputFormat) {
		java.util.Date utdt = null;
		String outputDate = null;
		try { 
			if(strDate==null)
				return "-";
			
			SimpleDateFormat smft = new SimpleDateFormat(inputFormat);
			utdt = smft.parse(strDate);
			smft = new SimpleDateFormat(outputFormat);
			outputDate = smft.format(utdt);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputDate;
	}
	
	public String getStrTimeStamp() {
		if(strTimeStamp!=null){
			return strTimeStamp.toString();
		}else{
			return "";
		}
		
	}
	public void setStrTimeStamp(StringBuilder strTimeStamp) {
		this.strTimeStamp = strTimeStamp;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
}
