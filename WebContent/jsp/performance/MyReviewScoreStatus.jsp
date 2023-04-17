<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<style>
#statusbody p{
margin: 0px !important;
}
.statusheader {
background-color: rgb(245, 245, 245);
padding: 5px;
}
</style>
<script> 
    var memberId='<%=request.getAttribute("memberId") %>';
    function getScoreDetails(id,empId,levelId,scoreId,appFreqId){
    	var action="MyReviewScore.action?id="+id+"&empid="+empId+"&type=popup&levelid="+levelId+"&scoreid="+scoreId+"&appFreqId="+appFreqId;
    	if(memberId!='null'){
    		action+="&memberId="+memberId; 
    	}
    	var dialogEdit = '#modal-body1';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo1").show();
		 $(".modal-title1").html('See Score');
		 $.ajax({
				
				url : action,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
    }
</script>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Appraisal Status" name="title" />
    </jsp:include> --%>
<%
    Map<String, List<Map<String,String>>> scoreMp = (Map<String, List<Map<String,String>>>) request.getAttribute("scoreMp");
    	
    	UtilityFunctions uF = new UtilityFunctions();
    	String appFreqId = (String) request.getParameter("appFreqId");
    	Map<String, String> hmAppLevelName = (Map<String, String>) request.getAttribute("hmAppLevelName");
    
    %>

<div id="statusbody">
<table class="table table-bordered" style="display:none">
    <tr>
        <th width="20%">Level</th>
        <th>Scorecard</th>
        <th>Total</th>
        <th>Rating</th>
    </tr>
    <%
        double marksTotal = 0;
        double outOfmarksTotal = 0;
        
        Iterator it = hmAppLevelName.keySet().iterator();
        int j=0;
        while (it.hasNext()) {
        	String key = (String) it.next();
        	List<Map<String,String>> outerList = scoreMp.get(key);%>
    <tr>
        <td width="20%" colspan="4"><%=hmAppLevelName.get(key)%></td>
    </tr>
    <%for (int i=0; outerList != null && i < outerList.size(); i++) {
        Map<String,String> innerMap =outerList.get(i);
        if(innerMap==null)innerMap=new HashMap<String,String>();
        
        marksTotal+=uF.parseToDouble(innerMap.get("MARKS"));
        outOfmarksTotal+=uF.parseToDouble(innerMap.get("WEIGHTAGE"));
        %>
    <tr>
        <td>&nbsp;</td>
        <td><%=innerMap.get("SCORECARD")%></td>
        <td align="center">
            <a onclick="getScoreDetails('<s:property value="id"/>','<s:property value="empid"/>','<%=innerMap.get("LEVEL_ID") %>','<%=innerMap.get("SCORE_ID") %>','<%=appFreqId %>')"
                href="javascript:void(0)"><%=uF.showData(innerMap.get("MARKS"),"0")%>/<%=uF.showData(innerMap.get("WEIGHTAGE"),"0")%>
            </a>
        </td>
        <td align="center">
            <div id="starPrimary<%=j%>"></div>
            <script type="text/javascript">
                $(function(){
                	$('#starPrimary'+ '<%=j%>').raty(
                	{
                		readOnly : true,
                		start : <%=uF.showData(innerMap.get("AVERAGE"),"0")%>,
                		half : true,
                		targetType : 'number'
                	});
                	});
            </script>
        </td>
    </tr>
    <%
        j++;
        		}
        	}
        %>
    <tr>
        <td width="20%">&nbsp;</td>
        <td align="right"><b>Total</b></td>
        <td align="center"><b><%=marksTotal%>/<%=outOfmarksTotal%></b></td>
        <td align="center">
            <div id="starPrimary"></div>
            <script type="text/javascript">
                $(function(){
                	$('#starPrimary').raty(
                	{
                		readOnly : true,
                		start : <%=(marksTotal*100)/(outOfmarksTotal*20)%>,
                		half : true,
                		targetType : 'number'
                	});
                	});
                				
            </script>
        </td>
    </tr>
</table>
<%
    Map<String, String> hmorientationMembers = (Map) request.getAttribute("hmorientationMembers");
    
    Map hmScoreQuestionsMap = (Map) request.getAttribute("hmScoreQuestionsMap");
    Map hmOtherQuestionsMap = (Map) request.getAttribute("hmOtherQuestionsMap");
    Map hmLevelScoreMap = (Map) request.getAttribute("hmLevelScoreMap");
    
    
    Map hmLevel = (Map) request.getAttribute("hmLevel");
    Map hmQuestions = (Map) request.getAttribute("hmQuestions");
    Map hmScoreCard = (Map) request.getAttribute("hmScoreCard");
    Map hmOptions = (Map) request.getAttribute("hmOptions");
	String memberId = (String) request.getAttribute("memberId");
    Map hmQuestionMarks = (Map) request.getAttribute("hmQuestionMarks");
    Map hmQuestionWeightage = (Map) request.getAttribute("hmQuestionWeightage");
    Map hmQuestionAnswer = (Map) request.getAttribute("hmQuestionAnswer");
    Map hmQuestionRemak = (Map) request.getAttribute("hmQuestionRemak");
    List alRoles = (List)request.getAttribute("alRoles");
    List rolesUserIds = (List)request.getAttribute("rolesUserIds");
	Map<String, String> useNameMP = (Map<String, String>) request.getAttribute("useNameMP");
	if(useNameMP == null) useNameMP = new HashMap<String, String>();
    Map<String, List<String>> hmOuterpeerAppraisalDetails = (Map<String, List<String>> )request.getAttribute("hmOuterpeerAppraisalDetails");
    Map<String, String> hmOuterpeerAnsDetails = (Map<String, String> )request.getAttribute("hmOuterpeerAnsDetails");
    List<String> sectionIdsList = (List<String>)request.getAttribute("sectionIdsList");
    if(sectionIdsList == null) sectionIdsList = new ArrayList<String>();
    Map<String, List<String>> hmSubsectionIds = (Map<String, List<String>>)request.getAttribute("hmSubsectionIds");
    if(hmSubsectionIds == null) hmSubsectionIds = new LinkedHashMap<String, List<String>>();
    Map<String, String> hmSectionDetails = (Map<String, String>)request.getAttribute("hmSectionDetails");
    if(hmSectionDetails == null) hmSectionDetails = new HashMap<String, String>();
    
  %>
<% if(rolesUserIds != null && !rolesUserIds.isEmpty() && rolesUserIds.size()>0) { 
	 for(int m=0; m<rolesUserIds.size(); m++){%>
		<strong><%=useNameMP.get(rolesUserIds.get(m)) %>(<%=hmorientationMembers.get(memberId)%>)</strong>
		<%for(int a=0; sectionIdsList != null && !sectionIdsList.isEmpty() && a<sectionIdsList.size(); a++) {
		    List<String> alLevelScore = hmSubsectionIds.get(sectionIdsList.get(a)+"SCR");
		     int cnt=0;
    	%>
			<div class="statusheader">
				<h4><%=a+1 %>)&nbsp;<%=hmSectionDetails.get(sectionIdsList.get(a))%></h4>
				<div ><%=uF.showData(hmSectionDetails.get(sectionIdsList.get(a)+"_SD"), "")%></div>
				<div ><%=uF.showData(hmSectionDetails.get(sectionIdsList.get(a)+"_LD"), "")%></div>
		   </div>
			<%for(int i=0; i<alLevelScore.size(); i++){
			    cnt++;
			    List alScore = (List)hmLevelScoreMap.get((String)alLevelScore.get(i));
			    if(alScore!=null && !alScore.isEmpty()){
    		%>
				<div class="marginleft20">
				<h4><%=a+1 %>.<%=cnt %>)&nbsp;<%=hmLevel.get((String)alLevelScore.get(i))%></h4>
				<div><%=uF.showData((String)hmLevel.get((String)alLevelScore.get(i)+"_SD"), "")%></div>
				<div><%=uF.showData((String)hmLevel.get((String)alLevelScore.get(i)+"_LD"), "")%></div>
				<table class="table table-bordered">
				    <tr>
				        <!-- <th>Score Card</th> -->
				        <th>Competencies</th>
				        <th>Question</th>
				        <%for(int r=0; alRoles!=null && r<alRoles.size(); r++){ %>
				        <th class="alignRight">Marks <br>Role: <%=hmorientationMembers.get((String)alRoles.get(r))%></th>
				        <%} %>
				        <th class="alignRight">Weightage %</th>
				    </tr>
			    <%
			        for(int s=0; alScore!=null && s<alScore.size(); s++){ 
			        List alQuestions = (List)hmScoreQuestionsMap.get(alScore.get(s));
			        
			        for(int q=0; alQuestions!=null && q<alQuestions.size(); q++){
			        	List alOptions = (List)hmOptions.get((String)alQuestions.get(q));
			        %>
						    <tr>
						        <td width="20%" valign="top"><%=hmScoreCard.get((String)alScore.get(s))%></td>
						        <td width="60%" valign="top">
						            <strong><%=q+1%>)&nbsp;<%=hmQuestions.get((String)alQuestions.get(q))%></strong>
						            <div style="margin-left:20px;font-size:12px">
						                <%if(alOptions!=null){
						                    int nOptionType= uF.parseToInt((String)alOptions.get(4));
						                    
						                    switch(nOptionType){
						                    
						                    case 1:
						                    	%>
								                <p>a) <%=(String)alOptions.get(0)%></p>
								                <p>b) <%=(String)alOptions.get(1)%></p>
								                <p>c) <%=(String)alOptions.get(2)%></p>
								                <p>d) <%=(String)alOptions.get(3)%></p>
						                <%	
						                    break;
						                    
						                    case 2:
						                    %>
								                <p>a) <%=(String)alOptions.get(0)%></p>
								                <p>b) <%=(String)alOptions.get(1)%></p>
								                <p>c) <%=(String)alOptions.get(2)%></p>
								                <p>d) <%=(String)alOptions.get(3)%></p>
						                <%	
						                    break;
						                    
						                    case 3:
						                    break;
						                    
						                    case 4:
						                    break;
						                    
						                    case 5:
						                    %>
								                <p>a) <%=(String)alOptions.get(0)%></p>
								                <p>b) <%=(String)alOptions.get(1)%></p>
						                <%	
						                    break;
						                    
						                    case 6:
						                    %>
								                <p>a) <%=(String)alOptions.get(0)%></p>
								                <p>b) <%=(String)alOptions.get(1)%></p>
						                <%
						                    break;
                    
					                    case 7:
					                    break;
					                    
					                    case 8:
					                    %>
							                <p>a) <%=(String)alOptions.get(0)%></p>
							                <p>b) <%=(String)alOptions.get(1)%></p>
							                <p>c) <%=(String)alOptions.get(2)%></p>
							                <p>d) <%=(String)alOptions.get(3)%></p>
					                <%
					                    break;
					                    
					                    case 9:
					                    %>
							                <p>a) <%=(String)alOptions.get(0)%></p>
							                <p>b) <%=(String)alOptions.get(1)%></p>
							                <p>c) <%=(String)alOptions.get(2)%></p>
							                <p>d) <%=(String)alOptions.get(3)%></p>
					                <%
					                    break;
					                    
					                    case 10:
					                    break;
					                    
					                    case 11:
					                    break;
					                    case 12:
					                    break;
					                    }
					                    %>    		
					            </div>
					            <%}%>
				            <div>
				                <%for(int r=0; alRoles!=null && r<alRoles.size(); r++){ %>
				                <div>
				                    <%if(r==0){ %>
				                    <p style="font-weight:bold;margin: 0px !important;">Answer/Comments:</p>
				                    <%} %>
				                    <%if(uF.parseToInt(alRoles.get(0).toString())==4){
				                        String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m));
				                        %>		
				                    <div style="margin-left:20px;font-size:12px;">
				                        <%
				                            out.println(uF.showData(queAns, ""));
				                            %>
				                    </div>
				                    <%}else{ %>
				                    <div style="margin-left:20px;font-size:12px; ">
				                        <%if(hmQuestionAnswer.containsKey(alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m))){
				                            String strAns = ((String)hmQuestionAnswer.get(alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m)));
				                            	if(strAns!=null){
				                            		strAns = strAns.replace(":_:", "<br/>");
				                            		out.println(uF.showData(strAns, ""));
				                            	}
				                            }
				                            
				                            %>
				                    </div>
				                    <%} %>
				                </div>
				                <%} %>
				            </div>
       					 </td>
				        <%for(int rr=0; alRoles!=null && rr<alRoles.size(); rr++){   %>
					        <%if(uF.parseToInt(alRoles.get(rr).toString())==4){ 
					            List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q));
					            if(innList==null)innList=new ArrayList<String>();
					            //List<String> innAnsList = hmOuterpeerAnsDetails.get(alQuestions.get(q));
					            if(innList !=null && !innList.isEmpty()){
					            %>
							        <td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(0), "Not Rated") %></td>
						       		<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(1), "") %></td>
						       <%}else { %>
						       		<td width="10%" valign="top" align="right" style="padding-right:10px">Not Rated</td>
						        	<td width="10%" valign="top" align="right" style="padding-right:10px"></td>
						        <%
						         }
						       }else{ %>
						        	<td width="10%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)hmQuestionMarks.get((String)alQuestions.get(q)+"_"+alRoles.get(rr)+"_"+rolesUserIds.get(m)), "Not Rated") %></td>
						        	<td width="10%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
						     <%}
						     } 
						    %>
				   		 </tr>
    				 <%} 
       				 }
        		  %>
		   </table>
       </div>
    <%}
   }
