<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<div id="divResult">


<%String btnSubmit = (String)request.getAttribute("btnSubmit");
	if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) {
%>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script>
<% } %>

<script type="text/javascript" src="js/jquery.sparkline.js"></script>

<!-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" /> -->
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>

<script type="text/javascript">
/*$(document).ready(function() { 
	$("body").on('click','#closeButton',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});

});*/


function viewSummary(id) {
	/* var dialogEdit = '#viewsummary';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false,
		height : 820,
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
	$(dialogEdit).dialog('open'); */
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Project Summary');
	$.ajax({
		url : 'ProjectSummaryView.action?pro_id='+id,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
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
 
 
	
	
	function submitForm(type) {
		//strProType f_org
		var data = "";
		if(type == '1') {
			var f_org = document.getElementById("f_org").value;
			var strProType = '';
			if(document.getElementById("strProType")) {
				strProType = document.getElementById("strProType").value;
			}
			var proType = document.getElementById("proType").value;
			data = 'f_org='+f_org+'&strProType='+strProType+'&proType='+proType;
		} else if(type == '2') {
			data = $("#frmProjectPerformanceReportWP").serialize();
		} else {
			data = 'proType='+type;
		}
		
		//alert(data);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ProjectPerformanceReportWP.action?btnSubmit=Submit',
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
	
	
	function loadMore(proPage, minLimit) {
		
		document.getElementById("proPage").value = proPage;
		document.getElementById("minLimit").value = minLimit;
		var form_data = $("#frmProjectPerformanceReportWP").serialize();
		//alert(form_data);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ProjectPerformanceReportWP.action?btnSubmit=Submit',
			data: form_data,
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
	
	$(function() {
		
		$("body").on('click','#closeButton',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
	    });
		$("body").on('click','.close',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
		});
	});
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
	<section class="content">
          <!-- title row -->
        <div class="row"> 
			<div class="col-md-12">
			<%  
				UtilityFunctions uF = new UtilityFunctions();
				String proType = (String)request.getAttribute("proType");
				String proCount = (String)request.getAttribute("proCount");
			%>
				
			<div class="nav-tabs-custom">
				<ul class="nav nav-tabs">
					<li class="<%=((proType == null || proType.equalsIgnoreCase("null") || proType.equalsIgnoreCase("L")) ? "active" : "") %>"><a href="javascript:void(0);" style="padding: 5px;" onclick="submitForm('L')" data-toggle="tab">Working Projects</a></li>
					<li class="<%=((proType != null && proType.equalsIgnoreCase("C")) ? "active" : "") %>"><a href="javascript:void(0);" style="padding: 5px;" onclick="submitForm('C')" data-toggle="tab">Completed Projects</a></li>
				</ul>
			
			<div class="tab-content box-body">
				<s:form name="frmProjectPerformanceReportWP" id="frmProjectPerformanceReportWP" action="ProjectPerformanceReportWP" theme="simple">
					<s:hidden name="proType" id="proType" />
					<s:hidden name=" proPage" id="proPage" />
					<s:hidden name="minLimit" id="minLimit" />
					<div class="box box-default collapsed-box" style="margin-bottom: 0px;">
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
										if(poFlag) {
									%>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Project Type</p> 
											<s:select theme="simple" name="strProType" id="strProType" headerKey="1" headerValue="All Projects"
								                list="#{'2':'My Projects'}" onchange="submitForm('1');"/>
										</div>
									<%} %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organisation</p> 
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" 
											onchange="submitForm('1');" list="organisationList" />
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
									
									<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" multiple="true"/>
									</div> --%>
									
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
										<s:select theme="simple" name="selectOne" id="selectOne" headerKey="" headerValue="Select Period" list="#{'1':'From-To', '2':'Financial Year', '3':'Month', '4':'Paycycle'}" onchange="checkSelectType(this.value);"/>
									</div>
									
									<div id="fromToDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display:none;">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="text" name="strStartDate" id="strStartDate" placeholder="From Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strStartDate"), "") %>"/>
										<input type="text" name="strEndDate" id="strEndDate" placeholder="To Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strEndDate"), "") %>"/>
						      		</div>
						      		
						      		<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display:none;">
										<p style="padding-left: 5px;">Financial Year</p>
										<s:select label="Select PayCycle" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName"
											headerValue="Select Financial Year" list="financialYearList" />
						      		</div>
						      		
						      		<div id="monthDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display:none;">
										<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
										<s:select label="Select PayCycle" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName"
											headerValue="Select Financial Year" list="financialYearList" /> 
										<s:select name="strMonth" id="strMonth" cssStyle="margin-left: 7px; width: 100px !important;" listKey="monthId" listValue="monthName" list="monthList" />	
						      		</div>
						      		
						      		<div id="paycycleDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display:none;">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName"
											headerValue="Select Paycycle" list="paycycleList" />
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
	

		<div>
			<jsp:include page="/jsp/task/ProjectPerformanceChart.jsp"></jsp:include> 
		</div>
		
		<br/>
	<%
		Map hmProPerformaceBillable = (Map)request.getAttribute("hmProPerformaceBillable"); 
		Map hmProPerformaceActual = (Map)request.getAttribute("hmProPerformaceActual");
		Map hmProPerformaceBudget = (Map)request.getAttribute("hmProPerformaceBudget");
		Map hmProPerformaceActualTime = (Map)request.getAttribute("hmProPerformaceActualTime");
		Map hmProPerformaceIdealTime = (Map)request.getAttribute("hmProPerformaceIdealTime");
		Map hmProPerformaceProjectName = (Map)request.getAttribute("hmProPerformaceProjectName");
		Map<String, String> hmProPerformaceCurrency = (Map<String, String>)request.getAttribute("hmProPerformaceCurrency");
		
		Map hmProPerformaceProjectManager = (Map)request.getAttribute("hmProPerformaceProjectManager");
		Map hmProPerformaceProjectProfit = (Map)request.getAttribute("hmProPerformaceProjectProfit");
		Map hmProPerformaceProjectAmountIndicator = (Map)request.getAttribute("hmProPerformaceProjectAmountIndicator");
		Map hmProPerformaceProjectTimeIndicator = (Map)request.getAttribute("hmProPerformaceProjectTimeIndicator");
		Map hmProjectClient = (Map)request.getAttribute("hmProjectClient");
		List alProjectId = (List)request.getAttribute("alProjectId");
		
		Map<String, String> hmProActIdealTimeHRS = (Map<String, String>)request.getAttribute("hmProActIdealTimeHRS");
		if(hmProActIdealTimeHRS == null) hmProActIdealTimeHRS = new HashMap<String, String>();
		
	%>


		<table class="table table-bordered">
			<% if(alProjectId != null && !alProjectId.isEmpty()) { %>
				<tr>
					<th width="20%" rowspan="2">Project Name</th>
					<th width="10%" rowspan="2">Manager/Owner</th>
					<th width="10%" rowspan="2">Client</th>
					<th width="40%" colspan="4">Money</th>
					<td width="1%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="30%" colspan="4">Time</th>
				</tr>
				
				<tr>
					<th width="10%">Budgeted</th> <%-- <br/>(<%=CF.getStrCURRENCY_SHORT() %>) --%>
					<th width="10%">Actual Amount</th> 
					<th width="10%">Billable Amount</th>
					<!-- <th width="10%">Profit Margin<br/>(%)</th> -->
					<th width="10%">Indicator</th>
					<td width="1%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="10%">Deadline</th>
					<th width="10%">Estimated Time<br/>(days/hrs/mths)</th>
					<th width="10%">Time Spent<br/>(days/hrs/mths)</th>
					<th width="10%">Indicator</th>
				</tr>
				<%
					for(int i=0; alProjectId != null && !alProjectId.isEmpty() && i<alProjectId.size(); i++){
						String strBullet = uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_ACT_TIME_HRS"),"0")+","+uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_IDEAL_TIME_HRS"),"0");
				%>
				<tr>
					<td><a href="javascript:void(0)" onclick="viewSummary(<%=(String)alProjectId.get(i)%>)"> <%=hmProPerformaceProjectName.get((String)alProjectId.get(i)) %></a></td>
					<td> <%=hmProPerformaceProjectManager.get((String)alProjectId.get(i)) %></td>
					<td> <%=hmProjectClient.get((String)alProjectId.get(i)) %></td>
					<td class="alignRight padRight20">
					<%-- <span style="float: left;"> <%=hmProPerformaceCurrency.get((String)alProjectId.get(i)) %> </span> --%>
					<%=hmProPerformaceCurrency.get((String)alProjectId.get(i)) %> 
					<a href="javascript:void(0)" onclick="viewBudgetedSummary(<%=(String)alProjectId.get(i)%>)"><%=hmProPerformaceBudget.get((String)alProjectId.get(i)) %></a></td>
					<td class="alignRight padRight20">
					<%=hmProPerformaceCurrency.get((String)alProjectId.get(i)) %> 
					<%=hmProPerformaceActual.get((String)alProjectId.get(i)) %></td>
					<td class="alignRight padRight20">
					<%=hmProPerformaceCurrency.get((String)alProjectId.get(i)) %> 
					<%=hmProPerformaceBillable.get((String)alProjectId.get(i)) %></td>
					<%-- <td class="alignRight padRight20"><%=hmProPerformaceProjectProfit.get((String)alProjectId.get(i)) %></td> --%>
					<td class="alignRight padRight20"><%=hmProPerformaceProjectAmountIndicator.get((String)alProjectId.get(i)) %></td>
					
					<td width="1%" style="border: 0px solid #fff">&nbsp;</td>
					<%-- <td class="alignRight padRight20" ><%=hmProPerformaceIdealTime.get((String)alProjectId.get(i)) %></td>
					<td class="alignRight padRight20" ><%=hmProPerformaceActualTime.get((String)alProjectId.get(i)) %></td>
					<td align="center"><%=hmProPerformaceProjectTimeIndicator.get((String)alProjectId.get(i)) %></td> --%>
					<td align="center"> 
						<span id="bullet<%=(String)alProjectId.get(i)%>">Loading..</span>
						<script type="text/javascript">
						    $(function() {
						    	 $('#bullet<%=(String)alProjectId.get(i)%>').sparkline(new Array(<%=strBullet %>), {type: 'bullet',taretColor: '#b2b2b2',performanceColor: '#9acd32'} );
						    });
					    </script>
					</td>
					<td class="alignRight padRight20"><%=hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_IDEAL_TIME_HRS") %></td>
					<td class="alignRight padRight20"><%=hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_ACT_TIME_HRS") %></td>
					<td align="center"><%=hmProPerformaceProjectTimeIndicator.get((String)alProjectId.get(i)) %></td>
				</tr>
				<%} %>
				<%} else { %>
				<tr><td colspan="12"><div class="msg nodata"><span>Projects not available for this selection.</span></div> </td></tr>
				<% } %>
				</table>
	<br/>

	<div style="text-align: center; float: left; width: 100%;">
		
		<% int intproCnt = uF.parseToInt(proCount);
			int pageCnt = 0;
			int minLimit = 0;
			
			for(int i=1; i<=intproCnt; i++) {
				minLimit = pageCnt * 10;
				pageCnt++;
		%>
		<% if(i ==1) {
			String strPgCnt = (String)request.getAttribute("proPage");
			String strMinLimit = (String)request.getAttribute("minLimit");
			if(uF.parseToInt(strPgCnt) > 1) {
				 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
				 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
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
				<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');">
				<%="< Prev" %></a>
			<% } else { %>
				<b><%="< Prev" %></b>
			<% } %>
			</span>
			<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
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
			<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
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
			 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
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
		
			<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
			<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
			style="color: black;"
			<% } %>
			><%=pageCnt %></a></span>
			<span style="color: lightgray;">
			<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
				<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"><%="Next >" %></a>
			<% } else { %>
				<b><%="Next >" %></b>
			<% } %>
			</span>
		<% } %>
		<%} %>
		</div>


