<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page buffer = "16kb" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%
UtilityFunctions uF = new UtilityFunctions();
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String strTitle = (String)request.getAttribute(IConstants.TITLE);

Map<String, List<Map<String, String>>> hmBooking = (Map<String, List<Map<String, String>>>) request.getAttribute("hmBooking");
if(hmBooking == null) hmBooking = new HashMap<String, List<Map<String, String>>>();
 
%>
<style>  
.dataTables_wrapper .row{
margin-left: 0px;
margin-right: 0px;
}
</style>

<script>

$(function(){
Highcharts.chart('containerLeaveChart', {
    chart: {
        type: 'column'
    },
    title: {
        text: 'Leave chart'
    },
    xAxis: {
        categories: [<%=request.getAttribute("sbLeaveTypeName")%>],
        title:{
        	text:'Leave Type'
        }
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Leave Count'
        },
        stackLabels: {
            enabled: true,
            style: {
                fontWeight: 'bold',
                color: 'gray'
            }
        }
    },
    
   colors: [
    '#ffa323', /*orange  */
	'#008000', /*green */
 	'#b4bcc1' /*gray  */
],

plotOptions: {
    column: {
        colorByPoint: true
    }
}, 
    legend: {
        align: 'right',
        x: -30,
        verticalAlign: 'top',
        y: 25,
        floating: true,
        backgroundColor: 'white',
        borderColor: '#eeeeee',
        borderWidth: 1,
        shadow: false
    },
    tooltip: {
        headerFormat: '<b>{point.x}</b><br/>',
        pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
    },
    plotOptions: {
        column: {
            stacking: 'normal',
            dataLabels: {
                enabled: true,
                color:'white'
            }
        }
    },
    series: [<%=request.getAttribute("sbLeaveChartData")%>]
 });
});

</script>

