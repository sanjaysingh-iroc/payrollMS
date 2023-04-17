<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<div id="popupDivResult">

<script>
$(function() {
	// binds form submission and fields to the validation engine
	
	$("#setData").click(function(){
		$("#frmProjectAdHocInvoiceFormat_1").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#frmProjectAdHocInvoiceFormat_1").find('.validateRequired').filter(':visible').prop('required',true);
	});
	
	var val = document.getElementById("strInvoiceTemplate").value;
	checkInvoiceTemplate(val);
	
	var val1 = document.getElementById("billingType").value;
	checkBillingType(val1);

    $("#strDueDate").datepicker({format : 'dd/mm/yyyy'});
    $("#invoiceGenDate").datepicker({format : 'dd/mm/yyyy'});
});


function getClientDetails(clientid) {
	//otherClntTR cPocDiv othrCPocDiv cAddressDiv othrCAddressDiv othrClntProjectTR
	if(clientid == '0') {
		document.getElementById("otherClientNameDiv").style.display = "block";
		document.getElementById("otherClientProNameDiv").style.display = "block";
		document.getElementById("othrCPocDiv").style.display = "block";
		document.getElementById("otherClientAddressDiv").style.display = "block";
		document.getElementById("cPocDiv").style.display = "none";
	} else {
		document.getElementById("otherClientNameDiv").style.display = "none";
		document.getElementById("otherClientProNameDiv").style.display = "none";
		document.getElementById("othrCPocDiv").style.display = "none";
		document.getElementById("otherClientAddressDiv").style.display = "none";
		document.getElementById("cPocDiv").style.display = "block";
		
			var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var xhr = $.ajax({
					url : "GetClientSpocAndAddress.action?clientid="+clientid,
					cache : false,
					success : function(data) {
						var res = data.split("::::");
						//alert("data ===>> " + data);
						document.getElementById("cPocDiv").innerHTML=res[0];
						//document.getElementById("cAddressDiv").innerHTML=res[1];
					}
				});
			}
		}	
	}
	
	
	function GetXmlHttpObject() {
		if (window.XMLHttpRequest) {
			// code for IE7+, Firefox, Chrome, Opera, Safari
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject) {
			// code for IE6, IE5
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
		return null;
	}


	function checkInvoiceTemplate(val) {
		if(val == 1) {
			document.getElementById("fromatOneDataDiv").style.display = "block";
		} else {
			document.getElementById("fromatOneDataDiv").style.display = "none";
		}
	}
	
	
	function checkBillingType(val) {
		if(val == 2) {
			document.getElementById("proRataBillTypeDiv").style.display = "block";
		} else {
			document.getElementById("proRataBillTypeDiv").style.display = "none";
		}
	}


	$("#frmProjectAdHocInvoiceFormat_1").submit(function(e){
		e.preventDefault();
		var form_data = $("form[name='frmProjectAdHocInvoiceFormat_1']").serialize();
     	$("#popupDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "ProjectAdHocInvoiceFormat_1.action?setData=Submit",
 			data: form_data,
 			cache : false,
 			success : function(res) {
 				$("#popupDivResult").html(res);
 			}
 		});
	});
	
	
	/* function getFormForInvoice() {
		
		var cnt = 0;
		if(document.getElementById("strInvoiceTemplate").value == '' || document.getElementById("strProjectOwner").value == '' || document.getElementById("client1").value == '' 
			|| document.getElementById("service").value == '' || document.getElementById("strInvoiceCurrency").value == '' || document.getElementById("clientPoc1").value == '' 
			|| document.getElementById("bankName1").value == '') {
			cnt = 1;
		} else if(document.getElementById("client1").value == '0' && document.getElementById("othrClientName")) {
			if(document.getElementById("othrClientName").value == '' || document.getElementById("otheClientPoc").value == '' || document.getElementById("otherClientAddress").value == ''
				 || document.getElementById("othrClntProject").value == '') {
				cnt = 1;
			}
		} else if(document.getElementById("strInvoiceTemplate").value == '1' && document.getElementById("strDueDate")) {
			if(document.getElementById("strDueDate").value == '') {
				cnt = 1;
			}
		}
		if(cnt == 1) {
			alert("Please fill all manditory fields.");
		} else {
			var form_data = $("form[name='frmProjectAdHocInvoiceFormat_1']").serialize();
	     	$("#popupDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	     	$.ajax({
	 			url : "ProjectAdHocInvoiceFormat_1.action?setData=Submit",
	 			data: form_data,
	 			cache : false,
	 			success : function(res) {
	 				$("#popupDivResult").html(res);
	 			}
	 		});
		}
	}  */
	
</script>

<div id="invoiceSummaryid" class="box-body">

<div style="float: left; width: 32%;">
	<s:form theme="simple" action="ProjectAdHocInvoiceFormat_1" name="frmProjectAdHocInvoiceFormat_1" id="frmProjectAdHocInvoiceFormat_1" cssClass="formcss">
		<s:hidden name="proType" id="proType"></s:hidden>
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Invoice Template:<sup>*</sup></div>
			<div style="float: left; width: 65%;">
			<s:select theme="simple" name="strInvoiceTemplate" id="strInvoiceTemplate" listKey="invoiceFormatId" listValue="invoiceFormatName"
				cssStyle="width: 200px !important;" cssClass="validateRequired" headerKey="" headerValue="Select Invoice Template" list="invoiceTemplateList" onchange="checkInvoiceTemplate(this.value);"/>
			</div> 
		</div>
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Billing Type:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<s:select label="Select Billing Type" name="billingType" id="billingType" cssClass="validateRequired" 
					headerKey="1" headerValue="General" list="#{'2':'Prorata'}" cssStyle="width: 200px !important;" onchange="checkBillingType(this.value)"/>
			</div>
		</div>
		
		<div id="proRataBillTypeDiv" style="display: none; float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Calculation Type:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<s:select name="billCalType" id="billCalType" cssClass="validateRequired" headerKey="1" headerValue="Days" 
					list="#{'2':'Hours', '3':'Months'}" cssStyle="width: 200px !important;" />
			</div>
		</div>
		
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Project Owner:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<s:select name="strProjectOwner" id="strProjectOwner" listKey="employeeId" cssClass="validateRequired" cssStyle="width: 200px !important;"
					headerKey="" headerValue="Select Project Owner" listValue="employeeName" list="projectOwnerList"/>
			</div>
		</div>
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Client:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<s:select name="client" id="client1" listKey="clientId" cssClass="validateRequired" headerKey="" headerValue="Select Client" listValue="clientName" 
					list="clientList" key="" required="true" cssStyle="width: 200px !important;" onchange="getClientDetails(this.value)" />
			</div>
		</div>
		<div id="otherClientNameDiv" style="display: none; float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Client Name:<sup>*</sup></div>
			<div style="float: left; width: 65%;">
				<s:textfield name="othrClientName" id="othrClientName" cssClass="validateRequired" />
			</div>
		</div>
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">SPOC:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<div id="cPocDiv">
		        	<s:select theme="simple" name="clientPoc" id="clientPoc1" listKey="clientPocId" listValue="clientPocName" headerKey="" headerValue="Select SPOC" 
		        		cssClass="validateRequired" cssStyle="width: 200px !important;" list="clientPocList" />
				</div>
				<div id="othrCPocDiv" style="display: none;">
					<s:textfield name="otheClientPoc" id="otheClientPoc" cssClass="validateRequired" />
				</div>
			</div>
		</div>
		<div id="otherClientAddressDiv" style="display: none; float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Client Address:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<s:textfield name="otherClientAddress" id="otherClientAddress" cssClass="validateRequired" />
			</div>
		</div>
		<div id="otherClientProNameDiv" style="display: none; float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Project Name:<sup>*</sup> </div> 
			<div style="float: left; width: 65%;">
				<s:textfield name="othrClntProject" id="othrClntProject" cssClass="validateRequired" />
			</div>
		</div>
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Service:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<s:select name="service" id="service" list="serviceList" listKey="serviceId" listValue="serviceName" headerKey="" headerValue="Select Service" 
			        cssClass="validateRequired" cssStyle="width: 200px !important;" />
			</div>
		</div>
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Currency:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<s:select id="strInvoiceCurrency" cssClass="validateRequired" name="strInvoiceCurrency" listKey="currencyId" listValue="currencyName" 
					list="currencyList" key="" required="true" cssStyle="width: 200px !important;" />
			</div>
		</div>
			
			
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Bank:<sup>*</sup></div> 
			<div style="float: left; width: 65%;">
				<s:select name="bankName" id="bankName1" listKey="bankId" listValue="bankName" cssClass="validateRequired" cssStyle="width: 200px !important;" headerKey="" 
					headerValue="Select Bank" list="bankList"/>
			</div>
		</div>
		
		<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Paypal Id:</div> 
				<div style="float: left; width: 65%;">
					<s:textfield name="payPalMailId" id="payPalMailId" cssStyle="width:200px !important; height: 25px;"/>
				</div>
			</div>
			
		<div id="fromatOneDataDiv" style="display: none;">
			<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Account Ref:</div> 
				<div style="float: left; width: 65%;">
					<s:textfield name="strAccountRef" id="strAccountRef" cssStyle="width: 200px !important; height: 25px;"/>
				</div>
			</div>
			
			<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">P.O. No.:</div> 
				<div style="float: left; width: 65%;">
					<s:textfield name="strPONo" id="strPONo" cssStyle="width: 200px !important; height: 25px;"/>
				</div>
			</div>
			
			<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Terms:</div> 
				<div style="float: left; width: 65%;">
					<s:textarea name="strTerms" id="strTerms" cols="23" rows="3"></s:textarea>
				</div>
			</div>
			
			<div style="float: left; width: 100%; padding: 5px 0px;"><div style="float: left; width: 35%;">Due Date:<sup>*</sup></div> 
				<div style="float: left; width: 65%;">
					<s:textfield name="strDueDate" id="strDueDate" cssStyle="width: 100px !important; height: 25px;" cssClass="validateRequired" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div style="float: left; width: 100%; text-align: center; padding: 5px 0px;">
			<input type="submit" name="setData" id="setData" class="btn btn-primary" value="Submit"/> <!-- onclick="getFormForInvoice();" -->
		</div>
		
	</s:form>
</div>

	<% 
		String billingType = (String) request.getAttribute("billingType");
		String strInvoiceTemplate = (String) request.getAttribute("strInvoiceTemplate");
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	%>

<div style="float: left; width: 68%; padding: 4px; min-height: 300px; border-radius: 4px 4px 4px 4px; border: 1px solid #A1A1A1;">
	<%-- <s:form theme="simple" action="ProjectAdHocInvoiceFormat_1" name="frmProjectAdHocInvoiceFormat_1" id="formProjectAdHocInvoiceFormat_1" cssClass="formcss">
	<input type="hidden" name="invoiceType" id="invoiceType" value="1"/>
	<s:hidden name="operation" id="operation" />
	<s:hidden name="invoiceId" id="invoiceId" /> --%>
	<% if(strInvoiceTemplate == null || strInvoiceTemplate.equals("")) { %>
		<div class="msg nodata" style="width:94%"><span>No data has been submitted.</span></div>
	<% } %>
	
	<% if(billingType != null && uF.parseToInt(billingType) == 1 && (uF.parseToInt(strInvoiceTemplate) == 1 || uF.parseToInt(strInvoiceTemplate) == 2)) { %>
	
	<%
		String proCurr = (String)request.getAttribute("proCurrency");
		Map<String, Map<String, String>> hmCurrencyDetails = (Map<String, Map<String, String>>)request.getAttribute("hmCurrencyDetails");
		Map<String, String> hmCurr = hmCurrencyDetails.get(proCurr);
		if(hmCurr==null) hmCurr = new HashMap<String, String>();
		
		String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("")? hmCurr.get("SHORT_CURR") : "";
		
		Map<String, List<String>> hmProBillingHeadData = (Map<String, List<String>>)request.getAttribute("hmProBillingHeadData");
		Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmProTaxHeadData");
		
		Map<String, String> hmOrgData = (Map<String, String>) request.getAttribute("hmOrgData");
		if(hmOrgData == null) hmOrgData = new HashMap<String, String>();
		
		Map<String,String> hmClientDetails = (Map<String,String>) request.getAttribute("hmClientDetails");
		if(hmClientDetails == null) hmClientDetails = new HashMap<String, String>();
		
		Map<String, String> hmBankAccData = (Map<String, String>) request.getAttribute("hmBankAccData");
		if(hmBankAccData == null) hmBankAccData = new HashMap<String, String>();
	%>


<script>
	$(function() {
	// binds form submission and fields to the validation engine 
	$("#btnOk").click(function(){
		$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
		//$(".validateRequired").prop('required',true);
	});
	
	/* var strProjectOwner = document.getElementById("strProjectOwner").value; */
	var strProjectOwner = <%=request.getAttribute("strProjectOwner") %>;
	getProjectOwnerDetails(strProjectOwner);
	
	var proCurrType = document.getElementById("strCurrency").value;
	getExchangeValue(proCurrType);
});


function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
      return false;
   }
   return true;
}


