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


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Requisitions" name="title"/>
</jsp:include>

<div class="leftbox reportWidth" >


<!-- the tabs -->
<ul class="tabs">
	<li><a class="<%=strClass%>" href="MyRequests.action">My Requisitions</a></li>
	<li><a class="<%=strClass%>" href="BonafideRequests.action">Bonafide</a></li>
	<li><a class="<%=strClass%>" href="InfraRequests.action">Infrastructure</a></li>
	<li><a class="<%=strClass%>" href="OtherRequests.action">Others</a></li>
	
</ul>
 
	<div class="panes">
		<div style="display:block"></div>
	</div>

</div>