<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="deptList != null">
<s:select theme="simple" label="Select Department" name="department" listKey="deptId"
		listValue="deptName" headerKey="0" headerValue="Select Department"
		list="deptList" key="" required="true" />
		
</s:if> 
