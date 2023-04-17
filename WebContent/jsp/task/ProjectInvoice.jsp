<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
	
<%
Map<String,String> hmProjectDetails = (Map<String,String>)request.getAttribute("hmProjectDetails");
Map<String,String> hmTaxMiscSetting = (Map<String,String>)request.getAttribute("hmTaxMiscSetting");

UtilityFunctions uF=new UtilityFunctions();

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
Map<String, Map<String, String>> hmCurrencyDetails = (Map<String, Map<String, String>>)request.getAttribute("hmCurrencyDetails");
Map<String, String> hmCurr = hmCurrencyDetails.get(hmProjectDetails.get("CURRENCY_ID"));

if(hmCurr == null)hmCurr = new HashMap<String, String>();
String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";
//System.out.println("currency ===>> " + currency);

Map<String, List<String>> hmProBillingHeadData = (Map<String, List<String>>)request.getAttribute("hmProBillingHeadData");
Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmProTaxHeadData");

%>

<script>
$(function() {
	// binds form submission and fields to the validation engine
	$("#btnOk").click(function(){
		$('.validateRequired').filter(':hidden').prop('required', false);
		$('.validateRequired').filter(':visible').prop('required', true);
	});
	
	/* var strProjectOwner = document.getElementById("strProjectOwner").value; */
	var strProjectOwner = <%=request.getAttribute("strProjectOwner") %>;
	//alert("strProjectOwner ===>> " + strProjectOwner);
	getProjectOwnerDetails(strProjectOwner);
	
	var proCurrType = document.getElementById("strCurrency").value; 
	getExchangeValue(proCurrType);
	
});


$(function() {
    $("#invoiceGenDate").datepicker({format : 'dd/mm/yyyy'});
    
    $("#strStartDateOtherCurr").datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("strStartDateOtherCurr")%>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
		onClose: function(selectedDate){
			$("#strEndDateOtherCurr").datepicker("option", "minDate", selectedDate);
		}
	});
	
	$("#strEndDateOtherCurr").datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("strStartDateOtherCurr")%>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
		onClose: function(selectedDate){
			$("#strStartDateOtherCurr").datepicker("option", "maxDate", selectedDate);
		}
	});
	
	$("#strStartDate").datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("strStartDate")%>", maxDate: "<%=request.getAttribute("strEndDate")%>", 
		onClose: function(selectedDate){
			$("#strEndDate").datepicker("option", "minDate", selectedDate);
		}
	});
	
	$("#strEndDate").datepicker({
		Format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("strStartDate")%>", maxDate: "<%=request.getAttribute("strEndDate")%>", 
		onClose: function(selectedDate){
			$("#strStartDate").datepicker("option", "maxDate", selectedDate);
		}
	});
});



function addAmtType() {
	var cntAdd = document.getElementById("strPartiCount").value; 
	cntAdd++;
    var divTag = document.createElement("div");
    divTag.id = "p_add"+cntAdd;
    divTag.setAttribute("style","float:left; margin-top:5px;");
    divTag.innerHTML = "<span style=\"float:left;\"><input type=\"text\" style=\"width:250px !important;\" id=\"strParticulars\" name=\"strParticulars\" value=\"\"/></span>"+ // class=\"validate[required]\"
    "<span style=\"float:left; height: 15px; margin-top:-9px;\"><a title=\"Add\" class=\"fa fa-plus\" onclick=\"addAmtType()\" href=\"javascript:void(0)\">&nbsp;</a>"+
    "<a href=\"javascript:void(0)\" onclick=\"removeAddress(this.id)\" id=\""+cntAdd+"\" class=\"fa fa-remove\">&nbsp;</a></span>";
		
    document.getElementById("particularsID").appendChild(divTag);
    
    var divTag1 = document.createElement("div");
    divTag1.id = "pamt_add" + cntAdd;
    divTag1.setAttribute("style","float:right; margin-bottom: 5px;");
    divTag1.innerHTML = "<input type=\"text\" style=\"width:65px !important; text-align:right;\" id=\"strParticularsAmt"+cntAdd+"\" name=\"strParticularsAmt\" onkeyup=\"calAmt();\" onkeypress=\"return isNumberKey(event)\" value=\"\"/>"; //class=\"validate[required]\" 
		
    document.getElementById("particularsAmtID").appendChild(divTag1);
    document.getElementById("strPartiCount").value = cntAdd;
}


