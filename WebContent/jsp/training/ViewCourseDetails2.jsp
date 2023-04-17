<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script src="scripts/ckeditor_cust/ckeditor.js"></script>


<style type="text/css">
	
	.txtlbl {
		color: #777777;
	    font-family: verdana,arial,helvetica,sans-serif;
	    
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
	.ui-tabs-hide { display: none !important; }
	
</style>	

<script type="text/javascript">
	$(document).ready(function() {
		
			$('#container').tabs({
				fxAutoHeight : true
			});
			
			<%-- $('#container').tabs('select', <%=request.getAttribute("tab")%>); --%>
	});
	
    function deleteCourse(courseId) {
    	
    	$("#divCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
    	if(confirm('Are you sure you want to delete this course?')) {
			$.ajax({
				type:'POST',
				url:'AddNewCourse.action?operation=D&fromPage=LD&courseId='+courseId,
		        cache:true,
				success:function(result){
					//alert("delete course result==>"+result);
					$("#divCDResult").html(result);
				},
				error : function(result) { 
					$.ajax({ 
						url: "CourseDashboardData.action?fromPage=LD&dataType=C",
			   	     	cache: true,
			   	     	success: function(result){
				     		$("#divCDResult").html(result);
				     	}
					});
				}
			});
    	}
    }
    
  function editCourse(courseId) {
    	
    	$("#divCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
    	$.ajax({
				type:'POST',
				url:'AddNewCourse.action?operation=E&fromPage=LD&courseId='+courseId,
		        cache:true,
				success:function(result){
					//alert("edit course result==>"+result);
					$("#divCDResult").html(result);
				}
			});
    	
    }
  
	function fullViewPage(strId){ 
  		$.ajax({
			type:'POST',
			url:'LearningDocView.action?courseContentId='+strId,
			cache:true,
			success:function(result){
				//alert("edit course result==>"+result);
			}
		});
	}
	
	
</script>
<%
//System.out.println("VCD2.jsp");
	UtilityFunctions uF = new UtilityFunctions();
	String anstype = (String) request.getAttribute("anstype");
	List<List<String>> ansTypeList = (List<List<String>>) request.getAttribute("ansTypeList");
	String tab = (String)request.getAttribute("tab"); 
	String courseId = (String)request.getAttribute("courseId");
	String crsPreface = (String)request.getAttribute("crsPreface");
	String fromPage = (String)request.getAttribute("fromPage");
	String sbCourses = (String)request.getAttribute("sbCourses"); 
	List<List<String>> chapterList = (List<List<String>>) request.getAttribute("chapterList");
	Map<String, List<List<String>>> hmSubchapterData = (Map<String, List<List<String>>>) request.getAttribute("hmSubchapterData");
	Map<String, List<List<String>>> hmAssessmentData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentData");
	Map<String, String> hmAnstypeName = (Map<String, String>) request.getAttribute("hmAnstypeName");
	Map<String, String> hmAssessTotWeight = (Map<String, String>) request.getAttribute("hmAssessTotWeight");
	Map<String, List<List<String>>> hmContentData = (Map<String, List<List<String>>>) request.getAttribute("hmContentData");
	Map<String, String> hmContentImg = (Map<String, String>) request.getAttribute("hmContentImg");
	
	%>

<%if(fromPage != null && fromPage.equalsIgnoreCase("LD")) { %>
	<div style="width:100%;font-size:14px;">
		<%=uF.showData(sbCourses,"")%>
	</div>
<% } %>

<div id="printDiv" class="leftbox reportWidth" style="margin-top:10px;" >
	<div style="width: 100%; background-color: #868686; box-shadow:3px 4px 12px #616161;height: 80%;">
	
	<% if(uF.parseToInt(courseId)>0) { %>
		<div id="container" style="width: 99%; float: left; height: 98%; min-height: 600px;font-size: 12px;"> 
		   <ul>
				<li><a href="#course"><span>Cover Page</span> </a></li>
				<li><a href="#preface"><span>Preface</span> </a></li>
				<%if(courseId != null){ %>
					<li id="tabindex" ><a href="#index"><span>Index</span> </a></li>
					<% if(chapterList != null && !chapterList.isEmpty()){
							for(int i=0; i< chapterList.size(); i++){
								List<String> innerList = chapterList.get(i);
								if(innerList.get(1) != null && !innerList.get(1).equals("")) {
								%>
								<li  id="tabchapter<%=i+1 %>"><a href="#chapter<%=i+1 %>"><span><%=uF.showData(innerList.get(1), "") %> </span></a></li>
								<%
								}
							}
						}
					%>
				<%} %>
			</ul>
 			
 			<div style=" border: solid 0px #ff0000;width:96% " id="course">
 				<s:form theme="simple" action="AddNewCourse" id="frmAddNewCourse" method="POST" cssClass="formcss" enctype="multipart/form-data">
					<s:hidden name="operation"></s:hidden>
					<s:hidden name="courseId"></s:hidden>
					<input type="hidden" name="tab" value="0"/>
					<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>" />
					
					<div style="width: 100%; min-height: 500px; position:relative; box-shadow: 0 0 20px gray;">
						<table border="0" class="formcss" style="width: 97%; position:absolute; top:30%;">
							<tr>
								<td align="center"><br/><br/><br/> 
									<div style="float: left; margin: 10px; width: 100%; font-size: 28px; font-weight: bold; font-style: italic; font-family: Verdana;">
										<%=request.getAttribute("courseName") %></div><br/>
									<% if(request.getAttribute("courseSubject") != null) { %>
										<div style="width: 100%; float: left; margin: 5px;"><%=request.getAttribute("courseSubject") %></div> <br/>
									<% } %>
									<% if(request.getAttribute("courseAuthor") != null) { %>
										<div style="width: 100%; float: left; margin: 5px;"><%=request.getAttribute("courseAuthor") %></div> <br/>
									<% } %>
									<% if(request.getAttribute("courseVersion") != null) { %>
										<div style="width: 100%; float: left; margin: 5px;"><%=request.getAttribute("courseVersion") %></div> <br/>
									<% } %>
							 	</td>
							</tr>
					   </table>
				       <div style="float: right; position:absolute; bottom:10px; right:20px;">1</div>
				  </div>
			  </s:form> 
			</div>
  
  			<div style=" border: solid 0px #ff0000;width:96% " id="preface">
 				<s:form theme="simple" action="AddNewCourse" id="frmAddNewCourse" method="POST" cssClass="formcss" enctype="multipart/form-data">
					<s:hidden name="operation"></s:hidden>
					<s:hidden name="courseId"></s:hidden>
					<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>" />
					<input type="hidden" name="tab" value="0"/>
				
					<div style="width: 100%; min-height: 500px; position:relative; box-shadow:0 0 20px gray;">
						<table border="0" class="formcss" style="width: 90%; margin-left: 5%;">
							<tr>
								<td class="txtlabel" style="vertical-align: top; text-align: center;"> <h3>Preface:</h3></td>
							</tr>
							<tr>
								<td> <%=uF.showData(crsPreface, "") %> </td>
							</tr>
							
						</table>
						<div style="float: right; position:absolute; bottom:10px; right:20px;">2</div>
					</div>
				</s:form>
			</div>
  
  <% if(courseId != null) { %>
  		<div style="border: solid 0px #ff0000; width:96% " id="index">
			<s:form theme="simple" action="AddNewCourse" id="frmAddNewCourse1" method="POST" cssClass="formcss" enctype="multipart/form-data">
				<s:hidden name="operation"></s:hidden>
				<s:hidden name="courseId"></s:hidden>
				<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>" />
				<input type="hidden" name="tab" value="1"/>
				
				<div style=" width: 100%; min-height: 500px; position:relative; box-shadow:0 0 20px gray;">
					<table border="0" class="formcss" style="width: 90%; margin-left: 5%;">
						<tr>
							<td colspan="4" class="txtlabel" style="vertical-align: top; text-align: center; padding-bottom:20px;"> <h3>Index:</h3></td>
						</tr>
						
						<%  if(chapterList != null && !chapterList.isEmpty()){ %>
								<tr>
									<td height="10px">&nbsp;</td>
									<td height="10px" style="font-size: 13px; font-weight: bold;">Particulars</td>
									<td class="txtlbl" height="10px">&nbsp;</td>
									<td height="10px" align="right" style="font-size: 13px; font-weight: bold;">Page No.</td>
								</tr>	  
							  <%
								for(int i=0; i< chapterList.size(); i++){
									List<String> innerList = chapterList.get(i);
									if(innerList.get(1) != null && !innerList.get(1).equals("")) {
									%> 
									<tr>
										<td class="txtlbl" height="10px" align="right" style="width: 20px;"><%=i+1 %>)&nbsp;&nbsp;</td>
										<td class="txtlbl" height="10px" colspan="2"><%=innerList.get(1) %></td>
										<td class="txtlbl" height="10px" align="right"><%=i+4 %></td>
									</tr>
									<%
										if(hmSubchapterData != null && !hmSubchapterData.isEmpty()) {
											List<List<String>> subchapterList = hmSubchapterData.get(innerList.get(0));
											for(int j=0; subchapterList != null && !subchapterList.isEmpty() && j< subchapterList.size(); j++){
												List<String> subinnerList = subchapterList.get(j);
								%>  
												<tr>
													<td height="10px">&nbsp;</td>
													<td class="txtlbl" height="10px" colspan="2"><%=i+1 %>.<%=j+1 %>)&nbsp;&nbsp;&nbsp; <%=subinnerList.get(1) %> </td>
													<td class="txtlbl" height="10px" align="right"><%=i+4 %></td>
												</tr>
										<%  }
										} %> 
								<% } 
								} 
							 } %>
						
							<% if(chapterList == null || chapterList.isEmpty()){ %>
									<tr>
										<td class="txtlbl" colspan="4"><div class="nodata msg"><span> No Content Added</span></div></td>
									</tr>
							<% } %>
						</table>
					<div style="float: right; position:absolute; bottom:10px; right:20px;">3</div>
				</div>
			</s:form> 
		 </div>
  <% 
  String chapterCnt = (String) request.getAttribute("chapterCnt");
  if(chapterList != null && !chapterList.isEmpty()){
		for(int i=0; i< chapterList.size(); i++){
			List<String> innerList = chapterList.get(i);
			if(innerList.get(1) != null && !innerList.get(1).equals("")) {
	%>
						
  <div style="border: solid 0px #ff0000;width:96% " id="chapter<%=i+1 %>">
 	<s:form theme="simple" action="AddNewCourse" id="frmAddNewCourse2" method="POST" cssClass="formcss" enctype="multipart/form-data">
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="courseId"></s:hidden>
		<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>" />
		<input type="hidden" name="chapterCnt" value="<%=i+1 %>"/>
		<input type="hidden" name="tab" value="<%=i+2 %>"/>
 		<div style="width: 100%; min-height: 500px; position:relative; box-shadow: 0 0 20px gray;">
		 	<table border="0" class="formcss" style="width: 85%; margin-left: 5%;">
				
				<tr>
					<td> <h3><%=i+1 %>)&nbsp;<%=uF.showData(innerList.get(1), "") %></h3></td>
				</tr>
				
				<tr>
					<td><%=uF.showData(innerList.get(2), "") %></td>
				</tr>
			</table>
	
	<div id="subchapterDiv<%=i+1 %>">
	<% if(hmSubchapterData != null && !hmSubchapterData.isEmpty()) {
		List<List<String>> subchapterList = hmSubchapterData.get(innerList.get(0));
		for(int j=0; subchapterList != null && !subchapterList.isEmpty() && j< subchapterList.size(); j++) {
			List<String> subinnerList = subchapterList.get(j);
		%>
			<div id="subchapter<%=i+1 %>_<%=j+1 %>" style="width: 100%;">
				<table border="0" class="formcss" style="width: 85%; margin-left: 5%;">
					<tr>
						<td><h4> <%=i+1 %>.<%=j+1 %>)&nbsp;<%=uF.showData(subinnerList.get(1), "") %></h4></td>
					</tr>
					 
					<tr>
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
	<div id="contentDiv<%=i+1 %>_<%=j+1 %>" style="width: 100%; margin-left: 6%; margin-bottom: 20px; display: <%=showContentDiv %>;">  <!-- border: 1px solid; -->
		<div id="addcontentDiv<%=i+1 %>_<%=j+1 %>" style="width: 75%; margin-top: -10px;">  <!-- min-height: 200px; border: 1px solid; -->
		<%
				if(hmContentData != null && !hmContentData.isEmpty()) {
					List<List<String>> contentList = hmContentData.get(subinnerList.get(0));
					if(contentList != null) { 

					for(int k=0; contentList != null && k< contentList.size(); k++) {
						List<String> contentinnerList = contentList.get(k);
						if(contentinnerList != null) {
							//System.out.println(contentinnerList.get(2));
				%>
			<% if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("IMAGE")) { %>
			<div id="imageDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;">
				<table width="100%">
					<tr>
						<td colspan="3"><img width="500" height="300" src="<%=hmContentImg.get(contentinnerList.get(0)) %>" id="contentImgIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>"></td> 
					</tr>
					<tr>
						<td colspan="3"><%=contentinnerList.get(7) %></td> 
					</tr>
				</table>
			</div>
			
			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("TEXT")) { %>
			<div id="textareaDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;">
				<table width="100%">
				<tr>
					<td><%=contentinnerList.get(1) %></td>
				</tr>
				</table>
			</div>
			
			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("VIDEO")) { %>
			<div id="videoDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;">
				<table width="100%">
					<tr>
						<td>Select Video:</td>
						<td><input type="file" id="contentVideo<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentVideo"></td>
					</tr>
					<tr>
						<td colspan="3"><%=contentinnerList.get(8) %></td>
					</tr>
					<tr>
						<td colspan="3"><%=contentinnerList.get(7) %></td>
					</tr>
				</table>
			</div>
			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("PDF")) { %>
			
			<div id="pdfDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;">
				<table width="100%">
					<tr>
						<td colspan="3"><%=contentinnerList.get(7) %></td>
					</tr>
					<tr>
						<%-- <td colspan="3"><iframe width="120%" height="350" src="<%=hmContentImg.get(contentinnerList.get(0)) %>#toolbar=0" id="contentPDFIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>"></iframe></td> --%>
						<td colspan="3">
							<div class="col-md-12" style="width: 130%;">
								<div class="col-md-10">
									<iframe width="100%" height="350" src="https://docs.google.com/gview?url=<%=hmContentImg.get(contentinnerList.get(0)) %>&embedded=true" id="contentPDFIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" sandbox="allow-scripts allow-same-origin"></iframe>
								</div>
								<div class="col-md-2" style="text-align: left; ">
									<a href="LearningDocView.action?strId=<%=contentinnerList.get(0) %>" target = "_blank"><strong>Full View</strong> </a>
									<%-- <a onclick="fullViewPage('<%=contentinnerList.get(0) %>');" href="javascript:void(0)" target = "_blank"><strong>Full View</strong> </a> --%>
								</div>
							</div>
							
						</td>
					</tr>
					<%-- <tr>
						<td colspan="3">
							<input type="checkbox" name="chapters" id="chapters<%=i %>" value="<%=contentinnerList.get(0) %>" onclick="updateDocReadStatus('contentPDFIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>');" />
						</td>
					</tr> --%>
				</table>
			</div>
			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("PPT")) { %>
			
			<div id="pptDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;">
				<table width="100%">
					<tr>
						<td colspan="3"><%=contentinnerList.get(7) %></td>
					</tr>
					<tr>
						<td colspan="3">
							<div class="col-md-12" style="width: 130%;">
								<div class="col-md-10">
									<iframe width="100%" height="350" src="https://docs.google.com/gview?url=<%=hmContentImg.get(contentinnerList.get(0)) %>&embedded=true" id="contentPPTIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" sandbox="allow-scripts allow-same-origin"></iframe>
								</div>
								<div class="col-md-2" style="text-align: left;">
									<a href="LearningDocView.action?strId=<%=contentinnerList.get(0) %>" target = "_blank"><strong>Full View</strong> </a>
									<%-- <a onclick="fullViewPage('<%=contentinnerList.get(0) %>');" href="javascript:void(0)" target = "_blank"><strong>Full View</strong> </a> --%>
								</div>
							</div>
						</td>
					</tr>
				</table>
			</div>
			<% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("ATTACH")) { %>
			<div id="attachDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;">
				<table width="100%">
					<tr>
						<td colspan="3">&nbsp;<%=contentinnerList.get(7) %>&nbsp;&nbsp;&nbsp; <a href="<%=contentinnerList.get(6)%>" ><i class="fa fa-file-o" aria-hidden="true" title="click to download"></i></a></td>
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
		<div id="assessmentDiv<%=i+1 %>_<%=j+1 %>" style="float: left; width: 100%; margin-bottom: 20px; display: <%=showAssessDiv %>;"> <!-- min-height: 200px; border: 1px solid; display: none; -->
		
	<%
		if(hmAssessmentData != null && !hmAssessmentData.isEmpty()) {
			List<List<String>> assessmentList = hmAssessmentData.get(subinnerList.get(0));
			String totWeightage = hmAssessTotWeight != null ? hmAssessTotWeight.get(subinnerList.get(0)) : "";
				if(assessmentList != null) {
			for(int k=0; assessmentList != null && k < assessmentList.size(); k++) {
				List<String> assessinnerList = assessmentList.get(k);
				if(assessinnerList != null) {
		%>
				<ul id="questionUl<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="width: 93%;">
					<li>
					
						<table class="tb_style" width="100%">
								<tr>
								<th style="width: 50px;"><%=i+1 %>.<%=j+1 %>.<%=k+1 %>)</th>
									<td colspan="3"><span style="float: left; margin-left: 10px; width: 90%;"><%=assessinnerList.get(0) %>&nbsp;&nbsp;(<%=hmAnstypeName.get(assessinnerList.get(9)) %>) </span>
									<span style="float: right; margin-right: 10px;">Marks: <%=assessinnerList.get(1) %> </span>
									</td>
								</tr>
						
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
				<!-- </li></ul> -->
			</div>
	
		</div>
	 	<% } } %>
 	</div>
 			<div style="float: right; position:absolute; bottom:10px; right:20px;"><%=i+4 %></div>
 	</div>
	
	</s:form>
  </div>
  
  		<%
				}
			}
		}
		%>

  <% } %>
		</div>
	<% } else { %>
	
	<% } %>	
	</div>
	</div>

