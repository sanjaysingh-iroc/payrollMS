<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    UtilityFunctions uF = new UtilityFunctions();
    String strUserTYpe = (String)session.getAttribute(IConstants.USERTYPE);
    String strSessionEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strE = (String)request.getParameter("E");
    
    Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
    if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
    String policy_id = (String) request.getAttribute("policy_id");
    
    Map<String, Map<String, List<List<String>>>> hmParentIdBulkExpenseData = (Map<String, Map<String, List<List<String>>>>) request.getAttribute("hmParentIdBulkExpenseData");
    if(hmParentIdBulkExpenseData == null) hmParentIdBulkExpenseData = new HashMap<String, Map<String, List<List<String>>>>();
    
    Map<String, String> hmDraftSavedOn = (Map<String, String>) request.getAttribute("hmDraftSavedOn");
    if(hmDraftSavedOn == null) hmDraftSavedOn = new HashMap<String, String>();
    
    /* Map<String, List<List<String>>> hmBulkExpenseData = (Map<String, List<List<String>>>) request.getAttribute("hmBulkExpenseData");
    if(hmBulkExpenseData == null) hmBulkExpenseData = new HashMap<String, List<List<String>>>(); */
    %>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<title>Bulk Expenses</title>

<script>

$(document).ready(function(){
	$("body").on("click","#submitButton",function(){
		$('.validateRequired').filter(':hidden').prop('required',false);
		$('.validateRequired').filter(':visible').prop('required',true);
	});
	
	$("body").on("click","#saveButton",function(){
		$('.validateRequired').filter(':hidden').prop('required',false);
		$('.validateRequired').filter(':visible').prop('required',true);
	});
});


//var cnt=0;
function addDoc(count, fieldName, saveType, parentId) {
	
	//cnt++;
	 var fileCount = document.getElementById("fileCount_"+fieldName+saveType+'_'+parentId+'_'+count).value;
	 fileCount = parseInt(fileCount) + 1;
	var divTag = document.createElement("div");
    divTag.id = "row_kra"+count+fileCount;
	divTag.innerHTML = 	"<div class=\"row row_without_margin\">"+
	"<div class=\"col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding\">"+
	"<input type=\"file\" name=\"strDocument_"+fieldName+count+"\" id=\"strDocument_"+fieldName+saveType+'_'+parentId+'_'+count+"\"></div>"+
	"<div class=\"col-lg-6 col-md-6 col-sm-6 autoWidth col_no_padding\">"+
	"<a href=\"javascript:void(0)\" onclick=\"addDoc("+count+", '"+fieldName+"', '"+saveType+"', '"+parentId+"')\" class=\"add-font\"></a>"+
	"<a href=\"javascript:void(0)\" onclick=\"removeDoc('"+fileCount+"', '"+count+"', '"+fieldName+"', '"+saveType+"', '"+parentId+"')\" id=\""+fileCount+"\" class=\"remove-font\"></a>"+
	"</div></div>";
	//alert("strDocument ===>> " + document.getElementById("strDocument_"+fieldName+saveType+'_'+parentId+'_'+count));
	document.getElementById("strDocument_"+fieldName+saveType+'_'+parentId+'_'+count).accept = '.gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs';
	//divTag.attributes['accept'] = '.gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs';
	
	//$('#new1').attr('accept', '.gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs');
	/* var b = document.querySelector("file"); 
	
	b.setAttribute("accept", ".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs");
	alert("hiii "+b); */
    document.getElementById("div_documents_"+fieldName+saveType+'_'+parentId+'_'+count).appendChild(divTag);
    document.getElementById("fileCount_"+fieldName+saveType+'_'+parentId+'_'+count).value = fileCount;
}


