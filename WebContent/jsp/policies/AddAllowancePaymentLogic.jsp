<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
$(function () {
	$("input[name='btnSubmit']").click(function(){
		$("#formAddAllowancePaymentLogic").find('.validateRequired').filter(':hidden').prop('required',false);
	    $("#formAddAllowancePaymentLogic").find('.validateRequired').filter(':visible').prop('required',true);
	});
	$("input[name='btnSubmitPublish']").click(function(){
		$("#formAddAllowancePaymentLogic").find('.validateRequired').filter(':hidden').prop('required',false);
	    $("#formAddAllowancePaymentLogic").find('.validateRequired').filter(':visible').prop('required',true);
	});
	$( "#idEffectiveDate" ).datepicker({format: 'dd/mm/yyyy'});
});

function getAllowancePaymentLogic(conditionId) {
	var action='GetAllowancePaymentLogicType.action?conditionId='+conditionId ;
	getContent('paymentLogicDiv', action);
}

function showAllowancePaymentLogic(val){
	if(parseInt(val) == parseInt(""+<%=IConstants.A_FIXED_ONLY_ID %>) || parseInt(val) == parseInt(""+<%=IConstants.A_FIXED_X_DAYS_ID %>) || parseInt(val) == parseInt(""+<%=IConstants.A_FIXED_X_HOURS_ID %>) || parseInt(val) == parseInt(""+<%=IConstants.A_FIXED_X_CUSTOM_ID %>) || parseInt(val) == parseInt(""+<%=IConstants.A_FIXED_X_ACHIEVED_ID %>)) {
		document.getElementById('trFixedAmt').style.display = "table-row";
		$("#strFixedAmt").prop('required', true);
		document.getElementById('trSalaryHead').style.display = "none";
		$("#strCalsalaryHead").prop('required', false);
		$("#strCalsalaryHead").removeClass("validateRequired");
		document.getElementById('trPerHourAmt').style.display = "none";
		
		$("#strPerHourDayAmt").prop('required', false);
		$("#strPerHourDayAmt").removeClass("validateRequired");
		document.getElementById('strPerHourDayAmtLabelId').innerHTML='';
		document.getElementById('trDeductFullAmount').style.display = "none";
	} else if(parseInt(val) == parseInt(""+<%=IConstants.A_EQUAL_TO_SALARY_HEAD_ID %>) || parseInt(val) == parseInt(""+<%=IConstants.A_SALARY_HEAD_X_DAYS_ID %>) || parseInt(val) == parseInt(""+<%=IConstants.A_SALARY_HEAD_X_HOURS_ID %>) || parseInt(val) == parseInt(""+<%=IConstants.A_SALARY_HEAD_X_CUSTOM_ID %>) || parseInt(val) == parseInt(""+<%=IConstants.A_SALARY_HEAD_X_ACHIEVED_ID %>)) {
		document.getElementById('trFixedAmt').style.display = "none";
		$("#strFixedAmt").prop('required', false);
		$("#strFixedAmt").removeClass("validateRequired");
		document.getElementById('trSalaryHead').style.display = "table-row";
		$("#strCalsalaryHead").prop('required', true);
		document.getElementById('trPerHourAmt').style.display = "none";
		$("#strPerHourDayAmt").prop('required', false);
		$("#strPerHourDayAmt").removeClass("validateRequired");
		document.getElementById('strPerHourDayAmtLabelId').innerHTML='';
		document.getElementById('trDeductFullAmount').style.display = "none";
	} else if(parseInt(val) == parseInt(""+<%=IConstants.A_FIXED_AND_PER_HOUR_ID %>)) {
		document.getElementById('trFixedAmt').style.display = "table-row";
		$("#strFixedAmt").prop('required', true);
		document.getElementById('trSalaryHead').style.display = "none";
		$("#strCalsalaryHead").prop('required', false);
		$("#strCalsalaryHead").removeClass("validateRequired");
		document.getElementById('trPerHourAmt').style.display = "table-row";
		$("#strPerHourDayAmt").prop('required', true);
		document.getElementById('strPerHourDayAmtLabelId').innerHTML='Per Hour Amount';
		document.getElementById('trDeductFullAmount').style.display = "none";
	} else if(parseInt(val) == parseInt(""+<%=IConstants.A_FIXED_AND_PER_DAY_ID %>)) {
		document.getElementById('trFixedAmt').style.display = "table-row";
		$("#strFixedAmt").prop('required', true);
		document.getElementById('trSalaryHead').style.display = "none";
		$("#strCalsalaryHead").prop('required', false);
		$("#strCalsalaryHead").removeClass("validateRequired");
		document.getElementById('trPerHourAmt').style.display = "table-row";
		$("#strPerHourDayAmt").prop('required', true);
		document.getElementById('strPerHourDayAmtLabelId').innerHTML='Per Day Amount';
		document.getElementById('trDeductFullAmount').style.display = "none";
	} else if(parseInt(val) == parseInt(""+<%=IConstants.A_FIXED_ONLY_DEDUCTION_ID %>)) {
		document.getElementById('trFixedAmt').style.display = "table-row";
		$("#strFixedAmt").prop('required', true);
		document.getElementById('trSalaryHead').style.display = "none";
		document.getElementById('trPerHourAmt').style.display = "none";
		document.getElementById('strPerHourDayAmtLabelId').innerHTML='';
		document.getElementById('trDeductFullAmount').style.display = "table-row";
	} else {
		document.getElementById('trFixedAmt').style.display = "none";
		$("#strFixedAmt").prop('required', false);
		$("#strFixedAmt").removeClass("validateRequired");
		document.getElementById('trSalaryHead').style.display = "none";
		$("#strCalsalaryHead").prop('required', false);
		$("#strCalsalaryHead").removeClass("validateRequired");
		document.getElementById('trPerHourAmt').style.display = "none";
		$("#strPerHourDayAmt").prop('required', false);
		$("#strPerHourDayAmt").removeClass("validateRequired");
		document.getElementById('strPerHourDayAmtLabelId').innerHTML='';
		document.getElementById('trDeductFullAmount').style.display = "none";
	}
}

