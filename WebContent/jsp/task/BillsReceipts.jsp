<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*,ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div id="divResult">

<%	String btnSubmit = (String)request.getAttribute("btnSubmit");
	if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) {
%>
	<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"> </script> --%>
<% } %>
 
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>

<script type="text/javascript">

function submitForm(type){
	var data = "";
	if(type == '1') {
		var proOwner = document.getElementById("proOwner").value;
		data = 'proOwner='+proOwner;
	} else if(type == '2') {
		data = $("#frm_BillsReceipts").serialize();
	}
	//alert(data);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'BillsReceipts.action?btnSubmit=Submit',
		data: data,
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

</script>

	<%
	 	UtilityFunctions uF = new UtilityFunctions();
	 	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	 	
	 	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>


<section class="content">
		<!-- title row -->
	<div class="row">
		<section class="content">
			<div class="col-lg-12 col-md-12 col-sm-12 box box-body">

				<s:form name="frm_BillsReceipts" id="frm_BillsReceipts" action="BillsReceipts" theme="simple">
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
												key="" headerKey="" headerValue="All Project Owners" onchange="submitForm('1');"/>
										</div>
									<% } %>
									
						      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Calendar Year</p>
										<s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" headerKey="0" list="calendarYearList" key="" cssStyle="width:200px !important;"/>
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
					<h4>Bills and Receipts</h4>
					<div id="chartdiv" style="width:100%; height:600px;"></div>
					<script>
			            var chart;
			            var graph;
			
			            var chartData = [<%=request.getAttribute("sbBillsReceipts")%>];
			            configChart = function() {
			            /* AmCharts.ready(function () { */
			                // SERIAL CHART
			                chart = new AmCharts.AmSerialChart();
			
			                chart.dataProvider = chartData;
			                chart.marginLeft = 10;
			                chart.categoryField = "month";
			                //chart.dataDateFormat = "YYYY";
			
			                // AXES
			                // category
			                var categoryAxis = chart.categoryAxis;
			                categoryAxis.parseDates = false; // as our data is date-based, we set parseDates to true
			                //categoryAxis.minPeriod = "YYYY"; // our data is yearly, so we set minPeriod to YYYY
			                categoryAxis.dashLength = 3;
			                categoryAxis.minorGridEnabled = true;
			                categoryAxis.minorGridAlpha = 0.1;
			
			                // value
			                var valueAxis = new AmCharts.ValueAxis();
			                valueAxis.axisAlpha = 0;
			                valueAxis.inside = true;
			                valueAxis.dashLength = 3;
			                chart.addValueAxis(valueAxis);
			
			                // GRAPH
			                graph = new AmCharts.AmGraph();
			                graph.type = "smoothedLine"; // this line makes the graph smoothed line.
			                graph.title = "Bills";
			                graph.valueField = "bills";
			                graph.bullet = "round";
			                graph.bulletSize = 8;
			                graph.bulletBorderColor = "#FFFFFF";
			                graph.bulletBorderAlpha = 1;
			                graph.bulletBorderThickness = 2;
			                graph.lineThickness = 2;
			                graph.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
			                chart.addGraph(graph);
			                
			                graph = new AmCharts.AmGraph();
			                graph.type = "smoothedLine"; // this line makes the graph smoothed line.
			                graph.title = "Receipts";
			                graph.valueField = "receipts";
			                graph.bullet = "round";
			                graph.bulletSize = 8;
			                graph.bulletBorderColor = "#FFFFFF";
			                graph.bulletBorderAlpha = 1;
			                graph.bulletBorderThickness = 2;
			                graph.lineThickness = 2;
			                graph.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
			                chart.addGraph(graph);
			
			                // CURSOR
			                var chartCursor = new AmCharts.ChartCursor();
			                chartCursor.cursorAlpha = 0;
			                chartCursor.cursorPosition = "mouse";
			                chartCursor.categoryBalloonDateFormat = "YYYY";
			                chart.addChartCursor(chartCursor);
			                
			            	// LEGEND
			                var legend = new AmCharts.AmLegend();
			                legend.position = "top";
			                legend.valueText = "[[value]]";
			                legend.valueWidth = 100;
			                legend.valueAlign = "left";
			                legend.equalWidths = false;
			                legend.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
			                chart.addLegend(legend); 
			
			                // SCROLLBAR
			                var chartScrollbar = new AmCharts.ChartScrollbar();
			                chart.addChartScrollbar(chartScrollbar);
			
			                chart.creditsPosition = "bottom-right";
			
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
	
	
				<div class="col-lg-6 col-md-6 col-sm-12">
					<div id="chartDonutdiv" style="width:100%; height:400px;"></div>
					<script>
			            var chart1;
			
			            var chartData1 = [<%=request.getAttribute("sbBillsDonut")%>];
			            var legend1;
			            configChart1 = function() {
			           /*  AmCharts.ready(function () { */
			                // PIE CHART
			                chart1 = new AmCharts.AmPieChart();
			
			                // title of the chart
			               // chart1.addTitle("Collections", 14);
			                chart1.allLabels=[{
			                        "text": "Total Billing",
			                        "align": "center",
			                        "bold": true,
			                        "y": 150
			                    },{
			                        "text": "<%=uF.showData((String)request.getAttribute("dblTotalAmt"),"0") %>",
			                        "align": "center",
			                        "bold": true,
			                        "size": 16,
			                        "y": 180
			                    }];
			                chart1.theme="none";
			                chart1.dataProvider = chartData1;
			                chart1.titleField = "sbu";
			                chart1.valueField = "bills";
			                chart1.sequencedAnimation = true;
			              	chart1.startEffect = "elastic";
			                chart1.innerRadius = "60%";
			                chart1.radius= "42%",
			                chart1.startDuration = 2;
			                chart1.labelRadius = -130; 
			                chart1.labelText = "";
			                chart1.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
			 
			                // LEGEND
			                legend1 = new AmCharts.AmLegend();
			                legend1.align = "center";
			                legend1.markerType = "circle";
			                chart1.addLegend(legend1);
			                
			                // WRITE
			                chart1.write("chartDonutdiv");
			            };
			            if (AmCharts.isReady) {
			                configChart1();
			              } else {
			                AmCharts.ready(configChart1);
			              }
			        </script>
				</div>
			</div>
		</section>
	</div>
</section>

</div>