function addOtherCurrType() {
	var cntOtherAdd = document.getElementById("strPartiCountOtherCurr").value;
	cntOtherAdd++;
    var divTag = document.createElement("div");
    divTag.id = "p_add" + cntOtherAdd;
    divTag.setAttribute("style","float:left; margin-top:5px;");
    divTag.innerHTML = "<span style=\"float:left;\"><input type=\"text\" style=\"width:250px !important;\" id=\"strParticularsINRCurr\" name=\"strParticularsINRCurr\" value=\"\"/></span>"+ //class=\"validate[required]\"
    "<span style=\"float:left; height: 15px; margin-top:-9px;\"><a title=\"Add\" class=\"fa fa-plus\" onclick=\"addOtherCurrType()\" href=\"javascript:void(0)\">&nbsp;</a>"+
    "<a href=\"javascript:void(0)\" onclick=\"removeAddressOtherCurr(this.id)\" id=\""+cntOtherAdd+"\" class=\"fa fa-remove\">&nbsp;</a></span>";
		
    document.getElementById("particularsOtherCurrID").appendChild(divTag);
    
    var divTag1 = document.createElement("div");
    divTag1.id = "pamt_add"+cntOtherAdd; 
    divTag1.setAttribute("style","float:right; margin-bottom: 5px;");
    divTag1.innerHTML = "<input type=\"text\" style=\"width:65px !important; text-align:right;\" id=\"strParticularsAmtINRCurr"+cntOtherAdd+"\" name=\"strParticularsAmtINRCurr\" onkeyup=\"calAmtOtherCurr();\" onkeypress=\"return isNumberKey(event)\" value=\"\"/>"; //class=\"validate[required]\"
		
    document.getElementById("particularsAmtINRCurrID").appendChild(divTag1);
    
    var divTag2 = document.createElement("div");
    divTag2.id = "pOCamt_add"+cntOtherAdd; 
    divTag2.setAttribute("style","float:right; margin-bottom: 5px;");
    divTag2.innerHTML = "<input type=\"text\" style=\"width:65px !important; text-align:right;\" id=\"strParticularsAmtOtherCurr"+cntOtherAdd+"\" name=\"strParticularsAmtOtherCurr\" readonly=\"readonly\" value=\"\"/>";
	
    document.getElementById("particularsAmtOtherCurrID").appendChild(divTag2);
    document.getElementById("strPartiCountOtherCurr").value = cntOtherAdd;
}


function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
      return false;
   }
   return true;
}
// serviceTaxDiv eduCessDiv secHighCessDiv totaldiv

