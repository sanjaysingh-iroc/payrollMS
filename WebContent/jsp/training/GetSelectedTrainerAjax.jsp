
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%
Map<String, String> hmtrainer = (Map<String, String>)request.getAttribute("hmtrainer");
Map<String, String> hmSelectedTrainer = (Map<String, String>)request.getAttribute("hmSelectedTrainer");
String type = (String)request.getAttribute("type"); 
%>

<%
 if(type!=null && type.equals("location")){
	if(!hmtrainer.isEmpty()){
%>
			<div style="width: 100%; border: 2px solid rgb(204, 204, 204);">
			
			<table class="tb_style" width="100%">
				<tr>
					<th width="10%"></th>
					<th align="center">Trainer</th>
					<th align="center">Factsheet</th>
				</tr>
				<%
				List<List<String>> trainerOuterList = (List<List<String>>) request.getAttribute("trainerOuterList");
					for (int i = 0; trainerOuterList != null && i < trainerOuterList.size(); i++) {
									List<String> innerList = trainerOuterList.get(i);
									String trainerID = innerList.get(0);
									String trainerEmpID = innerList.get(1);
									String trainerTYPE = innerList.get(2);
									String trainerName = innerList.get(3);
				%>
				<tr>
					<td>
					<input type="checkbox" name="strTrainerId" id="strTrainerId<%=trainerID%>" onclick="getSelectedTrainer(this.checked,this.value);"
						value="<%=trainerID%>" <%if (hmSelectedTrainer.get(trainerID) != null) {%> checked="checked" <%}%>>
					<%-- <input onclick="getContent('idTrainerInfo', 'GetSelectedTrainerAjax.action?chboxStatus='+this.checked+'&trainerId='+this.value+'&type=select&planId=<s:property value="planId"/>')"
						type="checkbox" name="strTrainerId" id="strTrainerId<%=i%>" value="<%=trainerID%>" <%if (hmSelectedTrainer.get(trainerID) != null) {%> checked="checked" <%}%>> --%>
						
					</td>
					<td><a href="javascript:void(0)"
						onclick="showTrainerCalender(<%=trainerID%>);"><%=trainerName%></a>
					</td>
					<td>
					<%if(trainerTYPE != null && trainerTYPE.equals("EXTrainer")){ %>
					<a class="factsheet" href="javascript: void(0)" onclick="openTrainerProfilePopup('<%=trainerEmpID %>');"></a>
					<%} else { %>
					<a class="factsheet" href="javascript: void(0)" onclick="openPanelEmpProfilePopup('<%=trainerEmpID %>');"></a>
					<% } %>
					<%-- <a class="factsheet" href="TrainerMyProfile.action?empId=<%=trainerID%>"></a> --%>
					</td>

				</tr>
				<%
					}
				%>
			</table> 
				
		</div>
<%}
else{%>
<div class="nodata msg" style="width:85%"><span>No Trainers Available</span></div>
<% }
 }else{ %>

<%=request.getAttribute("allData").toString()%>
<% } %>