function calAmt() {
	
	var cntAdd = document.getElementById("strPartiCount").value;
	//alert("cntAdd ===>> " + cntAdd);
	var amt = 0;
	var taxamt = 0;
	var totalAmt = 0;
	if(cntAdd == undefined || cntAdd == 'undefined') {
		cntAdd = 0;
	}
	
	for(var i=0; i<=cntAdd; i++) {
		var id=document.getElementById("strParticularsAmt"+i);
		if(id) {
			if(id.value != '') {
				amt=parseFloat(amt) +parseFloat(id.value);
			}
		}
	}
	
	for(var i=0; i<=cntAdd; i++) {
		var id = document.getElementById("strOtherParticularsAmt"+i);
		if(id) {
			if(id.value != '') {
				amt=parseFloat(amt) +parseFloat(id.value);
			}
		}
	}
	
	document.getElementById("strTotalAmt").value = parseFloat(amt).toFixed(2);
	totalAmt = parseFloat(amt);
	
	//alert("totalAmt ===>> " + totalAmt);
	
	<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
		Iterator<String> it = hmProTaxHeadData.keySet().iterator();
		int i = 0;
		while(it.hasNext()) {
		String taxHeadId = it.next();	
		List<String> innerList = hmProTaxHeadData.get(taxHeadId);
		%>
		var taxPercent = <%=uF.showData(innerList.get(2), "0") %>;
		var cnt = <%=uF.showData(i+"", "0") %>;
		taxamt=(parseFloat(amt) * parseFloat(taxPercent)) / 100;
		
		totalAmt = parseFloat(totalAmt) + parseFloat(taxamt);
		
		document.getElementById("taxHeadAmtDiv"+cnt).innerHTML = parseFloat(taxamt).toFixed(2);

		document.getElementById("taxHeadAmt"+cnt).value = parseFloat(taxamt).toFixed(2);
		<% i++; } } %>

		document.getElementById("totaldiv").innerHTML = parseFloat(totalAmt).toFixed(2);
		document.getElementById("particularTotalAmt").value = parseFloat(amt).toFixed(2);
		document.getElementById("totalAmt").value = parseFloat(totalAmt).toFixed(2);
		//alert("totalAmt ===>> " + totalAmt);
}


function calAmtOtherCurr() {
	
	var cntOtherAdd = document.getElementById("strPartiCountOtherCurr").value;
	
	var amt = 0;
	var otherCurrAmt = 0;
	
	var totalAmt = 0;
	var otherCurrTotalAmt = 0;
	var otherCurrVal = 0;
	var otherCurrOtherPAmt = 0;
	
	var taxamt = 0;
	var otherCurrTaxamt = 0;
	if(cntOtherAdd == undefined || cntOtherAdd == 'undefined'){
		cntOtherAdd = 0;
	}
	var exchangeValue = document.getElementById("exchangeValue").value;
	//alert("exchangeValue ==>> " + exchangeValue);
	
	for(var i=0;i<=cntOtherAdd;i++) {
		var id = document.getElementById("strParticularsAmtINRCurr"+i);
		if(id) {
			if(id.value != '') {
				if(parseFloat(exchangeValue) > 0) {
					otherCurrVal = parseFloat(id.value) / parseFloat(exchangeValue);
				}
				document.getElementById("strParticularsAmtOtherCurr"+i).value = parseFloat(otherCurrVal).toFixed(2);
				amt = parseFloat(amt) + parseFloat(id.value);
				otherCurrAmt = parseFloat(otherCurrAmt) + parseFloat(otherCurrVal);
			} else {
				document.getElementById("strParticularsAmtOtherCurr"+i).value = '0.00';
			}
		}
	}

	for(var i=0;i<=cntOtherAdd;i++) {
		var id = document.getElementById("strOtherParticularsAmtINRCurr"+i);
		if(id) {
			if(id.value != '') {
				if(parseFloat(exchangeValue) > 0) {
					otherCurrVal = parseFloat(id.value) / parseFloat(exchangeValue);
				}
				document.getElementById("strOtherParticularsAmtOtherCurr"+i).value = parseFloat(otherCurrVal).toFixed(2);
				amt = parseFloat(amt) + parseFloat(id.value);
				otherCurrAmt = parseFloat(otherCurrAmt) + parseFloat(otherCurrVal);
			} else {
				document.getElementById("strOtherParticularsAmtOtherCurr"+i).value = '0.00';
			}
		}
	}
		
	document.getElementById("strTotalAmtINRCurr").value = parseFloat(amt).toFixed(2);
	document.getElementById("strTotalAmtOtherCurr").value = parseFloat(otherCurrAmt).toFixed(2);
	
	totalAmt = parseFloat(amt);
	otherCurrTotalAmt = parseFloat(otherCurrAmt);
	
	<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
		Iterator<String> it = hmProTaxHeadData.keySet().iterator();
		int i = 0;
		while(it.hasNext()) {
		String taxHeadId = it.next();	
		List<String> innerList = hmProTaxHeadData.get(taxHeadId);
		%>
		var taxPercent = <%=uF.showData(innerList.get(2), "0") %>;
		var cnt = <%=uF.showData(i+"", "0") %>;
		taxamt=(parseFloat(amt) * parseFloat(taxPercent)) / 100;
		
		if(parseFloat(exchangeValue) > 0) {
			otherCurrTaxamt = parseFloat(taxamt) / parseFloat(exchangeValue);
		}
		totalAmt = parseFloat(totalAmt) + parseFloat(taxamt);
		otherCurrTotalAmt = parseFloat(otherCurrTotalAmt) + parseFloat(otherCurrTaxamt);
		
		document.getElementById("taxHeadINRAmtDiv"+cnt).innerHTML = parseFloat(taxamt).toFixed(2);
		document.getElementById("taxHeadOtherAmtDiv"+cnt).innerHTML = parseFloat(otherCurrTaxamt).toFixed(2);

		document.getElementById("taxHeadAmtINRCurr"+cnt).value = parseFloat(taxamt).toFixed(2);
		document.getElementById("taxHeadAmtOtherCurr"+cnt).value = parseFloat(otherCurrTaxamt).toFixed(2);
		<% i++; } } %>

	document.getElementById("totalINRAmtdiv").innerHTML = parseFloat(totalAmt).toFixed(2);
	document.getElementById("totalOtherCurrdiv").innerHTML = parseFloat(otherCurrTotalAmt).toFixed(2);
	
	document.getElementById("particularTotalAmtINRCurr").value = parseFloat(amt).toFixed(2);
	document.getElementById("totalAmtINRCurr").value = parseFloat(totalAmt).toFixed(2);
	
	document.getElementById("particularTotalAmtOtherCurr").value = parseFloat(otherCurrAmt).toFixed(2);
	document.getElementById("totalAmtOtherCurr").value = parseFloat(otherCurrTotalAmt).toFixed(2);
}


function getExchangeValue(value) {
	
	 var proCurrency = document.getElementById("proCurrency").value;
		
	 if(value == proCurrency || value == '' || value == '0') {
		 //getContent('exchangeValDIV', '');
		 document.getElementById("invoiceType").value = '1';
		 document.getElementById("exchangeValDIV").innerHTML = "";
	 } else {
		 document.getElementById("invoiceType").value = '2';
		 getContent('exchangeValDIV', 'ViewAndUpdateExchangeValue.action?currencyType='+value+'&proCurrency='+proCurrency+'&type=V');
	 }
	 window.setTimeout(function() {
		 changeCurrencywiseData(value, proCurrency);
		}, 1000);
}


function updateExchangeValue() {
	 
	var currencyType = document.getElementById("currencyType").value;
	var proCurrency = document.getElementById("proCurrency").value;
	var exchangeValue = document.getElementById("exchangeValue").value;
 	getContent('exchangeValDIV', 'ViewAndUpdateExchangeValue.action?currencyType='+currencyType+'&proCurrency='+proCurrency+'&exchangeValue='+exchangeValue+'&type=U');
}


function checkInvoiceCode() {

	//alert("strInvoiceCode ===>> " + strInvoiceCode);
	var strInvoiceCode = document.getElementById("invoiceCode").value;
	var strProId = document.getElementById("pro_id").value;
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {

			var xhr = $.ajax({
				url : "CheckInvoiceCodeExist.action?strInvoiceCode="+strInvoiceCode+"&strProId="+strProId,
				cache : false,
				success : function(data) {
					var res = data.split("::::");
					//alert("data ===>> " + data+" res ==>> " + res.length);
					if(parseFloat(res[0]) == 1) {
						alert("This invoice code exists, please try with another invoice code.");
						document.getElementById("invoiceCode").value=res[1];
						return false;
					} else {
						return true;
					}
				}
			});
		}
	}
	
	
	function changeCurrencywiseData(value, proCurrency) {
		 
		 //getContent('INRCurrencyDIV', 'GetINRCurrencyValue.action?currencyType='+currencyType+'&exchangeValue='+exchangeValue+'&type=U');
		 if(value == proCurrency || value == '' || value == '0') {
			 document.getElementById("INRCurrencyTable").style.display = "block";
			 document.getElementById("otherCurrencyTable").style.display = "none";
			 //INRCurrencyTable otherCurrencyTable
			 calAmt();
		 } else {
			 document.getElementById("otherCurrencyTable").style.display = "block";
			 if(document.getElementById("longCurrency")) {
				 var longCurrency = document.getElementById("longCurrency").value;
				 document.getElementById("otherCurrSpan").innerHTML = longCurrency;
			 }
			 document.getElementById("INRCurrencyTable").style.display = "none";
			 calAmtOtherCurr();
		 }
	 }
	
	
	function addParticular() {
		
		var cntAdd = document.getElementById("strPartiCount").value;
		var srNoCnt = document.getElementById("srNoCnt").value;
		cntAdd++;
	    var divTag = document.createElement("div");
	    divTag.id = "p_add"+cntAdd;
	    divTag.setAttribute("style","float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;");
	    divTag.innerHTML = "<div style=\"float: left; width: 6%;\"><div style=\"float: left; margin-left: 10px;\">"+srNoCnt+".</div></div>"+
	    	"<div style=\"float: left; width: 74%;\"><div style=\"float: left; margin-left: 10px;\"><span style=\"float: left;\">"+
	    	//"<input type=\"text\" style=\"width: 250px !important;\" id=\"strParticulars\" name=\"strParticulars\" class=\"validateRequired\"></span>"+
	    	"<textarea style=\"width: 250px !important;\" id=\"strParticulars\" name=\"strParticulars\" cols=\"85\" rows=\"3\" class=\"validateRequired\"></textarea></span>"+
	    	"<span style=\"float: left; height: 15px;\"><a title=\"Add\" class=\"fa fa-plus\" onclick=\"addParticular()\" href=\"javascript:void(0)\">&nbsp;</a>"+
	    	"<a href=\"javascript:void(0)\" onclick=\"removeParticular('p_add"+cntAdd+"')\" class=\"fa fa-remove\">&nbsp;</a></span>"+
	    	"</div></div>"+
	    	"<div style=\"float: left; width: 20%;\"><div style=\"float: right; margin-right: 10px;\">"+
	    	"<input type=\"text\" name=\"strParticularsAmt\" id=\"strParticularsAmt"+cntAdd+"\" style=\"width: 65px !important; text-align: right;\" class=\"validateRequired\" onkeyup=\"calAmt();\" onkeypress=\"return isNumberKey(event)\"/>"+
	    	"</div></div>";

	    document.getElementById("INRCurrPartiDiv").appendChild(divTag);
	    srNoCnt++;
	    document.getElementById("srNoCnt").value = srNoCnt;
	    document.getElementById("strPartiCount").value = cntAdd;
	}
	
	
	function addParticularOtherCurr() {
		
		var cntAdd = document.getElementById("strPartiCountOtherCurr").value;
		var srNoCnt = document.getElementById("srNoCntOtherCurr").value;
		cntAdd++;
	    var divTag = document.createElement("div");
	    divTag.id = "p_addOtherCurr"+cntAdd;
	    divTag.setAttribute("style","float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;");
	    divTag.innerHTML = "<div style=\"float: left; width: 6%;\"><div style=\"float: left; margin-left: 10px;\">"+srNoCnt+".</div></div>"+
	    	"<div style=\"float: left; width: 58%;\"><div style=\"float: left; margin-left: 10px;\"><span style=\"float: left;\">"+
	    	//"<input type=\"text\" style=\"width: 250px !important;\" id=\"strParticularsINRCurr\" name=\"strParticularsINRCurr\" class=\"validateRequired\"></span>"+
	    	"<textarea style=\"width: 250px !important;\" id=\"strParticularsINRCurr\" name=\"strParticularsINRCurr\" cols=\"85\" rows=\"3\" class=\"validateRequired\"></textarea></span>"+
	    	"<span style=\"float: left; height: 15px;\"><a title=\"Add\" class=\"fa fa-plus\" onclick=\"addParticularOtherCurr()\" href=\"javascript:void(0)\">&nbsp;</a>"+
	    	"<a href=\"javascript:void(0)\" onclick=\"removeParticular('p_addOtherCurr"+cntAdd+"')\" class=\"fa fa-remove\">&nbsp;</a></span>"+
	    	"</div></div>"+
	    	"<div style=\"float: left; width: 18%;\"><div style=\"float: right; margin-right: 10px;\">"+
	    	"<input type=\"text\" name=\"strParticularsAmtINRCurr\" id=\"strParticularsAmtINRCurr"+cntAdd+"\" style=\"width: 65px !important; text-align: right;\" class=\"validateRequired\" onkeyup=\"calAmtOtherCurr();\" onkeypress=\"return isNumberKey(event)\"/>"+
	    	"</div></div>"+
	    	"<div style=\"float: left; width: 18%;\"><div style=\"float: right; margin-right: 10px;\">"+
	    	"<input type=\"text\" name=\"strParticularsAmtOtherCurr\" id=\"strParticularsAmtOtherCurr"+cntAdd+"\" style=\"width: 65px !important; text-align: right;\" readonly=\"readonly\"/>"+
	    	"</div></div>";

	    document.getElementById("OtherCurrPartiDiv").appendChild(divTag);
	    srNoCnt++;
	    document.getElementById("srNoCntOtherCurr").value = srNoCnt;
	    document.getElementById("strPartiCountOtherCurr").value = cntAdd;
	}
	
	
	function removeParticular(id){
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
		}
		
		calAmt();
	}
	
