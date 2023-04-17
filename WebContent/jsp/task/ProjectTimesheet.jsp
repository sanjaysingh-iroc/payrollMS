<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*"%>



<style>
 
.anaAttrib1 {
	font-size: 12px;
	font-family: digital;
	color: #3F82BF;
	font-weight: bold;
	text-align: center;
	height: 22px;
}
</style>


<script>
    
    jQuery(document).ready(function() {
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
    });
</script>


<script type="text/javascript">

function getApprovalStatus(leave_id,empname){
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Leave Approval Status of '+empname);
	$.ajax({
		url : 'GetLeaveApprovalStatus.action?effectiveid='+leave_id+'&type=2',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function showEffertEmpWise(proId, proName) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Timesheet Details of '+proName);
	$.ajax({
		url : 'ProjectwiseTimesheetDetails.action?proId='+proId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


	function executeProjectActions(val, proID, proFreqID, timesheetType, pageType) {
		//alert("proID --->> " + proID);
		if(val == '1') {
			if(confirm('Are you sure, you want to approve this timesheet for billing?')) {
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					url: 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proID='+proID+'&proFreqID='+proFreqID+'&operation=ApproveForBilling&pageType='+pageType,
					success: function(result) {
						$("#divResult").html(result);
			   		}
				});
				/* var action = 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proID='+proID+'&proFreqID='+proFreqID+'&operation=ApproveForBilling&pageType='+pageType;
				window.location = action; */
			}
		} else if(val == '2') {
			if(confirm('Are you sure, you want to approve this timesheet and send to customer?')) {
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					url: 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proID='+proID+'&proFreqID='+proFreqID+'&operation=ApproveAndSendToCustomer&pageType='+pageType,
					success: function(result) {
						$("#divResult").html(result);
			   		}
				});
				/* var action = 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proID='+proID+'&proFreqID='+proFreqID+'&operation=ApproveAndSendToCustomer&pageType='+pageType;
				window.location = action; */
			}
		} else if(val == '3') {
			//alert("proID 1 --->> " + proID);
			if(confirm('Are you sure, you want to save for later?')) {
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					url: 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proID='+proID+'&proFreqID='+proFreqID+'&operation=SaveLater&pageType='+pageType,
					success: function(result) {
						$("#divResult").html(result);
			   		}
				});
				/* var action = 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proID='+proID+'&proFreqID='+proFreqID+'&operation=SaveLater&pageType='+pageType;
				window.location = action; */
			}
		} else if(val == '4') {
			customerTimesheetDeny(timesheetType, proID, proFreqID, pageType);
		}
	}

	
	function customerTimesheetDeny(timesheetType, proID, proFreqID, pageType) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Deny Timesheet');
		$.ajax({
			url : 'ProjectTimesheetDenyByCustomer.action?timesheetType='+timesheetType+'&proID='+proID+'&proFreqID='+proFreqID+'&operation=deny&pageType='+pageType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	function sortByTimesheets() { 
		//document.frm_ProjectTimesheet.sortBy.value = document.getElementById('sortBy1').value;
		document.frm_ProjectTimesheet.sortBy.value = '';
		
		var form_data = $("#frm_ProjectTimesheet").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ProjectTimesheets.action',
			data: form_data,
			success: function(result) {
				$("#divResult").html(result);
	   		}
		});
	}
	
	
	function sortByEmpTimesheets(val) { 
		//document.frmTimesheet.sortBy.value = document.getElementById('sortBy1').value;
		
		document.getElementById('strPaycycle').value = document.getElementById('paycycle').value;
		
		document.frmTimesheet.sortBy.value = '';
		if(val != '') {
			document.frmTimesheet.filterBy.value = val;
		}
		var cnt = 0;
		if(val == 'O') {
			var strYear = document.getElementById('strYear').value;
			var strMonth = document.getElementById('strMonth').value;
			if(strYear == '' && strMonth == '') {
				alert("Please, select year and month.");
				cnt = 1;
			} else if(strYear == '') {
				alert("Please, select year.");
				cnt = 1;
			} else if(strMonth == '') {
				alert("Please, select month.");
				cnt = 1;
			}
		}
		
		if(parseInt(cnt) == 0) {
			var form_data = $("#frmTimesheet").serialize();
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ProjectTimesheets.action',
				data: form_data,
				success: function(result) {
					$("#divResult").html(result);
		   		}
			});
			/* document.frmTimesheet.submit(); */
		}
	}
	
	
	function checkAllTimesheetCheckedUnchecked() {
		var allPro = document.getElementById("allPro");		
		var proFreqId = document.getElementsByName('proFreqId');
		var cnt = 0;
		var chkCnt = 0;
		for(var i=0;i<proFreqId.length;i++) {
			cnt++;
			 if(proFreqId[i].checked) {
				 chkCnt++;
			 }
		 }
		
		if(parseFloat(chkCnt) > 0) {
			document.getElementById("disableSpan").style.display = 'none';
			document.getElementById("enableSpan").style.display = 'block';
		} else {
			document.getElementById("disableSpan").style.display = 'block';
			document.getElementById("enableSpan").style.display = 'none';
		}
		
		if(cnt == chkCnt) {
			allPro.checked = true;
		} else {
			allPro.checked = false;
		}
	}
	
	
	function checkUncheckAllTimesheet() {
		var allPro = document.getElementById("allPro");		
		var proFreqId = document.getElementsByName('proFreqId');
		if(allPro.checked == true) {
			 for(var i=0;i<proFreqId.length;i++) {
				 proFreqId[i].checked = true;
			 }
			 document.getElementById("disableSpan").style.display = 'none';
			 document.getElementById("enableSpan").style.display = 'block';
		} else {		
			 for(var i=0; i<proFreqId.length; i++) {
				 proFreqId[i].checked = false;
			 }
			 document.getElementById("disableSpan").style.display = 'block';
			 document.getElementById("enableSpan").style.display = 'none';
		}
	}

	
	function executeAllProjectAction(val, timesheetType, pageType) {
		
		var pros = document.getElementsByName('proFreqId');
		var i;
		var proIds = "";
		for (i = 0; i < pros.length; i++) {
		  if (pros[i].checked) {
			  if(proIds == "") {
				  proIds = proIds + pros[i].value;
			  } else {
				  proIds = proIds + "," + pros[i].value;
			  }
		  }
		}
		if(val == '1') {
			if(confirm('Are you sure, you want to approve this timesheet for billing?')) {
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					url: 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proFreqID='+proIds+'&operation=ApproveForBilling&pageType='+pageType,
					success: function(result) {
						$("#divResult").html(result);
			   		}
				});
			}
		} else if(val == '2') {
			if(confirm('Are you sure, you want to approve this timesheet and send to customer?')) {
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					url: 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proFreqID='+proIds+'&operation=ApproveAndSendToCustomer&pageType='+pageType,
					success: function(result) {
						$("#divResult").html(result);
			   		}
				});
			}
		} else if(val == '3') {
			//alert("proIds 1 --->> " + proIds);
			if(confirm('Are you sure, you want to save for later?')) {
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					url: 'ProjectTimesheets.action?timesheetType='+timesheetType+'&proFreqID='+proIds+'&operation=SaveLater&pageType='+pageType,
					success: function(result) {
						$("#divResult").html(result);
			   		}
				});
			}
		}
	}
	
	function executeEmpTimesheetActions(val, count, fromDate, toDate, empId, submitDate, timesheetType, pageType, paycycle) {
	//alert('paycycle==>'+paycycle);	
		if(val == '1') {
			if(confirm('Are you sure, you want to approve this timesheet?')) {
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					url: 'ProjectTimesheets.action?timesheetType='+timesheetType+'&fromDate='+fromDate+'&toDate='+toDate+'&submitDate='+submitDate+'&empId='+empId+'&operation=Approve&pageType='+pageType+'&paycycle='+paycycle,
					success: function(result) {
						$("#divResult").html(result);
			   		}
				});
			}
		} else if(val == '2') {
			if(confirm('Are you sure, you want to deny this timesheet?')) {
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					url: 'ProjectTimesheets.action?timesheetType='+timesheetType+'&fromDate='+fromDate+'&toDate='+toDate+'&submitDate='+submitDate+'&empId='+empId+'&operation=Deny&pageType='+pageType+'&paycycle='+paycycle,
					success: function(result) {
						$("#divResult").html(result);
			   		}
				});
			}
		}
		 document.getElementById("empTimesheetActions"+count).selectedIndex = '0';
	}
	
	
	function viewComments(proID, proFreqID) {
		 
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Deny Comment');
		$.ajax({
			url : 'ProjectTimesheetDenyByCustomer.action?proID='+proID+'&proFreqID='+proFreqID+'&operation=V',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function openFeedsForm(proFreqId, proId, timesheetType) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Task Feeds');
		$.ajax({
			url : 'FeedsPopup.action?pageFrom=Timesheet&proFreqId='+proFreqId+'&proId='+proId+'&pageType='+timesheetType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function importBulkTimeshhets() {
		removeLoadingDiv('the_div');
		var dialogEdit = '#importTimesheetDiv';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 350,
			width : 550,
			modal : true,
			title : 'Import Timesheet',
			open : function() {
				var xhr = $.ajax({
					url : 'ImportTimesheet.action',
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
		$(dialogEdit).dialog('open');
	}
	
</script>


<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>

<script type="text/javascript">
$(function() {
	$("#proId").multiselect().multiselectfilter();
	$("#projectOwner").multiselect().multiselectfilter();
	$("#client").multiselect().multiselectfilter();
	$("#projectType").multiselect().multiselectfilter();
	$("#projectFrequency").multiselect().multiselectfilter();
});
</script>

	<%  
		String timesheetType = (String)request.getAttribute("timesheetType");
		String pageType = (String)request.getAttribute("pageType");
		
		String strTitle = (String)request.getAttribute(IConstants.TITLE); 
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> alReport = (List<List<String>>)request.getAttribute("alReport");
		if(alReport == null) alReport = new ArrayList<List<String>>();
		
		List<List<String>> projectList = (List<List<String>>)request.getAttribute("projectList");
		if(projectList == null) projectList = new ArrayList<List<String>>();
		
		Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
		if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Timesheets" name="title" />
</jsp:include> --%>

<div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>

<%-- <%
	List couterlist = (List)session.getAttribute("alErrorReport");
	if(couterlist!=null) {
	%>
	<div style="margin-top: 10px;">
		<table style="width:100%;">
			<tbody>
			   <% for (int i=0; i<couterlist.size(); i++) { %>
			    <tr>
			 		<td align="left"><%= couterlist.get(i) %></td>
			    </tr>
			   <% } %>
			</tbody>
		</table>
	</div>
	<% } %> --%>
	
<%-- <% 	session.setAttribute(IConstants.MESSAGE, "");
	session.setAttribute("alErrorReport", null);
%> --%>

	<div style="float: right; margin-right: 3px; font-size: 14px;">
	 <% if(timesheetType == null || timesheetType.equals("") || timesheetType.equals("null") || timesheetType.equals("EA") || timesheetType.equals("EU")) { %>
	 	<span class="anaAttrib1" style="font-size: 26px;"><%=uF.parseToInt((String) request.getAttribute("proCnt")) %> </span> Timesheets
	 <% } else if(timesheetType != null && (timesheetType.equals("PC") || timesheetType.equals("PA") || timesheetType.equals("PU"))) { %>
		<span class="anaAttrib1" style="font-size: 26px;"><%=uF.parseToInt((String) request.getAttribute("proCnt")) %> </span> Timesheets <!-- font-weight: bolder; font-family: digital; color: green;  -->
	<% } %>	 
	</div>
	
	<!-- <div style="float: right; margin-top:3px; margin-right: 24px;"><a href="ImportTimesheet.action">Import TimeSheet</a></div> -->
	<div style="float: right; margin-top:3px; margin-right: 24px;"><a href="javascript:void(0);" onclick="importBulkTimeshhets();">Import Timesheet</a></div>

<% 
	//System.out.println("timesheetType ===>> " + timesheetType);
	if(timesheetType == null || timesheetType.equals("") || timesheetType.equals("null") || timesheetType.equals("EU") || timesheetType.equals("EA")) {
		String proCount = (String)request.getAttribute("proCount");
%>
	<script type="text/javascript">
		function loadMore(proPage, minLimit) {
			document.frmTimesheet.proPage.value = proPage;
			document.frmTimesheet.minLimit.value = minLimit;
			
			var form_data = $("#frmTimesheet").serialize();
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ProjectTimesheets.action',
				data: form_data,
				success: function(result) {
					$("#divResult").html(result);
		   		}
			});
			
			/* document.frmTimesheet.submit(); */
		}
	</script>
	
	<%-- <% if(pageType == null || !pageType.equals("MP")) { %>	
    	<form name="frmTimesheet" id="frmTimesheet" action="ProjectTimesheets.action" theme="simple" method="post">
    <% } else { %>
    	<form name="frmTimesheet" id="frmTimesheet" action="MyTeamTimesheets.action" theme="simple" method="post">
    <% } %> --%>
		
		<s:form name="frmTimesheet" id="frmTimesheet" action="ProjectTimesheets" theme="simple" method="post">
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
				    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
				    <div class="box-tools pull-right">
				        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
				<div class="box-body" style="padding: 5px; overflow-y: auto;">
					<s:hidden name="timesheetType" id="timesheetType" />
			    	<s:hidden name="proPage" id="proPage" />
			    	<s:hidden name="minLimit" id="minLimit" />
			    	<s:hidden name="filterBy" id="filterBy" />
			    	<s:hidden name="sortBy" id="sortBy" />
			    	<s:hidden name="pageType" id="pageType" />
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key="" onchange="sortByEmpTimesheets('P');"/>
							</div>
							<% if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_FILTER_PAYCYCLE_OR_MONTH))) { %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<div style="width: 150px; text-align: center; font-weight: bold;"> OR </div>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Year</p>
									<s:select theme="simple" name="strYear" id="strYear" cssStyle="width: 120px !important;" headerKey="" headerValue="Select Year" listKey="yearsID" listValue="yearsName" list="yearList" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" cssStyle="width: 120px !important;" headerKey="" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" value="Submit" class="btn btn-primary" onclick="sortByEmpTimesheets('O');"/>
								</div>
							<% } %>	
						</div>
					</div>
				</div>
			</div>
		
			<%
				String sbData = (String) request.getAttribute("sbData");
				String strSearchJob = (String) request.getAttribute("strSearchJob");
			%>
			<div style="float:left; font-size:12px; line-height:22px; width:514px; margin-left: 350px;margin-bottom: 10px;">
				<div class="col-sm-12 col-md-12 col-lg-12 no-padding no-padding">
					<input id="strSearchJob" class="form-control ui-autocomplete-input" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>" autocomplete="off" type="text">
					<input value="Search" class="btn btn-primary" onclick="sortByEmpTimesheets('');" type="button">
				</div>
			</div>
		       
			<script>
				$( "#strSearchJob" ).autocomplete({
					source: [ <%=uF.showData(sbData,"") %> ]
				});
			</script>
			
		</s:form>
	
	
	<table class="table form-table">
		<tr>
			<th>Status</th>
			<th>Employee Name</th>
			<th>Submitted On</th>
			<th>Approved By</th>
			<th>Total no. of D/H</th>
			<th>Actual Efforts (h)</th>
			<th>Billable Efforts (h)</th>
			
			<%if(uF.parseToBoolean(CF.getIsWorkFlow())) { %>
				<th class="alignLeft">Workflow</th>
			<% } %>
			<th>View | Download</th>
			<% if((timesheetType == null || timesheetType.equals("") || timesheetType.equals("null") || timesheetType.equals("EU")) || (strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.OTHER_HR)) && timesheetType != null && timesheetType.equals("EA"))) { %>
			<th>Action</th>
			<% } %>
		</tr>
	
	<%
	for(int i=0; alReport != null && i<alReport.size(); i++) {
		List<String> alInner = alReport.get(i);
	%>
		<tr>
			<td align="center"><%=alInner.get(0)%></td>
			<td><%=alInner.get(1)%></td>
			<td align="center"><%=alInner.get(2)%></td>
			<td><%=alInner.get(3)%></td>
			<td class="padRight20" align="right"><%=alInner.get(5)%> (<%=alInner.get(4)%>)</td>
			<td class="padRight20" align="right"><%=alInner.get(9)%></td>
			<td class="padRight20" align="right"><%=alInner.get(10)%></td>
			
			<%if(uF.parseToBoolean(CF.getIsWorkFlow())) { %>
				<td><%=(String)alInner.get(8) %></td>
			<% } %>
			<td><%=alInner.get(6)%> <%=alInner.get(7)%></td>
			<% if((timesheetType == null || timesheetType.equals("") || timesheetType.equals("null") || timesheetType.equals("EU")) || (strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.OTHER_HR)) && timesheetType != null && timesheetType.equals("EA"))) { %>
			<td>
				<% if(!uF.parseToBoolean(alInner.get(12))) { %>	
					<%=alInner.get(11) %>
				<% } %>
			</td>
			<% } %>
		</tr>
	
	<%} if(alReport.size()==0) { %>
	<tr><td colspan="10"><div style="width: 96%;" class="msg nodata"><span>No timesheet submitted in the selected filter</span></div></td></tr>
	<%} %>
	</table>
	
	
	<div style="text-align: center; float: left; width: 100%; margin-top: 10px;">
			
			<% int intproCnt = uF.parseToInt(proCount);
				int pageCnt = 0;
				int minLimit = 0;
				
				for(int i=1; i<=intproCnt; i++) { 
						minLimit = pageCnt * 100;
						pageCnt++;
			%>
			<% if(i ==1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				if(uF.parseToInt(strPgCnt) > 1) {
					 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)-100) + "";
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
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
					<%="< Prev" %></a>
				<% } else { %>
					<b><%="< Prev" %></b>
				<% } %>
				</span>
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
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
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
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
				 strMinLimit = (uF.parseToInt(strMinLimit)+100) + "";
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
			
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
				<% } else { %>
					<b><%="Next >" %></b>
				<% } %>
				</span>
			<% } %>
			<%} %>
			
			</div>
	

