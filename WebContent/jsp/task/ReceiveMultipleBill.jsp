<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib prefix="g" uri="http://granule.com/tags"%>

<%
/* Map hmCurr = (HashMap)request.getAttribute("hmCurr");
if(hmCurr==null) hmCurr=new HashMap(); */

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF=new UtilityFunctions();

Map<String, String> hmTaxMiscSetting = (Map<String, String>) request.getAttribute("hmTaxMiscSetting");

List<List<String>> invoiceIDList = (List<List<String>>)request.getAttribute("invoiceIDList");

List<String> invoiceOPEHeads = (List<String>) request.getAttribute("invoiceOPEHeads");
List<String> invoicePartiHeads = (List<String>) request.getAttribute("invoicePartiHeads");
List<String> invoiceTaxHeads = (List<String>) request.getAttribute("invoiceTaxHeads");

Map<String, Map<String, List<String>>> hmInvoiceOPEHeads = (Map<String, Map<String, List<String>>>) request.getAttribute("hmInvoiceOPEHeads");
if(hmInvoiceOPEHeads == null) hmInvoiceOPEHeads = new HashMap<String, Map<String, List<String>>>();

Map<String, Map<String, List<String>>> hmInvoicePartiHeads = (Map<String, Map<String, List<String>>>) request.getAttribute("hmInvoicePartiHeads");
if(hmInvoicePartiHeads == null) hmInvoicePartiHeads = new HashMap<String, Map<String, List<String>>>();

Map<String, Map<String, List<String>>> hmInvoiceTaxHeads = (Map<String, Map<String, List<String>>>) request.getAttribute("hmInvoiceTaxHeads");
if(hmInvoiceTaxHeads == null) hmInvoiceTaxHeads = new HashMap<String, Map<String, List<String>>>();

Map<String, Map<String, List<String>>> hmInvoiceProTaxHeads = (Map<String, Map<String, List<String>>>) request.getAttribute("hmInvoiceProTaxHeads");
if(hmInvoiceProTaxHeads == null) hmInvoiceProTaxHeads = new HashMap<String, Map<String, List<String>>>();

%>
 
