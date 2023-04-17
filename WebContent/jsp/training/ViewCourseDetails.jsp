<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
	
	<script src="scripts/ckeditor_cust/ckeditor.js"></script>
	<link rel="stylesheet" href="<%= request.getContextPath()%>/css/demos.css" media="screen" type="text/css">
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
	</style>	
	
	<script type="text/javascript">

/* $(document).ready(function() {
	jQuery("#frmAddNewCourse").validationEngine();
}); */

</script>


<div id="printDiv" class="leftbox reportWidth" >
		<%
		UtilityFunctions uF = new UtilityFunctions();
		String anstype = (String) request.getAttribute("anstype");
		List<List<String>> ansTypeList = (List<List<String>>) request.getAttribute("ansTypeList");
		String tab = (String)request.getAttribute("tab"); 
		String courseId = (String)request.getAttribute("courseId");
		String crsPreface = (String)request.getAttribute("crsPreface");
		List<List<String>> chapterList = (List<List<String>>) request.getAttribute("chapterList");
		Map<String, List<List<String>>> hmSubchapterData = (Map<String, List<List<String>>>) request.getAttribute("hmSubchapterData");
		Map<String, List<List<String>>> hmAssessmentData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentData");
		Map<String, String> hmAnstypeName = (Map<String, String>) request.getAttribute("hmAnstypeName");
		Map<String, List<List<String>>> hmContentData = (Map<String, List<List<String>>>) request.getAttribute("hmContentData");
		Map<String, String> hmContentImg = (Map<String, String>) request.getAttribute("hmContentImg");
		System.out.println("VCD.jsp/56 ===> ");
		%>
			
		
		<div style="width: 100%; background-color: #868686; box-shadow:3px 4px 12px #616161;height: 80%;">
		<div id="container" style="width: 99%; float: left;height:98%;"> 
		
 		<div style=" border: solid 0px #ff0000; float: left; width:96% " id="course">

				<div style="float: left; width: 100%;">
					<table border="0" class="formcss" style="width: 85%;">

						<tr>
							<td class="tdLabelheadingBg alignCenter" colspan="2">
							<span style="color: #68AC3B; font-size: 18px; padding: 5px;"></span> Course Information</td>
						</tr>
						
						<tr>
							<td class="txtlabel"
								style="vertical-align: top; text-align: right">Course Name :<sup>*</sup></td>
							<td> <%=request.getAttribute("courseName") %> </td>
						</tr>
						
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Subject :<sup>*</sup></td>
							<td> <%=request.getAttribute("subjectID") %> </td>
						</tr>

						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Author :<sup>*</sup></td>
							<td> <%=request.getAttribute("courseAuthor") %> </td>
						</tr>
						
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Version :<sup>*</sup></td>
							<td> <%=request.getAttribute("courseVersion") %> </td>
						</tr>
						
						<tr>
							<td class="txtlabel" style="vertical-align: top; text-align: right">Preface :</td>
							<td> <%=uF.showData(crsPreface, "") %> </td>
						</tr>
						
					</table>
				</div>
			<!-- </div> -->
  		</div>
  
		  <div style="border: solid 0px #ff0000; float: left; width:96% " id="index">

				<div style="float: left; width: 100%;">
					<table border="0" class="formcss" style="width: 85%;">

						<tr>
							<td class="tdLabelheadingBg alignCenter" colspan="2">
							<span style="color: #68AC3B; font-size: 18px; padding: 5px;"> </span> Index</td>
						</tr>
						<% 
						  if(chapterList != null && !chapterList.isEmpty()){
							for(int i=0; i< chapterList.size(); i++){
								List<String> innerList = chapterList.get(i);
						%> 
						<tr>
							<td class="txtlbl" height="10px" align="right"><%=i+1 %>)&nbsp;&nbsp;</td>
							<td class="txtlbl" height="10px" colspan="2"><%=innerList.get(1) %></td>
						</tr>
						<%
						if(hmSubchapterData != null && !hmSubchapterData.isEmpty()){
							List<List<String>> subchapterList = hmSubchapterData.get(innerList.get(0));
							
								for(int j=0; subchapterList != null && !subchapterList.isEmpty() && j< subchapterList.size(); j++){
									List<String> subinnerList = subchapterList.get(j);
					%>  
					<tr>
						<td class="txtlbl" height="10px">&nbsp;</td>
						<td class="txtlbl" height="10px" colspan="2"><%=i+1 %>.<%=j+1 %>)&nbsp;&nbsp;&nbsp; <%=subinnerList.get(1) %> </td>
					</tr>
							
					<% } } %> 
					
				<% } } %>
						
				<% if(hmSubchapterData == null || hmSubchapterData.isEmpty()){ %>
						<tr>
							<td class="txtlbl" colspan="3"><b>'No Content Added'</b></td>
						</tr>
				<% } %>
					</table>
				</div>

 		 </div>
  <% 
  String chapterCnt = (String) request.getAttribute("chapterCnt");
  if(chapterList != null && !chapterList.isEmpty()){
		for(int i=0; i< chapterList.size(); i++){
			List<String> innerList = chapterList.get(i);
			
	%>
						
  <div style="border: solid 0px #ff0000; float: left; width:96%" id="chapter<%=i+1 %>">
 	
 	<table border="0" class="formcss" style="width: 85%;">

		<tr>
			<td class="tdLabelheadingBg alignCenter" colspan="2">
			<span style="color: #68AC3B; font-size: 18px; padding: 5px;"> </span> Chapter Information</td>
		</tr>
		
		<tr>
			<td class="txtlabel" style="vertical-align: top; text-align: right">Chapter Name :<sup>*</sup></td>
			<td><%=uF.showData(innerList.get(1), "") %></td>
		</tr>
		
		<tr>
			<td class="txtlabel" style="vertical-align: top; text-align: right">Chapter Description :<sup>*</sup></td>
			<td><%=uF.showData(innerList.get(2), "") %></td>
		</tr>

	</table>
	<div id="subchapterDiv<%=i+1 %>">
	<% if(hmSubchapterData != null && !hmSubchapterData.isEmpty()){
		List<List<String>> subchapterList = hmSubchapterData.get(innerList.get(0));
		
			for(int j=0; subchapterList != null && !subchapterList.isEmpty() && j< subchapterList.size(); j++){
				List<String> subinnerList = subchapterList.get(j);
		%>
	
	
	<div id="subchapter<%=i+1 %>_<%=j+1 %>" style="float: left; width: 100%; margin-top: 25px;">
	<table border="0" class="formcss" style="width: 85%;">
		<tr>
			<td class="txtlabel" style="vertical-align: top; text-align: right">Subchapter Name :<sup>*</sup></td>
			<td><%=uF.showData(subinnerList.get(1), "") %></td>
		</tr>
		 
		<tr>
			<td class="txtlabel" style="vertical-align: top; text-align: right">Subchapter Description :<sup>*</sup></td>
			<td><%=uF.showData(subinnerList.get(2), "") %></td>
		</tr>
		
	</table>
	
	<%
	String showContentDiv = "none";
	if(hmContentData != null && !hmContentData.isEmpty()) {
		List<List<String>> contentList = hmContentData.get(subinnerList.get(0));
		if(contentList != null) { 
			showContentDiv = "block";
		} }
	%>
	
	<div id="contentDiv<%=i+1 %>_<%=j+1 %>" style="float: left; width: 100%; display: <%=showContentDiv %>;">  <!-- border: 1px solid; -->
		<div style="float: left; width: 11%; min-height: 200px; border: 1px solid; margin: 5px 5px 5px 5px;">
		<div style="width: 90%; margin: 5px 0px 5px 10px;"> Text </div>
		<div style="width: 90%; margin: 5px 0px 5px 10px;"> Image </div>
		<div style="width: 90%; margin: 5px 0px 5px 10px;"> Video </div>
		<div style="width: 90%; margin: 5px 0px 5px 10px;"> PDF </div>
		<div style="width: 90%; margin: 5px 0px 5px 10px;"> Attachment </div>
		</div>
		<div id="addcontentDiv<%=i+1 %>_<%=j+1 %>" style="float: left; width: 75%; min-height: 200px; margin: 5px 5px 5px 5px;">  <!-- border: 1px solid; -->
			<%
				if(hmContentData != null && !hmContentData.isEmpty()) {
					List<List<String>> contentList = hmContentData.get(subinnerList.get(0));
					if(contentList != null) { 

					for(int k=0; contentList != null && k< contentList.size(); k++) {
						List<String> contentinnerList = contentList.get(k);
						if(contentinnerList != null) {
				%>
			<% if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("IMAGE")) { %>
			<div id="imageDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
				<table width="100%">
					<%-- <tr>
						<td>Select Image:</td>
						<td><input type="file" onchange="readImageURL(this,'<%=i+1 %>', '<%=j+1 %>','<%=k+1 %>','contentImgIframe');" id="contentImage<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentImage"> </td>
					</tr> --%>
					<tr>
						<td colspan="3"><img width="500" height="300" src="<%=hmContentImg.get(contentinnerList.get(0)) %>" id="contentImgIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>"></td> 
					</tr>
				</table>
			</div>
			
			
		
			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("TEXT")) { %>
			<div id="textareaDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
				<table width="100%">
				<tr>
					<td><%=contentinnerList.get(1) %></td>
				</tr>
				</table>
			</div>
			

			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("VIDEO")) { %>
			<div id="videoDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
				<table width="100%">
					<tr>
						<td>Select Video:</td>
						<td><input type="file" id="contentVideo<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentVideo"></td>
					</tr>
				</table>
			</div>
			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("PDF")) { %>
			<div id="pdfDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
				<table width="100%">
					<%-- <tr><td>Select PDF:</td>
						<td><input type="file" onchange="readPdfURL(this, '<%=i+1 %>', '<%=j+1 %>','<%=k+1 %>','contentPDFIframe');" id="contentPdf<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentPdf"> </td>
					</tr> --%>
					<tr>
						<td colspan="3"><iframe width="96%" height="300" src="<%=hmContentImg.get(contentinnerList.get(0)) %>" id="contentPDFIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>"></iframe></td>
					</tr>
				</table>
			</div>
			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("ATTACH")) { %>
			<div id="attachDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
				<table width="100%">
					<tr>
						<td>Select Attachment:</td>
						<td><input type="file" id="contentAttach<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentAttach"></td>
					</tr>
				</table>
			</div>
			<% } %>
		<% } } } } %>	
		</div>
	</div>
		<% 
		String showAssessDiv = "none";
		int assessSize = 0;
		if(hmAssessmentData != null && !hmAssessmentData.isEmpty()) {
			List<List<String>> assessmentList = hmAssessmentData.get(subinnerList.get(0));
			
			if(assessmentList != null) { 
				showAssessDiv = "block";
				assessSize = assessmentList.size();
			} 
		}
		//System.out.println("assessSize ===> " + assessSize);
		%>
		<div id="assessmentDiv<%=i+1 %>_<%=j+1 %>" style="float: left; width: 100%; min-height: 200px; display: <%=showAssessDiv %>;"> <!-- border: 1px solid; display: none; -->
	<%
		if(hmAssessmentData != null && !hmAssessmentData.isEmpty()) {
			List<List<String>> assessmentList = hmAssessmentData.get(subinnerList.get(0));
				if(assessmentList != null) {
			for(int k=0; assessmentList != null && k < assessmentList.size(); k++) {
				List<String> assessinnerList = assessmentList.get(k);
				if(assessinnerList != null) {
		%>
		<ul id="questionUl<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="margin-left: 100px;">
						<li>
						
											<table class="tb_style" width="100%">
													<tr>
													<th><%=i+1 %>.<%=j+1 %>.<%=k+1 %>)</th>
														<td colspan="3"><span style="float: left; margin-left: 10px;"><%=assessinnerList.get(0) %>&nbsp;&nbsp;(<%=hmAnstypeName.get(assessinnerList.get(9)) %>) </span>
														<span style="float: right; margin-right: 20px;">Marks: <%=assessinnerList.get(1) %> </span>
														</td>
													</tr>
													<%-- <tr><th></th><td colspan="3"> <%=hmAnstypeName.get(assessinnerList.get(9)) %> </td></tr> --%>
													<%
														int getanstype = uF.parseToInt(assessinnerList.get(9));
														if(getanstype == 1 || getanstype == 2 || getanstype == 8 || getanstype == 9) { %>
														<tr id="answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
														<th></th><td>a)&nbsp;<%=assessinnerList.get(4)%> </td>
														<td colspan="2">b)&nbsp;<%=assessinnerList.get(5)%></td>
														</tr>
														<tr id="answerType1<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
														<th></th><td>c)&nbsp;<%=assessinnerList.get(6)%></td>
														<td colspan="2">d)&nbsp;<%=assessinnerList.get(7)%></td>
														</tr>
														<% } else if(getanstype == 6) { %>
														<tr id="answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>"><th></th><td colspan="3">&nbsp;True&nbsp;&nbsp;False</td></tr>
														<% } else if(getanstype == 5) { %>
														<tr id="answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>"><th></th><td colspan="3">&nbsp;Yes&nbsp;&nbsp;No</td></tr>
														<% } %>
												</table>
						</li>
					</ul>
		
		<% } } } } %>
		
	</div>
	
	</div>
	 	<% } } %>
 	</div>
	
	
  </div>
  		<%
			}
		}
		%>
  
   
	</div>
	</div>
	</div>

