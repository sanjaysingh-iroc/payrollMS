<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<script>
function getStepTabContent(EmpId,resignId,formId,currentLevel,levelCount){
	$("#queAsnDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $.ajax({
    	url: "ResigClearenceForm.action?strEmpId="+EmpId+"&resignId="+resignId+"&formId="+formId+"&currentLevel="+currentLevel+"&levelCount="+levelCount, 
    	type: "GET",
    	success: function(result){
        	$("#queAsnDiv").html(result);
    	}
    });
}

$(function(){
	$("input[type='submit']").click(function(e){
		e.preventDefault();
		var form_data = $("#ResigClearenceFormID").serialize();
		$("#queAsnDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
	    	url: "ResigClearenceForm.action", 
	    	type: "POST",
	    	data: form_data,
	    	success: function(result){
	        	$("#queAsnDiv").html(result);
	    	}
	    });
	});
});
</script>
<%
    UtilityFunctions uF = new UtilityFunctions();
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
    if (hmEmpProfile == null) hmEmpProfile = new HashMap<String, String>();
    Map<String, String> empMap = (Map<String, String>) request.getAttribute("empDetailsMp");
    if (empMap == null) empMap = new HashMap<String, String>();
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    String probationRemaining = (String) request.getAttribute("PROBATION_REMAINING");
    String noticePeriod = (String) request.getAttribute("NOTICE_PERIOD");
    
    Map<String, String> hmForm = (Map<String, String>)request.getAttribute("hmForm");
    if(hmForm == null) hmForm = new HashMap<String, String>();
    /* List<Map<String, String>> alSection = (List<Map<String, String>>)request.getAttribute("alSection");
    if(alSection == null) alSection = new ArrayList<Map<String,String>>(); */
    List<String> alSectionId = (List<String>)request.getAttribute("alSectionId");
    if(alSectionId == null) alSectionId = new ArrayList<String>();
    Map<String, List<Map<String, String>>> hmSectionQuestion = (Map<String, List<Map<String, String>>>)request.getAttribute("hmSectionQuestion");
    if(hmSectionQuestion == null) hmSectionQuestion = new HashMap<String, List<Map<String,String>>>();
    
    String currentLevel = (String) request.getAttribute("currentLevel");
    Map<String, String> levelStatus = (Map<String, String>) request.getAttribute("LEVEL_STATUS");
    if(levelStatus == null) levelStatus = new HashMap<String, String>();
    
    Map<String, String> hmSection = (Map<String, String>) request.getAttribute("hmSection");
    if(hmSection == null) hmSection = new HashMap<String, String>();
    
    //boolean existLevelFlag = (Boolean)request.getAttribute("existLevelFlag");
    boolean existLevelFlag = false;
    Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
    if(hmQuestion == null) hmQuestion = new HashMap<String, List<String>>();
    Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
    if(answertypeSub == null) answertypeSub = new HashMap<String, List<List<String>>>();
    Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
    if(hmQuestionanswerType == null) hmQuestionanswerType = new HashMap<String, List<List<String>>>();
    List<String> answerTypeList = new ArrayList<String>();
    Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
    String resigAcceptedBy = (String) request.getAttribute("resigAcceptedBy");
    Map<String,String> hmApprovalStatus =(Map<String,String>)request.getAttribute("hmApprovalStatus");  
    if(hmApprovalStatus == null) hmApprovalStatus = new HashMap<String,String>();
    String effectiveid=(String)request.getAttribute("effectiveid");
    String strReason=(String)request.getAttribute("strReason");
    %>
