<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script> --%>
 

<%-- <script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>

<script>
$(function () {
	$("input[name='btnSubmit']").click(function(){
		$("#formAddNewRow").find('.validateRequired').filter(':hidden').prop('required',false);
	    $("#formAddNewRow").find('.validateRequired').filter(':visible').prop('required',true);
	});
	
	$("#strLevelGoals").multiselect().multiselectfilter();
	$("#strLevelKras").multiselect().multiselectfilter();
});

function showType(id){
	if(document.getElementById(id).value == 'A') {
		if(document.getElementById("typeId")) {
    		document.getElementById("typeId").innerHTML = "Amount:";
    	}
	} else if(document.getElementById(id).value == 'P') {
		if(document.getElementById("typeId")) {
    		document.getElementById("typeId").innerHTML = "%:";
    	}
	}
}

function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
       return false;
    }
    return true;
 }
 
 function checkCondition(val){
	 var cFI = '<%=IConstants.A_CUSTOM_FACTOR_ID %>';
	 var nHI = '<%=IConstants.A_NO_OF_HOURS_ID %>';
	 var gktFI = '<%=IConstants.A_GOAL_KRA_TARGET_ID %>'; 
	 var kFI = '<%=IConstants.A_KRA_ID %>';
	 var nDAI = '<%=IConstants.A_NO_OF_DAYS_ABSENT_ID %>';
	 if(parseInt(val) == parseInt(cFI)){
		document.getElementById('trMinMax').style.display = "none";
		/* $("#strMax").prop('required',false);
		$("#strMax").removeClass("validateRequired");
		$("#strMin").prop('required',false);
		$("#strMin").removeClass("validateRequired"); */
		document.getElementById('trType').style.display = "table-row";
		/* $("#strType").prop('required',true); */
		document.getElementById('trGoalKRATarget').style.display = "none";
		/* $("select[name='strLevelGoals']").prop('required',false);
		$("select[name='strLevelGoals']").removeClass("validateRequired");
		$("select[name='strLevelKras']").prop('required',false);
		$("select[name='strLevelKras']").removeClass("validateRequired"); */
		document.getElementById('spanAchievedMin').style.display = "none";
		document.getElementById('spanAchievedMax').style.display = "none";
		document.getElementById('trCalculateFrom').style.display = "none";
		document.getElementById('trAddDaysAttendance').style.display = "none";
		//document.getElementById('trTypeStatus').style.display = "table-row";
	 } else if(parseInt(val) == parseInt(gktFI) || parseInt(val) == parseInt(kFI)){
		document.getElementById('trMinMax').style.display = "table-row";
		document.getElementById('trType').style.display = "none";
		/* $("#strType").prop('required',false);
		$("#strType").removeClass("validateRequired"); */
		document.getElementById('trGoalKRATarget').style.display = "table-row";
		/* $("select[name='strLevelGoals']").prop('required',true);
		$("select[name='strLevelKras']").prop('required',true); */
		document.getElementById('spanAchievedMin').style.display = "inline";
		document.getElementById('spanAchievedMax').style.display = "inline";
		document.getElementById('trCalculateFrom').style.display = "none";
		document.getElementById('trAddDaysAttendance').style.display = "none";
		
		var strOrg = document.getElementById('strOrg').value;
		var strLevel = document.getElementById('strLevel').value;
		getContent('TDGoalKRATarget', 'GetGoalKraTargetForAllowance.action?orgId='+strOrg+'&levelId='+strLevel+'&conditionType='+val);
	 } else if(parseInt(val) == parseInt(nDAI)){
			document.getElementById('trMinMax').style.display = "table-row";
			document.getElementById('trType').style.display = "none";
			document.getElementById('trGoalKRATarget').style.display = "none";
			document.getElementById('spanAchievedMin').style.display = "none";
			document.getElementById('spanAchievedMax').style.display = "none";
			document.getElementById('trCalculateFrom').style.display = "none";
			//document.getElementById('trTypeStatus').style.display = "table-row";
			document.getElementById('trAddDaysAttendance').style.display = "none";
	} else {
		document.getElementById('trMinMax').style.display = "table-row";
		document.getElementById('trType').style.display = "none";
		/* $("#strType").prop('required',false);
		$("#strType").removeClass("validateRequired"); */
		document.getElementById('trGoalKRATarget').style.display = "none";
		/* $("select[name='strLevelGoals']").prop('required',false);
		$("select[name='strLevelGoals']").removeClass("validateRequired");
		$("select[name='strLevelKras']").prop('required',false);
		$("select[name='strLevelKras']").removeClass("validateRequired"); */
		document.getElementById('spanAchievedMin').style.display = "none";
		document.getElementById('spanAchievedMax').style.display = "none";
		document.getElementById('trCalculateFrom').style.display = "table-row";
		
		if(parseInt(val) == parseInt(nHI)){
			document.getElementById('trAddDaysAttendance').style.display = "none";
		} else {
			document.getElementById('trAddDaysAttendance').style.display = "table-row";			
		}
		
		//document.getElementById('trTypeStatus').style.display = "none";
	 }
 }
 
