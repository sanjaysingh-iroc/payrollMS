<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#lt').dataTable({
		bJQueryUI : true,
		"sPaginationType" : "full_numbers",
		"aaSorting" : []
	})
}); 

$(document).ready(function() {
	$('#lt1').dataTable({
		bJQueryUI : true,
		"sPaginationType" : "full_numbers",
		"aaSorting" : []
	})
}); 

$(document).ready(function() {
	$('#lt2').dataTable({
		bJQueryUI : true,
		"sPaginationType" : "full_numbers",
		"aaSorting" : []
	})
}); 


jQuery(document).ready(function() {
	jQuery(".offerContent").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading").click(function() {
		jQuery(this).next(".offerContent").slideToggle(500);
		$(this).toggleClass("close_div");
	});
});
		

function openAppraisalPreview(id,appFreqId) {
	//alert("openQuestionBank count "+ count)
	 var dialogEdit = '#appraisalPreviewDiv';
			$(dialogEdit).dialog(
				{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 800,
				width : 1100,
				modal : true,
				title : 'Appraisal Preview',
				open : function() {
					var xhr = $.ajax({
						url : "AppraisalPreview.action?id="+id+"&appFreqId="+appFreqId,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
					xhr = null;
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});
		$(dialogEdit).dialog('open');
	}

</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Performance Reviews" name="title"/>
</jsp:include>    
<% String strSessionUserType=(String) session.getAttribute(IConstants.USERTYPE);%>

    <div id="printDiv" class="leftbox reportWidth">  
<!-- Place holder where add and delete buttons will be generated -->
<!-- <div class="add_delete_toolbar"></div> -->  

	<div class="filter_div">
		<div class="filter_caption">Filter</div>
		<s:form name="frm" action="AppraisalReport" theme="simple">
			<%-- <s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select> --%>
			<s:select theme="simple" name="appraisalType" headerKey="" headerValue="All Appraisal Type" 
			list="#{'Annual Appraisal':'Annual Appraisal', 'Monthly Appraisal':'Monthly Appraisal','Mid Term Appraisal':'Mid Term Appraisal', 'Ad hoc Appraisal':'Ad hoc Appraisal','Feedback':'Feedback', 'Review':'Review'}" onchange="document.frm.submit();"/>
			<s:select theme="simple" name="oreinted"  list="orientationList" listKey="id" listValue="name" headerKey="" headerValue="All Orientation" onchange="document.frm.submit();"/>
			<s:select theme="simple" name="frequency"  list="frequencyList" listKey="id" listValue="name" headerKey="" headerValue="All Frequency"  onchange="document.frm.submit();" /> 
			<s:select theme="simple" name="strLocation" listKey="wLocationId" headerKey="" headerValue="All Location" listValue="wLocationName" list="workList" onchange="document.frm.submit();"/>
			<s:select theme="simple" name="strEmployee" listKey="employeeId" list="empList" listValue="employeeCode" headerKey="" headerValue="All Appraisee" onchange="document.frm.submit();"/>
		</s:form>
	</div> 

    <s:property value="message"/>
    
    <% if(strSessionUserType!=null && (strSessionUserType.equals(IConstants.HRMANAGER) || strSessionUserType.equals(IConstants.ADMIN))){ %>
   <div style="float: right; margin-bottom: 10px">
		<a href="CreateAppraisal.action"><input type="button" class="input_button" value="Add New Appraisal"> </a>
	</div>
	<%} %>
	<div>
	<h2>Live</h2>
    	<table class="display" id="lt">
    	<% UtilityFunctions uF=new UtilityFunctions(); %>
				<thead>
					<tr> 
						<th style="text-align: left;">Appraisal Name</th>
						<th style="text-align: left;">Appraisal Type</th>
						<th style="text-align: left;">Orientation Type</th>
						<th style="text-align: left;">Frequency Type</th>
						<th style="text-align: left;">From Date</th>
						<th style="text-align: left;">End Date</th>
						<th style="text-align: left;">Location</th>
						<th style="text-align: left;">Added By</th>
						<th style="text-align: left;">Entry Date</th>
						<th style="text-align: left;">
						<s:if test="type =='choose'">
						Preview
						</s:if><s:else>Summary</s:else> </th>
						<s:if test="type =='choose'">
						<th style="text-align: left;">Choose</th>						
						</s:if>
						<s:else>
						<th style="text-align: left;">Status</th>
						<th style="text-align: left;">Publish</th>
						</s:else>
					</tr>
				</thead>
				
				<tbody>
				<% java.util.List<List<String>> liveList = (java.util.List<List<String>>)request.getAttribute("liveList"); 
				
				for(int i=0;liveList!=null && i<liveList.size();i++){
					List<String> innerList=liveList.get(i);
					//System.out.println("Status ========== > "+innerList.get(10));
					%>
					<tr id = "<%= innerList.get(0) %>">
						<td>
						<%if(innerList.get(11)!=null && uF.parseToInt(innerList.get(11)) != 0){ %>
						<img border="0" style="padding: 5px 5px 0pt; width: 17px; height: 17px;" src="<%=request.getContextPath()%>/images1/warning.png">
						<%} %>
						<%= innerList.get(1) %>
						<%if(innerList.get(10).equalsIgnoreCase("F")){ %>
						<img border="0" style="padding: 5px 5px 0pt;" src="<%=request.getContextPath()%>/images1/icons/news_icon.gif"/>
						<%} %>
						</td>
						<td><%= innerList.get(3) %></td>
						<td><%= innerList.get(2) %>&deg;</td>
						<td><%= innerList.get(4) %></td>
						<td><%= innerList.get(5) %></td>
						<td><%= innerList.get(6) %></td>	
						<td><%= innerList.get(7) %></td>		
						<td><%= innerList.get(8) %></td>
						<td><%= innerList.get(9) %></td>		
						<td>
							<a href="AppraisalSummary.action?id=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12) %>&type=<s:property value="type"/>"><img src="images1/summary_gray.png" title="View" /></a>
							<a href="javascript: void(0)" onclick="openAppraisalPreview('<%=innerList.get(0) %>','<%=innerList.get(12)%>')"><img src="images1/summary_gray.png" title="Preview" /></a>
						</td>
						<s:if test="type =='choose'">
							<td>
							<a href="javascript:void(0);" onclick="if(confirm('Are you sure, You want to create Appraisal from this template?')) window.location='CreateAppraisalFromTemplate.action?existID=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12) %>';">Choose</a>
						</td>
						</s:if>
						<s:else>
						<td><a href="AppraisalStatus.action?id=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12) %>"><img src="images1/status_gray.png" title="View" /></a></td>
						<td>
						
						<%if(innerList.get(10).equals("t")){ %>
						
						<div id="myDivM<%=i %>" >
						
						<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to unpublish this appraisal?'))
						getContent('myDivM<%=i %>','PublishAppraisal.action?id=<%=innerList.get(0) %>&dcount=<%=i %>&appFreqId=<%=innerList.get(12) %>');" >
							<img src="<%=request.getContextPath()%>/images1/icons/icons/unpublish_icon.png" title="Published" /></a> 
						</div>
						
						<%} else{ %>
						
						<div id="myDivM<%=i %>">
						<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this appraisal?'))
						getContent('myDivM<%=i %>','PublishAppraisal.action?id=<%=innerList.get(0) %>&dcount=<%=i %>&appFreqId=<%=innerList.get(12) %>');" >
							<img src="<%=request.getContextPath()%>/images1/icons/icons/publish_icon.png" title="Waiting for Publish" /></a> 
						</div>
						
						<%} %>
						</td>	
						</s:else>
					</tr>
				<%}	%>
				
				</tbody>
			</table> 
		</div>
		
		
		<div>
		<h2>Future</h2>
    	<table class="display" id="lt1">
				<thead>
					<tr> 
						<th style="text-align: left;">Appraisal Name</th>
						<th style="text-align: left;">Appraisal Type</th>
						<th style="text-align: left;">Orientation Type</th>
						<th style="text-align: left;">Frequency Type</th>
						<th style="text-align: left;">From Date</th>
						<th style="text-align: left;">End Date</th>
						<th style="text-align: left;">Location</th>
						<th style="text-align: left;">Added By</th>
						<th style="text-align: left;">Entry Date</th>
						<th style="text-align: left;">
						<s:if test="type =='choose'">
						Preview
						</s:if><s:else>Summary</s:else> </th>
						
						<s:if test="type =='choose'">
						<th style="text-align: left;">Choose</th>						
						</s:if>
						<s:else>
						<th style="text-align: left;">Status</th>
						<th style="text-align: left;">Publish</th>
						</s:else>

					</tr>
				</thead>
				
				<tbody>
				<% java.util.List<List<String>> futureList = (java.util.List<List<String>>)request.getAttribute("futureList"); 
				
				for(int i=0;futureList!=null && i<futureList.size();i++){
					List<String> innerList=futureList.get(i);%>
					
					<tr id = "<%= innerList.get(0) %>" >
						<td>
						<%if(innerList.get(11)!=null && uF.parseToInt(innerList.get(11)) != 0){ %>
						<img border="0" style="padding: 5px 5px 0pt; width: 17px; height: 17px;" src="<%=request.getContextPath()%>/images1/warning.png">
						<%} %>
						<%= innerList.get(1) %>
						</td>
						<td><%= innerList.get(3) %></td>
						<td><%= innerList.get(2) %>&deg;</td>
						<td><%= innerList.get(4) %></td>
						<td><%= innerList.get(5) %></td>
						<td><%= innerList.get(6) %></td>	
						<td><%= innerList.get(7) %></td>		
						<td><%= innerList.get(8) %></td>
						<td><%= innerList.get(9) %></td>		
						<td>
							<a href="AppraisalSummary.action?id=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12) %>&type=<s:property value="type"/>"><img src="images1/summary_gray.png" title="View" /></a>
						</td>
						<s:if test="type =='choose'">
							<td>
							<a href="javascript:void(0);" onclick="if(confirm('Are you sure, You want to create Appraisal from this template?')) window.location='CreateAppraisalFromTemplate.action?existID=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12) %>';">Choose</a>
						</td>
						</s:if>
						<s:else>
						<td><a href="AppraisalStatus.action?id=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12) %>"><img src="images1/status_gray.png" title="View" /></a></td>
						<td>
						
						<%if(innerList.get(10).equals("t")){ %>
						
						<div id="myDivM<%=i %>" >
						
						<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to unpublish this appraisal?'))
						getContent('myDivM<%=i %>','PublishAppraisal.action?id=<%=innerList.get(0) %>&dcount=<%=i %>&appFreqId=<%=innerList.get(12) %>');" >
							<%-- <img src="<%=request.getContextPath()%>/images1/icons/approved.png" title="Published" /> --%>
							<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Published" ></i>
							</a> 
						</div>
						
						<%} else{ %>
						
						<div id="myDivM<%=i %>" >
						
						<%-- <a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this appraisal?'))
						getContent('myDivM<%=i %>','PublishAppraisal.action?id=<%=innerList.get(0) %>&dcount=<%=i %>&appFreqId=<%=innerList.get(12) %>');" >
							<img src="<%=request.getContextPath()%>/images1/icons/re_submit.png" title="Waiting for Publish" /></a>  --%>
							
							
							<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this appraisal?'))
						getContent('myDivM<%=i %>','PublishAppraisal.action?id=<%=innerList.get(0) %>&dcount=<%=i %>&appFreqId=<%=innerList.get(12) %>');" >
							<i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d\" title="Waiting for Publish" ></i></a> 
							
							
						</div>
						
						<%} %>
						
						
						</td>	
						</s:else>
					</tr>
				<%}	%>
				
				</tbody>
			</table> 
		</div>
		
		
		<div>
		<h2>Previous</h2>
    	<table class="display" id="lt2">
				<thead>
					<tr> 
						<th style="text-align: left;">Appraisal Name</th>
						<th style="text-align: left;">Appraisal Type</th>
						<th style="text-align: left;">Orientation Type</th>
						<th style="text-align: left;">Frequency Type</th>
						<th style="text-align: left;">From Date</th>
						<th style="text-align: left;">End Date</th>
						<th style="text-align: left;">Location</th>
						<th style="text-align: left;">Added By</th>
						<th style="text-align: left;">Entry Date</th>
						<th style="text-align: left;">
						<s:if test="type =='choose'">
						Preview
						</s:if><s:else>Summary</s:else> </th>
						
						<s:if test="type =='choose'">
						<th style="text-align: left;">Choose</th>						
						</s:if>
						<s:else>
						<th style="text-align: left;">Status</th>
						<!-- <th style="text-align: left;">Publish</th> -->
						</s:else>

					</tr>
				</thead>
				
				<tbody>
				<% java.util.List<List<String>> previousList = (java.util.List<List<String>>)request.getAttribute("previousList"); 
				
				for(int i=0;previousList!=null && i<previousList.size();i++){
					List<String> innerList=previousList.get(i);%>
					
					<tr id = "<%= innerList.get(0) %>" >
						<td>
						<%if(innerList.get(11)!=null && uF.parseToInt(innerList.get(11)) != 0){ %>
						<img border="0" style="padding: 5px 5px 0pt; width: 17px; height: 17px;" src="<%=request.getContextPath()%>/images1/warning.png">
						<%} %>
						<%= innerList.get(1) %></td>
						<td><%= innerList.get(3) %></td>
						<td><%= innerList.get(2) %>&deg;</td>
						<td><%= innerList.get(4) %></td>
						<td><%= innerList.get(5) %></td>
						<td><%= innerList.get(6) %></td>	
						<td><%= innerList.get(7) %></td>		
						<td><%= innerList.get(8) %></td>
						<td><%= innerList.get(9) %></td>		
						<td>
							<a href="AppraisalSummary.action?id=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12)%>&type=<s:property value="type"/>"><img src="images1/summary_gray.png" title="View" /></a>
						</td>
						<s:if test="type =='choose'">
							<td>
							<a href="javascript:void(0);" onclick="if(confirm('Are you sure, You want to create Appraisal from this template?')) window.location='CreateAppraisalFromTemplate.action?existID=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12)%>';">Choose</a>
						</td>
						</s:if>
						<s:else>
						<td><a href="AppraisalStatus.action?id=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(12)%>"><img src="images1/status_gray.png" title="View" /></a></td>
						<%-- <td>
						
						<%if(innerList.get(10).equals("t")){ %>
						
						<div id="myDivM<%=i %>" >
						
						<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to unpublish this appraisal?'))
						getContent('myDivM<%=i %>','PublishAppraisal.action?id=<%=innerList.get(0) %>&dcount=<%=i %>');" >
							<img src="<%=request.getContextPath()%>/images1/icons/approved.png" title="Published" /></a> 
						</div>
						
						<%} else{ %>
						
						<div id="myDivM<%=i %>" >
						
						<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this appraisal?'))
						getContent('myDivM<%=i %>','PublishAppraisal.action?id=<%=innerList.get(0) %>&dcount=<%=i %>');" >
							<img src="<%=request.getContextPath()%>/images1/icons/re_submit.png" title="Waiting for Publish" /></a> 
						</div>
						
						<%} %>
						
						
						</td> --%>	
						</s:else>
					</tr>
				<%}	%>
				
				</tbody>
			</table> 
		</div>
		
		
    </div>
    
    <div id="appraisalPreviewDiv"></div>
    