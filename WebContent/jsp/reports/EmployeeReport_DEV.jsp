<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	UtilityFunctions uF=new UtilityFunctions();
%>



<%-- <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/jquery.dataTables.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/dataTables.fixedColumns.css">
<style type="text/css">

	/* Ensure that the demo table scrolls */
	th, td { white-space: nowrap; }
	div.dataTables_wrapper {
		width: 100%;
		margin: 0 auto;
	}
</style>

<script type="text/javascript" language="javascript" src="js/jquery1.js"></script>
<script type="text/javascript" language="javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" src="js/dataTables.fixedColumns.js"></script>


<script type="text/javascript" language="javascript">

$(document).ready(function() {
	var table = $('#example').DataTable( {
		scrollY:        "300px",
		scrollX:        true,
		scrollCollapse: true,
		paging:         true,
		pagingType: "full_numbers"
	} );
	new $.fn.dataTable.FixedColumns( table );
} );

</script> --%>
	
		
<script type="text/javascript">
   
function show_empList() {
	dojo.event.topic.publish("show_empList");
}

function callMe() { 
	window.location='AddEmployeeMode.action';
}

</script>



<s:if test="page=='Live'"> 

<script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
				
				var sbStatusList = '<%=((String) request.getAttribute("sbStatusList"))%>';
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
							
							
							$(function() {    
							    $("#strBirthStartDate").datepicker({dateFormat: 'dd/mm/yy'});
							    $("#strBirthEndDate").datepicker({dateFormat: 'dd/mm/yy'});
							    $("#strJoiningStartDate").datepicker({dateFormat: 'dd/mm/yy'});
							    $("#strJoiningEndDate").datepicker({dateFormat: 'dd/mm/yy'});
							    $("#strTerminateStartDate").datepicker({dateFormat: 'dd/mm/yy'});
							    $("#strTerminateEndDate").datepicker({dateFormat: 'dd/mm/yy'});   
							});
							
							function showattribList(check) {
								if (check==true) {
									document.getElementById("advanceFilterDiv").style.display="block";
								}else{
									document.getElementById("advanceFilterDiv").style.display="none";	
								}
							}

							$(document).ready(function() {
								 showattribList(<%=request.getAttribute("advanceFilter")!=null ? true : false %>);
							});


			
			
</script>


<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
	$("#f_status").multiselect();
});    
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Working People Database" name="title"/>
</jsp:include>

</s:if>

<s:elseif test="page=='Pending'">

	<script type="text/javascript" charset="utf-8">
				$(document).ready(function () {
					
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
						});
				});
				
	</script>
	
	
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
	$("#f_status").multiselect();
});    
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Pending People" name="title"/>
</jsp:include>

</s:elseif>
<s:else>

	<script type="text/javascript" charset="utf-8">
					$(document).ready(function () {
						
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
							});
					});
					
					
					
					
					
		</script>
		
		
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
	$("#f_status").multiselect();
});    
</script>
	
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Ex-People" name="title"/>
	</jsp:include>

</s:else>