</script>
 
<s:form theme="simple" id="formAddNewRow" action="AddAllowanceCondition" method="POST" cssClass="formcss">
	<s:hidden name="conditionId"/>
	<s:hidden name="strOrg" id="strOrg"/>
	<s:hidden name="strLevel" id="strLevel"></s:hidden>
	<s:hidden name="strSalaryHeadId" id="strSalaryHeadId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	<%
		UtilityFunctions uF = new UtilityFunctions();
		String strAllowanceCondition = (String) request.getAttribute("strAllowanceCondition");
		String strType = (String) request.getAttribute("strType");
		String strPublish = (String) request.getAttribute("strPublish");
		
		String strOtherDisplay = "table-row";
		String strCustomDisplay = "none";
		String strGKTDisplay = "none";
		String spanGKTDisplay = "none";
		String strCalculationFromDisplay = "table-row";
		String strAddDaysAttendnaceDisplay = "table-row";
		if(uF.parseToInt(strAllowanceCondition) == IConstants.A_CUSTOM_FACTOR_ID) {
			strOtherDisplay = "none";
			strGKTDisplay = "none";
			spanGKTDisplay = "none";
			strCustomDisplay = "table-row";
			strCalculationFromDisplay = "none";
			strAddDaysAttendnaceDisplay = "none";
		} else if(uF.parseToInt(strAllowanceCondition) == IConstants.A_GOAL_KRA_TARGET_ID || uF.parseToInt(strAllowanceCondition) == IConstants.A_KRA_ID) {
			strOtherDisplay = "table-row";
			strGKTDisplay = "table-row";
			spanGKTDisplay = "inline";
			strCustomDisplay = "none";
			strCalculationFromDisplay = "none";
			strAddDaysAttendnaceDisplay = "none";
		} else if(uF.parseToInt(strAllowanceCondition) == IConstants.A_NO_OF_HOURS_ID) {
			strAddDaysAttendnaceDisplay = "none";
		} else if(uF.parseToInt(strAllowanceCondition) == IConstants.A_NO_OF_DAYS_ABSENT_ID) {
			strCalculationFromDisplay = "none";
			strAddDaysAttendnaceDisplay = "none";
		}
		
		String strTypeName = "Amount:";
		if(strType!=null && strType.equals("P")) {
			strTypeName = "%:";
		}
	%>
	<table class="table table_no_border">
		<tr> 
			<td class="txtlabel alignRight">Condition Slab:<sup>*</sup></td>
			<td><s:textfield name="strCondtionSlab" id="strCondtionSlab" cssClass="validateRequired"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Condition:<sup>*</sup></td>
			<td><s:select name="strAllowanceCondition" cssClass="validateRequired" headerKey="" headerValue="Select Condition"
					list="allowanceConditionList" listKey="conditionId" listValue="conditionName" onchange="checkCondition(this.value);"></s:select>
			</td>
		</tr>
		
		<tr id="trGoalKRATarget" style="display: <%=strGKTDisplay %>">
			<td class="txtlabel alignRight" valign="top">Goal/KRA/Target:<sup>*</sup></td>
			<td id="TDGoalKRATarget">
				<% if(uF.parseToInt(strAllowanceCondition) == IConstants.A_GOAL_KRA_TARGET_ID) { %>
					<s:select name="strLevelGoals" id="strLevelGoals" cssClass="validateRequired" list="goalList" listKey="paymentLogicId" 
						listValue="paymentLogicName" multiple="true" value="gktValue"></s:select> <!-- headerKey="" headerValue="Select Goal/KRA/Target" -->
				<% } else if(uF.parseToInt(strAllowanceCondition) == IConstants.A_KRA_ID) { %>
					<s:select name="strLevelKras" id="strLevelKras" cssClass="validateRequired" list="kraList" listKey="paymentLogicId" 
						listValue="paymentLogicName" multiple="true" value="kValue"></s:select> <!-- headerKey="" headerValue="Select Goal/KRA/Target" -->
				<% } %>
			</td>
		</tr>
		
		<tr id="trMinMax" style="display: <%=strOtherDisplay %>">
			<!-- <td class="txtlabel alignRight">&nbsp;</td> -->
			<td colspan="2" class="txtlabel" align="center" style="padding-left: 169px;"><span id="spanAchievedMin" style="display: <%=spanGKTDisplay %>">Achieved %</span>&nbsp;
				Min:<sup>*&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</sup><s:textfield name="strMin" id="strMin" cssClass="validateRequired" cssStyle="width:50px !important;" onkeypress="return isNumberKey(event)"/>&nbsp;
				<span id="spanAchievedMax" style="display: <%=spanGKTDisplay %>">Achieved %</span>&nbsp;
				Max:<sup>*&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</sup><s:textfield name="strMax" id="strMax" cssClass="validateRequired" cssStyle="width:50px !important;" onkeypress="return isNumberKey(event)"/>
			</td>
		</tr>
		<tr id="trCalculateFrom" style="display: <%=strCalculationFromDisplay %>">
			<td colspan="2" class="txtlabel" style="padding-left: 100px;" align="center">
				Calculate:<sup>*</sup> &nbsp;&nbsp;&nbsp; <s:textfield name="strCalculateFrom" id="strCalculateFrom" cssClass="validateRequired" cssStyle="width:50px !important;" onkeypress="return isNumberKey(event)"/>&nbsp;
				to actual amount
			</td>
		</tr>
		
		<tr id="trAddDaysAttendance" style="display: <%=strAddDaysAttendnaceDisplay %>">
			<td class="txtlabel alignRight">Add days from approve attendance:</td>
			<td class="txtlabel"><s:checkbox name="addDaysAttendance"/></td>
		</tr>
		
		<tr id="trType" style="display: <%=strCustomDisplay %>">
			<td class="txtlabel alignRight">Type:<sup>*</sup></td>
			<td><s:select theme="simple" name="strType" id="strType" cssClass="validateRequired" list="#{'A':'Amount','P':'Percentage'}"/></td>
		</tr>
		
		<%-- <tr id="trTypeStatus" style="display: <%=strCustomDisplay %>">
			<td class="txtlabel alignRight"><span id="typeId"><%=strTypeName %></span><sup>*</sup></td>
			<td><s:textfield name="strAmtPercentage" id="strAmtPercentage" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr> --%>

		<tr>
			<td></td>
			<td> 
				<s:submit cssClass="btn btn-primary" name="btnSubmit" value="Submit"/>
				<%-- <% if(!uF.parseToBoolean(strPublish)) { %>
			 		<s:submit cssClass="btn btn-primary" name="btnSubmitPublish" value="Submit & Publish" />
			 	<% } %> --%>
			</td>
		</tr>

	</table>
	
</s:form>

