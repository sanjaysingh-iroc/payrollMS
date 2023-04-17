<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<% 
	UtilityFunctions uF = new UtilityFunctions(); 
	String strTitle = (String) request.getAttribute(IConstants.TITLE); 
	
	List<Map<String, String>> alEmp = (List<Map<String, String>>) request.getAttribute("alEmp");
	if(alEmp == null) alEmp = new ArrayList<Map<String,String>>();
	List<Map<String, String>> alCondition = (List<Map<String, String>>) request.getAttribute("alCondition");
	if(alCondition == null) alCondition = new ArrayList<Map<String,String>>();
	List<Map<String, String>> alLogic = (List<Map<String, String>>) request.getAttribute("alLogic");
	if(alLogic == null) alLogic = new ArrayList<Map<String,String>>();
	Map<String, Map<String, String>> hmConditionDayEmpCal =(Map<String, Map<String, String>>) request.getAttribute("hmConditionDayEmpCal");
	if(hmConditionDayEmpCal == null) hmConditionDayEmpCal = new HashMap<String, Map<String, String>>();
	Map<String, Map<String, String>> hmConditionHourEmpCal = (Map<String, Map<String, String>>) request.getAttribute("hmConditionHourEmpCal");
	if(hmConditionHourEmpCal == null) hmConditionHourEmpCal = new HashMap<String, Map<String, String>>();
	Map<String, Map<String, String>> hmConditionAchievedEmpCal = (Map<String, Map<String, String>>) request.getAttribute("hmConditionAchievedEmpCal");
	if(hmConditionAchievedEmpCal == null) hmConditionAchievedEmpCal = new HashMap<String, Map<String, String>>();
	Map<String, String> hmPaymentLogicAmt = (Map<String, String>) request.getAttribute("hmPaymentLogicAmt");
	if(hmPaymentLogicAmt == null) hmPaymentLogicAmt = new HashMap<String, String>();
	Map<String, List<Map<String, String>>> hmConditionLogic = (Map<String, List<Map<String, String>>>) request.getAttribute("hmConditionLogic");
	if(hmConditionLogic == null) hmConditionLogic = new HashMap<String, List<Map<String, String>>>();
	List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
	if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>();
	Map<String, String> hmAllowance = (Map<String, String>) request.getAttribute("hmAllowance");
	if(hmAllowance == null) hmAllowance = new HashMap<String, String>();
	Map<String, String> hmAllowanceId = (Map<String, String>) request.getAttribute("hmAllowanceId");
	if(hmAllowanceId == null) hmAllowanceId = new HashMap<String, String>();
	Map<String, String> hmAllowanceValue = (Map<String, String>) request.getAttribute("hmAllowanceValue");
	if(hmAllowanceValue == null) hmAllowanceValue = new HashMap<String, String>();
	Map<String, String> hmAssignConditionAmt = (Map<String, String>) request.getAttribute("hmAssignConditionAmt");
	if(hmAssignConditionAmt == null) hmAssignConditionAmt = new HashMap<String, String>();
	Map<String, String> hmAssignLogicAmt = (Map<String, String>) request.getAttribute("hmAssignLogicAmt");
	if(hmAssignLogicAmt == null) hmAssignLogicAmt = new HashMap<String, String>();
	Map<String, String> hmCalFrom = (Map<String, String>) request.getAttribute("hmCalFrom");
	if(hmCalFrom == null) hmCalFrom = new HashMap<String, String>();
	Map<String, Map<String, String>> hmConditionAbsentDayEmpCal =(Map<String, Map<String, String>>) request.getAttribute("hmConditionAbsentDayEmpCal");
	if(hmConditionAbsentDayEmpCal == null) hmConditionAbsentDayEmpCal = new HashMap<String, Map<String, String>>();
	
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
	String f_level = (String)request.getAttribute("f_level"); 
	String isCustomPercentage = (String)request.getAttribute("isCustomPercentage");
	String f_salaryhead = (String)request.getAttribute("f_salaryhead"); 
	String isProductionLine = (String)request.getAttribute("isProductionLine");
	String productionLineId = (String)request.getAttribute("productionLineId");
	String isSalaryHeadProdLine = (String)request.getAttribute("isSalaryHeadProdLine");
%>

<script type="text/javascript">
	$(function(){
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		
		$("body").on('click','#closeButton',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
	    });
		$("body").on('click','.close',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
		});
	});    

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

var roundOffCondition='<%=(String)request.getAttribute("roundOffCondition") %>';

