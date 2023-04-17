<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <s:select name="skills"  theme="simple"
							listKey="skillsName" listValue="skillsName" list="skillslist"
							 multiple="true" cssClass="validate[required] chosen-select-no-results" /> --%>
<s:select name="essentialSkills" theme="simple" listKey="skillsId" listValue="skillsName" list="essentialSkillsList"
							 multiple="true" cssClass="validateRequired" />
::::
<s:select name="skills"  theme="simple" listKey="skillsId" listValue="skillsName" list="skillslist"
							 multiple="true" cssClass="validateRequired" />