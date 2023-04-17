<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.*"%>

<%
UtilityFunctions uF = new UtilityFunctions();
List alLinks = (List)request.getAttribute("alLinks");
if(alLinks==null)alLinks  = new ArrayList();
String strTitle = (String)request.getAttribute("strTitle");
strTitle = uF.showData(strTitle, "");
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String strNN = (String)request.getParameter("NN");

%>

  

<div class="aboveform">

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Requests" name="title"/>
</jsp:include>





<script type="text/javascript">
jQuery(document).ready(function() {
  
  jQuery(".content1").show();
  //toggle the componenet with class msg_body 
  jQuery(".close_div").click(function()
  { 
    jQuery(this).next(".content1").slideToggle(500);
	$(this).toggleClass("heading_dash");
  });
  jQuery("#content1").show();
}); 
</script>
 




<div class="leftbox reportWidth">

<%
List alParentNavL = (List) session.getAttribute("alParentNavL");
Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");

List alParentNavR = (List) session.getAttribute("alParentNavR");
Map hmChildNavR = (Map) session.getAttribute("hmChildNavR");
Map hmNavigation = (Map) session.getAttribute("hmNavigation");
Map hmNavigationParent = (Map) session.getAttribute("hmNavigationParent");


CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
String []arrEnabledModules = CF.getArrEnabledModules();
String []arrAllModules = CF.getArrAllModules();

 
 List alList = (List) hmChildNavL.get("360");
 if(alList==null)alList=new ArrayList();
 for(int i=0; i<alList.size(); i++){
 	Navigation nav = (Navigation)alList.get(i);
 	%>
 	
 	<div class="dashboard_linksholder comp_info" style="width:99%;">
	<h2 class="close_div">- <%=nav.getStrLabel() %> <p style="font-size:10px;font-weight:normal"><%=uF.showData(nav.getStrDescription(), "-") %></p></h2>
	<div class="content1">
 	<%
 	List alListC = (List) hmChildNavL.get(nav.getStrNavId());
 	if(alListC==null)alListC=new ArrayList();
 	for(int ic=0; ic<alListC.size(); ic++){ 		
 		Navigation navC = (Navigation)alListC.get(ic);
 		%>
 		<div class="mg_ln"> <a href="<%=navC.getStrAction() %>"><%=navC.getStrLabel() %></a>
	       <div class="clr"></div>
	       <span class="infotext"><%= uF.showData(navC.getStrDescription(), "-")  %></span>
	    </div>
 		<%
 	}
 	%>
 	</div>
 	</div>
 	<div class="clr"></div>
 	<%
 }
 %>
 
 


</div>

</div>


