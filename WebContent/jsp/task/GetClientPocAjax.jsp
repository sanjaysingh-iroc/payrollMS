<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@taglib prefix="s" uri="/struts-tags" %>


<% 
	UtilityFunctions uF = new UtilityFunctions();
	String clientBrandId = (String)request.getAttribute("clientBrandId"); 
	Boolean brandFlag = (Boolean)request.getAttribute("brandFlag");
%>
<% if(uF.parseToInt(clientBrandId)>0) { %>
	<s:select theme="simple" name="clientPoc" id="clientPoc" listKey="clientPocId" cssClass="validateRequired" cssStyle="width:390px" listValue="clientPocName" headerKey="" 
		headerValue="Select SPOC" list="clientPocList" key="" required="true" /> <!--   -->
<% } else { %>
	<% if(brandFlag) { %> <!--  -->
		<s:select theme="simple" label="Select Subsidiary/ Brand" name="strClientBrand" id="strClientBrand" cssClass="validateRequired" listKey="clientBrandId"
			headerKey="" headerValue="Select Subsidiary/ Brand" listValue="clientBrandName" list="clientBrandList" onchange="getContent('clientPocTD', 'GetClientPocAjax.action?strClientBrand='+this.value)" />
		::::
	<% } %> <!-- -->
	<s:select theme="simple" name="clientPoc" id="clientPoc" listKey="clientPocId" cssClass="validateRequired" cssStyle="width:390px" listValue="clientPocName" headerKey="" 
		headerValue="Select SPOC" list="clientPocList" key="" required="true" />
<% } %>