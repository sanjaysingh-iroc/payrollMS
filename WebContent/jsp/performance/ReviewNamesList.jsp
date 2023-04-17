<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String)request.getParameter("dataType");
	String currUserType = (String)request.getParameter("currUserType");
	String strReviewId = (String)request.getAttribute("strReviewId");
	String appFreqId = (String)request.getAttribute("appFreqId");
	
	Map<String,List<String>> hmReviewNames = (Map<String,List<String>>) request.getAttribute("hmReviewNames");
	if(hmReviewNames == null) hmReviewNames = new HashMap<String,List<String>>(); 
	Map<String,List<String>> hmSelfReviewNames = (Map<String,List<String>>) request.getAttribute("hmSelfReviewNames");
	if(hmSelfReviewNames == null) hmSelfReviewNames = new HashMap<String,List<String>>(); 
	String proCount = (String) request.getAttribute("proCount");
	String callFrom = (String) request.getAttribute("callFrom");
	String alertID = (String) request.getAttribute("alertID");
%>

		<ul class="products-list product-list-in-box">
		 	<%if(dataType != null && dataType.equalsIgnoreCase("SRR")) { %>	
		 		<%if(hmSelfReviewNames != null && hmSelfReviewNames.size()>0) { 
		 			Iterator<String> it = hmSelfReviewNames.keySet().iterator();
		 			while(it.hasNext()) {
		 				String appDetailId = it.next();
		 			//	System.out.println("appDetailId==>"+appDetailId);
		 				List<String> alInner1 = hmSelfReviewNames.get(appDetailId);
		 				if(alInner1 ==  null) alInner1 = new ArrayList<String>();
		 				if(alInner1 != null && alInner1.size() >0 && !alInner1.isEmpty() ) {
		 		%>	
					 		<li class="item">
					 			<span style="float: left; width: 100%;">
					 				<%if(uF.parseToInt(alInner1.get(0)) == uF.parseToInt(strReviewId)) { %>
							 			<a href="javascript:void(0);" class="activelink" onclick="getSelfReviewDetails('SelfReviewRequests','<%=alInner1.get(0)%>','<%=currUserType%>','<%=alInner1.get(1)%>')">
							 			<%=alInner1.get(2)%></a>
							 		<%} else { %>
							 			<a href="javascript:void(0);" onclick="getSelfReviewDetails('SelfReviewRequests','<%=alInner1.get(0)%>','<%=currUserType%>','<%=alInner1.get(1)%>')">
							 			<%=alInner1.get(2)%></a>
							 		<%} %>
							 		<br/>
						 			<%=uF.showData(alInner1.get(3), "") %>, &nbsp;<%=uF.showData(alInner1.get(4), "") %>
							   </span>					 			
					 	    </li>
		 	    		<%} 
		 			  } %>
		 	 	 		 			
		 	      <% }  else { %>
  						<div class="nodata msg">No self review requests.</div>
  				  <% } %>
		 	     
		 	<% } else { %>
		 		
		 		<%if(hmReviewNames != null && hmReviewNames.size()>0) { 
		 			Iterator<String> it = hmReviewNames.keySet().iterator();
			 			while(it.hasNext()) {
			 				String appDetailId = it.next();
			 				List<String> alInner = hmReviewNames.get(appDetailId);
			 				if(alInner ==  null) alInner = new ArrayList<String>();
			 				if(alInner != null && !alInner.isEmpty() && alInner.size() >0) {
			 		%>
					 			<li class="item">
					 				<span style="float: left; width: 100%;">
					 					<%if(uF.parseToInt(alInner.get(0)) == uF.parseToInt(strReviewId)) { %>
							 				<a href="javascript:void(0);" class="activelink" onclick ="getReviewDetails('ReviewDetails','<%=alInner.get(0) %>','<%=alInner.get(1) %>')" >
							 				<%=alInner.get(2) %></a>
							 			<%} else { %>
							 				<a href="javascript:void(0);"  onclick ="getReviewDetails('ReviewDetails','<%=alInner.get(0) %>','<%=alInner.get(1) %>')" >
							 				<%=alInner.get(2) %></a>
							 			<%} %>
							 			<br/>
						 			<%=uF.showData(alInner.get(3), "") %>, &nbsp;<%=uF.showData(alInner.get(4), "") %>
							   		</span>	
					 	    	</li>
				 	    	<%} %>
				 	    <%} %>
				 	    <%if (hmReviewNames.size() >0) { %>
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
										<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"> <%="< Prev"%></a>
									<%
									} else {%>
										 <b><%="< Prev"%></b>
								 <% }%> 
							  </span> 
							 <span>
								 <a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
								<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
									style="color: black;" <%}%>><%=pageCnt%></a>
							</span>

						<% 	if ((uF.parseToInt((String) request.getAttribute("proPage")) - 3) > 1) {%>
								<b>...</b>
						<%	} %>
					<%	} %>

					<%if (i > 1 && i < intproCnt) { %>
						<% if (pageCnt >= (uF.parseToInt((String) request.getAttribute("proPage")) - 2) && pageCnt <= (uF.parseToInt((String) request.getAttribute("proPage")) + 2)) {%>
							<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
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
							<a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
							<%if (uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
									style="color: black;" <%}%>><%=pageCnt%></a>
						</span> 
						<span style="color: lightgray;"> 
							<%if (uF.parseToInt((String) request.getAttribute("proPage")) < pageCnt) {%>
								<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"><%="Next >"%></a>
							<%} else { %> 
								<b><%="Next >"%></b> 
							<%}%> 
						</span>
						<%}%>
						<%}%>
					</div>
					<% } %>
				</div>
				 	    
		 	 <%  } else { %>
  					<div class="nodata msg">No reviews.</div>
 			 <% } %>
 			
		 	<% } %>
	 	 </ul>
	 	
	 	
 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		var data = '<%=dataType%>';
		if(data != "" && data != "SRR") {
			getReviewDetails('ReviewDetails','<%=strReviewId%>','<%=appFreqId %>');
		} else {
			getSelfReviewDetails('SelfReviewRequests','<%=strReviewId%>','<%=currUserType%>','<%=appFreqId %>');
		}
	});

