<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
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

<%
	List<List<String>> sectionList = (List<List<String>>) request.getAttribute("sectionList");
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	//Map<String, String> hmSectionDetails = (Map<String, String>) request.getAttribute("hmSectionDetails"); 
	//Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
	//String currentLevel = (String) request.getAttribute("currentLevel");
	//Map<String, String> sectionStatus = (Map<String, String>) request.getAttribute("SECTION_STATUS");
	//if (hmSectionDetails == null)hmSectionDetails = new HashMap<String, String>();
	List<String> answerTypeList = new ArrayList<String>();
%>

<script type="text/javascript">
		
	function editAssessmentQueAnswer(queID,assessmentId,lPlanId,empID,userType,queCnt) {
		var dialogEdit = '#modal-body1';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo1").show();
		$('.modal-title1').html('Question Bank');
		$.ajax({
			url : "EditTakeAssessmentQueAnswerPopUp.action?queID="+queID+"&assessmentId="+assessmentId+"&lPlanId="+lPlanId+"&empID="+empID+"&userType="+userType+"&queCnt="+queCnt,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}	
		/* var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Edit Answer');
		 $.ajax({
				url : "EditTakeAssessmentQueAnswerPopUp.action?queID="+queID+"&assessmentId="+assessmentId+"&lPlanId="+lPlanId+"&empID="+empID+"&userType="+userType+"&queCnt="+queCnt,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		} */
	
	
	function editTakeAssessmentQuestion(queAnsId, assessmentId, lPlanId, ansType, assessQueId, queBankId, i) {
		///alert(ansType+" " + assessQueId + " " + queBankId +" " + i);
		//document.getElementsByName("correct"+queBankId+"_"+assessQueId).disabled=false;
		//document.getElementById("correcta"+queBankId+"_"+assessQueId).disabled=false;
		//alert(document.getElementById("correcta"+queBankId+"_"+assessQueId).disabled);
		if(ansType == '1' || ansType == '2' || ansType == '8' || ansType == '9') {
			//alert("ansType ===> " + ansType);
			document.getElementById("correcta"+queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("correctb"+queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("correctc"+queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("correctd"+queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
			if(ansType == '1'){
				document.getElementById(queBankId+"_"+assessQueId).disabled=false;
			}
		} else if(ansType == '3') {
			
			document.getElementById("sliderscore"+ansType+i+"_"+assessQueId).disabled=false;
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
			
		} else if(ansType == '4') {
			var outerList1 = '<%List<List<String>> outer1 = answertypeSub.get("4");%>';
			//alert("outerList1 ===> " + outerList1);
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
		} else if(ansType == '5') {
			var outerList2 = '<%List<List<String>> outer2 = answertypeSub.get("5");%>';
			//alert("outerList2 ===> " + outerList2);
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
		} else if(ansType == '6') {
			var outerList3 = '<%List<List<String>> outer3 = answertypeSub.get("6");%>';
			//alert("outerList3 ===> " + outerList3);
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
		} else if(ansType == '7') {
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
		} else if(ansType == '10') {
			document.getElementById("a"+queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("b"+queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("c"+queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("d"+queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
		} else if(ansType == '11') {
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
		} else if(ansType == '12') {
			document.getElementById(queBankId+"_"+assessQueId).disabled=false;
			document.getElementById("anscomment"+queBankId+"_"+assessQueId).disabled=false;
		}
		
        xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
                alert("Browser does not support HTTP Request");
                return;
        } else {/* 
                var xhr = $.ajax({
                        url : "SetQuestionToTextfield.action?queid=" + queid + '&count=' +count,
                        cache : false,
                        success : function(data) {
                        	if(data == ""){
                        		
                        	}else{
                        		var allData = data.split("::::");
                                document.getElementById("newquespan"+count).innerHTML = allData[0];
                                document.getElementById("answerType"+count).innerHTML = allData[1];
                                if(allData.length > 2){
                                	document.getElementById("answerType1"+count).style.display = 'table-row';
                                	document.getElementById("answerType1"+count).innerHTML = allData[2];
                                }else{
                                	document.getElementById("answerType1"+count).style.display = 'none';
                                }
                        	}
                        }
                }); */
        }
      
}

 function GetXmlHttpObject() {
        if (window.XMLHttpRequest) {
                return new XMLHttpRequest();
        }
        if (window.ActiveXObject) {
                return new ActiveXObject("Microsoft.XMLHTTP");
        }
        return null;
}
	

		$("#TakeAssessmentPreviewFormID").submit(function(e){
			e.preventDefault();
			var form_data = $("#TakeAssessmentPreviewFormID").serialize();
			$("#queAsnDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			var assessmentId = document.getElementById("assessmentId").value;
			var lPlanId = document.getElementById("lPlanId").value;
			var empID = document.getElementById("empID").value;
			$.ajax({
		    	url: "TakeAssessmentPreview.action",
		    	type: "POST",
		    	data: form_data+"&submit=Finish",
		    	success: function(result){
		        	$("#queAsnDiv").html(result);
		    	}, 
	 			error : function(err) {
	 				$.ajax({ 
						url: 'TakeAssessmentSummary.action?assessmentId='+assessmentId+'&lPlanId='+lPlanId+'&empID='+empID,
						cache: true,
						success: function(result){
							$("#queAsnDiv").html(result);
				   		}
					});
	 			}
		    });
		});
	
</script>



<%
Map<String, String> hmEmpList = (Map<String, String>) request.getAttribute("hmEmpList");

CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

String strUserTypeId = (String) session.getAttribute(IConstants.USERTYPEID);
String id=request.getParameter("id");
String empid=request.getParameter("empID");
String lPlanId=request.getParameter("lPlanId");
String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);

 String userType=request.getParameter("userType");
//boolean levelFlag=(Boolean)request.getAttribute("levelFlag");
//boolean existLevelFlag=(Boolean)request.getAttribute("existLevelFlag");



List<String> assessmentList = (List<String>) request.getAttribute("assessmentList");
Map<String, List<List<String>>> hmAssessmentQueData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentQueData");
Map<String, Map<String, String>> questionAnswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionAnswerMp");
//System.out.println("questionList ---> " + questionList);
//System.out.println("assessmentList ---> " + assessmentList);
%>


<div class="leftbox reportWidth">

	<div id="queAsnDiv">
		<div class="addgoaltoreview">
			<h4><%=assessmentList.get(1) %></h4>

			<div style="padding: 5px;font-size: 12px;line-height: 12px;">
				Subject: <%=assessmentList.get(2) %> <br/><br/>
				Author: <%=assessmentList.get(3) %>		
			</div>
		</div>
		<div>
			<%
		 	int size =0 ;
			if(sectionList!= null && sectionList.size() > 0) {
				size = 100 / sectionList.size();
			}
			
		 	String sectionCount = (String)request.getAttribute("sectionCount");
		  	double completePercent =  0;
		 	if(sectionCount !=null && sectionList!= null && sectionList.size() > 0) {
		 		completePercent = (uF.parseToDouble(sectionCount)/uF.parseToDouble(""+sectionList.size()))*100;
		 	}
		 	
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
                Instruction
            </div>
				<%
				if(sectionList!= null && sectionList.size() > 0) {
					size = sectionList.size();
				}
					for (int i = 0; i < sectionList.size(); i++) {								
				%>
				<div class="step-tab">
                  <img src="images1/icons/bullet-green1.png">
               </div>
				<%	
					}
				%>
		</div>
		
		<div class="step-tab-content">
			<s:form action="TakeAssessmentPreview" id="TakeAssessmentPreviewFormID" method="POST" theme="simple">
				<s:hidden name="empID" id="empID"></s:hidden>
				<s:hidden name="lPlanId" id="lPlanId" />
				<s:hidden name="assessmentId" id="assessmentId" />
				<%
					 for(int a=0; sectionList != null && !sectionList.isEmpty() && a<sectionList.size(); a++) {
						 List<String> innerSectionList = sectionList.get(a);
				%>
						
							<div class="addgoaltoreview">
				<h4><%=a+1 %>)&nbsp;<%=uF.showData(innerSectionList.get(1), "")%>
				<input type="hidden" name="hideSubsectionId" id="hideSubsectionId" value="<%=uF.showData(innerSectionList.get(0), "")%>" />
				</h4>

				<div style="width: 70%;font-size: 12px; text-align: justify; line-height: 12px;">
					<%=uF.showData(innerSectionList.get(2), "")%>				
				</div>
				
				<div style="width: 70%;font-size: 12px; text-align: justify; line-height: 12px;">
					Marks for section: <%=uF.showData(innerSectionList.get(3), "")%>				
				</div>
				
				<div style="width: 70%;font-size: 12px; text-align: justify; line-height: 12px;">
					Questions to attempt: Any <%=uF.showData(innerSectionList.get(4), "")%>				
				</div>
				<div class="addgoaltoreview-arrow"></div>
				</div>	
						
			<%
				List<List<String>> questionList =hmAssessmentQueData.get(innerSectionList.get(0));
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
						<li><b><%=a+1 %>.<%=(i + 1)%>)&nbsp;&nbsp;<%=innerlist.get(1)%> </b> 
						<span style="float: right;"><a id="editexist<%=i%>" href="javascript:void(0)" class="edit_lvl" onclick="editAssessmentQueAnswer('<%=innerMp.get("ASSESS_QUE_ANS_ID")%>','<%=assessmentList.get(0) %>','<%=lPlanId %>','<%=empid %>','<%=userType %>','<%=a + 1 %>.<%=(i + 1)%>');" title="Edit/Change Answer"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a></span>
						<%-- <span style="float: right;"><a id="editexist<%=i%>" href="javascript:void(0)" class="edit_lvl" onclick="editTakeAssessmentQuestion('<%=innerMp.get("ASSESS_QUE_ANS_ID")%>','<%=assessmentList.get(0) %>','<%=lPlanId %>','<%=innerlist.get(8) %>','<%=innerlist.get(9) %>','<%=innerlist.get(0) %>','<%=i %>');" title="Edit/Change Answer">Edit</a></span> --%>
						
						</li>
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
										<input type="checkbox" value="a" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correcta<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> disabled="disabled"/> a) <%=innerlist.get(2)%><br /> 
										
										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctb<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" disabled="disabled"/> b) <%=innerlist.get(3)%><br />

										<input type="checkbox" value="c"
											name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctc<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> disabled="disabled"/> c) <%=innerlist.get(4)%><br /> 
										
										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctd<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" disabled="disabled"/> d) <%=innerlist.get(5)%><br />

										<textarea rows="5" cols="50" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%" disabled="disabled"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
									</div> 
									<div id="ansType1cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
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
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correcta<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" disabled="disabled"/> a) <%=innerlist.get(2)%><br />

										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctb<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" disabled="disabled"/> b) <%=innerlist.get(3)%><br />

										<input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctc<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" disabled="disabled"/> c) <%=innerlist.get(4)%><br />

										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctd<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" disabled="disabled"/> d) <%=innerlist.get(5)%>
									</div>
									<div id="ansType2cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
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
												disabled:true,
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
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
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
										<input type="radio" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="<%=j%>_<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").equals(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" disabled="disabled"/><%=inner.get(1)%><br />
										<% } %>
									</div> 
									<div id="ansType4cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 5) {
									 		if(!answerTypeList.contains("5")) {		
												answerTypeList.add("5");
											}
									 		List<List<String>> outer = answertypeSub.get(innerlist.get(8));
									 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="<%=j%>_<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" disabled="disabled"/><%=inner.get(1)%><br />
										<% } %>
									</div> 
									<div id="ansType5cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 6) {
									 		if(!answerTypeList.contains("6")) {		
												answerTypeList.add("6");
											}
									 		List<List<String>> outer = answertypeSub.get(innerlist.get(8));
									 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="<%=j%>_<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" disabled="disabled"/><%=inner.get(1)%><br />
										<% } %>
									</div>
									<div id="ansType6cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 7) {
									 		if(!answerTypeList.contains("7")) {		
												answerTypeList.add("7");
											}
									 %>
									<div>
										<textarea rows="5" cols="50" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%" readonly="readonly"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
										<br />
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
												disabled:true,
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
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 8) {
									 		if(!answerTypeList.contains("8")) {
												answerTypeList.add("8");
											}
									 %>
									<div>
										<input type="radio" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correcta<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" disabled="disabled"/> a) <%=innerlist.get(2)%><br />

										<input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctb<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" disabled="disabled"/> b) <%=innerlist.get(3)%><br />

										<input type="radio" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctc<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" disabled="disabled"/> c) <%=innerlist.get(4)%><br />

										<input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctd<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" disabled="disabled"/> d) <%=innerlist.get(5)%><br />
									</div>
									<div id="ansType8cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 9) {
									 		if(!answerTypeList.contains("9")) {		
												answerTypeList.add("9");
											}
									 %>
									<div>
										<input type="checkbox" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correcta<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" disabled="disabled"/> a) <%=innerlist.get(2)%><br />

										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctb<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" disabled="disabled"/> b) <%=innerlist.get(3)%><br />

										<input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctc<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" disabled="disabled"/> c) <%=innerlist.get(4)%><br />

										<input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="correctd<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" disabled="disabled"/> d) <%=innerlist.get(5)%><br />
									</div>
									<div id="ansType9cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 10) {
									 		if(!answerTypeList.contains("10")) {		
												answerTypeList.add("10");
											}
									 %> <%
									 	String[] aa = null;
									
									 	if (innerMp.get("ANSWER") != null) {
									 		aa = innerMp.get("ANSWER").split(":_:");
									 	}
									 %>
									<div>
										<div style="float: left; margin: 30px 10px 0px 0px;">a)
										</div>
										<div>
											<textarea rows="5" cols="50" name="a<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="a<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%" readonly="readonly"><%=aa != null ? aa[0] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">b)
										</div>
										<div>
											<textarea rows="5" cols="50" name="b<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="b<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%" readonly="readonly"><%=aa != null ? aa[1] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">c)
										</div>
										<div>
											<textarea rows="5" cols="50" name="c<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="c<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%" readonly="readonly"><%=aa != null ? aa[2] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">d)
										</div>
										<div>
											<textarea rows="5" cols="50" name="d<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="d<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%" readonly="readonly"><%=aa != null ? aa[3] : ""%></textarea>
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
												disabled:true,
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
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 11) {
									 		if(!answerTypeList.contains("11")) {		
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
								        		readOnly: true,
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
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 12) { 
									 		if(!answerTypeList.contains("12")) {		
												answerTypeList.add("12");
											}
									%>
												<div>
													<textarea rows="5" cols="50" name="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:100%" disabled="disabled"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
												</div> 
									<div id="ansType12cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" id="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%" readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
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
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" disabled="disabled"/> a) <%=innerlist.get(2)%><br />

										<input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" disabled="disabled"/> b) <%=innerlist.get(3)%><br />

										<input type="radio" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" disabled="disabled"/> c) <%=innerlist.get(4)%><br />

										<input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" disabled="disabled" /> d) <%=innerlist.get(5)%><br />
									</div>
									</div> 
									<div id="ansType14cmnt<%=innerlist.get(0)%>_<%=innerlist.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" style="width:70%"  readonly="readonly"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									
									<%}%>
								</li>
		   					</ul>
						</li>
					</ul>
				</div>
				<hr>
				<% } %>
				<%} %>
				
				<div class="addgoaltoreview">
				<fieldset style="margin: 0px 15px 0px 10px;">
				<legend style="font-size: 14px !important;font-weight: bold;">Answer Type Structure</legend>
				<!-- <div style="float: left; margin-left: 35px;">
				<h2>Answer type structure :</h2>
				</div>
				<br/> -->
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
							<td style="text-align: left; min-width: 100px;"><%=innerlist.get(0)%>-<%=innerlist.get(0)%></td>
						</tr>
						<% } %>
						</table>
						</td>
						<% } %>
						</tr>
					</table>
					</fieldset>
					<!-- <div class="addgoaltoreview-arrow"></div> -->
				</div>
				
				<div class="clr">
					<%-- <%
						if (mainLevelList.size() == size) {
					%>
					<s:submit value="Preview" cssClass="btn btn-primary" name="submit"></s:submit> 
					<s:submit value="Finish" cssClass="btn btn-primary" name="btnfinish" onclick="return confirm('Are you sure, you want to finish this ?')"></s:submit>
					<!-- <input type="button" value="Finish" class="btn btn-primary" name="btnfinish" onclick="finishForm();"/> -->
					<%
						} else {
					%> --%>
					<s:submit value="Finish" cssClass="btn btn-primary" name="submit"></s:submit>
					<%-- <%
						}
					%> --%>
				</div>
			</s:form>
		</div>
		</div>
	</div>
</div>

