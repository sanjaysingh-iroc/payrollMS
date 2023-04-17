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

<script type="text/javascript">
function submitForm(type){
	var data = "";
	/* if(type == '1') {
		var outstandingFrom = document.getElementById("outstandingFrom").value;
		data = '&outstandingFrom='+outstandingFrom;
	} else if(type == '2') { */
		data = $("#frmOutstandingBalance").serialize();
	/* } */
	//alert(data);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'OutstandingBalance.action?btnSubmit=Submit',
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

				<s:form theme="simple" name="frmOutstandingBalance" id="frmOutstandingBalance" action="OutstandingBalance" method="POST" cssClass="formcss">
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
									<% boolean poFlag = (Boolean) request.getAttribute("poFlag"); %>
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Period</p>
										<s:select theme="simple" name="outstandingFrom" id="outstandingFrom" headerKey="1" headerValue="Since last 1 Year" list="#{'2':'Since last 6 months','3':'Since last 3 months','4':'Since last 1 month'}"
											onchange="submitForm('1');"/>
									</div>
									
						      		<% if(poFlag || (strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER)))) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Project Type</p>
											<s:select theme="simple" name="strProType" id="strProType" headerKey="1" headerValue="All Projects" list="#{'2':'My Projects'}" onchange="submitForm('2');"/>
										</div>
									<% } %>
								</div>
							</div>
						</div>
					</div>
				</s:form>


				<div class="col-lg-12 col-md-12 col-sm-12">
				<% if(((String)request.getAttribute("sbOutstanding")) !=null && ((String)request.getAttribute("sbOutstanding")).length()>0) { %>
					<div id="chartdiv" style="width:100%; height:600px;"></div>
					<script>
						var chart;
				        var chartData = [<%=request.getAttribute("sbOutstanding")%>];
				
				        AmCharts.ready(function () {
				            // SERIAL CHART
				            chart = new AmCharts.AmSerialChart();
				            chart.dataProvider = chartData;
				            chart.categoryField = "month";
				            chart.plotAreaBorderAlpha = 0.2;
				
				            // AXES
				            // category
				            var categoryAxis = chart.categoryAxis;
				            categoryAxis.gridAlpha = 0.1;
				            categoryAxis.axisAlpha = 0;
				            categoryAxis.gridPosition = "start";
				
				            // value
				            var valueAxis = new AmCharts.ValueAxis();
				            valueAxis.stackType = "regular";
				            valueAxis.gridAlpha = 0.1;
				            valueAxis.axisAlpha = 0;
				            chart.addValueAxis(valueAxis);
				
				            // GRAPHS
				            <%
				            List<String> alOutStanding = (List<String>)request.getAttribute("alOutStanding");
				            if(alOutStanding == null) alOutStanding = new ArrayList<String>();
				            for(int i = 0; i < alOutStanding.size(); i++){
				           	 String strGraph = alOutStanding.get(i);
				            %>
					             <%=strGraph%>
							<%}%>
				
				            // LEGEND
				            var legend = new AmCharts.AmLegend();
				            legend.borderAlpha = 0.2;
				            legend.horizontalGap = 10;
				            chart.addLegend(legend);
				
				            // WRITE
				            chart.write("chartdiv");
				        });
			        </script>
			     <% } else { %>
			     	<div class="msg nodata" style="width: 98%;"><span>No bills generated.</span></div>
			     <% } %>   
				</div>
	
			</div>
		</section>
	</div>
</section>

</div>