function getReviewDetails(strAction,appId,appFreqId,dataType){
	//alert("ReviewNameList jsp strAction ===>> " + strAction+"==appId==>"+appId+"==>appFreqId==>"+appFreqId);
	$("#reviewDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var form_data = $("#"+this.id).serialize();
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+"&fromPage=AD",
		data: form_data,
		cache: true,
		success: function(result){
			//alert("result2==>"+result);
			$("#reviewDetails").html(result);
   		}
	});
}


function getSelfReviewDetails(strAction,appId,currUserType,appFreqId) {
	//alert("getSelfReviewDetails jsp strAction ===>> " + strAction+"==appId==>"+appId+"==>currUserType==>"+currUserType);
	$("#reviewDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var form_data = $("#"+this.id).serialize();
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?appId='+appId+'&currUserType='+currUserType+'&appFreqId='+appFreqId+'&alertID='+<%=alertID%>,
		data: form_data,
		cache: true,
		success: function(result){
			//alert("result2==>"+result);
			$("#reviewDetails").html(result);
   		}
	});
}

function loadMore(proPage, minLimit,dataType,currUserType) {
		//alert("loadMore ==>dataType==>"+dataType+"==>proPage==>"+proPage+"==>minLimit==>"+minLimit);
		$("#reviewResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: 'ReviewNamesList.action?dataType='+dataType+'&currUserType='+currUserType+"&proPage="+proPage+"&minLimit="+minLimit,
			cache: true,
			success: function(result){
				//alert("result1==>"+result);
				$("#reviewResult").html(result);
	   		}
		});
	
}
</script> 

	 	