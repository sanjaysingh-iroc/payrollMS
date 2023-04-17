<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript">
function generateEmpYearlySummarySalary()
{
	var paycycle=document.frm_fromEmpYearly.paycycle.value;
	var wlocation=document.frm_fromEmpYearly.wlocation.value;
	var url='EmpSalaryYearlySummaryPdfReports.action?wlocation='+wlocation;
	url+="&paycycle="+paycycle;
	window.location = url;
	}
</script>
<% 
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

String  strMonth = (String)request.getAttribute("strMonth");
String  strWLocation = (String)request.getAttribute("strWLocation");

Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
Map hmEmpName = (Map)request.getAttribute("hmEmpName");
List alMonth = (List)request.getAttribute("alMonth");

List alEarning = (List)request.getAttribute("alEarning");
List alDeduction = (List)request.getAttribute("alDeduction");


if(alEarning==null)alEarning=new ArrayList();
if(alDeduction==null)alDeduction=new ArrayList();

int nMaxSize = Math.max(alEarning.size(), alDeduction.size());

%>





<!-- Custom form for adding new records -->

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Yearly Salary Report" name="title"/>
</jsp:include>


    <div id="printDiv" class="leftbox reportWidth">


		<s:form name="frm_fromEmpYearly" action="EmpSalaryMonthlySummaryReport" theme="simple">
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
			<s:select name="paycycle" listKey="paycycleId" headerValue="Select Paycycle"
						listValue="paycycleName" headerKey="0" 
						onchange="document.frm_fromEmpYearly.submit();"
						list="paycycleList" key="" />
			
			<s:select theme="simple" name="f_org" listKey="orgId" 
                         listValue="orgName"
                         onchange="document.frm_fromEmpYearly.submit();"
                         list="orgList" key=""  />
                         			
			<s:select name="wlocation" listKey="wLocationId" headerValue="Select Work Location"
				listValue="wLocationName" headerKey="0" 
				onchange="document.frm_fromEmpYearly.submit();"
				list="wlocationList" key="" />
				
			<a onclick="generateEmpYearlySummarySalary();" href="javascript:void(0)" class="fa fa-file-pdf-o" > </a>
		</div>
		</s:form>

		   
		
		<br/>
		
		
		<div style="text-align:center;margin:10px" > <h2>Monthly Salary Register: <%=uF.showData(strMonth, "") %> </h2></div>
		
		<div style="overflow:scroll">
		<table cellpadding="3" cellspacing="0" width="100%">
			<tr>			
				<%	int i=0;
					for(i=0; i<alEarning.size(); i++){
				%>
					<td align="right" width="7%" style="border-top:solid 1px #000" nowrap="nowrap"><%=(String)hmSalaryHeadMap.get((String)alEarning.get(i))%></td>
				<%} %>
				
				<%
					for(; i<nMaxSize; i++){
				%>
					<td align="right" width="7%" style="border-top:solid 1px #000">&nbsp;</td>
				<%} %>
				<td align="right" width="7%" style="border-top:solid 1px #000" colspan="" nowrap="nowrap">Total Earning</td>
				<td align="right" width="7%" style="border-top:solid 1px #000" colspan="" nowrap="nowrap">Net Pay</td>
			</tr>
			<tr>
				<%
					for(i=0; i<alDeduction.size(); i++){
				%>
					<td align="right" width="7%" nowrap="nowrap"><%=(String)hmSalaryHeadMap.get((String)alDeduction.get(i))%></td>
				<%} %>
				<%
					for(; i<nMaxSize; i++){
				%>
					<td align="right" width="7%" style="">&nbsp;</td>
				<%} %>
				<td align="right" width="7%" colspan=""nowrap="nowrap">Total Deduction</td>
				<td align="right" width="7%" colspan="">&nbsp;</td>
			</tr>
			
			<tr>
				<td colspan="<%=nMaxSize+2%>" style="border-top:solid 1px #000;border-bottom:solid 1px #000">
				<strong>Location: <%=uF.showData(strWLocation, "") %></strong>    
				<span style="padding-left:40%"><strong>Month: <%=uF.showData(strMonth, "") %></strong></span>
				</td>
			</tr>
			
			<%
			
				Set set = hmEarningSalaryMap.keySet();
				Iterator it = set.iterator();
				while(it.hasNext()){
					String strEmpId = (String)it.next();
					Map hmInner = (Map)hmEarningSalaryMap.get(strEmpId);
					%>
					
					<tr>
						<td colspan="<%=nMaxSize+2%>">
						<span><strong>Emp Code:</strong> <%=(String)hmEmpCode.get(strEmpId) %></span>
						<span style="padding-left:30%"><strong>Emp Name:</strong> <%=(String)hmEmpName.get(strEmpId) %></span>
						</td>
					</tr>
					
					
					<tr>
					<%
					double dblTotalE = 0;
					double dblTotalD = 0;
					for(i=0; i<alEarning.size(); i++){
						String strAmount = (String)hmInner.get((String)alEarning.get(i));
						%>
						<td align="right" class="paddingRight20"><%=uF.showData(strAmount,"0")%></td>
						<%		
						dblTotalE += uF.parseToDouble(strAmount);
					}
					%>
					<%
					for(; i<nMaxSize; i++){
					%>
						<td>&nbsp;</td>
					<%} %>
					<td align="right" class="paddingRight20" colspan=""><%=uF.formatIntoTwoDecimal(dblTotalE)%></td>
					<td align="right" width="7%" colspan="">&nbsp;</td>
					</tr>
					
					<tr>
					<%
					for(i=0; i<alDeduction.size(); i++){
						String strAmount = (String)hmInner.get((String)alDeduction.get(i));
						%>
						<td align="right" class="paddingRight20"><%=uF.showData(strAmount,"0")%></td>
						<%
						dblTotalD += uF.parseToDouble(strAmount);
					}
					%>
					<%
					for(; i<nMaxSize; i++){
					%>
						<td>&nbsp;</td>
					<%} %>
					<td align="right" class="paddingRight20" colspan=""><%=uF.formatIntoTwoDecimal(dblTotalD)%></td>
					<td align="right" width="7%" colspan=""><%=uF.formatIntoTwoDecimal(dblTotalE - dblTotalD)%></td>
					</tr>
					
					
					<tr>
						<td style="border-bottom:dashed 1px #ccc" colspan="<%=nMaxSize+2%>"></td>
					</tr>
					
					<%
				}
				
			%>
			
			<%-- <tr>
				<td><strong>Total</strong></td>
			<%
				for(int i=0; i<alMonth.size(); i++){
					String strTotalAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
					
					%>
					<td align="right" class="paddingRight20"><strong><%=uF.showData(strTotalAmount,"0")%></strong></td>
					<%		
				}
			
			%>
			</tr> --%>
			
		</table>
		</div>
		
		
		
		
    </div>
