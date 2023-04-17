<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<style> 
	.listMenu1 .icon .fa{
		font-size: 45px;
		vertical-align: top;
		margin-top: 20px;
	}
</style>


<script type="text/javascript">

function applyLeave() { 

	var dialogEdit = '.modal-body'; 
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Apply Leave');
	if($(window).width() >= 800){
		 $(".modal-dialog").width(800);
	}
	$.ajax({
		url : "EmployeeLeaveEntry.action",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function submitForm(type, strPaycycle){
	var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
		divResult = 'subDivResult';
	}
	
	var currUserType = document.getElementById("currUserType").value;
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	if(strPaycycle.length>0) {
		var tmpPaycycle = strPaycycle.split('-');
		strStartDate = tmpPaycycle[0];
		strEndDate = tmpPaycycle[1];
	}
	var leaveStatus = document.getElementById("leaveStatus").value;
	
	var org = "";
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	
	var location = "";
	if(document.getElementById("f_strWLocation")) {
		location = getSelectedValue("f_strWLocation");
	}
	
	var department = "";
	if(document.getElementById("f_department")) {
		department = getSelectedValue("f_department");
	}
	
	var service = "";
	if(document.getElementById("f_service")) {
		service = getSelectedValue("f_service");
	}

	var level = "";
	if(document.getElementById("f_level")) {
		level = getSelectedValue("f_level");
	}
	
	var strGrade = '';
	if(document.getElementById("f_grade")) {
		strGrade = getSelectedValue("f_grade");
	}
	var strEmployeType = '';	
	if(document.getElementById("f_employeType")) {
		strEmployeType = getSelectedValue("f_employeType");
	}
	
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate+'&leaveStatus='+leaveStatus+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
	}
	//alert("service ===>> " + service);
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ManagerLeaveApprovalReport.action?f_org='+org+'&currUserType='+currUserType+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#"+divResult).html(result);
   		}
	});
}


function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	return exportchoice;
}


