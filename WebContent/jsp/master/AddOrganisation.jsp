<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/customAjax.js"></script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready(function() { 
	showCodeFields();
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required', true);
		$(".validateNumber").prop('type','number');
		$(".validateNumber").prop('step','any');
		$(".validateEmail").prop('type', 'email'); 
	});
});
function getState(country) {
	var action= 'GetStateDetails.action?type=org&country_id=' + country;
	getContent('statetdid', action);
}


function sameAsCompanyInfo(obj) {
	
	if(obj.checked) {
		getState(document.getElementById("comCountry").value);
		
		window.setTimeout(function() {
			document.getElementById("idOrgName").value = document.getElementById("comName").value;
			document.getElementById("orgSubTitle").value = document.getElementById("comSubTitle").value;
			document.getElementById("orgDescription").value = document.getElementById("comDescription").value;
			document.getElementById("idOrgAddress").value = document.getElementById("comAddress").value;
			document.getElementById("idOrgCity").value = document.getElementById("comCity").value;
			document.getElementById("orgCountry").value = document.getElementById("comCountry").value;
			document.getElementById("orgState").value = document.getElementById("comState").value;
			document.getElementById("idOrgPincode").value = document.getElementById("comPincode").value;
			document.getElementById("idOrgContact1").value = document.getElementById("comContactNo").value;
			document.getElementById("orgFaxNo").value = document.getElementById("comFaxNo").value;
			document.getElementById("idOrgEmail").value = document.getElementById("comEmail").value;
			document.getElementById("orgWebsite").value = document.getElementById("comWebSite").value;
			document.getElementById("orgIndustry").value = document.getElementById("comIndustry").value;
			document.getElementById("orgCurrency").value = document.getElementById("comCurrency").value;
		}, 700);
		
	} else {
		getState('');
		document.getElementById("idOrgName").value = '';
		document.getElementById("orgSubTitle").value = '';
		document.getElementById("orgDescription").value = '';
		document.getElementById("idOrgAddress").value = '';
		document.getElementById("idOrgCity").value = '';
		document.getElementById("orgCountry").value = '';
		document.getElementById("orgState").value = '';
		document.getElementById("idOrgPincode").value = '';
		document.getElementById("idOrgContact1").value = '';
		document.getElementById("orgFaxNo").value = '';
		document.getElementById("idOrgEmail").value = '';
		document.getElementById("orgWebsite").value = '';
		document.getElementById("orgIndustry").value = '';
		document.getElementById("orgCurrency").value = '';
	}
}

function showCodeFields() {
	var obj = document.getElementById('isEmpCode');
	if(obj.checked) {
		document.getElementById('empCode1').style.display = 'table-row';
		document.getElementById('contractCode1').style.display = 'table-row';
		document.getElementById('empCode2').style.display = 'table-row';
		$("#formAddNewRow_strEmpCodeAlpha").prop('required', true);
		$("#formAddNewRow_strContractorCodeAlpha").prop('required', true);
		$("#formAddNewRow_strEmpCodeNumber").prop('required', true);
	} else {
		document.getElementById('empCode1').style.display = 'none';	
		document.getElementById('contractCode1').style.display = 'none';
		document.getElementById('empCode2').style.display = 'none';
		$("#formAddNewRow_strEmpCodeAlpha").removeClass("validateRequired");
		$("#formAddNewRow_strContractorCodeAlpha").removeClass("validateRequired");
		$("#formAddNewRow_strEmpCodeNumber").removeClass("validateRequired");
		$("#formAddNewRow_strEmpCodeAlpha").prop('required', false);
		$("#formAddNewRow_strContractorCodeAlpha").prop('required', false);
		$("#formAddNewRow_strEmpCodeNumber").prop('required', false);
	}
}


</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String strOrgID = (String) request.getAttribute("orgId");
	Map<String, String> hmCompanySetting = (Map<String, String>) request.getAttribute("hmCompanySetting");
	if(hmCompanySetting == null)hmCompanySetting = new HashMap<String, String>();
