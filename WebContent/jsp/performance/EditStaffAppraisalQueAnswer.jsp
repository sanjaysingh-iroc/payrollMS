<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<%

	Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
	//System.out.println("9---hmQuestion="+hmQuestion);
	Map<String, String> innerMp = (Map<String, String>) request.getAttribute("innerMp");
	//System.out.println("11---innerMp="+innerMp);
	List<String> answerTypeList = new ArrayList<String>();
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
	//System.out.println("14---hmQuestionanswerType="+hmQuestionanswerType);
	UtilityFunctions uF = new UtilityFunctions();
	String id = (String)request.getAttribute("id");
	String userType = (String)request.getAttribute("userType");
	String appFreqId = (String)request.getParameter("appFreqId");
	String sectionId = (String)request.getParameter("sectionId");
	String strQueOrSec = (String)request.getParameter("strQueOrSec");
	//System.out.println("21---strQueOrSec="+strQueOrSec);
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus==null) hmFeatureStatus = new HashMap<String, String>();
	//System.out.println("24---hmFeatureStatus="+strQueOrSec);
	Map<String, String> hmUsersFeedbackDetails = (Map<String, String>) request.getAttribute("hmUsersFeedbackDetails");
	if(hmUsersFeedbackDetails==null) hmUsersFeedbackDetails = new HashMap<String, String>();
	//System.out.println("27---hmFeatureStatus="+strQueOrSec);
%>

<%-- <script>
$(function() {
	$("input[type='submit']").click(function(e) {
		$("#modalInfo1").hide();
		e.preventDefault();
		var id = document.getElementById("id").value;
		var empID = document.getElementById("empID").value;
		var userType = document.getElementById("userType").value;
		var queID = document.getElementById("queID").value;
		var appFreqId = document.getElementById("appFreqId").value;
		var form_data = $("#formID1").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
	    	url: "EditStaffAppraisalQueAnswer.action?submit", 
	    	type: "POST",
	    	data: form_data,
	    	success: function(result){
	        	$("#divResult").html(result);
	    	},
			error: function(result){
				$.ajax({
					url: 'StaffAppraisalPreview.action?id='+id+'&empID='+empID+'&userType='+userType+'&appFreqId='+appFreqId,
					cache: true,
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
			}
	    });
	});
});
</script> --%>

<!-- ===start parvez date: 12-04-2022=== -->
<script>
/* $(function() { */
	$("input[type='submit']").click(function(e) {
	
 		e.preventDefault();
 		var flag11QueId = 1;
			if(document.getElementById("ans11QueId")) {
				/* var ans11QueId = document.getElementById("ans11QueId").value;
				var a1 = '';
				if(ans11QueId.trim() != '') {
					a1 = new Array();
					a1 = ans11QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = document.getElementById('gradewithrating'+a1[i]).value;
					if (parseFloat(v1) > 0) {
					    cnt++;
					}
				}
				if(a1.length != cnt) {
					flag11QueId=0;
				} */
				
				var ans11QueId = document.getElementById("ans11QueId").value;
			/* ===start parvez date: 22-02-2023=== */	
				var v1;
				if(document.getElementById('gradewithrating'+ans11QueId)){
					v1 = document.getElementById('gradewithrating'+ans11QueId).value;
				}
			/* ===end parvez date: 22-02-2023=== */	
				
				if (parseFloat(v1) == 0) {
					flag11QueId=0;
				}
				
			}
			if(flag11QueId==1){
				submitForm1('submit');
		
			} else{
				alert("Please answer question, then click on Save button.");
			}
	});
/* }); */

