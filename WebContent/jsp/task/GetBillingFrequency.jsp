<%@ taglib prefix="s" uri="/struts-tags"%>

<s:select theme="simple" name="strBillingKind" id="strBillingKind" listKey="billingId" cssClass="validateRequired" headerKey="" headerValue="Select Billing Frequency" 
		listValue="billingName" list="billingKindList" key="" required="true" onchange="hideShowMilestone(this.value, '');"/>