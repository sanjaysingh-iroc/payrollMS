<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:if test="projectList != null">
	<s:select theme="simple" name="f_project" id="f_project" listKey="id" listValue="name" list="projectList" key="" multiple="true" />
</s:if>