function calAllowance(strEmpId) {
	<%
	for(int j = 0; j < alCondition.size(); j++) {
		Map<String,String> hmCondition = alCondition.get(j);
		if(hmCondition == null) hmCondition = new HashMap<String, String>();
		
		List<Map<String, String>> alInner = (List<Map<String, String>>) hmConditionLogic.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
		if(alInner == null) alInner = new ArrayList<Map<String,String>>();
		//System.out.println("ALLOWANCE_CONDITION_TYPE ===>> " + uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")));
		if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ID) {
			//System.out.println("in A_NO_OF_DAYS_ID");
%>
			var nDays = document.getElementById("idStrDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
			var min = '<%=hmCondition.get("MIN_CONDITION")%>';
			var max = '<%=hmCondition.get("MAX_CONDITION")%>';
			var calFrom = '<%=hmCondition.get("CALCULATE_FROM")%>';
			if(parseInt(min) <= parseInt(nDays) && parseInt(nDays) <= parseInt(max)) {
				<%
					for(int k = 0; k < alInner.size(); k++) {
						Map<String,String> hmLogic = alInner.get(k);
						if(hmLogic == null) hmLogic = new HashMap<String, String>();
						if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
				%>			
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(<%=hmLogic.get("FIXED_AMOUNT")%>).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(<%=hmLogic.get("FIXED_AMOUNT")%>).toFixed(2);
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_DAYS_ID) { %>
						var fixedAmt = '<%=hmLogic.get("FIXED_AMOUNT") %>';
						var amt = parseFloat(fixedAmt) * parseFloat(nDays);
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_DAY_ID) { %>
						var nDiff = (parseInt(nDays) - parseInt(calFrom));
						//nDiff = parseInt(nDiff) + 1;
						var fixedAmt = '<%=hmLogic.get("FIXED_AMOUNT") %>';
						var perDayAmt = '<%=hmLogic.get("PER_HOUR_DAY_AMOUNT") %>';
						var amt = (parseFloat(fixedAmt) + (parseFloat(perDayAmt) * parseFloat(nDiff)));
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
						var amt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_DAYS_ID) { %>
						var salaryHeadAmt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
						var amt = parseFloat(salaryHeadAmt) * parseFloat(nDays);
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
				<%	}
				}
				%>
			} else {
				//alert('Please enter proper days.');
				document.getElementById("idStrDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
			<%
				for(int k = 0; k < alInner.size(); k++) {
					Map<String,String> hmLogic = alInner.get(k);
					if(hmLogic == null) hmLogic = new HashMap<String, String>();
					if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
			%>			
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_DAYS_ID) { %>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_DAYS_ID) { %>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_DAY_ID) { %>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
				<%	}
				}
				%>
			}	
<%		} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_HOURS_ID) { 
	//System.out.println("in A_NO_OF_HOURS_ID");
%>
			var nHours = document.getElementById("idStrHour_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
			var min = '<%=hmCondition.get("MIN_CONDITION")%>';
			var max = '<%=hmCondition.get("MAX_CONDITION")%>';
			var calFrom = '<%=hmCondition.get("CALCULATE_FROM")%>';
			//alert("nHours ===>> " + nHours + " --- min ===>> " + min + " --- max ===>> " + max + " --- calFrom ===>> " + calFrom);
			if(parseFloat(min) <= parseFloat(nHours) && parseFloat(nHours) <= parseFloat(max)){
				<%
					for(int k = 0; k < alInner.size(); k++) {
						Map<String,String> hmLogic = alInner.get(k);
						if(hmLogic == null) hmLogic = new HashMap<String, String>();
						//System.out.println("ALLOWANCE_PAYMENT_LOGIC_ID ===>> " + uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")));
						if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
				%>			
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(<%=hmLogic.get("FIXED_AMOUNT")%>).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(<%=hmLogic.get("FIXED_AMOUNT")%>).toFixed(2);
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_HOURS_ID) { %>
						var fixedAmt = '<%=hmLogic.get("FIXED_AMOUNT") %>';
						var amt = parseFloat(fixedAmt) * parseFloat(nHours);
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_HOUR_ID) { %>
						var dblDiff = (parseFloat(nHours) - parseFloat(calFrom));
						//alert("dblDiff ===>> " + dblDiff);
						//dblDiff = parseFloat(dblDiff) + 1;
						var fixedAmt = '<%=hmLogic.get("FIXED_AMOUNT") %>';
						var perHourAmt = '<%=hmLogic.get("PER_HOUR_DAY_AMOUNT") %>';
						var amt = (parseFloat(fixedAmt) + (parseFloat(perHourAmt) * parseFloat(dblDiff)));
						//alert("amt ===>> " + amt+ " -- fixedAmt ===>> " + fixedAmt + " -- perHourAmt ===>> " + perHourAmt);
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
						var amt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);	
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_HOURS_ID) { %>
						var salaryHeadAmt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
						var amt = parseFloat(salaryHeadAmt) * parseFloat(nHours);
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
				<%	}
				}
				%>
			} else {
				//alert('Please enter proper hours.');
				document.getElementById("idStrHour_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
				<%
				for(int k = 0; k < alInner.size(); k++) {
					Map<String,String> hmLogic = alInner.get(k);
					if(hmLogic == null) hmLogic = new HashMap<String, String>();
					if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
			%>			
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_HOURS_ID) { %>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';	
				<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_HOURS_ID) { %>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
				<%  } else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_HOUR_ID) { %>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
			<%	   }
				}
				%>
			}	
		<% } else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID) { 
			//System.out.println("in A_CUSTOM_FACTOR_ID");
		%>
			var amtPercentageType = '<%=hmCondition.get("CUSTOM_FACTOR_TYPE")%>';
			var amtPercentage = document.getElementById("idStrCustomAmtPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
			if(parseFloat(amtPercentage) > 0){
			<%	for(int k = 0; k < alInner.size(); k++) {
					Map<String,String> hmLogic = alInner.get(k);
					if(hmLogic == null) hmLogic = new HashMap<String, String>();
					if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
			%>			
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(<%=hmLogic.get("FIXED_AMOUNT")%>).toFixed(2);
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(<%=hmLogic.get("FIXED_AMOUNT")%>).toFixed(2);
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_CUSTOM_ID) { %>
					var fixedAmt = '<%=hmLogic.get("FIXED_AMOUNT") %>';
					var amt = 0;
					if(amtPercentageType == 'A'){
						amt = parseFloat(fixedAmt) * parseFloat(amtPercentage);
					} else if(amtPercentageType == 'P'){
						amt = (parseFloat(fixedAmt) * parseFloat(amtPercentage))/100;
					} 
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
					var amt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_CUSTOM_ID) { %>
					var salaryHeadAmt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
					var amt = 0;
					if(amtPercentageType == 'A'){
						amt = parseFloat(salaryHeadAmt) * parseFloat(amtPercentage);
					} else if(amtPercentageType == 'P'){
						amt = (parseFloat(salaryHeadAmt) * parseFloat(amtPercentage))/100;
					} 
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
			<%	}
				} %>
			} else {
				//alert('Please enter proper custum amount/percentage.');
				document.getElementById("idStrCustomAmtPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
				<%	for(int k = 0; k < alInner.size(); k++){
					Map<String,String> hmLogic = alInner.get(k);
					if(hmLogic == null) hmLogic = new HashMap<String, String>();
					if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
			%>			
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_CUSTOM_ID) { %>
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_CUSTOM_ID) { %>
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
			<%	}
			} %>
			}
		<% } else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID 
				|| uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_KRA_ID) { 
			//System.out.println("in A_GOAL_KRA_TARGET_ID -- A_KRA_ID");
				%>
			var amtPercentage = document.getElementById("idStrGoalPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
			var min = '<%=hmCondition.get("MIN_CONDITION")%>';
			var max = '<%=hmCondition.get("MAX_CONDITION")%>';
			if(parseFloat(min) <= parseFloat(amtPercentage) && parseFloat(amtPercentage) <= parseFloat(max)) {
				
			<%	for(int k = 0; k < alInner.size(); k++) {
					Map<String,String> hmLogic = alInner.get(k);
					if(hmLogic == null) hmLogic = new HashMap<String, String>();
					if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
			%>			
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(<%=hmLogic.get("FIXED_AMOUNT")%>).toFixed(2);
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(<%=hmLogic.get("FIXED_AMOUNT")%>).toFixed(2);
					
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_ACHIEVED_ID) { %>
					var fixedAmt = '<%=hmLogic.get("FIXED_AMOUNT") %>';
					var amt = parseFloat(fixedAmt) * parseFloat(amtPercentage);
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
					
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
					var amt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
					
			<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_ACHIEVED_ID) { %>
					var salaryHeadAmt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
					var amt = parseFloat(salaryHeadAmt) * parseFloat(amtPercentage);
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
			<%	}
			} %>
		} else {
			//alert('Please enter proper custum amount/percentage.');
			document.getElementById("idStrGoalPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
			<%	for(int k = 0; k < alInner.size(); k++) {
				Map<String,String> hmLogic = alInner.get(k);
				if(hmLogic == null) hmLogic = new HashMap<String, String>();
			%>			
				document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
				document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
			<%	} %>
		}
	<%} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ABSENT_ID) {
		//System.out.println("in A_NO_OF_DAYS_ABSENT_ID");
		%>
		var nDays = document.getElementById("idStrAbsentDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
		var min = '<%=hmCondition.get("MIN_CONDITION")%>';
		var max = '<%=hmCondition.get("MAX_CONDITION")%>';
		var calFrom = '<%=hmCondition.get("CALCULATE_FROM")%>';
		if(parseInt(min) <= parseInt(nDays) && parseInt(nDays) <= parseInt(max)) {
			<%
				for(int k = 0; k < alInner.size(); k++) {
					Map<String,String> hmLogic = alInner.get(k);
					if(hmLogic == null) hmLogic = new HashMap<String, String>();
					if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_DEDUCTION_ID) { %>
						var fixedAmt = '<%=hmLogic.get("FIXED_AMOUNT") %>';
						var salaryHeadAmt = document.getElementById("idSalaryHeadAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
						var amt = 0;
						<%if(!uF.parseToBoolean(hmLogic.get("IS_DEDUCT_FULL_AMOUNT"))){ %>
							amt = parseFloat(salaryHeadAmt) - parseFloat(fixedAmt);
							if(parseFloat(amt) < 0){
								amt = 0;	
							}
						<%}%>
						document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = parseFloat(amt).toFixed(2);
						document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = parseFloat(amt).toFixed(2);
				<%	}
			}
			%>
		} else {
			//alert('Please enter proper days.');
			document.getElementById("idStrAbsentDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
		<%
			for(int k = 0; k < alInner.size(); k++) {
				Map<String,String> hmLogic = alInner.get(k);
				if(hmLogic == null) hmLogic = new HashMap<String, String>();
				if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_DEDUCTION_ID) { %>
					document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value = '';
					document.getElementById("idSpanStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).innerHTML = '';
			<%	}
			}
			%>
		}	
<%		}
		%>
	<%}  %>
	calAllowanceTotal(strEmpId);
}


