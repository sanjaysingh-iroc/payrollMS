
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<style>
.ul_class li {
	margin: 10px 0px 10px 100px;
}
</style>
<script type="text/javascript">
jQuery(document).ready(function(){
    // binds form submission and fields to the validation engine
    jQuery("#formID").validationEngine();
    
    
}); 


function isOnlyNumberKey(evt) {
	var charCode = (evt.which) ? evt.which : event.keyCode;
	if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
		return true; 
	}
	return false;
}


	function showSystem(value, divcount) {
		if (value == '1') {

			document.getElementById("mainDiv").style.display = 'block';
			document.getElementById("otherDiv").style.display = 'none';
			document.getElementById("otherDiv").innerHTML = '';
			document.getElementById("scoreCardID" + divcount).style.display = 'table-row';

		} else if (value == '2') {

			questionCnt = 0;
			document.getElementById("mainDiv").style.display = 'none';
			document.getElementById("otherDiv").style.display = 'block';
			document.getElementById("otherDiv").innerHTML = otherDivData();

			document.getElementById("mainDiv").innerHTML = '';
			document.getElementById("scoreCardID" + divcount).style.display = 'none';

		} else {
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("otherDiv" + divcount).style.display = 'none';
			document.getElementById("scoreCardDiv" + divcount).style.display = "none";

		}

	}

	

	function addNewQuestion(id, val, cnt1) {

		if (val == '0') {
			//cnt++;

			document.getElementById("QuestionName" + cnt1).style.display = 'table-row';
			document.getElementById("AddQuestion" + cnt1).style.display = 'table-row';
			document.getElementById("selectanstype" + cnt1).style.display = 'table-row';
			document.getElementById("answerType" + cnt1).style.display = 'table-row';
			document.getElementById("answerType1" + cnt1).style.display = 'table-row';
			if(document.getElementById("answerType2" + cnt1)) {
				document.getElementById("answerType2" + cnt1).style.display = 'table-row';
			}
		} else {

			document.getElementById("QuestionName" + cnt1).style.display = 'none';
			document.getElementById("AddQuestion" + cnt1).style.display = 'none';
			document.getElementById("selectanstype" + cnt1).style.display = 'none';
			document.getElementById("answerType" + cnt1).style.display = 'none';
			document.getElementById("answerType1" + cnt1).style.display = 'none';
			if(document.getElementById("answerType2" + cnt1)) {
				document.getElementById("answerType2" + cnt1).style.display = 'none';
			}
		}
	}

	function changeNewQuestionType(val, id, id1, id2, cnt) {

		if (val == 1 || val == 2 || val == 8) {
			addQuestionType1(id, cnt);
			document.getElementById(id).style.display = 'table-row';

			addQuestionType2(id1, cnt);
			document.getElementById(id1).style.display = 'table-row';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';
		} else if (val == 9) {
			addQuestionType3(id, cnt);
			document.getElementById(id).style.display = 'table-row';

			addQuestionType4(id1, cnt);
			document.getElementById(id1).style.display = 'table-row';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';

		} else if (val == 6) {
			addTrueFalseType(id, cnt);
			document.getElementById(id).style.display = 'table-row';
			document.getElementById(id1).innerHTML = "";
			document.getElementById(id1).style.display = 'none';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';

		} else if (val == 5) {
			addYesNoType(id, cnt);
			document.getElementById(id).style.display = 'table-row';
			document.getElementById(id1).innerHTML = "";
			document.getElementById(id1).style.display = 'none';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';

		} else if (val == 13) {
			addQuestionType1_1(id, cnt);
			addQuestionType2_1(id1, cnt);
			addQuestionType5_1(id2, cnt);
			document.getElementById(id).style.display = 'table-row';
			document.getElementById(id1).style.display = 'table-row';
			document.getElementById(id2).style.display = 'table-row';
		} else {
			addQuestionType1(id, cnt);
			addQuestionType2(id1, cnt);
			document.getElementById(id).style.display = 'none';
			document.getElementById(id1).style.display = 'none';
			document.getElementById(id2).innerHTML = "";
			document.getElementById(id2).style.display = 'none';
		}

	}

	function addTrueFalseType(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True"
				+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
	}

	function addYesNoType(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">yes"
				+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
	}
	function addQuestionType1(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><td>a)<input type=\"text\" name=\"optiona\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\" /><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
	}
	function addQuestionType2(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><td>d)<input type=\"text\" name=\"optionc\"  /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
	}
	function addQuestionType3(id, cnt) {
		document.getElementById(id).innerHTML = "<th></th><td>a)<input type=\"text\" name=\"optiona\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
	}
	function addQuestionType4(id1, cnt) {
		document.getElementById(id1).innerHTML = "<th></th><td>d)<input type=\"text\" name=\"optionc\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
	}
	
	function addQuestionType1_1(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
		+"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
	}
	function addQuestionType2_1(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
		+"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
	}
	function addQuestionType5_1(id,cnt) {
		document.getElementById(id).innerHTML = "<th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td><td colspan=\"2\">&nbsp;</td>";
	}
	
	
	function changeStatus(id) {
		if (document.getElementById('addFlag' + id).checked == true) {
			document.getElementById('status' + id).value = '1';
		} else {
			document.getElementById('status' + id).value = '0';
		}
	}

	
	
</script>
<%
	List<String> appraisalList = (List<String>) request
			.getAttribute("appraisalList");
	Map hmScoreDetailsMap = (Map) request
			.getAttribute("hmScoreDetailsMap");
	
%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Edit Level" name="title" />
</jsp:include>


