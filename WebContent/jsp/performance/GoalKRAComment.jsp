<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<% 
	
	
	UtilityFunctions uF=new UtilityFunctions();
	String strBaseUserType = (String) request.getAttribute("strBaseUserType"); 
	String strEmpId = (String) request.getAttribute("empId"); 
	String goalid = (String) request.getAttribute("goalId");
	String goalId = (String) request.getAttribute("goalid");

	String goalFreqId = (String) request.getAttribute("goalFreqId");
	String hrComment = (String) request.getAttribute("hrComment");
	String feedbackUserType = (String) request.getAttribute("feedbackUserType");
	String currUserType = (String) request.getAttribute("currUserType");
	String dataType = (String) request.getAttribute("dataType");
	String kraId = (String) request.getAttribute("kraId");
	String kraTaskId = (String) request.getAttribute("kraTaskId");
    Map<String, String> hmTargetRatingAndComment = (Map<String, String>) request.getAttribute("hmTargetRatingAndComment");
    if(hmTargetRatingAndComment == null) hmTargetRatingAndComment = new HashMap<String, String>();
    
	  String goalStatusPercent = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_STATUS");
      String managerRating = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
      String hrRating = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
      String managerComment = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
      //String hrComment = hmTargetRatingAndComment.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
     	System.out.println("strEmpId "+strEmpId+"goalid "+goalid+"goalFreqId "+goalFreqId+"hrComment "+hrComment+"feedbackUserType "+feedbackUserType+"dataType "+dataType+"kraId "+kraId+"kraTaskId "+kraTaskId+"currUserType "+currUserType);
	  String hrMngrReview = "block";
      if(uF.parseToDouble(goalStatusPercent) >=100 || uF.parseToInt(kraId) == 1) {
      	hrMngrReview = "block";
      }
      String pageFrom = (String) request.getAttribute("pageFrom");
  		System.out.println("pageFrom11:"+pageFrom);
%>
 <script type="text/javascript">
		          $(function() {
		          	$('#starGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=kraId %>_<%=kraTaskId %>').raty({
		            readOnly: false,
		             start: 0,
		           	half: true,
		            targetType: 'number',
		           click: function(score, evt) {
		          $('#hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=kraId %>_<%=kraTaskId %>').val(score);
		         }
		        });
		      });
 </script>
		 <div id="TaskRatingDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=kraId %>_<%=kraTaskId %>" style="float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
			<input type="hidden" name="hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=kraId%>_<%=kraTaskId %>"
                       id="hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=kraId %>_<%=kraTaskId %>">
          	<div style="float: left; margin: 5px 0px 0px 5px; width: 110px;"  id="starGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=kraId %>_<%=kraTaskId %>" >
          	</div>
          	</br>
           <div style="float: left; margin: 0px 0px 5px 0px;">
                 <textarea rows="1" cols="40" name="strComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=kraId %>_<%=kraTaskId %>" id="strComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=kraId %>_<%=kraTaskId %>"></textarea>
          </div>
             <div style="float: left; margin: 0px 0px 5px 7px;">
            
                 <a href="javascript:void(0);" onclick="updateKRATaskRatingAndComment('<%=strEmpId %>','<%=goalid %>', '<%=goalFreqId %>', '<%=kraId %>','<%=kraTaskId %>', 'KRA','Myself','<%=goalId %>'),'<%=pageFrom%>'">
                  <input type="button" class="btn btn-primary" name="update" value="Update">
                   </a>
               
            </div>
         </div>
          
             
           
     	                                		                                                          
</body>

<script type="text/javascript">
function updateKRATaskRatingAndComment(empId, goalid, goalFreqId, kraId, kraTaskId, goalType, feedbackUserType,goalId,pageFrom) {
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
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
      		$(dialogEdit).html('<div id="the_div1"><div id="ajaxLoadImage"></div></div>');
        	  var xhr = $.ajax({
        		 	 url : 'UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalId+'&goalFreqId='+goalFreqId+'&kraId='+kraId+'&kraTaskId='+kraTaskId+'&strComment='
                    		+encodeURIComponent(strComment)+'&taskRating='+taskRating+'&type=GoalKRA&operation=RC&goalType='+goalType
                    		+"&feedbackUserType="+feedbackUserType,
                    cache : false,
                    success : function(data) {
						$(dialogEdit).html(data);
                    	window.close();
                    	
                    }
                    		
                    	//if(data == "") 
                    //	}else if(data.length > 1) 
                    		//var allData = data.split("::::");
                    		//window.location = "MyHR.action";
                    		//window.location = "GoalKRATargets_1.action?callFrom=GoalKRAComments"+"&empId"+empId;
                   
                    	
                    
                     
                });
        	}
        
		}
}
 function updateKRATaskRatingAndComment1(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,feedbackUserType,dataType,currUserType) {
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
                        		getGoalKRADetails('GoalKRATarget',empId,dataType,currUserType,'GKT');
                        	}
                        }
                 });
         	}
    }
    }
 </script>
</html>