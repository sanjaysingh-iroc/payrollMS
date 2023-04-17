<%-- <%@page import="com.sun.org.apache.bcel.internal.generic.ICONST"%> --%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<style>
	.table-bordered>thead>tr>th, .table-bordered>tbody>tr>th, .table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td, .table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td {
		padding-left: 4px;
	}
</style>

<script>
	var memberId='<%=request.getAttribute("memberId") %>';
	function getScoreDetails(id,empId,levelId,scoreId,appFreqId) {
		var action="AppraisalScore.action?id="+id+"&empid="+empId+"&type=popup&levelid="+levelId+"&scoreid="+scoreId+"&appFreqId="+appFreqId;
		if(memberId!='null'){
			action+="&memberId="+memberId; 
		}
		var dialogEdit = '#scoreDetail';
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : true,
			height : 600,
			width : 850,
			modal : true,
			title : 'See Score',
			open : function() {
				var xhr = $.ajax({
					url : action,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	}
	
	
	function generateReportExcel() {
		window.location = "ExportExcelReport.action";
	}
	
	
	function revokeUserFeedback(id, empid, userId, memberId, appFreqId) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			if(confirm('Are you sure, you want to revoke this feedback?')) {
				var xhr = $.ajax({
					url : "AppraisalScoreStatus.action?id="+id+"&empid="+empid+"&userId="+userId+"&memberId="+memberId
							+"&appFreqId="+appFreqId+"&operation=Revoke",
					cache : false,
					success : function(data) {
						document.getElementById(id+'_'+empid+'_'+userId+'_'+memberId).innerHTML = data;
					}
				});
			}
		}
	}
	
/* ===start parvez date: 06-07-2022=== */	
	function reopenUserFeedback(id, empid, userId, memberId, appFreqId, role) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			if(confirm('Are you sure, you want to reopen this feedback?')) {
				var reopenComment = window.prompt("Please enter feedback reopen comment.");
				if (reopenComment != null) {
					var xhr = $.ajax({
						url : "AppraisalScoreStatus.action?id="+id+"&empid="+empid+"&userId="+userId+"&memberId="+memberId+"&role="+role
							+"&appFreqId="+appFreqId+"&operation=Reopen&reopenComment="+reopenComment,
						cache : false,
						success : function(data) {
							document.getElementById('reopen_'+id+'_'+empid+'_'+userId+'_'+memberId).innerHTML = data;
						}
					});
				}
			}
		}
	}
/* ===end parvez date: 06-07-2022=== */	
	
/* ===start parvez date: 08-07-2022=== */	
	function approveUserFeedback(id, empid, userId, memberId, appFreqId, role) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			if(confirm('Are you sure, you want to reopen this feedback?')) {
				var approveComment = window.prompt("Please enter feedback reopen comment.");
				if (approveComment != null) {
					var xhr = $.ajax({
						url : "AppraisalScoreStatus.action?id="+id+"&empid="+empid+"&userId="+userId+"&memberId="+memberId+"&role="+role
							+"&appFreqId="+appFreqId+"&operation=Approve&approveComment="+approveComment,
						cache : false,
						success : function(data) {
							document.getElementById('approve_'+id+'_'+empid+'_'+userId+'_'+memberId).innerHTML = data;
						}
					});
				}
			}
		}
	}
/* ===end parvez date: 08-07-2022=== */
	
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

<%
	Map<String, List<Map<String,String>>> scoreMp = (Map<String, List<Map<String,String>>>) request.getAttribute("scoreMp");
	UtilityFunctions uF = new UtilityFunctions();
	
	Map<String, String> hmAppLevelName = (Map<String, String>) request.getAttribute("hmAppLevelName");
	Map<String, String> existUserIds = (Map<String, String>) request.getAttribute("existUserIds");
	String appFreqId = (String)request.getAttribute("appFreqId");
	String appId = (String)request.getAttribute("id");
	String empid = (String)request.getAttribute("empid");
	String memberId = (String)request.getAttribute("memberId");
	String fromPage = (String)request.getAttribute("fromPage");
	boolean flag = (Boolean) request.getAttribute("flag");
	
//===start parvez date: 15-07-2022===
	Map<String, String> hmSubmitApprovedStatus = (Map<String, String>) request.getAttribute("hmSubmitApprovedStatus");
	if(hmSubmitApprovedStatus == null) hmSubmitApprovedStatus = new HashMap<String, String>();
//===end parvez date: 15-07-2022===
	
	Map<String, String> hmDepartmentMap = (Map<String, String>) request.getAttribute("hmDepartmentMap");
	if(hmDepartmentMap == null) hmDepartmentMap = new HashMap<String, String>();
	
