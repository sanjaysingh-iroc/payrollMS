<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<% String callFrom = (String) request.getAttribute("callFrom"); 
String alertID = (String) request.getAttribute("alertID");
String strUserType = (String) session.getAttribute(IConstants.USERTYPE); //added parvez date: 09-02-2023
%> 
<style>
/* Started By Dattatray Date:28-09-21 */
.disabled-pointer {
    pointer-events:none;
    opacity:0.6;     
}
</style>
<section class="content">
    <div class="row"> 
        <div class="col-md-12"> 
            <div class="nav-tabs-custom"> 
                <ul class="nav nav-tabs">  
                <!-- ===start parvez date: 09-02-2023=== -->	
                	<%if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.OTHER_HR)){ %>
                		<li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPeoplePage('PeopleDashboard','0');" data-toggle="tab" id="id0">Dashboard</a></li>
                	<%} else { %>
                	
	                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPeoplePage('PeopleDashboard','0');" data-toggle="tab" id="id0">Dashboard</a></li>
	                    <li <% if(callFrom != null && (callFrom.equals("ADDEMP") || callFrom.equals("MP") || callFrom.equals("EMPBLKACTIVITY"))) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPeoplePage('EmployeeReport','1');" data-toggle="tab" id="id1">Working</a></li>
	                    <li><a href="javascript:void(0)" onclick="getPeoplePage('ExEmployeeReport','2');" data-toggle="tab" id="id2">Ex-Employees</a></li>
	                    <li <% if(callFrom != null && callFrom.equals("USERS")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPeoplePage('UserReport','3');" data-toggle="tab" id="id3">Users</a></li>
                	<% } %>
                <!-- ===end parvez date : 09-02-2023=== -->	
                </ul>
                <div class="tab-content">
                 <!-- ===start parvez date: 24-02-2023=== -->   
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;"></div>
                    <!-- <div class="active tab-pane" id="divResult" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;"></div> -->
                 <!-- ===end parvez date: 24-02-2023=== -->   
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	<% if(callFrom != null && (callFrom.equals("ADDEMP") || callFrom.equals("MP") || callFrom.equals("EMPBLKACTIVITY"))) { %>
		getPeoplePage('EmployeeReport','1');
		/* getPeoplePage('StudentsAndStaffListView'); */
	<% } else if(callFrom != null && callFrom.equals("USERS")) { %>
		getPeoplePage('UserReport','3');
	<% } else { %>
		getPeoplePage('PeopleDashboard','0');
	<% } %>

	
	
});

function getPeoplePage(strAction,id){
	
	for (let i = 0; i < 4; i++) {
		if(i != id){
			$("#id"+i).addClass("disabled-pointer");
		}
	}
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var alertID = '<%=alertID %>';
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?alertID=' + alertID,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			
			$("#divResult").html(result);
			for (let i = 0; i < 4; i++) {

				if(i != id){
					$("#id"+i).removeClass("disabled-pointer");
				}
			}
   		}
	});
}

/* ===start parvez date: 24-02-2023=== */
/* $(window).bind('mousewheel DOMMouseScroll', function(event){
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
}); */
/* ===end parvez date: 24-02-2023=== */
</script>
