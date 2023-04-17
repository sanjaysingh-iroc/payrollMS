<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script src="scripts/ckeditor_cust/ckeditor.js"></script>
<style type="text/css">
    /* CSS for the demo. CSS needed for the scripts are loaded dynamically by the scripts */
    .txtlbl {
    color: #777777;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 11px;
    font-style: normal;
    font-weight: 600;
    width: 100px;
    }
    .ul_class li {
    margin: 10px 0px 10px 100px;
    }
    .clear{
    clear:both;
    }
    img{
    border:0px;
    }	
    .ui-tabs-hide{
    display:none !important;
    }
</style>
<%String tab = (String)request.getAttribute("tab"); %>
<script type="text/javascript">
    $(document).ready(function() {

    	$('#container').tabs({
   			fxAutoHeight : true
   		});
    		
    });
    
    function deleteAssessment(assessId) {
    	
    	$("#divCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
    	if(confirm('Are you sure, you want to delete this assessment?')) {
			$.ajax({
				type:'POST',
				url:'AddNewAssessment.action?operation=D&fromPage=LD&assessmentId='+assessId,
		        cache:true,
				success:function(result){
					//alert("delete assess result==>"+result);
					$("#divCDResult").html(result);
				}, 
	 			error : function(err) {
	 				$.ajax({ 
						url: 'CourseDashboardData.action?dataType=A',
						cache: true,
						success: function(result){
							$("#divCDResult").html(result);
				   		}
					});
	 			}
			});
    	} else {
    		$.ajax({ 
				url: 'CourseDashboardData.action?dataType=A',
				cache: true,
				success: function(result){
					$("#divCDResult").html(result);
		   		}
			});
    	}
    }
    
  function editAssessment(assessId) {
    	$("#divCDResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
			type:'POST',
			url:'AddNewAssessment.action?operation=E&fromPage=LD&assessmentId='+assessId,
	        cache:true,
			success:function(result){
				$("#divCDResult").html(result);
			}
		});
    	
    }
