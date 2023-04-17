 
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib uri="http://granule.com/tags" prefix="g" %>


<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String proType = (String)request.getAttribute("proType");
	
	Map<String, String> hmProInfoDisplay = (Map<String, String>) request.getAttribute("hmProInfoDisplay");
	if(hmProInfoDisplay == null) hmProInfoDisplay = new HashMap<String, String>();
	
	List alEmpId = (List)request.getAttribute("alEmpId");
	Map hmEmpNames = (Map)request.getAttribute("hmEmpNames");
	Map hmEmpLevel = (Map)request.getAttribute("hmEmpLevel");
	Map hmLevel = (Map)request.getAttribute("hmLevel");
	Map hmWLocation = (Map)request.getAttribute("hmWLocation");
	Map hmEmpWLocation = (Map)request.getAttribute("hmEmpWLocation");
	Map<String,String> empMp = (Map<String,String>)request.getAttribute("empMp");
	UtilityFunctions uF = new UtilityFunctions();
	
	Map hmEmpCostAndRate = (Map)request.getAttribute("hmEmpCostAndRate");
	
	Map<String, String> hmEmpAllocatePercentAndBilledUnbilled = (Map<String, String>)request.getAttribute("hmEmpAllocatePercentAndBilledUnbilled");
	
	//Map hmEmpSalaryMap  = (Map)request.getAttribute("hmEmpSalaryMap");
	Map hmLeaves = (Map)request.getAttribute("hmLeaves");
	Map hmEmpList = (Map)request.getAttribute("hmEmpList");
	Map hmEmpSkills = (Map)request.getAttribute("hmEmpSkills");
	
	Map<String, String> hmEmployeeExperience = (Map<String, String>)request.getAttribute("hmEmployeeExperience");
	Map<String, Map<String, List<String>>> hmEmpProDetails = (Map<String, Map<String, List<String>>>) request.getAttribute("hmEmpProDetails");
	
	//Map hmEmpSkillsRates = (Map)request.getAttribute("hmEmpSkillsRates");
	Map hmTaskAllocation = (Map)request.getAttribute("hmTaskAllocation");
	
	Map<String, String> hmTLMembEmp = (Map<String, String>) request.getAttribute("hmTLMembEmp");
	
	String strActualBillingType = (String) request.getAttribute("strActualBillingType");
	String strProCurrId = (String) request.getAttribute("strProCurrId");
	
	String fromPage = (String) request.getAttribute("fromPage");
	
	String tblWidth = "100%";
	String strMargin = "0px";
	String strMarginLeft = "0px";
	if(fromPage != null && fromPage.equals("VAP")) {
		tblWidth = "99%";
		strMargin = "5px";
		strMarginLeft = "5px";
	}
%>



