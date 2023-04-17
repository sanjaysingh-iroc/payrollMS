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

	/* $(function() {
	    $("#invoiceGenDate").datepicker({format : 'dd/mm/yyyy'});
	    $("#p_id").multiselect().multiselectfilter();
	}); */
	
	$(document).ready(function() {
		$("#invoiceGenDate").datepicker({format : 'dd/mm/yyyy'});
	    $("#p_id").multiselect().multiselectfilter();
	    $(".validateRequired").prop('required',true);
	}); 
	
	function getCleintwiseProject(clientId){
		xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
	    } else {
			var xhr = $.ajax({
				url : "GetProjectListAjax.action?clientId="+clientId+"&fromPage=AID",
				cache : false,
				success : function(data) {
                	if(data == "") {
                	} else {
                		document.getElementById("clientProTd").innerHTML = data;
					}
                	$("#p_id").multiselect().multiselectfilter();
				}
			});
		}
	}
	
	function AddProjectAmount(){
		document.getElementById("proTable").style.display = "";
		
		var pIds = getSelectedValue("p_id");
		var proNameMap = getSelectedText("p_id");
		
		var tableId = document.getElementById("row_pro_table");
		var tableRowCnt = tableId.rows.length;
		//console.log(tableRowCnt); 
		for(var k=1; k<tableRowCnt; k++){
			  //document.getElementById("row_pro_table").deleteRow(k); 
			removeProjects("TR_"+k);
			
		}
		
		document.getElementById("totalOPE").value = "";
		document.getElementById("totalProfFees").value = "";
		document.getElementById("invoiceTotalAmt").value = "";
		var gst = $('#isDisplay').val();
		if(gst == '1'){
			document.getElementById("strCGSTAmt").value = "";
			document.getElementById("strSGSTAmt").value = "";
		} else if(gst == 2){
			document.getElementById("strIGSTAmt").value = "";
		}
		
		for(var i=0; i<pIds.length; i++){
			console.log(proNameMap.get(pIds[i]));
			var table = document.getElementById("row_pro_table");
			var rowCount = i+1;
			var row = table.insertRow(rowCount);
			
	        row.id = "TR_"+rowCount;
	        
	        var cell0 = row.insertCell(0);
	        //cell0.setAttribute("class", "txtlabel alignRight");
	        cell0.innerHTML ="<td class='txtlabel'><input type='hidden' name = 'proId' id='proId"+i+"' value='"+pIds[i]+"'></input>"+proNameMap.get(pIds[i])+"</td>";
	        var cell1 = row.insertCell(1); 
			/* cell1.innerHTML = "<td class='alignRight'> <input type='text' name='proFees' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" ></input></td>"; */
			cell1.innerHTML = "<td class='alignRight'> <input type='text' name='proFees' id='proFees"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" onchange=\"calTotal('proFees','"+pIds.length+"');\" ></input></td>";
			var cell2 = row.insertCell(2); 
			cell2.innerHTML = "<td class='alignRight'> <input type='text' name='strOPE' id='strOPE"+i+"' style = 'width:120px !important;text-align:right' onkeypress=\"return isNumberKey(event);\" onchange=\"calTotal('OPE','"+pIds.length+"');\" ></input></td>";
			
		}
		
	}
	
	
	function removeProjects(rowid) {
	    var table =  document.getElementById('row_pro_table');
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
	
	function makeZeroOnUncheck(strValue){
		var cgst = document.getElementById("strCGST");
		var sgst = document.getElementById("strSGST");
		var igst = document.getElementById("strIGST");
		
		if(strValue == "1"){
			document.getElementById("strIGST").value = "18";
			document.getElementById("strIGSTAmt").value = "";
			cgst.readOnly = false;
			sgst.readOnly = false;
			igst.readOnly = true;
			
		} else if(strValue == "2"){
			document.getElementById("strCGST").value = "9";
			document.getElementById("strSGST").value = "9";
			document.getElementById("strCGSTAmt").value = "";
			document.getElementById("strSGSTAmt").value = "";
			cgst.readOnly = true;
			sgst.readOnly = true;
			igst.readOnly = false;
			
		}
		
		calTotal('','');
		
	}
	
	function calGSTAmt(strVal,id){
		
		var totProfFees = $('#totalProfFees').val();
		var totOPE = $('#totalOPE').val();
		/* var totAmt = $('#invoiceTotalAmt').val(); */
		var totAmt = 0;
		/* if(totAmt=='') {
			totAmt = '0';
		} */
		if(totProfFees=='') {
			totProfFees = '0';
		}
		if(totOPE=='') {
			totOPE = '0';
		}
		if(strVal=='') {
			strVal = '0';
		}
		
		totAmt = parseFloat(totProfFees)+parseFloat(totOPE);
		
		var strTotalAmt = (parseFloat(totAmt)*parseFloat(strVal)) /100;
		document.getElementById(id).value = strTotalAmt;
		
		/* var display = $("#isDisplay").val(); */
		var display = $("input[name='isDisplay']:checked").val();
		
		if(display == '1'){
			var cGstAmt = $("#strCGSTAmt").val();
			var sGstAmt = $("#strSGSTAmt").val();
			if(cGstAmt == ''){
				cGstAmt = '0';
			}
			
			if(sGstAmt == ''){
				sGstAmt = '0';
			}
			
			totAmt = parseFloat(totAmt)+parseFloat(sGstAmt)+parseFloat(cGstAmt);
			
		} else if(display == '2'){
			var iGstAmt = $("#strIGSTAmt").val();
			if(iGstAmt == ''){
				iGstAmt = '0';
			}
			totAmt = parseFloat(totAmt)+parseFloat(iGstAmt);
		}
		document.getElementById("invoiceTotalAmt").value = parseFloat(totAmt).toFixed(2);
	}
	
	function addInvoice(operation) {
		
		
		var form_data = $("#formAddInvoicingDetails").serialize();
		$("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
	     	url: "AddInvoicingDetails.action",
	     	type: 'POST',
	     	data: form_data+'&operation='+operation,
	     	success: function(result){
	     		$("#divResult").html(result);
	     		$("#invoiceGenDate").datepicker({format : 'dd/mm/yyyy'});
	    	    $("#p_id").multiselect().multiselectfilter();
	     	}
	    });
		
	}
	
	
	function calTotal(type,size){
		
		if(size == ''){
			size = 0;
		}
		var strTotal = 0;
		
		var totalProfFeesAmt = $('#totalProfFees').val();
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
				var strOpe = $('#strOPE'+i).val();
				
				if((isNaN(parseFloat(strOpe)))) {
					strOpe = 0;
				}
				
				totalOPEAmt = parseFloat(totalOPEAmt)+parseFloat(strOpe);
			}
			
			document.getElementById("totalOPE").value = totalOPEAmt;
		
		} else if(type == "proFees"){
			
			totalProfFeesAmt = 0;
			
			for(var i =0; i<parseInt(size); i++){
				var strprofFees = $('#proFees'+i).val();
				
				if((isNaN(parseFloat(strprofFees)))) {
					strprofFees = 0;
				}
				
				totalProfFeesAmt = parseFloat(totalProfFeesAmt)+parseFloat(strprofFees);
			}
			
			document.getElementById("totalProfFees").value = totalProfFeesAmt;
		}
		
		strTotal = parseFloat(totalProfFeesAmt)+parseFloat(totalOPEAmt);
		/* document.getElementById("invoiceTotalAmt").value = strTotal; */
		
		/* var gst = $('#isDisplay').val(); */
		var gst = $("input[name='isDisplay']:checked").val();
		if(gst == '1'){
			var cGstPer = $('#strCGST').val();
			var sGstPer = $('#strSGST').val();
			var strCGstAmt = (parseFloat(strTotal)*parseFloat(cGstPer)) /100;
			var strSGstAmt = (parseFloat(strTotal)*parseFloat(sGstPer)) /100;
			
			strTotal = parseFloat(totalProfFeesAmt)+parseFloat(totalOPEAmt)+parseFloat(strCGstAmt)+parseFloat(strSGstAmt);
			
			document.getElementById("strCGSTAmt").value = strCGstAmt;
			document.getElementById("strSGSTAmt").value = strSGstAmt;
			
		}else if(gst == '2'){
			var iGstPer = $('#strIGST').val(); 
			var strIGstAmt = (parseFloat(strTotal)*parseFloat(iGstPer)) /100;
			
			strTotal = parseFloat(totalProfFeesAmt)+parseFloat(totalOPEAmt)+parseFloat(strIGstAmt);
			
			document.getElementById("strIGSTAmt").value = strIGstAmt; 
			
 		}
		
		document.getElementById("invoiceTotalAmt").value = strTotal.toFixed(2);
		
	}
	
