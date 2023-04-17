<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 

<%
	UtilityFunctions uF = new UtilityFunctions();
	java.util.List couterlist = (java.util.List)request.getAttribute("reportList");
	if(couterlist == null) couterlist = new java.util.ArrayList();
	
	List<String> alOldGradeSalaryHeadId = (List<String>)request.getAttribute("alOldGradeSalaryHeadId");
	if(alOldGradeSalaryHeadId == null) alOldGradeSalaryHeadId = new ArrayList<String>();
%>


<style>

.crdb_details
{
  width:97%;
  border:#a4a4a4 solid 1px;
  height:auto;
  float:left;
  margin:10px;
  padding:10px 10px 10px 10px;
  -moz-border-radius:5px;
  -webkit-border-radius:5px;
   border-radius:5px;
}

.credit
{
  width:50%;
  height:auto;
  float:left;
  border-right:#E6E6E6 solid 1px;
  margin:0px 5px 0px 0px;
  padding:5px 10px 5px 5px;
 
}

.deduction
{
width:47%;
  height:auto;
  float:left;
 /* border:#FF0000 solid 1px;*/
  padding:5px;
  
}

.heading1 {
    background-color: #ECF6FF;
    background-position: 10px 6px;
    background-repeat: no-repeat;
    cursor: pointer;
    text-shadow: 0 1px 0 #FFFFFF;
    padding-top: 5px; 
}

.heading1 h3
{
	margin: 0 0 10px;
	padding:0px 0px 10px 0px;
    border-bottom: 2px solid #EDEBEF;
    color: #686868;
    font-family: verdana,arial,sans-serif;
    font-size: 18px;
	text-align:center;
}

.details_lables
{
 width:auto;
 height:auto;
/* border:#0000CC solid 1px;*/
 float:left;
}

.col1
{
   width:150px; 
 /* border:#00CC33 solid 1px;*/ 
  float:left;
  text-align:right;
}

.col2
{
float:left;
/*text-align:left;*/
width:auto;
margin:0px 0px 0px 10px;

}

.buttons
{
  float:left;
  width:310px;
  text-align:center;
 /* border:#FF9900 solid 1px;*/
  margin:10px 0px 10px 0px;
}

h4
{
 margin:0px;
 color:#666666;
}

.netvalue
{
  float:right;
  margin:10px 5px 10px 5px;
  font-size:22px;
}

.tdDashLabel {
    color: #298CE9;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 12px;
    padding: 3px;
}

.tdDashLabel_net
{
   color: #298CE9;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 18px;
    padding: 3px;
}

#popup_deduction label {
    color: #FFFFFF;
    text-align: left;
}







</style>