function removeDoc(removeId, cnt, fieldName, saveType, parentId) {
	var remove_elem = "row_kra"+cnt+removeId;
	var row_kra = document.getElementById(remove_elem);
	document.getElementById("div_documents_"+fieldName+saveType+'_'+parentId+'_'+cnt).removeChild(row_kra);
	
	var fileCount = document.getElementById("fileCount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
    fileCount = parseInt(fileCount) - 1;
    document.getElementById("fileCount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = fileCount;	
}


//var cnt1=0;
function addReceiptNo(count, fieldName, saveType, parentId) {
	//cnt1++;
	var receiptNoCount = document.getElementById("receiptNoCount_"+fieldName+saveType+'_'+parentId+'_'+count).value;
    receiptNoCount = parseInt(receiptNoCount) + 1;
	var divTag = document.createElement("div");
    divTag.id = "row_receipt_"+fieldName+saveType+'_'+parentId+'_'+count+receiptNoCount;
	divTag.innerHTML = 	"<div style=\"float:left\">"+
		"<input type=\"text\" name=\"strReceiptNo_"+fieldName+count+"\" style=\"width: 140px !important;\">"+
		"<a href=\"javascript:void(0)\" onclick=\"addReceiptNo("+count+", '"+fieldName+"', '"+saveType+"', '"+parentId+"')\" class=\"add-font\"></a>"+
		"<a href=\"javascript:void(0)\" onclick=\"removeReceiptNo("+receiptNoCount+", '"+count+"', '"+fieldName+"', '"+saveType+"', '"+parentId+"')\" id=\"removeReceiptId_"+fieldName+saveType+receiptNoCount+"\" class=\"remove-font\"></a>"+
		"</div>";
    document.getElementById("divReceiptNo_"+fieldName+saveType+'_'+parentId+'_'+count).appendChild(divTag);
    document.getElementById("receiptNoCount_"+fieldName+saveType+'_'+parentId+'_'+count).value = receiptNoCount;
}


function removeReceiptNo(removeId, cnt, fieldName, saveType, parentId) {
	var remove_elem = "row_receipt_"+fieldName+saveType+'_'+parentId+'_'+cnt+removeId;
	var row_receipt = document.getElementById(remove_elem); 
	document.getElementById("divReceiptNo_"+fieldName+saveType+'_'+parentId+'_'+cnt).removeChild(row_receipt);
	
	var receiptNoCount = document.getElementById("receiptNoCount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
	receiptNoCount = parseInt(receiptNoCount) - 1;
    document.getElementById("receiptNoCount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = receiptNoCount;	
}


function showType(value, cnt, fieldName, saveType, parentId) {
	// client1 pro1 trav1 othr1 isbillfirst1 typeC1 typeP1 typeT1 typeO1 isbillsecond1
	//alert("value ===>> " + value);
	if(value=='P') {
		
	} else if(value=='T') {
		document.getElementById("trTransType_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		document.getElementById("trTrain_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="none";
		document.getElementById("trBus_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "none";
		document.getElementById("trFlight_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "none";
		document.getElementById("trCar_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "none";
		document.getElementById("trTransAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		
		document.getElementById("trLodgingType_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		document.getElementById("trLodgingAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		
		document.getElementById("trLocalConveyanceType_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		document.getElementById("trLocalConveyanceRate_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		document.getElementById("trLocalConveyanceAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		
		document.getElementById("trFoodBeverageAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		
		document.getElementById("trLaundryAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		
		document.getElementById("trSundryAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display = "table-row";
		
		document.getElementById("tdAmt_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-cell";
		
	} else if(value=='M') {
		
	} else if(value=='L') {
		document.getElementById("tdAmt_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-cell";
	}
}


function othervalue(value,cnt,fieldName, saveType, parentId) {
	//trModeTravel trPlaceFrom trPlaceTo trNodays trTotalKM trRateKM tdAmt 
	if(value == "Travel" || value == "Conveyance Bill") {	
		document.getElementById("trModeTravel_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-row";
		document.getElementById("trPlaceFrom_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-row";
		document.getElementById("trPlaceTo_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-row";
		document.getElementById("trNodays_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-row";
		document.getElementById("trTotalKM_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-row";
		document.getElementById("trRateKM_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-row";
		
		document.getElementById("tdAmt_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-cell";
	} else {
		document.getElementById("trModeTravel_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="none";
		document.getElementById("trPlaceFrom_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="none";
		document.getElementById("trPlaceTo_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="none";
		document.getElementById("trNodays_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="none";
		document.getElementById("trTotalKM_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="none";
		document.getElementById("trRateKM_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="none";
		
		document.getElementById("tdAmt_"+fieldName+saveType+'_'+parentId+'_'+cnt).style.display="table-cell";		
	}
	checkTransportType(cnt, fieldName, saveType, parentId);
}

function setamount(reimType, cnt, fieldName, saveType, parentId) {
    if(reimType == 'L') {
    	var strType = document.getElementById("strType_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
    	var modeoftravel = document.getElementById("modeoftravel_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
    	if(strType == 'Conveyance Bill') {
    		if(document.getElementById("local_1_"+modeoftravel)) {
	    		var limit = document.getElementById("local_1_"+modeoftravel).value;
				var kmpd = document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
			    var ratepkm = document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
			    var noofdays = document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
				if(parseFloat(ratepkm) <= parseFloat(limit)) {
					/* var totalAmt=kmpd*ratepkm*noofdays;
	    			document.getElementById("strAmount"+cnt).value=totalAmt; */
					if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))) {
						var totalAmt = kmpd * ratepkm * noofdays;
						document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = totalAmt;
				    }
	    		} else {
	    			alert('Enter proper rate/km upto '+limit);
	    			document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    		    document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    		    document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    			document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    		}	
    		} else {
        		var kmpd = document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
        		//alert("kmpd ===>> " + kmpd);
        		var ratepkm = document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
    		    //alert("ratepkm ===>> " + ratepkm);
    		    var noofdays = document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
    		    //alert("noofdays ===>> " + noofdays);
    			/* var totalAmt=kmpd * ratepkm * noofdays;
    			document.getElementById("strAmount"+cnt).value=totalAmt; */
    		    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))) {
    				var totalAmt = kmpd * ratepkm * noofdays;
    				document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = totalAmt;
    		    }
        	}
    	} else if(strType =='Food Expenses') { 
    		if(document.getElementById("local_2")) {
	    		var limit = document.getElementById("local_2").value;
	    		var strAmount = document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
	    		if(parseFloat(strAmount) <= parseFloat(limit)) {
	    			document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = strAmount;
	    		} else {
	    			alert('Enter proper amount upto '+limit);
	    			document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    		    document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    		    document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    			document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value='';
	    		}
    		} /* else {
        		var kmpd=document.getElementById("kmpd"+cnt).value;
    		    var ratepkm=document.getElementById("ratepkm"+cnt).value ;
    		    var noofdays=document.getElementById("noofdays"+cnt).value ;
    			var totalAmt=kmpd * ratepkm * noofdays;
    			document.getElementById("strAmount"+cnt).value=totalAmt;
        	} */
    	} else if(strType == 'Travel') {
    		if(document.getElementById("local_3_"+modeoftravel)) {
	    		var limit = document.getElementById("local_3_"+modeoftravel).value;
				var kmpd = document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
			    var ratepkm = document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
			    var noofdays = document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
				
				if(parseFloat(ratepkm) <= parseFloat(limit)){
					/* var totalAmt=kmpd*ratepkm*noofdays;
	    			document.getElementById("strAmount"+cnt).value=totalAmt; */
					if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))) {
						var totalAmt = kmpd * ratepkm * noofdays;
						document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = totalAmt;
				    }
	    		} else {
	    			alert('Enter proper rate/km upto '+limit);
	    			document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    		    document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    		    document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	    			document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value='';
	    		}	
    		} else {
        		var kmpd = document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
    		    var ratepkm = document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
    		    var noofdays = document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
    			/* var totalAmt=kmpd * ratepkm * noofdays;
    			document.getElementById("strAmount"+cnt).value=totalAmt; */
    		    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))) {
    				var totalAmt = kmpd * ratepkm * noofdays;
    				document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = totalAmt;
    		    }
        	}
    	} else {
    		var kmpd = document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
		    var ratepkm = document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value ;
		    var noofdays = document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value ;
			/* var totalAmt=kmpd * ratepkm * noofdays;
			document.getElementById("strAmount"+cnt).value=totalAmt; */
		    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))) {
				var totalAmt = kmpd * ratepkm * noofdays;
				document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = totalAmt;
		    }
    	}
    } else if(reimType == 'M') {
    	if(document.getElementById("mobile")) {
    		var limit = document.getElementById("mobile").value;
			var totalAmt = document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
			if(parseFloat(totalAmt) <= parseFloat(limit)) {
    			document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = totalAmt;
    		} else {
    			alert('Enter proper amount upto '+limit);
    			document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
    		}	
		}
    } else {
    	var kmpd = document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
	    var ratepkm = document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
	    var noofdays = document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value;
	    
	    if((isInt(kmpd) || isFloat(kmpd)) && (isInt(ratepkm) || isFloat(ratepkm)) && (isInt(noofdays) || isFloat(noofdays))) {
			var totalAmt = kmpd * ratepkm * noofdays;
			document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = totalAmt;
	    }		
    }
}


function clearAmt(cnt,fieldName, saveType, parentId) {
	document.getElementById("kmpd_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
    document.getElementById("ratepkm_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
    document.getElementById("noofdays_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
	document.getElementById("strAmount_"+fieldName+saveType+'_'+parentId+'_'+cnt).value = '';
}


var prevcnt=0;
var sbPaycycleList = '<%=(String) request.getAttribute("sbPaycycleList") %>';
<%-- var reimType = '<%=(String) request.getAttribute("reimType") %>'; --%>
var sbClientList = '<%=(String) request.getAttribute("sbClientList") %>';
var sbProjectList = '<%=(String) request.getAttribute("sbProjectList") %>';
var sbTravelPlanList = '<%=(String) request.getAttribute("sbTravelPlanList") %>';
var sbTypeList = '<%=(String) request.getAttribute("sbTypeList") %>';
var sbModeoftravelList = '<%=(String) request.getAttribute("sbModeoftravelList") %>';
var sbLodgingTypeList = '<%=(String) request.getAttribute("sbLodgingTypeList") %>';
var sbLocalConveyanceTranTypeList = '<%=(String) request.getAttribute("sbLocalConveyanceTranTypeList") %>';
var sbReimbCurrencyList = '<%=(String) request.getAttribute("sbReimbCurrencyList") %>';
var sbPaymentModeList = '<%=(String) request.getAttribute("sbPaymentModeList") %>';

function addRow(reimType, tableName, trName, fieldName, saveType, parentId) {
	
	var chkP = "";
	var chkT = "";
	var chkO = "";
	var chkM = "";
	if(reimType != 'P') {
		chkP = "display:none";
	}
	if(reimType != 'T') {
		chkT = "display:none";
	}
	if(reimType != 'L') {
		chkO = "display:none";
	}
	if(reimType != 'M') {
		chkM = "display:none";
	}
	
	if(reimType == 'P') {
		prevcnt = document.getElementById("projectCount"+saveType+"_"+parentId).value;
	}
	if(reimType == 'T') {
		prevcnt = document.getElementById("travelCount"+saveType+"_"+parentId).value;
	}
	if(reimType == 'L') {
		prevcnt = document.getElementById("localCount"+saveType+"_"+parentId).value;
	}
	if(reimType == 'M') {
		prevcnt = document.getElementById("mobileCount"+saveType+"_"+parentId).value;
	}
	prevcnt++;
	//alert("parentId ===>> " + parentId);
    var table = document.getElementById(tableName+"_"+parentId);
    var rowCount = table.rows.length;
    if(rowCount == 1) {
    	document.getElementById(tableName+"_"+parentId).style.display = 'block';
    	document.getElementById("workflowDiv_"+parentId).style.display = 'block';
    	document.getElementById("noExpenseMsgDiv").style.display = 'none';
    }
    //alert("rowCount ====> " + rowCount);
    
    var lblrowid = (parseInt(rowCount)+1);
    //alert("rowCount 1 ====> " + rowCount);
    var lblrow = table.insertRow(rowCount);
    //alert("rowCount 2 ====> " + rowCount);
    lblrow.id = trName+saveType+""+lblrowid;
    lblrow.className = "bb";
    //alert("rowCount 3 ====> " + rowCount);
    
    if(reimType == 'T' || reimType == 'L') {
	    var lblcell0 = lblrow.insertCell(0);
	    var lblcell1 = lblrow.insertCell(1);
	    var lblcell2 = lblrow.insertCell(2);
	    var lblcell3 = lblrow.insertCell(3);
	    var lblcell4 = lblrow.insertCell(4);
	    var lblcell5 = lblrow.insertCell(5);
		var lblcell6 = lblrow.insertCell(6);
		var lblcell7 = lblrow.insertCell(7);
		var lblcell8 = lblrow.insertCell(8);
	    
		lblcell0.setAttribute('nowrap','nowrap');
		lblcell0.setAttribute('class','alignCenter');
		lblcell1.setAttribute('nowrap','nowrap');
		lblcell1.setAttribute('class','alignCenter');
		lblcell2.setAttribute('nowrap','nowrap');
		lblcell2.setAttribute('class','alignCenter');
		lblcell3.setAttribute('nowrap','nowrap');
		lblcell3.setAttribute('class','alignRight');
		lblcell4.setAttribute('nowrap','nowrap');
		lblcell4.setAttribute('class','alignRight');
		lblcell5.setAttribute('nowrap','nowrap');
		lblcell5.setAttribute('class','alignRight');
		lblcell6.setAttribute('nowrap','nowrap');
		lblcell6.setAttribute('class','alignRight');
		lblcell7.setAttribute('nowrap','nowrap');
		lblcell7.setAttribute('class','alignRight');
		lblcell8.setAttribute('nowrap','nowrap');
		lblcell8.setAttribute('class','alignRight');
		
		lblcell0.vAlign = 'top';
		lblcell1.vAlign = 'top';
		lblcell2.vAlign = 'top';
		lblcell3.vAlign = 'top';
		lblcell4.vAlign = 'top';
		lblcell5.vAlign = 'top';
		lblcell6.vAlign = 'top';
		lblcell7.vAlign = 'top';
		lblcell8.vAlign = 'top';
		
		var var1 = "Travel Plan";
		if(reimType == 'L') {
			var1 = "Type";
		}
		lblcell0.innerHTML = "Add";
		lblcell1.innerHTML = var1+"<sup>*</sup>";
		lblcell2.innerHTML = "<span style=\"float: left;\">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span>";
		lblcell3.innerHTML = "Number of Persons<sup>*</sup>";
		lblcell4.innerHTML = "Vendor";
		lblcell5.innerHTML = "Receipt No.";
		lblcell6.innerHTML = "Amount<sup>*</sup>";
		lblcell7.innerHTML = "Purpose";
		lblcell8.innerHTML = "Attach Document";
	
    } else if(reimType == 'M') {
        var lblcell0 = lblrow.insertCell(0);
        var lblcell1 = lblrow.insertCell(1);
        var lblcell2 = lblrow.insertCell(2);
        var lblcell3 = lblrow.insertCell(3);
        var lblcell4 = lblrow.insertCell(4);
        var lblcell5 = lblrow.insertCell(5);
    	var lblcell6 = lblrow.insertCell(6);
    	var lblcell7 = lblrow.insertCell(7);
    	var lblcell8 = lblrow.insertCell(8);
    	var lblcell9 = lblrow.insertCell(9);
        
    	lblcell0.setAttribute('nowrap','nowrap');
    	lblcell0.setAttribute('class','alignCenter');
    	lblcell1.setAttribute('nowrap','nowrap');
    	lblcell1.setAttribute('class','alignCenter');
    	lblcell2.setAttribute('nowrap','nowrap');
    	lblcell2.setAttribute('class','alignRight');
    	lblcell3.setAttribute('nowrap','nowrap');
    	lblcell3.setAttribute('class','alignRight');
    	lblcell4.setAttribute('nowrap','nowrap');
    	lblcell4.setAttribute('class','alignRight');
    	lblcell5.setAttribute('nowrap','nowrap');
    	lblcell5.setAttribute('class','alignRight');
    	lblcell6.setAttribute('nowrap','nowrap');
    	lblcell6.setAttribute('class','alignRight');
    	lblcell7.setAttribute('nowrap','nowrap');
    	lblcell7.setAttribute('class','alignRight');
    	lblcell8.setAttribute('nowrap','nowrap');
    	lblcell8.setAttribute('class','alignRight');
    	lblcell9.setAttribute('nowrap','nowrap');
    	lblcell9.setAttribute('class','alignRight');
    	
    	lblcell0.vAlign = 'top';
    	lblcell1.vAlign = 'top';
    	lblcell2.vAlign = 'top';
    	lblcell3.vAlign = 'top';
    	lblcell4.vAlign = 'top';
    	lblcell5.vAlign = 'top';
    	lblcell6.vAlign = 'top';
    	lblcell7.vAlign = 'top';
    	lblcell8.vAlign = 'top';
    	lblcell9.vAlign = 'top';
    	
    	lblcell0.innerHTML = "Add";
    	lblcell1.innerHTML = "<span style=\"float: left;\">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span>";
    	lblcell2.innerHTML = "Number of Persons<sup>*</sup>";
    	lblcell3.innerHTML = "Vendor";
    	lblcell4.innerHTML = "Receipt No.";
    	lblcell5.innerHTML = "Currency";
    	lblcell6.innerHTML = "Amount<sup>*</sup>";
    	lblcell7.innerHTML = "Payment Mode";
    	lblcell8.innerHTML = "Purpose";
    	lblcell9.innerHTML = "Attach Document";
    	
	} else if(reimType == 'P') {
		var lblcell0 = lblrow.insertCell(0);
		var lblcell1 = lblrow.insertCell(1);
		var lblcell2 = lblrow.insertCell(2);
		var lblcell3 = lblrow.insertCell(3);
		var lblcell4 = lblrow.insertCell(4);
		var lblcell5 = lblrow.insertCell(5);
       	var lblcell6 = lblrow.insertCell(6);
       	var lblcell7 = lblrow.insertCell(7);
       	var lblcell8 = lblrow.insertCell(8);
       	var lblcell9 = lblrow.insertCell(9);
       	var lblcell10 = lblrow.insertCell(10);
       	var lblcell11 = lblrow.insertCell(11);
       	var lblcell12 = lblrow.insertCell(12);
           
       	lblcell0.setAttribute('nowrap','nowrap');
       	lblcell0.setAttribute('class','alignCenter');
       	lblcell1.setAttribute('nowrap','nowrap');
       	lblcell1.setAttribute('class','alignCenter');
       	lblcell2.setAttribute('nowrap','nowrap');
       	lblcell2.setAttribute('class','alignCenter');
       	lblcell3.setAttribute('nowrap','nowrap');
       	lblcell3.setAttribute('class','alignCenter');
       	lblcell4.setAttribute('nowrap','nowrap');
       	lblcell4.setAttribute('class','alignRight');
       	lblcell5.setAttribute('nowrap','nowrap');
       	lblcell5.setAttribute('class','alignRight');
       	lblcell6.setAttribute('nowrap','nowrap');
       	lblcell6.setAttribute('class','alignRight');
       	lblcell7.setAttribute('nowrap','nowrap');
       	lblcell7.setAttribute('class','alignRight');
       	lblcell8.setAttribute('nowrap','nowrap');
       	lblcell8.setAttribute('class','alignRight');
       	lblcell9.setAttribute('nowrap','nowrap');
       	lblcell9.setAttribute('class','alignRight');
       	lblcell10.setAttribute('nowrap','nowrap');
       	lblcell10.setAttribute('class','alignRight');
       	lblcell11.setAttribute('nowrap','nowrap');
       	lblcell11.setAttribute('class','alignRight');
       	lblcell12.setAttribute('nowrap','nowrap');
       	lblcell12.setAttribute('class','alignRight');
       	
       	lblcell0.vAlign = 'top';
       	lblcell1.vAlign = 'top';
       	lblcell2.vAlign = 'top';
       	lblcell3.vAlign = 'top';
       	lblcell4.vAlign = 'top';
       	lblcell5.vAlign = 'top';
       	lblcell6.vAlign = 'top';
       	lblcell7.vAlign = 'top';
       	lblcell8.vAlign = 'top';
       	lblcell9.vAlign = 'top';
       	lblcell10.vAlign = 'top';
       	lblcell11.vAlign = 'top';
       	lblcell12.vAlign = 'top';
       	
       	lblcell0.innerHTML = "Add";
       	lblcell1.innerHTML = "Client<sup>*</sup>";
       	lblcell2.innerHTML = "Project<sup>*</sup>";
       	lblcell3.innerHTML = "<span style=\"float: left;\">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span>";
       	lblcell4.innerHTML = "Number of Persons<sup>*</sup>";
       	lblcell5.innerHTML = "Vendor";
       	lblcell6.innerHTML = "Receipt No.";
       	lblcell7.innerHTML = "Currency";
       	lblcell8.innerHTML = "Amount<sup>*</sup>";
       	lblcell9.innerHTML = "Payment Mode";
       	lblcell10.innerHTML = "Purpose";
       	lblcell11.innerHTML = "Chargeable to client";
       	lblcell12.innerHTML = "Attach Document";
        	
	}
    
    rowCount++;
    var rowid = (parseInt(rowCount)+1);
    //alert("rowCount 1 ====> " + rowCount);
    var row = table.insertRow(rowCount);
    //alert("rowCount 2 ====> " + rowCount);
    row.id = trName+saveType+""+rowid;
    row.className = "bb";
    //alert("rowCount 3 ====> " + rowCount);
    var cell0 = row.insertCell(0);
    var cell1 = ""; // Client
    var cell2 = ""; // Project
    var cell3 = "";
    var cell4 = "";
    var cell5 = "";
	var cell6 = "";
	var cell7 = "";
	var cell8 = "";
	var cell9 = ""; // Travel & Local
	var cell91 = ""; // Mobile & Project
	var cell92 = ""; // Mobile & Project
	var cell93 = ""; // Mobile & Project
	var cell10 = ""; 
	var cell11 = ""; // project
	var cell12 = "";
    if(reimType == 'P') {
    	var cell1 = row.insertCell(1); // Client
        var cell2 = row.insertCell(2); // Project
        var cell5 = row.insertCell(3);
    	var cell6 = row.insertCell(4);
    	var cell7 = row.insertCell(5);
    	var cell8 = row.insertCell(6);
    	var cell91 = row.insertCell(7);
    	var cell92 = row.insertCell(8);
    	var cell93 = row.insertCell(9);
    	var cell10 = row.insertCell(10);
    	var cell11 = row.insertCell(11); // project
    	var cell12 = row.insertCell(12);
    	cell0.vAlign = 'top';
    	cell1.vAlign = 'top';
    	cell2.vAlign = 'top';
    	cell5.vAlign = 'top';
    	cell6.vAlign = 'top';
    	cell7.vAlign = 'top';
    	cell8.vAlign = 'top';
    	cell91.vAlign = 'top';
    	cell92.vAlign = 'top';
    	cell93.vAlign = 'top';
    	cell10.vAlign = 'top';
    	cell11.vAlign = 'top';
    	cell12.vAlign = 'top';
    	
    	cell0.setAttribute('nowrap','nowrap');
    	cell1.setAttribute('nowrap','nowrap');
    	cell2.setAttribute('nowrap','nowrap');
    	cell5.setAttribute('nowrap','nowrap');
    	cell6.setAttribute('nowrap','nowrap');
    	cell7.setAttribute('nowrap','nowrap');
    	cell8.setAttribute('nowrap','nowrap');
    	cell91.setAttribute('nowrap','nowrap');
    	cell92.setAttribute('nowrap','nowrap');
    	cell93.setAttribute('nowrap','nowrap');
    	cell10.setAttribute('nowrap','nowrap');
    	cell11.setAttribute('nowrap','nowrap');
    	cell12.setAttribute('nowrap','nowrap');
	}
    //alert("rowCount 4 ====> " + rowCount);
    if(reimType == 'T') {
    	var cell3 = row.insertCell(1); // Travel
    	var cell5 = row.insertCell(2);
    	var cell6 = row.insertCell(3);
    	var cell7 = row.insertCell(4);
    	var cell8 = row.insertCell(5);
    	var cell9 = row.insertCell(6);
    	var cell10 = row.insertCell(7);
    	var cell12 = row.insertCell(8);
    	cell0.vAlign = 'top';
    	cell3.vAlign = 'top';
    	cell5.vAlign = 'top';
    	cell6.vAlign = 'top';
    	cell7.vAlign = 'top';
    	cell8.vAlign = 'top';
    	cell9.vAlign = 'top';
    	cell10.vAlign = 'top';
    	cell12.vAlign = 'top';
    	
    	cell0.setAttribute('nowrap','nowrap');
    	cell3.setAttribute('nowrap','nowrap');
    	cell5.setAttribute('nowrap','nowrap');
    	cell6.setAttribute('nowrap','nowrap');
    	cell7.setAttribute('nowrap','nowrap');
    	cell8.setAttribute('nowrap','nowrap');
    	cell9.setAttribute('nowrap','nowrap');
    	cell10.setAttribute('nowrap','nowrap');
    	cell12.setAttribute('nowrap','nowrap');
	}
    //alert("rowCount 5 ====> " + rowCount);
	if(reimType == 'L') {
		var cell4 = row.insertCell(1); // Local
		var cell5 = row.insertCell(2);
    	var cell6 = row.insertCell(3);
    	var cell7 = row.insertCell(4);
    	var cell8 = row.insertCell(5);
    	var cell9 = row.insertCell(6);
    	var cell10 = row.insertCell(7);
    	var cell12 = row.insertCell(8);
    	
    	cell0.vAlign = 'top';
    	cell4.vAlign = 'top';
    	cell5.vAlign = 'top';
    	cell6.vAlign = 'top';
    	cell7.vAlign = 'top';
    	cell8.vAlign = 'top';
    	cell9.vAlign = 'top';
    	cell10.vAlign = 'top';
    	cell12.vAlign = 'top';
    	
    	cell0.setAttribute('nowrap','nowrap');
    	cell4.setAttribute('nowrap','nowrap');
    	cell5.setAttribute('nowrap','nowrap');
    	cell6.setAttribute('nowrap','nowrap');
    	cell7.setAttribute('nowrap','nowrap');
    	cell8.setAttribute('nowrap','nowrap');
    	cell9.setAttribute('nowrap','nowrap');
    	cell10.setAttribute('nowrap','nowrap');
    	cell12.setAttribute('nowrap','nowrap');
	}
	//alert("rowCount 6 ====> " + rowCount);
	if(reimType == 'M') {
		var cell5 = row.insertCell(1);
    	var cell6 = row.insertCell(2);
    	var cell7 = row.insertCell(3);
    	var cell8 = row.insertCell(4);
    	var cell91 = row.insertCell(5);
    	var cell92 = row.insertCell(6);
    	var cell93 = row.insertCell(7);
    	var cell10 = row.insertCell(8);
    	var cell12 = row.insertCell(9);
    	cell0.vAlign = 'top';
    	cell5.vAlign = 'top';
    	cell6.vAlign = 'top';
    	cell7.vAlign = 'top';
    	cell8.vAlign = 'top';
    	cell91.vAlign = 'top';
    	cell92.vAlign = 'top';
    	cell93.vAlign = 'top';
    	cell10.vAlign = 'top';
    	cell12.vAlign = 'top';
    	
    	cell0.setAttribute('nowrap','nowrap');
    	cell5.setAttribute('nowrap','nowrap');
    	cell6.setAttribute('nowrap','nowrap');
    	cell7.setAttribute('nowrap','nowrap');
    	cell8.setAttribute('nowrap','nowrap');
    	cell91.setAttribute('nowrap','nowrap');
    	cell92.setAttribute('nowrap','nowrap');
    	cell93.setAttribute('nowrap','nowrap');
    	cell10.setAttribute('nowrap','nowrap');
    	cell12.setAttribute('nowrap','nowrap');
	}
	//alert("rowCount 7 ====> " + rowCount);
	cell0.innerHTML = "<input type=\"hidden\" name=\""+fieldName+"ReimbursementId"+prevcnt+"\" id=\""+fieldName+"ReimbursementId_"+parentId+"_"+prevcnt+"\" value=\"0\" />"+
		"<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to remove this record?'))deleteRow(this, '"+tableName+"', '"+saveType+"', '0', '"+parentId+"')\" class=\"remove-font\"></a>"+ 
    	"<a href=\"javascript:void(0)\" onclick=\"addRow('"+reimType+"', '"+tableName+"', '"+trName+"', '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" class=\"add-font\"></a>";
    //alert("rowCount 8 ====> " + rowCount);
    if(reimType == 'P') {
    	cell1.innerHTML = "<select name='strClient_"+fieldName+prevcnt+"' id='strClient_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' onchange=\"getContent('typeP_"+fieldName+saveType+prevcnt+"', 'GetProjectClientTask.action?client_id='+this.value+'&type=R&cnt='+prevcnt);\" class='validateRequired form-control'>"+
			"		<option value=''>Select Client</option>"+
			""+sbClientList+
			"	</select>";
    	cell2.innerHTML = "<select theme='simple' name='strProject_"+fieldName+prevcnt+"' id='strProject_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control'>"+
			"		<option value=''>Select Project</option>"+
			""+sbProjectList+
			"	</select>";
    }
    if(reimType == 'T') {
    	cell3.innerHTML = "<select name='strTravelPlan_"+fieldName+prevcnt+"' id='strTravelPlan_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control' onchange=\"getTravelPlanDetails(this.value,"+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
			"		<option value=''>Select Travel Plan</option>"+
			""+sbTravelPlanList+
			"	</select> <input type='hidden' name='strTravelPlanDays_"+fieldName+prevcnt+"' id='strTravelPlanDays_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' value='0'/>";
    }
    if(reimType == 'L') {
    	cell4.innerHTML = "<select name='strType_"+fieldName+prevcnt+"' id='strType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class=\"validateRequired form-control\" onchange=\"othervalue(this.value, "+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
			"		<option value=''>Select Type</option>"+
			""+sbTypeList+
			"	</select>";
    }
    //alert("rowCount 10 ====> " + rowCount);
    cell5.innerHTML = "<input type='text' name='fromDate_"+fieldName+prevcnt+"' id='fromDate_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired' style=\"width: 90px !important;\"/>&nbsp;&nbsp;"
    	+"<input type='text' name='toDate_"+fieldName+prevcnt+"' id='toDate_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired' style=\"width: 90px !important;\"/>";
    cell6.innerHTML = "<input type='text' name='noofperson_"+fieldName+prevcnt+"' id='noofperson_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control' style=\"width: 140px !important;\" onkeypress=\"return isNumberKey(event);\"/>";
    cell7.innerHTML = "<input type='text' name='strVendor_"+fieldName+prevcnt+"' id='strVendor_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"'/>";
	cell8.innerHTML = "<input type='hidden' name='receiptNoCount_"+fieldName+prevcnt+"' id='receiptNoCount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' value='1'/>"+
			"<input type='text' name='strReceiptNo_"+fieldName+prevcnt+"' id='strReceiptNo_"+fieldName+saveType+prevcnt+"' style=\"width: 140px !important;\" />"+
			"<a href='javascript:void(0)' onclick=\"addReceiptNo("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"')\" class='add-font'></a>"+
 			"<div id='divReceiptNo_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"'></div>";		
 			
 	if(reimType == 'T' || reimType == 'L') {
 		cell9.innerHTML = "<table class=\"table\" id='innerTable"+saveType+"_"+parentId+"_"+prevcnt+"' cellpadding=\"0\" cellspacing=\"0\">"+
    		"<tr id='trModeTravel_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' style=\"display: none;\">"+
    		"<td class=\"alignRight\" style=\"border: 0px none;\">Mode of travel:<sup>*</sup></td>"+
    		"<td style=\"border: 0px none;\">"+
    		"<select name='modeoftravel_"+fieldName+prevcnt+"' id='modeoftravel_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control' onchange=\"clearAmt("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
    		"		<option value=''>Select Mode</option>"+
    		""+sbModeoftravelList+
    		"	</select>"+
    		"</td>"+
    		"</tr>"+
    		"<tr id='trPlaceFrom_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' style=\"display: none;\">"+
    		"<td class=\"alignRight\" style=\"border: 0px none;\">Place From:<sup>*</sup></td>"+
    		"<td style=\"border: 0px none;\">"+
    		"<input type='text' name='placefrom_"+fieldName+prevcnt+"' id='placefrom_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class=\"validateRequired form-control\" />"+
    		"</td></tr>"+
    		"<tr id='trPlaceTo_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' style=\"display: none;\">"+
    		"<td class=\"alignRight\" style=\"border: 0px none;\">Place To:<sup>*</sup></td>"+
    		"<td style=\"border: 0px none;\"><input type='text' name='placeto_"+fieldName+prevcnt+"' id='placeto_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class=\"validateRequired form-control\" /></td>"+
    		"</tr>"+
    		"<tr id='trNodays_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' style=\"display: none;\">"+
    		"<td class=\"alignRight\" style=\"border: 0px none;\">No of Days:<sup>*</sup></td>"+
    		"<td style=\"border: 0px none;\">"+
    		"<input type='text' name='noofdays_"+fieldName+prevcnt+"' id='noofdays_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class=\"validateRequired form-control\" onkeyup=\"setamount('"+reimType+"', "+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event)\"/></td>"+
    		"</tr>"+
    		"<tr id='trTotalKM_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' style=\"display: none;\">"+
    		"<td class=\"alignRight\" style=\"border: 0px none;\">Total KM:</td>"+
    		"<td style=\"border: 0px none;\">"+
    		"<input type='text' name='kmpd_"+fieldName+prevcnt+"' id='kmpd_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' style=\"width:70px !important;\" onkeyup=\"setamount('"+reimType+"', "+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event)\"/>"+
    		"</td></tr>"+
    		"<tr id='trRateKM_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' style=\"display: none;\">"+
    		"<td class=\"alignRight\" style=\"border: 0px none;\">Rate/KM:</td>"+
    		"<td style=\"border: 0px none;\"><input type='text' name='ratepkm_"+fieldName+prevcnt+"' id='ratepkm_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' style=\"width:63px !important;\" onkeyup=\"setamount('"+reimType+"', "+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event)\"/>"+
    		"</td></tr>"+
    		"<tr id=\"trTransType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Transportation Type:<sup>*</sup></td>"+
			"<td colspan=\"3\" style=\"border: 0px none;\">"+
			"	<select name='transportType_"+fieldName+prevcnt+"' id='transportType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportType("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
    		"		<option value=''>Select Transport Type</option>"+
    		"		<option value='1'>Train</option>"+
    		"		<option value='2'>Bus</option>"+
    		"		<option value='3'>Flight</option>"+
    		"		<option value='4'>Car</option>"+
    		"	</select>"+	
			"</td></tr>"+ 
			
			"<tr id=\"trTrain_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Train:<sup>*</sup></td>"+
			"<td style=\"border: 0px none;\">"+
			"	<select name='trainType_"+fieldName+prevcnt+"' id='trainType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
    		"		<option value='1'>3 Tier</option>"+
    		"		<option value='2'>Chair Car</option>"+
    		"		<option value='3'>AC 3 Tier</option>"+
    		"		<option value='4'>AC 2 Tier</option>"+
    		"		<option value='5'>AC 1st Class</option>"+
    		"	</select>"+
			"</td></tr>"+
			
			"<tr id=\"trBus_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Bus:<sup>*</sup></td>"+
			"<td style=\"border: 0px none;\">"+
			"	<select name='busType_"+fieldName+prevcnt+"' id='busType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
    		"		<option value='1'>A/c Bus</option>"+
    		"		<option value='2'>Non- A/c Bus</option>"+
    		"	</select>"+		
			"</td></tr>"+
			
			"<tr id=\"trFlight_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Flight:<sup>*</sup></td>"+ 
			"<td style=\"border: 0px none;\">"+
			"	<select name='flightType_"+fieldName+prevcnt+"' id='flightType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportAmount("+prevcnt+", '"+fieldName+"','"+saveType+"', '"+parentId+"');\">"+
    		"		<option value='1'>Economy Class</option>"+
    		"		<option value='2'>Business Class</option>"+
    		"	</select>"+		
			"</td></tr>"+
			
			"<tr id=\"trCar_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+ 
			"<td class=\"alignRight\" style=\"border: 0px none;\">Car:<sup>*</sup></td>"+
			"<td colspan=\"3\" style=\"border: 0px none;\">"+
			"	<select name='carType_"+fieldName+prevcnt+"' id='carType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' class='validateRequired form-control' onchange=\"checkTransportAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
    		"		<option value='1'>Cab</option>"+
    		"		<option value='2'>Self Owned</option>"+
    		"	</select>"+		
			"</td></tr>"+
			
			"<tr id=\"trTransAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Transport Amount:<sup>*</sup></td>"+
			"<td style=\"border: 0px none;\">"+
			"	<input type='text' name=\"strTransAmount_"+fieldName+prevcnt+"\" id=\"strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" class=\"validateRequired form-control\" onchange=\"checkTransportAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event);\"/>"+
			"</td></tr>"+
		
			"<tr id=\"trLodgingType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Lodging Type:</td>"+
			"<td style=\"border: 0px none;\">"+
			"	<select name=\"lodgingType_"+fieldName+prevcnt+"\" id=\"lodgingType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" onchange=\"checkLodgingAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
			"		<option value=''>Select Lodging Type</option>"+
			""+sbLodgingTypeList+
    		"	</select>"+			
			"</td></tr>"+
		
			"<tr id=\"trLodgingAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Lodging Amount:</td>"+
			"<td style=\"border: 0px none;\">"+
			"	<input type='text' name=\"strLodgingAmount_"+fieldName+prevcnt+"\" id=\"strLodgingAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onchange=\"checkLodgingAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event);\"/>"+
			"</td></tr>"+
		
			"<tr id=\"trLocalConveyanceType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Local Conveyance Type:</td>"+
			"<td style=\"border: 0px none;\">"+
			"	<select name=\"localConveyanceTranType_"+fieldName+prevcnt+"\" id=\"localConveyanceTranType_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" onchange=\"checkLocalConveyanceAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\">"+
			"		<option value=''>Select Mode</option>"+
			""+sbLocalConveyanceTranTypeList+
    		"	</select>"+		
			"</td></tr>"+
		
			"<tr id=\"trLocalConveyanceRate_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Total KM:</td>"+
			"<td style=\"border: 0px none;\">"+
			"	<input type='text' name=\"localConveyanceKM_"+fieldName+prevcnt+"\" id=\"localConveyanceKM_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width:70px !important; text-align: right;\" onchange=\"checkLocalConveyanceAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;"+	
			"	Rate/KM:&nbsp;<input type='text' name=\"localConveyanceRate_"+fieldName+prevcnt+"\" id=\"localConveyanceRate_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width:63px !important; text-align: right;\" onchange=\"checkLocalConveyanceAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event)\"/>"+
			"</td></tr>"+
		
			"<tr id=\"trLocalConveyanceAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Local Conveyance Amount:</td>"+
			"<td style=\"border: 0px none;\">"+
			"	<input type='text' name=\"strLocalConveyanceAmount_"+fieldName+prevcnt+"\" id=\"strLocalConveyanceAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onkeypress=\"return isNumberKey(event);\"/>"+
			"</td></tr>"+
		
			"<tr id=\"trFoodBeverageAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Food &amp; Beverage:</td>"+
			"<td style=\"border: 0px none;\">"+
			"	<input type='text' name=\"strFoodBeverageAmount_"+fieldName+prevcnt+"\" id=\"strFoodBeverageAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onchange=\"checkFoodBeverageAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event);\"/>"+
			"</td></tr>"+
		
			"<tr id=\"trLaundryAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Laundry:</td>"+
			"<td style=\"border: 0px none;\">"+
			"	<input type='text' name=\"strLaundryAmount_"+fieldName+prevcnt+"\" id=\"strLaundryAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onchange=\"checkLaundryAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event);\"/>"+
			"</td></tr>"+
		
			"<tr id=\"trSundryAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"display: none;\">"+
			"<td class=\"alignRight\" style=\"border: 0px none;\">Sundry:</td>"+
			"<td style=\"border: 0px none;\">"+
			"	<input type='text' name=\"strSundryAmount_"+fieldName+prevcnt+"\" id=\"strSundryAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 81px !important; text-align: right;\" onchange=\"checkSundryAmount("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" onkeypress=\"return isNumberKey(event);\"/>"+
			"</td></tr>"+
			
			"<tr><td class=\"alignRight\" style=\"border: 0px none;\">Currency:<sup>*</sup></td>"+
    		"<td style=\"border: 0px none;\"><select name=\"reimbCurrency_"+fieldName+prevcnt+"\" id=\"reimbCurrency_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 85px !important;\" onchange=\"setSameCurrToAllReimbursement(this.id, '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" >"+
			""+sbReimbCurrencyList+
    		"	</select></td>"+
    		"</tr>"+
    		"<tr><td class=\"alignRight\" style=\"border: 0px none; display: none;\" id='tdAmt_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"'>Amount:<sup>*</sup></td>"+
    		"<td style=\"border: 0px none;\"><input type='text' name='strAmount_"+fieldName+prevcnt+"' id='strAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"'  onchange=\"setamount('"+reimType+"', "+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" class=\"validateRequired form-control\" style=\"width: 81px !important; text-align: right;\" onkeypress=\"return isNumberKey(event);\" /></td>"+
    		"</tr>"+
    		"<tr><td class=\"alignRight\" style=\"border: 0px none;\">Payment Mode:<sup>*</sup></td>"+
    		"<td style=\"border: 0px none;\"><select name=\"reimbPaymentMode_"+fieldName+prevcnt+"\" id=\"reimbPaymentMode_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 100px !important;\" >"+
				""+sbPaymentModeList+
    		"</select></td>"+
    		"</tr>"+
    		"</table>";
 	}
 	
 	if(reimType != 'T' && reimType != 'L') {
 		cell91.innerHTML = "<select name=\"reimbCurrency_"+fieldName+prevcnt+"\" id=\"reimbCurrency_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 85px !important;\" onchange=\"setSameCurrToAllReimbursement(this.id, '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" >"+
								""+sbReimbCurrencyList+"</select>";
 		cell92.innerHTML = "<input type='text' name='strAmount_"+fieldName+prevcnt+"' id='strAmount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"'  onchange=\"setamount('"+reimType+"', "+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" class=\"validateRequired form-control\" style=\"width: 81px !important; text-align: right;\" onkeypress=\"return isNumberKey(event);\" />";
 		cell93.innerHTML = "<select name=\"reimbPaymentMode_"+fieldName+prevcnt+"\" id=\"reimbPaymentMode_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"\" style=\"width: 100px !important;\" >"+
								""+sbPaymentModeList+"</select>";
 	}
    cell10.innerHTML = "<textarea rows='2' cols='25' name='strPurpose_"+fieldName+prevcnt+"' id='strPurpose_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"'></textarea>";
    if(reimType == 'P') {
    	cell11.innerHTML = "<input type='checkbox' name='isbillable_"+fieldName+prevcnt+"' id='isbillable_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' value='true' checked>";
    }
    cell12.innerHTML = "<input type='hidden' name='fileCount_"+fieldName+prevcnt+"' id='fileCount_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"' value='1'/>"+
	"		<input type='file' name='strDocument_"+fieldName+prevcnt+"' id='strDocument_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"'/>"+
	"		<a href='javascript:void(0)' onclick=\"addDoc("+prevcnt+", '"+fieldName+"', '"+saveType+"', '"+parentId+"');\" class='add-font'></a>"+
	"		<div id='div_documents_"+fieldName+saveType+"_"+parentId+"_"+prevcnt+"'></div>";
	
	document.getElementById("strDocument_"+fieldName+saveType+"_"+parentId+"_"+prevcnt).accept = '.gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs';
	if(reimType == 'P') {
		document.getElementById("projectCount"+saveType+"_"+parentId).value = prevcnt;
	}
	if(reimType == 'T') {
		document.getElementById("travelCount"+saveType+"_"+parentId).value = prevcnt;
	}
	if(reimType == 'L') {
		document.getElementById("localCount"+saveType+"_"+parentId).value = prevcnt;
	}
	if(reimType == 'M') {
		document.getElementById("mobileCount"+saveType+"_"+parentId).value = prevcnt;
	}
	
	setSelectedCurrToNewReimbursement("reimbCurrency_"+fieldName+saveType+"_"+parentId+"_"+prevcnt, fieldName, saveType, parentId);
	
	$("#fromDate_"+fieldName+saveType+'_'+parentId+'_'+prevcnt).datepicker({
	    format: 'dd/mm/yyyy',
	    autoclose: true
	}).on('changeDate', function (selected) {
	    var minDate = new Date(selected.date.valueOf());
	    $('#toDate_'+fieldName+saveType+'_'+parentId+'_'+prevcnt).datepicker('setStartDate', minDate);
	    $('#toDate_'+fieldName+saveType+'_'+parentId+'_'+prevcnt).datepicker('setDate', minDate);
	});
	
    $("#toDate_"+fieldName+saveType+'_'+parentId+'_'+prevcnt).datepicker({
		format: 'dd/mm/yyyy',
		//startDate : new Date(strMinDate),
		autoclose: true
	}).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#fromDate_'+fieldName+saveType+'_'+parentId+'_'+prevcnt).datepicker('setEndDate', minDate);
	});
    
//	document.getElementById("count").value = rowid;
	//othervalue('',prevcnt, fieldName);
	showType(reimType, prevcnt, fieldName, saveType, parentId);
	
	document.getElementById(""+rowid).style.borderBottom = "1px solid black;"; 
}



function setSelectedCurrToNewReimbursement(reimbId, fieldName, saveType, parentId) {
	//alert("reimbId ===>> " + reimbId);
	var projectCnt = document.getElementById("projectCount"+saveType+"_"+parentId).value;
	var travelCnt = document.getElementById("travelCount"+saveType+"_"+parentId).value;
	var localCnt = document.getElementById("localCount"+saveType+"_"+parentId).value;
	var mobileCnt = document.getElementById("mobileCount"+saveType+"_"+parentId).value;
	//alert("projectCnt ===>> " + projectCnt + " -- localCnt ===>> " + localCnt + " -- travelCnt ===>> " + travelCnt+ " -- mobileCnt ===>> " + mobileCnt);
	var selCnt = 0;
	for(var i=0; i<=projectCnt; i++) {
		if(selCnt == 0) {
			if(document.getElementById("reimbCurrency_project"+saveType+"_"+parentId+"_"+i) && "reimbCurrency_project"+saveType+"_"+parentId+"_"+i != reimbId) {
				document.getElementById(reimbId).selectedIndex = document.getElementById("reimbCurrency_project"+saveType+"_"+parentId+"_"+i).selectedIndex;
				selCnt++;
			}
		}
	}
	if(selCnt == 0) {
		for(var i=0; i<=travelCnt; i++) {
			if(selCnt == 0) {
				if(document.getElementById("reimbCurrency_travel"+saveType+"_"+parentId+"_"+i) && "reimbCurrency_travel"+saveType+"_"+parentId+"_"+i != reimbId) {
					document.getElementById(reimbId).selectedIndex = document.getElementById("reimbCurrency_travel"+saveType+"_"+parentId+"_"+i).selectedIndex;
					selCnt++;
				}
			}
		}
	}
	if(selCnt == 0) {
		for(var i=0; i<=localCnt; i++) {
			if(selCnt == 0) {
				if(document.getElementById("reimbCurrency_local"+saveType+"_"+parentId+"_"+i) && "reimbCurrency_local"+saveType+"_"+parentId+"_"+i != reimbId) {
					document.getElementById(reimbId).selectedIndex = document.getElementById("reimbCurrency_local"+saveType+"_"+parentId+"_"+i).selectedIndex;
					selCnt++;
				}
			}
		}
	}
	if(selCnt == 0) {
		for(var i=0; i<=mobileCnt; i++) {
			if(selCnt == 0) {
				if(document.getElementById("reimbCurrency_mobile"+saveType+"_"+parentId+"_"+i) && "reimbCurrency_mobile"+saveType+"_"+parentId+"_"+i != reimbId) {
					document.getElementById(reimbId).selectedIndex = document.getElementById("reimbCurrency_mobile"+saveType+"_"+parentId+"_"+i).selectedIndex;
					selCnt++;
				}
			}
		}
	}
}



function deleteRow(trIndex, tableName, saveType, expenseId, parentId) {
	//alert("trIndex ===>> " + trIndex);
	var lblrow = trIndex.parentNode.parentNode.rowIndex-1;
	//alert("lblrow ===>> " + lblrow);
	var row = trIndex.parentNode.parentNode;
	/* row.parentNode.removeChild(lblrow); */
	document.getElementById(tableName+'_'+parentId).deleteRow(lblrow);
	row.parentNode.removeChild(row);
	var table = document.getElementById(tableName+'_'+parentId);
    var rowCount = table.rows.length;
    //alert("rowCount ===>> " + rowCount);
    if(rowCount == 1) {
    	document.getElementById(tableName+'_'+parentId).style.display = 'none';
    }
    var travelRowCnt = document.getElementById('travelPlanTable_'+parentId).rows.length;
    var localRowCnt = document.getElementById('localTable_'+parentId).rows.length;
    var mobileRowCnt = document.getElementById('mobileBillTable_'+parentId).rows.length;
    var projectRowCnt = document.getElementById('projectTable_'+parentId).rows.length;
    if(travelRowCnt == 1 && localRowCnt == 1 && mobileRowCnt == 1 && projectRowCnt == 1) {
    	document.getElementById("workflowDiv_"+parentId).style.display = 'none';
    	document.getElementById("noExpenseMsgDiv").style.display = 'block';
    }
    
    var travelRowCntDraft = "1";
    var localRowCntDraft = "1";
    var mobileRowCntDraft = "1";
    var projectRowCntDraft = "1";
    if(document.getElementById("travelPlanTableDraft_"+parentId)) {
    	travelRowCntDraft = document.getElementById("travelPlanTableDraft_"+parentId).rows.length;
    }
	if(document.getElementById("localTableDraft_"+parentId)) {
		localRowCntDraft = document.getElementById("localTableDraft_"+parentId).rows.length;
	}
	if(document.getElementById("mobileBillTableDraft_"+parentId)) {
		mobileRowCntDraft = document.getElementById("mobileBillTableDraft_"+parentId).rows.length;
	}
	if(document.getElementById("projectTableDraft_"+parentId)) {
		projectRowCntDraft = document.getElementById("projectTableDraft_"+parentId).rows.length;
	}
    if(travelRowCntDraft == 1 && localRowCntDraft == 1 && mobileRowCntDraft == 1 && projectRowCntDraft == 1) {
    	document.getElementById("workflowDivDraft_"+parentId).style.display = 'none';
    	document.getElementById("noExpenseMsgDivDraft").style.display = 'block';
    }
    
    getContent(''+expenseId, 'BulkExpenses.action?btnRemove=Remove&expenseId='+expenseId);
}


function setSameCurrToAllReimbursement(reimbId, fieldName, saveType, parentId) {
	//alert("reimbId ===>> " + reimbId);
	var selIndex = document.getElementById(reimbId).selectedIndex;
	//alert("selIndex ===>> " + selIndex);
	var projectCnt = document.getElementById("projectCount"+saveType+"_"+parentId).value;
	var travelCnt = document.getElementById("travelCount"+saveType+"_"+parentId).value;
	var localCnt = document.getElementById("localCount"+saveType+"_"+parentId).value;
	var mobileCnt = document.getElementById("mobileCount"+saveType+"_"+parentId).value;
	//alert("projectCnt ===>> " + projectCnt + " -- localCnt ===>> " + localCnt + " -- travelCnt ===>> " + travelCnt+ " -- mobileCnt ===>> " + mobileCnt);
	for(var i=0; i<=projectCnt; i++) {
		if(document.getElementById("reimbCurrency_project"+saveType+"_"+parentId+"_"+i)) {
			document.getElementById("reimbCurrency_project"+saveType+"_"+parentId+"_"+i).selectedIndex = selIndex;
		}
	}
	for(var i=0; i<=travelCnt; i++) {
		if(document.getElementById("reimbCurrency_travel"+saveType+"_"+parentId+"_"+i)) {
			document.getElementById("reimbCurrency_travel"+saveType+"_"+parentId+"_"+i).selectedIndex = selIndex;
		}
	}
	for(var i=0; i<=localCnt; i++) {
		if(document.getElementById("reimbCurrency_local"+saveType+"_"+parentId+"_"+i)) {
			document.getElementById("reimbCurrency_local"+saveType+"_"+parentId+"_"+i).selectedIndex = selIndex;
		}
	}
	for(var i=0; i<=mobileCnt; i++) {
		if(document.getElementById("reimbCurrency_mobile"+saveType+"_"+parentId+"_"+i)) {
			document.getElementById("reimbCurrency_mobile"+saveType+"_"+parentId+"_"+i).selectedIndex = selIndex;
		}
	}
}


function checkTransportType(cnt, fieldName, saveType, parentId) {
	var transportType = document.getElementById("transportType_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	if(parseInt(transportType) == 1) {
		document.getElementById("trTrain_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'table-row';
		document.getElementById("trBus_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trFlight_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trCar_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
	} else if(parseInt(transportType) == 2) {
		document.getElementById("trTrain_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trBus_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'table-row';
		document.getElementById("trFlight_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trCar_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
	} else if(parseInt(transportType) == 3) {
		document.getElementById("trTrain_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trBus_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trFlight_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'table-row';
		document.getElementById("trCar_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
	} else if(parseInt(transportType) == 4) {
		document.getElementById("trTrain_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trBus_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trFlight_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trCar_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'table-row';
	} else {
		document.getElementById("trTrain_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trBus_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trFlight_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
		document.getElementById("trCar_"+fieldName+saveType+"_"+parentId+"_"+cnt).style.display = 'none';
	}
	
	checkTransportAmount(cnt, fieldName, saveType, parentId);
}

function checkTransportAmount(cnt, fieldName, saveType, parentId) {
	var strTransAmount = document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	var hiddenTravelType = document.getElementById("hiddenTravelType_"+fieldName+saveType+"_"+parentId).value;
	var transportType = document.getElementById("transportType_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	if(parseInt(transportType) == 1 && document.getElementById("hiddenTrainType_"+fieldName+saveType+"_"+parentId)) {
		var hiddenTrainType = document.getElementById("hiddenTrainType_"+fieldName+saveType+"_"+parentId).value;
		var trainType = document.getElementById("trainType_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		if(parseInt(hiddenTrainType) == parseInt(trainType)) {
			var limit = document.getElementById("train_"+fieldName+saveType+"_"+parentId+"_"+hiddenTravelType+"_"+hiddenTrainType).value;
			if(parseFloat(strTransAmount) <= parseFloat(limit)) {
				document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = strTransAmount;
			} else {
				alert('Enter proper amount upto '+limit);
				document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = '';    			
			}	
		}
		sumTotalTravelAmt(cnt, fieldName, saveType, parentId);
	} else if(parseInt(transportType) == 2 && document.getElementById("hiddenBusType_"+fieldName+saveType+"_"+parentId)){
		var hiddenBusType = document.getElementById("hiddenBusType_"+fieldName+saveType+"_"+parentId).value;
		var busType = document.getElementById("busType_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		var limit = document.getElementById("bus_"+fieldName+saveType+"_"+parentId+"_"+hiddenTravelType+"_"+hiddenBusType).value;
		if(parseInt(hiddenBusType) == parseInt(busType)){
			if(parseFloat(strTransAmount) <= parseFloat(limit)){
				document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = strTransAmount;
			} else {
				alert('Enter proper amount upto '+limit);
				document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = '';    			
			}	
		}
		sumTotalTravelAmt(cnt, fieldName, saveType, parentId);
	} else if(parseInt(transportType) == 3 && document.getElementById("hiddenFlightType_"+fieldName+saveType+"_"+parentId)) {
		var hiddenFlightType = document.getElementById("hiddenFlightType_"+fieldName+saveType+"_"+parentId).value;
		var flightType = document.getElementById("flightType_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		var limit = document.getElementById("flight_"+fieldName+saveType+"_"+parentId+"_"+hiddenTravelType+"_"+hiddenFlightType).value;
		if(parseInt(hiddenFlightType) == parseInt(flightType)) {
			if(parseFloat(strTransAmount) <= parseFloat(limit)) {
				document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = strTransAmount;
			} else {
				alert('Enter proper amount upto '+limit);
				document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = '';    			
			}	
		}
		sumTotalTravelAmt(cnt, fieldName, saveType, parentId);
	} else if(parseInt(transportType) == 4 && document.getElementById("hiddenCarType_"+fieldName+saveType+"_"+parentId)) {
		var hiddenCarType = document.getElementById("hiddenCarType_"+fieldName+saveType+"_"+parentId).value;
		var carType = document.getElementById("carType_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		var limit = document.getElementById("car_"+fieldName+saveType+"_"+parentId+"_"+hiddenTravelType+"_"+hiddenCarType).value;
		if(parseInt(hiddenCarType) == parseInt(carType)) {
			if(parseFloat(strTransAmount) <= parseFloat(limit)){
				document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = strTransAmount;
			} else {
				alert('Enter proper amount upto '+limit);
				document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = '';    			
			}	
		}
		sumTotalTravelAmt(cnt, fieldName, saveType, parentId);
	} 	
}

function checkLodgingAmount(cnt, fieldName, saveType, parentId) {
	var lodgingType = document.getElementById("lodgingType_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	if(parseInt(lodgingType) == 9) {
		var lodgingLimit = document.getElementById("lodgingLimit_"+fieldName+saveType+"_"+parentId).value;
		var strLodgingAmount = document.getElementById("strLodgingAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		var strTravelPlanDays = document.getElementById("strTravelPlanDays_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		
		var totalLodingLimit = parseFloat(lodgingLimit) * parseFloat(strTravelPlanDays);
		if(parseFloat(strLodgingAmount) <= parseFloat(totalLodingLimit)) {
			document.getElementById("strLodgingAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value=strLodgingAmount;
		} else {
			alert('Enter proper amount upto '+lodgingLimit);
			document.getElementById("strLodgingAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value='';    			
		}	 
	}	
	sumTotalTravelAmt(cnt, fieldName, saveType, parentId);
}


function checkLocalConveyanceAmount(cnt, fieldName, saveType, parentId) {
	var localConveyanceTranType = document.getElementById("localConveyanceTranType_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	if(document.getElementById("localConveyanceLimit_"+fieldName+localConveyanceTranType)) {
		var localConveyanceLimit = document.getElementById("localConveyanceLimit_"+fieldName+saveType+"_"+parentId+"_"+localConveyanceTranType).value;
		var localConveyanceRate = document.getElementById("localConveyanceRate_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		var strTravelPlanDays = document.getElementById("strTravelPlanDays_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		
		var totalLocalConveyanceLimit = parseFloat(localConveyanceLimit);
		if(parseFloat(localConveyanceRate) <= parseFloat(totalLocalConveyanceLimit)) {
			document.getElementById("localConveyanceRate_"+fieldName+saveType+"_"+parentId+"_"+cnt).value=localConveyanceRate;
		} else {
			alert('Enter proper km upto '+totalLocalConveyanceLimit);
			document.getElementById("localConveyanceRate_"+fieldName+saveType+"_"+parentId+"_"+cnt).value='';  			
		}	 
	}
	calLocalConveyanceAmount(cnt, fieldName, saveType, parentId);
}

function calLocalConveyanceAmount(cnt, fieldName, saveType, parentId) {
	var localConveyanceKM = document.getElementById("localConveyanceKM_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
    var localConveyanceRate = document.getElementById("localConveyanceRate_"+fieldName+saveType+"_"+parentId+"_"+cnt).value ;
	var totalAmt = localConveyanceKM * localConveyanceRate;
	document.getElementById("strLocalConveyanceAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = totalAmt;
	
	sumTotalTravelAmt(cnt, fieldName, saveType, parentId);
}

function checkFoodBeverageAmount(cnt, fieldName, saveType, parentId) {
	if(document.getElementById("foodBeverageLimit_"+fieldName+saveType+"_"+parentId)) {
		var foodBeverageLimit = document.getElementById("foodBeverageLimit_"+fieldName+saveType+"_"+parentId).value;
		var strFoodBeverageAmount = document.getElementById("strFoodBeverageAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		var strTravelPlanDays = document.getElementById("strTravelPlanDays_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		
		var totalFoodBeverageLimit = parseFloat(foodBeverageLimit) * parseFloat(strTravelPlanDays);
		if(parseFloat(strFoodBeverageAmount) <= parseFloat(totalFoodBeverageLimit)) {
			document.getElementById("strFoodBeverageAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = strFoodBeverageAmount;
		} else {
			alert('Enter proper amount upto '+totalFoodBeverageLimit);
			document.getElementById("strFoodBeverageAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = '';    			
		}
	}
	sumTotalTravelAmt(cnt, fieldName, saveType, parentId);
}

function checkLaundryAmount(cnt, fieldName, saveType, parentId) {
	if(document.getElementById("laundryLimit_"+fieldName+saveType+"_"+parentId)) {
		var laundryLimit = document.getElementById("laundryLimit_"+fieldName+saveType+"_"+parentId).value;
		var strLaundryAmount = document.getElementById("strLaundryAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		var strTravelPlanDays = document.getElementById("strTravelPlanDays_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		
		var totalLaundryLimit= parseFloat(laundryLimit) * parseFloat(strTravelPlanDays);
		if(parseFloat(strLaundryAmount) <= parseFloat(totalLaundryLimit)) {
			document.getElementById("strLaundryAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = strLaundryAmount;
		} else {
			alert('Enter proper amount upto '+totalLaundryLimit);
			document.getElementById("strLaundryAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = '';    			
		}
	}
	sumTotalTravelAmt(cnt, fieldName, saveType, parentId);
}


function checkSundryAmount(cnt, fieldName, saveType, parentId) {
	if(document.getElementById("sundryLimit")) {
		var sundryLimit = document.getElementById("sundryLimit_"+fieldName+saveType+"_"+parentId).value;
		var strSundryAmount = document.getElementById("strSundryAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		var strTravelPlanDays = document.getElementById("strTravelPlanDays_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
		
		var totalSundryLimit = parseFloat(sundryLimit) * parseFloat(strTravelPlanDays);
		if(parseFloat(strSundryAmount) <= parseFloat(totalSundryLimit)) {
			document.getElementById("strSundryAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = strSundryAmount;
		} else {
			alert('Enter proper amount upto '+totalSundryLimit);
			document.getElementById("strSundryAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = '';    			
		}
	}
	sumTotalTravelAmt(cnt,fieldName, saveType, parentId);
}


function sumTotalTravelAmt(cnt,fieldName, saveType, parentId) {
	var strTransAmount = document.getElementById("strTransAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	var strLodgingAmount = document.getElementById("strLodgingAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	var strLocalConveyanceAmount = document.getElementById("strLocalConveyanceAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	var strFoodBeverageAmount = document.getElementById("strFoodBeverageAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	var strLaundryAmount = document.getElementById("strLaundryAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	var strSundryAmount = document.getElementById("strSundryAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value;
	var totalAmt = 0;
	if(isInt(strTransAmount) || isFloat(strTransAmount)) {
		totalAmt += parseFloat(strTransAmount);
	}
	if(isInt(strLodgingAmount) || isFloat(strLodgingAmount)) {
		totalAmt += parseFloat(strLodgingAmount);
	}
	if(isInt(strLocalConveyanceAmount) || isFloat(strLocalConveyanceAmount)) {
		totalAmt += parseFloat(strLocalConveyanceAmount);
	}
	if(isInt(strFoodBeverageAmount) || isFloat(strFoodBeverageAmount)) {
		totalAmt += parseFloat(strFoodBeverageAmount);
	}
	if(isInt(strLaundryAmount) || isFloat(strLaundryAmount)) {
		totalAmt += parseFloat(strLaundryAmount);
	}
	if(isInt(strSundryAmount) || isFloat(strSundryAmount)) {
		totalAmt += parseFloat(strSundryAmount);
	}
	document.getElementById("strAmount_"+fieldName+saveType+"_"+parentId+"_"+cnt).value = parseFloat(totalAmt); 
}


function isInt(n) {
    return n != "" && !isNaN(n) && Math.round(n) == n;
}

function isFloat(n) {
    return n != "" && !isNaN(n) && Math.round(n) != n;
}


function getTravelPlanDetails(val, cnt, fieldName, saveType, parentId) {
	document.getElementById("strTravelPlanDays_"+fieldName+saveType+"_"+parentId+"_"+cnt).value='';
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var xhr = $.ajax({
			url : 'GetTravelPlanDetails.action?empId='+document.getElementById('strSessionEmpId').value+'&travelId='+val,
			cache : false,
			success : function(data) {
				document.getElementById("strTravelPlanDays_"+fieldName+saveType+"_"+parentId+"_"+cnt).value=data;
			}
		});
	}
}

function closeForm() {
	window.location = "MyPay.action?callFrom=MyDashReimbursements";
}

</script>


	<section class="content">
    	<div class="row jscroll">
    		<section class="col-lg-12 connectedSortable">
        		<div class="box" style="border-top: 0px;">
            		<div class="box-header with-border">
						<section class="col-lg-4 connectedSortable">
                  			<div class="box box-primary">
	                    		<div class="box-header with-border"> <!-- data-widget="collapse-full" -->
	                        		<h3 class="box-title" style="font-weight: normal;">Click on <b>Expense Type</b> to add in the block on right</h3>
	                    		</div>
	                    <!-- /.box-header -->
			                    <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
			                        <ul>
			                        	<li id="add_new"><a href="javascript:void(0);" onclick="addRow('T', 'travelPlanTable', 'travelPlanTR', 'travel', '', '0');" class="add-font"> Add Travel </a></li> 
			                        	<li id="add_new1"><a href="javascript:void(0);" onclick="addRow('L', 'localTable', 'localTR', 'local', '', '0');" class="add-font"> Add Local </a></li>
			                        	<li id="add_new2"><a href="javascript:void(0)" onclick="addRow('M', 'mobileBillTable', 'mobileBillTR', 'mobile', '', '0');" class="add-font"> Add Mobile Bill </a></li>
			                        	<li id="add_new3"><a href="javascript:void(0)" onclick="addRow('P', 'projectTable', 'projectTR', 'project', '', '0');" class="add-font"> Add Project </a></li>
			                       	</ul>
			                    </div>
			                    <!-- /.box-body -->
			                </div>
						</section>
	
			 	<section class="col-lg-8 connectedSortable">
			 	<s:form theme="simple" action="BulkExpenses" name="frmBulkExpenses_<%=policy_id %>" method="post" id="frmBulkExpenses_<%=policy_id %>" cssClass="formcss" enctype="multipart/form-data">
					<%-- <input type="hidden" name="parentId" id="parentId" value="<%=(String)request.getAttribute("parentId") %>" /> --%>
					<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id %>"/>
					<div class="box box-primary">
						<div class="box-header with-border" data-widget="collapse-full"><h3 class="box-title">New Expenses</h3>
						<div class="box-tools pull-right">
							<a href="javascript:void(0);" onclick="closeForm()" class="close-font" style="margin-right:20px;">
						</a></div>
						
						</div>
						<br>
                    <!-- /.box-header -->
					<div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 320px;">
                    	<div class="widget-content nopadding updates" style="height: auto; background: white;">
							<table id="travelPlanTable_0" style="display: none;" class="table pdzn_tbl1"> <!-- table-bordered -->
                                   <tr id="1" class="bb">
                                       <th colspan="9" style="border-top: 0px solid;"></th>
                                       <%-- <th nowrap="nowrap" class="alignCenter">Travel Plan<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top"><span style="float: left;">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Number of Persons<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Vendor</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Receipt No.</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Amount<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Purpose</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Attach Document</th> --%>
                                   </tr>
							</table>

							<table id="localTable_0" style="display: none;" class="table pdzn_tbl1">
                                  <tr id="1" class="bb">
                                      <th colspan="9" style="border-top: 0px solid;"></th>
                                      <%-- <th nowrap="nowrap" class="alignCenter" id="othr1">Type<sup>*</sup></th>
                                      <th nowrap="nowrap" class="alignRight" valign="top"><span style="float: left;">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span></th>
                                      <th nowrap="nowrap" class="alignRight" valign="top">Number of Persons<sup>*</sup></th>
                                      <th nowrap="nowrap" class="alignRight" valign="top">Vendor</th>
                                      <th nowrap="nowrap" class="alignRight" valign="top">Receipt No.</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Amount<sup>*</sup></th>
                                      <th nowrap="nowrap" class="alignRight" valign="top">Purpose</th>
                                      <th nowrap="nowrap" class="alignRight" valign="top">Attach Document</th> --%>
                                  </tr>
		                  </table>
		                  
		                  <table id="mobileBillTable_0" style="display: none;" class="table pdzn_tbl1">
                                   <tr id="1" class="bb">
                                       <th colspan="10" style="border-top: 0px solid;"></th>
                                       <%-- <th nowrap="nowrap" class="alignRight" valign="top"><span style="float: left;">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Number of Persons<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Vendor</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Receipt No.</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Currency</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Amount<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Payment Mode</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Purpose</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Attach Document</th> --%>
                                   </tr>
		                  </table>
		                  
		                  <table id="projectTable_0" style="display: none;" class="table pdzn_tbl1">
                                   <tr id="1" class="bb">
                                       <th colspan="13" style="border-top: 0px solid;"></th>
                                       <%-- <th nowrap="nowrap" class="alignCenter" id="client1">Client<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignCenter" id="pro1">Project<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top"><span style="float: left;">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Number of Persons<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Vendor</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Receipt No.</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Currency</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Amount<sup>*</sup></th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Payment Mode</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Purpose</th>
                                       <th nowrap="nowrap" id="isbillfirst1" class="alignRight" valign="top">Chargeable to client</th>
                                       <th nowrap="nowrap" class="alignRight" valign="top">Attach Document</th> --%>
                                   </tr>
		                  </table>
		                  
		                  <div id="noExpenseMsgDiv" class="filter"><div class="msg nodata"><span>Please select expense type.</span></div></div>
					</div>
				</div>
		        <!-- /.box-body -->
		        <div id="workflowDiv_0" style="display: none;">
					<input type="hidden" name="travelCount" id="travelCount_0" value="0">
					<input type="hidden" name="localCount" id="localCount_0" value="0">
					<input type="hidden" name="mobileCount" id="mobileCount_0" value="0">
					<input type="hidden" name="projectCount" id="projectCount_0" value="0">
					<table class="table table_no_border">
					
						<%
							if(uF.parseToBoolean(CF.getIsWorkFlow())) {
	 							if(hmMemberOption != null && !hmMemberOption.isEmpty() ) {
									Iterator<String> it1 = hmMemberOption.keySet().iterator();
									while(it1.hasNext()) {
										String memPosition=it1.next();
										String optiontr=hmMemberOption.get(memPosition);					
										out.println(optiontr); 
									}
						%>
							<tr>
    							<td>&nbsp;</td>
    							<td>
        							<input type="submit" id="saveButton" name="btnSave" class="btn btn-primary" value="Save" style="float:left;">
									<input type="submit" id="submitButton" name="btnSubmit" value="Submit" class="btn btn-primary">
    							</td>
							</tr>
						<% } else { %>
							<tr>
    							<td>Your work flow is not defined. Please, speak to your hr for your workflow.</td>
							</tr>
						<% } %>
						<% } else { %>
							<tr>
    							<td>&nbsp;</td>
    							<td>
        							<input type="submit" id="saveButton" name="btnSave" class="btn btn-primary" value="Save" style="float:left;">
									<input type="submit" id="submitButton" name="btnSubmit" value="Submit" class="btn btn-primary">
    							</td>
							</tr>
						<% } %>
					</table>
				</div>
			</div>
		</s:form>
         
         
         <% Iterator<String> it = hmParentIdBulkExpenseData.keySet().iterator();
         	int cntDraft=0;
         	while(it.hasNext()) {
         		String parentId = it.next();
         		Map<String, List<List<String>>> hmBulkExpenseData = hmParentIdBulkExpenseData.get(parentId);
         	    if(hmBulkExpenseData == null) hmBulkExpenseData = new HashMap<String, List<List<String>>>();
         	   cntDraft++;
         %>
			<div class="box box-primary">
				<div class="box-header with-border"><h3 class="box-title">Draft <%=cntDraft %> saved on <%=hmDraftSavedOn.get(parentId) %></h3></div>  <!-- data-widget="collapse-full" -->
			<!-- /.box-header -->
				<s:form theme="simple" action="BulkExpenses" name="frmBulkExpenses_<%=parentId %>" method="post" id="frmBulkExpenses_<%=parentId %>" cssClass="formcss" enctype="multipart/form-data">
					<input type="hidden" name="parentId" id="parentId" value="<%=parentId %>" />
					<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id %>"/>
					<div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 320px;">
						<div class="widget-content nopadding updates" style="height: auto; background: white;">
							<% 
								List<List<String>> alBulkExpenseData = hmBulkExpenseData.get("T");
								int trCount = 0;
								int travelTrSize = 0;
								int localTrSize = 0;
								int mobileTrSize = 0;
								int projectTrSize = 0;
								if(alBulkExpenseData == null) alBulkExpenseData = new ArrayList<List<String>>();
								if(alBulkExpenseData.size() > 0) {
									travelTrSize = alBulkExpenseData.size();
							%>
							<table id="travelPlanTableDraft_<%=parentId %>" style="display: block;" class="table pdzn_tbl1"> <!-- table-bordered -->
								<tbody>
									<%
										for(int i=0; alBulkExpenseData.size()>0 && i<alBulkExpenseData.size(); i++) {
											trCount++;
											List<String> innerList = alBulkExpenseData.get(i);
									%>
									<tr id="1" class="bb">
										<th class="alignCenter" nowrap="nowrap">Add</th>
										<th class="alignCenter" nowrap="nowrap">Travel Plan<sup>*</sup></th>
										<th nowrap="nowrap" class="alignRight" valign="top"><span style="float: left;">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span></th>
										<th class="alignRight" valign="top" nowrap="nowrap">Number of Persons<sup>*</sup></th>
										<th class="alignRight" valign="top" nowrap="nowrap">Vendor</th>
										<th class="alignRight" valign="top" nowrap="nowrap">Receipt No.</th>
										<th class="alignRight" valign="top" nowrap="nowrap">Amount<sup>*</sup></th>
										<th class="alignRight" valign="top" nowrap="nowrap">Purpose</th>
										<th class="alignRight" valign="top" nowrap="nowrap">Attach Document</th>
									</tr>
									<tr id="travelPlanTRDraft_<%=parentId %>_<%=trCount %>" class="bb">
										<td valign="top" nowrap="nowrap">
											<input type="hidden" name="travelReimbursementId<%=trCount %>" id="travelReimbursementId_<%=parentId %>_<%=trCount %>" value="<%=innerList.get(36) %>" />
											<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to remove this record?'))deleteRow(this, 'travelPlanTableDraft', 'Draft', '<%=innerList.get(36) %>', '<%=parentId %>')" class="remove-font"></a>
											<a href="javascript:void(0)" onclick="addRow('T', 'travelPlanTableDraft', 'travelPlanTRDraft', 'travel', 'Draft', '<%=parentId %>');" class="add-font"></a>
										</td>
										<td valign="top" nowrap="nowrap">
											<select name="strTravelPlan_travel<%=trCount %>" id="strTravelPlan_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" onchange="getTravelPlanDetails(this.value, '<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');">
												<option value="">Select Travel Plan</option>
												<%=innerList.get(2) %>
											</select>
											<input name="strTravelPlanDays_travel<%=trCount %>" id="strTravelPlanDays_travelDraft_<%=parentId %>_<%=trCount %>" value="0" type="hidden">
										</td>
										<td valign="top" nowrap="nowrap">
											<input name="fromDate_travel<%=trCount %>" id="fromDate_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired" type="text" value="<%=innerList.get(37) %>" style="width: 90px !important;"/>&nbsp;&nbsp;
											<input name="toDate_travel<%=trCount %>" id="toDate_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired" type="text" value="<%=innerList.get(38) %>" style="width: 90px !important;"/>
										</td>
										
										<script type="text/javascript">
											$(function(){
												$("#fromDate_travelDraft_<%=parentId %>_<%=trCount %>").datepicker({
												    format: 'dd/mm/yyyy',
												    autoclose: true
												}).on('changeDate', function (selected) {
												    var minDate = new Date(selected.date.valueOf());
												    $('#toDate_travelDraft_<%=parentId %>_<%=trCount %>').datepicker('setStartDate', minDate);
												    $('#toDate_travelDraft_<%=parentId %>_<%=trCount %>').datepicker('setDate', minDate);
												});
												
											    $("#toDate_travelDraft_<%=parentId %>_<%=trCount %>").datepicker({
													format: 'dd/mm/yyyy',
													//startDate : new Date(strMinDate),
													autoclose: true
												}).on('changeDate', function (selected) {
											        var minDate = new Date(selected.date.valueOf());
											        $('#fromDate_travelDraft_<%=parentId %>_<%=trCount %>').datepicker('setEndDate', minDate);
												});
											});
										</script>
										
										<td valign="top" nowrap="nowrap">
											<input name="noofperson_travel<%=trCount %>" id="noofperson_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" type="text" value="<%=innerList.get(9) %>" style="width: 140px !important;" onkeypress="return isNumberKey(event);" />
										</td>
										<td valign="top" nowrap="nowrap">
											<input name="strVendor_travel<%=trCount %>" id="strVendor_travelDraft_<%=parentId %>_<%=trCount %>" type="text" value="<%=innerList.get(19) %>">
										</td>
										<td valign="top" nowrap="nowrap">
											<div id="divReceiptNo_travelDraft_<%=parentId %>_<%=trCount %>">
											<% String[] strReceiptNo = innerList.get(20).split(":_:");
												int intCnt = 0;
												for(int j=0; j<strReceiptNo.length; j++) {
													if(strReceiptNo[j] != null && strReceiptNo[j].length()>0) {
														intCnt++;
											%>
												<div id="row_receipt_travelDraft_<%=parentId %>_<%=trCount %><%=intCnt %>">
													<input name="strReceiptNo_travel<%=trCount %>" id="strReceiptNo_travelDraft_<%=parentId %>_<%=trCount %>" type="text" value="<%=strReceiptNo[j] %>" style="width: 140px !important;" />
													<a href="javascript:void(0)" onclick="addReceiptNo('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>')" class="add-font"></a>
													<% if(intCnt > 1) { %>
														<a href="javascript:void(0)" onclick="removeReceiptNo('<%=intCnt %>', '<%=trCount %>', 'travel', 'Draft', '<%=parentId %>')" id="removeReceiptId_travelDraft<%=intCnt %>" class="remove-font"></a>
													<% } %>
												</div>
											<% } } %>
											</div>
											<input name="receiptNoCount_travel<%=trCount %>" id="receiptNoCount_travelDraft_<%=parentId %>_<%=trCount %>" value="<%=intCnt %>" type="hidden">
										</td>
										<td valign="top" nowrap="nowrap">
											<table class="table" id="innerTableDraft_<%=parentId %>_<%=trCount %>" cellspacing="0" cellpadding="0">
												<tbody>
													<tr id="trTransType_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Transportation Type:<sup>*</sup></td>
														<td colspan="3" style="border: 0px none;">
															<select name="transportType_travel<%=trCount %>" id="transportType_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" onchange="checkTransportType('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');">
																<option value="">Select Transport Type</option>
																<option value="1" <%if(innerList.get(21).equals("1")) { %> selected <% } %>>Train</option>
																<option value="2" <%if(innerList.get(21).equals("2")) { %> selected <% } %>>Bus</option>
																<option value="3" <%if(innerList.get(21).equals("3")) { %> selected <% } %>>Flight</option>
																<option value="4" <%if(innerList.get(21).equals("4")) { %> selected <% } %>>Car</option>
															</select>
														</td>
													</tr>
													<tr id="trTrain_travelDraft_<%=parentId %>_<%=trCount %>" style="display: none;">
														<td class="alignRight" style="border: 0px none;">Train:<sup>*</sup></td>
														<td style="border: 0px none;">
															<select name="trainType_travel<%=trCount %>" id="trainType_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" onchange="checkTransportAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');">
																<option value="1" <%if(innerList.get(21).equals("1") && innerList.get(22).equals("1")) { %> selected <% } %>>3 Tier</option>
																<option value="2" <%if(innerList.get(21).equals("1") && innerList.get(22).equals("2")) { %> selected <% } %>>Chair Car</option>
																<option value="3" <%if(innerList.get(21).equals("1") && innerList.get(22).equals("3")) { %> selected <% } %>>AC 3 Tier</option>
																<option value="4" <%if(innerList.get(21).equals("1") && innerList.get(22).equals("4")) { %> selected <% } %>>AC 2 Tier</option>
																<option value="5" <%if(innerList.get(21).equals("1") && innerList.get(22).equals("5")) { %> selected <% } %>>AC 1st Class</option>
															</select>
														</td>
													</tr>
													<tr id="trBus_travelDraft_<%=parentId %>_<%=trCount %>" style="display: none;">
														<td class="alignRight" style="border: 0px none;">Bus:<sup>*</sup></td>
														<td style="border: 0px none;">
															<select name="busType_travel<%=trCount %>" id="busType_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" onchange="checkTransportAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');">
																<option value="1" <%if(innerList.get(21).equals("2") && innerList.get(22).equals("1")) { %> selected <% } %>>A/c Bus</option>
																<option value="2" <%if(innerList.get(21).equals("2") && innerList.get(22).equals("2")) { %> selected <% } %>>Non- A/c Bus</option>
															</select>
														</td>
													</tr>
													<tr id="trFlight_travelDraft_<%=parentId %>_<%=trCount %>" style="display: none;">
														<td class="alignRight" style="border: 0px none;">Flight:<sup>*</sup></td>
														<td style="border: 0px none;">
															<select name="flightType_travel<%=trCount %>" id="flightType_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" onchange="checkTransportAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');">
																<option value="1" <%if(innerList.get(21).equals("3") && innerList.get(22).equals("1")) { %> selected <% } %>>Economy Class</option>
																<option value="2" <%if(innerList.get(21).equals("3") && innerList.get(22).equals("2")) { %> selected <% } %>>Business Class</option>
															</select>
														</td>
													</tr>
													<tr id="trCar_travelDraft_<%=parentId %>_<%=trCount %>" style="display: none;">
														<td class="alignRight" style="border: 0px none;">Car:<sup>*</sup></td>
														<td colspan="3" style="border: 0px none;">
															<select name="carType_travel<%=trCount %>" id="carType_travelDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" onchange="checkTransportAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');">
																<option value="1" <%if(innerList.get(21).equals("4") && innerList.get(22).equals("1")) { %> selected <% } %>>Cab</option>
																<option value="2" <%if(innerList.get(21).equals("4") && innerList.get(22).equals("2")) { %> selected <% } %>>Self Owned</option>
															</select>
														</td>
													</tr>
													<tr id="trTransAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Transport Amount:<sup>*</sup></td>
														<td style="border: 0px none;">
															<input name="strTransAmount_travel<%=trCount %>" id="strTransAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="width: 81px !important; text-align: right;" class="validateRequired form-control" value="<%=innerList.get(23) %>" onchange="checkTransportAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" onkeypress="return isNumberKey(event);" type="text">
														</td>
													</tr>
													<tr id="trLodgingType_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Lodging Type:</td>
														<td style="border: 0px none;">
															<select name="lodgingType_travel<%=trCount %>" id="lodgingType_travelDraft_<%=parentId %>_<%=trCount %>" onchange="checkLodgingAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');">
																<option value="">Select Lodging Type</option>
																<!-- <option value="1">2 Star- Single Occupancy</option><option value="2">2 Star- Double Occupancy</option><option value="3">3 Star- Single Occupancy</option><option value="4">3 Star- Double Occupancy</option><option value="5">4 Star- Single Occupancy</option><option value="6">4 Star- Double Occupancy</option><option value="7">5 Star- Single Occupancy</option><option value="8">5 Star- Double Occupancy</option><option value="9">Service Apartment</option> -->
																<%=innerList.get(24) %>
															</select>
														</td>
													</tr>
													<tr id="trLodgingAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Lodging Amount:</td>
														<td style="border: 0px none;">
															<input name="strLodgingAmount_travel<%=trCount %>" id="strLodgingAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="width: 81px !important; text-align: right;" value="<%=innerList.get(25) %>" onchange="checkLodgingAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" onkeypress="return isNumberKey(event);" type="text">
														</td>
													</tr>
													<tr id="trLocalConveyanceType_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Local Conveyance Type:</td>
														<td style="border: 0px none;">
															<select name="localConveyanceTranType_travel<%=trCount %>" id="localConveyanceTranType_travelDraft_<%=parentId %>_<%=trCount %>" onchange="checkLocalConveyanceAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');">
																<option value="">Select Mode</option>
																<!-- <option value="Owned vehical- 2 Wheeler">Owned vehical- 2 Wheeler</option><option value="Owned vehical- 4 Wheeler">Owned vehical- 4 Wheeler</option><option value="Bus">Bus</option><option value="Train">Train</option><option value="Taxi">Taxi</option><option value="Auto">Auto</option><option value="Two Wheeler">Two Wheeler</option><option value="Other">Other</option> -->
																<%=innerList.get(26) %>
															</select>
														</td>
													</tr>
													<tr id="trLocalConveyanceRate_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Total KM:</td>
														<td style="border: 0px none;">
															<input name="localConveyanceKM_travel<%=trCount %>" id="localConveyanceKM_travelDraft_<%=parentId %>_<%=trCount %>" style="width:70px !important; text-align: right;" value="<%=innerList.get(27) %>" onchange="checkLocalConveyanceAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" onkeypress="return isNumberKey(event)" type="text">&nbsp;	Rate/KM:&nbsp;
															<input name="localConveyanceRate_travel<%=trCount %>" id="localConveyanceRate_travelDraft_<%=parentId %>_<%=trCount %>" style="width:63px !important; text-align: right;" value="<%=innerList.get(28) %>" onchange="checkLocalConveyanceAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" onkeypress="return isNumberKey(event)" type="text">
														</td>
													</tr>
													<tr id="trLocalConveyanceAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Local Conveyance Amount:</td>
														<td style="border: 0px none;">
															<input name="strLocalConveyanceAmount_travel<%=trCount %>" id="strLocalConveyanceAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="width: 81px !important; text-align: right;" value="<%=innerList.get(29) %>" onkeypress="return isNumberKey(event);" type="text">
														</td>
													</tr>
													<tr id="trFoodBeverageAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Food &amp; Beverage:</td>
														<td style="border: 0px none;">
															<input name="strFoodBeverageAmount_travel<%=trCount %>" id="strFoodBeverageAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="width: 81px !important; text-align: right;" value="<%=innerList.get(30) %>" onchange="checkFoodBeverageAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" onkeypress="return isNumberKey(event);" type="text">
														</td>
													</tr>
													<tr id="trLaundryAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Laundry:</td>
														<td style="border: 0px none;">
															<input name="strLaundryAmount_travel<%=trCount %>" id="strLaundryAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="width: 81px !important; text-align: right;" onchange="checkLaundryAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" value="<%=innerList.get(31) %>" onkeypress="return isNumberKey(event);" type="text">
														</td>
													</tr>
													<tr id="trSundryAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="display: table-row;">
														<td class="alignRight" style="border: 0px none;">Sundry:</td>
														<td style="border: 0px none;">
															<input name="strSundryAmount_travel<%=trCount %>" id="strSundryAmount_travelDraft_<%=parentId %>_<%=trCount %>" style="width: 81px !important; text-align: right;" onchange="checkSundryAmount('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" value="<%=innerList.get(32) %>" onkeypress="return isNumberKey(event);" type="text">
														</td>
													</tr>
													<tr>
														<td class="alignRight" style="border: 0px none; display: table-cell;" id="tdReimbCurrency_travelDraft<%=trCount %>">Currency:<sup>*</sup></td>
														<td style="border: 0px none;">
															<select name="reimbCurrency_travel<%=trCount %>" id="reimbCurrency_travelDraft_<%=parentId %>_<%=trCount %>" style="width: 85px !important;" onchange="setSameCurrToAllReimbursement(this.id, 'travel', 'Draft', '<%=parentId %>');" >
																<%=innerList.get(39) %>
															</select>
														</td>
													</tr>
													<tr>
														<td class="alignRight" style="border: 0px none; display: table-cell;" id="tdAmt_travelDraft<%=trCount %>">Amount:<sup>*</sup></td>
														<td style="border: 0px none;">
															<input type="text" name="strAmount_travel<%=trCount %>" id="strAmount_travelDraft_<%=parentId %>_<%=trCount %>" value="<%=innerList.get(4) %>" onchange="setamount('T', '<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" class="validateRequired form-control" style="width: 81px !important; text-align: right;" onkeypress="return isNumberKey(event);" />
														</td>
													</tr>
													<tr>
														<td class="alignRight" style="border: 0px none; display: table-cell;" id="tdReimbPaymentMode_travelDraft<%=trCount %>">Payment Mode:<sup>*</sup></td>
														<td style="border: 0px none;">
															<select name="reimbPaymentMode_travel<%=trCount %>" id="reimbPaymentMode_travelDraft_<%=parentId %>_<%=trCount %>" style="width: 100px !important;">
																<%=innerList.get(40) %>
															</select>
														</td>
													</tr>
												</tbody>
											</table>
										</td>
										<td valign="top" nowrap="nowrap">
											<textarea rows="2" cols="25" name="strPurpose_travel<%=trCount %>" id="strPurpose_travelDraft_<%=parentId %>_<%=trCount %>"><%=innerList.get(3) %></textarea>
										</td>
										<td valign="top" nowrap="nowrap">
											<div id="div_existdoc_travelDraft_<%=parentId %>_<%=trCount %>">
												<% String[] strAttachedFiles = innerList.get(41).split(":_:");
													for(int j=0; j<strAttachedFiles.length; j++) {
														if(strAttachedFiles[j] != null && strAttachedFiles[j].length()>0) {
															String strFilePath = CF.getStrDocRetriveLocation()+IConstants.I_REIMBURSEMENTS+"/"+IConstants.I_DOCUMENT+"/"+strSessionEmpId+"/"+strAttachedFiles[j];
												%>
													<a href="<%=strFilePath %>" class="viewattach"></a> &nbsp;
												<% } } %>
											</div>
											
											<input name="fileCount_travel<%=trCount %>" id="fileCount_travelDraft_<%=parentId %>_<%=trCount %>" value="1" type="hidden">
											<input name="strDocument_travel<%=trCount %>" id="strDocument_travelDraft_<%=parentId %>_<%=trCount %>" type="file" 
												accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs">
											<a href="javascript:void(0)" onclick="addDoc('<%=trCount %>', 'travel', 'Draft', '<%=parentId %>');" class="add-font"></a>
											<div id="div_documents_travelDraft_<%=parentId %>_<%=trCount %>"></div>
										</td>
									</tr>
									<% } %>
								</tbody>
							</table>
						<% } %>
				
						<%  alBulkExpenseData = new ArrayList<List<String>>();
							alBulkExpenseData = hmBulkExpenseData.get("L");
							trCount = 0;
							if(alBulkExpenseData == null) alBulkExpenseData = new ArrayList<List<String>>();
							if(alBulkExpenseData.size() > 0) {
								localTrSize = alBulkExpenseData.size();
						%>
							<table id="localTableDraft_<%=parentId %>" style="display: block;" class="table pdzn_tbl1"> <!-- table-bordered -->
								<tbody>
									
									<% 
										for(int i=0; alBulkExpenseData.size()>0 && i<alBulkExpenseData.size(); i++) {
											trCount++;
											List<String> innerList = alBulkExpenseData.get(i);
									%>
									<tr id="1" class="bb">
										<th class="alignCenter" nowrap="nowrap">Add</th>
										<th class="alignCenter" id="othr1" nowrap="nowrap">Type<sup>*</sup></th>
										<th nowrap="nowrap" class="alignRight" valign="top"><span style="float: left;">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span></th>
										<th class="alignRight" valign="top" nowrap="nowrap">Number of Persons<sup>*</sup></th>
										<th class="alignRight" valign="top" nowrap="nowrap">Vendor</th>
										<th class="alignRight" valign="top" nowrap="nowrap">Receipt No.</th>
                                        <th nowrap="nowrap" class="alignRight" valign="top">Amount<sup>*</sup></th>
										<th class="alignRight" valign="top" nowrap="nowrap">Purpose</th>
										<th class="alignRight" valign="top" nowrap="nowrap">Attach Document</th>
									</tr>
									<tr id="localTRDraft_<%=parentId %>_<%=trCount %>" class="bb">
										<td valign="top" nowrap="nowrap">
											<input type="hidden" name="localReimbursementId<%=trCount %>" id="localReimbursementId_<%=parentId %>_<%=trCount %>" value="<%=innerList.get(36) %>" />
											<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to remove this record?'))deleteRow(this, 'localTableDraft', 'Draft', '<%=innerList.get(36) %>', '<%=parentId %>')" class="remove-font"></a>
											<a href="javascript:void(0)" onclick="addRow('L', 'localTableDraft', 'localTRDraft', 'local', 'Draft', '<%=parentId %>');" class="add-font"></a>
										</td>
										<td valign="top" nowrap="nowrap">
											<select name="strType_local<%=trCount %>" id="strType_localDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" onchange="othervalue(this.value, '<%=trCount %>', 'local', 'Draft', '<%=parentId %>');">
												<!-- <option value="">Select Type</option><option value="Conveyance Bill">Conveyance Bill</option><option value="Food Expenses">Food Expenses</option><option value="Supplies">Supplies</option><option value="Repair and Maintenance">Repair and Maintenance</option><option value="Accommodation">Accommodation</option><option value="Internet Charges">Internet Charges</option><option value="Relocation Expenses">Relocation Expenses</option><option value="Courier Charges">Courier Charges</option><option value="Laundry Charges">Laundry Charges</option><option value="Printing &amp; Stationery">Printing &amp; Stationery</option><option value="Others">Others</option> -->
												<%=innerList.get(2) %>
											</select>
										</td>
										<td valign="top" nowrap="nowrap">
											<input name="fromDate_local<%=trCount %>" id="fromDate_localDraft_<%=parentId %>_<%=trCount %>" class="validateRequired" type="text" value="<%=innerList.get(37) %>" style="width: 90px !important;"/>&nbsp;&nbsp;
											<input name="toDate_local<%=trCount %>" id="toDate_localDraft_<%=parentId %>_<%=trCount %>" class="validateRequired" type="text" value="<%=innerList.get(38) %>" style="width: 90px !important;"/>
										</td>
										
										<script type="text/javascript">
											$(function(){
												$("#fromDate_localDraft_<%=parentId %>_<%=trCount %>").datepicker({
												    format: 'dd/mm/yyyy',
												    autoclose: true
												}).on('changeDate', function (selected) {
												    var minDate = new Date(selected.date.valueOf());
												    $('#toDate_localDraft_<%=parentId %>_<%=trCount %>').datepicker('setStartDate', minDate);
												    $('#toDate_localDraft_<%=parentId %>_<%=trCount %>').datepicker('setDate', minDate);
												});
												
											    $("#toDate_localDraft_<%=parentId %>_<%=trCount %>").datepicker({
													format: 'dd/mm/yyyy',
													//startDate : new Date(strMinDate),
													autoclose: true
												}).on('changeDate', function (selected) {
											        var minDate = new Date(selected.date.valueOf());
											        $('#fromDate_localDraft_<%=parentId %>_<%=trCount %>').datepicker('setEndDate', minDate);
												});
											});
										</script>
										
										<td valign="top" nowrap="nowrap">
											<input name="noofperson_local<%=trCount %>" id="noofperson_localDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" type="text" value="<%=innerList.get(9) %>" style="width: 140px !important;" onkeypress="return isNumberKey(event);" />
										</td>
										<td valign="top" nowrap="nowrap">
											<input name="strVendor_local<%=trCount %>" id="strVendor_localDraft_<%=parentId %>_<%=trCount %>" type="text" value="<%=innerList.get(19) %>">
										</td>
										<td valign="top" nowrap="nowrap">
											<div id="divReceiptNo_localDraft_<%=parentId %>_<%=trCount %>">
											<% String[] strReceiptNo = innerList.get(20).split(":_:");
												int intCnt = 0;
												for(int j=0; j<strReceiptNo.length; j++) {
													if(strReceiptNo[j] != null && strReceiptNo[j].length()>0) {
														intCnt++;
											%>
												<div id="row_receipt_localDraft_<%=parentId %>_<%=trCount %><%=intCnt %>">
													<input name="strReceiptNo_local<%=trCount %>" id="strReceiptNo_localDraft_<%=parentId %>_<%=trCount %>" type="text" value="<%=strReceiptNo[j] %>" style="width: 140px !important;" />
													<a href="javascript:void(0)" onclick="addReceiptNo('<%=trCount %>', 'local', 'Draft', '<%=parentId %>')" class="add-font"></a>
													<% if(intCnt > 1) { %>
														<a href="javascript:void(0)" onclick="removeReceiptNo('<%=intCnt %>', '<%=trCount %>', 'local', 'Draft', '<%=parentId %>')" id="removeReceiptId_localDraft_<%=parentId %>_<%=intCnt %>" class="remove-font"></a>
													<% } %>
												</div>
											<% } } %>
											</div>
											<input name="receiptNoCount_local<%=trCount %>" id="receiptNoCount_localDraft_<%=parentId %>_<%=trCount %>" value="<%=intCnt %>" type="hidden">
										</td>
										
										<td valign="top" nowrap="nowrap">
										<% 	String innerTblStatus = ((innerList.get(35) != null && innerList.get(35).equals("Conveyance Bill")) ? "table-row" : "none");
											String innerTblTDStatus = ((innerList.get(35) != null && innerList.get(35).equals("Conveyance Bill")) ? "table-cell" : "none");
										%>
											<table class="table" id="innerTableDraft_<%=parentId %>_<%=trCount %>" cellspacing="0" cellpadding="0">
												<tbody>
													<tr id="trModeTravel_localDraft_<%=parentId %>_<%=trCount %>" style="display: <%=innerTblStatus %>;">
														<td class="alignRight" style="border: 0px none;">Mode of Travel:<sup>*</sup></td>
														<td style="border: 0px none;">
														<select name="modeoftravel_local<%=trCount %>" id="modeoftravel_localDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" onchange="clearAmt('<%=trCount %>', 'local', 'Draft', '<%=parentId %>');">
														<!-- <option value="">Select Mode</option><option value="Owned vehical- 2 Wheeler">Owned vehical- 2 Wheeler</option><option value="Owned vehical- 4 Wheeler">Owned vehical- 4 Wheeler</option><option value="Bus">Bus</option><option value="Train">Train</option><option value="Taxi">Taxi</option><option value="Auto">Auto</option><option value="Two Wheeler">Two Wheeler</option><option value="Other">Other</option> -->	
														<%=innerList.get(8) %>
														</select>
														</td>
													</tr>
													<tr id="trPlaceFrom_localDraft_<%=parentId %>_<%=trCount %>" style="display: <%=innerTblStatus %>;">
														<td class="alignRight" style="border: 0px none;">Place From:<sup>*</sup></td>
														<td style="border: 0px none;">
															<input name="placefrom_local<%=trCount %>" id="placefrom_localDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" type="text" value="<%=innerList.get(10) %>">
														</td>
													</tr>
													<tr id="trPlaceTo_localDraft_<%=parentId %>_<%=trCount %>" style="display: <%=innerTblStatus %>;">
														<td class="alignRight" style="border: 0px none;">Place To:<sup>*</sup></td>
														<td style="border: 0px none;">
															<input name="placeto_local<%=trCount %>" id="placeto_localDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" type="text" value="<%=innerList.get(11) %>">
														</td>
													</tr>
													<tr id="trNodays_localDraft_<%=parentId %>_<%=trCount %>" style="display: <%=innerTblStatus %>;">
														<td class="alignRight" style="border: 0px none;">No of Days:<sup>*</sup></td>
														<td style="border: 0px none;">
															<input name="noofdays_local<%=trCount %>" id="noofdays_localDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" value="<%=innerList.get(12) %>" onkeyup="setamount('L', '<%=trCount %>', 'local', 'Draft', '<%=parentId %>');" onkeypress="return isNumberKey(event)" type="text">
														</td>
													</tr>
													<tr id="trTotalKM_localDraft_<%=parentId %>_<%=trCount %>" style="display: <%=innerTblStatus %>;">
														<td class="alignRight" style="border: 0px none;">Total KM:</td>
														<td style="border: 0px none;">
															<input name="kmpd_local<%=trCount %>" id="kmpd_localDraft_<%=parentId %>_<%=trCount %>" style="width:70px !important;" value="<%=innerList.get(13) %>" onkeyup="setamount('L', '<%=trCount %>', 'local', 'Draft', '<%=parentId %>');" onkeypress="return isNumberKey(event)" type="text">
														</td>
													</tr>
													<tr id="trRateKM_localDraft_<%=parentId %>_<%=trCount %>" style="display: <%=innerTblStatus %>;">
														<td class="alignRight" style="border: 0px none;">Rate/KM:</td>
														<td style="border: 0px none;">
															<input name="ratepkm_local<%=trCount %>" id="ratepkm_localDraft_<%=parentId %>_<%=trCount %>" style="width:63px !important;" value="<%=innerList.get(14) %>" onkeyup="setamount('L', '<%=trCount %>', 'local', 'Draft', '<%=parentId %>');" onkeypress="return isNumberKey(event)" type="text">
														</td>
													</tr>
													<tr>
														<td class="alignRight" style="border: 0px none; display: table-cell;" id="tdReimbCurrency_localDraft<%=trCount %>">Currency:<sup>*</sup></td>
														<td style="border: 0px none;">
															<select name="reimbCurrency_local<%=trCount %>" id="reimbCurrency_localDraft_<%=parentId %>_<%=trCount %>" style="width: 85px !important;" onchange="setSameCurrToAllReimbursement(this.id, 'local', 'Draft', '<%=parentId %>');">
																<%=innerList.get(39) %>
															</select>
														</td>
													</tr>
													<tr>
														<td class="alignRight" style="border: 0px none;" id="tdAmt_localDraft<%=trCount %>">Amount:<sup>*</sup></td> <%-- display: <%=innerTblTDStatus %>; --%>
														<td style="border: 0px none;">
															<input type="text" name="strAmount_local<%=trCount %>" id="strAmount_localDraft_<%=parentId %>_<%=trCount %>" value="<%=innerList.get(4) %>" onchange="setamount('L', '<%=trCount %>', 'local', 'Draft', '<%=parentId %>');" class="validateRequired form-control" style="width: 81px !important; text-align: right;" onkeypress="return isNumberKey(event);" />
														</td>
													</tr>
													<tr>
														<td class="alignRight" style="border: 0px none; display: table-cell;" id="tdReimbPaymentMode_localDraft<%=trCount %>">Payment Mode:<sup>*</sup></td>
														<td style="border: 0px none;">
															<select name="reimbPaymentMode_local<%=trCount %>" id="reimbPaymentMode_localDraft_<%=parentId %>_<%=trCount %>" style="width: 100px !important;">
																<%=innerList.get(40) %>
															</select>
														</td>
													</tr>
												</tbody>
											</table>
										</td>
										<td valign="top" nowrap="nowrap">
											<textarea rows="2" cols="25" name="strPurpose_local<%=trCount %>" id="strPurpose_localDraft_<%=parentId %>_<%=trCount %>"><%=innerList.get(3) %></textarea>
										</td>
										<td valign="top" nowrap="nowrap">
											<div id="div_existdoc_localDraft_<%=parentId %>_<%=trCount %>">
												<% String[] strAttachedFiles = innerList.get(41).split(":_:");
													for(int j=0; j<strAttachedFiles.length; j++) {
														if(strAttachedFiles[j] != null && strAttachedFiles[j].length()>0) {
															String strFilePath = CF.getStrDocRetriveLocation()+IConstants.I_REIMBURSEMENTS+"/"+IConstants.I_DOCUMENT+"/"+strSessionEmpId+"/"+strAttachedFiles[j];
												%>
													<a href="<%=strFilePath %>" class="viewattach"></a> &nbsp;
												<% } } %>
											</div>
											
											<input name="fileCount_local<%=trCount %>" id="fileCount_localDraft_<%=parentId %>_<%=trCount %>" value="1" type="hidden">
											<input type="file" name="strDocument_local<%=trCount %>" id="strDocument_localDraft_<%=parentId %>_<%=trCount %>" accept=".gif,.jpg,.png,.tif,.svg,.svgz,.xls,.pdf,.ppt,.doc,.docs">
											<a href="javascript:void(0)" onclick="addDoc('<%=trCount %>', 'local', 'Draft', '<%=parentId %>');" class="add-font"></a>
											<div id="div_documents_localDraft_<%=parentId %>_<%=trCount %>"></div>
											
										</td>
									</tr>
									<% } %>
								</tbody>
							</table>
						<% } %>
				
						
						<% alBulkExpenseData = new ArrayList<List<String>>();
							alBulkExpenseData = hmBulkExpenseData.get("M");
							trCount = 0;
							if(alBulkExpenseData == null) alBulkExpenseData = new ArrayList<List<String>>();
							if(alBulkExpenseData.size() > 0) {
								mobileTrSize = alBulkExpenseData.size();
						%>
							<table id="mobileBillTableDraft_<%=parentId %>" style="display: block;" class="table pdzn_tbl1">
								<tbody>
									
									<% 
										for(int i=0; alBulkExpenseData.size()>0 && i<alBulkExpenseData.size(); i++) {
											trCount++;
											List<String> innerList = alBulkExpenseData.get(i);
									%>
									<tr id="1" class="bb">
										<th class="alignCenter" nowrap="nowrap">Add</th>
										<th nowrap="nowrap" class="alignRight" valign="top"><span style="float: left;">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span></th>
										<th class="alignRight" valign="top" nowrap="nowrap">Number of Persons<sup>*</sup></th>
										<th class="alignRight" valign="top" nowrap="nowrap">Vendor</th>
										<th class="alignRight" valign="top" nowrap="nowrap">Receipt No.</th>
										<th nowrap="nowrap" class="alignRight" valign="top">Currency</th>
                                        <th nowrap="nowrap" class="alignRight" valign="top">Amount<sup>*</sup></th>
                                        <th nowrap="nowrap" class="alignRight" valign="top">Payment Mode</th>
										<th class="alignRight" valign="top" nowrap="nowrap">Purpose</th>
										<th class="alignRight" valign="top" nowrap="nowrap">Attach Document</th>
									</tr>
									<tr id="mobileBillTRDraft_<%=parentId %>_<%=trCount %>" class="bb">
										<td valign="top" nowrap="nowrap">
											<input type="hidden" name="mobileReimbursementId<%=trCount %>" id="mobileReimbursementId_<%=parentId %>_<%=trCount %>" value="<%=innerList.get(36) %>" />
											<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to remove this record?'))deleteRow(this, 'mobileBillTableDraft', 'Draft', '<%=innerList.get(36) %>', '<%=parentId %>')" class="remove-font"></a>
											<a href="javascript:void(0)" onclick="addRow('M', 'mobileBillTableDraft', 'mobileBillTRDraft', 'mobile', 'Draft', '<%=parentId %>');" class="add-font"></a>
										</td>
										<td valign="top" nowrap="nowrap">
											<input name="fromDate_mobile<%=trCount %>" id="fromDate_mobileDraft_<%=parentId %>_<%=trCount %>" class="validateRequired" type="text" value="<%=innerList.get(37) %>" style="width: 90px !important;"/>&nbsp;&nbsp;
											<input name="toDate_mobile<%=trCount %>" id="toDate_mobileDraft_<%=parentId %>_<%=trCount %>" class="validateRequired" type="text" value="<%=innerList.get(38) %>" style="width: 90px !important;"/>
										</td>
										<script type="text/javascript">
											$(function(){
												$("#fromDate_mobileDraft_<%=parentId %>_<%=trCount %>").datepicker({
												    format: 'dd/mm/yyyy',
												    autoclose: true
												}).on('changeDate', function (selected) {
												    var minDate = new Date(selected.date.valueOf());
												    $('#toDate_mobileDraft_<%=parentId %>_<%=trCount %>').datepicker('setStartDate', minDate);
												    $('#toDate_mobileDraft_<%=parentId %>_<%=trCount %>').datepicker('setDate', minDate);
												});
												
											    $("#toDate_mobileDraft_<%=parentId %>_<%=trCount %>").datepicker({
													format: 'dd/mm/yyyy',
													//startDate : new Date(strMinDate),
													autoclose: true
												}).on('changeDate', function (selected) {
											        var minDate = new Date(selected.date.valueOf());
											        $('#fromDate_mobileDraft_<%=parentId %>_<%=trCount %>').datepicker('setEndDate', minDate);
												});
											});
										</script>
										<td valign="top" nowrap="nowrap">
											<input name="noofperson_mobile<%=trCount %>" id="noofperson_mobileDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" type="text" value="<%=innerList.get(9) %>" style="width: 140px !important;" onkeypress="return isNumberKey(event);" />
										</td>
										<td valign="top" nowrap="nowrap">
											<input name="strVendor_mobile<%=trCount %>" id="strVendor_mobileDraft_<%=parentId %>_<%=trCount %>" type="text" value="<%=innerList.get(19) %>">
										</td>
										<td valign="top" nowrap="nowrap">
										<div id="divReceiptNo_mobileDraft_<%=parentId %>_<%=trCount %>">
											<% String[] strReceiptNo = innerList.get(20).split(":_:");
												int intCnt = 0;
												for(int j=0; j<strReceiptNo.length; j++) {
													if(strReceiptNo[j] != null && strReceiptNo[j].length()>0) {
														intCnt++;
											%>
												<div id="row_receipt_mobileDraft<%=trCount %><%=intCnt %>">
													<input name="strReceiptNo_mobile<%=trCount %>" id="strReceiptNo_mobileDraft_<%=parentId %>_<%=trCount %>" type="text" value="<%=strReceiptNo[j] %>" style="width: 140px !important;" />
													<a href="javascript:void(0)" onclick="addReceiptNo('<%=trCount %>', 'mobile', 'Draft', '<%=parentId %>')" class="add-font"></a>
													<% if(intCnt > 1) { %>
														<a href="javascript:void(0)" onclick="removeReceiptNo('<%=intCnt %>', '<%=trCount %>', 'mobile', 'Draft', '<%=parentId %>')" id="removeReceiptId_mobileDraft<%=intCnt %>" class="remove-font"></a>
													<% } %>
												</div>
											<% } } %>
											</div>
											<input name="receiptNoCount_mobile<%=trCount %>" id="receiptNoCount_mobileDraft_<%=parentId %>_<%=trCount %>" value="<%=intCnt %>" type="hidden">
										</td>
										<td valign="top" nowrap="nowrap">
											<select name="reimbCurrency_mobile<%=trCount %>" id="reimbCurrency_mobileDraft_<%=parentId %>_<%=trCount %>" style="width: 85px !important;" onchange="setSameCurrToAllReimbursement(this.id, 'mobile', 'Draft', '<%=parentId %>');">
												<%=innerList.get(39) %>
											</select>
										</td>
										<td valign="top" nowrap="nowrap">
											<input type="text" name="strAmount_mobile<%=trCount %>" id="strAmount_mobileDraft_<%=parentId %>_<%=trCount %>" onchange="setamount('M', <%=trCount %>, 'mobile', 'Draft');" class="validateRequired form-control" style="width: 81px !important; text-align: right;" value="<%=innerList.get(4) %>" onkeypress="return isNumberKey(event);" />
										</td>
										<td valign="top" nowrap="nowrap">
											<select name="reimbPaymentMode_mobile<%=trCount %>" id="reimbPaymentMode_mobileDraft_<%=parentId %>_<%=trCount %>" style="width: 100px !important;">
												<%=innerList.get(40) %>
											</select>
										</td>
										<td valign="top" nowrap="nowrap">
											<textarea rows="2" cols="25" name="strPurpose_mobile<%=trCount %>" id="strPurpose_mobileDraft_<%=parentId %>_<%=trCount %>"><%=innerList.get(3) %></textarea>
										</td>
										<td valign="top" nowrap="nowrap">
											<div id="div_existdoc_mobileDraft_<%=parentId %>_<%=trCount %>">
												<% String[] strAttachedFiles = innerList.get(41).split(":_:");
													for(int j=0; j<strAttachedFiles.length; j++) {
														if(strAttachedFiles[j] != null && strAttachedFiles[j].length()>0) {
															String strFilePath = CF.getStrDocRetriveLocation()+IConstants.I_REIMBURSEMENTS+"/"+IConstants.I_DOCUMENT+"/"+strSessionEmpId+"/"+strAttachedFiles[j];
												%>
													<a href="<%=strFilePath %>" class="viewattach"></a> &nbsp;
												<% } } %>
											</div>
											
											<input name="fileCount_mobile<%=trCount %>" id="fileCount_mobileDraft_<%=parentId %>_<%=trCount %>" value="1" type="hidden">
											<input name="strDocument_mobile<%=trCount %>" id="strDocument_mobileDraft_<%=parentId %>_<%=trCount %>"  type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs">
											<a href="javascript:void(0)" onclick="addDoc('<%=trCount %>', 'mobile', 'Draft', '<%=parentId %>');" class="add-font"></a>
											<div id="div_documents_mobileDraft_<%=parentId %>_<%=trCount %>"></div>
										</td>
									</tr>
									<% } %>
								</tbody>
							</table>
						<% } %>
					
						
					<% alBulkExpenseData = new ArrayList<List<String>>();
						alBulkExpenseData = hmBulkExpenseData.get("P");
						trCount = 0;
						if(alBulkExpenseData == null) alBulkExpenseData = new ArrayList<List<String>>();
						if(alBulkExpenseData.size() > 0) {
							projectTrSize = alBulkExpenseData.size();
					%>
						<table id="projectTableDraft_<%=parentId %>" style="display: block;" class="table pdzn_tbl1">
							<tbody>
								
								<% 
									for(int i=0; alBulkExpenseData.size()>0 && i<alBulkExpenseData.size(); i++) {
										trCount++;
										List<String> innerList = alBulkExpenseData.get(i);
								%>
								<tr id="1" class="bb">
									<th class="alignCenter" nowrap="nowrap">Add</th>
									<th class="alignCenter" id="client1" nowrap="nowrap">Client<sup>*</sup></th>
									<th class="alignCenter" id="pro1" nowrap="nowrap">Project<sup>*</sup></th>
									<th nowrap="nowrap" class="alignRight" valign="top"><span style="float: left;">From Date<sup>*</sup></span> <span>To Date<sup>*</sup></span></th>
									<th class="alignRight" valign="top" nowrap="nowrap">Number of Persons<sup>*</sup></th>
									<th class="alignRight" valign="top" nowrap="nowrap">Vendor</th>
									<th class="alignRight" valign="top" nowrap="nowrap">Receipt No.</th>
									<th nowrap="nowrap" class="alignRight" valign="top">Currency</th>
                                    <th nowrap="nowrap" class="alignRight" valign="top">Amount<sup>*</sup></th>
                                    <th nowrap="nowrap" class="alignRight" valign="top">Payment Mode</th>
									<th class="alignRight" valign="top" nowrap="nowrap">Purpose</th>
									<th id="isbillfirst1" class="alignRight" valign="top" nowrap="nowrap">Chargeable to client</th>
									<th class="alignRight" valign="top" nowrap="nowrap">Attach Document</th>
								</tr>
								
								<tr id="projectTRDraft_<%=parentId %>_<%=trCount %>" class="bb">
									<td valign="top" nowrap="nowrap">
										<input type="hidden" name="projectReimbursementId<%=trCount %>" id="projectReimbursementId_<%=parentId %>_<%=trCount %>" value="<%=innerList.get(36) %>" />
										<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to remove this record?'))deleteRow(this, 'projectTableDraft', 'Draft', '<%=innerList.get(36) %>', '<%=parentId %>')" class="remove-font"></a>
										<a href="javascript:void(0)" onclick="addRow('P', 'projectTableDraft', 'projectTRDraft', 'project', 'Draft', '<%=parentId %>');" class="add-font"></a>
									</td>
									<td valign="top" nowrap="nowrap">
										<select name="strClient_project<%=trCount %>" id="strClient_projectDraft_<%=parentId %>_<%=trCount %>" onchange="getContent('typeP_projectDraft_<%=parentId %>_<%=trCount %>', 'GetProjectClientTask.action?client_id='+this.value+'&type=BULKEXP&cnt='+<%=trCount %>+'&parentId='+<%=parentId %>+'&saveType=Draft');" class="validateRequired form-control">
											<%=innerList.get(17) %>
										</select>
									</td>
									<td valign="top" nowrap="nowrap" id="typeP_projectDraft_<%=parentId %>_<%=trCount %>">
										<select name="strProject_project<%=trCount %>" id="strProject_projectDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control">
											<%=innerList.get(18) %>
										</select>
									</td>
									<td valign="top" nowrap="nowrap">
										<input name="fromDate_project<%=trCount %>" id="fromDate_projectDraft_<%=parentId %>_<%=trCount %>" class="validateRequired" type="text" value="<%=innerList.get(37) %>" style="width: 90px !important;"/>&nbsp;&nbsp;
										<input name="toDate_project<%=trCount %>" id="toDate_projectDraft_<%=parentId %>_<%=trCount %>" class="validateRequired" type="text" value="<%=innerList.get(38) %>" style="width: 90px !important;"/>
									</td>
									<script type="text/javascript">
										$(function(){
											$("#fromDate_projectDraft_<%=parentId %>_<%=trCount %>").datepicker({
											    format: 'dd/mm/yyyy',
											    autoclose: true
											}).on('changeDate', function (selected) {
											    var minDate = new Date(selected.date.valueOf());
											    $('#toDate_projectDraft_<%=parentId %>_<%=trCount %>').datepicker('setStartDate', minDate);
											    $('#toDate_projectDraft_<%=parentId %>_<%=trCount %>').datepicker('setDate', minDate);
											});
											
										    $("#toDate_projectDraft_<%=parentId %>_<%=trCount %>").datepicker({
												format: 'dd/mm/yyyy',
												//startDate : new Date(strMinDate),
												autoclose: true
											}).on('changeDate', function (selected) {
										        var minDate = new Date(selected.date.valueOf());
										        $('#fromDate_projectDraft_<%=parentId %>_<%=trCount %>').datepicker('setEndDate', minDate);
											});
										});
									</script>
									<td valign="top" nowrap="nowrap">
										<input name="noofperson_project<%=trCount %>" id="noofperson_projectDraft_<%=parentId %>_<%=trCount %>" class="validateRequired form-control" type="text" value="<%=innerList.get(9) %>" style="width: 140px !important;" onkeypress="return isNumberKey(event);" />
									</td>
									<td valign="top" nowrap="nowrap">
										<input name="strVendor_project<%=trCount %>" id="strVendor_projectDraft_<%=parentId %>_<%=trCount %>" type="text" value="<%=innerList.get(19) %>">
									</td>
									<td valign="top" nowrap="nowrap">
									<div id="divReceiptNo_projectDraft_<%=parentId %>_<%=trCount %>">
										<% String[] strReceiptNo = innerList.get(20).split(":_:");
											int intCnt = 0;
											for(int j=0; j<strReceiptNo.length; j++) {
												if(strReceiptNo[j] != null && strReceiptNo[j].length()>0) {
													intCnt++;
										%>
											<div id="row_receipt_projectDraft_<%=parentId %>_<%=trCount %><%=intCnt %>">
												<input name="strReceiptNo_project<%=trCount %>" id="strReceiptNo_projectDraft_<%=parentId %>_<%=trCount %>" type="text" value="<%=strReceiptNo[j] %>" style="width: 140px !important;" />
												<a href="javascript:void(0)" onclick="addReceiptNo('<%=trCount %>', 'project', 'Draft', '<%=parentId %>')" class="add-font"></a>
												<% if(intCnt > 1) { %>
													<a href="javascript:void(0)" onclick="removeReceiptNo('<%=intCnt %>', '<%=trCount %>', 'project', 'Draft', '<%=parentId %>')" id="removeReceiptId_projectDraft<%=intCnt %>" class="remove-font"></a>
												<% } %>
											</div>
										<% } } %>
										</div>
										<input name="receiptNoCount_project<%=trCount %>" id="receiptNoCount_projectDraft_<%=parentId %>_<%=trCount %>" value="<%=intCnt %>" type="hidden">
									</td>
									<td valign="top" nowrap="nowrap">
										<select name="reimbCurrency_project<%=trCount %>" id="reimbCurrency_projectDraft_<%=parentId %>_<%=trCount %>" style="width: 85px !important;" onchange="setSameCurrToAllReimbursement(this.id, 'project', 'Draft', '<%=parentId %>');">
											<%=innerList.get(39) %>
										</select>
									</td>
									<td valign="top" nowrap="nowrap">
										<input type="text" name="strAmount_project<%=trCount %>" id="strAmount_projectDraft_<%=parentId %>_<%=trCount %>" onchange="setamount('P', '<%=trCount %>', 'project', 'Draft', '<%=parentId %>');" class="validateRequired form-control" style="width: 81px !important; text-align: right;" value="<%=innerList.get(4) %>" onkeypress="return isNumberKey(event);" />
									</td>
									<td valign="top" nowrap="nowrap">
										<select name="reimbPaymentMode_project<%=trCount %>" id="reimbPaymentMode_projectDraft_<%=parentId %>_<%=trCount %>" style="width: 100px !important;">
											<%=innerList.get(40) %>
										</select>
									</td>
									<td valign="top" nowrap="nowrap">
										<textarea rows="2" cols="25" name="strPurpose_project<%=trCount %>" id="strPurpose_projectDraft_<%=parentId %>_<%=trCount %>"><%=innerList.get(3) %></textarea>
									</td>
									<td valign="top" nowrap="nowrap">
										<input type='checkbox' name='isbillable_project<%=trCount %>' id='isbillable_projectDraft_<%=parentId %>_<%=trCount %>' value='true' <%=uF.parseToBoolean(innerList.get(16)) ? "checked" : "" %>/>
									</td>
									<td valign="top" nowrap="nowrap">
										<div id="div_existdoc_projectDraft_<%=parentId %>_<%=trCount %>">
											<% String[] strAttachedFiles = innerList.get(41).split(":_:");
												for(int j=0; j<strAttachedFiles.length; j++) {
													if(strAttachedFiles[j] != null && strAttachedFiles[j].length()>0) {
														String strFilePath = CF.getStrDocRetriveLocation()+IConstants.I_REIMBURSEMENTS+"/"+IConstants.I_DOCUMENT+"/"+strSessionEmpId+"/"+strAttachedFiles[j];
											%>
												<a href="<%=strFilePath %>" class="viewattach"></a> &nbsp;
											<% } } %>
										</div>
										
										<input name="fileCount_project<%=trCount %>" id="fileCount_projectDraft_<%=parentId %>_<%=trCount %>" value="1" type="hidden">
										<input name="strDocument_project<%=trCount %>" id="strDocument_projectDraft_<%=parentId %>_<%=trCount %>" type="file"  accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs">
										<a href="javascript:void(0)" onclick="addDoc('<%=trCount %>', 'project', 'Draft', '<%=parentId %>');" class="add-font"></a>
										<div id="div_documents_projectDraft_<%=parentId %>_<%=trCount %>"></div>
									</td>
								</tr>
								<% } %>
							</tbody>
						</table>
					<% } %>
					<% 
						String msgDivStatus = "none";	
						if(hmBulkExpenseData == null || hmBulkExpenseData.size()==0) { 
							msgDivStatus = "block";
						}
					%>
					<div id="noExpenseMsgDivDraft" style="display: <%=msgDivStatus %>" class="filter"><div class="msg nodata"><span>No Drafts.</span></div></div>
					</div>
				</div>
				<% if(hmBulkExpenseData != null && hmBulkExpenseData.size()>0) { %>
					<div id="workflowDivDraft_<%=parentId %>">
						<input type="hidden" name="travelCount" id="travelCountDraft_<%=parentId %>" value="<%=travelTrSize %>">
						<input type="hidden" name="localCount" id="localCountDraft_<%=parentId %>" value="<%=localTrSize %>">
						<input type="hidden" name="mobileCount" id="mobileCountDraft_<%=parentId %>" value="<%=mobileTrSize %>">
						<input type="hidden" name="projectCount" id="projectCountDraft_<%=parentId %>" value="<%=projectTrSize %>">
						<table class="table table_no_border">
							<%
								if(uF.parseToBoolean(CF.getIsWorkFlow())) {
		 							if(hmMemberOption != null && !hmMemberOption.isEmpty() ) {
										Iterator<String> it1 = hmMemberOption.keySet().iterator();
										while(it1.hasNext()) {
											String memPosition=it1.next();
											String optiontr=hmMemberOption.get(memPosition);					
											out.println(optiontr); 
										}
							%>
								<tr>
	    							<td>&nbsp;</td>
	    							<td>
	        							<input type="submit" id="saveButton" name="btnSave" class="btn btn-primary" value="Save" style="float:left;">
										<input type="submit" id="submitButton" name="btnSubmit" value="Submit" class="btn btn-primary">
	    							</td>
								</tr>
							<% } else { %>
								<tr>
	    							<td>Your work flow is not defined. Please, speak to your hr for your workflow.</td>
								</tr>
							<% } %>
							<% } else { %>
								<tr>
	    							<td>&nbsp;</td>
	    							<td>
	        							<input type="submit" id="saveButton" name="btnSave" class="btn btn-primary" value="Save" style="float:left;">
										<input type="submit" id="submitButton" name="btnSubmit" value="Submit" class="btn btn-primary">
	    							</td>
								</tr>
							<% } %>
						</table>
					</div>
				<% } %>
         	</s:form>
         </div>
              
         <% } %>     
                
		</section>
            
	</div>
	</div>
	</section>
	</div>
</section>
      
