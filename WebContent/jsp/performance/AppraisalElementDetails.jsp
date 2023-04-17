<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

</head>
<body>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Appraisal Element Details" name="title" />
</jsp:include>
<div class="leftbox reportWidth">

<s:hidden name="operation" value="A"></s:hidden>
<!-- <table class="tb_style"> -->
<div>
          <ul class="level_list">
<%List<List<String>> outerList=(List<List<String>>)request.getAttribute("outerList");
List<List<String>>  memberList=(List<List<String>>  )request.getAttribute("innerList");
Map<String,String> mp=(Map<String,String>)request.getAttribute("mp");
for(int i=0;i<outerList.size();i++){
	List<String> innerList=outerList.get(i); %>
	<li>
	<a href="AddAppraisalElement.action?operation=A&ID=<%=innerList.get(0)%>" class="edit_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax', width:400 })"> Change Setting </a>
	<strong><%=innerList.get(1)%></strong> 
       <ul>
       <%for(int j=0;j<memberList.size();j++){
		List<String> memberInner=memberList.get(j);
		%>
		<%if(mp.get( memberInner.get(0)+"element"+innerList.get(0))!=null){ %>
	<li class="addnew desgn">
	
	<%= memberInner.get(1)%>
	</li>
	<%} %>
	
	
	
	 
	<%} %>
       
       </ul>  
         
	
	
	
	</li>
<%}

%>
<!-- </table> -->
</ul>
         </div>
</div>

         
         
</body>
</html>