function showDefaultAmount(id){ 
	if(document.getElementById(id).checked==true){
		document.getElementById("trFixedAmt").style.display = "none";
	} else {
		document.getElementById("trFixedAmt").style.display = "table-row";
	}
}
 
</script>
 
<s:form theme="simple" id="formAddAllowancePaymentLogic" action="AddAllowancePaymentLogic" method="POST" cssClass="formcss">
	<s:hidden name="paymentLogicId"/>
	<s:hidden name="strOrg" id="strOrg"/>
	<s:hidden name="strLevel" id="strLevel"></s:hidden>
	<s:hidden name="strSalaryHeadId" id="strSalaryHeadId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	<%
		UtilityFunctions uF = new UtilityFunctions();
		String strType = (String) request.getAttribute("strAllowancePaymentLogic");
		String strPublish = (String) request.getAttribute("strPublish");
		Boolean isDeductFullAmount = (Boolean) request.getAttribute("isDeductFullAmount");
		
		String strFixedAmtDisplay = "none";
		String strSalaryHeadDisplay = "none";
		String strPerHourDayAmtDisplay = "none";
		String strPerHourDayAmtLabel = "";
		String strDeductFullAmount = "none";
		if(uF.parseToInt(strType) == IConstants.A_FIXED_ONLY_ID || uF.parseToInt(strType) == IConstants.A_FIXED_X_DAYS_ID || uF.parseToInt(strType) == IConstants.A_FIXED_X_HOURS_ID || uF.parseToInt(strType) == IConstants.A_FIXED_X_CUSTOM_ID || uF.parseToInt(strType) == IConstants.A_FIXED_X_ACHIEVED_ID) {
			strFixedAmtDisplay = "table-row";
			strSalaryHeadDisplay = "none";
			strPerHourDayAmtDisplay = "none";
			strPerHourDayAmtLabel = "";
			strDeductFullAmount = "none";
		} else if(uF.parseToInt(strType) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID || uF.parseToInt(strType) == IConstants.A_SALARY_HEAD_X_DAYS_ID || uF.parseToInt(strType) == IConstants.A_SALARY_HEAD_X_HOURS_ID || uF.parseToInt(strType) == IConstants.A_SALARY_HEAD_X_CUSTOM_ID || uF.parseToInt(strType) == IConstants.A_SALARY_HEAD_X_ACHIEVED_ID) {
			strFixedAmtDisplay = "none";
			strSalaryHeadDisplay = "table-row";
			strPerHourDayAmtDisplay = "none";
			strPerHourDayAmtLabel = "";
			strDeductFullAmount = "none";
		} else if(uF.parseToInt(strType) == IConstants.A_FIXED_AND_PER_HOUR_ID) {
			strFixedAmtDisplay = "table-row";
			strSalaryHeadDisplay = "none";
			strPerHourDayAmtDisplay = "table-row";
			strPerHourDayAmtLabel = "Per Hour Amount";
			strDeductFullAmount = "none";
		} else if(uF.parseToInt(strType) == IConstants.A_FIXED_AND_PER_DAY_ID) {
			strFixedAmtDisplay = "table-row";
			strSalaryHeadDisplay = "none";
			strPerHourDayAmtDisplay = "table-row";
			strPerHourDayAmtLabel = "Per Day Amount";
			strDeductFullAmount = "none";
		} else if(uF.parseToInt(strType) == IConstants.A_FIXED_ONLY_DEDUCTION_ID) {
			if(isDeductFullAmount){
				strFixedAmtDisplay = "none";
			} else {
				strFixedAmtDisplay = "table-row";
			}
			strSalaryHeadDisplay = "none";
			strPerHourDayAmtDisplay = "none";
			strPerHourDayAmtLabel = "";
			strDeductFullAmount = "table-row";
		}
	%>
	<table class="table table_no_border">
		<tr>
			<td class="txtlabel alignRight">Payment Logic Slab:<sup>*</sup></td>
			<td><s:textfield name="strPaymentLogicSlab" id="strPaymentLogicSlab" cssClass="validateRequired"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Effective From:<sup>*</sup></td>
			<td><s:textfield id="idEffectiveDate" name="effectiveDate" cssStyle="width: 75px;" cssClass="validateRequired"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Condition:<sup>*</sup></td>
			<td><s:select name="strAllowanceConditionSlab" cssClass="validateRequired" headerKey="" headerValue="Select Condition Slab"
					list="allowanceConditionSlabList" listKey="conditionId" listValue="conditionName" onchange="getAllowancePaymentLogic(this.value);"></s:select>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Payment Logic:<sup>*</sup></td>
			<td>
				<div id="paymentLogicDiv">
					<s:select name="strAllowancePaymentLogic" cssClass="validateRequired" headerKey="" headerValue="Select Payment Logic"
							list="allowancePaymentLogicList" listKey="paymentLogicId" listValue="paymentLogicName" onchange="showAllowancePaymentLogic(this.value);"></s:select>
				</div>
			</td>
		</tr>
		
		<tr id="trDeductFullAmount" style="display: <%=strDeductFullAmount %>">
			<td class="txtlabel alignRight">Deduct Full Amount:</td>
			<td class="txtlabel"><s:checkbox name="isDeductFullAmount" id="isDeductFullAmount" onclick="showDefaultAmount(this.id);"/></td>
		</tr>
		
		<tr id="trFixedAmt" style="display: <%=strFixedAmtDisplay %>">
			<td class="txtlabel alignRight">Fixed Amount:<sup>*</sup></td>
			<td><s:textfield name="strFixedAmt" id="strFixedAmt" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<tr id="trSalaryHead" style="display: <%=strSalaryHeadDisplay %>">
			<td class="txtlabel alignRight">Salary Head:<sup>*</sup></td>
			<td><s:select theme="simple" name="strCalsalaryHead" id="strCalsalaryHead"  headerKey="" headerValue="Select Salary Head"
				 listKey="salaryHeadId" cssClass="validateRequired" listValue="salaryHeadName" list="salaryHeadList" key="" required="true"/></td>
		</tr>
		
		<tr id="trPerHourAmt" style="display: <%=strPerHourDayAmtDisplay %>">
			<td class="txtlabel alignRight"><span id="strPerHourDayAmtLabelId"><%=strPerHourDayAmtLabel %></span>:<sup>*</sup></td>
			<td><s:textfield name="strPerHourDayAmt" id="strPerHourDayAmt" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<tr>
			<td colspan="2" align="center"> 
				<s:submit cssClass="btn btn-primary" name="btnSubmit" value="Submit" />
				<%-- <% if(!uF.parseToBoolean(strPublish)) { %> --%>
			 		<s:submit cssClass="btn btn-primary" name="btnSubmitPublish" value="Submit & Publish"/>
			 	<%-- <% } %> --%>
			</td>
		</tr>

	</table>
	
</s:form>

