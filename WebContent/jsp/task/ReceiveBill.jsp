<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib prefix="g" uri="http://granule.com/tags"%>

<%
Map hmCurr = (HashMap)request.getAttribute("hmCurr");
if(hmCurr==null) hmCurr=new HashMap();

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF=new UtilityFunctions();

Map<String, List<String>> hmInvoiceFixHeadData = (Map<String, List<String>>)request.getAttribute("hmInvoiceFixHeadData");
Map<String, List<String>> hmInvoiceOPEHeadData = (Map<String, List<String>>)request.getAttribute("hmInvoiceOPEHeadData");
Map<String, List<String>> hmInvoiceTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmInvoiceTaxHeadData");
Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmProTaxHeadData");

Map<String, String> hmTaxMiscSetting = (Map<String, String>) request.getAttribute("hmTaxMiscSetting");

%>

<g:compress>
	<script>
	$(function() {
		// binds form submission and fields to the validation engine
		$("#btnOk").click(function(){
			$("#frmReceiveMultipleBill").find('.validateRequired').filter(':hidden').prop('required',false);
			$("#frmReceiveMultipleBill").find('.validateRequired').filter(':visible').prop('required',true);
		});
		
	    $("#idInstrumentDate").datepicker({format: 'dd/mm/yyyy'});
	});

	
	function calculateAmount() {
		calculateBalanceAmount();
	}
	
	
	function calculateExchangeRate() {
		var exchangeRate = parseFloat(document.frm.exchangeRate.value).toFixed(2);
		var dueAmount = parseFloat(document.frm.amountDue.value).toFixed(2);		
		
		if(exchangeRate>0 && dueAmount>0) {
			var receivedAmount = (exchangeRate * dueAmount).toFixed(2);
			document.frm.amountReceived.value = receivedAmount;	
		}
	}
	
	
	function validateAmount() {
		var exchangeRate = parseFloat(document.frm.exchangeRate.value).toFixed(2);
		var dueAmount = parseFloat(document.frm.amountDue.value).toFixed(2);
		var receivedAmount = parseFloat(document.frm.amountReceived.value).toFixed(2);
		var TDSAmount = parseFloat(document.frm.tdsAmount.value).toFixed(2);
		
		if(dueAmount>0 && receivedAmount>0 && TDSAmount) {
			if(dueAmount<((receivedAmount / exchangeRate)+(TDSAmount / exchangeRate))) {
				alert('Amount received can not be more than due amount.');
			}
		}
	}
	
	
	
	function calculateBalanceAmount() {
		var billCurr = '<%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>';
		//var exchangeRate = parseFloat(document.frm.exchangeRate.value).toFixed(2);
		var dueAmount = parseFloat(document.frm.amountDue.value).toFixed(2);
		var receivedAmount = parseFloat(document.frm.amountReceived.value).toFixed(2);
		
		var writeOffAmt = parseFloat(document.frm.hidewriteOffAmount.value).toFixed(2);
		if(writeOffAmt == 'NaN' || writeOffAmt == '') {
			writeOffAmt = 0;
		}
		
		if(dueAmount>0) {
			var deductType = document.frm.deductionType.value;
			
			if(deductType == '1') {
			var balanceAmount = 0;
			var baseTaxName = '';
			var allTaxAmount = 0;
			var allTaxPercent = 0;
			<%
				if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>						
				var taxName = '<%=uF.showData(innerList.get(1), "0") %>';
				<% if(i==0) { %>
					baseTaxName = taxName;
				<% } %>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var taxPercent = 0;
				if(document.getElementById('taxPercent'+cnt))
					taxPercent = document.getElementById('taxPercent'+cnt).value;
				if(taxPercent == 'NaN' || taxPercent == '') {
					taxPercent = 0;
				}
				var taxAmount = (dueAmount * taxPercent) / 100;
				allTaxAmount = parseFloat(allTaxAmount) + parseFloat(taxAmount);
				allTaxPercent = parseFloat(allTaxPercent) + parseFloat(taxPercent);
				
				if(document.getElementById('hideTaxAmount'+cnt))
					document.getElementById('hideTaxAmount'+cnt).value =parseFloat(taxAmount).toFixed(2);
				if(document.getElementById('hideTaxPercent'+cnt))
					document.getElementById('hideTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
				if(document.getElementById('tax_amt'+cnt))
					document.getElementById('tax_amt'+cnt).innerHTML = ' ('+taxName+' deducted equal to '+taxAmount.toFixed(2)+' '+billCurr+')';
			<% i++; } } %>
			
			if(document.getElementById('previousYearTdsPercent')) {
				var pyTaxPercent = document.getElementById('previousYearTdsPercent').value;
				var pyTaxAmount = (dueAmount * pyTaxPercent) / 100;
				allTaxAmount = parseFloat(allTaxAmount) + parseFloat(pyTaxAmount);
				allTaxPercent = parseFloat(allTaxPercent) + parseFloat(pyTaxPercent);
				
				document.getElementById('hidePreviousYearTdsAmount').value = parseFloat(pyTaxAmount).toFixed(2);
				document.getElementById('hidePreviousYearTdsPercent').value = parseFloat(pyTaxPercent).toFixed(2);
				document.getElementById('previous_year_tds_amt').innerHTML = ' (Previous Year '+baseTaxName+' deducted equal to '+pyTaxAmount.toFixed(2)+' '+billCurr+')';
			}
			
			document.getElementById('hideTotTaxAmount').value = parseFloat(allTaxAmount).toFixed(2);
			document.getElementById('hideTotTaxPercent').value = parseFloat(allTaxPercent).toFixed(2);
			
				if(receivedAmount>0) {
					balanceAmount = parseFloat(dueAmount) - (parseFloat(receivedAmount) + parseFloat(writeOffAmt) + parseFloat(allTaxAmount));
				} 
				if(balanceAmount>=0) {
					document.frm.balanceAmount.value = balanceAmount.toFixed(2);	
				} else {
					document.frm.balanceAmount.value = '0.0';
				}
			
			} else {
				var balanceAmount = 0;
				var baseTaxName = '';
				var allTaxAmount = 0;
				var allTaxPercent = 0;
				
				<%
					if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
					Iterator<String> it = hmProTaxHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList = hmProTaxHeadData.get(taxHeadId);
				%>						
					var taxName = '<%=uF.showData(innerList.get(1), "0") %>';
					<% if(i==0) { %>
						baseTaxName = taxName;
					<% } %>
					var cnt = <%=uF.showData(i+"", "0") %>;
					var taxAmount = 0;
					if(document.getElementById('taxAmount'+cnt))
						taxAmount = document.getElementById('taxAmount'+cnt).value;
					if(taxAmount == 'NaN' || taxAmount == '') {
						taxAmount = 0;
					}
					allTaxAmount = parseFloat(allTaxAmount) + parseFloat(taxAmount);
					allTaxPercent = parseFloat(allTaxPercent) + parseFloat(taxPercent);
					
					var taxPercent = ((taxAmount * 100)/dueAmount).toFixed(2);
					if(document.getElementById('hideTaxAmount'+cnt))
						document.getElementById('hideTaxAmount'+cnt).value = parseFloat(taxAmount).toFixed(2);
					if(document.getElementById('hideTaxPercent'+cnt))
						document.getElementById('hideTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
					
					if(document.getElementById('tax_per'+cnt))
						document.getElementById('tax_per'+cnt).innerHTML = ' ('+taxName+' deducted @ '+taxPercent+'%)';
				<% i++; } } %>
				if(document.getElementById('previousYearTdsAmount')) {
					var pyTaxAmount = document.getElementById('previousYearTdsAmount').value;
					var pyTaxPercent = ((pyTaxPercent * 100)/dueAmount).toFixed(2);
					allTaxAmount = parseFloat(allTaxAmount) + parseFloat(pyTaxAmount);
					allTaxPercent = parseFloat(allTaxPercent) + parseFloat(pyTaxPercent);
					
					document.getElementById('hidePreviousYearTdsAmount').value = parseFloat(pyTaxAmount).toFixed(2);
					document.getElementById('hidePreviousYearTdsPercent').value = parseFloat(pyTaxPercent).toFixed(2);
					document.getElementById('previous_year_tds_per').innerHTML = ' ('+baseTaxName+' deducted @ '+pyTaxPercent+'%)';
				}
				
				document.getElementById('hideTotTaxAmount').value = parseFloat(allTaxAmount).toFixed(2);
				document.getElementById('hideTotTaxPercent').value = parseFloat(allTaxPercent).toFixed(2);
				
				//alert("receivedAmount ====>> " + receivedAmount + "  writeOffAmt ===>> " + writeOffAmt + "  allTaxAmount ===>> " + allTaxAmount);
				if(receivedAmount>0) {
					balanceAmount = parseFloat(dueAmount) - (parseFloat(receivedAmount) + parseFloat(writeOffAmt) + parseFloat(allTaxAmount));
				} 
				if(balanceAmount>=0) {
					document.frm.balanceAmount.value = balanceAmount.toFixed(2);	
				} else {
					document.frm.balanceAmount.value = '0.0';
				}
			}
			
		} else if(dueAmount<=0) {
			alert('Amount due can not be less than or equal zero');
		}
		//alert("endddd ........");
		calAmt();
	}
	
	
	function calAmt() {
		
		//strOPEAmount strParticularsAmount strTaxAmount
		var receivedAmount = parseFloat(document.frm.amountReceived.value).toFixed(2);
		var invoiceAmount = parseFloat(document.frm.invoiceAmount.value).toFixed(2);
		
		if(receivedAmount == '') {
			receivedAmount = 0;
		}
		
		var receivedAmtPercent = (parseFloat(receivedAmount) * 100) / parseFloat(invoiceAmount);
		
		var allPercent = 0;
		<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String billHeadId = it.next();	
			List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
		%>
			var cnt = <%=uF.showData(i+"", "0") %>;
			var OPEAmount = <%=uF.showData(innerList.get(3), "0") %>;
			var opeAmt = (parseFloat(OPEAmount) * parseFloat(receivedAmtPercent)) / 100;
			if(parseFloat(receivedAmount) > 0) {
				opePer = (parseFloat(opeAmt) * 100) / parseFloat(receivedAmount);
			}
			allPercent = parseFloat(allPercent) + parseFloat(opePer);
			
			document.getElementById('strOPEAmount'+cnt).value = parseFloat(opeAmt).toFixed(2);
			document.getElementById('strHideOPEPercent'+cnt).value = parseFloat(opePer).toFixed(2);
			
			//document.getElementById('w_o_ope_amt'+cnt).innerHTML = ' (equal to '+writeOffopeAmt.toFixed(2)+'INR)';
		<% i++; } } %>
	
		
		<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String billHeadId = it.next();	
			List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
		%>
			var cnt = <%=uF.showData(i+"", "0") %>;
			var partiAmount = <%=uF.showData(innerList.get(3), "0") %>;
			var partiAmt = (parseFloat(partiAmount) * parseFloat(receivedAmtPercent)) / 100;
			var partiPer =0;
			if(parseFloat(receivedAmount) > 0) {
				partiPer = (parseFloat(partiAmt) * 100) / parseFloat(receivedAmount);
			}
			<% if(i>0) { %>
			allPercent = parseFloat(allPercent) + parseFloat(partiPer);
			<% } %>
			document.getElementById('strParticularsAmount'+cnt).value = parseFloat(partiAmt).toFixed(2);
			document.getElementById('strHideParticularsPercent'+cnt).value = parseFloat(partiPer).toFixed(2);
			//document.getElementById('w_o_parti_amt'+cnt).innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+'INR)';
		<% i++; } } %>
		
		
		<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();
			List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
		%>
			var cnt = <%=uF.showData(i+"", "0") %>;
			var taxPercent = <%=uF.showData(innerList.get(5), "0") %>;
			var taxAmt = (parseFloat(receivedAmount) * parseFloat(taxPercent)) / 100;
			allPercent = parseFloat(allPercent) + parseFloat(taxPercent);
			if(parseFloat(receivedAmount) == 0) {
				taxPercent = 0;
			}
			document.getElementById('strTaxAmount'+cnt).value = parseFloat(taxAmt).toFixed(2);
			document.getElementById('strHideTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
			//document.getElementById('w_o_Tax_amt'+cnt).innerHTML = ' (equal to '+writeOffTaxAmt.toFixed(2)+'INR)';
		<% i++; } } %>
		
		var remainPercent = 100 - parseFloat(allPercent);
		if(parseFloat(receivedAmount) == 0) {
			remainPercent = 0;
		}
		
		var partiAmt = (parseFloat(remainPercent) * parseFloat(receivedAmount)) / 100;
		if(document.getElementById('strParticularsAmount0')) {
			document.getElementById('strParticularsAmount0').value = parseFloat(partiAmt).toFixed(2);
			document.getElementById('strHideParticularsPercent0').value = parseFloat(remainPercent).toFixed(2);
			//document.getElementById('w_o_parti_amt0').innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+'INR)';
		}
		
		
	} 
	
	
	function calcuOPE() {
		var receivedAmount = parseFloat(document.frm.amountReceived.value).toFixed(2);
		var invoiceAmount = parseFloat(document.frm.invoiceAmount.value).toFixed(2);
		
		if(receivedAmount == '') {
			receivedAmount = 0;
		}
		
		var receivedAmtPercent = (parseFloat(receivedAmount) * 100) / parseFloat(invoiceAmount);
		
		var allPercent = 0;
		<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String billHeadId = it.next();	
			List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
		%>
			var cnt = <%=uF.showData(i+"", "0") %>;
			var opeAmt = document.getElementById('strOPEAmount'+cnt).value;
			//var opeAmt = (parseFloat(OPEAmount) * parseFloat(receivedAmtPercent)) / 100;
			if(parseFloat(receivedAmount) > 0) {
				opePer = (parseFloat(opeAmt) * 100) / parseFloat(receivedAmount);
			}
			allPercent = parseFloat(allPercent) + parseFloat(opePer);
			
			document.getElementById('strOPEAmount'+cnt).value = parseFloat(opeAmt).toFixed(2);
			document.getElementById('strHideOPEPercent'+cnt).value = parseFloat(opePer).toFixed(2);
		<% i++; } } %>
	
		
		<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String billHeadId = it.next();	
			List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
		%>
			var cnt = <%=uF.showData(i+"", "0") %>;
			var partiAmount = <%=uF.showData(innerList.get(3), "0") %>;
			var partiAmt = (parseFloat(partiAmount) * parseFloat(receivedAmtPercent)) / 100;
			var partiPer =0;
			if(parseFloat(receivedAmount) > 0) {
				partiPer = (parseFloat(partiAmt) * 100) / parseFloat(receivedAmount);
			}
			<% if(i>0) { %>
			allPercent = parseFloat(allPercent) + parseFloat(partiPer);
			<% } %>
			document.getElementById('strParticularsAmount'+cnt).value = parseFloat(partiAmt).toFixed(2);
			document.getElementById('strHideParticularsPercent'+cnt).value = parseFloat(partiPer).toFixed(2);
		<% i++; } } %>
		
		
		<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();
			List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
		%>
			var cnt = <%=uF.showData(i+"", "0") %>;
			var taxPercent = <%=uF.showData(innerList.get(5), "0") %>;
			var taxAmt = (parseFloat(receivedAmount) * parseFloat(taxPercent)) / 100;
			allPercent = parseFloat(allPercent) + parseFloat(taxPercent);
			if(parseFloat(receivedAmount) == 0) {
				taxPercent = 0;
			}
			document.getElementById('strTaxAmount'+cnt).value = parseFloat(taxAmt).toFixed(2);
			document.getElementById('strHideTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
		<% i++; } } %>
		
		var remainPercent = 100 - parseFloat(allPercent);
		if(parseFloat(receivedAmount) == 0) {
			remainPercent = 0;
		}
		
		var partiAmt = (parseFloat(remainPercent) * parseFloat(receivedAmount)) / 100;
		if(document.getElementById('strParticularsAmount0')) {
			document.getElementById('strParticularsAmount0').value = parseFloat(partiAmt).toFixed(2);
			document.getElementById('strHideParticularsPercent0').value = parseFloat(remainPercent).toFixed(2);
		}
	}
	
	
	
	function checkTDS(obj) {
		if(obj.checked) {
			//alert("vsxdssd 11111");
			<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
			var taxPercent = <%=uF.showData(innerList.get(2), "0") %>;
			var cnt = <%=uF.showData(i+"", "0") %>;
			document.getElementById('taxTR'+cnt).style.display = 'table-row';
			if(document.getElementById('taxPercent'+cnt)) {
				document.getElementById('taxPercent'+cnt).value = taxPercent;
			}
			if(document.getElementById('taxAmount'+cnt)) {
				document.getElementById('taxAmount'+cnt).value = "0";
			}
			<% i++; } } %>
			if(document.getElementById('previousYearTdsAmount')) {
				document.getElementById('previousYearTdsAmount').value = "0";
			}
			
			document.getElementById('taxD1').style.display = 'table-row';
			
			document.getElementById('deductionType').selectedIndex = '0';
			
			checkDeductionFields('1');
			calculateBalanceAmount();
			
		} else {
			//alert("vsxdssd");  
			
			<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
			var taxPercent = <%=uF.showData(innerList.get(2), "0") %>;
			var cnt = <%=uF.showData(i+"", "0") %>;
			document.getElementById('taxTR'+cnt).style.display = 'none';
			if(document.getElementById('taxPercent'+cnt)) {
				document.getElementById('taxPercent'+cnt).value = '0.0';
			}
			if(document.getElementById('taxAmount'+cnt)) {
				document.getElementById('taxAmount'+cnt).value = "0";
			}
			<% i++; } } %>
			if(document.getElementById('previousYearTdsAmount')) {
				document.getElementById('previousYearTdsAmount').value = "0";
			}
			document.getElementById('taxD1').style.display = 'none';
			
			calculateBalanceAmount();
			
		}
	}
	
	
	function checkPrevYearTDS(obj) {

		if(obj.checked) {
			
			var deductType = document.frm.deductionType.value;
			if(deductType == '1') {
				document.getElementById('previousYearTdsPercentSpan').style.display = 'table-row';
				document.frm.previousYearTdsPercent.value = '0.0';
				document.getElementById('previousYearTdsAmountSpan').style.display = 'none';
			} else {
				document.getElementById('previousYearTdsAmountSpan').style.display = 'table-row';
				document.frm.previousYearTdsAmount.value = '0.0';
				document.getElementById('previousYearTdsPercentSpan').style.display = 'none';
			}
			document.getElementById('taxDpy').style.display = 'table-row';
			calculateBalanceAmount();
		} else {
			
			document.frm.previousYearTdsPercent.value = '0.0';
			document.frm.previousYearTdsAmount.value = '0.0';
			calculateBalanceAmount();			
			document.getElementById('taxDpy').style.display = 'none';
		}
	}
	
	
	
	function calcuWriteOffOPE() {
		var billCurr = '<%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>';
		var amountReceived = parseFloat(document.getElementById('amountReceived').value).toFixed(2);
		
		var writeOffType = document.getElementById('writeOffType').value;
		if(writeOffType == '1') {
			
			var writeOffopePer = 0;
			var writeOffopeAmt = 0;
			var writeOffsTaxAmt = 0;
			var writeOffeduCessAmt = 0;
			var writeOffstdCessAmt = 0;
			
			var invoiceAmount = parseFloat(document.getElementById('invoiceAmount').value).toFixed(2);
			var dueAmount = parseFloat(document.getElementById('amountDue').value).toFixed(2);
			var writeOffPercent = parseFloat(document.getElementById('writeOffPercent').value).toFixed(2);
			
			var balanceAmount = parseFloat(dueAmount) - (parseFloat(amountReceived)); // + parseFloat(allTaxAmount) parseFloat(hideTdsAmount) + parseFloat(hidePreviousYearTdsAmount) + parseFloat(hideOtherDeduction)
			
			if(writeOffPercent == 'NaN' || writeOffPercent == '') {
				writeOffPercent = 0;
			}
			
			var writeOffAmt = (parseFloat(balanceAmount) * parseFloat(writeOffPercent)) / 100;
			var writeOffAmtPercent = (parseFloat(writeOffAmt) * 100) / parseFloat(invoiceAmount);
			
			var totAllPercent = 0;
			<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) { 
				Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				<%-- var OPEAmount = <%=uF.showData(innerList.get(3), "0") %>; --%>
				var writeOffOperationExpPercent = document.getElementById('strWriteOffOPEPercent'+cnt).value;
				if(writeOffOperationExpPercent == 'NaN' || writeOffOperationExpPercent == '') {
					writeOffOperationExpPercent = 0;
				}
				if(parseFloat(writeOffPercent) == 0) {
					alert('Write off percentage is zero.');
					document.getElementById('strWriteOffOPEPercent'+cnt).value= '0';
					writeOffOperationExpPercent = 0;
				}
				
				writeOffopeAmt = (parseFloat(writeOffOperationExpPercent) * parseFloat(writeOffAmt)) / 100;
				totAllPercent = parseFloat(totAllPercent) + parseFloat(writeOffOperationExpPercent);
				if(document.getElementById('strWriteOffOPEPercent'+cnt)) {
					document.getElementById('strWriteOffOPEPercent'+cnt).value = parseFloat(writeOffOperationExpPercent).toFixed(2);
					document.getElementById('hidewoOPEAmount'+cnt).value = writeOffopeAmt.toFixed(2);
					document.getElementById('hidewoOPEPercent'+cnt).value = parseFloat(writeOffOperationExpPercent).toFixed(2);
					document.getElementById('w_o_ope_amt'+cnt).innerHTML = ' (equal to '+writeOffopeAmt.toFixed(2)+' '+billCurr+')';
				}
			<% i++; } } %>
		
			
			<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var partiAmount = <%=uF.showData(innerList.get(3), "0") %>;
				var writeOffPartiAmt = (parseFloat(partiAmount) * parseFloat(writeOffAmtPercent)) / 100;
				var writeOffPartiPer =0;
				if(parseFloat(writeOffAmt) > 0) {
					writeOffPartiPer = (parseFloat(writeOffPartiAmt) * 100) / parseFloat(writeOffAmt);
				}
				<% if(i>0) { %>
					totAllPercent = parseFloat(totAllPercent) + parseFloat(writeOffPartiPer);
				<% } %>
				if(document.getElementById('strWriteOffParticularsPercent'+cnt)) {
					document.getElementById('strWriteOffParticularsPercent'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
					document.getElementById('hidewoPartiAmount'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
					document.getElementById('hidewoPartiPercent'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
					document.getElementById('w_o_parti_amt'+cnt).innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+' '+billCurr+')';
				}
			<% i++; } } %>
			
			
			<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var taxPercent = <%=uF.showData(innerList.get(5), "0") %>;
				totAllPercent = parseFloat(totAllPercent) + parseFloat(taxPercent);
				var writeOffTaxAmt = (parseFloat(writeOffAmt) * parseFloat(taxPercent)) / 100;
				if(parseFloat(writeOffAmt) == 0) {
					taxPercent = 0;
				}
				if(document.getElementById('strWriteOffTaxPercent'+cnt)) {
					document.getElementById('strWriteOffTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
					document.getElementById('hidewoTaxAmount'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
					document.getElementById('hidewoTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
					document.getElementById('w_o_Tax_amt'+cnt).innerHTML = ' (equal to '+writeOffTaxAmt.toFixed(2)+' '+billCurr+')';
				}
			<% i++; } } %>
			
			var remainWOPercent = 100 - parseFloat(totAllPercent);
			//alert("remainWOPercent ===>> " + remainWOPercent);
			if(parseFloat(writeOffAmt) == 0) {
				remainWOPercent = 0;
			}
			var writeOffPartiAmt = (parseFloat(remainWOPercent) * parseFloat(writeOffAmt)) / 100;
			if(document.getElementById('strWriteOffParticularsPercent0')) {
				document.getElementById('strWriteOffParticularsPercent0').value = parseFloat(remainWOPercent).toFixed(2);
				document.getElementById('hidewoPartiAmount0').value = writeOffPartiAmt.toFixed(2);
				document.getElementById('hidewoPartiPercent0').value = parseFloat(remainWOPercent).toFixed(2);
				document.getElementById('w_o_parti_amt0').innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+' '+billCurr+')';
			}
			
		} else {

			var writeOffopePer = 0;
			var writeOffopeAmt = 0;
			var writeOffsTaxAmt = 0;
			var writeOffeduCessAmt = 0;
			var writeOffstdCessAmt = 0;
			
			var invoiceAmount = parseFloat(document.getElementById('invoiceAmount').value).toFixed(2);
			var dueAmount = parseFloat(document.getElementById('amountDue').value).toFixed(2);
			var writeOffAmount = document.getElementById('writeOffAmount').value;
			
			var balanceAmount = parseFloat(dueAmount) - (parseFloat(amountReceived)); //  +  parseFloat(allTaxAmount) parseFloat(hideTdsAmount) + parseFloat(hidePreviousYearTdsAmount) + parseFloat(hideOtherDeduction)
			
			if(parseFloat(writeOffAmount) > parseFloat(balanceAmount)) {
				alert("Entered amount more than balance amount.");
				document.getElementById('writeOffAmount').value = "0";
				writeOffAmount = 0;
			}
			
			if(writeOffAmount == 'NaN' || writeOffAmount == '') {
				writeOffAmount = 0;
			}
			
			var writeOffAmtPercent = (parseFloat(writeOffAmount) * 100) / parseFloat(invoiceAmount);
			if(writeOffAmtPercent == 'NaN' || writeOffAmtPercent == '') {
				writeOffAmtPercent = 0;
			}
			var writeOffAmtPer = (parseFloat(writeOffAmount) * 100) / parseFloat(balanceAmount);
			if(writeOffAmtPer == 'NaN' || writeOffAmtPer == '') {
				writeOffAmtPer = 0;
			}
			writeOffAmtPer = parseFloat(writeOffAmtPer).toFixed(2);
			
			
			var allWOAmount = 0;
			<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				<%-- var OPEAmount = <%=uF.showData(innerList.get(3), "0") %>; --%>
				var writeOffOperationExpAmount = document.getElementById('strWriteOffOPEAmount'+cnt).value;
				if(writeOffOperationExpAmount == 'NaN' || writeOffOperationExpAmount == '') {
					writeOffOperationExpAmount = 0;
				}
				if(parseFloat(writeOffAmount) == 0) {
					alert("Write off amount is zero.");
					document.getElementById('strWriteOffOPEAmount'+cnt).value = "0";
					writeOffOperationExpAmount = 0;
				}
				if(parseFloat(writeOffAmount) > 0) {
					writeOffopePer = (parseFloat(writeOffOperationExpAmount) * 100) / parseFloat(writeOffAmount);
				}
				allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffOperationExpAmount);

				if(document.getElementById('strWriteOffOPEAmount'+cnt)) {
					document.getElementById('strWriteOffOPEAmount'+cnt).value = parseFloat(writeOffOperationExpAmount).toFixed(2);
					document.getElementById('hidewoOPEAmount'+cnt).value = parseFloat(writeOffOperationExpAmount).toFixed(2);
					document.getElementById('hidewoOPEPercent'+cnt).value = parseFloat(writeOffopePer).toFixed(2);
					document.getElementById('w_o_ope_per'+cnt).innerHTML = ' (@ '+parseFloat(writeOffopePer).toFixed(2)+'%)';
				}
			<% i++; } } %>
		
			
			<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var partiAmount = <%=uF.showData(innerList.get(3), "0") %>;
				var writeOffPartiAmt = (parseFloat(partiAmount) * parseFloat(writeOffAmtPercent)) / 100;
				<% if(i>0) { %>
					allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffPartiAmt);
				<% } %>
				var writeOffPartiPer =0;
				if(parseFloat(writeOffAmount) > 0) {
					writeOffPartiPer = (parseFloat(writeOffPartiAmt) * 100) / parseFloat(writeOffAmount);
				}
				if(document.getElementById('strWriteOffParticularsAmount'+cnt)) {  
					document.getElementById('strWriteOffParticularsAmount'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
					document.getElementById('hidewoPartiAmount'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
					document.getElementById('hidewoPartiPercent'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
					document.getElementById('w_o_parti_per'+cnt).innerHTML = ' (@ '+parseFloat(writeOffPartiPer).toFixed(2)+'%)';
				}
			<% i++; } } %>
			
			
			<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var taxPercent = <%=uF.showData(innerList.get(5), "0") %>;
				if(parseFloat(writeOffAmount) == 0) {
					taxPercent = 0;
				}
				var writeOffTaxAmt = (parseFloat(writeOffAmount) * parseFloat(taxPercent)) / 100;
				allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffTaxAmt);
				
				if(document.getElementById('strWriteOffTaxAmount'+cnt)) {
					document.getElementById('strWriteOffTaxAmount'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
					document.getElementById('hidewoTaxAmount'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
					document.getElementById('hidewoTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
					document.getElementById('w_o_Tax_per'+cnt).innerHTML = ' (@ '+parseFloat(taxPercent).toFixed(2)+'%)';
				}
			<% i++; } } %>
			
			
			var remainWOAmount = parseFloat(writeOffAmount) - parseFloat(allWOAmount);
			if(parseFloat(writeOffAmount) == 0) {
				remainWOPercent = 0;
			}
			
			var writeOffPartiPer = 0;
			if(parseFloat(writeOffAmount) > 0) {
				writeOffPartiPer = (parseFloat(remainWOAmount) * 100 / parseFloat(writeOffAmount));
			}
			if(document.getElementById('strWriteOffParticularsAmount0')) {
				document.getElementById('strWriteOffParticularsAmount0').value = parseFloat(remainWOAmount).toFixed(2);
				document.getElementById('hidewoPartiAmount0').value = parseFloat(remainWOAmount).toFixed(2);
				document.getElementById('hidewoPartiPercent0').value = parseFloat(writeOffPartiPer).toFixed(2);
				document.getElementById('w_o_parti_per0').innerHTML = ' (@ '+parseFloat(writeOffPartiPer).toFixed(2)+'%)';
			}
			
			/* document.getElementById('hidewriteOffAmount').value = parseFloat(writeOffAmount).toFixed(2);
			document.getElementById('hidewriteOffPercent').value = writeOffAmtPer;
			
			balanceAmount = parseFloat(balanceAmount) - parseFloat(writeOffAmount);
			
			if(balanceAmount >= 0) {
				document.frm.balanceAmount.value = balanceAmount.toFixed(2);	
			} else {
				document.frm.balanceAmount.value = '0.0';
			} */
		}
	}
	

	
	function checkWriteoffAmount() {
		var billCurr = '<%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>';
		var amountReceived = parseFloat(document.getElementById('amountReceived').value).toFixed(2);
		
		var allTaxAmount = 0;
		<%
			if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
			Iterator<String> it = hmProTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();
			List<String> innerList = hmProTaxHeadData.get(taxHeadId);
		%>		
			var cnt = <%=uF.showData(i+"", "0") %>;
			var taxAmount = 0;
			if(document.getElementById('hideTaxAmount'+cnt))
				taxAmount = document.getElementById('hideTaxAmount'+cnt).value;
			allTaxAmount = parseFloat(allTaxAmount) + parseFloat(taxAmount);
		<% i++; } } %>
		
		var pyTaxAmount = 0;
		if(document.getElementById('hidePreviousYearTdsAmount'))
			pyTaxAmount = document.getElementById('hidePreviousYearTdsAmount').value;
		allTaxAmount = parseFloat(allTaxAmount) + parseFloat(pyTaxAmount);
		
		//alert("allTaxAmount ===>> " + allTaxAmount);
		
		var writeOffType = document.getElementById('writeOffType').value;
		if(writeOffType == '1') {
			
			var writeOffopePer = 0;
			var writeOffprofFeesPercent = 0;
			var writeOffopeAmt = 0;
			var writeOffsTaxAmt = 0;
			var writeOffeduCessAmt = 0;
			var writeOffstdCessAmt = 0;
			
			var invoiceAmount = parseFloat(document.getElementById('invoiceAmount').value).toFixed(2);
			var dueAmount = parseFloat(document.getElementById('amountDue').value).toFixed(2);
			var writeOffPercent = parseFloat(document.getElementById('writeOffPercent').value).toFixed(2);
			
			if(writeOffPercent > 100) {
				alert("Entered Percentage more than 100.");
				document.getElementById('writeOffPercent').value = "0";
				writeOffPercent = 0;
			}
			var balanceAmount = parseFloat(dueAmount) - (parseFloat(amountReceived) + parseFloat(allTaxAmount)); //parseFloat(hideTdsAmount) + parseFloat(hidePreviousYearTdsAmount) + parseFloat(hideOtherDeduction)
			
			if(writeOffPercent == 'NaN' || writeOffPercent == '') {
				writeOffPercent = 0;
			}
			
			if(parseFloat(balanceAmount)== 0) {
				alert("Balance amount is zero.");
				document.getElementById('writeOffPercent').value = "0";
				writeOffAmount = 0;
			}
			
			var writeOffAmt = (parseFloat(balanceAmount) * parseFloat(writeOffPercent)) / 100;
			if(writeOffAmt == 'NaN' || writeOffAmt == '') {
				writeOffAmt = 0;
			}
			var writeOffAmtPercent = (parseFloat(writeOffAmt) * 100) / parseFloat(invoiceAmount);
			
			if(writeOffAmtPercent == 'NaN' || writeOffAmtPercent == '') {
				writeOffAmtPercent = 0;
			}
			
			var allWOPercent = 0;
			<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var OPEAmount = <%=uF.showData(innerList.get(3), "0") %>;
				writeOffopeAmt = (parseFloat(OPEAmount) * parseFloat(writeOffAmtPercent)) / 100;
				if(parseFloat(writeOffAmt) > 0) {
					writeOffopePer = (parseFloat(writeOffopeAmt) * 100) / parseFloat(writeOffAmt);
				}
				allWOPercent = parseFloat(allWOPercent) + parseFloat(writeOffopePer);
				if(document.getElementById('strWriteOffOPEPercent'+cnt)) {
					document.getElementById('strWriteOffOPEPercent'+cnt).value = parseFloat(writeOffopePer).toFixed(2);
					document.getElementById('hidewoOPEAmount'+cnt).value = parseFloat(writeOffopeAmt).toFixed(2);
					document.getElementById('hidewoOPEPercent'+cnt).value = parseFloat(writeOffopePer).toFixed(2);
					document.getElementById('w_o_ope_amt'+cnt).innerHTML = ' (equal to '+writeOffopeAmt.toFixed(2)+' '+billCurr+')';
				}
			<% i++; } } %>
		
			
			<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var partiAmount = <%=uF.showData(innerList.get(3), "0") %>;
				var writeOffPartiAmt = (parseFloat(partiAmount) * parseFloat(writeOffAmtPercent)) / 100;
				var writeOffPartiPer =0;
				if(parseFloat(writeOffAmt) > 0) {
					writeOffPartiPer = (parseFloat(writeOffPartiAmt) * 100) / parseFloat(writeOffAmt);
				}
				<% if(i>0) { %>
				allWOPercent = parseFloat(allWOPercent) + parseFloat(writeOffPartiPer);
				<% } %>
				if(document.getElementById('strWriteOffParticularsPercent'+cnt)) {
					document.getElementById('strWriteOffParticularsPercent'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
					document.getElementById('hidewoPartiAmount'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
					document.getElementById('hidewoPartiPercent'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
					document.getElementById('w_o_parti_amt'+cnt).innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+' '+billCurr+')';
				}
			<% i++; } } %>
			
			
			<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var taxPercent = <%=uF.showData(innerList.get(5), "0") %>;
				var writeOffTaxAmt = (parseFloat(writeOffAmt) * parseFloat(taxPercent)) / 100;
				allWOPercent = parseFloat(allWOPercent) + parseFloat(taxPercent);
				if(parseFloat(writeOffAmt) == 0) {
					taxPercent = 0;
				}
				if(document.getElementById('strWriteOffTaxPercent'+cnt)) {
					document.getElementById('strWriteOffTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
					document.getElementById('hidewoTaxAmount'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
					document.getElementById('hidewoTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
					document.getElementById('w_o_Tax_amt'+cnt).innerHTML = ' (equal to '+writeOffTaxAmt.toFixed(2)+' '+billCurr+')';
				}
			<% i++; } } %>
			
			var remainWOPercent = 100 - parseFloat(allWOPercent);
			if(parseFloat(writeOffAmt) == 0) {
				remainWOPercent = 0;
			}
			
			var writeOffPartiAmt = (parseFloat(remainWOPercent) * parseFloat(writeOffAmt)) / 100;
			if(document.getElementById('strWriteOffParticularsPercent0')) {
				document.getElementById('strWriteOffParticularsPercent0').value = parseFloat(remainWOPercent).toFixed(2);
				document.getElementById('hidewoPartiAmount0').value = writeOffPartiAmt.toFixed(2);
				document.getElementById('hidewoPartiPercent0').value = parseFloat(remainWOPercent).toFixed(2);
				document.getElementById('w_o_parti_amt0').innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+' '+billCurr+')';
			}
			
			document.getElementById('hidewriteOffAmount').value = parseFloat(writeOffAmt).toFixed(2);
			document.getElementById('hidewriteOffPercent').value = writeOffPercent;
			
			//alert("balanceAmount ===>> " + balanceAmount + "  writeOffAmt ====>> " + writeOffAmt);
			balanceAmount = parseFloat(balanceAmount) - parseFloat(writeOffAmt);
			
			//alert("balanceAmount ===>> " + balanceAmount);
			
			if(balanceAmount >= 0) {
				document.frm.balanceAmount.value = balanceAmount.toFixed(2);	
			} else {
				document.frm.balanceAmount.value = '0.0';
			}
			
		} else {

			var writeOffopePer = 0;
			var writeOffopeAmt = 0;
			var writeOffsTaxAmt = 0;
			var writeOffeduCessAmt = 0;
			var writeOffstdCessAmt = 0;
			
			var invoiceAmount = parseFloat(document.getElementById('invoiceAmount').value).toFixed(2);
			var dueAmount = parseFloat(document.getElementById('amountDue').value).toFixed(2);
			var writeOffAmount = document.getElementById('writeOffAmount').value;
			
			//alert("allTaxAmount else ===>> " + allTaxAmount);
			var balanceAmount = parseFloat(dueAmount) - (parseFloat(amountReceived) + parseFloat(allTaxAmount)); //parseFloat(hideTdsAmount) + parseFloat(hidePreviousYearTdsAmount) + parseFloat(hideOtherDeduction)
			
			if(parseFloat(writeOffAmount) > parseFloat(balanceAmount)) {
				alert("Entered amount more than balance amount.");
				document.getElementById('writeOffAmount').value = "0";
				writeOffAmount = 0;
			}
			
			if(writeOffAmount == 'NaN' || writeOffAmount == '') {
				writeOffAmount = 0;
			}
			
			var writeOffAmtPercent = (parseFloat(writeOffAmount) * 100) / parseFloat(invoiceAmount);
			if(writeOffAmtPercent == 'NaN' || writeOffAmtPercent == '') {
				writeOffAmtPercent = 0;
			}
			var writeOffAmtPer = (parseFloat(writeOffAmount) * 100) / parseFloat(balanceAmount);
			if(writeOffAmtPer == 'NaN' || writeOffAmtPer == '') {
				writeOffAmtPer = 0;
			}
			writeOffAmtPer = parseFloat(writeOffAmtPer).toFixed(2);
			
			
			var allWOAmount = 0;
			<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var OPEAmount = <%=uF.showData(innerList.get(3), "0") %>;
				writeOffopeAmt = (parseFloat(OPEAmount) * parseFloat(writeOffAmtPercent)) / 100;
				allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffopeAmt);
				if(parseFloat(writeOffAmount) > 0) {
					writeOffopePer = (parseFloat(writeOffopeAmt) * 100) / parseFloat(writeOffAmount);
				}
				//alert("writeOffopePer --->> " + writeOffopePer);
				
				if(document.getElementById('strWriteOffOPEAmount'+cnt)) {
					document.getElementById('strWriteOffOPEAmount'+cnt).value = parseFloat(writeOffopeAmt).toFixed(2);
					document.getElementById('hidewoOPEAmount'+cnt).value = parseFloat(writeOffopeAmt).toFixed(2);
					document.getElementById('hidewoOPEPercent'+cnt).value = parseFloat(writeOffopePer).toFixed(2);
					document.getElementById('w_o_ope_per'+cnt).innerHTML = ' (@ '+parseFloat(writeOffopePer).toFixed(2)+'%)';
				}
			<% i++; } } %>
		
			
			<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var partiAmount = <%=uF.showData(innerList.get(3), "0") %>;
				var writeOffPartiAmt = (parseFloat(partiAmount) * parseFloat(writeOffAmtPercent)) / 100;
				<% if(i>0) { %>
					allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffPartiAmt);
				<% } %>
				var writeOffPartiPer =0;
				if(parseFloat(writeOffAmount) > 0) {
					writeOffPartiPer = (parseFloat(writeOffPartiAmt) * 100) / parseFloat(writeOffAmount);
				}
				//alert("writeOffPartiPer --->> " + writeOffPartiPer);
				
				if(document.getElementById('strWriteOffParticularsAmount'+cnt)) {  
					document.getElementById('strWriteOffParticularsAmount'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
					document.getElementById('hidewoPartiAmount'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
					document.getElementById('hidewoPartiPercent'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
					document.getElementById('w_o_parti_per'+cnt).innerHTML = ' (@ '+parseFloat(writeOffPartiPer).toFixed(2)+'%)';
				}
			<% i++; } } %>
			
			
			<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				var taxPercent = <%=uF.showData(innerList.get(5), "0") %>;
				if(parseFloat(writeOffAmt) == 0) {
					taxPercent = 0;
				}
				var writeOffTaxAmt = (parseFloat(writeOffAmount) * parseFloat(taxPercent)) / 100;
				if(writeOffTaxAmt == 'NaN' || writeOffTaxAmt == '') {
					writeOffTaxAmt = 0;
					taxPercent = 0;
				}
				allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffTaxAmt);
				
				if(document.getElementById('strWriteOffTaxAmount'+cnt)) {
					document.getElementById('strWriteOffTaxAmount'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
					document.getElementById('hidewoTaxAmount'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
					document.getElementById('hidewoTaxPercent'+cnt).value = parseFloat(taxPercent).toFixed(2);
					document.getElementById('w_o_Tax_per'+cnt).innerHTML = ' (@ '+parseFloat(taxPercent).toFixed(2)+'%)';
				}
			<% i++; } } %>
			
			
			var remainWOAmount = parseFloat(writeOffAmount) - parseFloat(allWOAmount);
			if(parseFloat(writeOffAmt) == 0) {
				remainWOPercent = 0;
			}
			
			var writeOffPartiPer = 0;
			if(parseFloat(writeOffAmount) > 0) {
				writeOffPartiPer = (parseFloat(remainWOAmount) * 100 / parseFloat(writeOffAmount));
			}
			//alert("writeOffPartiPer ===>> " + writeOffPartiPer);
			if(document.getElementById('strWriteOffParticularsAmount0')) {
				document.getElementById('strWriteOffParticularsAmount0').value = parseFloat(remainWOAmount).toFixed(2);
				document.getElementById('hidewoPartiAmount0').value = parseFloat(remainWOAmount).toFixed(2);
				document.getElementById('hidewoPartiPercent0').value = parseFloat(writeOffPartiPer).toFixed(2);
				document.getElementById('w_o_parti_per0').innerHTML = ' (@ '+parseFloat(writeOffPartiPer).toFixed(2)+'%)';
			}
			
			document.getElementById('hidewriteOffAmount').value = parseFloat(writeOffAmount).toFixed(2);
			document.getElementById('hidewriteOffPercent').value = writeOffAmtPer;
			
			balanceAmount = parseFloat(balanceAmount) - parseFloat(writeOffAmount);
			
			if(balanceAmount >= 0) {
				document.frm.balanceAmount.value = balanceAmount.toFixed(2);	
			} else {
				document.frm.balanceAmount.value = '0.0';
			}
		}
	}
	
	
	
	function checkWriteOffFields(val) {
		 //    writeOffOperationExp  writeOffServiceTax  writeOffCess2Percent 
		 if(val == '1') {
			 document.getElementById('woPercentTR').style.display = 'table-row';
			 
				if(document.getElementById('writeOffPercent'))
					document.getElementById('writeOffPercent').value = "0";
				if(document.getElementById('writeOffAmount'))
					document.getElementById('writeOffAmount').value = "0";
				if(document.getElementById('hidewriteOffAmount'))
					document.getElementById('hidewriteOffAmount').value = "0";
				if(document.getElementById('hidewriteOffPercent'))
					document.getElementById('hidewriteOffPercent').value = "0";
				
				<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
					Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
				%>
					var cnt = <%=uF.showData(i+"", "0") %>;
					document.getElementById('writeOffOPEPercentSpan'+cnt).style.display = 'block';
					if(document.getElementById('strWriteOffOPEPercent'+cnt))
						document.getElementById('strWriteOffOPEPercent'+cnt).value = "0";
					if(document.getElementById('strWriteOffOPEAmount'+cnt))
						document.getElementById('strWriteOffOPEAmount'+cnt).value = "0";
					if(document.getElementById('w_o_ope_per'+cnt))
						document.getElementById('w_o_ope_per'+cnt).innerHTML = '';
					if(document.getElementById('w_o_ope_amt'+cnt))
						document.getElementById('w_o_ope_amt'+cnt).innerHTML = '';
					document.getElementById('writeOffOPEAmountSpan'+cnt).style.display = 'none';
				<% i++; } } %>
			
				
				<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
					Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
				%>
					var cnt = <%=uF.showData(i+"", "0") %>;
					document.getElementById('writeOffParticularsPercentSpan'+cnt).style.display = 'block';
					if(document.getElementById('strWriteOffParticularsPercent'+cnt))
						document.getElementById('strWriteOffParticularsPercent'+cnt).value = "0";
					if(document.getElementById('strWriteOffParticularsAmount'+cnt))
						document.getElementById('strWriteOffParticularsAmount'+cnt).value = "0";
					if(document.getElementById('w_o_parti_per'+cnt))
						document.getElementById('w_o_parti_per'+cnt).innerHTML = '';
					if(document.getElementById('w_o_parti_amt'+cnt))
						document.getElementById('w_o_parti_amt'+cnt).innerHTML = '';
					document.getElementById('writeOffParticularsAmountSpan'+cnt).style.display = 'none';
				<% i++; } } %>
				
				
				<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
					Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
				%>
					var cnt = <%=uF.showData(i+"", "0") %>;
					document.getElementById('writeOffTaxPercentSpan'+cnt).style.display = 'block';
					if(document.getElementById('strWriteOffTaxPercent'+cnt))
						document.getElementById('strWriteOffTaxPercent'+cnt).value = "0";
					if(document.getElementById('strWriteOffTaxAmount'+cnt))
						document.getElementById('strWriteOffTaxAmount'+cnt).value = "0";
					if(document.getElementById('w_o_Tax_per'+cnt))
						document.getElementById('w_o_Tax_per'+cnt).innerHTML = '';
					if(document.getElementById('w_o_Tax_amt'+cnt))
						document.getElementById('w_o_Tax_amt'+cnt).innerHTML = '';
					document.getElementById('writeOffTaxAmountSpan'+cnt).style.display = 'none';
				<% i++; } } %>
			
				calculateBalanceAmount();
				
				document.getElementById('woAmountTR').style.display = 'none';
			 
		 } else {

			 document.getElementById('woAmountTR').style.display = 'table-row';
			 
			 	if(document.getElementById('writeOffPercent'))
					document.getElementById('writeOffPercent').value = "0";
				if(document.getElementById('writeOffAmount'))
					document.getElementById('writeOffAmount').value = "0";
				if(document.getElementById('hidewriteOffAmount'))
					document.getElementById('hidewriteOffAmount').value = "0";
				if(document.getElementById('hidewriteOffPercent'))
					document.getElementById('hidewriteOffPercent').value = "0";
				
			 <% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
					Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
				%>
					var cnt = <%=uF.showData(i+"", "0") %>;
					document.getElementById('writeOffOPEAmountSpan'+cnt).style.display = 'block';
					if(document.getElementById('strWriteOffOPEPercent'+cnt))
						document.getElementById('strWriteOffOPEPercent'+cnt).value = "0";
					if(document.getElementById('strWriteOffOPEAmount'+cnt))
						document.getElementById('strWriteOffOPEAmount'+cnt).value = "0";
					if(document.getElementById('w_o_ope_per'+cnt))
						document.getElementById('w_o_ope_per'+cnt).innerHTML = '';
					if(document.getElementById('w_o_ope_amt'+cnt))
						document.getElementById('w_o_ope_amt'+cnt).innerHTML = '';
					
					document.getElementById('writeOffOPEPercentSpan'+cnt).style.display = 'none';
				<% i++; } } %>
			
				
				<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
					Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
				%>
					var cnt = <%=uF.showData(i+"", "0") %>;
					document.getElementById('writeOffParticularsAmountSpan'+cnt).style.display = 'block';
					if(document.getElementById('strWriteOffParticularsPercent'+cnt))
						document.getElementById('strWriteOffParticularsPercent'+cnt).value = "0";
					if(document.getElementById('strWriteOffParticularsAmount'+cnt))
						document.getElementById('strWriteOffParticularsAmount'+cnt).value = "0";
					if(document.getElementById('w_o_parti_per'+cnt))
						document.getElementById('w_o_parti_per'+cnt).innerHTML = '';
					if(document.getElementById('w_o_parti_amt'+cnt))
						document.getElementById('w_o_parti_amt'+cnt).innerHTML = '';
					document.getElementById('writeOffParticularsPercentSpan'+cnt).style.display = 'none';
				<% i++; } } %>
				
				
				<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
					Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
				%>
					var cnt = <%=uF.showData(i+"", "0") %>;
					document.getElementById('writeOffTaxAmountSpan'+cnt).style.display = 'block';
					if(document.getElementById('strWriteOffTaxPercent'+cnt))
						document.getElementById('strWriteOffTaxPercent'+cnt).value = "0";
					if(document.getElementById('strWriteOffTaxAmount'+cnt))
						document.getElementById('strWriteOffTaxAmount'+cnt).value = "0";
					if(document.getElementById('w_o_Tax_per'+cnt))
						document.getElementById('w_o_Tax_per'+cnt).innerHTML = '';
					if(document.getElementById('w_o_Tax_amt'+cnt))
						document.getElementById('w_o_Tax_amt'+cnt).innerHTML = '';
					document.getElementById('writeOffTaxPercentSpan'+cnt).style.display = 'none';
				<% i++; } } %>
				
				document.getElementById('woPercentTR').style.display = 'none';
				
				calculateBalanceAmount();
		 }
	}
	
	
	function checkDeductionFields(val) {
		 //tdsAmountSpan tdsAmountPercentSpan otherDeductionSpan otherDeductionPercentSpan
		 if(val == '1') {
			 
			 <% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
				var taxPercent = <%=uF.showData(innerList.get(2), "0") %>;
				var cnt = <%=uF.showData(i+"", "0") %>;
				document.getElementById('taxPercentSpan'+cnt).style.display = 'table-row';
				if(document.getElementById('taxPercent'+cnt))
					document.getElementById('taxPercent'+cnt).value = taxPercent;
				if(document.getElementById('taxAmount'+cnt))
					document.getElementById('taxAmount'+cnt).value = "0";
				document.getElementById('taxAmountSpan'+cnt).style.display = 'none';
			<% i++; } } %>
			//alert("vxcvxcv nnnnnnnn");	
			 document.frm.previousYearTdsPercent.value = '0';
			 document.frm.previousYearTdsAmount.value = '0';
			 
			 if(document.getElementById('previousYearTdsPercentSpan')) {
			 	document.getElementById('previousYearTdsPercentSpan').style.display = 'block';
			 }
			 if(document.getElementById('previousYearTdsAmountSpan')) {
			 	document.getElementById('previousYearTdsAmountSpan').style.display = 'none';
			 }
			 calculateBalanceAmount();
		 } else {
			 
			 <% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
				var taxPercent = <%=uF.showData(innerList.get(2), "0") %>;
				var cnt = <%=uF.showData(i+"", "0") %>;
				document.getElementById('taxAmountSpan'+cnt).style.display = 'table-row';
				if(document.getElementById('taxPercent'+cnt))
					document.getElementById('taxPercent'+cnt).value = "0";
				if(document.getElementById('taxAmount'+cnt))
					document.getElementById('taxAmount'+cnt).value = "0";
				document.getElementById('taxPercentSpan'+cnt).style.display = 'none';
			<% i++; } } %>
			
			 document.frm.previousYearTdsPercent.value = '0';
			 document.frm.previousYearTdsAmount.value = '0';
			 
			 if(document.getElementById('previousYearTdsPercentSpan')) {
			 	document.getElementById('previousYearTdsPercentSpan').style.display = 'none';
			 }
			 if(document.getElementById('previousYearTdsAmountSpan')) {
			 	document.getElementById('previousYearTdsAmountSpan').style.display = 'block';
			 }
			 calculateBalanceAmount();

		 }
	}
	
	
	function checkWOff(obj) {
		if(obj.checked) {
			document.getElementById('woTypeTR').style.display = 'table-row';
			document.getElementById('woPercentTR').style.display = 'table-row';
			//document.getElementById('ins6').style.display = 'table-row';
			
			document.getElementById('writeOffType').selectedIndex = '0';
			if(document.getElementById('writeOffPercent'))
				document.getElementById('writeOffPercent').value = "0";
			if(document.getElementById('writeOffAmount'))
				document.getElementById('writeOffAmount').value = "0";
			if(document.getElementById('hidewriteOffAmount'))
				document.getElementById('hidewriteOffAmount').value = "0";
			if(document.getElementById('hidewriteOffPercent'))
				document.getElementById('hidewriteOffPercent').value = "0";
			
			checkWriteOffFields('1');
			
			<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				document.getElementById('woOPETR'+cnt).style.display = 'table-row';
				if(document.getElementById('strWriteOffOPEPercent'+cnt))
					document.getElementById('strWriteOffOPEPercent'+cnt).value = "0";
				if(document.getElementById('strWriteOffOPEAmount'+cnt))
					document.getElementById('strWriteOffOPEAmount'+cnt).value = "0";
			<% i++; } } %>
		
			
			<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				document.getElementById('woPartiTR'+cnt).style.display = 'table-row';
				if(document.getElementById('strWriteOffParticularsPercent'+cnt))
					document.getElementById('strWriteOffParticularsPercent'+cnt).value = "0";
				if(document.getElementById('strWriteOffParticularsAmount'+cnt))
					document.getElementById('strWriteOffParticularsAmount'+cnt).value = "0";
			<% i++; } } %>
			
			
			<%  //System.out.println("hmInvoiceTaxHeadData  ==>> " + hmInvoiceTaxHeadData);
			if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				//alert("cnt ===>> " + cnt);
				document.getElementById('woTaxTR'+cnt).style.display = 'table-row';
				//alert("woTaxTR ===>> "+cnt+ "   -- " + document.getElementById('woTaxTR'+cnt));
				if(document.getElementById('strWriteOffTaxPercent'+cnt))
					document.getElementById('strWriteOffTaxPercent'+cnt).value = "0";
				if(document.getElementById('strWriteOffTaxAmount'+cnt))
					document.getElementById('strWriteOffTaxAmount'+cnt).value = "0";
			<% i++; } } %>
			
			
		} else {
			
			document.getElementById('woTypeTR').style.display = 'none';
			document.getElementById('woPercentTR').style.display = 'none';
			document.getElementById('woAmountTR').style.display = 'none';
			
			//document.getElementById('ins6').style.display = 'table-row';
			document.getElementById('writeOffType').selectedIndex = '0';
			if(document.getElementById('writeOffPercent'))
				document.getElementById('writeOffPercent').value = "0";
			if(document.getElementById('writeOffAmount'))
				document.getElementById('writeOffAmount').value = "0";
			if(document.getElementById('hidewriteOffAmount'))
				document.getElementById('hidewriteOffAmount').value = "0";
			if(document.getElementById('hidewriteOffPercent'))
				document.getElementById('hidewriteOffPercent').value = "0";
			
			<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				document.getElementById('woOPETR'+cnt).style.display = 'none';
				if(document.getElementById('strWriteOffOPEPercent'+cnt))
					document.getElementById('strWriteOffOPEPercent'+cnt).value = "0";
				if(document.getElementById('strWriteOffOPEAmount'+cnt))
					document.getElementById('strWriteOffOPEAmount'+cnt).value = "0";
			<% i++; } } %>
		
			
			<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				document.getElementById('woPartiTR'+cnt).style.display = 'none';
				if(document.getElementById('strWriteOffParticularsPercent'+cnt))
					document.getElementById('strWriteOffParticularsPercent'+cnt).value = "0";
				if(document.getElementById('strWriteOffParticularsAmount'+cnt))
					document.getElementById('strWriteOffParticularsAmount'+cnt).value = "0";
			<% i++; } } %>
			
			
			<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
				Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
			%>
				var cnt = <%=uF.showData(i+"", "0") %>;
				document.getElementById('woTaxTR'+cnt).style.display = 'none';
				if(document.getElementById('strWriteOffTaxPercent'+cnt))
					document.getElementById('strWriteOffTaxPercent'+cnt).value = "0";
				if(document.getElementById('strWriteOffTaxAmount'+cnt))
					document.getElementById('strWriteOffTaxAmount'+cnt).value = "0";
			<% i++; } } %>
			
			calculateBalanceAmount();
			
		}
	}

	
	function showInstrumentInfo(val) {
		if(val && (val=='Q' || val=='D')) {
			document.getElementById('ins1').style.display = 'table-row';
			document.getElementById('ins2').style.display = 'table-row';
		} else {
			document.getElementById('ins1').style.display = 'none';
			document.getElementById('ins2').style.display = 'none';
		}
	}
	
	
	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
	      return false;
	   }
	   return true;
	}
	
