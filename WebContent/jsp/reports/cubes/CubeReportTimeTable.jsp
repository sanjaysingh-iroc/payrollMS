<jsp:include page="../../common/Links.jsp" flush="true"></jsp:include>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>

<%@page import="java.util.*"%>
<%

List alDates = (List)session.getAttribute("alDates");
List alReportLabel = (List)session.getAttribute("alReportLabel");
Map hmRoster = (Map)session.getAttribute("hmRoster");
Map hmRosterEmp = (Map)session.getAttribute("hmRosterEmp");
Map hmActual = (Map)session.getAttribute("hmActual");
Map hmActualEmp = (Map)session.getAttribute("hmActualEmp");
Map hmEmployeeName = (Map)session.getAttribute("hmEmployeeName");
UtilityFunctions uF = new UtilityFunctions();

%>

 
<table>

<tr>
		<td class="reportHeading alignCenter"></td>
		<%
			for(int k=0; k<alDates.size(); k++){
			String []arrDates = (String[])alDates.get(k);	
			
		%>
    	<td class="reportHeading alignCenter" colspan="<%=alReportLabel.size() %>" >Paycycle <%=arrDates[2] %><br/> [<%=arrDates[0] %> - <%=arrDates[1] %>]</td>
		<%} %>    			
</tr>

<tr>
<td class="reportHeading alignCenter">Employee</td>    		
<%
for(int k=0; k<alDates.size(); k++){
	for(int i=0; i<alReportLabel.size(); i++){ %>
		<td class="reportHeading alignCenter"><%=(String)alReportLabel.get(i) %></td>
	<%}
} %>
</tr>


<%

	Set set = null;
	Iterator it = null;
	if(hmRosterEmp!=null){
		set = hmRosterEmp.keySet();	
	}else if(hmActualEmp!=null){
		set = hmActualEmp.keySet();	
	} 
	
	if(set!=null){
		it = set.iterator();	
	}
	
	while(it!=null && it.hasNext()){
		String strEmpId = (String)it.next();	

	%>
	<tr>
	<td class="reportLabel alignRight"><%=(String)hmEmployeeName.get(strEmpId) %></td>
	<%
	
		for(int k=0; k<alDates.size(); k++){
			String []arrDates = (String[])alDates.get(k);	

			if(hmRosterEmp!=null){
				%>
					<td class="reportLabel alignRight"><%=uF.showData((String)hmRoster.get(strEmpId+arrDates[2]), "0")%></td>				
				<%	
			} if(hmActualEmp!=null){
				%>
					<td class="reportLabel alignRight"><%= uF.showData((String)hmActual.get(strEmpId+arrDates[2]), "0") %></td>				
				<%	
			} 
	
		} %>
</tr>
<%} %>
</table>