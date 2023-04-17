<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
List<String> learningPlanList = (List<String>) request.getAttribute("learningPlanList");
if(learningPlanList == null) learningPlanList = new ArrayList<String>();


if(learningPlanList!=null || !learningPlanList.isEmpty()){
%>
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