<div  style="float: left; width:100%; margin-left: <%=strMarginLeft %>"><b>Project Team Summary</b></div>  <!-- font-size: 14px; -->			
<table class="table-bordered table"> <!-- overflowtable  -->
  <thead>
	<tr>
		<% if(proType == null || proType.equals("") || proType.equals("null") || !proType.equalsIgnoreCase("P")) { %>
			<th>TL</th>
		<% } %>
		<th>Team</th>
		<th>Employee Name</th> 
		<th>Skill </th>
		<th>Exp </th>
		<!-- <th>Project Name </th>
		<th>Earliest Release Date </th> -->
		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L") || proType.equalsIgnoreCase("P")) { %>
			<th nowrap="nowrap">Allocation Date </th>
			<th nowrap="nowrap">Release Date </th>
			<th>Allocation % </th>
			<th>Billed </th>
		<% } %>
		<th>Level </th>
		<th>Work Location </th>
		
		<!-- Rate/Day & Cost/Day hide for KPCA  -->
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
			<th align="center">Rate/
			<% if(strActualBillingType != null && strActualBillingType.equals("H")) { %>
			Hour
			<% } else if(strActualBillingType != null && strActualBillingType.equals("M")) { %>
			Month
			<% } else { %>
			Day
			<% } %>
			<br/>(<%=uF.showData((String)request.getAttribute("strShortCurrency"), "-") %>)</th>
		<% } %>
		
		
		<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
			<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<th align="center">Cost/
				<% if(strActualBillingType != null && strActualBillingType.equals("H")) { %>
				Hour
				<% } else if(strActualBillingType != null && strActualBillingType.equals("M")) { %>
				Month
				<% } else { %>
				Day
				<% } %>
				<br/>(<%=uF.showData((String)request.getAttribute("strShortCurrency"), "-") %>)</th>
			<% } %>
		
			<th>Availability</th>
			<th>Work Allocation
			<input type="hidden" name="hideExistEmps" id="hideExistEmps" value="<%=alEmpId != null ? alEmpId.size() : "0" %>">
			</th>
		<% } %>
	</tr> 
  </thead>
	
	<%
	String strEmpIdNew = null;
	
	for(int i=0; alEmpId != null && i<alEmpId.size(); i++){ 
		
		strEmpIdNew = (String)alEmpId.get(i);
		
		Map hmInner = (Map)hmEmpList.get((String)alEmpId.get(i));
		if(hmInner == null)hmInner = new HashMap();
		
		Map hmLocation = (Map)hmWLocation.get((String)hmEmpWLocation.get((String)alEmpId.get(i)));
		if(hmLocation == null)hmLocation = new HashMap();
		
		Map hmInnerLeave = (Map)hmLeaves.get((String)alEmpId.get(i));
		if(hmInnerLeave == null)hmInnerLeave = new HashMap();	
		
		Map<String, List<String>> hmProData = hmEmpProDetails.get((String)alEmpId.get(i));
		if(hmProData == null)hmProData = new HashMap<String, List<String>>();
	%> 
	<tr>
	<% if(proType == null || proType.equals("") || proType.equals("null") || !proType.equalsIgnoreCase("P")) { %>
		<td>
			<input type="hidden" name="strAllEmpIdS" value="<%=(String)alEmpId.get(i)%>">
			<input type="checkbox" name="strTeamLeadIdS" disabled="disabled" <%if(hmTLMembEmp != null && hmTLMembEmp.get((String)alEmpId.get(i)+"_T") != null && hmTLMembEmp.get((String)alEmpId.get(i)+"_T").equals("TL")) { %> checked="checked" <% } %> >
		</td>
	<% } %>	
		<td>
			<input type="checkbox" name="strEmpIdS" disabled="disabled" <%if(hmTLMembEmp != null && hmTLMembEmp.get((String)alEmpId.get(i)+"_M") != null && hmTLMembEmp.get((String)alEmpId.get(i)+"_M").equals("MEMB")) { %> checked="checked" <% } %> >
		</td>
		<td nowrap="nowrap"><%=(String)hmEmpNames.get((String)alEmpId.get(i))%></td>
		<td nowrap="nowrap"><%=uF.showData((String)hmEmpSkills.get(strEmpIdNew), "")%></td>
		<td nowrap="nowrap"><%=uF.showData(hmEmployeeExperience.get(strEmpIdNew), "")%></td>
		<!-- <td> -->
		<% 	Iterator<String> it = hmProData.keySet().iterator();
			String earliestReleaseDate = null;
			double dblBilledPercent = 0;
			double dblUnbilledPercent = 0;
			double dblAvailablePercent = 0;
			while(it.hasNext()) {
				String proId = it.next();
				List<String> innerList = hmProData.get(proId);
				if(earliestReleaseDate ==null) {
					earliestReleaseDate = innerList.get(1);
				}
				if(uF.parseToBoolean(innerList.get(2))) {
					dblBilledPercent += uF.parseToDouble(innerList.get(3));
				} else {
					dblUnbilledPercent += uF.parseToDouble(innerList.get(3));
				}
		%>
			<%-- <%=uF.showData(innerList.get(0), "")%><br/> --%>
		<% } %>
		<!-- </td> -->
		<%-- <td nowrap="nowrap"><%=uF.showData(earliestReleaseDate, "") %></td> --%>
		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L") || proType.equalsIgnoreCase("P")) { %>
			<td nowrap="nowrap">
				<%-- <input type="hidden" name="hideAllocationDate_<%=alExistEmpId.get(i) %>" id="hideAllocationDate_<%=alExistEmpId.get(i) %>" value="<%=uF.showData(hmEmpAllocationPercent.get(alExistEmpId.get(i)), "0") %>"> --%>
				<div id="divAllocationDateEdit_<%=alEmpId.get(i) %>" style="display: none;">
					<input type="text" name="allocationDate_<%=alEmpId.get(i) %>" id="allocationDate_<%=alEmpId.get(i) %>" style="width: 90px !important;" readonly="readonly"/> <a href="javascript:void(0);" onclick="updateAllocationDate('<%=alEmpId.get(i) %>','<%=(String)request.getAttribute("proId") %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div>
				<div id="divAllocationDateReadonly_<%=alEmpId.get(i) %>">
					<span id="spanAllocationDate_<%=alEmpId.get(i) %>"><%=hmEmpAllocatePercentAndBilledUnbilled.get(alEmpId.get(i)+"_ALLOCATION_DATE") %></span> <a href="javascript:void(0);" style="float: right;" onclick="openForUpdateAllocationDate('<%=alEmpId.get(i) %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div> 
			</td>
			
			<td nowrap="nowrap">
				<%-- <input type="hidden" name="hideReleaseDate_<%=alEmpId.get(i) %>" id="hideReleaseDate_<%=alEmpId.get(i) %>" value="<%=uF.showData(hmEmpAllocationPercent.get(alEmpId.get(i)), "0") %>"> --%>
				<div id="divReleaseDateEdit_<%=alEmpId.get(i) %>" style="display: none;">
					<input type="text" name="releaseDate_<%=alEmpId.get(i) %>" id="releaseDate_<%=alEmpId.get(i) %>" style="width: 90px !important;" readonly="readonly"/> <a href="javascript:void(0);" onclick="updateReleaseDate('<%=alEmpId.get(i) %>','<%=(String)request.getAttribute("proId") %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div>
				<div id="divReleaseDateReadonly_<%=alEmpId.get(i) %>">
					<span id="spanReleaseDate_<%=alEmpId.get(i) %>"><%=hmEmpAllocatePercentAndBilledUnbilled.get(alEmpId.get(i)+"_RELEASE_DATE") %></span> <a href="javascript:void(0);" style="float: right;" onclick="openForUpdateReleaseDate('<%=alEmpId.get(i) %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div> 
			</td>
			<script type="text/javascript">
				$("#allocationDate_<%=alEmpId.get(i) %>").datepicker({format : 'dd/mm/yyyy'});
				$("#releaseDate_<%=alEmpId.get(i) %>").datepicker({format : 'dd/mm/yyyy'});
			</script>
			
			<td nowrap="nowrap">
				<input type="hidden" name="allocatatedPercent_<%=alEmpId.get(i) %>" id="allocatatedPercent_<%=alEmpId.get(i) %>" value="<%=(dblBilledPercent+dblUnbilledPercent) %>">
				<div id="divAllocationEdit_<%=alEmpId.get(i) %>" style="display: none;">
					<input type="text" name="allocation_<%=alEmpId.get(i) %>" id="allocation_<%=alEmpId.get(i) %>" style="width: 40px !important;" onkeypress="return isNumberKey(event)" onkeyup="checkAllocationPercent('<%=alEmpId.get(i) %>')"/> <a href="javascript:void(0);" onclick="updateAllocationPercent('<%=alEmpId.get(i) %>','<%=(String)request.getAttribute("proId") %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div>
				<div id="divAllocationReadonly_<%=alEmpId.get(i) %>">
					<span id="spanAllocation_<%=alEmpId.get(i) %>"><%=hmEmpAllocatePercentAndBilledUnbilled.get(alEmpId.get(i)+"_ALLOCATION_PERCENT") %></span>% <a href="javascript:void(0);" style="float: right;" onclick="openForUpdateAllocationPercent('<%=alEmpId.get(i) %>');"><i class="fa fa-pencil-square-o"></i></a>
				</div> 
			</td>
			<td nowrap="nowrap"><input type="checkbox" name="billedUnbilled_<%=alEmpId.get(i) %>" id="billedUnbilled_<%=alEmpId.get(i) %>" <%=uF.parseToBoolean(hmEmpAllocatePercentAndBilledUnbilled.get(alEmpId.get(i)+"_BILLED_UNBILLED")) ? "checked" : "" %> onclick="updateBilledUnbilledStatus('<%=alEmpId.get(i) %>','<%=(String)request.getAttribute("proId") %>');"/></td>
		<% } %>
		<td nowrap="nowrap"><%=(String)hmLevel.get((String)hmEmpLevel.get((String)alEmpId.get(i)))%> </td>
		<td nowrap="nowrap"><%=uF.showData((String)hmLocation.get("WL_NAME"), "")%></td>
		
		<!-- Rate/Day & Cost/Day hide for KPCA  -->
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
			<%if(hmEmpCostAndRate.get((String)alEmpId.get(i)+"_RATE") != null) { %>
			<td class="alignRight padRight20"><input type="hidden" name="rate" value="<%=uF.showData((String)hmEmpCostAndRate.get((String)alEmpId.get(i)+"_RATE"), "0")%>">
			<span id="<%=(String)alEmpId.get(i) %>RateSpan">
				<%=uF.showData((String)hmEmpCostAndRate.get((String)alEmpId.get(i)+"_RATE"), "0") %>
			</span>
			<% if(fromPage == null || !fromPage.equals("VAP")) { %>
				<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
					<a title="Edit Rate" class="fa fa-edit" onclick="editEmpRateAndCost('<%=(String)request.getAttribute("proId") %>','<%=(String)alEmpId.get(i) %>', '<%=strActualBillingType %>', 'rate', '<%=strProCurrId %>');" href="javascript:void(0);">&nbsp;</a>
				<% } %>
			<% } %>
			</td>
			<% } else { %>
			
			<% } %>
		<% } %>
		
		<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
			<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<%if(hmEmpCostAndRate.get((String)alEmpId.get(i)+"_COST") != null) { %>
				<td class="alignRight padRight20">
				<input type="hidden" value="<%=(String)hmEmpCostAndRate.get((String)alEmpId.get(i)+"_COST")%>" name="actualRate">
				<span id="<%=(String)alEmpId.get(i) %>CostSpan">
					<%=(String)hmEmpCostAndRate.get((String)alEmpId.get(i)+"_COST") %>
				</span>
				<% if(fromPage == null || !fromPage.equals("VAP")) { %>
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						<a title="Edit Cost" class="fa fa-edit" onclick="editEmpRateAndCost('<%=(String)request.getAttribute("proId") %>','<%=(String)alEmpId.get(i) %>', '<%=strActualBillingType %>', 'cost', '<%=strProCurrId %>');" href="javascript:void(0);">&nbsp;</a>
					<% } %>
				<% } %>	
				</td>
				<% } else { %>
				<td class="alignRight padRight20"><input type="hidden" name="actualRate">
				<span id="<%=(String)alEmpId.get(i) %>CostSpan">0</span>
				<% if(fromPage == null || !fromPage.equals("VAP")) { %>
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						<a title="Edit Cost" class="fa fa-edit" onclick="editEmpRateAndCost('<%=(String)request.getAttribute("proId") %>','<%=(String)alEmpId.get(i) %>', '<%=strActualBillingType %>', 'cost', '<%=strProCurrId %>');" href="javascript:void(0);">&nbsp;</a>
					<% } %>
				<% } %>
				</td>
				<% } %>
			<% } %>

			<td nowrap="nowrap">
				<%if(hmInnerLeave.size()==0) { %>
					<div style="background-color: #00CC00">No Leave</div>
				<%} else if(hmInnerLeave.size() < 4) { %>
					<div style="background-color: #99FF00"><%=hmInnerLeave.size() %> leaves</div>
				<%} else if(hmInnerLeave.size() < 7) { %>
					<div style="background-color: #FFFF33"><%=hmInnerLeave.size() %> leaves</div>
				<%} else if(hmInnerLeave.size() < 15) { %>
					<div style="background-color: #FF9900"><%=hmInnerLeave.size() %> leaves</div>
				<%} else if(hmInnerLeave.size() >= 15) { %>
					<div style="background-color: #FF3300"><%=hmInnerLeave.size() %> leaves</div>
				<% } %>
			</td>	
			
			<td align="center"><%=uF.showData((String)hmTaskAllocation.get((String)alEmpId.get(i)), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\">&nbsp;</div>") %></td>
		<% } %>
	
	</tr>
	<% } if(alEmpId!=null && alEmpId.size()==0) { %>
	<tr><td colspan="10"><div class="nodata msg" style="width:92%"><span>No employee available in chosen skill category</span></div></td></tr>
	<% } %>
</table>		
