
<div id="divResult">

<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<% 
    String[] arrEnabledModules = (String[]) session.getAttribute("arrEnabledModules");
    List alEmp = (List)request.getAttribute("reportListEmp");
    UtilityFunctions uF = new UtilityFunctions();
    %>

<link href='scripts/calender/fullcalendar.min.css' rel='stylesheet' />
<link href='scripts/calender/fullcalendar.print.css' rel='stylesheet' media='print' />
 
<style type="text/css">	 
.fc-event-hover {
	position: relative !important;
	height: 17px;
}

.fc-event-hover .fc-content {
	position: absolute !important;
	top: -1px;
	left: 0;
	background: red;
	z-index: 99999;
	width: auto;
	overflow: visible !important;
	background-color: #ffff1a;
	padding: 1px;
	border-radius: 2px;
}

.fc-content-skeleton tr td:last-child .fc-event-hover .fc-content {
	left: auto;
	right: 0;
}
</style>

<%  
    String dataType = (String)request.getAttribute("dataType");
	String fromPage = (String)request.getAttribute("fromPage");
%>

<script src='scripts/calender/moment.min.js'></script>
<% if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null") || fromPage.equalsIgnoreCase("ajax")) { %>
	<script src='scripts/calender/jquery.min.js'></script>
<% }%>
<script src='https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.3.1/fullcalendar.min.js'></script>

