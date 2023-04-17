<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>

<style>
	.label {
    	font-size: 13px;
	}
</style>

	<script type="text/javascript" charset="utf-8">
    function panelcomment(panelid,candidateid,recruitid) {
    //	alert("panel id"+panelid+"candidateid"+candidateid);
    	var id=document.getElementById("popupAjaxLoad");
    	if(id){
        	id.parentNode.removeChild(id);
    	}
    	var dialogEdit = '.modal-body';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $('.modal-title').html('Panel Comments');
    $("#modalInfo").show();
    $.ajax({
    url : "PopupPanelcomments.action?panelid="+panelid+"&candidateid="+candidateid+"&recruitid="+recruitid,
    cache : false,
    success : function(data) {
    $(dialogEdit).html(data);
    }
    });
    }
    
    
	function alignResumeShortlistWorkflowMember(recruitId) {
		var id=document.getElementById("panelDiv");
    	if(id){
    		id.parentNode.removeChild(id);
    	}
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Align Resume Shortlisting Workflow');
		$("#modalInfo").show();
		$.ajax({
			url :"AlignResumeShortlistWorkflowMember.action?recruitmentID="+recruitId+'&formName=A',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
	    });
    
	}
    
    
    function addpanel(recruitId) {
    	var id=document.getElementById("panelDiv");
    	if(id){
    		id.parentNode.removeChild(id);
    	}
    	var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('Interview Round & Panel');
	    $("#modalInfo").show();
	    if($(window).width() >= 900){
	    	$(".modal-dialog").width(900);
	    }
	    $.ajax({
		    //url : "ApplyLeavePopUp.action", 
		    url :"AddCriteriaPanel.action?type=popup&recruitId="+recruitId+'&formName=A' ,
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
	    });
    
     }
    
    
    function openCandidateProfilePopup(CandID,recruitId,apptype, callType) {
     
    	var id=document.getElementById("panelDiv");
    	if(id){
    		id.parentNode.removeChild(id);
    	}
    	var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('Candidate Information');
	    $("#modalInfo").show();
	    
	    /* if($(window).width() >= 900){
	     $(".modal-dialog").width(900);
	    } */
	    var height = $(window).height()* 0.95;
		var width = $(window).width()* 0.95;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
	    
	    $.ajax({
		    url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId+"&form=A&apptype="+apptype+"&callType="+callType,
		    cache : false,
		    success : function(data) {
		    $(dialogEdit).html(data);
	    }
	    });
     }
    
    
    function viewCandidates(recruitId) {     
    	var id=document.getElementById("panelDiv");
    	if(id){
    		id.parentNode.removeChild(id);
    			}
    	var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('Shortlist Candidates');
	    $("#modalInfo").show();
	    if($(window).width() >= 900){
	    	$(".modal-dialog").width(900);
	    }
	    $.ajax({
	    //url : "ApplyLeavePopUp.action", 
	    url :"ViewCandidatePopUp.action?recruitId="+recruitId,
	    cache : false,
	    success : function(data) {
	    	$(dialogEdit).html(data);
	    }
	    });
     }
    
    
    function addCandidateModePopup(recruitId) {    	
    	var id=document.getElementById("panelDiv");
    	if(id){
    		id.parentNode.removeChild(id);
    			}
    	var dialogEdit = '.modal-body';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $('.modal-title').html('Add Candidate');
    $("#modalInfo").show();
    if($(window).width() >= 900){
    $(".modal-dialog").width(900);
    }
    if($(window).height() >= 400){
    $(".modal-body").height(400);
    }
    $.ajax({
    url :"AddCandidateModePopup.action?fromPage=CAPP&recruitId="+recruitId,
    cache : false,
    success : function(data) {
    $(dialogEdit).html(data);
    }
    });
     }
    
    
    var dialogEdit3 = '#addCandidateDiv';
    function addNewCandidate(recruitId, fromPage) {
	    var id=document.getElementById("panelDiv");
	    if(id){
	    id.parentNode.removeChild(id);
	    }
	    var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $("#modalInfo").show();
	    $(".modal-title").html('Add New Candidate');
	    if($(window).width() >= 1100){
	    	$(".modal-dialog").width(1100);
	    }
	    if($(window).height() >= 400){
	    	$(".modal-body").height(400);
	    }
	    $.ajax({
	     	url :"AddCandidate.action?recruitId="+recruitId+"&fromPage="+fromPage,
	     	cache : false,
	     	success : function(data) {
	     		$(dialogEdit).html(data);
	     	}
	     });
    }
    
    
    /* var dialogEdit3 = '#addCandidateDiv';
    function addNewCandidate(recruitId) {
    var id=document.getElementById("panelDiv");
    if(id){
    id.parentNode.removeChild(id);
    	}
    var dialogEdit = '.modal-body';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $('.modal-title').html('Add Candidate');
    $("#modalInfo").show();
    $.ajax({
    url :"AddCandidatePopup.action?recruitId="+recruitId,
    cache : false,
    success : function(data) {
    $(dialogEdit).html(data);
    }
    });
    } */
    
    
    function openPanelEmpProfilePopup(empId) {
    var id = document.getElementById("panelDiv");
    if(id) {
    	id.parentNode.removeChild(id);
    }
    var dialogEdit = '.modal-body';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $('.modal-title').html('Employee Information');
    $("#modalInfo").show();
    if($(window).width() >= 900){
    $(".modal-dialog").width(900);
    }
    $.ajax({
    //url : "ApplyLeavePopUp.action",  
    url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
    cache : false,
    success : function(data) {
    $(dialogEdit).html(data);
    }
    });
    }
    
    
    function changePanelRound(empId,roundId,recruitId,strRound) {
    	if(confirm("Are you sure, do you want to change round?")) {
    		/* alert("You can not modify this employee list ..."); */
    		getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?empId='+empId+'&recruitId='+recruitId+'&roundId='+roundId+'&strRound='+strRound+'&mode=emproundchange');
    		$("changeRoundDiv").dialog('close');
    		addpanel(recruitId);
    	}else{
    		/* getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?chboxStatus='+checked+'&selectedEmp='+value+'&recruitmentID=<s:property value="recruitID"/>') */	
    	}
    }
    /* function copyAddress12(obj){
    	alert("obj ===> "+obj);
    } */
    
    function closeJob(recruitmentId) {
	    //alert("openQuestionBank id "+ id)
	     var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('Close Job');
	    $("#modalInfo").show();
	    $.ajax({
		    url : "CloseJob.action?recruitmentId="+recruitmentId+"&fromPage=Application",
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
	    });
    }
    
    
    function addCandidateShortFormPopup(recruitId) {
	    var heght = '700';
	    var wdth = '95%';
	    var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('Add Candidate In One Step');
	    $("#modalInfo").show();
	    if($(window).width() >= 1100){
	    	$(".modal-dialog").width(1100);
	    }
	    $.ajax({
		    url :"AddCandidateInOneStep.action?recruitId="+recruitId+"&fromPage=A" ,
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
	    });
    }
    
    
    function viewAssessmentDetail(assessmentId, assessmentName) {
     //alert("openQuestionBank id "+ id)
        var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$(".modalInfo").show();
		$('.modal-title').html('View Assessment');
	    $.ajax({
		    url : "ViewAssessmentDetails.action?assessmentId="+assessmentId,
		    cache : false,
		    success : function(data) {
		   		 $(dialogEdit).html(data);
	   		 }
	    });
    }
    
    
    function viewAssessmentScoreSummary(assessmentId, candidateId, recruitId, roundId) {
    //alert("openQuestionBank id "+ id)
	    var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('Assessment Score Summary');
	    $("#modalInfo").show();
	    $.ajax({
		    url : "CandidateAssessmentScoreSummary.action?assessmentId="+assessmentId+"&candidateId="+candidateId+"&recruitId="+recruitId+"&roundId="+roundId,
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
	    });
    }
    
    function sendMailforBackgroundVerification(recruitId,candidateId)  {
		if(confirm('Are you sure, to send mail for document/ background verification process?')) {
			xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
			var xhr = $.ajax({
				url :"AddCandidateBackgroundDetails.action?recruitId="+recruitId+'&candidateId='+candidateId,
				cache : false,
				success : function(data) {
					alert(data);
				}
			});
			}
		}
	}

    
	function offerHoldPopup(candidateID, recruitId, rejectType) { 
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	var strTitle = 'Candidate Offer Hold';
    	if(rejectType == 'CANDIONHOLD_REASON') {
    		strTitle = 'Candidate Offer Hold Details';
    	}
    	$('.modal-title').html(strTitle);
    	$("#modalInfo").show();
    	$.ajax({
			url : "OfferAcceptAndRenegotiate.action?candidateID="+candidateID+"&recruitId="+recruitId+"&rejectType="+rejectType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
	
	
	function getApprovalStatus(id){
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('Work flow');
    	$.ajax({
    		url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=15",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
	function shortlistOrRejectResume(strStatus,candiAppId, userType,currUserType) {
    	//alert("empId==>"+empId);
    	var strmsg = 'Are you sure, you want to reject this candidate?';
    	
    	if(strStatus == 2){
    		strmsg = 'Are you sure, you want to shortlist this candidate?';
    	}
    	if(strmsg) {
    		var reason = window.prompt("Please enter your reason.");
    		if (reason != null) {   
    			xmlhttp = GetXmlHttpObject();
    		    if (xmlhttp == null) {
    				alert("Browser does not support HTTP Request");
    				return;
    		    } else {
    	            var xhr = $.ajax({
    	                url : 'UpdateADRRequest.action?S='+strStatus+'&candiAppId='+candiAppId+'&mReason='+reason+'&userType='+userType+'&currUserType='+currUserType,
    	                cache : false,
    	                success : function(data) {
    	            	   //alert("data ===>> " + data);
    	               	if(data == "") {
    	               	} else {
    	               		/* var allData = data.split("::::");
    	               		document.getElementById("myDivStatus"+nCount).innerHTML = allData[0];
    	               		document.getElementById("myDivM"+nCount).innerHTML= "";
    	               		if(allData[1] == 1) { */
    	               			document.getElementById("myDivM"+candiAppId).innerHTML="Shortlisted";
    	               		/* } */
    	               	}
    	                }
    	            });
    		    }
    		}
    	}
    }
	
    
    /* function loadMore(proPage, minLimit) {
    document.frmApplications.proPage.value = proPage;
    document.frmApplications.minLimit.value = minLimit;
    document.frmApplications.submit();
    } */
    
    $(function() {
   		$("body").on('click','#closeButton',function(){
   			$(".modal-dialog").removeAttr('style');
   			$(".modal-body").height(400);
   			$("#modalInfo").hide();
   		});
   		$("body").on('click','.close',function(){
   			$(".modal-dialog").removeAttr('style');
   			$(".modal-body").height(400);
   			$("#modalInfo").hide();
   		});
   		
   		/* $("body").on('click','#closeButton1',function(){
   			$(".modal-dialog1").removeAttr('style');
   			$("#modal-body1").height(400);
   			$("#modalInfo1").hide();
   	    });
   		$("body").on('click','#close1',function(){
   			$(".modal-dialog1").removeAttr('style');
   			$("#modal-body1").height(400);
   			$("#modalInfo1").hide();
   	    }); */
    	
    });
     
</script>
<%
    UtilityFunctions uF = new UtilityFunctions();
    String dataType = (String) request.getAttribute("dataType");
    String proCount = (String)request.getAttribute("proCount");
    
    String strVm = (String)request.getAttribute("strVm");
    %>
<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 400px;">
    <%
        String sbData = (String) request.getAttribute("sbData");
        String strSearchJob = (String) request.getAttribute("strSearchJob");
        String appliSourceType = (String) request.getAttribute("appliSourceType");
        
        String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
        Map<String, String> hmCandidateEducation = (Map<String, String>) request.getAttribute("hmCandidateEducation");
        Map<String, String> hmCandidateSkill = (Map<String, String>) request.getAttribute("hmCandidateSkill");
        Map<String, String> hmCandidateExperience = (Map<String, String>) request.getAttribute("hmCandidateExperience");
        
        List<Map<String, String>> alheader = (List<Map<String, String>>) request.getAttribute("applicationstatus");
        if(alheader == null ) alheader = new ArrayList<Map<String, String>>();
        
        Map<String, String> applyMp = new HashMap<String, String>();
        Map<String, String> approveMp = new HashMap<String, String>();
        Map<String, String> denyMp = new HashMap<String, String>();
        Map<String, String> finalisedMp = new HashMap<String, String>();
        
        if(alheader != null && !alheader.isEmpty()) {
        	applyMp = (Map<String, String>) alheader.get(0);
        	approveMp = (Map<String, String>) alheader.get(1);
        	denyMp = (Map<String, String>) alheader.get(2);
        	finalisedMp = (Map<String, String>) alheader.get(3);
        }
        
        //Map todayTommorowAppl = (Map) request.getAttribute("todayTommorowAppl");
        Map<String, List<List<String>>> hmNewApplications = (Map<String, List<List<String>>>)request.getAttribute("hmNewApplications");
        Map<String, String> hmJobCodeName = (Map<String, String>) request.getAttribute("hmJobCodeName");
        Map<String, String> hmJobPriority = (Map<String, String>) request.getAttribute("hmJobPriority");
        
        Map<String, Map<String, String>> hmPanel = (Map<String, Map<String, String>>) request.getAttribute("hmPanel");
        Map<String, String> hmpanelname1 = (Map<String, String>) request.getAttribute("hmpanelname1");
        List<String> recruitmentIdList = (List<String>)request.getAttribute("recruitmentIdList");
        Map<String, String> hmJobStatus = (Map<String, String>)request.getAttribute("hmJobStatus");
        if(hmJobStatus == null) hmJobStatus = new HashMap<String, String>();
        
        Map<String, String> hmCandiShortlistStatus = (Map<String, String>) request.getAttribute("hmCandiShortlistStatus");
        if(hmCandiShortlistStatus == null) hmCandiShortlistStatus = new HashMap<String, String>();
        Map<String, Map<String, String>> hmshortlistedname = (Map<String, Map<String, String>>) request.getAttribute("hmshortlistedname");
        Map<String, Map<String, String>> hmCandiImage = (Map<String, Map<String, String>>) request.getAttribute("hmCandiImage");
        Map<String, Map<String, String>> hmRejectCandiImage = (Map<String, Map<String, String>>) request.getAttribute("hmRejectCandiImage");
        Map<String, String> hmFinalCount = (Map<String, String>)request.getAttribute("hmFinalCount");
        Map<String, String> hmSelectCount = (Map<String, String>)request.getAttribute("hmSelectCount");
        Map<String, String> hmRejectCount = (Map<String, String>)request.getAttribute("hmRejectCount");
        Map<String, List<String>> hmRoundIdsRecruitwise = (Map<String, List<String>>) request.getAttribute("hmRoundIdsRecruitwise");
        Map<String, String> hmpanelNameRAndRwise = (Map<String, String>)request.getAttribute("hmpanelNameRAndRwise");
        Map<String, String> hmPanelInterviewDates = (Map<String, String>)request.getAttribute("hmPanelInterviewDates");
        Map<String, Map<String, String>> hmcandiStarRecruitwise = (Map<String, Map<String, String>>)request.getAttribute("hmcandiStarRecruitwise");
        
        Map<String, String> hmRoundStatus = (Map<String, String>)request.getAttribute("hmRoundStatus");
        Map<String, Map<String, String>> hmFinalisedName = (Map<String, Map<String, String>>) request.getAttribute("hmFinalisedName");
        //Map<String, String> hmCommentsHR = (Map) request.getAttribute("hmCommentsHR");
        Map<String, Map<String, String>> hmPanelRatingAndComments = (Map<String, Map<String, String>>) request.getAttribute("hmPanelRatingAndComments");
        Map<String, Map<String, Map<String, String>>> hmRecruitWiseRoundId = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmRecruitWiseRoundId");
        //Map<String, List<String>> hmRoundUserIds = (Map<String, List<String>>) request.getAttribute("hmRoundUserIds");
        
        //Map<String, Map<String, String>> hmPanelComments = (Map) request.getAttribute("hmPanelComments");
        Map<String, Map<String, String>> hmRejectedName = (Map<String, Map<String, String>>) request.getAttribute("hmRejectedName");
        Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
        
        String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
        String fromPage = (String) request.getAttribute("fromPage");
        
        
        Map<String, String> hmCandToEmp = (Map<String, String>) request.getAttribute("hmCandToEmp");
        if(hmCandToEmp == null) hmCandToEmp = new HashMap<String, String>();
        
        Map<String, String> hmRoundAssessment = (Map<String, String>) request.getAttribute("hmRoundAssessment");
        if(hmRoundAssessment == null) hmRoundAssessment = new HashMap<String, String>();
        
        Map<String, String> hmJobTitle = (Map<String, String>) request.getAttribute("hmJobTitle");
        if(hmJobTitle == null) hmJobTitle = new HashMap<String, String>();
        
        Map<String, String> hmAssessRateRecruitAndRoundIdWise = (Map<String, String>) request.getAttribute("hmAssessRateRecruitAndRoundIdWise");
        if(hmAssessRateRecruitAndRoundIdWise == null) hmAssessRateRecruitAndRoundIdWise = new HashMap<String, String>();
        
        Map<String, Map<String, Map<String, String>>> hmSource = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmSource");
        if(hmSource == null) hmSource = new HashMap<String, Map<String,Map<String,String>>>();
      
        Map<String, Map<String, Map<String, String>>> hmCandiOnHold = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmCandiOnHold");
        if(hmCandiOnHold == null) hmCandiOnHold = new HashMap<String, Map<String,Map<String,String>>>();
        
	/* ===start parvez date: 17-01-2022=== */
   		CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
		boolean isEnableHiringInCloseJd = CF.getFeatureManagementStatus(request, uF, IConstants.F_ENABLE_HIRING_PROCEDURE_IN_CLOSED_JOB_REQUIREMENT);
	//	System.out.println("App.jsp/545--F_ENABLE_HIRING_PROCEDURE_IN_CLOSED_JOB_REQUIREMENT="+isEnableHiringInCloseJd);
	/* ===start parvez date: 17-01-2022=== */	
    %>
        
    <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
    
    <%		
        for(int r=0; recruitmentIdList!= null && r<recruitmentIdList.size(); r++) {
        	String recruitId = recruitmentIdList.get(r);
        	Map<String, String> hmCandiStars = hmcandiStarRecruitwise.get(recruitId);
        	String priorityClass = "";
        	if (uF.parseToInt(hmJobPriority.get(recruitId)) == 1) {
        		priorityClass ="high";
        	}else if (uF.parseToInt(hmJobPriority.get(recruitId)) == 2) {
        		priorityClass ="medium";
        	}else{
        		priorityClass ="low";
        	}
        	boolean closeFlag = false;
        	if(uF.parseToBoolean(hmJobStatus.get(recruitId))){ 
        		closeFlag = true;
        	}
        	Map<String, Map<String, String>> hmCandidate = hmSource.get(recruitId);
        	if(hmCandidate == null) hmCandidate = new HashMap<String, Map<String,String>>();
        	
        	Map<String, Map<String, String>> hmCandiHoldDetails = hmCandiOnHold.get(recruitId);
        	if(hmCandiHoldDetails == null) hmCandiHoldDetails = new HashMap<String, Map<String,String>>();
        %>
    <%-- <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height: 30px;">
        <div class="<%if(request.getAttribute("currRecruitId") != null && request.getAttribute("currRecruitId").toString().equals(recruitId)){ %> heading <%} else { %> heading_dash <% } %>" style="padding-left: 50px;float:left;width:96%;">
        <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;margin-bottom: 0px;">
            
            <!-- /.box-header -->
           <!--  <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                
            </div> -->
        </div>
    </div> --%>
    <div class="box-tools pull-right">
			<span class="label label-warning">Total Applications(<%=uF.showData(applyMp.get(recruitId), "0")%>)</span>		
			<span class="label label-info">Shortlisted(<%=uF.showData(hmSelectCount.get(recruitId), "0")%>)</span>
			<span class="label label-success">Finalisation(<%=uF.showData(hmFinalCount.get(recruitId), "0")%>)</span>
			<span class="label label-danger">Rejected(<%=uF.showData(hmRejectCount.get(recruitId), "0")%>)</span>
	</div>
				
				
				
    		<div class="box-header" style="width:100%; float:left; padding: 0px;">
                
		        <div class="applicationContent" style="padding:0px; padding-top:0px; height:auto;">
                    <div style="float: left; width: 100%">
	                    <div style="float: left;"><b>Candidate shortlisting workflow:</b>&nbsp;</div>
	                    <% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
		                    <div style="float: left;">
		                        <a href="javascript:void(0)" title="Align Workflow" onclick="alignResumeShortlistWorkflowMember('<%=recruitId%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
		                    </div>
	                    <% } %>
                    </div>
                    <div style="float: left; width: 100%">
	                    <div style="float: left;"><b>Interview Round & Panel:</b>&nbsp;</div>
	                    <div style="float: left;">
	                        <%
	                            //System.out.println("strUserType ===> " + strUserType);
	                      
	                       //===start parvez date: 17-01-2022=== 
	                            //if(!closeFlag){
	                            if(!closeFlag || isEnableHiringInCloseJd){	
		                   //===end parvez date: 17-01-2022===        
	                            	if (hmpanelname1.get(recruitId) == null || hmpanelname1.get(recruitId).equals("")) {
		                            	if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
		                            %>
					                        <a href="javascript:void(0)" title="Add Panel" onclick="addpanel('<%=recruitId%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
					                  <%}else{ %>
				                        	No panel list created
				                        <%} %>
	                        <%
	                            } else {
	                            %>
	                        <%=hmpanelname1.get(recruitId)%>&nbsp;&nbsp;
	                        <% if (strUserType != null &&  (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
	                        <a href="javascript:void(0)" title="Modify Panel" onclick="addpanel('<%=recruitId%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
	                        <%		}
	                            }
	                            } else {
	                            %>
	                        <%=hmpanelname1.get(recruitId)%>
	                        <%} %>
	                        
	                    </div>
                    </div>
                    <div style="float: left; width: 100%;">
                        <hr style="border:solid 1px #ECECEC;"/>
                    </div>
                    <%
                        List<List<String>> newAppList = hmNewApplications.get(recruitId);
                        
                        Calendar cal = new GregorianCalendar();
                        Date currentDate = cal.getTime();
                        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
                        String today = date_format.format(currentDate);
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        currentDate = cal.getTime();
                        String yesterday = date_format.format(currentDate);
                        %>
                    <div style="float:left;width:100%;">
                        <p>
                            <b>New Applications: (<%=(newAppList!= null && !newAppList.isEmpty()) ? newAppList.size() : "0" %>)</b>&nbsp;
                       
                       <!-- ===start parvez date: 17-01-2022=== -->    
                            <%-- <%if(!closeFlag) { %> --%>
                            <%if(!closeFlag || isEnableHiringInCloseJd) { %>
                      <!-- ===end parvez date: 17-01-2022=== -->      
                            <a href="javascript:void(0)" onclick="viewCandidates('<%=recruitId%>')">Compare & Shortlist Candidates </a> |
                            <a href="javascript:void(0)" onclick="addCandidateModePopup('<%=recruitId %>')">Add New Candidate(8 Step)</a> |
                            <a href="javascript:void(0)" onclick="addCandidateShortFormPopup('<%=recruitId %>')">Add New Candidate(1 Step)</a>
                            <% } %>
                        <div style="margin-top: -10px;">
                            <%
                                int ii=0;
                                for(ii=0; newAppList!= null && !newAppList.isEmpty() && ii< newAppList.size(); ii++) {
                                	String candiId = null;
                                	String appName = null;
                                	
                                	List<String> innerList = newAppList.get(ii);
                                	candiId = innerList.get(0);
                                	appName = innerList.get(1).trim();
                                	/* if(ii==newAppList.size()-1){
                                		appName = innerList.get(1).trim();	
                                	}else{
                                		appName = innerList.get(1).trim()+", ";
                                	 } */
                                	 if(ii > 0) {
                                %>
                            , 
                            <% } %>
                     
                     <!-- ===start parvez date: 17-01-2022=== -->       
                            <%-- <%if(closeFlag){ %> --%>
                            <%if(closeFlag && !isEnableHiringInCloseJd){ %>
                     <!-- ===end parvez date: 17-01-2022=== -->
                            
                            <%=uF.showData(appName, "")%>
                            <%} else { %>
                            <a style="font-weight: normal;" href="javascript: void(0)" onclick="openCandidateProfilePopup('<%=candiId%>','<%=recruitId %>', '', '')"><%=uF.showData(appName, "")%></a>
                            <%} %>
                            <% } if(ii==0) { %>
                            <span>No New Applications</span>
                            <% } %>
                        </div>
                        </p>
                    </div>
                    <div class="attendance" style="clear:both;">
                        <h4>Shortlisted: (<%=uF.showData(hmSelectCount.get(recruitId), "0")%>)</h4>
                        <table width="100%" cellspacing="0" cellpadding="2" style="margin: 0px;" class="table table-bordered">
                            <tbody>
                                <tr class="darktable">
                                    <th style="text-align: center;">Name</th>
                                    <th style="text-align: center;">Experience</th>
                                    <th style="text-align: center;">Education</th>
                                    <th style="text-align: center;">Skills</th>
                                    <th style="text-align: center;">Round</th>
                                </tr>
                                <%
                                    Map<String, String> shortlistedNameMap = hmshortlistedname.get(recruitId);
                                    	if (shortlistedNameMap == null)shortlistedNameMap = new HashMap<String, String>();
                                    	Map<String, String> hmInnerImage = hmCandiImage.get(recruitId);
                                    	if (hmInnerImage == null)hmInnerImage = new HashMap<String, String>();	
                                    	Iterator<String> iterator = shortlistedNameMap.keySet().iterator();
                                    	int x = 1;
                                    	while (iterator.hasNext()) { 
                                    		/* if(x == 11 && uF.parseToInt(strVm) == 0){
                                    			break;
                                    		} */
                                    		String candidateId = iterator.next();
                                    		String strStars = hmCandiStars.get(candidateId);
                                    		//System.out.println(recruitId+"  candidateId ==>> "+candidateId+"  strStars ==>> "+strStars);
                                    		 
                                    		Map<String, String> hmSourceDetails = hmCandidate.get(candidateId);
                                    		if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                    		
                                    %>
                                <tr class="lighttable">
                                    <td valign="top" style="text-align: left;">
                                        <div style="float: left; margin: 2px 10px 0px 0px;">
                                            <!-- border: 1px solid #000; -->
                                            <%if(docRetriveLocation == null) { %>
                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateId %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmInnerImage.get(candidateId) %>" />
                                            <%} else { %>
                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateId %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+candidateId+"/"+IConstants.I_100x100+"/"+hmInnerImage.get(candidateId)%>" />
                                            <%} %> 
                                            <%-- <img height="100" width="100" class="lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="userImages/<%=hmInnerImage.get(candidateId)%>" /> --%> 
                                        </div>
                                        <div style="float: left;">
                                        
                                   <!-- ===start parvez date: 17-01-2022=== -->
                                            <%-- <%if(closeFlag){ %> --%>
                                            <%if(closeFlag && !isEnableHiringInCloseJd){ %>
                                   <!-- ===end parvez date: 17-01-2022=== -->         
                                            	<%=shortlistedNameMap.get(candidateId)%>
                                            <%} else { %>
                                            	<a href="javascript: void(0)" onclick="<%if(hmCandToEmp.containsKey(candidateId.trim())){%>alert('<%=hmCandToEmp.get(candidateId.trim()) %>');<%} else {%>openCandidateProfilePopup('<%=candidateId%>','<%=recruitId %>','shortlist', '')<%}%>"> <%=shortlistedNameMap.get(candidateId)%></a>
                                            <%} %>
                                            <br/>
                                            <div style="line-height: 16px;">
                                                <span id="Reting<%=candidateId%>" style="margin-right: 5px; line-height: 18px;"><%=strStars %>/5</span>
                                                <span id="starPrimaryS<%=recruitId+"_"+candidateId %>" style="margin-left: 5px; line-height: 12px;"></span>
                                            </div>
                                            <script type="text/javascript">
                                                $('#starPrimaryS'+'<%=recruitId+"_"+candidateId %>').raty({
                                                      readOnly: true,
                                                      start:	<%=strStars %> ,
                                                      half: true,
                                                      targetType: 'number'
                                                });
                                            </script>
                                            <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Source:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_TYPE"),"-") %></div>
                                            <div style="font-size: 11px; line-height: 15px;"><strong style="color: gray;">Source Name:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_NAME"),"-") %></div>
                                        </div>
                                        <%-- 						<%
                                            if (alUnderProcessCandidate.contains(innerlist.get(0))) {
                                            %> (UP)<%
                                            }
                                            %> --%>
                                    </td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateExperience.get(candidateId), "No experience added")%></td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateEducation.get(candidateId), "No education added")%></td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateSkill.get(candidateId), "No skill added")%></td>
                                    <td style="text-align: left; width: 30%; line-height: 21px;" valign="top">
                                        <%
                                            List<String> roundIdsRecruitwiseList = hmRoundIdsRecruitwise.get(recruitId);
                                            for(int a=0; roundIdsRecruitwiseList!= null && a<roundIdsRecruitwiseList.size(); a++) {
                                            %> 
                                            <% if(hmCandiShortlistStatus.get(candidateId) !=null && !hmCandiShortlistStatus.get(candidateId).equals("")) { %>
	                                        	<% if(a==0) { %>
	                                        	<div style="float: left;">Candidate Shortlisting</div> <%=hmCandiShortlistStatus.get(candidateId) %>
	                                        	<% } else { break; } %>
	                                        <% } else { %>
	                                        <span style="font-weight: bold; color: gray;"> Round <%=uF.showData(roundIdsRecruitwiseList.get(a), "0") %></span> 
	                                        &nbsp;&nbsp;&nbsp;
	                                        <%if(hmPanelInterviewDates.get(recruitId+"_"+ roundIdsRecruitwiseList.get(a) +"_"+ candidateId) != null && !hmPanelInterviewDates.get(recruitId+"_"+ roundIdsRecruitwiseList.get(a) +"_"+ candidateId).equals("")){ %>
	                                        <%=uF.showData(hmPanelInterviewDates.get(recruitId+"_"+ roundIdsRecruitwiseList.get(a) +"_"+ candidateId),"No Date Added Yet") %> 
	                                        <% if(hmRoundStatus != null && hmRoundStatus.get(recruitId+"_"+candidateId+"_"+roundIdsRecruitwiseList.get(a)) != null && !hmRoundStatus.get(recruitId+"_"+candidateId+"_"+roundIdsRecruitwiseList.get(a)).equals("0")){ %>
	                                         <!-- <img src="images1/tick.png" style="height: 16px;"/> -->
	                                         <i class="fa fa-check checknew" aria-hidden="true"></i>
	                                        <% } %>
	                                        <% } else { %>
	                                   
	                                  <!-- ===start parvez date: 17-01-2022=== -->     
	                                        <%-- <%if(!closeFlag) { %> --%>
	                                        <%if(!closeFlag || isEnableHiringInCloseJd) { %>
	                                  <!-- ===end parvez date: 17-01-2022=== -->      
	                                        	<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
	                                        		<a href="javascript: void(0)" onclick="openCandidateProfilePopup('<%=candidateId%>','<%=recruitId %>','shortlist', 'addDates')"> Click here to add dates</a>
	                                        	<% } %>
	                                        <% } %>
	                                        <% } %>
	                                        <br/>
	                                        <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID")) > 0) { 
	                                            //System.out.println("recruitId ===>> " + recruitId + " -- candidateId ===>> " + candidateId + " -- roundId ===>> " + roundIdsRecruitwiseList.get(a)+" -- asessID ===>> " + hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID"));
	                                            //System.out.println("hmAssessRateRecruitAndRoundIdWise ===>> " + hmAssessRateRecruitAndRoundIdWise + " ------ " + hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID"))); 
	                                            %>
	                                       
	                              <!-- ===start parvez date: 17-01-2022=== -->             
	                                        <%-- <%if(closeFlag){ %> --%>
	                                        <%if(closeFlag && !isEnableHiringInCloseJd){ %>
	                              <!-- ===end parvez date: 17-01-2022=== -->          
	                                        <div>
	                                            <span style="color: gray;">Assessment:</span> &nbsp;
	                                            <span>
	                                            <%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_NAME"), "") %>
	                                            </span>
	                                            <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID"))) > 0) { %>
	                                            <span id="assessScoreCard_<%=recruitId %><%=candidateId %>_<%=roundIdsRecruitwiseList.get(a) %>" style="margin-left: 7px;">
	                                            <%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID")), "NA") %>%
	                                            </span>
	                                            <% } %>
	                                        </div>
	                                        <%} else { %>
	                                        <div>
	                                            <span style="color: gray;">Assessment:</span> &nbsp;
	                                            <span>
	                                            <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_NAME"), "") %></a>
	                                            </span>
	                                            <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID"))) > 0) { %>
	                                            <span id="assessScoreCard_<%=recruitId %><%=candidateId %>_<%=roundIdsRecruitwiseList.get(a) %>" style="margin-left: 7px;">
	                                            <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID") %>', '<%=candidateId%>', '<%=recruitId %>', '<%=roundIdsRecruitwiseList.get(a) %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID")), "NA") %>%</a>
	                                            </span>
	                                            <% } %>
	                                        </div>
	                                        <!-- <br/> -->
	                                        <% } %>
	                                        <% } %>
	                                        <%if(hmpanelNameRAndRwise.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)) !=null && !hmpanelNameRAndRwise.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)).isEmpty()){ %>
	                                        <%=hmpanelNameRAndRwise.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)) %>
	                                        <%  } %>
	                                        <% if(a<(roundIdsRecruitwiseList.size()-1)) { %>
	                                        <div style="border-bottom: 1px solid #CCCCCC;"></div>
	                                        <% } %>
                                        <%  }  %>
                                        <%  }  %>
                                    </td>
                                </tr>
                                <% //x++; 
                                    } %>
                                <% if (shortlistedNameMap.keySet().size() == 0) { %>
                                <tr class="lighttable">
                                    <td colspan="7">
                                        <div class="nodata msg">
                                            <span>No Application Shortlisted.</span>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                    } else{
                                    	if(x == 11){
                                    %>
                                <tr class="lighttable">
                                    <td colspan="7" style="text-align: right;"><a style="padding-right:5px" href="Applications.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                </tr>
                                <%}
                                    }
                                    %>
                            </tbody>
                        </table>
                    </div>
                    <div class="attendance" style="margin-top: 20px;">
			            <h4>Finalisation: (<%=uF.showData(hmFinalCount.get(recruitId), "0")%>)</h4>
                        <table width="100%" cellspacing="0" cellpadding="2" style="margin: 0px;" class="table table-bordered">
                            <tbody>
                                <tr class="darktable">
                                    <th style="text-align: center; width: 25%;">Name</th>
                                    <th style="text-align: center;">Experience</th>
                                    <th style="text-align: center;">Education</th>
                                    <th style="text-align: center;">Skills</th>
                                    <th style="text-align: center; width: 20%;">Panel Rating</th>
                                    <th style="text-align: center; width: 25%;">Panel Report</th>
                                    <!-- <th style="text-align: center;">Summary</th> -->
                                </tr>
                                <%
                                    Map<String, String> finalisedNameMap = (Map<String, String>) hmFinalisedName.get(recruitId);
                                    if (finalisedNameMap == null)
                                    	finalisedNameMap = new HashMap<String, String>();
                                    
                                    Iterator<String> itr = finalisedNameMap.keySet().iterator();
                                    //System.out.println("hmCommentsHR ---> "+hmCommentsHR);
                                    int y = 1;
                                    while (itr.hasNext()) {
                                    	/* if(y == 11 && uF.parseToInt(strVm) == 0){
                                    		break;
                                    	} */
                                    	String candidateId = itr.next();
                                    	String strStars = hmCandiStars.get(candidateId);
                                    	
                                    	Map<String, String> hmSourceDetails = hmCandidate.get(candidateId);
                                    	if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                    	
                                    	Map<String, String> hmHoldDetails = hmCandiHoldDetails.get(candidateId);
                                		if(hmHoldDetails == null) hmHoldDetails = new HashMap<String, String>();
                                    %>
                                <tr class="lighttable">
                                    <td valign="top" style="text-align: left;">
                                        <div style="float: left; margin: 2px 10px 0px 0px">
                                            <!-- border: 1px solid #000; -->
                                            <%if(docRetriveLocation == null) { %>
                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateId %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmInnerImage.get(candidateId) %>" />
                                            <%} else { %>
                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateId %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+candidateId+"/"+IConstants.I_100x100+"/"+hmInnerImage.get(candidateId)%>" />
                                            <%} %>
                                            <%-- <img height="100" width="100" class="lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="userImages/<%=hmInnerImage.get(candidateId)%>" /> --%> 
                                        </div>
                                        <div style="float: left;">
                                        
                                   <!-- ===start parvez date: 17-01-2022=== -->     
                                            <%-- <%if(closeFlag){ %> --%>
                                            <%if(closeFlag && !isEnableHiringInCloseJd){ %>
                                   <!-- ===end parvez date: 17-01-2022=== -->         
                                            <%=finalisedNameMap.get(candidateId)%>
                                            <%} else { %>
                                            <%-- <a href="javascript: void(0)" onclick="openCandidateProfilePopup('<%=candidateId%>','<%=recruitId %>','finalize')"> <%=finalisedNameMap.get(candidateId)%></a> --%>
                                            <a href="javascript:void(0)" onclick="<%if(hmCandToEmp.containsKey(candidateId.trim())){%>alert('<%=hmCandToEmp.get(candidateId.trim()) %>');<%} else {%>openCandidateProfilePopup('<%=candidateId%>','<%=recruitId %>','finalize', '')<%}%>"> <%=finalisedNameMap.get(candidateId)%></a>
                                            <% } %>
                                            <br/>
                                            <div style="line-height: 16px;">
                                                <span id="Reting<%=candidateId%>" style="margin-right: 5px; line-height: 18px;"><%=strStars %>/5</span>
                                                <span id="starPrimaryF<%=recruitId+"_"+candidateId %>" style="margin-left: 5px; line-height: 12px;"></span>
                                            </div>
                                            <script type="text/javascript">
                                                $('#starPrimaryF'+'<%=recruitId+"_"+candidateId %>').raty({
                                                      readOnly: true,
                                                      start:	<%=strStars %> ,
                                                      half: true,
                                                      targetType: 'number'
                                                });
                                            </script>
                                            <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Source:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_TYPE"),"-") %></div>
                                            <div style="font-size: 11px; line-height: 15px;"><strong style="color: gray;">Source Name:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_NAME"),"-") %></div>
                                        </div>
                                        <%-- <a href="CandidateMyProfile.action?CandID=<%=candidateId%>&recruitId=<%=recruitId %>"><%=finalisedNameMap.get(candidateId)%></a> --%>
                                    </td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateExperience.get(candidateId), "No experience added")%></td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateEducation.get(candidateId), "No education added")%></td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateSkill.get(candidateId), "No skill added")%></td>
                                    <%
                                        Map<String, String> hmPanelRatingCommentMap = new HashMap<String, String>();
                                        if(hmPanelRatingAndComments != null) {
                                        	hmPanelRatingCommentMap = hmPanelRatingAndComments.get(recruitId +"_"+ candidateId);
                                        }
                                         Map<String, Map<String, String>> hmRoundIds = new HashMap<String, Map<String, String>>();
                                        if(hmRecruitWiseRoundId != null) {
                                        	hmRoundIds = hmRecruitWiseRoundId.get(recruitId);
                                        } 
                                        %>
                                    <td valign="top" style="text-align: left;">
                                        <%
                                            int count = 0;
                                            Set keys=hmRoundIds.keySet();
                                            Iterator it=keys.iterator();
                                            
                                            while(it.hasNext()) {	
                                            	String roundId = (String)it.next();
                                            	Map<String, String> panelUserIds = hmRoundIds.get(roundId);
                                            	//System.out.println("roundId rating ----->> " + roundId);
                                            	//System.out.println("panelUserList rating ----->> " + panelUserIds);
                                            	%>
                                        <div style="float: left; width: 100%;">
                                    <!-- ===start parvez date: 17-01-2022=== -->    
                                            <%-- <%if(closeFlag){ %> --%>
                                            <%if(closeFlag && !isEnableHiringInCloseJd){ %>
                                    <!-- ===end parvez date: 17-01-2022=== -->        
                                            <span style="font-weight: bold; color: gray;">Round <%=roundId %></span>
                                            <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                            <div>
                                                <span style="color: gray;">Assessment:</span> &nbsp;
                                                <span>
                                                <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %></a>
                                                </span>
                                                <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                <span id="assessScoreCard_<%=recruitId %><%=candidateId %>_<%=roundId %>" style="margin-left: 7px;">
                                                <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=candidateId%>', '<%=recruitId %>', '<%=roundId %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%</a>
                                                </span>
                                                <% } %>
                                            </div>
                                            <!-- <br/> -->
                                            <% } %>
                                            <%} else { %>
                                            <a href="javascript:void(0)" style="line-height: 10px;" onclick="panelcomment(<%=roundId%>,<%=candidateId%>,<%=recruitId%>);">Round <%=roundId %> </a>
                                            <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                            <div>
                                                <span style="color: gray;">Assessment:</span> &nbsp;
                                                <span>
                                                <%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>
                                                </span>
                                                <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                <span id="assessScoreCard_<%=recruitId %><%=candidateId %>_<%=roundId %>" style="margin-left: 7px;">
                                                <%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%
                                                </span>
                                                <% } %>
                                            </div>
                                            <!-- <br/> -->
                                            <% } %>
                                            <%} %>
                                        </div>
                                        <%
                                            Set keysEmpID = panelUserIds.keySet();
                                            Iterator itEmpID = keysEmpID.iterator();
                                            while(itEmpID.hasNext()) {	
                                            	String panelEmpId = (String)itEmpID.next();	
                                            	//System.out.println("panelempid ---> "+panelempid);
                                            			if (count > 0) {
                                            %> <br style="line-height: 0px;"> <% } %>
                                        <div style="float: left; width: 100%;">
                                            <div style="float: left; margin-right: 10px; margin-top: -3px;"> <%=hmEmpName.get(panelEmpId) %> </div>
                                            <div style="float: left;" id="starPrimary<%=roundId + panelEmpId + candidateId%>" style="line-height: 20px;"></div>
                                        </div>
                                        <script type="text/javascript">
                                            $('#starPrimary<%=roundId + panelEmpId + candidateId%>').raty({
			                                    readOnly: true,
			                                    start: <%=uF.parseToDouble(hmPanelRatingCommentMap != null ? hmPanelRatingCommentMap.get(roundId +"_" + panelEmpId +"_RATING") : "")%> ,
			                                    half: true,
			                                    targetType: 'number'
			                          		});
                                        </script>
                                        <%
                                            count++;
                                            }	 			
                                            }
                                            %>
                               
                               <!-- ===start parvez date: 17-01-2022=== -->       
                                        <%-- <% if(!closeFlag && strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %> --%>
                                         <% if(!closeFlag && strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN)) || isEnableHiringInCloseJd) { %>			 
                               <!-- ===end parvez date: 17-01-2022=== -->         
                                        <div style="float: left; width: 100%"><a href="javascript: void(0)" onclick="openCandidateProfilePopup('<%=candidateId%>','<%=recruitId %>','finalize', 'finalisation')" title="Click here for Finalisation"><i class="fa fa-play" aria-hidden="true"></i>Finalisation & Offer</a></div>
                                        <div style="float: left; width: 100%">
                                        <% if(hmHoldDetails!=null && uF.parseToInt(hmHoldDetails.get("HOLD_STATUS"))== -2) { %>
                                        	<a href="javascript: void(0);" onclick="offerHoldPopup('<%=candidateId %>','<%=recruitId %>', 'CANDIONHOLD_REASON');" title="This candidate is on hold"><i class="fa fa-play" aria-hidden="true"></i>Offer is on hold</a>
                                        <% } else { %>
                                        	<a href="javascript: void(0);" onclick="offerHoldPopup('<%=candidateId %>','<%=recruitId %>', 'CANDIONHOLD');" title="Click here for Hold Offer"><i class="fa fa-play" aria-hidden="true"></i>Hold Offer</a>
                                        <% } %>
                                        </div>
                                        <div style="float: left; width: 100%"><a href="javascript:void(0)" onclick="sendMailforBackgroundVerification('<%=recruitId%>','<%=candidateId %>');" title="Send mail to candidate for Background Verification Process"><i class="fa fa-play" aria-hidden="true"></i>Background Verification Process</a></div>
                                        <% } %>
                                    </td>
                                    <td style="text-align: left; vertical-align: text-top;">
                                        <%
                                            //System.out.println("hmPanelRatingCommentMap ----->> " + hmPanelRatingCommentMap);
                                            
                                            	count = 0;
                                             Set keys1 = hmRoundIds.keySet();
                                            Iterator it1 = keys1.iterator();
                                            while(it1.hasNext()) {
                                            	String roundId = (String)it1.next(); 
                                            	Map<String, String> panelUserIds = hmRoundIds.get(roundId);
                                            	//System.out.println("roundId rating ----->> " + roundId);
                                            	//System.out.println("panelUserIds rating ----->> " + panelUserIds);
                                            	Set keysEmpID = panelUserIds.keySet();
                                            	Iterator itEmpID = keysEmpID.iterator();
                                            	while(itEmpID.hasNext()) {	
                                            		String panelEmpId = (String)itEmpID.next();
                                            		if (count > 0) {
                                            %> <br style="line-height: 0px;"> <% } %>
                                        <div style="float: left;"><strong><%=hmEmpName.get(panelEmpId) %>:-</strong>  
                                            <%=uF.showData(hmPanelRatingCommentMap != null ? hmPanelRatingCommentMap.get(roundId +"_" + panelEmpId+"_COMMENT") : "", "")%>
                                        </div>
                                        <% count++;
                                            }
                                            } %>
                                    </td>
                                    
                                    <%-- <td style="text-align: left; vertical-align: text-top;">
                                        <%=uF.showData(hmCommentsHR.get(recruitId +"_"+ candidateId), "")%>
                                        </td> --%>
                                        
                                        <!-- added by Priyanka need to enable -->
                                     <%--  <td>
                                     	<a href="javascript:void(0)" onclick="sendMailforBackgroundVerification('<%=recruitId%>','<%=candidateId %>');"><i class="fa fa-plus-circle"></i>send Mail to candidate for Background Verification Process</a>&nbsp;&nbsp;
                                    </td> --%>
                                </tr>
                                <%
                                    //y++;
                                    }	%>
                                <% if (finalisedNameMap.keySet().size() == 0) { %>
                                <tr class="lighttable">
                                    <td colspan="7">
                                        <div class="nodata msg">
                                            <span>No Application Finalised</span>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                    } else{
                                    	if(y == 11){
                                    %>
                                <tr class="lighttable">
                                    <td colspan="7" style="text-align: right;"><a style="padding-right:5px" href="Applications.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                </tr>
                                <%}
                                    }
                                    %>
                            </tbody>
                        </table>
                       
                    </div>
                    <div class="attendance" style="margin-top: 20px;">
                        <%
                            Map<String, String> hmInnerRejectImage = hmRejectCandiImage.get(recruitId);
                            if (hmInnerRejectImage == null)hmInnerRejectImage = new HashMap<String, String>();
                            Map<String, String> rejectedNameMap = (Map<String, String>) hmRejectedName.get(recruitId+"_CNAME");
                            if (rejectedNameMap == null)rejectedNameMap = new HashMap<String, String>();
                            Iterator<String> itr1 = rejectedNameMap.keySet().iterator();
                            		
                            Map<String, String> rejectedNameStatusMap = (Map<String, String>) hmRejectedName.get(recruitId+"_CSTATUS");
                            if (rejectedNameStatusMap == null)rejectedNameStatusMap = new HashMap<String, String>();
                            %>
                        <h4>Rejected: (<%=uF.showData(hmRejectCount.get(recruitId), "0")%>)</h4>
                        <table width="100%" cellspacing="0" cellpadding="2" style="margin: 0px;" class="table table-bordered">
                            <tbody>
                                <tr class="darktable">
                                    <th style="text-align: center;">Name</th>
                                    <th style="text-align: center;">Experience</th>
                                    <th style="text-align: center;">Education</th>
                                    <th style="text-align: center;">Skills</th>
                                    <!-- <th style="text-align: center;">Schedule</th>
                                        <th style="text-align: center;">Panellist</th> -->
                                    <th style="text-align: center;">Round</th>
                                </tr>
                                <%
                                    int z = 1;
                                    	while (itr1.hasNext()) {
                                    		/* if(z == 11 && uF.parseToInt(strVm) == 0){
                                    			break;
                                    		} */
                                    		String candidateId = itr1.next();
                                    		String strStars = hmCandiStars.get(candidateId);
                                    		Map<String, String> hmSourceDetails = hmCandidate.get(candidateId);
                                    		if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                    %>
                                <tr class="lighttable">
                                    <td valign="top" style="text-align: left;">
                                        <div style="float: left; margin: 2px 10px 0px 0px">
                                            <!-- border: 1px solid #000; -->
                                            <%if(docRetriveLocation == null) { %>
                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateId %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmInnerImage.get(candidateId) %>" />
                                            <%} else { %>
                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateId %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+candidateId+"/"+IConstants.I_100x100+"/"+hmInnerRejectImage.get(candidateId)%>" />
                                            <%} %>
                                            <%-- <img height="100" width="100" class="lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="userImages/<%=hmInnerRejectImage.get(candidateId)%>" /> --%> 
                                        </div>
                                        <div style="float: left;">
                                        
                                   <!-- ===start parvez date: 17-01-2022=== -->     
                                            <%-- <%if(closeFlag){ %> --%>
                                            <%if(closeFlag && !isEnableHiringInCloseJd){ %>
                                   <!-- ===end parvez date: 17-01-2022=== -->         
                                            <%=rejectedNameMap.get(candidateId)%>
                                            <%} else { %>
                                            <%-- <a href="javascript: void(0)" onclick="openCandidateProfilePopup('<%=candidateId%>','<%=recruitId %>','reject')"> <%=rejectedNameMap.get(candidateId)%></a> --%>
                                            <a href="javascript: void(0)" onclick="<%if(hmCandToEmp.containsKey(candidateId.trim())){%>alert('<%=hmCandToEmp.get(candidateId.trim()) %>');<%} else {%>openCandidateProfilePopup('<%=candidateId%>','<%=recruitId %>','reject', '');<%}%>"><%=rejectedNameMap.get(candidateId)%></a>
                                            <%} %>
                                            <br/>
                                            <div style="line-height: 16px;">
                                                <span id="Reting<%=candidateId%>" style="margin-right: 5px; line-height: 18px;"><%=strStars %>/5</span>
                                                <span id="starPrimaryR<%=recruitId+"_"+candidateId %>" style="margin-left: 5px; line-height: 12px;"></span>
                                            </div>
                                            <script type="text/javascript">
                                                $('#starPrimaryR'+'<%=recruitId+"_"+candidateId %>').raty({
                                                      readOnly: true,
                                                      start:	<%=strStars %> ,
                                                      half: true,
                                                      targetType: 'number'
                                                });
                                            </script>
                                            <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Source:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_TYPE"),"-") %></div>
                                            <div style="font-size: 11px; line-height: 15px;"><strong style="color: gray;">Source Name:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_NAME"),"-") %></div>
                                        </div>
                                    </td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateExperience.get(candidateId), "No experience added")%></td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateEducation.get(candidateId), "No education added")%></td>
                                    <td style="text-align: left; vertical-align: text-top;"><%=uF.showData(hmCandidateSkill.get(candidateId), "No skill added")%></td>
                                    <td style="text-align: left; width: 30%; line-height: 21px;" valign="top">
                                        <%
                                            List<String> roundIdsRecruitwiseList = hmRoundIdsRecruitwise.get(recruitId);
                                            if(rejectedNameStatusMap.get(candidateId).equals("-1")) {
                                            for(int a=0; roundIdsRecruitwiseList!= null && a<roundIdsRecruitwiseList.size(); a++) {
                                            %> 
                                        <span style="font-weight: bold; color: gray;">Round <%=uF.showData(roundIdsRecruitwiseList.get(a), "0") %></span> 
                                        &nbsp;&nbsp;&nbsp;<%=uF.showData(hmPanelInterviewDates.get(recruitId+"_"+ roundIdsRecruitwiseList.get(a) +"_"+ candidateId),"No Date Added Yet") %>
                                        <%
                                            if(hmRoundStatus != null && hmRoundStatus.get(recruitId+"_"+candidateId+"_"+roundIdsRecruitwiseList.get(a)) != null && !hmRoundStatus.get(recruitId+"_"+candidateId+"_"+roundIdsRecruitwiseList.get(a)).equals("0")){ 
                                            //System.out.println("Interview Taken ........ ");
                                            %>
                                        <!-- <img src="images1/tick.png" style="height: 16px;"/> -->
                                        <i class="fa fa-check checknew" aria-hidden="true"></i>
                                        <%} %> 
                                        <br/>
                                        <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID")) > 0) { %>
                                        
                                <!-- ===start parvez date: 17-01-2022=== -->        
                                        <%-- <%if(closeFlag){ %> --%>
                                        <%if(closeFlag && !isEnableHiringInCloseJd){ %>
                                <!-- ===end parvez date: 17-01-2022=== -->        
                                        <div>
                                            <span style="color: gray;">Assessment:</span> &nbsp;
                                            <span>
                                            <%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_NAME"), "") %>
                                            </span>
                                            <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID"))) > 0) { %>
                                            <span id="assessScoreCard_<%=recruitId %><%=candidateId %>_<%=roundIdsRecruitwiseList.get(a) %>" style="margin-left: 7px;">
                                            <%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID")), "NA") %>%
                                            </span>
                                            <% } %>
                                        </div>
                                        <%} else { %>
                                        <div>
                                            <span style="color: gray;">Assessment:</span> &nbsp;
                                            <span>
                                            <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_NAME"), "") %></a>
                                            </span>
                                            <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID"))) > 0) { %>
                                            <span id="assessScoreCard_<%=recruitId %><%=candidateId %>_<%=roundIdsRecruitwiseList.get(a) %>" style="margin-left: 7px;">
                                            <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID") %>', '<%=candidateId%>', '<%=recruitId %>', '<%=roundIdsRecruitwiseList.get(a) %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateId+"_"+ roundIdsRecruitwiseList.get(a)+"_"+hmRoundAssessment.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)+"_ID")), "NA") %>%</a>
                                            </span>
                                            <% } %>
                                        </div>
                                        <!-- <br/> -->
                                        <% } %>
                                        <% } %>
                                        <%if(hmpanelNameRAndRwise.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)) !=null && !hmpanelNameRAndRwise.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)).isEmpty()){ %>
                                        <%=hmpanelNameRAndRwise.get(recruitId+"_"+roundIdsRecruitwiseList.get(a)) %>
                                        <%  }  %>
                                        <% if(a<(roundIdsRecruitwiseList.size()-1)) { %>
                                        <div style="border-bottom: 1px solid #CCCCCC;"></div>
                                        <% } %>
                                        <%  } } else { %>
                                        Application Rejected.
                                        <% } %>
                                    </td>
                                </tr>
                                <% //z++;
                                    }	%>
                                <% if (rejectedNameMap.keySet().size() == 0) { %>
                                <tr class="lighttable">
                                    <td colspan="7">
                                        <div class="nodata msg">
                                            <span>No Application Rejected</span>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                    } else {
                                    	if(z == 11) {
                                    %>
                                <tr class="lighttable">
                                    <td colspan="7" style="text-align: right;"><a style="padding-right:5px" href="Applications.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                </tr>
                                <% } } %>
                            </tbody>
                        </table>
                    </div>
                </div>
               </div> 
             
                <%-- <h3 class="box-title" style="width: 96%; font-size: 14px;">
                    <div class="heading_dash <%=priorityClass %>" style="float:left;width: 100%;">
                        <!-- border:solid 1px red; -->
                        <div style="width:25%; float:left;"><a href="javascript:void(0)" onclick="viewProfileReport(<%=recruitId%>)">
                            <%=uF.showData(hmJobCodeName.get(recruitId), "N/a")%></a> (<%=uF.showData(hmJobTitle.get(recruitId), "-")%>)
                            </div>
                        <div style="width:50%; float:left;">
                            Total Applications(<%=uF.showData(applyMp.get(recruitId), "0")%>),
                            Shortlisted(<%=uF.showData(hmSelectCount.get(recruitId), "0")%>),
                            Finalisation(<%=uF.showData(hmFinalCount.get(recruitId), "0")%>),
                            Rejected(<%=uF.showData(hmRejectCount.get(recruitId), "0")%>)
                        </div>
                        <%
                            if(!closeFlag){
                            if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) {
                            %>
                        <div style="float:right; padding-right:10px;"><img border="0" style="padding: 9px 20px 9px ; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" title="Close Job" onclick="if(confirm('Are you sure, you want to close the job?')) window.location='CloseJobPublish.action?S=close&RID=<%=recruitId%>';"/>
                            <a onclick="if(confirm('Are you sure, you want to close the job?')) window.location='CloseJobPublish.action?S=close&RID=<%=recruitId%>';"
                                href="javascript:void(0)"><u>Click Here to Close This Job</u></a>
                            <a onclick="closeJob('<%=recruitId%>');" href="javascript:void(0)"><u>Click Here to Close This Job</u></a>			
                        </div>
                        <% }
                            } %>
                    </div>
                </h3> --%>
                <!-- <div class="box-tools pull-right">
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                </div> -->
            
    <% } %>
    <%
        if (recruitmentIdList.size() == 0) {
        %>
    <div class="nodata msg">
        <span>No Data Available.</span>
    </div>
    <% } %>
</div>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        Modal content
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<!-- <div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog modal-dialog1">
        Modal content
        <div class="modal-content">
            <div class="modal-header">
            	<button type="button" class="close1" id="close1" data-dismiss="modal">&times;</button>
                <h4 class="modal-title modal-title1"></h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div> -->