<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
	
	$(document).ready( function () {
		$("#btnAddNewRowOk").click(function(){
			$(".validateRequired").prop('required',true);
		});
	});
</script>


<% 
UtilityFunctions uF = new UtilityFunctions();
String productionLineId = (String)request.getAttribute("productionLineId"); %>
<s:form theme="simple" action="AddProductionLine" method="POST" cssClass="formcss" id="formAddNewRow" name="formAddNewRow">
	<s:hidden name="productionLineId"/>
	<s:hidden name="strOrg"/>
	<s:hidden name="userscreen"></s:hidden>
	<s:hidden name="navigationId"></s:hidden>
	<s:hidden name="toPage"></s:hidden>
	<table class="table table_no_border">
		<tr>   
			<td class="txtlabel alignRight"><label for="service_Code">Production Line Code:<sup>*</sup></label><br/></td>
			<td><s:textfield name="productionLineCode" cssClass="validateRequired" /><span class="hint">Short code of the production line.<span class="hint-pointer">&nbsp;</span></span></td> 
		</tr>
		
		<tr>
			<td class="txtlabel alignRight"><label for="service_Name">Production Line Name:<sup>*</sup></label><br/></td>
			<td><s:textfield name="productionLineName" cssClass="validateRequired"/><span class="hint">Name of the production line.<span class="hint-pointer">&nbsp;</span></span></td> 
		</tr>
		
		<tr>
			<td colspan="2" align="center">
			<% if(uF.parseToInt(productionLineId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk"/>
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
			<% } %>	
			</td>
		</tr>
	</table>
</s:form>