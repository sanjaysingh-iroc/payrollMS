<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
if(CF==null)return;
%>


<script type="text/javascript" charset="utf-8">

/* $('#formAddNewRow').css("display","none"); */

			$(document).ready( function () {
				var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
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
					}else {
						$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
					} 
									
			});
		</script>
	</head>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Increment Details" name="title"/>
</jsp:include>




<div id="printDiv" class="leftbox reportWidth" >
        
    
<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>


<table class="display" id="lt">
	<thead>
		<tr>
			<th style="text-align: left;">Emp Name</th>
			<th style="text-align: left;">Emp Joining Date</th>
			<th style="text-align: left;">Last Increment Date</th>
			<th style="text-align: left;">New Increment Date</th>
			<th style="text-align: left;">Existing Basic</th>
			<th style="text-align: left;">Increment Amount</th>
			<th style="text-align: left;">Action</th>
		</tr>
	</thead>
	<tbody>
	<% java.util.List cOuterList = (java.util.List)request.getAttribute("reportList"); %>
	 <% for (int i=0; i<cOuterList.size(); i++) { %>
	 <% java.util.List cInnerList = (java.util.List)cOuterList.get(i); %>
		<tr id = <%= cInnerList.get(0) %> >
			<td> <%= cInnerList.get(1) %></td>
			<td> <%= cInnerList.get(2) %></td>
			<td> <%= cInnerList.get(3) %></td>
			<td> <%= cInnerList.get(4) %></td>
			<td> <%= cInnerList.get(5) %></td>
			<td> <%= cInnerList.get(6) %></td>
			<td> <%= cInnerList.get(7) %></td>
		</tr>
		<% } %>
	</tbody>
</table> 

</div>    
