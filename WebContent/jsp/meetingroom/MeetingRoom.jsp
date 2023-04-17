<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
#lt_wrapper .row{
margin-left: 0px;
margin-right: 0px;
}
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/autocomplete/jquery-ui.min.js"> </script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
	String sbData = (String) request.getAttribute("sbData");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	String dataType = (String) request.getAttribute("dataType");
	
	if(dataType == null || dataType.equals("")){
		dataType = "M";
	}
	
	String bookingReqCnt = (String) request.getAttribute("bookingReqCnt");
	//String meetingRoomCount = (String) request.getAttribute("meetingRoomCount");
	
	Map<String, List<String>> hmMeetingRoomDetails = (Map<String, List<String>>)request.getAttribute("hmMeetingRoomDetails");
	if(hmMeetingRoomDetails == null) hmMeetingRoomDetails = new LinkedHashMap<String, List<String>>(); 
	
	Map<String, List<String>> hmMeetingRoomsBookingReqDetails = (Map<String, List<String>>)request.getAttribute("hmMeetingRoomsBookingReqDetails");
	if(hmMeetingRoomsBookingReqDetails == null) hmMeetingRoomsBookingReqDetails = new LinkedHashMap<String, List<String>>(); 
		
%>
<script type="text/javascript">
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
function addNewMeetingRoom() {
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
	    	var dialogEdit = '.modal-body';
	    	$(dialogEdit).empty();
	    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$('.modal-title').html('Add New Meeting Room');
	    	$("#modalInfo").show();
	    	$.ajax({
                url : 'AddMeetingRoom.action',
                cache : false,
                success : function(data) {
                	     $(dialogEdit).html(data);
                }
            });
	    }
  }

  function editDeleteMeetingRoom(value,meetingRoomId,bookingCount){
	  
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
			// removeLoadingDiv('the_div');
			if(value == 1){
				var dialogEdit = '.modal-body';
		    	$(dialogEdit).empty();
		    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		    	$('.modal-title').html('Edit Meeting Room');
		    	$("#modalInfo").show();
		    	$.ajax({
	                url : 'AddMeetingRoom.action?meetingRoomId='+meetingRoomId+'&operation=E',
	                cache : false,
	                success : function(data) {
	                //	alert("data==>"+data);
	                	      $(dialogEdit).html(data);
	                }
	            });
			
			}else if(value == 2) {
				if(bookingCount != "" && parseInt(bookingCount) > 0) {
					alert("You can not delete this room ,since already booked!");
				} else {
					 if(confirm('Are you sure, you want to delete this Meeting room?')) {
						 var xhr = $.ajax({
				             url : 'AddMeetingRoom.action?meetingRoomId='+meetingRoomId+'&operation=D',
				             cache : false,
				             success : function(data) {
				             	   document.getElementById("trMeetingRoom_"+meetingRoomId).style.display="none";        	
				             }
				         });
					}
				}
			}
	    }
 }

  function approveOrDenyBookingRequest(value, bookingId) {
		
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
		    	if(value == 1) {
		    		var dialogEdit = '.modal-body';
			    	$(dialogEdit).empty();
			    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			    	$('.modal-title').html('Meeting Room Booking Request');
			    	$("#modalInfo").show();
			    	$.ajax({
		                url : 'ApproveOrDenyBookingRequests.action?bookingId='+bookingId,
		                cache : false,
		                success : function(data) {
		                
		                	     $(dialogEdit).html(data);
		                }
		            });
		   	  }
		    }
		}
  
</script>

<script type="text/javascript" charset="utf-8">


 
	
function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		var value = choice.options[i].value;
		if(choice.options[i].selected == true && value != ""){
			
			if (j == 0) {
				exportchoice =  choice.options[i].value ;
				j++;
			} else {
				exportchoice += ","+ choice.options[i].value ;
				j++;
			}
		}else if(choice.options[i].selected == true && value == ""){
			exportchoice = "";
			break;
		}
		
	}
	//alert("exportchoice==>"+exportchoice);
	return exportchoice;
}
	$(function () {
			$('#lt').DataTable({
				"order": [],
				"columnDefs": [ {
				      "targets"  : 'no-sort',
				      "orderable": false
				    }],
				'dom': 'lBfrtip',
		        'buttons': [
					'copy', 'csv', 'excel', 'pdf', 'print'
		        ]
		  	});
			$("#f_wlocation").multiselect().multiselectfilter();
	});
	
	
	function submitForm(type) {
		
		var selectedLocation="";

		if(document.getElementById("f_wlocation")){
			selectedLocation = getSelectedValue("f_wlocation");
			 console.log("emplocation"+selectedLocation);
			}
		
		if(type == '1') {
			console.log(""+"<%=dataType%>");
			var f_org = document.getElementById("f_org").value;
			window.location = 'MeetingRooms.action?f_org='+f_org+"&"+'dataType=<%=dataType%>'+"&strLocation="+selectedLocation;	
			
		} else {
			document.frmMeetingRoom.submit();
		}
	}

