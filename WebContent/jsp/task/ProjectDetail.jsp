<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.task.FillTaskEmpList"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">

$(document).ready(function() {
	$('#lt1').DataTable({
		aLengthMenu: [
			[25, 50, 100, 200, -1],
			[25, 50, 100, 200, "All"]
		],
		//iDisplayLength: -1,
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ],
        order: [],
		columnDefs: [ {
	      "targets"  : 'no-sort',
	      "orderable": false
	    }]
	});
});


function changeTaskView(viewType) {
	if(viewType == 'LIST') {
		document.getElementById("listView").style.display="block";
		document.getElementById("agileView").style.display="none";
		document.getElementById("aView").className = "";
		document.getElementById("lView").className = "btn btn-primary disabled btn-sm active";
	} else {
		document.getElementById("listView").style.display="none";
		document.getElementById("agileView").style.display="block";
		document.getElementById("lView").className = "";
		document.getElementById("aView").className = "btn btn-primary disabled btn-sm active";
	}
}

</script>


	<%

		Map<String, String> hmProCompPercent = (Map<String, String>)request.getAttribute("hmProCompPercent");
		if(hmProCompPercent == null) hmProCompPercent = new HashMap<String, String>();
		
		List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
		//List<List<String>> taskList = (List<List<String>>)request.getAttribute("taskList");
		Map<String, List<String>> hmProTasks = (Map<String, List<String>>) request.getAttribute("hmProTasks");
		Map<String, List<List<String>>> hmProSubTasks = (Map<String, List<List<String>>>) request.getAttribute("hmProSubTasks");
		
		UtilityFunctions uF = new UtilityFunctions();
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		
		String strProId = (String) request.getAttribute("pro_id");
		String proType = (String) request.getAttribute("proType");
		String pageType = (String) request.getAttribute("pageType");

		if(request.getAttribute("PROJECT_NAME") != null) {
			String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
			String costLbl = "Hr";
			String estimateLbl = "hours";
			if((String)request.getAttribute("PROJECT_CALC_TYPE") != null && "D".equalsIgnoreCase((String)request.getAttribute("PROJECT_CALC_TYPE"))) {
				costLbl = "Day";
				estimateLbl = "days";
			} else if((String)request.getAttribute("PROJECT_CALC_TYPE") != null && "M".equalsIgnoreCase((String)request.getAttribute("PROJECT_CALC_TYPE"))) {
				costLbl = "Month";
				estimateLbl = "months";
			}

		//===start parvez date: 14-10-2022===	
			/* Map<String, String> hmProOwner = (Map<String, String>) request.getAttribute("hmProOwner");
			if(hmProOwner == null) hmProOwner = new HashMap<String, String>(); */
			List<Map<String, String>> alProOwners = (List<Map<String, String>>) request.getAttribute("alProOwners");
			if(alProOwners == null) alProOwners = new ArrayList<Map<String,String>>();
		//===end parvez date: 14-10-2022===	
			
			Map<String, String> hmCustomer = (Map<String, String>) request.getAttribute("hmCustomer");
			if(hmCustomer == null) hmCustomer = new HashMap<String, String>();
			
			double proCompletePecent = uF.parseToDouble(hmProCompPercent.get(strProId));
			if(proCompletePecent < 0) {
				proCompletePecent = 0;
			}
			String strIdealTime = (String)request.getAttribute("IDEAL_TIME");
			String strActualTime = (String)request.getAttribute("ACTUAL_TIME");
			String strCalType = (String)request.getAttribute("CAL_TYPE");
		%> 
			<!-- Post -->
			<!-- <div class="post"> -->
				<p style="font-size: 18px; font-weight: bold; color: gray; margin-bottom: 0px;"><%=uF.showData((String)request.getAttribute("PROJECT_NAME"),"")%>
				<% 
				if(pageType == null || !pageType.equals("MYPRO")) {
					if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L") || proType.equals("P")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
						<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<span style="float:right; margin-right:2%;">
								<input type="button" name="approve" class="btn btn-primary" style="padding: 2px 5px;" value="Mark as Completed" onclick="actionProjectCompleted('<%=pageType %>', '<%=strProId %>');"/>
							</span>
						<% } %>
						<% if((proType != null && proType.equals("P")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
						<span style="float:right; margin-right: 1%;">
							<input type="button" name="working" class="btn btn-primary" style="padding: 2px 5px;" value="Move into Working" onclick="actionProjectWorking('<%=pageType %>', '<%=strProId %>')"/>
						</span>
						<% } %>
						<span style="float:right; margin-right: 1%;">
							<input type="button" name="blocked" class="btn btn-primary" style="padding: 2px 5px;" value="Mark as Blocked" onclick="actionProjectBlocked('<%=pageType %>', '<%=strProId %>')"/>
						</span>
						<% if(uF.parseToDouble(strActualTime) == 0) { %>
							<span style="float:right; margin-right: 1%;">
								<a href="javascript:void(0)" class="fa fa-trash-o" style="color: red;" onclick="deleteProject('<%=pageType %>', '<%=strProId %>')">&nbsp;</a>
							</span>
						<% } else { %>
							<span style="float:right; margin-right: 1%;">
								<a href="javascript:void(0)" class="fa fa-trash-o" style="color: red;" onclick="alert('You can not delete this project as user has already booked the time against this project.');">&nbsp;</a>
							</span>
						<% } %>
					<% } else { %>
						<span style="float:right; margin-right: 1%;">
						<span id="unlock_div" style="font-size: 14px;"><a href="javascript:void(0)" onclick="((confirm('Are you sure, you want to unlock this project?')) ? getContent('unlock_div', 'UpdateStatus.action?pro_id=<%=strProId %>&status=n'):'')">Click to unlock</a></span>
							<%-- <input type="button" name="btnUnlock" class="btn btn-primary" style="padding: 2px 5px;" value="Click to Unclock" onclick="blockedProjects('<%=pageType %>')"/> --%>
						</span>
					<% } %>
				<% } %>
				</p>
				<div style="float:left; width:100%; margin-top: 10px;">
					<div style="float: left; width: 46%;">
				<!-- ===start parvez date: 14-10-2022=== -->		
						<%-- <div style="float:left;width:100%;">
							<span style="float: left;">
								<%if(docRetriveLocation==null) { %>
								<img height="60px" width="60px" class="lazy" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmProOwner.get("EMP_IMAGE")%>" title="<%=hmProOwner.get("EMP_NAME") %>"/>
								<% } else { %>
								<img height="60px" width="60px" class="lazy" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmProOwner.get("EMP_ID")+"/"+IConstants.I_60x60+"/"+hmProOwner.get("EMP_IMAGE")%>" title="<%=hmProOwner.get("EMP_NAME") %>"/>
								<% } %> 
							</span>
							<span style="float:left; padding-left: 10px; width: 78%;">
							<%=uF.showData((String)hmProOwner.get("EMP_NAME"),"") %><br/>
							<%if(request.getAttribute("TEAM_LEADER")!=null) { %>
								<%=uF.showData((String)request.getAttribute("TEAM_LEADER"),"") %>
								<br/>
							<% } %>
							<%=uF.showData((String)request.getAttribute("proService"),"") %>, <%=uF.showData((String)request.getAttribute("proOrg"),"") %>
							</span>
		             	</div> --%>
		             	<%-- <%for(int ii=0; ii<alProOwners.size();ii++){ 
		             		Map<String, String> hmProOwner = alProOwners.get(ii);
		             		if(hmProOwner == null) hmProOwner = new HashMap<String, String>();
		             	%> --%>
		             	
		             		<div style="float:left;width:100%;">
								<%for(int ii=0; ii<alProOwners.size();ii++){ 
				             		Map<String, String> hmProOwner = alProOwners.get(ii);
				             		if(hmProOwner == null) hmProOwner = new HashMap<String, String>();
				             	%>
								<span style="float: left;">
									<%if(docRetriveLocation==null) { %>
									<%-- <img height="60px" width="60px" class="lazy" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmProOwner.get("EMP_IMAGE")%>" title="<%=hmProOwner.get("EMP_NAME") %>"/> --%>
									<img height="20px" width="20px" class="lazy" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmProOwner.get("EMP_IMAGE")%>" title="<%=hmProOwner.get("EMP_NAME") %>"/>
									<% } else { %>
									<%-- <img height="60px" width="60px" class="lazy" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmProOwner.get("EMP_ID")+"/"+IConstants.I_60x60+"/"+hmProOwner.get("EMP_IMAGE")%>" title="<%=hmProOwner.get("EMP_NAME") %>"/> --%>
									<img height="20px" width="20px" class="lazy" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmProOwner.get("EMP_ID")+"/"+IConstants.I_60x60+"/"+hmProOwner.get("EMP_IMAGE")%>" title="<%=hmProOwner.get("EMP_NAME") %>"/>
									<% } %> 
								</span>
								&nbsp;&nbsp;<%=uF.showData((String)hmProOwner.get("EMP_NAME"),"") %><br/>
								<% } %>
								<span style="float:left; padding-left: 10px; width: 78%;">
								<%-- <%=uF.showData((String)hmProOwner.get("EMP_NAME"),"") %><br/> --%>
								<%if(request.getAttribute("TEAM_LEADER")!=null) { %>
									<%=uF.showData((String)request.getAttribute("TEAM_LEADER"),"") %>
									<br/>
								<% } %>
								<%=uF.showData((String)request.getAttribute("proService"),"") %>, <%=uF.showData((String)request.getAttribute("proOrg"),"") %>
								</span>
		             		</div>
		             		<br/>
		             		<br/>
		             		<br/>
		             	<%-- <% } %> --%>
		        <!-- ===end parvez date: 14-10-2022=== -->     	
					</div>
				<div style="float:left;width:27%">
					<div style="float:left;width:100%;margin-left: 15px;">
						<strong>Completion Status</strong><br/>
							<%=uF.showData((String)request.getAttribute("PRO_STATUS"),"") %><br/>
						<div style="float: left; width: 50%;">
							<div class="anaAttrib1"><%=uF.formatIntoOneDecimalWithOutComma(proCompletePecent) %>%</div>
							<div id="Pcomplete_<%=strProId %>" class="outbox">
								<div class="greenbox" style="width: <%=uF.formatIntoOneDecimalWithOutComma(proCompletePecent) %>%;"></div>
							</div>
						</div>
					</div>
				</div>
				<div style="float:left;width:27%">
					<div style="float:left;width:100%;">
						<strong>Condition</strong><br/>
							<span style="float:left;"><%=uF.showData((String)request.getAttribute("proDeadlinePercentColor"),"") %></span>
							<span style="float:left;margin-left: 7px;"><%=uF.showData((String)request.getAttribute("strCondition"),"") %></span>
					</div>
				</div>
			</div>
	  
			<div style="float:left;width:100%;margin-top: 20px;">
				<div style="float:left;width:33%;">
					<div style="float:left;width:100%;">
						<span style="float:left;">
						<%if(docRetriveLocation==null) { %>
							<img height="60" width="60" class="lazy" style="float: left; border: 1px lightgray solid;" src="userImages/company_avatar_photo.png" data-original="<%=IConstants.IMAGE_LOCATION + ((hmCustomer.get("CLIENT_LOGO")!=null && !hmCustomer.get("CLIENT_LOGO").toString().equals("")) ? hmCustomer.get("CLIENT_LOGO"):"company_avatar_photo.png") %>" />
						<% } else {
							String customerImage = IConstants.IMAGE_LOCATION + ((hmCustomer.get("CLIENT_LOGO")!=null && !hmCustomer.get("CLIENT_LOGO").toString().equals("")) ? hmCustomer.get("CLIENT_LOGO"):"company_avatar_photo.png");
							if(hmCustomer.get("CLIENT_LOGO")!=null && !hmCustomer.get("CLIENT_LOGO").toString().equals("")) {
							customerImage = docRetriveLocation +IConstants.I_CUSTOMER+"/"+hmCustomer.get("CLIENT_ID")+"/"+IConstants.I_IMAGE+"/"+IConstants.I_60x60+"/"+((hmCustomer.get("CLIENT_LOGO")!=null && !hmCustomer.get("CLIENT_LOGO").toString().equals("")) ? hmCustomer.get("CLIENT_LOGO"):"company_avatar_photo.png");
							}
						%>
						<img height="60" width="60" class="lazy" style="float: left; margin-right: 10px; border: 1px lightgray solid;" src="userImages/company_avatar_photo.png" data-original="<%=customerImage %>" />
						<% } %>
						</span>
						<span style="float:left;padding-left: 7px; width: 70%;">
							<%=uF.showData(hmCustomer.get("CLIENT_NAME"),"") %>, <%=uF.showData(hmCustomer.get("CLIENT_INDUSTRY"),"") %>
							<br/><%=uF.showData((String)request.getAttribute("clientPoc"),"") %>	
						</span>
					</div>
				</div>
	            
				<div style="float:left;width:25%">
					<div style="float:left;width:100%; padding-left: 10px;">
						<span style="float:left; width:100%;"><strong>Time Status</strong></span>
						<span style="float:left;margin-left: 2px;">
							<span class="anaAttrib1" style="color: <%=uF.showData((String)request.getAttribute("strProDeadlineColor"),"") %>;"><%=uF.showData(strActualTime,"0") %></span>&nbsp;<%=uF.showData(strCalType,"") %>
							spent of <span class="anaAttrib1" style="color: <%=uF.showData((String)request.getAttribute("strProDeadlineColor"),"") %>;"><%=uF.showData(strIdealTime,"0") %></span>&nbsp;<%=uF.showData(strCalType,"") %>
						</span>
					</div>
				</div>
				<div style="float:left;width:42%">
					<div style="float:left;width:100%; padding-left: 10px;">
						<div style="float:left;width:100%;">
							<span style="float:left;"><strong>Start Date</strong><br/>
								<%=uF.showData((String)request.getAttribute("PROJECT_START_DATE"),"-") %>
							</span>
							<span style="float:left; padding-left: 25px;"><strong>Project End Date</strong><br/>
								<%=uF.showData((String)request.getAttribute("PROJECT_END_DATE"),"-") %>
							</span>
						</div>
						<div style="float:left;width:100%;">
							<span style="float:left;"><strong>Type</strong>: <%=uF.showData((String)request.getAttribute("strBillingType"),"") %></span>
						</div>
						<%
							boolean proFreqFlag = (Boolean) request.getAttribute("proFreqFlag");
							if(proFreqFlag) {
						%>
						<div style="float:left;width:100%;">
							<span style="float:left;"><strong>Period</strong>: <%=uF.showData((String)request.getAttribute("strProFreqStartDate"),"") %> - <%=uF.showData((String)request.getAttribute("strProFreqEndDate"),"") %></span>
						</div>
						<%} %>
					</div>
				</div>
			</div>
			
			<div class="nav-tabs-custom" style="float: left; width: 100%; margin-top: 20px;">
	            <ul class="nav nav-tabs">
					<li id="proDetail" class="active"><a>Requests</a></li>
	            </ul>
				<div class="tab-content">
					<div class="active tab-pane" style="max-height: 400px;">
						<% if(strProId != null && alOuter.size()>0) {
							for(int i=0; i<alOuter.size();i++) {
							List<String> innerList = new ArrayList<String>();
							innerList = alOuter.get(i);
						%>
						<div class="custom-legend no-borderleft-for-legend"><!-- <font size="4">Requests</font><br/> -->
							<span style="float:left; width: 85%;"> <%=uF.showData(innerList.get(3),"") %> has sent <%=uF.showData(innerList.get(0),"") %> for <%=uF.showData(innerList.get(1),"")%> <%=uF.showData(innerList.get(2),"") %>
							<% if(!innerList.get(5).equals("") && !innerList.get(6).equals("")){ %>
								with start date: <%= uF.showData(innerList.get(5),"") %> and deadline date: <%= uF.showData(innerList.get(6),"") %>
							<% } else { %>
								with comment: <%= uF.showData(innerList.get(4),"") %>
							<% } %></span>
							<div class="legend-info"><i class="fa fa-check-circle checknew" aria-hidden="true" title="Approve Request" onclick="approveRescheduleReassignRequest(<%=innerList.get(1) %>)"></i></div>
							<div class="legend-info"><i class="fa fa-times-circle cross" aria-hidden="true" title="Deny Request" onclick="denyRescheduleReassignRequest(<%=innerList.get(1) %>)"></i></div>
						</div>
						<% } %>
						<% } else { %>
							<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No request available.</div>
						<% } %>
					</div>
				</div>
			</div>
					
			
			<div class="nav-tabs-custom" style="float: left; width: 100%;">
	            <ul class="nav nav-tabs">
					<li id="proDetail" class="active"><a>Tasks</a></li>
	            </ul>
				<div class="tab-content">
					<div class="active tab-pane" style="max-height: 500px;">
					<% 
					//System.out.println("hmProTasks --->> " + hmProTasks);
					if(hmProTasks != null && hmProTasks.size()>0) { %>
					    <div style="float: left; width: 100%; padding-bottom: 5px;"> 
					    	<span style="float:left;"><a href="javascript:void(0);" onclick="getProjectDetails('PreAddNewProject1', '', 'PDADDTASK');" data-toggle="tab">+ Add New Task</a></span>
					    	<span style="float:right; border: 1px solid lightgrey;">&nbsp;<a id="aView" href="javascript:void(0);" onclick="changeTaskView('AGILE');">Scrum</a> &nbsp;<a id="lView" class="btn btn-primary disabled btn-sm active" href="javascript:void(0)" onclick="changeTaskView('LIST');" title="List View"><i class="fa fa-list"> </i></a></span> 
					    </div>
						
						<div id="listView">
							<table class="table table-bordered" id ="lt1">
								<thead>
									<tr>
										<th>Task Name</th>
										<th>Resource</th>
										<th>Dependency Type</th>
										<th>Dependency on</th>
										<th>Start Date</th>
										<th>Deadline</th>
										<th><%="Est. m- "+estimateLbl %></th>
										<th>Complete</th>
									</tr>
								</thead>
								<tbody>
									<%
									Iterator<String> it = hmProTasks.keySet().iterator();
									while(it.hasNext()) {
										String taskId = it.next();
										List<String> innerList = hmProTasks.get(taskId);
									%>	
										<tr>
											<td><%=uF.showData(innerList.get(6),"") %> <%=uF.showData(innerList.get(3),"") %></td>
											<td><%=innerList.get(7)%></td>
											<td><%=uF.showData(innerList.get(5),"-") %></td>
											<td><%=uF.showData(innerList.get(4),"-") %></td>
											<td><%=uF.showData(innerList.get(8),"") %></td>
											<td><%=uF.showData(innerList.get(9),"") %></td>
											<td><%=uF.showData(innerList.get(10),"") %></td>
											<td>
											<div class="anaAttrib1"><span id="TcompletePercent_<%=taskId%>"><%=innerList.get(15) %>%</span> 
											<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
											<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
							                    <% if(uF.parseToInt(innerList.get(14)) == 0) { %>
							                    <%if(innerList.get(19) != null && innerList.get(19).equals("n")) { %>
							                   		<a href="javascript:void(0)" onclick="updateStatus(<%=taskId %>,<%=strProId %>, 'Tcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
							                    <% } } %>
						                    <% } %>
						                    <% } %>
											</div>
											<div id="Tcomplete_<%=taskId%>" class="outbox">
												<div class="greenbox" style="width: <%=innerList.get(15) %>%;"></div>
											</div>
											</td>
										</tr>
										
										<% 
										if(hmProSubTasks == null) hmProSubTasks = new HashMap<String, List<List<String>>>();
										List<List<String>> proSubTaskList = hmProSubTasks.get(taskId);
											for(int j=0; proSubTaskList != null && j<proSubTaskList.size(); j++) {
												List<String> alInner = proSubTaskList.get(j);
										%>
											<tr>
												<td><%=uF.showData(alInner.get(6),"") %> <%=uF.showData(alInner.get(3),"") %></td>
												<td><%=alInner.get(7)%></td>
												<td><%=uF.showData(alInner.get(5),"-") %></td>
												<td><%=uF.showData(alInner.get(4),"-") %></td>
												<td><%=uF.showData(alInner.get(8),"") %></td>
												<td><%=uF.showData(alInner.get(9),"") %></td>
												<td><%=uF.showData(alInner.get(10),"") %></td>
												<td>
												<div class="anaAttrib1"> <span id="STcompletePercent_<%=innerList.get(0)%>"><%=innerList.get(15) %>% </span> 
												<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
												<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
													<%if(innerList.get(19) != null && innerList.get(19).equals("n")) { %>
								                   		<a href="javascript:void(0)" onclick="updateStatus(<%=innerList.get(0)%>,<%=strProId %>, 'STcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
								                    <% } %>
							                    <% } %>
							                    <% } %>
												</div>
												<div id="STcomplete_<%=innerList.get(0)%>" class="outbox">
													<div class="greenbox" style="width: <%=innerList.get(15) %>%;"></div>
												</div>
												</td>
											</tr>
										<% } %>
								    <% } %>
							    </tbody>
						    </table>
					    </div>
					    
					    <div id="agileView" style="display: none;">
					    	<div class="col-lg-3 col-md-6 col-sm-12">
					    		<div style="padding-left: 10px !important; font-weight: bold; border-left: 2px solid lightgray; margin-bottom: 5px;">Requests</div>
					    		<div>
					    		<%
									Iterator<String> it1 = hmProTasks.keySet().iterator();
									while(it1.hasNext()) {
										String taskId = it1.next();
										List<String> innerList = hmProTasks.get(taskId);
										if(innerList != null && innerList.get(19).equals("n") && innerList.get(24).equals("0") && uF.parseToInt(innerList.get(15))==0) {
									%>	
											<div style="padding: 5px 5px; border: 1px solid lightgrey; border-radius: 3px; margin-bottom: 7px; border-top: 4px solid <%=innerList.get(25) %>;">
												<div><%=uF.showData(innerList.get(6), "") %> <b><%=uF.showData(innerList.get(3), "") %></b></div>
												<div><%=innerList.get(7) %></div>
												<div><%=uF.showData(innerList.get(8), "") %> <b>to</b> <%=uF.showData(innerList.get(9), "") %></div>
												<div><span><b><%="Est. m- "+estimateLbl %>:</b> <%=uF.showData(innerList.get(10), "") %></span>
													<span>
														<div class="anaAttrib1"><span id="AgileTcompletePercent_<%=taskId%>"><%=innerList.get(15) %>%</span> 
															<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
															<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
											                    <% if(uF.parseToInt(innerList.get(14)) == 0) { %>
											                    <%if(innerList.get(19) != null && innerList.get(19).equals("n")) { %>
											                   		<a href="javascript:void(0)" onclick="updateStatus(<%=taskId%>,<%=strProId %>, 'AgileTcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
											                    <% } } %>
										                    <% } %>
										                    <% } %>
														</div>
														<div id="AgileTcomplete_<%=taskId %>" class="outbox">
															<div class="greenbox" style="width: <%=innerList.get(15) %>%;"></div>
														</div>
													</span>
												</div>
											</div>
										<% } %>
								    <% } %>
					    		</div>
					    	</div>
					    	<div class="col-lg-3 col-md-6 col-sm-12">
					    		<div style="padding-left: 10px !important; font-weight: bold; border-left: 2px solid lightgray; margin-bottom: 5px;">New</div>
					    		<div>
					    		<%
									Iterator<String> it2 = hmProTasks.keySet().iterator();
									while(it2.hasNext()) {
										String taskId = it2.next();
										List<String> innerList = hmProTasks.get(taskId);
										if(innerList != null && innerList.get(19).equals("n") && innerList.get(24).equals("1") && uF.parseToInt(innerList.get(15))==0) {
									%>	
											<div style="padding: 5px 5px; border: 1px solid lightgrey; border-radius: 3px; margin-bottom: 7px; border-top: 4px solid <%=innerList.get(25) %>;">
												<div><%=uF.showData(innerList.get(6), "") %> <b><%=uF.showData(innerList.get(3), "") %></b></div>
												<div><%=innerList.get(7) %></div>
												<div><%=uF.showData(innerList.get(8), "") %> <b>to</b> <%=uF.showData(innerList.get(9), "") %></div>
												<div><span><b><%="Est. m- "+estimateLbl %>:</b> <%=uF.showData(innerList.get(10), "") %></span>
													<span>
														<div class="anaAttrib1"><span id="AgileTcompletePercent_<%=taskId%>"><%=innerList.get(15) %>%</span> 
															<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
															<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
											                    <% if(uF.parseToInt(innerList.get(14)) == 0) { %>
											                    <%if(innerList.get(19) != null && innerList.get(19).equals("n")) { %>
											                   		<a href="javascript:void(0)" onclick="updateStatus(<%=taskId%>,<%=strProId %>, 'AgileTcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
											                    <% } } %>
										                    <% } %>
										                    <% } %>
														</div>
														<div id="AgileTcomplete_<%=taskId %>" class="outbox">
															<div class="greenbox" style="width: <%=innerList.get(15) %>%;"></div>
														</div>
													</span>
												</div>
											</div>
										<% } %>
								    <% } %>
					    		</div>
					    	</div>
					    	<div class="col-lg-3 col-md-6 col-sm-12">
					    		<div style="padding-left: 10px !important; font-weight: bold; border-left: 2px solid lightgray; margin-bottom: 5px;">In Progress</div>
					    		<div>
					    		<%
									Iterator<String> it3 = hmProTasks.keySet().iterator();
									while(it3.hasNext()) {
										String taskId = it3.next();
										List<String> innerList = hmProTasks.get(taskId);
										if(innerList != null && innerList.get(19).equals("n") && innerList.get(24).equals("1") && uF.parseToInt(innerList.get(15))>0) {
									%>	
											<div style="padding: 5px 5px; border: 1px solid lightgrey; border-radius: 3px; margin-bottom: 7px; border-top: 4px solid <%=innerList.get(25) %>;">
												<div><%=uF.showData(innerList.get(6), "") %> <b><%=uF.showData(innerList.get(3), "") %></b></div>
												<div><%=innerList.get(7) %></div>
												<div><%=uF.showData(innerList.get(8), "") %> <b>to</b> <%=uF.showData(innerList.get(9), "") %></div>
												<div><span><b><%="Est. m- "+estimateLbl %>:</b> <%=uF.showData(innerList.get(10), "") %></span>
													<span>
														<div class="anaAttrib1"><span id="AgileTcompletePercent_<%=taskId%>"><%=innerList.get(15) %>%</span> 
															<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
															<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
											                    <% if(uF.parseToInt(innerList.get(14)) == 0) { %>
											                    <%if(innerList.get(19) != null && innerList.get(19).equals("n")) { %>
											                   		<a href="javascript:void(0)" onclick="updateStatus(<%=taskId%>,<%=strProId %>, 'AgileTcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
											                    <% } } %>
										                    <% } %>
										                    <% } %>
														</div>
														<div id="AgileTcomplete_<%=taskId %>" class="outbox">
															<div class="greenbox" style="width: <%=innerList.get(15) %>%;"></div>
														</div>
													</span>
												</div>
											</div>
										<% } %>
								    <% } %>
					    		</div>
					    	</div>
					    	<div class="col-lg-3 col-md-6 col-sm-12">
					    		<div style="padding-left: 10px !important; font-weight: bold; border-left: 2px solid lightgray; margin-bottom: 5px;">Complete</div>
					    		<div>
					    		<%
									Iterator<String> it4 = hmProTasks.keySet().iterator();
									while(it4.hasNext()) {
										String taskId = it4.next();
										List<String> innerList = hmProTasks.get(taskId);
										if(innerList != null && innerList.get(19).equals("approved")) {
									%>	
											<div style="padding: 5px 5px; border: 1px solid lightgrey; border-radius: 3px; margin-bottom: 7px; border-top: 4px solid <%=innerList.get(25) %>;">
												<div><%=uF.showData(innerList.get(6), "") %> <b><%=uF.showData(innerList.get(3), "") %></b></div>
												<div><%=innerList.get(7) %></div>
												<div><%=uF.showData(innerList.get(8), "") %> <b>to</b> <%=uF.showData(innerList.get(9), "") %></div>
												<div><span><b><%="Est. m- "+estimateLbl %>:</b> <%=uF.showData(innerList.get(10), "") %></span>
													<span>
														<div class="anaAttrib1"><span id="AgileTcompletePercent_<%=taskId%>"><%=innerList.get(15) %>%</span> 
														</div>
														<div id="AgileTcomplete_<%=taskId %>" class="outbox">
															<div class="greenbox" style="width: <%=innerList.get(15) %>%;"></div>
														</div>
													</span>
												</div>
											</div>
										<% } %>
								    <% } %>
					    		</div>
					    	</div>
					    </div>
						<% } else { %>
							<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No task data available.</div>
						<% } %>
					</div>
				</div>
			</div>
             <!-- </div> --><!-- /.post -->
		<% } else { %>
	    	<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No project data available.</div>       
        <% } %> 
        
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="overflow-y:auto; padding-left: 25px;height:400px">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>      
<script type="text/javascript">

	function actionProjectCompleted(pageType, proIds) {
		if(confirm('Are you sure, you wish to complete this project?')) {
			var proStatus = "";
			var strClient = "";
			if(document.getElementById("proStatus")) {
				proStatus = document.getElementById("proStatus").value;
			}
			var assignedBy = document.getElementById("assignedBy").value;
			var recurrOrMiles = document.getElementById("recurrOrMiles").value;
			var strSBU = getSelectedValue("f_sbu");
			var strService = getSelectedValue("f_service");
			var strProjectId = getSelectedValue("pro_id");
			var strManagerId = getSelectedValue("managerId");
			if(document.getElementById("client")) {
				strClient = getSelectedValue("client");
			}
			//alert("strClient ===>> " + strClient);
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AllProjectNameList.action?proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
					+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
					+'&approvePr='+proIds+'&approve=approve&pageType='+pageType+'&proType=L',
				success: function(result){
					$("#subDivResult").html(result);
		   		}
			});
		}
	}
	
	
	function actionProjectBlocked(pageType, proIds) {
		if(confirm('Are you sure, you wish to block this project?')) {
			var proStatus = "";
			var strClient = "";
			if(document.getElementById("proStatus")) {
				proStatus = document.getElementById("proStatus").value;
			}
			var assignedBy = document.getElementById("assignedBy").value;
			var recurrOrMiles = document.getElementById("recurrOrMiles").value;
			var strSBU = getSelectedValue("f_sbu");
			var strService = getSelectedValue("f_service");
			var strProjectId = getSelectedValue("pro_id");
			var strManagerId = getSelectedValue("managerId");
			if(document.getElementById("client")) {
				strClient = getSelectedValue("client");
			}
			//alert("strClient ===>> " + strClient);
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AllProjectNameList.action?proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
					+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
					+'&approvePr='+proIds+'&blocked=blocked&pageType='+pageType+'&proType=L',
				success: function(result){
					$("#subDivResult").html(result);
		   		}
			});
		}
	}

	
	function actionProjectWorking(pageType, proIds) {
		if(confirm('Are you sure, you wish to move this project into working?')) {
			var proStatus = "";
			var strClient = "";
			if(document.getElementById("proStatus")) {
				proStatus = document.getElementById("proStatus").value;
			}
			var assignedBy = document.getElementById("assignedBy").value;
			var recurrOrMiles = document.getElementById("recurrOrMiles").value;
			var strSBU = getSelectedValue("f_sbu");
			var strService = getSelectedValue("f_service");
			var strProjectId = getSelectedValue("pro_id");
			var strManagerId = getSelectedValue("managerId");
			if(document.getElementById("client")) {
				strClient = getSelectedValue("client");
			}
			//alert("strClient ===>> " + strClient);
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AllProjectNameList.action?proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
					+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
					+'&approvePr='+proIds+'&working=working&pageType='+pageType+'&proType=L',
				success: function(result){
					$("#subDivResult").html(result);
		   		}
			});
		}
	}
	
	
	function deleteProject(pageType, proIds) {
		if(confirm('Are you sure, you wish to delete this project?')) {
			var proStatus = "";
			var strClient = "";
			if(document.getElementById("proStatus")) {
				proStatus = document.getElementById("proStatus").value;
			}
			var assignedBy = document.getElementById("assignedBy").value;
			var recurrOrMiles = document.getElementById("recurrOrMiles").value;
			var strSBU = getSelectedValue("f_sbu");
			var strService = getSelectedValue("f_service");
			var strProjectId = getSelectedValue("pro_id");
			var strManagerId = getSelectedValue("managerId");
			if(document.getElementById("client")) {
				strClient = getSelectedValue("client");
			}
			//alert("strClient ===>> " + strClient);
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AllProjectNameList.action?proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
					+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
					+'&operation=D&ID='+proIds+'&pageType='+pageType+'&proType=L',
				success: function(result){
					$("#subDivResult").html(result);
		   		}
			});
		}
	}
	
function approveRescheduleReassignRequest(TaskId){
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Accept Request');
	 $.ajax({
		url : 'AcceptRescheduleReassign.action?&TaskId='+TaskId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
	
}
function denyRescheduleReassignRequest(TaskId){
	 $.ajax({
		url : 'DeclineRescheduleReassign.action?&TaskId='+TaskId,
		cache : false,
		success: function(result){
			$("#subDivResult").html(result);
   		}
	}); 
	
}
	
	
</script>