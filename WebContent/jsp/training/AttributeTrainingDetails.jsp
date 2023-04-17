<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript">

	function getTrainingData(value) {
		var action = 'GetTrainingDetails.action?plan_id=' + value;
		getContent('idAttributeTrainingInfo', action);
	}	
	
	$('#formID1').submit(function(event){
		event.preventDefault();
		var form_data = $('#formID1').serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type:'POST',
			url:'AttributeTrainingDetails.action',
			data:form_data,
			success:function(result){
				$('#divResult').html(result);
			}
		});
	});
</script>
<%
	String fromPage = (String) request.getAttribute("fromPage");
	List<List<String>> outerList = (List<List<String>>) request.getAttribute("outerList");
	if(outerList == null)outerList=new ArrayList<List<String>>();
	List<String> learningPlanList = (List<String>) request.getAttribute("learningPlanList");
	if(learningPlanList == null) learningPlanList = new ArrayList<String>();
%>

<div id="idAttributeTraining">
	<div style="float: left; left: 70%; top: 46px; width: 45%; padding: 5px; margin: 10px;">
		<s:form id="formID1" action="AttributeTrainingDetails" theme="simple" name="frmAttributeTraining">
			<s:hidden name="empid"></s:hidden>
			<s:hidden name="attribute_id"></s:hidden>
			<s:hidden name="tgap_id"></s:hidden>
			<input type="fromPage" name="fromPage" id="fromPage" value="<%=fromPage %>"/>
			<table class="table" style="width: 100%">
				<tr>
					<th></th>
					<th>Learning Name</th>
				</tr>
				<%
					for (int i = 0; outerList != null && i < outerList.size(); i++) {
						List<String> innerList = outerList.get(i);
				%>
						<tr>
							<td><input type="radio" name="plan_id" value="<%=innerList.get(0)%>" onclick="getTrainingData(this.value);" <%if (i == 0) {%> checked="checked" <%}%>>
							</td>
							<td><%=innerList.get(1)%></td>
						</tr>
				<% }
				%>
			</table>
			<div> <s:submit cssClass="btn btn-primary" value="Assign" name="submit" /></div>
		</s:form>
	</div>
	<div id="idAttributeTrainingInfo" style="float: left; left: 70%; top: 46px; width: 350px; padding: 5px; margin: 10px;">
		<% if (learningPlanList != null && !learningPlanList.isEmpty()) { %>
			 <div id="idTrainingplanInfo" style="border: 2px solid #ccc; padding:2px;">
				<b>Learning Plan Details</b>
				<div style="width: 96%">
					<table class="tb_style" width="100%">
						<tr>
							<th width="18%" align="right" nowrap="nowrap">Learning Plan</th>
							<td><%=learningPlanList.get(1)%></td>
						</tr>
						<tr>
							<th width="18%" align="right" nowrap="nowrap">Objective</th>
							<td><%=learningPlanList.get(2)%></td>
						</tr>
						<tr>
							<th valign="top" align="right" nowrap="nowrap">Aligned with</th>
							<td colspan="1"><%=learningPlanList.get(3)%></td>
						</tr>
						<tr>
							<th valign="top" align="right" nowrap="nowrap">Certificate</th>
							<td><%=learningPlanList.get(6)%></td>
						</tr>
						<tr>
							<th align="right" nowrap="nowrap">Effective Date</th>
							<td><%=learningPlanList.get(8)%></td>
						</tr>
						<tr>
							<th align="right" nowrap="nowrap">Due Date</th>
							<td><%=learningPlanList.get(9)%></td>
						</tr>
						<tr>	
							<th align="right" nowrap="nowrap">Attribute</th>
							<td><%=learningPlanList.get(5)%></td>
						</tr>
						<tr>
							<th align="right" nowrap="nowrap">Skills</th>
							<td><%=learningPlanList.get(7)%></td>
						</tr>
						<tr>	
							<th align="right" nowrap="nowrap">Assignee</th>
							<td><%=learningPlanList.get(4)%></td>
						</tr>
						<tr>	
							<th align="right" nowrap="nowrap">Type</th>
							<td><%=learningPlanList.get(10)%></td>
						</tr>
					</table>
				</div>
			</div>
		<%}%>
	</div>
</div>