</script>


<s:form theme="simple" action="ProjectAdHocInvoiceFormat_1" name="frm" id="formID" cssClass="formcss" method="post">
	<s:hidden name="pro_id" id="pro_id"></s:hidden>
	<s:hidden name="pro_freq_id"></s:hidden>
	<s:hidden name="billingType"></s:hidden>
	<%-- <s:hidden name="billCalType"></s:hidden> --%>
	<s:hidden name="strInvoiceTemplate"></s:hidden>
	<s:hidden name="service" id="service"></s:hidden>
	<s:hidden name="client" id="client"></s:hidden>
	<s:hidden name="clientPoc" id="clientPoc"></s:hidden>
	<s:hidden name="strProjectOwner" id="strProjectOwner"></s:hidden>
	<s:hidden name="bankName" id="bankName"></s:hidden>
	<s:hidden name="payPalMailId" id="payPalMailId"></s:hidden>
	<s:hidden name="strAccountRef" id="strAccountRef"></s:hidden>
	<s:hidden name="strPONo" id="strPONo"></s:hidden>
	<s:hidden name="strTerms" id="strTerms"></s:hidden>
	<s:hidden name="strDueDate" id="strDueDate"></s:hidden>
	<s:hidden name="proType" id="proType"></s:hidden>
	<input type="hidden" name="invoiceType" id="invoiceType" value="1"/>

	<div style="width: 100%; float: left; margin-bottom: 25px;">
		<div style="float: left; width: 30%;">
			<img src='<%=CF.getStrDocRetriveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+hmOrgData.get("ORG_LOGO") %>' height="40">&nbsp;
		</div> 
		<div style="float: left; text-align: center; width: 30%; padding: 0px 55px;">
			<div style="float: left; text-align: center; width: 100%; font-weight: bold; font-size: 14px;"><%=hmOrgData.get("ORG_NAME") %></div>
			<div style="float: left; text-align: center; width: 100%;"><%=hmOrgData.get("ORG_ADDRESS") %></div>
		</div>
		<div style="float: right; width: 25%; text-align: center; font-size: 26px; font-weight: bold;">
			Invoice
		</div>
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 10px;">
		<div style="float: left; width: 30%;">
			<div style="float: left; text-align: center; font-weight: bold; width: 100%; color: white; background-color: #3B9C9C;">Invoice To</div>
			<div style="float: left; color: #404040; background-color: #EFEFEF; width: 100%; padding-bottom: 7px;">
				<div style="margin-left: 20px;">
					<%=hmClientDetails.get("CLIENT_SPOC") %>
					<br/>
					<%=hmClientDetails.get("CLIENT_NAME") %>
					<br/>
					<%=hmClientDetails.get("CLIENT_ADDRESS") %>
				</div>
			</div>
		</div>
		
		<div style="float: left; text-align: center; font-weight: bold; width: 25%; padding: 0px 30px;">
			&nbsp;
		</div>
		
		<div style="float: right; width: 36%; text-align: center;">
			<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
				<div style="float: left; width: 35%; text-align: center; font-weight: bold;">Date</div>
				<div style="float: left; width: 65%; font-weight: bold; text-align: center;">Invoice No.</div>
			</div>
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding-bottom: 7px;">
				<div style="float: left; width: 35%; text-align: center;">
					<s:textfield name="invoiceGenDate" id="invoiceGenDate" cssClass="validateRequired" cssStyle="width: 80px !important; margin-top: 5px;" readonly="true"/>
				</div>
				<div style="float: left; width: 65%; text-align: center;">
					<s:textfield name="invoiceCode" id="invoiceCode" cssClass="validateRequired" cssStyle="width: 150px !important; margin-top: 5px;" onkeyup="checkInvoiceCode();"/>
					<%-- <s:hidden name="invoiceCode" id="invoiceCode"></s:hidden>
					<%=(String)request.getAttribute("invoiceCode") %> --%>
				</div>
			</div>
		</div>
	</div>
	
	<div style="width: 100%; float: left; margin-bottom: 15px; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
		<div style="float: left; margin-left: 7px;">
        	<s:hidden name="proCurrency" id="proCurrency"/>
	        	<s:select id="strCurrency" cssClass="validateRequired" name="strCurrency" listKey="currencyId" listValue="currencyName" headerKey="" 
	        		headerValue="Select Currency" list="currencyList" key="" required="true" cssStyle="width: 200px;" onchange="getExchangeValue(this.value);"/>
        	</div>
        	
		<div id="exchangeValDIV" style="float: left; margin-left: 10px; margin-top: -10px;"> </div>
    </div>	
	
	
	<% if(uF.parseToInt(strInvoiceTemplate) == 1) { %>
		<div style="width: 100%; float: left; margin-bottom: 15px;">
			<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Account Ref.</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">P.O. No.</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Terms</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Due Date</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Project Name</div>
				</div>
			</div>
			
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding-bottom: 7px;">
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=uF.showData((String)request.getAttribute("strAccountRef"), "-") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=uF.showData((String)request.getAttribute("strPONo"), "-") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=uF.showData((String)request.getAttribute("strTerms"), "-") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=(request.getAttribute("strDueDate")!= null && !request.getAttribute("strDueDate").equals("")) ? uF.showData(uF.getDateFormat((String)request.getAttribute("strDueDate"), IConstants.DATE_FORMAT, CF.getStrReportDateFormat()), "") : "-" %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=uF.showData("", "-") %></div>
				</div>
			</div>
		</div>
	<% } %>
	
	
	<!-- ************************************** INRCurrencyTable Start *************************************** -->
<div id="INRCurrencyTable" style="display: block;">
	<div style="width: 100%; float: left; margin-bottom: 9px;">
		<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
			<div style="float: left; width: 6%;">
				<div style="float: left; font-weight: bold; margin-left: 5px;">#</div>
			</div>
			<div style="float: left; width: 74%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Description</div>
			</div>
			<div style="float: left; width: 20%;">
				<input type="hidden" name="strPartiCount" id="strPartiCount" value="<%=(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) ? hmProBillingHeadData.size() : "1" %>">
				<s:hidden name="strStartDate" id="strStartDate"></s:hidden>
				<s:hidden name="strEndDate" id="strEndDate"></s:hidden>
				<div style="float: right; font-weight: bold; margin-right: 20px;">Amount (<%=currency %>)</div>
			</div>
		</div>
		
		<% 
			String strPerticnt = (String)request.getAttribute("strPerticnt");
			int srNoCnt = 1;
			if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) {
				Iterator<String> it = hmProBillingHeadData.keySet().iterator();
				int i = 0;
				int j = 0;
			%>
			<div id="INRCurrPartiDiv">
			<%
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProBillingHeadData.get(billingHeadId);
				
				if(uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE && uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE_OVERALL) { // 
			%>
			
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 6%;">
					<div style="float: left; margin-left: 10px;"><%=srNoCnt %>.</div>
				</div>
				<div style="float: left; width: 74%;">
					<div style="float: left; margin-left: 10px;">
						<span style="float: left;">
						<%-- <input type="text" style="width: 250px !important;" id="strParticulars" value="<%=innerList.get(1) %>" name="strParticulars" class="validateRequired"> --%>
						<textarea style="width: 250px !important;" id="strParticulars" name="strParticulars" cols="85" rows="3" class="validateRequired"><%=innerList.get(1) %></textarea>
						</span>
						
						<span style="float: left; height: 15px;"><a title="Add" class="fa fa-plus" onclick="addParticular()" href="javascript:void(0)">&nbsp;</a></span>
					</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strParticularsAmt" id="strParticularsAmt<%=i %>" style="width: 65px !important; text-align: right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" />
					</div>
				</div>
			</div>
		<% i++; srNoCnt++; }
			} %>
			
		</div>
		<%
		while(it.hasNext()) {
			String billingHeadId = it.next();
			List<String> innerList = hmProBillingHeadData.get(billingHeadId);
			if(uF.parseToInt(innerList.get(2)) == IConstants.DT_OPE || uF.parseToInt(innerList.get(2)) == IConstants.DT_OPE_OVERALL) {
		%>
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 6%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 74%;">
					<div style="float: left; margin-left: 10px;">
						<input type="text" style="width: 250px !important;" id="strOtherParticulars" value="<%=innerList.get(1) %>" name="strOtherParticulars" > <!-- class="validateRequired" -->
					</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt<%=i %>" style="width: 65px !important; text-align: right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" /> <!-- class="validateRequired" -->
					</div>
				</div>
			</div>
		<% j++; 
			i++; } %>
			
		<%  } %>
		<% } else { %>
			
			<div id="INRCurrPartiDiv">
				<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
					<div style="float: left; width: 6%;">
						<div style="float: left; margin-left: 10px;"><%=srNoCnt %>.</div>
					</div>
					<div style="float: left; width: 74%;">
						<div style="float: left; margin-left: 10px;">
							<span style="float: left;">
							<!-- <input type="text" style="width: 250px !important;" id="strParticulars" name="strParticulars" class="validateRequired"> -->
							<textarea style="width: 250px !important;" id="strParticulars" name="strParticulars" cols="85" rows="3" class="validateRequired"></textarea>
							</span>
							<span style="float: left; height: 15px;"><a title="Add" class="fa fa-plus" onclick="addParticular()" href="javascript:void(0)">&nbsp;</a></span>
						</div>
					</div>
					<div style="float: left; width: 20%;">
						<div style="float: right; margin-right: 10px;">
							<input type="text" name="strParticularsAmt" id="strParticularsAmt0" style="width: 65px !important; text-align: right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired"/>
						</div>
					</div>
				</div>
			</div>
			
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 6%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 74%;">
					<div style="float: left; margin-left: 10px;">
						<input type="text" style="width: 250px !important;" id="strOtherParticulars" name="strOtherParticulars"> <!-- class="validateRequired" -->
					</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt0" style="width: 65px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> <!-- class="validateRequired" -->
					</div>
				</div>
			</div>
		<% srNoCnt++; } %>
		<input type="hidden" name="srNoCnt" id="srNoCnt" value="<%=srNoCnt %>">
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 15px;">
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
			<div style="float: left; width: 65%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 15%;">
				<div style="float: left; margin-left: 10px;">Subtotal</div>
			</div>
			<div style="float: left; width: 20%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strTotalAmt" id="strTotalAmt" style="width: 65px !important; text-align:right;" readonly="readonly" class="validateRequired" />
				</div>
			</div>
		</div>
				
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 5%; color: white; background-color: #3B9C9C;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 55%; color: white; background-color: #3B9C9C;">
				<div style="float: left; margin-left: 10px; font-weight: bold;">Other Fees/Taxes</div>
			</div>
			<div style="float: left; width: 20%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 20%;">
				<div style="float: right; margin-right: 10px;">&nbsp;</div>
			</div>
		</div>
		
		<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
			Iterator<String> it = hmProTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();	
			List<String> innerList = hmProTaxHeadData.get(taxHeadId);
		%>
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">  <!-- padding: 4px 0px; -->
			<div style="float: left; width: 80%;">
				<div style="float: left; margin-left: 10px;">Add: <%=uF.showData(innerList.get(1),"-") %></div>
			</div>
			<div style="float: left; width: 20%;">
				<div id="taxHeadAmtDiv<%=i %>" style="float: right; margin-right: 10px;"></div>
			</div>
		</div>
		<% i++; } } %>
		
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 60%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 20%;">
				<div style="float: left; margin-left: 10px;">Total (<%=currency %>)</div>
			</div>
			<div style="float: left; width: 20%;">
				<div id="totaldiv" style="float: right; margin-right: 65px;"></div>
			</div>
		</div>
		
		
		<!-- <div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 100%;">
				<div style="float: left; margin-left: 10px;">Amount in text</div>
			</div>
		</div> -->
		
	</div>
