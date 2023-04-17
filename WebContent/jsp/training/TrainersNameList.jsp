<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%

	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
	String strTrainerId = (String) request.getAttribute("strTrainerId");
	String strEmpId = (String) request.getAttribute("strEmpId");
	String trainerEmpId = (String) request.getAttribute("trainerEmpId");
	Map<String,List<String>> hmTrainers=(Map<String,List<String>>)request.getAttribute("hmTrainers");
	if(hmTrainers == null) hmTrainers = new HashMap<String,List<String>>();
	String proCount = (String) request.getAttribute("proCount");
  //System.out.println("strTrainerId==>"+strTrainerId+"==>strEmpId==>"+strEmpId+"==>trainerEmpId==>"+trainerEmpId);
%>	
		<ul class="products-list product-list-in-box">
		 	<%if(hmTrainers != null && hmTrainers.size()>0) { 
					Iterator<String> it = hmTrainers.keySet().iterator();
					while(it.hasNext()) {
						String trainerId = it.next();
		 				List<String> alInner = hmTrainers.get(trainerId);
		 				if(alInner ==  null) alInner = new ArrayList<String>();
		 				if(alInner != null && !alInner.isEmpty() && alInner.size() >0) {
	 		%>
						 	   <li class="item">
						 		 <span style="float: left; width: 100%;">
						 		      <%=alInner.get(5) %>
						 		     <div style="float:left;">
				 					<%if((uF.parseToInt(alInner.get(0)) == uF.parseToInt(strTrainerId))) { %>
						 				<%if(alInner.get(2)==null || alInner.get(2).equals("null")){ %>
											<a href="javascript:void(0);" class="activelink" onclick ="getTrainerDetails('TrainerMyProfile','<%=alInner.get(0) %>','<%=alInner.get(3) %>','LD')" >
						 					<%=alInner.get(1) %></a><br/><%=alInner.get(4) %>
										<%}else{ %>
											<a href="javascript:void(0);" class="activelink" onclick ="getEmpDetails('MyProfile','<%=alInner.get(2) %>','LD')" >
						 					<%=alInner.get(1) %></a><br/><%=alInner.get(4) %>
											
										<%} %>
						 				
						 			<%} else { %>
						 					<%if(alInner.get(2)==null || alInner.get(2).equals("null")){ %>
											<a href="javascript:void(0);" onclick ="getTrainerDetails('TrainerMyProfile','<%=alInner.get(0) %>','<%=alInner.get(3) %>','LD')" >
						 					<%=alInner.get(1) %></a><br/><%=alInner.get(4) %>
										<%}else{ %>
											<a href="javascript:void(0);" onclick ="getEmpDetails('MyProfile','<%=alInner.get(2) %>','LD')" >
						 					<%=alInner.get(1) %></a><br/><%=alInner.get(4) %>
										<%} %>
						 			<%} %>
						 			</div>
						   		 </span>	
						 	   </li>
					 	   <%} %>
					 	<%} %>
						 	  		
			 	  		<%if (hmTrainers.size() > 0) { %>
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
				<% } %>
				<%} else { %>
					<div class="nodata msg">No trainer.</div>
			   <% } %>
	 	 </ul>
	 	
	 	
 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		var strEmp = '<%=strEmpId%>';
		
		if(strEmp == null || strEmp == "" || strEmp.trim()=="null" || strEmp.trim() == "NULL" ){
			getTrainerDetails('TrainerMyProfile','<%=strTrainerId%>','<%=trainerEmpId%>','LD');
		} else {
			getEmpDetails('MyProfile','<%=strEmpId%>','LD');
		}
		
	});

function getEmpDetails(strAction,empId,fromPage) {
	//alert("getEmpDetails ==>empId==>"+empId);
	$("#subDivTrainerResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var form_data = $("#"+this.id).serialize();
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?empId='+empId+'&fromPage='+fromPage+'&proPopup=proPopup',
		data: form_data,
		cache: true,
		success: function(result){
		//	alert("result4==>"+result);
			$("#subDivTrainerResult").html(result);
   		}
	});
}

function getTrainerDetails(strAction,trainerId,empId,fromPage) {
	//alert("getTrainerDetails trainerId==>"+trainerId+"==>empId==>"+empId);
	$("#subDivTrainerResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var form_data = $("#"+this.id).serialize();
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?empId='+empId+'&proPopup=proPopup&trainerId='+trainerId+'&fromPage='+fromPage,
		data: form_data,
		cache: true,
		success: function(result){
			//alert("result2==>"+result);
			$("#subDivTrainerResult").html(result);
   		}
	});
}

function loadMore(proPage, minLimit) {
	$("#divTrainerResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'TrainersNameList.action?proPage='+proPage+'&minLimit='+minLimit,
		cache: true,
		success: function(result){
			//alert("result3==>"+result);
			$("#divTrainerResult").html(result);
   		}
	});
}
</script> 