</script>
</g:compress>


<s:form theme="simple" id="frmReceiveMultipleBill" action="ReceiveBill" method="POST" name="frm" cssClass="formcss">
	<s:hidden name="invoiceId"></s:hidden>
	<s:hidden name="currId"></s:hidden>
	<s:hidden name="proId"></s:hidden>
	<s:hidden name="proFreqId"></s:hidden>
	<s:hidden name="stateId"></s:hidden>
	<s:hidden name="proType" id="proType"/>

	<table class="table table_bordered">

		<tr>
			<td class="txtlabel alignRight">Invoice Amount:<sup>*</sup></td>
			<td><s:textfield name="invoiceAmount" id="invoiceAmount" cssClass="validateRequired" readonly="true" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Amount Due:<sup>*</sup>
			</td>
			<td><s:textfield name="amountDue" id="amountDue" cssClass="validateRequired" readonly="true" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Exchange rate:<sup>*</sup>
			</td>
			<td><s:textfield name="exchangeRate" id="exchangeRate" cssClass="validateRequired" onkeyup="calculateExchangeRate();" onkeypress="return isNumberKey(event)" />
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Amount Received:<sup>*</sup>
			</td>
			<td><s:textfield name="amountReceived" id="amountReceived" cssClass="validateRequired" onkeyup="calculateBalanceAmount();"
					onkeypress="return isNumberKey(event)" /><%=uF.showData(""+hmCurr.get("LONG_CURR"), "") %></td>
		</tr>

		<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String billHeadId = it.next();	
			List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
		%>
		<tr>
			<td class="txtlabel alignRight"><input type="hidden" name="strHideOPE" value="<%=uF.showData(innerList.get(1),"-") %>" />
				<%=uF.showData(innerList.get(1),"-") %>:<sup>*</sup>
			</td>
			<td><input type="hidden" name="strHideOPEPercent" id="strHideOPEPercent<%=i %>" /> <input type="text"
				name="strOPEAmount" id="strOPEAmount<%=i %>" class="validateRequired" onkeyup="calcuOPE();"
				onkeypress="return isNumberKey(event)" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
			</td>
		</tr>
		<% i++; } } %>

		<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String billHeadId = it.next();	
			List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
		%>
		<tr>
			<td class="txtlabel alignRight"><input type="hidden" name="strHideParticulars" value="<%=uF.showData(innerList.get(1),"-") %>" /> <%=uF.showData(innerList.get(1),"-") %>:</td>
			<td><input type="hidden" name="strHideParticularsPercent" id="strHideParticularsPercent<%=i %>" /> 
				<input type="text" name="strParticularsAmount" id="strParticularsAmount<%=i %>" readonly="readonly" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
			</td>
		</tr>
		<% i++; } } %>

		<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();	
			List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
		%>
		<tr>
			<td class="txtlabel alignRight"><input type="hidden" name="strHideTax" value="<%=uF.showData(innerList.get(1),"-") %>" />
				<%=uF.showData(innerList.get(1),"-") %>:</td>
			<td><input type="hidden" name="strHideTaxPercent" id="strHideTaxPercent<%=i %>" /> 
				<input type="text" name="strTaxAmount" id="strTaxAmount<%=i %>" readonly="readonly" />
				<%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %></td>
		</tr>
		<% i++; } } %>

		<tr>
			<td colspan="2" style="padding-left: 65px; border-bottom: 1px solid #ccc"></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Tax deducted?</td>
			<td><s:checkbox name="deductTDS" onclick="checkTDS(this)" />
			</td>
		</tr>

		<tr style="display: none" id="taxD1">
			<td class="txtlabel alignRight">Deduction Type:<sup>*</sup>
			</td>
			<td><input type="hidden" name="hideTotTaxAmount" id="hideTotTaxAmount" /> <input type="hidden" name="hideTotTaxPercent" id="hideTotTaxPercent" /> 
				<s:select list="#{'2':'Amount'}" name="deductionType" id="deductionType" headerKey="1" headerValue="Percentage" onchange="checkDeductionFields(this.value);" />
			</td>
		</tr>


		<% 
		if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
			Iterator<String> it = hmProTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();
			List<String> innerList = hmProTaxHeadData.get(taxHeadId);
		%>
		<tr style="display: none" id="taxTR<%=i %>">
			<td class="txtlabel alignRight"><input type="hidden" name="hideTaxName" value="<%=uF.showData(innerList.get(1),"-") %>" />
				<%=uF.showData(innerList.get(1),"-") %>:<sup>*</sup></td>
			<td><input type="hidden" name="hideTaxAmount" id="hideTaxAmount<%=i %>" /> 
				<input type="hidden" name="hideTaxPercent" id="hideTaxPercent<%=i %>" /> 
				<span id="taxAmountSpan<%=i %>" style="float: left; display: none;">
					<input type="text" name="taxAmount" id="taxAmount<%=i %>" class="validateRequired" value="0" onkeyup="calculateAmount();"
					onkeypress="return isNumberKey(event)" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
					<span id="tax_per<%=i %>"></span> </span> 
				<span id="taxPercentSpan<%=i %>" style="float: left;"> 
					<input type="text" name="taxPercent" id="taxPercent<%=i %>" class="validateRequired" style="width: 80px;" value="0" onkeyup="calculateAmount();" onkeypress="return isNumberKey(event)" /> % 
					<span id="tax_amt<%=i %>"></span> </span> <% if(i==0) { %> 
					<span style="float: left; margin: 3px;"><s:checkbox name="prevYearTDS" onclick="checkPrevYearTDS(this);" /> Add Previous Year's <%=uF.showData(innerList.get(1),"-") %></span> <% } %>
			</td>
		</tr>

		<% if(i==0) { %>
		<tr style="display: none" id="taxDpy">
			<td class="txtlabel alignRight"><input type="hidden" name="hidePreviousYearTaxName" value="<%=uF.showData(innerList.get(1),"-") %>" /> Previous Year <%=uF.showData(innerList.get(1),"-") %>:<sup>*</sup>
			</td>
			<td><input type="hidden" name="hidePreviousYearTdsAmount" id="hidePreviousYearTdsAmount" /> 
				<input type="hidden" name="hidePreviousYearTdsPercent" id="hidePreviousYearTdsPercent" />
				<span id="previousYearTdsAmountSpan" style="display: none;">
					<s:textfield name="previousYearTdsAmount" id="previousYearTdsAmount" cssClass="validateRequired" value="0"
						onkeyup="calculateBalanceAmount();" onkeypress="return isNumberKey(event)" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
					<span id="previous_year_tds_per"></span> </span>
				<span id="previousYearTdsPercentSpan">
					<s:textfield name="previousYearTdsPercent" id="previousYearTdsPercent" cssClass="validateRequired" cssStyle="width: 80px;" value="0" onkeyup="calculateBalanceAmount();" onkeypress="return isNumberKey(event)" /> % 
					<span id="previous_year_tds_amt"></span> </span>
			</td>
		</tr>
		<% } %>

		<% i++; } } %>

		<tr>
			<td colspan="2" style="padding-left: 65px; border-bottom: 1px solid #ccc"></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Write off balance amount?</td>
			<td><s:checkbox name="writeoffBalance" onclick="checkWOff(this);" /></td>
		</tr>

		<tr style="display: none" id="woTypeTR">
			<td class="txtlabel alignRight">Write off Type:<sup>*</sup>
			</td>
			<td><input type="hidden" name="hidewriteOffAmount" id="hidewriteOffAmount" /> <input type="hidden" name="hidewriteOffPercent" id="hidewriteOffPercent" /> 
				<s:select list="#{'2':'Amount'}" name="writeOffType" id="writeOffType" headerKey="1" headerValue="Percentage" onchange="checkWriteOffFields(this.value);" />
			</td>
		</tr>

		<tr style="display: none" id="woPercentTR">
			<td class="txtlabel alignRight">Write off %:<sup>*</sup></td>
			<td><s:textfield name="writeOffPercent" id="writeOffPercent" cssClass="validateRequired" cssStyle="width: 80px;" value="0" onkeyup="checkWriteoffAmount();"
					onkeypress="return isNumberKey(event)" /> % <span id="w_o_writeOff_amt"></span>
			</td>
		</tr>

		<tr style="display: none" id="woAmountTR">
			<td class="txtlabel alignRight">Write off amount:<sup>*</sup>
			</td>
			<td><s:textfield name="writeOffAmount" id="writeOffAmount" value="0" cssClass="validateRequired" onkeyup="checkWriteoffAmount();"
					onkeypress="return isNumberKey(event)" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
				<span id="w_o_writeOff_per"></span></td>
		</tr>


		<% if(hmInvoiceOPEHeadData != null && !hmInvoiceOPEHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceOPEHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String billHeadId = it.next();	
			List<String> innerList = hmInvoiceOPEHeadData.get(billHeadId);
		%>
		<tr style="display: none" id="woOPETR<%=i %>">
			<td class="txtlabel alignRight"><input type="hidden" name="hidewoOPE" value="<%=uF.showData(innerList.get(1),"-") %>" />
				<%=uF.showData(innerList.get(1),"-") %>:<sup>*</sup>
			</td>
			<td><input type="hidden" name="hidewoOPEAmount" id="hidewoOPEAmount<%=i %>" /> 
				<input type="hidden" name="hidewoOPEPercent" id="hidewoOPEPercent<%=i %>" /> 
				<span id="writeOffOPEAmountSpan<%=i %>" style="display: none;"> 
				<input type="text" name="strWriteOffOPEAmount" id="strWriteOffOPEAmount<%=i %>" class="validateRequired" value="0" onkeyup="calcuWriteOffOPE();"
					onkeypress="return isNumberKey(event)" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
					<span id="w_o_ope_per<%=i %>"></span> </span>
				<span id="writeOffOPEPercentSpan<%=i %>"> 
					<input type="text" name="strWriteOffOPEPercent" id="strWriteOffOPEPercent<%=i %>" class="validateRequired" style="width: 80px;" value="0"
					onkeyup="calcuWriteOffOPE();" onkeypress="return isNumberKey(event)" /> % <span id="w_o_ope_amt<%=i %>"></span> 
				</span>
			</td>
		</tr>

		<% i++; } } %>

		<% if(hmInvoiceFixHeadData != null && !hmInvoiceFixHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceFixHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String billHeadId = it.next();	
			List<String> innerList = hmInvoiceFixHeadData.get(billHeadId);
		%>

		<tr style="display: none" id="woPartiTR<%=i %>">
			<td class="txtlabel alignRight"><input type="hidden" name="hidewoParti" value="<%=uF.showData(innerList.get(1),"-") %>" />
				<%=uF.showData(innerList.get(1),"-") %>:</td>
			<td><input type="hidden" name="hidewoPartiAmount" id="hidewoPartiAmount<%=i %>" /> 
				<input type="hidden" name="hidewoPartiPercent" id="hidewoPartiPercent<%=i %>" /> 
				<span id="writeOffParticularsAmountSpan<%=i %>" style="display: none;">
					<input type="text" name="strWriteOffParticularsAmount" id="strWriteOffParticularsAmount<%=i %>" value="0"
					readonly="readonly" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
					<span id="w_o_parti_per<%=i %>"></span> </span> 
				<span id="writeOffParticularsPercentSpan<%=i %>"> <input type="text" name="strWriteOffParticularsPercent"
					id="strWriteOffParticularsPercent<%=i %>" style="width: 80px;" value="0" readonly="readonly" /> % <span id="w_o_parti_amt<%=i %>"></span>
				</span>
			</td>
		</tr>

		<% i++; } } %>


		<% if(hmInvoiceTaxHeadData != null && !hmInvoiceTaxHeadData.isEmpty()) {
			Iterator<String> it = hmInvoiceTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();	
			List<String> innerList = hmInvoiceTaxHeadData.get(taxHeadId);
		%>

		<tr style="display: none" id="woTaxTR<%=i %>">
			<td class="txtlabel alignRight"><input type="hidden" name="hidewoTax" value="<%=uF.showData(innerList.get(1),"-") %>" />
				<%=uF.showData(innerList.get(1),"-") %>:</td>
			<td><input type="hidden" name="hidewoTaxAmount" id="hidewoTaxAmount<%=i %>" /> 
				<input type="hidden" name="hidewoTaxPercent" id="hidewoTaxPercent<%=i %>" /> 
				<span id="writeOffTaxAmountSpan<%=i %>" style="display: none;"> 
				<input type="text" name="strWriteOffTaxAmount" id="strWriteOffTaxAmount<%=i %>" class="validateRequired" value="0"
					readonly="readonly" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
					<span id="w_o_Tax_per<%=i %>"></span> </span> <span id="writeOffTaxPercentSpan<%=i %>"> 
						<input type="text" name="strWriteOffTaxPercent" id="strWriteOffTaxPercent<%=i %>"
						class="validateRequired" style="width: 80px;" value="0" readonly="readonly" /> % <span id="w_o_Tax_amt<%=i %>"></span> 
					</span>
				</td>
		</tr>

		<% i++; } } %>

		<tr style="display: none" id="ins12">
			<td class="txtlabel alignRight" valign="top">Write off Description:<sup>*</sup>
			</td>
			<td><s:textarea name="writeOffDescription" cssClass="validateRequired" rows="2" cols="22" /></td>
		</tr>

		<tr style="display: none" id="ins13">
			<td colspan="2" style="padding-left: 65px; border-bottom: 1px solid #ccc"></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Balance Amount:</td>
			<td><s:textfield name="balanceAmount" id="balanceAmount" readonly="true" value="0" /> <%=uF.showData(""+hmCurr.get("LONG_CURR"),"") %>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight" valign="top">Payment Description:</td>
			<td><s:textarea name="paymentDescription" id="loanDescription" rows="3" cols="22" /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Payment Mode:<sup>*</sup>
			</td>
			<td><s:select list="paymentSourceList" name="paymentSource" headerKey="O" headerValue="Other" listKey="paymentSourceId"
					listValue="paymentSourceName" onchange="showInstrumentInfo(this.value)"></s:select>
			</td>
		</tr>


		<tr style="display: none" id="ins1">
			<td class="txtlabel alignRight">Instrument No:<sup>*</sup>
			</td>
			<td><s:textfield name="strInstrumentNo" cssClass="validateRequired" />
			</td>
		</tr>

		<tr style="display: none" id="ins2">
			<td class="txtlabel alignRight">Instrument Date:<sup>*</sup>
			</td>
			<td><s:textfield name="strInstrumentDate" id="idInstrumentDate" cssClass="validateRequired" id="idInstrumentDate" />
			</td>
		</tr>

		<tr>
			<td></td>
			<td><s:submit cssClass="btn btn-primary" value="Save" name="submit" id="btnOk" />
			</td>
		</tr>

	</table>

</s:form>


<script type="text/javascript">
	$(function() {
		calculateBalanceAmount();
	});
	
	/* jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		$("#btnOk").click(function(){
			$(".validateRequired").prop('required',true);
			$(".validateNumber").prop('type','number');
			$(".validateNumber").prop('step','any');
		});
		
	}); */
</script>