<div id="printDiv" class="leftbox reportWidth">

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
<% 
String strMessage = (String)request.getAttribute(IConstants.MESSAGE);
if(strMessage == null) {
	strMessage = "";
}%>
<%= strMessage%>

	<s:if test="page=='Live'">
		<s:form name="frmLiveEmployee" action="EmployeeReport" theme="simple">
			<div class="filter_div">

				<div class="filter_caption">Filter</div>
				<div style="margin-top: 10px; float: left; width: 100%;">
					<div
						style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
						<p style="padding-left: 5px;">Organisation</p>
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
							cssStyle="float:left;margin-right: 10px;" listValue="orgName"
							onchange="document.frmLiveEmployee.submit();"
							list="organisationList" key="" /> <!-- headerKey="" headerValue="All Organisations" -->
					</div>
					<div
						style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
						<p style="padding-left: 5px;">Location</p>
						<s:select theme="simple" name="f_strWLocation" id="f_strWLocation"
							listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
							listValue="wLocationName" multiple="true" list="wLocationList"
							key="" />
					</div>
					<div
						style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
						<p style="padding-left: 5px;">Department</p>
						<s:select name="f_department" id="f_department"
							list="departmentList" listKey="deptId"
							cssStyle="float:left;margin-right: 10px;" listValue="deptName"
							multiple="true"></s:select>
					</div>
					<div
						style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
						<p style="padding-left: 5px;">Service</p>
						<s:select name="f_service" id="f_service" list="serviceList"
							listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
							listValue="serviceName" multiple="true"></s:select>
					</div>
					<div
						style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
						<p style="padding-left: 5px;">Level</p>
						<s:select theme="simple" name="f_level" id="f_level"
							listKey="levelId"
							cssStyle="float:left;margin-right: 10px;width:100px;"
							listValue="levelCodeName" multiple="true" list="levelList" key="" />
					</div>
					<div
						style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
						<p style="padding-left: 5px;">Status</p>
						<s:select theme="simple" name="f_status" id="f_status"
							listKey="statusId"
							cssStyle="float:left;margin-right: 10px;width:100px;"
							listValue="statusName" list="empStatusList" key=""
							multiple="true" />
					</div>
					<div
						style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:submit value="Submit" cssClass="input_button"
							cssStyle="margin:0px" />
					</div>
				</div>
				<div style="margin-top: 10px; float: left; width: 100%;">
					Advanced Filter&nbsp;<input type="checkbox" name="advanceFilter"
						value="AF"
						<%=request.getAttribute("advanceFilter") != null ? "checked" : ""%>
						onclick="showattribList(this.checked);" />
				</div>
				<div id="advanceFilterDiv"
					style="display: none; float: left; width: 100%;">
					<table style="margin-left: 83px;">
						<tr>
							<td><s:radio name="afParam" list="#{'1':'Birth Date'}" />
							</td>
							<td><s:textfield name="strBirthStartDate"
									id="strBirthStartDate" cssStyle="width:65px"></s:textfield> <s:textfield
									name="strBirthEndDate" id="strBirthEndDate"
									cssStyle="width:65px"></s:textfield></td>
						</tr>
						<tr>
							<td><s:radio name="afParam" list="#{'2':'Joining Date'}" />
							</td>
							<td><s:textfield name="strJoiningStartDate"
									id="strJoiningStartDate" cssStyle="width:65px"></s:textfield> <s:textfield
									name="strJoiningEndDate" id="strJoiningEndDate"
									cssStyle="width:65px"></s:textfield></td>
						</tr>
						<tr>
							<td><s:radio name="afParam" list="#{'3':'Terminated'}" />
							</td>
							<td><s:textfield name="strTerminateStartDate"
									id="strTerminateStartDate" cssStyle="width:65px"></s:textfield>
								<s:textfield name="strTerminateEndDate" id="strTerminateEndDate"
									cssStyle="width:65px"></s:textfield></td>
						</tr>
						<tr>
							<td valign="top"><s:radio name="afParam"
									list="#{'4':'Education'}" />
							</td>
							<td><s:select theme="simple" name="f_education"
									listKey="eduId" headerValue="All"
									cssStyle="float:left;margin-right: 10px;width:150px;"
									listValue="eduName" headerKey="" multiple="true" size="4"
									list="eduList" key="" required="true" /></td>
						</tr>
					</table>
				</div>
			</div>
		</s:form>
	</s:if>
	<s:elseif test="page=='Pending'">
		<s:form name="frmPendingEmployee" action="PendingEmployeeReport"
			theme="simple">
			<div class="filter_div">

				<div class="filter_caption">Filter</div>

				<div
					style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
					<p style="padding-left: 5px;">Organisation</p>
					<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;" listValue="orgName"
						headerKey="" headerValue="All Organisations" onchange="document.frmPendingEmployee.submit();" list="organisationList" key="" /> <!-- headerKey="" headerValue="All Organisations" -->
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
					<p style="padding-left: 5px;">Servce</p>
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
					style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
					<p style="padding-left: 5px;">&nbsp;</p>
					<s:submit value="Submit" cssClass="input_button"
						cssStyle="margin:0px" />
				</div>

			</div>
		</s:form>
	</s:elseif>
	<s:else>
		<s:form name="frmExEmployee" action="ExEmployeeReport" theme="simple">
			<div class="filter_div">

				<div class="filter_caption">Filter</div>
				<div
					style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
					<p style="padding-left: 5px;">Organisation</p>
					<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
						cssStyle="float:left;margin-right: 10px;" listValue="orgName"
						onchange="document.frmExEmployee.submit();"
						list="organisationList" key="" /> <!-- headerKey="" headerValue="All Organisations" -->

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
					style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
					<p style="padding-left: 5px;">&nbsp;</p>
					<s:submit value="Submit" cssClass="input_button"
						cssStyle="margin:0px" />
				</div>
			</div>
		</s:form>
	</s:else>










	<!-- Place holder where add and delete buttons will be generated -->
	<!-- <div class="add_delete_toolbar"></div> -->

	<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || 
			strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.CFO))) {
          %>
	<input type="button" onclick="callMe()" value="Add New Teacher" class="input_button">
	<%
    	}
    %>

	<s:property value="message" />

	<s:if test="page=='Live'">
	
