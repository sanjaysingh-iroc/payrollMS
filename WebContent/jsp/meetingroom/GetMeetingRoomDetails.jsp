<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%
    String frmDate = (String)request.getParameter("fromDate");
    String tDate = (String)request.getParameter("toDate");
    String frmTime = (String)request.getParameter("fromTime");
    String tTime = (String)request.getParameter("toTime");
    
    String meetingRoomId = (String)request.getAttribute("meetingRoomId");
    List<String> meetingRoomDetails = (List<String>)request.getAttribute("meetingRoomDetails");
    
    if(meetingRoomDetails == null) meetingRoomDetails = new ArrayList<String>();
    
    
    
    Map<String, List<String>> hmMeetingRoomBookingDetails = (Map<String,List<String>>)request.getAttribute("hmMeetingRoomBookingDetails");
    if(hmMeetingRoomBookingDetails == null) hmMeetingRoomBookingDetails = new HashMap<String, List<String>>();
    
    %>
<input type="hidden" name = "fromDate" value="<%=frmDate %>" />
<input type="hidden" name = "toDate" value="<%=tDate %>" />
<input type="hidden" name = "fromTime" value="<%=frmTime %>" />
<input type="hidden" name = "toTime" value="<%= tTime %>" />
<%if(meetingRoomDetails != null && meetingRoomDetails.size() >0) {
    %>
<input type="hidden" name="location" id ="location" value="<%=meetingRoomDetails.get(6) %>"/>
<input type="hidden" name="strCapacity" id ="strCapacity" value="<%=meetingRoomDetails.get(4) %>"/>
<table border="0" class="table table_no_border" cellpadding="3" cellspacing="2" style="font-size:12px;" >
    <% 
        String roomLen = meetingRoomDetails.get(2);
        String roomWidth = meetingRoomDetails.get(3);
        if( roomLen != null && !roomLen.equals("")) {  %>
    <tr>
        <td class="txtlabel alignRight">Room length:</td>
        <td style="width:60%"><%=meetingRoomDetails.get(2) %></td>
    </tr>
    <% }%>
    <% if( roomWidth != null && !roomWidth.equals("")) {  %>
    <tr>
        <td class="txtlabel alignRight">Room width:</td>
        <td><%=meetingRoomDetails.get(3) %></td>
    </tr>
    <% } %>
    <tr>
        <td class="txtlabel alignRight">Seating Capacity:</td>
        <td><%=meetingRoomDetails.get(4) %></td>
    </tr>
    <tr>
        <td class="txtlabel alignRight">Room Color:</td>
        <td><input type="text" style="width:20px !important;background-color:<%=meetingRoomDetails.get(5) %>" readonly="readonly"></td>
    </tr>
    <% if(hmMeetingRoomBookingDetails != null && hmMeetingRoomBookingDetails.size() >0)  {
        Set bookingSet = hmMeetingRoomBookingDetails.keySet();
        Iterator<String> it = bookingSet.iterator();
        int count = 1;
        %>
    <tr>
        <td class="txtlabel alignRight">Bookings:</td>
        <td>
            <div style="float:left;width:100%;" class="formcss">
                <%
                    while(it.hasNext()) {
                    	String bookingId = it.next();
                    	List<String> bookings = hmMeetingRoomBookingDetails.get(bookingId);
                        if(bookings != null && bookings.size() > 0 && !bookings.isEmpty()) {
                    %>      
                <div style="float:left;width:100%;">
                    <div style="float:left;font-size:11px;margin-left:2px;"><%=count%>.</div>
                    <div style="float:left;font-size:11px;margin-left:2px;"><%=bookings.get(1)%> </div>
                    <div style="float:left;font-size:11px;margin-left:2px;"><%=bookings.get(2)%></div>
                    <div style="float:left;font-size:11px;margin-left:2px;"><%=bookings.get(3)%></div>
                </div>
                <%  
                    count++;
                    }
                    } 
                    %>
            </div>
        </td>
    </tr>
    <%
        } %>
</table>
<% } %>