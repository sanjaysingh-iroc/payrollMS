<%@page import="com.konnect.jpms.select.FillPerkPaymentCycle"%>
<%@page import="com.konnect.jpms.select.FillPerkType"%>
<%@page import="com.konnect.jpms.select.FillDesig"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">
$(document).ready( function () {
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

</script>
 
<s:form theme="simple" id="formAddPerkSalary" action="AddPerkSalary" method="POST" cssClass="formcss">
	<s:hidden name="perkSalaryId"></s:hidden>
	<s:hidden name="orgId"></s:hidden>
	<s:hidden name="levelId"></s:hidden>
	<s:hidden name="salaryHeadId"></s:hidden>
	<s:hidden name="financialYear"></s:hidden>
	<s:hidden name="userscreen" id="userscreen"/>
	<s:hidden name="navigationId" id="navigationId"/>
	<s:hidden name="toPage" id="toPage"/>
	
	<table border="0" class="table table_no_border autoWidth">
		<tr>
			<td class="txtlabel alignRight">Perk Code:<sup>*</sup></td>
			<td>
				<s:textfield name="perkCode" id="perkCode" cssClass="validateRequired" /> 
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight">Perk Name:<sup>*</sup></td>
			<td>
				<s:textfield name="perkName" id="perkName" cssClass="validateRequired" /> 
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Perk Description:</td>
			<td>
				<s:textfield name="perkDesc" id="perkDesc" /> 
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Amount:<sup>*</sup></td> 
			<td>
				<s:textfield name="perkAmount" id="perkAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>  
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Need to apply with documents or receipts:</td>
			<td>
				<s:checkbox name="attachment" id="attachment"/>
			</td>
		</tr> 
		
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Is Optimal:</td>
			<td>
				<s:checkbox name="strIsOptimal" id="strIsOptimal"/>
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