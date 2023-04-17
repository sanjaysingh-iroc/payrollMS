<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Leave Policies" name="title"/>
</jsp:include>


<%
UtilityFunctions uF = new UtilityFunctions();
Map hmLevelMap = (Map)request.getAttribute("hmLevelMap"); 
Map hmLeavePoliciesMap = (Map)request.getAttribute("hmLeavePoliciesMap");

String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
if(strAction!=null){
	strAction = strAction.replace(request.getContextPath()+"/","");
}
%>

<div id="printDiv" class="leftbox reportWidth">

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>	

<div class="filter_div">
<div class="filter_caption">Select Organisation</div>
<s:form name="frm" action="LeaveTypeReport" theme="simple">
	<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
	<s:select theme="simple" name="strLocation" listKey="wLocationId" listValue="wLocationName" list="workList" onchange="document.frm.submit();"/>
	<s:hidden name="type" value="level"></s:hidden>
</s:form>
</div>
	 
<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>		
<div style="float:left; margin:0px 0px 10px 0px"> <a href="AddLeaveType.action?orgId=<%=request.getAttribute("strOrg") %>&strLocation=<%=request.getAttribute("strLocation") %>" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Leave Type</a></div>
<%} %>

<div style="float:right; margin:0px 0px 10px 0px"> <a href="LeaveTypeReport.action?strOrg=<%=request.getAttribute("strOrg") %>&strLocation=<%=request.getAttribute("strLocation") %>"> Leave Policies Leave Type wise</a></div>
  
<div class="clr"></div>

<div>
         <ul class="level_list">

		
		<% 
			Set setLevelMap = hmLevelMap.keySet();
			Iterator it = setLevelMap.iterator();
			
			while(it.hasNext()){
				String strLevelId = (String)it.next();
				List alLeaveType = (List)hmLevelMap.get(strLevelId);
				if(alLeaveType==null)alLeaveType=new ArrayList();
				
					
					List alLeavePolicy = (List)hmLeavePoliciesMap.get(strLevelId);
					if(alLeavePolicy==null)alLeavePolicy=new ArrayList();
					%>
					
					<li>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
					<a href="AddLevel.action?operation=D&ID=<%=strLevelId%>&URI=<%=strAction%>" class="del" title="Delete Leave Policy"  onclick="return confirm('Are you sure you wish to delete this level?')"> - </a> 
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>
					<a href="AddLevel.action?orgId=<%=request.getAttribute("strOrg") %>&operation=E&ID=<%=strLevelId%>&URI=<%=strAction%>&strLocation=<%=request.getAttribute("strLocation") %>" class="edit_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax', width:700,height:300 })" title="Edit Level">Edit</a> 
					<%} %>
					
					
					
					<strong><%=alLeaveType.get(2)%> [<%=alLeaveType.get(1)%>]</strong>&nbsp;&nbsp;
					<ul>		
					<li class="addnew desgn">
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>
					<a href="EmployeeIssueLeave.action?orgId=<%=request.getAttribute("strOrg") %>&param=<%=alLeaveType.get(0) %>&strLocation=<%=request.getAttribute("strLocation") %>" onclick="return hs.htmlExpand(this, { objectType: 'ajax',  width:1110,height:700  })"> + Add New Leave Policy</a>
					<%} %>
					</li>
					
					<%
						for(int d=0; d<alLeavePolicy.size(); d+=14){
						String strPolicyId = (String)alLeavePolicy.get(d);
						
					%>  
					
					<li>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %> 
                    <a href="EmployeeIssueLeave.action?operation=D&ID=<%=strPolicyId%>" class="del" onclick="return confirm('Are you sure you wish to delete this policy?')"> - </a>
                    <%} %>
                    <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> 
                    <a href="EmployeeIssueLeave.action?orgId=<%=request.getAttribute("strOrg") %>&param=<%=alLeavePolicy.get(d+2)%>&operation=E&ID=<%=strPolicyId%>&strLocation=<%=request.getAttribute("strLocation") %>" class="edit_lvl" onclick="return hs.htmlExpand(this, {objectType: 'ajax' , width:1110,height:700 })">Edit</a>
                    <%} %> 
                    
                    <%-- <a href="LeaveDescription.action?LID=<%=alLeavePolicy.get(d+1)%>" class="edit_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax' })">Edit</a> --%>
                    
                     Leave Type: <strong><%=alLeavePolicy.get(d+2)%></strong> &nbsp;&nbsp;&nbsp;
                     No. of Leaves: <strong><%=alLeavePolicy.get(d+3)%></strong> &nbsp;&nbsp;&nbsp;
                     Leave Calculation from: <strong><%=alLeavePolicy.get(d+4)%></strong> &nbsp;&nbsp;&nbsp;
                     Is Paid: <strong><%=alLeavePolicy.get(d+5)%></strong> &nbsp;&nbsp;&nbsp;
                     Is Carry Forward: <strong><%=  alLeavePolicy.get(d+6)%></strong>  
                     Monthly Leave Limit: <strong><%=uF.showData((String)alLeavePolicy.get(d+7), "0")%></strong> &nbsp;&nbsp;&nbsp;
                     Consecutive Leave Limit: <strong><%=uF.showData((String)alLeavePolicy.get(d+8), "0")%></strong> &nbsp;&nbsp;&nbsp; 
                     Is Monthly Carry Forward: <strong><%=  alLeavePolicy.get(d+9)%></strong>
	                     
                      <p style="font-size: 10px; padding-left: 42px; font-style: italic;">	Last updated by <%=uF.showData((String)alLeavePolicy.get(d+10), "N/A")%> on <%=uF.showData((String)alLeavePolicy.get(d+11),"")%></p>
                    </li>
						
				<%
					}
				%>		
					
                 
                 </ul>
                 </li> 
		<%
			}
		%>
		 
		 </ul>
         
     </div>	
		
		
		
		
		
		
		
		
	</div>

	