function calAllowanceTotal(strEmpId) {
	var totalAmt = 0;
	
	var dayFlag = false;
	var hoursFlag = false;
	var isDay = false;
	var isHours = false;
	var isCustom = false;
	var isGoal = false;
	<%
	
		for(int j = 0; j < alCondition.size(); j++) {
			Map<String,String> hmCondition = alCondition.get(j);
			if(hmCondition == null) hmCondition = new HashMap<String, String>();
			
			List<Map<String, String>> alInner = (List<Map<String, String>>) hmConditionLogic.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
			if(alInner == null) alInner = new ArrayList<Map<String,String>>();
			
			if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ID) {
				//System.out.println("in TOTAL ===>> A_NO_OF_DAYS_ID");
	%>
				isDay = true;
				var nDays = document.getElementById("idStrDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
				var min = '<%=hmCondition.get("MIN_CONDITION")%>';
				var max = '<%=hmCondition.get("MAX_CONDITION")%>';
				if(parseInt(min) <= parseInt(nDays) && parseInt(nDays) <= parseInt(max)) {
					dayFlag = true;
					<%
						for(int k = 0; k < alInner.size(); k++) {
							Map<String,String> hmLogic = alInner.get(k);
							if(hmLogic == null) hmLogic = new HashMap<String, String>();
							if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
					%>			
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_DAYS_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_DAYS_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_DAY_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	}
					}
					%>
				} else {
					//alert('Please enter proper days.');
					dayFlag = false;
					document.getElementById("idStrDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
				}	
	<%		} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_HOURS_ID) { 
		//System.out.println("in TOTAL ===>> A_NO_OF_HOURS_ID");
	%>
				isHours = true;
				var nHours = document.getElementById("idStrHour_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
				var min = '<%=hmCondition.get("MIN_CONDITION")%>';
				var max = '<%=hmCondition.get("MAX_CONDITION")%>';
				if(parseFloat(min) <= parseFloat(nHours) && parseFloat(nHours) <= parseFloat(max)){
					hoursFlag = true;
					<%
						for(int k = 0; k < alInner.size(); k++) {
							Map<String,String> hmLogic = alInner.get(k);
							if(hmLogic == null) hmLogic = new HashMap<String, String>();
							if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
					%>			
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_HOURS_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_HOURS_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_HOUR_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	}
					}
					%>
				} else {
					//alert('Please enter proper hours.');
					hoursFlag = false;
					document.getElementById("idStrHour_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
				}	
			<%} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID) { 
				//System.out.println("in TOTAL ===>> A_CUSTOM_FACTOR_ID");
			%>
				isCustom = true;
				var amtPercentageType = '<%=hmCondition.get("CUSTOM_FACTOR_TYPE")%>';
				var amtPercentage = document.getElementById("idStrCustomAmtPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
				if(parseFloat(amtPercentage) > 0) {
				<%	for(int k = 0; k < alInner.size(); k++) {
						Map<String,String> hmLogic = alInner.get(k);
						if(hmLogic == null) hmLogic = new HashMap<String, String>();
						if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
				%>			
							totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
					<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_CUSTOM_ID) { %>
							totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
					<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
							totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
					<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_CUSTOM_ID) { %>
							totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
					<%	}
				} %>
				} else {
					//alert('Please enter proper custum amount/percentage.');
					document.getElementById("idStrCustomAmtPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
				}
			<% } else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID 
					|| uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_KRA_ID) { %>
				isGoal = true;
				var amtPercentage = document.getElementById("idStrGoalPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
				var min = '<%=hmCondition.get("MIN_CONDITION")%>';
				var max = '<%=hmCondition.get("MAX_CONDITION")%>';
				if(parseFloat(min) <= parseFloat(amtPercentage) && parseFloat(amtPercentage) <= parseFloat(max)) {
				<%	for(int k = 0; k < alInner.size(); k++) {
						Map<String,String> hmLogic = alInner.get(k);
						if(hmLogic == null) hmLogic = new HashMap<String, String>();
						if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID) {
				%>			
							totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
					<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_ACHIEVED_ID) { %>
							totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
					<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID) { %>
							totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
					<%	} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_ACHIEVED_ID) { %>
							totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
					<%	}
				} %>
				} else {
					//alert('Please enter proper custum amount/percentage.');
					document.getElementById("idStrGoalPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
				}
			<%} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ABSENT_ID) { 
				//System.out.println("in TOTAL ===>> A_NO_OF_DAYS_ABSENT_ID");
				%>
				var nDays = document.getElementById("idStrAbsentDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
				var min = '<%=hmCondition.get("MIN_CONDITION")%>';
				var max = '<%=hmCondition.get("MAX_CONDITION")%>';
				var calFrom = '<%=hmCondition.get("CALCULATE_FROM")%>';
				if(parseInt(min) <= parseInt(nDays) && parseInt(nDays) <= parseInt(max)) {
					<%
						for(int k = 0; k < alInner.size(); k++) {
							Map<String,String> hmLogic = alInner.get(k);
							if(hmLogic == null) hmLogic = new HashMap<String, String>();
							if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_DEDUCTION_ID) { %>
								totalAmt =  parseFloat(totalAmt) + parseFloat(parseFloat(document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value).toFixed(2));
						<%	}
					}
					%>
				} else {
				<%
					for(int k = 0; k < alInner.size(); k++) {
						Map<String,String> hmLogic = alInner.get(k);
						if(hmLogic == null) hmLogic = new HashMap<String, String>();
						if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_DEDUCTION_ID) { %>
							document.getElementById("idStrAbsentDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
					<%	}
					}
					%>
				}	
	<%		}
			%>
	<%}  %>
	//document.getElementById("idAllowanceAmt_"+strEmpId).value = parseFloat(totalAmt).toFixed(2);
	if(isDay && isHours && isCustom && (!dayFlag || !hoursFlag)){
		totalAmt=0;
	}
	//document.getElementById("idAllowanceAmt_"+strEmpId).value = Math.round(parseFloat(totalAmt));
	document.getElementById("idAllowanceAmt_"+strEmpId).value = getRoundOffValue(Math.round(parseFloat(totalAmt)));
}


