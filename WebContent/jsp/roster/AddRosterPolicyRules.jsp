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

$("#btnSubmit").click(function() {
	$(".validateRequired").prop('required',true);
});

$("#strLevel").multiselect().multiselectfilter();
$("#strWLocationCombined").multiselect().multiselectfilter();


function showHideTR(ruleType) {
	if(ruleType == '1') {
		document.getElementById("trShifts").style.display = 'table-row';
		document.getElementById("trMultiShifts").style.display = 'none';
		document.getElementById("trNoOfDays").style.display = 'table-row';
		document.getElementById("trGender").style.display = 'none';
	} else if(ruleType == '2') {
		document.getElementById("trShifts").style.display = 'none';
		document.getElementById("trMultiShifts").style.display = 'none';
		document.getElementById("trNoOfDays").style.display = 'table-row';
		document.getElementById("trGender").style.display = 'none';
	} else if(ruleType == '3') {
		document.getElementById("trShifts").style.display = 'none';
		document.getElementById("trMultiShifts").style.display = 'none';
		document.getElementById("trNoOfDays").style.display = 'none';
		document.getElementById("trGender").style.display = 'table-row';
	} else if(ruleType == '4') {
		document.getElementById("trShifts").style.display = 'none';
		document.getElementById("trMultiShifts").style.display = 'table-row';
		document.getElementById("trNoOfDays").style.display = 'none';
		document.getElementById("trGender").style.display = 'none';
	} else {
		document.getElementById("trShifts").style.display = 'none';
		document.getElementById("trMultiShifts").style.display = 'none';
		document.getElementById("trNoOfDays").style.display = 'none';
		document.getElementById("trGender").style.display = 'none';
	}
	
}

