<%@ page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
	
	String id=(String)request.getAttribute("id");
	String dcount=(String)request.getAttribute("dcount");
	String empId = (String)request.getAttribute("empId");
	String appFreqId = (String)request.getAttribute("appFreqId");
	boolean is_publish =(Boolean)request.getAttribute("is_publish");
	String fromPage = (String)request.getAttribute("fromPage");
	UtilityFunctions uF = new UtilityFunctions();
	
	if(is_publish == false) {
		%>
			<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to Unpublish this appraisal?'))
			getPublishAppraisal('<%=id %>', '<%=dcount %>','<%=empId %>','<%=appFreqId %>');" >
			<i class="fa fa-toggle-on" aria-hidden="true" title="Published"></i></a>
			<%-- <img src="<%=request.getContextPath()%>/images1/icons/icons/publish_icon_b.png" title="Published" /></a> --%>
			::::
			<a target=new  href="AppraisalSummary.action?id=<%=id%>&appFreqId=<%=appFreqId %>" onclick="if(confirm('Unpublish this review To Edit,Click OK to proceed...!'))
			getPublishAppraisal('<%=id %>', '<%=dcount %>','<%=empId %>','<%=appFreqId %>');" title="Edit Review"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
			::::
			<%
				if(uF.parseToInt(id)== uF.parseToInt(empId)) {
			%>
					<%-- <img src="<%=request.getContextPath()%>/images1/icons/approved.png"/>  --%>
					<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
			<%
				}else{
			%>
					<%-- <img src="<%=request.getContextPath()%>/images1/icons/pullout.png"/> --%>
					<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
			<% } %>
				
				
							
	<%	} else { %>
	
			<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to Publish this appraisal?'))
			getPublishAppraisal('<%=id %>', '<%=dcount %>','<%=empId %>','<%=appFreqId %>');" >
			<i class="fa fa-toggle-off" aria-hidden="true" title="Waiting for publish"></i></a>
			<%-- <img src="<%=request.getContextPath()%>/images1/icons/icons/unpublish_icon_b.png" title="Waiting to be publish" /></a> --%>
			::::
			<a target=new  href="AppraisalSummary.action?id=<%=id%>&appFreqId=<%=appFreqId %>" title="Edit Review" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
			::::
			 <%-- <img src="<%=request.getContextPath()%>/images1/icons/pending.png"/> --%>
			 <i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"></i>
			 
		
	<%}%>
		
<%--		
	} else if((operation != null || !operation.equals("")) && operation.equalsIgnoreCase("E")) {
		if(is_publish == false) {
%>
			<%--<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to unpublish this appraisal?'))
			getContent('myDivM<%=dcount %>','PublishAppraisal.action?id=<%=id %>&dcount=<%=dcount %>');" >
			<img src="<%=request.getContextPath()%>/images1/icons/icons/publish_icon_b.png" title="Published" /></a>
			 %>
			<a target=new  href="AppraisalSummary.action?id="<%=id%> onclick="if(Unpublish this review To Edit,Click OK to proceed...!'))
			getContent('myDivE<%=dcount %>','PublishAppraisal.action?id=<%=id %>&dcount=<%=dcount %>&operation=E');" >
			<img src="<%=request.getContextPath()%>/images1/icons/icons/edit_icon.png" title="Edit Review" /></a>
	<% } else { %>
			<%--<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this appraisal?'))
			getContent('myDivM<%=dcount %>','PublishAppraisal.action?id=<%=id %>&dcount=<%=dcount %>');" >
			<img src="<%=request.getContextPath()%>/images1/icons/icons/unpublish_icon_b.png" title="Waiting to be publish" /></a>
			-%>
			<a target=new  href="AppraisalSummary.action?id=<%=id%>">
			<img src="<%=request.getContextPath()%>/images1/icons/icons/edit_icon.png" title="Edit Review" /></a>
<%			
		}
	}
%>    --%>