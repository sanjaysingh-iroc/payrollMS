<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%> 
<script type="text/javascript" charset="utf-8">
<%-- $(document).ready( function () {
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
	}); 
});
 --%>
 

 $(document).ready( function () {
 
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
	
	"aoColumns": [ 
					null,
					null,
					null,
					null,
					{
						indicator: 'Saving...',
                      	tooltip: 'Click to edit ',
                      	submit:'Save',
                  		sUpdateURL: "ResignationReport.action?operation=U"
					},
					null,
					null,
					null,
					null
					/* {
						tooltip: 'Click to View/Edit ACL'
					} */
				]
	
	});
 });

 
 function updateApproveDenyStatus(status, approveType, offBoardId) {
		
	 var denyApprove = "Approve";
	 if(status == '-1') {
		 denyApprove = "Deny";
	 }
	 removeLoadingDiv('the_div');
		
		var dialogEdit = '#approveDenyDiv';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog(
		{
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 250,
			width : 400,
			modal : true,
			title : 'Resignation '+ denyApprove + ' Reason',
			open : function() {
				var xhr = $.ajax({
					url : "UpdateRequest.action?S=" + status + "&M=" + approveType + "&RID=" + offBoardId + "&T=REG",
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
 
 
 function exitFeedbackFormsDashboard() {
		
	 removeLoadingDiv('the_div');
		var dialogEdit = '#exitfeedbackFormDiv';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog(
		{
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 500,
			width : 700,
			modal : true,
			title : 'Exit Feedback Forms',
			open : function() {
				var xhr = $.ajax({
					url : "ExitFeedbackForms.action",
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

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#strWLocation").multiselect();
	$("#department").multiselect();
});    
</script>

<!-- Custom form for adding new records -->

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TResignationReport %>" name="title"/>
</jsp:include>

    <div id="printDiv" class="leftbox reportWidth">
    
    
    <s:form name="frm_Resignation" action="ResignationReport" theme="simple">
    
	<div class="filter_div">
    <div class="filter_caption">Filter</div>			
			<div style="float: left; margin-left:10px; width: 215px;">
				<p style="padding-left: 5px;">Organisation</p>	
				<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
						cssStyle="float:left;margin-right: 10px;" listValue="orgName"
						onchange="document.frm_Resignation.submit();"
						list="organisationList" key="" /> <!-- headerKey="" headerValue="All Organisations" -->
				<%-- <s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left; margin-right: 10px;" listValue="orgName" headerKey="" 
					headerValue="All Organisations" list="organisationList" key="" /> --%>
			</div>
			<div style="float: left; margin-left: 10px; width: 215px;">
				<p style="padding-left: 5px;">Location</p>
				<s:select theme="simple" name="strWLocation" id="strWLocation" listKey="wLocationId"
                     listValue="wLocationName" multiple="true" list="wLocationList" key=""/>
             </div>
             
             <div style="float: left; margin-left:10px; width: 215px;">
				<p style="padding-left: 5px;">Department</p>
	             <s:select name="department" id="department" list="departmentList" listKey="deptId" 
	             			listValue="deptName" multiple="true"/>
            </div>
            
             <div style="float: left; margin-left:10px; width: 215px;">
				<p style="padding-left: 5px;">&nbsp;</p>  
	        	 <s:submit value="Submit" cssClass="input_button"  cssStyle="margin:0px"/>
	         </div>  
	         
	         <div style="float: right; margin-right: 10px;">
	         <a href="javascript:void(0);" onclick="exitFeedbackFormsDashboard();">Exit Feedback Forms</a>
	         </div>
	</div>	                      
	                            
	</s:form>
    
        <table class="display" id="lt">
			<thead>
				<tr>
					<th>Teacher Name</th>
					<th>Status</th>
					<th>Applied on</th>
					<th>Reason</th>
					<th>Notice Days</th>
					<th>Last Day</th>
					<th>Manager's Action</th>
					<th>HR Manager's Action</th>
					<th>Full & Final</th>
				</tr>
			</thead>
			<tbody>
			<% 
			java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 <% for (int i=0; i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id="<%= cinnerlist.get(0) %>">
					<td><%= cinnerlist.get(1) %></td>
					<td align="center"><%= cinnerlist.get(2) %></td>
					<td align="center"><%= cinnerlist.get(3) %></td>
					<td><%= cinnerlist.get(4) %></td>
					<td align="center"><%= cinnerlist.get(5) %></td>
					<td align="center"><%= cinnerlist.get(6) %></td>
					<td align="center"><%= cinnerlist.get(7) %></td>
					<td align="center"><%= cinnerlist.get(8) %></td>
					<td align="center"><%= cinnerlist.get(9) %></td>
				</tr>
				<% } %>
			</tbody>
		</table> 



<jsp:include page="../common/Legends.jsp" />


</div>
   
<div id="approveDenyDiv"></div>
<div id="exitfeedbackFormDiv"></div>