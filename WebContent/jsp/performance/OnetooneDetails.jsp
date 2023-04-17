<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	
	String oneToOneId = (String)request.getAttribute("oneToOneId");
	String fromPage = (String)request.getAttribute("fromPage");
	//System.out.println("reviewDetails appId==>"+appId+"==>appFreqId==>"+appFreqId);
%>

			<div class="nav-tabs-custom">
				<ul class="nav nav-tabs">
      				<li class="active"><a href="javascript:void(0)" onclick="getOneToOneSummary('OneToOneSummary','<%=oneToOneId %>','<%=fromPage %>');" data-toggle="tab">Summary</a></li>
      				<li><a href="javascript:void(0)" onclick="getOneToOneStatus('OneToOneSummary','<%=oneToOneId %>','<%=fromPage %>');" data-toggle="tab">Status</a></li>
      		   </ul>
	           <div class="tab-content" >
	              <div class="active tab-pane" id="oneToOneInfo" style="min-height: 600px;">
				
	              </div>
	           </div>
	        </div>
 <script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	getOneToOneSummary('OneToOneSummary','<%=oneToOneId %>','<%=fromPage %>');
});

function getOneToOneSummary(strAction,oneToOneId,fromPage){
	$("#oneToOneInfo").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action?strId='+oneToOneId+'&fromPage='+fromPage,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result) {
			//alert("result3==>"+result);
			$("#oneToOneInfo").html(result);
   		}
	});
}

function getOneToOneStatus(strAction,oneToOneId,fromPage){
	$("#oneToOneInfo").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?id='+oneToOneId+'&fromPage='+fromPage,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result) {
			//alert("result3==>"+result);
			$("#oneToOneInfo").html(result);
   		}
	});
}
</script>
   