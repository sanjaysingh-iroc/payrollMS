<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Manage Break Policies" name="title"/>
</jsp:include>


<%
UtilityFunctions uF = new UtilityFunctions();
Map hmLeaveTypeMap = (Map)request.getAttribute("hmLeaveTypeMap"); 
Map hmLeavePoliciesMap = (Map)request.getAttribute("hmLeavePoliciesMap");

//out.println("<br/>hmLeavePoliciesMap="+hmLeavePoliciesMap);
//out.println("<br/>hmLeaveTypeMap="+hmLeaveTypeMap);


%> 
 

<div id="printDiv" class="leftbox reportWidth">
 
<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

<div class="filter_div">
<div class="filter_caption">Filter</div>
<s:form name="frm" action="LeaveBreakTypeReport" theme="simple">
	<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
	<s:select theme="simple" name="strLocation" listKey="wLocationId" listValue="wLocationName" list="workList" onchange="document.frm.submit();"/>
</s:form>
</div>
		
<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>		
<div style="float:left; margin:0px 0px 10px 0px"> <a href="AddLeaveBreakType.action?orgId=<%=request.getAttribute("strOrg") %>&strLocation=<%=request.getAttribute("strLocation") %>" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Break Type</a></div>
<%} %>
<%-- <div style="float:right; margin:0px 0px 10px 0px"> <a href="LeaveBreakTypeReport.action?type=level&strOrg=<%=request.getAttribute("strOrg") %>&strLocation=<%=request.getAttribute("strLocation") %>"> Leave Policies Level wise</a></div> --%>  
<div class="clr"></div>

<div>
         <ul class="level_list">

		
		<% 
			Set setLevelMap = hmLeaveTypeMap.keySet();
			Iterator it = setLevelMap.iterator();
			
			while(it.hasNext()){
				String strLeaveTypeId = (String)it.next();
				List alLeaveType = (List)hmLeaveTypeMap.get(strLeaveTypeId);
				if(alLeaveType==null)alLeaveType=new ArrayList();
				
					
					List alLeavePolicy = (List)hmLeavePoliciesMap.get(strLeaveTypeId);
					if(alLeavePolicy==null)alLeavePolicy=new ArrayList();
					%>
					
					<li>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
					<a href="AddLeaveBreakType.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strLeaveTypeId%>&strLocation=<%=request.getAttribute("strLocation") %>" class="del" onclick="return confirm('Are you sure you wish to delete this leave type?\nAll leave policies associated will also be deleted.')"> - </a> 
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>
					<a href="AddLeaveBreakType.action?operation=E&ID=<%=strLeaveTypeId%>" class="edit_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax' })">Edit</a>
					<%} %>
					
					
					
					<strong><%=alLeaveType.get(2)%> [<%=alLeaveType.get(1)%>]</strong>&nbsp;&nbsp;<span style="background-color:<%=alLeaveType.get(3)%>;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
					<ul>		
					<li class="addnew desgn">
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>
					<a href="EmployeeIssueLeaveBreak.action?orgId=<%=request.getAttribute("strOrg") %>&param=<%=strLeaveTypeId %>&strLocation=<%=request.getAttribute("strLocation") %>" onclick="return hs.htmlExpand(this, { objectType: 'ajax', width:800,height:500 })"> + Add New Break Policy</a>
					<%} %>
					</li>
					
					<%
						for(int d=0; d<alLeavePolicy.size(); d+=8){
						String strPolicyId = (String)alLeavePolicy.get(d);
						
					%>  
					
					<li>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %> 
                    <a href="EmployeeIssueLeaveBreak.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strPolicyId%>&strLocation=<%=request.getAttribute("strLocation") %>" class="del" onclick="return confirm('Are you sure you wish to delete this policy?')"> - </a>
                    <%} %>
                    <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> 
                    <a href="EmployeeIssueLeaveBreak.action?orgId=<%=request.getAttribute("strOrg") %>&param=<%=strLeaveTypeId%>&operation=E&ID=<%=strPolicyId%>&strLocation=<%=request.getAttribute("strLocation") %>" class="edit_lvl" onclick="return hs.htmlExpand(this, {objectType: 'ajax' ,width:800,height:500})">Edit</a>
                    <%} %> 
                    
                    <a href="LeaveDescriptionBreak.action?LID=<%=alLeavePolicy.get(d+1)%>" class="designation_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax' })">Edit</a>
                    
                     Level: <strong><%=alLeavePolicy.get(d+2)%></strong> &nbsp;&nbsp;&nbsp;
                     
                     No. of Monthly Breaks: <strong><%=alLeavePolicy.get(d+3)%></strong> &nbsp;&nbsp;&nbsp;
                     Is Carry Forward: <strong><%=  alLeavePolicy.get(d+4)%></strong>  &nbsp;&nbsp;&nbsp;
                     Is Monthly Carry Forward: <strong><%=  alLeavePolicy.get(d+5)%></strong>
                     	
                      <p style="font-size: 10px; padding-left: 42px; font-style: italic;">	Last updated by <%=uF.showData((String)alLeavePolicy.get(d+6), "N/A")%> on <%=uF.showData((String)alLeavePolicy.get(d+7),"")%></p>
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

	