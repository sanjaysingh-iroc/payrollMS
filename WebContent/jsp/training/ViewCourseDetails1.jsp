<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>


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
		margin: 10px 0px 10px 50px;
	}
			
	.clear{
		clear:both;
	}
	img{
		border:0px;
	}	
	</style>
	
<head>
    <meta charset="utf-8" />
    <title>Booklet - jQuery Plugin</title>
    <link href="scripts/booklet/jquery.booklet.latest.css" type="text/css" rel="stylesheet" media="screen, projection, tv" />
    <style type="text/css">
        body {background:#ccc; font:normal 12px/1.2 verdana, arial, sans-serif;}
    </style>
</head>
<body>
	
		<h1>Booklet Example</h1>
	
	
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
		System.out.println("VCD1.jsp/59 ===> ");
		%>
		
	
	    <div id="mybook">
	    <div title="cover page"><h2> Course Details </h2></div>
	    <div title="cover1 page"></div>
	    <%-- <% String allDataOfCourse = uF.showData((String)request.getAttribute("courseName"),"")
	    	+ uF.showData((String)request.getAttribute("courseSubject"),"")
	    	+ uF.showData((String)request.getAttribute("courseAuthor"),"")
	    	+ uF.showData((String)request.getAttribute("courseVersion"),"")
	    	+ uF.showData(crsPreface, "");
	    	int dataLength = allDataOfCourse.length();
	    	int pages = 1; 
	    	if(dataLength > 2000){
	    		pages = dataLength/ 2000;
	    		pages++;
	    	}
	    	%> --%>
	        <div title="first page 1">
	            <h3><%=uF.showData((String)request.getAttribute("courseName"),"") %></h3> <br/>
	            <strong>Subject:-</strong> <%=uF.showData((String)request.getAttribute("courseSubject"),"") %><br/>
	            <strong>Author:-</strong> <%=uF.showData((String)request.getAttribute("courseAuthor"),"") %><br/>
	            <strong>Version:-</strong> <%=uF.showData((String)request.getAttribute("courseVersion"),"") %><br/>
	            <div style="float: left; width: 100%; text-align: center; margin: 5px;"><strong> Preface </strong></div>
	            <%-- <%=uF.showData(crsPreface.substring(0,crsPreface.length() > 1900 ? 1900 : crsPreface.length()), "") %> --%>
	            <%=uF.showData(crsPreface, "") %>
	        </div>
	        <%-- <% if(pages > 1) {
	        	int charLength = 1900;
	        	
	        for(int i=2; i<= pages; i++){
	        	
	        	int lastCharLen = charLength + 2000;
	        %>
	        <div title="first page <%=i %>"> <%=uF.showData(crsPreface.substring(charLength,crsPreface.length() > lastCharLen ? lastCharLen : crsPreface.length()), "") %></div>
	        <% 
	        charLength = charLength + 2000;
	        } } %> --%>
	        
	        <div title="index page">
	            <div style="float: left; width: 100%;">
					<table class="formcss" style="width: 85%;">

						<tr>
							<td class="alignCenter" colspan="2"><span style="font-size: 17px; padding: 5px;"> Index </span></td>
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
	        <div title="chapter page">
	            <div style="border: solid 0px #ff0000; float: left; width:96%" id="chapter<%=i+1 %>">
 	
						<div style="float: left; width: 100%; margin: 2px;"><strong>Chapter <%=i+1 %>:</strong> <%=uF.showData(innerList.get(1), "") %></div>
						<div style="float: left; width: 100%; margin: 2px;"><%=uF.showData(innerList.get(2), "") %></div>
					

				<div id="subchapterDiv<%=i+1 %>">
				<% if(hmSubchapterData != null && !hmSubchapterData.isEmpty()){
					List<List<String>> subchapterList = hmSubchapterData.get(innerList.get(0));
					
						for(int j=0; subchapterList != null && !subchapterList.isEmpty() && j< subchapterList.size(); j++){
							List<String> subinnerList = subchapterList.get(j);
					%>
				
				
				<div id="subchapter<%=i+1 %>_<%=j+1 %>" style="float: left; width: 100%; margin-top: 25px;">
						<div style="float: left; width: 100%; margin: 2px;"><strong>Subchapter <%=i+1 %>.<%=j+1 %></strong> <%=uF.showData(subinnerList.get(1), "") %></div>

						<div style="float: left; width: 100%; margin: 2px;"><%=uF.showData(subinnerList.get(2), "") %></div>
				
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
					<ul id="questionUl<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="margin-left: 50px;">
									<li>
										
										<div style="float: left; width: 100%; margin: 2px;"><span style="float: left;"><%=i+1 %>.<%=j+1 %>.<%=k+1 %>) &nbsp;&nbsp;<%=assessinnerList.get(0) %>&nbsp;&nbsp;(<%=hmAnstypeName.get(assessinnerList.get(9)) %>) </span>
											<span style="float: right; margin-right: 20px;">Marks: <%=assessinnerList.get(1) %> </span>
										</div>
										<%
											int getanstype = uF.parseToInt(assessinnerList.get(9));
											if(getanstype == 1 || getanstype == 2 || getanstype == 8 || getanstype == 9) { %>
											
											<div style="float: left; width: 100%; margin: 2px;">a)&nbsp;<%=assessinnerList.get(4)%> 
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b)&nbsp;<%=assessinnerList.get(5)%>
											</div>
											<div style="float: left; width: 100%; margin: 2px;">c)&nbsp;<%=assessinnerList.get(6)%>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;d)&nbsp;<%=assessinnerList.get(7)%>
											</div>
											<% } else if(getanstype == 6) { %>
											<div style="float: left; width: 100%; margin: 2px;">&nbsp;True&nbsp;&nbsp;False</div>
											<% } else if(getanstype == 5) { %>
											<div style="float: left; width: 100%; margin: 2px;">&nbsp;Yes&nbsp;&nbsp;No</div>
											<% } %>
									
									</li>
								</ul>
								<div>&nbsp;</div>
					
					<% } } } } %>
					
				</div>
				
				</div>
				 	<% } } %>
			 	</div>
				
				
			  </div>
	        </div>
	        
	        <% } } %>
	        <div title="backcover page"></div>
	    </div>
	
  <!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.0/jquery.min.js"></script>
	<script> window.jQuery || document.write('<script src="scripts/booklet/jquery-2.1.0.min.js"><\/script>') </script>
    <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
	<script> window.jQuery.ui || document.write('<script src="scripts/booklet/jquery-ui-1.10.4.min.js"><\/script>') </script>
    <script src="scripts/booklet/jquery.easing.1.3.js"></script>
    <script src="scripts/booklet/jquery.booklet.latest.js"></script> -->
	<script>
	    $(function () {		
	        $("#mybook").booklet();
	        
	        $('#mybook').booklet({
   		        width:  '90%',
   		        height: 500,
   		        
   		     	pagePadding: 50,
   		     	
	   		    closed: true,
	   		    autoCenter: true,
	   		    covers: true 
   		     
   		    });
	    });
    </script>
</body>
