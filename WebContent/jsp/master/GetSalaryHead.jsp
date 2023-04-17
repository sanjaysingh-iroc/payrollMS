<%@page import="com.konnect.jpms.select.FillSalaryHeads"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<select rel="7" name="strSalaryHead" id="strSalaryHead" multiple="multiple" size="4">
					<% java.util.List  salaryHeadList = (java.util.List) request.getAttribute("salaryHeadList"); %>
					<% for (int i=0; salaryHeadList!=null && !salaryHeadList.isEmpty() &&  i<salaryHeadList.size(); i++) { %>
					<option value=<%= ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId() %>> <%= ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName() %></option>
					<% } %>
				</select>
				<span class="hint">Salary Type<span class="hint-pointer">&nbsp;</span></span>