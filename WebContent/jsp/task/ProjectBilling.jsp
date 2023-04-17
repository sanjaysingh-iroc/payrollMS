<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.*"%>
<%	String btnSubmit = (String)request.getAttribute("btnSubmit");
	if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) {
%>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script>
<% } %>
<%--  <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script> --%> 
<%-- <link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script> --%>
<style>
 
.anaAttrib1 {
	font-size: 12px;
	font-family: digital;
	color: #3F82BF;
	font-weight: bold;
	text-align: center;
	height: 22px;
}
</style>

<div id="divResult">

<script> 
$(function() {
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});

	$("#client").multiselect().multiselectfilter();
	$("#f_start").datepicker({format: 'dd/mm/yyyy'});
	$("#f_end").datepicker({format: 'dd/mm/yyyy'});
});

	function receiveBill(proFreqId, proId, invoiceId, clientId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Bill Receipt Summary');
		var proType = document.getElementById("proType").value; 
		$.ajax({
			url : 'ReceiveBill.action?invoiceId='+invoiceId+'&proId='+proId+'&clientId='+clientId+'&proFreqId='+proFreqId+'&proType='+proType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	

function viewProjectInvoice(pro_freq_id, pro_id, pro_amount, invoice_amount, invoice_format_id, proOPEAmt) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Project Invoice');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : 'ProjectInvoice.action?pro_id='+pro_id+'&pro_amount='+pro_amount+'&invoice_amount='+invoice_amount+'&invoice_format_id='+invoice_format_id+'&pro_freq_id='+pro_freq_id+'&proOPEAmt='+proOPEAmt,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 


function viewProjectAdHocInvoice() {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Project AdHoc Invoice');
	if($(window).width() >= 1100){
		$(".modal-dialog").width(1100);
	}
	var proType = document.getElementById("proType").value;
	//alert("proType ===>> " + proType);
	$.ajax({
		url : 'ProjectAdHocInvoiceFormat_1.action?proType='+proType,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editProjectAdHocInvoice(invoiceId, billingType) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Update AdHoc Invoice');
	if($(window).width() >= 1100){
		$(".modal-dialog").width(1100);
	}
	var proType = document.getElementById("proType").value;
	$.ajax({
		url : 'ProjectAdHocInvoice.action?operation=E&invoiceId='+invoiceId+'&billingType='+billingType+'&proType='+proType,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function viewProjectProRetaInvoice(pro_freq_id,pro_id,bill_type,invoice_format_id,proOPEAmt) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Project Pro-rata Invoice');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : 'ProjectProRetaInvoice.action?pro_id='+pro_id+'&bill_type='+bill_type+'&invoice_format_id='+invoice_format_id+'&pro_freq_id='+pro_freq_id+'&proOPEAmt='+proOPEAmt,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 


	function viewProjectPartialInvoicePopup(pro_freq_id,pro_id,pro_amount,invoice_amount,invoice_format_id,proOPEAmt) { 
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Partial Invoice');
		$.ajax({
			url : 'ProjectPartialInvoicePopup.action?pro_id='+pro_id+'&pro_amount='+pro_amount+'&invoice_amount='+invoice_amount
				+'&invoice_format_id='+invoice_format_id+'&divid=addInvoiceSummary'+"&pro_freq_id="+pro_freq_id+'&proOPEAmt='+proOPEAmt,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	} 
	

function viewProjectPartialInvoice(pro_freq_id,bPercent,pro_id,pro_amount,invoice_amount,divid,invoice_format_id) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Project Partial Invoice');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : 'ProjectPartialInvoice.action?pro_id='+pro_id+'&pro_amount='+pro_amount+'&invoice_amount='+invoice_amount
			+'&balPercent='+bPercent+'&invoice_format_id='+invoice_format_id+"&pro_freq_id="+pro_freq_id,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
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
			if(projectID != '' && invoiceID != '' && clientID != '') {
				window.location = "ReceiveMultipleBill.action?invoice_id="+invoiceID+"&pro_id="+projectID+"&client_id="+clientID+"&pro_freq_id="+projectFreqID;
			}
		}
	}
   

	function checkMultipleBill() {
		var allReceiveBill = document.getElementById("allReceiveBill");		
		var receiveBill = document.getElementsByName('receiveBill');
		//alert("allReceiveBill.checked ==>> " + allReceiveBill.checked);
		var cnt = 0;
		if(allReceiveBill.checked == true) {
			 for(var i=0;i<receiveBill.length;i++) {
				 receiveBill[i].checked = true;
				 cnt++;
			 }
		} else {		
			 for(var i=0; i<receiveBill.length; i++) {
				 receiveBill[i].checked = false;
			 }
		}
		
		if(cnt > 0) {
			document.getElementById("selectReceiptsSpan").style.display = "block";
			document.getElementById("unselectReceiptsSpan").style.display = "none";
		} else {
			document.getElementById("selectReceiptsSpan").style.display = "none";
			document.getElementById("unselectReceiptsSpan").style.display = "block";
		}
	}


	function checkAllBillChecked() {
		var allReceiveBill = document.getElementById("allReceiveBill");		
		var receiveBill = document.getElementsByName('receiveBill');
		var cnt = 0;
		var chkCnt = 0;
		for(var i=0;i<receiveBill.length;i++) {
			cnt++;
			 if(receiveBill[i].checked) {
				 chkCnt++;
			 }
		 }
		if(cnt == chkCnt) {
			allReceiveBill.checked = true;
		} else {
			allReceiveBill.checked = false;
		}
		
		if(chkCnt > 0) {
			document.getElementById("selectReceiptsSpan").style.display = "block";
			document.getElementById("unselectReceiptsSpan").style.display = "none";
		} else {
			document.getElementById("selectReceiptsSpan").style.display = "none";
			document.getElementById("unselectReceiptsSpan").style.display = "block";
		}
	}
	
	
	function deleteGeneratedProjectInvoice(invoiceId, proId) {
		if(confirm('Are you sure, you want to delete this invoice?')) {
			getContent('deleteDivInvoice_'+invoiceId, 'DeleteGeneratedProjectInvoice.action?type=D&invoiceId='+invoiceId+'&proId='+proId);
			document.getElementById("deleteDivReceiveBill_"+invoiceId).innerHTML = "";
		}
	}
	
	function cancelGeneratedProjectInvoice(invoiceId, proId) {
		if(confirm('Are you sure, you want to cancel this invoice?')) {
			getContent('deleteDivInvoice_'+invoiceId, 'DeleteGeneratedProjectInvoice.action?type=C&invoiceId='+invoiceId+'&proId='+proId);
		}
	}
	
	
	function openFeedsForm(invoiceId, proId, proType) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Task Feeds');
		if($(window).width() >= 900){
			$(".modal-dialog").width(900);
		}
		$.ajax({
			url : 'FeedsPopup.action?pageFrom=Invoice&invoiceId='+invoiceId+'&proId='+proId+'&pageType='+proType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function getSelectedValue(selectId) {
		var choice = document.getElementById(selectId);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			if (choice.options[i].selected == true) {
				if (j == 0) {
					exportchoice = choice.options[i].value;
					j++;
				} else {
					exportchoice += "," + choice.options[i].value;
					j++;
				}
			}
		}
		return exportchoice;
	}
	
	
	function submitForm(type) {
		var proType = document.getElementById("proType").value;
		var f_org = document.getElementById("f_org").value;
		var f_strWLocation = "";
		var f_department = "";
		var billingType = "";
		var strBillingFreq = "";
		var reportType = "";
		var client = "";
		if(document.getElementById("f_strWLocation")) {
			f_strWLocation = document.getElementById("f_strWLocation").value;
		}
		if(document.getElementById("f_department")) {
			f_department = document.getElementById("f_department").value;
		}
		if(document.getElementById("billingType")) {
			billingType = document.getElementById("billingType").value;
		}
		if(document.getElementById("strBillingFreq")) {
			strBillingFreq = document.getElementById("strBillingFreq").value;
		}
		if(document.getElementById("reportType")) {
			reportType = document.getElementById("reportType").value;
		}
		if(document.getElementById("client")) {
			client = getSelectedValue("client");
		}
		var paramValues = "";
		if(type != "" && type == '2') {
			paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&billingType='+billingType+'&strBillingFreq='+strBillingFreq
			+'&reportType='+reportType+'&strClient='+client;
		}
        
    	var action = 'Billing.action?f_org='+f_org+paramValues+'&proType='+proType+'&btnSubmit=Submit';
    	//alert("action=>"+action);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#divResult").html(result);
            	$("#client").multiselect().multiselectfilter();
       		}
    	});
    }
	
	
	function loadMore(proPage, minLimit) {
		/* document.frmProjectBilling.proPage.value = proPage;
		document.frmProjectBilling.minLimit.value = minLimit;
		document.frmProjectBilling.submit(); */
		var proType = document.getElementById("proType").value;
		var f_org = document.getElementById("f_org").value;
		var f_strWLocation = "";
		var f_department = "";
		var billingType = "";
		var strBillingFreq = "";
		var reportType = "";
		var client = "";
		if(document.getElementById("f_strWLocation")) {
			f_strWLocation = document.getElementById("f_strWLocation").value;
		}
		if(document.getElementById("f_department")) {
			f_department = document.getElementById("f_department").value;
		}
		if(document.getElementById("billingType")) {
			billingType = document.getElementById("billingType").value;
		}
		if(document.getElementById("strBillingFreq")) {
			strBillingFreq = document.getElementById("strBillingFreq").value;
		}
		if(document.getElementById("reportType")) {
			reportType = document.getElementById("reportType").value;
		}
		/* if(proType == 'CPB' || proType == 'CAB') {
			reportType = "";
		} */
		if(document.getElementById("client")) {
			client = getSelectedValue("client");
		}
		var paramValues = "";
		/* if(type != "" && type == '2') { */
			paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&billingType='+billingType+'&strBillingFreq='+strBillingFreq
			+'&reportType='+reportType+'&strClient='+client+'&proType='+proType+'&proPage='+proPage+'&minLimit='+minLimit+'&btnSubmit=Submit';
		/* } */
        
    	var action = 'Billing.action?f_org='+f_org+paramValues;
    	//alert("action=>"+action);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#divResult").html(result);
            	$("#client").multiselect().multiselectfilter();
       		}
    	});
	}
	
	function getBillingForm(proType) {
		//var proType = document.getElementById("proType").value;
		var f_org = document.getElementById("f_org").value;
		var f_strWLocation = "";
		var f_department = "";
		var billingType = "";
		var strBillingFreq = "";
		var reportType = "";
		var client = "";
		if(document.getElementById("f_strWLocation")) {
			f_strWLocation = document.getElementById("f_strWLocation").value;
		}
		if(document.getElementById("f_department")) {
			f_department = document.getElementById("f_department").value;
		}
		if(document.getElementById("billingType")) {
			billingType = document.getElementById("billingType").value;
		}
		if(document.getElementById("strBillingFreq")) {
			strBillingFreq = document.getElementById("strBillingFreq").value;
		}
		if(document.getElementById("reportType")) {
			reportType = document.getElementById("reportType").value;
		}
		if(proType == 'CPB' || proType == 'CAB') {
			reportType = "";
		}
		if(document.getElementById("client")) {
			client = getSelectedValue("client");
		}
		//alert("f_org ===>> " + f_org);
		var paramValues = "";
		/* if(type != "" && type == '2') { */
			paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&billingType='+billingType+'&strBillingFreq='+strBillingFreq
			+'&reportType='+reportType+'&strClient='+client+'&proType='+proType+'&btnSubmit=Submit';
		/* } */
        
    	var action = 'Billing.action?f_org='+f_org+paramValues;
    	//alert("action=>"+action);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#divResult").html(result);
            	$("#client").multiselect().multiselectfilter();
       		}
    	});
    }
	
