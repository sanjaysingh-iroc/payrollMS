<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@page import="java.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
 

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Broadcast Message" name="title"/>
</jsp:include>


<div class="reportWidth">

 <s:form action="BroadcastMessage.action" method="post" theme="simple">

    <div class="leftbox">
          
          <table>
          
          	<tr><td valign="top" class="label">Choose Employees</td>
          	<td class="label">
	          	<s:checkboxlist list="empList" listKey="employeeId" listValue="employeeName" name="strEmpId" />
          	</td></tr>
          	<tr><td class="label">Title</td><td><s:textfield name="strTitle" cssStyle="width:600px"></s:textfield></td></tr>
          	<tr><td class="label" valign="top">Message</td><td><s:textarea name="strMessage" rows="10" cols="80"></s:textarea></td></tr>
          	<tr><td colspan="2" align="right"><s:submit value="Send Message" cssClass="input_button"></s:submit></td></tr>
          </table>
     </div>   
                      
   </s:form>                      


</div>