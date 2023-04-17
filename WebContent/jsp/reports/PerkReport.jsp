<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
					var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
					var sbPerkTypeList = '<%= ((String)request.getAttribute("sbPerkTypeList")) %>';
					var sbPerkPaymentCycleList = '<%= ((String)request.getAttribute("sbPerkPaymentCycleList")) %>';
					var sbLevelList = '<%= ((String)request.getAttribute("sbLevelList")) %>';
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
							
							}).makeEditable({
			                           			sAddURL: "AddPerk.action?operation=A",
												sDeleteURL: "AddPerk.action?operation=D",
												"aoColumns": [
		                    									{
		                    										cssclass:"required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddPerk.action?operation=U"
		                    									},
		                    									{
		                    										cssclass:"required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',		                                                         	
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddPerk.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',		                                                         	
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddPerk.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                                                         	data: sbPerkTypeList,
		                                                         	submit:'Save',
		                                                         	sUpdateURL: "AddPerk.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                                                         	data: sbPerkPaymentCycleList,
		                                                         	submit:'Save',
		                                                         	sUpdateURL: "AddPerk.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                                                         	data: sbLevelList,
		                                                         	submit:'Save',
		                                                         	sUpdateURL: "AddPerk.action?operation=U"
		                    									}
		                    									
															],
												oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}, 
												oAddNewRowButtonOptions: {label: "Add...", icons: {primary:'ui-icon-plus'}},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: { 	
	                                                    title: 'Add a new Perk',
														show: "blind", 
														width: '700px',
														modal: true,
														hide: "explode"
												}
												}); 
							}
							
							else {
									$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
								} 
							});
		</script>
	</head>

<%
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
				|| strUserType.equalsIgnoreCase(IConstants.CFO) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){
%>
<!-- Custom form for adding new records -->
<jsp:include page="../master/AddPerk.jsp" flush="true" />  
<%} %>
 
 <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Approve Perks" name="title"/>
</jsp:include>

 

    <div id="printDiv" class="leftbox reportWidth">
        
<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<table class="display" id="lt">
	<thead>
		<tr>
			<th style="text-align: left;">Perk Code</th>
			<th style="text-align: left;">Perk Name</th>
			<th style="text-align: left;">Perk Description</th>
			<th style="text-align: left;">Perk Type</th>
			<th style="text-align: left;">Perk Payment Cycle</th>
			<th style="text-align: left;">Perk Level</th>
		</tr>
	</thead>
	<tbody>
	<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
	 <% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
	 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
		<tr id = <%= cinnerlist.get(0) %> >
			<td><%=  cinnerlist.get(1) %></td>
			<td><%=  cinnerlist.get(2) %></td>
			<td><%=  cinnerlist.get(3) %></td>
			<td><%=  cinnerlist.get(4) %></td>
			<td><%=  cinnerlist.get(5) %></td>
			<td><%=  cinnerlist.get(6) %></td>
		</tr>
		<% } %>
	</tbody>
</table>

</div>
