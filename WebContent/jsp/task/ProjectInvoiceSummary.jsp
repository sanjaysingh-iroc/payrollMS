<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
List alOuter = (List)request.getAttribute("alOuter");
List alProfitSummary = (List)request.getAttribute("alProfitSummary");
%>


<script>

var div = document.getElementById("invoiceSummaryid");
var divs = document.getElementsByName("frm");
if(divs.length>1){
	div.parentNode.removeChild(div);
}


function calculate(){
	
	var modTaskIdElements = document.getElementsByName("taskId");
	var modActualTimeElements = document.getElementsByName("modifiedActualTime");
	var modBillableRateElements = document.getElementsByName("billableRate");
	var modBillableAmountElements = document.getElementsByName("billableAmount");
	var modBillableAmount1Elements = document.getElementsByName("billableAmount1");
	
	var totalBudgeted = document.getElementById("totalBudgeted");
	
	var modLength = modTaskIdElements.length;
	
	var actualTimeTotal = 0;
	var billableAmountTotal = 0;
	for(var i=0;i<modLength;i++){
		var billableAmount = parseFloat(modActualTimeElements[i].value) * parseFloat(modBillableRateElements[i].innerHTML);
		modBillableAmountElements[i].innerHTML = billableAmount.toFixed(2);
		modBillableAmount1Elements[i].value = billableAmount.toFixed(2);
		billableAmountTotal += billableAmount;
		actualTimeTotal += parseFloat(modActualTimeElements[i].value);
		
	}
	
	var totalProfit = billableAmountTotal - parseFloat(totalBudgeted.innerHTML);
	var totalProfitPer = 0;
	if(parseFloat(totalBudgeted.innerHTML)>0){
		totalProfitPer = (totalProfit / parseFloat(totalBudgeted.innerHTML))*100;	
	}
	
	document.getElementsByName("modifiedActualTimeTotal")[0].value = actualTimeTotal.toFixed(2);
	document.getElementsByName("modifiedBillableAmountTotal")[0].innerHTML = billableAmountTotal.toFixed(2);
	
	
	
	document.getElementById("profitPer").innerHTML = totalProfitPer.toFixed(2)+"%";
	document.getElementById("profitAmt").innerHTML = totalProfit.toFixed(2);
	
	
}

</script>



<div  id="invoiceSummaryid" class="leftbox reportWidth">
<h3>Budgeted Summary for <%=((request.getAttribute("PROJECT_NAME")!=null)?request.getAttribute("PROJECT_NAME"):"") %></h3>

<s:form theme="simple" action="ProjectInvoiceSummary" name="frm">
<s:hidden name="pro_id"></s:hidden>
<s:hidden name="paycycle"></s:hidden>


<table class="tb_style">

<tr>
	<th>Task Name</th>
	<th>Resource Name</th>
	<th>Service</th>
	<th>Estimated Time<br/>(hrs)</th>
	<th>Actual Time<br/>(hrs)</th>
	<th>Cost/Hr<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
	<th>Budgeted Cost<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
	<th>Billable Rate<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
	<th>Billable Cost<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
</tr>


<%for(int i=0; i<alOuter.size(); i++){ 
	List alInner = (List)alOuter.get(i);
%>

<%if(i==alOuter.size()-1){%>
<tr style="border-top:2px solid #000fff">
<%}else{ %>
<tr>
<%} %>

	<td><%=alInner.get(0) %></td>
	<td><%=alInner.get(1) %></td>
	<td><%=alInner.get(2) %></td>
	<td class="alignRight padRight20"><%=alInner.get(3) %></td>
	<td class="alignRight padRight20"><%=alInner.get(4) %></td>
	<td class="alignRight padRight20" style="background-color: lightgreen"><%=alInner.get(5) %></td>
	<td class="alignRight padRight20" style="background-color: lightgreen"><%=alInner.get(6) %></td>
	<td class="alignRight padRight20" style="background-color: cyan"><%=alInner.get(7) %></td>
	<td class="alignRight padRight20" style="background-color: cyan"><%=alInner.get(8) %></td>
</tr>
<%} %>

<tr><td colspan="9" align="center">

<%-- <s:submit value="Save As Draft" name="saveDraft" cssClass="input_button"></s:submit> --%>
<s:submit value="Save"  name="save" onclick="return confirm('Are you sure you wish to proceed with the existing invoice. You will not be able to modify the invoice once it is saved. Click OK to continue else Cancel to stay on the same invoice.')" cssClass="input_button"></s:submit> 

</td></tr>

</table>
</s:form>

<br><br>

<h3>Project Projection</h3>
<table class="tb_style" style="width:300px">
	<tr>
		<th>Expected Gross Profit</th>
		<td class="alignRight padRight20" id="profitAmt"><%=(String)alProfitSummary.get(0)%></td>
	</tr>
	<tr>
		<th>Gross Profit Margin</th>
		<td class="alignRight padRight20" id="profitPer"><%=(String)alProfitSummary.get(1)%></td>
	</tr>
</table>


</div>


		
    

<script>
calculate();
</script>


