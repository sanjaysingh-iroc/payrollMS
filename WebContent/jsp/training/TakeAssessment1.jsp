<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script type="text/javascript" src="scripts/_rating/js/jquery.raty.min.js"> </script>	<!-- ===added by parvez date: 14-02-2023=== -->

<%
UtilityFunctions uF = new UtilityFunctions();
List alSkills = (List) request.getAttribute("alSkills");
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String empID = (String)session.getAttribute("empID");
boolean isOfficialFilledStatus = uF.parseToBoolean((String)request.getAttribute("isOfficialFilledStatus"));

Map hm = (HashMap) request.getAttribute("myProfile");
if (hm == null) {
	hm = new HashMap();
}

String strImage = (String) hm.get("IMAGE");

%>
<script type="text/javascript">
	function isNumber(n,id,val) {
	
		if((isNaN(parseInt(n)))){
			document.getElementById(id).value='';
			
			if(n.length>0){
			alert("Not a Number");
			}
		}else{
			document.getElementById(id).value=parseInt(n);
		}
		if(parseInt(n)>parseInt(val)){
			document.getElementById(id).value='';
			alert("Value is greater than Weightage");
		}
	}
	
	function showQuestions(){
		document.getElementById("queAsnDiv").style.display = "block";
		document.getElementById("startDiv").style.display = "none";
	}
	
	var timerId;
	function startTimer(duration, display) {
	    var timer = duration, minutes, seconds;
	   
	    timerId = window.setInterval(function () {
			minutes = parseInt(timer / 60, 10);
			seconds = parseInt(timer % 60, 10);
	
			minutes = minutes < 10 ? "0" + minutes : minutes;
			seconds = seconds < 10 ? "0" + seconds : seconds;
	
			display.textContent = minutes + ":" + seconds;
			document.getElementById("timeDuration").value = minutes + "." + seconds;
			var strTime = minutes + "." + seconds;
			
			updateTime(strTime);
			 
			if (--timer < 0) {
			    timer = duration;
			    clearTimeout(timerId);
			    timerId = null;
			    submitForm();
			}
	    }, 1000);
	}

	function submitForm(){
	    document.getElementById("finishType").value = 'finish';
	    //document.TakeAssessment1FormID.submit(); 
	   // $("#TakeAssessment1FormID").submit();
	   // $( "form:first" ).submit();
	    $("form[name='TakeAssessment1FormID']").submit();
	    
	}
	
	function updateTime(strTime){
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var assessmentId = document.getElementById("assessmentId").value;
			var lPlanId = document.getElementById("lPlanId").value;
			var xhr = $.ajax({
				url : "AssessmentEmpRemainTime.action?assessmentId="+ assessmentId+"&lPlanId="+lPlanId+"&timeDuration="+strTime,
				cache : false,
				success : function(data) {
					
				}
			});
		}
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
	
	function getStepTabContent(assessmentId,lPlanId,empID,userType,currentLevel,role,levelCount){
		$("#queAsnDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $.ajax({
	    	url: "TakeAssessment1.action?assessmentId="+assessmentId+"&lPlanId="+lPlanId+"&empID="+empID+"&userType="+userType+"&currentLevel="+currentLevel+"&role="+role+"&levelCount="+levelCount,
	    	type: "GET",
	    	success: function(result){
	        	$("#queAsnDiv").html(result);
	    	}
	    });
	}
	
	   var submitActor = null;
	   var submitButtons = $('form').find('input[type=submit]').filter(':visible');
	   $("form").bind('submit',function(event) {
			  event.preventDefault();
			  if (null === submitActor) {
	              // If no actor is explicitly clicked, the browser will
	              // automatically choose the first in source-order
	              // so we do the same here
	              submitActor = submitButtons[0];
	          }
			  var form_data = $("#"+this.id).serialize();
		   	  var submitBtn=$('input[name = submit ]').val();
		   	  var btnfinish=$('input[name = btnfinish ]').val();
			  var submit = submitActor.name;
	         // alert("submit==>"+submit);
	         var varAction = 'TakeAssessmentPreview.action';
	          if(submit != null && submit == "submit") {
	        	  form_data = form_data +"&submit="+submitBtn;
	          } else if(submit != null && submit == "btnfinish"){
	        	  form_data = form_data +"&btnfinish="+btnfinish;
	        	  varAction = 'TakeAssessmentSummary.action';
	          }
	          var assessmentId = document.getElementById("assessmentId").value;
	          var lPlanId = document.getElementById("lPlanId").value;
	          var empID = document.getElementById("empID").value;
	          var userType = document.getElementById("userType").value;
	          
	          $("#queAsnDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
			    	url: "TakeAssessment1.action", 
			    	type: "POST",
			    	data: form_data,
			    	success: function(result){
			        	$("#queAsnDiv").html(result);
			    	}, 
		 			error : function(err) {
		 				$.ajax({ 
							url: varAction+'?assessmentId='+assessmentId+'&lPlanId='+lPlanId+'&empID='+empID+'&userType='+userType,
							cache: true,
							success: function(result){
								$("#queAsnDiv").html(result);
					   		}
						});
		 			}
			    });
		     
	   });
	   submitButtons.click(function(event) {
	       submitActor = this;
	   });

	/* $(function(){
		$("#TakeAssessment1FormID").submit(function(e){
			e.preventDefault();
			var form_data = $("#TakeAssessment1FormID").serialize();
			$("#queAsnDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
		    	url: "TakeAssessment1.action", 
		    	type: "POST",
		    	data: form_data,
		    	success: function(result){
		        	$("#queAsnDiv").html(result);
		    	}
		    });
		});
	}); */
