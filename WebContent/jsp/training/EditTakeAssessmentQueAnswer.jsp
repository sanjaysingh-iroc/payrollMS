<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>



<style>
.Gray12 {
	font: 12px;
	color: #999999;
}

.Box1 {
	width: 30px;
	height: 70px;
}

.ML {
	margin-left: 120px;
}

.MB {
	margin-bottom: 50px;
}

.W14 {
	font: 14px;
	color: #fff;
}

.greenbutton {
	width: 8px;
	height: 8px;
	background-color: #bcd29d;
	line-height: 8px;
	-moz-border-radius: 15px;
	-webkit-border-radius: 15px;
	border-radius: 15px;
	border: 1px solid #a0a0a0;
	padding: 5px;
}

.topDiv {
	background: #f8f8f8;
	border: 1px #adadad solid;
	height: 25%;
	width: 99.9%;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	-o-border-radius: 5px;
	border-radius: 5px;
	margin-top: 25px
}

.topDiv table tr td {
	vertical-align: middle;
}

.FL {
	float: left;
}

.addgoaltoreview {
	background-color: #ECF3F8;
	border-bottom: 1px solid #DDDDDD;
	box-shadow: 0 17px 21px -6px #CCCCCC;
	display: inline-block;
	padding: 10px 0;
	position: relative;
	text-align: center;
	width: 99.8%;
}

.addgoaltoreview-arrow {
	border-color: #ecf3f8 transparent transparent transparent;
	border-style: solid;
	border-width: 10px;
	height: 0;
	width: 0;
	position: absolute;
	bottom: -19px;
	left: 50%;
}

.addgoaltoreview h3 {
	float: left;
	padding: 10px 20px;
	width: 100%;
	text-align: left;
}

.addgoaltoreview input {
	font-weight: 700;
	float: right;
}

div.reviewbar {
	background: #f8f8f8;
	border: solid 2px #ccc;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	border-radius: 5px;
	clear: both;
	display: inline-block;
	margin: 10px 0;
	width: 99.8%;
	height: 35px;
}

div.reviewbar ul li {
	border-right: 2px solid #ccc;
	float: left;
	height: 35px;
}

div.reviewbar ul li {
	border-bottom: 4px solid #86B600;
}

div.reviewbar ul li.col3 {
	height: 35px;
	padding: 0px;
	width: 140px;
	border-bottom: 4px solid #86B600;
}

div.reviewbar ul li.col5 {
	border-right: 2px solid #ccc;
	border: none;
	float: right;
}

div.reviewbar ul li.col5 span {
	display: inline-block;
	padding: 12px 0;
}