<section class="content">
    <div class="row jscroll">           
        <section class="col-lg-12 connectedSortable">
            <div class="box box-none nav-tabs-custom">
               	<ul class="nav nav-tabs">
                    <% if(dataType == null || dataType.equals("C")) { %>
                    	<li class="active"><a href="javascript:void(0)" onclick="window.location='Calendar.action?dataType=C'" data-toggle="tab">My Calendar</a></li>
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_MEETING_ROOM + "") >= 0) { %>
							<li><a href="javascript:void(0)" onclick="window.location='Calendar.action?dataType=MRB'" data-toggle="tab">My Bookings</a></li>
	                    <% } %>
                    <% } else if(dataType != null && dataType.equals("MRB")){ %>
	                    <li><a href="javascript:void(0)" onclick="window.location='Calendar.action?dataType=C'" data-toggle="tab">My Calendar</a></li>
						<% if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_MEETING_ROOM + "") >= 0) { %>
							<li class="active"><a href="javascript:void(0)" onclick="window.location='Calendar.action?dataType=MRB'" data-toggle="tab">My Bookings</a></li>
	                    <% } %>	
                    <% } %>
                </ul>
                
            <!-- ===start parvez date: 24-02-2023=== -->    
                <div class="box-body" style="overflow-y: auto;min-height: 600px; max-height: 600px;">
           <!-- ===end parvez date: 24-02-2023=== -->     
                    <div id="printDiv" style="padding:15px 10px;">
                        <form name="frm" id="calendarForm">
                            <s:hidden name="strFrm"></s:hidden> 
                            <input type="hidden" name="dataType" id="dataType" value="<%=dataType%>"/>
                            <% if(dataType == null || dataType.equals("C")) { %>
                            
							    <div class="col-md-3" style="min-height: 600px;">
                            		<div class="box box-body">
		                            	<div class="external-event ui-draggable ui-draggable-handle brown" style="position: relative;background-color: #f56954;border-color: #f56954;color: #fff;">
		                            		<s:checkbox name="strBirthdays" id="strBirthdays" onclick="getCalendarData();"/> Birthdays
		                            	</div>
		                            	<div class="external-event bg-green ui-draggable ui-draggable-handle brown" style="position: relative;">
		                            		<s:checkbox name="strTrainings" id="strTrainings" onclick="getCalendarData();"/> My Learnings
		                            	</div>
		                            	<div class="external-event bg-yellow ui-draggable ui-draggable-handle brown" style="position: relative;">
		                            		<s:checkbox name="strInterviewsPending" id="strInterviewsPending" onclick="getCalendarData();"/> My Interviews
		                            	</div>
		                               	<div class="external-event bg-aqua ui-draggable ui-draggable-handle brown" style="position: relative;">
		                            		<s:checkbox name="strEvents" id="strEvents" onclick="getCalendarData();"/> Events
		                            	</div>
		                            	  <div class="external-event ui-draggable ui-draggable-handle brown" style="position: relative;background-color: #808080;border-color: #808080;color: #fff;">
		                            		<s:checkbox name="strVisits" id="strVisits" onclick="getCalendarData();"/> Visits
		                            	</div>
		                            	
                                		<div style="width:100%; margin:1px; font-size: 14px; margin: 5px 1px 0px;padding-top: 15px;">
		                                    <div><strong>Leaves</strong> </div>
		                                </div>
		                                <%
		                                    Map<String, String> hmLeaveColor = (Map<String, String>) request.getAttribute("hmLeaveColor");
		                                    if(hmLeaveColor == null) hmLeaveColor = new HashMap<String, String>();
		                                    Iterator<String> it = hmLeaveColor.keySet().iterator();
		                                    while(it.hasNext()){
		                                    	String strLeaveName = it.next();
		                                    	String strLeaveColor = hmLeaveColor.get(strLeaveName);
		                                    %>
				                                <div class="external-event ui-draggable ui-draggable-handle" style="position: relative;background-color: <%=uF.showData(strLeaveColor,"") %>;border-color: <%=uF.showData(strLeaveColor,"") %>;color: #fff;">
				                            		<%=uF.showData(strLeaveName,"") %>
				                            	</div>
		                               	 <% } %>
		                                <div class="external-event bg-fuchsia ui-draggable ui-draggable-handle" style="position: relative;">
		                            		 Travel
		                            	</div>
		                                
		                                <div style="width:100%; font-size: 14px; margin: 15px 1px 15px;">
		                                    <div><strong>Holiday(Current Year)</strong> </div>
		                                </div>
	                                <%
	                                    List<Map<String,String>> alHolidayList = (List<Map<String,String>>) request.getAttribute("alHolidayList");
	                                    	if(alHolidayList == null) alHolidayList = new ArrayList<Map<String,String>>();
	                                    	for(int i = 0; alHolidayList != null && i < alHolidayList.size(); i++){
	                                    		Map<String,String> hmInner = (Map<String,String>) alHolidayList.get(i); 
	                                    %>
                                
				                                <div class="external-event ui-draggable ui-draggable-handle" style="position: relative;background-color: <%=uF.showData(hmInner.get("HOLIDAY_COLOR"),"") %>;border-color: <%=uF.showData(hmInner.get("HOLIDAY_COLOR"),"") %>;color: #fff;">
				                            		<%=uF.showData(hmInner.get("HOLIDAY_NAME"),"") %>
				                            	</div>
		                                	<%} %>
		                                <%
		                                    List<Map<String,String>> alOptHolidayList = (List<Map<String,String>>) request.getAttribute("alOptHolidayList");
		                                    if(alOptHolidayList == null) alOptHolidayList = new ArrayList<Map<String,String>>();
		                                    if(alOptHolidayList.size() > 0){
		                                    %>
				                                <div style=" width:100%; font-size: 14px; margin: 15px 1px 15px;">
				                                    <div><strong>Optional Holiday(Current Year)</strong> </div>
				                                </div>
			                                <%
			                                    for(int i = 0; alOptHolidayList != null && i < alOptHolidayList.size(); i++){
			                                    	Map<String,String> hmInner = (Map<String,String>) alOptHolidayList.get(i); 
			                                    %>
                                
					                                <div class="external-event ui-draggable ui-draggable-handle" style="position: relative;background-color: <%=uF.showData(hmInner.get("HOLIDAY_COLOR"),"") %>;border-color: <%=uF.showData(hmInner.get("HOLIDAY_COLOR"),"") %>;color: #fff;">
					                            		<%=uF.showData(hmInner.get("HOLIDAY_NAME"),"") %>
					                            	</div>
                                				<%} %>
                               			 <%} %>
                               			 
		                                <div style="float:right;">
		                                    <!-- <img width="60px" src="images/icons/Outlook.jpg"> -->
		                                </div>
                            		</div>
                            	</div>
                            <div class="col-md-9" style="border-left:1px solid #efefef;min-height: 600px;">
                               <div class="box box-body">
                                	<div id="calendar" ></div>
                                </div>
                            </div>
                            <% } else if(dataType != null && dataType.equals("MRB")) { %>
                                <s:action name="MeetingRoomBooking" executeResult="true"></s:action>
                           
                            <% } %>
                        </form>
                        <%-- <% if(dataType == null || dataType.equals("C")) { %>
                             <div class="col-md-9" style="padding-left: 0px;border-left:1px solid #efefef;min-height: 600px;">
                                <div class="box box-body">
                            		<div id="calendar"></div>
                            	</div>
                            </div>	
                            <% } %>	 --%>
                    </div>
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




