<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
 <!-- created by seema  Added scrollbox and scrollbox-content css -->
<head>
<style type="text/css">
.scrollbox {
  max-height: 250px;
  overflow: auto;
  visibility: hidden;
}

.scrollbox-content,
.scrollbox:hover,
.scrollbox:focus {
  visibility: visible;
}
</style>
</head>
 <!-- created by seema  Added scrollbox and scrollbox-content css -->
<script type="text/javascript" src="scripts/customAjax.js"></script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 <!-- created by seema  -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.9.2/html2pdf.bundle.js"></script>
<!-- created by seema  -->
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>


	.emps {
		text-align:center;
		font-size: 26px;
		color: #3F82BF;/* none repeat scroll 0 0 #3F82BF */
		font-family: digital;
		font-weight: bold;
	}
	
	.anaAttrib1 {
		font-size: 14px;
		font-family: digital;
		color: #3F82BF;
		font-weight: bold;
	}
</style>


<% UtilityFunctions uF = new UtilityFunctions();%>

<script type="text/javascript">
 

	jQuery(document).ready(function() {
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
		
		$("body").on('click','#closeButton1',function(){
			$(".modal-dialog1").removeAttr('style');
			$("#modal-body1").height(400);
			$("#modalInfo1").hide();
	    });
		
	});


