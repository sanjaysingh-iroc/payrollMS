<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>

<% 
	String callFrom = (String) request.getAttribute("callFrom");
	String profileEmpId = (String) request.getAttribute("profileEmpId");
%>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                <!-- Started By Dattatray Date:18-10-21 -->
                    <li <% if(callFrom == null || callFrom.equals("") || callFrom.equals("FactQuickLink")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getRosterPage('RosterReport', '','0');" data-toggle="tab" id="rosterId_0">Staff Roster</a></li>
                    <%-- <li <% if(callFrom == null || callFrom.equals("") || callFrom.equals("FactQuickLink")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getRosterPage('RosterOfEmployee', '');" data-toggle="tab">Staff Roster</a></li> --%>
                    <li <% if(callFrom != null && callFrom.equals("FA")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getRosterPage('ManageRoster', '','1');" data-toggle="tab" id="rosterId_1">Manage Shifts</a></li>
                    <li><a href="javascript:void(0)" onclick="getRosterPage('RosterDependency', '','2');" data-toggle="tab" id="rosterId_2">Roster Dependency Details</a></li>
                <!-- Ended By Dattatray Date:18-10-21 -->
                </ul>
                <div class="tab-content" >
                 <!-- ===start parvez date: 24-02-2023=== -->   
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;">
                    <!-- <div class="active tab-pane" id="divResult" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;"> -->
                 <!-- ===end parvez date: 24-02-2023=== -->   
						
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
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
$(function(){
	<% if(callFrom == null || callFrom.equals("") || callFrom.equals("FactQuickLink")) { %>
		  var profileEmpId = '<%=profileEmpId %>';
		  var parmenters = '?profileEmpId='+profileEmpId;
		  getRosterPage('RosterReport', parmenters,'0');//Created By Dattatray Date:18-10-21
	<% }else if(callFrom!=null && callFrom.equals("FA")){ %>
		  getRosterPage('ManageRoster', '','1');//Created By Dattatray Date:18-10-21
	 <%} else { %>
		  getRosterPage('RosterReport', '','0');//Created By Dattatray Date:18-10-21
	<% } %>
});

function getRosterPage(strAction, parmenters,index){
	//alert("service ===>> " + service);
	disabledPointerAddAndRemove(3,'rosterId_',index,true);//Created By Dattatray Date:18-10-21
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action'+parmenters,
		data: $("#"+this.id).serialize(),
		success: function(result){
			$("#divResult").html(result);
			disabledPointerAddAndRemove(3,'rosterId_',index,false);//Created By Dattatray Date:18-10-21
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

