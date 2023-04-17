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
Map<String, Map<String, String>> hmCurrencyDetails = (Map<String, Map<String, String>>)request.getAttribute("hmCurrencyDetails");
Map<String, String> hmCurr = hmCurrencyDetails.get(hmProjectDetails.get("CURRENCY_ID"));
if(hmCurr==null) hmCurr = new HashMap<String, String>();
String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("")? " ("+hmCurr.get("SHORT_CURR")+")" : "";

Map<String,String> hmEmpMap = (Map<String,String>)request.getAttribute("hmEmpMap");
String emp_count = (String)request.getAttribute("emp_count");

Map<String, List<String>> hmProBillingHeadData = (Map<String, List<String>>)request.getAttribute("hmProBillingHeadData");
Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmProTaxHeadData");
%>




<script>
jQuery(document).ready(function() {
	// binds form submission and fields to the validation engine 
	$("#btnOk").click(function(){
		$('.validateRequired').filter(':hidden').prop('required', false);
		$('.validateRequired').filter(':visible').prop('required', true);
	});
	
	/* var strProjectOwner = document.getElementById("strProjectOwner").value; */
	var strProjectOwner = <%=request.getAttribute("strProjectOwner") %>;
	getProjectOwnerDetails(strProjectOwner);
	
	var proCurrType = document.getElementById("strCurrency").value;
	var btype = document.getElementById("billtype").value;
	getExchangeValue(proCurrType, btype);
});

$(function() {
   /*  $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'}); */
    
    $("#invoiceGenDate").datepicker({format : 'dd/mm/yyyy'});
    
    $("#strStartDateOtherCurr").datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("strStartDateOtherCurr")%>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
		onClose: function(selectedDate) {
			$("#strEndDateOtherCurr").datepicker("option", "minDate", selectedDate);
		}
	});
	
	$("#strEndDateOtherCurr").datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("strStartDateOtherCurr")%>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
		onClose: function(selectedDate) {
			$("#strStartDateOtherCurr").datepicker("option", "maxDate", selectedDate);
		}
	});
    
	$("#strStartDate").datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("strStartDate")%>", maxDate: "<%=request.getAttribute("strEndDate")%>", 
		onClose: function(selectedDate) {
			$("#strEndDate").datepicker("option", "minDate", selectedDate);
		}
	});
	
	$("#strEndDate").datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("strStartDate")%>", maxDate: "<%=request.getAttribute("strEndDate")%>", 
		onClose: function(selectedDate) {
			$("#strStartDate").datepicker("option", "maxDate", selectedDate);
		}
	});
});


function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
      return false;
   }
   return true;
}



