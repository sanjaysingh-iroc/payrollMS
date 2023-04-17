<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="DA Increment Slabs" name="title"/>
</jsp:include>


<%
UtilityFunctions uF = new UtilityFunctions();
Map hmIncrementReport = (Map)request.getAttribute("hmIncrementReport"); 

//out.println(hmOfficeTypeMap);

%>



<div id="printDiv" class="leftbox reportWidth">

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>		
		
<div style="float:left; margin:0px 0px 10px 0px"> <a href="AddIncrementDA.action" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Slab</a></div>  
<div class="clr"></div>


<div>
         <ul class="level_list">

		
		<% 
			Set setIncrementMap = hmIncrementReport.keySet();
			Iterator it = setIncrementMap.iterator();
			int count=0;
			while(it.hasNext()){
				String strIncrementId = (String)it.next();
				List alIncrement = (List)hmIncrementReport.get(strIncrementId);
				if(alIncrement==null)alIncrement=new ArrayList();
				count++;
					
					
					%>
					
					<li>
					<a href="AddIncrementDA.action?operation=D&ID=<%=strIncrementId%>" class="del" onclick="return confirm('Are you sure you wish to delete this slab?')"> - </a> <a href="AddIncrementDA.action?operation=E&ID=<%=strIncrementId%>" class="edit_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })">Edit</a> 
					Increment From: <strong><%=alIncrement.get(1) %></strong>&nbsp;&nbsp;&nbsp;
					Increment To: <strong><%=alIncrement.get(2) %></strong>&nbsp;&nbsp;&nbsp;
					Increment Amount: <strong><%=alIncrement.get(3) %></strong>&nbsp;&nbsp;&nbsp;
					Type: <strong><%=alIncrement.get(4) %></strong>&nbsp;&nbsp;&nbsp;
					Payable Month: <strong><%=alIncrement.get(5) %></strong>&nbsp;&nbsp;&nbsp;
					<p style="font-size: 10px; padding-left: 42px; font-style: italic;">	Last updated by <%=uF.showData((String)alIncrement.get(5), "N/A")%> on <%=uF.showData((String)alIncrement.get(6),"")%></p>
					
					</li> 
					
		<%
			}
		%>
		 
		 </ul>
         
     </div>	
		
	</div>
