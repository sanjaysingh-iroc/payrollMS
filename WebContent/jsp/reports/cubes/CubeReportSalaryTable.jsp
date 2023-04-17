<jsp:include page="../../common/Links.jsp" flush="true"></jsp:include>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%

Map hmSalaryHeadsMap = (Map)session.getAttribute("hmSalaryHeadsMap");
Map hmSalary = (Map)session.getAttribute("hmSalary");
Map hmSalaryEmp = (Map)session.getAttribute("hmSalaryEmp");
Map hmEmployeeNameMap = (Map)session.getAttribute("hmEmployeeNameMap");
List alSalaryHeadsSelected = (List)session.getAttribute("alSalaryHeadsSelected");
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
		    				<td class="reportHeading alignCenter" colspan="<%=alSalaryHeadsSelected.size()%>">Paycycle <%=arrDates[2] %><br/> [<%=arrDates[0] %> - <%=arrDates[1] %>]</td>	
		    			<%
	    			}
    			
    		
    		%>
    			
    		</tr>
    		
    		
    		<tr>
			<td class="reportLabel alignLeft" nowrap="nowrap"></td>
			<%
			
			for(int k=0; k<alDates.size(); k++){

				for(int i=0; i<alSalaryHeadsSelected.size(); i++){ 
	    			%>
	    			<td class="reportLabel alignRight"><%=uF.showData((String)hmSalaryHeadsMap.get((String)alSalaryHeadsSelected.get(i)), "-")%></td>
	    			<%
				}
			}
    		%>
		</tr>	

    <%
    Set set = hmSalaryEmp.keySet();
    Iterator it = set.iterator();
    while(it.hasNext()){
    	String strEmpId = (String)it.next();
    	%>
    	<tr>
			<td class="reportLabel alignLeft" nowrap="nowrap"><%=hmEmployeeNameMap.get(strEmpId)%></td>
			<%
			for(int k=0; k<alDates.size(); k++){
				String []arrDates = (String[])alDates.get(k); 
				Map hmTemp = (Map)hmSalary.get(strEmpId+arrDates[2]);
				if(hmTemp==null){
					hmTemp = new HashMap();
				}
				for(int i=0; i<alSalaryHeadsSelected.size(); i++){  
	    			%>
	    			<td class="reportLabel alignRight"><%=uF.showData((String)hmTemp.get((String)alSalaryHeadsSelected.get(i)), "0")%></td>	
	    			<%
	    		}
			}
    		%>
		</tr>	
		<%
    }
    %>
    
    </table>