<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	

	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 

	List _allDates = (List) request.getAttribute("_allDates");
	List _alHolidays = (List) request.getAttribute("_alHolidays");
	 
	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
	Map hmRosterHours = (Map) request.getAttribute("hmRosterHours");	
	Map hmRosterHoursE = (Map) request.getAttribute("hmRosterHoursE");
	Map hmActualHours = (Map) request.getAttribute("hmActualHours");
	Map hmActualHoursE = (Map) request.getAttribute("hmActualHoursE");
	Map hmEmpData = (Map) request.getAttribute("hmEmpData");
	String strFrom = (String)request.getAttribute("FROM");
	String strTo = (String)request.getAttribute("TO");
	
	System.out.println("hmRosterHours::::>"+hmRosterHours.size()+" hmActualHours:::>"+hmActualHours.size());
	
	if(_allDates==null){
		_allDates = new ArrayList();
	}
	if(_alHolidays==null){
		_alHolidays = new ArrayList();
	}
	
	if(_hmHolidaysColour==null){
		_hmHolidaysColour = new HashMap();
	}
	if(hmRosterHours==null){
		hmRosterHours = new HashMap();
	}
	if(hmActualHours==null){
		hmActualHours = new HashMap();
	}

	

%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Rostered hours vs actual hours" name="title"/>
</jsp:include>



<div class="clr"></div> 
<div>
<s:form cssStyle="margin-left:0px; margin-bottom:10px" theme="simple" method="get" name="frm_roster_actual">
	<%-- <span style="color: #346897; font-size: 14px; font-weight: 300;">
		Please select employee from the drop down list.
	</span> --%>
	<s:select label="Select Employee" name="employee" listKey="employeeId"
			listValue="employeeName" headerKey="0" headerValue="Select Employee"
			onchange="document.frm_roster_actual.submit();"
			list="empList" key="" />
	<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId"
			listValue="paycycleName" headerKey="0" 
			onchange="document.frm_roster_actual.submit();"
			list="paycycleList" key="" />
</s:form>
</div>

<div id="printDiv" class="leftbox reportWidth">
<span style="color: #346897; font-size: 14px; font-weight: 300;">
		between [<%= uF.getDateFormat(strFrom, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) %> to <%= uF.getDateFormat(strTo, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) %>]
		</span>

<div id="EmpData">
<%
	if(hmRosterHoursE.size()!=0 && hmActualHours.size()!=0)
	{
%>

<span style="color: #346897; font-size: 14px; font-weight: 300; margin: 20px 0px 20px  0px; float: left;">

<% if(hmEmpData.get("NAME")!= null) {%>

<%= hmEmpData.get("NAME")%>'s Hours
		
<%}%>
</span>

<div class="clr"></div>

<table cellpadding="2" cellspacing="1">

	<tr>
		<td>&nbsp;</td>
		<%
			for (int i = 0; i<((_allDates.size()>=15)?_allDates.size()/2:_allDates.size()); i++) {
		%>
		<td width=50px class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDates.get(i) == null) ? "" : uF.getDateFormat((String) _allDates.get(i), CF.getStrReportDateFormat(), "dd MMM")) %>
		</td>
		<%
			}
		%>
	</tr>

	
	<tr>
		<td class="reportHeading alignCenter">Rostered</td>

		<%
			for (int i = 0; i<((_allDates.size()>=15)?_allDates.size()/2:_allDates.size()); i++) {
		%>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmRosterHoursE.get((String)_allDates.get(i)),"0") %></td>
		<%
			}
		%>



	</tr>


	<tr>
		<td class="reportHeading alignCenter">Actual</td>

		<%
			for (int i = 0; i<((_allDates.size()>=15)?_allDates.size()/2:_allDates.size()); i++) {
		%>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmActualHoursE.get((String)_allDates.get(i)),"0") %></td>
		<%
			}
		%>

	</tr>

</table>


<br><br>

