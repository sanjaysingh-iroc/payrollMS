<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*,ChartDirector.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
li>span { 
	top: 0px;
}
 
.btn-info {
	background-color: #00c0ef;
	border-color: #00acd6;
	color: #fff;
	font-weight: 600;
} 

.btn-info:hover {
	background-color: #1298B9;
}
</style>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script>

function sendMail() {
	if(confirm('Are you sure, you want to send request to file your tax return?')) {
		getContent('msgDiv', 'SendReminder.action?mailType=ITR_FILLING_MAIL');
	}
	//window.location = 'SendReminder.action?mailType=ITR_FILLING_MAIL';
}


function onBoardProcessingFun() {
	var dialogEdit ='#modal-body1';

		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo1").show();
		
		 var height = $(window).height()* 0.95;
		 var width = $(window).width()* 0.95;
		 
			$("#modal-dialog1").css("height", height);
			$("#modal-dialog1").css("width", width);
			$("#modal-dialog1").css("max-height", height);
			$("#modal-dialog1").css("max-width", width);
			
		 $("#modal-title1").html('My Onboarding');
		 
		 $.ajax({
				url:"OnBoardProcessing.action",
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		}

</script>

<script type="text/javascript">  
    $(document).ready(function(){
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    		document.getElementById("modalContent").setAttribute('class', 'modal-content');
    		document.getElementById("modalHeader").setAttribute('class', 'modal-header');
    		document.getElementById("modalFooter").style.display = "block";
        });
    	
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    		document.getElementById("modalContent").setAttribute('class', 'modal-content');
    		document.getElementById("modalHeader").setAttribute('class', 'modal-header');
    		document.getElementById("modalFooter").style.display = "block";
    	});
    	
    	$("body").on('click','#closeButton1',function(){
			$(".modal-dialog1").removeAttr('style');
			$("#modalInfo1").hide();
	    });
    	
		$("body").on('click','#close1',function(){
			$(".modal-dialog1").removeAttr('style');
			$("#modalInfo1").hide();
	    });
    	
    });
                      
   function getEmpProfile(val) {
       	var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 
   		 $(".modal-title").html('Employee Profile');
   		$.ajax({
		     url : "AppraisalEmpProfile.action?empId=" + val,
		     cache : false,
		     success : function(data) {
		     $(dialogEdit).html(data);
		     }
	     });
   	}
     
     
     function openEventPopup(eventId) {
 		var dialogEdit = '.modal-body';
 		$(dialogEdit).empty();
 		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
 		$('.modal-title').html('Event Information');
 		$("#modalInfo").show();
 		$.ajax({
 			url :"EventPopup.action?eventId="+eventId,
 			cache : false,
 			success : function(data) {
 				$(dialogEdit).html(data);
 			}
 		});
 		
 	 }
                
    function getTeamMembers() {
     
     	var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('My Team');
   		$.ajax({
 			url : "TeamMembers.action?type=MyTeam",
 			cache : false,
 			success : function(data) {
 				$(dialogEdit).html(data);
 			}
 		});
     }
     
     function approveDenyLeave(apStatus,leaveId,levelId,compensatory,userType){
     	var status = '';
     	if(apStatus == '1'){
     		status='approve';
     	} else if(apStatus == '-1'){
     		status='deny';
     	}
     	if(confirm('Are you sure, do you want to '+status+' this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'ManagerLeaveApproval.action?type=type&apType=auto&apStatus='+apStatus+'&E='+leaveId+'&LID='+levelId+'&strCompensatory='+compensatory+'&mReason='+reason+'&userType='+userType;
     			//alert(action); 
     			window.location = action;
     		}
     	}
     }
     
     function approveDenyReimbursement(apStatus, reimbId, userType) {
     	var status = '';
     	if(apStatus == '1'){
     		status='approve';
     	} else if(apStatus == '-1'){
     		status='deny';
     	}
     	if(confirm('Are you sure, do you want to '+status+' this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'UpdateReimbursements.action?type=type&S='+apStatus+'&RID='+ reimbId +'&T=RIM&M=AA&mReason='+reason+'&userType='+userType; 
     			//alert(action); 
     			window.location = action;
     		}
     	}
     }
     
     function approveDenyPerk(apStatus,perkId,userType){
     	var status = '';
     	if(apStatus == '1'){
     		status='approve';
     	} else if(apStatus == '-1'){
     		status='deny';
     	}
     	if(confirm('Are you sure, do you want to '+status+' this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'UpdatePerks.action?type=type&S='+apStatus+'&RID='+perkId+'&T=PERK&M=AA&mReason='+reason+'&userType='+userType;
     			//alert(action); 
     			window.location = action;
     		}
     	}
     }
     
     
     function approveDenyLeaveEncash(apStatus,leaveEncashId,userType){
     	var status = '';
     	if(apStatus == '1'){
     		status='approve';
     	} else if(apStatus == '-1'){
     		status='deny';
     	}
     	if(confirm('Are you sure, do you want to '+status+' this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'UpdateLeaveEncashment.action?type=type&approveStatus='+apStatus+'&leaveEncashId='+leaveEncashId+'&mReason='+reason+'&userType='+userType;
     			//alert(action); 
     			window.location = action;
     		}
     	}
     }
     
    
     
     function approveDenyLTA(apStatus,ltaId,userType){
     	var status = '';
     	if(apStatus == '1'){
     		status='approve';
     	} else if(apStatus == '-1'){
     		status='deny';
     	}
     	if(confirm('Are you sure, do you want to '+status+' this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'UpdateLTA.action?type=type&approveStatus='+apStatus+'&empLtaId='+ltaId+'&mReason='+reason+'&userType='+userType;
     			//alert(action); 
     			window.location = action;
     		}
     	}
     }
     
     
     
     
     function approveDenyLoan(apStatus,loanId,userType){
     	var status = '';
     	if(apStatus == '1'){
     		status='approve';
     	} else if(apStatus == '-1'){
     		status='deny';
     	}
     	if(confirm('Are you sure, do you want to '+status+' this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'ApproveLoan.action?type=type&loanAppId='+loanId+'&approvalStatus='+apStatus+'&mReason='+reason+'&userType='+userType;
     			//alert(action); 
     			window.location = action;
     		}
     	}
     }
     
   
     function approveDenyResign(status, approveType, offBoardId,userType) {
     	 var denyApprove = "Approve";
     	 if(status == '-1') {
     		 denyApprove = "Deny";
     	 }
     	 var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('Resignation '+ denyApprove + ' Reason');
 	  		$.ajax({
 				url : 'UpdateRequest.action?type=type&S='+status+'&M='+approveType+'&RID='+offBoardId+'&T=REG&userType='+userType, 
 				cache : false,
 				success : function(data) {
 					$(dialogEdit).html(data);
 				}
 			});
     	}
     	
     
     
     function approveDenyRequisition(apStatus,strRequiId,userType){
     	var status = '';
     	if(apStatus == '1'){
     		status='approve';
     	} else if(apStatus == '-1'){
     		status='deny';
     	}
     	if(confirm('Are you sure, do you want to '+status+' this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'RequisitionApprovalReport.action?type=type&operation=E&approveStatus='+apStatus+'&strRequiId='+strRequiId+'&mReason='+reason+'&userType='+userType;
     			//alert(action); 
     			window.location = action;
     		}
     	}
     }	
     
     function approveDenySelfReviewRequest(strStatus,reviewId,userType,appFreqId) {
     	var status = '';
     	if(strStatus == '1'){
     		status='approve';
     	} else if(strStatus == '-1'){
     		status='deny';
     	}
     	if(confirm('Are you sure, do you want to '+status+' this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'SelfReviewRequestApproveDeny.action?type=type&reviewId='+reviewId+'&strStatus='+strStatus+'&strReason='+reason+'&userType='+userType+'&appFreqId='+appFreqId;
     			window.location = action;
     		}
     	}
     }
     
     
     function getApprovalEncashment(approveStatus,leaveEncashId,empname,empId,userType){
     	 
     	var dialogEdit = '.modal-body';
  		 $(dialogEdit).empty();
  		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  		 $("#modalInfo").show();
  		 $(".modal-title").html('Leave Encashment Approval Status of '+empname);	
  		$.ajax({
 			url : 'UpdateLeaveEncashment.action?type=type&approveStatus='+approveStatus+'&leaveEncashId='+leaveEncashId+'&empId='+empId+'&userType='+userType,
 			cache : false,
 			success : function(data) {
 				$(dialogEdit).html(data);
 			}
 		});
      
     }
     
     
     function denyRequest(ncount, RID, userType) {
     	var dialogEdit = '.modal-body';
 		 $(dialogEdit).empty();
 		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
 		 $("#modalInfo").show();
 		 $(".modal-title").html('Deny Reason');
 		 $.ajax({
 				url : "DenyRequestPopUp.action?type=type&ST=-1&RID="+RID+"&requestDeny=popup&userType="+userType,
 				cache : false,
 				success : function(data) {
 					$(dialogEdit).html(data);
 				}
 			});
     }
     
     
     function approveRequest(nCount,RID, userType) {
     	if(confirm('Are you sure, you want to approve this request?')){
     		var reason = window.prompt("Please enter your "+status+" reason.");
     		if (reason != null) {
     			var action = 'UpdateADRRequest.action?type=type&S=1&RID='+RID+'&mReason='+reason+'&userType='+userType;
     			window.location = action;
     		}
     	}
     }
     
     function approveDenyTravel(travelId,userTypeId,empname) {
         
     	var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('Approve / Deny Travel of '+empname);
   		$.ajax({
 			url : "ApproveTravel.action?type=myhome&E="+travelId+"&userType="+userTypeId,
 			cache : false,
 			success : function(data) {
 				$(dialogEdit).html(data);
 			}
 		});
     }
     
     
	/* Start Dattatray*/
	function approveDeny(apStatus,travelId,userType,empId){
     	var status = '';
      	if(apStatus == '1'){
      		status='approve';
      	} else if(apStatus == '-1'){
      		status='deny';
      	}
      	if(confirm('Are you sure, do you want to '+status+' this request?')){
      		var reason = window.prompt("Please enter your "+status+" reason.");
      		if (reason != null) {
      			var action ='ApproveTravel.action?travelType=OD&type=myhome&isapproved='+apStatus+'&leaveId='+travelId+'&userType='+userType+'&managerReason='+reason+'&empId='+empId;
      			console.log(action);
      			window.location = action;
      		}
      	}
     }/* End Dattatray*/

     
     function validateForm(){
   		var re5digit=/^\d{2}:\d{2}$/;
   		
   		/* if(document.frmClockEntries1.strNotify.checked){
   			if(document.frmClockEntries1.strNewTime.value==""){
   				alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
   				return false;
   			}else if(document.frmClockEntries1.strNewTime.value.search(re5digit)==-1){
   				alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
   				return false;
   			}
   		} */
   		if(document.frmClockEntries1.strReason.value==""){
   			alert('Please enter valid reason.');
   			return false;
   		}
   		
   	}

   	function validateService(){	
   		
   		var re5digit=/^\d{2}:\d{2}$/;
   		
   		if(document.frmClockEntries1.strRosterStartTime.value==""){
   			alert('Please enter roster start time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
   			return false;
   		}else if(document.frmClockEntries1.strRosterStartTime.value.search(re5digit)==-1){
   			alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
   			return false;
   		}
   		
   		if(document.frmClockEntries1.strRosterEndTime.value==""){
   			alert('Please enter roster end time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
   			return false;
   		}else if(document.frmClockEntries1.strRosterEndTime.value.search(re5digit)==-1){
   			alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
   			return false;
   		}
   		
   		if(document.frmClockEntries1.service.options[0].selected){
   			alert('Please choose the cost centre.'); 
   			return false;
   		}
   	}
    	
   
</script>
<%
    UtilityFunctions uF = new UtilityFunctions();
    /* List alSkills = (List) request.getAttribute("alSkills"); */
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    String []arrEnabledModules = CF.getArrEnabledModules();
    //String empUserTypeId = (String) request.getAttribute("EMP_USER_TYPE_ID");
    String empUserTypeId = (String) request.getAttribute("strUserTypeId");
    String sessionEmpId = (String) session.getAttribute(IConstants.EMPID);
    //Map<String,List<String>> hmThoughts = (Map<String, List<String>>) request.getAttribute("hmthoughts");
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
    if (hmEmpProfile == null) {
    	hmEmpProfile = new HashMap<String, String>();
    }
    
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
    Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
    Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
    String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
    
    boolean isClockOnOffBlockShow = CF.getFeatureManagementStatus(request, uF, IConstants.F_CLOCK_ON_OFF_BLOCK_SHOW);
    String strUITheme = CF.getStrUI_Theme();
    
    %>
<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0 && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
<%=uF.showData(((String)session.getAttribute(IConstants.MESSAGE)), "") %>
<% session.removeAttribute(IConstants.MESSAGE); %>
<section class="content">
<div class="row jscroll">
	<section class="col-lg-4 connectedSortable">
	<div class="box box-widget widget-user widget-user1">
		<!-- Add the bg color to the header using any of the bg-* classes -->
		<!-- <div class="widget-user-header bg-aqua-active"> -->
			<%if(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO) !=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
				List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
			%>
			<div class="widget-user-header bg-aqua-active" style="height:140px !important;">
				<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png"
					style="display: inline; position: absolute; height:auto;"
					data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
			<% } else{ %>
			
				<div class="widget-user-header bg-aqua-active">
				<img class="lazy" src="images1/user-background-photo.jpg"
				style="display: inline; position: absolute;"
				data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
			<% } %>
			<h3 class="widget-user-username"
				style="color: #fff; font-weight: 600; margin-top: <%=uF.parseToInt(strUITheme)==1 ? "-135px;" : "0px;" %>;">
				<span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>
				<span style="float: right;"><a href="MyProfile.action"
					title="Go to My FactSheet.."><i class="fa fa-address-card-o"
						style="color: #fff;"></i>
				</a>
				</span>
			</h3>
			<h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
		</div>
		<div class="widget-user-image">
			<%if(docRetriveLocation==null) { %>
			<img class="img-circle lazy" id="profilecontainerimg"
				src="userImages/avatar_photo.png"
				data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
			<%} else { %>
			<img class="img-circle lazy" id="profilecontainerimg"
				src="userImages/avatar_photo.png"
				data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
			<%} %>
		</div>
		<div class="box-footer" style="padding-top: 7% !important;">
			<div class="row">
				<div class="col-sm-12">
					<div class="description-block">
						<h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%>
						</h5>
						<%-- [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>] --%>
						<span class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%>
						</span>
						<%-- [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>] --%>
						<p class="description-text"><%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%>
						</p>
						<p class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></p>
						<%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
						<span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong>
						</span>
						<% } else { %>
						You don't have a reporting manager.
						<% } %>
					</div>
					<!-- /.description-block -->
				</div>
			</div> 
			<!-- /.row -->
		</div>
	</div>


	<div class="box box-body" style="padding: 5px; overflow-y: auto; max-height: 250px; background-color: rgb(229, 121, 38);">
		<div id="profilecontainer">
			<div class="content1" style="max-height: 300px; padding: 5px;">
				<a style="color: white; font-size: 14px; font-weight: bold;" href="OnBoardProcessing.action" target = "_blank" title="My Onboarding"><i class="fa fa-caret-right" aria-hidden="true"></i>My Onboarding</a>
				<!-- <a style="color: white; font-size: 14px; font-weight: bold;" href="javascript:void(0)" onclick="onBoardProcessingFun()" title="My Onboarding"><i class="fa fa-caret-right" aria-hidden="true"></i>My Onboarding</a> -->					
			</div>
		</div>
	</div>
		
	<div class="box box-warning">
		<div class="box-header with-border">
			<%
				List<String> alOfferNegotiationRequests = (List<String>) request.getAttribute("alOfferNegotiationRequests");
				if(alOfferNegotiationRequests==null) alOfferNegotiationRequests = new ArrayList<String>();
			
	             List<String> alLeaveRequest = (List<String>)request.getAttribute("alLeaveRequest");
	             if(alLeaveRequest == null) alLeaveRequest = new ArrayList<String>();
	             
	             List<String> alTravelRequest = (List<String>)request.getAttribute("alTravelRequest");
	             if(alTravelRequest == null) alTravelRequest = new ArrayList<String>();
	             
	             List<String> alReimbursementRequest = (List<String>)request.getAttribute("alReimbursementRequest");
	             if(alReimbursementRequest == null) alReimbursementRequest = new ArrayList<String>();
	             
	             List<String> alTimesheetRequests = (List<String>)request.getAttribute("alTimesheetRequests");
	             if(alTimesheetRequests == null) alTimesheetRequests = new ArrayList<String>();
	             
	             List<String> alPerkRequest = (List<String>)request.getAttribute("alPerkRequest");
	             if(alPerkRequest == null) alPerkRequest = new ArrayList<String>();
	             
	             List<String> alLeaveEncashRequest = (List<String>)request.getAttribute("alLeaveEncashRequest");
	             if(alLeaveEncashRequest == null) alLeaveEncashRequest = new ArrayList<String>();
	             
	             List<String> alLtaRequest = (List<String>)request.getAttribute("alLtaRequest");
	             if(alLtaRequest == null) alLtaRequest = new ArrayList<String>();
	             
	             List<String> alLoanRequest = (List<String>)request.getAttribute("alLoanRequest");
	             if(alLoanRequest == null) alLoanRequest = new ArrayList<String>();
	             
	             List<String> alResignRequest = (List<String>)request.getAttribute("alResignRequest");
	             if(alResignRequest == null) alResignRequest = new ArrayList<String>();
	             
	             List<String> alRequisitionRequest = (List<String>)request.getAttribute("alRequisitionRequest");
	             if(alRequisitionRequest == null) alRequisitionRequest = new ArrayList<String>();
	             
	             List<String> alSelfReviewRequest = (List<String>)request.getAttribute("alSelfReviewRequest");
	             if(alSelfReviewRequest == null) alSelfReviewRequest = new ArrayList<String>();
	             
	             List<String> alNewJobRequest = (List<String>)request.getAttribute("alNewJobRequest");
	             if(alNewJobRequest == null) alNewJobRequest = new ArrayList<String>();
	             
	             int totToDo = alLeaveRequest.size() + alTravelRequest.size() + alReimbursementRequest.size() + alTimesheetRequests.size() + alPerkRequest.size();
	             totToDo += alLeaveEncashRequest.size() + alLtaRequest.size() + alLoanRequest.size() + alResignRequest.size() + alRequisitionRequest.size();
	             totToDo += alSelfReviewRequest.size() + alNewJobRequest.size() + alOfferNegotiationRequests.size();
	             
	             //System.out.println("totToDo ===>> " + totToDo);
			%>
			<h3 class="box-title">To do (approvals)</h3>
			<div class="box-tools pull-right">
				<span data-toggle="tooltip" title="" class="badge bg-yellow"
					data-original-title="<%=totToDo %> Approvals"><%=totToDo %></span>
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div id="profilecontainer">
				<div class="content1" style="max-height: 300px; padding: 5px;">
					<%-- <ul class="todo-list ui-sortable">
                                <li>
                                  <span>Dattatray Jadhav, has submitted timesheet</span>
                                  <small class="label label-warning"><i class="fa fa-clock-o"></i>  17-Aug-15</small>
                                  <div class="tools">
                                    <i class="fa fa-eye"></i>
                                  </div>
                                </li>
                                </ul> --%>
					<% for(int i=0; alOfferNegotiationRequests != null && i< alOfferNegotiationRequests.size(); i++) { 
                             if(i>9) {
                             	break;
                             }
                             %>
					<div class="issues" style="float: left;"><%=alOfferNegotiationRequests.get(i) %></div>
					<% } %>
					<% for(int i=0; alLeaveRequest != null && i< alLeaveRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                } 
                                %>
					<div class="issues" style="float: left;"><%=alLeaveRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alTravelRequest != null && i< alTravelRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alTravelRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alReimbursementRequest != null && i< alReimbursementRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alReimbursementRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alPerkRequest != null && i< alPerkRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alPerkRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alLeaveEncashRequest != null && i< alLeaveEncashRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alLeaveEncashRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alLtaRequest != null && i< alLtaRequest.size(); i++) {
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alLtaRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alLoanRequest != null && i< alLoanRequest.size(); i++) {
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alLoanRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alResignRequest != null && i< alResignRequest.size(); i++) {
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alResignRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alRequisitionRequest != null && i< alRequisitionRequest.size(); i++) {
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alRequisitionRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alSelfReviewRequest != null && i< alSelfReviewRequest.size(); i++) {
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alSelfReviewRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alNewJobRequest != null && i< alNewJobRequest.size(); i++) {
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="float: left;"><%=alNewJobRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alTimesheetRequests != null && i< alTimesheetRequests.size(); i++) { %>
					<div class="issues" style="float: left;"><%=alTimesheetRequests.get(i) %></div>
					<% } %>
					<% if(totToDo == 0) { %>
					<div class="nodata msg">No to do (approvals).</div>
					<% } %>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	<div class="box box-success">
		<div class="box-header with-border">
			<h3 class="box-title">My Updates</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="widget-content nopadding updates" id="collapseG3"
				style="height: auto;">
				<%
                            String strThought = (String) request.getAttribute("DAY_THOUGHT_TEXT");
                            String strThoughtBy = (String) request.getAttribute("DAY_THOUGHT_BY");
                            if(strThought!=null) {
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-lightbulb-o"></i>
					<div class="update-done"><%=strThought %><span><strong>-
								<%=strThoughtBy %></strong>
						</span>
					</div>
					<%-- <div class="update-date"><span class="update-day">20</span>jan</div> --%>
				</div>
				<%} 
                            Map<String,List<String>> hmEventUpdates = (Map<String,List<String>>) request.getAttribute("eventUpdates");
                            Map<String,List<String>> hmQuoteUpdates = (Map<String,List<String>>) request.getAttribute("quoteUpdates");
                            Map<String,List<String>> hmNoticeUpdates = (Map<String,List<String>>) request.getAttribute("noticeUpdates");
                            
                            List<String> holidayList = (List<String>) request.getAttribute("holidays");
                            if(holidayList == null ) holidayList = new ArrayList<String>();
                            if(hmEventUpdates == null){
                            	hmEventUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            
                            if(hmQuoteUpdates == null){
                            	hmQuoteUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            if(hmNoticeUpdates == null){
                            	hmNoticeUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            
                            if(hmQuoteUpdates != null && hmQuoteUpdates.size()>0){
                            	Set<String> quoteSet = hmQuoteUpdates.keySet();
                            	Iterator<String> qit = quoteSet.iterator();
                            	while(qit.hasNext()){
                            		String quoteId = qit.next();
                            		List<String> quoteList  = hmQuoteUpdates.get(quoteId);  
                            		if(quoteList == null ) quoteList = new ArrayList<String>();
                            		if(quoteList != null && quoteList.size()>0){
                            			
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-lightbulb-o"></i>
					<div class="update-done"><%=quoteList.get(1) %><span><%=quoteList.get(2) %></span>
					</div>
					<div class="update-date">
						<span class="update-day"><%=quoteList.get(4) %></span><%=quoteList.get(5) %></div>
				</div>
				<%
                            }
                            }
                            }
                            %>
				<%
                            String strResignationStatus = (String) request.getAttribute("RESIG_STATUS");
                            String nRemaining = (String) request.getAttribute("RESIGNATION_REMAINING");
                            String strResignationStatusD = (String) request.getAttribute("RESIGNATION_STATUS_D");
                            String strRADay = (String) request.getAttribute("strRADay");
                            String strRAMonth = (String) request.getAttribute("strRAMonth");
                            //if(strResignationStatus!=null) {
                            if(strResignationStatusD!=null) {
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-bell-o"></i>
					<div class="update-done"><%=strResignationStatusD %>
						<%if(uF.parseToInt(strResignationStatus) == 1) { %>
						<span><%=uF.showData(nRemaining+"", "0") %></span>
						<% } %>
					</div>
					<div class="update-date">
						<span class="update-day"><%=strRADay %></span><%=strRAMonth %></div>
				</div>
				<% } %>
				<%
                            String nMailCount = (String)request.getAttribute("MAIL_COUNT");
                            if(uF.parseToInt(nMailCount)>0) { %>
				<div class="new-update clearfix">
					<i class="fa fa-envelope-o"></i>
					<div class="update-done">
						You have <a href="MyMail.action" title="My Mail"><strong><%=nMailCount %>
								new</strong>
						</a> mails.
					</div>
					<%-- <div class="update-date"><span class="update-day">20</span>jan</div> --%>
				</div>
				<%-- <p class="mail">You have <a href="MyMail.action" title="My Mail"><strong><%=nMailCount %> new</strong></a> mails.</p> --%>
				<% } %>
				<%
                            List<List<String>> alBirthDays = (List<List<String>>)request.getAttribute("alBirthDays");
                            
                            for(int i=0; alBirthDays!=null && i<alBirthDays.size(); i++) { 
                            	List<String> innerList = alBirthDays.get(i);
                            	if(innerList.size()>0) {
                            %>
				<%-- <div class="repeat_row" style="width: 90%;"><%=(String)alBirthDays.get(i)%></div> --%>
				<div class="new-update clearfix">
					<i class="fa fa-birthday-cake"></i>
					<div class="update-done"><%=innerList.get(0)%></div>
					<div class="update-date">
						<span class="update-day"><%=innerList.get(1)%></span><%=innerList.get(2)%></div>
				</div>
				<% } %>
				<% } %>
		<!-- ===start parvez date: 28-10-2022=== -->		
				<%
                   List<List<String>> alWorkAnniversary = (List<List<String>>)request.getAttribute("alWorkAnniversary");
                            
                   for(int i=0; alWorkAnniversary!=null && i<alWorkAnniversary.size(); i++) { 
                       List<String> innerList = alWorkAnniversary.get(i);
                       if(innerList.size()>0) {
                %>
					<div class="new-update clearfix">
						<i class="fa fa-birthday-cake"></i>
						<div class="update-done"><%=innerList.get(0)%></div>
						<div class="update-date">
							<span class="update-day"><%=innerList.get(1)%></span><%=innerList.get(2)%></div>
					</div>
					<% } %>
				<% } %>
				<%
                            if(hmNoticeUpdates != null && hmNoticeUpdates.size()>0) {	
                            	Set<String> noticeSet = hmNoticeUpdates.keySet();
                            	Iterator<String> nit = noticeSet.iterator();
                            	while(nit.hasNext()){
                            		String noticeId = nit.next();
                            		List<String> noticeList  = hmNoticeUpdates.get(noticeId);  
                            		if(noticeList == null ) noticeList = new ArrayList<String>();
                            		if(noticeList != null && noticeList.size()>0) {
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-bullhorn"></i>
					<div class="update-done"><%=noticeList.get(2) %>
						<span><a href="<%=noticeList.get(0) %>"> <!-- <img title="Go to Announcements.." src="images1/icons/icons/forward_icon.png"> -->
								<i class="fa fa-forward" aria-hidden="true"
								title="Go to Announcements.."></i> </a>
						</span>
					</div>
					<div class="update-date">
						<span class="update-day"><%=noticeList.get(8) %></span><%=noticeList.get(9) %></div>
				</div>
				<%
                            }
                            }
                            }
                            %>
				<%
                            if(hmEventUpdates != null && hmEventUpdates.size()>0){	
                            	Set<String> eventSet = hmEventUpdates.keySet();
                            	Iterator<String> eit = eventSet.iterator();
                            	while(eit.hasNext()){
                            		String eventId = eit.next();
                            		List<String> eventList  = hmEventUpdates.get(eventId);  
                            		if(eventList == null ) eventList = new ArrayList<String>();
                            		if(eventList != null && eventList.size()>0){
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-calendar-o"></i>
					<div class="update-done">
						<a href="<%=eventList.get(0) %>"><strong><%=eventList.get(2) %></strong>
						</a> organised at
						<%=eventList.get(6) %>
						from
						<%=eventList.get(4)%>
						to
						<%=eventList.get(5)%>
					</div>
				</div>
				<%
                            }
                            }
                            }
                            if(holidayList != null && holidayList.size()>0){
                            Iterator hit  = holidayList.iterator();
                            while(hit.hasNext()){
                            	String holidayData = (String) hit.next();	
                            
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-bell-o"></i>
					<div class="update-done"><%=holidayData%></div>
					<%-- <div class="update-date"><span class="update-day">14</span>Feb</div> --%>
				</div>
				<%
                            }
                            }
                            %>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	</section>
	<section class="col-lg-5 connectedSortable" style="padding-left: 0px;">
	<%
	//isClockOnOffBlockShow && 
	if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0 && CF.isClockOnOff()) { %>
	<div class="box box-primary">
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; background-color: #2A3B3F; background-image: url(images1/clockon_bg.png); background-position: right top; background-repeat: no-repeat;">
			<div id="involmentcontainer" style="padding: 10px;">
				<div id="clockONOFF" style="color: #fff;"></div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	<% } %>

	<div class="box box-info">
		<% //if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
                    String totalHubCount = (String) request.getAttribute("totalHubCount");
                    String feedCount = (String) request.getAttribute("feedCount");
                    String announcementCount = (String) request.getAttribute("announcementCount");
                    String eventCount = (String) request.getAttribute("eventCount");
                    String quoteCount = (String) request.getAttribute("quoteCount");
                    %>
		<div class="box-header with-border">
			<h3 class="box-title">My Hub</h3>
			<div class="box-tools pull-right">
				<span data-toggle="tooltip" title="" class="badge bg-blue"><%=totalHubCount %></span>
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek" style="margin-top: 10px;">
				<div class="content1">
					<div class="holder">
						<ul class="site-stats">
							<a href="Hub.action?type=F">
								<li class="bg_lh"><i class="fa fa-rss"></i> <strong><%=feedCount %></strong>
									<small>Unread Feed</small>
							</li> </a>
							<a href="Hub.action?type=A">
								<li class="bg_lh"><i class="fa fa-microphone"></i> <strong><%=announcementCount %></strong>
									<small>Announcements</small>
							</li> </a>
							<a href="Hub.action?type=E">
								<li class="bg_lh"><i class="fa fa-calendar-o"></i> <strong><%=eventCount %></strong>
									<small>Events</small>
							</li> </a>
							<a href="Hub.action?type=Q">
								<li class="bg_lh"><i class="fa fa-quote-right"></i> <strong><%=quoteCount %></strong>
									<small>Quotes</small>
							</li> </a>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	<div class="box box-danger">
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { 
                    Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
                          	if(hmEmp==null) hmEmp=new HashMap<String,String>();
                    	Map<String,String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
                    	if(empImageMap==null) empImageMap=new HashMap<String,String>();
                    %>
		<div class="box-header with-border">
			<h3 class="box-title">My Team</h3>
			<div class="box-tools pull-right">
				<span class="label label-danger"><%=hmEmp.size() %> Members</span>
				<button type="button" class="btn btn-box-tool"
					data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button type="button" class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body no-padding"
			style="max-height: 350px; overflow-y: auto;">
			<ul class="users-list clearfix">
				<%
                            Iterator<String> it=hmEmp.keySet().iterator();
                            int i=0;
                            while(it.hasNext()) {
                            	i++;
                            	String empId=it.next();
                            	String empName=hmEmp.get(empId);
                            	if(i > 24) {
                            break;
                            }
                            %>
				<li><a href="javascript:void(0);"
					onclick="getEmpProfile('<%=empId %>');" title="<%=empName %>">
						<%if(docRetriveLocation==null) { %> <img class="lazy img-circle"
						src="userImages/avatar_photo.png"
						data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(empId.trim())%>">
						<% } else { %> <img class="lazy img-circle"
						src="userImages/avatar_photo.png"
						data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId.trim()+"/"+IConstants.I_60x60+"/"+empImageMap.get(empId.trim())%>" />
						<% } %> <span class="users-list-name"><%=empName %></span> </a></li>
				<%  } %>
				<% if(hmEmp == null || hmEmp.isEmpty()) { %>
				<div class="content1" style="max-height: 300px; padding: 5px;">
					<div class="nodata msg"> No team available.</div>
				</div>
				<% } %>
			</ul>
			<!-- /.users-list -->
		</div>
		<!-- /.box-body -->
		<%if(hmEmp != null && hmEmp.size() > 24) { %>
		<div class="box-footer text-center">
			<a href="javascript:void(0);" onclick="getTeamMembers();"
				class="uppercase">View All Users</a>
		</div>
		<%} %>
		<!-- /.box-footer -->
	</div>
	</section>
	<section class="col-lg-3 connectedSortable" style="padding-left: 0px;">


	<% if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ITR_FILLING_REQUEST_BLOCK))) { %>
	<!--************start of code for mail link ************ -->
	<div class="box box-primary">
		<div class="box-header with-border" style="background-color: #3c8dbc;">
			<h3 class="box-title"
				style="color: #ff5900; font-size: 18px !important; font-weight: 900;">
				<marquee>File Your Income Tax Return</marquee>
			</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek" style="width: 100%;">
				<div class="content1">
					<div class="holder" style="padding: 0px;">
						<% String strDays = uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone())+"", IConstants.DBDATE, "2019-08-31", IConstants.DBDATE); %>
						<div style="margin: 5px 0px;">
							Due date to file ITR is 31st Aug 2019 (<%=uF.showData(strDays, "0") %>
							days remaining). File your return to avoid interest (1% per
							month), late fee and penalty of INR 5,000/-
						</div>
						<div class="col-md-12 col_no_padding" style="margin: 5px 0px;">
							<a href="javascript:void(0)" onclick="sendMail()"><b>
									&nbsp;&nbsp;Apply for Your Tax Return</b>
							</a>
						</div>
						<div class="col-md-12 col_no_padding" style="margin: 5px 0px;">
							<div id="msgDiv"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!--************end of code for mail link ************ --> <% } %>

	<div class="box box-primary">
		<% List<List<String>> alJobList = (List<List<String>>) request.getAttribute("alJobList"); %>
		<div class="box-header with-border">
			<h3 class="box-title">Live Jobs</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek">
				<div class="content1">
					<div class="holder">
						<div style="width: 100%; float: left;">
							<% for(i=0; alJobList !=null && i<alJobList.size(); i++) { 
                                    	String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789" + "abcdefghijklmnopqrstuvxyz";
                        				int n=25;
                        				StringBuilder sb = new StringBuilder(n); 
                        		        for (int a=0; a<n; a++) {
                        		            int index = (int)(AlphaNumericString.length() * Math.random()); 
                        		            sb.append(AlphaNumericString.charAt(index)); 
                        		        }
                        		        List<String> alInner = alJobList.get(i);
                                    %>
							<div style="margin: 5px 0px;">
								<a
									href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alInner.get(0) %>&refEmpId=<%=sessionEmpId %>"><%=uF.showData(alInner.get(2), "") %></a>
								<span
									style="float: right; font-weight: bold; padding-right: 10px;"><%=uF.showData(alInner.get(3), "") %></span>
								<!-- <a style="color: #ff8826;" href="Login.action?role=3&product=3&userscreen=CEODashboard"><i class="fa fa-caret-right" aria-hidden="true"></i><u>CEO Dashboard (Project)</u></a> -->
							</div>
							<% } %>
							
							<% if(alJobList==null || alJobList.size()==0) { %>
								<div class="nodata msg"> No live Jobs. </div>
							<% } %>
							
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>

	<div class="box box-primary">
		<% 
                    String BASEUSERTYPE = (String)session.getAttribute(IConstants.BASEUSERTYPE);
                    String poFlag = (String)request.getAttribute("poFlag"); 
                    %>
		<div class="box-header with-border">
			<h3 class="box-title">Quick Links</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek">
				<div class="content1">
					<div class="holder">
						<div style="width: 100%; float: left;">
							<% if(CF.isTaskRig() && BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.CEO)) { %>
							<div style="margin: 5px 0px;">
								<a style="color: #ff8826;"
									href="Login.action?role=3&product=3&userscreen=CEODashboard"><i class="fa fa-caret-right" aria-hidden="true"></i><u>CEO Dashboard (Project)</u>
								</a>
							</div>
							<% } %>
							<% if(CF.isWorkRig() && BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.CEO)) { %>
							<div style="margin: 5px 0px;">
								<a style="color: #ff8826;" href="Login.action?role=2&product=2"><i class="fa fa-caret-right" aria-hidden="true"></i><u>CEO Dashboard (HR)</u>
								</a>
							</div>
							<% } %>
							<% if(CF.isWorkRig()) { %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="MyTime.action?callFrom=MyDashLeaveSummary"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Apply Leave</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="MyPay.action?callFrom=MyDashReimbursements"><i class="fa fa-caret-right" aria-hidden="true"></i>Apply Reimbursement</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="MyPay.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Check Payroll</a>
							</div>
							<% } %>
							<div style="margin: 5px 0px;">
								<a href="MyDashboard.action"><i class="fa fa-caret-right" aria-hidden="true"></i>My Dashboard</a>
							</div>
							<div style="margin: 5px 0px;">
								<a href="MyProfile.action"><i class="fa fa-caret-right" aria-hidden="true"></i>My Profile</a>
							</div>
							<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_HOME_MY_TEAM)) && hmFeatureUserTypeId.get(IConstants.F_MY_HOME_MY_TEAM).contains(strUsertypeId)) { %>
							<div style="margin: 5px 0px;">
								<a href="OrganisationalChart.action"><i class="fa fa-caret-right" aria-hidden="true"></i>My Team</a>
							</div>
							<% } %>
							<!--code onboard_processor  -->


							<%--                                     <a href="javascript:void(0)" onclick="applyEncashments('<%=(String)request.getAttribute("strPaycycle") %>', '<%=(String)request.getAttribute("policy_id") %>', '<%=(String)request.getAttribute("pageType") %>')" title="Apply Encashment"><i class="fa fa-plus-circle" aria-hidden="true"></i> Apply Encashment</a>
 --%>
							<!-- <div class="col-md-12 col_no_padding" style="margin: 5px 0px;">
								<a href="javascript:void(0)" onclick="onBoardProcessingFun()" title="My Onboarding"><i class="fa fa-caret-right" aria-hidden="true"></i>My Onboarding</a>
							</div> -->

							<!-- end of onboard_processor  -->
							<% } %>
							<% if(CF.isTaskRig()) { %>
							<% if(uF.parseToBoolean(poFlag) || BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.MANAGER)) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=myProjects"><i class="fa fa-caret-right" aria-hidden="true"></i>Go to Projects</a>
							</div>
							<% } else if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN))) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=allProjects"><i class="fa fa-caret-right" aria-hidden="true"></i>Go to Projects</a>
							</div>
							<% } %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=myTimesheet"><i class="fa fa-caret-right" aria-hidden="true"></i>Update Timesheets</a>
							</div>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3"><i class="fa fa-caret-right" aria-hidden="true"></i>My Project Dashboard</a>
							</div>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=myWorkTasks"><i class="fa fa-caret-right" aria-hidden="true"></i>My Work</a>
							</div>
								
							<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ADD_BUDGET_LINK_UNABLE)) && hmFeatureUserTypeId.get(IConstants.F_ADD_BUDGET_LINK_UNABLE).contains(strUsertypeId)){ %>
								<div style="margin: 5px 0px;">
									<a href="AddPartnerwiseBudget.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Update Partner Budget</a>
								</div>
							<% } %>
						
							<% if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN) || BASEUSERTYPE.equals(IConstants.ACCOUNTANT))) { %>
								<div style="margin: 5px 0px;">
									<a href="AddInvoicingDetails.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Add Invoicing Details</a>
								</div>
								<div style="margin: 5px 0px;">
									<a href="AddReceiptDetails.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Add Receipt Details</a>
								</div>
							<% } %>
					<!-- ===start parvez date: 09-12-2022=== -->		
							<div style="margin: 5px 0px;">
								<a href="EmailDashboard.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Emails</a>
							</div>
					<!-- ===end parvez date: 09-12-2022=== -->		
							<% }
							} %>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	<% 
                String BASEUSERTYPE = (String)session.getAttribute(IConstants.BASEUSERTYPE);
                if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN))) { %>
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">If I am an HR</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek" style="width: 100%;">
				<div class="content1">
					<div class="holder">
						<div style="width: 100%; float: left;">
							<% if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN))) { %>
							<% if(CF.isWorkRig()) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7"><i class="fa fa-caret-right" aria-hidden="true"></i>Go to HR's Dashboard</a>
							</div>
							<% if(BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=1"><i class="fa fa-caret-right" aria-hidden="true"></i>Go to Admin</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=approvePay"><i class="fa fa-caret-right" aria-hidden="true"></i>Approve Pay</a>
							</div>
							<% } %>
							<%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=onboardCandidate"><i class="fa fa-caret-right" aria-hidden="true"></i>On-Board Candidate</a>
							</div>
							<% } %> --%>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=finalizeReviews"><i class="fa fa-caret-right" aria-hidden="true"></i>Finalize Reviews</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=planLearningGaps"><i class="fa fa-caret-right" aria-hidden="true"></i>Plan Learning Gaps</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=updateStatutoryCompliance"><i class="fa fa-caret-right" aria-hidden="true"></i>Update Statutory Compliance</a>
							</div>
							<% } %>
							<% } %>
							<% if(CF.isTaskRig()) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=generateBills"><i class="fa fa-caret-right" aria-hidden="true"></i>Generate Bills</a>
							</div>
							
					<!-- ===start parvez date: 30-03-2022=== -->
							<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ENABLE_MIS_REPORT_QUICK_LINK))){ %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=reportMIS"><i class="fa fa-caret-right" aria-hidden="true"></i>MIS Report</a>
							</div>
							<% } %>
					<!-- ===end parvez date: 30-03-2022=== -->
							
							<% } %>
							
							<% } else { %>
							<div class="nodata msg">You are not a HR.</div>
							<% } %>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	<% } %>

	<div class="box box-info">
		<%List<String> event = (List<String>)request.getAttribute("alInner"); %>
		<div class="box-header with-border">
			<h3 class="box-title">Upcoming Event</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek" style="width: 100%;">
				<div class="content1">
					<div class="holder" style="padding: 0px;">
						<% if(event != null && !event.isEmpty() ) { %>
						<div id="mainEventDiv">
							<div style="float: left; width: 100%; padding: 5px 5px;">
								<div id="eventDataDiv" style="float: left; width: 100%;">
									<div style="float: left; width: 100%;">
										<div style="float: left; width: 94%; margin: 7px;">
											<% if(event.get(9) != null && !event.get(9).equals("")) { %>
												<a href="javascript:void(0)" onclick="openEventPopup(<%=event.get(0)%>)"><%=event.get(12)%></a>
											<% } else { %>
												<a href="javascript:void(0)" onclick="openEventPopup(<%=event.get(0)%>)">
												<div style="float: left; padding: 30px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Image Preview not available</div> </a>
											<% } %>
										</div>
									</div>
									<div style="float: left; width: 95%; margin: 3px 7px 3px 7px;">
										<div style="float: left; width: 100%;">
											<div style="float: left; width: 100%; font-size: 14px; font-weight: bold; color: #00688B; margin-top: 10px; font-style:italic; margin-left:7px;">
												<a href="javascript:void(0)" onclick="openEventPopup(<%=event.get(0)%>)"><%=event.get(4)%></a>
											</div>
										</div>
										<div style="float: left; width: 100%; margin-bottom: -4px;">
											<div id="blueLikeDiv_<%=event.get(0) %>" style="display: block; font-size: 12px; float: left; color: gray; width: 90%; margin-right: 5px">
												<div style="float: left; margin-left: 7px; margin-top: -2px;">
													Organised at
													<%=event.get(7) %>
													from
													<%=event.get(1) %>
													to
													<%=event.get(2) %></div>
											</div>
											<div id="grayLikeDiv_<%=event.get(0) %>"
												style="display: block; font-size: 12px; width: 90%; color: gray; float: left; margin-right: 5px;">
												<div style="float: left; margin-left: 7px; margin-top: -2px;">
													Timing:
													<%=event.get(14)%>
													To
													<%=event.get(15)%></div>
											</div>
										</div>
										<div style="float: left; width: 92%; margin-left: 15px;">
											<div class="eventExpandDiv">
												<p style="font-size: 11px; padding-top: 5px;"><%=event.get(5) %></p>
											</div>
										</div>
										<div style="float: left; width: 96%; font-style: italic; font-size: 11px; margin-left: 7px; color: gray;">
											Posted on:
											<%=event.get(3) %>.
										</div>
										<%-- <div style="float: left; width:96%;margin-left: 5px;margin-top:-7px;font-style:italic;font-size:11px;color:gray;">Shared with: <%=event.get(8) %> .</div> --%>
									</div>
								</div>
							</div>
						</div>
						<% } else { %>
						<div id="mainEventDiv">
							<div style="float: left; width: 100%;">
								<img src="images1/no-events.jpg" style="width: 100%;">
							</div>
						</div>
						<% } %>
						<div class="clr"></div>
					</div>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
		<div class="box-footer text-center">
			<a href="Hub.action?type=E">View All Events</a>
		</div>
	</div>
	</section>
