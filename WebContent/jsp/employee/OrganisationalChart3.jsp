<%@page import="com.konnect.jpms.util.IMessages"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TOrganisationalChart%>" name="title"/>
</jsp:include>
 
    <%-- <script type='text/javascript' src='https://www.google.com/jsapi'></script> --%>
    <script type='text/javascript' src='<%=request.getContextPath() %>/scripts/charts/jsapi.js'></script>
    <script type='text/javascript'>
//https://google-developers.appspot.com/chart/interactive/docs/gallery/geochart#Example
//https://google-developers.appspot.com/chart/interactive/docs/gallery/geochart#Continent_Hierarchy
     google.load('visualization', '1', {'packages': ['geochart']});
     google.setOnLoadCallback(drawMarkersMap);

      function drawMarkersMap() {
    <%--   var data = google.visualization.arrayToDataTable([
        <%=request.getAttribute("sbLocation")%>
      ]); --%>
      var dataTable = new google.visualization.DataTable();
      dataTable.addColumn('string', 'City');
      dataTable.addColumn('string', 'Address');
      dataTable.addColumn('number', 'ColourCode');
      dataTable.addColumn('number', 'Size');
      dataTable.addColumn({type: 'string', role: 'tooltip'});
      dataTable.addRows([
			<%=request.getAttribute("sbLocation")%>
      ]);

      var options = {
//        region: 'world',
        region: 'IN',
        displayMode: 'markers',
	    magnifyingGlass: {enable: true, zoomFactor: 7.5},
        colorAxis: {colors: ['red', 'green', 'blue', 'darkBlue', 'BlueViolet', 'orange', 'violet', 'gray', 'Brown', 'Chartreuse']},
        resolution:'provinces',
        legend: 'none'
      };
   
      var chart = new google.visualization.GeoChart(document.getElementById('chart_div'));
      chart.draw(dataTable, options);
    };
    </script>

    


 <div class="leftbox reportWidth" style="overflow:auto">
	     <div id="chart_div" style="width: 100%;height:500px"></div>
</div>


<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
}); 
</script>

