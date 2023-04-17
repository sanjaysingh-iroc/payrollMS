<%@ taglib prefix="s" uri="/struts-tags"%>


  <s:if test="pagefrom=='addpanel'">
  	<s:select theme="simple" name="strDesignationUpdate" listKey="desigId"  id="desigIdV"
			listValue="desigCodeName" headerKey="" headerValue="Select Designation"  cssClass="validateRequired"
			list="desigList" key="" required="true" onchange="getEmployeebyDesig(this.value);"/>
  </s:if>
  <s:elseif test="pagefrom=='updateJobProfile'">
  		<s:select theme="simple" name="strDesignation" list="designationList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
			 headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();" cssStyle="width:150px;" cssClass="validateRequired"></s:select>
  </s:elseif>
  <s:elseif test="pagefrom=='RR'">
  		<s:select theme="simple" name="strDesignation1" list="designationList1" listKey="desigId" id="desigIdV1" listValue="desigCodeName"
			 headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig1();" cssStyle="width:150px;" cssClass="validateRequired"></s:select>
  </s:elseif>
  <s:else>
	<s:select theme="simple" name="strDesignationUpdate" listKey="desigId" listValue="desigCodeName" headerKey="" cssClass="validateRequired"
	 headerValue="Select Designation" list="desigList" key="" required="true" onchange="getGradebyDesig(this.value,'add');" />
			<!-- getIdealCandidateDetails(this.value); -->
</s:else>



    	