function calAmt() {

	var cntAdd = document.getElementById("strPartiCount").value;
	
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


function removeAddressOtherCurr(removeId) {
    var remove_elem = "p_add"+removeId;
    var row_skill = document.getElementById(remove_elem);
    document.getElementById("particularsOtherCurrID").removeChild(row_skill);
    
    var remove_elem1 = "pamt_add"+removeId;
    var row_skill1 = document.getElementById(remove_elem1);
    document.getElementById("particularsAmtINRCurrID").removeChild(row_skill1);
    
    var remove_elem1 = "pOCamt_add"+removeId;
    var row_skill1 = document.getElementById(remove_elem1);
    document.getElementById("particularsAmtOtherCurrID").removeChild(row_skill1);
    
    calAmtOtherCurr();
}


function removeAddress(removeId) {
    var remove_elem = "p_add"+removeId;
    var row_skill = document.getElementById(remove_elem);
    document.getElementById("particularsID").removeChild(row_skill);
    
    var remove_elem1 = "pamt_add"+removeId;
    var row_skill1 = document.getElementById(remove_elem1);
    document.getElementById("particularsAmtID").removeChild(row_skill1);
    
    calAmt();
}

 
function getExchangeValue(value) {
	
	var proCurrency = document.getElementById("proCurrency").value;
	//alert("proCurrency ===>> " + proCurrency)
	 if(value == proCurrency || value == '' || value == '0') {
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
	
	
function getProjectOwnerDetails(strProjectOwner) {

		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {

			var xhr = $.ajax({
				url : "ProjectOwnerDetails.action?strProjectOwner="+strProjectOwner,
				cache : false,

				success : function(data) {
					var res = data.split("::::");
					//alert("data ===>> " + data);
					document.getElementById("ownerDiv").innerHTML=res[0];
					document.getElementById("ownerEmailDiv").innerHTML=res[1];
					document.getElementById("ownerSignDiv").innerHTML=res[2];
					document.getElementById("officesDiv").innerHTML=res[3];
					document.getElementById("ownerLocationTelDiv").innerHTML=res[4]; 
					document.getElementById("ownerLocationFaxDiv").innerHTML=res[5];
					document.getElementById("ownerORGPanDiv").innerHTML=res[6];
					document.getElementById("ownerORGMCARegDiv").innerHTML=res[7];
					document.getElementById("ownerORGSTRegDiv").innerHTML=res[8];
				}
			});
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
	 
</script>


<div id="invoiceSummaryid" class="box-body">


<s:form theme="simple" action="ProjectInvoice" name="frm" id="formID" cssClass="formcss" onsubmit="return checkInvoiceCode();">

<s:hidden name="pro_id" id="pro_id"></s:hidden>
<s:hidden name="pro_freq_id"></s:hidden>
<input type="hidden" name="invoiceType" id="invoiceType" value="1"/>

<table class="table table_hover">
<tr>
	<td colspan="5" width="70%">
		<div id="ownerDiv"></div>
    </td>
    <td><s:select label="Select Project Owner" name="strProjectOwner" id="strProjectOwner" listKey="employeeId" cssClass="validateRequired" headerKey=""
			headerValue="Select Project Owner" listValue="employeeName" list="projectOwnerList" key="" required="true" onchange="getProjectOwnerDetails(this.value)"/>
    </td>
</tr>
<tr>
     <td colspan="4" style="text-align: right;">Tel:</td>
     <td colspan="2"><div id="ownerLocationTelDiv"></div></td>
</tr>

<tr>
     <td colspan="4" style="text-align: right;">Fax:</td>
     <td colspan="2"><div id="ownerLocationFaxDiv"></div></td> 
</tr>
<tr>
     <td colspan="4" style="text-align: right;">Email:</td>
     <td colspan="2"><div id="ownerEmailDiv"></div></td>
</tr>
<tr>
        <td colspan="6">Bill No. <s:textfield name="invoiceCode" id="invoiceCode" cssClass="validateRequired" onkeyup="checkInvoiceCode(this.value);"/><br/>
        	<s:textfield name="invoiceGenDate" id="invoiceGenDate" cssClass="validateRequired" cssStyle="width: 120px !important; margin-left: 43px; margin-top: 5px;" readonly="true"></s:textfield>
        </td>
</tr>
<tr>
        <td colspan="6">To <br/>
        	<s:select theme="simple" name="clientPoc" listKey="clientPocId" listValue="clientPocName" headerKey="" headerValue="Select SPOC" 
        	cssClass="validateRequired" cssStyle="width:200px !important;" list="clientPocList" key="" required="true" /></td>
</tr>
<tr>
        <td colspan="6">
        	<s:select theme="simple" name="clientAddress" listKey="clientAddressId" listValue="clientAddress" headerKey="" headerValue="Select Address" 
        	cssClass="validateRequired" cssStyle="width: 200px !important;" list="clientAddressList" key="" required="true" /></td>
</tr>
<tr>
    	<td colspan="6">
        	<div style="float: left;">
        	<s:hidden name="proCurrency" id="proCurrency"/>
	        	<s:select id="strCurrency" cssClass="validateRequired" name="strCurrency" listKey="currencyId" listValue="currencyName" headerKey="" 
	        		headerValue="Select Currency" list="currencyList" key="" required="true" cssStyle="width: 200px !important;" onchange="getExchangeValue(this.value);"/>
        	</div>
        	
        	<div id="exchangeValDIV" style="float: left; margin-top: 5px;"> </div>
		</td>
	</tr>
	
<tr>
    <td colspan="6">
    	<table class="table table_no_border" id="INRCurrencyTable" style="display: block;"> 
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >PARTICULARS</td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;">AMOUNT<%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;">AMOUNT<%=currency %></td>
			</tr>
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;"><!-- Professional Fees for rendering Accounting Services for the month of December 2013 --></td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			<tr id="BillPeriodTR" style="display: none;">
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDate" id="strStartDate" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDate"  id="strEndDate" cssStyle="width:65px !important;" cssClass="validateRequired" />)
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textarea name="strReferenceNo" id="strReferenceNo" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
		
			<tr><td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">&nbsp;</td>
			<td style="border-right: 1pt solid black;">
			<input type="hidden" name="strPartiCount" id="strPartiCount" value="<%=(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) ? hmProBillingHeadData.size() : "1" %>">
			&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<% 
			String strPerticnt = (String)request.getAttribute("strPerticnt");
			if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) { 
				Iterator<String> it = hmProBillingHeadData.keySet().iterator();
				int i = 0;
				int j = 0;
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProBillingHeadData.get(billingHeadId);
				
				if(uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE && uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE_OVERALL) { // 
			%>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;"
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsID"
				 <% } %> >									
					<div><span style="float:left;"><input type="text" style="width:250px !important;" id="strParticulars" value="<%=uF.showData(innerList.get(1), "") %>" name="strParticulars" class="validateRequired"></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="fa fa-plus" onclick="addAmtType()" href="javascript:void(0)">&nbsp;</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsAmtID"
				 <% } %> >
				<div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmt" id="strParticularsAmt<%=i %>" style="width:65px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" 
				<% if(i==0) { %> value="<%=uF.showData((String)request.getAttribute("remainAmt"), "") %>" <% } %> /></div>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			<% 
				} else { %> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px !important;" id="strOtherParticulars" value="<%=innerList.get(1) %>" name="strOtherParticulars"> <!-- class="validateRequired" -->
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt<%=i %>" style="width:65px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" 
					<% if(j==0) { %> value="<%=uF.showData((String)request.getAttribute("reimbursement_amount"), "") %>" <% } %> />
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% j++; } %>
			<% i++;
				} %>
			<% } else { %>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;" id="particularsID">									
					<div><span style="float:left;"><input type="text" style="width:250px !important;" id="strParticulars" value="Professional Fees" name="strParticulars" class="validateRequired"></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="fa fa-plus" onclick="addAmtType()" href="javascript:void(0)">&nbsp;</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtID">
				<div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmt" id="strParticularsAmt0" style="width:65px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" value="<%=uF.showData((String)request.getAttribute("remainAmt"), "") %>"/></div>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textfield name="strOtherParticulars" id="strOtherParticulars" value="Out of Pocket Expenses" cssStyle="width:250px !important;" />
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
				<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt0" style="width:65px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("reimbursement_amount"), "") %>"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% } %>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmt" id="strTotalAmt" cssStyle="width:65px !important; text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
			</tr> 
			
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;"><%=hmProjectDetails.get("PRO_NAME") %></td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;"><%=hmProjectDetails.get("DESCRIPTION") %></td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;">
					<s:textarea name="proDescription" id="proDescription" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();	
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
				%>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=uF.showData(innerList.get(1),"-") %></td> <%-- @ <%=uF.showData(innerList.get(2),"0") %>% --%>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadAmtDiv<%=i %>" style="text-align: right;"></div></td>
			</tr>
			<% i++; } } %>
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;" >&nbsp;</td>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black;"></td>
			</tr>
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;">
					<textarea name="otherDescription" id="otherDescription" cols="50" rows="2"><%=uF.showData((String)request.getAttribute("otherDescription"), "") %> </textarea>
					<%-- <s:textarea name="otherDescription" id="otherDescription" cols="50" rows="2" value="TDS is required to be deducted @ 3% as per approval of ITO"></s:textarea> --%>
				</td>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black;"></td>
			</tr>
			<tr>   
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >TOTAL <%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totaldiv" style="text-align: right;"></div></td>
			</tr>
		</table>
		
		
		<table class="table table_no_border" id="otherCurrencyTable" style="display: none;"> 
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;">PARTICULARS</td>
				<td style="text-align: center; font-weight: bold; font-size: 5; border: 1pt solid black;"><%=currency %></td>
				<td style="text-align: center; font-weight: bold; font-size: 5; border: 1pt solid black;">
					<span id="otherCurrSpan"><%=currency %></span>
				</td>
			</tr>
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;"> <!-- Professional Fees for rendering Accounting Services for the month of December 2013 --></td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			<tr id="BillPeriodOtherCurrTR" style="display: none;">
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDateOtherCurr" id="strStartDateOtherCurr" cssStyle="width:65px !important;" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDateOtherCurr"  id="strEndDateOtherCurr" cssStyle="width:65px !important;" cssClass="validateRequired"></s:textfield>)
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textarea name="strReferenceNoOtherCurr" id="strReferenceNoOtherCurr" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr><td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">
				<input type="hidden" name="strPartiCountOtherCurr" id="strPartiCountOtherCurr" value="<%=(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) ? hmProBillingHeadData.size() : "1" %>">
				&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<% 
			if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) { 
				Iterator<String> it = hmProBillingHeadData.keySet().iterator();
				int i = 0;
				int j = 0;
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProBillingHeadData.get(billingHeadId);
				
				if(uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE && uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE_OVERALL) { //
			%>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;"
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsOtherCurrID"
				 <% } %> >									
					<div><span style="float:left;"><input type="text" style="width:250px !important;" id="strParticularsINRCurr" value="<%=uF.showData(innerList.get(1), "") %>" name="strParticularsINRCurr" class="validateRequired"></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="fa fa-plus" onclick="addOtherCurrType()" href="javascript:void(0)">&nbsp;</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsAmtINRCurrID"
				 <% } %> >
				 <div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr<%=i %>" style="width:65px !important; text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" 
				<% if(i==0) { %> value="<%=uF.showData((String)request.getAttribute("remainAmt"), "") %>" <% } %> /></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				id="particularsAmtOtherCurrID"
				<% } %> >
					<div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr<%=i %>" style="width:65px !important; text-align:right;" readonly="readonly"/></div>
				</td> 
			</tr>
			<% 
				} else { %> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px !important;" id="strOtherParticularsINRCurr" value="<%=uF.showData(innerList.get(1), "") %>" name="strOtherParticularsINRCurr">
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr<%=i %>" style="width:65px !important; text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" 
					<% if(j==0) { %> value="<%=uF.showData((String)request.getAttribute("reimbursement_amount"), "") %>" <% } %> />
				</div>
				</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" >
					<div style="margin-bottom: 5px;"><input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr<%=i %>" style="width:65px !important; text-align:right;" readonly="readonly"/></div>
				</td>
			</tr> 
			<% j++; } %>
			<% i++;
				} %>
			<% } else { %>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;" id="particularsOtherCurrID">									
					<div><span style="float:left;"><input type="text" style="width:250px !important;" id="strParticularsINRCurr" value="Professional Fees" name="strParticularsINRCurr" class="validateRequired"></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="fa fa-plus" onclick="addOtherCurrType()" href="javascript:void(0)">&nbsp;</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtINRCurrID">
				 <div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr0" style="width:65px !important; text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" value="<%=uF.showData((String)request.getAttribute("remainAmt"), "") %>"/></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtOtherCurrID">
					<div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr0" style="width:65px !important; text-align:right;" readonly="readonly"/></div>
				</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px !important;" id="strOtherParticularsINRCurr" value="Out of Pocket Expenses" name="strOtherParticularsINRCurr">
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr0" style="width:65px !important; text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("reimbursement_amount"), "") %>"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" >
					<div style="margin-bottom: 5px;"><input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr0" style="width:65px !important; text-align:right;" readonly="readonly"/></div>
				</td>
			</tr> 
			
			<% } %>
			
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtINRCurr" id="strTotalAmtINRCurr" cssStyle="width:65px !important; text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtOtherCurr" id="strTotalAmtOtherCurr" cssStyle="width:65px !important; text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
			</tr> 
			
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;"><%=hmProjectDetails.get("PRO_NAME") %></td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;"><%=hmProjectDetails.get("DESCRIPTION") %></td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;">
					<s:textarea name="proDescriptionOtherCurr" id="proDescriptionOtherCurr" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String taxHeadId = it.next();
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
				%>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=uF.showData(innerList.get(1),"-") %></td>  <%-- @ <%=uF.showData(innerList.get(2),"0") %>% --%>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadINRAmtDiv<%=i %>" style="text-align: right;"></div></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadOtherAmtDiv<%=i %>" style="text-align: right;"></div></td>
			</tr>
				<% i++; } } %>
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;" >&nbsp;</td>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black;"></td>
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;">
					<textarea name="otherDescriptionOtherCurr" id="otherDescriptionOtherCurr" cols="50" rows="2"><%=uF.showData((String)request.getAttribute("otherDescription"), "") %> </textarea>
				</td>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black;"></td>
			</tr>
				
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >TOTAL <%=currency %></td>
				<td style="text-align: center; font-weight: bold; font-size: 5; border: 1pt solid black;"><div id="totalINRAmtdiv" style="text-align: right;"></div></td>
				<td style="text-align: center; font-weight: bold; font-size: 5; border: 1pt solid black; padding-right: 5px;"><div id="totalOtherCurrdiv" style="text-align: right;"></div></td>
			</tr>
		</table>
		
    </td>
