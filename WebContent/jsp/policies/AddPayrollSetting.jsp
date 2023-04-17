<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script>
$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	$( "#idPaycycleStart" ).datepicker({format: 'dd/mm/yyyy'});
}); 


function ckeckSalaryCalBasis(val){
	if(val == 'AFD'){
		document.getElementById("fixDayDisplayTr").style.display="table-row";
		$("input[name='strFixDays']").prop('required',true);
	} else{
		$("input[name='strFixDays']").prop('required',false);
		$("input[name='strFixDays']").removeClass("validateRequired");
		document.getElementById("fixDayDisplayTr").style.display="none";
	}
}

</script>

<s:form theme="simple" id="formAddNewRow" action="AddPayrollSetting" method="POST" cssClass="formcss">
	<table class="table">
		<s:hidden name="orgId"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		<tr>
			<td valign="top" class="txtlabel alignRight">Start Pay cycle:<sup>*</sup></td>
			<td><s:textfield name="startPaycycle" id="idPaycycleStart" cssClass="validateRequired"/></td>
		</tr>
	
		<tr> 
			<td valign="top" class="txtlabel alignRight">Display Paycycle:<sup>*</sup></td>
			<td><s:textfield name="displayPaycycle" cssClass="validateRequired"/></td>
		</tr>
		
		<tr>
			<td valign="top" class="txtlabel alignRight">Duration of Paycycle:<sup>*</sup></td>
			<td>
				<s:select label="Select Duration" name="strPaycycleDuration" listKey="paycycleDurationId" listValue="paycycleDurationName" headerKey="" 
				headerValue="Select Duration" list="paycycleDurationList" key=""  cssClass="validateRequired"/>
			</td>
		</tr>
			
		<tr>
			<td valign="top" class="txtlabel alignRight">Salary Calculation Basis:<sup>*</sup></td>
			<td>
				<s:select name="strSalaryCalculation" listKey="salaryCalcId" listValue="salaryCalcName" list="salaryCalculationList" key="" cssClass="validateRequired" onchange="ckeckSalaryCalBasis(this.value);"/>
			</td>
		</tr>
		<%
			String strSalCalBasis = (String) request.getAttribute("strSalaryCalculation");
			String strFixDayDisplay = "none";
			if(strSalCalBasis!=null && strSalCalBasis.equalsIgnoreCase("AFD")){
				 strFixDayDisplay = "table-row";
			}
		%>
		<tr id="fixDayDisplayTr" style="display: <%=strFixDayDisplay %>;"> 
			<td valign="top" class="txtlabel alignRight">Fix Days:<sup>*</sup></td>
			<td><s:textfield name="strFixDays" cssClass=""/></td>
		</tr>
		
		<tr>
			<td colspan="2" align="center">
				<s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
			</td>
		</tr>

	</table>
	
</s:form>

