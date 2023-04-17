<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
	$(function () {
		/* $("#btnAddNewLevelOk").click(function(){
			$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
		}); */
		$( "#idInstrumentDate" ).datepicker({format: 'dd/mm/yyyy'});
		
		$("body").on("click","#btnAddNewLevelOk",function(){
			$("#formPayLoan").find('.validateRequired').filter(':hidden').prop('required',false);
			$("#formPayLoan").find('.validateRequired').filter(':visible').prop('required',true);
	    });
	    
	});

	function calculateAmount(){
		document.frm.loanPaidAmount.value = parseFloat(document.frm.loanAmount.value).toFixed(2) - parseFloat(document.frm.TDSAmount.value).toFixed(2);
	}
	
	function checkTDS(obj){
		
		if(obj.checked){
			document.getElementById('ins3').style.display = 'table-row';
			document.frm.loanPaidAmount.value = parseFloat(document.frm.loanAmount.value).toFixed(2) - parseFloat(document.frm.TDSAmount.value).toFixed(2);
		}else{
			document.getElementById('ins3').style.display = 'none';
			document.frm.loanPaidAmount.value = document.frm.loanAmount.value;
		}
	}

	function showInstrumentInfo(val){
		
		if(val && (val=='Q' || val=='D')){
			document.getElementById('ins1').style.display = 'table-row';
			document.getElementById('ins2').style.display = 'table-row';	
		}else{
			document.getElementById('ins1').style.display = 'none';
			document.getElementById('ins2').style.display = 'none';
		}
	}
	
	
	$("#formPayLoan").submit(function(e){
		e.preventDefault();
		var form_data = $("form[name='formPayLoan']").serialize();
		//alert("form_data ===>> " + form_data);
     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "PayLoan.action",
 			data: form_data,
 			cache : false/* ,
 			success : function(res) {
 				$("#divResult").html(res);
 			} */
 		});
     	
     	$.ajax({ 
			url: 'LoanApplicationReport.action',
			cache: true,
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
     	
	});
</script>



<s:form theme="simple" name="formPayLoan" id="formPayLoan" action="PayLoan" method="POST" cssClass="formcss">
	<s:hidden name="loanApplId"></s:hidden>
	<table class="table table_no_border form-table">
		<tr>
			<td class="txtlabel alignRight">Loan Amount:<sup>*</sup></td>
			<td><s:textfield name="loanAmount" cssClass="validateRequired" readonly="true"/>
				<span class="hint">Loan Amount<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight" valign="top">Payment Description:<sup>*</sup></td>
			<td><s:textarea name="paymentDescription" id="loanDescription" cssClass="validateRequired" rows="3" cols="22"/> 
				<span class="hint">Payment Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Payment Mode:<sup>*</sup></td>
			<td><s:select list="paymentSourceList" name="paymentSource" listKey="paymentSourceId" listValue="paymentSourceName" onchange="showInstrumentInfo(this.value)"></s:select></td>
		</tr>


		<tr style="display: none" id="ins1">
			<td class="txtlabel alignRight">Instrument No:<sup>*</sup></td>
			<td><s:textfield name="strInstrumentNo"	cssClass="validateRequired" /></td>
		</tr>

		<tr style="display: none" id="ins2">
			<td class="txtlabel alignRight">Instrument Date:<sup>*</sup></td>
			<td><s:textfield name="strInstrumentDate" id="idInstrumentDate" cssClass="validateRequired" id="idInstrumentDate" /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Deduct TDS:</td>
			<td><s:checkbox name="deductTDS" onclick="checkTDS(this)"/></td>
		</tr>

		<tr style="display: none" id="ins3">
			<td class="txtlabel alignRight">TDS:<sup>*</sup></td>
			<td><s:textfield name="TDSAmount" cssClass="validateRequired" value="0" onkeyup="calculateAmount();"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Pay Amount:<sup>*</sup></td>
			<td><s:textfield name="loanPaidAmount" cssClass="validateRequired" readonly="true"/></td>
		</tr>
		
		<tr>
			<td></td>
			<td><s:submit cssClass="btn btn-primary" value="Save" name="btnAddNewLevelOk" id="btnAddNewLevelOk" /></td>
		</tr>

	</table>

</s:form>