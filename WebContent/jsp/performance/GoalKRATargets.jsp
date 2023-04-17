<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
</script>
<%String callFrom = (String)request.getAttribute("callFrom"); 
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String pType = (String) request.getAttribute("pType");
String alertStatus = (String) request.getAttribute("alertStatus");
String alert_type = (String) request.getAttribute("alert_type");
String alertID = (String) request.getAttribute("alertID");
String strEmpId = (String) request.getAttribute("strEmpId");

 String strClassL1 ="class=\"active\"";
 String strClassL2 ="";
	if(callFrom != null && callFrom.equals("KTDash")) {
		strClassL1 ="";
		strClassL2 ="class=\"active\"";
	}
%> 
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
		<!--  ===start parvez date: 23-02-2023=== -->	
			<div class="col-md-12" style="padding-left: 0px;min-height: 600px; max-height: 600px;">
		<!--  ===end parvez date: 23-02-2023=== -->		 
				 <div class="box box-none nav-tabs-custom">
           	 		<ul class="nav nav-tabs">
           	 		   	<!-- Started By Dattatray Date:29-09-21 -->
           	 		   	<li <%=strClassL1 %>><a href="javascript:void(0)" onclick="getGoalsData('GoalSummaryDashboard','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>', '<%=strEmpId %>',0);" data-toggle="tab" id="id0">OKR</a></li>
     					<li <%=strClassL2 %>><a href="javascript:void(0)"  onclick="getGoalsData('GoalKRATargetDashboard','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>', '<%=strEmpId %>',1);" data-toggle="tab" id="id1">
						<% if(strUserType!=null && strUserType.equals(IConstants.MANAGER)) { %>
     						Team KRAs
     					<% } else { %>
     						Employee KRAs
     					<% } %>
     					<!-- Ended By Dattatray Date:29-09-21 -->
						</a></li> <!-- Goals, KRAs, Targets -->
           	 		</ul>
           	 	<!-- ===start parvez date: 23-02-2023=== -->	
           	 		<div class="active tab-pane" id="divGoalsResult" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;">
				<!-- ===end parvez date: 23-02-2023=== -->   
				   </div>
			    </div>
			 </div>
		 </section>
	 </div>
</section>


 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		<%if(callFrom != null && callFrom.equals("KTDash")){%>
			getGoalsData('GoalKRATargetDashboard','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>', '<%=strEmpId %>','1');//Created By Dattatray Date:19-10-21
		<%} else {%>
			getGoalsData('GoalSummaryDashboard','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>', '<%=strEmpId %>','0');//Created By Dattatray Date:19-10-21
		<% }%>
	});
	
	function getGoalsData(strAction,alertStatus,alert_type,pType,alertID,strEmpId,index){
		//alert("GoalKRATargetDashboard jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		disabledPointerAddAndRemove(2,'id',index,true);//Created By Dattatray Date:19-10-21
		$("#divGoalsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?fromPage=GKTS&alertStatus='+alertStatus+'&alert_type='+alert_type+'&pType='+pType
				+'&alertID='+alertID+'&strEmpId='+strEmpId,
			cache: true,
			success: function(result){
				//alert("result1==>"+result);
				$("#divGoalsResult").html(result);
				disabledPointerAddAndRemove(2,'id',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
	}
	
	/* ===start parvez date: 24-02-2023=== */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#divGoalsResult").scrollTop() != 0) {
	        	$("#divGoalsResult").scrollTop($("#divGoalsResult").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#divGoalsResult").scrollTop($("#divGoalsResult").scrollTop() + 30);
	   		}
	    }
	});

	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#divGoalsResult").scrollTop($("#divGoalsResult").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#divGoalsResult").scrollTop() != 0) {
		    	$("#divGoalsResult").scrollTop($("#divGoalsResult").scrollTop() - 50);
		    }
		}
	});
	/* ===end parvez date: 24-02-2023=== */

</script>