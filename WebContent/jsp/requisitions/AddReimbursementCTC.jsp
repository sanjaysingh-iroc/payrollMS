<%@page import="java.util.LinkedHashMap"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
	UtilityFunctions uF =  new UtilityFunctions();
%>
 
<script type="text/javascript">
    
	function readFileURL(input, targetDiv) {
	    if (input.files && input.files[0]) {
	        var reader = new FileReader();
	        reader.onload = function (e) {
	            $('#'+targetDiv)
	                .attr('path', e.target.result);
	        };
	        reader.readAsDataURL(input.files[0]);
	    }
	}

    $("#frm_AddReimbursementCTC").submit(function(e){
		//alert("check ........");
    	var limitAmount = document.getElementById("limitAmount").value;
    	var strAmount = document.getElementById("strAmount").value;
    	var financialYear = document.getElementById("financialYear").value;
    	if(parseFloat(strAmount) == parseFloat('0')){
    		alert ("You can not apply 0");
    		document.getElementById("strAmount").value='';
    		return false;
    	} else if(parseFloat(strAmount) > parseFloat(limitAmount) || parseFloat(strAmount) == parseFloat('0')){
    		alert ("You can not apply for more than "+limitAmount);
    		document.getElementById("strAmount").value='';
    		return false;
    	} else {
			e.preventDefault();
			if($("#file").attr('path') !== undefined) {
	  		  var form_data = new FormData($(this)[0]);
	  		  form_data.append("strDocument", $("#file").attr('path'));
	  		  form_data.append("submit1", "Submit");
	  		  //alert("form_data ===>> " + form_data);
	  		  $("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	  		  $.ajax({
		      		url: "AddReimbursementCTC.action",
		      		type: 'POST',
		      		data: form_data,
		      		contentType: false,
		            cache: false,
		      		processData: false,
		 			success : function(res) {
		 				$("#subDivResult").html(res);
		 			},
		 			error : function(res) {
		 				$.ajax({
		 					url: 'ReimbursementCTC.action?financialYear='+financialYear,
		 					cache: true,
		 					success: function(result) {
		 						$("#subDivResult").html(result);
		 					}
		 				});
		 			}
		      	 });
		   	  } else { 
				var form_data = $("form[name='frm_AddReimbursementCTC']").serialize();
		  		form_data.append("submit1", "Submit");
		     	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		     	$.ajax({
		 			url : "AddReimbursementCTC.action",
		 			data: form_data,
		 			cache : false,
		 			success : function(res) {
		 				$("#subDivResult").html(res);
		 			},
		 			error : function(res) {
		 				$.ajax({
		 					url: 'ReimbursementCTC.action?financialYear='+financialYear,
		 					cache: true,
		 					success: function(result) {
		 						$("#subDivResult").html(result);
		 					}
		 				});
		 			}
		 		});
	    	} 
	     	
    	}
	});
    
    //jQuery(document).ready(function(){
    	$("#submitButton").click(function(){
    		$("#frm_AddReimbursementCTC").find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#frm_AddReimbursementCTC").find('.validateRequired').filter(':visible').prop('required',true);
        });
    	
    	 $("#paycycle").multiselect().multiselectfilter(); 
    //}); 
    
    function getReimbursementCTCHead() {
    	document.getElementById("strAmount").value='';
    	var financialYear = document.getElementById("financialYear").value;
    	if(financialYear != ''){
    		var action = 'GetReimbursementCTCHead.action?financialYear=' + financialYear;
    		getContent("tdReimCTCHead", action);
    		
    		window.setTimeout(function() {
    			getReimbursementCTCHeadPaycycle();
    		}, 700);
    	}
    }

    function getReimbursementCTCHeadPaycycle() {
    	document.getElementById("strAmount").value='';
    	var financialYear = document.getElementById("financialYear").value;
    	var reimbursementCTCHead = document.getElementById("reimbursementCTCHead").value;
    	if(financialYear != ''){
    		var action = 'GetReimbursementCTCHeadPaycycle.action?financialYear='+financialYear+'&reimbursementCTCHead='+reimbursementCTCHead;
    		getContent("tdPaycycle", action);
    		$("#paycycle").multiselect().multiselectfilter();
    	}
    }

    function checkReimbursementCTCHeadLimit(){
    	var limitAmount = document.getElementById("limitAmount").value;
    	var strAmount = document.getElementById("strAmount").value;
    	if(parseFloat(strAmount) == parseFloat('0')){
    		alert ("You can not apply 0");
    		document.getElementById("strAmount").value='';
    		return false;
    	} else if(parseFloat(strAmount) > parseFloat(limitAmount) || parseFloat(strAmount) == parseFloat('0')){
    		alert ("You can not apply for more than "+limitAmount);
    		document.getElementById("strAmount").value='';
    		return false;
    	}
    	return true;
    }
    
</script>

<div class="box-body">
	<s:form id="frm_AddReimbursementCTC" theme="simple" name="frm_AddReimbursementCTC" action="AddReimbursementCTC" enctype="multipart/form-data" method="post">
		<table class="table table_no_border form-table"> 
			<tr>
				<td class="alignRight">Financial Year:<sup>*</sup></td>
				<td><s:select theme="simple" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" cssClass="validateRequired"
						list="financialYearList" key="" onchange="
						getReimbursementCTCHead();"/>
				</td>
			</tr>
			
			<tr>
				<td class="alignRight" valign="top">CTC Head:<sup>*</sup></td>
				<td id="tdReimCTCHead">
					<s:select theme="simple" name="reimbursementCTCHead" id="reimbursementCTCHead" listKey="reimbursementCTCHeadId" 
						listValue="reimbursementCTCHeadName" list="reimbursementCTCHeadList" key="" cssClass="validateRequired" 
						onchange="getReimbursementCTCHeadPaycycle();"/>
				</td>
			</tr>
								
			<tr>
				<td class="alignRight" valign="top">Paycycle:<sup>*</sup></td>
				<td id="tdPaycycle"><s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" 
					list="paycycleList" key="" cssClass="validateRequired" multiple="true"/>
					<s:hidden name="limitAmount" id="limitAmount"></s:hidden>
				</td>
			</tr>
			
			<tr>
				<td class="alignRight" valign="top">Amount:<sup>*</sup></td>
				<td>
					<s:textfield name="strAmount" id="strAmount" cssClass="validateRequired" onkeyup="return checkReimbursementCTCHeadLimit();" 
					onkeypress="return isNumberKey(event)" cssStyle="width: 75px; text-align: right;"/>
				</td> 
			</tr>
			
			<tr>
				<td class="alignRight" valign="top">Description:</td>
				<td><s:textarea rows="5" cols="25" name="strDescription"></s:textarea></td>
			</tr>

			<tr>
				<td class="alignRight">Attach Document:<sup>*</sup></td>
				<td>
					<span id="file"></span>
					<s:file accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strDocument" cssStyle="float:left" cssClass="validateRequired" onchange="readFileURL(this, 'file');"/>
					<%-- <s:file name="strDocument" cssClass="validateRequired"/> --%>
				</td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" name="submit1" id="submitButton" value="Submit" class="btn btn-primary"/></td>
			</tr>
		</table>
	</s:form>
</div>
