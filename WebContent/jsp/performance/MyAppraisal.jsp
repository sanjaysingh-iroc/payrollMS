<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>



<script type="text/javascript">
   
function show_empList() {
	dojo.event.topic.publish("show_empList");
}

function callMe() { 
	window.location='AddEmployeeMode.action';
}

</script>


<script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
				
				var sbStatusList = '<%= ((String)request.getAttribute("sbStatusList")) %>';
							$('#lt').dataTable({ bJQueryUI: true, 
								  								
								"sPaginationType": "full_numbers",
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
									//sDeleteURL: "AddEmployee.action?operation=D",
									//oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}},
									"aoColumns": [
	      									null,
	      									null,
	      									null,
	      									null,
	      									null,
	      									null,
	      									{
	      										indicator: 'Saving...',
	                                           	tooltip: 'Click to edit ',
	                                           	type: 'select',
                                             	data: sbStatusList,
	                                           	submit:'Save',
	                                       		sUpdateURL: "AddEmployee.action?operation=ajax"
	      									},
	      									null,
	      									null
											]
								});
							});
							
							/* ).makeEditable({
								sDeleteURL: "AddEmployee.action?operation=D",
								oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}
							
							}); */
			
			
							

							function seeQuestions(id,empId) {

								var dialogEdit = '#comment';
								$(dialogEdit).dialog({
									autoOpen : false,
									bgiframe : true,
									resizable : true,
									height : 600,
									width : 850,
									modal : true,
									title : 'See Comment',
									open : function() {
										var xhr = $.ajax({
											url : "MyAppraisalDetail.action?id="+id+"&empId="+empId,
											cache : false,
											success : function(data) {
												$(dialogEdit).html(data);
											}
										});
										xhr = null;

									},
									overlay : {
										backgroundColor : '#000',
										opacity : 0.5
									}
								});

								$(dialogEdit).dialog('open');
								
								
							}
							 
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My Appraisal Report" name="title"/>
</jsp:include>

    <div id="printDiv" class="leftbox reportWidth">
 
    
    
    
    
<!-- Place holder where add and delete buttons will be generated -->
<!-- <div class="add_delete_toolbar"></div> -->
     
     
    
    <s:property value="message"/>
    
   
    	<table class="display" id="lt">
    	
				<thead>
					<tr> 
						<th style="text-align: left;">Appraisal Name</th>
						<th style="text-align: left;">Orientation Type</th>
						<th style="text-align: left;">Frequency</th>
						<th style="text-align: left;">From Date</th>
						<th style="text-align: left;">End date</th>
						<th style="text-align: left;">Action</th>

					</tr>
				</thead>
				
				<tbody>
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("outerList"); 
				
				for(int i=0;i<couterlist.size();i++){
					List<String> innerList=couterlist.get(i);%>
					
					<tr id = "<%= innerList.get(0) %>" >
						<td><%= innerList.get(1) %></td>
						<td><%= innerList.get(2) %></td>
						<td><%= innerList.get(7) %></td>
						<td><%= innerList.get(8) %></td>
						<td><%= innerList.get(9) %></td>
						<td>
						<%-- <a href="javascript:void(0);" onclick="seeQuestions('<%=innerList.get(0) %>','<%=(String)session.getAttribute(IStatements.EMPID) %>')"><img src="images1/icons/approved.png" title="My Appraisal Details" /></a> --%>
						<a href="StaffAppraisal.action?id=<%= innerList.get(0) %>&empID=<%=(String)session.getAttribute(IStatements.EMPID) %>" target="_blank"><img src="images1/viewdetails.png" title="View" /></a>
						</td>
						
					</tr>
				<%}	%>
				
				</tbody>
			</table> 
    </div>
