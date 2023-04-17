<%@page import="java.util.ArrayList"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<!-- <script type="text/javascript" src="scripts/charts/highcharts.js"></script> -->

<script type="text/javascript">
<%
	ArrayList alRemainingLeaves = new ArrayList();
	ArrayList alLeavesTaken = new ArrayList();
	alRemainingLeaves = (ArrayList)request.getAttribute("alRemainingLeaves");
	alLeavesTaken =(ArrayList)request.getAttribute("alLeavesTaken");
	
	String strLeavesTaken="[";
	String strRemainingLeaves="[";
	String strLeaveTypeName="[";
	
	int i;
	for(i=0; i<alLeavesTaken.size()-1; i++) {
		strLeaveTypeName = strLeaveTypeName + "'" + (String)((ArrayList)alLeavesTaken.get(i)).get(0) + "',";
		strLeavesTaken = strLeavesTaken + (String)((ArrayList)alLeavesTaken.get(i)).get(1) + ",";
		strRemainingLeaves = strRemainingLeaves + (String)alRemainingLeaves.get(i) + ",";
	}
	strLeaveTypeName = strLeaveTypeName + "'" + (String)((ArrayList)alLeavesTaken.get(i)).get(0) + "'";
	strLeavesTaken = strLeavesTaken + (String)((ArrayList)alLeavesTaken.get(i)).get(1);
	strRemainingLeaves = strRemainingLeaves + (String)alRemainingLeaves.get(i);
	
	strLeaveTypeName = strLeaveTypeName + "]";
	strLeavesTaken = strLeavesTaken + "]";
	strRemainingLeaves= strRemainingLeaves + "]";
	System.out.print(strLeaveTypeName);

%>

var strLeaveTypeName = <%=strLeaveTypeName%>;
var strLeavesTaken = <%= strLeavesTaken%>;
var strRemainingLeaves = <%=strRemainingLeaves%>;

Highcharts.setOptions({
    colors: ['#EE9A00','#00FF00']
});

var chart;
$(document).ready(function() {
   	chart = new Highcharts.Chart({
      chart: {
         renderTo: 'container',
         defaultSeriesType: 'bar',
       	 plotBorderWidth: 1,
       	 plotShadow: true
      },
      title: {
         text: 'Leaves Taken & Leaves Left'
      },
      credits: {
       	enabled: false
   	  },
      xAxis: {
         categories: strLeaveTypeName
      },
      yAxis: {
         min: 0,
         title: {
            text: 'Number Of Leaves'
         }
      },
      plotOptions: {
         series: {
            stacking: 'normal'
         }
      },
         series: [{
         	name: 'Leaves Left',
         	data:  strRemainingLeaves
      },
      {
       	name: 'Leaves Taken',
       	data: strLeavesTaken
   		}
      ]
   });
});

</script>

<div id="container" style="height: 500px;"></div>