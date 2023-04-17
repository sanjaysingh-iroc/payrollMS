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
 
<script>
	$(function() {
	    $("#strStartDate").datepicker({format: 'dd/mm/yyyy'});
	    $("#strEndDate").datepicker({format: 'dd/mm/yyyy'});
	     
	    var value = document.getElementById("selectOne").value; 
	    checkSelectType(value);
	});
    
    
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
    
    
    function submitForm(type) {
    	//strProType f_org
    	var data = "";
    	if(type == '1') {
    		var f_org = document.getElementById("f_org").value;
    		var strProType = '';
    		if(document.getElementById("strProType")) {
    			strProType = document.getElementById("strProType").value;
    		}
    		data = '&f_org='+f_org+'&strProType='+strProType;
    	} else if(type == '2') {
    		data = $("#frmProjectCosting").serialize();
    	}
    	//alert(data);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'ProjectCosting.action?btnSubmit=Submit',
    		data: data,
    		success: function(result){
            	$("#divResult").html(result);
            	$("#f_strWLocation").multiselect().multiselectfilter();
            	$("#f_department").multiselect().multiselectfilter();
            	$("#f_service").multiselect().multiselectfilter();
            	$("#f_level").multiselect().multiselectfilter();
            	$("#f_project_service").multiselect().multiselectfilter();
            	$("#f_client").multiselect().multiselectfilter();
       		}
    	});
    }
</script>


<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>

<script type="text/javascript">
$(function(){
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
	
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
%>


<section class="content">
		<!-- title row -->
	<div class="row">
		<section class="content">
			<div class="col-lg-12 col-md-12 col-sm-12 box box-body">

				<s:form name="frmProjectCosting" id="frmProjectCosting" action="ProjectCosting" theme="simple">
					<s:hidden name="strType" id="strType" />
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
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" onchange="submitForm('1');"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">SBU</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Service</p>
										<s:select name="f_project_service" id="f_project_service" list="projectServiceList" listKey="serviceId" listValue="serviceName" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Client</p>
										<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" />
									</div>
							
								</div>
							</div>
							
							<div class="row row_without_margin" style="margin-top: 10px;">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Select Period</p> 
										<s:select theme="simple" name="selectOne" id="selectOne" cssStyle="float:left; margin-right: 10px;" headerKey="" 
											headerValue="Select Period" list="#{'1':'From-To', '2':'Financial Year', '3':'Month', '4':'Paycycle'}" onchange="checkSelectType(this.value);"/> <!--   -->
									</div>
									
									<div id="fromToDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="text" name="strStartDate" id="strStartDate" placeholder="From Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strStartDate"), "") %>"/>
										<input type="text" name="strEndDate" id="strEndDate" placeholder="To Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strEndDate"), "") %>"/>
						      		</div>
						      		
						      		<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
										<p style="padding-left: 5px;">Financial Year</p>
										<s:select label="Select PayCycle" name="financialYear" listKey="financialYearId" listValue="financialYearName"
											headerValue="Select Financial Year" list="financialYearList" />
						      		</div>
						      		
						      		<div id="monthDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
										<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
										<s:select label="Select PayCycle" name="financialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" /> 
										<s:select name="strMonth" cssStyle="margin-left: 7px; width: 100px !important;" listKey="monthId" listValue="monthName" list="monthList" />	
						      		</div>
						      		
						      		<div id="paycycleDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId" listValue="paycycleName" headerValue="Select Paycycle" list="paycycleList"/>
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


				<div class="col-lg-12 col-md-12 col-sm-12">
					<div id="chartdiv" style="width:100%; height:600px;"></div>
					<script>
						var chart;
				
				        var chartData = [<%=request.getAttribute("sbProCosting")%>];
				        configChart = function() {
				        /* AmCharts.ready(function () { */
				            // SERIAL CHART
				            chart = new AmCharts.AmSerialChart();
				            chart.dataProvider = chartData;
				            chart.categoryField = "project";
				            chart.plotAreaBorderAlpha = 0.2;
				
				            // AXES
				            // category
				            var categoryAxis = chart.categoryAxis;
				            categoryAxis.autoGridCount = false;
				            categoryAxis.gridCount = chartData.length;
				            categoryAxis.gridAlpha = 0.1;
				            categoryAxis.axisAlpha = 0;
				            categoryAxis.gridPosition = "start";
				            categoryAxis.labelRotation = 90;
				
				            // value
				            var valueAxis = new AmCharts.ValueAxis();
				            valueAxis.stackType = "regular";
				            valueAxis.gridAlpha = 0.1;
				            valueAxis.axisAlpha = 0;
				            chart.addValueAxis(valueAxis);
				
				            // GRAPHS
							var graph1 = new AmCharts.AmGraph();
							graph1.title = 'salary';
							graph1.labelText = '[[value]]';
							graph1.valueField = 'salary';
							graph1.type = 'column';
							graph1.lineAlpha = 0;
							graph1.fillAlphas = 1;
							graph1.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
							chart.addGraph(graph1);  
				
							var graph2 = new AmCharts.AmGraph();
							graph2.title = 'reimbursement';
							graph2.labelText = '[[value]]';
							graph2.valueField = 'reimbursement';
							graph2.type = 'column';
							graph2.lineAlpha = 0;
							graph2.fillAlphas = 1;
							graph2.balloonText = "<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>";
							chart.addGraph(graph2);
				
				            // LEGEND
				            var legend = new AmCharts.AmLegend();
				            legend.borderAlpha = 0.2;
				            legend.horizontalGap = 10;
				            chart.addLegend(legend);
				
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
	
			</div>
		</section>
	</div>
</section>

</div>