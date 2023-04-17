<%@ page import="com.konnect.jpms.select.FillApproval"%>
<%@ page import="com.konnect.jpms.select.FillInOut"%>
<%@ page import="com.konnect.jpms.select.FillTimeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script> 

jQuery(document).ready(function(){
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	$( "#strEffectiveDate" ).datepicker({format: 'dd/mm/yyyy'});
});
</script>


<div class="box-body">

	<s:form theme="simple" action="RosterPolicyFD" method="POST" cssClass="formcss" id="formAddNewRow">
		<s:hidden name="orgId"></s:hidden>
		<s:hidden name="rosterpolicyFDId" />
		<s:hidden name="strWlocation"></s:hidden>   
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table border="0" class="table">
			<tr><td colspan=2><s:fielderror/></td></tr>   
			
			<tr>
				<td class="txtlabel alignRight">Effective Date:<sup>*</sup></td>
				<td><s:textfield name="strEffectiveDate" id="strEffectiveDate" cssClass="validateRequired" readonly="true"/><span class="hint">Effective Date.<span class="hint-pointer">&nbsp;</span></span> dd/MM/yyyy</td>
			</tr>
			
			<tr>
				<td class="txtlabel" colspan="2">Full day policy is applicable if:</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">IN-Late/OUT-Early:<sup>*</sup></td>
				<td>
					<s:select list="in_out_List" name="in_out" listKey="in_out_Id" listValue="in_out_Name" cssClass="validateRequired" ></s:select>			
					<span class="hint">Is this policy applicable for coming in or going out?<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Time (in mins):<sup>*</sup></td>
				<td><s:textfield name="timeValue" cssClass="validateRequired"/><span class="hint">Specify time in minutes.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">In Days:<sup>*</sup></td>
				<td><s:textfield name="dayValue" cssClass="validateRequired"/><span class="hint">Specify days.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">of Month:<sup>*</sup></td>
				<td><s:textfield name="monthValue" cssClass="validateRequired"/><span class="hint">Specify months.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
				
			<tr>
				<td></td>
				<td><s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/></td>
			</tr>
		</table>
	</s:form>
</div>