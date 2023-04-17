



<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="javax.swing.Icon"%>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Database Maintenance" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">

<%=((request.getAttribute(IConstants.MESSAGE)!=null)?request.getAttribute(IConstants.MESSAGE):"") %>
<form action="DBMaintenance.action" method="post">

<input type="submit" class="input_button" value="Start" name="submit"/>
</form>

 
</div>