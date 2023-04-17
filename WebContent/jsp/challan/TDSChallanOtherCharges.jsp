<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function calAmt(){
	//incomeTax underSection234 surcharge eduCess interestAmt penaltyAmt totalAmt 
	
	var incomeTax = document.getElementById("incomeTax").value;
	var underSection234=document.getElementById("underSection234").value;
	var surcharge=document.getElementById("surcharge").value;
	var eduCess=document.getElementById("eduCess").value;
	var interestAmt=document.getElementById("interestAmt").value;
	var penaltyAmt=document.getElementById("penaltyAmt").value;
	
	var totalAmt = 0;
	
	if(incomeTax != ''){
		totalAmt +=parseFloat(incomeTax);
		
		if(underSection234 != ''){
			totalAmt +=parseFloat(underSection234);
		}
		if(surcharge != ''){
			totalAmt +=parseFloat(surcharge);
		}
		if(eduCess != ''){
			totalAmt +=parseFloat(eduCess);
		}
		if(interestAmt != ''){
			totalAmt +=parseFloat(interestAmt);
		}
		if(penaltyAmt != ''){
			totalAmt +=parseFloat(penaltyAmt);
		}
	}	
	
	document.getElementById("totalAmt").value = totalAmt;
}


$("#formTDSUpdateChallanData").submit(function(e){ 
	e.preventDefault();
	var form_data = $("form[name='formTDSUpdateChallanData']").serialize();
   	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	$.ajax({
		url : "TDSUpdateChallanData.action",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		},
		error: function(result){
			$.ajax({
				url: 'TDSTaxChallan.action',
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

Map<String, String> hmTDSOtherCharge = (Map<String, String>)request.getAttribute("hmTDSOtherCharge");
if(hmTDSOtherCharge == null) hmTDSOtherCharge = new HashMap<String, String>();

double dblTotal = uF.parseToDouble(hmTDSOtherCharge.get("INCOME_TAX")) + uF.parseToDouble(hmTDSOtherCharge.get("UNDER_SECTION_234")) + uF.parseToDouble(hmTDSOtherCharge.get("SURCHARGE")) 
+ uF.parseToDouble(hmTDSOtherCharge.get("EDU_CESS")) + uF.parseToDouble(hmTDSOtherCharge.get("INTEREST_AMT")) 
+ uF.parseToDouble(hmTDSOtherCharge.get("PENALTY_AMT")); 
String strCurrency = (String) request.getAttribute("strCurrency"); 

%> 
	<s:form id="formTDSUpdateChallanData" action="TDSUpdateChallanData" method="post" name="formTDSUpdateChallanData" theme="simple">		
		<div style="float: center" id="tblDiv">
		<s:hidden name="challanDate"></s:hidden>
		<s:hidden name="operation" value="otherCharges"></s:hidden>
		<s:hidden name="f_org"></s:hidden>
			<table border="0" class="table table-bordered">
				<tr>
					<td><b>Challan printed on <%= uF.getDateFormat(challanDate, IConstants.DBDATE, CF.getStrReportDateFormat())%> for <%=uF.showData(strCurrency,"")%> <%=payAmount%>.</b></td>
				</tr>
			</table>
			
			<table border="0" class="table table-bordered">
				<tr>
					<td class="txtlabel alignRight">Income Tax:</td> 
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="incomeTax" name="strIncomeTax" readonly="readonly" value="<%=uF.showData(hmTDSOtherCharge.get("INCOME_TAX"),"0") %>" style="width: 100px;text-align:right;"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Fee under section 234 E:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="underSection234" name="underSection234" value="<%=uF.showData(hmTDSOtherCharge.get("UNDER_SECTION_234"),"0") %>" style="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Surcharge:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="surcharge" name="surcharge" value="<%=uF.showData(hmTDSOtherCharge.get("SURCHARGE"),"0") %>" style="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Education Cess:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="eduCess" name="eduCess" readonly="readonly" value="<%=uF.showData(hmTDSOtherCharge.get("EDU_CESS"),"0") %>" style="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Interest Amount:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="interestAmt" name="interestAmt" value="<%=uF.showData(hmTDSOtherCharge.get("INTEREST_AMT"),"0") %>" style="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Penalty Amount:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="penaltyAmt" name="penaltyAmt" value="<%=uF.showData(hmTDSOtherCharge.get("PENALTY_AMT"),"0") %>" style="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"/> </td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Total:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="totalAmt" name="totalAmt" value="<%=dblTotal %>" readonly="readonly" value="0" style="width: 100px;text-align:right;"/></td>
				</tr>	
			</table>
		</div>		
		<div class="clr"></div>
		<div style="margin: 0px 0px 0px 210px">
			<table border="0" class="table table-bordered">
				<tr><td class="txtlabel alignLeft"><s:submit value="Submit" cssClass="btn btn-primary" name="strSubmit"/></td>
				</tr>
			</table>
		</div>
	</s:form>

