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



<style>

.clr 
{
 clear:both; 
}

.input {
width : 100px;
}

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
  border-right:#489BE9 solid 1px;
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

.heading h3
{
	margin: 0 0 10px;
	padding:0px 0px 10px 0px;
    border-bottom: 2px solid #EDEBEF;
    color: #000000;
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

.crdb_details .row
{
  width:auto;
  /*border:#009966 solid 1px;*/
  float:left;
  margin:0px 0px 10px 0px;
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

<%  
	java.util.List couterlist = (java.util.List)request.getAttribute("reportList");
	if(couterlist == null) couterlist = new java.util.ArrayList();
	
	UtilityFunctions uF = new UtilityFunctions();
	String strActivity = (String) request.getAttribute("strActivity");
	
	List<String> alOldGradeSalaryHeadId = (List<String>)request.getAttribute("alOldGradeSalaryHeadId");
	if(alOldGradeSalaryHeadId == null) alOldGradeSalaryHeadId = new ArrayList<String>();
	
%>	

<script type="text/javascript">
/* $(function() {
    $( "#idEffectiveDate" ).datepicker({dateFormat: 'dd/mm/yy'});
}); */

<% if(strActivity != null && (strActivity.equals(IConstants.ACTIVITY_PROMOTION_ID) || strActivity.equals(IConstants.ACTIVITY_DEMOTION_ID) || strActivity.equals(IConstants.ACTIVITY_GRADE_CHANGE_ID))) { %>
	function checkSalaryHeadDisable1(){
		changeLabelValuesE1('1', '');
	}

	var roundOffCondition='<%=(String)request.getAttribute("roundOffCondition") %>';
	function changeLabelValuesE1(id, type) {
		if(id == '301' && (type=='' || type!='MZ')) {
    		var strEmpId = document.getElementById("strEmpId2").value;
			var effectiveDate = document.getElementById("idEffectiveDate").value;
    		var ctcAmt = document.getElementById("301").value;
    		getContent('salaryDetailsDiv','EmpActivityEmpSalaryDetails.action?empId='+strEmpId+'&effectiveDate='+effectiveDate+'&ctcAmt='+ctcAmt);
    		
    	} else {
			var disableSalaryStructure = document.getElementById("disableSalaryStructure1");		
			if(disableSalaryStructure.checked == false) {
		   		var reimbursementCTC = document.getElementById("reimbursementCTC").value;
		   		var reimbursementCTCOptional = document.getElementById("reimbursementCTCOptional").value;
		   		
		   		<%  
				for (int i=0; i<couterlist.size(); i++) {
					List<String> cinnerlist = (List<String>)couterlist.get(i); 
					if(cinnerlist.get(3)!=null && (cinnerlist.get(3)).trim().equals("P") && cinnerlist.get(7) != null && !cinnerlist.get(7).trim().equals("") && !cinnerlist.get(7).trim().equalsIgnoreCase("NULL") && cinnerlist.get(7).trim().length() > 0) {
						List<String> al = Arrays.asList(cinnerlist.get(7).trim().split(","));
						int nAl = al != null ? al.size() : 0;
			%>	
						var formula = "";
						var cnt = 0;
						var isReimbursementCTC = new Boolean(false);
				<%		for(int j = 0; j < nAl; j++) {
							String str = al.get(j);
							if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")){
								boolean isInteger = uF.isInteger(str.trim());
								if(isInteger){
									if(uF.parseToInt(str.trim()) == IConstants.REIMBURSEMENT_CTC){
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
												var frmlSalHeadAmt = document.getElementById(sAnnualHeadId).value;
												var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
												if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
													frmlSalHeadAmt = maxCapAmt;
												}
												formula += ""+ parseFloat(getRoundOffValue(frmlSalHeadAmt));
												//formula += ""+ parseFloat(getRoundOffValue(document.getElementById(sAnnualHeadId).value));
												cnt++;
											} else if(document.getElementById(sHeadId)){
												var frmlSalHeadAmt = document.getElementById(sHeadId).value;
												var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
												if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
													frmlSalHeadAmt = maxCapAmt;
												}
												formula += ""+ parseFloat(getRoundOffValue(frmlSalHeadAmt));
												//formula += ""+parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));
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
					<% } %>		
				<% } %>
				}
			calculateTotalEarningandDeduction1();
    	}
	}
 

	function calculateTotalEarningandDeduction1() {
		var total = 0;
		var totalD = 0;
	    <%  
			for (int j=0; j<couterlist.size(); j++) {
				java.util.List innerlist = (java.util.List)couterlist.get(j); 
				if(uF.parseToInt(""+innerlist.get(0)) == IConstants.CTC) {
					continue;
				}
		%>
				var sSalED = ""+'<%=innerlist.get(2)%>';
				var isContribution = ""+'<%=innerlist.get(10)%>';
				if(sSalED == 'E') {
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(0)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
						var sHeadId = ""+<%=innerlist.get(0)%>;
						if(document.getElementById(sHeadId) && isContribution == 'F') {
							var frmlSalHeadAmt = document.getElementById(sHeadId).value;
							var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							total =  parseFloat(total) + parseFloat(getRoundOffValue(frmlSalHeadAmt));
							//total =  parseFloat(total) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
						}
					}
				} else if(sSalED == 'D') {
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(0)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
						var sHeadId = ""+<%=innerlist.get(0)%>;
						if(document.getElementById(sHeadId)) {
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


	$(document).ready(function() {
		changeAllLabelValues();
	});

	var cnt;
	var oldValues1 = new Array();
	<%  
	//System.out.println("couterlist=====>"+couterlist.toString());
	for (int i=0; couterlist != null && !couterlist.isEmpty() && i<couterlist.size(); i++) {
		java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
		//System.out.println("cinnerlist=====>"+cinnerlist.toString());
%>
		oldValues1[<%=cinnerlist.get(0)%>] = "<%=cinnerlist.get(6)%>"; 
<% } %>


	function makeZeroOnUncheck1(displayId) {
		var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
		if(!document.getElementById(displayId).checked) {
			if(document.getElementById(headId)) {
				oldValues1[headId] = document.getElementById(headId).value;
				changeLabelValuesE1(headId, 'MZ');
			}
		} else {
			if(document.getElementById(headId)) {
				document.getElementById(headId).value = oldValues1[headId];
				changeLabelValuesE1(headId, 'MZ');
			}
		}		
	}
	
	changeLabelValuesE1('1', '');
	
<% } else { %>

function changeLabelValuesE(id, type) {
	//alert('id ===>> ' + id);
	if(id == '301' && (type=='' || type!='MZ')) {
		var strEmpId = document.getElementById("strEmpId2").value;
		var effectiveDate = document.getElementById("idEffectiveDate").value;
		var ctcAmt = document.getElementById("301").value;
		var hideCtcAmt = document.getElementById("hide_301").value;
		//alert("ctcAmt ===>> " + ctcAmt);
		getContent('salaryDetailsDiv','EmpActivityEmpSalaryDetails.action?empId='+strEmpId+'&effectiveDate='+effectiveDate+'&ctcAmt='+ctcAmt+'&hideCtcAmt='+hideCtcAmt);
		
	} else {
		//alert("changeLabelValuesE 1");
		var disableSalaryStructure = document.getElementById("disableSalaryStructure");
		//alert("changeLabelValuesE 2");
		if(disableSalaryStructure.checked == false){
		//	alert("changeLabelValuesE 3");
			var reimbursementCTC = document.getElementById("reimbursementCTC").value;
			//alert("changeLabelValuesE 4");
			var reimbursementCTCOptional = document.getElementById("reimbursementCTCOptional").value;
			var salHeadsAndAmt = ",";
			//alert("reimbursementCTC ===>> " +reimbursementCTC); 
			<%  
			List<String> alContributeHeads = new ArrayList<String>();
			StringBuilder sbContributeHeads = new StringBuilder();
			//System.out.println("couterlist ===>> " + couterlist);
			for (int i=0; i<couterlist.size(); i++) {
				List<String> cinnerlist = (List<String>)couterlist.get(i); 
				if(cinnerlist.get(4)!=null && (cinnerlist.get(4)).trim().equals("P") && cinnerlist.get(14) != null && !cinnerlist.get(14).trim().equals("") 
					&& !cinnerlist.get(14).trim().equalsIgnoreCase("NULL") && cinnerlist.get(14).trim().length() > 0) {
					List<String> al = Arrays.asList(cinnerlist.get(14).trim().split(","));
					int nAl = al != null ? al.size() : 0;
			%>	
					var formula = "";
					var cnt = 0;
					var isReimbursementCTC = new Boolean(false);
			<%		for(int j=0; j<nAl; j++) {
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
											var frmlSalHeadAmt = document.getElementById(sAnnualHeadId).value;
											var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
											if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
												frmlSalHeadAmt = maxCapAmt;
											}
											formula += ""+ parseFloat(getRoundOffValue(frmlSalHeadAmt));
											//formula += ""+ parseFloat(getRoundOffValue(document.getElementById(sAnnualHeadId).value));
											cnt++;
										} else if(document.getElementById(sHeadId)){
											var frmlSalHeadAmt = document.getElementById(sHeadId).value;
											var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
											if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
												frmlSalHeadAmt = maxCapAmt;
											}
											formula += ""+ parseFloat(getRoundOffValue(frmlSalHeadAmt));
											//formula += ""+parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));
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
				<%			}
						}
					}
				%>
					//alert("cnt ===>> " + cnt);
					var total = 0;
					if(cnt > 0 && formula.trim() != ''){
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
				//alert("out of loop");
				
			<% } 
			for (int a=0; a<couterlist.size(); a++) {
				List<String> cinnerlist = (List<String>)couterlist.get(a);
			%>
				var salHd = '<%=cinnerlist.get(1)%>';
				//alert("salHd ===>> " + salHd);
				var salHdAmt = document.getElementById(""+<%=cinnerlist.get(1)%>).value;
				//alert("salHdAmt ===>> " + salHdAmt);
				salHeadsAndAmt = salHeadsAndAmt+salHd+'::::'+salHdAmt+',';
			<%
				}
			if(couterlist == null || couterlist.size() == 0) { %>
				salHeadsAndAmt = "";
			<% } %>
			
			//alert("out of loop 1");
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
			//alert("out of loop 2");
		}
		calculateTotalEarningandDeduction();
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
									//formula += ""+ parseFloat(getRoundOffValue(document.getElementById(sAnnualHeadId).value));
									cnt++;
								} else if(document.getElementById(sHeadId)){
									var frmlSalHeadAmt = document.getElementById(sHeadId).value;
									var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
									if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
										frmlSalHeadAmt = maxCapAmt;
									}
									formula += ""+ parseFloat(getRoundOffValue(frmlSalHeadAmt));
									//formula += ""+parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));
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
		//alert("out of loop changeLabelValuesEWithMultiCalcu");
	}
	calculateTotalEarningandDeduction();
}


function calculateTotalEarningandDeduction() {
	var total = 0;
	var totalD = 0;
    <%  
		for (int j=0; couterlist!=null && j<couterlist.size(); j++) {
			java.util.List innerlist = (java.util.List)couterlist.get(j); 
			if(uF.parseToInt(""+innerlist.get(1)) == IConstants.CTC) {
				continue;
			}
	%>
	
			var sSalED = ""+'<%=innerlist.get(3)%>';
			var isContribution = ""+'<%=innerlist.get(17)%>';
			if(sSalED == 'E') {
				var sHeadDisplay = "isDisplay_"+<%=innerlist.get(1)%>;
				if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
					var sHeadId = ""+<%=innerlist.get(1)%>;
					if(document.getElementById(sHeadId) && isContribution == 'F') {
						var frmlSalHeadAmt = document.getElementById(sHeadId).value;
						var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
						if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
							frmlSalHeadAmt = maxCapAmt;
						}
						total =  parseFloat(total) + parseFloat(getRoundOffValue(frmlSalHeadAmt));
						//total =  parseFloat(total) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
					}
				}
			} else if(sSalED == 'D') {
				var sHeadDisplay = "isDisplay_"+<%=innerlist.get(1)%>;
				if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
					var sHeadId = ""+<%=innerlist.get(1)%>;
					if(document.getElementById(sHeadId)) {
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
	<% } %>
	document.getElementById("total_earning_value").innerHTML = getRoundOffValue(Math.round(parseFloat(total)));
	document.getElementById("total_deduction_value").innerHTML = getRoundOffValue(Math.round(parseFloat(totalD)));
}



var oldValues = new Array();
<% for (int i=0;  couterlist!=null && i<couterlist.size(); i++) {
	java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
%>
	oldValues[<%=cinnerlist.get(1)%>] = "<%=cinnerlist.get(8)%>"; 
<% } %>

function makeZeroOnUncheck(displayId) {
	var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
	//alert("headId ===>> " + headId);
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

changeLabelValuesE('1', '');
<% } %>


</script>



      <!-- <div class="leftbox reportWidth"> -->
      	<%
      	if(strActivity != null && (strActivity.equals(IConstants.ACTIVITY_PROMOTION_ID) || strActivity.equals(IConstants.ACTIVITY_DEMOTION_ID) || strActivity.equals(IConstants.ACTIVITY_GRADE_CHANGE_ID))) { %>
      	<div class="crdb_details">
			<s:checkbox name="disableSalaryStructure" id="disableSalaryStructure1" onclick="checkSalaryHeadDisable1();"/>Calculation from level/grade based structure is disabled
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
				
				<input type="hidden" name="reimbursementCTC" id="reimbursementCTC" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTC")) %>"/>
				<input type="hidden" name="reimbursementCTCOptional" id="reimbursementCTCOptional" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTCOptional")) %>"/>
				
				<div class="clr"></div>
				
				<div id="div_earning" style=" width: 48%; float: left; margin: 0px 5px 0px 0px; border-right: 1px solid #489BE9;">
	
				    <div style="width: 200px; margin-bottom: 10px; background-color: #EFEFEF; background-position: 10px 6px; background-repeat: no-repeat; cursor: pointer; text-shadow: 0 1px 0 #FFFFFF;">
		      			<h3>EARNING DETAILS</h3>
				    </div>
			    	
			    	<div class="details_lables" >
			    	
					<% couterlist = (java.util.List)request.getAttribute("reportList"); 
						//System.out.println("couterlist --->> " + couterlist);
					%>
	 				<% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
	 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
					
					<% if(cinnerlist.get(2).equals("E")) {
						String isDisplayChecked =""; 
						if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){
							if(alOldGradeSalaryHeadId.contains(""+cinnerlist.get(0))){
								isDisplayChecked ="checked=\"checked\"";
							}
						}
					%>	
						
							<div class="row">
								<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
							 	<div class="col1 tdDashLabel">
					      	 		<label id="lbl"><%= cinnerlist.get(1) %>:</label>	
		      	 				</div>
		      	 				
		      	 				<div class="col2" id="col2">
		      	 					<input type="hidden" id="<%=cinnerlist.get(0)%>_max_cap_amount" value="<%=cinnerlist.get(9) %>">
		       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>" style="width:90px !important; text-align:right" onchange="changeLabelValuesE1(this.id, '')" maxlength="15" onkeypress="return isNumberKey(event)"/>
		       						<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>">	
			       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)%>" value="true" onclick="makeZeroOnUncheck1(this.id)" <%=isDisplayChecked %>/>
			       					<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equalsIgnoreCase("P")) { %>
				       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
							      	 	 % of<label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">[<%=uF.showData(""+cinnerlist.get(8),"") %>]</label>
				       				<% } %>
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
	 
				<div id="div_deduction" style="float: left; width: 50%;">
					<div style="width: 200px; margin-bottom: 10px; background-color: #EFEFEF; background-position: 10px 6px; background-repeat: no-repeat; cursor: pointer; text-shadow: 0 1px 0 #FFFFFF;">
						<h3>DEDUCTION DETAILS</h3>
					</div>
		         	<div class="details_lables" >
		 				<% couterlist = (java.util.List)request.getAttribute("reportList"); %>
		 				<% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
		 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
							<% if(cinnerlist.get(2).equals("D")) { 
								String isDisplayChecked ="";
								if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){
									if(alOldGradeSalaryHeadId.contains(""+cinnerlist.get(0))){
										isDisplayChecked ="checked=\"checked\"";
									}
								}
							%>	
								<input type="hidden" name="salary_head_id" value='<%=cinnerlist.get(0) %>'></input>
								<div class="row">
								 	<div class="col1 tdDashLabel">
						      	 		<label id="lbl"><%=cinnerlist.get(1) %>:</label>	
			      	 				</div>
			      	 				<div class="col2" id="col2">
			      	 					<input type="hidden" id="<%=cinnerlist.get(0)%>_max_cap_amount" value="<%=cinnerlist.get(9) %>">
			       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>" style="width:90px !important;text-align:right" onchange="changeLabelValuesE1(this.id, '')" maxlength="15" onkeypress="return isNumberKey(event)" />
				       					<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>">
			       						<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)%>" value="true" onclick="makeZeroOnUncheck1(this.id)" <%=isDisplayChecked %>/>
			       						<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equalsIgnoreCase("P")) { %>
					       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%=cinnerlist.get(6) %></label>
								      	 	 % of<label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">[<%=uF.showData(""+cinnerlist.get(8),"") %>]</label>
					       				<%} %>	
				       				</div>
			       				</div>
			       				<div class="clr"></div>
		       					<%}%>
	   						<%}%>
	   					
						<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
							<label >Total Deduction:</label>
							<label id= "total_deduction_value" style="color: green;">0</label>
						</div>
				
					    <div class="clr"></div>
	   				</div>
				</div>
		       	<!-- </form> -->
       	<div class="clr"></div>
       	<div style="float: left; width: 100%;">This salary has to be approved again in salary approval</div>
		       	
		  <div class="clr"></div>		  
		  <div class="netamount" style="display:none;">
		      <div class="netvalue"> 
		      		<label class="tdDashLabel_net" id="lblNetAmountRs">Net Amount Rs:</label> 
		      		<s:label id="lbl_net_amount" name="lbl_net_amount" cssStyle="color: green;"></s:label>
		      </div>
		  </div>
		  
		</div>
		
		
      	<% } else { %>
			<div class="crdb_details">
					<s:checkbox name="disableSalaryStructure" id="disableSalaryStructure" onclick="checkSalaryHeadDisable();"/>Calculation from level/grade based structure is disabled
					<%
					Map<String, String> hmEmpAnnualVarPolicyAmount = (Map<String, String>) request.getAttribute("hmEmpAnnualVarPolicyAmount");
					if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
					Iterator<String> it = hmEmpAnnualVarPolicyAmount.keySet().iterator();
					if(it.hasNext()){
						String strSalaryHeadId = it.next();
						String strAmt = hmEmpAnnualVarPolicyAmount.get(strSalaryHeadId);
					%>
						<input type="hidden" name="annual_<%=strSalaryHeadId %>" id="annual_<%=strSalaryHeadId %>" value="<%=strAmt %>"/>
					<%} %>
					
					<input type="hidden" name="reimbursementCTC" id="reimbursementCTC" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTC")) %>"/>
					<input type="hidden" name="reimbursementCTCOptional" id="reimbursementCTCOptional" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTCOptional")) %>"/>
								
					<div id="div_earning" style=" width: 48%; float: left; margin: 0px 5px 0px 0px; border-right: 1px solid #489BE9;">
		
					    <div style="width: 200px; margin-bottom: 10px; background-color: #EFEFEF; background-position: 10px 6px; background-repeat: no-repeat; cursor: pointer; text-shadow: 0 1px 0 #FFFFFF;">
					    
			      			<h4>EARNING DETAILS</h4>
					    </div>
				    	
				    	<div class="details_lables" style="width: 200px">
							<% 	boolean isBenefit = false;
								boolean isGRoss = false;
								couterlist = (java.util.List)request.getAttribute("reportList"); 
							%>
			 				<% for (int i=0; i<couterlist.size(); i++) { %>
			 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i);
			 				if(uF.parseToInt((String)cinnerlist.get(1))==IConstants.GROSS) {
			 					isGRoss = true;
			 				}
			 				%>
							
							<% if(cinnerlist.get(3).equals("E")) { %>	
								<%if(uF.parseToInt((String)cinnerlist.get(13))>990 && !isBenefit){ isBenefit=true;%>
								Benefits
								<hr/>
								<%} %>
								
								
									<div class="row" style="width: 100%;">
									
										<input type="hidden" name="emp_salary_id" value='<%= cinnerlist.get(0) %>'></input>
										<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(1) %>'></input>
										
									 	<div class="tdDashLabel" style="float: left; width: 25%;" <%=((uF.parseToInt((String)cinnerlist.get(1))==IConstants.GROSS)?"color: #298CE9;font-size: 18px;":"")%>>
							      	 		<label id="lbl"><%= cinnerlist.get(2) %>:</label>	
				      	 				</div>
				      	 				
				      	 				<div id="col2" style="float: right;">
				      	 					<% String hideCtcAmt = (String)request.getAttribute("hideCtcAmt"); 
				      	 					if(uF.parseToInt((String)cinnerlist.get(1))==301 && uF.parseToDouble(hideCtcAmt)>0) {
				      	 					%>
				      	 						<input type="hidden" id="hide_<%=cinnerlist.get(1)%>" name="hide_salary_head_value" value="<%=uF.showData(hideCtcAmt, "0") %>"/>
				      	 					<% } else { %>
				      	 						<input type="hidden" id="hide_<%=cinnerlist.get(1)%>" name="hide_salary_head_value" value="<%=cinnerlist.get(8) %>"/>
				      	 					<% } %>
				      	 					<input type="hidden" id="<%=cinnerlist.get(1)%>_max_cap_amount" value="<%=cinnerlist.get(16) %>">
				      	 					<input type="text" id="<%=cinnerlist.get(1)%>" name="salary_head_value" value="<%= cinnerlist.get(8) %>" 
				       							style="width:90px !important;text-align:right" maxlength="15" onchange="changeLabelValuesE(this.id, '')" onkeypress="return isNumberKey(event)"/>
				       						<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(1) %>" value="<%=cinnerlist.get(8) %>">	
					       					
					       					<% if(uF.parseToBoolean((String)cinnerlist.get(12))) { %>
						       					<input type="hidden" id="hideIsDisplay_<%=(String)cinnerlist.get(1) %>" name="hideIsDisplay" value="<%=(String)cinnerlist.get(0)%>"/>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" onclick="makeZeroOnUncheck(this.id)" value="true" checked="checked"  />
						       				<% } else { %>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" onclick="makeZeroOnUncheck(this.id)" value="true" />
						       				<% } %>
					       				</div>
					       				
					       				
					       				<% if(cinnerlist.get(4)!=null && ((String)cinnerlist.get(4)).trim().equals("P")) { %>
								      	 	<div id="col2" style="float: right;">
						       					<label id="lbl_amount_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(7) %></label>
									      	 	% of <label id="lblMulCal_<%=(String)cinnerlist.get(1) %>">[<%=uF.showData(""+cinnerlist.get(15),"") %>]</label>
						       				</div>
						       			<%} %>
				       				</div>
				       				<div class="clr"></div>
			       					<% } %>
			       				
		   					<% } %>
		   					 
								<div class="row" style="text-align:center; float: left; font-size: 16px; color: #298CE9; <%=((!isGRoss)?"":"display:none;")%>">
									<label name="total_gross" id="lbl_total_gross">Total Gross Salary: </label>
									<label name="total_earning_value" id= "total_earning_value" style="color: green;">0</label>
								</div>
						</div>
						
					    <div class="clr"></div>
		  
		  			</div> 
		 
					<div id="div_deduction" style="float: left; width: 50%;">
		  
						<div style="width: 218px; margin-bottom: 10px; background-color: #EFEFEF; background-position: 10px 6px; background-repeat: no-repeat; cursor: pointer; text-shadow: 0 1px 0 #FFFFFF;">
							<h4>DEDUCTION DETAILS</h4>
						</div>
		    
			         	<div class="details_lables" style="width: 200px;">
			 				<% couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 				<%  int count = 0;
			 					for (int i=0; i<couterlist.size(); i++) { %>
			 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
							
							<%  
								if(cinnerlist.get(3).equals("D")) { %>	
								
									<div class="row" style="width: 100%;">
									
									<%if(isGRoss && count==0){count++; %>
									<div class="row" style="width:100%; float: left; font-size: 18px; color: #298CE9;">
										<label >Total Deduction:</label>
										<label id= "total_deduction_value" style="color: green;">0</label>
									</div>
									<%} %>
								
										<input type="hidden" name="emp_salary_id" value='<%= cinnerlist.get(0) %>'></input>
										<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(1) %>'></input>
									 	<div class="tdDashLabel" style="float: left; width: 25%;">
							      	 		<label id="lbl"><%= cinnerlist.get(2) %>:</label>	
				      	 				</div>
				      	 				
				      	 				<div id="col2" style="float: right;">
								      	 	<input type="hidden" id="hide_<%=cinnerlist.get(1)%>" name="hide_salary_head_value" value="<%= cinnerlist.get(8) %>"/>
								      	 	<input type="hidden" id="<%=cinnerlist.get(1)%>_max_cap_amount" value="<%=cinnerlist.get(16) %>">
				       						<input type="text" id="<%=cinnerlist.get(1)%>" name="salary_head_value" value="<%= cinnerlist.get(8) %>"
				       							style="width:90px !important;text-align:right" maxlength="15" onchange="changeLabelValuesE(this.id, '')" onkeypress="return isNumberKey(event)"/>
				       						<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(1) %>" value="<%=cinnerlist.get(8) %>">	
					       					
					       					<% if(uF.parseToBoolean((String)cinnerlist.get(12))) { %>
					       						<input type="hidden" id="hideIsDisplay_<%=(String)cinnerlist.get(1) %>" name="hideIsDisplay" value="<%=(String)cinnerlist.get(0)%>"/>
						       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>" value="true" checked="checked" onclick="makeZeroOnUncheck(this.id);" />
						       				<%}else{%>
								       			<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>" onclick="makeZeroOnUncheck(this.id);" value="true" />
						       				<%}%>
					       				</div>
					       				
					       				<%if(cinnerlist.get(4)!=null && ((String)cinnerlist.get(4)).trim().equals("P")) { %>
								      	 	<div id="col2" style="float: right;">
						       					<label id="lbl_amount_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(7) %></label>
									      	 	 % of <label id="lblMulCal_<%=(String)cinnerlist.get(1) %>">[<%=uF.showData(""+cinnerlist.get(15),"") %>]</label>
						       				</div>
						       			<%} %>
					       					
				       				</div>
				       				<div class="clr"></div>
			       					<%}%>
			       				
		   					<% } %>
		   					
								<div class="row" style="text-align:center; float: left; font-size: 16px; color: #298CE9;<%=((!isGRoss)?"":"display:none;")%>">
									<label >Total Deduction:</label>
									<label id= "total_deduction_value" style="color: green;">0</label>
								</div>
							
						    <div class="clr"></div>
		   				</div>
					</div>
		       	<!-- </form> -->
		       	<div class="clr"></div>
		       	<div style="float: left; width: 100%;">This salary has to be approved again in salary approval</div>
		       	
		  <div class="clr"></div>
		  <div class="netamount" style="display:none;">
		     
		      <div class="netvalue"> 
		      		<label class="tdDashLabel_net" id="lblNetAmountRs">Net Amount Rs:</label> 
		      		<s:label id="lbl_net_amount" name="lbl_net_amount" cssStyle="color: green;"></s:label>
		      </div>
		       
		  </div>
		
		</div>
	<% } %>	
<!-- </div> -->


<%-- <script>
$(document).ready(function() {
	calculateTotalEarningandDeduction();
});
</script> --%>