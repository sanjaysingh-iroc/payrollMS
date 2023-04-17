<%@ page import="com.konnect.jpms.util.UtilityFunctions" %>
<%@ page import="com.konnect.jpms.util.IConstants" %>
<%@ page import="com.konnect.jpms.util.CommonFunctions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
	
<%
Map<String,String> hmProjectDetails = (Map<String,String>)request.getAttribute("hmProjectDetails");

Map<String,String> hmTaxMiscSetting = (Map<String,String>)request.getAttribute("hmTaxMiscSetting");
UtilityFunctions uF = new UtilityFunctions();

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
Map<String, Map<String, String>> hmCurrencyDetails = (Map<String, Map<String, String>>)request.getAttribute("hmCurrencyDetails");
Map<String, String> hmCurr = hmCurrencyDetails.get(hmProjectDetails.get("CURRENCY_ID"));
if(hmCurr==null) hmCurr = new HashMap<String, String>();
String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("")? hmCurr.get("SHORT_CURR") : "";

Map<String,String> hmEmpMap = (Map<String,String>)request.getAttribute("hmEmpMap");
String emp_count = (String)request.getAttribute("emp_count");

Map<String, List<String>> hmProBillingHeadData = (Map<String, List<String>>)request.getAttribute("hmProBillingHeadData");
Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmProTaxHeadData");

Map<String, String> hmOrgData = (Map<String, String>) request.getAttribute("hmOrgData");
Map<String,String> hmClientDetails = (Map<String,String>) request.getAttribute("hmClientDetails");

Map<String, String> hmBankAccData = (Map<String, String>) request.getAttribute("hmBankAccData");
%>



<script>
$(function() {
	// binds form submission and fields to the validation engine 
	$("#btnOk").click(function(){
		$('.validateRequired').filter(':hidden').prop('required', false);
		$('.validateRequired').filter(':visible').prop('required', true);
	});
	
	$("#invoiceGenDate").datepicker({format : 'dd/mm/yyyy'});
	
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
    
   <%--  $("#strStartDateOtherCurr").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDateOtherCurr")%>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
		onClose: function(selectedDate) {
			$("#strEndDateOtherCurr").datepicker("option", "minDate", selectedDate);
		}
	});
	
	$("#strEndDateOtherCurr").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDateOtherCurr")%>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
		onClose: function(selectedDate) {
			$("#strStartDateOtherCurr").datepicker("option", "maxDate", selectedDate);
		}
	});
    
	$("#strStartDate").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDate")%>", maxDate: "<%=request.getAttribute("strEndDate")%>", 
		onClose: function(selectedDate) {
			$("#strEndDate").datepicker("option", "minDate", selectedDate);
		}
	});
	
	$("#strEndDate").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDate")%>", maxDate: "<%=request.getAttribute("strEndDate")%>", 
		onClose: function(selectedDate) {
			$("#strStartDate").datepicker("option", "maxDate", selectedDate);
		}
	}); --%>
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
	//alert("cntAdd ===> " + cntAdd + " amt ====>> " + amt);
	
	for(var i=0;i<eCount;i++) {
		var strEmpAmt=document.getElementById("strEmpAmt"+i);
		if(strEmpAmt) {
			if(strEmpAmt.value != '') {
				amt=parseFloat(amt) +parseFloat(strEmpAmt.value);
			}
		}
	}
	//alert(" amt ====>> " + amt);
	//amt=parseFloat(amt);
	
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
	//alert(" cntOtherAdd ===>> " + cntOtherAdd);
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
			//alert("3 ");
				var amt = parseFloat(billHrs) * parseFloat(empRt);
				document.getElementById("strEmpAmt"+i).value = parseFloat(amt).toFixed(2);
				//alert("4 ");
				calAmt();
				//alert("5 ");
		}
	}
}



