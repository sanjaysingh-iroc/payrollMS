<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String)request.getParameter("dataType");
	String strRecruitId = (String)request.getAttribute("recruitId");
	String fromPage = (String)request.getAttribute("fromPage");

	Map<String,List<String>> hmJobNames = (Map<String,List<String>>) request.getAttribute("hmJobNames");
	if(hmJobNames == null) hmJobNames = new HashMap<String,List<String>>(); 
	String callFrom = (String) request.getAttribute("callFrom");
	
	String proCount = (String) request.getAttribute("proCount");
%>	
	<ul class="products-list product-list-in-box">
		<%if(hmJobNames != null && hmJobNames.size()>0) { 
 			Iterator<String> it = hmJobNames.keySet().iterator();
	 			while(it.hasNext()) {
	 				String recruitId = it.next();
	 				List<String> innerList = hmJobNames.get(recruitId);
	 				
	 		%>
				 		<li class="item">
				 			<span style="float: left; width: 100%;">
				 				<%if(uF.parseToInt(recruitId) == uF.parseToInt(strRecruitId)) { %>
				 				  			 				   
						 				<a href="javascript:void(0);" class="activelink" onclick ="getJobDetails('ReportJobProfilePopUp', '<%=recruitId %>', '<%=fromPage %>')" >
						 				<%=uF.showData(innerList.get(1), "") %></a>
						 			
						 		<%} else { %>
						 			 	<a href="javascript:void(0);"  onclick ="getJobDetails('ReportJobProfilePopUp', '<%=recruitId %>', '<%=fromPage %>')" >
					 					<%=uF.showData(innerList.get(1), "") %></a>
						 			
						 		<%} %>
						 		<br/>
						 		<%=uF.showData(innerList.get(0), "") %>
						   	</span>	
				 	    </li>
			 	   
			 	 <%} %>
		 	    <%if (hmJobNames.size() > 0) { %>
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
									<span style="color: lightgray;"> 
										<%if (uF.parseToInt((String) request.getAttribute("proPage")) > 1) {%>
											<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>');"> <%="< Prev"%></a>
										<% } else {%>
											 <b><%="< Prev"%></b>
									    <% }%> 
								 	</span> 
									<span>
										<a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>');"
										<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
											style="color: black;" <%}%>><%=pageCnt%></a>
									</span>
			
								   <%  if ((uF.parseToInt((String) request.getAttribute("proPage")) - 3) > 1) {%>
										 <b>...</b>
								    <% } %>
							  <%  } %>
		
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
			 <%  } else { %>
  					<div class="nodata msg">No Jobs found.</div>
 			 <% } %>
		 	
	 	</ul>
 	
 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		<%-- <%if(callFrom != null && callFrom.equalsIgnoreCase("IND")) {%>
		   
			getJobProfileDataPage('Induction', '', 'On-boards','<%=fromPage %>', '<%=strRecruitId %>');
		<%} else {%> --%>
			getJobDetails('ReportJobProfilePopUp', '<%=strRecruitId %>','<%=fromPage%>');
		<%--}--%>
	});

function getJobDetails(strAction, recruitId,fromPage) {
	//console.log("getJobDetails");
	document.getElementById("recruitId").value = recruitId;
	var form_data = $("#"+this.id).serialize();
	$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?view=jobreport&recruitId='+recruitId+'&fromPage='+fromPage,
		data: form_data,
		success: function(result){
			$("#subSubDivResult").html(result);
   		}
	});
}



function loadMore(proPage, minLimit, dataType) {
		//alert("loadMore ==>dataType==>"+dataType+"==>proPage==>"+proPage+"==>minLimit==>"+minLimit);
		var f_org = document.getElementById("f_org").value;
		var location = document.getElementById("location").value;
		var designation = document.getElementById("designation").value;
		var appliSourceType = document.getElementById("appliSourceType").value;
		var appliSourceName = '';
		if(document.getElementById("appliSourceName")) {
			appliSourceName = document.getElementById("appliSourceName").value;
		}
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: 'JobNamesList.action?dataType='+dataType+"&proPage="+proPage+"&minLimit="+minLimit+"&f_org="+f_org+"&location="+location
			+"&designation="+designation+"&appliSourceType="+appliSourceType+"&appliSourceName="+appliSourceName,
			success: function(result){
				//alert("result1==>"+result);
				$("#subDivResult").html(result);
				
	   		}
		});
	
}
</script> 

	 	