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
								"sDom": '<"H"Tf>rt<"F"ip>',
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
			                           			sAddURL: "AddDeduction.action?operation=A",
												sDeleteURL: "AddDeduction.action?operation=D",
												"aoColumns": [
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeduction.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeduction.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeduction.action?operation=U"
		                    									},
		                    									
															 ],
												oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}, 
												oAddNewRowButtonOptions: {label: "Add...", icons: {primary:'ui-icon-plus'}},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: { 	
	                                                    title: 'Add a new Deduction Policy',
														show: "blind", 
														width: '700px',
														modal: true,
														hide: "explode"
												},
												fnOnAdded: function(status)
												{ 	
													window.location.reload( true );
												}
											}); 
					}else {
						$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
					} 
									
			});
		</script>
	</head>

<!-- Custom form for adding new records -->

<%
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
				|| strUserType.equalsIgnoreCase(IConstants.CFO)  || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){
%>
<jsp:include page="../../policies/AddDeduction.jsp" flush="true" />
<%} %>

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Deduction List" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth" >
        
    
<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<table class="display" id="lt">
	<thead>
		<tr>
			<th style="text-align: left;">Income From</th>
			<th style="text-align: left;">Income To</th>
			<th style="text-align: left;">Deduction Amount</th>
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
		</tr>
		<% } %>
	</tbody>
</table> 

</div>    
