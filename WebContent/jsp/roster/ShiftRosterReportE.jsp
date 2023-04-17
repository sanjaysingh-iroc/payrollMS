<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%!String showData(String str) {
		if (str != null) {
			return str;
		} else {
			return "";
		}
	}%>
  
 
<%
	List alDay = (List) request.getAttribute("alDay");
	List alDate = (List) request.getAttribute("alDate");
	List alEmpId = (List) request.getAttribute("alEmpId");
	Map hmRosterServiceId = (HashMap) request.getAttribute("hmRosterServiceId");
	Map hmRosterServiceName = (HashMap) request.getAttribute("hmRosterServiceName");
	List alServiceId = (List) request.getAttribute("alServiceId");
	List<String> shiftDetails = (List<String>)request.getAttribute("shiftDetails");
	Map hmList = (Map) request.getAttribute("hmList");
	
	Map hmHolidays = (HashMap) request.getAttribute("hmHolidays");
	Map hmHolidayDates = (HashMap) request.getAttribute("hmHolidayDates");
	Map _hmHolidaysColour = (HashMap) request.getAttribute("_hmHolidaysColour");
	Map hmWLocation = (HashMap) request.getAttribute("hmWLocation");
	if(hmWLocation == null) hmWLocation = new HashMap();
	Map hmWeekEnds = (HashMap) request.getAttribute("hmWeekEnds");
	String strWLocation = (String)hmWLocation.get((String)session.getAttribute(IConstants.EMPID));
	
	String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
	if(strAction!=null){
		strAction = strAction.replace(request.getContextPath()+"/","");
	}
	
	
	
//	out.println("<br/>hmRosterServiceName====>"+hmRosterServiceName);
//	out.println("<br/>hmRosterServiceId====>"+hmRosterServiceId);
//	out.println("<br/>alServiceId====>"+alServiceId);
	
%>



<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My Roster Details" name="title"/>
</jsp:include>

<div class="leftbox reportWidth">



<s:form theme="simple" method="post" name="frm_roster_actual">
	
	<div class="filter_div">
	
		<s:select theme="simple" name="strMonth" listKey="monthId" cssStyle="width:101px;"
                         listValue="monthName" headerKey="0"
                         onchange="document.frm_roster_actual.submit();" 		
                         list="monthList" key=""  />
                         
                         	
			<s:select theme="simple" name="strYear" listKey="yearsID" cssStyle="width:65px;"
                         listValue="yearsName" headerKey="0"
                         onchange="document.frm_roster_actual.submit();" 		
                         list="yearList" key=""  />
       </div>            
                   
</s:form>



<div >

<%if(strAction!=null && strAction.equalsIgnoreCase("ShiftRosterReport.action")) {%>
	<a href="RosterReport.action"><img src="images1/ckt_rep.png" /></a>
	<img src="images1/shft_rep_dis.png" />
<%} %>

