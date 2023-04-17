<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://displaytag.sf.net/" prefix="display"%>
<script type="text/javascript" charset="utf-8"> 
	$(function () {
		$('#reportTable').DataTable({
   			"order": [],
   			"columnDefs": [ {
   			      "targets"  : 'no-sort',
   			      "orderable": false
   			    }],
 			'dom': 'lBfrtip',
   		    'buttons': [
   				'copy', 'csv', 'excel', 'pdf', 'print'
   		    ]
   		});
	});
</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Travel Advance Eligibility" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 20px 5px 0px; overflow-y: auto; min-height: 600px;">
		<table class="table table-bordered" id="reportTable">
			<thead>
				<tr>
					<th style="text-align: left;">Emp Code</th>
					<th style="text-align: left;">Emp Name</th>
					<th style="text-align: left;">Eligibility Amount</th>
					<th style="text-align: left;">Approved By</th>
					<th style="text-align: center;">Approved On</th>
					<th style="text-align: center;" class="no-sort">Eligibility</th>
				</tr>  
			</thead>
			<tbody>
				<% java.util.List couterlist = (java.util.List)request.getAttribute("alreportList"); %>
				<% for (int i=0; i<couterlist.size(); i++) { %>
				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id = <%= cinnerlist.get(0) %> >
					<td><%= cinnerlist.get(1) %></td>  
					<td><%= cinnerlist.get(2) %></td>
					<td align="right" style="padding-right:20px"><%= cinnerlist.get(3) %></td>
					<td><%= cinnerlist.get(4) %></td>
					<td align="center"><%= cinnerlist.get(5) %></td>
					<td align="center"><%= cinnerlist.get(6) %></td>
				</tr>
				<% } %>
			</tbody>
		</table> 

	</div>
	<!-- /.box-body -->



