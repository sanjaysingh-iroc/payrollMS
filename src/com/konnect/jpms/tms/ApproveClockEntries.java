package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.select.FillInOut;
import com.konnect.jpms.select.FillLateEarly;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveClockEntries extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strEmpId = null;
	String strP = null;
	
	
	String strReqEMPID = null; 
	String strD1 = null;
	String strD2 = null;

	CommonFunctions CF = null;
	
	public String execute() throws Exception {

		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute("EMPID");
		
		
		strReqEMPID = (String)request.getParameter("EMPID");
		strD1 = (String)request.getParameter("D1");
		strD2 = (String)request.getParameter("D2");
		
		
		request.setAttribute(PAGE, PReportClockSummary);
		request.setAttribute(TITLE, TViewExceptions);

		String strAID = request.getParameter("AID");
		String strDID = request.getParameter("DID");

		String strATNID = request.getParameter("ATNID");
		strP = request.getParameter("P");
		

		leList = new FillLateEarly().fillLateEarlyAll();
		inOUTList = new FillInOut().fillInOut();

		String referer = request.getHeader("Referer");

		if (referer != null) {
			int index1 = referer.indexOf(request.getContextPath());
			int index2 = request.getContextPath().length();
			referer = referer.substring(index1 + index2 + 1);
		}
		setRedirectUrl(referer);

//		if (strATNID != null && strP != null) {
//			approveClockEntries(strAID, false);
//			return UPDATE_CLOCK_ENTRIES;
//
//		}
		
		
		if (strAID != null && strP != null) {
			approveClockEntries(strAID, false);
			return UPDATE_CLOCK_ENTRIES;

		} else if (strDID != null && strP != null) {
			approveClockEntries(strAID, true);
			return UPDATE_CLOCK_ENTRIES;

		} else if (strAID != null) {
			return approveClockEntries(strAID, false);
		} else if (strDID != null) {
			return approveClockEntries(strDID, true);
		} else {
			
			return viewClockEntries(strATNID);
		}
	}

	public String viewClockEntries(String strATNID) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			Map<String, String> hm = new HashMap<String, String>();

			List al = new ArrayList();
			List alInner = new ArrayList();

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			if (strFROM != null && strFROM.length() > 6 && strTO != null && strTO.length() > 6 && getInOUT() != null) {
				
				if (getLe().equalsIgnoreCase("EW")) {
					pst = con.prepareStatement(selectClockEntriesR2_E, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					pst.setDate(1, uF.getDateFormat(strFROM, CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat(strTO, CF.getStrReportDateFormat()));
					pst.setDate(3, uF.getDateFormat(strFROM, CF.getStrReportDateFormat()));
					pst.setDate(4, uF.getDateFormat(strTO, CF.getStrReportDateFormat()));
					pst.setInt(5, uF.parseToInt(getStrFilterEmpId()));
					
					
					System.out.println("getStrFilterEmpId()="+getStrFilterEmpId());
					
				}else if (getInOUT().equalsIgnoreCase("A")) {

					if (getLe().equalsIgnoreCase("A")) {
						pst = con.prepareStatement(selectClockEntriesR1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					} else if (getLe().equalsIgnoreCase("L")) {
						pst = con.prepareStatement(selectClockEntriesR1L, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					} else if (getLe().equalsIgnoreCase("E")) {
						pst = con.prepareStatement(selectClockEntriesR1E, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					}

					pst.setDate(1, uF.getDateFormat(strFROM, CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat(strTO, CF.getStrReportDateFormat()));
				} else {

					if (getLe().equalsIgnoreCase("A")) {
						pst = con.prepareStatement(selectClockEntriesR2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					} else if (getLe().equalsIgnoreCase("L")) {
						pst = con.prepareStatement(selectClockEntriesR2L, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					} else if (getLe().equalsIgnoreCase("E")) {
						pst = con.prepareStatement(selectClockEntriesR2E, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					}

					pst.setDate(1, uF.getDateFormat(strFROM, CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat(strTO, CF.getStrReportDateFormat()));
					pst.setString(3, getInOUT());
				}
			} else if (strATNID != null) {
				pst = con.prepareStatement(selectClockEntriesR4, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				pst.setInt(1, uF.parseToInt(strATNID));
			}else if (strD1!=null && strD2!=null && strReqEMPID!=null) {
				pst = con.prepareStatement(selectClockEntriesR5, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				pst.setInt(1, uF.parseToInt(strReqEMPID));
				pst.setDate(2, uF.getDateFormat(strD1, CF.getStrReportDateFormat()));
				pst.setDate(3, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
			}else {
				pst = con.prepareStatement(selectClockEntriesR3, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				// pst = con.prepareStatement(selectTimeSheet1);
			}
			
			rs = pst.executeQuery();
			rs.last();

			double[] inData = new double[20];
			double[] outData = new double[20];
			String[] inLabels = new String[20];
			String[] outLabels = new String[20];

			rs.beforeFirst();
			int i = 0;
			String strOldEmp = null;
			String strNewEmp = null;

			while (rs.next()) {
				alInner = new ArrayList();
				strNewEmp = rs.getString("emp_id");

				if (strUserType == null) {
					return LOGIN;
				}

				if (strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE) && strEmpId != null && !strEmpId.equalsIgnoreCase(rs.getString("emp_id"))) {
					continue;
				} else {

					if (i < 25 && "IN".equalsIgnoreCase(rs.getString("in_out"))) {
						inData[i] = rs.getDouble("early_late");
						inLabels[i] = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
					} else if (i < 25 && "OUT".equalsIgnoreCase(rs.getString("in_out"))) {
						outData[i] = rs.getDouble("early_late");
						outLabels[i] = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
					}
					i++;
				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alInner.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				alInner.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				alInner.add(rs.getString("in_out"));

				if (rs.getDouble("early_late") != 0) {
					alInner.add(uF.getTimeDiffInHoursMins(Math.abs(rs.getDouble("early_late"))) + ((rs.getDouble("early_late") > 0) ? " Late" : " Early"));
				} else {
					alInner.add(" On Time");
				}

				alInner.add((rs.getString("reason") != null) ? rs.getString("reason") : "");
//				alInner.add((rs.getString("comments") != null) ? rs.getString("comments") : "");
				alInner.add((rs.getString("hours_worked") != null) ? rs.getString("hours_worked") : "");

				if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
					alInner.add("<a href=\"?AID=" + rs.getString("atten_id") + ((strP!=null)?"&P=Y":"")+"\">Approve</a>|");
					alInner.add("<a href=\"#?w=500\" rel=\"popup_name\" class=\"poplight\" onclick=\"document.frmDeny.DID.value=" + rs.getString("atten_id") + ";document.frmDeny.Empreason.value='" + rs.getString("reason") + "';\">Deny</a>");

				} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
					alInner.add("Waiting For Approval");
					alInner.add("");
				} else if (rs.getInt("approved") == -1) {
					alInner.add("");
					alInner.add("Denied");
				} else if (rs.getInt("approved") == 1) {
					alInner.add("Approved");
					alInner.add("");
				}

				al.add(alInner);

			}
			rs.close();
			pst.close();

			request.setAttribute("alreportList", al);
  
//			request.setAttribute("IN_CHART", new BarChart().getEarlyLate(inData, inLabels, "Incoming trend"));
//			request.setAttribute("OUT_CHART", new BarChart().getEarlyLate(outData, outLabels, "Outgoing trend"));
			
			if(strD1!=null){
				setStrFROM(strD1);
				setStrTO(strD2);
				System.out.println("strReqEMPID="+strReqEMPID);
				setStrFilterEmpId(strReqEMPID);
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}

	public String approveClockEntries(String strApproveId, boolean isDeny) {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rst = null,rst2 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String strEmpId = null;
		try {

			con = db.makeConnection(con);


			pst1 = con.prepareStatement(selectClockEntriesAID);
			pst1.setInt(1, uF.parseToInt(strApproveId));
			String strTimeStamp = null;
			String strServiceId = null;
			String strInOut = null;
			Time tNewTime = null;
			rst = pst1.executeQuery();
			while (rst.next()) {
				strTimeStamp = rst.getString("in_out_timestamp");
				strInOut = rst.getString("in_out");
				strEmpId = rst.getString("emp_id");
				tNewTime = rst.getTime("new_time");
				strServiceId = rst.getString("service_id");
			}
			rst.close();
			pst1.close();

			
			pst2 = con.prepareStatement(selectRosterClockDetails12);
			pst2.setInt(1, uF.parseToInt(strEmpId));
			pst2.setDate(2, uF.getDateFormat(strTimeStamp, DBTIMESTAMP));
			pst2.setInt(3, uF.parseToInt(strServiceId));
			rst2 = pst2.executeQuery();
			Time _from = null;
			Time _to = null;
			int rosterId = 0;
			while(rst2.next()){
				_from = rst2.getTime("_from");
				_to = rst2.getTime("_to");
				rosterId = rst2.getInt("roster_id");
			}
			rst2.close();
			pst2.close();
			
			double dblTotalRosterTime = 0;
			
//			if(strInOut!=null && strInOut.equalsIgnoreCase("IN") && tNewTime!=null && _to!=null){
//				
//				dblTotalRosterTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(tNewTime.getTime(), _to.getTime()));
//				
//				pst2 = con.prepareStatement(updateRosterDetails_FROM);
//				pst2.setTime(1, tNewTime);
//				pst2.setDouble(2, dblTotalRosterTime);
//				pst2.setInt(3, rosterId);
//				pst2.execute();
//				
//			}else if(strInOut!=null && strInOut.equalsIgnoreCase("OUT") && tNewTime!=null && _from!=null){
//				
//				dblTotalRosterTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(_from.getTime(), tNewTime.getTime()));
//				
//				pst2 = con.prepareStatement(updateRosterDetails_TO);
//				pst2.setTime(1, tNewTime);
//				pst2.setDouble(2, dblTotalRosterTime);
//				pst2.setInt(3, rosterId);
//				pst2.execute();
//			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(strTimeStamp, DBTIMESTAMP, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strTimeStamp, DBTIMESTAMP, "MM")) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strTimeStamp, DBTIMESTAMP, "yyyy")));
			cal.set(Calendar.HOUR_OF_DAY, uF.parseToInt(uF.getDateFormat(strTimeStamp, DBTIMESTAMP, "HH")));
			cal.set(Calendar.MINUTE, uF.parseToInt(uF.getDateFormat(strTimeStamp, DBTIMESTAMP, "mm")));

			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int DAY = cal.get(Calendar.DAY_OF_MONTH);

			int HOUR = cal.get(Calendar.HOUR_OF_DAY);
			int MINUTE = cal.get(Calendar.MINUTE);

			
			System.out.println("HOUR="+HOUR);
			System.out.println("MINUTE="+MINUTE);
			
			
			int RoundOff = 30;
			int mode = MINUTE % RoundOff;
			
			MINUTE = RoundOff - mode;
			
			System.out.println("HOUR="+HOUR);
			System.out.println("MINUTE="+MINUTE);
			
			if(strInOut!=null && strInOut.equalsIgnoreCase("IN")){
			
				System.out.println("strInOut   IN  ="+strInOut+"   ===   "+MINUTE);
				if(mode!=0){
					cal.add(Calendar.MINUTE, MINUTE);
				}
			}else{
				System.out.println("strInOut   OUT  ="+strInOut+"   ===   "+mode);
				cal.add(Calendar.MINUTE, -mode);
			}
			
			HOUR = cal.get(Calendar.HOUR_OF_DAY);
			int MINUTE_A = cal.get(Calendar.MINUTE);
			
			
			System.out.println("HOUR="+HOUR);
			System.out.println("MINUTE="+MINUTE);
			

			pst = con.prepareStatement(updateClockEntries);
			pst.setInt(1, ((isDeny) ? -1 : 1));
			pst.setString(2, "");
			pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", "yyyy-MM-dd") + "" + uF.getTimeFormat(HOUR + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
			pst.setInt(4, uF.parseToInt(strApproveId));
			pst.execute();
			pst.close();
			
			if(strInOut!=null && strInOut.equalsIgnoreCase("IN") && _to!=null){
				
				if(tNewTime==null){
					tNewTime = uF.getTimeFormat(HOUR + ":" + MINUTE_A, "HH:mm");
				}
				
				
				dblTotalRosterTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(tNewTime.getTime(), _to.getTime()));
				
				pst2 = con.prepareStatement(updateRosterDetails_FROM);   
				pst2.setTime(1, tNewTime);
				pst2.setDouble(2, dblTotalRosterTime);
				pst2.setInt(3, rosterId);
				pst2.execute();
				pst2.close();
				System.out.println("IN === tNewTime====>"+tNewTime);
				
			}else if(strInOut!=null && strInOut.equalsIgnoreCase("OUT") && _from!=null){
				
				if(tNewTime==null){
					tNewTime = uF.getTimeFormat(HOUR + ":" + MINUTE_A, "HH:mm");
				}
				
				dblTotalRosterTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(_from.getTime(), tNewTime.getTime()));
				
				pst2 = con.prepareStatement(updateRosterDetails_TO);
				pst2.setTime(1, tNewTime);
				pst2.setDouble(2, dblTotalRosterTime);
				pst2.setInt(3, rosterId);
				pst2.execute();
				pst2.close();
			}
			
			
			
			
			
			
			
//			UpdateClockEntries updateClockEntries = new UpdateClockEntries();
//			updateClockEntries.setStrEmpId(strEmpId);
//			if(strInOut!=null && strInOut.equalsIgnoreCase("OUT")){
//				updateClockEntries.setStrEmpOUT(uF.getDateFormat(HOUR + ":" + MINUTE_A, DBTIME, ReportTimeFormat));
//			}else{
//				updateClockEntries.setStrEmpIN(uF.getDateFormat(HOUR + ":" + MINUTE_A, DBTIME, ReportTimeFormat));
//			}
//			
//			updateClockEntries.setStrDate(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", ReportDateFormat));
//			updateClockEntries.updateClockEntries();
			

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rst2);
			db.closeResultSet(rst);
			db.closeStatements(pst2);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String denyClockEntries(String strDenyId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(updateClockEntries);
			pst.setInt(1, -1);
			pst.setInt(2, uF.parseToInt(strDenyId));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String redirectUrl;

	String inOUT;
	String le;
	String strFROM;
	String strTO;
	String strFilterEmpId;

	List<FillInOut> inOUTList;
	List<FillLateEarly> leList;

	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	public String getInOUT() {
		return inOUT;
	}

	public void setInOUT(String inOUT) {
		this.inOUT = inOUT;
	}

	public String getLe() {
		return le;
	}

	public void setLe(String le) {
		this.le = le;
	}

	public String getStrFROM() {
		return strFROM;
	}

	public void setStrFROM(String strFROM) {
		this.strFROM = strFROM;
	}

	public String getStrTO() {
		return strTO;
	}

	public void setStrTO(String strTO) {
		this.strTO = strTO;
	}

	public List<FillInOut> getInOUTList() {
		return inOUTList;
	}

	public List<FillLateEarly> getLeList() {
		return leList;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getStrFilterEmpId() {
		return strFilterEmpId;
	}

	public void setStrFilterEmpId(String strFilterEmpId) {
		this.strFilterEmpId = strFilterEmpId;
	}

}
