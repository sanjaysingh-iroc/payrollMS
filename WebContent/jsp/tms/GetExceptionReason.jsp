<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
String strReason = (String)request.getAttribute("strReason");
if(strReason!=null && !strReason.equals("")){
	out.println("<strong>Employee:</strong><br/>");
	out.println(strReason);
}else{
	out.println("<strong>Employee:</strong>");
	out.println("<br/>NO Reason specified");
}

String strManagerReason = (String)request.getAttribute("strManagerReason");
if(strManagerReason!=null && !strManagerReason.equals("")){
	out.println("<br/><strong>Manager:</strong><br/>");
	out.println(strManagerReason);
}else{
	out.println("<br/><strong>Manager:</strong><br/>");
	out.println("NO Reason specified");
}



%>