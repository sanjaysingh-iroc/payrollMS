<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
	String strType = (String) request.getParameter("type");
%>

 
<script>
	$(function() {
		$("#idStdTimeStart").timepicker({});
		$("#idStdTimeEnd").timepicker({});
		$("#idStdDateStart").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		$("#idStdDateEnd").datepicker({
			dateFormat : 'dd/mm/yy'
		});
	});

	
	jQuery(document).ready(function(){
        // binds form submission and fields to the validation engine
        jQuery("#formID1").validationEngine();
    });
	
	
	
	var xmlhttp = "";
	var xhttp = "";
	function GetXmlHttpObject() {
		if (window.XMLHttpRequest) {
			// code for IE7+, Firefox, Chrome, Opera, Safari
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject) {
			// code for IE6, IE5
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
		return null;
	}

	function getPersonsList(id) {

		var extraTask = document.getElementById("extraTask").value;

		if (extraTask == 'a Sales Call' || extraTask == 'a Conference Call'
				|| extraTask == 'a Meeting with Client'
				|| extraTask == 'a Client Demo'
				|| extraTask == 'a Client Visit'
				|| extraTask == 'a Field Visit') {
			document.getElementById("empList").style.display = 'none';
			document.getElementById("client").style.display = 'table-row';

			xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var url = "GetClientListAjax.action";
				xmlhttp.onreadystatechange = stateChanged_getClientList;
				xmlhttp.open("GET", url, true);
				xmlhttp.send(null);

			}

		} else if (extraTask == 'a Meeting with my Supervisor'
				|| extraTask == 'a Meeting with my Subordinate'
				|| extraTask == 'a Meeting with HR'
				|| extraTask == 'a Team Meeting') {
			document.getElementById("client").style.display = 'none';
			document.getElementById("empList").style.display = 'table-row';

			xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var url = "GetEmployeeListAjax.action";

				xmlhttp.onreadystatechange = stateChanged_getEmployeeList;
				xmlhttp.open("GET", url, true);
				xmlhttp.send(null);

			}
		} else {
			document.getElementById("client").style.display = 'none';
			document.getElementById("empList").style.display = 'none';
		}

	}
	function stateChanged_getClientList() {
		if (xmlhttp.readyState == 4) {

			var res = xmlhttp.responseText;

			document.getElementById("clientListId").innerHTML = res;
			document.getElementById("client").style.visibility = 'visible';

		}
	}
	function stateChanged_getEmployeeList() {
		if (xmlhttp.readyState == 4) {

			var res = xmlhttp.responseText;

			document.getElementById("employeeListID").innerHTML = res;
			document.getElementById("empList").style.visibility = 'visible';

		}
	}

	function getActivityList(id) {

		document.getElementById("tsktr").style.display = 'table-row';

		xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var url = "GetActivityListAjax.action";
			url += "?pro_id=" + id;

			xmlhttp.onreadystatechange = stateChanged_getActivityList;
			xmlhttp.open("GET", url, true);
			xmlhttp.send(null);
		}

	}

	function stateChanged_getActivityList() {
		if (xmlhttp.readyState == 4) {

			var res = xmlhttp.responseText;
			//alert(res);

			document.getElementById("activityListId").innerHTML = res;
			document.getElementById("tsktr").style.visibility = 'visible';

		}
	}

	function getProjectList(id, id1) {

		if (document.getElementById(id).checked == true) {

			/* document.getElementById("protr").style.visibility='visible';
			document.getElementById("projectListId").style.visibility='visible'; */

			document.getElementById("protr").style.display = 'table-row';
			document.getElementById("projectListId").style.display = 'table-cell';

			/* document.getElementById("tsktr").style.display='table-row'; */

			xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var url = "GetProjectListAjax.action";
				url += "?emp_id=" + id1;

				xmlhttp.onreadystatechange = stateChanged_getProjectList;
				xmlhttp.open("GET", url, true);
				xmlhttp.send(null);
			}
		} else {
			/* document.getElementById("projectListId").style.visibility='hidden';
			document.getElementById("protr").style.visibility='hidden'; */
			document.getElementById("projectListId").style.display = 'none';
			document.getElementById("protr").style.display = 'none';

			document.getElementById("tsktr").style.display = 'none';
		}

	}

	function stateChanged_getProjectList() {
		if (xmlhttp.readyState == 4) {

			var res = xmlhttp.responseText;
			// 		alert(res);

			document.getElementById("projectListId").innerHTML = res;
			document.getElementById("protr").style.visibility = 'visible';

		}
	}

	$(function() {
		$("#deadline").datepicker({
			dateFormat : 'dd/mm/yy'
		});
	});

	addLoadEvent(prepareInputsForHints);

	function getDateDiff() {
		var d1 = document.getElementById("idStdDateStart").value;
		var d2 = document.getElementById("idStdDateEnd").value;
		var arrD1 = d1.split("/");
		var arrD2 = d2.split("/");
		var dt1 = new Date(arrD1[2], (arrD1[1] - 1), arrD1[0]);
		var dt2 = new Date(arrD2[2], (arrD2[1] - 1), arrD2[0]);
		var millisecondsPerDay = 1000 * 60 * 60 * 24;
		var millisBetween = dt2.getTime() - dt1.getTime();
		var days = millisBetween / millisecondsPerDay;
		var diff = Math.floor(days) + 1;

		var t1 = document.getElementById("idStdTimeStart").value;
		var t2 = document.getElementById("idStdTimeEnd").value;

		var arrT1 = t1.split(":");
		var arrT2 = t2.split(":");

		var tt1 = new Date(arrD1[2], (arrD1[1] - 1), arrD1[0], arrT1[0],
				arrT1[1], 0);
		var tt2 = new Date(arrD2[2], (arrD2[1] - 1), arrD2[0], arrT2[0],
				arrT2[1], 0);

		var oDiff = new Object();

		var nTotalDiff = tt2.getTime() - tt1.getTime();

		oDiff.days = Math.floor(nTotalDiff / 1000 / 60 / 60 / 24);
		nTotalDiff -= oDiff.days * 1000 * 60 * 60 * 24;

		oDiff.hours = Math.floor(nTotalDiff / 1000 / 60 / 60);
		nTotalDiff -= oDiff.hours * 1000 * 60 * 60;

		oDiff.minutes = Math.floor(nTotalDiff / 1000 / 60);
		nTotalDiff -= oDiff.minutes * 1000 * 60;

		oDiff.seconds = Math.floor(nTotalDiff / 1000);

		var totalHrs = (oDiff.hours * diff);
		var totalMns = (oDiff.minutes * diff);
		var hrs = Math.floor(totalMns / 60);
		totalHrs = totalHrs + hrs;
		var mns = totalMns - Math.floor(hrs * 60);
		if (mns < 10) {
			mns = "0" + mns;
		}
		document.getElementById("idTotalHours").value = totalHrs + "." + mns;

	}
