<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script>

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
				removeLoadingDiv('the_div');
				var dialogEdit = '#ApproveOrDenyBookingRequest';
				dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
				$(dialogEdit).dialog ({
					autoOpen : false,
					bgiframe : true,
					resizable : false, 
					height : 250,  
					width :350, 
					modal : true,
					title : 'Meeting Room Booking Request',
					open : function() {
							 var xhr = $.ajax({
				                url : 'ApproveOrDenyBookingRequests.action?bookingId='+bookingId,
				                cache : false,
				                success : function(data) {
				                
				                	     $(dialogEdit).html(data);
				                }
				            });
			           	
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
				$(dialogEdit).dialog('open');
	   	  }
	    }
	}

  
</script>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function () {
		
			$('#lt').dataTable({ bJQueryUI: true, 
				  								
				"sPaginationType": "full_numbers",
				"aaSorting": [],
				"sDom": '<"H"lTf>rt<"F"ip>',
				oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
				aButtons: [
					"csv", "xls", {
						sExtends: "pdf",
						sPdfOrientation: "landscape"
						//sPdfMessage: "Your custom message would go here."
						}, "print" 
					]
				}
			});
	});
</script>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Meeting Rooms Booing Requests" name="title"/>
</jsp:include>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
	String sbData = (String) request.getAttribute("sbData");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	Map<String, List<String>> hmMeetingRoomsBookingReqDetails = (Map<String, List<String>>)request.getAttribute("hmMeetingRoomsBookingReqDetails");
	if(hmMeetingRoomsBookingReqDetails == null) hmMeetingRoomsBookingReqDetails = new LinkedHashMap<String, List<String>>(); 
		
%>

<div id="printDiv" class="leftbox reportWidth">
	<s:form name="frmMeetingRoomBookingRequests" id="frmMeetingRoomBookingRequests" action="MeetingRoomsBookingRequests" theme="simple">
    <% if(strUserType != null && strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN)) { %>
	 <div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
		<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
			<%=(String)request.getAttribute("selectedFilter") %>
		</p>
	    <div class="content1" style="height: 65px;">
				<div style="float: left; width: 100%; margin-top: -5px;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-filter"></i>
					</div>
					<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Legal Entity</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left; margin-right: 10px;" listValue="orgName" 
							onchange="document.frmMeetingRoomBookingRequests.submit();" list="orgList" />
					</div>
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Work Location</p>
							<s:select theme="simple" name="f_wlocation" id="f_wlocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
							listValue="wLocationName" list="wLocationList" multiple="true"/>
					</div>
					<div style="float:left; margin-top: 10px;margin-left: 40px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" />
					</div>
				</div>
			</div>
		</div>
      <% } %>
	<div style="float:left;width:100%;margin-top:10px;">
	
			<div style="float:left;width:57%;">
				<div style="float:left; font-size:12px; line-height:22px; width:514px; margin:7px 0px 0px 270px;">
			           <span style="float:left; margin-right:7px;">Search:</span>
			           <div style="border:solid 1px #68AC3B;float:left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
				            <div style="float:left;">
				            	<input type="text" id="strSearchJob" name="strSearchJob" style="margin-left: 0px; border:0px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>"/> 
				          	</div>
				         	 <div style="float:right">
				            	<input type="submit" value="Search"  class="input_search" >
				            </div>
			       		</div>
			     </div>
		       </div>
		      <script>
				$( "#strSearchJob" ).autocomplete({
					source: [ <%=uF.showData(sbData,"") %> ]
				});
			</script>
		</div>
		
	</s:form>
			
	<div style="float: left; width:100%;">
		<table id="lt" class="display">
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
						<th style="text-align: left;">Food Service Location</th>
						<%if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
							<th style="text-align: left;">Action</th>
						
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
									<select style="width:70px;" onchange="approveOrDenyBookingRequest(this.value,'<%=bookingRequestsList.get(0)%>');">
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
</div>
<div id="ApproveOrDenyBookingRequest"></div>
