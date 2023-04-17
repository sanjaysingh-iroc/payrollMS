

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">

function leaveChartDaily() {
	document.getElementById("leaveChart").setAttribute("src", "LeaveChart.action?duration=day");
}

function leaveChartWeekly() {
	document.getElementById("leaveChart").setAttribute("src", "LeaveChart.action?duration=week");
}

function leaveChartMonthly() {
	document.getElementById("leaveChart").setAttribute("src", "LeaveChart.action?duration=month");
}

$(document).ready(function() {
	
	leaveChartMonthly();
	
}); 

function costCenterChart(value) {
	alert(value);
}

</script>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="CFO's Dashboard" name="title"/>
</jsp:include>


<div class="leftbox reportWidth">

	<div style="border: 1px solid blue; height: 300px;">
	
		<s:action name="AttendenceChart" executeResult="true"></s:action>
		
		<div id="Chart1Details_Holder"></div>
	
	</div>
	
	<br><br>
	
	<div style="border: 1px solid red; height: 300px;">
	
		<iframe id="leaveChart" height="300px" width="100%">
				<p>Your browser does not support iframes</p>
		</iframe>
		
		<a href="#" onclick="leaveChartMonthly();">Monthly Details</a>
		<a href="#" onclick="leaveChartWeekly();">Weekly Details</a>
		<a href="#" onclick="leaveChartDaily();">Daily Details</a>
	
	</div>
	
	<br><br>
	
	<div style="border: 1px solid green; height: 350px;">
	
		<s:action name="CostCenterChart" executeResult="true"></s:action>
		
		<form name="frmCostCentre" action="CostCenterChart.action">
			<select name="costCentreList" multiple="multiple" onclick="costCenterChart(this.value)">
				<option value="wl1">WL1</option>
				<option value="wl2">WL2</option>
				<option value="wl3">WL3</option>
				<option value="wl4">WL4</option>
				<option value="wl5">WL5</option>
			</select>
		</form>
		
	</div>
	
	<br><br>

</div>