<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%UtilityFunctions uF = new UtilityFunctions();
%>

<div style="float:left;width:100%;">
<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE), "")%>
</div>