%>
<s:form theme="simple" id="formAddNewRow" action="AddOrganisation" method="POST" cssClass="formcss">
	<s:hidden name="userscreen"></s:hidden>
	<s:hidden name="navigationId"></s:hidden>
	<s:hidden name="toPage"></s:hidden>
	<s:hidden name="wlocationTypeId"></s:hidden>
	<table class="table table_no_border">
		<s:hidden name="orgId"></s:hidden>
		<%if(uF.parseToInt(strOrgID) == 0){ %>
			<tr>
				<td></td>
				<td>
				<input type="checkbox" name="sameAsCompany" id="sameAsCompany" onclick="sameAsCompanyInfo(this);" /> 
				&nbsp&nbspSame as Company Settings
				<input type="hidden" name="comName" id="comName" value="<%=uF.showData(hmCompanySetting.get("COMPANY_NAME"),"") %>"/>
				<input type="hidden" name="comSubTitle" id="comSubTitle" value="<%=uF.showData(hmCompanySetting.get("COMPANY_SUB_TITLE"),"") %>"/>
				<input type="hidden" name="comDescription" id="comDescription" value="<%=uF.showData(hmCompanySetting.get("COMPANY_DESCRIPTION"),"") %>"/>
				<input type="hidden" name="comAddress" id="comAddress" value="<%=uF.showData(hmCompanySetting.get("COMPANY_ADDRESS"),"") %>"/>
				<input type="hidden" name="comCity" id="comCity" value="<%=uF.showData(hmCompanySetting.get("COMPANY_CITY"),"") %>"/>
				<input type="hidden" name="comCountry" id="comCountry" value="<%=uF.showData(hmCompanySetting.get("COMPANY_COUNTRY"),"") %>"/>
				<input type="hidden" name="comState" id="comState" value="<%=uF.showData(hmCompanySetting.get("COMPANY_STATE"),"") %>"/>
				<input type="hidden" name="comPincode" id="comPincode" value="<%=uF.showData(hmCompanySetting.get("COMPANY_PINCODE"),"") %>"/>
				<input type="hidden" name="comContactNo" id="comContactNo" value="<%=uF.showData(hmCompanySetting.get("COMPANY_CONTACT_NO"),"") %>"/>
				<input type="hidden" name="comFaxNo" id="comFaxNo" value="<%=uF.showData(hmCompanySetting.get("COMPANY_FAX_NO"),"") %>"/>
				<input type="hidden" name="comEmail" id="comEmail" value="<%=uF.showData(hmCompanySetting.get("COMPANY_EMAIL"),"") %>"/>
				<input type="hidden" name="comWebSite" id="comWebSite" value="<%=uF.showData(hmCompanySetting.get("COMPANY_WEBSITE"),"") %>"/>
				<input type="hidden" name="comIndustry" id="comIndustry" value="<%=uF.showData(hmCompanySetting.get("COMPANY_INDUSTRY"),"") %>"/>
				<input type="hidden" name="comCurrency" id="comCurrency" value="<%=uF.showData(hmCompanySetting.get("COMPANY_CURRENCY"),"") %>"/>
				</td>
			</tr>
		<%} %> 
		<tr>
			<th class="txtlabel alignRight">Organization Code:<sup>*</sup></th>
			<td>
				<s:textfield name="orgCode" id="idOrgCode" cssClass="validateRequired"/> 
				
			</td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Name of the Organization:<sup>*</sup></th>
			<td>
				<s:textfield name="orgName" id="idOrgName" cssClass="validateRequired"/> 
				
			</td>
		</tr> 
		
		<tr>
			<th class="txtlabel alignRight" style="width:300px">Organization Sub Title:</th>
			<td><s:textfield name="orgSubTitle" id="orgSubTitle"/></td>
		</tr>
	
		<tr>
			<th class="txtlabel alignRight" style="width:300px">Organization Description:</th>
			<td><s:textarea name="orgDescription" id="orgDescription" rows="2" cols="22"></s:textarea></td>
		</tr>

		<tr>
			<td colspan="2"><h4>Contact Information</h4><hr style="border:solid 1px #000"/></td>
		</tr>

		<tr>
			<th class="txtlabel alignRight">Address:<sup>*</sup></th>
			<td>
				<s:textarea name="orgAddress" id="idOrgAddress" cols="22" cssClass="validateRequired"/> 
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight">City:<sup>*</sup></th>
			<td>
				<s:textfield name="orgCity" id="idOrgCity" cssClass="validateRequired"/> 
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight">Select Country:<sup>*</sup></th>
			<td>
				<s:select theme="simple" list="countryList" name="orgCountry" id="orgCountry" headerKey="" headerValue="Select Country" listKey="countryId" listValue="countryName" cssClass="validateRequired"
				onchange="getState(this.value);"/>
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight">Select State:<sup>*</sup></th>
			<td id="statetdid">
				<s:select theme="simple" list="stateList" name="orgState" id="orgState" headerKey="" headerValue="Select State" listKey="stateId" listValue="stateName" cssClass="validateRequired"/>
			</td>
		</tr>

		<tr>
			<th class="txtlabel alignRight">Pincode:<sup>*</sup></th>
			<td>
				<s:textfield name="orgPincode" id="idOrgPincode" cssClass="validateRequired validateNumber"/>
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight">Contact No.:<sup>*</sup></th>
			<td>
				<s:textfield name="orgContact1" id="idOrgContact1" cssClass="validateRequired validateNumber"/> 
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight" style="width:300px">Fax No.:</th>
			<td><s:textfield name="orgFaxNo" id="orgFaxNo"/></td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight">e-mail Address:<sup>*</sup></th>
			<td>
				<s:textfield name="orgEmail" id="idOrgEmail" cssClass="validateEmail"/> 
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight" style="width:300px">Website:</th>
			<td><s:textfield name="orgWebsite" id="orgWebsite"/><span class="hint mt">Please enter website of your organisation.<span class="hint-pointer t55">&nbsp;</span></span></td>
		</tr>
	
		<tr>
			<th class="txtlabel alignRight" style="width:300px">Industry:</th>
			<td><s:textfield name="orgIndustry" id="orgIndustry"/><span class="hint mt">Please enter industry of your organisation.<span class="hint-pointer t55">&nbsp;</span></span></td>
		</tr>
		
		<tr>
			<td colspan="2"><h4>Code &amp; Standards</h4><hr style="border:solid 1px #000"/></td>
		</tr>
		
		<tr>
			<th valign="top" class="txtlabel alignRight">Auto-generate Employee Code:</th>
			<td><s:checkbox name="isAutoGenerate" id="isEmpCode" onclick="showCodeFields()"/><span class="hint">By enabling this, system will generate the employee code and will not allow user to change it.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
		
		<tr id="empCode1">
			<th valign="top" class="txtlabel alignRight">Employee Code Alpha:<sup>*</sup></th>
			<td><s:textfield name="strEmpCodeAlpha" cssClass="validateRequired"/><span class="hint">Employee code will be alpha numeric combination, and will have this as alpha code.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
		
		<tr id="contractCode1">
			<th valign="top" class="txtlabel alignRight">Contractor Code Alpha:<sup>*</sup></th>
			<td><s:textfield name="strContractorCodeAlpha" cssClass="validateRequired"/><span class="hint">Contractor code will be alpha numeric combination, and will have this as alpha code.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
		
		<tr id="empCode2">
			<th valign="top" class="txtlabel alignRight">Employee Code start numeric :<sup>*</sup></th>
			<td><s:textfield name="strEmpCodeNumber" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)"/><span class="hint">Employee code will be alpha numeric combination, and will start from this number.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
	
		<tr>
			<td colspan="2"><h4>Other Information</h4><hr style="border:solid 1px #000"/></td>  
		</tr>
		
		<tr>
			<th class="txtlabel alignRight"><label for="state">Select Currency:<sup>*</sup></label><br/></th>
			<td>
				<s:select name="orgCurrency" id="orgCurrency" cssClass="validateRequired" listKey="currencyId" listValue="currencyName" 
				  headerKey="" headerValue="Select Currency" list="currencyList" key="" required="true" />
			</td>
		</tr>
		
		<tr> 
			<th class="txtlabel alignRight"><label for="state">Additional Note:</label><br/></th>
			<td><s:textarea name="orgAdditionalNote" rows="2" cols="40"/></td>
		</tr>
		
		<tr> 
			<th class="txtlabel alignRight"><label for="state">Offices At:</label><br/></th>
			<td><s:textarea name="officesAt" rows="2" cols="40"/></td>
		</tr>
		
		<tr>
			<td colspan="2" align="center">
				<s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
			</td>
		</tr>

	</table>
	
</s:form>

