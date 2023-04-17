<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<%
	List<List<String>> questionList = (List<List<String>>) request.getAttribute("questionList");

	Map<String,List<List<String>>> hmLevelQuestio1n = (Map<String,List<List<String>>>) request.getAttribute("hmLevelQuestion");
	Map hmLevelDetails = (Map) request.getAttribute("hmLevelDetails");
	
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
	Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
	List<String> answerTypeList = new ArrayList<String>();
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
	List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
	List<String> empList = (List<String>) request.getAttribute("empList");
	
	Map<String, String> hmEmpDetails = (Map) request.getAttribute("hmEmpDetails");
	Map<String, String> levelStatus = (Map<String, String>) request.getAttribute("LEVEL_STATUS");
	List<String> levelList = (List<String>) request.getAttribute("levelList");
	List<String> mainLevelList = (List<String>) request.getAttribute("mainLevelList");
	String currentLevel = (String) request.getAttribute("currentLevel");
	Map<String, List<List<String>>> hmSection =(Map<String, List<List<String>>> )request.getAttribute("hmSection");
	List<String> sectionIDList = (List<String>)request.getAttribute("sectionIDList");
	Map<String, String> hmMemberPosition = (Map<String, String>)request.getAttribute("hmMemberPosition");
	//System.out.println("hmMemberPosition 11111 ----> "+hmMemberPosition);
	
	String fromPage = (String) request.getAttribute("fromPage");
	
/* ===start parvez date: 27-02-2023=== */
	Map<String, String> othrQueType = (Map<String, String>) request.getAttribute("othrQueType");
	if(othrQueType==null) othrQueType = new HashMap<String, String>();;
/* ===end parvez date: 27-02-2023=== */
%>

