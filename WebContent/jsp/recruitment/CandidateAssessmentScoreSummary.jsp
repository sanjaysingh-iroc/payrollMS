<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%
    Map<String, String> hmAssessmentData = (Map<String, String>) request.getAttribute("hmAssessmentData");
    
    	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
    	Map hmScoreAggregateMap = (Map) request.getAttribute("hmScoreAggregateMap");
    	
    %>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Learning Plan Summary" name="title"/>
    </jsp:include>
    --%>
<div class="leftbox reportWidth">
    <s:form action="#" id="formID" method="POST" theme="simple">
        <!-- <h2>Appraisal Summary</h2>
            <br/> -->
        <h3> 
            <%=hmAssessmentData.get("ASSESSMENT_NAME")%>
        </h3>
        <table class="table">
            <tr>
                <th align="right">Subject</th>
                <td><%=hmAssessmentData.get("SUBJECT")%></td>
            </tr>
            <tr>
                <th valign="top" align="right">Author</th>
                <td><%=hmAssessmentData.get("AUTHOR")%></td>
            </tr>
            <tr>
                <th valign="top" align="right">Version</th>
                <td><%=hmAssessmentData.get("VERSION")%></td>
            </tr>
            <tr>
                <th align="right">Times to attempt the assessment</th>
                <td><%=hmAssessmentData.get("ATTEMPT_COUNT")%></td>
            </tr>
            <tr>
                <th align="right">Time Duration</th>
                <td><%=hmAssessmentData.get("TIME_DURATION")%></td>
            </tr>
            <tr>
                <th valign="top" align="right">Grading Type</th>
                <td colspan="1"><%=hmAssessmentData.get("MARK_GRADE_TYPE")%></td>
            </tr>
            <tr>
                <th align="right">Grading Standard</th>
                <td><%=hmAssessmentData.get("MARK_GRADE_STANDARD")%></td>
            </tr>
            <tr>
                <th align="right">Description</th>
                <td><%=hmAssessmentData.get("DESCRIPTION")%></td>
            </tr>
        </table>
        <%
            Map<String, List<List<String>>> hmSectionData = (Map<String, List<List<String>>>) request.getAttribute("hmSectionData");
            Map<String, Map<String, List<List<String>>>> hmAssessmentwiseQueData = (Map<String, Map<String, List<List<String>>>>) request.getAttribute("hmAssessmentwiseQueData");
            
            List<List<String>> assessSectionList = (List<List<String>>) request.getAttribute("assessSectionList");
            	UtilityFunctions uF = new UtilityFunctions();
            	Map<String, List<List<String>>> hmAssessmentQueData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentQueData");		
            
            List<List<String>> sectionList =hmSectionData.get(hmAssessmentData.get("ID"));
            for (int i = 0; sectionList != null && i < sectionList.size(); i++) {
            	List<String> innerList1 = sectionList.get(i);
            %>
        <div class="box box-default collapsed-box" style="margin-top: 10px;">
            <div class="box-header with-border">
                <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=i+1 %>)&nbsp;&nbsp;<%=innerList1.get(1) %></h3>
                <div class="box-tools pull-right">
                	<span style="font-weight: normal;">Aggregate Score: 
	                <%
	                    Set set3 = hmScoreAggregateMap.keySet();
	                    Iterator it3 = set3.iterator();
	                    while(it3.hasNext()) {
	                    	String str = (String)it3.next();
	                    	Map hmTemp = (Map)hmScoreAggregateMap.get(str);
	                    	if(hmTemp!=null) {
	                    %>
	                <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet")%>
	                <% } } %>
	                </span>
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                <table class="table table-bordered">
                     <tr>
                         <td width="90%"><b>Question</b></td>
                         <td><b>Weightage</b></td>
                         <td>Percentage</td>
                     </tr>
                     <%
                         List<List<String>> assessQuestionList =hmAssessmentQueData.get(innerList1.get(0));
                         	for (int k = 0; assessQuestionList != null && k < assessQuestionList.size(); k++) {
                         		List<String> queInnerList = assessQuestionList.get(k);
                         %>
                     <tr>
                         <td><%=i+1%>).<%=k+1%>)&nbsp;<%=queInnerList.get(1)%></td>
                         <td style="text-align: right"><%=queInnerList.get(7)%>%</td>
                         <%
                             double avgPercent =0, totPercent = 0;
                             int roleCnt=0;
                             	Set set21 = hmScoreDetailsMap.keySet();
                             	Iterator it21 = set21.iterator();
                             	while(it21.hasNext()){
                             		roleCnt++;
                             		String str = (String)it21.next();
                             		Map hm = (Map)hmScoreDetailsMap.get(str);
                             		if(hm==null)hm=new HashMap();
                             		String strPercent = hm.get(queInnerList.get(9)) != null ? hm.get(queInnerList.get(9)).toString().replace("%","") : "0";
                             		double percent = uF.parseToDouble(strPercent);
                             		totPercent = percent;
                             		//totPercent += percent;
                             	}
                             	
                             	avgPercent = totPercent;
                             	//avgPercent = totPercent / roleCnt;
                             	%>
                         <th style="text-align: right"><%=avgPercent%>%</th>
                     </tr>
                     <% } %>
                 </table>
            </div>
        </div>
        <% } %>
    </s:form>
</div>