</script>

<style>

.m_div {
	float: left;
	width: 100%;
	border-bottom: solid 1px #ccc;
	margin: 10px 0px;
	height: 60px;
}
.i_div1 {
	float: left; width: 100%;
}
.i_div2 {
	float: left; width: 100%;
}

.ui-state-default, .ui-multiselect-menu{
	width: 175px;
	font-size: 12PX !important;
}
</style>
<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
 --%>
<script type="text/javascript">

</script>

<script type="text/javascript">
	
</script>

	<%
	String proType = (String)request.getAttribute("proType");
	
	List alReport = (List)request.getAttribute("alReport");
	Map hmBilling = (Map)request.getAttribute("hmBilling");
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	
	Map hmWlocation = (Map)request.getAttribute("hmWlocation");
	
	Map<String, List<Map<String,String>>> hmInvDetails = (Map<String, List<Map<String,String>>>)request.getAttribute("hmInvDetails");
	Map<String, String> hmProReceivedAmount = (Map<String, String>) request.getAttribute("hmProReceivedAmount");
	
	Map<String, String> hmInvoiceReceivedAmount = (Map<String, String>) request.getAttribute("hmInvoiceReceivedAmount");
	
	List alAdhocReport = (List)request.getAttribute("alAdhocReport");
	Map<String, List<Map<String,String>>> hmAdhocInvDetails =(Map<String, List<Map<String,String>>>)request.getAttribute("hmAdhocInvDetails");
	Map<String, Map<String, String>> hmProInvoiceAmt=(Map<String, Map<String, String>>)request.getAttribute("hmProInvoiceAmt");
	
	boolean isEmail = (Boolean)request.getAttribute("isEmail");
	String proCount = (String)request.getAttribute("proCount");