<div class="leftbox reportWidth">
<%if(appraisalList != null && appraisalList.size()>0 && !appraisalList.isEmpty()) { %>
		<div style="float: left; width: 100%; font-size: 18px; font-weight: bold; margin-bottom: 9px;">
			<div style="display:inline;"><%=uF.showData(appraisalList.get(1), "")%></div>
			<%-- <% if(!uF.showData(appraisalList.get(5), "").equals("")) { %>
				<div style="display:inline; font-size: 14px; margin-left: 7px; font-weight: normal;"><%=uF.showData(appraisalList.get(5), "") %></div>
			<% } %>
			<% if(!uF.showData(appraisalList.get(9), "").equals("")) { %>
				<div style="display:inline; font-size: 14px; margin-left: 7px; font-weight: normal;"><%=uF.showData(appraisalList.get(9), "")%></div>
			<% } %>
			<% if(!uF.showData(appraisalList.get(8), "").equals("")) { %>
				<div style="display:inline; font-size: 14px; margin-left: 7px; font-weight: normal;"><%=uF.showData(appraisalList.get(8), "")%></div>
			<% } %> --%>
		</div>
	<br/>
		<table class="table table-striped">
			<tr>
				<th width="25%" align="right">Review Type:</th>
				<td><%=appraisalList.get(14)%></td>
			</tr>
			<tr>
				<th valign="top" align="right">Description:</th>
				<td colspan="1"><%=appraisalList.get(15)%></td>
			</tr>
			<%-- <tr>
				<th valign="top" align="right">Instruction</th>
				<td colspan="1"><%=appraisalList.get(16)%></td>
			</tr> --%>
			<tr>	
				<th align="right">Frequency:</th>
				<td><%=appraisalList.get(7)%></td>
			</tr>
			<tr>	
				<th align="right">Effective Date:</th>
				<td><%=appraisalList.get(17)%></td>
			</tr>
			<tr>	
				<th align="right">Due Date:</th>
				<td><%=appraisalList.get(18)%></td>
			</tr>
			<tr>
				<th align="right">Orientation:</th>
				<td colspan="1"><%=appraisalList.get(2)%></td>
			</tr>
			
			<% if(fromPage==null || !fromPage.equals("KRATARGET")) { %>
				<tr>
					<th valign="top" align="right">Reviewee:</th>
					<td colspan="1"><%=appraisalList.get(12)%></td>
				</tr>
				<tr>
					<th valign="top" align="right">Appraiser:</th>
					<td colspan="1"><%=appraisalList.get(23)%></td>
				</tr>
				<tr>
					<th valign="top" align="right">Reviewer:</th>
					<td colspan="1"><%=appraisalList.get(22)%></td>
				</tr>
			<% } %>
			
		  </table>
	   <br/>
		<div class="addgoaltoreview">
			<h4 class="boxHeading">Instruction:</h4>
			<div style="padding: 0px 20px; font-size: 14px; text-align: justify; line-height: 12px;">
				<%=appraisalList.get(16)%>				
			</div>
		</div>	
		<br/><br/>
	<%} %>
	<s:form action="StaffAppraisal" id="formID" method="POST" theme="simple">
	    <s:hidden name="appFreqId" id = "appFreqId" />
		<%
		Map<String, Map<String, String>> hmOrientPosition =(Map<String, Map<String, String>>)request.getAttribute("hmOrientPosition");
		int sectionCnt=0;
		//System.out.println("hmSection --->> "+hmSection);
		Set set = hmSection.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()) {
			sectionCnt++;
			String str = (String)it.next();
			//System.out.println("Section Id --->> "+str);
			Map<String, String> orientPosition = hmOrientPosition.get(str);
			int subsectionCnt=0;
			%>
			
			<div class="addgoaltoreview">
			<div style="width: 68%; float: left;">
		<!-- ===start parvez date: 27-02-2023=== -->	
				<table style="width: 90%;">
					<tr>
						<td>
							<h4 class="boxHeading"><%=sectionCnt %>)&nbsp;<%=uF.showData((String)hmLevelDetails.get(str+"_TITLE"), "")%>  </h4>
						</td>    
            			<td>
            				<div class="pull-right">Weightage: <%=uF.showData((String)hmLevelDetails.get(str+"_LWEIGHTAGE"), "")%></div>
            			</td>
            		</tr>
            	</table>
        <!-- ===end parvez date: 27-02-2023=== -->
				<div style="padding: 0px 20px; font-size: 14px; text-align: justify; line-height: 12px;">
					<%=uF.showData((String)hmLevelDetails.get(str+"_SDESC"), "")%>				
				</div>
				<div style="padding: 0px 20px; font-size: 12px; text-align: justify; line-height: 12px;">
					<%=uF.showData((String)hmLevelDetails.get(str+"_LDESC"), "")%> 				
				</div>
			</div>
			<div style="float: right; padding-right: 10px;">
				<%-- <b>WorkFlow:</b> &nbsp;&nbsp;
				<br/>
				<%
					String member1=(String)request.getAttribute("member");
					String[] memberArray1=member1.split(",");
					for(int i=0;i<memberArray1.length;i++){
				%>
				<%=memberArray1[i]%>:-
				<% if(hmMemberPosition != null){%>
					&nbsp;<%=hmMemberPosition.get(str+"_"+memberArray1[i]) %>,&nbsp;&nbsp;&nbsp;
				<% } } %> --%>
				<table >
					<tr>
						<th align="right" style="padding-right:5px">Work Flow</th>
						<td >
							<%
							//if(request.getAttribute("oreinted") != null && !request.getAttribute("oreinted").equals("5")) {
								String member1=(String)request.getAttribute("member");
								String[] memberArray1=member1.split(",");
								//System.out.println("member ==== > "+member);
								for(int i=0;i<memberArray1.length;i++) {
							%>
							 <span style="float: left; width: 60px; text-align: center;">Step<%=i+1%></span>
							<%-- <% if(memberArray1[i].equals("Manager")) { %>&nbsp;&nbsp;
							<% } else { %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<% } %> --%>
							<%-- <% } 
							} else { %>
							Anyone --%>
							<% } %>
						</td>
					</tr>
					
					<%
						//if(request.getAttribute("oreinted") != null && !request.getAttribute("oreinted").equals("5")){
							String member=(String)request.getAttribute("member");
							String[] memberArray=member.split(",");
							for(int i=0;i<memberArray.length;i++){
					%>			
					<tr>
						<th align="right" style="padding-right:5px"><%=memberArray[i]%></th>
						<td >
							<%for(int j=1;j<=memberArray.length;j++){ %>
								<span style="float: left; width: 60px; text-align: center;">
									<input type="radio" name="<%=str %><%=memberArray[i]%>" id="<%=str %><%=memberArray[i] %>" value="<%=j %>"
									<%if(orientPosition != null && uF.parseToInt(orientPosition.get(memberArray[i]))==j) { %>
									checked="checked"<% } else if(j==1) { %>checked="checked"<% } %> disabled="disabled"/>
								</span>
							<% } %>
						<%-- <%} %> --%>
						</td>
					</tr>
					<% } 
					//} %>
				</table>
				
				</div>
			</div>	
			
			<%
			List<List<String>> alQuestion11 = hmSection.get(str);
			for(int kk=0;kk<alQuestion11.size();kk++){
				subsectionCnt++;
				List<String> innerList=alQuestion11.get(kk);
				List<List<String>> alQuestion=hmLevelQuestio1n.get(innerList.get(0));
			%>
		<!-- <img src="images1/bottom-right-arrow.png">
		<h4 class="inline">Sections</h4> -->
		<div class="marginleft20">
				<h4 class="boxHeading"><%=sectionCnt %>.<%=subsectionCnt %>)&nbsp;<%=uF.showData(innerList.get(1), "")%>  </h4>
				<div style="line-height: 12px;">
					<%=uF.showData(innerList.get(2), "")%>				
				</div>
				<div style="line-height: 12px;">
					<%=uF.showData(innerList.get(3), "")%> 				
				</div>
			</div>	
			
			<%
					for (int i = 0; alQuestion != null && i < alQuestion.size(); i++) {
						List<String> innerlist = (List<String>) alQuestion.get(i);
						List<String> questioninnerList = hmQuestion.get(innerlist.get(1));
						//System.out.println("innerlist ===> "+ innerlist);
						//System.out.println("questioninnerList ===> "+ questioninnerList);
			%>
		<div style="margin: 10px 50px 10px 20px;">

			<ul>
				<li><b><%=sectionCnt %>.<%=subsectionCnt %>.<%=(i + 1)%>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b> 
				
			<!-- ===start parvez date: 17-03-2022=== -->
					<div class="pull-right">Weightage: <%=questioninnerList.get(10)%>
			<!-- ===end parvez date: 17-03-2022=== -->	
				
					<s:if test="innerlist.get(3)!=null">(<%=innerlist.get(12)%>)</s:if>
				</li>
			<!-- ===start parvez date: 27-02-2023=== -->	
				<%if(othrQueType.get(innerList.get(0)) == null || (othrQueType.get(innerList.get(0)) !=null && !othrQueType.get(innerList.get(0)).equals("Without Short Description"))){ %>
				<li>
					Description: <%=uF.showData(questioninnerList.get(11),"") %>
				</li>
				<%} %>
			<!-- ===end parvez date: 27-02-2023=== -->	
				<li>

					<ul style="margin: 10px 10px 10px 30px">
						<li>
							<% if (uF.parseToInt(questioninnerList.get(8)) == 1) { %>
							<div>
								a) <input type="checkbox" disabled="disabled" value="a" name="correct"/><%=questioninnerList.get(2)%><br/> 
								b) <input type="checkbox" disabled="disabled" name="correct" value="b"/><%=questioninnerList.get(3)%><br/>
								c) <input type="checkbox" disabled="disabled" value="c" name="correct"/><%=questioninnerList.get(4)%><br/> 
								d) <input type="checkbox" disabled="disabled" name="correct" value="d" /><%=questioninnerList.get(5)%><br/>
								<textarea rows="5" cols="50" readonly="readonly" style="width:100%" name="<%=innerlist.get(1)%>"></textarea>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
	 				} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
	 					answerTypeList.add("2");
 				%>
							<div>
								a) <input type="checkbox" disabled="disabled" value="a" name="correct"/><%=questioninnerList.get(2)%><br />
								b) <input type="checkbox" disabled="disabled" name="correct"value="b"/><%=questioninnerList.get(3)%><br />
								c) <input type="checkbox" disabled="disabled" value="c" name="correct"/><%=questioninnerList.get(4)%><br />
								d) <input type="checkbox" disabled="disabled" name="correct" value="d" /><%=questioninnerList.get(5)%>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
 					} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
 						answerTypeList.add("3");
 				%>
							<div>
								<input type="hidden" name="marks<%=innerlist.get(1)%>" id="marks<%=i%>" style="width: 31px;"/>
								<script>
									$(function() {
										$("#sliderscore"+<%=questioninnerList.get(8)+i%>).slider({
											value : 50,
											min : 0,
											max : <%=innerlist.get(2)%>,
											step : 2,
											disabled:true,
											slide : function(event, ui) {
												$("#marks"+<%=i%>+"").val(ui.value);
												$("#slidemarksscore"+<%=i%>+"").html(ui.value);
											}
										});
										$("#marks"+<%=i%>+"").val($("#sliderscore"+<%=questioninnerList.get(8)+i%>).slider("value"));
										$("#slidemarksscore"+<%=i%>+"").html($("#sliderscore"+<%=questioninnerList.get(8)+i%>).slider("value"));
									});
								</script>
								<br/>
								<div id="slidemarksscore<%=i%>" style="width:25%; text-align:center;"></div>
								<div id="sliderscore<%=questioninnerList.get(8)+i%>" style="width:25%; float: left;"></div>
								<div id="marksscore<%=questioninnerList.get(8)+i%>" style="width:25%;margin-top: 20px;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
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
								<input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>" value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
								<% } %>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
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
								<input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>" value="<%=inner.get(0)%>" /><%=inner.get(1)%><br/>
								<% } %>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
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
								<input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>" value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
								<% } %>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
 					} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
 						answerTypeList.add("7");
 				%>
							<div>
								<strong>Ans:</strong>
								<input type="hidden" name="outofmarks<%=innerlist.get(1)%>" id="outofmarks<%=i%>" value="<%=innerlist.get(2)%>" />
								<input type="hidden" name="marks<%=innerlist.get(1)%>" id="marks<%=i%>" style="width: 31px;"/>
								<script>
									$(function() {
										$("#slidersingleopen"+<%=questioninnerList.get(8)+i%>).slider({
											value : 0,
											min : 0,
											max : <%=innerlist.get(2)%>,
											step : 2,
											disabled:true,
											slide : function(event, ui) {
												$("#marks"+<%=i%>+"").val(ui.value);
												$("#slidemarkssingleopen"+<%=i%>+"").html(ui.value);
											}
										});
										$("#marks"+<%=i%>+"").val($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>).slider("value"));
										$("#slidemarkssingleopen"+<%=i%>+"").html($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>).slider("value"));
									});
								</script>
						
								<br/>
								<div id="slidemarkssingleopen<%=i%>" style="width:25%; text-align:center;"></div>
								<div id="slidersingleopen<%=questioninnerList.get(8)+i%>" style="width:25%; float: left;"></div>						
								<div id="markssingleopen<%=questioninnerList.get(8)+i%>" style="width:25%;margin-top: 20px;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
 					} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
 						answerTypeList.add("8");
 				%>
							<div>
								a) <input type="radio" value="a" disabled="disabled" name="correct<%=innerlist.get(1)%>"/><%=questioninnerList.get(2)%><br />
								b) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="b"/><%=questioninnerList.get(3)%><br />
								c) <input type="radio" disabled="disabled" value="c" name="correct<%=innerlist.get(1)%>"/><%=questioninnerList.get(4)%><br />
								d) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="d" /><%=questioninnerList.get(5)%><br />
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
 					} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
 						answerTypeList.add("9");
 				%>
							<div>
								a) <input type="checkbox" disabled="disabled" value="a" name="correct<%=innerlist.get(1)%>"/><%=questioninnerList.get(2)%><br />
								b) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="b"/><%=questioninnerList.get(3)%><br />
								c) <input type="checkbox" disabled="disabled" value="c" name="correct<%=innerlist.get(1)%>"/><%=questioninnerList.get(4)%><br />
								d) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="d" /><%=questioninnerList.get(5)%><br />
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
 					} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
 					answerTypeList.add("10");
 				%> 
							<div>
								<div style="float: left; margin: 30px 10px 0px 0px;">a)</div>
								<div>
									<textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="a<%=innerlist.get(1)%>"></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">b)</div>
								<div>
									<textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="b<%=innerlist.get(1)%>"></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">c)</div>
								<div>
									<textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="c<%=innerlist.get(1)%>"></textarea>
									<br />
								</div>
								<div style="float: left; margin: 30px 10px 0px 0px;">d)</div>
								<div>
									<textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="d<%=innerlist.get(1)%>"></textarea>
									<br />
								</div>
								
									<input type="hidden" name="outofmarks<%=innerlist.get(1)%>" id="outofmarks<%=i%>" value="<%=innerlist.get(2)%>" />
											
									<input type="hidden" name="marks<%=innerlist.get(1)%>" id="marks<%=i%>" style="width: 31px;"/>
											
									<script>
										$(function() {
											$("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>).slider({
												value : 0,
												min : 0,
												max : <%=innerlist.get(2)%>,
												step : 2,
												disabled:true,
												slide : function(event, ui) {
													$("#marks"+<%=i%>+"").val(ui.value);
													$("#slidemarksmultipleopen"+<%=i%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=i%>+"").val($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>).slider("value"));
											$("#slidemarksmultipleopen"+<%=i%>+"").html($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>).slider("value"));
										});
									</script>
									<br/>
									<div id="slidemarksmultipleopen<%=i%>" style="width:25%; text-align:center;"></div>
									<div id="slidermultipleopen<%=questioninnerList.get(8)+i%>" style="width:25%; float: left;"></div>
									<div id="marksmultipleopen<%=questioninnerList.get(8)+i%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
							</div> 
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
 					} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
 						answerTypeList.add("11");
 				%>
							<div id="starPrimary<%=innerlist.get(1)%>"></div> <input type="hidden" id="gradewithrating<%=innerlist.get(1)%>" value="3" name="gradewithrating<%=innerlist.get(1)%>"/> 
							<script type="text/javascript">
						        $(function() {
						        	$('#starPrimary<%=innerlist.get(1)%>').raty({
						        		readOnly: true,
						        		start: 3,
						        		half: true,
						        		targetType: 'number',
						        		click: function(score, evt) {
						        			$('#gradewithrating<%=innerlist.get(1)%>').val(score);
						        			}
						        	});
						        	});
						        </script>
						        <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
 					} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
						answerTypeList.add("12");
		 		%>
						<div>
							<strong>Ans:</strong>
							<textarea rows="5" cols="50" readonly="readonly" style="width:100%" name="anscomment<%=innerlist.get(1)%>"></textarea>
						</div> 
						<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				<%
 					} else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
 						answerTypeList.add("13");
 				%>
							<div>
								a) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="a"/><%=questioninnerList.get(2)%><br />
								b) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="b"/><%=questioninnerList.get(3)%><br />
								c) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="c"/><%=questioninnerList.get(4)%><br />
								d) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="d"/><%=questioninnerList.get(5)%><br />
								e) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>" value="e"/><%=questioninnerList.get(9)%><br />
							</div>
							<div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>"></textarea></div>
				
				
				<% } %>
						</li>
					</ul>
				</li>
			</ul>
		</div>
		<%
			} }
			}	
		%>


	</s:form>
</div>
