<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
	
<%
	Map<String,String> hmProjectDetails = (Map<String,String>)request.getAttribute("hmProjectDetails");
	Map<String,String> hmTaxMiscSetting = (Map<String,String>)request.getAttribute("hmTaxMiscSetting");
	UtilityFunctions uF = new UtilityFunctions();
	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	Map<String, Map<String, String>> hmCurrencyDetails = (Map<String, Map<String, String>>) request.getAttribute("hmCurrencyDetails");
	Map<String, String> hmCurr = hmCurrencyDetails.get(hmProjectDetails.get("CURRENCY_ID"));
	if(hmCurr == null) hmCurr = new HashMap<String, String>();
	String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";
	
	Map<String, String> hmProInvoiceData = (Map<String, String>)request.getAttribute("hmProInvoiceData");
	if(hmProInvoiceData == null)
		hmProInvoiceData = new HashMap<String, String>();
	
	Map<String, List<String>> hmProBillingHeadData = (Map<String, List<String>>)request.getAttribute("hmProBillingHeadData");
	Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmProTaxHeadData");
%>
<script>
jQuery(document).ready(function() {
	// binds form submission and fields to the validation engine
	jQuery("#formID").validationEngine();
	//calAmt();
	
	/* var strProjectOwner = document.getElementById("strProjectOwner").value; */
	var strProjectOwner = <%=request.getAttribute("strProjectOwner") %>;
	//alert("strProjectOwner ===>>" + strProjectOwner);
	getProjectOwnerDetails(strProjectOwner);
	
	var proCurrType = document.getElementById("strCurrency").value; 
	//alert(" proCurrType ==>>> " + proCurrType);
	getExchangeValue(proCurrType);	
});

$(function() {
   /*  $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'}); */
    
    $("#strStartDateOtherCurr").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDateOtherCurr")%>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
		onClose: function(selectedDate){
			$("#strEndDateOtherCurr").datepicker("option", "minDate", selectedDate);
		}
	});
	
	$("#strEndDateOtherCurr").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDateOtherCurr")%>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
		onClose: function(selectedDate){
			$("#strStartDateOtherCurr").datepicker("option", "maxDate", selectedDate);
		}
	});
	
	$("#strStartDate").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDate")%>", maxDate: "<%=request.getAttribute("strEndDate")%>", 
		onClose: function(selectedDate){
			$("#strEndDate").datepicker("option", "minDate", selectedDate);
		}
	});
	
	$("#strEndDate").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDate")%>", maxDate: "<%=request.getAttribute("strEndDate")%>", 
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
    divTag.innerHTML = "<span style=\"float:left;\"><input type=\"text\" style=\"width:250px;\" id=\"strParticulars\" name=\"strParticulars\" value=\"\"/></span>"+ // class=\"validateRequired\"
    "<span style=\"float:left; height: 15px; margin-top:-9px;\"><a title=\"Add\" class=\"add\" onclick=\"addAmtType()\" href=\"javascript:void(0)\">Add</a>"+
    "<a href=\"javascript:void(0)\" onclick=\"removeAddress(this.id)\" id=\""+cntAdd+"\" class=\"remove\">Remove</a></span>";
		
    document.getElementById("particularsID").appendChild(divTag);
    
    var divTag1 = document.createElement("div");
    divTag1.id = "pamt_add" + cntAdd;
    divTag1.setAttribute("style","float:right; margin-bottom: 5px;");
    divTag1.innerHTML = "<input type=\"text\" style=\"width:65px;text-align:right;\" id=\"strParticularsAmt"+cntAdd+"\" name=\"strParticularsAmt\" onkeyup=\"calAmt();\" onkeypress=\"return isNumberKey(event)\" value=\"\"/>"; //class=\"validateRequired\" 
		
    document.getElementById("particularsAmtID").appendChild(divTag1);
    document.getElementById("strPartiCount").value = cntAdd;
}


