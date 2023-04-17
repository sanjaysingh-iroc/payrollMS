<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%  UtilityFunctions uF = new UtilityFunctions();
	String fromPage = (String) request.getAttribute("fromPage"); 
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
/* ===start parvez date: 27-10-2022=== */	
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
/* ===end parvez date: 27-10-2022=== */	
%>

<script type="text/javascript"
	src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>

<script type="text/javascript">
    function getEmpProfile(val, empName){		
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html(''+empName+'');
    	 $.ajax({
   			url : "AppraisalEmpProfile.action?empId=" + val ,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    }
   
    function myGoal() { 
    
    	var strID = '';
    	if(document.getElementById("hideOrgid")) {
    		strID = document.getElementById("hideOrgid").value; 
        }
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-dialog").width(1100);
    	 $(".modal-title").html('My Personal Goal');
    	 $.ajax({
   			url : "MyGoalPopUp.action?operation=A&type=type&typeas=goal&strID="+strID+"&fromPage=KT",
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    }
     
    function editGoal(goalid) { 
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Edit Personal Goal');
    	 if($(window).width() >= 800){
    		 $(".modal-dialog").width(800);
    	 }
    	 $.ajax({
   			url : "EditMyPersonalGoalPopUp.action?operation=E&type=type&goalid="+goalid+"&typeas=goal&from=KT",
   			cache : false,
   			success : function(data) {
				$(dialogEdit).html(data);
   			}
   		});
    }
     
    function updateTarget(id, val, measuretype, amount, dayHrs, empid, goalid, goalFreqId) {
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
                        	if(data == ""){
                        	}else if(data.length > 1){
                        		var allData = data.split("::::");
                                document.getElementById(id+"targetStatusDiv"+val).innerHTML = allData[1];
                                document.getElementById(id+"ProBarDiv"+val).innerHTML = allData[2];
                                if(allData[1] == "Target Updated"){
                                	document.getElementById("TargetRatingDiv_"+empid+"_"+goalid+"_"+goalFreqId).style.display = "block";
                                	document.getElementById(id+"remarkSpan"+val).style.display = "inline";
                                }
                        	}
                        }
                    });
            	}
    		}  
    	}
    
    
    function updateCompletedPercent(empId, goalid, goalFreqId, kraId, kraTaskId, goalType) {
		var completedPercent = 0;
		if(goalType == 'KRA') {
			completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).value;
		} else {
			completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId).value;
		}
		if(parseFloat(completedPercent) > 100) {
			alert('Entered percentage greater than 100, please enter correct percentage.');
		} else {
			if(confirm('Are you sure, you wish to update task completion percentage?')) {
	        xmlhttp = GetXmlHttpObject();
	        if (xmlhttp == null) {
	                alert("Browser does not support HTTP Request");
	                return;
	        } else {
	                var xhr = $.ajax({
                        url : 'UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalid+'&goalFreqId='+goalFreqId+'&kraId='+kraId
                       		+'&kraTaskId='+kraTaskId+'&completedPercent='+completedPercent+'&type=GoalKRA&goalType='+goalType,
                        cache : false,
                        success : function(data) {
                        	if(data == "") {
                        	} else if(data.length > 1) {
                        		var allData = data.split("::::");
                        		if(goalType == 'KRA') {
	                                document.getElementById("completedPercentStatusSpan_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).innerHTML = allData[1];
	                                document.getElementById("KTProBarDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).innerHTML = allData[2];
	                                if(parseFloat(completedPercent) == 100) {
	                                	document.getElementById("TaskRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).style.display = "block";
	                                } else {
	                                	document.getElementById("TaskRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).style.display = "block";
	                                }
                        		} else {
                        			document.getElementById("completedPercentStatusSpan_"+empId+"_"+goalid+"_"+goalFreqId).innerHTML = allData[1];
	                                document.getElementById("GoalProBarDiv_"+empId+"_"+goalid+"_"+goalFreqId).innerHTML = allData[2];
	                                if(parseFloat(completedPercent) == 100) {
	                                	document.getElementById("GoalRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId).style.display = "block";
	                                } else {
	                                	document.getElementById("GoalRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId).style.display = "block";
	                                }
                        		}
                        	}
                        }
	                });
	        	}
			}
		}
	}
    
    
    function viewManagerAndHRComments(empId, goalId, goalFreqId, kraId, kraTaskId, goalType) { 
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Comments');
    	 $.ajax({
    			url : "ViewManagerAndHRComentsOfGoalKRATarget.action?empId="+empId+"&goalId="+goalId+"&goalFreqId="+goalFreqId
    				+"&kraId="+kraId+"&kraTaskId="+kraTaskId+"&goalType="+goalType,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    }
    
    		
    function GetXmlHttpObject() {
	    if (window.XMLHttpRequest) {
	     
	        return new XMLHttpRequest();
	    }
	    if (window.ActiveXObject) {
	       
	        return new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    return null;
    }
     
    
    function getMemberData(empId, goalid, goalFreqId, type, assignedTarget, measureType) {
    	
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $(".modal-title").html('Give Remark');
    	$("#modalInfo").show();
    	$.ajax({
    		url : "TargetStatus.action?goalid=" + goalid + "&goalFreqId=" + goalFreqId + "&empid=" + empId + "&type=" + type + "&assignedTarget=" 
    		+ assignedTarget + "&measureType=" + measureType,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    } 
   
    function staffReviewPoup(id, empID, userType, currentLevel, role,appFreqId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$(".modal-title").html('Review Form');
    	if($(window).width() >= 800){
			$(".modal-dialog").width(800);
		}
    	$.ajax({
   			url : "StaffAppraisal.action?id=" + id + "&empID=" + empID + "&userType=" + userType + "&currentLevel=" + currentLevel
   				+ "&role=" + role + "&appFreqId=" + appFreqId,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		}); 
    }
    
    
    function staffReviewSummaryPoup(id, empID, userType, currentLevel, role, appFreqId) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Review Summary Form');
    	 if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		 }
    	 $.ajax({
   			url : "EmpAppraisalSummary.action?id=" + id + "&empID=" + empID + "&userType=" + userType + "&currentLevel=" + currentLevel
   					+ "&role=" + role+"&appFreqId="+appFreqId,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    }
    
    
    function staffAppraisalPreview(id, empID, userType, appFreqId) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Review Summary Form');
    	 if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		 }
    	 $.ajax({
   			
   			url : 'StaffAppraisalPreview.action?id='+id+'&empID='+empID+'&userType='+userType+'&appFreqId='+appFreqId,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    }
    
    
    function seeUserTypeList(id, empId, sectionId, memberIds,appFreqId) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Workflow Preview');
    	 $.ajax({
   			url : "UserTypeListPopUp.action?id=" + id
   				+ "&empId=" + empId+ "&sectionId=" + sectionId+ "&memberIds=" + memberIds+"&appFreqId="+appFreqId,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
    }
    
    
    function openAppraisalPreview(id,appFreqId) {
    	 var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		 $("#modalInfo").show();
    		 if($(window).width() >= 950){
    			$(".modal-dialog").width(950);
    		 }
    		 if($(window).height() > 600){
    			 $(".modal-body").height(400);
    		 }
    		 $(".modal-title").html('Review Preview');
    		 $.ajax({
    		url : "AppraisalPreview.action?id="+id+"&appFreqId="+appFreqId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
	}
    	
    function closeMyReview(reviewId, type,appFreqId) {
    	var pageTitle = 'Close My Review';
    	if(type=='view') {
    		pageTitle = 'Close My Review with Reason';
    	}
    	 var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $('.modal-body').height('auto');
    	 $(".modal-title").html(pageTitle);
    	 $.ajax({
    			url : "CloseReview.action?reviewId="+reviewId+"&fromPage=MyReview&appFreqId="+appFreqId,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    	}
    
    
    function closeGoal(goalId, type) {
    	var pageTitle = 'Close Goal';
    	if(type=='view') {
    		pageTitle = 'Close Goal Reason';
    	}
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html(pageTitle);
    	 $.ajax({
    			url : "CloseGoalTargetKRA.action?goalId="+goalId+"&fromPage=MyGoals",
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    	}
    
    
    function openGoalForLive(goalId, type) {
 	   
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Open Goal for Live');
    	$("#modalInfo").show();
    	$.ajax({
    		url : "CloseGoalTargetKRA.action?goalId="+goalId+"&type="+type+"&fromPage=MyGoals",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    
    function goalChart(corpGoalID, managerGoalID, teamGoalID, goalID) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Goal Chart');
    	 $.ajax({
    			url : "GoalChartIndividualGoal.action?corpGoalID=" + corpGoalID + "&managerGoalID=" + managerGoalID +"&teamGoalID=" + teamGoalID 
    			+"&goalID=" + goalID,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    }
    
    function getPublishAppraisal(id, dcount, empId,appFreqId) {
    	xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
    		alert("Browser does not support HTTP Request");
    		return;
        } else {
               var xhr = $.ajax({
                  url : "PublishAppraisal.action?id=" + id + '&dcount=' +dcount + '&empId='+empId+'&appFreqId='+appFreqId,
                  cache : false,
                  success : function(data) {
                  	if(data == "") {
                  		
                  	} else {
                  		var allData = data.split("::::");
                  		document.getElementById("myDivM"+dcount).innerHTML = allData[0];
                  	}
                  }
               });
        }
    }
    function viewAddComments(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,feedbackUserType) 
	{
			
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('New Comment');
		$("#modalInfo").show();
		$(".modal-dialog").width(50);
		$(".modal-dialog").height(50);
		$.ajax({
			url : "GoalKRAComment.action?empId="+empId+"&goalid="+goalid+"&goalFreqId="+goalFreqId
		+"&kraId="+kraId+"&kraTaskId="+kraTaskId+"&goalType="+goalType+"&feedbackUserType="+feedbackUserType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
		
		
	}

    
    
    function updateKRATaskRatingAndComment(empId, goalid, goalFreqId, kraId, kraTaskId, goalType, feedbackUserType) {
		// 
		var taskRating = 0;
		var strComment = '';
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
                        url : 'UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalid+'&goalFreqId='+goalFreqId+'&kraId='+kraId+'&kraTaskId='+kraTaskId+'&strComment='
                        		+encodeURIComponent(strComment)+'&taskRating='+taskRating+'&type=GoalKRA&operation=RC&goalType='+goalType
                        		+"&feedbackUserType="+feedbackUserType,
                        cache : false,
                        success : function(data) {
                        	if(data == "") {
                        	} else if(data.length > 1) {
                        		var allData = data.split("::::");
                        		window.location = "MyHR.action";
                                
                        	}
                        }
	                });
	        	}
			}
	}
    
    function getApprovalStatus(reviewId, empname,appFreqId) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Work flow of '+empname);
    	 $.ajax({
    			url : "GetLeaveApprovalStatus.action?effectiveid="+reviewId+"&type=12&appFreqId="+appFreqId,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
     
    }
    
    
    function GetXmlHttpObject() {
        if (window.XMLHttpRequest) {
                return new XMLHttpRequest();
        }
        if (window.ActiveXObject) {
                return new ActiveXObject("Microsoft.XMLHTTP");
        }
        return null;
    }
    
    function checkHrsLimit(cnt,goalCnt,strEmpId) {
		
		var empSelected = strEmpId+",";
		var days = document.getElementById(cnt+"mDays"+goalCnt).value;
		var hrs = document.getElementById(cnt+"msHrs"+goalCnt).value;
		
		if(parseInt(days) == 0 && parseInt(hrs) == 0) {
				alert("Invalid data!");
				document.getElementById(cnt+"mDays"+goalCnt).value = '';
				document.getElementById(cnt+"msHrs"+goalCnt).value = '';
			
		} else {
							
				var xmlhttp;
				if (window.XMLHttpRequest) {
		           
		            xmlhttp = new XMLHttpRequest();
		    	}
			    if (window.ActiveXObject) {
			      
			    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			    }
			    if (xmlhttp == null) {
			            alert("Browser does not support HTTP Request");
			            return;
			    } else {
			    	var xhr = $.ajax({
		                url : 'GetEmpMaxWorkingHrs.action?empselected='+empSelected+'&hrs='+ hrs, 
		                		
		                cache : false,
		                success : function(data) {
		                	if(data.trim() == '1') {
		                		document.getElementById(cnt+"msHrs"+goalCnt).value = '';
		                	}
		                }
		            });
				}
		   
			}
		}  
    
    function  getKRATargetData(strAction,dataType,fromPage){
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
    
    
    function  deleteGoal(id){
    	if(confirm('Are you sure you wish to delete this Goal?')) {
    		$("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	  	 	$.ajax({ 
	  			type : 'POST',
	  			url: 'MyPersonalGoal.action?operation=D&type=type&goal_id='+id,
	  			cache: true,
	  			success: function(result){
	  				$("#divMyHRData").html(result);
	     		},
	     		error: function(result){
	     			getMyHRData('KRATarget','L','','');
	     		}
	  	 	 });
    	}
    }
    
    function addSelfReview(strAction) {
      $("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	  $.ajax({ 
   		type : 'POST',
   		url: strAction,
   		cache: true,
   		success: function(result){
   			
   			$("#divMyHRData").html(result);
      		}
   	  });
    }
    function hideData()
    {
    	
    	if(document.getElementById("nameper").style.display="none")
    	document.getElementById("nameper").style.display="block";
    	if(document.getElementById("nameper").style.display="block")
    	document.getElementById("nameper").style.display="none";
    	var x = document.getElementById("headerName");   
    	x.style.border = "none";   
    	
    	
     }
   
</script>

<%
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String []arrEnabledModules = CF.getArrEnabledModules();
	
    String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    Map<String, String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
  
    Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
    
    Map<String, List<List<String>>> hmEmpKra = (Map<String, List<List<String>>>) request.getAttribute("hmEmpKra");
    
    List<String> alCheckList = (List<String>) request.getAttribute("alCheckList");
    Map<String, String> hmOrientationViewAccess = (Map<String, String>) request.getAttribute("hmOrientationViewAccess");
	if(hmOrientationViewAccess == null) hmOrientationViewAccess = new HashMap<String,String>();
	
	Map<String, String> hmOrientationEditAccess = (Map<String, String>) request.getAttribute("hmOrientationEditAccess");
	if(hmOrientationEditAccess == null) hmOrientationEditAccess = new HashMap<String,String>();
	
	Map<String, String> hmSectionGivenQueCnt = (Map<String, String>) request.getAttribute("hmSectionGivenQueCnt");
	if(hmSectionGivenQueCnt == null) hmSectionGivenQueCnt = new HashMap<String, String>();
	
	Map<String, String> hmSectionQueCnt = (Map<String, String>) request.getAttribute("hmSectionQueCnt");
	if(hmSectionQueCnt == null) hmSectionQueCnt = new HashMap<String, String>();
	
	
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
	if (hmEmpProfile == null) {
		hmEmpProfile = new HashMap<String, String>();
	}
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
    %>
<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-none nav-tabs-custom">
		<ul class="nav nav-tabs">
			<%
	                    String dataType = (String) request.getAttribute("dataType");
	                    if(dataType != null && dataType.equals("L")) { %>
			<li class="active"><a href="javascript:void(0)" onclick="getKRATargetData('KRATarget','L','<%=fromPage%>')" data-toggle="tab">Live</a>
			</li>
			<li><a href="javascript:void(0)" onclick="getKRATargetData('KRATarget','C','<%=fromPage%>')" data-toggle="tab">Closed</a>
			</li>
			<% } else if(dataType != null && dataType.equals("C")) { %>
			<li><a href="javascript:void(0)" onclick="getKRATargetData('KRATarget','L','<%=fromPage%>')" data-toggle="tab">Live</a>
			</li>
			<li class="active"><a href="javascript:void(0)" onclick="getKRATargetData('KRATarget','C','<%=fromPage%>')" data-toggle="tab">Closed</a>
			</li>
			<% } %>
		</ul>
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
			<section class="content">
			<div class="row jscroll">
			
				<section class="col-lg-4 connectedSortable">
	 			<div class="box box-widget widget-user widget-user1">
			            <!-- Add the bg color to the header using any of the bg-* classes -->
			            <div class="widget-user-header bg-aqua-active">
			         <!-- ====start parvez on 27-10-2022===== -->     
			              <%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
			              
			         	<%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
							List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
						%>
							<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>' style="height: auto !important;">
						<% } else{ %>
							<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
						<% } %>	
			         <!-- ====end parvez on 27-10-2022===== -->     
			              <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: 0px;"><span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>  <!--  -->
			              	<span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
			              </h3>
			              <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
			            </div>
			            <div class="widget-user-image">
			            	<%if(docRetriveLocation==null) { %> 
			              	<img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
			              	<%} else { %>
			              	<img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
			              	<%} %>
			            </div>
			            <div class="box-footer">
			              <div class="row">
			                <div class="col-sm-12">
			                  <div class="description-block">
			                    <h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> </h5> <%-- [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>] --%>
			                    <span class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%> </span> <%-- [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>] --%>
			                    <p class="description-text"><%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%> </p>
			                    <%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
			                   	  <span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong> </span>
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
                  
	              <div class="box box-info">
			          <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){ 
		            	List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
		            	Map<String,List<List<String>>> hmElementAttribute=(Map<String,List<List<String>>>)request.getAttribute("hmElementAttribute");
						Map<String, String> hmScoreAggregateMap =(Map<String, String>) request.getAttribute("hmScoreAggregateMap");
		            	double dblScorePrimary = 0, aggregeteMarks = 0, totAllAttribMarks = 0;
		 				int count = 0;
		
		 				for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
		 					List<String> innerList = elementouterList.get(i);
		 					List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
		 					
		 					if(attributeouterList1 != null && !attributeouterList1.isEmpty()) {
			 					for (int j = 0; j < attributeouterList1.size(); j++) {
			 						List<String> attributeList1 = attributeouterList1.get(j);
			 						totAllAttribMarks += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
			 						count++;
			 					}
		 					} else {
		 						count++;
		 					}
		 				}
		 				aggregeteMarks = totAllAttribMarks / count;
		
		 				dblScorePrimary = aggregeteMarks; 
		             %>
	                <div class="box-header with-border">
	                  <h3 class="box-title">My Ratings</h3>
	                  <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	 <div id="profilecontainer">
							<div class="content1">
								<div class="holder" style="max-height: 250px;">
									<div style="float:left;width:100%">
									<div style="float:left;width:200px;">Overall: </div>
										<div id="starAllPrimary" style="float:left; width: 100px;"></div>
							   			<input type="hidden" id="gradeAllwithrating" value="0" />
											<script type="text/javascript">
										        	$('#starAllPrimary').raty({
										        		readOnly: true,
										        		start: <%=dblScorePrimary %>,
										        		half: true,
										        		targetType: 'number',
										        		click: function(score, evt) {
										        			$('#gradeAllwithrating').val(score);
										        		}
										        	});
								        	</script>
										
								</div>
								
								
								<%
								for(int i=0; elementouterList != null && i<elementouterList.size();i++){
									List<String> innerList=elementouterList.get(i);
								%>
									<div style="float:left; padding-left: 10px; width:100%"><strong><%=innerList.get(1)%></strong></div>
									<%List<List<String>> attributeouterList1=hmElementAttribute.get(innerList.get(0).trim());
								       for(int j=0; attributeouterList1 != null && j<attributeouterList1.size();j++) {
										List<String> attributeList1=attributeouterList1.get(j);
								    %>
										<div style="float:left; padding-left: 17px; width:98%;">
										<div style="float:left;width:200px;"><%=attributeList1.get(1) %>: </div>
										
										<div id="starPrimary<%=i %><%=j %>" style="float:left; width: 100px;"></div>
							   			<input type="hidden" id="gradewithrating<%=i %><%=j%>" value="<%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) + "" : "0"%>"; name="gradewithrating<%=i %><%=j%>" />
											<script type="text/javascript">
										        	$('#starPrimary<%=i %><%=j %>').raty({
										        		readOnly: true,
										        		start: <%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) + "" : "0"%>,
										        		half: true,
										        		targetType: 'number',
										        		click: function(score, evt) {
										        			$('#gradewithrating<%=i %><%=j %>').val(score);
										        			}
										        	});
								        	</script>
										</div>
								      <% }
									}
									%>
								
								<%
								if(hmElementAttribute == null || hmElementAttribute.isEmpty()){
								%>
								<div class="nodata msg" style="width: 93%">
									<span>No attribute aligned with this level</span>
								</div>
								<%} %>
								
				                </div>
			                </div>
						</div>
						<% } %>
	                </div><!-- /.box-body -->
	              </div>
	              <div class="box box-default">
			          <% List<List<String>> alSkills = (List<List<String>>)request.getAttribute("alSkills"); %>
	                <div class="box-header with-border">
	                	<h3 class="box-title">My Skills</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-gray"><%=alSkills != null ? alSkills.size() : "0" %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div id="profilecontainer">
							<div class="content1">
								<div class="holder">
							<%
								Map<String, String> hmSkillAvgRating = (Map<String, String>) request.getAttribute("hmSkillAvgRating");
								int i=0;
								for(;alSkills!=null && i<alSkills.size();i++) {
									List<String> alInner = (List<String>)alSkills.get(i);
									
									String skillValue = "0";
									double skillrate = 0.0d;
									String ratingBy = "(Self Rated)";
									if(hmSkillAvgRating != null && uF.parseToDouble(hmSkillAvgRating.get(alInner.get(0))) > 0) {
										skillrate = uF.parseToDouble(hmSkillAvgRating.get(alInner.get(0))) / 20;
										ratingBy = "";
									} else {
										skillrate = uF.parseToDouble(alInner.get(2)) / 2;
									}
								%>
								
								<div style="float:left;width:100%">
									<div style="float:left; width: 200px;"><%=alInner.get(1) %>&nbsp;: </div>
										<div id="starSkills_<%=i %>" style="float:left; margin-left: 5px;"></div>
											<script type="text/javascript">
											        	$('#starSkills_<%=i %>').raty({
											        		readOnly: true,
											        		start: <%=skillrate %>,
											        		half: true, 
											        		targetType: 'number'
											        	});
								        	</script>
										</div>
								<% }
								if(i==0) {
									%>
									<div class="tdDashLabel" style="width: 93%">
									<span>No skills aligned.</span>
									</div>
								<% } %>
								</div>
							</div>
						</div>
	                </div>
	              </div>
	              <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
					%>
	              <div class="box box-success">
			          <%	List<Map<String,String>> myKRAList= (List<Map<String,String>>)request.getAttribute("myKRAList"); 
							List<List<String>> goalIdList = (List<List<String>>)request.getAttribute("goalIdList");
							int intKRACnt = myKRAList != null ? myKRAList.size() : 0;
							int intGoalCnt = goalIdList != null ? goalIdList.size() : 0;
						%>
	                <div class="box-header with-border">
	                	<h3 class="box-title">My Goals</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-green"><%=(intKRACnt + intGoalCnt) %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div id="profilecontainer">
							<div class="content1">
								<div class="holder" style="max-height: 300px;">
							<%
								int cnt = 0;
								i=0;
								for(;myKRAList!=null && i<myKRAList.size();i++) {
									Map<String, String> hmKRA =(Map<String, String>)myKRAList.get(i);
									cnt++;
									if(cnt > 20) {
										break;
									}
								%> 
								
								<div style="float:left;width:100%;padding-top: 2px;padding-bottom: 2px;">
								<div style="float:left; min-width: 200px;"><%=hmKRA.get("KRA_NAME") %>: </div>
										
										<%if(uF.parseToDouble(hmKRA.get("KRA_AVERAGE"))<=0) { %>
										 	<div>Not Rated</div>
										<%} else { %>
										<div id="starKRA_<%=i %>" style="float:left; margin-left: 5px;"></div>
											<script type="text/javascript">
												        	$('#starKRA_<%=i %>').raty({
												        		readOnly: true,
												        		start: <%=hmKRA.get("KRA_AVERAGE") != null ? uF.parseToDouble(hmKRA.get("KRA_AVERAGE")) + "" : "0"%>,
												        		half: true,
												        		targetType: 'number'
												        	});
								        	</script>
										<% } %>
										</div>
								<% } %>
								
								<%
								
								Map<String, String> hmGoalAverage = (Map<String, String>) request.getAttribute("hmGoalAverage");
								i=0;
								for(;goalIdList!=null && i<goalIdList.size();i++) {
									List<String> goalInnerList =goalIdList.get(i);
									cnt++;
									if(cnt > 20) {
										break;
									}
								%>
								
								<div style="float:left;width:100%;padding-top: 2px;padding-bottom: 2px;">
								<div style="float:left; min-width: 200px;"><%=goalInnerList.get(1) %>: </div>
										
										<div id="starMYGOALS_<%=i %>" style="float:left; margin-left: 5px;"></div>
											<script type="text/javascript">
											        	$('#starMYGOALS_<%=i %>').raty({
											        		readOnly: true,
											        		start: <%=hmGoalAverage.get(goalInnerList.get(0)) != null ? uF.parseToDouble(hmGoalAverage.get(goalInnerList.get(0))) + "" : "0"%>,
											        		half: true,
											        		targetType: 'number'
											        	});
								        	</script>
										</div>
								<% } %>
									
								<% if(i==0) { %>
									<div class="tdDashLabel" style="width: 93%">
									<span>No Goals found.</span>
									</div>
								<%} %>
								
								</div>
								
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	 		</section>
	 		
			
				<section class="col-lg-4 connectedSortable">
				<div class="box box-primary"
					style="border-top-color: #cda55f /*#E0E0E0;*/">
					<div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px !important;">My Finalized Reviews</h3>
					</div>
					<!-- /.box-header -->
					<div class="box-body"
						style="padding: 5px; overflow-y: auto; max-height: 250px;">
						<div class="attendance">
							<% i = 0;
				               List<List<String>> allAppraisalreport = (List<List<String>>) request.getAttribute("allAppraisalreport");
							%>
							<% if(allAppraisalreport != null && !allAppraisalreport.isEmpty()) { %>
							<table width="100%" cellspacing="0" cellpadding="2" align="left"
								class="table" style="margin-bottom: 0px;">
								<tbody>
									<tr class="darktable">
										<th
											style="width: 60%; text-align: center; border-top-color: #FFF;">Reviews
											Name</th>
										<th style="text-align: center; border-top-color: #FFF;">Deadline</th>
										<th style="text-align: center; border-top-color: #FFF;">Actions</th>
									</tr>
									<%
                                                        int totalAppraisal = 0;
                                                        for (i = 0; i < allAppraisalreport.size(); i++) {
                                                        	List<String> alinner = (List<String>) allAppraisalreport.get(i);
                                                        	totalAppraisal = allAppraisalreport.size();
                                                        %>
									<tr class="lighttable">
										<td><b style="padding-right: 5px; float: left;"><%=alinner.get(1)%></b>
											<p style="padding-left: 20px"><%=alinner.get(2)%>,
												<%=alinner.get(3)%>,
												<%=alinner.get(4)%></p></td>
										<td align="center"><%=alinner.get(8)%></td>
										<td align="center"><%=alinner.get(6)%></td>
									</tr>
									<% } %>
									<% if (allAppraisalreport.size() == 0) { %>
									<tr class="lighttable">
										<td colspan="3">
											<div class="nodata msg">
												<span> No data available.</span>
											</div></td>
									</tr>
									<% } else { %>
									<% } %>
								</tbody>
							</table>
							<% } else { %>
							<!-- <ul><li> No Data Available.</li></ul> -->
							<div class="nodata msg" style="width: 96%;">No data available.</div>
							<%} %>
						
						</div>
					</div>
					</div>


				<div class="box box-primary"
					style="border-top-color: #cda55f /*#E0E0E0;*/">
					<div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px !important;">Review Forms</h3>
						<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_SELF_REVIEW_LINK))) { %>
						<div class="box-tools pull-right">
							<span style="float: right; margin-top: 10px;">
								<a href="javascript:void(0);" onclick="addSelfReview('CreateMyReview.action')"><i class="fa fa-plus-circle" aria-hidden="true"></i> Get a Self Review </a>
							</span>
						</div>
						<% } %>
					</div>
					<% String message = (String) session.getAttribute("message");
									   if(message != null ) { %>
					<%=message %>
					<%	} 
									   session.setAttribute("message", "");
									%>
					
					<div class="box-body" style="padding: 5px; overflow-y: auto;">
						<div class="details_lables">
							<%
                                       	boolean rateFlag = false;
                                          Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
                                           if (hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
                                           
                                           Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
                                           if (orientationMemberMp == null)  orientationMemberMp = new HashMap<String, String>();
                                           
                                           Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
                                           if(hmScoreAggregateMap == null) hmScoreAggregateMap = new HashMap<String, String>();
                                           
                                           Map<String, Map<String, String>> appraisalDetails = (Map<String, Map<String, String>>) request.getAttribute("appraisalDetails");
                                           if(appraisalDetails == null ) appraisalDetails = new HashMap<String, Map<String, String>>();
                                           
                                           List<String> appraisalIdList = (List<String>) request.getAttribute("appraisalIdList");
                                           if(appraisalIdList == null ) appraisalIdList = new ArrayList<String>(); 
                                           
                                           Map<String, Map<String, List<String>>> empMpDetails = (Map<String, Map<String, List<String>>>) request.getAttribute("empMpDetails");
                                           if(empMpDetails == null) empMpDetails = new HashMap<String, Map<String, List<String>>>();
                                           
                                           Map<String, Map<String, List<String>>> hmReviewedEmpDetails = (Map<String, Map<String, List<String>>>) request.getAttribute("hmReviewedEmpDetails");
                                           if(hmReviewedEmpDetails == null) hmReviewedEmpDetails = new HashMap<String, Map<String, List<String>>>();
                                           
							Map<String, Map<String, Map<String, String>>> appraisalStatusMp = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("appraisalStatusMp");
                                           if(appraisalStatusMp == null) appraisalStatusMp = new HashMap<String, Map<String, Map<String, String>>>();
							
                                           Map<String, List<String>> hmAppraisalNextStep = (Map<String, List<String>>) request.getAttribute("hmAppraisalNextStep");
                                           if(hmAppraisalNextStep == null) hmAppraisalNextStep = new HashMap<String, List<String>>();
                                           
                                           Map<String, List<String>> hmNextStepFillEmpIds = (Map<String, List<String>>) request.getAttribute("hmNextStepFillEmpIds");
                                           if(hmNextStepFillEmpIds == null) hmNextStepFillEmpIds = new HashMap<String, List<String>>();
                                           
							Map<String, List<List<String>>> hmAppraisalSectins = (Map<String, List<List<String>>>) request.getAttribute("hmAppraisalSectins");
							if(hmAppraisalSectins == null) hmAppraisalSectins = new HashMap<String, List<List<String>>>();
							
                                           Map<String, Map<String, List<String>>> hmRemainOrientDetailsAppWise = (Map<String, Map<String, List<String>>>) request.getAttribute("hmRemainOrientDetailsAppWise");
                                           if(hmRemainOrientDetailsAppWise == null ) hmRemainOrientDetailsAppWise = new HashMap<String, Map<String, List<String>>>();
                                           
                                           Map<String, Map<String, List<String>>> hmRemainOrientDetailsForSelfAppWise = (Map<String, Map<String, List<String>>>) request.getAttribute("hmRemainOrientDetailsForSelfAppWise");
                                           if(hmRemainOrientDetailsForSelfAppWise == null ) hmRemainOrientDetailsForSelfAppWise = new HashMap<String, Map<String, List<String>>>();
                                           
                                           Map<String, Map<String, List<String>>> hmRemainOrientDetailsForPeerAppWise = (Map<String, Map<String, List<String>>>) request.getAttribute("hmRemainOrientDetailsForPeerAppWise");
                                           if(hmRemainOrientDetailsForPeerAppWise == null ) hmRemainOrientDetailsForPeerAppWise = new HashMap<String, Map<String, List<String>>>();
                                         
                                           Map<String, List<String>> hmExistUsersAQA = (Map<String, List<String>>) request.getAttribute("hmExistUsersAQA");
                                           if (hmExistUsersAQA == null) hmExistUsersAQA = new HashMap<String, List<String>>();
                                           
                                           Map<String, List<String>> hmOrientTypewiseID = (Map<String, List<String>>) request.getAttribute("hmOrientTypewiseID");
                                           if (hmOrientTypewiseID == null) hmOrientTypewiseID = new HashMap<String, List<String>>();
                                           
                                           Map<String, List<String>> hmExistSectionID = (Map<String, List<String>>) request.getAttribute("hmExistSectionID");
                                           if (hmExistSectionID == null) hmExistSectionID = new HashMap<String, List<String>>();
                                           
                                           Map<String, Map<String, List<String>>> hmExistOrientTypeAQAAppWise = (Map<String, Map<String, List<String>>>) request.getAttribute("hmExistOrientTypeAQAAppWise");
                                           if (hmExistOrientTypeAQAAppWise == null) hmExistOrientTypeAQAAppWise = new HashMap<String, Map<String, List<String>>>();
                                           
                                           Map<String, String> hmSectionwiseWorkflow = (Map<String, String>) request.getAttribute("hmSectionwiseWorkflow");
                                           if (hmSectionwiseWorkflow == null) hmSectionwiseWorkflow = new HashMap<String, String>();
                                           
                                           Map<String, String> hmReviewId = (Map<String, String>) request.getAttribute("hmReviewId");
                                           if (hmReviewId == null) hmReviewId = new HashMap<String, String>();
                                           
                                           String strEmpName = (String) request.getAttribute("strEmpName");
                                           
                                           int appCnt = 0;
                                           if(appraisalIdList != null && !appraisalIdList.isEmpty()) {
                                           %>
							<ul style="margin-bottom: 10px; padding-left: 10px;">
								<%
                                                	for (int j = 0; appraisalIdList != null && !appraisalIdList.isEmpty() && j < appraisalIdList.size(); j++) {
                                                		Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdList.get(j));
                                                		if (userTypeMp == null) userTypeMp = new HashMap<String, Map<String, String>>();
                                                		Map<String, String> hmAppraisalMp = appraisalDetails.get(appraisalIdList.get(j));
                                              			Map<String, List<String>> empMp = empMpDetails.get(appraisalIdList.get(j));
                                                		
                                                		String appId = hmAppraisalMp.get("ID");
                										String appFreqId = hmAppraisalMp.get("APP_FREQ_ID");
                										List<List<String>> listAppSections = hmAppraisalSectins.get(appId);
                                                		List<String> alSelfFillEmpIds = new ArrayList<String>();
                                                		if(hmAppraisalMp != null && hmAppraisalMp.size()>0 && hmAppraisalMp.get("SELFID")!= null && !hmAppraisalMp.get("SELFID").equals("")) {
                                                			alSelfFillEmpIds = Arrays.asList(hmAppraisalMp.get("SELFID").split(","));
                                                		}
                                                		Set<String> keys = empMp.keySet();
                                                		Iterator<String> it = keys.iterator();
                                                		
                                                		while (it.hasNext()) {
                                                			String key = it.next();
                                                			Map<String, String> empstatusMp = userTypeMp.get(key);
                                                			if (empstatusMp == null) 
                                                				empstatusMp = new HashMap<String, String>();
                                                			List<String> employeeList = empMp.get(key);
                                                			for (int ii = 0; employeeList != null && ii < employeeList.size(); ii++) {
                                                				appCnt++; 
                                                				String role = orientationMemberMp.get(key);
                                                				if(uF.parseToInt(hmAppraisalMp.get("MY_REVIEW_STATUS")) == 0 || (uF.parseToInt(hmAppraisalMp.get("MY_REVIEW_STATUS")) == 1 && uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_PUBLISH")) == true)
                                                						|| (uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_PUBLISH")) == false && uF.parseToInt(hmAppraisalMp.get("MY_REVIEW_STATUS")) == 1 && role != null && role.equals("Self"))) {
                                                				double dblRate = uF.parseToDouble(hmScoreAggregateMap.get(appraisalIdList.get(j) + "_" + employeeList.get(ii))) / 20;
                                                	
                                                				List<String> sectionIDList = hmExistSectionID.get(appraisalIdList.get(j)+"_"+key+ "_" +employeeList.get(ii));
                                                				Map<String, List<String>> hmRemainOrientDetails = hmRemainOrientDetailsAppWise.get(appraisalIdList.get(j));
                                                				if (hmRemainOrientDetails == null) hmRemainOrientDetails = new HashMap<String, List<String>>();
                                                
                                                				Map<String, List<String>> hmRemainOrientDetailsForSelf = hmRemainOrientDetailsForSelfAppWise.get(appraisalIdList.get(j));
                                                				if (hmRemainOrientDetailsForSelf == null) hmRemainOrientDetailsForSelf = new HashMap<String, List<String>>();
                                                
                                                				Map<String, List<String>> hmRemainOrientDetailsForPeer = hmRemainOrientDetailsForPeerAppWise.get(appraisalIdList.get(j));
                                                				if (hmRemainOrientDetailsForPeer == null) hmRemainOrientDetailsForPeer = new HashMap<String, List<String>>();
                                                
                                                				Map<String, List<String>> hmExistOrientTypeAQA = hmExistOrientTypeAQAAppWise.get(appraisalIdList.get(j));
                                                				if (hmExistOrientTypeAQA == null) hmExistOrientTypeAQA = new HashMap<String, List<String>>();
                                                    				
                                                    %>
								<li class="list">
									<div style="margin-bottom: 10px;">
										<div>
											<span><b><%=hmAppraisalMp.get("APPRAISAL")%></b> for <% if (strSessionEmpId.equals(employeeList.get(ii))) { %>
												You <% } else { %> <%=hmEmpName.get(employeeList.get(ii))%> <% } %>
												<% if((alSelfFillEmpIds != null && alSelfFillEmpIds.size() > 0 && alSelfFillEmpIds.contains(strSessionEmpId) && role != null && role.equals("Self")) || (role != null && !role.equals("Self"))) { %>
												[Role-<%=role%>] <%=uF.showData(" ("+hmAppraisalMp.get("APP_FREQ_NAME")+")","")%>
												<% } %> <br /> <i><%=hmAppraisalMp.get("REVIEW_TYPE")%>, <%=hmAppraisalMp.get("ORIENT")%>&deg;,
											</i> </span>
											<% if (uF.parseToInt(hmAppraisalMp.get("MY_REVIEW_STATUS")) == 1 && role != null && role.equals("Self")) { %>
											<span style="float: right; margin-top: 5px; margin-right: 10px;">
												<% if(!uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_CLOSE")) && (!uF.parseToBoolean(hmAppraisalMp.get("APP_FINAL_STATUS")) && !uF.parseToBoolean(hmAppraisalMp.get("APP_STATUS")))){ %>
												<span id="myDivM<%=j%>"> <% if (uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_PUBLISH")) == true) { %>
													<a onclick="if(confirm('Are you sure, you want to unpublish this review?'))getPublishAppraisal('<%=appId%>', '<%=j%>','<%=strSessionEmpId %>','<%=appFreqId %>');"
													href="javascript:void(0)"> <img title="Published" src="images1/icons/icons/publish_icon_b.png">
												</a> <% } else { %> <% if(uF.parseToInt(hmReviewId.get(appId)) > 0) { %>
													<a onclick="getApprovalStatus('<%=appId%>', '<%=strEmpName %>','<%=appFreqId %>');" href="javascript:void(0)">
														<img title="Waiting for workflow approval" src="images1/icons/icons/unpublish_icon_b.png">
												</a> <% } else { %> <a onclick="if(confirm('Are you sure, you want to publish this review?'))getPublishAppraisal('<%=appId%>', '<%=j%>','<%=strSessionEmpId %>','<%=appFreqId %>');"
													href="javascript:void(0)"> <img title="Waiting to be publish" src="images1/icons/icons/unpublish_icon_b.png">
												</a> <% } %> <% } %> </span> <% } else { %> <a class="approve-font" title="In progress"></a> <% } %> <a onclick="openAppraisalPreview('<%=appId %>','<%=appFreqId %>')"
												href="javascript: void(0)" title="Preview"><i class="fa fa-eye" aria-hidden="true"></i>
											</a> <a href="MyReviewStatus.action?id=<%=appId %>&appFreqId=<%=appFreqId %>"><img title="Status" src="images1/icons/icons/status_icon.png">
											</a>&nbsp; <%if(!uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_CLOSE")) && (!uF.parseToBoolean(hmAppraisalMp.get("APP_FINAL_STATUS")) || !uF.parseToBoolean(hmAppraisalMp.get("APP_STATUS")))) { %>
												<a href="MyReviewSummary.action?id=<%=appId%>&appFreqId=<%=appFreqId%>" target="_new" title="Edit Review">
													<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
											</a> <a href="javascript:void(0);" onclick="closeMyReview('<%=appId%>','close','<%=appFreqId%>');" title="Close My Review">
												<i class="fa fa-times-circle-o" aria-hidden="true"></i>
											</a> <% } else { %> <a href="javascript:void(0);" onclick="closeMyReview('<%=appId%>','view','<%=appFreqId%>');" title="Close My Review Reason">
												<i class="fa fa-comment-o" aria-hidden="true"></i>
											</a> <% } %> </span>
											<% } %>
										</div>
										<% if (uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_PUBLISH")) == true) { %>
										<div>
											<%  List<String> innList = hmAppraisalNextStep.get(appId+"_"+appFreqId);
                                                    	    	Iterator<String> it1 = userTypeMp.keySet().iterator();
                                                           		boolean flag1 = false;
	                                                      		while (it1.hasNext()) {
	                                                      			String userTypeId = it1.next();
	                                                      			List<String> innList1 = hmNextStepFillEmpIds.get(appId+"_"+appFreqId+"_"+userTypeId);
	                                                      			if(innList != null && innList.contains(userTypeId) && innList1 != null && innList1.contains(strSessionEmpId)) {
	                                                      				flag1 = true;
	                                                      			}
	                                                      		}
                                                                if((alSelfFillEmpIds != null && alSelfFillEmpIds.size()>0 && alSelfFillEmpIds.contains(strSessionEmpId) && role != null && role.equals("Self")) || (role != null && !role.equals("Self"))) {
                                                                	int secCnt = 0;
                                                                    boolean sectionFilled = false;
                                                                	for (int k = 0; listAppSections != null && !listAppSections.isEmpty() && k < listAppSections.size(); k++) {
                                                                		List<String> innerList = listAppSections.get(k);
                                                                		
                                                                		List<String> listRemainOrientName = hmRemainOrientDetails.get(innerList.get(0) + "NAME");
                                                                		List<String> listRemainOrientID = hmRemainOrientDetails.get(innerList.get(0) + "ID");
                                                                
                                                                		List<String> listRemainOrientNameForSelf = hmRemainOrientDetailsForSelf.get(innerList.get(0) + "NAME");
                                                                		List<String> listRemainOrientIDForSelf = hmRemainOrientDetailsForSelf.get(innerList.get(0) + "ID");
                                                                
                                                                		List<String> listRemainOrientNameForPeer = hmRemainOrientDetailsForPeer.get(innerList.get(0) + "NAME");
                                                                		List<String> listRemainOrientIDForPeer = hmRemainOrientDetailsForPeer.get(innerList.get(0) + "ID");
                                                                		List<String> listExistOrientTypeInAQA = hmExistOrientTypeAQA.get(innerList.get(0) + "_" + employeeList.get(ii));
                                                                
                                                                		List<String> listRemainOrientType = new ArrayList<String>();
                                                                		List<String> listRemainOrientTypeForSelf = new ArrayList<String>();
                                                                		List<String> listRemainOrientTypeForPeer = new ArrayList<String>();
                                                                		StringBuilder sbRemainOrientTypeID = new StringBuilder();
                                                                		StringBuilder sbRemainOrientTypeIDForSelf = new StringBuilder();
                                                                		StringBuilder sbRemainOrientTypeIDForPeer = new StringBuilder();
                                                                		
                                                                		for (int b = 0; listRemainOrientID != null && b < listRemainOrientID.size(); b++) {
                                                                			if (listExistOrientTypeInAQA != null) {
                                                                
                                                                				if (!listRemainOrientID.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientID.get(b).trim())) {
                                                                					listRemainOrientType.add(listRemainOrientName.get(b));
                                                                					sbRemainOrientTypeID.append(listRemainOrientID.get(b) + ",");
                                                                				} else if (!listRemainOrientID.get(b).trim().equals("3")) {
                                                                					List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(ii) + "_" + innerList.get(0) + "_"+ listRemainOrientID.get(b));
                                                                					List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j) + "_" + listRemainOrientID.get(b));
                                                                					boolean flag = false;
                                                                					for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                                                						if (listExistUserInAQA != null) {
                                                                							if (!listIds.get(a).trim().equals("") && uF.parseToInt(listIds.get(a).trim()) > 0 && !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                                                								flag = true;
                                                                							}
                                                                						}
                                                                					}
                                                                					if (flag == true) {
                                                                						listRemainOrientType.add(listRemainOrientName.get(b));
                                                                						sbRemainOrientTypeID.append(listRemainOrientID.get(b) + ",");
                                                                					}
                                                                				}
                                                                			} else {
                                                                				listRemainOrientType.add(listRemainOrientName.get(b));
                                                                				sbRemainOrientTypeID.append(listRemainOrientID.get(b) + ",");
                                                                			}
                                                                		}
                                                                		for (int b = 0; listRemainOrientIDForSelf != null && b < listRemainOrientIDForSelf.size(); b++) {
                                                                			if (listExistOrientTypeInAQA != null) {
                                                                
                                                                				if (!listRemainOrientIDForSelf.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientIDForSelf.get(b).trim())) {
                                                                					listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                                                					sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                                                				} else {
                                                                					List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(ii) + "_" + innerList.get(0) + "_"+ listRemainOrientIDForSelf.get(b));
                                                                					List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j) + "_" + listRemainOrientIDForSelf.get(b));
                                                                					boolean flag = false;
                                                                					for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                                                						if (listExistUserInAQA != null) {
                                                                							if (!listIds.get(a).trim().equals("") && uF.parseToInt(listIds.get(a).trim()) > 0 && !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                                                								flag = true;
                                                                							} else {
                                                                								flag = false;
                                                                								break;
                                                                							}
                                                                						}
                                                                					}
                                                                					if (flag == true) {
                                                                						listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                                                						sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                                                					}
                                                                				}
                                                                			} else {
                                                                				listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                                                				sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                                                			}
                                                                		}
                                                                
                                                                		for (int b = 0; listRemainOrientIDForPeer != null && b < listRemainOrientIDForPeer.size(); b++) {
                                                                			if (listExistOrientTypeInAQA != null) {
                                                                
                                                                				if (!listRemainOrientIDForPeer.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientIDForPeer.get(b).trim())) {
                                                                					listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                                                					sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                                                				} else if (!listRemainOrientIDForPeer.get(b).trim().equals("3")) {
                                                                					List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(ii) + "_" + innerList.get(0) + "_"+ listRemainOrientIDForPeer.get(b));
                                                                					List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j) + "_" + listRemainOrientIDForPeer.get(b));
                                                                					boolean flag = false;
                                                                					for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                                                						if (listExistUserInAQA != null) {
                                                                							if (!listIds.get(a).trim().equals("") && uF.parseToInt(listIds.get(a).trim()) > 0 && !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                                                								flag = true;
                                                                							} else {
                                                                								flag = false;
                                                                								break;
                                                                							}
                                                                						}
                                                                					}
                                                                					if (flag == true) {
                                                                						listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                                                						sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                                                					}
                                                                				}
                                                                			} else {
                                                                				listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                                                				sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                                                			}
                                                                		}
                                                                %>
											<%
											if (empstatusMp.get(employeeList.get(ii)+"_"+strSessionEmpId) != null && sectionIDList != null && sectionIDList.contains(innerList.get(0))) {
                                            %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #f7ee1d" title="Waiting for Approval"></i>

											<% } else if (listRemainOrientType != null && !listRemainOrientType.isEmpty() && !role.equalsIgnoreCase("Self") && !role.equalsIgnoreCase("Peer")) { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #ea9900" title="Waiting for Approval"></i>

											<% } else if (listRemainOrientTypeForSelf != null && !listRemainOrientTypeForSelf.isEmpty() && role.equalsIgnoreCase("Self")) { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #ea9900" title="Waiting for Approval"></i>

											<% } else if (listRemainOrientTypeForPeer != null && !listRemainOrientTypeForPeer.isEmpty() && role.equalsIgnoreCase("Peer")) { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #ea9900" title="Waiting for Approval"></i>

											<% } else { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #b71cc5" title="Waiting"> </i>

											<% } %>
											<%
												if (empstatusMp.get(employeeList.get(ii)+"_"+strSessionEmpId) != null && sectionIDList != null && sectionIDList.contains(innerList.get(0)) 
														&& uF.parseToInt(hmSectionQueCnt.get(hmAppraisalMp.get("ID")+"_"+innerList.get(0))) == uF.parseToInt(hmSectionGivenQueCnt.get(key+"_"+hmAppraisalMp.get("ID")+"_"+appFreqId+"_"+"0"+"_"+employeeList.get(ii)+"_"+innerList.get(0)))) {
													if(flag1) {
                                                %>
											<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(ii)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=appFreqId%>')"><%=innerList.get(1) %></a>
											<%--  Section(<%=k + 1%>) --%>
											<% } else { %>
											<a href="javascript: void(0);" onclick="staffAppraisalPreview('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(ii)%>','<%=key%>','<%=appFreqId%>')"><%=innerList.get(1) %></a>
											<% } %>
											<% } else if (listRemainOrientType != null && !listRemainOrientType.isEmpty() && !role.equalsIgnoreCase("Self") && !role.equalsIgnoreCase("Peer")) { %>

											<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(ii)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeID.toString()%>','<%=appFreqId%>');"><%=innerList.get(1) %></a>
											<% } else if (listRemainOrientTypeForSelf != null && !listRemainOrientTypeForSelf.isEmpty() && role.equalsIgnoreCase("Self")) { %>

											<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(ii)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForSelf.toString()%>','<%=appFreqId%>');"><%=innerList.get(1) %></a>
											<% } else if (listRemainOrientTypeForPeer != null && !listRemainOrientTypeForPeer.isEmpty() && role.equalsIgnoreCase("Peer")) { %>

											<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(ii)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForPeer.toString()%>','<%=appFreqId%>');"><%=innerList.get(1) %></a>
											<% } else { %>
											<% if(secCnt == 0 || sectionFilled) { %>
											<a href="javascript: void(0);" onclick="staffReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(ii)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=appFreqId%>')">
												<%=innerList.get(1) %></a>
											<% } else { %>
											<%=innerList.get(1) %>
											<% } %>

											<% } %>
											<br />
											<% secCnt++; 
                                             if(uF.parseToInt(hmSectionQueCnt.get(hmAppraisalMp.get("ID")+"_"+innerList.get(0))) == uF.parseToInt(hmSectionGivenQueCnt.get(key+"_"+hmAppraisalMp.get("ID")+"_"+appFreqId+"_"+"0"+"_"+employeeList.get(ii)+"_"+innerList.get(0)))) {
                                             	sectionFilled = true;
                                             } else {
                                             	sectionFilled = false;
                                             }
                                             
                                                	}
                                                }	
                                            %>
										</div>
										<% if(rateFlag) { %>
										<% if (role != null && role.equals("Self")) { %>
										<% if (dblRate >= 3.5) { %>
										<div
											style="background-color: #00FF00; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
										</div>
										<% } else if (dblRate < 3.5 && dblRate >= 2.5) { %>
										<div
											style="background-color: #FFFF00; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
										</div>
										<% } else if (dblRate > 0) {  //#FF0000; %>
										<div
											style="background-color: #FF0000; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
										</div>
										<% } %>
										<% } %>
										<% } %>
										<%} %>
									</div></li>
								<% }
									}
									}
								%>
								<% } %>
							</ul>
							<% }
								if (appraisalIdList == null || appraisalIdList.isEmpty() || appraisalIdList.size() == 0 || appCnt == 0) { %>
							<div class="nodata msg" style="width: 96%;">No reviews assigned.</div>
							<% } %>
						</div>
					</div>
					<!-- /.box-body -->
				</div>


				<div class="box box-primary"
					style="border-top-color: #cda55f /*#E0E0E0;*/">
					<div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px !important;">Reviewed Forms</h3>
					</div>
					<!-- /.box-header -->
					<div class="box-body" style="padding: 5px; overflow-y: auto;">
						<div class="details_lables">
							<%
		                       	int appReviewedCnt = 0;
		                       	if(appraisalIdList != null && !appraisalIdList.isEmpty()) {
	                       	%>
							<ul style="margin-bottom: 10px; padding-left: 10px;">
								<%
                                              	for (int j = 0; appraisalIdList != null && !appraisalIdList.isEmpty() && j < appraisalIdList.size(); j++) {
                                              		Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdList.get(j));
                                              		if (userTypeMp == null) userTypeMp = new HashMap<String, Map<String, String>>();
                                              		Map<String, String> hmAppraisalMp = appraisalDetails.get(appraisalIdList.get(j));
                                            			Map<String, List<String>> hmReviewedEmp = hmReviewedEmpDetails.get(appraisalIdList.get(j));
                                              		
                                              		String appId = hmAppraisalMp.get("ID");
              										String appFreqId = hmAppraisalMp.get("APP_FREQ_ID");
              										List<List<String>> listAppSections = hmAppraisalSectins.get(appId);
                                              		List<String> alSelfFillEmpIds = new ArrayList<String>();
                                              		if(hmAppraisalMp != null && hmAppraisalMp.size()>0 && hmAppraisalMp.get("SELFID")!= null && !hmAppraisalMp.get("SELFID").equals("")) {
                                              			alSelfFillEmpIds = Arrays.asList(hmAppraisalMp.get("SELFID").split(","));
                                              		}
                                              		Set<String> keys = hmReviewedEmp.keySet();
                                              		Iterator<String> it = keys.iterator();
                                              		
                                              		while (it.hasNext()) {
                                              			String key = it.next();
                                              			Map<String, String> empstatusMp = userTypeMp.get(key);
                                              			if (empstatusMp == null) 
                                              				empstatusMp = new HashMap<String, String>();
                                              			List<String> reviewedEmpList = hmReviewedEmp.get(key);
                                              			for (int ii = 0; reviewedEmpList != null && ii < reviewedEmpList.size(); ii++) {
                                              				appReviewedCnt++; 
                                              				String role = orientationMemberMp.get(key);
                                              				if(uF.parseToInt(hmAppraisalMp.get("MY_REVIEW_STATUS")) == 0 || (uF.parseToInt(hmAppraisalMp.get("MY_REVIEW_STATUS")) == 1 && uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_PUBLISH")) == true)
                                              						|| (uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_PUBLISH")) == false && uF.parseToInt(hmAppraisalMp.get("MY_REVIEW_STATUS")) == 1 && role != null && role.equals("Self"))) {
                                              				double dblRate = uF.parseToDouble(hmScoreAggregateMap.get(appraisalIdList.get(j) + "_" + reviewedEmpList.get(ii))) / 20;
                                              	
                                              				List<String> sectionIDList = hmExistSectionID.get(appraisalIdList.get(j)+"_"+key+"_"+reviewedEmpList.get(ii));
                                              				Map<String, List<String>> hmRemainOrientDetails = hmRemainOrientDetailsAppWise.get(appraisalIdList.get(j));
                                              				if (hmRemainOrientDetails == null) hmRemainOrientDetails = new HashMap<String, List<String>>();
                                              
                                              				Map<String, List<String>> hmRemainOrientDetailsForSelf = hmRemainOrientDetailsForSelfAppWise.get(appraisalIdList.get(j));
                                              				if (hmRemainOrientDetailsForSelf == null) hmRemainOrientDetailsForSelf = new HashMap<String, List<String>>();
                                              
                                              				Map<String, List<String>> hmRemainOrientDetailsForPeer = hmRemainOrientDetailsForPeerAppWise.get(appraisalIdList.get(j));
                                              				if (hmRemainOrientDetailsForPeer == null) hmRemainOrientDetailsForPeer = new HashMap<String, List<String>>();
                                              
                                              				Map<String, List<String>> hmExistOrientTypeAQA = hmExistOrientTypeAQAAppWise.get(appraisalIdList.get(j));
                                              				if (hmExistOrientTypeAQA == null) hmExistOrientTypeAQA = new HashMap<String, List<String>>();
                                                    				
                                                    %>
								<li class="list">
									<div style="margin-bottom: 10px;">
										<div>
											<span><b><%=hmAppraisalMp.get("APPRAISAL")%></b> for <% if (strSessionEmpId.equals(reviewedEmpList.get(ii))) { %>
												You <% } else { %> <%=hmEmpName.get(reviewedEmpList.get(ii))%>
												<% } %> <% if((alSelfFillEmpIds != null && alSelfFillEmpIds.size() > 0 && alSelfFillEmpIds.contains(strSessionEmpId) && role != null && role.equals("Self")) || (role != null && !role.equals("Self"))) { %>
												[Role-<%=role%>] <%=uF.showData(" ("+hmAppraisalMp.get("APP_FREQ_NAME")+")","")%>
												<% } %> <br /> <i><%=hmAppraisalMp.get("REVIEW_TYPE")%>, <%=hmAppraisalMp.get("ORIENT")%>&deg;,
											</i> </span>
										</div>
										<% if (uF.parseToBoolean(hmAppraisalMp.get("FREQ_IS_PUBLISH")) == true) { %>
										<div>
											<%  List<String> innList = hmAppraisalNextStep.get(appId+"_"+appFreqId);
                                                     	    		Iterator<String> it1 = userTypeMp.keySet().iterator();
                                                            		boolean flag1 = false;
		                                                      		while (it1.hasNext()) {
		                                                      			String userTypeId = it1.next();
		                                                      			List<String> innList1 = hmNextStepFillEmpIds.get(appId+"_"+appFreqId+"_"+userTypeId);
		                                                      			if(innList != null && innList.contains(userTypeId) && innList1 != null && innList1.contains(strSessionEmpId)) {
		                                                      				flag1 = true;
		                                                      			}
		                                                      		}
		                                                      		
                                                                if((alSelfFillEmpIds != null && alSelfFillEmpIds.size()>0 && alSelfFillEmpIds.contains(strSessionEmpId) && role != null && role.equals("Self")) || (role != null && !role.equals("Self"))) {
                                                                	int secCnt = 0;
                                                                    boolean sectionFilled = false;
                                                                	for (int k = 0; listAppSections != null && !listAppSections.isEmpty() && k < listAppSections.size(); k++) {
                                                                		List<String> innerList = listAppSections.get(k);
                                                                		
                                                                		List<String> listRemainOrientName = hmRemainOrientDetails.get(innerList.get(0) + "NAME");
                                                                		List<String> listRemainOrientID = hmRemainOrientDetails.get(innerList.get(0) + "ID");
                                                                
                                                                		List<String> listRemainOrientNameForSelf = hmRemainOrientDetailsForSelf.get(innerList.get(0) + "NAME");
                                                                		List<String> listRemainOrientIDForSelf = hmRemainOrientDetailsForSelf.get(innerList.get(0) + "ID");
                                                                
                                                                		List<String> listRemainOrientNameForPeer = hmRemainOrientDetailsForPeer.get(innerList.get(0) + "NAME");
                                                                		List<String> listRemainOrientIDForPeer = hmRemainOrientDetailsForPeer.get(innerList.get(0) + "ID");
                                                                		List<String> listExistOrientTypeInAQA = hmExistOrientTypeAQA.get(innerList.get(0) + "_" + reviewedEmpList.get(ii));
                                                                
                                                                		List<String> listRemainOrientType = new ArrayList<String>();
                                                                		List<String> listRemainOrientTypeForSelf = new ArrayList<String>();
                                                                		List<String> listRemainOrientTypeForPeer = new ArrayList<String>();
                                                                		StringBuilder sbRemainOrientTypeID = new StringBuilder();
                                                                		StringBuilder sbRemainOrientTypeIDForSelf = new StringBuilder();
                                                                		StringBuilder sbRemainOrientTypeIDForPeer = new StringBuilder();
                                                                		
                                                                		for (int b = 0; listRemainOrientID != null && b < listRemainOrientID.size(); b++) {
                                                                			if (listExistOrientTypeInAQA != null) {
                                                                
                                                                				if (!listRemainOrientID.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientID.get(b).trim())) {
                                                                					listRemainOrientType.add(listRemainOrientName.get(b));
                                                                					sbRemainOrientTypeID.append(listRemainOrientID.get(b) + ",");
                                                                				} else if (!listRemainOrientID.get(b).trim().equals("3")) {
                                                                					List<String> listExistUserInAQA = hmExistUsersAQA.get(reviewedEmpList.get(ii) + "_" + innerList.get(0) + "_"+ listRemainOrientID.get(b));
                                                                					List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j) + "_" + listRemainOrientID.get(b));
                                                                					boolean flag = false;
                                                                					for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                                                						if (listExistUserInAQA != null) {
                                                                							if (!listIds.get(a).trim().equals("") && uF.parseToInt(listIds.get(a).trim()) > 0 && !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                                                								flag = true;
                                                                							}
                                                                						}
                                                                					}
                                                                					if (flag == true) {
                                                                						listRemainOrientType.add(listRemainOrientName.get(b));
                                                                						sbRemainOrientTypeID.append(listRemainOrientID.get(b) + ",");
                                                                					}
                                                                				}
                                                                			} else {
                                                                				listRemainOrientType.add(listRemainOrientName.get(b));
                                                                				sbRemainOrientTypeID.append(listRemainOrientID.get(b) + ",");
                                                                			}
                                                                		}
                                                                		for (int b = 0; listRemainOrientIDForSelf != null && b < listRemainOrientIDForSelf.size(); b++) {
                                                                			if (listExistOrientTypeInAQA != null) {
                                                                
                                                                				if (!listRemainOrientIDForSelf.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientIDForSelf.get(b).trim())) {
                                                                					listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                                                					sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                                                				} else {
                                                                					List<String> listExistUserInAQA = hmExistUsersAQA.get(reviewedEmpList.get(ii) + "_" + innerList.get(0) + "_"+ listRemainOrientIDForSelf.get(b));
                                                                					List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j) + "_" + listRemainOrientIDForSelf.get(b));
                                                                					boolean flag = false;
                                                                					for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                                                						if (listExistUserInAQA != null) {
                                                                							if (!listIds.get(a).trim().equals("") && uF.parseToInt(listIds.get(a).trim()) > 0 && !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                                                								flag = true;
                                                                							} else {
                                                                								flag = false;
                                                                								break;
                                                                							}
                                                                						}
                                                                					}
                                                                					if (flag == true) {
                                                                						listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                                                						sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                                                					}
                                                                				}
                                                                			} else {
                                                                				listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                                                				sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                                                			}
                                                                		}
                                                                
                                                                		for (int b = 0; listRemainOrientIDForPeer != null && b < listRemainOrientIDForPeer.size(); b++) {
                                                                			if (listExistOrientTypeInAQA != null) {
                                                                
                                                                				if (!listRemainOrientIDForPeer.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientIDForPeer.get(b).trim())) {
                                                                					listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                                                					sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                                                				} else if (!listRemainOrientIDForPeer.get(b).trim().equals("3")) {
                                                                					List<String> listExistUserInAQA = hmExistUsersAQA.get(reviewedEmpList.get(ii) + "_" + innerList.get(0) + "_"+ listRemainOrientIDForPeer.get(b));
                                                                					List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j) + "_" + listRemainOrientIDForPeer.get(b));
                                                                					boolean flag = false;
                                                                					for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                                                						if (listExistUserInAQA != null) {
                                                                							if (!listIds.get(a).trim().equals("") && uF.parseToInt(listIds.get(a).trim()) > 0 && !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                                                								flag = true;
                                                                							} else {
                                                                								flag = false;
                                                                								break;
                                                                							}
                                                                						}
                                                                					}
                                                                					if (flag == true) {
                                                                						listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                                                						sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                                                					}
                                                                				}
                                                                			} else {
                                                                				listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                                                				sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                                                			}
                                                                		}
                                                                %>
											<%
                                                               if (empstatusMp.get(reviewedEmpList.get(ii)+"_"+strSessionEmpId) != null && sectionIDList != null && sectionIDList.contains(innerList.get(0))) {
                                                            %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #f7ee1d" title="Waiting for Approval"></i>

											<% } else if (listRemainOrientType != null && !listRemainOrientType.isEmpty() && !role.equalsIgnoreCase("Self") && !role.equalsIgnoreCase("Peer")) { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #ea9900" title="Waiting for Approval"></i>

											<% } else if (listRemainOrientTypeForSelf != null && !listRemainOrientTypeForSelf.isEmpty() && role.equalsIgnoreCase("Self")) { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #ea9900" title="Waiting for Approval"></i>

											<% } else if (listRemainOrientTypeForPeer != null && !listRemainOrientTypeForPeer.isEmpty() && role.equalsIgnoreCase("Peer")) { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #ea9900" title="Waiting for Approval"></i>

											<% } else { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #b71cc5" title="Waiting"> </i>
											<% } %>
											<%
                                                   if (empstatusMp.get(reviewedEmpList.get(ii)+"_"+strSessionEmpId) != null && sectionIDList != null && sectionIDList.contains(innerList.get(0))) {
                                                	   if(flag1) {
                                                %>
												<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=reviewedEmpList.get(ii)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=appFreqId%>')"><%=innerList.get(1) %></a>
											<% } else { %>
												<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_REVIEW_FEEDBACK_EDIT_AFTER_REVIEW))) { %>
													<a href="javascript: void(0);" onclick="staffAppraisalPreview('<%=hmAppraisalMp.get("ID")%>','<%=reviewedEmpList.get(ii)%>','<%=key%>','<%=appFreqId%>')"><%=innerList.get(1) %></a>
												<% } else { %>
													<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=reviewedEmpList.get(ii)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=appFreqId%>')"><%=innerList.get(1) %></a>
												<% } %>
											<% } %>
											<% } else if (listRemainOrientType != null && !listRemainOrientType.isEmpty() && !role.equalsIgnoreCase("Self") && !role.equalsIgnoreCase("Peer")) { %>

											<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=reviewedEmpList.get(ii)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeID.toString()%>','<%=appFreqId%>');"><%=innerList.get(1) %></a>
											<% } else if (listRemainOrientTypeForSelf != null && !listRemainOrientTypeForSelf.isEmpty() && role.equalsIgnoreCase("Self")) { %>

											<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=reviewedEmpList.get(ii)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForSelf.toString()%>','<%=appFreqId%>');"><%=innerList.get(1) %></a>
											<% } else if (listRemainOrientTypeForPeer != null && !listRemainOrientTypeForPeer.isEmpty() && role.equalsIgnoreCase("Peer")) { %>

											<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=reviewedEmpList.get(ii)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForPeer.toString()%>','<%=appFreqId%>');"><%=innerList.get(1) %></a>
											<% } else { %>
											<% if(secCnt == 0 || sectionFilled) { %>
											<a href="javascript: void(0);" onclick="staffReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=reviewedEmpList.get(ii)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=appFreqId%>')">
												<%=innerList.get(1) %></a>
											<% } else { %>
											<%=innerList.get(1) %>
											<% } %>

											<% } %>
											<br />
											<% secCnt++; 
                                                  if(uF.parseToInt(hmSectionQueCnt.get(hmAppraisalMp.get("ID")+"_"+innerList.get(0))) == uF.parseToInt(hmSectionGivenQueCnt.get(key+"_"+hmAppraisalMp.get("ID")+"_"+appFreqId+"_"+"0"+"_"+reviewedEmpList.get(ii)+"_"+innerList.get(0)))) {
                                                  	sectionFilled = true;
                                                  } else {
                                                  	sectionFilled = false;
                                                  }
                                                     	}
                                                     }	
                                                 %>
										</div>
										<% if(rateFlag) { %>
										<% if (role != null && role.equals("Self")) { %>
										<% if (dblRate >= 3.5) { %>
										<div
											style="background-color: #00FF00; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
										</div>
										<% } else if (dblRate < 3.5 && dblRate >= 2.5) { %>
										<div
											style="background-color: #FFFF00; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
										</div>
										<% } else if (dblRate > 0) {  //#FF0000; %>
										<div
											style="background-color: #FF0000; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
										</div>
										<% } %>
										<% } %>
										<% } %>
										<%} %>
									</div></li>
								<% }
                                                    }
                                                     }
                                                    %>
								<% } %>
							</ul>
							<% }
                                             if (appraisalIdList == null || appraisalIdList.isEmpty() || appraisalIdList.size() == 0 || appReviewedCnt == 0) { %>
							<div class="nodata msg" style="width: 96%;">No forms
								reviewed.</div>
							<% } %>
						</div>
					</div>
					<!-- /.box-body -->
				</div>

				</section>


				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_GOAL_KRA_TARGET))) { %>
				<section class="col-lg-4 connectedSortable">
				<h3 style="margin-top: 0px; font-size: 14px !important; font-weight: 600;" class="pagetitle">My Goal</h3> <!-- My Goals, KRAs & Targets -->
				<% if (strSessionUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
				<div>
					<input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=request.getAttribute("strID")%>"> 
					<a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myGoal();" class="" title="Add New Objective">
						<i class="fa fa-plus-circle" aria-hidden="true"></i>&nbsp;&nbsp;Add new Objective</a>  <!-- Add New Personal Goals & Targets -->
				</div>
				<% } %> <br />
				<%
                                Map<String, Map<String, Map<String, List<List<String>>>>> hmGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmGoalKraEmpwise");
                                if(hmGoalKraEmpwise == null) hmGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                                
                                Map<String, List<String>> hmGoalDetails = (Map<String, List<String>>) request.getAttribute("hmGoalDetails");
                                if(hmGoalDetails == null) hmGoalDetails = new HashMap<String, List<String>>();
                                System.out.println("hmGoalDetails:"+hmGoalDetails);
                              
                                Map<String, List<String>> hmGoalKraPerspective = (Map<String, List<String>>) request.getAttribute("hmGoalKraPerspective");
                                if(hmGoalKraPerspective == null) hmGoalKraPerspective = new HashMap<String, List<String>>();
                                
                                System.out.println("hmGoalKraPerspective:"+hmGoalKraPerspective);

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
                                
                                Map<String, String> hmTargetValue = (Map<String, String>)request.getAttribute("hmTargetValue");
                                Map<String, String> hmTargetID = (Map<String, String>)request.getAttribute("hmTargetID");
                                Map<String, String> hmTargetRemark = (Map<String, String>)request.getAttribute("hmTargetRemark");
                                Map<String, String> hmUpdateBy = (Map<String, String>)request.getAttribute("hmUpdateBy");
                                
                                String strCurrency = (String) request.getAttribute("strCurrency");
                                
                                
                                List<String> empList=(List<String>)request.getAttribute("empList");
                                Map<String, String> hmHrIds = (Map<String, String>) request.getAttribute("hmHrIds");
                                if(hmHrIds == null) hmHrIds = new HashMap<String, String>();
                                
                                Map<String, String> hmEmpCodeName = (Map<String, String>)request.getAttribute("hmEmpName");
                                
                                Map<String, Map<String, Map<String, List<List<String>>>>> hmIndividualGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmIndividualGoalKraEmpwise");
                                if(hmIndividualGoalKraEmpwise == null) hmIndividualGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                                
                                Map<String, List<String>> hmIndividualGoalDetails = (Map<String, List<String>>) request.getAttribute("hmIndividualGoalDetails");
                                if(hmIndividualGoalDetails == null) hmIndividualGoalDetails = new HashMap<String, List<String>>();
                                
                                Map<String, String> hmKraAverage = (Map<String, String>)request.getAttribute("hmKraAverage");
                                if(hmKraAverage == null) hmKraAverage = new HashMap<String, String>();
                                
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
                                
                                Map<String, List<String>> hmActualAchievedGoal = (Map<String, List<String>>)request.getAttribute("hmActualAchievedGoal");
                    			if(hmActualAchievedGoal == null) hmActualAchievedGoal = new HashMap<String, List<String>>();
                                
                    			 Map<String,String> hmGoaldetailsData = ( Map<String,String>)request.getAttribute("hmGoaldetailsData");
                    			 if(hmGoaldetailsData == null) hmGoaldetailsData = new HashMap<String,String>(); 
								 //System.out.println("hmGoaldetailsData :"+hmGoaldetailsData);
                    			 Map<String, Map<String, List<List<String>>>> hmGoalKraSuperIdwise = hmGoalKraEmpwise.get(strSessionEmpId);
                             	String perspectiveId = hmGoaldetailsData.get(strSessionEmpId);
                    			  Map<String,String> hmGoal = ( Map<String,String>)request.getAttribute("hmGoal");
                    			 String GoalId = hmGoal.get(perspectiveId);
                    			 System.out.println("perspectiveId :"+perspectiveId+"GoalId:"+GoalId);
                    			 
                    			 Map<String, Map<String, String>> hmPerspectiveData =(Map<String, Map<String, String>>)request.getAttribute("hmPerspectiveData");
                    			 if(hmPerspectiveData==null) hmPerspectiveData = new HashMap<String, Map<String, String>>();
                    			 	Map<String, String> hmPerspectData = hmPerspectiveData.get(perspectiveId);
                    			
                    			   
                    			 System.out.println("hmPerspectiveData ===>> " + hmPerspectiveData);

                                	String perspectColor = hmPerspectData.get("PERSPECTIVE_COLOR");
                                	String perspectName = hmPerspectData.get("PERSPECTIVE_NAME");
                                
                                  	 %>
                                 
                                   	
                                	<div class="box box-primary collapsed-box" style="border-top-color: #E0E0E0;"> 
                                	
                                	  	<div class="box-header with-border" id="headerName" style="height: 40px;">
                                	  		
											<h3 class="box-title" id ="nameper" style="font-size: 14px !important; color: #000000 !important; font-weight: 500 !important;"><%= perspectName %>
											</h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse" onClick=" hideData();"><i class="fa fa-plus"></i></button>
											<button class="btn btn-box-tool" data-widget="remove" onClick="showPerData();"><i class="fa fa-times"></i></button>
										</div>
										</div>
									
										<div class="box-body" style="padding: 5px; overflow-y: auto; display: none; margin-top:-60px;">
											
										 <div align="center" style="float: left; margin-top:20px; width: 3%;padding-top:150px;word-wrap:break-word;border-radius:5px;height:500px;background-color:<%=perspectColor%>">
                                              <%=perspectName%>
										</div> 
										<div style="float: left; width: 95%; margin-bottom: 5px; margin-top:5px; margin-left:5px;">
										
									 <%
										
                                   		if (hmGoalKraSuperIdwise != null && !hmGoalKraSuperIdwise.isEmpty()) {
                                    	Iterator<String> itGoalSIKRA = hmGoalKraSuperIdwise.keySet().iterator();
                                    	
                                    	int goalCount = 0;
                                    	while (itGoalSIKRA.hasNext()) {
                                
                                    		String superId = itGoalSIKRA.next();								
                                    		Map<String, List<List<String>>> hmGoalKra = hmGoalKraSuperIdwise.get(superId);
                                    		if(hmGoalKra == null) hmGoalKra = new HashMap<String, List<List<String>>>();
                                    		
                                    		Iterator<String> itgKRA = hmGoalKra.keySet().iterator();
                                    		 while(itgKRA.hasNext()) {
                                    			goalCount++;
                                    			String goalAndFreqId = itgKRA.next();
                                    			
                                    			System.out.println("GoalId:"+GoalId);
                                    			System.out.println("goalAndFreqId:"+goalAndFreqId);
                                    			List<String> gInnerList = hmGoalKraPerspective.get(GoalId);
                                    			//System.out.println("gInnerList"+gInnerList);
                                    			
                                    			String goalid = gInnerList.get(1);
                                    			
                                    			String goalFreqId = gInnerList.get(32);
                                    			double avgGoalRating = 0.0d;
                                    			double dblWeightScore = 0.0d;
                                    			
                                    			
                                    			if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                    				String goalRating = hmEmpwiseGoalRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
                                    				String goalTaskCount = hmEmpwiseGoalRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                    				
                                    				String goalEmpRating = hmEmpwiseGoalEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
                                    				String goalEmpTaskCount = hmEmpwiseGoalEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                    				if(uF.parseToInt(goalTaskCount) > 0 || uF.parseToInt(goalEmpTaskCount) > 0) {
                                    					avgGoalRating = (uF.parseToDouble(goalRating) + uF.parseToDouble(goalEmpRating)) / (uF.parseToInt(goalTaskCount) + uF.parseToInt(goalEmpTaskCount));
                                    				}
                                    				dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                                    			} else {
                                    				double avgEmpGoalRating = 0;
                                    				avgGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING"));
                                    				if(uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT")) > 0) {
                                    					avgEmpGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING"))/ uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT"));
                                    					if(avgGoalRating > 0) {
                                    						avgGoalRating = (avgGoalRating + avgEmpGoalRating) / 2; 
                                    					} else {
                                    						avgGoalRating = avgEmpGoalRating;
                                    					}
                                    				}
                                    				
                                    				dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                                    			}
                                    			
                                    			
                                    			String strGoalType = "Goal";
                                    			if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                    				strGoalType = "KRA";
                                    			} else if(gInnerList.get(20) != null && gInnerList.get(20).equals("Measure")) {
                                    				strGoalType = "Target";
                                    			}
                                    			String strPHeight = "";
                                    			if (uF.parseToInt(gInnerList.get(18)) == IConstants.PERSONAL_GOAL && strSessionUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
                                    				strPHeight = "height: auto;";
                                    			}
                                    			
                                    			
                                    	%> <%  
										 boolean flag = true;
										 if(gInnerList.get(34)!= null && uF.parseToInt(gInnerList.get(34)) > 5) {
											 flag = false;
										 }	
									 	  String orientaionKey = gInnerList.get(34)+"_"+strUserTypeId;
									 	  String userId = gInnerList.get(36);
									 	
										 if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationViewAccess.get(orientaionKey)) || gInnerList.get(35).contains(","+strSessionEmpId+",")))) { 
										 %>
				 <div style="border-top-color: #E0E0E0;">
					 <div  style="height: 40px;">
						<h3 class="box-title"
							style="font-size: 14px !important; color: #000000 !important; font-weight: 500 !important;"><%=gInnerList.get(3) %>
							<%=(gInnerList.get(33) != null && !gInnerList.get(33).equals("")) ? "["+gInnerList.get(33)+"]" : "" %>
							(<%=strGoalType %>)
							<% if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>
							<% if (uF.parseToInt(gInnerList.get(18)) == IConstants.PERSONAL_GOAL && strSessionUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
							<%if(!uF.parseToBoolean(gInnerList.get(12))) { %>
							<a href="javascript:void(0);" onclick="closeGoal('<%=goalid %>', 'close');" title="Close Goal"><i class="fa fa-times-circle-o" aria-hidden="true"></i>
							</a>
							<% if(!alCheckList.contains(goalid)) { %>
							<a href="javascript:void(0)" style="float: right;" class="del" title="Delete Initiative" onclick="deleteGoal('<%=goalid %>')"><span class="fa fa-trash" style="color: rgb(218, 0, 0);"></span>
							</a>
							<% } else { %>
							<a href="javascript:void(0)" class="del" onclick="alert('You have already updated the Goal. You can not delete this Goal.');"> <span class="fa fa-trash" style="color: rgb(218, 0, 0);"></span>
							</a>
							<% } %>
							<a href="javascript:void(0)" style="float: right; padding-right: 3px;" class="edit_lvl" onclick="editGoal('<%=goalid %>')" title="Edit Initiative"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>
							</a>
							<% } else { %>
							<a href="javascript:void(0);" onclick="closeGoal('<%=goalid %>', 'view');" title="Close Initiative Reason"><i class="fa fa-comment-o" aria-hidden="true"></i></a>
							<a href="javascript:void(0);" onclick="openGoalForLive('<%=goalid%>','open');" title="Open Initiative for Live"> <i class="fa fa-reply" aria-hidden="true"></i> </a>
							<%} %>
							<% } 
			                                               }%>
							<% if (uF.parseToInt(gInnerList.get(6)) > 0) { %>
							<a href="javascript:void(0)" style="float: right; margin: 3px 5px;" onclick="goalChart('<%=gInnerList.get(8) %>','<%=gInnerList.get(7) %>','<%=gInnerList.get(6) %>','<%=goalid %>')" title="Goal Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>
							<% } else if (uF.parseToInt(gInnerList.get(18)) != IConstants.PERSONAL_GOAL) { %>
							<span style="float: right; margin: 0px 5px; font-weight: normal;">(Not Aligned)</span>
							<% } %>
							<br />
							<% if (uF.parseToBoolean(gInnerList.get(25)) && gInnerList.get(26) != null && !gInnerList.get(26).equals("")) { %>
							<span style="font-weight: normal; font-size: 11px;">This target aligned with</span> <span style="font-size: 11px;"><%=gInnerList.get(26)%></span>
							<span style="font-weight: normal; font-size: 11px;">(Team Key Result).</span>
							<% } %>
						</h3>
					</div>
						<div style="float: left; width: 100%; margin-bottom: 5px;">
							<div style="float: left; width: 100%;">
								<div style="float: left; width: 100%;">
									<div style="float: right; margin-top: 5px;">
										<div
											id="starPrimaryG<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>"
											style="float: left; margin-top:-18px; width:100%"></div>
										<div style="float: right; margin-right: 10px; margin-top:-14px;">
											<b>Rated Score:</b>
											<%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(dblWeightScore+"")) %>/<%=uF.parseToDouble(gInnerList.get(16)) %></div>
										<div
											style="float: right; margin: 10px 25px 1px 0px; padding-bottom: 3px; padding-right: 3px;text-align:center;">
											<%
												List<String> goalAchieved = hmActualAchievedGoal.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId);
												 if(goalAchieved == null) goalAchieved = new ArrayList<String>();
											%>
											<table cellspacing="0" cellpadding="2"
												class="table table-bordered">
												<tr>
													<th colspan="4" style="padding: 0px;text-align:center; ">Finalisation Score</th>
												</tr>
												<tr>
													<th style="width: 70px; padding: 0px;text-align:center;">Weightage</th>
													<th style="width: 100px; padding: 0px;text-align:center;">Actual Achieved</th>
													<th style="width: 105px; padding: 0px;text-align:center;">Achieved Share</th>
												</tr>

												<tr>
													<td><%=uF.parseToDouble(gInnerList.get(16))%></td>
													<td><%=uF.showData(goalAchieved.size()>0 ? goalAchieved.get(5) : "-", "-")%> </td>
													<td><%=uF.showData(goalAchieved.size()>0 ? goalAchieved.get(4) : "-", "-")%> </td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								<div style="float: left; width: 100%;">
									<span class="<%=gInnerList.get(10) %>" style="float: left; margin-left: 15px; font-size: 12px; line-height: 18px;"><b>Obj:
									</b><%=uF.showData(gInnerList.get(13), "-") %></span>
								</div>
								<div
									style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC;">
									<span class="<%=gInnerList.get(10) %>" style="float: left; margin-left: 15px; font-size: 12px; line-height: 18px;">-
										assigned by <%=gInnerList.get(4)%>, attribute <%=gInnerList.get(14) %>, effective date <%=gInnerList.get(15) %>, due date <%=gInnerList.get(5)%>
									</span>
								</div>
								<script type="text/javascript">
                                    $('#starPrimaryG<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>').raty({
                                   		readOnly: true,
                                   		start: <%=avgGoalRating %>,
                                   		half: true,
                                   		targetType: 'number'
                                    });
                                 </script>
							</div>
							<% 
			                                                if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
			                                                	List<List<String>> goalOuterList = hmGoalKra.get(goalAndFreqId);
			                                                	 for(int j=0; goalOuterList!=null && !goalOuterList.isEmpty() && j<goalOuterList.size(); j++) {
			                                                	 List<String> innerList=goalOuterList.get(j);
			                                                	 List<List<String>> taskOuterList = hmKRATasks.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11));
			                                                	 	double avgKRARating = 0.0d;
			                                                		double dblKRAWeightScore = 0.0d;
			                                                	 	String kraRating = hmEmpwiseKRARating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
			                                                		String kraTaskCount = hmEmpwiseKRARating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
			                                                		if(uF.parseToInt(kraTaskCount) > 0) {
			                                                			avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
			                                                		}
			                                                		
			                                                		String strUserCnt = "";
			                                                		if(hmKRATaskEmpRating != null) {
			                                                			String strUserRating = hmEmpwiseKRAEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
			                                                			strUserCnt = hmEmpwiseKRAEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
			                                                			double avgUserRating = 0;
			                                                			if(uF.parseToInt(strUserCnt) > 0) {
			                                                				avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
			                                                			}
			                                                			if(avgKRARating > 0 && avgUserRating > 0) {
			                                                				avgKRARating = (avgKRARating + avgUserRating) / 2;
			                                                			} else if(avgUserRating > 0) {
			                                                				avgKRARating = avgUserRating;
			                                                			}
			                                                		}
			                                                		dblKRAWeightScore = (avgKRARating * uF.parseToDouble(innerList.get(27))) / 5;
			                       %>
							<ul
								style="float: left; margin: 0px 10px; width: 100%; padding-left: 0px;">
								<li
									style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-top: 10px; margin-bottom: 5px;">
									<div style="float: left; width: 100%;">
										<div style="float: left; margin: 0px 0px 0px 0px; width: 100%;">
											<span class="<%=innerList.get(10) %>"
												style="margin: 0px 0px 0px 15px; float: left;"><strong>KRA:</strong>&nbsp;<%=uF.showData(innerList.get(2), "-") %></span>
										</div>
										<div  width: 100%;">
											<div 
												id="starPrimaryGK<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>_<%=j%>"
												style="float: left;"></div>
											<div style="float: right; margin-right:10px;">
												<b>Rated Score:</b>
												<%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(dblKRAWeightScore+"")) %>/<%=uF.parseToDouble(innerList.get(27)) %></div>
										</div>
										<script type="text/javascript">
                                          $('#starPrimaryGK<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>_<%=j%>').raty({
                                          	readOnly: true,
                                          	start: <%=avgKRARating %>,
                                          	half: true,
                                          	targetType: 'number'
                                          });
                                      </script>
									</div> <% if(taskOuterList!=null && !taskOuterList.isEmpty()) { %>
									<div style="float: left; width: 100%;">
										<div style="float: left; width: 100%; line-height: 12px; margin-top:10px;margin-left:-30px;">
											<span style="font-weight: bold; margin-left: 30px; color: gray;">Tasks:</span>
										</div>
										<% for(int a=0; a<taskOuterList.size(); a++) {
			                                                        	List<String> taskInnerList = taskOuterList.get(a);
			            												String taskStatusPercent = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_STATUS");
			            												String managerRating = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_RATING");
			            												String hrRating = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_RATING");
			            												String managerComment = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_COMMENT");
			            												String hrComment = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_COMMENT");
			                                                            
			                                                            String hrMngrReview = "block";
			                                                            if(uF.parseToInt(taskStatusPercent) == 100 || uF.parseToInt(taskInnerList.get(2)) == 1) {
			                                                            	hrMngrReview = "block";
			                                                            }
			                                                            double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
			                                                            if(managerRating == null) {
			                                                            	avgRating = uF.parseToDouble(hrRating);
			                                                            } else if(hrRating == null) {
			                                                            	avgRating = uF.parseToDouble(managerRating);
			                                                            }
			                                                            
			                                                            String strUserTaskCnt = "";
			                                                            if(hmKRATaskEmpRating != null) {
			                                                            	String strUserRating = hmKRATaskEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_RATING");
			                                                            	strUserTaskCnt = hmKRATaskEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_COUNT");
			                                                            	double avgUserRating = 0;
			                                                            	if(uF.parseToInt(strUserCnt) > 0) {
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
											<div
												style="float: left; margin: 20px 0px 0px 0px; line-height: 15px; width: 25%;"><%=taskInnerList.get(1) %></div>
											<div id="KTProBarDiv_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
												style="float: left; margin: 3px 25px 3px 25px; width: 15%;">
												<div class="anaAttrib1">
													<span
														style="margin-left:<%=uF.parseToDouble(taskStatusPercent) > 95 ? uF.parseToDouble(taskStatusPercent) - 10 : uF.parseToDouble(taskStatusPercent) - 4%>%;"><%=uF.showData(taskStatusPercent, "0")%>%</span>
												</div>
												<div id="outbox" style=" width: 100px";>
													<% if (uF.parseToDouble(taskStatusPercent) < 33.33) { %>
													<div id="redbox"
														style="width: <%=uF.showData(taskStatusPercent, "0") %>%;"></div>
													<% } else if (uF.parseToDouble(taskStatusPercent) >= 33.33 && uF.parseToDouble(taskStatusPercent) < 66.67) { %>
													<div id="yellowbox"
														style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
													<% } else if (uF.parseToDouble(taskStatusPercent) >= 66.67) { %>
													<div id="greenbox"
														style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
													<% } %>
												</div>
												<div class="anaAttrib1" style="float: left; width: 100px;">
													<span style="float: left; margin-left: -4%;">0%</span> <span
														style="float: right; margin-right: -10%;">100%</span>
												</div>
											</div>
											<% String addedBy = taskInnerList.get(4);
												if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(addedBy)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) {%>
											<div
												style="float: left; margin: 15px 0px 0px 50px;">
												<input type="text"
													name="completedPercent_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													id="completedPercent_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													value="<%=uF.showData(taskStatusPercent, "") %>"
													style="width: 40px !important;"
													onkeypress="return isNumberKey(event)" /> <a
													onclick="updateCompletedPercent('<%=strSessionEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>','<%=taskInnerList.get(0) %>', 'KRA');"
													href="javascript:void(0);" title="Update"><i
													class="fa fa-pencil-square-o" aria-hidden="true"></i>
												</a> <br />
												<span
													id="completedPercentStatusSpan_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>
											</div>
											<% } else { %>
											<div
												style="float: left; margin: 20px 5px 3px 15px; width: 15%;">
												<label style="width: 40px;"><%=uF.showData(taskStatusPercent, "") %></label>
												<br />
												<span
													id="completedPercentStatusSpan_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>
											</div>
											<% } %>

											
											<div
												id="GivenTaskRatingDiv_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
												style="float: left; margin-left:-20px;width:100%;">
												<div style="width:40%;">
												 <a style="height: 1px; width: 2px; padding-top:2px;" href="javascript:void(0)" onclick="viewAddComments('<%=strSessionEmpId %>', '<%=goalid%>', '<%=goalFreqId%>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA','Myself')"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Comment</a>
												</div>
												<div id="starPrimaryGKT<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													style="float: left;width:40%;padding-left:5px;  margin-top:-24px;margin-left:120px;"></div>
												<% if(commentCnt > 0 || uF.parseToInt(strUserTaskCnt) > 0) { %>
												<div style="float:right;margin-top:-21px;">
													<a href="javascript:void(0);"
														onclick="viewManagerAndHRComments('<%=strSessionEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA')">Comments
														<%=(commentCnt + uF.parseToInt(strUserTaskCnt)) %></a>
												</div>
												<% } %>
											</div>
											
											<div
												id="TaskRatingDiv_<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
												style="display:none; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
												<input type="hidden"
													name="hideGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													id="hideGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>">
												<div
													id="starGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
												<br />
												<div style="float: left; display:none; margin: 0px 0px 5px 0px;">
													<textarea rows="1" cols="40"
														name="strComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
														id="strComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></textarea>
												</div>
											<div style="float: left;  display:none; margin: 0px 0px 5px 7px;">
													<a href="javascript:void(0);"
														onclick="updateKRATaskRatingAndComment('<%=strSessionEmpId %>', '<%=goalid%>', '<%=goalFreqId%>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA','Myself')"><input
														type="button" class="btn btn-primary" name="update"
														style="padding-bottom: 3px; padding-top: 3px;"
														value="Update">
													</a>
												</div>
											</div>
											<script type="text/javascript">
			                                                                $('#starPrimaryGKT<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
			                                                                	readOnly: true,
			                                                                	start: <%=avgRating %>,
			                                                                	half: true,
			                                                                	targetType: 'number'
			                                                                });
			                                                                
			                                                                $('#starGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
			                                                                 readOnly: false,
			                                                                 start: 0,
			                                                                 half: true,
			                                                                 targetType: 'number',
			                                                                 click: function(score, evt) {
			                                                                 	$('#hideGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').val(score);
			                                                                }
			                                                                });
			                                                            </script>
										</div>
										<% } %>
									</div> <% } %>
								</li>
							</ul>
							<% } %>
							<% } else if(gInnerList.get(20) != null && gInnerList.get(20).equals("Measure")) { %>
							<%
			                                                String target=hmTargetValue.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId);
			                                                String targetID = hmTargetID.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId);
			                                                String targetRemark = hmTargetRemark.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId);
			                                                String assignedTarget = "", measureType="";
			                                                
			                                                String managerRating = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
			                                                String hrRating = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
			                                                String managerComment = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
			                                                String hrComment = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
			                                                
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
			                                                if(hmEmpwiseGoalAndTargetEmpRating != null) {
			                                                	String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
			                                                	strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
			                                                	double avgUserRating = 0;
			                                                	if(uF.parseToInt(strUserCnt) > 0) {
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
							<ul
								style="float: left; margin: 0px 10px; width: 98%; padding-left: 0px;">
								<li
									style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px; margin-top: 10px;">
									<div style="float: left; width: 100%;">
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
			                                                            	if(inttotTargetHrs != 0){
			                                                            		totalTarget= uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
			                                                            	}else{
			                                                            		totalTarget = 100;
			                                                            	}
			                                                            	twoDeciTot = ""+Math.round(totalTarget);
			                                                            	
			                                                            	if(uF.parseToInt(twoDeciTot+"") >= 100) {
			                                                            		hrMngrReview = "block";
			                                                            	}
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
										<div class="row row_without_margin">
											<div class="col-lg-3 col-md-3" style="padding-left: 0px;">
												<div id="<%=i %>ProBarDiv<%=goalCount %>">
													<!-- <div style="width: 100%;"> -->
													<div class="anaAttrib1">
														<span
															style="margin-left:<%=uF.parseToDouble(twoDeciTotProgressAvg) > 97 ? uF.parseToDouble(twoDeciTotProgressAvg)-6 : uF.parseToDouble(twoDeciTotProgressAvg)-3 %>%;"><%=twoDeciTot%>%</span>
													</div>
													<div id="outbox">
														<%if(uF.parseToDouble(twoDeciTotProgressAvg) < 33.33){ %>
														<div id="redbox"
															style="width: <%=twoDeciTotProgressAvg %>%;"></div>
														<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(twoDeciTotProgressAvg) < 66.67){ %>
														<div id="yellowbox"
															style="width: <%=twoDeciTotProgressAvg%>%;"></div>
														<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 66.67){ %>
														<div id="greenbox"
															style="width: <%=twoDeciTotProgressAvg%>%;"></div>
														<%} %>
													</div>
													<div class="anaAttrib1" style="float: left; width: 100%;">
														<span style="float: left; margin-left: -3%;">0%</span> <span
															style="float: right; margin-right: -6%;"><%=total %>%</span>
													</div>
												</div>
											</div>

											<div class="col-lg-6 col-md-6" style="padding-left: 20px;">
												<div>
													<table class="table-bordered table autoWidth">
														<tr>
															<td><strong><u>Target</u>
															</strong>
															</td>
															<td style="padding-left: 10px;"><strong><u>Actual</u>
															</strong>
															</td>
														</tr>
														<tr>
															<td nowrap="nowrap"
																style="vertical-align: top; padding-right: 10px;">
																<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
					                                                                            measureType= "effort";
					                                                                            assignedTarget = gInnerList.get(21);
					                                                                            %>
																<%=gInnerList.get(21) %> <% } else {
					                                                                            measureType= "amount";
					                                                                            assignedTarget = gInnerList.get(21);
					                                                                            %>
																<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																<%=uF.showData(strCurrency,"")%>&nbsp; <% } %> <%=""+uF.parseToDouble(gInnerList.get(21))%>
																<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																&nbsp;% <% } %> <% } %>
															</td>
															<% if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>
															<td style="padding-left: 10px;">
																<div id="<%=i%>spanid<%=goalCount %>">
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
						                                                                                String t=""+uF.parseToDouble(target);
						                                                                                String days="0";
						                                                                                String hours="0";
						                                                                                if(t.contains(".")) {
						                                                                                	t=t.replace(".","_");
						                                                                                	String[] temp=t.split("_");
						                                                                                	days=temp[0];
						                                                                                	hours=temp[1];
						                                                                                }
						                                                                                %>
																	<table>
																		<tr>
																			<td><input type="text" name="mDays"
																				id="<%=i%>mDays<%=goalCount %>"
																				style="width: 30px !important; text-align: right;"
																				value="<%=days %>"
																				onkeyup="checkHrsLimit('<%=i %>','<%=goalCount %>','<%=strSessionEmpId %>')"
																				onkeypress="return isOnlyNumberKey(event)" />
																			</td>
																			<td><input type="text" name="mHrs"
																				style="width: 30px !important; text-align: right;"
																				id="<%=i%>msHrs<%=goalCount %>" value="<%=hours %>"
																				onkeyup="checkHrsLimit('<%=i %>','<%=goalCount %>','<%=strSessionEmpId %>')"
																				onkeypress="return isOnlyNumberKey(event)" />
																			</td>
																			<td><a href="javascript:void(0);"
																				onclick="updateTarget('<%=i%>','<%=goalCount %>','<%=gInnerList.get(19) %>','<%=gInnerList.get(21) %>','<%=gInnerList.get(22) %>', '<%=strSessionEmpId%>', '<%=goalid %>', '<%=goalFreqId %>');"
																				title="Update"><i class="fa fa-pencil-square-o"
																					aria-hidden="true"></i>
																			</a> <%
						                                                                               				String disp = "none";
									                                                                                if(targetRemark == null || targetRemark.equals("")) { 
									                                                                                	disp = "inline";
									                                                                                }
						                                                                               			 %>
																				<span id="<%=i%>remarkSpan<%=goalCount %>"
																				style="display: <%=disp %>;"> <a
																					href="javascript:void(0);"
																					onclick="getMemberData('<%=strSessionEmpId%>', '<%=goalid%>', '<%=goalFreqId %>', 'remark','<%=assignedTarget %>','<%=measureType %>');"
																					title="Add Remark"><i class="fa fa-bookmark"
																						aria-hidden="true"></i>
																				</a> <%-- updatePersonalTagetRemark('<%=targetID %>'); --%>
																			</span></td>
																		</tr>
																		<tr>
																			<td style="padding-right: 5px;">Days</td>
																			<td>Hrs</td>
																			<td></td>
																		</tr>
																	</table>
																	<% } else { %>
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																	<%=uF.showData(strCurrency,"")%>&nbsp;
																	<% } %>
																	<input
																		style="width: 65px !important; text-align: right;"
																		type="text" name="emptarget"
																		id="<%=i%>emptarget<%=goalCount%>"
																		value="<%=target!=null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(target)) : "0" %>"
																		onkeypress="return isNumberKey(event)" />
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																	&nbsp;%
																	<% } %>
																	<a href="javascript:void(0);"
																		onclick="updateTarget('<%=i%>','<%=goalCount %>','<%=gInnerList.get(19) %>','<%=gInnerList.get(21) %>','<%=gInnerList.get(22) %>', '<%=strSessionEmpId%>', '<%=goalid %>', '<%=goalFreqId %>');"
																		title="Update"><i class="fa fa-pencil-square-o"
																		aria-hidden="true"></i>
																	</a>
																	<%
                                                                        String disp = "none";
                                                                        if(targetRemark == null || targetRemark.equals("")) { 
                                                                        	disp = "inline";
                                                                        }
                                                                     %>
																	<span id="<%=i%>remarkSpan<%=goalCount %>"
																		style="display: <%=disp %>;"> <a
																		href="javascript:void(0);"
																		onclick="getMemberData('<%=strSessionEmpId%>', '<%=goalid%>', '<%=goalFreqId %>', 'remark','<%=assignedTarget %>','<%=measureType %>');"
																		title="Add Remark"><i class="fa fa-bookmark"
																			aria-hidden="true"></i>
																	</a> </span>
																	<% } %>
																	<br />
																	<span id="<%=i%>targetStatusDiv<%=goalCount %>"></span>
																</div></td>
															<% } else { %>
															<td>
																<div id="<%=i%>spanid<%=goalCount %>"
																	style="padding-left: 10px;">
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
																										String t=""+uF.parseToDouble(target);
																										String days="0";
																										String hours="0";
																										if(t.contains(".")) {
																											t=t.replace(".","_");
																											String[] temp=t.split("_");
																											days=temp[0];
																											hours=temp[1];
																										}
																									%>
																	&nbsp;<label style="width: 20px; text-align: right;"><%=days %></label>&nbsp;Days&nbsp;
																	<label style="width: 20px; text-align: right;"><%=hours %></label>&nbsp;Hrs

																	<% } else { %>
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																	<%=uF.showData(strCurrency,"")%>&nbsp;
																	<% } %>
																	<label style="width: 65px; text-align: right;"><%=target!=null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(target)) : "0" %></label>
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																	&nbsp;%
																	<% } %>
																	<% } %>

																	<br />
																	<span id="<%=i%>targetStatusDiv<%=goalCount %>"></span>
																</div></td>
															<% } %>
														</tr>
														<%if(hmUpdateBy.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId)!=null){ %>
														<tr>
															<td colspan="2"><a href="javascript:void(0)"
																onclick="getMemberData('<%=strSessionEmpId%>', '<%=goalid%>', '<%=goalFreqId %>', 'status','<%=assignedTarget %>','<%=measureType %>');"
																style="font-size: 10px;">Last updated by <%=hmUpdateBy.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId)!=null ? hmUpdateBy.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId) : "" %></a>
															</td>
														</tr>
														<%} else { %>
														<tr>
															<td colspan="2">Not updated yet.</td>
														</tr>
														<%} %>
													</table>
												</div>
											</div>
											<div class="col-lg-3 col-md-3" style="padding: 0px;">
												<div style="float: left; margin: 7px 0px 0px 16px;">
													<div
														id="starPrimaryGTarget<%=strSessionEmpId%>_<%=goalid %>_<%=goalFreqId %>"></div>
													<% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
													<div>
														<a href="javascript:void(0);"
															onclick="viewManagerAndHRComments('<%=strSessionEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'TARGET')">Comments
															<%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
													</div>
													<% } %>
													<script type="text/javascript">
					                                                                $('#starPrimaryGTarget<%=strSessionEmpId%>_<%=goalid %>_<%=goalFreqId %>').raty({
					                                                                	readOnly: true,
					                                                                	start: <%=avgRating %>,
					                                                                	half: true,
					                                                                	targetType: 'number'
					                                                                });
					                                                            </script>
												</div>
											</div>
										</div>
										<div class="row row_without_margin">
											<div class="col-lg-12 col-md-12">
												<div
													id="TargetRatingDiv_<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
													style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
													<input type="hidden"
														name="hideGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
														id="hideGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>">
													<div
														id="starGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
														style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
													<br />
													<div style="float: left; margin: 0px 0px 5px 0px;">
														<textarea rows="1" cols="40"
															name="strTargetComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
															id="strTargetComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"></textarea>
													</div>
													<div style="float: left; margin: 0px 0px 5px 7px;">
														<a href="javascript:void(0);"
															onclick="updateKRATaskRatingAndComment('<%=strSessionEmpId %>','<%=goalid%>', '<%=goalFreqId%>', '0', '0', 'TARGET', 'Myself')"><input
															type="button" class="btn btn-primary" name="update"
															style="padding-bottom: 3px; padding-top: 3px;"
															value="Update">
														</a>
													</div>
													<script type="text/javascript">
					                                                                $('#starGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>').raty({
					                                                                	readOnly: false,
					                                                                	start: 0,
					                                                                	half: true,
					                                                                	targetType: 'number',
					                                                                	click: function(score, evt) {
					                                                                			$('#hideGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>').val(score);
					                                                                	}
					                                                                });
					                                                            </script>
												</div>
											</div>
										</div>
									</div></li>
							</ul>
							<% } else { %>
							<%
			                                                String goalStatusPercent = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_STATUS");
			                                                String managerRating = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
			                                                String hrRating = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
			                                                String managerComment = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
			                                                String hrComment = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
			                                                
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
			                                                if(hmEmpwiseGoalAndTargetEmpRating != null) {
			                                                	String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
			                                                	strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
			                                                	double avgUserRating = 0;
			                                                	if(uF.parseToInt(strUserCnt) > 0) {
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
							<ul style="float: left; width: 98%; padding-left: 0px;">
								<li
									style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px; margin-top: 10px;">
									<div style="float: left; width: 100%;">
										<div
											style="float: left; margin: 20px 5px 3px 55px; line-height: 15px; width: 25%;">No
											Measure or KRA.</div>
										<div
											id="GoalProBarDiv_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
											style="float: left; margin: 3px 25px 3px 25px; width: 15%;">
											<div class="anaAttrib1">
												<span
													style="margin-left:<%=uF.parseToDouble(goalStatusPercent) > 95 ? uF.parseToDouble(goalStatusPercent) - 10 : uF.parseToDouble(goalStatusPercent) - 4%>%;"><%=uF.showData(goalStatusPercent, "0")%>%</span>
											</div>
											<div id="outbox">
												<% if (uF.parseToDouble(goalStatusPercent) < 33.33) { %>
												<div id="redbox"
													style="width: <%=uF.showData(goalStatusPercent, "0") %>%;"></div>
												<% } else if (uF.parseToDouble(goalStatusPercent) >= 33.33 && uF.parseToDouble(goalStatusPercent) < 66.67) { %>
												<div id="yellowbox"
													style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
												<% } else if (uF.parseToDouble(goalStatusPercent) >= 66.67) { %>
												<div id="greenbox"
													style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
												<% } %>
											</div>
											<div class="anaAttrib1" style="float: left; width: 100%;">
												<span style="float: left; margin-left: -4%;">0%</span> <span
													style="float: right; margin-right: -10%;">100%</span>
											</div>
										</div>
										<% if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>
										<div
											style="float: left; margin: 20px 5px 3px 15px; width: 15%;">
											<input type="text"
												name="completedPercent_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
												id="completedPercent_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
												value="<%=uF.showData(goalStatusPercent, "") %>"
												style="width: 40px !important;"
												onkeypress="return isNumberKey(event)" /> <a
												onclick="updateCompletedPercent('<%=strSessionEmpId %>','<%=goalid %>', '<%=goalFreqId %>', '0' ,'0', 'GOAL');"
												href="javascript:void(0);" title="Update"><i
												class="fa fa-pencil-square-o" aria-hidden="true"></i>
											</a> <br />
											<span
												id="completedPercentStatusSpan_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"></span>
										</div>
										<% } else { %>
										<div
											style="float: left; margin: 20px 5px 3px 15px; width: 15%;">
											<label style="width: 40px;"><%=uF.showData(goalStatusPercent, "") %></label>
											<br />
											<span
												id="completedPercentStatusSpan_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"></span>
										</div>
										<% } %>
										<div
											id="GivenGoalRatingDiv_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
											style="float: left;">
											<div
												id="starPrimaryGoal<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
												style="float: left; margin: 20px 0px 0px 0px;"></div>
											<% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
											<div style="margin-left: 25px;">
												<a href="javascript:void(0);"
													onclick="viewManagerAndHRComments('<%=strSessionEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'TARGET')">Comments
													<%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
											</div>
											<% } %>
										</div>
										<div
											id="GoalRatingDiv_<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
											style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
											<input type="hidden"
												name="hideGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
												id="hideGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>">
											<div
												id="starGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
												style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
											<br />
											<div style="float: left; margin: 0px 0px 5px 0px;">
												<textarea rows="1" cols="40"
													name="strGoalComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
													id="strGoalComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"></textarea>
											</div>
											<div style="float: left; margin: 0px 0px 5px 7px;">
												<a href="javascript:void(0);"
													onclick="updateKRATaskRatingAndComment('<%=strSessionEmpId %>', '<%=goalid%>', '<%=goalFreqId%>', '0', '0', 'GOAL', 'Myself')"><input
													type="button" class="btn btn-primary" name="update"
													style="padding-top: 3px; padding-bottom: 3px;"
													value="Update">
												</a>
											</div>
										</div>
										<script type="text/javascript">
			                                                            $('#starPrimaryGoal<%=strSessionEmpId%>_<%=goalid %>_<%=goalFreqId %>').raty({
			                                                            	readOnly: true,
			                                                            	start: <%=avgRating %>,
			                                                            	half: true,
			                                                            	targetType: 'number'
			                                                            });
			                                                            
			                                                            	$('#starGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>').raty({
			                                                            		readOnly: false,
			                                                            		start: 0,
			                                                            		half: true,
			                                                            		targetType: 'number',
			                                                            		click: function(score, evt) {
			                                                            			$('#hideGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>').val(score);
			                                                            }
			                                                            });
			                                                        </script>
									</div></li>
							</ul>
							<% } %>
						</div>
					<!--  </div>-->
					<!-- /.box-body -->
				</div>
				</div>
				</div></div>
			
				
				<%} %> <% } %> <% } %> <% } %> <% if((hmGoalKraEmpwise == null || hmGoalKraEmpwise.isEmpty()) ) { %>
				<div class="nodata msg" style="margin-top: 0px; width: 96%;">No
					data available.</div>
				<% } %>


				<h3 style="margin-top: 0px; font-size: 14px !important; font-weight: 600;" class="pagetitle">Goal Forms</h3> <!-- Goal, KRA, Target Forms -->
				<%
                                    i=0;
                                    for(;empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
                                    	int kraSize = 0;
                                    	hmGoalKraSuperIdwise = hmGoalKraEmpwise.get(empList.get(i));
                                    	kraSize = uF.parseToInt(hmEmpwiseKRACnt.get(empList.get(i)));
                                    
                                    	List<List<String>> empKraOuterList = hmEmpKra.get(empList.get(i));		
                                    	
                                    	String empname = hmEmpCodeName.get(empList.get(i).trim()); 
                                    	if(empList.get(i)!=null && strSessionEmpId!=null && empList.get(i).trim().equals(strSessionEmpId)) {
                                    		continue;
                                    	}
                                    	String strImage =empImageMap.get(empList.get(i).trim());
                                    	
                                    %>
				<div class="box box-primary collapsed-box"
					style="border-top-color: #E0E0E0;">
					<div class="box-header with-border" style="height: 40px;">
						<h3 class="box-title"
							style="font-size: 14px !important; color: #000000 !important; font-weight: 500 !important;">
							<img height="20" width="20" class="lazy img-circle"
								src="userImages/avatar_photo.png"
								data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+strImage%>" />
							<%=empname %>

						</h3>
						<div class="box-tools pull-right">
							<span id="empStatus<%=empList.get(i) %>"
								style="margin-right: 1cm;" class="badge bg-gray"><%=kraSize %></span>
							<button class="btn btn-box-tool" data-widget="collapse">
								<i class="fa fa-plus"></i>
							</button>
							<button class="btn btn-box-tool" data-widget="remove">
								<i class="fa fa-times"></i>
							</button>
						</div>
					</div>
					<!-- /.box-header -->
					<div class="box-body"
						style="padding: 5px; overflow-y: auto; display: none;">
						<ul>
							<% if(hmGoalKraSuperIdwise!=null && !hmGoalKraSuperIdwise.isEmpty()) { %>
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
                                                		strHr = "<hr width=\"3%\" style=\"float: left; border: 1px solid #3F82BF;\">";
                                                	}
                                                	
                                                	
                                                	
                                                	%>
							<li style="border-left: <%=strBorder %>"><%=strHr %>
								<ul style="float: left; width: 99%; padding-left: 0px;">
									<%
                                                        Iterator<String> itgKRA = hmGoalKra.keySet().iterator();
                                                        while(itgKRA.hasNext()) {
                                                         goalCount++;
                                                         String goalAndFreqId = itgKRA.next();
                                                          List<String> gInnerList = hmGoalDetails.get(goalAndFreqId);
                                                         String goalid = gInnerList.get(1);
                                                         String goalFreqId = gInnerList.get(32);
                                                        	double avgGoalRating = 0.0d;
                                                        	double dblWeightScore = 0.0d;
                                                        	if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                												String goalRating = hmEmpwiseGoalRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING");
                												String goalTaskCount = hmEmpwiseGoalRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT");
                												
                												String goalEmpRating = hmEmpwiseGoalEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING");
                												String goalEmpTaskCount = hmEmpwiseGoalEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT");
                												if(uF.parseToInt(goalTaskCount) > 0 || uF.parseToInt(goalEmpTaskCount) > 0) {
                													avgGoalRating = (uF.parseToDouble(goalRating) + uF.parseToDouble(goalEmpRating)) / (uF.parseToInt(goalTaskCount) + uF.parseToInt(goalEmpTaskCount));
                												}
                												dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                											} else {
                												double avgEmpGoalRating = 0;
                												avgGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING"));
                												if(uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT")) > 0) {
                													avgEmpGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING"))/ uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT"));
                													if(avgGoalRating > 0) {
                														avgGoalRating = (avgGoalRating + avgEmpGoalRating) / 2; 
                													} else {
                														avgGoalRating = avgEmpGoalRating;
                													}
                												}
                												
                												dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                											}
                                                        	
                                                        	String strRole = "-";
                											if(gInnerList.get(28) != null) {
                												List<String> al = Arrays.asList(gInnerList.get(28).split(","));
                												if(al.contains(strSessionEmpId)) {
                													strRole = "Peer";
                												}
                											}
                											
                											if(gInnerList.get(31) != null) {
                												List<String> al = Arrays.asList(gInnerList.get(31).split(","));
                												if(al.contains(strSessionEmpId)) {
                													strRole = "Anyone";
                												}
                											}
                                                        	
                                                        %>
									<li
										style="float: left; width: 96%; margin: 10px 0px 10px 0px; padding: 5px; border: 1px solid #CCCCCC;">
										<div style="float: left; width: 100%;">
											<div style="float: left; width: 100%;">
												<div style="float: left; width: 100%;">
													<div class="<%=gInnerList.get(10) %>"
														style="float: left; line-height: 18px;">
														<b>Goal: </b><%=gInnerList.get(3) %>
														<%=(gInnerList.get(33) != null && !gInnerList.get(33).equals("")) ? "["+gInnerList.get(33)+"]" : "" %>
													</div>
													<div style="float: left; margin-left: 2%;">
														[Role- <b><%=strRole %></b>]
													</div>
													<div style="float: right; width: 50%;">
														<div
															id="starPrimaryG<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=goalCount %>"
															style="float: left;"></div>
														<div style="float: left;">
															<b>Rated Score:</b>
															<%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(dblWeightScore+"")) %>/<%=uF.parseToDouble(gInnerList.get(16)) %></div>

														<div
															style="float: right; margin: 3px 0px 1px 0px; width: 100%; padding-bottom: 3px; padding-right: 3px;">
															<%
																				List<String> goalAchieved = hmActualAchievedGoal.get(empList.get(i)+"_"+goalid+"_"+goalFreqId);
																				 if(goalAchieved == null) goalAchieved = new ArrayList<String>();
																			%>
															<table cellspacing="0" cellpadding="2"
																class="table table-bordered">
																<tr>
																	<th colspan="4" style="padding: 0px;">Finalisation
																		Score</th>
																</tr>
																<tr>
																	<th style="width: 70px; padding: 0px;">Weightage</th>
																	<th style="width: 100px; padding: 0px;">Actual
																		Achieved</th>
																	<th style="width: 105px; padding: 0px;">Achieved
																		Share</th>
																</tr>
																<tr>
																	<td><%=uF.parseToDouble(gInnerList.get(16))%></td>
																	<td><%=uF.showData(goalAchieved.size()>0 ? goalAchieved.get(5) : "-", "-")%>
																	</td>
																	<td><%=uF.showData(goalAchieved.size()>0 ? goalAchieved.get(4) : "-", "-")%>
																	</td>
																</tr>
															</table>
														</div>
													</div>
												</div>
												<div style="float: left; width: 100%;">
													<span class="<%=gInnerList.get(10) %>"
														style="float: left; margin-left: 15px; font-size: 10px; line-height: 18px;"><b>Obj:
													</b><%=uF.showData(gInnerList.get(13), "-") %></span>
												</div>
												<div
													style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC;">
													<span class="<%=gInnerList.get(10) %>"
														style="float: left; margin-left: 15px; font-size: 10px; line-height: 18px;">-
														assigned by <%=gInnerList.get(4)%>, attribute <%=gInnerList.get(14) %>,
														effective date <%=gInnerList.get(15) %>, due date <%=gInnerList.get(5)%>
													</span>
												</div>
											</div>
										</div> <%
                                                            if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                                            List<List<String>> goalOuterList = hmGoalKra.get(goalAndFreqId);
                                                             for(int j=0; goalOuterList!=null && !goalOuterList.isEmpty() && j<goalOuterList.size(); j++) {
                                                             List<String> innerList=goalOuterList.get(j);
                                                             List<List<String>> taskOuterList = hmKRATasks.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11));
                                                             	double avgKRARating = 0.0d;
                                                            	double dblKRAWeightScore = 0.0d;
                                                             	String kraRating = hmEmpwiseKRARating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                                            	String kraTaskCount = hmEmpwiseKRARating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                                            	
                                                            	if(uF.parseToInt(kraTaskCount) > 0) {
                                                            		avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
                                                            	}
                                                            	
                                                            	String strUserCnt = "";
                                                            	if(hmKRATaskEmpRating != null && !hmKRATaskEmpRating.isEmpty()) {
                                                            		String strUserRating = hmEmpwiseKRAEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                                            		strUserCnt = hmEmpwiseKRAEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                                            		double avgUserRating = 0;
                                                            		if(uF.parseToInt(strUserCnt) > 0) {
                                                            			avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                                                            		}
                                                            		if(avgKRARating > 0 && avgUserRating > 0) {
                                                            			avgKRARating = (avgKRARating + avgUserRating) / 2;
                                                            		} else if(avgUserRating > 0) {
                                                            			avgKRARating = avgUserRating;
                                                            		}
                                                            	}
                                                            	dblKRAWeightScore = (avgKRARating * uF.parseToDouble(innerList.get(27))) / 5;
                                                            	
                                                            %>
										<ul style="float: left; margin: 0px 10px; width: 98%;">
											<li
												style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px;">
												<div style="float: left; width: 100%;">
													<div
														style="float: left; margin: 0px 0px 0px 15px; width: 50%;">
														<span class="<%=innerList.get(10) %>"
															style="margin: 0px 0px 0px 15px; float: left;"><strong>KRA:</strong>&nbsp;<%=uF.showData(innerList.get(2), "-") %>
															<span style="float: left; width: 100%;"> <% if(innerList.get(11) != null && hmCheckKWithAllowance != null && uF.parseToBoolean(hmCheckKWithAllowance.get(innerList.get(11)))) { %>
																<span style="font-style: italic;"><a
																	href="javascript:void(0);"
																	style="font-size: 10px; font-weight: normal;"
																	onclick="viewPerformanceAllowanceData('<%=innerList.get(11) %>', 'K');">This
																		KRA has Performance Incentives attached</a>
															</span> <% } else { %> <span
																style="font-size: 10px; font-style: italic; color: gray;">This
																	KRA has no Performance Incentives attached</span> <% } %> </span> </span>
													</div>
													<div style="float: right; width: 42%;">
														<div
															id="starPrimaryGK<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11)%>_<%=j%>"
															style="float: left;"></div>
														<div style="float: left;">
															<b>Rated Score:</b>
															<%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(dblKRAWeightScore+"")) %>/<%=uF.parseToDouble(innerList.get(27)) %></div>
													</div>
												</div> <% if(taskOuterList!=null && !taskOuterList.isEmpty()) { %>
												<div style="float: left; width: 100%;">
													<div style="float: left; width: 100%; line-height: 12px;">
														<span
															style="font-weight: bold; margin-left: 30px; color: gray;">Tasks:</span>
													</div>
													<% for(int a=0; a<taskOuterList.size(); a++) {
                                                                        List<String> taskInnerList = taskOuterList.get(a);
                                                                        String taskStatusPercent = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_STATUS");
                                                                        String managerRating = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_RATING");
                                                                        String hrRating = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_RATING");
                                                                        String managerComment = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_COMMENT");
                                                                        String hrComment = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_COMMENT");
                                                                        
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
                                                                        if(hmKRATaskEmpRating != null) {
                                                                        	String strUserRating = hmKRATaskEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_RATING");
                                                                        	strUserTaskCnt = hmKRATaskEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_COUNT");
                                                                        	double avgUserRating = 0;
                                                                        	if(uF.parseToInt(strUserCnt) > 0) {
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

														<div
															style="float: left; margin: 20px 5px 3px 55px; line-height: 15px; width: 16%;"><%=uF.showData(taskInnerList.get(1), "-") %></div>
														<div
															id="KTProBarDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
															style="float: left; margin: 3px 25px 3px 25px; width: 20%;">
															<div class="anaAttrib1">
																<span style="margin-left:<%=uF.parseToDouble(taskStatusPercent) > 95 ? uF.parseToDouble(taskStatusPercent) - 10 : uF.parseToDouble(taskStatusPercent) - 4 %>%;"><%=uF.showData(taskStatusPercent, "0")%>%</span>
															</div>
															<div id="outbox">
																<% if (uF.parseToDouble(taskStatusPercent) < 33.33) { %>
																<div id="redbox" style="width: <%=uF.showData(taskStatusPercent, "0") %>%;"></div>
																<% } else if (uF.parseToDouble(taskStatusPercent) >= 33.33 && uF.parseToDouble(taskStatusPercent) < 66.67) { %>
																<div id="yellowbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
																<% } else if (uF.parseToDouble(taskStatusPercent) >= 66.67) { %>
																<div id="greenbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
																<% } %>
															</div>
															<div class="anaAttrib1" style="float: left; width: 100%;">
																<span style="float: left; margin-left: -4%;">0%</span> <span style="float: right; margin-right: -10%;">100%</span>
															</div>
														</div>
														<div style="float: left; margin: 20px 5px 3px 5px; width: 7%;">
															<%=uF.showData(taskStatusPercent, "0") %>%
														</div>
														<div
															id="GivenTaskRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
															style="float: left;">
															<div
																id="starPrimaryGKT<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																style="float: left; margin: 20px 0px 0px 25px;"></div>
															<% if(commentCnt > 0 || uF.parseToInt(strUserTaskCnt) > 0) { %>
															<div style="margin-left: 25px;">
																<a href="javascript:void(0);"
																	onclick="viewManagerAndHRComments('<%=empList.get(i) %>', '<%=goalid%>', '<%=goalFreqId%>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA')">Comments
																	<%=(commentCnt + uF.parseToInt(strUserTaskCnt)) %></a>
															</div>
															<% } %>
															<script type="text/javascript">
                                                                                $('#starPrimaryGKT<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
                                                                                	readOnly: true,
                                                                                	start: <%=avgRating %>,
                                                                                	half: true,
                                                                                	targetType: 'number'
                                                                                });
                                                                            </script>
														</div>
														<% if((uF.parseToInt(hmHrIds.get(empList.get(i))) == uF.parseToInt(strSessionEmpId) && strSessionUserType != null && strSessionUserType.equals(IConstants.HRMANAGER)) || (strSessionUserType != null && !strSessionUserType.equals(IConstants.HRMANAGER))) { %>
														<div
															id="TaskRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
															style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
															<input type="hidden"
																name="hideGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																id="hideGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>">
															<div
																id="starGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
															<br />
															<div style="float: left; margin: 0px 0px 5px 0px;">
																<textarea rows="1" cols="40"
																	name="strComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																	id="strComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																	class="form-control autoWidth"></textarea>
															</div>
															<div style="float: left; margin: 0px 0px 5px 7px;">
																<a href="javascript:void(0);"
																	onclick="updateKRATaskRatingAndComment('<%=empList.get(i) %>', '<%=goalid%>', '<%=goalFreqId%>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA','<%=strRole %>')"><input
																	type="button" class="btn btn-primary" name="update"
																	style="padding-bottom: 3px; padding-top: 3px;"
																	value="Update">
																</a>
															</div>
															<script type="text/javascript">
	                                                                                $('#starGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
	                                                                                	readOnly: false,
	                                                                                	start: 0,
	                                                                                	half: true,
	                                                                                	targetType: 'number',
	                                                                                	click: function(score, evt) {
	                                                                                			$('#hideGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').val(score);
	                                                                                }
	                                                                                });
	                                                                            </script>
														</div>
														<% } %>
													</div>
													<% } %>
												</div> <% } %>
											</li>
										</ul> <% } %> <% } else if(gInnerList.get(20) != null && gInnerList.get(20).equals("Measure")) {
                                                            String target=hmTargetValue.get(empList.get(i)+"_"+goalid+"_"+goalFreqId);
                                                            String targetID = hmTargetID.get(empList.get(i)+"_"+goalid+"_"+goalFreqId);
                                                            String targetRemark = hmTargetRemark.get(empList.get(i)+"_"+goalid+"_"+goalFreqId);
                                                            String assignedTarget = "", measureType="";
                                                            
                                                            String managerRating = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
                                                            String hrRating = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
                                                            String managerComment = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
                                                            String hrComment = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
                                                            
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
                                                            if(hmEmpwiseGoalAndTargetEmpRating != null) {
                												String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING");
                												strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT");
                												double avgUserRating = 0;
                												if(uF.parseToInt(strUserCnt) > 0) {
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
										<ul style="padding-left: 0px;">
											<li
												style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px;">
												<div
													style="float: left; width: 100%; margin: 0px 0px 0px 15px">
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
													<div class="row row_without_margin">
														<div class="col-lg-3 col-md-3">
															<div id="<%=i %>ProBarDiv<%=goalCount %>">
																<!-- <div style="width: 100%;"> -->
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
														</div>
														<div class="col-lg-5 col-md-5">
															<div>
																<table class="table table-bordered"
																	style="margin-top: 10px;">
																	<tr>
																		<td><strong><u>Target</u>
																		</strong>
																		</td>
																		<td style="padding-left: 5px;"><strong><u>Actual</u>
																		</strong>
																		</td>
																	</tr>
																	<tr>
																		<td nowrap="nowrap">
																			<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
																								measureType= "effort";
																								assignedTarget = gInnerList.get(21);
																								%> <%=gInnerList.get(21) %> <% } else {
																									measureType= "amount";
																									assignedTarget = gInnerList.get(21);
																								%> <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																			<%=uF.showData(strCurrency,"")%>&nbsp; <% } %> <%=""+uF.parseToDouble(gInnerList.get(21))%>
																			<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																			&nbsp;% <% } %> <% } %>
																		</td>
																		<td style="padding-left: 5px;">
																			<div id="<%=i%>spanid<%=goalCount %>"
																				style="float: left; text-align: center;">
																				<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
			                                                                                            String t=""+uF.parseToDouble(target);
			                                                                                            String days="0";
			                                                                                            String hours="0";
			                                                                                            if(t.contains(".")) {
			                                                                                            	t=t.replace(".","_");
			                                                                                            	String[] temp=t.split("_");
			                                                                                            	days=temp[0];
			                                                                                            	hours=temp[1];
			                                                                                            }
			                                                                                            if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)) {
			                                                                                            %>
																				<%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
																				<% } else { %>
																				<%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
																				<% }
			                                                                                      } else { 
		                                                                                            	if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)) {
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
																				<%=target!=null ? uF.getAmountInCrAndLksFormat(uF.parseToDouble(target)) : "0" %>
																				<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																				&nbsp;%
																				<% } %>
																				<% }
		                                                                                            } %>
																			</div></td>
																	</tr>
																</table>
															</div>
														</div>
														<div class="col-lg-4 col-md-4">
															<div>
																<div
																	id="starPrimaryGTarget<%=empList.get(i)%>_<%=goalid%>_<%=goalFreqId%>"></div>
																<% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
																<div>
																	<a href="javascript:void(0);"
																		onclick="viewManagerAndHRComments('<%=empList.get(i) %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'TARGET')">Comments
																		<%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
																</div>
																<% } %>
																<script type="text/javascript">
		                                                                            $('#starPrimaryGTarget<%=empList.get(i)%>_<%=goalid%>_<%=goalFreqId%>').raty({
		                                                                            	readOnly: true,
		                                                                            	start: <%=avgRating %>,
		                                                                            	half: true,
		                                                                            	targetType: 'number'
		                                                                            });
		                                                                        </script>
															</div>
														</div>
													</div>
													<% if((uF.parseToInt(hmHrIds.get(empList.get(i))) == uF.parseToInt(strSessionEmpId) && strSessionUserType != null && strSessionUserType.equals(IConstants.HRMANAGER)) || (strSessionUserType != null && !strSessionUserType.equals(IConstants.HRMANAGER))) { %>
													<div
														id="TargetRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
														style="margin-top: 10px;display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
														<input type="hidden"
															name="hideGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															id="hideGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>">
														<div
															id="starGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
														<br />
														<div style="float: left; margin: 0px 0px 5px 0px;">
															<textarea rows="1" cols="40"
																name="strTargetComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
																id="strTargetComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
																class="form-control autoWidth"></textarea>
														</div>
														<div style="float: left; margin: 0px 0px 5px 7px;">
															<a href="javascript:void(0);"
																onclick="updateKRATaskRatingAndComment('<%=empList.get(i) %>','<%=goalid%>', '<%=goalFreqId%>', '0', '0', 'TARGET', '<%=strRole %>')"><input
																type="button" class="btn btn-primary" name="update"
																style="padding-bottom: 3px; padding-top: 3px;"
																value="Update">
															</a>
														</div>
														<script type="text/javascript">
	                                                                            $('#starGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>').raty({
	                                                                            	readOnly: false,
	                                                                            	start: 0,
	                                                                            	half: true,
	                                                                            	targetType: 'number',
	                                                                            	click: function(score, evt) {
	                                                                            			$('#hideGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>').val(score);
	                                                                            }
	                                                                            });
	                                                                        </script>
													</div>
													<% } %>
												</div>
												<div style="float: left; width: 100%;">
													<% if(uF.parseToBoolean(hmCheckGTWithAllowance.get(gInnerList.get(1)))) { %>
													<span style="font-style: italic;"><a
														href="javascript:void(0);"
														style="font-size: 10px; font-weight: normal;"
														onclick="viewPerformanceAllowanceData('<%=gInnerList.get(1) %>', 'GT');">This
															Target has Performance Incentives attached</a>
													</span>
													<% } else { %>
													<span
														style="font-size: 10px; font-style: italic; color: gray;">This
														Target has no Performance Incentives attached</span>
													<% } %>
												</div></li>
										</ul> <% } else { %> <%
                                                            String goalStatusPercent = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_STATUS");
                                                            String managerRating = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
                                                            String hrRating = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
                                                            String managerComment = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
                                                            String hrComment = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
                                                            
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
                                                            if(hmEmpwiseGoalAndTargetEmpRating != null) {
                                                            	String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING");
                                                            	strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                                            	double avgUserRating = 0;
                                                            	if(uF.parseToInt(strUserCnt) > 0) {
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
											<li
												style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px;">
												<div style="float: left; width: 100%;">
													<div
														style="float: left; margin: 20px 0px 3px 0px; line-height: 15px; width: 21.5%;">
														No Measure or KRA. <br />
														<br />
														<% if(hmCheckGTWithAllowance != null && gInnerList.get(1) != null && uF.parseToBoolean(hmCheckGTWithAllowance.get(gInnerList.get(1)))) { %>
														<span style="font-style: italic;"><a
															href="javascript:void(0);"
															style="font-size: 10px; font-weight: normal;"
															onclick="viewPerformanceAllowanceData('<%=gInnerList.get(1) %>', 'GT');">This
																Goal has Performance Incentives attached</a>
														</span>
														<% } else { %>
														<span
															style="font-size: 10px; font-style: italic; color: gray;">This
															Goal has no Performance Incentives attached</span>
														<% } %>
													</div>
													<div
														id="GoalProBarDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
														style="float: left; margin: 3px 25px 3px 25px; width: 10%;">
														<div class="anaAttrib1">
															<span
																style="margin-left:<%=uF.parseToDouble(goalStatusPercent) > 95 ? uF.parseToDouble(goalStatusPercent) - 10 : uF.parseToDouble(goalStatusPercent) - 4%>%;"><%=uF.showData(goalStatusPercent, "0")%>%</span>
														</div>
														<div id="outbox">
															<% if (uF.parseToDouble(goalStatusPercent) < 33.33) { %>
															<div id="redbox"
																style="width: <%=uF.showData(goalStatusPercent, "0") %>%;"></div>
															<% } else if (uF.parseToDouble(goalStatusPercent) >= 33.33 && uF.parseToDouble(goalStatusPercent) < 66.67) { %>
															<div id="yellowbox"
																style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
															<% } else if (uF.parseToDouble(goalStatusPercent) >= 66.67) { %>
															<div id="greenbox"
																style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
															<% } %>
														</div>
														<div class="anaAttrib1" style="float: left; width: 100%;">
															<span style="float: left; margin-left: -4%;">0%</span> <span
																style="float: right; margin-right: -10%;">100%</span>
														</div>
													</div>

													<div
														style="float: left; margin: 20px 5px 3px 15px; width: 7%;">
														<%=uF.showData(goalStatusPercent, "") %>% <input
															type="text"
															name="completedPercent_<%=empList.get(i) %>_<%=gInnerList.get(1) %>"
															id="completedPercent_<%=empList.get(i) %>_<%=gInnerList.get(1) %>"
															value="<%=uF.showData(goalStatusPercent, "") %>"
															style="width: 40px !important;"
															onkeypress="return isNumberKey(event)" /> <a
															onclick="updateCompletedPercent('<%=empList.get(i) %>','<%=gInnerList.get(1) %>','0', 'GOAL');"
															href="javascript:void(0);" title="Update"><i
															class="fa fa-pencil-square-o" aria-hidden="true"></i>
														</a> <br />
														<span
															id="completedPercentStatusSpan_<%=empList.get(i) %>_<%=gInnerList.get(1) %>"></span>
													</div>
													<div
														id="GivenGoalRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
														style="float: left;">
														<div
															id="starPrimaryGoal<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															style="float: left; margin: 20px 0px 0px 25px;"></div>
														<% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
														<div style="margin-left: 25px;">
															<a href="javascript:void(0);"
																onclick="viewManagerAndHRComments('<%=empList.get(i) %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'GOAL')">Comments
																<%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
														</div>
														<% } %>
														<script type="text/javascript">
                                                                            $('#starPrimaryGoal<%=empList.get(i) %>_<%=goalid %>_<%=goalFreqId %>').raty({
                                                                            	readOnly: true,
                                                                            	start: <%=avgRating %>,
                                                                            	half: true,
                                                                            	targetType: 'number'
                                                                            });
                                                                        </script>
													</div>
													<% if((uF.parseToInt(hmHrIds.get(empList.get(i))) == uF.parseToInt(strSessionEmpId) && strSessionUserType != null && strSessionUserType.equals(IConstants.HRMANAGER)) || (strSessionUserType != null && !strSessionUserType.equals(IConstants.HRMANAGER))) { %>
													<div
														id="GoalRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
														style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
														<input type="hidden"
															name="hideGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															id="hideGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>">
														<div
															id="starGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
														<br />
														<div style="float: left; margin: 0px 0px 5px 0px;">
															<textarea rows="1" cols="40"
																name="strGoalComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
																id="strGoalComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
																class="form-control autoWidth"></textarea>
														</div>
														<div style="float: left; margin: 0px 0px 5px 7px;">
															<a href="javascript:void(0);"
																onclick="updateKRATaskRatingAndComment('<%=empList.get(i) %>', '<%=goalid%>', '<%=goalFreqId%>', '0', '0', 'GOAL', '<%=strRole %>')"><input
																type="button" class="btn btn-primary" name="update"
																style="padding-bottom: 3px; padding-top: 3px;"
																value="Update">
															</a>
														</div>
														<script type="text/javascript">
                                                                            $('#starGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>').raty({
                                                                            	readOnly: false,
                                                                            	start: 0,
                                                                            	half: true,
                                                                            	targetType: 'number',
                                                                            	click: function(score, evt) {
                                                                            		$('#hideGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>').val(score);
                                                                            }
                                                                            });
                                                                        </script>
													</div>
													<% } %>
												</div></li>
										</ul> <% }  %>
									</li>
									<% } %>
								</ul> <%=strHr %></li>
							<%} %>
							<%} %>
						</ul>
						<%if((hmGoalKraSuperIdwise == null || hmGoalKraSuperIdwise.isEmpty()) && (empKraOuterList == null || empKraOuterList.isEmpty())) { %>
						<div class="nodata"
							style="border-radius: 4px 4px 4px 4px; padding: 10px; width: 95%; margin: 5px 5px 5px 5px">
							No Goal, KRA, Target assigned to
							<%=empname %>.
						</div>
						<% } %>
					</div>
					<!-- /.box-body -->
				</div>
			
				<% } %> </section>
				<% } %>
				

				<div class="custom-legends">
					<div class="custom-legend pullout">
						<div class="legend-info">Waiting for workflow(Workflow)</div>
					</div>
					<div class="custom-legend pending">
						<div class="legend-info">Not filled yet(Myself)</div>
					</div>
					<div class="custom-legend approved">
						<div class="legend-info">Completed(Myself)</div>
					</div>
					<div class="custom-legend denied">
						<div class="legend-info">Expired</div>
					</div>
					<div class="custom-legend re_submit">
						<div class="legend-info">Waiting for completion(Workflow)</div>
					</div>
					<br />
					<div class="custom-legend no-borderleft-for-legend">
						<div class="legend-info high">High Priority</div>
					</div>
					<div class="custom-legend no-borderleft-for-legend">
						<div class="legend-info medium">Medium Priority</div>
					</div>
					<div class="custom-legend no-borderleft-for-legend">
						<div class="legend-info low">Low Priority</div>
					</div>
				</div>
			</div>
			</section>
		</div>
	</div>
	</section>
</div>
</section>
<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">View Information</h4>
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

<script>
    $(document).ready(function(){
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
    	
    });
    
</script>