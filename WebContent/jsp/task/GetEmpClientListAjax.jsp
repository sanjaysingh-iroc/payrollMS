<%@taglib prefix="s" uri="/struts-tags" %>

<td id="clientListId" style="display:none;" class="txtlabel alignRight" >

<td valign="top">
<s:select theme="simple" name="strClient" listKey="clientId"
listValue="clientName" headerKey="" headerValue="Select Client" 
	          list="clientlist" key="" required="true" 
	          onchange="getContent('typeP', 'GetProjectClientTask.action?client_id='+this.value)"/>
</td>