function calEmpAmtOtherCurr(aCnt, btype) {
	var eCount=<%=uF.parseToInt(emp_count) %>;
	
	var exchangeValue = document.getElementById("exchangeValue").value;
	
	for(var i=0; i<parseInt(eCount); i++) {
		var billDailyHours = document.getElementById("billDailyHoursINRCurr"+i);
		var empRate = document.getElementById("empRateINRCurr"+i);
		//alert("billDailyHours ===>> " + billDailyHours + " --- empRate ===>> " + empRate)
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
			//alert("btype ===>> " + btype);
				var otherCurrAmt = 0;
				var amt = parseFloat(billHrs) * parseFloat(empRt);
				if(parseFloat(exchangeValue) > 0) {
					otherCurrAmt = parseFloat(amt) / parseFloat(exchangeValue);
				}
				
				document.getElementById("strEmpAmtINRCurr"+i).value = parseFloat(amt).toFixed(2);
				document.getElementById("strEmpAmtOtherCurr"+i).value = otherCurrAmt.toFixed(2);
				//alert(" ---1 ");
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
				String invoice_format_id = (String) request.getAttribute("invoice_format_id");
			
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
	<s:hidden name="invoice_format_id"></s:hidden>
	<s:hidden name="clientPoc" id="clientPoc"></s:hidden>
	<s:hidden name="strProjectOwner" id="strProjectOwner"></s:hidden>
	<s:hidden name="bankName" id="bankName"></s:hidden>
	<s:hidden name="payPalMailId" id="payPalMailId"></s:hidden>
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
        	<input type="hidden" name="billtype" id="billtype" value="<%=btype %>"/>
	        	<s:select id="strCurrency" cssClass="validateRequired" name="strCurrency" listKey="currencyId" listValue="currencyName" headerKey="" 
	        		headerValue="Select Currency" list="currencyList" key="" required="true" cssStyle="width: 200px !important;" onchange="getExchangeValue(this.value, '<%=btype %>');"/>
        	</div>
        	
		<div id="exchangeValDIV" style="float: left; margin-left: 10px;"> </div>
    </div>	
	
	<% if(uF.parseToInt(invoice_format_id) == 1) { %>
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
					<div style="float: left; margin-left: 10px;"><%=hmProjectDetails.get("PRO_ACCOUNT_REF") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=hmProjectDetails.get("PRO_PO_NO") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=hmProjectDetails.get("PRO_TERMS") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=hmProjectDetails.get("PRO_BILL_DUEDATE") %></div>
				</div>
				<div style="float: left; width: 20%;">
					<div style="float: left; margin-left: 10px;"><%=hmProjectDetails.get("PRO_NAME") %></div>
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
			<div style="float: left; width: 44%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Description</div>
			</div>
			<div style="float: left; width: 15%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Qty/<%=btype %></div>
			</div>
			<div style="float: left; width: 15%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Rate (<%=currency %>)</div>
			</div>
			<div style="float: left; width: 20%;">
				<input type="hidden" name="strPartiCount" id="strPartiCount" value="<%=(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) ? hmProBillingHeadData.size() : "1" %>">
				<s:hidden name="strStartDate" id="strStartDate"></s:hidden>
				<s:hidden name="strEndDate" id="strEndDate"></s:hidden>
				
				<div style="float: right; font-weight: bold; margin-right: 20px;">Amount (<%=currency %>)</div>
			</div>
		</div>
		
		<%
			String totBillableamtINR = (String)request.getAttribute("totBillableamt");
			Map<String,List<String>> hmTaskEmpMapINR = (Map<String,List<String>>)request.getAttribute("hmTaskEmpMap");
			Iterator<String> itINR = hmTaskEmpMapINR.keySet().iterator(); 
				int eCount = uF.parseToInt(emp_count);
				int aa=0;
				while(itINR.hasNext()) {
					String taskId = itINR.next();
					List<String> innerList = hmTaskEmpMapINR.get(taskId);
			%>
			
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
			<input type="hidden" name="hideTaskId" id="hideTaskId<%=aa %>" value="<%=innerList.get(0) %>" />
			<%-- <input type="hidden" name="strEmp" id="strEmp<%=aa %>" value="<%=innerList.get(1) %>" /> --%>
			<div style="float: left; width: 6%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 44%;">
				<div style="float: left; margin-left: 10px;">
					1.<%=aa+1 %>) <select name="strEmp" id="strEmp<%=aa %>"><option value="">Select Employee</option><%=innerList.get(1) %></select>
				</div>
			</div>
			<div style="float: left; width: 15%;">
				<div style="float: left; margin-left: 10px;">
					<input type="text" name="billDailyHours" id="billDailyHours<%=aa %>" style="width: 50px !important;" value="<%=innerList.get(2) %>" onkeyup="calEmpAmt('<%=aa %>','<%=btype %>');"  onkeypress="return isNumberKey(event)"/>
				</div>
			</div>
			<div style="float: left; width: 15%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="empRate" id="empRate<%=aa %>" style="width: 50px !important;" value="<%=innerList.get(3) %>" onkeyup="calEmpAmt('<%=aa %>','<%=btype %>');" onkeypress="return isNumberKey(event)"/>
				</div>
			</div>
			<div style="float: left; width: 20%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strEmpAmt" id="strEmpAmt<%=aa %>" value="<%=innerList.get(4) %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
				</div>
			</div>
		</div>
		
		<% aa++;
			} %>
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 9px;">
		<% 
			String strPerticnt = (String)request.getAttribute("strPerticnt");
			if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) { 
				Iterator<String> it = hmProBillingHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProBillingHeadData.get(billingHeadId);
			%> 
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
			<div style="float: left; width: 6%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 59%;">
				<div style="float: left; margin-left: 10px;">
					<input type="text" style="width:250px" id="strOtherParticulars" value="<%=innerList.get(1) %>" name="strOtherParticulars"> <!-- class="validateRequired" -->
				</div>
			</div>
			<div style="float: left; width: 15%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 20%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt<%=i %>" style="width: 65px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" 
					<% if(i==0) { %> value="<%=(String)request.getAttribute("reimbursement_amount") %>" <% } %> /> <!-- class="validateRequired" -->
				</div>
			</div>
		</div>
		
		<% i++;
			} } %>
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
					<input type="text" name="strTotalAmt" id="strTotalAmt" style="width: 65px !important; text-align:right;" value="<%=totBillableamtINR %>" readonly="readonly" class="validateRequired" />
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
				<div id="totaldiv" style="float: right; margin-right: 10px;"></div>
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
			<div style="float: left; width: 34%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Description</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Qty/<%=btype %></div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; font-weight: bold; margin-left: 20px;">Rate (<%=currency %>)</div>
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
			String totBillableamt = (String)request.getAttribute("totBillableamt");
			Map<String,List<String>> hmTaskEmpMap = (Map<String,List<String>>)request.getAttribute("hmTaskEmpMap");
			Iterator<String> it = hmTaskEmpMap.keySet().iterator(); 
				int a = 0;
				while(it.hasNext()) {
					String taskId = it.next();
					List<String> innerList = hmTaskEmpMap.get(taskId);
			%>
			
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
			<input type="hidden" name="hideTaskIdOtherCurr" id="hideTaskIdOtherCurr<%=a %>" value="<%=innerList.get(0) %>" />
			<%-- <input type="hidden" name="strEmp" id="strEmp<%=aa %>" value="<%=innerList.get(1) %>" /> --%>
			<div style="float: left; width: 6%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 34%;">
				<div style="float: left; margin-left: 10px;">
					1.<%=a+1 %>) <select name="strEmpOtherCurr" id="strEmpOtherCurr<%=a %>"><option value="">Select Employee</option><%=innerList.get(1) %></select>
				</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; margin-left: 10px;">
					<input type="text" name="billDailyHoursINRCurr" id="billDailyHoursINRCurr<%=a %>" style="width: 50px !important;" value="<%=innerList.get(2) %>" onkeyup="calEmpAmtOtherCurr('<%=a %>','<%=btype %>');"  onkeypress="return isNumberKey(event)"/>
				</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; margin-left: 10px;">
					<input type="text" name="empRateINRCurr" id="empRateINRCurr<%=a %>" style="width: 50px !important;" value="<%=innerList.get(3) %>" onkeyup="calEmpAmtOtherCurr('<%=a %>','<%=btype %>');" onkeypress="return isNumberKey(event)"/>
				</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strEmpAmtINRCurr" id="strEmpAmtINRCurr<%=a %>" value="<%=innerList.get(4) %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
				</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strEmpAmtOtherCurr" id="strEmpAmtOtherCurr<%=a %>" value="<%=innerList.get(4) %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
				</div>
			</div>
		</div>
		
		<% a++;
			} %>
	</div>
	
	
	<div style="width: 100%; float: left; margin-bottom: 9px;">
		<% 
			if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) { 
				Iterator<String> it1 = hmProBillingHeadData.keySet().iterator();
				int i = 0;
				while(it1.hasNext()) {
				String billingHeadId = it1.next();	
				List<String> innerList = hmProBillingHeadData.get(billingHeadId);
			%> 
		<div style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
			<div style="float: left; width: 6%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 46%;">
				<div style="float: left; margin-left: 10px;">
					<input type="text" style="width: 250px !important;" id="strOtherParticularsINRCurr" value="<%=innerList.get(1) %>" name="strOtherParticularsINRCurr"> <!-- class="validateRequired" -->
				</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr<%=i %>" style="width: 65px !important; text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" 
					<% if(i==0) { %> value="<%=(String)request.getAttribute("reimbursement_amount") %>" <% } %> /> <!-- class="validateRequired" -->
				</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr<%=i %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
				</div>
			</div>
		</div>
		
		<% i++;
			} } %>
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
				<div id="totalOtherCurrdiv" style="float: right; margin-right: 55px;"></div>
			</div>
		</div>
		
	</div>