</div>
</section>
<% } else { %>

<%=uF.showData(((String)session.getAttribute(IConstants.MESSAGE)), "") %>
<% session.removeAttribute(IConstants.MESSAGE); %>
<section class="content">
<div class="row jscroll">
	<section class="col-lg-4 connectedSortable"> 

	<div class="box box-widget widget-user">
		<!-- Add the bg color to the header using any of the bg-* classes -->
		<div class="widget-user-header bg-aqua-active"
			style="padding-top: 10px;">
			<h3 class="widget-user-username"
				style="color: #fff; font-weight: 600; margin-top: 0px;">
				<span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>
				<span style="float: right;"><a href="MyProfile.action"
					title="Go to My FactSheet.."><i class="fa fa-address-card-o"
						style="color: #fff;"></i>
				</a>
				</span>
			</h3>
			<h5 class="widget-user-desc" style="margin-bottom: 10px;"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
		</div>
		<div class="widget-user-image">
			<%if(docRetriveLocation==null) { %>
			<img class="img-circle lazy" id="profilecontainerimg"
				src="userImages/avatar_photo.png"
				data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
			<%} else { %>
			<img class="img-circle lazy" id="profilecontainerimg"
				src="userImages/avatar_photo.png"
				data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
			<%} %>
		</div>
		<div class="box-footer">
			<div class="row">
				<div class="col-sm-12">
					<div class="description-block">
						<h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%>
						</h5>
						<%-- [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>] --%>
						<span class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%>
						</span>
						<%-- [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>] --%>
						<p class="description-text"><%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%>
						</p>
						<p class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></p>
						<%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
						<span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong>
						</span>
						<% } else { %>
						You don't have a reporting manager.
						<% } %>
					</div>
					<!-- /.description-block -->
				</div>
			</div>
			<!-- /.row -->
		</div>
	</div>
	<div class="box box-warning">
		<div class="box-header with-border">
			<%
                        List<String> alLeaveRequest = (List<String>)request.getAttribute("alLeaveRequest");
                        if(alLeaveRequest == null) alLeaveRequest = new ArrayList<String>();
                        
                        List<String> alTravelRequest = (List<String>)request.getAttribute("alTravelRequest");
                        if(alTravelRequest == null) alTravelRequest = new ArrayList<String>();
                        
                        List<String> alReimbursementRequest = (List<String>)request.getAttribute("alReimbursementRequest");
                        if(alReimbursementRequest == null) alReimbursementRequest = new ArrayList<String>();
                        
                        List<String> alTimesheetRequests = (List<String>)request.getAttribute("alTimesheetRequests");
                        if(alTimesheetRequests == null) alTimesheetRequests = new ArrayList<String>();
                        
                        List<String> alPerkRequest = (List<String>)request.getAttribute("alPerkRequest");
                        if(alPerkRequest == null) alPerkRequest = new ArrayList<String>();
                        
                        List<String> alLeaveEncashRequest = (List<String>)request.getAttribute("alLeaveEncashRequest");
                        if(alLeaveEncashRequest == null) alLeaveEncashRequest = new ArrayList<String>();
                        
                        List<String> alLtaRequest = (List<String>)request.getAttribute("alLtaRequest");
                        if(alLtaRequest == null) alLtaRequest = new ArrayList<String>();
                        
                        List<String> alLoanRequest = (List<String>)request.getAttribute("alLoanRequest");
                        if(alLoanRequest == null) alLoanRequest = new ArrayList<String>();
                        
                        List<String> alResignRequest = (List<String>)request.getAttribute("alResignRequest");
                        if(alResignRequest == null) alResignRequest = new ArrayList<String>();
                        
                        List<String> alRequisitionRequest = (List<String>)request.getAttribute("alRequisitionRequest");
                        if(alRequisitionRequest == null) alRequisitionRequest = new ArrayList<String>();
                        
                        List<String> alSelfReviewRequest = (List<String>)request.getAttribute("alSelfReviewRequest");
                        if(alSelfReviewRequest == null) alSelfReviewRequest = new ArrayList<String>();
                        
                        List<String> alNewJobRequest = (List<String>)request.getAttribute("alNewJobRequest");
                        if(alNewJobRequest == null) alNewJobRequest = new ArrayList<String>();
                        
                        int totToDo = alLeaveRequest.size() + alTravelRequest.size() + alReimbursementRequest.size() + alTimesheetRequests.size() + alPerkRequest.size();
                        totToDo += alLeaveEncashRequest.size() + alLtaRequest.size() + alLoanRequest.size() + alResignRequest.size() + alRequisitionRequest.size();
                        totToDo += alSelfReviewRequest.size() + alNewJobRequest.size();
                        %>
			<h3 class="box-title">To do (approvals)</h3>
			<div class="box-tools pull-right">
				<span data-toggle="tooltip" title="" class="badge bg-yellow"
					data-original-title="<%=totToDo %> Approvals"><%=totToDo %></span>
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div id="profilecontainer">
				<div class="content1" style="max-height: 300px; padding: 5px;">
					<%-- <ul class="todo-list ui-sortable">
                                <li>
                                  <span>Dattatray Jadhav, has submitted timesheet</span>
                                  <small class="label label-warning"><i class="fa fa-clock-o"></i>  17-Aug-15</small>
                                  <div class="tools">
                                    <i class="fa fa-eye"></i>
                                  </div>
                                </li>
                                </ul> --%>
					<% for(int i=0; alLeaveRequest != null && i< alLeaveRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                } 
                                %>
					<div class="issues" style="width: 94%;"><%=alLeaveRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alTravelRequest != null && i< alTravelRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alTravelRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alReimbursementRequest != null && i< alReimbursementRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alReimbursementRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alPerkRequest != null && i< alPerkRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alPerkRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alLeaveEncashRequest != null && i< alLeaveEncashRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alLeaveEncashRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alLtaRequest != null && i< alLtaRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alLtaRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alLoanRequest != null && i< alLoanRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alLoanRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alResignRequest != null && i< alResignRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alResignRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alRequisitionRequest != null && i< alRequisitionRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alRequisitionRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alSelfReviewRequest != null && i< alSelfReviewRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alSelfReviewRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alNewJobRequest != null && i< alNewJobRequest.size(); i++) { 
                                if(i>9) {
                                	break;
                                }
                                %>
					<div class="issues" style="width: 94%;"><%=alNewJobRequest.get(i) %></div>
					<% } %>
					<% for(int i=0; alTimesheetRequests != null && i< alTimesheetRequests.size(); i++) { %>
					<div class="issues" style="width: 94%;"><%=alTimesheetRequests.get(i) %></div>
					<% } %>
					<% if(totToDo == 0) { %>
					<div class="nodata msg">No to do (approvals).</div>
					<% } %>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	</section>

	<section class="col-lg-5 connectedSortable" style="padding-left: 0px;">
	
	<% //isClockOnOffBlockShow && 
	if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0 && CF.isClockOnOff()) { %>
	<div class="box box-primary">
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; background-color: #2A3B3F; background-image: url(images1/clockon_bg.png); background-position: right top; background-repeat: no-repeat;">
			<div id="involmentcontainer" style="padding: 10px;">
				<div id="clockONOFF" style="color: #fff;"></div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	<% } %>
	
	<div class="box box-info">
		<% //if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
                    String totalHubCount = (String) request.getAttribute("totalHubCount");
                    String feedCount = (String) request.getAttribute("feedCount");
                    String announcementCount = (String) request.getAttribute("announcementCount");
                    String eventCount = (String) request.getAttribute("eventCount");
                    String quoteCount = (String) request.getAttribute("quoteCount");
                    %>
		<div class="box-header with-border">
			<h3 class="box-title">My Hub</h3>
			<div class="box-tools pull-right">
				<span data-toggle="tooltip" title="" class="badge bg-blue"><%=totalHubCount %></span>
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek" style="margin-top: 10px;">
				<div class="content1">
					<div class="holder">
						<ul class="site-stats">
							<a href="Hub.action?type=F">
								<li class="bg_lh"><i class="fa fa-rss"></i> <strong><%=feedCount %></strong>
									<small>Unread Feed</small>
							</li> </a>
							<a href="Hub.action?type=A">
								<li class="bg_lh"><i class="fa fa-microphone"></i> <strong><%=announcementCount %></strong>
									<small>Announcements</small>
							</li> </a>
							<a href="Hub.action?type=E">
								<li class="bg_lh"><i class="fa fa-calendar-o"></i> <strong><%=eventCount %></strong>
									<small>Events</small>
							</li> </a>
							<a href="Hub.action?type=Q">
								<li class="bg_lh"><i class="fa fa-quote-right"></i> <strong><%=quoteCount %></strong>
									<small>Quotes</small>
							</li> </a>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	<div class="box box-success">
		<div class="box-header with-border">
			<h3 class="box-title">My Updates</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="widget-content nopadding updates" id="collapseG3"
				style="height: auto;">
				<%
                            String strThought = (String) request.getAttribute("DAY_THOUGHT_TEXT");
                            String strThoughtBy = (String) request.getAttribute("DAY_THOUGHT_BY");
                            if(strThought!=null) {
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-lightbulb-o"></i>
					<div class="update-done"><%=strThought %><span><strong>-
								<%=strThoughtBy %></strong>
						</span>
					</div>
					<%-- <div class="update-date"><span class="update-day">20</span>jan</div> --%>
				</div>
				<%} 
                            Map<String,List<String>> hmEventUpdates = (Map<String,List<String>>) request.getAttribute("eventUpdates");
                            Map<String,List<String>> hmQuoteUpdates = (Map<String,List<String>>) request.getAttribute("quoteUpdates");
                            Map<String,List<String>> hmNoticeUpdates = (Map<String,List<String>>) request.getAttribute("noticeUpdates");
                            List<String> holidayList = (List<String>) request.getAttribute("holidays");
                            if(holidayList == null ) holidayList = new ArrayList<String>();
                            if(hmEventUpdates == null){
                            	hmEventUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            
                            if(hmQuoteUpdates == null){
                            	hmQuoteUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            if(hmNoticeUpdates == null){
                            	hmNoticeUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            
                            if(hmQuoteUpdates != null && hmQuoteUpdates.size()>0){
                            	Set<String> quoteSet = hmQuoteUpdates.keySet();
                            	Iterator<String> qit = quoteSet.iterator();
                            	while(qit.hasNext()){
                            		String quoteId = qit.next();
                            		List<String> quoteList  = hmQuoteUpdates.get(quoteId);  
                            		if(quoteList == null ) quoteList = new ArrayList<String>();
                            		if(quoteList != null && quoteList.size()>0){
                            			
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-lightbulb-o"></i>
					<div class="update-done"><%=quoteList.get(1) %><span><%=quoteList.get(2) %></span>
					</div>
					<div class="update-date">
						<span class="update-day"><%=quoteList.get(4) %></span><%=quoteList.get(5) %></div>
				</div>
				<%
                            }
                            }
                            }
                            %>
				<%
                            String strResignationStatus = (String) request.getAttribute("RESIG_STATUS");
                            String nRemaining = (String) request.getAttribute("RESIGNATION_REMAINING");
                            String strResignationStatusD = (String) request.getAttribute("RESIGNATION_STATUS_D");
                            String strRADay = (String) request.getAttribute("strRADay");
                            String strRAMonth = (String) request.getAttribute("strRAMonth");
                            //if(strResignationStatus!=null) {
                            if(strResignationStatusD!=null) {
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-bell-o"></i>
					<div class="update-done"><%=strResignationStatusD %>
						<%if(uF.parseToInt(strResignationStatus) == 1) { %>
						<span><%=uF.showData(nRemaining+"", "0") %></span>
						<% } %>
					</div>
					<div class="update-date">
						<span class="update-day"><%=strRADay %></span><%=strRAMonth %></div>
				</div>
				<% } %>
				<%
                            String nMailCount = (String)request.getAttribute("MAIL_COUNT");
                            if(uF.parseToInt(nMailCount)>0) { %>
				<div class="new-update clearfix">
					<i class="fa fa-envelope-o"></i>
					<div class="update-done">
						You have <a href="MyMail.action" title="My Mail"><strong><%=nMailCount %>
								new</strong>
						</a> mails.
					</div>
					<%-- <div class="update-date"><span class="update-day">20</span>jan</div> --%>
				</div>
				<%-- <p class="mail">You have <a href="MyMail.action" title="My Mail"><strong><%=nMailCount %> new</strong></a> mails.</p> --%>
				<% } %>
				<%
                            List<List<String>> alBirthDays = (List<List<String>>)request.getAttribute("alBirthDays");
                            
                            for(int i=0; alBirthDays!=null && i<alBirthDays.size(); i++) { 
                            	List<String> innerList = alBirthDays.get(i);
                            	if(innerList.size()>0) {
                            %>
				<%-- <div class="repeat_row" style="width: 90%;"><%=(String)alBirthDays.get(i)%></div> --%>
				<div class="new-update clearfix">
					<i class="fa fa-birthday-cake"></i>
					<div class="update-done"><%=innerList.get(0)%></div>
					<div class="update-date">
						<span class="update-day"><%=innerList.get(1)%></span><%=innerList.get(2)%></div>
				</div>
				<% } %>
				<% } %>
				<%
                            if(hmNoticeUpdates != null && hmNoticeUpdates.size()>0) {	
                            	Set<String> noticeSet = hmNoticeUpdates.keySet();
                            	Iterator<String> nit = noticeSet.iterator();
                            	while(nit.hasNext()){
                            		String noticeId = nit.next();
                            		List<String> noticeList  = hmNoticeUpdates.get(noticeId);  
                            		if(noticeList == null ) noticeList = new ArrayList<String>();
                            		if(noticeList != null && noticeList.size()>0) {
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-bullhorn"></i>
					<div class="update-done"><%=noticeList.get(2) %>
						<span><a href="<%=noticeList.get(0) %>"> <!-- <img title="Go to Announcements.." src="images1/icons/icons/forward_icon.png"> -->
								<i class="fa fa-forward" aria-hidden="true"
								title="Go to Announcements.."></i> </a> </span>
					</div>
					<div class="update-date">
						<span class="update-day"><%=noticeList.get(8) %></span><%=noticeList.get(9) %></div>
				</div>
				<%
                            }
                            }
                            }
                            %>
				<%
                            if(hmEventUpdates != null && hmEventUpdates.size()>0){	
                            	Set<String> eventSet = hmEventUpdates.keySet();
                            	Iterator<String> eit = eventSet.iterator();
                            	while(eit.hasNext()){
                            		String eventId = eit.next();
                            		List<String> eventList  = hmEventUpdates.get(eventId);  
                            		if(eventList == null ) eventList = new ArrayList<String>();
                            		if(eventList != null && eventList.size()>0){
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-calendar-o"></i>
					<div class="update-done">
						<a href="<%=eventList.get(0) %>"><strong><%=eventList.get(2) %></strong>
						</a> organised at
						<%=eventList.get(6) %>
						from
						<%=eventList.get(4)%>
						to
						<%=eventList.get(5)%>
					</div>
				</div>
				<%
                            }
                            }
                            }
                            if(holidayList != null && holidayList.size()>0){
                            Iterator hit  = holidayList.iterator();
                            while(hit.hasNext()){
                            	String holidayData = (String) hit.next();	
                            
                            %>
				<div class="new-update clearfix">
					<i class="fa fa-bell-o"></i>
					<div class="update-done"><%=holidayData%></div>
					<%-- <div class="update-date"><span class="update-day">14</span>Feb</div> --%>
				</div>
				<%
                            }
                            }
                            %>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	</section>
	<section class="col-lg-3 connectedSortable" style="padding-left: 0px;">


	<% if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ITR_FILLING_REQUEST_BLOCK))) { %>
	<!--************start of code for mail link ************ -->
	<div class="box box-primary">
		<div class="box-header with-border" style="background-color: #3c8dbc;">
			<h3 class="box-title"
				style="color: #ff5900; font-size: 18px !important; font-weight: 900;">
				<marquee>File Your Income Tax Return</marquee>
			</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek" style="width: 100%;">
				<div class="content1">
					<div class="holder" style="padding: 0px;">
						<% String strDays = uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone())+"", IConstants.DBDATE, "2019-08-31", IConstants.DBDATE); %>
						<div style="margin: 5px 0px;">
							Due date to file ITR is 31st Aug 2019 (<%=uF.showData(strDays, "0") %>
							days remaining). File your return to avoid interest (1% per
							month), late fee and penalty of INR 5,000/-
						</div>
						<div class="col-md-12 col_no_padding" style="margin: 5px 0px;">
							<a href="javascript:void(0)" onclick="sendMail()"><b>
									&nbsp;&nbsp;Apply for Your Tax Return</b>
							</a>
						</div>
						<div class="col-md-12 col_no_padding" style="margin: 5px 0px;">
							<div id="msgDiv"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!--************end of code for mail link ************ --> <% } %> <% 
                String BASEUSERTYPE = (String)session.getAttribute(IConstants.BASEUSERTYPE);
                if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN))) { %>
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">If I am an HR</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek" style="width: 100%;">
				<div class="content1">
					<div class="holder">
						<div style="width: 100%; float: left;">
							<% if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN))) { %>
							<% if(CF.isWorkRig()) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7"><i class="fa fa-caret-right"
									aria-hidden="true"></i>Go to HR's Dashboard</a>
							</div>
							<% if(BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=1"><i class="fa fa-caret-right"
									aria-hidden="true"></i>Go to Admin</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=approvePay"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Approve Pay</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=onboardCandidate"><i
									class="fa fa-caret-right" aria-hidden="true"></i>On-Board
									Candidate</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=finalizeReviews"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Finalize
									Reviews</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=planLearningGaps"><i class="fa fa-caret-right" aria-hidden="true"></i>Plan Learning Gaps</a>
							</div>
							<% } %> 
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=7&userscreen=updateStatutoryCompliance"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Update
									Statutory Compliance</a>
							</div>
							<% } %>
							<% } %>
							<% if(CF.isTaskRig()) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=generateBills"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Generate Bills</a>
							</div>
							
					<!-- ===start parvez date: 30-03-2022=== -->
							<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ENABLE_MIS_REPORT_QUICK_LINK))){ %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=reportMIS"><i class="fa fa-caret-right" aria-hidden="true"></i>MIS Report</a>
							</div>
							<% } %>
					<!-- ===end parvez date: 30-03-2022=== -->
							
							<% } %>
							<% } else { %>
							<div class="nodata msg">You are not a HR.</div>
							<% } %>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	<% } %>
	<div class="box box-primary">
		<% 
                    String poFlag = (String)request.getAttribute("poFlag"); 
                    %>
		<div class="box-header with-border">
			<h3 class="box-title">Quick Links</h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse">
					<i class="fa fa-minus"></i>
				</button>
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<!-- /.box-header -->
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; max-height: 250px;">
			<div class="rosterweek">
				<div class="content1">
					<div class="holder">
						<div style="width: 100%; float: left;">
							<% if(CF.isTaskRig() && BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.CEO)) { %>
							<div style="margin: 5px 0px;">
								<a style="color: #ff8826;"
									href="Login.action?role=3&product=3&userscreen=CEODashboard"><i
									class="fa fa-caret-right" aria-hidden="true"></i><u>CEO
										Dashboard (Project)</u>
								</a>
							</div>
							<% } %>
							<% if(CF.isWorkRig() && BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.CEO)) { %>
							<div style="margin: 5px 0px;">
								<a style="color: #ff8826;" href="Login.action?role=2&product=2"><i
									class="fa fa-caret-right" aria-hidden="true"></i><u>CEO
										Dashboard (HR)</u>
								</a>
							</div>
							<% } %>
							<% if(CF.isWorkRig()) { %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="MyTime.action?callFrom=MyDashLeaveSummary"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Apply Leave</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="MyPay.action?callFrom=MyDashReimbursements"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Apply
									Reimbursement</a>
							</div>
							<% } %>
							<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
							<div style="margin: 5px 0px;">
								<a href="MyPay.action"><i class="fa fa-caret-right"
									aria-hidden="true"></i>Check Payroll</a>
							</div>
							<% } %>
							<div style="margin: 5px 0px;">
								<a href="MyDashboard.action"><i class="fa fa-caret-right"
									aria-hidden="true"></i>My Dashboard</a>
							</div>
							<div style="margin: 5px 0px;">
								<a href="MyProfile.action"><i class="fa fa-caret-right"
									aria-hidden="true"></i>My Profile</a>
							</div>
							<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_HOME_MY_TEAM)) && hmFeatureUserTypeId.get(IConstants.F_MY_HOME_MY_TEAM).contains(strUsertypeId)) { %>
							<div style="margin: 5px 0px;">
								<a href="OrganisationalChart.action"><i
									class="fa fa-caret-right" aria-hidden="true"></i>My Team</a>
							</div>
							<% } %>
							<% } %>
							<% if(CF.isTaskRig()) { %>
							<% if(uF.parseToBoolean(poFlag) || BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.MANAGER)) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=myProjects"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Go to Projects</a>
							</div>
							<% } else if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN))) { %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=allProjects"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Go to Projects</a>
							</div>
							<% } %>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=myTimesheet"><i
									class="fa fa-caret-right" aria-hidden="true"></i>Update
									Timesheets</a>
							</div>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3"><i
									class="fa fa-caret-right" aria-hidden="true"></i>My Project
									Dashboard</a>
							</div>
							<div style="margin: 5px 0px;">
								<a href="Login.action?role=3&product=3&userscreen=myWorkTasks"><i
									class="fa fa-caret-right" aria-hidden="true"></i>My Work</a>
							</div>
						
							<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ADD_BUDGET_LINK_UNABLE)) && hmFeatureUserTypeId.get(IConstants.F_ADD_BUDGET_LINK_UNABLE).contains(strUsertypeId)){ %>
								<div style="margin: 5px 0px;">
									<a href="AddPartnerwiseBudget.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Update Partner Budget</a>
								</div>
							<% } %>
						
							<% if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN) || BASEUSERTYPE.equals(IConstants.ACCOUNTANT))) { %>
								<div style="margin: 5px 0px;">
									<a href="AddInvoicingDetails.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Add Invoicing Details</a>
								</div>
								<div style="margin: 5px 0px;">
									<a href="AddReceiptDetails.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Add Receipt Details</a>
								</div>
							<% } %>
					<!-- ===start parvez date: 09-12-2022=== -->		
							<div style="margin: 5px 0px;">
								<a href="EmailDashboard.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Emails</a>
							</div>
					<!-- ===end parvez date: 09-12-2022=== -->			
							<% } %>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
	</section>
