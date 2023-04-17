<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
    List alMeetingRoomBooking = (List)request.getAttribute("reportListEmp");
     //  if(alMeetingRoomBooking == null ) alMeetingRoomBooking = new ArrayList();
       Map<String, List<String>> hmMeetingRoomDetails = (Map<String, List<String>>) request.getAttribute("hmMeetingRoomDetails");
      
       if(hmMeetingRoomDetails == null ) hmMeetingRoomDetails = new HashMap<String, List<String>>();
    %>
<style type='text/css'>
	
    #calendar1 {
    /*width: 900px;*/
    margin: 0 auto;
    }
   


.fc-sat, .fc-sun {
    /* background-color: #a9cfff !important; */
    background-color: rgba(215, 215, 215, 0.4) !important;
}

</style>

<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Meeting Room Booking" name="title"/>
    </jsp:include> --%>
<%
	String fromPage = (String)request.getAttribute("fromPage");
    UtilityFunctions uF = new UtilityFunctions();
    List<String> meetingRoomIds = (List<String>)request.getAttribute("roomIdList");
    if(meetingRoomIds == null) meetingRoomIds = new ArrayList<String>();
    
    %>

    <form name="frm" action="MeetingRoomBooking.action" method="post">
        <input type="hidden" name="strMeetingRoomId" id="strMeetingRoomId"/>	
       
		<div class="col-md-3" style="min-height: 600px;">
		   <div class="box box-body">
            <%if(hmMeetingRoomDetails != null && hmMeetingRoomDetails.size()>0 && !hmMeetingRoomDetails.isEmpty()) { %>
            <div style="float:left; width:100%;margin-top:10px;">
                <a class="all" href="#" onclick="openMeetingRoomBookingPopup('','','');" style="width:130px;padding:0px 10px;"><i class="fa fa-plus-circle" aria-hidden="true"></i>Book Meeting Room</a>
            </div>
            <div style="float:left; width:100%;margin-top:10px;">
                <div style="width:100%;font-weight:bold;font-size: 14px;">Meeting Rooms</div>
                <% Set roomSet = hmMeetingRoomDetails.keySet();
                    Iterator<String> it = roomSet.iterator();
                    
                    while(it.hasNext()) {
                      String roomId = it.next();
                      List<String> roomDetails = hmMeetingRoomDetails.get(roomId);
                    
                      if(roomDetails != null && roomDetails.size() >0 && !roomDetails.isEmpty()) {
                    %>
                <div class="external-event ui-draggable ui-draggable-handle" style="position: relative;background-color: <%=roomDetails.get(2) %>;border-color: <%=roomDetails.get(2) %>;color: #fff;">
                    <%if(meetingRoomIds!= null && meetingRoomIds.contains(roomId)) {%>
                    <input type="checkbox" name="strRoomIdCheck" value="<%=roomDetails.get(0)%>" onclick="submitFrm('<%=roomDetails.get(0)%>');" /> 
                    <% } else { %>
                    <input type="checkbox" name="strRoomIdCheck" value="<%=roomDetails.get(0)%>" checked="checked" onclick="submitFrm('<%=roomDetails.get(0)%>');"/>
                    <% } %> 
                    <%=roomDetails.get(1) %>
                </div>
                <% 	 }
                }
                %>
            </div>
            <% } else { %>
            <div style="float:left; width:100%;margin-top:10px;font-size:12px;">Meeting Rooms Not Available.</div>
            <% } %>
          </div>
        </div>
       <div class="col-md-9" style="border-left:1px solid #efefef;min-height: 600px;">
           <div class="box box-body">
            	<div id="calendar1" "></div>
            </div>
        </div>
    </form>

<div class="modal" id="bookModal" role="dialog">
    <div class="modal-dialog">
      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title"></h4>
        </div>
        <div class="modal-body" style="height:400px;overflow-y:auto;">
          
        </div>
        <div class="modal-footer">
          <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div>
      
    </div>
  </div>


<%-- <script src='scripts/calender/moment.min.js'></script> --%>
<%-- <script src='scripts/calender/jquery.min.js'></script> --%>
<%-- <script src='http://cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.3.1/fullcalendar.min.js'></script>  --%>
<script type='text/javascript'>
$("body").on('click','#closeButton',function(){
	$("#bookModal").hide();
});
$("body").on('click','.close',function(){
	$("#bookModal").hide();
});
    function openRoomBookingPopup(bookingId) {
    	//alert("bookingId==>"+bookingId);
        var xmlhttp;
    		if (window.XMLHttpRequest) {
    	        // code for IE7+, Firefox, Chrome, Opera, Safari
    	        xmlhttp = new XMLHttpRequest();
    		}
    	    if (window.ActiveXObject) {
    	        // code for IE6, IE5
    	    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    	    }
    	    if (xmlhttp == null) {
    	            alert("Browser does not support HTTP Request");
    	            return;
    	    } else {
    			 
    			 //removeLoadingDiv('the_div');
    			 var id=document.getElementById("panelDiv");
    				if(id){
    					id.parentNode.removeChild(id);
    				}
    				var dialogEdit = '.modal-body';
    				$('.modal-title').html('Booking Details');
    				$(dialogEdit).empty();
    				$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    				$("#bookModal").show();
    				$.ajax({
						url :"GetBookingDetailsPopup.action?bookingId="+bookingId,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
    	   } 
    }
    
    function openMeetingRoomBookingPopup(day, month, year) {
    	 var xmlhttp;
    		if (window.XMLHttpRequest) {
    	        // code for IE7+, Firefox, Chrome, Opera, Safari
    	        xmlhttp = new XMLHttpRequest();
    		}
    	    if (window.ActiveXObject) {
    	        // code for IE6, IE5
    	    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    	    }
    	    if (xmlhttp == null) {
    	            alert("Browser does not support HTTP Request");
    	            return;
    	    } else {
    	    	var bookingDate = "";   	
    	    
    	    	if(day != "" && month != "" && year != ""){
    			  bookingDate = day+"/"+month+"/"+year;
    	    	}
    			 
    			 //removeLoadingDiv('the_div');
    			 var id=document.getElementById("panelDiv");
    				if(id){
    					id.parentNode.removeChild(id);
    				}
    				var dialogEdit = '.modal-body';
    				$('.modal-title').html('Meeting Room Booking');
    				$(dialogEdit).empty();
    				$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    				$("#bookModal").show();
					$.ajax({
							url :"AddMeetingRoomBookingPopup.action?bookingDate="+bookingDate,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
    	   }
    }
    
    function submitFrm(meetingRoomId) {
    	document.getElementById("strMeetingRoomId").value = meetingRoomId;
    }
    
    var date = new Date();
    	var d = date.getDate();
    	var m = date.getMonth();
    	var y = date.getFullYear();
    	<% if(fromPage == null) { %>
    	$('#calendar1').fullCalendar({
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
    		events: <%=alMeetingRoomBooking %>
    	});
    	<% }else{ %>
    		$(function(){
    			$('#calendar1').fullCalendar({
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
    	    		events: <%=alMeetingRoomBooking %>
    	    	});
    		});
    	<% } %>
</script>