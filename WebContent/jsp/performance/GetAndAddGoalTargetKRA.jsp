		<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
		<%@page import="java.util.Map"%>
		<%@page import="java.util.List"%>
		
		<% 
		String systemType = (String)request.getAttribute("systemType");
		String ansType = (String)request.getAttribute("ansType");
		UtilityFunctions uF = new UtilityFunctions();
		//System.out.println("ansType ---> "+ansType);
		//System.out.println("systemType ---> "+systemType);
		List<String> goalIdsList = (List<String>)request.getAttribute("goalIdsList");
		Map<String, String> hmGoalParentID = (Map<String, String>)request.getAttribute("hmGoalParentID");
		int cnt =0;
		%>
		<s:form action="AddGoalTargetKRA" name="frmothersystem" id="frmothersystem" method="POST" theme="simple">
		<s:hidden name="id" id="id"></s:hidden>
		<s:hidden name="sysType"></s:hidden>
		<s:hidden name="ansType"></s:hidden>
		<s:hidden name="subSectionId"></s:hidden>
		<s:hidden name="attributeId"></s:hidden>
		<s:hidden name="appFreqId" id="appFreqId"></s:hidden>
			<ul class="level_list ul_class">
					<li>
					<%if(systemType != null && systemType.equals("goal")) { %>
						
						<%
						for(int i= 0; goalIdsList != null && !goalIdsList.isEmpty() && i < goalIdsList.size(); i++){
							Map<String, List<String>> hmGoalData = (Map<String, List<String>>)request.getAttribute("hmGoalData");
							List<String> innerList = hmGoalData.get(goalIdsList.get(i));
							if(innerList != null && !innerList.isEmpty()){
								String teamGoalID = hmGoalParentID.get(innerList.get(0));
								String managerGoalID = hmGoalParentID.get(teamGoalID);
								String corpGoalID = hmGoalParentID.get(managerGoalID);
								cnt++;
						%>
					
					<table class="table table_no_border sectionfont" width="100%" style="margin: 5px;">
						<tr> <!-- align="right" style="padding-right:20px" -->
							<th align="center" width="25px"><strong><%=cnt %>)</strong></th>
							<th align="center" width="35px">
							<input type="hidden" name="orientt" value="<%=goalIdsList.get(i)%>"/>
							<input type="hidden" name="goalWeightage<%=goalIdsList.get(i) %>" value="<%=innerList.get(5) %>"/>
							<input type="checkbox" name="goalId" id="goalId<%=goalIdsList.get(i)%>" value="<%=goalIdsList.get(i)%>"></th>
							<td colspan="2"> <%=innerList.get(1) %> (<%=innerList.get(6) %>)
							<% if(uF.parseToInt(teamGoalID) > 0) { %>
								<span id="goalChartId" style="float: right; margin-right: 1cm;">
									<a href="javascript:void(0)" onclick="goalChart('<%=corpGoalID %>','<%=managerGoalID %>','<%=teamGoalID %>','<%=innerList.get(0) %>')"  style="float:left; margin-right: 7px; margin-top: 3px;" title="Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>&nbsp;
								</span>
							<% } %>
							</td>
							<!-- <td></td> -->
						</tr> 
						
						<%if(ansType != null && (ansType.equals("1") || ansType.equals("2") || ansType.equals("8"))){ %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>a)&nbsp;<input type="text" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" value="a" name="correct<%=goalIdsList.get(i)%>"/></td>
								<td>b)&nbsp;<input type="text" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>" style="height: 25px;"/><input type="radio" name="correct<%=goalIdsList.get(i)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>c)&nbsp;<input type="text" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="c"/></td>
								<td>d)&nbsp;<input type="text" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="d"/></td>
							</tr>
					
						<%}else if (ansType != null && ansType.equals("9")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>a)&nbsp;<span id="aspan"><input type="text" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>" style="height: 25px;"/></span> <input type="checkbox" value="a" name="correct<%=goalIdsList.get(i)%>"/> </td>
								<td>b)&nbsp;<span id="bspan"><input type="text" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=goalIdsList.get(i)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>c)&nbsp;<span id="cspan"><input type="text" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=goalIdsList.get(i)%>" value="c"/></td>
								<td>d)&nbsp;<span id="dspan"><input type="text" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=goalIdsList.get(i)%>" value="d"/></td>
							</tr>
				
						<% }else if (ansType != null && ansType.equals("6")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td><input type="hidden" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>"/><input type="hidden" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>"/><input type="hidden" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>"/><input type="hidden" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>"/>
								<input type="radio" name="correct<%=goalIdsList.get(i)%>" checked="checked" value="1">True&nbsp;<input type="radio" name="correct<%=goalIdsList.get(i)%>" value="0">False</td>
								</tr>
						
						<%}else if (ansType != null && ansType.equals("5")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td><input type="hidden" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>"/><input type="hidden" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>"/><input type="hidden" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>"/><input type="hidden" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>"/>
								<input type="radio" name="correct<%=goalIdsList.get(i)%>" checked="checked" value="1">Yes&nbsp;<input type="radio" name="correct<%=goalIdsList.get(i)%>" value="0">No</td>
							</tr>
						<%} else if (ansType != null && ansType.equals("13")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>a)&nbsp;<input type="text" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" value="a" name="correct<%=goalIdsList.get(i)%>"/></td>
								<td>b)&nbsp;<input type="text" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>" style="height: 25px;"/><input type="radio" name="correct<%=goalIdsList.get(i)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>c)&nbsp;<input type="text" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="c"/></td>
								<td>d)&nbsp;<input type="text" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="d"/></td>
							</tr>
							<tr id="answerType2<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>e)&nbsp;<input type="text" name="optione<%=goalIdsList.get(i)%>" id="optione<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="e"/></td>
								<td>&nbsp;</td>
							</tr>
						<% } %>
					</table>
					
						<% 
						}  
						}
						%>
						<%if(cnt == 0){ %>
							<div style="width: 100%; font-weight: bold;"> Please set some goals for these reviewee in the Goals option </div>
						<%} else{ %>	
					<div align="center">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeGoalTargetDiv('<%=request.getAttribute("divCount") %>');"/>
					</div>
					<%} %>
					
					<%} %>
				
				<%if(systemType != null && systemType.equals("target")) { %>
						
						<%
						for(int i= 0; goalIdsList != null && !goalIdsList.isEmpty() && i < goalIdsList.size(); i++){
							Map<String, List<String>> hmTargetData = (Map<String, List<String>>)request.getAttribute("hmTargetData");
							List<String> innerList = hmTargetData.get(goalIdsList.get(i));
							if(innerList != null && !innerList.isEmpty()){
								String teamGoalID = hmGoalParentID.get(innerList.get(0));
								String managerGoalID = hmGoalParentID.get(teamGoalID);
								String corpGoalID = hmGoalParentID.get(managerGoalID);
								cnt++;
						%>
					<table class="table table_no_border sectionfont" width="100%" style="margin: 5px;">
						<tr> <!-- align="right" style="padding-right:20px" -->
							<th align="center" width="25px"><strong><%=cnt %>)</strong></th>
							<th align="center" width="35px">
							<input type="hidden" name="orientt" value="<%=goalIdsList.get(i)%>"/>
							<input type="hidden" name="goalWeightage<%=goalIdsList.get(i) %>" value="<%=innerList.get(5) %>"/>
							<input type="checkbox" name="goalId" id="goalId<%=goalIdsList.get(i)%>" value="<%=goalIdsList.get(i)%>"></th>
							<td colspan="2"> <%=innerList.get(1) %> (<%=innerList.get(6) %>)
							<% if(uF.parseToInt(teamGoalID) > 0) { %>
								<span id="goalChartId" style="float: right; margin-right: 1cm;">
									<a href="javascript:void(0)" onclick="goalChart('<%=corpGoalID %>','<%=managerGoalID %>','<%=teamGoalID %>','<%=innerList.get(0) %>')"  style="float:left; margin-right: 7px; margin-top: 3px;" title="Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>&nbsp;
								</span>
							<% } %>
							</td>
							<!-- <td></td> -->
						</tr>
						
						<%if(ansType != null && (ansType.equals("1") || ansType.equals("2") || ansType.equals("8"))){ %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>a)&nbsp;<input type="text" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" value="a" name="correct<%=goalIdsList.get(i)%>"/></td>
								<td>b)&nbsp;<input type="text" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>" style="height: 25px;"/><input type="radio" name="correct<%=goalIdsList.get(i)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>c)&nbsp;<input type="text" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="c"/></td>
								<td>d)&nbsp;<input type="text" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="d"/></td>
							</tr>
					
						<%}else if (ansType != null && ansType.equals("9")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>a)&nbsp;<span id="aspan"><input type="text" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>" style="height: 25px;"/></span> <input type="checkbox" value="a" name="correct<%=goalIdsList.get(i)%>"/> </td>
								<td>b)&nbsp;<span id="bspan"><input type="text" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=goalIdsList.get(i)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>c)&nbsp;<span id="cspan"><input type="text" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=goalIdsList.get(i)%>" value="c"/></td>
								<td>d)&nbsp;<span id="dspan"><input type="text" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=goalIdsList.get(i)%>" value="d"/></td>
							</tr>
				
						<% }else if (ansType != null && ansType.equals("6")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td><input type="hidden" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>"/><input type="hidden" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>"/><input type="hidden" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>"/><input type="hidden" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>"/>
								<input type="radio" name="correct<%=goalIdsList.get(i)%>" checked="checked" value="1">True&nbsp;<input type="radio" name="correct<%=goalIdsList.get(i)%>" value="0">False</td>
								</tr>
						
						<%}else if (ansType != null && ansType.equals("5")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td><input type="hidden" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>"/><input type="hidden" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>"/><input type="hidden" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>"/><input type="hidden" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>"/>
								<input type="radio" name="correct<%=goalIdsList.get(i)%>" checked="checked" value="1">Yes&nbsp;<input type="radio" name="correct<%=goalIdsList.get(i)%>" value="0">No</td>
							</tr>
						<%} else if (ansType != null && ansType.equals("13")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>a)&nbsp;<input type="text" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" value="a" name="correct<%=goalIdsList.get(i)%>"/></td>
								<td>b)&nbsp;<input type="text" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>" style="height: 25px;"/><input type="radio" name="correct<%=goalIdsList.get(i)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>c)&nbsp;<input type="text" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="c"/></td>
								<td>d)&nbsp;<input type="text" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="d"/></td>
							</tr>
							<tr id="answerType2<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>e)&nbsp;<input type="text" name="optione<%=goalIdsList.get(i)%>" id="optione<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="e"/></td>
								<td>&nbsp;</td>
							</tr>
						<%} %>
					
					</table>
					
					<%
						}
						}
					%>	
					<%if(cnt == 0){ %>
					<div style="width: 100%; font-weight: bold;"> Please set some targets for these reviewee in the Goals option </div>
					<%} else{ %>
					<div align="center">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeGoalTargetDiv('<%=request.getAttribute("divCount") %>');"/>
					</div>
					<%} %>
				<% } %>
				
				<%if(systemType != null && systemType.equals("KRA")){ %>
						
						<%
						for(int i= 0; goalIdsList != null && !goalIdsList.isEmpty() && i < goalIdsList.size(); i++){
							Map<String, List<String>> hmKraIDs = (Map<String, List<String>>)request.getAttribute("hmKraIDs");
							List<String> kraIDList = hmKraIDs.get(goalIdsList.get(i));
							for(int j=0; kraIDList != null && j< kraIDList.size(); j++){
							Map<String, List<String>> hmKRAData = (Map<String, List<String>>)request.getAttribute("hmKRAData");
							List<String> innerList = hmKRAData.get(kraIDList.get(j));
							if(innerList != null && !innerList.isEmpty()){
								String teamGoalID = hmGoalParentID.get(innerList.get(0));
								String managerGoalID = hmGoalParentID.get(teamGoalID);
								String corpGoalID = hmGoalParentID.get(managerGoalID);
								cnt++;
						%>
					<table class="table table_no_border sectionfont" width="100%" style="margin: 5px;">
						<tr> <!-- align="right" style="padding-right:20px" -->
							<th align="center" width="25px"><strong><%=cnt %>)</strong></th>
							<th align="center" width="35px">
							<input type="hidden" name="orientt" value="<%=kraIDList.get(j)%>"/>
							<input type="hidden" name="goalWeightage<%=kraIDList.get(j) %>" value="<%=innerList.get(5) %>"/>
							<input type="hidden" name="goalID<%=kraIDList.get(j) %>" value="<%=innerList.get(0) %>"/>
							<input type="checkbox" name="goalId" id="goalId<%=kraIDList.get(j)%>" value="<%=kraIDList.get(j)%>"></th>
							<td colspan="2"> <%=innerList.get(1) %> (<%=innerList.get(6) %>)
							<% if(uF.parseToInt(teamGoalID) > 0) { %>
								<span id="goalChartId" style="float: right; margin-right: 1cm;">
									<a href="javascript:void(0)" onclick="goalChart('<%=corpGoalID %>','<%=managerGoalID %>','<%=teamGoalID %>','<%=innerList.get(0) %>')"  style="float:left; margin-right: 7px; margin-top: 3px;" title="Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>&nbsp;
								</span>
							<% } %>
							</td>
							<!-- <td></td> -->
						</tr>
						
						<%if(ansType != null && (ansType.equals("1") || ansType.equals("2") || ansType.equals("8"))){ %>
							<tr id="answerType<%=kraIDList.get(j)%>"><th></th><th></th>
								<td>a)&nbsp;<input type="text" name="optiona<%=kraIDList.get(j)%>" id="optiona<%=kraIDList.get(j)%>" style="height: 25px;"/> <input type="radio" value="a" name="correct<%=kraIDList.get(j)%>"/></td>
								<td>b)&nbsp;<input type="text" name="optionb<%=kraIDList.get(j)%>" id="optionb<%=kraIDList.get(j)%>" style="height: 25px;"/><input type="radio" name="correct<%=kraIDList.get(j)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=kraIDList.get(j)%>"><th></th><th></th>
								<td>c)&nbsp;<input type="text" name="optionc<%=kraIDList.get(j)%>" id="optionc<%=kraIDList.get(j)%>" style="height: 25px;"/> <input type="radio" name="correct<%=kraIDList.get(j)%>" value="c"/></td>
								<td>d)&nbsp;<input type="text" name="optiond<%=kraIDList.get(j)%>" id="optiond<%=kraIDList.get(j)%>" style="height: 25px;"/> <input type="radio" name="correct<%=kraIDList.get(j)%>" value="d"/></td>
							</tr>
					
						<%}else if (ansType != null && ansType.equals("9")) { %>
							<tr id="answerType<%=kraIDList.get(j)%>"><th></th><th></th>
								<td>a)&nbsp;<span id="aspan"><input type="text" name="optiona<%=kraIDList.get(j)%>" id="optiona<%=kraIDList.get(j)%>" style="height: 25px;"/></span> <input type="checkbox" value="a" name="correct<%=kraIDList.get(j)%>"/> </td>
								<td>b)&nbsp;<span id="bspan"><input type="text" name="optionb<%=kraIDList.get(j)%>" id="optionb<%=kraIDList.get(j)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=kraIDList.get(j)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=kraIDList.get(j)%>"><th></th><th></th>
								<td>c)&nbsp;<span id="cspan"><input type="text" name="optionc<%=kraIDList.get(j)%>" id="optionc<%=kraIDList.get(j)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=kraIDList.get(j)%>" value="c"/></td>
								<td>d)&nbsp;<span id="dspan"><input type="text" name="optiond<%=kraIDList.get(j)%>" id="optiond<%=kraIDList.get(j)%>" style="height: 25px;"/></span> <input type="checkbox" name="correct<%=kraIDList.get(j)%>" value="d"/></td>
							</tr>
				
						<% }else if (ansType != null && ansType.equals("6")) { %>
							<tr id="answerType<%=kraIDList.get(j)%>"><th></th><th></th>
								<td><input type="hidden" name="optiona<%=kraIDList.get(j)%>" id="optiona<%=kraIDList.get(j)%>"/><input type="hidden" name="optionb<%=kraIDList.get(j)%>" id="optionb<%=kraIDList.get(j)%>"/><input type="hidden" name="optionc<%=kraIDList.get(j)%>" id="optionc<%=kraIDList.get(j)%>"/><input type="hidden" name="optiond<%=kraIDList.get(j)%>" id="optiond<%=kraIDList.get(j)%>"/>
								<input type="radio" name="correct<%=kraIDList.get(j)%>" checked="checked" value="1">True&nbsp;<input type="radio" name="correct<%=kraIDList.get(j)%>" value="0">False</td>
								</tr>
						
						<%}else if (ansType != null && ansType.equals("5")) { %>
							<tr id="answerType<%=kraIDList.get(j)%>"><th></th><th></th>
								<td><input type="hidden" name="optiona<%=kraIDList.get(j)%>" id="optiona<%=kraIDList.get(j)%>"/><input type="hidden" name="optionb<%=kraIDList.get(j)%>" id="optionb<%=kraIDList.get(j)%>"/><input type="hidden" name="optionc<%=kraIDList.get(j)%>" id="optionc<%=kraIDList.get(j)%>"/><input type="hidden" name="optiond<%=kraIDList.get(j)%>" id="optiond<%=kraIDList.get(j)%>"/>
								<input type="radio" name="correct<%=kraIDList.get(j)%>" checked="checked" value="1">Yes&nbsp;<input type="radio" name="correct<%=kraIDList.get(j)%>" value="0">No</td>
							</tr>
						<%} else if (ansType != null && ansType.equals("13")) { %>
							<tr id="answerType<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>a)&nbsp;<input type="text" name="optiona<%=goalIdsList.get(i)%>" id="optiona<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" value="a" name="correct<%=goalIdsList.get(i)%>"/></td>
								<td>b)&nbsp;<input type="text" name="optionb<%=goalIdsList.get(i)%>" id="optionb<%=goalIdsList.get(i)%>" style="height: 25px;"/><input type="radio" name="correct<%=goalIdsList.get(i)%>" value="b"/></td>
							</tr>
							<tr id="answerType1<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>c)&nbsp;<input type="text" name="optionc<%=goalIdsList.get(i)%>" id="optionc<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="c"/></td>
								<td>d)&nbsp;<input type="text" name="optiond<%=goalIdsList.get(i)%>" id="optiond<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="d"/></td>
							</tr>
							<tr id="answerType2<%=goalIdsList.get(i)%>"><th></th><th></th>
								<td>e)&nbsp;<input type="text" name="optione<%=goalIdsList.get(i)%>" id="optione<%=goalIdsList.get(i)%>" style="height: 25px;"/> <input type="radio" name="correct<%=goalIdsList.get(i)%>" value="e"/></td>
								<td>&nbsp;</td>
							</tr>
						<%} %>
					
					</table>
						
					<%
						}
						}
						}
					%>	
					<%if(cnt == 0){ %>
					<div style="width: 100%; font-weight: bold;"> Please set some KRAs for these reviewee in the Goals option </div>
					<%} else{ %>
					<div align="center">
						<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeGoalTargetDiv('<%=request.getAttribute("divCount") %>');"/>
					</div>
				<% } %>
				<% } %>
					</li>
				</ul>
			
			<%-- <div align="center">
				<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
				<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeGoalTargetDiv('<%=request.getAttribute("divCount") %>');"/>
			</div> --%>
						
</s:form>

<script type="text/javascript">

$("#frmothersystem").submit(function(e){
	e.preventDefault();
	var appFreqId = document.getElementById("appFreqId").value;
	var id = document.getElementById("id").value;
	var form_data = $("#frmothersystem").serialize();
	$("#reviewInfo").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: "AddGoalTargetKRA.action",
		data: form_data+"&submit=Save",
		success: function(result){
			$("#reviewInfo").html(result);
   		},
		error: function(result){
			$.ajax({
				url: 'AppraisalSummary.action?id='+id+'&appFreqId'+appFreqId,
				cache: true,
				success: function(result){
					$("#reviewInfo").html(result);
				}
			});
		}
	});
});

</script>