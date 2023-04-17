<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%String strUserType = (String) session.getAttribute(IConstants.USERTYPE); %>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/highslide/highslide-with-html.js"> </script>
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/highslide/highslide.css" />

<script type="text/javascript">
hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';

/* function show_employees() { 
	dojo.event.topic.publish("show_employees");
} */
  
$(function() {
	
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(200);
		$(".modal-body").width(350);
		$("#modalInfo").hide();
    });
	
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(200);
		$(".modal-body").width(350);
		$("#modalInfo").hide();
	});
	
});


/* ===start parvez date: 31-01-2022 ---Note: added empReason in the argument=== */
function validateReason(strStartTime, strEndTime, strReason, EMPID, DT, SID, S, AST, AET, divid, exceptionMode, empReason) {
/* ===end parvez date: 31-01-2022=== */
	alert(empReason);
	if(strReason=="") {
		alert('Please enter the valid reason');
		//return false;
	}else{
	
	/* ===start parvez date: 31-01-2022=== */	
		var action='UpdateException.action?S='+S+'&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&AST='+AST+'&AET='+AET+'&strReason='+strReason
			+'&exceptionMode='+exceptionMode+'&strStartTime='+strStartTime+'&strEndTime='+strEndTime+'&employeeReason='+empReason;
		
	/* ===end parvez date: 31-01-2022=== */
		getContent(divid, action);
	}
	$("#modalInfo").hide();
	//$(dialogEdit).dialog('close'); 
	//$(dialogEdit1).dialog('close'); 
	//return true; 
}


