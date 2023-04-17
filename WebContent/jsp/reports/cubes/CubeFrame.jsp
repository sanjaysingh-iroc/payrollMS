<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>

<%
Map hmOuter = (Map)request.getAttribute("hmOuter");

%>
<iframe id="Chart" height="300px" width="100%" src="<%=request.getAttribute("PAGE")%>">
		<p>Your browser does not support iframes</p>
</iframe>

<div id="Acontainer" style="height: 300px;"></div>
