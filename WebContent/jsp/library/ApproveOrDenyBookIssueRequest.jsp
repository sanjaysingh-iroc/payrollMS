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

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>

<script type="text/javascript">

$(function() {
    $( "#strReturnDate" ).datepicker({format: 'dd/mm/yyyy'});
    
});

</script>

<% UtilityFunctions uF = new UtilityFunctions(); %>

<script type="text/javascript">
   
	function checkQuantity() { 
	   var quantityAvail = document.getElementById("availQuantity").value;
	   var appQuantity = document.getElementById("strQuantityIssued").value;
	   var reqQuantity = document.getElementById("reqQuantity").value;
	  
	   if(appQuantity!="" && parseInt(appQuantity) <= 0) {
		   alert("Invalid quantity!");
		   document.getElementById("strQuantityIssued").value = '';
	   } else if(reqQuantity!=null && parseInt(appQuantity) > parseInt(reqQuantity) && parseInt(reqQuantity) <= parseInt(quantityAvail)){
		   alert("Requested : "+ reqQuantity+" copies only.");
		   document.getElementById("strQuantityIssued").value = reqQuantity;
	   } else {
		   if(parseInt(appQuantity) > parseInt(quantityAvail) && parseInt(appQuantity) > 0 && parseInt(quantityAvail) >0) {
			   alert("In Stock : "+ quantityAvail+" copies only.");
			   document.getElementById("strQuantityIssued").value = quantityAvail;
		    } else if(parseInt(quantityAvail) == 0) {
			   alert("Out of Stock.");
			   document.getElementById("strQuantityIssued").value = '';
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

<div style="float:left;width:100%;">	
	<% List<String> requestList = (List<String>) request.getAttribute("requestList");
		String operation = (String) request.getAttribute("operation");
	%>
	<s:form id="frmApproveDenyBookIssue" name="frmApproveDenyBookIssue" action="ApproveOrDenyBookIssueRequest" method="POST" theme="simple">
		<s:hidden name="bookId" id="bookId"></s:hidden>
		<s:hidden name="bookIssuedId" id="bookIssuedId"></s:hidden>
		<s:hidden name="availQuantity" id="availQuantity"></s:hidden>
		<s:hidden name="reqQuantity" id="reqQuantity"></s:hidden>
		<s:hidden name="operation" id="operation"></s:hidden>
	    	<table class="table table_no_border form-table" cellpadding="2" cellspacing="2">
	    		<tr>
	    			<td class="txtlabel alignRight">Requested By:</td>
	    			<td><%=requestList.get(1) %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">Book Name:</td>
	    			<td><%=requestList.get(0) %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">Author:</td>
	    			<td><%=requestList.get(2) %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">Requested No. of Copies:</td>
	    			<td><%=requestList.get(3) %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">Requested On:</td>
	    			<td><%=requestList.get(5) %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">From:</td>
	    			<td><%=requestList.get(6) %></td>
	    		</tr>
	    		<tr>
	    			<td class="txtlabel alignRight">To:</td>
	    			<td><%=requestList.get(7) %></td>
	    		</tr>
	    		
	    		<tr>
	    			<td class="txtlabel alignRight">Requested Comment:</td>
	    			<td><%=requestList.get(4) %></td>
	    		</tr>
	    		
	    		<% if(operation != null && operation.equals("A")) { %>
	    		<tr>
	    			<td class="txtlabel alignRight">Issued No. of Copies:<sup>*</sup></td>
	    			<td><input type="number" id="strQuantityIssued" name="strQuantityIssued" onkeypress="return isOnlyNumberKey(event)"  onkeyup="checkQuantity();" style="width:90px;" required /></td>
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

