<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%
    List<List<String>> questionList = (List<List<String>>) request.getAttribute("questionList");
    
    Map<String,List<List<String>>> hmLevelQuestio1n = (Map<String,List<List<String>>>) request.getAttribute("hmLevelQuestion");
    Map hmLevelDetails = (Map) request.getAttribute("hmLevelDetails");
    
    Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
    Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
    List<String> answerTypeList = new ArrayList<String>();
    Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
    Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
    List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
    //System.out.println("appraisalList in jsp ---> "+appraisalList);
    List<String> empList = (List<String>) request.getAttribute("empList");
    
    Map<String, String> hmEmpDetails = (Map) request.getAttribute("hmEmpDetails");
    Map<String, String> levelStatus = (Map<String, String>) request.getAttribute("LEVEL_STATUS");
    List<String> levelList = (List<String>) request.getAttribute("levelList");
    List<String> mainLevelList = (List<String>) request.getAttribute("mainLevelList");
    String currentLevel = (String) request.getAttribute("currentLevel");
    Map<String, List<List<String>>> hmSection =(Map<String, List<List<String>>> )request.getAttribute("hmSection");
    List<String> sectionIDList = (List<String>)request.getAttribute("sectionIDList");
    List<String> IsQueSectionIDList = (List<String>) request.getAttribute("IsQueSectionIDList");
    
    %>
<%
    UtilityFunctions uF = new UtilityFunctions();
    String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
    //boolean isOfficialFilledStatus = uF.parseToBoolean((String)request.getAttribute("isOfficialFilledStatus"));
    String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
    %>

