<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
$(document).ready( function () {
	$("#submitButton").click(function(){
		$(".validateRequired").prop('required',true);
	});
	
	$("#strLevel").multiselect().multiselectfilter();
});	
</script>

<%
	Map<String,String> attributeList=(Map<String,String>)request.getAttribute("attributeList");
	UtilityFunctions uF=new UtilityFunctions();
	Map<String,String> attributeDetails=(Map<String,String>)request.getAttribute("attributeDetails");
	if(attributeDetails==null)attributeDetails=new HashMap<String,String>();
%>
<div>

	<s:form id="frmId11" method="POST" theme="simple" action="AddAppraisalLevelForAttribute">
		<s:hidden name="attributeid"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="elementid"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		  
		<table class="table table_no_border">
		 	<tr>
				<td style="text-align: right;">Element:<sup>*</sup></td>
				<td><%=attributeList.get("ELEMENT_NAME")!=null ? attributeList.get("ELEMENT_NAME") : "" %></td>
			</tr>
			<tr>
				<td style="text-align: right;">Attribute Name:<sup>*</sup></td>
				<td><%=attributeList.get("ATTRIBUTE_NAME")!=null ? attributeList.get("ATTRIBUTE_NAME") : "" %></td>
			</tr>

			<%-- <tr>
				<td valign="top" style="text-align: right;">Attribute Description<sup>*</sup></td>
				<td><%=attributeList.get(3)!=null ? attributeList.get(3) : "" %></td>
			</tr> --%>
			<%if(attributeList.get("ATTRIBUTE_INFO")!=null){ %>
			<tr>
				<td style="text-align: right;">System Information:<sup>*</sup></td>
				<td>
				<% 
					String attributeinfo="";
					
					if(attributeList.get("ATTRIBUTE_INFO").equals("1")){
						attributeinfo="Punctuality KPI";
					}else if(attributeList.get("ATTRIBUTE_INFO").equals("2")){
						attributeinfo="Attendance KPI";
					}else if(attributeList.get("ATTRIBUTE_INFO").equals("3")){
						attributeinfo="Efforts KPI";
					}else if(attributeList.get("ATTRIBUTE_INFO").equals("4")){
						attributeinfo="Work Performance KPI";
					}else if(attributeList.get("ATTRIBUTE_INFO").equals("5")){
						attributeinfo="Quality of Work KPI";
					} 
				
				%>
				<%=attributeinfo %>					
				</td>
			</tr>
			<%} %>
			<tr>
				<td style="text-align: right;">Level:<sup>*</sup></td>
				<td> 
				<%if(attributeDetails.get("LEVEL_ID")!=null){%>
					<%=attributeDetails.get("Level_NAME") %>
					
					<s:hidden name="strLevel"></s:hidden>
				<%}else{%>
				<s:select theme="simple" name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName"
					  multiple="true" cssClass="validateRequired"></s:select> <!-- headerKey="" headerValue="select Level" -->
                 <%} %>               
                 </td>
			</tr>
			<tr>
				<td style="text-align: right;">Threshhold Value:<sup>*</sup></td>
				<td><s:textfield name="attributeThreshhold"
						cssClass="validateRequired" id="attributeThreshholdValue" 
						cssStyle="border: 0; color: #f6931f; font-weight: bold;" readonly="true"/></td>
			</tr> 
			
			<tr>
				<td></td>
				<td>
					<script>
					$(function() {
						$("#slider").slider({
							value : <%=uF.parseToInt(attributeDetails.get("THRESHOLD")) %>,
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
				<div id="slider" style="width:200px;"></div>
				</td>
			</tr>

			<tr>
				<td colspan="2">
				<div style="float: left; margin-left: 100px;">
					<input type="hidden" name="arribute_level_id" id="arribute_level_id" value="<%=attributeDetails.get("ID") %>">
					<s:submit name="submit" value="Save" cssClass="btn btn-primary" id="submitButton"></s:submit>
				</div>
				</td>
			</tr>

		</table>

	</s:form>
</div>
