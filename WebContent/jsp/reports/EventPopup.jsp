<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- timeline -->
<link rel="stylesheet" href="scripts/timeline/css/reset.css"> <!-- CSS reset -->
<link rel="stylesheet" href="scripts/timeline/css/style.css"> <!-- Resource style -->
<script src="scripts/timeline/js/modernizr.js"></script> <!-- Modernizr -->
<!-- timeline end -->


<%
	List<String> eventList = (List<String>)request.getAttribute("alInner");
	if(eventList == null) eventList = new ArrayList<String>();
	
	List<String> availableExt = (List<String>)request.getAttribute("availableExt");
	if(availableExt == null) availableExt = new ArrayList<String>();
	
	
%>

<%if(eventList!=null && eventList.size()>0){ %>
<div id="printDiv" class="leftbox reportWidth">

	<div class="leftholder" style="width: 100%; ">
		<div style="float: left;  width: 100%;">
			<% 
				boolean flag = false;
				if(availableExt.contains(eventList.get(11))) {
					flag = true;
				}
			
				if(eventList.get(9) != null && !eventList.get(9).equals("")) {
					String filePath = eventList.get(12);					
					if(eventList.get(11)!=null && (eventList.get(11).equalsIgnoreCase("jpg") || eventList.get(11).equalsIgnoreCase("jpeg") || eventList.get(11).equalsIgnoreCase("png") || eventList.get(11).equalsIgnoreCase("bmp") || eventList.get(11).equalsIgnoreCase("gif"))){ 
				%>	
						<img class="lazy" src="<%=filePath %>" data-original="<%=filePath %>" style="max-width: 100%; max-height: 250px;">
									
				<% } else { 
						if(flag && eventList.get(12)!=null && !(eventList.get(12)).equals("")){
				%>
							<div id="tblDiv" style="float: left; width: 100%;">
									<a href="javascript:void(0)" onclick="viewEventFilePopup('<%=eventList.get(0)%>')" style="color:gray;">&nbsp;<%=eventList.get(9)%></a>
							</div>
					<% } else {	%>
							<div style="float:left; padding: 60px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Preview not available.</div>		
					<% }
				 }
				}
			%>						
			</div>
			<table class="table table_no_border ">
				<tr> 
				   <td><span  style="font-size: 16px;width:100%;color:#a14f76;font-style: italic;">Event <%=eventList.get(4)%></span></td>
				</tr>
				<tr> 
				   <td><span style = "font-size:11px;font-style:italic;color:blue;">Posted By : </span>
				   		<span style = "font-size:11px;color:gray;"><%=eventList.get(6)%> on <%=eventList.get(3) %></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:14px;color:green;">Description : </span>
				   	<span style = "font-size:12px;color:gray;"><%= eventList.get(5)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:12px;">Location: </span>
				   	<span style = "font-size:12px;color:gray;"><%= eventList.get(7)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:12px;">Start Date : </span>
				   	<span style = "font-size:12px;color:gray;"><%= eventList.get(1)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:12px;">End Date : </span>
				   	<span style = "font-size:12px;color:gray;"><%= eventList.get(2)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:12px;">From Time : </span>
				   	<span style = "font-size:12px;color:gray;"><%= eventList.get(14)%></span>
				   </td>
				</tr>
				
				<tr> 
				   <td><span style = "font-size:12px;">To Time: </span>
				   	<span style = "font-size:12px;color:gray;"><%= eventList.get(15)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "color:gray;font-style:italic;font-size:11px;">Shared With : </div>
          				<span style = "font-size:10px;color:gray;width:88%;"><%= eventList.get(8)%></span>
				   </td>
				</tr>
				
			</table>
			<%-- <div style="width:100%;">
		 		<div  style="float:left;font-size: 16px;width:100%;margin:20px 2px 3px 2px;color:#a14f76;font-style: italic;">Event <%=eventList.get(4)%></div>
		 	</div>	
		 	<div style="width:100%;">
		 		<div style = "float:left;margin-left:3px;font-size:11px;font-style:italic;color:blue;">Posted By : </div>
		 		<div style = "float:left;width:90%;margin-left:5px;font-size:11px;color:gray;"><%=eventList.get(6)%> on <%=eventList.get(3) %></div>
		 	</div>
			<div style="width:100%;">
         		<div style = "float:left;margin-left:3px;margin-top:12px;font-size:14px;color:green;width:100%;">Description : </div>
         		<div style = "float:left;margin-left:20px;margin-top:4px;font-size:12px;color:gray;width:97%"><%= eventList.get(5)%></div>
         	</div>
         	<div style="width:100%;">
          		<div style = "float:left;margin-left:3px;margin-top:10px;font-size:12px;">Location: </div>
          		<div style = "float:left;margin-left:5px;margin-top:10px;font-size:12px;color:gray;width:87%;"><%= eventList.get(7)%></div>
          	</div>
          	<div style="width:100%;">
          		<div style = "float:left;margin-left:3px;margin-top:10px;font-size:12px;">Start Date : </div>
          		<div style = "float:left;margin-left:5px;margin-top:10px;font-size:12px;color:gray;width:87%;"><%= eventList.get(1)%></div>
          	</div>
          	<div style="width:100%;">
          		<div style = "float:left;margin-left:3px;margin-top:10px;font-size:12px;">End Date :</div>
          		<div style = "float:left;margin-left:5px;margin-top:10px;font-size:12px;color:gray;width:87%;"> <%= eventList.get(2)%></div>
         	 </div>
          	<div style="width:100%;">
          		<div style = "float:left;margin-left:3px;margin-top:10px;font-size:12px;">From Time : </div>
          		<div style = "float:left;margin-left:5px;margin-top:10px;font-size:12px;color:gray;width:87%;"><%= eventList.get(14)%></div>
         	</div>
          	<div style="width:100%;">
          		<div style = "float:left;margin-left:3px;margin-top:10px;font-size:12px;">To Time: </div>
          		<div style = "float:left;margin-left:5px;margin-top:10px;font-size:12px;color:gray;width:87%;"><%= eventList.get(15)%></div>
          </div>
          <div style="width:100%;">
          	<div style = "float:left;margin-left:3px;margin-top:10px;color:gray;font-style:italic;font-size:11px;">Shared With : </div>
          	<div style = "float:left;margin-left:5px;margin-top:10px;font-size:10px;color:gray;width:88%;"><%= eventList.get(8)%></div>
          </div> --%>
   
   </div>
</div>	
<%} %>	
<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
}); 
</script>