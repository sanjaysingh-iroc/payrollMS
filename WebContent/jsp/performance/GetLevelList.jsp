<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="java.util.List"%>
<table>
                  <%
                  List<FillLevel> levelList=(List<FillLevel>)request.getAttribute("levelList");
                  	for(int i=0;levelList!=null && !levelList.isEmpty() && i<levelList.size();i++){
                  %>
                  	<tr>
                  	<td class="textblue"><input type="checkbox" value="<%=(String)((FillLevel)levelList.get(i)).getLevelId() %>" name="checkLevel" checked="checked" onclick="getParameterByLevel();"/></td>
                  	<td><%=(String)((FillLevel)levelList.get(i)).getLevelCodeName() %></td> 
                  	</tr>
                  <%} %>
                  </table>