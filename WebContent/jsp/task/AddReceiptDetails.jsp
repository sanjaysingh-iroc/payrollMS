<div id="divResult">

<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script type="text/javascript">

	$(document).ready(function() {
	    $("#receiptDate").datepicker({format : 'dd/mm/yyyy'});
	    $("#billingId").multiselect().multiselectfilter();
	    $(".validateRequired").prop('required',true);
	});
	
	function validateAmt(strValue,id){
		var totalReceivedAmt = $('#totalAmtReceived').val();
		var pfAmt = $("#totalprofFees").val();
		var opeAmt = $("#totalOPE").val();
		var tdsAmt = $("#strTDS").val();
		
		if((isNaN(parseFloat(pfAmt)))) {
			pfAmt = 0;
		}
		if((isNaN(parseFloat(opeAmt)))) {
			opeAmt = 0;
		}
		if((isNaN(parseFloat(tdsAmt)))) {
			tdsAmt = 0;
		}
		
		var strTotAmt = parseFloat(pfAmt)+parseFloat(opeAmt)-parseFloat(tdsAmt);
		
		/* if(parseFloat(strTotAmt) > parseFloat(totalReceivedAmt) || parseFloat(strValue) > parseFloat(totalReceivedAmt)){ */
		if(parseFloat(strTotAmt) > parseFloat(totalReceivedAmt)){
			alert("Amount exceeded from total received amount");
			document.getElementById(id).value = "";
		}
		
	}
		
	function validateRecAmt(strValue,id,cnt){
		
		var receivedAmt = $('#receivedAmt'+cnt).val();
		var pfAmt = $("#profFeesAmt"+cnt).val();
		var opeAmt = $("#opeAmt"+cnt).val();
		
		if((isNaN(parseFloat(pfAmt)))) {
			pfAmt = 0;
		}
		if((isNaN(parseFloat(opeAmt)))) {
			opeAmt = 0;
		}
		
		if(strValue == ''){
			strValue = 0;
		}
		
		var strTotAmt = parseFloat(pfAmt)+parseFloat(opeAmt);
		
		if(parseFloat(strTotAmt) > parseFloat(receivedAmt)){
			alert("Amount exceeded from total received amount");
			document.getElementById(id).value = "";
		}
		
	}
	
	function getCleintwiseBill(clientId){
		xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
	    } else {
			var xhr = $.ajax({
				url : "GetProjectBillingAjax.action?clientId="+clientId,
				cache : false,
				success : function(data) {
                	if(data == "") {
                	
                	} else {
                		document.getElementById("clientBillTd").innerHTML = data;
					}
                	$("#billingId").multiselect().multiselectfilter();
				}
			});
		}
	}
	
	function addBillAmount(){
		
		document.getElementById("billTableDiv").style.display = "";
		
		var billIds = getSelectedValue("billingId");
		var billNumberMap = getSelectedText("billingId");
		
		var tableId = document.getElementById("row_bill_table");
		var tableRowCnt = tableId.rows.length;
		for(var k=1; k<tableRowCnt; k++){
			removeProjects("TR_"+k);
		}
		
		document.getElementById("totalOPE").value = "";
		document.getElementById("totalprofFees").value = "";
		document.getElementById("totalAmtReceived").value = "";
		document.getElementById("totCGSTAmt").value = "";
		document.getElementById("totSGSTAmt").value = "";
		document.getElementById("totIGSTAmt").value = "";
		
		var strBill_ID = "";
		var billMap = new Map();
		var hmId = "";
		for(var j=0; j<billIds.length; j++){
			if(j == 0){
				strBill_ID = billIds[j];
			} else{
				strBill_ID += "," + billIds[j];
			}
		}
		
		
		for(var i=0; i<billIds.length; i++){
			var table = document.getElementById("row_bill_table");
			var rowCount = i+1;
			var row = table.insertRow(rowCount);
			
	        row.id = "TR_"+rowCount;
	        var cell0 = row.insertCell(0);
	        cell0.innerHTML ="<td class='txtlabel'><input type='hidden' name = 'strBillId' id='strBillId"+i+"' value='"+billIds[i]+"'></input>"+billNumberMap.get(billIds[i])+"</td>";
	        var cell1 = row.insertCell(1); 
			cell1.innerHTML = "<td class='alignRight'> <input type='text' name='billAmt' id='billAmt"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" readonly='true' ></input></td>";
			var cell2 = row.insertCell(2); 
			cell2.innerHTML = "<td class='alignRight'> <input type='text' name='billOSAmt' id='billOSAmt"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" readonly='true' ></input></td>";
			var cell3 = row.insertCell(3); 
			cell3.innerHTML = "<td class='alignRight'> <input type='text' name='receivedAmt' id='receivedAmt"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" ></input></td>";
			var cell4 = row.insertCell(4); 
			cell4.innerHTML = "<td class='alignRight'> <input type='text' name='profFeesAmt' id='profFeesAmt"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" onchange=\"calTotal('profFees','"+billIds.length+"'); validateRecAmt(this.value,this.id,'"+i+"');\" ></input></td>";
			var cell5 = row.insertCell(5); 
			cell5.innerHTML = "<td class='alignRight'> <input type='text' name='opeAmt' id='opeAmt"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" onchange=\"calTotal('OPE','"+billIds.length+"'); validateRecAmt(this.value,this.id,'"+i+"');\" ></input></td>";
			
			var cell6 = row.insertCell(6); 
			cell6.innerHTML = "<td class='alignRight'> <input type='text' name='strCGST' id='strCGST"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" onchange=\"calTotalGST('CGST','"+billIds.length+"');\" ></input></td>";
			var cell7 = row.insertCell(7); 
			cell7.innerHTML = "<td class='alignRight'> <input type='text' name='strSGST' id='strSGST"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" onchange=\"calTotalGST('SGST','"+billIds.length+"');\" ></input></td>";
			var cell8 = row.insertCell(8); 
			cell8.innerHTML = "<td class='alignRight'> <input type='text' name='strIGST' id='strIGST"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" onchange=\"calTotalGST('IGST','"+billIds.length+"');\" ></input></td>";
			var valAmt = $("#option_"+i).attr('billDetails');
			$("#billAmt"+i).val(valAmt);
			
			var valAmtRcvd = $("#option_"+i).attr('billDetailsRcvd');
			var totalPaid = eval(valAmt) - eval(valAmtRcvd);
			$("#billOSAmt"+i).val(totalPaid);
		}
		
		
	}
	
	function removeProjects(rowid) {
	    var table =  document.getElementById('row_bill_table');
	    var row = document.getElementById(rowid);
	    table.deleteRow(row.rowIndex);
	}
	
	function getSelectedText(selectId) {
		var choice = document.getElementById(selectId);
		var selectedPro = new Map();
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			if (choice.options[i].selected == true) {
				var optionTag = choice.options[i].text;
				var optionValue = choice.options[i].value;
				selectedPro.set(optionValue,optionTag);
			}
		}
		
		return selectedPro;
	}
	
	function getSelectedValue(selectId) {
		var choice = document.getElementById(selectId);
		var exportchoice = new Array();
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			if (choice.options[i].selected == true) {
				var optionValue = choice.options[i].value;
				exportchoice.push(optionValue);
			}
		}
		
		return exportchoice;
	}
	
	function calTotal(type,size){
		
		var strTotal = 0;
		var totalProfFeesAmt = $('#totalprofFees').val();
		var totalOPEAmt = $('#totalOPE').val();
		
		if((isNaN(parseFloat(totalProfFeesAmt)))) {
			totalProfFeesAmt = 0;
		}
		
		if((isNaN(parseFloat(totalOPEAmt)))) {
			totalOPEAmt = 0;
		}
		
		if(type == "OPE"){
			
			totalOPEAmt = 0;
			for(var i =0; i<parseInt(size); i++){
				var strOpe = $('#opeAmt'+i).val();
				
				if((isNaN(parseFloat(strOpe)))) {
					strOpe = 0;
				}
				
				totalOPEAmt = parseFloat(totalOPEAmt)+parseFloat(strOpe);
			}
			
			document.getElementById("totalOPE").value = totalOPEAmt.toFixed(2);
		
		} else if(type == "profFees"){
			
			totalProfFeesAmt = 0;
			
			for(var i =0; i<parseInt(size); i++){
				
				var strprofFees = $('#profFeesAmt'+i).val();
				
				if((isNaN(parseFloat(strprofFees)))) {
					strprofFees = 0;
				}
				
				totalProfFeesAmt = parseFloat(totalProfFeesAmt)+parseFloat(strprofFees);
			}
			
			document.getElementById("totalprofFees").value = totalProfFeesAmt.toFixed(2);
		}
		
		strTotal = parseFloat(totalProfFeesAmt)+parseFloat(totalOPEAmt);
		document.getElementById("totalAmtReceived").value = strTotal.toFixed(2);
		
	}
	
	function calTotalGST(type,size){
		
		var sGstTotAmt = 0;
		var cGstTotAmt = 0;
		var iGstTotAmt = 0;
		
		if(type == 'CGST'){
			
			for(var i =0; i<parseInt(size); i++){
				
				var cgstAmt = $('#strCGST'+i).val();
				
				if((isNaN(parseFloat(cgstAmt)))) {
					cgstAmt = 0;
				}
				
				cGstTotAmt = parseFloat(cGstTotAmt)+parseFloat(cgstAmt);
			}
			document.getElementById("totCGSTAmt").value = cGstTotAmt.toFixed(2);
			
		} else if(type == 'SGST'){
			
			for(var i =0; i<parseInt(size); i++){
				
				var sgstAmt = $('#strSGST'+i).val();
				
				if((isNaN(parseFloat(sgstAmt)))) {
					sgstAmt = 0;
				}
				
				sGstTotAmt = parseFloat(sGstTotAmt)+parseFloat(sgstAmt);
			}
			document.getElementById("totSGSTAmt").value = sGstTotAmt.toFixed(2);
			
		} else if(type == 'IGST'){
			
			for(var i =0; i<parseInt(size); i++){
				
				var igstAmt = $('#strIGST'+i).val();
				
				if((isNaN(parseFloat(igstAmt)))) {
					igstAmt = 0;
				}
				
				iGstTotAmt = parseFloat(iGstTotAmt)+parseFloat(igstAmt);
			}
			document.getElementById("totIGSTAmt").value = iGstTotAmt.toFixed(2);
			
		}
		
	}
	
	function addReceipt(operation) {
		
		/* var strBillId = document.getElementById("strBillId").value; */
		var form_data = $("#formAddReceiptDetails").serialize();
		$("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
	     	url: "AddReceiptDetails.action",
	     	type: 'POST',
	     	data: form_data+'&operation='+operation,
	     	success: function(result){
	     		$("#divResult").html(result);
	     	}
	    });
		
	}
	
	
