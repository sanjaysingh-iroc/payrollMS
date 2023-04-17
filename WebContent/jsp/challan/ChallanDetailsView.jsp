<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">

function checkUncheckValue() {
	var allEmp=document.getElementById("allEmp");		
	var strEmpId = document.getElementsByName('empIds');

	if(allEmp.checked==true){
		 for(var i=0;i<strEmpId.length;i++){
			 strEmpId[i].checked = true;
			  
		 }
	}else{		
		 for(var i=0;i<strEmpId.length;i++){
			 strEmpId[i].checked = false;			 
		 }
	}
	
	/* calAmt();   */
}

$("#formUpdateChallanData").submit(function(e){
		e.preventDefault();
		var form_data = $("form[name='formUpdateChallanData']").serialize();
	   	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	$.ajax({
	   		type: 'POST',
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
/* 
function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function calAmt(){
	//amtTax interestAmt penaltyAmt compositionMoney fineAmt feesAmt advanceAmt totalAmt 
	
	var empIds = document.getElementsByName('empIds');
	var amtTax = 0;
	for(var i=0; i < empIds.length; i++){
		//alert(empIds[i].checked);
		if(empIds[i].checked){
			//alert(empIds[i].value);
			var amt = document.getElementById("empAmount_"+empIds[i].value).value;
			if(amt != ''){
				amtTax +=parseFloat(amt);
			} 
		}
	}
	
	document.getElementById("amtTax").value = amtTax;
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
 */
 
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map hmEmpName = (Map) request.getAttribute("hmEmpName");
	Map<String, Map<String, String>> hmMap = (Map<String, Map<String, String>>) request.getAttribute("hmMap");
	List<String> empList = (List<String>) request.getAttribute("empList");
	String totalMonths = (String) request.getAttribute("months");
	String financialYear = (String) request.getAttribute("financialYear");
	String orgId = (String) request.getAttribute("f_org");
	//String locationId = (String) request.getAttribute("f_strWLocation");
	String state = (String) request.getAttribute("state");
%>

	<s:form name="formUpdateChallanData" id="formUpdateChallanData" action="UpdateChallanData" method="post" theme="simple">
		<input type="hidden" name="financialYear" value="<%=financialYear%>"/>
		<input type="hidden" name="totalMonths" value="<%=totalMonths%>"/>
		<input type="hidden" name="operation" value="insert"/>
		<input type="hidden" name="orgid" value="<%=orgId%>"/>
		<input type="hidden" name="state" value="<%=state %>"/>
		
		<div style="float: center" id="tblDiv">
			<table class="table table_no_border">
				<tr>
					<td><strong>Print the challan</strong></td>
				</tr>
			</table>
			
			<table border="0" class="table table-bordered">
				<tr>
					<th><input onclick="checkUncheckValue();" type="checkbox" name="allEmp" id="allEmp"/>Name</th>
					<th>Amount</th>
				</tr>
				<%
				int count = 0;
				double dblAmtTax = 0.0d;
				if (empList != null) {
					
					for (int i = 0; i < empList.size(); i++) {
						count++;
						Map<String, String> hmInner = hmMap.get(empList.get(i));
						dblAmtTax += uF.parseToDouble(hmInner.get("AMOUNT"));
				%>
				<tr>
 					<td class="txtlabel alignLeft"><input type="checkbox" name="empIds" value="<%=empList.get(i)%>"/><%=uF.showData((String) hmEmpName.get(hmInner.get("EMP_ID")),"")%></td> 

					<td class="txtlabel alignRight"><input type="hidden" name="empAmount_<%=empList.get(i)%>" id="empAmount_<%=empList.get(i)%>" value="<%=hmInner.get("AMOUNT") %>"/><%=hmInner.get("AMOUNT")%></td>
				</tr>
				<%
					}
				}
				%>
				<% if (count == 0) { %>
				<tr>
					<td colspan="4"><div class="msg nodata"><span>No more challan available.</span></div></td>
				</tr>
				<% } %>
			</table>
			
			<%-- <table cellpadding="0" cellspacing="0" width="43%" style="float: right;">
			
				
				<%
					if(count > 0){
				%>
				<tr>
					<td class="txtlabel alignRight">Amount of Tax:</td> 
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="amtTax" name="amtTax" readonly="readonly" value="0" style="width: 100px;text-align:right;"/> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Interest Amount:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><s:textfield id="interestAmt" name="interestAmt" value="0" cssStyle="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"></s:textfield> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Penalty Amount:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><s:textfield id="penaltyAmt" name="penaltyAmt" value="0" cssStyle="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"></s:textfield> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Composition Money:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><s:textfield id="compositionMoney" name="compositionMoney" value="0" cssStyle="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"></s:textfield> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Fine:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><s:textfield id="fineAmt" name="fineAmt" value="0" cssStyle="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"></s:textfield> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Fees:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><s:textfield id="feesAmt" name="feesAmt" value="0" cssStyle="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"></s:textfield> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Advance Payment:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><s:textfield id="advanceAmt" name="advanceAmt" value="0" cssStyle="width: 100px;text-align:right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)"></s:textfield> </td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Total:</td>
					<td class="txtlabel alignLeft" style="width: 41%;"><input type="text" id="totalAmt" name="totalAmt" value="0" readonly="readonly" value="0" style="width: 100px;text-align:right;"/></td>
				</tr>	
				<%}
				%>
			</table> --%>
		</div>
		
		<div class="clr"></div>
		<div style="margin: 0px 0px 0px 210px">
		<% if(count > 0) { %>
			<table class="formcss">
				<tr><td class="txtlabel alignLeft"><s:submit value="Generate Challan" cssClass="btn btn-primary" name="strSubmit"></s:submit></td>
				<td></td>
				</tr>
			</table>
			<% } %>
		</div>
	</s:form>