//	out.println("proCount=======>"+proCount);
	
	%>

        <!-- Main content -->
        <section class="content">
          <!-- title row -->
	        <div class="row"> 
				<div class="col-md-12">
					<div class="nav-tabs-custom">
						<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<span style="float: left; padding: 9px 16px 0px 15px; font-weight: 600;"> Pending-</span>
			                <ul class="nav nav-tabs" style="float: left;">
			                  <li class="<%=((proType != null && proType.equalsIgnoreCase("PPB")) ? "active" : "") %>"><a href="#bills" style="padding: 5px;" onclick="getBillingForm('PPB');" data-toggle="tab">Project Bills</a></li> <%-- (<%=(String)request.getAttribute("wProCnt") %>) --%> <!-- window.location.href='Billing.action?proType=PPB' -->
			                  <li class="<%=((proType != null && proType.equalsIgnoreCase("PAB")) ? "active" : "") %>"><a href="#bills" style="padding: 5px;" onclick="getBillingForm('PAB');" data-toggle="tab">AdHoc Bills</a></li>
			                </ul>
			            <% } %>
			                <span style="float: left; padding: 9px 16px 0px 35px; font-weight: 600;"> Cleared-</span>
			                <ul class="nav nav-tabs" style="float: left;">
			                  <li class="<%=((proType != null && proType.equalsIgnoreCase("CPB")) ? "active" : "") %>"><a href="#bills" style="padding: 5px;" onclick="getBillingForm('CPB');" data-toggle="tab">Project Bills</a></li>
			                  <li class="<%=((proType != null && proType.equalsIgnoreCase("CAB")) ? "active" : "") %>"><a href="#bills" style="padding: 5px;" onclick="getBillingForm('CAB');" data-toggle="tab">AdHoc Bills</a></li>
			                </ul>
			                
			                <span style="float: right; margin-right: 30px; font-size: 16px;">
								<span class="anaAttrib1" style="font-size: 22px;"><%=uF.parseToInt((String) request.getAttribute("proCnt")) %> </span> Bills <!-- font-weight: bolder; font-family: digital; color: green;  -->
							</span>
			             
			             
		                <div class="tab-content box-body">
			                <div class="active tab-pane" id="#bills">
			                	<div class="box-body" style="float: left; width: 100%;"> 
								
									<div class="box box-warning direct-chat direct-chat-warning collapsed-box" style="margin-bottom: 0px;">
					                    <div class="box-header with-border" style="padding: 5px;">
					                      <h4 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h4>
					                      <div class="box-tools pull-right">
					                        <button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-plus"></i></button>
					                      </div>
					                    </div><!-- /.box-header -->
					                    <div class="box-body" style="padding-bottom: 10px;">
									    	<s:form name="frmProjectBilling" id="frmProjectBilling" action="Billing" theme="simple">
									    		<s:hidden name="proType" id="proType" />
									    		<s:hidden name="proPage" id="proPage" />
									    		<%-- <s:hidden name="proCount" id="proCount" /> --%>
									    		<s:hidden name="minLimit" id="minLimit" />
												<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
													<i class="fa fa-filter"></i>
												</div>
												<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">Organisation</p>
														<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" headerKey="" headerValue="All Organisations" 
															onchange="submitForm('1');" list="organisationList"/>
													</div>
													<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
														<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
															<p style="padding-left: 5px;">Location</p>
															<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" 
											    				headerKey="" headerValue="All Locations" onchange="submitForm('2');" list="wLocationList" key=""/>
														</div>
														<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
															<p style="padding-left: 5px;">Department</p>
															<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" 
											    				headerKey="0" headerValue="All Departments" onchange="submitForm('2');"/>
														</div>
													<% } else { %>
														<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
															<p style="padding-left: 5px;">Billing Type</p>
															<s:select name="billingType" listKey="billingId" headerKey="" headerValue="All Billing Type" listValue="billingName" 
											    				id="billingType" list="billingTypeList" onchange="submitForm('2');"/>
														</div>
														<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
															<p style="padding-left: 5px;">Billing Frequency</p>
															<s:select name="strBillingFreq" id="strBillingFreq" listKey="billingId" headerKey="" headerValue="All Billing Frequency" 
																listValue="billingName" list="billingFreqList" onchange="submitForm('2');"/>
														</div>
													<% } %>
													<% if(proType == null || proType.equals("") || proType.equalsIgnoreCase("PPB") || proType.equalsIgnoreCase("PAB")) { %>
														<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
															<p style="padding-left: 5px;">Billing Status</p>
															<s:select name="reportType" id="reportType" headerKey="" headerValue="All" list="#{'1':'Pending', '2':'Processing', '3':'Partial'}" 
														    	onchange="submitForm('2');"/>
														</div>
													<% } %>
													
													<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
														<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
															<p style="padding-left: 5px;">Client</p>
															<s:select label="Select Client" name="client" id="client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" />
														</div>
													<% } %>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">&nbsp;</p>
														<input type="button" name="btnSubmit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
													</div>
												</div>
											</s:form>
										</div><!-- /.box-body -->
									</div>	<!-- box box-warning direct-chat direct-chat-warning collapsed-box -->
									
									<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER) && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("PAB"))) { %>
									 	<div class="box-body">
									 		<div style="float: right;">
												<a href="javascript:void(0);" onclick="viewProjectAdHocInvoice();">Generate New AdHoc Invoice</a>
											</div>
										</div>
									<% } %>
										
									<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER) && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("PPB") || proType.equalsIgnoreCase("PAB"))) { %>
										<div class="box-body">
											<div style="float: right; padding-top: 0px;">
											Bulk Action: 
												<span id="selectReceiptsSpan" style="display: none; float: right; margin-left: 7px;"> <a href="javascript:void(0);" onclick="receiveMultipleBill();">Receive Multiple Bills</a> </span>
												<span id="unselectReceiptsSpan" style="float: right; margin-left: 7px; font-weight: bold; color: lightgray;"> Receive Multiple Bills </span>
											</div>
										</div>
									<% } %>
						
						
									<% if((alReport!=null && !alReport.isEmpty()) || (alAdhocReport!= null && !alAdhocReport.isEmpty())) { %>
										<div class="box-body table-responsive no-padding">
											<table class="table table-hover">
												<tr>
													<th>Client Name</th>
													<th>Billing Type</th>
													<th>Billing Frequency</th>
													<% if(proType != null && (proType.equals("PPB") || proType.equals("CPB"))) { %>
														<th>Timesheet Approval</th>
													<% } %>
													<th>Billing Summary</th>
													<th>Invoices</th>
													<th nowrap="nowrap" align="center">
													<% if(strUserType != null && strUserType.equals(IConstants.CUSTOMER)) { %>
														Payments
													
													<% } if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER) && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("PPB") || proType.equalsIgnoreCase("PAB"))) { %>
														Receipts <br/>
														<span style="font-weight: normal;"><input type="checkbox" name="allReceiveBill" id="allReceiveBill" onclick="checkMultipleBill();" /> Select All Receipt</span>
													<% } %>
													</th>
												</tr>
												
												<%
												if(proType != null && (proType.equals("PPB") || proType.equals("CPB"))) {
												
												for(int i=0; alReport!=null && i<alReport.size(); i++) {
													Map hmFreqInner = (Map)alReport.get(i);
													if(hmFreqInner == null) hmFreqInner = new HashMap();
													Map hmInner = (Map)hmFreqInner.get(hmFreqInner.get("PRO_ID"));
													if(hmInner == null) hmInner = new HashMap();
													
													List alInner = (List)hmFreqInner.get("BILLING_SUMMARY");
													if(alInner == null) alInner = new ArrayList();
													
													Map<String, String> hmProInvAmt = hmProInvoiceAmt.get((String)hmFreqInner.get("PRO_FREQ_ID"));
													if(hmProInvAmt == null) hmProInvAmt = new HashMap<String, String>();
													
													List<Map<String,String>> outerList = hmInvDetails.get((String)hmFreqInner.get("PRO_FREQ_ID"));
													String proFreqName = "";
													if(hmFreqInner.get("PRO_FREQ_NAME") != null && !((String)hmFreqInner.get("PRO_FREQ_NAME")).equals("")) {
														proFreqName = " (" +(String)hmFreqInner.get("PRO_FREQ_NAME")+ ")";
													}
												%>
												
												<tr>
													<td valign="top"><%=uF.showData((String)hmInner.get("CLIENT_NAME"), "") %><br/><i>[<%=(String)hmInner.get("PRO_NAME") + proFreqName %>]</i> 
														<%if((outerList == null || outerList.isEmpty() || outerList.size() == 0) && strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
															<img border="0" src="<%=request.getContextPath()%>/images1/icons/news_icon.gif"/>
														<% } else if(strUserType != null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToDouble(hmProReceivedAmount.get((String)hmFreqInner.get("PRO_FREQ_ID"))) == 0) { %>
															<img border="0" src="<%=request.getContextPath()%>/images1/icons/news_icon.gif"/>
														<% } %>
													</td>
													<td valign="top" nowrap="nowrap"><%=uF.showData((String)hmInner.get("BILLING_TYPE"), "") %> </td>
													<td valign="top" nowrap="nowrap"><%=((String)hmFreqInner.get("BILLING_KIND")) %></td>
													<td valign="top">
														<div style="width: 100%; float: left;">
														Project Owner: 
														<% if(uF.parseToInt((String)hmFreqInner.get("MANAGER_TIMESHEET_APPROVAL")) == 1) { %>
															<img title="Approved" style="height:11px;" src="images1/icons/hd_tick_20x20.png">
														<% } else { %>
															<img title="Pending" style="height:10px;" src="images1/icons/hd_cross_16x16.png">
														<% } %>
														</div>
														<div style="width: 100%; float: left;">
														Customer:
														<% if(uF.parseToInt((String)hmFreqInner.get("CUSTOMER_TIMESHEET_APPROVAL")) == 1) { %>
															<img title="Approved" style="height:11px;" src="images1/icons/hd_tick_20x20.png">
														<% } else { %>
															<img title="Pending" style="height:10px;" src="images1/icons/hd_cross_16x16.png;">
														<% } %>
														</div>
													</td>
													
													<td valign="top" nowrap="nowrap" align="right">
													<%	for(int ii=0; ii<alInner.size();) { %>
													
														Contract Amount: <b><%=uF.showData((String)alInner.get(ii++), "") %></b><br/>
														Pending Amount: <b><%=uF.showData((String)alInner.get(ii++), "") %></b><br/>
														<br/>
														Invoiced Amount: <b><%=uF.showData((String)alInner.get(ii++), "") %></b><br/>
														<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
														Received 
														<% } else { %>
														Paid 
														<% } %>
														Amount: <b><%=uF.showData((String)alInner.get(ii++), "") %></b><br/>
														Balance Amount: <b><%=uF.showData((String)alInner.get(ii++), "") %></b><br/>
														
													<%} %>
													</td>
												
													<td nowrap="nowrap" valign="top" align="center" >
														<%
														for(int ii=0;outerList!=null && ii<outerList.size();ii++) {
															Map<String,String> hmInnerMap = (Map<String,String>)outerList.get(ii);
															if((String)hmInnerMap.get("INVOICE_GENERATED_DATE")!=null) { 
																String invoiceLabel = "Generated Invoice";
																if(uF.parseToBoolean((String)hmInnerMap.get("INVOICE_IS_CANCEL"))) {
																	invoiceLabel = "Cancelled Invoice";
																} 
															%>
															
														<div class="m_div">
															<div id="deleteDivInvoice_<%=(String)hmInnerMap.get("INVOICE_ID")%>" class="i_div1">
																<%if(hmInner.get("BILLING_TYPE")!=null && (hmInner.get("BILLING_TYPE").equals("Daily") || hmInner.get("BILLING_TYPE").equals("Hourly") || hmInner.get("BILLING_TYPE").equals("Monthly")) && (uF.parseToInt(hmInnerMap.get("INVOICE_TYPE")) == IConstants.PRORETA_INVOICE) ) { %>
																	<% if(uF.parseToInt((String)hmInner.get("PROJECT_INVOICE_FORMAT_ID")) > 0) { %>
																		<%=invoiceLabel %>
																		- <a target="_blank" href="GenerateInvoicePdfFormatOne.action?operation=preview&type=<%=""+IConstants.PRORETA_INVOICE %>&pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Preview</a>
																		| <a href="GenerateInvoicePdfFormatOne.action?operation=pdfDwld&type=<%=""+IConstants.PRORETA_INVOICE %>&pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Download</a>
																		<%if(isEmail){ %>
																		| <a href="javascript:void(0)" onclick="confirm('Do you want to send a mail?')? window.location='GenerateInvoicePdfFormatOne.action?operation=mail&proType=<%=proType %>&type=<%=""+IConstants.PRORETA_INVOICE %>&pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>': ''">Send Mail</a>
																		<%} %>
																	<% } else { %>
																		<%=invoiceLabel %>
																		- <a target="_blank" href="GenerateProjectInvoice.action?operation=pdfDwld&type=<%=""+IConstants.PRORETA_INVOICE %>&pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Preview</a>
																		| <a href="GenerateProjectInvoice.action?operation=pdfDwld&type=<%=""+IConstants.PRORETA_INVOICE %>&pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Download</a>
																		<%if(isEmail){ %>
																		| <a href="javascript:void(0)" onclick="confirm('Do you want to send a mail?')? window.location='GenerateProjectInvoice.action?operation=mail&proType=<%=proType %>&type=<%=""+IConstants.PRORETA_INVOICE %>&pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>': ''">Send Mail</a>
																		<%} %>
																	<% } %>
																<% } else { %>
																	<% if(uF.parseToInt((String)hmInner.get("PROJECT_INVOICE_FORMAT_ID")) > 0) { %>
																		<%=invoiceLabel %>
																		- <a target="_blank" href="GenerateInvoicePdfFormatOne.action?operation=preview&amp;pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Preview</a>
																		| <a href="GenerateInvoicePdfFormatOne.action?operation=pdfDwld&amp;pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Download</a>
																		<%if(isEmail){ %>
																		| <a href="javascript:void(0)" onclick="confirm('Do you want to send a mail?')? window.location='GenerateInvoicePdfFormatOne.action?operation=mail&proType=<%=proType %>&pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>': ''">Send Mail</a>
																		<%} %>
																	<% } else { %>
																		<%=invoiceLabel %>
																		- <a target="_blank" href="GenerateProjectInvoice.action?operation=preview&amp;pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Preview</a>
																		| <a href="GenerateProjectInvoice.action?operation=pdfDwld&amp;pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Download</a>
																		<%if(isEmail){ %>
																		| <a href="javascript:void(0)" onclick="confirm('Do you want to send a mail?')? window.location='GenerateProjectInvoice.action?operation=mail&proType=<%=proType %>&pro_id=<%=(String)hmInner.get("PROJECT_ID")%>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>': ''">Send Mail</a>
																		<%} %>
																	<% } %>
																<% } %>
																<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER) && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("PPB"))) { %>
																	<%-- <span style="float: right">
																		<a href="javascript:void(0)" onclick="openFeedsForm('<%=(String)hmInnerMap.get("INVOICE_ID")%>','<%=(String)hmInner.get("PROJECT_ID")%>', '<%=proType %>')" title="click here for feed">
																			<img src="images1/icons/feed.png" height="16" width="16">
																		</a>
																	</span> --%>
																	<% if(hmInvoiceReceivedAmount != null && uF.parseToDouble(hmInvoiceReceivedAmount.get((String)hmInnerMap.get("INVOICE_ID"))) <= 0) { %>
																		<span style="float: right" ><a href="javascript:void(0)" style="color: red;" onclick="deleteGeneratedProjectInvoice('<%=(String)hmInnerMap.get("INVOICE_ID")%>','<%=(String)hmInner.get("PROJECT_ID")%>')" class="fa fa-trash-o" title="click to delete invoice"></a></span>
																		<% if(!uF.parseToBoolean((String)hmInnerMap.get("INVOICE_IS_CANCEL"))) { %>
																			<span style="float: right" ><a href="DeleteGeneratedProjectInvoice.action?type=C&proType=<%=proType %>&invoiceId=<%=(String)hmInnerMap.get("INVOICE_ID")%>&proId=<%=(String)hmInner.get("PROJECT_ID")%>" onclick="return confirm('Are you sure, you want to cancel this invoice?')" title="click to cancel invoice"><img src="images1/icons/hd_cross_16x16.png" style="margin-right: 5px; margin-top: 2px;"></a> </span>
																		<% } %>
																	<% } %>
																<% } else { %>
																	<%-- <span style="float: right">
																		<a href="javascript:void(0)" onclick="openFeedsForm('<%=(String)hmInnerMap.get("INVOICE_ID")%>','<%=(String)hmInner.get("PROJECT_ID")%>', '<%=proType %>')" title="click here for feed">
																			<img src="images1/icons/feed.png" height="16" width="16">
																		</a>
																	</span> --%>
																<% } %>
																<br/> on <%=uF.showData((String)hmInnerMap.get("INVOICE_GENERATED_DATE"), "")%> for <%=(String)hmInnerMap.get("INVOICE_AMOUNT")%><br/>Inv. No.: <%=uF.showData((String)hmInnerMap.get("INVOICE_CODE"), "")%><br/>
															</div>
														</div>
														
														<% } else { %>
															<%=uF.showData((String)hmInner.get("COMPLETED"), "")%><div id="myDiv_<%=i%>"><a href="javascript:void(0)" onclick="showAjaxLoading('myDiv_<%=i%>'); getContent('myDiv_<%=i%>', 'GenerateInvoice.action?strProjectId=<%=(String)hmInner.get("PROJECT_ID")%>')">Raise Invoice</a></div><br/>
														<% } 
														}
														%>
														<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER) && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("PPB"))) { %>
															<div style="float:left;width:99%;">
																<a href="javascript:void(0)" onclick="viewProjectInvoice('<%=(String)hmFreqInner.get("PRO_FREQ_ID") %>','<%=(String)hmInner.get("PROJECT_ID")%>','<%=hmProInvAmt.get("PRO_AMT") %>','<%=hmProInvAmt.get("INVOICE_AMT") %>','<%=(String)hmInner.get("PROJECT_INVOICE_FORMAT_ID")%>','<%=(String)hmFreqInner.get("PRO_OPE_AMOUNT") %>')">Generate New Invoice</a>
																<a href="Billing.action?operation=D&proFreqId=<%=(String)hmFreqInner.get("PRO_FREQ_ID")%>&proId=<%=(String)hmInner.get("PROJECT_ID")%>" onclick="return confirm('Are you sure, you want to delete this project from billing?')" title="click to delete project from billing"><img src="images1/icons/hd_cross_16x16.png" style="margin-right: 5px; margin-top: 2px;"></a><br/>or
																<% if(hmInner.get("BILLING_TYPE")!=null && (hmInner.get("BILLING_TYPE").equals("Daily") || hmInner.get("BILLING_TYPE").equals("Hourly") || hmInner.get("BILLING_TYPE").equals("Monthly") )) { %>
																	<br/> <a href="javascript:void(0)" onclick="viewProjectProRetaInvoice('<%=(String)hmFreqInner.get("PRO_FREQ_ID") %>','<%=(String)hmInner.get("PROJECT_ID")%>','<%=uF.showData((String)hmInner.get("BILLING_TYPE"), "") %>','<%=(String)hmInner.get("PROJECT_INVOICE_FORMAT_ID")%>','<%=(String)hmFreqInner.get("PRO_OPE_AMOUNT") %>')">Generate New Pro-rata Invoice</a>
																<%} else { %>
																	<br/> <a href="javascript:void(0)" onclick="viewProjectPartialInvoicePopup('<%=(String)hmFreqInner.get("PRO_FREQ_ID") %>','<%=(String)hmInner.get("PROJECT_ID")%>','<%=hmProInvAmt.get("PRO_AMT") %>','<%=hmProInvAmt.get("INVOICE_AMT") %>','<%=(String)hmInner.get("PROJECT_INVOICE_FORMAT_ID")%>','<%=(String)hmFreqInner.get("PRO_OPE_AMOUNT") %>')">Generate New Partial Invoice</a>
																<%} %>
															</div>
														<% } %>
													</td>
														
													<td nowrap="nowrap" valign="top" align="center" >
														<%
														for(int ii=0;outerList!=null && ii<outerList.size(); ii++) {
															Map<String,String> hmInnerMap = (Map<String,String>)outerList.get(ii);
															if((String)hmInnerMap.get("INVOICE_GENERATED_DATE") != null) { 
																
															%>
															
														<div class="m_div"> 	
															<div id="deleteDivReceiveBill_<%=(String)hmInnerMap.get("INVOICE_ID")%>" class="i_div2">
															
															<% 
															if(!uF.parseToBoolean((String)hmInnerMap.get("INVOICE_IS_CANCEL"))) { %>
															<%if((String)hmInnerMap.get("BALANCE_AMOUNT") != null) { %>
																<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER) && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("PPB"))) { %>
																	<input type="checkbox" name="receiveBill" id="receiveBill" value="<%=(String)hmFreqInner.get("PRO_FREQ_ID")+"_"+(String)hmInner.get("PROJECT_ID")+"_"+(String)hmInnerMap.get("INVOICE_ID")+"_"+(String)hmInner.get("CLIENT_ID")%>"  onclick="checkAllBillChecked();"/>
																	<a href="javascript:void(0);" onclick="receiveBill('<%=(String)hmFreqInner.get("PRO_FREQ_ID") %>','<%=(String)hmInner.get("PROJECT_ID")%>','<%=(String)hmInnerMap.get("INVOICE_ID")%>','<%=(String)hmInner.get("CLIENT_ID")%>')">Receive Bill</a>
																	<br/>
																<% } %>
																Balance: <%=(String)hmInnerMap.get("BALANCE_AMOUNT")%>
															<% } else if(uF.parseToDouble((String)hmInnerMap.get("INVOICE_AMOUNT_ONLY")) > 0) { %>
																Full Amount 
																<% if(strUserType != null && strUserType.equals(IConstants.CUSTOMER)) { %>
																	Paid.
																<% } else { %>
																	Received.
																<% } %>
															<% } else { %>
																-
															<% } %>
															<% } else { %>
															Cancelled Invoice.
															<% } %>
															</div>
														</div>
														<% } } %>
													</td>
												</tr>
												<% } %>
	
											<% } else if(proType != null && (proType.equals("PAB") || proType.equals("CAB"))) {
													
												for(int i=0; alAdhocReport!=null && i<alAdhocReport.size(); i++) {
													Map hmInner = (Map)alAdhocReport.get(i);
													if(hmInner == null) hmInner = new HashMap();
													List alInner = (List)hmInner.get("BILLING_SUMMARY");
													if(alInner == null)alInner = new ArrayList(); 
													
												%>
												
												<tr>
													<td valign="top"><%=hmInner.get("CLIENT_NAME") %> </td>
													<td valign="top">AdHoc</td>
													<td valign="top">AdHoc</td>
													<td valign="top" nowrap="nowrap" align="right">
													
														<% for(int ii=0; ii<alInner.size();) { %>
														
														Invoiced Amount: <b><%=uF.showData((String)alInner.get(ii++), "") %></b><br/>
															<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
														Received 
														<% } else { %>
														Paid 
														<% } %>
														Amount: <b><%=uF.showData((String)alInner.get(ii++), "") %></b><br/>
														Balance Amount: <b><%=uF.showData((String)alInner.get(ii++), "") %></b><br/>
														
														<% } %>
													</td>
												
													<td nowrap="nowrap" valign="top" align="center" >
													
														<%
														List<Map<String,String>> outerList = hmAdhocInvDetails.get((String)hmInner.get("INVOICE_ID"));
														for(int ii=0; outerList!=null && ii<outerList.size();ii++) {
															Map<String, String> hmInnerMap = (Map<String, String>)outerList.get(ii);
															if(hmInnerMap.get("INVOICE_GENERATED_DATE") != null) { 
															String InvoiceType = "";
															if(hmInnerMap.get("INVOICE_TYPE") != null && hmInnerMap.get("INVOICE_TYPE").equals(""+IConstants.ADHOC_PRORETA_INVOICE)) {
																InvoiceType = IConstants.ADHOC_PRORETA_INVOICE + ""; 
															} else {
																InvoiceType = IConstants.ADHOC_INVOICE + "";
															}
															String invoiceLabel = "Generated Invoice";
															if(uF.parseToBoolean((String)hmInnerMap.get("INVOICE_IS_CANCEL"))) {
																invoiceLabel = "Cancelled Invoice";
															} 
															%>
															
														<div class="m_div">
															<div id="deleteDivInvoice_<%=(String)hmInnerMap.get("INVOICE_ID")%>" class="i_div1">
																<% if(uF.parseToInt(hmInnerMap.get("INVOICE_TEMPLATE_ID")) > 0) { %>
																	<%=invoiceLabel %>
																	- <a target="_blank" href="GenerateInvoicePdfFormatOne.action?operation=preview&type=<%=InvoiceType %>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Preview</a>
																	| <a href="GenerateInvoicePdfFormatOne.action?operation=pdfDwld&type=<%=InvoiceType %>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Download</a>
																	<%if(isEmail){ %>
																	| <a href="javascript:void(0)" onclick="confirm('Do you want to send a mail?')? window.location='GenerateInvoicePdfFormatOne.action?operation=mail&proType=<%=proType %>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>': ''">Send Mail</a>
																	<%} %>
																<% } else { %>
																	<%=invoiceLabel %>
																	- <a target="_blank" href="GenerateProjectInvoice.action?operation=preview&type=<%=InvoiceType %>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Preview</a>
																	| <a href="GenerateProjectInvoice.action?operation=pdfDwld&type=<%=InvoiceType %>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>">Download</a>
																	<%if(isEmail){ %>
																	| <a href="javascript:void(0)" onclick="confirm('Do you want to send a mail?')? window.location='GenerateProjectInvoice.action?operation=mail&proType=<%=proType %>&invoice_id=<%=(String)hmInnerMap.get("INVOICE_ID")%>': ''">Send Mail</a>
																	<%} %>
																<% } %>
																
																<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER) && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("PAB"))) { %>
																	<% if(hmInvoiceReceivedAmount != null && uF.parseToDouble(hmInvoiceReceivedAmount.get((String)hmInnerMap.get("INVOICE_ID"))) <= 0) { %>
																		<%-- <span style="float: right">
																			<a href="javascript:void(0)" onclick="openFeedsForm('<%=(String)hmInnerMap.get("INVOICE_ID")%>','<%=(String)hmInner.get("PROJECT_ID")%>', '<%=proType %>')" title="click here for feed">
																				<img src="images1/icons/feed.png" height="16" width="16">
																			</a>
																		</span> --%>
																		<span style="float: right" ><a href="javascript:void(0)" style="color: red;" onclick="deleteGeneratedProjectInvoice('<%=(String)hmInnerMap.get("INVOICE_ID")%>','<%=(String)hmInner.get("PROJECT_ID")%>')" class="fa fa-trash-o" title="click to delete invoice"></a></span>
																		<% if(!uF.parseToBoolean((String)hmInnerMap.get("INVOICE_IS_CANCEL"))) { %>
																			<span style="float: right" ><a href="DeleteGeneratedProjectInvoice.action?type=C&proType=<%=proType %>&invoiceId=<%=(String)hmInnerMap.get("INVOICE_ID")%>&proId=<%=(String)hmInner.get("PROJECT_ID")%>" onclick="return confirm('Are you sure, you want to cancel this invoice?')" title="click to cancel invoice"><img src="images1/icons/hd_cross_16x16.png" style="margin-right: 5px; margin-top: 2px;"></a> </span>
																			<% if(uF.parseToInt(hmInnerMap.get("INVOICE_TEMPLATE_ID")) == 0) { %>
																				<span style="float: right" ><a href="javascript:void(0)" onclick="editProjectAdHocInvoice('<%=(String)hmInnerMap.get("INVOICE_ID")%>','<%=(String)hmInnerMap.get("ADHOC_BILLING_TYPE")%>')" class="edit_lvl" title="click to edit invoice"></a></span>
																			<% } %>
																		<% } %>
																	<% } %>
																<% } else { %>
																	<%-- <span style="float: right">
																		<a href="javascript:void(0)" onclick="openFeedsForm('<%=(String)hmInnerMap.get("INVOICE_ID")%>','<%=(String)hmInner.get("PROJECT_ID")%>', '<%=proType %>')" title="click here for feed">
																			<img src="images1/icons/feed.png" height="16" width="16">
																		</a>
																	</span> --%>
																<% } %>
																<br/> on <%=uF.showData((String)hmInnerMap.get("INVOICE_GENERATED_DATE"), "")%> for <%=(String)hmInnerMap.get("INVOICE_AMOUNT")%><br/>Inv. No.: <%=uF.showData((String)hmInnerMap.get("INVOICE_CODE"), "")%><br/>
															</div>
														</div>
															
														<%} else { %>
															<%=uF.showData((String)hmInner.get("COMPLETED"), "")%><div id="myDiv_<%=i%>"><a href="javascript:void(0)" onclick="showAjaxLoading('myDiv_<%=i%>'); getContent('myDiv_<%=i%>', 'GenerateInvoice.action?strProjectId=<%=(String)hmInner.get("PROJECT_ID")%>')">Raise Invoice</a></div><br/>
														<% } } %>
													</td>
													
													<td nowrap="nowrap" valign="top" align="center" >
														<%
														for(int ii=0; outerList!=null && ii<outerList.size();ii++) {
															Map<String,String> hmInnerMap = (Map<String,String>)outerList.get(ii);
															if((String)hmInnerMap.get("INVOICE_GENERATED_DATE") != null) { 
														%>
															
														<div class="m_div">
															<div id="deleteDivReceiveBill_<%=(String)hmInnerMap.get("INVOICE_ID")%>" class="i_div2">
															<% if(!uF.parseToBoolean((String)hmInnerMap.get("INVOICE_IS_CANCEL"))) { %>
															<%if((String)hmInnerMap.get("BALANCE_AMOUNT") != null) { %>
																<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER) && (proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("PAB"))) { %>
																	<input type="checkbox" name="receiveBill" id="receiveBill" value="<%=(String)hmInner.get("PRO_FREQ_ID")+"_"+(String)hmInner.get("PROJECT_ID")+"_"+(String)hmInnerMap.get("INVOICE_ID")+"_"+(String)hmInner.get("CLIENT_ID")%>"  onclick="checkAllBillChecked();"/>
																	<a href="javascript:void(0);" onclick="receiveBill('<%=(String)hmInner.get("PRO_FREQ_ID") %>',<%=(String)hmInner.get("PROJECT_ID")%>,<%=(String)hmInnerMap.get("INVOICE_ID")%>,<%=(String)hmInner.get("CLIENT_ID")%>)">Receive Bill</a>
																	<br/>
																<% } %>
																Balance: <%=(String)hmInnerMap.get("BALANCE_AMOUNT")%>
															<% } else if(uF.parseToDouble((String)hmInnerMap.get("INVOICE_AMOUNT_ONLY")) > 0) { %>
																Full Amount 
																<% if(strUserType != null && strUserType.equals(IConstants.CUSTOMER)) { %>
																	Paid.
																<% } else { %>
																	Received.
																<% } %>
															<% } else { %>
																-
															<% } %>
															<% } else { %>
															Cancelled Invoice.
															<% } %>
															</div>	
														</div>		
														<% } } %>
													</td>
												
												</tr>
												<% }
												
												} %>
												
											</table>
										</div>
									<% } else { %>
										<div class="alert" style="float: left; width: 100%; background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No bills available.</div>
									<% } %>
									
									<div class="box-footer text-center">
										<div style="text-align: center; float: left; width: 100%; margin-top: 10px;">
										
										<%  int intproCnt = uF.parseToInt(proCount);
											int pageCnt = 0;
											int minLimit = 0;
											//out.println("intProCNT==========>"+intproCnt);
											for(int i=1; i<=intproCnt; i++) { 
													minLimit = pageCnt * 10;
													pageCnt++;
										%>
										<% if(i ==1) {
											String strPgCnt = (String)request.getAttribute("proPage");
											String strMinLimit = (String)request.getAttribute("minLimit");
											//out.println("proPage=====>"+strPgCnt);
											//out.println("minLimit=====>"+strMinLimit);
											if(uF.parseToInt(strPgCnt) > 1) {
												 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
												 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
											}
											if(strMinLimit == null) {
												strMinLimit = "0";
											}
											if(strPgCnt == null) {
												strPgCnt = "1";
											}
										%>
											<span style="color: lightgray;">
											<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
												<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
												<%="< Prev" %></a>
											<% } else { %>
												<b><%="< Prev" %></b>
											<% } %>
											</span>
											<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
											<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
											style="color: black;"
											<% } %>
											><%=pageCnt %></a></span>
											
											<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
												<b>...</b>
											<% } %>
										
										<% } %>
										<% if(i > 1 && i < intproCnt) { %>
										<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
											<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
											<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
											style="color: black;"
											<% } %>
											><%=pageCnt %></a></span>
										<% } %>
										<% } %>
										
										<% if(i == intproCnt && intproCnt > 1) {
											String strPgCnt = (String)request.getAttribute("proPage");
											String strMinLimit = (String)request.getAttribute("minLimit");
											 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
											 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
											 if(strMinLimit == null) {
												strMinLimit = "0";
											}
											if(strPgCnt == null) {
												strPgCnt = "1";
											}
											%>
											<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
												<b>...</b>
											<% } %>
										
											<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
											<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
											style="color: black;"
											<% } %>
											><%=pageCnt %></a></span>
											<span style="color: lightgray;">
											<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
												<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
											<% } else { %>
												<b><%="Next >" %></b>
											<% } %>
											</span>
										<% } %>
										<%} %>
										
										</div>
							
									</div>	<!-- box-footer text-center -->
								
			                	</div>	<!-- box-body -->
			                </div>	<!-- active tab-pane -->
						</div>	<!-- tab-content box-body -->
						
					</div>	<!-- nav-tabs-custom -->
	            </div>	<!-- col-md-12 -->
		
			</div>	<!-- row -->
          
<!--           
			<div id="myModal" class="modal fade">
			    <div id="modalDialog" class="modal-dialog" style="width: 800px;">
			        <div class="modal-content">
			            <div class="modal-header">
			                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			                <h4 id="modalTitle" class="modal-title">&nbsp;</h4>
			            </div>
			            <div id="modalBody" class="modal-body">&nbsp;</div>
			        </div>
			    </div>
			</div>
 -->		
		</section>


	<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
	
	<%-- <script>
		
	</script> --%>

</div>