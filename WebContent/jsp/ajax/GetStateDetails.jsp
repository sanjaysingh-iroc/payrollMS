<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<s:if test="type=='org'">
<s:select theme="simple" list="stateList" name="orgState" listKey="stateId" listValue="stateName" headerKey="" headerValue="Select State" 
		cssClass="validateRequired"/>
</s:if>
<s:elseif test="type=='location'">
<s:select  theme="simple" id="state" cssClass="validateRequired" name="state" listKey="stateId" listValue="stateName" 
		headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
</s:elseif>

<s:elseif test="type=='locationBilling'">
<s:select  theme="simple" id="billingState" cssClass="validateRequired" name="billingState" listKey="stateId" listValue="stateName" 
		headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
</s:elseif>

<s:elseif test="type=='employee'">
<s:select theme="simple" title="state" cssClass="validateRequired" id="state" name="state" listKey="stateId" listValue="stateName"
		 headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
</s:elseif>
<s:elseif test="type=='employee1'">
<s:select theme="simple" title="state" cssClass="validateRequired" id="stateTmp" name="stateTmp" listKey="stateId" listValue="stateName"
 		headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
</s:elseif>
<s:elseif test="type=='candidate'">
<s:select theme="simple" title="state" cssClass="validateRequired" id="state" name="state" listKey="stateId" listValue="stateName" 
		headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
</s:elseif>
<s:elseif test="type=='candidate1'">
<s:select theme="simple" title="state" cssClass="validateRequired" id="stateTmp" name="stateTmp" listKey="stateId" listValue="stateName" 
		headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
</s:elseif>
<s:elseif test="type=='bank'">
<s:select theme="simple" title="bankState" cssClass="validateRequired" id="bankState" name="bankState" listKey="stateId" listValue="stateName" 
		headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
</s:elseif>
<s:elseif test="type=='bankBranch'">
	<s:select name="bankState" list="stateList" cssClass="validateRequired" listKey="stateId" listValue="stateName" headerKey="" headerValue="Select State"/>
</s:elseif>