</script> 
<% String operation = (String)request.getAttribute("operation"); %>

	<% if(operation == null || !operation.equals("PREVIEW")) { %>
		<s:form theme="simple" id="frmAddRosterPolicyRules" name="frmAddRosterPolicyRules" action="AddRosterPolicyRules" method="POST" cssClass="formcss">
			<s:hidden name="rPolicyRuleId"></s:hidden>
			<s:hidden name="strOrg"></s:hidden>
			
			<s:hidden name="userscreen" />
			<s:hidden name="navigationId" />
			<s:hidden name="toPage" />
			
			<table class="table table_no_border">
				<tr>
					<td class="txtlabel alignRight" style="width: 50%;">Organisation:<sup>*</sup></td>
					<td>
						<s:select theme="simple" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" list="organisationList" key="" /> <!-- onchange="submitForm('1', 'EmployeeReport');"  -->
					</td>
				</tr>
			
				<tr>
					<td class="txtlabel alignRight" style="width: 50%;">Department:<sup>*</sup></td>
					<td>
						<s:select name="f_department" id="f_department" list="departList" listKey="deptId" listValue="deptName"></s:select>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight" style="width: 50%;">Roster Policy Rule Name:<sup>*</sup></td>
					<td>
						<%-- <s:select name="rosterPolicyRuleType" id="rosterPolicyRuleType" headerKey="" headerValue="Select Rule Type" listKey="rosterPolicyRuleId" listValue="rosterPolicyRuleName" 
						list="rosterPolicyRuleList" key="" required="true" cssClass="validateRequired" onchange="showHideTR(this.value);"/>  <!-- onchange="showHideTR(this.value);" --> --%>
						<s:textfield name="rosterPolicyRuleName" id="rosterPolicyRuleName" cssClass="validateRequired"/>
					</td>
				</tr>
				
				
				<tr>
					<td class="txtlabel alignRight">Rotation of Shifts:<sup>*</sup></td>
					<td>
						<div style="padding: 2px; float: left;"><s:select name="shiftName1" id="shiftName1" listKey="shiftId" cssStyle="width:100px !important;"
							listValue="shiftCode" headerKey="" headerValue="Select Shift" list="shiftList" key="" required="true" cssClass="validateRequired"/></div>
						<div style="padding: 2px; float: left;"><i class="fa fa-long-arrow-right" title="Next" style="padding-top: 7px;"></i></div>
						<div style="padding: 2px; float: left;"><s:select name="shiftName2" id="shiftName2" listKey="shiftId" cssStyle="width:100px !important;"
							listValue="shiftCode" headerKey="" headerValue="Select Shift" list="shiftList" key="" required="true" cssClass="validateRequired"/></div>
						<div style="padding: 2px; float: left;"><i class="fa fa-long-arrow-right" title="Next" style="padding-top: 7px;"></i></div>
						<div style="padding: 2px; float: left;"><s:select name="shiftName3" id="shiftName3" listKey="shiftId" cssStyle="width:100px !important;"
							listValue="shiftCode" headerKey="" headerValue="Select Shift" list="shiftList" key="" required="true" cssClass="validateRequired"/></div>
					</td>
				</tr>
				
				<tr>
					<%-- <td class="txtlabel alignRight">Rotation of Shifts:<sup>*</sup></td> --%>
					<td class="txtlabel alignCenter" colspan="2">Remaining employee to be assigned to &nbsp;<s:select name="shiftNameOther" id="shiftNameOther" listKey="shiftId" cssStyle="width:100px !important;"
						listValue="shiftCode" headerKey="" headerValue="Select Shift" list="shiftList" key="" required="true" cssClass="validateRequired"/> &nbsp;shift.
					</td>
				</tr>
											
				<tr id="trShifts">
					<td class="txtlabel alignRight">Minimum No. of members in Shift:</td>
					<%-- <td><s:select name="shiftName" id="shiftName" listKey="shiftId" listValue="shiftCode" list="shiftList" key="" required="true" cssClass="validateRequired"/></td> --%>
					<td><s:textfield name="minNoofMemberInShift" id="minNoofMemberInShift"/></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Minimum No. of members in Shift on the Weekend:</td>
					<td><s:textfield name="minNoofMemberInShiftOnWeekend" id="minNoofMemberInShiftOnWeekend"/></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignCenter" colspan="2" >
						No. of leaders <s:textfield name="minNoofLeadersInShift" id="minNoofLeadersInShift" cssStyle="width: 50px !important;"/> and their levels 
						<s:select theme="simple" name="strLevel" id="strLevel" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""/>  
						for No. of members <s:textfield name="minNoofMemberWithLeadersInShift" id="minNoofMemberWithLeadersInShift" cssStyle="width: 50px !important;"/></td>
					<%-- <td class="txtlabel alignRight">No. of leaders and their levels for No. of members:</td>
					<td><s:textfield name="minNoofLeadersInShift" id="minNoofLeadersInShift"/>
						<div style="margin: 5px 0px;"><s:select theme="simple" name="strLevel" id="strLevel" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""/></div>
						<s:textfield name="minNoofMemberWithLeadersInShift" id="minNoofMemberWithLeadersInShift"/>
					</td> --%>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">No. of Weekend off in a Month:</td>
					<td><s:textfield name="noofWeekendOffInMonth" id="noofWeekendOffInMonth"/></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Max. No. of different shifts assigned to members in a Month:</td>
					<td><s:textfield name="maxNoofShiftsAssignInMonth" id="maxNoofShiftsAssignInMonth"/></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Min. No. of days break in stretch shift:</td>
					<td><s:textfield name="minNoofDaysBreakInStretchShift" id="minNoofDaysBreakInStretchShift"/></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Min. No. of days week off between change shift:</td>
					<td><s:textfield name="minNoofDaysWeekOffbetweenChangeShift" id="minNoofDaysWeekOffbetweenChangeShift"/></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignCenter" colspan="2">
						Members in Work Location <s:select theme="simple" name="strWLocation" id="strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" /> 
						to be combined with <s:select theme="simple" name="strWLocationCombined" id="strWLocationCombined" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" /> Work Location.</td>
					<%-- <td class="txtlabel alignRight">Members in Work Location to be combined with these Work Location:</td>
					<td>
						<s:select theme="simple" name="strWLocation" id="strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" />
						<div style="margin: 5px 0px;"><s:select theme="simple" name="strWLocationCombined" id="strWLocationCombined" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" /></div>
					</td> --%>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Min. No. of male members in a shift:</td>
					<td><s:textfield name="minNoofMaleMemberInShift" id="minNoofMaleMemberInShift"/></td>
				</tr>
				
				<%-- <tr id="trMultiShifts" style="display: none;">
					<th class="txtlabel alignRight">Shifts:<sup>*</sup></th>
					<td><s:select name="multiShiftName" id="multiShiftName" listKey="shiftId" listValue="shiftCode" list="shiftList" key="" required="true" cssClass="validateRequired"/></td>
				</tr>
				
				<tr id="trNoOfDays" style="display: none;"> 
					<th class="txtlabel alignRight">No. of Days:<sup>*</sup></th>
					<td><s:textfield cssClass="validateRequired" id="noOfDays" name="noOfDays" required="true" ></s:textfield></td>
				</tr>
				
				<tr id="trGender" style="display: none;">
					<th class="txtlabel alignRight">Gender:<sup>*</sup></th>
					<td><s:select theme="simple" name="empGender" listKey="genderId" listValue="genderName" headerKey="0" headerValue="All" list="empGenderList"/></td>
				</tr> --%>
				
				<tr>
					<td class="txtlabel">&nbsp;</td>
					<td><s:submit cssClass="btn btn-primary" value="Submit" name="btnSubmit" id="btnSubmit" /></td>
				</tr>
				
			</table>
		</s:form>
		
	<% } else { %>
	
		<table class="table table_no_border">
			<tr>
				<td class="txtlabel alignRight" style="width: 50%;">Organisation:</td>
				<td><%=(String)request.getAttribute("strOrg") %></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" style="width: 50%;">Department:</td>
				<td><%=(String)request.getAttribute("f_department") %></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" style="width: 50%;">Roster Policy Rule Name:</td>
				<td><%=(String)request.getAttribute("rosterPolicyRuleName") %></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Rotation of Shifts:</td>
				<td>
					<div style="padding: 2px; float: left;"><%=(String)request.getAttribute("shiftName1") %></div>
					<div style="padding: 2px; float: left;"><i class="fa fa-long-arrow-right" title="Next"></i></div>
					<div style="padding: 2px; float: left;"><%=(String)request.getAttribute("shiftName2") %></div>
					<div style="padding: 2px; float: left;"><i class="fa fa-long-arrow-right" title="Next"></i></div>
					<div style="padding: 2px; float: left;"><%=(String)request.getAttribute("shiftName3") %></div>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignCenter" colspan="2">Remaining employee to be assigned to &nbsp; <b><%=(String)request.getAttribute("shiftNameOther") %></b> &nbsp;shift.</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">Minimum No. of members in Shift:</td>
				<td><%=(String)request.getAttribute("minNoofMemberInShift") %></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Minimum No. of members in Shift on the Weekend:</td>
				<td><%=(String)request.getAttribute("minNoofMemberInShiftOnWeekend") %></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignCenter" colspan="2">No. of leaders <b><%=(String)request.getAttribute("minNoofLeadersInShift") %></b> and their levels <b><%=(String)request.getAttribute("strLevelNames") %></b> for No. of members <b><%=(String)request.getAttribute("minNoofMemberWithLeadersInShift") %></b>.</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">No. of Weekend off in a Month:</td>
				<td><%=(String)request.getAttribute("noofWeekendOffInMonth") %></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Max. No. of different shifts assign to members in a Month:</td>
				<td><%=(String)request.getAttribute("maxNoofShiftsAssignInMonth") %></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Min. No. of days break in stretch shift:</td>
				<td><%=(String)request.getAttribute("minNoofDaysBreakInStretchShift") %></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Min. No. of days week off between change shift:</td>
				<td><%=(String)request.getAttribute("minNoofDaysWeekOffbetweenChangeShift") %></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignCenter" colspan="2">Members in Work Location <b><%=(String)request.getAttribute("strWLocation") %></b> to be combined with <b><%=(String)request.getAttribute("strWLocation") %></b> Work Location.</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Min. No. of male members in a shift:</td>
				<td><%=(String)request.getAttribute("minNoofMaleMemberInShift") %></td>
			</tr>
			
		</table>
	<% } %>
	
	
	<%-- <s:form theme="simple" id="frmAddRosterPolicyRules" name="frmAddRosterPolicyRules" action="AddRosterPolicyRules" method="POST" cssClass="formcss">
		<s:hidden name="rPolicyRuleId"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight">Roster Policy Rule Type:<sup>*</sup></th>
				<td>
					<s:select name="rosterPolicyRuleType" id="rosterPolicyRuleType" headerKey="" headerValue="Select Rule Type" listKey="rosterPolicyRuleId" listValue="rosterPolicyRuleName" 
						list="rosterPolicyRuleList" key="" required="true" cssClass="validateRequired" onchange="showHideTR(this.value);"/>
				</td>
			</tr>
			
			<tr id="trShifts" style="display: none;">
				<th class="txtlabel alignRight">Shifts:<sup>*</sup></th>
				<td><s:select name="shiftName" id="shiftName" listKey="shiftId" listValue="shiftCode" list="shiftList" key="" required="true" cssClass="validateRequired"/></td>
			</tr>
			
			<tr id="trMultiShifts" style="display: none;">
				<th class="txtlabel alignRight">Shifts:<sup>*</sup></th>
				<td><s:select name="multiShiftName" id="multiShiftName" listKey="shiftId" listValue="shiftCode" list="shiftList" multiple="true" key="" required="true" cssClass="validateRequired"/></td>
			</tr>
			
			<tr id="trNoOfDays" style="display: none;"> 
				<th class="txtlabel alignRight">No. of Days:<sup>*</sup></th>
				<td><s:textfield name="noOfDays" id="noOfDays" cssClass="validateRequired"/></td>
			</tr>
			
			<tr id="trGender" style="display: none;">
				<th class="txtlabel alignRight">Gender:<sup>*</sup></th>
				<td><s:select theme="simple" name="empGender" listKey="genderId" listValue="genderName" headerKey="0" headerValue="All" list="empGenderList"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel">&nbsp;</td>
				<td><s:submit cssClass="btn btn-primary" value="Save" name="btnAddNewRowOk" id="btnAddNewRowOk" /></td>
			</tr>
			
		</table>
	</s:form> --%>

