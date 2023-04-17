<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<!-- ===start parvez date: 22-03-2022=== -->
<style type="text/css">
	#textlabel{
		white-space:pre-line;
	}
</style>
<!-- ===end parvez date: 22-03-2022=== -->
<script>
	$(document).ready(function(){
			
		$("body").on('click','#closeButton1',function(){
			$(".modal-dialog1").removeAttr('style');
			$("#modal-body1").height(400);
			$("#modalInfo1").hide();
	    });
	});
</script>
<%
	List<List<String>> questionList = (List<List<String>>) request.getAttribute("questionList");

	Map<String,List<List<String>>> hmLevelQuestio1n = (Map<String,List<List<String>>>) request.getAttribute("hmLevelQuestion");
	Map hmLevelDetails = (Map) request.getAttribute("hmLevelDetails");
	
	Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
	Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
	List<String> answerTypeList = new ArrayList<String>();
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
	List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
	
//===start parvez date: 17-03-2022===	
	if(appraisalList == null) appraisalList = new ArrayList<String>();
//===end parvez date: 17-03-2022===	
	List<String> empList = (List<String>) request.getAttribute("empList");
	
	Map<String, String> hmEmpDetails = (Map) request.getAttribute("hmEmpDetails");
	Map<String, String> levelStatus = (Map<String, String>) request.getAttribute("LEVEL_STATUS");
	List<String> levelList = (List<String>) request.getAttribute("levelList");
	List<String> mainLevelList = (List<String>) request.getAttribute("mainLevelList");
//===start parvez date: 17-03-2022===	
	if(mainLevelList == null) mainLevelList = new ArrayList<String>();
//===end parvez date: 17-03-2022===	
	String currentLevel = (String) request.getAttribute("currentLevel");
	Map<String, List<List<String>>> hmSection = (Map<String, List<List<String>>>) request.getAttribute("hmSection");
	List<String> sectionIDList = (List<String>) request.getAttribute("sectionIDList");
	List<String> IsQueSectionIDList = (List<String>) request.getAttribute("IsQueSectionIDList");
	
	UtilityFunctions uF = new UtilityFunctions();
	List alSkills = (List) request.getAttribute("alSkills");
	
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	//System.out.println("SAppP.jsp/42--strUserType="+strUserType);
	
	String empID = (String)request.getAttribute("empID");
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	
	String id = (String)request.getAttribute("id");
	String userType = (String)request.getAttribute("userType");
//	System.out.println("SAppP.jsp/46--userType="+userType);
	
	//boolean isOfficialFilledStatus = uF.parseToBoolean((String)request.getAttribute("isOfficialFilledStatus"));
	String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
	if (hmEmpProfile == null) {
		hmEmpProfile = new HashMap<String, String>();
	}
	
	List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
	Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) request.getAttribute("hmElementAttribute");
	Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
	String appFreqId = (String) request.getParameter("appFreqId");
	
/* ===start parvez date: 07-07-2022=== */	
	Map<String, String> hmUsersFeedbackReopenComment = (Map<String, String>)request.getAttribute("hmUsersFeedbackReopenComment");
	if(hmUsersFeedbackReopenComment==null) hmUsersFeedbackReopenComment = new HashMap<String, String>();
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
/* ===end parvez date: 07-07-2022=== */

	Map<String, String> hmUsersFeedbackDetails = (Map<String, String>)request.getAttribute("hmUsersFeedbackDetails");
	if(hmUsersFeedbackDetails == null) hmUsersFeedbackDetails = new HashMap<String, String>();
	
/* ===start parvez date: 27-02-2023=== */
	Map<String, String> othrQueType = (Map<String, String>) request.getAttribute("othrQueType");
	if(othrQueType==null) othrQueType = new HashMap<String, String>();;
/* ===end parvez date: 27-02-2023=== */

%>

