<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="desigList != null">

	<%-- <select name="strDesignationUpdate" id="strDesignationUpdate<%=cnt %>" class="validate[required]" onchange ="getGrades(this.value, '<%=cnt %>')">
	<option value="">Select Designation</option>
	<%=request.getAttribute("sbDesig") %>
	</select> --%>
	<s:if test="fromPage == null || fromPage != 'AddPeople'">
		<s:select theme="simple" name="strDesignation" id="strDesignation" listKey="desigId" cssClass="validateRequired" listValue="desigCodeName" 
			headerKey="" headerValue="Select Designation" list="desigList" key="" required="true" onchange="getGrades(this.value)" />
	</s:if>
	<s:if test="fromPage != null && fromPage == 'AddPeople'">
		<s:select theme="simple" name="strDesignation" id="strDesignation" listKey="desigId" cssClass="validateRequired" listValue="desigCodeName" 
			headerKey="" headerValue="Select Designation" list="desigList" key="" required="true" />
	</s:if>
</s:if> 

<s:if test="gradeList != null">

	<s:if test="fromPage == null">
		<s:if test="typeEA == 'promotion'">
			<s:select theme="simple" name="empGrade" id="empGrade" cssClass="validateRequired" list="gradeList" 
			listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" onchange="getGradeSalaryStructure(this.value)"/>
		</s:if>
		<s:else>
			<s:select theme="simple" name="empGrade" id="empGrade" cssClass="validateRequired" list="gradeList" 
			listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" />
		</s:else>
	</s:if>
	<s:if test="fromPage != null && fromPage == 'filter'">
		<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" multiple="true"/>
	</s:if>

</s:if>