<br/><br/>

<div style="float: left; width: 45%">
		<div class="fieldset">
			<fieldset>
				<legend>Money</legend>
				
				<div> <i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/denied.png"> --%>Actual &gt; Billable</div>
				
				<div><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/re_submit.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d;padding: 5px 5px 0 5px;"></i>Actual &gt; Budgeted and Actual &lt; Billable</div>
				
				<div> <%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/approved.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i> Actual &lt; Budgeted</div>
				
			</fieldset>
		</div>
	</div>
	

<div style="float: right; width: 45%">
		<div class="fieldset">
			<fieldset>
				<legend>Time</legend>
				
				<div> <i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/denied.png"> --%>Actual &gt; Deadline</div>
				
				<div><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/re_submit.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d;padding: 5px 5px 0 5px;"></i>Actual &gt; Estimated</div>
				
				<div> <%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/approved.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>Actual Time &lt; Estimated</div>
				
			</fieldset>
		</div>
	</div>
	
	</div>
	
</div>
</div>
</div>
</section>


	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:500px;overflow-y:auto;padding-left: 25px;">/*height:auto;*/
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>

</div>
<!--<script type="text/javascript">
$(document).ready(function() {

	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_project_service").multiselect().multiselectfilter();
	$("#f_client").multiselect().multiselectfilter();
	
});

</script>-->
