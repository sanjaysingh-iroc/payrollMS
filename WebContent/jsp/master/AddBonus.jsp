<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillMonth"%>
<%@page import="com.konnect.jpms.select.FillAmountType"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script>
$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		/* $(".validateRequired").prop('required',true); */
		$("#formAddBonus").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formAddBonus").find('.validateRequired').filter(':visible').prop('required',true);
	});
	$( "#bonusFrom" ).datepicker({format: 'dd/mm/yyyy'});
    $( "#bonusTo" ).datepicker({format: 'dd/mm/yyyy'});
    $("#formAddBonus_bonusPeriod").multiselect().multiselectfilter();
    $("#formAddBonus_salaryHeadId").multiselect().multiselectfilter();
});

function checkBonusSlabType(){
	var strBSType = document.getElementById("bonusSlabType").value;
	document.getElementById("bonusAmountType").value = "%";
	if(strBSType == '1') {
		document.getElementById("trSalaryHeads").style.display = "table-row";
		document.getElementById("divSalHeadList").style.display = "block";
		document.getElementById("divSalHeadListNet").style.display = "none";
		document.getElementById("trSalaryCal").style.display = "table-row";
		document.getElementById("trBonusMax").style.display = "table-row";
		document.getElementById("divBonusAmt").innerHTML = "Bonus Percentage<sup>*</sup>:";
		document.getElementById("trIsBonusCondition").style.display = "table-row";
		document.getElementById("trBonusAmt").style.display = "table-row";
		showBonusCondition();
		//    
		document.getElementById("trBonusMax").style.display = "table-row"; 
		document.getElementById("trBonusMinDays").style.display = "table-row"; 
		document.getElementById("trMonthList").style.display = "table-row";
		document.getElementById("trLimitSalaryHead").style.display = "table-row";
		document.getElementById("trLimitAmt").style.display = "table-row";
		
	} else {
		document.getElementById("trSalaryHeads").style.display = "table-row";
		document.getElementById("divSalHeadList").style.display = "none";
		document.getElementById("divSalHeadListNet").style.display = "block";
		document.getElementById("trSalaryCal").style.display = "table-row";
		document.getElementById("trBonusMax").style.display = "none";
		document.getElementById("divBonusAmt").innerHTML = "Bonus Percentage<sup>*</sup>:";
		document.getElementById("trIsBonusCondition").style.display = "none"; 
		document.getElementById("bonusCondition").checked = false;
		document.getElementById("trBonusAmt").style.display = "table-row";
		showBonusCondition();
		//    
		document.getElementById("trBonusMax").style.display = "none"; 
		document.getElementById("trBonusMinDays").style.display = "none"; 
		document.getElementById("trMonthList").style.display = "none";
		document.getElementById("trLimitSalaryHead").style.display = "none";
		document.getElementById("trLimitAmt").style.display = "none";
	}
}


