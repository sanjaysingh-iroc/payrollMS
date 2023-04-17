
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
	<div align="center" style="border: 1px solid rgb(204, 204, 204);"><b>Round Information</b></div>
			<%	
				UtilityFunctions uF = new UtilityFunctions();
				Map<String, List<List<String>>> hmEmpIdsRoundwise = (Map<String, List<List<String>>>) request.getAttribute("hmEmpIdsRoundwise");
				Map<String, String> hmRoundName = (Map<String, String>)request.getAttribute("hmRoundName");
				List<String> listRoundId = (List<String>)request.getAttribute("listRoundId");
				Map<String, String> hmRoundAssessment = (Map<String, String>) request.getAttribute("hmRoundAssessment");
				if(hmRoundAssessment == null) hmRoundAssessment = new HashMap<String, String>();
				
				String maxRountId = null;
				if(listRoundId != null){
					maxRountId = listRoundId.get(listRoundId.size()-1);
				}else{
					maxRountId = "1";
				}
			%>
			<input type="hidden" name="totalRound" id="totalRound" value="<%=(listRoundId != null && !listRoundId.isEmpty()) ? listRoundId.size() : "0" %>">
			
			<%
			String recruitId = (String)request.getAttribute("recruitId");
			for (int i = 0; listRoundId!= null && i < listRoundId.size(); i++) {
				List<List<String>> listEmpIds = hmEmpIdsRoundwise.get(listRoundId.get(i));
				String assessmentId = hmRoundAssessment.get(listRoundId.get(i)+"_ASSESSID");
				String assessmentName = hmRoundAssessment.get(listRoundId.get(i)+"_ASSESSNAME");
				String roundRemoveStatus = "yes";
				if(listEmpIds != null) {
					roundRemoveStatus = "no";
				}
			%>
			<div id="row_round<%=listRoundId.get(i) %>" class="row_round" style="border-bottom: 1px solid rgb(235, 235, 235);padding:4px;">
			<table width="100%;">
				<tr>
					<td nowrap="nowrap" style="width: 50%;">
						<input type="hidden" name="roundId" id="roundId" value="<%=listRoundId.get(i) %>">
						<a href="javascript:void(0);" id="roundDIV_<%=listRoundId.get(i) %>" style="float: left; margin-left: 20px; font-weight: bold; width: 100%; color: <%if(i==0) { %> #ff851b; <% } else { %>#535353; <% } %>" onclick="setRoundIdForPanelist('<%=listRoundId.get(i) %>');">
							<span style="float: left;">Round <%=listRoundId.get(i) %></span>
						</a>
						<div id="roundTitDiv<%=listRoundId.get(i) %>" style="float: left;margin-left:20px; font-size: 11px; font-style: oblique;"><%=uF.showData(hmRoundName.get(listRoundId.get(i)), "") %></div>
					</td>
					
					<td colspan="2">
					<span style="float: left;">
						<a href="javascript:void(0)" onclick="setRoundTitle('<%=listRoundId.get(i) %>','<s:property value="recruitId"/>');" title="Set Round Title">
						<i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					</span>
					<a href="javascript:void(0)" onclick="addRound('<%=maxRountId %>','<%=request.getAttribute("recruitId") %>');" title="Add New Round"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
					<%if(i>0){ %>
					<a href="javascript:void(0)" onclick="removeRound(this.id,'<%=request.getAttribute("recruitId") %>','<%=roundRemoveStatus %>')" id="<%=listRoundId.get(i) %>" title="Remove Round"><i class="fa fa-trash" aria-hidden="true"></i></a>
					<%} %>
					</td>
					<td>&nbsp;</td>
					<!-- <a href="javascript:void(0)" onclick="removeRound(this.id)" id="0" class="remove">Remove</a> -->
				</tr>
				
				<tr>
					<td colspan="3">
						<div id="roundAssessDiv_<%=listRoundId.get(i) %>">
							<span>
								<a href="javascript: void(0);" onclick="viewAllAssessment('<%=listRoundId.get(i) %>', '<%=assessmentId %>')">Assessment:</a> &nbsp;
							</span>
							<p id="roundAssessLblSpan_<%=listRoundId.get(i) %>"> <%=uF.showData(assessmentName, "") %> </p>
						</div>
					</td>
				</tr>
				
				<%for (int j = 0; listEmpIds!= null && j < listEmpIds.size(); j++) {
						List<String> innerList = listEmpIds.get(j);
						%>
						<tr>
						<td colspan="3" nowrap="nowrap"> <%-- <%=j+1 %>.&nbsp; --%>
						<input type="hidden" name="empId" id="empId" value="<%=innerList.get(0) %>">
						<span style="float: left;"><%=innerList.get(1) %> 
						<a href="javascript: void(0)" onclick="resetEmployee('<%=innerList.get(0) %>','<%=listRoundId.get(i) %>','<%=request.getAttribute("recruitId") %>');" title="Remove Panel">
						<i class="fa fa-arrow-circle-o-left" aria-hidden="true"></i></a>
						</span>
						<span id="roundchangeImgDiv<%=request.getAttribute("recruitId") %><%=listRoundId.get(i) %><%=innerList.get(0) %>" style="float: left; margin-top: 4px;">
						<a href="javascript: void(0)" onclick="showRounds('<%=request.getAttribute("recruitId") %><%=listRoundId.get(i) %><%=innerList.get(0) %>');" title="Change Panel Round">
						<i class="fa fa-arrows-v" aria-hidden="true"></i></a>
					</span>
					
					<span id="roundsDiv<%=request.getAttribute("recruitId") %><%=listRoundId.get(i) %><%=innerList.get(0) %>" style="display: none; float: left; margin-left: 5px;">
						<select name="strRound" id="strRound<%=request.getAttribute("recruitId") %><%=listRoundId.get(i) %><%=innerList.get(0) %>" style="width:95px !important;" onchange="changePanelRound('<%=innerList.get(0) %>','<%=listRoundId.get(i) %>','<%=recruitId %>',this.value);">
					<%=request.getAttribute("option") %>
					</select>
						
						<%-- <s:select theme="simple" name="strRound" cssStyle="width:95px;" listKey="roundId" listValue="roundName" headerKey="" headerValue="All Round" list="roundList" 
						key="" onchange="changePanelRound('<%=innerList.get(0) %>','<%=listRoundId.get(i) %>','<%=recruitId %>',this.value);"/> --%>
					</span>
						</td>
					</tr>
					<%} %>
				
			</table>
			</div>
			<%} %>

	