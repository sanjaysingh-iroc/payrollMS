<%
	boolean is_publish =(Boolean)request.getAttribute("is_publish");
	String id=(String)request.getAttribute("id");
	String dcount=(String)request.getAttribute("dcount");
	//System.out.println("is_publish==>"+is_publish+"==>id==>"+id+"==>dcount==>"+dcount);
	if(is_publish==false){
%>
<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this learning plan?'))
						getContent('myDivM<%=dcount %>','PublishLearningPlan.action?id=<%=id %>&dcount=<%=dcount %>');" >
							<img src="<%=request.getContextPath()%>/images1/icons//icons/unpublish_icon_b.png" title="Waiting to be publish" /></a>
<% } else { %>
<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to unpublish this learning plan?'))
						getContent('myDivM<%=dcount %>','PublishLearningPlan.action?id=<%=id %>&dcount=<%=dcount %>');" >
						<img src="<%=request.getContextPath()%>/images1/icons/icons/publish_icon_b.png" title="Published" /></a>
<%}%>