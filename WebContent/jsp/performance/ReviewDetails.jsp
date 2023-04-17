<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	String appId = (String)request.getParameter("appId");
	String currUserType = (String)request.getParameter("currUserType");
	String appFreqId = (String)request.getAttribute("appFreqId");
	String fromPage = (String)request.getAttribute("fromPage");
	//System.out.println("reviewDetails appId==>"+appId+"==>appFreqId==>"+appFreqId);
%>

			<div class="nav-tabs-custom">
				<ul class="nav nav-tabs">
				<!-- created by seema -->
      				<li class="active"><a href="javascript:void(0)" onclick="getReviewSummary('AppraisalSummary','<%=appId %>','<%=appFreqId %>','<%=fromPage %>','summary');" data-toggle="tab">Summary</a></li>
      				<li><a href="javascript:void(0)" onclick="getReviewSummary('AppraisalSummary','<%=appId %>','<%=appFreqId %>','<%=fromPage %>','reviewforms');" data-toggle="tab">Review Forms</a></li>
      		   <!-- created by seema -->
      				<li><a href="javascript:void(0)" onclick="getReviewStatus('AppraisalStatus','<%=appId %>','<%=appFreqId %>','<%=fromPage %>');" data-toggle="tab">Status</a></li>
      			
      		   </ul>
	           <div class="tab-content" >
	              <div class="active tab-pane" id="reviewInfo" style="min-height: 600px;">
				
	              </div>
	           </div>
	        </div>
   
<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	<!-- created by seema -->
	getReviewSummary('AppraisalSummary','<%=appId%>','<%=appFreqId %>','<%=fromPage %>','summary');
	<!-- created by seema -->
});
<!-- created by seema -->
function getReviewSummary(strAction,appId,appFreqId,fromPage,pageTab){
	//alert("ReviewDetails App summary jsp strAction ===>> " + strAction+"==>appId==>"+appId+"==appFreqId==>"+appFreqId);
	$("#reviewInfo").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action?id='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&tabName='+pageTab,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result) {
			//alert("result3==>"+result);
			$("#reviewInfo").html(result);
   		}
	});
}
<!-- created by seema -->
function getReviewStatus(strAction,appId,appFreqId,fromPage,pageTab){
	//alert("AppraisalStatus strAction ===>> " + strAction+"==>appId==>"+appId+"==appFreqId==>"+appFreqId);
	$("#reviewInfo").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?id='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result4==>"+result);
			$("#reviewInfo").html(result);
   		}
	});
}
</script>