</script>
<div class="leftbox reportWidth" style="margin: 0px; min-height: 0">

	<s:form id="formID1" action="AddProjectActivity" cssClass="formcss"
		method="post" theme="simple">

		<s:hidden name="emp_id" />


		<table>

			<tr>
				<td>From Date</td>
				<td><s:textfield name="frmDate" id="idStdDateStart"
						onblur="getDateDiff()" cssClass="validateRequired" /></td>

				<td>To Date</td>
				<td><s:textfield name="toDate" id="idStdDateEnd"
						onblur="getDateDiff()" cssClass="validateRequired" /></td>
			</tr>

			<tr>
				<td>Select Client</td>
				<td colspan="3"><s:select name="strClient" listKey="clientId"
						listValue="clientName" headerKey="" headerValue="Select Client"
						list="clientlist" key="" cssClass="validateRequired" 
						onchange="getContent('myProject','GetProjectClientTask.action?client_id='+this.value)"/></td>
			</tr>

			<tr>
				<td>Select Project</td>
				<td colspan="3" id="myProject"><s:select name="strProject" listKey="projectID"
						listValue="projectName" headerKey="" headerValue="Select Project"
						list="projectdetailslist" key="" cssClass="validateRequired" 
						onchange="getContent('myTask','GetProjectClientTask.action?project_id='+this.value)"/>
				</td>
			</tr>

			<tr>
				<td>Select Task</td>
				<td colspan="3" id="myTask"><s:select name="strTask" listKey="taskId"
						listValue="taskName" headerKey="" headerValue="Select Task"
						list="tasklist" key="" cssClass="validateRequired" /></td>
			</tr>

			<tr>
				<td>Start Time</td>
				<td><s:textfield name="frmTime" id="idStdTimeStart"
						onblur="getDateDiff()" cssClass="validateRequired" /></td>

				<td>End Time</td>
				<td><s:textfield name="toTime" id="idStdTimeEnd"
						onblur="getDateDiff()" cssClass="validateRequired" /></td>
			</tr>

			<tr>
				<td>Total Hours</td>
				<td><s:textfield name="totalHours" id="idTotalHours"
						cssClass="validateRequired" /></td>
			</tr>

			<tr>
				<td></td>
				<td><s:submit cssClass="input_button" value="Add Activity"
						name="submit" /></td>
			</tr>

		</table>

	</s:form>



</div>