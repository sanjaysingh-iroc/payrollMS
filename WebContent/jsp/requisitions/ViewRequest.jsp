<%
String strClass="";
String strRT = (String)request.getParameter("RT");
String strFromDate = (String)request.getAttribute("FROM_DATE");
String strToDate = (String)request.getAttribute("TO_DATE");
String strFromTime = (String)request.getAttribute("FROM_TIME");
String strToTime = (String)request.getAttribute("TO_TIME");
String strName = (String)request.getAttribute("NAME");
String strType = (String)request.getAttribute("TYPE");
String strPurpose = (String)request.getAttribute("PURPOSE");
String strMode = (String)request.getAttribute("MODE");

%>

<div>
<%if(strRT!=null && strRT.equalsIgnoreCase("BF")){%>

	<%=strPurpose%>
	
<%}%>

<%if(strRT!=null && strRT.equalsIgnoreCase("IR")){%>

	<%=strPurpose%>
	
<%}%>

</div>