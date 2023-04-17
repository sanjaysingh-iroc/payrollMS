<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="strMessage!=null">
	
	<table>
		<tr>
			<td><s:hidden name="strMode"></s:hidden></td>
			<td><s:hidden name="strApproval"></s:hidden></td>
		</tr>
		<tr>
			<td><s:property value="strMessage"/></td>
		</tr>
		<tr>
			<td><s:textarea theme="simple" rows="2" cols="25" name="strReason" cssClass="validateRequired"></s:textarea></td>
		</tr>
		<tr>
			<td style="font-size: 10px"><s:checkbox theme="simple" cssStyle="width:10px" name="strNotify" onclick="validateNewTimeNotification();"></s:checkbox>
            Please select the box if you wish to notify your manager about your change in time. </td>
		</tr>
		<tr id="newTimeNotification" style="display:none">
			<td>Enter your new roster time : <s:textfield theme="simple" cssStyle="width:80px" name="strNewTime"></s:textfield></td>
		</tr>
		<tr>
			<td><s:submit theme="simple" cssClass="input_button" onclick="return validateForm();" cssStyle="margin-left:40px" value="Enter Reason" /></td>
		</tr>
		<tr>
			<td style="font-size: 9px">Please note: You will not be clocked untill you enter the reason.</td>
		</tr>		
	</table>
	
</s:if>



<s:elseif test="strNotRoster!=null">
	
	<table>
	
		<tr>
			<td><s:hidden name="strMode"></s:hidden></td>
			<td><s:hidden name="strApproval"></s:hidden></td>
			
		</tr>
		<tr>
			<td class="label"><s:property value="strNotRoster"/></td>
		</tr>
		<tr>
			<td>
			<s:select theme="simple" label="Select Cost center" name="service" listKey="serviceId"
		listValue="serviceName" headerKey="0" headerValue="Select Cost center"
		list="serviceList" key="" required="true" />
			</td>
		</tr>
		<tr id="newTimeNotificationS">
			<td>Enter your roster start time : <s:textfield theme="simple" cssStyle="width:80px" name="strRosterStartTime"></s:textfield></td>
		</tr>
		<tr id="newTimeNotificationE">
			<td>Enter your roster end time &nbsp;&nbsp;: <s:textfield theme="simple" cssStyle="width:80px" name="strRosterEndTime"></s:textfield></td>
		</tr>
		
		<tr>
			<td><s:submit theme="simple" onclick="return validateService();" cssClass="input_button" cssStyle="margin-left:40px" value="Enter" /></td>
		</tr>
		<tr>
			<td style="font-size: 9px">Please note: Your payroll will be calculated as per the cost centre chosen.</td>
		</tr>		
	</table>
	
</s:elseif>



<s:elseif test="strNotRosterY!=null">
	
	<table>
	
		<tr>
			<td><s:hidden name="strMode"></s:hidden></td>
			<td><s:hidden name="strApproval"></s:hidden></td>
		</tr>
		<tr>
			<td class="label"><s:property value="strNotRoster"/></td>
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
			<td><s:submit theme="simple" onclick="return validateService();" cssClass="input_button" cssStyle="margin-left:40px" value="Enter" /></td>
		</tr>
		<tr>
			<td style="font-size: 9px">Please note: Your payroll will be calculated as per the cost centre chosen.</td>
		</tr>		
	</table>
	
</s:elseif>




<div class="clockon_content"><s:property value="dashboardClockEntryText1"/></div>
<%-- <s:property value="dashboardClockEntryText2"/>  --%>