div.reviewbar div.customerreview { /* Safari 4-5, Chrome 1-9 */
	background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#000000),
		to(#757474) );
	/* Safari 5.1, Chrome 10+ */
	background: -webkit-linear-gradient(top, #757474, #000000);
	/* Firefox 3.6+ */
	background: -moz-linear-gradient(top, #757474, #000000);
	/* IE 10 */
	background: -ms-linear-gradient(top, #757474, #000000);
	/* Opera 11.10+ */
	background: -o-linear-gradient(top, #757474, #000000);
	border-radius: 5px 0 0 5px;
	color: #FFFFFF;
	height: 35px;
	line-height: 35px;
	text-align: center;
	width: 150px
}

.reportWidth1 {
    background: none repeat scroll 0 0 #FFFFFF;
    border: 1px solid #A1A1A1;
    border-radius: 4px 4px 4px 4px;
    box-shadow: 0 0 0 #CCCCCC;
    float: left;
    margin: 10px 5px;
    padding: 10px 10px 20px;
    width: 97%; 
}

</style>

<%
	Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
	Map<String, String> innerMp = (Map<String, String>) request.getAttribute("innerMp");
	List<String> answerTypeList = new ArrayList<String>();
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");

	UtilityFunctions uF = new UtilityFunctions();
	String empID = (String)session.getAttribute("empID");
	String assessmentId = (String)request.getAttribute("assessmentId");
	String lPlanId = (String)request.getAttribute("lPlanId");
	
	String userType = (String)request.getAttribute("userType");

%>

<div class="reportWidth1">

	<s:form action="EditTakeAssessmentQueAnswer" id="formID1" method="POST" theme="simple">
		<s:hidden name="assessmentId"/>
		<s:hidden name="lPlanId"/>
		<s:hidden name="empID"/>
		<s:hidden name="userType"/>
		<s:hidden name="queID"/>
		<% List<String> questioninnerList = hmQuestion.get(innerMp.get("QUESTION_ID")); %>
		<div style="margin: 10px 25px 10px 25px; width:96%; border-bottom: 1px solid #000;">
			<ul>
				<li><b><%=request.getAttribute("queCnt") %>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b> </li>
				<li>
					<ul style="margin: 10px 10px 10px 30px">
						<li>
							<% if (uF.parseToInt(questioninnerList.get(8)) == 1) { %>
							<div>
								<input type="checkbox" value="a" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
									checked <%}%> /> a) <%=questioninnerList.get(2)%><br /> 
								
								<input type="checkbox" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
									checked <%}%> value="b" /> b) <%=questioninnerList.get(3)%><br />

								<input type="checkbox" value="c" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
									checked <%}%> /> c) <%=questioninnerList.get(4)%><br /> 
								
								<input type="checkbox" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
									checked <%}%> value="d" /> d) <%=questioninnerList.get(5)%><br />

								<textarea rows="5" cols="50" style="width:100%" name="remark<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
					 				answerTypeList.add("2");
							 %>

							<div>
								<input type="checkbox" value="a"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
									checked <%}%> name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" /> a) <%=questioninnerList.get(2)%><br />

								<input type="checkbox" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
									checked <%}%> value="b" /> b) <%=questioninnerList.get(3)%><br />

								<input type="checkbox" value="c"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
									checked <%}%> name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" /> c) <%=questioninnerList.get(4)%><br />

								<input type="checkbox" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
									checked <%}%> value="d" /> d) <%=questioninnerList.get(5)%>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
					 				answerTypeList.add("3");
							 %>
							<div>
								<input type="hidden" name="marks<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" id="marks<%=questioninnerList.get(8)%>_<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width: 31px;"/>
								<script>
									$(function() {
										$("#sliderscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider({
											value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
											min : 0,
											max : <%=innerMp.get("WEIGHTAGE")%>,
											step : 1,
											slide : function(event, ui) {
												$("#marks"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").val(ui.value);
												$("#slidemarksscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").html(ui.value);
											}
										});
										$("#marks"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").val($("#sliderscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider("value"));
										$("#slidemarksscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").html($("#sliderscore"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider("value"));
									});
								</script>
								<br/>
								<div id="slidemarksscore<%=questioninnerList.get(8)%>_<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width:25%; text-align:center;"></div>
								<div id="sliderscore<%=questioninnerList.get(8)%>_<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
								<div id="marksscore<%=questioninnerList.get(8)%>_<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerMp.get("WEIGHTAGE")%></span></div>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
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
								<input type="radio" name="<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").equals(inner.get(0))) {%>
									checked <%}%> value="<%=inner.get(0)%>" /> <%=inner.get(1)%><br />
								<% } %>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
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
								<input type="radio" name="<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
									checked <%}%> value="<%=inner.get(0)%>" /> <%=inner.get(1)%><br />
								<% } %>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
					 				answerTypeList.add("6");
					 				List<List<String>> outer = hmQuestionanswerType.get(questioninnerList.get(8));
							 %>
							<div>
								<% for (int j = 0; j < outer.size(); j++) {
									List<String> inner = outer.get(j);
								%>
								<input type="radio" name="<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
									checked <%}%> value="<%=inner.get(0)%>" /> <%=inner.get(1)%><br/>
								<% } %>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
							 		answerTypeList.add("7");
							 %>
							<div>
								<strong>Ans:</strong>&nbsp;<%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : "" %>
								<input type="hidden" name="outofmarks<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" id="outofmarks" value="<%=innerMp.get("WEIGHTAGE")%>"/>
								<input type="hidden" name="marks<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" id="marks" style="width: 31px;"/>
								<script>
									$(function() {
										$("#slidersingleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider({
											value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
											min : 0,
											max : <%=innerMp.get("WEIGHTAGE")%>,
											step : 1,
											slide : function(event, ui) {
												$("#marks"+"").val(ui.value);
												$("#slidemarkssingleopen"+"").html(ui.value);
											}
										});
										$("#marks"+"").val($("#slidersingleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider("value"));
										$("#slidemarkssingleopen"+"").html($("#slidersingleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider("value"));
									});
								</script>
						
							<br/>
							<div id="slidemarkssingleopen" style="width:25%; text-align:center;"></div>
							<div id="slidersingleopen<%=questioninnerList.get(8)%>_<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>						
							<div id="markssingleopen<%=questioninnerList.get(8)%>_<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerMp.get("WEIGHTAGE")%></span></div>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
							 		answerTypeList.add("8");
							 %>
							<div>
								<input type="radio" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
									checked <%}%> name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" /> a) <%=questioninnerList.get(2)%><br />

								<input type="radio" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
									checked <%}%> value="b" /> b) <%=questioninnerList.get(3)%><br />

								<input type="radio" value="c"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
									checked <%}%> name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" /> c) <%=questioninnerList.get(4)%><br />

								<input type="radio" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
									checked <%}%> value="d" /> d) <%=questioninnerList.get(5)%><br />
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
							 		answerTypeList.add("9");
							 %>
							<div>
								<input type="checkbox" value="a"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
									checked <%}%> name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" /> a) <%=questioninnerList.get(2)%><br />

								<input type="checkbox" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
									checked <%}%> value="b" /> b) <%=questioninnerList.get(3)%><br />

								<input type="checkbox" value="c"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
									checked <%}%> name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" /> c) <%=questioninnerList.get(4)%><br />

								<input type="checkbox" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
									<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
									checked <%}%> value="d" /> d) <%=questioninnerList.get(5)%><br />
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							 <%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
					 				answerTypeList.add("10");
							 %> <%
							 	String[] a = null;
					 				if (innerMp.get("ANSWER") != null) {
				 					a = innerMp.get("ANSWER").split(":_:");
							 	}
							 %>
							<div>
								<div style="float: left; margin: 30px 10px 0px 0px;">a) </div>
								<div><textarea rows="5" cols="50" style="width:100%" name="a<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=a != null ? a[0] : ""%></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">b) </div>
								<div><textarea rows="5" cols="50" style="width:100%" name="b<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=a != null ? a[1] : ""%></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">c) </div>
								<div><textarea rows="5" cols="50" style="width:100%" name="c<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=a != null ? a[2] : ""%></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">d) </div>
								<div><textarea rows="5" cols="50" style="width:100%" name="d<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=a != null ? a[3] : ""%></textarea>
									<br />
								</div>
								<input type="hidden" name="outofmarks<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" id="outofmarks" value="<%=innerMp.get("WEIGHTAGE")%>"/>
								<input type="hidden" name="marks<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" id="marks" style="width: 31px;"/>
								<script>
								/* disabled:true, */
									$(function() {
										$("#slidermultipleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider({
											value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
											min : 0,
											max : <%=innerMp.get("WEIGHTAGE")%>,
											step : 1,
											slide : function(event, ui) {
												$("#marks"+"").val(ui.value);
												$("#slidemarksmultipleopen"+"").html(ui.value);
											}
										});
										$("#marks"+"").val($("#slidermultipleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider("value"));
										$("#slidemarksmultipleopen"+"").html($("#slidermultipleopen"+<%=questioninnerList.get(8)%>+"_"+<%=innerMp.get("ASSESS_QUE_ANS_ID")%>+"").slider("value"));
									});
								</script>
							<br/>
							<div id="slidemarksmultipleopen" style="width:25%; text-align:center;"></div>
							<div id="slidermultipleopen<%=questioninnerList.get(8)%>_<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
							<div id="marksmultipleopen<%=questioninnerList.get(8)%>_<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerMp.get("WEIGHTAGE")%></span></div>
							</div> 
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							<%
						 	} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
						 		answerTypeList.add("11");
						 		double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
						 		double starweight = weightage*20/100;
						 	%>
							<div id="starPrimary<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"></div> <input type="hidden" id="gradewithrating<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
							value="<%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / 20 + "" : "0"%>" name="gradewithrating<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" />
							<script type="text/javascript">
					        $(function() {
					        	$('#starPrimary<%=innerMp.get("ASSESS_QUE_ANS_ID")%>').raty({
					        		start: <%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>,
					        		half: true,
					        		targetType: 'number',
					        		click: function(score, evt) {
					        			$('#gradewithrating<%=innerMp.get("ASSESS_QUE_ANS_ID")%>').val(score);
					        		}
					        	});
					        });
					        </script>
					        <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							<%
						 	} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
								answerTypeList.add("12");
							%>
							<div>
								<strong>Ans:</strong>
								<textarea rows="5" cols="50" style="width:100%" name="<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
							</div> 
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" style="width:70%" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
							<%
							 	} else if (uF.parseToInt(questioninnerList.get(8)) == 14) { 
							 		if(!answerTypeList.contains("14")) {
										answerTypeList.add("14");
									}
							%>
							<div>
								<div>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=questioninnerList.get(10) %> <br/>
									<input type="radio" value="a"
										<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
										checked <%}%> name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" /> a) <%=questioninnerList.get(2)%><br />
	
									<input type="radio" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
										<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
										checked <%}%> value="b" /> b) <%=questioninnerList.get(3)%><br />
	
									<input type="radio" value="c"
										<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
										checked <%}%> name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" /> c) <%=questioninnerList.get(4)%><br />
	
									<input type="radio" name="correct<%=innerMp.get("ASSESS_QUE_ANS_ID")%>"
										<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
										checked <%}%> value="d" /> d) <%=questioninnerList.get(5)%><br />
								</div>
							</div> 
							<div id="ansType14cmnt<%=innerMp.get("ASSESS_QUE_ANS_ID")%>">
							<br/><b>Comment:</b><br/>
							<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("ASSESS_QUE_ANS_ID")%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
							</div>
							<% } %>
						</li>
					</ul>
				</li>
			</ul>
		</div>
		<hr>

	<div style="text-align: center; margin: 10px 10px 10px 10px;">
		<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
	</div>
	</s:form>
</div>

<script>
 $("#formID1").submit(function(event) {
	 event.preventDefault();
	     var form_data = $("#formID1").serialize();
		$(".modal-body").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		//alert("1");
		var assessmentId = '<%=assessmentId %>';
        var lPlanId = '<%=lPlanId %>';
        var empID = '<%=empID %>';
        var userType = '<%=userType %>';

        $.ajax({
	   		type :'POST',
	   		url:'EditTakeAssessmentQueAnswer.action',
	   		data :form_data+"&submit=Save",
	   		success:function(result){
	   			$(".modal-body").html(result);
	   		}, 
 			error : function(err) {
 				$.ajax({ 
					url: 'TakeAssessmentPreview.action?assessmentId='+assessmentId+'&lPlanId='+lPlanId+'&empID='+empID+'&userType='+userType,
					cache: true,
					success: function(result){
						$(".modal-body").html(result);
			   		}
				});
 			}
		});
   		
   		$("#modalInfo1").hide();
 });
</script>