<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>
function getReimbursementDetails(pro_id, emp_id) {

	var dialogEdit = '#addproject'; 
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false, 
		height : 350,
		width : 800, 
		modal : true,
		title : 'Reimbursement Summary',
		open : function() {
			var xhr = $.ajax({
				url : "ProReimbursementSummary.action?pro_id="+pro_id+"&emp_id="+emp_id,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
			xhr = null;
		},
		overlay : {
			backgroundColor : '#000',
			opacity : 0.5
		}
	});
	$(dialogEdit).dialog('open');
}

</script>

<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();

Map<String, String> hmProInfoDisplay = (Map<String, String>) request.getAttribute("hmProInfoDisplay");
if(hmProInfoDisplay == null) hmProInfoDisplay = new HashMap<String, String>();

List alOuter = (List)request.getAttribute("alOuter");
Map<String, List<List<String>>> hmProjectCostSubTaskReport = (Map<String, List<List<String>>>) request.getAttribute("hmProjectCostSubTaskReport");
List alProfitSummary = (List)request.getAttribute("alProfitSummary");
%>

<div style="margin: 5px;" > <!-- class="leftbox reportWidth" -->
<h3>Project Profitability</h3>
<table class="tb_style" style="width:350px">

	<tr>
		<th>&nbsp;</th>
		<th>Actual</th>
		<th>Budgeted</th>
	</tr>
	
	<tr>
		<th>Gross Profit</th>
		<td nowrap="nowrap" class="alignRight padRight20"><%=(String)alProfitSummary.get(0)%></td>
		<td nowrap="nowrap" class="alignRight padRight20"><%=(String)alProfitSummary.get(1)%></td>
	</tr>
	<tr>
		<th>Gross Profit Margin</th>
		<td class="alignRight padRight20"><%=(String)alProfitSummary.get(2)%></td>
		<td class="alignRight padRight20"><%=(String)alProfitSummary.get(3)%></td>
	</tr>
</table>

<br/>

<h3>Cost Summary for <%=((request.getAttribute("PROJECT_NAME")!=null)?request.getAttribute("PROJECT_NAME"):"") %></h3>

<table class="tb_style">

<tr>
	<th>Task Name</th>
	<th>Resource Name</th>
	<th>Service</th>
	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<th>
		<%
			String costLbl = "Hr";
			if((String)request.getAttribute("strBillingType") != null && "D".equalsIgnoreCase((String)request.getAttribute("strBillingType"))) {
				costLbl = "Day";
			} else if((String)request.getAttribute("strBillingType") != null && "M".equalsIgnoreCase((String)request.getAttribute("strBillingType"))) {
				costLbl = "Month";
			}
		%>
		Cost/<%=costLbl %> *<br/>(<%=(String)request.getAttribute("SHORT_CURR") %>)</th>
	<% } %>
	
	<th>Actual Time</th>
	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<th>Actual Cost<br/>(<%=(String)request.getAttribute("SHORT_CURR") %>) **</th>
	<% } %>
	<th>Estimated Time</th>
	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<th>Budgeted Cost<br/>(<%=(String)request.getAttribute("SHORT_CURR") %>)</th>
	<% } %>
	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
		<th>Billable Rate<br/>(<%=(String)request.getAttribute("SHORT_CURR") %>)</th>
		<th>Billable Cost<br/>(<%=(String)request.getAttribute("SHORT_CURR") %>)</th>
	<% } %>
</tr>


<%for(int i=0; i<alOuter.size(); i++) {
	List alInner = (List)alOuter.get(i);
%>

<% if(i == alOuter.size()-1) { %>
<tr style="border-top:2px solid #000fff">
<% } else { %>
<tr>
<% } %>

	<td><%=alInner.get(1) %></td>
	<td><%=alInner.get(2) %></td>
	<td><%=alInner.get(3) %></td>
	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<td class="alignRight padRight20"><%=alInner.get(4) %></td>
	<% } %>
	<td class="alignRight padRight20"><%=alInner.get(5) %></td>
	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<td class="alignRight padRight20"><%=alInner.get(6) %></td>
	<% } %>
	<td class="alignRight padRight20" style="background-color: lightgreen"><%=alInner.get(7) %></td>
	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<td class="alignRight padRight20" style="background-color: lightgreen"><%=alInner.get(8) %></td>
	<% } %>
	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
		<td class="alignRight padRight20" style="background-color: cyan"><%=alInner.get(9) %></td>
		<td class="alignRight padRight20" style="background-color: cyan"><%=alInner.get(10) %></td>
	<% } %>
</tr>

	<%
	if(hmProjectCostSubTaskReport != null) {
	List<List<String>> alSubTasks = hmProjectCostSubTaskReport.get(alInner.get(0));
	for(int j=0; alSubTasks != null && j<alSubTasks.size(); j++) {
		List<String> innerList = alSubTasks.get(j);
	%>
		<tr>
			<td><%=innerList.get(1) %> [ST]</td>
			<td><%=innerList.get(2) %></td>
			<%-- <td><%=innerList.get(3) %></td> --%>
			<td><%=innerList.get(4) %></td>
			<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
			<td class="alignRight padRight20">&nbsp;</td>
			<% } %>
			<td class="alignRight padRight20">&nbsp;</td>
			<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
			<td class="alignRight padRight20">&nbsp;</td>
			<% } %>
			<td>&nbsp;</td>
			<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
			<td>&nbsp;</td>
			<% } %>
			<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			<% } %>
		</tr>
	<%} }%>
				
<% } %>

</table>
<br/>

	<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<div> ** Cost is derived based on calculation provided in setting.</div>
	<% } %>

</div>


		
    




