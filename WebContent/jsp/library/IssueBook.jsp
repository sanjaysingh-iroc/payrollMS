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


<%
	UtilityFunctions uF = new UtilityFunctions();
	String bookId = (String)request.getAttribute("bookId");
	String bookIssuedId = (String)request.getAttribute("bookIssuedId");
    String type = (String)request.getAttribute("type"); 
   
    String availQuantity = (String)request.getAttribute("availQuantity");
    String bookName = (String)request.getAttribute("bookName");
  
   
%>

<script type="text/javascript">

		
   function checkQuantity() { 
	   var quantityAvail = document.getElementById("availQuantity").value;
	   var appQuantity = document.getElementById("strQuantityReq").value;
	  
	   if(appQuantity!="" && parseInt(appQuantity) <= 0) {
		   alert("Invalid quantity!");
		   document.getElementById("strQuantityReq").value = '';
	   } else {
		   	if(parseInt(appQuantity) > parseInt(quantityAvail) && parseInt(appQuantity) > 0) {
			  		 alert("In Stock : "+ quantityAvail+" copies only.");
			   		document.getElementById("strQuantityReq").value = quantityAvail;
		    } else if(parseInt(quantityAvail) == 0){
			   alert("Out of Stock.");
			   document.getElementById("strQuantityReq").value = '';
			}
	    }
     }
   
   
   function isOnlyNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	      return true;
	   }
	   return false;
	}
   
</script>

<div >	
	<s:form id="frmIssueBook" name="frmIssueBook" action="IssueBook" method="POST" theme="simple">
		<s:hidden name="bookId" id="bookId" />
		<s:hidden name="bookIssuedId" id="bookIssuedId" />
		<s:hidden name="availQuantity" id="availQuantity" />
	      <table class="table table_no_border form-table" cellpadding="2" cellspacing="2">
	    		<tr><td class="txtlabel alignRight">Book Name:</td>
	    			<td><%=bookName %></td>
	    		</tr>
	    		<%-- <tr>
	    			<td class="txtlabel alignRight">Requested By:</td>
	    			<td>
	    				<s:select name="strEmpId" id="strEmpId" listKey="employeeId" cssStyle="float:left;margin:5px 0px 5px 0px;" cssClass="validateRequired" listValue="employeeName" headerKey="" headerValue="Select Employee"
						list="empList" key="" required="true"/>
	    			</td>
	    		</tr> --%>
	    		
	    		<tr>
	    			<td class="txtlabel alignRight">Requested No. of Copies:<sup>*</sup></td>
	    			<td><input type="number" id="strQuantityReq" name="strQuantityReq" class="validateRequired form-control " onkeyup="checkQuantity();" onkeypress="return isOnlyNumberKey(event)" required/></td>
	    		</tr>
	    		 <tr>
	    			<td class="txtlabel alignRight">From:<sup>*</sup></td>
	    			<td>
	    				<input type="text" name="strStartDate" id="strStartDate" class="validateRequired form-control " required/>
	    			</td>
		    	</tr>
		    	<tr>
	    			<td class="txtlabel alignRight"> To:<sup>*</sup></td>
	    			<td>
	    				<input type="text" name="strEndDate" id="strEndDate" class="validateRequired form-control "  required/>
	    			</td>
		    	</tr>
		    	
	    		<tr>
	    			<td class="txtlabel alignRight">Comment:</td>
	    			<td><s:textarea rows="3" cols="20" name="strComment" id="strComment" cssClass=" form-control "></s:textarea></td>
	    		</tr>
	    		<tr>
	    			<td></td>
	    			<td> 
	    				<s:submit cssClass="btn btn-primary" name="strSubmit" value="Want to Rent"/>
						<%-- <input type="button" class="input_button" name="strIssue" style="margin-top: 5px;" onclick="checkQuantity('I', '<%=availQuantity %>');" value="Want to Rent"> --%>
						<!-- <input type="button" class="btn btn-danger" name="strCancel" onclick="closePopup();" value="Cancel"> -->
	    			</td>
	    		</tr>
	    	</table>
	</s:form>
</div>

<script type="text/javascript">
	$(".btn-danger").click(function(){$("#modalInfo").hide();});
	$(function(){
		$("#strStartDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strEndDate').datepicker('setStartDate', minDate);
        });
        
        $("#strEndDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strStartDate').datepicker('setEndDate', minDate);
        });
	}); 
</script>