function approveException(empname,SID,EMPID,DT,AST,AET,divid, exceptionMode) { 
	/* alert("In approveExceptionn=="+DT); */
	var st='';
	var et='';
	if(AET!='') {
		if(document.getElementById(AET)) {
			et=document.getElementById(AET).value;
		}
	}
	//alert("et====>"+et);
	if(AST!='') {
		if(document.getElementById(AST)) {
			st = document.getElementById(AST).value;
		}
	}
	//alert(empname);
	//alert(SID);
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('Approve Exception for '+empname);
	$("#modalInfo").show();
	$.ajax({
		url : 'ApproveDenyReason.action?S=1&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&AET='+et+'&AST='+st+'&divid='+divid
			+'&exceptionMode='+exceptionMode,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
	
function denyException(empname,SID,EMPID,DT,AST,AET,divid, exceptionMode) { 
	var st='';
	var et='';
	if(AET!=''){
		if(document.getElementById(AET)) {
			et=document.getElementById(AET).value;
		}	
	}
	if(AST!=''){
		if(document.getElementById(AST)) {
			st=document.getElementById(AST).value;
		}	
	}
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('Deny Exception for '+empname);
	$("#modalInfo").show();
	$.ajax({
		url : 'ApproveDenyReason.action?S=-1&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&AET='+et+'&AST='+st+'&divid='+divid+'&exceptionMode='+exceptionMode,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function approveAndDenyException(empname, apStatus, S, SID, EID, DATE, divid, strReportDate) { 
	//alert("In approveAndDenyException");
	//alert(empname);
	
	var titl='Approve';
	if(S=='-1') {
		denyClockEntry(empname, S, SID, EID, DATE, divid,strReportDate);	
	} else {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html(titl+' Exception for '+empname);
		$("#modalInfo").show();
		$.ajax({
			url : 'AddClockEntries.action?apStatus='+apStatus+'&S='+S+'&SID='+SID+'&EID='+EID+'&DATE='+DATE+'&divid='+divid,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
}

//Start Dattatray Date:10-11-21 Note:added strApproveByReason
function updateSttings(DATE,EID,SID,AS,AE,divid,startTime,endTime,strE,e,strApStatusTmp,strOTMinuteStatus) {
	if(startTime=="") {
		alert('Please enter the valid start time');
	} else if(endTime=="") {
		alert('Please enter the valid end time');
	} else {
		var strServiceId=SID;
		var strEmpId=EID;
		var strDate=DATE;
		var strStatus='';
		var action='AddClockEntries.action?strDate='+DATE+'&strEmpId='+EID+'&EID='+EID+'&strServiceId='+SID+'&strStatus=&strE='+strE
			+'&divid='+divid+'&strStartTime='+startTime+'&strEndTime='+endTime+'&E='+e+'&strApStatusTmp='+strApStatusTmp
			+'&strOTMinuteStatus='+strOTMinuteStatus;

		getContent(divid, action);
		$("#modalInfo").hide();
	}
}

function denyClockEntry(empname, S, SID, EID, DATE, divid, strReportDate){
	if(confirm('Are you sure you wish to deny '+empname+'s exception for '+strReportDate+'?')){
			var action='AddClockEntries.action?DATE='+DATE+'&strEmpId='+EID+'&EID='+EID+'&strServiceId='+SID+'&SID='+SID+'&S='+S+'&divid='+divid;
			//alert("action ===>> " + action);
			getContent(divid, action);
		}
}

function submitForm(type){
	/* var org = document.getElementById("f_org").value; */
	var paycycle = document.getElementById("paycycle").value;
	var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	var org = "";
	var location = "";
	var department = "";
	var level = "";
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	if(document.getElementById("strLocation")) {
		location = getSelectedValue("strLocation");
	}
	if(document.getElementById("strDepartment")) {
		department = getSelectedValue("strDepartment");
	}
	if(document.getElementById("strLevel")) {
		level = getSelectedValue("strLevel");
	}
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&paycycle='+paycycle;
	} 
	if(type=='3') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&paycycle='+paycycle
		+'&strSelectedEmpId='+strSelectedEmpId;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'UpdateClockEntries.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
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


function approveDenyHDFDException(empname,SID,EMPID,DT,status,divid,exceptionType) {
	//alert("In approveDenyException");
	var strTitle = "Approve";
	if(status == -1) {
		strTitle = "Deny";
	}
	var excpType = "Half Day ";
	if(exceptionType == 'FD') {
		excpType = "Full Day ";
	}
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html(strTitle+' '+excpType+'Exception for '+empname);
	$("#modalInfo").show();
	$.ajax({
		url : 'ApproveDenyReason.action?exceptionType='+exceptionType+'&S='+status+'&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&divid='+divid,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function validateHDFDReason(strReason,EMPID,DT,SID,S,divid,exceptionType) {
	if(strReason=="") {
		alert('Please enter the valid reason');
	} else {
		var action='UpdateException.action?S='+S+'&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&strReason='+strReason+'&exceptionType='+exceptionType;
		getContent(divid, action);
	}
	$("#modalInfo").hide();
}


function getApprovalStatus(exceptionid, empname) {
	//alert("getApprovalStatus 1");
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Work flow of '+empname);
	 $.ajax({
		url : "GetLeaveApprovalStatus.action?effectiveid="+exceptionid+"&type=14",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
function selectAll(x) {
	var status=x.checked; 
	var arr= document.getElementsByName("strReimId");
	for(i=0; i<arr.length; i++) {
  		arr[i].checked=status;
 	}
}

function checkAll() {
		var strAllReimId = document.getElementById("strAllReimId");		
		var strReimId = document.getElementsByName('strReimId');
		var cnt = 0;
		var chkCnt = 0;
		for(var i=0; i<strReimId.length; i++) {
			cnt++;
			 if(strReimId[i].checked) {
				 chkCnt++;
			 }
		 }
		if(cnt == chkCnt) {
			strAllReimId.checked = true;
		} else {
			strAllReimId.checked = false;
		}
	}


/*
function approveException(empname,SID,EMPID,DT,AST,AET,divid) { 
	var st='';
	var et='';
	if(AET!=''){
		et=document.getElementById(AET).value;	
	}
	//alert("et====>"+et);
	if(AST!=''){
		st=document.getElementById(AST).value;	
	}
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('Approve Exception for '+empname);
	$("#modalInfo").show();
	$.ajax({
		url : 'ApproveDenyReason.action?S=1&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&AET='+et+'&AST='+st+'&divid='+divid,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}*/

function bulkApproveDeny(status, strUserType1) {
	var arr= document.getElementsByName("strReimId");
	if(arr.length > 0) {
		var strReim="";
		var x=0;
		
		for(i=0;i<arr.length;i++) {
			if(arr[i].checked == true) {
		  		if(x==0) {
		  			strReim = arr[i].value;
		  		} else {
		  			strReim +=","+ arr[i].value;
		  		}
		  		x++;
			}
	 	}
		//alert("in bulkApprove");
		//alert(status);
		//alert(strReim);
		
		var empname = document.getElementById("empName1").value;
		//alert(document.getElementById("empName1").value);
		if(x > 0) {
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$('.modal-title').html('Approve Exception for '+empname);
			$("#modalInfo").show();
	    
	    	//alert("strUserType1 ===>> " + strUserType1);
			if(status=='1') {
				$.ajax({
					url : 'ApproveDenyReason.action?T=bulk&M=AA&S=1&RID='+strReim+'&userType='+strUserType1,
					//url : 'ApproveDenyReason.action?S=1&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&AET='+et+'&AST='+st+'&divid='+divid,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				
			} else if(status=='-1') {
				if(confirm('Are you sure, you want to deny the selected expenses?')) {
					var reason = window.prompt("Please enter your denial reason.");
					if (reason != null) {
						$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
						$.ajax({
							type : 'POST',
							url : 'ApproveDenyReason.action?T=bulk&M=AA&S=1&RID='+strReim+'&userType='+strUserType1,
							//url : 'ApproveDenyReason.action?S=1&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&AET='+et+'&AST='+st+'&divid='+divid,
							success: function(result) {
							$(dialogEdit).html(data);
					   		}
							
						});
						//window.location='UpdateReimbursements.action?T=bulk&M=AA&S=-1&RID=' + strReim +'&mReason='+reason;
					}
				}
			}
		} else {
			alert('Please select the expense.');
		}
	} else {
		alert('Please select the expense.');
	}
}
</script>


<%
	
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	//String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String strUserTypeId = (String)session.getAttribute(IConstants.USERTYPEID);

	String DATE = request.getParameter("DATE");
	String EMPID = request.getParameter("EMPID");
	String date = request.getParameter("strDATE");
	String strDate = request.getParameter("strDate");
	String strPC = request.getParameter("PC");

	String strD1 = request.getParameter("D1");
	String strD2 = request.getParameter("D2");

	String paycycle = (String) request.getAttribute("paycycle");
	Map hmEmpData = (Map) request.getAttribute("hmEmpData");

	List<String> alSalPaidEmpList = (List<String>) request.getAttribute("alSalPaidEmpList");
	if (alSalPaidEmpList == null)
		alSalPaidEmpList = new ArrayList<String>();

	String[] strPayCycleDates = null;

	if (paycycle != null) {
		strPayCycleDates = paycycle.split("-");
		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];

	} else {

		strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];
	}

	String strType = (String) request.getParameter("T");
	String PAY = (String) request.getParameter("PAY");

	UtilityFunctions uF = new UtilityFunctions();

	if (strDate != null) {
		date = strDate;
	} else if (DATE != null) {
		date = DATE;
	} else if (date == null || (date != null && date.equalsIgnoreCase(""))) {
		date = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", IConstants.DBDATE, CF.getStrReportDateFormat());
	}

	Map<String, String> hmEmpLevelMap = (Map<String, String>) request.getAttribute("hmEmpLevelMap");
	if (hmEmpLevelMap == null)
		hmEmpLevelMap = new HashMap<String, String>();
	Map<String, Set<String>> hmWeekEndHalfDates = (Map<String, Set<String>>) request.getAttribute("hmWeekEndHalfDates");
	if (hmWeekEndHalfDates == null)
		hmWeekEndHalfDates = new HashMap<String, Set<String>>();
	Map<String, Set<String>> hmWeekEndDates = (Map<String, Set<String>>) request.getAttribute("hmWeekEndDates");
	if (hmWeekEndDates == null)
		hmWeekEndDates = new HashMap<String, Set<String>>();
	Map<String, String> hmEmpWlocation = (Map<String, String>) request.getAttribute("hmEmpWlocation");
	if (hmEmpWlocation == null)
		hmEmpWlocation = new HashMap<String, String>();
	List<String> alEmpCheckRosterWeektype = (List<String>) request.getAttribute("alEmpCheckRosterWeektype");
	if (alEmpCheckRosterWeektype == null)
		alEmpCheckRosterWeektype = new ArrayList<String>();
	Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>) request.getAttribute("hmRosterWeekEndDates");
	if (hmRosterWeekEndDates == null)
		hmRosterWeekEndDates = new HashMap<String, Set<String>>();

	Map<String, String> hmHolidays = (Map<String, String>) request.getAttribute("hmHolidays");
	if (hmHolidays == null)
		hmHolidays = new HashMap<String, String>();
	Map<String, String> hmHolidayDates = (Map<String, String>) request.getAttribute("hmHolidayDates");
	if (hmHolidayDates == null)
		hmHolidayDates = new HashMap<String, String>();

%>




<%
	String strTitle = "Clock On/Off Exceptions";
%>
<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
</jsp:include>	 --%>

		<div class="box-header with-border">
			<h4 class="box-title">
				<div class="pagetitle" style="margin: 0px;">
					<% if ((String) hmEmpData.get("NAME") != null) { %>
					<%="Showing exceptions for " + (String) hmEmpData.get("NAME") + " for pay cycle " + uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) + "-" + uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())%>
					<% } else { %>
					<%="Showing exceptions for all employees for pay cycle " + uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) + "-" + uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())%>
					<% } %>
				</div>
			</h4>
		</div>
		<div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
				
				<div class="box box-default">  <!-- collapsed-box -->
					<%-- <div class="box-header with-border">
					    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
					    <div class="box-tools pull-right">
					        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					    </div>
					</div> --%>
					<div class="box-body" style="padding: 5px; overflow-y: auto;">
						<s:form theme="simple" name="frm_roster_actual1" id="UpdateClockEntries" action="UpdateClockEntries" method="POST" cssClass="formcss">
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER))) { %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organization</p>
										<s:select name="f_org" id="f_org" listKey="orgId" list="organisationList" listValue="orgName" headerKey="0" onchange="submitForm('1');"/>
									</div>
									<% } %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select theme="simple" label="Select Pay Cycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" list="payCycleList" key="" required="true" onchange="submitForm('2');" />
									</div>
									<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Employee</p>
											<s:select theme="simple" label="Select Employee" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeName" headerKey="0" headerValue="Select Employee" list="empNamesList" key="" required="true"  onchange="submitForm('3');"/>
										</div>
									<% } %>
									<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER))) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Location</p>
											<s:select theme="simple" name="strLocation" id="strLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" headerKey="" headerValue="Select Location" onchange="submitForm('2');"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Department</p>
											<s:select name="strDepartment" id="strDepartment" list="departmentList" listKey="deptId" listValue="deptName" headerKey="" headerValue="Select Department" onchange="submitForm('2');" />
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Level</p>
											<s:select theme="simple" name="strLevel" id="strLevel" listKey="levelId" listValue="levelCodeName" list="levelList" key="" headerKey="" headerValue="Select Level" onchange="submitForm('2');"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">&nbsp;</p>
											<input type="hidden" name="filterType" value="filter" />
											<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('3');" />
										</div>
									<% } %>
								</div>
							</div><br>
						</s:form>
					</div>
				</div>
				<%-- <div class="col-md-12" style="margin: 0px 0px 10px;">
					<input type="button" name="btnBulkApprove" value="Bulk Approve" class="btn btn-primary" onclick="bulkApproveDeny('1', '<%=strUserTypeId %>');"/>
					<input type="button" name="btnBulkDeny" value="Bulk Deny" class="btn btn-danger" onclick="bulkApproveDeny('-1', '<%=strUserTypeId %>');"/>
				</div> --%>
				<table class="table table-bordered" id="lt">
					<!-- <thead>
						<tr>
							<th style="text-align: left; width: 4%;"><input type="checkbox" name="strAllReimId" id="strAllReimId" onclick="selectAll(this)" checked></th>
							<th style="text-align: left; width: 80%;">Exceptions</th>
						</tr>
					</thead> -->
				
				<%
					List alEmp = (List) request.getAttribute("TIMESHEET_EMP"); 
					Map<String, List<String>> hmEmpSbu = (Map<String, List<String>>) request.getAttribute("hmEmpSbu");
					if (hmEmpSbu == null)
						hmEmpSbu = new HashMap<String, List<String>>();

					if (alEmp == null) {
						alEmp = new ArrayList();
					}
					Map hmTime = (Map) request.getAttribute("TIMESHEET_");
					
					Map hmEmpName = (Map) request.getAttribute("TIMESHEET_EMPNAME");
					
					Map hmServicesMap = (Map) request.getAttribute("TIMESHEET_SERVICENAME");
					
					Map hmExceptionReason = (Map) request.getAttribute("hmExceptionReason");
					
					Map hmGenderMap = (Map) request.getAttribute("hmGenderMap");

					List alDt = (List) request.getAttribute("TIMESHEET_DATE");
					List alService = (List) request.getAttribute("TIMESHEET_SERVICE");

					/* out.println("<br/>"+alDt);
					out.println("<br/>"+hmTime);
					out.println("<br/>"+alEmp);
					 */
					 
					 
					/* System.out.println("hmTime==>"+hmTime);
					System.out.println("hmServicesMap==>"+hmServicesMap);
					System.out.println("hmExceptionReason==>"+hmExceptionReason); */
					 

					int nCount = 0;
					int rowCount = 0;
					String strColour = "";
					boolean isRowCountFirst = false;
					int i = 0;
					//int k=0;
					for (i = 0; i < alEmp.size(); i++) {
					
						Map hmExceptionInner = (Map) hmExceptionReason.get((String) alEmp.get(i));
						if (hmExceptionInner == null)
							hmExceptionInner = new HashMap();

						List<String> alEmpSbu = hmEmpSbu.get((String) alEmp.get(i));
						if (alEmpSbu == null)
							alEmpSbu = new ArrayList<String>();

						String strWLocationId = hmEmpWlocation.get((String) alEmp.get(i));
						Set<String> weeklyOffSet = hmWeekEndDates.get(strWLocationId);
						if (weeklyOffSet == null)
							weeklyOffSet = new HashSet<String>();

						Set<String> halfDayWeeklyOffSet = hmWeekEndHalfDates.get(strWLocationId);
						if (halfDayWeeklyOffSet == null)
							halfDayWeeklyOffSet = new HashSet<String>();

						Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get((String) alEmp.get(i));
						if (rosterWeeklyOffSet == null)
							rosterWeeklyOffSet = new HashSet<String>();

						//for(k=0; k<alDt.size(); k++){
						//System.out.println("VCE1.jsp/624--alDt ===>> " + alDt);
						 for (int k = alDt.size() - 1; k >= 0; k--) {
							%>
						
						<% 
						for (int j = 0; j < alService.size(); j++) {
							if (!alEmpSbu.contains(alService.get(j))) {
								continue;
							}

							Map hmAS = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_AS");
							Map hmAE = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_AE");
							Map hmRS = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_RS");
							Map hmRE = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_RE");
							Map hmReason_OUT = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_OUT_REASON");
							Map hmApprove_OUT = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_OUT_APPROVE");
							Map hmReason_IN = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_IN_REASON");
							Map hmApprove_IN = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_IN_APPROVE");

							/* if(uF.parseToInt((String)alEmp.get(i))==608){
								System.out.println("VCE1.jsp/647--"+(String)alDt.get(k)+"=====hmApprove_OUT====>"+hmApprove_OUT);
							} */
							//out.println("<br/><br/> 222 ===>"+(String)alDt.get(k));
							//out.println("<br/>hmAS="+hmAS);
							//out.println("<br/>hmAE="+hmRE);
							
								System.out.println((String)alDt.get(k)+"=====hmRS====>"+hmRS);
								System.out.println((String)alDt.get(k)+"=====hmRE====>"+hmRE);

							if (hmAS == null) {
								hmAS = new HashMap();
							}
							if (hmAE == null) {
								hmAE = new HashMap();
							}
							if (hmRS == null) {
								hmRS = new HashMap();
							}
							if (hmRE == null) {
								hmRE = new HashMap();
							}
							if (hmReason_IN == null) {
								hmReason_IN = new HashMap();
							}
							if (hmApprove_IN == null) {
								hmApprove_IN = new HashMap();
							}
							if (hmReason_OUT == null) {
								hmReason_OUT = new HashMap();
							}
							if (hmApprove_OUT == null) {
								hmApprove_OUT = new HashMap();
							}

							//		out.println("<br/><br/>"+hmAS);
							//		out.println("<br/><br/>"+hmAE);

							isRowCountFirst = false;
				%>

						<%
							//	out.println("<br/><br/>"+hmRS);
							String strTemp = (String) hmRE.get((String) alEmp.get(i));
							//	if(strTemp!=null){
							rowCount++;
							isRowCountFirst = true;
							if (rowCount % 2 == 0) {
								strColour = "1";
							} else {
								strColour = "";
							}
						%>
					
						<%
						/* System.out.println("<br/>hmAE===>"+hmAE);
						System.out.println("<br/>(String)alEmp.get(i)===>"+(String)alEmp.get(i));
						System.out.println("<br/>(String)alEmp.get(i)===>"+(String)alDt.get(k) ); */
						
						if ((String) hmAE.get((String) alEmp.get(i)) != null && ((String) hmAE.get((String) alEmp.get(i))).length() > 0) {
							
							
						%>
					<tr>
					<!-- <td>
						<input name="strReimId" value="3974" onclick="checkAll();" checked="" type="checkbox">
					</td> -->
					<td>
					<div class="exceptions">

					<% if ((String) hmReason_OUT.get((String) alEmp.get(i)) != null) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval" ></i>
					<% } %>
			<!-- ===start parvez date: 06-12-2021=== -->
					<%=(String) hmEmpName.get((String) alEmp.get(i))%>
			<!-- ===end parvez date: 06-12-2021=== -->
					clocked off for <strong><%=(String) hmServicesMap.get((String) alService.get(j))%></strong> at 
					<input style="width: 65px !important;" type="text" name="strEmpOUT" id="strEmpOUT<%=i%>_<%=j%>_<%=k%>" value="<%=(String) hmAE.get((String) alEmp.get(i))%>" readonly="readonly" /> hrs on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>
					and
					<%=(("F".equalsIgnoreCase((String) hmGenderMap.get((String) alEmp.get(i))) ? "her" : "his"))%>
					end time was
					<%=(String) hmRE.get((String) alEmp.get(i))%>hrs
					<% if ((String) hmReason_OUT.get((String) alEmp.get(i)) != null) { 
					//System.out.println("VCE1.jsp/727--hmEmpName.get((String) alEmp.get(i):"+hmEmpName.get((String) alEmp.get(i)));
					%>
					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">Reason given...</a>
					<div class="highslide-maincontent">
						<h4>Given Reason</h4>
						<%=(String) (hmReason_OUT.get((String) alEmp.get(i)) != null && !hmReason_OUT.get((String) alEmp.get(i)).equals("null") ? hmReason_OUT.get((String) alEmp.get(i)) : "Not Specified")%>
					</div>
					
					
					<span id="myDivO<%=i%>_<%=j%>_<%=k%>"> 
				
						<%if (alSalPaidEmpList.contains((String) alEmp.get(i))) {%> 
							<font size="1"><i>(Payroll has been processed for this date.)</i> </font> 
						<%} else {%>
						<%-- <% System.out.println("VCE1.jsp/749--out else ===>> "); %> --%>
						
							<a href="javascript:void(0);" onclick="approveException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','','strEmpOUT<%=i%>_<%=j%>_<%=k%>','myDivO<%=i%>_<%=j%>_<%=k%>', 'OUT');"><i class="fa fa-check-circle checknew" aria-hidden="true"></i> </a>
							<a href="javascript:void(0);" onclick="denyException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','','strEmpOUT<%=i%>_<%=j%>_<%=k%>','myDivO<%=i%>_<%=j%>_<%=k%>', 'OUT');"><i class="fa fa-times-circle cross" aria-hidden="true" style="width: 16px;"></i> </a>
							
							
						<% } %>
					</span>

				</div>
				</td>
				</tr>
				<%
					nCount++;
						}

					} else if (((String) hmAE.get((String) alEmp.get(i))) != null && ((String) hmAE.get((String) alEmp.get(i))).length() > 0 && ((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j))) != null && uF.parseToInt((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+ "_STATUS")) == 0) {
						
				%>
				<tr>
					<!-- <td>
						<input name="strReimId" value="3974" onclick="checkAll();" checked="" type="checkbox">
					</td> -->
					<td>
				<div class="exceptions">
				<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j))) != null) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval"></i>
					<% } else { %>
					 <i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for your reasons"></i>
					<% } %>

					<%=(String) hmEmpName.get((String) alEmp.get(i))%>
					did not clock off for <strong><%=(String) hmServicesMap.get((String) alService.get(j))%></strong> on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>, however,
					<%=(("F".equalsIgnoreCase((String) hmGenderMap.get((String) alEmp.get(i))) ? "her" : "his"))%>
					end time was
					<%=(String) hmRE.get((String) alEmp.get(i))%>hrs <a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">Reason given...</a>
					<div class="highslide-maincontent">
						<h4>Given Reason</h4>
						<%=(String) (hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)) != null && !hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)).equals("null") ? hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)) : "Not Specified")%>
					</div>
					<%-- <input style="width: 65px !important;" type="text" name="strEmpOUT" id="strEmpOUT<%=i%>_<%=j%>_<%=k%>" readonly="readonly"/> --%>

					
					<span id="myDivO<%=i%>_<%=j%>_<%=k%>"> 
					<% if (alSalPaidEmpList.contains((String) alEmp.get(i))) { %>
						<font size="1"><i>(Payroll has been processed for this date.)</i> </font> 
					<% } else { %>
					<%-- <% System.out.println("VCE1.jsp/798--out else ===>> "); %> --%>
						<a href="javascript:void(0);" onclick="approveException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','','strEmpOUT<%=i%>_<%=j%>_<%=k%>','myDivO<%=i%>_<%=j%>_<%=k%>', 'OUT');"><i class="fa fa-check-circle checknew" aria-hidden="true"></i></a>
						<a href="javascript:void(0);" onclick="denyException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','','strEmpOUT<%=i%>_<%=j%>_<%=k%>','myDivO<%=i%>_<%=j%>_<%=k%>', 'OUT');"><i class="fa fa-times-circle cross" aria-hidden="true" style="width: 16px;"></i> </a>
					<% } %>
					</span>

				</div>
				</td>
				</tr>
				<%
					nCount++;
					}
				%>

				<% if (((String) hmAS.get((String) alEmp.get(i))) != null && ((String) hmAE.get((String) alEmp.get(i))) != null && ((String) hmAS.get((String) alEmp.get(i))).length() == 0 && ((String) hmAE.get((String) alEmp.get(i))).length() == 0) {
					/* if((uF.parseToInt((String)alEmp.get(i))==608) && alDt.get(k).equals("2021-11-03")){
						System.out.println("VCE1.jsp/812--"+(String)alDt.get(k));
					} */
				%>
				<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j) +"_IN_OUT"+ "_STATUS"))) == 0) {
					/* if(((String)alEmp.get(i)).equals("112")){
						System.out.println((String)alDt.get(k)+"=====4");
					} */
					//System.out.println((String)alDt.get(k)+"=====4");
					if (hmHolidayDates.containsKey(uF.getDateFormat(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, IConstants.DATE_FORMAT), IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) + "_" + strWLocationId)) {
						/* if(((String)alEmp.get(i)).equals("112")){
							System.out.println((String)alDt.get(k)+"=====9");
						} */
						//System.out.println((String)alDt.get(k)+"=====9");
						continue;
					} else if (alEmpCheckRosterWeektype.contains((String) alEmp.get(i))) {
					/* if(((String)alEmp.get(i)).equals("112")){
						System.out.println((String)alDt.get(k)+"=====5");
					} */
					//System.out.println((String)alDt.get(k)+"=====5");
						if (rosterWeeklyOffSet.contains(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, IConstants.DATE_FORMAT))) {
						/* if(((String)alEmp.get(i)).equals("112")){
							System.out.println((String)alDt.get(k)+"=====6");
						} */
						//System.out.println((String)alDt.get(k)+"=====6");
							continue;
						}
					} else if (weeklyOffSet.contains(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, IConstants.DATE_FORMAT))) {
						/* if(((String)alEmp.get(i)).equals("112")){
							System.out.println((String)alDt.get(k)+"=====7");
						} */
						//System.out.println((String)alDt.get(k)+"=====7");
						continue;
					} else if (halfDayWeeklyOffSet.contains(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, IConstants.DATE_FORMAT))) {
						/* if(((String)alEmp.get(i)).equals("112")){
							System.out.println((String)alDt.get(k)+"=====8");
						} */
						//System.out.println((String)alDt.get(k)+"=====8");
						continue;
					}
				%>
				<tr>
					<!-- <td>
						<input name="strReimId" value="3974" onclick="checkAll();" checked="" type="checkbox">
					</td> -->
				<td>
				<div class="exceptions">
					
					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT")) != null) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval"></i>
					<% } else { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for your reasons"></i>
					<% } %>

					<%=(String) hmEmpName.get((String) alEmp.get(i))%>
					had neither clocked on nor clocked off on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>
					for <strong><%=(String) hmServicesMap.get((String) alService.get(j))%></strong> because .... 
					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">Reason given...</a>
					<div class="highslide-maincontent">
						<h4>Given Reason</h4>
						<!-- Started By Dattatray Date:07-10-21-->
						In Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT"+"_IN_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
					Out Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT"+"_OUT_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
						Reason : <%=(String) (hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT") != null && !hmExceptionInner.get((String) alDt.get(k)+ "_"+ (String) alService.get(j)+"_IN_OUT").equals("null") ? hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT") : "Not Specified")%>
					<!-- Ended By Dattatray Date:07-10-21-->
					</div>
					
					<span id="myDivCO<%=i%>_<%=j%>_<%=k%>"> 
					<% if (alSalPaidEmpList.contains((String) alEmp.get(i))) { %>
					<font size="1"><i>(Payroll has been processed for this date.)</i> </font>
					<% } else { 
						//System.out.println("hmEmpName.get((String) alEmp.get(i)):"+hmEmpName.get((String) alEmp.get(i))); %>
						<input type ="hidden" name="empName1" id = "empName1" value = '<%=(String) hmEmpName.get((String) alEmp.get(i))%>' >
						<!-- Started By Dattatray Date:10-11-21 -->
						<% if(alEmp .get(i).equals("296")) System.out.println("VCE1.jsp/889--out else ===>> "); %>
						<a href="javascript:void(0);" onclick="approveException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','strEmpIN<%=i%>_<%=j%>_<%=k%>','','myDivCO<%=i%>_<%=j%>_<%=k%>', 'IN_OUT');"><i class="fa fa-check-circle checknew" aria-hidden="true"></i></a>
						<a href="javascript:void(0);" onclick="denyException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','strEmpIN<%=i%>_<%=j%>_<%=k%>','','myDivCO<%=i%>_<%=j%>_<%=k%>', 'IN_OUT');"><i class="fa fa-times-circle cross" aria-hidden="true"></i></a>
						<!-- Ended By Dattatray Date:10-11-21 -->
						<%-- <a href="javascript:void(0);" onclick="approveAndDenyException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','1','1','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','myDivCO<%=i%>_<%=j%>_<%=k%>','<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>');"><i class="fa fa-check-circle checknew" aria-hidden="true"></i></a>
						
						<a href="javascript:void(0);" onclick="approveAndDenyException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','','-1','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','myDivCO<%=i%>_<%=j%>_<%=k%>','<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>');"> <i class="fa fa-times-circle cross" aria-hidden="true" style="width: 16px;"></i> </a> --%>
						
				<%	}%>
				
				</span>
				</div>
				</td>
				</tr>

				<%
					nCount++;
					}

					} else if (((String) hmAS.get((String) alEmp.get(i))) != null && ((String) hmAS.get((String) alEmp.get(i))).length() > 0) {
						
				%>
				<tr>
					<!-- <td>
						<input name="strReimId" value="3974" onclick="checkAll();" checked="" type="checkbox">
					</td> -->
					<td>
				<div class="exceptions">

					<% if ((String) hmReason_IN.get((String) alEmp.get(i)) != null) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"  title="Waiting for approval" ></i>
					<% } %>
					<%=(String) hmEmpName.get((String) alEmp.get(i))%>
					clocked on for <strong><%=(String) hmServicesMap.get((String) alService.get(j))%></strong> at <input style="width: 65px !important;" type="text" name="strEmpIN" id="strEmpIN<%=i%>_<%=j%>_<%=k%>" value="<%=(String) hmAS.get((String) alEmp.get(i))%>" readonly="readonly" />hrs on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>
					and
					<%=(("F".equalsIgnoreCase((String) hmGenderMap.get((String) alEmp.get(i))) ? "her" : "his"))%>
					start time was
					<%=(String) hmRS.get((String) alEmp.get(i))%>hrs

					<% if ((String) hmReason_IN.get((String) alEmp.get(i)) != null) { %>

					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">Reason given...</a>
					<div class="highslide-maincontent">
						<h4>Given Reason</h4>
						<%=(String) (hmReason_IN.get((String) alEmp.get(i)) != null && !hmReason_IN.get((String) alEmp.get(i)).equals("null") ? hmReason_IN.get((String) alEmp.get(i)) : "Not Specified")%>
					</div>
				
						
					<span id="myDivI<%=i%>_<%=j%>_<%=k%>">
					<% if (alSalPaidEmpList.contains((String) alEmp.get(i))) { %>
					<font size="1"><i>(Payroll has been processed for this date.)</i> </font>
					<% } else { %>
					
						<a href="javascript:void(0);" onclick="approveException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','strEmpIN<%=i%>_<%=j%>_<%=k%>','','myDivI<%=i%>_<%=j%>_<%=k%>', 'IN');"><i class="fa fa-check-circle checknew" aria-hidden="true"></i></a>
						<a href="javascript:void(0);" onclick="denyException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','strEmpIN<%=i%>_<%=j%>_<%=k%>','','myDivI<%=i%>_<%=j%>_<%=k%>', 'IN');"><i class="fa fa-times-circle cross" aria-hidden="true"></i></a>
					<% } %>
					</span>

					<%
						nCount++;
						}
					%>
				</div>
				</td>
				</tr>

				<% } else {
					/* if(((String)alEmp.get(i)).equals("112")){
						System.out.println((String)alDt.get(k)+"=====11");
						System.out.println("((String)hmExceptionInner.get((String)alDt.get(k)(String)alService.get(j)))----->"+((String)hmExceptionInner.get((String)alDt.get(k)+"_"+(String)alService.get(j))));
						System.out.println("(String)hmRS.get((String)alEmp.get(i))----->"+(String)hmRS.get((String)alEmp.get(i)));
					} */
					//System.out.println("VCE1.jsp/961--"+(String)alDt.get(k)+"=====11");
					
				%>
		<!-- ===start parvez date: 02-12-2021=== -->
				<% /* if (((String) hmAS.get((String) alEmp.get(i)) == null || ((String) hmAS.get((String) alEmp.get(i))).length() == 0) && ((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN")) != null && (String) hmRS.get((String) alEmp.get(i)) != null) { */
				if (((String) hmAS.get((String) alEmp.get(i)) == null || ((String) hmAS.get((String) alEmp.get(i))).length() == 0) && ((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN")) != null && (String) hmRS.get((String) alEmp.get(i)) != null
						&& uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_STATUS"))) != -1) {
		/* ===end parvez date: 02-12-2021=== */
					
				%>

				<tr>
					<!-- <td>
						<input name="strReimId" value="3974" onclick="checkAll();" checked="" type="checkbox">
					</td> -->
					<td>
				<div class="exceptions">

					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN")) != null) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval" ></i>
					<% } else { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for your reasons"></i>
					<% } %>

					<%=(String) hmEmpName.get((String) alEmp.get(i))%>
					did not clock on for <strong><%=(String) hmServicesMap.get((String) alService.get(j))%></strong> on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>, however,
					<%=(("F".equalsIgnoreCase((String) hmGenderMap.get((String) alEmp.get(i))) ? "her" : "his"))%>
					start time was
					<%=(String) hmRS.get((String) alEmp.get(i))%>hrs
					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">Reason given...</a>
					<div class="highslide-maincontent">
						<h4>Given Reason</h4>
						<!-- Started By Dattatray Date:07-10-21-->
						
						In Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+"_IN"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
						Reason : <%=(String) (hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN") != null && !hmExceptionInner.get( (String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN").equals("null") ? hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN") : "Not Specified")%>
					<!-- Ended By Dattatray Date:07-10-21-->
					</div>
					
					
					<span id="myDivI<%=i%>_<%=j%>_<%=k%>">
					<% if (alSalPaidEmpList.contains((String) alEmp .get(i))) { %>
					<font size="1"><i>(Payroll has been processed for this date.)</i> </font>
					<% } else { %>
					
					<% if(alEmp .get(i).equals("296")) System.out.println("VCE1.jsp/1010--out else ===>> "); %>
						<a href="javascript:void(0);" onclick="approveException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','strEmpIN<%=i%>_<%=j%>_<%=k%>','','myDivI<%=i%>_<%=j%>_<%=k%>', 'IN');"><i class="fa fa-check-circle checknew" aria-hidden="true"></i></a>
						<a href="javascript:void(0);" onclick="denyException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','strEmpIN<%=i%>_<%=j%>_<%=k%>','','myDivI<%=i%>_<%=j%>_<%=k%>', 'IN');"><i class="fa fa-times-circle cross" aria-hidden="true"></i></a>
					<% } %>
					</span>
				</div>
				</td>
				</tr>
				<%
					nCount++;
		//===start parvez date: 02-12-2021===
				/* } else if (((String) hmAE.get((String) alEmp.get(i)) == null || ((String) hmAE.get((String) alEmp.get(i))).length() == 0) && ((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT")) != null && (String) hmRE.get((String) alEmp.get(i)) != null) { */
					} else if (((String) hmAE.get((String) alEmp.get(i)) == null || ((String) hmAE.get((String) alEmp.get(i))).length() == 0) && ((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT")) != null && (String) hmRE.get((String) alEmp.get(i)) != null
							&& uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT_STATUS"))) != -1) {
		//===end parvez date: 02-12-2021===
						/* if(((String)alEmp.get(i)).equals("112")){
							System.out.println((String)alDt.get(k)+"=====13");
						} */
						//System.out.println((String)alDt.get(k)+"=====13");
						
				%>
					<tr>
						<!-- <td>
							<input name="strReimId" value="3974" onclick="checkAll();" checked="" type="checkbox">
						</td> -->
						<td>
				<div class="exceptions">

					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT")) != null) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"></i>
					<% } else { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for your reasons"></i>
					
					<% } %>

					<%=(String) hmEmpName.get((String) alEmp.get(i))%>
					did not clock off for <strong>
					<%=(String) hmServicesMap.get((String) alService.get(j))%>
					</strong>
					on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>, however, my end time was
			<!-- ===start parvez date: 10-08-2022=== -->		
					<%-- <%=(String) hmRE.get((String) alEmp.get(i))%>hrs <input type="hidden" name="strEmpNOUT" id="strEmpNOUT<%=i%>_<%=j%>_<%=k%>" value="<%=(String) hmRE.get((String) alEmp.get(i))%>" /> --%>
					<%=(String) hmRE.get((String) alEmp.get(i))%>hrs <input type="hidden" name="strEmpNOUT" id="strEmpNOUT<%=i%>_<%=j%>_<%=k%>" value="<%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT"+"_OUT_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT)%>" />
			<!-- ===end parvez date: 10-08-2022=== -->		
					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">Reason given...</a>
					<div class="highslide-maincontent">
						<h4>Given Reason</h4>
						<!-- Started By Dattatray Date:07-10-21-->
						<%-- <% if(alEmp .get(i).equals("296")) System.out.println("VCE1.jsp/1066--out time ===>> "+uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT"+"_OUT_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT)); %> --%>
						Out Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT"+"_OUT_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
						Reason : <%=(String) (hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT") != null && !hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT").equals("null") ? hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT") : "Not Specified")%>
					<!-- Ended By Dattatray Date:07-10-21-->
					</div>

					<span id="myDivIC<%=i%>_<%=j%>_<%=k%>"> 
					<% if (alSalPaidEmpList.contains((String) alEmp.get(i))) { %>
						<font size="1"><i>(Payroll has been processed for this date.)</i> </font> 
					<% } else { %>
					<%-- <% if(alEmp .get(i).equals("296")) System.out.println("VCE1.jsp/1066--out else ===>> "); %> --%>
						<a href="javascript:void(0);" onclick="approveException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','','strEmpNOUT<%=i%>_<%=j%>_<%=k%>','myDivIC<%=i%>_<%=j%>_<%=k%>', 'OUT');"><i class="fa fa-check-circle checknew" aria-hidden="true" ></i></a>
						<a href="javascript:void(0);" onclick="denyException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','','strEmpNOUT<%=i%>_<%=j%>_<%=k%>','myDivIC<%=i%>_<%=j%>_<%=k%>', 'OUT');"><i class="fa fa-times-circle cross" aria-hidden="true" style="width: 16px;"></i> </a> 
					<% } %>
					</span>
				</div>
				</td>
				</tr>
				<%
					nCount++;
					}
				}
				%>




				<% Map<String, List<List<String>>> hmEmpHDFDException = (Map<String, List<List<String>>>) request.getAttribute("hmEmpHDFDException");
					if(hmEmpHDFDException == null) hmEmpHDFDException = new HashMap<String, List<List<String>>>();
					//System.out.println("VCE1.jsp/1072--hmEmpHDFDException ===>> " + hmEmpHDFDException);
					//System.out.println("alEmp.get(i) ===>> " + alEmp.get(i));
					//System.out.println("(String) alDt.get(k) ===>> " + (String) alDt.get(k)); 
					
					List<List<String>> alData = hmEmpHDFDException.get(alEmp.get(i)+"_"+(String) alDt.get(k));
					//System.out.println("VCE1.jsp/1077--alData16 ===>> " );
					
					for(int l=0; alData!=null && l<alData.size(); l++) {
						List<String> innerList = alData.get(l);		
						//System.out.println("VCE1.jsp/1081--innerList="+innerList);
				%>
				<tr>
					<!-- <td>
						<input name="strReimId" value="3974" onclick="checkAll();" checked="" type="checkbox">
					</td> -->
					<td>
					
				<div class="exceptions">
					<%=(String) hmEmpName.get((String) alEmp.get(i))%>
					has worked for only <%=innerList.get(3) %> hrs, on date <%=innerList.get(1) %>, auto generated.
					
					<span id="myDivHDFD<%=i %>_<%=j %>_<%=k %>"> 
					
					<% if (alSalPaidEmpList.contains((String) alEmp.get(i))) { %>
						<font size="1"><i>(Payroll has been processed for this date.)</i> </font> 
					<% } else { 
					
					%>
						
						<a href="javascript:void(0);" onclick="approveDenyHDFDException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','1','myDivHDFD<%=i%>_<%=j%>_<%=k%>', '<%=innerList.get(2) %>');"><i class="fa fa-check-circle checknew" aria-hidden="true" ></i></a>
						<a href="javascript:void(0);" onclick="approveDenyHDFDException('<%=(String) hmEmpName.get((String) alEmp.get(i))%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>','<%=(String) alDt.get(k)%>','-1','myDivHDFD<%=i%>_<%=j%>_<%=k%>', '<%=innerList.get(2) %>');"><i class="fa fa-times-circle cross" aria-hidden="true" style="width: 16px;"></i> </a> 
					<% } %>
					</span>
					<span><a href="javascript:void(0);" style="margin-left: 10px;" onclick="getApprovalStatus('<%=innerList.get(6) %>','<%=(String) hmEmpName.get((String) alEmp.get(i))%>');">View</a> </span>
				</div>
				</td>
				</tr>
				<% nCount++; 
				} %>

				<%
					}%>
					
						<% }%>
						
					</tr>
						 <% }	%>


				<% if (nCount == 0) { %>
					<div class="msg nodata"><span>No exception found for the selected employee</span></div>
				<% } %>
			</table>	
				<div class="custom-legends">
				  <div> <strong>Status Legends</strong> </div>
				  <div class="custom-legend pending">
				    <div class="legend-info">Waiting for approval</div>
				  </div>
				  <div><strong>Action Legends</strong></div>
				  <div class="custom-legend  no-borderleft-for-legend">
				    <div class="legend-info"><div><i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve</div></div>
				  </div>
				  <div class="custom-legend no-borderleft-for-legend">
				    <div class="legend-info"><i class="fa fa-times-circle cross" aria-hidden="true"></i>Deny</div>
				  </div>
				</div>
		</div>
		<!-- /.box-body -->


	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content" style="width:400px;">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:200px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
	