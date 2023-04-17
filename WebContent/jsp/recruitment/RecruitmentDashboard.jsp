<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%String fromPage =(String)request.getAttribute("fromPage"); 
	String callFrom =(String)request.getAttribute("callFrom");
	String recruitId = (String)request.getAttribute("recruitId");
%>
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
	<script type="text/javascript" src="scripts/charts/jquery.min.js" ></script>
<%} %>

<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
<section class="content">
    <div class="row jscroll">
        <section class="connectedSortable">
 
       	   <div class="col-md-12">
       	   		<div class="box box-primary"> 
<%} %>
       	    		<div class="box-body">
       		  			<div class="active tab-pane" id="divResult" style="min-height: 600px;">
						
			  			</div>
			 		 </div>
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
			 	 </div>
		    </div>
        </section>
    </div>
</section>
<%} %>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	getRecruitmentDashboardData('RecruitmentDashboardData', '', '','<%=fromPage %>','<%=callFrom %>', '<%=recruitId %>');
});

function getRecruitmentDashboardData(strAction, dataType, currUserType, fromPage, callFrom, recruitId) {
	//alert("getRecruitmentDashboardData jsp action" );
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action?fromPage='+fromPage+'&callFrom='+callFrom+'&recruitId='+recruitId,
		cache: true,
		success: function(result) {
			//alert("result1==>"+result);
			$("#divResult").html(result);
   		}
	});
}

</script>
        	