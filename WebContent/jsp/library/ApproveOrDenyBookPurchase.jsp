<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
#strAmount{
background-image: url('images1/rupee-image.png');
background-position: 4px 6px;
background-size: 10px 15px;
padding-left: 15px !important;
background-repeat: no-repeat;
}
</style>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready( function () {
	$("input[name='strSubmit']").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');
		$(".validateNumber").prop('step','any');
	});
});

</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
%>

<script type="text/javascript">
   
function checkQuantity() { 
	   var quantityAvail = document.getElementById("availQuantity").value;
	   var appQuantity = document.getElementById("strQuantityApproved").value;
	   var reqQuantity = document.getElementById("reqQuantity").value;
	 	   if(appQuantity!="" && parseInt(appQuantity) <= 0) {
		   alert("Invalid quantity!");
		   document.getElementById("strQuantityApproved").value = '';
	   } else if(reqQuantity!=null && parseInt(appQuantity) > parseInt(reqQuantity) && parseInt(reqQuantity) <= parseInt(quantityAvail)){
		   alert("Requested : "+ reqQuantity+" copies only.");
		   document.getElementById("strQuantityApproved").value = reqQuantity;
	   } else {
		   if(parseInt(appQuantity) > parseInt(quantityAvail) && parseInt(appQuantity) > 0 && parseInt(quantityAvail)>0) {
			   alert("In Stock : "+ quantityAvail+" copies only.");
			   document.getElementById("strQuantityApproved").value = quantityAvail;
		    } else if(parseInt(quantityAvail) == 0) {
			   alert("Out of Stock.");
			   document.getElementById("strQuantityApproved").value = '';
			}
	   }
  }
  
function isOnlyNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	      return true;
	   }
	   return false;
	}
   
</script>

	<% 	
		
		List<String> requestList = (List<String>) request.getAttribute("requestList");
		String operation = (String) request.getAttribute("operation");
		
		
	%>
	
	<div style="float:left;width:100%;">	
		<s:form id = "frmApproveDenyBookPurchase" name="frmApproveDenyBookPurchase" action="ApproveOrDenyBookPurchase" method = "POST" theme ="simple">
			<s:hidden name="bookId" id="bookId" />
			<s:hidden name="empId" id="empId" />
			<s:hidden name="bookPurchaseId" id="bookPurchaseId" />
			<s:hidden name="availQuantity" id="availQuantity" />
			<s:hidden name="reqQuantity" id="reqQuantity"></s:hidden>
			<s:hidden name="operation" id="operation" />
	    	<table class="table table_no_border form-table" cellpadding="3" cellspacing="2">
	    		<tr>
	    			<td class="txtlabel alignRight">Requested By:</td>
	    			<td><%=uF.showData(requestList.get(1), "-") %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">Book Name:</td>
	    			<td><%=requestList.get(0) %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">Requested No. of Copies:</td>
	    			<td><%=requestList.get(2) %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight"> Requested On:</td>
	    			<td><%=requestList.get(3) %></td>
	    		</tr>
	    		
	    		<% if(operation != null && operation.equals("A")) { %>
	    		<tr>
	    			<td class="txtlabel alignRight"> Approved No. of Copies:<sup>*</sup></td>
	    			<td>
	    				<s:textfield id="strQuantityApproved" name="strQuantityApproved" cssClass="validateRequired" onkeyup="checkQuantity();" onkeypress="return isOnlyNumberKey(event)" cssStyle="width:90px;" />
	    			</td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">Book Amount:<sup>*</sup></td>
	    			<td> 
	    				<s:textfield id="strAmount" name="strAmount" cssClass="validateRequired" cssStyle="width:62px;" onkeypress="return isNumberKey(event)"/>
	    			</td>
	    		</tr>
	    		<% } %>
	    		
	    		<tr>
	    			<td class="txtlabel alignRight">Comment:</td>
	    			<td><s:textarea rows="3" cols="20" cssStyle="width:205px;" name="strComment" id="strComment" /></td>
	    		</tr>
	    		<tr>
	    				<td colspan="2" align="center">
		    			<% if(operation != null && operation.equals("A")) { %>
		    				<s:submit cssClass="btn btn-primary"  name="strSubmit" value="Approve"/>
			    			<%-- <input type="button" class="input_button"  name="strSubmit" onclick="checkQuantity('<%=avaQuantity%>', 'A');" value="Approve"> --%>
			    		<% } else { %>
			    			<s:submit cssClass="btn btn-danger" name="strCancel" value="Deny"/>
							<%-- <input type="button" class="cancel_button"  name="strCancel"  onclick="checkQuantity('<%=avaQuantity%>', 'D');" value="Deny"> --%>
						<% } %>
					
	    		   </td>
	    		</tr>
	    	</table>
		</s:form>
	</div>