</div>
	<!-- ************************************** INRCurrencyTable End *************************************** -->
	
	
	
	<!-- ************************************** Other CurrencyTable Start *************************************** -->
<div id="otherCurrencyTable" style="display: none;">
	<div style="width: 100%; float: left; margin-bottom: 9px;">
		<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
			<div style="float: left; width: 6%;">
				<div style="float: left; font-weight: bold; margin-left: 5px;">#</div>
			</div>
			<div style="float: left; width: 58%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Description</div>
			</div>
			<div style="float: left; width: 18%;">
				<input type="hidden" name="strPartiCountOtherCurr" id="strPartiCountOtherCurr" value="<%=(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) ? hmProBillingHeadData.size() : "1" %>">
				<s:hidden name="strStartDateOtherCurr" id="strStartDateOtherCurr"></s:hidden>
				<s:hidden name="strEndDateOtherCurr" id="strEndDateOtherCurr"></s:hidden>

				<div style="float: right; font-weight: bold; margin-right: 20px;">Amount (<%=currency %>)</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; font-weight: bold; margin-right: 20px;">Amount (<span id="otherCurrSpan"><%=currency %></span>)</div>
			</div>
		</div>
		
		<% 
			int srNoCntOtherCurr = 1;
			if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) {
				Iterator<String> it = hmProBillingHeadData.keySet().iterator();
				int i = 0;
				int j = 0;
			%>
			<div id="OtherCurrPartiDiv">
			<%
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProBillingHeadData.get(billingHeadId);
				
				if(uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE && uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE_OVERALL) { // 
			%>
			
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 6%;">
					<div style="float: left; margin-left: 10px;"><%=srNoCntOtherCurr %>.</div>
				</div>
				<div style="float: left; width: 58%;">
					<div style="float: left; margin-left: 10px;">
						<span style="float: left;">
						<%-- <input type="text" style="width: 250px !important;" id="strParticularsINRCurr" value="<%=innerList.get(1) %>" name="strParticularsINRCurr" class="validateRequired"> --%>
						<textarea style="width: 250px !important;" id="strParticularsINRCurr" name="strParticularsINRCurr" cols="85" rows="3" class="validateRequired"><%=innerList.get(1) %></textarea>
						</span>
						<span style="float: left; height: 15px;"><a title="Add" class="fa fa-plus" onclick="addParticularOtherCurr()" href="javascript:void(0)">&nbsp;</a></span>
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr<%=i %>" style="width: 65px !important; text-align: right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" />
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr<%=i %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
					</div>
				</div>
			</div>
		<% i++; srNoCntOtherCurr++; }
			} %>
		</div>
		
		<%
		while(it.hasNext()) {
			String billingHeadId = it.next();
			List<String> innerList = hmProBillingHeadData.get(billingHeadId);
			if(uF.parseToInt(innerList.get(2)) == IConstants.DT_OPE || uF.parseToInt(innerList.get(2)) == IConstants.DT_OPE_OVERALL) {
		%>
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 6%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 58%;">
					<div style="float: left; margin-left: 10px;">
						<input type="text" style="width: 250px !important;" id="strOtherParticularsINRCurr" value="<%=innerList.get(1) %>" name="strOtherParticularsINRCurr"> <!-- class="validateRequired" -->
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr<%=i %>" style="width: 65px !important; text-align: right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)"/>  <!-- class="validateRequired" -->
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr<%=i %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
					</div>
				</div>
			</div>
		<% j++; 
			i++; } %>
			
		<%  } %>
		<% } else { %>
			
			<div id="OtherCurrPartiDiv">
				<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
					<div style="float: left; width: 6%;">
						<div style="float: left; margin-left: 10px;"><%=srNoCntOtherCurr %>.</div>
					</div>
					<div style="float: left; width: 58%;">
						<div style="float: left; margin-left: 10px;">
							<span style="float: left;">
							<!-- <input type="text" style="width: 250px !important;" id="strParticularsINRCurr" name="strParticularsINRCurr" class="validateRequired"> -->
							<textarea style="width: 250px !important;" id="strParticularsINRCurr" name="strParticularsINRCurr" cols="85" rows="3" class="validateRequired"></textarea>
							</span>
							<span style="float: left; height: 15px;"><a title="Add" class="fa fa-plus" onclick="addParticularOtherCurr()" href="javascript:void(0)">&nbsp;</a></span>
						</div>
					</div>
					<div style="float: left; width: 18%;">
						<div style="float: right; margin-right: 10px;">
							<input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr0" style="width: 65px !important; text-align: right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired"/>
						</div>
					</div>
					<div style="float: left; width: 18%;">
						<div style="float: right; margin-right: 10px;">
							<input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr0" style="width: 65px !important; text-align:right;" readonly="readonly"/>
						</div>
					</div>
				</div>
			</div>
			
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 6%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 58%;">
					<div style="float: left; margin-left: 10px;">
						<input type="text" style="width: 250px !important;" id="strOtherParticularsINRCurr" name="strOtherParticularsINRCurr"> <!-- class="validateRequired" -->
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr0" style="width: 65px !important; text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)"/> <!-- class="validateRequired" -->
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr0" style="width: 65px !important; text-align:right;" readonly="readonly"/>
					</div>
				</div>
			</div>
		<% srNoCntOtherCurr++; } %>
		<input type="hidden" name="srNoCntOtherCurr" id="srNoCntOtherCurr" value="<%=srNoCntOtherCurr %>">
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 15px;">
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
			<div style="float: left; width: 52%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; margin-left: 10px;">Subtotal</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strTotalAmtINRCurr" id="strTotalAmtINRCurr" style="width: 65px !important; text-align:right;" readonly="readonly" class="validateRequired" />
				</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strTotalAmtOtherCurr" id="strTotalAmtOtherCurr" style="width: 65px !important; text-align:right;" readonly="readonly" class="validateRequired" />
				</div>
			</div>
		</div>
		
		
				
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 64%; color: white; background-color: #3B9C9C;">
				<div style="float: left; margin-left: 10px;">Other Fees/Taxes</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">&nbsp;</div>
			</div>
		</div>
		
		<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
			Iterator<String> it1 = hmProTaxHeadData.keySet().iterator();
			int i = 0;
			while(it1.hasNext()) {
			String taxHeadId = it1.next();	
			List<String> innerList = hmProTaxHeadData.get(taxHeadId);
		%>
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">  <!-- padding: 4px 0px; -->
			<div style="float: left; width: 64%;">
				<div style="float: left; margin-left: 10px;">Add: <%=uF.showData(innerList.get(1),"-") %></div>
			</div>
			<div style="float: left; width: 18%;">
				<div id="taxHeadINRAmtDiv<%=i %>" style="float: right; margin-right: 10px;"></div>
			</div>
			<div style="float: left; width: 18%;">
				<div id="taxHeadOtherAmtDiv<%=i %>" style="float: right; margin-right: 10px;"></div>
			</div>
		</div>
		<% i++; } } %>
		
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 52%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; margin-left: 10px;">Total (<%=currency %>)</div>
			</div>
			<div style="float: left; width: 18%;">
				<div id="totalINRAmtdiv" style="float: right; margin-right: 10px;"></div>
			</div>
			<div style="float: left; width: 18%;">
				<div id="totalOtherCurrdiv" style="float: right; margin-right: 10px;"></div>
			</div>
		</div>
		
	</div>
</div>
	<!-- ************************************** Other CurrencyTable End *************************************** -->
	
	<div style="width: 100%; float: left; color: #404040; margin-bottom: 15px;">
		<div style="float: left; width: 100%;">
			<div style="float: left; margin-left: 10px;">Payment Mode:</div>
		</div>
		<% if((String)request.getAttribute("bankName") != null && !request.getAttribute("bankName").equals("")) { %>
			<div style="float: left; width: 100%;">
				<div style="float: left; margin-left: 20px; line-height: 15px;">
				<b>Bank Details:</b><br/>
				A/C No.: <%=hmBankAccData.get("ACC_NO") %><br/>
				Branch: <%=hmBankAccData.get("BRANCH_NAME") %><br/>
				Bank: <%=hmBankAccData.get("BANK_NAME") %><br/>
				<% if(hmBankAccData.get("IFSC_CODE") != null && !hmBankAccData.get("IFSC_CODE").equals("")) { %>
					IFSC: <%=hmBankAccData.get("IFSC_CODE") %><br/>
				<% } %>
				<% if(hmBankAccData.get("SWIFT_CODE") != null && !hmBankAccData.get("SWIFT_CODE").equals("")) { %>
					SWIFT: <%=hmBankAccData.get("SWIFT_CODE") %><br/>
				<% } %>
				<% if(hmBankAccData.get("CLEARING_CODE") != null && !hmBankAccData.get("CLEARING_CODE").equals("")) { %>
					BCC: <%=hmBankAccData.get("CLEARING_CODE") %><br/>
				<% } %>
				<% if(hmBankAccData.get("OTHER_INFO") != null && !hmBankAccData.get("OTHER_INFO").equals("")) { %>
					<%=hmBankAccData.get("OTHER_INFO") %><br/>
				<% } %>
				</div>
			</div>
			<%-- <div style="float: left; width: 100%;">
				<div style="float: left; margin-left: 20px;">Bank: <%=hmClientDetails.get("BANK_NAME") %></div>
			</div> --%>
		<% } %>
		<% if((String)request.getAttribute("payPalMailId") != null && !request.getAttribute("payPalMailId").equals("")) { %>
			<div style="float: left; width: 100%;">
				<div style="float: left; margin-left: 20px;">Paypal: <%=(String)request.getAttribute("payPalMailId") %></div>
			</div>
		<% } %>
	</div>


	<div style="width: 100%; float: left; margin-bottom: 15px;">
		<div style="float: left; width: 100%;">
			<div style="float: left; margin-left: 10px;">
				<textarea name="otherDescription" id="otherDescription" cols="85" rows="3"></textarea>
				<%-- <s:textarea name="otherDescription" id="otherDescription" rows="3" cols="95"/> --%>
			</div>
		</div>
	</div>
	
	
	<div style="width: 100%; float: left; color: #404040; margin-bottom: 15px;">
		<div style="float: left; width: 100%;">
			<div style="float: left; margin-left: 10px; font-weight: bold;">This is computer generated statement and does not need signature.</div>
		</div>
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 29px;">
		<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
			<div style="float: left; width: 30%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Phone No.</div>
			</div>
			<div style="float: left; width: 35%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">E-mail</div>
			</div>
			<div style="float: left; width: 35%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Web-site</div>
			</div>
		</div>
		
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 30%;">
				<div style="float: left; margin-left: 10px;"><%=hmOrgData.get("ORG_CONTACT") %></div>
			</div>
			<div style="float: left; width: 35%;">
				<div style="float: left; margin-left: 10px;"><%=hmOrgData.get("ORG_EMAIL") %></div>
			</div>
			<div style="float: left; width: 35%;">
				<div style="float: left; margin-left: 10px;"><%=hmOrgData.get("ORG_WEBSITE") %></div>
			</div>
		</div>
	</div>
	
	
	<div style="float: left; width: 100%; text-align: center;">
	
		<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it1 = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it1.hasNext()) {
				String taxHeadId = it1.next();	
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
				<input type="hidden" name="taxHead" id="taxHead<%=i %>" value="<%=innerList.get(3) %>"/>
				<input type="hidden" name="taxNameLabel" id="taxNameLabel<%=i %>" value="<%=innerList.get(1) %>"/>
				<input type="hidden" name="taxHeadPercent" id="taxHeadPercent<%=i %>" value="<%=innerList.get(2) %>"/>
				<input type="hidden" name="taxHeadAmt" id="taxHeadAmt<%=i %>"/>
			<% i++; } } %>
        	<input type="hidden" name="particularTotalAmt" id="particularTotalAmt"/>
        	<input type="hidden" name="totalAmt" id="totalAmt"/>
        	
        	<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it1 = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it1.hasNext()) {
				String taxHeadId = it1.next();	
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
				<input type="hidden" name="taxHeadOtherCurr" id="taxHeadOtherCurr<%=i %>" value="<%=innerList.get(3) %>"/>
				<input type="hidden" name="taxNameLabelOtherCurr" id="taxNameLabelOtherCurr<%=i %>" value="<%=innerList.get(1) %>"/>
				<input type="hidden" name="taxHeadPercentOtherCurr" id="taxHeadPercentOtherCurr<%=i %>" value="<%=innerList.get(2) %>"/>
				<input type="hidden" name="taxHeadAmtINRCurr" id="taxHeadAmtINRCurr<%=i %>"/>
				<input type="hidden" name="taxHeadAmtOtherCurr" id="taxHeadAmtOtherCurr<%=i %>"/>
			<% i++; } } %>
        	<input type="hidden" name="particularTotalAmtINRCurr" id="particularTotalAmtINRCurr"/>
        	<input type="hidden" name="particularTotalAmtOtherCurr" id="particularTotalAmtOtherCurr"/>
        	<input type="hidden" name="totalAmtINRCurr" id="totalAmtINRCurr"/>
        	<input type="hidden" name="totalAmtOtherCurr" id="totalAmtOtherCurr"/>
        	
        	<s:submit value="Submit" cssClass="btn btn-primary" id="btnOk" name="submit" />
	</div>
	
	
	<div style="width: 100%; float: left; color: #404040; margin-bottom: 9px;">
		<div style="float: left; width: 100%; text-align: right;">
			<div style="float: right; margin-right: 10px; margin-left: -5px;"><img style="width: 90px;" src="images1/icons/icons/taskrig.png"></div>
			<div style="float: right; margin-top: 10px;">Powered by </div> 
		</div>
	</div>