<script type="text/javascript">
	
	var gross_lbl_values = new Array();
	var deduction_lbl_values = new Array();
	var expected_deduction = 0;
	
	function isNumberKey(evt)
	{
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	
	function checkSalaryHeadDisable(){
		changeLabelValuesE('1', '');
	}
	
	var roundOffCondition='<%=(String)request.getAttribute("roundOffCondition") %>';
	
   	
	function changeLabelValuesE(id, type) {
		if(id == '301' && (type=='' || type!='MZ')) {
			var empId = document.getElementById("empId").value;
			var CCID = document.getElementById("CCID").value;
			var step = document.getElementById("step").value;
			var ctcAmt = document.getElementById("301").value;
			getContent('salDiv','EmployeeSalaryDetails.action?mode=E&empId='+empId+'&CCID='+CCID+'&step='+step+'&ctcAmt='+ctcAmt);
		} else {
	   		var disableSalaryStructure = document.getElementById("disableSalaryStructure");		
			if(disableSalaryStructure.checked == false) {
		   		var reimbursementCTC = document.getElementById("reimbursementCTC").value;
		   		var reimbursementCTCOptional = document.getElementById("reimbursementCTCOptional").value;
		   		var salHeadsAndAmt = ",";
		   		<%
		   		List<String> alContributeHeads = new ArrayList<String>();
				StringBuilder sbContributeHeads = new StringBuilder();
				for (int i=0; i<couterlist.size(); i++) {
					List<String> cinnerlist = (List<String>)couterlist.get(i); 
					if(cinnerlist.get(3)!=null && (cinnerlist.get(3)).trim().equals("P") && cinnerlist.get(7) != null && !cinnerlist.get(7).trim().equals("") 
							&& !cinnerlist.get(7).trim().equalsIgnoreCase("NULL") && cinnerlist.get(7).trim().length() > 0){
						List<String> al = Arrays.asList(cinnerlist.get(7).trim().split(","));
						int nAl = al!=null ? al.size() : 0;
				%>
						var formula = "";
						var cnt = 0;
						var isReimbursementCTC = new Boolean(false);
				<%		for(int j = 0; j < nAl; j++){
							String str = al.get(j);
							if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")){
								boolean isInteger = uF.isInteger(str.trim());
								if(isInteger) {
									if((!alContributeHeads.contains(IConstants.EMPLOYER_EPF+"") && uF.parseToInt(str) == IConstants.EMPLOYER_EPF) 
										|| (!alContributeHeads.contains(IConstants.EMPLOYER_ESI+"") && uF.parseToInt(str) == IConstants.EMPLOYER_ESI) 
										|| (!alContributeHeads.contains(IConstants.EMPLOYER_LWF+"") && uF.parseToInt(str) == IConstants.EMPLOYER_LWF)) {
										alContributeHeads.add(str);
										sbContributeHeads.append(str+",");
										//System.out.println("str in if ===>> " + str);
									}
									if(uF.parseToInt(str.trim()) == IConstants.REIMBURSEMENT_CTC) {
				%>
										formula += ""+(parseFloat(getRoundOffValue(reimbursementCTC)) + parseFloat(getRoundOffValue(reimbursementCTCOptional)));
										cnt++;
										isReimbursementCTC = true;
							<%	} else { %>
										var sHeadDisplay = "isDisplay_"+<%=str.trim()%>;
										if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
											var sHeadId = ""+<%=str.trim()%>;
											var sAnnualHeadId = "annual_"+<%=str.trim()%>;
											if(document.getElementById(sAnnualHeadId)){
												var frmlSalHeadAmt = document.getElementById(sAnnualHeadId).value;
												var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
												if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
													frmlSalHeadAmt = maxCapAmt;
												}
												formula += ""+ parseFloat(getRoundOffValue(frmlSalHeadAmt));
												cnt++;
											} else if(document.getElementById(sHeadId)){
												var frmlSalHeadAmt = document.getElementById(sHeadId).value;
												var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
												if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
													frmlSalHeadAmt = maxCapAmt;
												}
												formula += ""+parseFloat(getRoundOffValue(frmlSalHeadAmt));
												cnt++;
											}
										} else {
											formula += ""+parseFloat(getRoundOffValue('0'));
											cnt++;
										}
				<%					}
								} else {
				%>			
									formula += '<%=str.trim() %>'; 	
				<%				}
							}
						}
					%>
						var total = 0;
						if(cnt > 0 && formula.trim() != '') {
							var formulaCal = eval(formula);
							var percentage = '<%=cinnerlist.get(6) %>';
							var maxCapAmt = '<%=cinnerlist.get(9) %>';
							total = (parseFloat(percentage) * parseFloat(formulaCal))/100;
							total = isNaN(total) ? 0 : total;
							if(parseFloat(total) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								total = maxCapAmt;
							}
							if(parseFloat(total) <0) {
								total=0;
							}
							/* if(isReimbursementCTC){
								total += parseFloat(getRoundOffValue(reimbursementCTCOptional));
							} */
						}
						document.getElementById(""+<%=cinnerlist.get(0)%>).value = getRoundOffValue(total);
						document.getElementById("tempValue_"+<%=cinnerlist.get(0)%>).value = getRoundOffValue(total);
					<%}%>		
				<%} 
				
				for (int a=0; a<couterlist.size(); a++) {
					List<String> cinnerlist = (List<String>)couterlist.get(a);
				%>
					var salHd = '<%=cinnerlist.get(1)%>';
					var salHdAmt = document.getElementById(""+<%=cinnerlist.get(0)%>).value;
					salHeadsAndAmt = salHeadsAndAmt+salHd+'::::'+salHdAmt+',';
				<%
					}
				if(couterlist == null || couterlist.size() == 0) { %>
					salHeadsAndAmt = "";
				<% } %>
				
				<% 
				//System.out.println("sbContributeHeads ===>> " + sbContributeHeads.toString());
				if (sbContributeHeads.toString().length()>0) { %>
					if(salHeadsAndAmt != '') {
						var empId = document.getElementById("empId").value;
						//var effectiveDate = document.getElementById("idEffectiveDate").value;
						var contributeHeads = '<%=sbContributeHeads.toString() %>';
						var action = 'GetCalculatedContributions.action?salHeadsAndAmt='+salHeadsAndAmt+'&contributeHeads='+contributeHeads
							+'&empId='+empId; //+'&effectiveDate='+effectiveDate
						var xmlhttp = GetXmlHttpObject();
						if (xmlhttp == null) {
							alert("Browser does not support HTTP Request");
							return;
						} else {
							var xhr = $.ajax({
								url : action,
								cache : false,
								success : function(data) {
									//alert("data ===>> " + data);
									if(data.trim() != "") {
										var allData = data.trim().split("::::");
										changeLabelValuesEWithMultiCalcu(allData[0], allData[1], allData[2]);
									}
								}
							});
						}
						
					}
				<% } %>
			}
			//alert("1111111");
			calculateTotalEarningandDeduction();
			//changeLabelValuesEWithMultiCalcu('1');
		}
	}
	
	
	
	function changeLabelValuesEWithMultiCalcu(dblERPF, dblERESI, dblERLWF) {
		var disableSalaryStructure = document.getElementById("disableSalaryStructure");
		if(disableSalaryStructure.checked == false) {
			var reimbursementCTC = document.getElementById("reimbursementCTC").value;	
			var reimbursementCTCOptional = document.getElementById("reimbursementCTCOptional").value;
			<%  
			
			for (int i=0; i<couterlist.size(); i++) {
				List<String> cinnerlist = (List<String>)couterlist.get(i); 
				if(cinnerlist.get(3)!=null && (cinnerlist.get(3)).trim().equals("P") && cinnerlist.get(7) != null && !cinnerlist.get(7).trim().equals("") 
					&& !cinnerlist.get(7).trim().equalsIgnoreCase("NULL") && cinnerlist.get(7).trim().length() > 0) {
					List<String> al = Arrays.asList(cinnerlist.get(7).trim().split(","));
					int nAl = al != null ? al.size() : 0;
			%>	
					var formula = "";
					var cnt = 0;
				<%		
					if(al != null && (al.contains(IConstants.EMPLOYER_EPF+"") || al.contains(IConstants.EMPLOYER_ESI+"") || al.contains(IConstants.EMPLOYER_LWF+""))) {
						for(int j = 0; j < nAl; j++) {
							String str = al.get(j);
							if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")) {
								boolean isInteger = uF.isInteger(str.trim());
								if(isInteger) {
									if((!alContributeHeads.contains(IConstants.EMPLOYER_EPF+"") && uF.parseToInt(str) == IConstants.EMPLOYER_EPF) 
										|| (!alContributeHeads.contains(IConstants.EMPLOYER_ESI+"") && uF.parseToInt(str) == IConstants.EMPLOYER_ESI) 
										|| (!alContributeHeads.contains(IConstants.EMPLOYER_LWF+"") && uF.parseToInt(str) == IConstants.EMPLOYER_LWF)) {
										alContributeHeads.add(str);
										sbContributeHeads.append(str+",");
										//System.out.println("str in if ===>> " + str);
									}
								if(uF.parseToInt(str.trim()) == IConstants.EMPLOYER_EPF) {
					%>
										formula += ""+parseFloat(getRoundOffValue(dblERPF));
										cnt++;
							<%	} else if(uF.parseToInt(str.trim()) == IConstants.EMPLOYER_ESI) { %>
										formula += ""+parseFloat(getRoundOffValue(dblERESI));
										cnt++;
							<%	} else if(uF.parseToInt(str.trim()) == IConstants.EMPLOYER_LWF) { %>
										formula += ""+parseFloat(getRoundOffValue(dblERLWF));
										cnt++;
							<%	} else { %>
										var sHeadDisplay = "isDisplay_"+<%=str.trim()%>;
										if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
											var sHeadId = ""+<%=str.trim()%>;
											var sAnnualHeadId = "annual_"+<%=str.trim()%>;
											if(document.getElementById(sAnnualHeadId)){
												var frmlSalHeadAmt = document.getElementById(sAnnualHeadId).value;
												var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
												if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
													frmlSalHeadAmt = maxCapAmt;
												}
												formula += ""+ parseFloat(getRoundOffValue(frmlSalHeadAmt));
												cnt++;
											} else if(document.getElementById(sHeadId)){
												var frmlSalHeadAmt = document.getElementById(sHeadId).value;
												var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
												if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
													frmlSalHeadAmt = maxCapAmt;
												}
												formula += ""+parseFloat(getRoundOffValue(frmlSalHeadAmt));
												cnt++;
											}
										} else {
											formula += ""+parseFloat(getRoundOffValue('0'));
											cnt++;
										}
							<%		}
								} else {
							%>
									formula += '<%=str.trim() %>'; 	
						<%		}
							}
						}
					}
				%>
					var total = 0;
					if(cnt > 0 && formula.trim() != '') {
						var formulaCal = eval(formula);
						var percentage = '<%=cinnerlist.get(6) %>';
						var maxCapAmt = '<%=cinnerlist.get(9) %>';
						total = (parseFloat(percentage) * parseFloat(formulaCal))/100;
						total = isNaN(total) ? 0 : total;
						if(parseFloat(total) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
							total = maxCapAmt;
						}
						if(parseFloat(total) <0) {
							total=0;
						}
						document.getElementById(""+<%=cinnerlist.get(0)%>).value = getRoundOffValue(total);
						document.getElementById("tempValue_"+<%=cinnerlist.get(0)%>).value = getRoundOffValue(total);
					}
					
				<% } %>
			<% } %>
		}
		calculateTotalEarningandDeduction();
	}
	
	
   	

	function calculateTotalEarningandDeduction(){
		var total = 0;
		var totalD = 0;
	    <%  
			for (int j=0; j<couterlist.size(); j++) {
				java.util.List innerlist = (java.util.List)couterlist.get(j); 
				
				if(uF.parseToInt(""+innerlist.get(0)) == IConstants.CTC){
					continue;
				}
		%>
				var sSalED = ""+'<%=innerlist.get(2)%>';
				var isContribution = ""+'<%=innerlist.get(10)%>';
				if(sSalED == 'E'){
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(0)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
						var sHeadId = ""+<%=innerlist.get(0)%>;
						if(document.getElementById(sHeadId) && isContribution == 'F'){
							var frmlSalHeadAmt = document.getElementById(sHeadId).value;
							var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							total =  parseFloat(total) + parseFloat(getRoundOffValue(frmlSalHeadAmt));
							//total =  parseFloat(total) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
						}
					}
				} else if(sSalED == 'D'){
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(0)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
						var sHeadId = ""+<%=innerlist.get(0)%>;
						if(document.getElementById(sHeadId)){
							var frmlSalHeadAmt = document.getElementById(sHeadId).value;
							var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(frmlSalHeadAmt));
							//totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
						}
					}
				}
		<%}%>
		document.getElementById("total_earning_value").innerHTML = getRoundOffValue(Math.round(parseFloat(total)));
		document.getElementById("total_deduction_value").innerHTML = getRoundOffValue(Math.round(parseFloat(totalD)));
	}
	
	
   	
	function checkCal(){
		calculateTotalEarningandDeduction();
		
		if(confirm('Are you sure, you want to update this salary structure?')){
		   return true;
		}
		return false;
	}
	
	
   	
	function saveAll() {
		var sample_salary_value = document.getElementById("sample_salary").value;
		var total_earning = document.getElementById("total_earning_value").innerHTML;
		var total_deduction = document.getElementById("total_deduction_value").innerHTML;
		var testValue = 0;
		testValue = parseInt(total_earning) - parseInt(total_deduction);
		if(testValue == sample_salary_value) {
			alert('Saving..');
			return true;
		} else {
			alert('Net Amount is not equal to sample Ammount Entered..');
			return false;
		}
	}
	
	
	function removeField(id1) {
		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
			window.location="EmployeeSalaryDetails.action?id=" +id1; 
  			return true;
  		} else {
  			return false;
  		}
	}
	
	
	$(document).ready(function() {
		changeAllLabelValues();
	});
	
	
	<%  String empId= (String) request.getAttribute("empId");
		String CCID= (String) request.getAttribute("CCID");
		String ccName = (String) request.getAttribute("CCNAME");
		String EMPNAME = (String)session.getAttribute("EMPNAME_P");
		
		if(EMPNAME==null)
			EMPNAME = (String)request.getAttribute("EMPNAMEFORCC");
	%>
	
	
	var cnt;
	var oldValues = new Array();
	<%  for (int i=0; i<couterlist.size(); i++) {
			java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
	%>
			oldValues[<%=cinnerlist.get(0)%>] = "<%=cinnerlist.get(6)%>"; 
	<%}%>
	
	
	function makeZeroOnUncheck(displayId) {
		var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
		if(!document.getElementById(displayId).checked) {
			if(document.getElementById(headId)) {
				oldValues[headId] = document.getElementById(headId).value;
				changeLabelValuesE(headId, 'MZ');
			}
		} else {
			if(document.getElementById(headId)) {
				document.getElementById(headId).value = oldValues[headId];
				changeLabelValuesE(headId, 'MZ');
			}
		}
	}
	
	
	function getRoundOffValue(val) {
		var roundOffVal = 0;
		if(parseInt(roundOffCondition) == 1){
			roundOffVal = parseFloat(val).toFixed(1);
		} else if(parseInt(roundOffCondition) == 2){
			roundOffVal = parseFloat(val).toFixed(2);
		} else {
			roundOffVal = Math.round(parseFloat(val));
		}
		return roundOffVal;
	}
	
	
