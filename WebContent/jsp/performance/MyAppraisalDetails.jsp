<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@taglib prefix="s" uri="/struts-tags"%>
</head>
<body>
	<div class="leftbox reportWidth">
		<%
			List<List<String>> appraiselQuestionList = (List<List<String>>) request
					.getAttribute("appraiselQuestionList");
			List<List<String>> appraiselAnswerList = (List<List<String>>) request
					.getAttribute("appraiselAnswerList");
			Map<String, List<String>> mp = (Map<String, List<String>>) request
					.getAttribute("mp");
			UtilityFunctions uF = new UtilityFunctions();
			Map<String,String> appraisalMp=(Map<String,String> )request.getAttribute("appraisalMp");
			String orient=appraisalMp.get("ORIENTATION");
			//Map<String,Boolean> approvalList=(Map<String,Boolean> )request.getAttribute("approvalList");
			String empId=(String)request.getAttribute("empId");
			boolean flag=(Boolean)request.getAttribute("flag");

		%>
		<s:form theme="simple" action="SubmitAppraisalQuestions" method="POST">
			<table class="tb_style">
				<s:hidden name="id"></s:hidden>
				<s:hidden name="empId"></s:hidden>
				<s:hidden name="profile" />
				<%
					for (int i = 0; i < appraiselQuestionList.size(); i++) {
							List<String> innerList = appraiselQuestionList.get(i);
							
							List<String> innerList1 = mp.get(innerList.get(0));
							//System.out.println("innerList.get(0) "+innerList.get(0)+" innerList.get(1) "+innerList.get(1)+" innerList1.get(1) "+innerList1.get(1));
							
				%>

				<tr>
					<th><%=innerList.get(1)%></th>
				</tr>
				<%
					if (uF.parseToInt(innerList.get(2)) == 1) {
						
				%>
				<tr>
					<td><input type="radio" name="answer<%=i%>"
					<% if(innerList1 !=null && innerList1.get(1)!=null && innerList1.get(1).contains("a") ){ %>
					checked
					<%} %>
					 value="a" /><%=innerList.get(3)%></td>
					<td><input type="radio" name="answer<%=i%>"
					<% if(innerList1 !=null && innerList1.get(1)!=null && innerList1.get(1).contains("b") ){ %>
					checked
					<%} %> value="b" /><%=innerList.get(4)%></td>
					<td><input type="radio" name="answer<%=i%>"
					<% if(innerList1 !=null && innerList1.get(1)!=null && innerList1.get(1).contains("c") ){ %>
					checked
					<%} %>
					 value="c" /><%=innerList.get(5)%></td>
					<td><input type="radio" name="answer<%=i%>" 
					<% if(innerList1 !=null && innerList1.get(1)!=null && innerList1.get(1).contains("d") ){ %>
					checked
					<%} %>value="d" /><%=innerList.get(6)%></td>
				</tr>
				<%
					} else if (uF.parseToInt(innerList.get(2)) == 2) {
				%>
				<tr>
					<td><input type="checkbox" name="answer<%=i%>"
					<% if(innerList1 !=null && innerList1.get(1)!=null && innerList1.get(1).contains("a") ){ %>
					checked
					<%} %>
					 value="a" /><%=innerList.get(3)%></td>
					<td><input type="checkbox" name="answer<%=i%>" 
					<% if(innerList1 !=null && innerList1.get(1)!=null && innerList1.get(1).contains("b") ){ %>
					checked
					<%} %>value="b" /><%=innerList.get(4)%></td>
					<td><input type="checkbox" name="answer<%=i%>"
					<% if(innerList1 !=null && innerList1.get(1)!=null && innerList1.get(1).contains("c") ){ %>
					checked
					<%} %> value="c" /><%=innerList.get(5)%></td>
					<td><input type="checkbox" name="answer<%=i%>" 
					<% if(innerList1 !=null && innerList1.get(1)!=null && innerList1.get(1).contains("d") ){ %>
					checked
					<%} %>
					value="d" /><%=innerList.get(6)%></td>
				</tr>
				<%
					} else if (uF.parseToInt(innerList.get(2)) == 3) {
				%>
				<tr>
					<td><textarea rows="4" cols="20" name="answer<%=i%>"><% if(innerList1 !=null && innerList1.get(1)!=null){ %><%=innerList1.get(1)%><%} %></textarea>
					</td>
				</tr>
				<%
					}
				%>
				<%
					}
				%>

				<tr>
				<%if(flag){ %>
					<td><s:submit value="Submit Question" cssClass="input_button"></s:submit></td>
					<%} %>
					
				</tr>

			</table>
		</s:form>
	</div>
</body>
</html>