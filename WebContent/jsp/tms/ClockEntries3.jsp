<%@page import="java.util.*,com.konnect.jpms.util.*"%> 
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%!
UtilityFunctions uF = new UtilityFunctions();
double dblPayAmount = 0.0;
double dblTotalPayAmount = 0.0;

	String showData(String strData, String strVal) {
		if (strData == null)
			return strVal;
		else
			return strData;
	}

	String showDataAdd(String strData) {
		if (strData == null) {
			return "0";
		} else {
			dblPayAmount += Double.parseDouble(strData);
			dblTotalPayAmount += Double.parseDouble(strData);
			return strData;
		}
	}%>

<%

dblPayAmount = 0.0;
dblTotalPayAmount = 0.0;
%>

<%
	String strEmpID = (String) request.getParameter("EMPID");
	String strReqEmpID = (String) request.getParameter("EMPID");
	if (strEmpID != null) {
		strEmpID = "&EMPID=" + strEmpID;
	} else {
		strEmpID = "";
	}

	String strUserType = (String) session.getAttribute("USERTYPE");

	List alInOut = (List) request.getAttribute("alInOut");
	List alDate = (List) request.getAttribute("alDate");
	List alDay = (List) request.getAttribute("alDay");

	Map hmHours = (HashMap) request.getAttribute("hmHours");
	Map hmStart = (HashMap) request.getAttribute("hmStart");
	Map hmEnd = (HashMap) request.getAttribute("hmEnd");
	
	Map hmRosterStart = (HashMap) request.getAttribute("hmRosterStart");
	Map hmRosterEnd = (HashMap) request.getAttribute("hmRosterEnd");
	
	

	Map hmDailyRate = (HashMap) request.getAttribute("hmDailyRate");
	Map hmHoursRates = (HashMap) request.getAttribute("hmHoursRates");
	Map hmServicesWorkedFor = (HashMap) request.getAttribute("hmServicesWorkedFor");

	String TOTALW1 = (String) request.getAttribute("TOTALW1");
	String TOTALW2 = (String) request.getAttribute("TOTALW2");
	String DEDUCTION = (String) request.getAttribute("DEDUCTION");
	String PAYW1 = (String) request.getAttribute("PAYTOTALW1");
	String PAYW2 = (String) request.getAttribute("PAYTOTALW2");

	String _TOTALRosterW1 = (String) request.getAttribute("_TOTALRosterW1");
	String _TOTALRosterW2 = (String) request.getAttribute("_TOTALRosterW2");
	String _ALLOWANCE = (String) request.getAttribute("ALLOWANCE");
	

	String strPayMode = (String) request.getAttribute("strPayMode");
	String strFIXED = (String) request.getAttribute("FIXED");

	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
	Map hmEarlyLateReporting = (Map) request.getAttribute("hmEarlyLateReporting");

	List _alHolidays = (List) request.getAttribute("_alHolidays");

	Map hmEmpData = (Map) request.getAttribute("hmEmpData");
	Map hmRosterHours = (Map) request.getAttribute("hmRosterHours");

	if (hmEmpData == null) {
		hmEmpData = new HashMap();
	}

	if (hmHours == null) {
		hmHours = new HashMap();
	}

	if (hmStart == null) {
		hmStart = new HashMap();
	}

	if (hmEnd == null) {
		hmEnd = new HashMap();
	}
	if (hmDailyRate == null) {
		hmDailyRate = new HashMap();
	}
	if (hmHoursRates == null) {
		hmHoursRates = new HashMap();
	}

	if (hmHoursRates == null) {
		hmHoursRates = new HashMap();
	}

	if (hmEarlyLateReporting == null) {
		hmEarlyLateReporting = new HashMap();
	}
	if (hmEmpData == null) {
		hmEmpData = new HashMap();
	}
	if (hmRosterHours == null) {
		hmRosterHours = new HashMap();
	}
	if (_hmHolidaysColour == null) {
		_hmHolidaysColour = new HashMap();
	}
%>