</div>

	<table cellpadding="1" cellspacing="1" width="95%">
	
	<%
		int lastCount=0;
		if (hmList!=null && hmList.size() != 0) {
	%>

		<tr>
			<td class="">&nbsp;</td>
			<%
				for (int i = 0; alDay!=null && i < ((alDay.size() >= 7) ? 7 : alDay.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
			%>
			<td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter"><%=showData((String) alDay.get(i))%></td>
			<%
				}
			%>
		</tr>

		<tr>
			<td class="">&nbsp;</td>
			<%
				for (int i = 0; alDate!=null && i < ((alDate.size() >= 7) ? 7 : alDate.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
			%>
			<td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter"><%=showData((String) alDate.get(i))%></td>
			<%
				}
			%>
		</tr>


		<%-- <tr>
			<td class="">&nbsp;</td>
			<%
				for (int i = 0; i < ((alDate.size() >= 7) ? 7 : alDate.size()); i++) {
			%>
			<td class="reportHeading alignCenter">IN</td>
			<!-- <td class="reportHeading alignCenter">OUT</td> -->
			<%
				}
			%>

		</tr> --%>

		<%
			int k = 0;
				for (k = 0; alServiceId!=null && alEmpId!=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
					String strCol = ((k % 2 == 0) ? "1" : "");
					Map hm = (Map) hmList.get((String) alEmpId.get(0));
					Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
					String strServiceId = (String) alServiceId.get(k);
					String strServiceName = (String) hmRosterServiceName.get(strServiceId);
		%>

		<tr>
			<%
				for (int i = 0; alDate!=null && i < ((alDate.size() >= 7) ? 7 : alDate.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
							if (i == 0) {
			%>
			<td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
			<%
				}
			%>

			<td style="background-color:<%=strHolidayColour%>" align="center" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "FROM_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "FROM_" + strServiceId) : "-")%></td>
			<%-- <td class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "TO_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "TO_" + strServiceId) : "-")%></td> --%>
			<%
				}
			%>
		</tr>
		<%
			}
		%>

	</table>


	<table cellpadding="1" cellspacing="1" style="margin-top: 50px" width="95%">
		<tr>

			<td class="">&nbsp;</td>
			<%
				for (int i = 7; alDate!=null && i < ((alDate.size() > 15) ? 14 : alDate.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
			%>
			<td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter"><%=showData((String) alDay.get(i))%></td>
			<%
				}
			%>
		</tr>

		<tr>

			<td class="">&nbsp;</td>
			<%
				for (int i = 7; alDate!=null && i < ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : alDate.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
			%>
			<td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter"><%=showData((String) alDate.get(i))%></td>
			<%
				}
			%>
		</tr>


		<%-- <tr>
			<td class="">&nbsp;</td>
			<%
				for (int i = 7; i < ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : alDate.size()); i++) {
			%>
			<td class="reportHeading alignCenter">IN</td>
			<!-- <td class="reportHeading alignCenter">OUT</td> -->
			<%
				}
			%>

		</tr> --%>

		<%
			k = 0;
				for (k = 0; alServiceId!=null && alEmpId!=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
					String strCol = ((k % 2 == 0) ? "1" : "");
					Map hm = (Map) hmList.get((String) alEmpId.get(0));
					Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
					String strServiceId = (String) alServiceId.get(k);
					String strServiceName = (String) hmRosterServiceName.get(strServiceId);
		%>

		<tr>

			<%
				for (int i = 7; alDate!=null && i < ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : alDate.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
							if (i == 7) {
			%>
			<td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
			<%
				}
			%>
			<td style="background-color:<%=strHolidayColour%>" align="center" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "FROM_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "FROM_" + strServiceId) : "-")%></td>
			<%-- <td class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "TO_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "TO_" + strServiceId) : "-")%></td> --%>

			<%
				}
			%>


		</tr>
		<%
			}
		%>

	</table>



	<table cellpadding="1" cellspacing="1" style="margin-top: 50px"
		width="95%">

		<tr>

			<td class="">&nbsp;</td>
			<%
				for (int i = ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15); alDate!=null && i < ((alDate.size() >= 21) ? 21 : alDate.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
			%>
			<td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter"><%=showData((String) alDay.get(i))%></td>
			<%
				}
			%>
		</tr>

		<tr>

			<td class="">&nbsp;</td>
			<%
				for (int i = ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15); alDate!=null && i < ((alDate.size() >= 21) ? 21 : alDate.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
			%>
			<td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter"><%=showData((String) alDate.get(i))%></td>
			<%
				}
			%>
		</tr>


		<%-- <tr>
			<td class="">&nbsp;</td>
			<%
				for (int i = ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15); i < ((alDate.size() >= 21) ? 21 : alDate.size()); i++) {
			%>
			<td class="reportHeading alignCenter">IN</td>
			<!-- <td class="reportHeading alignCenter">OUT</td> -->
			<%
				}
			%>

		</tr> --%>

		<%
			k = 0;
				for (k = 0; alServiceId!=null && alEmpId!=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
					String strCol = ((k % 2 == 0) ? "1" : "");
					Map hm = (Map) hmList.get((String) alEmpId.get(0));
					Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
					String strServiceId = (String) alServiceId.get(k);
					String strServiceName = (String) hmRosterServiceName.get(strServiceId);
		%>

		<tr>

			<%
				for (int i = ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15); alDate!=null && i < ((alDate.size() >= 21) ? 21 : alDate.size()); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
							if (i == ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15)) {
			%>
			<td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
			<%
				}
			%>
			<td style="background-color:<%=strHolidayColour%>" align="center" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "FROM_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "FROM_" + strServiceId) : "-")%></td>
			<%-- <td class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "TO_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "TO_" + strServiceId) : "-")%></td> --%>

			<%
				}
			%>


		</tr>
		<%
			}
		%>

	</table>



	<table cellpadding="1" cellspacing="1" style="margin-top: 50px"
		width="95%">

		<tr>

			<td class="">&nbsp;</td>
			<%
				for (int i = 21; alDate!=null && i < alDate.size(); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
					
			%>
			<td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter"><%=showData((String) alDay.get(i))%></td>
			<%
				}
			%>
		</tr>

		<tr>

			<td class="">&nbsp;</td>
			<%
				for (int i = 21; alDate!=null && i < alDate.size(); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
			%>
			<td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter"><%=showData((String) alDate.get(i))%></td>
			<%
				}
			%>
		</tr>


		<%-- <tr>
			<td class="">&nbsp;</td>
			<%
				for (int i = 21; i < alDate.size(); i++) {
			%>
			<td class="reportHeading alignCenter">IN</td>
			<!-- <td class="reportHeading alignCenter">OUT</td> -->
			<%
				}
			%>

		</tr>
 --%>
		<%
			k = 0;
				for (k = 0; alServiceId!=null && alEmpId!=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
					String strCol = ((k % 2 == 0) ? "1" : "");
					Map hm = (Map) hmList.get((String) alEmpId.get(0));
					Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
					String strServiceId = (String) alServiceId.get(k);
					String strServiceName = (String) hmRosterServiceName.get(strServiceId);
		%>

		<tr>


			<%
				for (int i = 21; alDate!=null && i < alDate.size(); i++) {
					
					String strHolidayColour = null;
					strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
					if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
					
							if (i == 21) {
			%>
			<td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
			<%
				}
			%>
			<td style="background-color:<%=strHolidayColour%>" align="center" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "FROM_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "FROM_" + strServiceId) : "-")%></td>
			<%-- <td class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "TO_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "TO_" + strServiceId) : "-")%></td> --%>

			<%
				}
			%>


		</tr>
		<%
			}
		%>

		<%
			} else {
		%>
		<tr>
			<td class="">&nbsp;</td>
			<td class="reportLabel alignCenter" colspan="14">
			<div class="msg nodata"><span>You have no roster allocated for the current Pay Cycle, please change 'Pay Cycle' to view other roster's.</span></div>
			</td>
		</tr>
		
		<%
			}
		%>
