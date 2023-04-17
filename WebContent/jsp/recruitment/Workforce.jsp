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
                    <!-- <li class="active"><a href="javascript:void(0)" onclick="getWorkforcePage('RecruitmentDashboard');" data-toggle="tab">Job Requirements</a></li> -->
                    <li class="active"><a href="javascript:void(0)" onclick="getWorkforcePage('ResourcePlanner');" data-toggle="tab">Workforce Plan</a></li>
                    <li><a href="javascript:void(0)" onclick="getWorkforcePage('JobProfilesApproval');" data-toggle="tab">Job Profiles</a></li>
                    <!-- <li><a href="javascript:void(0)" onclick="getWorkforcePage('SearchEmployeeSkills');" data-toggle="tab">Search skills</a></li> -->
                </ul>
                <div class="tab-content" >
                    <div class="active tab-pane" id="divWFResult" style="min-height: 600px;">
						
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>


<script type="text/javascript" charset="utf-8">
$(function(){
	getWorkforcePage('ResourcePlanner');
});

function getWorkforcePage(strAction){
	//alert("service ===>> " + service);
	$("#divWFResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action?fromPage=WF',
		data: $("#"+this.id).serialize(),
		success: function(result){
			$("#divWFResult").html(result);
   		}
	});
}

</script>



 