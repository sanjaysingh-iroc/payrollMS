<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.konnect.jpms.util.*"%>

<%
	UtilityFunctions uF=new UtilityFunctions();// Start Dattatray Date:05-July-21
%>
<div class="skill_div" style="width: 80px;">
	<input type="hidden" id="intPlannedCount" value="<%=uF.showData((String)request.getAttribute("Output"),"0") %>"/><!-- Created by Dattatray Date:05-July-21  -->
     <p class="sk_value" style="text-align:center"><%=(String)request.getAttribute("Output")%></p>             
     <p class="sk_name" style="text-align:center">Planned</p>                
</div>

<div class="skill_div" style="width: 80px;">
     <p class="sk_value" style="text-align:center"><%=(String)request.getAttribute("strExistCount")%></p>             
     <p class="sk_name" style="text-align:center">Exists</p>                
</div>