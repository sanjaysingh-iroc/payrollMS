<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<% 
	UtilityFunctions uF = new UtilityFunctions();
	String managerName = (String) request.getAttribute("managerName");
	String managerRating = (String) request.getAttribute("managerRating"); 
	String managerComment = (String) request.getAttribute("managerComment"); 
	String hrName = (String) request.getAttribute("hrName");
	String hrRating = (String) request.getAttribute("hrRating");
	String hrComment = (String) request.getAttribute("hrComment");
	
	Map<String, List<List<String>>> hmUserComments = (Map<String, List<List<String>>>) request.getAttribute("hmUserComments");
	
%>
	
	<% if(managerComment != null && !managerComment.equals("null") && managerRating != null && !managerRating.equals("null")) { %>
		<script type="text/javascript">
		$(function() {
			$('#starPrimaryManager').raty({
			readOnly: true,
			start: <%=managerRating %>,
			half: true,
			targetType: 'number'
			});
		});
		</script>
		
		<b><u>Manager Rating & Comment:</u></b><br/>
		<div style="float: left;"><b><%=managerName %>:</b></div> <div id="starPrimaryManager" style="float: left; margin: -3px 0px 0px 0px;"></div>
		<br/><i>Comment:</i> <%=managerComment %>
		<br/><br/>
	<% } %>
	
	<% if(hrComment != null && !hrComment.equals("null") && hrRating != null && !hrRating.equals("null")) { %>
		<script type="text/javascript">
		$(function() {
			$('#starPrimaryHR').raty({
			readOnly: true,
			start: <%=hrRating %>,
			half: true,
			targetType: 'number'
			});
		});
		</script>
		
		<b><u>HR Rating & Comment:</u></b><br/> 
		<div style="float: left;"><b><%=hrName %>:</b></div> <div id="starPrimaryHR" style="float: left; margin: -3px 0px 0px 0px;"></div>
		<br/><i>Comment:</i> <%=hrComment %>
		
	<% } %>
	
	
	<div style="margin: 5px 0px;">
		<%  if(hmUserComments != null) { 
			Iterator<String> it = hmUserComments.keySet().iterator();
			while(it.hasNext()) {
				String userType = it.next();
			%>
			<div style="float: left; width: 100%; margin: 3px 0px 0px; font-size: 14px;"><b><u><%=userType %> Ratings &amp; Comments:</u></b></div>
			<%
				List<List<String>> alUserComments = hmUserComments.get(userType);
				for(int i=0; alUserComments != null && !alUserComments.isEmpty() && i<alUserComments.size(); i++) {
					List<String> innerList = alUserComments.get(i);
					if(innerList != null) {
			%>
				<div style="float: left; width: 100%; margin: 3px 0px;">
					<div style="float: left; width: 100%;">
						<script type="text/javascript">
							$(function() {
								$('#starPrimaryUser_<%=innerList.get(3) %>_<%=innerList.get(4) %>').raty({
								readOnly: true,
								start: <%=innerList.get(2) %>,
								half: true,
								targetType: 'number'
								});
							});
						</script>
						<div style="float: left; max-width: 30%;"><b><%=innerList.get(0) %>:</b></div>
						<div style="float: left; max-width: 66%; margin-left: 5px;">
							<div id="starPrimaryUser_<%=innerList.get(3) %>_<%=innerList.get(4) %>" style="float: left; margin: -3px 0px 0px 0px;"></div>
						</div>
					</div>
					<div style="float: left; width: 100%; margin: 3px 0px;">
						<div style="float: left; width: 12%; font-style: italic;">Comment:</div>
						<div style="float: left; max-width: 90%; margin-left: 5px;"><%=innerList.get(1) %></div>
					</div>	
				</div>
					
			<%			
					}
				}
			}
		%>
		
		<% } %>
	</div>