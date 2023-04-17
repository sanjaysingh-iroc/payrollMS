<%
String str = (String)request.getParameter("NN");
%>



<div class="aboveform">



<%if(str!=null && str.equalsIgnoreCase("ME11")){ %>
	<jsp:include page="../innerNav/A_ME_11.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("ME12")){%>
	<jsp:include page="../innerNav/A_ME_12.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("ME13")){%>
	<jsp:include page="../innerNav/A_ME_13.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("ME14")){%>
	<jsp:include page="../innerNav/A_ME_14.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("ME15")){%>
	<jsp:include page="../innerNav/A_ME_15.jsp" flush="true" />
<%}



else if(str!=null && str.equalsIgnoreCase("R11")){%>
	<jsp:include page="../innerNav/A_R_11.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R12")){%>
	<jsp:include page="../innerNav/A_R_12.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R13")){%>
	<jsp:include page="../innerNav/A_R_13.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R14")){%>
	<jsp:include page="../innerNav/A_R_14.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R15")){%>
	<jsp:include page="../innerNav/A_R_15.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R16")){%>
	<jsp:include page="../innerNav/A_R_16.jsp" flush="true" />
<%}else if(str!=null && str.equalsIgnoreCase("R17")){%>
	<jsp:include page="../innerNav/A_R_17.jsp" flush="true" />
<%}%>

</div>



