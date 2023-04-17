<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<script> 

$(function () {
    // binds form submission and fields to the validation engine
	$("body").on("click","#submit1",function(){
		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
    });
    
});

$(function() {
    $( "#idInstrumentDate" ).datepicker({dateFormat: 'dd/mm/yy'});
});


function showInstrumentInfo(val){
	
	if(val && val=='Q'){
		document.getElementById('ins1').style.display = 'table-row';
		document.getElementById('ins2').style.display = 'table-row';	
	}else{
		document.getElementById('ins1').style.display = 'none';
		document.getElementById('ins2').style.display = 'none';
	}
}

function isNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	      return false;
	   }
	   return true;
}

function checkLoanBalance(amt){
	//alert("abc");
	//document.getElementById("strMessageId").innerHTML='';
	var loanBalance = document.getElementById("loanBalance").value;
	if(amt =='' || parseFloat(amt)==0){
		//document.getElementById("strMessageId").innerHTML='You have entered 0 or none.';
		alert('You have entered 0 or none.');
	}else if(parseFloat(amt) >0 && parseFloat(amt)<=parseFloat(loanBalance)){
		
	}else{
		//document.getElementById("strMessageId").innerHTML='You have entered more than loan balance amount.';
		alert('You have entered more than loan balance amount.');
		document.getElementById("strAmount").value = "";
	}
}


$("#frmLoanPayments").submit(function(e){
	e.preventDefault();
	var amt = document.getElementById("strAmount").value;
	var loanBalance = document.getElementById("loanBalance").value;
	if(amt =='' || parseFloat(amt)==0){
		//document.getElementById("strMessageId").innerHTML='You have entered 0 or none.';
		alert('You have entered 0 or none.');
	} else if(parseFloat(amt) >0 && parseFloat(amt)<=parseFloat(loanBalance)) {
		var form_data = $("form[name='frmLoanPayments']").serialize();
		//alert("form_data ===>> " + form_data);
	 	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 	$.ajax({
			url : "LoanPayments.action",
			data: form_data,
			cache : false
			/* ,
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
	 	
	}
});


/* function checkLoanBalance1(){
	//document.getElementById("strMessageId").innerHTML='';
	//alert("abcd");
	var amt = document.getElementById("strAmount").value;
	var loanBalance = document.getElementById("loanBalance").value;

	if(amt =='' || parseFloat(amt)==0){
		//document.getElementById("strMessageId").innerHTML='You have entered 0 or none.';
		alert('You have entered 0 or none.');
		return false;
	}else if(parseFloat(amt) >0 && parseFloat(amt)<=parseFloat(loanBalance)){
		return true;
	}else{
		//document.getElementById("strMessageId").innerHTML='You have entered more than loan balance amount.';
		alert('You have entered more than loan balance amount.');
		return false;
	}
} */

</script>

<% UtilityFunctions uF= new UtilityFunctions(); %>

<div class="title">
	<h3>Loan Payments</h3>
</div>



<s:form name="frmLoanPayments" id="frmLoanPayments" action="LoanPayments" theme="simple">
<s:hidden name="strLoanApplicationId"></s:hidden>
<s:hidden name="strLoanId"></s:hidden>
<s:hidden name="strEmpId"></s:hidden>
<input type="hidden" name="loanBalance" id="loanBalance" value="<%=uF.showData((String) request.getAttribute("loanBalance"),"0" ) %>"/>

<table class="table table_no_border form-table" style="width:100%">
<tr>
	<th>Enter Amount:<sup>*</sup></th>
	<td>
		<s:textfield name="strAmount" id="strAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event)" onkeyup="checkLoanBalance(this.value);"/>
		<div id="strMessageId"></div>
	</td>
</tr>


<tr>
	<th>Source:</th>
	<td><s:select list="paymentSourceList" name="paymentSource" listKey="paymentSourceId" listValue="paymentSourceName" onchange="showInstrumentInfo(this.value)"></s:select> </td>
</tr>


<tr style="display:none" id="ins1">
	<th>Instrument No.:<sup>*</sup></th>
	<td><s:textfield name="strInstrumentNo" cssClass="validateRequired"/></td>
</tr>

<tr style="display:none" id="ins2">
	<th>Instrument Date:<sup>*</sup></th>
	<td><s:textfield name="strInstrumentDate" cssClass="validateRequired" id="idInstrumentDate"/></td>
</tr>

<tr>
	<td colspan="2" align="center">
		<%-- <s:submit value="Save" cssClass="input_button" /> --%>
		<input type="submit" name="submit1" id="submit1" value="Submit" class="btn btn-primary"/> <!-- onclick="return checkLoanBalance1();" -->
	</td>
</tr>



</table>
</s:form>