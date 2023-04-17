<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*,com.konnect.jpms.util.UtilityFunctions,com.konnect.jpms.task.*"%>


<script type="text/javascript">

/* 	function approveBillableHrs(empId, submitdate) {
		
		get
	}
	 */
	
</script>
	
<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();
List alReport = (List)request.getAttribute("alReport");
List<List<String>> projectList = (List<List<String>>)request.getAttribute("projectList");

%>

<div class="leftbox reportWidth" style="font-size: 12px;">

	<table class="tb_style" style="width:100%">
		<tr>
			<th>Status</th>
			<th>Employee Name</th>
			<th>Submitted On</th>
			<th>Approved By</th>
			<th>Billable Efforts</th>
			<!-- <th>Actual worked days</th> -->
			<%-- <th>View</th>
			<%if(uF.parseToBoolean(CF.getIsWorkFlow())) { %>
				<th class=" alignLeft">Workflow</th>
			<% } %> --%>
			<th>Approve</th>
		</tr>
	
	<%
	for(int i=0; alReport != null && i<alReport.size(); i++) {
		List alInner = (List)alReport.get(i);
	%>
		<tr>
			<td align="center"><%=alInner.get(0)%></td>
			<td><%=alInner.get(1)%></td>
			<td align="center"><%=alInner.get(2)%></td>
			<td><%=alInner.get(3)%></td>
			<td nowrap="nowrap" class="padRight20" align="right"><%=alInner.get(5)%> </td>
			<%-- <td class="padRight20" align="right"><%=alInner.get(5)%></td> --%>
			<%-- <td><%=alInner.get(6)%></td>
			<%if(uF.parseToBoolean(CF.getIsWorkFlow())){ %>
				<td><%=(String)alInner.get(8) %></td>
			<%} %> --%>
			<td nowrap="nowrap"><span id="myProjectTimesheet_<%=alInner.get(9) %>_<%=alInner.get(10) %>"> <%=alInner.get(7)%> </span></td>
		</tr>
	
	<% } if(alReport.size() == 0) { %>
	<tr><td colspan="9"><div class="msg nodata"><span>No timesheet for approval</span></div></td></tr>
	<% } %>
	</table>

 
</div>
