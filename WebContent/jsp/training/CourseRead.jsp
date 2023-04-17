
<% 
String courseId = (String)request.getAttribute("courseId"); 
String lPlanId = (String)request.getAttribute("lPlanId");
String courseName = (String)request.getAttribute("courseName");
%>
<a onclick="addCourseReadStatus('<%=courseId%>','<%=lPlanId%>','<%=courseName %>');" href="javascript:void(0);">Update</a> 
| <a onclick="viewCourseForRead('<%=courseId%>','<%=courseName %>');" href="javascript:void(0)">Reading</a>

