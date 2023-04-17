<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%--  <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Approve Travel" name="title"/>
</jsp:include> --%>

	<%
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); 
		UtilityFunctions uF = new UtilityFunctions();
		
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		String currUserType = (String) request.getAttribute("currUserType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>

	<%-- <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		<div style="width: 100%;">
			<ul class="nav nav-pills">
				<li class="<%=(currUserType == null || currUserType.equa
				ls("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="TravelApprovalReport.action?currUserType=MYTEAM" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"  style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="TravelApprovalReport.action?currUserType=<%=strBaseUserType %>" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			</ul>
		</div>
	<% } %> --%>
	
		<div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
			<s:form name="frm" action="TravelApprovalReport" theme="simple">
				<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
				<s:hidden name="currUserType" id="currUserType"/>
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
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status</p>
									<s:select theme="simple" name="leaveStatus" id="leaveStatus" list="#{'0':'All','1':'Approved', '2':'Pending','3':'Denied'}" />
								</div>
								<% 
								String strStartDate = (String)request.getAttribute("strStartDate");
								String strEndDate = (String)request.getAttribute("strEndDate");
								if(strStartDate == null || strStartDate.equals("null") || strStartDate.equals("")) {
									strStartDate = "From Date";
								}
								if(strEndDate == null || strEndDate.equals("null") || strEndDate.equals("")) {
									strEndDate = "To Date";
								}
							%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">From Date</p>
									<input type="text" name="strStartDate" id="strStartDate" style="width: 100px !important;" value="<%=strStartDate %>"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">To Date</p>
									<input type="text" name="strEndDate" id="strEndDate" style="width: 100px !important;" value="<%=strEndDate %>"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
					</div>
				</div>
			</s:form>

				<table class="table table-bordered" id="lt">
					<thead>
						<tr>
							<th>Employee Name</th>
							<!-- <th>Leave Type</th> -->
							<th>Apply date</th>
							<th>From</th>
							<th>To</th>

							<!-- Start Dattatray  -->
							<th>From Time</th>
							<th>To Time</th>
							<!-- End Dattatray  -->

							<th>No of days</th>
							<th class="no-sort">Emp Reason</th>
							<!-- <th>Manager Reason</th> -->
							<th class="no-sort">Status</th>
							<th>Approving Profile</th>
							<% if (uF.parseToBoolean(CF.getIsWorkFlow())) { %>
							<th width="10%" class=" alignLeft">WorkFlow</th>
							<% } %>
							<th class="no-sort">Cancel</th>
						</tr>
					</thead>
					<tbody>
						<% java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
							for (int i = 0; couterlist != null && i < couterlist.size(); i++) {
								java.util.List cinnerlist = (java.util.List) couterlist.get(i);
								System.out.println(couterlist.get(i));
						%>
						<tr>
							<!-- Start Dattatray  -->
							<td><%=cinnerlist.get(0)%></td>
							<%-- <td><%= cinnerlist.get(1) %></td> --%>
							<td><%=cinnerlist.get(2)%></td>
							<td><%=cinnerlist.get(3)%></td>
							<td><%=cinnerlist.get(4)%></td>
							<td><%=cinnerlist.get(5)%></td>
							<td><%=cinnerlist.get(6)%></td>
							<td align="center"><%= cinnerlist.get(7) %></td>
							<td ><%=cinnerlist.get(8)%></div>
							<%
								if(uF.parseToInt((String) cinnerlist.get(14)) == -2){
							%>
								<td align="left"><div id="myDiv<%=i%>"><%=cinnerlist.get(11)%></div>
							<% } else { %>
								
									<td align="left"><div id="myDiv<%=i%>"><%=cinnerlist.get(10)%></div> 
							<% } %>
							
							</td>
							<% if (uF.parseToInt((String) cinnerlist.get(14)) == -2){ %>
								<td><%=cinnerlist.get(19)%></td>
							<%} else { %>
								<td><%=cinnerlist.get(18)%></td>
							<%} %>
							<% if (uF.parseToBoolean(CF.getIsWorkFlow())) { %>
							<% if (uF.parseToInt((String) cinnerlist.get(14)) == -2){ %>
								<td class="alignLeft"><%=(String) cinnerlist.get(12)%></td>
							<% } else { %>
								<td class="alignLeft"><%=(String) cinnerlist.get(11)%></td>
							<% } %>
							<% } %>
							<td>
							<%  if (uF.parseToInt((String) cinnerlist.get(14)) == -2){ %>
							
							<!-- index 16 -->
								<%
									String isApprovedOD = (String) cinnerlist.get(16);
									if (isApprovedOD.equals("1")) {
										if (uF.parseToBoolean((String) cinnerlist.get(17))) {
								%>
								<a href="javascript:void(0)" onclick="alert('<%=(String) cinnerlist.get(16)%>');">Canceled</a>
								<% } else { %>
									<div id="myDiv_<%=(String) cinnerlist.get(13)%>">
										<a href="javascript:void(0)" onclick="modifyTravel(<%=(String) cinnerlist.get(13)%>)">Cancel</a>
									</div>
								<% } %>
								<% } %>
							<% } else { %>
								<%
									String isApproved = (String) cinnerlist.get(15);
										if (isApproved.equals("1")) {
											if (uF.parseToBoolean((String) cinnerlist.get(16))) {
								%>
								<a href="javascript:void(0)" onclick="alert('<%=(String) cinnerlist.get(15)%>');">Canceled</a>
								<% } else { %>
								
									<div id="myDiv_<%=(String) cinnerlist.get(12)%>">
										<a href="javascript:void(0)" onclick="modifyTravel(<%=(String) cinnerlist.get(12)%>)">Cancel</a>
									</div>
								<% }
									} %>
							<%} %>
							</td>
							<!-- End Dattatray  -->
						</tr>
						<% } %>
					</tbody>
				</table>

			<div class="custom-legends">
			  <div class="custom-legend pullout">
			    <div class="legend-info">Pull Out</div>
			  </div>
			  <div class="custom-legend pending">
			    <div class="legend-info">Waiting for approval</div>
			  </div>
			  <div class="custom-legend approved">
			    <div class="legend-info">Approved</div>
			  </div>
			  <div class="custom-legend denied">
			    <div class="legend-info">Denied</div>
			  </div>
			  <div class="custom-legend re_submit">
			    <div class="legend-info">Waiting for workflow</div>
			  </div>
			  <br/>
			  <div class="custom-legend no-borderleft-for-legend">
			    <div class="legend-info"><i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve Travel</div>
			  </div>
			  <div class="custom-legend no-borderleft-for-legend">
			    <div class="legend-info"><i class="fa fa-times-circle cross" aria-hidden="true"></i>Deny Travel</div>
			  </div>
			</div>
		</div>
		<!-- /.box-body -->
		
	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
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

<script type="text/javascript" charset="utf-8">
		
/* function submitForm(type){
	alert("type --->> " + type);
} */

function submitForm(type) {
	var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
		divResult = 'subDivResult';
	}
	var currUserType = document.getElementById("currUserType").value;
	var leaveStatus = document.getElementById("leaveStatus").value;
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'TravelApprovalReport.action?currUserType='+currUserType+'&leaveStatus='+leaveStatus+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#"+divResult).html(result);
   		}
	});
}

