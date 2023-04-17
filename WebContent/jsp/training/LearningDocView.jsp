<%@page import="com.konnect.jpms.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script src="scripts/ckeditor_cust/ckeditor.js"></script>

<div id="divResult">
	<div class="box-body" style="padding: 5px; overflow-y: auto;">
		<iframe width="100%" height="100%" src="https://docs.google.com/gview?url=<%=request.getAttribute("strCourseContent") %>&embedded=true" id="contentIframe" sandbox="allow-scripts allow-same-origin"></iframe>
	</div>
</div>