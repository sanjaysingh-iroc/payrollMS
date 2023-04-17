

<%-- <%
System.out.println("ID => " + request.getAttribute("id") + "  SYSDIV => " + request.getAttribute("sysdiv") + "  NEWLVLNO => " + request.getAttribute("newlvlno")
		+ "  WEIGHTAGE => " + request.getAttribute("weightage") + "  TYPE => " + request.getAttribute("type"));
%> --%>
 <% if(request.getAttribute("type") == null){ %>
 <iframe id="iframe" style=" width:100%; height: 800px;" src="openMyReviewSectionAndSubsection.action?id=<%=request.getAttribute("id") %>&appFreqId=<%=request.getAttribute("appFreqId") %>&sysdiv=<%=request.getAttribute("sysdiv") %>
&newlvlno=<%=request.getAttribute("newlvlno") %>&weightage=<%=request.getAttribute("weightage") %>&fromPage=<%=request.getAttribute("fromPage") %>" frameborder="0"  name="myone"></iframe>
 <%} %>
 
 <% if(request.getAttribute("type") != null){ %>
 <iframe id="iframe" style=" width:100%; height: 800px;" src="openMyReviewSectionAndSubsection.action?id=<%=request.getAttribute("id") %>&appFreqId=<%=request.getAttribute("appFreqId") %>&sysdiv=<%=request.getAttribute("sysdiv") %>
&MLID=<%=request.getAttribute("MLID") %>&type=<%=request.getAttribute("type") %>&newsysno=<%=request.getAttribute("newsysno") %>&subWeightage=<%=request.getAttribute("subWeightage") %>
&linkDiv=<%=request.getAttribute("linkDiv") %>&divCount=<%=request.getAttribute("divCount") %>&linkType=<%=request.getAttribute("linkType") %>&fromPage=<%=request.getAttribute("fromPage") %>" frameborder="0"  name="myone"></iframe>
 <%} %>
 
  