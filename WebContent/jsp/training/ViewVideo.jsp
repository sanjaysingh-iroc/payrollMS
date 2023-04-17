<% 
String videoId = (String)request.getAttribute("videoId"); 
String lPlanId = (String)request.getAttribute("lPlanId");
String videoName = (String)request.getAttribute("videoName");
%>
<%-- <a onclick="addCourseReadStatus('<%=videoId%>','<%=lPlanId%>','<%=videoName %>');" href="javascript:void(0);">Update</a> --%> 
<a onclick="showVideo('<%=videoId%>','<%=videoName %>');" href="javascript:void(0)">Viewed</a>