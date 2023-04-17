<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

	<%
		UtilityFunctions uF=new UtilityFunctions();
		List<List<String>> daysList = (List<List<String>>)request.getAttribute("daysList");
		Map<String, String> hmdaysDate = (Map<String, String>) request.getAttribute("hmdaysDate");
		String scheduleId = (String) request.getAttribute("scheduleId");
		String trainingName = (String) request.getAttribute("trainingName");
		Map<String, String> hmDayDescription = (Map<String, String>) request.getAttribute("hmDayDescription");
		
		%>

		<div style="float: left; width: 100%">
			<table border="0" class="table">

				<%-- <tr><td class="tdLabelheadingBg alignCenter" colspan="3">
					<span style="color: #68AC3B; font-size: 18px; padding: 5px;"> Step  4: </span>Plan
				</td> </tr> --%>
				<tr>
					<td colspan="4" align="center" height="10px" style="font-size: 14px; font-weight: bold;"><%=trainingName %></td>
				</tr>
				<tr>
				<td class="txtlabel" align="center" style="width:50px;">Days</td>
				<td class="txtlabel" align="center" style="width:80px;">Date</td>
				<td class="txtlabel" align="center">Short Description</td>
				<td class="txtlabel" align="center">Long Description</td>
				</tr>
			<% 
			for(int i=0; daysList != null && !daysList.isEmpty() && i< daysList.size(); i++) {
				%>
				<tr><td class="txtlabel" align="right" valign="top" style="width:50px;">Day <%=i+1 %>:</td>
				<td  class="txtlabel" align="center" valign="top" style="width:80px;">
				<input type="hidden" name="dayDate" id="dayDate" value="<%=daysList.get(i) %>"/>
				<%=hmdaysDate.get(daysList.get(i)) %>
				</td>
				<td valign="top"><%=uF.showData(hmDayDescription.get(scheduleId+"_"+daysList.get(i)+"_S"),"") %>
				<%-- <input type="text" name="daydescription" id="daydescription" value="<%=uF.showData(hmDayDescription.get(scheduleId+"_"+daysList.get(i)+"_S"),"") %>"> --%>
				</td>
				<td valign="top"><%=uF.showData(hmDayDescription.get(scheduleId+"_"+daysList.get(i)+"_L"),"") %>
				<%-- <span style="float: left;"><a href="javascript:void(0);" onclick="showLongDescription('<%=i %>');">LD</a></span>
				<input type="hidden" name="status" id="status<%=i %>" value="0">
				<span id="longDesSpan<%=i %>" style="display: none; float: left; margin-left: 10px;"> <textarea rows="2" cols="75" name="longdescription" id="longdescription"><%=uF.showData(hmDayDescription.get(scheduleId+"_"+daysList.get(i)+"_L"),"") %></textarea></span> --%>
				</td>
				</tr>
				
				<% } %>
			</table>
			
		</div>

