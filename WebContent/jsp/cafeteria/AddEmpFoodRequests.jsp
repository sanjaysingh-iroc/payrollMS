<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script>
$(document).ready( function () {
	$("#strEmpIds").multiselect().multiselectfilter();
});
	/* jQuery(document).ready(function() {
		jQuery("#frmAddEmpFoodRequests").validationEngine();
	});
 */
</script>

<div style="float:left;width:100%;">	
   <s:form name="frmAddEmpFoodRequests" id="frmAddEmpFoodRequests" action="AddEmpFoodRequests" method="POST" theme="simple">
		<s:hidden name="dishId" id="dishId"/>
		<table class="table" cellpadding="2" cellspacing="2" style="font-size:12px;">
			<tr>
				<td class="txtlabel alignRight" style="width: 120px;">Select Employees:</td>
				<td>
					<s:select theme="simple" name="strEmpIds" id="strEmpIds" listKey="employeeId"  cssStyle="float:left" listValue="employeeName"  
						list="empList" key="" required="true" cssClass="validateRequired" multiple="true" /> <!-- headerKey="" headerValue="Select Employee" -->
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
				<input type="submit" class="btn btn-primary" style="float:left;" name="strSubmit" id="strSubmit" value="Order Confirm"/>&nbsp;
				 <!-- <input type="button" class="btn btn-danger" name="strCancel" onclick="closeAddEmpOrderPopup();" value="Cancel"> -->
				</td>
			</tr>
		</table>
   </s:form>
 </div>
 <script>
 	$(".btn-danger").click(function(){$("#modalInfo").hide();});
 </script>