function updateAllowance(strEmpId) {
	var salaryHeadId = document.getElementById("f_salaryhead").value;
	var paycycle = document.getElementById("paycycle").value;
	var amount = document.getElementById("idAllowanceAmt_"+strEmpId).value;
	var conditionId = '';
	var conditionAmt = '';
	var logicId = '';
	var logicAmt = '';
	var productionLineId = '';
	if(document.getElementById("productionLineId")){
		productionLineId = document.getElementById("productionLineId").value;
	}
	
	<%
	int a = 0;
	for(int j = 0; j < alCondition.size(); j++){
		Map<String,String> hmCondition = alCondition.get(j);
		if(hmCondition == null) hmCondition = new HashMap<String, String>();
		if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ID) {
	%>		
			var conId = '<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>';
			var amt = document.getElementById("idStrDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
			if(amt == ''){
				amt = 0;
			}
			<%if(a == 0){%>
				conditionId += ''+conId;
				conditionAmt += ''+parseFloat(amt);
			<%} else {%>
				conditionId += ','+conId;
				conditionAmt += ','+parseFloat(amt);
			<%}
			a++;%>
	<%	} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_HOURS_ID) { %>
			var conId = '<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>';
			var amt = document.getElementById("idStrHour_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
			if(amt == ''){
				amt = 0;
			}
			<%if(a == 0){%>
				conditionId += ''+conId;
				conditionAmt += ''+parseFloat(amt);
			<%} else {%>
				conditionId += ','+conId;
				conditionAmt += ','+parseFloat(amt);
			<%}
			a++;%>
	<%	} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID) { %>
			var conId = '<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>';
			var amt = document.getElementById("idStrCustomAmtPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
			if(amt == ''){
				amt = 0;
			}
			<%if(a == 0){%>
				conditionId += ''+conId;
				conditionAmt += ''+parseFloat(amt);
			<%} else {%>
				conditionId += ','+conId;
				conditionAmt += ','+parseFloat(amt);
			<%}
			a++;%>
	<% 	} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID 
				|| uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_KRA_ID) { %>
			var conId = '<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>';
			var amt = document.getElementById("idStrGoalPercentage_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
			if(amt == ''){
				amt = 0;
			}
			<%if(a == 0){%>
				conditionId += ''+conId;
				conditionAmt += ''+parseFloat(amt);
			<%} else {%>
				conditionId += ','+conId;
				conditionAmt += ','+parseFloat(amt);
			<%}
			a++;%>
	<% 	} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ABSENT_ID) {
		%>		
		var conId = '<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>';
		var amt = document.getElementById("idStrAbsentDays_"+strEmpId+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value;
		if(amt == ''){
			amt = 0;
		}
		<%if(a == 0){%>
			conditionId += ''+conId;
			conditionAmt += ''+parseFloat(amt);
		<%} else {%>
			conditionId += ','+conId;
			conditionAmt += ','+parseFloat(amt);
		<%}
		a++;%>
<%	} %>
<% } %>
	
	
	<%
	a=0;
	for(int k = 0; k < alLogic.size(); k++) {
		Map<String,String> hmLogic = alLogic.get(k);
		if(hmLogic == null) hmLogic = new HashMap<String, String>();
	%>
		var logId = '<%=hmLogic.get("PAYMENT_LOGIC_ID")%>';
		var amt = document.getElementById("idStrLogicAmt_"+strEmpId+"_"+<%=hmLogic.get("PAYMENT_LOGIC_ID")%>).value;
		if(amt == ''){
			amt = 0;
		}
		<%if(a == 0){%>
			logicId += ''+logId;
			logicAmt += ''+parseFloat(amt);
		<%} else {%>
			logicId += ','+logId;
			logicAmt += ','+parseFloat(amt);
		<%}
		a++;%>
	<%}%>
	var action = 'UpdateAllowance.action?strEmpId='+strEmpId+'&salaryHeadId='+salaryHeadId+'&paycycle='+paycycle+'&amount='+amount;
	action +='&conditionId='+conditionId+'&conditionAmt='+conditionAmt;
	action +='&logicId='+logicId+'&logicAmt='+logicAmt+'&productionLineId='+productionLineId;

	getContent('myDiv_'+strEmpId, action);
	
}

