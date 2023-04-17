<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Leave Application</title>
<s:head />
</head>
<body>
	<h4>Leave Application</h4>
	
	<s:form action="LeaveApplication" method="POST" >
          <s:textfield name="leaveFrom" label="Leave From" />
          <s:textfield name="leaveTo" label="Leave To" />
          <s:textarea  name="reason" label="Reason" />
          <s:div cssClass="login_submit:hover">
          <s:submit cssClass="input_button" value="Apply Leave" align="center"/>
          </s:div>
    </s:form>
</body>
</html>  