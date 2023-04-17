<jsp:include page="../../common/Links.jsp" flush="true"></jsp:include>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%

Map hmSalary = (Map)request.getAttribute("hmSalary");
Map hmEmployeeNameMap = (Map)request.getAttribute("hmEmployeeNameMap");
Map hmSalaryHeadsMap = (Map)request.getAttribute("hmSalaryHeadsMap");
String []currentPayCycle = (String[])request.getAttribute("currentPayCycle");

UtilityFunctions uF = new UtilityFunctions();

%>

Salary Chart