<script>
function selectall(x,strEmpId){
	var  status=x.checked; 
	var  arr= document.getElementsByName(strEmpId);
	for(i=0;i<arr.length;i++){ 
  		arr[i].checked=status;
 	}
}
</script>
	
<%-- <div class="container">
		<section> --%>
		<!-- <table id="example" class="stripe row-border order-column" cellspacing="0" width="100%"> -->
		<s:form theme="simple" action="EmployeeBulkActivity" method="POST">
		<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || 
			strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.CFO))) {
          %>
			<p style="float: right; margin-bottom: 24px;"><input type="submit" value="Bulk Teacher Activity" class="input_button"/></p>
		<%
	    	}
	    %>
			<table id="lt" class="display" style="width:100%">
				<thead>
					<tr>
						<th align="left"><input type="checkbox" onclick="selectall(this,'strIsAssigActivity')" checked="checked"/></th>
						<th style="text-align: left;">Teacher Code</th>
						<th style="text-align: left;">First Name</th>
						<th style="text-align: left;">Middle Name</th>
						<th style="text-align: left;">Last Name</th>
						<th style="text-align: left;">Joining Date</th>
						<th style="text-align: left;">Designation</th>
						<th style="text-align: left;">Department</th>
						<th style="text-align: left;">Work Location</th>
						<th style="text-align: left;">Teacher Status</th>
						<th style="text-align: left;">Employment Type</th>
	
						<s:if test="advanceFilter!=null && advanceFilter=='AF'">
							<%
								String af=(String)request.getAttribute("afParam");
								if(af!=null && af.equals("1")){%>
							<th style="text-align: left;">Birth Date</th>
							<%}else if(af!=null && af.equals("3")){%>
							<th style="text-align: left;">Terminate Date</th>
							<%}else if(af!=null && af.equals("4")){%>
							<th style="text-align: left;">Education</th>
							<%} %>
						</s:if>
						<th style="text-align: left;">Facts</th>
	
	
					</tr>
				</thead>
				<tbody>
					<%
						java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
						Map<String, String> hmEducation=(Map<String, String>)request.getAttribute("hmEducation");
						if(hmEducation==null) hmEducation=new HashMap<String, String>();
						
					 	for (int i = 0; couterlist != null && i < couterlist.size(); i++) {
					 		java.util.List cinnerlist = (java.util.List) couterlist.get(i);
					 %>
	
					<tr id=<%=cinnerlist.get(0)%>>
						<td><input type="checkbox" name="strIsAssigActivity" value="<%=cinnerlist.get(0)%>" checked/></td>
						<td><%=cinnerlist.get(1)%></td>
						<td><%=cinnerlist.get(2)%></td>
	
						<td><%=cinnerlist.get(3)%></td>
						<td><%=cinnerlist.get(4)%></td>
						<td><%=cinnerlist.get(5)%></td>
	
						<td><%=cinnerlist.get(6)%></td>
						<td><%=cinnerlist.get(7)%></td>
						<td><%=cinnerlist.get(8)%></td>
						<td><%=cinnerlist.get(9)%></td>
						<td><%=cinnerlist.get(10)%></td>
	
						<s:if test="advanceFilter!=null && advanceFilter=='AF'">
							<%	String af=(String)request.getAttribute("afParam");
								if(af!=null && af.equals("1")){%>
							<td><%=cinnerlist.get(12)%></td>
							<%}else if(af!=null && af.equals("3")){%>
							<td><%=cinnerlist.get(12)%></td>
							<%}else if(af!=null && af.equals("4")){%>
							<td><%=uF.showData(hmEducation.get(""+cinnerlist.get(0)),"") %></td>
							<%} %>
						</s:if>
	
						<td><%=cinnerlist.get(11)%></td>
					</tr>
					<%
							}
						%>
				</tbody>
			</table>
		</s:form>
	<%-- </section>
