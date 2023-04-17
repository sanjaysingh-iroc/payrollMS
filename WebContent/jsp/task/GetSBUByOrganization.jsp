<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

	<s:select theme="simple" name="strSBU" id="strSBU" listKey="serviceId" cssClass="validateRequired" listValue="serviceName" 
		list="sbuList" /> <!-- headerKey="" headerValue="Select SBU" -->

		
		