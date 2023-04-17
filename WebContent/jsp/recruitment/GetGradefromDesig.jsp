<%@ taglib prefix="s" uri="/struts-tags"%>


	<s:if test="pagefrom=='addpanel'">
	<s:select name="empGrade"  list="gradeList"  id="gradeIdV" theme="simple"
	listKey="gradeId" listValue="gradeCode" headerKey=""  onchange="getEmployeebyGrade(this.value);"
				headerValue="Select Grade" required="true" cssStyle="width:170px" ></s:select>

	</s:if>
	<s:else>
	<s:select name="strGrade"  list="gradeList" id="empGrade" theme="simple" listKey="gradeId" listValue="gradeCode" headerKey=""
		cssClass="validateRequired" headerValue="Select Grade" required="true"></s:select>
	</s:else>

    	