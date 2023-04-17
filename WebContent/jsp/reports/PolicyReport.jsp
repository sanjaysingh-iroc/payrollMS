<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Business Rules" name="title"/>
</jsp:include>

<div id="printDiv" class="leftbox reportWidth">
<div class="pagetitle" style="font-size:12px">"These Business Rules are drawn from Policies that have been administered using the Software."</div>
		
<%
UtilityFunctions uF = new UtilityFunctions();
Map hmRosterPolicyReport = (Map)request.getAttribute("hmRosterPolicyReport");
Map hmRosterHDPolicyReport = (Map)request.getAttribute("hmRosterHDPolicyReport");

//out.println(hmOfficeTypeMap);

%>
		
  
<div class="clr"></div>

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

<div style="margin-bottom: 20px;" class="pagetitle">Exception Rules</div>
<div style="float:left;width:100%">
         <ul class="level_list">

		
		<% 
			Set setRosterPolicyMap = hmRosterPolicyReport.keySet();
			Iterator it = setRosterPolicyMap.iterator();
			int count=0;
			while(it.hasNext()){
				String strRosterPolicyId = (String)it.next();
				List alRosterPolicy = (List)hmRosterPolicyReport.get(strRosterPolicyId);
				if(alRosterPolicy==null)alRosterPolicy=new ArrayList();
				count++;
					
					
					%>
					
					<li>					 
					Anyone who <strong><%=(("IN".equalsIgnoreCase((String)alRosterPolicy.get(4)))?"comes":"leaves") %></strong> <strong><%=(("LATE".equalsIgnoreCase((String)alRosterPolicy.get(3)))?"late":"early") %></strong> by <strong><%=alRosterPolicy.get(1) %></strong> mins  <%=((alRosterPolicy.get(2)!=null && ((String)alRosterPolicy.get(2)).length()>0)?" will be asked "+"<strong>\""+alRosterPolicy.get(2) +"\"</strong>":" will not be asked a question ") %> and this <strong><%=(("YES".equalsIgnoreCase((String)alRosterPolicy.get(5)))?"needs to be approved":"does not need approval") %></strong> and is effective from <strong><%=alRosterPolicy.get(6) %></strong>   <strong> <%=(uF.parseToInt((String)alRosterPolicy.get(7))==1)?"[Enabled]":"[Disabled]" %></strong>
					</li> 
					
		<%
			}if(count==0){
				%>
				<div class="filter"><div class="msg nodata"><span>No exception rule has been set.</span></div></div>
				<%
				}
			%>
		
		 
		 </ul>
         
     </div>	
     
     
     
   <div class="clr"></div>

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
<div style="margin-bottom: 20px;" class="pagetitle">Halfday Policies</div>

<div style="float:left;width:100%">
         <ul class="level_list">

		
		<% 
			Set setRosterHDPolicyMap = hmRosterHDPolicyReport.keySet();
			it = setRosterHDPolicyMap.iterator();
			count=0;
			while(it.hasNext()){
				String strRosterHDPolicyId = (String)it.next();
				List alRosterHDPolicy = (List)hmRosterHDPolicyReport.get(strRosterHDPolicyId);
				if(alRosterHDPolicy==null)alRosterHDPolicy=new ArrayList();
				count++;
					
					
					%>
					
					<li>					 
					Anyone who <strong><%=(("IN".equalsIgnoreCase((String)alRosterHDPolicy.get(2)))?"comes late":"leaves early") %></strong> by <strong><%=alRosterHDPolicy.get(1) %></strong> mins  every <strong><%=alRosterHDPolicy.get(3)%> days</strong> in <strong><%=alRosterHDPolicy.get(4)%> months </strong> will be considered as <strong>unpaid halfday leave</strong> and is effective from <strong><%=alRosterHDPolicy.get(5) %></strong>
					</li> 
					
		<%
			}if(count==0){
				%>
				<div class="filter"><div class="msg nodata"><span>No halfday rule has been set.</span></div></div>
				<%
				}
			%>
		 </ul>
         
     </div>	  
     
     
     
     
     <%
     Map hmLeaveTypeMap = (Map)request.getAttribute("hmLeaveTypeMap");
     Map hmLeavePoliciesMap = (Map)request.getAttribute("hmLeavePoliciesMap");
     if(hmLeaveTypeMap==null)hmLeaveTypeMap = new HashMap();
     
     %>
     
     <div style="margin-bottom: 20px;" class="pagetitle">Leave Policies</div>
	<div style="float:left;width:100%">
         <ul class="level_list">
			
			<%Set set =  hmLeaveTypeMap.keySet();
				Iterator it1 = set.iterator();
				count = 0;
				while(it1.hasNext()){
					count++;
					String strLeaveTypeId = (String)it1.next();
					List alLeave = (List)hmLeaveTypeMap.get(strLeaveTypeId);
					List alLeaveDetails = (List)hmLeavePoliciesMap.get(strLeaveTypeId);
					if(alLeaveDetails==null)alLeaveDetails = new ArrayList();
					
					for(int i=0; i<alLeaveDetails.size(); i+=12){
						%>
						<li><strong><%= (String)alLeaveDetails.get(i+3)%></strong> <strong><%= ((uF.parseToBoolean((String)alLeaveDetails.get(i+5)))?"paid":"unpaid")%></strong> <strong><%= (String)alLeave.get(2)%></strong> are available for all employees who fall under level <strong><%= (String)alLeaveDetails.get(i+2)%></strong> and will be calcualted on <strong><%= (String)alLeaveDetails.get(i+4)%></strong> </li>
						<%
					}
				}
			if(count==0){
			%>
			<div class="filter"><div class="msg nodata"><span>No leave policy has been set.</span></div></div>
			<%
			}
		%>
		 </ul>
         
     </div>	
     
     
		
	</div>
