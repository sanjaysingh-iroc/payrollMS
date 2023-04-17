<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page buffer = "16kb" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
						
						var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
						var sbWLocationList = '<%= ((String)request.getAttribute("sbWLocationList")) %>';
						var sbDeptList = '<%= ((String)request.getAttribute("sbDeptList")) %>';
						var sbColorList = '<%= ((String)request.getAttribute("sbColorList")) %>';
						
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
			                           			sAddURL: "AddHolidays.action?operation=A&orgId=<%=request.getAttribute("strOrg") %>",
					   							sDeleteURL: "AddHolidays.action?operation=D",
					   							"aaSorting": [null],
												"aoColumns": [
		                    									{
		                    										cssclass: "required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'save',
	                                                         		sUpdateURL: "AddHolidays.action?operation=U"
		                    									},
		                    									null,
		                    									{
		                    										cssclass: "required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'save',
	                                                         		sUpdateURL: "AddHolidays.action?operation=U"
		                    									},
		                    									{
		                    										cssclass: "required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                                                         	data: sbWLocationList, 
		                                                         	submit:'save',
	                                                         		sUpdateURL: "AddHolidays.action?operation=U"
		                    									},
		                    									{
		                    										cssclass: "required",
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                                                         	data: sbColorList,
		                                                         	submit:'save',
	                                                         		sUpdateURL: "AddHolidays.action?operation=U"
		                    									},
															],
												oDeleteRowButtonOptions: {label: "Remove"}, 
												oAddNewRowButtonOptions: {label: "Add..."},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: { 	
	                                                    title: 'Add a new Holiday Policy',
														show: "blind",
														width: '700px',
														modal: true,
														hide: "explode"
												},
												fnOnAdded: function(status)
												{ 	
													window.location.reload( true );
												},
												fnOnEdited: function(status)
												{ 	
													window.location.reload( true );
												}
											}); 
									}
						else {
							$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" })
							} 
				});
		</script>

<!-- Custom form for adding new records -->
<%
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
				|| strUserType.equalsIgnoreCase(IConstants.CFO)  || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){
%>
<jsp:include page="../policies/AddHolidays.jsp" flush="true" /> 
<%} %>
	

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Holidays" name="title"/>
</jsp:include>


    
     <div id="printDiv" class="leftbox reportWidth">
     
     
<div class="filter_div">
<div class="filter_caption">Select Organisation</div>
<s:form name="frm1" action="HolidayReport1" theme="simple">
	<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm1.submit();"></s:select>
	<s:select list="wLocationList" name="strWLocation" listKey="wLocationId" listValue="wLocationName" onchange="document.frm1.submit();"></s:select>
</s:form>
</div>
     
     
		<!-- Place holder where add and delete buttons will be generated -->
		<!-- <div class="add_delete_toolbar"></div> -->
		
		<table class="display" id="lt">
			<thead>
				<tr>
					<th>Date</th>
					<th>Day</th>
					<th>Description</th>
					<th>Work Location</th>
					<th>Colour</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 <% for (int i=0; i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id = <%= cinnerlist.get(0) %> >
					<td><%= cinnerlist.get(1) %></td>
					<td class="read_only"><%= cinnerlist.get(2) %></td>
					<td><%= cinnerlist.get(3) %></td>
					<td><%= cinnerlist.get(4) %></td>
					<td><%= cinnerlist.get(5) %></td>
				</tr>
				<% } %>
			</tbody>
		</table>
		
	</div>

