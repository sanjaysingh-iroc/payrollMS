<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
String strType = (String)request.getParameter("type");
double totalHrs = (Double)request.getAttribute("TotalHrs");
%>


<script>
$(function() {
    $( "#idStdTimeStart" ).timepicker({});
	$( "#idStdTimeEnd" ).timepicker({});
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
	
 	if(extraTask=='a Sales Call' || extraTask=='a Conference Call' || extraTask=='a Meeting with Client' || extraTask=='a Client Demo' || extraTask=='a Client Visit' || extraTask=='a Field Visit') {
 		document.getElementById("empList").style.display='none';
 		document.getElementById("client").style.display='table-row';
 		
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
		
	} else if(extraTask=='a Meeting with my Supervisor' || extraTask=='a Meeting with my Subordinate' || 
		extraTask=='a Meeting with HR' || extraTask=='a Team Meeting') {
		document.getElementById("client").style.display='none';
		document.getElementById("empList").style.display='table-row';

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
		document.getElementById("client").style.display='none';
		document.getElementById("empList").style.display='none';
	}
	
}


function stateChanged_getClientList() {
	if (xmlhttp.readyState == 4) {

		var res = xmlhttp.responseText;

		document.getElementById("clientListId").innerHTML = res;
		document.getElementById("client").style.visibility='visible';

	}
}


function stateChanged_getEmployeeList() {
	if (xmlhttp.readyState == 4) {

		var res = xmlhttp.responseText;

		document.getElementById("employeeListID").innerHTML = res;
		document.getElementById("empList").style.visibility='visible';

	}
}


function getActivityList(id) {
	 
	document.getElementById("tsktr").style.display='table-row';
	xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var url = "GetActivityListAjax.action";
		url += "?pro_id=" +id;

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
		document.getElementById("tsktr").style.visibility='visible';

	}
}


function getProjectList(id, id1) {
	
	//alert("id ===>>> " + id);
	//alert("id1 ===>>> " + id1);
	var extraTask = document.getElementById("extraTask").value;
	var clientId = "";
	if(document.getElementById("clientId"))
		clientId = document.getElementById("clientId").value;
	
	if(document.getElementById(id).checked==true ) {
		if(parseInt(extraTask) > 0) {
			//alert("extraTask if ---->>> " + parseInt(extraTask));
		} else {
			//alert("extraTask else ---->>> " + parseInt(extraTask));
			document.getElementById("protr").style.display='table-row';
			document.getElementById("projectListId").style.display='table-cell';
			
			xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var url = "GetProjectListAjax.action";
				url += "?emp_id="+id1+"&clientId="+clientId;
		
				xmlhttp.onreadystatechange = stateChanged_getProjectList;
				xmlhttp.open("GET", url, true);
				xmlhttp.send(null);
			}
		}
	    document.getElementById("strBillableYesNoT_0").value='1';
	    if(document.getElementById("strBillableTime_0") && document.getElementById("strTime_0")) {
		    document.getElementById("strBillableTime_0").readOnly = false;
		    document.getElementById("strBillableTime_0").value = document.getElementById("strTime_0").value;
	    }
	} else {
		if(document.getElementById("strBillableTime_0")) {
			document.getElementById("strBillableTime_0").readOnly = true;
			document.getElementById("strBillableTime_0").value = '0.00';
		}
		document.getElementById("strBillableYesNoT_0").value='0';
		
		/* document.getElementById("projectListId").style.visibility='hidden';
		document.getElementById("protr").style.visibility='hidden'; */
		document.getElementById("projectListId").style.display='none';
		document.getElementById("protr").style.display='none';
		
		document.getElementById("tsktr").style.display='none';
	}
	checkAndAddBillableTime();
}


function stateChanged_getProjectList() {
	if (xmlhttp.readyState == 4) {

		var res = xmlhttp.responseText;
// 		alert(res);

		document.getElementById("projectListId").innerHTML = res;
		document.getElementById("protr").style.visibility='visible';

	}
}


