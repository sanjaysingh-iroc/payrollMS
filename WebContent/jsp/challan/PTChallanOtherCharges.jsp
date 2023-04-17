<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">

$(function() {
	$("#paidDate").datepicker({
		dateFormat : 'dd/mm/yy'
	});
});


function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function calAmt(){
	//amtTax interestAmt penaltyAmt compositionMoney fineAmt feesAmt advanceAmt totalAmt 
	
	var amtTax = document.getElementById("amtTax").value;
	var interestAmt=document.getElementById("interestAmt").value;
	var penaltyAmt=document.getElementById("penaltyAmt").value;
	var compositionMoney=document.getElementById("compositionMoney").value;
	var fineAmt=document.getElementById("fineAmt").value;
	var feesAmt=document.getElementById("feesAmt").value;
	var advanceAmt=document.getElementById("advanceAmt").value;
	
	var totalAmt = 0;
	
	if(amtTax != ''){
		totalAmt +=parseFloat(amtTax);
		
		if(interestAmt != ''){
			totalAmt +=parseFloat(interestAmt);
		}
		if(penaltyAmt != ''){
			totalAmt +=parseFloat(penaltyAmt);
		}
		if(compositionMoney != ''){
			totalAmt +=parseFloat(compositionMoney);
		}
		if(fineAmt != ''){
			totalAmt +=parseFloat(fineAmt);
		}
		if(feesAmt != ''){
			totalAmt +=parseFloat(feesAmt);
		}
		if(advanceAmt != ''){
			totalAmt +=parseFloat(advanceAmt);
		}
	}	
	
	document.getElementById("totalAmt").value = totalAmt;
}


	$("#formUpdateChallanData").click(function(e){
		e.preventDefault();
		var form_data = $("form[name='formUpdateChallanData']").serialize();
	   	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	$.ajax({
			url : "UpdateChallanData.action",
			data: form_data,
			cache : false,
			success : function(res) {
				$("#divResult").html(res);
			},
			error: function(result){
				$.ajax({
					url: 'Form5PTChallan.action',
					cache: true,
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
			}
		});
	});
	
</script>


<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();

String payAmount=(String)request.getAttribute("payAmount") ;
String challanDate=(String)request.getAttribute("challanDate");
String orgId = (String) request.getAttribute("f_org");
//String locationId = (String) request.getAttribute("f_strWLocation");
String state = (String) request.getAttribute("state");

Map<String, String> hmPTOtherCharge = (Map<String, String>)request.getAttribute("hmPTOtherCharge");
if(hmPTOtherCharge == null) hmPTOtherCharge = new HashMap<String, String>();

double dblTotal = uF.parseToDouble(payAmount) + uF.parseToDouble(hmPTOtherCharge.get("INTEREST_AMT")) + uF.parseToDouble(hmPTOtherCharge.get("PENALTY_AMT")) 
+ uF.parseToDouble(hmPTOtherCharge.get("COMPOSITION_MONEY")) + uF.parseToDouble(hmPTOtherCharge.get("FINE_AMT")) 
+ uF.parseToDouble(hmPTOtherCharge.get("FEES_AMT")) + uF.parseToDouble(hmPTOtherCharge.get("ADVANCE_AMT")); 
String strCurrency = (String) request.getAttribute("strCurrency"); 

%> 
	<s:form name="formUpdateChallanData" id="formUpdateChallanData" action="UpdateChallanData" method="post" cssClass="formcss" theme="simple">		
		<div style="float: center" id="tblDiv">
			<s:hidden name="challanDate"></s:hidden>
			<s:hidden name="operation" value="otherCharges"></s:hidden>
			<input type="hidden" name="orgid" value="<%=orgId %>"/>
			<input type="hidden" name="state" value="<%=state %>"/>
			<table class="table table_no_border form-table">
				<tr>
					<td><b>Challan printed on <%= uF.getDateFormat(challanDate, IConstants.DBDATE, CF.getStrReportDateFormat())%> for <%=uF.showData(strCurrency,"")%> <%=payAmount%>.</b></td>
				</tr>
			</table>
			
			<table class="table table_no_border form-table">
				<tr>
					<td class="txtlabel alignRight">Amount of Tax:</td> 
					<td class="txtlabel alignLeft" ><input type="text" id="amtTax" name="amtTax" readonly="readonly" value="<%=payAmount%>" style="width: 100px !important;text-align:right;"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Interest Amount:</td>
					<td class="txtlabel alignLeft"><input type="text" id="interestAmt" name="interestAmt" value="<%=uF.showData(hmPTOtherCharge.get("INTEREST_AMT"),"0") %>" style="width: 100px !important; text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Penalty Amount:</td>
					<td class="txtlabel alignLeft"><input type="text" id="penaltyAmt" name="penaltyAmt" value="<%=uF.showData(hmPTOtherCharge.get("PENALTY_AMT"),"0") %>" style="width: 100px !important;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Composition Money:</td>
					<td class="txtlabel alignLeft"><input type="text" id="compositionMoney" name="compositionMoney" value="<%=uF.showData(hmPTOtherCharge.get("COMPOSITION_MONEY"),"0") %>" style="width: 100px !important;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Fine:</td>
					<td class="txtlabel alignLeft"><input type="text" id="fineAmt" name="fineAmt" value="<%=uF.showData(hmPTOtherCharge.get("FINE_AMT"),"0") %>" style="width: 100px !important;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Fees:</td>
					<td class="txtlabel alignLeft"><input type="text" id="feesAmt" name="feesAmt" value="<%=uF.showData(hmPTOtherCharge.get("FEES_AMT"),"0") %>" style="width: 100px !important;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Advance Payment:</td>
					<td class="txtlabel alignLeft"><input type="text" id="advanceAmt" name="advanceAmt" value="<%=uF.showData(hmPTOtherCharge.get("ADVANCE_AMT"),"0") %>" style="width: 100px !important;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Total:</td>
					<td class="txtlabel alignLeft"><input type="text" id="totalAmt" name="totalAmt" value="<%=dblTotal %>" readonly="readonly" value="0" style="width: 100px !important;text-align:right;"/></td>
				</tr>	
			</table>
		</div>		
		<div class="clr"></div>
		<div style="margin: 0px 0px 0px 210px">
			<table class="table table_no_border form-table">
				<tr>
					<td class="txtlabel alignLeft"><s:submit value="Submit" cssClass="btn btn-primary" name="strSubmit" /></td>
				</tr>
			</table>
		</div>
	</s:form>

