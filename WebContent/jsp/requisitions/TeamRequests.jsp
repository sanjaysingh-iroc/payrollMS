<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>

	<% 	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		String callFrom = (String) request.getAttribute("callFrom"); 
		String alertID = (String) request.getAttribute("alertID");
		String currUserType = (String) request.getAttribute("currUserType");
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		String []arrEnabledModules = CF.getArrEnabledModules();
	%>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { 
                    %>
                    	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
                    		<li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('RequirementApprovalData','?currUserType=<%=currUserType%>');" data-toggle="tab">Requirements</a></li>
                    	<% } %>
                    	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
	                    	<li <% if(callFrom != null && callFrom.equals("NotiApplySelfReview")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodAppraisalRequest','?dataType=SRR');" data-toggle="tab">Reviews</a></li>
	                    <% } %>
	                    <li id="liResignation" <% if(callFrom != null && callFrom.equals("NotiResignation")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodResignationRequest','?currUserType=<%=currUserType%>');" data-toggle="tab">Resignations</a></li>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyLeave")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodLeaveRequest','?currUserType=<%=currUserType%>');" data-toggle="tab">Leave</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyTravel")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodTravelRequest','?currUserType=<%=currUserType%>');" data-toggle="tab">Travel</a></li>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>
	                    	<li <% if(callFrom != null && callFrom.equals("NotiApplyReimbursement")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodReimbursementRequest','?currUserType=<%=currUserType%>');" data-toggle="tab">Reimbursements</a></li>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyPerk")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('Perk','');" data-toggle="tab">Perks</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyLTA")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodCTCVariableRequest','?currUserType=<%=currUserType%>');" data-toggle="tab">CTC</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiLeaveEncashment")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodLeaveEncashmentRequest','?currUserType=<%=currUserType%>');" data-toggle="tab">Leave Encashment</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyLoan")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodLoanRequest','?currUserType=<%=currUserType%>');" data-toggle="tab">Loan</a></li>
	                    <% } %>
	                    <!-- ===start parvez date: 30-09-2021=== -->
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0) { %>
                    		<%-- <li <% if(callFrom == null || callFrom.equals("NotiApplyLearningRequest")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('LearningRequestApproval','?currUserType=<%=currUserType%>');" data-toggle="tab">Learning</a></li> --%>
                    		<li <% if(callFrom != null && callFrom.equals("LRDash")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CeoHodLearningRequest','?currUserType=<%=currUserType%>');" data-toggle="tab">Learning</a></li>
                    	<% } %>
	                    <!-- ===end parvez date: 30-09-2021=== -->
	                    
                    <% } else { %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
	                    	<li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('RequirementApproval','');" data-toggle="tab">Requirements</a></li>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
	                    	<li <% if(callFrom != null && callFrom.equals("NotiApplySelfReview")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('AppraisalDashboard','?dataType=SRR&callFrom=TeamRequest');" data-toggle="tab">Reviews</a></li>
	                    <% } %>
	                    <li id="liResignation" <% if(callFrom != null && callFrom.equals("NotiResignation")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('ResignationReport','');" data-toggle="tab">Resignations</a></li>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
	                    	<li <% if(callFrom != null && callFrom.equals("NotiApplyLeave")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('ManagerLeaveApprovalReport','?currUserType=<%=currUserType%>');" data-toggle="tab">Leave</a></li>
	                    	<li <% if(callFrom != null && callFrom.equals("NotiApplyTravel")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('TravelApprovalReport','?currUserType=<%=currUserType%>');" data-toggle="tab">Travel</a></li>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>
	                    	<li <% if(callFrom != null && callFrom.equals("NotiApplyReimbursement")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('Reimbursements','?currUserType=<%=currUserType%>');" data-toggle="tab">Reimbursements</a></li>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyPerk")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('Perk','');" data-toggle="tab">Perks</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyLTA")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('CTCVariable','?currUserType=<%=currUserType%>');" data-toggle="tab">CTC</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiLeaveEncashment")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('LeaveEncashment','?currUserType=<%=currUserType%>');" data-toggle="tab">Leave Encashment</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyLoan")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('LoanApplicationReport','?currUserType=<%=currUserType%>');" data-toggle="tab">Loan</a></li>
	                    <% } %>
	               <!-- ===start parvez date: 30-09-2021=== -->
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0) { %>
                    		<%-- <li <% if(callFrom == null || callFrom.equals("NotiApplyLearningRequest")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('LearningRequestApproval','?currUserType=<%=currUserType%>');" data-toggle="tab">Learning</a></li> --%>
                    		<li <% if(callFrom != null && callFrom.equals("LRDash")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTeamRequestPage('LearningRequestApprovalReport','?currUserType=<%=currUserType%>');" data-toggle="tab">Learning</a></li>
                    	<% } %>
	               <!-- ===end parvez date: 30-09-2021=== -->
                    <% } %>
                </ul>
                <div class="tab-content" >
                 <!-- ===start parvez date: 24-02-2023=== -->  
                    <div class="active tab-pane" id="divResult" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;">
				<!-- ===end parvez date: 24-02-2023=== -->		
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>


<script type="text/javascript" charset="utf-8">
$(function(){
	<% if(callFrom != null && callFrom.equals("NotiResignation")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		/* getTeamRequestPage('CeoHodLeaveRequest', '?currUserType=CEO&callFrom=NotiApplyLeave'); */
		getTeamRequestPage('CeoHodResignationRequest','?currUserType=<%=currUserType%>&callFrom=NotiResignation&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('ResignationReport', '?alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiApplySelfReview")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		/* getTeamRequestPage('CeoHodLeaveRequest', '?currUserType=CEO&callFrom=NotiApplyLeave'); */
		getTeamRequestPage('CeoHodAppraisalRequest','?dataType=SRR&currUserType=<%=currUserType%>&callFrom=NotiApplySelfReview&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('AppraisalDashboard', '?dataType=SRR&callFrom=TeamRequest&alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiApplyLeave")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		/* getTeamRequestPage('CeoHodLeaveRequest', '?currUserType=CEO&callFrom=NotiApplyLeave'); */
		getTeamRequestPage('CeoHodLeaveRequest','?currUserType=<%=currUserType%>&callFrom=NotiApplyLeave&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('ManagerLeaveApprovalReport', '?alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiApplyTravel")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		getTeamRequestPage('CeoHodTravelRequest', '?currUserType=<%=currUserType%>&callFrom=NotiApplyTravel&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('TravelApprovalReport', '?alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiApplyReimbursement")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		getTeamRequestPage('CeoHodReimbursementRequest','?currUserType=<%=currUserType%>&callFrom=NotiApplyReimbursement&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('Reimbursements', '?alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiApplyPerk")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		getTeamRequestPage('Perk', '?currUserType=<%=currUserType%>&callFrom=NotiApplyPerk&alertID=<%=alertID%>'); 
		<% } else { %>
		getTeamRequestPage('Perk', '?alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiApplyLTA")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		getTeamRequestPage('CeoHodCTCVariableRequest','?currUserType=<%=currUserType%>&callFrom=NotiApplyLTA&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('CTCVariable', '?alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiLeaveEncashment")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD)) ) { %>
		getTeamRequestPage('CeoHodLeaveEncashmentRequest', '?currUserType=<%=currUserType%>&callFrom=NotiLeaveEncashment&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('LeaveEncashment', '?alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiApplyLoan")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		getTeamRequestPage('CeoHodLoanRequest','?currUserType=<%=currUserType%>&callFrom=NotiApplyLoan&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('LoanApplicationReport', '?alertID=<%=alertID%>');
		<% } %>
	<% } else if(callFrom != null && callFrom.equals("NotiApplySelfReview")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		getTeamRequestPage('CeoHodAppraisalRequest','?currUserType=<%=currUserType%>&callFrom=NotiApplyLoan&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('LoanApplicationReport', '?alertID=<%=alertID%>');
		<% } %>
		/* ===start parvez date: 07-10-2021=== */
	<% } else if(callFrom != null && callFrom.equals("LRDash")) { %>
		<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		/* getTeamRequestPage('CeoHodLeaveRequest', '?currUserType=CEO&callFrom=NotiApplyLeave'); */ 
		getTeamRequestPage('CeoHodLearningRequest','?currUserType=<%=currUserType%>&callFrom=LRDash&alertID=<%=alertID%>');
		<% } else { %>
		getTeamRequestPage('LearningRequestApprovalReport', '?alertID=<%=alertID%>');
		<% } %>
		/* ===end parvez date: 07-10-2021=== */
	<% } else { %>
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
			<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
			getTeamRequestPage('RequirementApprovalData','?currUserType=<%=currUserType%>');
			<% } else { %>
				getTeamRequestPage('RequirementApproval', '?alertID=<%=alertID%>');
			<% } %>
		<% } else { %>	
			<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
			getTeamRequestPage('CeoHodResignationRequest','?currUserType=<%=currUserType%>&callFrom=NotiResignation&alertID=<%=alertID%>');
			document.getElementById('liResignation').className = "active";
			<% } else { %>
			getTeamRequestPage('ResignationReport', '?alertID=<%=alertID%>');
			document.getElementById('liResignation').className = "active";
			<% } %>
		<% } %>
		
	<% } %>
});

function getTeamRequestPage(strAction, parameters){
	//console.log("parameters==>"+parameters);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action'+parameters,
		success: function(result){	
			$("#divResult").html(result);
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

