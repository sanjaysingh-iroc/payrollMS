<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%

UtilityFunctions uF = new UtilityFunctions();

Map hmTotalSalary = (Map)request.getAttribute("hmTotalSalary");
Map hmEmpNameMap = (Map)request.getAttribute("hmEmpNameMap");
Map hmSalaryDetails = (Map)request.getAttribute("hmSalaryDetails");
Map hmEmpSalary = (Map)request.getAttribute("hmEmpSalary");
Map hmServices = (Map)request.getAttribute("hmServices");

List alEmp = (List)request.getAttribute("alEmp");
List alEmpSalaryDetailsEarning = (List)request.getAttribute("alEmpSalaryDetailsEarning");
List alEmpSalaryDetailsDeduction = (List)request.getAttribute("alEmpSalaryDetailsDeduction");
List alEmpIdPayrollG = (List)request.getAttribute("alEmpIdPayrollG");

 
Map hmPresentDays = (Map)request.getAttribute("hmPresentDays");
Map hmLeaveDays = (Map)request.getAttribute("hmLeaveDays");
Map hmLeaveTypeDays 	= (Map)request.getAttribute("hmLeaveTypeDays");
String strTotalDays = (String)request.getAttribute("strTotalDays");


/* out.println("<br/><br/>hmTotalSalary="+hmTotalSalary);
out.println("<br/><br/>hmEmpNameMap="+hmEmpNameMap);
out.println("<br/><br/>hmSalaryDetails="+hmSalaryDetails);
out.println("<br/><br/>hmEmpSalary="+hmEmpSalary);
out.println("<br/><br/>hmServices="+hmServices);
out.println("<br/><br/>alEmp="+alEmp);
out.println("<br/><br/>alEmpSalaryDetailsEarning="+alEmpSalaryDetailsEarning);
out.println("<br/><br/>alEmpSalaryDetailsDeduction="+alEmpSalaryDetailsDeduction);
out.println("<br/><br/>alEmpIdPayrollG="+alEmpIdPayrollG);
out.println("<br/><br/>hmPresentDays="+hmPresentDays);
out.println("<br/><br/>hmLeaveDays="+hmLeaveDays);
out.println("<br/><br/>strTotalDays="+strTotalDays);
out.println("<br/><br/>hmTotalSalary="+hmTotalSalary);
 */

  
 
%>

		<%
		
		String strEmpId = (String)request.getParameter("emp_id");
		Map hmInner = (Map)hmTotalSalary.get(strEmpId);
		if(hmInner==null)hmInner = new HashMap();
		
		Map hmLeaves = (Map)hmLeaveDays.get(strEmpId);
		if(hmLeaves==null)hmLeaves = new HashMap();
		
		Map hmLeavesType = (Map)hmLeaveTypeDays.get(strEmpId);
		if(hmLeavesType==null)hmLeavesType = new HashMap();
		
		%>	
			
			

        	
			<table class="tb_style" width="100%">
				<tr>
					<th>Present Days</th><th>Paid Leaves</th><th>Total Days</th>
					
				</tr>
			
				<tr>
					<td align="center"><%=uF.parseToDouble((String)hmPresentDays.get(strEmpId))%></td>
					<td align="center"><%=uF.parseToDouble((String)hmLeavesType.get("COUNT"))%></td>
					<td align="center"><%=uF.parseToInt(strTotalDays)%></td>
				</tr>
				
						
			</table>

		<table style="width:100%">	
			
			<tr>
				<td valign="top">
			
			
			<table class="tb_style">
			
			<tr><th colspan="2">Earnings</th></tr>
			
				<%
						for(int i=0; i<alEmpSalaryDetailsEarning.size(); i++){
							String strAmount = (String)hmInner.get((String)alEmpSalaryDetailsEarning.get(i));
						
					%>
					<tr>
						<td class="	alignCenter" nowrap>
						<%=(String)hmSalaryDetails.get((String)alEmpSalaryDetailsEarning.get(i))%>
						</td>
						
						<td class="alignRight">
							<%=uF.formatIntoTwoDecimal(uF.parseToDouble(strAmount))%>
						</td>
					</tr>	
					<%} %>
			</table>
			</td>
			<td valign="top" align="right">
			
			<table class="tb_style">
				<tr><th colspan="2">Deductions</th></tr>
			
					<%
						for(int i=0; i<alEmpSalaryDetailsDeduction.size(); i++){
							String strAmount = (String)hmInner.get((String)alEmpSalaryDetailsDeduction.get(i));
						
					%>
					<tr>
						<td class="alignCenter" nowrap>
						<%=(String)hmSalaryDetails.get((String)alEmpSalaryDetailsDeduction.get(i))%>
						</td>
						
						<td class="alignRight">
							<%=uF.formatIntoTwoDecimal(uF.parseToDouble(strAmount))%>
						</td>
					</tr>
					<%} %>
					
			</table>
			
			</td>
			</tr>
			</table>
			
			<table width="100%">
			<tr>
			
		
			
			<td valign="top">
			
			<table class="tb_style" style="width:100%">
				<tr>
					<th><strong>Gross Earning</strong></th>
					<td align="right"><strong><%=uF.showData((String)hmInner.get("GROSS"), "0")%><strong></td>
				</tr>
			
				<tr>
					<th><strong>Net Earning</strong></th>
					<td align="right"><strong><%=uF.showData((String)hmInner.get("NET"), "0")%></strong></td>
				</tr>
				
			</table>
			
			</td>
			</tr>
			
			
			</table>
			
