<%@ taglib prefix="s" uri="/struts-tags"%>
<s:select theme="simple" name="strType" id="strType" listKey="perkTypeId" cssClass="validateRequired" listValue="perkTypeName" headerKey="" headerValue="Select Perk Policy"		
						list="typeList" key="" required="true" onchange="checkPerkPolicy();" />