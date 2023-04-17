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
			
			
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="	Employee Database" name="title"/>
</jsp:include>

    <div id="printDiv" class="leftbox reportWidth">
    
<%-- 
<s:form name="frmEmployeeHours" action="EmployeeReport" theme="simple">
<div class="filter_div">

<div class="filter_caption">Filter</div>	
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
     
    <s:select theme="simple" name="f_status" listKey="statusId" headerValue="All Working"  cssStyle="float:left;margin-right: 10px;"
                        listValue="statusName" headerKey="0" 
                        list="empStatusList" key="" required="true" /> 
    
    <s:submit value="Submit" cssClass="input_button"  cssStyle="margin:0px"/>

    </div>
</s:form> 

    
    
 --%>

    
    
    
    
    
    
<!-- Place holder where add and delete buttons will be generated -->
<!-- <div class="add_delete_toolbar"></div> -->
     
     
    
    <s:property value="message"/>
    
   
    	<table class="display" id="lt">
    	
				<thead>
					<tr> 
						<th style="text-align: left;">First Name</th>
						<th style="text-align: left;">Resign/Termination</th>
						<th style="text-align: left;">Resign Date</th>
						<th style="text-align: left;">Last Working Date</th>
						<th style="text-align: left;">Department</th>
						<th style="text-align: left;">Work Location</th>
						<th style="text-align: left;">Designation</th>
						<th style="text-align: left;">Resignation Accepted By</th>
						<th style="text-align: left;">Action</th>

					</tr>
				</thead>
				
				<tbody>
				<% java.util.Map<String,Map<String,String>> couterlist = (java.util.Map<String,Map<String,String>>)request.getAttribute("empDetailsMp"); 
				
						Set ketSet=couterlist.keySet();
						Iterator it=ketSet.iterator();
						while(it.hasNext()){
							String key=(String)it.next();
							
							Map<String,String> empMap=couterlist.get(key);%>
							<tr id = <%= empMap.get("EMP_ID") %> >
					
						<td><%= empMap.get("EMP_FNAME") %></td>
						
						<td><%= empMap.get("OFF_BOARD_TYPE") %></td>
						<td><%= empMap.get("ENTRY_DATE") %></td>
						<td><%= empMap.get("NOTICE_DAYS") %></td>
						<td><%= empMap.get("DEPART_NAME") %></td>
						<td><%= empMap.get("WLOCATION_NAME") %></td>
						<td><%= empMap.get("DESIGNATION_NAME") %></td>
						<td><%= empMap.get("ACCEPTED_BY") %></td>
						<td><a href="ExitForm.action?id=<%=empMap.get("EMP_ID") %>&resignId=<%=empMap.get("OFF_BOARD_ID") %>" class="del"></a></td>
						
					</tr>
					<%	}
				%>
				
				</tbody>
				
				
			</table> 
   
    
		   
			<%-- <%
				if (request.getAttribute("empPayRates") != null) {
					out.println(request.getAttribute("empPayRates"));
				}
			%> --%>

    </div>
