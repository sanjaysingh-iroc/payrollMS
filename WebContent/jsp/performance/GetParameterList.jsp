<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.performance.FillAttribute"%>
<%@page import="java.util.List"%>
<% 
Map<String,List<List<String>>> hmElementAttribute=(Map<String,List<List<String>>>)request.getAttribute("hmElementAttribute");
if(hmElementAttribute == null ) hmElementAttribute = new HashMap<String,List<List<String>>>(); 
List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
%>


<ul >
<% 
	//System.out.println("elementouterList ---> "+elementouterList);
	for(int i=0;elementouterList!=null && !elementouterList.isEmpty() &&  i<elementouterList.size();i++){
	List<String> innerList=elementouterList.get(i);
	
	%>
	<li>
	
	<strong><%=innerList.get(1)%></strong> 
       <ul>
       <%
     int count=0;
       List<List<String>> attributeouterList1=hmElementAttribute.get(innerList.get(0).trim());
       //System.out.println("attributeouterList1 ---> "+attributeouterList1);
       for(int j=0;attributeouterList1!=null && !attributeouterList1.isEmpty() && j<attributeouterList1.size();j++){
		List<String> attributeList1=attributeouterList1.get(j);
		%>
		
	<li >
	<input type="checkbox" value="<%=attributeList1.get(0) %>" name="checkParam" checked="checked" onclick="showPerformanceReport();showAnalysisSummary();"/>
	<%=attributeList1.get(1) %>
	</li>
	 
	<%} %>
      
       </ul>  
         
	
	
	
	</li>
<%}

%>
</ul>





<%-- <table>
                  <%
                  List<FillAttribute> attributeList =(List<FillAttribute>)request.getAttribute("paramList");
                  for(int i=0;attributeList!=null && !attributeList.isEmpty() && i<attributeList.size();i++){
                  %>
                  	<tr>
                  	<td class="textblue"><input type="checkbox" value="<%=(String)((FillAttribute)attributeList.get(i)).getId() %>" name="checkParam" checked="checked" onclick="showPerformanceReport();"/></td>
                  	<td><%=(String)((FillAttribute)attributeList.get(i)).getName() %></td> 
                  	</tr>
                  <%} %>
                  </table> --%>