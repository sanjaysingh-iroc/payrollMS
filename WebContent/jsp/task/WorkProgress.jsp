<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*,ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<div id="divResult">

<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"> </script> --%>

<% 
String btnSubmit = (String) request.getAttribute("btnSubmit");
if(btnSubmit != null && !btnSubmit.equalsIgnoreCase("null") && !btnSubmit.equals("")) { %>
<%-- <script type="text/javascript" src="js/jquery.sparkline.js"></script> --%>
<% } %>

<script type="text/javascript">



<%-- <% if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) { %>
	$(function() {
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		var data = $("#frm_WorkProgress").serialize();
		$.ajax({
			type : 'POST',
			url: 'WorkProgress.action?btnSubmit=Submit',
			data: data,
			success: function(result){
	        	$("#divResult").html(result);
	        	if(document.getElementById("f_strWLocation")) {
		        	$("#f_strWLocation").multiselect().multiselectfilter();
		        	$("#f_department").multiselect().multiselectfilter();
		        	$("#f_service").multiselect().multiselectfilter();
	        	}
	   		}
		});
	});

<% } %> --%>


function loadMore(proPage, minLimit) {
	
	document.getElementById("proPage").value = proPage;
	document.getElementById("minLimit").value = minLimit;
	var data = $("#frm_WorkProgress").serialize();
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'WorkProgress.action?btnSubmit=Submit',
		data: data,
		success: function(result){
        	$("#divResult").html(result);
        	if(document.getElementById("f_strWLocation")) {
	        	$("#f_strWLocation").multiselect().multiselectfilter();
	        	$("#f_department").multiselect().multiselectfilter();
	        	$("#f_service").multiselect().multiselectfilter();
        	}
   		}
	});
	
}


function submitForm(type){
	document.getElementById("proPage").value = '';
	document.getElementById("minLimit").value = '';
	
	var data = "";
	if(type == '1') {
		var f_org = document.getElementById("f_org").value;
		var proType = document.getElementById("proType").value;
		var strProType = '';
		if(document.getElementById("strProType")) {
			strProType = document.getElementById("strProType").value;
		}
		data = 'f_org='+f_org+'&strProType='+strProType+'&proType='+proType;
	} else if(type == '2') {
		data = $("#frm_WorkProgress").serialize();
	} else {
		data = 'proType='+type;
	}
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'WorkProgress.action?btnSubmit=Submit',
		data: data,
		success: function(result){
        	$("#divResult").html(result);
        	if(document.getElementById("f_strWLocation")) {
	        	$("#f_strWLocation").multiselect().multiselectfilter();
	        	$("#f_department").multiselect().multiselectfilter();
	        	$("#f_service").multiselect().multiselectfilter();
        	}
   		}
	});
	
}


