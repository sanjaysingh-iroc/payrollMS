<%
String str = (String)request.getParameter("NN");
%>

 

<div class="aboveform">



<%if(str!=null && str.equalsIgnoreCase("R11")){%>
	<jsp:include page="../innerNav/M_R_11.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R12")){%>
	<jsp:include page="../innerNav/M_R_12.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R13")){%>
	<jsp:include page="../innerNav/M_R_13.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R14")){%>
	<jsp:include page="../innerNav/M_R_14.jsp" flush="true" />
<%}%>

</div>



