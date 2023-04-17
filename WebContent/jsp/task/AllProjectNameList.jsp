<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.task.FillTaskEmpList"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

	<script type="text/javascript">
	
	$(document).ready(function() {
		$("body").on('click','#closeButton',function() {
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
	    });
		$("body").on('click','.close',function() {
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
		});
	});
	
	</script>


	<%

		Map<String, String> hmProCompPercent = (Map<String, String>)request.getAttribute("hmProCompPercent");
		if(hmProCompPercent == null) hmProCompPercent = new HashMap<String, String>();
		
		Map hmProject = (java.util.Map)request.getAttribute("hmProject");
		if(hmProject == null) hmProject = new HashMap();

		UtilityFunctions uF = new UtilityFunctions();
	
		String proId = (String)request.getAttribute("proId");
		String proType = (String)request.getAttribute("proType");
		//System.out.println("proType --->> " + proType);
		String pageType = (String)request.getAttribute("pageType");
		String step = (String)request.getAttribute("step");
		
		String proPage = (String)request.getAttribute("proPage");
		String strMinLimit1 = (String)request.getAttribute("minLimit");
		String operation = (String)request.getAttribute("operation");
		String singleProData = (String)request.getAttribute("singleProData");
		
		String proCount = (String)request.getAttribute("proCount");
		
		String strTitle = (String)request.getAttribute(IConstants.TITLE); 
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		String strEmpId = (String)session.getAttribute(IConstants.EMPID);
	%>
	
	
		<div class="col-md-12" style="margin-bottom: 5px;">
			<div class="box box-body col-lg-12 col-md-12 no-padding" style="margin-bottom: 5px;">
				<ul class="nav nav-tabs">
					<li class="<%=((proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) ? "active" : "") %>"><a style="padding: 5px 10px;" href="javascript:void(0)" onclick="getAllProjectNameList('AllProjectNameList', 'L');" data-toggle="tab">Working (<%=(String)request.getAttribute("wProCnt") %>)</a></li>
					<li class="<%=((proType != null && proType.equalsIgnoreCase("C")) ? "active" : "") %>"><a style="padding: 5px 10px;" href="javascript:void(0)" onclick="getAllProjectNameList('AllProjectNameList', 'C');" data-toggle="tab">Completed (<%=(String)request.getAttribute("cProCnt") %>)</a></li>
					<li class="<%=((proType != null && proType.equalsIgnoreCase("B")) ? "active" : "") %>"><a style="padding: 5px 10px;" href="javascript:void(0)" onclick="getAllProjectNameList('AllProjectNameList', 'B');" data-toggle="tab">Blocked (<%=(String)request.getAttribute("bProCnt") %>)</a></li>
					<li class="<%=((proType != null && proType.equalsIgnoreCase("P")) ? "active" : "") %>"><a style="padding: 5px 10px;" href="javascript:void(0)" onclick="getAllProjectNameList('AllProjectNameList', 'P');" data-toggle="tab">Pipelined (<%=(String)request.getAttribute("pProCnt") %>)</a></li>
					
					<%
						String sbData = (String) request.getAttribute("sbData");
						String strSearchJob = (String) request.getAttribute("strSearchJob");
					%>
					<span style="margin-left: 10px;padding-top: 2px; float: left;">
						<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search Projects" value="<%=uF.showData(strSearchJob, "") %>"/>
						<input type="button" value="Search" class="btn btn-primary" style="padding: 2px 5px;" onclick="getSearchProjectNameList();">
					</span>
				       
					<script>
				       $(function(){
				    	   $("#strSearchJob" ).autocomplete({
								source: [ <%=uF.showData(sbData, "") %> ]
							});
				       });
				  	</script>
					
					<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
					<div style="float:right; margin-right:1%; padding-top: 2px;">
						<span id="uncompletedSpan">
						<input type="button" name="approve" class="btn btn-primary disabled" style="padding: 2px 5px;" value="Mark as Completed" />
						</span>
						<span id="completedSpan" style="display: none;">
						<input type="button" name="approve" class="btn btn-primary" style="padding: 2px 5px;" value="Mark as Completed" onclick="approveProjectsAndTasks('<%=pageType %>');"/>
						</span>
					</div>
					
					<div style="float:right; margin-right: 1%; padding-top: 2px;">
						<span id="unblockedSpan">
						<input type="button" name="blocked" class="btn btn-primary disabled" style="padding: 2px 5px;" value="Mark as Blocked" />
						</span>
						<span id="blockedSpan" style="display: none;">
						<input type="button" name="blocked" class="btn btn-primary" style="padding: 2px 5px;" value="Mark as Blocked" onclick="blockedProjects('<%=pageType %>')"/>
						</span>
					</div>
				<% } %>
			 	<div style="float:right; margin-right:1%; padding-top: 2px;">
					<input type="button" name="importProject" class="btn btn-primary" style="padding: 2px 5px;" value="Import Project" onclick="importProjects('<%=pageType %>')"/>
				</div>
			
				<div style="float:right; margin-right:1%; padding-top: 2px;">
					<input type="button" name="newProject" class="btn btn-primary" style="padding: 2px 5px;" value="Add New Project" onclick="addNewProject('L');"/>
					<%-- <input type="button" name="newProject" class="btn btn-primary" style="padding: 2px 5px;" value="Add New Project" onclick="window.location.href='ViewAllProjects.action?singleProData=YES&proType=<%=proType %>&pageType=<%=pageType %>&step=0&proPage=<%=proPage %>&minLimit=<%=strMinLimit1 %>'"/> --%>
				</div>
				<% if(strUserType != null && !strUserType.equals(IConstants.RECRUITER) && proType!=null && proType.equals("P")) { %>
					<div style="float:right; margin-right:1%; padding-top: 2px;">
						<input type="button" name="newProjectPipeline" class="btn btn-primary" style="padding: 2px 5px;" value="Create New Project Pipeline" onclick="addNewProject('P');"/>
					</div>
				<% } %>	  	
				  	
				</ul>
			</div>
			
		</div>
		
	<div class="col-lg-2 col-md-6 col-sm-12">
	
		<div class="nav-tabs-custom">
			<%-- <ul class="nav nav-tabs">
				<li class="<%=((proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="getAllProjectNameList('AllProjectNameList', 'L');" data-toggle="tab">Working (<%=(String)request.getAttribute("wProCnt") %>)</a></li>
				<li class="<%=((proType != null && proType.equalsIgnoreCase("C")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="getAllProjectNameList('AllProjectNameList', 'C');" data-toggle="tab">Completed (<%=(String)request.getAttribute("cProCnt") %>)</a></li>
				<li class="<%=((proType != null && proType.equalsIgnoreCase("B")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="getAllProjectNameList('AllProjectNameList', 'B');" data-toggle="tab">Blocked (<%=(String)request.getAttribute("bProCnt") %>)</a></li>
				<li class="<%=((proType != null && proType.equalsIgnoreCase("P")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="getAllProjectNameList('AllProjectNameList', 'P');" data-toggle="tab">Pipelined (<%=(String)request.getAttribute("pProCnt") %>)</a></li>
			</ul> --%>
			
			<%-- <%
			String sbData = (String) request.getAttribute("sbData");
			String strSearchJob = (String) request.getAttribute("strSearchJob");
			%>
			<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search Projects" value="<%=uF.showData(strSearchJob, "") %>"/>
			<input type="button" value="Search" class="btn btn-primary" onclick="getSearchProjectNameList();">
			<script>
		       $(function(){
		    	   $("#strSearchJob" ).autocomplete({
						source: [ <%=uF.showData(sbData, "") %> ]
					});
		       });
				
		  	</script> --%>
		  	
			<div class="tab-content box-body" style="padding: 5px;">
				<div class="active tab-pane">
				<s:hidden name="proType" id="proType" />
				<s:hidden name="submitType" id="submitType" />
					<ul class="products-list product-list-in-box">
	                  
					<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
						<%if(hmProject != null && hmProject.size()>0) { %>
							<li class="item" style="padding-top: 0px;">
								<input type="checkbox" name="allLivePr" id="allLivePr" style="margin-top: 0px;" onclick="checkUncheckAllProject(this.value);" /> Select All
							</li>
						<% } %>
					<% } %>
	                  
	                  <%
						Set setProjectMap = hmProject.keySet();
						Iterator it = setProjectMap.iterator();
						
						String projectId = null;
						if(uF.parseToInt(proId)>0) {
							projectId = proId;
						}
						while(it.hasNext()) {
							String strProjectId = (String)it.next();
							if(projectId == null) {
								projectId = strProjectId;
							}
							List alProjects = (List)hmProject.get(strProjectId);
							if(alProjects == null) alProjects = new ArrayList();
							double proCompletePecent = 0;
							//if(uF.parseToDouble(hmProTaskCount.get(strProjectId)) > 0) {
								proCompletePecent = uF.parseToDouble(hmProCompPercent.get(strProjectId));
							//}
							if(proCompletePecent < 0) {
								proCompletePecent = 0;
							}

							String[] strTakenTime1 = alProjects.get(7).toString().split("::::");
			                %>

		                    <li class="item">
		                    <input type="hidden" name="proStartDate_<%=strProjectId %>" id="proStartDate_<%=strProjectId %>" value="<%=alProjects.get(13) %>"/>
							<input type="hidden" name="proEndDate_<%=strProjectId %>" id="proEndDate_<%=strProjectId %>" value="<%=alProjects.get(14) %>"/>
		                    
							<div class="product-img" style="margin-top: 3px;">
								<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
									<span id="proCheckboxDiv" style="float: left; margin-right: 5px;">
										<input type="checkbox" value="<%=strProjectId%>" name="approvePr" id="approvePr" style="margin-top: 0px;" onclick="checkAllProjectCheckedUnchecked();"/>
									</span>
								<% } %>
									<!-- <img alt="Product Image" src="dist/img/default-50x50.gif"> -->
								<span style="float: left; width: 17px; height: 17px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 14px;" id="proLogoDiv">
				                    <% String proLogo = "";
				                    	if(alProjects.get(1) != null && alProjects.get(1).toString().length()>0) {
											proLogo = alProjects.get(1).toString().substring(0, 1);
					                    }
				                   	%>
				                   	<span><%=proLogo %></span>
								</span>
							</div>
							
		                    <div class="product-info" style="margin-left: 2px; float: left; width: 87%;">
			                    	
		                    	<div style="float: left; padding-left:2px; width: 100%; border-left: 3px solid <%=(String)alProjects.get(15) %>;">
				                    <div style="float: left; width: 100%;">
				                    	<span style="float: left; width: 100%; <%=(proCompletePecent == 0 && uF.parseToDouble(strTakenTime1[0]) == 0) ? "background-image: url(&quot;images1/icons/new2.gif&quot;);":""%> background-repeat: no-repeat; background-position: right top;">
				                    		<%-- <a href="ViewAllProjects.action?singleProData=YES&proId=<%=strProjectId %>&proType=<%=proType %>&pageType=<%=pageType %>&proPage=<%=proPage %>&minLimit=<%=strMinLimit1 %>"><%=alProjects.get(1) %></a> --%>
				                    		<a id="<%=strProjectId %>" href="javascript:void(0);" <% if(uF.parseToInt(projectId) == uF.parseToInt(strProjectId)) { %> class="users-list-name activelink" <% } else {%> class="users-list-name" <% } %> onclick="getProjectDetails('ProjectDetail', '<%=strProjectId %>', 'BYPROJECT')" title="<%=alProjects.get(1) %>"><%=alProjects.get(1) %></a>
				                    	</span>
										<span style="float: left; width: 100%;"><%=alProjects.get(10) %> </span>
									</div>
			                    </div>
							</div>
		                      
	                    </li><!-- /.item -->
                    <% } %>
                    
                    <%if(hmProject == null || hmProject.size() == 0) { %>
                    	<li class="item">
							<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No projects available.</div>
						</li>
					<% } %>
					<span><input type="hidden" name="projectId" id="projectId" value="<%=projectId %>"/></span>
	                  </ul>
	                  
	                  <div class="box-footer text-center">
	                  <div style="text-align: center; float: left; width: 100%;">
						<% int intproCnt = uF.parseToInt(proCount);
							int pageCnt = 0;
							int minLimit = 0;
							
							for(int i=1; i<=intproCnt; i++) {
									minLimit = pageCnt * 10;
									pageCnt++;
						%>
						<% if(i ==1) {
							String strPgCnt = (String)request.getAttribute("proPage");
							String strMinLimit = (String)request.getAttribute("minLimit");
							if(uF.parseToInt(strPgCnt) > 1) {
								 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
								 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
							}
							if(strMinLimit == null) {
								strMinLimit = "0";
							}
							if(strPgCnt == null) {
								strPgCnt = "1";
							}
						%>
							<span style="color: lightgray;">
							<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
								<a href="javascript:void(0);" onclick="loadMoreProjects('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');">
								<%="< Prev" %></a>
							<% } else { %>
								<b><%="< Prev" %></b>
							<% } %>
							</span>
							<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
							<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
							style="color: black;"
							<% } %>
							><%=pageCnt %></a></span>
							
							<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
								<b>...</b>
							<% } %>
						
						<% } %>
						
						<% if(i > 1 && i < intproCnt) { %>
						<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
							<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
							<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
							style="color: black;"
							<% } %>
							><%=pageCnt %></a></span>
						<% } %>
						<% } %>
						
						<% if(i == intproCnt && intproCnt > 1) {
							String strPgCnt = (String)request.getAttribute("proPage");
							String strMinLimit = (String)request.getAttribute("minLimit");
							 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
							 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
							 if(strMinLimit == null) {
								strMinLimit = "0";
							}
							if(strPgCnt == null) {
								strPgCnt = "1";
							}
							%>
							<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
								<b>...</b>
							<% } %>
						
							<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
							<%-- <a href="ViewAllProjects.action?proPage=<%=pageCnt %>&minLimit=<%=minLimit %>" --%>
							<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
							style="color: black;"
							<% } %>
							><%=pageCnt %></a></span>
							<span style="color: lightgray;">
							<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
								<a href="javascript:void(0);" onclick="loadMoreProjects('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"><%="Next >" %></a>
							<% } else { %>
								<b><%="Next >" %></b>
							<% } %>
							</span>
						<% } %>
						<%} %>
						</div>
						
					</div>
	                
				</div>
			</div>
		</div>
 	</div>
 	
	<div class="col-lg-10 col-md-6 col-sm-12">
		<div class="box box-none">
			<div class="nav-tabs-custom">
	            <ul class="nav nav-tabs">
					<li id="proDetail" class="active" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('ProjectDetail', '', '');" data-toggle="tab">Project Details</a></li>
					<li id="proSnapshot" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('ProjectSnapshot', '', '&pageFrom=Project');" data-toggle="tab">Dashboard</a></li>
					<li id="proStep1" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('PreAddNewProject1', '', '&step=0&operation=E');" data-toggle="tab">Project Info</a></li>
					<li id="proStep2" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('PreAddNewProject1', '', '&step=1&operation=E');" data-toggle="tab">Team Info</a></li>
					<li id="proStep3" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('PreAddNewProject1', '', '&step=2&operation=E');" data-toggle="tab">Task Info</a></li>
					<li id="proStep4" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('PreAddNewProject1', '', '&step=3&operation=E');" data-toggle="tab">Documents</a></li>
					<%-- <li id="proStep5" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('PreAddNewProject1', '', '&step=4&operation=E');" data-toggle="tab">Feeds</a></li> --%>
					<li id="proStep6" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('PreAddNewProject1', '', '&step=4&operation=E');" data-toggle="tab">Project Biling</a></li>
					<li id="proStep7" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('PreAddNewProject1', '', '&step=6&operation=E');" data-toggle="tab">Project Cost</a></li>
					<li id="proStep8" <% if(uF.parseToInt(projectId) == 0) { %> style="display: none;" <% } %>><a href="javascript:void(0)" style="padding: 10px 5px;" onclick="getProjectDetails('PreAddNewProject1', '', '&step=7&operation=E');" data-toggle="tab">Project Summary</a></li>
	            </ul>
				<div class="tab-content">
					<div class="active tab-pane" id="subSubDivResult" style="min-height: 600px;">  <!-- max-height: 600px; overflow-y: auto; -->
						<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No project data available.</div>
					</div>
				</div>
			</div>
		</div>
	</div>
 	
 	
 	<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="overflow-y:auto; padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		<% if(uF.parseToInt(projectId) > 0) { %>
			document.getElementById("proDetail").style.display = 'block';
			document.getElementById("proSnapshot").style.display = 'block';
			var projectId = document.getElementById("projectId").value;
			//var parameters = 'pro_id='+projectId;
			var callFrom = "";
			if(parseInt(projectId) > 0) {
				callFrom = "BYPROJECT";
			}
			getProjectDetails('ProjectDetail', projectId, callFrom);
		<% } %>
	});

	
	function getSearchProjectNameList() {
		/* $(".col-md-9 .nav-tabs-custom .nav-tabs").find("li").removeClass("active");
		$(".col-md-9").find('a:contains(Details)').parent().addClass("active"); */
		//alert("strAction ===>> " + strAction);
		var proType = document.getElementById("proType").value;
		var pageType = document.getElementById("pageType").value;
		var proStatus = "";
		var strClient = "";
		if(document.getElementById("proStatus")) {
			proStatus = document.getElementById("proStatus").value;
		}
		var assignedBy = document.getElementById("assignedBy").value;
		var strSearchJob = document.getElementById("strSearchJob").value;
		var recurrOrMiles = document.getElementById("recurrOrMiles").value;
		var strSBU = getSelectedValue("f_sbu");
		var strService = getSelectedValue("f_service");
		var strProjectId = getSelectedValue("pro_id");
		var strManagerId = getSelectedValue("managerId");
		if(document.getElementById("client")) {
			strClient = getSelectedValue("client");
		}
		//alert("strSBU ===>> " + strSBU);
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		
		$.ajax({
			type : 'POST',
			url: 'AllProjectNameList.action?proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
				+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
				+'&proType='+proType+'&strSearchJob='+strSearchJob+'&pageType='+pageType,
			success: function(result){
				$("#subDivResult").html(result);
	   		}
		});
	}
	
	function getProjectInfo(strAction, projectId, callFrom) {
		document.getElementById("proDetail").className = "";
		document.getElementById("proSnapshot").className = "";
		document.getElementById("proStep1").className = "active";
		document.getElementById("proStep2").className = "";
		document.getElementById("proStep3").className = "";
		document.getElementById("proStep4").className = "";
		document.getElementById("proStep6").className = "";
		document.getElementById("proStep7").className = "";
		document.getElementById("proStep8").className = "";
		
		getProjectDetails(strAction, projectId, callFrom);
	}
	
	
	function getProjectDetails(strAction, projectId, callFrom) {
		var proType = document.getElementById("proType").value;
		//alert("proType ===>> " + proType);
		var parameter = '';
		if(callFrom == 'BYPROJECT') {
			if(projectId == '' && document.getElementById("projectId").value > 0) {
				projectId = document.getElementById("projectId").value;
			} else {
				document.getElementById("projectId").value = projectId;
			}
				document.getElementById("proDetail").style.display = 'block';
				document.getElementById("proSnapshot").style.display = 'block';
				document.getElementById("proStep1").style.display = 'block';
				document.getElementById("proStep2").style.display = 'block';
				if(proType!=null && proType=="P") {
					document.getElementById("proSnapshot").style.display = 'none';
					document.getElementById("proStep3").style.display = 'none';
					document.getElementById("proStep4").style.display = 'none';
					/* document.getElementById("proStep5").style.display = 'none'; */
					document.getElementById("proStep6").style.display = 'none';
					document.getElementById("proStep7").style.display = 'none';
					document.getElementById("proStep8").style.display = 'none';
				} else {
					document.getElementById("proStep3").style.display = 'block';
					document.getElementById("proStep4").style.display = 'block';
					/* document.getElementById("proStep5").style.display = 'block'; */
					document.getElementById("proStep6").style.display = 'block';
					document.getElementById("proStep7").style.display = 'block';
					document.getElementById("proStep8").style.display = 'block';
				}
				document.getElementById("proDetail").className = "active";
				document.getElementById("proSnapshot").className = "";
				document.getElementById("proStep1").className = "";
				document.getElementById("proStep2").className = "";
				document.getElementById("proStep3").className = "";
				document.getElementById("proStep4").className = "";
				document.getElementById("proStep6").className = "";
				document.getElementById("proStep7").className = "";
				document.getElementById("proStep8").className = "";
				
			var submitType = document.getElementById("submitType").value;
			if(submitType != null && submitType == 'DOC') {
				strAction = 'PreAddNewProject1';
				parameter = '&step=4&operation=E';
				document.getElementById("proStep6").className = "active";
				document.getElementById("proDetail").className = "";
				document.getElementById("submitType").value = '';
			} else if(submitType != null && submitType == 'ADDTOPROJECT') {
				strAction = 'PreAddNewProject1';
				parameter = '&step=1&operation=E';
				document.getElementById("proStep2").className = "active";
				document.getElementById("proDetail").className = "";
				document.getElementById("submitType").value = '';
			}
		} else if(callFrom == 'PDADDTASK') {
			parameter = '&step=2&operation=E';
			document.getElementById("proStep3").className = "active";
			document.getElementById("proDetail").className = "";
			document.getElementById("submitType").value = '';
		} else {
			parameter = callFrom;
		}
		projectId = document.getElementById("projectId").value;
		
		//var proType = document.getElementById("proType").value;
		var pageType = document.getElementById("pageType").value;
		
		if(pageType != null && pageType == 'MP') {
			//checkProjectOwnerOrTL(projectId);
		}
		//alert("projectId ===>> " + projectId);
		var form_data = $("#"+this.id).serialize();
		//alert("form_data ===>> " + form_data);
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: strAction+'.action?pro_id='+projectId+'&proType='+proType+parameter+'&pageType='+pageType,
			data: form_data,
			success: function(result) {
				$("#subSubDivResult").html(result);
	   		}
		});
	}

	
	function checkProjectOwnerOrTL(projectId) {
		var xmlhttp;
		if (window.XMLHttpRequest) {
	        // code for IE7+, Firefox, Chrome, Opera, Safari
	        xmlhttp = new XMLHttpRequest();
		}
	    if (window.ActiveXObject) {
	        // code for IE6, IE5
	    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	    }

	    if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
	    } else {
            var xhr = $.ajax({
                url : 'CheckProjectOwnerOrTL.action?projectId='+projectId,
                cache : false,
                success : function(data) {
					if(data.length > 0) {
						if(data.trim() == '2') {
							document.getElementById("proStep6").style.display = 'none';
							document.getElementById("proStep7").style.display = 'none';
							document.getElementById("proStep8").style.display = 'none';
						} else {
							document.getElementById("proStep6").style.display = 'block';
							document.getElementById("proStep7").style.display = 'block';
							document.getElementById("proStep8").style.display = 'block';
						}
					}
                }
            });
	    }
	} 
	
	
	function addNewProject(proType) {
		document.getElementById("proDetail").style.display = 'none';
		document.getElementById("proSnapshot").style.display = 'none';
		document.getElementById("proStep1").style.display = 'block';
		document.getElementById("proStep1").className = "active";
		document.getElementById("proStep2").style.display = 'none';
		document.getElementById("proStep3").style.display = 'none';
		document.getElementById("proStep4").style.display = 'none';
		/* document.getElementById("proStep5").style.display = 'none'; */
		document.getElementById("proStep6").style.display = 'none';
		document.getElementById("proStep7").style.display = 'none';
		document.getElementById("proStep8").style.display = 'none';
		
		if(document.getElementById("projectId")) {
			var proId = document.getElementById("projectId").value;
			if(document.getElementById(proId)) {
				document.getElementById(proId).className = "";
			}
			document.getElementById("projectId").value = "";
		}
		var pageType = document.getElementById("pageType").value;
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?pageType='+pageType+'&proType='+proType,
			success: function(result) {
				$("#subSubDivResult").html(result);
	   		}
		});
	}

	
	function importProjects(pageType) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Import Projects');
		$.ajax({
			url : 'ImportProjects.action?pageType='+pageType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	
	function updateStatus(taskId, proId, divId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Update Task Status');
		$.ajax({
			url : 'UpdateTaskPercentage.action?taskId='+taskId+'&proId='+proId+'&divId='+divId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function saveStatus(taskId, percent, divId, proId) {
		var xmlhttp;
		if (window.XMLHttpRequest) {
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp = new XMLHttpRequest();
    	}
	    if (window.ActiveXObject) {
	        // code for IE6, IE5
	    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	    }
    
	    if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
	    } else {
            var xhr = $.ajax({
                url : 'UpdateTaskPercentage.action?ID='+taskId+'&percent='+percent,
                cache : false,
                success : function(data) {
                	var allData = data.split("::::");
                	document.getElementById(divId+'Percent_'+taskId).innerHTML = allData[0] +'%';
                	document.getElementById(divId+'_'+taskId).innerHTML = allData[1];
                	//$("#modalInfo").hide();
                }
            });
	    }
	    $("#modalInfo").hide();
	}
	
	
	function GetXmlHttpObject() {
		if (window.XMLHttpRequest) {
			// code for IE7+, Firefox, Chrome, Opera, Safari
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject) {
			// code for IE6, IE5
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
		return null;
	}
	
	
</script> 
