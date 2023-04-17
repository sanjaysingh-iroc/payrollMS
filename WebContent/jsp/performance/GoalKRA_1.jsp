<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
    .desgn {
    padding: 0px;
    border: solid 1px #ccc;
    }
    li > span {
    
    position: relative;
    top: 0px;
    }
    .level_list>li {
    padding-top: 5px;
    padding-bottom: 5px;
    border-bottom: 1px solid rgb(240, 240, 240);
    }
    
    .emps {
    text-align: center;
    font-size: 26px;
    color: #3F82BF; /* none repeat scroll 0 0 #3F82BF */
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

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>

<script type="text/javascript">
    $(document).ready(function(){
    	$("body").on('click','#closeButton',function(){
    		$('.modal-body').height(400);
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$('.modal-body').height(400);
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
    	});
    	$("body").on('click','#closeButton1',function(){
    		$(".proDialog").removeAttr('style');
    		$("#proBody").height(400);
    		$("#profileInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$('.modal-body1').height(400);
    		$(".proDialog").removeAttr('style');
    		$("#profileInfo").hide();
    	});
    });
    
    function closeGoal(goalId, type, empId,dataType,currUserType) {
   
    	var pageTitle = 'Close Goal';
       	if(type=='view') {
    		pageTitle = 'Close Goal Reason';
    	}
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html(''+pageTitle);
    	$("#modalInfo").show();
    	$.ajax({
    		url : "CloseGoalTargetKRA.action?goalId="+goalId+"&dataType="+dataType+"&currUserType="+currUserType+"&empId="+empId+"&fromPage=GKT",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    

    function openGoalForLive(goalId, type, empId,dataType,currUserType) {
    	   
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Open Goal for Live');
    	$("#modalInfo").show();
    	$.ajax({
    		url : "CloseGoalTargetKRA.action?goalId="+goalId+"&type="+type+"&dataType="+dataType+"&currUserType="+currUserType+"&empId="+empId+"&fromPage=GKT",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    		
    function editGoal(goalid, typeas, empId, goalTitle, currUserType, orgId) { 
       	var tit = "Target";
    	if(typeas == 'KRA') {
    		tit = "KRA";
    	}
    	
    /*	var pageCountLimit = document.getElementById("pageCount").value;
       	var allData = pageCountLimit.split("_");
       	var pageCnt = allData[0];
       	var minLimit = allData[1];*/
       	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Update Individual Buisness Goal' );
    	$("#modalInfo").show();
    	if($(window).width() >= 1100){
    		$(".modal-dialog").width(1100);
    	}
    	if($(window).height() >= 500){
    		$('.modal-body').height(500);
    	}
    	
    	$.ajax({
    		url : "EditMyPersonalGoalPopUp_1.action?operation=E&fromPage=GKT&type=type&goalid="+goalid+"&typeas="+typeas
    				+"&from=GKT&currUserType="+currUserType+"&empId="+empId+"&strID="+orgId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    function updateComment(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,feedbackUserType,accessFlag) {
    	var completedPercent = 0;
        if(goalType == 'KRA') {
        completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).value;
        } else {
        completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId).value;
        }
    	 if(confirm('Are you sure, you wish to add task rating and comment?')) {
    		 xmlhttp = GetXmlHttpObject();
             if (xmlhttp == null) {
                     alert("Browser does not support HTTP Request");
                     return;
             } else {
             	alert('UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalid+'&goalFreqId='+goalFreqId+'&kraId='+kraId+'&kraTaskId='+kraTaskId
                	+'&completedPercent='+completedPercent+'&type=GoalKRA&goalType='+goalType+'&feedbackUserType='+feedbackUserType);

                     var xhr = $.ajax({
                            url : 'UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalid+'&goalFreqId='+goalFreqId+'&kraId='+kraId+'&kraTaskId='+kraTaskId
                            	+'&completedPercent='+completedPercent+'&type=GoalKRA&goalType='+goalType+'&feedbackUserType='+feedbackUserType,
                            cache : false,
                            success : function(data) {
                            	if(goalType == 'KRA') {
                                  		document.getElementById("TaskRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).style.display = "block";
                                    }else{
                             			document.getElementById("GoalRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId).style.display = "block";
									}
                            	}
                            });
                            	
                     }
    		 
    	 }
	
		}
    
    function updateCompletedPercent(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,accessFlag) {
    //alert("accessFlag==>"+ accessFlag+"==>goalType==>"+goalType);
    var completedPercent = 0;
    if(goalType == 'KRA') {
    completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).value;
    } else {
    completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId).value;
    }
    if(parseFloat(completedPercent) > 100) {
    alert('Entered percentage greater than 100, please enter currect percentage.');
    }else if(completedPercent ==  null){
     alert('please enter currect percentage.');
    }else {
    if(confirm('Are you sure, you wish to update task completion percentage?')) {
         xmlhttp = GetXmlHttpObject();
         if (xmlhttp == null) {
                 alert("Browser does not support HTTP Request");
                 return;
         } else {
                 var xhr = $.ajax({
                        url : 'UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalid+'&goalFreqId='+goalFreqId+'&kraId='+kraId+'&kraTaskId='+kraTaskId
                        	+'&completedPercent='+completedPercent+'&type=GoalKRA&goalType='+goalType,
                        cache : false,
                        success : function(data) {
                        	if(data == "") {
                        		
                        	} else if(data.length > 1) {
                        		var allData = data.split("::::");
                        		if(goalType == 'KRA') {
                                 document.getElementById("completedPercentStatusSpan_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).innerHTML = allData[1];
                                 document.getElementById("KTProBarDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).innerHTML = allData[2];
                                // if(parseFloat(completedPercent) == 100) {
                                // 	document.getElementById("TaskRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).style.display = "block";
                                 //} else {
                                 //	document.getElementById("TaskRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).style.display = "block";
                                 //}
                        		} else {
                        			document.getElementById("completedPercentStatusSpan_"+empId+"_"+goalid+"_"+goalFreqId).innerHTML = allData[1];
                                 document.getElementById("GoalProBarDiv_"+empId+"_"+goalid+"_"+goalFreqId).innerHTML = allData[2];
                                 //if(parseFloat(completedPercent) == 100) {
                                 //	document.getElementById("GoalRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId).style.display = "block";
                                 //} else {
                                 //	document.getElementById("GoalRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId).style.display = "block";
                                 //}
                        		}
                        	}
                        }
                 });
	         	}
	          }
	       }
	    }
    
    
    function updateKRATaskRatingAndComment(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,feedbackUserType,dataType,currUserType) {
    // 
    var taskRating = 0;
    var strComment = '';
    var chTaskRating = 0;
    var chStrComment = '';
    if(goalType == 'KRA') {
    taskRating = document.getElementById("hideGKTRating"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).value;
    strComment = document.getElementById("strComment"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).value;
    } else if(goalType == 'TARGET') {
    taskRating = document.getElementById("hideGTargetRating"+empId+"_"+goalid+"_"+goalFreqId).value;
    strComment = document.getElementById("strTargetComment"+empId+"_"+goalid+"_"+goalFreqId).value;
    } else if(goalType == 'GOAL') {
    taskRating = document.getElementById("hideGoalRating"+empId+"_"+goalid+"_"+goalFreqId).value;
    strComment = document.getElementById("strGoalComment"+empId+"_"+goalid+"_"+goalFreqId).value;
    }
    
    if(confirm('Are you sure, you wish to update rating & comment?')) {
         xmlhttp = GetXmlHttpObject();
         if (xmlhttp == null) {
                 alert("Browser does not support HTTP Request");
                 return;
         } else {
                 var xhr = $.ajax({
                        url : 'UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalid+'&goalFreqId='+goalFreqId+'&kraId='+kraId
                        	+'&kraTaskId='+kraTaskId+'&strComment='+encodeURIComponent(strComment)+'&taskRating='+taskRating
                        	+'&type=GoalKRA&operation=RC&goalType='+goalType+'&feedbackUserType='+feedbackUserType,
                        cache : false,
                        success : function(data) {
                        	if(data == "") {
                        	} else if(data.length > 1) {
                        		var allData = data.split("::::");
                        		getGoalKRADetails('GoalKRATarget_1',empId,dataType,currUserType,'GKT');
                        	}
                        }
                 });
         	}
    }
    }
    
    
    function updateTarget(id, val, measuretype, amount, dayHrs, empid, goalid, goalFreqId, accessFlag) {
    //alert("amount ===> "+amount+"==>dayHrs ===> "+dayHrs);
    if(confirm('Are you sure you wish to update target?')){
    var emptarget= "";
    if(measuretype=='Effort'){
    var mDays=document.getElementById(id+"mDays"+val).value;
    var msHrs=document.getElementById(id+"msHrs"+val).value;
    if(mDays==''){
    	mDays='0';
    }
    if(msHrs==''){
    	msHrs='0';
    }
    emptarget = mDays+'.'+msHrs;
    }else{			
    emptarget = document.getElementById(id+"emptarget"+val).value;
    }
    //alert("emptarget ===> "+emptarget);
        xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
                alert("Browser does not support HTTP Request");
                return;
        } else {
                var xhr = $.ajax({
                        url : 'UpdateTarget.action?tgoalid='+goalid + "&goalFreqId=" + goalFreqId + '&type=type&emptarget='+emptarget +'&empid=' + empid 
                        		+ '&measuretype='+measuretype + '&amount='+amount + '&dayHrs='+dayHrs + '&typeas=GoalKRA',
                        cache : false,
                        success : function(data) {
                        	//alert("Data ===> "+data.length);
                        	if(data == "") {
                        	} else if(data.length > 1) {
                        		var allData = data.split("::::");
                                document.getElementById(id+"targetStatusDiv"+val).innerHTML = allData[1];
                                document.getElementById(id+"ProBarDiv"+val).innerHTML = allData[2];
                                if(allData[1] == "Target Updated"){
                                	document.getElementById(id+"remarkSpan"+val).style.display = "block";
                                	if((parseFloat(amount) <= parseFloat(emptarget)) || (parseFloat(dayHrs) <= parseFloat(emptarget))) {
                                	  document.getElementById("TargetRatingDiv_"+empid+"_"+goalid+"_"+goalFreqId).style.display = "block";
                                  } else {
                                  	document.getElementById("TargetRatingDiv_"+empid+"_"+goalid+"_"+goalFreqId).style.display = "block";
                                  } 
                                }
                        	}
                        }
				});
			}
		}
    }
  
    
    function updateActualAchievedGoal(empId, goalid, goalFreqId, goalCount,weightage, type, status, empCount,dataType,currUserType) {
    
	    var achievedShare = document.getElementById("achievedShare_"+goalCount).value;
	    var actualAchieved = document.getElementById("actualAchieved_"+goalCount).value;
	    if(achievedShare == '') {
	       		alert("Please enter achieved share.");
	       	} else {
	       		var alertMsg = 'Are you sure, you wish to save actual achieved score?';
	       		if(type == 'SAVECLOSE' && status == 'NO') { 
	       			alertMsg = 'There are '+empCount+' other users on this goal, do you want to save actual achieved score and close all of them as well?';
	       		}
	   			
	       		if(confirm(alertMsg)) {
	   				xmlhttp = GetXmlHttpObject();
		        	if (xmlhttp == null) {
		                alert("Browser does not support HTTP Request");
		                return;
		       		} else {
		                var xhr = $.ajax({
		                   url : 'UpdateActualAchievedGoal.action?goalid='+goalid +'&empId=' + empId +'&goalFreqId=' + goalFreqId 
		                   		+ '&weightage='+weightage + '&achievedShare='+achievedShare + '&actualAchieved='+actualAchieved
		                   		+ '&type='+type,
		                   	cache : false,
		                   success : function(data) {
		                	   getGoalKRADetails('GoalKRATarget_1',empId,dataType,currUserType,'GKT');
		                	   
		                   }
		                 });
	    			}
	        	}
	         }
    }
    
    function getMemberData(empId, goalid, goalFreqId, type, assignedTarget, measureType,dataType,currUserType) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Target Status');
    	$("#modalInfo").show();
    	$.ajax({
    		url : "TargetStatus.action?goalid=" + goalid + "&goalFreqId=" + goalFreqId + "&empid=" + empId + "&type=" + type + "&assignedTarget=" 
    				+ assignedTarget + "&measureType=" + measureType+"&dataType="+dataType+"&currUserType="+currUserType,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    
    } 
    
    
    function GetXmlHttpObject() {
           if (window.XMLHttpRequest) {
                   // code for IE7+, Firefox, Chrome, Opera, Safari
                   return new XMLHttpRequest();
           }
           if (window.ActiveXObject) {
                   // code for IE6, IE5
                   return new ActiveXObject("Microsoft.XMLHTTP");
           }
           return null;
    }
   
    
    function viewAddComments1(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,feedbackUserType,dataType,currUserType) 
    	{
  			
  		var dialogEdit = '.modal-body';
  		$(dialogEdit).empty();
  		$(dialogEdit).html('<div id="the_div1"><div id="ajaxLoadImage"></div></div>');
  		$('.modal-title').html('New Comment');
  		$("#modalInfo").show();
  		$(".modal-dialog").width(50);
  		$(".modal-dialog").height(50);
  		$.ajax({
  			url : "GoalKRAComment.action?empId="+empId+"&goalid="+goalid+"&goalFreqId="+goalFreqId
			+"&kraId="+kraId+"&kraTaskId="+kraTaskId+"&goalType="+goalType+"&feedbackUserType="+feedbackUserType+"&dataType="+dataType+"&currUserType="+currUserType+"&pagefrom = GoalKRA_1",
  			cache : false,
  			success : function(data) {
  				$(dialogEdit).html(data);
  			}
  		});
  		
  		
  	}
    
    
    function viewAddComments(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,feedbackUserType,dataType,currUserType) 
	{
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div1"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('New Comment');
		$("#modalInfo").show();
		$(".modal-dialog").width(50);
		$(".modal-dialog").height(50);
		$.ajax({
			url : "GoalKRAComment_1.action?empId="+empId+"&goalid="+goalid+"&goalFreqId="+goalFreqId
				+"&kraId="+kraId+"&kraTaskId="+kraTaskId+"&goalType="+goalType+"&feedbackUserType="+feedbackUserType+"&dataType="+dataType+"&currUserType="+currUserType+"&pagefrom = GoalKRA_1",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
		
		
	}
    
    
    function viewManagerAndHRComments(empId, goalId, goalFreqId, kraId, kraTaskId, goalType) { 
    	
    	var strID = '';
    	//alert("strID == "+strID);
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Comments');
    	$("#modalInfo").show();
    	$.ajax({
    		url : "ViewManagerAndHRComentsOfGoalKRATarget.action?empId="+empId+"&goalId="+goalId+"&goalFreqId="+goalFreqId
    			+"&kraId="+kraId+"&kraTaskId="+kraTaskId+"&goalType="+goalType,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    
    function deleteGoalKRA(goalId,empId,from,typeas,dataType,currUserType){
    	//alert("typeas==>"+typeas);
    	if(confirm('Are you sure you want to delete this Goal?')) {
    		var strAction  = "RemoveEmpFromGoal.action?goalId="+goalId+"&empId="+empId
    				+"&from="+from+"&typeas="+typeas;
			$.ajax({ 
				type : 'POST',
				url: strAction,
				cache: true,
				success: function(result) {
					getGoalKRADetails('GoalKRATarget_1',empId,dataType,currUserType,'GKT');
				},
				error: function(result) {
					getGoalKRADetails('GoalKRATarget_1',empId,dataType,currUserType,'GKT');
				}
			});
  		}
    	
    }
    
    function viewPerformanceAllowanceData(goalTargetKraId, type) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Performance Incentives Details');
    	$("#modalInfo").show();
    	$.ajax({
    		url : "ViewGoalKRATargetAlignedPerformanceIncentives.action?goalTargetKraId="+goalTargetKraId+"&type="+type,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    function checkHrsLimit(empCnt, goalCnt) {
    	
    	var hrs = document.getElementById(empCnt+"msHrs"+goalCnt).value;
    	if(parseFloat(hrs) >= parseFloat("24")) {
    		document.getElementById(empCnt+"msHrs"+goalCnt).value = '';
    		return false;
    	}
    	return true;
    } 
    
    
    function checkHrsLimit(empCnt, goalCnt, empWorkHrs) {
    //alert("empWorkHrs==>"+empWorkHrs);
    	if(empWorkHrs == '0.0') {
   		 empWorkHrs = '8.0';
    	}
    	var hrs = document.getElementById(empCnt+"msHrs"+goalCnt).value;
    	if(parseFloat(hrs) >= parseFloat(empWorkHrs)) {
    		document.getElementById(empCnt+"msHrs"+goalCnt).value = '';
   			 return false;
   		 }
   		 return true;
    }
    //....end....
    
    function getAchievedValue(weightage, goalCount) {
	    var total = "";
	    var actualAchieved = document.getElementById("actualAchieved_"+goalCount).value;
	    if(parseFloat(actualAchieved) > 0 && parseFloat(weightage)>0) {
	       	total = (100 * parseFloat(actualAchieved)) / parseFloat(weightage);
	    	document.getElementById("achievedShare_"+goalCount).value = total;
		} else {
			document.getElementById("achievedShare_"+goalCount).value = '';
		}
    return true;
    }
    
</script>

<%
    UtilityFunctions uF=new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
    String sbData = (String) request.getAttribute("sbData");
    String strSearchJob = (String) request.getAttribute("strSearchJob");
    String proCount = (String)request.getAttribute("proCount");
    
    String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
    String strBaseUserType=(String) session.getAttribute(IConstants.BASEUSERTYPE);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    
    List<String> empList=(List<String>)request.getAttribute("empList");
    Map<String, String> hmHrIds = (Map<String, String>) request.getAttribute("hmHrIds");
    if(hmHrIds == null) hmHrIds = new HashMap<String, String>();
    
    Map<String, String> hmEmpCodeName = (Map<String, String>)request.getAttribute("hmEmpName");
    if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
    
    Map<String, Map<String, Map<String, List<List<String>>>>> hmGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmGoalKraEmpwise");
    if(hmGoalKraEmpwise == null) hmGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
    
    Map<String, List<String>> hmGoalDetails = (Map<String, List<String>>) request.getAttribute("hmGoalDetails");
    if(hmGoalDetails == null) hmGoalDetails = new HashMap<String, List<String>>();
    
    Map<String, Map<String, Map<String, List<List<String>>>>> hmIndividualGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmIndividualGoalKraEmpwise");
    if(hmIndividualGoalKraEmpwise == null) hmIndividualGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
    
    Map<String, List<String>> hmIndividualGoalDetails = (Map<String, List<String>>) request.getAttribute("hmIndividualGoalDetails");
    if(hmIndividualGoalDetails == null) hmIndividualGoalDetails = new HashMap<String, List<String>>();
    
    Map<String, List<List<String>>> hmKRATasks = (Map<String, List<List<String>>>) request.getAttribute("hmKRATasks");
    if(hmKRATasks == null) hmKRATasks = new HashMap<String, List<List<String>>>();
    
    Map<String, String> hmKRATaskStatusAndRating = (Map<String, String>) request.getAttribute("hmKRATaskStatusAndRating");
    if(hmKRATaskStatusAndRating == null) hmKRATaskStatusAndRating = new HashMap<String, String>();
    
    Map<String, String> hmTargetRatingAndComment = (Map<String, String>) request.getAttribute("hmTargetRatingAndComment");
    if(hmTargetRatingAndComment == null) hmTargetRatingAndComment = new HashMap<String, String>();
    
    Map<String, String> hmEmpwiseKRARating = (Map<String, String>) request.getAttribute("hmEmpwiseKRARating");
    if(hmEmpwiseKRARating == null) hmEmpwiseKRARating = new HashMap<String, String>();
    
    Map<String, String> hmEmpwiseGoalRating = (Map<String, String>) request.getAttribute("hmEmpwiseGoalRating");
    if(hmEmpwiseGoalRating == null) hmEmpwiseGoalRating = new HashMap<String, String>();
    
    Map<String, String> hmEmpwiseGoalAndTargetRating = (Map<String, String>) request.getAttribute("hmEmpwiseGoalAndTargetRating");
    if(hmEmpwiseGoalAndTargetRating == null) hmEmpwiseGoalAndTargetRating = new HashMap<String, String>();
    	
    Map<String, String> hmEmpwiseKRACnt = (Map<String, String>) request.getAttribute("hmEmpwiseKRACnt");
    if(hmEmpwiseKRACnt == null) hmEmpwiseKRACnt = new HashMap<String, String>();
    
    
    Map<String, List<List<String>>> hmEmpKra = (Map<String, List<List<String>>>)request.getAttribute("hmEmpKra");
    if(hmEmpKra == null) hmEmpKra = new HashMap<String, List<List<String>>>();
    
    Map<String, String> empImageMap = (Map<String, String>)request.getAttribute("empImageMap");
    
    Map<String, String> hmKraAverage = (Map<String, String>)request.getAttribute("hmKraAverage");
    if(hmKraAverage == null) hmKraAverage = new HashMap<String, String>();
    
    
    Map<String, String> hmTargetValue = (Map<String, String>)request.getAttribute("hmTargetValue");
    if(hmTargetValue == null) hmTargetValue = new HashMap<String,String>();
    
    Map<String, String> hmTargetID = (Map<String, String>)request.getAttribute("hmTargetID");
    if(hmTargetID == null) hmTargetID = new HashMap<String,String>();
     
    Map<String, String> hmTargetRemark = (Map<String, String>)request.getAttribute("hmTargetRemark");
    if(hmTargetRemark == null) hmTargetRemark = new HashMap<String,String>();
    
    Map<String, String> hmUpdateBy = (Map<String, String>)request.getAttribute("hmUpdateBy");
    if(hmUpdateBy == null) hmUpdateBy = new HashMap<String,String>();
    
    List<String> alCheckList = (List<String>) request.getAttribute("alCheckList"); ;
    if (alCheckList == null) alCheckList = new ArrayList<String>();
    
    String strCurrency = (String) request.getAttribute("strCurrency"); 
    
    Map<String, String> hmCheckGTWithAllowance = (Map<String, String>)request.getAttribute("hmCheckGTWithAllowance");
    if(hmCheckGTWithAllowance == null) hmCheckGTWithAllowance = new HashMap<String, String>();
    
    Map<String, String> hmCheckKWithAllowance = (Map<String, String>)request.getAttribute("hmCheckKWithAllowance");
    if(hmCheckKWithAllowance == null) hmCheckKWithAllowance = new HashMap<String, String>();
    
    
    Map<String, String> hmEmpwiseGoalAndTargetEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseGoalAndTargetEmpRating");
    if(hmEmpwiseGoalAndTargetEmpRating == null) hmEmpwiseGoalAndTargetEmpRating = new HashMap<String, String>();
    
    Map<String, String> hmKRATaskEmpRating = (Map<String, String>)request.getAttribute("hmKRATaskEmpRating");
    if(hmKRATaskEmpRating == null) hmKRATaskEmpRating = new HashMap<String, String>();
    
    Map<String, String> hmEmpwiseKRAEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseKRAEmpRating");
    if(hmEmpwiseKRAEmpRating == null) hmEmpwiseKRAEmpRating = new HashMap<String, String>();
    
    Map<String, String> hmEmpwiseGoalEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseGoalEmpRating");
    if(hmEmpwiseGoalEmpRating == null) hmEmpwiseGoalEmpRating = new HashMap<String, String>();
    
    Map<String, String> hmOrientationViewAccess = (Map<String, String>) request.getAttribute("hmOrientationViewAccess");
    if(hmOrientationViewAccess == null) hmOrientationViewAccess = new HashMap<String,String>();
    
    Map<String, String> hmOrientationEditAccess = (Map<String, String>) request.getAttribute("hmOrientationEditAccess");
    if(hmOrientationEditAccess == null) hmOrientationEditAccess = new HashMap<String,String>();
    
    List<String> membersAccessList = (List<String>)request.getAttribute("orientationMembersList");
    if(membersAccessList == null ) membersAccessList = new ArrayList<String>();
    
    Map<String, String> hmEmpSuperIds = (Map<String, String>)request.getAttribute("hmEmpSuperIds");
    if(hmEmpSuperIds == null) hmEmpSuperIds = new HashMap<String, String>();
    
    Map<String, String> hmEmpUserId = (Map<String, String>)request.getAttribute("hmEmpUserId");
    if(hmEmpUserId == null) hmEmpUserId = new HashMap<String, String>();
    
    Map<String, String> hmUserTypeMap = (Map<String, String>)request.getAttribute("hmUserTypeMap");
    if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
    
    Map<String, String> hmKraTotalTaskCount = (Map<String, String>)request.getAttribute("hmKraTotalTaskCount");
    if(hmKraTotalTaskCount == null) hmKraTotalTaskCount = new HashMap<String, String>();
    
    Map<String, String> hmKraCompletedPercentage = (Map<String, String>)request.getAttribute("hmKraCompletedPercentage");
    if(hmKraCompletedPercentage == null) hmKraCompletedPercentage = new HashMap<String, String>();
    
    Map<String, List<String>> hmActualAchievedGoal = (Map<String, List<String>>)request.getAttribute("hmActualAchievedGoal");
    if(hmActualAchievedGoal == null) hmActualAchievedGoal = new HashMap<String, List<String>>();
    
    Map<String, String> hmEmpWorkingHrs = (Map<String, String>)request.getAttribute("hmEmpWorkingHrs");
    if(hmEmpWorkingHrs == null) hmEmpWorkingHrs = new HashMap<String,String>();
    
    String dataType = (String) request.getAttribute("dataType");
    String currUserType = (String) request.getAttribute("currUserType");
    String strEmpId = (String)request.getParameter("strEmpId");
    String strperspective = (String)request.getAttribute("strPerspective");
    
    Map<String, Map<String, String>> hmPerspectiveData = (Map<String, Map<String, String>>) request.getAttribute("hmPerspectiveData");
    if(hmPerspectiveData==null) hmPerspectiveData = new HashMap<String, Map<String, String>>();
    
    String Perspectcolor = null;
	String PerspectName = null;
    
    %>
	 
            <div class="col-md-12 col_no_padding">
                <div class="box box-none">
                    <div class="box-header with-border">
                  	<h3 class="box-title" >
					<%String empname = hmEmpCodeName.get(strEmpId);%>
					Balance Scorecard assigned to <strong><%=uF.showData(empname,"Employee") %></strong>
					</h3>
	            </div>

                       <div class="box-body" style="padding: 5px; overflow-y: auto; display: block; width:100%; margin-left:0px !important;">
                 			   <ul style="padding-left:0px;margin-left:-10px;margin-right:-10px;">
                                   <%    int i = 0;
                                     if(strEmpId != null && uF.parseToInt(strEmpId) > 0) {  
                                       	int kraSize = 0;
                                       	Map<String, Map<String, List<List<String>>>> hmGoalKraSuperIdwise = hmGoalKraEmpwise.get(strEmpId);
                                                                                        	
                                       	List<List<String>> empKraOuterList = hmEmpKra.get(strEmpId);		
                                       if(hmGoalKraSuperIdwise!=null && !hmGoalKraSuperIdwise.isEmpty()) { %>
                                   <%
                                       Iterator<String> itGoalSIKRA = hmGoalKraSuperIdwise.keySet().iterator();
                                       int goalCount = 0;
                                       while(itGoalSIKRA.hasNext()) {
                                       	String superId = itGoalSIKRA.next();								
                                       	Map<String, List<List<String>>> hmGoalKra = hmGoalKraSuperIdwise.get(superId);
                                       	if(hmGoalKra == null) hmGoalKra = new HashMap<String, List<List<String>>>();
                                       	String strBorder = "";
                                       	String strHr = "";
                                       	String strMargin = "0px 0px 0px 25px";
                                       	if(hmGoalKra.size() >1) {
                                       		strBorder = "2px solid #3F82BF;";
                                       		strMargin = "10px 0px 10px 25px";
                                       		strHr = "<hr width=\"3%\" style=\"float: left; \">";
                                       	}
                                       	%>
                                   <li style="float: left; width:100%; border-left: <%=strBorder %>">
                                       <%=strHr %>
                                       <ul style="float: left; padding-left:0px;">
                                           <%
                                               Iterator<String> itgKRA = hmGoalKra.keySet().iterator();
                                               while(itgKRA.hasNext()) {
                                               	goalCount++;
                                               	String goalAndFreqId = itgKRA.next();
                                               	List<String> gInnerList = hmGoalDetails.get(goalAndFreqId);
                                                String goalid = gInnerList.get(1);
                                               	String goalFreqId = gInnerList.get(32);
                                               	String goalPerspectiveId = gInnerList.get(39);
                                             	Boolean align_with_perspective = uF.parseToBoolean(gInnerList.get(40));
                                             	Map<String, String> hmPerspectData = hmPerspectiveData.get(goalPerspectiveId);
                                              // 	System.out.println("PERSPECTIVE_COLOR ===>> " +hmPerspectData.get("PERSPECTIVE_COLOR"));
                                             //	System.out.println("PERSPECTIVE_NAME ===>> " +hmPerspectData.get("PERSPECTIVE_NAME"));
                                             	if(align_with_perspective){
                                            	  if(hmPerspectData!=null && hmPerspectData.size() > 0 ){
                                              		 Perspectcolor = hmPerspectData.get("PERSPECTIVE_COLOR");
                                              	 	PerspectName = hmPerspectData.get("PERSPECTIVE_NAME");
                                              		}
                                             	}else{
                                             		Perspectcolor = " ";
                                             		PerspectName = " ";
                                             	}
                                              	
                                               		double avgGoalRating = 0.0d;
                                               		double dblWeightScore = 0.0d;
                                               		
                                               		if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                               			String goalRating = hmEmpwiseGoalRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
                                               			String goalTaskCount = hmEmpwiseGoalRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                               			
                                               			String goalEmpRating = hmEmpwiseGoalEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
                                               			String goalEmpTaskCount = hmEmpwiseGoalEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                               			if(uF.parseToInt(goalTaskCount) > 0 || uF.parseToInt(goalEmpTaskCount) > 0) {
                                               				avgGoalRating = (uF.parseToDouble(goalRating) + uF.parseToDouble(goalEmpRating)) / (uF.parseToInt(goalTaskCount) + uF.parseToInt(goalEmpTaskCount));
                                               			}
                                               			dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                                               		} else {
                                               			double avgEmpGoalRating = 0;
                                               			avgGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_RATING"));
                                               			if(uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT")) > 0) {
                                               				avgEmpGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_RATING"))/ uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT"));
                                               				if(avgGoalRating > 0) {
                                               					avgGoalRating = (avgGoalRating + avgEmpGoalRating) / 2; 
                                               				} else {
                                               					avgGoalRating = avgEmpGoalRating;
                                               				}
                                               			}
                                               			dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                                               		}
                                               	  
                                               boolean flag = true;
                                               if(gInnerList.get(34)!= null && uF.parseToInt(gInnerList.get(34)) > 5) {
                                               flag = false;
                                               }	
                                               String orientaionKey = gInnerList.get(34)+"_"+strUserTypeId;
                                               if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                               orientaionKey = gInnerList.get(34)+"_"+strBaseUserTypeId;
                                               }
                                               String userId = gInnerList.get(36);
                                               
                                               /* System.out.println("goal Type==>"+gInnerList.get(20)+"==>orientaionKey==>"+orientaionKey);
                                               System.out.println("strEmpId==>"+gInnerList.get(35)+"==>emp==>"+strSessionEmpId);
                                               System.out.println("===========================view==>"+uF.parseToBoolean(hmOrientationViewAccess.get(orientaionKey)) +"=============================================="); */
                                               if(flag || (strUserType != null && (strUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationViewAccess.get(orientaionKey)) || gInnerList.get(35).contains(","+strSessionEmpId+",")))) {
                                               %>
										<div>
										<!-- <div style="padding-left: 1% !important;border-left: 20px solid orange;float: left;margin: 10px 0px;border-radius: 5px;"> -->

											<!-- <div style="writing-mode: vertical-rl;margin-left: -27px;float: left;width: 1%;height: 500px;text-align: -moz-center;transform: rotate(180deg);">RAHUL</div> -->
											  
                                    <li style="float: left;	padding: 5px;background-color:#E6E6E6;" >
                                    
                                    <div style="float: left; width: 100%; padding-left: 15px;"  >
                                          	 <div style="float: right; margin-left: 2%;">
                                                                   <% if(uF.parseToInt(gInnerList.get(17)) > 0) { %>
                                                                   <a href="javascript:void(0)" onclick="goalChart('<%=gInnerList.get(8) %>','<%=gInnerList.get(7) %>','<%=gInnerList.get(6) %>','<%=goalid %>')" title="Goal Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>
                                                                   <% } else {
                                                                       String typeas = "Goal";
                                                                       if(gInnerList.get(20) != null && !gInnerList.get(20).equals("")) {
                                                                        if(gInnerList.get(20).equals("KRA")){
                                                                        	typeas = "KRA";
                                                                        }
                                                                        
                                                                        if(gInnerList.get(20).equals("Measure")){
                                                                       	 	typeas = "Target";
                                                                        }
                                                                       }
                                                                       %>
                                                                   <% if(flag || (strUserType != null && (strUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>
	                                                                   <%if(!uF.parseToBoolean(gInnerList.get(12))) { %>
	                                                                   <a href="javascript:void(0)" onclick="editGoal('<%=gInnerList.get(1)%>', '<%=typeas %>','<%=strEmpId%>','<%=gInnerList.get(3)%>','<%=currUserType %>','<%=gInnerList.get(41)%>')" title="Edit Initiative"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i> </a>
	                                                                   <% if(!alCheckList.contains(gInnerList.get(1)) ) { %>
	                                                                   <a href="javascript:void(0);" title="Delete Initiative"
	                                                                       onclick="deleteGoalKRA('<%=gInnerList.get(1)%>','<%=strEmpId%>','KRA','<%=typeas %>','<%=dataType %>','<%=currUserType %>')"><i
	                                                                       class="fa fa-trash" aria-hidden="true"></i> </a>
	                                                                   <% } else { %>
		                                                                   <a href="javascript:void(0)" onclick="alert('Employee has already updated the Goal. You can not delete this Initiative.');">
	                                                                   		<i class="fa fa-trash" aria-hidden="true"></i> </a>
	                                                                   <% } 
	                                                                       }
	                                                                       } %>
	                                                                   <% } %>
	                                                               <% if(flag || (strUserType != null && (strUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>    
	                                                                   <%if(!uF.parseToBoolean(gInnerList.get(12))) { %>
	                                                                   <a href="javascript:void(0);" onclick="closeGoal('<%=gInnerList.get(1)%>','close','<%=strEmpId%>','<%=dataType%>','<%=currUserType%>');" title="Close Initiative">
	                                                                       <i class="fa fa-times-circle-o" aria-hidden="true"></i>
	                                                                   </a>
	                                                                   <% } else { %>
		                                                                   <a href="javascript:void(0);" onclick="closeGoal('<%=gInnerList.get(1)%>','view','<%=strEmpId%>','<%=dataType%>','<%=currUserType%>');"  title="Close Initiative Reason">
		                                                                   		<i class="fa fa-comment-o" aria-hidden="true"></i>
		                                                                   </a>
		                                                                   <a href="javascript:void(0);" onclick="openGoalForLive('<%=gInnerList.get(1)%>','open','<%=strEmpId%>','<%=dataType%>','<%=currUserType%>');"  title="Open Initiative for Live">
		                                                                   		<i class="fa fa-reply" aria-hidden="true"></i>
		                                                                   </a>
	                                                                   <% } %>
                                                                   <% } %>
                                                                   
                                                      </div> 
                                                      <%if(PerspectName != null && Perspectcolor!=null){ %>
                                                      <div align="center" style="float: left; width:3%; padding-left: 5px; margin: 30px 10px 10px 0px; border-radius:5px; height:400px; writing-mode:vertical-lr; transform: rotate(180deg); font-size:16px; font-weight:bold; background-color:<%=Perspectcolor %>">
                                                      		 <%-- <div align="center" style="float: left; width: 3%;word-wrap:break-word;margin: 30px 10px 10px 0px;padding:150px 17px 0px 10px;border-radius:5px;height:400px;background-color:<%=Perspectcolor%>"> --%>
                                                       			 <%=PerspectName%>
															</div>
														<%} %> 
                                                     <div style="float: left; width: 38%;background-color:white;margin-top:30px; padding-left:10px;border-radius:5px;">
                                                    	 <div style="float: left; width: 100%; margin-top:10px;">
                                                     		<div class="<%=gInnerList.get(10) %>"
                                                                   style="float: left; line-height: 18px; font-size: 14px;"">
                                                                   <b>Objective </b>
                                                                   <br><%=gInnerList.get(3) %>
                                                                   <%=(gInnerList.get(33) != null && !gInnerList.get(33).equals("")) ? "["+gInnerList.get(33)+"]" : "" %></br>
                                                               
                                                               </div>
                                                               <div style="float: left; width: 100%; border-right: 1 px solid #CCCCCC;font-size: 14px !important; margin-top:-25px;">
                                                               	<br></br>
                                                                   <span class="<%=gInnerList.get(10) %>" style="float: left;  font-size: 14px; line-height: 18px;"><b>Goal
                                                                <br>   </b><%=uF.showData(gInnerList.get(13), "-") %></br></span>
                                                               </div>
                                                                <div style="float: left; width: 100%;">
                                                                   <span class="<%=gInnerList.get(10) %>" style="float: left;font-size: 14px; line-height: 18px;">-
                                                                   assigned by <%=gInnerList.get(4)%>, attribute <%=gInnerList.get(14) %>, effective date <%=gInnerList.get(15) %>, due date <%=gInnerList.get(5)%>
                                                                 
                                                                   </span>
                                                               </div>
                                                                 <%
                                                                   if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                                                   	int kraTotalTaskCount = 0;
                                                                   	double totalKraStatus =0;
                                                                   	double kraStatus = 0;
                                                                   	totalKraStatus = uF.parseToDouble(hmKraCompletedPercentage.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_PERCENTAGE"));
                                                                   	
                                                                   	kraTotalTaskCount= uF.parseToInt(hmKraCompletedPercentage.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_TASKCOUNT")); 
                                                                   	kraStatus = totalKraStatus / kraTotalTaskCount;
                                                                   	
                                                                   %>
                                                                   <div id="KraPercentage_<%=strEmpId %>_<%=goalid %>"
                                                                   style="float: left; margin: 0px 25px 3px 25px; width:70%;">
                                                                   <div class="anaAttrib1">
                                                                       <span style="margin-left:<%=kraStatus > 95 ? kraStatus - 10 : kraStatus - 4%>%;"><%=uF.showData(""+kraStatus, "0")%>%</span>
                                                                   </div>
                                                                   <div id="outbox" style="width: 100% !important;">
                                                                       <% if (kraStatus < 33.33) { %>
                                                                       <div id="redbox" style="width: <%=uF.showData(""+kraStatus, "0") %>%;"></div>
                                                                       <% } else if (kraStatus >= 33.33 && kraStatus < 66.67) { %>
                                                                       <div id="yellowbox" style="width: <%=uF.showData(""+kraStatus, "0")%>%;"></div>
                                                                       <% } else if (kraStatus >= 66.67) { %>
                                                                       <div id="greenbox" style="width: <%=uF.showData(""+kraStatus, "0")%>%;"></div>
                                                                       <% } %>
                                                                   </div>
                                                                   <div class="anaAttrib1"
                                                                       style="float: left; width: 100%;">
                                                                       <span style="float: left; margin-left: -4%;">0%</span>
                                                                       <span style="float: right; margin-right: -10%;">100%</span>
                                                                   </div>
                                                               </div>
                                                                <% } %>
                                                           </div>
                                                     		<div style="float: left; width: 100%;">
                                                     			<script type="text/javascript">
					                                                   $(function() {
					                                                   	$('#starPrimaryG<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>').raty({
					                                                   		readOnly: true,
					                                                   		start: <%=avgGoalRating %>,
					                                                   		half: true,
					                                                   		targetType: 'number'
					                                                   });
					                                                   });
				                                               		</script>
                                               
                                                                   <div id="starPrimaryG<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>" style="float: left;margin-top:10px; width:100%;" ></div>
                                                                   <div style="float: right; margin-top:10px;">
                                                                     <b>Rated Score:</b>
                                                                       <%=uF.formatIntoOneDecimalWithOutComma(dblWeightScore) %>/<%=uF.parseToDouble(gInnerList.get(16)) %>
                                                                     &nbsp;&nbsp;</div>
                                                               	</div>
                                                               	  <%--updated by kalpana on 21/10/2016 - start --%>
                                                               <div style="float: left; width: 99%; margin-top: 5px;">
                                                                   <%	List<String> goalAchieved = hmActualAchievedGoal.get(strEmpId+"_"+goalid+"_"+goalFreqId);
                                                                       if(goalAchieved == null) goalAchieved = new ArrayList<String>();
                                                                       String strActualAchieved = uF.formatIntoOneDecimalWithOutComma(dblWeightScore);
                                                                       double dblActualAchieved = (dblWeightScore * 100) / uF.parseToDouble(gInnerList.get(16));
                                                                       String strAchievedShare = uF.formatIntoOneDecimalWithOutComma(dblActualAchieved);
                                                                       if(goalAchieved != null && goalAchieved.size()>0) {
                                                                    	   strAchievedShare = goalAchieved.get(4);
                                                                    	   strActualAchieved = goalAchieved.get(5);
                                                                       }
                                                                       %>
                                                                   <table cellspacing="0" cellpadding="2" class="table table-bordered">
                                                                       <tr>
                                                                           <th colspan="4" style="padding: 0px;">
                                                                               <%if(strUserType !=null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {%>
                                                                               Finalise & Close: <% } else { %> Finalisation Score:
                                                                               <% } %>
                                                                           </th>
                                                                       </tr>
                                                                       <tr style="text-align: center !important;">
                                                                           <th style="width: 18%; padding: 0px; text-align:center;">Weightage</th>
                                                                           <th style="width: 23%; padding: 0px; text-align:center;">Actual Achieved</th>
                                                                           <th style="width: 24%; padding: 0px; text-align:center;">Achieved Share</th>
                                                                       </tr>
                                                                       <tr  style="text-align: center !important;">
                                                                           <td><%=uF.parseToDouble(gInnerList.get(16))%></td>
                                                                           <td><input type="text" name="actualAchieved" id="actualAchieved_<%=goalCount%>" style="width: 40px !important;"
                                                                               value="<%=uF.parseToDouble(strActualAchieved) > 0 ? strActualAchieved : "" %>" onkeypress="return isNumberKey(event)"
                                                                               onkeyup="getAchievedValue('<%=uF.parseToDouble(gInnerList.get(16))%>','<%=goalCount%>')"
                                                                               <%if((strUserType !=null && !strUserType.equals(IConstants.HRMANAGER) && !strUserType.equals(IConstants.ADMIN)) || uF.parseToBoolean(gInnerList.get(12))) { %>
                                                                               readonly="readonly" <% } %> />
                                                                           </td>
                                                                           <td>
                                                                           		<input type="text" name="achievedShare" id="achievedShare_<%=goalCount%>" value="<%=uF.parseToDouble(strAchievedShare) > 0 ? strAchievedShare : "" %>" style="width: 40px !important;" readonly="readonly" />
                                                                           </td>
                                                                       </tr>
                                                                       <tr>
                                                                          <td colspan="3" style="border:none; text-align: center; padding-top:10px !important">
                                                   							<%if(strUserType !=null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN)) && (dataType == null || dataType.equals("L"))) { %>
                                                                           <input type="button" name="saveBtn" id="saveBtn" class="btn btn-primary" style="width: 60px !important; margin-top: 1px;" value="Save"
                                                                               	onclick="updateActualAchievedGoal('<%=strEmpId%>','<%=goalid%>','<%=goalFreqId%>','<%=goalCount%>','<%=uF.parseToDouble(gInnerList.get(16))%>', 'SAVE', '<%=gInnerList.get(37) %>', '<%=gInnerList.get(38) %>','<%=dataType%>','<%=currUserType %>')" />&nbsp;&nbsp;
                                                                             
                                                                            <input type="button" name="saveBtnClose" id="saveBtnClose" class="btn btn-primary" style="margin-top: 1px;" value="Finalize"
                                                                                   	onclick="updateActualAchievedGoal('<%=strEmpId%>','<%=goalid%>','<%=goalFreqId%>','<%=goalCount%>','<%=uF.parseToDouble(gInnerList.get(16))%>', 'SAVECLOSE', '<%=gInnerList.get(37) %>', '<%=gInnerList.get(38) %>','<%=dataType%>','<%=currUserType %>')" />
                                                                           <% } %>
                                                                           </td>
                                                                       </tr>
                                                                   </table>
                                                               </div>
                                                    		 </div>
                                            
                                               <%
                                                   boolean accessFlag = false;
                                                   boolean managerAccessFlag = false;
                                                   if(membersAccessList.contains(gInnerList.get(34)+"_5") || membersAccessList.contains(gInnerList.get(34)+"_13")) {
                                                   accessFlag = true;
                                                   }
                                                   //int superIdUserTypeId = uF.parseToInt(hmEmpUserId.get(hmEmpSuperIds.get(strEmpId)));
                                                   if(uF.parseToInt(strSessionEmpId) == uF.parseToInt(hmEmpSuperIds.get(strEmpId))) {
                                                   managerAccessFlag = true;
                                                   }
                                                   //System.out.println("CEO oerien==>"+gInnerList.get(34)+"_5"+"==>Manager==>"+gInnerList.get(34)+"_2");
                                                   //System.out.println("emp==>"+strEmpId+"==>superId==>"+hmEmpSuperIds.get(strEmpId)+"==>superIdUserType==>"+superIdUserTypeId);
                                                     //String superIdUser = hmUserTypeMap.get(String.valueOf(superIdUserTypeId));
                                                     
                                                   if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                                   List<List<String>> goalOuterList = hmGoalKra.get(goalAndFreqId);
                                                  %>
													<div style="float: left; width: 57%; margin-top:-5px;">                                                  
                                                  <%
                                                    for(int j=0; goalOuterList!=null && !goalOuterList.isEmpty() && j<goalOuterList.size(); j++) {
                                                    List<String> innerList = goalOuterList.get(j);
                                                    List<List<String>> taskOuterList = hmKRATasks.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11));
                                                   
                                                    double avgKRARating = 0.0d;
                                                   	double dblKRAWeightScore = 0.0d;
                                                   	String kraRating = hmEmpwiseKRARating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                                   	String kraTaskCount = hmEmpwiseKRARating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                                   	
                                                   	if(uF.parseToInt(kraTaskCount) > 0) {
                                                   		avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
                                                   	}
                                                   	String strUserCnt = "";
                                                   	if(hmKRATaskEmpRating != null && !hmKRATaskEmpRating.isEmpty()) {
                                                   		String strUserRating = hmEmpwiseKRAEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                                   		strUserCnt = hmEmpwiseKRAEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                                   		double avgUserRating = 0;
                                                   		if(uF.parseToInt(strUserCnt)>0) {
                                                   			avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                                                   		}
                                                   		if(avgKRARating > 0 && avgUserRating > 0) {
                                                   			avgKRARating = (avgKRARating + avgUserRating) / 2;
                                                   		} else if(avgUserRating > 0) {
                                                   			avgKRARating = avgUserRating;
                                                   		}
                                                   	}
                                                   	dblKRAWeightScore = (avgKRARating * uF.parseToDouble(innerList.get(27))) / 5;
                                                   %> <script type="text/javascript">
                                                   $(function() {
                                                   	$('#starPrimaryGK<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>_<%=j%>').raty({
                                                   		readOnly: true,
                                                   		start: <%=avgKRARating %>,
                                                   		half: true,
                                                   		targetType: 'number'
                                                   });
                                                   });
                                               </script>
                                              
                                                <ul style="float: left; width:100%; margin-top:15px;">
                                                <li  style="float: left; width: 100%; margin-left:-10px; margin-bottom:-10px; background-color:white;border-radius:5px;">
                                                  <div id = "the_div1">
                                                   <div  style="float: left; width: 100%;">
                                                             <div style="float: left; margin: 10px 0px 0px 10px">
                                                               <span class="<%=innerList.get(10) %>" style="margin: 0px 0px 0px 0px; float: left;"><strong>Initiative:</strong>&nbsp;<%=uF.showData(innerList.get(2), "-") %>
                                                               <span style="float: left; width: 100%;"> <% if(uF.parseToBoolean(hmCheckKWithAllowance.get(innerList.get(11)))) { %>
                                                               <span style="font-style: italic;"><a href="javascript:void(0);" style="font-size: 10px; font-weight: normal;"
                                                                   onclick="viewPerformanceAllowanceData('<%=innerList.get(11) %>', 'K');">This Initiative has Performance Incentives attached</a>
                                                               </span> <% } else { %> <span style="font-size: 10px; font-style: italic; color: gray;">This Initiative has no Performance Incentives attached</span> <% } %> </span> </span>
                                                           </div>
                                                           <div style="float: right;padding-left:10px; width: 100%; margin-top:5px; border-bottom: 1px solid #CCCCCC;">
                                                               <div   id="starPrimaryGK<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>_<%=j%>" style="float: left;  width: 100%;margin-top:-5px;"></div>
                                                               <div style="float: right;padding-right:10px">
                                                                   <b>Rated Score:</b>
                                                                   <%=uF.formatIntoOneDecimalWithOutComma(dblKRAWeightScore) %>/<%=uF.parseToDouble(innerList.get(27)) %>
                                                               </div>
                                                           </div>
                                                     </div>
                                                    <% if(taskOuterList!=null && !taskOuterList.isEmpty()) { %>
                                                       <div style="float: left; width: 100%; ">
                                                           <div style="float: left; width: 100%; line-height: 12px; margin-top:10px;margin-left:-5px;" >
                                                               <span style="font-weight: bold; margin-left: 30px; color: gray;">Tasks:</span>
                                                           </div>
                                                           <% 
                                                               //System.out.println("taskOuterList==>"+taskOuterList.size());
                                                               for(int a=0; a<taskOuterList.size(); a++) {
                                                               
                                                               List<String> taskInnerList = taskOuterList.get(a);
                                                               //System.out.println("EMPID ===>> " + strEmpId + " -- KRAID ===>> " + innerList.get(11) + " -- TASKID ===>> " + taskInnerList.get(0));
                                                               String taskStatusPercent = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_STATUS");
                                                               //System.out.println("taskStatusPercent ===>> " + taskStatusPercent);
                                                               String managerRating = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_RATING");
                                                               String hrRating = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_RATING");
                                                               String managerComment = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_COMMENT");
                                                               String hrComment = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_COMMENT");
                                                               
                                                               String hrMngrReview = "block";
                                                               if(uF.parseToInt(taskStatusPercent) >= 100 || uF.parseToInt(taskInnerList.get(2)) == 1) {
                                                               	hrMngrReview = "block";
                                                               }
                                                               double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
                                                               if(managerRating == null) {
                                                               	avgRating = uF.parseToDouble(hrRating);
                                                               } else if(hrRating == null) {
                                                               	avgRating = uF.parseToDouble(managerRating);
                                                               }
                                                               
                                                               String strUserTaskCnt = "";
                                                               if(hmKRATaskEmpRating != null && !hmKRATaskEmpRating.isEmpty()) {
                                                               	String strUserRating = hmKRATaskEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_RATING");
                                                               	strUserTaskCnt = hmKRATaskEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_COUNT");
                                                               	double avgUserRating = 0;
                                                               	if(uF.parseToInt(strUserCnt)>0) {
                                                               		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserTaskCnt);
                                                               	}
                                                               	if(avgRating > 0 && avgUserRating > 0) {
                                                               		avgRating = (avgRating + avgUserRating) / 2;
                                                               	} else if(avgUserRating > 0) {
                                                               		avgRating = avgUserRating;
                                                               	}
                                                               }
                                                               
                                                               
                                                               int commentCnt = 0;
                                                               if(managerComment != null && !managerComment.equals("null")) {
                                                               	commentCnt++;
                                                               }
                                                               if(hrComment != null && !hrComment.equals("null")) {
                                                               	commentCnt++;
                                                               }
                                                               %>
                                                                 
                                                           <div style="float: left; width: 100%;">
                                                               <script type="text/javascript">
                                                                   $(function() {
                                                                   	$('#starPrimaryGKT<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
                                                                   		readOnly: true,
                                                                   		start: <%=avgRating %>,
                                                                   		half: true,
                                                                   		targetType: 'number'
                                                                   });
                                                                   });
                                                               </script>
                                                            <!-- <div style="float: left;width:100%;"> -->
                                                            <div style="float: left; margin-left:10px; line-height: 15px; width: 40%; margin-top:10px;"><%=uF.showData(taskInnerList.get(1), "-") %></div>
																 <div id="KTProBarDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; width: 30%;">                                                                   
																	<div class="anaAttrib1">
                                                                       <span style="margin-left:<%=uF.parseToDouble(taskStatusPercent) > 95 ? uF.parseToDouble(taskStatusPercent) - 10 : uF.parseToDouble(taskStatusPercent) - 4%>%;"><%=uF.showData(taskStatusPercent, "0")%>%</span>
                                                                   </div>
                     												<div id="outbox" style="margin-left:10px">
                                                                       <% if (uF.parseToDouble(taskStatusPercent) < 33.33) { %>
                                                                       <div id="redbox" style="width: <%=uF.showData(taskStatusPercent, "0") %>%;"></div>
                                                                       <% } else if (uF.parseToDouble(taskStatusPercent) >= 33.33 && uF.parseToDouble(taskStatusPercent) < 66.67) { %>
                                                                       <div id="yellowbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
                                                                       <% } else if (uF.parseToDouble(taskStatusPercent) >= 66.67) { %>
                                                                       <div id="greenbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
                                                                       <% } %>
                                                                   </div>
                                                                  <div class="anaAttrib1" style="float: left; width: 100%; margin-left:0px;">
                                                                       <span style="float: left; margin-left: -4%;">0%</span>
                                                                       <span style="float: right; margin-left: -10%;">100%</span>
                                                                   </div>
                                                               </div>
                                                               <%
                                                                   String addedBy = taskInnerList.get(4);
                                                                   if(flag ||  (strUserType != null && (strUserType.equals(IConstants.ADMIN) || uF.parseToInt(addedBy)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { 
                                                                	   if(dataType == null || dataType.equals("L")) {
                                                                   %>
	                                                               <div style="float: left;">
	                                                                   <input type="text" name="completedPercent_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" id="completedPercent_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" value="<%=uF.showData(taskStatusPercent, "") %>" style="width: 30px !important; margin-left:18px; margin-top:10px;" onkeypress="return isNumberKey(event)" />
	                                                                    <a onclick="updateCompletedPercent('<%=strEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>','<%=taskInnerList.get(0) %>', 'KRA','<%=accessFlag %>');" href="javascript:void(0);" title="Update">
	                                                                      <i class="fa fa-pencil-square-o" aria-hidden="true" style = "margin-left:20px;"></i>
                             											</a> <br />
 																		<span id="completedPercentStatusSpan_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>	                                                              
 																 </div>
                                                               <% } %>
                                                               <%} else { %>
                                                               <div style="float: left; margin: 20px 5px 3px 15px; width: 7%;">
                                                                   <label style="width: 40px !important;"><%=uF.showData(taskStatusPercent, "") %></label>
                                                                   <br />
 																	<span id="completedPercentStatusSpan_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>                                                               
 																</div>
                                                               <% } %>
 																 <div id="GivenTaskRatingDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; width:100%">
                                                                     <div style="float: left; padding-left: 0px; margin-top:0px; ">		
                                                                     <% 
                                                                     	String feedbackUserType = null;
                                                                     	if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                                                           	feedbackUserType = strBaseUserType;
                                                                           }
                                                                    	    String taskInnerListdata =  taskInnerList.get(0);
                                                                    	    String innerListdata = innerList.get(11);
                                                                    	    
                                                                   		%>
																	 <a style="height: 1px; width: 2px; padding-top:2px;" href="javascript:void(0)" onclick="updateComment('<%=strEmpId %>','<%=goalid %>', '<%=goalFreqId %>', '<%=innerListdata %>','<%=taskInnerList.get(0) %>', 'KRA','<%=feedbackUserType %>','<%=accessFlag %>')"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Comment</a>
																	 </div>
                                                                   <div id="starPrimaryGKT<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; margin: -5px 0px 0px 150px;"></div>
                                                                   <% if(commentCnt > 0 || uF.parseToInt(strUserTaskCnt) > 0) { %>
                                                                  
                                                                   <div>
                                                                       <a href="javascript:void(0);" onclick="viewManagerAndHRComments('<%=strEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA')">Comments
                                                                       <%=(commentCnt + uF.parseToInt(strUserTaskCnt)) %></a>
                                                                   </div>
                                                                   <% } %>
                                                               </div>
                                                               <% 
                                                                   String feedbackUserType1 = "";
                                                                   if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                                                   	feedbackUserType1 = strBaseUserType;
                                                                   }
                                                                   /* if((uF.parseToInt(hmHrIds.get(strEmpId)) == uF.parseToInt(strSessionEmpId) && strUserType != null && strUserType.equals(IConstants.HRMANAGER)) || (strUserType != null && !strUserType.equals(IConstants.HRMANAGER) && ((managerAccessFlag && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) || (strBaseUserType != null && !strBaseUserType.equals(IConstants.CEO) && !strBaseUserType.equals(IConstants.HOD)) ) ) ) { */
                                                                   	if((uF.parseToInt(hmHrIds.get(strEmpId)) == uF.parseToInt(strSessionEmpId) && strUserType != null && strUserType.equals(IConstants.HRMANAGER)) || (strUserType != null && !strUserType.equals(IConstants.HRMANAGER)) ) { %>
                                                                   <% if(dataType == null || dataType.equals("L")) {%>	
		                                                               <div id="TaskRatingDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="display:none; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
		                                                                   <script type="text/javascript">
		                                                                       $(function() {
		                                                                       	$('#starGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
		                                                                       		readOnly: false,
		                                                                       		start: 0,
		                                                                       		half: true,
		                                                                       		targetType: 'number',
		                                                                       		click: function(score, evt) {
		                                                                       				$('#hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').val(score);
		                                                                       }
		                                                                       });
		                                                                       });
		                                                                   </script>
		                                                                   <input type="hidden"
		                                                                       name="hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
		                                                                       id="hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>">
		                                                                   <div
		                                                                       id="starGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
		                                                                       style="float: left; margin: -4px 0px 0px 25px; width: 110px;"></div>
		                                                                   <br />
		                                                                   <div style="float: left; margin: 0px 0px 5px 0px;">
		                                                                       <textarea rows="1" cols="40" name="strComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" id="strComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></textarea>
		                                                                   </div>
		                                                                   <div style="float: left; margin: 0px 0px 5px 7px;">
		                                                                       <a href="javascript:void(0);" onclick="updateKRATaskRatingAndComment('<%=strEmpId %>','<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>','<%=taskInnerList.get(0) %>', 'KRA','<%=feedbackUserType %>','<%=dataType%>','<%=currUserType%>')">
		                                                                           <input type="button" class="btn btn-primary" name="update" value="Update">
		                                                                       </a>
		                                                                   </div>
		                                                               </div>
		                                                           <% } %>    
                                                               <% } %>
                                                           </div>
                                                           <% } %>
                                                       </div>
                                                       <% } %>
                                                       </div>
                                                   </li>
                                               </ul>
                                            <!--  </div>
                                                </div> -->
                                               
                                               <% } %> 
                                               </div>
                                               <% } else if(gInnerList.get(20) != null && gInnerList.get(20).equals("Measure")) {
                                                   String target = hmTargetValue.get(strEmpId+"_"+goalid+"_"+goalFreqId);
                                                   String targetID = hmTargetID.get(strEmpId+"_"+goalid+"_"+goalFreqId);
                                                   String targetRemark = hmTargetRemark.get(strEmpId+"_"+goalid+"_"+goalFreqId);
                                                   String assignedTarget = "", measureType="";
                                                   
                                                   String managerRating = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
                                                   String hrRating = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
                                                   String managerComment = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
                                                   String hrComment = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
                                                   
                                                   String hrMngrReview = "block";
                                                   if(uF.parseToInt(gInnerList.get(23)) == 1) {
                                                   	hrMngrReview = "block";
                                                   }
                                                   
                                                   double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
                                                   if(managerRating == null) {
                                                   	avgRating = uF.parseToDouble(hrRating);
                                                   } else if(hrRating == null) {
                                                   	avgRating = uF.parseToDouble(managerRating);
                                                   }
                                                   
                                                   String strUserCnt = "";
                                                   if(hmEmpwiseGoalAndTargetEmpRating != null && !hmEmpwiseGoalAndTargetEmpRating.isEmpty()) {
                                                   	String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
                                                   	strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                                   	double avgUserRating = 0;
                                                   	if(uF.parseToInt(strUserCnt)>0) {
                                                   		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                                                   	}
                                                   	if(avgRating > 0 && avgUserRating > 0) {
                                                   		avgRating = (avgRating + avgUserRating) / 2;
                                                   	} else if(avgUserRating > 0) {
                                                   		avgRating = avgUserRating;
                                                   	}
                                                   }
                                                   
                                                   int commentCnt = 0;
                                                   if(managerComment != null && !managerComment.equals("null")) {
                                                   	commentCnt++;
                                                   }
                                                   if(hrComment != null && !hrComment.equals("null")) {
                                                   	commentCnt++;
                                                   }
                                                   %>
                                               <ul style="float: left; margin: 0px 10px; width: 98%;">
                                                   <li style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px;">
                                                       <div  style="float: left; width: 100%; margin: 0px 0px 0px 15px">
                                                           <%  
                                                               String twoDeciTotProgressAvg = "0";
                                                               String twoDeciTot = "0";
                                                               String total="100";
                                                               double totalTarget=0;
                                                               if(gInnerList.get(19)!=null && !gInnerList.get(19).equals("Effort")) {
                                                               	if(uF.parseToDouble(gInnerList.get(21)) == 0) {
                                                               		totalTarget=100;
                                                               	} else {
                                                               		totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(gInnerList.get(21)))*100;
                                                               	}
                                                               	twoDeciTot=""+Math.round(totalTarget);
                                                               	if(uF.parseToInt(twoDeciTot+"") >= 100) {
                                                               		hrMngrReview = "block";
                                                               	}
                                                               } else {
                                                               	
                                                               	String t=""+uF.parseToDouble(target);
                                                               	String days="0";
                                                               	String hours="0";
                                                               	if(t.contains(".")){
                                                               		t=t.replace(".","_");
                                                               		String[] temp=t.split("_");
                                                               		days=temp[0];
                                                               		hours=temp[1];
                                                               	}	
                                                               	String t1=""+uF.parseToDouble(gInnerList.get(22));
                                                               	String targetDays = "0";
                                                               	String targetHrs = "0";
                                                               	if(t1.contains(".")){
                                                               		t1=t1.replace(".","_");
                                                               		String[] temp=t1.split("_");
                                                               		targetDays=temp[0];
                                                               		targetHrs=temp[1];
                                                               	}
                                                               	int daysInHrs = uF.parseToInt(days) * 8;
                                                               	int inttotHrs = daysInHrs + uF.parseToInt(hours);
                                                               	
                                                               	int targetDaysInHrs = uF.parseToInt(targetDays) * 8;
                                                               	int inttotTargetHrs = targetDaysInHrs + uF.parseToInt(targetHrs);
                                                               	if(inttotTargetHrs != 0) {
                                                               		totalTarget= uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
                                                               	} else {
                                                               		totalTarget = 100;
                                                               	}
                                                               	twoDeciTot = ""+Math.round(totalTarget);
                                                               	
                                                               	if(uF.parseToInt(twoDeciTot+"") >= 100) {
                                                               		hrMngrReview = "block";
                                                               	}
                                                               	
                                                               	//System.out.println("goalid==>"+goalid+"==>totalTarget==>"+totalTarget+"==>twoDeciTot==>"+twoDeciTot+"==>hrMngrReview==>"+hrMngrReview);
                                                               	/* totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(innerList.get(8)))*100;
                                                               	twoDeciTot=uF.formatIntoTwoDecimal(totalTarget); */
                                                               }
                                                               if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
                                                               	double totalTarget1=(totalTarget/150)*100;
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                                                               	total="150";
                                                               }else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
                                                               	double totalTarget1=(totalTarget/200)*100;
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                                                               	total="200";
                                                               }else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
                                                               	double totalTarget1=(totalTarget/250)*100;
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                                                               	total="250";
                                                               }else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
                                                               	double totalTarget1=(totalTarget/300)*100;
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                                                               	total="300";
                                                               }else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
                                                               	double totalTarget1=(totalTarget/350)*100;
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                                                               	total="350";
                                                               }else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
                                                               	double totalTarget1=(totalTarget/400)*100;
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                                                               	total="400";
                                                               }else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
                                                               	double totalTarget1=(totalTarget/450)*100;
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                                                               	total="450";
                                                               }else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
                                                               	double totalTarget1=(totalTarget/500)*100;
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                                                               	total="500";
                                                               }else{
                                                               	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
                                                               	if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
                                                               		twoDeciTotProgressAvg = "100";
                                                               		total=""+Math.round(totalTarget);
                                                               	}else{
                                                               		total="100";
                                                               	}
                                                               }
                                                               
                                                               %>
                                                           <div id="<%=i %>ProBarDiv<%=goalCount %>"
                                                               style="float: left; min-height: 40px !important; width: 10%; margin: 0px 30px 0px 17px;">
                                                               <div class="anaAttrib1">
                                                                   <span
                                                                       style="margin-left:<%=uF.parseToDouble(twoDeciTotProgressAvg) > 97 ? uF.parseToDouble(twoDeciTotProgressAvg)-6 : uF.parseToDouble(twoDeciTotProgressAvg)-3 %>%;"><%=twoDeciTot%>%</span>
                                                               </div>
                                                               <div id="outbox">
                                                                   <%if(uF.parseToDouble(twoDeciTotProgressAvg) < 33.33) { %>
                                                                   <div id="redbox"
                                                                       style="width: <%=twoDeciTotProgressAvg %>%;"></div>
                                                                   <%} else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(twoDeciTotProgressAvg) < 66.67) { %>
                                                                   <div id="yellowbox"
                                                                       style="width: <%=twoDeciTotProgressAvg%>%;"></div>
                                                                   <%} else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 66.67) { %>
                                                                   <div id="greenbox"
                                                                       style="width: <%=twoDeciTotProgressAvg%>%;"></div>
                                                                   <% } %>
                                                               </div>
                                                               <div class="anaAttrib1"
                                                                   style="float: left; width: 100%;">
                                                                   <span style="float: left; margin-left: -3%;">0%</span>
                                                                   <span style="float: right; margin-right: -6%;"><%=total %>%</span>
                                                               </div>
                                                           </div>
                                                           <div style="float: left; width: 30%">
                                                               <table width="100%">
                                                                   <tr>
                                                                       <td width="40%"><strong><u>Target</u>
                                                                           </strong>
                                                                       </td>
                                                                       <td><strong><u>Actual</u>
                                                                           </strong>
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td nowrap="nowrap">
                                                                           <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
                                                                               measureType= "effort";
                                                                               assignedTarget = gInnerList.get(21);
                                                                               %> <%=gInnerList.get(21) %>
                                                                           <% } else {
                                                                               measureType= "amount";
                                                                               assignedTarget = gInnerList.get(21);
                                                                               %> <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
                                                                           <%=uF.showData(strCurrency,"")%>&nbsp; <% } %> <%=""+uF.parseToDouble(gInnerList.get(21))%>
                                                                           <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
                                                                           &nbsp;% <% } %> <% } %>
                                                                       </td>
                                                                       <%if(flag || (strUserType != null && (strUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) {
                                                                    	   if(dataType == null || dataType.equals("L")) {
                                                                       %>
		                                                                       <td>
		                                                                           <div id="<%=i%>spanid<%=goalCount %>" style="float: left; text-align: center;">
		                                                                               <% if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
		                                                                                   String t=""+uF.parseToDouble(target);
		                                                                                   String days="0";
		                                                                                   String hours="0";
		                                                                                   if(t.contains(".")) {
		                                                                                   	t=t.replace(".","_");
		                                                                                   	String[] temp=t.split("_");
		                                                                                   	days=temp[0];
		                                                                                   	hours=temp[1];
		                                                                                   }
		                                                                                   if(strSessionEmpId!=null && strEmpId.equals(strSessionEmpId)) {
		                                                                                   %>
		                                                                               <%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
		                                                                               <% } else { %>
		                                                                               &nbsp;<input type="text" name="mDays" id="<%=i%>mDays<%=goalCount %>" style="width: 20px !important; text-align: right;" value="<%=days %>"
		                                                                                   onkeypress="return isOnlyNumberKey(event)" <% if(dataType != null && dataType.equals("C")) { %> readonly="readonly" <% } %>/>&nbsp;Days&nbsp;
		                                                                               		<input type="text" name="mHrs" style="width: 20px !important; text-align: right;" id="<%=i%>msHrs<%=goalCount %>" value="<%=hours %>" onkeyup="checkHrsLimit('<%=i %>','<%=goalCount %>','<%=hmEmpWorkingHrs.get(strEmpId) %>')"
		                                                                                   onkeypress="return isOnlyNumberKey(event)" <% if(dataType != null && dataType.equals("C")) { %> readonly="readonly" <% } %>/>&nbsp;Hrs
		                                                                               <% }
		                                                                                   } else { 
		                                                                                   	if(strSessionEmpId!=null && strEmpId.equals(strSessionEmpId)) {
		                                                                                   %>
		                                                                               <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
		                                                                               <%=uF.showData(strCurrency,"")%>&nbsp;
		                                                                               <% } %>
		                                                                               <%=target!=null ? uF.getAmountInCrAndLksFormat(uF.parseToDouble(target)) : "0" %>
		                                                                               <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
		                                                                               &nbsp;%
		                                                                               <% } %>
		                                                                               <% } else { %>
		                                                                               <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
		                                                                               <%=uF.showData(strCurrency,"")%>&nbsp;
		                                                                               <% } %>
		                                                                               <input style="width: 65px !important; text-align: right;" type="text" name="emptarget" id="<%=i%>emptarget<%=goalCount%>"
		                                                                                   value="<%=target!=null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(target)) : "0" %>"
		                                                                                   onkeypress="return isNumberKey(event)" <% if(dataType != null && dataType.equals("C")) { %> readonly="readonly" <% } %>/>
		                                                                               <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
		                                                                               &nbsp;%
		                                                                               <% } %>
		                                                                               <% } 
		                                                                                   } %>
		                                                                               <%if(strSessionEmpId!=null && !strEmpId.equals(strSessionEmpId)) {%>
		                                                                               <% if(dataType == null || dataType.equals("L")) { %>
		                                                                               		<a href="javascript:void(0);"  title="Update" onclick="updateTarget('<%=i%>','<%=goalCount %>','<%=gInnerList.get(19) %>','<%=gInnerList.get(21) %>','<%=gInnerList.get(22) %>', '<%=strEmpId%>', '<%=goalid%>', '<%=goalFreqId %>','<%=accessFlag%>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
		                                                                               <% } %>
		                                                                               <%
		                                                                                   String disp = "none";
		                                                                                   if(targetRemark == null || targetRemark.equals("")){ 
		                                                                                   	disp = "block";
		                                                                                   }
		                                                                                   %>
			                                                                               <% if(dataType == null || dataType.equals("L")) { %>
			                                                                               <span id="<%=i%>remarkSpan<%=goalCount %>" style="display: <%=disp %>; float: right; margin: 2px;">
			                                                                               		<a href="javascript:void(0);"  title="Add Remark" onclick="getMemberData('<%=strEmpId%>', '<%=goalid%>', '<%=goalFreqId %>', 'remark','<%=assignedTarget %>','<%=measureType %>');"><i class="fa fa-commenting" aria-hidden="true"></i></a> <%-- updatePersonalTagetRemark('<%=targetID %>'); --%>
			                                                                               </span>
			                                                                               <%} %>
		                                                                               <%} %>
		                                                                               <br />
		                                                                               <span id="<%=i%>targetStatusDiv<%=goalCount %>"></span>
		                                                                           </div>
		                                                                       </td>
		                                                                       <%} %>
                                                                       <% } else { %>
                                                                       <td>
                                                                           <div id="<%=i%>spanid<%=goalCount %>"
                                                                               style="float: left; text-align: center;">
                                                                               <% if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
                                                                                   String t=""+uF.parseToDouble(target);
                                                                                   String days="0";
                                                                                   String hours="0";
                                                                                   if(t.contains(".")) {
                                                                                   	t=t.replace(".","_");
                                                                                   	String[] temp=t.split("_");
                                                                                   	days=temp[0];
                                                                                   	hours=temp[1];
                                                                                   }
                                                                                   if(strSessionEmpId!=null && strEmpId.equals(strSessionEmpId)) {
                                                                                   %>
                                                                               <%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
                                                                               <% } else { %>
                                                                               &nbsp;<label style="width: 20px !important;; text-align: right;"><%=days %></label>&nbsp;Days&nbsp;
                                                                               <label><%=hours %></label>&nbsp;Hrs
                                                                               <% }
                                                                                   } else { 
                                                                                   	if(strSessionEmpId!=null && strEmpId.equals(strSessionEmpId)) {
                                                                                   %>
                                                                               <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
                                                                               <%=uF.showData(strCurrency,"")%>&nbsp;
                                                                               <% } %>
                                                                               <%=target!=null ? uF.getAmountInCrAndLksFormat(uF.parseToDouble(target)) : "0" %>
                                                                               <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
                                                                               &nbsp;%
                                                                               <% } %>
                                                                               <% } else { %>
                                                                               <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
                                                                               <%=uF.showData(strCurrency,"")%>&nbsp;
                                                                               <% } %>
                                                                               <label style="width: 65px !important; text-align: right;"><%=target!=null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(target)) : "0" %></label>
                                                                               <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
                                                                               &nbsp;%
                                                                               <% } %>
                                                                               <% } 
                                                                                   } %>
                                                                               <br />
                                                                               <span id="<%=i%>targetStatusDiv<%=goalCount %>"></span>
                                                                           </div>
                                                                       </td>
                                                                       <% } %>
                                                                   </tr>
                                                                   <%if(hmUpdateBy.get(strEmpId+"_"+goalid+"_"+goalFreqId) != null) { %>
                                                                   <tr>
                                                                       <td colspan="2"><a href="javascript:void(0)" onclick="getMemberData('<%=strEmpId%>', '<%=goalid%>', '<%=goalFreqId %>', 'status','<%=assignedTarget %>','<%=measureType %>');"
                                                                           style="font-size: 10px;">Last updated by <%=hmUpdateBy.get(strEmpId+"_"+goalid+"_"+goalFreqId)!=null ? hmUpdateBy.get(strEmpId+"_"+goalid+"_"+goalFreqId) : "" %></a>
                                                                       </td>
                                                                   </tr>
                                                                   <%}else{ %>
                                                                   <tr>
                                                                       <td colspan="2"><a>Not updated yet.</a></td>
                                                                   </tr>
                                                                   <%} %>
                                                               </table>
                                                           </div>
                                                           <div style="float: left; margin: 7px 0px 0px 16px;">
                                                               <script type="text/javascript">
                                                                   $(function() {
                                                                   	$('#starPrimaryGTarget<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>').raty({
                                                                   		readOnly: true,
                                                                   		start: <%=avgRating %>,
                                                                   		half: true,
                                                                   		targetType: 'number'
                                                                   });
                                                                   });
                                                               </script>
                                                               <div id="starPrimaryGTarget<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>"></div>
                                                               <% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
                                                               <div>
                                                                   <a href="javascript:void(0);"
                                                                       onclick="viewManagerAndHRComments('<%=strEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'TARGET')">Comments
                                                                   <%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
                                                               </div>
                                                               <% } %>
                                                           </div>
                                                           <% 
                                                               String feedbackUserType = "";
                                                               if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                                               	feedbackUserType = strBaseUserType;
                                                               }
                                                               if((uF.parseToInt(hmHrIds.get(strEmpId)) == uF.parseToInt(strSessionEmpId) && (strUserType != null && strUserType.equals(IConstants.HRMANAGER)) || (strUserType != null && !strUserType.equals(IConstants.HRMANAGER) && ((managerAccessFlag && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) || (strBaseUserType != null && !strBaseUserType.equals(IConstants.CEO) && !strBaseUserType.equals(IConstants.HOD)) ) ) ) )  { %>
                                                           		<% if(dataType == null || dataType.equals("L")) { %>
		                                                           <div id="TargetRatingDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
		                                                               <script type="text/javascript">
		                                                                   $(function() {
		                                                                   	$('#starGTargetRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>').raty({
		                                                                   		readOnly: false,
		                                                                   		start: 0,
		                                                                   		half: true,
		                                                                   		targetType: 'number',
		                                                                   		click: function(score, evt) {
		                                                                   			$('#hideGTargetRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>').val(score);
		                                                                   }
		                                                                   });
		                                                                   });
		                                                               </script>
		                                                               <input type="hidden" name="hideGTargetRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" id="hideGTargetRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>">
		                                                               <div id="starGTargetRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
		                                                               <br />
		                                                               <div style="float: left; margin: 0px 0px 5px 0px;">
		                                                                   <textarea rows="1" cols="40" name="strTargetComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" id="strTargetComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>"></textarea>
		                                                               </div>
		                                                               <div style="float: left; margin: 0px 0px 5px 7px;">
		                                                                   <a href="javascript:void(0);" onclick="updateKRATaskRatingAndComment('<%=strEmpId %>','<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'TARGET','<%=feedbackUserType %>','<%=dataType%>','<%=currUserType%>')">
		                                                                   	<input type="button" class="btn btn-primary" name="update" value="Update">
		                                                                   </a>
		                                                               </div>
		                                                           </div>
		                                                       <% } %>    
                                                           <% } %>
                                                       </div>
                                                       <div style="float: left; width: 100%;">
                                                           <% if(uF.parseToBoolean(hmCheckGTWithAllowance.get(gInnerList.get(1)))) { %>
                                                           <span style="font-style: italic;"><a href="javascript:void(0);" style="font-size: 10px; font-weight: normal;" onclick="viewPerformanceAllowanceData('<%=gInnerList.get(1) %>', 'GT');">This Target has Performance Incentives attached</a></span>
                                                           <% } else { %>
                                                           <span style="font-size: 10px; font-style: italic; color: gray;">This Target has no Performance Incentives attached</span>
                                                           <% } %>
                                                       </div>
                                                   </li>
                                               </ul>
                                               <% } else { %> <%
                                                   //System.out.println("goalid==>"+goalid);
                                                   String goalStatusPercent = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_STATUS");
                                                   String managerRating = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
                                                   String hrRating = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
                                                   String managerComment = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
                                                   String hrComment = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
                                                   
                                                   String hrMngrReview = "block";
                                                   if(uF.parseToDouble(goalStatusPercent) >=100 || uF.parseToInt(gInnerList.get(23)) == 1) {
                                                   	hrMngrReview = "block";
                                                   }
                                                   
                                                   double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
                                                   if(managerRating == null) {
                                                   	avgRating = uF.parseToDouble(hrRating);
                                                   } else if(hrRating == null) {
                                                   	avgRating = uF.parseToDouble(managerRating);
                                                   }
                                                   
                                                   String strUserCnt = "";
                                                   if(hmEmpwiseGoalAndTargetEmpRating != null && !hmEmpwiseGoalAndTargetEmpRating.isEmpty()) {
                                                   	String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
                                                   	strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                                   	double avgUserRating = 0;
                                                   	if(uF.parseToInt(strUserCnt)>0) {
                                                   		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                                                   	}
                                                   	if(avgRating > 0 && avgUserRating > 0) {
                                                   		avgRating = (avgRating + avgUserRating) / 2;
                                                   	} else if(avgUserRating > 0) {
                                                   		avgRating = avgUserRating;
                                                   	}
                                                   }
                                                   
                                                   int commentCnt = 0;
                                                   if(managerComment != null && !managerComment.equals("null")) {
                                                   	commentCnt++;
                                                   }
                                                   if(hrComment != null && !hrComment.equals("null")) {
                                                   	commentCnt++;
                                                   }
                                                   
                                                   %>
                                               <ul style="float: left; margin: 0px 10px; width: 98%;">
                                                   <li style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px;">
                                                       <div style="float: left; width: 100%;">
                                                           <div style="float: left; margin: 20px 0px 3px 0px; line-height: 15px; width: 21.5%;">
                                                               No Measure or KRA. <br />
                                                               <br />
                                                               <% if(uF.parseToBoolean(hmCheckGTWithAllowance.get(gInnerList.get(1)))) { %>
                                                               <span style="font-style: italic;"><a href="javascript:void(0);" style="font-size: 10px; font-weight: normal;" onclick="viewPerformanceAllowanceData('<%=gInnerList.get(1) %>', 'GT');">This Goal has Performance Incentives attached</a> </span>
                                                               <% } else { %>
                                                               <span style="font-size: 10px; font-style: italic; color: gray;">This Goal has no Performance Incentives attached</span>
                                                               <% } %>
                                                           </div>
                                                           <script type="text/javascript">
                                                               $(function() {
                                                               	$('#starPrimaryGoal<%=strEmpId%>_<%=goalid %>_<%=goalFreqId %>').raty({
                                                               		readOnly: true,
                                                               		start: <%=avgRating %>,
                                                               		half: true,
                                                               		targetType: 'number'
                                                               });
                                                               });
                                                           </script>
                                                           <div id="GoalProBarDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" style="float: left; margin: 3px 25px 3px 25px; width: 10%;">
                                                               <div class="anaAttrib1">
                                                                   <span style="margin-left:<%=uF.parseToDouble(goalStatusPercent) > 95 ? uF.parseToDouble(goalStatusPercent) - 10 : uF.parseToDouble(goalStatusPercent) - 4%>%;"><%=uF.showData(goalStatusPercent, "0")%>%</span>
                                                               </div>
                                                               <div id="outbox">
                                                                   <% if (uF.parseToDouble(goalStatusPercent) < 33.33) { %>
                                                                   <div id="redbox" style="width: <%=uF.showData(goalStatusPercent, "0") %>%;"></div>
                                                                   <% } else if (uF.parseToDouble(goalStatusPercent) >= 33.33 && uF.parseToDouble(goalStatusPercent) < 66.67) { %>
                                                                   <div id="yellowbox" style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
                                                                   <% } else if (uF.parseToDouble(goalStatusPercent) >= 66.67) { %>
                                                                   <div id="greenbox" style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
                                                                   <% } %>
                                                               </div>
                                                               <div class="anaAttrib1"
                                                                   style="float: left; width: 100%;">
                                                                   <span style="float: left; margin-left: -4%;">0%</span>
                                                                   <span style="float: right; margin-right: -10%;">100%</span>
                                                               </div>
                                                           </div>
                                                           <% if(flag || (strUserType != null && (strUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId)  || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>
	                                                           <% if(dataType == null || dataType.equals("L")) { %>
		                                                           <div style="float: left; margin: 20px 5px 3px 15px; width: 7%;">
		                                                               <input type="text" name="completedPercent_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" id="completedPercent_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" value="<%=uF.showData(goalStatusPercent, "") %>" style="width: 40px !important;" onkeypress="return isNumberKey(event)" />
		                                                                   <a onclick="updateCompletedPercent('<%=strEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '0','0', 'GOAL','<%=accessFlag %>');" href="javascript:void(0);" title="Update"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>
		                                                               </a> <br />
		                                                               <span id="completedPercentStatusSpan_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>"></span>
		                                                           </div>
                                                           		<% } %>
                                                           <% } else { %>
                                                           <div style="float: left; margin: 20px 5px 3px 15px; width: 7%;">
                                                               <label style="width: 40px !important;"><%=uF.showData(goalStatusPercent, "") %></label>
                                                               <br />
                                                               <span id="completedPercentStatusSpan_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>"></span>
                                                           </div>
                                                           <% } %>
                                                           <div id="GivenGoalRatingDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" style="float: left;">
                                                               <div id="starPrimaryGoal<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" style="float: left; margin: 20px 0px 0px 25px;"></div>
                                                               <% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
                                                               <div style="margin-left: 25px;">
                                                                   <a href="javascript:void(0);" onclick="viewManagerAndHRComments('<%=strEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'GOAL')">Comments <%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
                                                               </div>
                                                               <% } %>
                                                           </div>
                                                           <% 
                                                               String feedbackUserType = "";
                                                               if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                                               	feedbackUserType = strBaseUserType;
                                                               }
                                                               if((uF.parseToInt(hmHrIds.get(strEmpId)) == uF.parseToInt(strSessionEmpId) && !uF.parseToBoolean(gInnerList.get(12)) && strUserType != null && strUserType.equals(IConstants.HRMANAGER)) || (strUserType != null && !strUserType.equals(IConstants.HRMANAGER) && ((managerAccessFlag && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) || (strBaseUserType != null && !strBaseUserType.equals(IConstants.CEO) && !strBaseUserType.equals(IConstants.HOD)) ) ) ) { %>
                                                           		<% if(dataType == null || dataType.equals("L")) { %>
                                                           		<div id="GoalRatingDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
	                                                               <script type="text/javascript">
	                                                                   $(function() {
	                                                                   	$('#starGoalRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>').raty({
	                                                                   		readOnly: false,
	                                                                   		start: 0,
	                                                                   		half: true,
	                                                                   		targetType: 'number',
	                                                                   		click: function(score, evt) {
	                                                                   			$('#hideGoalRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>').val(score);
	                                                                   }
	                                                                   });
	                                                                   });
	                                                               </script>
	                                                               <input type="hidden" name="hideGoalRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" id="hideGoalRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>">
	                                                               <div id="starGoalRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
	                                                               <br />
	                                                               <div style="float: left; margin: 0px 0px 5px 0px;">
	                                                                   <textarea rows="1" cols="40" name="strGoalComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>" id="strGoalComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>"></textarea>
	                                                               </div>
	                                                               <div style="float: left; margin: 0px 0px 5px 7px;">
	                                                                   <a href="javascript:void(0);" onclick="updateKRATaskRatingAndComment('<%=strEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'GOAL','<%=feedbackUserType %>','<%=dataType%>','<%=currUserType%>')">
	                                                                       <input type="button" class="btn btn-primary" name="update" value="Update">
	                                                                   </a>
	                                                               </div>
	                                                           </div>
                                                           		<% } %>
                                                           <% } %>
                                                       </div>
                                                   </li>
                                               </ul>
                                               <% }  %>
                                              </div> 
                                           </li>
                                           <% }  %>
                                           </div>
                                              <% } %>
                                       </ul>
                                       <%-- <%=strHr %> --%>
                                   </li>
                                   <%} %>
                                   <%} %>
                               </ul>
                               </div>
                              <%if((hmGoalKraSuperIdwise == null || hmGoalKraSuperIdwise.isEmpty()) && (empKraOuterList == null || empKraOuterList.isEmpty())) { %>
                               <div class="nodata" style="border-radius: 4px 4px 4px 4px; padding: 10px; width: 97%; margin: 5px 5px 5px 5px">
                                   No Initiatives assigned to
                                   <%=empname %>.
                               </div>
                               <% } 
                                   } %>
                       </div>
                       <!-- /.box-body -->
                   </div>
               </div>
	         
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body"
                style="height: 400px; overflow-y: auto; padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default"
                    data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal" id="profileInfo" role="dialog">
    <div class="modal-dialog proDialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title1">-</h4>
            </div>
            <div class="modal-body1" id="proBody" style="height: 400px; overflow-y: auto; padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default"
                    data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>