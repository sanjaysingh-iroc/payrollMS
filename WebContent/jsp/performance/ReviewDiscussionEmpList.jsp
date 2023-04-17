<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String)request.getParameter("dataType");
	String currUserType = (String)request.getParameter("currUserType");
	String strEmpId = (String)request.getAttribute("strEmpId");
	//System.out.println(strEmpId);
	Map<String,String> hmEmpNames = (Map<String,String>) request.getAttribute("hmEmpNames");
	Map<String,String> empImageMap = (Map<String,String>) request.getAttribute("empImageMap");
	if(hmEmpNames == null) hmEmpNames = new HashMap<String,String>(); 
	if(empImageMap == null) empImageMap = new HashMap<String,String>(); 
	Map<String, String> hmEmpwiseAppCnt = (Map<String,String>) request.getAttribute("hmEmpwiseAppCnt");
	if(hmEmpwiseAppCnt == null) hmEmpwiseAppCnt = new HashMap<String,String>();
	
	String proCount = (String) request.getAttribute("proCount");
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
  
%>

<ul class="products-list product-list-in-box">
	<%if(hmEmpNames != null && hmEmpNames.size()>0) { 
			Iterator<String> it = hmEmpNames.keySet().iterator();
			while(it.hasNext()) {
				String empId = it.next();
				String strImage = uF.showData(empImageMap.get(empId),"");
				int goalSize = 0;
				int appSize = 0;
				appSize = uF.parseToInt(hmEmpwiseAppCnt.get(empId));
		%>
		<li class="item">
			<span style="float: left; width: 100%;"> <%if(uF.parseToInt(empId) == uF.parseToInt(strEmpId)) { %>
				<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png"
					data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId.trim()+"/"+IConstants.I_22x22+"/"+strImage%>" />
				<a href="javascript:void(0);" class="activelink"
					onclick="getOneOneDiscussionDetails('OneOneReviewDiscussionDetails','<%=empId %>','<%=dataType %>','<%=currUserType %>','OOD')"> <%=hmEmpNames.get(empId) %></a> 
				<span style="float: right; margin-right: 5px;" class="label label-primary"><%=appSize %></span>
				<% } else { %> 
					<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png"
						data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId.trim()+"/"+IConstants.I_22x22+"/"+strImage%>" />
					<a href="javascript:void(0);" onclick="getOneOneDiscussionDetails('OneOneReviewDiscussionDetails','<%=empId %>','<%=dataType %>','<%=currUserType %>','OOD')"> <%=hmEmpNames.get(empId)%></a> 
					<span style="float: right; margin-right: 5px;" class="label label-primary"><%=appSize %></span>
				<% } %> 
			</span>
		</li>
	<% } %>

	<%if (hmEmpNames.size() > 0) { %>
		<div style="text-align: center;">
		<%
			int intproCnt = uF.parseToInt(proCount);
			int pageCnt = 0;
			int minLimit = 0;
			for (int i = 1; i <= intproCnt; i++) {
				minLimit = pageCnt * 15;
				pageCnt++;
		
				if (i == 1) {
					String strPgCnt = (String) request.getAttribute("proPage");
					String strMinLimit = (String) request.getAttribute("minLimit");
							
					if (uF.parseToInt(strPgCnt) > 1) {
						strPgCnt = (uF.parseToInt(strPgCnt) - 1) + "";
						strMinLimit = (uF.parseToInt(strMinLimit) - 15)+ "";
					}
						
					if (strMinLimit == null) {
						strMinLimit = "0";
					}
							
					if (strPgCnt == null) {
						strPgCnt = "1";
					}
				%>
					<span style="color: lightgray;"> 
					<% if (uF.parseToInt((String) request.getAttribute("proPage")) > 1) { %>
						<a href="javascript:void(0);"
							onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');">
							<%="< Prev"%></a> 
					<%  } else { %> 
						<b><%="< Prev"%></b> 
					<% }%> 
					</span> 
					<span>
						<a href="javascript:void(0);"
									onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
						<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
								style="color: black;" 
						<%}%>><%=pageCnt%></a> 
					</span>

					<% 	if ((uF.parseToInt((String) request.getAttribute("proPage")) - 3) > 1) {%>
					<b>...</b>
					<%	} %>
				<%	} %>

			<%if (i > 1 && i < intproCnt) { %>
				<% if (pageCnt >= (uF.parseToInt((String) request.getAttribute("proPage")) - 2) && pageCnt <= (uF.parseToInt((String) request.getAttribute("proPage")) + 2)) {%>
					<span>
						<a href="javascript:void(0);"
							onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
						<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
							style="color: black;" 
						<%}%>><%=pageCnt%></a> 
					</span>
				<% 	} %>
			<% } %>
	
			<%if (i == intproCnt && intproCnt > 1) {
				String strPgCnt = (String) request.getAttribute("proPage");
				String strMinLimit = (String) request.getAttribute("minLimit");
				strPgCnt = (uF.parseToInt(strPgCnt) + 1) + "";
				strMinLimit = (uF.parseToInt(strMinLimit) + 15) + "";
				if (strMinLimit == null) {
					strMinLimit = "0";
				}
				if (strPgCnt == null) {
					strPgCnt = "1";
				}
							
				if ((uF.parseToInt((String) request.getAttribute("proPage")) + 3) < intproCnt) {%>
					<b>...</b>
				<% } %>
	
				<span> <a href="javascript:void(0);"
							onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
					<%if (uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
						style="color: black;" <%}%>><%=pageCnt%></a> </span> <span
						style="color: lightgray;"> <%if (uF.parseToInt((String) request.getAttribute("proPage")) < pageCnt) {%>
						<a href="javascript:void(0);"
						onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"><%="Next >"%></a>
					<%} else { %> <b><%="Next >"%></b> <%}%> 
				</span>
			<% } %>
		<% } %>
	</div>
	<% } %>
	</div>

	<%  } else { %>
	<div class="nodata msg">No Employees.</div>
	<% } %>
</ul>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getOneOneDiscussionDetails('OneOneReviewDiscussionDetails','<%=strEmpId%>','<%=dataType%>','<%=currUserType%>','OOD');
		 $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	
		$(window).bind("load", function() {
		    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
		}); 
	});
	
	function getOneOneDiscussionDetails(strAction,empId,dataType,currUserType,fromPage){
		
		$("#reviewDiscussionDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		var form_data = $("#"+this.id).serialize();
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?strEmpId='+empId+'&dataType='+dataType+'&currUserType='+currUserType+'&fromPage='+fromPage,
			data: form_data,
			cache: true,
			success: function(result){
				//alert("result2==>"+result);
				$("#reviewDiscussionDetails").html(result);
	   		}
		});
	}
	
	function loadMore(proPage, minLimit,dataType,currUserType) {
		
		//alert("loadMore ==>dataType==>"+dataType+"==>proPage==>"+proPage+"==>minLimit==>"+minLimit);
		$("#reviewDiscussionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: 'ReviewDiscussionEmpList.action?dataType='+dataType+'&currUserType='+currUserType+"&proPage="+proPage+"&minLimit="+minLimit,
			cache: true,
			success: function(result){
				//alert("result1==>"+result);
				$("#reviewDiscussionResult").html(result);
	   		}
		});
	
	}
</script>