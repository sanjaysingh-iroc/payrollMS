
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/jquery-ui.css" />
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.css' />
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.print.css' media='print' />

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.ui.core.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery-1.4.4.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery.tools.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.min.js"> </script>

<script type="text/javascript">
$(document).ready(function() {

	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y = date.getFullYear();

	$('#calendar').fullCalendar({
		editable : false,
		events : [ 
		          
					<%HashMap<String,List<String>> hmSchedule=(HashMap<String,List<String>>) request.getAttribute("hmSchedule");
					Iterator itr=hmSchedule.keySet().iterator();
					int keycount=0;
					while(itr.hasNext()) {
						String title=(String)itr.next();
							
						List<String> alDates=hmSchedule.get(title);
						for(int i=0;i<alDates.size();i++) {
						%>
			        		{
								title : '<%=title%>',
								start : new Date(<%=alDates.get(i)%>)
							}
							
							<%if(i!=alDates.size()-1 || keycount!=hmSchedule.keySet().size()-1){%>
							,
							<%}%>
						<%
						}  
						keycount++;
					}%>
		        ]
	});
});
</script>

Calender for <s:property value="trainerName"></s:property> 

<div id='calendar' style="font-size:10"></div>	
						