<div class="reportWidth">

	<s:form action="AddLevelData" id="formID" method="POST" theme="simple">

		<h2>Appraisal</h2>
		<br />
		<div>

				<table class="tb_style" width="100%">
					<tr>
						<th width="15%" align="right">Appraisal Name</th>
						<td colspan="1"><b><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></b>
						</td>
					</tr>
					<tr>
						<th align="right">Appraisal Type</th>
						<td><%=appraisalList.get(14)%></td>
					</tr>
					<tr>
						<th valign="top" align="right">Description</th>
						<td colspan="1"><%=appraisalList.get(15)%></td>
					</tr>
					<tr>
						<th valign="top" align="right">Instruction</th>
						<td colspan="1"><%=appraisalList.get(16)%></td>
					</tr>
					<tr>	
						<th align="right">Frequency</th>
						<td><%=appraisalList.get(7)%></td>
					</tr>
					<tr>	
						<th align="right">Effective Date</th>
						<td><%=appraisalList.get(17)%></td>
					</tr>
					<tr>	
						<th align="right">Due Date</th>
						<td><%=appraisalList.get(18)%></td>
					</tr>
					<tr>
						<th valign="top" align="right">Appraisee</th>
						<td colspan="1"><%=appraisalList.get(12)%></td>
					</tr>
					<tr>
						<th align="right">Orientation</th>
						<td colspan="1"><%=appraisalList.get(2)%></td>
					</tr>
				</table>
		</div>

		<div id="mainDiv"
			style="float: left; margin: 10px 0px 0px 0px; width: 100%;">
			<s:hidden name="id"></s:hidden>
			<s:hidden name="appsystem"></s:hidden>
			<s:hidden name="scoreType"></s:hidden>
			<s:hidden name="type"></s:hidden>
			<s:hidden name="UID"></s:hidden>
			<%
					int counter = 0;
					String appsystem = request.getParameter("appsystem");
					String scoreType = request.getParameter("scoreType");
					String type = request.getParameter("type");

					if (appsystem != null && appsystem.equals("1")) {
						if (scoreType != null && scoreType.equals("1")) {
							if (type != null && type.equals("score")) {

			%>
			<ul class="ul_class">
				<li>
					 <table class="tb_style" style="width: 100%;">
						<tr>
							<th width="15%" style="text-align: right;">Level Type</th>
							<td>Competency
							</td>
						</tr>
						<tr>
							<th style="text-align: right;">Section Name</th>
							<td><input type="text" name="scoreSectionName"/></td>
						</tr>
						<tr>
							<th style="text-align: right;">Description</th>
							<td><input type="text" name="scoreCardDescription"/></td>
						</tr>
						<tr>
							<th style="text-align: right;">Weightage</th>
							<td><input type="text" name="scoreCardWeightage" value="100"/></td>
						</tr>
						<tr>
							<th style="text-align: right;">Select Attribute</th>
							<td>
								<%-- <select name="attribute"><%=request
												.getAttribute("attribute")%></select> --%> <s:select
									name="attribute" list="attributeList" theme="simple"
									listKey="id" id="attribute" listValue="name"></s:select></td>
						</tr>
					</table>
				</li>
				
				<li>
					<ul class="ul_class">
						<li>
							 <table class="tb_style" style="width: 100%;">
								<tr>
									<th width="15%" style="text-align: right;">Level Type</th>
									<td>Goals
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Section Name</th>
									<td><input type="text" name="goalSectionName"/>
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Description</th>
									<td><input type="text" name="goalDescription"/>
									</td>
								</tr>
								<tr>
									<th style="text-align: right;">Weightage</th>
									<td><input type="text" name="goalWeightage" value="100"/>
									</td>
								</tr>
							</table></li>
						
						<li>
							<ul class="ul_class">
								<li>
									 <table class="tb_style" style="width: 100%;">
										<tr>
											<th width="15%" style="text-align: right;">Level Type</th>
											<td>Objective 
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Section Name</th>
											<td><input type="text" name="objectiveSectionName"/></td>
										</tr>
										<tr>
											<th style="text-align: right;">Description</th>
											<td><input type="text" name="objectiveDescription"/></td>
										</tr>
										<tr>
											<th style="text-align: right;">Weightage</th>
											<td><input type="text" name="objectiveWeightage" value="100"/></td>
										</tr>
									</table>
								</li>
								
								<li>
									<ul class="ul_class">
										<li>
											 <table class="tb_style" style="width: 100%;">
												<tr>
													<th width="15%" style="text-align: right;">Level Type</th>
													<td>Measures <input type="hidden" name="measureID"/>
													</td>
												</tr>
												<tr>
													<th style="text-align: right;">Section Name</th>
													<td><input type="text" name="measuresSectionName"/>
													</td>
												</tr>
												<tr>
													<th style="text-align: right;">Description</th>
													<td><input type="text" name="measuresDescription"/>
													</td>
												</tr>
												<tr>
													<th style="text-align: right;">Weightage</th>
													<td><input type="text" name="measureWeightage" value="100"/>
													</td>
												</tr>
											</table></li>
										
										<li>
											<ul>
												<li>
													 <table class="tb_style" style="width: 100%;">
														<tr>
															<th width="15%" style="text-align: right;">Select Question</th>
															<td colspan="3"><select name="questionSelect"
																onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																	<option value="">Select Question</option>
																	<%=request.getAttribute("option")%>		
																	</select></td>
														</tr>
														<tr>
															<th style="text-align: right;">Weightage in(%)<input type="hidden"
																name="orientt" value="0" /></th>
															<td colspan="3"><input type="text" name="weightage"
																value="100" />
															</td>
														</tr>
														<tr id="QuestionName0" style="display: none">
															<th style="text-align: right;">Question Name:</th>
															<td colspan="3"><input type="text" name="question" />
															</td>
														</tr>
														<tr id="AddQuestion0" style="display: none">
															<th style="text-align: right;">Add to Question Bank:</th>
															<td colspan="3"><input name="addFlag"
																type="checkbox" id="addFlag0"
																onclick="changeStatus('0')" /> <input
																type="hidden" id="status0" name="status"
																value="0" />
															</td>
														</tr>
														<tr id="selectanstype0" style="display: none">
															<th style="text-align: right;">Select Answer Type</th>
															<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																	<%=request.getAttribute("anstype")%>
															</select><input type="hidden" name="questiontypename" value="0" /></td>
														</tr>
														<tr id="answerType0" style="display: none">
															<th style="text-align: right;">&nbsp;</th>
															<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
															<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
														</tr>
														<tr id="answerType10" style="display: none">
															<th style="text-align: right;">&nbsp;</th>
															<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
															<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
														</tr>
														<tr id="answerType20" style="display: none">
															<th style="text-align: right;">&nbsp;</th>
															<td>&nbsp;</td>
															<td colspan="2">&nbsp;</td>
														</tr>

														<%
															String memberList = (String) request.getAttribute("member");
															String[] memberArray = memberList.split(",");
															for (int c = 0; c < memberArray.length; c++) {
														%>
														<tr>
															<th style="text-align: right;"><%=memberArray[c]%></th>
															<td colspan="3">
																<input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																<input type="radio" name="<%=memberArray[c]%>" value="0" />
															</td>
														</tr>

														<% } %>
													</table>
												</li>
											</ul>
										</li>
										
									</ul></li>
								
							</ul>
						</li>

					</ul></li>
				
			</ul>


			<%
				}else if (type != null && type.equals("goal")){

					%>
					<ul class="ul_class">
								<li>
									 <table class="tb_style" style="width: 100%;">
										<tr>
											<th width="15%" style="text-align: right;">Level Type</th>
											<td>Goals
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Section Name</th>
											<td><input type="text" name="goalSectionName"/>
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Description</th>
											<td><input type="text" name="goalDescription"/>
											</td>
										</tr>
										<tr>
											<th style="text-align: right;">Weightage</th>
											<td><input type="text" name="goalWeightage" value="100"/>
											</td>
										</tr>
									</table></li>
								
								<li>
									<ul class="ul_class">
										<li>
											 <table class="tb_style" style="width: 100%;">
												<tr>
													<th width="15%" style="text-align: right;">Level Type</th>
													<td>Objective 
													</td>
												</tr>
												<tr>
													<th style="text-align: right;">Section Name</th>
													<td><input type="text" name="objectiveSectionName"/></td>
												</tr>
												<tr>
													<th style="text-align: right;">Description</th>
													<td><input type="text" name="objectiveDescription"/></td>
												</tr>
												<tr>
													<th style="text-align: right;">Weightage</th>
													<td><input type="text" name="objectiveWeightage" value="100"/></td>
												</tr>
											</table>
										</li>
										
										<li>
											<ul class="ul_class">
												<li>
													 <table class="tb_style" style="width: 100%;">
														<tr>
															<th width="15%" style="text-align: right;">Level Type</th>
															<td>Measures <input type="hidden" name="measureID"/>
															</td>
														</tr>
														<tr>
															<th style="text-align: right;">Section Name</th>
															<td><input type="text" name="measuresSectionName"/>
															</td>
														</tr>
														<tr>
															<th style="text-align: right;">Description</th>
															<td><input type="text" name="measuresDescription"/>
															</td>
														</tr>
														<tr>
															<th style="text-align: right;">Weightage</th>
															<td><input type="text" name="measureWeightage" value="100"/>
															</td>
														</tr>
													</table></li>
												
												<li>
													<ul>
														<li>
															 <table class="tb_style" style="width: 100%;">
																<tr>
																	<th width="15%" style="text-align: right;">Select Question</th>
																	<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																		<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																	</td>
																</tr>
																<tr>
																	<th style="text-align: right;">Weightage in(%)<input type="hidden"
																		name="orientt" value="0" /></th>
																	<td colspan="3"><input type="text" name="weightage" value="100" />
																	</td>
																</tr>
																<tr id="QuestionName0" style="display: none">
																	<th style="text-align: right;">Question Name:</th>
																	<td colspan="3"><input type="text" name="question" />
																	</td>
																</tr>
																<tr id="AddQuestion0" style="display: none">
																	<th style="text-align: right;">Add to Question Bank:</th>
																	<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" />
																			<input type="hidden" id="status0" name="status" value="0" />
																	</td>
																</tr>
																<tr id="selectanstype0" style="display: none">
																	<th style="text-align: right;">Select Answer Type</th>
																	<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																			<%=request.getAttribute("anstype")%>
																	</select><input type="hidden" name="questiontypename"value="0" /></td>
																</tr>
																<tr id="answerType0" style="display: none">
																	<th style="text-align: right;">&nbsp;</th>
																	<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																	<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																</tr>
																<tr id="answerType10" style="display: none">
																	<th style="text-align: right;">&nbsp;</th>
																	<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																	<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																</tr>
																<tr id="answerType20" style="display: none">
																	<th style="text-align: right;">&nbsp;</th>
																	<td>&nbsp;</td>
																	<td colspan="2">&nbsp;</td>
																</tr>
																<%
																	String memberList = (String) request.getAttribute("member");
																	String[] memberArray = memberList.split(",");
																	for (int c = 0; c < memberArray.length; c++) {
																%>
																<tr>
																	<th style="text-align: right;"><%=memberArray[c]%></th>
																	<td colspan="3">
																		 <input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																		<input type="radio" name="<%=memberArray[c]%>" value="0" />
																	</td>
																</tr>

																<% } %>
															</table>
														</li>
													</ul>
												</li>
												
											</ul></li>
										
									</ul>
								</li>

							</ul>


					<%
						}else if (type != null && type.equals("objective")){

							%>
									<ul class="ul_class">
												<li>
													 <table class="tb_style" style="width: 100%;">
														<tr>
															<th width="15%" style="text-align: right;">Level Type</th>
															<td>Objective </td>
														</tr>
														<tr>
															<th style="text-align: right;">Section Name</th>
															<td><input type="text" name="objectiveSectionName"/></td>
														</tr>
														<tr>
															<th style="text-align: right;">Description</th>
															<td><input type="text" name="objectiveDescription"/></td>
														</tr>
														<tr>
															<th style="text-align: right;">Weightage</th>
															<td><input type="text" name="objectiveWeightage" value="100"/></td>
														</tr>
													</table>
												</li>
												
												<li>
													<ul class="ul_class">
														<li>
															 <table class="tb_style" style="width: 100%;">
																<tr>
																	<th width="15%" style="text-align: right;">Level Type</th>
																	<td>Measures <input type="hidden" name="measureID"/>
																	</td>
																</tr>
																<tr>
																	<th style="text-align: right;">Section Name</th>
																	<td><input type="text" name="measuresSectionName"/>
																	</td>
																</tr>
																<tr>
																	<th style="text-align: right;">Description</th>
																	<td><input type="text" name="measuresDescription"/>
																	</td>
																</tr>
																<tr>
																	<th style="text-align: right;">Weightage</th>
																	<td><input type="text" name="measureWeightage" value="100"/>
																	</td>
																</tr>
															</table></li>
														
														<li>
															<ul>
																<li>
																	 <table class="tb_style" style="width: 100%;">
																		<tr>
																			<th width="15%" style="text-align: right;">Select Question</th>
																			<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																					<option value="">Select Question</option><%=request.getAttribute("option")%></select></td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																			<td colspan="3"><input type="text" name="weightage" value="100" />
																			</td>
																		</tr>
																		<tr id="QuestionName0" style="display: none">
																			<th style="text-align: right;">Question Name:</th>
																			<td colspan="3"><input type="text" name="question" />
																			</td>
																		</tr>
																		<tr id="AddQuestion0" style="display: none">
																			<th style="text-align: right;">Add to Question Bank:</th>
																			<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" />
																				<input type="hidden" id="status0" name="status" value="0" />
																			</td>
																		</tr>
																		<tr id="selectanstype0" style="display: none">
																			<th style="text-align: right;">Select Answer Type</th>
																			<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																					<%=request.getAttribute("anstype")%>
																				</select><input type="hidden" name="questiontypename" value="0" />
																			</td>
																		</tr>
																		<tr id="answerType0" style="display: none">
																			<th style="text-align: right;">&nbsp;</th>
																			<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																			<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																		</tr>
																		<tr id="answerType10" style="display: none">
																			<th style="text-align: right;">&nbsp;</th>
																			<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																			<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																		</tr>
																		<tr id="answerType20" style="display: none">
																			<th style="text-align: right;">&nbsp;</th>
																			<td>&nbsp;</td>
																			<td colspan="2">&nbsp;</td>
																		</tr>
																		<%
																			String memberList = (String) request.getAttribute("member");
																			String[] memberArray = memberList.split(",");
																			for (int c = 0; c < memberArray.length; c++) {
																		%>
																		<tr>
																			<th style="text-align: right;"><%=memberArray[c]%></th>
																			<td colspan="3">
																				<input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																				<input type="radio" name="<%=memberArray[c]%>" value="0" />
																			</td>
																		</tr>

																		<% } %>
																	</table>
																</li>
															</ul>
														</li>
														
													</ul></li>
												
											</ul>


							<% } else if (type != null && type.equals("measure")) { %>
											
															<ul class="ul_class">
																<li>
																	 <table class="tb_style" style="width: 100%;">
																		<tr>
																			<th width="15%" style="text-align: right;">Level Type</th>
																			<td>Measures <input type="hidden" name="measureID"/> </td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Section Name</th>
																			<td><input type="text" name="measuresSectionName"/> </td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Description</th>
																			<td><input type="text" name="measuresDescription"/>
																			</td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Weightage</th>
																			<td><input type="text" name="measureWeightage" value="100"/>
																			</td>
																		</tr>
																	</table></li>
																
																<li>
																	<ul>
																		<li>
																			 <table class="tb_style" style="width: 100%;">
																				<tr>
																					<th width="15%" style="text-align: right;">Select Question</th>
																					<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																							<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																					</td>
																				</tr>
																				<tr>
																					<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																					<td colspan="3"><input type="text" name="weightage" value="100" />
																					</td>
																				</tr>
																				<tr id="QuestionName0" style="display: none">
																					<th style="text-align: right;">Question Name:</th>
																					<td colspan="3"><input type="text" name="question" />
																					</td>
																				</tr>
																				<tr id="AddQuestion0" style="display: none">
																					<th style="text-align: right;">Add to Question Bank:</th>
																					<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" />
																						<input type="hidden" id="status0" name="status" value="0" />
																					</td>
																				</tr>
																				<tr id="selectanstype0" style="display: none">
																					<th style="text-align: right;">Select Answer Type</th>
																					<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																							<%=request.getAttribute("anstype")%>
																						</select><input type="hidden" name="questiontypename"value="0" />
																					</td>
																				</tr>
																				<tr id="answerType0" style="display: none">
																					<th style="text-align: right;">&nbsp;</th>
																					<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																					<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" /></td>
																				</tr>
																				<tr id="answerType10" style="display: none">
																					<th style="text-align: right;">&nbsp;</th>
																					<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																					<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																				</tr>
																				<tr id="answerType20" style="display: none">
																					<th style="text-align: right;">&nbsp;</th>
																					<td>&nbsp;</td>
																					<td colspan="2">&nbsp;</td>
																				</tr>
																				<%
																					String memberList = (String) request.getAttribute("member");
																					String[] memberArray = memberList.split(",");
																					for (int c = 0; c < memberArray.length; c++) {
																				%>
																				<tr>
																					<th style="text-align: right;"><%=memberArray[c]%></th>
																					<td colspan="3">
																						<input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																						<input type="radio" name="<%=memberArray[c]%>" value="0" />
																					</td>
																				</tr>
																				<% } %>
																			</table>
																		</li>
																	</ul>
																</li>
															</ul>


												<% } else if (type != null && type.equals("quest")) { %>
																			<ul class="ul_class">
																				<li>
																					 <table class="tb_style" style="width: 100%;">
																						<tr>
																							<th width="15%" style="text-align: right;">Select Question</th>
																							<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																								<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																							</td>
																						</tr>
																						<tr>
																							<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																							<td colspan="3"><input type="text" name="weightage" value="100" /></td>
																						</tr>
																						<tr id="QuestionName0" style="display: none">
																							<th style="text-align: right;">Question Name:</th>
																							<td colspan="3"><input type="text" name="question" /></td>
																						</tr>
																						<tr id="AddQuestion0" style="display: none">
																							<th style="text-align: right;">Add to Question Bank:</th>
																							<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" />
																								<input type="hidden" id="status0" name="status" value="0" />
																							</td>
																						</tr>
																						<tr id="selectanstype0" style="display: none">
																							<th style="text-align: right;">Select Answer Type</th>
																							<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																									<%=request.getAttribute("anstype")%>
																								</select><input type="hidden" name="questiontypename" value="0" />
																							</td>
																						</tr>
																						<tr id="answerType0" style="display: none">
																							<th style="text-align: right;">&nbsp;</th>
																							<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																							<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" /> </td>
																						</tr>
																						<tr id="answerType10" style="display: none">
																							<th style="text-align: right;">&nbsp;</th>
																							<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																							<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																						</tr>
																						<tr id="answerType20" style="display: none">
																							<th style="text-align: right;">&nbsp;</th>
																							<td>&nbsp;</td>
																							<td colspan="2">&nbsp;</td>
																						</tr>
																						<%
																							String memberList = (String) request.getAttribute("member");
																							String[] memberArray = memberList.split(",");
																							for (int c = 0; c < memberArray.length; c++) {
																						%>
																						<tr>
																							<th style="text-align: right;"><%=memberArray[c]%></th>
																							<td colspan="3"><input type="radio" name="<%=memberArray[c] + "0"%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																								<input type="radio" name="<%=memberArray[c] + "0"%>" value="0" />
																							</td>
																						</tr>
																						<% } %>
																					</table>
																				</li>
																			</ul>

											<%
												}
										} else if (scoreType != null && scoreType.equals("3")) {
											if (type != null && type.equals("score")) {

									%>
									<ul class="ul_class">
										<li>
											 <table class="tb_style" style="width: 100%;">
												<tr>
													<th width="15%" style="text-align: right;">Level Type</th>
													<td>Competency</td>
												</tr>
												<tr>
													<th style="text-align: right;">Section Name</th>
													<td><input type="text" name="scoreSectionName"/></td>
												</tr>
												<tr>
													<th style="text-align: right;">Description</th>
													<td><input type="text" name="scoreCardDescription"/></td>
												</tr>
												<tr>
													<th style="text-align: right;">Weightage</th>
													<td><input type="text" name="scoreCardWeightage" value="100"/></td>
												</tr>
												<tr>
													<th style="text-align: right;">Select Attribute</th>
													<td>
														<%-- <select name="attribute"><%=request.getAttribute("attribute")%></select> --%> 
														<s:select name="attribute" list="attributeList" theme="simple" listKey="id" id="attribute" listValue="name"></s:select>
													</td>
												</tr>
											</table>
										</li>
										
										<li>
											<ul class="ul_class">
												<li>
													 <table class="tb_style" style="width: 100%;">
														<tr>
															<th width="15%" style="text-align: right;">Level Type</th>
															<td>Goals</td>
														</tr>
														<tr>
															<th style="text-align: right;">Section Name</th>
															<td><input type="text" name="goalSectionName"/></td>
														</tr>
														<tr>
															<th style="text-align: right;">Description</th>
															<td><input type="text" name="goalDescription"/></td>
														</tr>
														<tr>
															<th style="text-align: right;">Weightage</th>
															<td><input type="text" name="goalWeightage" value="100"/></td>
														</tr>
													</table></li>
																										
														<li>
															<ul class="ul_class">
																<li>
																	 <table class="tb_style" style="width: 100%;">
																		<tr>
																			<th width="15%" style="text-align: right;">Level Type</th>
																			<td>Measures <input type="hidden" name="measureID"/></td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Section Name</th>
																			<td><input type="text" name="measuresSectionName"/></td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Description</th>
																			<td><input type="text" name="measuresDescription"/></td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Weightage</th>
																			<td><input type="text" name="measureWeightage" value="100"/></td>
																		</tr>
																	</table></li>
																
																<li>
																	<ul>
																		<li>
																			 <table class="tb_style" style="width: 100%;">
																				<tr>
																					<th width="15%" style="text-align: right;">Select Question</th>
																					<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																						<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																					</td>
																				</tr>
																				<tr>
																					<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																					<td colspan="3"><input type="text" name="weightage" value="100" /></td>
																				</tr>
																				<tr id="QuestionName0" style="display: none">
																					<th style="text-align: right;">Question Name:</th>
																					<td colspan="3"><input type="text" name="question" /></td>
																				</tr>
																				<tr id="AddQuestion0" style="display: none">
																					<th style="text-align: right;">Add to Question Bank:</th>
																					<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" />
																						<input type="hidden" id="status0" name="status" value="0" />
																					</td>
																				</tr>
																				<tr id="selectanstype0" style="display: none">
																					<th style="text-align: right;">Select Answer Type</th>
																					<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																						<%=request.getAttribute("anstype")%>
																						</select><input type="hidden" name="questiontypename"value="0" />
																					</td>
																				</tr>
																				<tr id="answerType0" style="display: none">
																					<th style="text-align: right;">&nbsp;</th>
																					<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																					<td colspan="2">b)<input type="text" name="optionb" /> <input type="checkbox" name="correct0" value="b" /></td>
																				</tr>
																				<tr id="answerType10" style="display: none">
																					<th style="text-align: right;">&nbsp;</th>
																					<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																					<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																				</tr>
																				<tr id="answerType20" style="display: none">
																					<th style="text-align: right;">&nbsp;</th>
																					<td>&nbsp;</td>
																					<td colspan="2">&nbsp;</td>
																				</tr>

																				<%
																					String memberList = (String) request.getAttribute("member");
																					String[] memberArray = memberList.split(",");
																					for (int c = 0; c < memberArray.length; c++) {
																				%>
																				<tr>
																					<th style="text-align: right;"><%=memberArray[c]%></th>
																					<td colspan="3">
																						<input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																						<input type="radio" name="<%=memberArray[c]%>" value="0" />
																					</td>
																				</tr>
																				<% } %>
																			</table>
																		</li>
																	</ul>
																</li>
																
															</ul></li>
											</ul></li>
										
									</ul>


									<% } else if (type != null && type.equals("goal")) { %>
											<ul class="ul_class">
														<li>
															 <table class="tb_style" style="width: 100%;">
																<tr>
																	<th width="15%" style="text-align: right;">Level Type</th>
																	<td>Goals</td>
																</tr>
																<tr>
																	<th style="text-align: right;">Section Name</th>
																	<td><input type="text" name="goalSectionName"/></td>
																</tr>
																<tr>
																	<th style="text-align: right;">Description</th>
																	<td><input type="text" name="goalDescription"/></td>
																</tr>
																<tr>
																	<th style="text-align: right;">Weightage</th>
																	<td><input type="text" name="goalWeightage" value="100"/></td>
																</tr>
															</table></li>
														
														
																
																<li>
																	<ul class="ul_class">
																		<li>
																			 <table class="tb_style" style="width: 100%;">
																				<tr>
																					<th width="15%" style="text-align: right;">Level Type</th>
																					<td>Measures <input type="hidden" name="measureID"/></td>
																				</tr>
																				<tr>
																					<th style="text-align: right;">Section Name</th>
																					<td><input type="text" name="measuresSectionName"/></td>
																				</tr>
																				<tr>
																					<th style="text-align: right;">Description</th>
																					<td><input type="text" name="measuresDescription"/></td>
																				</tr>
																				<tr>
																					<th style="text-align: right;">Weightage</th>
																					<td><input type="text" name="measureWeightage" value="100"/></td>
																				</tr>
																			</table></li>
																		
																		<li>
																			<ul>
																				<li>
																					 <table class="tb_style" style="width: 100%;">
																						<tr>
																							<th width="15%" style="text-align: right;">Select Question</th>
																							<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																								<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																							</td>
																						</tr>
																						<tr>
																							<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																							<td colspan="3"><input type="text" name="weightage" value="100" /></td>
																						</tr>
																						<tr id="QuestionName0" style="display: none">
																							<th style="text-align: right;">Question Name:</th>
																							<td colspan="3"><input type="text" name="question" /></td>
																						</tr>
																						<tr id="AddQuestion0" style="display: none">
																							<th style="text-align: right;">Add to Question Bank:</th>
																							<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" />
																								<input type="hidden" id="status0" name="status" value="0" />
																							</td>
																						</tr>
																						<tr id="selectanstype0" style="display: none">
																							<th style="text-align: right;">Select Answer Type</th>
																							<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																								<%=request.getAttribute("anstype")%>
																								</select><input type="hidden" name="questiontypename" value="0" />
																							</td>
																						</tr>
																						<tr id="answerType0" style="display: none">
																							<th style="text-align: right;">&nbsp;</th>
																							<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																							<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																						</tr>
																						<tr id="answerType10" style="display: none">
																							<th style="text-align: right;">&nbsp;</th>
																							<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																							<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																						</tr>
																						<tr id="answerType20" style="display: none">
																							<th style="text-align: right;">&nbsp;</th>
																							<td>&nbsp;</td>
																							<td colspan="2">&nbsp;</td>
																						</tr>
																						<%
																							String memberList = (String) request.getAttribute("member");
																							String[] memberArray = memberList.split(",");
																							for (int c = 0; c < memberArray.length; c++) {
																						%>
																						<tr>
																							<th style="text-align: right;"><%=memberArray[c]%></th>
																							<td colspan="3">
																								<input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																								<input type="radio" name="<%=memberArray[c]%>" value="0" />
																							</td>
																						</tr>

																						<% } %>
																					</table>
																				</li>
																			</ul>
																		</li>
																	</ul></li>
														</ul>

												<% } else if (type != null && type.equals("measure")) { %>
																	
																					<ul class="ul_class">
																						<li>
																							 <table class="tb_style" style="width: 100%;">
																								<tr>
																									<th width="15%" style="text-align: right;">Level Type</th>
																									<td>Measures <input type="hidden" name="measureID"/></td>
																								</tr>
																								<tr>
																									<th style="text-align: right;">Section Name</th>
																									<td><input type="text" name="measuresSectionName"/></td>
																								</tr>
																								<tr>
																									<th style="text-align: right;">Description</th>
																									<td><input type="text" name="measuresDescription"/></td>
																								</tr>
																								<tr>
																									<th style="text-align: right;">Weightage</th>
																									<td><input type="text" name="measureWeightage" value="100"/></td>
																								</tr>
																							</table></li>
																						<li>
																							<ul>
																								<li>
																									 <table class="tb_style" style="width: 100%;">
																										<tr>
																											<th width="15%" style="text-align: right;">Select Question</th>
																											<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																												<option value="">Select Question</option><%=request .getAttribute("option")%></select></td>
																										</tr>
																										<tr>
																											<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																											<td colspan="3"><input type="text" name="weightage" value="100" /></td>
																										</tr>
																										<tr id="QuestionName0" style="display: none">
																											<th style="text-align: right;">Question Name:</th>
																											<td colspan="3"><input type="text" name="question" /></td>
																										</tr>
																										<tr id="AddQuestion0" style="display: none">
																											<th style="text-align: right;">Add to Question Bank:</th>
																											<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" />
																												<input type="hidden" id="status0" name="status" value="0" />
																											</td>
																										</tr>
																										<tr id="selectanstype0" style="display: none">
																											<th style="text-align: right;">Select Answer Type</th>
																											<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																												<%=request.getAttribute("anstype")%>
																												</select><input type="hidden" name="questiontypename" value="0" />
																											</td>
																										</tr>
																										<tr id="answerType0" style="display: none">
																											<th style="text-align: right;">&nbsp;</th>
																											<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																											<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																										</tr>
																										<tr id="answerType10" style="display: none">
																											<th style="text-align: right;">&nbsp;</th>
																											<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																											<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																										</tr>
																										<tr id="answerType20" style="display: none">
																											<th style="text-align: right;">&nbsp;</th>
																											<td>&nbsp;</td>
																											<td colspan="2">&nbsp;</td>
																										</tr>

																										<%
																											String memberList = (String) request.getAttribute("member");
																											String[] memberArray = memberList.split(",");
																											for (int c = 0; c < memberArray.length; c++) {
																										%>
																										<tr>
																											<th style="text-align: right;"><%=memberArray[c]%></th>
																											<td colspan="3">
																												<input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																												<input type="radio" name="<%=memberArray[c]%>" value="0" />
																											</td>
																										</tr>

																										<% } %>
																									</table>
																								</li>
																							</ul>
																						</li>
																						
																					</ul>


															<% } else if (type != null && type.equals("quest")) { %>
																									<ul class="ul_class">
																										<li>
																											 <table class="tb_style" style="width: 100%;">
																												<tr>
																													<th width="15%" style="text-align: right;">Select Question</th>
																													<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																														<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																													</td>																												</tr>
																												<tr>
																													<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																													<td colspan="3"><input type="text" name="weightage" value="100" />
																													</td>
																												</tr>
																												<tr id="QuestionName0" style="display: none">
																													<th style="text-align: right;">Question Name:</th>
																													<td colspan="3"><input type="text" name="question" /></td>
																												</tr>
																												<tr id="AddQuestion0" style="display: none">
																													<th style="text-align: right;">Add to Question Bank:</th>
																													<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" /> 
																														<input type="hidden" id="status0" name="status" value="0" />
																													</td>
																												</tr>
																												<tr id="selectanstype0" style="display: none">
																													<th style="text-align: right;">Select Answer Type</th>
																													<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																														<%=request.getAttribute("anstype")%>
																														</select><input type="hidden" name="questiontypename" value="0" />
																													</td>
																												</tr>
																												<tr id="answerType0" style="display: none">
																													<th style="text-align: right;">&nbsp;</th>
																													<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																													<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																												</tr>
																												<tr id="answerType10" style="display: none">
																													<th style="text-align: right;">&nbsp;</th>
																													<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																													<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																												</tr>
																												<tr id="answerType20" style="display: none">
																													<th style="text-align: right;">&nbsp;</th>
																													<td>&nbsp;</td>
																													<td colspan="2">&nbsp;</td>
																												</tr>
																												<%
																													String memberList = (String) request.getAttribute("member");
																													String[] memberArray = memberList.split(",");
																													for (int c = 0; c < memberArray.length; c++) {
																												%>
																												<tr>
																													<th style="text-align: right;"><%=memberArray[c]%></th>
																													<td colspan="3">
																														<input type="radio" name="<%=memberArray[c] + "0"%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																														<input type="radio" name="<%=memberArray[c] + "0"%>" value="0" />
																													</td>
																												</tr>

																												<% } %>
																											</table>
																										</li>
																									</ul>

																	<%
																		}
																		} else if (scoreType != null && scoreType.equals("2")) {
																			if (type != null && type.equals("score")) {
																	%>
															<ul class="ul_class">
																<li>
																	 <table class="tb_style" style="width: 100%;">
																		<tr>
																			<th width="15%" style="text-align: right;">Level Type</th>
																			<td>Competency</td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Section Name</th>
																			<td><input type="text" name="scoreSectionName"/></td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Description</th>
																			<td><input type="text" name="scoreCardDescription"/></td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Weightage</th>
																			<td><input type="text" name="scoreCardWeightage" value="100"/></td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Select Attribute</th>
																			<td>
																				<%-- <select name="attribute"><%=request.getAttribute("attribute")%></select> --%>
																				<s:select name="attribute" list="attributeList" theme="simple" listKey="id" id="attribute" listValue="name"></s:select>
																			</td>
																		</tr>
																	</table>
																</li>
																
																																
																				<li>
																					<ul class="ul_class">
																						<li>
																							 <table class="tb_style" style="width: 100%;">
																								<tr>
																									<th width="15%" style="text-align: right;">Level Type</th>
																									<td>Measures <input type="hidden" name="measureID"/></td>
																								</tr>
																								<tr>
																									<th style="text-align: right;">Section Name</th>
																									<td><input type="text" name="measuresSectionName"/></td>
																								</tr>
																								<tr>
																									<th style="text-align: right;">Description</th>
																									<td><input type="text" name="measuresDescription"/></td>
																								</tr>
																								<tr>
																									<th style="text-align: right;">Weightage</th>
																									<td><input type="text" name="measureWeightage" value="100"/></td>
																								</tr>
																							</table></li>
																						
																						<li>
																							<ul>
																								<li>
																									 <table class="tb_style" style="width: 100%;">
																										<tr>
																											<th width="15%" style="text-align: right;">Select Question</th>
																											<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																												<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																											</td>
																										</tr>
																										<tr>
																											<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																											<td colspan="3"><input type="text" name="weightage" value="100" /></td>
																										</tr>
																										<tr id="QuestionName0" style="display: none">
																											<th style="text-align: right;">Question Name:</th>
																											<td colspan="3"><input type="text" name="question" /></td>
																										</tr>
																										<tr id="AddQuestion0" style="display: none">
																											<th style="text-align: right;">Add to Question Bank:</th>
																											<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" /> 
																												<input type="hidden" id="status0" name="status" value="0" />
																											</td>
																										</tr>
																										<tr id="selectanstype0" style="display: none">
																											<th style="text-align: right;">Select Answer Type</th>
																											<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																												<%=request.getAttribute("anstype")%>
																												</select><input type="hidden" name="questiontypename"value="0" />
																											</td>
																										</tr>
																										<tr id="answerType0" style="display: none">
																											<th style="text-align: right;">&nbsp;</th>
																											<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																											<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																										</tr>
																										<tr id="answerType10" style="display: none">
																											<th style="text-align: right;">&nbsp;</th>
																											<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																											<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																										</tr>
																										<tr id="answerType20" style="display: none">
																											<th style="text-align: right;">&nbsp;</th>
																											<td>&nbsp;</td>
																											<td colspan="2">&nbsp;</td>
																										</tr>

																										<%
																											String memberList = (String) request.getAttribute("member");
																											String[] memberArray = memberList.split(",");
																											for (int c = 0; c < memberArray.length; c++) {
																										%>
																										<tr>
																											<th style="text-align: right;"><%=memberArray[c]%></th>
																											<td colspan="3">
																												<input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																												<input type="radio" name="<%=memberArray[c]%>" value="0" />
																											</td>
																										</tr>

																										<% } %>
																									</table>
																								</li>
																							</ul>
																						</li>
																						
																					</ul></li>
																
																		</ul>


															<% } else if (type != null && type.equals("measure")) { %>
																							
																											<ul class="ul_class">
																												<li>
																													 <table class="tb_style" style="width: 100%;">
																														<tr>
																															<th width="15%" style="text-align: right;">Level Type</th>
																															<td>Measures <input type="hidden" name="measureID"/></td>
																														</tr>
																														<tr>
																															<th style="text-align: right;">Section Name</th>
																															<td><input type="text" name="measuresSectionName"/></td>
																														</tr>
																														<tr>
																															<th style="text-align: right;">Description</th>
																															<td><input type="text" name="measuresDescription"/></td>
																														</tr>
																														<tr>
																															<th style="text-align: right;">Weightage</th>
																															<td><input type="text" name="measureWeightage" value="100"/></td>
																														</tr>
																													</table></li>
																												
																												<li>
																													<ul>
																														<li>
																															 <table class="tb_style" style="width: 100%;">
																																<tr>
																																	<th width="15%" style="text-align: right;">Select Question</th>
																																	<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																																		<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																																	</td>
																																</tr>
																																<tr>
																																	<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																																	<td colspan="3"><input type="text" name="weightage" value="100" /></td>
																																</tr>
																																<tr id="QuestionName0" style="display: none">
																																	<th style="text-align: right;">Question Name:</th>
																																	<td colspan="3"><input type="text" name="question" /></td>
																																</tr>
																																<tr id="AddQuestion0" style="display: none">
																																	<th style="text-align: right;">Add to Question Bank:</th>
																																	<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" /> 
																																		<input type="hidden" id="status0" name="status" value="0" />
																																	</td>
																																</tr>
																																<tr id="selectanstype0" style="display: none">
																																	<th style="text-align: right;">Select Answer Type</th>
																																	<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																																		<%=request.getAttribute("anstype")%>
																																		</select><input type="hidden" name="questiontypename" value="0" />
																																	</td>
																																</tr>
																																<tr id="answerType0" style="display: none">
																																	<th style="text-align: right;">&nbsp;</th>
																																	<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																																	<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																																</tr>
																																<tr id="answerType10" style="display: none">
																																	<th style="text-align: right;">&nbsp;</th>
																																	<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																																	<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																																</tr>
																																<tr id="answerType20" style="display: none">
																																	<th style="text-align: right;">&nbsp;</th>
																																	<td>&nbsp;</td>
																																	<td colspan="2">&nbsp;</td>
																																</tr>

																																<%
																																	String memberList = (String) request.getAttribute("member");
																																	String[] memberArray = memberList.split(",");
																																	for (int c = 0; c < memberArray.length; c++) {
																																%>
																																<tr>
																																	<th style="text-align: right;"><%=memberArray[c]%></th>
																																	<td colspan="3">
																																		<input type="radio" name="<%=memberArray[c]%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																																		<input type="radio" name="<%=memberArray[c]%>" value="0" />
																																	</td>
																																</tr>

																																<% } %>
																															</table>
																														</li>
																													</ul>
																												</li>
																												
																											</ul>


																							<% } else if (type != null && type.equals("quest")) { %>
																															<ul class="ul_class">
																																<li>
																																	 <table class="tb_style" style="width: 100%;">
																																		<tr>
																																			<th width="15%" style="text-align: right;">Select Question</th>
																																			<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																																				<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																																			</td>
																																		</tr>
																																		<tr>
																																			<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																																			<td colspan="3"><input type="text" name="weightage" value="100" /></td>
																																		</tr>
																																		<tr id="QuestionName0" style="display: none">
																																			<th style="text-align: right;">Question Name:</th>
																																			<td colspan="3"><input type="text" name="question" /></td>
																																		</tr>
																																		<tr id="AddQuestion0" style="display: none">
																																			<th style="text-align: right;">Add to Question Bank:</th>
																																			<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" /> 
																																				<input type="hidden" id="status0" name="status" value="0" />
																																			</td>
																																		</tr>
																																		<tr id="selectanstype0" style="display: none">
																																			<th style="text-align: right;">Select Answer Type</th>
																																			<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																																				<%=request.getAttribute("anstype")%>
																																				</select><input type="hidden" name="questiontypename" value="0" />
																																			</td>
																																		</tr>
																																		<tr id="answerType0" style="display: none">
																																			<th style="text-align: right;">&nbsp;</th>
																																			<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																																			<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																																		</tr>
																																		<tr id="answerType10" style="display: none">
																																			<th style="text-align: right;">&nbsp;</th>
																																			<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																																			<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																																		</tr>
																																		<tr id="answerType20" style="display: none">
																																			<th style="text-align: right;">&nbsp;</th>
																																			<td>&nbsp;</td>
																																			<td colspan="2">&nbsp;</td>
																																		</tr>

																																		<%
																																			String memberList = (String) request.getAttribute("member");
																																			String[] memberArray = memberList.split(",");
																																			for (int c = 0; c < memberArray.length; c++) {
																																		%>
																																		<tr>
																																			<th style="text-align: right;"><%=memberArray[c]%></th>
																																			<td colspan="3">
																																				<input type="radio" name="<%=memberArray[c] + "0"%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																																				<input type="radio" name="<%=memberArray[c] + "0"%>" value="0" />
																																			</td>
																																		</tr>

																																		<% } %>
																																	</table>
																																</li>
																															</ul>

																							<%
																									}
																								}
																							} else if(appsystem != null && appsystem.equals("2")) {
																							%>
															<ul class="ul_class">
																<li>
																	 <table class="tb_style" style="width: 100%;">
																		<tr>
																			<th width="15%" style="text-align: right;">Select Question</th>
																			<td colspan="3"><select name="questionSelect" onchange="addNewQuestion('addNewQuestionId0',this.value,'0');">
																				<option value="">Select Question</option><%=request.getAttribute("option")%></select>
																			</td>
																		</tr>
																		<tr>
																			<th style="text-align: right;">Weightage in(%)<input type="hidden" name="orientt" value="0" /></th>
																			<td colspan="3"><input type="text" name="weightage" value="100" /></td>
																		</tr>
																		<tr id="QuestionName0" style="display: none">
																			<th style="text-align: right;">Question Name:</th>
																			<td colspan="3"><input type="text" name="question" /></td>
																		</tr>
																		<tr id="AddQuestion0" style="display: none">
																			<th style="text-align: right;">Add to Question Bank:</th>
																			<td colspan="3"><input name="addFlag" type="checkbox" id="addFlag0" onclick="changeStatus('0')" /> 
																				<input type="hidden" id="status0" name="status" value="0" />
																			</td>
																		</tr>
																		<tr id="selectanstype0" style="display: none">
																			<th style="text-align: right;">Select Answer Type</th>
																			<td colspan="3"><select name="ansType0" onchange="changeNewQuestionType(this.value,'answerType0','answerType10','answerType20','0')" value="1">
																				<%=request.getAttribute("anstype")%>
																				</select><input type="hidden" name="questiontypename" value="0" />
																			</td>
																		</tr>
																		<tr id="answerType0" style="display: none">
																			<th style="text-align: right;">&nbsp;</th>
																			<td>a)<input type="text" name="optiona" /><input type="checkbox" value="a" name="correct0" /></td>
																			<td colspan="2">b)<input type="text" name="optionb" /><input type="checkbox" name="correct0" value="b" /></td>
																		</tr>
																		<tr id="answerType10" style="display: none">
																			<th style="text-align: right;">&nbsp;</th>
																			<td>c)<input type="text" name="optionc" /> <input type="checkbox" name="correct0" value="c" /></td>
																			<td colspan="2">d)<input type="text" name="optiond" /><input type="checkbox" name="correct0" value="d" /></td>
																		</tr>
																		<tr id="answerType20" style="display: none">
																			<th style="text-align: right;">&nbsp;</th>
																			<td>&nbsp;</td>
																			<td colspan="2">&nbsp;</td>
																		</tr>

																		<%
																			String memberList = (String) request.getAttribute("member");
																			String[] memberArray = memberList.split(",");
																			for (int c = 0; c < memberArray.length; c++) {
																		%>
																		<tr>
																			<th style="text-align: right;"><%=memberArray[c]%></th>
																			<td colspan="3">
																				<input type="radio" name="<%=memberArray[c] + "0"%>" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																				<input type="radio" name="<%=memberArray[c] + "0"%>" value="0" />
																			</td>
																		</tr>

																		<% } %>
																	</table>
																</li>
															</ul>

										<% } %>


			<div>
				<s:submit value="Save" cssClass="input_button" name="submit"></s:submit>
			</div>
		</div>
	</s:form>
</div>

