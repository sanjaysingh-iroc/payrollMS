<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>

	<% 	String callFrom = (String) request.getAttribute("callFrom");
		String taskId = (String) request.getAttribute("taskId");
		String proType = (String) request.getAttribute("proType");
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		//System.out.println("1 taskId --->> " + taskId);
		String tlOrPoFlag = (String)request.getAttribute("tlOrPoFlag");
		String pageType = (String)request.getAttribute("pageType");
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		
	%>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                   	<li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyWorkPage('MyTasks');" data-toggle="tab">My Tasks</a></li>
                   	<li <% if(callFrom != null && callFrom.equals("MyTimesheet")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyTimesheetPage('MyTimesheet');" data-toggle="tab">My Timesheet</a></li>
                   	<% if(uF.parseToBoolean(tlOrPoFlag)) { %>
                   		<li <% if(callFrom != null && callFrom.equals("MyProjects")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyProjectsPage('ViewMyProjects');" data-toggle="tab">My Projects</a></li>
                   	<% } %>
                   	<%-- <li <% if(callFrom != null && callFrom.equals("DailyTimesheet")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getDailyTimesheetPage('DailyTimesheet');" data-toggle="tab">Daily Timesheet</a></li> --%>
                </ul>
                
                <div class="tab-content" >
                    <div class="active tab-pane" id="mainDivResult" style="min-height: 600px;">
						
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>


<script type="text/javascript" charset="utf-8">

$(function(){
	<% if(callFrom != null && callFrom.equals("MyTimesheet")) { %>
		getMyTimesheetPage('MyTimesheet');
	<% } else if(callFrom != null && callFrom.equals("MyProjects")) { %>
		getMyProjectsPage('ViewMyProjects');
	<% } else { %>
		getMyWorkPage('MyTasks');
	<% } %>
});

function getMyWorkPage(strAction) {
	$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var taskId = '<%=taskId %>';
	var proType = '<%=proType %>';
	$.ajax({
		url: strAction+'.action?taskId='+taskId+'&proType='+proType,
		success: function(result){
			$("#mainDivResult").html(result);
   		}
	});
}

function getMyProjectsPage(strAction) {
	$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		url: strAction+'.action?pageType=MP',
		success: function(result){
			$("#mainDivResult").html(result);
   		}
	});
}

function getMyTimesheetPage(strAction) {
	$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		url: strAction+'.action?submitType=LOAD',
		success: function(result){
			$("#mainDivResult").html(result);
   		}
	});
}

function getDailyTimesheetPage(strAction) {
	$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		url: strAction+'.action?submitType=LOAD',
		success: function(result){
			$("#mainDivResult").html(result);
   		}
	});
}

</script>


