<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<select id="prevCompanyState" style="width:189px;" name="prevCompanyState">
<option value="">Select State</option>
<%=(String)request.getAttribute("states") %>
</select>