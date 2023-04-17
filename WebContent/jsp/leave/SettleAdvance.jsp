<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%UtilityFunctions uF = new UtilityFunctions(); %>

<script type="text/javascript">

$(function () {
	$("body").on("click","#strApprove",function(){
		$(".validateRequired").prop('required',true);
    });
});

function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;

	   return true;
	}

$("#frmSettleAdvance").submit(function(e){
	e.preventDefault();
	if(confirm('Are you sure, you want to settle this travel transactions?')) {
		var form_data = $("form[name='frmSettleAdvance']").serialize();
		//alert("form_data ===>> " + form_data);
	 	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 	$.ajax({
			url : "SettleAdvance.action",
			data: form_data,
			cache : false,
			success : function(res) {
				$("#divResult").html(res);
			}
		});
	}
});

</script>

	<s:form id="frmSettleAdvance" name="frmSettleAdvance" theme="simple" action="SettleAdvance" method="POST" cssClass="formcss">
		<table class="table table-bordered">
			<tr style="display:none"><td><s:hidden name="strAdvId" /></td></tr>
			<tr>
				<th align="right">Advance Amount: </th>
				<td align="left"><s:div>${strAdvAmount}</s:div></td>
				
				<th align="right">Date of Advance: </th>
				<td align="left"><s:div>${strAdvDate}</s:div></td>
			</tr>
				
			<tr>
				<th align="right">Claim Amount: </th>
				<td align="left" colspan="3"><s:div>${strClaimAmount}</s:div></td>
			</tr>
				
			<tr>
				<th align="right">Eligibility Amount: </th>
				<td align="left" colspan="3"><s:div>${strEligibilityAmount}</s:div></td>
			</tr>
			
			<tr>
				<th align="right">Balance Amount: </th>
				<td align="left" colspan="3"><s:div>${strBalanceAmount}</s:div></td>
			</tr>
				
			<tr>
				<th valign="top" align="right">Manager Comments: </th>
				<td align="left" colspan="3"><s:div>${strMgrComment}</s:div></td>
			</tr>
			
			<tr>
				<th align="right">Approved By: </th>
				<td align="left" nowrap="nowrap"><s:div>${strApprovedBy}</s:div></td>
				
				<th align="right">Date of Approval: </th>
				<td align="left"><s:div>${strApprovedDate}</s:div></td>
			</tr>
			
			<%if(uF.parseToInt((String)request.getAttribute("status"))==1) { %>
			<tr>
				<th align="right">Settlement Amount: </th>
				<td align="left" colspan="3"><s:div>${strSettleAmount}</s:div></td>
			</tr>
			<tr>
				<th align="right" nowrap="nowrap">Balance Settlement Amount(Written Off): </th>
				<td align="left" colspan="3"><s:div>${strBalanceSettleAmount}</s:div></td>
			</tr>
				
			<tr>
				<th valign="top" align="right">Settlement Comments: </th>
				<td align="left" colspan="3"><s:div>${strSettleComment}</s:div></td>
			</tr>
			
			<tr>
				<th align="right">Settled By: </th>
				<td align="left" nowrap="nowrap"><s:div>${strSettledBy}</s:div></td>
				
				<th align="right" nowrap="nowrap">Date of Settlement: </th>
				<td align="left"><s:div>${strSettledDate}</s:div></td>
			</tr>
			
			<% } else { %>	
			<tr>
				<th align="right">Settlement Amount:<sup>*</sup> </th>
				<td align="left" colspan="3"><s:textfield name="strSettleAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
			</tr>
				
			<tr>
				<th valign="top" align="right">Settlement Comments: </th>
				<td align="left" colspan="3"><s:textarea cols="50" rows="5" name="strSettleComment" ></s:textarea></td>
			</tr>
			<tr>
				<td colspan="4" align="center">
				 <s:submit name="strApprove" id="strApprove" cssClass="btn btn-primary" value="Click to Settle"></s:submit>
				</td>
			</tr>
			<% } %>
		</table>
		
	</s:form>
