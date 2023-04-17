<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<% String sbSelfReviewRequest = (String)request.getAttribute("sbSelfReviewRequest");
 String appId = (String)request.getAttribute("appId");
 String appFreqId = (String)request.getAttribute("appFreqId");
	if(sbSelfReviewRequest != null && !sbSelfReviewRequest.equals("")) {
%>
	
	<%=sbSelfReviewRequest%>
	<div id="reviewSummary" style="min-height: 600px;">
		<s:action name="MyReviewSummary" executeResult="true">
    		<s:param name="id"><%=appId%></s:param>
    		<s:param name="appFreqId"><%=appFreqId%></s:param>
    		<s:param name="fromPage">SRR</s:param>
   		 </s:action>  
     </div>
  <%} else { %>
  		<div class="nodata msg">No self review requests.</div>
  <% } %>
  
  <script>
  
	  function getMyReviewSummary(strAction,appId,appFreqId,fromPage){
			//alert("getMyReviewSummary jsp strAction ===>> " + strAction+"==appId==>"+appId+"==>appFreqId==>"+appFreqId);
			$("#reviewSummary").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'POST',
				url: strAction+'.action?id='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage,
				//data: $("#"+this.id).serialize(),
				cache: true,
				success: function(result){
					//alert("result2==>"+result);
					$("#reviewSummary").html(result);
		   		}
			});
		}

  </script>