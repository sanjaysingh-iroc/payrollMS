<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
});    
</script>

<script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
					var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
					var sbUserTypeList = '<%= ((String)request.getAttribute("sbUserTypeList")) %>';
					var sbEmpCodeList = '<%= ((String)request.getAttribute("sbEmpCodeList")) %>';
					var sbUserStatusList = '<%= ((String)request.getAttribute("sbUserStatusList")) %>';
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
			                           			sAddURL: "AddUser.action?operation=A",
												sDeleteURL: "AddUser.action?operation=D",
												"aoColumns": [
		                    									null,
		                    									null,
		                    									/* {
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									},
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',		                                                         	
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									} */
		                    									null,
		                    									null,
		                    									/* ,
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                                                         	data: sbUserTypeList,
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									} */null,
		                    									
		                    									/* {
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
																	data: sbUserStatusList,
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									} */null,   
		                    									null,
		                    									null,
		                    									/* {
		                    										tooltip: 'Click to View/Edit ACL'
		                    									} */
		                    									null
															],
												oDeleteRowButtonOptions: {label: "Remove", icons: {primary:'ui-icon-trash'}}, 
												oAddNewRowButtonOptions: {label: "Add...", icons: {primary:'ui-icon-plus'}},
												sAddDeleteToolbarSelector: ".dataTables_length" ,		
												oAddNewRowFormOptions: { 	
	                                                    title: 'Add a new User',
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
			
			
			
	function changeUserName(empid,userid,empname) { 
		
		
		removeLoadingDiv('the_div');
		var dialogEdit = '#changeUserNameDiv';
		var data1 = "<div id=\"the_div\"><div id=\"ajaxLoadImage\"></div></div>";
		dialogEdit = $(data1).appendTo('body');				
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 250,
					width : 400,
					modal : true,
					title : 'Change Username for '+empname,
					open : function() {
						var xhr = $.ajax({  
							url : 'ChangeUserName.action?empid='+empid+'&userid='+userid,
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
	
	
	function changeUserType(empid,userid,empname) { 
		
		removeLoadingDiv('the_div');
		var dialogEdit = '#changeUserNameDiv';
		var data1 = "<div id=\"the_div\"><div id=\"ajaxLoadImage\"></div></div>";
		dialogEdit = $(data1).appendTo('body');				
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 280,
					width : 450,
					modal : true,
					title : 'Change User type for '+empname,
					open : function() {
						var xhr = $.ajax({  
							url : 'ChangeUserType.action?empid='+empid+'&userid='+userid,
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

	
	function getWlocation() {
		var org = $('#strOrg').val();
	 	 var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var xhr = $.ajax({
					url : 'GetOrgWLocationList.action?strOrgId='+org+"&type=UTChange",
					cache : false,
					success : function(data) {
						document.getElementById('idWlocId').innerHTML=data;
						getdepartment(val);
					}
				});
			}
	   }
	
</script>
		
		

		
		
<%
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
				|| strUserType.equalsIgnoreCase(IConstants.CFO) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){
%>
<!-- Custom form for adding new records -->
<jsp:include page="../../employee/AddUser.jsp" flush="true" /> 
<%} %>

 
<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="User Database" name="title"/>
</jsp:include>
 

    <div id="printDiv" class="leftbox reportWidth">

	<s:form name="frmUsers" action="UserReport" theme="simple">
		<div class="filter_div">

			<div class="filter_caption">Filter</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
				<p style="padding-left: 5px;">Organisation</p>
				<s:select theme="simple" name="f_org" listKey="orgId"
					cssStyle="float:left;margin-right: 10px;" listValue="orgName"
					onchange="document.frmUsers.submit();" list="organisationList"
					key="" /> <!-- headerKey="" headerValue="All Organisations" -->
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
				<p style="padding-left: 5px;">Location</p>
				<s:select theme="simple" name="f_strWLocation" id="f_strWLocation"
					listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
					listValue="wLocationName" list="wLocationList" key=""
					multiple="true" />
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
				<p style="padding-left: 5px;">Department</p>
				<s:select name="f_department" id="f_department"
					list="departmentList" listKey="deptId"
					cssStyle="float:left;margin-right: 10px;" listValue="deptName"
					multiple="true"></s:select>
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
				<p style="padding-left: 5px;">Service</p>
				<s:select name="f_service" id="f_service" list="serviceList"
					listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
					listValue="serviceName" multiple="true"></s:select>
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
				<p style="padding-left: 5px;">Level</p>
				<s:select theme="simple" name="f_level" id="f_level"
					listKey="levelId" cssStyle="float:left;margin-right: 10px;"
					listValue="levelCodeName" list="levelList" key="" multiple="true" />
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: 144px;">
				<p style="padding-left: 5px;">&nbsp;</p>
				<s:submit value="Submit" cssClass="input_button"
					cssStyle="margin:0px" />
			</div>

		</div>
	</s:form>


	<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<table class="display" id="lt">
	<thead>
		<tr>
			
			<th style="text-align: left;">Teacher Code</th>
			<th style="text-align: left;">Teacher Name</th>
			<th style="text-align: left;">UserName</th>
			<th style="text-align: left;">Password</th>
			<th style="text-align: left;">Created On</th>
			<th style="text-align: left;">User Type</th>
			<th style="text-align: left;">Status</th>
			<th style="text-align: left;">Reset</th>
			<th style="text-align: left;">Last Reset On</th>
			<th style="text-align: left;">Action</th>
		</tr>
	</thead>
	<tbody>
	<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
	 <% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
	 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
		<tr id = <%=cinnerlist.get(0) %> >
			<td><%=cinnerlist.get(1) %></td>
			<td><%=cinnerlist.get(2) %></td>
			<td><%=cinnerlist.get(3) %></td>
			<td><%=cinnerlist.get(4) %></td>
			<td><%=cinnerlist.get(5) %></td>
			<td><%=cinnerlist.get(6) %></td>
			<td><%=cinnerlist.get(7) %></td>
			<td><%=cinnerlist.get(8) %></td>
			<td><%=cinnerlist.get(9) %></td>
			<td><%=cinnerlist.get(10) %></td>				
		</tr>
		<% } %>
	</tbody>
</table>

</div>
<div id="changeUserNameDiv"></div>