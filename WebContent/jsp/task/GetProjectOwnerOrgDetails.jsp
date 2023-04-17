<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:select theme="simple" name="organisation" id="organisation" cssClass="validateRequired" listKey="orgId" listValue="orgName" list="organisationList" key="" onchange="getLocationOrganization(this.value);" />
::::
<s:select theme="simple" cssClass="validateRequired" name="location" listKey="wLocationId" listValue="wLocationName" list="workLocationList" />
::::
<s:select theme="simple" name="strSBU" id="strSBU" listKey="serviceId" cssClass="validateRequired" listValue="serviceName" list="sbuList" key="" required="true" />
::::
<s:select theme="simple" name="strDepartment" listKey="deptId" cssClass="validateRequired" listValue="deptName" list="departmentList" key="" required="true" /> 