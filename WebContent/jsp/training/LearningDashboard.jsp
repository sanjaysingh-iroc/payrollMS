<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script src='scripts/charts/jquery.min.js'></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery-ui-1.8.16.custom.min.js"></script>

<script src="scripts/ckeditor_cust/ckeditor.js"></script> 
<%	
	String dataType = (String) request.getAttribute("dataType"); 
	String callFrom = (String) request.getAttribute("callFrom"); 
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    String strClassL1 = "class=\"active\"";
    String strClassL2 = "";
    String strClassL3 = "";
    
    if(callFrom != null && (callFrom.equalsIgnoreCase("LA") || callFrom.equalsIgnoreCase("LDash"))) {
    	 strClassL1 = "";
    	 strClassL2 = "class=\"active\"";
    	 strClassL3 = "";
    } else if(callFrom != null && (callFrom.equalsIgnoreCase("LRDash"))) {
    	strClassL1 = "";
    	strClassL2 = "";
   	 	strClassL3 = "class=\"active\"";
    }
%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
			
			<div class="col-md-12 no-padding" style="min-height: 600px; ">
			  	
			  	<div class="box box-none nav-tabs-custom">
            	 		<ul class="nav nav-tabs">
            	 		    <%-- <% if(callFrom != null && callFrom.equalsIgnoreCase("LDash")) { %>
            	 		    	<li class="active"><a href="javascript:void(0)" onclick="getLearningDashboardData('TrainingGap','LD');" data-toggle="tab">Learning Gaps</a></li>
            	 		    <%}else { %>  --%> 
            	 		       	 		
           	 		   		<li <%=strClassL1 %>><a href="javascript:void(0)" onclick="getLearningDashboardData('LearningPlanDashboard','LD','0');" data-toggle="tab" id="learningId_0">Learnings</a></li>
     						<li><a href="javascript:void(0)" onclick="getLearningDashboardData('CourseDashboard','LD','1');" data-toggle="tab" id="learningId_1">Courses & Assessments</a></li>
      						<li><a href="javascript:void(0)" onclick="getLearningDashboardData('TrainingPlanInfo','LD','2');" data-toggle="tab" id="learningId_2">Classroom Trainings</a></li>
      				
      						<!-- <li><a href="javascript:void(0)" onclick="getLearningDashboardData('VideosDashboard','LD');" data-toggle="tab">Videos</a></li> -->
            	 	
      						<li <%=strClassL2 %>><a href="javascript:void(0)" onclick="getLearningDashboardData('TrainingGap','LD','3');" data-toggle="tab" id="learningId_3">Learning Gaps</a></li>
      					 	<li ><a href="javascript:void(0)" onclick="getLearningDashboardData('TrainerInfo','LD','4');" data-toggle="tab" id="learningId_4">Trainers</a></li>
      						<li><a href="javascript:void(0)" onclick="getLearningDashboardData('CertificateInfo','LD','5');" data-toggle="tab" id="learningId_5">Certificates</a></li>
      				
      						<li <%=strClassL3 %>><a href="javascript:void(0)" onclick="getLearningDashboardData('LearningRequestApprovalReport','LD','6');" data-toggle="tab" id="learningId_6">Learning Request</a></li>
            	 	
            	 		    <%-- <%} %> --%>
            	 		</ul>
            	 		
            	 		<div class="box-body" style="padding: 5px;min-height: 600px;">
							<div class="nav-tabs-custom">
								<div class="tab-content" >
								<!-- ===start parvez date: 24-02-2023=== -->
		             				 <div class="active tab-pane" id="divResult" style="min-height: 550px; max-height: 500px !important; overflow-y: hidden;">
								<!-- ===end parvez date: 24-02-2023=== -->
					
		        				      </div>
		           				</div>
		        			</div>
					    </div>
            	 	</div>
			  </div>
		 </section>
	 </div>
</section>

 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		<% if(callFrom != null && (callFrom.equalsIgnoreCase("LA") || callFrom.equalsIgnoreCase("LDash"))) { %>
			getLearningDashboardData('TrainingGap','LD','');
		<% } else if(callFrom != null && (callFrom.equalsIgnoreCase("LRDash"))){%>
			getLearningDashboardData('LearningRequestApprovalReport','LD','');
		<% } else { %>
			getLearningDashboardData('LearningPlanDashboard','LD','0');//Created By Dattatray Date:19-10-21
		<% } %>
	});
	
	function getLearningDashboardData(strAction,fromPage,index){
		//alert("getLearningPlanDashboard jsp action" + strAction+"==>fromPage==>"+fromPage);
		disabledPointerAddAndRemove(7,'learningId_',index,true);//Created By Dattatray Date:19-10-21
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?fromPage='+fromPage,
			cache: true,
			success: function(result){
			//	alert("result1==>"+result);
				$("#divResult").html(result);
				disabledPointerAddAndRemove(7,'learningId_',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
	}
	
	/* ===start parvez date: 24-02-2023=== */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#divResult").scrollTop() != 0) {
	        	$("#divResult").scrollTop($("#divResult").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#divResult").scrollTop($("#divResult").scrollTop() + 30);
	   		}
	    }
	});

	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#divResult").scrollTop($("#divResult").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#divResult").scrollTop() != 0) {
		    	$("#divResult").scrollTop($("#divResult").scrollTop() - 50);
		    }
		}
	});
	/* ===end parvez date: 24-02-2023=== */
</script>