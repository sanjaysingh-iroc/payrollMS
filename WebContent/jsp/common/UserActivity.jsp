<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

 
<script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
				var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
				var sbDesigList = '<%= ((String)request.getAttribute("sbDesigList")) %>';
				if (usertype == '<%=IConstants.ADMIN%>' 
					|| usertype == '<%=IConstants.CEO%>' || usertype == '<%=IConstants.CFO%>'
						|| usertype == '<%=IConstants.ACCOUNTANT%>' || usertype == '<%=IConstants.HRMANAGER%>'
							|| usertype == '<%=IConstants.MANAGER%>') {
							$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers",
								"aaSorting": [],
								"sDom": '<"H"lTf>rt<"F"ip>',
								oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
									aButtons: [
										"csv", "xls", {
											sExtends: "pdf",
											sPdfOrientation: "landscape"
											//sPdfMessage: "Your custom message would go here."
										}, "print" 
									]
								} 
								
							
							}); 
					}
						
			});
		</script>



    <div class="pagetitle">
      <span>Employee Activity List</span>
    </div>


    <div class="leftbox reportWidth">
		
		<!-- Place holder where add and delete buttons will be generated -->
		<div class="add_delete_toolbar"></div>
		
		<table class="display" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Employee Name</th>
					<th style="text-align: left;">User Name</th>
					<th style="text-align: left;">Date</th>
					<th style="text-align: left;">Time</th>
					<th style="text-align: left;">IP Address</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 <% for (int i=0; i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id = <%= cinnerlist.get(0) %> >
					<td><%= cinnerlist.get(1) %></td>
					<td><%= cinnerlist.get(2) %></td>
					<td><%= cinnerlist.get(3) %></td>
					<td><%= cinnerlist.get(4) %></td>
					<td><%= cinnerlist.get(5) %></td>
				</tr>
				<% } %>
			</tbody>
		</table> 
 
 	</div>    