<% } else if(timesheetType != null && (timesheetType.equals("PC") || timesheetType.equals("PA") || timesheetType.equals("PU"))) {
	String proCount = (String)request.getAttribute("proCount");
%>
	<script type="text/javascript">
		function loadMore(proPage, minLimit) {
			document.frm_ProjectTimesheet.proPage.value = proPage;
			document.frm_ProjectTimesheet.minLimit.value = minLimit;
			var form_data = $("#frm_ProjectTimesheet").serialize();
			//alert("form_data ===>> " + form_data);
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ProjectTimesheets.action',
				data: form_data,
				success: function(result) {
					$("#divResult").html(result);
		   		}
			});
			/* document.frm_ProjectTimesheet.submit(); */
		}
	</script>


	<s:form name="frm_ProjectTimesheet" id="frm_ProjectTimesheet" action="ProjectTimesheets" theme="simple" method="post">
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
				    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
				    <div class="box-tools pull-right">
				        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
				<div class="box-body" style="padding: 5px; overflow-y: auto;">
					<s:hidden name="timesheetType" id="timesheetType"/>
			    	<s:hidden name="proPage" id="proPage"/>
			    	<s:hidden name="minLimit" id="minLimit"/>
			    	<s:hidden name="sortBy" id="sortBy"/>
			    	<s:hidden name="pageType" id="pageType"/>

					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Project</p>
								<s:select theme="simple" name="proId" id="proId" listKey="projectID" listValue="projectName" list="projectdetailslist" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Project Owner</p>
								<s:select theme="simple" name="projectOwner" id="projectOwner" listKey="proOwnerId" listValue="proOwnerName" list="projectOwnerList" key="" multiple="true"/>
							</div>
							<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Customer</p>
									<s:select theme="simple" name="client" id="client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true"/>
								</div>
							<% } %>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Project Type</p>
								<s:select theme="simple" name="projectType" id="projectType" listKey="billingId" listValue="billingName" list="billingBasisList" key=""  multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Project Frequency</p>
								<s:select theme="simple" name="projectFrequency" id="projectFrequency" listKey="billingId" listValue="billingName" list="billingFreqList" key=""  multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" value="Submit" class="btn btn-primary" onclick="sortByTimesheets();"/>
							</div>
						</div>
					</div>
				</div>
			</div>
		
			<%
				String sbData = (String) request.getAttribute("sbData");
				String strSearchJob = (String) request.getAttribute("strSearchJob");
			%>
			<div style="float:left; font-size:12px; line-height:22px; width:514px; margin-left:350px; margin-bottom:10px;">
				<div class="col-sm-12 col-md-12 col-lg-12 no-padding no-padding">
					<input id="strSearchJob" class="form-control ui-autocomplete-input" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>" autocomplete="off" type="text">
					<input value="Search" class="btn btn-primary" onclick="sortByTimesheets();" type="button">
				</div>
			</div>
		       
		       <script>
				$( "#strSearchJob" ).autocomplete({
					source: [ <%=uF.showData(sbData,"") %> ]
				});
			</script>
			
		</s:form>
		
		<div class="row">
            <div class="col-md-12" style="margin-bottom: 7px;">
				<% if((timesheetType != null && !timesheetType.equals("PA") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (timesheetType != null && timesheetType.equals("PC") && strUserType!=null && strUserType.equals(IConstants.CUSTOMER))) { %>
					<div style="float:right; margin-top: 8px; margin-right: 1%;">
						<span id="disableSpan">
							<select name="allProActions" id="allProActions" style="width: 120px !important;" disabled="disabled">
		                  		<option value="">Bulk Action</option>
		                  	</select>
						</span>
						<span id="enableSpan" style="display: none;">
							<select name="allProActions" id="allProActions" style="width: 120px !important;" onchange="executeAllProjectAction(this.value, '<%=timesheetType %>', '<%=pageType %>');">
		                  		<option value="">Bulk Action</option>
			                  	<option value="1">Approve for Billing</option>
			                  	<option value="2">Approve & Send to Customer</option>
		                  	<% if(timesheetType != null && timesheetType.equals("PC") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
		                  		<option value="3">Save for Later</option>
		                  	<% } %>
		                	</select>
						</span>
					</div>
				<% } %>	
			</div>
		</div>
		
	
	<table class="table form-table">
		<tr>
		<% if((timesheetType != null && !timesheetType.equals("PA") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (timesheetType != null && timesheetType.equals("PC") && strUserType!=null && strUserType.equals(IConstants.CUSTOMER))) { %>
			<th><input type="checkbox" name="allPro" id="allPro" onclick="checkUncheckAllTimesheet();" /></th>
		<% } %>	
			<th>Status</th>
			<th>Project</th>
			<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
				<th>Customer</th>
			<% } %>
			<th>Project Owner</th>
			<th>Project Type (Project Frequency)</th>
			<th>Period</th>
			<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
				<th>Submissions</th>
			<% } %>
			<th>Approved By</th>
			<th>Client Approval</th>
			<th>Total Efforts</th>
			<th>Billable Efforts</th>
			<%-- <th>View</th>
			<%if(uF.parseToBoolean(CF.getIsWorkFlow())){ %>
				<th class=" alignLeft">Workflow</th>
			<%} %> --%>
			<th>View | Download</th>
			<% if((timesheetType != null && !timesheetType.equals("PA") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (timesheetType != null && timesheetType.equals("PC") && strUserType!=null && strUserType.equals(IConstants.CUSTOMER))) { %>
				<th>Action</th>
			<% } %>
			<!-- <th>Feed</th> -->
		</tr>
	
	<%
	for(int i=0; projectList!= null && i<projectList.size(); i++) {
		List<String> alInner = (List<String>)projectList.get(i);
		//System.out.println("alInner.size --->> " + alInner.size());
	%>
		<tr>
			<% if((timesheetType != null && !timesheetType.equals("PA") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (timesheetType != null && timesheetType.equals("PC") && strUserType!=null && strUserType.equals(IConstants.CUSTOMER))) { %>
				<td><input type="checkbox" value="<%=alInner.get(12) %>" name="proFreqId" id="proFreqId" onclick="checkAllTimesheetCheckedUnchecked();"/></td>
			<% } %>
			<td align="center"><%=alInner.get(11) %></td>
			<td><%=alInner.get(1)%></td>
			<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
				<td align="center"><%=alInner.get(2)%></td>
			<% } %>
			<td nowrap="nowrap"><%=alInner.get(3)%></td>
			<td><%=alInner.get(4)%> (<%=alInner.get(5)%>)</td>
			<td><%=alInner.get(6)%></td>
			<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
				<td><%=alInner.get(16)%></td>
			<% } %>
			<td><%=alInner.get(7)%></td>
			<td><%=alInner.get(15)%></td>
			<td nowrap="nowrap" class="padRight20" align="right"><%=alInner.get(9)%></td>
			<td nowrap="nowrap" class="padRight20" align="right"><%=alInner.get(10)%></td>
			<td>
			<% if(uF.parseToBoolean(alInner.get(13))) { %>
				<a href="CustomerProjectTimesheet.action?proId=<%=alInner.get(0) %>&proFreqId=<%=alInner.get(12) %>">View</a>
				<%=alInner.get(14) %>
			<% } else { %>
				-
			<% } %>
			</td>
			<% if((timesheetType != null && !timesheetType.equals("PA") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (timesheetType != null && timesheetType.equals("PC") && strUserType!=null && strUserType.equals(IConstants.CUSTOMER))) { %>
			<td>
				<select name="proActions<%=alInner.get(0) %>_<%=alInner.get(12) %>" id="proActions<%=alInner.get(0) %>_<%=alInner.get(12) %>" style="width: 120px !important;" onchange="executeProjectActions(this.value, '<%=alInner.get(0) %>', '<%=alInner.get(12) %>', '<%=timesheetType %>', '<%=pageType %>');">
                  	<option value="">Action</option>
                  	<% if(uF.parseToBoolean(alInner.get(13))) { %>
	                  	<option value="1">Approve for Billing</option>
	                  	<% if(uF.parseToInt(alInner.get(17)) == 0 || (uF.parseToInt(alInner.get(17)) == -1 && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER))) { %>
		                	<option value="2">Approve & Send to Customer</option>
		                <% } %>
	                <% } %>
                  	<% if(timesheetType != null && timesheetType.equals("PC") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
                  		<option value="3">Save for Later</option>
                  	<% } %>
                  	<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER)) { %>
                  		<option value="4">Deny</option>
                  	<% } %>
                </select>
			</td>
			<% } %>
			<%-- <td><a href="javascript:void(0);" onclick="openFeedsForm('<%=alInner.get(12) %>', '<%=alInner.get(0) %>', '<%=timesheetType %>');" title="click here for feed">
					<img src="images1/icons/feed.png" height="16" width="16">
				</a>
			</td> --%>
		</tr>
	
	<% } if(projectList == null || projectList.size()==0) { %>
	<tr><td colspan="14"><div class="msg nodata"><span>No timesheet submitted </span></div></td></tr>
	<% } %>
	</table>
	
	
	<div style="text-align: center; float: left; width: 100%; margin-top: 10px;">
			
			<% int intproCnt = uF.parseToInt(proCount);
				int pageCnt = 0;
				int minLimit = 0;
				
				for(int i=1; i<=intproCnt; i++) {
					minLimit = pageCnt * 100;
					pageCnt++;
			%>
			<% if(i ==1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				if(uF.parseToInt(strPgCnt) > 1) {
					 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)-100) + "";
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
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
					<%="< Prev" %></a>
				<% } else { %>
					<b><%="< Prev" %></b>
				<% } %>
				</span>
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
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
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
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
				 strMinLimit = (uF.parseToInt(strMinLimit)+100) + "";
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
			
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
				<% } else { %>
					<b><%="Next >" %></b>
				<% } %>
				</span>
			<% } %> 
			<%} %>
			
			</div>
	<% } %> 
</div>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:auto;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>