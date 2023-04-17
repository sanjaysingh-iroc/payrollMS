<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>


<% 
	UtilityFunctions uF = new UtilityFunctions();
	List<String> holidayList = (List<String>)request.getAttribute("holidayList");
	if(holidayList == null) holidayList = new ArrayList<String>(); 
	Map<String, Map<String, String>> hmHolidayDates =(Map<String, Map<String, String>>)request.getAttribute("hmHolidayDates");
	if(hmHolidayDates == null) hmHolidayDates = new LinkedHashMap<String, Map<String,String>>(); 
%>


<script type='text/javascript'>

	$(document).ready(function() {
		
		var date = new Date();
		var d = date.getDate();
		var m = date.getMonth();
		var y = date.getFullYear();

		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,basicWeek,basicDay'
			},editable: false,
			events: <%=holidayList %>
		});
	}); 
	
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


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Company Holidays" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">

	<div style="float:right; width:19%; margin:1px">
		<%
		Iterator<String> it = hmHolidayDates.keySet().iterator();
		//for(int i = 0; holidayDateList!=null && i < holidayDateList.size(); i++){
		int i=1;
		while(it.hasNext()){
			String strHolidayDate = it.next();	
			Map<String, String> hmInner = hmHolidayDates.get(strHolidayDate);
		%>
			<div style="float:left; width:100%; margin:1px">
					<div style="float: left; width: 18px; height: 20px;"><%=i++ %>.</div>
					<div style="float:left; padding:0px 10px; width: 165px; font-size: 12px;"><%=uF.showData(hmInner.get("HOLIDAY"),"") %></div>
					<div style="float:left; width:20px; height:20px; background-color:<%=uF.showData(hmInner.get("HLIDAY_COLOR"),"") %>"></div>
			</div>
		<%} %>
	</div>


	<div style="float:left; width:80%; text-align: center;">
		<div id="calendar" style="float:left; width:100%;"></div>
	</div>		
</div>

