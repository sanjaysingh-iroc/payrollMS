<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script>
    $(function() {
        $( "#f_start" ).datepicker({dateFormat: 'dd/mm/yy'});
        $( "#f_end" ).datepicker({dateFormat: 'dd/mm/yy'});
    });
</script>



<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Profitability By Work Location" name="title"/>
</jsp:include>
 
 

<div class="leftbox reportWidth">
 
<%
String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
String strQuery = (String)request.getAttribute("javax.servlet.forward.query_string");


if(strAction!=null){
	strAction = strAction.replace(request.getContextPath()+"/","");
	
	if(strQuery!=null && strQuery.indexOf("NN")>=0){
		strAction = strAction+"?"+strQuery;
	}
}

%>
<div style="margin-bottom: 20px">
  <a class="<%=((strAction.equalsIgnoreCase("ProfitabilityByService.action")?"current":"next")) %>" href="ProfitabilityByService.action">ProfitabilityByService</a> |      
  <a class="<%=((strAction.equalsIgnoreCase("ProfitabilityByIndustry.action")?"current":"next")) %>" href="ProfitabilityByIndustry.action">ProfitabilityByIndustry</a> |
  <a class="<%=((strAction.equalsIgnoreCase("ProfitabilityByWLocation.action")?"current":"next")) %>" href="ProfitabilityByWLocation.action">ProfitabilityByWLocation</a>
</div>



<s:form action="ProfitabilityByWLocation" name="frmProPerformance" id="idProProPerformance" theme="simple">
<div class="filter_div">
	<div class="filter_caption">Filter</div>
	<s:textfield cssStyle="width:80px" name="f_start" id="f_start" value="From Date" onblur="fillField(this.id, 3);" onclick="clearField(this.id);"></s:textfield>
	<s:textfield cssStyle="width:80px" name="f_end" id="f_end" value="To Date" onblur="fillField(this.id, 4);" onclick="clearField(this.id);"></s:textfield>
	<input type="submit" class="input_button" style="margin:0" value="Search">
</div>
</s:form>


<div style="width:30%;float:left">
        
<display:table name="alOuter" cellspacing="1" class="tb_style" export="true" pagesize="50" id="lt1" requestURI="ProfitabilityByWLocation.action" width="90%">
	
	<display:setProperty name="export.excel.filename" value="Profitability.xls" />
	<display:setProperty name="export.xml.filename" value="Profitability.xml" />
	<display:setProperty name="export.csv.filename" value="Profitability.csv" />
	
	<display:column nowrap="nowrap" title="WLocation Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
	<display:column nowrap="nowrap" title="Billable Amount" styleClass="alignRight padRight20" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
	<display:column nowrap="nowrap" title="Actual Cost" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
	<display:column nowrap="nowrap" title="Profit" styleClass="alignRight padRight20" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
	
</display:table>

</div>

<div style="width:55%;float:right">
<jsp:include page="/jsp/task/ProjectProfitabilityChart.jsp"></jsp:include>
</div>

</div>
