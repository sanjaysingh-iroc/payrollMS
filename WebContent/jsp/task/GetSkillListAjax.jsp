<%@taglib prefix="s" uri="/struts-tags" %>

<td id="skillListID" style="display:none;" class="txtlabel alignRight" >
<td valign="top">
<s:select theme="simple" label="Select Skill" name="skill" listKey="skillsName"
multiple="true"  size="6" onclick="getTeamLeadList(this);"
	 cssClass="validateRequired" listValue="skillsName" headerKey="" headerValue="Select Skill" list="skillList" key="" required="true" />
</td>
