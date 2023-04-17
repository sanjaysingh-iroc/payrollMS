<%@page import="java.util.*" %>

<%String filterType=(String)request.getAttribute("filterType"); %>

<table>
<%Map<String,String> mp=(Map<String,String>)request.getAttribute("mp"); 
	Set set=mp.keySet();
	Iterator it=set.iterator();
	while(it.hasNext()){
		String key=(String)it.next();
	%>
		<tr><td><input type="checkbox" value="<%=key %>" name="class1" onclick="onsingleclick('class')" checked /></td><td><%=mp.get(key) %></td></tr>
	<%}
%>
</table>