</tr>
<tr>
        <td colspan="6"> 
	        Choose Bank: <s:select name="bankName" listKey="bankId" listValue="bankName" cssClass="validateRequired" headerKey="" headerValue="Select Bank" list="bankList"/>
        </td>
</tr>  

<tr>
        <td colspan="3"> 
	        <div id="ownerSignDiv"> </div>
        </td>
        
    	<td colspan="3">
		    <div style="float: left; width: 100%;">MCA Registration No.: <span id="ownerORGMCARegDiv"></span></div>
			<div style="float: left; width: 100%;">PAN: <span id="ownerORGPanDiv"></span></div>
			<div style="float: left; width: 100%;">S T Registration No.: <span id="ownerORGSTRegDiv"></span></div>
		</td> 
</tr>

<tr>
    <td colspan="6" style="text-align: center;"><div id="officesDiv"></div></td>
</tr>

<tr>
        <td colspan="6" style="text-align: center;">
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
        	<!-- <input type="hidden" name="serviceTaxAmt" id="serviceTaxAmt"/>  
        	<input type="hidden" name="eduCessAmt" id="eduCessAmt"/>
        	<input type="hidden" name="stdTaxAmt" id="stdTaxAmt"/> -->
        	<input type="hidden" name="particularTotalAmt" id="particularTotalAmt"/>
        	<input type="hidden" name="totalAmt" id="totalAmt"/>
        	
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
        	<!-- <input type="hidden" name="serviceTaxAmtOtherCurr" id="serviceTaxAmtOtherCurr"/>  
        	<input type="hidden" name="eduCessAmtOtherCurr" id="eduCessAmtOtherCurr"/>
        	<input type="hidden" name="stdTaxAmtOtherCurr" id="stdTaxAmtOtherCurr"/> -->
        	<input type="hidden" name="particularTotalAmtINRCurr" id="particularTotalAmtINRCurr"/>
        	<input type="hidden" name="particularTotalAmtOtherCurr" id="particularTotalAmtOtherCurr"/>
        	<input type="hidden" name="totalAmtINRCurr" id="totalAmtINRCurr"/>
        	<input type="hidden" name="totalAmtOtherCurr" id="totalAmtOtherCurr"/>
        	
        	<s:submit value="Submit" cssClass="btn btn-primary" id="btnOk" name="submit" ></s:submit>
        </td>
</tr>


</table>
</s:form>

<script type="text/javascript">
//(document).ready(function() {
	 	calAmt();
	// });
</script>

</div>
    


