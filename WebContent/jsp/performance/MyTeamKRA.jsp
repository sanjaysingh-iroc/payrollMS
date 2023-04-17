
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My Team KRAs" name="title" />
</jsp:include>


<div class="leftbox reportWidth">

	<table class="tb_style" width="100%">
		<tr>
			<th>Employee Name</th>
			<th>Emp Code</th>
			<th>Designation</th>
			<th>KRA</th>
			<th>Description</th>
			<th>Progress</th>
		</tr>

	</table>

</div>

