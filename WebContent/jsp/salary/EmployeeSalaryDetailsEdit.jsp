<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.lang.reflect.Array"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 


<%
	UtilityFunctions uF = new UtilityFunctions();
	java.util.List couterlist = (java.util.List)request.getAttribute("reportList");
	if(couterlist == null) couterlist = new java.util.ArrayList();
	
	Map<String, String> hmERPFData = (Map<String, String>) request.getAttribute("hmERPFData");
	if(hmERPFData == null) hmERPFData = new HashMap<String, String>();
	
%>
<style>


.crdb_details {
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
 
.crdb_details .credit {
	width:50%;
	height:auto;
	float:left;
	border-right:#489BE9 solid 1px;
	margin:0px 5px 0px 0px;
	padding:5px 10px 5px 5px;
}

.crdb_details .deduction {
	width:47%;
	height:auto;
	float:left;
 /* border:#FF0000 solid 1px;*/
	padding:5px;
}

/* .heading h3
{
	margin: 0 0 10px;
	padding:0px 0px 10px 0px;
    border-bottom: 2px solid #EDEBEF;
    color: #000000;
    font-family: verdana,arial,sans-serif;
    font-size: 18px;
	text-align:center;
} */

.crdb_details .heading1 {
    background-color: #EFEFEF;
    background-position: 10px 6px;
    background-repeat: no-repeat;
    cursor: pointer;
    text-shadow: 0 1px 0 #FFFFFF;
    padding-top: 5px; 
}

.crdb_details .heading1 h3 {
	margin: 0 0 10px;
	padding:0px 0px 10px 0px;
    border-bottom: 2px solid #EDEBEF;
    color: #000000;
    font-family: verdana,arial,sans-serif;
    font-size: 18px;
	text-align:center;
}

.crdb_details .details_lables {
 width:auto;
 height:auto;
/* border:#0000CC solid 1px;*/
 float:left;
}

.crdb_details .row {
  width:auto;
  /*border:#009966 solid 1px;*/
  float:left;
  margin:0px 0px 10px 0px;
}

.crdb_details .col1 {
   width:150px; 
 /* border:#00CC33 solid 1px;*/
  float:left;
  text-align:right;
}

.crdb_details .col2 {
	float:left;
	/*text-align:left;*/
	width:auto;
	margin:0px 0px 0px 10px;
}

.crdb_details .buttons {
  float:left;
  width:310px;
  text-align:center;
 /* border:#FF9900 solid 1px;*/
  margin:10px 0px 10px 0px;
}

.crdb_details h4 {
	margin:0px;
	color:#666666;
}

.crdb_details .netvalue {
  float:right;
  margin:10px 5px 10px 5px;
  font-size:22px;
}

.crdb_details .tdDashLabel {
    color: #298CE9;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 12px;
    padding: 3px;
}

.crdb_details .tdDashLabel_net {
   color: #298CE9;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 18px;
    padding: 3px;
}

.crdb_details #popup_deduction label {
    color: #FFFFFF;
    text-align: left;
}

</style>