<div class="leftbox reportWidth">
    <div class="addgoaltoreview">
        <h4><%=appraisalList.get(1) %></h4>
        <div style="float: left; padding: 0px 20px; text-align: justify; line-height: 12px;">
            <%=appraisalList.get(2) %>				
        </div>
        <div class="addgoaltoreview-arrow"></div>
        <%-- <span style="float: right; margin: 0px 20px 0px 0px; font-size: 16px;"> Role As <b><%=hmEmpDetails.get("ORIENTATION")%></b></span> --%>
        <div style="float: right; margin: 0px 20px 0px 0px; font-size: 14px;">
            <table>
                <tr>
                    <td><u>role as</u> </td>
                    <td class="textblue">&nbsp;&nbsp;<b><%=hmEmpDetails.get("ORIENTATION")%></b></td>
                </tr>
            </table>
        </div>
    </div>
    <%
        int size = 100 / mainLevelList.size();
        String sectionCount = (String)request.getAttribute("sectionCount");

        double completePercent =(uF.parseToDouble(sectionCount)/uF.parseToDouble(""+mainLevelList.size()))*100;
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
            <a href="javascript:void(0)">Instruction</a>
        </div>

        <%
            size = mainLevelList.size();
            for (int i = 0; i < mainLevelList.size(); i++) {								
            %>
	          <div class="step-tab">
			     <img src="images1/icons/bullet-green.png">
			  </div>
        <%	
            }
            %>
    </div>
    <div class="step-tab-content">
        <%if(appraisalList.get(5) != null && !appraisalList.get(5).equals("")){ %>
        <%=appraisalList.get(5) %>
        <% } else { %>
        <div> No instructions provided. </div>
        <!-- <div class="nodata msg"> No instructions provided. </div> -->
        <% } %>
    </div>
    <br/>
    
    <s:form action="StaffAppraisal" id="formID" method="POST" theme="simple">
        <%
            int sectionCnt=0;
            Set set = hmSection.keySet();
            Iterator it = set.iterator();
            while(it.hasNext()) {
            	sectionCnt++;
            	String str = (String)it.next();
            	//System.out.println("str :: "+str);	
            	int subsectionCnt = 0;
            	String strSectionComment = null;
            	%>
        <div class="addgoaltoreview">
            <h4><%=sectionCnt %>)&nbsp;<%=uF.showData((String)hmLevelDetails.get(str+"_TITLE"), "")%>  </h4>
            <div style="line-height: 12px;">
                <%=uF.showData((String)hmLevelDetails.get(str+"_SDESC"), "")%>				
            </div>
            <div style="line-height: 12px;">
                <%=uF.showData((String)hmLevelDetails.get(str+"_LDESC"), "")%> 				
            </div>
            <!-- <div class="addgoaltoreview-arrow"></div> -->
        </div>
        <%-- <%if( sectionIDList == null || sectionIDList.isEmpty() || !sectionIDList.contains(str)) { %> --%>
        <% if( sectionIDList != null && !sectionIDList.isEmpty() && sectionIDList.contains(str)) { %>
        <%
            List<List<String>> alQuestion11 = hmSection.get(str);
        	//System.out.println("alQuestion11 :::: " + alQuestion11);
            for(int kk=0;kk<alQuestion11.size();kk++) {
            	subsectionCnt++;
            	//System.out.println("subsectionCnt 1 :: "+subsectionCnt);
            	List<String> innerList=alQuestion11.get(kk);
            	List<List<String>> alQuestion=hmLevelQuestio1n.get(innerList.get(0));
            %>
        <div class="addgoaltoreview">
            <h4><%=sectionCnt %>.<%=subsectionCnt %>)&nbsp;<%=uF.showData(innerList.get(1), "")%>  </h4>
            <div style="line-height: 12px;">
                <%=uF.showData(innerList.get(2), "")%>				
            </div>
            <br/>
            <div style=" line-height: 12px;">
                <%=uF.showData(innerList.get(3), "")%> 				
            </div>
            <div class="addgoaltoreview-arrow"></div>
        </div>
       		<%
           		for (int i = 0; alQuestion != null && i < alQuestion.size(); i++) {
           			List<String> innerlist = (List<String>) alQuestion.get(i);
           			List<String> questioninnerList = hmQuestion.get(innerlist.get(1));
           
           		Map<String, String> innerMp = null;
           		if (innerlist.get(14) != null && !innerlist.get(14).equals("")) {
           			innerMp = questionanswerMp.get(innerlist.get(14) + "question" + innerlist.get(1));
           		}else if (innerlist.get(15) != null && !innerlist.get(15).equals("")) {
           			innerMp = questionanswerMp.get(innerlist.get(15) + "question" + innerlist.get(1));
           		} else {
           			innerMp = questionanswerMp.get("question" + innerlist.get(1));
           		}
           		if (innerMp == null) innerMp = new HashMap<String, String>();
           		strSectionComment = innerMp.get("LEVEL_COMMENT");
            %>
        <div style="width:96%;">
            <ul>
                <li>
                    <b><%=sectionCnt %>.<%=subsectionCnt %>.<%=(i + 1)%>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b> 
                    <s:if test="innerlist.get(3)!=null">(<%=innerlist.get(12)%>)</s:if>
                </li>
                <li>
                    <ul style="margin: 10px 10px 10px 30px">
                        <li>
                            <% if (uF.parseToInt(questioninnerList.get(8)) == 1) { %>
                            <div>
                                a) <input type="checkbox" disabled="disabled" value="a" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%> checked <%}%> /><%=questioninnerList.get(2)%><br /> 
                                b) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                c) <input type="checkbox" disabled="disabled" value="c" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%> checked <%}%> /><%=questioninnerList.get(4)%><br /> 
                                d) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
                                <textarea rows="5" cols="50" readonly="readonly" style="width:100%" name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
                               		answerTypeList.add("2");
                                %>
                            <div>
                                a) <input type="checkbox" disabled="disabled" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
                                b) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                c) <input type="checkbox" disabled="disabled" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
                                d) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
                               	answerTypeList.add("3");
                            %>
                            <div>
                                <input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width: 31px;"/>
                                <script>
                                    $(function() {
                                    	$("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
                                    		value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
                                    		min : 0,
                                    		max : <%=innerlist.get(2)%>,
                                    		step : 1,
                                    		disabled:true,
                                    		slide : function(event, ui) {
                                    			$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val(ui.value);
                                    			$("#slidemarksscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html(ui.value);
                                    		}
                                    	});
                                    	$("#marks"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").val($("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
                                    	$("#slidemarksscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").html($("#sliderscore"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
                                    });
                                </script>
                                <br/>
                                <div id="slidemarksscore<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; text-align:center;"></div>
                                <div id="sliderscore<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
                                <div id="marksscore<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
                         			answerTypeList.add("4");
                         			List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                                %>
                            <div>
                                <% for (int j = 0; j < outer.size(); j++) {
                                   	List<String> inner = outer.get(j);
                                %>
                                <input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").equals(inner.get(0))) {%> checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
                                <% } %>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
                               	answerTypeList.add("5");
                               	List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                            %>
                            <div>
                            <% for (int j = 0; j < outer.size(); j++) {
                               	List<String> inner = outer.get(j);
                            %>
                                <input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%> checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
                            <% } %>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
                               	answerTypeList.add("6");
                               	List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                            %>
                            <div>
                            <% for (int j = 0; j < outer.size(); j++) {
                                List<String> inner = outer.get(j);
                            %>
                                <input type="radio" disabled="disabled" name="<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%> checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
                            <% } %>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
                               	answerTypeList.add("7");
                            %>
                            <div>
                                <strong>Ans:</strong>&nbsp;<%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : "" %>
                                <input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" id="outofmarks<%=i%>" value="<%=innerlist.get(2)%>" />
                                <input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks<%=i%>" style="width: 31px;"/>
                                <script>
                                    $(function() {
                                    	$("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
                                    		value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
                                    		min : 0,
                                    		max : <%=innerlist.get(2)%>,
                                    		step : 1,
                                    		disabled:true,
                                    		slide : function(event, ui) {
                                    			$("#marks"+<%=i%>+"").val(ui.value);
                                    			$("#slidemarkssingleopen"+<%=i%>+"").html(ui.value);
                                    		}
                                    	});
                                    	$("#marks"+<%=i%>+"").val($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
                                    	$("#slidemarkssingleopen"+<%=i%>+"").html($("#slidersingleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
                                    });
                                </script>
                                <br/>
                                <div id="slidemarkssingleopen<%=i%>" style="width:25%; text-align:center;"></div>
                                <div id="slidersingleopen<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
                                <div id="markssingleopen<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
                               	answerTypeList.add("8");
                            %>
                            <div>
                                a) <input type="radio" value="a" disabled="disabled" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
                                b) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                c) <input type="radio" disabled="disabled" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
                                d) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
                               	answerTypeList.add("9");
                            %>
                            <div>
                                a) <input type="checkbox" disabled="disabled" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
                                b) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                c) <input type="checkbox" disabled="disabled" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
                                d) <input type="checkbox" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
                               	answerTypeList.add("10");
                                String[] a = null;
                                if (innerMp.get("ANSWER") != null) {
                                	a = innerMp.get("ANSWER").split(":_:");
                                }
                            %>
                            <div>
                                <div style="float: left; margin: 30px 10px 0px 0px;">a)</div>
                                <div><textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="a<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[0] : ""%></textarea><br /></div>
                                <div style="float: left; margin: 30px 10px 0px 0px;">b)</div>
                                <div><textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="b<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[1] : ""%></textarea><br /></div>
                                <div style="float: left; margin: 30px 10px 0px 0px;">c)</div>
                                <div><textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="c<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[2] : ""%></textarea><br /></div>
                                <div style="float: left; margin: 30px 10px 0px 0px;">d)</div>
                                <div><textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="d<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=a != null ? a[3] : ""%></textarea><br/></div>
                                <input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" id="outofmarks<%=i%>" value="<%=innerlist.get(2)%>" />
                                <input type="hidden" name="marks<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" id="marks<%=i%>" style="width: 31px;"/>
                                <script>
                                    $(function() {
                                    	$("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider({
                                    		value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
                                    		min : 0,
                                    		max : <%=innerlist.get(2)%>,
                                    		step : 1,
                                    		disabled:true,
                                    		slide : function(event, ui) {
                                    			$("#marks"+<%=i%>+"").val(ui.value);
                                    			$("#slidemarksmultipleopen"+<%=i%>+"").html(ui.value);
                                    		}
                                    	});
                                    	$("#marks"+<%=i%>+"").val($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
                                    	$("#slidemarksmultipleopen"+<%=i%>+"").html($("#slidermultipleopen"+<%=questioninnerList.get(8)+i%>+"_"+<%=innerMp.get("APP_QUE_ANS_ID")%>+"").slider("value"));
                                    });
                                </script>
                                <br/>
                                <div id="slidemarksmultipleopen<%=i%>" style="width:25%; text-align:center;"></div>
                                <div id="slidermultipleopen<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%; float: left;"></div>
                                <div id="marksmultipleopen<%=questioninnerList.get(8)+i%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" style="width:25%;">0 <span style="float:right;"><%=innerlist.get(2)%></span></div>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
                       			answerTypeList.add("11");
                       			double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
                       			double starweight = weightage*20/100;
                            %>
                            <div id="starPrimary<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"></div>
                            <input type="hidden" id="gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" value="<%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>" name="gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /> 
                            <script type="text/javascript">
                                $(function() {
                                	$('#starPrimary<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>').raty({
                                		readOnly: true,
                                		start: <%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0"%>,
                                		half: true,
                                		targetType: 'number',
                                		click: function(score, evt) {
                                			$('#gradewithrating<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>').val(score);
                               			}
                                	});
                               	});
                            </script>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
                                answerTypeList.add("12");
                            %>
                            <div>
                                <strong>Ans:</strong>
                                <textarea rows="5" cols="50" readonly="readonly" style="width:100%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
                               	answerTypeList.add("13");
                            %>
                            <div>
                                a) <input type="radio" value="a" disabled="disabled" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(2)%><br />
                                b) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />
                                c) <input type="radio" disabled="disabled" value="c" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%> checked <%}%> name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" /><%=questioninnerList.get(4)%><br />
                                d) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%> checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
                                e) <input type="radio" disabled="disabled" name="correct<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("e")) {%> checked <%}%> value="e" /><%=questioninnerList.get(9)%><br />
                            </div>
                            <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=innerlist.get(1)%>_<%=innerMp.get("APP_QUE_ANS_ID")%>"><%=innerMp.get("ANSWERCOMMENT") != null ? innerMp.get("ANSWERCOMMENT") : ""%></textarea></div>
                            <% } %>
                        </li>
                        
                    </ul>
                </li>
            </ul>
        </div>
        <hr>
        <%
            } 
       	}
        } %>
        
        <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="levelcomment<%=str %>"><%=strSectionComment != null ? strSectionComment : ""%></textarea></div>
        <% } %>
    </s:form>
</div>