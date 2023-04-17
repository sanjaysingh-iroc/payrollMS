<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
	UtilityFunctions uF=new UtilityFunctions();
	String hybridLearning = (String) request.getAttribute("hybridLearning");
 	String coursesLearning = (String) request.getAttribute("coursesLearning");
 	String assessmentLearning = (String) request.getAttribute("assessmentLearning");
 	String classRoomLearning = (String) request.getAttribute("classRoomLearning");
%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>
<script type="text/javascript" src="scripts/charts/exporting.js"></script>
<script type="text/javascript">

var chartLearning;
$(document).ready(function () {
//$(function () { 
  //  $('#containerforTargetDonutChart').highcharts({ 
    	chartLearning = new Highcharts.Chart({ 
        chart: {
        	renderTo: 'containerforLearningDonutChart',
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
                innerSize: 130,
                depth: 45
            }
        },
        series: [{
            name: 'Complete',
            data: [
                   <% if(uF.parseToInt(hybridLearning)>0 || uF.parseToInt(coursesLearning)>0 || uF.parseToInt(assessmentLearning)>0 || uF.parseToInt(classRoomLearning)>0){%>
                ['Hybrid', <%=uF.showData(hybridLearning,"0")%>], 
                ['Courses', <%=uF.showData(coursesLearning,"0")%>],
                ['Assessment', <%=uF.showData(assessmentLearning,"0")%>], 
                ['Class Room', <%=uF.showData(classRoomLearning,"0")%>]
                <%} else {%>
                ['No Contents', <%="0"%>]
                <%}%>
            ]
        }]
    });
  }); 

</script>

<div id="containerforLearningDonutChart" style="height: 330px"></div>