<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>

$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
});

function getState(country) {
	var action= 'GetStateDetails.action?type=bank&country_id=' + country;
	$.ajax({
		type : 'GET',
		url:action,
		success:function(data){
			//alert("data2==>"+data);
			document.getElementById('bankStateTdid').innerHTML = data;
			
		}
	});

}
</script>

<s:form theme="simple" id="formAddNewRow" action="AddBank" method="POST"
	cssClass="formcss">
	<s:hidden name="bankId" />

	<table border="0" class="formcss" style="width: 675px">

		<tr>
			<td class="txtlabel alignRight">Bank Code:<sup>*</sup></td>
			<td><s:textfield name="bankCode" id="levelCode"
					cssClass="validateRequired" /> <span class="hint">Bank Code<span
					class="hint-pointer">&nbsp;</span>
			</span></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank Name:<sup>*</sup></td>
			<td><s:textfield name="bankName" id="levelName"
					cssClass="validateRequired" /> <span class="hint">Bank Name<span
					class="hint-pointer">&nbsp;</span>
			</span></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank Description:</td>
			<td><s:textfield name="bankDesc" id="levelDesc" /> <span
				class="hint">Bank Description<span class="hint-pointer">&nbsp;</span>
			</span></td>
		</tr>


		<tr>
			<td class="txtlabel alignRight">Bank Address:<sup>*</sup></td>
			<td><s:textfield name="bankAddress" id="levelDesc"
					cssClass="validateRequired" /> <span class="hint">Bank Address<span
					class="hint-pointer">&nbsp;</span>
			</span></td>
		</tr>


		<tr>
			<td class="txtlabel alignRight">Bank City:<sup>*</sup></td>
			<td><s:textfield name="bankCity" cssClass="validateRequired" /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank Pincode:<sup>*</sup></td>
			<td><s:textfield name="bankPincode" cssClass="validateRequired" /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank Country:<sup>*</sup></td>
			<td><s:select name="bankCountry" list="countryList" cssClass="validateRequired" listKey="countryId" listValue="countryName"
					headerKey="0" headerValue="Select Country" onchange="getState(this.value);"></s:select></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank State:<sup>*</sup></td>
			<td id="bankStateTdid" ><s:select name="bankState" list="stateList"
					cssClass="validateRequired" id="bankState" listKey="stateId"
					listValue="stateName" headerKey="" headerValue="Select State"></s:select>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank Branch:</td>
			<td><s:textfield name="bankBranch" /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank Email:</td>
			<td><s:textfield name="bankEmail" /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank Fax:</td>
			<td><s:textfield name="bankFax" /></td>
		</tr>


		<tr>
			<td class="txtlabel alignRight">Bank Contact No:</td>
			<td><s:textfield name="bankContactNo" /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank IFSC Code:</td>
			<td><s:textfield name="bankIFSCCode" /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Bank Acc/No:</td>
			<td><s:textfield name="bankAccNo" /></td>
		</tr>

		<tr>
			<td></td>
			<td><s:submit cssClass="input_button" value="Submit" id="btnAddNewRowOk" /></td>
		</tr>

	</table>

</s:form>

