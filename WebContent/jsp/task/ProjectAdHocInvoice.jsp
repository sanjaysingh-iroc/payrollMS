<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
	
<%
UtilityFunctions uF = new UtilityFunctions();

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
Map<String, Map<String, String>> hmCurrencyDetails = (Map<String, Map<String, String>>) request.getAttribute("hmCurrencyDetails");
Map<String, String> hmCurr = hmCurrencyDetails.get(IConstants.INR_CURR_ID);
if(hmCurr == null)hmCurr = new HashMap<String, String>();
String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";

Map<String, List<String>> hmProBillingHeadData = (Map<String, List<String>>)request.getAttribute("hmProBillingHeadData");
Map<String, List<String>> hmProRataProBillingHeadData = (Map<String, List<String>>)request.getAttribute("hmProRataProBillingHeadData");
Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>)request.getAttribute("hmProTaxHeadData");

List<List<String>> resourcesList = (List<List<String>>)request.getAttribute("resourcesList");
List<List<String>> particularsList = (List<List<String>>)request.getAttribute("particularsList");
List<List<String>> otherPartiListProrata = (List<List<String>>)request.getAttribute("otherPartiListProrata");
List<List<String>> taxHeadsList = (List<List<String>>)request.getAttribute("taxHeadsList");

List<List<String>> resourcesListOtherCurr = (List<List<String>>)request.getAttribute("resourcesListOtherCurr");
List<List<String>> particularsListOtherCurr = (List<List<String>>)request.getAttribute("particularsListOtherCurr");
List<List<String>> otherPartiListOtherCurrProrata = (List<List<String>>)request.getAttribute("otherPartiListOtherCurrProrata");
List<List<String>> taxHeadsListOtherCurr = (List<List<String>>)request.getAttribute("taxHeadsListOtherCurr");

String operation = (String)request.getAttribute("operation");
%>
<script>
jQuery(document).ready(function() {
	// binds form submission and fields to the validation engine
	jQuery("#formProjectAdHocInvoice").validationEngine();
	
	var strProjectOwner = document.getElementById("strProjectOwner").value;
	if(parseInt(strProjectOwner) > 0) {
		getProjectOwnerDetails(strProjectOwner);
	}
	
	var strInvcCurr = document.getElementById("strCurrency").value;
	getExchangeValue(strInvcCurr, 'E');
	
	changeCurrencywiseData();
	
});

$(function() {
   /*  $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'}); */
    
    $("#invoiceGenDate").datepicker({dateFormat : 'dd/mm/yy'});
    
    $("#strStartDateOtherCurr").datepicker({
		dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("strStartDateOtherCurr") %>", maxDate: "<%=request.getAttribute("strEndDateOtherCurr")%>", 
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
	});
});


function addAmtType() { 
	var cntAdd = document.getElementById("strPartiCount").value;
	
	cntAdd++;
    var divTag = document.createElement("div");
    divTag.id = "p_add"+cntAdd; 
    divTag.setAttribute("style","float:left; height: 50px; margin-top:5px;");
   /*  divTag.innerHTML = "<input type=\"text\" style=\"width:250px;\" id=\"strParticulars\" name=\"strParticulars\" class=\"validateRequired\" value=\"\"/>"+
    "<span style=\"float:right;\"><a title=\"Add\" class=\"add\" onclick=\"addAmtType()\" href=\"javascript:void(0)\">Add</a>"+
    "<a href=\"javascript:void(0)\"  onclick=\"removeAddress(this.id)\" id=\""+cntAdd+"\" class=\"remove\">Remove</a></span>"; */
    divTag.innerHTML = "<span style=\"float:left;\"><textarea cols=\"27\" rows=\"2\" id=\"strParticulars\" name=\"strParticulars\" class=\"validateRequired\"></textarea></span>"+ //
    "<span style=\"float:left;\"><a title=\"Add\" class=\"add\" onclick=\"addAmtType()\" href=\"javascript:void(0)\">Add</a>"+
    "<a href=\"javascript:void(0)\"  onclick=\"removeAddress(this.id)\" id=\""+cntAdd+"\" class=\"remove\">Remove</a></span>";
    
    document.getElementById("particularsID").appendChild(divTag);
    
    var divTag1 = document.createElement("div");
    divTag1.id = "pamt_add"+cntAdd; 
    divTag1.setAttribute("style","height: 50px; margin-top: 5px;");
    divTag1.innerHTML = "<input type=\"text\" style=\"width:65px; text-align:right;\" id=\"strParticularsAmt"+cntAdd+"\" name=\"strParticularsAmt\" class=\"validateRequired\" onkeyup=\"calAmt();\" onkeypress=\"return isNumberKey(event)\" value=\"\"/>"; //class=\"validateRequired\" 
		
    document.getElementById("particularsAmtID").appendChild(divTag1);
    document.getElementById("strPartiCount").value = cntAdd;
}



function addNewEmpBillOtherCurr() {
	var cntProrataAddOCurr = document.getElementById("strResourceCountOtherCurrProRata").value;
	cntProrataAddOCurr++;
    var divTag = document.createElement("div");
    divTag.id = "strEmpINRCurrDIV"+cntProrataAddOCurr; 
    divTag.setAttribute("style","float:left;");
   
    divTag.innerHTML = "<span style=\"float: left;\"><input type=\"text\" name=\"strEmpOtherCurr\" id=\"strEmpOtherCurr"+cntProrataAddOCurr+"\" />&nbsp;"+
    "&nbsp;<input type=\"text\" name=\"billDailyHoursINRCurr\" id=\"billDailyHoursINRCurr"+cntProrataAddOCurr+"\" style=\"width:30px;\" onkeyup=\"calEmpAmtOtherCurr();\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;"+
    "&nbsp;&nbsp;<select name=\"billDaysHoursINRCurr\" id=\"billDaysHoursINRCurr"+cntProrataAddOCurr+"\"  style=\"width:70px;\"><option value=\"\">Select</option><option value=\"1\">Days</option>"
    +"<option value=\"2\">Hours</option></select>"+
    "&nbsp;@&nbsp;<input type=\"text\" name=\"empRateINRCurr\" id=\"empRateINRCurr"+cntProrataAddOCurr+"\" style=\"width:30px;\" onkeyup=\"calEmpAmtOtherCurr();\" onkeypress=\"return isNumberKey(event)\"/>"+
    "</span>"+
	"<span style=\"float:right;margin-right:25px;margin-top:-9px;\"><a title=\"Add\" class=\"add\" onclick=\"addNewEmpBillOtherCurr()\" href=\"javascript:void(0)\">Add</a>"+
    "<a href=\"javascript:void(0)\"  onclick=\"removeNewEmpBillOtherCurr(this.id)\" id=\""+cntProrataAddOCurr+"\" class=\"remove\">Remove</a></span>";
    
    document.getElementById("strEmpINRCurrTD").appendChild(divTag);
    
    var divTag1 = document.createElement("div");
    divTag1.id = "strEmpAmtINRCurrDIV"+cntProrataAddOCurr; 
    divTag1.setAttribute("style","float:right;margin-bottom: 10px;");
    divTag1.innerHTML = "<input type=\"text\" name=\"strEmpAmtINRCurr\" id=\"strEmpAmtINRCurr"+cntProrataAddOCurr+"\" style=\"width:65px;text-align:right;\" readonly=\"readonly\"/>"; 
	
    document.getElementById("strEmpAmtINRCurrTD").appendChild(divTag1);
    
    var divTag2 = document.createElement("div");
    divTag2.id = "strEmpAmtOtherCurrDIV"+cntProrataAddOCurr; 
    divTag2.setAttribute("style","float:right;margin-bottom: 10px;");
    divTag2.innerHTML = "<input type=\"text\" name=\"strEmpAmtOtherCurr\" id=\"strEmpAmtOtherCurr"+cntProrataAddOCurr+"\" style=\"width:65px;text-align:right;\" readonly=\"readonly\"/>"; 
	
    document.getElementById("strEmpAmtOtherCurrTD").appendChild(divTag2);
    document.getElementById("strResourceCountOtherCurrProRata").value = cntProrataAddOCurr;
}


function removeNewEmpBillOtherCurr(removeId) {
    var remove_elem = "strEmpINRCurrDIV"+removeId;
    var row_skill = document.getElementById(remove_elem);
    document.getElementById("strEmpINRCurrTD").removeChild(row_skill);
    
    var remove_elem1 = "strEmpAmtINRCurrDIV"+removeId;
    var row_skill1 = document.getElementById(remove_elem1);
    document.getElementById("strEmpAmtINRCurrTD").removeChild(row_skill1);
    
    var remove_elem2 = "strEmpAmtOtherCurrDIV"+removeId;
    var row_skill2 = document.getElementById(remove_elem2);
    document.getElementById("strEmpAmtOtherCurrTD").removeChild(row_skill2);
    
    calEmpAmtOtherCurr();
}


function calEmpAmtOtherCurr() {
	//billDailyHours empRate strEmpAmt
	
	var cntProrataAddOCurr = document.getElementById("strResourceCountOtherCurrProRata").value;
	
	//alert("cntProrataAddOCurr ===>> " + cntProrataAddOCurr);
	var exchangeValue = 0;
	if(document.getElementById("exchangeValue")) {
		exchangeValue = document.getElementById("exchangeValue").value;
	}
	//alert("exchangeValue ===>> " + exchangeValue);
	
	for(var i=0; i<=parseInt(cntProrataAddOCurr); i++) {
		var billDailyHours = document.getElementById("billDailyHoursINRCurr"+i);
		var billDaysHours = document.getElementById("billDaysHoursINRCurr"+i);
		var empRate = document.getElementById("empRateINRCurr"+i);
		//alert("billDailyHours ===>> " + billDailyHours+ "  empRate ===>> " + empRate);
		if(billDailyHours && empRate && billDaysHours) {
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
				
				document.getElementById("strEmpAmtINRCurr"+i).value = amt.toFixed(1);
				document.getElementById("strEmpAmtOtherCurr"+i).value = otherCurrAmt.toFixed(2);
//				alert("otherCurrAmt ===>> " + otherCurrAmt);
				calProrataAmtOtherCurr();
		}
	}
}



