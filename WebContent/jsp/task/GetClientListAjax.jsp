<%@taglib prefix="s" uri="/struts-tags" %>

<td id="clientListId" style="display:none;" class="txtlabel alignRight" >

<td valign="top">
<s:select theme="simple" label="Select Client" name="clientId" listKey="clientId"
listValue="clientName" headerKey="" headerValue="Select Client" 
	          list="clientlist" key="" required="true" />
</td>

