<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<section class="content"> <!-- style="padding-top: 0px; margin-top: -5px;" -->
    <div class="row">
        <div class="col-md-12" style="padding: 0px;">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li class="active"><a href="javascript:void(0)" onclick="getTeamUtilizationReport('T');" data-toggle="tab">Team</a></li>
                    <li><a href="javascript:void(0)" onclick="getTeamUtilizationReport('P');" data-toggle="tab">Projects</a></li>
                    <li><a href="javascript:void(0)" onclick="getTeamUtilizationReport('C');" data-toggle="tab">Clients</a></li>
                </ul>
                <div class="tab-content">
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;"></div>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	getTeamUtilizationReport("T");
});

function getTeamUtilizationReport(reportType) {
	//alert("strAction ===>> " + strAction);
	var action = 'TeamUtilizationReport.action?reportType='+reportType;
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: action,
		cache: true, 
		//async: false,
		success: function(result){
			//alert("result ===>> " + result);
			$("#divResult").html(result);
   		}
	});
}

</script>
