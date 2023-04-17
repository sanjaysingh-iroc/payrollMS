<%@ taglib prefix="s" uri="/struts-tags"%>


<style>
.clockon_content td{
text-align: center;
color: white;
}
.table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td {
border-top: 0px solid #2B4E57;
}
</style>

<% 
	boolean isRosterDependant = (Boolean) request.getAttribute("isRosterDependant"); 
	boolean isRosterRequired = (Boolean) request.getAttribute("isRosterRequired");
	//System.out.println("jsp---isRosterRequired --->> " + isRosterRequired);
	//System.out.println("jsp---isRosterDependant --->> " + isRosterDependant);
	
%>
<s:form id="frmClockEntries1" name="frmClockEntries1" theme="simple" action="ClockOnOffEntry">

<%if(isRosterDependant && isRosterRequired) { %>

	<s:if test="dashboardClockLabel=='Clock On'">
		<s:hidden name="strMode" id="strMode"></s:hidden>
		<s:hidden name="strApproval" id="strApproval"></s:hidden>
		<s:hidden name="strPrevMode" id="strPrevMode"></s:hidden>
		<s:hidden name="isRosterDependant" id="isRosterDependant"></s:hidden>
		<s:hidden name="isRosterRequired" id="isRosterRequired"></s:hidden>
		<s:hidden name="isSingleButtonClockOnOff" id="isSingleButtonClockOnOff"></s:hidden>
		<center><h2>
			<a class="btn btn-info" style="font-size: 20px !important;" onclick="setClockOnOff('onclick', 'CON')" href="javascript:void(0)"> <s:property value="dashboardClockLabel"/> </a>
		</h2></center>
	</s:if>
	
	<s:elseif test="dashboardClockLabel=='Clock Off'">
		<s:hidden name="strMode" id="strMode"></s:hidden>
		<s:hidden name="strApproval" id="strApproval"></s:hidden>
		<s:hidden name="strPrevMode" id="strPrevMode"></s:hidden>
		<s:hidden name="isRosterDependant" id="isRosterDependant"></s:hidden>
		<s:hidden name="isRosterRequired" id="isRosterRequired"></s:hidden>
		<s:hidden name="isSingleButtonClockOnOff" id="isSingleButtonClockOnOff"></s:hidden>
		<center><h2>
			<a class="btn btn-info" style="font-size: 20px !important;" onclick="setClockOnOff('onclick', 'COFF')" href="javascript:void(0)"> <s:property value="dashboardClockLabel"/> </a>
		</h2></center>
	</s:elseif >
	
	<s:else>
		<h2>
			<a href="#"><s:property value="dashboardClockLabel"/></a>
		</h2>
	</s:else>
	
	
	<s:if test="strMessage!=null">
		<table class="table table_no_border">
			<%-- <tr>
				<td><s:hidden name="strMode"></s:hidden></td>
				<td><s:hidden name="strApproval"></s:hidden></td>
			</tr> --%>
			<tr><td></td></tr>
			<tr><td></td></tr>
			<tr>
				<td><s:property value="strMessage"/></td>
			</tr>
			<tr>
				<td><s:textarea theme="simple" rows="2" cols="25" name="strReason" cssClass="validateRequired" cssStyle="color:#000"></s:textarea></td>
			</tr>
			<%-- <tr>
				<td style="font-size: 10px"><s:checkbox theme="simple" cssStyle="width:10px" name="strNotify" onclick="validateNewTimeNotification();"></s:checkbox>
	            Please select the box if you wish to notify your manager about your change in time. </td>
			</tr>
			<tr id="newTimeNotification" style="display:none">
				<td>Enter your new roster time : <s:textfield theme="simple" cssStyle="width:80px" name="strNewTime"></s:textfield> &nbsp;hh:mm</td>
			</tr> --%>
			<tr>
				<td><s:submit theme="simple" cssClass="btn btn-primary" onclick="return validateForm();" value="Enter Reason" /></td>
			</tr>
			<tr>
				<td style="font-size: 11px">Please note: You will not be clocked ON/OFF until you enter the reason.</td>
			</tr>		
		</table>
		
	</s:if>
	
	
	
	<s:elseif test="strNotRoster!=null">
		
		<table class="table table_no_border">
		
			<%-- <tr>
				<td><s:hidden name="strMode"></s:hidden></td>
				<td><s:hidden name="strApproval"></s:hidden></td>
				
			</tr> --%>
			<tr><td></td></tr>
			<tr><td></td></tr>
			<tr>
				<td><s:property value="strNotRoster"/></td>
			</tr>
			<tr>
				<td>
				<s:select theme="simple" label="Select Cost center" name="service" cssClass="validateRequired" listKey="serviceId"
					listValue="serviceName" headerKey="0" headerValue="Select Cost center"
					list="serviceList" key="" required="true" />
				</td>
			</tr>
			<tr id="newTimeNotificationS">
				<td>Enter your roster start time : <s:textfield theme="simple" cssStyle="width:80px !important;" name="strRosterStartTime" cssClass="validateRequired"></s:textfield> &nbsp;hh:mm</td>
			</tr>
			<tr id="newTimeNotificationE">
				<td>Enter your roster end time &nbsp;&nbsp;: <s:textfield theme="simple" cssStyle="width:80px !important;" name="strRosterEndTime" cssClass="validateRequired"></s:textfield> &nbsp;hh:mm</td>
			</tr>
			
			<tr>
				<td><s:submit theme="simple" onclick="return validateService();" cssClass="btn btn-primary"  value="Enter" /></td>
			</tr>
			<tr>
				<td style="font-size: 11px">Please note: Your payroll will be calculated as per the cost centre chosen.</td>
			</tr>		
		</table>
		
	</s:elseif>
	
	 
	
	<s:elseif test="strNotRosterY!=null">
		
		<table class="table table_no_border">
			<%-- <tr>
				<td><s:hidden name="strMode"></s:hidden></td>
				<td><s:hidden name="strApproval"></s:hidden></td>
			</tr> --%>
			<tr><td></td></tr>
			<tr><td></td></tr>
			<tr>
				<td><s:property value="strNotRoster"/></td>
			</tr>
			<tr>
				<td>
				<span>Please choose the cost center you will be working on today.</span>
				<s:select theme="simple" label="Select Cost center" name="service" listKey="serviceId"
					listValue="serviceName" headerKey="0" headerValue="Select Cost center"
					list="serviceList" key="" required="true" />
				</td>
			</tr>
			<tr>
				<td><s:submit theme="simple" onclick="return validateService();" cssClass="btn btn-primary"  value="Enter" /></td>
			</tr>
			<tr>
				<td style="font-size: 11px">Please note: Your payroll will be calculated as per the cost centre chosen.</td>
			</tr>		
		</table>
	</s:elseif>
	
	<div class="clockon_content"><s:property value="dashboardClockEntryText1"/></div>
	<%-- <s:property value="dashboardClockEntryText2"/>  --%>
<% } else { %>
		<s:hidden name="strMode" id="strMode"></s:hidden>
		<s:hidden name="strApproval" id="strApproval"></s:hidden>
		<s:hidden name="strPrevMode" id="strPrevMode"></s:hidden>
		<s:hidden name="isRosterDependant" id="isRosterDependant"></s:hidden>
		<s:hidden name="isRosterRequired" id="isRosterRequired"></s:hidden>
		
		<center><h2>
			<a class="btn btn-info" style="font-size: 20px !important;" onclick="setClockOnOff('onclick', 'CON')" href="javascript:void(0)">Clock On</a>
			<a class="btn btn-info" style="font-size: 20px !important;" onclick="setClockOnOff('onclick', 'COFF')" href="javascript:void(0)">Clock Off</a>
		</h2></center>
		
		<div class="clockon_content"><s:property value="dashboardClockEntryText1"/></div>
<% } %>

</s:form>

<script type="text/javascript">
	$(document).ready(function() {
	    // jQuery code goes here
	    jQuery("#frmClockEntries1").validationEngine();
	});
</script>