
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
$(document).ready( function () {
	jQuery("#formAddNewRow").validationEngine();
	$( "#idPaycycleStart" ).datepicker({format: 'dd/mm/yyyy'});
}); 



	function generatePdfNew(){
      var paySlipFormatId = document.getElementById("strSalaryPaySlip").value;
		var url="ReviewPaySlipFormat.action?paySlipFormatId="+paySlipFormatId;
		window.location = url;
	
	}
		
</script>


<s:form theme="simple" id="formAddNewRow" action="AddPaySlipSetting" method="POST" cssClass="formcss">
	<table class="table">
		<s:hidden name="orgId"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		<tr>
			<td valign="top" class="txtlabel alignRight">Salary Slip Format:</td>
			<td>
				<s:select name="strSalaryPaySlip" id="strSalaryPaySlip" listKey="paySlipFormatId" listValue="paySlipFormatName" list="paySlipFormatList" key="" cssStyle="width: 142px;" cssClass="validateRequired"/>
			    <a href="javascript:void(0)" style="text-decoration: none; color: green; border-bottom: 1px solid #008000;" onclick="generatePdfNew();">Preview</a>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" align="center">
				<s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
			</td>
		</tr>

	</table>
	
</s:form>