</s:form>

<script type="text/javascript">
$(function() {
		calAmt();
	 });
</script>


	<% } else if(billingType != null && uF.parseToInt(billingType) == 2 && (uF.parseToInt(strInvoiceTemplate) == 1 || uF.parseToInt(strInvoiceTemplate) == 2)) { %>

<!-- **************************************** ProRata ******************************************* -->	
	<%
		String proCurr = (String)request.getAttribute("proCurrency");
		Map<String, Map<String, String>> hmCurrencyDetails = (Map<String, Map<String, String>>)request.getAttribute("hmCurrencyDetails");
		Map<String, String> hmCurr = hmCurrencyDetails.get(proCurr);
		if(hmCurr==null) hmCurr = new HashMap<String, String>();
		
		String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("")? hmCurr.get("SHORT_CURR") : "";
		
		Map<String, List<String>> hmProRataProBillingHeadData = (Map<String, List<String>>)request.getAttribute("hmProRataProBillingHeadData");
		
		Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmProTaxHeadData");
		
		Map<String, String> hmOrgData = (Map<String, String>) request.getAttribute("hmOrgData");
		if(hmOrgData == null) hmOrgData = new HashMap<String, String>();
		
		Map<String,String> hmClientDetails = (Map<String,String>) request.getAttribute("hmClientDetails");
		if(hmClientDetails == null) hmClientDetails = new HashMap<String, String>();
		
		Map<String, String> hmBankAccData = (Map<String, String>) request.getAttribute("hmBankAccData");
	%>


<script>
$(function() {
	// binds form submission and fields to the validation engine 
	$("#btnOk").click(function(){
		$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
		//$(".validateRequired").prop('required',true);
	});
	
	/* var strProjectOwner = document.getElementById("strProjectOwner").value; */
	var strProjectOwner = <%=request.getAttribute("strProjectOwner") %>;
	getProjectOwnerDetails(strProjectOwner);
	
	var proCurrType = document.getElementById("strCurrency").value;
	getExchangeValue(proCurrType);
});


	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
	      return false;
	   }
	   return true;
	}



	function calEmpAmtOtherCurr() {
		var cntProrataAddOCurr = document.getElementById("strResourceCountOtherCurrProRata").value;
		//alert("cntProrataAddOCurr ===>> " + cntProrataAddOCurr);
		//alert("cntProrataAddOCurr ===>> " + cntProrataAddOCurr);
		var exchangeValue = 0;
		if(document.getElementById("exchangeValue")) {
			exchangeValue = document.getElementById("exchangeValue").value;
		}
		//alert("exchangeValue ===>> " + exchangeValue);
		
		for(var i=0; i<=parseInt(cntProrataAddOCurr); i++) {
			var billDailyHours = document.getElementById("billDailyHoursINRCurr"+i);
			var billDaysHours = document.getElementById("billCalType");
			var empRate = document.getElementById("empRateINRCurr"+i);
			//alert("billDailyHours ===>> " + billDailyHours+ "  empRate ===>> " + empRate);
			if(billDailyHours && empRate) { // && billDaysHours
				var billHrs = 0;
				var billHrsSixty = 0;
				var empRt = 0;
				
				var btype = billDaysHours.value;
				
				if(billDailyHours.value != '') {
					billHrs = billDailyHours.value;
				}
				if(empRate.value != '') {
					empRt = empRate.value;
				}
				
				if(btype == '2') {
					var cnt = 0;
					if(billHrs !=null && billHrs.indexOf(".") > 0) {
						var strHour = billHrs.substring(0, billHrs.indexOf("."));
						var strMinute = billHrs.substring(billHrs.indexOf(".")+1);
						
						var dblTime = 0;
						if (strMinute != null && strMinute.trim() != "") {
							strMinute = "."+strMinute;
							dblTime = parseFloat(strMinute);
						}
						if(dblTime < 0.60) {
							var dbl100Min = (parseFloat(dblTime) * 100) /60;
							dblTime = dbl100Min;
							billHrs = parseFloat(strHour)+parseFloat(dblTime);
						} else {
							billHrs = parseFloat(strHour)+parseFloat(dblTime);
							cnt = 1;
						}
						if(cnt == 1) {
							var dbl60Min = (dblTime * 60) /100;
							billHrsSixty = parseFloat(strHour)+parseFloat(dbl60Min);
							document.getElementById("billDailyHoursINRCurr"+i).value = parseFloat(billHrsSixty).toFixed(2);
						}
					}
				}
				
					var otherCurrAmt = 0;
					var amt = parseFloat(billHrs) * parseFloat(empRt);
					if(parseFloat(exchangeValue) > 0) {
						otherCurrAmt = parseFloat(amt) / parseFloat(exchangeValue);
					}
					amt = Math.round(parseFloat(amt));
					
					document.getElementById("strEmpAmtINRCurr"+i).value = amt.toFixed(2);
					document.getElementById("strEmpAmtOtherCurr"+i).value = otherCurrAmt.toFixed(2);
	//				alert("otherCurrAmt ===>> " + otherCurrAmt);
					calProrataAmtOtherCurr();
			}
		}
	}


	function calProrataAmtOtherCurr() {
		var exchangeValue = 0;
		if(document.getElementById("exchangeValue")) {
			exchangeValue = document.getElementById("exchangeValue").value;
		}
		
		var cntProrataAddOCurr = document.getElementById("strResourceCountOtherCurrProRata").value;
		var cntOtherAdd = document.getElementById("strPartiCountOtherCurrProrata").value;
		
		var amt=0;
		var otherCurrAmt = 0;
		
		var totalAmt=0;
		var otherCurrTotalAmt = 0;
		var otherCurrVal = 0;
		var otherCurrOtherPAmt = 0;
		
		var taxamt = 0;
		var otherCurrTaxamt = 0;
		
		for(var i=0;i<=cntOtherAdd;i++) {
			var id = document.getElementById("strOtherParticularsAmtINRCurrProrata"+i);
			if(id) {
				if(id.value != '') {
					if(parseFloat(exchangeValue) > 0) {
						otherCurrVal = parseFloat(id.value) / parseFloat(exchangeValue);
					}
					document.getElementById("strOtherParticularsAmtOtherCurrProrata"+i).value = parseFloat(otherCurrVal).toFixed(2);
					amt = parseFloat(amt) + parseFloat(id.value);
					otherCurrAmt = parseFloat(otherCurrAmt) + parseFloat(otherCurrVal);
				} else {
					document.getElementById("strOtherParticularsAmtOtherCurrProrata"+i).value = '0.00';
				}
			}
		}
		
		for(var i=0;i<=cntProrataAddOCurr;i++) {
			var strEmpAmt = document.getElementById("strEmpAmtINRCurr"+i);
			if(strEmpAmt) {
				if(strEmpAmt.value != '') {
					if(parseFloat(exchangeValue) > 0) {
						otherCurrVal = parseFloat(strEmpAmt.value) / parseFloat(exchangeValue);
					}
					amt = parseFloat(amt) + parseFloat(strEmpAmt.value);
					otherCurrAmt = parseFloat(otherCurrAmt) + parseFloat(otherCurrVal);
				}
			}
		}
		
		document.getElementById("strTotalAmtINRCurrProrata").value = parseFloat(amt).toFixed(2);
		document.getElementById("strTotalAmtOtherCurrProrata").value = parseFloat(otherCurrAmt).toFixed(2);
		
		totalAmt = parseFloat(amt);
		otherCurrTotalAmt = parseFloat(otherCurrAmt);
		
		<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
			Iterator<String> it = hmProTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();	
			List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
			var taxPercent = <%=uF.showData(innerList.get(2), "0") %>;
			var cnt = <%=uF.showData(i+"", "0") %>;
			taxamt=(parseFloat(amt) * parseFloat(taxPercent)) / 100;
			
			if(parseFloat(exchangeValue) > 0) {
				otherCurrTaxamt = parseFloat(taxamt) / parseFloat(exchangeValue);
			}
			totalAmt = parseFloat(totalAmt) + parseFloat(taxamt);
			otherCurrTotalAmt = parseFloat(otherCurrTotalAmt) + parseFloat(otherCurrTaxamt);
			
			document.getElementById("taxHeadINRAmtDivProrata"+cnt).innerHTML = parseFloat(taxamt).toFixed(2);
			document.getElementById("taxHeadOtherAmtDivProrata"+cnt).innerHTML = parseFloat(otherCurrTaxamt).toFixed(2);
	
			document.getElementById("taxHeadAmtINRCurr"+cnt).value = parseFloat(taxamt).toFixed(2);
			document.getElementById("taxHeadAmtOtherCurr"+cnt).value = parseFloat(otherCurrTaxamt).toFixed(2);
			<% i++; } } %>
	
		document.getElementById("totalINRAmtdivProrata").innerHTML = parseFloat(totalAmt).toFixed(2);
		document.getElementById("totalOtherCurrdivProrata").innerHTML = parseFloat(otherCurrTotalAmt).toFixed(2);
		
		document.getElementById("particularTotalAmtINRCurr").value = parseFloat(amt).toFixed(2);
		document.getElementById("totalAmtINRCurr").value = parseFloat(totalAmt).toFixed(2);
		
		document.getElementById("particularTotalAmtOtherCurr").value = parseFloat(otherCurrAmt).toFixed(2);
		document.getElementById("totalAmtOtherCurr").value = parseFloat(otherCurrTotalAmt).toFixed(2);
	}

	
	function calEmpAmt() {
		
		var cntProrataAdd = document.getElementById("strResourceCountProRata").value;
		//alert("cntProrataAdd ===>> " + cntProrataAdd);
		for(var i=0; i<=parseInt(cntProrataAdd); i++) {
			var billDailyHours = document.getElementById("billDailyHours"+i);
			var billDaysHours = document.getElementById("billCalType");
			//alert("billDaysHours ===>> " + billDaysHours.value);
			var empRate = document.getElementById("empRate"+i);
			if(billDailyHours && empRate) { //&& billDaysHours
				var billHrs = 0;
				var billHrsSixty = 0;
				var empRt = 0;
				var btype = billDaysHours.value;
				//alert("btype 0 ===>> " + btype + " --- billDaysHours.value ===>> " + billDaysHours.value);
				if(billDailyHours.value != '') {
					billHrs = billDailyHours.value;
				}
				if(empRate.value != '') {
					empRt = empRate.value;
				}
				
				//alert("btype ===>> " + btype);
				if(btype == '2') {
					var cnt=0;
					if(billHrs !=null && billHrs.indexOf(".") > 0) {
						var strHour = billHrs.substring(0, billHrs.indexOf("."));
						var strMinute = billHrs.substring(billHrs.indexOf(".")+1);
						
						var dblTime = 0;
						if (strMinute != null && strMinute.trim() != "") {
							strMinute = "."+strMinute;
							dblTime = parseFloat(strMinute);
						}
						if(dblTime < 0.60) {
							var dbl100Min = (parseFloat(dblTime) * 100) /60;
							dblTime = dbl100Min;
							billHrs = parseFloat(strHour)+parseFloat(dblTime);
						} else {
							billHrs = parseFloat(strHour)+parseFloat(dblTime);
							cnt = 1;
						}
						
						if(cnt == 1) {
							var dbl60Min = (dblTime * 60) /100;
							billHrsSixty = parseFloat(strHour)+parseFloat(dbl60Min);
							document.getElementById("billDailyHours"+i).value = parseFloat(billHrsSixty).toFixed(2);
						}
					}
				}
					var amt = parseFloat(billHrs) * parseFloat(empRt);
					amt = Math.round(parseFloat(amt));
					
					document.getElementById("strEmpAmt"+i).value = amt.toFixed(2);
					calProrataAmt();
			}
		}
	}
	
	
	function calProrataAmt() {
		var cntProrataAdd = document.getElementById("strResourceCountProRata").value;
		var cntAdd = document.getElementById("strPartiCountProRata").value;
		var amt=0;
		var taxamt = 0;
		var totalAmt=0;
		
		for(var i=0; i<=cntAdd; i++) {
			var id = document.getElementById("strOtherParticularsAmtProrata"+i);
			if(id) {
				if(id.value != '') {
					amt = parseFloat(amt) +parseFloat(id.value);
				}
			}
		}
		
		for(var i=0;i<=cntProrataAdd;i++) {
			var strEmpAmt=document.getElementById("strEmpAmt"+i);
			if(strEmpAmt) {
				if(strEmpAmt.value != '') {
					amt=parseFloat(amt) +parseFloat(strEmpAmt.value);
				}
			}
		}
		
		document.getElementById("strTotalAmtProrata").value = parseFloat(amt).toFixed(2);
		
		totalAmt = parseFloat(amt);
		
		<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
			Iterator<String> it = hmProTaxHeadData.keySet().iterator();
			int i = 0;
			while(it.hasNext()) {
			String taxHeadId = it.next();	
			List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
			var taxPercent = <%=uF.showData(innerList.get(2), "0") %>;
			var cnt = <%=uF.showData(i+"", "0") %>;
			taxamt=(parseFloat(amt) * parseFloat(taxPercent)) / 100;
			
			totalAmt = parseFloat(totalAmt) + parseFloat(taxamt);
			
			document.getElementById("taxHeadAmtDivProrata"+cnt).innerHTML = parseFloat(taxamt).toFixed(2);

			document.getElementById("taxHeadAmt"+cnt).value = parseFloat(taxamt).toFixed(2);
			<% i++; } } %>

			document.getElementById("totaldivProrata").innerHTML = parseFloat(totalAmt).toFixed(2);
			document.getElementById("particularTotalAmt").value = parseFloat(amt).toFixed(2);
			document.getElementById("totalAmt").value = parseFloat(totalAmt).toFixed(2);
	} 
	
	
	function getExchangeValue(value) {
		
		 var proCurrency = document.getElementById("proCurrency").value;
			
		 if(value == proCurrency || value == '' || value == '0') {
			 //getContent('exchangeValDIV', '');
			 document.getElementById("invoiceType").value = '1';
			 document.getElementById("exchangeValDIV").innerHTML = "";
		 } else {
			 document.getElementById("invoiceType").value = '2';
			 getContent('exchangeValDIV', 'ViewAndUpdateExchangeValue.action?currencyType='+value+'&proCurrency='+proCurrency+'&type=V');
		 }
		 window.setTimeout(function() {
			 changeCurrencywiseData(value, proCurrency);
			}, 1000);
	}
	
	
	function updateExchangeValue() {
		var currencyType = document.getElementById("currencyType").value;
		var proCurrency = document.getElementById("proCurrency").value;
		var exchangeValue = document.getElementById("exchangeValue").value;
	 	getContent('exchangeValDIV', 'ViewAndUpdateExchangeValue.action?currencyType='+currencyType+'&proCurrency='+proCurrency+'&exchangeValue='+exchangeValue+'&type=U');
	}


	function checkInvoiceCode() {
		//alert("strInvoiceCode ===>> " + strInvoiceCode);
		var strInvoiceCode = document.getElementById("invoiceCode").value;
		var strProId = document.getElementById("pro_id").value;
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {

			var xhr = $.ajax({
				url : "CheckInvoiceCodeExist.action?strInvoiceCode="+strInvoiceCode+"&strProId="+strProId,
				cache : false,
				success : function(data) {
					var res = data.split("::::");
					//alert("data ===>> " + data+" res ==>> " + res.length);
					if(parseFloat(res[0]) == 1) {
						alert("This invoice code exists, please try with another invoice code.");
						document.getElementById("invoiceCode").value=res[1];
						return false;
					} else {
						return true;
					}
				}
			});
		}
	}
	
	
	function changeCurrencywiseData(value, proCurrency) {
		 //getContent('INRCurrencyDIV', 'GetINRCurrencyValue.action?currencyType='+currencyType+'&exchangeValue='+exchangeValue+'&type=U');
		 if(value == proCurrency || value == '' || value == '0') {
			 document.getElementById("INRCurrencyTable").style.display = "block";
			 document.getElementById("otherCurrencyTable").style.display = "none";
			 //INRCurrencyTable otherCurrencyTable
			 calAmt();
		 } else {
			 document.getElementById("otherCurrencyTable").style.display = "block";
			 if(document.getElementById("longCurrency")) {
				 var longCurrency = document.getElementById("longCurrency").value;
				 document.getElementById("otherCurrSpan").innerHTML = longCurrency;
			 }
			 document.getElementById("INRCurrencyTable").style.display = "none";
			 calAmtOtherCurr();
		 }
	 }
	
	
	function addParticularProrata() {
		
		var cntAdd = document.getElementById("strResourceCountProRata").value;
		var srNoCnt = document.getElementById("srNoCnt").value;
		cntAdd++;
	    var divTag = document.createElement("div");
	    divTag.id = "p_add"+cntAdd;
	    divTag.setAttribute("style","float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;");
	    divTag.innerHTML = "<div style=\"float: left; width: 6%;\"><div style=\"float: left; margin-left: 10px;\">"+srNoCnt+".</div></div>"+
	    	"<div style=\"float: left; width: 44%;\"><div style=\"float: left; margin-left: 10px;\"><span style=\"float: left;\">"+
	    	"<input type=\"text\" id=\"strEmp"+cntAdd+"\" name=\"strEmp\" class=\"validateRequired\"></span>"+
	    	"<span style=\"float: left; height: 15px;\"><a title=\"Add\" class=\"fa fa-plus\" onclick=\"addParticularProrata()\" href=\"javascript:void(0)\">&nbsp;</a>"+
	    	"<a href=\"javascript:void(0)\" onclick=\"removeParticularProrata('p_add"+cntAdd+"')\" class=\"fa fa-remove\">&nbsp;</a></span>"+
	    	"</div></div>"+
	    	"<div style=\"float: left; width: 15%;\"><div style=\"float: left; margin-left: 10px;\">"+
	    	"<input type=\"text\" name=\"billDailyHours\" id=\"billDailyHours"+cntAdd+"\" style=\"width: 50px !important;\" onkeyup=\"calEmpAmt();\"  onkeypress=\"return isNumberKey(event)\"/>"+
	    	"</div></div>"+
	    	"<div style=\"float: left; width: 15%;\"><div style=\"float: left; margin-left: 10px;\">"+
	    	"<input type=\"text\" name=\"empRate\" id=\"empRate"+cntAdd+"\" style=\"width: 50px !important;\" onkeyup=\"calEmpAmt();\" onkeypress=\"return isNumberKey(event)\"/>"+
	    	"</div></div>"+
	    	"<div style=\"float: left; width: 20%;\"><div style=\"float: right; margin-right: 10px;\">"+
	    	"<input type=\"text\" name=\"strEmpAmt\" id=\"strEmpAmt"+cntAdd+"\" style=\"width: 65px !important; text-align: right;\"/>"+
	    	"</div></div>";

	    document.getElementById("INRCurrPartiDiv").appendChild(divTag);
	    srNoCnt++;
	    document.getElementById("srNoCnt").value = srNoCnt;
	    document.getElementById("strResourceCountProRata").value = cntAdd;
	}
	
	function removeParticularProrata(id) {
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
		}
		calEmpAmt();
	}
	
	function addParticularProrataOtherCurr() {
		
		var cntAdd = document.getElementById("strResourceCountOtherCurrProRata").value;
		var srNoCnt = document.getElementById("srNoCntOtherCurr").value;
		cntAdd++;
	    var divTag = document.createElement("div");
	    divTag.id = "p_addOtherCurr"+cntAdd;
	    divTag.setAttribute("style","float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;");
	    divTag.innerHTML = "<div style=\"float: left; width: 4%;\"><div style=\"float: left; margin-left: 10px;\">"+srNoCnt+".</div></div>"+
    	"<div style=\"float: left; width: 36%;\"><div style=\"float: left;\"><span style=\"float: left;\">"+
    	"<input type=\"text\" id=\"strEmpOtherCurr"+cntAdd+"\" name=\"strEmpOtherCurr\" class=\"validateRequired\"></span>"+
    	"<span style=\"float: left; height: 15px;\"><a title=\"Add\" class=\"fa fa-plus\" onclick=\"addParticularProrataOtherCurr()\" href=\"javascript:void(0)\">&nbsp;</a>"+
    	"<a href=\"javascript:void(0)\" onclick=\"removeParticularProrataOtherCurr('p_addOtherCurr"+cntAdd+"')\" class=\"fa fa-remove\">&nbsp;</a></span>"+
    	"</div></div>"+
    	"<div style=\"float: left; width: 12%;\"><div style=\"float: left; margin-left: 10px;\">"+
    	"<input type=\"text\" name=\"billDailyHoursINRCurr\" id=\"billDailyHoursINRCurr"+cntAdd+"\" style=\"width: 50px !important;\" onkeyup=\"calEmpAmtOtherCurr();\"  onkeypress=\"return isNumberKey(event)\"/>"+
    	"</div></div>"+
    	"<div style=\"float: left; width: 12%;\"><div style=\"float: left; margin-left: 10px;\">"+
    	"<input type=\"text\" name=\"empRateINRCurr\" id=\"empRateINRCurr"+cntAdd+"\" style=\"width: 50px !important;\" onkeyup=\"calEmpAmtOtherCurr();\" onkeypress=\"return isNumberKey(event)\"/>"+
    	"</div></div>"+
    	"<div style=\"float: left; width: 18%;\"><div style=\"float: right; margin-right: 10px;\">"+
    	"<input type=\"text\" name=\"strEmpAmtINRCurr\" id=\"strEmpAmtINRCurr"+cntAdd+"\" style=\"width: 65px !important; text-align: right;\"/>"+
    	"</div></div>"+
    	"<div style=\"float: left; width: 18%;\"><div style=\"float: right; margin-right: 10px;\">"+
    	"<input type=\"text\" name=\"strEmpAmtOtherCurr\" id=\"strEmpAmtOtherCurr"+cntAdd+"\" style=\"width: 65px !important; text-align: right;\"/>"+
    	"</div></div>";
    	
	    document.getElementById("OtherCurrPartiDiv").appendChild(divTag);
	    srNoCnt++;
	    document.getElementById("srNoCntOtherCurr").value = srNoCnt;
	    document.getElementById("strResourceCountOtherCurrProRata").value = cntAdd;
	}
	
	
	function removeParticularProrataOtherCurr(id) {
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
		}
		calEmpAmtOtherCurr();
	}
	
