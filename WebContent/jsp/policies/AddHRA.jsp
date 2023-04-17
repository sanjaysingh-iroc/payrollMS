<%@page import="com.konnect.jpms.select.FillState"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>
	addLoadEvent(prepareInputsForHints);
	
	function isNumberKey(evt)
	{
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	
	$(function() {
        $( "#idFinancialYearFrom" ).datepicker({dateFormat: 'dd/mm/yy'});
    });
	$(function() {
        $( "#idFinancialYearTo" ).datepicker({dateFormat: 'dd/mm/yy'});
    });
</script>
 
<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	if(CF==null)return;

	String strEmpType = (String) session.getAttribute("USERTYPE");
%>

 
<div id="addForm1" style="display: none;">
<s:form theme="simple" action="AddHRA" method="POST" cssClass="formcss" id="formAddNewRow" cssStyle="display: none;">

	<s:hidden name="strHRAId" />

	<table border="0" class="formcss" style="width:675px">
 
		<tr>
			<td colspan=2><s:fielderror /></td>
		</tr> 
		
		<tr>
			<td style="width:220px" class="txtlabel alignRight">Rent paid in excess of:<sup>*</sup></td>
			<td class="label"><input style="width:100px" type="text" name="strCond1" rel="0" class="validateRequired" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This %age of salary will used to calculate the HRA exemption.<span class="hint-pointer">&nbsp;</span></span>
			% of salary			
			</td>
			
		</tr>

		<tr>
			<td class="txtlabel alignRight">% of salary in metro cities:<sup>*</sup></td>
			<td class="label"><input style="width:100px" type="text" name="strCond2" rel="1" class="validateRequired" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This %age of salary will used to calcualte the HRA exemption in metro cities.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">% of salary in other cities:<sup>*</sup></td>
			<td class="label"><input style="width:100px" type="text" name="strCond3" rel="2" class="validateRequired" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This %age of salary will used to calcualte the HRA exemption in other cities.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Financial Year start date:<sup>*</sup></td>
			<td class="label"><input style="width:100px" style="width:220px" type="text" name="strFinancialYearFrom" id="idFinancialYearFrom" class="validateRequired" onkeypress="return isNumberKey(event)" value="<%=CF.getStrFinancialYearFrom()%>"/>
			<span class="hint">Financial Year start date.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Financial Year end date:<sup>*</sup></td>
			<td class="label"><input style="width:100px" type="text" name="strFinancialYearTo" id="idFinancialYearTo" class="validateRequired" onkeypress="return isNumberKey(event)" value="<%=CF.getStrFinancialYearTo()%>"/>
			<span class="hint">Financial Year end date.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
		
		<tr>
			
			<td></td>
			<td><s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk"/> 
			<s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/></td>
		</tr>
		
	</table>
</s:form>
</div>