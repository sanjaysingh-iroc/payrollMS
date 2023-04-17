<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<% String learningPlanId = (String)request.getParameter("learningPlanId");%>

	<div class="nav-tabs-custom">
		<ul class="nav nav-tabs">
				<li class="active"><a href="javascript:void(0)" onclick="getLearningPlanSummary('LearningPlanSummary','<%=learningPlanId %>');" data-toggle="tab">Summary</a></li>
				<li><a href="javascript:void(0)" onclick="getLearningPlanPreview('LearningPlanPreview','<%=learningPlanId %>');" data-toggle="tab">Preview</a></li>
				<li><a href="javascript:void(0)" onclick="getLearningPlanStatus('LearningPlanAssessmentStatus','<%=learningPlanId %>');" data-toggle="tab">Status</a></li>
		   </ul>
         
             <div id="divLPDetailsResult" style="min-height: 600px;">
		
             </div>
          
       </div>
   
<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	getLearningPlanSummary('LearningPlanSummary','<%=learningPlanId%>');
});

function getLearningPlanSummary(strAction,learningPlanId){
	//alert("getLearningPlanData summary jsp strAction ===>> " + strAction+"==>learningPlanId==>"+learningPlanId);
	$("#divLPDetailsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?learningPlanId='+learningPlanId,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result3==>"+result);
			$("#divLPDetailsResult").html(result);
   		}
	});
}

function getLearningPlanStatus(strAction,learningPlanId){
	//alert("getLearningPlanStatus summary jsp strAction ===>> " + strAction+"==>learningPlanId==>"+learningPlanId);
	$("#divLPDetailsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?lPlanId='+learningPlanId,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result3==>"+result);
			$("#divLPDetailsResult").html(result);
   		}
	});
}

function getLearningPlanPreview(strAction,learningPlanId){
	//alert("getLearningPlanStatus summary jsp strAction ===>> " + strAction+"==>learningPlanId==>"+learningPlanId);
	$("#divLPDetailsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?planId='+learningPlanId,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result3==>"+result);
			$("#divLPDetailsResult").html(result);
   		}
	});
}

</script>