<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();
String strAmount = (String)request.getAttribute("GratuityAmount");
String strEmpId = (String)request.getAttribute("EMPID");
String paycycle = (String)request.getAttribute("paycycle");

if(paycycle!=null && (strAmount!=null && uF.parseToDouble(strAmount)>0)){
	out.println(strAmount);
}else{
	out.println("Please enter the valid amount.");
	out.println("<div id=\"myDiv"+strEmpId+"\"> <form name=\"frm"+strEmpId+"\"><input style=\"height:20px; width:100px\" type=\"text\" name=\"strGratuity\" /> <input type=\"button\" value=\"Pay\" class=\"input_button\" onclick=\"getContent('myDiv"+strEmpId+"', 'SetGratuity.action?EMPID="+strEmpId+"&AMOUNT='+document.frm"+strEmpId+".strGratuity.value+'&FYS="+CF.getStrFinancialYearFrom()+"&FYE="+CF.getStrFinancialYearTo()+"');\" /> </form></div>");
}

%>