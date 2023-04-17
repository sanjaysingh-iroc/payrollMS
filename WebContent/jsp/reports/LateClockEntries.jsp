<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%

	UtilityFunctions uF = new UtilityFunctions();

	List _allDates = (List) request.getAttribute("_allDates");
	List _allDays = (List) request.getAttribute("_allDays");
	List _alHolidays = (List) request.getAttribute("_alHolidays");
	 
	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
	Map hmInLateCount = (Map) request.getAttribute("hmInLateCount");	
	Map hmOutLateCount = (Map) request.getAttribute("hmOutLateCount");
	
	String strFrom = (String)request.getAttribute("FROM");
	String strTo = (String)request.getAttribute("TO");
	
	if(_allDates==null){
		_allDates = new ArrayList();
	}
	if(_alHolidays==null){
		_alHolidays = new ArrayList();
	}
	
	if(_hmHolidaysColour==null){
		_hmHolidaysColour = new HashMap();
	}
	if(hmInLateCount==null){
		hmInLateCount = new HashMap();
	}
	if(hmOutLateCount==null){
		hmOutLateCount = new HashMap();
	}
	
%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Exceptions (Late)" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">

<span style="color: #346897; font-size: 14px; font-weight: 300;">
       By Day (per day)- by and between [<%=strFrom %> to <%=strTo %>]
</span>

<s:form cssStyle="margin-left:710px; margin-bottom:10px" theme="simple" method="post" name="frm_roster_actual">
<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId"
		listValue="paycycleName" headerKey="0" headerValue="Select Paycycle"
		onchange="document.frm_roster_actual.submit();"
		list="paycycleList" key="" />
</s:form>

<% if (hmInLateCount.size()!=0 || hmOutLateCount.size()!=0 ) {%>

<table cellpadding="2" cellspacing="1">

	<tr>
		<td width="85px" class="reportHeading alignCenter" nowrap>Date</td>
		<td width="85px" class="reportHeading alignCenter" nowrap>Day</td>
		<td width="50px" class="reportHeading alignCenter" nowrap>In (hrs)</td>
		<td width="50px" class="reportHeading alignCenter" nowrap>Out (hrs)</td>
	</tr>
		
		<%
			for (int i = 0; i<_allDates.size(); i++) {
		%>
		
	<tr>
		
		<td class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDates.get(i) == null) ? "" : (String) _allDates.get(i))%>
		</td>
		
		<td class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDays.get(i) == null) ? "" : (String) _allDays.get(i))%>
		</td>
		
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmInLateCount.get((String)_allDates.get(i)),"0") %></td>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmOutLateCount.get((String)_allDates.get(i)),"0") %></td>
	</tr>
	
		<%
			}
		%>

	
</table>

<%}else{ %>
	
	<span style="color: #346897; font-size: 14px; font-weight: 300;">
		Data is Not Available.
	</span>

<%} %>
</div>