<%@ taglib uri="/struts-tags" prefix="s"%>

<script>
$(function() {
	// binds form submission and fields to the validation engine 
	$("#btnOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	
});

</script>

<s:form action="ProjectPartialInvoicePopup" name="frmAddReason" theme="simple" method="POST">
	<s:hidden name="invoice_format_id"></s:hidden>	
	<table class="table table_bordered">
		<tr>
			<td align="right">Project Amount:</td>
		    <td><%=(String)request.getAttribute("pro_amount") %></td>
		</tr>
		<tr>
			<td align="right">Invoice Amount:</td>
		    <td><%=(String)request.getAttribute("invoice_amount") %></td>
		</tr>
		<tr>
			<td align="right">Balance Amount:</td>
		    <td><%=(String)request.getAttribute("remainAmt") %>&nbsp;&nbsp;<input type="text" name="balancePercentage" id="balancePercentage" class="validateRequired" style="width: 70px !important; text-align:right;" value="<%=(String)request.getAttribute("balancePercentage") %>"/>%</td>
		</tr>
		<tr>
			<td align="right">&nbsp;</td>
		    <td><input type="button" class="btn btn-primary" value="Submit" id="btnOk" onclick="viewProjectPartialInvoice('<%=(String)request.getAttribute("pro_freq_id") %>',this.form.balancePercentage.value,'<%=(String)request.getAttribute("pro_id") %>','<%=(String)request.getAttribute("pro_amount") %>','<%=(String)request.getAttribute("invoice_amount") %>','<%=(String)request.getAttribute("divid") %>','<%=(String)request.getAttribute("invoice_format_id") %>','<%=(String)request.getAttribute("proOPEAmt") %>');"/></td>
		</tr>
	</table>
	
</s:form>