<script type="text/javascript" charset="utf-8">
	$(function(){
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
		
	function addCompensatoryLeave() {
	
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Apply New Extra Working');
		$("#modalInfo").show();
		if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		}
		$.ajax({
			url : "ApplyCompLeavePopUp.action?isCompensate=true",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function applyWorkFromHome() {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Apply New Work From Home');
		$("#modalInfo").show();
		if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		}
		$.ajax({
			url : "ApplyCompLeavePopUp.action?isWorkFromHome=true",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	
	function addLeave() {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Apply New Leave');
		$("#modalInfo").show();
		if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		 }
		$.ajax({
			url : "ApplyLeavePopUp.action?isCompensate=false",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}  
		
		
	function addTravel() {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Apply New Travel Itinerary');
		$("#modalInfo").show();
		if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		 }
		$.ajax({
			url : "ApplyTravelPopUp.action",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	} 
	
	/* Start Dattatray */
	function addOnDuty() {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Apply New On Duty');
		$("#modalInfo").show();
		if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		 }
		$.ajax({
			url : "ApplyOnDutyPopUp.action",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	} /* End Dattatray */
	
	function getApprovalStatus(leave_id,empname,type){
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Work flow of '+empname);
		$("#modalInfo").show();
		$.ajax({
			url : "GetLeaveApprovalStatus.action?effectiveid="+leave_id+"&type="+type,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	function addOptionalHolidayLeave() {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Apply New Optional Holiday Leave');
		$("#modalInfo").show();
		if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		 }
		$.ajax({
			url : "ApplyOptionalHolidayLeave.action?isOptHolidayLeave=true&isCompensate=false",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	
	function loadLeaveEntryPage(dataType) {
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'EmployeeLeaveEntryReport.action?dataType='+dataType,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
	
	function cancelLeave(leaveId){
		
		if(confirm('Are you sure you want to cancel this?')){ 
			var reason = window.prompt("Please enter cancel reason.");
			if (reason != null) {
				var divResult = 'divResult';
				$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'ModifyLeave.action?type=emp&modify=modify&leaveId='+leaveId+'&cancelReason='+reason,
					data: $("#"+this.id).serialize(),
					success: function(result){
						//alert("result ===>>" + result);
			        	$("#"+divResult).html(result);
			   		},
		 			error: function(res){
		 				$.ajax({
		 					url: 'EmployeeLeaveEntryReport.action',
		 					cache: true,
		 					success: function(result){
		 						$("#"+divResult).html(result);
		 			   		}
		 				});	 				
		 			}
				});
			}
		}
	} 
	
	/* Start Dattatray */
	function approveDeny(apStatus,leaveId,levelId,compensatory,leaveStatus,startDate,endDate,userType){
		var divResult = 'divResult';
		var strBaseUserType = document.getElementById("strBaseUserType").value;
		var currUserType = document.getElementById("currUserType").value;
		if(strBaseUserType == '<%=IConstants.CEO %>' || strBaseUserType == '<%=IConstants.HOD %>') {
			divResult = 'subDivResult';
		}
		var status = '';
		if(apStatus == '1'){
			status='approve';
		} else if(apStatus == '-1'){
			status='deny';
		}
		
		if(confirm('Are you sure, do you want to '+status+' this request?')){
			var reason = window.prompt("Please enter your "+status+" reason.");
			if (reason != null) {
				//alert("divResult ===>>" + divResult);
				
				$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'ManagerLeaveApproval.action?apType=auto&apStatus='+apStatus+'&E='+leaveId+'&LID='+levelId+'&strCompensatory='+compensatory+'&leaveStatus='+leaveStatus
							+'&strStartDate='+startDate+'&strEndDate='+endDate+'&mReason='+reason +'&userType='+userType+'&currUserType='+currUserType,
					data: $("#"+this.id).serialize(),
					success: function(result){
						//alert("result ===>>" + result);
			        	$("#"+divResult).html(result);
			   		},
		 			error: function(res){
		 				$.ajax({
		 					url: 'ManagerLeaveApprovalReport.action?leaveStatus='+leaveStatus+'&strStartDate='+startDate+'&strEndDate='+endDate+'&currUserType='+currUserType,
		 					cache: true,
		 					success: function(result){
		 						
		 						$("#"+divResult).html(result);
		 			   		}
		 				});	 				
		 			}
				});
			}
		}
	}/*  End Dattatray */

</script>


<%	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); 
	String msg=(String)request.getAttribute("msg");
	String []arrEnabledModules = CF.getArrEnabledModules();

//===start parvez date: 05-08-2022===	
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
//===end parvez date: 05-08-2022===		
%>
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
	<%session.setAttribute(IConstants.MESSAGE, ""); %>

	<%if(strUserType!=null && strUserType.equals(IConstants.EMPLOYEE)){ %>
		<div class="box box-none">
			<div class="box-body" style="padding: 5px;">
				<div style="float:right;margin-bottom:10px">
				<%
				String isOptHoolidayLeave = (String)request.getAttribute("isOptHoolidayLeave");
				String isWorkFromHome = (String)request.getAttribute("isWorkFromHome");
				%>
				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_TIME_DISABLE_LINK))){ 
					List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_TIME_DISABLE_LINK);
					
				%>
					<%
					if(disableTabList != null && disableTabList.contains("APPLY_FOR_WORK_FROM_HOME")){
						if(uF.parseToBoolean(isWorkFromHome)) { %>
							<a href="javascript:void(0)" onclick="applyWorkFromHome()"><i class="fa fa-plus-circle"></i> Apply for Work From Home</a>
					<% }
					}%>
					 
					<%
					if(disableTabList != null && disableTabList.contains("APPLY_FOR_OPTIONAL_HOLIDAY_LEAVE")){
						if(uF.parseToBoolean(isOptHoolidayLeave)){ %>
							<a href="javascript:void(0)" onclick="addOptionalHolidayLeave()"><i class="fa fa-plus-circle"></i> Apply for Optional Holiday Leave</a>
					<%	} 
					}%>
					
					<% if(disableTabList != null && disableTabList.contains("APPLY_FOR_EXTRA_WORKING")){ %>
						<a href="javascript:void(0)" onclick="addCompensatoryLeave()"><i class="fa fa-plus-circle"></i>Apply for Extra Working</a>
					<% } %>	
					<% if(disableTabList != null && disableTabList.contains("APPLY_LEAVE")){ %>
						<a href="javascript:void(0)" onclick="addLeave()"><i class="fa fa-plus-circle"></i>Apply Leave</a>
					<% } %>	
					<% if(disableTabList != null && disableTabList.contains("APPLY_TRAVEL")){ %>
						<a href="javascript:void(0)" onclick="addTravel()"><i class="fa fa-plus-circle"></i>Apply Travel</a>
					<% } %>		
				<% } else { %>	
				
					<%
					if(uF.parseToBoolean(isWorkFromHome)) { %>
						<a href="javascript:void(0)" onclick="applyWorkFromHome()"><i class="fa fa-plus-circle"></i> Apply for Work From Home</a>
					<%} 
					
					if(uF.parseToBoolean(isOptHoolidayLeave)){ %>
						<a href="javascript:void(0)" onclick="addOptionalHolidayLeave()"><i class="fa fa-plus-circle"></i> Apply for Optional Holiday Leave</a>
					<%} %>
						<a href="javascript:void(0)" onclick="addCompensatoryLeave()"><i class="fa fa-plus-circle"></i>Apply for Extra Working</a>
						<a href="javascript:void(0)" onclick="addLeave()"><i class="fa fa-plus-circle"></i>Apply Leave</a>
						<a href="javascript:void(0)" onclick="addTravel()"><i class="fa fa-plus-circle"></i>Apply Travel</a>

				<% } %>
					<!-- Start Dattatray -->
					<!-- <a href="javascript:void(0)" onclick="addOnDuty()"><i class="fa fa-plus-circle"></i>Apply On Duty</a> -->
					<!-- End Dattatray -->

					<!-- <a href="javascript:void(0)" onclick="addCompensatoryLeave()"><input type="button" class="btn btn-primary" value="Apply for Extra Working"></a>
					<a href="javascript:void(0)" onclick="addLeave()"><input type="button" class="btn btn-primary" value="Apply Leave"></a>
					<a href="javascript:void(0)" onclick="addTravel()"><input type="button" class="btn btn-primary" value="Apply Travel"></a> -->
				</div>
				
				<div style="float: left; width: 100%;">
				 <div class="box box-info">
		            <div class="box-header with-border">
		                <h3 class="box-title">Leave Chart</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
		            <!-- /.box-header -->
		            <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                <div class="content1">
							<div id="containerLeaveChart" style="height: 250px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
						</div>
		            </div>
		            <!-- /.box-body -->
		        </div>
			 </div>
				
				<div class="pagetitle" style="margin:0px 0px 10px 0px"><%=uF.showData(msg,"") %></div>
				<div style="float: left; width: 100%;">
					<table class="table table-bordered" id="lt">
						<thead>
							<tr>
								<th>Leave Type</th>
								<th>Total No. Of Leaves<br>(in days)</th>
								<th>Pending Leaves<br>(in days)</th>
								<th>Approved Leaves<br>(in days)</th>
								<th>Denied Leaves<br>(in days)</th>
								<th>Remaining Leaves<br>(in days)</th>
							</tr>
						</thead>
						<tbody>
							<% java.util.List couterlist = (java.util.List)request.getAttribute("leaveList"); %>
							<% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
							<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
							<tr>
								<td><%= cinnerlist.get(0) %></td>
								<td class="alignRight" style="padding-right: 20px"><%= cinnerlist.get(1) %></td>
								<td class="alignRight" style="padding-right: 20px"><%= cinnerlist.get(2) %></td>
								<td class="alignRight" style="padding-right: 20px"><%= cinnerlist.get(3) %></td>
								<td class="alignRight" style="padding-right: 20px"><%= cinnerlist.get(4) %></td>
								<td class="alignRight" style="padding-right: 20px"><%= cinnerlist.get(5) %></td>
							</tr>
							<% } %>
						</tbody>
					</table>
				</div>
				
			</div>
		</div>
      <%} %>
	
		<div class="box box-none nav-tabs-custom">
			<ul class="nav nav-tabs">
				<%
				String dataType = (String) request.getAttribute("dataType");
				String strLabel = "";
				if(dataType == null || dataType.equals("L")) { 
				%>
				<li class="active"><a href="javascript:void(0)" onclick="loadLeaveEntryPage('L');" data-toggle="tab">Applied Leaves</a></li>
				<li><a href="javascript:void(0)" onclick="loadLeaveEntryPage('T');" data-toggle="tab">Applied Travels</a></li>
				<% } else if(dataType != null && dataType.equals("T")) {%>
				<li><a href="javascript:void(0)" onclick="loadLeaveEntryPage('L');" data-toggle="tab">Applied Leaves</a></li>
				<li class="active"><a href="javascript:void(0)" onclick="loadLeaveEntryPage('T');" data-toggle="tab">Applied Travels</a></li>
				<% } %>	
			</ul>
            <!-- /.box-header -->
	<div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
		<div style="overflow-y: auto; max-height: 500px;">
			<table cellpadding="5"  class="table table-bordered" id="lt">
			<%
			List alLeaveList = (List)request.getAttribute("alLeaveList");
			if(alLeaveList==null)alLeaveList = new ArrayList();
			 for(int i=0; i<alLeaveList.size(); i++){
				List alInner = (List)alLeaveList.get(i);
				if(alInner==null)alInner = new ArrayList();
				
					if(i==0){
			%>
					<tr>
						<th class="alignCenter">Status</th>
						<th class="alignCenter" <%if(dataType == null || dataType.equals("L")) {%>style="width: 15%;"<%} %>>Canceled</th>
						<%if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){ %>
							<th class="alignLeft">Employee</th>
						<%} %>
						<%if(dataType != null && dataType.equals("T")) {%>
							<th class="alignCenter">Plan Name</th>
							<th class="alignCenter">Place From</th>
							<th class="alignCenter">Place To</th>
							<th class="alignLeft">From Time</th>
							<th class="alignLeft">To Time</th>
						<%} %>
						<th class="alignCenter">From Date</th>
						<th class="alignCenter">To Date</th>
						<th class="alignCenter">Days</th>
						<th class="alignLeft">Reason</th>
						
					<!-- ===start parvez date: 18-03-2023=== -->	
						<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BACKUP_EMPLOYEE_FOR_LEAVE))){ %>	
							<th class="alignLeft">Back-up</th>
						<%} %>	
					<!-- ===end parvez date: 18-03-2023=== -->	
						<th class="alignLeft">Type</th>
						<%if(dataType != null && dataType.equals("T")) {%>
							<th class="alignLeft">Concierge Service</th>
							<th class="alignLeft">Mode of Travel</th>
							<th class="alignLeft">Booking</th>
							<th class="alignLeft">Booking Details</th>
							<th class="alignLeft">Accommodation Required</th>
							<th class="alignLeft">Accommodation Details</th>
							<th class="alignLeft">Attachment</th>
						<%} %>
						<%if(uF.parseToBoolean(CF.getIsWorkFlow())){ %>
							<th class=" alignLeft">Workflow</th>
						<%} %>
					</tr>
				<%}%>
				<tr>
					<td class="alignCenter" id="myDiv_<%=i%>" nowrap="nowrap"><%=(String)alInner.get(0) %></td>
					<td class="alignLeft"><%=(String)alInner.get(11) %></td>
					<%if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){ %>
						<td class="alignLeft"><%=(String)alInner.get(1) %></td>
					<%} %>
					<%if(dataType != null && dataType.equals("T")) {%>
						
						<td class="alignCenter"><%=(String)alInner.get(20) %></td>
						<td class="alignCenter"><%=(String)alInner.get(21) %></td>
						<td class="alignCenter"><%=(String)alInner.get(22) %></td>
						<td class="alignCenter"><%=(String)alInner.get(23) %></td>
						<td class="alignCenter"><%=(String)alInner.get(24) %></td>
						
						
					
					<%} %>
					<td class="alignCenter"><%=(String)alInner.get(2) %></td>
					<td class="alignCenter"><%=(String)alInner.get(3) %></td>
				
					<%if(alInner.get(7).equals("Extra Working") && alInner.get(23) !=null && alInner.get(24)!=null ) 
					{%>
						
						<td align="center"><%= alInner.get(4)+"("+ alInner.get(23)+" , " + alInner.get(24)+")" %></td>
						
				  <%} 
				  else{%>
				 	 <td class="alignRight"><%=(String)alInner.get(4) %></td>
				  <%}%>
					<td class="alignLeft"><%=uF.showData((String)alInner.get(5),"") %></td>
				<!-- ===start parvez date: 18-03-2023=== -->	
					<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BACKUP_EMPLOYEE_FOR_LEAVE))){ %>
						<td class="alignLeft"><%=uF.showData((String)alInner.get(25),"") %></td>
					<%} %>
				<!-- ===end parvez date: 18-03-2023=== -->	
					<td class="alignLeft" style="background-color:<%=(String)alInner.get(7) %>"><%=(String)alInner.get(6) %></td>
					
					<%if(dataType != null && dataType.equals("T")) {%>
						<td class="alignCenter"><%=(String)alInner.get(14) %></td>
						<td class="alignCenter"><%=(String)alInner.get(15) %></td>
						<td class="alignCenter"><%=(String)alInner.get(16) %></td>
						<td class="alignCenter"><%=(String)alInner.get(17) %></td>
						<td class="alignCenter"><%=(String)alInner.get(18) %></td>
						<td class="alignCenter"><%=(String)alInner.get(19) %></td>
						<td>
							<%if(hmBooking.containsKey((String)alInner.get(12))){%>
								<span style="float:left;">
									<%
										List<Map<String, String>> alData = (List<Map<String, String>>)hmBooking.get((String)alInner.get(12));
										for(int j = 0; alData!=null && j<alData.size(); j++){
										Map<String, String> hmAttach = (Map<String, String>) alData.get(j);
										if(hmAttach == null) hmAttach = new HashMap<String, String>();
										
										if(hmAttach.get("FILE_PATH")!=null && !hmAttach.get("FILE_PATH").trim().equals("") && !hmAttach.get("FILE_PATH").trim().equalsIgnoreCase("NULL")){
									%>
										<%=hmAttach.get("FILE_PATH") %>
									<%} 
									}%>
								</span>
							<%} %>
						</td>
					<%} %>
					<%if(uF.parseToBoolean(CF.getIsWorkFlow())){ %>
						<td class="alignLeft"><%=(String)alInner.get(10) %></td>
					<%} %>
				</tr>
			<%} %>
			</table>

			<%if(alLeaveList.size()==0){ %>You have not applied for any leave till date. <%} %>
			</div>
			<br/>
			<input type="hidden" name="travelType" id="travelType" value="<%=(String)request.getAttribute("type") %>"/>
			<br/>
			<jsp:include page="../common/Legends.jsp"></jsp:include>
		</div>
		<!-- /.box-body -->
	</div>


	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>

	<script type="text/javascript">
	window.setTimeout(function() {  
		var travelType = document.getElementById("travelType").value;
		if(parseInt(travelType) > 0){
			addTravel();
		}
	}, 1000); 
	</script>
	
<!-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> -->
<script src="scripts/charts/highcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />
	