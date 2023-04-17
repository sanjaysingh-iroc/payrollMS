<%@ taglib prefix="s" uri="/struts-tags"%>
<script>

</script>
<% if(request.getAttribute("page") != null && request.getAttribute("page").equals("SOrient")) { %>
	<s:select name="employeeOrient" list="empList" theme="simple" id="employeeOrient" listKey="employeeId" listValue="employeeCode" required="true" multiple="true" size="4"></s:select>
<% } else { %>
	<s:select name="employee" list="empList" theme="simple" cssClass="validateRequired form-control" id="employee" listKey="employeeId" listValue="employeeCode" required="true" multiple="true" size="4" onchange="createRevieweePanelForReview('employee');" ></s:select>
<% } %>