$(function(){
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
		"columnDefs": [{
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});

	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect();
	$("#f_grade").multiselect();

	$("#strStartDate").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
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

function openEmpProfilePopup(empId){
	var dialogEdit = '.modal-body';
 	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
    $(".modal-title").html('Employee Information');
    if($(window).width() >= 900){
	  $(".modal-dialog").width(900);
    }
    $.ajax({
	    url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
	    cache : false,
	    success : function(data) {
	   		 $(dialogEdit).html(data);
	    }
	 });
};

function modifyLeave(leaveId, leaveTypeId) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Cancel Leave');
	 $.ajax({
		url : "ModifyLeave.action?leaveId="+leaveId+"&leaveTypeId="+leaveTypeId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function getApprovalStatus(leave_id,empname) {
	//alert("getApprovalStatus 1");
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Work flow of '+empname);
	 $.ajax({
		url : "GetLeaveApprovalStatus.action?effectiveid="+leave_id+"&type=1",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

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
}

function viewLeaveBalance(strEmpId,strEmpName,nCurrentYear) {
	//alert("viewLeaveBalance 1");
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 //alert("viewLeaveBalance 2");
	 $(".modal-title").html('Consolidate leave information of '+strEmpName+' since 01/01/'+nCurrentYear);
	 $.ajax({
		url : 'ViewEmpLeaveBalance.action?strEmpId='+strEmpId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function Payroll_dashboard_link(strpaycycle1) {
	//alert("strStartDate="+strStartDate+"strEndDate="+strEndDate+"paycycle_no"+paycycle_no);
	window.location='PayrollDashboard_2.action?strpaycycle1='+strpaycycle1;
}

 function addComment(leaveId,levelId,leaveStatus,startDate,endDate,userType){
	
	var currUserType = document.getElementById("currUserType").value;
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 //alert("viewLeaveBalance 2");
	 $(".modal-title").html('Leave Comment');
	 
	 $.ajax({
			type : 'POST',
			url: 'AddLeaveComment.action?leaveId='+leaveId+'&leaveStatus='+leaveStatus
						+'&strStartDate='+startDate+'&strEndDate='+endDate +'&userType='+userType+'&currUserType='+currUserType,
			data: $("#"+this.id).serialize(),
			success: function(result){
					//alert("result ===>>" + result);
		        $(dialogEdit).html(result);
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

<%-- function addComment(leaveId,levelId,leaveStatus,startDate,endDate,userType){
	var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var currUserType = document.getElementById("currUserType").value;
	if(strBaseUserType == '<%=IConstants.CEO %>' || strBaseUserType == '<%=IConstants.HOD %>') {
		divResult = 'subDivResult';
	}
	
	var reason = window.prompt("Please enter your comment.");
		if (reason != null) {
			//alert("divResult ===>>" + divResult);
			
			$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ManagerLeaveApproval.action?apType=auto&E='+leaveId+'&LID='+levelId+'&leaveStatus='+leaveStatus
						+'&strStartDate='+startDate+'&strEndDate='+endDate+'&mReason='+reason +'&userType='+userType+'&currUserType='+currUserType+'&dataType=HRComment',
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
 --%>
</script>

<%--  <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TViewManagerLeaveApproval%>" name="title"/>
</jsp:include> --%>

	<%
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); 
		UtilityFunctions uF = new UtilityFunctions();
		
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		//System.out.println("user Type===>"+strUserType);
		String currUserType = (String) request.getAttribute("currUserType");  
		//System.out.println("Current Type===>"+currUserType);
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		//System.out.println("Base Type===>"+strBaseUserType);
		
		List<List<String>> alPaycycleList = (List<List<String>>) request.getAttribute("alPaycycleList");
		
		String strpaycycle1 = (String) request.getAttribute("strpaycycle1");
		
		//System.out.println("strpaycycle1-in mnger jsp "+strpaycycle1);
		String fromPage=(String)request.getAttribute("fromPage");
		//System.out.println("frompage-in mngr jsp"+fromPage);
//===start parvez date: 18-03-2023===	
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
//===end parvez date: 18-03-2023===	
	
	%>


	<%-- <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		<div style="width: 100%;">
			<ul class="nav nav-pills">
				<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="ManagerLeaveApprovalReport.action?currUserType=MYTEAM" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"  style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="ManagerLeaveApprovalReport.action?currUserType=<%=strBaseUserType %>" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			</ul>
		</div>
	<% } %> --%>
	
		<div class="col-lg-12 col-md-12 col-sm-12 paddingright0 listMenu1" style="padding-left: 0px;">
			<% if(alPaycycleList != null && alPaycycleList.size()>0) { 
				for(int i=0; i<alPaycycleList.size(); i++) {
					List<String> innerList = alPaycycleList.get(i);
					String strBgClass = "bg-gray";
					if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) > uF.parseToInt(innerList.get(3))) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == uF.parseToInt(innerList.get(2))) {
						strBgClass = "bg-green";
					}
			%>
				<div class="col-lg-2 col-xs-6 col-sm-12 paddingright0">
				<!-- small box -->
					<div class="small-box <%=strBgClass %>">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-umbrella" aria-hidden="true"></i>
						</div>
						<div class="inner" style="padding: 0px 10px; text-align: right;">
							<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
							<div style="margin-top: -5px;">Applied</div>
						</div>
						<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
							<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
							<div style="margin-top: -5px;">Approved/Denied</div>
						</div>
						<a href="javascript:void(0);" style="font-size: 12px;" onclick="submitForm('2', '<%=innerList.get(0) %>');" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a>
					</div>
				</div>
				<% } %>
			<% } %>
		</div>
			
		<div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
			<s:form name="frm" action="ManagerLeaveApprovalReport" theme="simple">
				<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
				<input type="hidden" name="currUserType" id="currUserType" value="<%=currUserType %>" />
				<div class="box box-primary collapsed-box" style="border-top-color: #F0F0F0;">
					<div class="box-header with-border">
						<p class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></p>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div>
					<!-- /.box-header -->
					<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status</p>
									<s:select theme="simple" name="leaveStatus" id="leaveStatus" list="#{'0':'All','1':'Approved', '2':'Pending','3':'Denied'}" onchange="document.frm_Bonus.submit();" />
								</div>
								<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organisation</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1', '');" list="organisationList" key="" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Service</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Grade</p>
										<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Employee Type</p>
										<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
									</div>
								<% } %>
							</div>
						</div><br>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">From Date</p>
									<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">To Date</p>
									<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2', '');" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</s:form>
			
			<% if(strUserType != null && (strUserType.equals(IConstants.MANAGER) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.OTHER_HR))) { %>
				<div class="col-md-12" style="margin: 0px 0px 10px 0px">
					<a href="javascript:void(0)" onclick="applyLeave();" title="Apply Leave"><i class="fa fa-plus-circle"></i> Apply Leave</a>
				</div>
			<% } %>
			
			<!--************message div  -->
			<div style="float: left;width:100%;margin-top: 10px;">
			<%  
			 String strMessage = (String) session.getAttribute("MESSAGE");
	       	 if (strMessage == null) {
	        	strMessage = "";
	       	 }else{%>
				<p class="message"><%=strMessage%></p>
				<% session.setAttribute("MESSAGE","");
	       	 } %>
    
			</div>
			<!--************message div End -->
			
			<table class="table table-bordered" id="lt">
				<thead>
					<tr>
						<th>Employee Name</th>
						<th>Leave Type</th>
						<th>Apply date</th>
						<th>From</th>
						<th>To</th>
						<th>No of days</th>
						<th>Emp Reason</th>
				<!-- ===start parvez date: 29-09-2022=== -->		
						<th>Emp Relation</th>
				<!-- ===end parvez date: 29-09-2022=== -->		
						<!-- <th>Manager Reason</th> -->
					<!-- ===start parvez date:18-03-2023=== -->	
						<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BACKUP_EMPLOYEE_FOR_LEAVE))){ %>
						<th>Back-up</th>
						<%} %>
					<!-- ===end parvez date:18-03-2023=== -->	
						<th>Status</th>
						<th>Approving Profile</th>
						<%if(uF.parseToBoolean(CF.getIsWorkFlow())){ %>
							<th width="10%" class=" alignLeft">WorkFlow</th>
						<%} %>
						<th>Cancel</th>
						<th>Leave Balance</th> 
					</tr>
				</thead>
				<tbody>
				<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList");
					//System.out.println("reportList:"+couterlist);
				 for (int i=0; couterlist!=null && i<couterlist.size(); i++) { 
				 	java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
						<tr>
							<td nowrap="nowrap"><%=cinnerlist.get(0) %></td>
							<td nowrap="nowrap"><%=cinnerlist.get(1) %></td>
							<td nowrap="nowrap"><%=cinnerlist.get(2) %></td>
							<td nowrap="nowrap"><%=cinnerlist.get(3) %></td>
							<td nowrap="nowrap"><%=cinnerlist.get(4) %></td>
							<%if(cinnerlist.get(1).equals("Extra Working") && cinnerlist.get(17) !=null && !cinnerlist.get(17).equals("") && cinnerlist.get(18)!=null && !cinnerlist.get(18).equals("")) { %>
								<td align="center"><%= cinnerlist.get(5)+"("+ cinnerlist.get(17)+" , " + cinnerlist.get(18)+")" %></td>
							<% } else { %>
							<%-- <% System.out.println("MLAR.jsp/498---cinnerlist.get(5)=="+cinnerlist.get(5)); %> --%>
								<td align="center"><%= cinnerlist.get(5) %></td>
							<% } %>
								
							<td><%= cinnerlist.get(6) %></td>
						<!-- ===start parvez date: 29-09-2022=== -->	
							<td><%= cinnerlist.get(20) %></td>
						<!-- ===end parvez date: 29-09-2022=== -->	
							<%-- <td><%= cinnerlist.get(7) %></td> --%>
							
					<!-- ===start parvez date:18-03-2023=== -->	
						<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BACKUP_EMPLOYEE_FOR_LEAVE))){ %>
							<td><%= cinnerlist.get(21) %></td>
						<%} %>
					<!-- ===end parvez date:18-03-2023=== -->
							<td  nowrap="nowrap"><div id="myDiv<%=i%>"><%= cinnerlist.get(8) %> <%= cinnerlist.get(19) %></div></td>
							<td><%= cinnerlist.get(14) %></td>
							<%if(uF.parseToBoolean(CF.getIsWorkFlow())) { %>
								<td class="alignLeft"><%=(String)cinnerlist.get(9) %></td>
							<% } %>
							
							<td>
							<%
								String isApproved=(String) cinnerlist.get(13);
								if(isApproved.equals("1")){
									String strCancelStatus = "Cancel";
									if(uF.parseToBoolean((String) cinnerlist.get(16))) { 
										strCancelStatus = "view";
									}
							%>
								<a href="javascript:void(0)" onclick="modifyLeave(<%=(String)cinnerlist.get(10)%>, <%=(String)cinnerlist.get(11)%>)"><%=strCancelStatus %></a>
							<%} %>
							</td>
							<td><%= cinnerlist.get(15) %></td>
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
			    <div class="legend-info"><i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve Leave</div>
			  </div>
			  <div class="custom-legend no-borderleft-for-legend">
			    <div class="legend-info"><i class="fa fa-times-circle cross" aria-hidden="true"></i>Deny Leave</div>
			  </div>
			</div>
		</div>
		
		<%if(fromPage != null && fromPage.equalsIgnoreCase("P")) { %>
			<div>
			   <button type="button" id="PD_button" onclick="Payroll_dashboard_link('<%=strpaycycle1 %>')">Go Back to PayrollDashboard</button>
			</div>
		<% } %>	
		<!-- /.box-body -->
		
		
		
<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
        <!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">-</h4>
			</div>
			<div class="modal-body" style="height:400px; overflow-y:auto; padding-left: 25px;"></div>
			<div class="modal-footer">
				<button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>