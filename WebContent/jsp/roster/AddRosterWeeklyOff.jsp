<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*" %>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script>
$("#btnAddNewRowOk").click(function(){
	$(".validateRequired").prop('required',true);
});
$("#weeklyOffDay").multiselect({noneSelectedText: 'Select Something (required)'}).multiselectfilter();
$("#weekno").multiselect({noneSelectedText: 'Select Something (required)'}).multiselectfilter();
</script> 

	<s:form theme="simple" id="frm" name="frm" action="AddRosterWeeklyOff" method="POST" cssClass="formcss">
		<s:hidden name="rWeeklyOffId"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight" style="width: 25%;"><label for="BankDetails">Weekly Off:<sup>*</sup></label></th>
				<td><s:textfield name="weeklyOff" id="wlocationTypeCode" cssClass="validateRequired"/> </td>
			</tr>
			<tr> 
				<th class="txtlabel alignRight" style="width: 25%;"><label for="BankDetails">Weekly Off Type:<sup>*</sup></label><br/></th>
				<td>
					<s:select id="weeklyOffType" cssClass="validateRequired" name="weeklyOffType" listKey="weekDayId" listValue="weekDayName" 
					list="weeklyOffTypeList"  key="" required="true" /> <!-- onchange="checkHalfDay1();" -->
			</tr>
			
			<tr>
				<th class="txtlabel alignRight" style="width: 25%;"><label for="BankDetails">Weekly Off Day:<sup>*</sup></label><br/></th>
				<td>
					<span id="weeklyOff1Span">
						<s:select id="weeklyOffDay" multiple="true" size="2" cssClass="validateRequired" name="weeklyOffDay" listKey="weekDayId" listValue="weekDayName"
						list="weeklyOffList" key="" required="true" />
					</span>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight" style="width: 25%;"><label for="BankDetails">Weeks no.:<sup>*</sup></label><br/></th>
				<td>
					<s:select id="weekno" multiple="true" size="2" name="weekno" listKey="weekDayId" listValue="weekDayName" 
					list="weeklNoList" key="" required="true"  cssClass="validateRequired"/>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel" style="width: 25%;">&nbsp;</td>
				<td>
					<s:submit  cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
				</td>
			</tr>
			
		</table>
	</s:form>

<script>
	checkHalfDay1();
</script>