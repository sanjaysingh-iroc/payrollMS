<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillApproval"%>
<%@page import="com.konnect.jpms.select.FillLeaveType"%>
<%@page import="com.konnect.jpms.select.FillUserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div class="aboveform">

<script type="text/javascript"> 

function cdate(){ 
	 
	 //alert(document.forms['frmleavedate'].approvalDate[i].value);
	 if(document.forms['frmleavedate'].approvalDate[i].value=="CD"){
		 //alert(a);
		  document.getElementById("other").style.display="block";
	 } 
	
}	 


function showPolicy(val) {
	//alert("sdgmksdfg");
	var action = 'GetPolicyDetailsByAjax.action?policyid='+val;
	alert(action);
	var el = document.getElementById("ifrmpolicy");
	el.setAttribute('src', action);
} 

function calculateLeave(val){
	var var1 = (parseFloat(val) * 12);
	document.getElementById('idnoOfLeaveAnnually').value = var1.toFixed(1);;
}
</script>
<%
	List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
	String policy_id = (String) request.getAttribute("policy_id");
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
%>

<s:form theme="simple" name="frmleavedate" action="EmployeeIssueLeaveBreak" id="formAddNewRow" method="get" cssClass="formcss" >
<div style="float: left;">
	<table border="0" class="formcss" style="width:675px">
	
	
		<tr><td colspan=2><s:fielderror/></td></tr>
		
		
		<tr><td>
		<s:hidden name="empBreakTypeId" />
		<s:hidden name="orgId" />
		<s:hidden name="strLocation" />
		</td></tr>
		<tr><td height="10px">&nbsp;</td></tr>
		
		<tr>
		
			<td class="txtlabel alignRight">Break Type:<sup>*</sup></td>
				<td>
 					<s:select name="typeOfbreak" cssClass="validateRequired text-input" listKey="breakTypeId" listValue="breakTypeName" headerKey="0" headerValue="Select Break Type" list="empBreakTypeList" key="" required="true" />
 					<span class="hint">Select Break Type.<span class="hint-pointer">&nbsp;</span></span>
				</td>
		</tr>
		
		
		<tr>
			<td class="txtlabel alignRight">Level Type:<sup>*</sup></td>
			<td>
				<s:select name="levelType" listKey="levelId" cssClass="validateRequired text-input" listValue="levelCodeName" headerKey="0" headerValue="Select Level" list="levelTypeList" key="" required="true" />
				<span class="hint">Select type of level.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		
		<tr id="idNoOfLeave">
			<td class="txtlabel alignRight">No Of Breaks (Monthly):<sup>*</sup></td>
			<td><s:textfield name="noOfLeaveMonthly" required="true" cssClass="validateRequired text-input"/></td>
		</tr>
		
		
		<tr>
			<td class="txtlabel alignRight">Accrual System:<sup>*</sup></td>
			<td><s:radio name="accrualSystem" cssClass="validateRequired" list="#{'1':'Begining of month','2':'End of month'}"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Accrual From:<sup>*</sup></td>
			<td><s:radio name="accrualFrom" cssClass="validateRequired" list="#{'1':'Joining Date','2':'Probation Date'}"/></td>
		</tr>
		
		
		<%-- <tr id="idLeaveCalculation">
			<td class="txtlabel alignRight">Leave Calculation:<sup>*</sup></td>
			<td>
				<s:select name="approvalDate" label="Break Calculation" cssClass="validateRequired text-input" listKey="approvalId" listValue="approvalName" headerKey="0" headerValue="Select Approval Type" list="approvalList" key="" required="true" onchange="cdate()" /><span class="hint">Select Approval Type.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr> --%>

		<%-- <tr>
			<td class="txtlabel alignRight">Paid/Unpaid:</td>
		
			<td>
				<s:checkbox cssStyle="width:10px" name="ispaid" label="Is Paid" required="false"/>
			</td>
		</tr> --%>
		<!-- <tr>
		
			<td class="txtlabel alignRight">Is Carry forward:</td>
			<td>
				<s:checkbox cssStyle="width:10px" name="isCarryForward" label="Is Carry forward" required="false" />
			</td>
		</tr>  -->
		<tr>
			<td><input type="hidden" name="entryDate" rel="6"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Is Monthly Carry forward:<sup>*</sup></td>
			<td><s:checkbox cssStyle="width:10px" name="isMonthlyCarryForward" cssClass="validateRequired text-input" /></td>
		</tr>
		
		<%-- <tr>
			<td class="txtlabel alignRight">Does this need approval?</td>
			<td><s:checkbox cssStyle="width:10px" name="isApproval" /></td>
		</tr>
		 --%>
		<tr>
			<td colspan="2" align="center"><s:submit cssClass="input_button" value="Save" id="btnAddNewRowOk"/>
		</tr>
	
	</table>
</div>

</s:form>

</div>

				