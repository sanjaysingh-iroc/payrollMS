
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% 	
	String from = (String)request.getAttribute("from");
	String type = (String)request.getAttribute("type");
%>

<%if(from.equals("AddPRate")) { %>
	<%=(String)request.getAttribute("OrgCurrency") %>
<% } else if(from != null && from.equals("AddPro") && type != null && type.equals("R")) { %>
	<s:select theme="simple" id="strCurrency" cssClass="validateRequired" name="strCurrency" listKey="currencyId" listValue="currencyName"
	headerKey="" headerValue="Select Currency" list="currencyList" key="" required="true" />
<% } else if(from != null && from.equals("AddPro") && type != null && type.equals("B")) { %>
	<s:select theme="simple" id="strBillingCurrency" cssClass="validateRequired" name="strBillingCurrency" listKey="currencyId" listValue="currencyName"
	headerKey="" headerValue="Select Billing Currency" list="currencyList" required="true" onchange="getExchangeValue(this.value);"/>
<% } %>