<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
                
                <div class="box-header with-border">
                    <h3 class="box-title"><%=((strUserType != null && strUserType.equalsIgnoreCase("EMPLOYEE") ? "My " : (String) hmEmpData.get("NAME") + "'s "))%>Roster vs Actual Hours</h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                    <div class="aboveform">

						 <%
						 	dblPayAmount = 0.0d;
						 %>
						
						<table cellpadding="2" cellspacing="1" align="left" class="table">
						
							<tr>
						
								<td>&nbsp;</td>
						
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDay.size() : 7); i++) {
								%>
								<th class="reportHeading alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(String) alDay.get(i)%></th>
								<%
									}
								%>
						
								<td>&nbsp;</td>
							</tr>
						
							<tr>
								<td>&nbsp;</td>
						
						
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDate.size() : 7); i++) {
								%>
								<th class="reportHeading alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(String) alDate.get(i)%></th>
								<%
									}
								%>
								<td>&nbsp;</td>
							</tr>
						
						
							<tr>
								<th class="reportHeading alignRight">Start Time</th>
						
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDate.size() : 7); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmStart.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
								<td class="reportHeading alignCenter" rowspan="2">Total Hours
								Week 1</td>
							</tr>
						
							<tr>
						
								<th class="reportHeading alignRight">End Time</th>
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDate.size() : 7); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmEnd.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
						
							</tr>
						
							<tr>
								<th class="reportHeading alignRight">Daily Actual Hours</th>
						
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDate.size() : 7); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmEarlyLateReporting.get((String) alDate.get(i)), "")%><%=showData((String) hmHours.get((String) alDate.get(i)), "0")%></td>
								<%
									}
								%>
								<td class="reportLabel alignCenter"><%=showData(TOTALW1, "-")%></td>
							</tr>
						
						
						
						<tr>
								<th class="reportHeading alignRight">Roster Start Time</th>
						
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDate.size() : 7); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmRosterStart.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
								<td class="reportHeading alignCenter" rowspan="2">&nbsp;</td>
							</tr>
						
							<tr>
						
								<th class="reportHeading alignRight">Roster End Time</th>
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDate.size() : 7); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmRosterEnd.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
						
							</tr>
							
							
							<tr>
								<th class="reportHeading alignRight">Daily Roster Hours</th>
						
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDate.size() : 7); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmRosterHours.get((String) alDate.get(i)), "0")%></td>
								<%
									}
								%>
								<td class="reportLabel alignCenter"><%=showData(_TOTALRosterW1, "0")%></td>
							</tr>
						
						 
							<tr>
								<th class="reportHeading alignRight">Cost Center</th>
						
								<%
									for (int i = 0; i < ((alDate.size() < 7) ? alDate.size() : 7); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmServicesWorkedFor.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
								<td class="reportLabel alignCenter">&nbsp;</td>
							</tr>
						
							
						</table>
						
						
						<%
							dblPayAmount = 0.0d;
						%>
						
						
						
						
						
						
						
						
						<table cellpadding="2" cellspacing="1" align="left"
							style="padding-left: 0px; margin-top: 50px; display: block" class="table">
						
							<tr>
						
								<td>&nbsp;</td>
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<th class="reportHeading alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=((alDay.size() > i) ? (String) alDay.get(i) : "")%></th>
								<%
									}
								%>
						
						
								<td>&nbsp;</td>
							</tr>
						
							<tr>
								<td>&nbsp;</td>
						
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<th class="reportHeading alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=((alDate.size() > i) ? (String) alDate.get(i) : "")%></th>
								<%
									}
								%>
						
								<td>&nbsp;</td>
							</tr>
						
						
							<tr>
								<th class="reportHeading alignRight">Actual Start Time</th>
						
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmStart.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
						
								<td class="reportHeading alignCenter" rowspan="2">Total Hours
								Week 2</td>
							</tr>
						
							<tr>
								<th class="reportHeading alignRight">Actual End Time</th>
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmEnd.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
							</tr>
						
							<tr>
								<th class="reportHeading alignRight">Daily Actual Hours</th>
						
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmEarlyLateReporting.get((String) alDate.get(i)), "")%><%=showData((String) hmHours.get((String) alDate.get(i)), "0")%></td>
								<%
									}
								%>
						
								<td class="reportLabel alignCenter"><%=showData(TOTALW2, "-")%></td>
							</tr>
						
						
						<tr>
								<th class="reportHeading alignRight">Roster Start Time</th>
						
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmRosterStart.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
						
								<td class="reportHeading alignRight" rowspan="2">&nbsp;</td>
							</tr>
						
							<tr>
								<th class="reportHeading alignRight">Roster End Time</th>
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmRosterEnd.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
							</tr>
							
							<tr>
								<th class="reportHeading alignRight">Daily Roster Hours</th>
						
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmRosterHours.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
								<td class="reportLabel alignCenter"><%=showData(_TOTALRosterW2, "0")%></td>
							</tr>
						 
							<tr>
								<th class="reportHeading alignRight">Cost Center</th>
						
								<%
									for (int i = 7; i < alDate.size(); i++) {
								%>
								<td class="reportLabel alignCenter"
									<%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=showData((String) hmServicesWorkedFor.get((String) alDate.get(i)), "-")%></td>
								<%
									}
								%>
						
								<td class="reportLabel alignCenter">&nbsp;</td>
							</tr>
						
							
						
						</table>
						</div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>
