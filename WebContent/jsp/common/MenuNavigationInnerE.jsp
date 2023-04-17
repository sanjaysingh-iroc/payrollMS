<%
String str = (String)request.getParameter("NN");
%>



<div class="aboveform">

  

<%if(str!=null && str.equalsIgnoreCase("TM")){ %>
	<jsp:include page="../innerNav/E_TM.jsp" flush="true" />
<%}


else if(str!=null && str.equalsIgnoreCase("R10")){%>
	<jsp:include page="../innerNav/E_R_10.jsp" flush="true" />
<%}%>

</div>



