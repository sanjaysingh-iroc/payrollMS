
<%@page import="com.konnect.jpms.util.IStatements"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.*, com.konnect.jpms.util.*"%>

<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	List alDate = (List) request.getAttribute("alDate");
	List alDay = (List) request.getAttribute("alDay");
	
	List alEmpName = (List) request.getAttribute("alEmpName");
	List alEmpCode = (List) request.getAttribute("alEmpCode");
	List alEmpCodeLink = (List) request.getAttribute("alEmpCodeLink");
	List alEmpIdPayroll = (List) request.getAttribute("alEmpIdPayroll");
	
	
	Map hmEmpData = (Map) request.getAttribute("hmManagerAttendenceReport");
	Map hmPayMode = (Map) request.getAttribute("hmPayMode");
	Map hmEarlyLateReporting = (Map) request.getAttribute("hmEarlyLateReporting");
	
	String dblTotalWorkedHours = (String) request.getAttribute("dblTotalWorkedHours");
	
	
	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");	
	List _alHolidays = (List) request.getAttribute("_alHolidays");
	

	

	
	String DT_MIN = (String) request.getAttribute("DT_MIN");
	String DT_MAX = (String) request.getAttribute("DT_MAX");
	
	
	

	if (alDate == null) {
		alDate = new ArrayList();
	}
	if (alDate == null) {
		alDate = new ArrayList();
	}
	if (alEmpCode == null) {
		alEmpCode = new ArrayList();
	}

	if (hmEmpData == null) {
		hmEmpData = new HashMap();
	}

	if (alEmpIdPayroll == null) {
		alEmpIdPayroll = new ArrayList();
	}
	
	
%>

<%
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>

<script>
function temp(val){
	document.frm_roster_actual.alphaValue.value=val;
	document.frm_roster_actual.submit();	
}

