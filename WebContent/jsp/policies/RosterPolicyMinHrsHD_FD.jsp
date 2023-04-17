<%@ page import="com.konnect.jpms.select.FillApproval"%>
<%@ page import="com.konnect.jpms.select.FillInOut"%>
<%@ page import="com.konnect.jpms.select.FillTimeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script>
$("#btnAddNewRowOk").click(function(){
	$(".validateRequired").prop('required',true);
});
$( "#strEffectiveDate" ).datepicker({format: 'dd/mm/yyyy'});
</script>

<div class="box-body">

	<% String exceptionType = (String) request.getAttribute("exceptionType"); %>
	<s:form theme="simple" action="RosterPolicyMinHrsHD_FD" method="POST" cssClass="formcss" id="formAddNewRow">
		<s:hidden name="orgId"></s:hidden>
		<s:hidden name="rosterpolicyHDFDId" />
		<s:hidden name="strWlocation" />
		<s:hidden name="exceptionType" />
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table border="0" class="table">
			
			<tr>
				<td class="txtlabel alignRight">Effective Date:<sup>*</sup></td>
				<td><s:textfield name="strEffectiveDate" id="strEffectiveDate" cssClass="validateRequired" readonly="true"/><span class="hint">Select Effective Date- dd/MM/yyyy<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
			<% String strExceptionMsg = "Full Day"; 
				if(exceptionType != null && exceptionType.equals("HD")) {
					strExceptionMsg = "Half Day";
				}
			%>
				<td class="txtlabel alignRight">Min. Hours for <%=strExceptionMsg %>:<sup>*</sup></td>
				<td>
					<s:textfield name="minHrs" cssClass="validateRequired"/><span class="hint">Specify time in hours.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<td></td>
				<td><s:submit cssClass="btn btn-primary" value="Ok" id="btnAddNewRowOk"/></td>
			</tr>
			
		</table>
	</s:form>

</div>

