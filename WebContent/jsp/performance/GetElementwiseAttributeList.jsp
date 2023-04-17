<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% String count = (String)request.getAttribute("count"); %>
    <select name="elementAttribute" id="elementAttribute<%=count %>" style="width: 130px;"><option value="">Select Attribute</option>
    <%=request.getAttribute("attributeOptions") %>
    </select>