<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<style>


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

#greenbox {
height: 20px;
background-color:#00FF00; /* the critical component */
}
#redbox {
height: 20px;
background-color:#FF0000; /* the critical component */
}
#yellowbox {
height: 20px;
background-color:#FFFF00; /* the critical component */
}
#outbox {

height: 20px;
width: 100%;
background-color:#D8D8D8; /* the critical component */

}

.anaAttrib1 {
font-size: 14px;
font-family: digital;
color: #3F82BF;
font-weight: bold;
}

.table_font {
font-size: 12px;
}

</style>
<%-- <%
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


%> --%>


<%-- <g:compress>
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
</g:compress> --%>

<%-- 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Review Form" name="title" />
</jsp:include>
 --%>

	<%
	UtilityFunctions uF = new UtilityFunctions();
	String anstype = (String) request.getAttribute("anstype");
	List<List<String>> ansTypeList = (List<List<String>>) request.getAttribute("ansTypeList");
	String assessmentId = (String)request.getAttribute("assessmentId");
	String assessPreface = (String)request.getAttribute("assessPreface");
	List<List<String>> sectionList = (List<List<String>>) request.getAttribute("sectionList");
	Map<String, List<List<String>>> hmAssessmentQueData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentQueData");
	List<String> assessmentList = (List<String>) request.getAttribute("assessmentList");
	List<String> answerTypeList = new ArrayList<String>();
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	Map<String, Map<String, String>> questionAnswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionAnswerMp");
	
	%>


