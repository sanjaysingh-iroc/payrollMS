<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<% 
	List<String> exchangeCurrList = (List<String>) request.getAttribute("exchangeCurrList");
	String currencyType = (String) request.getAttribute("currencyType");
	//String proCurrency = (String) request.getAttribute("proCurrency");
	String type = (String) request.getAttribute("type");
	String operation = (String) request.getAttribute("operation");
	//System.out.println("exchangeCurrList ====>>> " + exchangeCurrList);
%>

<% if(type != null && type.equals("V")) { %> 
	<input type="hidden" name="currencyType" id="currencyType" value="<%=currencyType %>"/>
	<%-- <input type="hidden" name="proCurrency" id="proCurrency" value="<%=proCurrency %>"/> --%>
	<input type="hidden" name="longCurrency" id="longCurrency" value="<%=exchangeCurrList.get(3) %>"/>
	Exchange value 1<%=exchangeCurrList.get(2) %> = <input type="text" name="exchangeValue" id="exchangeValue" style="width: 60px !important;" value="<%=exchangeCurrList.get(4) %>"
	<%if(operation != null && operation.equals("E")) { %> readonly="readonly" <% } %> > <%=exchangeCurrList.get(0) %> 
	<%if(operation == null || !operation.equals("E")) { %>
	<input type="button" class="btn btn-primary" name="save" value="Update Currency" onclick="updateExchangeValue();"/><br/>
	<span style="font-size: 11px; font-style: italic;">Last updated by - <%=exchangeCurrList.get(5) %> on - <%=exchangeCurrList.get(6) %>.</span>
	<% } %>
<% } else if(type != null && type.equals("ExV")) { 
	if(exchangeCurrList != null && exchangeCurrList.size()> 0) {
%>
	<input type="hidden" name="exchangeValueHide" id="exchangeValueHide" value="<%=exchangeCurrList.get(0) %>"/>
	<%-- <input type="hidden" name="currencyType" id="currencyType" value="<%=currencyType %>"/>
	<input type="hidden" name="longCurrency" id="longCurrency" value="<%=exchangeCurrList.get(1) %>"/> --%> 
	<%=exchangeCurrList.get(0) %>
<% }
} %>