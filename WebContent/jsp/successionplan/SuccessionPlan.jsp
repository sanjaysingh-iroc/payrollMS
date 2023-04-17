<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="scripts/_rating/js/jquery.raty.js"> </script>
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
 --%>	
<%
String callFrom = (String)request.getAttribute("callFrom"); 

String empId = (String)request.getAttribute("empId"); 
String pType = (String) request.getAttribute("pType");
String alertStatus = (String) request.getAttribute("alertStatus");
String alert_type = (String) request.getAttribute("alert_type");
String alertID = (String) request.getAttribute("alertID");
String Flag = "true";
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
			<div class="col-md-12" style="padding-left: 0px;min-height: 600px;">
				 <div class="box box-none nav-tabs-custom">
           	 		<ul class="nav nav-tabs">
           	 		   	<!-- Started By Dattatray Date:28-09-21  Note: Id applied on all tab-->
	           	 		   	<li <%=strClassL1 %>><a href="javascript:void(0)" onclick="getSuccessionPlanData('ShowSuccessionPlan','0');" data-toggle="tab" id="id0">Succession Plan</a></li>
	     					<li <%=strClassL2 %>><a href="javascript:void(0)" onclick="getSuccessionPlanData('SuccessionPlanReport','1');" data-toggle="tab" title="Settings" id="id1"><i class="fa fa-gear faa-spin" style="font-size: 14px;"></i></a></li> <!-- Goals, KRAs, Targets -->
           	 			<!-- Ended By Dattatray Date:28-09-21  Note: Id applied on all tab-->
           	 		</ul>
           	 <!-- ===start parvez date: 24-02-2023=== -->		
           	 		<div class="active tab-pane" id="divSuccessionPlanResult" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;">
			<!-- ===end parvez date: 24-02-2023=== -->	   
				   </div>
			    </div>
			 </div>
		 </section>
	 </div>
</section>


 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getSuccessionPlanData('ShowSuccessionPlan','0');//Created By Dattatray Date:19-10-21
	});
	
	function getSuccessionPlanData(strAction,index) {
		//alert("GoalKRATargetDashboard jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		disabledPointerAddAndRemove(2,'id',index,true);//Created By Dattatray Date:29-09-21
		$("#divSuccessionPlanResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action',
			cache: true,
			success: function(result){
				$("#divSuccessionPlanResult").html(result);
				disabledPointerAddAndRemove(2,'id',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
	}

	/* ===start parvez date: 24-02-2023=== */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#divSuccessionPlanResult").scrollTop() != 0) {
	        	$("#divSuccessionPlanResult").scrollTop($("#divSuccessionPlanResult").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#divSuccessionPlanResult").scrollTop($("#divSuccessionPlanResult").scrollTop() + 30);
	   		}
	    }
	});

	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#divSuccessionPlanResult").scrollTop($("#divSuccessionPlanResult").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#divSuccessionPlanResult").scrollTop() != 0) {
		    	$("#divSuccessionPlanResult").scrollTop($("#divSuccessionPlanResult").scrollTop() - 50);
		    }
		}
	});
	/* ===end parvez date: 24-02-2023=== */	
    
</script>