function checkAmtType(){
	var strType = document.getElementById("bonusAmountType").value;
	var strBSType = document.getElementById("bonusSlabType").value;
	if (strType == '%') {
		if(strBSType == '1') {
			document.getElementById("trSalaryHeads").style.display = "table-row";
			/* $("select[name='salaryHeadId']").prop('required',true); */
			document.getElementById("trSalaryCal").style.display = "table-row";
			/* $("input[name='strSalaryCalculation']").prop('required',true); */
			document.getElementById("trBonusMax").style.display = "table-row";
			/* $("#bonusMax").prop('required',true); */
			// document.getElementById("trEffectiveFY").style.display = "table-row"; 
			document.getElementById("divBonusAmt").innerHTML = "Bonus Percentage<sup>*</sup>:";
			document.getElementById("trIsBonusCondition").style.display = "table-row"; 
			showBonusCondition();
		} else {
			document.getElementById("trSalaryHeads").style.display = "table-row";
			document.getElementById("trSalaryCal").style.display = "table-row";
			document.getElementById("trBonusMax").style.display = "none";
			document.getElementById("divBonusAmt").innerHTML = "Bonus Percentage<sup>*</sup>:";
			document.getElementById("trIsBonusCondition").style.display = "none"; 
			document.getElementById("bonusCondition").checked = false;
			showBonusCondition();
		}
		
	} else {
		if(strBSType == '1') {
			document.getElementById("trSalaryHeads").style.display = "none";
			/* $("select[name='salaryHeadId']").prop('required',false);
			$("select[name='salaryHeadId']").removeClass("validateRequired"); */
			document.getElementById("trSalaryCal").style.display = "none";
			/* $("input[name='strSalaryCalculation']").prop('required',false);
			$("input[name='strSalaryCalculation']").removeClass("validateRequired"); */
			document.getElementById("trBonusMax").style.display = "none";
			/* $("#bonusMax").prop('required',false);
			$("#bonusMax").removeClass("validateRequired"); */
			// document.getElementById("trEffectiveFY").style.display = "none"; 
			document.getElementById("divBonusAmt").innerHTML = "Bonus Amount<sup>*</sup>:";
			document.getElementById("trIsBonusCondition").style.display = "none";
			document.getElementById("trBonusAmt").style.display = "table-row";
			/* $("input[name='bonusAmount']").prop('required',true); */
			if(document.getElementById("bonusCondition")) {
				document.getElementById("trMinMax").style.display = "none";
				/* $("#strMin").prop('required',false);
				$("#strMin").removeClass("validateRequired");
				$("#strMax").prop('required',false);
				$("#strMax").removeClass("validateRequired");
				$("#formAddBonus_condition1").prop('required',false);
				$("#formAddBonus_condition1").removeClass("validateRequired"); */
				document.getElementById("trMinMaxSecond").style.display = "none";
				/* $("#strMax2").prop('required',false);
				$("#strMax2").removeClass("validateRequired");
				$("#formAddBonus_condition2").prop('required',false);
				$("#formAddBonus_condition2").removeClass("validateRequired"); */
			}
	
		} else {
			document.getElementById("trSalaryHeads").style.display = "none";
			document.getElementById("trSalaryCal").style.display = "none";
			document.getElementById("trBonusMax").style.display = "none";
			document.getElementById("divBonusAmt").innerHTML = "Bonus Amount<sup>*</sup>:";
			document.getElementById("trIsBonusCondition").style.display = "none";
			document.getElementById("trBonusAmt").style.display = "table-row";
			if(document.getElementById("bonusCondition")) {
				document.getElementById("trMinMax").style.display = "none";
				document.getElementById("trMinMaxSecond").style.display = "none";
			}
		}
	}
}

function showBonusCondition(){
	var bonusCondition = document.getElementById("bonusCondition"); //trBonusAmt
	if(bonusCondition.checked == true){
		document.getElementById("trMinMax").style.display = "table-row";
		/* $("#strMin").prop('required',true);
		$("#strMax").prop('required',true);
		$("#formAddBonus_condition1").prop('required',true); */
		document.getElementById("trMinMaxSecond").style.display = "table-row";
		/* $("#strMax2").prop('required',true);
		$("#formAddBonus_condition2").prop('required',true); */
		document.getElementById("trBonusAmt").style.display = "none";
		/* $("#formAddBonus_bonusAmount").prop('required',false);
		$("#formAddBonus_bonusAmount").removeClass("validateRequired"); */
	} else {
		document.getElementById("trMinMax").style.display = "none";
		/* $("#strMin").prop('required',false);
		$("#strMin").removeClass("validateRequired");
		$("#strMax").prop('required',false);
		$("#strMax").removeClass("validateRequired");
		$("#formAddBonus_condition1").prop('required',false);
		$("#formAddBonus_condition1").removeClass("validateRequired"); */
		document.getElementById("trMinMaxSecond").style.display = "none";
		/* $("#strMax2").prop('required',false);
		$("#strMax2").removeClass("validateRequired"); */
		/* $("#formAddBonus_condition2").prop('required',false);
		$("#formAddBonus_condition2").removeClass("validateRequired"); */
		document.getElementById("trBonusAmt").style.display = "table-row";
		/* $("#formAddBonus_bonusAmount").prop('required',true); */
	}
}

function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
       return false;
    }
    return true;
 }

