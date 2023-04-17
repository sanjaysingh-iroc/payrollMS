
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%
	Map<String,String> hmMemberMP=(Map<String,String>)request.getAttribute("hmMemberMP"); 
	String empID=(String)request.getAttribute("empID");	
%>
<div style="margin-top: 4px; padding-left: 6px;">
	<%=hmMemberMP!=null && hmMemberMP.get(empID)!=null ? hmMemberMP.get(empID) : "" %>
					
</div>