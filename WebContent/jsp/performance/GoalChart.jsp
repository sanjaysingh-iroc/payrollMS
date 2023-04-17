<%@page import="java.util.Arrays"%>
<%@page import="com.konnect.jpms.util.IMessages"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>

<g:compress>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/organisational/jquery.jOrgChart.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/organisational/prettify.css" />
	
	<script src="<%=request.getContextPath()%>/scripts/organisational/prettify.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/scripts/organisational/jquery.jOrgChart.js" type="text/javascript"></script>
</g:compress>



<div class="leftbox reportWidth" style="overflow: auto">

	<g:compress>
		<script>
			jQuery(document).ready(function() {
				$("#org").jOrgChart({
					chartElement : '#chart',
					dragAndDrop : false
				});
				prettyPrint();
			});
		</script>
	</g:compress>

	<%
	CommonFunctions CF= (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map<String, List<String>> hmCorporate = (Map<String, List<String>>) request.getAttribute("hmCorporate");
	Map<String, List<List<String>>> hmManager = (Map<String, List<List<String>>>) request.getAttribute("hmManager");
	Map<String, List<List<String>>> hmTeam = (Map<String, List<List<String>>>) request.getAttribute("hmTeam");
	Map<String, List<List<String>>> hmIndividual = (Map<String, List<List<String>>>) request.getAttribute("hmIndividual");
	Map<String,String> empImageMap= (Map<String,String>)request.getAttribute("empImageMap");
	
	Map<String, String> hmEmpName = (Map<String,String>)request.getAttribute("hmEmpName");
	UtilityFunctions uF = new UtilityFunctions();
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
	%>
	<div id="myDiv" style="background-color: #999999; color: white; font-weight: bold; margin: 5px; text-align: center; width: 100%;"></div>
	
	<ul id="org" style="display: none">

		<%-- <li> <img
			src="<%=request.getContextPath() %>/userImages/<%=CF.getStrOrgLogo() %>"
			height="60px" />
			<ul> --%>
				<%
				Iterator<String> it = hmCorporate.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					List<String> cinnerList = hmCorporate.get(key);
			
			%>

				<li>

					<div class="emp" style="margin-top: 5px;" id="<%=cinnerList.get(3)%>"><%=cinnerList.get(3)%><p style="font-weight: normal;"><%=cinnerList.get(4)%></p></div>
					<%-- <span class="desg_tree"><%="Manager Goal ["+uF.showData(""+hmManager.get(cinnerList.get(0)).size(),"0")+"]" %></span> --%>
				<%
					if(cinnerList.get(29)!=null){ 
					List<String> emplistID=Arrays.asList(cinnerList.get(29).split(","));
				%>

				<div style="float:left;width:100%;">
					<% for(int i=0; emplistID!=null && i<emplistID.size();i++){
						if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
							String empName = hmEmpName.get(emplistID.get(i).trim());
							String empimg=uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
						%>
					<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');"><span style="float:left; width:20px; height:20px; margin:2px;"> <!-- border:1px solid #CCCCCC; -->
						<%-- <img height="20" width="20" border="0" data-original="<%=CF.getStrDocRetriveLocation()+empimg %>" src="userImages/avatar_photo.png" class="lazy"> --%>
						<%if(CF.getStrDocRetriveLocation()==null) { %>
								<img height="20" width="20" class="lazy img-circle"  src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empimg%>" />
						  	<%} else { %>
				                <img height="20" width="20" class="lazy img-circle"  src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
				            <%} %>
					</span></a>
					<%} 
					}%>
				
				</div>
				<%} %> 

					<ul>

				<%
 				List<List<String>> mouterList = hmManager.get(cinnerList.get(0));
		 			for (int j = 0; mouterList != null && j < mouterList.size(); j++) {
	 				List<String> minnerList = mouterList.get(j);
 				%>
					<li>
						<div class="emp" style="margin-top: 5px;" id="<%=minnerList.get(3)%>"><%=minnerList.get(3)%><p style="font-weight: normal;"><%=minnerList.get(4)%></p></div>
							<%-- <span class="desg_tree"><%="Team Goal ["+uF.showData(""+hmTeam.get(minnerList.get(0)).size(),"0")+"]"%></span> --%>
				<%
					if(minnerList.get(29)!=null){ 
					List<String> emplistID=Arrays.asList(minnerList.get(29).split(","));
				%>
						<div style="float:left;width:100%;">
							<% for(int i=0; emplistID!=null && i<emplistID.size();i++){
								if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
									String empName = hmEmpName.get(emplistID.get(i).trim());
									String empimg=uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
								%>
							<%-- <div style="float:left;width:20px;height:20px;margin:2px;border:1px solid #000"><a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');">
							<img src="userImages/<%=empimg %>" height="20"></a></div> --%>
							<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');"><span style="float:left; width:20px; height:20px; margin:2px;"> <!-- border:1px solid #CCCCCC; -->
								<%-- <img height="20" width="20" border="0" data-original="<%=CF.getStrDocRetriveLocation()+empimg %>" src="userImages/avatar_photo.png" class="lazy"> --%>
								<%if(CF.getStrDocRetriveLocation()==null) { %>
										<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png"  data-original="<%=IConstants.DOCUMENT_LOCATION + empimg%>" />
								  	<%} else { %>
						                <img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png"  data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
						            <%} %>
							</span></a>
							<% }  } %>
						</div>
						<%} %> 

							<ul>

						<%
						List<List<String>> touterList = hmTeam.get(minnerList.get(0));
			 				for (int k = 0; touterList != null&& k < touterList.size(); k++) {
			 					List<String> tinnerList = touterList.get(k);
						%>
							<li>
								<div class="emp" style="margin-top: 5px;" id="<%=tinnerList.get(3)%>"><%=tinnerList.get(3)%><p style="font-weight: normal;"><%=tinnerList.get(4)%></p></div>
									<%-- <span class="desg_tree"><%="Individual Goal ["+uF.showData(""+hmIndividual.get(tinnerList.get(0)).size(),"0")+"]"%></span> --%>
						<%
							if(tinnerList.get(29)!=null){ 
							List<String> emplistID=Arrays.asList(tinnerList.get(29).split(","));
						%>
						<div style="float:left;width:100%;">
							<% for(int i=0; emplistID!=null && i<emplistID.size();i++){
								if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
									String empName = hmEmpName.get(emplistID.get(i).trim());
									String empimg=uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
								%>
							<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');"><span style="float:left; width:20px; height:20px; margin:2px;"> <!-- border:1px solid #CCCCCC -->
							<%-- <img src="userImages/<%=empimg %>" height="20"> --%>
								<%-- <img height="20" width="20" border="0" data-original="<%=CF.getStrDocRetriveLocation()+empimg %>" src="userImages/avatar_photo.png" class="lazy"> --%>
								<%if(CF.getStrDocRetriveLocation()==null) { %>
										<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empimg%>" />
								  	<%} else { %>
						                <img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
						            <%} %>
							</span></a>
							<% }  } %>
						</div>
						<%} %> 

							<ul>
 							<%
							List<List<String>> iouterList = hmIndividual.get(tinnerList.get(0));
								for (int a = 0; iouterList != null && a < iouterList.size(); a++) {
								List<String> iinnerList = iouterList.get(a);
							%>
								<li style="background-color: #ECD0F1 !important;">
									<div class="emp" style="margin-top: 5px;" id="<%=iinnerList.get(3)%>"><%=iinnerList.get(3)%><p style="font-weight: normal;"><%=iinnerList.get(4)%></p></div>
										<%-- <span class="desg_tree"><%="1"%></span> --%>
								<%
									if(iinnerList.get(29)!=null){
									List<String> emplistID=Arrays.asList(iinnerList.get(29).split(","));
								%>
								<div style="float:left;width:100%;">
									<% for(int i=0; emplistID!=null && i<emplistID.size();i++){
										if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
											String empName = hmEmpName.get(emplistID.get(i).trim());
											String empimg=uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
										%>
									<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');"><span style="float:left; width:20px; height:20px; margin:2px;"> <!-- border:1px solid #CCCCCC; -->
										<%-- <img height="20" width="20" border="0" data-original="<%=CF.getStrDocRetriveLocation()+empimg %>" src="userImages/avatar_photo.png" class="lazy"> --%>
										<%if(CF.getStrDocRetriveLocation()==null) { %>
												<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empimg%>" />
										  	<%} else { %>
								                <img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
								            <%} %>
									</span></a>
									<% }  } %>
								</div>
								<%} %> 
							</li>
							<%} %>
									</ul>
								</li>
								<%} %>
							</ul>

						</li>
						<%} %>
					</ul>
				</li>
			<% } %>
			<!-- </ul></li> -->
	</ul>

	<script>
		jQuery(document).ready(function() {

			/* Custom jQuery for the example */
			$("#show-list").click(function(e) {
				e.preventDefault();

				$('#list-html').toggle('fast', function() {
					if ($(this).is(':visible')) {
						$('#show-list').text('Hide underlying list.');
						$(".topbar").fadeTo('fast', 0.9);
					} else {
						$('#show-list').text('Show underlying list.');
						$(".topbar").fadeTo('fast', 1);
					}
				});
			});

			$('#list-html').text($('#org').html());
			$("#org").bind("DOMSubtreeModified", function() {
				$('#list-html').text('');

				$('#list-html').text($('#org').html());

				prettyPrint();
			});
		});
	</script>

	<div id="chart" class="orgChart" style="float: left; width: 100%; text-align: center;"></div>
	</div> 

<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>
