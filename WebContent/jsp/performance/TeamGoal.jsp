<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<script type="text/javascript">
jQuery(document).ready(function() {	
		
	  jQuery(".content1").hide();
	  //toggle the componenet with class msg_body
	  jQuery(".heading_dash").click(function()
	  {
	    jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("close_div"); 
	  });
	});
</script>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Team Goal" name="title" />
</jsp:include>
<%
	Map<String, List<List<String>>> hmTeam = (Map<String, List<List<String>>>) request.getAttribute("hmTeam");

	UtilityFunctions uF=new UtilityFunctions();
%>

<div class="leftbox reportWidth">
<div class="clr"></div>
<%
if(!hmTeam.isEmpty()){
Iterator<String> it=hmTeam.keySet().iterator();
while(it.hasNext()){
	String key = it.next();
	List<List<String>> outerList=hmTeam.get(key);
	
	for(int i=0;outerList!=null && i<outerList.size();i++){
		List<String> innerList=outerList.get(i);
%>
<div style="margin:10px 0px 0px 0px ;float:left; width:100%">
<p class="past heading_dash" style="text-align:left;padding-left:35px;">Team Goal</p>
					   <div class="content1">
         <ul class="level_list">
							
					<li>
					<div style="float: left; background-repeat: no-repeat; background-position: right top;">
					<p style="margin:0px 0px 0px 50px"><strong><%=innerList.get(3) %></strong></p>
					<p style="margin:0px 0px 0px 50px">						
	                    Objective:<strong> <%=innerList.get(4) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Attribute:<strong> <%=innerList.get(6) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  					</p>
  					<p style="margin:0px 0px 0px 50px">Description:<strong><%=innerList.get(5) %></strong></p>
                   	<p style="margin:0px 0px 0px 50px">                
	                    Measure Efforts Days & Hrs:<strong><%=innerList.get(9) %>&nbsp;Days&nbsp;<%=innerList.get(10) %>&nbsp;Hrs</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Due Date:<strong><%=innerList.get(16) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Weightage:<strong><%=innerList.get(19) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    				</p>
    				<p style="margin:0px 0px 0px 50px">                
	                    Assigned To:&nbsp;<strong><%=innerList.get(20) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    
    				</p>  
    				 
    				             
                    </div>
                    
                    <div class="clr"></div>
                
                 </li>
		 
		 </ul>
         
     </div>
     </div>
     <%}
	} %>
	<%
		} else {
	%>
	<div class="nodata msg">No Team Goal assigned</div>
	<%
		}
	%>
     
</div>