function calAmt() {
	var eCount=<%=uF.parseToInt(emp_count) %>;
	var cntAdd = document.getElementById("strPartiCount").value;
	var amt=0;
	var taxamt = 0;
	var totalAmt=0;
	
	for(var i=0; i<=cntAdd; i++) {
		var id = document.getElementById("strOtherParticularsAmt"+i);
		if(id) {
			if(id.value != '') {
				amt = parseFloat(amt) +parseFloat(id.value);
			}
		}
	}
	for(var i=0;i<eCount;i++) {
		var strEmpAmt=document.getElementById("strEmpAmt"+i);
		if(strEmpAmt) {
			if(strEmpAmt.value != '') {
				amt=parseFloat(amt) +parseFloat(strEmpAmt.value);
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
	
	var eCount=<%=uF.parseToInt(emp_count) %>;
	var amt=0;
	var otherCurrAmt = 0;
	
	var totalAmt=0;
	var otherCurrTotalAmt = 0;
	var otherCurrVal = 0;
	var otherCurrOtherPAmt = 0;
	
	var taxamt = 0;
	var otherCurrTaxamt = 0;
	
	var exchangeValue = document.getElementById("exchangeValue").value;
	
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
	
	for(var i=0;i<eCount;i++) {
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



function calEmpAmt(iCnt, btype) {
	//billDailyHours empRate strEmpAmt 
	var eCount=<%=uF.parseToInt(emp_count) %>;
	
	for(var i=0; i<parseInt(eCount); i++) {
		var billDailyHours = document.getElementById("billDailyHours"+i);
		var empRate = document.getElementById("empRate"+i);
		if(billDailyHours && empRate) {
			var billHrs = 0;
			var billHrsSixty = 0;
			var empRt = 0;
			if(billDailyHours.value != '') {
				billHrs = billDailyHours.value;
			}
			if(empRate.value != '') {
				empRt = empRate.value;
			}
			
			if(btype == 'Hours') {
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
			/* if(billDailyHours.value != '' && empRate.value != '') { */
				var amt = parseFloat(billHrs) * parseFloat(empRt);
				document.getElementById("strEmpAmt"+i).value = parseFloat(amt).toFixed(2);
				calAmt();
			/* } */
		}
	}
}



function calEmpAmtOtherCurr(aCnt, btype) {
	//billDailyHours empRate strEmpAmt 
	var eCount=<%=uF.parseToInt(emp_count) %>;
	
	var exchangeValue = document.getElementById("exchangeValue").value;
	//alert("exchangeValue ===>> " + exchangeValue);
	
	for(var i=0; i<parseInt(eCount); i++) {
		var billDailyHours = document.getElementById("billDailyHoursINRCurr"+i);
		var empRate = document.getElementById("empRateINRCurr"+i);
		if(billDailyHours && empRate) {
			var billHrs = 0;
			var billHrsSixty = 0;
			var empRt = 0;
			if(billDailyHours.value != '') {
				billHrs = billDailyHours.value;
			}
			if(empRate.value != '') {
				empRt = empRate.value;
			}
			
			if(btype == 'Hours') {
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
			/* if(billDailyHours.value != '' && empRate.value != '') { */
				var otherCurrAmt = 0;
				var amt = parseFloat(billHrs) * parseFloat(empRt);
				if(parseFloat(exchangeValue) > 0) {
					otherCurrAmt = parseFloat(amt) / parseFloat(exchangeValue);
				}
				
				document.getElementById("strEmpAmtINRCurr"+i).value = parseFloat(amt).toFixed(2);
				document.getElementById("strEmpAmtOtherCurr"+i).value = otherCurrAmt.toFixed(2);
				
				calAmtOtherCurr();
			/* } */
		}
	}
}


function getExchangeValue(value, btype) {
	
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
		 changeCurrencywiseData(value, proCurrency, btype);
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
	
	 
	 
	function changeCurrencywiseData(value, proCurrency, btype) {
		 
		 //getContent('INRCurrencyDIV', 'GetINRCurrencyValue.action?currencyType='+currencyType+'&exchangeValue='+exchangeValue+'&type=U');
		 if(value == proCurrency || value == '' || value == '0') {
			 document.getElementById("INRCurrencyTable").style.display = "block";
			 document.getElementById("otherCurrencyTable").style.display = "none";
			 //INRCurrencyTable otherCurrencyTable
			 calEmpAmt(0, btype);
		 } else {
			 document.getElementById("otherCurrencyTable").style.display = "block";
			 if(document.getElementById("longCurrency")) {
				 var longCurrency = document.getElementById("longCurrency").value;
				 document.getElementById("otherCurrSpan").innerHTML = longCurrency;
			 }
			 document.getElementById("INRCurrencyTable").style.display = "none";
			 calEmpAmtOtherCurr(0, btype);
		 }
	 }
	
	
</script>

<div id="invoiceSummaryid" class="box-body">
	<%
		String btype="";
		if((String)request.getAttribute("bill_type")!=null && String.valueOf(request.getAttribute("bill_type")).equals("Daily")) {
			btype="Days";
		} else if((String)request.getAttribute("bill_type")!=null && String.valueOf(request.getAttribute("bill_type")).equals("Hourly")) {
			btype="Hours";
		}  else if((String)request.getAttribute("bill_type")!=null && String.valueOf(request.getAttribute("bill_type")).equals("Monthly")) {
			btype="Months";
		} 
	%>
<s:form theme="simple" action="ProjectProRetaInvoice" name="frm" id="formID" cssClass="formcss">
<s:hidden name="pro_id" id="pro_id"></s:hidden>
<s:hidden name="pro_freq_id"></s:hidden>
<s:hidden name="bill_type"></s:hidden>
<input type="hidden" name="invoiceType" id="invoiceType" value="1"/>

<table class="table table_bordered">
<tr>
	<td colspan="5" width="70%">
		<div id="ownerDiv"></div>
    </td>
    <td>
    <s:select label="Select Project Owner" name="strProjectOwner" id="strProjectOwner" listKey="employeeId" cssClass="validateRequired" headerKey=""
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
        <td colspan="6">Bill No.<s:textfield name="invoiceCode" id="invoiceCode" cssClass="validateRequired" onkeyup="checkInvoiceCode(this.value);"/> <br/>
        <s:textfield name="invoiceGenDate" id="invoiceGenDate" cssClass="validateRequired" cssStyle="width: 120px !important; margin-left: 43px; margin-top: 5px;" readonly="true"></s:textfield>
        <%-- <%=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),IConstants.DBDATE,CF.getStrReportDateFormat()) %> --%></td>
</tr>
<tr>
        <td colspan="6">To <br/>
        	<s:select theme="simple" name="clientPoc" listKey="clientPocId" listValue="clientPocName" headerKey="" headerValue="Select SPOC" 
        		cssClass="validateRequired" cssStyle="width:200px !important;" list="clientPocList" key="" required="true" /></td>
</tr>
<tr>
        <td colspan="6">
        	<s:select theme="simple" name="clientAddress" listKey="clientAddressId" listValue="clientAddress" headerKey=""
				headerValue="Select Address" cssClass="validateRequired" cssStyle="width:200px !important;" list="clientAddressList" key="" required="true" />
		</td>
</tr>
<tr>
    	<td colspan="6">
        	<div style="float: left;">
        	<s:hidden name="proCurrency" id="proCurrency"/>
        	<input type="hidden" name="billtype" id="billtype" value="<%=btype %>"/>
	        	<s:select id="strCurrency" cssClass="validateRequired" name="strCurrency" listKey="currencyId" listValue="currencyName" headerKey="" 
	        		headerValue="Select Currency" list="currencyList" key="" required="true" cssStyle="width: 200px !important;" onchange="getExchangeValue(this.value, '<%=btype %>');"/>
        	</div>
        	
        	<div id="exchangeValDIV" style="float: left; margin-left: 10px; margin-top: -10px;"> </div>
		</td>
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
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;"> <!-- Professional Fees for rendering Accounting Services for the month of December 2013 --></td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			<tr id="BillPeriodTR" style="display: none;">
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 65px !important;" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDate"  id="strEndDate" cssStyle="width: 65px !important;" cssClass="validateRequired"></s:textfield>)
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
			
			/* String sTaxamtINR = (String)request.getAttribute("sTaxamt");
			String eduCessamtINR = (String)request.getAttribute("eduCessamt");
			String stdCessamtINR = (String)request.getAttribute("stdCessamt");
			String totalBillAmountINR = (String)request.getAttribute("totalBillAmount"); */
			String totBillableamtINR = (String)request.getAttribute("totBillableamt");
			Map<String,List<String>> hmTaskEmpMapINR = (Map<String,List<String>>)request.getAttribute("hmTaskEmpMap");
			Iterator<String> itINR = hmTaskEmpMapINR.keySet().iterator(); 
				int eCount = uF.parseToInt(emp_count);
				int aa=0;
				while(itINR.hasNext()) {
					String taskId = itINR.next();
					List<String> innerList = hmTaskEmpMapINR.get(taskId);
			%>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;">
				<input type="hidden" name="hideTaskId" id="hideTaskId<%=aa %>" value="<%=innerList.get(0) %>" />
					<select name="strEmp" id="strEmp<%=aa %>">
						<option value="">Select Employee</option>
						<%=innerList.get(1) %>
					</select>
					
					<input type="text" name="billDailyHours" id="billDailyHours<%=aa %>" style="width: 30px  !important;" value="<%=innerList.get(2) %>" onkeyup="calEmpAmt('<%=aa %>','<%=btype %>');"  onkeypress="return isNumberKey(event)"/>&nbsp;<%=btype %>
					&nbsp;@&nbsp;<input type="text" name="empRate" id="empRate<%=aa %>" style="width: 30px !important;" value="<%=innerList.get(3) %>" onkeyup="calEmpAmt('<%=aa %>','<%=btype %>');" onkeypress="return isNumberKey(event)"/>
				</td> 
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" > 
				<div style="margin-bottom: 5px;"><input type="text" name="strEmpAmt" id="strEmpAmt<%=aa %>" value="<%=innerList.get(4) %>" style="width: 65px !important; text-align:right;" readonly="readonly"/></div>
				</td> 
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
			</tr> 
			<%
				aa++;
				} %>
			
			<% 
			String strPerticnt = (String)request.getAttribute("strPerticnt");
			if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) { 
				Iterator<String> it = hmProBillingHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProBillingHeadData.get(billingHeadId);
				
			%> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px !important;" id="strOtherParticulars" value="<%=innerList.get(1) %>" name="strOtherParticulars"> <!-- class="validateRequired" -->
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt<%=i %>" style="width: 65px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" 
					<% if(i==0) { %> value="<%=(String)request.getAttribute("reimbursement_amount") %>" <% } %> /> <!-- class="validateRequired" -->
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% i++; 
				} %>
			<% } else { %>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textfield name="strOtherParticulars" id="strOtherParticulars" value="Out of Pocket Expenses" cssStyle="width: 250px !important;"/> <!-- cssClass="validateRequired" -->
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
				<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt0" style="width: 65px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" value="<%=(String)request.getAttribute("reimbursement_amount") %>"/> <!-- class="validateRequired" -->
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% } %>
			
			<tr> 
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;"><input type="text" name="strTotalAmt" id="strTotalAmt" style="width: 65px !important; text-align:right;" value="<%=totBillableamtINR %>" readonly="readonly" class="validateRequired" /></td>
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
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=uF.showData(innerList.get(1),"-") %></td> <%--  @ <%=uF.showData(innerList.get(2),"0") %>% --%>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadAmtDiv<%=i %>" style="text-align: right;"></div></td>
			</tr>
			<% i++; } } %>
			
			<%-- <tr> 
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Service Tax @ <%=uF.showData(hmTaxMiscSetting.get("SERVICE_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="serviceTaxDiv" style="text-align: right;"><%=sTaxamtINR %></div></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Educational Cess @ <%=uF.showData(hmTaxMiscSetting.get("EDUCATION_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="eduCessDiv" style="text-align: right;"><%=eduCessamtINR %></div></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Secondary and Higher Secondary Cess @ <%=uF.showData(hmTaxMiscSetting.get("STANDARD_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="secHighCessDiv" style="text-align: right;"><%=stdCessamtINR %></div></td>
			</tr> --%>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;" >&nbsp;</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
					<textarea name="otherDescription" id="otherDescription" cols="50" rows="2"><%=(String)request.getAttribute("otherDescription") %> </textarea>
					<%-- <s:textarea name="otherDescription" id="otherDescription" cols="50" rows="2" value="TDS is required to be deducted @ 3% as per approval of ITO"></s:textarea> --%>
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
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"><span id="otherCurrSpan"><%=currency %></span></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			
			<tr id="BillPeriodOtherCurrTR" style="display: none;">
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDateOtherCurr" id="strStartDateOtherCurr" cssStyle="width: 65px !important;" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDateOtherCurr"  id="strEndDateOtherCurr" cssStyle="width: 65px !important;" cssClass="validateRequired"></s:textfield>)
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
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
			/* String totalBillAmount = (String)request.getAttribute("totalBillAmount"); */
			String totBillableamt = (String)request.getAttribute("totBillableamt");
			Map<String,List<String>> hmTaskEmpMap = (Map<String,List<String>>)request.getAttribute("hmTaskEmpMap");
			Iterator<String> it = hmTaskEmpMap.keySet().iterator(); 
				int a = 0;
				while(it.hasNext()) {
					String taskId = it.next();
					List<String> innerList = hmTaskEmpMap.get(taskId);
			%>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;">
					<input type="hidden" name="hideTaskIdOtherCurr" id="hideTaskIdOtherCurr<%=a %>" value="<%=innerList.get(0) %>" />
					<select name="strEmpOtherCurr" id="strEmpOtherCurr<%=a %>">
						<option value="">Select Employee</option>
						<%=innerList.get(1) %>
					</select>
					<%-- <%
						String btype="";
						if((String)request.getAttribute("bill_type")!=null && String.valueOf(request.getAttribute("bill_type")).equals("Daily")) {
							btype="Days";
						} else if((String)request.getAttribute("bill_type")!=null && String.valueOf(request.getAttribute("bill_type")).equals("Hourly")) {
							btype="Hours";
						} 
					%> --%>
					<input type="text" name="billDailyHoursINRCurr" id="billDailyHoursINRCurr<%=a %>" style="width: 30px !important;" value="<%=innerList.get(2) %>" onkeyup="calEmpAmtOtherCurr('<%=a %>', '<%=btype %>');"  onkeypress="return isNumberKey(event)"/>&nbsp;<%=btype %>
					&nbsp;@&nbsp;<input type="text" name="empRateINRCurr" id="empRateINRCurr<%=a %>" style="width: 30px !important;" value="<%=innerList.get(3) %>" onkeyup="calEmpAmtOtherCurr('<%=a %>', '<%=btype %>');" onkeypress="return isNumberKey(event)"/>
				</td> 
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" > 
				<div style="margin-bottom: 5px;"><input type="text" name="strEmpAmtINRCurr" id="strEmpAmtINRCurr<%=a %>" value="<%=innerList.get(4) %>" style="width: 65px !important; text-align:right;" readonly="readonly"/></div>
				</td>
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" > 
				<div style="margin-bottom: 5px;"><input type="text" name="strEmpAmtOtherCurr" id="strEmpAmtOtherCurr<%=a %>" style="width: 65px !important; text-align:right;" readonly="readonly"/></div>
				</td> 
			</tr> 
			<%
				a++;
				} %>
			
			<% 
			if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) { 
				Iterator<String> it1 = hmProBillingHeadData.keySet().iterator();
				int i = 0;
				while(it1.hasNext()) {
				String billingHeadId = it1.next();	
				List<String> innerList = hmProBillingHeadData.get(billingHeadId);
				
			%> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width: 250px !important;" id="strOtherParticularsINRCurr" value="<%=innerList.get(1) %>" name="strOtherParticularsINRCurr"> <!-- class="validateRequired" -->
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr<%=i %>" style="width: 65px !important; text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" 
					<% if(i==0) { %> value="<%=(String)request.getAttribute("reimbursement_amount") %>" <% } %> /> <!-- class="validateRequired" -->
				</div>
				</td> 
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr<%=i %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
				</div>
				</td> 
			</tr> 
			<% i++; 
				} %>
			<% } else { %>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textfield name="strOtherParticularsINRCurr" id="strOtherParticularsINRCurr" value="Out of Pocket Expenses" cssStyle="width: 250px !important;"/> <!-- cssClass="validateRequired" -->
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
				<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr0" style="width: 65px !important; text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" value="<%=(String)request.getAttribute("reimbursement_amount") %>"/> <!-- class="validateRequired" -->
				</div>
				</td> 
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr0" style="width: 65px !important; text-align:right;" readonly="readonly"/>
				</div>
				</td> 
			</tr> 
			<% } %>
			
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtINRCurr" id="strTotalAmtINRCurr" cssStyle="width: 65px !important; text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtOtherCurr" id="strTotalAmtOtherCurr" cssStyle="width: 65px !important; text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
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
					<s:textarea name="proDescriptionOtherCurr" id="proDescriptionOtherCurr" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			
			<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it1 = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it1.hasNext()) {
				String taxHeadId = it1.next();	
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
				%>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=uF.showData(innerList.get(1),"-") %></td> <%--  @ <%=uF.showData(innerList.get(2),"0") %>% --%>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadINRAmtDiv<%=i %>" style="text-align: right;"></div></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadOtherAmtDiv<%=i %>" style="text-align: right;"></div></td>
			</tr>
			<% i++; } } %>
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;" >&nbsp;</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
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
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totalINRAmtdiv" style="text-align: right;"></div></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totalOtherCurrdiv" style="text-align: right;"></div></td>
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
	        <div id="ownerSignDiv">
		       
	        </div>
        </td>
        
    	<td colspan="3">
		    <%-- <div style="float: left;">
		    	<span id="ownerLocationECC1Div" style="float: left; width: 100%; margin-left: 72px;"></span>
		    	<span style="float: left; width: 100%;">ECC Code: -----------------------------</span>
		    	<span id="ownerLocationECC2Div" style="float: left; width: 100%; margin-left: 72px;"></span>
		    </div>
			<div style="float: left; width: 100%;">PAN: <span id="ownerLocationPanDiv"></span></div>
			<div style="float: left; width: 100%;">REGN. NO.: <span id="ownerLocationRegNoDiv"></span></div> --%>
			
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
        	
        	<s:submit value="Submit" cssClass="btn btn-primary" id="btnOK" name="submit" ></s:submit>
        </td>
</tr>


</table>
</s:form>

<script type="text/javascript">
jQuery(document).ready(function() {
	 	calEmpAmt(0, '');
	 });
</script>

</div>
    


