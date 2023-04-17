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
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<%UtilityFunctions uF = new UtilityFunctions();%>
<script type="text/javascript">
    function checkQuantity() {
       var issuedQuantity = document.getElementById("issuedQuantity").value;
       var appQuantity = document.getElementById("strQuantityReturned").value;
       if(appQuantity!="" && parseInt(appQuantity) <= 0) {
    	   alert("Invalid quantity!");
    	   document.getElementById("strQuantityReturned").value = '';
       } else {
    	   	if(parseInt(appQuantity) > parseInt(issuedQuantity) && parseInt(appQuantity) > 0) {
    	   	  alert("Issued : "+ issuedQuantity+" copies only.");
    		   document.getElementById("strQuantityReturned").value = issuedQuantity;
    	    } 
        }
    }
</script>
<div style="float:left;width:100%;">
    <% List<String> requestList = (List<String>) request.getAttribute("requestList"); %>
    <s:form id = "frmReturnBook" name="frmReturnBook" action="ReturnIssuedBook" method = "POST" theme ="simple">
        <s:hidden name="bookId" id="bookId"></s:hidden>
        <s:hidden name="bookIssuedId" id="bookIssuedId"></s:hidden>
        <s:hidden name="issuedQuantity" id="issuedQuantity"></s:hidden>
        <s:hidden name="operation" id="operation"></s:hidden>
        <s:hidden name="issuedDate" id="issuedDate"></s:hidden>
        <table class="table table_no_border form-table" cellpadding="2" cellspacing="2">
            <tr>
                <td class="txtlabel alignRight">Employee Name:</td>
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
                <td class="txtlabel alignRight">Issued Quantity:</td>
                <td><%=requestList.get(3) %></td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Issued Comment:</td>
                <td><%=requestList.get(4) %></td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Issued Date:
                <input type="hidden" value="<%=requestList.get(5) %>" id="issued-date">
                </td>
                <td><%=requestList.get(5) %></td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">From Date:</td>
                <td><%=requestList.get(7) %></td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Due Date:</td>
                <td><%=requestList.get(7) %></td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Returned Quantity:<sup>*</sup></td>
                <td>
                    <s:textfield id="strQuantityReturned" name="strQuantityReturned" cssClass="validateRequired" onkeyup="checkQuantity();" onkeypress="return isOnlyNumberKey(event)" ></s:textfield>
                </td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Return Date:<sup>*</sup></td>
                <td>
                    <s:textfield name="returnDate" id="returnDate" cssClass="validateRequired" onblur="fillField(this.id, 3);" onclick="clearField(this.id);"></s:textfield>
                </td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Comment:</td>
                <td>
                    <s:textarea rows="3" cols="20" cssStyle="width: 205px;" name="strComment" id="strComment"></s:textarea>
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <s:submit cssClass="btn btn-primary" name="strSubmit" value="Return"/>
                    <!-- <input type="button" class="cancel_button" name="strCancel" onclick="closeReturnPopup();" value="Cancel"> -->
                </td>
            </tr>
        </table>
    </s:form>
</div>
<script>
    $(document).ready( function () {
    	$("input[name='strSubmit']").click(function(){
    		$(".validateRequired").prop('required',true);
    		$(".validateNumber").prop('type','number');	 		
    	});
	    	var strMinDate = new Date(document.getElementById("issued-date").value);
	    	$('#returnDate').datepicker('setStartDate', strMinDate);
	    	$('#returnDate').datepicker('setEndDate', now);
    }); 
</script>