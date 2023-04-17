<%@ taglib prefix="s" uri="/struts-tags"%>

<%String type = (String)request.getAttribute("type"); %>
<%if(type.equals("org")){ %>
<s:if test="workList != null">
   <s:select theme="simple" name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" headerKey=""
       headerValue="All WorkLocation" required="true" value="{userlocation}" onchange="getEmployeebyLocation();" cssStyle="width:150px;"></s:select>
</s:if>
::::
<s:if test="departmentList != null">
    <s:select theme="simple" name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
        headerValue="All Department" required="true" onchange="getEmployeebyDepart();" cssStyle="width:150px;"></s:select>
</s:if> 
::::
<s:if test="levelList != null">
    <s:select theme="simple" name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" headerKey=""
        headerValue="All Level" required="true" onchange="getEmployeebyLevel()" cssStyle="width:150px;"></s:select>
</s:if>
::::
<s:if test="desigList != null">
    <s:select theme="simple" name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
        headerKey="" headerValue="All Designation" required="true" onchange="getEmployeebyDesig();" cssStyle="width:150px;"></s:select>
</s:if>
<% } %>

<%if(type.equals("wloc")){ %>
<s:if test="departmentList != null">
    <s:select theme="simple" name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
        headerValue="All Department" required="true" onchange="getEmployeebyDepart();" cssStyle="width:150px;"></s:select>
</s:if>
<% } %>

<%if(type.equals("level")){ %>
<s:if test="desigList != null">
    <s:select theme="simple" name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
        headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();" cssStyle="width:150px;" required="true"></s:select>
</s:if>
<% } %>