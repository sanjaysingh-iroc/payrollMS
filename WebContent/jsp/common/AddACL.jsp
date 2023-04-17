<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
 



<%-- <style>
.leftFix {
            position:absolute; 
            width:20em; 
            left:0;
            top:auto;
            padding-left: 10px;
        }
</style> --%>
<%String strTitle = "Access Control Level"; %>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
</jsp:include> --%>
 

<div class="reportWidth">    
 
	<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, List<String>> hmParentLblUserId = (Map<String, List<String>>) request.getAttribute("hmParentLblUserId");
	Map<String, String> hmParentLblAndUserwiseNaviId = (Map<String, String>) request.getAttribute("hmParentLblAndUserwiseNaviId");
	
	Map<String, Map<String, List<String>>> hmParentwiseLblUserId = (Map<String, Map<String, List<String>>>) request.getAttribute("hmParentwiseLblUserId");
	Map<String, String> hmChildLblAndUserwiseNaviId = (Map<String, String>) request.getAttribute("hmChildLblAndUserwiseNaviId");
	
	Map<String, Map<String, List<String>>> hmParentwiseLblUserId1 = (Map<String, Map<String, List<String>>>) request.getAttribute("hmParentwiseLblUserId1");
	Map<String, String> hmChildLblAndUserwiseNaviId1 = (Map<String, String>) request.getAttribute("hmChildLblAndUserwiseNaviId1");
	
	//Map hmVisibilityNavigationMap = (Map)request.getAttribute("hmVisibilityNavigationMap");
	//Map hmAclNavigationMap = (Map)request.getAttribute("hmAclNavigationMap");
	//Map hmNavigationAccessMap = (Map)request.getAttribute("hmNavigationAccessMap");
	Map hmUserTypeMap = (Map)request.getAttribute("hmUserTypeMap");
	List alAcl = (List)request.getAttribute("alAcl");
	
	%>


	<div class="attendance" style="overflow-y:scroll;width: 100%;max-height: 600px;">
		<table align="left" class="table-bordered table-striped">
			<tr class="darktable">
				<td align="center" class="leftFix"> Navigation</td>
				<%
				for(int i=0; i<alAcl.size(); i++){
				%>
				<td align="center" colspan="4"> <%=(String)hmUserTypeMap.get((String)alAcl.get(i)) %></td>
				<%} %>
			</tr>
		
			<tr class="darktable">
				<td align="center" class="leftFix"> &nbsp;</td>
				<%
				for(int i=0; i<alAcl.size(); i++){
				%>
				<td align="center">C</td>
				<td align="center">R</td>
				<td align="center">U</td>
				<td align="center">D</td>
				<%} %>
			</tr>


		<%
			Set<String> setParent = hmParentLblUserId.keySet();
			Iterator<String> itParent = setParent.iterator();
			int count = 0;
			while(itParent.hasNext()) {
				String strLabelParent = (String)itParent.next();
				List<String> alParentLblUserId = hmParentLblUserId.get(strLabelParent);
				
				Map<String, List<String>> hmChildLblUserId = hmParentwiseLblUserId.get(strLabelParent);
				
				Map<String, List<String>> hmChildLblUserId1 = hmParentwiseLblUserId1.get(strLabelParent);
				count++;
				%>
			<tr>
		
				<td style="font-weight: bold;"  class="leftFix"><%=strLabelParent%></td>
				<%
					for(int i=0; i<alAcl.size(); i++){
						String strUserid = (String)alAcl.get(i);
						String strNavId = hmParentLblAndUserwiseNaviId.get(strUserid+"_"+strLabelParent);
						
						if(alParentLblUserId.contains(strUserid)) {
						%>
							<%-- <td align="center"><div id="myDiv_A_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
							<td align="center"><div id="myDiv_V_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
							<td align="center"><div id="myDiv_U_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
							<td align="center"><div id="myDiv_D_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td> --%>
							
							<td align="center"><div id="myDiv_A_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
							<td align="center"><div id="myDiv_V_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
							<td align="center"><div id="myDiv_U_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
							<td align="center"><div id="myDiv_D_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
							
							
						<% } else { %>
							<td align="center" colspan="4"><img src="images1/na.png"/></td>
						<%} %>	
				<%} %>
			</tr>
			
				<%
				if(hmChildLblUserId != null && !hmChildLblUserId.isEmpty()) {
					Set<String> set = hmChildLblUserId.keySet();
					Iterator<String> it = set.iterator();
					while(it.hasNext()) {
						String strLabel = (String)it.next();
						List<String> alChildLblUserId = hmChildLblUserId.get(strLabel);
						
						Map<String, List<String>> hmChildLblUserIdCHLD = hmParentwiseLblUserId1.get(strLabelParent);
						
						count++;
						%>
					<tr>
						<td style="padding-left:20px;"  class="leftFix"><%=strLabel%></td>
						<%
							for(int i=0; i<alAcl.size(); i++){
								String strUserid = (String)alAcl.get(i);
								String strNavId = hmChildLblAndUserwiseNaviId.get(strUserid+"_"+strLabel);
								
								if(alChildLblUserId.contains(strUserid)) {
								%>
									<%-- <td align="center"><div id="myDiv_A_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
									<td align="center"><div id="myDiv_V_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
									<td align="center"><div id="myDiv_U_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
									<td align="center"><div id="myDiv_D_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td> --%>
									
									<td align="center"><div id="myDiv_A_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
									<td align="center"><div id="myDiv_V_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
									<td align="center"><div id="myDiv_U_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
									<td align="center"><div id="myDiv_D_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
									
								<% } else { %>
									<td align="center" colspan="4"><img src="images1/na.png"/></td>
								<%} %>	
						<%} %>
					</tr>
					
					<%
					if(hmChildLblUserIdCHLD != null && !hmChildLblUserIdCHLD.isEmpty()) {
						Set<String> setCHLD = hmChildLblUserIdCHLD.keySet();
						Iterator<String> itCHLD = setCHLD.iterator();
						while(itCHLD.hasNext()) {
							String strLabelCHLD = (String)itCHLD.next();
							List<String> alChildLblUserId1 = hmChildLblUserIdCHLD.get(strLabelCHLD);
							
							count++;
							%>
						<tr>
							<td style="padding-left:20px;" class="leftFix" ><%=strLabelCHLD%></td>
							<%
								for(int i=0; i<alAcl.size(); i++){
									String strUserid = (String)alAcl.get(i);
									String strNavId = hmChildLblAndUserwiseNaviId1.get(strUserid+"_"+strLabelCHLD);
									
									if(alChildLblUserId1.contains(strUserid)) {
									%>
										<%-- <td align="center"><div id="myDiv_A_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
										<td align="center"><div id="myDiv_V_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
										<td align="center"><div id="myDiv_U_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
										<td align="center"><div id="myDiv_D_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td> --%>
										
										<td align="center"><div id="myDiv_A_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
										<td align="center"><div id="myDiv_V_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
										<td align="center"><div id="myDiv_U_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
										<td align="center"><div id="myDiv_D_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
										
										
									<% } else { %>
										<td align="center" colspan="4"><img src="images1/na.png"/></td>
									<%} %>	
							<%} %>
						</tr>
				<% } } %>
				
			<% } } %>
			
			
			<%
				if(hmChildLblUserId1 != null && !hmChildLblUserId1.isEmpty()) {
					Set<String> set = hmChildLblUserId1.keySet();
					Iterator<String> it = set.iterator();
					while(it.hasNext()) {
						String strLabel = (String)it.next();
						List<String> alChildLblUserId1 = hmChildLblUserId1.get(strLabel);
						
						count++;
						%>
					<tr>
						<td style="padding-left:20px;" class="leftFix" ><%=strLabel%></td>
						<%
							for(int i=0; i<alAcl.size(); i++){
								String strUserid = (String)alAcl.get(i);
								String strNavId = hmChildLblAndUserwiseNaviId1.get(strUserid+"_"+strLabel);
								
								if(alChildLblUserId1.contains(strUserid)) {
								%>
									<%-- <td align="center"><div id="myDiv_A_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
									<td align="center"><div id="myDiv_V_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
									<td align="center"><div id="myDiv_U_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td>
									<td align="center"><div id="myDiv_D_<%=count%>_<%=strUserid%>"><img src="images1/tick.png" title="Enable"/></div></td> --%>
									
									<td align="center"><div id="myDiv_A_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
									<td align="center"><div id="myDiv_V_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
									<td align="center"><div id="myDiv_U_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
									<td align="center"><div id="myDiv_D_<%=count%>_<%=strUserid%>"><i class="fa fa-check checknew" aria-hidden="true" title="Enable"></i></div></td>
									
									
								<% } else { %>
									<td align="center" colspan="4"><img src="images1/na.png"/></td>
								<%} %>	
						<%} %>
					</tr>
			<% } } %>
			
		<% } %>
	</table>
</div>

</div>
