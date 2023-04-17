<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*, ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/jquery.lazyload.js"></script>

<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script> 


<%-- <script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script>
<script type="text/javascript" src="js/jquery.sparkline.js"></script>
 
<script src="scripts/charts/justgage/raphael-2.1.4.min.js" type="text/javascript"></script>
<script src="scripts/charts/justgage/justgage.js" type="text/javascript"></script>
 --%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	
	String pageFrom = (String) request.getAttribute("pageFrom");
	String strProOwnerOrTL = (String) request.getAttribute("strProOwnerOrTL");
	String strCurr = (String) request.getAttribute("strCurr");
	
%>
 

<script>

jQuery(document).ready(function() { 
	  jQuery(".content1").show();
	  //toggle the componenet with class msg_body
	  jQuery(".heading").click(function()
	  {
	    jQuery(this).next(".content1").slideToggle(500);
	    $(this).toggleClass("close_div");
	  });
	});

</script> 
<%-- <%if(pageFrom==null || !pageFrom.trim().equalsIgnoreCase("Project")){ %>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="<%=strTitle %>" name="title"/>
	</jsp:include>
<%} %> --%>

<%-- <%if(pageFrom==null || !pageFrom.trim().equalsIgnoreCase("Project")){ %>
<div class="leftbox reportWidth">
<%} %> --%>

	<div class="row">
		
		<div class="col-lg-6 col-md-6 col-sm-12 paddingright0">
			<div class="box box-default">
            	<% List<Map<String, String>> alProTask = (List<Map<String, String>>) request.getAttribute("alProTask"); %>
                <div class="box-header with-border" data-widget="collapse-full">
                	<h3 class="box-title">Project Tasks</h3>
                  <div class="box-tools pull-right">
                  	<span class="badge bg-gray"><%=alProTask.size() %></span>
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                  </div>
                </div><!-- /.box-header -->
                
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                	<table class="table table-hover" style="font-size: 12px;">
					<% if(alProTask != null && !alProTask.isEmpty()) { %>
						<tr>
							<th>Task Name</th>
							<th>Deadline</th>  
							<th>Estimated Time<br/>(hrs)</th>
							<th>Time Spent<br/>(hrs)</th>
							<th>Indicator</th>
						</tr>
					<%
						for(int i = 0; i < alProTask.size(); i++){
							Map<String, String> hmProTask = (Map<String, String>) alProTask.get(i);
							String strTaskBullet = uF.showData(hmProTask.get("TASK_SPENT_TIME"),"0")+","+uF.showData(hmProTask.get("TASK_EST_TIME"),"0");
					%>
							<tr>
								<td><%=hmProTask.get("TASK_NAME") %></td>
								<td><span class="myTaskbullet" id="myTaskbullet<%=hmProTask.get("TASK_ID")%>"><%=strTaskBullet %></span>
									<script type="text/javascript">
									    $(function() {
									    	 $('#myTaskbullet<%=hmProTask.get("TASK_ID")%>').sparkline(new Array(<%=strTaskBullet %>), {type: 'bullet',targetColor: '#b2b2b2',performanceColor: '#9acd32'});
									    });
								    </script>
								</td>
								<td class="alignRight padRight20"><%=hmProTask.get("TASK_EST_TIME") %></td>
								<td class="alignRight padRight20"><%=hmProTask.get("TASK_SPENT_TIME") %></td>
								<td align="center"><%=hmProTask.get("TASK_TIME_INDICATOR") %></td>
							</tr>
						<%} %> 
					<%} else { %>
						<tr><td colspan="3"><div class="msg nodata" style="width: 94%;"><span>Tasks not available.</span></div> </td></tr>
					<% } %>
					</table>
                </div>
			</div>
			
			
			<div class="box box-default">
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
                <div class="box-header with-border" data-widget="collapse-full">
                	<h3 class="box-title">Project Efforts</h3>
                  <div class="box-tools pull-right">
                  	<%-- <span class="badge bg-gray"><%=alProTask.size() %></span> --%>
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                  </div>
                </div><!-- /.box-header -->
                
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                	<table class="table table-hover" style="font-size: 12px;">
					<%if (alPeople != null && alPeople.size() > 0) {%>
						<tr>
							<th width="35%">Resource</th>
							<th>Billable</th>
							<th>Non-Billable</th>
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
								<td style="text-align: center;"><span id="billablebar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmBillable.get(strEmpId + "_BILLABLE_COUNT"),"0")%></span></td>
								<td style="text-align: center;"><span id="nonBillablebar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmNonBillable.get(strEmpId + "_NON_BILLABLE_COUNT"), "0")%></span>
									<script type="text/javascript">
									    $(function() {
									    	 $('#billablebar<%=strEmpId%>').sparkline(new Array(<%=hmBillable.get(strEmpId + "_BILLABLE")%>), {type: 'bar', barColor: '#9ACD32'} );
										     $('#nonBillablebar<%=strEmpId%>').sparkline(new Array(<%=hmNonBillable.get(strEmpId + "_NON_BILLABLE")%>), {type: 'bar', barColor: '#4682B4'} );
									    });
								    </script>
								</td>
							</tr>
						
						<%}%>
					<%} else {%>
						<tr><td colspan="3"><div class="msg nodata" style="width: 94%;"><span>Resources not available.</span></div> </td></tr>
					<%}%>
					</table>
                </div>
			</div>
			
			
			<div class="box box-default">
				<div class="box-header with-border" data-widget="collapse-full">
                	<h3 class="box-title">Tasks Status</h3>
                  <div class="box-tools pull-right">
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                  </div>
                </div><!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                	<div id="chartProDonutdiv" style="width:100%; height:240px;"></div>
					<script>
			            var chart3;
			
			            var chartData3 = [<%=request.getAttribute("sbProTaskDonut")%>];
			            var legend3;
			
			            		tasksStatus = function () {
			                // PIE CHART
			                chart3 = new AmCharts.AmPieChart();
			
			                chart3.allLabels=[{
			                        "text": "Tasks",
			                        "align": "center",
			                        "bold": true,
			                        "y": 150
			                    },{
			                        "text": "",
			                        "align": "center",
			                        "bold": true,
			                        "size": 16,
			                        "y": 180
			                    }];
			                chart3.theme="none";
			                chart3.dataProvider = chartData3;
			                chart3.titleField = "task";
			                chart3.valueField = "count";
			                chart3.sequencedAnimation = true;
			              	chart3.startEffect = "elastic";
			                chart3.innerRadius = "60%";
			                chart3.radius= "42%",
			                chart3.startDuration = 2;
			                chart3.labelRadius = -130; 
			                chart3.labelText = "";
			                chart3.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
			 
			                // LEGEND
			                legend3 = new AmCharts.AmLegend();
			                legend3.align = "center";
			                legend3.markerType = "circle";
			                chart3.addLegend(legend3);
			                
			                // WRITE
			                chart3.write("chartProDonutdiv");
			            }
			            		
			            		if (AmCharts.isReady) {
						        	debugger;
						        	tasksStatus();
						          } else {
						        	  debugger;
						            AmCharts.ready(tasksStatus);
						          }			            		
			            		
			        </script>
                </div>
			</div>

		
		<% if(uF.parseToInt(strProOwnerOrTL) !=2) { %>
			<div class="box box-default">
				<div class="box-header with-border" data-widget="collapse-full">
                	<h3 class="box-title">Project &amp; Business Snapshot</h3>
                  <div class="box-tools pull-right">
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                  </div>
                </div><!-- /.box-header -->
                <div class="box-body" style="padding: 5px;">  <!-- overflow-y: auto; max-height: 350px; -->
					<div style="width: 100%;float: left;">
			          <div style="width: 45%; height: auto; float: left; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblProfit"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Profit</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: right; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=uF.showData((String) request.getAttribute("dblProfitMargin"),"0") %>%</p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Profit Margin</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: left; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblBugedtedAmt"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Budgeted</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: right; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblActualAmt"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Actuals</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: left; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblBilled"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Billed</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: right; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblReceived"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Received</p>                
			          </div>
		          	</div>
		          	
		          	<div id="chartDonutdiv" style="width: 95%;	height: 300px;font-size	: 11px; margin-left: 23px;" align="center"></div>
					<script>
			            var chartData4 = [<%=request.getAttribute("sbBillsDonut")%>];
		            	var chart4 = AmCharts.makeChart( "chartDonutdiv", {
		            	  "theme": "light",
		            	  "type": "serial",
		            	  "depth3D": 100,
		            	  "angle": 30,
		            	  "autoMargins": true,
		            	  "dataProvider": chartData4,
		            	  "valueAxes": [ {
		            	    "stackType": "100%", 
		            	    "gridAlpha": 0
		            	  } ],
		            	  "graphs": [ {
		            	    "type": "column",
		            	    "topRadius": 1,
		            	    "columnWidth": 1,
		            	    "showOnAxis": true,
		            	    "lineThickness": 2,
		            	    "lineAlpha": 0.5,
		            	    "lineColor": "#FFFFFF",
		            	    "fillColors": "#8d003b",
		            	    "fillAlphas": 0.8,
		            	    "valueField": "Received",
		            	    "balloonText" : "<span style='font-size:14px; color:#000000;'>Received: <b>[[value]]%</b></span>"
		            	  }, {
		            	    "type": "column",
		            	    "topRadius": 1,
		            	    "columnWidth": 1,
		            	    "showOnAxis": true,
		            	    "lineThickness": 2,
		            	    "lineAlpha": 0.5,
		            	    "lineColor": "#cdcdcd",
		            	    "fillColors": "#cdcdcd",
		            	    "fillAlphas": 0.5,
		            	    "valueField": "Pending",
		            	    "balloonText" : "<span style='font-size:14px; color:#000000;'>Pending: <b>[[value]]%</b></span>"
		            	  } ],
	
		            	  "categoryField": "category",
		            	  "categoryAxis": {
		            	    "axisAlpha": 0,
		            	    "labelOffset": 40,
		            	    "gridAlpha": 0
		            	  },
		            	  "export": {
		            	    "enabled": true
		            	  }
		            	} );
					</script>
				</div>
			</div>
		<% } %>	
			
			
	    </div>
	    
	    <div class="col-lg-6 col-md-6 col-sm-12">
		    <% if(uF.parseToInt(strProOwnerOrTL) !=2) { %>
				<div class="box box-default">
	            	<% List<Map<String, String>> alBillInvoice = (List<Map<String, String>>) request.getAttribute("alBillInvoice"); %>
	                <div class="box-header with-border" data-widget="collapse-full">
	                	<h3 class="box-title">Billing Status</h3>
	                  <div class="box-tools pull-right">
	                  	<%-- <span class="badge bg-gray"><%=alProTask.size() %></span> --%>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<table class="table table-hover" style="font-size: 12px;">
						<% if(alBillInvoice != null && !alBillInvoice.isEmpty()) { %>
							<tr>
								<th>Invoice No</th>
								<th>Invoice Amount</th>  
								<th>Received Amount</th>
							</tr>
						<%
							Map<String, String> hmReceivedAmt = (Map<String, String>) request.getAttribute("hmReceivedAmt");
							if(hmReceivedAmt == null) hmReceivedAmt = new HashMap<String, String>();
						
							for(int i = 0; i < alBillInvoice.size(); i++){
								Map<String, String> hmInvoice = (Map<String, String>) alBillInvoice.get(i);
						%>
								<tr>
									<td><%=hmInvoice.get("INVOICE_CODE") %></td>
									<td class="alignRight padRight20"><%=uF.showData(hmInvoice.get("INVOICE_AMT"),"0") %></td>
									<td class="alignRight padRight20"><%=uF.showData(hmReceivedAmt.get(hmInvoice.get("INVOICE_ID")),"0") %></td>
								</tr>
							<%} %> 
						<%} else { %>
							<tr><td colspan="3"><div class="msg nodata" style="width: 94%;"><span>Invoice not available.</span></div> </td></tr>
						<% } %>
						</table>
	                </div>
				</div>
	    	<% } %>
	    	
	    	
	    	<% if(uF.parseToInt(strProOwnerOrTL) !=2) { %>
				<div class="box box-default">
	                <div class="box-header with-border" data-widget="collapse-full">
	                	<h3 class="box-title">Project Expenses</h3>
	                  <div class="box-tools pull-right">
	                  	<%-- <span class="badge bg-gray"><%=alProTask.size() %></span> --%>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div id="chartProCostdiv" style="width:100%; min-height:240px;"></div>
						<script>
						debugger;
						var chart1;
				        var chartData1 = [<%=request.getAttribute("sbProCosting")%>];
				
				        configChart =	function () {
				        	debugger;
				            // SERIAL CHART
				            chart1 = new AmCharts.AmSerialChart();
				            chart1.dataProvider = chartData1;
				            chart1.categoryField = "project";
				            chart1.plotAreaBorderAlpha = 0.2;
				
				            // AXES
				            // category
				            var categoryAxis1 = chart1.categoryAxis;
				            categoryAxis1.gridAlpha = 0.1;
				            categoryAxis1.axisAlpha = 0;
				            categoryAxis1.gridPosition = "start";
				
				            // value
				            var valueAxis1 = new AmCharts.ValueAxis();
				            valueAxis1.stackType = "regular";
				            valueAxis1.gridAlpha = 0.1;
				            valueAxis1.axisAlpha = 0;
				            chart1.addValueAxis(valueAxis1);
				
				            // GRAPHS
							var graph1 = new AmCharts.AmGraph();
							graph1.title = 'salary';
							graph1.labelText = '[[value]]';
							graph1.valueField = 'salary';
							graph1.type = 'column';
							graph1.lineAlpha = 0;
							graph1.fillAlphas = 1;
							graph1.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
							chart1.addGraph(graph1);  
				
							graph1 = new AmCharts.AmGraph();
							graph1.title = 'reimbursement';
							graph1.labelText = '[[value]]';
							graph1.valueField = 'reimbursement';
							graph1.type = 'column';
							graph1.lineAlpha = 0;
							graph1.fillAlphas = 1;
							graph1.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
							chart1.addGraph(graph1);
				
				            // LEGEND
				            var legend1 = new AmCharts.AmLegend();
				            legend1.borderAlpha = 0.2;
				            legend1.horizontalGap = 10;
				            chart1.addLegend(legend1);
				
				            // WRITE
				            chart1.write("chartProCostdiv");
				        }
				
				        if (AmCharts.isReady) {
				        	debugger;
				            configChart();
				          } else {
				        	  debugger;
				            AmCharts.ready(configChart);
				          }
				        
				        </script>
	                </div>
				</div>
	    	<% } %>
	    
	    
	    	<div class="box box-default">
                <div class="box-header with-border" data-widget="collapse-full">
                	<h3 class="box-title">Team Weekly Work Progress(Overall)</h3>
                  <div class="box-tools pull-right">
                  	<%-- <span class="badge bg-gray"><%=alProTask.size() %></span> --%>
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                  </div>
                </div><!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                	<div id="chartWeeklydiv" style="width:100%; height:240px;"></div>
					<script>
					debugger;
			            var chart2;
			            var chartData2 = [<%=request.getAttribute("sbWork")%>];
			            
			            teamWeeklyProgress = function () {
			            	debugger;
			                // SERIAL CHART
			                chart2 = new AmCharts.AmSerialChart();
			
			                chart2.dataProvider = chartData2;
			                chart2.marginTop = 10;
			                chart2.categoryField = "week";
			
			                // AXES
			                // Category
			                var categoryAxis2 = chart2.categoryAxis;
			                categoryAxis2.gridAlpha = 0.07;
			                categoryAxis2.axisColor = "#DADADA";
			                categoryAxis2.startOnAxis = true;
			
			                // Value
			                var valueAxis2 = new AmCharts.ValueAxis();
			                valueAxis2.stackType = "regular"; // this line makes the chart "stacked"
			                valueAxis2.gridAlpha = 0.07;
			                valueAxis2.title = "Number of Tasks";
			                chart2.addValueAxis(valueAxis2);
			
			                // GRAPHS
			                // first graph
			                var graph2 = new AmCharts.AmGraph();
			                graph2.type = "line"; // it's simple line graph
			                graph2.title = "Completed";
			                graph2.valueField = "completed";
			                graph2.lineAlpha = 0;
			                graph2.fillAlphas = 0.6; // setting fillAlphas to > 0 value makes it area graph
			                graph2.balloonText = "<span style='font-size:14px; color:#000000;'>Completed: <b>[[value]]</b></span>";
			               // graph2.hidden = true;
			               	graph2.fillColors = "#9ACD32";
			                chart2.addGraph(graph2);
			
			                // second graph
			                graph2 = new AmCharts.AmGraph();
			                graph2.type = "line";
			                graph2.title = "Active";
			                graph2.valueField = "active";
			                graph2.lineAlpha = 0;
			                graph2.fillAlphas = 0.6;
			                graph2.balloonText = "<span style='font-size:14px; color:#000000;'>Active: <b>[[value]]</b></span>";
			               	graph2.fillColors = "#4682B4";
			                chart2.addGraph(graph2);
			
			                // third graph
			                graph2 = new AmCharts.AmGraph();
			                graph2.type = "line";
			                graph2.title = "Overdue";
			                graph2.valueField = "overdue";
			                graph2.lineAlpha = 0;
			                graph2.fillAlphas = 0.6;
			                graph2.balloonText = "<span style='font-size:14px; color:#000000;'>Overdue: <b>[[value]]</b></span>";
			               	graph2.fillColors = "#B22222";
			                chart2.addGraph(graph2);
			
			                // LEGEND
			                var legend2 = new AmCharts.AmLegend();
			                legend2.position = "top";
			                legend2.valueText = "[[value]]";
			                legend2.valueWidth = 100;
			                legend2.valueAlign = "left";
			                legend2.equalWidths = false;
			                legend2.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
			                chart2.addLegend(legend2);
			
			                // CURSOR
			                var chartCursor2 = new AmCharts.ChartCursor();
			                chartCursor2.cursorAlpha = 0;
			                chart2.addChartCursor(chartCursor2);
			
			                // SCROLLBAR
			                var chartScrollbar2 = new AmCharts.ChartScrollbar();
			                chartScrollbar2.color = "#FFFFFF";
			                chart2.addChartScrollbar(chartScrollbar2);
			
			                // WRITE
			                chart2.write("chartWeeklydiv");
			            }
			            
			            if (AmCharts.isReady) {
				        	debugger;
				        	teamWeeklyProgress();
				          } else {
				        	  debugger;
				            AmCharts.ready(teamWeeklyProgress);
				          }
			            
			        </script>
                </div>
			</div>
			
	    
	    	<div class="box box-default">
				<%
				List<Map<String, String>> alTeamMember = (List<Map<String, String>>) request.getAttribute("alTeamMember");
				if(alTeamMember == null) alTeamMember = new ArrayList<Map<String, String>>(); 
				%>
                <div class="box-header with-border" data-widget="collapse-full">
                	<h3 class="box-title">Project Team Members</h3>
                  <div class="box-tools pull-right">
                  	<span class="badge bg-gray"><%=alTeamMember.size() %></span>
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                  </div>
                </div><!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                	<div style="width:100%; float:left;">
					<%
						for(int i = 0; i < alTeamMember.size(); i++){
							Map<String, String> hmInner = (Map<String, String>) alTeamMember.get(i);
					%>
						<%if(docRetriveLocation==null) { %>
							<img height="60px" width="60px" class="lazy img-circle" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmInner.get("EMP_IMAGE")%>" title="<%=hmInner.get("EMP_NAME") %>"/>
						<%} else { %>
							<img height="60px" width="60px" class="lazy img-circle" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmInner.get("EMP_ID")+"/"+IConstants.I_60x60+"/"+hmInner.get("EMP_IMAGE")%>" title="<%=hmInner.get("EMP_NAME") %>"/>
						<%} %>    
					<%} %>
					<% if(alTeamMember == null || alTeamMember.isEmpty() || alTeamMember.size() == 0) { %>
						<span>No team availabel.</span>
					<% } %>
					</div>
                </div>
			</div>
	    
	    
	    	<div class="box box-default">
				<div class="box-header with-border" data-widget="collapse-full">
                	<h3 class="box-title">Team & Tasks Snapshot</h3>
                  <div class="box-tools pull-right">
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                  </div>
                </div><!-- /.box-header -->
                
                <div class="box-body" style="padding: 5px;">  <!-- overflow-y: auto; max-height: 350px; -->
					<div style="width: 98%;float: left;">
			          <div style="width: 45%; height: auto; float: left; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=alTeamMember.size() %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Team Members</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: right; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=uF.showData((String) request.getAttribute("nMemAssigned"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Task Assigned</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: right; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=uF.showData((String) request.getAttribute("nMemUnAssigned"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Task Un-Assigned</p>                
			          </div>
		          	</div>
		          	
		          	<div style="width: 100%; border-bottom: 1px solid #C4C4C4; float: left;"></div>
		          	
		          	<div style="width: 98%;float: left;">
			          <div style="width: 45%; height: auto; float: left; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=uF.showData((String) request.getAttribute("nTotalTask"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Tasks/ Issues</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: right; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=uF.showData((String) request.getAttribute("nTaskAssigned"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Assigned</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: right; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=uF.showData((String) request.getAttribute("nTaskUnAssigned"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Un-Assigned</p>                
			          </div>
		          	</div>
		          	
		          	<div style="width: 100%; border-bottom: 1px solid #C4C4C4; float: left;"></div>
		          	
		          	<div style="width: 98%;float: left;">
			         <div style="width: 45%; height: auto; float: left; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=uF.showData((String) request.getAttribute("nTotalMilestone"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Total Milestones</p>                
			          </div>
			          
			          <div style="width: 45%; height: auto; float: right; padding: 5px;">
			               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; color: #C4C4C4;"><%=uF.showData((String) request.getAttribute("nAchievedMilestone"),"0") %></p>             
			               <p style="font-size: 14px; margin: 0px; text-align: right;">Achieved Milestones</p>                
			          </div>
		          	</div>
                </div>
			</div>
	    
		</div>
		
		
	    
			
	
		
		
	</div>
	
<%-- <%if(pageFrom==null || !pageFrom.trim().equalsIgnoreCase("Project")){ %>
</div>
<%} %> --%>


<script type="text/javascript">
	$("img.lazy").lazyload({event : "sporty", threshold : 200, effect : "fadeIn", failure_limit : 10});
	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});
</script>
