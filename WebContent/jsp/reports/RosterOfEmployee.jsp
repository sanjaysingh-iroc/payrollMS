<%@page import="com.konnect.jpms.roster.FillShift"%>
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<style>
    .reportHeading {
	    background-color: #d8d8d8;
	    font-family: verdana,arial,helvetica,sans-serif;
	    font-size: 10px;
	    font-weight: bold;
    }
    
    img {
    	border:none;
    }
    
    .prevlink a {
    	float:right;
    }
    
    .day {
	    text-align:center;
	    width:36px;
	    float:left;
	    border:#fff solid 1px;
    }
    
    .date {
	    text-align:center;
	    width:36px;
	    float:left;
	    border:#fff solid 1px;
    }
    
    /* .inout {
	    width:50px ; float:left;
	    border:#fff solid 0px;
	    _height:21px;
	    background:#efefef;
    } */
    
    .empname {
	    border:#fff solid 1px;
	    font-size:12px;
	    background:#d8d8d8;
	    float:left;
	    overflow:hidden;
	    width:100%;
	    padding:0px 3px 0px 3px;
	    /* line-height:19px; */
	    height:auto;
    }
    
    .block { 
	    float:left;
	    position:absolute; 
	    /* background-color:#abc; */ 
	    /*left:180px;*/
	    top:0px;
	    width:1459px; 
	    height:auto;
	    margin:0px; 
	    z-index:10;
	    overflow:hidden;
    }
    
    .block2 { 
	    float:left;
	    background-color:#fff; 
	    left:0px;
	    top:0px;
	    width:100%; 
	    height:auto;
	    margin:0px; 
	    z-index:100;
	    border-top:#fff solid 1px; 
	    overflow:hidden;
    }
    
    .block_dates { 
	    float:left;
	    position:absolute; 
	    background-color:#fff; 
	    left:0px;
	    top:0px;
	    width:1460px; 
	    height:auto;
	    margin:0px; 
	    z-index:10;
	    border:solid 0px #f00;
    }
    
    .next { width:32px; float:right; border:#666666 solid 1px;display:block;}
    .posfix { left:20%; top:35% ; width:630px; }
    .posfix h2 { color:#fff;margin:5px 0px 10px 0px}
    #mask { width:100%; border:#fff solid 1px; height:600px; overflow:hidden; position:relative; float:left;}
    #pivot {width:87%;  border:solid 0px #ff0; position:absolute; height:595px;overflow:scroll;float:left;margin:0px 0px 0px 12%;left:0px; }
    .weekly_width{   width: 730px; }
    .biweekly_width{  width: 1563px; }
    .monthly_width{  width: 1612px; }
    .fortnightly_width{  width: 1560px; }
    
</style>


<script>

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
});	

    function divScroll() {
        var pvt = document.getElementById('pivot');
        var sdates = document.getElementById('scrolldates');
    	var semp = document.getElementById('scrollemp');
        semp.scrollTop = pvt.scrollTop;
    	sdates.scrollLeft = pvt.scrollLeft;
    }
    
    
    function displayBlock(id) {
    	document.getElementById(id).style.display= 'block';
    }
    
    function hideBlock(id) {
    	document.getElementById(id).style.display= 'none';
    }
    
    function submitForm(type) {
    	var calendarYear = document.getElementById("calendarYear").value;
    	var strMonth = document.getElementById("strMonth").value;
    	var org = "";
    	var f_strWLocation = "";
    	var f_department = "";
    	var f_service = "";
    	var f_level = "";
    	if(document.getElementById("f_org")) {
    		org = document.getElementById("f_org").value;
    	}
    	if(document.getElementById("f_strWLocation")) {
    		f_strWLocation = document.getElementById("f_strWLocation").value;
    	}
    	if(document.getElementById("f_department")) {
    		f_department = document.getElementById("f_department").value;
    	}
    	if(document.getElementById("f_service")) {
    		f_service = document.getElementById("f_service").value;
    	}
    	if(document.getElementById("f_level")) {
    		f_level = document.getElementById("f_level").value;
    	}
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_service='+f_service+'&f_level='+f_level+'&calendarYear='+calendarYear
    			+'&strMonth='+strMonth;
    	}
    	//alert("1 ===>> ");
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'RosterOfEmployee.action?f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#divResult").html(result);
       		}
    	});
    }
    
	function generateReportExcel() {
		document.frm_roster_actual.exportType.value='excel';
		document.frm_roster_actual.submit();
	}
    
   
	/* function changeAndAssignNewShift(val, empId, rosterId, strDate, strServiceId, strShiftId, remainingEmpShift, strLeaveDate) {
		
		if(val != 'L') {
			if(confirm('Are you sure, you want to change shift for this User?')) {
		   		var dialogEdit = '.modal-body';
		   		var calendarYear = document.getElementById("calendarYear").value;
		   		var strMonth = document.getElementById("strMonth").value;
				$(dialogEdit).empty();
				$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$('.modal-title').html('Shift Swapping Suggestions');
				$("#modalInfo").show();
				$(".modal-dialog").width(500);
				$(".modal-dialog").height(200);
				$.ajax({
					url : 'EmpShiftSwappingSuggestionBox.action?strEmpId='+empId+'&strRosterId='+rosterId+'&strShiftChangeVal='+val
						+'&strShiftChangeDate='+encodeURIComponent(strDate)+'&strServiceId='+strServiceId+'&strShiftId='+strShiftId
						+'&remainingEmpShift='+remainingEmpShift+'&calendarYear='+calendarYear+'&strMonth='+strMonth,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
			}
		} else {
			if(confirm('Are you sure, you want to apply leave for this User?')) {
				applyLeave(empId, strLeaveDate);
			}
		}
	}  */
   
	function changeAndAssignNewShift(val, empId, rosterId, strDate, strServiceId, strShiftId, remainingEmpShift, strLeaveDate) {
		if(val != '') {
			var alertMsg = 'Are you sure, you want to change shift for this User?';
			if(val == '0') {
				alertMsg = 'Are you sure, you want to assign weekoff for this User?';
			}
			if(val == 'L') {
				alertMsg = 'Are you sure, you want to apply leave for this User?';
			}
			if(confirm(alertMsg)) {
				var f_org = document.getElementById("f_org").value;
				var calendarYear = document.getElementById("calendarYear").value;
		   		var strMonth = document.getElementById("strMonth").value;
				getContent(empId+'_'+strDate, 'EmpShiftSwappingSuggestionBox.action?operation=Update&strEmpId='+empId+'&strRosterId='+rosterId
					+'&strShiftChangeVal='+val+'&strShiftChangeDate='+encodeURIComponent(strDate)+'&strServiceId='+strServiceId+'&strShiftId='+strShiftId
					+'&remainingEmpShift='+remainingEmpShift+'&calendarYear='+calendarYear+'&strMonth='+strMonth);
				
				/* $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				
				$.ajax({
					url: 'RosterOfEmployee.action?calendarYear='+calendarYear+'&strMonth='+strMonth,
					cache: true,
					success: function(result){
						$("#divResult").html(result);
			   		}
				}); */
			}
		} else {
			/* if(confirm('Are you sure, you want to apply leave for this User?')) {
				applyLeave(empId, strLeaveDate);
			} */
		}
	}
	
	function applyLeave(empId, strLeaveDate) { 
		var dialogEdit = '.modal-body'; 
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('Apply Leave');
		if($(window).width() >= 800) {
			 $(".modal-dialog").width(800);
		}
		var calendarYear = document.getElementById("calendarYear").value;
   		var strMonth = document.getElementById("strMonth").value;
		$.ajax({
			url : 'ApplyLeavePopUp.action?type=ROSTER&strEmpId='+empId+'&leaveFromTo='+encodeURIComponent(strLeaveDate)+'&leaveToDate='+encodeURIComponent(strLeaveDate)
				+'&calendarYear='+calendarYear+'&strMonth='+strMonth,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function sendRosterMailToAllUsers() {
		if(confirm('Are you sure, you want to send new roster mail to all users?')) {
			var f_org = document.getElementById("f_org").value;
			var calendarYear = document.getElementById("calendarYear").value;
	   		var strMonth = document.getElementById("strMonth").value;
			getContent("mailMsg", 'RosterOfEmployee.action?operation=SENDMAIL&f_org='+f_org+'&calendarYear='+calendarYear+'&strMonth='+strMonth);
		}
	}
	
	
   /* function changeAndAssignNewShift(val, empId, rosterId, strDate, strServiceId, strShiftId, remainingEmpShift) {
	   if(confirm('Are you sure, you want to change shift for this User?')) { 
			getContent(empId+'_'+strDate, 'RosterOfEmployee.action?operation=AJAX&strEmpId='+empId+'&strRosterId='+rosterId
				+'&strShiftChangeVal='+val+'&strShiftChangeDate='+encodeURIComponent(strDate)+'&strServiceId='+strServiceId
				+'&strShiftId='+strShiftId+'&remainingEmpShift='+remainingEmpShift);
		}
   } */

</script>


<%
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    
    List alDay = (List) request.getAttribute("alDay");
    List alDate = (List) request.getAttribute("alDate");
    List alEmpId = (List) request.getAttribute("alEmpId");
    Map hmList = (Map) request.getAttribute("hmList");
    //System.out.println("hmList ===>> " + hmList);
    
    String paycycleDuration = (String)request.getAttribute("paycycleDuration");
    
    Map hmRosterServiceId = (HashMap) request.getAttribute("hmRosterServiceId");
    Map hmServicesWorkrdFor = (Map) request.getAttribute("hmServicesWorkrdFor");
    Map hmServices = (Map) request.getAttribute("hmServices");
    
    
    Map hmHolidayDates = (Map) request.getAttribute("hmHolidayDates");
    Map hmHolidays = (Map) request.getAttribute("hmHolidays");
    Map hmWLocation = (Map) request.getAttribute("hmWLocation");
    Map<String, Set<String>> hmWeekEnds = (Map<String, Set<String>>) request.getAttribute("hmWeekEnds");
    if(hmWeekEnds==null) hmWeekEnds = new HashMap<String, Set<String>>();
    
    Map<String, String> hmEmpLevelMap = (Map<String, String>) request.getAttribute("hmEmpLevelMap");
    if(hmEmpLevelMap==null) hmEmpLevelMap = new HashMap<String, String>();
    
    Map hmLeavesMap = (Map) request.getAttribute("hmLeavesMap");
    Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
    Map hmLeavesName = (Map) request.getAttribute("hmLeavesName");
    if(hmLeavesName==null) hmLeavesName = new HashMap();
    //	out.println("<br/>hmServices="+hmServices);
    //	out.println("<br/>hmServicesWorkrdFor="+hmServicesWorkrdFor);
    
    Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");	
    List _alHolidays = (List) request.getAttribute("_alHolidays");
    
    String strReqAlphaValue = (String)request.getParameter("alphaValue");
    if(strReqAlphaValue==null){
    	strReqAlphaValue="";
    }
    
    String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
    if(strAction!=null){
    	strAction = strAction.replace(request.getContextPath()+"/","");
    }
    
    
    //out.println("<br/>hmHolidays===>"+hmHolidays);
    //out.println("<br/>hmHolidayDates===>"+hmHolidayDates);
    //out.println("<br/>hmWLocation===>"+hmWLocation);
    //out.println("<br/>hmWeekEnds===>"+hmWeekEnds);
    
    String f_org = (String) request.getAttribute("f_org");
    String f_strWLocation = (String) request.getAttribute("f_strWLocation");
    String f_department = (String) request.getAttribute("f_department");
    String f_service = (String) request.getAttribute("f_service");
    String f_level = (String) request.getAttribute("f_level");
    String strMonth = (String) request.getAttribute("strMonth");
    String strYear = (String) request.getAttribute("strYear");
    String calendarYear = (String) request.getAttribute("calendarYear");
    List<FillShift> shiftList = (List<FillShift>) request.getAttribute("shiftList");
    if(shiftList == null) shiftList = new ArrayList<FillShift>();
    
    Map<String, String> hmShiftName = (Map<String, String>) request.getAttribute("hmShiftName");
    if(hmShiftName==null) hmShiftName = new HashMap<String, String>();
    
    List<String> alTlLevels = (List<String>) request.getAttribute("alTlLevels");
    if(alTlLevels==null) alTlLevels = new ArrayList<String>();
    
    Map<String, String> hmShiftColor = (Map<String, String>) request.getAttribute("hmShiftColor");
    if(hmShiftColor==null) hmShiftColor = new HashMap<String, String>();
    
    Map<String, Set<String>> hmWeekEndList = (Map<String, Set<String>>) request.getAttribute("hmWeekEndList");
    if(hmWeekEndList == null) hmWeekEndList = new HashMap<String, Set<String>>();
    List<String> alEmpCheckRosterWeektype = (List<String>)request.getAttribute("alEmpCheckRosterWeektype");;
    if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
    Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");;
    if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
    
    Map<String, String> hmAssignShiftDetails = (Map<String, String>) request.getAttribute("hmAssignShiftDetails");
    if(hmAssignShiftDetails==null) hmAssignShiftDetails = new HashMap<String, String>();
    	
    Map<String, String> hmShiftDetails = (Map<String, String>) request.getAttribute("hmShiftDetails");
    if(hmShiftDetails == null) hmShiftDetails = new HashMap<String, String>();
    String rotFirst = (String) request.getAttribute("rotFirst");
    String rotSecond = (String) request.getAttribute("rotSecond");
    String rotThird = (String) request.getAttribute("rotThird");
    List<String> alShiftIds = new ArrayList<String>();
    alShiftIds.add(rotFirst);
    alShiftIds.add(rotSecond);
    alShiftIds.add(rotThird);
    Collections.sort(alShiftIds);
    
    String remainingEmpShift = (String) request.getAttribute("remainingEmpShift");
    
    String monthWeekEndCnt = (String) request.getAttribute("monthWeekEndCnt");
    
    System.out.println("alEmpCheckRosterWeektype -->> " + alEmpCheckRosterWeektype +" -- hmRosterWeekEndDates -->> " + hmRosterWeekEndDates);
	
    %>
    
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Staff Roster" name="title"/>
    </jsp:include> --%>
    
	<%StringBuilder sb = new StringBuilder(); %>
        <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
        <div class="box box-default collapsed-box" style="margin-bottom: 7px;"> <!--  collapsed-box -->
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form theme="simple" action="RosterOfEmployee" method="post" name="frm_roster_actual">
					<s:hidden name="currUserType" id="currUserType" />
					<s:hidden name="exportType"></s:hidden>
					<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" onchange="submitForm('2');" list="wLocationList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments" onchange="submitForm('2');"></s:select>
							</div>

							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Service</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" headerKey="0" headerValue="All Services"  onchange="submitForm('2');"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" onchange="submitForm('2')" list="levelList"/>
							</div>
						</div>
					</div><br>
					<% } %>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Calendar</p>
								<s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" headerKey="0"  list="calendarYearList" key=""/>  <!-- onchange="submitForm('2');" -->
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Month</p>
								<s:select theme="simple" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="0" list="monthList"/>  <!-- onchange="submitForm('2');" -->
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input class="btn btn-primary" style="display: inline;" id="btnSubmit" type="button" value="Submit" onclick="submitForm('2');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
							</div>
							<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="float: right;">
								<p style="padding-left: 5px;">&nbsp;</p>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
        
	    <div class="col-lg-12 col-md-12 col-sm-12">
	    <%-- <% if(hmAssignShiftDetails!=null && hmAssignShiftDetails.size()>0) { %>
	    	<div style="float: left;"><span>Algorithm run on: <%=uF.showData(hmAssignShiftDetails.get("ALGO_RUN_DATE_TIME"), "") %> </span>&nbsp;&nbsp;
	    	<span>Last updated: <%=uF.showData(hmAssignShiftDetails.get("LAST_UPDATED_DATE"), "") %> </span>&nbsp;&nbsp;
	    	<% if(uF.parseToBoolean(hmAssignShiftDetails.get("APPROVED_STATUS"))) { %>
	    	<span>Approved on: <%=uF.showData(hmAssignShiftDetails.get("APPROVED_DATE_TIME"), "") %> </span>&nbsp;&nbsp;
	    	<% } %>
	    	 </div>
	    <% } %> --%>
	        <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
		        <div style="float: right; text-align: right;">
					<input class="btn btn-primary" style="display: inline;" id="submitButton" type="button" value="Roster Approve & Send Mail" onclick="sendRosterMailToAllUsers();"/>&nbsp;&nbsp;
					<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true" style="font-size: 20px; margin-top: 3px;"></i></a>
				</div>
			<% } %>
		</div>
		
        <div id="mailMsg"></div>
        <div class="col-lg-12 col-md-12 col-sm-12" style="padding: 15px 0px 0px;">
        <%
            List<String> alLegends = new ArrayList<String>();
            if(hmList.size()!=0) {
        %>
        <div class="clr"></div>
        <div class="roster_holder" style="border:solid 0px #ccc; margin:0px auto; width:100%">
        <div style="width:100%;  float:left; border:#ff0 solid 0px; height:45px !important;">
	        <div class="prev" style="width:12%; border:solid #fff 0px; height:70px;float:left; padding:0px">
	            <div class="prevlink" style="float:right"> </div>
	        </div>
        <div class="mask_dates" id="scrolldates" style="width:87%;border:#00f solid 0px;position:relative;float:left;overflow:hidden;height:55px !important;top:-11px;">
        <% if(paycycleDuration.equalsIgnoreCase("W")) { %>
        <div class="block_dates weekly_width" style="height:45px;">
        <% } else if(paycycleDuration.equalsIgnoreCase("BW")) { %>
        <div class="block_dates biweekly_width" style="height:45px;">
            <% } else if(paycycleDuration.equalsIgnoreCase("F")) { %>
            <div class="block_dates fortnightly_width" style="height:45px;">
                <% } else if(paycycleDuration.equalsIgnoreCase("M")) { %>
                <div class="block_dates monthly_width" style="height:45px;">
                    <% } %>
                    <!-- <div class="block_dates" style="width:1462px; height:45px;"> -->
                    <div>
                        <div class="row_day">
                            <% for (int i = 0; alDay!=null && i < alDay.size(); i++) { %>
                            <div class="day reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) alDay.get(i) == null) ? "" : ((String) alDay.get(i)).substring(0, 3).toUpperCase() )%> </div>
                            <% } %>
                            <div class="reportHeading" style="float:left; width: 60px;">&nbsp;</div>
                            <div class="reportHeading" style="float:left; width: 60px;">&nbsp;</div>
                            <% for(int i=0; alShiftIds!=null && i<alShiftIds.size(); i++) { %>
                            	<div class="reportHeading" style="float:left; width: 60px;">&nbsp;</div>
                            <% } %>
                            <div class="reportHeading" style="float:left; width: 60px;">&nbsp;</div>
                            <div class="reportHeading" style="float:left; width: 60px;">&nbsp;</div>
                        </div>
                        <div style="clear:both"></div>
                        <div class="row_date">
                            <% for (int i = 0; alDate!=null && i < alDate.size(); i++) { %>
                            	<div class="date reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) alDate.get(i) == null) ? "" : ((String) alDate.get(i)).substring(0, 6) )%>
                            	</div>
                            <% } %>
                            <div class="reportHeading" style="float:left; width: 60px;">Leaves</div>
                            <div class="reportHeading" style="float:left; width: 60px;">Total WD</div>
                            <% for(int i=0; alShiftIds!=null && i<alShiftIds.size(); i++) { %>
                            	<div class="reportHeading" style="float:left; width: 60px;">Total <%=hmShiftDetails.get(alShiftIds.get(i)) %></div>
                            <% } %>
                            <%-- <div class="reportHeading" style="float:left; width: 60px;">Total <%=hmShiftDetails.get(rotFirst) %></div>
                            <div class="reportHeading" style="float:left; width: 60px;">Total <%=hmShiftDetails.get(rotSecond) %></div>
                            <div class="reportHeading" style="float:left; width: 60px;">Total <%=hmShiftDetails.get(rotThird) %></div> --%>
                            <div class="reportHeading" style="float:left; width: 60px;">Total <%=hmShiftDetails.get(remainingEmpShift) %></div>
                            <div class="reportHeading" style="float:left; width: 60px;">WFH</div>
                        </div>
                        <%-- <div style="clear:both"></div>
                        <div class="row_inout">
                            <% for (int i = 0; alDate != null && i < alDate.size(); i++) { %>
                            <div style="width: 104px; float: left; background: #ccc; border: 1px solid rgb(255, 255, 255);">
                                <div class="inout reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>>IN</div>
                                <div class="inout reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>>OUT</div>
                            </div>
                            <% } %>
                        </div> --%>
                    </div>
                </div>
            </div>
            </div>
            <!-- <a  href="#" id="reset" onclick="resetall()">reset</a>-->
        </div>
        
        <div style="border:#fff solid 0px; width:100%; float:left ; height:auto">
        <div id="mask" >
            <div id="scrollemp" style="border:0px solid #f0f; width:12%; height: 580px; overflow: hidden; float: left;"  >
                <div class="block2" >
                    <!-- Employee names -->      
                    <%
                        for (int i = 0; alEmpId!=null && i < alEmpId.size(); i++) {
                        	// String strCol = ((i % 2 == 0) ? "1" : "");
                        	String strCol = ((i % 2 == 0) ? "#f9f9f9" : "#efefef");
                        	List alServices = (List) hmList.get((String) alEmpId.get(i));
                        	Map hm2 = (Map) hmServicesWorkrdFor.get((String) alEmpId.get(i));
                    %>
                  	<div class="empname" >
                        <% 
                            for(int ii=0; alServices!=null && ii<alServices.size(); ii++) {
                            	String strServiceId = (String)alServices.get(ii);
                            	if(uF.parseToInt(strServiceId) == 0) {
                    				continue;
                    			}
                            	Map hm = (Map) hmList.get((String) alEmpId.get(i)+"_"+strServiceId);
                            	if(hm==null){
                            		hm = new HashMap();
                            	}
                            	if(ii==0) {
                            		String strTL = "";
                            		if(hm.get("EMP_LEVELID") !=null && alTlLevels.contains(hm.get("EMP_LEVELID"))) {
                            			strTL = "L";
                            		}
                        %>
                      	<div class="alignLeft rosterEmpName" title="<%=(String) hm.get("EMPNAME") %>"><%=(String) hm.get("EMPNAME") %></div>
                       	<div class="rosterEmpNameL"><%=strTL %></div>
                        
                        <% } else { %>
                        	<div class="alignLeft rosterEmpName">&nbsp;</div>
                        	<div class="rosterEmpNameL">&nbsp;</div>
                        <% } %>
                            
                            <div class="clr" style="clear:both"></div>
                            
                        <% } %>
                    </div>
                    <% } %>
                    <% for(int i=0; alShiftIds!=null && i<alShiftIds.size(); i++) { %>
                    	<div class="empname" >
	                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(alShiftIds.get(i))+"(L)" %></div>
							<div class="clr" style="clear:both"></div>
	                   	</div>
                    <% } %>
                    <%-- <div class="empname" >
                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(rotFirst)+"(L)" %></div>
						<div class="clr" style="clear:both"></div>
                   	</div>
                   	<div class="empname" >
                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(rotSecond)+"(L)" %></div>
						<div class="clr" style="clear:both"></div>
                   	</div>
                   	<div class="empname" >
                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(rotThird)+"(L)" %></div>
						<div class="clr" style="clear:both"></div>
                   	</div> --%>
                   	<% for(int i=0; alShiftIds!=null && i<alShiftIds.size(); i++) { %>
                    	<div class="empname" >
	                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(alShiftIds.get(i)) %></div>
							<div class="clr" style="clear:both"></div>
	                   	</div>
                    <% } %>
                   	<%-- <div class="empname" >
                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(rotFirst) %></div>
						<div class="clr" style="clear:both"></div>
                   	</div>
                   	<div class="empname" >
                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(rotSecond) %></div>
						<div class="clr" style="clear:both"></div>
                   	</div>
                   	<div class="empname" >
                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(rotThird) %></div>
						<div class="clr" style="clear:both"></div>
                   	</div> --%>
                   	<div class="empname" >
                       	<div class="alignLeft rosterEmpName"><%=hmShiftDetails.get(remainingEmpShift) %></div>
						<div class="clr" style="clear:both"></div>
                   	</div>
                   	
                </div>
            </div>
            <div id="pivot" onscroll="divScroll();">
                <% if(paycycleDuration.equalsIgnoreCase("W")) { %>
                <div class="block weekly_width" id="sos">
                    <% } else if(paycycleDuration.equalsIgnoreCase("BW")) { %>
                    <div class="block biweekly_width" id="sos">
                        <% } else if(paycycleDuration.equalsIgnoreCase("F")) { %>
                        <div class="block fortnightly_width" id="sos">
                            <% } else if(paycycleDuration.equalsIgnoreCase("M")) { %>
                            <div class="block monthly_width" id="sos" style="width:1600px">
                                <% } %>
                                <!-- <div class="block" id="sos"> -->
                                <div >
                                    <!-- hrizontal colck entries row thas is to be repeated -->
                                    <%
                                    //System.out.println("shiftList ===>> " + shiftList);
                                    Map<String, String> hmShiftEmpCntDaywise = new HashMap<String, String>();
                                    Map<String, String> hmShiftTLCntDaywise = new HashMap<String, String>();
                                    Map<String, String> hmShiftDayCntEmpwise = new HashMap<String, String>();
                                       	for (int i = 0; alEmpId!=null && i < alEmpId.size(); i++) {
                                       	// String strCol = ((i % 2 == 0) ? "1" : "");
                                       	/* int rotFirstCnt=0;
			                        	int rotSecondCnt=0;
			                        	int rotThirdCnt=0;
			                        	int rotRenainEmpShiftCnt=0; */
			                        	int empShiftDaysCnt=0;
			                        	int leaveCnt=0;
			                        	int wOffCnt=0;
			                        	int holidayCnt=0;
			                        	int rotWFHCnt=0;
                                       	String strCol = ((i % 2 == 0) ? "#f9f9f9" : "#efefef");
                                       	List alServices = (List) hmList.get((String) alEmpId.get(i));
                                       	Map hm2 = (Map) hmServicesWorkrdFor.get((String) alEmpId.get(i));
                                       	String strLocationId  = (String)hmWLocation.get((String)alEmpId.get(i));
                                       	
                                       	Map hmLeaves = (Map)hmLeavesMap.get((String) alEmpId.get(i));
                                       	if(hmLeaves==null)hmLeaves=new HashMap();
                                       	
                                       	String level=hmEmpLevelMap.get((String)alEmpId.get(i));
                                       	
                                       	Set<String> weeklyOffSet= (Set<String>)hmWeekEndList.get(strLocationId);
                                       	if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
                                       	
                                       	Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get((String)alEmpId.get(i));
                                       	if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
									%>
                                    <%
                                        for(int ii=0; alServices!=null && ii<alServices.size(); ii++) {
                                        	String strServiceId = (String)alServices.get(ii);
                                        	if(uF.parseToInt(strServiceId) == 0) {
                                				continue;
                                			}
                                        	
                                        	Map hm = (Map) hmList.get((String) alEmpId.get(i)+"_"+strServiceId);
                                        //	out.println("<br>hm"+hm);
                                        	if(hm==null) {
                                        		hm = new HashMap();
                                        	}
                                        	String strTL = "";
                                    		if(hm.get("EMP_LEVELID") !=null && alTlLevels.contains(hm.get("EMP_LEVELID"))) {
                                    			strTL = "L";
                                    		}
                                        %>
                                    <% if(paycycleDuration.equalsIgnoreCase("W")) { %>
                                    <div style="height:30px; float:left; border:solid 1px #fff;" class="weekly_width" >
                                        <% } else if(paycycleDuration.equalsIgnoreCase("BW")) { %>
                                        <div style="height:30px; float:left; border:solid 1px #fff;" class="biweekly_width" >
                                            <% } else if(paycycleDuration.equalsIgnoreCase("F")) { %>
                                            <div style="height:30px; float:left; border:solid 1px #fff;" class="fortnightly_width" >
                                                <% } else if(paycycleDuration.equalsIgnoreCase("M")) { %>
                                                <div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">  <!-- style="width:1700px" -->
                                                    <% } %>
                                                    <%
                                                        for (int k = 0; alDate != null && k < alDate.size(); k++) {
                                                        
                                                        	String strShiftId = (String) hm.get((String) alDate.get(k) + "SHIFT_ID");
                                                        	String strRosterId = (String) hm.get((String) alDate.get(k) + "ROSTER_ID");
                                                        	String shiftColor = hmShiftColor.get(strShiftId);
                                                        	//System.out.println("(String) alDate.get(k) ===>> " + (String) alDate.get(k));
                                                        	//System.out.println("hm ===>> " + hm);
                                                        	//System.out.println("strShiftId ===>> " + strShiftId);
                                                        	
                                                        	String strWeekDay = uF.getDateFormat((String)alDate.get(k),  CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                                        	if(strWeekDay!=null) {
                                                        		strWeekDay = strWeekDay.toUpperCase();
                                                        	}
                                                        	
                                                        	String strWeekOff = null;
                                                        	String strShiftName = null;
                                                        	String strColour = (String)hmHolidayDates.get((String)alDate.get(k)+"_"+strLocationId);
                                                        	if(strColour!=null) {
                                                        		holidayCnt++;
                                                        		strColour = null;
                                                        	}
                                                        	
                                                       		String strDay = uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                                       		if(strDay!=null)strDay=strDay.toUpperCase();
                                                       		if(alEmpCheckRosterWeektype.contains((String)alEmpId.get(i))) {
                                                       			if(rosterWeeklyOffSet.contains(strDay)) {
                                                       				strColour = IConstants.WEEKLYOFF_COLOR;
                                                       				strWeekOff = "W/Off";
                                                       				wOffCnt++;
                                                       				strShiftName = "0";
                                                       				
                                                       			}
                                                       		}
                                                       		
                                                        	//String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                                        	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                                        	if(strLeave != null) {
                                                        		//strColour = (String)hmLeavesColour.get(strLeave);
                                                        		strColour = "#b5b5b5";
                                                        		leaveCnt++;
                                                        		strShiftName = "L";
                                                        	}
                                                        	
                                                        	String strDate = (String) alDate.get(k);
                                                        	String strCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", IConstants.DBDATE, CF.getStrReportDateFormat());
                                                        	
                                                        	if(strColour == null && shiftColor != null) {
                                                        		strColour = shiftColor;
                                                        		strShiftName = hmShiftName.get(strShiftId);
                                                        	}
                                                        	/* if(strLeave==null && strWeekOff==null && uF.parseToInt(rotFirst) == uF.parseToInt(strShiftId)) {
                                                        		rotFirstCnt++;
                                                        	} else if(strLeave==null && strWeekOff==null && uF.parseToInt(rotSecond) == uF.parseToInt(strShiftId)) {
                                                        		rotSecondCnt++;
                                                        	} else if(strLeave==null && strWeekOff==null && uF.parseToInt(rotThird) == uF.parseToInt(strShiftId)) {
                                                        		rotThirdCnt++;
                                                        	} else if(strLeave==null && strWeekOff==null && uF.parseToInt(remainingEmpShift) == uF.parseToInt(strShiftId)) {
                                                        		rotRenainEmpShiftCnt++;
                                                        	} */
                                                        	
                                                        	if(strLeave==null && strWeekOff==null && uF.parseToInt(strShiftId)>0) {
                                                        		int shiftEmpCnt = uF.parseToInt(hmShiftEmpCntDaywise.get(strShiftId+"_"+strDate));
                                                        		shiftEmpCnt++;
                                                        		hmShiftEmpCntDaywise.put(strShiftId+"_"+strDate, ""+shiftEmpCnt);
                                                        		if(strTL !=null && strTL.equals("L")) {
                                                        			int shiftTLCnt = uF.parseToInt(hmShiftTLCntDaywise.get(strShiftId+"_"+strDate));
                                                        			shiftTLCnt++;
                                                            		hmShiftTLCntDaywise.put(strShiftId+"_"+strDate, ""+shiftTLCnt);
                                                        		}
                                                        		
                                                        		int shiftDayCnt = uF.parseToInt(hmShiftDayCntEmpwise.get(strShiftId+"_"+alEmpId.get(i)));
                                                        		shiftDayCnt++;
                                                        		hmShiftDayCntEmpwise.put(strShiftId+"_"+alEmpId.get(i), ""+shiftDayCnt);
                                                        		empShiftDaysCnt++;
                                                        	}
                                                        %>
                                                    <div style="width: 36px; float: left; background: #fff; <%= (strDate!=null && strDate.equalsIgnoreCase(strCurrentDate)?"border: 1px solid rgb(255, 255, 255);border-left: 1px solid blue;":"border: 1px solid rgb(255, 255, 255)")%>;">
                                                       <div id="<%=alEmpId.get(i)+"_"+(String)alDate.get(k) %>">
	                                                       <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
		                                                       <select style="height: 25px !important; width: 35px !important; <%=((strColour!=null) ? "background-color:" + strColour + ";" : "background:"+strCol+";")%>"
		                                                       		onchange="changeAndAssignNewShift(this.value, '<%=alEmpId.get(i) %>','<%=strRosterId %>', '<%=(String)alDate.get(k) %>','<%=strServiceId %>','<%=strShiftId %>','<%=remainingEmpShift %>', '<%=uF.getDateFormat((String)alDate.get(k), IConstants.DATE_FORMAT_STR, IConstants.DATE_FORMAT) %>');" >
		                                                       		<option value="-1">-</option>
		                                                       		<option value="L" <% if(strLeave != null) { %> selected <% } %> >L</option>
		                                                       		<option value="0" <% if(strLeave == null && strWeekOff != null) { %> selected <% } %> >0</option>
		                                                       		<% for(int a=0; shiftList!=null && a<shiftList.size(); a++) { %>
		                                                       			<option value="<%=shiftList.get(a).getShiftId() %>"
		                                                       			<% if(strWeekOff == null && strLeave == null && strShiftId != null && strShiftId.equals(shiftList.get(a).getShiftId())) { %> selected <% } %>
		                                                       			><%=shiftList.get(a).getShiftCode() %></option>
		                                                       		<% } %>
		                                                       </select>
	                                                       <% } else { %>
	                                                       		<div style="text-align: right; padding: 5px; height: 25px !important; width: 35px !important; <%=((strColour!=null) ? "background-color:" + strColour + ";" : "background:"+strCol+";")%>" ><%=(strShiftName==null) ? uF.showData(strShiftName, "") : strShiftName %></div>
	                                                       <% } %>
                                                       </div>
                                                   </div>
                                                   <% } %>
                                                   <% int totWorkDays = (leaveCnt+empShiftDaysCnt); 
                                                   		int totActualWorkDays = alDate.size() - (uF.parseToInt(monthWeekEndCnt)+holidayCnt);
                                                   		if(totActualWorkDays < totWorkDays && leaveCnt>0) {
                                                   			int diffWorkDays =  totWorkDays - totActualWorkDays;
                                                   			if(leaveCnt>diffWorkDays) {
                                                   				leaveCnt = leaveCnt - diffWorkDays;
                                                   			} else {
                                                   				leaveCnt = 0;
                                                   			}
                                                   		}
                                                   		totActualWorkDays = (leaveCnt+empShiftDaysCnt); 
                                                   %>
                                                   <div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%=leaveCnt %></div>
                                                   <%-- <div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%=(leaveCnt+rotFirstCnt+rotSecondCnt+rotThirdCnt+rotRenainEmpShiftCnt) %></div> --%>
                                                   <div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%=totActualWorkDays %></div>
                                                   <% for(int a=0; alShiftIds!=null && a<alShiftIds.size(); a++) { %>
								                    	<div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%=uF.showData(hmShiftDayCntEmpwise.get(alShiftIds.get(a)+"_"+alEmpId.get(i)), "0") %></div>
								                    <% } %>
                                                   <%-- <div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%=rotFirstCnt %></div>
                                                   <div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%=rotSecondCnt %></div>
                                                   <div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%=rotThirdCnt %></div> --%>
                                                   <div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%=uF.showData(hmShiftDayCntEmpwise.get(remainingEmpShift+"_"+alEmpId.get(i)), "0") %></div>
                                                   <div style="width: 60px; float: left; text-align: right; padding-right: 5px;"><%="0" %></div> 
                                                </div>
                                                <div class="clr" style="clear:both"></div>
                                                <% } %>
											<% } %>
                                        
                                        	<% for(int a=0; alShiftIds!=null && a<alShiftIds.size(); a++) { %>
						                    	<div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">
		                                       	<% 
		                                       	int shiftTLCnt=0;
		                                       	for (int k = 0; alDate != null && k < alDate.size(); k++) { 
		                                       		shiftTLCnt += uF.parseToInt(hmShiftTLCntDaywise.get(alShiftIds.get(a)+"_"+(String)alDate.get(k)));
		                                       	%>
		                                       		<div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
		                                                   <div id="<%=alShiftIds.get(a)+"TL_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftTLCntDaywise.get(alShiftIds.get(a)+"_"+(String)alDate.get(k)), "0") %></div>
		                                               </div>
												<% } %>
													<div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
		                                                   <div id="<%=rotFirst+"TL_TOTAL" %>"><%=shiftTLCnt %></div>
		                                               </div>
												</div>
												<div class="clr" style="clear: both"></div>
												
						                    <% } %>
                                        	
                                        	<%-- <div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">
                                        	<% 
                                        	int firstShiftTLCnt=0;
                                        	for (int k = 0; alDate != null && k < alDate.size(); k++) { 
                                        		firstShiftTLCnt += uF.parseToInt(hmShiftTLCntDaywise.get(rotFirst+"_"+(String)alDate.get(k)));
                                        	%>
                                        		<div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotFirst+"TL_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftTLCntDaywise.get(rotFirst+"_"+(String)alDate.get(k)), "0") %></div>
                                                </div>
											<% } %>
												<div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotFirst+"TL_TOTAL" %>"><%=firstShiftTLCnt %></div>
                                                </div>
											</div>
											<div class="clr" style="clear: both"></div>
											<div style="height: 30px; float: left; border: solid 1px #fff;" class="monthly_width">
                                                <% 
                                                int secShiftTLCnt = 0;
                                                for (int k = 0; alDate != null && k < alDate.size(); k++) { 
                                                	secShiftTLCnt += uF.parseToInt(hmShiftTLCntDaywise.get(rotSecond+"_"+(String)alDate.get(k)));
                                                %>
                                                <div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotSecond+"TL_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftTLCntDaywise.get(rotSecond+"_"+(String)alDate.get(k)), "0") %></div>
                                                </div>
												<% } %>
												<div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotSecond+"TL_TOTAL" %>"><%=secShiftTLCnt %></div>
                                                </div>
											</div>
											<div class="clr" style="clear:both"></div>
											<div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">
                                                <% 
                                                int thirdShiftTLCnt=0;
                                                for (int k = 0; alDate != null && k < alDate.size(); k++) { 
                                                	thirdShiftTLCnt += uF.parseToInt(hmShiftTLCntDaywise.get(rotThird+"_"+(String)alDate.get(k)));
                                                %>
                                                <div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotThird+"TL_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftTLCntDaywise.get(rotThird+"_"+(String)alDate.get(k)), "0") %></div>
                                                </div>
                                                <% } %>
                                                <div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotThird+"TL_TOTAL" %>"><%=thirdShiftTLCnt %></div>
                                                </div>
											</div>
											<div class="clr" style="clear:both"></div> --%>
											
											<% for(int a=0; alShiftIds!=null && a<alShiftIds.size(); a++) { %>
						                    	<div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">
		                                       	<% 
		                                       	int shiftEmpCnt=0;
		                                       	for (int k = 0; alDate != null && k < alDate.size(); k++) { 
		                                       		shiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(alShiftIds.get(a)+"_"+(String)alDate.get(k)));
		                                       	%>
		                                       		<div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
		                                                   <div id="<%=alShiftIds.get(a)+"_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftEmpCntDaywise.get(alShiftIds.get(a)+"_"+(String)alDate.get(k)), "0") %></div>
		                                               </div>
												<% } %>
													<div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
		                                                   <div id="<%=rotFirst+"_TOTAL" %>"><%=shiftEmpCnt %></div>
		                                               </div>
												</div>
												<div class="clr" style="clear: both"></div>
												
						                    <% } %>
						                    
											
											<%-- <div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">
                                                <% 
	                                        	int firstShiftEmpCnt=0;
	                                        	for (int k = 0; alDate != null && k < alDate.size(); k++) { 
	                                        		firstShiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(rotFirst+"_"+(String)alDate.get(k)));
	                                        	%>
                                        		<div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotFirst+"_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftEmpCntDaywise.get(rotFirst+"_"+(String)alDate.get(k)), "0") %></div>
                                                </div>
                                                <% } %>
                                                <div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotFirst+"_TOTAL" %>"><%=firstShiftEmpCnt %></div>
                                                </div>
											</div>
                                                <div class="clr" style="clear:both"></div>
											<div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">
                                                <% 
	                                        	int secShiftEmpCnt=0;
	                                        	for (int k = 0; alDate != null && k < alDate.size(); k++) { 
	                                        		secShiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(rotSecond+"_"+(String)alDate.get(k)));
	                                        	%>
                                                <div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotSecond+"_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftEmpCntDaywise.get(rotSecond+"_"+(String)alDate.get(k)), "0") %></div>
                                                </div>
                                                <% } %>
                                                <div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotSecond+"_TOTAL" %>"><%=secShiftEmpCnt %></div>
                                                </div>
											</div>
                                                <div class="clr" style="clear:both"></div>
											<div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">
                                                <% 
	                                        	int thirdShiftEmpCnt=0;
	                                        	for (int k = 0; alDate != null && k < alDate.size(); k++) { 
	                                        		thirdShiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(rotThird+"_"+(String)alDate.get(k)));
	                                        	%>
                                                <div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotThird+"_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftEmpCntDaywise.get(rotThird+"_"+(String)alDate.get(k)), "0") %></div>
                                                </div>
                                                <% } %>
                                                <div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=rotThird+"_TOTAL" %>"><%=thirdShiftEmpCnt %></div>
                                                </div>
											</div>
                                            <div class="clr" style="clear:both"></div> --%>
                                                
											<div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width">
                                                <% 
	                                        	int remainingShiftEmpCnt=0;
	                                        	for (int k = 0; alDate != null && k < alDate.size(); k++) { 
	                                        		remainingShiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(remainingEmpShift+"_"+(String)alDate.get(k)));
	                                        	%>
                                                <div style="width: 36px; float: left; text-align: right; padding-right: 5px; background: #fff;">
													<div id="<%=remainingEmpShift+"_"+(String)alDate.get(k) %>"><%=uF.showData(hmShiftEmpCntDaywise.get(remainingEmpShift+"_"+(String)alDate.get(k)), "0") %></div>
                                                </div>
                                                <% } %>
                                                <div style="width: 60px; float: left; text-align: right; padding-right: 5px; background: #fff;">
                                                    <div id="<%=remainingEmpShift+"_TOTAL" %>"><%=remainingShiftEmpCnt %></div>
                                                </div>
											</div>
                                        		<div class="clr" style="clear:both"></div>
                                                
											<% if(alEmpId!=null && alEmpId.size()==0) { %>
                                                <div class="msg nodata"><span>No employees found for the current selection.</span></div>
											<% } %>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                            </div>
                            <%
                                if (request.getAttribute("empRosterDetails") != null) {
                                	out.println(request.getAttribute("empRosterDetails"));
                                }
                                out.print(sb.toString());
                                } else {
                            %>
                            You have no roster allocated for the current Pay Cycle, change 'Pay Cycle' to view other roster's
                            <% } %>
                        </div>
                    </div>   
                        
                        <div class="paddingtop20">&nbsp;</div>
                        <%-- <div class="custom-legends">
							<%
								Set set = hmLeavesColour.keySet();
								Iterator it = set.iterator();
								while (it.hasNext()) {
									String strLeave = (String) it.next();
							%>
								<div class="custom-legend" style="border-color:<%=(String) hmLeavesColour.get(strLeave)%>">
								    <div class="legend-info"><%=(String) hmLeavesName.get(strLeave)%>[<%=strLeave%>]</div>
								</div>
							<% } %>
						</div> --%>
						
                </div>
                <!-- /.box-body -->

                
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