$( function () {
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
	
	$("#lt").DataTable({
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

	$("#strStartDate").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
    	console.log(selected);
        var minDate = new Date(selected.date.valueOf());
        $('#strEndDate').datepicker('strStartDate', minDate);
    });
    
    $("#strEndDate").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#strStartDate').datepicker('strEndDate', minDate);
    });
});	

function getApprovalStatus(travelId,empname){
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Work flow of '+empname);
	 $.ajax({
		url : "GetLeaveApprovalStatus.action?effectiveid="+travelId+"&type=4",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function modifyTravel(leaveId) {
	if(confirm('Are you sure you want to cancel this travel?')){ 
		var reason = window.prompt("Please enter your cancel reason.");
		if (reason != null) {
			var action = 'TravelApprovalReport.action?cancelStatus=1&leaveId='+leaveId+'&cancelReason='+reason;
			getContent('myDiv_'+leaveId, action);
		}
	}
}


function approveOrDenyTravelRequest(travelId, userType, empname){
	var currUserType = document.getElementById("currUserType").value;
	var leaveStatus = document.getElementById("leaveStatus").value;
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Approve / Deny Travel of'+empname);
	 $.ajax({
		url : "ApproveTravel.action?E="+travelId+"&userType="+userType+'&currUserType='+currUserType+'&leaveStatus='+leaveStatus+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		} 
	});
}

function approveDeny(apStatus,travelId,empId,leaveStatus,startDate,endDate,userType){
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
			$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url : "ApproveTravel.action?travelType=OD&isapproved="+apStatus+"&leaveId="+travelId+"&userType="+userType+'&currUserType='+currUserType+'&leaveStatus='+leaveStatus+
						'&managerReason='+reason+'&empId='+empId,
				data: $("#"+this.id).serialize(),
				success: function(result){
		        	$("#"+divResult).html(result);
		   		},
	 			error: function(res){
	 				$.ajax({
	 					url: 'TravelApprovalApprovalReport.action?leaveStatus='+leaveStatus+'&strStartDate='+startDate+'&strEndDate='+endDate+'&currUserType='+currUserType,
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

</script>