%>

<%
    List<String> alLevelOther = hmSubsectionIds.get(sectionIdsList.get(a)+"OTHR"); 
    for(int i=0; alLevelOther != null && i<alLevelOther.size(); i++){
    	cnt++;
   		List alQuestions = (List)hmOtherQuestionsMap.get((String)alLevelOther.get(i));
    %>
		  <div class="marginleft20">
			<h4><%=a+1 %>.<%=cnt %>)&nbsp;<%=hmLevel.get((String)alLevelOther.get(i))%></h4>
			<div style="text-shadow: 0px 0px 2px #ccc"><%=uF.showData((String)hmLevel.get((String)alLevelOther.get(i)+"_SD"), "")%></div>
			<div style="text-shadow: 0px 0px 2px #ccc"><%=uF.showData((String)hmLevel.get((String)alLevelOther.get(i)+"_LD"), "")%></div>
			<table class="table table-bordered">
			    <tr>
			        <th>Question</th>
			        <%for(int r=0; alRoles!=null && r<alRoles.size(); r++){ %>
			        <th class="alignRight">Marks <br>Role: <%=hmorientationMembers.get((String)alRoles.get(r))%></th>
			        <%} %>
			        <th class="alignRight">Weightage %</th>
			    </tr>
			    <%for(int q=0; alQuestions!=null && q<alQuestions.size(); q++){ %>
			    <tr>
			        <td width="60%">
			            <strong><%=q+1%>)&nbsp;<%=hmQuestions.get((String)alQuestions.get(q))%></strong>
			            <div>
			                <%for(int r=0; alRoles!=null && r<alRoles.size(); r++){ %>
			                <div>
			                    <%if(r==0){ %>
			                    <p style="font-weight:bold;margin: 0px !important;">Answer/Comments:</p>
			                    <%} %>
			                    <%if(uF.parseToInt(alRoles.get(0).toString())==4){
			                        String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m));
			                        %>		
			                    <div style="margin-left:20px;font-size:12px;">
			                        <%
			                            out.println(uF.showData(queAns, ""));
			                            %>
			                    </div>
			                    <%}else{ %>
			                    <div style="margin-left:20px;font-size:12px;">
			                        <%if(hmQuestionAnswer!= null && hmQuestionAnswer.containsKey(alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m))){
			                            String strAns = ((String)hmQuestionAnswer.get(alQuestions.get(q)+"_"+alRoles.get(r)+"_"+rolesUserIds.get(m)));
			                            	if(strAns!=null){
			                            		strAns = strAns.replace(":_:", "<br/>");
			                            		out.println(uF.showData(strAns, ""));
			                            	}
			                            }
			                            
			                            %>
			                    </div>
			                    <%} %>
			                </div>
			                <%} %>
			            </div>
			        </td>
			        <%  for(int ii=0; alRoles!=null && ii<alRoles.size(); ii++) {%>
			       		<%if(uF.parseToInt(alRoles.get(0).toString())==4){ 
			           		List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q));
			            	if(innList==null)innList=new ArrayList<String>();
			           		//List<String> innAnsList = hmOuterpeerAnsDetails.get(alQuestions.get(q));
			            	if(innList !=null && !innList.isEmpty()) {
			            %>
			      			  <td width="20%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(0), "Not Rated") %></td>
			        		  <td width="20%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)innList.get(1), "") %></td>
			       		   <%} else{ %>
			        		  <td width="20%" valign="top" align="right" style="padding-right:10px">Not Rated</td>
			                  <td width="20%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
			             <%  }
			              } else { %>
			       			  <td width="20%" valign="top" align="right" style="padding-right:10px"><%=uF.showData((String)hmQuestionMarks.get((String)alQuestions.get(q)+"_"+alRoles.get(ii)+"_"+rolesUserIds.get(m)), "Not Rated") %></td>
			                  <td width="20%" valign="top" align="right" style="padding-right:10px"><%=hmQuestionWeightage.get((String)alQuestions.get(q)) %></td>
			           <% } 
			       	  } %>
			     <% } %>
			  </table>
		  </div>
       <%
    	}
      }
    }
  }
    %>
<div style="padding:10px">
    <h4><i class="fa fa-comments" aria-hidden="true"></i>Appraiser Comments</h4>
    <p><%=uF.showData((String)request.getAttribute("strFinalComments"), "Not Commented yet")%></p>
    <p style="width:97%;text-align:right">Appraised by - <%=uF.showData((String)request.getAttribute("strAppraisedBy"), "")%></p>
    <p style="width:97%;text-align:right">on <%=uF.showData((String)request.getAttribute("strAppraisedOn"), "")%></p>
</div>
</div>