</script>

<% UtilityFunctions uF = new UtilityFunctions(); %>

<section class="content">
	<div class="row jscroll">
		<div class="col-md-12">
			<div class="box box-primary">
				<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 500px;">
				<div><h4> Add Receipts</h4></div>
				<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE),"") %>
					<s:form theme="simple" action="AddReceiptDetails" name="frmAddReceiptDetails" id="formAddReceiptDetails" cssClass="formcss" >
						<div><h5 style="font-weight: bold;">Step 1: Select Bills</h5></div>
						<table border="0" class="table table_no_border" style="margin-top: 10px; width:auto;">
							<tr>
								<th class="alignCenter">Client Name</th>
								<th class="alignCenter">Bill Number</th>
							</tr>
							<tr>
								<td>
									<s:select label="Select Client" name="strClient" id="strClient" listKey="clientId" listValue="clientName" headerKey="" headerValue="Select Client" list="clientList" key="" onchange="getCleintwiseBill(this.value);" />
								</td>
								<%-- <td>
									<s:select label="Select Client" name="strClient" id="strClient" listKey="clientId" listValue="clientName" list="clientList" key="" />
								</td> --%>
								<td id="clientBillTd">
									<s:select theme="simple" label="Select Bill No" name="billingId" id="billingId" listKey="billID" listValue="billNumber" list="billDetailsList" key="" multiple="true" onchange="addBillAmount();" />
								</td>
							</tr>
						</table>
						<div><h5 style="font-weight: bold;">Step 2: Enter Receipt Details</h5></div>
						<div id="billTableDiv" style="padding-left:20px; display:none text-align:center">
							<table id="row_bill_table" class="table table_no_border" style="margin-top: 10px; width:90%;">
								<tr id="TR_0">
									<th>Bill No.</th>
									<th>Bill Amount</th>
									<th>Balance O/s Amt</th>
									<th>Amount Received</th>
									<th>Prof Fees</th>
									<th>OPE</th>
									<th>CGST</th>
									<th>SGST</th>
									<th>IGST</th>
								</tr>
							</table>
						</div>
						<div><h5 style="font-weight: bold;">Step 3: Receipt Summary</h5></div>
						<table border="0" class="table table_no_border" style="margin-top: 10px; width:93%;">
							<tr>
								<th class="alignCenter">Bank Name & Account</th>
								<th class="alignCenter">Receipt No.</th>
								<th class="alignCenter">Receipt Date</th>
								<th class="alignCenter">Total Amount Received</th>
								<th class="alignCenter">Total Prof Fees</th>
								<th class="alignCenter">Total OPE</th>
								<th class="alignCenter">CGST</th>
								<th class="alignCenter">SGST</th>
								<th class="alignCenter">IGST</th>
								<th class="alignCenter">TDS</th>
							</tr>
							<tr>
								<td>
									<s:select name="bankName" listKey="bankId" listValue="bankName" headerKey="" headerValue="Select Bank" list="bankList"/>
								</td>
								<td>
									<s:textfield name="receiptNo" id="receiptNo" cssClass="validateRequired" ></s:textfield>	<!-- cssStyle="width: 120px !important;" -->
								</td>
								<td>
									<s:textfield name="receiptDate" id="receiptDate" cssClass="validateRequired" cssStyle="width: 120px !important;" />
								</td>
								<td>
									<s:textfield name="totalAmtReceived" id="totalAmtReceived" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event);" />		<!-- onchange="validateAmt(this.value,this.id);" -->
								</td>
								<td>
									<s:textfield name="totalProfFees" id="totalprofFees" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event);" onchange="validateAmt(this.value,this.id);" readonly="true" />
								</td>
								<td>
									<s:textfield name="totalOPE" id="totalOPE" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event);" onchange="validateAmt(this.value,this.id);" readonly="true" />
								</td>
								<td>
									<s:textfield name="totCGSTAmt" id="totCGSTAmt" cssStyle="width: 120px !important; text-align:right" onkeypress="return isNumberKey(event)" readonly="true" />
								</td>
								<td>
									<s:textfield name="totSGSTAmt" id="totSGSTAmt" cssStyle="width: 120px !important; text-align:right" onkeypress="return isNumberKey(event)" readonly="true" />
								</td>
								<td>
									<s:textfield name="totIGSTAmt" id="totIGSTAmt" cssStyle="width: 120px !important; text-align:right" onkeypress="return isNumberKey(event)" readonly="true" />
								</td>
								<td>
									<s:textfield name="strTDS" id="strTDS" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event);" onchange="validateAmt(this.value,this.id);" />
								</td>
								
							</tr>
						</table>
						
						<div style="width:90%; text-align:center">
							<%-- <s:submit value="Submit" cssClass="btn btn-primary" cssStyle="margin-top: 10px;" id="submit" name="submit" ></s:submit> --%>
							<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin-top: 10px;" onclick="addReceipt('A');"/>
						</div>
					</s:form>
				</div>
			</div>
		</div>
	</div>
</section>
</div>