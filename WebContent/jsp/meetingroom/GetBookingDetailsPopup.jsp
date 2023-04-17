<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<style>
    .tr_div {
    font-size:12px;font-family:arial;margin-top:5px;
    }
    #bookingDetailsTable .tdValue{
    word-break: break-all;
    width: 70%;
    padding-bottom: 5px;
    }
    #bookingDetailsTable .formcss{
    margin-left: 20px;
    margin-right: 20px;
    }	
    #bookingDetailsTable .alignRight{
    text-align: right;
    padding-right: 10px;
    padding-bottom: 5px;
    }
</style>
<%
    UtilityFunctions uF = new UtilityFunctions();
    String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    List<String> bookingDetails = (List<String>)request.getAttribute("bookingDetails");
    if( bookingDetails == null)  bookingDetails = new ArrayList<String>(); 
    %>
<div id="printDiv" >
    <div style="float:left;width:100%;">
        <% if(bookingDetails != null && bookingDetails.size()>0 && !bookingDetails.isEmpty()) { %>
        <center>
            <table class="formcss" id="bookingDetailsTable" cellpadding="2" cellspacing="2">
                <tr>
                    <%if(uF.parseToInt(bookingDetails.get(14)) == 1) { %>
                    <td class="txtlabel alignRight">Booked by</td>
                    <%} else { %>
                    <td class="txtlabel alignRight">Requested by</td>
                    <%} %>
                    <td class="tdValue"><%=bookingDetails.get(4)%></td>
                </tr>
                <tr>
                    <td class="txtlabel alignRight">Meeting Room Name:</td>
                    <td class="tdValue"><%=bookingDetails.get(2)%></td>
                </tr>
                <tr>
                    <td class="txtlabel alignRight">Date:</td>
                    <td class="tdValue">From <%=bookingDetails.get(5)%> to <%=bookingDetails.get(6)%> </td>
                </tr>
                <tr>
                    <td class="txtlabel alignRight">Between(Time):</td>
                    <td class="tdValue">From <%=bookingDetails.get(7)%> to <%=bookingDetails.get(8)%></td>
                </tr>
                <tr>
                    <td class="txtlabel alignRight">F&B service Details:</td>
                    <td class="tdValue"><%=bookingDetails.get(11)%></td>
                </tr>
                <tr>
                    <td class="txtlabel alignRight">People invited:</td>
                    <td class="tdValue"><%=bookingDetails.get(17)%><%=bookingDetails.get(13)%></td>
                </tr>
                <tr>
                    <td class="txtlabel alignRight">Status:</td>
                    <td class="tdValue"><%=bookingDetails.get(18)%></td>
                </tr>
                <% if(strUserType != null && ((uF.parseToInt(strEmpId) == uF.parseToInt(bookingDetails.get(15))) || strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN)) { %>
                <%if(uF.parseToInt(bookingDetails.get(14)) == 0) { %>
                <tr>
                    <td class="txtlabel alignRight">
                        <input type="button" class="input_button btn btn-info"  name="strEdit" style="margin-top: 5px;" onclick="editAndDeleteBookingRequest('<%=bookingDetails.get(0)%>', 'E');" value="Edit">
                    </td>
                    <td>
                        <input type="button" class="cancel_button btn btn-danger"  name="strDelete" style="margin-top: 5px;margin-left:10px;" onclick="editAndDeleteBookingRequest('<%=bookingDetails.get(0)%>', 'D');" value="Delete">
                    </td>
                </tr>
                <% } } %>
            </table>
        </center>
        <% } %>
    </div>
</div>
<div class="modal" id="editBooking" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Edit Booking Request</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div id = "EditBookingRequestPopup"></div>
<script>
    $("body").on('click','#closeButton',function(){
    	$("#editBooking").hide();
    });
    $("body").on('click','.close',function(){
    	$("#editBooking").hide();
    });
       var dialogEditBooking = '.modal-body';
    function editAndDeleteBookingRequest(bookingId, type) {
      if( type == 'E') {
    		 //removeLoadingDiv('the_div');
    		$(dialogEditBooking).empty();
    		$(dialogEditBooking).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$("#editBooking").show();
    		$.ajax({
                   url : 'AddMeetingRoomBookingPopup.action?bookingId='+bookingId+'&operation=E',
                   cache : false,
                   success : function(data) {
                   	
                   	$(dialogEditBooking).html(data);
                   }
               });
    		
    	 } else {
    		 if(confirm("Are you sure, you wish to delete this booking request?")) {
    		    window.location = 'AddMeetingRoomBookingPopup.action?bookingId='+bookingId+'&operation=D';
    		 }
        }
    }    	
</script>