</script>

<g:compress>
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
					  start:    <%=dblPrimary%>,
					  half: true
					});
		});

</script>
</g:compress>

<%
	List<String> sectionsList = (List<String>) request.getAttribute("sectionsList");
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	Map<String, String> hmSectionDetails = (Map<String, String>) request.getAttribute("hmSectionDetails"); 
	Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
	String currentLevel = (String) request.getAttribute("currentLevel");
	Map<String, String> sectionStatus = (Map<String, String>) request.getAttribute("SECTION_STATUS");
	if (hmSectionDetails == null)hmSectionDetails = new HashMap<String, String>();
	List<String> answerTypeList = new ArrayList<String>();
%>

<%
Map<String, String> hmEmpList = (Map<String, String>) request.getAttribute("hmEmpList");
Map<String, String> hmMesures = (Map<String, String>) request.getAttribute("hmMesures");
Map<String, String> hmMesuresType = (Map<String, String>) request.getAttribute("hmMesuresType");

CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");

Map<String, List<List<String>>> hmGoalTitle = (Map<String, List<List<String>>>) request.getAttribute("hmGoalTitle");

Map<String, List<List<String>>> hmKRA =(Map<String, List<List<String>>>)request.getAttribute("hmKRA");

List<String> memberList = (List<String>) request.getAttribute("memberList");
Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");

String strUserTypeId = (String) session.getAttribute(IConstants.USERTYPEID);
String id=request.getParameter("id");
String empid=request.getParameter("empID");

Map<String, String> hmTarget =(Map<String, String>)request.getAttribute("hmTarget");

String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);

Map<String, String> hmMesures1 = (Map<String, String>) request.getAttribute("hmMesures1");
Map<String, String> hmMesuresType1 = (Map<String, String>) request.getAttribute("hmMesuresType1");

Map<String, List<List<String>>> hmGoalTitle1 = (Map<String, List<List<String>>>) request.getAttribute("hmGoalTitle1");

LinkedHashMap<String, List<List<String>>> hmKRA1 =(LinkedHashMap<String, List<List<String>>>)request.getAttribute("hmKRA1");

List<String> memberList1 = (List<String>) request.getAttribute("memberList1");
Map<String, String> orientationMemberMp1 = (Map<String, String>) request.getAttribute("orientationMemberMp1");

Map<String, String> hmKRARating =(Map<String, String>)request.getAttribute("hmKRARating");

 //String userType=request.getParameter("userType");
//boolean levelFlag=(Boolean)request.getAttribute("levelFlag");
//boolean existLevelFlag=(Boolean)request.getAttribute("existLevelFlag");

List<String> listRemainOrientType = (List<String>)request.getAttribute("listRemainOrientType");





List<String> assessmentList = (List<String>) request.getAttribute("assessmentList");
List<List<String>> questionList = (List<List<String>>) request.getAttribute("questionList");
Map<String, Map<String, String>> questionAnswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionAnswerMp");
//System.out.println("questionList ---> " + questionList);
//System.out.println("assessmentList ---> " + assessmentList);
%>


