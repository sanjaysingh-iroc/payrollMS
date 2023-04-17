<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>


<script type="text/javascript">

$(document).ready( function () {
	$("#submitButton").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	
</script>

 

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> attributeDetails = (Map<String, String>) request.getAttribute("attributeDetails");
	if (attributeDetails == null)
		attributeDetails = new HashMap<String, String>();
%>
<div>

	<s:form id="frmId11" method="POST" theme="simple" action="AddAppraisalAttributeForLevel">

		<s:hidden name="strLevel"></s:hidden>
		<s:hidden name="elementid"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="type"></s:hidden>
		<s:hidden name="type"></s:hidden>   
		<s:hidden name="strOrg"></s:hidden>
		
		<table class="table">
		 	<tr>
				<td style="text-align: right;">Select Attribute<sup>*</sup></td>
				<td>
				<% if (attributeDetails.get("ARRTIBUTE_ID") != null) { %>
					<%=attributeDetails.get("ATTRIBUTE_NAME")%>
				<% } else { %>
				<s:select theme="simple" name="attributeid" list="attributeList" listKey="id" id="attributeid" listValue="name" headerKey=""
					headerValue="select Attribute" required="true" cssClass="validateRequired"></s:select>
                 <% } %>
				</td>
			</tr>

			<tr>
				<td style="text-align: right;">Select Threshhold value<sup>*</sup></td>
				<td><s:textfield name="attributeThreshhold" cssClass="validateRequired" id="attributeThreshholdValue" cssStyle="border: 0; color: #f6931f; font-weight: bold;"/></td>
			</tr> 
			
			<tr>
				<td></td>
				<td>
				
				<script>
					$(function() {
						$("#slider").slider({
							value : <%=uF.parseToInt(attributeDetails.get("THRESHOLD"))==0 ? 75 : uF.parseToInt(attributeDetails.get("THRESHOLD"))%>,
							min : 0,
							max : 100,
							step : 1,
							slide : function(event, ui) {
								$("#attributeThreshholdValue").val(ui.value);
							}
						});
						$("#attributeThreshholdValue").val($("#slider").slider("value"));
					});
				</script>
				
				
				
				<div id="slider"></div></td>
			</tr>

			<tr>
				<td colspan="2">
				<div style="float: left; margin-left: 100px;">
						<input type="hidden" name="arribute_level_id" id="arribute_level_id" value="<%=attributeDetails.get("ID")%>">
						<s:submit name="submit" value="Save" cssClass="btn btn-primary" id="submitButton"></s:submit>
					</div>
				</td>
			</tr>

		</table>

	</s:form>
</div>
