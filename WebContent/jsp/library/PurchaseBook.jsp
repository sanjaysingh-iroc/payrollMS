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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	
function checkQuantity() { 
	   var quantityAvail = document.getElementById("availQuantity").value;
	   var appQuantity = document.getElementById("strQuantity").value;
	  
	   if(appQuantity!="" && parseInt(appQuantity) <= 0) {
		   alert("Invalid quantity!");
		   document.getElementById("strQuantity").value = '';
	   } else {
		   	if(parseInt(appQuantity) > parseInt(quantityAvail) && parseInt(appQuantity) > 0) {
			   alert("In Stock : "+ quantityAvail+" copies only.");
			   document.getElementById("strQuantity").value = quantityAvail;
		    } else if(parseInt(quantityAvail) == 0){
			   alert("Out of Stock.");
			   document.getElementById("strQuantity").value = '';
			}
	    }
  }
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
    String empId = (String)session.getAttribute(IConstants.EMPID);
    String bookId = (String)request.getAttribute("bookId");
    String bookName = (String)request.getAttribute("bookName");
%>

<div>	
	<s:form id = "frmPurchaseBook" name="frmPurchaseBook" action="PurchaseBook" method = "POST" theme ="simple">
		<input type = "hidden" name="bookId" value = "<%=bookId %>"/>
		<input type = "hidden" name="empId" value = "<%=empId %>"/>
		<s:hidden name="availQuantity" id="availQuantity" />
		<table class="table table_no_border form-table" cellpadding="2" cellspacing="2">
    		<tr>
    			<td class="txtlabel alignRight">Book Name:</td>
    			<td><%=uF.showData(bookName, "-") %></td> 
    		</tr>
    		<tr>
    			<td class="txtlabel alignRight">Quantity:<sup>*</sup></td>
    			<td><input type="number" id="strQuantity" name="strQuantity" onkeyup="checkQuantity();" onkeypress="return isOnlyNumberKey(event)" required/></td>
    		</tr>
    		<tr>
    			<td></td>
    			<td>
    				<input type="submit" class="btn btn-primary" name="strSubmit" value="Want to Purchase">
    				<!-- <input type="button" class="btn btn-danger" name="strCancel" value="Cancel"> -->
    		   </td>
    		</tr>
    	</table>
	</s:form>
</div>
<script>
	$(".btn-danger").click(function(){$("#modalInfo").hide();});
    $(window).load(function(){
        $(".validateRequired").prop('required',true);
    });
</script>