function prevAllowance(emp_id,empname) {
	var f_salaryhead=document.getElementById("f_salaryhead").value;
	var productionLineId = '';
	if(document.getElementById("productionLineId")){
		productionLineId = document.getElementById("productionLineId").value;
	}
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Allowance of '+empname);
	 $.ajax({
		url : "PrevAllowance.action?strEmpId="+emp_id+"&SHID="+f_salaryhead+'&productionLineId='+productionLineId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var f_salaryhead = document.getElementById("f_salaryhead").value;
	var f_level = document.getElementById("f_level").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var productionLineId = '';
	if(document.getElementById("productionLineId")){
		productionLineId = document.getElementById("productionLineId").value;
	}
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&f_level='+f_level+'&paycycle='+paycycle 
			+'&f_salaryhead='+f_salaryhead+'&productionLineId='+productionLineId;
	} else if(type == '3') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&f_level='+f_level+'&paycycle='+paycycle;
	} else if(type == '4') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&f_level='+f_level+'&paycycle='+paycycle 
			+'&f_salaryhead='+f_salaryhead;
	}
	var action = 'AllowanceForm.action?f_org='+org+paramValues; 
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}


function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	return exportchoice;
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

function selectall(x,strEmpId){
	var status=x.checked;
	var arr= document.getElementsByName(strEmpId);
	for(i=0;i<arr.length;i++){
	  	arr[i].checked=status;
	}
	
	if(x.checked == true){
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
	}
}

function checkAll(){
	
	var approveAll = document.getElementById("approveAll");		
	var strEmpIds = document.getElementsByName('strEmpIds');
	var cnt = 0;
	var chkCnt = 0;

	for(var i=0;i<strEmpIds.length;i++) {
		cnt++;
		 if(strEmpIds[i].checked) {
			 chkCnt++;
		 }
	 }
	if(parseInt(chkCnt) > 0) {
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
	}
	
	if(parseInt(cnt) == parseInt(chkCnt) && parseInt(chkCnt) > 0) {
		approveAll.checked = true;
	} else {
		approveAll.checked = false;
	}
}

function approveAllowance(){
	if(confirm('Are you sure, you want to approve allowance of selected employee?')){
		document.frm_Allowance.formType.value='approve';
		
		var data = $("#frm_Allowance").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'AllowanceForm.action',
			data: data,
			success: function(result){
	        	$("#divResult").html(result); 
	   		}
		});
	}
}

