<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <%String type = (String)request.getAttribute("type"); %>
<%if(type.equals("org")){ %> --%>
<s:if test="workList1 != null">
   <s:select theme="simple" name="strWlocation1" list="workList1" id="wlocation1" listKey="wLocationId" listValue="wLocationName" headerKey=""
       headerValue="All WorkLocation" required="true" value="{userlocation1}" onchange="getEmployeebyLocation1();" cssStyle="width:150px;"></s:select>
</s:if>
::::
<s:if test="departmentList1 != null">
    <s:select theme="simple" name="strDepart1" list="departmentList1" id="depart1" listKey="deptId" listValue="deptName" headerKey=""
        headerValue="All Department" required="true" onchange="getEmployeebyDepart1();" cssStyle="width:150px;"></s:select>
</s:if>
::::
<s:if test="levelList1 != null">
    <s:select theme="simple" name="strLevel11" list="levelList1" listKey="levelId" id="strLevel11" listValue="levelCodeName" headerKey=""
        headerValue="All Level" required="true" onchange="getEmployeebyLevel1()" cssStyle="width:150px;"></s:select>
</s:if>
::::
<s:if test="designationList1 != null">
    <s:select theme="simple" name="strDesignation1" list="designationList1" listKey="desigId" id="desigIdV1" listValue="desigCodeName"
        headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig1();" cssStyle="width:150px;"></s:select>
</s:if>
<%-- <% } %>

<%if(type.equals("wloc")){ %>
<s:if test="departmentList1 != null">
    <s:select theme="simple" name="strDepart1" list="departmentList1" id="depart1" listKey="deptId" listValue="deptName" headerKey=""
        headerValue="All Department" required="true" onchange="getEmployeebyDepart1();" cssStyle="width:150px;"></s:select>
</s:if>
<% } %>

<%if(type.equals("level")){ %>
<s:if test="designationList1 != null">
    <s:select theme="simple" name="strDesignation1" list="designationList1" listKey="desigId" id="desigIdV1" listValue="desigCodeName"
        headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig1();" cssStyle="width:150px;"></s:select>
</s:if>
<% } %> --%>