function setValue() {
	if (document.getElementById("strTaskOnOffSite_0").checked == 1) {
          document.getElementById("strTaskOnOffSiteT_0").value='1';
	} else {
		document.getElementById("strTaskOnOffSiteT_0").value='0';
	}
}


function convertTimeFormat() {
	// var time = $("#starttime").val();
	var time = document.getElementById('idStdTimeStart').value;
	var hrs = Number(time.match(/^(\d+)/)[1]);
	var mnts = Number(time.match(/:(\d+)/)[1]);
	var format = time.match(/\s(.*)$/)[1];
	if (format == "PM" && hrs < 12) hrs = hrs + 12;
	if (format == "AM" && hrs == 12) hrs = hrs - 12;
	var hours =hrs.toString();
	var minutes = mnts.toString();
	if (hrs < 10) hours = "0" + hours;
	if (mnts < 10) minutes = "0" + minutes;
	//alert(hours + ":" + minutes);
	 
	 var date1 = new Date();
	date1.setHours(hours );
	date1.setMinutes(minutes);
	//alert(date1);
	 
	var time = document.getElementById('idStdTimeEnd').value;
	var hrs = Number(time.match(/^(\d+)/)[1]);
	var mnts = Number(time.match(/:(\d+)/)[1]);
	var format = time.match(/\s(.*)$/)[1];
	if (format == "PM" && hrs < 12) hrs = hrs + 12;
	if (format == "AM" && hrs == 12) hrs = hrs - 12;
	var hours = hrs.toString();
	var minutes = mnts.toString();
	if (hrs < 10) hours = "0" + hours;
	if (mnts < 10) minutes = "0" + minutes;
	//alert(hours+ ":" + minutes);
	var date2 = new Date();
	date2.setHours(hours );
	date2.setMinutes(minutes);
	//alert(date2);
	 
	var diff = date2.getTime() - date1.getTime();
	 
	var hours = Math.floor(diff / (1000 * 60 * 60));
	diff -= hours * (1000 * 60 * 60);
	 
	var mins = Math.floor(diff / (1000 * 60));
	diff -= mins * (1000 * 60);
	//alert( hours + " hours : " + mins + " minutes : " );
	
	var totTime = hours+"."+minutes;
	
	if(parseFloat(totTime) < 0) {
		totTime = 0;
	}
	document.getElementById("strTime_0").value = totTime;
	
	var checkboxval = document.getElementById("strBillableYesNoT_0").value;
	if(checkboxval == '1') {
		document.getElementById("strBillableTime_0").value = document.getElementById("strTime_0").value;
	} else {
		document.getElementById("strBillableTime_0").value = '0.00';
	}
}


	$(function() {
		$("#deadline").datepicker({
			dateFormat : 'dd/mm/yy'
		});
	});

	
	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#frmAddExtraActivity").validationEngine();
	});
	addLoadEvent(prepareInputsForHints);
	
	
	function checkTimeFilledEmp() {
		
		var totalHrs = document.getElementById("hidetotalHrs").value;
		var strTime = document.getElementById("strTime_0").value;
		
		var filledTotHrs = parseFloat(totalHrs) + parseFloat(strTime);
		
		if(parseFloat(filledTotHrs) > 24) {
			alert('Actual time is more than 24 hours');
			document.getElementById("strTime_0").value = '';
			document.getElementById("strBillableTime_0").value = '';
			return false;
		}
		var extraTask = document.getElementById("extraTask").value;
		//alert("extraTask ===>> " + extraTask);
		getContent('dateApprovedStatus', 'CheckDateApprovedStatus.action?tId='+extraTask);
		window.setTimeout(function() {
			var strDateStatus = document.getElementById("hideDateApprovedStatus").value;
			if(strDateStatus == '1') {
				alert("Timesheet for this date is already approved.");
				return false;
			} else {
				document.frmAddExtraActivity.submit();
				return true;
			}
		}, 500);
		//return false;
	}

	
	function checkBillHours() {
		var strTime = document.getElementById('strTime_0').value;
		var strBillableTime = document.getElementById('strBillableTime_0').value;
		
		//alert("strTime ===> "+ strTime + "  strBillableTime ==>>" + strBillableTime);
			if(strTime == '') {
				strTime = 0;
			}
			if(strBillableTime == '') {
				strBillableTime = 0;
			}
		if(parseFloat(strBillableTime) > parseFloat(strTime)) {
			alert('Billing Time is exceeding total hours.\nPlease ensure time does not exceed '+strTime+' hours.');
			document.getElementById('strBillableTime_0').value = '0.00';
		}
	}
	
	
	function isNumberKey(evt) {
		   var charCode = (evt.which) ? evt.which : event.keyCode;
		   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
		      return false;
		
		   return true;
		}

	function checkPercentage(value) {
		//alert("value ===>> " + value);
		if(parseFloat(value) > 100) {
			alert("Please check, completion percentage greater than 100");
			document.getElementById("workStatus").value = '0';
		}
	}
