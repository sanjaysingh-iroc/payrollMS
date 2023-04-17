<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillSalaryHeads"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
jQuery(document).ready(function(){
	$("#submitButton").click(function(){
    	$(".validateRequired").prop('required',true);
    });
}); 

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function checkLocalType(){
	var localType = document.getElementById("localType").value;
	if(parseInt(localType) == 1){
		document.getElementById("trTransportType").style.display = 'table-row';
		document.getElementById("trLocalLimitType").style.display = 'table-row';
		document.getElementById("trRequireApproval").style.display = 'table-row';
		document.getElementById("limitLabel").innerHTML='per km/mile';
	} else if(parseInt(localType) == 2){
		document.getElementById("trTransportType").style.display = 'none';
		document.getElementById("trLocalLimitType").style.display = 'table-row';
		document.getElementById("trRequireApproval").style.display = 'table-row';
		document.getElementById("limitLabel").innerHTML='pm';
		
	} else if(parseInt(localType) == 3){
		document.getElementById("trTransportType").style.display = 'table-row';
		document.getElementById("trLocalLimitType").style.display = 'table-row';
		document.getElementById("trRequireApproval").style.display = 'table-row';
		document.getElementById("limitLabel").innerHTML='per km/mile';
	} else {
		document.getElementById("trTransportType").style.display = 'none';
		document.getElementById("trLocalLimitType").style.display = 'none';
		//document.getElementById("trLimit").style.display = 'none';
		document.getElementById("trRequireApproval").style.display = 'none';
		document.getElementById("trMinMax").style.display = 'none';
		document.getElementById("limitLabel").innerHTML='';
	}
	checkLimitType();
	checkRequireApproval();
}

function checkLimitType(){
	var localLimitType = document.getElementById("localLimitType").value;
	
	if(parseInt(localLimitType) == 2){
		document.getElementById("trLimit").style.display = 'table-row';
	}else {
		document.getElementById("trLimit").style.display = 'none';
	}
}

function checkRequireApproval(){
	var requireApproval = document.getElementById("requireApproval");
	if(requireApproval.checked==true){
		document.getElementById('trMinMax').style.display='table-row';
	}else{
		document.getElementById('trMinMax').style.display='none';
	}
}

</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
%>
	
<div>
	<s:form theme="simple" name="formAddLocalReimbursement" id="formAddLocalReimbursement" action="AddLocalReimbursement" method="POST" cssClass="formcss">
		<s:hidden name="reimbPolicyId"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="strLevel"></s:hidden>
		<s:hidden name="userscreen" id="userscreen"/>
		<s:hidden name="navigationId" id="navigationId"/>
		<s:hidden name="toPage" id="toPage"/>
		
		<table class="table table_no_border">
			<tr>
				<td class="alignRight" style="width: 40%;">Default Policy:</td> 
				<td><s:checkbox name="localDefaultPolicy" value="localDefaultPolicyValue"></s:checkbox></td>
			</tr>
			<tr>
				<td class="alignRight">Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="localType" id="localType" list="#{'1':'Local Conveyance','2':'Food & Beverage (Non-Alcoholic)','3':'Local Travel'}" cssClass="validateRequired form-control" onchange="checkLocalType();"/>
				</td>
			</tr>
			
			<tr id="trTransportType" style="display: none;">
				<td class="alignRight">Transportation Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="transportType" id="transportType" listKey="typeId" cssClass="validateRequired form-control" listValue="typeName" headerKey="" headerValue="Select Mode"		
						list="transportTypeList" key="" required="true" />					
				</td>
			</tr>
			
			<tr id="trLocalLimitType" style="display: none;"> 
				<td class="alignRight">Limit Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="localLimitType" id="localLimitType" list="#{'1':'No Limit','2':'Actual'}" cssClass="validateRequired form-control" onchange="checkLimitType();"/>
				</td>
			</tr>
			
			<tr id="trLimit" style="display: none;">
				<td class="alignRight">Limit:<sup>*</sup></td> 
				<td><s:textfield name="localLimit" id="localLimit" cssClass="validateRequired form-control" onkeypress="return isNumberKey(event)" cssStyle="text-align:right; width:72px;"></s:textfield>
				&nbsp;<span id="limitLabel"></span></td>
			</tr>
			
			<tr id="trRequireApproval" style="display: none;">
				<td class="alignRight" style="width: 40%;">Require Approval:</td> 
				<td><s:checkbox name="requireApproval" id="requireApproval" value="requireApprovalDefaultValue" onclick="checkRequireApproval();"></s:checkbox></td>
			</tr>
			
			<tr id="trMinMax" style="display: none;">
				<td class="alignRight">Min:</td>
				<td><s:textfield name="strMin" id="strMin" cssStyle="width:50px !important;" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;
					Max:&nbsp;<s:textfield name="strMax" id="strMax" cssStyle="width:50px !important;" onkeypress="return isNumberKey(event)"/>
				</td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td>
					<s:submit value="Save" cssClass="btn btn-primary" name="submit" theme="simple" id="submitButton"/> 
				</td>
			</tr>
		</table>
	</s:form>
</div>

<script type="text/javascript">
checkLocalType();
</script>