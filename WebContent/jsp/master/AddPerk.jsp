<%@page import="com.konnect.jpms.select.FillPerkPaymentCycle"%>
<%@page import="com.konnect.jpms.select.FillPerkType"%>
<%@page import="com.konnect.jpms.select.FillDesig"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');
		$(".validateNumber").prop('step','any');
	});
});
</script>
 
<s:form theme="simple" id="formAddNewRow" action="AddPerk" method="POST" cssClass="formcss">
	<s:hidden name="perkId"></s:hidden>
	<s:hidden name="orgId"></s:hidden>
	<s:hidden name="perklevel"></s:hidden>
	<s:hidden name="financialYear"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table border="0" class="table table_no_border">
		
		<tr>
			<td class="txtlabel alignRight">Perk Code:<sup>*</sup></td>
			<td>
				<s:textfield name="perkCode" id="perkCode"  cssClass="validateRequired" /> 
				<span class="hint">Perk Code<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight">Perk Name:<sup>*</sup></td>
			<td>
				<s:textfield name="perkName" id="perkName"  cssClass="validateRequired" /> 
				<span class="hint">Perk Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Perk Description:</td>
			<td>
				<s:textfield name="perkDesc" id="perkDesc" /> 
				<span class="hint">Perk Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Max Amount:<sup>*</sup></td>
			<td>
				<s:textfield name="perkMaxAmount" id="perkMaxAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/> 
				<span class="hint">Perk Max Amount<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<%-- <tr>
			<td class="txtlabel alignRight">Perk Type:</td>
			<td>
			<s:select list="perkTypeList" listKey="perkTypeId" listValue="perkTypeName" name="perkType"></s:select>
			<span class="hint">Perk Type<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr> --%>  
		
		<tr>
			<td class="txtlabel alignRight">Perk Payment Cycle:</td>
			<td>
			<s:select list="perkPaymentCycleList" listKey="perkPaymentCycleId" listValue="perkPaymentCycleName" name="perkPaymentCycle"></s:select>
			<span class="hint">Perk Payment Cycle<span class="hint-pointer">&nbsp;</span></span>
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

