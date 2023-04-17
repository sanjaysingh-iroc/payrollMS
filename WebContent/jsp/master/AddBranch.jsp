<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
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
	//getContent('billingStateTdid', action);
}
</script>
 
	<s:form theme="simple" id="formAddNewRow" action="AddBranch" method="POST" cssClass="formcss">
	<input type="hidden" name="bankId" value="<%=request.getParameter("param") %>" />
	<input type="hidden" name="branchId" value="<%=request.getParameter("ID") %>" />
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
		<table border="0" class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight">Branch Name:<sup>*</sup></th>
				<td>
					<s:textfield name="bankBranch"  cssClass="validateRequired"/> 
				</td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">Branch Code:<sup>*</sup></th>
				<td>
					<s:textfield name="branchCode" cssClass="validateRequired" /> 
				</td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">Address:<sup>*</sup></th>
				<td>
					<s:textfield name="bankAddress" id="levelDesc"  cssClass="validateRequired"/> 
					<span class="hint">Bank Address<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
	
			<tr>
				<th class="txtlabel alignRight">City:</th>
				<td>
					<s:textfield name="bankCity"/> 
				</td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">Country:<sup>*</sup></th>
				<td>
					<s:select name="bankCountry" list="countryList" cssClass="validateRequired" listKey="countryId" 
					listValue="countryName" headerKey="0" headerValue="Select Country" onchange="getState(this.value);"></s:select>
				</td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">State:<sup>*</sup></th>
				<td id="bankStateTdid">
					<s:select name="bankState" id="bankState" list="stateList" cssClass="validateRequired"
						listKey="stateId" listValue="stateName" headerKey="0" headerValue="Select State"
					></s:select> 
				</td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">Pincode:<sup>*</sup></th>
				<td><s:textfield name="bankPincode" cssClass="validateRequired"/></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">e-mail Address:</th>
				<td><s:textfield name="bankEmail"/></td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">Contact No:</th>
				<td><s:textfield name="bankContactNo" /></td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">Fax No.:</th>
				<td>
					<s:textfield name="bankFax"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><h4>Other Codes</h4><hr style="border:solid 1px #000"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">&nbsp;</td>
				<td style="font-size: 12px;">select the check box to be part of bank details in invoices.</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">A/c No.:<sup>*</sup></th>
				<td>
					<s:textfield name="bankAccNo" cssClass="validateRequired"/> 
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Branch IFSC Code:</th>
				<td>
					<s:textfield name="bankIFSCCode" /> <s:checkbox name="isIFSC" id="isIFSC"/>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">SWIFT Code:</th>
				<td> <s:textfield name="swiftCode" /> <s:checkbox name="isSWIFT" id="isSWIFT"/></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Branch Clearing Code:</th>
				<td> <s:textfield name="bankClearingCode" /> <s:checkbox name="isClearingCode" id="isClearingCode"/></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight" valign="top">Other Information:</th>
				<td> <s:textarea name="otherInformation" cols="55" rows="10"></s:textarea> </td>
			</tr>
			
			<tr>
				<td></td>
				<td> <s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
				</td>
			</tr>
	
		</table>
	
	</s:form>

