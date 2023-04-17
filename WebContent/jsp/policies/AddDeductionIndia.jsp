<%@page import="com.konnect.jpms.select.FillState"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>	
	function isNumberKey(evt)
	{
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	
	$(function() {
        
        $("input[type='submit'").click(function(){
    		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
       	});
    });
	
</script>
 
<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	if(CF==null)return;

	UtilityFunctions uF =new UtilityFunctions();
	
	String strEmpType = (String) session.getAttribute("USERTYPE");
%>

 
	<s:form theme="simple" action="AddDeductionIndia" method="POST" cssClass="formcss" id="formAddNewRow">
	
		<s:hidden name="deductionId" />
		<s:hidden name="operation" />
		<s:hidden name="financialYear" id="financialYear" />
		<s:hidden name="userscreen" id="userscreen" />
		<s:hidden name="navigationId" id="navigationId" />
		<s:hidden name="toPage" id="toPage" />
	
		<table class="table table_no_border">
			<tr>
				<td colspan=2><s:fielderror /></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Income from:<sup>*</sup></td>
				<td class=""><s:textfield name="incomeFrom" id="incomeFrom" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="incomeFrom" id="incomeFrom" rel="0" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">This field is used to calculate the deduction amount. Income from is the lower slab.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
	
			<tr>
				<td class="txtlabel alignRight">Income to:<sup>*</sup></td>
				<td class=""><s:textfield name="incomeTo" id="incomeTo" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="incomeTo" id="incomeTo" rel="1" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">This field is used to calculate the deduction amount. Income to is the upper slab.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
	
			<tr>
				<td class="txtlabel alignRight">Deduction amount per paycycle:<sup>*</sup></td>
				<td class=""><s:textfield name="deductionAmountPaycycle" id="deductionAmountPaycycle" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="deductionAmountPaycycle" id="deductionAmountPaycycle" rel="2" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">This is the actual amount which will be deducted from net income every paycycle.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Total deduction amount:<sup>*</sup></td>
				<td class=""><s:textfield name="deductionAmount" id="deductionAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="deductionAmount" id="deductionAmount" rel="3" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">This is the total amount which will be deducted from net income based on the slabs.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			<tr>
				<td class="alignRight">Select gender:<sup>*</sup></td>
				<td>
					<s:select name="gender" id="gender" cssClass="validateRequired" list="genderList" listKey="genderId" listValue="genderName"/>
					<span class="hint">Select the gender for this deduction.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">Select State:<sup>*</sup></td>
				<td>
					<select rel="4" name="state" id="state" class="validateRequired">
							<%
								java.util.List  stateList = (java.util.List) request.getAttribute("stateList");
							%>
							<% for (int i=0; i<stateList.size(); i++) { %>
							<option value=<%= ((FillState)stateList.get(i)).getStateId() %>
								<% if(uF.parseToInt(((FillState)stateList.get(i)).getStateId()) == uF.parseToInt((String)request.getAttribute("state"))) { %>
								selected
								<% } %>> <%= ((FillState)stateList.get(i)).getStateName() %></option>
							<% } %>
					</select>
				<span class="hint">Select state for this deduction.<span class="hint-pointer">&nbsp;</span></span>
				</td>
				</tr>
	
			<tr>
			
			<tr>	
				<td>
				<input type="hidden" name="strFinancialYearFrom" value="<%=request.getAttribute("strFinancialYearFrom")%>"/>
				<input type="hidden" name="strFinancialYearTo" value="<%=request.getAttribute("strFinancialYearTo")%>"/>
				</td>
				<td><s:submit cssClass="btn btn-primary" value="Ok" id="btnAddNewRowOk"/> 
				
				</td>
			</tr>
			
		</table>
	</s:form>
<script>
	$("#formAddNewRow").submit(function(event){
		event.preventDefault();
		var financialYear = document.getElementById("financialYear").value;
		var form_data = $("#formAddNewRow").serialize();
		$.ajax({
			type : 'POST',
			url  : 'AddDeductionIndia.action',
			data : form_data,
			success:function(result){
				$("#actionResult").html(result);
			},
			error: function(result){
				$.ajax({
					url: 'DeductionReportIndia.action?financialYear='+financialYear,
					cache: true,
					success: function(result){
						$("#actionResult").html(result);
			   		}
				});
			}
		});
	});
   
</script>