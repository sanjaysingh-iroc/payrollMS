<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
               <div class="box-body" style="padding: 5px; overflow-y: auto; ">
                   <%--  <s:action name="SearchEmployeeSkills" executeResult="true">
                    	<s:param name="from">TS</s:param>
                    </s:action> --%>
	               <div class="btn-group viewChangeButtons" data-toggle="btn-toggle">
	                  <button type="button" class="btn btn-default btn-sm view active" onclick="getTeamStructureData('list', event, '')" data-toggle="tooltip" for="List View" data-original-title="List View">
	                  	<i class="fa fa-bars" aria-hidden="true"></i>
	                  </button>
	                  <button type="button" class="btn btn-default btn-sm view" onclick="getTeamStructureData('grid', event, '')" data-toggle="tooltip" for="Grid View" data-original-title="Grid View">
	                  	<i class="fa fa-th-large" aria-hidden="true"></i>
	                  </button>
	                  <button type="button" class="btn btn-default btn-sm view " onclick="getTeamStructureData('org', event, '')" data-toggle="tooltip" for="Chart View" data-original-title="Chart View">
	                  	<i class="fa fa-sitemap" aria-hidden="true"></i>
	                  </button>
	                </div>
                </div>
                <div class="tab-content">
	                <div class="active tab-pane" id="divResult" style="min-height: 600px;">
							
	                </div>
                </div>
            </div>
        </div>
    </div>
</section>


<script type="text/javascript" charset="utf-8">
 $(document).ready(function(event){
	getTeamStructureData('list', event, 'onload');
}); 

function getTeamStructureData(type, event, callType){
	if(callType == null || callType == '') {
		$(".view").removeClass("active");
		$(event.target).parent().addClass("active");
	}
	if(type === "list"){
		var action = "EmployeeReport.action?fromPage=TS&page=Live";
	}else if(type === "grid"){
		var action = "SearchEmployee.action?fromPage=TS&strFirstName=";
	}else if(type === "org"){
		var action = "OrganisationalChart.action?fromPage=TS";
	}
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'GET',
		url: action,
		success: function(result){
			$("#divResult").html(result);
			if(type === "list"){
				$(".viewChangeButtons")[1].remove();
			}
   		}
	});

}

</script>


