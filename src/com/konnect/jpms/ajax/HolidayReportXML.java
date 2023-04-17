package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;




public class HolidayReportXML extends ActionSupport implements ServletRequestAware {

	HttpSession session;
	CommonFunctions CF;
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		if(CF==null)return LOGIN;
		
		String strSessionEmpId = (String)session.getAttribute(IConstants.EMPID);
		
		   
		
		createXMLDoc(strSessionEmpId);
		return SUCCESS;
	}
	
	public void createXMLDoc(String strSessionEmpId){
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rs = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			StringBuilder sbXML = new StringBuilder();
			StringBuilder sbXMLAll = new StringBuilder();
			
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmWLocationMap = CF.getWorkLocationMap(con); 
			
			
//			pst = con.prepareStatement("select* from holidays where wlocation_id in (select wlocation_id from work_location_info) order by _year, wlocation_id, _date");
			pst = con.prepareStatement(IStatements.selectHolidaysE);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			
			
			String strYearNewAll = null;
			String strYearOldAll = null;
			String strWLocationNewAll = null;
			String strWLocationOldAll = null;
			
			Map<String, String> hmYearAll = new HashMap<String, String>();
			while(rs.next()){
				strYearNewAll = rs.getString("_year");
				strWLocationNewAll = rs.getString("wlocation_id");
				Map<String, String> hmWLocation = hmWLocationMap.get(strWLocationNewAll);
				if(hmWLocation==null)hmWLocation = new HashMap<String, String>();
				
				
				boolean isWlocation = false;
				boolean isMonth = false;
				
				if(strWLocationOldAll!=null && !strWLocationNewAll.equalsIgnoreCase(strWLocationOldAll)){
					sbXMLAll.append("</country>");
					isWlocation = true;
				}
				
				
				if(strYearOldAll!=null && !strYearNewAll.equalsIgnoreCase(strYearOldAll)){
					sbXMLAll.append("</month>");
					isMonth = true;
				}
				
				if(strYearNewAll!=null && !strYearNewAll.equalsIgnoreCase(strYearOldAll)){
					if(!isWlocation){
						sbXMLAll.append("</country>");
					}
					if(!isMonth){
						sbXMLAll.append("</month>");
					}
					
					hmYearAll.put(strYearOldAll, sbXMLAll.toString());
					sbXMLAll = new StringBuilder();
					sbXMLAll.append("<month name=\"All\" id=\"0\">");
				}
				
				
				if(strWLocationNewAll!=null && !strWLocationNewAll.equalsIgnoreCase(strWLocationOldAll)){
					sbXMLAll.append("<country name=\""+uF.showData(hmWLocation.get("WL_NAME"), "")+"\">");
				}

				sbXMLAll.append("<holidayList date=\""+uF.getDateFormat(rs.getString("_date"), IConstants.DBDATE, "dd MMM yy")+"\" day=\""+uF.getDateFormat(rs.getString("_date"), IConstants.DBDATE, "EE")+"\"><![CDATA["+rs.getString("description")+"]]></holidayList>");
				
				
				
				hmYearAll.put(strYearNewAll, sbXMLAll.toString());
				
				strYearOldAll = strYearNewAll;
				strWLocationOldAll = strWLocationNewAll;
				
			}
            rs.close();
            pst.close();
			
			sbXMLAll.append("</country>");
			sbXMLAll.append("</month>");
			hmYearAll.put(strYearNewAll, sbXMLAll.toString());
			
			
			sbXML.append("<calendar>");
//			pst = con.prepareStatement("select* from holidays where wlocation_id in (select wlocation_id from work_location_info) order by _year, _date, wlocation_id");
			pst = con.prepareStatement(IStatements.selectHolidaysE);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			String strYearNew = null;
			String strYearOld = null;
			String strMonthNew = null;
			String strMonthOld = null;
			String strWLocationNew = null;
			String strWLocationOld = null;
			while(rs.next()){
				strYearNew = rs.getString("_year");
				strMonthNew = uF.getDateFormat(rs.getString("_date"), IConstants.DBDATE, "MM");
				strWLocationNew = rs.getString("wlocation_id");
				
				Map<String, String> hmWLocation = hmWLocationMap.get(strWLocationNewAll);
				if(hmWLocation==null)hmWLocation = new HashMap<String, String>();
				
				boolean isYear = false;
				boolean isMonth = false;
				boolean isWLocation = false;

				
				if(strWLocationOld!=null && !strWLocationNew.equalsIgnoreCase(strWLocationOld)){
					
					sbXML.append("</country>");
					isWLocation = true;
				}
				
				
				if(strYearNew!=null && !strYearNew.equalsIgnoreCase(strYearOld)){
					
					if(strYearOld!=null){
						if(!isWLocation){
							sbXML.append("</country>");
						}
						if(!isMonth){
							sbXML.append("</month>");
						}
						if(!isYear){
							sbXML.append("</year>");
						}
					}
					
					sbXML.append("<year whichyear=\""+strYearNew+"\" id=\"1\">");
					sbXML.append(hmYearAll.get(strYearNew));
					strMonthOld = null;
				}
				
				if(strMonthOld!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					if(!isWLocation){
						sbXML.append("</country>");
					}
					sbXML.append("</month>");
					isMonth = true;
				}
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					sbXML.append("<month name=\""+uF.getDateFormat(strMonthNew, "MM", "MMMM")+"\" id=\""+uF.parseToInt(strMonthNew)+"\">");
					strWLocationOld = null;
					strYearOld = null;
					
				}
				
				if(strYearOld!=null && !strYearNew.equalsIgnoreCase(strYearOld)){
					if(!isYear){
						sbXML.append("</country>");
					}
					if(!isMonth){
						sbXML.append("</month>");
					}
					sbXML.append("</year>");
					isYear = true;
				}
				
				if(strWLocationNew!=null && !strWLocationNew.equalsIgnoreCase(strWLocationOld)){
					sbXML.append("<country name=\""+uF.showData(hmWLocation.get("WL_NAME"), "")+"\">");
				}
				
				sbXML.append("<holidayList date=\""+uF.getDateFormat(rs.getString("_date"), IConstants.DBDATE, "dd MMM yy")+"\" day=\""+uF.getDateFormat(rs.getString("_date"), IConstants.DBDATE, "EE")+"\"><![CDATA["+rs.getString("description")+"]]></holidayList>");
				
				strWLocationOld = strWLocationNew;
				strMonthOld = strMonthNew;
				strYearOld = strYearNew;
			}
            rs.close();
            pst.close();
			  
			sbXML.append("</country>");
			sbXML.append("</month>");
			sbXML.append("</year>");
			
			sbXML.append("</calendar>");

			request.setAttribute("xml", sbXML.toString());
					
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	String xml = "<calendar>"+
			"<year whichyear=\"2010\" id=\"1\">"+
			"<month name=\"January\" id=\"1\">"+
				"<country name=\"India\">"+
					"<holidayList date=\"Jan 01st\" day=\"Friday\"><![CDATA[New Year\'s Day]]></holidayList>"+
					"<holidayList date=\"Jan 14th\" day=\"Friday\"><![CDATA[Pongal, Harvest Festival of South India]]></holidayList>"+
					"<holidayList date=\"Jan 26th\" day=\"Wednesday\"><![CDATA[India Republic Day]]></holidayList>"+
				"</country>"+
				"<country name=\"US\">"+
					"<holidayList date=\"Jan 01st\" day=\"Saturday\"><![CDATA[New Year\'s Day]]></holidayList>"+
					"<holidayList date=\"Jan 17th\" day=\"Monday\"><![CDATA[Martin Luther King, Jr. Day]]></holidayList>"+
				"</country>"+
			"</month>"+
		"</year> <!-- 2010 Year Tag [End] -->"+
		"</calendar>";;


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

}
