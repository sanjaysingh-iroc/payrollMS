<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<% String callFrom = (String) request.getAttribute("callFrom"); %>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayPayPage('AttendanceReport');" data-toggle="tab">Real Timings</a></li>
                    <li><a href="javascript:void(0)" onclick="getPayPayPage('AttendanceRegister');" data-toggle="tab">Attendance Register</a></li>
                    <li><a href="javascript:void(0)" onclick="getPayPayPage('AttendanceRegisterInOut');" data-toggle="tab">Attendance Register Details</a></li>
                    <li><a href="javascript:void(0)" onclick="getPayPayPage('LeaveRegister');" data-toggle="tab">Leave Calendar</a></li>
                    <li><a href="javascript:void(0)" onclick="getPayPayPage('RosterReport');" data-toggle="tab">Shifts</a></li>
                    <li <% if(callFrom != null && callFrom.equals("MyDashTimeExceptions")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayPayPage('UpdateClockEntries');" data-toggle="tab">Time Exceptions</a></li>
                    <li><a href="javascript:void(0)" onclick="getPayPayPage('ManageRoster');" data-toggle="tab">Manage Schedules</a></li>
                    <li <% if(callFrom != null && callFrom.equals("HRDashApproveOT")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayPayPage('OvertimeApproval');" data-toggle="tab">Approve Overtime</a></li>
                </ul>
                <div class="tab-content">
                 <!-- ===start parvez date: 24-02-2023=== -->   
                    <div class="active tab-pane" id="divResult" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;"></div>
                 <!-- ===end parvez date: 24-02-2023=== -->
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	<% if(callFrom != null && callFrom.equals("MyDashTimeExceptions")) { %>
		getPayPayPage('UpdateClockEntries');
	<% } else if(callFrom != null && callFrom.equals("HRDashApproveOT")) { %>
		getPayPayPage('OvertimeApproval');
	<% } else { %>
		getPayPayPage('AttendanceReport');
	<% } %>
});

function getPayPayPage(strAction){
	//alert("strAction ===>> " + strAction);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action',
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result ===>> " + result);
			$("#divResult").html(result);
   		}
	});
}

/* ===start parvez date: 24-02-2023=== */
$(window).bind('mousewheel DOMMouseScroll', function(event){
    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
        // scroll up
        if($(window).scrollTop() == 0 && $("#divResult").scrollTop() != 0) {
        	$("#divResult").scrollTop($("#divResult").scrollTop() - 30);
        }
    } else {
        // scroll down
        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#divResult").scrollTop($("#divResult").scrollTop() + 30);
   		}
    }
});

$(window).keydown(function(event){
	if(event.which == 40 || event.which == 34){
		if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
			$("#divResult").scrollTop($("#divResult").scrollTop() + 50);
   		}
	} else if(event.which == 38 || event.which == 33){
		if($(window).scrollTop() == 0 && $("#divResult").scrollTop() != 0) {
	    	$("#divResult").scrollTop($("#divResult").scrollTop() - 50);
	    }
	}
});
/* ===end parvez date: 24-02-2023=== */

</script>
