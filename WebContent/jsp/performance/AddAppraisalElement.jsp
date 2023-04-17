<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<title>Insert title here</title>
</head>
<body>

<div >

<%List<List<String>>  memberList=(List<List<String>>  )request.getAttribute("innerList");
Map<String,String> mp=(Map<String,String>)request.getAttribute("mp"); %>
<s:form method="POST" action="AddAppraisalElement">
<s:hidden name="operation" value="E"></s:hidden>
<s:hidden name="ID"></s:hidden>
<table style="width:27%">
<%for(int j=0;j<memberList.size();j++){
		List<String> memberInner=memberList.get(j);
		%>
		<tr><td style="text-align:right"><%= memberInner.get(1)%></td>
		<td style="text-align:center">
		<input type="checkbox" name="<%= memberInner.get(0)%>" value=""
		<%if(mp.get( memberInner.get(0))!=null){ %>
	
	checked="checked"
	
	<%} %>
	 />
	</td></tr>
	
	
	 
	<%} %>
	
	<tr><td colspan="2"><s:submit value="Change Setting" cssClass="input_button"> </s:submit>
	</td></tr>
</table>

</s:form>
</div>
</body>
</html>