function submitForm1(submit) {
	$("#modalInfo1").hide();
	//alert("save");
	/* e.preventDefault(); */
	var id = document.getElementById("id").value;
	var empID = document.getElementById("empID").value;
	var userType = document.getElementById("userType").value;
	var dataType = document.getElementById("dataType").value;
	var queID = document.getElementById("queID").value;
	var appFreqId = document.getElementById("appFreqId").value;
	var form_data = $("#formID1").serialize();
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
    	url: "EditStaffAppraisalQueAnswer.action?submit", 
    	type: "POST",
    	data: form_data,
    	success: function(result){
        	$("#divResult").html(result);
    	},
		error: function(result){
			$.ajax({
				url: 'StaffAppraisalPreview.action?id='+id+'&empID='+empID+'&userType='+userType+'&appFreqId='+appFreqId+'&dataType='+dataType,
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
    });
}

</script>
<!-- ===end parvez date: 12-04-2022=== -->

	<div class="reportWidth1">
	<s:form action="EditStaffAppraisalQueAnswer" id="formID1" method="POST" theme="simple">
		<s:hidden name="id" id="id" />
		<s:hidden name="empID" id="empID" />
		<s:hidden name="userType" id="userType" />
		<s:hidden name="queID" id="queID" />
		<s:hidden name="appFreqId" id="appFreqId" />
		<s:hidden name="strQueOrSec" id="strQueOrSec" />
		<s:hidden name="sectionId" id="sectionId" />
		<input type="hidden" id="dataType" value="<%=request.getAttribute("dataType") %>" name="dataType" />
		<% //System.out.println("149---F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT="+uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))); %>
		<% if((uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(userType) == 13)){ %>
			<% //System.out.println("149---userType="+userType); %>
			<% 	
			 		double weightage = uF.parseToDouble(hmUsersFeedbackDetails.get("WEIGHTAGE"));
			 		//double starweight = weightage*20/100;
			 		double starweight = hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? (weightage * 10 / 100) : (weightage * 20 / 100);
			 		//System.out.println("hmUsersFeedbackDetails="+uF.parseToInt(hmUsersFeedbackDetails.get("MARKS")) / starweight);
			%>
							
							<div id="starPrimary1"></div> 
							<input type="hidden" id="gradewithrating1"
							value="<%=hmUsersFeedbackDetails.get("MARKS") != null ? uF.parseToInt(hmUsersFeedbackDetails.get("MARKS")) / starweight + "" : "0"%>"
							name="gradewithrating1" />
							<script type="text/javascript">
						        $(function() {
						        	$('#starPrimary1').raty({
						        		start: <%=hmUsersFeedbackDetails.get("MARKS") != null ? uF.parseToDouble(hmUsersFeedbackDetails.get("MARKS")) / starweight + "" : "0"%>,
						        		number: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? 10 : 5 %>,
										half: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? false : true %>,
						        		targetType: 'number',
						        		click: function(score, evt) {
						        			$('#gradewithrating1').val(score);
						        		}
						        	});
						        });
							</script>
						    <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment"><%=hmUsersFeedbackDetails.get("COMMENT") != null ? hmUsersFeedbackDetails.get("COMMENT") : ""%></textarea></div>
		<% } else{ %>
		<% if(uF.parseToInt(sectionId) == 0 && (strQueOrSec == null || strQueOrSec.equalsIgnoreCase("QUESTION"))) { %>
		<% List<String> questioninnerList = hmQuestion.get(innerMp.get("QUESTION_ID")); %>
		
			<%
			StringBuilder sbAnsType11QueId = null;//Grade with Rating
			%>
		
		<div style="margin: 10px 25px 10px 25px;">
		
			<ul>
				<li><b><%=request.getAttribute("queCnt") %>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b>
					<div class="pull-right">Weightage: <%=uF.showData(questioninnerList.get(16),"")%></div> 
				</li>
				<li>
					<%if(questioninnerList.get(17)!=null && !questioninnerList.get(17).equals("")){ %>
						Description: <%=uF.showData(questioninnerList.get(17),"")%>
					<%} %>
				</li>
				<li>
					<ul style="margin: 10px 10px 10px 30px">
						<li>
							<% if (uF.parseToInt(questioninnerList.get(8)) == 1) { %>
							<div>
								a) <input type="checkbox" value="a" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
									checked <%}%> /><%=questioninnerList.get(2)%><br /> b) <input
									type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
									checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

								c) <input type="checkbox" value="c" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
									checked <%}%> /><%=questioninnerList.get(4)%><br /> d) <input
									type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
									checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />

								<textarea rows="5" cols="50" style="width:100%" name="remark<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
							 		answerTypeList.add("2");
							 %>

							<div>
								a) <input type="checkbox" value="a"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
									checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />

								b) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
									checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

								c) <input type="checkbox" value="c"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
									checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />

								d) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
									checked <%}%> value="d" /><%=questioninnerList.get(5)%>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
							 		answerTypeList.add("3");
							 %>
							<div>
							<input type="hidden" name="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks<%=questioninnerList.get(8)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width: 31px;"/>
						
						<script>
							$(function() {
								$("#sliderscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
									value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
									min : 0,
									max : <%=innerMp.get("WEIGHTAGE")%>,
									step : 1,
									slide : function(event, ui) {
										$("#marks"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val(ui.value);
										$("#slidemarksscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html(ui.value);
									}
								});
								$("#marks"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val($("#sliderscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
								$("#slidemarksscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html($("#sliderscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
							});
						</script>
						<br/>
						<div id="slidemarksscore<%=questioninnerList.get(8)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; text-align:center;"></div>
						<div id="sliderscore<%=questioninnerList.get(8)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
						<div id="marksscore<%=questioninnerList.get(8)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerMp.get("WEIGHTAGE")%></span></div>
									
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
					 				answerTypeList.add("4");
					 				List<List<String>> outer = hmQuestionanswerType.get(questioninnerList.get(8));
							 %>
							<div>
								<%
									for (int j = 0; j < outer.size(); j++) {
										List<String> inner = outer.get(j);
								%>
								<input type="radio" name="<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").equals(inner.get(0))) {%>
									checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
								<% } %>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
					 				answerTypeList.add("5");
					 				List<List<String>> outer = hmQuestionanswerType.get(questioninnerList.get(8));
							 %>
							<div>
								<%
									for (int j = 0; j < outer.size(); j++) {
										List<String> inner = outer.get(j);
								%>
								<input type="radio" name="<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
									checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />

								<% } %>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
					 				answerTypeList.add("6");
					 				List<List<String>> outer = hmQuestionanswerType.get(questioninnerList.get(8));
							 %>
							<div>
								<%
									for (int j = 0; j < outer.size(); j++) {
													List<String> inner = outer.get(j);
								%>
								<input type="radio" name="<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
									checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br/>
								<%
									}
								%>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
							 		answerTypeList.add("7");
							 %>
							<div>
								<strong>Ans:</strong>&nbsp;<%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : "" %>
								<input type="hidden" name="outofmarks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="outofmarks" value="<%=innerMp.get("WEIGHTAGE")%>"/>
								<input type="hidden" name="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks" style="width: 31px;" value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>"/>
							<script>
								$(function() {
									$("#slidersingleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
										value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
										min : 0,
										max : <%=innerMp.get("WEIGHTAGE")%>,
										step : 1,
										slide : function(event, ui) {
											$("#marks"+"").val(ui.value);
											$("#slidemarkssingleopen"+"").html(ui.value);
										}
									});
									$("#marks"+"").val($("#slidersingleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
									$("#slidemarkssingleopen"+"").html($("#slidersingleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
								});
							</script>
						
						<br/>
						<div id="slidemarkssingleopen" style="width:25%; text-align:center;"></div>
						<div id="slidersingleopen<%=questioninnerList.get(8)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>						
						<div id="markssingleopen<%=questioninnerList.get(8)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerMp.get("WEIGHTAGE")%></span></div>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
							 		answerTypeList.add("8");
							 %>
							<div>
								a) <input type="radio" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
									checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />

								b) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
									checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

								c) <input type="radio" value="c"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
									checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />

								d) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
									checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
							 		answerTypeList.add("9");
							 %>
							<div>
								a) <input type="checkbox" value="a"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
									checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />

								b) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
									checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

								c) <input type="checkbox" value="c"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
									checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />

								d) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
									checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
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
									<textarea rows="5" cols="50" style="width:100%"
										name="a<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[0] : ""%></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">b)</div>
								<div>
									<textarea rows="5" cols="50" style="width:100%"
										name="b<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[1] : ""%></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">c)</div>
								<div>
									<textarea rows="5" cols="50" style="width:100%"
										name="c<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[2] : ""%></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">d)</div>
								<div>
									<textarea rows="5" cols="50" style="width:100%"
										name="d<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[3] : ""%></textarea>
									<br />
								</div>
								<input type="hidden" name="outofmarks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="outofmarks" value="<%=innerMp.get("WEIGHTAGE")%>"/>
								<input type="hidden" name="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks" style="width: 31px;" value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>"/>
								<script>
								/* disabled:true, */
									$(function() {
										$("#slidermultipleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
											value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
											min : 0,
											max : <%=innerMp.get("WEIGHTAGE")%>,
											step : 1,
											slide : function(event, ui) {
												$("#marks"+"").val(ui.value);
												$("#slidemarksmultipleopen"+"").html(ui.value);
											}
										});
										$("#marks"+"").val($("#slidermultipleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
										$("#slidemarksmultipleopen"+"").html($("#slidermultipleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
									});
								</script>
							<br/>
							<div id="slidemarksmultipleopen" style="width:25%; text-align:center;"></div>
							<div id="slidermultipleopen<%=questioninnerList.get(8)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
							<div id="marksmultipleopen<%=questioninnerList.get(8)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerMp.get("WEIGHTAGE")%></span></div>
							</div> 
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
						<%
					 	} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
					 		answerTypeList.add("11");
			 		//===start parvez date: 12-04-2022===
			 				if (sbAnsType11QueId == null) {
			 					sbAnsType11QueId = new StringBuilder();
			 					sbAnsType11QueId.append(innerMp.get("APP_QUE_ANS_ID"));
			 				}/*  else {
			 					sbAnsType11QueId.append(","+ innerMp.get("APP_QUE_ANS_ID") + "_"+ questioninnerList.get(9));
			 				} */ 
			 		//===end parvez date: 12-04-2022===	
			 				
			 				double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
			 			//===start parvez date: 09-03-2023===	
			 				//double starweight = weightage*20/100;
			 				double starweight = hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? (weightage*10/100) : (weightage*20/100);
					 		
			 			//===end parvez date: 09-03-2023===
					 	%>
							<div id="starPrimary<%=innerMp.get("APP_QUE_ANS_ID")%>"></div> 
							<input type="hidden" id="gradewithrating<%=innerMp.get("APP_QUE_ANS_ID")%>"
							value="<%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / starweight + "" : "0"%>"
							name="gradewithrating<%=innerMp.get("APP_QUE_ANS_ID")%>" />
							<script type="text/javascript">
						        $(function() {
						        	$('#starPrimary<%=innerMp.get("APP_QUE_ANS_ID")%>').raty({
						        		start: <%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>,
						        	/* ===start parvez date: 09-03-2023=== */	
						        		number: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? 10 : 5 %>,
						        		half: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? false : true %>,
						        	/* ===end parvez date: 09-03-2023=== */	
						        		targetType: 'number',
						        		click: function(score, evt) {
						        			$('#gradewithrating<%=innerMp.get("APP_QUE_ANS_ID")%>').val(score);
						        		}
						        	});
						        });
							</script>
						    <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
						<%
					 		} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
								answerTypeList.add("12");
						%>
						<div>
							<strong>Ans:</strong>
							<textarea rows="5" cols="50" style="width:100%" name="<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
						</div> 
						<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
						<%
						 	} else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
						 		answerTypeList.add("13");
						 		
						 		List<String> al = new ArrayList<String>();
						 		
						 		al.add("<input type=\"radio\" value=\"a\" "+((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) ?"checked" : "")
						 			+ " name='correct"+innerMp.get("APP_QUE_ANS_ID")+"' />"+questioninnerList.get(2)+"<br />");
						 		
						 		al.add("<input type=\"radio\" value=\"b\" "+((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) ?"checked" : "")
							 		+ " name='correct"+innerMp.get("APP_QUE_ANS_ID")+"' />"+questioninnerList.get(3)+"<br />");
						 		
						 		al.add("<input type=\"radio\" value=\"c\" "+((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) ?"checked" : "")
							 		+ " name='correct"+innerMp.get("APP_QUE_ANS_ID")+"' />"+questioninnerList.get(4)+"<br />");
						 		
						 		al.add("<input type=\"radio\" value=\"d\" "+((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) ?"checked" : "")
							 		+ " name='correct"+innerMp.get("APP_QUE_ANS_ID")+"' />"+questioninnerList.get(5)+"<br />");
						 		
						 		al.add("<input type=\"radio\" value=\"e\" "+((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("e")) ?"checked" : "")
							 		+ " name='correct"+innerMp.get("APP_QUE_ANS_ID")+"' />"+questioninnerList.get(10)+"<br />");
						 		
						 		Collections.shuffle(al); 
						 %>
						<div>
							<%-- a) <input type="radio" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
								checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />

							b) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
								<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
								checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

							c) <input type="radio" value="c"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
								checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />

							d) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
								<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
								checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
								
							e) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"
								<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("e")) {%>
								checked <%}%> value="e" /><%=questioninnerList.get(10)%><br /> --%>
								
							a)<%=al.get(0) %>
							b)<%=al.get(1) %>
							c)<%=al.get(2) %>
							d)<%=al.get(3) %>
							e)<%=al.get(4) %>
							
						</div>
						<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
						 
						<% } %>
						</li>
					</ul>
				</li>
			</ul>
			
		</div>
		<!-- ===start parvez date: 12-04-2022=== -->	
			<input type="hidden" name="ans11QueId" id="ans11QueId" value="<%=sbAnsType11QueId != null ? sbAnsType11QueId.toString() : ""%>" />
		<!-- ===end parvez date: 12-04-2022=== -->
		<% } else if(uF.parseToInt(sectionId) == 0 && (strQueOrSec == null || strQueOrSec.equalsIgnoreCase("AREAOFSI"))) { %>
		<div style="margin: 10px 25px 10px 0px;">
			<ul>
				<li>
					<div id="reviewCmnt">
						<input type="hidden" name="isAreasOfStrengthAndImprovement" value="true">
						<div style="float: left; width: 47%; margin: 5px;">
							<div><b>Areas of Strength:</b></div>
							<textarea rows="3" cols="100" name="areasOfStrength" id="areasOfStrength" style="width: 100% !important;"><%=uF.showData((String)request.getAttribute("areasOfStrength"), "") %></textarea>
						</div>
						<div style="float: left; width: 47%; margin: 5px;">
							<div><b>Areas of Improvement:</b></div>
							<textarea rows="3" cols="100" name="areasOfImprovement" id="areasOfImprovement" style="width: 100% !important;"><%=uF.showData((String)request.getAttribute("areasOfImprovement"), "") %></textarea>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<% } else { 
			Map<String, String> hmLevelDetails = (Map<String, String>) request.getAttribute("hmLevelDetails");
		%>
			<div style="margin: 10px 25px 10px 0px;">
			<ul>
				<li><b><%=request.getAttribute("queCnt") %>)&nbsp;&nbsp;<%=hmLevelDetails.get(sectionId+"_TITLE") %> </b><br/>
					&nbsp;&nbsp;&nbsp;&nbsp;<%=hmLevelDetails.get(sectionId+"_SDESC") %>
				
				</li>
				<li>
					<ul>
						<li>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="100" style="width: 450px !important;" name="levelcomment<%=sectionId %>"><%=uF.showData((String)hmLevelDetails.get(sectionId+"_COMMENT"), "") %></textarea></div>
						</li>
					</ul>
				</li>
			</ul>
		</div>
		<% } %>
		
		<% } %>
		<hr>

	<div style="text-align: center; margin: 10px 10px 10px 10px;">
		<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
	</div>

	</s:form>
</div>