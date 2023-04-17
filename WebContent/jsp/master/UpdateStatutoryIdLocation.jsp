<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>

<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script> 
$(document).ready( function () {
	$("#btnUpdate").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');	 		
	});
});
</script>
<% 
UtilityFunctions uF = new UtilityFunctions();
String fromPage=(String)request.getAttribute("fromPage");
%>

<script>
$(document).ready(function(){
	
	var fromPage="<%=(String)request.getAttribute("fromPage")%>";
	//alert("fromPage=="+fromPage);
	if(fromPage == "CD")
		{
			$("#tblv").show();
			$("#tbl").hide();
		}else {
			$("#tblv").hide();
			$("#tbl").show();
		}
});
</script>

<s:form theme="simple" id="formAddNewRow" action="UpdateStatutoryIdLocation" method="POST" cssClass="formcss">
	
	<s:hidden name="strWLocationId" />
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
	<table id="tbl" hidden class="table table_no_border">
	
		<%-- <s:hidden name="strWLocationId" />
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" /> --%>
		
		<tr>
			<td class="txtlabel alignRight">Office Name:</td>
			<td><s:hidden name="strWLocationName"/>
			<%=uF.showData((String)request.getAttribute("strWLocationName"), "") %></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight" valign="top">CIT (TDS) Address:</td>
			<td><s:textarea name="citAddress" id="citAddress" cols="22" rows="6"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Professional Tax(RC/EC):</td>
			<td><s:textfield name="ptRCEC" id="ptRCEC"></s:textfield></td>
		</tr>
		
		
			<tr>
				<td colspan="2"><h4>Authorised Details</h4><hr style="border:solid 1px #000"/></td>
 			</tr>
			<tr> 
				<td class="txtlabel alignRight" nowrap="nowrap">Financial Year:<sup>*</sup></td>
				<td><s:select theme="simple" name="f_strFinancialYear" id="f_strFinancialYear"
					listKey="financialYearId"listValue="financialYearName" list="financialYearList" key="" cssClass="validateRequired"/>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">Authorised Person:<sup>*</sup></td>
				<td>
					<s:select name="strEmpId" id="strEmpId" listKey="employeeId" cssStyle="float:left" 
					listValue="employeeName" headerKey="" headerValue="Select Employee"
					list="empList" key="" cssClass="validateRequired" />
				</td>          
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td><strong>Authorised Person Details</strong></td>
 			</tr>
 			
 			<tr>
				<td class="txtlabel alignRight">&nbsp;</td>
				<td nowrap="nowrap">
					
					<%
						List<Map<String, String>> hmAuthorisedDetails = (List<Map<String, String>>)request.getAttribute("hmAuthorisedDetails");
						if(hmAuthorisedDetails == null) hmAuthorisedDetails = new ArrayList<Map<String,String>>();
					%>
					<table width="100%" class="table table-striped">
						<tr>
							<th>Financial Year</th>
							<th>Authorised Person</th> 
						</tr>
						<%
							for(int i=0; hmAuthorisedDetails!=null && i < hmAuthorisedDetails.size(); i++){
								Map<String, String> hmInner = (Map<String, String>) hmAuthorisedDetails.get(i);
						%>
							<tr>
								<td class="alignCenter"><%=uF.showData(hmInner.get("FINANCIAL_YEAR"), "")%></td>
								<td class="alignCenter"><%=uF.showData(hmInner.get("EMP_NAME"), "")%></td>
							</tr>
						<%} %>
					</table>
				</td>          
			</tr>
			 
			<tr>
			<td colspan="2" align="center">
				<s:submit cssClass="btn btn-primary" value="Update" id="btnUpdate" /> 
			</td>
	
		</tr>
			
	</table>
	
</s:form>

<table id="tblv" hidden class="table table_no_border">
		
		<tr>
			<td class="txtlabel alignRight">Office Name:</td>
			<td><s:hidden name="strWLocationName1"/>
			<%=uF.showData((String)request.getAttribute("strWLocationName"), "") %></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight" valign="top">CIT (TDS) Address:</td>
			<td><s:textarea name="citAddress1" readonly="true" id="citAddress1" cols="22" rows="6"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Professional Tax(RC/EC):</td>
			<td><s:textfield name="ptRCEC1" readonly="true" id="ptRCEC1"></s:textfield></td>
		</tr>
		
		
			<tr>
				<td colspan="2"><h4>Authorised Details</h4><hr style="border:solid 1px #000"/></td>
 			</tr>
			<tr> 
				<td class="txtlabel alignRight" nowrap="nowrap">Financial Year:<sup>*</sup></td>
				<td><s:select theme="simple" name="f_strFinancialYear1" id="f_strFinancialYear1"
					listKey="financialYearId"listValue="financialYearName" list="financialYearList" key="" cssClass="validateRequired"/>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">Authorised Person:<sup>*</sup></td>
				<td>
					<s:select name="strEmpId1" id="strEmpId1" listKey="employeeId" cssStyle="float:left" 
					listValue="employeeName" headerKey="" headerValue="Select Employee"
					list="empList" key="" cssClass="validateRequired" />
				</td>          
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td><strong>Authorised Person Details</strong></td>
 			</tr>
 			<tr>
				<td class="txtlabel alignRight">&nbsp;</td>
				<td nowrap="nowrap">
					
					<%
						List<Map<String, String>> hmAuthorisedDetails1 = (List<Map<String, String>>)request.getAttribute("hmAuthorisedDetails");
						if(hmAuthorisedDetails1 == null) hmAuthorisedDetails1 = new ArrayList<Map<String,String>>();
					%>
					<table width="100%" class="table table-striped">
						<tr>
							<th>Financial Year</th>
							<th>Authorised Person</th> 
						</tr>
						<%
							for(int i=0; hmAuthorisedDetails1!=null && i < hmAuthorisedDetails1.size(); i++){
								Map<String, String> hmInner = (Map<String, String>) hmAuthorisedDetails1.get(i);
						%>
							<tr>
								<td class="alignCenter"><%=uF.showData(hmInner.get("FINANCIAL_YEAR"), "")%></td>
								<td class="alignCenter"><%=uF.showData(hmInner.get("EMP_NAME"), "")%></td>
							</tr>
						<%} %>
					</table>
				</td>          
			</tr>
	</table>
