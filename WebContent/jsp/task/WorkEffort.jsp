<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*,ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div id="divResult">

<%String btnSubmit = (String)request.getAttribute("btnSubmit");
	if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) {
%>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script> 
<% } %>

<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery.sparkline.js"></script>

<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script>
 
<script type="text/javascript">



</script>  
<script type="text/javascript">
	 
	function loadMore(proPage, minLimit) {
		
		/* var action = 'WorkEffort.action?btnSubmit=Submit&proOwner='+proOwner+'&calendarYear='+calendarYear+'&strMonth='+strMonth
		+'&proPage='+proPage+'&minLimit='+minLimit; */
		document.getElementById("proPage").value = proPage;
		document.getElementById("minLimit").value = minLimit;
		var form_data = $("#frm_WorkEffort").serialize();
		//alert(form_data);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'WorkEffort.action?btnSubmit=Submit',
			data: form_data,
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}

	function submitForm(type) {
		//alert(data);
		var proOwner = "";
		if(document.getElementById("proOwner")) {
			proOwner = document.getElementById("proOwner").value;
		}
		var calendarYear = document.getElementById("calendarYear").value;
		var strMonth = document.getElementById("strMonth").value;
		
		var action = 'WorkEffort.action?btnSubmit=Submit&proOwner='+proOwner+'&calendarYear='+calendarYear+'&strMonth='+strMonth;
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		//alert("action ===>> " + action);
		$.ajax({
			type : 'POST',
			url: action,
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
    }
	
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
		<section class="content">
			<div class="col-lg-12 col-md-12 col-sm-12 box box-body">
	
		<%
			List<Map<String, String>> alPeople = (List<Map<String, String>>)request.getAttribute("alPeople");
			if(alPeople == null) alPeople = new ArrayList<Map<String,String>>();
		 	Map<String, String> hmBillable = (Map<String, String>) request.getAttribute("hmBillable");
		 	if (hmBillable == null)hmBillable = new HashMap<String, String>();
		 	Map<String, String> hmNonBillable = (Map<String, String>) request.getAttribute("hmNonBillable");
		 	if (hmNonBillable == null)hmNonBillable = new HashMap<String, String>();
		 	Map<String, String> hmOther = (Map<String, String>) request.getAttribute("hmOther");
		 	if (hmOther == null)hmOther = new HashMap<String, String>(); 
	 	%>
	
			<s:form name="frm_WorkEffort" id="frm_WorkEffort" action="WorkEffort" theme="simple">
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
								<%
								boolean poFlag = (Boolean) request.getAttribute("poFlag");
								if(!poFlag || (strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER)))){
								%>
									<i class="fa fa-filter"></i>
								<% } else { %>
									<i class="fa fa-calendar"></i>
								<% } %>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<% if(!poFlag || (strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER)))) { %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Project Owner</p>
										<s:select theme="simple" name="proOwner" id="proOwner" listKey="proOwnerId" listValue="proOwnerName" list="proOwnerList"
											key=""  headerKey="" headerValue="All Project Owners" onchange="submitForm('1');"/>
									</div>
								<% } %>
								
					      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Calendar Year</p>
									<s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" headerKey="0"  list="calendarYearList" key="" cssStyle="width:200px !important;"  onchange="submitForm('2');"/>
								</div>
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" listKey="monthId" cssStyle="width:110px !important;" listValue="monthName" list="monthList" key="" />
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
							<th>Billable</th>
							<th>Non-Billable</th>
							<th>Other</th>
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
				                 	<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strEmpId+"/"+IConstants.I_22x22+"/"+hmPeople.get("EMP_IMAGE")%>" />
				                <%} %> 
								<%=hmPeople.get("EMP_NAME") %> 
							</td>
							<td style="text-align: center;"><span id="billablebar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmBillable.get(strEmpId + "_BILLABLE_COUNT"),"0")%></span></td>
							<td style="text-align: center;"><span id="nonBillablebar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmNonBillable.get(strEmpId + "_NON_BILLABLE_COUNT"), "0")%></span></td>
							<td style="text-align: center;"><span id="otherbar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmOther.get(strEmpId + "_OTHER_COUNT"), "0")%></span>
								<script type="text/javascript">
								    $(function() {
								    	 $('#billablebar<%=strEmpId%>').sparkline(new Array(<%=hmBillable.get(strEmpId + "_BILLABLE")%>), {type: 'bar', barColor: '#9ACD32'} );
									     $('#nonBillablebar<%=strEmpId%>').sparkline(new Array(<%=hmNonBillable.get(strEmpId + "_NON_BILLABLE")%>), {type: 'bar', barColor: '#4682B4'} );
									     $('#otherbar<%=strEmpId%>').sparkline(new Array(<%=hmOther.get(strEmpId + "_OTHER")%>), {type: 'bar', barColor: '#B22222'} );
								    });
							    </script>
							</td>
						</tr>
						
						<% } %>
					<% } else { %>
						<tr><td colspan="4"><div class="msg nodata" style="width: 94%;"><span>Resources not available.</span></div> </td></tr>
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
			
			
				<%-- <div style="text-align: center; float: left; width: 100%;">
				
				<% 
					
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
				
				</div> --%>
				
				
			</div>
	
				<div class="col-lg-6 col-md-6 col-sm-12">
					<div id="chartdiv" style="width:100%; height:600px;"></div>
					<script>
			            var chart;
			
			            var chartData = [<%=request.getAttribute("sbWork")%>];
			            configChart = function() {
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
			                valueAxis.title = "Number of Hrs";
			                chart.addValueAxis(valueAxis);
			
			                // GRAPHS
			                // first graph
			                var graph = new AmCharts.AmGraph();
			                graph.type = "line"; // it's simple line graph
			                graph.title = "Billable";
			                graph.valueField = "billable";
			                graph.lineAlpha = 0;
			                graph.fillAlphas = 0.6; // setting fillAlphas to > 0 value makes it area graph
			                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Billable: <b>[[value]]</b></span>";
			               // graph.hidden = true;
			               	graph.fillColors = "#9ACD32";
			                chart.addGraph(graph);
			
			                // second graph
			                graph = new AmCharts.AmGraph();
			                graph.type = "line";
			                graph.title = "Non-Billable";
			                graph.valueField = "non-billable";
			                graph.lineAlpha = 0;
			                graph.fillAlphas = 0.6;
			                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Non-Billable: <b>[[value]]</b></span>";
			               	graph.fillColors = "#4682B4";
			                chart.addGraph(graph);
			
			                // third graph
			                graph = new AmCharts.AmGraph();
			                graph.type = "line";
			                graph.title = "Other";
			                graph.valueField = "other";
			                graph.lineAlpha = 0;
			                graph.fillAlphas = 0.6;
			                graph.balloonText = "<span style='font-size:14px; color:#000000;'>Other: <b>[[value]]</b></span>";
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
			
				<script>
					//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
					$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
			
					$(window).bind("load", function() {
			   		 var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
					});  
				</script>
			</div>
		</section>
	</div>
</section>

</div>