<script type="text/javascript">
	$(function() {
	    $( "#idEffectiveDate" ).datepicker({format: 'dd/mm/yyyy'});
	});

	function isNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	
	function checkSalaryHeadDisable(){
		changeLabelValuesE('1', '');
	}
	
	var roundOffCondition = '<%=(String)request.getAttribute("roundOffCondition") %>';
	
	function changeLabelValuesE(id, type) {
		//alert('id ===>> ' + id);
		if(id == '301' && (type=='' || type!='MZ')) {
			var empId = document.getElementById("empId").value;
			//alert("empId ===>> " + empId);
			var CCID = document.getElementById("CCID").value;
			//alert("CCID ===>> " + CCID);
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
						//System.out.println("sal Head ===>> " + cinnerlist.get(1));
						if(cinnerlist.get(4)!=null && (cinnerlist.get(4)).trim().equals("P") && cinnerlist.get(14) != null 
							&& !cinnerlist.get(14).trim().equals("") && !cinnerlist.get(14).trim().equalsIgnoreCase("NULL") && cinnerlist.get(14).trim().length() > 0) {
							List<String> al = Arrays.asList(cinnerlist.get(14).trim().split(","));
							int nAl = al != null ? al.size() : 0;
				%>	
						var formula = "";
						var cnt = 0;
						var isReimbursementCTC = new Boolean(false);
				<%		for(int j = 0; j < nAl; j++) {
							String str = al.get(j);
							//System.out.println("str ===>> " + str);
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
									
									if(uF.parseToInt(str.trim()) == IConstants.REIMBURSEMENT_CTC) {
				%>
										formula += ""+(parseFloat(getRoundOffValue(reimbursementCTC)) + parseFloat(getRoundOffValue(reimbursementCTCOptional)));
										cnt++;
										isReimbursementCTC = true;
				<%					} else {
				%>
										var sHeadDisplay = "isDisplay_"+<%=str.trim()%>;
										if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
											var sHeadId = ""+<%=str.trim()%>;
											var sAnnualHeadId = "annual_"+<%=str.trim()%>;
											if(document.getElementById(sAnnualHeadId)){
												//alert("sAnnualHeadId ===>> " + sAnnualHeadId + " -- value ===>> " + document.getElementById(sAnnualHeadId).value);
												var frmlSalHeadAmt = document.getElementById(sAnnualHeadId).value;
												var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
												if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
													frmlSalHeadAmt = maxCapAmt;
												}
												formula += ""+ parseFloat(getRoundOffValue(frmlSalHeadAmt));
												cnt++;
											} else if(document.getElementById(sHeadId)){
												var frmlSalHeadAmt = document.getElementById(sHeadId).value;
												//alert("sHeadId ===>> " + sHeadId + " -- frmlSalHeadAmt ===>> " + frmlSalHeadAmt);
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
					//alert("formula ==>> " + formula);
						var total = 0;
						if(cnt > 0 && formula.trim() != '') {
							var formulaCal = eval(formula);
							var percentage = '<%=cinnerlist.get(7) %>';
							var maxCapAmt = '<%=cinnerlist.get(16) %>';
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
						
						document.getElementById(""+<%=cinnerlist.get(1)%>).value = getRoundOffValue(total);
						document.getElementById("tempValue_"+<%=cinnerlist.get(1)%>).value = getRoundOffValue(total);
						
					<% } %>
				<% } 
				
					for (int a=0; a<couterlist.size(); a++) {
						List<String> cinnerlist = (List<String>)couterlist.get(a);
				%>
					var salHd = '<%=cinnerlist.get(1)%>';
					var salHdAmt = document.getElementById(""+<%=cinnerlist.get(1)%>).value;
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
						var effectiveDate = document.getElementById("idEffectiveDate").value;
						var contributeHeads = '<%=sbContributeHeads.toString() %>';
						var action = 'GetCalculatedContributions.action?salHeadsAndAmt='+salHeadsAndAmt+'&contributeHeads='+contributeHeads
							+'&empId='+empId+'&effectiveDate='+effectiveDate;
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
									//document.getElementById('divLeaveBal').innerHTML = data;
								}
							});
						}
						
					}
				<% } %>
			}
			//alert("1111111");
			calculateTotalEarningandDeduction();
			//changeLabelValuesEWithMultiCalcu(dblERPF); 
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
					//System.out.println("sal Head ===>> " + cinnerlist.get(1));
					if(cinnerlist.get(4)!=null && (cinnerlist.get(4)).trim().equals("P") && cinnerlist.get(14) != null 
						&& !cinnerlist.get(14).trim().equals("") && !cinnerlist.get(14).trim().equalsIgnoreCase("NULL") && cinnerlist.get(14).trim().length() > 0) {
						List<String> al = Arrays.asList(cinnerlist.get(14).trim().split(","));
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
						<%			}
								} else {
						%>
									formula += '<%=str.trim() %>'; 	
						<%		}
							}
						}
					}
				%>
				//alert("formula ===>>" + formula);
					var total = 0;
					if(cnt > 0 && formula.trim() != '') {
						var formulaCal = eval(formula);
						var percentage = '<%=cinnerlist.get(7) %>';
						var maxCapAmt = '<%=cinnerlist.get(16) %>';
						total = (parseFloat(percentage) * parseFloat(formulaCal))/100;
						total = isNaN(total) ? 0 : total;
						if(parseFloat(total) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
							total = maxCapAmt;
						}
						if(parseFloat(total) <0) {
							total=0;
						}
						document.getElementById(""+<%=cinnerlist.get(1)%>).value = getRoundOffValue(total);
						document.getElementById("tempValue_"+<%=cinnerlist.get(1)%>).value = getRoundOffValue(total);
					}
					
				<% } %>
			<% } %>
			
		}
		calculateTotalEarningandDeduction();
	}
	
	
	
	function calculateTotalEarningandDeduction() {
		var total = 0;
		var totalD = 0;
        <%  
			for (int j=0; j<couterlist.size(); j++) {
				java.util.List innerlist = (java.util.List)couterlist.get(j); 
				if(uF.parseToInt(""+innerlist.get(1)) == IConstants.CTC){
					continue;
				}
		%>
				var sSalED = ""+'<%=innerlist.get(3)%>';
				var isContribution = ""+'<%=innerlist.get(17)%>';
				if(sSalED == 'E'){
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(1)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
						var sHeadId = ""+<%=innerlist.get(1)%>;
						if(document.getElementById(sHeadId) && isContribution == 'F'){
							var frmlSalHeadAmt = document.getElementById(sHeadId).value;
							var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							total =  parseFloat(total) + parseFloat(getRoundOffValue(frmlSalHeadAmt));	
						}
					}
				} else if(sSalED == 'D'){
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(1)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
						var sHeadId = ""+<%=innerlist.get(1)%>;
						if(document.getElementById(sHeadId)){
							var frmlSalHeadAmt = document.getElementById(sHeadId).value;
							var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(frmlSalHeadAmt));	
						}
					}
				}
		<%}%>
		document.getElementById("total_earning_value").innerHTML = getRoundOffValue(Math.round(parseFloat(total)));
		document.getElementById("total_deduction_value").innerHTML = getRoundOffValue(Math.round(parseFloat(totalD)));
	}
	
	function removeField(id1) {
		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
			window.location="EmployeeSalaryDetails.action?id=" +id1; 
  			return true;
  		} else {
  			return false;
  		}
	}
	
	function checkCal(){
		calculateTotalEarningandDeduction();
		
		if(confirm('Are you sure, you want to update this salary structure?')){
		   return true;
		}
		return false;
	}
	
	<%  
	String CCID= (String) request.getAttribute("CCID");
	String ccName = (String) request.getAttribute("CCNAME");
	String EMPNAME = (String)session.getAttribute("EMPNAME_P");
	
	if(EMPNAME==null) EMPNAME = (String)request.getAttribute("EMPNAMEFORCC");
	
	%>	
	
	var cnt;
	
	var oldValues = new Array();
	
	<%  for (int i=0; i<couterlist.size(); i++) {
			java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
	%>
			oldValues[<%=cinnerlist.get(1)%>] = "<%=cinnerlist.get(8)%>"; 
			
	<%}%>
	
	function makeZeroOnUncheck(displayId) {
		var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
		if(!document.getElementById(displayId).checked) {
			if(document.getElementById(headId)) {
				oldValues[headId] = document.getElementById(headId).value;
				changeLabelValuesE(headId, 'MZ');
			}
		}else {
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
	
	
	function GetXmlHttpObject() {
	    if (window.XMLHttpRequest) {
	            // code for IE7+, Firefox, Chrome, Opera, Safari
	            return new XMLHttpRequest();
	    }
	    if (window.ActiveXObject) {
	            // code for IE6, IE5
	            return new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    return null;
	}
	
	</script>

     <%--  <div class="pagetitle">
        <span>Salary Details <%= (EMPNAME!=null) ?" of "+EMPNAME: "" %><%= (ccName!=null) ? " for "+ ccName:""%></span>
      </div> --%>
    
      <!-- <div class="leftbox reportWidth"> -->
      <%
      	String nEarningCnt = (String) request.getAttribute("nEarningCnt");
      	String displayFlag = (String) request.getAttribute("displayFlag");
      %>
			<div class="crdb_details">
			
				<s:hidden name="curr_short" id="curr_short"></s:hidden>
			
				<!-- <form id="frm_global" name="frm_global" action="EmployeeSalaryDetails.action" method="get" enctype="multipart/form-data" onsubmit="return checkCal();"> -->
				<form id="frm_global" name="frm_global" action="EmployeeSalaryDetails.action" method="get" onsubmit="return checkCal();">
					
					<div style="float:left; margin:5px;">
						Effective From : <s:textfield id="idEffectiveDate" name="effectiveDate" cssStyle="width:100px !important;"></s:textfield>
						<s:checkbox name="disableSalaryStructure" id="disableSalaryStructure" onclick="checkSalaryHeadDisable();"/>Calculation from level/grade based structure is disabled
					</div>
					
					<div id="div_update" style="float:right; margin:5px;">
						<%if(couterlist.size() > 0 && uF.parseToInt(nEarningCnt) > 0){ %>
							<!-- <input type="submit" class="btn btn-primary" name="update" value="Update" onclick="return saveAll()"/> -->  
							<input type="submit" class="btn btn-primary" name="update" value="Update"/>
						<%}else if(couterlist.size() > 0 && uF.parseToInt(nEarningCnt) == 0){
							String levelId = (String)request.getAttribute("levelId"); 
							String strOrgId = (String)request.getAttribute("strOrgId");
						%>
							add atleast 1 more earning salary head <br/>
							<%-- <a target="_blank" href="SalaryDetails.action?strOrg=<%=strOrgId %>&level=<%=levelId %>">setup salary structure</a> --%>
							<a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=125&toPage=PS&toTab=null&strOrg=<%=strOrgId %>&strLevel=<%=levelId %>">setup salary structure</a>
						<%} else {
								String levelId = (String)request.getAttribute("levelId");
								String strOrgId = (String)request.getAttribute("strOrgId");
						%>
							<%-- <a target="_blank" href="SalaryDetails.action?strOrg=<%=strOrgId %>&level=<%=levelId %>">setup salary structure</a> --%>
							<a target="_blank" href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=125&toPage=PS&toTab=null&strOrg=<%=strOrgId %>&strLevel=<%=levelId %>">setup salary structure</a>
						<%} %>
					</div>
					<s:hidden name="empId" id="empId" />
					<s:hidden name="CCID" id="CCID" />
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
					if(it.hasNext()){
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
				    	
							<% 	boolean isBenefit = false;
								boolean isGRoss = false;
								//couterlist = (java.util.List)request.getAttribute("reportList"); 
								//System.out.println("couterlist ===>> " + couterlist);
							%>
			 				<%
			 				for (int i=0; i<couterlist.size(); i++) { 
			 				%>
			 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i);
			 				if(uF.parseToInt((String)cinnerlist.get(1))==IConstants.GROSS){
			 					isGRoss = true;
			 				}
			 				//System.out.println("isGRoss ===>> " + isGRoss);
			 				%>
							
							<% if(cinnerlist.get(3).equals("E")) { %>	
								
									<div class="row">
									
										<input type="hidden" name="emp_salary_id" value='<%= cinnerlist.get(0) %>'></input>
										<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(1) %>'></input>
										
									 	<div class="col1 tdDashLabel" <%=((uF.parseToInt((String)cinnerlist.get(1))==IConstants.GROSS)?"color: #298CE9;font-size: 18px;":"")%>>
							      	 		<label id="lbl"><%= cinnerlist.get(2) %>:</label>	
				      	 				</div>
				      	 				
				      	 				<div class="col2" id="col2">
				      	 				
				      	 				<%-- <%if(cinnerlist.get(4).equals("P")) { %>
				      	 				
								      	 	<input type="text" id="lblValue_<%=(String)cinnerlist.get(1) %>_<%=(String)cinnerlist.get(5) %>" name="salary_head_value" value="<%= cinnerlist.get(8) %>"
								      	 		style="width:60px;text-align:right" onchange="changeLabelValuesE(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" readonly="readonly"/>
								      	 		<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(1) %>_<%=(String)cinnerlist.get(5) %>" value="<%=cinnerlist.get(8) %>">
								      	 	<span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(8) %></span>
								      	 	
								      	 	<% if(uF.parseToBoolean((String)cinnerlist.get(12))) { %>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="<%=(String)cinnerlist.get(0)%>" checked="checked">
						       						<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="true" checked="checked">	
						       				<%} else if(uF.parseToBoolean(displayFlag)) {%>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="true" checked="checked">
						       				<% } else { %>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="<%=(String)cinnerlist.get(0)%>">
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="true">
						       				<% } %>
								      	 	
								      	 	<label id="lbl_amount_<%=(String)cinnerlist.get(1) %>_<%=(String)cinnerlist.get(5) %>"><%= cinnerlist.get(7) %></label>
								      	 	% of <label id="lbl_<%=(String)cinnerlist.get(1) %>"><%= cinnerlist.get(15).equals("0") ? "": cinnerlist.get(15) %></label>
								      	 	
								      	 	
								      	 <% } else { %> --%>
								      	 	
				       					<%-- <%if(uF.parseToInt((String)cinnerlist.get(13))>990) { %>
				      	 				
										<label>As per calcuations</label>
										<input type="hidden" name="salary_head_value">
				      	 				<% } else { %> --%>
				      	 					<input type="hidden" id="<%=cinnerlist.get(1)%>_max_cap_amount" value="<%=cinnerlist.get(16) %>">
				      	 					<input type="text" id="<%=cinnerlist.get(1)%>" name="salary_head_value" value="<%=cinnerlist.get(8) %>" 
				       							style="width:60px !important;text-align:right" onchange="changeLabelValuesE(this.id, '')" maxlength="15" onkeypress="return isNumberKey(event)" />
				       						<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(1) %>" value="<%=cinnerlist.get(8) %>">
					       					<%-- <span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(1)%>"><%= cinnerlist.get(8) %></span> --%>
				      	 				<%-- <%} %> --%>
					       					
					       					<% if(uF.parseToBoolean((String)cinnerlist.get(12))) { %>
						       					<%-- <input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="<%=(String)cinnerlist.get(0)%>" checked="checked"> --%>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="true" checked="checked">	
						       				<%} else if(uF.parseToBoolean(displayFlag)) {%>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="true" checked="checked">
						       				<% } else { %>
						       					<%-- <input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="<%=(String)cinnerlist.get(0)%>"> --%>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" 
						       							onclick="makeZeroOnUncheck(this.id)" value="true">
						       				<% } %>
						       				<% if(cinnerlist.get(4)!=null && ((String)cinnerlist.get(4)).trim().equals("P")) { %>
						       					<label id="lbl_amount_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(7) %></label>
								      	 	 	% of <label id="lblMulCal_<%=(String)cinnerlist.get(1) %>">[<%=uF.showData(""+cinnerlist.get(15),"") %>]</label>
						       				<%} %>
					       					
				       					<%-- <%} %> --%>
				       					
					       				</div>
				       				</div>
				       				<div class="clr"></div>
			       					<% } %>
			       				
		   					<% } %>
		   					 
								<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9; <%=((!isGRoss)?"":"display:none;")%>">
									<label name="total_gross" id="lbl_total_gross">Total Gross Salary: </label>
									<label name="total_earning_value" id= "total_earning_value" style="color: green;">0</label>
								</div>
						</div>
						
					    <div class="clr"></div>
		  
		  			</div> 
		 
					<div class="deduction" id="div_deduction">
		  
						<div class="heading1">
							<h3>DEDUCTION DETAILS</h3>
						</div>
		    
			         	<div class="details_lables" >
			 				<% //couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 				<%  int count = 0;
			 					for (int i=0; i<couterlist.size(); i++) { %>
			 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
							
							<%  
								if(cinnerlist.get(3).equals("D")) { %>	
								
									<div class="row">
									
									<%if(isGRoss && count==0){count++; %>
									<div class="row" style="width:100%; float: left; font-size: 18px; color: #298CE9;">
										<label >Total Deduction:</label>
										<label id= "total_deduction_value" style="color: green;">0</label>
									</div>
									<%} %>
								
										<input type="hidden" name="emp_salary_id" value='<%= cinnerlist.get(0) %>'></input>
										<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(1) %>'></input>
									 	<div class="col1 tdDashLabel">
							      	 		<label id="lbl"><%= cinnerlist.get(2) %>:</label>	
				      	 				</div>
				      	 				
				      	 				<div class="col2" id="col2">
				      	 				
				      	 				<%-- <%if(cinnerlist.get(4).equals("P")) { %>
								      	 	
								      	 	<input type="text" id="lblValue_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" name="salary_head_value" value="<%= cinnerlist.get(8) %>"
								      	 		style="width:60px;text-align:right" onchange="changeLabelValuesE(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" readonly="readonly"/>
								      	 	
								      	 	<span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(8) %></span>
								      	 	<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(1) %>_<%=(String)cinnerlist.get(5) %>" value="<%=cinnerlist.get(8) %>">
								      	 	
								      	 	<% if(uF.parseToBoolean((String)cinnerlist.get(12))) { %>
							       				<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
							       						value="<%=(String)cinnerlist.get(0)%>" checked="checked" onclick="makeZeroOnUncheck(this.id);">
							       				<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
							       						value="true" checked="checked" onclick="makeZeroOnUncheck(this.id);">	
							       			<%} else if(uF.parseToBoolean(displayFlag)) {%>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
							       						value="true" checked="checked" onclick="makeZeroOnUncheck(this.id);">
						       				<% } else { %>
								       			<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
								       					onclick="makeZeroOnUncheck(this.id);" value="<%=(String)cinnerlist.get(0)%>">
								       			<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>" 
								       					onclick="makeZeroOnUncheck(this.id);" value="true">
								       			
							       			<%}%>
								      	 	
								      	 	<label id="lbl_amount_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(7) %></label>
				      	 					
								      	 	 % of <label id="lbl_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(6).equals("0") ? "": cinnerlist.get(6) %></label>
								      	 	
								      	 <%}else{%> --%>
								      	 	<input type="hidden" id="<%=cinnerlist.get(1)%>_max_cap_amount" value="<%=cinnerlist.get(16) %>">
				       						<input type="text" id="<%=cinnerlist.get(1)%>" name="salary_head_value" value="<%= cinnerlist.get(8) %>"
				       							style="width:60px !important;text-align:right" onchange="changeLabelValuesE(this.id, '')" maxlength="15" onkeypress="return isNumberKey(event)" />
					       					<%-- <span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(1)%>"><%= cinnerlist.get(8) %></span> --%>
					       					<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(1) %>" value="<%=cinnerlist.get(8) %>">
					       					
					       					<% if(uF.parseToBoolean((String)cinnerlist.get(12))) { %>
						       					<%-- <input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>"
						       							value="<%=(String)cinnerlist.get(0)%>" checked="checked" onclick="makeZeroOnUncheck(this.id);"> --%>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>"
						       							value="true" checked="checked" onclick="makeZeroOnUncheck(this.id);">	
						       				<%} else if(uF.parseToBoolean(displayFlag)) {%>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>"
						       							value="true" checked="checked" onclick="makeZeroOnUncheck(this.id);">
						       				<% } else { %>
								       			<%-- <input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>" 
								       					onclick="makeZeroOnUncheck(this.id);" value="<%=(String)cinnerlist.get(0)%>"> --%>
								       			<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>" 
								       					onclick="makeZeroOnUncheck(this.id);" value="true">
						       				<%}%>
					       					<% if(cinnerlist.get(4)!=null && ((String)cinnerlist.get(4)).trim().equals("P")) { %>
						       					<label id="lbl_amount_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(7) %></label>
								      	 	 	% of <label id="lblMulCal_<%=(String)cinnerlist.get(1) %>">[<%=uF.showData(""+cinnerlist.get(15),"") %>]</label>
						       				<%} %>
				       					<%-- <%} %> --%>
				       					
					       				</div>
				       				</div>
				       				<div class="clr"></div>
			       					<%}%>
			       				
		   					<%}%>
		   					
							<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;<%=((!isGRoss)?"":"display:none;")%>">
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