function addOtherCurrType() {
	var cntOtherAdd = document.getElementById("strPartiCountOtherCurr").value;
	cntOtherAdd++;
    var divTag = document.createElement("div");
    divTag.id = "p_add" + cntOtherAdd;
    divTag.setAttribute("style","float:left; margin-top:5px;");
    divTag.innerHTML = "<span style=\"float:left;\"><input type=\"text\" style=\"width:250px;\" id=\"strParticularsINRCurr\" name=\"strParticularsINRCurr\" value=\"\"/></span>"+ //class=\"validateRequired\"
    "<span style=\"float:left; height: 15px; margin-top:-9px;\"><a title=\"Add\" class=\"add\" onclick=\"addOtherCurrType()\" href=\"javascript:void(0)\">Add</a>"+
    "<a href=\"javascript:void(0)\" onclick=\"removeAddressOtherCurr(this.id)\" id=\""+cntOtherAdd+"\" class=\"remove\">Remove</a></span>";
		
    document.getElementById("particularsOtherCurrID").appendChild(divTag);
    
    var divTag1 = document.createElement("div");
    divTag1.id = "pamt_add"+cntOtherAdd; 
    divTag1.setAttribute("style","float:right; margin-bottom: 5px;");
    divTag1.innerHTML = "<input type=\"text\" style=\"width:65px;text-align:right;\" id=\"strParticularsAmtINRCurr"+cntOtherAdd+"\" name=\"strParticularsAmtINRCurr\" onkeyup=\"calAmtOtherCurr();\" onkeypress=\"return isNumberKey(event)\" value=\"\"/>"; //class=\"validateRequired\"
		
    document.getElementById("particularsAmtINRCurrID").appendChild(divTag1);
    
    var divTag2 = document.createElement("div");
    divTag2.id = "pOCamt_add"+cntOtherAdd; 
    divTag2.setAttribute("style","float:right; margin-bottom: 5px;");
    divTag2.innerHTML = "<input type=\"text\" style=\"width:65px;text-align:right;\" id=\"strParticularsAmtOtherCurr"+cntOtherAdd+"\" name=\"strParticularsAmtOtherCurr\" readonly=\"readonly\" value=\"\"/>";
	
    document.getElementById("particularsAmtOtherCurrID").appendChild(divTag2);
    document.getElementById("strPartiCountOtherCurr").value = cntOtherAdd;
    
}


function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
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
		}, 500); 
	 
 }

 function updateExchangeValue() {
	 
	var currencyType = document.getElementById("currencyType").value;
	var proCurrency = document.getElementById("proCurrency").value;
	var exchangeValue = document.getElementById("exchangeValue").value;
 	getContent('exchangeValDIV', 'ViewAndUpdateExchangeValue.action?currencyType='+currencyType+'&proCurrency='+proCurrency+'&exchangeValue='+exchangeValue+'&type=U');
 }


function getProjectOwnerDetails(strProjectOwner) {
	//alert(strProjectOwner);
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

					document.getElementById("ownerDiv").innerHTML=res[0];
					document.getElementById("ownerEmailDiv").innerHTML=res[1];
					document.getElementById("ownerSignDiv").innerHTML=res[2];
					document.getElementById("officesDiv").innerHTML=res[3]; 
					
					document.getElementById("ownerLocationTelDiv").innerHTML=res[4]; 
					document.getElementById("ownerLocationFaxDiv").innerHTML=res[5];
					document.getElementById("ownerLocationPanDiv").innerHTML=res[6];
					document.getElementById("ownerLocationRegNoDiv").innerHTML=res[7];
					document.getElementById("ownerLocationECC1Div").innerHTML=res[8];
					document.getElementById("ownerLocationECC2Div").innerHTML=res[9];
					
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
			 calAmtOtherCurr();
			 document.getElementById("INRCurrencyTable").style.display = "none";

		 }
	 }
 
</script>

<div id="invoiceSummaryid" class="leftbox reportWidth"  style="font-size: 12px;">

<s:form theme="simple" action="ProjectPartialInvoice" name="frm" id="formID" cssClass="formcss">
<s:hidden name="pro_id"></s:hidden>
<s:hidden name="pro_freq_id"></s:hidden>
<input type="hidden" name="invoiceType" id="invoiceType" value="1"/>