<script type="text/javascript">
	$(function() {
		$('#default').raty();
		 <%double dblPrimary = 0;	
		 if(alSkills!=null && alSkills.size()!=0) { 
               	for(int i=0; i<alSkills.size(); i++) {
               		List alInner = (List)alSkills.get(i); %>
				$('#star<%=i%>').raty({
					  readOnly: true,
					  start:    <%=uF.parseToDouble((String)alInner.get(2))/2%>,
					  half: true
					});
				<%
				if(i==0){dblPrimary = uF.parseToDouble((String)alInner.get(2))/2;}
			}
		}%>
		$('#skillPrimary').raty({
			  readOnly: true,
			  start: <%=dblPrimary%>,
			  half: true
			});
		});

	
	$(function(){
		$("input[type='submit']").click(function(e){
			e.preventDefault();
			if(confirm('Are you sure, you want to finish this?')) {
				
				var form_data = $("#formID").serialize();
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
			    	url: "StaffAppraisalPreview.action?submit=Submit",
			    	type: "POST",
			    	data: form_data,
			    	success: function(result){
			        	//$("#divResult").html(result);
			    		<% if(strUserType==IConstants.EMPLOYEE) { %>
						getMyHRData('KRATarget', 'L', '', '');
					<% } else { %>
						getAppraisalData('Appraisal', 'L', '');
					<% } %>
			    	},
			    	error: function(result){
			        	//$("#divResult").html(result);
			    		<% if(strUserType==IConstants.EMPLOYEE) { %>
						getMyHRData('KRATarget', 'L', '', '');
					<% } else { %>
						getAppraisalData('Appraisal', 'L', '');
					<% } %>
			    	}
			    });
			}
		});
	});
	
	