</div>
	<!-- ************************************** Other CurrencyTable End *************************************** -->
	
	<div style="width: 100%; float: left; color: #404040; margin-bottom: 15px;">
		<div style="float: left; width: 100%;">
			<div style="float: left; margin-left: 10px;">Payment Mode:</div>
		</div>
		<% if(uF.parseToInt(hmProjectDetails.get("PRO_BRANCH_ID")) > 0) { %>
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
		<% } %>
		<% if(hmProjectDetails.get("PRO_PAYPAL_MAIL_ID") != null && !hmProjectDetails.get("PRO_PAYPAL_MAIL_ID").equals("")) { %>
			<div style="float: left; width: 100%;">
				<div style="float: left; margin-left: 20px;">Paypal: <%=hmProjectDetails.get("PRO_PAYPAL_MAIL_ID") %></div>
			</div>
		<% } %>
	</div>


	<div style="width: 100%; float: left; margin-bottom: 15px;">
		<div style="float: left; width: 100%;">
			<div style="float: left; margin-left: 10px;">
				<textarea name="otherDescription" id="otherDescription" cols="85" rows="3"><%=(String)request.getAttribute("otherDescription") %> </textarea>
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
	jQuery(document).ready(function() {
	 	calEmpAmt(0, '');
	 });
</script>

</div>
  