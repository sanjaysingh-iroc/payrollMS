<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>


	<s:select name="location" theme="simple" listKey="wLocationId" listValue="wLocationName" cssClass="validateRequired"  
		 list="workLocationList" /><!-- headerKey="" headerValue="Select Work Location" -->
		
		