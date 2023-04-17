<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
$(function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	
	$("#strSalaryHeadId").multiselect().multiselectfilter();
});


function getSalaryHead() {
	var strLevel = document.getElementById("strLevel").value; 
	getContent('tdSalaryHead','GetSalaryHeadsByLevel.action?strLevel='+strLevel);
}

</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	String strAlignSalaryHeadId = (String)request.getAttribute("strAlignSalaryHeadId"); 
%>

<s:form theme="simple" action="AddAlignSalaryHead" method="POST" cssClass="formcss" id="formAddNewRow" name="formAddNewRow">
	<s:hidden name="strAlignSalaryHeadId"></s:hidden>
	<s:hidden name="productionLineId"/>
	<s:hidden name="strOrg"/>
	<s:hidden name="userscreen"></s:hidden>
	<s:hidden name="navigationId"></s:hidden>
	<s:hidden name="toPage"></s:hidden>
	
	<table class="table table_no_border">
		<tr>   
			<td class="txtlabel alignRight"><label for="service_Code">Level:<sup>*</sup></label><br/></td>
			<td>
				<% if(uF.parseToInt(strAlignSalaryHeadId) > 0) { %>
					<%=uF.showData((String)request.getAttribute("strLevelName"),"") %>					
					<s:hidden name="strLevel"></s:hidden>
				<%} else { %>
					<s:select theme="simple" name="strLevel" id="strLevel" list="levelList" listKey="levelId" listValue="levelCodeName" 
						headerKey="" headerValue="Select Level" required="true" cssClass="validateRequired" onchange="getSalaryHead();"></s:select>
				<%} %>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight"><label for="service_Name">Salary Head:<sup>*</sup></label><br/></td>
			<td id="tdSalaryHead">
				<s:select theme="simple" name="strSalaryHeadId" id="strSalaryHeadId" list="salaryHeadList" listKey="salaryHeadId"
                     listValue="salaryHeadName" key="" cssClass="validateRequired" multiple="true" size="4"/>
            </td> 
		</tr>
		
		<tr>
			<td colspan="2" align="center">
			<% if(uF.parseToInt(strAlignSalaryHeadId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk"/>
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
			<% } %>	
			</td>
		</tr>
	</table>
</s:form>