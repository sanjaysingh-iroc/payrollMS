<jsp:include page="../../common/Links.jsp" flush="true"></jsp:include>


<%@page import="java.util.ArrayList"%>

<script type="text/javascript">
var alLeaveType = '<%=session.getAttribute("alLeaveType")%>';
var approvedCnt = '<%=session.getAttribute("approvedCnt")%>';
var deniedCnt = '<%=session.getAttribute("deniedCnt")%>';
var waitingCnt = '<%=session.getAttribute("waitingCnt")%>';
var chart;
</script>

<script type="text/javascript">

$(document).ready(function() {
	chart = new Highcharts.Chart({
      chart: {
         renderTo: 'Acontainer',
        	defaultSeriesType: 'column'
      },
      title: {
         text: 'Leave Details'
      },
      credits: {
          enabled: false
      },
      xAxis: {
         categories: alLeaveType
      },
      yAxis: {
         min: 0,
         title: {
            text: 'No of Employees'
         }
      },
      plotOptions: {
         column: {
            pointPadding: 0.2,
            borderWidth: 0
         }
      },
     series: [{
         name: 'Approved',
         data: approvedCnt
   
      }, {
         name: 'Denied',
         data: deniedCnt
   
      }, {
         name: 'Waiting',
         data: waitingCnt
   
      }]
   });
});




</script>

<div id="Acontainer" style="height: 300px;">
</div>

















	