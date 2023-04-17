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
		<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to Unpublish this one-to-one?'))
		getPublishOneToOne('<%=id %>');" >
		<img src="<%=request.getContextPath()%>/images1/icons/icons/publish_icon_b.png" title="Published" /></a>
		::::
		<a target=new  href="OneToOneSummary.action?id=<%=id%>" onclick="if(confirm('Unpublish this one-to-one To Edit,Click OK to proceed...!'))
		getPublishOneToOne('<%=id %>');" title="Edit one-to-one"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
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
	
				<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to Publish this one-to-one'))
				getPublishOneToOne('<%=id %>');" >
				<img src="<%=request.getContextPath()%>/images1/icons/icons/unpublish_icon_b.png" title="Waiting to be publish" /></a>
				::::
				<a target=new  href="OneToOneSummary.action?id=<%=id%>" title="Edit one-to-one" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
				::::
				 <%-- <img src="<%=request.getContextPath()%>/images1/icons/pending.png"/> --%>
				 <i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"></i>
				 
			
	<%}%>