</script>


<s:form theme="simple" action="ProjectAdHocInvoiceFormat_1" name="frm" id="formID" cssClass="formcss" method="post">
	<s:hidden name="pro_id" id="pro_id"></s:hidden>
	<s:hidden name="pro_freq_id"></s:hidden>
	<s:hidden name="billingType"></s:hidden>
	<s:hidden name="billCalType"></s:hidden>
	<s:hidden name="strInvoiceTemplate"></s:hidden>
	<s:hidden name="service" id="service"></s:hidden>
	<s:hidden name="client" id="client"></s:hidden>
	<s:hidden name="clientPoc" id="clientPoc"></s:hidden>
	<s:hidden name="strProjectOwner" id="strProjectOwner"></s:hidden>
	<s:hidden name="bankName" id="bankName"></s:hidden>
	<s:hidden name="payPalMailId" id="payPalMailId"></s:hidden>
	<s:hidden name="strAccountRef" id="strAccountRef"></s:hidden>
	<s:hidden name="strPONo" id="strPONo"></s:hidden>
	<s:hidden name="strTerms" id="strTerms"></s:hidden>
	<s:hidden name="strDueDate" id="strDueDate"></s:hidden>
	<s:hidden name="proType" id="proType"></s:hidden>
	<input type="hidden" name="invoiceType" id="invoiceType" value="1"/>

	<div style="width: 100%; float: left; margin-bottom: 25px;">
		<div style="float: left; width: 30%;">
			<img src='<%=CF.getStrDocRetriveLocation()+IConstants.I_ORGANISATION+"/"+IConstants.I_IMAGE+"/"+hmOrgData.get("ORG_LOGO") %>' height="40">&nbsp;
		</div> 
		<div style="float: left; text-align: center; width: 30%; padding: 0px 55px;">
			<div style="float: left; text-align: center; width: 100%; font-weight: bold; font-size: 14px;"><%=hmOrgData.get("ORG_NAME") %></div>
			<div style="float: left; text-align: center; width: 100%;"><%=hmOrgData.get("ORG_ADDRESS") %></div>
		</div>
		<div style="float: right; width: 25%; text-align: center; font-size: 26px; font-weight: bold;">
			Invoice
		</div>
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 10px;">
		<div style="float: left; width: 30%;">
			<div style="float: left; text-align: center; font-weight: bold; width: 100%; color: white; background-color: #3B9C9C;">Invoice To</div>
			<div style="float: left; color: #404040; background-color: #EFEFEF; width: 100%; padding-bottom: 7px;">
				<div style="margin-left: 20px;">
					<%=hmClientDetails.get("CLIENT_SPOC") %>
					<br/>
					<%=hmClientDetails.get("CLIENT_ADDRESS") %>
				</div>
			</div>
		</div>
		
		<div style="float: left; text-align: center; font-weight: bold; width: 25%; padding: 0px 30px;">
			&nbsp;
		</div>
		
		<div style="float: right; width: 36%; text-align: center;">
			<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
				<div style="float: left; width: 35%; text-align: center; font-weight: bold;">Date</div>
				<div style="float: left; width: 65%; font-weight: bold; text-align: center;">Invoice No.</div>
			</div>
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding-bottom: 7px;">
				<div style="float: left; width: 35%; text-align: center;">
					<s:textfield name="invoiceGenDate" id="invoiceGenDate" cssClass="validateRequired" cssStyle="width: 80px !important; margin-top: 5px;" readonly="true"/>
				</div>
				<div style="float: left; width: 65%; text-align: center;">
					<s:textfield name="invoiceCode" id="invoiceCode" cssClass="validateRequired" cssStyle="width: 150px !important; margin-top: 5px;" onkeyup="checkInvoiceCode();"/>
					<%-- <s:hidden name="invoiceCode" id="invoiceCode"></s:hidden>
					<%=(String)request.getAttribute("invoiceCode") %> --%>
				</div>
			</div>
		</div>
	</div>
	
	<div style="width: 100%; float: left; margin-bottom: 15px; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
		<div style="float: left; margin-left: 7px;">
        	<s:hidden name="proCurrency" id="proCurrency"/>
	        	<s:select id="strCurrency" cssClass="validateRequired" name="strCurrency" listKey="currencyId" listValue="currencyName" headerKey="" 
	        		headerValue="Select Currency" list="currencyList" key="" required="true" cssStyle="width: 200px !important;" onchange="getExchangeValue(this.value);"/>
        	</div>
        	
		<div id="exchangeValDIV" style="float: left; margin-left: 10px; margin-top: -10px;"> </div>
    </div>	
	
	
	<% if(uF.parseToInt(strInvoiceTemplate) == 1) { %>
		<div style="width: 100%; float: left; margin-bottom: 15px;">
			<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Account Ref.</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">P.O. No.</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Terms</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Due Date</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Project Name</div>
				</div>
			</div>
			
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding-bottom: 7px;">
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=uF.showData((String)request.getAttribute("strAccountRef"), "-") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=uF.showData((String)request.getAttribute("strPONo"), "-") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=uF.showData((String)request.getAttribute("strTerms"), "-") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=(request.getAttribute("strDueDate")!= null && !request.getAttribute("strDueDate").equals("")) ? uF.showData(uF.getDateFormat((String)request.getAttribute("strDueDate"), IConstants.DATE_FORMAT, CF.getStrReportDateFormat()), "") : "-" %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=uF.showData("", "-") %></div>
				</div>
			</div>
		</div>
	<% } %>
	
	
	<!-- ************************************** INRCurrencyProRataTable Start *************************************** -->
	<div id="INRCurrencyTable" style="display: block;">
		<div style="width: 100%; float: left; margin-bottom: 9px;">
			<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
				<div style="float: left; width: 6%;">
					<div style="float: left; font-weight: bold; margin-left: 5px;">#</div>
				</div>
				<div style="float: left; width: 44%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Description</div>
				</div>
				<div style="float: left; width: 15%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Qty/<%=(String)request.getAttribute("billCalTypeLbl") %></div>
				</div>
				<div style="float: left; width: 15%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Rate (<%=currency %>)</div>
				</div>
				<div style="float: left; width: 20%;">
					<input type="hidden" name="strResourceCountProRata" id="strResourceCountProRata" value="0">
					<input type="hidden" name="strPartiCountProRata" id="strPartiCountProRata" value="<%=(hmProRataProBillingHeadData != null && !hmProRataProBillingHeadData.isEmpty()) ? hmProRataProBillingHeadData.size() : "1" %>">
					<s:hidden name="strStartDate" id="strStartDate"></s:hidden>
					<s:hidden name="strEndDate" id="strEndDate"></s:hidden>
					
					<div style="float: right; font-weight: bold; margin-right: 20px;">Amount (<%=currency %>)</div>
				</div>
			</div>
			
			<% 
				int srNoCnt = 1;
			%>
				
				<div id="INRCurrPartiDiv">
				
					<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
						<div style="float: left; width: 6%;">
							<div style="float: left; margin-left: 10px;"><%=srNoCnt %>.</div>
						</div>
						<div style="float: left; width: 44%;">
							<div style="float: left; margin-left: 10px;">
								<span style="float: left;"><input type="text" name="strEmp" id="strEmp0" class="validateRequired"/></span>
								<span style="float: left; height: 15px;"><a title="Add" class="fa fa-plus" onclick="addParticularProrata()" href="javascript:void(0)">&nbsp;</a></span>
							</div>
						</div>
						<div style="float: left; width: 15%;">
							<div style="float: left; margin-left: 10px;">
								<input type="text" name="billDailyHours" id="billDailyHours0" style="width: 50px !important;" onkeyup="calEmpAmt();"  onkeypress="return isNumberKey(event)"/>
							</div>
						</div>
						<div style="float: left; width: 15%;">
							<div style="float: left; margin-left: 10px;">
								<input type="text" name="empRate" id="empRate0" style="width: 50px !important;" onkeyup="calEmpAmt();" onkeypress="return isNumberKey(event)"/>
							</div>
						</div>
						<div style="float: left; width: 20%;">
							<div style="float: right; margin-right: 10px;">
								<input type="text" name="strEmpAmt" id="strEmpAmt0" style="width: 65px !important; text-align:right;" readonly="readonly"/>
							</div>
						</div>
					</div>
					<% srNoCnt++; %>
				</div>
				
				<% 
				if(hmProRataProBillingHeadData != null && !hmProRataProBillingHeadData.isEmpty()) { 
					Iterator<String> it = hmProRataProBillingHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String billingHeadId = it.next();	
					List<String> innerList = hmProRataProBillingHeadData.get(billingHeadId);
				%> 
				
				<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
					<div style="float: left; width: 6%;">
						<div style="float: left; margin-left: 10px;">&nbsp;</div>
					</div>
					<div style="float: left; width: 74%;">
						<div style="float: left; margin-left: 10px;">
							<input type="text" style="width: 250px !important;" id="strOtherParticularsProrata" value="<%=innerList.get(1) %>" name="strOtherParticularsProrata"> <!-- class="validateRequired" -->
						</div>
					</div>
					<div style="float: left; width: 20%;">
						<div style="float: right; margin-right: 10px;">
							<input type="text" name="strOtherParticularsAmtProrata" id="strOtherParticularsAmtProrata<%=i %>" style="width: 65px !important; text-align:right;" onkeyup="calProrataAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" />
						</div>
					</div>
				</div>
				
				<% i++; 
					} %>
				<% } else { %>
				<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
					<div style="float: left; width: 6%;">
						<div style="float: left; margin-left: 10px;">&nbsp;</div>
					</div>
					<div style="float: left; width: 74%;">
						<div style="float: left; margin-left: 10px;">
							<input type="text" style="width: 250px !important;" id="strOtherParticularsProrata" name="strOtherParticularsProrata" value="Out of Pocket Expenses" class="validateRequired">
						</div>
					</div>
					<div style="float: left; width: 20%;">
						<div style="float: right; margin-right: 10px;">
							<input type="text" name="strOtherParticularsAmtProrata" id="strOtherParticularsAmtProrata0" style="width: 65px !important; text-align:right;" onkeyup="calProrataAmt();" onkeypress="return isNumberKey(event)" class="validateRequired"/>
						</div>
					</div>
				</div>
				<% } %>
			<input type="hidden" name="srNoCnt" id="srNoCnt" value="<%=srNoCnt %>">
		</div>
		
		
		<div style="width: 100%; float: left; margin-bottom: 15px;">
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 65%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 15%;">
					<div style="float: left; margin-left: 10px;">Subtotal</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strTotalAmtProrata" id="strTotalAmtProrata" style="width: 65px !important; text-align:right;" readonly="readonly" class="validateRequired" />
					</div>
				</div>
			</div>
					
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
				<div style="float: left; width: 5%; color: white; background-color: #3B9C9C;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 55%; color: white; background-color: #3B9C9C;">
					<div style="float: left; margin-left: 10px; font-weight: bold;">Other Fees/Taxes</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: right; margin-right: 10px;">&nbsp;</div>
				</div>
			</div>
			
			<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();	
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">  <!-- padding: 4px 0px; -->
				<div style="float: left; width: 80%;">
					<div style="float: left; margin-left: 10px;">Add: <%=uF.showData(innerList.get(1),"-") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div id="taxHeadAmtDivProrata<%=i %>" style="float: right; margin-right: 10px;"></div>
				</div>
			</div>
			<% i++; } } %>
			
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
				<div style="float: left; width: 60%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;">Total (<%=currency %>)</div>
				</div>
				<div style="float: left; width: 20%;">
					<div id="totaldivProrata" style="float: right; margin-right: 10px;"></div>
				</div>
			</div>
			
		</div>
	</div>
	<!-- ************************************** INRCurrencyProRataTable End *************************************** -->
	
	
	
	<!-- ************************************** Other CurrencyProRataTable Start *************************************** -->