%>

		<table class="table-bordred" width="100%" style="display:none; padding-left: 4px;">
			<tr>
				<th width="20%">Level</th>
				<th>Scorecard</th>
				<th>Total</th>
				<th>Rating</th>
			</tr>
			<%
				double marksTotal = 0;
				double outOfmarksTotal = 0;
				Iterator it = hmAppLevelName.keySet().iterator();
				int j=0;
				while (it.hasNext()) {
					String key = (String) it.next();
					List<Map<String,String>> outerList = scoreMp.get(key);%>
					<tr><td width="20%" colspan="4"><%=hmAppLevelName.get(key)%></td></tr>
					<%for (int i=0; outerList != null && i < outerList.size(); i++) {
						Map<String,String> innerMap =outerList.get(i);
						if(innerMap==null)innerMap=new HashMap<String,String>();
						
						marksTotal+=uF.parseToDouble(innerMap.get("MARKS"));
						outOfmarksTotal+=uF.parseToDouble(innerMap.get("WEIGHTAGE"));
				%>
						<tr>
							<td>&nbsp;</td>
							<td><%=innerMap.get("SCORECARD")%></td>
							<td align="center"><a onclick="getScoreDetails('<s:property value="id"/>','<s:property value="empid"/>','<%=innerMap.get("LEVEL_ID") %>','<%=innerMap.get("SCORE_ID") %>','<%=appFreqId%>')"
								href="javascript:void(0)"><%=uF.showData(innerMap.get("MARKS"),"0")%>/<%=uF.showData(innerMap.get("WEIGHTAGE"),"0")%></a>
							</td>
							<td align="center">
								<div id="starPrimary<%=j%>"></div> 
								<script type="text/javascript">
									$(function(){
										$('#starPrimary'+ '<%=j%>').raty({
											readOnly : true,
											start : <%=uF.showData(innerMap.get("AVERAGE"),"0")%>,
											half : true,
											targetType : 'number'
										});
									});
								</script>
							</td>
						</tr>
					<%
							j++;
						}
					}
			%>
			<tr>
				<td width="20%">&nbsp;</td>
				<td align="right"><b>Total</b></td>
				<td align="center"><b><%=marksTotal%>/<%=outOfmarksTotal%></b></td>
				<td align="center">
				<div id="starPrimary"></div> 
					<script type="text/javascript">
						$(function(){
							$('#starPrimary').raty({
								readOnly : true,
								start : <%=(marksTotal*100)/(outOfmarksTotal*20)%>,
								half : true,
								targetType : 'number'
							});
						});
					</script>
				</td>
			</tr>		
		</table>
		
		<%
		Map<String, String> hmorientationMembers = (Map) request.getAttribute("hmorientationMembers");
		Map<String, String> hmAnswerType = (Map<String, String>) request.getAttribute("hmAnswerType");
		
		Map hmScoreQuestionsMap = (Map) request.getAttribute("hmScoreQuestionsMap");
		Map hmOtherQuestionsMap = (Map) request.getAttribute("hmOtherQuestionsMap");
		Map hmGoalTargetKraQuestionsMap = (Map) request.getAttribute("hmGoalTargetKraQuestionsMap");
		Map hmLevelScoreMap = (Map) request.getAttribute("hmLevelScoreMap");
		
		Map hmLevel = (Map) request.getAttribute("hmLevel");
		Map hmQuestions = (Map) request.getAttribute("hmQuestions");
		Map hmScoreCard = (Map) request.getAttribute("hmScoreCard");
		Map hmOptions = (Map) request.getAttribute("hmOptions");
		//String memberId = (String) request.getAttribute("memberId");
		Map hmQuestionMarks = (Map) request.getAttribute("hmQuestionMarks");
		//System.out.println("hmQuestionMarks=="+hmQuestionMarks);
		Map hmQuestionWeightage = (Map) request.getAttribute("hmQuestionWeightage");
		Map hmQuestionAnswer = (Map) request.getAttribute("hmQuestionAnswer");
		//Map hmQuestionRemak = (Map) request.getAttribute("hmQuestionRemak");
		List alRoles = (List)request.getAttribute("alRoles");
		List rolesUserIds = (List)request.getAttribute("rolesUserIds");
		Map<String, String> useNameMP = (Map<String, String>) request.getAttribute("useNameMP");
		if(useNameMP == null) useNameMP = new HashMap<String, String>();
		Map<String, List<String>> hmOuterpeerAppraisalDetails = (Map<String, List<String>> )request.getAttribute("hmOuterpeerAppraisalDetails");
		Map<String, String> hmOuterpeerAnsDetails = (Map<String, String> )request.getAttribute("hmOuterpeerAnsDetails");
		List<String> sectionIdsList = (List<String>)request.getAttribute("sectionIdsList");
		if(sectionIdsList == null) sectionIdsList = new ArrayList<String>();
		Map<String, List<String>> hmSubsectionIds = (Map<String, List<String>>)request.getAttribute("hmSubsectionIds");
		if(hmSubsectionIds == null) hmSubsectionIds = new LinkedHashMap<String, List<String>>();
		Map<String, String> hmSectionDetails = (Map<String, String>)request.getAttribute("hmSectionDetails");
		if(hmSectionDetails == null) hmSectionDetails = new HashMap<String, String>();
		
		String role = (String) request.getAttribute("role");
		
		Map<String,String> hmSectionComment = (Map<String,String>)request.getAttribute("hmSectionComment");
		if(hmSectionComment == null) hmSectionComment = new HashMap<String, String>();
		Map<String,String> hmStrengthImprovements = (Map<String,String>)request.getAttribute("hmStrengthImprovements");
		if(hmStrengthImprovements == null) hmStrengthImprovements = new HashMap<String, String>();
	
			
		Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
		if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
		
		Map hmReviewerMarksComment = (Map) request.getAttribute("hmReviewerMarksComment");
		if(hmReviewerMarksComment == null) hmReviewerMarksComment = new HashMap();
		
		%>
		<input type="hidden" name ="appFreqId" value="<%=appFreqId%>"/>
		<% if(rolesUserIds != null && !rolesUserIds.isEmpty() && rolesUserIds.size()>0) { 
			for(int m=0; m<rolesUserIds.size(); m++) { %>
				<br/><br/>
				<strong><%=useNameMP.get(rolesUserIds.get(m)) %> 
					<% if(role != null && role.equalsIgnoreCase("Reviewer")) { %>
						(<%="Reviewer" %>)
					<% } else { %>
					<!-- ===start parvez date: 03-04-2023=== -->
						<%-- (<%=hmorientationMembers.get(memberId)%>) --%>
						<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) && hmDepartmentMap!=null && uF.parseToInt(hmDepartmentMap.get(rolesUserIds.get(m))) != uF.parseToInt(hmDepartmentMap.get(empid))){ %>
							(<%=hmorientationMembers.get(memberId)+"-Cross Department"%>)
						<% } else { %>
							(<%=hmorientationMembers.get(memberId)%>)
						<% } %>
					<!-- ===end parvez date: 03-04-2023=== -->	
					<% } %>
				</strong>
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="float: right;"><i class="fa fa-file-excel-o"></i></a>
			
			<!-- ===start parvez date: 05-08-2022=== -->	
				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(memberId) == 13){ %>
						
					<table class="table-bordered" width="100%"  style="padding-left: 4px;">
						<tr>
							   <th></th>
							    <%for(int r=0; alRoles!=null && r<alRoles.size(); r++) { %>
							    	<th>Marks <br>Role: 
								    	<% if(role != null && role.equalsIgnoreCase("Reviewer")) { %>
											<%="Reviewer" %>
										<% } else { %>
											<%=hmorientationMembers.get((String)alRoles.get(r))%>
										<% } %>
							    	</th>
							    <% } %>
							   <th>Weightage %</th>
						</tr>
						<tr>
				    	<td width="80%"><%-- <strong></strong> --%>
					    	<div style="margin:10px 0;">	<!-- border-top:1px solid #ccc; -->
					    	  	<%for(int r=0; alRoles!=null && r<alRoles.size(); r++) { %>
							    <div style="float: left;width:100%">
							    	<%if(r==0) { %>
									<p style="font-weight:bold">Answer/Comments:</p>
									<% } %>
									<div style="margin-left:20px;font-size:12px; float: left; width: 96%;">
										<%if(hmReviewerMarksComment.containsKey(memberId+"_"+rolesUserIds.get(m))){
											String strAns = ((String)hmReviewerMarksComment.get(memberId+"_"+rolesUserIds.get(m)));
												if(strAns!=null){
													strAns = strAns.replace(":_:", "<br/>");
													out.println(uF.showData(strAns, ""));
												}
											}
										%>
									</div>
								</div>
								<% } %>
							</div>
				    	</td>
				    	<% for(int ii=0; alRoles!=null && ii<alRoles.size(); ii++) { %>
				    	
				    		<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)hmReviewerMarksComment.get(alRoles.get(ii)+"_"+rolesUserIds.get(m)+"_MARKS"), "Not Rated") %></td>
				    		<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)hmReviewerMarksComment.get(alRoles.get(ii)+"_"+rolesUserIds.get(m)+"_WEIGHTAGE"), "") %></td>
				    	<% }  %>
				  
				</table>
						
				<% } else { %>
			
			<%
			//System.out.println("AScS/336---sectionIdsList=="+sectionIdsList);
				for(int a=0; sectionIdsList != null && !sectionIdsList.isEmpty() && a<sectionIdsList.size(); a++) {
					List<String> alLevelScore = hmSubsectionIds.get(sectionIdsList.get(a)+"SCR");
					int cnt=0;
					%>
					<h4><%=a+1 %>)&nbsp;<%=uF.showData((String)hmSectionDetails.get(sectionIdsList.get(a)), "") %></h4>
					<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc"><%=uF.showData(hmSectionDetails.get(sectionIdsList.get(a)+"_SD"), "")%></div>
					<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc"><%=uF.showData(hmSectionDetails.get(sectionIdsList.get(a)+"_LD"), "")%></div>
					
					
					<%for(int i=0; alLevelScore!= null && i<alLevelScore.size(); i++){
						cnt++;
						List alScore = (List)hmLevelScoreMap.get((String)alLevelScore.get(i));
						if(alScore!=null && !alScore.isEmpty()) {
						%>
							<h4><%=a+1 %>.<%=cnt %>)&nbsp;<%=uF.showData((String)hmLevel.get((String)alLevelScore.get(i)), "") %></h4>
							<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc"><%=uF.showData((String)hmLevel.get((String)alLevelScore.get(i)+"_SD"), "")%></div>
							<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc"><%=uF.showData((String)hmLevel.get((String)alLevelScore.get(i)+"_LD"), "")%></div>
							
							<table class="table-bordered" width="100%" style="padding-left: 4px;">
				 			 <tr>
				   			    <th>Competencies</th>
							    <th>Question</th>
							    <%for(int r=0; alRoles!=null && r<alRoles.size(); r++) { %>
						    		<th>Marks <br>Role: 
							    		<% if(role != null && role.equalsIgnoreCase("Reviewer")) { %>
											<%="Reviewer" %>
										<% } else { %>
											<%=hmorientationMembers.get((String)alRoles.get(r))%>
										<% } %>
						    		</th>
							    <% } %>
							    
							    <th>Weightage %</th>
							  </tr>
							  <%
							  	for(int s=0; alScore!=null && s<alScore.size(); s++) { 
							  	List alQuestions = (List)hmScoreQuestionsMap.get(alScore.get(s));
							  	
							  	for(int q=0; alQuestions!=null && q<alQuestions.size(); q++) {
							  		List alOptions = (List)hmOptions.get((String)alQuestions.get(q));
							  %>
							  	<tr>
							    	<td width="20%" valign="top"><%=hmScoreCard.get((String)alScore.get(s))%></td>
							    	<td width="60%" valign="top">
							    	<strong><%=q+1%>)&nbsp;<%=hmQuestions.get((String)alQuestions.get(q))%></strong>
							    	<%	if(alOptions!=null) { 
							    		int nOptionType= uF.parseToInt((String)alOptions.get(4));
							    	%>
							    	<div style="margin-left:20px;font-size:12px">
										<% 
										switch(nOptionType) {
											case 1:
												%>
												<p>a) <%=(String)alOptions.get(0)%></p>
												<p>b) <%=(String)alOptions.get(1)%></p>
												<p>c) <%=(String)alOptions.get(2)%></p>
												<p>d) <%=(String)alOptions.get(3)%></p>
												<%
												break;
											
											case 2:
												%>
												<p>a) <%=(String)alOptions.get(0)%></p>
												<p>b) <%=(String)alOptions.get(1)%></p>
												<p>c) <%=(String)alOptions.get(2)%></p>
												<p>d) <%=(String)alOptions.get(3)%></p>
												<%
												break;
												
											case 3:
												out.println(uF.showData(hmAnswerType.get("3"), ""));
												break;
												
											case 4:
												out.println(uF.showData(hmAnswerType.get("4"), ""));
												break;
												
											case 5:
												%>
												<p>a) Yes</p>
												<p>b) No</p>
												<%	
												break;
												
											case 6:
												%>
												<p>a) True</p>
												<p>b) False</p>
												<%
												break;
												
											case 7:
												out.println(uF.showData(hmAnswerType.get("7"), ""));
												break;
												
											case 8:
												%>
												<p>a) <%=(String)alOptions.get(0)%></p>
												<p>b) <%=(String)alOptions.get(1)%></p>
												<p>c) <%=(String)alOptions.get(2)%></p>
												<p>d) <%=(String)alOptions.get(3)%></p>
												<%
												break;
												
											case 9:
												%>
												<p>a) <%=(String)alOptions.get(0)%></p>
												<p>b) <%=(String)alOptions.get(1)%></p>
												<p>c) <%=(String)alOptions.get(2)%></p>
												<p>d) <%=(String)alOptions.get(3)%></p>
												<%
												break;
												
											case 10:
												out.println(uF.showData(hmAnswerType.get("10"), ""));
												break;
												
											case 11:
												out.println(uF.showData(hmAnswerType.get("11"), ""));
												break;
											case 12:
												out.println(uF.showData(hmAnswerType.get("12"), ""));
												break;
										}
									%>    		
								</div>
								
						    	<div style="border-top:1px solid #ccc;margin:10px 0;">
						    	<% for(int r=0; alRoles!=null && r<alRoles.size(); r++) { %>
						    		<div style="float: left;width:100%">
								    	<%if(r==0) { %>
											<p style="font-weight:bold">Answer/Comments:</p>
										<% } %>
										<%
										if(uF.parseToInt(alRoles.get(0).toString())==4  || uF.parseToInt(alRoles.get(0).toString())==10) {
											String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m));
											%>		
						       				 <div style="margin-left:20px;font-size:12px; float: left; width: 96%; margin-bottom: 5px;">
						       					<% out.println(uF.showData(queAns, ""));%>
											</div>
										<% } else { %>
											<div style="margin-left:20px;font-size:12px; float: left; width: 96%;">
											<%if(hmQuestionAnswer.containsKey((String)alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m))) {
												String strAns = ((String)hmQuestionAnswer.get((String)alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m)));
												if(strAns!=null) {
													strAns = strAns.replace(":_:", "<br/>");
													out.println(uF.showData(strAns, ""));
												}
										 } %>
								     </div>
								  <% } %>
								</div>
							<% } %>
						</div>
				<% } %>
			</td>
				    	
	    	<%for(int rr=0; alRoles!=null && rr<alRoles.size(); rr++) { %>
	    		<%if(uF.parseToInt(alRoles.get(rr).toString())==4 || uF.parseToInt(alRoles.get(rr).toString())==10) {
	    			List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q)+"_"+alRoles.get(rr)+"_"+rolesUserIds.get(m));
	    			if(innList==null)innList=new ArrayList<String>();
	    			if(innList !=null && !innList.isEmpty()) {
	    			%>
	    				<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(0), "Not Rated") %></td>
	    				<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(1), "") %></td>
	    		  <% } else { %>
	    				<td width="10%" valign="top" align="right" style="padding-right:10px">Not Rated</td>
	    				<td width="10%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
	    	     <%
	    			}
	    		} else { %>
    				<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)hmQuestionMarks.get((String)alQuestions.get(q)+"_"+alRoles.get(rr)+"_"+rolesUserIds.get(m)), "Not Rated") %></td>
    				<td width="10%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
	    		<% }
	    		} %>
			</tr>
		 <% }
			} %>
			
			<tr>
			<!-- ===start parvez date: 03-03-2023=== -->	
				<td colspan="<%=alRoles.size()+3%>">
			<!-- ===end parvez date: 03-03-2023=== -->	  	
				  	<div style="float: left;width:100%">
				  		<p style="font-weight:bold">Section Comment:</p>
				  		<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc; padding-bottom: 10px;"> <%=uF.showData((String)hmSectionComment.get(sectionIdsList.get(a)+"_"+memberId+"_"+rolesUserIds.get(m)), "")%></div>
				  	</div>		
				</td>
			</tr>			  		
	</table>
	<% }
	} %>
	 
	<%
		List<String> alLevelOther = hmSubsectionIds.get(sectionIdsList.get(a)+"OTHR"); 
		for(int i=0; alLevelOther != null && i<alLevelOther.size(); i++) {
			cnt++;
			List alQuestions = (List)hmOtherQuestionsMap.get((String)alLevelOther.get(i));
		%>
			<h4><%=a+1 %>.<%=cnt %>)&nbsp;<%=uF.showData((String)hmLevel.get((String)alLevelOther.get(i)), "")%></h4>
			<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc"><%=uF.showData((String)hmLevel.get((String)alLevelOther.get(i)+"_SD"), "")%></div>
			<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc"><%=uF.showData((String)hmLevel.get((String)alLevelOther.get(i)+"_LD"), "")%></div>
		
			<table class="table-bordered" width="100%" style="padding-left: 4px;">
			  <tr>
			    <th>Question</th>
			    <%for(int r=0; alRoles!=null && r<alRoles.size(); r++) { %>
		    		<th>Marks <br>Role: 
			    		<% if(role != null && role.equalsIgnoreCase("Reviewer")) { %>
							<%="Reviewer" %>
						<% } else { %>
							<%=hmorientationMembers.get((String)alRoles.get(r))%>
						<% } %>
		    		</th>
			    <% } %>
			    <th>Weightage %</th>
			  </tr>
			 <%for(int q=0; alQuestions!=null && q<alQuestions.size(); q++) { 
				 List alOptions = (List)hmOptions.get((String)alQuestions.get(q));
			 %>
				  	<tr>
				    	<td width="80%"><strong><%=q+1%>)&nbsp;<%=hmQuestions.get((String)alQuestions.get(q))%></strong>
				    	<div style="margin-left:20px; font-size:12px">
						<%if(alOptions!=null) {
							int nOptionType= uF.parseToInt((String)alOptions.get(4));
							switch(nOptionType) {
								case 1:
									%>
									<p>a) <%=(String)alOptions.get(0)%></p>
									<p>b) <%=(String)alOptions.get(1)%></p>
									<p>c) <%=(String)alOptions.get(2)%></p>
									<p>d) <%=(String)alOptions.get(3)%></p>
									<%
									break;
								
								case 2:
									%>
									<p>a) <%=(String)alOptions.get(0)%></p>
									<p>b) <%=(String)alOptions.get(1)%></p>
									<p>c) <%=(String)alOptions.get(2)%></p>
									<p>d) <%=(String)alOptions.get(3)%></p>
									<%
									break;
									
								case 3:
									out.println(uF.showData(hmAnswerType.get("3"), ""));
									break;
									
								case 4:
									out.println(uF.showData(hmAnswerType.get("4"), ""));
									break;
									
								case 5:
									%>
									<p>a) Yes</p>
									<p>b) No</p>
									<%	
									break;
									
								case 6:
									%>
									<p>a) True</p>
									<p>b) False</p>
									<%
									break;
									
								case 7:
									out.println(uF.showData(hmAnswerType.get("7"), ""));
									break;
									
								case 8:
									%>
									<p>a) <%=(String)alOptions.get(0)%></p>
									<p>b) <%=(String)alOptions.get(1)%></p>
									<p>c) <%=(String)alOptions.get(2)%></p>
									<p>d) <%=(String)alOptions.get(3)%></p>
									<%
									break;
									
								case 9:
									%>
									<p>a) <%=(String)alOptions.get(0)%></p>
									<p>b) <%=(String)alOptions.get(1)%></p>
									<p>c) <%=(String)alOptions.get(2)%></p>
									<p>d) <%=(String)alOptions.get(3)%></p>
									<%
									break;
									
								case 10:
									out.println(uF.showData(hmAnswerType.get("10"), ""));
									break;
									
								case 11:
									out.println(uF.showData(hmAnswerType.get("11"), ""));
									break;
								case 12:
									out.println(uF.showData(hmAnswerType.get("12"), ""));
									break;
							}
						}
							%>    		
						</div>
				    	
				    	<div style="border-top:1px solid #ccc;margin:10px 0;">
				    		<%for(int r=0; alRoles!=null && r<alRoles.size(); r++) { %>
				    	    	<div style="float: left;width:100%">
				    				<%if(r==0) { %>
										<p style="font-weight:bold">Answer/Comments:</p>
									<% } %>
									<% if(uF.parseToInt(alRoles.get(0).toString())==4 || uF.parseToInt(alRoles.get(0).toString())==10) {
										 String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q)+"_"+memberId+"_"+rolesUserIds.get(m));
									
									%>
					       				 <div style="margin-left:20px;font-size:12px; float: left; width: 96%; margin-bottom: 5px;">
											<%out.println(uF.showData(queAns, ""));%>
										</div>
									<% } else { %>
										<div style="margin-left:20px;font-size:12px; float: left; width: 96%;">
											<%if(hmQuestionAnswer.containsKey((String)alQuestions.get(q)+"_"+memberId+"_"+rolesUserIds.get(m))){
												String strAns = ((String)hmQuestionAnswer.get((String)alQuestions.get(q)+"_"+memberId+"_"+rolesUserIds.get(m)));
												if(strAns!=null) {
													strAns = strAns.replace(":_:", "<br/>");
													out.println(uF.showData(strAns, ""));
												}
											}
										%>
									</div>
								<%} %>
							</div>
						<%} %>
					</div>
				</td>
		    	<% for(int ii=0; alRoles!=null && ii<alRoles.size(); ii++) { %>
	    			<%if(uF.parseToInt(alRoles.get(ii).toString())==4  || uF.parseToInt(alRoles.get(ii).toString())==10) {
	    				//System.out.println("alRoles.get(rr) ===>> " + alRoles.get(ii));
		    			//System.out.println("alQuestions.get(q)_alRoles.get(ii)_rolesUserIds.get(m) ===>> " + alQuestions.get(q)+"_"+alRoles.get(ii)+"_"+rolesUserIds.get(m));
	    				List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q)+"_"+alRoles.get(ii)+"_"+rolesUserIds.get(m));
	    				if(innList==null)innList=new ArrayList<String>();
	    				//System.out.println("innList ===>> " + innList);
	    				if(innList !=null && !innList.isEmpty()) {
	    			%>
	    					<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(0), "Not Rated") %></td>
	    					<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(1), "") %></td>
	    			   <% } else { %>
	    					<td width="10%" valign="top" align="right" style="padding-right:10px">Not Rated</td>
	    					<td width="10%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
		    			  <% }
		    			} else { %>
	    					<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)hmQuestionMarks.get((String)alQuestions.get(q)+"_"+alRoles.get(ii)+"_"+rolesUserIds.get(m)), "Not Rated") %></td>
	    					<td width="10%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
		    		<%  }
		    		} %>
		 	 	<% } %>	 	
		 	 	<tr>
				<!-- ===start parvez date: 03-03-2023=== -->	
					<td colspan="<%=alRoles.size()+3%>">
				<!-- ===end parvez date: 03-03-2023=== -->	
						<div style="float: left;width:100%">
							<p style="font-weight:bold">Section Comment:</p>
					  		<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc; padding-bottom: 10px;"> <%=uF.showData((String)hmSectionComment.get(sectionIdsList.get(a)+"_"+memberId+"_"+rolesUserIds.get(m)), "")%></div>
					  	</div>
					</td>
				</tr> 	
			</table>	
		<%}	%>
					
					
				<%
					Map<String, String> hmAppSystemType = (Map<String, String>) request.getAttribute("hmAppSystemType");
					List<String> alLevelGoalTargetKRA = hmSubsectionIds.get(sectionIdsList.get(a)+"GTK"); 
					//System.out.println("alLevelGoalTargetKRA ===> "+alLevelGoalTargetKRA);
					for(int i=0; alLevelGoalTargetKRA != null && i<alLevelGoalTargetKRA.size(); i++) {
						cnt++;
						String appSystemType = hmAppSystemType.get((String)alLevelGoalTargetKRA.get(i));
						//System.out.println("hmGoalTargetKraQuestionsMap ===> "+hmGoalTargetKraQuestionsMap);
						List alQuestions = (List)hmGoalTargetKraQuestionsMap.get((String)alLevelGoalTargetKRA.get(i));
						String strSystemType = "";
						if(appSystemType != null && appSystemType.equals("3")) {
							strSystemType = "Goal";
						} else if(appSystemType != null && appSystemType.equals("4")) {
							strSystemType = "KRA";
						} else if(appSystemType != null && appSystemType.equals("5")) {
							strSystemType = "Target";
						}
					%>
					
				<h4><%=a+1 %>.<%=cnt %>)&nbsp;<%=uF.showData((String)hmLevel.get((String)alLevelGoalTargetKRA.get(i)), "") %></h4>
				<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc"><%=uF.showData((String)hmLevel.get((String)alLevelGoalTargetKRA.get(i)+"_SD"), "")%></div>
				<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc"><%=uF.showData((String)hmLevel.get((String)alLevelGoalTargetKRA.get(i)+"_LD"), "")%></div>
				
				<table class="table-bordered" width="100%"  style="padding-left: 4px;">
				  <tr>
				    <th><%=strSystemType %></th>
				    <%for(int r=0; alRoles!=null && r<alRoles.size(); r++) { %>
				    	<th>Marks <br>Role: 
					    	<% if(role != null && role.equalsIgnoreCase("Reviewer")) { %>
								<%="Reviewer" %>
							<% } else { %>
								<%=hmorientationMembers.get((String)alRoles.get(r))%>
							<% } %>
				    	</th>
				    <% } %>
				    <th>Weightage %</th>
				  </tr>
				  <% for(int q=0; alQuestions!=null && q<alQuestions.size(); q++) { %>
				  	<tr>
				    	<td width="80%"><strong><%=q+1%>)&nbsp;<%=hmQuestions.get((String)alQuestions.get(q))%></strong>
				    	<div style="border-top:1px solid #ccc;margin:10px 0;">
				    	  	<%for(int r=0; alRoles!=null && r<alRoles.size(); r++) { %>
						    <div style="float: left;width:100%">
						    	<%if(r==0) { %>
								<p style="font-weight:bold">Answer/Comments:</p>
								<% } %>
								
								<%if(uF.parseToInt(alRoles.get(0).toString())==4 || uF.parseToInt(alRoles.get(0).toString())==10) {
									String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q)+"_"+memberId+"_"+rolesUserIds.get(m));
								%>		
							        <div style="margin-left:20px;font-size:12px; float: left; width: 96%; margin-bottom: 5px;">
									<%
										out.println(uF.showData(queAns, ""));
									%>
									</div>
								<% } else { %>
									<div style="margin-left:20px;font-size:12px; float: left; width: 96%;">
									<%if(hmQuestionAnswer.containsKey((String)alQuestions.get(q)+"_"+memberId+"_"+rolesUserIds.get(m))){
										String strAns = ((String)hmQuestionAnswer.get((String)alQuestions.get(q)+"_"+memberId+"_"+rolesUserIds.get(m)));
											if(strAns!=null){
												strAns = strAns.replace(":_:", "<br/>");
												out.println(uF.showData(strAns, ""));
											}
										}
									%>
									</div>
								<% } %>
							</div>
							<% } %>
						</div>
				    	
				    	</td>
				    	<% for(int ii=0; alRoles!=null && ii<alRoles.size(); ii++) { %>
				    	<% if(uF.parseToInt(alRoles.get(ii).toString())==4  || uF.parseToInt(alRoles.get(ii).toString())==10) { 
					    	List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q)+"_"+alRoles.get(ii)+"_"+rolesUserIds.get(m));
					    	if(innList==null)innList=new ArrayList<String>();
					    	if(innList !=null && !innList.isEmpty()) {
				    	%>
				    	<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(0), "Not Rated") %></td>
				    	<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(1), "") %></td>
				    	<% } else { %>
				    	<td width="10%" valign="top" align="right" style="padding-right:10px">Not Rated</td>
				    	<td width="10%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
				    	<%
				    	} } else { %>
				    		<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)hmQuestionMarks.get((String)alQuestions.get(q)+"_"+alRoles.get(ii)+"_"+rolesUserIds.get(m)), "Not Rated") %></td>
				    		<td width="10%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
				    	<% } } %>
				  <% } %>
				  		<tr>
					  	<!-- ===start parvez date: 03-03-2023=== -->	
					  		<td colspan="<%=alRoles.size()+3%>">
					  	<!-- ===end parvez date: 03-03-2023=== -->	
					  			<div style="float: left;width:100%">
					  				<p style="font-weight:bold">Section Comment:</p>
					  				<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc; padding-bottom: 10px;"> <%=uF.showData((String)hmSectionComment.get(sectionIdsList.get(a)+"_"+memberId+"_"+rolesUserIds.get(m)), "")%></div>
					  			</div>
					  		</td>
				  		</tr>
				  
				</table>
				<%
					}	
				}
				%>
				
				
				<br/>
				<table class="table-bordered" width="100%"  style="padding-left: 4px;">
					<tr>
						<td>
							<p style="font-weight:bold">Areas of Strength:</p>
							<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc; padding-bottom: 10px;"> <%=uF.showData((String)hmStrengthImprovements.get(rolesUserIds.get(m)+"_STRENGTH"), "")%></div>
						</td>
						<td>
							<p style="font-weight:bold">Areas of Improvement:</p>
							<div style="font-size:12px; text-shadow: 0px 0px 2px #ccc; padding-bottom: 10px;"><%=uF.showData((String)hmStrengthImprovements.get(rolesUserIds.get(m)+"_IMPROVEMENT"), "")%></div>
						</td>
					</tr>
				</table>	
				
			<% } %>	
	<!-- ===end parvez date: 05-08-2022=== -->		
				
		<!-- ===start parvez date: 07-07-2022=== -->		
				<%-- <% if (uF.parseToInt(memberId)!=3) { %> --%>
			<% //System.out.println("memberId=="+memberId);
				String strSessionUserType = (String)session.getAttribute(IConstants.USERTYPE);
				String strSessionUserTypeID = (String)session.getAttribute(IConstants.USERTYPEID);
				String strSessionBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
				String strSessionBaseUserTypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
				
				Map<String, String> hmPriorUser = (Map<String, String>) request.getAttribute("hmPriorUser");
				if(hmPriorUser == null) hmPriorUser = new HashMap<String, String>();
				
			%>
				
				<%-- <% if (uF.parseToInt(memberId)!=3) { %>
					
						<div class="col-lg-12 col-sm-12 col-md-12" style="text-align: center;">
							<div id="reopen_<%=appId %>_<%=empid %>_<%=rolesUserIds.get(m) %>_<%=memberId %>" style="margin: 10px;">
								
								<input type="button" value="Reopen" class="btn btn-primary" onclick="reopenUserFeedback('<%=appId %>', '<%=empid %>', '<%=rolesUserIds.get(m) %>', '<%=memberId %>', '<%=appFreqId %>', '<%=role %>');" /> <!-- id, empID, userId, userType, appFreqId -->
								
							</div>
						</div>
				<% } %> --%>
				
		<div class="col-lg-12 col-sm-12 col-md-12" style="text-align: center;">	
			<div style="margin: 10px;">
			<!-- ===start parvez date: 20-03-2023=== -->
				<% if ((uF.parseToInt(memberId)!=3 || (uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_REVIEW_REOPEN_BY_HR_GHR_FOR_UPDATE_FEEDBACK)) || uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_REVIEW_REOPEN_BY_MANAGER_FOR_UPDATE_FEEDBACK)))) && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_REVIEW_REOPEN))) { %>
					<% if (!strSessionBaseUserType.equals(IConstants.ADMIN) && !strSessionBaseUserType.equals(IConstants.HRMANAGER) && ((strSessionBaseUserType.equals(IConstants.MANAGER) || strSessionUserType.equals(IConstants.MANAGER)) && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_REVIEW_REOPEN_BY_MANAGER_FOR_UPDATE_FEEDBACK))
							|| hmPriorUser!=null && (uF.parseToInt(hmPriorUser.get(memberId)) >= uF.parseToInt(hmPriorUser.get(strSessionBaseUserTypeId))))) { %>
						
					<% } else { %>
			<!-- ===end parvez date: 20-03-2023=== -->		
						<!-- <div class="col-lg-12 col-sm-12 col-md-12" style="text-align: center;"> -->
							<%-- <div id="reopen_<%=appId %>_<%=empid %>_<%=rolesUserIds.get(m) %>_<%=memberId %>" style="margin: 10px;">
								<input type="button" value="Reopen" class="btn btn-primary" onclick="reopenUserFeedback('<%=appId %>', '<%=empid %>', '<%=rolesUserIds.get(m) %>', '<%=memberId %>', '<%=appFreqId %>', '<%=role %>');" /> <!-- id, empID, userId, userType, appFreqId -->
							</div> --%>
							
							<% if(uF.parseToBoolean(hmSubmitApprovedStatus.get(memberId+"_"+rolesUserIds.get(m)+"_"+empid+"_Submit")) && !uF.parseToBoolean(hmSubmitApprovedStatus.get(memberId+"_"+rolesUserIds.get(m)+"_"+empid+"_Approved"))){ %>
								<span id="reopen_<%=appId %>_<%=empid %>_<%=rolesUserIds.get(m) %>_<%=memberId %>" >
									<input type="button" value="Reopen" class="btn btn-primary" onclick="reopenUserFeedback('<%=appId %>', '<%=empid %>', '<%=rolesUserIds.get(m) %>', '<%=memberId %>', '<%=appFreqId %>', '<%=role %>');" /> <!-- id, empID, userId, userType, appFreqId -->
								</span>
							<% } %>
						<!-- </div> -->
					<%} %>
				<% } %>
				
				<% if((strSessionBaseUserType.equals(IConstants.ADMIN) || strSessionBaseUserType.equals(IConstants.HRMANAGER)) && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HR_APPROVAL_REQUIRED_FOR_HOD_FEEDBACK))){ %>
					<% if(uF.parseToBoolean(hmSubmitApprovedStatus.get(memberId+"_"+rolesUserIds.get(m)+"_"+empid+"_Submit")) && !uF.parseToBoolean(hmSubmitApprovedStatus.get(memberId+"_"+rolesUserIds.get(m)+"_"+empid+"_Approved"))){ %>
						<span id="approve_<%=appId %>_<%=empid %>_<%=rolesUserIds.get(m) %>_<%=memberId %>" >
							<input type="button" value="Approve" class="btn btn-primary" onclick="approveUserFeedback('<%=appId %>', '<%=empid %>', '<%=rolesUserIds.get(m) %>', '<%=memberId %>', '<%=appFreqId %>', '<%=role %>');" /> <!-- id, empID, userId, userType, appFreqId -->
						</span>
					<% } %>
				<% } %>
			</div>
		</div>
		<!-- ===end parvez date: 07-07-2022=== -->		
				
			<% if(fromPage != null && fromPage.equalsIgnoreCase("ReviewerStatus") && !flag && !existUserIds.containsKey(rolesUserIds.get(m))) { %>
				<div class="col-lg-12 col-sm-12 col-md-12" style="text-align: center;">
					<div id="<%=appId %>_<%=empid %>_<%=rolesUserIds.get(m) %>_<%=memberId %>" style="margin: 10px;">
						<input type="button" value="Deny" class="btn btn-danger" onclick="revokeUserFeedback('<%=appId %>', '<%=empid %>', '<%=rolesUserIds.get(m) %>', '<%=memberId %>', '<%=appFreqId %>');" /> <!-- id, empID, userId, userType, appFreqId -->
						<input type="button" value="Correction" class="btn btn-primary" onclick="staffReviewCorrectionPoup('<%=appId %>', '<%=empid %>', '<%=rolesUserIds.get(m) %>', '<%=memberId %>', '', 'Reviewer', '<%=appFreqId %>');" /><!-- id, empID, userId, userType, currentLevel, role, appFreqId -->
					</div>
				</div>
			<% } %>
		<%
			}
		} 
		%>
		
	<div style="width: 98%; float: left;padding:10px">
		<h4>Appraiser Comments</h4>
		<p><%=uF.showData((String)request.getAttribute("strFinalComments"), "Not Commented yet")%></p>
		<p style="width:97%;text-align:right">Appraised by - <%=uF.showData((String)request.getAttribute("strAppraisedBy"), "")%></p>
		<p style="width:97%;text-align:right">on <%=uF.showData((String)request.getAttribute("strAppraisedOn"), "")%></p>
	</div>

<div id="scoreDetail"></div>