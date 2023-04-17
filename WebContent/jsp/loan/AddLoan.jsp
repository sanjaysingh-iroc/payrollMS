<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready( function () {
	$("#btnAddNewLevelOk").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');
		$(".validateNumber").prop('step','any');
	});
});
</script>
 


<s:form theme="simple" id="formID" action="AddLoan" method="POST" cssClass="formcss">
	<s:hidden name="loanId"></s:hidden>
	<s:hidden name="orgId"></s:hidden>
	<s:hidden name="strLevel"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />

	<table class="table table_no_border"> 
		
		<tr>
			<td class="txtlabel alignRight">Loan Code:<sup>*</sup></td>
			<td>
				<s:textfield name="loanCode" id="loanCode" cssClass="validateRequired" /> 
				<span class="hint">Loan Code<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	 
		<tr>
			<td class="txtlabel alignRight">Loan Description:</td>
			<td>
				<s:textfield name="loanDescription" id="loanDescription"/> 
				<span class="hint">Loan Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Minimum Service:</td>
			<td>
				<s:textfield name="minServiceYears" id="minServiceYears"/> 
				<span class="hint">Minimum Service in Years<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Rate of Interest (%):</td>
			<td>
				<s:textfield name="loanInterest" id="loanInterest"/> 
				<span class="hint">Rate of Interest (%)<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Fine Amount:</td>
			<td>
				<s:textfield name="fineAmount" id="fineAmount"/> 
				<span class="hint">Fine Amount<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Times Earning Salary:<sup>*</sup></td>
			<td>
				<s:textfield name="timesSalary" id="timesSalary" cssClass="validateRequired validateNumber"/> 
				<span class="hint">Times Earning Salary<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Is Check Previous Loan:</td>
			<td>
				<s:checkbox name="isCheckPreviousLoan" id="isCheckPreviousLoan"/> 
				<span class="hint">Is Check Previous Loan<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		
		<tr>
			<td></td>
			<td>
				<s:submit cssClass="btn btn-primary" value="Save Policy" id="btnAddNewLevelOk" /> 
			</td>
		</tr>

	</table>
	
</s:form>