<div class="leftbox reportWidth">
	<div id="queAsnDiv">
	<div>
		<div class="addgoaltoreview">
				<h4><%=assessmentList.get(1) %></h4>
				<div style="padding: 5px;font-size: 12px;line-height: 12px;">
					Subject: <%=assessmentList.get(2) %> <br/><br/>
					Author: <%=assessmentList.get(3) %>		
				</div>
			</div>
			<%
		 	int size = 100 / sectionsList.size();
		 	String sectionCount = (String)request.getAttribute("sectionCount");
		 	double completePercent =(uF.parseToDouble(sectionCount)/uF.parseToDouble(""+sectionsList.size()))*100;
		 	long intcompletePercent = Math.round(completePercent);
		 %>
		 		<br/>
		 		<br/>
				<%if(intcompletePercent < 33.33){ %>
			        <span class="badge bg-red marginbottom5"><%=intcompletePercent %>%</span>
			        <div class="progress progress-xs">
			            <div class="progress-bar progress-bar-danger" style="width: <%=intcompletePercent %>%;"></div>
			        </div>
			        <%}else if(intcompletePercent >= 33.33 && intcompletePercent < 66.67){ %>
			        <span class="badge progress-bar-yellow marginbottom5"><%=intcompletePercent %>%</span>
			        <div class="progress progress-xs">
			            <div class="progress-bar progress-bar-yellow" style="width: <%=intcompletePercent %>%;"></div>
			        </div>
			        <%}else if(intcompletePercent >= 66.67){ %>
			        <span class="badge bg-green marginbottom5"><%=intcompletePercent %>%</span>
			        <div class="progress progress-xs">
			            <div class="progress-bar progress-bar-green" style="width: <%=intcompletePercent %>%;"></div>
			        </div>
		        <%} %>
		<div class="reviewbar">
			<div class="step-tab instruction-step-tab">
                <a href="javascript:void(0)" onclick="getStepTabContent('<s:property value="assessmentId"/>','<s:property value="lPlanId"/>','<s:property value="empID"/>','<s:property value="userType"/>','<%=currentLevel%>','<s:property value="role"/>','null')">Instruction
                </a>
            </div>
			<%
				size = sectionsList.size();
				for (int i = 0; i < sectionsList.size(); i++) {
					if (request.getAttribute("levelCount").toString().equals("1")) {
						size = 0;
					}
						if (currentLevel.equals(sectionsList.get(i)) && !request.getAttribute("levelCount").toString().equals("1")) {
						size = i + 1;
			%>
			<div class="step-tab">
                  <img src="images1/icons/bullet-green.png">
               </div>
			<%
				} else {
						if (sectionStatus.get(sectionsList.get(i)) != null) {
			%>
			 <div class="step-tab">
		          <a href="javascript:void(0)" onclick="getStepTabContent('<s:property value="assessmentId"/>','<s:property value="lPlanId"/>','<s:property value="empID"/>','<s:property value="userType"/>','<%=sectionsList.get(i)%>','<s:property value="role"/>','<%=uF.parseToInt(request.getAttribute("levelCount").toString())+1 %>')">
		              <img src="images1/icons/bullet-green1.png">
		          </a>
		      </div>
			<%
				} else {
			%>
			<div class="step-tab">
	          <a href="javascript:void(0)" onclick="getStepTabContent('<s:property value="assessmentId"/>','<s:property value="lPlanId"/>','<s:property value="empID"/>','<s:property value="userType"/>','<%=sectionsList.get(i)%>','<s:property value="role"/>','<%=uF.parseToInt(request.getAttribute("levelCount").toString())+1 %>')">
	              <img src="images1/icons/bullet-white-1.png">
	          </a>
	        </div>
			<%
				}
				}
				}
			%>
		</div>
		<div class="step-tab-content">
			<% if(!request.getAttribute("levelCount").toString().equals("1")){ %>
			<div class="addgoaltoreview">
				<h4><%=size%>)&nbsp;<%=uF.showData(hmSectionDetails.get("SECTION_NAME"), "")%>
				<input type="hidden" name="hideSectionId" id="hideSectionId" value="<%=uF.showData(hmSectionDetails.get("ASSESS_SECTION_ID"), "")%>" />
				</h4>

				<div style="width: 70%;font-size: 12px; text-align: justify; line-height: 12px;">
					<%=uF.showData(hmSectionDetails.get("SECTION_DESC"), "")%>				
				</div>
				
				<div style="width: 70%;font-size: 12px; text-align: justify; line-height: 12px;">
					Marks for section: <%=uF.showData(hmSectionDetails.get("MARKS_FOR_SECTION"), "")%>				
				</div>
				
				<div style="width: 70%;font-size: 12px; text-align: justify; line-height: 12px;">
					Questions to attempt: Any <%=uF.showData(hmSectionDetails.get("ATTEMPT_QUE"), "")%>				
				</div>
				
				<div class="addgoaltoreview-arrow"></div>
				<%-- <% } %> --%>
			</div>
			<%} %>
			<form name="TakeAssessment1FormID" id="TakeAssessment1FormID">
				<s:hidden name="empID" id="empID"></s:hidden>
				<s:hidden name="assessmentId" id="assessmentId"></s:hidden>
				<s:hidden name="lPlanId" id="lPlanId"></s:hidden>
				<s:hidden name="levelId" />
				<s:hidden name="currentLevel" />
				<s:hidden name="userType" id="userType" />
				<%-- <s:hidden name="role" /> --%>
				<s:hidden name="levelCount"/>
				<input type="hidden" name="timeDuration" id="timeDuration" value="<%=((String)request.getAttribute("TIME_DURATION"))%>"/>
				<s:hidden name="finishType" id="finishType"></s:hidden>
				
				<%						
					if(request.getAttribute("levelCount").toString().equals("1")) {
					if(assessmentList.get(5)!= null && !assessmentList.get(5).equals("")) {
				%>
					<div style="font-size: 16px;"><%=assessmentList.get(5) %> </div>
				<% } else { %>
					<div style="font-size: 16px;"> No description provided. </div>
				<%	
				}
				} else {
			
					for (int i = 0; questionList != null && i < questionList.size(); i++) {
							List<String> innerlist = (List<String>) questionList.get(i);
//							List<String> questioninnerList = hmQuestion.get(innerlist.get(0));

							Map<String, String> innerMp = null;
							if(questionAnswerMp != null)
								innerMp = questionAnswerMp.get(innerlist.get(9) + "question" + innerlist.get(0));
							if (innerMp == null)
								innerMp = new HashMap<String, String>();
							
				%>
				<div style="width: 80%; margin: 10px 50px 10px 50px; border-bottom: 1px solid rgb(0, 0, 0);">

					<ul>
						<li><b><%=size %>.<%=(i + 1)%>)&nbsp;&nbsp;<%=innerlist.get(1)%> </b> </li>
						<li>
							<ul style="margin: 10px 10px 10px 30px">
								<li>
									<%
										if (uF.parseToInt(innerlist.get(8)) == 1) {
											if(!answerTypeList.contains("1")){		
												answerTypeList.add("1");
											}
									%>
									<div>
										<input type="checkbox" value="a" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> /> a) <%=innerlist.get(2)%><br />
											 
										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /> b) <%=innerlist.get(3)%><br />

										<input type="checkbox" value="c"
											name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> /> c) <%=innerlist.get(4)%><br /> 
										
										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /> d) <%=innerlist.get(5)%><br />

										<textarea rows="5" cols="50" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
									</div> 
									<div id="ansType1cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									<%
								 	} else if (uF.parseToInt(innerlist.get(8)) == 2) {
								 		if(!answerTypeList.contains("2")){		
											answerTypeList.add("2");
										}
									 %>
									<div>
										<input type="checkbox" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> a) <%=innerlist.get(2)%><br />

										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /> b) <%=innerlist.get(3)%><br />

										<input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> c) <%=innerlist.get(4)%><br />

										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /> d) <%=innerlist.get(5)%>
									</div>
									<div id="ansType2cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 3) {
									 		if(!answerTypeList.contains("3")){		
												answerTypeList.add("3");
											}
									 %>
									<div>
											<input type="hidden" name="marks<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											id="marks<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width: 31px;"/>
						
									<script>
										$(function() {
											$("#sliderscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider({
												value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(7)%>,
												step : 1,
												slide : function(event, ui) {
													$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").val(ui.value);
													$("#slidemarksscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").val($("#sliderscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider("value"));
											$("#slidemarksscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").html($("#sliderscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider("value"));
										});
									</script>
									<br/>
									<div id="slidemarksscore<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%; text-align:center;"></div>
									<div id="sliderscore<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%; float: left;"></div>
									<div id="marksscore<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(7)%></span></div>
									</div>
									<div id="ansType3cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 4) {
									 		if(!answerTypeList.contains("4")){		
												answerTypeList.add("4");
												}
									 				List<List<String>> outer = answertypeSub.get(innerlist.get(8));
									 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("MARKS") != null && innerMp.get("MARKS").equals(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /> <%=inner.get(1)%><br />
										<% } %>
									</div> 
									<div id="ansType4cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 5) {
									 		if(!answerTypeList.contains("5")){		
												answerTypeList.add("5");
											}
									 		List<List<String>> outer = answertypeSub.get(innerlist.get(8));
									 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /> <%=inner.get(1)%><br />
										<% } %>
									</div> 
									<div id="ansType5cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 6) {
									 		if(!answerTypeList.contains("6")){		
												answerTypeList.add("6");
											}
									 		List<List<String>> outer = answertypeSub.get(innerlist.get(8));
									 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /> <%=inner.get(1)%><br />
										<% } %>
									</div>
									<div id="ansType6cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 7) {
									 		if(!answerTypeList.contains("7")){		
												answerTypeList.add("7");
											}
									 %>
									<div>
										<textarea rows="5" cols="50" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
										<br/>
										<input type="hidden" name="outofmarks<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											id="outofmarks<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" value="<%=innerlist.get(7)%>" />
											
											<input type="hidden" name="marks<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											id="marks<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width: 31px;"/>
						
									<script>
										$(function() {
											$("#slidersingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider({
												value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(7)%>,
												step : 1,
												slide : function(event, ui) {
													$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").val(ui.value);
													$("#slidemarkssingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").val($("#slidersingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider("value"));
											$("#slidemarkssingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").html($("#slidersingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider("value"));
										});
									</script>
									<br/>
									<div id="slidemarkssingleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%; text-align:center;"></div>
									<div id="slidersingleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%; float: left;"></div>						
									<div id="markssingleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(7)%></span></div>
									</div> 
									<div id="ansType7cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 8) {
									 		if(!answerTypeList.contains("8")){		
												answerTypeList.add("8");
											}
									 %>
									<div>
										<input type="radio" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> a) <%=innerlist.get(2)%><br />

										<input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /> b) <%=innerlist.get(3)%><br />

										<input type="radio" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> c) <%=innerlist.get(4)%><br />

										<input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /> d) <%=innerlist.get(5)%><br />
									</div>
									<div id="ansType8cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 9) {
									 		if(!answerTypeList.contains("9")){		
												answerTypeList.add("9");
											}
									 %>
									<div>
										<input type="checkbox" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> a) <%=innerlist.get(2)%><br />

										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /> b) <%=innerlist.get(3)%><br />

										<input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> c) <%=innerlist.get(4)%><br />

										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /> d) <%=innerlist.get(5)%><br />
									</div>
									<div id="ansType9cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 10) {
									 		if(!answerTypeList.contains("10")){		
												answerTypeList.add("10");
											}
									 %>
									 	<%
										 	String[] aa = null;
							 				if (innerMp.get("ANSWER") != null) {
							 					aa = innerMp.get("ANSWER").split(":_:");
									 		}
							 			%>
									<div>
										<div style="float: left; margin: 30px 10px 0px 0px;">a)
										</div>
										<div>
											<textarea rows="5" cols="50" name="a<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%"><%=aa != null ? aa[0] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">b)
										</div>
										<div>
											<textarea rows="5" cols="50" name="b<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%"><%=aa != null ? aa[1] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">c)
										</div>
										<div>
											<textarea rows="5" cols="50" name="c<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%"><%=aa != null ? aa[2] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">d)
										</div>
										<div>
											<textarea rows="5" cols="50" name="d<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%"><%=aa != null ? aa[3] : ""%></textarea>
											<br />
										</div>
										<input type="hidden" name="outofmarks<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											id="outofmarks<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" value="<%=innerlist.get(7)%>" />
											
											<input type="hidden" name="marks<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											id="marks<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width: 31px;"/>
											
									<script>
										$(function() {
											$("#slidermultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider({
												value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(7)%>,
												step : 1,
												slide : function(event, ui) {
													$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").val(ui.value);
													$("#slidemarksmultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").val($("#slidermultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider("value"));
											$("#slidemarksmultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>+"").html($("#slidermultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(9)%>).slider("value"));
										});
									</script>
									<br/>
									<div id="slidemarksmultipleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%; text-align:center;"></div>
									<div id="slidermultipleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%; float: left;"></div>
									<div id="marksmultipleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(7)%></span></div>
											
									</div> 
									<div id="ansType10cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 11) {
									 		if(!answerTypeList.contains("11")){		
												answerTypeList.add("11");
											}
									 				//System.out.println("innerlist.get(0)_innerlist.get(9) ::::: "+innerlist.get(0)+"_"+innerlist.get(9));
									 %>
									<div id="starPrimary<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"></div> 
									<input
									type="hidden" id="gradewithrating<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
									value="<%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / 20 + "" : "0"%>"
									name="gradewithrating<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" />
									<script type="text/javascript">
								        $(function() {
								        	$('#starPrimary<%=innerlist.get(0)%>_<%=innerlist.get(9)%>').raty({
								        		readOnly: false,
								        		start: <%=(innerMp.get("MARKS") != null && innerMp.get("WEIGHTAGE") != null) ? (uF.parseToDouble(innerMp.get("MARKS"))*5) / uF.parseToDouble(innerMp.get("WEIGHTAGE")) + "" : "0"%>,
								        		half: true,
								        		targetType: 'number',
								        		click: function(score, evt) {
								        			$('#gradewithrating<%=innerlist.get(0)%>_<%=innerlist.get(9)%>').val(score);
												}
											});
										});
									</script>
									<div id="ansType11cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 12) { 
									 		if(!answerTypeList.contains("12")) {		
												answerTypeList.add("12");
											}
									%>
									<div>
										<textarea rows="5" cols="50" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
									</div>
									<div id="ansType12cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 14) { 
									 		if(!answerTypeList.contains("14")) {
												answerTypeList.add("14");
											}
									%>
									<div>
										<div>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=innerlist.get(13) %> <br/>
										<input type="radio" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> a) <%=innerlist.get(2)%><br />

										<input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /> b) <%=innerlist.get(3)%><br />

										<input type="radio" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> c) <%=innerlist.get(4)%><br />

										<input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /> d) <%=innerlist.get(5)%><br />
									</div>
									</div> 
									<div id="ansType14cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"></textarea>
									</div>
								<%}%>
								</li>
		   					</ul>
						</li>
					</ul>
				</div>
				<hr>
				<% } %>				
				<div class="addgoaltoreview">
				<fieldset>
					<legend style="font-size: 14px !important;font-weight: bold;">Answer Type Structure</legend>
					<table class="table table_no_border">
						<tr>
						<%
							int k = 1;
							for (int i = 0; i < answerTypeList.size(); i++) {
								List<List<String>> outerList = hmQuestionanswerType.get(answerTypeList.get(i));
						%>
					<td valign="top">
					<table class="table table_no_border">
						<%
							for (int j = 0; outerList != null && j < outerList.size(); j++) {
								List<String> innerlist = (List<String>) outerList.get(j);
						%>
						<tr>
							<% if (j == 0) { %>
							<td><b><%=k++%>).</b></td>
							<% } else { %>
							<td>&nbsp;</td>
							<% } %>
							<td style="text-align: left; min-width: 100px;"><%=innerlist.get(0)%>-<%=innerlist.get(1)%></td>
						</tr>
						<% } %>
						</table>
						</td>
						<% } %>
						</tr>
					</table>
					</fieldset>
				</div>
				<% } %>
				
				<div class="clr">
					<% if (sectionsList.size() == size) { %>
					<script type="text/javascript">
						window.onload = function () {
							var timeDuration = document.getElementById("timeDuration").value; 
						    var fiveMinutes = 60 * parseFloat(timeDuration),
							display = document.querySelector('#time');
						    if(timerId != null){
							    clearTimeout(timerId);
							    timerId = null;
						    }
						    startTimer(fiveMinutes, display);
						};
					</script>					
					<input type="submit" id="submit" name="submit" value="Preview" class="btn btn-primary">	
					<input type="submit" id="btnfinish" name="btnfinish" value="Finish" class="btn btn-primary" onclick="return confirm('Are you sure, you want to finish this?')">
					<% } else { %>
					<input type="submit" id="submit" name="submit" value="Next" class="btn btn-primary">
					<% } %>
				</div>
			</form>
		</div>
		</div>
	</div>
	<div></div>
</div>