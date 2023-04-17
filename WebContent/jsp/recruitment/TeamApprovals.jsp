<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>

<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
	                    <li><a class="active" href="javascript:void(0)" onclick="getTeamApprovalsPage('JobProfilesApproval','');" data-toggle="tab">Job Profiles</a></li>
                </ul>
                <div class="tab-content" >
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;">
						
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>


<script type="text/javascript" charset="utf-8">
$(function(){
	getTeamApprovalsPage('JobProfilesApproval', '');
});

function getTeamApprovalsPage(strAction, parameters){
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action'+parameters,
		data: $("#"+this.id).serialize(),
		success: function(result){
			$("#divResult").html(result);
   		}
	});
}

</script>


