<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
	
<script type="text/javascript">
	$(function() {
		jQuery("#frmCourseReadStatusUpdate").validationEngine();
	});

</script>

<div id="printDiv" class="leftbox reportWidth" >
		<%
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> chapterList = (List<List<String>>) request.getAttribute("chapterList");
		Map<String, List<List<String>>> hmSubchapterData = (Map<String, List<List<String>>>) request.getAttribute("hmSubchapterData");
		Map<String, List<List<String>>> hmAssessmentData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentData");
		Map<String, String> hmCourseReadStatus = (Map<String, String>) request.getAttribute("hmCourseReadStatus");
		int readChapterCount = (Integer)request.getAttribute("readChapterCount");
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		
		%>
 			<s:form theme="simple" action="CourseReadStatusUpdate" id="frmCourseReadStatusUpdate" name="frmCourseReadStatusUpdate" method="POST" cssClass="formcss">
				<s:hidden name="courseId"></s:hidden>
				<s:hidden name="lPlanId"></s:hidden>
					<table border="0" class="formcss" style="width: 85%;">
						<% 
						  if(chapterList != null && !chapterList.isEmpty()) {
							for(int i=0; i< chapterList.size(); i++){
								List<String> innerList = chapterList.get(i);
								if(innerList.get(1) != null && !innerList.get(1).equals("")) {
								
						%> 
						<tr>
							<td class="txtlbl" height="10px" align="right">
							<% if(hmCourseReadStatus.get(innerList.get(0)) != null && hmCourseReadStatus.get(innerList.get(0)).equals("1")) { %>
								<input type="checkbox" name="chaters" id="chaters<%=i %>" value="<%=innerList.get(0) %>" checked="checked" disabled="disabled"/>
							<% } else{ %>
							<%-- <input type="checkbox" name="chaters" id="chaters<%=i %>" value="<%=innerList.get(0) %>"/> --%>
								<%if(strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){ %>
									<input type="checkbox" name="chaters" id="chaters<%=i %>" value="<%=innerList.get(0) %>" disabled="disabled"/>
								<%} else{ %>
									<input type="checkbox" name="chaters" id="chaters<%=i %>" value="<%=innerList.get(0) %>"/>
								<%} %>
							<% } %>
							</td>
							<td class="txtlbl" style="width: 20px; height: 10px;" align="right"><%=i+1 %>)&nbsp;&nbsp;</td>
							<td class="txtlbl" height="10px" colspan="2"><%=innerList.get(1) %></td>
						</tr>
					
				<%  } }  
				if(chapterList.size() > readChapterCount) {
				//===condition added by parvez date: 15-02-2023===	
					if(strUserType != null && !strUserType.equalsIgnoreCase(IConstants.ADMIN) && !strUserType.equalsIgnoreCase(IConstants.HRMANAGER)){
				%>
						<tr>
							<td align="center" colspan="3">
								<s:submit cssClass="btn btn-primary" cssStyle="width:100px;" name="updateStatus" value="Update"/>
							</td>
						</tr>
				<% } } } %>	
					
				<% if(chapterList == null || chapterList.isEmpty()){ %>
						<tr>
							<td class="txtlbl" colspan="3"><b>No content added.</b></td>
						</tr>
				<% } %>
					</table>
			</s:form> 
	</div>


<script>

	$("#frmCourseReadStatusUpdate").submit(function(event){
		
		event.preventDefault();
		
		var form_data = $("#frmCourseReadStatusUpdate").serialize();
		
		//$("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
			type : 'POST',
			url: 'CourseReadStatusUpdate.action',
			data: form_data+"&updateStatus=Update",
			success: function(result) {
				//alert("result1==>"+result); 
				getMyHRData('MyLearningPlan','','','');
	 		},
			error: function(result){
				getMyHRData('MyLearningPlan','','','');
			}
		});
	});
	
</script>