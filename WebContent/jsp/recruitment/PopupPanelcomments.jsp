<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@taglib uri="/struts-tags" prefix="s"%>


<%
	UtilityFunctions uF = new UtilityFunctions();
	List<List<String>> empCommentList = (List<List<String>>) request.getAttribute("empCommentList");
%>
	

<div id="popupAjaxLoad">
<% 
	int cnt = 0;
	for(int i=0; empCommentList != null && !empCommentList.isEmpty() && i<empCommentList.size(); i++) { 
		List<String> innerList = empCommentList.get(i);
		if(innerList.get(2) != null) {
			cnt++;
		}
%>
<script type="text/javascript">
	$(function() {
		$('#starPrimary<%=i %>').raty({
			readOnly : true,
			start :<%=uF.parseToDouble(innerList.get(3))%>,
			half : true,
			targetType : 'number'
		});

	});
	
</script>
<div style="float: left; margin: 7px;">
	<div style="float: left; width: 100%;">
		<div style="float: left; margin-right: 10px; font-weight: bold;"> <%=innerList.get(1) %>: </div> 
		<div style="float: left; line-height: 15px;" id="starPrimary<%=i %>"></div> 
	</div>
	<div style="float: left; width: 100%;"> <%=innerList.get(2) %></div>
</div>
<% } 
	if(empCommentList == null || empCommentList.isEmpty()) {
%>
	<div class="nodata msg"> <span>Interview yet to be taken</span> </div>
<% } %>

</div>

