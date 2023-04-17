
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="java.util.*"%>

<%
	List alDate = (List) request.getAttribute("alDate");
	List alDay = (List) request.getAttribute("alDay");
	List alEmpCode = (List) request.getAttribute("alEmpCode");
	List alEmpCodeLink = (List) request.getAttribute("alEmpCodeLink");
	Map hmEmpData = (Map) request.getAttribute("hmManagerAttendenceReport");
	
	System.out.println("Inside the clockEntriesManager Dates are as follows::::::::>>>>>>>>"+alDate);
	
	
	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");	
	List _alHolidays = (List) request.getAttribute("_alHolidays");
	Map hmEarlyLateReporting = (Map) request.getAttribute("hmEarlyLateReporting");
	Map hmRosterHoursDetails = (Map) request.getAttribute("hmRosterHoursDetails");
	Map hmRosterServices = (Map) request.getAttribute("hmRosterServices");
	
		
	List<String> alHolidays = new ArrayList<String>();
	
	
	

	if (alDay == null) {
		alDay = new ArrayList();
	}
	if (alDate == null) {
		alDate = new ArrayList();
	}
	if (alEmpCode == null) {
		alEmpCode = new ArrayList();
	}
	if (_alHolidays == null) {
		_alHolidays = new ArrayList();
	}
	
	if (hmEmpData == null) {
		hmEmpData = new HashMap();
	}
%>


<h4 style="margin-left:50px">My Staff's Attendance</h4>

<div class="aboveform scroll">
<table cellpadding="2" cellspacing="1">

	<tr>
		<td class="">&nbsp;</td>
		<%
			for (int i = 0; i < alDay.size(); i++) {
		%>

		<td colspan="2" class="reportHeading alignCenter" <%= ((_alHolidays.contains(i+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(i+"")+"\'":"")) %>><%=(String) alDay.get(i)%></td>

		<%
			}
		%>
	</tr>

	<tr>
		<td class="">&nbsp;</td>
		<%
			for (int i = 0; i < alDate.size(); i++) {
		%>

		<td colspan="2" class="reportHeading alignCenter" <%= ((_alHolidays.contains(i+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(i+"")+"\'":"")) %>><%=(String) alDate.get(i)%></td>

		<%
			}
		%>
	</tr>
	
	<tr>
		<td class="">&nbsp;</td>
		<%
			for (int i = 0; i < alDate.size(); i++) {
		%>

		<td class="reportLabel alignCenter" <%= ((_alHolidays.contains(i+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(i+"")+"\'":"")) %>>Actual</td>
		<td class="reportLabel alignCenter" <%= ((_alHolidays.contains(i+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(i+"")+"\'":"")) %>>Roster</td>

		<%
			}
		%>
	</tr>




	<%
		for (int i = 0; i < alEmpCode.size(); i++) {
			Map hm = (HashMap) hmEmpData.get((String) alEmpCode.get(i));
			Map hmEarlyLateMark = (HashMap)hmEarlyLateReporting.get((String) alEmpCode.get(i));
			Map hmRH = (HashMap) hmRosterHoursDetails.get((String) alEmpCode.get(i));
			Map hmS = (HashMap) hmRosterServices.get((String) alEmpCode.get(i));
			
			if (hm == null) {
				hm = new HashMap();
			}
			if (hmRH == null) {
				hmRH = new HashMap();
			}
			if (hmS == null) {
				hmS = new HashMap();
			}
	%>
	<tr>
		
		<td class="reportLabel alignCenter" ><%=(String) alEmpCodeLink.get(i)%></td>
		<%
			
		for (int k = 0; k < alDate.size(); k++) {
		%>

		<td width="30px" class="reportLabel pointer alignCenter" <%= ((_alHolidays.contains(k+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(k+"")+"\'":"")) %> title="<%= (((String) hmS.get((String) alDate.get(k))!=null)?(String) hmS.get((String) alDate.get(k)):"") %>"><%=(((String) hm.get((String) alDate.get(k)) != null) ? (String) hm.get((String) alDate.get(k)) : "0")%></td>
		<td width="30px" class="reportLabel pointer alignCenter" <%= ((_alHolidays.contains(k+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(k+"")+"\'":"")) %> title="<%= (((String) hmS.get((String) alDate.get(k))!=null)?(String) hmS.get((String) alDate.get(k)):"") %>"><%=(((String) hmRH.get((String) alDate.get(k)) != null) ? (String) hmRH.get((String) alDate.get(k)) : "0")%></td>

		<%
			}
		%>
	</tr>
	<%
		}
	%>


</table>
</div>