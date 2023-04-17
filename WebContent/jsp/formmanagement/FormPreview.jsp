<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
 
<%
 	UtilityFunctions uF = new UtilityFunctions();
	String anstype = (String) request.getAttribute("anstype");
	
	String formId = (String) request.getAttribute("formId");
	Map<String, String> hmForm = (Map<String, String>)request.getAttribute("hmForm");
	if(hmForm == null) hmForm = new HashMap<String, String>();
	List<Map<String, String>> alSection = (List<Map<String, String>>)request.getAttribute("alSection");
	if(alSection == null) alSection = new ArrayList<Map<String,String>>();
	Map<String, List<Map<String, String>>> hmSectionQuestion = (Map<String, List<Map<String, String>>>)request.getAttribute("hmSectionQuestion");
	if(hmSectionQuestion == null) hmSectionQuestion = new HashMap<String, List<Map<String,String>>>();
	Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
	if(hmQuestion == null) hmQuestion = new HashMap<String, List<String>>();
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	if(answertypeSub == null) answertypeSub = new HashMap<String, List<List<String>>>();
%>

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

.addgoaltoreview h4 {
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

h4{
text-align: left !important;
padding-left: 10px !important;
padding-bottom: 0px !important;
padding-top: 0px !important;
}
</style>


<div class="leftbox reportWidth">
	<div style="width:100%; float:left">
		<table class="tb_style" style="float: left; width: 100%;">
			<tr>
				<th class="txtlabel alignRight">Form Title:</th>
				<td><%=uF.showData(hmForm.get("FORM_NAME"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Organisation:</th>
				<td><%=uF.showData(hmForm.get("FORM_ORG_NAME"),"") %></td> 
			</tr>
			<tr>
				<th class="txtlabel alignRight">Form Node:</th>
				<td><%=uF.showData(hmForm.get("FORM_NODE"),"") %></td>
			</tr>
		</table>
	</div>
	
	<div class="addgoaltoreview" style="margin-top: 10px;">
		<h4>Section Details (<%=alSection.size() %>)</h4>		  
	</div>	
	<br/><br/>
	<%
	int nWeightage = 0;
	if(uF.parseToInt(formId) > 0){ 
		for(int j = 0; alSection !=null && j < alSection.size(); j++ ){	
			Map<String, String> hmSection = (Map<String, String>) alSection.get(j);
			nWeightage += uF.parseToDouble(hmSection.get("SECTION_WEIGHTAGE")); 
	%>
			<div class="addgoaltoreview">
				<div style="width: 100%; float: left;">
					<h4><%=j+1%>)&nbsp;<%=uF.showData(hmSection.get("SECTION_NAME"),"")%> </h4>
					<span style="float: right; margin-right: 100px;">
						<strong>Weightage :-&nbsp;&nbsp;</strong> <%=uF.showData(hmSection.get("SECTION_WEIGHTAGE"),"0")%>%&nbsp;&nbsp;
						<strong>Answer Type :-&nbsp;&nbsp;</strong> <%=uF.showData(hmSection.get("SECTION_ANSWER_TYPE"),"")%>
					</span>
					<div style="width: 70%; text-align: left; margin-left: 40px;">
						<%=uF.showData(hmSection.get("SECTION_SHORT_DESCRIPTION"), "")%>
					</div>
				</div>
			</div>	
			
			<%	
						
			List<Map<String, String>> alSecQueList = (List<Map<String, String>>) hmSectionQuestion.get(hmSection.get("SECTION_ID"));
					
			for (int i = 0; alSecQueList != null && i < alSecQueList.size(); i++) {
				Map<String, String> hmSecQuestion = (Map<String, String>) alSecQueList.get(i);
				List<String> questioninnerList = hmQuestion.get(hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID"));
				if(questioninnerList == null) questioninnerList = new ArrayList<String>();
				int nQuestioninnerListSize = questioninnerList.size();
				if(nQuestioninnerListSize == 0){
					continue;
				}
				
				/* Map<String, String> innerMp = questionanswerMp.get(strSectionId + "question" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID"));
				if (innerMp == null) innerMp = new HashMap<String, String>(); */
			%>
				<div style="width: 80%; margin: 10px 50px 10px 50px; border-bottom: 1px solid rgb(0, 0, 0);">
					<ul>
						<li><b><%=(j+1)%>.<%=(i + 1)%>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b></li>
						<li>

							<ul style="margin: 10px 10px 10px 30px">
								<li>
									<%
										if (uF.parseToInt(questioninnerList.get(8)) == 1) {
									%>
										<div>
											a) <input type="checkbox" value="a" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly"/><%=questioninnerList.get(2)%><br /> 
											b) <input type="checkbox" value="b" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly"/><%=questioninnerList.get(3)%><br />
											c) <input type="checkbox" value="c" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly"/><%=questioninnerList.get(4)%><br /> 
											d) <input type="checkbox" value="d" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly"/><%=questioninnerList.get(5)%><br />
	
											<textarea rows="5" cols="50" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%" readonly="readonly"></textarea>
										</div> 
										<%-- <div id="ansType1cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
											<br/><b>Comment:</b><br/>
											<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
										</div> --%>
									<%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
 									%>
										<div>
											a) <input type="checkbox" value="a" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly"/><%=questioninnerList.get(2)%><br />
											b) <input type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly" value="b" /><%=questioninnerList.get(3)%><br />
											c) <input type="checkbox" value="c" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"  readonly="readonly"/><%=questioninnerList.get(4)%><br />
											d) <input type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly" value="d" /><%=questioninnerList.get(5)%>
										</div>
										<%-- <div id="ansType2cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
										</div> --%>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
									 %>
									<div>
										<input type="hidden" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
											id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
						
										<script>
											$(function() {
												$("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
													value : 0,
													min : 0,
													max : <%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>,
													step : 1,
													slide : function(event, ui) {
														$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").val(ui.value);
														$("#slidemarksscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").html(ui.value);
													}
												});
												$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").val($("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
												$("#slidemarksscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").html($("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
											});
										</script>
										<br/>
										<div id="slidemarksscore<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%; text-align:center;"></div>
										<div id="sliderscore<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%; float: left;"></div>
										<div id="marksscore<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%></span></div>
									</div>
									<%-- <div id="ansType3cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
									</div> --%>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
										<div>
											<%
												for (int k = 0; outer!=null && k < outer.size(); k++) {  
													List<String> inner = outer.get(k);
											%>
													<input type="radio" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly" value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
											<%
												}
											%>
										</div> 
										<%-- <div id="ansType4cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
										</div> --%>
									<%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
										<div>
											<%
												for (int k = 0; outer!=null && j < outer.size(); k++) {
													List<String> inner = outer.get(k);
											%>
													<input type="radio" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly" value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
											<%
												}
											%>
										</div> 
										<%-- <div id="ansType5cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
										</div> --%>
									<%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
										<div>
											<%
												for (int k = 0; outer!=null && k < outer.size(); k++) {
													List<String> inner = outer.get(k);
											%>
											<input type="radio" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" value="<%=inner.get(0)%>" readonly="readonly"/><%=inner.get(1)%><br />
											<%
												}
											%>
										</div>
										<%-- <div id="ansType6cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
										</div> --%>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
									 %>
										<div>
											<textarea rows="5" cols="50" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%" readonly="readonly"></textarea>
											<br /> 
											<input type="hidden" name="outofmarks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
												id="outofmarks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" value="<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>" />
												
											<input type="hidden" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
											id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
						
											<script>
												$(function() {
													$("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
														value : 0,
														min : 0,
														max : <%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>,
														step : 1,
														slide : function(event, ui) {
															$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").val(ui.value);
															$("#slidemarkssingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").html(ui.value);
														}
													});
													$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").val($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
													$("#slidemarkssingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").html($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
												});
											</script>
											<br/>
											<div id="slidemarkssingleopen<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%; text-align:center;"></div>
											<div id="slidersingleopen<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%; float: left;"></div>						
											<div id="markssingleopen<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%></span></div>
										 </div> 
											<%-- <div id="ansType7cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
											<br/><b>Comment:</b><br/>
											<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
											</div> --%>
									<%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
									 %>
									<div>
										a) <input type="radio" value="a"  readonly="readonly" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"/><%=questioninnerList.get(2)%><br/>
										b) <input type="radio" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly" value="b"/><%=questioninnerList.get(3)%><br/>
										c) <input type="radio" value="c" readonly="readonly" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"/><%=questioninnerList.get(4)%><br/>
										d) <input type="radio" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly" value="d"/><%=questioninnerList.get(5)%><br/>
									</div>
									<%-- <div id="ansType8cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
									</div> --%>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
									 %>
									<div>
										a) <input type="checkbox" value="a" readonly="readonly" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />
										b) <input type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" readonly="readonly" value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="checkbox" value="c" readonly="readonly" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />
										d) <input type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"value="d" readonly="readonly"/><%=questioninnerList.get(5)%><br />
									</div>
									<%-- <div id="ansType9cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
									</div> --%>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
									 %>
										<div>
											<div style="float: left; margin: 30px 10px 0px 0px;">a)
											</div>
											<div>
												<textarea rows="5" cols="50" name="a<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%" readonly="readonly"></textarea>
												<br />
											</div>
											<div style="float: left; margin: 30px 10px 0px 0px;">b)
											</div>
											<div>
												<textarea rows="5" cols="50" name="b<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%" readonly="readonly"></textarea>
												<br />
											</div>
											<div style="float: left; margin: 30px 10px 0px 0px;">c)
											</div>
											<div>
												<textarea rows="5" cols="50" name="c<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%" readonly="readonly"></textarea>
												<br />
											</div>
											<div style="float: left; margin: 30px 10px 0px 0px;">d)
											</div>
											<div>
												<textarea rows="5" cols="50" name="d<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%" readonly="readonly"></textarea>
												<br />
											</div>
											<input type="hidden" name="outofmarks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
												id="outofmarks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" value="<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>" />
												
												<input type="hidden" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
												id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
												
											<script>
												$(function() {
													$("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
														value : 0,
														min : 0,
														max : <%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>,
														step : 1,
														slide : function(event, ui) {
															$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").val(ui.value);
															$("#slidemarksmultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").html(ui.value);
														}
													});
													$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").val($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
													$("#slidemarksmultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>+"").html($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
												});
											</script>
											<br/>
											<div id="slidemarksmultipleopen<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%; text-align:center;"></div>
											<div id="slidermultipleopen<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%; float: left;"></div>
											<div id="marksmultipleopen<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%></span></div>
										</div> 
										<%-- <div id="ansType10cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
										</div> --%>
									<%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
									 %>
										<div id="starPrimary<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"></div> 
										<input type="hidden" id="gradewithrating<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
											value="0" name="gradewithrating<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /> 
										<script type="text/javascript">
									        $(function() {
									        	$('#starPrimary<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>').raty({
									        		readOnly: false,
									        		start: 0,
									        		half: true,
									        		targetType: 'number',
									        		click: function(score, evt) {
									        			$('#gradewithrating<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>').val(score);
													}
												});
											});
										</script>
										<%-- <div id="ansType11cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
										</div> --%>
									 <%
									 	} else if (uF.parseToInt(questioninnerList.get(8)) == 12) { 
									%>
										<div>
											<textarea rows="5" cols="50" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%" readonly="readonly"></textarea>
										</div> 
										<%-- <div id="ansType12cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%" readonly="readonly"></textarea>
										</div> --%>
									<%}%>
								</li>
		   					</ul>
						</li>
					</ul>
				</div>
				<hr>
						
					<%}%>
	<%	}
	}%>
	
</div>