function takeLearning(planId){
	
 	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$(".modal-title").html('View Learning');
	$.ajax({
		url : "TakeLearningPlan.action?planId="+planId ,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function viewtraining(id){
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$(".modal-title").html('My Training Notes');
	$.ajax({
		url : "TrainingStatus.action?mode=view&planId="+id,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addTrainingStatus(trainingId, lPlanId) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$(".modal-title").html('Training Status');
	$.ajax({
		url : "TrainingStatus.action?fromPage=MyHR&trainingId="+trainingId+"&lPlanId="+lPlanId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addTakeAssessment(assessmentId, lPlanId,empId,userType,currLvl,role) {
	//alert("assessmentId==>"+assessmentId+"==>lPlanId==>"+lPlanId+"==>empId==>"+empId+"==>role==>"+role+"==>userType==>"+userType);
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$(".modal-title").html('Assessment Questions');
	$.ajax({
		//url : "IFrameTakeAssessment.action?assessmentId="+assessmentId+"&lPlanId="+lPlanId,
		url : "TakeAssessment1.action?assessmentId="+assessmentId+"&lPlanId="+lPlanId+"&empID="+empId+
				"&userType="+userType+"&currentLevel="+currLvl+"&role="+role,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
	
function addTrainingFeedback(trainingId, lPlanId) {
//alert(trainingId+" " +lPlanId );
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	 $(".modal-title").html('Training Feedback Questions');
	 $.ajax({
			url : "AddTrainingFeedbackPopup.action?fromPage=MLP&trainingId="+trainingId+"&lPlanId="+lPlanId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}

function addCourseReadStatus(courseId, lPlanId, courseName) {

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 /* var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width); */
	 $(".modal-title").html(''+courseName+' Status');
	 $.ajax({
			url : "CourseReadStatusUpdatePopup.action?courseId="+courseId+"&lPlanId="+lPlanId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}


	function viewCourseForRead(courseId, courseName) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	/* ===start parvez date: 15-02-2023=== */	 
		 $(".modal-body").height(500);
	/* ===end parvez date: 15-02-2023=== */
		 $("#modalInfo").show();
		 var height = $(window).height()* 0.95;
		var width = $(window).width()* 0.95;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
		 $(".modal-title").html(''+courseName);
		 $.ajax({
			url : "ViewCourseDetails.action?courseId="+courseId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	
function courseRead(courseId, lPlanId, count) {
	//alert(courseId + " lPlanId " + lPlanId + " count " + count);
	if(confirm("Are you sure, you want to read this course?")) {
	var action = "CourseRead.action?courseId=" + courseId + "&lPlanId=" + lPlanId;
	//var action = "GetCandidateEmployeeList.action?grade="+value+"&empSelection="+empSelection;
	getContent('courseActionDiv'+count, action);
	}
}

/* ===start parvez date: 23-09-2021=== */
	function seeVideo(videoId, lPlanId, count, videoName, fromPage) {
		//alert(videoId + " lPlanId " + lPlanId + " count " + count);
		if(confirm("Are you sure, you want to see this video?")) {
			//var action = "ViewVideo.action?videoId=" + videoId + "&lPlanId=" + lPlanId; 
			//var action = "ViewVideo.action?dataType=V&videoId=" + videoId + "&lPlanId=" + lPlanId;
			//var action = "GetCandidateEmployeeList.action?grade="+value+"&empSelection="+empSelection;
			showVideo(videoId,videoName,fromPage,lPlanId);
			//getContent('videoActionDiv'+count, action);
		}
	}
	
	function showVideo(videoId, videoName,fromPage,lPlanId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		var height = $(window).height()* 0.95;
		var width = $(window).width()* 0.95;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
		$(".modal-title").html(''+videoName);
		$.ajax({
			url : "LearningVideoDetails.action?learningVideoId="+videoId+"&fromPage="+fromPage+"&lPlanId="+lPlanId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
/* ===end parvez date: 23-09-2021=== */


function trainingAttend(trainingId, lPlanId, count) {
	//alert(trainingId + " lPlanId " + lPlanId + " count " + count);
	if(confirm("Are you sure, you want to attend this training?")) {
		var action = "TrainingAttend.action?trainingId=" + trainingId + "&lPlanId=" + lPlanId;
		//var action = "GetCandidateEmployeeList.action?grade="+value+"&empSelection="+empSelection;
		getContent('trainingActionDiv'+count, action);
	}
}


function getFeedBackData(empId,planId,type) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	 $(".modal-title").html('Feedback');
	 $.ajax({
			url : "FeedBackDetails.action?empid="+empId+"&planId="+planId+"&type="+type,
			cache : false,
			success : function(data) {
					$(dialogEdit).html(data);
			}
		});
} 

function showStageDetails(val, stageSize) {
	//alert(val+"  "+ stageSize);
	var status = document.getElementById("hideStageType"+val).value;
	if(status == 0){
		document.getElementById('livename'+val).rowSpan = parseInt(stageSize)+1;
		for(var a=0; a<stageSize; a++){
			document.getElementById("liveTR"+val+"_"+a).style.display = "table-row";
		}
		document.getElementById("hideStageType"+val).value = "1";
		//alert("csdfc sdvfdsf");
		document.getElementById("MLuparrowSpan"+val).style.display = "block";
		//alert("csdfc sdvfdsf 111111111");
		document.getElementById("MLdownarrowSpan"+val).style.display = "none";
		//alert("csdfc sdvfdsf 22222 ");
	} else {
		document.getElementById('livename'+val).rowSpan = 1;
		for(var a=0; a<stageSize; a++){
			document.getElementById("liveTR"+val+"_"+a).style.display = "none";
		}
		document.getElementById("hideStageType"+val).value = "0";
		document.getElementById("MLuparrowSpan"+val).style.display = "none";
		document.getElementById("MLdownarrowSpan"+val).style.display = "block";
	}
}

function showPrevStageDetails(val, stageSize) {
	//alert(val+"  "+ stageSize);
	var status = document.getElementById("hidePrevStageType"+val).value;
	if(status == 0){
		document.getElementById('prevname'+val).rowSpan = parseInt(stageSize)+1;
		for(var a=0; a<stageSize; a++){
			document.getElementById("prevTR"+val+"_"+a).style.display = "table-row";
		}
		document.getElementById("hidePrevStageType"+val).value = "1";
		//alert("csdfc sdvfdsf");
		document.getElementById("MLPrevuparrowSpan"+val).style.display = "block";
		//alert("csdfc sdvfdsf 111111111");
		document.getElementById("MLPrevdownarrowSpan"+val).style.display = "none";
		//alert("csdfc sdvfdsf 22222 ");
	} else {
		document.getElementById('prevname'+val).rowSpan = 1;
		for(var a=0; a<stageSize; a++){
			document.getElementById("prevTR"+val+"_"+a).style.display = "none";
		}
		document.getElementById("hidePrevStageType"+val).value = "0";
		document.getElementById("MLPrevuparrowSpan"+val).style.display = "none";
		document.getElementById("MLPrevdownarrowSpan"+val).style.display = "inline";
	}
}


function showLPlanReason(lPlanReason) {
	//alert("lPlanReason ===> "+ lPlanReason)
	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
 		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
 		 $("#modalInfo").show();
 		var height = $(window).height()* 0.95;
 		var width = $(window).width()* 0.95;
 		$(".modal-dialog").css("height", height);
 		$(".modal-dialog").css("width", width);
 		$(".modal-dialog").css("max-height", height);
 		$(".modal-dialog").css("max-width", width);
 		 $(".modal-title").html('Learning Plan Reason');
 		$.ajax({
			url : "LearningPlanReasonPopup.action?lPlanReason="+lPlanReason,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}


	function openLearningPreview(planId) {

	 var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
 		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
 		 $("#modalInfo").show();
 		 if($(window).width() >= 900){
 			 $('.modal-dialog').width(900);
 		 }else{
 			 $(".modal-dialog").removeAttr('style');
 		 }
 		 
 		 $(".modal-title").html('Learning Summary');
 		$.ajax({
			url : "LearningPlanPreview.action?planId="+planId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
function viewCertificate(strEmpId,planId) { 
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	 $(".modal-title").html('Certificate');
	 $.ajax({
			url : "ViewEmpCertificate.action?strEmpId="+strEmpId+"&planId="+planId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
function getTakeAssessmentPreview(assessmentId, lPlanId, empId, userType) {
 	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.95;
	var width = $(window).width()* 0.95;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$(".modal-title").html('Assessment Summary');
	$.ajax({
		url : 'TakeAssessmentSummary.action?assessmentId='+assessmentId+'&lPlanId='+lPlanId+'&empID='+empId+'&userType='+userType,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

/* created by seema */
<%if(request.getParameter("download")!=null){%>
       setTimeout(() => {
    	   var hrdata = this.document.getElementById("downloadPDF");
    	      // console.log(hrdata);
    	       //console.log(window);
    	       var opt = {
    	           margin: 1,
    	           filename: 'MyLearningPlan.pdf',
    	           image: { type: 'jpeg', quality: 0.98 },
    	           html2canvas: { scale: 2 },
    	           jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
    	       };
    	       html2pdf().from(hrdata).set(opt).save();
	   }, 3000);
       
       <%}%>

/* created by seema */
 
 /* created by parvez date: 27-09-2021
 *	start 
 */
 	function  getLearningDataData(strAction,dataType,fromPage){
    	$("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  	 	$.ajax({ 
	  		type : 'POST',
	  		url: strAction+'.action?fromPage='+fromPage+'&dataType='+dataType,
	  		cache: true,
	  		success: function(result){ 
	  			$("#divMyHRData").html(result);
	     	}
	  	});
    }

 /* end */
 
 /* ===start parvez date: 30-09-2021=== */
	 function requestForLearning(learningPlanId,fromPage,lPlanName){
	 	/* if(confirm("Are you sure, you want to Nominate yourself for "+planName+"?")) {
			var action = "ApplyLearningRequest.action?planId="+planId;
		
			//getContent('learningCalDiv', action);
		} */
	 
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		/* var height = $(window).height()* 0.95;
		var width = $(window).width()* 0.95;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
		$(".modal-title").html('Learning Request'); */
		$.ajax({
			url : "LearningRequestPopUp.action?planId="+learningPlanId+"&fromPage="+fromPage+"&planName="+lPlanName,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
 	}
 
	 function getApprovalStatus(lPlan_id){
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$('.modal-title').html('Work flow');
			$("#modalInfo").show();
			$.ajax({
				url : "GetLRApprovalStatus.action?effectiveid="+lPlan_id,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
 /* ===end parvez date: 30-09-2021=== */
       
</script>

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	String empID = (String) session.getAttribute(IConstants.EMPID);

	Map<String,String> hmLearnerTotal=(Map<String,String>)request.getAttribute("hmLearnerTotal");
	Map<String, List<List<String>>> hmStageType = (Map<String, List<List<String>>>)request.getAttribute("hmStageType");
	Map<String, String> hmTrainingStatus = (Map<String, String>) request.getAttribute("hmTrainingStatus");
	Map<String, String> hmTrainingAttendStatus = (Map<String, String>) request.getAttribute("hmTrainingAttendStatus");
	Map<String, String> hmTrainingCompleteStatus = (Map<String, String>) request.getAttribute("hmTrainingCompleteStatus");
	Map<String, String> hmStageTypeId = (Map<String, String>) request.getAttribute("hmStageTypeId");
	
	Map<String, String> hmcourseReadStatus = (Map<String, String>) request.getAttribute("hmcourseReadStatus");
	Map<String, String> hmChapterCount = (Map<String, String>) request.getAttribute("hmChapterCount");
	Map<String, String> hmChapterReadCount = (Map<String, String>) request.getAttribute("hmChapterReadCount");
	Map<String, String> hmAssessmentTakePercent = (Map<String, String>) request.getAttribute("hmAssessmentTakePercent");
	Map<String, String> hmAssessmentRating = (Map<String, String>) request.getAttribute("hmAssessmentRating");
	Map<String, String> hmCourseName = (Map<String, String>) request.getAttribute("hmCourseName");
	
	List<String> alCloseLearnPlan = (List<String>)request.getAttribute("alCloseLearnPlan");
	if(alCloseLearnPlan == null) alCloseLearnPlan = new ArrayList<String>();
	
	Map<String, String> hmAssessmentAttempt = (Map<String, String>) request.getAttribute("hmAssessmentAttempt");
	if(hmAssessmentAttempt==null)hmAssessmentAttempt = new HashMap<String, String>();
	Map<String, String> hmAssessmentTaken = (Map<String, String>) request.getAttribute("hmAssessmentTaken");
	if(hmAssessmentTaken==null)hmAssessmentTaken = new HashMap<String, String>();
	List<String> alAssessmentFinish = (List<String>) request.getAttribute("alAssessmentFinish");
	if(alAssessmentFinish == null) alAssessmentFinish = new ArrayList<String>();
	
	//===start parvez date: 23-09-2021===
	Map<String, String> hmVideoSeenStatus = (Map<String, String>) request.getAttribute("hmVideoSeenStatus");
	
	Map<String, String> hmVideoName = (Map<String, String>) request.getAttribute("hmVideoName");
	String fromPage = (String) request.getAttribute("fromPage");
	//System.out.println(fromPage);
	
	Map<String, String> hmVideoCount = (Map<String, String>) request.getAttribute("hmVideoCount");
	Map<String, String> hmVideoViewedCount = (Map<String, String>) request.getAttribute("hmVideoViewedCount");
	
	//===end parvez date: 23-09-2021===
%>

<section class="content">
<!-- ===start parvez date: 27-09-2021=== -->
<%-- <div class="row jscroll">
	<section class="col-lg-12 connectedSortable"> --%>
	<div class="box box-none nav-tabs-custom">
		<ul class="nav nav-tabs">
			<%
               String dataType = (String) request.getAttribute("dataType");
               if(dataType != null && dataType.equals("L")) { %>
			<li class="active"><a href="javascript:void(0)" style="padding: 5px 10px;" onclick="getLearningDataData('MyLearningPlan','L','<%=fromPage%>')" data-toggle="tab">Live</a>
			</li>
			<li><a href="javascript:void(0)" style="padding: 5px 10px;" onclick="getLearningDataData('MyLearningPlan','LC','<%=fromPage%>')" data-toggle="tab">Learning Calendar</a>
			</li>
			<% } else if(dataType != null && dataType.equals("LC")) { %>
			<li><a href="javascript:void(0)" style="padding: 5px 10px;" onclick="getLearningDataData('MyLearningPlan','L','<%=fromPage%>')" data-toggle="tab">Live</a>
			</li>
			<li class="active"><a href="javascript:void(0)" style="padding: 5px 10px;" onclick="getLearningDataData('MyLearningPlan','LC','<%=fromPage%>')" data-toggle="tab">Learning Calendar</a>
			</li>
			<% } %>
		</ul>
<!-- ===end parvez date: 27-09-2021=== -->
    <!-- created by seema -->
    <div id="downloadPDF" class="row jscroll">
    <!-- <div id="downloadPDF" class="box-body"> -->
    <!-- created by seema -->
    <% if(dataType != null && dataType.equals("L")) { %>
        <section class="col-lg-12 connectedSortable" style="padding-top: 10px;">
            <div class="box box-primary">
                <div class="box-header with-border" data-widget="collapse-full">
                    <h3 class="box-title"> My Live Learnings</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                 <!-- created by seema  Added scrollbox and scrollbox-content css -->
                <div class="scrollbox box-body" style="padding: 5px;">
                    <div class="scrollbox-content contentLive" style="padding: 20px; height:auto;">
                  <!-- created by seema  Added scrollbox and scrollbox-content css -->
                        <div class="attendance">
                            <table  class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin:10px 0px 25px 0px;">
                                <tbody>
                                    <tr class="darktable">
                                        <td style="text-align: center; width: 190px;">Plan Name</td>
                                        <td style="text-align: center; width: 110px;">Type</td>
                                        <td style="text-align: center; width: 100px;">Certificate</td>
                                        <td style="text-align: center; width: 60px;">Start Date</td>
                                        <td style="text-align: center; width: 60px;">End Date</td>
                                        <td style="text-align: center; width: 140px;">Created By</td>
                                        <td style="text-align: center; width: 120px;">Reason for</td>
                                        <td style="text-align: center; width: 90px;">Status</td>
                                        <td style="text-align: center; width: 100px;">Actions</td>
                                        <td style="text-align: center; width: 120px;">Rating</td>
                                        <td style="text-align: center; width: 140px;">Certificates/Thumbs up</td>
                                    </tr>
                                    <%
                                        Map<String, String> hmLearningPlanAttend = (Map<String, String>) request.getAttribute("hmLearningPlanAttend");
                                        //System.out.println("MLP.jsp/505--hmLearningPlanAttend="+hmLearningPlanAttend);
                                        List<List<String>> alLiveLearnings = (List<List<String>>) request.getAttribute("alLiveLearnings");
                                        //System.out.println("MLP.jsp/505--alLiveLearnings="+alLiveLearnings);
                                        if(alLiveLearnings == null) alLiveLearnings = new ArrayList<List<String>>();
                                        
                                        int i = 0;
                                        //System.out.println("MLP.jsp/636--alLiveLearnings="+alLiveLearnings);
                                        for (List<String> alInner : alLiveLearnings) {
                                        %>
                                    <tr class="lighttable">
                                        <%
                                            String rowspn = "1";
                                            	List<List<String>> stagesList = hmStageType.get(alInner.get(0));
                                            	if(stagesList == null) stagesList = new ArrayList<List<String>>();
                                            	
                                            	int rwspn = 1;
                                            	/* if(alInner.get(2) != null && !alInner.get(2).equals("Training") && !alInner.get(2).equals("Course") && !alInner.get(2).equals("Assessment")) { 
                                            		rwspn = stagesList != null ? stagesList.size()+1 : 1;
                                            	} */
                                            	if(alInner.get(2) != null && !alInner.get(2).equals("Training") && !alInner.get(2).equals("Course") && !alInner.get(2).equals("Assessment") && !alInner.get(2).equals("Video")) { 
                                            		rwspn = stagesList != null ? stagesList.size()+1 : 1;
                                            	}
                                            	int stageSize = stagesList.size();
                                            	%>
                                        <td id="livename<%=i %>" style="text-align: left; vertical-align: top;" nowrap>
                                            <a href="javascript: void(0)" onclick="openLearningPreview('<%=alInner.get(0) %>')"><%=alInner.get(1)%> </a>
                                            <%
                                                if(hmLearningPlanAttend != null && uF.parseToInt(hmLearningPlanAttend.get(alInner.get(0))) == 0) { %>
                                            <img border="0" src="<%=request.getContextPath()%>/images1/icons/news_icon.gif"/>
                                            <% } %>
                                        </td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>
                                            <span style="float: left;"><%=alInner.get(2)%></span>
                                            <%-- <%if(alInner.get(2) != null && !alInner.get(2).equals("Training") && !alInner.get(2).equals("Course") && !alInner.get(2).equals("Assessment")) { %> --%>
                                            <%if(alInner.get(2) != null && !alInner.get(2).equals("Training") && !alInner.get(2).equals("Course") && !alInner.get(2).equals("Assessment") && !alInner.get(2).equals("Video")) { %>
                                            <input type="hidden" name="hideStageType" id="hideStageType<%=i %>" value = "0"/>
                                            <a href="javascript: void(0);" onclick="showStageDetails('<%=i %>','<%=stageSize %>');">
                                            <span id="MLdownarrowSpan<%=i %>">
                                            &nbsp;&nbsp;
                                            <i class="fa fa-angle-down" aria-hidden="true" style="width: 12px;margin-top: -2px;"></i>
                                            </span>
                                            <span id="MLuparrowSpan<%=i %>" style="display: none;">
                                            &nbsp;&nbsp;<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i> 
                                            </span>
                                            </a> <!-- ,this.parentNode.parentNode.rowIndex -->
                                            <% } %>
                                        </td>
                                        <td style="text-align: left; vertical-align: top;" nowrap><%=alInner.get(3)%></td>
                                        <td style="text-align: left; vertical-align: top;" nowrap><%=alInner.get(4)%></td>
                                        <td style="text-align: left; vertical-align: top;" nowrap><%=alInner.get(5)%></td>
                                        <td style="text-align: left; vertical-align: top;" nowrap><%=alInner.get(6)%></td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>
                                            <a href="javascript:void(0);" onclick="showLPlanReason('<%=alInner.get(7)%>');"><%=alInner.get(7).substring(0, alInner.get(7).length()>9 ? 9 : alInner.get(7).length()) %>...</a>
                                        </td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>
                                            <div id="<%=i %>liveStatusBarDiv" style="width: 92%; margin: 0px 0px 0px 3px;">
                                                <%-- <div class="anaAttrib1"><span style="margin-left:<%=uF.parseToDouble("0") > 97 ? uF.parseToDouble("0")-6 : uF.parseToDouble("0")-3 %>%;"><%=0 %>%</span></div> --%>
                                                <% if(alInner.get(2) != null && alInner.get(2).equals("Training")) { 
                                                    //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
                                                    double trainingStatus  = 0;
                                                    if(uF.parseToDouble(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0))) > 100) {
                                                    	trainingStatus = 100;
                                                    } else {
                                                    	trainingStatus = uF.parseToDouble(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0)));
                                                    }
                                                    %>
                                                <div class="anaAttrib1" style="text-align: center;"><%-- <span style="margin-left:<%=uF.parseToInt(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0))) > 94 ? uF.parseToInt(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0)))-6 : uF.parseToInt(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0)))-2.5 %>%;"><%=hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0))%>%</span> --%>
                                                    <%=uF.formatIntoOneDecimalWithOutComma(trainingStatus) %>%
                                                </div>
                                                <div id="outbox">
                                                    <%if(trainingStatus < 33.33){ %>
                                                    <div id="redbox" style="width: <%=trainingStatus %>%;"></div>
                                                    <%}else if(trainingStatus >= 33.33 && trainingStatus < 66.67){ %>
                                                    <div id="yellowbox" style="width: <%=trainingStatus %>%;"></div>
                                                    <%}else if(trainingStatus >= 66.67){ %>
                                                    <div id="greenbox" style="width: <%=trainingStatus %>%;"></div>
                                                    <%} %>
                                                </div>
                                                <% } else if(alInner.get(2) != null && alInner.get(2).equals("Course")) { 
                                                    //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
                                                    	String chapterCount = hmChapterCount.get(alInner.get(8));
                                                    	String chapterReadCount = hmChapterReadCount.get(alInner.get(8)+"_"+alInner.get(0));
                                                    	double readPercant = 0;
                                                    
                                                    	if(chapterReadCount != null  && chapterCount != null ) {
                                                    		readPercant = (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);	
                                                    	}
                                                    	
                                                    	if(readPercant > 100) {
                                                    		readPercant = 100;
                                                    	}
                                                    	
                                                    %>
                                                <div class="anaAttrib1" style="text-align: center; "><%=uF.formatIntoOneDecimalWithOutComma(readPercant) %>%</div>
                                                <div id="outbox">
                                                    <%if(readPercant < 33.33){ %>
                                                    <div id="redbox" style="width: <%=readPercant %>%;"></div>
                                                    <%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
                                                    <div id="yellowbox" style="width: <%=readPercant %>%;"></div>
                                                    <%}else if(readPercant >= 66.67){ %>
                                                    <div id="greenbox" style="width: <%=readPercant %>%;"></div>
                                                    <%} %>
                                                </div>
                                                <% } else if(alInner.get(2) != null && alInner.get(2).equals("Assessment")) { %>
                                                <div class="anaAttrib1" style="text-align: center; "><%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) < 100 ? uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) : 100) %>%</div>
                                                <div id="outbox">
                                                    <%if(uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) < 33.33){ %>
                                                    <div id="redbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) %>%;"></div>
                                                    <%}else if(uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) >= 33.33 && uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) < 66.67){ %>
                                                    <div id="yellowbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) %>%;"></div>
                                                    <%}else if(uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) >= 66.67){ %>
                                                    <div id="greenbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) < 100 ? uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) : "100" %>%;"></div>
                                                    <%} %>
                                                </div>
                                <!-- ===start parvez date: 23-09-2021=== -->
                                                <% } else if(alInner.get(2) != null && alInner.get(2).equals("Video")) { 
                                                    //System.out.println("MLP.jsp/745--alInner="+alInner.get(8));
                                                	/* String videoStatus = hmVideoSeenStatus.get(alInner.get(8)+"_"+alInner.get(0));
                                                	double readPercant = 0;
                                                
                                                	if(videoStatus != null &&  uF.parseToInt(videoStatus) == 1) {
                                                		readPercant = 100;	
                                                	} */
                                                	
                                                	String videoCount = hmVideoCount.get(alInner.get(8));
                                                	String videoViewedCount = hmVideoViewedCount.get(alInner.get(8)+"_"+alInner.get(0));
                                                	double readPercant = 0;
                                                	
                                                	if(videoViewedCount != null  && videoCount != null ) {
                                                		readPercant = (uF.parseToDouble(videoViewedCount)* 100) / uF.parseToDouble(videoCount);	
                                                	}
                                                	
                                                	if(readPercant > 100) {
                                                		readPercant = 100;
                                                	}
                                                	
                                                %>
                                            <div class="anaAttrib1" style="text-align: center; "><%=uF.formatIntoOneDecimalWithOutComma(readPercant) %>%</div>
                                            <div id="outbox">
                                                <%if(readPercant < 33.33){ %>
                                                <div id="redbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=readPercant %>%;"></div>
                                                <%} %>
                                                <%-- <%if(readPercant == 100){ %>
                                                <div id="greenbox" style="width: <%=readPercant %>%;"></div>
                                                <%} %> --%>
                                            </div>
                                            <% } else {
                                   /* ===end parvez date: 23-09-2021=== */
                                                    double trainingPercent = 0;
                                                    double coursePercent = 0;
                                                    double assessPercent = 0;
                                                    double videoPercent = 0;	//add by parvez date: 23-09-2021
                                                    int count = 0;
                                                    double avgPercent = 0;
                                                    
                                                    for(int a=0; stagesList != null && !stagesList.equals("") && a<stagesList.size(); a++) {
                                                    	List<String> innerList = stagesList.get(a);
                                                    	
                                                    	if(innerList.get(4) != null && innerList.get(4).equals("Training")) { 
                                                    		trainingPercent += uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0)));												
                                                    			count++;
                                                    		} else if(innerList.get(4) != null && innerList.get(4).equals("Course")) { 
                                                    			String chapterCount = hmChapterCount.get(innerList.get(7));
                                                    			String chapterReadCount = hmChapterReadCount.get(innerList.get(7)+"_"+alInner.get(0));
                                                    			coursePercent += (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);
                                                    			count++;
                                                    		} else if(innerList.get(4) != null && innerList.get(4).equals("Assessment")) {
                                                    			assessPercent += uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7)));
                                                    			count++;
                                                    		/* ===start parvez date: 21-10-2021=== */
                                                    		} else if(innerList.get(4) != null && innerList.get(4).equals("Video")) {
                                                    			
                                                    			/* if(uF.parseToInt(hmVideoSeenStatus.get(innerList.get(7)+"_"+innerList.get(5))) == 1){
                                                    				videoPercent = 100;
                                                    			}else{
                                                    				videoPercent = 0;
                                                    			} */
                                                    			
                                                    			String videoCount = hmVideoCount.get(innerList.get(7));
                                                    			String videoViewedCount = hmVideoViewedCount.get(innerList.get(7)+"_"+alInner.get(0));
                                                    			videoPercent += (uF.parseToDouble(videoViewedCount)* 100) / uF.parseToDouble(videoCount);
                                                    			count++;
                                                    			
                                                    		}
                                                    	/* ===end parvez date: 21-10-2021=== */
                                                    	
                                                    	}
                                                    if(count > 0){
                                                    	//===start parvez date: 23-09-2021===
                                                    	//avgPercent = (trainingPercent + coursePercent + assessPercent) / count;
                                                    	avgPercent = (trainingPercent + coursePercent + assessPercent+videoPercent) / count;
                                                    	//===end parvez date: 23-09-2021===
                                                    } %>
                                                <div class="anaAttrib1" style="text-align: center; "><%=avgPercent < 100 ? uF.formatIntoOneDecimalWithOutComma(avgPercent) : "100" %>%</div>
                                                <div id="outbox">
                                                    <%if(avgPercent < 33.33){ %>
                                                    <div id="redbox" style="width: <%=avgPercent %>%;"></div>
                                                    <%}else if(avgPercent >= 33.33 && avgPercent < 66.67){ %>
                                                    <div id="yellowbox" style="width: <%=avgPercent %>%;"></div>
                                                    <%}else if(avgPercent >= 66.67){ %>
                                                    <div id="greenbox" style="width: <%=avgPercent < 100 ? avgPercent : "100" %>%;"></div>
                                                    <%} %>
                                                </div>
                                                <%
                                                    }	
                                                    %>
                                            </div>
                                        </td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>
                                            <%if(alCloseLearnPlan.contains(alInner.get(0).trim())){ %>
                                            <span style="color: red;">Closed</span>
                                            <%}else{ %>
                                            <% if(alInner.get(2) != null && alInner.get(2).equals("Training")) { %>
                                            <% if(hmTrainingAttendStatus.get(alInner.get(8)+"_"+alInner.get(0)) == null) { %>
                                            <div id="trainingActionDiv<%=i %>"><a onclick="trainingAttend('<%=alInner.get(8) %>','<%=alInner.get(0)%>','<%=i %>');" href="javascript:void(0);"> Attend</a></div>
                                            <% } else if(hmTrainingAttendStatus.get(alInner.get(8)+"_"+alInner.get(0)) != null && hmTrainingAttendStatus.get(alInner.get(8)+"_"+alInner.get(0)).equals("1") 
                                                && hmTrainingCompleteStatus.get(alInner.get(8)+"_"+alInner.get(0)) == null) { %> Attending
                                            <%-- <div id="trainingActionDiv<%=i %>"><a onclick="addTrainingStatus('<%=alInner.get(8) %>','<%=alInner.get(0)%>');" href="javascript:void(0)">Update</a></div> --%>
                                            <% } else if(hmTrainingCompleteStatus.get(alInner.get(8)+"_"+alInner.get(0)) != null && hmTrainingCompleteStatus.get(alInner.get(8)+"_"+alInner.get(0)).equals("1")) { %>
                                            <div id="trainingActionDiv<%=i %>"><a onclick="addTrainingFeedback('<%=alInner.get(8)%>','<%=alInner.get(0)%>');" href="javascript:void(0)">Feedback</a></div>
                                            <%-- <div id="trainingActionDiv<%=i %>"><a onclick="" href="javascript:void(0)">Feedback</a></div> --%>
                                            <% } else { %>
                                            <% } %>
                                            <% } else if(alInner.get(2) != null && alInner.get(2).equals("Course")) { 
                                                //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
                                                %>
                                            <% if(hmcourseReadStatus.get(alInner.get(8)+"_"+alInner.get(0)) == null) { %>
                                            <div id="courseActionDiv<%=i %>"><a onclick="courseRead('<%=alInner.get(8)%>','<%=alInner.get(0)%>','<%=i %>');" href="javascript:void(0);"> Read</a></div>
                                            <% } else if(hmcourseReadStatus.get(alInner.get(8)+"_"+alInner.get(0)) != null && hmcourseReadStatus.get(alInner.get(8)+"_"+alInner.get(0)).equals("1")) { %>
                                            <div id="courseActionDiv<%=i %>">
                                                <a onclick="addCourseReadStatus('<%=alInner.get(8)%>','<%=alInner.get(0)%>','<%=hmCourseName.get(alInner.get(8)) %>');" href="javascript:void(0)">Update</a> 
                                                | <a onclick="viewCourseForRead('<%=alInner.get(8)%>','<%=hmCourseName.get(alInner.get(8)) %>');" href="javascript:void(0)">Reading</a>
                                            </div>
                                            <% } else if(hmTrainingCompleteStatus.get(alInner.get(8)+"_"+alInner.get(0)) != null && hmTrainingCompleteStatus.get(alInner.get(8)+"_"+alInner.get(0)).equals("1")) { %>
                                            <%-- <div id="courseActionDiv<%=i %>"><a onclick="addTrainingFeedback('<%=innerList.get(7)%>','<%=alInner.get(0)%>');" href="javascript:void(0)">Feedback</a></div> --%>
                                            <% } else { %>
                                            <% } %> 
                                            <% } else if(alInner.get(2) != null && alInner.get(2).equals("Assessment")) { 
                                                
                                                //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
                                                
                                                int nAttempt = uF.parseToInt(hmAssessmentAttempt.get(alInner.get(8)));
                                                int nTaken = uF.parseToInt(hmAssessmentTaken.get(alInner.get(0)+"_"+alInner.get(8)));
                                                if(nTaken < nAttempt && !alAssessmentFinish.contains(alInner.get(0)+"_"+alInner.get(8))){
                                                %>
					
	                                            <a onclick="addTakeAssessment('<%=alInner.get(8) %>','<%=alInner.get(0)%>','<%=request.getAttribute("empID") %>','<%=request.getAttribute("userType") %>','<%=request.getAttribute("currentLevel") %>','<%=request.getAttribute("role") %>');" href="javascript:void(0)">Take</a>
	                                            <% } else { %>
	                                            Finished <a href="javascript:void(0);" onclick="getTakeAssessmentPreview('<%=alInner.get(8) %>','<%=alInner.get(0)%>','<%=request.getAttribute("empID") %>','<%=request.getAttribute("userType") %>');"><i class="fa fa-file-o" aria-hidden="true"></i></a>
	                                            <%} %>
	                                <!-- ===start parvez date: 23-09-2021=== -->
	                                            <% } else if(alInner.get(2) != null && alInner.get(2).equals("Video")) { 
	                                                //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
	                                                %>
	                                            <% if(hmVideoSeenStatus.get(alInner.get(8)+"_"+alInner.get(0)) == null) { %>
	                                            <div id="videoActionDiv<%=i %>"><a onclick="seeVideo('<%=alInner.get(8)%>','<%=alInner.get(0)%>','<%=i %>','<%=hmVideoName.get(alInner.get(8)) %>','<%=fromPage %>');" href="javascript:void(0);"> View</a></div>
	                                            <% } else if(hmVideoSeenStatus.get(alInner.get(8)+"_"+alInner.get(0)) != null && hmVideoSeenStatus.get(alInner.get(8)+"_"+alInner.get(0)).equals("1")) { %>
	                                            <div id="videoActionDiv<%=i %>">
	                                                <a onclick="showVideo('<%=alInner.get(8)%>','<%=hmVideoName.get(alInner.get(8)) %>','<%=fromPage %>','<%=alInner.get(0)%>');" href="javascript:void(0)">Viewed</a>
	                                            </div>
	                                            <% } else { %>
	                                            <% } %> 
	                                            <% }
	                                            } %>
                                  <!-- ===end parvez date: 23-09-2021=== -->
                                        </td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>
                                            <% if(alInner.get(2) != null && alInner.get(2).equals("Assessment")) { %>
                                            <div id="starLive<%=i%>" style="width: 92px;"></div>
                                            <script type="text/javascript">
                                                $(function() {
                                                	$('#starLive<%=i%>').raty({
                                                		readOnly: true,
                                                		start: <%=uF.parseToDouble(hmAssessmentRating.get(alInner.get(0)+"_"+alInner.get(8))) / 20 %>,
                                                		half: true,
                                                		targetType: 'number'
                                                });
                                                });
                                            </script>
                                            <% } %>
                                        </td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>
                                            <% 
                                            /* if(alInner.get(2) != null && (alInner.get(2).equals("Assessment") || alInner.get(2).equals("Training"))) { */
                                            if(alInner.get(2) != null && (alInner.get(2).equals("Assessment") || alInner.get(2).equals("Training") || alInner.get(2).equals("Video") || alInner.get(2).equals("Course"))) { 
                                                Map<String, String> hmMyLearnCertiAndThumbsups = (Map<String, String>) request.getAttribute("hmMyLearnCertiAndThumbsups");
                                                %>
                                            <%if(hmMyLearnCertiAndThumbsups != null) { 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+alInner.get(8)+"_CERTI"))) {
                                                %>
                                            <span style="float: left; margin-left: 10px; margin-top: 5px;">
                                                <!-- <img src="images1/certificate_img.png"> -->
                                                <a style="float: right; margin-top: 2px;" onclick="viewCertificate('<%=(String)session.getAttribute(IConstants.EMPID)%>','<%=alInner.get(0)%>')" href="javascript:void(0)" style="margin-left:10px">
                                                <img src="images1/certificate_img.png">
                                                </a>
                                            </span>
                                            <% } 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+alInner.get(8)+"_THUMBSUP"))) {
                                                %>
                                            <%-- <span style="float: left; margin-left: 10px; margin-top: 5px;"><img src="images1/thumbs_up.png"></span> --%>
                                            <span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-up" aria-hidden="true"></i></span>
                                            
                                            <% } 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+alInner.get(8)+"_THUMBSDOWN"))) {
                                                %>
                                            <%-- <span style="float: left; margin-left: 10px; margin-top: 5px;"><img src="images1/thumbs_down.png"></span> --%>
                                            <span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-down" aria-hidden="true"></i></span>
                                            
                                            <% } %>
                                            <% } %>
                                            <% } %>
                                        </td>
                                    </tr>
                                    <%
                                        for(int a=0; stagesList != null && !stagesList.equals("") && a<stagesList.size(); a++) {
                                        	List<String> innerList = stagesList.get(a);
                                        	if(innerList == null) innerList = new ArrayList<String>();
                                        %>	
                                    <tr class="lighttable" id="liveTR<%=i%>_<%=a %>" style="display: none;">
                                        <td style="text-align: left; vertical-align: top;" nowrap><%=innerList.get(4) %></td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>&nbsp;</td>
                                        <td style="text-align: left; vertical-align: top;" nowrap><%=innerList.get(2) %></td>
                                        <td style="text-align: left; vertical-align: top;" nowrap><%=innerList.get(3) %></td>
                                        <td style="text-align: left; vertical-align: top;" nowrap><%=innerList.get(6) %></td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>&nbsp;</td>
                                        <td style="text-align: left; vertical-align: top;" nowrap>
                                            <div id="<%=i %><%=a %>liveStatusBarDiv" style="width: 92%; margin: 0px 0px 0px 3px;">
                                                <%-- <div class="anaAttrib1"><span style="margin-left:<%=uF.parseToDouble("0") > 97 ? uF.parseToDouble("0")-6 : uF.parseToDouble("0")-3 %>%;"><%=0 %>%</span></div> --%>
                                                <% if(innerList.get(4) != null && innerList.get(4).equals("Training")) { %>
                                                <div class="anaAttrib1" style="text-align: center; "><%=uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) < 100 ? uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0)))) : "100"%>%</div>
                                                <div id="outbox">
                                                    <%if(uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) < 33.33){ %>
                                                    <div id="redbox" style="width: <%=uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) %>%;"></div>
                                                    <%}else if(uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) >= 33.33 && uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) < 66.67){ %>
                                                    <div id="yellowbox" style="width: <%=uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) %>%;"></div>
                                                    <%}else if(uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) >= 66.67){ %>
                                                    <div id="greenbox" style="width: <%=uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) < 100 ? uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) : "100" %>%;"></div>
                                                    <%} %>
                                                </div>
                                                <% } else if(innerList.get(4) != null && innerList.get(4).equals("Course")) { 
                                                    String chapterCount = hmChapterCount.get(innerList.get(7));
                                                    String chapterReadCount = hmChapterReadCount.get(innerList.get(7)+"_"+alInner.get(0));
                                                    double readPercant = (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);
                                                    //System.out.println("readPercant ===> " + readPercant);
                                                    %>
                                                <div class="anaAttrib1" style="text-align: center; "><%=readPercant < 100 ? uF.formatIntoOneDecimalWithOutComma(readPercant) : "100" %>%</div>
                                                <div id="outbox">
                                                    <%if(readPercant < 33.33){ %>
                                                    <div id="redbox" style="width: <%=readPercant %>%;"></div>
                                                    <%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
                                                    <div id="yellowbox" style="width: <%=readPercant %>%;"></div>
                                                    <%}else if(readPercant >= 66.67){ %>
                                                    <div id="greenbox" style="width: <%=readPercant < 100 ? readPercant : "100" %>%;"></div>
                                                    <%} %>
                                                </div>
                                                <% } else if(innerList.get(4) != null && innerList.get(4).equals("Assessment")) { %>
                                                <div class="anaAttrib1" style="text-align: center;"><%=uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) < 100 ? uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7)))) : "100" %>%</div>
                                                <div id="outbox">
                                                    <%if(uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) < 33.33){ %>
                                                    <div id="redbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) %>%;"></div>
                                                    <%}else if(uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) >= 33.33 && uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) < 66.67){ %>
                                                    <div id="yellowbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) %>%;"></div>
                                                    <%}else if(uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) >= 66.67){ %>
                                                    <div id="greenbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) < 100 ? uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) : "100" %>%;"></div>
                                                    <%} %>
                                                </div>
                                          <!-- ===start parvez date: 24-09-2021=== -->
                                                <% } else if(innerList.get(4) != null && innerList.get(4).equals("Video")) { 
                                                    
                                                    /* String videoStatus = hmVideoSeenStatus.get(innerList.get(7)+"_"+alInner.get(0));
                                                    double readPercant = 0;
                                                    
                                                    if(videoStatus != null &&  uF.parseToInt(videoStatus) == 1){
                                                    	readPercant = 100;
                                                    } */
                                                    
                                                    String videoCount = hmVideoCount.get(innerList.get(7));
                                                    String videoViewedCount = hmVideoViewedCount.get(innerList.get(7)+"_"+alInner.get(0));
                                                    double readPercant = (uF.parseToDouble(videoViewedCount)* 100) / uF.parseToDouble(videoCount);
                                                    
                                                    //System.out.println("readPercant ===> " + readPercant);
                                                    %>
                                                <div class="anaAttrib1" style="text-align: center; "><%=readPercant < 100 ? uF.formatIntoOneDecimalWithOutComma(readPercant) : "100" %>%</div>
                                                <div id="outbox">
                                                    <%if(readPercant < 33.33){ %>
                                                    <div id="redbox" style="width: <%=readPercant %>%;"></div>
                                                    <%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
                                                    <div id="yellowbox" style="width: <%=readPercant %>%;"></div>
                                                    <%}else if(readPercant >= 66.67){ %>
                                                    <div id="greenbox" style="width: <%=readPercant < 100 ? readPercant : "100" %>%;"></div>
                                                    <%} %>
                                                    
                                                    <%-- <% if(readPercant == 100){ %>
                                                    <div id="greenbox" style="width: <%=readPercant%>%;"></div>
                                                    <%} %> --%>
                                                </div>
                                                <% } %>
                                          <!-- ===end parvez date: 24-09-2021=== -->
                                            </div>
                                        </td>
                                        <!-- status -->
                                        <td style="text-align: left; vertical-align: top; width: 73px;" nowrap>
                                            <%if(alCloseLearnPlan.contains(alInner.get(0).trim())){ %>
                                            <span style="color: red;">Closed</span>
                                            <%}else{ %>
                                            <% if(innerList.get(4) != null && innerList.get(4).equals("Training")) { %>
                                            <%-- <div id="trainingActionDiv<%=i %><%=a %>"><a onclick="addTrainingStatus('<%=innerList.get(7)%>','<%=alInner.get(0)%>');" href="javascript:void(0)">Update</a> | <a onclick="trainingAttend('<%=innerList.get(7)%>','<%=alInner.get(0)%>','<%=i %><%=a %>');" href="javascript:void(0);"> Attend</a> | <a>Feedback</a></div> --%>
                                            <% if(hmTrainingAttendStatus.get(innerList.get(7)+"_"+alInner.get(0)) == null) { %>
                                            <div id="trainingActionDiv<%=i %><%=a %>"><a onclick="trainingAttend('<%=innerList.get(7)%>','<%=alInner.get(0)%>','<%=i %><%=a %>');" href="javascript:void(0);"> Attend</a></div>
                                            <% } else if(hmTrainingAttendStatus.get(innerList.get(7)+"_"+alInner.get(0)) != null && hmTrainingAttendStatus.get(innerList.get(7)+"_"+alInner.get(0)).equals("1") 
                                                && hmTrainingCompleteStatus.get(innerList.get(7)+"_"+alInner.get(0)) == null) { %> Attending
                                            <%-- <div id="trainingActionDiv<%=i %><%=a %>"><a onclick="addTrainingStatus('<%=innerList.get(7)%>','<%=alInner.get(0)%>');" href="javascript:void(0)">Update</a></div> --%>
                                            <% } else if(hmTrainingCompleteStatus.get(innerList.get(7)+"_"+alInner.get(0)) != null && hmTrainingCompleteStatus.get(innerList.get(7)+"_"+alInner.get(0)).equals("1")) { %>
                                            <div id="trainingActionDiv<%=i %><%=a %>"><a onclick="addTrainingFeedback('<%=innerList.get(7)%>','<%=alInner.get(0)%>');" href="javascript:void(0)">Feedback</a></div>
                                            <% } else { %>
                                            <% } %> 
                                            <% } else if(innerList.get(4) != null && innerList.get(4).equals("Course")) { %>
                                            <% if(hmcourseReadStatus.get(innerList.get(7)+"_"+alInner.get(0)) == null) { %>
                                            <div id="courseActionDiv<%=i %><%=a %>"><a onclick="courseRead('<%=innerList.get(7)%>','<%=alInner.get(0)%>','<%=i %><%=a %>');" href="javascript:void(0);"> Read</a></div>
                                            <% } else if(hmcourseReadStatus.get(innerList.get(7)+"_"+alInner.get(0)) != null && hmcourseReadStatus.get(innerList.get(7)+"_"+alInner.get(0)).equals("1")) { %>
                                            <div id="courseActionDiv<%=i %><%=a %>">
                                                <a onclick="addCourseReadStatus('<%=innerList.get(7)%>','<%=alInner.get(0)%>','<%=hmCourseName.get(innerList.get(7)) %>');" href="javascript:void(0)">Update</a>
                                                | <a onclick="viewCourseForRead('<%=innerList.get(7)%>','<%=hmCourseName.get(innerList.get(7)) %>');" href="javascript:void(0)">Reading</a>
                                            </div>
                                            <% } else if(hmTrainingCompleteStatus.get(innerList.get(7)+"_"+alInner.get(0)) != null && hmTrainingCompleteStatus.get(innerList.get(7)+"_"+alInner.get(0)).equals("1")) { %>
                                            <%-- <div id="courseActionDiv<%=i %>"><a onclick="addTrainingFeedback('<%=innerList.get(7)%>','<%=alInner.get(0)%>');" href="javascript:void(0)">Feedback</a></div> --%>
                                            <% } else { %>
                                            <% } %>
                                            <% } else if(innerList.get(4) != null && innerList.get(4).equals("Assessment")) { 
                                                int nAttempt = uF.parseToInt(hmAssessmentAttempt.get(innerList.get(7)));
                                                int nTaken = uF.parseToInt(hmAssessmentTaken.get(alInner.get(0)+"_"+innerList.get(7)));
                                                if(nTaken < nAttempt && !alAssessmentFinish.contains(alInner.get(0)+"_"+innerList.get(7))){
                                                %>
                                            <div id="assessActionDiv<%=i %><%=a %>"><a onclick="addTakeAssessment('<%=innerList.get(7)%>','<%=alInner.get(0)%>','<%=request.getAttribute("empID") %>','<%=request.getAttribute("userType") %>','<%=request.getAttribute("currentLevel") %>','<%=request.getAttribute("role") %>');" href="javascript:void(0)">Take</a> </div>
                                            <% } else { %>
                                            Finished <a href="javascript:void(0);" onclick="getTakeAssessmentPreview('<%=innerList.get(7) %>','<%=alInner.get(0)%>','<%=request.getAttribute("empID") %>','<%=request.getAttribute("userType") %>');"><i class="fa fa-file-o" aria-hidden="true"></i></a>
                                   <!-- ===start parvez date: 24-09-2021=== -->
                                            <%} %>
                                            
                                            <% } else if(innerList.get(4) != null && innerList.get(4).equals("Video")) { %>
                                            <% if(hmVideoSeenStatus.get(innerList.get(7)+"_"+alInner.get(0)) == null) { %>
                                            <%-- <div id="videoActionDiv<%=i %><%=a %>"><a onclick="seeVideo('<%=innerList.get(7)%>','<%=alInner.get(0)%>','<%=i %><%=a %>');" href="javascript:void(0);"> View</a></div> --%>
                                            <div id="videoActionDiv<%=i %><%=a %>"><a onclick="seeVideo('<%=innerList.get(7)%>','<%=alInner.get(0)%>','<%=i %><%=a %>','<%=hmCourseName.get(innerList.get(7)) %>','<%=fromPage %>');" href="javascript:void(0);"> View</a></div>
                                            
                                            <% } else if(hmVideoSeenStatus.get(innerList.get(7)+"_"+alInner.get(0)) != null && hmVideoSeenStatus.get(innerList.get(7)+"_"+alInner.get(0)).equals("1")) { %>
                                            <div id="videoActionDiv<%=i %><%=a %>">
                                                <%-- <a onclick="addCourseReadStatus('<%=innerList.get(7)%>','<%=alInner.get(0)%>','<%=hmCourseName.get(innerList.get(7)) %>');" href="javascript:void(0)">Update</a> --%>
                                                <a onclick="showVideo('<%=innerList.get(7)%>','<%=hmCourseName.get(innerList.get(7)) %>','<%=fromPage %>','<%=alInner.get(0)%>');" href="javascript:void(0)">Viewed</a>
                                            </div>
                                            <%-- <% } else if(hmTrainingCompleteStatus.get(innerList.get(7)+"_"+alInner.get(0)) != null && hmTrainingCompleteStatus.get(innerList.get(7)+"_"+alInner.get(0)).equals("1")) { %>
                                            <div id="courseActionDiv<%=i %>"><a onclick="addTrainingFeedback('<%=innerList.get(7)%>','<%=alInner.get(0)%>');" href="javascript:void(0)">Feedback</a></div>
                                            <% } else { %> --%>
                                            <% } %>
                                            
                                            <% } %>
                                  <!-- ===end parvez date: 24-09-2021=== -->
                                            <%} %>	
                                        </td>
                                        <!-- action -->
                                        <td style="text-align: left; vertical-align: top; width: 104px;" nowrap>
                                            <% if(innerList.get(4) != null && innerList.get(4).equals("Assessment")) { %>
                                            <div id="starLive<%=i%><%=a%>" style="width: 92px;"></div>
                                            <script type="text/javascript">
                                                $(function() {
                                                	$('#starLive<%=i%><%=a%>').raty({
                                                		readOnly: true,
                                                		start: <%=uF.parseToDouble(hmAssessmentRating.get(alInner.get(0)+"_"+innerList.get(7))) / 20 %>,
                                                		half: true,
                                                		targetType: 'number'
                                                });
                                                });
                                            </script>
                                            <% } %>
                                        </td>
                                        <!-- rating -->
                                        <td style="text-align: center; vertical-align: top;" nowrap>
                                            <!-- certificates/thumbs up -->
                                     <!-- ===start parvez date: 16-10-2021=== -->
                                     		<%-- <% if(innerList.get(4) != null && (innerList.get(4).equals("Assessment") || innerList.get(4).equals("Training"))) { %> --%>
                                            <% if(innerList.get(4) != null && (innerList.get(4).equals("Assessment") || innerList.get(4).equals("Training") || innerList.get(4).equals("Video") || innerList.get(4).equals("Course"))) { 
                                                Map<String, String> hmMyLearnCertiAndThumbsups = (Map<String, String>) request.getAttribute("hmMyLearnCertiAndThumbsups");
                                                %>
                                    <!-- ===end parvez date: 16-10-2021=== -->
                                            <span style="margin-top: 5px;">
                                            <%if(hmMyLearnCertiAndThumbsups != null) { 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+innerList.get(7)+"_CERTI"))) {
                                                %>
                                            <a style="float: right; margin-top: 2px;" onclick="viewCertificate('<%=(String)session.getAttribute(IConstants.EMPID)%>','<%=alInner.get(0)%>')" href="javascript:void(0)" style="margin-left:10px">
                                            <img src="images1/certificate_img.png">
                                            </a>						
                                            <% } 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+innerList.get(7)+"_THUMBSUP"))) {
                                                %>
                                            &nbsp;&nbsp;<!-- <img src="images1/thumbs_up.png"> --><i class="fa fa-thumbs-up" aria-hidden="true"></i>
                                            <% } 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+innerList.get(7)+"_THUMBSDOWN"))) {
                                                %>
                                            &nbsp;&nbsp;<!-- <img src="images1/thumbs_down.png"> -->
                                            <i class="fa fa-thumbs-down" aria-hidden="true"></i>
                                            <% } %>
                                            <% } %>
                                            </span>
                                            <% } %>
                                        </td>
                                    </tr>
                                    <%}%>	
                                    <%
                                        i++;
                                        }
                                        if (alLiveLearnings.size() == 0) {
                                        %>
                                    <tr>
                                        <td colspan="11">
                                            <div class="nodata msg">
                                                <span>No Live Learnings </span>
                                            </div>
                                        </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            <div class="box box-primary collapsed-box">
                <div class="box-header with-border" data-widget="collapse-full">
                    <h3 class="box-title"> My Future Learnings</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                 <!-- created by seema  Added scrollbox and scrollbox-content css -->
                <div class="scrollbox box-body" style="padding: 5px;display:none;">
                    <div class="scrollbox-content attendance">
                  <!-- created by seema  Added scrollbox and scrollbox-content css -->
                        <table width="100%"  class="table table-bordered" cellspacing="0" cellpadding="2" align="left" style="margin:10px 0px 25px 0px;">
                            <tbody>
                                <tr class="darktable">
                                    <td style="text-align: center;">Plan Name</td>
                                    <td style="text-align: center;">Type</td>
                                    <td style="text-align: center;">Certificate</td>
                                    <td style="text-align: center;">Start Date</td>
                                    <td style="text-align: center;">End Date</td>
                                    <td style="text-align: center;">Created By</td>
                                    <td style="text-align: center;">Reason for</td>
                                    <td style="text-align: center;">Status (bar)</td>
                                    <td style="text-align: center;">Actions</td>
                                    <td style="text-align: center;">Rating</td>
                                    <td style="text-align: center;">Certificates/Thumbs up</td>
                                </tr>
                                <%
                                    List<List<String>> alFutureLearnings = (List<List<String>>) request.getAttribute("alFutureLearnings");
                                    if(alFutureLearnings == null) alFutureLearnings = new ArrayList<List<String>>();
                                    int j = 0;
                                    for (List<String> alInner : alFutureLearnings) {
                                    %>
                                <tr class="lighttable">
                                    <td style="text-align: left; vertical-align: top;">
                                        <a href="javascript: void(0)" onclick="openLearningPreview('<%=alInner.get(0) %>')"><%=alInner.get(1)%> </a>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(2)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(3)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(4)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(5)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(6)%></td>
                                    <td style="text-align: left; vertical-align: top;">
                                        <a href="javascript:void(0);" onclick="showLPlanReason('<%=alInner.get(7)%>');"><%=alInner.get(7).substring(0, alInner.get(7).length()>9 ? 9 : alInner.get(7).length()) %>...</a>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;">
                                        <%-- <a onclick="addTrainingStatus(<%=alInner.get(0)%>);" href="javascript:void(0)"><div style="height: .4cm" id="progressbar<%=i%>"></div> </a> --%>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;">&nbsp;</td>
                                    <td style="text-align: left; vertical-align: top;"></td>
                                    <td style="text-align: left; vertical-align: top;">&nbsp;</td>
                                </tr>
                                <%
                                    j++;
                                    }
                                    if (alFutureLearnings == null || alFutureLearnings.size() == 0) {
                                    %>
                                <tr>
                                    <td colspan="11">
                                        <div class="nodata msg">
                                            <span>No Future Learnings </span>
                                        </div>
                                    </td>
                                </tr>
                                <% 	}%>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="box box-primary collapsed-box">
                <div class="box-header with-border" data-widget="collapse-full">
                    <h3 class="box-title"> My Previous Learnings</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                 <!-- created by seema  Added scrollbox and scrollbox-content css -->
                <div class="scrollbox box-body" style="padding: 5px;display:none;">
                    <div class="scrollbox-content attendance">
                 <!-- created by seema  Added scrollbox and scrollbox-content css -->
                        <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin:10px 0px 25px 0px;">
                            <tbody>
                                <tr class="darktable">
                                    <td style="text-align: center;">Plan Name</td>
                                    <td style="text-align: center;">Type</td>
                                    <td style="text-align: center;">Certificate</td>
                                    <td style="text-align: center;">Start Date</td>
                                    <td style="text-align: center;">End Date</td>
                                    <td style="text-align: center;">Created By</td>
                                    <td style="text-align: center;">Reason for</td>
                                    <td style="text-align: center;">Status (bar)</td>
                                    <!-- <td style="text-align: center;">Actions</td> -->
                                    <td style="text-align: center;">Rating</td>
                                    <td style="text-align: center;">Certificates/Thumbs up</td>
                                </tr>
                                <%
                                    List<List<String>> alPreviousLearnings = (List<List<String>>) request.getAttribute("alPreviousLearnings");
                                    if(alPreviousLearnings == null) alPreviousLearnings = new ArrayList<List<String>>();
                                    int k = 0;
                                    //System.out.println("MLP.jsp/1147--alPreviousLearnings="+alPreviousLearnings);
                                    for (List<String> alInner : alPreviousLearnings) {
                                    %>
                                <tr class="lighttable">
                                    <%
                                        String rowspn = "1";
                                        /* if(alInner.get(2) != null && alInner.get(2).equals("Hybrid")) { 
                                        	rowspn = "2";
                                        } */
                                        	List<List<String>> stagesList = hmStageType.get(alInner.get(0));
                                        	if(stagesList == null) stagesList = new ArrayList<List<String>>();
                                        	int rwspn = 1;
                                        	/* if(alInner.get(2) != null && !alInner.get(2).equals("Training") && !alInner.get(2).equals("Course") && !alInner.get(2).equals("Assessment")) { */
                                        	if(alInner.get(2) != null && !alInner.get(2).equals("Training") && !alInner.get(2).equals("Course") && !alInner.get(2).equals("Assessment") && !alInner.get(2).equals("Video")) {
                                        		rwspn = stagesList != null ? stagesList.size()+1 : 1;
                                        	}
                                        	int stageSize = stagesList.size();
                                        	%>
                                    <td  id="prevname<%=k %>" style="text-align: left; vertical-align: top;" nowrap>
                                        <a href="javascript: void(0)" onclick="openLearningPreview('<%=alInner.get(0) %>')"><%=alInner.get(1)%> </a>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;">
                                        <%-- <%=alInner.get(2)%> --%>
                                        <span style="float: left;"><%=alInner.get(2)%></span>
                                        <%-- <%if(alInner.get(2) != null && !alInner.get(2).equals("Training") && !alInner.get(2).equals("Course") && !alInner.get(2).equals("Assessment")) { %> --%>
                                        <%if(alInner.get(2) != null && !alInner.get(2).equals("Training") && !alInner.get(2).equals("Course") && !alInner.get(2).equals("Assessment") && !alInner.get(2).equals("Video")) { %>
                                        <input type="hidden" name="hidePrevStageType" id="hidePrevStageType<%=k %>" value = "0"/>
                                        <a href="javascript: void(0);" onclick="showPrevStageDetails('<%=k %>','<%=stageSize %>');">
                                            <!-- <img src="images1/icons/icons/downarrow.png" style="width: 12px;"/> -->
                                            <span id="MLPrevdownarrowSpan<%=k %>">
                                            &nbsp&nbsp
                                            <i class="fa fa-angle-down" aria-hidden="true" style="width: 12px;margin-top: -2px;"></i>
                                            
                                            </span>
                                            <span id="MLPrevuparrowSpan<%=k %>" style="display: none;">
                                            &nbsp&nbsp<i class="fa fa-angle-up" aria-hidden="true" style="width: 12px;"></i>
                                            </span>
                                        </a>
                                        <!-- ,this.parentNode.parentNode.rowIndex -->
                                        <% } %>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(3)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(4)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(5)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(6)%></td>
                                    <td style="text-align: left; vertical-align: top;">
                                        <a href="javascript:void(0);" onclick="showLPlanReason('<%=alInner.get(7)%>');"><%=alInner.get(7).substring(0, alInner.get(7).length()>9 ? 9 : alInner.get(7).length()) %>...</a>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;">
                                        <div id="<%=k %>PrevStatusBarDiv" style="width: 92%; margin: 0px 0px 0px 3px;">
                                            <% if(alInner.get(2) != null && alInner.get(2).equals("Training")) { 
                                                //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
                                                double trainingStatus  = 0;
                                                if(uF.parseToDouble(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0))) > 100) {
                                                	trainingStatus = 100;
                                                } else {
                                                	trainingStatus = uF.parseToDouble(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0)));
                                                }
                                                %>
                                            <div class="anaAttrib1" style="text-align: center; "><%-- <span style="margin-left:<%=uF.parseToInt(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0))) > 94 ? uF.parseToInt(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0)))-6 : uF.parseToInt(hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0)))-2.5 %>%;"><%=hmTrainingStatus.get(alInner.get(8)+"_"+alInner.get(0))%>%</span> --%>
                                                <%=uF.formatIntoOneDecimalWithOutComma(trainingStatus) %>%
                                            </div>
                                            <div id="outbox">
                                                <%if(trainingStatus < 33.33){ %>
                                                <div id="redbox" style="width: <%=trainingStatus %>%;"></div>
                                                <%}else if(trainingStatus >= 33.33 && trainingStatus < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=trainingStatus %>%;"></div>
                                                <%}else if(trainingStatus >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=trainingStatus %>%;"></div>
                                                <%} %>
                                            </div>
                                            <% } else if(alInner.get(2) != null && alInner.get(2).equals("Course")) { 
                                                //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
                                                String chapterCount = hmChapterCount.get(alInner.get(8));
                                                String chapterReadCount = hmChapterReadCount.get(alInner.get(8)+"_"+alInner.get(0));
                                         //===start parvez date: 24-09-2021===
                                                double readPercant = 0;
                                                //double readPercant = (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);
                                                
                                                if(chapterReadCount != null  && chapterCount != null ) {
                                                	readPercant = (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);
                                                }
                                         //===end parvez date: 24-09-2021===
                                        	 
                                                if(readPercant > 100) {
                                                	readPercant = 100;
                                                }
                                               // System.out.println("readPercant ===> " + readPercant);
                                                %>
                                            <div class="anaAttrib1" style="text-align: center; "><%=uF.formatIntoOneDecimalWithOutComma(readPercant) %>%</div>
                                            <div id="outbox">
                                                <%if(readPercant < 33.33){ %>
                                                <div id="redbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=readPercant %>%;"></div>
                                                <%} %>
                                            </div>
                                            <% } else if(alInner.get(2) != null && alInner.get(2).equals("Assessment")) { 
                                                //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
                                                //hmAssessmentTakePercent.get(stageTypeId);
                                                %>
                                            <div class="anaAttrib1" style="text-align: center;"><%=uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) < 100 ? uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8)))) : "100"%>%</div>
                                            <div id="outbox">
                                                <%if(uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) < 33.33){ %>
                                                <div id="redbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) %>%;"></div>
                                                <%}else if(uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) >= 33.33 && uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) %>%;"></div>
                                                <%}else if(uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) < 100 ? uF.parseToDouble(hmAssessmentTakePercent.get(alInner.get(8))) : "100" %>%;"></div>
                                                <%} %>
                                            </div>
                                <!-- ===start parvez date: 24-09-2021=== -->
                                            <% } else if(alInner.get(2) != null && alInner.get(2).equals("Video")) { 
                                                //String stageTypeId = hmStageTypeId.get(alInner.get(0)+"_"+alInner.get(2));
                                                
                                                /* String videoStatus = hmVideoSeenStatus.get(alInner.get(8)+"_"+alInner.get(0));
                                                double readPercant = 0;
                                                if(videoStatus != null &&  uF.parseToInt(videoStatus) == 1) {
                                                	readPercant = 100;
                                                } */
                                                //System.out.println("readPercant ===> " + readPercant);
                                                
                                                String videoCount = hmVideoCount.get(alInner.get(8));
                                                String videoViewedCount = hmVideoViewedCount.get(alInner.get(8)+"_"+alInner.get(0));
                                         		double readPercant = 0;
                                                //double readPercant = (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);
                                                
                                                if(videoViewedCount != null  && videoCount != null ) {
                                                	readPercant = (uF.parseToDouble(videoViewedCount)* 100) / uF.parseToDouble(videoCount);
                                                }
                                                
                                                %>
                                            <div class="anaAttrib1" style="text-align: center; "><%=uF.formatIntoOneDecimalWithOutComma(readPercant) %>%</div>
                                            <div id="outbox">
                                                <%if(readPercant < 33.33){ %>
                                                <div id="redbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=readPercant %>%;"></div>
                                                <%} %>
                                                
                                                <%-- <%if(readPercant == 100){ %>
                                                <div id="greenbox" style="width: <%=readPercant %>%;"></div>
                                                <%} %> --%>
                                            </div>
                                <!-- ===end parvez date: 28-09-2021=== -->
                                            <% } else {
                                                double trainingPercent = 0;
                                                double coursePercent = 0;
                                                double assessPercent = 0;
                                                double videoPercent = 0;	//===added by parvez date: 24-09-2021===
                                                int count = 0;
                                                double avgPercent = 0; 
                                                for(int a=0; stagesList != null && !stagesList.equals("") && a<stagesList.size(); a++) {
                                                	List<String> innerList = stagesList.get(a);
                                                	if(innerList == null) innerList = new ArrayList<String>();
                                                	
                                                	if(innerList.get(4) != null && innerList.get(4).equals("Training")) { 
                                                		trainingPercent += uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0)));												
                                                			count++;
                                                		} else if(innerList.get(4) != null && innerList.get(4).equals("Course")) { 
                                                			String chapterCount = hmChapterCount.get(innerList.get(7));
                                                			String chapterReadCount = hmChapterReadCount.get(innerList.get(7)+"_"+alInner.get(0));
                                                			coursePercent += (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);
                                                			count++;
                                                		} else if(innerList.get(4) != null && innerList.get(4).equals("Assessment")) {
                                                			assessPercent += uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7)));
                                                			count++;
                                                /* ===start parvez date: 24-09-2021=== */
                                                		} else if(innerList.get(4) != null && innerList.get(4).equals("Video")) { 
                                                			
                                                			if(uF.parseToInt(hmVideoSeenStatus.get(innerList.get(7)+"_"+alInner.get(0))) == 1){
                                                				videoPercent = 100;
                                                			} else{
                                                				videoPercent = 0;
                                                			}
                                                			
                                                			count++;
                                                		}
                                                /* ===end parvez date: 24-09-2021=== */
                                                	
                                                	}
                                                if(count > 0){
                                                	/* avgPercent = (trainingPercent + coursePercent + assessPercent) / count; */
                                                	avgPercent = (trainingPercent + coursePercent + assessPercent+videoPercent) / count;
                                                	
                                                } %>
                                            <div class="anaAttrib1" style="text-align: center;"><%=avgPercent < 100 ? uF.formatIntoOneDecimalWithOutComma(avgPercent) : "100" %>%</div>
                                            <div id="outbox">
                                                <%if(avgPercent < 33.33){ %>
                                                <div id="redbox" style="width: <%=avgPercent %>%;"></div>
                                                <%}else if(avgPercent >= 33.33 && avgPercent < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=avgPercent %>%;"></div>
                                                <%}else if(avgPercent >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=avgPercent < 100 ? avgPercent : "100" %>%;"></div>
                                                <%} %>
                                            </div>
                                            <%} %>
                                        </div>
                                    </td>
                                    <!-- <td style="text-align: left; vertical-align: top;">&nbsp;</td> -->
                                    <td style="text-align: left; vertical-align: top;">
                                        <div id="starPrevious<%=k %>" style="width: 92px;"></div>
                                        <% if(alInner.get(2) != null && alInner.get(2).equals("Assessment")) { %>
                                        <div id="starPrevious<%=k %>" style="width: 92px;"></div>
                                        <script type="text/javascript">
                                            $(function() {
                                            	$('#starLive<%=i%>').raty({
                                            		readOnly: true,
                                            		start: 2,
                                            		half: true,
                                            		targetType: 'number'
                                            });
                                            });
                                        </script>
                                        <% } %>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;">
                                        <% /* if(alInner.get(2) != null && (alInner.get(2).equals("Assessment") || alInner.get(2).equals("Training") || alInner.get(2).equals("Video"))) { */
                                        if(alInner.get(2) != null && (alInner.get(2).equals("Assessment") || alInner.get(2).equals("Training"))) {
                                            Map<String, String> hmMyLearnCertiAndThumbsups = (Map<String, String>) request.getAttribute("hmMyLearnCertiAndThumbsups");
                                            %>
                                        <%if(hmMyLearnCertiAndThumbsups != null) { 
                                            if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+alInner.get(8)+"_CERTI"))) {
                                            %>
                                        <span style="float: left; margin-left: 10px; margin-top: 5px;">
                                            <!-- <img src="images1/certificate_img.png"> -->
                                            <a style="float: right; margin-top: 2px;" onclick="viewCertificate('<%=(String)session.getAttribute(IConstants.EMPID)%>','<%=alInner.get(0)%>')" href="javascript:void(0)" style="margin-left:10px">
                                            <img src="images1/certificate_img.png">
                                            </a>
                                        </span>
                                        <% 			} 
                                            if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+alInner.get(8)+"_THUMBSUP"))) {
                                            %>
                                        <%-- <span style="float: left; margin-left: 10px; margin-top: 5px;"><img src="images1/thumbs_up.png"></span> --%>
                                        <span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-up" aria-hidden="true"></i></span>
                                        <% 			} 
                                            if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+alInner.get(8)+"_THUMBSDOWN"))) {
                                            %>
                                       <%--  <span style="float: left; margin-left: 10px; margin-top: 5px;"><img src="images1/thumbs_down.png"></span> --%>
                                        <span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-down" aria-hidden="true"></i></span>
                                        
                                        <% } %>
                                        <% } %>
                                        <% } %>
                                    </td>
                                </tr>
                                <%
                                    for(int a=0; stagesList != null && !stagesList.equals("") && a<stagesList.size(); a++) {
                                    	List<String> innerList = stagesList.get(a);
                                    	if(innerList == null) innerList = new ArrayList<String>();
                                    %>	
                                <tr class="lighttable" id="prevTR<%=k%>_<%=a %>" style="display: none;">
                                    <td style="text-align: left; vertical-align: top;" nowrap><%=innerList.get(4) %></td>
                                    <td style="text-align: left; vertical-align: top;" nowrap>&nbsp;</td>
                                    <td style="text-align: left; vertical-align: top;" nowrap><%=innerList.get(2) %></td>
                                    <td style="text-align: left; vertical-align: top;" nowrap><%=innerList.get(3) %></td>
                                    <td style="text-align: left; vertical-align: top;" nowrap><%=innerList.get(6) %></td>
                                    <td style="text-align: left; vertical-align: top;" nowrap>&nbsp;</td>
                                    <td style="text-align: left; vertical-align: top;" nowrap>
                                        <div id="<%=i %><%=a %>prevStatusBarDiv" style="width: 92%; margin: 0px 0px 0px 3px;">
                                            <%-- <div class="anaAttrib1"><span style="margin-left:<%=uF.parseToDouble("0") > 97 ? uF.parseToDouble("0")-6 : uF.parseToDouble("0")-3 %>%;"><%=0 %>%</span></div> --%>
                                            <% if(innerList.get(4) != null && innerList.get(4).equals("Training")) { %>
                                            <div class="anaAttrib1" style="text-align: center;"><%=uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) < 100 ? uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0)))) : "100"%>%</div>
                                            <div id="outbox">
                                                <%if(uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) < 33.33){ %>
                                                <div id="redbox" style="width: <%=uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) %>%;"></div>
                                                <%}else if(uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) >= 33.33 && uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) %>%;"></div>
                                                <%}else if(uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) < 100 ? uF.parseToDouble(hmTrainingStatus.get(innerList.get(7)+"_"+alInner.get(0))) : "100" %>%;"></div>
                                                <%} %>
                                            </div>
                                            <% } else if(innerList.get(4) != null && innerList.get(4).equals("Course")) { 
                                                String chapterCount = hmChapterCount.get(innerList.get(7));
                                                String chapterReadCount = hmChapterReadCount.get(innerList.get(7)+"_"+alInner.get(0));
                                                double readPercant = (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);
                                                //System.out.println("readPercant ===> " + readPercant);
                                                %>
                                            <div class="anaAttrib1" style="text-align: center;"><%=readPercant < 100 ? uF.formatIntoOneDecimalWithOutComma(readPercant) : "100" %>%</div>
                                            <div id="outbox">
                                                <%if(readPercant < 33.33){ %>
                                                <div id="redbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=readPercant < 100 ? readPercant : "100" %>%;"></div>
                                                <%} %>
                                            </div>
                                            <% } else if(innerList.get(4) != null && innerList.get(4).equals("Assessment")) { %>
                                            <div class="anaAttrib1" style="text-align: center;"><%=uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) < 100 ? uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7)))) : "100" %>%</div>
                                            <div id="outbox">
                                                <%if(uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) < 33.33){ %>
                                                <div id="redbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) %>%;"></div>
                                                <%}else if(uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) >= 33.33 && uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) %>%;"></div>
                                                <%}else if(uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) < 100 ? uF.parseToDouble(hmAssessmentTakePercent.get(innerList.get(7))) : "100" %>%;"></div>
                                                <%} %>
                                            </div>
                                            <% } else if(innerList.get(4) != null && innerList.get(4).equals("Video")) { 
                                                
                                                String videoStatus = hmVideoSeenStatus.get(innerList.get(7)+"_"+alInner.get(0));
                                                double readPercant = 0;
                                                
                                                if(videoStatus != null &&  uF.parseToInt(videoStatus) == 1) {
                                                	readPercant = 100;	
                                            	}
                                                //System.out.println("readPercant ===> " + readPercant);
                                                %>
                                            <div class="anaAttrib1" style="text-align: center;"><%=readPercant < 100 ? uF.formatIntoOneDecimalWithOutComma(readPercant) : "100" %>%</div>
                                            <div id="outbox">
                                                <%-- <%if(readPercant < 33.33){ %>
                                                <div id="redbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
                                                <div id="yellowbox" style="width: <%=readPercant %>%;"></div>
                                                <%}else if(readPercant >= 66.67){ %>
                                                <div id="greenbox" style="width: <%=readPercant < 100 ? readPercant : "100" %>%;"></div>
                                                <%} %> --%>
                                                
                                                <% if(readPercant == 100){ %>
                                                <div id="greenbox" style="width: <%=readPercant %>%;"></div>
                                                <%} %>
                                            </div>
                                            <% } %>
                                        </div>
                                    </td>
                                    <td style="text-align: left; vertical-align: top; width: 104px;" nowrap>
                                        <% if(innerList.get(4) != null && innerList.get(4).equals("Assessment")) { %>
                                        <div id="starPrev<%=k%><%=a%>" style="width: 92px;"></div>
                                        <script type="text/javascript">
                                            $(function() {
                                            	$('#starPrev<%=k%><%=a%>').raty({
                                            		readOnly: true,
                                            		start: <%=uF.parseToDouble(hmAssessmentRating.get(alInner.get(0)+"_"+innerList.get(7))) / 20 %>,
                                            		half: true,
                                            		targetType: 'number'
                                            });
                                            });
                                        </script>
                                        <% } %>
                                    </td>
                                    <!-- rating -->
                                    <td style="text-align: center; vertical-align: top;" nowrap>
                                        <!-- certificates/thumbs up -->
                                        <% if(innerList.get(4) != null && (innerList.get(4).equals("Assessment") || innerList.get(4).equals("Training"))) { 
                                        	/* if(innerList.get(4) != null && (innerList.get(4).equals("Assessment") || innerList.get(4).equals("Training") || innerList.get(4).equals("Video"))) { */
                                            Map<String, String> hmMyLearnCertiAndThumbsups = (Map<String, String>) request.getAttribute("hmMyLearnCertiAndThumbsups");
                                            
                                            %>
                                        <span style="margin-top: 5px;">
                                            <%if(hmMyLearnCertiAndThumbsups != null) { 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+innerList.get(7)+"_CERTI"))) {
                                                %>
                                            <!-- <img src="images1/certificate_img.png"> -->
                                            <a style="float: right; margin-top: 2px;" onclick="viewCertificate('<%=(String)session.getAttribute(IConstants.EMPID)%>','<%=alInner.get(0)%>')" href="javascript:void(0)" style="margin-left:10px">
                                            <img src="images1/certificate_img.png">
                                            </a>						
                                            <% } 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+innerList.get(7)+"_THUMBSUP"))) {
                                                %>
                                            &nbsp;&nbsp;<!-- <img src="images1/thumbs_up.png"> -->
                                            <i class="fa fa-thumbs-up" aria-hidden="true"></i>
                                            <% } 
                                                if(uF.parseToBoolean(hmMyLearnCertiAndThumbsups.get(alInner.get(0)+"_"+innerList.get(7)+"_THUMBSDOWN"))) {
                                                %>
                                            &nbsp;&nbsp;<!-- <img src="images1/thumbs_down.png"> -->
                                            <i class="fa fa-thumbs-down" aria-hidden="true"></i>
                                            <% } %>
                                            <% } %>
                                        </span>
                                        <% } %>
                                    </td>
                                </tr>
                                <%	
                                    }
                                    %>	
                                <%
                                    k++;
                                    }
                                    if (alPreviousLearnings == null || alPreviousLearnings.size() == 0) {
                                    %>
                                <tr>
                                    <td colspan="11">
                                        <div class="nodata msg">
                                            <span>No Previous Learnings </span>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                    }
                                    %>
                            </tbody>
                        </table>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            <div class="box box-primary collapsed-box">
                <div class="box-header with-border" data-widget="collapse-full">
                    <h3 class="box-title">Absent Learnings</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                 <!-- created by seema  Added scrollbox and scrollbox-content css -->
                <div class="scrollbox box-body" style="padding: 5px;display:none">
                    <div class="scrollbox-content attendance">
                     <!-- created by seema  Added scrollbox and scrollbox-content css -->
                        <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin:10px 0px 25px 0px;">
                            <tbody>
                                <tr class="darktable">
                                    <td style="text-align: center;">Name</td>
                                    <td style="text-align: center;">Location</td>
                                    <td style="text-align: center;">Start Date</td>
                                    <td style="text-align: center;">End Date</td>
                                    <td style="text-align: center;">Result</td>
                                </tr>
                                <%
                                    List<List<String>> alAbsentLearnings = (List<List<String>>) request.getAttribute("alAbsentLearnings");
                                    for (List<String> alInner : alAbsentLearnings) {
                                    %>
                                <tr class="lighttable">
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(1)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(2)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(3)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(4)%></td>
                                    <td style="text-align: left; vertical-align: top;">
                                        <b>Absent</b>
                                    </td>
                                </tr>
                                <%
                                    } if (alAbsentLearnings.size() == 0) {
                                    %>
                                <tr>
                                    <td colspan="5">
                                        <div class="nodata msg">
                                            <span>No Absent Learnings </span>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </section>
  <!-- ===start parvez date: 28-09-2021=== --> 
    <% } else if(dataType != null && dataType.equals("LC")) { %>
    <section class="col-lg-12 connectedSortable" style="padding-top: 10px;">
       <div class="box box-primary">
       		<div class="box box-primary collapsed-box">
                <!-- <div class="box-header with-border" data-widget="collapse-full">
                    <h3 class="box-title"> My Future Learnings</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div> -->
                <!-- /.box-header -->
                 <!-- created by seema  Added scrollbox and scrollbox-content css -->
                <!-- <div class="scrollbox box-body" style="padding: 5px;display:none;">
                    <div class="scrollbox-content attendance"> -->
                  <!-- created by seema  Added scrollbox and scrollbox-content css -->
                        <table width="100%"  class="table table-bordered" cellspacing="0" cellpadding="2" align="left" style="margin:10px 0px 25px 0px;">
                            <tbody>
                                <tr class="darktable">
                                    <td style="text-align: center;">Plan Name</td>
                                    <td style="text-align: center;">Type</td>
                                    <td style="text-align: center;">Certificate</td>
                                    <td style="text-align: center;">Start Date</td>
                                    <td style="text-align: center;">End Date</td>
                                    <td style="text-align: center;">Created By</td>
                                    <td style="text-align: center;">Reason for</td>
                                    <td style="text-align: center;">Actions</td>
                                    <%if(uF.parseToBoolean(CF.getIsWorkFlow())){ %>
                                    	<td style="text-align: center;">Workflow</td>
                                    <%} %>
                                </tr>
                                <%
                                    List<List<String>> alOrgLearnings = (List<List<String>>) request.getAttribute("alOrgLearnings");
                                    if(alOrgLearnings == null) alOrgLearnings = new ArrayList<List<String>>();
                                    int j = 0;
                                    for (List<String> alInner : alOrgLearnings) {
                                    %>
                                <tr class="lighttable">
                                    <td style="text-align: left; vertical-align: top;">
                                        <a href="javascript: void(0)" onclick="openLearningPreview('<%=alInner.get(0) %>')"><%=alInner.get(1)%> </a>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(2)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(3)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(4)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(5)%></td>
                                    <td style="text-align: left; vertical-align: top;"><%=alInner.get(6)%></td>
                                    <td style="text-align: left; vertical-align: top;">
                                        <a href="javascript:void(0);" onclick="showLPlanReason('<%=alInner.get(7)%>');"><%=alInner.get(7).substring(0, alInner.get(7).length()>9 ? 9 : alInner.get(7).length()) %>...</a>
                                    </td>
                                    <td style="text-align: left; vertical-align: top;">
                                    	<a href="javascript:void(0);" style="padding-left:20px;" onclick="requestForLearning('<%=alInner.get(0)%>','<%=fromPage%>','<%=alInner.get(1)%>');"><input type="button" class="btn btn-primary" value="Nominate"></a>
                                    </td>
                                    <%if(uF.parseToBoolean(CF.getIsWorkFlow())){ %>
                                    	
                                    	<td style="text-align: left; vertical-align: top;"><%=uF.showData(alInner.get(9),"") %></td>
                                    <%} %>
                                    
                                </tr>
                                
                                <%
                                    j++;
                                    }
                                    if (alOrgLearnings == null || alOrgLearnings.size() == 0) {
                                    %>
                                <tr>
                                    <td colspan="11">
                                        <div class="nodata msg">
                                            <span>No Future Learnings </span>
                                        </div>
                                    </td>
                                </tr>
                                <% 	}%>
                            </tbody>
                        </table>
                        <div id="learningCalDiv"></div>
                   <!--  </div>
                </div> -->
            </div>
       </div>
    </section>
    <% } %>
  <!-- ===end parvez date: 28-09-2021=== -->  
    </div>
    </div>
	<%-- </section>
   </div> --%>
</section>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1">Employee Information</h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>