<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
 
         <s:select theme="simple" name="wLocation" id="wLocation" cssClass="validateRequired" listKey="wLocationId" listValue="wLocationName" 
			headerKey="" headerValue="Select Location" list="wLocationList" />
::::
         <s:select theme="simple" name="service" cssClass="validateRequired" listKey="serviceId" headerKey="" headerValue="Select SBU"
			listValue="serviceName" list="serviceList" key="" />
::::
         <s:select theme="simple" name="department" id="department" cssClass="validateRequired" listKey="deptId" listValue="deptName" 
         	headerKey="" headerValue="Select Department" list="deptList" onchange="showCurrentOrgSelectedDepartment();"/>
::::
         <s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" cssClass="validateRequired" 
				headerKey="" headerValue="Select Level" onchange="getDesig(this.value);"></s:select>