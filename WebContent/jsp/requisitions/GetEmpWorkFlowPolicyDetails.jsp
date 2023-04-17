<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
 <span style="margin-left: 120px;"><input type="hidden" name="policy_id" id="policy_id" value="<%=request.getAttribute("policy_id") %>"/>
		<a href="#?w=600" id="divid">
		<%if(request.getAttribute("divpopup")!=null){ 
			out.println(request.getAttribute("divpopup"));
		} %>
		</a>
		</span>
		<%
			if (request.getAttribute("reimbursementsD") != null) {
				out.println(request.getAttribute("reimbursementsD"));
			}
		%>