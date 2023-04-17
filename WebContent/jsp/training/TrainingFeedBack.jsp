<%@page import="java.util.List"%>
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
	width:100%;
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
</style>
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
</script>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Training Feedback" name="title" />
</jsp:include>



<%
	Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
	List<List<String>> questionList = (List<List<String>>) request.getAttribute("questionList");
	UtilityFunctions uF = new UtilityFunctions();

	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
	
	List<String> answerTypeList = new ArrayList<String>();
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
	List<String> trainingDetails=(List<String>)request.getAttribute("trainingDetails");
%>


<div class="leftbox reportWidth">

	<table class="tb_style" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<th colspan="4" align="left" style="padding-left: 20px">Training Information</th>
		</tr>
		<tr>
			<th align="right" width="20%" style="padding-right: 20px">Training Title</th>
			<td valign="top" width="30%"><%=trainingDetails.get(0)%></td>

			<th align="right" width="20%" style="padding-right: 20px">Training Type</th>
			<td valign="top" width="30%"><%=(trainingDetails.get(3)==null && trainingDetails.get(3).equals("")) ? "" : trainingDetails.get(3).equals("1") ? "Trainer driven" : "Self Learning" %></td>
		</tr>
		<tr>
			<th align="right" style="padding-right: 20px">Certificate</th>
			<td valign="top"><%=trainingDetails.get(4)%></td>

			<th align="right" style="padding-right: 20px">Associated With Attribute</th>
			<td valign="top"><%=trainingDetails.get(5)%></td>
		</tr>
		<tr>
			<th align="right" style="padding-right: 20px">Organization</th>
			<td valign="top"><%=trainingDetails.get(7)%></td>

			<th align="right" style="padding-right: 20px">Location</th>
			<td valign="top"><%=trainingDetails.get(8)%></td>
		</tr>
		
	</table>

	<div style="width: 100%">
	
	<div class="topDiv FL">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>

					<td class="Gray12  FL  ML ">Training Objective</td>
				</tr>
				<tr><td class="Gray12  FL  ML ">
				<span style="color: #000"> 
 						<%=trainingDetails.get(1) %>
						</span>
				</td></tr>
			</table>
		</div>
		
		<div class="topDiv FL">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="Gray12  FL  ML ">Training Preface/Summary</td>
				</tr>
				<tr><td class="Gray12  FL  ML ">
				<span style="color: #000"> 
 						<%=trainingDetails.get(2) %>
						</span>
				</td></tr>
			</table>
		</div>
	
	
