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
                    <li class="active"><a href="javascript:void(0);"><span id="labelSpan">Leave Calendar</span></a></li> <%-- <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %> --%>
                    <!-- <a href="#leaveCal" onclick="getAbsencePage('LeaveRegister');" data-toggle="tab">Leave Calendar</a> -->
                    <%-- <li <% if(callFrom != null && callFrom.equals("ILRegularisation")) { %> class="active" <% } %> ><a href="javascript:void(0)" onclick="getAbsencePage('LeaveRegularise');" data-toggle="tab">Regularise Leave Balance</a></li>
                    <li><a href="javascript:void(0)" onclick="getAbsencePage('TravelAdvanceEligibility');" data-toggle="tab">Travel Advance Eligibility</a></li> --%>
                </ul>
                <div class="tab-content" >
                	<!-- Started By Dattatray Date:18-10-21 -->
                	<a href="javascript:void(0)" onclick="getAbsencePage('LeaveRegister','0');" data-toggle="tab" id="btnId_0">&nbsp;&nbsp;Leave Calendar</a> &nbsp; | &nbsp;
                	<a href="javascript:void(0)" onclick="getAbsencePage('LeaveRegularise','1');" data-toggle="tab" id="btnId_1">Regularise Leave Balance</a> &nbsp; | &nbsp;
                	<a href="javascript:void(0)" onclick="getAbsencePage('TravelAdvanceEligibility','2');" data-toggle="tab" id="btnId_2">Travel Advance Eligibility</a> <!-- <i class="fa fa-plus-circle"></i> -->
                    <!-- Ended By Dattatray Date:18-10-21 -->  
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;">
						
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	<% if(callFrom == null || callFrom.equals("")) { %>
	getAbsencePage('LeaveRegister','');//Created By Dattatray Date:18-10-21
<% } else if(callFrom != null && callFrom.equals("ILRegularisation")) { %>
	getAbsencePage('LeaveRegularise','');//Created By Dattatray Date:18-10-21
<% } %>
});

function getAbsencePage(strAction,index) {
	//alert("service ===>> " + service);
	//Started By Dattatray Date:18-10-21
	if(index.trim() != ''){
		disabledPointerAddAndRemove(3,'btnId_',index,true);
	}
	else{
		disabledPointerAddAndRemove(3,'btnId_','',true);
	}//Ended By Dattatray Date:18-10-21
	if(strAction == 'LeaveRegister') {
		document.getElementById("labelSpan").innerHTML = "Leave Calendar";
	} else if(strAction == 'LeaveRegularise') {
		document.getElementById("labelSpan").innerHTML = "Regularise Leave Balance";
	} else if(strAction == 'TravelAdvanceEligibility') {
		document.getElementById("labelSpan").innerHTML = "Travel Advance Eligibility";
	}
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action',
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			$("#divResult").html(result);
			//Started By Dattatray Date:18-10-21
			if(index.trim() != ''){
				disabledPointerAddAndRemove(3,'btnId_',index,false);
			}else{
				disabledPointerAddAndRemove(3,'btnId_','',false);
			}//Ended By Dattatray Date:18-10-21
   		}
	});
}

</script>