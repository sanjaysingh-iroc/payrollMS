<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
	if (request.getAttribute("viewEmpLeaveBalaace") != null) {
		out.println(request.getAttribute("viewEmpLeaveBalaace"));
	}
%>
  