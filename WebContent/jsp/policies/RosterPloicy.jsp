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
	/* addLoadEvent(prepareInputsForHints); */
</script>
 
<div class="box-body">

	<s:form theme="simple" action="RosterPolicy" method="POST" cssClass="formcss" id="formAddNewRow">
		<s:hidden name="orgId"></s:hidden>
		<s:hidden name="rosterpolicyId" />
		<s:hidden name="strWlocation"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table border="0" class="table table_no_border">
			<tr><td colspan=2><s:fielderror/></td></tr>
			
			<tr> 
				<th class="txtlabel alignRight">Effective Date:<sup>*</sup></th>
				<td><s:textfield name="strEffectiveDate" id="strEffectiveDate" cssClass="validateRequired"/><span class="hint">Effective Date.<span class="hint-pointer">&nbsp;</span></span> dd/MM/yyyy</td>
			</tr>   
			
			<tr>
				<th class="txtlabel alignRight">Upto (min):<sup>*</sup></th>
				<td><s:textfield name="timeValue" cssClass="validateRequired"/><span class="hint">Specify time in minutes.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">Display Message:<sup>*</sup></th>
				<td><s:textfield name="textMsg" cssClass="validateRequired"/><span class="hint">What message you wish to display for the employee when he clock in/out?<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Type:</th>
				<td>
					<s:select list="timeTypeList" name="timeType" listKey="timeTypeId" listValue="timeTypeName" ></s:select>
					
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">IN/OUT:</th>
				<td>
					<s:select list="in_out_List" name="in_out" listKey="in_out_Id" listValue="in_out_Name" ></s:select>			
					<span class="hint">Is this policy applicable for coming in or going out?<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>	
				
			<tr>
				<th class="txtlabel alignRight" valign="top">Need Approval:</th>
				<td>
					<s:select list="approvalList" name="approval" listKey="approvalId" listValue="approvalName" ></s:select>
					<br/><font size="2">(If 'Yes' then exception generated.)</font>
					<span class="hint"><span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>	
			
			<tr>
				<td></td>
				<td><s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
				<%-- <s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/> --%>
				</td>
			</tr>
			
		</table>
	</s:form>

</div>

