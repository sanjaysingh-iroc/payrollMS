<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<style>
.lazy{
border-radius:50px;}
</style>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
%>
<div class="tableblock" style="padding: 5px;">
	<% 
		String type = (String) request.getAttribute("type");
		if(type == null || !type.equals("MyTeam")) {
	%>
	<%
	List<Map<String, String>> alTeamMember = (List<Map<String, String>>) request.getAttribute("alTeamMember");
	if(alTeamMember == null) alTeamMember = new ArrayList<Map<String, String>>(); 
	%>
	
	<div style="padding: 5px;">
    	<div style="width:100%; float:left;"> 
    	<ul class="users-list clearfix">
		<%
			for(int i = 0; i < alTeamMember.size(); i++){
				Map<String, String> hmInner = (Map<String, String>) alTeamMember.get(i);
		%>
				
			       <li>
			       	   <%if(docRetriveLocation==null) { %>
							<img class="lazy img-circle"  src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmInner.get("EMP_IMAGE")%>" title="<%=hmInner.get("EMP_NAME") %>"/>
					   <%} else { %>
		                	<img class="lazy img-circle"  src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmInner.get("EMP_ID")+"/"+IConstants.I_60x60+"/"+hmInner.get("EMP_IMAGE")%>" title="<%=hmInner.get("EMP_NAME") %>"/>
		               <%} %>
			           <span class="users-list-name"><%=hmInner.get("EMP_NAME") %></span>
			       </li>     
			    
                  
		<%} %>
		</ul>
		<% if(alTeamMember == null || alTeamMember.isEmpty() || alTeamMember.size() == 0) { %>
			<span>No team available.</span>
		<%} %> 
		</div>
	</div>
	
	<% } else { %>
	<% 	Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
	 	if(hmEmp==null) hmEmp=new HashMap<String,String>();
		Map<String,String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
		if(empImageMap==null) empImageMap=new HashMap<String,String>();
	%>
	
		<div style="width:100%; float:left;">
		<ul class="users-list clearfix">
		<%
			Iterator<String> it=hmEmp.keySet().iterator();
			int i=0;
            while(it.hasNext()) {
				String empId=it.next();
				String empName=hmEmp.get(empId);
		%>

				<li>
		       	   <a href="javascript:void(0);" onclick="getEmpProfile('<%=empId %>');" ><%if(docRetriveLocation==null) { %>
						<img class="lazy img-circle"  src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(empId.trim())%>"/>
				   <%} else { %>
	                	<img class="lazy img-circle"  src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId.trim()+"/"+IConstants.I_60x60+"/"+empImageMap.get(empId.trim())%>"/>
	               <%} %>
		           <span class="users-list-name"><%=empName %></span></a>
		       </li>   
				
			<% } %>
			</ul>
			<% if(hmEmp == null || hmEmp.isEmpty()) { %>
				<div class="tdDashLabel">No team available.</div>
			<% } %>
		</div>
	<% } %>
</div>

<script type="text/javascript">
//$("img.lazy1").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy1").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy1").trigger("sporty") }, 1000);
});  
</script>