function importAllowanceHours(){
 	var paycycle = document.getElementById("paycycle").value;
 	var f_org = document.getElementById("f_org").value;
 	var f_level = document.getElementById("f_level").value;
 	
 	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Import Allowance Hours');
	$.ajax({
		url : "ImportAllowanceHours.action?paycycle="+paycycle+"&f_org="+f_org+"&f_level="+f_level,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function calCustomPercentage(val){
	if(isInt(val) || isFloat(val)){
		var strEmpId = document.getElementsByName('strEmpIdApplicable');
		
		for(var i=0;i<strEmpId.length;i++) {
		<%
			for(int j = 0; j < alCondition.size(); j++) {
				Map<String,String> hmCondition = alCondition.get(j);
				if(hmCondition == null) hmCondition = new HashMap<String, String>();

				if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID 
						&& hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").trim().equalsIgnoreCase("P")) { 
		%>		
					document.getElementById("idStrCustomAmtPercentage_"+strEmpId[i].value+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = val;
			<%	}
			} %>
			
			calAllowance(strEmpId[i].value);
		 }
	} else{
		var strEmpId = document.getElementsByName('strEmpIdApplicable');
		
		for(var i=0;i<strEmpId.length;i++) {
		<%
			for(int j = 0; j < alCondition.size(); j++) {
				Map<String,String> hmCondition = alCondition.get(j);
				if(hmCondition == null) hmCondition = new HashMap<String, String>();

				if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID 
						&& hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").trim().equalsIgnoreCase("P")) { 
		%>		
					document.getElementById("idStrCustomAmtPercentage_"+strEmpId[i].value+"_"+<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>).value = '';
			<%	}
			} %>
			
			calAllowance(strEmpId[i].value);
		 }
	}
}

function isFloat(val) {
    var floatRegex = /^-?\d+(?:[.,]\d*?)?$/;
    if (!floatRegex.test(val))
        return false;

    val = parseFloat(val);
    if (isNaN(val))
        return false;
    return true;
}

function isInt(val) {
    var intRegex = /^-?\d+$/;
    if (!intRegex.test(val))
        return false;

    var intVal = parseInt(val, 10);
    return parseFloat(val) == intVal && !isNaN(intVal);
}

function downloadAllowance(){
	if(confirm('Are you sure, you want to download this allowance?')){
		document.frm_Allowance.formType.value='download';
		document.frm_Allowance.submit();
	}
}

</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%> 

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
		<% session.setAttribute(IConstants.MESSAGE, ""); %>	
		<s:form name="frm_Allowance" id="frm_Allowance" action="AllowanceForm" theme="simple" method="post">
			<s:hidden name="formType"></s:hidden>
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
				    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
				    <div class="box-tools pull-right">
				        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organisation</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select name="paycycle" id="paycycle" listKey="paycycleId" headerKey="" headerValue="Select Paycycle" listValue="paycycleName" list="paycycleList" key="" onchange="submitForm('2');"/>
							</div>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerKey="" headerValue="Select Level" listValue="levelCodeName" list="levelList" key="" onchange="submitForm('3');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Allowance Head</p>
								<s:select theme="simple" name="f_salaryhead" id="f_salaryhead" headerKey="" headerValue="Select Allowance Head" listKey="salaryHeadId" listValue="salaryHeadName" onchange="submitForm('4');" list="salaryHeadList"/>
							</div>
							
							<%if(uF.parseToBoolean(isProductionLine) && uF.parseToBoolean(isSalaryHeadProdLine)){ %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Production Line</p>
									<s:select name="productionLineId" id="productionLineId" listKey="productionLineId" listValue="productionLineName" list="productionLineList" headerKey="" headerValue="Select Production Line" key="" onchange="submitForm('2');"/>
								</div>
							<%} %>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Service</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true" />
							</div>	
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
					    		<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
						</div>
					</div>
				</div>
			</div>	
			
			<%if(uF.parseToBoolean(isProductionLine)){ 
				if(uF.parseToInt(f_level) > 0 && uF.parseToInt(productionLineId) > 0){ %>
					<div style="float: left; margin-bottom: 24px;">
						<%if(uF.parseToInt(f_salaryhead) > 0){ %>
							<span id="unApproveSpan">
								<input type="button" name="Submit" class="btn btn-default" value="Approve" onclick="alert('Please select employee for approve allowance.');"/>
							</span>
							<span id="approveSpan" style="display: none;">
								<input type="button" value="Approve" name="Submit" class="btn btn-primary" onclick="approveAllowance();"/>
							</span>
							<span style="margin-left:10px;">
								<a href="javascript:void(0);" onclick="downloadAllowance();">Download</a>
							</span>
						<%} %>
						<%if(uF.parseToBoolean(isCustomPercentage)){ %>
							<span style="margin-left:10px;">
								Percentage: <input type="text" id="customPercentage" name="customPercentage" value="" style="width:51px !important; text-align: right;" onkeyup="calCustomPercentage(this.value);" onkeypress="return isNumberKey(event)"/>
							</span>
						<%} %>
					</div>
								
					<div style="text-align: right;">
						<a href="javascript:void(0)" onclick="importAllowanceHours();">Import Allowance Hours</a>
					</div>
				<%} else if(uF.parseToInt(f_level) > 0){ %>
					<div style="float: left; margin-bottom: 24px;">
						<%if(uF.parseToInt(f_salaryhead) > 0){ %>
							<span id="unApproveSpan">
								<input type="button" name="Submit" class="btn btn-default" value="Approve" onclick="alert('Please select employee for approve allowance.');"/>
							</span>
							<span id="approveSpan" style="display: none;">
								<input type="button" value="Approve" name="Submit" class="btn btn-primary" onclick="approveAllowance();"/>
							</span>
							<span style="margin-left:10px;">
								<a href="javascript:void(0);" onclick="downloadAllowance();">Download</a>
							</span>
						<%} %>
						<%if(uF.parseToBoolean(isCustomPercentage)){ %>
							<span style="margin-left:10px;">
								Percentage: <input type="text" id="customPercentage" name="customPercentage" value="" style="width:51px !important; text-align: right;" onkeyup="calCustomPercentage(this.value);" onkeypress="return isNumberKey(event)"/>
							</span>
						<%} %>
					</div>
								
					<div style="text-align: right;">
						<a href="javascript:void(0)" onclick="importAllowanceHours();">Import Allowance Hours</a>
					</div>	
			<%	}
			} else { 
				if(uF.parseToInt(f_level) > 0){ %>
					<div style="float: left; margin-bottom: 24px;">
						<%if(uF.parseToInt(f_salaryhead) > 0){ %>
							<span id="unApproveSpan">
								<input type="button" name="Submit" class="btn btn-default" value="Approve" onclick="alert('Please select employee for approve allowance.');"/>
							</span>
							<span id="approveSpan" style="display: none;">
								<input type="button" value="Approve" name="Submit" class="btn btn-primary" onclick="approveAllowance();"/>
							</span>
							<span style="margin-left:10px;">
								<a href="javascript:void(0);" onclick="downloadAllowance();">Download</a>
							</span>
						<%} %>
						<%if(uF.parseToBoolean(isCustomPercentage)){ %>
							<span style="margin-left:10px;">
								Percentage: <input type="text" id="customPercentage" name="customPercentage" value="" style="width:51px !important; text-align: right;" onkeyup="calCustomPercentage(this.value);" onkeypress="return isNumberKey(event)"/>
							</span>
						<%} %>
					</div>
								
					<div style="text-align: right;">
						<a href="javascript:void(0)" onclick="importAllowanceHours();">Import Allowance Hours</a>
					</div>
			<%} 
			} %>
				
			<div style="float: left; width: 100%; overflow-x: auto;">
				
				<% if(alEmp.size() > 0 && alCondition.size() > 0  && alLogic.size() > 0){ %>
					<table class="table table-bordered">
					    <tr>	
					   		<th class="alignCenter">Approve<br/><input type="checkbox" name="approveAll" id="approveAll" onclick="selectall(this,'strEmpIds')"/></th>
					    	<th class="alignCenter" nowrap="nowrap">Employee Code</th>
					    	<th class="alignCenter" nowrap="nowrap">Employee Name</th>
					    	<%
					    		for(int j = 0; j < alCondition.size(); j++){ 
					    			Map<String,String> hmCondition = alCondition.get(j);
									if(hmCondition == null) hmCondition = new HashMap<String, String>();
					    	%>
				    			<th class="alignCenter" style="background-color: #efe;" nowrap="nowrap">
				    				<%=hmCondition.get("ALLOWANCE_CONDITION_SLAB") %><br/>
				    				<span style="font-size: 10px;"><%=hmCondition.get("ALLOWANCE_CONDITION") %></span><br/>
				    				<%
									if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID){
										String StrType= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "Percentage";
										String StrTypeStatus= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "%";
									%>
										<span style="font-size: 10px;">Type: <%=StrType %></span>
									<%} else { %>
										<span style="font-size: 10px;">
											<% if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_KRA_ID) { %>Achieved % <% } %>
											Min: <%=hmCondition.get("MIN_CONDITION") %>&nbsp;-&nbsp;
											<% if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_KRA_ID) { %><br/>Achieved % <% } %>
											Max: <%=hmCondition.get("MAX_CONDITION") %>&nbsp;<br/>
										</span>
									<%} %>
				    			</th>
					    	<%} %>
					    	<%
						    	for(int k = 0; k < alLogic.size(); k++) {
									Map<String,String> hmLogic = alLogic.get(k);
									if(hmLogic == null) hmLogic = new HashMap<String, String>();
					    	%>
				    			<th class="alignCenter" style="background-color: #FFFFCC;" nowrap="nowrap">
				    				<%=hmLogic.get("PAYMENT_LOGIC_SLAB") %><br/>
				    				<span style="font-size: 10px;">Condition Name: <%=hmLogic.get("ALLOWANCE_CONDITION") %></span><br/>
				    				<span style="font-size: 10px;">Payment Logic: <%=hmLogic.get("ALLOWANCE_PAYMENT_LOGIC") %></span><br/>
								<% if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_DAYS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_HOURS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_CUSTOM_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_ACHIEVED_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_HOUR_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_DAY_ID) { %>
									<span style="font-size: 10px;">Fixed Amount: <%=hmLogic.get("FIXED_AMOUNT") %></span>
									<%if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_HOUR_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_DAY_ID) { %>
										<br/><span style="font-size: 10px;">Plus amount <%=uF.showData(hmLogic.get("PER_HOUR_DAY_AMOUNT"),"0") %> per Extra Hour above <%=uF.showData(hmCalFrom.get(hmLogic.get("ALLOWANCE_CONDITION_ID")),"0") %> Extra hours</span>
									<%} %>
								<% } else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_DAYS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_HOURS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_CUSTOM_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_ACHIEVED_ID) { %>
									<span style="font-size: 10px;">Salary Head: <%=hmLogic.get("CAL_SALARY_HEAD_NAME") %></span>
								<% } else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_DEDUCTION_ID) {
										if(!uF.parseToBoolean(hmLogic.get("IS_DEDUCT_FULL_AMOUNT"))){
								%>
											<span style="font-size: 10px;">Fixed Amount: <%=hmLogic.get("FIXED_AMOUNT") %></span>	
								<% 		} 
									}
								%>
				    				
				    			</th>
					    	<%} %>
					    	<th class="alignCenter">Allowance Amount</th>
					    	<th class="alignCenter" colspan="2">Action</th>
					    </tr>
			    <%
			    	int cnt = 0;
			    	for(int i = 0; i < alEmp.size(); i++){
			    		cnt++;
			    		Map<String, String> hmEmp = (Map<String, String>)alEmp.get(i);
			    		String strEmpId = hmEmp.get("EMP_ID");
			    		double dblAmt = 0.0d;
			    		String payStatus="0";
            			if(ckEmpPayList.contains(strEmpId)){
            				payStatus="1";
            			}
            			
            			String strStatus = "onblur=\"calAllowance("+strEmpId+");\"";
            			boolean isAssign = false;
	    				if(hmAllowance!=null && hmAllowance.containsKey(strEmpId)){
	    					isAssign = true;
	    					strStatus = " readonly=\"readonly\"";
	    				}
			    %>
				    	<tr>
				    	   <td class="alignCenter">
					    	   <%if(!uF.parseToBoolean(payStatus) && hmAllowance != null && !hmAllowance.containsKey(strEmpId)){%>
					    	   		<input type="checkbox" name="strEmpIds" onclick="checkAll();" style="width:10px; height:10px" value="<%=strEmpId%>"/>
					    	   		<input type="hidden" id="strEmpIdApplicable_<%=i%>" name="strEmpIdApplicable" value="<%=strEmpId%>">
					    	   <%} %>
				    	   </td>
				    		<td class="alignCenter"><%=hmEmp.get("EMP_CODE")%>
					    		<input type="hidden" id="idStrEmpId_<%=i%>" name="strEmpId" value="<%=strEmpId%>">
					    	</td>
					    	<td nowrap="nowrap"><%=hmEmp.get("EMP_NAME")%></td>
					    	<%
					    		for(int j = 0; j < alCondition.size(); j++){
					    			Map<String,String> hmCondition = alCondition.get(j);
									if(hmCondition == null) hmCondition = new HashMap<String, String>();
					    	%>
					    			<td align="right" style="background-color: #efe;">
					    				<%
					    					if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ID) {
					    						Map<String, String> hmEmpDayCnt = (Map<String, String>) hmConditionDayEmpCal.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
					    						if(hmEmpDayCnt == null) hmEmpDayCnt = new HashMap<String, String>();
					    						String strConditionVal = uF.showData(hmEmpDayCnt.get(strEmpId),"");
					    						if(isAssign){
					    							strConditionVal = uF.showData(hmAssignConditionAmt.get(strEmpId+"_"+hmCondition.get("ALLOWANCE_CONDITION_ID")),"");
					    						}
					    				%>
					    						<input style="width:75px !important; text-align: right;" type="text" id="idStrDays_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" name="strDays_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" value="<%=strConditionVal %>" onkeypress="return isNumberKey(event)" <%=strStatus %>/>	
					    				<%
					    					} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_HOURS_ID) {
					    						Map<String, String> hmEmpHourCnt = (Map<String, String>) hmConditionHourEmpCal.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
					    						if(hmEmpHourCnt == null) hmEmpHourCnt = new HashMap<String, String>();
					    						String strConditionVal = uF.showData(hmEmpHourCnt.get(strEmpId),"");
					    						if(isAssign){
					    							strConditionVal = uF.showData(hmAssignConditionAmt.get(strEmpId+"_"+hmCondition.get("ALLOWANCE_CONDITION_ID")),"");
					    						}
					    				%>
					    						<input style="width:75px !important; text-align: right;" type="text" id="idStrHour_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" name="strHours_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" value="<%=strConditionVal %>" onkeypress="return isNumberKey(event)" <%=strStatus %>/>	
					    				<% } else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID) {
						    					String strConditionVal = "";
					    						if(isAssign){
					    							strConditionVal = uF.showData(hmAssignConditionAmt.get(strEmpId+"_"+hmCondition.get("ALLOWANCE_CONDITION_ID")),"");
					    						}
					    				%>
					    						<input style="width:75px !important; text-align: right;" type="text" id="idStrCustomAmtPercentage_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" name="strCustomAmtPercentage_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" value="<%=strConditionVal %>" onkeypress="return isNumberKey(event)" <%=strStatus %>/>
					    				<% } else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_KRA_ID) {
					    					Map<String, String> hmEmpAchieved = (Map<String, String>) hmConditionAchievedEmpCal.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
				    						if(hmEmpAchieved == null) hmEmpAchieved = new HashMap<String, String>();
				    						String strConditionVal = uF.showData(hmEmpAchieved.get(strEmpId),"");
				    						if(isAssign){
				    							strConditionVal = uF.showData(hmAssignConditionAmt.get(strEmpId+"_"+hmCondition.get("ALLOWANCE_CONDITION_ID")),"");
				    						}
				    					%>
				    						<input style="width:75px !important; text-align: right;" type="text" id="idStrGoalPercentage_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" name="strGoalPercentage_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" value="<%=strConditionVal %>" onkeypress="return isNumberKey(event)" readonly="readonly"/>
				    					<% } else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ABSENT_ID) {
				    						Map<String, String> hmEmpAbsentDays = (Map<String, String>) hmConditionAbsentDayEmpCal.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
				    						if(hmEmpAbsentDays == null) hmEmpAbsentDays = new HashMap<String, String>();
				    						String strConditionVal = uF.showData(hmEmpAbsentDays.get(strEmpId),"");
				    						if(isAssign){
				    							strConditionVal = uF.showData(hmAssignConditionAmt.get(strEmpId+"_"+hmCondition.get("ALLOWANCE_CONDITION_ID")),"");
				    						}
				    				%>
				    						<input style="width:75px !important; text-align: right;" type="text" id="idStrAbsentDays_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" name="strAbsentDays_<%=strEmpId%>_<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>" value="<%=strConditionVal %>" onkeypress="return isNumberKey(event)" <%=strStatus %>/>	
				    				<%
				    					} %>
					    			</td>
					    	<%} %>
				    	<%
					    	for(int k = 0; k < alLogic.size(); k++){
								Map<String,String> hmLogic = alLogic.get(k);
								if(hmLogic == null) hmLogic = new HashMap<String, String>();
								
				    	%>
				    			<td align="right" style="background-color: #FFFFCC;">
				    				<%if(isAssign){ %>
				    					<span id="idSpanStrLogicAmt_<%=strEmpId%>_<%=hmLogic.get("PAYMENT_LOGIC_ID")%>">
					    					<%=uF.showData(hmAssignLogicAmt.get(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")),"") %>
					    				</span>
				    				<%} else { %>
					    				<input type="hidden" id="idSalaryHeadAmt_<%=strEmpId%>_<%=hmLogic.get("PAYMENT_LOGIC_ID")%>" name="strSalaryHeadAmt" value="<%=uF.showData(hmPaymentLogicAmt.get(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT"),"0") %>"/>
					    				<input type="hidden" id="idStrLogicAmt_<%=strEmpId%>_<%=hmLogic.get("PAYMENT_LOGIC_ID")%>" name="strLogicAmt_<%=strEmpId%>_<%=hmLogic.get("PAYMENT_LOGIC_ID")%>" value="<%=uF.showData(hmPaymentLogicAmt.get(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")),"0") %>"/>
					    				<span id="idSpanStrLogicAmt_<%=strEmpId%>_<%=hmLogic.get("PAYMENT_LOGIC_ID")%>">
					    					<%if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) != IConstants.A_SALARY_HEAD_X_CUSTOM_ID && uF.parseToBoolean(hmPaymentLogicAmt.get(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE"))){
					    						dblAmt += uF.parseToDouble(hmPaymentLogicAmt.get(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"))); 
					    					%>
					    						<%=uF.showData(hmPaymentLogicAmt.get(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")),"") %>
					    					<%} %>
					    				</span>
				    				<%} %>
								</td>
				    		<%} %>
				    		<td class="alignCenter">
				    			<%
				    				if(hmAllowance!=null && hmAllowance.containsKey(strEmpId)){
				    					dblAmt = uF.parseToDouble(hmAllowanceValue.get(strEmpId));
				    				}
				    			%>
				    			<input style="width:75px !important; text-align: right;" type="text" id="idAllowanceAmt_<%=strEmpId%>" name="strAllowanceAmt_<%=strEmpId%>" value="<%=uF.formatIntoZeroWithOutComma(dblAmt) %>"/>
				    		</td>
					    	<td class="alignCenter" nowrap="nowrap">
					    		<%
						    		if (hmAllowance != null && uF.parseToInt(hmAllowance.get(strEmpId)) == 1) {
						    	%>
						    		<div id="myDiv_<%=strEmpId%>">
							    		<!-- <img src="images1/icons/approved.png" width="17px" /> -->
							    		<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
							    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=hmEmp.get("EMP_NAME")%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=strEmpId%>', 'UpdateAllowance.action?requestid=<%=uF.parseToInt(hmAllowanceId.get(strEmpId))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
							    	</div>
						    	<%
						    		} else if (hmAllowance != null && uF.parseToInt(hmAllowance.get(strEmpId)) == -1) {
						    	%>
						    		<div id="myDiv_<%=strEmpId%>">
							    		<!-- <img src="images1/icons/denied.png" width="17px" /> -->
							    		<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
							    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=hmEmp.get("EMP_NAME")%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=strEmpId%>', 'UpdateAllowance.action?requestid=<%=uF.parseToInt(hmAllowanceId.get(strEmpId))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
							    	</div>
						    	<%
						    		} else if (hmAllowance != null && uF.parseToInt(hmAllowance.get(strEmpId)) == 2) {
						    	%>
						    		<div id="myDiv_<%=strEmpId%>">
							    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=hmEmp.get("EMP_NAME")%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=strEmpId%>', 'UpdateAllowance.action?requestid=<%=uF.parseToInt(hmAllowanceId.get(strEmpId))%>&approval=1&payStatus=<%=payStatus %>&strEmpId=<%=strEmpId%>')<%} %>" width="17px" src="images1/icons/icons/approve_icon.png"/> --%>
							    		<i class="fa fa-check-circle checknew" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=hmEmp.get("EMP_NAME")%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=strEmpId%>', 'UpdateAllowance.action?requestid=<%=uF.parseToInt(hmAllowanceId.get(strEmpId))%>&approval=1&payStatus=<%=payStatus %>&strEmpId=<%=strEmpId%>')<%} %>" ></i>
							    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=hmEmp.get("EMP_NAME")%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=strEmpId%>', 'UpdateAllowance.action?requestid=<%=uF.parseToInt(hmAllowanceId.get(strEmpId))%>&approval=-1&payStatus=<%=payStatus %>&strEmpId=<%=strEmpId%>')<%} %>" width="16px" src="images1/icons/icons/close_button_icon.png"> --%> 
							    		<i class="fa fa-times-circle cross" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=hmEmp.get("EMP_NAME")%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=strEmpId%>', 'UpdateAllowance.action?requestid=<%=uF.parseToInt(hmAllowanceId.get(strEmpId))%>&approval=-1&payStatus=<%=payStatus %>&strEmpId=<%=strEmpId%>')<%} %>"></i>
						    		</div>
						    	<%
						    		} else if (hmAllowance != null && uF.parseToInt(hmAllowance.get(strEmpId)) == 0) {
						    	%>
					    				<div id="myDiv_<%=strEmpId%>"><input type="button" class="btn btn-primary" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=hmEmp.get("EMP_NAME") %>\'s payroll has been processed for this paycycle.');<%} else{%>updateAllowance(<%=strEmpId%>);<%} %>" value="Update"></div>
					    		<%} %>
					    	</td>
					    	<td class="alignCenter" nowrap="nowrap"><a href="javascript:void(0)" onclick="prevAllowance(<%=strEmpId %>,'<%=hmEmp.get("EMP_NAME")%>')">Previous Allowance</a></td>
				    	</tr>
				    <%} %>
					</table>
				<%}else {%>
			    	<div style="width: 92%;" class="msg nodata"><span>No conditions set for the selected Allowance Head</span></div>
			    <%}%> 
			</div>
		</s:form>
	</div>
	<!-- /.box-body -->
        
<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
	    <!-- Modal content-->
	   <div class="modal-content">
	       <div class="modal-header">
	           <button type="button" class="close" data-dismiss="modal">&times;</button>
	           <h4 class="modal-title">-</h4>
	       </div>
	       <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>