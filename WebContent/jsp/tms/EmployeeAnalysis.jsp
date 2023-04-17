<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>



<%!String showData(String strData, String strVal) {
		if (strData == null)
			return strVal;
		else
			return strData;
	}

	double dblPayAmount = 0.0;

	String showDataAdd(String strData) {
		if (strData == null) {
			return "0";
		} else {
			dblPayAmount += Double.parseDouble(strData);
			return strData;
		}
	}%>



<%
	String strUserType = (String) session.getAttribute("USERTYPE");

	List alDate = (List) request.getAttribute("_alDate");
	Map hmOuter = (Map) request.getAttribute("hmOuter");
	
	
	out.println("hmOuter="+hmOuter);
	out.println("alDate="+alDate);

%>




<div class="aboveform">
<h4 class="alignLeft" style="padding-left: 50px">Employee Timesheet Analysis</h4>



<table cellpadding="2" cellspacing="1" align="left"
	style="padding-left: 50px">

	<tr>

		<td>&nbsp;</td>

		<%
			for (int i = 0; i < alDate.size() ; i++) {
		%>
		<td class="reportHeading alignCenter"><%=(String) alDate.get(i)%></td>
		<%
			}
		%>

		<td>&nbsp;</td>
	</tr>

	

</table>



</div>