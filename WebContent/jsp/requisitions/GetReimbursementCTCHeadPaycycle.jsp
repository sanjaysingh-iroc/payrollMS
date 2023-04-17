<%@ taglib prefix="s" uri="/struts-tags"%>
<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" 
						list="paycycleList" key="" cssClass="validateRequired" multiple="true"/>
<s:hidden name="limitAmount" id="limitAmount"></s:hidden>