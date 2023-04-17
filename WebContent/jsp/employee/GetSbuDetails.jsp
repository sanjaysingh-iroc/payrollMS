<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.select.FillDepartment"%>
<%@page import="com.konnect.jpms.select.FillServices"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String parentDept = (String)request.getAttribute("parentDept");
	String orgId = (String)request.getAttribute("orgId");
	String strSerivce = (String)request.getAttribute("strSerivce");
	String type = (String)request.getAttribute("type");
	if(strSerivce != null && !strSerivce.equals("") && type != null && type.equals("DEPT")) {
%>
	
				<s:select theme="simple" cssClass="valiadate[required]" list="departList" name="parentDept" id="parentDept" 
						 headerKey="0" headerValue="No Parent Department" listKey="deptId" listValue="deptName"></s:select>	
<% } else if(orgId != null && !orgId.equals("") && type != null && type.equals("SBU")) { %>		
				<s:select theme="simple" list="serviceList" name="strSerivce" id="strSerivce" listKey="serviceId" listValue="serviceName"
						onchange="getDepartDetails();" headerKey="0" headerValue="No SBU"/>
<% } else if(orgId != null && !orgId.equals("") && type != null && type.equals("LEVEL")) { %>		
				<s:select theme="simple" list="levelList" name="strParentId" listKey="levelId" listValue="levelCodeName" headerKey="0" 
				headerValue="No Parent" />
<% } %>	
	
	
	<%--
	//if(uF.parseToInt(service_id)>0){
--%>
	<%-- <tr id="trSBUid">
					<td class="txtlabel alignRight">&nbsp;</td>
					<td>
						<input type="hidden" name="strSerivce" value="<%=service_id %>" />
					</td>
		</tr> 
<%
	}else{
%>
	<tr id="trSBUid">
					<td class="txtlabel alignRight">SBU<sup>*</sup>:</td>
					<td>
						<s:select theme="simple" cssClass="valiadate[required]" list="serviceList" name="strSerivce" listKey="serviceId" listValue="serviceName"/>	
						<span class="hint">Select SBU to add.<span class="hint-pointer">&nbsp;</span></span>
					</td>
	</tr>
<%}%> --%>