<table cellpadding="2" cellspacing="1">

	<tr>
		<td>&nbsp;</td>
		<%
			for (int i = (int)Math.floor(_allDates.size()/2); i < _allDates.size(); i++) {
		%>
		<td width=50px class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDates.get(i) == null) ? "" : uF.getDateFormat((String) _allDates.get(i), CF.getStrReportDateFormat(), "dd MMM"))%>
		</td>
		<%
			}
		%>
	</tr>

	
	<tr>
		<td class="reportHeading alignCenter">Rostered</td>

		<%
			for (int i = (int)Math.floor(_allDates.size()/2); i < _allDates.size(); i++) {
		%>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmRosterHoursE.get((String)_allDates.get(i)), "0") %></td>
		<%
			}
		%>



	</tr>


	<tr>
		<td class="reportHeading alignCenter">Actual</td>

		<%
			for (int i = (int)Math.floor(_allDates.size()/2); i < _allDates.size(); i++) {
		%>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmActualHoursE.get((String)_allDates.get(i)), "0") %></td>
		<%
			}
		%>

	</tr>

</table>

<%
}
else{
	
	if(hmEmpData.get("NAME")!=null) { 
%>
	<span style="color: #346897; font-size: 14px; font-weight: 300;">
	Data is not available for <%= hmEmpData.get("NAME")%>
	</span>
<%} 
}%>
</div>

<div class="clr"></div>
<div class="clr"></div>

<div class="clr"></div>

<div id="totalData">
<%
	if(hmRosterHours.size()!=0 && hmActualHours.size()!=0)
	{
%>

<span style="color: #346897; font-size: 14px; font-weight: 300; margin: 20px 0px 20px  0px; float: left;">

Total Hours for all Employees <%-- between [<%=strFrom %> to <%=strTo %>] --%>
		
</span>

<div class="clr"></div>

<table cellpadding="2" cellspacing="1">

	<tr>
		<td>&nbsp;</td>
		<%
			for (int i = 0; i<((_allDates.size()>=15)?_allDates.size()/2:_allDates.size()); i++) {
		%>
		<td width=50px class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDates.get(i) == null) ? "" : uF.getDateFormat((String) _allDates.get(i), CF.getStrReportDateFormat(), "dd MMM"))%>
		</td>
		<%
			}
		%>
	</tr>

	
	<tr>
		<td class="reportHeading alignCenter">Rostered</td>

		<%
			for (int i = 0; i<((_allDates.size()>=15)?_allDates.size()/2:_allDates.size()); i++) {
		%>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmRosterHours.get((String)_allDates.get(i)),"0") %></td>
		<%
			}
		%>



	</tr>


	<tr>
		<td class="reportHeading alignCenter">Actual</td>

		<%
			for (int i = 0; i<((_allDates.size()>=15)?_allDates.size()/2:_allDates.size()); i++) {
		%>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmActualHours.get((String)_allDates.get(i)),"0") %></td>
		<%
			}
		%>



	</tr>

</table>


<br/><br/>

<table cellpadding="2" cellspacing="1">

	<tr>
		<td>&nbsp;</td>
		<%
			for (int i = (int)Math.floor(_allDates.size()/2) ; i < _allDates.size(); i++) {
		%>
		<td width=50px class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDates.get(i) == null) ? "" : uF.getDateFormat((String) _allDates.get(i), CF.getStrReportDateFormat(), "dd MMM"))%>
		</td>
		<%
			}
		%>
	</tr>

	
	<tr>
		<td class="reportHeading alignCenter">Rostered</td>

		<%
			for (int i = (int)Math.floor(_allDates.size()/2); i < _allDates.size(); i++) {
		%>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmRosterHours.get((String)_allDates.get(i)), "0") %></td>
		<%
			}
		%>



	</tr>


	<tr>
		<td class="reportHeading alignCenter">Actual</td>

		<%
			for (int i = (int)Math.floor(_allDates.size()/2); i < _allDates.size(); i++) {
		%>
		<td class="reportLabel alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmActualHours.get((String)_allDates.get(i)), "0") %></td>
		<%
			}
		%>



	</tr>

</table>


<%
}
else{
%>
<span style="color: #346897; font-size: 14px; font-weight: 300; margin: 20px 0px 20px  0px; float: left;">
	Total Hours For Employees Data is not available!
</span>
<%} %>

</div>


</div>
