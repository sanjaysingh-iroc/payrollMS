<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
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
$("#submitButton").click(function(){
	$("#formAddReimbursementCTCHead").find('.validateRequired').filter(':hidden').prop('required',false);
    $("#formAddReimbursementCTCHead").find('.validateRequired').filter(':visible').prop('required',true);
});
function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}
function checkFinancialYear(){
	//reimCTCHeadAmount attachment strIsOptimal  hideAmount_ hideAttachment_ hideIsOptimal_
	if(document.getElementById("financialYear")){
		var financialYear = document.getElementById("financialYear").value;
		if(document.getElementById("hideFinancialYear_"+financialYear)){
			document.getElementById("reimCTCHeadAmount").value = document.getElementById("hideAmount_"+financialYear).value;
			if(document.getElementById("hideAttachment_"+financialYear).value == 'Yes'){
				document.getElementById("attachment").checked = true;	
			} else {
				document.getElementById("attachment").checked = false;
			}
			if(document.getElementById("hideIsOptimal_"+financialYear).value == 'Yes'){
				document.getElementById("strIsOptimal").checked = true;	
			} else {
				document.getElementById("strIsOptimal").checked = false;
			}
		} else {
			document.getElementById("reimCTCHeadAmount").value = "";
			document.getElementById("attachment").checked = false;
			document.getElementById("strIsOptimal").checked = false;
		}
	} else {
		document.getElementById("reimCTCHeadAmount").value = "";
		document.getElementById("attachment").checked = false;
		document.getElementById("strIsOptimal").checked = false;
	}
}
</script>
 
<s:form theme="simple" id="formAddReimbursementCTCHead" action="AddReimbursementCTCHead" method="POST" cssClass="formcss">
	<s:hidden name="reimCTCHeadId"></s:hidden>
	<s:hidden name="orgId"></s:hidden>
	<s:hidden name="levelId"></s:hidden>
	<s:hidden name="reimCTCId"></s:hidden>
	<s:hidden name="userscreen" id="userscreen"/>
	<s:hidden name="navigationId" id="navigationId"/>
	<s:hidden name="toPage" id="toPage"/>
	
	<table class="table table_no_border">
		<tr>
			<td class="alignRight">Code:<sup>*</sup></td>
			<td>
				<s:textfield name="reimCTCHeadCode" id="ReimCTCHeadCode" cssClass="validateRequired" /> 
			</td>
		</tr>
	
		<tr>
			<td class="alignRight">Name:<sup>*</sup></td>
			<td>
				<s:textfield name="reimCTCHeadName" id="reimCTCHeadName" cssClass="validateRequired" /> 
			</td>
		</tr>
		
		<tr>
			<td class="alignRight">Description:</td>
			<td>
				<s:textfield name="reimCTCHeadDesc" id="reimCTCHeadDesc"/> 
			</td>
		</tr>
		
		<tr>
			<th colspan="2">Reimbursement CTC Head Details<hr style="border:solid 1px #000"/></th>
		</tr>
		
		<tr> 
			<td class="alignRight" nowrap="nowrap">Financial Year:<sup>*</sup></td>
			<td><s:select theme="simple" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" 
					list="financialYearList" key="" onchange="checkFinancialYear();"/>
			</td> 
		</tr>
		
		<tr>
			<td class="alignRight">Amount:<sup>*</sup></td> 
			<td>
				<s:textfield name="reimCTCHeadAmount" id="reimCTCHeadAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/> 
			</td>
		</tr>
		
		<tr>
			<td class="alignRight" nowrap="nowrap">Need to apply with documents or receipts:</td>
			<td>
				<s:checkbox name="attachment" id="attachment"/> 
			</td>
		</tr>
		
		<tr>
			<td class="alignRight" nowrap="nowrap">Is Optional:</td>
			<td>
				<s:checkbox name="strIsOptimal" id="strIsOptimal"/>
			</td>
		</tr> 
		
		<%
			List<Map<String, String>> alReimHeadAmt = (List<Map<String, String>>)request.getAttribute("alReimHeadAmt");
			if(alReimHeadAmt == null) alReimHeadAmt = new ArrayList<Map<String,String>>();
			if(alReimHeadAmt.size() > 0){
		%>
			<tr>
				<td>&nbsp;</td>
				<td><strong>Financial Year Details</strong></td>
			</tr>
			
			<tr>
				<td class="alignRight">&nbsp;</td>
				<td nowrap="nowrap">
					<%
						for(int i=0; alReimHeadAmt!=null && i < alReimHeadAmt.size(); i++){
							Map<String, String> hmInner = (Map<String, String>) alReimHeadAmt.get(i);
					%>
						<input type="hidden" name="hideFinancialYear_<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>" id="hideFinancialYear_<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>" value="<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>"/>
						<input type="hidden" name="hideAmount_<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>" id="hideAmount_<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>" value="<%=hmInner.get("AMOUNT") %>"/>
						<input type="hidden" name="hideAttachment_<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>" id="hideAttachment_<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>" value="<%=hmInner.get("ATTACHMENT") %>"/>
						<input type="hidden" name="hideIsOptimal_<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>" id="hideIsOptimal_<%=hmInner.get("FINANCIAL_YEAR_DATE_FORMAT") %>" value="<%=hmInner.get("IS_OPTIMAL") %>"/>
						<p> 
							<strong><%=(i+1) %>. Financial Year:</strong>&nbsp;<%=hmInner.get("FINANCIAL_YEAR") %>,&nbsp;
							<strong>Amount:</strong>&nbsp;<%=hmInner.get("AMOUNT") %>,&nbsp;<br/>&nbsp;&nbsp;
							<strong>Need to apply with documents or receipts:</strong>&nbsp;<%=hmInner.get("ATTACHMENT") %>,&nbsp;
							<strong>Is Optional:</strong>&nbsp;<%=hmInner.get("IS_OPTIMAL") %>&nbsp;
						</p>
					<%} %>
				</td>          
			</tr>
		<%} %>
		
		<tr>
			<td colspan="2" align="center"><s:submit value="Save" cssClass="btn btn-primary" name="submit" theme="simple" id="submitButton"/></td>
		</tr>
	</table>
</s:form>
<script type="text/javascript">
checkFinancialYear();
</script>