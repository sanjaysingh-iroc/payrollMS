<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%

	UtilityFunctions uF = new UtilityFunctions();
	String trainingPlanId = (String)request.getAttribute("trainingPlanId");
	String fromPage = (String)request.getAttribute("fromPage");
	List<List<String>> alTrainingPlan = (List) request.getAttribute("alTrainingPlan");
	if(alTrainingPlan == null) alTrainingPlan = new ArrayList<List<String>>();
	String proCount = (String) request.getAttribute("proCount");
%>	
		<ul class="products-list product-list-in-box">
			<%if(alTrainingPlan != null && alTrainingPlan.size()>0) { %>
			  	<%for(int i =0;i<alTrainingPlan.size();i++) {
			  		List<String> alInner = alTrainingPlan.get(i);
			  		if(alInner != null && alInner.size()>0 && !alInner.isEmpty()){
			  	%>
			  			<li class="item">
							<span style="float: left; width: 100%;">
								<%=alInner.get(3) %>
					 			<div style="float:left;">
								<%if(uF.parseToInt(trainingPlanId) == uF.parseToInt(alInner.get(0))) { %>
									<a href="javascript:void(0);" class="activelink" onclick="getTrainingPlanDetails('TrainingPlanDetails','<%=alInner.get(0)%>','<%=fromPage%>')"><%=alInner.get(1)%></a>
									<br/><%=alInner.get(2)%>
								<%} else { %>
									<a href="javascript:void(0);" onclick="getTrainingPlanDetails('TrainingPlanDetails','<%=alInner.get(0)%>','<%=fromPage%>')"><%=alInner.get(1)%></a>
									<br/><%=alInner.get(2)%>
								<%} %>
								</div>
							</span>
						</li>
			  	<%  }
			  	}%>	
				  	
			  	<%if(alTrainingPlan.size() > 0){ %>
			  		<div style="text-align: center;clear: both;width: 100%;">
					<%
						int intproCnt = uF.parseToInt(proCount);
							int pageCnt = 0;
							int minLimit = 0;

							for (int i = 1; i <= intproCnt; i++) {
								minLimit = pageCnt * 15;
								pageCnt++;
					%>
							<%
								if (i == 1) {
									String strPgCnt = (String) request.getAttribute("proPage");
									String strMinLimit = (String) request.getAttribute("minLimit");
									if (uF.parseToInt(strPgCnt) > 1) {
										strPgCnt = (uF.parseToInt(strPgCnt) - 1) + "";
										strMinLimit = (uF.parseToInt(strMinLimit) - 15)
												+ "";
									}
									if (strMinLimit == null) {
										strMinLimit = "0";
									}
									if (strPgCnt == null) {
										strPgCnt = "1";
									}
							%>
									<span style="color: lightgray;"> <%
		 								if (uF.parseToInt((String) request.getAttribute("proPage")) > 1) {
							 %>
											<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>');"> <%="< Prev"%></a>
										<%
										} else {%>
											 <b><%="< Prev"%></b>
									 <% }%> 
								  </span> 
								 <span>
									 <a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>');"
									<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
										style="color: black;" <%}%>><%=pageCnt%></a>
								</span>

								<% 	if ((uF.parseToInt((String) request.getAttribute("proPage")) - 3) > 1) {%>
										<b>...</b>
								<%	} %>
							<%	} %>

							<%if (i > 1 && i < intproCnt) { %>
								<% if (pageCnt >= (uF.parseToInt((String) request.getAttribute("proPage")) - 2) && pageCnt <= (uF.parseToInt((String) request.getAttribute("proPage")) + 2)) {%>
									<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>');"
										<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
										style="color: black;" <%}%>><%=pageCnt%></a>
									</span>
								<% 	} %>
							<% } %>

							<%if (i == intproCnt && intproCnt > 1) {
									String strPgCnt = (String) request.getAttribute("proPage");
									String strMinLimit = (String) request.getAttribute("minLimit");
									strPgCnt = (uF.parseToInt(strPgCnt) + 1) + "";
									strMinLimit = (uF.parseToInt(strMinLimit) + 15)
											+ "";
									if (strMinLimit == null) {
										strMinLimit = "0";
									}
									if (strPgCnt == null) {
										strPgCnt = "1";
									}
							%>
							<%if ((uF.parseToInt((String) request.getAttribute("proPage")) + 3) < intproCnt) {%>
								<b>...</b>
							<% } %>
							<span>
								<a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>');"
								<%if (uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
										style="color: black;" <%}%>><%=pageCnt%></a>
							</span> 
							<span style="color: lightgray;"> 
								<%if (uF.parseToInt((String) request.getAttribute("proPage")) < pageCnt) {%>
									<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>');"><%="Next >"%></a>
								<%} else { %> 
									<b><%="Next >"%></b> 
								<%}%> 
							</span>
						<%}%>
					<%}%>
				</div>
		  	  	  <%} %>						  	
			<%}else { %>
 				<div class="nodata msg">No training plan.</div>
 	  	   <% } %> 
	 	 </ul>
 	
 <script type="text/javascript" charset="utf-8">
 $(document).ready(function(){
		getTrainingPlanDetails('TrainingPlanDetails','<%=trainingPlanId%>','LD');
	});
	
	function getTrainingPlanDetails(strAction,planId,fromPage) {
		
		$('#subDivTPResult').html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
		var form_data = $('#'+this.id).serialize();
		$.ajax({
			type : 'GET',
			url : strAction+'.action?planId='+planId+'&fromPage='+fromPage,
			data : form_data,
			success : function(result){
				//alert("result==>"+result);
				$('#subDivTPResult').html(result);
			}
			
		});
	}

function loadMore(proPage, minLimit) {
	$("#divTPNamesResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'TrainingPlansNameList.action?proPage='+proPage+'&minLimit='+minLimit,
		cache: true,
		success: function(result){
			//alert("result3==>"+result);
			$("#divTPNamesResult").html(result);
   		}
	});
}
</script> 