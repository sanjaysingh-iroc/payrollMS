<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List alSkills = (List) request.getAttribute("alSkills");
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	String empID = (String)session.getAttribute("empID");
	String dataType = (String)session.getAttribute("dataType");
	String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
	if (hmEmpProfile == null) {
		hmEmpProfile = new HashMap<String, String>();
	}
	
	List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
	Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) request.getAttribute("hmElementAttribute");
	Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
	
%>

<g:compress>
<script type="text/javascript">
	function getStepTabContent(id,EmpId,userId,userType,currentLevel,role,appFreqId,levelCount){
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $.ajax({
	    	url: "StaffAppraisalCorrection.action?id="+id+"&empID="+EmpId+"&userId="+userId+"&userType="+userType+"&currentLevel="+currentLevel+"&role="+role+"&appFreqId="+appFreqId+"&levelCount="+levelCount, 
	    	type: "GET",
	    	success: function(result){
	        	$("#divResult").html(result);
	    	}
	    });
	}
	
	
	$(function(){
		$("input[type='submit']").click(function(e){
			e.preventDefault();
			var cnt = 1;
			var submitActor = null;
			var submitButtons = $('form').find('input[type=submit]');
			submitButtons.click(function(event) {
			    submitActor = this;
			});
			if (null === submitActor) {
	           submitActor = submitButtons[0];
			}
			var form_data = $("#StaffAppraisalCorrectionFormID").serialize();
			//var form_data="";
			var submit = submitActor.name;
			var subBtn = "";
			if(submit != null && submit == "btnfinish") {
				subBtn = "?btnfinish=Finish";
	       	}
			//form_data = form_data + $("#StaffAppraisalCorrectionFormID").serialize();
			if(cnt==1) {
				if(confirm('Are you sure, you want to finish this?')) {
					var id = document.getElementById("id").value;
					var empID = document.getElementById("empID").value;
					var userType = document.getElementById("userType").value;
					var appFreqId = document.getElementById("appFreqId").value;
					$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					$.ajax({
				    	url: "StaffAppraisalCorrection.action"+subBtn, 
				    	type: "POST",
				    	data: form_data,
				    	success: function(result) {
				        	$("#divResult").html(result);
				    	},
				    	error: function(result) {
					       	if(submit != null && submit == "btnfinish") {
					       		$.ajax({
									url: 'EmpAppraisalSummaryReviewer.action?id='+id+'&empID='+empID+'&userType='+userType+'&appFreqId='+appFreqId,
									cache: true,
									success: function(result) {
										$("#divResult").html(result);
							   		}
								});
					       	}
				    	}
				    });
				}
			}
		});
	});
		
</script>
</g:compress>

<%
	List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
	Map<String, String> hmEmpDetails = (Map) request.getAttribute("hmEmpDetails");
	Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
	/* List<List<String>> questionList = (List<List<String>>) request.getAttribute("questionList"); */
	Map<String,List<List<String>>> hmLevelQuestion=(Map<String,List<List<String>>>)request.getAttribute("hmLevelQuestion");
	Map<String,Map<String,String>> hmSubsection=(Map<String,Map<String,String>>)request.getAttribute("hmSubsection");
	List<String> mainLevelList = (List<String>) request.getAttribute("mainLevelList");
	//List<String> levelList = (List<String>) request.getAttribute("mainLevelList");
	/* String tab=(String) request.getAttribute("tab"); */
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
	Map<String, String> hmLevelName = (Map<String, String>) request.getAttribute("hmLevelName"); 
	Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
	Map<String, Map<String, String>> questionanswerMpReviewer = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMpReviewer");
	if(questionanswerMpReviewer == null) questionanswerMpReviewer = new HashMap<String, Map<String, String>>();
	
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
	String currentLevel = (String) request.getAttribute("currentLevel");
	Map<String, String> levelStatus = (Map<String, String>) request.getAttribute("LEVEL_STATUS");
	if (hmLevelName == null)hmLevelName = new HashMap<String, String>();
	List<String> answerTypeList = new ArrayList<String>();

	Map<String, String> hmEmpList = (Map<String, String>) request.getAttribute("hmEmpList");

	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	
	
	String strUserTypeId = (String) session.getAttribute(IConstants.USERTYPEID);
	String id = request.getParameter("id");
	String strSessionEmpId = (String)session.getAttribute(IConstants.EMPID);
	
	String userType = request.getParameter("userType");
	boolean levelFlag = (Boolean)request.getAttribute("levelFlag");
	
	List<String> listRemainOrientType = (List<String>)request.getAttribute("listRemainOrientType");

