<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
%>


<% 
	int proId = (Integer)request.getAttribute("proId"); 
	List<List<String>> milestoneList = (List<List<String>>)request.getAttribute("milestoneList");
%>
	<s:form id="formProjectMilestones" cssClass="formcss" action="ProjectMilestones" name="formProjectMilestones" method="post" enctype="multipart/form-data" theme="simple">
		<div style="float: left; margin: 5px; width: 98%;" id="tblDiv">
		<h3>Milestones</h3>
			<s:hidden name="proId"></s:hidden>

			<table class="tb_style" style="font-size: 11px; width: 99%;">
				<tr>
					<td colspan="2" class="txtlabel" nowrap="nowrap">Milestone Name</td>
					<td class="txtlabel" nowrap="nowrap">Milestone Description</td>
					<td class="txtlabel" nowrap="nowrap"><%=(String)request.getAttribute("dependentOn") %></td>
					<td class="txtlabel" nowrap="nowrap">Milestone Amount</td>
				</tr>
			<% 
				for(int i=0; milestoneList!= null && !milestoneList.isEmpty() && i<milestoneList.size(); i++) { 
					List<String> innerList = milestoneList.get(i);
			%>
				<tr>
					<td valign="top" style="width: 2%;"><%=innerList.get(5) %></td>
					<td valign="top"><%=innerList.get(1) %></td>
					<td valign="top"><%=innerList.get(2) %></td>
					<td valign="top" class="alignRight"><%=innerList.get(3) %></td>
					<td valign="top" class="alignRight"><%=innerList.get(4) %></td>
				</tr>
			<% } %>
			</table>
			<div style="width: 97%; float: left; margin: 5px; text-align: right;"> <a href="PreAddNewProject1.action?operation=E&pro_id=<%=proId %>&step=4"> go to edit Milestone</a></div>
		</div>

<%-- 		<div style="margin: 0px 0px 0px 120px">
			<table class="table_style">
				<tr>
					<td>
					<s:submit value="Save" cssClass="input_button" cssStyle="float:right; margin-right: 5px;" name="submit"></s:submit>
					<input type="button" value="Cancel" class="cancel_button" style="float:right; margin-right: 5px;" name="cancel" onclick="closeForm();">
					</td>
					<td></td>
				</tr>
			</table>
		</div>
 --%>
	</s:form>

