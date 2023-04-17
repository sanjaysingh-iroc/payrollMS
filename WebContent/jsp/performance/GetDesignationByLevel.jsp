<%@ taglib prefix="s" uri="/struts-tags"%>

	<s:select theme="simple" name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
      headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();" cssStyle="width:150px;"></s:select>
