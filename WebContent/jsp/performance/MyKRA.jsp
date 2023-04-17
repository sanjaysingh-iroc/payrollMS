
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My KRAs" name="title" />
</jsp:include>


<div class="leftbox reportWidth">

	<table class="tb_style" width="100%">
		<tr>
			<th>KRA</th>
			<th>Description</th>
			<th>MBO</th>
			<th>Progress</th>
			<th>Star Rating</th>
			<th>Reviews</th>
		</tr>

	</table>

</div>

