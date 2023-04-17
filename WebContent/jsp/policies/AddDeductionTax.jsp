<%@page import="com.konnect.jpms.select.FillState"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">
	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	 
	$(function() {
		/* $("#idFinancialYearFrom").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#idFinancialYearTo').datepicker('setStartDate', minDate);
        });
        
        $("#idFinancialYearTo").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#idFinancialYearFrom').datepicker('setEndDate', minDate);
        }); */
        $("input[type='submit'").click(function(){
    		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
       	});
    });
	
	$("#formAddNewRow").submit(function(event) {
		event.preventDefault();
		var financialYear = document.getElementById("financialYear").value;
		var form_data = $("#formAddNewRow").serialize();
		$.ajax({
			type:'POST',
			url :'AddDeductionTax.action',
			data:form_data,
			success:function(data) {
				$("#actionResult").html(data);
			},
			error: function(result){
				$.ajax({
					url: 'DeductionReportTax.action?financialYear='+financialYear,
					cache: true,
					success: function(result){
						$("#actionResult").html(result);
			   		}
				});
			}
		});
	});
	
</script>

 
	<s:form theme="simple" action="AddDeductionTax" method="POST" cssClass="formcss" id="formAddNewRow">
		<s:hidden name="deductionId" />
		<s:hidden name="financialYear" id="financialYear" />
		<s:hidden name="operation" />
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
	
		<table class="table table_no_border form-table">		
			<tr>
				<td class="alignRight">Age from:<sup>*</sup></td>
				<td ><s:textfield name="ageFrom" id="ageFrom" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="ageFrom" id="ageFrom" rel="0" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">Please enter the lower age limit for this deduction slab.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
	
			<tr>
				<td class=" alignRight">Age to:<sup>*</sup></td>
				<td><s:textfield name="ageTo" id="ageTo" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="ageTo" id="ageTo" rel="1" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">Please enter the upper age limit for this deduction slab.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>

			 <tr>
				<td class="alignRight">Select gender:<sup>*</sup></td>
				<td>
					<select name="gender" id="gender" class="validateRequired">
							<% java.util.List  genderList = (java.util.List) request.getAttribute("genderList");
								for (int i=0; i<genderList.size(); i++) { %>
							<option value=<%= ((FillGender)genderList.get(i)).getGenderId() %>
							<% if(((FillGender)genderList.get(i)).getGenderId().equals((String)request.getAttribute("gender"))) { %>
								selected
							<% } %>> <%= ((FillGender)genderList.get(i)).getGenderName() %></option>
							<% } %>
					</select>
				<span class="hint">Select the gender for this deduction slab.<span class="hint-pointer">&nbsp;</span></span>
				</td>
				</tr>
				
				<tr>
				<td class="alignRight">Net income from:<sup>*</sup></td>
				<td><s:textfield name="incomeFrom" id="incomeFrom" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="incomeFrom" id="incomeFrom" rel="2" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">This field is used to calculate the deduction amount. Net icome from is the lower slab.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
	         
			<tr>
				<td class="alignRight">Net income to:<sup>*</sup></td>
				<td><s:textfield name="incomeTo" id="incomeTo" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="incomeTo" id="incomeTo" rel="3" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">This field is used to calculate the deduction amount. Net income to is the upper slab.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="alignRight">Deduction:<sup>*</sup></td>
				<td><s:textfield name="deductionAmount" id="deductionAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<!-- <input type="text" name="deductionAmount" id="deductionAmount" rel="4" class="validateRequired" onkeypress="return isNumberKey(event)"/> -->
				<span class="hint">This field is used to calculate the deduction amount. <span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="alignRight">Select deduction type:<sup>*</sup></td>
				<td>
					<select rel="5" name="deductionType" id="deductionType" class="validateRequired">
							<option value="A" <%=(request.getAttribute("deductionType") != null && ((String)request.getAttribute("deductionType")).equals("A")) ? "selected" : "" %>>Amount</option>
							<option value="P" <%=(request.getAttribute("deductionType") != null && ((String)request.getAttribute("deductionType")).equals("P")) ? "selected" : "" %>>Percent</option>
						</select>
						<span class="hint">Select the deduction type.<br>Amount - fixed amount will be deducted.<br>Percent - %age of the net amount will be deducted<span class="hint-pointer">&nbsp;</span></span>
					</td>
				</tr>
				<tr>
					<td class="alignRight">Select slab type:<sup>*</sup></td>
					<td>
						<select rel="5" name="slabType" id="slabType" class="validateRequired">
								<option value="0" <%=(request.getAttribute("slabType") != null && ((String)request.getAttribute("slabType")).equals("0")) ? "selected" : "" %>>Standard</option>
								<option value="1" <%=(request.getAttribute("slabType") != null && ((String)request.getAttribute("slabType")).equals("1")) ? "selected" : "" %>>New</option>
						</select>
						<span class="hint">Select the slab type.<br>Standard Slab<br>New Slab<span class="hint-pointer">&nbsp;</span></span>
					</td>
				</tr>
			
		<%-- 	<tr>
				<td class="txtlabel alignRight">Financial Year start date<sup>*</sup>:</td>
				<td class="label"><input style="width:100px" style="width:220px" type="text" name="strFinancialYearFrom" id="idFinancialYearFrom" class="required" onkeypress="return isNumberKey(event)" value="<%=CF.getStrFinancialYearFrom()%>"/>
				<span class="hint">Financial Year start date.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Financial Year end date<sup>*</sup>:</td>
				<td class="label"><input style="width:100px" type="text" name="strFinancialYearTo" id="idFinancialYearTo" class="required" onkeypress="return isNumberKey(event)" value="<%=CF.getStrFinancialYearTo()%>"/>
				<span class="hint">Financial Year end date.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr> --%>
	
			<tr>
				
				<td></td>
				<td><s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk1"/> 
				<%-- <s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel1"/> --%>
				</td>
			</tr>
			
		</table>
	</s:form>

	