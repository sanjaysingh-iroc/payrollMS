<%@page import="java.util.List"%>

<script type="text/javascript" charset="utf-8">

$(document).ready( function () {
	<%-- $('#lt').dataTable({ bJQueryUI: true, "sPagiType": "full_numbers", 
		"aaSorting": [],		
		"sDom": '<"H"T>rt<"F"ip>',
		oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
			aButtons: [
				"csv", "xls", {
					sExtends: "pdf",
					sPdfOrientation: "landscape"
					//sPdfMessage: "Your custom message would go here."
					}, "print" 
			]
		}	
	}) --%> 
	$('#lt').dataTable({
		bJQueryUI : true,
		"sPaginationType" : "full_numbers",
		"bSort": false
	});
});
</script>





<div class="tab_pagetitle">My Requisitions</div>

        
<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<table class="display" id="lt">
	<thead>
		<tr>
			
			<th style="text-align: left;">Requisition Type</th>
			<th style="text-align: left;">Date</th>
			<th style="text-align: left;">Status</th>
		</tr>
	</thead>
	<tbody>
	<% java.util.List cOuterList = (java.util.List)request.getAttribute("reportList"); %>
	 <% for (int i=0; cOuterList!=null && i<cOuterList.size(); i++) { %>
	 <% java.util.List cInnerList = (java.util.List)cOuterList.get(i); %>
		<tr>
			<td><%=  cInnerList.get(0) %></td>
			<td><%=  cInnerList.get(1) %></td>
			<td><%=  cInnerList.get(2) %></td>
		</tr>
		<% } %>
	</tbody>
</table>


