<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

	    <%
	    	List<List<String>> alEmpList = (List<List<String>>)request.getAttribute("alEmpList");
	    	int i=0;
	    	if(alEmpList.size() != 0) { %>
	    		<select  name="strArrearEmpId" id="strArrearEmpId" multiple="multiple" class="validateRequired">
		    		<% for(i=0; i<alEmpList.size(); i++) {
			    		List<String> alInner = alEmpList.get(i); %>
						<option value="<%=alInner.get(0) %>"><%=alInner.get(1) %></option>
			    	<% } %>
		    	</select>
	    	<% } else { %>
	    		<div class="nodata"><span>No Employee found for the current selection.</span></div>
	    	<% } %>
	    	