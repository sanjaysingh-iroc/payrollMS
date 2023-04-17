<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>

$(document).ready( function () {
	$("#btnOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	
	var value = document.getElementById("billingHeadDataType1").value;
	showEditOtherVariables(value, "1");
});	

function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
      return false;
   }
   return true;
}

function showOtherVariables(value, count) {
	//alert("value ===> " +value + " count ===> " + count);
	if(value == <%=IConstants.DT_PRORATA_INDIVIDUAL %> || value == <%=IConstants.DT_OPE_INDIVIDUAL %>) {
		document.getElementById("bhOtherVariableSpan"+count).style.display = 'block';
		getContent('bhOtherVariableSpan'+count, 'GetBillingHeadOtherVariable.action?billingHeadDataType='+value+'&count='+count);
	} else {
		document.getElementById("bhOtherVariableSpan"+count).style.display = 'none';
	}
}

function showEditOtherVariables(value, count) {
	//alert("value ===> " +value + " count ===> " + count);
	if(value == <%=IConstants.DT_PRORATA_INDIVIDUAL %> || value == <%=IConstants.DT_OPE_INDIVIDUAL %>) {
		document.getElementById("bhOtherVariableSpan"+count).style.display = 'block';
		//getContent('bhOtherVariableSpan'+count, 'GetBillingHeadOtherVariable.action?billingHeadDataType='+value+'&count='+count);
	} else {
		document.getElementById("bhOtherVariableSpan"+count).style.display = 'none';
	}
}

</script>

<%
UtilityFunctions uF = new UtilityFunctions();
String billingHeadId = (String)request.getAttribute("billingHeadId"); %>

<s:form theme="simple" id="formAddBillingHead" action="AddBillingHead" method="POST" cssClass="formcss">
	<s:hidden name="billingHeadId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
<%-- <s:hidden name="orgId"></s:hidden> --%>

	<table class="table table_bordered">
		
		<tr>
			<td class="txtlabel alignRight"><label for="organisation_Name">Organisation:<sup>*</sup></label><br/></td>
			<td><s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
			 cssClass="validateRequired"></s:select></td> 
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Billing Head:<sup>*</sup></td>
			<td><s:textfield name="billingHead" id="billingHead" cssClass="validateRequired" cssStyle="width: 300px;"/></td>  
		</tr>
	 
		<tr>
			<td class="txtlabel alignRight">Billing Data Type:<sup>*</sup></td>
			<td><s:select id="billingHeadDataType1" name="billingHeadDataType" cssStyle="width: 150px;" listKey="headId" listValue="headName" 
					list="billingHeadDataTypeList" onchange="showOtherVariables(this.value, '1');"/> <!-- headerKey="" headerValue="Select Data Type"  -->
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Other Variable:<sup>*</sup></td>
			<td><span id="bhOtherVariableSpan1" style="display: none;">
					<s:select id="billingHeadOtherVariable1" name="billingHeadOtherVariable1" cssStyle="width: 160px;"
						listKey="headId" listValue="headName" list="billingHeadOtherVariableList" /> <!-- headerKey="" headerValue="Select Other Variable"  -->
				</span>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
			<% if(uF.parseToInt(billingHeadId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" id="btnOk"/>
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Add" id="btnOk"/>
			<% } %>
			</td>
		</tr>
	</table>
	
</s:form>
