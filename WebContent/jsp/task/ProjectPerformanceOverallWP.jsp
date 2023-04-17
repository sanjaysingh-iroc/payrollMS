<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script>
    $(function() {
        $( "#f_start" ).datepicker({dateFormat: 'dd/mm/yy'});
        $( "#f_end" ).datepicker({dateFormat: 'dd/mm/yy'});
    });
</script>
<%CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); %>

<%
String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
String strTitle = "Project Performance";

if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.MANAGER)){
	strTitle = "My Performance";
}
%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
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
  <a class="<%=((strAction.equalsIgnoreCase("ProjectPerformanceOverallWP.action")?"current":"next")) %>" href="ProjectPerformanceOverallWP.action">Working Projects</a> |      
  <a class="<%=((strAction.equalsIgnoreCase("ProjectPerformanceOverallCP.action")?"current":"next")) %>" href="ProjectPerformanceOverallCP.action">Completed Projects</a>
</div>



<s:form action="ProjectPerformanceOverallWP" name="frmProPerformance" id="idProProPerformance" theme="simple">
<div class="filter_div">
	<div class="filter_caption">Filter</div>
	<s:textfield cssStyle="width:80px" name="f_start" id="f_start" value="From Date" onblur="fillField(this.id, 3);" onclick="clearField(this.id);"></s:textfield>
	<s:textfield cssStyle="width:80px" name="f_end" id="f_end" value="To Date" onblur="fillField(this.id, 4);" onclick="clearField(this.id);"></s:textfield>
	<input type="submit" class="input_button" style="margin:0" value="Search">
</div>
</s:form>