</script>


<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Meeting Rooms" name="title"/>
</jsp:include> --%>

<section class="content">
	<div class="row jscroll">
		<section class="col-lg-12 connectedSortable">
			<div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
					    <input type="hidden" name="dataType" value="<%=dataType%>"/>
						<s:form name="frmMeetingRoom" id="frmMeetingRoom" action="MeetingRooms" theme="simple">
					    <% if(strUserType != null && strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN)) { %>
							<div class="box box-default collapsed-box" style="margin-top: 10px;">
					                <div class="box-header with-border">
					                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					                	<div class="row row_without_margin">
											<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
												<i class="fa fa-filter"></i>
											</div>
											<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Legal Entity</p>
													<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" 
													onchange="submitForm('1');" list="organisationList" />
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Work Location</p>
													<s:select theme="simple" name="f_wlocation" id="f_wlocation" listKey="wLocationId" 
													listValue="wLocationName" list="wLocationList" multiple="true"/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">&nbsp;</p>
													<%-- <s:submit value="Submit" cssClass="btn btn-primary"/> --%>
													<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm(1)"/>							
												</div>
											</div>
										</div><br>
					                </div>
					                <!-- /.box-body -->
					            </div>
					      <% } %>
							
						</s:form>
						
						<% if(strUserType != null && strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN)) { %>
							<div class="row row_without_margin">
	                        	<div class="col-lg-12">
	                                <input type="button" onclick="addNewMeetingRoom()" value="Add New Meeting Room" class="btn btn-primary pull-right">
	                        	</div>
	                        </div>
						  <div class="clr margintop20">
						      <div class="nav-tabs-custom">
						      	<ul class="nav nav-tabs">
								<%
									if(dataType == null || dataType.equals("M")){
								%>
								   <li class="active"><a href="javascript:void(0)" onclick="window.location='MeetingRooms.action?dataType=M'" data-toggle="tab">Meeting Rooms</a></li>
								   <li><a href="javascript:void(0)" onclick="window.location='MeetingRooms.action?dataType=MBR'" data-toggle="tab">Booking Requests<span class="label label-primary tab-count"><%=bookingReqCnt %></span></a></li>
								<%  } else {%>
								   <li><a href="javascript:void(0)" onclick="window.location='MeetingRooms.action?dataType=M'" data-toggle="tab">Meeting Rooms</a></li>
								   <li class="active"><a href="javascript:void(0)" onclick="window.location='MeetingRooms.action?dataType=MBR'" data-toggle="tab">Booking Requests<span class="label label-primary tab-count"><%=bookingReqCnt %></span></a></li>
								<% } %>
								</ul>
							  </div>
						   </div>	
					   <% } %>
						
					    <%if(dataType == null || dataType.equals("M")){ %>		
						<div style="float: left; width:100%;">
							<table id="lt" class="table table-bordered">
							      <thead>
										<tr>
											<th style="text-align: left;">Meeting Room Name</th>
											<th style="text-align: left;">Length</th>
											<th style="text-align: left;">Width</th>
											<th style="text-align: left;">Seating Capacity</th>
											<th style="text-align: left;">Color</th>
											<%if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
												<th style="text-align: left;">Legal Entity</th>
												<th style="text-align: left;">Work Location</th>
												<th style="text-align: left;" class="no-sort">Action</th>
											
											<% } %>
											 
										</tr>
									</thead>
									
									<tbody>
									<% 
									    if(hmMeetingRoomDetails != null && hmMeetingRoomDetails.size() > 0 && !hmMeetingRoomDetails.isEmpty()) {
									    	Set roomSet = hmMeetingRoomDetails.keySet();
									    	Iterator<String> it = roomSet.iterator();
									    	while(it.hasNext()) {
									    		String roomId = it.next();
									    		List<String> roomDetailsList = hmMeetingRoomDetails.get(roomId);
									  %>
									  			<tr id = "trMeetingRoom_<%=roomDetailsList.get(0) %>">
													<td> <%=roomDetailsList.get(1) %></td>
													<td><%= roomDetailsList.get(2) %></td>  
													<td><%= roomDetailsList.get(3) %></td>
													<td> <%=roomDetailsList.get(4) %></td>
									            	<td>
									            	  <input type="text" name="colorCode" readonly="readonly" style="width:10px !important;height:10px !important;background-color:<%=roomDetailsList.get(5)%>"/>
									            	  <%= roomDetailsList.get(5) %></td>
								            	<%if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
													<td><%= roomDetailsList.get(6) %></td>
													<td><%= roomDetailsList.get(7) %></td>
											        <td>
														<a href="javascript:{}" onclick="editDeleteMeetingRoom('1','<%=roomDetailsList.get(0)%>','<%=roomDetailsList.get(8)%>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
														<a href="javascript:{}" onclick="editDeleteMeetingRoom('2','<%=roomDetailsList.get(0)%>','<%=roomDetailsList.get(8)%>');"><i class="fa fa-trash" aria-hidden="true"></i></a>
													</td>
												<% } %>
													  
										        </tr>
										 <%
										 		}
										 	}
										 %>
									</tbody>
								</table>
							</div>
						<%} else if(dataType != null && dataType.equals("MBR")) { %>
								<div style="float: left; width:100%;">
							<table id="lt" class="table table-bordered">
							      <thead>
										<tr>
											<th style="text-align: left;">Meeting Room Name</th>
											<th style="text-align: left;">Seating Capacity</th>
											<th style="text-align: left;">Requested by</th>
											<th style="text-align: left;">From Date</th>
											<th style="text-align: left;">To Date</th>
											<th style="text-align: left;">From Time</th>
											<th style="text-align: left;">To Time</th>
											<th style="text-align: left;">Requested on</th>
											<th style="text-align: left;">No.of people</th>
											<th style="text-align: left;">Food Service Details</th>
											<th style="text-align: left;">Location</th>
											<%if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
												<th style="text-align: left;" class="no-sort">Action</th>
											
											<% } %>
										</tr>
									</thead>
									
									<tbody>
									<% 
									    if(hmMeetingRoomsBookingReqDetails != null && hmMeetingRoomsBookingReqDetails.size() > 0 && !hmMeetingRoomsBookingReqDetails.isEmpty()) {
									    	Set bookingReqSet = hmMeetingRoomsBookingReqDetails.keySet();
									    	Iterator<String> it = bookingReqSet.iterator();
									    	while(it.hasNext()) {
									    		String bookingId = it.next();
									    		List<String> bookingRequestsList = hmMeetingRoomsBookingReqDetails.get(bookingId);
									  %>
									  			<tr id = "trMeetingRoom_<%=bookingRequestsList.get(0) %>">
													<td> <%=bookingRequestsList.get(2) %></td>
													<td><%= bookingRequestsList.get(3) %></td>  
													<td><%= bookingRequestsList.get(4) %></td>
													<td> <%=bookingRequestsList.get(5) %></td>
													<td> <%=bookingRequestsList.get(6) %></td>
													<td> <%=bookingRequestsList.get(7) %></td>
													<td> <%=bookingRequestsList.get(8) %></td>
													<td> <%=bookingRequestsList.get(9) %></td>
													<td> <%=bookingRequestsList.get(10) %></td>
													<td> <%=bookingRequestsList.get(11) %></td>
													<td> <%=bookingRequestsList.get(12) %></td>
									                <%if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
													 <td>
														<select style="width:120px !important;" onchange="approveOrDenyBookingRequest(this.value,'<%=bookingRequestsList.get(0)%>');">
															<option value="">Select</option>
															<option value="1">Approve/Deny</option>
														</select>
													</td>
													<% } %>
													  
										        </tr>
										 <%
										 		}
										 	}
										 %>
									</tbody>
								</table>
							</div>
						<%} %>
					</div>
                </div>
                <!-- /.box-body -->
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

