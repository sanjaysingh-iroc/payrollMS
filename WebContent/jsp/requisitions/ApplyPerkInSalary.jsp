<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF =  new UtilityFunctions();


String strE = (String)request.getParameter("E"); %> 
<script type="text/javascript">
	$(function () {
		$("input[value='Submit']").click(function(){
			$(".validateRequired").prop('required',true);
		});
		$("#paycycle").multiselect().multiselectfilter();
	});

	function getPerkSalaryPaycycle() {
		document.getElementById("strAmount").value='';
		var financialYear = document.getElementById("financialYear").value;
		var perkSalary = document.getElementById("perkSalary").value;
		if(financialYear != ''){
			var action = 'GetPerkSalaryPaycycle.action?financialYear='+financialYear+'&perkSalary='+perkSalary;
			getContent("tdPaycycle", action);
		}
		window.setTimeout(function() {
			$("#paycycle").multiselect().multiselectfilter();
		 }, 500);
	}

	function getPerkSalary() {
		document.getElementById("strAmount").value='';
		var financialYear = document.getElementById("financialYear").value;
		if(financialYear != ''){
			var action = 'GetPerkSalary.action?financialYear=' + financialYear;
			getContent("tdPerkSalary", action);
			
			window.setTimeout(function() {
				getPerkSalaryPaycycle();
			 }, 700); 
		}
	}
	
	function checkPerkLimit(){
		var limitAmount = 0;
		if(document.getElementById("limitAmount")) {
			limitAmount = document.getElementById("limitAmount").value;
		}
		var strAmount = 0;
		if(document.getElementById("strAmount")){
			strAmount = document.getElementById("strAmount").value;
		}
		
		if(parseFloat(strAmount) == parseFloat('0')){
			alert ("You can not apply 0");
			if(document.getElementById("strAmount")){
				document.getElementById("strAmount").value='';
			}
			//return false;
		} else if(parseFloat(strAmount) > parseFloat(limitAmount) || parseFloat(strAmount) == parseFloat('0')){
			alert ("You can not apply for more than "+limitAmount);
			if(document.getElementById("strAmount")){
				document.getElementById("strAmount").value='';
			}
			//return false;
		}
		//return true;
		
	}
	
	$("#frm_MyPerks").submit(function(event) {
		event.preventDefault();
		var form_data = $("#frm_MyPerks").serialize();
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		
	    $.ajax({
				type : 'POST',
				url: 'ApplyPerkInSalary.action',
				data:form_data+"&submit1=Submit",
				success:function(result) {
					$("#subDivResult").html(result);
				}
	    });
	 
	});

</script>


<div class="box-body">
	<s:form id="formID" theme="simple" name="frm_MyPerks" id="frm_MyPerks" action="ApplyPerkInSalary" enctype="multipart/form-data" method="post" onsubmit="return checkPerkLimit();">
		<table class="table table_no_border form-table">
			<tr>
				<td class="alignRight">Financial Year:<sup>*</sup></td>
				<td><s:select theme="simple" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" cssClass="validateRequired form-control"
						list="financialYearList" key="" onchange="getPerkSalary();"/>
				</td>
			</tr>
			
			<tr>
				<td class="alignRight" valign="top">Perk Salary:<sup>*</sup></td>
				<td id="tdPerkSalary">
					<s:select theme="simple" name="perkSalary" id="perkSalary" listKey="perkSalaryId" listValue="perkSalaryName" list="perkSalaryList" key="" cssClass="validateRequired form-control" 
						onchange="getPerkSalaryPaycycle();"/>
				</td>
			</tr>
								
			<tr>
				<td class="alignRight" valign="top">Paycycle:<sup>*</sup></td>
				<td id="tdPaycycle">
					<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" 
					list="paycycleList" key="" cssClass="validateRequired form-control" multiple="true"/>
					<s:hidden name="limitAmount" id="limitAmount"></s:hidden>
				</td>
			</tr>
			
			<tr>
				<td class="alignRight" valign="top">Amount:<sup>*</sup></td>
				<td>
					<s:textfield name="strAmount" id="strAmount" cssClass="validateRequired form-control" onkeyup="return checkPerkLimit();" 
					onkeypress="return isNumberKey(event)" cssStyle="width: 75px; text-align: right;"/>
				</td> 
			</tr>
			
			<tr>
				<td class="alignRight" valign="top">Description:</td>
				<td><s:textarea rows="5" cols="25" name="strDescription"></s:textarea></td>
			</tr>

			<tr>
				<td class="alignRight">Attach Document:<sup>*</sup></td>
				<td><s:file name="strDocument" cssClass="validateRequired"/></td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" name="submit1" value="Submit" class="btn btn-primary"/></td>
			</tr>
		</table>
	</s:form>
</div>