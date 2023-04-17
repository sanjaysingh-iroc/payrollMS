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
	$("#submitButton").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');	 
	});
});	
addLoadEvent(prepareInputsForHints);

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
} 


</script>

<s:form theme="simple" name="formAddExGratiaSlab" id="formAddExGratiaSlab" action="AddExGratiaSlab" method="POST" cssClass="formcss">

	<s:hidden name="gratiaSlabId" id="gratiaSlabId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	<s:hidden name="toTab" />
	
		<table class="table table_no_border">
		
		<tr>
			<td class="txtlabel alignRight">Ex Gratia Slab:<sup>*</sup></td>
			<td>
				<s:textfield name="exGratiaSlab" id="exGratiaSlab" cssClass="validateRequired" /> 
				<span class="hint">Ex Gratia Slab<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	 
		<tr>
			<td class="txtlabel alignRight">Slab From:<sup>*</sup></td> 
			<td>
				<s:textfield name="slabFrom" id="slabFrom" cssClass="validateRequired validateNumber" cssStyle="width: 141px;" onkeypress="return isNumberKey(event)" /> 
				<span class="hint">Slab From<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Slab To:<sup>*</sup></td>
			<td>
				<s:textfield name="slabTo" id="slabTo" cssClass="validateRequired validateNumber" cssStyle="width: 141px;" onkeypress="return isNumberKey(event)" /> 
				<span class="hint">Slab To<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Slab Percentage:<sup>*</sup></td> 
			<td>
				<s:textfield name="slabPercentage" id="slabPercentage" cssClass="validateRequired validateNumber" cssStyle="width: 40px;" onkeypress="return isNumberKey(event)" /> 
				<span class="hint">Slab Percentage<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<s:submit cssClass="btn btn-primary" value="Save" id="submitButton"/> 
			</td>
		</tr>
	</table>
</s:form>

