

<%@ page import="com.konnect.jpms.util.IConstants" %>
<%@ page import="com.konnect.jpms.util.UtilityFunctions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		String strEmpId = (String)session.getAttribute(IConstants.EMPID);
		
		Map hmProjectInvoiceDetails = (Map)request.getAttribute("hmProjectInvoiceDetails");
		List<List<String>> alGanntChart = (List<List<String>>)request.getAttribute("alGanntChart");
		Map<String, List<List<String>>> hmSubTaskGanntChart = (Map<String, List<List<String>>>)request.getAttribute("hmSubTaskGanntChart");
		String strProID = (String)request.getAttribute("strProID");
		String proType = (String)request.getAttribute("proType");
 
		String pageType = (String)request.getAttribute("pageType");
	%>


<div style="float:left; margin: 5px;"> <!-- class="leftbox reportWidth" -->
	<h3>Project Summary</h3>

	<div style="margin: 10px 0px;float:left;width:100%">
		<div style="float:left; padding:0px 10px; margin-right:10px"> <!-- width:30%; -->
		<% List proreportList = (List) request.getAttribute("proreportList"); %>
		<div><strong>Project Owner: </strong> <%=proreportList.get(8) %> </div>
		<div style="float:left;">
		<h4>Project Details</h4>
			<table class="table table-bordered">
				<tr><th style="width:40%">Project Name</th><td><%=proreportList.get(0)%></td></tr>
				<tr><th>Description</th><td><%=proreportList.get(2)%></td></tr>
				<tr><th>Service</th><td><%=proreportList.get(1)%></td></tr>
				<tr><th>Team Size</th><td><%=proreportList.get(3)%></td></tr>
				<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
					<tr><th>Estimated Time</th><td><%=proreportList.get(4)%></td></tr>
				<% } %>
				<tr><th>Project Deadline</th><td><%=proreportList.get(6)%></td></tr>
				<tr><th>Billing Frequency</th><td><%=proreportList.get(7)%></td></tr>
				<%-- <tr><th>Actual Time</th><td><%=proreportList.get(6)%></td></tr> --%>
			</table>	
			</div>
		</div>	
			
			<% 	List alClientDetails =(List) request.getAttribute("alClientDetails"); 
				if(alClientDetails != null && alClientDetails.size() > 5) {
			%>
				
				<div style="float:left; margin-right:10px;"><!-- width:30%; -->
					<h4>Client Details</h4>
					<table class="table table-bordered"> <!-- style="width:90%;" -->
						<tr><th style="width:40%">Client Name</th><td><%=alClientDetails.get(0)%></td></tr>
						<tr><th style="width:40%">Contact Name</th><td><%=alClientDetails.get(1)%></td></tr>
						<tr><th>Designation</th><td><%=alClientDetails.get(2)%></td></tr>
						<tr><th>Department</th><td><%=alClientDetails.get(3)%></td></tr>
						<tr><th>Contact No</th><td><%=alClientDetails.get(4)%></td></tr>
						<tr><th>Email</th><td><%=alClientDetails.get(5)%></td></tr> 
					</table>
				</div>
 			<%} %>
		
		<% 	List<List<String>> alTeamActivities = (List<List<String>>)request.getAttribute("alTeamActivities");
			Map<String, List<List<String>>> hmTeamSubTask = (Map<String, List<List<String>>>)request.getAttribute("hmTeamSubTask");
		%>
		<div style="float:left; margin:0px 0px 0px 10px;"> <!-- width:30%;  --> 
		<h4>Resource Details</h4>
		<table class="table table-bordered">
		
		<% if(alTeamActivities!=null && !alTeamActivities.isEmpty()) { %>
		   <tr>
		       <th>Resource</th>
		       <th>Task</th>
		       <th>Deadline</th>
		       <th>Completion Status</th>
		   </tr>
		
			<% for(int i=0; i<alTeamActivities.size(); i++) {
				List<String> innerList = alTeamActivities.get(i);
			%>
				<tr>
			         <td style="width:30%"><%= innerList.get(1)%> </td>
					 <td><%= innerList.get(2)%></td>
					 <td><%= innerList.get(3)%></td>
					 <td><%= innerList.get(4)%></td>
				</tr>
				
				<% if(hmTeamSubTask != null) { 
				List<List<String>> subTaskList = hmTeamSubTask.get(innerList.get(0));
				for(int j=0; subTaskList != null && j<subTaskList.size(); j++) {
					List<String> sunInnerList = subTaskList.get(j);
				%>
					<tr>
				         <td style="width:30%"><%= sunInnerList.get(1)%> </td>
						 <td><%= sunInnerList.get(2)%> [ST]</td>
						 <td><%= sunInnerList.get(3)%></td>
						 <td><%= sunInnerList.get(4)%></td>
					</tr>
				<% } } %>
			<% } %>
			
			<% } else { %>
			<tr><td colspan="4"> <div class="nodata msg" style="margin-left: 5px; width: 88%;"><span>No resource added</span></div></td></tr>
			<% } %>
		</table>
		</div>
	</div>
	
	<% if((proType ==null || proType.equals("null") || proType.equals("L")) && (pageType == null || !pageType.equals("MP") || (pageType.equals("MP") && uF.parseToInt((String)proreportList.get(9)) == uF.parseToInt(strEmpId))) && strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
		<%-- <div style="width: 97%; float: left; text-align: right;"> <a href="PreAddNewProject1.action?operation=E&pro_id=<%=strProID %>&step=0&pageType=<%=pageType %>"> go to edit Project Summary</a></div> --%>
	<% } %>