</div>
</section>
<% } %>

<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content" id="modalContent">
			<div class="modal-header" id="modalHeader">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body" id="modal-body">
			</div>
			<div class="modal-footer" id="modalFooter">
				<button type="button" id="closeButton" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

<div class="backtop  pull-right">
	<i class="fa fa-question" onclick="getHelpPage('MyHome')"></i>
</div>


<div class="modal" id="modalInfoHelp" role="dialog">
	<div class="modal-dialogHelp" id="modal-dialogHelp">
		<div>
			<div id="modalheader">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<div class="modal-title" id="modalTitleHelp"
					style="font-weight: bold">-</div>
			</div>
			<div id="modal-bodyHelp"></div>
			<!-- <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div> -->
		</div>
	</div>
</div>

<div class="modal" id="modalInfo1" role="dialog">
	<div class="modal-dialog modal-dialog1" id="modal-dialog1">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close1" id="close1"
					data-dismiss="modal">&times;</button>
				<h4 class="modal-title modal-title1" id="modal-title1"></h4>
			</div>
			<div id="modal-body1" style="height:400px; overflow-y:auto; padding-left: 25px;"></div>
			<div class="modal-footer">
				<button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>



<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	<% //isClockOnOffBlockShow && 
	if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0 && CF.isClockOnOff()) { %>
			//alert("onload");
			setClockOnOff('onload', '');
		<% } %>
	});


	function setClockOnOff(type, strAction) {
		var strMode = '';
		var strApproval = '';
		var isRosterDependant = '';
		var isRosterRequired = '';
		if(type == 'onclick') {
			if(document.getElementById("strMode")) {
				strMode = document.getElementById("strMode").value;
			}
			if(document.getElementById("strApproval")) {
				strApproval = document.getElementById("strApproval").value;
			}
			if(document.getElementById("isRosterDependant")) {
				isRosterDependant = document.getElementById("isRosterDependant").value;
			}
			if(document.getElementById("isRosterRequired")) {
				isRosterRequired = document.getElementById("isRosterRequired").value;
			}
			var id=false; 
			if(strAction == "CON") {
				id = confirm('Are you sure you want to clock on?');	
			} else if(strAction == "COFF") {
				id = confirm('Are you sure you want to clock off?');
			}
			if(id) {
				//alert("1 load");
				getContent('clockONOFF', 'GetClockEntryMessage.action?strAction='+strAction+'&strMode='+strMode+'&strApproval='+strApproval
						+'&isRosterDependant='+isRosterDependant+'&isRosterRequired='+isRosterRequired);
			}
		} else {
			//alert("2 load");
			getContent('clockONOFF', 'GetClockEntryMessage.action?strAction='+strAction+'&strMode='+strMode+'&strApproval='+strApproval
					+'&isRosterDependant='+isRosterDependant+'&isRosterRequired='+isRosterRequired);
		}
	}
	
	function openCandidateProfilePopup(CandID,recruitId) {
	    
	    var id=document.getElementById("panelDiv");
    	if(id) {
    		id.parentNode.removeChild(id);
    	}
    	var dialogEdit = '#modal-body1';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('#modal-title1').html('Candidate Information');
	    $("#modalInfo1").show();
	    var height = $(window).height()* 0.95;
		var width = $(window).width()* 0.95;
		$("#modal-dialog1").css("height", height);
		$("#modal-dialog1").css("width", width);
		$("#modal-dialog1").css("max-height", height);
		$("#modal-dialog1").css("max-width", width);
    	$.ajax({
		    url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId+"&form=MH&callType=calendar",
		    cache : false,
		    success : function(data) {
		    $(dialogEdit).html(data);
	    	}
    	});
	}
	
function getHelpPage(callPage) {
	var dialogEdit = '#modal-bodyHelp';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('#modalTitleHelp').html('Home Page'); 
	
	$("#modalInfoHelp").show();
	/* if($(window).width() >= 800){
		 $(".modal-dialog").width(800);
	 } */
	 var height = $(window).height()* 0.88;
	 var width = $(window).width()* 0.95;
	 $("#modal-dialogHelp").css("height", height);
	 $("#modal-dialogHelp").css("width", width);
	 $("#modal-dialogHelp").css("max-height", height);
	 $("#modal-dialogHelp").css("max-width", width);
	 
	/* document.getElementById("modalContent").setAttribute('class', 'modal-content1');
	document.getElementById("modalHeader").setAttribute('class', 'modal-header'); */
	/* document.getElementById("modalFooter").style.display = "none"; */
	$.ajax({
		url : "HelpPage.action?callPage="+callPage,
		cache : false,
		success : function(data) {
			/*alert("data ===>> " + data);*/
			$(dialogEdit).html(data);
		}
	});
}
</script>