<script>
/* document.getElementsByClassName("tablink")[0].click();

function openCity(evt, cityName) {
  var i, x, tablinks;
  x = document.getElementsByClassName("city");
  for (i = 0; i < x.length; i++) {
    x[i].style.display = "none";
  }
  tablinks = document.getElementsByClassName("tablink");
  for (i = 0; i < x.length; i++) {
    tablinks[i].classList.remove("w3-light-grey");
  }
  document.getElementById(cityName).style.display = "block";
  evt.currentTarget.classList.add("w3-light-grey");
} */


/* function approveDenyTravel(travelId,userTypeId,empname) {
    
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
 } */
</script>

<script>
    /* function callfunction() {
    	alert("callfunction ");
    } */
    $("body").on('click','#closeButton',function(){
    	$("#modalInfo").hide();
    });
    $("body").on('click','.close',function(){
    	$("#modalInfo").hide();
    });
 	$(function(){
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
 	
 	function getCalendarData() {
 		
 		var strBirthdays = document.getElementById("strBirthdays").checked;
 		var strTrainings = document.getElementById("strTrainings").checked;
 		var strInterviewsPending = document.getElementById("strInterviewsPending").checked;
 		var strEvents = document.getElementById("strEvents").checked;
 		var strVisits = document.getElementById("strVisits").checked;
 		
 		window.location='Calendar.action?dataType=C&fromPage=ajax&strBirthdays='+strBirthdays+'&strTrainings='+strTrainings+'&strInterviewsPending='+strInterviewsPending
 			+'&strEvents='+strEvents+'&strVisits='+strVisits;
 				
 		/* var form_data = $("#calendarForm").serialize();
    	$('#divResult').html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type: 'POST',
			url : "Calendar.action?fromPage=ajax",
			data: form_data,
			success : function(data) {
				$('#divResult').html(data);
			}
		}); */
 	}
 	
 	
 	
    /* $("#calendarForm input[type='checkbox']").click(function(event){
    	var form_data = $("#calendarForm").serialize();
    	$('#divResult').html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type: 'POST',
			url : "Calendar.action?fromPage=ajax",
			data: form_data,
			success : function(data) {
				alert("data ===>> " + data);
				$('#divResult').html(data);
			}
		});
    });
     */
     
     
    function openCandidateProfilePopup(CandID,recruitId) {
    
     var id=document.getElementById("panelDiv");
    	if(id){
    		id.parentNode.removeChild(id);
    			}
    	var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('Candidate Information');
	    $("#modalInfo").show();
	    var height = $(window).height()* 0.95;
		var width = $(window).width()* 0.95;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
    	$.ajax({
		    url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId+"&form=C&callType=calendar",
		    cache : false,
		    success : function(data) {
		    $(dialogEdit).html(data);
	    	}
    	});
	}
    
    function viewEventFilePopup(eventId) {
		var dialogEdit = '#modal-body1';
			 $(dialogEdit).empty();
			 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			 $("#modalInfo1").show(); 
			 $(".modal-title1").html('View Event File ');
			 $.ajax({
				 url : 'ViewEventFilePopup.action?strEventId='+eventId,
	                cache : false,
	                success : function(data) {
	                	//alert("data==>"+data);
	                	$(dialogEdit).html(data);  		                	
	                 }
	            });
	}   
    
    function openEventPopup(eventId) {
    	 /* document.getElementById("CandiProfilePopup").innerHTML=''; */
    	 //removeLoadingDiv('the_div');
    		var id=document.getElementById("panelDiv");
    		if(id){
    			id.parentNode.removeChild(id);
    		}
    		var dialogEdit = '.modal-body';
    		$(dialogEdit).empty();
    		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$('.modal-title').html('Event Information');
    		$("#modalInfo").show();
    		$.ajax({
    			//url : "ApplyLeavePopUp.action", 
    			url :"EventPopup.action?eventId="+eventId,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    		
    	 }
    function openVisitPopup(visitId) {
   	 /* document.getElementById("CandiProfilePopup").innerHTML=''; */
   	 //removeLoadingDiv('the_div');
   		var id=document.getElementById("panelDiv");
   		if(id){
   			id.parentNode.removeChild(id);
   		}
   		var dialogEdit = '.modal-body';
   		$(dialogEdit).empty();
   		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		$('.modal-title').html('Visit Information');
   		$("#modalInfo").show();
   		$.ajax({
   			url :"VisitPopup.action?visitId="+visitId,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
   		
   	 }
   
    
    function openTrainingScheduleDayDetails(dayDesId, dayCount) {<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="My Calendar" name="title"/>
    </jsp:include> --%>

    	
    	  var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('View Training Schedule Day Details');
    	$("#modalInfo").show();
    	$.ajax({
    url : "TrainingScheduleOneDayDetails.action?dayDesId="+dayDesId+"&dayCount="+dayCount,
    cache : false,
    success : function(data) {
    	$(dialogEdit).html(data);
    }
    });
      }
    
    /* function viewCourseDetail(courseId) {
    	alert("courseId --->> "+ courseId);
    } */
    
    function viewCourseDetails(courseId, courseName) {
    	//alert("courseId "+ courseId);
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html(''+courseName+'');
    	$("#modalInfo").show();
    	$.ajax({
    url : "ViewCourseDetails.action?courseId="+courseId,
    cache : false,
    success : function(data) {
    	$(dialogEdit).html(data);
    }
    });
    	}
    
    function viewAssessmentDetails(assessmentId, assessmentName) {
    	//alert("assessmentId "+ assessmentId);
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html(''+assessmentName+'');
    	$("#modalInfo").show();
    	$.ajax({
		    url : "ViewAssessmentDetails.action?assessmentId="+assessmentId,
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
		    });
    	}
    
    	var date = new Date();
    	var d = date.getDate();
    	var m = date.getMonth();
    	var y = date.getFullYear();
    	
    	<% if(fromPage == null || fromPage.equalsIgnoreCase("ajax")) { %>
	    	$('#calendar').fullCalendar({
	    		header: {
	                left: 'prev,next today',
	                center: 'title', 
	                right: 'month,agendaWeek,agendaDay'
	              },
	              buttonText: {
	                today: 'today',
	                month: 'month',
	                week: 'week',
	                day: 'day'
	              },
	            timeFormat: 'H(:mm)',
	    		editable: false,
	    		events: <%=alEmp %>
	    	});
    	<% } else { 
    	System.out.println("in else ...");
    	%>
	    	$(function(){
	    		$('#calendar').fullCalendar({
	    			header: {
	    	            left: 'prev,next today',
	    	            center: 'title',
	    	            right: 'month,agendaWeek,agendaDay'
	    	          },
	    	          buttonText: {
	    	            today: 'today',
	    	            month: 'month',
	    	            week: 'week',
	    	            day: 'day'
	    	          },
	    	        timeFormat: 'H(:mm)',
		    		editable: false,
		    		events: <%=alEmp %>
		    	});
	    	});
    	<% } %>
    	
   
</script>

<!--<script type="text/javascript">


$(document).ready(function(){
      $('body').append;
    	$(window).scroll(function () {
			if ($(this).scrollTop() != 0) {
				$('.backtop').fadeIn();
			} else {
				$('.backtop').fadeOut();
			}
		}); 
    $('.backtop').click(function(){
        $("html, body").animate({ scrollTop: 0 }, 600);
        return false;
    });
});
		</script>-->
		
		<script type="text/javascript">
			function pop(div) {
				document.getElementById(div).style.display = 'block';
			}
			function hide(div) {
				document.getElementById(div).style.display = 'none';
			}
			//To detect escape button
			document.onkeydown = function(evt) {
				evt = evt || window.event;
				if (evt.keyCode == 27) {
					hide('popDiv');
				}
			};
			
			$('.fc-event').mouseenter(function() {
				$(this).addClass('fc-event-hover');
			});

			$('.fc-event').mouseleave(function() {
				$(this).removeClass('fc-event-hover');
			});
		      
		</script>
		
		
		
</div>