</script>

<% UtilityFunctions uF = new UtilityFunctions(); %>

<section class="content">
	<div class="row jscroll">
		<div class="col-md-12">
			<div class="box box-primary">
				<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 550px;">
				<div><h4> Generate Invoice</h4></div>
				<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE),"") %>
					<s:form theme="simple" action="AddInvoicingDetails" name="frmAddInvoicingDetails" id="formAddInvoicingDetails" cssClass="formcss" >
					<div><h5 style="font-weight: bold;">Step 1: Select Project</h5></div>
						<table class="table table_no_border" style="margin-top: 10px; width:auto;">
							<tr>
								<th class="alignCenter">Client Name</th>
								<th class="alignCenter">Project</th>
							</tr>
							<tr>
								<td>
									<s:select label="Select Client" name="strClient" id="strClient" headerKey="" headerValue="Select Client" listKey="clientId" listValue="clientName" list="clientList" key="" onchange="getCleintwiseProject(this.value);" />		<!-- cssStyle="width: 120px !important; text-align:right;" -->
								</td>
								<td id="clientProTd">
									<s:select theme="simple" label="Select Project" name="p_id" id="p_id" listKey="projectID" listValue="projectName" list="projectdetailslist" key="" onchange="AddProjectAmount();" multiple="true" />		<!-- cssStyle="width: 120px !important; text-align:right;" -->
								</td>
							</tr>
						</table>
							
						<div><h5 style="font-weight: bold;">Step 2: Enter Invoice Details</h5></div>
						<div id="proTable" style="display:none">
							<table border="0" id="row_pro_table" class="table table_no_border" style="margin-top: 10px; width:50%;">
								<tr id="TR_0">
									<th>Project Name</th>
									<th>Prof Fees</th>
									<th>OPE</th>
								</tr>
							</table>
						</div>
						<div><h5 style="font-weight: bold;">Step 3: Invoice Summary</h5></div>
						<table class="table table_no_border" style="margin-top: 10px; width:93%;">
							<tr>
								<th class="alignCenter">Bill No.</th>
								<th class="alignCenter">Bill Date</th>
								<th class="alignCenter">Total Amount</th>
								<th class="alignCenter">Total Prof Fees</th>
								<th class="alignCenter">Total OPE</th>
								<th class="alignCenter">CGST</th>
								<th class="alignCenter">SGST</th>
								<th></th>
								<th class="alignCenter">IGST</th>
							</tr>
							
							<tr>
								<td> 
									<s:textfield name="invoiceNo" id="invoiceNo" cssClass="validateRequired" cssStyle="width: 120px !important;" />	<!-- onkeyup="checkInvoiceCode(this.value);" --> 
								</td>
								<td> 
									<s:textfield name="invoiceGenDate" id="invoiceGenDate" cssClass="validateRequired" cssStyle="width: 120px !important;"></s:textfield>  
								</td>
								<td>
									<s:textfield name="invoiceTotalAmt" id="invoiceTotalAmt" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event)" readonly="true" />
								</td>
								<td>
									<s:textfield name="totalProfFees" id="totalProfFees" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event)" readonly="true" />
								</td>
								<td>
									<s:textfield name="totalOPE" id="totalOPE" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event)" readonly="true" />
								</td>
								<td>
									<%-- <s:textfield name="strCGST" id="strCGST" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event)" /> --%>
									<div>
										<s:textfield name="strCGSTAmt" id="strCGSTAmt" cssStyle="width: 120px !important; text-align:right;" onkeypress="return isNumberKey(event)" readonly="true" />
									</div>
									<div style="margin-top: 10px;">
										<s:textfield name="strCGST" id="strCGST" cssStyle="width: 120px !important; text-align:right;" value="9" onkeypress="return isNumberKey(event)" onchange="calGSTAmt(this.value,'strCGSTAmt');" />
									</div>
								</td>
								<td>
									<%-- <s:textfield name="strSGST" id="strSGST" cssStyle="width: 120px !important; text-align:right" onkeypress="return isNumberKey(event)"  /> --%>
									<div>
										<s:textfield name="strSGSTAmt" id="strSGSTAmt" cssStyle="width: 120px !important; text-align:right" onkeypress="return isNumberKey(event)" readonly="true" />
									</div>
									<div style="margin-top: 10px;">
										<s:textfield name="strSGST" id="strSGST" cssStyle="width: 120px !important; text-align:right" value="9" onkeypress="return isNumberKey(event)" onchange="calGSTAmt(this.value,'strSGSTAmt');" />
									</div>
								</td>
								<td>
									<input type="radio" name="isDisplay" id="isDisplay" onclick="makeZeroOnUncheck(this.value)" value="1" checked="checked">
								</td>
								<td>
									<%-- <s:textfield name="strIGST" id="strIGST" cssStyle="width: 120px !important; text-align:right" onkeypress="return isNumberKey(event)" /> --%>
									<div>
										<s:textfield name="strIGSTAmt" id="strIGSTAmt" cssStyle="width: 120px !important; text-align:right" onkeypress="return isNumberKey(event)" readonly="true" />
									</div>
									<div style="margin-top: 10px;">
										<s:textfield name="strIGST" id="strIGST" cssStyle="width: 120px !important; text-align:right" value="18" onkeypress="return isNumberKey(event)" onchange="calGSTAmt(this.value,'strIGSTAmt');" readonly="true"/>
									</div>
								</td>
								<td>
									<input type="radio" name="isDisplay" id="isDisplay" onclick="makeZeroOnUncheck(this.value)" value="2" >
								</td>
							</tr>
						</table>
						
						<div style="width:90%; text-align:center">
							<%-- <s:submit value="Submit" cssClass="btn btn-primary" cssStyle="margin-top: 10px;" id="submit" name="Submit" ></s:submit> --%>
							<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin-top: 10px;" onclick="addInvoice('A');"/>
						</div>
						
					</s:form>
				</div>
			</div>
		</div>
	</div>
</section>
</div>	