<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

	<s:select theme="simple" name="strDepartment" listKey="deptId" cssClass="validateRequired" listValue="deptName" 
		list="departmentList" /> <!-- key="" required="true" headerKey="" headerValue="Select Department" -->