%>


	<div class="leftbox reportWidth" id="divResult">
		<div class="addgoaltoreview">
			<h4><%=appraisalList.get(1) %></h4>

			<div style="line-height: 12px;"><%=appraisalList.get(2) %></div>
			<div class="addgoaltoreview-arrow"></div>
			<div style="float: right; margin: 0px 20px 0px 0px; font-size: 14px;">
			<table><tr><td><u>role as</u> </td> <td class="textblue">&nbsp;&nbsp;<b><%=hmEmpDetails.get("ORIENTATION")%></b></td></tr></table>
			 </div>
		</div>
										
	      <div id="queAsnDiv">
			<%
		 	int size = 100 / mainLevelList.size();
		 	String sectionCount = (String)request.getAttribute("sectionCount");
		 	double completePercent =(uF.parseToDouble(sectionCount)/uF.parseToDouble(""+mainLevelList.size()))*100;
		 	long intcompletePercent = Math.round(completePercent);
		 %>
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
			    <% } else if(intcompletePercent >= 66.67) { %>
			    <span class="badge bg-green marginbottom5"><%=intcompletePercent %>%</span>
			    <div class="progress progress-xs">
			        <div class="progress-bar progress-bar-green" style="width: <%=intcompletePercent %>%;"></div>
			    </div>
			    <% } %>
		
		<div class="reviewbar">
	        <div class="step-tab instruction-step-tab">
                <a href="javascript:void(0)" onclick="getStepTabContent(<s:property value="id"/>,<s:property value="empID"/>,<s:property value="userId"/>,<s:property value="userType"/>,<%=currentLevel%>,<s:property value="role"/>,<s:property value="appFreqId"/>,null)">Instruction</a>
            </div>
						<%
							size = mainLevelList.size();
							for (int i = 0; i < mainLevelList.size(); i++) {
								if (request.getAttribute("levelCount").toString().equals("1")) {
									size = 0;
								}
								if (currentLevel.equals(mainLevelList.get(i)) && !request.getAttribute("levelCount").toString().equals("1")) {
								size = i + 1;
						%>
						<div class="step-tab"><img src="images1/icons/bullet-green.png"></div>
						<% } else {
							if (levelStatus.get(mainLevelList.get(i)) != null) {
						%>
							<div class="step-tab">
							     <a href="javascript:void(0)" onclick="getStepTabContent(<s:property value="id"/>,<s:property value="empID"/>,<s:property value="userId"/>,<s:property value="userType"/>,<%=mainLevelList.get(i)%>,'<s:property value="role"/>',<s:property value="appFreqId"/>,<%=uF.parseToInt(request.getAttribute("levelCount").toString())+1 %>)">
									<img src="images1/icons/bullet-white-1.png">
								</a>
							  </div>
						<%} else { %>
							<div class="step-tab">
							     <a href="javascript:void(0)" onclick="getStepTabContent(<s:property value="id"/>,<s:property value="empID"/>,<s:property value="userId"/>,<s:property value="userType"/>,<%=mainLevelList.get(i)%>,'<s:property value="role"/>',<s:property value="appFreqId"/>,<%=uF.parseToInt(request.getAttribute("levelCount").toString())+1 %>)">
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
				<% if(!request.getAttribute("levelCount").toString().equals("1")) { %>
					<div class="addgoaltoreview">
						<h4><%=size%>)&nbsp;<%=uF.showData(hmLevelName.get("LEVEL_NAME"), "")%>
							<input type="hidden" name="hideSectionId" id="hideSectionId" value="<%=uF.showData(hmLevelName.get("APP_LEVEL_ID"), "")%>" />
						</h4>
						<div style="line-height: 12px;"><%=uF.showData(hmLevelName.get("LEVEL_SDESC"), "")%></div>
						<div style="line-height: 12px;"><%=uF.showData(hmLevelName.get("LEVEL_LDESC"), "")%></div>
						<div class="addgoaltoreview-arrow"></div>
					</div>
			  <%} %>
			<s:form action="StaffAppraisalCorrection" id="StaffAppraisalCorrectionFormID" method="POST" theme="simple">
				<s:hidden name="empID" id="empID" />
				<s:hidden name="id" id="id" />
				<s:hidden name="appFreqId" id="appFreqId" />
				<s:hidden name="levelId" />
				<s:hidden name="currentLevel" />
				<s:hidden name="userType" id="userType" />
				<s:hidden name="role" />
				<s:hidden name="levelCount" />
				<s:hidden name="userId" />
				<% if(request.getAttribute("levelCount").toString().equals("1")) {
					if(appraisalList.get(5)!= null && !appraisalList.get(5).equals("")) {
				%>
					<%=appraisalList.get(5) %>
				<% } else { %>
						<div> No instructions provided. </div>
				<% }
				  } else {
					StringBuilder sbQueAnsIds = new StringBuilder();
					int subsectioncnt=0;
					Set keys=hmLevelQuestion.keySet();
					Iterator it=keys.iterator();
					while(it.hasNext()) {
						subsectioncnt++;
						String key=(String)it.next();
						List<List<String>> questionList =hmLevelQuestion.get(key);
						
						Map<String,String> hmSubsectionDetails =hmSubsection.get(key);
				%>
						<div class="addgoaltoreview">
							<h4><%=size%>.<%=subsectioncnt %>)&nbsp;<%=uF.showData(hmSubsectionDetails.get("LEVEL_NAME"), "")%>
							<input type="hidden" name="hideSubsectionId" id="hideSubsectionId" value="<%=uF.showData(hmSubsectionDetails.get("APP_LEVEL_ID"), "")%>" />
							</h4>
							<div style="line-height: 12px;"><%=uF.showData(hmSubsectionDetails.get("LEVEL_SDESC"), "")%></div>
							<div style="line-height: 12px;"><%=uF.showData(hmSubsectionDetails.get("LEVEL_LDESC"), "")%></div>
							<div class="addgoaltoreview-arrow"></div>
						</div>	
						<input type="hidden" name="levelAppSystem" value="<%=hmSubsectionDetails.get("LEVEL_APPSYSTEM")%>" />
					<%		
						for (int i = 0; questionList != null && i < questionList.size(); i++) {
							List<String> innerlist = (List<String>) questionList.get(i);
							List<String> questioninnerList = hmQuestion.get(innerlist.get(1));

							Map<String, String> innerMp = null;
							Map<String, String> innerMpReivewer = null;
							if (innerlist.get(14) != null) {
								innerMp = questionanswerMp.get(innerlist.get(14) + "question" + innerlist.get(1));
								innerMpReivewer = questionanswerMpReviewer.get(innerlist.get(14) + "question" + innerlist.get(1));
							} else {
								innerMp = questionanswerMp.get(innerlist.get(15) + "question" + innerlist.get(1));
								innerMpReivewer = questionanswerMpReviewer.get(innerlist.get(15) + "question" + innerlist.get(1));
							}
							if (innerMp == null)
								innerMp = new HashMap<String, String>();
							
							if (innerMpReivewer == null)
								innerMpReivewer = new HashMap<String, String>();
							sbQueAnsIds.append(innerMp.get("APP_QUE_ANS_ID")+"::::");
					%>
				<div style="float: left; width: 100%;">
					<ul style="float: left; width: 100%; border-bottom: 1px solid #efefef;">
						<li style="float: left; width: 100%; background-color: #e3e3e3">
							<b><%=size%>.<%=subsectioncnt %>.<%=(i + 1)%>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b> 
							<s:if test="innerlist.get(3)!=null">(<%=innerlist.get(12)%>)</s:if>
							
						</li>
						
						<li style="float: left; width: 100%;">
							<ul style="float: left; width: 100%; margin: 10px 10px 10px 30px">
								<li style="float: left; width: 50%;">
									<% if (uF.parseToInt(questioninnerList.get(8)) == 1) {
										if(!answerTypeList.contains("1")) {		
											answerTypeList.add("1");
										}
									%>
									<div>
										a) <input type="checkbox" disabled="disabled" value="a" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>checked <%}%> /><%=questioninnerList.get(2)%><br /> 
										b) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="checkbox" disabled="disabled" value="c" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>checked <%}%> /><%=questioninnerList.get(4)%><br /> 
										d) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
											<textarea rows="5" cols="50" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
									</div> 
									<div id="ansType1cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
									 		if(!answerTypeList.contains("2")){		
												answerTypeList.add("2");
											}
									 %>
									<div>
										a) <input type="checkbox" disabled="disabled" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br/>
										b) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="checkbox" disabled="disabled" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />
										d) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%>
									</div>
									<div id="ansType2cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
									 		if(!answerTypeList.contains("3")){		
												answerTypeList.add("3");
											}
									 %>
									<div>
										<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
										<script type="text/javascript">
											$(function() {
												$("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
													value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 1,
													disabled:true,
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
										<div id="marksscore<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div>
									<div id="ansType3cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
									 		if(!answerTypeList.contains("4")){		
												answerTypeList.add("4");
											}
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<% for (int j = 0; j < outer.size(); j++) {
											List<String> inner = outer.get(j);
										%>
										<input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").equals(inner.get(0))) {%> checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div> 
									<div id="ansType4cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
									 		if(!answerTypeList.contains("5")) {
												answerTypeList.add("5");
											}
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<% for (int j = 0; j < outer.size(); j++) {
											List<String> inner = outer.get(j);
										%>
										<input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%> checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div> 
									<div id="ansType5cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
									 		if(!answerTypeList.contains("6")) {
												answerTypeList.add("6");
											}
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<% for (int j = 0; j < outer.size(); j++) {
											List<String> inner = outer.get(j);
										%>
										<input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%> checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div>
									<div id="ansType6cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
									 		if(!answerTypeList.contains("7")) {
												answerTypeList.add("7");
											}
									 %>
									<div>
										<textarea rows="5" cols="50" readonly="readonly" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea><br />
										<input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="outofmarks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" value="<%=innerlist.get(2)%>" />
										<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
										<script type="text/javascript">
											$(function() {
												$("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
													value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 1,
													disabled : true,
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
										<div id="markssingleopen<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div> 
									<div id="ansType7cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
									 		if(!answerTypeList.contains("8")){		
												answerTypeList.add("8");
											}
									 %>
									<div>
										a) <input type="radio" disabled="disabled" value="a"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />
										b) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="radio" disabled="disabled" value="c"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />
										d) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div>
									<div id="ansType8cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
									 		if(!answerTypeList.contains("9")) {
												answerTypeList.add("9");
											}
									 %>
									<div>
										a) <input type="checkbox" disabled="disabled" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) { %> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />
										b) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) { %> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="checkbox" disabled="disabled" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) { %> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />
										d) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) { %> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div>
									<div id="ansType9cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
									 		if(!answerTypeList.contains("10")){		
												answerTypeList.add("10");
											}
										 	String[] a = null;
											if (innerMp.get("ANSWER") != null) {
												a = innerMp.get("ANSWER").split(":_:");
									 		} 
									 %>
									<div>
										<div style="float: left; margin: 30px 10px 0px 0px;">a)</div>
										<div><textarea rows="5" cols="50" readonly="readonly" name="a<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=a != null ? a[0] : ""%></textarea><br /></div>
										<div style="float: left; margin: 30px 10px 0px 0px;">b)</div>
										<div><textarea rows="5" cols="50" readonly="readonly" name="b<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=a != null ? a[1] : ""%></textarea><br /></div>
										<div style="float: left; margin: 30px 10px 0px 0px;">c)</div>
										<div><textarea rows="5" cols="50" readonly="readonly" name="c<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=a != null ? a[2] : ""%></textarea><br /></div>
										<div style="float: left; margin: 30px 10px 0px 0px;">d)</div>
										<div><textarea rows="5" cols="50" readonly="readonly" name="d<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=a != null ? a[3] : ""%></textarea><br /></div>
										<input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="outofmarks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" value="<%=innerlist.get(2)%>" />
										<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
											
										<script type="text/javascript">
											$(function() {
												$("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
													value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 1,
													disabled : true,
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
										<div id="marksmultipleopen<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div> 
									<div id="ansType10cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<%
								 	} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
								 		if(!answerTypeList.contains("11")){		
											answerTypeList.add("11");
										}
								 		double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
										double starweight = weightage*20/100;
								 		//System.out.println("innerlist.get(1)_questioninnerList.get(9) ::::: "+innerlist.get(1)+"_"+questioninnerList.get(9));
								 	%>
									<div id="starPrimary<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"></div> 
									<input type="hidden" id="gradewithrating<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" value="<%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>" name="gradewithrating<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /> 
										<script type="text/javascript">
									        $(function() {
									        	$('#starPrimary<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>').raty({
									        		readOnly: true,
									        		start: <%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>,
									        		half: true,
									        		targetType: 'number',
									        		click: function(score, evt) {
									        			$('#gradewithrating<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>').val(score);
													}
												});
											});
										</script>
									<div id="ansType11cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 12) { 
								 		if(!answerTypeList.contains("12")) {		
											answerTypeList.add("12");
										}
									%>
									<div><textarea rows="5" cols="50" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea></div> 
									<div id="ansType12cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" readonly="readonly" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } %>
								</li>
								
								
								
								
								
								
								<li style="float: left; width: 50%;">
									<% if (uF.parseToInt(questioninnerList.get(8)) == 1) {
										if(!answerTypeList.contains("1")){		
											answerTypeList.add("1");
										}
									%>
									<div>
										a) <input type="checkbox" value="a" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"<%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("a")) {%>checked <%}%> /><%=questioninnerList.get(2)%><br /> 
										b) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"<%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("b")) {%>checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="checkbox" value="c" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"<%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("c")) {%>checked <%}%> /><%=questioninnerList.get(4)%><br /> 
										d) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>"<%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("d")) {%>checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
										<textarea rows="5" cols="50" name="<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:100%"><%=innerMpReivewer.get("REMARK") != null ? innerMpReivewer.get("REMARK") : ""%></textarea>
									</div> 
									<div id="ansType1cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
									 		if(!answerTypeList.contains("2")){		
												answerTypeList.add("2");
											}
									 %>
									<div>
										a) <input type="checkbox" value="a" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br/>
										b) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="checkbox" value="c" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
										d) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%>
									</div>
									<div id="ansType2cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
									 		if(!answerTypeList.contains("3")){		
												answerTypeList.add("3");
											}
									 %>
									<div>
										<input type="hidden" name="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width: 31px;"/>
										<script type="text/javascript">
											$(function() {
												$("#sliderscore"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider({
													value : <%=innerMpReivewer.get("MARKS") != null ? innerMpReivewer.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 1,
													slide : function(event, ui) {
														$("#marks"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val(ui.value);
														$("#slidemarksscore"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html(ui.value);
													}
												});
												$("#marks"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val($("#sliderscore"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider("value"));
												$("#slidemarksscore"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html($("#sliderscore"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider("value"));
											});
										</script>
										<br/>
										<div id="slidemarksscore<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; text-align:center;"></div>
										<div id="sliderscore<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
										<div id="marksscore<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div>
									<div id="ansType3cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
									 		if(!answerTypeList.contains("4")) {		
												answerTypeList.add("4");
											}
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<% for(int j=0; j < outer.size(); j++) {
											List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerMp.get("APP_QUE_ANS_ID")%>" <% if(innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").equals(inner.get(0))) { %> checked <% } %> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div> 
									<div id="ansType4cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
									 		if(!answerTypeList.contains("5")) {
												answerTypeList.add("5");
											}
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<% for (int j = 0; j < outer.size(); j++) {
											List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains(inner.get(0))) {%> checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div> 
									<div id="ansType5cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
									 		if(!answerTypeList.contains("6")) {
												answerTypeList.add("6");
											}
									 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
									 %>
									<div>
										<% for (int j = 0; j < outer.size(); j++) {
											List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains(inner.get(0))) {%> checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div>
									<div id="ansType6cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
									 		if(!answerTypeList.contains("7")) {
												answerTypeList.add("7");
											}
									 %>
									<div>
										<textarea rows="5" cols="50" name="<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:100%"><%=innerMpReivewer.get("ANSWER") != null ? innerMpReivewer.get("ANSWER") : ""%></textarea><br />
										<input type="hidden" name="outofmarks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="outofmarks<%=innerMp.get("APP_QUE_ANS_ID")%>" value="<%=innerlist.get(2)%>" />
										<input type="hidden" name="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width: 31px;"/>
										<script type="text/javascript">
											$(function() {
												$("#slidersingleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider({
													value : <%=innerMpReivewer.get("MARKS") != null ? innerMpReivewer.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 1,
													slide : function(event, ui) {
														$("#marks"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val(ui.value);
														$("#slidemarkssingleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html(ui.value);
													}
												});
												$("#marks"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val($("#slidersingleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider("value"));
												$("#slidemarkssingleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html($("#slidersingleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider("value"));
											});
										</script>
										<br/>
										<div id="slidemarkssingleopen<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; text-align:center;"></div>
										<div id="slidersingleopen<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>						
										<div id="markssingleopen<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div> 
									<div id="ansType7cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
									 		if(!answerTypeList.contains("8")) {		
												answerTypeList.add("8");
											}
									 %>
									<div>
										a) <input type="radio" value="a"<%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
										b) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="radio" value="c"<%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
										d) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div>
									<div id="ansType8cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
									 		if(!answerTypeList.contains("9")) {
												answerTypeList.add("9");
											}
									 %>
									<div>
										a) <input type="checkbox" value="a" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("a")) { %> checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
										b) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("b")) { %> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="checkbox" value="c" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("c")) { %> checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
										d) <input type="checkbox" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("d")) { %> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div>
									<div id="ansType9cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									 <% } else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
									 		if(!answerTypeList.contains("10")) {		
												answerTypeList.add("10");
											}
										 	String[] a = null;
											if (innerMpReivewer.get("ANSWER") != null) {
												a = innerMpReivewer.get("ANSWER").split(":_:");
									 		} 
									 %>
									<div>
										<div style="float: left; margin: 30px 10px 0px 0px;">a)</div>
										<div><textarea rows="5" cols="50" name="a<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:100%"><%=a != null ? a[0] : ""%></textarea><br /></div>
										<div style="float: left; margin: 30px 10px 0px 0px;">b)</div>
										<div><textarea rows="5" cols="50" name="b<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:100%"><%=a != null ? a[1] : ""%></textarea><br /></div>
										<div style="float: left; margin: 30px 10px 0px 0px;">c)</div>
										<div><textarea rows="5" cols="50" name="c<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:100%"><%=a != null ? a[2] : ""%></textarea><br /></div>
										<div style="float: left; margin: 30px 10px 0px 0px;">d)</div>
										<div><textarea rows="5" cols="50" name="d<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:100%"><%=a != null ? a[3] : ""%></textarea><br /></div>
										<input type="hidden" name="outofmarks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="outofmarks<%=innerMp.get("APP_QUE_ANS_ID")%>" value="<%=innerlist.get(2)%>" />
										<input type="hidden" name="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width: 31px;"/>
											
										<script type="text/javascript">
											$(function() {
												$("#slidermultipleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider({
													value : <%=innerMpReivewer.get("MARKS") != null ? innerMpReivewer.get("MARKS") : "0"%>,
													min : 0,
													max : <%=innerlist.get(2)%>,
													step : 1,
													slide : function(event, ui) {
														$("#marks"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val(ui.value);
														$("#slidemarksmultipleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html(ui.value);
													}
												});
												$("#marks"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val($("#slidermultipleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider("value"));
												$("#slidemarksmultipleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html($("#slidermultipleopen"+<%=innerMp.get("APP_QUE_ANS_ID")%>).slider("value"));
											});
										</script>
										<br/>
										<div id="slidemarksmultipleopen<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; text-align:center;"></div>
										<div id="slidermultipleopen<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
										<div id="marksmultipleopen<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
									</div> 
									<div id="ansType10cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<%
								 	} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
								 		if(!answerTypeList.contains("11")){		
											answerTypeList.add("11");
										}
								 		double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
										double starweight = weightage*20/100;
								 		//System.out.println("innerlist.get(1)_questioninnerList.get(9) ::::: "+innerlist.get(1)+"_"+questioninnerList.get(9));
								 	%>
									<div id="starPrimary<%=innerMp.get("APP_QUE_ANS_ID")%>"></div> 
									<input type="hidden" id="gradewithrating<%=innerMp.get("APP_QUE_ANS_ID")%>" value="<%=innerMpReivewer.get("MARKS") != null ? uF.parseToDouble(innerMpReivewer.get("MARKS")) / starweight + "" : "0"%>" name="gradewithrating<%=innerMp.get("APP_QUE_ANS_ID")%>" /> 
										<script type="text/javascript">
									        $(function() {
									        	$('#starPrimary<%=innerMp.get("APP_QUE_ANS_ID")%>').raty({
									        		readOnly: false,
									        		start: <%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMpReivewer.get("MARKS")) / starweight + "" : "0"%>,
									        		half: true,
									        		targetType: 'number',
									        		click: function(score, evt) {
									        			$('#gradewithrating<%=innerMp.get("APP_QUE_ANS_ID")%>').val(score);
													}
												});
											});
										</script>
									<div id="ansType11cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 12) { 
								 		if(!answerTypeList.contains("12")) {		
											answerTypeList.add("12");
										}
									%>
									<div><textarea rows="5" cols="50" name="<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:100%"><%=innerMpReivewer.get("ANSWER") != null ? innerMpReivewer.get("ANSWER") : ""%></textarea></div> 
									<div id="ansType12cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
										<br/><b>Comment:</b><br/>
										<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"></textarea>
									</div>
									<% } else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
									 		if(!answerTypeList.contains("13")) {		
												answerTypeList.add("13");
											}
									 %>
									<div>
										a) <input type="radio" value="a"<%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
										b) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
										c) <input type="radio" value="c"<%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
										d) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
										e) <input type="radio" name="correct<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMpReivewer.get("ANSWER") != null && innerMpReivewer.get("ANSWER").contains("e")) {%> checked <%}%> value="e" /><%=questioninnerList.get(10)%><br />
									</div>
									<div id="ansType13cmnt<%=innerMp.get("APP_QUE_ANS_ID")%>">
									<br/><b>Comment:</b><br/>
									<textarea rows="3" cols="50" name="anscomment<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:70%"><%=innerMpReivewer.get("ANSWERCOMMENT") != null ? innerMpReivewer.get("ANSWERCOMMENT") : ""%></textarea>
									</div>
									<% } %>
								</li>
								
								
		   					</ul>
						</li>
					</ul>
				</div>
				<hr>
				<% }
				}
				%>
				
				<input type="hidden" name="appQueAnsIds" id="appQueAnsIds" value="<%=sbQueAnsIds.toString() %>" />
				
				<% if(answerTypeList.contains("4") || answerTypeList.contains("5") || answerTypeList.contains("6")) { %>
					<div class="addgoaltoreview">
					<fieldset style="margin: 0px 15px 0px 10px;">
						<legend>Answer Type Structure</legend>
						<table class="table_font" style="margin: 10px 10px 10px 30px;">
							<tr>
							<% 	int k = 1;
								for (int i = 0; i < answerTypeList.size(); i++) {
									List<List<String>> outerList = hmQuestionanswerType.get(answerTypeList.get(i));
							%>
								<td valign="top">
									<table class="table_font">
										<% for (int j = 0; outerList != null && j < outerList.size(); j++) {
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
				
				<%} %>
				<div class="clr margintop20">
					<% if (mainLevelList.size() == size) { %>
						<s:submit value="Finish" cssClass="btn btn-primary" name="btnfinish" /> 
					<% } else {
						if(request.getAttribute("levelCount").toString().equals("1")) {
					%>
						<s:submit value="Take Assessment" cssClass="btn btn-primary" name="submit" />
					<% } else { %>
						<s:submit value="Next" cssClass="btn btn-primary" name="submit" />
					<% }
						}
					%>
				</div>
			</s:form>
		</div>
		</div>

	<div></div>
</div>