</script>
	<%
        UtilityFunctions uF = new UtilityFunctions();
        List<List<String>> ansTypeList = (List<List<String>>) request.getAttribute("ansTypeList");
        String assessmentId = (String)request.getAttribute("assessmentId");
        String assessPreface = (String)request.getAttribute("assessPreface");
        List<List<String>> sectionList = (List<List<String>>) request.getAttribute("sectionList");
        Map<String, List<List<String>>> hmAssessmentQueData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentQueData");
        Map<String, String> hmAnstypeName = (Map<String, String>) request.getAttribute("hmAnstypeName");
        String fromPage = (String)request.getAttribute("fromPage");
    	String sbAssessments = (String)request.getAttribute("sbAssessments"); 
	%>
        
 <%if(fromPage != null && fromPage.equalsIgnoreCase("LD")) { %>
	<div style="width:100%;font-size:14px;">
		<%=uF.showData(sbAssessments,"") %>
	</div>
<% } %>
<div id="printDiv" class="leftbox reportWidth" style="margin-top:10px;">
        
	<% if(uF.parseToInt(assessmentId)>0) { %>
    <div style="width: 100%; background-color: #868686; box-shadow:3px 4px 12px #616161;height: 80%;">
        
        <div id="container" style="width: 99%; float: left;height:98%; min-height: 600px;font-size: 12px;">
            <ul>
                <li><a href="#course"><span>Cover Page</span> </a></li>
                <li><a href="#preface"><span>Description</span> </a></li>
                <%if(assessmentId != null) { %>
                <li id="tabindex" ><a href="#index"><span>Index</span> </a></li>
                <% if(sectionList != null && !sectionList.isEmpty()) {
                    for(int i=0; i< sectionList.size(); i++) {
                    	List<String> innerList = sectionList.get(i);
                    	
                    	%>
                <li  id="tabassessment<%=i+1 %>"><a href="#assessment<%=i+1 %>"><span><%=uF.showData(innerList.get(1), "") %> </span></a></li>
                <%
                    }
                    }
                    %>
                <% } %>
            </ul>
            <div style=" border: solid 0px #ff0000;width:96%;" id="course">
                <div style="float: left; width: 100%; min-height: 500px;">
                    <table border="0" class="formcss" style="width: 85%;">
                        <tr>
                            <td align="center">
                                <br/><br/><br/> 
                                <div style="float: left; margin: 10px; width: 100%; font-size: 28px; font-weight: bold; font-style: italic; font-family: Verdana;">
                                    <%=request.getAttribute("assessmentName") %>
                                </div>
                                <br/>
                                <div style="width: 100%; float: left; margin: 5px;">Subject: <%=request.getAttribute("assessmentSubject") %></div>
                                <br/>
                                <div style="width: 100%; float: left; margin: 5px;">Author: <%=request.getAttribute("assessmentAuthor") %></div>
                                <br/>
                                <div style="width: 100%; float: left; margin: 5px;">Version: <%=request.getAttribute("assessmentVersion") %></div>
                                <br/>
                                <div style="width: 100%; float: left; margin: 5px;">Times To Attempt: <%=request.getAttribute("timesToAttempt") %></div>
                                <br/>
                                <div style="width: 100%; float: left; margin: 5px;">Time Duration: <%=request.getAttribute("timeDuration") %></div>
                                <br/>
                                <div style="width: 100%; float: left; margin: 5px;">Grading Type: <%=request.getAttribute("gradingType") %></div>
                                <br/>
                                <div style="width: 100%; float: left; margin: 5px;">Grading Standard: <%=request.getAttribute("gradingStandard") %></div>
                                <br/>
                            </td>
                        </tr>
                    </table>
                </div>
                <div style="float: right;">1</div>
            </div>
            <div style=" border: solid 0px #ff0000;width:96%;" id="preface">
                <div style="float: left; width: 100%; min-height: 500px;">
                    <table border="0" class="formcss" style="width: 90%; margin-left: 5%;">
                        <tr>
                            <td class="txtlabel" style="vertical-align: top; text-align: center;">
                                <h1>Description:</h1>
                            </td>
                        </tr>
                        <tr>
                            <td> <%=uF.showData(assessPreface, "") %> </td>
                        </tr>
                    </table>
                </div>
                <div style="float: right;">2</div>
            </div>
            <% if(assessmentId != null) { %>
            <div style="border: solid 0px #ff0000;width:96%;" id="index">
                <div style="float: left; width: 100%; min-height: 500px;">
                    <table border="0" class="formcss" style="width: 90%; margin-left: 5%;">
                        <tr>
                            <td colspan="4" class="txtlabel" style="vertical-align: top; text-align: center;">
                                <h1>Index:</h1>
                            </td>
                        </tr>
                        <% 
                            if(sectionList != null && !sectionList.isEmpty()){
                            %>
                        <tr>
                            <td height="10px">&nbsp;</td>
                            <td height="10px" style="font-size: 13px; font-weight: bold;">Particulars</td>
                            <td class="txtlbl" height="10px">&nbsp;</td>
                            <td height="10px" align="right" style="font-size: 13px; font-weight: bold;">Page No.</td>
                        </tr>
                        <%
                            for(int i=0; i< sectionList.size(); i++){
                            	List<String> innerList = sectionList.get(i);
                            %> 
                        <tr>
                            <td class="txtlbl" height="10px" align="right" style="width: 20px;"><%=i+1 %>)&nbsp;&nbsp;</td>
                            <td class="txtlbl" height="10px" colspan="2"><%=innerList.get(1) %></td>
                            <td class="txtlbl" height="10px" align="right"><%=i+4 %></td>
                        </tr>
                        <% } } %>
                        <% if(sectionList == null || sectionList.isEmpty()){ %>
                        <tr>
                            <td class="txtlbl" colspan="4"><b>'No Content Added'</b></td>
                        </tr>
                        <% } %>
                    </table>
                </div>
                <div style="float: right;">3</div>
            </div>
            <% 
                String chapterCnt = (String) request.getAttribute("chapterCnt");
                if(sectionList != null && !sectionList.isEmpty()){
                for(int i=0; i< sectionList.size(); i++){
                	List<String> innerList = sectionList.get(i);
                	
                %>
            <div style="border: solid 0px #ff0000;width:96%;" id="assessment<%=i+1 %>">
                <div style="float: left; width: 100%; min-height: 500px;">
                    <table border="0" class="formcss" style="width: 85%; margin-left: 5%;">
                        <tr>
                            <td>
                                <h1><%=i+1 %>)&nbsp;<%=uF.showData(innerList.get(1), "") %></h1>
                            </td>
                        </tr>
                        <tr>
                            <td><%=uF.showData(innerList.get(2), "") %></td>
                        </tr>
                        <tr>
                            <td>Marks for section : <%=uF.showData(innerList.get(3), "") %></td>
                        </tr>
                        <tr>
                            <td> Attempt questions : Any <%=uF.showData(innerList.get(4), "") %></td>
                        </tr>
                    </table>
                    <% 
                        String showQueDiv = "none";
                        int assessSize = 0;
                        if(hmAssessmentQueData != null && !hmAssessmentQueData.isEmpty()) {
                        	List<List<String>> questionList = hmAssessmentQueData.get(innerList.get(0));
                        	
                        	if(questionList != null) { 
                        		showQueDiv = "block";
                        		assessSize = questionList.size();
                        	} 
                        }
                        //System.out.println("assessSize ===> " + assessSize);
                        %>
                    <div id="questionsDiv<%=i+1 %>" style="float: left; width: 100%; min-height: 385px; display: <%=showQueDiv %>;">
                        <!-- border: 1px solid; display: none; -->
                        <%
                            if(hmAssessmentQueData != null && !hmAssessmentQueData.isEmpty()) {
                            	List<List<String>> assessmentList = hmAssessmentQueData.get(innerList.get(0));
                            		if(assessmentList != null) {
                            	for(int k=0; assessmentList != null && k < assessmentList.size(); k++) {
                            		List<String> assessinnerList = assessmentList.get(k);
                            		if(assessinnerList != null) {
                            %>
                        <ul id="questionUl<%=(i+1)+"_"+(k+1) %>" style="margin-left: 100px;">
                            <li>
                                <table class="tb_style" width="100%">
                                    <tr>
                                        <th style="width: 40px;"><%=i+1 %>.<%=k+1 %>)</th>
                                        <td colspan="3"><span style="float: left; margin-left: 10px; width: 90%;"><%=assessinnerList.get(0) %>&nbsp;&nbsp;(<%=hmAnstypeName.get(assessinnerList.get(9)) %>) </span>
                                        </td>
                                    </tr>
                                    <%
                                        int getanstype = uF.parseToInt(assessinnerList.get(9));
                                        if(getanstype == 1 || getanstype == 2 || getanstype == 8 || getanstype == 9) { %>
                                    <tr id="answerType<%=i+1 %>_<%=k+1 %>">
                                        <th></th>
                                        <td>a)&nbsp;<%=assessinnerList.get(4)%> </td>
                                        <td colspan="2">b)&nbsp;<%=assessinnerList.get(5)%></td>
                                    </tr>
                                    <tr id="answerType1<%=i+1 %>_<%=k+1 %>">
                                        <th></th>
                                        <td>c)&nbsp;<%=assessinnerList.get(6)%></td>
                                        <td colspan="2">d)&nbsp;<%=assessinnerList.get(7)%></td>
                                    </tr>
                                    <% } else if(getanstype == 6) { %>
                                    <tr id="answerType<%=i+1 %>_<%=k+1 %>">
                                        <th></th>
                                        <td colspan="3">&nbsp;True&nbsp;&nbsp;False</td>
                                    </tr>
                                    <% } else if(getanstype == 5) { %>
                                    <tr id="answerType<%=i+1 %>_<%=k+1 %>">
                                        <th></th>
                                        <td colspan="3">&nbsp;Yes&nbsp;&nbsp;No</td>
                                    </tr>
                                    <% } else if(getanstype == 14) { %>
	                                <tr id="matrixHeading<%=i+1 %>_<%=k+1 %>">
	                                    <th></th>
	                                    <td>&nbsp;&nbsp;&nbsp;&nbsp;<%=assessinnerList.get(13)%> </td>
	                                    <td colspan="2"></td>
	                                </tr>
	                                <tr id="answerType<%=i+1 %>_<%=k+1 %>">
	                                    <th></th>
	                                    <td>a)&nbsp;<%=assessinnerList.get(4)%> </td>
	                                    <td colspan="2"></td>
	                                </tr>
	                                <tr id="1answerType<%=i+1 %>_<%=k+1 %>">
	                                    <th></th>
	                                    <td>b)&nbsp;<%=assessinnerList.get(5)%> </td>
	                                    <td colspan="2"></td>
	                                </tr>
	                                <tr id="answerType1<%=i+1 %>_<%=k+1 %>">
	                                    <th></th>
	                                    <td>c)&nbsp;<%=assessinnerList.get(6)%></td>
	                                    <td colspan="2"></td>
	                                </tr>
	                                <tr id="1answerType1<%=i+1 %>_<%=k+1 %>">
	                                    <th></th>
	                                    <td>d)&nbsp;<%=assessinnerList.get(7)%></td>
	                                    <td colspan="2"></td>
	                                </tr>
	                                <% } %>
                                </table>
                            </li>
                        </ul>
                        <% } } } } %>
                        <!-- </li></ul> -->
                    </div>
                </div>
                <div style="float: right;"><%=i+4 %></div>
            </div>
            <%
                }
                }
                %>
            <% } %>
        </div>
    </div>    
	<% } else { %>
		<div class="nodata msg"> No assessment added yet. </div>
	<% } %>
    
</div>