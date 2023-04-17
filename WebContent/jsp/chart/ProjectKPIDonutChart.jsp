<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
	UtilityFunctions uF=new UtilityFunctions();
	String workingProjects = (String) request.getAttribute("workingProjects");
 	String completedProjects = (String) request.getAttribute("completedProjects");
%>
<script type="text/javascript">
var chartLearning;
$(document).ready(function () {
//$(function () { 
  //  $('#containerforTargetDonutChart').highcharts({ 
    	chartLearning = new Highcharts.Chart({ 
        chart: {
        	renderTo: 'projectKPIDonutChart',
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
                   <% if(uF.parseToInt(workingProjects)>0 || uF.parseToInt(completedProjects)>0){%>
                ['Working Projects', <%=uF.showData(workingProjects,"0")%>], 
                ['Completed Projects', <%=uF.showData(completedProjects,"0")%>]
                <%} else {%>
                ['No Contents', <%="0"%>]
                <%}%>
            ]
        }]
    });
  }); 

</script>

<div id="projectKPIDonutChart" style="height: 230px"></div>