</script>


	  <%
      	String nEarningCnt = (String) request.getAttribute("nEarningCnt");
      %>
     <%--  <div class="pagetitle">
        <span>Salary Details <%= (EMPNAME!=null) ?"of "+EMPNAME: "" %><%= (ccName!=null) ? " for "+ ccName:""%></span>
      </div> --%>
    
      <!-- <div class="leftbox reportWidth"> -->
      
			<div class="crdb_details">
				<s:hidden name="curr_short" id="curr_short"></s:hidden> 
				<form id="frm_global" name="frm_global" action="EmployeeSalaryDetails.action" method="post" onsubmit="return checkCal();">
					<div style="float:left; margin:5px;">
						<s:checkbox name="disableSalaryStructure" id="disableSalaryStructure" onclick="checkSalaryHeadDisable();"/>Calculation from level/grade based structure is disabled  
					</div>
					<div id="div_save" style="float:right; margin:5px;">
						
						<%if(couterlist.size() > 0 && uF.parseToInt(nEarningCnt) > 0) { %>
							<!-- <input type="submit" class="input_button" name="update" value="Update" onclick="return saveAll()"/> -->
							<input type="submit" class="btn btn-primary" name="update" value="Submit"/>
						<% } else if(couterlist.size() > 0 && uF.parseToInt(nEarningCnt) == 0) {
							String levelId = (String)request.getAttribute("levelId");
							String strOrgId = (String)request.getAttribute("strOrgId");
						%>
							add atleast 1 more earning salary head <br/>
							<%-- <a target="_blank" href="SalaryDetails.action?strOrg=<%=strOrgId %>&level=<%=levelId %>">setup salary structure</a> --%>
							<a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=125&toPage=PS&toTab=null&strOrg=<%=strOrgId %>&strLevel=<%=levelId %>">setup salary structure</a>
						<% } else {
								String levelId = (String)request.getAttribute("levelId");
								String strOrgId = (String)request.getAttribute("strOrgId");
						%>
							<%-- <a target="_blank" href="SalaryDetails.action?strOrg=<%=strOrgId %>&level=<%=levelId %>">setup salary structure</a> --%>
							<a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=125&toPage=PS&toTab=null&strOrg=<%=strOrgId %>&strLevel=<%=levelId %>">setup salary structure</a>
						<% } %>
					</div>
					
					<input type="hidden" name="empId" value="<%=empId%>"></input>
					<input type="hidden" name="CCID" value="<%=CCID%>"></input>
					<s:hidden name="step" id="step" />
					<s:hidden name="basic" />
					<input type="hidden" name="reimbursementCTC" id="reimbursementCTC" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTC")) %>"/>
					<input type="hidden" name="reimbursementCTCOptional" id="reimbursementCTCOptional" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTCOptional")) %>"/>
					<%
					List<String> alAnnualSalaryHead = (List<String>) request.getAttribute("alAnnualSalaryHead");
					if(alAnnualSalaryHead==null) alAnnualSalaryHead = new ArrayList<String>();
					Map<String, String> hmEmpAnnualVarPolicyAmount = (Map<String, String>) request.getAttribute("hmEmpAnnualVarPolicyAmount");
					if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
					Iterator<String> it = hmEmpAnnualVarPolicyAmount.keySet().iterator();
					if(it.hasNext()) {
						String strSalaryHeadId = it.next();
						if(alAnnualSalaryHead.contains(strSalaryHeadId)) {
						String strAmt = hmEmpAnnualVarPolicyAmount.get(strSalaryHeadId);
					%>
						<input type="hidden" name="annual_<%=strSalaryHeadId %>" id="annual_<%=strSalaryHeadId %>" value="<%=strAmt %>"/>
					<% } } %>
					
					<div class="clr"></div>
					
					<div class="credit" id="div_earning">
		
					    <div class="heading1">
			      			<h3>EARNING DETAILS</h3>
					    </div>
				    	
				    	<div class="details_lables" >
				    	
			 				<% for (int i=0; i<couterlist.size(); i++) { 
			 					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			 				%>
							
							<% if(cinnerlist.get(2).equals("E")) { 
								String isDisplayChecked ="";
								if(alOldGradeSalaryHeadId.contains(cinnerlist.get(0))){
									isDisplayChecked ="checked=\"checked\"";
								}
							%>	
								
									<div class="row">
										<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
									 	<div class="col1 tdDashLabel">
							      	 		<label id="lbl"><%= cinnerlist.get(1) %>:</label>	
				      	 				</div>
				      	 				
				      	 				<div class="col2" id="col2">
				      	 				
				      	 				<%-- <%if(cinnerlist.get(3).equals("P")) {%>
				      	 				
								      	 	<input type="text" id="lblValue_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>" name="salary_head_value"
								      	 		style="width:60px;text-align:right" onchange="changeLabelValuesE(this.id)" maxlength="15" value="0" onkeypress="return isNumberKey(event)" readonly="readonly"/>

							       			<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>" 
							       				value="true" checked="checked" onclick="makeZeroOnUncheck(this.id)">
						       				<!-- <input type="hidden" value="false" name="isDisplay"> -->
								      	 	
								      	 	<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
				      	 					
								      	 	 % of <label id="lbl_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(5).equals("0") ? "": cinnerlist.get(5) %></label>
								      	 	
								      	 	<span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>"></span>
								      	 	<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>_<%=(String)cinnerlist.get(4) %>" />
								      	 <%} else { %> --%>
								      	 	<input type="hidden" id="<%=cinnerlist.get(0)%>_max_cap_amount" value="<%=cinnerlist.get(9) %>">
				       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>"
				       							style="width:60px !important;text-align:right" onchange="changeLabelValuesE(this.id, '')" maxlength="15" onkeypress="return isNumberKey(event)" />
					       					<%-- <span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(0)%>"></span> --%>
					       					<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>">
					       					
					       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)%>"
					       						 value="true" onclick="makeZeroOnUncheck(this.id)" <%=isDisplayChecked %>/>
							       			<!-- <input type="hidden" value="false" name="isDisplay"> -->
				       						
				       						<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equals("P")) { %>
						       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
									      	 	 % of <label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">[<%=uF.showData(""+cinnerlist.get(8),"") %>]</label>
						       				<% } %>	
				       					
				       					<%-- <% } %> --%>
				       					
				       					<%-- <label style="color:green" id="lbl_<%= cinnerlist.get(1) %>"></label><br></br> --%>
						       			
						       			<%-- <a style="color:red" href="javascript:void(0)" onclick="removeField(this.id)" id="remove_<%= cinnerlist.get(0) %>">Remove</a><br></br> --%>
						       				
					       				</div>
				       				</div>
				       				<div class="clr"></div>
				       				
			       					<% } %>
			       				
		   					<% } %>
		   					
								<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
									<label id="lbl_total_gross">Total Gross Salary:</label>
									<label id= "total_earning_value" style="color: green;">0</label>
								</div>
						</div>
						
					    <div class="clr"></div>
		  
		  			</div> 
		 
					<div class="deduction" id="div_deduction">
		  
						<div class="heading1">
							<h3>DEDUCTION DETAILS</h3>
						</div>
		    
			         	<div class="details_lables" >
			 				<% for (int i=0; i<couterlist.size(); i++) {
								java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
							%>
							
							<% if(cinnerlist.get(2).equals("D")) {
								String isDisplayChecked ="";
								if(alOldGradeSalaryHeadId.contains(cinnerlist.get(0))) {
									isDisplayChecked ="checked=\"checked\"";
								}
							%>	
								
								<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
									<div class="row">
									 	<div class="col1 tdDashLabel">
							      	 		<label id="lbl"><%= cinnerlist.get(1) %>:</label>	
				      	 				</div>
				      	 				
				      	 				<div class="col2" id="col2">
				      	 				<%-- <%if(cinnerlist.get(3).equals("P")) {%>
				      	 				
								      	 	<input type="text" id="lblValue_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>" name="salary_head_value"
								      	 	style="width:60px;text-align:right" onchange="changeLabelValuesE(this.id)" maxlength="15"  value="0" onkeypress="return isNumberKey(event)" />
								      	 	
								      	 	<span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"></span>
								      	 	<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>_<%=(String)cinnerlist.get(4) %>">
								      	 	
								      	 	<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>" 
								      	 	value="true" checked="checked" onclick="makeZeroOnUncheck(this.id)">
						       				<!-- <input type="hidden" value="false" name="isDisplay"> -->
						       				
						       				<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
				      	 					
								      	 	 % of <label id="lbl_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(5).equals("0") ? "": cinnerlist.get(5) %></label>
								      	 	
								      	<%}else{%> --%>
								      	 	<input type="hidden" id="<%=cinnerlist.get(0)%>_max_cap_amount" value="<%=cinnerlist.get(9) %>">
				       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>"
				       							style="width:60px !important;text-align:right" onchange="changeLabelValuesE(this.id, '')" maxlength="15" onkeypress="return isNumberKey(event)" />
					       					<%-- <span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(0)%>"></span> --%>
				       						<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>">
				       						
				       						<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=cinnerlist.get(0)%>" 
				       							value="true" onclick="makeZeroOnUncheck(this.id)" <%=isDisplayChecked %>/>
						       				<!-- <input type="hidden" value="false" name="isDisplay"> -->
				       						<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equals("P")) { %>
						       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
								      	 	 	% of <label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">[<%=uF.showData(""+cinnerlist.get(8),"") %>]</label>
						       				<% } %>	
				       					<%-- <%}%> --%>
				       					
				       					<%-- <label style="color:green" id="lbl_<%= cinnerlist.get(1) %>"></label><br></br> --%>
						       			<%-- <a style="color:red" href="javascript:void(0)" onclick="removeField(this.id)" id="remove_<%= cinnerlist.get(0) %>">Remove</a><br></br> --%>
						       				
					       				</div>
				       				</div>
				       				<div class="clr"></div>
			       					<% } %>
		   					<% } %>
		   					
							<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
								<label >Total Deduction:</label>
								<label id= "total_deduction_value" style="color: green;">0</label>
							</div>
					
						    <div class="clr"></div>
		   				</div>
					</div>
		       	</form>
		       	
		  <div class="clr"></div>
		  
		  <div class="netamount" style="display:none;">
		      <div class="netvalue"> 
		      		<label class="tdDashLabel_net" id="lblNetAmountRs">Net Amount Rs:</label> 
		      		<s:label id="lbl_net_amount" name="lbl_net_amount" cssStyle="color: green;"></s:label>
		      </div>
		  </div>
		  
		</div>
		
<!-- </div> -->


<script>
changeLabelValuesE('1', '');
</script>