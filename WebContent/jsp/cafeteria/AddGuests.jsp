<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
function isOnlyNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	      return true;
	   }
	   return false; 
	}
</script>
<%
	String dishId = (String)request.getAttribute("dishId");
	
%>
<div style="float:left;width:100%;">	
   <s:form name ="frmAddGuests" id = "frmAddGuests" action = "AddGuests" method= "POST" theme ="simple">
		<input type = "hidden" name="dishId" value = "<%=dishId %>"/>
		<div style="float:left;margin-top:10px;">
			<table class="table table_no_border form-table">
				<tr>
					<td class="txtlabel alignRight" style="width: 100px;">Select Employee:<sup>*</sup></td>
					<td>
						<s:select name="strEmpIds" id="strEmpIds" listKey="employeeId"  cssStyle="float:left;" listValue="employeeName" headerKey="" headerValue="Select Employee"
							list="empList" key="" required="true" cssClass="validateRequired"/>
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight" style="width: 90px;">Guest Name:<sup>*</sup></td>
					<td><input type="text" name="strGuestName" id = "strGuestName" style="float:left;" class="validateRequired"></td>
				</tr>
				<tr>
					<td class="txtlabel alignRight" style="width: 90px;">Quantity:<sup>*</sup></td>
					<td><input type="text" name="strQuantity" id = "strQuantity" style="float:left;" onkeypress="return isOnlyNumberKey(event)" class="validateRequired"></td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="submit" class="btn btn-primary" name="strSubmit" value="Order Confirm">
	    				<!-- <input type="button" class="btn btn-danger" name="strCancel" onclick="closeAddGuestPopup();" value="Cancel"> -->
	    		   </td>
				</tr>
			</table>
		</div>
		
   </s:form>
 </div>
	
<script>
	$("input[name='strSubmit']").click(function(){
		$(".validateRequired").prop('required',true);
	}); 
</script>