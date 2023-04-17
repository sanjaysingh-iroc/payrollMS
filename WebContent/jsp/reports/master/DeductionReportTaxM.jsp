<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
if(CF==null)return;
%> 



<script type="text/javascript" charset="utf-8">

			$(document).ready( function () {
				
				var sbGenderList = '<%= ((String)request.getAttribute("sbGenderList")) %>';
				
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
			                           			sAddURL: "AddDeductionTax.action?operation=A",
												sDeleteURL: "AddDeductionTax.action?operation=D",
												"aoColumns": [
		                    									
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeductionTax.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeductionTax.action?operation=U"
		                    									},
		                    									/* {
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
																	type: 'select',
																	data: sbGenderList,
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeductionTax.action?operation=U"
		                    									}, */
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeductionTax.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeductionTax.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeductionTax.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
																	data: "{'A':'Amount','P':'Percent'}",
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddDeductionTax.action?operation=U"
		                    									},
															 ],
												oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}, 
												oAddNewRowButtonOptions: {label: "Add...", icons: {primary:'ui-icon-plus'}},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: { 	
	                                                    title: 'Add a new tax deduction slab',
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



 <div>   
    
    
    <s:form name="frmTaxDeduction" action="DeductionReportTax">
    <s:hidden name="T" value="M"/>
    <s:hidden name="S" value="M"/>
	<s:select label="Select Financial Year" name="financialYear" listKey="financialYearId"
				listValue="financialYearName" headerKey="0" 
				onchange="document.frmTaxDeduction.submit();"
				list="financialYearList" key="" />
		
</s:form>
    
    
    
    
<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<table class="display" id="lt">
	<thead>
		<tr>
			<th style="text-align: left;">Age From</th>
			<th style="text-align: left;">Age To</th>			
			<th style="text-align: left;">Income From</th>
			<th style="text-align: left;">Income To</th>
			<th style="text-align: left;">Deduction Amount</th>
			<th style="text-align: left;">Deduction Type</th>
			<!-- <th style="text-align: left;">Financial Year Start</th>
			<th style="text-align: left;">Financial Year End</th> -->
		</tr>
	</thead>
	<tbody>
	<% java.util.List cOuterList = (java.util.List)request.getAttribute("reportList"); %>
	 <% for (int i=0; i<cOuterList.size(); i++) { %>
	 <% java.util.List cInnerList = (java.util.List)cOuterList.get(i); %>
		<tr id = <%= cInnerList.get(0) %> >
			<td><%= cInnerList.get(1) %></td>
			<td><%= cInnerList.get(2) %></td>
			<td><%= cInnerList.get(4) %></td>
			<td><%= cInnerList.get(5) %></td>
			<td><%= cInnerList.get(6) %></td>
			<td><%= cInnerList.get(7) %></td>
			<%-- <td><%= cInnerList.get(8) %></td>
			<td><%= cInnerList.get(9) %></td> --%>
		</tr>
		<% } %>
	</tbody>
</table> 




</div>