<div class="leftbox reportWidth">

	<div style="width: 100%">

		<div class="addgoaltoreview">
				<h3><%=assessmentList.get(1) %></h3>

				<div style="float: left; padding: 0px 20px; font-size: 10px; text-align: justify; line-height: 12px;">
					<%=assessmentList.get(2) %>				
				</div>
			</div>
			
		
										
	<div id="queAsnDiv" style="width: 100%;">
			
		<div>
			
			<s:form action="TakeAssessment" id="TakeAssessmentFormID" method="POST" theme="simple">
				<s:hidden name="empID" id="empID"></s:hidden>
				<s:hidden name="lPlanId" />
				<s:hidden name="assessmentId" />
				<%
					 for(int a=0; sectionList != null && !sectionList.isEmpty() && a<sectionList.size(); a++) {
						 List<String> innerSectionList = sectionList.get(a);
				%>
						
							<div class="addgoaltoreview">
				<h3><%=a+1 %>)&nbsp;<%=uF.showData(innerSectionList.get(1), "")%>
				<input type="hidden" name="hideSubsectionId" id="hideSubsectionId" value="<%=uF.showData(innerSectionList.get(0), "")%>" />
				</h3>

				<div style="width: 70%; float: left; padding: 0px 20px; font-size: 10px; text-align: justify; line-height: 12px;">
					<%=uF.showData(innerSectionList.get(2), "")%>				
				</div>
				
				<div style="width: 70%; float: left; margin-top: 10px; padding: 0px 20px; font-size: 10px; text-align: justify; line-height: 12px;">
					Marks for section: <%=uF.showData(innerSectionList.get(3), "")%>				
				</div>
				
				<div style="width: 70%; float: left; margin-top: 10px; padding: 0px 20px; font-size: 10px; text-align: justify; line-height: 12px;">
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
						<li><b><%=a+1 %>.<%=(i + 1)%>)&nbsp;&nbsp;<%=innerlist.get(1)%> </b> </li>
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
										a) <input type="checkbox" value="a" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> /><%=innerlist.get(2)%><br /> b) <input
											type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=innerlist.get(3)%><br />

										c) <input type="checkbox" value="c"
											name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> /><%=innerlist.get(4)%><br /> d) <input
											type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=innerlist.get(5)%><br />

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
										a) <input type="checkbox" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /><%=innerlist.get(2)%><br />

										b) <input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=innerlist.get(3)%><br />

										c) <input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /><%=innerlist.get(4)%><br />

										d) <input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=innerlist.get(5)%>
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
										<%-- <input type="text" name="marks<%=innerlist.get(0)%>"
											id="marks<%=i%>" style="width: 31px;"
											onkeyup="isNumber(this.value,this.id,'<%=innerlist.get(2)%>');"
											value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>" />/<%=innerlist.get(2)%> --%>	
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
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<%
											}
										%>
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
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<%
											}
										%>
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
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<%
											}
										%>
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
										<br /> <%-- <input type="text" name="marks<%=innerlist.get(0)%>"
											id="marks<%=i%>" style="width: 31px;"
											onkeyup="isNumber(this.value,this.id,'<%=innerlist.get(7)%>');"
											value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>" />/<%=innerlist.get(7)%> --%>
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
										a) <input type="radio" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /><%=innerlist.get(2)%><br />

										b) <input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=innerlist.get(3)%><br />

										c) <input type="radio" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /><%=innerlist.get(4)%><br />

										d) <input type="radio" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=innerlist.get(5)%><br />
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
										a) <input type="checkbox" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /><%=innerlist.get(2)%><br />

										b) <input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=innerlist.get(3)%><br />

										c) <input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /><%=innerlist.get(4)%><br />

										d) <input type="checkbox" name="correct<%=innerlist.get(0)%>_<%=innerlist.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=innerlist.get(5)%><br />
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
 %> <%
 	String[] aa = null;

 				if (innerMp.get("ANSWER") != null) {
 					aa = innerMp.get("ANSWER").split(":_:");
 %> <%
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
										<%-- <input type="text" name="marks<%=innerlist.get(0)%>"
											value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>"
											id="marks<%=i%>" style="width: 31px;"
											onkeyup="isNumber(this.value,this.id,'<%=innerlist.get(2)%>');" />/<%=innerlist.get(2)%> --%>
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
									name="gradewithrating<%=innerlist.get(0)%>_<%=innerlist.get(9)%>" /> <script
										type="text/javascript">
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
 		if(!answerTypeList.contains("12")){		
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
							<legend>Answer Type Structure</legend>
				<!-- <div style="float: left; margin-left: 35px;">
				<h2>Answer type structure :</h2>
				</div>
				<br/> -->
					<table class="table_font" style="margin: 10px 10px 10px 30px;">
						<tr>
						<%
							int k = 1;
								for (int i = 0; i < answerTypeList.size(); i++) {
									List<List<String>> outerList = hmQuestionanswerType.get(answerTypeList.get(i));
						%>
					<td valign="top">
					<table class="table_font">
						<%
							for (int j = 0; outerList != null && j < outerList.size(); j++) {
										List<String> innerlist = (List<String>) outerList.get(j);
						%>
						<tr>
							<%
								if (j == 0) {
							%>
							<td><b><%=k++%>).</b></td>
							<%
								} else {
							%>
							<td>&nbsp;</td>
							<%
								}
							%>
							<td style="text-align: left; min-width: 100px;"><%=innerlist.get(0)%>-<%=innerlist.get(0)%></td>
						</tr>
						<%
							}
						%>
						</table>
						</td>
						<%
							}
						%>
						</tr>
					</table>
					</fieldset>
					<!-- <div class="addgoaltoreview-arrow"></div> -->
				</div>
				
				<div style="float: right";>
					<%-- <%
						if (mainLevelList.size() == size) {
					%>
					<s:submit value="Preview" cssClass="input_button" name="submit"></s:submit> 
					<s:submit value="Finish" cssClass="input_button" name="btnfinish" onclick="return confirm('Are you sure, you want to finish this ?')"></s:submit>
					<!-- <input type="button" value="Finish" class="input_button" name="btnfinish" onclick="finishForm();"/> -->
					<%
						} else {
					%> --%>
					<s:submit value="Submit" cssClass="input_button" name="submit"></s:submit>
					<%-- <%
						}
					%> --%>
				</div>
			</s:form>
		</div>
		</div>
	</div>



	<div></div>
</div>
