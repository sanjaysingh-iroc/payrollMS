<%@page import="com.konnect.jpms.select.FillPayCycles"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%> 
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


    <script>
      function sendTimesheet(empid, datefrom, dateto, downloadSubmit) {
    	  	var date = document.frm.task_date.value;
    		window.location = 'GenerateTimeSheet1.action?mailAction=sendMail&empid='+ empid+'&datefrom='+datefrom+'&dateto='+dateto+'&downloadSubmit='+downloadSubmit;	  
		}  
    </script>
    
    <%
    List<String> alReport = (List<String>)request.getAttribute("alReport");
    %>
    
    
    <table class="tb_style" width="100%">
    <%
    for(int i=0; i<alReport.size(); i++){
    %>
	    <tr>
	    	<td>
	    		<%=alReport.get(i++) %>
	    	</td>
	    	<td><%=alReport.get(i) %></td>
	    </tr>
	<%
    }
    %>
	    
    </table>