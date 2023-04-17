<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script>
	
	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#formID").validationEngine();
	});
	
	
</script>

<div >


<s:form method="POST" action="#">
<table style="width:27%">
		<tr>
		<td style="text-align:right">Manager</td>
		<td style="text-align:left">ManagerComments</td>
		</tr>	
	
</table>

</s:form>
</div>




