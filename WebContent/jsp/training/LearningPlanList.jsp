<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String)request.getParameter("dataType");
	String strFirstPlanId = (String)request.getAttribute("strFirstPlanId");
	Map<String,List<String>> hmLearningPlanName = (Map<String,List<String>>) request.getAttribute("hmLearningPlanName");
	if(hmLearningPlanName == null) hmLearningPlanName = new HashMap<String,List<String>>(); 
	String proCount = (String) request.getAttribute("proCount");
%>	
		<ul class="products-list product-list-in-box">
	
		 		<%if(hmLearningPlanName != null && hmLearningPlanName.size()>0) { 
		 			Iterator<String> it = hmLearningPlanName.keySet().iterator();
			 			while(it.hasNext()) {
			 				String learningId = it.next();
			 				List<String> alInner =  hmLearningPlanName.get(learningId);
			 				if(alInner == null) alInner = new ArrayList<String>();
			 				if(alInner != null && alInner.size()>0 && !alInner.isEmpty()) {
			 		%>
					 			<li class="item">
					 				<span style="float: left; width: 100%;">
					 				    <%=alInner.get(3) %>
					 					<div style="float:left;">
							 				<%if(uF.parseToInt(alInner.get(0)) == uF.parseToInt(strFirstPlanId)) { %>
								 				<a href="javascript:void(0);" class="activelink" onclick ="getLearningPlanDetails('LearningPlanDetails','<%=alInner.get(0)%>')" >
								 				<%=alInner.get(1)%></a><br/>Aligned with-<%=alInner.get(2) %>
								 			<%} else { %>
								 				<a href="javascript:void(0);"  onclick ="getLearningPlanDetails('LearningPlanDetails','<%=alInner.get(0)%>')" >
								 				<%=alInner.get(1)%></a><br/>Aligned with-<%=alInner.get(2) %>
								 			<%} %>
									 	</div>
							   		</span>	
					 	    	</li>
				 	    	<%} %>
				 	    <%} %>
				 	    
				 	    <%if (hmLearningPlanName.size() >0) { %>
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
										<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>');"> <%="< Prev"%></a>
									<%
									} else {%>
										 <b><%="< Prev"%></b>
								 <% }%> 
							  </span> 
							 <span>
								 <a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>');"
								<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
									style="color: black;" <%}%>><%=pageCnt%></a>
							</span>

						<% 	if ((uF.parseToInt((String) request.getAttribute("proPage")) - 3) > 1) {%>
								<b>...</b>
						<%	} %>
					<%	} %>

					<%if (i > 1 && i < intproCnt) { %>
						<% if (pageCnt >= (uF.parseToInt((String) request.getAttribute("proPage")) - 2) && pageCnt <= (uF.parseToInt((String) request.getAttribute("proPage")) + 2)) {%>
							<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>');"
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
							<a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>');"
							<%if (uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
									style="color: black;" <%}%>><%=pageCnt%></a>
						</span> 
						<span style="color: lightgray;"> 
							<%if (uF.parseToInt((String) request.getAttribute("proPage")) < pageCnt) {%>
								<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>');"><%="Next >"%></a>
							<%} else { %> 
								<b><%="Next >"%></b> 
							<%}%> 
						</span>
						<%}%>
						<%}%>
					</div>
					<% } %>
				</div>
				 	 
				</div>
				 	    
		 	 <%  } else { %>
  					<div class="nodata msg">No learnings.</div>
 			 <% } %>
		 	 	</ul>
	 	
	 	
 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getLearningPlanDetails('LearningPlanDetails','<%=strFirstPlanId%>');
		
	});

function getLearningPlanDetails(strAction,learningPlanId){
	$("#subDivLPResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var form_data = $("#"+this.id).serialize();
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?learningPlanId='+learningPlanId,
		data: form_data,
		cache: true,
		success: function(result){
			//alert("result2==>"+result);
			$("#subDivLPResult").html(result);
   		}
	});
}

function loadMore(proPage, minLimit,dataType) {
	
	//alert("loadMore ==>dataType==>"+dataType+"==>proPage==>"+proPage+"==>minLimit==>"+minLimit);
	$("#divLPResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'LearningPlanList.action?dataType='+dataType+'&proPage='+proPage+'&minLimit='+minLimit,
		cache: true,
		success: function(result){
			//alert("result1==>"+result);
			$("#divLPResult").html(result);
   		}
	});

}

</script> 

	 	