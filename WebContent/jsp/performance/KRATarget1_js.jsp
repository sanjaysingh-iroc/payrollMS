<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

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




<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

</body>
</html>