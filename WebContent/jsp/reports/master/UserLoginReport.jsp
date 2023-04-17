<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

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
		                    									,
		                    									{
		                    										indicator: 'Saving...',
		                                                         	tooltip: 'Click to edit ',
		                                                         	type: 'select',
		                                                         	data: sbUserTypeList,
		                                                         	submit:'Save',
	                                                         		sUpdateURL: "AddUser.action?operation=U"
		                    									},
		                    									
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
			
			
			

			var dialogEdit = '#changeUserNameDiv';
			function changeUserName(empid,userid,empname) { 
				
				
				document.getElementById("changeUserNameDiv").innerHTML = '';
				
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
			
			$(document).ready(function(){
				 
			    $('#password').pwdMeter({
			        minLength: 6,
			        displayGeneratePassword: false,
			        generatePassText: 'Password Generator',
			        generatePassClass: 'GeneratePasswordLink',
			        randomPassLength: 13
			    });

			});
		</script>
	</head>

<%
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
				|| strUserType.equalsIgnoreCase(IConstants.CFO) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){
%>
<!-- Custom form for adding new records -->
<jsp:include page="../../employee/AddUser.jsp" flush="true" /> 
<%} %>
 
<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="User Login Report" name="title"/>
</jsp:include>
 

    <div id="printDiv" class="leftbox reportWidth">
         
         
         
         
<s:form name="frmUsers" action="UserLoginReport" theme="simple">
<div class="filter_div">

<div class="filter_caption">Filter</div>

<s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
                         listValue="orgName" headerKey="" headerValue="All Organisations"
                         onchange="document.frmUsers.submit();"
                         list="organisationList" key=""  />

    <s:select theme="simple" name="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         list="wLocationList" key=""  />
                    
    <s:select name="f_department" list="departmentList" listKey="deptId" cssStyle="float:left;margin-right: 10px;"
    			listValue="deptName" headerKey="0" headerValue="All Departments" 
    			></s:select>
    
    <s:select name="f_service" list="serviceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
    			listValue="serviceName" headerKey="0" headerValue="All Services" 
    			></s:select>
    			
    <s:select theme="simple" name="f_level" listKey="levelId" headerValue="All Levels"  cssStyle="float:left;margin-right: 10px;"
	                            listValue="levelCodeName" headerKey="0" 
	                            list="levelList" key="" required="true" />
     
       
    
    <s:submit value="Submit" cssClass="input_button"  cssStyle="margin:0px"/>

    </div>
</s:form>
         
         
<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<table class="display" id="lt">
	<thead>
		<tr>
			
			<th style="text-align: left;">Emp Code</th>
			<th style="text-align: left;">Emp Name</th>
			<th style="text-align: left;">UserName</th>
			<th style="text-align: left;">Password</th>
			<th style="text-align: left;">Created On</th>
			<th style="text-align: left;">User Type</th>
			<th style="text-align: left;">Status</th>
			<!-- <th style="text-align: left;">Reset</th> -->
			<th style="text-align: left;">Last Reset On</th>
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
			<td><%=  cinnerlist.get(7) %></td>
			<%-- <td><%=  cinnerlist.get(8) %></td> --%>
			<td><%=  cinnerlist.get(9) %></td>				
		</tr>
		<% } %>
	</tbody>
</table>

</div>

<div id="changeUserNameDiv"></div>
