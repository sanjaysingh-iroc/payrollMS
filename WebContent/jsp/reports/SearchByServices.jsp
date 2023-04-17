<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script>
  $(function() {
        $( "#idDate" ).datepicker();
    });

</script>


<div class="aboveform">

<div class="belowform">
<s:form action="SearchByServices"
	method="POST" >


Select Date &nbsp;&nbsp;&nbsp;<input type="text" id="idDate" name="D"/>


<s:select label="Cost Centre" name="service" listKey="serviceId"
		listValue="serviceName" headerKey="0" headerValue="Choose Cost Centres"
		list="serviceList" key="" required="true" onchange="this.form.submit();" />




	
	
</s:form>
</div>



<h4>Cost Centre Report</h4>
<display:table name="reportList" cellspacing="1" class="itis"
	export="true" pagesize="15" id="lt" requestURI="SearchByServices.action">

	<display:setProperty name="export.excel.filename" value="EmployeeServiceReport.xls" />
	<display:setProperty name="export.xml.filename" value="EmployeeService.xml" />
	<display:setProperty name="export.csv.filename" value="EmployeeServiceReport.csv" />

	<display:column title="Date" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<display:column title="Emp Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<display:column title="Start Time"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
	<display:column title="End Time"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
	
</display:table></div>