<div class="addgoaltoreview">
				<h3></h3>

				<div style="float: left; padding: 0px 20px; font-size: 10px; text-align: justify; line-height: 12px;">
							
				</div>
							
				<div class="addgoaltoreview-arrow"></div>
			</div>
			

		<div>
			
			<s:form action="TrainingFeedBack" id="formID" method="POST" theme="simple">
				<s:hidden name="empID"></s:hidden>
				<s:hidden name="plan_id"></s:hidden>			
				<%	
					for (int i = 0; questionList != null && i < questionList.size(); i++) {
							List<String> innerlist = (List<String>) questionList.get(i);
							List<String> questioninnerList = hmQuestion.get(innerlist.get(1));

							Map<String, String> innerMp = questionanswerMp.get(innerlist.get(2) + "question" + innerlist.get(1));
							if(innerMp == null) innerMp = new HashMap<String, String>();
							
				%>
				<div style="width: 80%; margin: 10px 50px 10px 50px; border-bottom: 1px solid rgb(0, 0, 0);">
					<ul>
						<li><b><%=(i + 1)%>).&nbsp;<%=questioninnerList.get(1)%> </b> <s:if test="innerlist.get(3)!=null">(<%=innerlist.get(12)%>)</s:if></li>
						<li>
							<ul style="margin: 10px 10px 10px 30px">
								<li>
									<%
										if (uF.parseToInt(questioninnerList.get(8)) == 1) {
											answerTypeList.add("1");
									%>
									<div>
										a) <input type="checkbox" value="a" name="correct<%=innerlist.get(1)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> /><%=questioninnerList.get(2)%><br /> 
										b) <input type="checkbox" name="correct<%=innerlist.get(1)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="checkbox" value="c" name="correct<%=innerlist.get(1)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> /><%=questioninnerList.get(4)%><br /> 
										d) <input type="checkbox" name="correct<%=innerlist.get(1)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />

										<textarea rows="5" cols="50" name="<%=innerlist.get(1)%>" style="width:100%"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
									</div> 
								<%
 									} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
 										answerTypeList.add("2");
								%>
									<div>

										a) <input type="checkbox" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>" /><%=questioninnerList.get(2)%><br />

										b) <input type="checkbox" name="correct<%=innerlist.get(1)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

										c) <input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>" /><%=questioninnerList.get(4)%><br />

										d) <input type="checkbox" name="correct<%=innerlist.get(1)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%>
									</div> 
								<%
									} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
										answerTypeList.add("3");
								%>
									<div>
										<input type="hidden" name="marks<%=innerlist.get(1)%>" id="marks<%=i%>" style="width: 31px;"/>
										<script>
											$(function() {
												$("#sliderscore"+<%=questioninnerList.get(8)+i%>+"").slider({
													value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 1,
													slide : function(event, ui) {
														$("#marks"+<%=i%>+"").val(ui.value);
														$("#slidemarksscore"+<%=i%>+"").html(ui.value);
													}
												});
												$("#marks"+<%=i%>+"").val($("#sliderscore"+<%=questioninnerList.get(8)+i%>+"").slider("value"));
												$("#slidemarksscore"+<%=i%>+"").html($("#sliderscore"+<%=questioninnerList.get(8)+i%>+"").slider("value"));
											});
										</script>
										<br/>
										<div id="slidemarksscore<%=i%>" style="width:25%; text-align:center;"></div>
										<div id="sliderscore<%=questioninnerList.get(8)+i%>" style="width:25%; float: left;"></div>
										<div id="marksscore<%=questioninnerList.get(8)+i%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
										<br/>
									</div> 
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
										<input type="radio" name="<%=innerlist.get(1)%>" <%if (innerMp.get("MARKS") != null && innerMp.get("MARKS").equals(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />

									<% } %>
									</div>
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
										<input type="radio" name="<%=innerlist.get(1)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />

									<% } %>
									</div>
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
										<input type="radio" name="<%=innerlist.get(1)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />

										<% } %>
									</div> 
								<%
 									} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
										answerTypeList.add("7");
								%>
									<div>
										<textarea rows="5" cols="50" name="<%=innerlist.get(1)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
										<br /> 
										<input type="hidden" name="outofmarks<%=innerlist.get(1)%>" id="outofmarks<%=i%>" value="<%=innerlist.get(2)%>" />
										<input type="hidden" name="marks<%=innerlist.get(1)%>" id="marks<%=i%>" style="width: 31px;"/>
						
										<script>
											$(function() {
												$("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"").slider({
													value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 2,
													slide : function(event, ui) {
														$("#marks"+<%=i%>+"").val(ui.value);
														$("#slidemarkssingleopen"+<%=i%>+"").html(ui.value);
													}
												});
												$("#marks"+<%=i%>+"").val($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"").slider("value"));
												$("#slidemarkssingleopen"+<%=i%>+"").html($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"").slider("value"));
											});
										</script>
										<br/>
										<div id="slidemarkssingleopen<%=i%>" style="width:25%; text-align:center;"></div>
										<div id="slidersingleopen<%=questioninnerList.get(8)+i%>" style="width:25%; float: left;"></div>						
										<div id="markssingleopen<%=questioninnerList.get(8)+i%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
										<br/>			
									</div> 
								<%
 									} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
 										answerTypeList.add("8");
								%>
									<div>

										a) <input type="radio" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>" /><%=questioninnerList.get(2)%><br />

										b) <input type="radio" name="correct<%=innerlist.get(1)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

										c) <input type="radio" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>" /><%=questioninnerList.get(4)%><br />

										d) <input type="radio" name="correct<%=innerlist.get(1)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div> 
								<%
 									} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
 										answerTypeList.add("9");
								%>
									<div>
										a) <input type="checkbox" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>" /><%=questioninnerList.get(2)%><br />

										b) <input type="checkbox" name="correct<%=innerlist.get(1)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

										c) <input type="checkbox" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>" /><%=questioninnerList.get(4)%><br />

										d) <input type="checkbox" name="correct<%=innerlist.get(1)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div> 
								<%
 									} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
 										answerTypeList.add("10");
								%> 
								<%
 									String[] a = null;
					 				if (innerMp.get("ANSWER") != null) {
					 					a = innerMp.get("ANSWER").split(":_:");
 								%> 
 								<% } %>
									<div>
										<div style="float: left; margin: 30px 10px 0px 0px;">a)
										</div>
										<div>
											<textarea rows="5" cols="50" name="a<%=innerlist.get(1)%>" style="width:100%"><%=a != null ? a[0] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">b)
										</div>
										<div>
											<textarea rows="5" cols="50" name="b<%=innerlist.get(1)%>" style="width:100%"><%=a != null ? a[1] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">c)
										</div>
										<div>
											<textarea rows="5" cols="50" name="c<%=innerlist.get(1)%>" style="width:100%"><%=a != null ? a[2] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">d)
										</div>
										<div>
											<textarea rows="5" cols="50" name="d<%=innerlist.get(1)%>" style="width:100%"><%=a != null ? a[3] : ""%></textarea>
											<br />
										</div>
										<input type="hidden" name="outofmarks<%=innerlist.get(1)%>" id="outofmarks<%=i%>" value="<%=innerlist.get(2)%>" />
										<input type="hidden" name="marks<%=innerlist.get(1)%>" id="marks<%=i%>" style="width: 31px;"/>
						
										<script>
											$(function() {
												$("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"").slider({
													value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 1,
													slide : function(event, ui) {
														$("#marks"+<%=i%>+"").val(ui.value);
														$("#slidemarksmultipleopen"+<%=i%>+"").html(ui.value);
													}
												});
												$("#marks"+<%=i%>+"").val($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"").slider("value"));
												$("#slidemarksmultipleopen"+<%=i%>+"").html($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"").slider("value"));
											});
										</script>
										<br/>
										<div id="slidemarksmultipleopen<%=i%>" style="width:25%; text-align:center;"></div>
										<div id="slidermultipleopen<%=questioninnerList.get(8)+i%>" style="width:25%; float: left;"></div>
										<div id="marksmultipleopen<%=questioninnerList.get(8)+i%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
										<br/>		
									</div> 
								<%
 									} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
 										answerTypeList.add("11");
								%>
									<div id="starPrimary<%=innerlist.get(1)%>"></div> 
									<input type="hidden" id="gradewithrating<%=innerlist.get(1)%>" value="<%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / 20 + "" : "0"%>" name="gradewithrating<%=innerlist.get(1)%>" /> 
									<script type="text/javascript">
											$(function() {
									        	$('#starPrimary<%=innerlist.get(1)%>').raty({
									        		readOnly: false,
									        		start: <%=innerMp.get("MARKS") != null ? uF.parseToInt(innerMp.get("MARKS")) / 20 + "" : "0"%>,
									        		half: true,
									        		targetType: 'number',
									        		click: function(score, evt) {
									        			$('#gradewithrating<%=innerlist.get(1)%>').val(score);
													}
												});
											});
										</script>
								<% } %>
								</li>
							</ul>
						</li>
					</ul>
				</div>
				<hr>
				<% } %>
				<div class="addgoaltoreview">
					<table style="margin: 10px 10px 10px 30px;">
						<%
							int k = 1;
							for (int i = 0; i < answerTypeList.size(); i++) {
								List<List<String>> outerList = hmQuestionanswerType.get(answerTypeList.get(i));
						%>

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
						<% } %>
					</table>

					<div class="addgoaltoreview-arrow"></div>
				</div>
				<div style="float: right";>
					<s:submit value="Finish" cssClass="input_button" name="submit"></s:submit>
				</div>
			</s:form>
		</div>
	</div>

	<div></div>
</div>