function editQuestionAnswer(queID,id,empID,userType,queCnt,appFreqId) {
	//alert("editQuestionAnswer queID "+ queID+" id "+ id +" empID "+ empID +" userType "+ userType);
	var dialogEdit = '#modal-body1';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo1").show();
	 $(".modal-title1").html('Edit Answer');
	 $(".modal-dialog1").width(600);
	 $.ajax({
		url : "EditStaffAppraisalQueAnswerPopUp.action?strQueOrSec=QUESTION&queID="+queID+"&id="+id+"&empID="+empID+"&userType="+userType+"&queCnt="+queCnt+"&appFreqId="+appFreqId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
	
	
function editSectionComment(queID, id, empID, userType, queCnt, appFreqId, sectionId) {
	//alert("editQuestionAnswer queID "+ queID+" id "+ id +" empID "+ empID +" userType "+ userType);
	var dialogEdit = '#modal-body1';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo1").show();
	 $(".modal-title1").html('Edit Section Comment');
	 $(".modal-dialog1").width(600);
	 $.ajax({
		url : "EditStaffAppraisalQueAnswerPopUp.action?strQueOrSec=SECTION&queID="+queID+"&id="+id+"&empID="+empID+"&userType="+userType+"&queCnt="+queCnt
			+"&appFreqId="+appFreqId+"&sectionId="+sectionId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editAreaOfStrengthAndImprovement(id, empID, userType, appFreqId) {
	//alert("editQuestionAnswer queID "+ queID+" id "+ id +" empID "+ empID +" userType "+ userType);
	var dialogEdit = '#modal-body1';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo1").show();
	 $(".modal-title1").html('Edit Area of Strength And Improvement');
	 $(".modal-dialog1").width(600);
	 $.ajax({
		url : "EditStaffAppraisalQueAnswerPopUp.action?strQueOrSec=AREAOFSI&id="+id+"&empID="+empID+"&userType="+userType+"&appFreqId="+appFreqId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editGoal(goalid,empId) { 
   
	goalTypePG = <%=IConstants.PERSONAL_GOAL%>;
	
   	var dialogEdit = '#modal-body1';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo1").show();
	$('.modal-title1').html('Edit Goal');
	$(".modal-dialog1").width(600);
	
	$.ajax({
		url : "EditMyPersonalGoalPopUp.action?operation=E&type=type&goalid="+goalid+"&typeas=goal"
				+"&empId="+empId+"&goalTypePG="+goalTypePG+"&dataType=NewGoal",
		
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
			
		}
	});
}

function approveDeny(apStatus,goalId,userType){
	
	var divResult = 'approveDenyDiv';
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			
			$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ApproveDenyGoalKRA.action?apStatus='+apStatus+'&strGoalId='+goalId+'&mReason='+reason+'&userType='+userType+'&from=review',
				cache:true,
				success : function(result) {
					
					$("#"+divResult).html(result);
				}
			});
		}
	}
}

function editQuestionAnswerFeedback(id,empID,userType,appFreqId,dataType) {
	//alert("editQuestionAnswerFeedbac");
	var dialogEdit = '#modal-body1';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo1").show();
	 $(".modal-title1").html('Edit Answer');
	 $(".modal-dialog1").width(600);
	 $.ajax({
		url : "EditStaffAppraisalQueAnswerPopUp.action?strQueOrSec=QUESTION&id="+id+"&empID="+empID+"&userType="+userType+"&appFreqId="+appFreqId+"&dataType="+dataType,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>
<!-- ===start parvez date: 15-07-2022=== -->
<%if(appraisalList !=null && !appraisalList.isEmpty()){ %>
<!-- ===end parvez date: 15-07-2022=== -->
	
	<div class="leftbox reportWidth" id="divResult">
		<div class="addgoaltoreview">
		
			<h4><%=uF.showData(appraisalList.get(1),"") %></h4>
			<div style="line-height: 12px;" id="textlabel"><%=uF.showData(appraisalList.get(2),"") %></div>
			
			<div class="addgoaltoreview-arrow"></div>
			<%-- <span style="float: right; margin: 0px 20px 0px 0px; font-size: 16px;"> Role As <b><%=hmEmpDetails.get("ORIENTATION")%></b></span> --%>
			<div style="float: right; margin: 0px 20px 0px 0px;">
			<table><tr><td>Review feedback for <b><%=hmEmpName.get(empID) %></b></td><td class="textblue">[Role- <b><%=hmEmpDetails.get("ORIENTATION")%></b>]</td></tr></table>
			 </div>
		</div>
		
<!-- ===start parvez date: 07-07-2022=== -->	
	
		<% if ((uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_REVIEW_REOPEN_BY_HR_GHR_FOR_UPDATE_FEEDBACK)) || uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_REVIEW_REOPEN_BY_HR_GHR_FOR_UPDATE_FEEDBACK))) 
				&& hmUsersFeedbackReopenComment != null && !hmUsersFeedbackReopenComment.isEmpty() && hmUsersFeedbackReopenComment.size()>0) { %>
				<div>
					<div style="line-height: 12px; " id="textlabel"><b>Reopen FeedBack</b></div>
					<div style="line-height: 12px; padding-top: 10px;" id="textlabel"><%=hmUsersFeedbackReopenComment.get("REOPEN_REASON") %></div>
				</div>
		<% } %>
<!-- ===end parvez date: 07-07-2022=== -->

			<%
			
		 	int size = 100 / (mainLevelList.size() > 0 ? mainLevelList.size() : 1);
		 	//int rating = 0;
		 	//int ratingcount=0;
		 	String sectionCount = (String)request.getAttribute("sectionCount");
		 	/* for (int i = 0; i < mainLevelList.size(); i++) {
		 		if (levelStatus.get(mainLevelList.get(i)) != null) {
		 			rating += size;
		 			ratingcount++;
		 		}
		 	} */
		 	double completePercent =(uF.parseToDouble(sectionCount)/uF.parseToDouble(""+(mainLevelList.size() > 0 ? mainLevelList.size() : 1)))*100;
		 	if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(userType) == 13 && hmUsersFeedbackDetails.get("MARKS") != null){
	        	completePercent = 100;
	        }
		 	long intcompletePercent = Math.round(completePercent);
		 	
		 	%>
 		<br/><br/>
 		<% if(intcompletePercent < 33.33) { %>
	    <span class="badge bg-red marginbottom5"><%=intcompletePercent %>%</span>
	    <div class="progress progress-xs">
	        <div class="progress-bar progress-bar-danger" style="width: <%=intcompletePercent %>%;"></div>
	    </div>
	    <% } else if(intcompletePercent >= 33.33 && intcompletePercent < 66.67) { %>
	    <span class="badge progress-bar-yellow marginbottom5"><%=intcompletePercent %>%</span>
	    <div class="progress progress-xs">
	        <div class="progress-bar progress-bar-yellow" style="width: <%=intcompletePercent %>%;"></div>
	    </div>
	    <% } else if(intcompletePercent >= 66.67) { %>
	    <span class="badge bg-green marginbottom5"><%=intcompletePercent %>%</span>
	    <div class="progress progress-xs">
	        <div class="progress-bar progress-bar-green" style="width: <%=intcompletePercent %>%;"></div>
	    </div>
	    <% } %>
		<div class="reviewbar">
			<div class="step-tab instruction-step-tab">
                <a href="javascript:void(0)">Instruction</a>
            </div>
			<%
				size = mainLevelList.size();
				for (int i = 0; i < mainLevelList.size(); i++) {								
			%>
			<div class="step-tab"><img src="images1/icons/bullet-green.png"></div>
			<% } %>
		</div>
		<div class="step-tab-content">
		
		<%if(appraisalList.get(5) != null && !appraisalList.get(5).equals("")) { %>
			
			<span id="textlabel"><%=appraisalList.get(5) %></span>
			
		<% } else { %>
			<div> No instructions provided. </div>
			<!-- <div class="nodata msg"> No instructions provided. </div> -->
		<% } %>
		</div>
		<br/>
		<s:form action="StaffAppraisalPreview" id="formID" method="POST" theme="simple">
			<s:hidden name="empID" />
			<s:hidden name="id" />
			<s:hidden name="userType" />
			<s:hidden name="appFreqId" />
			<s:hidden name="role" />
<!-- ===start parvez date: 13-07-2022=== -->			
		<%
		List<String> alUserTypeForFeedback = new ArrayList<String>();
		
		if(appraisalList.get(6) != null && !appraisalList.get(6).isEmpty()) {
			alUserTypeForFeedback = Arrays.asList(appraisalList.get(6).split(","));
		}
		%>
		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(userType) == 13){ %>
			
			<% 	
				//System.out.println("SAP.jsp/377--Comment="+hmUsersFeedbackDetails.get("COMMENT"));
				double weightage = uF.parseToDouble(hmUsersFeedbackDetails.get("WEIGHTAGE"));
				double starweight = weightage*20/100;
			%>
				<span style="float: right;margin-top: 10px;"><a id="editexist" href="javascript:void(0)" class="edit" onclick="editQuestionAnswerFeedback('<%=id %>','<%=empID %>','<%=userType %>','<%=appFreqId %>','<%=request.getAttribute("dataType") %>');" title="Edit/Change Answer"></a></span>
				<input type="hidden" id="dataType" value="<%=request.getAttribute("dataType") %>" name="dataType" />
				
				<div class="notranslate" id="starPrimary"></div>
				<input type="hidden" id="gradewithrating" value="<%=hmUsersFeedbackDetails.get("MARKS") != null ? uF.parseToDouble(hmUsersFeedbackDetails.get("MARKS")) / 20 + "" : "0"%>" name="gradewithrating" />
				
				<script type="text/javascript">
					$(function() {
						$('#starPrimary').raty({
							readOnly: true,
							start: <%=hmUsersFeedbackDetails.get("MARKS") != null ? uF.parseToDouble(hmUsersFeedbackDetails.get("MARKS")) / starweight + "" : "0"%>,
							half: true,
							targetType: 'number',
							click: function(score, evt) {
								$('#gradewithrating').val(score);
							}
						});
					});
					
				</script>
				
				<div>
					<div>
						<b>Comment:</b>
					</div>
					<textarea rows="3" cols="100" style="width: 98% !important;" name="levelcomment" readonly="readonly"><%=uF.showData((String)hmUsersFeedbackDetails.get("COMMENT"), "") %></textarea>
					
				</div>
				
		<% } else { 
			
			int sectionCnt=0;
			Set set = hmSection.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()) {
				sectionCnt++;
				String str = (String)it.next();
				//System.out.println("SAp.jsp/324--hmLevelDetails :: "+hmLevelDetails);
				int subsectionCnt=0;
				//String strSectionComment = null;
				%>
				
				<div class="addgoaltoreview">
				<!-- ===start parvez date: 27-02-2023=== -->	
					<table style="width: 98%;">
						<tr>
							<td><h4><%=sectionCnt %>)&nbsp;<%=uF.showData((String)hmLevelDetails.get(str+"_TITLE"), "") %></h4></td>
							<td><div class="pull-right">Weightage: <%=uF.showData((String)hmLevelDetails.get(str+"_LWEIGHTAGE"), "")%></div></td>
						</tr>
					</table>
				<!-- ===end parvez date: 27-02-2023=== -->	
					<div style="line-height: 12px;"><%=uF.showData((String)hmLevelDetails.get(str+"_SDESC"), "") %></div>
					<div style="line-height: 12px;"><%=uF.showData((String)hmLevelDetails.get(str+"_LDESC"), "") %></div>
					<div class="addgoaltoreview-arrow"></div>
				</div>	
				
				<% 
				if(!alUserTypeForFeedback.contains(userType)) {
					if((sectionIDList == null || sectionIDList.isEmpty() || !sectionIDList.contains(str)) && (IsQueSectionIDList !=null && IsQueSectionIDList.contains(str))) { %>
					<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<div>
						<%-- <span style="float: left;"> <img src="images1/warning.png" style="width: 20px; margin-left: 9px;"></span> --%> 
						<span style="float: left; margin-left: 3px; margin-top: 3px; font-size: 18px; color: red;"><b>This section is waiting for workflow. </b></span>
					</div>
					<br/><br/>
				<% } %>	
				
					<% if( sectionIDList != null && !sectionIDList.isEmpty() && sectionIDList.contains(str)) { %>
					<%
					List<List<String>> alQuestion11 = hmSection.get(str);
					//System.out.println("SAP.jsp---str=="+str+"---hmSection=="+hmSection);
					for(int kk=0;kk<alQuestion11.size();kk++) {
						subsectionCnt++;
						
						List<String> innerList=alQuestion11.get(kk);
						List<List<String>> alQuestion = hmLevelQuestio1n.get(innerList.get(0));
					%>
					
				<div class="addgoaltoreview">
						<h4><%=sectionCnt %>.<%=subsectionCnt %>)&nbsp;<%=uF.showData(innerList.get(1), "")%>  </h4>
						<div style="line-height: 12px;"><%=uF.showData(innerList.get(2), "")%></div>
						<br/>
						<div style="line-height: 12px;"><%=uF.showData(innerList.get(3), "")%></div>
						<div class="addgoaltoreview-arrow"></div>
					</div>	
					
					<%
							
							for (int i = 0; alQuestion != null && i < alQuestion.size(); i++) {
								List<String> innerlist = (List<String>) alQuestion.get(i);
								List<String> questioninnerList = hmQuestion.get(innerlist.get(1));
								//System.out.println("SApP/298--questioninnerList="+questioninnerList);
		
							Map<String, String> innerMp = null;
							if (innerlist.get(14) != null && !innerlist.get(14).equals("")) {
								innerMp = questionanswerMp.get(innerlist.get(14) + "question" + innerlist.get(1));
							} else if (innerlist.get(15) != null && !innerlist.get(15).equals("")) {
								innerMp = questionanswerMp.get(innerlist.get(15) + "question" + innerlist.get(1));
							} else {
								innerMp = questionanswerMp.get("question" + innerlist.get(1));
							}
							if (innerMp == null) innerMp = new HashMap<String, String>();
							//strSectionComment = innerMp.get("LEVEL_COMMENT");
				%>
				<div>
					<ul>
						<li>	
					
							<%-- <b><%=sectionCnt %>.<%=subsectionCnt %>.<%=(i + 1)%>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b> --%>
							<b><%=sectionCnt %>.<%=subsectionCnt %>.<%=(i + 1)%>)&nbsp;&nbsp;<%=uF.showData(questioninnerList.get(1),"")%> </b> 
							<div class="pull-right">Weightage: <%=uF.showData(questioninnerList.get(13),"")%></div>
					
						<s:if test="innerlist.get(3)!=null">(<%=innerlist.get(12)%>)</s:if>
							
							<% if(questioninnerList.get(10)==null || uF.parseToInt(questioninnerList.get(10)) != 6){ %>	
								<span style="float: right;margin-top: 10px;"><a id="editexist<%=i%>" href="javascript:void(0)" class="edit" onclick="editQuestionAnswer('<%=innerMp.get("APP_QUE_ANS_ID")%>','<%=id %>','<%=empID %>','<%=userType %>','<%=sectionCnt %>.<%=subsectionCnt %>.<%=(i + 1)%>','<%=appFreqId %>');" title="Edit/Change Answer"></a></span>
							<% } %>		
						</li>	
					<!-- ===start parvez date: 27-02-2023=== -->	
						<%if(othrQueType.get(innerList.get(0)) == null || (othrQueType.get(innerList.get(0)) !=null && !othrQueType.get(innerList.get(0)).equals("Without Short Description"))){ %>
						<li>
							Description: <%=uF.showData(questioninnerList.get(12),"")%>
						</li>
						<%} %>
					<!-- ===end parvez date: 27-02-2023=== -->	
						<li>
							<ul style="margin: 10px 10px 10px 30px">
								<li>
									<% if (uF.parseToInt(questioninnerList.get(8)) == 1) { %>
									<div>
										a) <input type="checkbox" disabled="disabled" value="a"
											name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> /><%=questioninnerList.get(2)%><br />
										 b) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
		
										c) <input type="checkbox" disabled="disabled" value="c" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> /><%=questioninnerList.get(4)%><br /> 
										
										d) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
		
										<textarea rows="5" cols="100" readonly="readonly" style="width: 100% !important;"
											name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
									 		answerTypeList.add("2");
									 %>
									<div>
										a) <input type="checkbox" disabled="disabled" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
		
										b) <input type="checkbox" disabled="disabled"
											name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
		
										c) <input type="checkbox" disabled="disabled" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
		
										d) <input type="checkbox" disabled="disabled"
											name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%>
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
									 		answerTypeList.add("3");
									 %>
									<div>
										<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											id="marks<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width: 31px;"/>
								
									<script>
										$(function() {
											$("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
												value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(2)%>,
												step : 1,
												disabled:true,
												slide : function(event, ui) {
													$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val(ui.value);
													$("#slidemarksscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val($("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
											$("#slidemarksscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html($("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
										});
									</script>
									<br/>
									<div id="slidemarksscore<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; text-align:center;"></div>
									<div id="sliderscore<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
									<div id="marksscore<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
							 				answerTypeList.add("4");
							 				List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").equals(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
									 		answerTypeList.add("5");
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" disabled="disabled"
											name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
		
										<% } %>
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
							 				answerTypeList.add("6");
							 				List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" disabled="disabled"
											name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
									 		answerTypeList.add("7");
									 %>
									<div>
										<strong>Ans:</strong>&nbsp;<%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : "" %>
											<input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
													id="outofmarks<%=i%>" value="<%=innerlist.get(2)%>" />
											<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
													id="marks<%=i%>" style="width: 31px;"/>
									<script>
										$(function() {
											$("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
												value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(2)%>,
												step : 1,
												disabled:true,
												slide : function(event, ui) {
													$("#marks"+<%=i%>+"").val(ui.value);
													$("#slidemarkssingleopen"+<%=i%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=i%>+"").val($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
											$("#slidemarkssingleopen"+<%=i%>+"").html($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
										});
									</script>
									<br/>
									<div id="slidemarkssingleopen<%=i%>" style="width:25%; text-align:center;"></div>
									<div id="slidersingleopen<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>						
									<div id="markssingleopen<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
									 		answerTypeList.add("8");
									 %>
									<div>
										a) <input type="radio" value="a" disabled="disabled"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
		
										b) <input type="radio" disabled="disabled"
											name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
		
										c) <input type="radio" disabled="disabled" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
		
										d) <input type="radio" disabled="disabled"
											name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
									 		answerTypeList.add("9");
									 %>
									<div>
										a) <input type="checkbox" disabled="disabled" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
		
										b) <input type="checkbox" disabled="disabled"
											name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
		
										c) <input type="checkbox" disabled="disabled" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
		
										d) <input type="checkbox" disabled="disabled"
											name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
								<%
			 						} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
			 							answerTypeList.add("10");
			 							String[] a = null;
			 							if (innerMp.get("ANSWER") != null) {
			 								a = innerMp.get("ANSWER").split(":_:");
			 						} 
			 					%>
									<div>
										<div style="float: left; margin: 30px 10px 0px 0px;">a)</div>
										<div>
											<textarea readonly="readonly" rows="5" cols="100" style="width: 100% !important;" name="a<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[0] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">b)</div>
										<div>
											<textarea readonly="readonly" rows="5" cols="100" style="width: 100% !important;" name="b<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[1] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">c)</div>
										<div>
											<textarea readonly="readonly" rows="5" cols="100" style="width: 100% !important;" name="c<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[2] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">d)</div>
										<div>
											<textarea readonly="readonly" rows="5" cols="100" style="width: 100% !important;" name="d<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[3] : ""%></textarea>
											<br />
										</div>
											<input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" id="outofmarks<%=i%>" value="<%=innerlist.get(2)%>" />
											<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks<%=i%>" style="width: 31px;"/>
													
									<script>
										$(function() {
											$("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
												value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(2)%>,
												step : 1,
												disabled:true,
												slide : function(event, ui) {
													$("#marks"+<%=i%>+"").val(ui.value);
													$("#slidemarksmultipleopen"+<%=i%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=i%>+"").val($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
											$("#slidemarksmultipleopen"+<%=i%>+"").html($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
										});
									</script>
									<br/>
									<div id="slidemarksmultipleopen<%=i%>" style="width:25%; text-align:center;"></div>
									<div id="slidermultipleopen<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
									<div id="marksmultipleopen<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div> 
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
								<%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
							 				answerTypeList.add("11");
							 				double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
							 			//===start parvez date: 09-03-2023===	
							 				//double starweight = weightage*20/100;
							 				double starweight = hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? (weightage*10/100) : (weightage*20/100);
							 			//===end parvez date: 09-03-2023===	
							 				//System.out.println("SAPP/743--questioninnerList.get(8) ===> "+questioninnerList.get(8));
							 				//System.out.println("marks ===> "+innerMp.get("MARKS")+"  weightage ===> "+weightage);
							 				//System.out.println("stars ===> "+uF.parseToDouble(innerMp.get("MARKS")) / starweight);
							 	%>
									<div id="starPrimary<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"></div> 
								<!-- ===start parvez date: 09-03-2023=== -->	
									<%-- <input
									type="hidden" id="gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
									value="<%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / 20 + "" : "0"%>"
									name="gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /> --%>
									<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
										<input
										type="hidden" id="gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
										value="<%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / starweight + "" : "0"%>"
										name="gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" />
									<%}else{ %>
										<input
										type="hidden" id="gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
										value="<%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / 20 + "" : "0"%>"
										name="gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" />
									<%} %>
								<!-- ===end parvez date: 09-03-2023=== -->	
									<script type="text/javascript">
								        $(function() {
								        	$('#starPrimary<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>').raty({
								        		readOnly: true,
								        	/* ===start parvez date: 09-03-2023=== */	
								        		number: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? 10 : 5 %>,
								        		start: <%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>,
								        		half: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? false : true %>,
								        	/* ===end parvez date: 09-03-2023=== */	
								        		targetType: 'number',
								        		click: function(score, evt) {
								        			$('#gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>').val(score);
								        			}
								        	});
								        });
								    </script>
								        <div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
								         <%
										 	} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
												answerTypeList.add("12");
										 %>
										<div>
											<strong>Ans:</strong>
											<textarea rows="5" cols="100" readonly="readonly" style="width: 100% !important;" name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
										</div> 
										<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
										<%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
									 		answerTypeList.add("13");
										 %>
										<div>
											a) <input type="radio" value="a" disabled="disabled"
												<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
												checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
			
											b) <input type="radio" disabled="disabled"
												name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
												<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
												checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
			
											c) <input type="radio" disabled="disabled" value="c"
												<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
												checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
			
											d) <input type="radio" disabled="disabled"
												name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
												<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
												checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
											e) <input type="radio" disabled="disabled"
												name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"
												<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("e")) {%>
												checked <%}%> value="e" /><%=questioninnerList.get(9)%><br />
										</div>
									<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" readonly="readonly" style="width: 100% !important;" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
									<% } %>
					<!-- ===start parvez date: 24-12-2021=== -->	
					 			<% if(questioninnerList.get(10)!=null && uF.parseToInt(questioninnerList.get(10)) == 6){ 
									Map<String, List<String>> alGoalTitle = (Map<String, List<String>>)request.getAttribute("alGoalTitle");
									
								%>
								<div>
						<!-- ===start parvez date: 17-03-2022=== -->		
									<%
									if(questioninnerList.get(11) != null){
										if(strUserType.equals(IConstants.MANAGER) && (questioninnerList.get(11) != null && uF.parseToInt(alGoalTitle.get(questioninnerList.get(11)+"").get(1)) == 0)){ %>		
												<div style="float: right; width:50%; " id="approveDenyDiv">
												
													<a href="javascript:void(0)" onclick="editGoal('<%=questioninnerList.get(11)%>','<%=alGoalTitle.get(questioninnerList.get(11)+"").get(2) %>')"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i> </a>
													
													<a href="javascript:void(0)" onclick="approveDeny('1','<%=questioninnerList.get(11)%>','<%=userType%>');"><i class="fa fa-check-circle checknew" aria-hidden="true"  title="Approve Personal Goal"></i></a>
													&nbsp;<a href="javascript:void(0)" onclick="approveDeny('-1','<%=questioninnerList.get(11)%>','<%=userType%>')"><i class="fa fa-times-circle cross" aria-hidden="true"  title="Deny Personal Goal"></i></a>
												</div>
										<% } %>	
											<div id="edit_goal" style="width:50%;" ><%=alGoalTitle.get(questioninnerList.get(11)+"").get(0)%></div>
											<br/>
									<% } %>	
						<!-- ===end parvez date: 17-03-2022=== -->			
								</div>		
								<% } %>		
					<!-- ===end parvez date: 24-12-2021=== -->
								</li>
							</ul>
						</li>
					</ul>
				</div>
				<hr>
				<%
						} 
					}
				} 
					} %>
				<div><div><b>Comment:</b>
						<span style="float: right;"><a id="editexist<%=str%>" href="javascript:void(0)" class="edit" onclick="editSectionComment('<%=(String)hmLevelDetails.get(str+"_QUE_ID") %>','<%=id %>','<%=empID %>','<%=userType %>','<%=sectionCnt %>','<%=appFreqId %>', '<%=str %>');" title="Edit/Change Section Comment"></a></span>
					</div>
					<textarea rows="3" cols="100" readonly="readonly" style="width: 98% !important;" name="levelcomment<%=str %>"><%=uF.showData((String)hmLevelDetails.get(str+"_COMMENT"), "") %></textarea>
					<div><a href="<%=uF.showData((String)hmLevelDetails.get(str+"_FILE_PATH"), "") %>"><%=uF.showData((String)hmLevelDetails.get(str+"_FILE_NAME"), "") %></a></div>
				</div>
				
			<% } %>
		
			<div id="reviewCmnt">
				<input type="hidden" name="isAreasOfStrengthAndImprovement" value="true">
				<div style="float: left; width: 47%; margin: 5px;">
					<div><b>Areas of Strength:</b>
						<span style="float: right;"><a id="editexistAreaOfStength" href="javascript:void(0)" class="edit" onclick="editAreaOfStrengthAndImprovement('<%=id %>','<%=empID %>','<%=userType %>','<%=appFreqId %>');" title="Edit/Change Areas of Strength"></a></span>
					</div>
					<textarea rows="3" cols="100" name="areasOfStrength" id="areasOfStrength" readonly="readonly" style="width: 100% !important;"><%=uF.showData((String)request.getAttribute("areasOfStrength"), "") %></textarea>
				</div>
				<div style="float: left; width: 47%; margin: 5px;">
					<div><b>Areas of Improvement:</b>
						<span style="float: right;"><a id="editexistAreaOfStength" href="javascript:void(0)" class="edit" onclick="editAreaOfStrengthAndImprovement('<%=id %>','<%=empID %>','<%=userType %>','<%=appFreqId %>');" title="Edit/Change Areas of Improvement"></a></span>
					</div>
					<textarea rows="3" cols="100" name="areasOfImprovement" id="areasOfImprovement" readonly="readonly" style="width: 100% !important;"><%=uF.showData((String)request.getAttribute("areasOfImprovement"), "") %></textarea>
				</div>
			</div>
		
		<% } %>
<!-- ===end parvez date: 13-07-2022=== -->		
			
		<div align="right" style="margin-top: 10px;">
			<s:submit value="Submit" cssClass="btn btn-primary" name="submit" />
		</div>
	</s:form>
</div>


<!-- ===start parvez date: 15-07-2022=== -->
<% } %>
<!-- ===end parvez date: 15-07-2022=== -->
<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog modal-dialog1">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1"></h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
