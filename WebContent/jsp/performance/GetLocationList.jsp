<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

 <table>
                  <%
                  List<FillWLocation> workList=(List<FillWLocation>)request.getAttribute("locList");
                  	for(int i=0;workList!=null && !workList.isEmpty() && i<workList.size();i++){
                  		//System.out.println("Work Location Name ===========>"+ (String)((FillWLocation)workList.get(i)).getwLocationName());
                  %>
                  	<tr>
                  	<td class="textblue"><input type="checkbox" value="<%=(String)((FillWLocation)workList.get(i)).getwLocationId() %>" name="checkLocation" checked="checked" onclick="getParameterByWorkLocation();"/></td>
                  	<td><%=(String)((FillWLocation)workList.get(i)).getwLocationName() %></td> 
                  	</tr>
                  <%} %>
                  </table>