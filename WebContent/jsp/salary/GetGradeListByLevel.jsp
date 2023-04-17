<%@ taglib prefix="s" uri="/struts-tags"%>

<s:select theme="simple" name="strGrade" id="strGrade" cssClass="validateRequired" list="gradeList" 
	listKey="gradeId" listValue="gradeCode" required="true" multiple="true"/>