<table class="table_style" width="99%">
<tr>
	<td colspan="2" style="width: 30%;"><%=hmProInvoiceData.get("SECTION_1") %></td>
	<td colspan="2" align="center"><%=hmProInvoiceData.get("SECTION_2") %></td>
	<td colspan="2" align="right" style="width: 30%;"><%=hmProInvoiceData.get("SECTION_3") %></td>
</tr>
<tr>
	<td colspan="2" style="width: 30%;"><%=hmProInvoiceData.get("SECTION_4") %></td>
	<td colspan="2" align="center"><%=hmProInvoiceData.get("SECTION_5") %></td>
	<td colspan="2" align="right" style="width: 30%;"><%=hmProInvoiceData.get("SECTION_6") %></td>
</tr>
<tr>
    <td colspan="6">
    	<table id="INRCurrencyTable" style="display: block; width: 99%;">  
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >PARTICULARS</td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;">AMOUNT<%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;">AMOUNT<%=currency %></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;"><!-- Professional Fees for rendering Accounting Services for the month of December 2013 --></td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			
			<tr id="BillPeriodTR" style="display: none;">
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDate" id="strStartDate" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDate"  id="strEndDate" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>)
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
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
				
				if(uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE) { 
			%>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;"
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsID"
				 <% } %> >									
					<div><span style="float:left;"><input type="text" style="width:250px" id="strParticulars" value="<%=innerList.get(1) %>" name="strParticulars" class="validateRequired"></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addAmtType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsAmtID"
				 <% } %> >
				<div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmt" id="strParticularsAmt<%=i %>" style="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" 
				<% if(i==0) { %> value="<%=(String)request.getAttribute("percentageAmt") %>" <% } %> /></div>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			<% 
				} else { %> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px" id="strOtherParticulars" value="<%=innerList.get(1) %>" name="strOtherParticulars" class="validateRequired">
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt<%=i %>" style="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" 
					<% if(j==0) { %> value="<%=(String)request.getAttribute("reimbursement_amount") %>" <% } %> />
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
					<div><span style="float:left;"><input type="text" style="width:250px" id="strParticulars" value="Professional Fees" name="strParticulars" class="validateRequired"></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addAmtType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtID">
				<div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmt" id="strParticularsAmt0" style="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" value="<%=(String)request.getAttribute("percentageAmt") %>"/></div>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textfield name="strOtherParticulars" id="strOtherParticulars" value="Out of Pocket Expenses" cssStyle="width:250px;" cssClass="validateRequired"></s:textfield>
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
				<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt0" style="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" value="<%=(String)request.getAttribute("reimbursement_amount") %>"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% } %>
			
			<tr> 
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmt" id="strTotalAmt" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
			</tr> 
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;"><%=hmProjectDetails.get("PRO_NAME") %></td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;"><%=hmProjectDetails.get("DESCRIPTION") %></td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
					<s:textarea name="proDescription" id="proDescription" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
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
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadAmtDiv<%=i %>" style="text-align: right;"></div></td>
			</tr>
			<% i++; } } %>
				
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;" >&nbsp;</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
					<textarea name="otherDescription" id="otherDescription" cols="50" rows="2"><%=(String)request.getAttribute("otherDescription") %> </textarea>
				</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			<tr>   
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >TOTAL <%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totaldiv" style="text-align: right;"></div></td>
			</tr>
		</table>
		
		
		
		<table id="otherCurrencyTable" style="display: none; width: 99%;"> 
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >PARTICULARS</td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"><%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;">
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
				(Billing period taken as <s:textfield name="strStartDateOtherCurr" id="strStartDateOtherCurr" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDateOtherCurr"  id="strEndDateOtherCurr" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>)
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
				
				if(uF.parseToInt(innerList.get(2)) != IConstants.DT_OPE) { 
			%>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;"
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsOtherCurrID"
				 <% } %> >									
					<div><span style="float:left;"><input type="text" style="width:250px" id="strParticularsINRCurr" value="<%=innerList.get(1) %>" name="strParticularsINRCurr" class="validateRequired"></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addOtherCurrType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsAmtINRCurrID"
				 <% } %> >
				 <div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr<%=i %>" style="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" 
				<% if(i==0) { %> value="<%=(String)request.getAttribute("percentageAmt") %>" <% } %> /></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				id="particularsAmtOtherCurrID"
				<% } %> >
					<div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr<%=i %>" style="width:65px;text-align:right;" readonly="readonly"/></div>
				</td> 
			</tr>
			<% 
				} else { %> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px" id="strOtherParticularsINRCurr" value="<%=innerList.get(1) %>" name="strOtherParticularsINRCurr" class="validateRequired">
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr<%=i %>" style="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" 
					<% if(j==0) { %> value="<%=(String)request.getAttribute("reimbursement_amount") %>" <% } %> />
				</div>
				</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" >
					<div style="margin-bottom: 5px;"><input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr<%=i %>" style="width:65px;text-align:right;" readonly="readonly"/></div>
				</td>
			</tr> 
			<% j++; } %>
			<% i++;
				} %>
			<% } else { %>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;" id="particularsOtherCurrID">									
					<div><span style="float:left;"><input type="text" style="width:250px" id="strParticularsINRCurr" value="Professional Fees" name="strParticularsINRCurr" class="validateRequired"></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addOtherCurrType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtINRCurrID">
				 <div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr0" style="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" value="<%=(String)request.getAttribute("percentageAmt") %>"/></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtOtherCurrID">
					<div style="margin-bottom: 5px;"><input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr0" style="width:65px;text-align:right;" readonly="readonly"/></div>
				</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px" id="strOtherParticularsINRCurr" value="Out of Pocket Expenses" name="strOtherParticularsINRCurr" class="validateRequired">
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr0" style="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" value="<%=(String)request.getAttribute("reimbursement_amount") %>"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" >
					<div style="margin-bottom: 5px;"><input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr0" style="width:65px;text-align:right;" readonly="readonly"/></div>
				</td>
			</tr> 
			<% } %>
			
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtINRCurr" id="strTotalAmtINRCurr" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtOtherCurr" id="strTotalAmtOtherCurr" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
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
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=uF.showData(innerList.get(1),"-") %></td> <%--  @ <%=uF.showData(innerList.get(2),"0") %>% --%>
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
					<textarea name="otherDescriptionOtherCurr" id="otherDescriptionOtherCurr" cols="50" rows="2"><%=(String)request.getAttribute("otherDescription") %> </textarea>
				</td>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black;"></td>
			</tr>
			
			<tr>   
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >TOTAL <%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"><div id="totalINRAmtdiv" style="text-align: right;"></div></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totalOtherCurrdiv" style="text-align: right;"></div></td>
			</tr>
		</table>
		
    </td>
</tr>

<tr>
        <td colspan="3"><%=hmProInvoiceData.get("SECTION_7") %></td>
        <td colspan="3" align="right"><%=hmProInvoiceData.get("SECTION_8") %></td>
	</tr>  

	<tr>
        <td colspan="3"><%=hmProInvoiceData.get("SECTION_9") %></td>
        <td colspan="3" align="right"><%=hmProInvoiceData.get("SECTION_10") %></td>
	</tr>
	
	<tr>
        <td colspan="6" align="center"><%=hmProInvoiceData.get("SECTION_11") %></td>
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
        	<input type="hidden" name="particularTotalAmtINRCurr" id="particularTotalAmtINRCurr"/>
        	<input type="hidden" name="particularTotalAmtOtherCurr" id="particularTotalAmtOtherCurr"/>
        	<input type="hidden" name="totalAmtINRCurr" id="totalAmtINRCurr"/>
        	<input type="hidden" name="totalAmtOtherCurr" id="totalAmtOtherCurr"/>
        	
        	<s:submit value="Submit" cssClass="input_button" name="submit" ></s:submit>
        </td>
</tr>


</table>
</s:form>
<script type="text/javascript">
jQuery(document).ready(function() {
	 	calAmt();
	 });
</script>
</div>
    