function calProrataAmtOtherCurr() {
	/* var amt=0;
	var totalAmt=0;
	var otherCurrTotalAmt = 0;
	var otherCurrVal = 0;
	var otherCurrOtherPAmt = 0; */
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





function addNewEmpBill() { 
	
	var cntProrataAdd = document.getElementById("strResourceCountProRata").value;
	cntProrataAdd++;
    var divTag = document.createElement("div");
    divTag.id = "strEmpDIV"+cntProrataAdd; 
    divTag.setAttribute("style","float:left;");
   
    divTag.innerHTML = "<span style=\"float: left;\"><input type=\"text\" name=\"strEmp\" id=\"strEmp"+cntProrataAdd+"\" />&nbsp;"+
    "&nbsp;<input type=\"text\" name=\"billDailyHours\" id=\"billDailyHours"+cntProrataAdd+"\" style=\"width:30px;\" onkeyup=\"calEmpAmt();\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;"+
    "&nbsp;&nbsp;<select name=\"billDaysHours\" id=\"billDaysHours"+cntProrataAdd+"\"  style=\"width:70px;\"><option value=\"\">Select</option><option value=\"1\">Days</option>"
    +"<option value=\"2\">Hours</option></select>"+
    "&nbsp;@&nbsp;<input type=\"text\" name=\"empRate\" id=\"empRate"+cntProrataAdd+"\" style=\"width:30px;\" onkeyup=\"calEmpAmt();\" onkeypress=\"return isNumberKey(event)\"/>"+
    "</span>"+
	"<span style=\"float:right;margin-right:25px;margin-top:-9px;\"><a title=\"Add\" class=\"add\" onclick=\"addNewEmpBill()\" href=\"javascript:void(0)\">Add</a>"+
    "<a href=\"javascript:void(0)\"  onclick=\"removeNewEmpBill(this.id)\" id=\""+cntProrataAdd+"\" class=\"remove\">Remove</a></span>";
    
    document.getElementById("strEmpTD").appendChild(divTag);
    
    var divTag1 = document.createElement("div");
    divTag1.id = "strEmpAmtDIV"+cntProrataAdd; 
    divTag1.setAttribute("style","float:right;margin-bottom: 10px;");
    divTag1.innerHTML = "<input type=\"text\" name=\"strEmpAmt\" id=\"strEmpAmt"+cntProrataAdd+"\" style=\"width:65px;text-align:right;\" readonly=\"readonly\"/>"; 
	
    document.getElementById("strEmpAmtTD").appendChild(divTag1);
    document.getElementById("strResourceCountProRata").value = cntProrataAdd;
}


function removeNewEmpBill(removeId) {
    var remove_elem = "strEmpDIV"+removeId;
    var row_skill = document.getElementById(remove_elem);
    document.getElementById("strEmpTD").removeChild(row_skill);
    
    var remove_elem1 = "strEmpAmtDIV"+removeId;
    var row_skill1 = document.getElementById(remove_elem1);
    document.getElementById("strEmpAmtTD").removeChild(row_skill1);
    
    calEmpAmt();
}


function calEmpAmt() {
	
	var cntProrataAdd = document.getElementById("strResourceCountProRata").value;
	for(var i=0; i<=parseInt(cntProrataAdd); i++) {
		var billDailyHours = document.getElementById("billDailyHours"+i);
		var billDaysHours = document.getElementById("billDaysHours"+i);
		var empRate = document.getElementById("empRate"+i);
		if(billDailyHours && empRate && billDaysHours) {
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
				
				document.getElementById("strEmpAmt"+i).value = amt.toFixed(1);
				calProrataAmt();
		}
	}
}



function addOtherCurrType() { 
	var cntOtherAdd = document.getElementById("strPartiCountOtherCurr").value;
	cntOtherAdd++;
    var divTag = document.createElement("div");
    divTag.id = "p_add" + cntOtherAdd;
    divTag.setAttribute("style","float:left; height: 50px; margin-top:5px;");
    divTag.innerHTML = "<span style=\"float:left;\"><textarea cols=\"27\" rows=\"2\" id=\"strParticularsINRCurr\" name=\"strParticularsINRCurr\" class=\"validateRequired\" /></textarea></span>"+ //
    "<span style=\"float:left;\"><a title=\"Add\" class=\"add\" onclick=\"addOtherCurrType()\" href=\"javascript:void(0)\">Add</a>"+
    "<a href=\"javascript:void(0)\" onclick=\"removeAddressOtherCurr(this.id)\" id=\""+cntOtherAdd+"\" class=\"remove\">Remove</a></span>";
		
    document.getElementById("particularsOtherCurrID").appendChild(divTag);
    
    var divTag1 = document.createElement("div");
    divTag1.id = "pamt_add"+cntOtherAdd; 
    divTag1.setAttribute("style","height: 50px; margin-top:5px;");
    divTag1.innerHTML = "<input type=\"text\" style=\"width:65px;text-align:right;\" id=\"strParticularsAmtINRCurr"+cntOtherAdd+"\" name=\"strParticularsAmtINRCurr\" class=\"validateRequired\" onkeyup=\"calAmtOtherCurr();\" onkeypress=\"return isNumberKey(event)\" value=\"\"/>"; //class=\"validateRequired\" 
		
    document.getElementById("particularsAmtINRCurrID").appendChild(divTag1);
    
    var divTag2 = document.createElement("div");
    divTag2.id = "pOCamt_add"+cntOtherAdd; 
    divTag2.setAttribute("style","height: 50px; margin-top:5px;");
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
<%-- var sercviceTax=<%=uF.showData(hmTaxMiscSetting.get("SERVICE_TAX"),"0") %>;
var eduCess=<%=uF.showData(hmTaxMiscSetting.get("EDUCATION_TAX"),"0") %>;
var standardTax=<%=uF.showData(hmTaxMiscSetting.get("STANDARD_TAX"),"0") %>; --%>


/* function calAmt() {
	
	var cntAdd = document.getElementById("strPartiCount").value;
	
	var amt=0;
	var sTaxamt=0;
	var eduCessamt=0;
	var stdCessamt=0;
	var totalAmt=0;
	
	for(var i=0;i<=cntAdd;i++){
		var id=document.getElementById("strParticularsAmt"+i);
		if(id){
			if(id.value != ''){
				amt=parseFloat(amt) +parseFloat(id.value);
			}
		}
	}

	amt=Math.round(parseFloat(amt));
	
	document.getElementById("strTotalAmt").value=parseFloat(amt);
	
	sTaxamt=(parseFloat(amt)*parseFloat(sercviceTax))/100;
	sTaxamt=Math.round(parseFloat(sTaxamt));
	
	eduCessamt=(parseFloat(sTaxamt)*parseFloat(eduCess))/100;
	stdCessamt=(parseFloat(sTaxamt)*parseFloat(standardTax))/100;
	
	
	eduCessamt=Math.round(parseFloat(eduCessamt));
	stdCessamt=Math.round(parseFloat(stdCessamt));
		
	totalAmt=parseFloat(amt)+parseFloat(sTaxamt)+parseFloat(eduCessamt)+parseFloat(stdCessamt);
	
	document.getElementById("serviceTaxDiv").innerHTML = parseFloat(sTaxamt).toFixed(2);
	document.getElementById("eduCessDiv").innerHTML = parseFloat(eduCessamt).toFixed(2);
	document.getElementById("secHighCessDiv").innerHTML = parseFloat(stdCessamt).toFixed(2);
	document.getElementById("totaldiv").innerHTML = parseFloat(totalAmt).toFixed(2);
	
	document.getElementById("serviceTaxAmt").value = parseFloat(sTaxamt).toFixed(2);
	document.getElementById("eduCessAmt").value = parseFloat(eduCessamt).toFixed(2);
	document.getElementById("stdTaxAmt").value = parseFloat(stdCessamt).toFixed(2);
	
	document.getElementById("particularTotalAmt").value = parseFloat(amt).toFixed(2);
	document.getElementById("totalAmt").value = parseFloat(totalAmt).toFixed(2);
} */


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



function calProrataAmt() {
	/* var amt=0;
	var sTaxamt=0;
	var eduCessamt=0;
	var stdCessamt=0;
	var totalAmt=0; */
	
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



/* function calAmtOtherCurr() {
	
	var cntOtherAdd = document.getElementById("strPartiCountOtherCurr").value;
	
	var amt = 0;
	var totalAmt = 0;
	var otherCurrTotalAmt = 0;
	var otherCurrVal = 0;
	var otherCurrOtherPAmt = 0;
	
	var exchangeValue = 0;
	if(document.getElementById("exchangeValue")) {
		exchangeValue = document.getElementById("exchangeValue").value;
	}
	
	for(var i=0;i<=cntOtherAdd;i++) {
		var id = document.getElementById("strParticularsAmtINRCurr"+i);
		if(id) {
			if(id.value != '') {
				if(parseFloat(exchangeValue) > 0) {
					otherCurrVal = parseFloat(id.value) / parseFloat(exchangeValue);
				}
				document.getElementById("strParticularsAmtOtherCurr"+i).value = parseFloat(otherCurrVal).toFixed(2);
				amt = parseFloat(amt) + parseFloat(id.value);
				otherCurrTotalAmt = parseFloat(otherCurrTotalAmt) + parseFloat(otherCurrVal);
			}
		}
	}
		
	amt = Math.round(parseFloat(amt));
	otherCurrTotalAmt = Math.round(parseFloat(otherCurrTotalAmt));
	
	totalAmt = parseFloat(amt);
	
	document.getElementById("totalINRCurrdiv").innerHTML = parseFloat(totalAmt).toFixed(2);
	document.getElementById("totalOtherCurrdiv").innerHTML = parseFloat(otherCurrTotalAmt).toFixed(2);
	
	document.getElementById("particularTotalAmt").value = parseFloat(amt).toFixed(2);
	document.getElementById("totalAmt").value = parseFloat(totalAmt).toFixed(2);
	
	document.getElementById("particularTotalAmtOtherCurr").value = parseFloat(otherCurrTotalAmt).toFixed(2);
	 document.getElementById("totalAmtOtherCurr").value = parseFloat(otherCurrTotalAmt).toFixed(2);
}  */


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
 
 
function getExchangeValue(value, operation) {
	 var proCurrency = document.getElementById("proCurrency").value;
		
	 if(value == proCurrency || value == '' || value == '0') {
		 //getContent('exchangeValDIV', '');
		 document.getElementById("invoiceType").value = '1';
		 document.getElementById("exchangeValDIV").innerHTML = "";
	 } else {
		 document.getElementById("invoiceType").value = '2';
		 getContent('exchangeValDIV', 'ViewAndUpdateExchangeValue.action?currencyType='+value+'&proCurrency='+proCurrency+'&type=V&operation='+operation);
	 }
	 
	 window.setTimeout(function() {  
		 changeCurrencywiseData();
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
	var strProjectOwner = document.getElementById("strProjectOwner").value;
	//var strProId = document.getElementById("pro_id").value;
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {

			var xhr = $.ajax({
				url : "CheckInvoiceCodeExist.action?strInvoiceCode="+strInvoiceCode+"&strProId=0&strProjectOwner="+strProjectOwner,
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
					/* document.getElementById("ownerLocationECC1Div").innerHTML=res[8];
					document.getElementById("ownerLocationECC2Div").innerHTML=res[9]; */
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
					//alert("data ====>> " + data);
					document.getElementById("ownerDiv").innerHTML=res[0];
					document.getElementById("ownerEmailDiv").innerHTML=res[1];
					document.getElementById("ownerSignDiv").innerHTML=res[2];
					document.getElementById("officesDiv").innerHTML=res[3];
					
					document.getElementById("ownerLocationTelDiv").innerHTML=res[4]; 
					document.getElementById("ownerLocationFaxDiv").innerHTML=res[5];
					document.getElementById("ownerORGPanDiv").innerHTML=res[6];
					document.getElementById("ownerORGMCARegDiv").innerHTML=res[7];
					document.getElementById("ownerORGSTRegDiv").innerHTML=res[8];
					document.getElementById("invoiceCode").value=res[9];
					
					/* document.getElementById("ownerLocationPanDiv").innerHTML=res[6];
					document.getElementById("ownerLocationRegNoDiv").innerHTML=res[7];
					document.getElementById("ownerLocationECC1Div").innerHTML=res[8];
					document.getElementById("ownerLocationECC2Div").innerHTML=res[9]; */
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

function getClientDetails(clientid) {
	//otherClntTR cPocDiv othrCPocDiv cAddressDiv othrCAddressDiv othrClntProjectTR
	if(clientid == '0') {
		document.getElementById("otherClntTR").style.display = "table-row";
		document.getElementById("othrClntProjectTR").style.display = "table-row";
		document.getElementById("othrCPocDiv").style.display = "block";
		document.getElementById("othrCAddressDiv").style.display = "block";
		document.getElementById("cPocDiv").style.display = "none";
		document.getElementById("cAddressDiv").style.display = "none";
	} else {
		document.getElementById("otherClntTR").style.display = "none";
		document.getElementById("othrClntProjectTR").style.display = "none";
		document.getElementById("othrCPocDiv").style.display = "none";
		document.getElementById("othrCAddressDiv").style.display = "none";
		document.getElementById("cPocDiv").style.display = "block";
		document.getElementById("cAddressDiv").style.display = "block";
		
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
						document.getElementById("cPocDiv").innerHTML=res[0];
						document.getElementById("cAddressDiv").innerHTML=res[1];
					}
				});

			}
		}	
	}
	
	
function changeCurrencywiseData() {
	 
	 //getContent('INRCurrencyDIV', 'GetINRCurrencyValue.action?currencyType='+currencyType+'&exchangeValue='+exchangeValue+'&type=U');
	 var value = document.getElementById("strCurrency").value;
	 var proCurrency = document.getElementById("proCurrency").value;
	 var billType = document.getElementById("billingType").value;
	 if(billType == '1') {
		 if(value == proCurrency || value == '' || value == '0') {
			 document.getElementById("INRCurrencyTable").style.display = "block";
			 document.getElementById("otherCurrencyTable").style.display = "none";
			 document.getElementById("INRCurrencyProrataTable").style.display = "none";
			 document.getElementById("otherCurrencyProrataTable").style.display = "none";
			 //INRCurrencyTable otherCurrencyTable
		 } else {
			 document.getElementById("otherCurrencyTable").style.display = "block";
			 if(document.getElementById("longCurrency")) {
				 var longCurrency = document.getElementById("longCurrency").value;
				 document.getElementById("otherCurrSpan").innerHTML = longCurrency;
			 }
			 document.getElementById("INRCurrencyTable").style.display = "none";
			 document.getElementById("INRCurrencyProrataTable").style.display = "none";
			 document.getElementById("otherCurrencyProrataTable").style.display = "none";
		 }
	} else {
		if(value == proCurrency || value == '' || value == '0') {
			 document.getElementById("INRCurrencyProrataTable").style.display = "block";
			 document.getElementById("otherCurrencyProrataTable").style.display = "none";
			 document.getElementById("INRCurrencyTable").style.display = "none";
			 document.getElementById("otherCurrencyTable").style.display = "none";
			 //INRCurrencyTable otherCurrencyTable
		 } else {
			 document.getElementById("otherCurrencyProrataTable").style.display = "block";
			 if(document.getElementById("longCurrency")) {
				 var longCurrency = document.getElementById("longCurrency").value;
				 document.getElementById("otherCurrSpanProrata").innerHTML = longCurrency;
			 }
			 document.getElementById("INRCurrencyProrataTable").style.display = "none";
			 document.getElementById("INRCurrencyTable").style.display = "none";
			 document.getElementById("otherCurrencyTable").style.display = "none";
		 }
	}
	 document.getElementById("serviceTaxAmt").value = '';
	 document.getElementById("eduCessAmt").value = '';
	 document.getElementById("stdTaxAmt").value = '';
	 document.getElementById("particularTotalAmt").value = '';
	 document.getElementById("totalAmt").value = '';
	 
	 document.getElementById("particularTotalAmtOtherCurr").value = '';
	 document.getElementById("totalAmtOtherCurr").value = '';
}
	
</script>

<div id="invoiceSummaryid" class="leftbox reportWidth" style="font-size: 12px;">

<s:form theme="simple" action="ProjectAdHocInvoice" name="frmProjectAdHocInvoice" id="formProjectAdHocInvoice" cssClass="formcss">
<input type="hidden" name="invoiceType" id="invoiceType" value="1"/>
<s:hidden name="proType" id="proType" />
<s:hidden name="operation" id="operation" />
<s:hidden name="invoiceId" id="invoiceId" />

<table class="table_style" width="99%">
	<tr>
		<td colspan="5" width="70%">
			<div id="ownerDiv"></div>
	    </td>
    	<td>
    	<% if(operation != null && operation.equals("E")) { %>
        	<s:hidden name="strProjectOwner" id="strProjectOwner"/>
        	<%=(String)request.getAttribute("lblProjectOwner") %>
        <% } else { %>
	    	<s:select label="Select Project Owner" name="strProjectOwner" id="strProjectOwner" listKey="employeeId" cssClass="validateRequired" headerKey=""
				headerValue="Select Project Owner" listValue="employeeName" list="projectOwnerList" key="" required="true" onchange="getProjectOwnerDetails(this.value)"/>
		<% } %>		
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
        <td colspan="6">Billing Type: 
        <% if(operation != null && operation.equals("E")) { %>
        	<s:hidden name="billingType" id="billingType"/>
        	&nbsp;<%=(String)request.getAttribute("lblBillingType") %>
        <% } else { %>
	        <s:select label="Select Billing Type" name="billingType" id="billingType" cssClass="validateRequired" 
	        	headerKey="1" headerValue="General" list="#{'2':'Prorata'}" cssStyle="width:135px" onchange="changeCurrencywiseData();" />
        <% } %>
        </td>
	</tr>
	
	<tr>
        <td colspan="6">Bill No.: 
        <%-- <% if(operation != null && operation.equals("E")) { %>
        	&nbsp;<%=(String)request.getAttribute("lblInvoiceCode") %>
        <% } %> --%>
        <% if(operation != null && operation.equals("E")) { %>
        	<s:textfield name="invoiceCode" id="invoiceCode" cssClass="validateRequired" onkeyup="checkInvoiceCode(this.value);" readonly="true"/>
        <% } else { %>
        	<s:textfield name="invoiceCode" id="invoiceCode" cssClass="validateRequired" onkeyup="checkInvoiceCode(this.value);" />
        <% } %>
        <br/>
        <s:textfield name="invoiceGenDate" id="invoiceGenDate" cssClass="validateRequired" cssStyle="width: 120px; margin-left: 43px; margin-top: 5px;" readonly="true"></s:textfield>
        <%-- <%=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),IConstants.DBDATE,CF.getStrReportDateFormat()) %> --%></td>
	</tr>
	
	<tr>
        <td colspan="6">To <br/>
        <% if(operation != null && operation.equals("E")) { %>
        	<s:hidden name="client" id="client"/>
        	Client: &nbsp;<%=(String)request.getAttribute("lblClient") %>
        <% } else { %>
	        <s:select label="Select Client" name="client" listKey="clientId" cssClass="validateRequired" headerKey="" headerValue="Select Client" 
	        	listValue="clientName" list="clientList" key="" required="true" cssStyle="width:200px" onchange="getClientDetails(this.value)" />
        <% } %>	
        </td>
	</tr>
	
	<tr id="otherClntTR" style="display: none;"> 
        <td colspan="6"><span style="float: left; width: 95px;"> Client Name: </span> 
        	<span style="float: left; margin-left: 7px;"><s:textfield name="othrClientName" cssClass="validateRequired" cssStyle="width: 490px"/></span>
		</td>
	</tr>
	
	<tr>
        <td colspan="6">
        <div id="cPocDiv">
         <% if(operation != null && operation.equals("E")) { %>
        	<s:hidden name="clientPoc" id="clientPoc"/>
        	SPOC: &nbsp;<%=(String)request.getAttribute("lblClientPoc") %>
        <% } else { %>
        	<s:select theme="simple" name="clientPoc" listKey="clientPocId" listValue="clientPocName" headerKey="" headerValue="Select SPOC" 
        		cssClass="validateRequired" cssStyle="width:200px" list="clientPocList" key="" required="true" />
        <% } %>	
		</div>
		
		<div id="othrCPocDiv" style="display: none;"><span style="float: left; width: 95px;">Contact Person: </span>
        	<span style="float: left; margin-left: 7px;"><s:textfield name="otheClientPoc" cssClass="validateRequired" cssStyle="width: 490px"/></span>
		</div>		
		</td>
	</tr>
	
	<tr>
        <td colspan="6"> 
        <div id="cAddressDiv">
        <% if(operation != null && operation.equals("E")) { %>
        	<s:hidden name="clientAddress" id="clientAddress"/>
        	Address: &nbsp;<%=(String)request.getAttribute("lblClientAddress") %>
        <% } else { %> 
        	<s:select theme="simple" name="clientAddress" listKey="clientAddressId" listValue="clientAddress" headerKey=""
				headerValue="Select Address" cssClass="validateRequired" cssStyle="width:200px" list="clientAddressList" key="" required="true" />
		<% } %>		
		</div>
		
		<div id="othrCAddressDiv" style="display: none;"><span style="float: left; width: 95px;">Client Address:</span> 
        	<span style="float: left; margin-left: 7px;"><s:textfield name="otherClientAddress" cssClass="validateRequired" cssStyle="width: 490px"/></span>
		</div>			
		</td>
	</tr>
	
	<tr id="othrClntProjectTR" style="display: none">
        <td colspan="6"><span style="float: left; width: 95px;">Project Name:</span>
        	<span style="float: left; margin-left: 7px;"><s:textfield name="othrClntProject" cssClass="validateRequired" cssStyle="width: 490px"/></span>
		</td>
	</tr>
	
	<tr>
        <td colspan="6">
        <% if(operation != null && operation.equals("E")) { %>
        	<s:hidden name="service" id="service"/>
        	Service: &nbsp;<%=(String)request.getAttribute("lblService") %>
        <% } else { %>
	        <s:select name="service" list="serviceList" listKey="serviceId" listValue="serviceName" headerKey="" headerValue="Select Service" 
	        	cssStyle="width:200px;" key=""/>
        <% } %>							
		</td>
	</tr>
		
	<tr>
        <td colspan="6">
        <div style="float: left;">
        <input type="hidden" name="proCurrency" id="proCurrency" value="<%=IConstants.INR_CURR_ID %>"/>
         <% 
         String exValTopMargin = "-10px";
         if(operation != null && operation.equals("E")) { 
        	 exValTopMargin = "-3px";
         %>
        	<s:hidden name="strCurrency" id="strCurrency"/>
        	Currency: &nbsp;<%=(String)request.getAttribute("lblCurrency") %>
        <% } else { %>
        	<s:select name="strCurrency" id="strCurrency" cssClass="validateRequired" listKey="currencyId" listValue="currencyName" headerKey="" 
        		headerValue="Select Currency" list="currencyList" key="" required="true" cssStyle="width: 200px;" onchange="getExchangeValue(this.value, '');"/>
	    <% } %>		
        	</div>
        	<div id="exchangeValDIV" style="float: left; margin-left: 10px; margin-top: <%=exValTopMargin %>;"> </div>					
		</td>
	</tr>
	<tr>
    	<td colspan="6">
    	
    <!-- ************************************ INRCurrencyTable  start ************************************************ -->
    	
    	<table id="INRCurrencyTable" style="display: block; width: 99%;"> 
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >PARTICULARS</td>
				<td style="text-align: center;font-weight: bold; font-size: 5; border: 1pt solid black;">AMOUNT<%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5; border: 1pt solid black;">AMOUNT<%=currency %></td>
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black;border-right : 1pt solid black;"><!-- Professional Fees for rendering Accounting Services for the month of December 2013 --></td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<tr id="BillPeriodTR" style="display: none;">
				<td style="border-left: 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDate" id="strStartDate" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDate" id="strEndDate" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>)
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
				<% if(operation != null && operation.equals("E")) { %>
					<input type="hidden" name="strPartiCount" id="strPartiCount" value="<%=(particularsList != null && !particularsList.isEmpty()) ? particularsList.size() : "1" %>">
				<% } else { %>
					<input type="hidden" name="strPartiCount" id="strPartiCount" value="<%=(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) ? hmProBillingHeadData.size() : "1" %>">
				<% } %>
				&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			<% if(particularsList != null && !particularsList.isEmpty()) { 
				for(int i=0; i<particularsList.size(); i++) {
					List<String> innerList = particularsList.get(i);
					if(innerList.get(4) != null && !innerList.get(4).equals("OPE")) {	
			%>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">
					<input type="hidden" name="particularId" id="particularId" value="<%=innerList.get(1) %>"/>									
					<div style="float:left; height: 50px; margin-top: 5px;"><span style="float:left;"><textarea rows="2" cols="27" id="strParticulars" name="strParticulars" class="validateRequired"><%=innerList.get(2) %></textarea>
					</span>
					<%-- <span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addAmtType()" href="javascript:void(0)">Add</a></span> --%>
					</div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;">
				<div style="height: 50px; margin-top: 5px;"><input type="text" name="strParticularsAmt" id="strParticularsAmt<%=i %>" style="width:65px;text-align:right;" value="<%=uF.showData(innerList.get(3), "0") %>" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" readonly="readonly"/></div>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			<% 
				} else { %> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">
					<input type="hidden" name="otherParticularId" id="otherParticularId" value="<%=innerList.get(1) %>"/>
					<input type="text" style="width:250px" id="strOtherParticulars" value="<%=innerList.get(2) %>" name="strOtherParticulars"/> <!-- class="validateRequired"  -->
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt<%=i %>" style="width:65px;text-align:right;" value="<%=uF.showData(innerList.get(3), "0") %>" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" readonly="readonly"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% } %>
			
			<% } 
				} else { %>
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
					<div style="float:left; height: 50px; margin-top: 5px;"><span style="float:left;"><textarea rows="2" cols="27" id="strParticulars" name="strParticulars" class="validateRequired"><%=innerList.get(1) %></textarea>
					<%-- <input type="text" style="width:250px" id="strParticulars" value="<%=innerList.get(1) %>" name="strParticulars" class="validateRequired"> --%>
					</span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addAmtType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsAmtID"
				 <% } %> >
				<div style="height: 50px; margin-top: 5px;"><input type="text" name="strParticularsAmt" id="strParticularsAmt<%=i %>" style="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" /></div>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			<% 
				} else { %> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px" id="strOtherParticulars" value="<%=innerList.get(1) %>" name="strOtherParticulars" />
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt<%=i %>" style="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" />
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
					<div style="float:left; height: 50px; margin-top: 5px;"><span style="float:left;">
					<textarea rows="2" cols="27" id="strParticulars" name="strParticulars" class="validateRequired"></textarea>
					<!-- <input type="text" style="width:250px" id="strParticulars" value="Professional Fees" name="strParticulars" class="validateRequired"> --></span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addAmtType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtID">
				<div style="height: 50px; margin-top: 5px;"><input type="text" name="strParticularsAmt" id="strParticularsAmt0" style="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" value="<%=uF.showData((String)request.getAttribute("remainAmt"), "0") %>"/></div>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textfield name="strOtherParticulars" id="strOtherParticulars" value="Out of Pocket Expenses" cssStyle="width:250px;"/>
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmt" id="strOtherParticularsAmt0" style="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("reimbursement_amount"), "0") %>"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% } %>
		<% } %>	
			<%-- <tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;" id="particularsID">									
					<div>
					<textarea rows="2" cols="27" id="strParticulars" name="strParticulars" class="validateRequired"></textarea>
					<span style="float:right;margin-right:195px;margin-top:-9px;"><a title="Add" class="add" onclick="addAmtType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtID">
				<div style="margin-bottom: 5px;"><s:textfield name="strParticularsAmt" id="strParticularsAmt0" cssStyle="width:65px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" cssClass="validateRequired"></s:textfield></div>
				</td> 
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
			</tr> --%>
			
			<tr> 
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmt" id="strTotalAmt" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
			</tr> 
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
					<s:textarea name="proDescription" id="proDescription" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			
			<% if(taxHeadsList != null && !taxHeadsList.isEmpty()) { 
				for(int i=0; i<taxHeadsList.size(); i++) {
					List<String> innerList = taxHeadsList.get(i);
			%>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=innerList.get(2) %> @ <%=innerList.get(5) %>%</td>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadAmtDiv<%=i %>" style="text-align: right;"><%=innerList.get(3) %></div></td>
			</tr>
			<% } 
				} else { %>
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
			<% } %>
			<%-- <tr> 
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Service Tax @ <%=uF.showData(hmTaxMiscSetting.get("SERVICE_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="serviceTaxDiv" style="text-align: right;"></div></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Educational Cess @ <%=uF.showData(hmTaxMiscSetting.get("EDUCATION_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="eduCessDiv" style="text-align: right;"></div></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Secondary and Higher Secondary Cess @ <%=uF.showData(hmTaxMiscSetting.get("STANDARD_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="secHighCessDiv" style="text-align: right;"></div></td>
			</tr> --%>
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;" >&nbsp;</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				<%if(operation != null && operation.equals("E")) { %>
					<textarea name="otherDescription" id="otherDescription" rows="2" cols="50"><%=(String)request.getAttribute("otherDescription") %></textarea>
				<% } else { %>
					<s:textarea name="otherDescription" id="otherDescription" cols="50" rows="2" value="TDS is required to be deducted @ 3% as per approval of ITO"></s:textarea>
					<% } %>
				</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			<tr>   
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >TOTAL <%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totaldiv" style="text-align: right;"><%=uF.showData((String)request.getAttribute("totalAmt"), "") %> </div></td>
			</tr>
		</table>
		
	<!-- ************************************ INRCurrencyTable  end ************************************************ -->	
		
		
		
	<!-- ************************************ INRCurrencyProrataTable Start ************************************************ -->	
		
		<table id="INRCurrencyProrataTable" style="display: none; width: 99%;"> 
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 75%; border: 1pt solid black;" >PARTICULARS</td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;">AMOUNT<%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;">AMOUNT<%=currency %></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;"><!-- Professional Fees for rendering Accounting Services for the month of December 2013 --></td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			<tr id="BillPeriodTRProrata" style="display: none;">
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDateProrata" id="strStartDateProrata" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDateProrata"  id="strEndDateProrata" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>)
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textarea name="strReferenceNoProrata" id="strReferenceNoProrata" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr><td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">
				<% if(operation != null && operation.equals("E")) { %>
					<input type="hidden" name="strResourceCountProRata" id="strResourceCountProRata" value="<%=(resourcesList != null && !resourcesList.isEmpty()) ? resourcesList.size()-1 : "0" %>">
					<input type="hidden" name="strPartiCountProRata" id="strPartiCountProRata" value="<%=(otherPartiListProrata != null && !otherPartiListProrata.isEmpty()) ? otherPartiListProrata.size() : "1" %>">
				<% } else { %>
					<input type="hidden" name="strResourceCountProRata" id="strResourceCountProRata" value="0">
					<input type="hidden" name="strPartiCountProRata" id="strPartiCountProRata" value="<%=(hmProRataProBillingHeadData != null && !hmProRataProBillingHeadData.isEmpty()) ? hmProRataProBillingHeadData.size() : "1" %>">
				<% } %>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<% if(resourcesList != null && !resourcesList.isEmpty()) { 
				for(int i=0; i<resourcesList.size(); i++) {
					List<String> innerList = resourcesList.get(i);
			%>
			<tr>
				<td id="strEmpTD" style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;">
					<div id="strEmpDIV0" style="float: left;">
						<span style="float: left;">
							<input type="hidden" name="resourceId" id="resourceId" value="<%=innerList.get(1) %>"/>
							<input type="text" name="strEmp" id="strEmp0" value="<%=innerList.get(2) %>"/>&nbsp;
							<input type="text" name="billDailyHours" id="billDailyHours<%=i %>" style="width:30px;" value="<%=innerList.get(3) %>" onkeyup="calEmpAmt();"  onkeypress="return isNumberKey(event)" readonly="readonly"/>&nbsp;
							<%=innerList.get(4) %>
							&nbsp;@&nbsp;<input type="text" name="empRate" id="empRate<%=i %>" style="width:30px;" value="<%=innerList.get(5) %>" onkeyup="calEmpAmt();" onkeypress="return isNumberKey(event)" readonly="readonly" />
						</span>
						<%-- <span style="float:right;margin-right:25px;margin-top:-9px;"><a title="Add" class="add" onclick="addNewEmpBill()" href="javascript:void(0)">Add</a></span> --%>
					</div>
				</td> 
				<td id="strEmpAmtTD" style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" >
					<div id="strEmpAmtDIV0" style="margin-bottom: 10px;"> 
						<input type="text" name="strEmpAmt" id="strEmpAmt<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(6) %>" readonly="readonly"/>
					</div>
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
			</tr>
			<% }
				} else { %>
			<tr>
				<td id="strEmpTD" style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;">
					<div id="strEmpDIV0" style="float: left;">
						<span style="float: left;">
							<input type="text" name="strEmp" id="strEmp0" />&nbsp;
							<input type="text" name="billDailyHours" id="billDailyHours0" style="width:30px;" onkeyup="calEmpAmt();"  onkeypress="return isNumberKey(event)"/>&nbsp;
							<select name="billDaysHours" id="billDaysHours0" style="width:70px;" onchange="calEmpAmt();">
								<!-- <option value="">Select</option> -->
								<option value="1">Days</option>
								<option value="2">Hours</option>
								<option value="3">Months</option>
							</select>
							&nbsp;@&nbsp;<input type="text" name="empRate" id="empRate0" style="width:30px;" onkeyup="calEmpAmt();" onkeypress="return isNumberKey(event)"/>
						</span>
						<span style="float:right;margin-right:25px;margin-top:-9px;"><a title="Add" class="add" onclick="addNewEmpBill()" href="javascript:void(0)">Add</a></span>
					</div>
				</td> 
				<td id="strEmpAmtTD" style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" >
					<div id="strEmpAmtDIV0" style="margin-bottom: 10px;"> 
						<input type="text" name="strEmpAmt" id="strEmpAmt0" style="width:65px;text-align:right;" readonly="readonly"/>
					</div>
				</td> 
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
			</tr>
			<% } %>
			
			
			<% if(otherPartiListProrata != null && !otherPartiListProrata.isEmpty()) { 
				for(int i=0; i<otherPartiListProrata.size(); i++) {
					List<String> innerList = otherPartiListProrata.get(i);
			%>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">
					<input type="hidden" name="otherParticularId" id="otherParticularId" value="<%=innerList.get(1) %>"/>
					<input type="text" style="width:250px" id="strOtherParticularsProrata" value="<%=innerList.get(2) %>" name="strOtherParticularsProrata">
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtProrata" id="strOtherParticularsAmtProrata<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(3) %>" onkeyup="calProrataAmt();" onkeypress="return isNumberKey(event)" readonly="readonly"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% } 
				} else { %>
			<% 
			String strProRaraPerticnt = (String)request.getAttribute("strProRaraPerticnt");
			if(hmProRataProBillingHeadData != null && !hmProRataProBillingHeadData.isEmpty()) { 
				Iterator<String> it = hmProRataProBillingHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProRataProBillingHeadData.get(billingHeadId);
				
			%> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px" id="strOtherParticularsProrata" value="<%=innerList.get(1) %>" name="strOtherParticularsProrata"/>
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtProrata" id="strOtherParticularsAmtProrata<%=i %>" style="width:65px;text-align:right;" onkeyup="calProrataAmt();" onkeypress="return isNumberKey(event)"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% i++; 
				} %>
			<% } else { %>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textfield name="strOtherParticularsProrata" id="strOtherParticularsProrata" value="Out of Pocket Expenses" cssStyle="width:250px;"/>
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
				<input type="text" name="strOtherParticularsAmtProrata" id="strOtherParticularsAmtProrata0" style="width:65px;text-align:right;" onkeyup="calProrataAmt();" onkeypress="return isNumberKey(event)" value="0"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr> 
			<% } %>
		<% } %>	
			
			<%-- <tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;">									
					<s:textfield name="strOtherParticularsProrata" id="strOtherParticularsProrata" cssStyle="width:250px;" cssClass="validateRequired"></s:textfield>
				</td>
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" >
				<div style="margin-bottom: 5px;"><s:textfield name="strOtherParticularsAmtProrata" id="strOtherParticularsAmtProrata" cssStyle="width:65px;text-align:right;" onkeyup="calProrataAmt();" onkeypress="return isNumberKey(event)" cssClass="validateRequired"></s:textfield></div>
				</td> 
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
			</tr>  --%>
			
			
			<tr> 
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtProrata" id="strTotalAmtProrata" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
			</tr> 
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
					<s:textarea name="proDescriptionProrata" id="proDescriptionProrata" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			<% if(taxHeadsList != null && !taxHeadsList.isEmpty()) { 
				for(int i=0; i<taxHeadsList.size(); i++) {
					List<String> innerList = taxHeadsList.get(i);
			%>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=innerList.get(2) %> @ <%=innerList.get(5) %>%</td>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadAmtDivProrata<%=i %>" style="text-align: right;"><%=innerList.get(3) %></div></td>
			</tr>
			<% } 
				} else { %>
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
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadAmtDivProrata<%=i %>" style="text-align: right;"></div></td>
			</tr>
			<% i++; } } %>
			<% } %>
			<%-- <tr> 
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Service Tax @ <%=uF.showData(hmTaxMiscSetting.get("SERVICE_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="serviceTaxDiv1" style="text-align: right;"></div></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Educational Cess @ <%=uF.showData(hmTaxMiscSetting.get("EDUCATION_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="eduCessDiv1" style="text-align: right;"></div></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;" >Add: Secondary and Higher Secondary Cess @ <%=uF.showData(hmTaxMiscSetting.get("STANDARD_TAX"),"0") %>%</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black; padding-right: 5px;"><div id="secHighCessDiv1" style="text-align: right;"></div></td>
			</tr> --%>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;" >&nbsp;</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				<%if(operation != null && operation.equals("E")) { %>
					<textarea name="otherDescriptionProrata" id="otherDescriptionProrata" rows="2" cols="50"><%=(String)request.getAttribute("otherDescriptionProrata") %></textarea>
				<% } else { %>
					<s:textarea name="otherDescriptionProrata" id="otherDescriptionProrata" cols="50" rows="2" value="TDS is required to be deducted @ 3% as per approval of ITO"></s:textarea>
				<% } %>	
				</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			<tr>   
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >TOTAL <%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totaldivProrata" style="text-align: right;"><%=uF.showData((String)request.getAttribute("totalAmt"), "") %></div></td>
			</tr>
		</table>
		
	<!-- ************************************ INRCurrencyProrataTable end ************************************************ -->
		
		
		
	<!-- ************************************ otherCurrencyTable  start ************************************************ -->
		
		<table id="otherCurrencyTable" style="display: none; width: 99%;"> 
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >PARTICULARS</td>
				<td style="text-align: center;font-weight: bold; font-size: 5; border: 1pt solid black;"><%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5; border: 1pt solid black;"><span id="otherCurrSpan"><%=currency %></span></td>
			</tr>
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;"><!-- Professional Fees for rendering Accounting Services for the month of December 2013 --></td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			
			<tr id="BillPeriodOtherCurrTR" style="display: none;">
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDateOtherCurr" id="strStartDateOtherCurr" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDateOtherCurr"  id="strEndDateOtherCurr" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>)
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
				<% if(operation != null && operation.equals("E")) { %>
					<input type="hidden" name="strPartiCountOtherCurr" id="strPartiCountOtherCurr" value="<%=(particularsListOtherCurr != null && !particularsListOtherCurr.isEmpty()) ? particularsListOtherCurr.size() : "1" %>">
				<% } else { %>
					<input type="hidden" name="strPartiCountOtherCurr" id="strPartiCountOtherCurr" value="<%=(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) ? hmProBillingHeadData.size() : "1" %>">
				<% } %>
				&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			<% if(particularsListOtherCurr != null && !particularsListOtherCurr.isEmpty()) { 
				for(int i=0; i<particularsListOtherCurr.size(); i++) {
					List<String> innerList = particularsListOtherCurr.get(i);
					if(innerList.get(5) != null && !innerList.get(4).equals("OPE")) {	
			%>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">
					<input type="hidden" name="particularIdOtherCurr" id="particularIdOtherCurr" value="<%=innerList.get(1) %>"/>									
					<div style="float:left; height: 50px; margin-top: 5px;">
						<span style="float:left;"><textarea rows="2" cols="27" id="strParticularsINRCurr" name="strParticularsINRCurr" class="validateRequired"><%=innerList.get(2) %></textarea></span>
					</div>
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
					<div style="height: 50px; margin-top:5px;"><input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(3) %>" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" readonly="readonly"/></div>
				</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;">
					<div style="height: 50px; margin-top:5px;"><input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(4) %>" readonly="readonly"/></div>
				</td> 
			</tr>
			<% 
				} else { %> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">
					<input type="hidden" name="otherParticularIdOtherCurr" id="otherParticularIdOtherCurr" value="<%=innerList.get(1) %>"/>
					<input type="text" style="width:250px" id="strOtherParticularsINRCurr" value="<%=innerList.get(2) %>" name="strOtherParticularsINRCurr"/>
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
					<div style="margin-bottom: 5px;">
						<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(3) %>" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" readonly="readonly"/>
					</div>
				</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;">
					<div style="margin-bottom: 5px;"><input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(4) %>" readonly="readonly"/></div>
				</td> 
			</tr> 
			<% } %>
			
			<% } 
				} else { %>
			
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
				 id="particularsOtherCurrID"
				 <% } %> >									
					<div style="float:left; height: 50px; margin-top:5px;"><span style="float:left;"><textarea rows="2" cols="27" id="strParticularsINRCurr" name="strParticularsINRCurr" class="validateRequired"><%=innerList.get(1) %></textarea>
					<%-- <input type="text" style="width:250px" id="strParticularsINRCurr" value="<%=innerList.get(1) %>" name="strParticularsINRCurr" class="validateRequired"> --%>
					</span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addOtherCurrType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				 id="particularsAmtINRCurrID"
				 <% } %> >
				 <div style="height: 50px; margin-top:5px;"><input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr<%=i %>" style="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" /></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" 
				<% if(i==(uF.parseToInt(strPerticnt)-1)) { %>
				id="particularsAmtOtherCurrID"
				<% } %> >
					<div style="height: 50px; margin-top:5px;"><input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr<%=i %>" style="width:65px;text-align:right;" readonly="readonly"/></div>
				</td> 
			</tr>
			<% 
				} else { %> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px" id="strOtherParticularsINRCurr" value="<%=innerList.get(1) %>" name="strOtherParticularsINRCurr"/>
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr<%=i %>" style="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)"/>
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
					<div style="height: 50px; margin-top:5px;"><span style="float:left;"><textarea rows="2" cols="27" id="strParticularsINRCurr" name="strParticularsINRCurr" class="validateRequired">Professional Fees</textarea>
					<!-- <input type="text" style="width:250px" id="strParticularsINRCurr" value="Professional Fees" name="strParticularsINRCurr" class="validateRequired"> -->
					</span>
					<span style="float:left; height: 15px; margin-top:-9px;"><a title="Add" class="add" onclick="addOtherCurrType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtINRCurrID">
				 <div style="height: 50px; margin-top:5px;"><input type="text" name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr0" style="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" /></div>
				</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtOtherCurrID">
					<div style="height: 50px; margin-top:5px;"><input type="text" name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr0" style="width:65px;text-align:right;" readonly="readonly"/></div>
				</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px" id="strOtherParticularsINRCurr" value="Out of Pocket Expenses" name="strOtherParticularsINRCurr"/>
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurr" id="strOtherParticularsAmtINRCurr0" style="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" value="0"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" >
					<div style="margin-bottom: 5px;"><input type="text" name="strOtherParticularsAmtOtherCurr" id="strOtherParticularsAmtOtherCurr0" style="width:65px;text-align:right;" readonly="readonly" value="0"/></div>
				</td>
			</tr> 
			
			<% } %>
		<% } %>	
			
			
			<%-- <tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;" id="particularsOtherCurrID">									
					<div>
					<textarea rows="2" cols="27" id="strParticularsINRCurr" name="strParticularsINRCurr" class="validateRequired"></textarea>
					<span style="float:right;margin-right:195px;margin-top:-9px;"><a title="Add" class="add" onclick="addOtherCurrType()" href="javascript:void(0)">Add</a></span></div>
				</td>
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtINRCurrID">
					<div style="margin-bottom: 5px;"><s:textfield name="strParticularsAmtINRCurr" id="strParticularsAmtINRCurr0" cssStyle="width:65px;text-align:right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" cssClass="validateRequired"></s:textfield></div>
				</td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;" id="particularsAmtOtherCurrID">
					<div style="margin-bottom: 5px;"><s:textfield name="strParticularsAmtOtherCurr" id="strParticularsAmtOtherCurr0" cssStyle="width:65px;text-align:right;" readonly="true"></s:textfield></div>
				</td> 
			</tr> --%>
			
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtINRCurr" id="strTotalAmtINRCurr" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtOtherCurr" id="strTotalAmtOtherCurr" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;">
					<s:textarea name="proDescriptionOtherCurr" id="proDescriptionOtherCurr" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<% if(taxHeadsListOtherCurr != null && !taxHeadsListOtherCurr.isEmpty()) { 
				for(int i=0; i<taxHeadsListOtherCurr.size(); i++) {
					List<String> innerList = taxHeadsListOtherCurr.get(i);
			%>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=innerList.get(2) %> @ <%=innerList.get(6) %>%</td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadINRAmtDiv<%=i %>" style="text-align: right;"><%=innerList.get(3) %></div></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadOtherAmtDiv<%=i %>" style="text-align: right;"><%=innerList.get(4) %></div></td>
			</tr>
			<% } 
				} else { %>
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
			<% } %>
			
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				<%if(operation != null && operation.equals("E")) { %>
					<textarea name="otherDescriptionOtherCurr" id="otherDescriptionOtherCurr" rows="2" cols="50"><%=(String)request.getAttribute("otherDescriptionOtherCurr") %></textarea>
				<% } else { %>
					<s:textarea name="otherDescriptionOtherCurr" id="otherDescriptionOtherCurr" cols="50" rows="2" value="TDS is required to be deducted @ 3% as per approval of ITO"></s:textarea>
				<% } %>	
				</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;" >&nbsp;</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>
			
			<tr>   
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >TOTAL <%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"><div id="totalINRAmtdiv" style="text-align: right;"><%=uF.showData((String)request.getAttribute("totalAmtINRCurr"), "") %></div></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totalOtherCurrdiv" style="text-align: right;"><%=uF.showData((String)request.getAttribute("totalAmtOtherCurr"), "") %></div></td>
			</tr>
		</table>
		
	<!-- ************************************ otherCurrencyTable end ************************************************ -->
		
		
		
	<!-- ************************************ otherCurrencyProrataTable start ************************************************ -->
		
		<table id="otherCurrencyProrataTable" style="display: none; width: 99%;"> 
			<tr>
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >PARTICULARS</td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"><%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black;"><span id="otherCurrSpanProrata"><%=currency %></span></td>
			</tr>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			
			<tr id="BillPeriodOtherCurrTRProrata" style="display: none;">
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
				(Billing period taken as <s:textfield name="strStartDateOtherCurrProrata" id="strStartDateOtherCurrProrata" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>
				 - <s:textfield name="strEndDateOtherCurrProrata"  id="strEndDateOtherCurrProrata" cssStyle="width:65px" cssClass="validateRequired"></s:textfield>)
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
				<td style="border-right : 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<s:textarea name="strReferenceNoOtherCurrProrata" id="strReferenceNoOtherCurrProrata" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td> 
			</tr>
			
			<tr><td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">&nbsp;</td>
				<td style="border-right: 1pt solid black;">
				<% if(operation != null && operation.equals("E")) { %>
					<input type="hidden" name="strResourceCountOtherCurrProRata" id="strResourceCountOtherCurrProRata" value="<%=(resourcesListOtherCurr != null && !resourcesListOtherCurr.isEmpty()) ? resourcesListOtherCurr.size()-1 : "0" %>">
					<input type="hidden" name="strPartiCountOtherCurrProrata" id="strPartiCountOtherCurrProrata" value="<%=(otherPartiListOtherCurrProrata != null && !otherPartiListOtherCurrProrata.isEmpty()) ? otherPartiListOtherCurrProrata.size() : "1" %>">
				<% } else { %>
				<input type="hidden" name="strResourceCountOtherCurrProRata" id="strResourceCountOtherCurrProRata" value="0">
				<input type="hidden" name="strPartiCountOtherCurrProrata" id="strPartiCountOtherCurrProrata" value="<%=(hmProRataProBillingHeadData != null && !hmProRataProBillingHeadData.isEmpty()) ? hmProRataProBillingHeadData.size() : "1" %>">
				<% } %>
				</td> 
				<td style="border-right: 1pt solid black;">&nbsp;</td>
			</tr>
			
			<% if(resourcesListOtherCurr != null && !resourcesListOtherCurr.isEmpty()) { 
				for(int i=0; i<resourcesListOtherCurr.size(); i++) {
					List<String> innerList = resourcesListOtherCurr.get(i);
			%>
			<tr>
				<td id="strEmpINRCurrTD" style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;">
					<div id="strEmpINRCurrDIV0" style="float: left;">
						<span style="float: left;">
							<input type="hidden" name="resourceIdOtherCurr" id="resourceIdOtherCurr" value="<%=innerList.get(1) %>"/>
							<input type="text" name="strEmpOtherCurr" id="strEmpOtherCurr0" value="<%=innerList.get(2) %>"/>&nbsp;
							<input type="text" name="billDailyHoursINRCurr" id="billDailyHoursINRCurr<%=i %>" style="width:30px;" value="<%=innerList.get(3) %>" onkeyup="calEmpAmtOtherCurr();"  onkeypress="return isNumberKey(event)" readonly="readonly"/>&nbsp;
							<%=innerList.get(4) %>
							&nbsp;@&nbsp;<input type="text" name="empRateINRCurr" id="empRateINRCurr<%=i %>" style="width:30px;" value="<%=innerList.get(5) %>" onkeyup="calEmpAmtOtherCurr();" onkeypress="return isNumberKey(event)" readonly="readonly" />
						</span>
					</div>
				</td>
				<td id="strEmpAmtINRCurrTD" style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" >
					<div id="strEmpAmtINRCurrDIV0" style="margin-bottom: 10px;"> 
						<input type="text" name="strEmpAmtINRCurr" id="strEmpAmtINRCurr<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(6) %>" readonly="readonly"/>
					</div>
				</td>
				
				<td id="strEmpAmtOtherCurrTD" style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" > 
				<div id="strEmpAmtOtherCurrDIV0" style="margin-bottom: 10px;">
					<input type="text" name="strEmpAmtOtherCurr" id="strEmpAmtOtherCurr<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(7) %>" readonly="readonly"/>
				</div>
				</td> 
			</tr>
			<% }
				} else { %>
			<tr>
				<td id="strEmpINRCurrTD" style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;">
					<div id="strEmpINRCurrDIV0" style="float: left;">
						<span style="float: left;">
							<input type="text" name="strEmpOtherCurr" id="strEmpOtherCurr0" />&nbsp;
							<input type="text" name="billDailyHoursINRCurr" id="billDailyHoursINRCurr0" style="width:30px;" onkeyup="calEmpAmtOtherCurr();"  onkeypress="return isNumberKey(event)"/>&nbsp;
							<select name="billDaysHoursINRCurr" id="billDaysHoursINRCurr0" style="width:70px;" onchange="calEmpAmtOtherCurr();">
								<!-- <option value="">Select</option> -->
								<option value="1">Days</option>
								<option value="2">Hours</option>
								<option value="3">Months</option>
							</select>
							&nbsp;@&nbsp;<input type="text" name="empRateINRCurr" id="empRateINRCurr0" style="width:30px;" onkeyup="calEmpAmtOtherCurr();" onkeypress="return isNumberKey(event)"/>
						</span>
						<span style="float:right;margin-right:25px;margin-top:-9px;"><a title="Add" class="add" onclick="addNewEmpBillOtherCurr()" href="javascript:void(0)">Add</a></span>
					</div>
				</td> 
				<td id="strEmpAmtINRCurrTD" style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" >
					<div id="strEmpAmtINRCurrDIV0" style="margin-bottom: 10px;"> 
						<input type="text" name="strEmpAmtINRCurr" id="strEmpAmtINRCurr0" style="width:65px;text-align:right;" readonly="readonly"/>
					</div>
				</td>
				
				<td id="strEmpAmtOtherCurrTD" style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" > 
				<div id="strEmpAmtOtherCurrDIV0" style="margin-bottom: 10px;">
					<input type="text" name="strEmpAmtOtherCurr" id="strEmpAmtOtherCurr0" style="width:65px;text-align:right;" readonly="readonly"/>
				</div>
				</td> 
			</tr>
			<% } %>
			
			<% if(otherPartiListOtherCurrProrata != null && !otherPartiListOtherCurrProrata.isEmpty()) { 
				for(int i=0; i<otherPartiListOtherCurrProrata.size(); i++) {
					List<String> innerList = otherPartiListOtherCurrProrata.get(i);
			%>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">
					<input type="hidden" name="otherParticularIdOtherCurr" id="otherParticularIdOtherCurr" value="<%=innerList.get(1) %>"/>
					<input type="text" style="width:250px" id="strOtherParticularsINRCurrProrata" value="<%=innerList.get(2) %>" name="strOtherParticularsINRCurrProrata" />
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurrProrata" id="strOtherParticularsAmtINRCurrProrata<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(3) %>" onkeyup="calProrataAmtOtherCurr();" onkeypress="return isNumberKey(event)" readonly="readonly"/>
				</div>
				</td> 
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtOtherCurrProrata" id="strOtherParticularsAmtOtherCurrProrata<%=i %>" style="width:65px;text-align:right;" value="<%=innerList.get(4) %>" readonly="readonly"/>
				</div>
				</td> 
			</tr> 
			<% } 
				} else { %>
			<% 
			if(hmProRataProBillingHeadData != null && !hmProRataProBillingHeadData.isEmpty()) { 
				Iterator<String> it = hmProRataProBillingHeadData.keySet().iterator();
				int i = 0;
				while(it.hasNext()) {
				String billingHeadId = it.next();	
				List<String> innerList = hmProRataProBillingHeadData.get(billingHeadId);
				
			%> 
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">									
					<input type="text" style="width:250px" id="strOtherParticularsINRCurrProrata" value="<%=innerList.get(1) %>" name="strOtherParticularsINRCurrProrata" />
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurrProrata" id="strOtherParticularsAmtINRCurrProrata<%=i %>" style="width:65px;text-align:right;" onkeyup="calProrataAmtOtherCurr();" onkeypress="return isNumberKey(event)" />
				</div>
				</td> 
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtOtherCurrProrata" id="strOtherParticularsAmtOtherCurrProrata<%=i %>" style="width:65px;text-align:right;" readonly="readonly"/>
				</div>
				</td>  
			</tr> 
			<% i++; 
				} %>
			<% } else { %>
			<tr>
				<td style="border-left: 1pt solid black; border-right: 1pt solid black; padding-left: 5px;">
					<input type="text" style="width:250px" id="strOtherParticularsINRCurrProrata" value="Out of Pocket Expenses" name="strOtherParticularsINRCurrProrata" />
				</td>
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtINRCurrProrata" id="strOtherParticularsAmtINRCurrProrata0" style="width:65px;text-align:right;" onkeyup="calProrataAmtOtherCurr();" onkeypress="return isNumberKey(event)" />
				</div>
				</td> 
				<td style="border-right: 1pt solid black; text-align:right; padding-right: 5px;">
				<div style="margin-bottom: 5px;">
					<input type="text" name="strOtherParticularsAmtOtherCurrProrata" id="strOtherParticularsAmtOtherCurrProrata0" style="width:65px;text-align:right;" readonly="readonly"/>
				</div>
				</td>
			</tr> 
			<% } %>
		<% } %>	
			
			<%-- <tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black; padding-left: 5px;">									
					<s:textfield name="strOtherParticularsINRCurrProrata" id="strOtherParticularsINRCurrProrata" cssStyle="width:250px;" cssClass="validateRequired"></s:textfield>
				</td>
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" >
					<div style="margin-bottom: 5px;"><s:textfield name="strOtherParticularsAmtINRCurrProrata" id="strOtherParticularsAmtINRCurrProrata" cssStyle="width:65px;text-align:right;" onkeyup="calProrataAmtOtherCurr();" onkeypress="return isNumberKey(event)" cssClass="validateRequired"></s:textfield></div>
				</td> 
				<td style="border-right : 1pt solid black;text-align:right; padding-right: 5px;" >
					<div style="margin-bottom: 5px;"><s:textfield name="strOtherParticularsAmtOtherCurrProrata" id="strOtherParticularsAmtOtherCurrProrata" cssStyle="width:65px;text-align:right;" readonly="true" ></s:textfield></div>
				</td> 
			</tr>  --%>
			
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;">&nbsp;</td>
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtINRCurrProrata" id="strTotalAmtINRCurrProrata" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td> 
				<td style="border-right: 1pt solid black;text-align:right; padding-right: 5px;"><s:textfield name="strTotalAmtOtherCurrProrata" id="strTotalAmtOtherCurrProrata" cssStyle="width:65px;text-align:right;" readonly="true" cssClass="validateRequired"></s:textfield></td>
			</tr>
			
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;padding-left: 5px;">
					<s:textarea name="proDescriptionOtherCurrProrata" id="proDescriptionOtherCurrProrata" cols="50" rows="2"></s:textarea>
				</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
				<td style="border-right : 1pt solid black;">&nbsp;</td>
			</tr>
			<% if(taxHeadsListOtherCurr != null && !taxHeadsListOtherCurr.isEmpty()) { 
				for(int i=0; i<taxHeadsListOtherCurr.size(); i++) {
					List<String> innerList = taxHeadsListOtherCurr.get(i);
			%>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=innerList.get(2) %> @ <%=innerList.get(6) %>%</td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadINRAmtDivProrata<%=i %>" style="text-align: right;"><%=innerList.get(3) %></div></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadOtherAmtDivProrata<%=i %>" style="text-align: right;"><%=innerList.get(4) %></div></td>
			</tr>
			<% } 
				} else { %>
			<% if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
				Iterator<String> it1 = hmProTaxHeadData.keySet().iterator();
				int i = 0;
				while(it1.hasNext()) {
				String taxHeadId = it1.next();	
				List<String> innerList = hmProTaxHeadData.get(taxHeadId);
				%>
			<tr> 
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;" >Add: <%=uF.showData(innerList.get(1),"-") %></td> <%--  @ <%=uF.showData(innerList.get(2),"0") %>% --%>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadINRAmtDivProrata<%=i %>" style="text-align: right;"></div></td>
				<td style="border-right: 1pt solid black; padding-right: 5px;"><div id="taxHeadOtherAmtDivProrata<%=i %>" style="text-align: right;"></div></td>
			</tr>
			<% i++; } } %>
			<% } %>
			<tr>
				<td style="border-left : 1pt solid black;border-right : 1pt solid black;" >&nbsp;</td>
				<td style="border-right : 1pt solid black;"></td>
				<td style="border-right : 1pt solid black;"></td>
			</tr>

			<tr>
				<td style="border-left: 1pt solid black;border-right: 1pt solid black;padding-left: 5px;">
				<%if(operation != null && operation.equals("E")) { %>
					<textarea name="otherDescriptionOtherCurrProrata" id="otherDescriptionOtherCurrProrata" rows="2" cols="50"><%=(String)request.getAttribute("otherDescriptionOtherCurrProrata") %></textarea>
				<% } else { %>
					<s:textarea name="otherDescriptionOtherCurrProrata" id="otherDescriptionOtherCurrProrata" cols="50" rows="2" value="TDS is required to be deducted @ 3% as per approval of ITO"></s:textarea>
				<% } %>
				</td>
				<td style="border-right: 1pt solid black;"></td>
				<td style="border-right: 1pt solid black;"></td>
			</tr>
			
			<tr>   
				<td style="text-align: center; font-weight: bold; font-size: 5; width: 70%; border: 1pt solid black;" >TOTAL <%=currency %></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totalINRAmtdivProrata" style="text-align: right;"><%=uF.showData((String)request.getAttribute("totalAmtINRCurr"), "") %></div></td>
				<td style="text-align: center;font-weight: bold; font-size: 5;border: 1pt solid black; padding-right: 5px;"><div id="totalOtherCurrdivProrata" style="text-align: right;"><%=uF.showData((String)request.getAttribute("totalAmtOtherCurr"), "") %></div></td>
			</tr>
		</table>
		
	<!-- ************************************ otherCurrencyProrataTable start ************************************************ -->
		
		
    </td>
