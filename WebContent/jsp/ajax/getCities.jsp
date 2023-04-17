<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="cityList != null">
<s:select theme="simple" label="Select City" name="city" listKey="cityId"
		listValue="cityName" headerKey="0" headerValue="Select Suburb"		
		list="cityList" key="" required="true" /><span class="hint">Select Suburb.<span class="hint-pointer">&nbsp;</span></span>
</s:if> 