</div>

    







<%-- <%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<script>

function sendMail()
{
	var url='SendProjectReport.action?pro_id='+<%=request.getParameter("pro_id")%>;
	window.location = url;
}
function generateSummaryReport()
{
	var url='GenerateSummayPdfReport.action?operation=pdfDwld&pro_id='+<%=request.getParameter("pro_id")%>;
	
	var url='GenerateProjectInvoice.action?operation=pdfDwld&pro_id='+<%=request.getParameter("pro_id")%>;
	
	window.location = url;
}

</script>

<style>
.tb_style tr td {
	padding: 5px;
	border: solid 1px #c5c5c5; width:auto;
}

.tb_style tr th {
	padding: 5px;
	border: solid 1px #c5c5c5;
	background: #efefef; 
	width: auto;
}

.p tr td.head {background: #efefef;  }

.graphv_red {
	background: #f00;
	width: 100px;float:left;margin:0px 10px 0px 0px;
	height: 15px;line-height:15px;
	color: #fff; padding:4px;
}

.graphv_yellow {
	background: #ff0;
	width: 100px;float:left;margin:0px 10px 0px 0px;
	height: 15px; padding:4px;line-height:15px;
}

.graphv_blue {
	background: #00f;
	width: 300px;float:left;margin:0px 10px 0px 0px;
	height: 15px;
	color: #fff;padding:4px;line-height:15px;
}
</style>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		Map hmProjectInvoiceDetails = (Map)request.getAttribute("hmProjectInvoiceDetails");
		List<List<String>> alGanntChart = (List<List<String>>)request.getAttribute("alGanntChart");
		Map<String, List<List<String>>> hmSubTaskGanntChart = (Map<String, List<List<String>>>)request.getAttribute("hmSubTaskGanntChart");
		String strProID = (String)request.getAttribute("strProID");
	%>


	<script>
		var div = document.getElementById("GanttChartDIV<%=strProID%>");
		var innerhtml = div.innerHTML;
		if(innerhtml!='') {
			div.parentNode.removeChild(div);	
		}
	</script>


<div class="leftbox reportWidth">
	<div class="pagetitle"  align="center" style="margin: 0px 0px 10px 0px; float:none; padding:10px 0px; border-bottom:solid 1px #ccc">Project Summary</div>

	<div class="clr"></div>
	<div style="margin: 10px 0px;float:left;width:100%">
		
		<div style="float:left; padding:0px 10px; width:30%;margin-right:10px">
		<%
		List proreportList =(List) request.getAttribute("proreportList"); 
		if(proreportList.size()>10) {
		%>
		
		<div style="float:left;width:80%">
		<div class="title"><h3>Project Details</h3></div>
		<table class="tb_style">
					<%
							
					%>
					<tr><th>Project Name</th><td><%=proreportList.get(0)%></td></tr>
					<tr><th>Service</th><td><%=proreportList.get(1)%></td></tr>
					<tr><th>Description</th><td><%=proreportList.get(2)%></td></tr>
					<tr><th>Team Size</th><td><%=proreportList.get(4)%></td></tr>
					<tr><th>Estimated Time</th><td><%=proreportList.get(5)%></td></tr>
					<tr><th>Actual Time</th><td><%=proreportList.get(6)%></td></tr>
					<tr><th>Project Deadline</th><td><%=proreportList.get(7)%></td></tr>
			</table>	
			</div>
			<% } %>
			
		</div>	
			
			<%
				List alClientDetails =(List) request.getAttribute("alClientDetails"); 
				if(alClientDetails != null && alClientDetails.size() > 5) {
			%>
				
				<div style="float:left;width:30%;margin-right:10px">
				<div class="title"><h3>Client Details</h3></div>
				<table class="tb_style" style="width:90%">
					<tr><th style="width:40%">Client Name</th><td><%=alClientDetails.get(0)%></td></tr>
					<tr><th style="width:40%">Contact Name</th><td><%=alClientDetails.get(1)%></td></tr>
					<tr><th>Designation</th><td><%=alClientDetails.get(2)%></td></tr>
					<tr><th>Department</th><td><%=alClientDetails.get(3)%></td></tr>
					<tr><th>Contact No</th><td><%=alClientDetails.get(4)%></td></tr>
					<tr><th>Email</th><td><%=alClientDetails.get(5)%></td></tr> 
				</table>	
				</div>
 			<%} %>
		
		<% 	List<List<String>> alTeamActivities = (List<List<String>>)request.getAttribute("alTeamActivities");
			Map<String, List<List<String>>> hmTeamSubTask = (Map<String, List<List<String>>>)request.getAttribute("hmTeamSubTask");
		%>
		<div style="float:left;margin:0px 0px 0px 10px; width:30%; "> 
		<div class="title"><h3>Resource Details</h3></div>
		<table cellpadding="0" cellspacing="0" class="tb_style" width="100%">
		
		   <tr>
		       <th>Resource</th>
		       <th>Task</th>
		       <th>Deadline</th>
		       <th>Completion Status</th>
		   </tr>	
		
			<% for(int i=0; alTeamActivities!=null && i<alTeamActivities.size(); i++) { 
				List<String> innerList = alTeamActivities.get(i);
			%>
				<tr>
			         <td style="width:30%"><%= innerList.get(1)%> </td>
					 <td><%= innerList.get(2)%></td>
					 <td><%= innerList.get(3)%></td>
					 <td><%= innerList.get(4)%></td>
				</tr>
				
				<% if(hmTeamSubTask != null) { 
				List<List<String>> subTaskList = hmTeamSubTask.get(innerList.get(0));
				for(int j=0; subTaskList != null && j<subTaskList.size(); j++) {
					List<String> sunInnerList = subTaskList.get(j);
				%>
					<tr>
				         <td style="width:30%"><%= sunInnerList.get(1)%> </td>
						 <td><%= sunInnerList.get(2)%> [ST]</td>
						 <td><%= sunInnerList.get(3)%></td>
						 <td><%= sunInnerList.get(4)%></td>
					</tr>
				<% } } %>
			<% } %>
		</table>
		
		</div>
		
<div class="clr"></div>
		

		
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/gantt/style.css" type="text/css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/gantt/jsgantt.css"/>
<script language="javascript" src="<%=request.getContextPath() %>/js/gantt/jsgantt_1.js"></script>
	
    	
<script>

  // here's all the html code neccessary to display the chart object

  // Future idea would be to allow XML file name to be passed in and chart tasks built from file.

  var g = new JSGantt.GanttChart('g',document.getElementById('GanttChartDIV<%=strProID%>'), 'day');

	g.setShowRes(1); // Show/Hide Responsible (0/1)
	g.setShowDur(0); // Show/Hide Duration (0/1)
	g.setShowComp(0); // Show/Hide % Complete(0/1)
   	g.setCaptionType('None');  // Set to Show Caption (None,Caption,Resource,Duration,Complete)



	g.setShowStartDate(0); // Show/Hide Start Date(0/1)
	g.setShowEndDate(0); // Show/Hide End Date(0/1)
	g.setDateInputFormat('dd/mm/yyyy')  // Set format of input dates ('mm/dd/yyyy', 'dd/mm/yyyy', 'yyyy-mm-dd')
	g.setDateDisplayFormat('dd/mm/yyyy') // Set format to display dates ('mm/dd/yyyy', 'dd/mm/yyyy', 'yyyy-mm-dd')
	g.setFormatArr("day","week","month","quarter") // Set format options (up to 4 : "minute","hour","day","week","month","quarter")


  //var gr = new Graphics();

  if( g ) {

    // Parameters             (pID, pName,                  pStart,      pEnd,    pDeadline,    pColor,   pLink,          pMile, pRes,  pComp, pGroup, pParent, pOpen, pDepend, pCaption)
	
	/* g.AddTaskItem(new JSGantt.TaskItem(1,   'Define Chart API',     '01/05/2012', '23/05/2012', '23/05/2012', 'ff0000', 'http://help.com', 0, 'Brian',     1, 1, 0, 1));
	g.AddTaskItem(new JSGantt.TaskItem(2,   'Define Chart API',     '05/05/2012', '26/05/2012', '22/05/2012', 'ff0000', 'http://help.com', 0, 'Brian',     10, 0, 1, 1));
	g.AddTaskItem(new JSGantt.TaskItem(3,   'Define Chart API',     '05/05/2012', '26/05/2012', '12/05/2012', 'ff0000', 'http://help.com', 0, 'Brian',     10, 0, 1, 1));
	g.AddTaskItem(new JSGantt.TaskItem(4,   'Define Chart API',     '15/05/2012', '30/05/2012', '18/05/2012', 'ff0000', 'http://help.com', 0, 'Brian',     0, 0, 1, 1));
 */
	
	<%
	
	
	if(alGanntChart==null)alGanntChart=new ArrayList<List<String>>();
	
	for(int i=0; i<alGanntChart.size(); i++) {
		List<String> alInner = alGanntChart.get(i);
		if(alInner == null)continue;
		
		%>
		g.AddTaskItem(new JSGantt.TaskItem(<%=alInner.get(0)%>, '<%=alInner.get(1)%>', '<%=alInner.get(2)%>', '<%=alInner.get(3)%>', '<%=alInner.get(4)%>', '<%=alInner.get(5)%>', '<%=alInner.get(6)%>', <%=alInner.get(7)%>, '<%=alInner.get(8)%>', <%=alInner.get(9)%>, <%=alInner.get(10)%>, <%=alInner.get(11)%>, <%=alInner.get(12)%>, <%=alInner.get(13)%>, '<%=alInner.get(14)%>'));
	
		<% if(hmSubTaskGanntChart != null) {
			List<List<String>> alSubTaskGC = hmSubTaskGanntChart.get(alInner.get(0));
			for(int j=0; alSubTaskGC != null && j<alSubTaskGC.size(); j++) {
				List<String> innerList = alSubTaskGC.get(j);
				if(innerList==null)continue;		
		%>
		g.AddTaskItem(new JSGantt.TaskItem(<%=innerList.get(0)%>, '<%=innerList.get(1)%>', '<%=innerList.get(2)%>', '<%=innerList.get(3)%>', '<%=innerList.get(4)%>', '<%=innerList.get(5)%>', '<%=innerList.get(6)%>', <%=innerList.get(7)%>, '<%=innerList.get(8)%>', <%=innerList.get(9)%>, <%=innerList.get(10)%>, <%=innerList.get(11)%>, <%=innerList.get(12)%>, <%=innerList.get(13)%>, '<%=innerList.get(14)%>'));
		<% } } %>
	<% } %>
	
	
    g.Draw();	
    g.DrawDependencies();

  }

  else

  {

    alert("not defined");

  }

</script>

 	
    	<div style="float:left; margin:20px 0px;max-width:98%">
    		<div class="title"><h3>Gantt Chart</h3></div>
    		
    		<%
				if (alGanntChart != null && alGanntChart.size() == 0) {
			%>
			<div class="msg nodata"><span>No task is assigned in this project.</span></div>
			<%
				} else {
			%>
        	<div style="position:relative;max-width:98%;overflow-y: hidden;float:left; position: relative;" class="gantt" id="GanttChartDIV<%=strProID%>"></div>
        	<% } %>
    	</div>
    	
	</div>
	
	
	
	<!-- <div>
		<table class="tb_style"  style="float:left;width:50%">
			<tr><th>Report status</th></tr>
			<tr><td>No report sent till date</td></tr>
		</table>
	
	</div> -->
	
	<%List empNameList = (List)request.getAttribute("empNameList"); 
	 List taskdateList = (List)request.getAttribute("taskdateList"); 
	 List linkList = (List)request.getAttribute("linkList"); 
	%>
	<div>
	<div id="myDiv"><a href="javascript:void();" onclick="getContent('myDiv','SendProjectReport.action?pro_id=<%=request.getParameter("pro_id")%>')">Click here to send a report to the client</a></div>
<!-- 	<div><a href="javascript:void();" onclick="sendMail();">Click here to send a report to the client</a></div> -->
		<table class="tb_style"  style="float:left;width:50%">
			<tr><th>Report status</th></tr>
			
	<%
	int i=0;
	for(i=0; taskdateList!=null && i<taskdateList.size(); i++){
		
	
	%>
	<tr><td><%=empNameList.get(i) %>&nbsp;sent report on <%=taskdateList.get(i) %> <%=linkList.get(i) %> <a onclick="generateSummaryReport();" href="javascript:void(0)" class="pdf" >Pdf </a></td></tr>
	
	<%	}	%>
	
	<%
	if(i==0){
		%>
		<tr><td><div class="msg nodata" style="width:96%"><span>No report sent till date</span></div></td></tr>
		<%
	}
	%>
		</table>
	

	
<%

if(alClientDetails!=null && alClientDetails.size()>4){
	
%>
	
<h3>Invoice Summary</h3>	
<table class="tb_style" style="float: left;width:75%;margin-top:50px">
<tr>
<th>Client Name</th>
<th>Project Type</th>
<th>Billing Type</th>
<th>Billing Summary</th>
<th>&nbsp;</th>
</tr>

<tr>
<td valign="top"><%=(String)alClientDetails.get(0)%> </td>
<td valign="top"></td>
<td valign="top"><%=uF.showData((String)hmProjectInvoiceDetails.get("BILLING_TYPE"), "")%></td>
<td valign="top">
Contract Amount: <%=uF.showData((String)hmProjectInvoiceDetails.get("BILLING_AMOUNT"), "0")%><br>
Paid Amount: <%=uF.showData((String)hmProjectInvoiceDetails.get("PAID_AMOUNT"), "0")%><br>
Pending Amount: <%=uF.showData((String)hmProjectInvoiceDetails.get("BILLING_AMOUNT"), "0")%><br>
</td>


<%if(hmProjectInvoiceDetails.get("INVOICE_DATE")!=null){ %>
<td valign="bottom" align="center"><%=uF.showData((String)hmProjectInvoiceDetails.get("COMPLETED"), "")%><br/><a href="GenerateProjectInvoice.action?operation=pdfDwld&amp;pro_id=strProID%>&invoice_id=<%=(String)hmProjectInvoiceDetails.get("INVOICE_ID")%>">Invoice Generated</a><br/> on<br/> <%=(String)hmProjectInvoiceDetails.get("INVOICE_DATE")%><br/>Inv. No.: <%=uF.showData((String)hmProjectInvoiceDetails.get("INVOICE_NUMBER"), "")%></td>
<%}else{ %>
<td valign="bottom"><div id="myDiv_1"><a onclick="showAjaxLoading('myDiv_1'); getContent('myDiv_1', 'GenerateInvoice.action?strProjectId=<%=strProID%>')" href="javascript:void(0)">Raise Invoice</a></div></td>
<%} %>

</tr>
</table>
	
<%} %>
	
	
	
	</div> 
	
	
</div>
<div id="editproject"></div>
<div id="view"></div>
<div id="addproject"></div>





		
    




 --%>