</tr>

<tr>
        <td colspan="6"> 
	        Choose Bank: 
	         <% if(operation != null && operation.equals("E")) { %>
	        	<s:hidden name="bankName" id="bankName"/>
	        	<%=(String)request.getAttribute("lblBankName") %>
	        <% } else { %>
	        	<s:select name="bankName" listKey="bankId" listValue="bankName" cssClass="validateRequired" headerKey="" headerValue="Select Bank" list="bankList"/>
	        <% } %>
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
        	<% if(taxHeadsList != null && !taxHeadsList.isEmpty()) { 
				for(int i=0; i<taxHeadsList.size(); i++) {
					List<String> innerList = taxHeadsList.get(i);
			%>
				<input type="hidden" name="taxHeadId" id="taxHeadId<%=i %>" value="<%=innerList.get(1) %>"/>
				<input type="hidden" name="taxHead" id="taxHead<%=i %>" value="<%=innerList.get(2) %>"/>
				<input type="hidden" name="taxNameLabel" id="taxNameLabel<%=i %>" value="<%=innerList.get(6) %>"/>
				<input type="hidden" name="taxHeadPercent" id="taxHeadPercent<%=i %>" value="<%=innerList.get(5) %>"/>
				<input type="hidden" name="taxHeadAmt" id="taxHeadAmt<%=i %>" value="<%=innerList.get(3) %>"/>
			<% } 
				} else { %>
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
			<% } %>
        	<input type="hidden" name="particularTotalAmt" id="particularTotalAmt" value="<%=(String)request.getAttribute("particularTotalAmt") %>"/>
        	<input type="hidden" name="totalAmt" id="totalAmt" value="<%=(String)request.getAttribute("totalAmt") %>"/>
        	
        	<% if(taxHeadsListOtherCurr != null && !taxHeadsListOtherCurr.isEmpty()) { 
				for(int i=0; i<taxHeadsListOtherCurr.size(); i++) {
					List<String> innerList = taxHeadsListOtherCurr.get(i);
			%>
				<input type="hidden" name="taxHeadIdOtherCurr" id="taxHeadIdOtherCurr<%=i %>" value="<%=innerList.get(1) %>"/>
				<input type="hidden" name="taxHeadOtherCurr" id="taxHeadOtherCurr<%=i %>" value="<%=innerList.get(2) %>"/>
				<input type="hidden" name="taxNameLabelOtherCurr" id="taxNameLabelOtherCurr<%=i %>" value="<%=innerList.get(7) %>"/>
				<input type="hidden" name="taxHeadPercentOtherCurr" id="taxHeadPercentOtherCurr<%=i %>" value="<%=innerList.get(6) %>"/>
				<input type="hidden" name="taxHeadAmtINRCurr" id="taxHeadAmtINRCurr<%=i %>" value="<%=innerList.get(3) %>"/>
				<input type="hidden" name="taxHeadAmtOtherCurr" id="taxHeadAmtOtherCurr<%=i %>" value="<%=innerList.get(4) %>"/>
			<% } 
				} else { %>
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
			<% } %>
        	<input type="hidden" name="particularTotalAmtINRCurr" id="particularTotalAmtINRCurr" value="<%=(String)request.getAttribute("particularTotalAmtINRCurr") %>"/>
        	<input type="hidden" name="particularTotalAmtOtherCurr" id="particularTotalAmtOtherCurr" value="<%=(String)request.getAttribute("particularTotalAmtOtherCurr") %>"/>
        	<input type="hidden" name="totalAmtINRCurr" id="totalAmtINRCurr" value="<%=(String)request.getAttribute("totalAmtINRCurr") %>"/>
        	<input type="hidden" name="totalAmtOtherCurr" id="totalAmtOtherCurr" value="<%=(String)request.getAttribute("totalAmtOtherCurr") %>"/>
        	<% if(operation != null && operation.equals("E")) { %>
        		<s:submit value="Update" cssClass="input_button" name="submit" ></s:submit>
        	<% } else { %>
        		<s:submit value="Submit" cssClass="input_button" name="submit" ></s:submit>
        	<% } %>
        </td>
</tr>


</table>
</s:form>

</div>
    


