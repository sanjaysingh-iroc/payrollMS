<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*, ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script type="text/javascript" src="js/jquery.sparkline.js"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");

	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
%>

<section class="content">
	    <%
			boolean poFlag = (Boolean) request.getAttribute("poFlag");
			String strProType = (String) request.getAttribute("strProType");
			if(poFlag && (strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.CEO)) ) ) {
		%>
		   	<s:form theme="simple" name="frmProjectDashboard" action="ProjectDashboard" id="frmProjectDashboard" method="POST" cssClass="formcss">
		    	<div style="float:left;">
			      	<s:select theme="simple" name="strProType" id="strProType" headerKey="1" headerValue="All Projects"
		                list="#{'2':'My Projects'}" onchange="document.frmProjectDashboard.submit();"/>
		                <% if(uF.parseToInt(strProType) == 2) { %>
		                - As a Project Owner
		                <% } else { %>
		                - As a CEO/ Global HR/ HR Manager
		                <% } %>
		        </div>
	        </s:form>
    <% } %>
	<div class="row jscroll margintop20">
		<section class="content">

	<div class="col-lg-4 col-md-6 col-sm-12 col_no_padding connectedSortable">    
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Bills vs Receipts & Commitments vs Receipts</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<div style="width: 100%; text-align: center; font-weight: bold; margin: 10px 0px 0px;"> Bills vs Receipts</div>
			
			<div style="float:right; width:100%;">
             	<s:form theme="simple" name="frmBillsReceiptsCommit" action="ProjectDashboard" id="frmBillsReceiptsCommit" method="POST" cssClass="formcss">
					<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="billsReceiptsCommitFYear" onchange="document.frmBillsReceiptsCommit.submit();"/>
				</s:form>
			</div>
				<div id="chartdiv2" style="width:100%; height:400px;"></div>
				<script>
		            var chart2;
		            var graphBill;
		
		            var chartData2 = [<%=request.getAttribute("sbBillsReceipts")%>];
		
		            AmCharts.ready(function () {
		                // SERIAL CHART
		                chart2 = new AmCharts.AmSerialChart();
		
		                chart2.dataProvider = chartData2;
		                chart2.marginLeft = 10;
		                chart2.categoryField = "month";
		
		                var categoryAxis2 = chart2.categoryAxis;
		                categoryAxis2.parseDates = false;
		                categoryAxis2.dashLength = 3;
		                categoryAxis2.minorGridEnabled = true;
		                categoryAxis2.minorGridAlpha = 0.1;
		
		                var valueAxis2 = new AmCharts.ValueAxis();
		                valueAxis2.axisAlpha = 0;
		                valueAxis2.inside = true;
		                valueAxis2.dashLength = 3;
		                chart2.addValueAxis(valueAxis2);
		
		                graphBill = new AmCharts.AmGraph();
		                graphBill.type = "smoothedLine"; 
		                graphBill.title = "Bills";
		                graphBill.valueField = "bills";
		                graphBill.bullet = "round";
		                graphBill.bulletSize = 8;
		                graphBill.bulletBorderColor = "#FFFFFF";
		                graphBill.bulletBorderAlpha = 1;
		                graphBill.bulletBorderThickness = 2;
		                graphBill.lineThickness = 2;
		                graphBill.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
		                chart2.addGraph(graphBill);
		                
		                graphBill = new AmCharts.AmGraph();
		                graphBill.type = "smoothedLine"; 
		                graphBill.title = "Receipts";
		                graphBill.valueField = "receipts";
		                graphBill.bullet = "round";
		                graphBill.bulletSize = 8;
		                graphBill.bulletBorderColor = "#FFFFFF";
		                graphBill.bulletBorderAlpha = 1;
		                graphBill.bulletBorderThickness = 2;
		                graphBill.lineThickness = 2;
		                graphBill.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
		                chart2.addGraph(graphBill);
		
		                var chartCursor2 = new AmCharts.ChartCursor();
		                chartCursor2.cursorAlpha = 0;
		                chartCursor2.cursorPosition = "mouse";
		                chartCursor2.categoryBalloonDateFormat = "YYYY";
		                chart2.addChartCursor(chartCursor2);
		                
		                var legend2 = new AmCharts.AmLegend();
		                legend2.position = "top";
		                legend2.valueText = "[[value]]";
		                legend2.valueWidth = 100;
		                legend2.valueAlign = "left";
		                legend2.equalWidths = false;
		                legend2.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
		                chart2.addLegend(legend2); 
		
		                var chartScrollbar2 = new AmCharts.ChartScrollbar();
		                chart2.addChartScrollbar(chartScrollbar2);
		
		                chart2.creditsPosition = "bottom-right";
		
		                chart2.write("chartdiv2");
		            });
		        </script>
		        
		        <div class="viewmore">
		        	<a href="BillsReceipts.action">
           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Bills vs Receipts.."/> --%>
           				<i class="fa fa-forward" aria-hidden="true" title="Go to Bills vs Receipts.."></i>
           			</a>
           		</div>
           		
           		<div style="width: 100%; text-align: center; font-weight: bold; margin: 10px 0px 0px;"> Commitments vs Receipts</div>
           		
           		<div id="chartdiv33" style="width:100%; height:400px;"></div>
				<script>
		            var chart33;
		            var graphCommitment;
		
		            var chartData33 = [<%=request.getAttribute("sbCommitReceipts") %>];
		
		            AmCharts.ready(function () {
		                // SERIAL CHART
		                chart33 = new AmCharts.AmSerialChart();
		
		                chart33.dataProvider = chartData33;
		                chart33.marginLeft = 10;
		                chart33.categoryField = "month";
		
		                // AXES
		                // category
		                var categoryAxis33 = chart33.categoryAxis;
		                categoryAxis33.parseDates = false; // as our data is date-based, we set parseDates to true
		                categoryAxis33.dashLength = 3;
		                categoryAxis33.minorGridEnabled = true;
		                categoryAxis33.minorGridAlpha = 0.1;
		
		                // value
		                var valueAxis33 = new AmCharts.ValueAxis();
		                valueAxis33.axisAlpha = 0;
		                valueAxis33.inside = true;
		                valueAxis33.dashLength = 3;
		                chart33.addValueAxis(valueAxis33);
		
		                // GRAPH
		                graphCommitment = new AmCharts.AmGraph();
		                graphCommitment.type = "smoothedLine"; // this line makes the graph smoothed line.
		                graphCommitment.title = "Commitments";
		                graphCommitment.valueField = "commitments";
		                graphCommitment.bullet = "round";
		                graphCommitment.bulletSize = 8;
		                graphCommitment.bulletBorderColor = "#FFFFFF";
		                graphCommitment.bulletBorderAlpha = 1;
		                graphCommitment.bulletBorderThickness = 2;
		                graphCommitment.lineThickness = 2;
		                graphCommitment.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
		                chart33.addGraph(graphCommitment);
		                
		                graphCommitment = new AmCharts.AmGraph();
		                graphCommitment.type = "smoothedLine"; // this line makes the graph smoothed line.
		                graphCommitment.title = "Receipts";
		                graphCommitment.valueField = "receipts";
		                graphCommitment.bullet = "round";
		                graphCommitment.bulletSize = 8;
		                graphCommitment.bulletBorderColor = "#FFFFFF";
		                graphCommitment.bulletBorderAlpha = 1;
		                graphCommitment.bulletBorderThickness = 2;
		                graphCommitment.lineThickness = 2;
		                graphCommitment.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
		                chart33.addGraph(graphCommitment);
		
		                // CURSOR
		                var chartCursor33 = new AmCharts.ChartCursor();
		                chartCursor33.cursorAlpha = 0;
		                chartCursor33.cursorPosition = "mouse";
		                chartCursor33.categoryBalloonDateFormat = "YYYY";
		                chart33.addChartCursor(chartCursor33);
		                
		            	// LEGEND
		                var legend33 = new AmCharts.AmLegend();
		                legend33.position = "top";
		                legend33.valueText = "[[value]]";
		                legend33.valueWidth = 100;
		                legend33.valueAlign = "left";
		                legend33.equalWidths = false;
		                legend33.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
		                chart33.addLegend(legend33);
		
		                // SCROLLBAR
		                var chartScrollbar33 = new AmCharts.ChartScrollbar();
		                chart33.addChartScrollbar(chartScrollbar33);
		                chart33.creditsPosition = "bottom-right";
		
		                // WRITE
		                chart33.write("chartdiv33");
		            });
		        </script>
		        
		        <div class="viewmore">
		        	<a href="CommitmentReport.action?strProType=2">
           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Commitments vs Receipts.."/> --%>
           				<i class="fa fa-forward" aria-hidden="true" title="Go to Commitments vs Receipts.."></i>
           				
           				
           			</a>
           		</div>
           		
			</div>
		</div>
		
		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_PROJECT_DASH_PROJECT_EXPENSE)) && hmFeatureUserTypeId.get(IConstants.F_PROJECT_DASH_PROJECT_EXPENSE).contains(strUsertypeId)) { %>
			<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Project Expenses</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<div style="float:right; width:100%;">
	             	<s:form theme="simple" name="frmProjectExpenses" action="ProjectDashboard" id="frmProjectExpenses" method="POST" cssClass="formcss">
						<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="projectExpensesFYear" onchange="document.frmProjectExpenses.submit();"/>
					</s:form>
				</div>
					<div id="chartProCostdiv" style="width:100%; height:400px;"></div>
					<script>
					var chart3;
			
			        var chartData3 = [<%=request.getAttribute("sbProCosting")%>];
			
			        AmCharts.ready(function () {
			            // SERIAL CHART
			            chart3 = new AmCharts.AmSerialChart();
			            chart3.dataProvider = chartData3;
			            chart3.categoryField = "project";
			            chart3.plotAreaBorderAlpha = 0.2;
			
			            // AXES
			            // category
			            var categoryAxis3 = chart3.categoryAxis;
			            categoryAxis3.gridAlpha = 0.1;
			            categoryAxis3.axisAlpha = 0;
			            categoryAxis3.gridPosition = "start";
			
			            // value
			            var valueAxis3 = new AmCharts.ValueAxis();
			            valueAxis3.stackType = "regular";
			            valueAxis3.gridAlpha = 0.1;
			            valueAxis3.axisAlpha = 0;
			            chart3.addValueAxis(valueAxis3);
			
			            // GRAPHS
						var graph3 = new AmCharts.AmGraph();
						graph3.title = 'salary';
						graph3.labelText = '[[value]]';
						graph3.valueField = 'salary';
						graph3.type = 'column';
						graph3.lineAlpha = 0;
						graph3.fillAlphas = 1;
						graph3.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
						chart3.addGraph(graph3);  
			
						graph3 = new AmCharts.AmGraph();
						graph3.title = 'reimbursement';
						graph3.labelText = '[[value]]';
						graph3.valueField = 'reimbursement';
						graph3.type = 'column';
						graph3.lineAlpha = 0;
						graph3.fillAlphas = 1;
						graph3.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
						chart3.addGraph(graph3);
			
			            // LEGEND
			            var legend3 = new AmCharts.AmLegend();
			            legend3.borderAlpha = 0.2;
			            legend3.horizontalGap = 10;
			            chart3.addLegend(legend3);
			
			            // WRITE
			            chart3.write("chartProCostdiv");
			        });
			        </script>
			        
			        <div class="viewmore">
			        	<a href="ProjectCosting.action?strProType=<%=strProType %>">
	           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Project Expenses.."/> --%>
	           				<i class="fa fa-forward" aria-hidden="true" title="Go to Project Expenses.."></i>
	           				
	           				
	           			</a>
	           		</div>
				</div>
			</div>
		<% } %>
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Collections</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<div style="float:right; width:100%;">
	             	<s:form theme="simple" name="frmCollection" action="ProjectDashboard" id="frmCollection" method="POST" cssClass="formcss">
						<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="collectionFYear" onchange="document.frmCollection.submit();"/>
					</s:form>
				</div>
				<div id="chartDonutdiv" style="width:100%; height:400px;"></div>
				<script>
		            var chart;
		            var chartData = [<%=request.getAttribute("sbBillsDonut")%>];
		            var legend;
		
		            AmCharts.ready(function () {
		                // PIE CHART
		                chart = new AmCharts.AmPieChart();
		                // title of the chart
		               // chart.addTitle("Collections", 14);
		                chart.allLabels=[{
		                        "text": "Total Collections",
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
		                chart.theme="none";
		                chart.dataProvider = chartData;
		                chart.titleField = "sbu";
		                chart.valueField = "bills";
		                chart.sequencedAnimation = true;
		              	chart.startEffect = "elastic";
		                chart.innerRadius = "60%";
		                chart.radius= "42%",
		                chart.startDuration = 2;
		                chart.labelRadius = -130; 
		                chart.labelText = "";
		                chart.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
		 
		                // LEGEND
		                legend = new AmCharts.AmLegend();
		                legend.align = "center";
		                legend.markerType = "circle";
		                chart.addLegend(legend);
		                
		                // WRITE
		                chart.write("chartDonutdiv");
		            });
		        </script>
		        
		        <div class="viewmore">
		        	<a href="BillsReceipts.action">
           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Collections.."/> --%>
           				<i class="fa fa-forward" aria-hidden="true" title="Go to Collections.."></i>
           				
           				
           			</a>
           		</div>
			</div>
		</div>
		
		
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Profitability</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<div style="float:right; width:100%;">
	             	<s:form theme="simple" name="frmProfitability" action="ProjectDashboard" id="frmProfitability" method="POST" cssClass="formcss">
						<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="profitabilityFYear" onchange="document.frmProfitability.submit();"/>
					</s:form>
				</div>
				<div id="CEStatus" style="height: 400px; width:100%;"></div>
				<script>
					 var chart1;
			         var chartData1 = [<%=request.getAttribute("sbProfitChart")%>];
			         AmCharts.ready(function () {
			             // SERIAL CHART
			             chart1 = new AmCharts.AmSerialChart();
			             chart1.dataProvider = chartData1;
			             chart1.categoryField = "Profitability";
			             chart1.startDuration = 1;
			             chart1.plotAreaBorderColor = "#DADADA";
			             chart1.plotAreaBorderAlpha = 1;
			             // this single line makes the chart a bar chart
			            chart1.rotate = true;
	
			             // AXES
			             // Category
			             var categoryAxis = chart1.categoryAxis;
			             categoryAxis.gridPosition = "start";
			             categoryAxis.gridAlpha = 0.1;
			             categoryAxis.axisAlpha = 0;
	
			             // Value
			             var valueAxis = new AmCharts.ValueAxis();
			             valueAxis.axisAlpha = 0;
			             valueAxis.gridAlpha = 0.1;
			             valueAxis.position = "top";
			             chart1.addValueAxis(valueAxis);
	
			             <%
			             List<String> alProfit = (List<String>)request.getAttribute("alProfit");
			             if(alProfit == null) alProfit = new ArrayList<String>();
			             for(int i = 0; i < alProfit.size(); i++){
			            	 String strGraph = alProfit.get(i);
			             %>
				             <%=strGraph%>
						<%}%>
			             // LEGEND
			             var legend1 = new AmCharts.AmLegend();
			             chart1.addLegend(legend1);
	
			             chart1.creditsPosition = "top-right";
	
			             // WRITE
			             chart1.write("CEStatus");
			         });
		        </script>
		        
		        <div class="viewmore">
		        	<a href="ProfitabilityByIndustry.action?strType=P&strProType=<%=strProType %>">
           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Profitability.."/> --%>
           				<i class="fa fa-forward" aria-hidden="true" title="Go to Profitability.."></i>
           				
           				
           				
           			</a>
           		</div>
			</div>
		</div>
	</div>
	
	<div class="col-lg-4 col-md-6 col-sm-12 paddingright0 connectedSortable">
		
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Project Performance (Time)</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
		<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<%
					Map hmProPerformaceProjectName = (Map)request.getAttribute("hmProPerformaceProjectName");
					Map hmProPerformaceProjectTimeIndicator = (Map)request.getAttribute("hmProPerformaceProjectTimeIndicator");
					List alProjectId = (List)request.getAttribute("alProjectId");
					Map<String, String> hmProOwner = (Map<String, String>)request.getAttribute("hmProOwner");
					if(hmProOwner == null) hmProOwner = new HashMap<String, String>();
					Map<String, String> hmProActIdealTimeHRS = (Map<String, String>)request.getAttribute("hmProActIdealTimeHRS");
					if(hmProActIdealTimeHRS == null) hmProActIdealTimeHRS = new HashMap<String, String>();
				%>
				<table class="table table-striped table-bordered">
				<% if(alProjectId != null && !alProjectId.isEmpty()) { %>
					<tr>
						<th>Project Name</th>
						<th>Project Owner</th>
						<th>Deadline</th> 
						<th>Estimated Time<br/>(hrs)</th>
						<th>Time Spent<br/>(hrs)</th>
						<th>Indicator</th>
					</tr>
				<%
					for(int i=0; alProjectId != null && !alProjectId.isEmpty() && i<alProjectId.size(); i++){
						String strBullet = uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_ACT_TIME_HRS"),"0")+","+uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_IDEAL_TIME_HRS"),"0");
				%>
					<tr>
						<td><%=hmProPerformaceProjectName.get((String)alProjectId.get(i)) %></td>
						<td> <%=hmProOwner.get((String)alProjectId.get(i)) %></td>
						<td>
							<span id="bullet<%=(String)alProjectId.get(i)%>">Loading..</span>
							<script type="text/javascript">
								
							    	$('#bullet<%=(String)alProjectId.get(i)%>').sparkline(new Array(<%=strBullet %>), {type: 'bullet',targetColor: '#b2b2b2',performanceColor: '#9acd32'} );
						    </script>
						</td>
						<td class="alignRight padRight20" ><%=hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_IDEAL_TIME_HRS") %></td>
						<td class="alignRight padRight20" ><%=hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_ACT_TIME_HRS") %></td>
						<td align="center"><%=hmProPerformaceProjectTimeIndicator.get((String)alProjectId.get(i)) %></td>
					</tr>
					<%} %> 
				<%} else { %>
					<tr><td colspan="6"><div class="msg nodata" style="width: 94%;"><span>Projects not available for this selection.</span></div> </td></tr>
				<% } %>
				</table>
				
				<div class="viewmore">
		        	<a href="ProjectPerformanceReportWP.action?strProType=<%=strProType %>">
           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Project Performance.."/> --%>
           				<i class="fa fa-forward" aria-hidden="true" title="Go to Project Performance.."></i>
           				
           				
           			</a>
           		</div>
			</div>
		</div>
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Project Performance (Money)</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
		<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<div style="float:right; width:100%;">
	             	<s:form theme="simple" name="frmProjectPerformance" action="ProjectDashboard" id="frmProjectPerformance" method="POST" cssClass="formcss">
						<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="projectPerformanceFYear" onchange="document.frmProjectPerformance.submit();"/>
					</s:form>
				</div>
			
				<div id="chartdiv55" style="width:100%; height:400px;"></div>
				<script>
		            var chart55;
		
		            var chartData55 = [<%=request.getAttribute("projectPerformance")%>];
		
		
		            AmCharts.ready(function () {
		                // SERIAL CHART
		                chart55 = new AmCharts.AmSerialChart();
		                chart55.dataProvider = chartData55;
		                chart55.categoryField = "project";
		                chart55.startDuration = 1;
		
		                // AXES
		                // category
		               /*  var categoryAxis = chart.categoryAxis;
		                categoryAxis.labelRotation = 90;
		                categoryAxis.gridPosition = "start"; */
		                
		             // AXES
		                // Category
		                var categoryAxis55 = chart55.categoryAxis;
		                categoryAxis55.gridAlpha = 0.07;
		                categoryAxis55.axisColor = "#DADADA";
		                categoryAxis55.startOnAxis = true;
		
		                // Value
		                var valueAxis55 = new AmCharts.ValueAxis();
		                valueAxis55.title = "Amount"; // this line makes the chart "stacked"
		                //valueAxis.stackType = "100%";
		                valueAxis55.gridAlpha = 0.07;
		                chart55.addValueAxis(valueAxis55);
		
		                // value
		                // in case you don't want to change default settings of value axis,
		                // you don't need to create it, as one value axis is created automatically.
		
		                // GRAPH
		                var graph55 = new AmCharts.AmGraph();
		                graph55.valueField = "Billable Cost";
		                graph55.balloonText = "[[category]]: Billable Cost:<b>[[value]]</b>";
		                graph55.type = "column";
		                graph55.lineAlpha = 0;
		                graph55.fillAlphas = 0.8;
		                chart55.addGraph(graph55);
				
				 		var graph155 = new AmCharts.AmGraph();
				 		graph155.valueField = "Budgeted Cost";
				 		graph155.balloonText = "[[category]]: Budgeted Cost:<b>[[value]]</b>";
				 		graph155.type = "column";
				 		graph155.lineAlpha = 0;
				 		graph155.fillAlphas = 0.8;
		                chart55.addGraph(graph155);
		                
		                var graph255 = new AmCharts.AmGraph();
		                graph255.valueField = "Actual Cost";
		                graph255.balloonText = "[[category]]: Actual Cost:<b>[[value]]</b>";
		                graph255.type = "column";
		                graph255.lineAlpha = 0;
		                graph255.fillAlphas = 0.8;
		                chart55.addGraph(graph255);
		
		                // CURSOR
		                var chartCursor55 = new AmCharts.ChartCursor();
		                chartCursor55.cursorAlpha = 0;
		                chartCursor55.zoomable = false;
		                chartCursor55.categoryBalloonEnabled = false;
		                chart55.addChartCursor(chartCursor55);
		
		                chart55.creditsPosition = "top-right";
		
		                chart55.write("chartdiv55");
		            });
		        </script>
		        
		        <div class="viewmore">
		        	<a href="ProjectPerformanceReportWP.action?strProType=<%=strProType %>">
           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Project Performance.."/> --%>
           				<i class="fa fa-forward" aria-hidden="true" title="Go to Project Performance.."></i>
           				
           			</a>
           		</div>
			</div>
		</div>
		
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Commitment Outstanding vs Billed Outstanding</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<div style="float:right; width:100%;">
	             	<s:form theme="simple" name="frmOutstandingCommitBilled" action="ProjectDashboard" id="frmOutstandingCommitBilled" method="POST" cssClass="formcss">
						<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="outstandingCommitBilledFYear" onchange="document.frmOutstandingCommitBilled.submit();"/>
					</s:form>
				</div>
			
				<div id="chartdiv44" style="width:100%; height:400px;"></div>
				<script>
		            var chart44;
		            var graphOCommitmentOBilled;
		
		            var chartData44 = [<%=request.getAttribute("sbOutstandingCommitBilled") %>];
		
		            AmCharts.ready(function () {
		                // SERIAL CHART
		                chart44 = new AmCharts.AmSerialChart();
		
		                chart44.dataProvider = chartData44;
		                chart44.marginLeft = 10;
		                chart44.categoryField = "month";
		
		                // AXES
		                // category
		                var categoryAxis44 = chart44.categoryAxis;
		                categoryAxis44.parseDates = false; // as our data is date-based, we set parseDates to true
		                categoryAxis44.dashLength = 3;
		                categoryAxis44.minorGridEnabled = true;
		                categoryAxis44.minorGridAlpha = 0.1;
		
		                // value
		                var valueAxis44 = new AmCharts.ValueAxis();
		                valueAxis44.axisAlpha = 0;
		                valueAxis44.inside = true;
		                valueAxis44.dashLength = 3;
		                chart44.addValueAxis(valueAxis44);
		
		                // GRAPH
		                graphOCommitmentOBilled = new AmCharts.AmGraph();
		                graphOCommitmentOBilled.type = "smoothedLine"; // this line makes the graph smoothed line.
		                graphOCommitmentOBilled.title = "Commitment Outstanding";
		                graphOCommitmentOBilled.valueField = "commitment";
		                graphOCommitmentOBilled.bullet = "round";
		                graphOCommitmentOBilled.bulletSize = 8;
		                graphOCommitmentOBilled.bulletBorderColor = "#FFFFFF";
		                graphOCommitmentOBilled.bulletBorderAlpha = 1;
		                graphOCommitmentOBilled.bulletBorderThickness = 2;
		                graphOCommitmentOBilled.lineThickness = 2;
		                graphOCommitmentOBilled.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
		                chart44.addGraph(graphOCommitmentOBilled);
		                
		                graphOCommitmentOBilled = new AmCharts.AmGraph();
		                graphOCommitmentOBilled.type = "smoothedLine"; // this line makes the graph smoothed line.
		                graphOCommitmentOBilled.title = "Billed Outstanding";
		                graphOCommitmentOBilled.valueField = "billed";
		                graphOCommitmentOBilled.bullet = "round";
		                graphOCommitmentOBilled.bulletSize = 8;
		                graphOCommitmentOBilled.bulletBorderColor = "#FFFFFF";
		                graphOCommitmentOBilled.bulletBorderAlpha = 1;
		                graphOCommitmentOBilled.bulletBorderThickness = 2;
		                graphOCommitmentOBilled.lineThickness = 2;
		                graphOCommitmentOBilled.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
		                chart44.addGraph(graphOCommitmentOBilled);
		
		                // CURSOR
		                var chartCursor33 = new AmCharts.ChartCursor();
		                chartCursor33.cursorAlpha = 0;
		                chartCursor33.cursorPosition = "mouse";
		                chartCursor33.categoryBalloonDateFormat = "YYYY";
		                chart44.addChartCursor(chartCursor33);
		                
		            	// LEGEND
		                var legend44 = new AmCharts.AmLegend();
		                legend44.position = "top";
		                legend44.valueText = "[[value]]";
		                legend44.valueWidth = 100;
		                legend44.valueAlign = "left";
		                legend44.equalWidths = false;
		                legend44.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
		                chart44.addLegend(legend44);
		
		                // SCROLLBAR
		                var chartScrollbar44 = new AmCharts.ChartScrollbar();
		                chart44.addChartScrollbar(chartScrollbar44);
		                chart44.creditsPosition = "bottom-right";
		
		                // WRITE
		                chart44.write("chartdiv44");
		            });
		        </script>
		        
		        <%-- <div class="viewmore">
		        	<a href="CommitmentReport.action?strProType=2">
           				<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Commitments vs Receipts.."/>
           			</a>
           		</div> --%>
			</div>
		</div>
		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_PROJECT_DASH_WORK_PROGRESS)) && hmFeatureUserTypeId.get(IConstants.F_PROJECT_DASH_WORK_PROGRESS).contains(strUsertypeId)) { %>
			<div class="box box-primary">
				<div class="box-header with-border">
					<h3 class="box-title">Work Progress</h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
			<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto;">
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
				 	<table class="table table-striped table-bordered" style="width: 100%;">
					<% if (hmProject != null && hmProject.size() > 0) { %>
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
							<td style="text-align: center;"><span id="completedbar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmCompleteTask.get(strProId + "_COMPLETE_COUNT"), "0")%></span></td>
							<td style="text-align: center;"><span id="activebar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmActiveTask.get(strProId + "_ACTIVE_COUNT"), "0")%></span></td>
							<td style="text-align: center;"><span id="overduebar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmOverdueTask.get(strProId + "_OVERDUE_COUNT"), "0")%></span>
								<script type="text/javascript">
								    //$(function() {
								    	 $('#completedbar<%=strProId%>').sparkline(new Array(<%=hmCompleteTask.get(strProId + "_COMPLETE")%>), {type: 'bar', barColor: '#9ACD32'} );
									     $('#activebar<%=strProId%>').sparkline(new Array(<%=hmActiveTask.get(strProId + "_ACTIVE")%>), {type: 'bar', barColor: '#4682B4'} );
									     $('#overduebar<%=strProId%>').sparkline(new Array(<%=hmOverdueTask.get(strProId + "_OVERDUE")%>), {type: 'bar', barColor: '#B22222'} );
								    //});
							    </script>
							</td>
						</tr>
						
						<% } %>
					<% } else { %>
						<tr><td colspan="4"><div class="msg nodata" style="width: 94%;"><span>Projects not available.</span></div> </td></tr>
					<% } %>		
					</table>
					
					<div class="viewmore">
			        	<a href="WorkProgress.action?strProType=<%=strProType %>">
	           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Work Progress.."/> --%>
	           				<i class="fa fa-forward" aria-hidden="true" title="Go to Work Progress.."></i>
	           				
	           				
	           			</a>
	           		</div>
				</div>
			</div>
		<% } %>
		
		
	</div>
	
	<div class="col-lg-4 col-md-6 col-sm-12 connectedSortable">
		<div class="box box-info">
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
					<ul class="site-stats-new">
						<li class="bg_lh"><i class="fa fa-flag" aria-hidden="true"></i><strong><%=uF.parseToInt((String)request.getAttribute("nProject")) %></strong> <small>Live Projects</small></li>
						<li class="bg_lh"><i class="fa fa-tasks" aria-hidden="true"></i><strong><%=uF.parseToInt((String)request.getAttribute("nTask")) %></strong> <small>Live Tasks</small></li>
					</ul>
                </div>
            </div>
        <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_PROJECT_DASH_WEEKLY_WORK_PROGRESS)) && hmFeatureUserTypeId.get(IConstants.F_PROJECT_DASH_WEEKLY_WORK_PROGRESS).contains(strUsertypeId)) { %>
			<div class="box box-primary">
				<div class="box-header with-border">
					<h3 class="box-title">Weekly Work Progress(Overall)</h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
			<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto;">					
					<div id="chartWeeklydiv" style="width:100%; height:400px;"></div>
					<script>
			            var chart6;
			            var chartData6 = [<%=request.getAttribute("sbWork")%>];
			            
			            AmCharts.ready(function () {
			                // SERIAL CHART
			                chart6 = new AmCharts.AmSerialChart();
			
			                chart6.dataProvider = chartData6;
			                chart6.marginTop = 10;
			                chart6.categoryField = "week";
			
			                // AXES
			                // Category
			                var categoryAxis6 = chart6.categoryAxis;
			                categoryAxis6.gridAlpha = 0.07;
			                categoryAxis6.axisColor = "#DADADA";
			                categoryAxis6.startOnAxis = true;
			
			                // Value
			                var valueAxis6 = new AmCharts.ValueAxis();
			                valueAxis6.stackType = "regular"; // this line makes the chart "stacked"
			                valueAxis6.gridAlpha = 0.07;
			                valueAxis6.title = "Number of Tasks";
			                chart6.addValueAxis(valueAxis6);
			
			                // GRAPHS
			                // first graph
			                var graph6 = new AmCharts.AmGraph();
			                graph6.type = "line"; // it's simple line graph
			                graph6.title = "Completed";
			                graph6.valueField = "completed";
			                graph6.lineAlpha = 0;
			                graph6.fillAlphas = 0.6; // setting fillAlphas to > 0 value makes it area graph
			                graph6.balloonText = "<span style='font-size:14px; color:#000000;'>Completed: <b>[[value]]</b></span>";
			               // graph.hidden = true;
			               	graph6.fillColors = "#9ACD32";
			                chart6.addGraph(graph6);
			
			                // second graph
			                graph6 = new AmCharts.AmGraph();
			                graph6.type = "line";
			                graph6.title = "Active";
			                graph6.valueField = "active";
			                graph6.lineAlpha = 0;
			                graph6.fillAlphas = 0.6;
			                graph6.balloonText = "<span style='font-size:14px; color:#000000;'>Active: <b>[[value]]</b></span>";
			               	graph6.fillColors = "#4682B4";
			                chart6.addGraph(graph6);
			
			                // third graph
			                graph6 = new AmCharts.AmGraph();
			                graph6.type = "line";
			                graph6.title = "Overdue";
			                graph6.valueField = "overdue";
			                graph6.lineAlpha = 0;
			                graph6.fillAlphas = 0.6;
			                graph6.balloonText = "<span style='font-size:14px; color:#000000;'>Overdue: <b>[[value]]</b></span>";
			               	graph6.fillColors = "#B22222";
			                chart6.addGraph(graph6);
			
			                // LEGEND
			                var legend6 = new AmCharts.AmLegend();
			                legend6.position = "top";
			                legend6.valueText = "[[value]]";
			                legend6.valueWidth = 100;
			                legend6.valueAlign = "left";
			                legend6.equalWidths = false;
			                legend6.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
			                chart6.addLegend(legend6);
			
			                // CURSOR
			                var chartCursor6 = new AmCharts.ChartCursor();
			                chartCursor6.cursorAlpha = 0;
			                chart6.addChartCursor(chartCursor6);
			
			                // SCROLLBAR
			                var chartScrollbar6 = new AmCharts.ChartScrollbar();
			                chartScrollbar6.color = "#FFFFFF";
			                chart6.addChartScrollbar(chartScrollbar6);
			
			                // WRITE
			                chart6.write("chartWeeklydiv");
			            });
			        </script>
			        
			        <div class="viewmore">
			        	<a href="WorkProgress.action?strProType=<%=strProType %>">
	           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Weekly Work Progress.."/> --%>
	           				<i class="fa fa-forward" aria-hidden="true" title="Go to Weekly Work Progress.."></i>
	           				
	           				
	           			</a>
	           		</div>
				</div>
			</div>
		<% } %>
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Live Projects (Service wise)</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
		<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
    			<div id="chartSbuDonutdiv" style="width:100%; height:400px;"></div>
				<script>
		            var chart4;
		            var chartData4 = [<%=request.getAttribute("sbProSbu")%>];
		            //var legend4;
		
		            AmCharts.ready(function () {
		                var chart = AmCharts.makeChart("chartSbuDonutdiv", {
		                	  "type": "pie",
		                	  "startDuration": 0,
		                	  "labelText" : "",
		                	   "theme": "light",
		                	  "addClassNames": true,
		                	  "innerRadius": "30%",
		                	  "defs": {
		                	    "filter": [{
		                	      "id": "shadow",
		                	      "width": "200%",
		                	      "height": "200%",
		                	      "feOffset": {
		                	        "result": "offOut",
		                	        "in": "SourceAlpha",
		                	        "dx": 0,
		                	        "dy": 0
		                	      },
		                	      "feGaussianBlur": {
		                	        "result": "blurOut",
		                	        "in": "offOut",
		                	        "stdDeviation": 5
		                	      },
		                	      "feBlend": {
		                	        "in": "SourceGraphic",
		                	        "in2": "blurOut",
		                	        "mode": "normal"
		                	      }
		                	    }]
		                	  },
		                	  "allLabels": [{
               			                        "text": "Total Projects",
               			                        "align": "center",
               			                        "bold": true
               			                    },{
               			                        "text": "<%=uF.showData((String)request.getAttribute("nProSbuTotal"),"0") %>",
               			                        "align": "center",
               			                        "bold": true,
               			                        "size": 16,
               			                        "y": 20
               			                    }],
		                	  "dataProvider": [<%=request.getAttribute("sbProSbu")%>],
		                	  "valueField": "project",
		                	  "titleField": "sbu"
		                	});


		            });
		        </script>
			</div>
		</div>
		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_PROJECT_DASH_SKILL_CHART)) && hmFeatureUserTypeId.get(IConstants.F_PROJECT_DASH_SKILL_CHART).contains(strUsertypeId)) { %>
			<div class="box box-primary">
				<div class="box-header with-border">
					<h3 class="box-title">Skill Chart (Resource wise)</h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
			<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto;">
	    			<div id="skillResourceDiv" style="height: 400px; width:100%;"></div>
					<script>
						 var chart5;
				         var chartData5 = [<%=request.getAttribute("sbSkillResource")%>];
				         AmCharts.ready(function () {
				             // SERIAL CHART
				             chart5 = new AmCharts.AmSerialChart();
				             chart5.dataProvider = chartData5;
				             chart5.categoryField = "Skills (Resources)";
				             chart5.startDuration = 1;
				             chart5.plotAreaBorderColor = "#DADADA";
				             chart5.plotAreaBorderAlpha = 1;
				             // this single line makes the chart a bar chart
				            chart5.rotate = false;
		
				             // AXES
				             // Category
				             var categoryAxis5 = chart5.categoryAxis;
				             categoryAxis5.gridPosition = "start";
				             categoryAxis5.gridAlpha = 0.1;
				             categoryAxis5.axisAlpha = 0;
		
				             // Value
				             var valueAxis5 = new AmCharts.ValueAxis();
				             valueAxis5.axisAlpha = 0;
				             valueAxis5.gridAlpha = 0.1;
				             valueAxis5.position = "top";
				             chart5.addValueAxis(valueAxis5);
		
				             <%
				             List<String> alSkillGraph = (List<String>)request.getAttribute("alSkillGraph");
				             if(alSkillGraph == null) alSkillGraph = new ArrayList<String>();
				             for(int i = 0; i < alSkillGraph.size(); i++){
				            	 String strGraph = alSkillGraph.get(i);
				             %>
					             <%=strGraph%>
							<%}%>
		
				             // LEGEND
				             var legend5 = new AmCharts.AmLegend();
				             chart5.addLegend(legend5);
		
				             chart5.creditsPosition = "top-right";
		
				             // WRITE
				             chart5.write("skillResourceDiv");
				         });
			        </script>
				</div>
			</div>
		<% } %>	
	</div>
	</section>
</div>
</section>