</script>
<div class="leftbox reportWidth" style=" margin: 0px;  min-height:0">
	
		<s:form name="frmAddExtraActivity" id="frmAddExtraActivity" action="AddExtraActivity" cssClass="formcss" method="post" onsubmit="return checkTimeFilledEmp()" theme="simple">
			
			<s:hidden name="emp_id" />
			<input type="hidden" name="hidetotalHrs" id="hidetotalHrs" value="<%=totalHrs %>" />
			<table class="formcss">
			<tr>
				<td class="txtlabel alignRight">Select Activity:<sup>*</sup></td>
				<td>
				<span id="dateApprovedStatus">
					<input type="hidden" name="hideDateApprovedStatus" id="hideDateApprovedStatus" value="0"/>
				</span>
				<s:select name="extraTask" id="extraTask" listKey="activityID" listValue="activityName" cssClass="validateRequired"
						headerKey="" headerValue="Select Activity" list="extraActivityList" key="" required="true" 
						onchange="getPersonsList(this.id);" /></td>

			</tr>
			<tr id="client" style="display: none;">
				<td class="txtlabel alignRight" valign="top">Select Client:<sup>*</sup>
				</td>
				<td id="clientListId"><s:select theme="simple" label="Select Client" name="clientId" id="clientId" listKey="clientId"
						listValue="clientName" headerKey="" headerValue="Select Client" list="clientlist" key="" required="true" /></td>
			</tr>
		
			<tr id="empList" style="display: none;">
				<td class="txtlabel alignRight" valign="top">Select Employee:<sup>*</sup>
				</td>
				<td id="employeeListID"><s:select theme="simple" label="Select Employees" name="empId" listKey="employeeId"
						size="6" cssClass="validateRequired" listValue="employeeName" headerKey="" headerValue="Select Employee" multiple="true"
						list="empNamesList" key="" required="true" />
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Is Billable: </td>
				<td><input type="hidden" style="width: 30px;"  id="strBillableYesNoT_0" name="strBillableYesNoT" value="0">
				<s:checkbox name="isBillable" onclick="getProjectList(this.id, %{emp_id});" label="Is Billable" /></td>
			</tr>
			<tr id="protr" style="display: none;">
				<td class="txtlabel alignRight"  valign="top" >Select Project:<sup>*</sup></td>
				<td  id="projectListId">
					<s:select theme="simple" label="Select Project" name="p_id" listKey="projectID" listValue="projectName" cssClass="validateRequired"
						headerKey="" headerValue="Select Project" list="projectdetailslist" key="" required="true" /> <!-- onchange="getActivityList(this.value);" -->
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">On-site: </td>
				<td><input type="hidden" style="width: 30px;"  id="strTaskOnOffSiteT_0" name="strTaskOnOffSiteT" value="1">
				<input type="checkbox" name="strTaskOnOffSite" id="strTaskOnOffSite_0" onchange="setValue()" style="width: 30px;" checked="checked">
			</td>
			</tr>		
					<%-- <tr id="tsktr" style="display: none;">
					<td class="txtlabel alignRight"  valign="top">Select Task<sup>*</sup></td>
					<td id="activityListId">
						<s:select theme="simple" label="Select Activity" name="task_id" listKey="activityID" listValue="activityName" 
							headerKey="" headerValue="Select Activity" list="activitydetailslist" key="" required="true" />
					</td>
				</tr> --%>
				
				
					<!-- <tr>
					<td class="txtlabel alignRight" valign="top"></td>
					<td  id="projectListId"></td>
					</tr>
					
					<tr>
					<td class="txtlabel alignRight" valign="top"></td>
					<td  id="activityListId"></td>
					</tr> -->
					
					<%-- <tr>
					<td class="txtlabel alignRight" valign="top">Select SBU</td>
					<td><s:select name="service_id" listKey="serviceId" listValue="serviceName" headerKey="" 
							headerValue="Select SBU" list="serviceList" key="" />
					</td>
					</tr> --%>
				<tr>
					<td class="txtlabel alignRight" valign="top">Description</td>
					<td colspan="2"><textarea cols="50" rows="2" style="width: 220px;" name="comment"></textarea>
					<%-- <s:textarea name="comment" cols="40" rows="03" label="Description" cssClass="validate[required]" required="true" /> --%>
					</td>
					
				</tr>
				
				<%if(strType!=null && strType.equalsIgnoreCase("M")) { %>
				
				<tr>
					<td class="txtlabel alignRight" valign="top">Start Time:<sup>*</sup></td>
					<td colspan="2"><s:textfield name="strStartTime" id="idStdTimeStart" cssClass="validateRequired" />
					</td>
				</tr>

				<tr>
					<td class="txtlabel alignRight" valign="top">End Time:<sup>*</sup></td>
					<td colspan="2"><s:textfield name="strEndTime" id="idStdTimeEnd" cssClass="validateRequired" />
					<a href="javascript:void(0);" onclick="convertTimeFormat()">Get Actual Hrs.</a>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Actual Hours:<sup>*</sup></td>
					<td><s:textfield name="strTime" id="strTime_0" cssStyle="width:93px" cssClass="validateRequired" readonly="true"/>&nbsp;&nbsp;
						<!-- <a href="javascript:void(0);" onclick="convertTimeFormat()"></a> -->
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Billable Hours:</td>
					<td>
					<input type="text" value="0" id="strBillableTime_0" name="strBillableTime" style="width:62px" onkeyup="checkBillHours();" onkeypress="return isNumberKey(event)">
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Completion Status (%):</td>
					<td><s:textfield name="workStatus" id="workStatus" cssStyle="width:93px" onkeyup="checkPercentage(this.value);" onkeypress="return isNumberKey(event)"/></td>
				</tr>
				<% } %>
				
				<tr><td colspan="2" align="center">
					<%-- <s:submit cssClass="input_button" value="Start" name="submit" label="Start"/> --%>
					<input type="hidden" name="start" value="Submit"/>
					<%if(strType!=null && strType.equalsIgnoreCase("M")) { %>
						<s:submit cssClass="input_button" value="Submit" name="submit"/>
						<!-- <input type="button" class="input_button" value="Submit" onclick="checkDateApprovedStatus();"/> -->
					<%} else { %> 
						<s:submit cssClass="input_button" value="Start" name="submit"/>
						<!-- <input type="button" class="input_button" value="Start" onclick="checkDateApprovedStatus();"/> -->
					<% } %>
					</td>
				</tr>
				
			</table>
		</s:form>
	
</div>