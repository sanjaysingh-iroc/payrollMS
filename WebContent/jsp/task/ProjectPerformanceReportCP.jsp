<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<script>
    $(function() {
        $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
        $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    });
    
    jQuery(document).ready(function() {

    	jQuery(".content1").hide();
    	//toggle the componenet with class msg_body
    	jQuery(".heading_dash").click(function() {
    		jQuery(this).next(".content1").slideToggle(500);
    		$(this).toggleClass("filter_close");
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
	$("#f_project_service").multiselect();
	$("#f_client").multiselect();
});    
</script>

<%CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); %>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Project Performance Analytics" name="title"/>
</jsp:include>

 <script>
 function viewSummary(id) {

		var dialogEdit = '#viewsummary';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 1120,
			width : 1000,
			modal : true,
			title : 'Project Summary',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectSummaryView.action?pro_id="+id,
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
 
 function viewBudgetedSummary(id) {
		var dialogEdit = '#viewbudgetedsummary';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 520,
			width : 800,
			modal : true,
			title : 'Project Budgeted Summary',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectBudgetedSummary.action?pro_id="+id,
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
 
 
 function checkSelectType(value) {
		//fromToDIV financialYearDIV monthDIV paycycleDIV
		if(value == '1') {
			document.getElementById("fromToDIV").style.display = 'block';
			document.getElementById("financialYearDIV").style.display = 'none';
			document.getElementById("monthDIV").style.display = 'none';
			document.getElementById("paycycleDIV").style.display = 'none';
		} else if(value == '2') {
			document.getElementById("fromToDIV").style.display = 'none';
			document.getElementById("financialYearDIV").style.display = 'block';
			document.getElementById("monthDIV").style.display = 'none';
			document.getElementById("paycycleDIV").style.display = 'none';
		} else if(value == '3') {
			document.getElementById("fromToDIV").style.display = 'none';
			document.getElementById("financialYearDIV").style.display = 'none';
			document.getElementById("monthDIV").style.display = 'block';
			document.getElementById("paycycleDIV").style.display = 'none';
		} else if(value == '4') {
			document.getElementById("fromToDIV").style.display = 'none';
			document.getElementById("financialYearDIV").style.display = 'none';
			document.getElementById("monthDIV").style.display = 'none';
			document.getElementById("paycycleDIV").style.display = 'block';
		}
	}
 
 </script>
 
<div class="leftbox reportWidth">

 
<%
String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
String strQuery = (String)request.getAttribute("javax.servlet.forward.query_string");

if(strAction!=null){
	strAction = strAction.replace(request.getContextPath()+"/","");
	
	if(strQuery!=null && strQuery.indexOf("NN")>=0){
		strAction = strAction+"?"+strQuery;
	}
}

%>
<div style="margin-bottom: 20px">
  <a class="<%=((strAction.equalsIgnoreCase("ProjectPerformanceReportWP.action")?"current":"next")) %>" href="ProjectPerformanceReportWP.action">Working Projects</a> |      
  <a class="<%=((strAction.equalsIgnoreCase("ProjectPerformanceReportCP.action")?"current":"next")) %>" href="ProjectPerformanceReportCP.action">Completed Projects</a>
</div>

<%-- <display:table name="alOuter" cellspacing="1" class="tb_style" export="true"
	pagesize="50" id="lt1" requestURI="ProjectPerformanceReportWP.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="ProjectPerformance.xls" />
	<display:setProperty name="export.xml.filename" value="ProjectPerformance.xml" />
	<display:setProperty name="export.csv.filename" value="ProjectPerformance.csv" />
	
	<display:column nowrap="nowrap" title="Project Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
	<display:column nowrap="nowrap" title="Partner/Manager"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
	<display:column title="Budgeted Cost" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>	
	<display:column nowrap="nowrap" title="Actual Cost" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
	<display:column nowrap="nowrap" title="Billable Amount" styleClass="alignRight padRight20" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
	<display:column nowrap="nowrap" title="Percentage" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
	<display:column nowrap="nowrap" title="P" align="center"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
	<display:column nowrap="nowrap" title="" align="center">&nbsp;</display:column>
	<display:column nowrap="nowrap" title="Estimated Time" align="center"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
	<display:column nowrap="nowrap" title="Actual Time" align="center"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>
	<display:column nowrap="nowrap" title="Deadline" align="center"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>
</display:table> --%>


	<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
		<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
			<%=(String)request.getAttribute("selectedFilter") %>
		</p>
		<div class="content1" style="height: 170px;">
		
    <s:form name="frmProjectPerformanceReportCP" action="ProjectPerformanceReportCP" theme="simple">
				<div style="float: left; width: 100%; margin-top: -5px;">
						<div style="float: left; margin-top: 10px;">
							<i class="fa fa-filter"></i>
						</div>
						
						<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Organisation</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left; margin-right: 10px;" listValue="orgName" 
								onchange="document.frmProjectPerformanceReportCP.submit();" list="organisationList" />
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Location</p>
							<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
								listValue="wLocationName" list="wLocationList" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Department</p>
							<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" cssStyle="float:left; margin-right: 10px;" 
								listValue="deptName" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">SBU</p>
							<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
								listValue="serviceName" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Level</p>
							<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" cssStyle="float:left;margin-right: 10px;"
								listValue="levelCodeName" list="levelList" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 35px; width: 215px;">
							<p style="padding-left: 5px;">Service</p>
							<s:select name="f_project_service" id="f_project_service" list="projectServiceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
								listValue="serviceName" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Client</p>
							<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" cssStyle="float:left;margin-right: 10px;" list="clientList" 
								key="" multiple="true" />
						</div>
				</div>
				
				<div style="float: left; width: 100%;">
						<div style="float: left; margin-top: 10px;">
							<i class="fa fa-calendar"></i>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Select Period</p>
							<s:select theme="simple" name="selectOne" id="selectOne" cssStyle="float:left; margin-right: 10px;" headerKey="" 
								headerValue="Select Period" list="#{'1':'From-To', '2':'Financial Year', '3':'Month', '4':'Paycycle'}" onchange="checkSelectType(this.value);"/> <!--   -->
						</div>
						
						<div id="fromToDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">&nbsp;</p>
							<s:textfield name="strStartDate" id="strStartDate" value="From Date" onblur="fillField(this.id, 3);" onclick="clearField(this.id);" cssStyle="width:65px"></s:textfield>
				      		<s:textfield name="strEndDate"  id="strEndDate" value="To Date" onblur="fillField(this.id, 4);" onclick="clearField(this.id);" cssStyle="width:65px"></s:textfield>
			      		</div>
			      		
			      		<div id="financialYearDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select label="Select PayCycle" name="financialYear" listKey="financialYearId" listValue="financialYearName"
								headerValue="Select Financial Year" list="financialYearList" />
			      		</div>
			      		
			      		<div id="monthDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 325px;">
							<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
							<s:select label="Select PayCycle" name="financialYear" listKey="financialYearId" listValue="financialYearName"
								headerValue="Select Financial Year" list="financialYearList" /> 
							<s:select name="strMonth" cssStyle="margin-left: 7px; width: 100px;" listKey="monthId" listValue="monthName" list="monthList" />	
			      		</div>
			      		
			      		<div id="paycycleDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Paycycle</p>
							<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId" listValue="paycycleName"
								headerValue="Select Paycycle" list="paycycleList" />
			      		</div>
			      		
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">&nbsp;</p>
							<s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" />
						</div>
				</div>
		</s:form>
	
		</div>
	</div>
	

<%-- <s:form action="ProjectPerformanceReportCP" name="frmProPerformance" id="idProProPerformance" theme="simple">
<div class="filter_div">
	<div class="filter_caption">Filter</div>
	<s:textfield cssStyle="width:80px" name="f_start" id="f_start" onblur="fillField(this.id, 3);" onclick="clearField(this.id);"></s:textfield>
	<s:textfield cssStyle="width:80px" name="f_end" id="f_end" onblur="fillField(this.id, 4);" onclick="clearField(this.id);"></s:textfield>
	<input type="submit" class="input_button" style="margin:0" value="Search">
</div>
</s:form> --%>
<%-- 

	<table class="tb_style">
			
				<tr>
					<th width="20%">&nbsp;</th>
					<th width="10%">&nbsp;</th>
					<th width="10%">&nbsp;</th>
					<th width="40%" colspan="4">Money</th>
					<td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="30%" colspan="3">Time</th>
				</tr>
				
				<tr>
					<th width="20%">Project Name</th>
					<th width="10%">Manager/TL</th>
					<th width="10%">Budgeted<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<th width="10%">Actual Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th> 
					<th width="10%">Billed Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<th width="10%">Profit Margin<br/>(%)</th>
					<th width="10%">Indicator</th>
					<td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="10%">Estimated Time<br/>(hrs)</th>
					<th width="10%">Time Spent<br/>(hrs)</th>
					<th width="10%">Deadline</th>
				</tr>
				<%
				List<List<String>> alOuter1=(List<List<String>>)request.getAttribute("alOuter");
				for(int i=0;i<alOuter1.size();i++){
					List<String> alInner=alOuter1.get(i);
				%>
				<tr>
					<td <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>> <%=alInner.get(1) %></td>
					<td <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>> <%=alInner.get(2) %></td>
					<td align="center" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(3) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(4) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(5) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(6) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(7) %></td>
					<td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(8) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(9) %></td>
					<td align="center" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(10) %></td>
				</tr>
				<%} %>
				
				</table>
 --%>
 

<%
Map hmProPerformaceBillable = (Map)request.getAttribute("hmProPerformaceBillable"); 
Map hmProPerformaceActual = (Map)request.getAttribute("hmProPerformaceActual");
Map hmProPerformaceBudget = (Map)request.getAttribute("hmProPerformaceBudget");
Map hmProPerformaceActualTime = (Map)request.getAttribute("hmProPerformaceActualTime");
Map hmProPerformaceIdealTime = (Map)request.getAttribute("hmProPerformaceIdealTime");
Map hmProPerformaceProjectName = (Map)request.getAttribute("hmProPerformaceProjectName");
Map hmProPerformaceProjectManager = (Map)request.getAttribute("hmProPerformaceProjectManager");
Map hmProPerformaceProjectProfit = (Map)request.getAttribute("hmProPerformaceProjectProfit");
Map hmProPerformaceProjectAmountIndicator = (Map)request.getAttribute("hmProPerformaceProjectAmountIndicator");
Map hmProPerformaceProjectTimeIndicator = (Map)request.getAttribute("hmProPerformaceProjectTimeIndicator");
Map hmProjectClient = (Map)request.getAttribute("hmProjectClient");
List alProjectId = (List)request.getAttribute("alProjectId");

%>


	<table class="tb_style">
				<tr>
					<th width="20%" rowspan="2">Project Name</th>
					<th width="10%" rowspan="2">Manager/TL</th>
					<th width="10%" rowspan="2">Client</th>
					<!-- <th width="20%">&nbsp;</th>
					<th width="10%">&nbsp;</th>
					<th width="10%">&nbsp;</th> -->
					<th width="40%" colspan="5">Money</th>
					<td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="30%" colspan="3">Time</th>
				</tr>
				
				<tr>
					<th width="10%">Budgeted<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<th width="10%">Actual Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th> 
					<th width="10%">Billable Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<th width="10%">Profit Margin<br/>(%)</th>
					<th width="10%">Indicator</th>
					<td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="10%">Estimated Time<br/>(days/hrs)</th>
					<th width="10%">Time Spent<br/>(days/hrs)</th>
					<th width="10%">Deadline</th>
				</tr>
				<%
					for(int i=0; alProjectId != null && !alProjectId.isEmpty() && i<alProjectId.size(); i++){
				%>
				<tr>
					<td valign="top"><a href="javascript:void(0)" onclick="viewSummary(<%=(String)alProjectId.get(i)%>)"> <%=hmProPerformaceProjectName.get((String)alProjectId.get(i)) %></a></td>
					<td valign="top"> <%=hmProPerformaceProjectManager.get((String)alProjectId.get(i)) %></td>
					<td valign="top"> <%=hmProjectClient.get((String)alProjectId.get(i)) %></td>
					<td valign="top" class="alignRight padRight20"><a href="javascript:void(0)" onclick="viewBudgetedSummary(<%=(String)alProjectId.get(i)%>)"><%=hmProPerformaceBudget.get((String)alProjectId.get(i)) %></a></td>
					<td valign="top" class="alignRight padRight20"><%=hmProPerformaceActual.get((String)alProjectId.get(i)) %></td>
					<td valign="top" class="alignRight padRight20"><%=hmProPerformaceBillable.get((String)alProjectId.get(i)) %></td>
					<td valign="top" class="alignRight padRight20"><%=hmProPerformaceProjectProfit.get((String)alProjectId.get(i)) %></td>
					<td valign="top" class="alignRight padRight20"><%=hmProPerformaceProjectAmountIndicator.get((String)alProjectId.get(i)) %></td>
					<td valign="top" width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<td valign="top" class="alignRight padRight20" ><%=hmProPerformaceIdealTime.get((String)alProjectId.get(i)) %></td>
					<td valign="top" class="alignRight padRight20" ><%=hmProPerformaceActualTime.get((String)alProjectId.get(i)) %></td>
					<td valign="top" align="center"><%=hmProPerformaceProjectTimeIndicator.get((String)alProjectId.get(i)) %></td>
					
				</tr>
				<%} if(alProjectId!=null && alProjectId.size()==0){%>
					<tr>
						<td colspan="12">
							<div class="msg nodata"><span>No project available under for the current selection.</span></div>
						</td>
					</tr>
				<%} %>
				</table>
<br/><br/>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"><!-- <img src="images1/icons/denied.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Billable</div>
</div>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"> <!-- <img src="images1/icons/re_submit.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Budgeted and Actual &lt; Billable</div>
</div>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"><!-- <img src="images1/icons/approved.png" width="17">  --><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &lt; Budgeted</div>
</div>

<%-- <div>
	<jsp:include page="/jsp/task/ProjectPerformanceChart.jsp"></jsp:include>
</div> --%>

</div>


<div id="viewsummary"></div>
<div id="viewbudgetedsummary"></div>

