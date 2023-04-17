<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
 
<script type="text/javascript" charset="utf-8">
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
								
							
							}).makeEditable({
			                           			sAddURL: "AddCountry.action?operation=A",
												sDeleteURL: "AddCountry.action?operation=D",
												"aoColumns": [
		                     									{
		                     										cssclass:"required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit',
		                                                         	submit:'save',
	                                                         		sUpdateURL: "AddCountry.action?operation=U"
		                    									},
															],
												oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}, 
												oAddNewRowButtonOptions: {label: "Add...", icons: {primary:'ui-icon-plus'}},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: {	
	                                                    title: 'Add a new Country',
														show: "blind",
														width: '700px',
														modal: true,
														hide: "explode" 
														}
											}); 
						}else {
							$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
						} 
			});
			
</script>

<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<!-- Custom form for adding new records -->
<%
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.CFO))){ 
%>
<jsp:include page="../location/AddCountry.jsp" flush="true" />
<%} %>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Country List" name="title"/>
</jsp:include>

    

    <div class="leftbox reportWidth">
        <table class="display" id="lt">
			<thead>
				<tr>
					<th>Country</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 <% for (int i=0; i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id = <%= cinnerlist.get(0) %> >
					<td><%= cinnerlist.get(1) %></td>
				</tr>
				<% } %>
			</tbody>
		</table> 

    </div>
   