<div id="otherCurrencyTable" style="display: none;">
	<div style="width: 100%; float: left; margin-bottom: 9px;">
		
		<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
				<div style="float: left; width: 4%;">
					<div style="float: left; font-weight: bold; margin-left: 5px;">#</div>
				</div>
				<div style="float: left; width: 36%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Description</div>
				</div>
				<div style="float: left; width: 12%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Qty/<%=(String)request.getAttribute("billCalTypeLbl") %></div>
				</div>
				<div style="float: left; width: 12%;">
					<div style="float: left; font-weight: bold; margin-left: 20px;">Rate (<%=currency %>)</div>
				</div>
				<div style="float: left; width: 18%;">
				<input type="hidden" name="strResourceCountOtherCurrProRata" id="strResourceCountOtherCurrProRata" value="0">
				<input type="hidden" name="strPartiCountOtherCurrProrata" id="strPartiCountOtherCurrProrata" value="<%=(hmProRataProBillingHeadData != null && !hmProRataProBillingHeadData.isEmpty()) ? hmProRataProBillingHeadData.size() : "1" %>">
				<s:hidden name="strStartDateOtherCurr" id="strStartDateOtherCurr"></s:hidden>
				<s:hidden name="strEndDateOtherCurr" id="strEndDateOtherCurr"></s:hidden>

				<div style="float: right; font-weight: bold; margin-right: 20px;">Amount (<%=currency %>)</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; font-weight: bold; margin-right: 20px;">Amount (<span id="otherCurrSpan"><%=currency %></span>)</div>
			</div>
		</div>
			
		<% 
			int srNoCntOtherCurr = 1;
		%>
			
			<div id="OtherCurrPartiDiv">
				
				<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
						<div style="float: left; width: 4%;">
							<div style="float: left; margin-left: 10px;"><%=srNoCntOtherCurr %>.</div>
						</div>
						<div style="float: left; width: 36%;">
							<div style="float: left;">
								<span style="float: left;"><input type="text" name="strEmpOtherCurr" id="strEmpOtherCurr0" class="validateRequired"/></span>
								<span style="float: left; height: 15px;"><a title="Add" class="fa fa-plus" onclick="addParticularProrataOtherCurr()" href="javascript:void(0)">&nbsp;</a></span>
							</div>
						</div>
						<div style="float: left; width: 12%;">
							<div style="float: left; margin-left: 10px;">
								<input type="text" name="billDailyHoursINRCurr" id="billDailyHoursINRCurr0" style="width: 50px !important;" onkeyup="calEmpAmtOtherCurr();"  onkeypress="return isNumberKey(event)"/>
							</div>
						</div>
						<div style="float: left; width: 12%;">
							<div style="float: left; margin-left: 10px;">
								<input type="text" name="empRateINRCurr" id="empRateINRCurr0" style="width: 50px !important;" onkeyup="calEmpAmtOtherCurr();" onkeypress="return isNumberKey(event)"/>
							</div>
						</div>
						<div style="float: left; width: 18%;">
							<div style="float: right; margin-right: 10px;">
								<input type="text" name="strEmpAmtINRCurr" id="strEmpAmtINRCurr0" style="width: 65px !important; text-align:right;" readonly="readonly"/>
							</div>
						</div>
						<div style="float: left; width: 18%;">
							<div style="float: right; margin-right: 10px;">
								<input type="text" name="strEmpAmtOtherCurr" id="strEmpAmtOtherCurr0" style="width: 65px !important; text-align:right;" readonly="readonly"/>
							</div>
						</div>
					</div>
				<% srNoCntOtherCurr++; %>
			</div>
			
			<% 
			if(hmProRataProBillingHeadData != null && !hmProRataProBillingHeadData.isEmpty()) { 
				Iterator<String> it = hmProRataProBillingHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProRataProBillingHeadData.get(billingHeadId);
			%> 
			
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 4%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 60%;">
					<div style="float: left;">
						<input type="text" style="width: 250px !important;" id="strOtherParticularsINRCurrProrata" name="strOtherParticularsINRCurrProrata" value="<%=innerList.get(1) %>" class="validateRequired">
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmtINRCurrProrata" id="strOtherParticularsAmtINRCurrProrata<%=i %>" style="width: 65px !important; text-align:right;" onkeyup="calProrataAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired"/>
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmtOtherCurrProrata" id="strOtherParticularsAmtOtherCurrProrata<%=i %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
					</div>
				</div>
			</div>
			
			<% i++; 
				} %>
			<% } else { %>
			<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
				<div style="float: left; width: 4%;">
					<div style="float: left; margin-left: 10px;">&nbsp;</div>
				</div>
				<div style="float: left; width: 60%;">
					<div style="float: left;">
						<input type="text" style="width: 250px !important;" id="strOtherParticularsINRCurrProrata" name="strOtherParticularsINRCurrProrata" value="Out of Pocket Expenses" class="validateRequired">
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmtINRCurrProrata" id="strOtherParticularsAmtINRCurrProrata0" style="width: 65px !important; text-align:right;" onkeyup="calProrataAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired"/>
					</div>
				</div>
				<div style="float: left; width: 18%;">
					<div style="float: right; margin-right: 10px;">
						<input type="text" name="strOtherParticularsAmtOtherCurrProrata" id="strOtherParticularsAmtOtherCurrProrata0" style="width: 65px !important; text-align:right;" readonly="readonly"/>
					</div>
				</div>
			</div>
			<% } %>
		<input type="hidden" name="srNoCntOtherCurr" id="srNoCntOtherCurr" value="<%=srNoCntOtherCurr %>">
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 15px;">
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
			<div style="float: left; width: 52%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; margin-left: 10px;">Subtotal</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<s:textfield name="strTotalAmtINRCurrProrata" id="strTotalAmtINRCurrProrata" cssStyle="width: 65px !important; text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield>
				</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<s:textfield name="strTotalAmtOtherCurrProrata" id="strTotalAmtOtherCurrProrata" cssStyle="width: 65px !important; text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield>
				</div>
			</div>
		</div>
		
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 64%; color: white; background-color: #3B9C9C;">
				<div style="float: left; margin-left: 10px;">Other Fees/Taxes</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">&nbsp;</div>
			</div>
		</div>
		
		<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
			Iterator<String> it1 = hmProTaxHeadData.keySet().iterator();
			int i = 0;
			while(it1.hasNext()) {
			String taxHeadId = it1.next();	
			List<String> innerList = hmProTaxHeadData.get(taxHeadId);
		%>
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">  <!-- padding: 4px 0px; -->
			<div style="float: left; width: 64%;">
				<div style="float: left; margin-left: 10px;">Add: <%=uF.showData(innerList.get(1),"-") %></div>
			</div>
			<div style="float: left; width: 18%;">
				<div id="taxHeadINRAmtDivProrata<%=i %>" style="float: right; margin-right: 10px;"></div>
			</div>
			<div style="float: left; width: 18%;">
				<div id="taxHeadOtherAmtDivProrata<%=i %>" style="float: right; margin-right: 10px;"></div>
			</div>
		</div>
		<% i++; } } %>
		
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 52%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; margin-left: 10px;">Total (<%=currency %>)</div>
			</div>
			<div style="float: left; width: 18%;">
				<div id="totalINRAmtdivProrata" style="float: right; margin-right: 10px;"></div>
			</div>
			<div style="float: left; width: 18%;">
				<div id="totalOtherCurrdivProrata" style="float: right; margin-right: 55px;"></div>
			</div>
		</div>
		
	</div>
