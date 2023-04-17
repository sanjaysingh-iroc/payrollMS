

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script type="text/javascript">
$(function () {
	$("#formEditQuestionDetails_submit").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');	 		
	});
});
	function isNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	      return false;
	   }
	   return true;
	}
	
	function isOnlyNumberKey(evt) {
		var charCode = (evt.which) ? evt.which : event.keyCode;
		if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
			return true; 
		}
		return false;
	}
	
	function validateScoreEdit(value,weightageid,weightagehideid,totweightage) {
		// alert("value1==>"+value1+"==>weightageid==>"+weightageid+"==>weightagehideid==>"+weightagehideid+"==>totweightage==>"+totweightage);
		var singleWeightage = document.getElementById(weightagehideid).value;
		var othertotweight = parseFloat(totweightage) - parseFloat(singleWeightage);
		 
		//  alert("singleWeightage==>"+singleWeightage+"==>othertotweight==>"+othertotweight);
		var remainWeightage = 100 - parseFloat(othertotweight);
		if(parseFloat(value) > parseFloat(remainWeightage)){
			alert("Entered value greater than Weightage");
			document.getElementById(weightageid).value = remainWeightage;
		}else if(parseFloat(value) <= 0 ){
			alert("Invalid Weightage");
			document.getElementById(weightageid).value = remainWeightage;
		}
	}
	
</script>

<%
	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
	Map<String, String> questMp = (Map<String, String>) request.getAttribute("questMp");
	Map<String, Map<String,String>> memberMp =(Map<String, Map<String,String>>) request.getAttribute("memberMp");
	UtilityFunctions uF = new UtilityFunctions();
	String id = (String) request.getAttribute("id");
	String appFreqId = (String) request.getAttribute("appFreqId");
	String fromPage = (String) request.getAttribute("fromPage");
	//System.out.println("GEQD.jsp");
%>