</script>



    <div class="pagetitle">
      <span>My Staff's Payroll for the period of <%=DT_MIN%> - <%=DT_MAX%></span>
    </div>

  
    <div class="leftbox reportWidth">

		<s:form cssStyle="margin-left:20px; margin-bottom:10px" theme="simple" method="post" name="frm_roster_actual">
		<s:select cssStyle="float:right; margin-bottom:10px;width:300px" label="Select PayCycle" name="paycycle" listKey="paycycleId"
				listValue="paycycleName" headerKey="0" headerValue="Select Paycycle"
				onchange="document.frm_roster_actual.submit();"
				list="paycycleList" key="" />
				
		
				
		<div class="alphaValue" style="margin-bottom:45px">
		<jsp:include page="/jsp/common/alphaValue.jsp" flush="true"></jsp:include>
		</div>
		
				
		</s:form>
		
		
		
		
		
		
		<s:form theme="simple" method="POST" action="ApprovePayroll.action">
		
		
		
		
			<input type="hidden" id="ApprovePayroll_dtMin" value="<%=DT_MIN%>"
				name="dtMin" />
			<input type="hidden" id="ApprovePayroll_dtMax" value="<%=DT_MAX%>"
				name="dtMax" />
			<input type="hidden" id="ApprovePayroll_approve" value="APPROVE"
				name="approve" />
		
		<div style="float:right;">
		<div class="btn_below_scroll">
		<s:submit value="Approve Payroll" cssClass="input_button"></s:submit>
		
			</div>
		</div>
			<div class="clr"></div>
		
		<div class="scroll" style="width:100%;height:500px">
			<table cellpadding="2" cellspacing="1">
		
				<tr>
					<td class="reportHeading alignCenter">&nbsp;</td>
					<td class="reportHeading alignCenter">&nbsp;</td>
					<td colspan="4" class="reportHeading alignCenter">&nbsp;</td>
					<td class="reportHeading alignCenter">&nbsp;</td>
					<%
						for (int i = 0; i < alDay.size(); i++) {
					%>
		
					<td class="reportHeading alignCenter" <%= ((_alHolidays.contains(i+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(i+"")+"\'":"")) %>><%=(String) alDay.get(i)%></td>
		
					<%
						}
					%>
		
		
				</tr>
		
				<tr>
					<td class="reportHeading alignCenter" nowrap>EMPLOYEE NAME</td>
					<td class="reportHeading alignCenter" nowrap>APPROVE</td>
					<td class="reportHeading alignCenter" nowrap>GROSS AMOUNT</td>
					<td class="reportHeading alignCenter" nowrap>LESS TAX<br/>(-)</td>
					<td class="reportHeading alignCenter" nowrap>ALLOW<br/>(+)</td>
					<td class="reportHeading alignCenter" nowrap>NETT AMOUNT</td>
					<td class="reportHeading alignCenter" nowrap>PAY MODE</td>
					<%
						for (int i = 0; i < alDate.size(); i++) {
					%>
		
					<td class="reportHeading alignCenter" <%= ((_alHolidays.contains(i+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(i+"")+"\'":"")) %>><%=(String) alDate.get(i)%></td>
		
					<%
						}
					%>
		
		
				</tr>
		
		
		
		
				<%
					int i = 0;
					for (i = 0; i < alEmpCode.size(); i++) {
						
							String strCol = ((i%2==0)?"dark":"light");
							Map hm = (HashMap) hmEmpData.get((String) alEmpCode.get(i));
							Map hmEarlyLateMark = (HashMap)hmEarlyLateReporting.get((String) alEmpCode.get(i));
							
							
							if (hm == null) {
								hm = new HashMap();
							}
							if (hmEarlyLateMark == null) {
								hmEarlyLateMark = new HashMap();
							}
							
							
				%>
				<tr class="<%=strCol %>" title="<%=(String) alEmpName.get(i)%>">
					<td nowrap="nowrap" class="alignLeft pos_abs"><%=(String) alEmpCodeLink.get(i)%></td>
					<td class="alignCenter">
					
					<%if(!alEmpIdPayroll.contains((String) alEmpCode.get(i))){ %>
					<input style="width:50px" type="checkbox" id="ApprovePayroll_chbox" checked="checked" value="<%=(String) alEmpCode.get(i)%>" name="chbox" /><input
						type="hidden" id="ApprovePayroll_empID"	value="<%=(String) alEmpCode.get(i)%>" name="empID" /> 
						<%} else{%>Approved<%}%>
						</td>
					<td class="alignRight"><%=(((String) hm.get("PAYGROSS") != null) ? (String) hm.get("PAYGROSS") : "0")%></td>
					<td class="alignRight"><%=(((String) hm.get("PAYDEDUCTION") != null) ? (String) hm.get("PAYDEDUCTION") : "0")%></td>
					<td class="alignRight"><%=(((String) hm.get("PAYALLOWANCE") != null) ? (String) hm.get("PAYALLOWANCE") : "0")%></td>
					<td class="alignRight"><%=(((String) hm.get("PAYNET") != null) ? (String) hm.get("PAYNET") : "0")%></td>
		
					<td class="alignLeft"><%=(((String) hmPayMode.get((String) alEmpCode.get(i)) != null) ? (String) hmPayMode.get((String) alEmpCode.get(i)) : "-")%></td>
		
					<%
						for (int k = 0; k < alDate.size(); k++) {
					%>
		
					<td valign="top" class="alignLeft" <%= ((_alHolidays.contains(k+"")?"style=\'background-color:"+(String)_hmHolidaysColour.get(k+"")+"\'":"")) %>><a href="<%=request.getContextPath()%>/UpdateClockEntries.action?DATE=<%=(String) alDate.get(k) %>&EMPID=<%=(String) alEmpCode.get(i)%>">
				<div class="time_edit_setting"></div></a><div style="clear:both;"></div><%=(((String) hmEarlyLateMark.get((String) alDate.get(k))!=null)?(String) hmEarlyLateMark.get((String) alDate.get(k)):"")%> <%=(((String) hm.get((String) alDate.get(k)) != null) ? (String) hm.get((String) alDate.get(k)) : "0")%></td>
		
					<%
						}
					%>
		
				</tr>
				<%
					}
					
					if(i==0){
						%>
						<tr class="dark">
						<td class="alignCenter" colspan="22">No entries reported yet for payroll.</td>
						</tr>
						
						<%
					}
				%>
		
			</table>
			</div>
		
		</s:form>

	</div>

