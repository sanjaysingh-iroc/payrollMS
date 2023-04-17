<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript">

jQuery(document).ready(function(){
    $("#submitButton").click(function(){
    	$(".validateRequired").prop('required',true);
    });
}); 


$(function() {
	$("#deferDate").datepicker({dateFormat: 'dd/mm/yy',minDate: 0});
});
</script>


<s:form theme="simple" name="formDeferBasicFitment" id="formDeferBasicFitment" action="DeferBasicFitment" method="POST" cssClass="formcss">
	<s:hidden name="operation" id="operation" value="U"></s:hidden>
	<s:hidden name="emp_id" id="emp_id"/>
	<s:hidden name="grade_from" id="grade_from"></s:hidden>
	<s:hidden name="grade_to" id="grade_to"></s:hidden>
	<s:hidden name="fitmentMonth" id="fitmentMonth"></s:hidden>
	<s:hidden name="fitmentYear" id="fitmentYear"></s:hidden>
	<table class="formcss">		
		<tr>
			<td class="txtlabel alignRight">Defer Date:<sup>*</sup></td>
			<td><s:textfield name="deferDate" id="deferDate" cssClass="validateRequired" cssStyle="width: 85px !important;"/></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><s:submit value="Submit" cssClass="btn btn-primary" name="submit" theme="simple" id="submitButton"/></td>
		</tr>
	</table>
</s:form>