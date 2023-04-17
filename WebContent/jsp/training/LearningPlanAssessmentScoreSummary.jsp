<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<script type="text/javascript">
    $(function() {
    	var a = '#from';
    	$("#from").datepicker({
    		format : 'dd/mm/yyyy'
    	});
    	$("#to").datepicker({
    		format : 'dd/mm/yyyy'
    	}); 
    });
</script>

<%
    Map<String, String> learningPlanMp = (Map<String, String>) request.getAttribute("learningPlanMp");
    Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
    Map hmScoreAggregateMap = (Map) request.getAttribute("hmScoreAggregateMap");
%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title"><%=learningPlanMp.get("LEARNING_PLAN_NAME")%></h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="leftbox reportWidth">
                        <s:form action="#" id="formID" method="POST" theme="simple">
                            <table class="table table-striped" width="98%">
                                <tr>
                                    <th width="15%" align="right">Learning Objective</th>
                                    <td><%=learningPlanMp.get("OBJECTIVE")%></td>
                                </tr>
                                <tr>
                                    <th valign="top" align="right">Aligned with</th>
                                    <td><%=learningPlanMp.get("ALIGNED_WITH")%></td>
                                </tr>
                                <tr>
                                    <th valign="top" align="right">Certificate</th>
                                    <td><%=learningPlanMp.get("CERTIFICATE")%></td>
                                </tr>
                                <tr>
                                    <th align="right">Effective Date</th>
                                    <td><%=learningPlanMp.get("FROM")%></td>
                                </tr>
                                <tr>
                                    <th align="right">Due Date</th>
                                    <td><%=learningPlanMp.get("TO")%></td>
                                </tr>
                                <tr>
                                    <th valign="top" align="right">Assignee</th>
                                    <td colspan="1"><%=learningPlanMp.get("ASSIGNEE")%></td>
                                </tr>
                                <tr>
                                    <th align="right">Attributes</th>
                                    <td><%=learningPlanMp.get("ATTRIBUTE")%></td>
                                </tr>
                                <tr>
                                    <th align="right">Skills</th>
                                    <td><%=learningPlanMp.get("SKILLS")%></td>
                                </tr>
                            </table>
                            <%
                                List<List<String>> assessmentList = (List<List<String>>) request.getAttribute("assessmentList");
                                Map<String, List<List<String>>> hmSectionData = (Map<String, List<List<String>>>) request.getAttribute("hmSectionData");
                                Map<String, Map<String, List<List<String>>>> hmAssessmentwiseQueData = (Map<String, Map<String, List<List<String>>>>) request.getAttribute("hmAssessmentwiseQueData");
                                	
                                List<List<String>> assessSectionList = (List<List<String>>) request.getAttribute("assessSectionList");
                                		UtilityFunctions uF = new UtilityFunctions();
                                		Map<String, List<List<String>>> hmAssessmentQueData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentQueData");		
                                %>
                            <%-- <h4>Sections (<%=assessSectionList != null ? assessSectionList.size() : "0" %>)</h4> --%>
                            <h4>Assessments (<%=assessmentList != null ? assessmentList.size() : "0" %>)</h4>
                            <%
                                for (int a = 0; assessmentList != null && a < assessmentList.size(); a++) {
                                		List<String> maininnerList = assessmentList.get(a);
                                %>
                            <div>
                                <div class="box box-default collapsed-box" style="margin-top: 10px;">
                                    <div class="box-header with-border">
                                        <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=a+1 %>)&nbsp;<%=maininnerList.get(1)%> 
                                           	&nbsp;&nbsp;&nbsp; <span class="label label-primary">
                                            <%=uF.showData(maininnerList.get(2), "")%>
                                            </span>
                                        </h3>
                                        <div class="box-tools pull-right">
                                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                        </div>
                                    </div>
                                    <!-- /.box-header -->
                                    <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                        <div class="content1">
                                            <%
                                                List<List<String>> sectionList =hmSectionData.get(maininnerList.get(0));
                                                for (int i = 0; sectionList != null && i < sectionList.size(); i++) {
                                                		List<String> innerList1 = sectionList.get(i);
                                                %>
                                            <div style="overflow: auto; border: 1px solid #EEEEEE;">
                                                <div style="text-align:left;">
                                                    <%-- <blockquote><strong><%=a %>.<%=i %>) Other</strong> --%>
                                                    <blockquote>
                                                        <strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=innerList1.get(1) %></strong>
                                                        <span>Aggregate Score: 
                                                        <%if(hmScoreAggregateMap != null && !hmScoreAggregateMap.isEmpty() && hmScoreAggregateMap.size() > 0 ) {
                                                            Set set3 = hmScoreAggregateMap.keySet();
                                                            Iterator it3 = set3.iterator();
                                                            while(it3.hasNext()){
                                                            	String str = (String)it3.next();
                                                            	Map hmTemp = (Map)hmScoreAggregateMap.get(str);
                                                            	if(hmTemp!=null){
                                                            %>
                                                        <%-- <%=hmorientationMembers.get(str)%> - --%> <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet")%>				
                                                        <%
                                                            }
                                                            }
                                                        }
                                                            %>
                                                        </span>
                                                    </blockquote>
                                                </div>
                                                <div style="overflow: hidden; float: left; border: 1px solid #eee; width: 99.8%;">
                                                    <table class="table table-bordered" style="width: 100%;">
                                                        <tr>
                                                            <th width="80%">Question</th>
                                                            <th style="text-align:right;">Weightage</th>
                                                            <th style="text-align:right;">Percentage</th>
                                                        </tr>
                                                        <%
                                                            List<List<String>> assessQuestionList =hmAssessmentQueData.get(innerList1.get(0));
                                                            	for (int k = 0; assessQuestionList != null && k < assessQuestionList.size(); k++) {
                                                            		List<String> queInnerList = assessQuestionList.get(k);
                                                            %>
                                                        <tr>
                                                            <td><%=a+1 %>.<%=i+1%>).
                                                                <%=k+1%>)&nbsp;<%=queInnerList.get(1)%>
                                                            </td>
                                                            <td style="text-align: right"><%=queInnerList.get(7)%>%</td>
                                                            <%
                                                                double avgPercent =0, totPercent = 0;
                                                                int roleCnt=0;
                                                                 if(hmScoreDetailsMap != null && !hmScoreDetailsMap.isEmpty() && hmScoreDetailsMap.size()>0) {
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
                                                                 }	%>
                                                            <th style="text-align: right"><%=avgPercent%>%</th>
                                                        </tr>
                                                        <%
                                                            }
                                                            %>
                                                    </table>
                                                </div>
                                            </div>
                                            <% } %>
                                        </div>
                                    </div>
                                    <!-- /.box-body -->
                                </div>
                            </div>
                            <% } %>
                            
                        </s:form>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>