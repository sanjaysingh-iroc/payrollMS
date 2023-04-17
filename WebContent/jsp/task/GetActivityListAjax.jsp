<%@taglib prefix="s" uri="/struts-tags" %>

<td id="activityListId" style="display:none;" class="txtlabel alignRight" >

<td valign="top">
<s:select theme="simple" label="Select Activity" name="activityId" listKey="activityID"
listValue="activityName" headerKey="" headerValue="Select Activity" 
	          list="activitydetailslist" key="" required="true" />
</td>