</div> --%>
	
	</s:if>
<s:elseif test="page=='Pending'">
<table id="lt" class="display" style="width:100%">
			<thead>
				<tr>
					<th style="text-align: left;">First Name</th>
					<th style="text-align: left;">Middle Name</th>
					<th style="text-align: left;">Last Name</th>
					<th style="text-align: left;">Email Id</th>
					<th style="text-align: left;">Mobile No</th>
					<th style="text-align: left;">Facts</th>
					<!-- <th style="text-align: left;">Delete</th> -->
				</tr>
			</thead>

			<tbody>
				<%
					java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
				 	for (int i = 0; i < couterlist.size(); i++) {
				 	java.util.List cinnerlist = (java.util.List) couterlist.get(i);
				 %>

				<tr id=<%=cinnerlist.get(0)%>>

					<td><%=cinnerlist.get(1)%></td>
					<td><%=cinnerlist.get(2)%></td>
					<td><%=cinnerlist.get(3)%></td>
					<td><%=cinnerlist.get(4)%></td>
					<td><%=cinnerlist.get(5)%></td>
					<td><%=cinnerlist.get(6)%></td>

				</tr>
				<% } %>
			</tbody>
		</table>

</s:elseif>
	<s:else>

		<table id="lt" class="display" style="width:100%">
			<thead>
				<tr>
					<th style="text-align: left;">First Name</th>
					<th style="text-align: left;">Middle Name</th>
					<th style="text-align: left;">Last Name</th>
					<th style="text-align: left;">Email Id</th>
					<th style="text-align: left;">Mobile No</th>
					<th style="text-align: left;">Facts</th>
					<th style="text-align: left;">Rejoin</th>
				</tr>
			</thead>

			<tbody>
				<%
					java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
				 	for (int i = 0; i < couterlist.size(); i++) {
				 	java.util.List cinnerlist = (java.util.List) couterlist.get(i);
				 %>

				<tr id=<%=cinnerlist.get(0)%>>

					<td><%=cinnerlist.get(1)%></td>
					<td><%=cinnerlist.get(2)%></td>
					<td><%=cinnerlist.get(3)%></td>
					<td><%=cinnerlist.get(4)%></td>
					<td><%=cinnerlist.get(5)%></td>   
					<td><%=cinnerlist.get(6)%></td>
					<td><a href="javascript:void(0)" onclick="(confirm('Are you sure you want to rejoin <%=cinnerlist.get(1)%> <%=cinnerlist.get(3)%>?') ? window.location='ReJoinEmployee.action?strEmpId=<%=cinnerlist.get(0)%>' : '')">Rejoin</a></td>
				</tr>
				<% } %>
			</tbody>
		</table>
		
	
	</s:else>



</div>