</table>





<div>
		<%--  <table>
				<%
					
					for (int z = 0; z < shiftDetails.size()-1;) {
				%>
				<tr>
					<td><div style="height:15px; padding:5px;text-align:center;  background-color:<%=shiftDetails.get(z++)%>"><%=shiftDetails.get(z++)%></div></td>
				</tr>
				<tr>
					<td>Shift Start <%=shiftDetails.get(z++)%></td>
					<td>End <%=shiftDetails.get(z++)%></td>
				</tr>
				<tr>
					<td>Break Start <%=shiftDetails.get(z++)%></td>
					<td>End <%=shiftDetails.get(z++)%></td>
				</tr>
				<%
					}
				%> 
			</table> --%>
			<div class="clr"></div>
				<%
							
					for (int z = 0; shiftDetails!=null && z < shiftDetails.size()-1;) {
						
					String strColour = shiftDetails.get(z++);
				%>
				<div style="width:100%;float:left;margin:5px">
     				<div style="border:1px solid <%=strColour%>; float:left;">
						<div style="background-color:<%=strColour%>;width:100%;height:15px;text-align:center"><%=shiftDetails.get(z++)%></div>
						<p style="padding-left:5px;padding-right:5px"><span style="font-weight:bold">Shift Start</span> <%=shiftDetails.get(z++)%>  	<span style="font-weight:bold">End</span> <%=shiftDetails.get(z++)%></p>
						<p style="padding-left:5px;padding-right:5px"><span style="font-weight:bold">Break Start</span> <%=shiftDetails.get(z++)%>  	<span style="font-weight:bold">End</span> <%=shiftDetails.get(z++)%></p>
					</div>
				</div>
     			<%
					}
				%> 
			
		</div>


</div>