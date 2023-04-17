 
 <%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>

<script type="text/javascript"> 
	
    $(function() {
    	 bindDate();
    }); 
    
    function bindDate(){
    	//alert("1");
    	if(document.getElementsByName("interviewdate")) {
	    	$("input[name=interviewdate]").datepicker({format: 'dd/mm/yyyy'});
	    	//alert("2");
    	}
    	if(document.getElementsByName("interviewTime")) {
	    	$('input[name=interviewTime]').datetimepicker({format: 'HH:mm'});
	    	//alert("3");
    	}
    	if(document.getElementsByName("preferedDate")) {
	    	$("input[name=preferedDate]").datepicker({format: 'dd/mm/yyyy'});
	    	//alert("4");
    	}
    	if(document.getElementsByName("preferedTime")) {
	    	$('input[name=preferedTime]').datetimepicker({format: 'HH:mm'});
	    	//alert("5"); 
    	}
    }

</script>
    
<%
	UtilityFunctions uF = new UtilityFunctions();
	String RID=(String) request.getAttribute("recruitId"); 

 	Map<String,List<String>> hmPanelScheduleInfo=(Map)request.getAttribute("hmPanelScheduleInfo");
	Map<String,List<String>> hmPanelInterviewTaken=(Map)request.getAttribute("hmPanelInterviewTaken");
	List<String> roundIdsRecruitwiseList = (List<String>)request.getAttribute("roundIdsRecruitwiseList");
	Map<String, String> hmpanelNameRAndRwise = (Map<String, String>)request.getAttribute("hmpanelNameRAndRwise");
	
	Map<String, String> hmRoundAssessment = (Map<String, String>) request.getAttribute("hmRoundAssessment");
	if(hmRoundAssessment == null) hmRoundAssessment = new HashMap<String, String>();
	
	//Map<String, List<String>> hmpanelIDSRAndRwise =(Map<String, List<String>>)request.getAttribute("hmpanelIDSRAndRwise");
 %>
			<div style="width: 100%;">
			<table style="width: 100%" class="table">
			<%		
			Map<String, String> hmDates=(Map)request.getAttribute("hmDateMap");
			if(hmDates==null)hmDates=new HashMap<String, String>();
			Map<String, String> hmTime=(Map)request.getAttribute("hmTimeMap");
		%>
			<%
			boolean flag = false;
			for (int i=0; i<roundIdsRecruitwiseList.size(); i++) { %>
			
			<tr>
				<td valign="top" colspan="4">
					<div style="float: left; width: 100%;">
						<div style="float: left; min-width: 25%; max-width: 40%;"><b><u> Round <%=roundIdsRecruitwiseList.get(i)%>:</u></b><br/><%=hmpanelNameRAndRwise.get(roundIdsRecruitwiseList.get(i))%></div>
						<% List<String> alInner = hmPanelScheduleInfo.get(roundIdsRecruitwiseList.get(i));
							if (alInner != null) { 
						%>
							<div id="interSchDateTime<%=i%>" style="float: left;"> Date: <%=alInner.get(0)%>&nbsp;&nbsp; Time: <%=alInner.get(1)%>
								<input type="hidden" name="interviewdate" id="interviewdate<%=i%>"/>
								<input type="hidden" name="interviewTime" id="interviewTime<%=i%>"/>
								<a href="javascript: void(0)" class="fa fa-times-circle cross" onclick="addToCalender(<%=roundIdsRecruitwiseList.get(i)%>,<%=RID %>,<%=(String)request.getAttribute("candidateId")%>,<%=i%>,'remove', <%=(String)request.getAttribute("notiStatus") %>, <%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>);">
									<!-- <img src="images1/list-remove.png" width="18" height="18" /> -->
								</a>
							</div> 
						<% } else { %>
						<% if(hmPanelInterviewTaken.keySet().contains(roundIdsRecruitwiseList.get(i))) {
								List<String> alInnerInterviewTaken = hmPanelInterviewTaken.get(roundIdsRecruitwiseList.get(i));
								if (alInnerInterviewTaken != null) {
						%>
						<div id="interSchDateTime<%=i%>" style="float: left;"> Date: <%=alInnerInterviewTaken.get(0)%>&nbsp;&nbsp; Time: <%=alInnerInterviewTaken.get(1)%>
							&nbsp;Interview Taken</div>
                            <% } } else { %>
                            <%if(flag == false) { %>
						<div id="interSchDateTime<%=i%>" style="float: left;">
							Date: <input type="text" name="interviewdate" id="interviewdate<%=i%>" style="width: 90px !important;"/>
							Time: <input type="text" name="interviewTime" id="interviewTime<%=i%>" style="width: 50px !important"/>&nbsp;
							<a href="javascript: void(0)" class="fa fa-check-circle checknew" title="Add to Calender" onclick="addToCalender(<%=roundIdsRecruitwiseList.get(i)%>,<%=RID %>,<%=(String)request.getAttribute("candidateId")%>,<%=i%>,'insert', <%=(String)request.getAttribute("notiStatus") %>, <%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>);">
								<!-- <img src="images1/calendar_grey.png" width="18" height="18"/> -->
							</a>
						</div>
						
						<% } } } %>
					</div>
					<% if(uF.parseToInt(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID")) > 0) { %>
						<div style="float: left; width: 100%;">
							<span>Assessment:</span>
							<span><a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_NAME"), "") %></a></span>
							<span id="assessSpan_<%=roundIdsRecruitwiseList.get(i) %>" style="margin-left: 7px;">
								<a href="javascript:void(0);" onclick="sendAssessmentToCandidate('<%=RID %>', '<%=roundIdsRecruitwiseList.get(i) %>', '<%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>', '<%=(String)request.getAttribute("CandID")%>');">Send Now</a>
							</span>
						</div>
					<% } %>
				</td>	
					
					
				<% if (alInner != null) { %>
				<td width="31%"></td>
				<% } else { %>
				<% if(hmPanelInterviewTaken.keySet().contains(roundIdsRecruitwiseList.get(i))) {
						List<String> alInnerInterviewTaken = hmPanelInterviewTaken.get(roundIdsRecruitwiseList.get(i));
						if (alInnerInterviewTaken != null) {
				%>
				<td width="31%"></td>
                          <% } } else { %>
                          <%if(flag == false) { %>
				<td width="31%" valign="top">
					<div style="margin-top: -5px; width: 100%;"><strong>Preferred Date Time</strong></div>
					<%
						int count = 0;
						Iterator<String> itr = hmDates.keySet().iterator();
						while (itr.hasNext()) {
						String id = itr.next();
					%>
					<div style="margin-top: -5px; width: 100%;"><input type="radio" name="preferedDtTmRedio" id="preferedDtTmRedio<%=count + 1%>">  <!-- onclick="showOtherDateTime();" -->
					<input type="hidden" name="preferedDate" id="preferedDate<%=count + 1%>" value="<%=uF.showData(hmDates.get(id), "")%>">
						<%=uF.showData(hmDates.get(id), "Not added")%>
					<input type="hidden" name="preferedTime" id="preferedTime<%=count + 1%>" value="<%=uF.showData(hmTime.get(id), "")%>">
						<%=uF.showData(hmTime.get(id), "Not given")%>	
					</div>
					<% count++;
					 }
					%>	
					<%if(hmDates == null || hmDates.keySet().size()==0){ %>
			           <div style="margin-top: -5px; width: 100%;">
			              <%for(int a=0;a<=3;a++){ %>
			              <input type="hidden" name="preferedDtTmRedio" id="preferedDtTmRedio<%=a %>" >
			              <input type="hidden" name="preferedDate" id="preferedDate<%=a %>" >
			              <input type="hidden" name="preferedTime" id="preferedTime<%=a %>" >
			              <%} %>
			               No Dates Added </div>                 
			               <%}%>
			          	<div style="margin-top: 0px; width: 100%;">
			          	 <span style="float: left;">Specify other Dates
			          	 <input type="hidden" name="status" id="status" value="0"/>
			          	 <input type="checkbox" name="selectionDate" onclick="openSendConfirmation();"></span>
			          	 <span id="sendConfirmationTR" style="float: left; display: none;">
			          	 <%if(request.getAttribute("notiStatus") != null && request.getAttribute("notiStatus").toString().equals("1")){ %>
			          	 	<a href="javascript: void(0);" onclick="SendMailToCandidate();">Resend Mail to Candidate</a>
			          	 <%}else{ %>
			          	 	<a href="javascript: void(0);" onclick="SendMailToCandidate();">Send Mail to Candidate</a>
			          	 <%} %>
			          	 </span>
		               </div> 
				</td>
				<% } else { %>
					<td nowrap="nowrap" colspan="4" class="label" valign="top">&nbsp;</td>
				<%} %>
				<% flag = true;
					} } %>
					
				</tr>
				
				<%-- <tr>
					<td valign="top"><b><u> Round <%=roundIdsRecruitwiseList.get(i)%>:</u></b>&nbsp;<%=hmpanelNameRAndRwise.get(roundIdsRecruitwiseList.get(i))%>
					</td>
					<% List<String> alInner = hmPanelScheduleInfo.get(roundIdsRecruitwiseList.get(i));
							if (alInner != null) { 
					%>
					<td nowrap="nowrap" colspan="3" class="label" valign="top">
					<div id="interSchDateTime<%=i%>"> Date: <%=alInner.get(0)%>&nbsp;&nbsp; Time: <%=alInner.get(1)%>
					<input type="hidden" name="interviewdate" id="interviewdate<%=i%>"/>
					<input type="hidden" name="interviewTime" id="interviewTime<%=i%>"/>
					<a href="javascript: void(0)" onclick="addToCalender(<%=roundIdsRecruitwiseList.get(i)%>,<%=RID %>,<%=(String)request.getAttribute("candidateId")%>,<%=i%>,'remove');">
					<img src="images1/list-remove.png" width="18" height="18" />
					</a>
					</div>
					</td>
					<td width="31%"></td>
					<%} else{ %>
					<% if(hmPanelInterviewTaken.keySet().contains(roundIdsRecruitwiseList.get(i))){
							List<String> alInnerInterviewTaken = hmPanelInterviewTaken.get(roundIdsRecruitwiseList.get(i));
							if (alInnerInterviewTaken != null) {
					%>
					<td nowrap="nowrap" colspan="2" class="label" valign="top"><div id="interSchDateTime<%=i%>"> Date: <%=alInnerInterviewTaken.get(0)%>&nbsp;&nbsp; Time: <%=alInnerInterviewTaken.get(1)%></div></td>
					<td nowrap="nowrap" valign="top"> Interview Taken </td>
                    <%} } else{ %>
                    <%if(flag == false){ %>
					<td nowrap="nowrap" colspan="3" class="label" valign="top"><div id="interSchDateTime<%=i%>">
					Date: <input type="text" name="interviewdate" id="interviewdate<%=i%>" style="width: 65px"/>
					Time: <input type="text" name="interviewTime" id="interviewTime<%=i%>" style="width: 50px"/>&nbsp;
					<a href="javascript: void(0)" title="Add to Calender" onclick="addToCalender(<%=roundIdsRecruitwiseList.get(i)%>,<%=RID %>,<%=(String)request.getAttribute("candidateId")%>,<%=i%>,'insert');">
					<img src="images1/calendar_grey.png" width="18" height="18"/></a> 
					</div>
					</td>
					<td width="31%" valign="top">
						<div style="margin-top: -5px; width: 100%;"><strong>Preferred Date Time</strong></div>
						<%
								int count = 0;
								Iterator<String> itr = hmDates.keySet().iterator();
								while (itr.hasNext()) {
								String id = itr.next();
						%>
						<div style="margin-top: -5px; width: 100%;"><input type="radio" name="preferedDtTmRedio" id="preferedDtTmRedio<%=count + 1%>">  <!-- onclick="showOtherDateTime();" -->
						<input type="hidden" name="preferedDate" id="preferedDate<%=count + 1%>" value="<%=uF.showData(hmDates.get(id), "")%>">
							<%=uF.showData(hmDates.get(id), "Not added")%>
						<input type="hidden" name="preferedTime" id="preferedTime<%=count + 1%>" value="<%=uF.showData(hmTime.get(id), "")%>">
							<%=uF.showData(hmTime.get(id), "Not given")%>	
						</div>
						<% count++;
						 }
						%>	
						<%if(hmDates == null || hmDates.keySet().size()==0){ %>
				           <div style="margin-top: -5px; width: 100%;">
				              <%for(int a=0;a<=3;a++){ %>
				              <input type="hidden" name="preferedDtTmRedio" id="preferedDtTmRedio<%=a %>" >
				              <input type="hidden" name="preferedDate" id="preferedDate<%=a %>" >
				              <input type="hidden" name="preferedTime" id="preferedTime<%=a %>" >
				              <%} %>
				               No Dates Added </div>                 
				               <%}%>
				          	<div style="margin-top: 0px; width: 100%;">
				          	 <span style="float: left;">Specify other Dates
				          	 	<input type="hidden" name="status" id="status" value="0"/>
					          	<input type="checkbox" name="selectionDate" onclick="openSendConfirmation();"/>
				          	 </span>
				          	 <span id="sendConfirmationTR" style="float: left; display: none;">
				          	 <%if(request.getAttribute("notiStatus") != null && request.getAttribute("notiStatus").toString().equals("1")){ %>
				          	 	<a href="javascript: void(0);" onclick="SendMailToCandidate();">Resend Mail to Candidate</a>
				          	 <%}else{ %>
				          	 	<a href="javascript: void(0);" onclick="SendMailToCandidate();">Send Mail to Candidate</a>
				          	 <%} %>
				          	 </span>
			               </div> 
					</td>
					<%}else{ %>
					<td nowrap="nowrap" colspan="4" class="label">&nbsp;</td>
					<%} %>
					<%  flag = true;
						} } %>
					</tr> --%>
					
					<tr><td colspan="5"></td></tr>	
				<%} %>
				<% if(roundIdsRecruitwiseList==null || roundIdsRecruitwiseList.size()==0){ %>
					<tr> <td><label><b> No Panel Added </b> <br/>Please add panel first.</label> </td></tr>
				<% } %>
			</table>
			</div>
			