<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');
	});
});

	
	/* function getSBUDetails(){
		var orgId = document.getElementById("strOrg").value;
		
		var action = 'GetSbuDetails.action?orgId='+orgId+"&type=SBU";
		getContent('divServiceid', action); 
		
	}	

	function getDepartDetails(){
		var strSerivce=document.getElementById("strSerivce").value;
		var orgId = document.getElementById("strOrg").value;
		
		var action = 'GetSbuDetails.action?strSerivce='+ strSerivce+'&orgId='+orgId+"&type=DEPT";
		getContent('divParentid', action); 
		
	}	 */

</script>
<%
	UtilityFunctions uF=new UtilityFunctions();
	String deptId = (String)request.getAttribute("deptId");
	String parentDept = (String)request.getAttribute("parentDept");
	
%>

		<s:form theme="simple" action="AddDepartment" method="POST" cssClass="formcss" id="formAddNewRow">
		
			<s:hidden name="deptId" />
			<s:hidden name="userscreen" />
			<s:hidden name="navigationId" />
			<s:hidden name="toPage" />
			
			<table border="0" class="table table_no_border">
			<tr><td colspan=2><s:fielderror/></td></tr>
			
				<tr>
					<td class="txtlabel alignRight"><label for="organisation_Name">Select Organization:<sup>*</sup></label><br/></td>
					<td><s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
					 cssClass="validateRequired" ></s:select></td> 
				</tr>
				
				<tr>
					<td class="txtlabel alignRight"><label for="deptName">Department Code:<sup>*</sup></label><br/></td>
					<td><s:textfield name="deptCode" cssClass="validateRequired"/><span class="hint">Add the department code. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<td class="txtlabel alignRight"><label for="deptName">Department Name:<sup>*</sup></label><br/></td>
					<td><s:textfield name="deptName" cssClass="validateRequired"/><span class="hint">Add the department name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
			
				<tr>
					<td class="txtlabel alignRight"><label for="deptContactNo">Contact No:</label></td>
					<td><s:textfield name="deptContactNo" cssClass="validateNumber"/><span class="hint">Add the contact number for this department.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<td class="txtlabel alignRight"><label for="deptFax">Fax No:</label></td>
					<td><s:textfield name="deptFax" cssClass="validateNumber"/><span class="hint">Add the fax number for this department.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<td class="txtlabel alignRight" valign="top"><label for="deptDescription">Description:</label></td>
					<td><s:textarea name="deptDescription" rows="5" cols="33"></s:textarea><span class="hint">Add the department description here. <span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<td colspan="2" align="center">
					<% if(uF.parseToInt(deptId) > 0) { %>
						<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk"/>
					<% } else { %>
						<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
					<% } %>	
					</td>
				</tr>
			
		</table>
		</s:form>