</script> 


	<s:form theme="simple" id="formAddBonus" action="AddBonus" method="POST" cssClass="formcss" >
		<s:hidden name="bonusId"></s:hidden>
		<s:hidden name="orgId"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		<s:hidden name="financialYear" />
		<input type="hidden" name="bonusLevel" value="<%=request.getParameter("param")%>"/>
		<input type="hidden" name="financialYearFrom" value="<%=request.getParameter("FYS")%>"/>
		<input type="hidden" name="financialYearTo" value="<%=request.getParameter("FYE")%>"/>
	<% 
			UtilityFunctions uF = new UtilityFunctions();
			String bonusSlabType = (String) request.getAttribute("bonusSlabType");
			String strAmtType = (String) request.getAttribute("bonusAmountType");
			String strBonusCondition = (String) request.getAttribute("bonusCondition");
			String strTrDisplay = "none;";
			String strBonusAmt = "Bonus Amount";
			String strTrConditionDisplay = "none;";
			String strTrBonusAmtDisplay = "table-row;";
			String strTrSlab1Display = "table-row;";
			String strDivSalHead = "block";
			String strDivSalHeadNet = "none";
			if (bonusSlabType!=null && bonusSlabType.trim().equals("2")) {
				strTrSlab1Display = "none";
				strDivSalHead = "none";
				strDivSalHeadNet = "block";
			}
				
			if (strAmtType!=null && strAmtType.trim().equals("%")){
				strTrDisplay = "table-row;";
				strBonusAmt = "Bonus Percentage";
				if(uF.parseToBoolean(strBonusCondition)){
					strTrConditionDisplay = "table-row;";
					strTrBonusAmtDisplay = "none;";
				}
			}
		%>
	<table class="table table_no_border">
		<tr>
			<td class="txtlabel alignRight">Bonus Slab Type:<sup>*</sup></td>
			<td>
				<s:select theme="simple" name="bonusSlabType" id="bonusSlabType" cssClass="validateRequired" headerKey="1" headerValue="Slab Type 1" list="#{'2':'Slab Type 2'}" onchange="checkBonusSlabType();"/>
			</td>
		</tr>
		<tr>
			<td class="txtlabel alignRight">Bonus Amount Type:<sup>*</sup></td>
			<td>
				<s:select theme="simple" name="bonusAmountType" id="bonusAmountType" list="amountTypeList" listKey="amountTypeId" listValue="amountTypeName" cssClass="validateRequired" onchange="checkAmtType();"/>
			</td>
		</tr>
		
		<tr id="trSalaryHeads" style="display: <%=strTrDisplay %>">
			<td class="txtlabel alignRight" valign="top">Salary Heads:<sup>*</sup></td>
			<td><div id="divSalHeadList" style="display: <%=strDivSalHead %>">
					<s:select theme="simple" list="salaryHeadList" cssClass="validateRequired" listKey="salaryHeadId" listValue="salaryHeadName" name="salaryHeadId" size="5" multiple="true"/>
				</div>
				<div id="divSalHeadListNet" style="display: <%=strDivSalHeadNet %>">
					<s:select theme="simple" list="salaryHeadListNet" cssClass="validateRequired" listKey="salaryHeadId" listValue="salaryHeadName" name="salaryHeadNetId"/>
				</div>
			</td>
		</tr>
		
		<tr id="trSalaryCal" style="display: <%=strTrDisplay %>">
			<td class="txtlabel alignRight">Salary Calculation:<sup>*</sup></td>
			<td>
				<s:radio name="strSalaryCalculation" list="#{'1':'Current Month','2':'Cumulative','3':'Previous Year'}"></s:radio>
			</td>
		</tr>
		
		<tr id="trIsBonusCondition" style="display: <%=strTrDisplay %>">
			<td valign="top" class="txtlabel alignRight">&nbsp;</td>
			<td><s:checkbox name="bonusCondition" id="bonusCondition" onclick="showBonusCondition()"/> if Bonus Calculation Condition</td>
		</tr> 
		
		<tr id="trMinMax" style="display: <%=strTrConditionDisplay %>">
			<td class="txtlabel alignRight">1.</td>
			<td class="txtlabel">
				Min:<sup>*</sup>&nbsp;<s:textfield name="strMin" id="strMin" cssClass="validateRequired" cssStyle="text-align: right; width:50px !important;" onkeypress="return isNumberKey(event)"/>&nbsp;
				Max:<sup>*</sup>&nbsp;<s:textfield name="strMax" id="strMax" cssClass="validateRequired" cssStyle="text-align: right; width:50px !important;" onkeypress="return isNumberKey(event)"/>&nbsp;
				%:<sup>*</sup>&nbsp;<s:textfield name="condition1" cssClass="validateRequired" cssStyle="text-align: right; width: 26px !important;" onkeypress="return isNumberKey(event)"/>
			</td>
		</tr>
		<tr id="trMinMaxSecond" style="display: <%=strTrConditionDisplay %>">
			<td class="txtlabel alignRight">2.</td>
			<td class="txtlabel">
				Max:<sup>*</sup>&nbsp;<s:textfield name="strMax2" id="strMax2" cssClass="validateRequired" cssStyle="text-align: right; width:50px !important;" onkeypress="return isNumberKey(event)"/>&nbsp;
				%:<sup>*</sup>&nbsp;<s:textfield name="condition2" cssClass="validateRequired" cssStyle="text-align: right; width: 26px !important;" onkeypress="return isNumberKey(event)"/>
			</td>
		</tr>
		
		<tr id="trBonusAmt" style="display: <%=strTrBonusAmtDisplay %>">
			<td class="txtlabel alignRight">
				<div id="divBonusAmt"><%=strBonusAmt %>:<sup>*</sup></div>
			</td>
			<td>
				<s:textfield name="bonusAmount" cssStyle="text-align: right; width:75px;" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/> 
			</td>
		</tr>
		
		<tr id="trBonusMax" style="display: <%=strTrDisplay %>">
			<td class="txtlabel alignRight">Bonus Maximum:<sup>*</sup></td>
			<td>
				<s:textfield name="bonusMax" id="bonusMax" cssStyle="text-align: right; width:75px;" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/> 
			</td>
		</tr> 
		
		<%-- <tr id="trEffectiveFY" style="display: <%=strTrDisplay %>">
			<td class="txtlabel alignRight">Year<sup>*</sup>:</td>
			<td>
				<s:radio name="strEffectiveFY" list="#{'1':'Current FY','2':'Previous FY'}"></s:radio>
			</td>
		</tr> --%>
		
		<tr id="trBonusMinDays" style="display: <%=strTrSlab1Display %>">
			<td class="txtlabel alignRight">Bonus Eligibility Days:<sup>*</sup></td>
			<td>
				<s:textfield name="bonusMinDays" id="bonusMinDays" cssStyle="text-align: right; width:75px;" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
			</td>
		</tr> 
		
		<tr id="trMonthList" style="display: <%=strTrSlab1Display %>">
			<td class="txtlabel alignRight">Payable Bonus Period:<sup>*</sup></td>
			<td>
				<s:select theme="simple" list="monthList" listKey="monthId" listValue="monthName" cssClass="validateRequired"  name="bonusPeriod" size="5" multiple="true"/>
			 
				<span class="hint">Bonus Period<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr id="trLimitSalaryHead" style="display: <%=strTrSlab1Display %>">
			<td class="txtlabel alignRight">Limit Salary Head:</td>
			<td>
				<s:select theme="simple" name="limitSalaryHead" id="limitSalaryHead" headerKey="0" headerValue="Select Salary Head"
				 list="salaryHeadList1" listKey="salaryHeadId" listValue="salaryHeadName"/>
				<span class="hint">Limit Salary Heads<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr id="trLimitAmt" style="display: <%=strTrSlab1Display %>">
			<td class="txtlabel alignRight">Limit Salary Head Amount:</td>
			<td>
				<s:textfield name="limitAmt" id="limitAmt" cssStyle="text-align: right; width:75px;" onkeypress="return isNumberKey(event)"/>
			</td>
		</tr> 
		
		<tr>
			<td></td>
			<td>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk" /> 
			</td>
		</tr>


	</table>
</s:form>

<script type="text/javascript">
checkAmtType();
</script> 
