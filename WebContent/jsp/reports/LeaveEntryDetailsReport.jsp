<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page buffer = "16kb" %>

<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<style>
#tooltip{
	position:absolute;
	border:1px solid #333;
	background:#f7f5d1;
	padding:2px 5px;
	color:#333;
	display:none; 
	}	
</style> 

<script type="text/javascript" charset="utf-8">
		$(document).ready( function () {
				$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
		});
</script>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Employee Leave Typewise Details" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">

	<table class="display" id="lt">
			<thead>
				<tr>
					<th>Entrydate</th>
					<th>From date</th>
					<th>To date</th>
					<th>No of Leave<br>(in days)</th>
					<th>Reason</th>
					<th>Manager Reason</th>
					<th>Approved By</th>
					<th>Status</th>
					<th>Modify</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 <% for (int i=0; i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr>
					<td><%= cinnerlist.get(0) %></td>
					<td><%= cinnerlist.get(1) %></td>
					<td><%= cinnerlist.get(2) %></td>
					<td><%= cinnerlist.get(3) %></td>
					<td><%= cinnerlist.get(4) %></td>
					<td><%= cinnerlist.get(5) %></td>
					<td><%= cinnerlist.get(6) %></td>
					<td><%= cinnerlist.get(7) %></td>
					<td><%= cinnerlist.get(8) %></td>
				</tr>
				<% } %>
			</tbody>
	</table>

<h5>Please <a href="javascript:void(0)" onclick="history.go(-1)">click here</a> to go back to the previous page.</h5>

</div>