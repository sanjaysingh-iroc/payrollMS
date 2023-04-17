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
String serviceId = (String)request.getAttribute("serviceId"); %>
		<s:form theme="simple" action="AddService" method="POST" cssClass="formcss" id="formAddNewRow" name="formAddNewRow">
			<s:hidden name="serviceId" />
			<s:hidden name="userscreen"></s:hidden>
			<s:hidden name="navigationId"></s:hidden>
			<s:hidden name="toPage"></s:hidden>

			<table class="table table_no_border">
				<tr><td colspan=2><s:fielderror/></td></tr>
				<tr>
					<td class="txtlabel alignRight"><label for="organisation_Name">Select Organization:<sup>*</sup></label><br/></td>
					<td><s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
					 cssClass="validateRequired"></s:select></td> 
				</tr>
				
				<tr>   
					<td class="txtlabel alignRight"><label for="service_Code">SBU Code:<sup>*</sup></label><br/></td>
					<td><s:textfield name="serviceCode" cssClass="validateRequired" /><span class="hint">Short code of the cost-center.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<td class="txtlabel alignRight"><label for="service_Name">SBU Name:<sup>*</sup></label><br/></td>
					<td><s:textfield name="serviceName" cssClass="validateRequired"/><span class="hint">Name of the cost-center/service.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<td class="txtlabel alignRight" valign="top"><label for="service_Desc">Description:</label><br/></td>
					<td><s:textarea name="serviceDescription" rows="3" cols="22"/><span class="hint">Description of the SBU.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<td colspan="2" align="center">
					<% if(uF.parseToInt(serviceId) > 0) { %>
						<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk"/>
					<% } else { %>
						<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
					<% } %>	
					</td>
				</tr>
				
			</table>
		
		</s:form>