</div>
	<!-- ************************************** Other CurrencyProRataTable End *************************************** -->
	
	<div style="width: 100%; float: left; color: #404040; margin-bottom: 15px;">
		<div style="float: left; width: 100%;">
			<div style="float: left; margin-left: 10px;">Payment Mode:</div>
		</div>
		<% if((String)request.getAttribute("bankName") != null && !request.getAttribute("bankName").equals("")) { %>
			<div style="float: left; width: 100%;">
				<div style="float: left; margin-left: 20px; line-height: 15px;">
				<b>Bank Details:</b><br/>
				A/C No.: <%=hmBankAccData.get("ACC_NO") %><br/>
				Branch: <%=hmBankAccData.get("BRANCH_NAME") %><br/>
				Bank: <%=hmBankAccData.get("BANK_NAME") %><br/>
				<% if(hmBankAccData.get("IFSC_CODE") != null && !hmBankAccData.get("IFSC_CODE").equals("")) { %>
					IFSC: <%=hmBankAccData.get("IFSC_CODE") %><br/>
				<% } %>
				<% if(hmBankAccData.get("SWIFT_CODE") != null && !hmBankAccData.get("SWIFT_CODE").equals("")) { %>
					SWIFT: <%=hmBankAccData.get("SWIFT_CODE") %><br/>
				<% } %>
				<% if(hmBankAccData.get("CLEARING_CODE") != null && !hmBankAccData.get("CLEARING_CODE").equals("")) { %>
					BCC: <%=hmBankAccData.get("CLEARING_CODE") %><br/>
				<% } %>
				<% if(hmBankAccData.get("OTHER_INFO") != null && !hmBankAccData.get("OTHER_INFO").equals("")) { %>
					<%=hmBankAccData.get("OTHER_INFO") %><br/>
				<% } %>
				</div>
			</div>
			<%-- <div style="float: left; width: 100%;">
				<div style="float: left; margin-left: 20px;">Bank: <%=hmClientDetails.get("BANK_NAME") %></div>
			</div> --%>
		<% } %>
		<% if((String)request.getAttribute("payPalMailId") != null && !request.getAttribute("payPalMailId").equals("")) { %>
			<div style="float: left; width: 100%;">
				<div style="float: left; margin-left: 20px;">Paypal: <%=(String)request.getAttribute("payPalMailId") %></div>
			</div>
		<% } %>
	</div>


	<div style="width: 100%; float: left; margin-bottom: 15px;">
		<div style="float: left; width: 100%;">
			<div style="float: left; margin-left: 10px;">
				<textarea name="otherDescription" id="otherDescription" cols="85" rows="3"></textarea>
			</div>
		</div>
	</div>
	
	
	<div style="width: 100%; float: left; color: #404040; margin-bottom: 15px;">
		<div style="float: left; width: 100%;">
			<div style="float: left; margin-left: 10px; font-weight: bold;">This is computer generated statement and does not need signature.</div>
		</div>
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 29px;">
		<div style="float: left; width: 100%; color: white; background-color: #3B9C9C;">
			<div style="float: left; width: 30%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Phone No.</div>
			</div>
			<div style="float: left; width: 35%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">E-mail</div>
			</div>
			<div style="float: left; width: 35%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Web-site</div>
			</div>
		</div>
		
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF;">
			<div style="float: left; width: 30%;">
				<div style="float: left; margin-left: 10px;"><%=hmOrgData.get("ORG_CONTACT") %></div>
			</div>
			<div style="float: left; width: 35%;">
				<div style="float: left; margin-left: 10px;"><%=hmOrgData.get("ORG_EMAIL") %></div>
			</div>
			<div style="float: left; width: 35%;">
				<div style="float: left; margin-left: 10px;"><%=hmOrgData.get("ORG_WEBSITE") %></div>
			</div>
		</div>
	</div>
	
	
	<div style="float: left; width: 100%; text-align: center;">
	
	        	<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
					Iterator<String> it = hmProTaxHeadData.keySet().iterator();
					int i = 0;
					while(it.hasNext()) {
					String taxHeadId = it.next();	
					List<String> innerList = hmProTaxHeadData.get(taxHeadId);
				%>
					<input type="hidden" name="taxHead" id="taxHead<%=i %>" value="<%=innerList.get(3) %>"/>
					<input type="hidden" name="taxNameLabel" id="taxNameLabel<%=i %>" value="<%=innerList.get(1) %>"/>
					<input type="hidden" name="taxHeadPercent" id="taxHeadPercent<%=i %>" value="<%=innerList.get(2) %>"/>
					<input type="hidden" name="taxHeadAmt" id="taxHeadAmt<%=i %>"/>
				<% i++; } } %>
        	<input type="hidden" name="particularTotalAmt" id="particularTotalAmt" value="<%=(String)request.getAttribute("particularTotalAmt") %>"/>
        	<input type="hidden" name="totalAmt" id="totalAmt" value="<%=(String)request.getAttribute("totalAmt") %>"/>
        	
        	<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();	
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
			%>
				<input type="hidden" name="taxHeadOtherCurr" id="taxHeadOtherCurr<%=i %>" value="<%=innerList.get(3) %>"/>
				<input type="hidden" name="taxNameLabelOtherCurr" id="taxNameLabelOtherCurr<%=i %>" value="<%=innerList.get(1) %>"/>
				<input type="hidden" name="taxHeadPercentOtherCurr" id="taxHeadPercentOtherCurr<%=i %>" value="<%=innerList.get(2) %>"/>
				<input type="hidden" name="taxHeadAmtINRCurr" id="taxHeadAmtINRCurr<%=i %>"/>
				<input type="hidden" name="taxHeadAmtOtherCurr" id="taxHeadAmtOtherCurr<%=i %>"/>
			<% i++; } } %>
			
        	<input type="hidden" name="particularTotalAmtINRCurr" id="particularTotalAmtINRCurr" value="<%=(String)request.getAttribute("particularTotalAmtINRCurr") %>"/>
        	<input type="hidden" name="particularTotalAmtOtherCurr" id="particularTotalAmtOtherCurr" value="<%=(String)request.getAttribute("particularTotalAmtOtherCurr") %>"/>
        	<input type="hidden" name="totalAmtINRCurr" id="totalAmtINRCurr" value="<%=(String)request.getAttribute("totalAmtINRCurr") %>"/>
        	<input type="hidden" name="totalAmtOtherCurr" id="totalAmtOtherCurr" value="<%=(String)request.getAttribute("totalAmtOtherCurr") %>"/>
        	
        	<s:submit value="Submit" cssClass="btn btn-primary" id="btnOk" name="submit" />
        	
	</div>
	
	
	<div style="width: 100%; float: left; color: #404040; margin-bottom: 9px;">
		<div style="float: left; width: 100%; text-align: right;">
			<div style="float: right; margin-right: 10px; margin-left: -5px;"><img style="width: 90px;" src="images1/icons/icons/taskrig.png"></div>
			<div style="float: right; margin-top: 10px;">Powered by </div> 
		</div>
	</div>

</s:form>

<script type="text/javascript">
$(function() {
		calAmt();
	 });
</script>

	<% } %>

 <!-- ********************************************* End ProRata ******************************************* -->

</div>

</div>


</div>