<h3>Service wise</h3>
<%-- 
<table class="tb_style">
				<tr>
					<th width="20%">&nbsp;</th>
					<th width="10%">&nbsp;</th>
					<th width="40%" colspan="4">Money</th>
					<!-- <td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="30%" colspan="3">Time</th> -->
				</tr>	
				<tr>
					<th width="20%">Service Name</th>
					<th width="10%">Indicator</th>
					<th width="10%">Profit Margin<br/>(%)</th>
					<th width="10%">Gross Profit<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th> 
					<th width="10%">Billable Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<th width="10%">Actual Cost<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<!-- <td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="10%">Estimated Time<br/>(hrs)</th>
					<th width="10%">Time Spent<br/>(hrs)</th>
					<th width="10%">Deadline</th> -->
					
				</tr>
				<%
				List<List<String>> alOuter=(List<List<String>>)request.getAttribute("alOuter");
				for(int i=0;i<alOuter.size();i++){
					List<String> alInner=alOuter.get(i);
				%>
				<tr>
					<td <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(1) %></td>
					<td align="center" <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(2) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(3) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(4) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(5) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(6) %></td>
					<td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<td class="alignRight padRight20" <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(7) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(8) %></td>
					<td align="center" <%=((i==alOuter.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(9) %></td>
				</tr>
				<%} %>  
			</table>
				
				
		 --%>		
				
				
				
				
				
				
				
				
				
				
				
					


<%
Map hmProPerformaceBillableService = (Map)request.getAttribute("hmProPerformaceBillableService"); 
Map hmProPerformaceActualService = (Map)request.getAttribute("hmProPerformaceActualService");
Map hmProPerformaceBudgetService = (Map)request.getAttribute("hmProPerformaceBudgetService");
Map hmProPerformaceProjectNameService = (Map)request.getAttribute("hmProPerformaceProjectNameService");
Map hmProPerformaceProjectProfitServiceA = (Map)request.getAttribute("hmProPerformaceProjectProfitServiceA");
Map hmProPerformaceProjectProfitServiceP = (Map)request.getAttribute("hmProPerformaceProjectProfitServiceP");
Map hmProPerformaceProjectAmountIndicatorService = (Map)request.getAttribute("hmProPerformaceProjectAmountIndicatorService");
List alServiceId = (List)request.getAttribute("alServiceId");

%>

<table class="tb_style">
	
		<tr>
			<th width="20%">&nbsp;</th>
			<th width="10%">&nbsp;</th>
			<th width="40%" colspan="4">Money</th>
			
		</tr>
		
		<tr>
			<th width="20%">Service Name</th>
			<th width="10%">Indicator</th>
			<!-- <th width="10%">Profit Margin<br/>(%)</th> -->
			<th width="10%">Gross Profit<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
		<th width="10%">Actual Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th> 
		<th width="10%">Billable Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
		</tr>
		<%
		for(int i=0; i<alServiceId.size(); i++){
		%>
		<tr>
			<td><%=hmProPerformaceProjectNameService.get((String)alServiceId.get(i)) %></td>
		<td class="alignRight padRight20"><%=hmProPerformaceProjectAmountIndicatorService.get((String)alServiceId.get(i)) %></td>
		<%-- <td class="alignRight padRight20"><%=hmProPerformaceProjectProfitServiceP.get((String)alServiceId.get(i)) %></td> --%>
		<td class="alignRight padRight20"><%=hmProPerformaceProjectProfitServiceA.get((String)alServiceId.get(i)) %></td>
		<td class="alignRight padRight20"><%=hmProPerformaceActualService.get((String)alServiceId.get(i)) %></td>
		<td class="alignRight padRight20"><%=hmProPerformaceBillableService.get((String)alServiceId.get(i)) %></td>
		</tr>
		<%} %>
		
	</table>



		
		
		
				
				
				
				
				
				
				
				
				
				
			<%-- <br/><br/>	
			<h3>Project wise</h3> 
				
			<table class="tb_style">
			
				<tr>
					<th width="20%">&nbsp;</th>
					<th width="10%">&nbsp;</th>
					<th width="40%" colspan="4">Money</th>
					<!-- <td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="30%" colspan="3">Time</th> -->
				</tr>
				
				<tr>
					<th width="20%">Project Name</th>
					<th width="10%">Indicator</th>
					<th width="10%">Profit Magrin <br/>(%)</th>
					<th width="10%">Gross Profit<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th> 
					<th width="10%">Billable Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<th width="10%">Actual Cost<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<!-- <td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="10%">Estimated Time<br/>(hrs)</th>
					<th width="10%">Time Spent<br/>(hrs)</th>
					<th width="10%">Deadline</th> -->
				</tr>
				<%
				List<List<String>> alOuter1=(List<List<String>>)request.getAttribute("alOuter1");
				for(int i=0;i<alOuter1.size();i++){
					List<String> alInner=alOuter1.get(i);
				%>
				<tr>
					<td <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>> <%=alInner.get(1) %></td>
					<td align="center" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(2) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(3) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(4) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(5) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(6) %></td>
					<td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(7) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(8) %></td>
					<td align="center" <%=((i==alOuter1.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(9) %></td>
				</tr>
				<%} %>
				
				</table> --%>
			
			<br/><br/>
			<h3>WorkLocation wise</h3>
		<%-- 		
			<table class="tb_style">	
			
				<tr>
					<th width="20%">&nbsp;</th>
					<th width="10%">&nbsp;</th>
					<th width="40%" colspan="4">Money</th>
					<!-- <td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="30%" colspan="3">Time</th> -->
				</tr>
				<tr>
					<th width="20%">WorkLocation Name</th>
					<th width="10%">Indicator</th>
					<th width="10%">Profit Margin <br/>(%)</th>
					<th width="10%">Gross Profit<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th> 
					<th width="10%">Billable Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<th width="10%">Actual Cost<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<!-- <td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<th width="10%">Estimated Time<br/>(hrs)</th>
					<th width="10%">Time Spent<br/>(hrs)</th>
					<th width="10%">Deadline</th> -->
				</tr>
				<%
				List<List<String>> alOuter2=(List<List<String>>)request.getAttribute("alOuter2");
				for(int i=0;alOuter2!=null && i<alOuter2.size();i++){
					List<String> alInner=alOuter2.get(i);
				%>
				<tr>
					<tr>
					<td <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(1) %></td>
					<td align="center" <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(2) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(3) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(4) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(5) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(6) %></td>
					<td width="5%" style="border: 0px solid #fff">&nbsp;</td>
					<td class="alignRight padRight20" <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(7) %></td>
					<td class="alignRight padRight20" <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(8) %></td>
					<td align="center" <%=((i==alOuter2.size()-1)?"style=\"border-top: 2px solid #000\"":"") %>><%=alInner.get(9) %></td>
				</tr>
				<%} %>
				</table>
				
		
		 --%>
		
		
		
		
		
		
		
		
		


<%
Map hmProPerformaceBillable = (Map)request.getAttribute("hmProPerformaceBillable"); 
Map hmProPerformaceActual = (Map)request.getAttribute("hmProPerformaceActual");
Map hmProPerformaceBudget = (Map)request.getAttribute("hmProPerformaceBudget");
Map hmProPerformaceActualTime = (Map)request.getAttribute("hmProPerformaceActualTime");
Map hmProPerformaceIdealTime = (Map)request.getAttribute("hmProPerformaceIdealTime");
Map hmProPerformaceProjectName = (Map)request.getAttribute("hmProPerformaceProjectName");
Map hmProPerformaceProjectManager = (Map)request.getAttribute("hmProPerformaceProjectManager");
Map hmProPerformaceProjectProfitA = (Map)request.getAttribute("hmProPerformaceProjectProfitA");
Map hmProPerformaceProjectProfitP = (Map)request.getAttribute("hmProPerformaceProjectProfitP");
Map hmProPerformaceProjectAmountIndicator = (Map)request.getAttribute("hmProPerformaceProjectAmountIndicator");
Map hmProPerformaceProjectTimeIndicator = (Map)request.getAttribute("hmProPerformaceProjectTimeIndicator");
List alProjectId = (List)request.getAttribute("alProjectId");





%>
	<table class="tb_style">
			
				<tr>
					<th width="20%">&nbsp;</th>
					<th width="10%">&nbsp;</th>
					<th width="40%" colspan="4">Money</th>
					
				</tr>
				
				<tr>
					<th width="20%">WorkLocation Name</th>
					<th width="10%">Indicator</th>
					<th width="10%">Profit Margin<br/>(%)</th>
					<th width="10%">Gross Profit<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
					<th width="10%">Actual Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th> 
					<th width="10%">Billable Amount<br/>(<%=CF.getStrCURRENCY_SHORT() %>)</th>
				</tr>
				<%
					for(int i=0; i<alProjectId.size(); i++){
				%>
				<tr>
					<td><%=hmProPerformaceProjectName.get((String)alProjectId.get(i)) %></td>
					<td class="alignRight padRight20"><%=hmProPerformaceProjectAmountIndicator.get((String)alProjectId.get(i)) %></td>
					<td class="alignRight padRight20"><%=hmProPerformaceProjectProfitP.get((String)alProjectId.get(i)) %></td>
					<td class="alignRight padRight20"><%=hmProPerformaceProjectProfitA.get((String)alProjectId.get(i)) %></td>
					<td class="alignRight padRight20"><%=hmProPerformaceActual.get((String)alProjectId.get(i)) %></td>
					<td class="alignRight padRight20"><%=hmProPerformaceBillable.get((String)alProjectId.get(i)) %></td>
				</tr>
				<%} %>
				
				</table>



		
		
		
		
		
		
<br/><br/><br/>		
				
<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i><!-- <img src="images1/icons/denied.png" width="17"> --></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Billable</div>
</div>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"> <!-- <img src="images1/icons/re_submit.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Budgeted and Actual &lt; Billable</div>
</div>


<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"> <!-- <img src="images1/icons/approved.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &lt; Budgeted</div>
</div>
				
		</div>
		