<g:compress>
<script>
	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		$("#btnOk").click(function(){
			$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
			$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
		});
	});

	$(function() {
		<% List<String> invoiceIdList = (List<String>) request.getAttribute("invoiceIdList"); 
		if (invoiceIdList != null && invoiceIdList.size() != 0) {
			for (int i = 0; i < invoiceIdList.size(); i++) {
		%>
	    $("#idInstrumentDate_<%=invoiceIdList.get(i) %>").datepicker({format: 'dd/mm/yyyy'});
	    <% } } %>
	});


	function showInstrumentInfo(val, invcID) {
		if(val && (val=='Q' || val=='D')) { 
			document.getElementById('instrumentNoSpan_'+invcID).style.display = 'block';
			document.getElementById('instrumentDateSpan_'+invcID).style.display = 'block';
			document.getElementById('strInstrumentNo_'+invcID).value = '';
			document.getElementById('idInstrumentDate_'+invcID).value = '';
			
		} else {
			document.getElementById('strInstrumentNo_'+invcID).value = '';
			document.getElementById('idInstrumentDate_'+invcID).value = '';
			document.getElementById('instrumentNoSpan_'+invcID).style.display = 'none';
			document.getElementById('instrumentDateSpan_'+invcID).style.display = 'none';
		}
	}
	
	
	
	function receiveMultipleBill() {
		var allReceiveBill = document.getElementById("allReceiveBill");		
		var receiveBill = document.getElementsByName('receiveBill');
		var selectID="";
		var projectFreqID="";
		var projectID="";
		var invoiceID="";
		var clientID="";
		var status=false;
		var receiptCnt=0;
		//alert("allReceiveBill.checked ==>> " + allReceiveBill.checked);
		
			if(allReceiveBill.checked == true) {
				status=true;
				 for(var i=0;i<receiveBill.length;i++){
					// receiveBill[i].checked = true;
					 if(receiveBill[i].checked) { 
						 if(selectID == '') {
							  selectID = receiveBill[i].value.split('_');
							  projectFreqID = selectID[0];
							  projectID = selectID[1];
							  invoiceID = selectID[2];
							  clientID = selectID[3];
						  } else {
							  selectID = receiveBill[i].value.split('_');
							  projectFreqID += ","+selectID[0];
							  projectID += ","+selectID[1];
							  invoiceID += ","+selectID[2];
							  clientID += ","+selectID[3];
						  }
						 receiptCnt++;
					 }
				 }
			} else {		
				status = false;
				 for(var i=0; i<receiveBill.length; i++) {
					 //receiveBill[i].checked = false;
					  if(receiveBill[i].checked) {
						  if(selectID == '') {
							  selectID = receiveBill[i].value.split('_');
							  projectFreqID = selectID[0];
							  projectID = selectID[1];
							  invoiceID = selectID[2];
							  clientID = selectID[3];
						  } else {
							  selectID = receiveBill[i].value.split('_');
							  projectFreqID += ","+selectID[0];
							  projectID += ","+selectID[1];
							  invoiceID += ","+selectID[2];
							  clientID += ","+selectID[3];
						  }
						  receiptCnt++;
					  }
				 }
			}
			//alert("receiptCnt ===>> " + receiptCnt);
			
			if(receiptCnt > 10) {
				alert("Please, select only ten receipts & try again.");
			} else {
				if(projectID != '' && invoiceID != '' && clientID != '' && projectFreqID != '') {
					document.getElementById("pro_id").value = projectID;
					document.getElementById("invoice_id").value = invoiceID;
					document.getElementById("client_id").value = clientID;
					document.getElementById("pro_freq_id").value = projectFreqID;
					//alert("dsfs");
					document.forms["frmReceiveMultipleBill"].submit();
					//document.getElementById("frmReceiveMultipleBill").submit();
					//window.location = "ReceiveMultipleBill.action?invoice_id="+invoiceID+"&pro_id="+projectID+"&client_id="+clientID+"&pro_freq_id="+projectFreqID;
				}
			}
		}
	   

		function checkMultipleBill() {
			var allReceiveBill = document.getElementById("allReceiveBill");		
			var receiveBill = document.getElementsByName('receiveBill');
			//alert("allReceiveBill.checked ==>> " + allReceiveBill.checked);
			var cnt = 0;
			var selectID="";
			var projectFreqID="";
			var projectID="";
			var invoiceID="";
			var clientID="";
			if(allReceiveBill.checked == true) {
				 for(var i=0;i<receiveBill.length;i++) {
					 if(i>9) {
						 alert("You can select only ten receipts at a time.");
						 break;
					 } else {
						 receiveBill[i].checked = true;
						 if(selectID == '') {
							  selectID = receiveBill[i].value.split('_');
							  projectFreqID = selectID[0];
							  projectID = selectID[1];
							  invoiceID = selectID[2];
							  clientID = selectID[3];
						  } else {
							  selectID = receiveBill[i].value.split('_');
							  projectFreqID += ","+selectID[0];
							  projectID += ","+selectID[1];
							  invoiceID += ","+selectID[2];
							  clientID += ","+selectID[3];
						  }
						 cnt++;
					 }
				 }
				 
				 if(projectID != '' && invoiceID != '' && clientID != '' && projectFreqID != '') {
					document.getElementById("pro_id").value = projectID;
					document.getElementById("invoice_id").value = invoiceID;
					document.getElementById("client_id").value = clientID;
					document.getElementById("pro_freq_id").value = projectFreqID;
				}
			} else {		
				 for(var i=0; i<receiveBill.length; i++) {
					 receiveBill[i].checked = false;
				 }
				 document.getElementById("pro_id").value = "";
				document.getElementById("invoice_id").value = "";
				document.getElementById("client_id").value = "";
				document.getElementById("pro_freq_id").value = "";
			}
			
		}


		function checkAllBillChecked(invcId) {
			var allReceiveBill = document.getElementById("allReceiveBill");		
			var receiveBill = document.getElementsByName('receiveBill');
			var cnt = 0;
			var chkCnt = 0;
			var selectID="";
			var projectFreqID="";
			var projectID="";
			var invoiceID="";
			var clientID="";
			for(var i=0;i<receiveBill.length;i++) {
				cnt++;
				 if(receiveBill[i].checked) {
					 if(selectID == '') {
						  selectID = receiveBill[i].value.split('_');
						  projectFreqID = selectID[0];
						  projectID = selectID[1];
						  invoiceID = selectID[2];
						  clientID = selectID[3];
					  } else {
						  selectID = receiveBill[i].value.split('_');
						  projectFreqID += ","+selectID[0];
						  projectID += ","+selectID[1];
						  invoiceID += ","+selectID[2];
						  clientID += ","+selectID[3];
					  }
					 chkCnt++;
				 }
			 }
			 if(chkCnt>10) {
				 alert("You can select only ten receipts at a time.");
				 document.getElementById("receiveBill_"+invcId).checked = false;
				 chkCnt = 10;
			 } else {
				 if(projectID != '' && invoiceID != '' && clientID != '' && projectFreqID != '') {
					document.getElementById("pro_id").value = projectID;
					document.getElementById("invoice_id").value = invoiceID;
					document.getElementById("client_id").value = clientID;
					document.getElementById("pro_freq_id").value = projectFreqID;
				}
			 }
			if(cnt == chkCnt) {
				allReceiveBill.checked = true;
			} else {
				allReceiveBill.checked = false;
			}
			
			/* if(chkCnt > 0) {
				document.getElementById("selectReceiptsSpan").style.display = "block";
				document.getElementById("unselectReceiptsSpan").style.display = "none";
			} else {
				document.getElementById("selectReceiptsSpan").style.display = "none";
				document.getElementById("unselectReceiptsSpan").style.display = "block";
			} */
			
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
<% 
	Map<String, List<String>> hmInvoiceDetails = (Map<String, List<String>>) request.getAttribute("hmInvoiceDetails");
	Map<String, String> hmClientData = (Map<String, String>) request.getAttribute("hmClientData");
	Map<String, String> hmProWStateId = (Map<String, String>) request.getAttribute("hmProWStateId");
	List<String> strInvcIdList = (List<String>)request.getAttribute("strInvcIdList");
	int colCnt = 12;
%>

<section class="content">
          <!-- title row -->
	        <div class="row"> 
				<div class="col-md-12">
				
<div class="box box-body">

<s:form theme="simple" id="frmReceiveMultipleBill" action="ReceiveMultipleBill" method="POST" name="frmReceiveMultipleBill" cssClass="formcss">
	<s:hidden name="invoice_id" id="invoice_id"></s:hidden>
	<s:hidden name="pro_id" id="pro_id"></s:hidden>
	<s:hidden name="pro_freq_id" id="pro_freq_id"></s:hidden>
	<s:hidden name="client_id" id="client_id"></s:hidden>
	
	<div style="float: left; width: 20%; margin-right: 5px; border-right: 2px solid #CCCCCC;">
	<div style="border-bottom: 1px solid #CCCCCC; width: 98%;"> <span style="font-weight: normal;"><input type="checkbox" name="allReceiveBill" id="allReceiveBill" onclick="checkMultipleBill();" /> Select All Receipt</span>
	<span style="margin-left: 7px;"> <input type="submit" class="btn btn-primary" style="margin-bottom: 7px;" value="Submit" name="search"/> </span>
	</div>
	<% 	for(int i=0; invoiceIDList != null && !invoiceIDList.isEmpty() && i<invoiceIDList.size(); i++) {
			List<String> innerList = invoiceIDList.get(i);
	%>
		<div>
			<input type="checkbox" name="receiveBill" id="receiveBill_<%=innerList.get(0) %>" value="<%=innerList.get(9)+"_"+innerList.get(1)+"_"+innerList.get(0)+"_"+innerList.get(2)%>"  onclick="checkAllBillChecked('<%=innerList.get(0) %>');"
			<% if(strInvcIdList !=null && strInvcIdList.contains(innerList.get(0))) { %> checked="checked" <% } %> />
			<%=innerList.get(3) %> <%=innerList.get(8) %> <%=innerList.get(5) %>
		</div>	
	<% } %>
	<!-- <div style="text-align: center;"><input type="submit" class="input_button" value="Submit" name="search"/></div> -->
	</div>
	
	<div style="float: left; width: 79%;">
		<div style="width: 100%; overflow-x: auto;">
		
		<% if(hmInvoiceDetails !=null && !hmInvoiceDetails.isEmpty()) { %>
		<table class="table table_bordered"> <!-- formcss -->
			<tr class="darktable">
				<th nowrap="nowrap" class="txtlabel alignCenter"> Invoice No. </th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Invoice Amount<sup>*</sup> </th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Amount Due<sup>*</sup> </th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Exchange rate<sup>*</sup> </th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Amount Received<sup>*</sup> </th>
				<% if(invoiceOPEHeads != null && !invoiceOPEHeads.isEmpty()) { 
					for(int i=0; i<invoiceOPEHeads.size(); i++) {
						colCnt++;
				%>
					<th nowrap="nowrap" class="txtlabel alignCenter"><%=invoiceOPEHeads.get(i) %></th>
				<% } } %>
				
				<% if(invoicePartiHeads != null && !invoicePartiHeads.isEmpty()) { 
					for(int i=0; i<invoicePartiHeads.size(); i++) {
						colCnt++;
				%>
					<th nowrap="nowrap" class="txtlabel alignCenter"><%=invoicePartiHeads.get(i) %></th>
				<% } } %>
				
				<% if(invoiceTaxHeads != null && !invoiceTaxHeads.isEmpty()) { 
					for(int i=0; i<invoiceTaxHeads.size(); i++) {
						colCnt++;
				%>
					<th nowrap="nowrap" class="txtlabel alignCenter"><%=invoiceTaxHeads.get(i) %></th>
				<% } } %>
				
				<th nowrap="nowrap" class="txtlabel alignCenter">Tax deducted? </th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Write off balance amount? </th>
				
				<th nowrap="nowrap" class="txtlabel alignCenter">Balance Amount</th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Payment Description</th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Payment Mode<sup>*</sup></th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Instrument No<sup>*</sup></th>
				<th nowrap="nowrap" class="txtlabel alignCenter">Instrument Date<sup>*</sup></th>
			</tr>
		
		<% 
		Iterator<String> itInvcId = hmInvoiceDetails.keySet().iterator();
		while(itInvcId.hasNext()) {
			String invoiceId = itInvcId.next();
			List<String> innerList = hmInvoiceDetails.get(invoiceId);
			
			Map<String, List<String>> hmInvoicewiseOPEHeads = hmInvoiceOPEHeads.get(invoiceId);
			if(hmInvoicewiseOPEHeads == null) hmInvoicewiseOPEHeads = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmInvoicewisePartiHeads = hmInvoicePartiHeads.get(invoiceId);
			if(hmInvoicewisePartiHeads == null) hmInvoicewisePartiHeads = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmInvoicewiseTaxHeads = hmInvoiceTaxHeads.get(invoiceId);
			if(hmInvoicewiseTaxHeads == null) hmInvoicewiseTaxHeads = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmProTaxHeadData = hmInvoiceProTaxHeads.get(invoiceId);
			if(hmProTaxHeadData == null) hmProTaxHeadData = new HashMap<String, List<String>>();
			
		%>
		
		<script type="text/javascript">
		
		
		function calculateAmount(invcID, billCurr) {
			calculateBalanceAmount(invcID, billCurr);
		}
		
		
		/* function mainFun(invcID) { */
		
			//alert("invcID mainFun ===>> " + invcID);
			
			<%-- <% 
			Iterator<String> itMain = hmInvoiceDetails.keySet().iterator();
			while(itMain.hasNext()) {
				String invoiceIdJS = itMain.next();
			%>
			var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
			
			if(invcID == invoiceID) {
				alert("invcID invoiceID mainFun ===>> " + invcID); 
				<% invoiceIdJS = invoiceIdJS;  %>
				calculateBalanceAmount(invcID);	
			}	 --%>
				function calculateBalanceAmount(invcID, billCurr) {
					
					<% Iterator<String> itCBA = hmInvoiceDetails.keySet().iterator(); %>
					//alert("invcID calculateBalanceAmount ===>> " + invcID); 
					//var exchangeRate = parseFloat(document.frm.exchangeRate.value).toFixed(2);
					var dueAmount = parseFloat(document.getElementById('amountDue_'+invcID).value).toFixed(2);
					var receivedAmount = parseFloat(document.getElementById('amountReceived_'+invcID).value).toFixed(2);
					if(receivedAmount == '') {
						receivedAmount = 0;
					}
					//alert("receivedAmount calculateBalanceAmount ===>> " + receivedAmount);
					
					var writeOffAmt = 0;
					if(document.getElementById('hidewriteOffAmount_'+invcID))
						writeOffAmt = parseFloat(document.getElementById('hidewriteOffAmount_'+invcID).value).toFixed(2);
					if(writeOffAmt == 'NaN' || writeOffAmt == '') {
						writeOffAmt = 0;
					}
					
					//alert("writeOffAmt calculateBalanceAmount ===>> " + writeOffAmt);
					
					if(dueAmount>0) {
						var deductType = "";
						if(document.getElementById('deductionType_'+invcID))
							deductType = document.getElementById('deductionType_'+invcID).value;
						
						//alert("deductType calculateBalanceAmount ===>> " + deductType);
						
						if(deductType == '1') {
						var balanceAmount = 0;
						var baseTaxName = '';
						var allTaxAmount = 0;
						var allTaxPercent = 0;
						
						<% while(itCBA.hasNext()) {
								String invoiceIdJS = itCBA.next();
						%>
						var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
						
						//alert("invoiceID ===>> " + invoiceID);
						
						if(invcID == invoiceID) {
						//alert("invcID ===>> " + invcID);	
						<%
						//System.out.println("invoiceIdJS ===>> " + invoiceIdJS);
							Map<String, List<String>> hmProTaxHeadDataJS = hmInvoiceProTaxHeads.get(invoiceIdJS);
							if(hmProTaxHeadDataJS != null && !hmProTaxHeadDataJS.isEmpty()) {
							Iterator<String> it = hmProTaxHeadDataJS.keySet().iterator();
							int i = 0;
							while(it.hasNext()) {
							String taxHeadId = it.next();
							List<String> innerList1 = hmProTaxHeadDataJS.get(taxHeadId);
						%>						
						//alert("taxName calculateBalanceAmount ===>> ");
							var taxName = '<%=uF.showData(innerList1.get(1), "0") %>';
							//alert("taxName calculateBalanceAmount ===>> " + taxName);
							<% if(i==0) { %>
								baseTaxName = taxName;
							<% } %>
							var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
							var taxPercent = 0;
							if(document.getElementById('taxPercent_'+invcID+'_'+cnt))
								taxPercent = document.getElementById('taxPercent_'+invcID+'_'+cnt).value;
							//alert("0 ===>> ");
							if(taxPercent == 'NaN' || taxPercent == '') {
								taxPercent = 0;
							}
							var taxAmount = (dueAmount * taxPercent) / 100;
							allTaxAmount = parseFloat(allTaxAmount) + parseFloat(taxAmount);
							allTaxPercent = parseFloat(allTaxPercent) + parseFloat(taxPercent);
							//alert("1 ===>> ");
							if(document.getElementById('hideTaxAmount_'+invcID+'_'+cnt))
								document.getElementById('hideTaxAmount_'+invcID+'_'+cnt).value = parseFloat(taxAmount).toFixed(2);
							//alert("2 ===>> ");
							if(document.getElementById('hideTaxPercent_'+invcID+'_'+cnt))
								document.getElementById('hideTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
							//alert("3 ===>> ");
							if(document.getElementById('tax_amt_'+invcID+'_'+cnt))
								document.getElementById('tax_amt_'+invcID+'_'+cnt).innerHTML = ' ('+taxName+' deducted equal to '+taxAmount.toFixed(2)+' '+billCurr+')';
							//alert("4 ===>> ");
						<% i++; } } %>
						}
						<% } %>
						//alert("hmProTaxHeadData calculateBalanceAmount ===>> " );
						
						if(document.getElementById('previousYearTdsPercent_'+invcID)) {
							var pyTaxPercent = document.getElementById('previousYearTdsPercent_'+invcID).value;
							if(pyTaxPercent == 'NaN' || pyTaxPercent == '') {
								pyTaxPercent = 0;
							}
							var pyTaxAmount = (dueAmount * pyTaxPercent) / 100;
							allTaxAmount = parseFloat(allTaxAmount) + parseFloat(pyTaxAmount);
							allTaxPercent = parseFloat(allTaxPercent) + parseFloat(pyTaxPercent);
							
							document.getElementById('hidePreviousYearTdsAmount_'+invcID).value = parseFloat(pyTaxAmount).toFixed(2);
							document.getElementById('hidePreviousYearTdsPercent_'+invcID).value = parseFloat(pyTaxPercent).toFixed(2);
							document.getElementById('previous_year_tds_amt_'+invcID).innerHTML = ' (Previous Year '+baseTaxName+' deducted equal to '+pyTaxAmount.toFixed(2)+' '+billCurr+')';
						}
						//alert("previousYearTdsPercent_ calculateBalanceAmount ===>> ");
						
						document.getElementById('hideTotTaxAmount_'+invcID).value = parseFloat(allTaxAmount).toFixed(2);
						document.getElementById('hideTotTaxPercent_'+invcID).value = parseFloat(allTaxPercent).toFixed(2);
						
						//alert("hideTotTaxPercent_ calculateBalanceAmount ===>> ");
						
							if(receivedAmount>0) {
								balanceAmount = parseFloat(dueAmount) - (parseFloat(receivedAmount) + parseFloat(writeOffAmt) + parseFloat(allTaxAmount));
							} 
							if(balanceAmount>=0) {
								document.getElementById('balanceAmount_'+invcID).value = balanceAmount.toFixed(2);	
							} else {
								document.getElementById('balanceAmount_'+invcID).value = '0.0';
							}
							//alert("balanceAmount calculateBalanceAmount ===>> " + balanceAmount);
							
						} else {
							var balanceAmount = 0;
							var baseTaxName = '';
							var allTaxAmount = 0;
							var allTaxPercent = 0;
							<% Iterator<String> itCBAAmt = hmInvoiceDetails.keySet().iterator();
								while(itCBAAmt.hasNext()) {
									String invoiceIdJS = itCBAAmt.next();
							%>
							var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
							
							if(invcID == invoiceID) {
							<%
							Map<String, List<String>> hmProTaxHeadDataJS = hmInvoiceProTaxHeads.get(invoiceIdJS);
								if(hmProTaxHeadDataJS != null && !hmProTaxHeadDataJS.isEmpty()) {
								Iterator<String> it = hmProTaxHeadDataJS.keySet().iterator();
								int i = 0;
								while(it.hasNext()) {
								String taxHeadId = it.next();
								List<String> innerList1 = hmProTaxHeadDataJS.get(taxHeadId);
							%>						
								var taxName = '<%=uF.showData(innerList1.get(1), "0") %>';
								<% if(i==0) { %>
									baseTaxName = taxName;
								<% } %>
								var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
								var taxAmount = 0;
								if(document.getElementById('taxAmount_'+invcID+'_'+cnt))
									taxAmount = document.getElementById('taxAmount_'+invcID+'_'+cnt).value;
								if(taxAmount == 'NaN' || taxAmount == '') {
									taxAmount = 0;
								}
								allTaxAmount = parseFloat(allTaxAmount) + parseFloat(taxAmount);
								allTaxPercent = parseFloat(allTaxPercent) + parseFloat(taxPercent);
								
								var taxPercent = ((taxAmount * 100)/dueAmount).toFixed(2);
								if(document.getElementById('hideTaxAmount_'+invcID+'_'+cnt))
									document.getElementById('hideTaxAmount_'+invcID+'_'+cnt).value = parseFloat(taxAmount).toFixed(2);
								if(document.getElementById('hideTaxPercent_'+invcID+'_'+cnt))
									document.getElementById('hideTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
								
								if(document.getElementById('tax_per_'+invcID+'_'+cnt))
									document.getElementById('tax_per_'+invcID+'_'+cnt).innerHTML = ' ('+taxName+' deducted @ '+taxPercent+'%)';
							<% i++; } } %>
							}
						<% } %>	
						
							if(document.getElementById('previousYearTdsAmount_'+invcID)) {
								var pyTaxAmount = document.getElementById('previousYearTdsAmount_'+invcID).value;
								if(pyTaxAmount == 'NaN' || pyTaxAmount == '') {
									pyTaxAmount = 0;
								}
								var pyTaxPercent = ((pyTaxAmount * 100)/dueAmount).toFixed(2);
								allTaxAmount = parseFloat(allTaxAmount) + parseFloat(pyTaxAmount);
								allTaxPercent = parseFloat(allTaxPercent) + parseFloat(pyTaxPercent);
								
								document.getElementById('hidePreviousYearTdsAmount_'+invcID).value = parseFloat(pyTaxAmount).toFixed(2);
								document.getElementById('hidePreviousYearTdsPercent_'+invcID).value = parseFloat(pyTaxPercent).toFixed(2);
								document.getElementById('previous_year_tds_per_'+invcID).innerHTML = ' ('+baseTaxName+' deducted @ '+pyTaxPercent+'%)';
							}
							
							document.getElementById('hideTotTaxAmount_'+invcID).value = parseFloat(allTaxAmount).toFixed(2);
							document.getElementById('hideTotTaxPercent_'+invcID).value = parseFloat(allTaxPercent).toFixed(2);
							
							//alert("receivedAmount ====>> " + receivedAmount + "  writeOffAmt ===>> " + writeOffAmt + "  allTaxAmount ===>> " + allTaxAmount);
							if(receivedAmount>0) {
								balanceAmount = parseFloat(dueAmount) - (parseFloat(receivedAmount) + parseFloat(writeOffAmt) + parseFloat(allTaxAmount));
							} 
							if(balanceAmount>=0) {
								document.getElementById('balanceAmount_'+invcID).value = balanceAmount.toFixed(2);	
							} else {
								document.getElementById('balanceAmount_'+invcID).value = '0.0';
							}
						}
						
					} else if(dueAmount<=0) {
						alert('Amount due can not be less than or equal zero');
					}
					//alert("endddd ........");
					calAmt(invcID);
				}
				
				
				
				function calAmt(invcID) {
					<% Iterator<String> itCA = hmInvoiceDetails.keySet().iterator(); %>
					
					var invoiceAmount = parseFloat(document.getElementById('invoiceAmount_'+invcID).value).toFixed(2);
					var receivedAmount = parseFloat(document.getElementById('amountReceived_'+invcID).value).toFixed(2);
					
					//alert("receivedAmount ===>> " + receivedAmount);
					/* var receivedAmount = parseFloat(document.frm.amountReceived.value).toFixed(2);
					var invoiceAmount = parseFloat(document.frm.invoiceAmount.value).toFixed(2); */
					if(receivedAmount == '') {
						receivedAmount = 0;
					}
					
					var receivedAmtPercent = (parseFloat(receivedAmount) * 100) / parseFloat(invoiceAmount);
					//alert("receivedAmtPercent ===>> " + receivedAmtPercent);
					
					var allPercent = 0;
					
					<% while(itCA.hasNext()) {
							String invoiceIdJS = itCA.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					if(invcID == invoiceID) {
						//alert("invcID 1 ===>> " + invcID);
					<% 
					Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
					//System.out.println("invoiceIdJS hmInvoicewiseOPEHeadsJS ===>> " +invoiceIdJS + " --- " + hmInvoicewiseOPEHeadsJS);
					if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String billHeadId = it.next();	
						List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						var OPEAmount = <%=uF.showData(innerList1.get(3), "0") %>;
						var opeAmt = (parseFloat(OPEAmount) * parseFloat(receivedAmtPercent)) / 100;
						if(parseFloat(receivedAmount) > 0) {
							opePer = (parseFloat(opeAmt) * 100) / parseFloat(receivedAmount);
						}
						allPercent = parseFloat(allPercent) + parseFloat(opePer);
						
						document.getElementById('strOPEAmount_'+invcID+'_'+cnt).value = parseFloat(opeAmt).toFixed(2);
						document.getElementById('strHideOPEPercent_'+invcID+'_'+cnt).value = parseFloat(opePer).toFixed(2);
					<% i++; } } %>
					}
					<% } %>
					//alert("after hmInvoicewiseOPEHeadsJS ===>> ");
					var baseCnt = '0';
					<% itCA = hmInvoiceDetails.keySet().iterator();
						while(itCA.hasNext()) {
							String invoiceIdJS = itCA.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					if(invcID == invoiceID) {
						//alert("invcID 2 ===>> " + invcID);
					<% 
					Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
					//System.out.println("invoiceIdJS hmInvoicewisePartiHeadsJS ===>> " + invoiceIdJS + " --- " + hmInvoicewisePartiHeadsJS);
					
					if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String billHeadId = it.next();	
						List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						var partiAmount = <%=uF.showData(innerList1.get(3), "0") %>;
						//alert("hmInvoicewisePartiHeadsJS baseCnt ===>> " + baseCnt);
						var partiAmt = (parseFloat(partiAmount) * parseFloat(receivedAmtPercent)) / 100;
						var partiPer =0;
						if(parseFloat(receivedAmount) > 0) {
							partiPer = (parseFloat(partiAmt) * 100) / parseFloat(receivedAmount);
						}
						<% if(i>0) { %>
							allPercent = parseFloat(allPercent) + parseFloat(partiPer);
						<% } else { %>
							baseCnt = '<%=uF.showData(innerList1.get(0), "0") %>';
						<% } %>
						//alert("after hmInvoicewisePartiHeads baseCnt ===>> " + baseCnt);
						
						document.getElementById('strParticularsAmount_'+invcID+'_'+cnt).value = parseFloat(partiAmt).toFixed(2);
						document.getElementById('strHideParticularsPercent_'+invcID+'_'+cnt).value = parseFloat(partiPer).toFixed(2);
					<% i++; } } %>
					}
					<% } %>
					//alert("after hmInvoicewisePartiHeadsJS ===>> ");
					
					<% itCA = hmInvoiceDetails.keySet().iterator();
						while(itCA.hasNext()) {
							String invoiceIdJS = itCA.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					
					if(invcID == invoiceID) {
						//alert("invcID 3 ===>> " + invcID);
					<% 
					Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
					//System.out.println("invoiceIdJS hmInvoicewiseTaxHeadsJS ===>> " + invoiceIdJS + " --- " + hmInvoicewiseTaxHeadsJS);
					if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String taxHeadId = it.next();
						List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						var taxPercent = <%=uF.showData(innerList1.get(5), "0") %>;
						var taxAmt = (parseFloat(receivedAmount) * parseFloat(taxPercent)) / 100;
						allPercent = parseFloat(allPercent) + parseFloat(taxPercent);
						if(parseFloat(receivedAmount) == 0) {
							taxPercent = 0;
						}
						document.getElementById('strTaxAmount_'+invcID+'_'+cnt).value = parseFloat(taxAmt).toFixed(2);
						document.getElementById('strHideTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
					<% i++; } } %>
					}
					<% } %>
					//alert("after hmInvoicewiseTaxHeadsJS ===>> ");
					
					var remainPercent = 100 - parseFloat(allPercent);
					if(parseFloat(receivedAmount) == 0) {
						remainPercent = 0;
					}
					
					var partiAmt = (parseFloat(remainPercent) * parseFloat(receivedAmount)) / 100;
					if(document.getElementById('strParticularsAmount_'+invcID+'_'+baseCnt)) {
						document.getElementById('strParticularsAmount_'+invcID+'_'+baseCnt).value = parseFloat(partiAmt).toFixed(2);
						document.getElementById('strHideParticularsPercent_'+invcID+'_'+baseCnt).value = parseFloat(remainPercent).toFixed(2);
					}
				}
		<%-- <% } %>
		} --%>
		
		
		function calcuOPE(invcID) {
			<% Iterator<String> itCOPE = hmInvoiceDetails.keySet().iterator(); %>
		
			var invoiceAmount = parseFloat(document.getElementById('invoiceAmount_'+invcID).value).toFixed(2);
			var receivedAmount = parseFloat(document.getElementById('amountReceived_'+invcID).value).toFixed(2);
			
			if(receivedAmount == '') {
				receivedAmount = 0;
			}
			
			var receivedAmtPercent = (parseFloat(receivedAmount) * 100) / parseFloat(invoiceAmount);
			
			var allPercent = 0;
			<% while(itCOPE.hasNext()) {
					String invoiceIdJS = itCOPE.next();
			%>
			var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
			if(invcID == invoiceID) {
			<% 
			Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
			if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
				Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
			%>
				var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
				var opeAmt = document.getElementById('strOPEAmount_'+invcID+'_'+cnt).value;
				if(opeAmt == '') {
					opeAmt = 0;
				}
				//var opeAmt = (parseFloat(OPEAmount) * parseFloat(receivedAmtPercent)) / 100;
				if(parseFloat(receivedAmount) > 0) {
					opePer = (parseFloat(opeAmt) * 100) / parseFloat(receivedAmount);
				}
				allPercent = parseFloat(allPercent) + parseFloat(opePer);
				
				document.getElementById('strOPEAmount_'+invcID+'_'+cnt).value = parseFloat(opeAmt).toFixed(2);
				document.getElementById('strHideOPEPercent_'+invcID+'_'+cnt).value = parseFloat(opePer).toFixed(2);
			<% i++; } } %>
			}
			<% } %>
			
			var baseCnt = '0';
			<% itCOPE = hmInvoiceDetails.keySet().iterator();
				while(itCOPE.hasNext()) {
					String invoiceIdJS = itCOPE.next();
			%>
			var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
			if(invcID == invoiceID) {
			<% 
			Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
			if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
				Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billHeadId = it.next();	
				List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
			%>
				var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
				var partiAmount = <%=uF.showData(innerList1.get(3), "0") %>;
				var partiAmt = (parseFloat(partiAmount) * parseFloat(receivedAmtPercent)) / 100;
				var partiPer =0;
				if(parseFloat(receivedAmount) > 0) {
					partiPer = (parseFloat(partiAmt) * 100) / parseFloat(receivedAmount);
				}
				<% if(i>0) { %>
				allPercent = parseFloat(allPercent) + parseFloat(partiPer);
				<% } else { %>
					baseCnt = <%=uF.showData(innerList1.get(0), "0") %>;
				<% } %>
				document.getElementById('strParticularsAmount_'+invcID+'_'+cnt).value = parseFloat(partiAmt).toFixed(2);
				document.getElementById('strHideParticularsPercent_'+invcID+'_'+cnt).value = parseFloat(partiPer).toFixed(2);
			<% i++; } } %>
			}
			<% } %>
			
			<% itCOPE = hmInvoiceDetails.keySet().iterator();
				while(itCOPE.hasNext()) {
					String invoiceIdJS = itCOPE.next();
			%>
			var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
			
			if(invcID == invoiceID) {
			<% 
			Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
			if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
				Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
			%>
				var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
				var taxPercent = <%=uF.showData(innerList1.get(5), "0") %>;
				var taxAmt = (parseFloat(receivedAmount) * parseFloat(taxPercent)) / 100;
				allPercent = parseFloat(allPercent) + parseFloat(taxPercent);
				if(parseFloat(receivedAmount) == 0) {
					taxPercent = 0;
				}
				document.getElementById('strTaxAmount_'+invcID+'_'+cnt).value = parseFloat(taxAmt).toFixed(2);
				document.getElementById('strHideTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
			<% i++; } } %>
			}
			<% } %>
			
			var remainPercent = 100 - parseFloat(allPercent);
			if(parseFloat(receivedAmount) == 0) {
				remainPercent = 0;
			}
			
			var partiAmt = (parseFloat(remainPercent) * parseFloat(receivedAmount)) / 100;
			if(document.getElementById('strParticularsAmount_'+invcID+'_'+baseCnt)) {
				document.getElementById('strParticularsAmount_'+invcID+'_'+baseCnt).value = parseFloat(partiAmt).toFixed(2);
				document.getElementById('strHideParticularsPercent_'+invcID+'_'+baseCnt).value = parseFloat(remainPercent).toFixed(2);
			}
		}
		
		
		function checkDeductionFields(val, invcID, billCurr) {
			<% Iterator<String> itCDF = hmInvoiceDetails.keySet().iterator(); %>
			 //tdsAmountSpan tdsAmountPercentSpan otherDeductionSpan otherDeductionPercentSpan
			 if(val == '1') {
				 <% while(itCDF.hasNext()) {
						String invoiceIdJS = itCDF.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmProTaxHeadDataJS = hmInvoiceProTaxHeads.get(invoiceIdJS);
				if(hmProTaxHeadDataJS != null && !hmProTaxHeadDataJS.isEmpty()) {
					Iterator<String> it = hmProTaxHeadDataJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmProTaxHeadDataJS.get(taxHeadId);
				%>
					var taxPercent = <%=uF.showData(innerList1.get(2), "0") %>;
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					document.getElementById('taxPercentSpan_'+invcID+'_'+cnt).style.display = 'table-row';
					if(document.getElementById('taxPercent_'+invcID+'_'+cnt))
						document.getElementById('taxPercent_'+invcID+'_'+cnt).value = taxPercent;
					if(document.getElementById('taxAmount_'+invcID+'_'+cnt))
						document.getElementById('taxAmount_'+invcID+'_'+cnt).value = "0";
					document.getElementById('taxAmountSpan_'+invcID+'_'+cnt).style.display = 'none';
				<% i++; } } %>
				}
				<% } %>
				//alert("vxcvxcv nnnnnnnn");	
	
				if(document.getElementById('previousYearTdsPercentSpan_'+invcID)) {
				 	document.getElementById('previousYearTdsPercentSpan_'+invcID).style.display = 'block';
				 	document.getElementById('previousYearTdsPercent_'+invcID).value = "0";
				 	document.getElementById('previousYearTdsAmount_'+invcID).value = "0";
				 }
				 if(document.getElementById('previousYearTdsAmountSpan_'+invcID)) {
					 document.getElementById('previousYearTdsPercent_'+invcID).value = "0";
					 document.getElementById('previousYearTdsAmount_'+invcID).value = "0";
					 document.getElementById('previousYearTdsAmountSpan_'+invcID).style.display = 'none';
				 }
				 calculateBalanceAmount(invcID, billCurr);
			 } else {
				 <% itCDF = hmInvoiceDetails.keySet().iterator();
				 	while(itCDF.hasNext()) {
						String invoiceIdJS = itCDF.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmProTaxHeadDataJS = hmInvoiceProTaxHeads.get(invoiceIdJS);
				if(hmProTaxHeadDataJS != null && !hmProTaxHeadDataJS.isEmpty()) {
					Iterator<String> it = hmProTaxHeadDataJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmProTaxHeadDataJS.get(taxHeadId);
				%>
					var taxPercent = <%=uF.showData(innerList1.get(2), "0") %>;
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					document.getElementById('taxAmountSpan_'+invcID+'_'+cnt).style.display = 'table-row';
					if(document.getElementById('taxPercent_'+invcID+'_'+cnt))
						document.getElementById('taxPercent_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('taxAmount_'+invcID+'_'+cnt))
						document.getElementById('taxAmount_'+invcID+'_'+cnt).value = "0";
					document.getElementById('taxPercentSpan_'+invcID+'_'+cnt).style.display = 'none';
				<% i++; } } %>
				}
				<% } %>
				 if(document.getElementById('previousYearTdsPercentSpan_'+invcID)) {
					document.getElementById('previousYearTdsPercent_'+invcID).value = "0";
					document.getElementById('previousYearTdsAmount_'+invcID).value = "0";
				 	document.getElementById('previousYearTdsPercentSpan_'+invcID).style.display = 'none';
				 }
				 if(document.getElementById('previousYearTdsAmountSpan_'+invcID)) {
				 	document.getElementById('previousYearTdsAmountSpan_'+invcID).style.display = 'block';
				 	document.getElementById('previousYearTdsPercent_'+invcID).value = "0";
					document.getElementById('previousYearTdsAmount_'+invcID).value = "0";
				 }
				 calculateBalanceAmount(invcID, billCurr);
	
			 }
		}
		
		function checkPrevYearTDS(obj, invcID, billCurr) {
	
			if(obj.checked) {
				var deductType = "";
				if(document.getElementById('deductionType_'+invcID))
					deductType = document.getElementById('deductionType_'+invcID).value;
				
				//alert("invcID ==>>> "  + invcID);
				document.getElementById('taxDpy_'+invcID).style.display = 'table-row';
				
				//var deductType = document.frm.deductionType.value;
				if(deductType == '1') {
					document.getElementById('previousYearTdsPercentSpan_'+invcID).style.display = 'block';
					document.getElementById('previousYearTdsPercent_'+invcID).value = '0.0';
					document.getElementById('previousYearTdsAmountSpan_'+invcID).style.display = 'none';
				} else {
					document.getElementById('previousYearTdsAmountSpan_'+invcID).style.display = 'block';
					document.getElementById('previousYearTdsAmount_'+invcID).value = '0.0';
					document.getElementById('previousYearTdsPercentSpan_'+invcID).style.display = 'none';
				}
				calculateBalanceAmount(invcID, billCurr);
			} else {
				document.getElementById('previousYearTdsPercent_'+invcID).value = '0.0';
				document.getElementById('previousYearTdsAmount_'+invcID).value = '0.0';
				calculateBalanceAmount(invcID, billCurr);
				document.getElementById('taxDpy_'+invcID).style.display = 'none';
			}
		}
		
		
		function checkTDS(obj, invcID, billCurr) {
			<% Iterator<String> itCTDS = hmInvoiceDetails.keySet().iterator(); %>
			
			//alert("invcID ===>> " + invcID);
			if(obj.checked) {
				document.getElementById('deductTaxTable_'+invcID).style.display = 'block';
				<% while(itCTDS.hasNext()) {
					String invoiceIdJS = itCTDS.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmProTaxHeadDataJS = hmInvoiceProTaxHeads.get(invoiceIdJS);
				if(hmProTaxHeadDataJS != null && !hmProTaxHeadDataJS.isEmpty()) {
					Iterator<String> it = hmProTaxHeadDataJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmProTaxHeadDataJS.get(taxHeadId);
				%>
				var taxPercent = <%=uF.showData(innerList1.get(2), "0") %>;
				var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
				//document.getElementById('taxTR'+cnt).style.display = 'table-row';
				if(document.getElementById('taxAmountSpan_'+invcID+'_'+cnt)) {
					document.getElementById('taxAmountSpan_'+invcID+'_'+cnt).style.display = 'none';
				}
				if(document.getElementById('taxPercent_'+invcID+'_'+cnt)) {
					document.getElementById('taxPercent_'+invcID+'_'+cnt).value = taxPercent;
				}
				if(document.getElementById('taxAmount_'+invcID+'_'+cnt)) {
					document.getElementById('taxAmount_'+invcID+'_'+cnt).value = "0";
				}
				<% i++; } } %>
				}
				<% } %>
				if(document.getElementById('previousYearTdsAmount_'+invcID)) {
					document.getElementById('previousYearTdsAmount_'+invcID).value = "0";
				}
				
				document.getElementById('taxD1_'+invcID).style.display = 'table-row';
				
				document.getElementById('deductionType_'+invcID).selectedIndex = '0';
				
				checkDeductionFields('1', invcID, billCurr);
				calculateBalanceAmount(invcID, billCurr);
				
			} else {
				document.getElementById('deductTaxTable_'+invcID).style.display = 'none';
				<% itCTDS = hmInvoiceDetails.keySet().iterator();
					while(itCTDS.hasNext()) {
					String invoiceIdJS = itCTDS.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmProTaxHeadDataJS = hmInvoiceProTaxHeads.get(invoiceIdJS);
				if(hmProTaxHeadDataJS != null && !hmProTaxHeadDataJS.isEmpty()) {
					Iterator<String> it = hmProTaxHeadDataJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmProTaxHeadDataJS.get(taxHeadId);
				%>
				var taxPercent = <%=uF.showData(innerList1.get(2), "0") %>;
				var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
				//document.getElementById('taxTR'+cnt).style.display = 'none';
				if(document.getElementById('taxPercent_'+invcID+'_'+cnt)) {
					document.getElementById('taxPercent_'+invcID+'_'+cnt).value = '0.0';
				}
				if(document.getElementById('taxAmount_'+invcID+'_'+cnt)) {
					document.getElementById('taxAmount_'+invcID+'_'+cnt).value = "0";
				}
				<% i++; } } %>
				}
				<% } %>
				if(document.getElementById('previousYearTdsAmount_'+invcID)) {
					document.getElementById('previousYearTdsAmount_'+invcID).value = "0";
				}
				document.getElementById('taxD1_'+invcID).style.display = 'none';
				
				calculateBalanceAmount(invcID, billCurr);
				
			}
		}
		
		
		
		function calcuWriteOffOPE(invcID, billCurr) {
			
			<% Iterator<String> itCWOOPE = hmInvoiceDetails.keySet().iterator(); %>
			
			var amountReceived = parseFloat(document.getElementById('amountReceived_'+invcID).value).toFixed(2);
			
			var writeOffType = document.getElementById('writeOffType_'+invcID).value;
			if(writeOffType == '1') {
				
				var writeOffopePer = 0;
				var writeOffopeAmt = 0;
				var writeOffsTaxAmt = 0;
				var writeOffeduCessAmt = 0;
				var writeOffstdCessAmt = 0;
				
				var invoiceAmount = parseFloat(document.getElementById('invoiceAmount_'+invcID).value).toFixed(2);
				var dueAmount = parseFloat(document.getElementById('amountDue_'+invcID).value).toFixed(2);
				var writeOffPercent = parseFloat(document.getElementById('writeOffPercent_'+invcID).value).toFixed(2);
				
				var balanceAmount = parseFloat(dueAmount) - (parseFloat(amountReceived)); // + parseFloat(allTaxAmount)  parseFloat(hideTdsAmount) + parseFloat(hidePreviousYearTdsAmount) + parseFloat(hideOtherDeduction)
				
				if(writeOffPercent == 'NaN' || writeOffPercent == '') {
					writeOffPercent = 0;
				}
				
				var writeOffAmt = (parseFloat(balanceAmount) * parseFloat(writeOffPercent)) / 100;
				var writeOffAmtPercent = (parseFloat(writeOffAmt) * 100) / parseFloat(invoiceAmount);
				
				var totAllPercent = 0;
				var baseCnt = '0';
				
				<% while(itCWOOPE.hasNext()) {
					String invoiceIdJS = itCWOOPE.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<% 
				Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
				if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) { 
					Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					<%-- var OPEAmount = <%=uF.showData(innerList1.get(3), "0") %>; --%>
					var writeOffOperationExpPercent = document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt).value;
					if(writeOffOperationExpPercent == 'NaN' || writeOffOperationExpPercent == '') {
						writeOffOperationExpPercent = 0;
					}
					writeOffopeAmt = (parseFloat(writeOffOperationExpPercent) * parseFloat(writeOffAmt)) / 100;
					totAllPercent = parseFloat(totAllPercent) + parseFloat(writeOffOperationExpPercent);
					if(document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffOperationExpPercent).toFixed(2);
						document.getElementById('hidewoOPEAmount_'+invcID+'_'+cnt).value = writeOffopeAmt.toFixed(2);
						document.getElementById('hidewoOPEPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffOperationExpPercent).toFixed(2);
						document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt).innerHTML = ' (equal to '+writeOffopeAmt.toFixed(2)+' '+billCurr+')';
					}
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWOOPE = hmInvoiceDetails.keySet().iterator();
					while(itCWOOPE.hasNext()) {
					String invoiceIdJS = itCWOOPE.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<% 
				Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
				if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var partiAmount = <%=uF.showData(innerList1.get(3), "0") %>;
					var writeOffPartiAmt = (parseFloat(partiAmount) * parseFloat(writeOffAmtPercent)) / 100;
					var writeOffPartiPer =0;
					if(parseFloat(writeOffAmt) > 0) {
						writeOffPartiPer = (parseFloat(writeOffPartiAmt) * 100) / parseFloat(writeOffAmt);
					}
					<% if(i>0) { %>
						totAllPercent = parseFloat(totAllPercent) + parseFloat(writeOffPartiPer);
					<% } else { %>
						baseCnt = <%=uF.showData(innerList1.get(0), "0") %>;
					<% } %>
					if(document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
						document.getElementById('hidewoPartiAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
						document.getElementById('hidewoPartiPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
						document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt).innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+' '+billCurr+')';
					}
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWOOPE = hmInvoiceDetails.keySet().iterator();
					while(itCWOOPE.hasNext()) {
					String invoiceIdJS = itCWOOPE.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<% 
				Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
				if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var taxPercent = <%=uF.showData(innerList1.get(5), "0") %>;
					totAllPercent = parseFloat(totAllPercent) + parseFloat(taxPercent);
					var writeOffTaxAmt = (parseFloat(writeOffAmt) * parseFloat(taxPercent)) / 100;
					if(parseFloat(writeOffAmt) == 0) {
						taxPercent = 0;
					}
					if(document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
						document.getElementById('hidewoTaxAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
						document.getElementById('hidewoTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
						document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt).innerHTML = ' (equal to '+writeOffTaxAmt.toFixed(2)+' '+billCurr+')';
					}
				<% i++; } } %>
				}
				<% } %>
				
				var remainWOPercent = 100 - parseFloat(totAllPercent);
				//alert("remainWOPercent ===>> " + remainWOPercent);
				if(parseFloat(writeOffAmt) == 0) {
					remainWOPercent = 0;
				}
				var writeOffPartiAmt = (parseFloat(remainWOPercent) * parseFloat(writeOffAmt)) / 100;
				if(document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+baseCnt)) {
					document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+baseCnt).value = parseFloat(remainWOPercent).toFixed(2);
					document.getElementById('hidewoPartiAmount_'+invcID+'_'+baseCnt).value = writeOffPartiAmt.toFixed(2);
					document.getElementById('hidewoPartiPercent_'+invcID+'_'+baseCnt).value = parseFloat(remainWOPercent).toFixed(2);
					document.getElementById('w_o_parti_amt_'+invcID+'_'+baseCnt).innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+' '+billCurr+')';
				}
				
			} else {
	
				var writeOffopePer = 0;
				var writeOffopeAmt = 0;
				var writeOffsTaxAmt = 0;
				var writeOffeduCessAmt = 0;
				var writeOffstdCessAmt = 0;
				
				var invoiceAmount = parseFloat(document.getElementById('invoiceAmount_'+invcID).value).toFixed(2);
				var dueAmount = parseFloat(document.getElementById('amountDue_'+invcID).value).toFixed(2);
				var writeOffAmount = document.getElementById('writeOffAmount_'+invcID).value;
				
				var balanceAmount = parseFloat(dueAmount) - (parseFloat(amountReceived)); // + parseFloat(allTaxAmount)  parseFloat(hideTdsAmount) + parseFloat(hidePreviousYearTdsAmount) + parseFloat(hideOtherDeduction)
				
				if(parseFloat(writeOffAmount) > parseFloat(balanceAmount)) {
					alert("Entered amount more than balance amount.");
					document.getElementById('writeOffAmount_'+invcID).value = "0";
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
				var baseCnt = '0';
				<% itCWOOPE = hmInvoiceDetails.keySet().iterator();
					while(itCWOOPE.hasNext()) {
					String invoiceIdJS = itCWOOPE.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<% 
				Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
				if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					<%-- var OPEAmount = <%=uF.showData(innerList1.get(3), "0") %>; --%>
					var writeOffOperationExpAmount = document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt).value;
					if(writeOffOperationExpAmount == 'NaN' || writeOffOperationExpAmount == '') {
						writeOffOperationExpAmount = 0;
					}
					if(parseFloat(writeOffAmount) > 0) {
						writeOffopePer = (parseFloat(writeOffOperationExpAmount) * 100) / parseFloat(writeOffAmount);
					}
					allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffOperationExpAmount);
	
					if(document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffOperationExpAmount).toFixed(2);
						document.getElementById('hidewoOPEAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffOperationExpAmount).toFixed(2);
						document.getElementById('hidewoOPEPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffopePer).toFixed(2);
						document.getElementById('w_o_ope_per_'+invcID+'_'+cnt).innerHTML = ' (@ '+parseFloat(writeOffopePer).toFixed(2)+'%)';
					}
				<% i++; } } %>
			
				}
				<% } %>
				
				<% itCWOOPE = hmInvoiceDetails.keySet().iterator();
					while(itCWOOPE.hasNext()) {
					String invoiceIdJS = itCWOOPE.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<% 
				Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
				if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var partiAmount = <%=uF.showData(innerList1.get(3), "0") %>;
					var writeOffPartiAmt = (parseFloat(partiAmount) * parseFloat(writeOffAmtPercent)) / 100;
					<% if(i>0) { %>
						allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffPartiAmt);
					<% } else { %>
						baseCnt = <%=uF.showData(innerList1.get(0), "0") %>;
					<% } %>
					var writeOffPartiPer =0;
					if(parseFloat(writeOffAmount) > 0) {
						writeOffPartiPer = (parseFloat(writeOffPartiAmt) * 100) / parseFloat(writeOffAmount);
					}
					if(document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt)) {  
						document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
						document.getElementById('hidewoPartiAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
						document.getElementById('hidewoPartiPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
						document.getElementById('w_o_parti_per_'+invcID+'_'+cnt).innerHTML = ' (@ '+parseFloat(writeOffPartiPer).toFixed(2)+'%)';
					}
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWOOPE = hmInvoiceDetails.keySet().iterator();
					while(itCWOOPE.hasNext()) {
					String invoiceIdJS = itCWOOPE.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<% 
				Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
				if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var taxPercent = <%=uF.showData(innerList1.get(5), "0") %>;
					if(parseFloat(writeOffAmt) == 0) {
						taxPercent = 0;
					}
					var writeOffTaxAmt = (parseFloat(writeOffAmount) * parseFloat(taxPercent)) / 100;
					allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffTaxAmt);
					
					if(document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
						document.getElementById('hidewoTaxAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
						document.getElementById('hidewoTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
						document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt).innerHTML = ' (@ '+parseFloat(taxPercent).toFixed(2)+'%)';
					}
				<% i++; } } %>
				}
				<% } %>
				
				var remainWOAmount = parseFloat(writeOffAmount) - parseFloat(allWOAmount);
				if(parseFloat(writeOffAmt) == 0) {
					remainWOPercent = 0;
				}
				var writeOffPartiPer = 0;
				if(parseFloat(writeOffAmount) > 0) {
					writeOffPartiPer = (parseFloat(remainWOAmount) * 100 / parseFloat(writeOffAmount));
				}
				
				if(document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+baseCnt)) {
					document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+baseCnt).value = parseFloat(remainWOAmount).toFixed(2);
					document.getElementById('hidewoPartiAmount_'+invcID+'_'+baseCnt).value = parseFloat(remainWOAmount).toFixed(2);
					document.getElementById('hidewoPartiPercent_'+invcID+'_'+baseCnt).value = parseFloat(writeOffPartiPer).toFixed(2);
					document.getElementById('w_o_parti_per_'+invcID+'_'+baseCnt).innerHTML = ' (@ '+parseFloat(writeOffPartiPer).toFixed(2)+'%)';
				}
				
			}
		}
		
		
		
		
		function checkWriteoffAmount(invcID, billCurr) {
			
			<% Iterator<String> itCWOA = hmInvoiceDetails.keySet().iterator(); %>
			
			var amountReceived = parseFloat(document.getElementById('amountReceived_'+invcID).value).toFixed(2);
			if(amountReceived == 'NaN' || amountReceived == '') {
				amountReceived = 0;
			}
			var allTaxAmount = 0;
			
			<% while(itCWOA.hasNext()) {
					String invoiceIdJS = itCWOA.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmProTaxHeadDataJS = hmInvoiceProTaxHeads.get(invoiceIdJS);
				if(hmProTaxHeadDataJS != null && !hmProTaxHeadDataJS.isEmpty()) {
				Iterator<String> it = hmProTaxHeadDataJS.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList1 = hmProTaxHeadDataJS.get(taxHeadId);
			%>		
				var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
				var taxAmount = 0;
				if(document.getElementById('hideTaxAmount_'+invcID+'_'+cnt))
					taxAmount = document.getElementById('hideTaxAmount_'+invcID+'_'+cnt).value;
				allTaxAmount = parseFloat(allTaxAmount) + parseFloat(taxAmount);
			<% i++; } } %>
			}
			<% } %>
			
			var pyTaxAmount = 0;
			if(document.getElementById('hidePreviousYearTdsAmount_'+invcID))
				pyTaxAmount = document.getElementById('hidePreviousYearTdsAmount_'+invcID).value;
			allTaxAmount = parseFloat(allTaxAmount) + parseFloat(pyTaxAmount);
			
			//alert("allTaxAmount ===>> " + allTaxAmount);
			
			var writeOffType = document.getElementById('writeOffType_'+invcID).value;
			if(writeOffType == '1') {
				
				var writeOffopePer = 0;
				var writeOffprofFeesPercent = 0;
				var writeOffopeAmt = 0;
				var writeOffsTaxAmt = 0;
				var writeOffeduCessAmt = 0;
				var writeOffstdCessAmt = 0;
				
				var invoiceAmount = parseFloat(document.getElementById('invoiceAmount_'+invcID).value).toFixed(2);
				var dueAmount = parseFloat(document.getElementById('amountDue_'+invcID).value).toFixed(2);
				var writeOffPercent = parseFloat(document.getElementById('writeOffPercent_'+invcID).value).toFixed(2);
				
				if(writeOffPercent > 100) {
					alert("Entered Percentage more than 100.");
					document.getElementById('writeOffPercent_'+invcID).value = "0";
					writeOffPercent = 0;
				}
				var balanceAmount = parseFloat(dueAmount) - (parseFloat(amountReceived) + parseFloat(allTaxAmount)); //parseFloat(hideTdsAmount) + parseFloat(hidePreviousYearTdsAmount) + parseFloat(hideOtherDeduction)
				
				if(writeOffPercent == 'NaN' || writeOffPercent == '') {
					writeOffPercent = 0;
				}
				
				if(parseFloat(balanceAmount)== 0) {
					alert("Balance amount is zero.");
					document.getElementById('writeOffPercent_'+invcID).value = "0";
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
				var baseCnt = '0';
				
				<% itCWOA = hmInvoiceDetails.keySet().iterator();
					while(itCWOA.hasNext()) {
					String invoiceIdJS = itCWOA.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
				if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var OPEAmount = <%=uF.showData(innerList1.get(3), "0") %>;
					writeOffopeAmt = (parseFloat(OPEAmount) * parseFloat(writeOffAmtPercent)) / 100;
					if(parseFloat(writeOffAmt) > 0) {
						writeOffopePer = (parseFloat(writeOffopeAmt) * 100) / parseFloat(writeOffAmt);
					}
					allWOPercent = parseFloat(allWOPercent) + parseFloat(writeOffopePer);
					if(document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffopePer).toFixed(2);
						document.getElementById('hidewoOPEAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffopeAmt).toFixed(2);
						document.getElementById('hidewoOPEPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffopePer).toFixed(2);
						document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt).innerHTML = ' (equal to '+writeOffopeAmt.toFixed(2)+' '+billCurr+')';
					}
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWOA = hmInvoiceDetails.keySet().iterator();
					while(itCWOA.hasNext()) {
					String invoiceIdJS = itCWOA.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
				if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var partiAmount = <%=uF.showData(innerList1.get(3), "0") %>;
					var writeOffPartiAmt = (parseFloat(partiAmount) * parseFloat(writeOffAmtPercent)) / 100;
					var writeOffPartiPer =0;
					if(parseFloat(writeOffAmt) > 0) {
						writeOffPartiPer = (parseFloat(writeOffPartiAmt) * 100) / parseFloat(writeOffAmt);
					}
					<% if(i>0) { %>
					allWOPercent = parseFloat(allWOPercent) + parseFloat(writeOffPartiPer);
					<% } else { %>
						baseCnt = <%=uF.showData(innerList1.get(0), "0") %>;
					<% } %>
					if(document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
						document.getElementById('hidewoPartiAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
						document.getElementById('hidewoPartiPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
						document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt).innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+' '+billCurr+')';
					}
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWOA = hmInvoiceDetails.keySet().iterator();
					while(itCWOA.hasNext()) {
					String invoiceIdJS = itCWOA.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
				if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var taxPercent = <%=uF.showData(innerList1.get(5), "0") %>;
					var writeOffTaxAmt = (parseFloat(writeOffAmt) * parseFloat(taxPercent)) / 100;
					allWOPercent = parseFloat(allWOPercent) + parseFloat(taxPercent);
					if(parseFloat(writeOffAmt) == 0) {
						taxPercent = 0;
					}
					if(document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
						document.getElementById('hidewoTaxAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
						document.getElementById('hidewoTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
						document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt).innerHTML = ' (equal to '+writeOffTaxAmt.toFixed(2)+' '+billCurr+')';
					}
				<% i++; } } %>
				}
				<% } %>
				
				var remainWOPercent = 100 - parseFloat(allWOPercent);
				if(parseFloat(writeOffAmt) == 0) {
					remainWOPercent = 0;
				}
				
				var writeOffPartiAmt = (parseFloat(remainWOPercent) * parseFloat(writeOffAmt)) / 100;
				if(document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+baseCnt)) {
					document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+baseCnt).value = parseFloat(remainWOPercent).toFixed(2);
					document.getElementById('hidewoPartiAmount_'+invcID+'_'+baseCnt).value = writeOffPartiAmt.toFixed(2);
					document.getElementById('hidewoPartiPercent_'+invcID+'_'+baseCnt).value = parseFloat(remainWOPercent).toFixed(2);
					document.getElementById('w_o_parti_amt_'+invcID+'_'+baseCnt).innerHTML = ' (equal to '+writeOffPartiAmt.toFixed(2)+' '+billCurr+')';
				}
				
				document.getElementById('hidewriteOffAmount_'+invcID).value = parseFloat(writeOffAmt).toFixed(2);
				document.getElementById('hidewriteOffPercent_'+invcID).value = writeOffPercent;
				
				//alert("balanceAmount ===>> " + balanceAmount + "  writeOffAmt ====>> " + writeOffAmt);
				balanceAmount = parseFloat(balanceAmount) - parseFloat(writeOffAmt);
				
				//alert("balanceAmount ===>> " + balanceAmount);
				
				if(balanceAmount>=0) {
					document.getElementById('balanceAmount_'+invcID).value = balanceAmount.toFixed(2);	
				} else {
					document.getElementById('balanceAmount_'+invcID).value = '0.0';
				}
				
			} else {
	
				var writeOffopePer = 0;
				var writeOffopeAmt = 0;
				var writeOffsTaxAmt = 0;
				var writeOffeduCessAmt = 0;
				var writeOffstdCessAmt = 0;
				
				var invoiceAmount = parseFloat(document.getElementById('invoiceAmount_'+invcID).value).toFixed(2);
				var dueAmount = parseFloat(document.getElementById('amountDue_'+invcID).value).toFixed(2);
				var writeOffAmount = document.getElementById('writeOffAmount_'+invcID).value;
				
				//alert("allTaxAmount else ===>> " + allTaxAmount);
				var balanceAmount = parseFloat(dueAmount) - (parseFloat(amountReceived) + parseFloat(allTaxAmount)); //parseFloat(hideTdsAmount) + parseFloat(hidePreviousYearTdsAmount) + parseFloat(hideOtherDeduction)
				
				if(parseFloat(writeOffAmount) > parseFloat(balanceAmount)) {
					alert("Entered amount more than balance amount.");
					document.getElementById('writeOffAmount_'+invcID).value = "0";
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
				var baseCnt = '0';
				
				<% itCWOA = hmInvoiceDetails.keySet().iterator();
					while(itCWOA.hasNext()) {
					String invoiceIdJS = itCWOA.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
				if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var OPEAmount = <%=uF.showData(innerList1.get(3), "0") %>;
					writeOffopeAmt = (parseFloat(OPEAmount) * parseFloat(writeOffAmtPercent)) / 100;
					allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffopeAmt);
					if(parseFloat(writeOffAmount) > 0) {
						writeOffopePer = (parseFloat(writeOffopeAmt) * 100) / parseFloat(writeOffAmount);
					}
					if(document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffopeAmt).toFixed(2);
						document.getElementById('hidewoOPEAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffopeAmt).toFixed(2);
						document.getElementById('hidewoOPEPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffopePer).toFixed(2);
						document.getElementById('w_o_ope_per_'+invcID+'_'+cnt).innerHTML = ' (@ '+parseFloat(writeOffopePer).toFixed(2)+'%)';
					}
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWOA = hmInvoiceDetails.keySet().iterator();
					while(itCWOA.hasNext()) {
					String invoiceIdJS = itCWOA.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
				if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var partiAmount = <%=uF.showData(innerList1.get(3), "0") %>;
					var writeOffPartiAmt = (parseFloat(partiAmount) * parseFloat(writeOffAmtPercent)) / 100;
					<% if(i>0) { %>
						allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffPartiAmt);
					<% } else { %>
						baseCnt = <%=uF.showData(innerList1.get(0), "0") %>;
					<% } %>
					var writeOffPartiPer =0;
					if(parseFloat(writeOffAmount) > 0) {
						writeOffPartiPer = (parseFloat(writeOffPartiAmt) * 100) / parseFloat(writeOffAmount);
					}
					if(document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt)) {  
						document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
						document.getElementById('hidewoPartiAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiAmt).toFixed(2);
						document.getElementById('hidewoPartiPercent_'+invcID+'_'+cnt).value = parseFloat(writeOffPartiPer).toFixed(2);
						document.getElementById('w_o_parti_per_'+invcID+'_'+cnt).innerHTML = ' (@ '+parseFloat(writeOffPartiPer).toFixed(2)+'%)';
					}
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWOA = hmInvoiceDetails.keySet().iterator();
					while(itCWOA.hasNext()) {
					String invoiceIdJS = itCWOA.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
				if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					var taxPercent = <%=uF.showData(innerList1.get(5), "0") %>;
					if(parseFloat(writeOffAmount) == 0) {
						taxPercent = 0;
					}
					var writeOffTaxAmt = (parseFloat(writeOffAmount) * parseFloat(taxPercent)) / 100;
					if(writeOffTaxAmt == 'NaN' || writeOffTaxAmt == '') {
						writeOffTaxAmt = 0;
						taxPercent = 0;
					}
					allWOAmount = parseFloat(allWOAmount) + parseFloat(writeOffTaxAmt);
					
					if(document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt)) {
						document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
						document.getElementById('hidewoTaxAmount_'+invcID+'_'+cnt).value = parseFloat(writeOffTaxAmt).toFixed(2);
						document.getElementById('hidewoTaxPercent_'+invcID+'_'+cnt).value = parseFloat(taxPercent).toFixed(2);
						document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt).innerHTML = ' (@ '+parseFloat(taxPercent).toFixed(2)+'%)';
					}
				<% i++; } } %>
				}
				<% } %>
				
				var remainWOAmount = parseFloat(writeOffAmount) - parseFloat(allWOAmount);
				if(parseFloat(writeOffAmount) == 0) {
					remainWOPercent = 0;
				}
				
				var writeOffPartiPer = 0;
				if(parseFloat(writeOffAmount) > 0) {
					writeOffPartiPer = (parseFloat(remainWOAmount) * 100 / parseFloat(writeOffAmount));
				}
				
				if(document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+baseCnt)) {
					document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+baseCnt).value = parseFloat(remainWOAmount).toFixed(2);
					document.getElementById('hidewoPartiAmount_'+invcID+'_'+baseCnt).value = parseFloat(remainWOAmount).toFixed(2);
					document.getElementById('hidewoPartiPercent_'+invcID+'_'+baseCnt).value = parseFloat(writeOffPartiPer).toFixed(2);
					document.getElementById('w_o_parti_per_'+invcID+'_'+baseCnt).innerHTML = ' (@ '+parseFloat(writeOffPartiPer).toFixed(2)+'%)';
				}
				
				document.getElementById('hidewriteOffAmount_'+invcID).value = parseFloat(writeOffAmount).toFixed(2);
				document.getElementById('hidewriteOffPercent_'+invcID).value = writeOffAmtPer;
				
				balanceAmount = parseFloat(balanceAmount) - parseFloat(writeOffAmount);
				
				if(balanceAmount>=0) {
					document.getElementById('balanceAmount_'+invcID).value = balanceAmount.toFixed(2);	
				} else {
					document.getElementById('balanceAmount_'+invcID).value = '0.0';
				}
				
				/* if(balanceAmount >= 0) {
					document.frm.balanceAmount.value = balanceAmount.toFixed(2);	
				} else {
					document.frm.balanceAmount.value = '0.0';
				} */
			}
		}
		
		
		
		function checkWriteOffFields(val, invcID, billCurr) {
			<% Iterator<String> itCWOF = hmInvoiceDetails.keySet().iterator(); %>
			
			 //    writeOffOperationExp  writeOffServiceTax  writeOffCess2Percent 
			 if(val == '1') {
				 document.getElementById('woPercentTR_'+invcID).style.display = 'table-row';
				 
					if(document.getElementById('writeOffPercent_'+invcID))
						document.getElementById('writeOffPercent_'+invcID).value = "0";
					if(document.getElementById('writeOffAmount_'+invcID))
						document.getElementById('writeOffAmount_'+invcID).value = "0";
					if(document.getElementById('hidewriteOffAmount_'+invcID))
						document.getElementById('hidewriteOffAmount_'+invcID).value = "0";
					if(document.getElementById('hidewriteOffPercent_'+invcID))
						document.getElementById('hidewriteOffPercent_'+invcID).value = "0";
					
					<% while(itCWOF.hasNext()) {
						String invoiceIdJS = itCWOF.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					if(invcID == invoiceID) {
					<%
					Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
					if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String billHeadId = it.next();	
						List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						document.getElementById('writeOffOPEPercentSpan_'+invcID+'_'+cnt).style.display = 'block';
						if(document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('w_o_ope_per_'+invcID+'_'+cnt))
							document.getElementById('w_o_ope_per_'+invcID+'_'+cnt).innerHTML = '';
						if(document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt))
							document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt).innerHTML = '';
						document.getElementById('writeOffOPEAmountSpan_'+invcID+'_'+cnt).style.display = 'none';
					<% i++; } } %>
					}
					<% } %>
					
					<% itCWOF = hmInvoiceDetails.keySet().iterator();
						while(itCWOF.hasNext()) {
						String invoiceIdJS = itCWOF.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					if(invcID == invoiceID) {
					<%
					Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
					if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String billHeadId = it.next();	
						List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						document.getElementById('writeOffParticularsPercentSpan_'+invcID+'_'+cnt).style.display = 'block';
						if(document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('w_o_parti_per_'+invcID+'_'+cnt))
							document.getElementById('w_o_parti_per_'+invcID+'_'+cnt).innerHTML = '';
						if(document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt))
							document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt).innerHTML = '';
						document.getElementById('writeOffParticularsAmountSpan_'+invcID+'_'+cnt).style.display = 'none';
					<% i++; } } %>
					}
					<% } %>
					
					<% itCWOF = hmInvoiceDetails.keySet().iterator();
						while(itCWOF.hasNext()) {
						String invoiceIdJS = itCWOF.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					if(invcID == invoiceID) {
					<%
					Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
					if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String taxHeadId = it.next();
						List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						document.getElementById('writeOffTaxPercentSpan_'+invcID+'_'+cnt).style.display = 'block';
						if(document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt))
							document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt).innerHTML = '';
						if(document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt))
							document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt).innerHTML = '';
						document.getElementById('writeOffTaxAmountSpan_'+invcID+'_'+cnt).style.display = 'none';
					<% i++; } } %>
					}
					<% } %>
					
					calculateBalanceAmount(invcID, billCurr);
					
					document.getElementById('woAmountTR_'+invcID).style.display = 'none';
				 
			 } else {
	
				 document.getElementById('woAmountTR_'+invcID).style.display = 'table-row';
				 
				 	if(document.getElementById('writeOffPercent_'+invcID))
						document.getElementById('writeOffPercent_'+invcID).value = "0";
					if(document.getElementById('writeOffAmount_'+invcID))
						document.getElementById('writeOffAmount_'+invcID).value = "0";
					if(document.getElementById('hidewriteOffAmount_'+invcID))
						document.getElementById('hidewriteOffAmount_'+invcID).value = "0";
					if(document.getElementById('hidewriteOffPercent_'+invcID))
						document.getElementById('hidewriteOffPercent_'+invcID).value = "0";
					
				 	<% itCWOF = hmInvoiceDetails.keySet().iterator();
						while(itCWOF.hasNext()) {
						String invoiceIdJS = itCWOF.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					if(invcID == invoiceID) {
					<%
					Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
					if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String billHeadId = it.next();	
						List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						document.getElementById('writeOffOPEAmountSpan_'+invcID+'_'+cnt).style.display = 'block';
						if(document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('w_o_ope_per_'+invcID+'_'+cnt))
							document.getElementById('w_o_ope_per_'+invcID+'_'+cnt).innerHTML = '';
						if(document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt))
							document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt).innerHTML = '';
						
						document.getElementById('writeOffOPEPercentSpan_'+invcID+'_'+cnt).style.display = 'none';
					<% i++; } } %>
					}
					<% } %>
					
					<% itCWOF = hmInvoiceDetails.keySet().iterator();
						while(itCWOF.hasNext()) {
						String invoiceIdJS = itCWOF.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					if(invcID == invoiceID) {
					<%
					Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
					if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String billHeadId = it.next();	
						List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						document.getElementById('writeOffParticularsAmountSpan_'+invcID+'_'+cnt).style.display = 'block';
						if(document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('w_o_parti_per_'+invcID+'_'+cnt))
							document.getElementById('w_o_parti_per_'+invcID+'_'+cnt).innerHTML = '';
						if(document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt))
							document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt).innerHTML = '';
						document.getElementById('writeOffParticularsPercentSpan_'+invcID+'_'+cnt).style.display = 'none';
					<% i++; } } %>
					}
					<% } %>
					
					<% itCWOF = hmInvoiceDetails.keySet().iterator();
						while(itCWOF.hasNext()) {
						String invoiceIdJS = itCWOF.next();
					%>
					var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
					if(invcID == invoiceID) {
					<%
					Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
					if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
						Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String taxHeadId = it.next();
						List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
					%>
						var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
						document.getElementById('writeOffTaxAmountSpan_'+invcID+'_'+cnt).style.display = 'block';
						if(document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt))
							document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt).value = "0";
						if(document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt))
							document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt).innerHTML = '';
						if(document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt))
							document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt).innerHTML = '';
						document.getElementById('writeOffTaxPercentSpan_'+invcID+'_'+cnt).style.display = 'none';
					<% i++; } } %>
					}
					<% } %>
					document.getElementById('woPercentTR_'+invcID).style.display = 'none';
					
					calculateBalanceAmount(invcID, billCurr);
			 }
		}
		
		
		
		function checkWOff(obj, invcID, billCurr) {
			<% Iterator<String> itCWO = hmInvoiceDetails.keySet().iterator(); %>
			
			//alert("invcID ======>> " + invcID);
			if(obj.checked) {
				document.getElementById('writeOffTable_'+invcID).style.display = 'block';
				document.getElementById('woPercentTR_'+invcID).style.display = 'table-row';
				
				document.getElementById('writeOffType_'+invcID).selectedIndex = '0';
				if(document.getElementById('writeOffPercent_'+invcID))
					document.getElementById('writeOffPercent_'+invcID).value = "0";
				if(document.getElementById('writeOffAmount_'+invcID))
					document.getElementById('writeOffAmount_'+invcID).value = "0";
				if(document.getElementById('hidewriteOffAmount_'+invcID))
					document.getElementById('hidewriteOffAmount_'+invcID).value = "0";
				if(document.getElementById('hidewriteOffPercent_'+invcID))
					document.getElementById('hidewriteOffPercent_'+invcID).value = "0";
				
				checkWriteOffFields('1', billCurr);
				
				<% while(itCWO.hasNext()) {
					String invoiceIdJS = itCWO.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
				if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					//document.getElementById('woOPETR'+cnt).style.display = 'table-row';
					if(document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('w_o_ope_per_'+invcID+'_'+cnt))
						document.getElementById('w_o_ope_per_'+invcID+'_'+cnt).innerHTML = '';
					if(document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt))
						document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt).innerHTML = '';
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWO = hmInvoiceDetails.keySet().iterator();
					while(itCWO.hasNext()) {
					String invoiceIdJS = itCWO.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
				if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					//document.getElementById('woPartiTR'+cnt).style.display = 'table-row';
					if(document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('w_o_parti_per_'+invcID+'_'+cnt))
						document.getElementById('w_o_parti_per_'+invcID+'_'+cnt).innerHTML = '';
					if(document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt))
						document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt).innerHTML = '';
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWO = hmInvoiceDetails.keySet().iterator();
					while(itCWO.hasNext()) {
					String invoiceIdJS = itCWO.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
				if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					//alert("cnt ===>> " + cnt);
					//document.getElementById('woTaxTR'+cnt).style.display = 'table-row';
					//alert("woTaxTR ===>> "+cnt+ "   -- " + document.getElementById('woTaxTR'+cnt));
					if(document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt))
						document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt).innerHTML = '';
					if(document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt))
						document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt).innerHTML = '';
				<% i++; } } %>
				}
				<% } %>
				
			} else {
				
				document.getElementById('writeOffTable_'+invcID).style.display = 'none';
				document.getElementById('woPercentTR_'+invcID).style.display = 'none';
				document.getElementById('woAmountTR_'+invcID).style.display = 'none';
				
				document.getElementById('writeOffType_'+invcID).selectedIndex = '0';
				if(document.getElementById('writeOffPercent_'+invcID))
					document.getElementById('writeOffPercent_'+invcID).value = "0";
				if(document.getElementById('writeOffAmount_'+invcID))
					document.getElementById('writeOffAmount_'+invcID).value = "0";
				if(document.getElementById('hidewriteOffAmount_'+invcID))
					document.getElementById('hidewriteOffAmount_'+invcID).value = "0";
				if(document.getElementById('hidewriteOffPercent_'+invcID))
					document.getElementById('hidewriteOffPercent_'+invcID).value = "0";
				
				<% itCWO = hmInvoiceDetails.keySet().iterator();
					while(itCWO.hasNext()) {
					String invoiceIdJS = itCWO.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewiseOPEHeadsJS = hmInvoiceOPEHeads.get(invoiceIdJS);
				if(hmInvoicewiseOPEHeadsJS != null && !hmInvoicewiseOPEHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseOPEHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewiseOPEHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					//document.getElementById('woOPETR'+cnt).style.display = 'none';
					if(document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffOPEPercent_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffOPEAmount_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('w_o_ope_per_'+invcID+'_'+cnt))
						document.getElementById('w_o_ope_per_'+invcID+'_'+cnt).innerHTML = '';
					if(document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt))
						document.getElementById('w_o_ope_amt_'+invcID+'_'+cnt).innerHTML = '';
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWO = hmInvoiceDetails.keySet().iterator();
					while(itCWO.hasNext()) {
					String invoiceIdJS = itCWO.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewisePartiHeadsJS = hmInvoicePartiHeads.get(invoiceIdJS);
				if(hmInvoicewisePartiHeadsJS != null && !hmInvoicewisePartiHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewisePartiHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billHeadId = it.next();	
					List<String> innerList1 = hmInvoicewisePartiHeadsJS.get(billHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					//document.getElementById('woPartiTR'+cnt).style.display = 'none';
					if(document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffParticularsPercent_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffParticularsAmount_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('w_o_parti_per_'+invcID+'_'+cnt))
						document.getElementById('w_o_parti_per_'+invcID+'_'+cnt).innerHTML = '';
					if(document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt))
						document.getElementById('w_o_parti_amt_'+invcID+'_'+cnt).innerHTML = '';
				<% i++; } } %>
				}
				<% } %>
				
				<% itCWO = hmInvoiceDetails.keySet().iterator();
					while(itCWO.hasNext()) {
					String invoiceIdJS = itCWO.next();
				%>
				var invoiceID = '<%=uF.showData(invoiceIdJS, "0") %>';
				if(invcID == invoiceID) {
				<%
				Map<String, List<String>> hmInvoicewiseTaxHeadsJS = hmInvoiceTaxHeads.get(invoiceIdJS);
				if(hmInvoicewiseTaxHeadsJS != null && !hmInvoicewiseTaxHeadsJS.isEmpty()) {
					Iterator<String> it = hmInvoicewiseTaxHeadsJS.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();
					List<String> innerList1 = hmInvoicewiseTaxHeadsJS.get(taxHeadId);
				%>
					var cnt = <%=uF.showData(innerList1.get(0), "0") %>;
					//document.getElementById('woTaxTR'+cnt).style.display = 'none';
					if(document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffTaxPercent_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt))
						document.getElementById('strWriteOffTaxAmount_'+invcID+'_'+cnt).value = "0";
					if(document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt))
						document.getElementById('w_o_Tax_per_'+invcID+'_'+cnt).innerHTML = '';
					if(document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt))
						document.getElementById('w_o_Tax_amt_'+invcID+'_'+cnt).innerHTML = '';
				<% i++; } } %>
				}
				<% } %>
				calculateBalanceAmount(invcID, billCurr);
				
			}
		}
		</script>
		
		
			<tr>
				<td nowrap="nowrap" class="txtlabel alignRight" valign="top">
				<input type="hidden" name="invoiceId" value="<%=invoiceId %>"/>
				<input type="hidden" name="currId_<%=invoiceId %>" value="<%=innerList.get(7) %>"/>
				<input type="hidden" name="proId_<%=invoiceId %>" value="<%=innerList.get(1) %>"/>
				<input type="hidden" name="proFreqId_<%=invoiceId %>" value="<%=innerList.get(9) %>"/>
				<input type="hidden" name="clientId_<%=invoiceId %>" value="<%=innerList.get(2) %>"/>
				<input type="hidden" name="stateId_<%=invoiceId %>" value="<%=hmProWStateId.get(innerList.get(1)) %>"/>
				<%=innerList.get(3) %> </td>
				<td nowrap="nowrap" valign="top"><input type="text" name="invoiceAmount_<%=invoiceId %>" id="invoiceAmount_<%=invoiceId %>" class="validateRequired" style="width: 100px !important;" readonly="readonly" value="<%=innerList.get(4) %>" onkeypress="return isNumberKey(event)"/>&nbsp;<%=innerList.get(8) %>
				</td>
	
				<td nowrap="nowrap" valign="top"><input type="text" name="amountDue_<%=invoiceId %>" id="amountDue_<%=invoiceId %>" class="validateRequired" style="width: 100px !important;" readonly="readonly" value="<%=innerList.get(5) %>" onkeypress="return isNumberKey(event)"/>&nbsp;<%=innerList.get(8) %>
				</td>
	
				<td nowrap="nowrap" valign="top"><input type="text" name="exchangeRate_<%=invoiceId %>" id="exchangeRate_<%=invoiceId %>" class="validateRequired" style="width: 100px !important;" value="<%=innerList.get(6) %>" onkeyup="calculateExchangeRate('<%=invoiceId %>');" onkeypress="return isNumberKey(event)"/>
				</td>
	
				<td nowrap="nowrap" valign="top"><input type="text" name="amountReceived_<%=invoiceId %>" id="amountReceived_<%=invoiceId %>" class="validateRequired" style="width: 100px !important;" value="<%=innerList.get(5) %>" onkeyup="calculateBalanceAmount('<%=invoiceId %>','<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/>&nbsp;<%=innerList.get(8) %>
				</td>
	
				<% if(invoiceOPEHeads != null && !invoiceOPEHeads.isEmpty()) { 
					for(int i=0; i<invoiceOPEHeads.size(); i++) {
						List<String> innerOPEList = hmInvoicewiseOPEHeads.get(invoiceOPEHeads.get(i));
						if(innerOPEList != null && innerOPEList.get(1).equals(invoiceOPEHeads.get(i))) {
				%>
						<td nowrap="nowrap" valign="top">
						<input type="hidden" name="strHideOPE_<%=invoiceId %>" value="<%=uF.showData(innerOPEList.get(1), "-") %>" />
						<input type="hidden" name="strHideOPEPercent_<%=invoiceId %>" id="strHideOPEPercent_<%=invoiceId %>_<%=innerOPEList.get(0) %>" />
						<input type="text" name="strOPEAmount_<%=invoiceId %>" id="strOPEAmount_<%=invoiceId %>_<%=innerOPEList.get(0) %>" style="width: 100px !important;" value="0" onkeyup="calcuOPE('<%=invoiceId %>');" onkeypress="return isNumberKey(event)"/>&nbsp;<%=innerList.get(8) %> </td>
					<% } else { %>
						<td nowrap="nowrap" valign="top">&nbsp;</td>
					<% } %>
				<% } } %>
				
				<% if(invoicePartiHeads != null && !invoicePartiHeads.isEmpty()) { 
					for(int i=0; i<invoicePartiHeads.size(); i++) {
						List<String> innerPartiList = hmInvoicewisePartiHeads.get(invoicePartiHeads.get(i));
						if(innerPartiList != null && innerPartiList.get(1).equals(invoicePartiHeads.get(i))) {
				%>
						<td nowrap="nowrap" valign="top">
						<input type="hidden" name="strHideParticulars_<%=invoiceId %>" value="<%=uF.showData(innerPartiList.get(1),"-") %>" />
						<input type="hidden" name="strHideParticularsPercent_<%=invoiceId %>" id="strHideParticularsPercent_<%=invoiceId %>_<%=innerPartiList.get(0) %>" />
						<input type="text" name="strParticularsAmount_<%=invoiceId %>" id="strParticularsAmount_<%=invoiceId %>_<%=innerPartiList.get(0) %>" style="width: 100px !important;" value="0" onkeypress="return isNumberKey(event)" readonly="readonly"/>&nbsp;<%=innerList.get(8) %> </td>
					<% } else { %>
						<td nowrap="nowrap" valign="top">&nbsp;</td>
					<% } %>
				<% } } %>
				
				<% if(invoiceTaxHeads != null && !invoiceTaxHeads.isEmpty()) { 
					for(int i=0; i<invoiceTaxHeads.size(); i++) {
						List<String> innerTaxList = hmInvoicewiseTaxHeads.get(invoiceTaxHeads.get(i));
						if(innerTaxList != null && innerTaxList.get(1).equals(invoiceTaxHeads.get(i))) {
				%>
						<td nowrap="nowrap" valign="top">
						<input type="hidden" name="strHideTax_<%=invoiceId %>" value="<%=uF.showData(innerTaxList.get(1),"-") %>" />
						<input type="hidden" name="strHideTaxPercent_<%=invoiceId %>" id="strHideTaxPercent_<%=invoiceId %>_<%=innerTaxList.get(0) %>" />
						<input type="text" name="strTaxAmount_<%=invoiceId %>" id="strTaxAmount_<%=invoiceId %>_<%=innerTaxList.get(0) %>" style="width: 100px !important;" value="0" onkeypress="return isNumberKey(event)" readonly="readonly"/>&nbsp;<%=innerList.get(8) %> </td>
					<% } else { %>
						<td nowrap="nowrap" valign="top">&nbsp;</td>
					<% } %>
				<% } } %>
				
				
				
				<td nowrap="nowrap" valign="top">
				<input type="checkbox" name="deductTDS_<%=invoiceId %>" id="deductTDS_<%=invoiceId %>" onclick="checkTDS(this, '<%=invoiceId %>', '<%=innerList.get(8) %>')"/>
				
				<table class="table table_bordered" cellpadding="0" cellspacing="0" id="deductTaxTable_<%=invoiceId %>" style="display: none;"> <!-- style="display: none;" -->
					<tr id="taxD1_<%=invoiceId %>">
						<td class="txtlabel alignRight" style="border: 0px none;">Deduction Type:<sup>*</sup></td>
						<td style="border: 0px none;">
							<input type="hidden" name="hideTotTaxAmount_<%=invoiceId %>" id="hideTotTaxAmount_<%=invoiceId %>" />
							<input type="hidden" name="hideTotTaxPercent_<%=invoiceId %>" id="hideTotTaxPercent_<%=invoiceId %>" />
							<select name="deductionType_<%=invoiceId %>" id="deductionType_<%=invoiceId %>" style="width: 100px !important;" onchange="checkDeductionFields(this.value, '<%=invoiceId %>', '<%=innerList.get(8) %>');">
								<option value="1">Percentage</option>
								<option value="2">Amount</option>
							</select>
						</td>
					</tr>
					
					<% 
					if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
						Iterator<String> it1 = hmProTaxHeadData.keySet().iterator();
						int i = 0;
						while(it1.hasNext()) {
						String taxHeadId = it1.next();
						List<String> innerPTHList = hmProTaxHeadData.get(taxHeadId);
					%>
					<tr>  <%-- id="taxTR_<%=invoiceId %>_<%=i %>" --%> <!-- display: none;  -->
						<td nowrap="nowrap" class="txtlabel alignRight" style="border: 0px none;">
						<input type="hidden" name="hideTaxName_<%=invoiceId %>" value="<%=uF.showData(innerPTHList.get(1), "-") %>"/>
						<%=uF.showData(innerPTHList.get(1), "-") %>:<sup>*</sup>
						</td>
						<td nowrap="nowrap" style="border: 0px none;">
						<input type="hidden" name="hideTaxAmount_<%=invoiceId %>" id="hideTaxAmount_<%=invoiceId %>_<%=innerPTHList.get(0) %>" />
						<input type="hidden" name="hideTaxPercent_<%=invoiceId %>" id="hideTaxPercent_<%=invoiceId %>_<%=innerPTHList.get(0) %>" /> 
						<span id="taxAmountSpan_<%=invoiceId %>_<%=innerPTHList.get(0) %>" style="float: left; display: none;">
							<input type="text" name="taxAmount_<%=invoiceId %>" id="taxAmount_<%=invoiceId %>_<%=innerPTHList.get(0) %>" class="validateRequired" style="width: 100px !important;" value="0" onkeyup="calculateAmount('<%=invoiceId %>', '<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/>&nbsp;<%=innerList.get(8) %> <span id="tax_per_<%=invoiceId %>_<%=innerPTHList.get(0) %>"></span>
						</span>
						<span id="taxPercentSpan_<%=invoiceId %>_<%=innerPTHList.get(0) %>" style="float: left;">
							<input type="text" name="taxPercent_<%=invoiceId %>" id="taxPercent_<%=invoiceId %>_<%=innerPTHList.get(0) %>" class="validateRequired" style="width: 80px !important;" value="0" onkeyup="calculateAmount('<%=invoiceId %>', '<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/> % <span id="tax_amt_<%=invoiceId %>_<%=innerPTHList.get(0) %>"></span>
						</span>
						</td>
					</tr>
					
					<% if(i==0) { %>
						<tr><td class="txtlabel" style="border: 0px none;">&nbsp;</td> 
							<td class="txtlabel" style="border: 0px none;">
							<input type="checkbox" name="prevYearTDS_<%=invoiceId %>" id="prevYearTDS_<%=invoiceId %>" onclick="checkPrevYearTDS(this, '<%=invoiceId %>','<%=innerList.get(8) %>')"/>
							<%-- <s:checkbox name="prevYearTDS_<%=invoiceId %>" onclick="checkPrevYearTDS(this, '<%=invoiceId %>');"/> --%> 
							Add Previous Year's <%=uF.showData(innerPTHList.get(1),"-") %></td>
						</tr>
					
						<tr id="taxDpy_<%=invoiceId %>" style="display: none;"> <!-- display: none; -->
							<td nowrap="nowrap" class="txtlabel alignRight" style="border: 0px none;">
							<input type="hidden" name="hidePreviousYearTaxName_<%=invoiceId %>" value="<%=uF.showData(innerPTHList.get(1),"-") %>"/>
							Previous Year <%=uF.showData(innerPTHList.get(1),"-") %>:<sup>*</sup> 
							</td>
							<td nowrap="nowrap" style="border: 0px none;">
							<input type="hidden" name="hidePreviousYearTdsAmount_<%=invoiceId %>" id="hidePreviousYearTdsAmount_<%=invoiceId %>" />
							<input type="hidden" name="hidePreviousYearTdsPercent_<%=invoiceId %>" id="hidePreviousYearTdsPercent_<%=invoiceId %>" />
							<div id="previousYearTdsAmountSpan_<%=invoiceId %>" style="display: none;">
								<input type="text" name="previousYearTdsAmount_<%=invoiceId %>" id="previousYearTdsAmount_<%=invoiceId %>" class="validateRequired" style="width: 100px !important;" value="0" onkeyup="calculateBalanceAmount('<%=invoiceId %>','<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/>&nbsp;<%=innerList.get(8) %> <span id="previous_year_tds_per_<%=invoiceId %>"></span>
							</div>
							<div id="previousYearTdsPercentSpan_<%=invoiceId %>">
								<input type="text" name="previousYearTdsPercent_<%=invoiceId %>" id="previousYearTdsPercent_<%=invoiceId %>" class="validateRequired" style="width: 80px !important;" value="0" onkeyup="calculateBalanceAmount('<%=invoiceId %>','<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/> % <span id="previous_year_tds_amt_<%=invoiceId %>"></span>
							</div>
							</td>
						</tr>
					<% } %>
					
					<% i++; } } %>
					</table>
			
				</td>
				
				
				<td nowrap="nowrap" valign="top">
				<input type="checkbox" name="writeoffBalance_<%=invoiceId %>" id="writeoffBalance_<%=invoiceId %>" onclick="checkWOff(this, '<%=invoiceId %>', '<%=innerList.get(8) %>')"/>
				
				<table class="table table_bordered" cellpadding="0" cellspacing="0" id="writeOffTable_<%=invoiceId %>" style="display: none;">
					<tr> <%-- id="woTypeTR_<%=invoiceId %>" --%>
						<td class="txtlabel alignRight" style="border: 0px none;">Write off Type:<sup>*</sup></td>
						<td style="border: 0px none;">
							<input type="hidden" name="hidewriteOffAmount_<%=invoiceId %>" id="hidewriteOffAmount_<%=invoiceId %>" />
							<input type="hidden" name="hidewriteOffPercent_<%=invoiceId %>" id="hidewriteOffPercent_<%=invoiceId %>" />
							<select name="writeOffType_<%=invoiceId %>" id="writeOffType_<%=invoiceId %>" style="width: 100px !important;" onchange="checkWriteOffFields(this.value, '<%=invoiceId %>', '<%=innerList.get(8) %>');">
								<option value="1">Percentage</option>
								<option value="2">Amount</option>
							</select>
						<%-- <s:select list="#{'2':'Amount'}" name="writeOffType" id="writeOffType" headerKey="1" headerValue="Percentage" onchange="checkWriteOffFields(this.value);"/> --%>
						</td>
					</tr>
					
					<tr id="woPercentTR_<%=invoiceId %>" style="display: none;" > <!-- style="display: none"  -->
						<td class="txtlabel alignRight" style="border: 0px none;">Write off %:<sup>*</sup> </td>
						<td style="border: 0px none;">
							<input type="text" name="writeOffPercent_<%=invoiceId %>" id="writeOffPercent_<%=invoiceId %>" class="validateRequired" style="width: 80px !important;" value="0" onkeyup="checkWriteoffAmount('<%=invoiceId %>', '<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/> %
							<span id="w_o_writeOff_amt_<%=invoiceId %>"></span>
						</td>
					</tr>
					
					<tr id="woAmountTR_<%=invoiceId %>" style="display: none;"> <!--   -->
						<td class="txtlabel alignRight" style="border: 0px none;">Write off amount:<sup>*</sup></td>
						<td style="border: 0px none;"><input type="text" name="writeOffAmount_<%=invoiceId %>" id="writeOffAmount_<%=invoiceId %>" value="0" class="validateRequired" style="width: 100px !important;" onkeyup="checkWriteoffAmount('<%=invoiceId %>', '<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/>&nbsp;<%=innerList.get(8) %>
						<span id="w_o_writeOff_per_<%=invoiceId %>"></span>
						</td>
					</tr>
				
					<% 
					if(hmInvoicewiseOPEHeads != null && !hmInvoicewiseOPEHeads.isEmpty()) {
						Iterator<String> it1 = hmInvoicewiseOPEHeads.keySet().iterator();
						int i = 0;
						while(it1.hasNext()) {
						String opeHeadId = it1.next();	
						List<String> innerOPEList = hmInvoicewiseOPEHeads.get(opeHeadId);
						//String opeHeadName = hmInvoicewiseOPEHeads.get(opeHeadId);
					%>
					<tr> <%-- id="woOPETR_<%=invoiceId %>_<%=i %>" --%> <!-- style="display: none"  -->
						<td class="txtlabel alignRight" style="border: 0px none;">
						<input type="hidden" name="hidewoOPE_<%=invoiceId %>" value="<%=uF.showData(innerOPEList.get(1), "-") %>"/>
						<%=uF.showData(innerOPEList.get(1), "-") %>:<sup>*</sup></td>
						<td style="border: 0px none;">
							<input type="hidden" name="hidewoOPEAmount_<%=invoiceId %>" id="hidewoOPEAmount_<%=invoiceId %>_<%=innerOPEList.get(0) %>" />
							<input type="hidden" name="hidewoOPEPercent_<%=invoiceId %>" id="hidewoOPEPercent_<%=invoiceId %>_<%=innerOPEList.get(0) %>" /> 
							<span id="writeOffOPEAmountSpan_<%=invoiceId %>_<%=innerOPEList.get(0) %>" style="display: none;">
								<input type="text" name="strWriteOffOPEAmount_<%=invoiceId %>" id="strWriteOffOPEAmount_<%=invoiceId %>_<%=innerOPEList.get(0) %>" class="validateRequired" style="width: 100px !important;" value="0" onkeyup="calcuWriteOffOPE('<%=invoiceId %>', '<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/>&nbsp;<%=innerList.get(8) %> <span id="w_o_ope_per_<%=invoiceId %>_<%=innerOPEList.get(0) %>"></span>
							</span>
							<span id="writeOffOPEPercentSpan_<%=invoiceId %>_<%=innerOPEList.get(0) %>">
								<input type="text" name="strWriteOffOPEPercent_<%=invoiceId %>" id="strWriteOffOPEPercent_<%=invoiceId %>_<%=innerOPEList.get(0) %>" class="validateRequired" style="width: 80px !important;" value="0" onkeyup="calcuWriteOffOPE('<%=invoiceId %>', '<%=innerList.get(8) %>');" onkeypress="return isNumberKey(event)"/> % <span id="w_o_ope_amt_<%=invoiceId %>_<%=innerOPEList.get(0) %>"></span>
							</span>
						
						</td>
					</tr>
					<% i++; } } %>
				
					<% 
					if(hmInvoicewisePartiHeads != null && !hmInvoicewisePartiHeads.isEmpty()) {
						Iterator<String> it1 = hmInvoicewisePartiHeads.keySet().iterator();
						int i = 0;
						while(it1.hasNext()) {
						String partiHeadId = it1.next();
						List<String> innerPartiList = hmInvoicewisePartiHeads.get(partiHeadId);
						//String partiHeadName = hmInvoicewisePartiHeads.get(partiHeadId);
					%>
					
					<tr>  <%-- id="woPartiTR_<%=invoiceId %>_<%=i %>" --%> <!-- style="display: none"  -->
						<td class="txtlabel alignRight" style="border: 0px none;">
						<input type="hidden" name="hidewoParti_<%=invoiceId %>" value="<%=uF.showData(innerPartiList.get(1), "-") %>"/>
						<%=uF.showData(innerPartiList.get(1), "-") %>:</td>
						<td style="border: 0px none;">
							<input type="hidden" name="hidewoPartiAmount_<%=invoiceId %>" id="hidewoPartiAmount_<%=invoiceId %>_<%=innerPartiList.get(0) %>" />
							<input type="hidden" name="hidewoPartiPercent_<%=invoiceId %>" id="hidewoPartiPercent_<%=invoiceId %>_<%=innerPartiList.get(0) %>" />
							<span id="writeOffParticularsAmountSpan_<%=invoiceId %>_<%=innerPartiList.get(0) %>" style="display: none;">
								<input type="text" name="strWriteOffParticularsAmount_<%=invoiceId %>" id="strWriteOffParticularsAmount_<%=invoiceId %>_<%=innerPartiList.get(0) %>" style="width: 100px !important;" value="0" readonly="readonly"/>&nbsp;<%=innerList.get(8) %> <span id="w_o_parti_per_<%=invoiceId %>_<%=innerPartiList.get(0) %>"></span>
							</span>
							<span id="writeOffParticularsPercentSpan_<%=invoiceId %>_<%=innerPartiList.get(0) %>">
								<input type="text" name="strWriteOffParticularsPercent_<%=invoiceId %>" id="strWriteOffParticularsPercent_<%=invoiceId %>_<%=innerPartiList.get(0) %>" style="width: 80px !important;" value="0" readonly="readonly"/> % <span id="w_o_parti_amt_<%=invoiceId %>_<%=innerPartiList.get(0) %>"></span>
							</span>
						</td>
					</tr>
					<% i++; } } %>
					
					<% 
					if(hmInvoicewiseTaxHeads != null && !hmInvoicewiseTaxHeads.isEmpty()) {
						Iterator<String> it1 = hmInvoicewiseTaxHeads.keySet().iterator();
						int i = 0;
						while(it1.hasNext()) {
						String taxHeadId = it1.next();
						List<String> innerTaxList = hmInvoicewiseTaxHeads.get(taxHeadId);
						//String taxHeadName = hmInvoicewiseTaxHeads.get(taxHeadId);
					%>
					
					<tr>  <%-- id="woTaxTR_<%=invoiceId %>_<%=i %>" --%> <!-- style="display: none"  -->
						<td class="txtlabel alignRight" style="border: 0px none;">
						<input type="hidden" name="hidewoTax_<%=invoiceId %>" value="<%=uF.showData(innerTaxList.get(1), "-") %>"/>
						<%=uF.showData(innerTaxList.get(1), "-") %>:</td>
						<td style="border: 0px none;">
							<input type="hidden" name="hidewoTaxAmount_<%=invoiceId %>" id="hidewoTaxAmount_<%=invoiceId %>_<%=innerTaxList.get(0) %>" />
							<input type="hidden" name="hidewoTaxPercent_<%=invoiceId %>" id="hidewoTaxPercent_<%=invoiceId %>_<%=innerTaxList.get(0) %>" />
							<span id="writeOffTaxAmountSpan_<%=invoiceId %>_<%=innerTaxList.get(0) %>" style="display: none;">
								<input type="text" name="strWriteOffTaxAmount_<%=invoiceId %>" id="strWriteOffTaxAmount_<%=invoiceId %>_<%=innerTaxList.get(0) %>" class="validateRequired" style="width: 100px !important;" value="0" readonly="readonly"/>&nbsp;<%=innerList.get(8) %> <span id="w_o_Tax_per_<%=invoiceId %>_<%=innerTaxList.get(0) %>"></span>
							</span>
							<span id="writeOffTaxPercentSpan_<%=invoiceId %>_<%=innerTaxList.get(0) %>"> 
								<input type="text" name="strWriteOffTaxPercent_<%=invoiceId %>" id="strWriteOffTaxPercent_<%=invoiceId %>_<%=innerTaxList.get(0) %>" class="validateRequired" style="width: 80px !important;" value="0" readonly="readonly"/> % <span id="w_o_Tax_amt_<%=invoiceId %>_<%=innerTaxList.get(0) %>"></span>
							</span>
						</td>
					</tr>
					<% i++; } } %>
				</table>
				</td>
			
				<td nowrap="nowrap" valign="top"><input type="text" name="balanceAmount_<%=invoiceId %>" id="balanceAmount_<%=invoiceId %>" style="width: 100px !important;" value="0" readonly="readonly"/>&nbsp;<%=innerList.get(8) %>
				</td>
			
				<td nowrap="nowrap"  valign="top"> <textarea name="paymentDescription_<%=invoiceId %>" id="paymentDescription_<%=invoiceId %>" rows="2" cols="22"></textarea>
				</td>
	
				<td nowrap="nowrap" valign="top">
				<select name="paymentSource_<%=invoiceId %>" id="paymentSource_<%=invoiceId %>" style="width: 100px !important;" onchange="showInstrumentInfo(this.value,'<%=invoiceId %>')">
					<%=(String)request.getAttribute("paymentSource") %>
				</select>
				</td>
	
				<td nowrap="nowrap" valign="top">
					<span id="instrumentNoSpan_<%=invoiceId %>" style="display: none;"><input type="text" name="strInstrumentNo_<%=invoiceId %>" id="strInstrumentNo_<%=invoiceId %>" class="validateRequired"/> </span>
				</td>
	
				<td nowrap="nowrap" valign="top">
					<span id="instrumentDateSpan_<%=invoiceId %>" style="display: none;"><input type="text" name="strInstrumentDate_<%=invoiceId %>" id="idInstrumentDate_<%=invoiceId %>"  class="validateRequired" style="width: 100px !important;"/></span>
				</td>
			</tr>
		<% } %>
		
			<tr>
				<td colspan="<%=colCnt %>" style="margin-left: 25px;"><s:submit cssClass="btn btn-primary" value="Save" name="submit" id="btnOk" /></td>
			</tr>
		</table>
	<% } else { %>
		<div class="msg nodata" style="width:94%"><span>No receipts selected.</span></div>
	<% } %>	
	</div>
</div>

</s:form>

</div>

</div>
</div>

</section>


<script type="text/javascript">

<% if(hmInvoiceDetails !=null && !hmInvoiceDetails.isEmpty()) { 
	Iterator<String> it = hmInvoiceDetails.keySet().iterator();

	while(it.hasNext()) {
		String invoiceId = it.next();
%>
	calculateBalanceAmount(<%=invoiceId %>);

<% 		}  
	} %>

</script>


