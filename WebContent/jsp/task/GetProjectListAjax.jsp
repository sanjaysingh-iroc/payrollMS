<%@taglib prefix="s" uri="/struts-tags" %>

<td id="projectListId" style="display:none;" class="txtlabel alignRight" >

<%-- <td valign="top">
<s:select theme="simple" label="Select Project" name="p_id" listKey="projectID"
onchange="getActivityList(this.value);" listValue="projectName" headerKey="" headerValue="Select Project" 
	           list="projectdetailslist" key="" />
</td> --%>

<!-- ===start parvez date: 18-02-2022=== -->
<% 
	String fromPage = (String)request.getParameter("fromPage");
	if(fromPage != null && fromPage.equalsIgnoreCase("AID")){ %>
		
		<td valign="top">
			<s:select theme="simple" label="Select Project" name="p_id" listKey="projectID" listValue="projectName" 
			onchange="AddProjectAmount(this.value);" list="projectdetailslist" key="" multiple="true" />
		</td>
	<% } else { %>
	
		<td valign="top">
			<s:select theme="simple" label="Select Project" name="p_id" listKey="projectID"
			onchange="getActivityList(this.value);" listValue="projectName" headerKey="" headerValue="Select Project" 
			           list="projectdetailslist" key="" />
		</td>
	
	<% }  %>
<!-- ===end parvez date: 18-02-2022=== -->

<%-- <tr id="projectListId" style="display:none;">
					<td class="txtlabel alignRight"  valign="top" >Select Project<sup>*</sup></td>
					<td  id="projectListId"><s:select theme="simple" label="Select Project" name="p_id" listKey="projectID"
onchange="getActivityList(this.value);" listValue="projectName" headerKey="" headerValue="Select Project" 
	           list="projectdetailslist" key="" required="true" />
					</td>
				</tr> --%>


