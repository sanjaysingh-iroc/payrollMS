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


<script>

    function submitFrm(type) {
    	document.getElementById("operation").value = type;
    	document.getElementById("frmApproveOrDenyBookingRequests").submit();
    }
    
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String bookingId = (String)request.getAttribute("bookingId");
	
%>


<div style="float:left;width:100%;">	
<s:form id = "frmApproveOrDenyBookingRequests" name="frmApproveOrDenyBookingRequests" action="ApproveOrDenyBookingRequests" method = "POST" theme ="simple">
	<input type = "hidden" name="bookingId" value = "<%=bookingId %>"/>
    <s:hidden name="operation"  id="operation"></s:hidden>
	  <div style="float:left;width:100%;">
	       <div style="float:left;width:100%;margin-top:15px;">
	       		<p style="float:left;width:100%;">Comment:</p>
	  	 		  <textarea rows="4" cols="20" style="width:90%;margin-top:5px;" name="strComment" id="strComment"></textarea>
	  	   </div>
		  	<div style="float:left;width:100%;margin:10px 0px 0px 1px;">
		  	    <input type="button" class="btn btn-primary"  name="strSubmit" style="margin-top: 5px;" onclick="submitFrm('A');" value="Approve">
				<input type="button" class="btn btn-cancel"  name="strCancel" style="margin-top: 5px;margin-left:10px;" onclick="submitFrm('D');" value="Deny">
		    </div>
	  </div>
	</s:form>
</div>

