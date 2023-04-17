
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
$(function(){
	$("body").on("click","#btnAddNewRowOk",function(){
    	$('.validateRequired').filter(':hidden').prop('required', false);
		$('.validateRequired').filter(':visible').prop('required', true);
    });
});

	function getPrevVDARate(){
		var strOrgId = document.getElementById("strOrgId").value;
		var paycycle = document.getElementById("paycycle").value;
		var action="AddUpdateDeleteVDARate.action?operation=CHECKVDA&strOrgId="+strOrgId+"&paycycle="+paycycle;
		$.ajax({ 
    		type : 'GET',
    		url: action,
    		success: function(result){
    			document.getElementById("vdaRate").value = result.trim();
    			//$("#vdaRate").value(result.trim());
       		}
    	});
	}
		
</script>


	<s:form theme="simple" id="formAddNewRow" action="AddUpdateDeleteVDARate" method="POST" cssClass="formcss">
		<table class="table">
			<s:hidden name="strOrgId" id="strOrgId"></s:hidden>
			<s:hidden name="userscreen" />
			<s:hidden name="navigationId" />
			<s:hidden name="toPage" />
			<s:hidden name="operation" />
			<s:hidden name="strVdaRateId" />
			<tr>
				<td valign="top" class="txtlabel alignRight">Organization:</td>
				<td>
				<% String strOrgName = (String)request.getAttribute("strOrgName");%>
					<%=strOrgName %>
				</td>
			</tr>
			<tr>
				<td valign="top" class="txtlabel alignRight">Paycycle:<sup>*</sup></td>
				<td>
				<% String operation = (String)request.getAttribute("operation");
					String strPaycycleName = (String)request.getAttribute("strPaycycleName");
					if(operation != null && operation.equals("U")) {
				%>
				<s:hidden name="paycycle" id="paycycle" />
					<%=strPaycycleName %>
				<% } else { %>
					<s:select label="Select PayCycle" name="paycycle" id="paycycle" cssClass="validateRequired" listKey="paycycleId" 
					listValue="paycycleName" headerKey="" headerValue="Select Paycycle" onchange="getPrevVDARate();" list="paycycleList"/>
				<% } %>	
				</td>
			</tr>
			<tr>
				<td valign="top" class="txtlabel alignRight">VDA Rate:<sup>*</sup></td>
				<td><s:textfield name="vdaRate" id="vdaRate" cssClass="validateRequired"/></td>
			</tr>
			
			<tr>
				<td colspan="2" align="center">
					<s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
				</td>
			</tr>
		</table>
		
	</s:form>