<div class="leftbox reportWidth">
    <div id="queAsnDiv">
        <div class="addgoaltoreview">
            <h4><%=uF.showData(hmForm.get("FORM_NAME"),"") %></h4>
        </div>
        <%
            int size = 100 / alSectionId.size();
            String sectionCount = (String)request.getAttribute("sectionCount");
            
            double completePercent =(uF.parseToDouble(sectionCount)/uF.parseToDouble(""+alSectionId.size()))*100;
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
                <a href="javascript:void(0)" onclick="getStepTabContent(<s:property value="strEmpId"/>,<s:property value="resignId"/>,<s:property value="formId"/>,<%=currentLevel%>,null)">Instruction
                </a>
            </div>
               <%
                   size = alSectionId.size();
                   for (int i = 0; i < alSectionId.size(); i++) {
                   	if (request.getAttribute("levelCount").toString().equals("1")) {
                   		size = 0;
                   	}
                   	if (currentLevel.equals(alSectionId.get(i)) && !request.getAttribute("levelCount").toString().equals("1")) {
                   		size = i + 1;
                   %>
               <div class="step-tab">
                   <img src="images1/icons/bullet-green.png">
               </div>
               <%
                   } else {
                   	if (levelStatus.get(alSectionId.get(i)) != null) {
                   %>
               <div class="step-tab">
		          <a href="javascript:void(0)" onclick="getStepTabContent(<s:property value="strEmpId"/>,<s:property value="resignId"/>,<s:property value="formId"/>,<%=alSectionId.get(i)%>,<%=uF.parseToInt(request.getAttribute("levelCount").toString())+1 %>)">
		              <img src="images1/icons/bullet-green1.png">
		          </a>
		      </div>
               <%
                   } else {
                   %>
               <div class="step-tab">
		          <a href="javascript:void(0)" onclick="getStepTabContent(<s:property value="strEmpId"/>,<s:property value="resignId"/>,<s:property value="formId"/>,<%=alSectionId.get(i)%>,<%=uF.parseToInt(request.getAttribute("levelCount").toString())+1 %>)">
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
                <% if(existLevelFlag) {
                    %>
                <br/>
                <h1>You Already Filled This Section.</h1>
                <br/>
                <%	
                    } else {
                    %>
                <h4><%=size%>)&nbsp;<%=uF.showData(hmSection.get("SECTION_NAME"), "")%>
                    <input type="hidden" name="hideSectionId" id="hideSectionId" value="<%=uF.showData(hmSection.get("SECTION_ID"), "")%>" />
                </h4>
                <div style="line-height: 12px;">
                    <%=uF.showData(hmSection.get("SECTION_SHORT_DESCRIPTION"), "")%>				
                </div>
                <div style="line-height: 12px;">
                    <%=uF.showData(hmSection.get("SECTION_LONG_DESCRIPTION"), "")%>				
                </div>
                <div class="addgoaltoreview-arrow"></div>
                <% } %>
            </div>
            <%} %>
            <form action="ResigClearenceForm.action" id="ResigClearenceFormID" method="POST" <%if (alSectionId.size() == size) {%>target="_parent"<%} %>>
                <s:hidden name="strEmpId" id="strEmpId"></s:hidden>
                <s:hidden name="resignId" id="resignId"></s:hidden>
                <s:hidden name="formId" />
                <s:hidden name="currentLevel"/>
                <s:hidden name="levelCount" />
                <%if(existLevelFlag == true) {%>
                <%	
                    } else if(request.getAttribute("levelCount").toString().equals("1")) {
                    %>
                <div> No instructions provided. </div>
                <%	
                    }else{
                    	Iterator<String> it = hmSectionQuestion.keySet().iterator();
                    	while(it.hasNext()){
                    		String strSectionId = it.next();
                    		List<Map<String, String>> alSecQueList = (List<Map<String, String>>) hmSectionQuestion.get(strSectionId);
                    				
                    		for (int i = 0; alSecQueList != null && i < alSecQueList.size(); i++) {
                    			Map<String, String> hmSecQuestion = (Map<String, String>) alSecQueList.get(i);
                    			List<String> questioninnerList = hmQuestion.get(hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID"));
                    			
                    			Map<String, String> innerMp = questionanswerMp.get(strSectionId + "question" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID"));
                    			if (innerMp == null) innerMp = new HashMap<String, String>();
                    %>
                <div style="width: 80%; margin: 10px 50px 10px 50px;">
                    <ul>
                        <li><b><%=size%>.<%=(i + 1)%>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b></li>
                        <li>
                            <ul style="margin: 10px 10px 10px 30px">
                                <li>
                                    <%
                                        if (uF.parseToInt(questioninnerList.get(8)) == 1) {
                                        	if(!answerTypeList.contains("1")){		
                                        		answerTypeList.add("1");
                                        	}
                                        %>
                                    <div>
                                        a) <input type="checkbox" value="a" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
                                            checked <%}%> /><%=questioninnerList.get(2)%><br /> b) <input
                                            type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
                                            checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                        c) <input type="checkbox" value="c"
                                            name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
                                            checked <%}%> /><%=questioninnerList.get(4)%><br /> d) <input
                                            type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
                                            checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
                                        <textarea rows="5" cols="50" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
                                    </div>
                                    <%-- <div id="ansType1cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%> 
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
                                        	if(!answerTypeList.contains("2")){		
                                        	answerTypeList.add("2");
                                        	}
                                        %>
                                    <div>
                                        a) <input type="checkbox" value="a"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
                                            checked <%}%> name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />
                                        b) <input type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
                                            checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                        c) <input type="checkbox" value="c"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
                                            checked <%}%> name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />
                                        d) <input type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
                                            checked <%}%> value="d" /><%=questioninnerList.get(5)%>
                                    </div>
                                    <%-- <div id="ansType2cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
                                        	if(!answerTypeList.contains("3")){		
                                        	answerTypeList.add("3");
                                        	}
                                        %>
                                    <div>
                                        <%-- <input type="text" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>"
                                            id="marks<%=i%>" style="width: 31px;"
                                            onkeyup="isNumber(this.value,this.id,'<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>');"
                                            value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>" />/<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%> --%>	
                                        <input type="hidden" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
                                        <script>
                                            $(function() {
                                            	$("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
                                            		value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
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
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
                                        	if(!answerTypeList.contains("4")){		
                                        	answerTypeList.add("4");
                                        	}
                                        			List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                                        %>
                                    <div>
                                        <%
                                            for (int j = 0; j < outer.size(); j++) {
                                            				List<String> inner = outer.get(j);
                                            %>
                                        <input type="radio" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").equals(inner.get(0))) {%>
                                            checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
                                        <%
                                            }
                                            %>
                                    </div>
                                    <%-- <div id="ansType4cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
                                        	if(!answerTypeList.contains("5")){		
                                        	answerTypeList.add("5");
                                        	}
                                        			List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                                        %>
                                    <div>
                                        <%
                                            for (int j = 0; j < outer.size(); j++) {
                                            				List<String> inner = outer.get(j);
                                            %>
                                        <input type="radio" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
                                            checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
                                        <%
                                            }
                                            %>
                                    </div>
                                    <%-- <div id="ansType5cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
                                        	if(!answerTypeList.contains("6")){		
                                        	answerTypeList.add("6");
                                        	}
                                        			List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                                        %>
                                    <div>
                                        <%
                                            for (int j = 0; j < outer.size(); j++) {
                                            				List<String> inner = outer.get(j);
                                            %>
                                        <input type="radio" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
                                            checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
                                        <%
                                            }
                                            %>
                                    </div>
                                    <%-- <div id="ansType6cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
                                        	if(!answerTypeList.contains("7")){		
                                        	answerTypeList.add("7");
                                        	}
                                        %>
                                    <div>
                                        <textarea rows="5" cols="50" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
                                        <br /> <%-- <input type="text" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>"
                                            id="marks<%=i%>" style="width: 31px;"
                                            onkeyup="isNumber(this.value,this.id,'<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>');"
                                            value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>" />/<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%> --%>
                                        <input type="hidden" name="outofmarks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            id="outofmarks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" value="<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>" />
                                        <input type="hidden" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
                                        <script>
                                            $(function() {
                                            	$("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
                                            		value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
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
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
                                        	if(!answerTypeList.contains("8")){		
                                        	answerTypeList.add("8");
                                        	}
                                        %>
                                    <div>
                                        a) <input type="radio" value="a"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
                                            checked <%}%> name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />
                                        b) <input type="radio" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
                                            checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                        c) <input type="radio" value="c"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
                                            checked <%}%> name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />
                                        d) <input type="radio" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
                                            checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
                                    </div>
                                    <%-- <div id="ansType8cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
                                        	if(!answerTypeList.contains("9")){		
                                        	answerTypeList.add("9");
                                        	}
                                        %>
                                    <div>
                                        a) <input type="checkbox" value="a"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
                                            checked <%}%> name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />
                                        b) <input type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
                                            checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                        c) <input type="checkbox" value="c"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
                                            checked <%}%> name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />
                                        d) <input type="checkbox" name="correct<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
                                            checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
                                    </div>
                                    <%-- <div id="ansType9cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
                                        	if(!answerTypeList.contains("10")){		
                                        	answerTypeList.add("10");
                                        	}
                                        %> <%
                                        String[] a = null;
                                        
                                        			if (innerMp.get("ANSWER") != null) {
                                        				a = innerMp.get("ANSWER").split(":_:");
                                        %> <%
                                        }
                                        %>
                                    <div>
                                        <div style="float: left; margin: 30px 10px 0px 0px;">a)
                                        </div>
                                        <div>
                                            <textarea rows="5" cols="50" name="a<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=a != null ? a[0] : ""%></textarea>
                                            <br />
                                        </div>
                                        <div style="float: left; margin: 30px 10px 0px 0px;">b)
                                        </div>
                                        <div>
                                            <textarea rows="5" cols="50" name="b<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=a != null ? a[1] : ""%></textarea>
                                            <br />
                                        </div>
                                        <div style="float: left; margin: 30px 10px 0px 0px;">c)
                                        </div>
                                        <div>
                                            <textarea rows="5" cols="50" name="c<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=a != null ? a[2] : ""%></textarea>
                                            <br />
                                        </div>
                                        <div style="float: left; margin: 30px 10px 0px 0px;">d)
                                        </div>
                                        <div>
                                            <textarea rows="5" cols="50" name="d<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=a != null ? a[3] : ""%></textarea>
                                            <br />
                                        </div>
                                        <%-- <input type="text" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>"
                                            value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>"
                                            id="marks<%=i%>" style="width: 31px;"
                                            onkeyup="isNumber(this.value,this.id,'<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>');" />/<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%> --%>
                                        <input type="hidden" name="outofmarks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            id="outofmarks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" value="<%=hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE")%>" />
                                        <input type="hidden" name="marks<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                            id="marks<%=questioninnerList.get(8)+i%>_<%=questioninnerList.get(9)%>" style="width: 31px;"/>
                                        <script>
                                            $(function() {
                                            	$("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=questioninnerList.get(9)%>).slider({
                                            		value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
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
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
                                        	if(!answerTypeList.contains("11")){		
                                        	answerTypeList.add("11");
                                        	}
                                        	double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
                                        	double starweight = weightage*20/100;
                                        			//System.out.println("hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")_questioninnerList.get(9) ::::: "+hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
                                        %>
                                    <div id="starPrimary<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"></div>
                                    <input
                                        type="hidden" id="gradewithrating<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>"
                                        value="<%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>"
                                        name="gradewithrating<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" /> <script
                                        type="text/javascript">
                                        $(function() {
                                        	$('#starPrimary<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>').raty({
                                        		readOnly: false,
                                        		start: <%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>,
                                        		half: true,
                                        		targetType: 'number',
                                        		click: function(score, evt) {
                                        			$('#gradewithrating<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>')
                                        	.val(
                                        			score);
                                        }
                                        });
                                        });
                                    </script>
                                    <%-- <div id="ansType11cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%
                                        } else if (uF.parseToInt(questioninnerList.get(8)) == 12) { 
                                        	if(!answerTypeList.contains("12")){		
                                        	answerTypeList.add("12");
                                        	}
                                        	 %>
                                    <div>
                                        <textarea rows="5" cols="50" name="<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:100%"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
                                    </div>
                                    <%-- <div id="ansType12cmnt<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>">
                                        <br/><b>Comment:</b><br/>
                                        <textarea rows="3" cols="50" name="anscomment<%=hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")%>_<%=questioninnerList.get(9)%>" style="width:70%"><%=uF.showData(innerMp.get("ANSWERCOMMENT"),"") %></textarea>
                                        </div> --%>
                                    <%}%>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
                <hr>
                <%		}
                    }
                    }%>		
                <% if(answerTypeList.contains("4") || answerTypeList.contains("5") || answerTypeList.contains("6")) { %>
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
                                            <td style="text-align: left; min-width: 100px;"><%=innerlist.get(0)%>-<%=innerlist.get(1)%></td>
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
                </div>
                <% } %>
                <div class="clr margintop20">
                    <%if (alSectionId.size() == size) { %>
                    <%-- <s:submit value="Preview" cssClass="btn btn-primary" name="submit"></s:submit>  --%>
                    <s:submit value="Finish" cssClass="btn btn-primary" name="btnfinish" onclick="return confirm('Are you sure, you want to finish this?')"></s:submit>
                    <% } else {
                        if(request.getAttribute("levelCount").toString().equals("1")) {
                        %>
                    <s:submit value="Take Assessment" cssClass="btn btn-primary" name="submit"></s:submit>
                    <% 		} else { %>
                    <s:submit value="Next" cssClass="btn btn-primary" name="submit"></s:submit>
                    <% 	}
                        }
                        %>
                </div>
            </form>
        </div>
    </div>
</div>