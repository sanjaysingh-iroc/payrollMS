<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%

UtilityFunctions uF = new UtilityFunctions();

Map hmSararyHistoryMap = (Map)request.getAttribute("hmSararyHistoryMap");
Map hmSalaryMap = (Map)request.getAttribute("hmSalaryMap");
Map hmSalaryDetails = (Map)request.getAttribute("hmSalaryDetails");
List alEmpSalaryDetailsEarning = (List)request.getAttribute("alEmpSalaryDetailsEarning");
List alEmpSalaryDetailsDeduction = (List)request.getAttribute("alEmpSalaryDetailsDeduction");
List alEmpSalaryMonths = (List)request.getAttribute("alEmpSalaryMonths");


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
 */

%>


<%if(alEmpSalaryMonths.size()>0){ %>

<table class="tb_style">
<tr>
<td>&nbsp;</td>
		<%
			
		for(int j=0; j<alEmpSalaryMonths.size(); j++){
			%>
			<th><%=uF.getDateFormat((String)alEmpSalaryMonths.get(j) , "MM-yyyy", "MMM")%>, <%=uF.getDateFormat((String)alEmpSalaryMonths.get(j) , "MM-yyyy", "yy")%></th>
			<%
		}
	%>
</tr>
	<%	
		
			for(int i=0; i<alEmpSalaryDetailsEarning.size(); i++){
				%>
				<tr>
				<%
								
				for(int j=0; j<alEmpSalaryMonths.size(); j++){
					Map hm = (Map)hmSalaryMap.get((String)alEmpSalaryMonths.get(j));
					
					if(j==0){
						%>
					<th align="left"><%=uF.showData((String)hmSalaryDetails.get((String)alEmpSalaryDetailsEarning.get(i)), "") %></th>	
						<%
					}
					
					%>
					<td align="right"><%=uF.showData((String)hm.get((String)alEmpSalaryDetailsEarning.get(i)),"0") %></td>
					<%	
				}
				%>
				</tr>
				<%
			}
		
			for(int i=0; i<alEmpSalaryDetailsDeduction.size(); i++){
				%>
				<tr>
				<%
								
				for(int j=0; j<alEmpSalaryMonths.size(); j++){
					Map hm = (Map)hmSalaryMap.get((String)alEmpSalaryMonths.get(j));
					
					if(j==0){
						%>
					<th align="left"><%=uF.showData((String)hmSalaryDetails.get((String)alEmpSalaryDetailsDeduction.get(i)), "") %></th>
						<%
					}
					%>
					<td align="right"><%=uF.showData((String)hm.get((String)alEmpSalaryDetailsDeduction.get(i)), "0") %></td>
					<%	
				}
				%>
				</tr>
				<%
			}
						
		%>
	
	
<tr>
<th>Net Salary</th>
		<%
			
		for(int j=0; j<alEmpSalaryMonths.size(); j++){
			Map hm = (Map)hmSalaryMap.get((String)alEmpSalaryMonths.get(j));
			%>
			<td align="right"><strong><%=uF.showData((String)hm.get("GROSS"), "")%></strong></td>
			<%
		}
	%>
</tr>			
	
	</table>	
	
	
	<%} else{%>
	
	<div class="msg nodata"><span>No salary has been paid yet.</span></div>
	<%}%>