function viewBudgetedSummary(id) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Project Budgeted Summary');
	$.ajax({
		url : 'ProjectBudgetedSummary.action?pro_id='+id,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
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
 
<script type="text/javascript">
$(document).ready(function() {

	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_project_service").multiselect().multiselectfilter();
	$("#f_client").multiselect().multiselectfilter();
	
});

</script>
<%
 	UtilityFunctions uF = new UtilityFunctions();
 	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
 	
 	String proType = (String)request.getAttribute("proType");
 	String proCount = (String)request.getAttribute("proCount");
 	
 	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
 %>


<section class="content">
	<!-- title row -->
	<div class="row">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<div class="nav-tabs-custom">
				<ul class="nav nav-tabs">
					<li class="<%=((proType == null || proType.trim().equals("") || proType.trim().equals("null") || proType.trim().equalsIgnoreCase("P")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('P')" data-toggle="tab">Projects</a></li>
					<li class="<%=((proType != null && proType.equalsIgnoreCase("R")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('R')" data-toggle="tab">Resources</a></li>
				</ul>

			<div class="tab-content box-body">

	<%if(proType == null || proType.trim().equals("") || proType.trim().equals("null") || proType.trim().equalsIgnoreCase("P")) { %>
		<%
			Map<String, String> hmProject = (Map<String, String>) request.getAttribute("hmProject");
		 	if (hmProject == null)hmProject = new LinkedHashMap<String, String>();
		 	Map<String, String> hmCompleteTask = (Map<String, String>) request.getAttribute("hmCompleteTask");
		 	if (hmCompleteTask == null)hmCompleteTask = new HashMap<String, String>();
		 	Map<String, String> hmActiveTask = (Map<String, String>) request.getAttribute("hmActiveTask");
		 	if (hmActiveTask == null)hmActiveTask = new HashMap<String, String>();
		 	Map<String, String> hmOverdueTask = (Map<String, String>) request.getAttribute("hmOverdueTask");
		 	if (hmOverdueTask == null)hmOverdueTask = new HashMap<String, String>(); 
	 	%>
	
		
		<s:form name="frm_WorkProgress" id="frm_WorkProgress" action="WorkProgress" theme="simple">
			<s:hidden name="proPage" id="proPage"/>
    		<s:hidden name="minLimit" id="minLimit"/>
    		<s:hidden name="proType" id="proType"/>
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
				    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
				    <div class="box-tools pull-right">
				        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<%
							boolean poFlag = (Boolean) request.getAttribute("poFlag");
							if(poFlag && (strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER)))){
							%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project Type</p> 
									<s:select theme="simple" name="strProType" id="strProType" headerKey="1" headerValue="All Projects" list="#{'2':'My Projects'}" onchange="submitForm('1');"/>
								</div>
							<% } %>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Project</p>
								<s:select theme="simple" name="strProId" id="strProId" listKey="projectID" listValue="projectName"  headerKey="" headerValue="All Projects" list="projectdetailslist" key="" onchange="submitForm('1');"/>
							</div>
							
						</div>
					</div>
					
					<div class="row row_without_margin" style="margin-top: 10px;">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						
				      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Calendar Year</p>
								<s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" headerKey="0"  list="calendarYearList" key="" cssStyle="width:200px !important;"  onchange="submitForm('2');"/>
							</div>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Month</p>
								<s:select theme="simple" name="strMonth" listKey="monthId" cssStyle="width:110px !important;" listValue="monthName" list="monthList" key="" />
							</div>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="btnSubmit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
							
						</div>
					</div>
				</div>
			</div>
		</s:form>
				
		<div class="col-lg-6 col-md-6 col-sm-12">
			<table class="table table-bordered">
				<%
					if (hmProject != null && hmProject.size() > 0) {
				%>
					<tr>
						<th width="35%">Project Name</th>
						<th>Completed</th>
						<th>Active</th>
						<th>Overdue</th>
					</tr>
					
					<%
						Iterator<String> it = hmProject.keySet().iterator();
						while (it.hasNext()) {
							String strProId = it.next();
							String strProName = hmProject.get(strProId);
					%>
						<tr>
							<td><%=strProName%></td>
							<td style="text-align: center;"><span id="completedbar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmCompleteTask.get(strProId + "_COMPLETE_COUNT"),"0")%></span></td>
							<td style="text-align: center;"><span id="activebar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmActiveTask.get(strProId + "_ACTIVE_COUNT"), "0")%></span></td>
							<td style="text-align: center;"><span id="overduebar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmOverdueTask.get(strProId + "_OVERDUE_COUNT"), "0")%></span>
								<script type="text/javascript">
								    $(function() {
								    	 $('#completedbar<%=strProId%>').sparkline(new Array(<%=hmCompleteTask.get(strProId + "_COMPLETE")%>), {type: 'bar', barColor: '#9ACD32'} );
									     $('#activebar<%=strProId%>').sparkline(new Array(<%=hmActiveTask.get(strProId + "_ACTIVE")%>), {type: 'bar', barColor: '#4682B4'} );
									     $('#overduebar<%=strProId%>').sparkline(new Array(<%=hmOverdueTask.get(strProId + "_OVERDUE")%>), {type: 'bar', barColor: '#B22222'} );
								    });
							    </script>
							</td>
						</tr>
					
					<%}%>
				<%
					} else {
				%>
					<tr><td colspan="4"><div class="msg nodata" style="width: 94%;"><span>Projects not available.</span></div> </td></tr>
				<%
					}
				%>		
			</table>
			
			<div style="text-align: center; float: left; width: 100%;">
				<% int intproCnt = uF.parseToInt(proCount);
					int pageCnt = 0;
					int minLimit = 0;
					for(int i=1; i<=intproCnt; i++) {
						minLimit = pageCnt * 20;
						pageCnt++;
				%>
				<% if(i ==1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					if(uF.parseToInt(strPgCnt) > 1) {
						 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
						 strMinLimit = (uF.parseToInt(strMinLimit)-20) + "";
					}
					if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
				%>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
						<%="< Prev" %></a>
					<% } else { %>
						<b><%="< Prev" %></b>
					<% } %>
					</span>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
						<b>...</b>
					<% } %>
				
				<% } %>
				
				<% if(i > 1 && i < intproCnt) { %>
				<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
				<% } %>
				<% } %>
				
				<% if(i == intproCnt && intproCnt > 1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)+20) + "";
					 if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
					%>
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
						<b>...</b>
					<% } %>
				
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
					<% } else { %>
						<b><%="Next >" %></b>
					<% } %>
					</span>
				<% } %>
				<%} %>
			</div>
			
		</div>


		<div class="col-lg-6 col-md-6 col-sm-12">
			<div id="chartdiv" style="width:100%; height:600px;"></div>
			<script>
	            var chart;
	
	            var chartData = [<%=request.getAttribute("sbWork")%>];
	            configChart = function() {
	           /*  AmCharts.ready(function () { */
	                // SERIAL CHART
	                chart = new AmCharts.AmSerialChart();
	
	                chart.dataProvider = chartData;
	                chart.marginTop = 10;
	                chart.categoryField = "week";
	
	                // AXES
	                // Category
	                var categoryAxis = chart.categoryAxis;
	                categoryAxis.gridAlpha = 0.07;
	                categoryAxis.axisColor = "#DADADA";
	                categoryAxis.startOnAxis = true;
	
	                // Value
	                var valueAxis = new AmCharts.ValueAxis();
	                valueAxis.stackType = "regular"; // this line makes the chart "stacked"
	                valueAxis.gridAlpha = 0.07;
	                valueAxis.title = "Number of Tasks";
	                chart.addValueAxis(valueAxis);
	
	                // GRAPHS
	                // first graph
	                var graph = new AmCharts.AmGraph();
	                graph.type = "line"; // it's simple line graph
	                graph.title = "Completed";
	                graph.valueField = "completed";
	                graph.lineAlpha = 0;
	                graph.fillAlphas = 0.6; // setting fillAlphas to > 0 value makes it area graph
	                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Completed: <b>[[value]]</b></span>";
	               // graph.hidden = true;
	               	graph.fillColors = "#9ACD32";
	                chart.addGraph(graph);
	
	                // second graph
	                graph = new AmCharts.AmGraph();
	                graph.type = "line";
	                graph.title = "Active";
	                graph.valueField = "active";
	                graph.lineAlpha = 0;
	                graph.fillAlphas = 0.6;
	                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Active: <b>[[value]]</b></span>";
	               	graph.fillColors = "#4682B4";
	                chart.addGraph(graph);
	
	                // third graph
	                graph = new AmCharts.AmGraph();
	                graph.type = "line";
	                graph.title = "Overdue";
	                graph.valueField = "overdue";
	                graph.lineAlpha = 0;
	                graph.fillAlphas = 0.6;
	                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Overdue: <b>[[value]]</b></span>";
	               	graph.fillColors = "#B22222";
	                chart.addGraph(graph);
	
	                // LEGEND
	                var legend = new AmCharts.AmLegend();
	                legend.position = "top";
	                legend.valueText = "[[value]]";
	                legend.valueWidth = 100;
	                legend.valueAlign = "left";
	                legend.equalWidths = false;
	                legend.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
	                chart.addLegend(legend);
	
	                // CURSOR
	                var chartCursor = new AmCharts.ChartCursor();
	                chartCursor.cursorAlpha = 0;
	                chart.addChartCursor(chartCursor);
	
	                // SCROLLBAR
	                var chartScrollbar = new AmCharts.ChartScrollbar();
	                chartScrollbar.color = "#FFFFFF";
	                chart.addChartScrollbar(chartScrollbar);
	
	                // WRITE
	                chart.write("chartdiv");
	            };
	            if (AmCharts.isReady) {
	                configChart();
	              } else {
	                AmCharts.ready(configChart);
	              }
	        </script>
	        
		</div>
		
		
	<% } else if(proType != null && proType.trim().equalsIgnoreCase("R")) { %>
		<script type="text/javascript">
			$(function(){
				$("#f_strWLocation").multiselect().multiselectfilter();
				$("#f_department").multiselect().multiselectfilter();
				$("#f_service").multiselect().multiselectfilter();
			});    
		</script> 
		
		<%
			List<Map<String, String>> alPeople = (List<Map<String, String>>)request.getAttribute("alPeople");
			if(alPeople == null) alPeople = new ArrayList<Map<String,String>>();
		 	Map<String, String> hmCompleteTask = (Map<String, String>) request.getAttribute("hmCompleteTask");
		 	if (hmCompleteTask == null)hmCompleteTask = new HashMap<String, String>();
		 	Map<String, String> hmActiveTask = (Map<String, String>) request.getAttribute("hmActiveTask");
		 	if (hmActiveTask == null)hmActiveTask = new HashMap<String, String>();
		 	Map<String, String> hmOverdueTask = (Map<String, String>) request.getAttribute("hmOverdueTask");
		 	if (hmOverdueTask == null)hmOverdueTask = new HashMap<String, String>(); 
	 	%>
		
		
		<s:form name="frm_WorkProgress" id="frm_WorkProgress" action="WorkProgress" theme="simple">
			<s:hidden name="proPage" id="proPage"/>
    		<s:hidden name="minLimit" id="minLimit"/>
    		<s:hidden name="proType" id="proType"/>
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
				    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
				    <div class="box-tools pull-right">
				        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<%
							boolean poFlag = (Boolean) request.getAttribute("poFlag");
							if(poFlag && (strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER)))){
							%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project Type</p> 
									<s:select theme="simple" name="strProType" id="strProType" headerKey="1" headerValue="All Projects" list="#{'2':'My Projects'}" onchange="submitForm('1');"/>
								</div>
							<% } %>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organisation</p> 
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" />
							</div>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" multiple="true"/>
							</div>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
							</div>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">SBU</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true" />
							</div>
						</div>
					</div>
					
					<div class="row row_without_margin" style="margin-top: 10px;">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						
				      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Calendar Year</p>
								<s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" headerKey="0"  list="calendarYearList" key="" cssStyle="width:200px !important;"  onchange="submitForm('2');"/>
							</div>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Month</p>
								<s:select theme="simple" name="strMonth" listKey="monthId" cssStyle="width:110px !important;" listValue="monthName" list="monthList" key="" />
							</div>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="btnSubmit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
							
						</div>
					</div>
				</div>
			</div>
		</s:form>
		
		
		<div class="col-lg-6 col-md-6 col-sm-12">
			<table class="table table-bordered">
				<% if (alPeople != null && alPeople.size() > 0) { %>
					<tr>
						<th width="35%">Resource Name</th>
						<th>Completed</th>
						<th>Active</th>
						<th>Overdue</th>
					</tr>
					
					<%
						for(int j =0; j<alPeople.size(); j++){
							Map<String, String> hmPeople = alPeople.get(j);
							String strEmpId = hmPeople.get("EMP_ID");
					%>
						<tr>
							<td>
								<%if(docRetriveLocation == null) { %>
									<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmPeople.get("EMP_IMAGE") %>" />
								<%} else { %>
				                 	<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strEmpId+"/"+IConstants.I_22x22+"/"+hmPeople.get("EMP_IMAGE")%>" />
				                <%} %> 
								<%=hmPeople.get("EMP_NAME") %> 
							</td>
							<td style="text-align: center;">
							<span id="completedbar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmCompleteTask.get(strEmpId + "_COMPLETE_COUNT"),"0")%></span></td>
							<td style="text-align: center;"><span id="activebar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmActiveTask.get(strEmpId + "_ACTIVE_COUNT"), "0")%></span></td>
							<td style="text-align: center;"><span id="overduebar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmOverdueTask.get(strEmpId + "_OVERDUE_COUNT"), "0")%></span>
								<script type="text/javascript">
								    $(function() {
								    	 $('#completedbar<%=strEmpId%>').sparkline(new Array(<%=hmCompleteTask.get(strEmpId + "_COMPLETE")%>), {type: 'bar', barColor: '#9ACD32'} );
									     $('#activebar<%=strEmpId%>').sparkline(new Array(<%=hmActiveTask.get(strEmpId + "_ACTIVE")%>), {type: 'bar', barColor: '#4682B4'} );
									     $('#overduebar<%=strEmpId%>').sparkline(new Array(<%=hmOverdueTask.get(strEmpId + "_OVERDUE")%>), {type: 'bar', barColor: '#B22222'} );
								    });
							    </script>
							</td>
						</tr>
					
					<%}%>
				<% } else { %>
					<tr><td colspan="4"><div class="msg nodata"><span>Resources not available.</span></div> </td></tr>
				<% } %>		
			</table>
			
			<div style="text-align: center; float: left; width: 100%;">
				<% int intproCnt = uF.parseToInt(proCount);
					int pageCnt = 0;
					int minLimit = 0;
					for(int i=1; i<=intproCnt; i++) {
						minLimit = pageCnt * 20;
						pageCnt++;
				%>
				<% if(i ==1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					if(uF.parseToInt(strPgCnt) > 1) {
						 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
						 strMinLimit = (uF.parseToInt(strMinLimit)-20) + "";
					}
					if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
				%>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
						<%="< Prev" %></a>
					<% } else { %>
						<b><%="< Prev" %></b>
					<% } %>
					</span>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
						<b>...</b>
					<% } %>
				
				<% } %>
				
				<% if(i > 1 && i < intproCnt) { %>
				<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
				<% } %>
				<% } %>
				
				<% if(i == intproCnt && intproCnt > 1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)+20) + "";
					 if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
					%>
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
						<b>...</b>
					<% } %>
				
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
					<% } else { %>
						<b><%="Next >" %></b>
					<% } %>
					</span>
				<% } %>
				<%} %>
			</div>
			
		</div>
		
		<div class="col-lg-6 col-md-6 col-sm-12">
			<div id="chartdiv" style="width:100%; height:600px;"></div>
			<script>
	            var chart;
	
	            var chartData = [<%=request.getAttribute("sbWork")%>];
	            configChartR = function() {
	            /* AmCharts.ready(function () { */
	                // SERIAL CHART
	                chart = new AmCharts.AmSerialChart();
	                chart.dataProvider = chartData;
	                chart.marginTop = 10;
	                chart.categoryField = "week";
	
	                // AXES
	                // Category
	                var categoryAxis = chart.categoryAxis;
	                categoryAxis.gridAlpha = 0.07;
	                categoryAxis.axisColor = "#DADADA";
	                categoryAxis.startOnAxis = true;
	
	                // Value
	                var valueAxis = new AmCharts.ValueAxis();
	                valueAxis.stackType = "regular"; // this line makes the chart "stacked"
	                valueAxis.gridAlpha = 0.07;
	                valueAxis.title = "Number of Tasks";
	                chart.addValueAxis(valueAxis);
	
	                // GRAPHS
	                // first graph
	                var graph = new AmCharts.AmGraph();
	                graph.type = "line"; // it's simple line graph
	                graph.title = "Completed";
	                graph.valueField = "completed";
	                graph.lineAlpha = 0;
	                graph.fillAlphas = 0.6; // setting fillAlphas to > 0 value makes it area graph
	                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Completed: <b>[[value]]</b></span>";
	               // graph.hidden = true;
	               	graph.fillColors = "#9ACD32";
	                chart.addGraph(graph);
	
	                // second graph
	                graph = new AmCharts.AmGraph();
	                graph.type = "line";
	                graph.title = "Active";
	                graph.valueField = "active";
	                graph.lineAlpha = 0;
	                graph.fillAlphas = 0.6;
	                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Active: <b>[[value]]</b></span>";
	               	graph.fillColors = "#4682B4";
	                chart.addGraph(graph);
	
	                // third graph
	                graph = new AmCharts.AmGraph();
	                graph.type = "line";
	                graph.title = "Overdue";
	                graph.valueField = "overdue";
	                graph.lineAlpha = 0;
	                graph.fillAlphas = 0.6;
	                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Overdue: <b>[[value]]</b></span>";
	               	graph.fillColors = "#B22222";
	                chart.addGraph(graph);
	
	                // LEGEND
	                var legend = new AmCharts.AmLegend();
	                legend.position = "top";
	                legend.valueText = "[[value]]";
	                legend.valueWidth = 100;
	                legend.valueAlign = "left";
	                legend.equalWidths = false;
	                legend.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
	                chart.addLegend(legend);
	
	                // CURSOR
	                var chartCursor = new AmCharts.ChartCursor();
	                chartCursor.cursorAlpha = 0;
	                chart.addChartCursor(chartCursor);
	
	                // SCROLLBAR
	                var chartScrollbar = new AmCharts.ChartScrollbar();
	                chartScrollbar.color = "#FFFFFF";
	                chart.addChartScrollbar(chartScrollbar);
	
	                // WRITE
	                chart.write("chartdiv");
	            };
	            if (AmCharts.isReady) {
	                configChartR();
	              } else {
	                AmCharts.ready(configChartR);
	              }
	        </script>
	        
		</div>
		
		<script>
			//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
			$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

			$(window).bind("load", function() {
  			  var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
			});  
		</script>
	<% } %>
	
	</div>
	
	</div>
	
	</div>
	
</div>
</section>

</div>