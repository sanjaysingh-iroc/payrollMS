<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script src="scripts/charts/highcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />

<%  
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	String callFrom = (String) request.getAttribute("callFrom"); 
	String alertID = (String) request.getAttribute("alertID");
	String []arrEnabledModules = CF.getArrEnabledModules(); 
//===start parvez date: 05-08-2022===	
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
	
//===end parvez date: 05-08-2022===	
%>

<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
           <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_TIME_DISABLE_TAB))){ 
        	  	List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_TIME_DISABLE_TAB);
        	  	//System.out.println("hmFeatureUserTypeId="+hmFeatureUserTypeId);
           %>  
              
                <ul class="nav nav-tabs">
                	<% if(disableTabList != null && disableTabList.contains("MY_TIME")){ %>
                    	<li <% if(callFrom == null || callFrom.equals("") || disableTabList.get(0).equals("MY_TIME")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyTimePage('ClockEntries', '');" data-toggle="tab">My Time</a></li>
                    <% } %>
                    <% if(disableTabList != null && disableTabList.contains("MY_LEAVE_AND_TRAVEL")){ %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
		                    <li <% if(callFrom != null && callFrom.equals("MyDashLeaveSummary") || callFrom != null && callFrom.equals("NotiApplyTravel") || disableTabList.get(0).equals("MY_LEAVE_AND_TRAVEL")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyTimePage('EmployeeLeaveEntryReport', 'L');" data-toggle="tab">My Leave & Travel</a></li>
	                    <% } %>
                    <% } %>
                    <% if(disableTabList != null && disableTabList.contains("MY_ROSTER")){ %>
                    	<li <% if(callFrom != null && callFrom.equals("FactQuickLink") || disableTabList.get(0).equals("MY_ROSTER")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyTimePage('RosterReport', '');" data-toggle="tab"><span id="labelRosterSpan">My Roster</span></a></li>
                   	<% } %>
                   	<% if(disableTabList != null && disableTabList.contains("MY_ISSUES")){ %>
                    	<li <% if(disableTabList.get(0).equals("MY_ISSUES")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyTimePage('UpdateClockEntries', '');" data-toggle="tab">My Issues</a></li>
                   <% } %>
                    <!-- <li><a href="javascript:void(0)" onclick="getMyTimePage('RosterOfEmployee', '');" data-toggle="tab">Roster</a></li> -->
                    
                </ul>
            <% } else { %>
            	<ul class="nav nav-tabs">
                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyTimePage('ClockEntries', '');" data-toggle="tab">My Time</a></li>
                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
	                    <li <% if(callFrom != null && callFrom.equals("MyDashLeaveSummary") || callFrom != null && callFrom.equals("NotiApplyTravel")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyTimePage('EmployeeLeaveEntryReport', 'L');" data-toggle="tab">My Leave & Travel</a></li>
                    <% } %>
                    <li <% if(callFrom != null && callFrom.equals("FactQuickLink")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyTimePage('RosterReport', '');" data-toggle="tab"><span id="labelRosterSpan">My Roster</span></a></li>
                    <li><a href="javascript:void(0)" onclick="getMyTimePage('UpdateClockEntries', '');" data-toggle="tab">My Issues</a></li>
                    <!-- <li><a href="javascript:void(0)" onclick="getMyTimePage('RosterOfEmployee', '');" data-toggle="tab">Roster</a></li> -->
                    
                </ul>
            <% } %>
                <div class="tab-content">
	                <div id="myRosterDiv" style="display: none;">
		                <a href="javascript:void(0)" onclick="getMyTimePage('RosterReport', '');" data-toggle="tab">&nbsp;&nbsp;My Roster</a> &nbsp; | &nbsp;
	                	<a href="javascript:void(0)" onclick="getMyTimePage('RosterOfEmployee', '');" data-toggle="tab"> Team Roster</a> &nbsp;
	                </div>
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;"></div>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_TIME_DISABLE_TAB))){ 
	  	List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_TIME_DISABLE_TAB);
	%>
		<% if(callFrom != null && callFrom.equals("FactQuickLink")) { %> 
			getMyTimePage('RosterReport', '');
		<% } else if(callFrom != null && callFrom.equals("MyDashLeaveSummary") ) { %>
			getMyTimePage('EmployeeLeaveEntryReport', "L");
		<%}else if(callFrom != null && callFrom.equals("NotiApplyTravel")) { %>
			getMyTimePage('EmployeeLeaveEntryReport', "T");
		<% } else { %>
			<% if(disableTabList != null && disableTabList.get(0).equals("MY_TIME")){ %>
				getMyTimePage('ClockEntries', '');
			<% } else if(disableTabList != null && disableTabList.get(0).equals("MY_LEAVE_AND_TRAVEL")){ %>
				getMyTimePage('EmployeeLeaveEntryReport', 'L');
			<% } else if(disableTabList != null && disableTabList.get(0).equals("MY_ROSTER")){ %>
				getMyTimePage('RosterReport', '');
			<% } else if(disableTabList != null && disableTabList.get(0).equals("MY_ISSUES")){ %>
				getMyTimePage('UpdateClockEntries', '');
			<% } %>
		<% } %>
   
   <% } else{ %>
		<% if(callFrom != null && callFrom.equals("FactQuickLink")) { %> 
			getMyTimePage('RosterReport', '');
		<% } else if(callFrom != null && callFrom.equals("MyDashLeaveSummary") ) { %>
			getMyTimePage('EmployeeLeaveEntryReport', "L");
		<%}else if(callFrom != null && callFrom.equals("NotiApplyTravel")) { %>
			getMyTimePage('EmployeeLeaveEntryReport', "T");
		<% } else { %>
			getMyTimePage('ClockEntries', '');
		<% } %>
	<% } %>
});

function getMyTimePage(strAction, type){
	//alert("strAction ===>> " + type);
	if(strAction == 'RosterOfEmployee') {
		document.getElementById("labelRosterSpan").innerHTML = "Team Roster";
	} else {
		if(document.getElementById("labelRosterSpan")){
			document.getElementById("labelRosterSpan").innerHTML = "My Roster";
		}
	}
	if(strAction == 'RosterReport' || strAction == 'RosterOfEmployee') {
		document.getElementById("myRosterDiv").style.display = "block";
	} else {
		document.getElementById("myRosterDiv").style.display = "none";
	}
	
	var action = strAction+'.action';
	if(type != '') {
		action = strAction+'.action?alertID='+<%=alertID%>+'&dataType='+type;
	}
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result ===>> " + result);
			$("#divResult").html(result);
   		}
	});
}


</script>
