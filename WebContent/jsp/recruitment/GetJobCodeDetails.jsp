
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>


<% 
UtilityFunctions uF = new UtilityFunctions();
List<List<String>> jobProfileLists = (List<List<String>>) request.getAttribute("jobProfileLists");
Map<String, String> hmRecruitIdStar = (Map<String, String>)request.getAttribute("hmRecruitIdStar");

if(jobProfileLists != null && !jobProfileLists.isEmpty()) {
	for(int i=0; i<jobProfileLists.size(); i++) {
		List<String> jobProfileList = jobProfileLists.get(i);
%>

<div style="width: 100%; float: left; margin-top: 15px;">
	<div style="width: 50%; float: left;"> 
	
		<table border="0" class="table table-bordered">
			<tr>
				<td class="txtlabel alignRight"><b>Job Code<br/>(Designation):</b></td>
				<td><b><%=jobProfileList.get(6)%></b> <br/>(<%=jobProfileList.get(1)%>)</td>
			</tr>
	
			<tr>
				<td class="txtlabel alignRight"><b>Organisation:</b></td>
				<td><%=jobProfileList.get(17)%></td>
			</tr>
	
			<tr>
				<td class="txtlabel alignRight"><b>Work Location:</b></td>
				<td><%=jobProfileList.get(3)%></td>
			</tr>
			 
			<tr>
				<td class="txtlabel alignRight"><b>Level:</b></td>
				<td><%=jobProfileList.get(16)%></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><b>Designation:</b></td>
				<td><%=jobProfileList.get(1)%></td>
			</tr>
	
			<tr>
				<td class="txtlabel alignRight"><b> Grade:</b></td>
				<td><%=jobProfileList.get(2)%></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><b>Priority:</b></td>
				<td><%=jobProfileList.get(20)%></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><b>Skills:</b></td>
				<td><%=jobProfileList.get(18)%></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><b>Ideal Candidate:</b></td>
				<td><%=jobProfileList.get(19)%></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><b>No. of Position(s): </b></td>
				<td><%=jobProfileList.get(4)%></td>
			</tr>
	
			<tr>
				<td class="txtlabel alignRight"><b>Min. Experience:</b></td>
				<td>
				<%if(uF.parseToInt(jobProfileList.get(8)) == 0 && uF.parseToInt(jobProfileList.get(9)) == 0){ %>
					No Experience Required.
				<%}else{ %>
					<%=uF.parseToInt(jobProfileList.get(8))%>Years and <%=uF.parseToInt(jobProfileList.get(9))%>Months
				<%} %>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight"><b>Max. Experience:</b></td>
				<td>
				<%if(uF.parseToInt(jobProfileList.get(10)) == 0 && uF.parseToInt(jobProfileList.get(11)) == 0){ %>
					No Experience Required.
				<%}else{ %>
					<%=uF.parseToInt(jobProfileList.get(10))%>Years and <%=uF.parseToInt(jobProfileList.get(11))%>Months
				<%} %>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><b>Education:</b></td>
				<td><%=uF.showData(jobProfileList.get(12), "NO EDUCATION SPECIFIED")%>
				</td>
			</tr>
	
			<tr>
				<td class="txtlabel" valign="top" colspan="2">Job
					Description:</td>
			</tr>
	
			<tr>
				<td colspan="2"><%= uF.showData(jobProfileList.get(7),"Not Added Yet")%></td>
			</tr>
	
			<tr>
				<td class="txtlabel" valign="top" colspan="2">Candidate
					Profile:</td>
			</tr>
	
			<tr>
				<td colspan="2"><%= uF.showData(jobProfileList.get(13),"Not Added Yet")%></td>
			</tr>
	
		</table>
	</div>
	
	<div style="width: 40%; float: left;">
		<table border="0" class="display tb_style" >
		<script type="text/javascript">
	           	  $(function() {
					$('#starPrimary0').raty({
	                    readOnly: true,
	                    start:	<%=hmRecruitIdStar.get(jobProfileList.get(0)) %> ,
	                    half: true,
	                    targetType: 'number'
					 });
	                });
	          </script>
			<tr>
				<td class="txtlabel alignRight"><b>Rating</b></td>
				<td>
				<div id="starPrimary0" style="width: 100%;"></div>
				 
				<%
					double dblRate = uF.parseToDouble(hmRecruitIdStar.get(jobProfileList.get(0)));
					if (dblRate >= 3.5) {
				%>
					<div style="background-color: #00FF00; padding: 3px; border-radius: 4px 4px 4px 4px; float: left; font-size: 18px; font-weight: bold; font-family: digital;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5</div>
				<%
					} else if (dblRate < 3.5 && dblRate >= 2.5) {
				%>
					<div style="background-color: #FFFF00; padding: 3px; border-radius: 4px 4px 4px 4px; float: left; font-size: 18px; font-weight: bold; font-family: digital;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5</div>
				<%
					} else if (dblRate > 0) {  //#FF0000; 
				%>
					<div style="background-color: #FF0000; padding: 3px; border-radius: 4px 4px 4px 4px; float: left; font-size: 18px; font-weight: bold; font-family: digital;"><%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5</div>
				<%
					}
				%>
				<%-- <%=request.getAttribute("strStars").toString() %>/5 --%>
				</td>
			</tr>
		</table>
	</div>
</div>

<% } } %>