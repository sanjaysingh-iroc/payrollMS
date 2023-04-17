<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
 


<%UtilityFunctions uF = new UtilityFunctions(); %>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Thank You" name="title"/>
</jsp:include>


<div class="reportWidth">

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "")%> 


Our executive will get back to you at an earliest to resolve your query.



</div>