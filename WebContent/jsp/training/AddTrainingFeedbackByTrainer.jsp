<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<% UtilityFunctions uF = new UtilityFunctions();%>
<script type="text/javascript">
	
	function changeStatus(id) {
		if (document.getElementById('trainerId' + id).checked == true) {
			document.getElementById('status' + id).value = id;
		} else {
			document.getElementById('status' + id).value = '';
		}
	}
	
</script>

<%
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
	Map<String, String> levelStatus = (Map<String, String>) request.getAttribute("LEVEL_STATUS");
	List<String> answerTypeList = new ArrayList<String>();

	List<List<String>> questionDetailsList = (List<List<String>>) request.getAttribute("questionDetailsList");
	
	List<String> trainerList = (List<String>) request.getAttribute("trainerList");
%>
			
<div class="leftbox reportWidth">
	<div style="width: 100%">
	  <div id="queAsnDiv" style="width: 100%;">
		<s:form action="AddTrainingFeedbackByTrainer" id="frmAddTrainingFeedbackByTrainer"  method="POST" theme="simple">
			<s:hidden name="empID" id="empID" />
			<s:hidden name="id" id="id" />
			<s:hidden name="trainingId"/>
			<s:hidden name="lPlanId" id="lPlanId"/>
			<div>
				<%		
					//System.out.println("questionDetailsList ---> " + questionDetailsList);
					for (int i = 0; questionDetailsList != null && i < questionDetailsList.size(); i++) {
						List<String> innerlist = (List<String>) questionDetailsList.get(i);
						Map<String, String> innerMp = null;
								innerMp = questionanswerMp.get(innerlist.get(1) + "question" + innerlist.get(2));
						if (innerMp == null)
								innerMp = new HashMap<String, String>();
				%>
						<div style="width: 80%; margin: 10px 50px 10px 50px; border-bottom: 1px solid rgb(0, 0, 0);">
							<ul>
								<li><b><%=(i + 1)%>)&nbsp;&nbsp;<%=innerlist.get(3)%></b></li>
								<li>
									<ul style="margin: 10px 10px 10px 30px">
									<li>
									<%
										if (uF.parseToInt(innerlist.get(8)) == 1) {
											if(!answerTypeList.contains("1")) {		
												answerTypeList.add("1");
											}
									%>
											<div>
												a) <input type="checkbox" value="a" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
													checked <%}%> /><%=innerlist.get(4)%><br /> 
												b) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
													checked <%}%> value="b" /><%=innerlist.get(5)%><br />

												c) <input type="checkbox" value="c" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
													checked <%}%> /><%=innerlist.get(6)%><br /> 
												d) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
													checked <%}%> value="d" /><%=innerlist.get(7)%><br />
												<textarea rows="5" cols="50" name="<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" style="width:100%"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
											</div> 
										
									<%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 2) {
									 		if(!answerTypeList.contains("2")){		
												answerTypeList.add("2");
											}
									 %>
											<div>
												a) <input type="checkbox" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
													checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" /><%=innerlist.get(4)%><br />
		
												b) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
													checked <%}%> value="b" /><%=innerlist.get(5)%><br />
		
												c) <input type="checkbox" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
													checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" /><%=innerlist.get(6)%><br />
		
												d) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
													checked <%}%> value="d" /><%=innerlist.get(7)%>
											</div>
									
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 3) {
									 		if(!answerTypeList.contains("3")){		
												answerTypeList.add("3");
											}
									 %>
											<div>
												<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													id="marks<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width: 31px;"/>
								
												<script>
													$(function() {
														$("#sliderscore<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>").slider({
															value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
															min : 0,
															max : <%=innerlist.get(10)%>,
															step : 1,
															slide : function(event, ui) {
																$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").val(ui.value);
																$("#slidemarksscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").html(ui.value);
															}
														});
														$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").val($("#sliderscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>).slider("value"));
														$("#slidemarksscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").html($("#sliderscore"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>).slider("value"));
													});
												</script>
												<br/>
												<div id="slidemarksscore<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%; text-align:center;"></div>
												<div id="sliderscore<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%; float: left;"></div>
												<div id="marksscore<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(10)%></span></div>
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
													for (int j = 0; outer!=null && j < outer.size(); j++) {
														List<String> inner = outer.get(j);
												%>
												<input type="radio" name="<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("MARKS") != null && innerMp.get("MARKS").equals(inner.get(0))) {%>
													checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
												<% } %>
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
											for (int j = 0; outer!=null && j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
												<input type="radio" name="<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
													checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
												<% }%>
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
												for (int j = 0; outer!=null && j < outer.size(); j++) {
													List<String> inner = outer.get(j);
											%>
													<input type="radio" name="<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
														<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
														checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
													<% } %>
										</div>
										 <%
								 	} else if (uF.parseToInt(innerlist.get(8)) == 7) {
								 		if(!answerTypeList.contains("7")){		
											answerTypeList.add("7");
										}
								 %>
										<div>
											<textarea rows="5" cols="50" name="<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
											<br />
											<input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
												id="outofmarks<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" value="<%=innerlist.get(10)%>" />
												
												<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
												id="marks<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width: 31px;"/>
						
											<script>
												$(function() {
													$("#slidersingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>).slider({
														value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
														min : 0,
														max : <%=innerlist.get(10)%>,
														step : 1,
														slide : function(event, ui) {
															$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").val(ui.value);
															$("#slidemarkssingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").html(ui.value);
														}
													});
													$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").val($("#slidersingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>).slider("value"));
													$("#slidemarkssingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").html($("#slidersingleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>).slider("value"));
												});
											</script>
											<br/>
											<div id="slidemarkssingleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%; text-align:center;"></div>
											<div id="slidersingleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%; float: left;"></div>						
											<div id="markssingleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(10)%></span></div>
									</div> 
			<%
								 	} else if (uF.parseToInt(innerlist.get(8)) == 8) {
								 		if(!answerTypeList.contains("8")){		
											answerTypeList.add("8");
										}
								 %>
										<div>
											a) <input type="radio" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
												checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" /><%=innerlist.get(4)%><br />
	
											b) <input type="radio" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
												<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
												checked <%}%> value="b" /><%=innerlist.get(5)%><br />
	
											c) <input type="radio" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
												checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" /><%=innerlist.get(6)%><br />
	
											d) <input type="radio" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
												<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
												checked <%}%> value="d" /><%=innerlist.get(7)%><br />
									  </div>
									
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 9) {
									 		if(!answerTypeList.contains("9")){		
												answerTypeList.add("9");
											}
									 %>
											<div>
												a) <input type="checkbox" value="a"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
													checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" /><%=innerlist.get(4)%><br />
		
												b) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
													checked <%}%> value="b" /><%=innerlist.get(5)%><br />
		
												c) <input type="checkbox" value="c"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
													checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" /><%=innerlist.get(6)%><br />
		
												d) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
													<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
													checked <%}%> value="d" /><%=innerlist.get(7)%><br />
											</div>
									
									 <%
									 	} else if (uF.parseToInt(innerlist.get(8)) == 10) {
									 		if(!answerTypeList.contains("10")){		
												answerTypeList.add("10");
											}
									 %> <%
										 	String[] a = null;
											if (innerMp.get("ANSWER") != null) {
										 			a = innerMp.get("ANSWER").split(":_:");
										  	}
										 %>
										<div>
											<div style="float: left; margin: 30px 10px 0px 0px;">a)</div>
											<div>
												<textarea rows="5" cols="50" name="a<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" style="width:100%"><%=a != null ? a[0] : ""%></textarea>
												<br />
											</div>
											<div style="float: left; margin: 30px 10px 0px 0px;">b)</div>
											<div>
												<textarea rows="5" cols="50" name="b<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" style="width:100%"><%=a != null ? a[1] : ""%></textarea>
												<br />
											</div>
											<div style="float: left; margin: 30px 10px 0px 0px;">c)</div>
											<div>
												<textarea rows="5" cols="50" name="c<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" style="width:100%"><%=a != null ? a[2] : ""%></textarea>
												<br />
											</div>
											<div style="float: left; margin: 30px 10px 0px 0px;">d)</div>
											<div>
												<textarea rows="5" cols="50" name="d<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" style="width:100%"><%=a != null ? a[3] : ""%></textarea>
												<br />
											</div>
											
											<input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
												id="outofmarks<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" value="<%=innerlist.get(10)%>" />
												
												<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
												id="marks<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width: 31px;"/>
											
									<script>
										$(function() {
											$("#slidermultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>).slider({
												value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(10)%>,
												step : 1,
												slide : function(event, ui) {
													$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").val(ui.value);
													$("#slidemarksmultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").val($("#slidermultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>).slider("value"));
											$("#slidemarksmultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>+"").html($("#slidermultipleopen"+<%=innerlist.get(8)+i%>+"_"+<%=innerlist.get(1)%>).slider("value"));
										});
									</script>
									<br/>
									<div id="slidemarksmultipleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%; text-align:center;"></div>
									<div id="slidermultipleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%; float: left;"></div>
									<div id="marksmultipleopen<%=innerlist.get(8)+i%>_<%=innerlist.get(1)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(10)%></span></div>
								</div> 
									
							<%
							 	} else if (uF.parseToInt(innerlist.get(8)) == 11) {
							 		if(!answerTypeList.contains("11")){		
										answerTypeList.add("11");
									}
							 %>
									<div id="starPrimary<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"></div> 
									<input type="hidden" id="gradewithrating<%=innerlist.get(1)%>_<%=innerlist.get(2)%>"
									value="<%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / 20 + "" : "0"%>"
									name="gradewithrating<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" /> 
									<script type="text/javascript">
								        $(function() {
								        	$('#starPrimary<%=innerlist.get(1)%>_<%=innerlist.get(2)%>').raty({
								        		readOnly: false,
								        		start: <%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / 20 + "" : "0"%>,
								        		half: true,
								        		targetType: 'number',
								        		click: function(score, evt) {
							        			$('#gradewithrating<%=innerlist.get(1)%>_<%=innerlist.get(2)%>')
																.val(
																		score);
													}
												});
										});
									</script>
							<%
							 	} else if (uF.parseToInt(innerlist.get(8)) == 12) { 
							 		if(!answerTypeList.contains("12")){		
										answerTypeList.add("12");
									}
								%>
									<div>
										<textarea rows="5" cols="50" name="<%=innerlist.get(1)%>_<%=innerlist.get(2)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
									</div> 
							 <%}%>
						</li>
					</ul>
				</li>
			</ul>
		</div>
	<hr>
<% } %>
	<% if(questionDetailsList == null || questionDetailsList.isEmpty() || questionDetailsList.size() == 0) { %>
            <div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No questions available.</span></div>
    <% } %>
	<div style="float: right";>
		<s:submit value="Submit" cssClass="btn btn-primary" name="btnfinish" onclick="return confirm('Are you sure, you want to submit this?')"></s:submit>
	</div>
</div>
</s:form>
</div>
</div>
</div>
<script>
$("#frmAddTrainingFeedbackByTrainer").submit(function(event){
	event.preventDefault();
	
	var lPlanId = document.getElementById("lPlanId").value;
	
	var form_data = $("#frmAddTrainingFeedbackByTrainer").serialize();
	var submitBtn = $('input[name = "btnfinish"]').val();
	$.ajax({
		type:'POST',
		url:'AddTrainingFeedbackByTrainer.action',
		data:form_data+"&btnfinish="+submitBtn/* ,
		success:function(result){
			$("#divLPDetailsResult").html(result);
		} */
	});
	
	$.ajax({
		url: 'LearningPlanAssessmentStatus.action?lPlanId='+lPlanId,
		cache: true,
		success: function(result){
			$("#divLPDetailsResult").html(result);
   		}
	});
	
});
</script>
