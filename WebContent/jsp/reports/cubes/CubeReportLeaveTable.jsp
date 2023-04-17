<jsp:include page="../../common/Links.jsp" flush="true"></jsp:include>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%

Map hmOuter = (Map)session.getAttribute("hmOuter");
Map hmEmpOuter = (Map)session.getAttribute("hmEmpOuter");

Map hmEmployeeNameMap = (Map)session.getAttribute("hmEmployeeNameMap");
List alLeaveType = (List)session.getAttribute("alLeaveType");
List alDates = (List)session.getAttribute("alDates");


UtilityFunctions uF = new UtilityFunctions();

%>




<table cellpadding="3" cellspacing="1">





			<tr>
    			<td class="reportHeading alignCenter"></td>
    			
    			<%
    			for(int i=0; i<alDates.size(); i++){
    			String []arrDates = (String[])alDates.get(i); 
    			%>
    			<td class="reportHeading alignCenter" colspan="<%=alLeaveType.size() * 3%>">Paycycle <%=arrDates[2] %><br/> [<%=arrDates[0] %> - <%=arrDates[1] %>]</td>	
    			<%
    		}
    		
    		%>
    		
    		</tr>

    	<tr>
    		<td class="reportHeading alignCenter">Employee</td>
    		
    		<%
    		for(int k=0; k<alDates.size(); k++){
	    		for(int i=0; i<alLeaveType.size(); i++){
	    			%>
	    			<td class="reportHeading alignCenter" colspan="3"><%=(String)alLeaveType.get(i)%></td>	
	    			<%
	    		}
    		}
    		%>
    		
    	</tr>
    
    	<tr>
			<td class="reportLabel alignLeft" nowrap="nowrap"></td>
			<%
			for(int k=0; k<alDates.size(); k++){
				
	    		for(int i=0; i<alLeaveType.size(); i++){
	    			%>
	    			<td class="reportLabel alignRight">Pending</td>
	   				<td class="reportLabel alignRight">Approved</td>
					<td class="reportLabel alignRight">Denied</td>	
	    			<%
	    		}
			}
    		%>
		</tr>	
	    
    
    <%
    Set set = hmEmpOuter.keySet();
    Iterator it = set.iterator();
    while(it.hasNext()){
    	String strEmpId = (String)it.next();
    	%>
    	<tr>
			<td class="reportLabel alignLeft" nowrap="nowrap"><%=hmEmployeeNameMap.get(strEmpId)%></td>
			<%
			for(int k=0; k<alDates.size(); k++){
				String []arrDates = (String[])alDates.get(k); 
				Map hmTemp = (Map)hmOuter.get(strEmpId+arrDates[2]);
				if(hmTemp==null)hmTemp = new HashMap();
    			for(int i=0; i<alLeaveType.size(); i++){
	    			%>
	    			<td class="reportLabel alignRight"><%=uF.showData((String)hmTemp.get((String)alLeaveType.get(i)+"_WAITING"), "0")%></td>
	    			<td class="reportLabel alignRight"><%=uF.showData((String)hmTemp.get((String)alLeaveType.get(i)+"_APPROVED"), "0")%></td>
	    			<td class="reportLabel alignRight"><%=uF.showData((String)hmTemp.get((String)alLeaveType.get(i)+"_DENIED"), "0")%></td>	
	    			<%
    			}
    		}
    		%>
		</tr>	
		<%
    }
    %>
    
    </table>