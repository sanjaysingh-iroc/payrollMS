<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.*"%>

<%
String strClass="";
String strRT = (String)request.getParameter("RT");

%>


<script>
// perform JavaScript after the document is scriptable.
$(function() {
	// setup ul.tabs to work as tabs for each div directly under div.panes 
	$("ul.tabs").tabs("div.panes > div", {effect: 'ajax'});
});
</script>


<%

List alParentNavL = (List) session.getAttribute("alParentNavL");
Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");

List alParentNavR = (List) session.getAttribute("alParentNavR");
Map hmChildNavR = (Map) session.getAttribute("hmChildNavR");
Map hmNavigation = (Map) session.getAttribute("hmNavigation");
Map hmNavigationParent = (Map) session.getAttribute("hmNavigationParent");



%>


<div class="pagetitle">
      <span>Setup Company</span>
</div>

<div class="leftbox reportWidth" >


<!-- the tabs -->
<ul class="tabs">


<%
 
 
 List alList = (List) hmChildNavL.get("1");
 if(alList==null)alList=new ArrayList();
 for(int i=0; i<alList.size(); i++){
 	Navigation nav = (Navigation)alList.get(i);
 	%>
 	
 	
 	<%
 	List alListC = (List) hmChildNavL.get(nav.getStrNavId());
 	for(int ic=0; ic<alListC.size(); ic++){
 		if(alListC==null)alListC=new ArrayList();
 		Navigation navC = (Navigation)alListC.get(ic);
 		String str = navC.getStrAction();
 		str = str.replace(".action", "Tab.action");
 		%>
 	
 	<li><a class="<%=strClass%>" href="<%=str%>"><%=navC.getStrLabel()%></a></li>
 		
 		<%
 	}
 	%>
 	
 	<%
 }
 %>


	
	<%-- <li><a class="<%=strClass%>" href="OtherRequests.action">Others</a></li> --%>
	
</ul>
 
	<div class="panes">
		<div style="display:block"></div>
	</div>

</div>