<div id="mainDiv">

	<s:form action="addEditQuestionDetails" id="formEditQuestionDetails" method="POST" theme="simple">
						
		<!-- <div id="mainDiv"
			style="float: left; margin: 10px 0px 0px 0px; width: 100%;"> -->
			<s:hidden name="id"></s:hidden>
			<s:hidden name="appsystem"></s:hidden>
			<s:hidden name="appFreqId"></s:hidden>
			<s:hidden name="scoreType"></s:hidden>
			<s:hidden name="type"></s:hidden>
			<s:hidden name="editID"></s:hidden>
			<s:hidden name="quediv"></s:hidden>
			<s:hidden name="sectionID"></s:hidden>
			<s:hidden name="subsectionID"></s:hidden>
			<s:hidden name="fromPage"></s:hidden>
			<%
				int counter = 0;
					String appsystem = request.getParameter("appsystem"); 
					String scoreType = request.getParameter("scoreType");
					String type = request.getParameter("type");

					if (appsystem != null && appsystem.equals("1")) {
						if (scoreType != null && scoreType.equals("1")) {
							if (type != null && type.equals("score")) {
								//System.out.println("in score jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								//System.out.println("in score jsp scoreList : " + scoreList);
								Map<String, List<List<String>>> scoreMp = scoreList.get(0);
								Map<String, List<List<String>>> measureMp = scoreList.get(1);
								Map<String, List<List<String>>> questionMp = scoreList.get(2);
								Map<String, List<List<String>>> GoalMp = scoreList.get(3);
								Map<String, List<List<String>>> objectiveMp = scoreList.get(4);
								//System.out.println("in score jsp scoreMp : " + scoreMp);
								if (!scoreMp.isEmpty()) {
									Iterator<String> it = scoreMp.keySet().iterator();
									//System.out.println("in score jsp scoreMp it : " + it);
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> scoreOuterList = scoreMp.get(key);
										//System.out.println("in score jsp scoreOuterList : " + scoreOuterList);
										for (int i = 0; scoreOuterList != null && i < scoreOuterList.size(); i++) {
											List<String> scoreInnerList = scoreOuterList.get(i);
											//System.out.println("in score jsp scoreInnerList : " + scoreInnerList);
			%>
			<ul class="">
				<li>
					 <table class="table table_no_border form-table">
						<tr>
							<th width="15%" style="text-align: right;">
							<span style="float: left;"><%=request.getAttribute("queno") %>)</span>Level Type</th>
							<td>Competency <input type="hidden" name="scoreID" value="<%=scoreInnerList.get(0)%>" />
								</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section name<sup>*</sup></th>
							<td><input type="text" name="scoreSectionName" id="scoreSectionName" class="validateRequired" style="width: 450px;" value="<%=scoreInnerList.get(1)%>" />
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="scoreCardDescription" style="width: 450px;"
								value="<%=scoreInnerList.get(2)%>" />
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage %<sup>*</sup></th>
							<td>
							<input type="number" name="scoreCardWeightage" id="scoreCardWeightage<%=counter%>" value="<%=scoreInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'scoreCardWeightage<%=counter%>','hidescoreCardWeightage<%=counter%>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
							<input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=counter%>" value="<%=scoreInnerList.get(3)%>"/>
							</td>
						</tr>
					</table></li>
				<%
					List<List<String>> goalOuterList = GoalMp.get(scoreInnerList.get(0));
					double CGOMtotSGoalWeight = 0;
						for (int j = 0; goalOuterList != null && j < goalOuterList.size(); j++) {
							List<String> goalInnerList = goalOuterList.get(j);
							CGOMtotSGoalWeight += uF.parseToDouble(goalInnerList.get(3));
						}
						//System.out.println("CGOMtotSGoalWeight : "+ CGOMtotSGoalWeight);
							for (int j = 0; goalOuterList != null && j < goalOuterList.size(); j++) {
								List<String> goalInnerList = goalOuterList.get(j);
				%>
				<li>
					<ul class="">
						<li>
							 <table class="table table_no_border form-table">
								<tr>
									<th width="15%" style="text-align: right;" nowrap="nowrap">
									<span style="float: left;"><%=request.getAttribute("queno") %>.<%=j+1 %>)</span>Level Type</th>
									<td>Goals <input type="hidden" name="goalID"
										value="<%=goalInnerList.get(0)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Section name<sup>*</sup></th>
									<td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;" value="<%=goalInnerList.get(1)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Description</th>
									<td><input type="text" name="goalDescription" style="width: 450px;"
										value="<%=goalInnerList.get(2)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Weightage %<sup>*</sup></th>
									<td>
									<input type="text" name="goalWeightage" id="goalWeightage<%=counter%>" class="validate[required,custom[integer]]" value="<%=goalInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'goalWeightage<%=counter%>','hidegoalWeightage<%=counter%>','<%=CGOMtotSGoalWeight %>')" onkeypress="return isNumberKey(event)"/>
									<input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=counter%>" value="<%=goalInnerList.get(3)%>" />
									</td>
								</tr>
							</table>
						</li>
						<%
							List<List<String>> objectiveOuterList = objectiveMp.get(goalInnerList.get(0));
								double CGOMtotSObjectiveWeight = 0;
								for (int k = 0; objectiveOuterList != null && k < objectiveOuterList.size(); k++) {
									List<String> objectiveInnerList = objectiveOuterList.get(k);
									CGOMtotSObjectiveWeight += uF.parseToDouble(objectiveInnerList.get(3));
								}
									for (int k = 0; objectiveOuterList != null && k < objectiveOuterList.size(); k++) {
										List<String> objectiveInnerList = objectiveOuterList.get(k);
										//System.out.println("in score jsp objectiveInnerList : " + objectiveInnerList);
						%>
						<li>
							<ul class="">
								<li>
									 <table class="table table_no_border form-table">
										<tr>
											<th width="15%" style="text-align: right;" nowrap="nowrap">
											<span style="float: left;"><%=request.getAttribute("queno") %>.<%=j+1 %>.<%=k+1 %>)</span>Level Type</th>
											<td>Objective <input type="hidden" name="objectiveID" value="<%=objectiveInnerList.get(0)%>" /></td>
										</tr>
										<tr>
											<th style="text-align: right;">Section name<sup>*</sup></th>
											<td><input type="text" name="objectiveSectionName" id="objectiveSectionName" class="validateRequired" style="width: 450px;"
												value="<%=objectiveInnerList
														.get(1)%>" />
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Description</th>
											<td><input type="text" name="objectiveDescription" style="width: 450px;"
												value="<%=objectiveInnerList
														.get(2)%>" />
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Weightage %<sup>*</sup></th>
											<td>
											<input type="text" name="objectiveWeightage" id="objectiveWeightage<%=counter%>" class="validate[required,custom[integer]]" value="<%=objectiveInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'objectiveWeightage<%=counter%>','hideobjectiveWeightage<%=counter%>','<%=CGOMtotSObjectiveWeight %>')" onkeypress="return isNumberKey(event)"/>
											<input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=counter%>" value="<%=objectiveInnerList.get(3)%>" />
											</td>
										</tr>
									</table></li>
								<%
									List<List<String>> measureMpOuterList = measureMp.get(objectiveInnerList.get(0));
										double CGOMtotSMeasureWeight = 0;
										for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
											List<String> measureMpInnerList = measureMpOuterList.get(a);
											CGOMtotSMeasureWeight += uF.parseToDouble(measureMpInnerList.get(3));
										}
										for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
											List<String> measureMpInnerList = measureMpOuterList.get(a);
											//System.out.println("in score jsp measureMpInnerList : " + measureMpInnerList);
								%>
								<li>
									<ul class="">
										<li>
											 <table class="table table_no_border form-table">
												<tr>
													<th width="15%" style="text-align: right;">
													<span style="float: left;"><%=request.getAttribute("queno") %>.<%=j+1 %>.<%=k+1 %>.<%=a+1 %>)</span>Level Type</th>
													<td>Measures <input type="hidden" name="measureID"
														value="<%=measureMpInnerList.get(0)%>" />
													</td>
												</tr>
												<tr>
													<th style="text-align: right;">Section name<sup>*</sup></th>
													<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"
														value="<%=measureMpInnerList.get(1)%>" />
													</td>
												</tr>
												<tr>
													<th style="text-align: right;">Description</th>
													<td><input type="text" name="measuresDescription" style="width: 450px;"
														value="<%=measureMpInnerList.get(2)%>" />
													</td>
												</tr>
												<tr>
													<th style="text-align: right;">Weightage %<sup>*</sup></th>
													<td>
													<input type="text" name="measureWeightage" id="measureWeightage<%=counter%>" class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter%>','hidemeasureWeightage<%=counter%>','<%=CGOMtotSMeasureWeight %>')" onkeypress="return isNumberKey(event)"/>
													<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter%>" value="<%=measureMpInnerList.get(3)%>" />
													
													</td>
												</tr> 
											</table>
										</li>
										<%
											List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
												double CGOMtotSQueWeight = 0;
												for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
													List<String> questionMpInnerList = questionMpOuterList.get(b);
													CGOMtotSQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
												}
												for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
													List<String> questionMpInnerList = questionMpOuterList.get(b);
										%>
										<li>
											<ul>
												<li>
												
												<table class="table table_no_border form-table">
												<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
													<tr>
													<th><%=request.getAttribute("queno") %>.<%=j+1 %>.<%=k+1 %>.<%=a+1 %>)</th>
													<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
														<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
														<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
														</th>
														<td colspan="3">
														<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
														<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
														<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
														<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
														<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
														</span>
														<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
														<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter%>','hideweightage<%=counter%>','<%=CGOMtotSQueWeight %>')" onkeypress="return isNumberKey(event)"/>
														<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
														</span>
														<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
														<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter %>')"/>
														<input type="hidden" id="status<%=counter %>" name="status"value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
														<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
														<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
														<input type="hidden" name="questiontypename" value="0" /></td>
													</tr>
													
													<%
														int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
														if(getanstype == 1 || getanstype == 2 || getanstype == 8) { %>
														<tr id="answerType<%=counter %>">
														<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
														<%if(questionMpInnerList.get(8).contains("a")){ %>
														checked="checked"
														<%} %>
														/> </td>
														<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
														<%if(questionMpInnerList.get(8).contains("b")){ %>
														checked="checked"
														<%} %>
														/></td>
														</tr>
														<tr id="answerType1<%=counter %>">
														<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
														<%if(questionMpInnerList.get(8).contains("c")){ %>
														checked="checked"
														<%} %>
														/></td>
														<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
														<%if(questionMpInnerList.get(8).contains("d")){ %>
														checked="checked"
														<%} %>
														/></td>
														</tr>
														<% } else if(getanstype == 9) { %>
														<tr id="answerType<%=counter %>">
														<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
														<%if(questionMpInnerList.get(8).contains("a")){ %>
														checked="checked"
														<%} %>
														/> </td>
														<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
														<%if(questionMpInnerList.get(8).contains("b")){ %>
														checked="checked"
														<%} %>
														/></td>
														</tr>
														<tr id="answerType1<%=counter %>">
														<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
														<%if(questionMpInnerList.get(8).contains("c")){ %>
														checked="checked"
														<%} %>
														/></td>
														<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
														<%if(questionMpInnerList.get(8).contains("d")){ %>
														checked="checked"
														<%} %>
														/></td>
														</tr>
														<% } else if(getanstype == 6) { %>
														<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
														<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
														<input type="radio" name="correct<%=counter %>" value="1" 
														<%if(questionMpInnerList.get(8).contains("1")){ %>
														checked="checked"
														<%} %>
														>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
														<%if(questionMpInnerList.get(8).contains("0")){ %>
														checked="checked"
														<%} %>
														>False</td>
														</tr>
														<% } else if(getanstype == 5) { %>
														<tr id="answerType<%=counter %>">
														<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
														<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
														<input type="radio" name="correct<%=counter %>" value="1" 
														<%if(questionMpInnerList.get(8).contains("1")){ %>
														checked="checked"
														<%} %>
														>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
														<%if(questionMpInnerList.get(8).contains("0")){ %>
														checked="checked"
														<%} %>
														>No</td>
														</tr>
														<% } else if(getanstype == 13) { %>
														<tr id="answerType<%=counter %>">
														<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
														<%if(questionMpInnerList.get(8).contains("a")){ %>
														checked="checked"
														<%} %>
														/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
														<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
														<%if(questionMpInnerList.get(8).contains("b")){ %>
														checked="checked"
														<%} %>
														/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
														</tr>
														<tr id="answerType1<%=counter %>">
														<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
														<%if(questionMpInnerList.get(8).contains("c")){ %>
														checked="checked"
														<%} %>
														/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
														<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
														<%if(questionMpInnerList.get(8).contains("d")){ %>
														checked="checked"
														<%} %>
														/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
														</tr>
														<tr id="answerType2<%=counter %>">
														<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
														<%if(questionMpInnerList.get(8).contains("e")){ %>
														checked="checked"
														<%} %>
														/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
														<td colspan="2">&nbsp;</td>
														</tr>
														<%} %>
													
												</table>
										
													
													</li>
											</ul></li>
										<%
											counter++;
																}
										%>
									</ul>
								</li>
								<%
									}
								%>
							</ul></li>

						<%
							}
						%>
					</ul>
				</li>
				<%
					}
				%>
			</ul>

			<%
				}
					}
						}
			%>

			<%
				} else if (type != null && type.equals("goal")) {
								//System.out.println("in goal jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> measureMp = scoreList.get(0);
								Map<String, List<List<String>>> questionMp = scoreList.get(1);
								Map<String, List<List<String>>> GoalMp = scoreList.get(2);
								Map<String, List<List<String>>> objectiveMp = scoreList.get(3);

								if (!GoalMp.isEmpty()) {
									Iterator<String> it = GoalMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> goalOuterList = GoalMp.get(key);
										for (int i = 0; goalOuterList != null && i < goalOuterList.size(); i++) {
											List<String> goalInnerList = goalOuterList.get(i);
			%>
			<ul class="">
				<li>
					 <table class="table table_no_border form-table">
						<tr>
							<th width="15%" style="text-align: right;" nowrap="nowrap">
							<span style="float: left;"><%=request.getAttribute("queno") %>)</span>Level Type</th>
							<td>Goals <input type="hidden" name="goalID"
								value="<%=goalInnerList.get(0)%>" />
								<%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/></span> --%>
								</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section name<sup>*</sup></th>
							<td><input type="text" name="goalSectionName" id="goalSectionName" class="validateRequired" style="width: 450px;"
								value="<%=goalInnerList.get(1)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="goalDescription" style="width: 450px;"
								value="<%=goalInnerList.get(2)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage %<sup>*</sup></th>
							<td>
							<input type="text" name="goalWeightage" id="goalWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=goalInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'goalWeightage<%=counter %>','hidegoalWeightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
							<input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=counter %>" value="<%=goalInnerList.get(3)%>" />
							</td>
						</tr>
					</table>
				</li>
				<%
					List<List<String>> objectiveOuterList = objectiveMp.get(goalInnerList.get(0));
					double CGOMtotGObjectiveWeight = 0;
					for (int k = 0; objectiveOuterList != null && k < objectiveOuterList.size(); k++) {
						List<String> objectiveInnerList = objectiveOuterList.get(k);
						CGOMtotGObjectiveWeight += uF.parseToDouble(objectiveInnerList.get(3));
					}
							for (int k = 0; objectiveOuterList != null && k < objectiveOuterList.size(); k++) {
								List<String> objectiveInnerList = objectiveOuterList.get(k);
				%>
				<li>
					<ul class="">
						<li>
							 <table class="table table_no_border form-table">
								<tr>
									<th width="15%" style="text-align: right;" nowrap="nowrap">
									<span style="float: left;"><%=request.getAttribute("queno") %>.<%=k+1 %>)</span>Level Type</th>
									<td>Objective <input type="hidden" name="objectiveID"
										value="<%=objectiveInnerList.get(0)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Section name<sup>*</sup></th>
									<td><input type="text" name="objectiveSectionName" id="objectiveSectionName" class="validateRequired" style="width: 450px;"
										value="<%=objectiveInnerList.get(1)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Description</th>
									<td><input type="text" name="objectiveDescription" style="width: 450px;"
										value="<%=objectiveInnerList.get(2)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Weightage %<sup>*</sup></th>
									<td>
									<input type="text" name="objectiveWeightage" id="objectiveWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=objectiveInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'objectiveWeightage<%=counter %>','hideobjectiveWeightage<%=counter %>','<%=CGOMtotGObjectiveWeight %>')" onkeypress="return isNumberKey(event)"/>
									<input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=counter %>" value="<%=objectiveInnerList.get(3)%>" />
									</td>
								</tr>
							</table></li>
						<%
							List<List<String>> measureMpOuterList = measureMp.get(objectiveInnerList.get(0));
							double CGOMtotGMeasureWeight = 0;
							for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
								List<String> measureMpInnerList = measureMpOuterList.get(a);
								CGOMtotGMeasureWeight += uF.parseToDouble(measureMpInnerList.get(3));
							}
								for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
									List<String> measureMpInnerList = measureMpOuterList.get(a);
						%>
						<li>
							<ul class="">
								<li>
									 <table class="table table_no_border form-table">
										<tr>
											<th width="15%" style="text-align: right;">
											<span style="float: left;"><%=request.getAttribute("queno") %>.<%=k+1 %>.<%=a+1 %>)</span>Level Type</th>
											<td>Measures <input type="hidden" name="measureID"
												value="<%=measureMpInnerList.get(0)%>" /></td>
										</tr>
										<tr>
											<th style="text-align: right;">Section name<sup>*</sup></th>
											<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"
												value="<%=measureMpInnerList.get(1)%>" />
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Description</th>
											<td><input type="text" name="measuresDescription" style="width: 450px;"
												value="<%=measureMpInnerList.get(2)%>" />
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Weightage %<sup>*</sup></th>
											<td>
											<input type="text" name="measureWeightage" id="measureWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter %>','hidemeasureWeightage<%=counter %>','<%=CGOMtotGMeasureWeight %>')" onkeypress="return isNumberKey(event)"/>
											<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter %>" value="<%=measureMpInnerList.get(3)%>" />
											</td>
										</tr>
									</table>
								</li>
								<%
									List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
									double CGOMtotGQueWeight = 0;
									for (int b = 0; questionMpOuterList != null	&& b < questionMpOuterList.size(); b++) {
										List<String> questionMpInnerList = questionMpOuterList.get(b);
										CGOMtotGQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
									}
										for (int b = 0; questionMpOuterList != null	&& b < questionMpOuterList.size(); b++) {
											List<String> questionMpInnerList = questionMpOuterList.get(b);
								%>
								<li>
									<ul>
										<li>
										
										<table class="table table_no_border form-table">
											<tr>
											<th><%=request.getAttribute("queno") %>.<%=k+1 %>.<%=a+1 %>.<%=b+1 %>)</th>
											<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
											<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
												<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
												<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
												</th>
												<td colspan="3">
												<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
												<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
												<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
												<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
												<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
												</span>
												<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
												<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=CGOMtotGQueWeight %>')" onkeypress="return isNumberKey(event)"/>
												<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
												</span>
												<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
												<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter %>')"/>
												<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
												<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
												<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
												<input type="hidden" name="questiontypename" value="0" /></td>
											</tr>

											<%
												int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
												if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
												<tr id="answerType<%=counter %>">
												<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
												<%if(questionMpInnerList.get(8).contains("a")){ %>
												checked="checked"
												<%} %>
												/> </td>
												<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
												<%if(questionMpInnerList.get(8).contains("b")){ %>
												checked="checked"
												<%} %>
												/></td>
												</tr>
												<tr id="answerType1<%=counter %>">
												<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
												<%if(questionMpInnerList.get(8).contains("c")){ %>
												checked="checked"
												<%} %>
												/></td>
												<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
												<%if(questionMpInnerList.get(8).contains("d")){ %>
												checked="checked"
												<%} %>
												/></td>
												</tr>
												<%}else if(getanstype == 9){ %>
												<tr id="answerType<%=counter %>">
												<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
												<%if(questionMpInnerList.get(8).contains("a")){ %>
												checked="checked"
												<%} %>
												/> </td>
												<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
												<%if(questionMpInnerList.get(8).contains("b")){ %>
												checked="checked"
												<%} %>
												/></td>
												</tr>
												<tr id="answerType1<%=counter %>">
												<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
												<%if(questionMpInnerList.get(8).contains("c")){ %>
												checked="checked"
												<%} %>
												/></td>
												<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
												<%if(questionMpInnerList.get(8).contains("d")){ %>
												checked="checked"
												<%} %>
												/></td>
												</tr>
												<%}else if(getanstype == 6){ %>
												<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
												<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
												<input type="radio" name="correct<%=counter %>" value="1" 
												<%if(questionMpInnerList.get(8).contains("1")){ %>
												checked="checked"
												<%} %>
												>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
												<%if(questionMpInnerList.get(8).contains("0")){ %>
												checked="checked"
												<%} %>
												>False</td>
												</tr>
												<%}else if(getanstype == 5){ %>
												<tr id="answerType<%=counter %>">
												<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
												<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
												<input type="radio" name="correct<%=counter %>" value="1" 
												<%if(questionMpInnerList.get(8).contains("1")){ %>
												checked="checked"
												<%} %>
												>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
												<%if(questionMpInnerList.get(8).contains("0")){ %>
												checked="checked"
												<%} %>
												>No</td>
												</tr>
												<% } else if(getanstype == 13) { %>
												<tr id="answerType<%=counter %>">
												<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
												<%if(questionMpInnerList.get(8).contains("a")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
												<%if(questionMpInnerList.get(8).contains("b")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												</tr>
												<tr id="answerType1<%=counter %>">
												<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
												<%if(questionMpInnerList.get(8).contains("c")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
												<%if(questionMpInnerList.get(8).contains("d")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												</tr>
												<tr id="answerType2<%=counter %>">
												<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
												<%if(questionMpInnerList.get(8).contains("e")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												<td colspan="2">&nbsp;</td>
												</tr>
												<%} %>
										</table>
										
											</li>
									</ul></li>
								<%
									counter++;
																		}
								%>
							</ul>
						</li>
						<%
							}
						%>
					</ul></li>
				<%
					}
				%>
			</ul>

			<%
				}
					}
						}
			%>


			<%
				} else if (type != null && type.equals("objective")) {
								//System.out.println("in objective jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> measureMp = scoreList.get(0);
								Map<String, List<List<String>>> questionMp = scoreList.get(1);
								Map<String, List<List<String>>> objectiveMp = scoreList.get(2);

								if (!objectiveMp.isEmpty()) {
									Iterator<String> it = objectiveMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> objectiveOuterList = objectiveMp.get(key);
										for (int i = 0; objectiveOuterList != null && i < objectiveOuterList.size(); i++) {
											List<String> objectiveInnerList = objectiveOuterList.get(i);
			%>

			<ul class="">
				<li>
					 <table class="table table_no_border form-table">
						<tr>
							<th width="15%" style="text-align: right;" nowrap="nowrap">
							<span style="float: left;"><%=request.getAttribute("queno") %>)</span>Level Type</th>
							<td>Objective <input type="hidden" name="objectiveID"
								value="<%=objectiveInnerList.get(0)%>" />
								<%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/></span> --%>
								</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section name<sup>*</sup></th>
							<td><input type="text" name="objectiveSectionName" id="objectiveSectionName" class="validateRequired" style="width: 450px;"
								value="<%=objectiveInnerList.get(1)%>" />
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="objectiveDescription" style="width: 450px;"
								value="<%=objectiveInnerList.get(2)%>" />
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage %<sup>*</sup></th>
							<td>
							<input type="text" name="objectiveWeightage" id="objectiveWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=objectiveInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'objectiveWeightage<%=counter %>','hideobjectiveWeightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
							<input type="hidden" name="hideobjectiveWeightage" id="hideobjectiveWeightage<%=counter %>" value="<%=objectiveInnerList.get(3)%>" />
							</td>
						</tr>
					</table></li>
				<%
					List<List<String>> measureMpOuterList = measureMp.get(objectiveInnerList.get(0));
					double CGOMtotOMeasureWeight = 0;
					for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
						List<String> measureMpInnerList = measureMpOuterList.get(a);
						CGOMtotOMeasureWeight += uF.parseToDouble(measureMpInnerList.get(3));
					}
							for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
								List<String> measureMpInnerList = measureMpOuterList.get(a);
				%>
				<li>
					<ul class="">
						<li>
							 <table class="table table_no_border form-table">
								<tr>
									<th width="15%" style="text-align: right;">
									<span style="float: left;"><%=request.getAttribute("queno") %>.<%=a+1 %>)</span>Level Type</th>
									<td>Measures <input type="hidden" name="measureID"
										value="<%=measureMpInnerList.get(0)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Section name<sup>*</sup></th>
									<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"
										value="<%=measureMpInnerList.get(1)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Description</th>
									<td><input type="text" name="measuresDescription" style="width: 450px;"
										value="<%=measureMpInnerList.get(2)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Weightage %<sup>*</sup></th>
									<td>
									<input type="text" name="measureWeightage" id="measureWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter %>','hidemeasureWeightage<%=counter %>','<%=CGOMtotOMeasureWeight %>')" onkeypress="return isNumberKey(event)"/>
									<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter %>" value="<%=measureMpInnerList.get(3)%>" />
									</td>
								</tr>
							</table>
						</li>
						<%
							List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
							double CGOMtotOQueWeight = 0;
							for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
								List<String> questionMpInnerList = questionMpOuterList.get(b);
								CGOMtotOQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
							}
									for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
										List<String> questionMpInnerList = questionMpOuterList.get(b);
						%>
						<li>
							<ul>
								<li>
								
								<table class="table table_no_border form-table">
									<tr>
									<th><%=request.getAttribute("queno") %>.<%=a+1 %>.<%=b+1 %>)</th>
									<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
									<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
										<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
										<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
										</th>
										<td colspan="3">
										<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
										<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
										<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
										<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
										<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
										</span>
										<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
										<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=CGOMtotOQueWeight %>')" onkeypress="return isNumberKey(event)"/>
										<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
										</span>
										<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
										<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter %>')"/>
										<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
										<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
										<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
										<input type="hidden" name="questiontypename" value="0" /></td>
									</tr>
									<%
										int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
										if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/> </td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<%}else if(getanstype == 9){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/> </td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<%}else if(getanstype == 6){ %>
										<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
										<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
										<input type="radio" name="correct<%=counter %>" value="1" 
										<%if(questionMpInnerList.get(8).contains("1")){ %>
										checked="checked"
										<%} %>
										>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
										<%if(questionMpInnerList.get(8).contains("0")){ %>
										checked="checked"
										<%} %>
										>False</td>
										</tr>
										<%}else if(getanstype == 5){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
										<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
										<input type="radio" name="correct<%=counter %>" value="1" 
										<%if(questionMpInnerList.get(8).contains("1")){ %>
										checked="checked"
										<%} %>
										>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
										<%if(questionMpInnerList.get(8).contains("0")){ %>
										checked="checked"
										<%} %>
										>No</td>
										</tr>
										<% } else if(getanstype == 13) { %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										</tr>
										<tr id="answerType2<%=counter %>">
										<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
										<%if(questionMpInnerList.get(8).contains("e")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">&nbsp;</td>
										</tr>
										<%} %>
								</table>
												
									</li>
							</ul></li>
						<%
							counter++;
															}
						%>
					</ul>
				</li>
				<%
					}
				%>
			</ul>


			<%
				}
									}
								}
			%>

			<%
				} else if (type != null && type.equals("measure")) {
								//System.out.println("in measure jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> measureMp = scoreList.get(0);
								Map<String, List<List<String>>> questionMp = scoreList.get(1);

								if (!measureMp.isEmpty()) {
									Iterator<String> it = measureMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> measureOuterList = measureMp.get(key);
										for (int i = 0; measureOuterList != null && i < measureOuterList.size(); i++) {
											List<String> measureMpInnerList = measureOuterList.get(i);
			%>

			<ul class="">

				<li>
					<ul class="">
						<li>
							 <table class="table table_no_border form-table">
								<tr>
									<th width="15%" style="text-align: right;">
									<span style="float: left;"><%=request.getAttribute("queno") %>)</span>Level Type</th>
									<td>Measures <input type="hidden" name="measureID"
										value="<%=measureMpInnerList.get(0)%>" />
										<%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/></span> --%>
										</td>
								</tr>
								<tr>
									<th style="text-align: right;">Section name<sup>*</sup></th>
									<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"
										value="<%=measureMpInnerList.get(1)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Description</th>
									<td><input type="text" name="measuresDescription" style="width: 450px;"
										value="<%=measureMpInnerList.get(2)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Weightage %<sup>*</sup></th>
									<td>
									<input type="text" name="measureWeightage" id="measureWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter %>','hidemeasureWeightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
									<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter %>" value="<%=measureMpInnerList.get(3)%>" />
									</td>
								</tr>
							</table>
						</li>
						<%
							List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
							double CGOMtotMQueWeight = 0;
							for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
								List<String> questionMpInnerList = questionMpOuterList.get(b);
								CGOMtotMQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
							}
								for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
									List<String> questionMpInnerList = questionMpOuterList.get(b);
						%>
						<li>
							<ul>
								<li>
								
								<table class="table table_no_border form-table">
									<tr>
									<th><%=request.getAttribute("queno") %>.<%=b+1 %>)</th>
									<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
									<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
										<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
										<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
										</th>
										<td colspan="3">
										<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
										<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
										<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
										<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
										<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
										</span>
										<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
										<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=CGOMtotMQueWeight %>')" onkeypress="return isNumberKey(event)"/>
										<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
										</span>
										<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
										<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %>  onclick="changeStatus('<%=counter %>')"/>
										<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
										<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
										<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
										<input type="hidden" name="questiontypename" value="0" /></td>
									</tr>
									<%
										int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
										if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/> </td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<%}else if(getanstype == 9){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/> </td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<%}else if(getanstype == 6){ %>
										<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
										<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
										<input type="radio" name="correct<%=counter %>" value="1" 
										<%if(questionMpInnerList.get(8).contains("1")){ %>
										checked="checked"
										<%} %>
										>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
										<%if(questionMpInnerList.get(8).contains("0")){ %>
										checked="checked"
										<%} %>
										>False</td>
										</tr>
										<%}else if(getanstype == 5){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
										<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
										<input type="radio" name="correct<%=counter %>" value="1" 
										<%if(questionMpInnerList.get(8).contains("1")){ %>
										checked="checked"
										<%} %>
										>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
										<%if(questionMpInnerList.get(8).contains("0")){ %>
										checked="checked"
										<%} %>
										>No</td>
										</tr>
										<% } else if(getanstype == 13) { %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										</tr>
										<tr id="answerType2<%=counter %>">
										<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
										<%if(questionMpInnerList.get(8).contains("e")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">&nbsp;</td>
										</tr>
										<%} %>
								</table>
								
									</li>
							</ul></li>
						<%
							counter++;
														}
						%>
					</ul>
				</li>

			</ul>


			<%
				}
									}
								}
			%>


			<%
				} else if (type != null && type.equals("quest")) {
					//System.out.println("in quest jsp");
					List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
					Map<String, List<List<String>>> questionMp = scoreList.get(0);
					if (!questionMp.isEmpty()) {
						Iterator<String> it = questionMp.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next();
							List<List<String>> questionMpOuterList = questionMp.get(key);
							for (int i = 0; questionMpOuterList != null && i < questionMpOuterList.size(); i++) {
								List<String> questionMpInnerList = questionMpOuterList.get(i);
			%>

			<ul class="">
				<li>
				
				<table class="table table_no_border form-table">
					<tr>
					<th><%=request.getAttribute("queno") %>)</th>
					<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
					<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
						<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
						<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
						</th>
						<td colspan="3">
						<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
						<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
						<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
						<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
						<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
						</span>
						<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
						<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>"  class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
						<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
						</span>
						<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
						<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %>onclick="changeStatus('<%=counter %>')"/>
						<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
						<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
						<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
						<input type="hidden" name="questiontypename" value="0" /></td>
					</tr>
					<%
						int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
						if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/> </td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<%}else if(getanstype == 9){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/> </td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<%}else if(getanstype == 6){ %>
						<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
						<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
						<input type="radio" name="correct<%=counter %>" value="1" 
						<%if(questionMpInnerList.get(8).contains("1")){ %>
						checked="checked"
						<%} %>
						>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
						<%if(questionMpInnerList.get(8).contains("0")){ %>
						checked="checked"
						<%} %>
						>False</td>
						</tr>
						<%}else if(getanstype == 5){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
						<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
						<input type="radio" name="correct<%=counter %>" value="1" 
						<%if(questionMpInnerList.get(8).contains("1")){ %>
						checked="checked"
						<%} %>
						>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
						<%if(questionMpInnerList.get(8).contains("0")){ %>
						checked="checked"
						<%} %>
						>No</td>
						</tr>
						<% } else if(getanstype == 13) { %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						</tr>
						<tr id="answerType2<%=counter %>">
						<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
						<%if(questionMpInnerList.get(8).contains("e")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">&nbsp;</td>
						</tr>
						<%} %>
				</table>
				
					</li>
			</ul>
			<%
				counter++;
			%>

			<%
				}
					}
						}
			%>

			<%
				}
						} else if (scoreType != null && scoreType.equals("3")) {
							if (type != null && type.equals("score")) {
								//System.out.println("in score jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> scoreMp = scoreList.get(0);
								Map<String, List<List<String>>> measureMp = scoreList.get(1);
								Map<String, List<List<String>>> questionMp = scoreList.get(2);
								Map<String, List<List<String>>> GoalMp = scoreList.get(3);

								if (!scoreMp.isEmpty()) {
									Iterator<String> it = scoreMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> scoreOuterList = scoreMp.get(key);
										for (int i = 0; scoreOuterList != null && i < scoreOuterList.size(); i++) {
											List<String> scoreInnerList = scoreOuterList.get(i);
			%>
			<ul class="">
				<li>
					 <table class="tb_style" style="width: 100%;">
						<tr>
							<th width="15%" style="text-align: right;">
							<span style="float: left;"><%=request.getAttribute("queno") %>)</span>Level Type</th>
							<td>Competency <input type="hidden" name="scoreID"
								value="<%=scoreInnerList.get(0)%>" />
								<%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/></span> --%>
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section name<sup>*</sup></th>
							<td><input type="text" name="scoreSectionName" id="scoreSectionName"  class="validateRequired" style="width: 450px;"
								value="<%=scoreInnerList.get(1)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="scoreCardDescription" style="width: 450px;"
								value="<%=scoreInnerList.get(2)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage %<sup>*</sup></th>
							<td>
							<input type="text" name="scoreCardWeightage" id="scoreCardWeightage<%=counter %>"  class="validate[required,custom[integer]]" value="<%=scoreInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'scoreCardWeightage<%=counter %>','hidescoreCardWeightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
							<input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=counter %>" value="<%=scoreInnerList.get(3)%>" />
							</td>
						</tr>
						<%-- <tr>
							<th style="text-align: right;">Select Attribute</th>
							<td>
								<select name="attribute"><%=request
												.getAttribute("attribute")%></select> <s:select
									name="attribute" list="attributeList" theme="simple"
									listKey="id" id="attribute" listValue="name"
									value="attributevalue"></s:select></td>
						</tr> --%>
					</table>
				</li>
				<%
					List<List<String>> goalOuterList = GoalMp.get(scoreInnerList.get(0));
					double CGMtotSGoalWeight = 0;
					for (int j = 0; goalOuterList != null && j < goalOuterList.size(); j++) {
						List<String> goalInnerList = goalOuterList.get(j);
						CGMtotSGoalWeight += uF.parseToDouble(goalInnerList.get(3));
					}
						for (int j = 0; goalOuterList != null && j < goalOuterList.size(); j++) {
							List<String> goalInnerList = goalOuterList.get(j);
				%>
				<li>
					<ul class="">
						<li>
							 <table class="tb_style" style="width: 100%;">
								<tr>
									<th width="15%" style="text-align: right;" nowrap="nowrap">
									<span style="float: left;"><%=request.getAttribute("queno") %>.<%=j+1 %>)</span>Level Type</th>
									<td>Goals <input type="hidden" name="goalID"
										value="<%=goalInnerList.get(0)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Section name<sup>*</sup></th>
									<td><input type="text" name="goalSectionName" id="goalSectionName"  class="validateRequired" style="width: 450px;"
										value="<%=goalInnerList.get(1)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Description</th>
									<td><input type="text" name="goalDescription" style="width: 450px;"
										value="<%=goalInnerList.get(2)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Weightage %<sup>*</sup></th>
									<td>
									<input type="text" name="goalWeightage" id="goalWeightage<%=counter %>"  class="validate[required,custom[integer]]" value="<%=goalInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'goalWeightage<%=counter %>','hidegoalWeightage<%=counter %>','<%=CGMtotSGoalWeight %>')" onkeypress="return isNumberKey(event)"/>
									<input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=counter %>" value="<%=goalInnerList.get(3)%>" />
									</td>
								</tr>
							</table></li>
						<%
							List<List<String>> measureMpOuterList = measureMp.get(goalInnerList.get(0));
							double CGMtotSMeasureWeight = 0;
							for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
								List<String> measureMpInnerList = measureMpOuterList.get(a);
								CGMtotSMeasureWeight += uF.parseToDouble(measureMpInnerList.get(3));
							}
									for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
										List<String> measureMpInnerList = measureMpOuterList.get(a);
						%>
						<li>
							<ul class="">
								<li>
									 <table class="tb_style" style="width: 100%;">
										<tr>
											<th width="15%" style="text-align: right;">
											<span style="float: left;"><%=request.getAttribute("queno") %>.<%=j+1 %>.<%=a+1 %>)</span>Level Type</th>
											<td>Measures <input type="hidden" name="measureID"
												value="<%=measureMpInnerList
														.get(0)%>" />
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Section name<sup>*</sup></th>
											<td><input type="text" name="measuresSectionName" id="measuresSectionName"  class="validateRequired" style="width: 450px;"
												value="<%=measureMpInnerList.get(1)%>" /></td>
										</tr>
										<tr>
											<th style="text-align: right;">Description</th>
											<td><input type="text" name="measuresDescription" style="width: 450px;"
												value="<%=measureMpInnerList
														.get(2)%>" /></td>
										</tr>
										<tr>
											<th style="text-align: right;">Weightage %<sup>*</sup></th>
											<td>
											<input type="text" name="measureWeightage" id="measureWeightage<%=counter %>"  class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter %>','hidemeasureWeightage<%=counter %>','<%=CGMtotSMeasureWeight %>')" onkeypress="return isNumberKey(event)"/>
											<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter %>" value="<%=measureMpInnerList.get(3)%>" />
											</td>
										</tr>
									</table></li>
								<%
									List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
									double CGMtotSQueWeight = 0;
									for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
										List<String> questionMpInnerList = questionMpOuterList.get(b);
										CGMtotSQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
									}
											for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
												List<String> questionMpInnerList = questionMpOuterList.get(b);
								%>
								<li>
									<ul>
										<li>
										
										<table class="table table_no_border form-table">
										<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
											<tr>
											<th><%=request.getAttribute("queno") %>.<%=j+1 %>.<%=a+1 %>.<%=b+1 %>)</th>
											<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
												<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
												<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
												</th>
												<td colspan="3">
												<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
												<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
												<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
												<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
												<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
												</span>
												<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
												<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=CGMtotSQueWeight %>')" onkeypress="return isNumberKey(event)"/>
												<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
												</span>
												<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
												<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter %>')"/>
												<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
												<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
												<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
												<input type="hidden" name="questiontypename" value="0" /></td>
											</tr>
											<%
												int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
												if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
												<tr id="answerType<%=counter %>">
												<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
												<%if(questionMpInnerList.get(8).contains("a")){ %>
												checked="checked"
												<%} %>
												/> </td>
												<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
												<%if(questionMpInnerList.get(8).contains("b")){ %>
												checked="checked"
												<%} %>
												/></td>
												</tr>
												<tr id="answerType1<%=counter %>">
												<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
												<%if(questionMpInnerList.get(8).contains("c")){ %>
												checked="checked"
												<%} %>
												/></td>
												<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
												<%if(questionMpInnerList.get(8).contains("d")){ %>
												checked="checked"
												<%} %>
												/></td>
												</tr>
												<%}else if(getanstype == 9){ %>
												<tr id="answerType<%=counter %>">
												<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
												<%if(questionMpInnerList.get(8).contains("a")){ %>
												checked="checked"
												<%} %>
												/> </td>
												<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
												<%if(questionMpInnerList.get(8).contains("b")){ %>
												checked="checked"
												<%} %>
												/></td>
												</tr>
												<tr id="answerType1<%=counter %>">
												<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
												<%if(questionMpInnerList.get(8).contains("c")){ %>
												checked="checked"
												<%} %>
												/></td>
												<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
												<%if(questionMpInnerList.get(8).contains("d")){ %>
												checked="checked"
												<%} %>
												/></td>
												</tr>
												<%}else if(getanstype == 6){ %>
												<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
												<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
												<input type="radio" name="correct<%=counter %>" value="1" 
												<%if(questionMpInnerList.get(8).contains("1")){ %>
												checked="checked"
												<%} %>
												>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
												<%if(questionMpInnerList.get(8).contains("0")){ %>
												checked="checked"
												<%} %>
												>False</td>
												</tr>
												<%}else if(getanstype == 5){ %>
												<tr id="answerType<%=counter %>">
												<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
												<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
												<input type="radio" name="correct<%=counter %>" value="1" 
												<%if(questionMpInnerList.get(8).contains("1")){ %>
												checked="checked"
												<%} %>
												>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
												<%if(questionMpInnerList.get(8).contains("0")){ %>
												checked="checked"
												<%} %>
												>No</td>
												</tr>
												<% } else if(getanstype == 13) { %>
												<tr id="answerType<%=counter %>">
												<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
												<%if(questionMpInnerList.get(8).contains("a")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
												<%if(questionMpInnerList.get(8).contains("b")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												</tr>
												<tr id="answerType1<%=counter %>">
												<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
												<%if(questionMpInnerList.get(8).contains("c")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
												<%if(questionMpInnerList.get(8).contains("d")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												</tr>
												<tr id="answerType2<%=counter %>">
												<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
												<%if(questionMpInnerList.get(8).contains("e")){ %>
												checked="checked"
												<%} %>
												/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
												<td colspan="2">&nbsp;</td>
												</tr>
												<%} %>
										</table>
										
										</li>
									</ul>
								</li>
								<%
									counter++;
																		}
								%>
							</ul></li>
						<%
							}
						%> 
					</ul>
				</li>

				<%
					}
				%>
			</ul>

			<%
				}
					}
						}
			%>


			<%
				} else if (type != null && type.equals("goal")) {

								//System.out.println("in goal jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> measureMp = scoreList.get(0);
								Map<String, List<List<String>>> questionMp = scoreList.get(1);
								Map<String, List<List<String>>> GoalMp = scoreList.get(2);

								if (!GoalMp.isEmpty()) {
									Iterator<String> it = GoalMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> goalOuterList = GoalMp.get(key);
										for (int i = 0; goalOuterList != null && i < goalOuterList.size(); i++) {
											List<String> goalInnerList = goalOuterList.get(i);
			%>
			<ul class="">
				<li>
					 <table class="tb_style" style="width: 100%;">
						<tr>
							<th width="15%" style="text-align: right;" nowrap="nowrap">
							<span style="float: left;"><%=request.getAttribute("queno") %>)</span>Level Type</th>
							<td>Goals <input type="hidden" name="goalID"
								value="<%=goalInnerList.get(0)%>" />
								<%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/></span> --%>
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section name<sup>*</sup></th>
							<td><input type="text" name="goalSectionName" style="width: 450px;"
								value="<%=goalInnerList.get(1)%>" />
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="goalDescription" style="width: 450px;"
								value="<%=goalInnerList.get(2)%>" />
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage %<sup>*</sup></th>
							<td>
							<input type="text" name="goalWeightage" id="goalWeightage<%=counter %>" value="<%=goalInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'goalWeightage<%=counter %>','hidegoalWeightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
							<input type="hidden" name="hidegoalWeightage" id="hidegoalWeightage<%=counter %>" value="<%=goalInnerList.get(3)%>" />
							</td>
						</tr>
					</table></li>
				<%
					List<List<String>> measureMpOuterList = measureMp.get(goalInnerList.get(0));
					double CGMtotGMeasureWeight = 0;
					for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
						List<String> measureMpInnerList = measureMpOuterList.get(a);
						CGMtotGMeasureWeight += uF.parseToDouble(measureMpInnerList.get(3));
					}
							for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
								List<String> measureMpInnerList = measureMpOuterList.get(a);
				%>
				<li>
					<ul class="">
						<li>
							 <table class="tb_style" style="width: 100%;">
								<tr>
									<th width="15%" style="text-align: right;">
									<span style="float: left;"><%=request.getAttribute("queno") %>.<%=a+1 %>)</span>Level Type</th>
									<td>Measures <input type="hidden" name="measureID"
										value="<%=measureMpInnerList.get(0)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Section name<sup>*</sup></th>
									<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"
										value="<%=measureMpInnerList.get(1)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Description</th>
									<td><input type="text" name="measuresDescription" style="width: 450px;"
										value="<%=measureMpInnerList.get(2)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Weightage %<sup>*</sup></th>
									<td>
									<input type="text" name="measureWeightage" id="measureWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter %>','hidemeasureWeightage<%=counter %>','<%=CGMtotGMeasureWeight %>')" onkeypress="return isNumberKey(event)"/>
									<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter %>" value="<%=goalInnerList.get(3)%>" />
									</td>
								</tr>
							</table></li>
						<%
							List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
							double CGMtotGQueWeight = 0;
							for (int b = 0; questionMpOuterList != null&& b < questionMpOuterList.size(); b++) {
								List<String> questionMpInnerList = questionMpOuterList.get(b);
								CGMtotGQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
							}
								for (int b = 0; questionMpOuterList != null&& b < questionMpOuterList.size(); b++) {
									List<String> questionMpInnerList = questionMpOuterList.get(b);
						%>
						<li>
							<ul>
								<li>
								
								<table class="table table_no_border form-table">
								<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
									<tr>
									<th><span style="float: left;"><%=request.getAttribute("queno") %>.<%=a+1 %>.<%=b+1 %>)</span></th>
									<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
										<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
										<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
										</th>
										<td colspan="3">
										<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
										<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
										<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
										<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
										<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
										</span>
										<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
										<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=CGMtotGQueWeight %>')" onkeypress="return isNumberKey(event)"/>
										<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
										</span>
										<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
										<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter %>')"/>
										<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
										<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
										<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
										<input type="hidden" name="questiontypename" value="0" /></td>
									</tr>
									<%
										int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
										if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/> </td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<%}else if(getanstype == 9){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/> </td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<%}else if(getanstype == 6){ %>
										<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
										<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
										<input type="radio" name="correct<%=counter %>" value="1" 
										<%if(questionMpInnerList.get(8).contains("1")){ %>
										checked="checked"
										<%} %>
										>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
										<%if(questionMpInnerList.get(8).contains("0")){ %>
										checked="checked"
										<%} %>
										>False</td>
										</tr>
										<%}else if(getanstype == 5){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
										<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
										<input type="radio" name="correct<%=counter %>" value="1" 
										<%if(questionMpInnerList.get(8).contains("1")){ %>
										checked="checked"
										<%} %>
										>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
										<%if(questionMpInnerList.get(8).contains("0")){ %>
										checked="checked"
										<%} %>
										>No</td>
										</tr>
										<% } else if(getanstype == 13) { %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										</tr>
										<tr id="answerType2<%=counter %>">
										<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
										<%if(questionMpInnerList.get(8).contains("e")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">&nbsp;</td>
										</tr>
										<%} %>
								</table>
								
								</li>
							</ul>
						</li>
						<%
							counter++;
															}
						%>
					</ul></li>
				<%
					}
				%>
			</ul>

			<%
				}
					}
						}
			%>


			<%
				} else if (type != null && type.equals("measure")) {

								//System.out.println("in measure jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> measureMp = scoreList.get(0);
								Map<String, List<List<String>>> questionMp = scoreList.get(1);

								if (!measureMp.isEmpty()) {
									Iterator<String> it = measureMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> measureMpOuterList = measureMp.get(key);
										for (int i = 0; measureMpOuterList != null && i < measureMpOuterList.size(); i++) {
											List<String> measureMpInnerList = measureMpOuterList.get(i);
			%>

			<ul class="">
				<li>
					 <table class="tb_style" style="width: 100%;">
						<tr>
							<th width="15%" style="text-align: right;">
							<span style="float: left;"><%=request.getAttribute("queno") %>)</span>Level Type</th>
							<td>Measures <input type="hidden" name="measureID"
								value="<%=measureMpInnerList.get(0)%>" />
								<%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/></span> --%>
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section name<sup>*</sup></th>
							<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"
								value="<%=measureMpInnerList.get(1)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="measuresDescription" style="width: 450px;"
								value="<%=measureMpInnerList.get(2)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage %<sup>*</sup></th>
							<td>
							<input type="text" name="measureWeightage" id="measureWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter %>','hidemeasureWeightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
							<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter %>" value="<%=measureMpInnerList.get(3)%>" />
							</td>
						</tr>
					</table></li>
				<%
					List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
					double CGMtotMQueWeight = 0;
					for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
						List<String> questionMpInnerList = questionMpOuterList.get(b);
						CGMtotMQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
					}
						for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
							List<String> questionMpInnerList = questionMpOuterList.get(b);
				%>
				<li>
					<ul>
						<li>
						
						<table class="table table_no_border form-table">
						<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
							<tr>
							<th><%=request.getAttribute("queno") %>.<%=b+1 %>)</th>
							<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
								<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
								<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
								</th>
								<td colspan="3">
								<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
								<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
								<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
								<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
								<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
								</span>
								<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
								<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=CGMtotMQueWeight %>')" onkeypress="return isNumberKey(event)"/>
								<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
								</span>
								<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
								<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter %>')"/>
								<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
								<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
								<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
								<input type="hidden" name="questiontypename" value="0" /></td>
							</tr>
							<%
								int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
								if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
								<tr id="answerType<%=counter %>">
								<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
								<%if(questionMpInnerList.get(8).contains("a")){ %>
								checked="checked"
								<%} %>
								/> </td>
								<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
								<%if(questionMpInnerList.get(8).contains("b")){ %>
								checked="checked"
								<%} %>
								/></td>
								</tr>
								<tr id="answerType1<%=counter %>">
								<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
								<%if(questionMpInnerList.get(8).contains("c")){ %>
								checked="checked"
								<%} %>
								/></td>
								<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
								<%if(questionMpInnerList.get(8).contains("d")){ %>
								checked="checked"
								<%} %>
								/></td>
								</tr>
								<%}else if(getanstype == 9){ %>
								<tr id="answerType<%=counter %>">
								<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
								<%if(questionMpInnerList.get(8).contains("a")){ %>
								checked="checked"
								<%} %>
								/> </td>
								<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
								<%if(questionMpInnerList.get(8).contains("b")){ %>
								checked="checked"
								<%} %>
								/></td>
								</tr>
								<tr id="answerType1<%=counter %>">
								<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
								<%if(questionMpInnerList.get(8).contains("c")){ %>
								checked="checked"
								<%} %>
								/></td>
								<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
								<%if(questionMpInnerList.get(8).contains("d")){ %>
								checked="checked"
								<%} %>
								/></td>
								</tr>
								<%}else if(getanstype == 6){ %>
								<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
								<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
								<input type="radio" name="correct<%=counter %>" value="1" 
								<%if(questionMpInnerList.get(8).contains("1")){ %>
								checked="checked"
								<%} %>
								>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
								<%if(questionMpInnerList.get(8).contains("0")){ %>
								checked="checked"
								<%} %>
								>False</td>
								</tr>
								<%}else if(getanstype == 5){ %>
								<tr id="answerType<%=counter %>">
								<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
								<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
								<input type="radio" name="correct<%=counter %>" value="1" 
								<%if(questionMpInnerList.get(8).contains("1")){ %>
								checked="checked"
								<%} %>
								>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
								<%if(questionMpInnerList.get(8).contains("0")){ %>
								checked="checked"
								<%} %>
								>No</td>
								</tr>
								<% } else if(getanstype == 13) { %>
								<tr id="answerType<%=counter %>">
								<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
								<%if(questionMpInnerList.get(8).contains("a")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
								<%if(questionMpInnerList.get(8).contains("b")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								</tr>
								<tr id="answerType1<%=counter %>">
								<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
								<%if(questionMpInnerList.get(8).contains("c")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
								<%if(questionMpInnerList.get(8).contains("d")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								</tr>
								<tr id="answerType2<%=counter %>">
								<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
								<%if(questionMpInnerList.get(8).contains("e")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								<td colspan="2">&nbsp;</td>
								</tr>
								<%} %>
						</table>
						
						</li>
					</ul>
				</li>
				<%
					counter++;
												}
				%>
			</ul>


			<%
				}
									}
								}
			%>


			<%
				} else if (type != null && type.equals("quest")) {
								//System.out.println("in quest jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> questionMp = scoreList.get(0);

								if (!questionMp.isEmpty()) {
									Iterator<String> it = questionMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> questionMpOuterList = questionMp.get(key);
										for (int i = 0; questionMpOuterList != null && i < questionMpOuterList.size(); i++) {
											List<String> questionMpInnerList = questionMpOuterList.get(i);
			%>

			<ul class="">
				<li>
				
				<table class="table table_no_border form-table">
				<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
					<tr>
					<th><%=request.getAttribute("queno") %>)</th>
					<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
						<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
						<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
						</th>
						<td colspan="3">
						<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
						<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
						<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
						<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
						<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
						</span>
						<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
						<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
						<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
						</span>
						<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
						<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter %>')"/>
						<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
						<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
						<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
						<input type="hidden" name="questiontypename" value="0" /></td>
					</tr>
					<%
						int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
						if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/> </td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<%}else if(getanstype == 9){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/> </td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<%}else if(getanstype == 6){ %>
						<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
						<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
						<input type="radio" name="correct<%=counter %>" value="1" 
						<%if(questionMpInnerList.get(8).contains("1")){ %>
						checked="checked"
						<%} %>
						>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
						<%if(questionMpInnerList.get(8).contains("0")){ %>
						checked="checked"
						<%} %>
						>False</td>
						</tr>
						<%}else if(getanstype == 5){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
						<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
						<input type="radio" name="correct<%=counter %>" value="1" 
						<%if(questionMpInnerList.get(8).contains("1")){ %>
						checked="checked"
						<%} %>
						>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
						<%if(questionMpInnerList.get(8).contains("0")){ %>
						checked="checked"
						<%} %>
						>No</td>
						</tr>
						<% } else if(getanstype == 13) { %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						</tr>
						<tr id="answerType2<%=counter %>">
						<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
						<%if(questionMpInnerList.get(8).contains("e")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">&nbsp;</td>
						</tr>
						<%} %>
				</table>
												
					</li>
			</ul>
			<%
				counter++;
			%>



			<%
				}
									}
								}
			%>


			<%
				}
						} else if (scoreType != null && scoreType.equals("2")) {
							if (type != null && type.equals("score")) {

								//System.out.println("in score jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> scoreMp = scoreList.get(0);
								Map<String, List<List<String>>> measureMp = scoreList.get(1);
								Map<String, List<List<String>>> questionMp = scoreList.get(2);

								if (!scoreMp.isEmpty()) {
									Iterator<String> it = scoreMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> scoreOuterList = scoreMp.get(key);
										for (int i = 0; scoreOuterList != null && i < scoreOuterList.size(); i++) {
											List<String> scoreInnerList = scoreOuterList.get(i);
			%>
			<ul class="">
				<li>
					 <table class="tb_style" style="width: 100%;">
						<tr>
							<th width="15%" style="text-align: right;">
							<span style="float: left;"><%=request.getAttribute("queno") %>)</span>Level Type</th>
							<td>Competency <input type="hidden" name="scoreID" value="<%=scoreInnerList.get(0)%>" />
								<%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/></span> --%>
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section name</th>
							<td><input type="text" name="scoreSectionName" id="scoreSectionName" class="validateRequired" style="width: 450px;"
								value="<%=scoreInnerList.get(1)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="scoreCardDescription" style="width: 450px;"
								value="<%=scoreInnerList.get(2)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage</th>
							<td>
							<input type="text" name="scoreCardWeightage" id="scoreCardWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=scoreInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'scoreCardWeightage<%=counter %>','hidescoreCardWeightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
							<input type="hidden" name="hidescoreCardWeightage" id="hidescoreCardWeightage<%=counter %>" value="<%=scoreInnerList.get(3)%>" />
							</td>
						</tr>
						<%-- <tr>
							<th style="text-align: right;">Select Attribute</th>
							<td>
								<select name="attribute"><%=request
												.getAttribute("attribute")%></select> <s:select
									name="attribute" list="attributeList" theme="simple"
									listKey="id" id="attribute" listValue="name"
									value="attributevalue"></s:select></td>
						</tr> --%>
					</table>
				</li>

				<%
					List<List<String>> measureMpOuterList = measureMp.get(scoreInnerList.get(0));
						double CMtotSMeasureWeight = 0;
						for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
							List<String> measureMpInnerList = measureMpOuterList.get(a);
							CMtotSMeasureWeight += uF.parseToDouble(measureMpInnerList.get(3));
						}
								for (int a = 0; measureMpOuterList != null && a < measureMpOuterList.size(); a++) {
									List<String> measureMpInnerList = measureMpOuterList.get(a);
				%>
				<li>
					<ul class="">
						<li>
							 <table class="tb_style" style="width: 100%;">
								<tr>
									<th width="15%" style="text-align: right;">
									<span style="float: left;"><%=request.getAttribute("queno") %>.<%=a+1 %>)</span>Level Type</th>
									<td>Measures <input type="hidden" name="measureID"
										value="<%=measureMpInnerList.get(0)%>" />
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Section name<sup>*</sup></th>
									<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"
										value="<%=measureMpInnerList.get(1)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Description</th>
									<td><input type="text" name="measuresDescription" style="width: 450px;"
										value="<%=measureMpInnerList.get(2)%>" /></td>
								</tr>
								<tr>
									<th style="text-align: right;">Weightage %<sup>*</sup></th>
									<td>
									<input type="text" name="measureWeightage" id="measureWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter %>','hidemeasureWeightage<%=counter %>','<%=CMtotSMeasureWeight %>')" onkeypress="return isNumberKey(event)"/>
									<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter %>" value="<%=scoreInnerList.get(3)%>" />
									</td>
								</tr>
							</table></li>
						<%
							List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
							double CMtotSQueWeight = 0;
							for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
								List<String> questionMpInnerList = questionMpOuterList.get(b);
								CMtotSQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
							}
										for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
											List<String> questionMpInnerList = questionMpOuterList.get(b);
						%>
						<li>
							<ul>
								<li>
								
								<table class="table table_no_border form-table">
								<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
									<tr>
									<th><%=request.getAttribute("queno") %>.<%=a+1 %>.<%=b+1 %>)</th>
									<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
										<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
										<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
										</th>
										<td colspan="3">
										<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
										<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
										<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
										<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
										<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
										</span>
										<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
										<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=CMtotSQueWeight %>')" onkeypress="return isNumberKey(event)"/>
										<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
										</span>
										<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
										<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter %>')"/>
										<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
										<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
										<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
										<input type="hidden" name="questiontypename" value="0" /></td>
									</tr>
									<%
										int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
										if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/> </td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<%}else if(getanstype == 9){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/> </td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/></td>
										</tr>
										<%}else if(getanstype == 6){ %>
										<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
										<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
										<input type="radio" name="correct<%=counter %>" value="1" 
										<%if(questionMpInnerList.get(8).contains("1")){ %>
										checked="checked"
										<%} %>
										>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
										<%if(questionMpInnerList.get(8).contains("0")){ %>
										checked="checked"
										<%} %>
										>False</td>
										</tr>
										<%}else if(getanstype == 5){ %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
										<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
										<input type="radio" name="correct<%=counter %>" value="1" 
										<%if(questionMpInnerList.get(8).contains("1")){ %>
										checked="checked"
										<%} %>
										>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
										<%if(questionMpInnerList.get(8).contains("0")){ %>
										checked="checked"
										<%} %>
										>No</td>
										</tr>
										<% } else if(getanstype == 13) { %>
										<tr id="answerType<%=counter %>">
										<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
										<%if(questionMpInnerList.get(8).contains("a")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
										<%if(questionMpInnerList.get(8).contains("b")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
										<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
										<%if(questionMpInnerList.get(8).contains("c")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
										<%if(questionMpInnerList.get(8).contains("d")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										</tr>
										<tr id="answerType2<%=counter %>">
										<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
										<%if(questionMpInnerList.get(8).contains("e")){ %>
										checked="checked"
										<%} %>
										/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
										<td colspan="2">&nbsp;</td>
										</tr>
										<%} %>
								</table>
									
								</li>
							</ul>
						</li>
						<%
							counter++;
															}
						%>
					</ul></li>
				<%
					}
				%>

			</ul>

			<%
				}
					}
						}
			%>

			<%
				} else if (type != null && type.equals("measure")) {
								//System.out.println("in measure jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> measureMp = scoreList.get(0);
								Map<String, List<List<String>>> questionMp = scoreList.get(1);

								if (!measureMp.isEmpty()) {
									Iterator<String> it = measureMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> measureMpOuterList = measureMp.get(key);
										for (int i = 0; measureMpOuterList != null && i < measureMpOuterList.size(); i++) {
											List<String> measureMpInnerList = measureMpOuterList.get(i);
			%>


			<ul class="">
				<li>
					 <table class="tb_style" style="width: 100%;">
						<tr>
							<th width="15%" style="text-align: right;"><span style="float: left;"><%=request.getAttribute("queno") %></span> Level Type</th>
							<td>Measures <input type="hidden" name="measureID"
								value="<%=measureMpInnerList.get(0)%>" />
								<%-- <span style="float: right"><img border="0" style="padding: 5px 5px 0pt; height: 18px; width: 18px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/></span> --%>
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section name<sup>*</sup></th>
							<td><input type="text" name="measuresSectionName" id="measuresSectionName" class="validateRequired" style="width: 450px;"
								value="<%=measureMpInnerList.get(1)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="measuresDescription" style="width: 450px;"
								value="<%=measureMpInnerList.get(2)%>" /></td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage %<sup>*</sup></th>
							<td>
							<input type="text" name="measureWeightage" id="measureWeightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=measureMpInnerList.get(3)%>" onkeyup="validateScoreEdit(this.value,'measureWeightage<%=counter %>','hidemeasureWeightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
							<input type="hidden" name="hidemeasureWeightage" id="hidemeasureWeightage<%=counter %>" value="<%=measureMpInnerList.get(3)%>" />
							</td>
						</tr>
					</table></li>
				<%
					List<List<String>> questionMpOuterList = questionMp.get(measureMpInnerList.get(0));
					double CMtotMQueWeight = 0;
					for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
						List<String> questionMpInnerList = questionMpOuterList.get(b);
						CMtotMQueWeight += uF.parseToDouble(questionMpInnerList.get(1));
					}
							for (int b = 0; questionMpOuterList != null && b < questionMpOuterList.size(); b++) {
								List<String> questionMpInnerList = questionMpOuterList.get(b);
				%>
				<li>
					<ul>
						<li>
						
						<table class="table table_no_border form-table">
						<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
							<tr>
							<th><%=request.getAttribute("queno") %>.<%=b+1 %>)</th>
							<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
								<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
								<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
								</th>
								<td colspan="3">
								<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
								<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
								<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
								<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
								<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
								</span>
								<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
								<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=CMtotMQueWeight %>')" onkeypress="return isNumberKey(event)"/>
								<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
								</span>
								<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
								<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %>  onclick="changeStatus('<%=counter %>')"/>
								<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
								<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
								<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
								<input type="hidden" name="questiontypename" value="0" /></td>
							</tr>
							<%
								int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
								if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
								<tr id="answerType<%=counter %>">
								<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
								<%if(questionMpInnerList.get(8).contains("a")){ %>
								checked="checked"
								<%} %>
								/> </td>
								<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
								<%if(questionMpInnerList.get(8).contains("b")){ %>
								checked="checked"
								<%} %>
								/></td>
								</tr>
								<tr id="answerType1<%=counter %>">
								<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
								<%if(questionMpInnerList.get(8).contains("c")){ %>
								checked="checked"
								<%} %>
								/></td>
								<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
								<%if(questionMpInnerList.get(8).contains("d")){ %>
								checked="checked"
								<%} %>
								/></td>
								</tr>
								<%}else if(getanstype == 9){ %>
								<tr id="answerType<%=counter %>">
								<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
								<%if(questionMpInnerList.get(8).contains("a")){ %>
								checked="checked"
								<%} %>
								/> </td>
								<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
								<%if(questionMpInnerList.get(8).contains("b")){ %>
								checked="checked"
								<%} %>
								/></td>
								</tr>
								<tr id="answerType1<%=counter %>">
								<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
								<%if(questionMpInnerList.get(8).contains("c")){ %>
								checked="checked"
								<%} %>
								/></td>
								<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
								<%if(questionMpInnerList.get(8).contains("d")){ %>
								checked="checked"
								<%} %>
								/></td>
								</tr>
								<%}else if(getanstype == 6){ %>
								<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
								<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
								<input type="radio" name="correct<%=counter %>" value="1" 
								<%if(questionMpInnerList.get(8).contains("1")){ %>
								checked="checked"
								<%} %>
								>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
								<%if(questionMpInnerList.get(8).contains("0")){ %>
								checked="checked"
								<%} %>
								>False</td>
								</tr>
								<%}else if(getanstype == 5){ %>
								<tr id="answerType<%=counter %>">
								<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
								<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
								<input type="radio" name="correct<%=counter %>" value="1" 
								<%if(questionMpInnerList.get(8).contains("1")){ %>
								checked="checked"
								<%} %>
								>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
								<%if(questionMpInnerList.get(8).contains("0")){ %>
								checked="checked"
								<%} %>
								>No</td>
								</tr>
								<% } else if(getanstype == 13) { %>
								<tr id="answerType<%=counter %>">
								<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
								<%if(questionMpInnerList.get(8).contains("a")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
								<%if(questionMpInnerList.get(8).contains("b")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								</tr>
								<tr id="answerType1<%=counter %>">
								<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
								<%if(questionMpInnerList.get(8).contains("c")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
								<%if(questionMpInnerList.get(8).contains("d")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								</tr>
								<tr id="answerType2<%=counter %>">
								<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
								<%if(questionMpInnerList.get(8).contains("e")){ %>
								checked="checked"
								<%} %>
								/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
								<td colspan="2">&nbsp;</td>
								</tr>
								<%} %>
						</table>
						
						</li>
					</ul>
				</li>
				<%
					counter++;
												}
				%>
			</ul>
			</li>

			<%
				}
					}
						}
			%>


			<%
				} else if (type != null && type.equals("quest")) {
								//System.out.println("in quest jsp");
								List<Map<String, List<List<String>>>> scoreList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
								Map<String, List<List<String>>> questionMp = scoreList.get(0);
								if (!questionMp.isEmpty()) {
									Iterator<String> it = questionMp.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next();
										List<List<String>> questionMpOuterList = questionMp.get(key);
										for (int i = 0; questionMpOuterList != null && i < questionMpOuterList.size(); i++) {
											List<String> questionMpInnerList = questionMpOuterList.get(i);
			%>

			<ul class="">
				<li>
				
				<table class="table table_no_border form-table">
				<%-- <th><%=a+1%>.<%=i+1%>.1)</th> --%>
					<tr>
					<th><%=request.getAttribute("queno") %></th>
					<th width="17%" style="text-align: right;">Add Question<sup>*</sup>
						<%-- <input type="hidden" name="queanstype" id="queanstype<%=a %>_<%=i %>s" value="<%=queAnstype%>"/>
						<input type="hidden" name="sectionattribute" id="sectionattribute<%=a %>_<%=i %>s" value="<%=sectionattribute %>"/> --%>
						</th>
						<td colspan="3">
						<input type="hidden" name="queanstype" id="queanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
						<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>" />
						<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
						<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
						<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
						</span>
						<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
						<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter %>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1) %>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter %>','hideweightage<%=counter %>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
						<input type="hidden" name="hideweightage" id="hideweightage<%=counter %>" value="<%=questionMpInnerList.get(1) %>" />
						</span>
						<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter %>','editQue');" > +Q </a></span>
						<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %>  onclick="changeStatus('<%=counter %>')"/>
						<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
						<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
						<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
						<input type="hidden" name="questiontypename" value="0" /></td>
					</tr>
					<%
						int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
						if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/> </td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<%}else if(getanstype == 9){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/> </td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<%}else if(getanstype == 6){ %>
						<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
						<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
						<input type="radio" name="correct<%=counter %>" value="1" 
						<%if(questionMpInnerList.get(8).contains("1")){ %>
						checked="checked"
						<%} %>
						>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
						<%if(questionMpInnerList.get(8).contains("0")){ %>
						checked="checked"
						<%} %>
						>False</td>
						</tr>
						<%}else if(getanstype == 5){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
						<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
						<input type="radio" name="correct<%=counter %>" value="1" 
						<%if(questionMpInnerList.get(8).contains("1")){ %>
						checked="checked"
						<%} %>
						>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
						<%if(questionMpInnerList.get(8).contains("0")){ %>
						checked="checked"
						<%} %>
						>No</td>
						</tr>
						<% } else if(getanstype == 13) { %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						</tr>
						<tr id="answerType2<%=counter %>">
						<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
						<%if(questionMpInnerList.get(8).contains("e")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">&nbsp;</td>
						</tr>
						<%} %>
				</table>
				
					</li>
			</ul>
			<%
				counter++;
			%>

			<%
				}
							}
						}
			%>

			<%
				}
						}
					} else if (appsystem != null && appsystem.equals("2")) {
						//System.out.println("in quest jsp");
						List<Map<String, List<List<String>>>> otherQuestionList = (List<Map<String, List<List<String>>>>) request.getAttribute("score1");
						Map<String, List<List<String>>> questionMp = otherQuestionList.get(0);
						if (!questionMp.isEmpty()) {
							Iterator<String> it = questionMp.keySet().iterator();
							while (it.hasNext()) {
								String key = it.next();
								double OTHERtotQueWeight = 0;
								List<List<String>> questionMpOuterList = questionMp.get(key);
								for (int i = 0; questionMpOuterList != null && i < questionMpOuterList.size(); i++) {
									List<String> questionMpInnerList = questionMpOuterList.get(i);
									//System.out.println("questionMpInnerList.get(2)====>" + questionMpInnerList.get(2));
			%>

			<ul class="">
				<li>
				
				<table class="table table_no_border form-table">
					<tr><th><%=request.getAttribute("queno") %></th><th width="17%" style="text-align: right;">Add Question<sup>*</sup></th>
					<td colspan="3">
					<input type="hidden" name="othrqueanstype" id="othrqueanstype<%=counter %>" value="<%=request.getAttribute("selectanstype") %>"/>
					<input type="hidden" name="questionID" value="<%=questionMpInnerList.get(2)%>"/>
					<span id="newquespan<%=counter %>" style="float: left;"><input type="hidden" name="hidequeid" id="hidequeid<%=counter %>" value="<%=questionMpInnerList.get(3) %>"/>
					<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;"><%=questionMpInnerList.get(0) %></textarea>
					<%-- <input type="text" name="question" id="question<%=counter %>" style="width: 330px;" value="<%=questionMpInnerList.get(0) %>"/> --%>
					</span>
					<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/><sup>*</sup>
					<input type="text" style="width: 35px !important;" name="weightage" id="weightage<%=counter%>" class="validate[required,custom[integer]]" value="<%=questionMpInnerList.get(1)%>" onkeyup="validateScoreEdit(this.value,'weightage<%=counter%>','hideweightage<%=counter%>','<%=request.getAttribute("totWeightage") %>')" onkeypress="return isNumberKey(event)"/>
					<input type="hidden" style="width: 35px !important;" name="hideweightage" id="hideweightage<%=counter%>" value="<%=questionMpInnerList.get(1)%>" />
					</span>
					<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter%>','editQue');" > +Q </a></span>
					<%if(request.getAttribute("othrquetype").equals("With Short Description")){ %>
					<span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" onclick="openOtheQueShortD('<%=counter %>')" > D </a></span>
					<%}else{ %>
					<span style="float: left; margin-left: 10px;"> D </span>
					<%} %>
					<span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" title="Add to Question Bank" <%if(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) { %>" checked="checked" <%} %> onclick="changeStatus('<%=counter%>')"/>
					<input type="hidden" id="status<%=counter %>" name="status" value="<%=(questionMpInnerList.get(10) != null && uF.parseToBoolean(questionMpInnerList.get(10))) ? "1" : "0" %>"/></span>
					<%-- <a href="javascript:void(0)" class="add_lvl" title="Add New Question" onclick="getOtherquestion()" ></a>&nbsp;&nbsp;
					<img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images/close_pop.png" title="Remove Question" onclick="removeOtherquestion('otherQuestionUl<%=a %>_<%=i %>')"/> --%>
					<input type="hidden" name="questiontypename" value="0" /></td></tr>
				<!-- ===start parvez date: 22-02-2023=== -->	
					<%-- <tr id="shortdescTr<%=counter%>" style="display: <%=(questionMpInnerList.get(17) != null) ? "table-row" : "none" %>;"><th>&nbsp;</th><th style="text-align: right;">Short Description</th><td colspan="3"><input type="hidden" name="hideotherSD" id="hideotherSD<%=counter %>" value="f"/> --%>
					<tr id="shortdescTr<%=counter%>" style="display: <%=(questionMpInnerList.get(17) != null && !request.getAttribute("othrquetype").equals("Without Short Description")) ? "table-row" : "none" %>;"><th>&nbsp;</th><th style="text-align: right;">Short Description</th><td colspan="3"><input type="hidden" name="hideotherSD" id="hideotherSD<%=counter %>" value="f"/>
				<!-- ===end parvez date: 22-02-2023=== -->	
					<input type="text" name="otherSDescription" id="otherSDescription" style="width: 450px;" value="<%=questionMpInnerList.get(17) %>" /></td></tr>
					<%
						int getanstype = uF.parseToInt((String)request.getAttribute("selectanstype"));
						if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/> </td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<%}else if(getanstype == 9){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/> </td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/></td>
						</tr>
						<%}else if(getanstype == 6){ %>
						<tr id="answerType<%=counter %>"><th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
						<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
						<input type="radio" name="correct<%=counter %>" value="1" 
						<%if(questionMpInnerList.get(8).contains("1")){ %>
						checked="checked"
						<%} %>
						>True&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
						<%if(questionMpInnerList.get(8).contains("0")){ %>
						checked="checked"
						<%} %>
						>False</td>
						</tr>
						<%}else if(getanstype == 5){ %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td><input type="hidden" name="optiona" value="<%=questionMpInnerList.get(4)%>"/><input type="hidden" name="optionb" value="<%=questionMpInnerList.get(5)%>"/>
						<input type="hidden" name="optionc" value="<%=questionMpInnerList.get(6)%>"/><input type="hidden" name="optiond" value="<%=questionMpInnerList.get(7)%>"/>
						<input type="radio" name="correct<%=counter %>" value="1" 
						<%if(questionMpInnerList.get(8).contains("1")){ %>
						checked="checked"
						<%} %>
						>Yes&nbsp; <input type="radio" name="correct<%=counter %>" value="0" 
						<%if(questionMpInnerList.get(8).contains("0")){ %>
						checked="checked"
						<%} %>
						>No</td>
						</tr>
						<% } else if(getanstype == 13) { %>
						<tr id="answerType<%=counter %>">
						<th></th><th></th><td>a)&nbsp;<input type="text" name="optiona" value="<%=questionMpInnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
						<%if(questionMpInnerList.get(8).contains("a")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptiona" value="<%=questionMpInnerList.get(12)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=questionMpInnerList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
						<%if(questionMpInnerList.get(8).contains("b")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptionb" value="<%=questionMpInnerList.get(13)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						</tr>
						<tr id="answerType1<%=counter %>">
						<th></th><th></th><td>c)&nbsp;<input type="text" name="optionc" value="<%=questionMpInnerList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
						<%if(questionMpInnerList.get(8).contains("c")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptionc" value="<%=questionMpInnerList.get(14)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=questionMpInnerList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
						<%if(questionMpInnerList.get(8).contains("d")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptiond" value="<%=questionMpInnerList.get(15)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						</tr>
						<tr id="answerType2<%=counter %>">
						<th></th><th></th><td>e)&nbsp;<input type="text" name="optione" value="<%=questionMpInnerList.get(11)%>"/> <input type="radio" name="correct<%=counter %>" value="e"
						<%if(questionMpInnerList.get(8).contains("e")){ %>
						checked="checked"
						<%} %>
						/>&nbsp;Rate: <input type="text" name="rateoptione" value="<%=questionMpInnerList.get(16)%>" style="width: 20px !important;" onkeypress="return isOnlyNumberKey(event)"/></td>
						<td colspan="2">&nbsp;</td>
						</tr>
						<% } %>
				</table>
					</li>
			</ul>
			<%
				counter++;
			%>

			<%
				}
							}
						}
			%>

			<%
				}
			%>
			
		<!-- </div> -->
		
		<div align="center">
				<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
			</div>
	</s:form>
</div>

<script>
 $("#formEditQuestionDetails").submit(function(event){
	 event.preventDefault();
	 var form_data = $("#formEditQuestionDetails").serialize();
		$.ajax({ 
			type : 'POST',
			url: "addEditQuestionDetails.action",
			data: form_data+"&submit=Save",
			cache: true,
			success: function(result){
				getReviewSummary('AppraisalSummary','<%=id%>','<%=appFreqId%>','<%=fromPage%>');
	   		},
			error: function(result){
				getReviewSummary('AppraisalSummary','<%=id%>','<%=appFreqId %>','<%=fromPage %>');
			}
		});
	 
 });
	
</script>
