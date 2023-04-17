<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
</script>
<%String strUserType = (String) session.getAttribute("USERTYPE");
  String callFrom = (String) request.getAttribute("callFrom");
  String pType = (String) request.getAttribute("pType");
  String alertStatus = (String) request.getAttribute("alertStatus");
  String alert_type = (String) request.getAttribute("alert_type");
  String alertID = (String) request.getAttribute("alertID");
  
	String strClassL1 = "class=\"active\"";
	String strClassL2 = "";
	boolean flagNoTranslate = true;
	if(callFrom != null && (callFrom.equals("Dash") || callFrom.equals("FDash"))) {
		strClassL2 = "class=\"active\"";
		strClassL1 = "";
		flagNoTranslate = false;
	}
	
%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
			<div class="col-md-12" style="padding-left: 0px;min-height: 600px;">
				 <div class="box box-none nav-tabs-custom">
           	 		<ul class="notranslate nav nav-tabs">
						<li <%=strClassL1%>><a href="javascript:void(0)" onclick="getWorkforcePlanData('ResourcePlanner','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>');" data-toggle="tab">Workforce Plan</a></li>
						<%-- <li <%=strClassL2%> ><a href="javascript:void(0)" onclick="getWorkforcePlanData('ProjectResourceRequests','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>');" data-toggle="tab">Resource Request for Projects</a></li> --%>
     				</ul>
     			<!-- ===start parvez date: 24-02-2023=== -->	
           	 		<!-- <div class=" active tab-pane" id="divWFResult" style="min-height: 600px;"> -->
           	 		<div class=" active tab-pane" id="divWFResult" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;">
           	 	<!-- ===end parvez date: 24-02-2023=== -->	
				   </div>
			    </div>
			 </div>
		 </section>
	 </div>
</section>


 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		<%-- <%if(callFrom != null && (callFrom.equals("Dash") || callFrom.equals("FDash"))) {%>
		getWorkforcePlanData('ProjectResourceRequests');
		<%} else {%> --%>
		getWorkforcePlanData('ResourcePlanner');
		<%-- <%}%> --%>
	});
	
	function getWorkforcePlanData(strAction) {
		//alert("getReviewsData jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		/* url: strAction+'.action?fromPage=R&alertStatus='+alertStatus+'&alert_type='+alert_type+'&pType='+pType+'&alertID='+alertID
				+'&reviewId='+reviewId+'&appFreqId='+appFreqId, */
		$("#divWFResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?fromPage=WF',
			cache: true,
			success: function(result){
				//alert("result1==>"+result);
				$("#divWFResult").html(result);
	   		}
		});
	}
	
	/* ===start parvez date: 24-02-2023=== */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#divWFResult").scrollTop() != 0) {
	        	$("#divWFResult").scrollTop($("#divWFResult").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#divWFResult").scrollTop($("#divWFResult").scrollTop() + 30);
	   		}
	    }
	});

	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#divWFResult").scrollTop($("#divWFResult").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#divWFResult").scrollTop() != 0) {
		    	$("#divWFResult").scrollTop($("#divWFResult").scrollTop() - 50);
		    }
		}
	});
	/* ===end parvez date: 24-02-2023=== */

</script>