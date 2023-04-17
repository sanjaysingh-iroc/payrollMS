<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>




<%--
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.ui.core.js"> </script>

 
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery-1.4.4.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery.tools.min.js"> </script> --%>


<% List alEmp = (List)request.getAttribute("reportListEmp"); 
List<String> monthList = (List<String>) request.getAttribute("monthList");	
//System.out.println("monthList -----> " + monthList);
//System.out.println("alEmp -----> " + alEmp);
String fromPage = (String) request.getAttribute("fromPage");
%>


<script type='text/javascript'> 
	  
  function openTrainingScheduleDayDetails(dayDesId, dayCount){
	//alert("sddddddddddddddddsdddddddddddd " + dayDesId);
	
	  var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('View Training Schedule Day Details');
	 $.ajax({
			url : "TrainingScheduleOneDayDetails.action?dayDesId="+dayDesId+"&dayCount="+dayCount,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
  }
		
</script>
 
<style type='text/css'>
body {
	font-size: 14px;
}
#calendar {
	width: 900px;
	margin: 0 auto;
}

/*.fc-sat, .fc-sun{
	background-color: #a9cfff !important;
}*/

</style>


<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Training Calendar" name="title"/>
</jsp:include> --%>


<div id="printDiv" class="box-body">

<%-- <form name="frm" action="Calendar.action" method="post">
<s:hidden name="strFrm"></s:hidden>
<div style="float:right; width:19%; margin:1px">

<div style="float:left; width:100%; margin:1px">
		<div style="float: left; width: 18px; height: 20px;"><s:checkbox name="strTrainings" onclick="document.frm.submit();"/></div>
		<div style="float:left;padding:0px 10px;width: 165px;">My Trainings</div>
		<div style="float:left;width:20px;height:20px;background-color:#889988;"></div>
</div>
<div style="float:left; width:100%; margin:1px">
        <div style="float: left; width: 18px; height: 20px;"><s:checkbox name="strInterviewsPending" onclick="document.frm.submit();"/></div>
		<div style="float: left; padding: 0px 10px;width: 165px;">My Interviews Schedules</div>
		<div style="float: left; width: 20px; height: 20px; background-color: #9900CC;"></div>
</div>

<div style="float:left;width:60%;margin:1px">
        <div style="float: left; width: 20px; height: 20px;"><s:checkbox name="strInterviewsApproved" onclick="document.frm.submit();"/></div>
		<div style="float: left; padding: 0px 10px;width: 170px;">Approved Candidates</div>
		<div style="float: left; width: 20px; height: 20px; background-color: #336600;"></div>
</div>  
<div style="float:left;width:60%;margin:1px">
        <div style="float: left; width: 20px; height: 20px;"><s:checkbox name="strInterviewsDenied" onclick="document.frm.submit();"/></div>
		<div style="float: left; padding: 0px 10px;width: 170px;">Rejected Candidates</div>
		<div style="float: left; width: 20px; height: 20px; background-color: #FF0000;"></div>
</div>

 
<div style="float:right;">
	<!-- <img width="60px" src="images/icons/Outlook.jpg"> -->
</div>


</div>
</form> --%>
<%  
int calCnt = 0;
for(int i=0; monthList != null && i < monthList.size(); i++) {

%>
<%if(calCnt%2 == 0 || calCnt == 0) { %>
	<div class="row">
	<% } %>
		<div class="col-lg-6 col-md-6 col-sm-12" style=" <%if(calCnt%2 != 0 || calCnt == 1) { %><% } %> ">
			<div id="calendar<%=i %>" style=" width: 100%; font-size: 11px;"></div>
		</div>	
	<%if(calCnt%2 != 0 || calCnt == 1) { %>
	</div>
	<% } %>
	<%if(calCnt%2 == 0 && calCnt > 0) { %>
	
	<% } %>
<%
calCnt++;
} %>

<%if(monthList == null || monthList.isEmpty()){ %>
<div class="nodata msg" style="width: 95%"><span>No training scheduled</span></div>
<% } %>	
</div>



<div id="TrainingScheduleDiv"></div>
<script>
jQuery.curCSS = function(element, prop, val) {
 	 return jQuery(element).css(prop, val);
}

var date = new Date();
var d = date.getDate();
var m = date.getMonth();
var y = date.getFullYear();
<% for(int i=0; monthList != null && i < monthList.size(); i++) { %>
$(function(){
	$('#calendar<%=i%>').fullCalendar({
		editable: false,
		events: <%=alEmp %>,
		month: <%=monthList.get(i) %>
	});
});
<% } %>
</script>