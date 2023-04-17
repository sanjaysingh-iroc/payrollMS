<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%
	String learningPlanId = (String)request.getParameter("learningPlanId");
	//System.out.println("reviewDetails appId==>"+appId+"==>appFreqId==>"+appFreqId);
%>

	
			<div class="nav-tabs-custom">
		<ul class="nav nav-tabs">
    				<li class="active"><a href="javascript:void(0)" onclick="getLearningSummary('LearningPlanPreview','<%=learningPlanId %>');" data-toggle="tab">Learning Summary</a></li>
    				<li><a href="javascript:void(0)" onclick="getLeraringDetails('LearningPlanDetails','<%=learningPlanId %>');" data-toggle="tab">Learning Details</a></li>
    		   </ul>
          <div class="tab-content" >
             <div class="active tab-pane" id="learningPlanDetails" style="min-height: 600px;">
		
             </div>
          </div>
    </div>
    
<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	getLearningSummary('LearningPlanPreview','<%=learningPlanId%>');
});

function getLearningSummary(strAction,learningPlanId){
	//alert("ReviewDetails App summary jsp strAction ===>> " + strAction+"==>appId==>"+appId+"==appFreqId==>"+appFreqId);
	$("#learningPlanDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?learningPlanId='+learningPlanId,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result3==>"+result);
			$("#learningPlanDetails").html(result);
   		}
	});
}

function getLeraringDetails(strAction,learningPlanId){
	//alert("AppraisalStatus strAction ===>> " + strAction+"==>appId==>"+appId+"==appFreqId==>"+appFreqId);
	$("#learningPlanDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?learningPlanId='+learningPlanId,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result4==>"+result);
			$("#learningPlanDetails").html(result);
   		}
	});
}
</script>