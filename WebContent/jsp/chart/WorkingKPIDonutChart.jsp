<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
	UtilityFunctions uF=new UtilityFunctions();
	String workingTasks = (String) request.getAttribute("workingTasks");
 	String completedTasks = (String) request.getAttribute("completedTasks");
%>
<script type="text/javascript">
var chartLearning;
$(document).ready(function () {
//$(function () { 
  //  $('#containerforTargetDonutChart').highcharts({ 
    	chartLearning = new Highcharts.Chart({ 
        chart: {
        	renderTo: 'workKPIDonutChart',
        	type: 'pie', 
            options3d: {
				enabled: true,
                alpha: 180
            }
        },
        title: {
            text: '' 
        },
        subtitle: {
            text: ''
        },
        plotOptions: {
            pie: {
                innerSize: 100,
                depth: 25
            }
        },
        series: [{
            name: 'Complete',
            data: [
                   <% if(uF.parseToInt(workingTasks)>0 || uF.parseToInt(completedTasks)>0){%>
                ['Working Tasks', <%=uF.showData(workingTasks,"0")%>], 
                ['Completed Tasks', <%=uF.showData(completedTasks,"0")%>]
                <%} else {%>
                ['No Contents', <%="0"%>]
                <%}%>
            ]
        }]
    });
  }); 

</script>

<div id="workKPIDonutChart" style="height: 230px"></div>