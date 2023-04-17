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

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>

<script type="text/javascript">
/* jQuery(document).ready(function() {
	jQuery("#frmDishOrder").validationEngine();
}); */

</script>

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
	UtilityFunctions uF = new UtilityFunctions();
	String dishName = (String)request.getAttribute("dishName");
	String dishId = (String)request.getAttribute("dishId");;
%>

<div style="float:left;width:100%;">	
	<s:form id="frmDishOrder" name="frmDishOrder" action="PlaceOrderForDish" method="POST" theme="simple">
		<s:hidden name="dishId" id="dishId"/>
		<table class="table table_no_border form-table" cellpadding="2" cellspacing="2">
    		<tr>
    			<td class="txtlabel alignRight no-top-border" style="width: 90px;">Dish Name:</td>
    			<td class="no-top-border"><%=uF.showData(dishName, "-") %></td>
    		</tr>
    		<tr>
    			<td class="txtlabel alignRight" style="width: 90px;">Quantity:<sup>*</sup></td>
    			<td><input type="number" id="strQuantity" name="strQuantity" onkeypress="return isOnlyNumberKey(event)" required /></td>
    		</tr>
    		<tr>
    			<td></td>
    			<td>
    				<input type="submit" class="btn btn-primary" name="strSubmit" value="Order">
    				<!-- <input type="button" class="btn btn-danger" name="strCancel" onclick="closeDishOrderPopup();" value="Cancel"> -->
    		   </td>
    		</tr>
    	</table>
	</s:form>
</div>
<script>
$(".btn-danger").click(function(){$("#modalInfo").hide();});
</script>


