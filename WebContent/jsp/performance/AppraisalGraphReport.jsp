<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>

<script type="text/javascript" charset="utf-8">
	$(function() {
		$('#lt').DataTable({
			"order": [],
			"columnDefs": [ {
			      "targets"  : 'no-sort',
			      "orderable": false
			    }],
			'dom': 'lBfrtip',
	        'buttons': [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
	  	});
	});

	function checkedLabel(x, strAppId){
   		var  status=x.checked; 
   		var  arr= document.getElementsByName(strAppId);
   		for(i=0;i<arr.length;i++){ 
   	  		arr[i].checked=status;
   	 	}
   	}
	
	
	function checkAll(){
   		var selectall = document.getElementById("selectall");		
   		var strCheck = document.getElementsByName('check');
   		var cnt = 0;
   		var chkCnt = 0;
   		for(var i=0;i<strCheck.length;i++) {
   			cnt++;
   			 if(strCheck[i].checked) {
   				 chkCnt++;
   			 }
   		 }
   		if(cnt == chkCnt) {
   			selectall.checked = true;
   		} else {
   			selectall.checked = false;
   		}
   	}
	

	function getGraph() {
		var checkboxes = document.getElementsByName("check");
		var len = document.getElementsByName("check").length;
		if (len == 0) {
			alert("Please select atleast one Appraisal");
		} else {
			//alert("len ===>> " + len);
			var id = "";
			for (var i=0; i<len; i++) {
	 		    if (checkboxes[i].checked) {
	 		    	/* var str = $(this).attr('value'); */
	 		    	//alert("len in if ===>> " + len);
	 		    	var str = checkboxes[i].value;
	 		    	//alert("str ===>> " + str);
					/* var idx = str.lastIndexOf("_");
					var sStr = str.substring(idx + 1, str.length); */
					id = id + str + ',';
	 		    }
	 		}
			//alert("id ===>> " + id);
			var freq = document.getElementById("frequency").value;
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url:'AppraisalGraphReport.action?appraisal='+id+'&frequency='+freq,
				data: $("#"+this.id).serialize(),
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
			//window.location = 'AppraisalGraphReport.action?appraisal=' + id;
		}

	}
	
	function getAppraisalGraph(currUserType) {
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url:'AppraisalGraphReport.action?currUserType='+currUserType,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}
	
	function submitForm(currUserType) {
		var freq = document.getElementById("frequency").value;
		
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url:'AppraisalGraphReport.action?currUserType='+currUserType+'&frequency='+freq,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}
</script>

<%
	UtilityFunctions uF=new UtilityFunctions();
	String strUserType = (String) session.getAttribute("USERTYPE");
	
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>
	
<%-- <section class="content">
<div class="row jscroll">
		<section class="col-lg-12 connectedSortable"> --%>
			<% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
				<div class="box box-none nav-tabs-custom">
					<ul class="nav nav-tabs">
						<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>"><a href="javascript:void(0)" onclick="getAppraisalGraph('MYTEAM')" data-toggle="tab">My Team</a></li>
						<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"><a href="javascript:void(0)" onclick="getAppraisalGraph('<%=strBaseUserType %>')" data-toggle="tab"><%=strBaseUserType %></a></li>
					</ul>
			<% }else{ %>
				<div class="box box-none">
			<%} %>
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
						<s:property value="message" />
						<s:form id="frmAppraisalGraphReport" name="frmAppraisalGraphReport" method="POST" action="AppraisalGraphReport" theme="simple">
							<s:hidden name="currUserType" id="currUserType" />
							<div class="box box-default collapsed-box">
								<div class="box-header with-border">
								    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
								    <div class="box-tools pull-right">
								        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
								    </div>
								</div>
								<div class="box-body" style="padding: 5px; overflow-y: auto;">
									<div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Frequency</p>
												<s:select theme="simple" name="frequency" id="frequency" list="frequencyList" headerKey="" headerValue="All Frequency" listKey="id" 
													listValue="name" onchange="submitForm();" />
											</div>
										</div>
									</div>
								</div>
							</div>
						</s:form>
						<%java.util.List<List<String>> couterlist = (java.util.List<List<String>>) request.getAttribute("outerList");
							if(couterlist != null && couterlist.size()>0) {
						%>
							<div class="col-lg-12 col-md-12 col-sm-12" style="margin-bottom: 10px;">
								<input type="button" value="Show Selected Bells Curve" class="btn btn-primary" onclick="getGraph()" />
							</div>
						<% } %>
					<!-- <div class="clr" style="margin-top: 20px;"></div> -->
					
					<% if(couterlist != null && couterlist.size()>0) { %>
						<table class="table table-bordered" id="lt" style="width:100%">
							<% UtilityFunctions uF1 = new UtilityFunctions();%>
							<thead>
								<tr>
									<th class="no-sort"><input type="checkbox" name="selectall" id="selectall" onclick="checkedLabel(this,'check')" /></th>
									<th style="text-align: left;">Appraisal Name</th>
									<th style="text-align: left;">Appraisal Type</th>
									<th style="text-align: left;">Orientation Type</th>
									<th style="text-align: left;">Frequency Type</th>
									<th style="text-align: left;">From Date</th>
									<th style="text-align: left;">End Date</th>
									<th style="text-align: left;">Location</th>
									<th style="text-align: left;">Added By</th>
									<th style="text-align: left;">Entry Date</th>
								</tr>
							</thead>
					
							<tbody>
							<%
							    List alAppraisalIds = (List)request.getAttribute("alAppraisalIds");
							
							    
							  
								for (int i = 0; couterlist != null && i < couterlist.size(); i++) {
									List<String> innerList = couterlist.get(i);
							%>
								<tr id="<%=innerList.get(0)%>">
									<td align="center"><input type="checkbox" value="<%=innerList.get(0)+"::::"+innerList.get(10)%>" <%=((alAppraisalIds!=null && alAppraisalIds.contains(innerList.get(0)+"::::"+innerList.get(10)))?"checked":"")%> id="check" name="check" onclick="checkAll();"/></td>
									<td><%=innerList.get(1)%></td>
									<td><%=innerList.get(3)%></td>
									<td><%=innerList.get(2)%>&deg;</td>
									<td><%=innerList.get(4)%></td>
									<td><%=innerList.get(15)%></td>
									<td><%=innerList.get(16)%></td>
									<td><%=innerList.get(7)%></td>
									<td><%=innerList.get(8)%></td>
									<td><%=innerList.get(9)%></td>
								</tr>
							<%}	%>
							</tbody>
						</table>
						<% } else { %>
							<div class="nodata msg"><span>No data available.</span></div>
						<% } %>

					<div id="chartAppraisaldiv" style="height:400px;margin-left:10px; margin-right: 10px;"></div>
					
							<script type="text/javascript">
							<% 	Map<String, String> appraisalIds = (Map<String, String>) request.getAttribute("appraisalIds");
								if(appraisalIds == null) appraisalIds = new HashMap<String, String>();
								Map<String, String> appraisalMp = (Map<String, String>) request.getAttribute("appraisalMp");
								if(appraisalMp == null) appraisalMp = new HashMap<String, String>();
								//System.out.println("appraisalMp --- ====>>> "+ appraisalMp);
								//System.out.println("sbAppraisalEmpCnt --- ====>>> "+(String)request.getAttribute("sbAppraisalEmpCnt"));
							%>
								var chart2;
						        var graphBill;
				         		var chartData2 = [<%=request.getAttribute("sbAppraisalEmpCnt")%>];
						        AmCharts.ready(function () {
            					// SERIAL CHART
         						chart2 = new AmCharts.AmSerialChart();
					            chart2.dataProvider = chartData2;
            					chart2.marginLeft = 10;
           						chart2.categoryField = "rate";
					            // AXES
           					   // category
           						var categoryAxis2 = chart2.categoryAxis;
					            categoryAxis2.parseDates = false; // as our data is date-based, we set parseDates to true
					            categoryAxis2.dashLength = 3;
					            categoryAxis2.minorGridEnabled = true;
					            categoryAxis2.minorGridAlpha = 0.1;

					            // value
					            var valueAxis2 = new AmCharts.ValueAxis();
					            valueAxis2.axisAlpha = 0;
					            valueAxis2.inside = true;
					            valueAxis2.dashLength = 3;
					            chart2.addValueAxis(valueAxis2);
					            <% 
					            Iterator<String> it = appraisalIds.keySet().iterator();
								while (it.hasNext()) {
									String appraisalId = it.next();
					            %>
						            var appId = '<%=appraisalId+"_empCnt" %>';
						            var appName = '<%=appraisalMp.get(appraisalId) %>';
						            //alert("appId ===>> " + appId);
						            // GRAPH
						            graphBill = new AmCharts.AmGraph();
						            graphBill.type = "smoothedLine"; // this line makes the graph smoothed line.
						            graphBill.title = appName;
						            graphBill.valueField = appId;
						            graphBill.bullet = "round";
						            graphBill.bulletSize = 8;
						            graphBill.bulletBorderColor = "#FFFFFF";
						            graphBill.bulletBorderAlpha = 1;
						            graphBill.bulletBorderThickness = 2;
						            graphBill.lineThickness = 2;
						            graphBill.balloonText = "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>";
						            chart2.addGraph(graphBill);
						      <% } %>
            
            			            // CURSOR
						            var chartCursor2 = new AmCharts.ChartCursor();
						            chartCursor2.cursorAlpha = 0;
						            chartCursor2.cursorPosition = "mouse";
						            chartCursor2.categoryBalloonDateFormat = "YYYY";
						            chart2.addChartCursor(chartCursor2);
						            
						        	// LEGEND
						            var legend2 = new AmCharts.AmLegend();
						            legend2.position = "top";
						            legend2.valueText = "[[value]]";
						            legend2.valueWidth = 100;
						            legend2.valueAlign = "left";
						            legend2.equalWidths = false;
						            legend2.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
						            chart2.addLegend(legend2); 

						            // SCROLLBAR
						            var chartScrollbar2 = new AmCharts.ChartScrollbar();
						            chart2.addChartScrollbar(chartScrollbar2);
						
						            chart2.creditsPosition = "bottom-right";
						            
						            // WRITE
						            chart2.write("chartAppraisaldiv");
						            
						        });
						</script>
                </div>
                <!-- /.box-body -->
            </div>
<%-- 		</section>
	</div>
</section> --%>

